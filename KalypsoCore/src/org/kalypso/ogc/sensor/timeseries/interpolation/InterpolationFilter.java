package org.kalypso.ogc.sensor.timeseries.interpolation;

import java.util.Calendar;
import java.util.Date;

import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.ITuppleModel;
import org.kalypso.ogc.sensor.ObservationUtilities;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.filter.filters.AbstractObservationFilter;
import org.kalypso.ogc.sensor.impl.SimpleTuppleModel;
import org.kalypso.ogc.sensor.status.KalypsoStatusUtils;
import org.kalypso.util.math.LinearEquation;
import org.kalypso.util.math.LinearEquation.SameXValuesException;
import org.kalypso.util.runtime.IVariableArguments;
import org.kalypso.util.runtime.args.DateRangeArgument;

/**
 * InterpolationFilter. This is a simple yet tricky interpolation filter. It
 * steps through the time and eventually interpolates the values at t, using the
 * values at t-1 and t+1.
 * <p>
 * This filter can also deal with Kalypso Status Axes. In that case it does not
 * perform a strict interpolation, but uses the bitwise OR-Operator to combine
 * the stati. When no status is available, it uses the default value for the
 * status provided in the constructor.
 * 
 * @author schlienger
 */
public class InterpolationFilter extends AbstractObservationFilter
{
  private final int m_calField;

  private final int m_amount;

  private final boolean m_fill;

  private final Double m_defValue;

  private final Integer m_defaultStatus;

  /**
   * Constructor.
   * 
   * @param calendarField
   *          which field of the date will be used for steping through the
   *          timeserie
   * @param amount
   *          amount of time for the step
   * @param forceFill
   *          when true, fills the model with defaultValue when no base value
   * @param defaultValue
   *          default value to use when filling absent values
   * @param defaultStatus
   *          value of the default status when base status is absent or when
   *          status-interpolation cannot be proceeded
   */
  public InterpolationFilter( final int calendarField, final int amount,
      final boolean forceFill, final double defaultValue,
      final int defaultStatus )
  {
    m_calField = calendarField;
    m_amount = amount;
    m_fill = forceFill;
    m_defaultStatus = new Integer( defaultStatus );
    m_defValue = new Double( defaultValue );
  }

  /**
   * @see org.kalypso.ogc.sensor.filter.filters.AbstractObservationFilter#getValues(org.kalypso.util.runtime.IVariableArguments)
   */
  public ITuppleModel getValues( final IVariableArguments args )
      throws SensorException
  {
    final ITuppleModel values = super.getValues( args );

    DateRangeArgument dr = null;
    if( args instanceof DateRangeArgument )
      dr = (DateRangeArgument) args;

    final IAxis dateAxis = ObservationUtilities.findAxisByClass( values
        .getAxisList(), Date.class )[0];
    final IAxis[] valueAxes = ObservationUtilities.findAxisByClass( values
        .getAxisList(), Number.class, false );

    final SimpleTuppleModel intModel = new SimpleTuppleModel( values
        .getAxisList() );

    final Calendar cal = Calendar.getInstance();

    if( values.getCount() == 0 )
    {
      // no values, and fill is not set, so return
      if( !m_fill )
        return values;

      // no values but fill is set, generate them
      if( dr != null )
      {
        cal.setTime( dr.getFrom() );

        while( cal.getTime().compareTo( dr.getTo() ) <= 0 )
          fillWithDefault( dateAxis, valueAxes, intModel, cal );

        return intModel;
      }
    }

    final Date begin = (Date) values.getElement( 0, dateAxis );

    Date d1 = null;
    Date d2 = null;
    final double[] v1 = new double[valueAxes.length + 1];
    final double[] v2 = new double[valueAxes.length + 1];

    int startIx = 0;

    // do we need to fill before the begining of the base model?
    if( dr != null && m_fill )
    {
      cal.setTime( dr.getFrom() );
      d1 = cal.getTime();

      for( int i = 0; i < valueAxes.length; i++ )
      {
        final Number nb = (Number) values.getElement( startIx, valueAxes[i] );
        v1[valueAxes[i].getPosition()] = nb.doubleValue();
      }

      while( cal.getTime().compareTo( begin ) < 0 )
      {
        d1 = cal.getTime();
        fillWithDefault( dateAxis, valueAxes, intModel, cal );
      }
    }
    else
    {
      cal.setTime( begin );

      final Object[] tupple = new Object[valueAxes.length + 1];
      tupple[dateAxis.getPosition()] = cal.getTime();

      for( int i = 0; i < valueAxes.length; i++ )
      {
        final Number nb = (Number) values.getElement( startIx, valueAxes[i] );

        tupple[valueAxes[i].getPosition()] = nb;
        v1[valueAxes[i].getPosition()] = nb.doubleValue();
      }

      intModel.addTupple( tupple );

      cal.add( m_calField, m_amount );

      startIx++;

      d1 = cal.getTime();
    }

    final LinearEquation eq = new LinearEquation();

    for( int ix = startIx; ix < values.getCount(); ix++ )
    {
      d2 = (Date) values.getElement( ix, dateAxis );

      for( int ia = 0; ia < valueAxes.length; ia++ )
      {
        final Number nb = (Number) values.getElement( ix, valueAxes[ia] );
        v2[valueAxes[ia].getPosition()] = nb.doubleValue();
      }

      while( cal.getTime().compareTo( d2 ) <= 0 )
      {
        long ms = cal.getTimeInMillis();

        Object[] tupple = new Object[valueAxes.length + 1];
        tupple[dateAxis.getPosition()] = cal.getTime();

        for( int ia = 0; ia < valueAxes.length; ia++ )
        {
          if( KalypsoStatusUtils.isStatusAxis( valueAxes[ia] ) )
          {
            // this is the status axis
            // no interpolation but a bitwise OR
            tupple[valueAxes[ia].getPosition()] = new Integer(
                (int) v1[valueAxes[ia].getPosition()]
                    | (int) v2[valueAxes[ia].getPosition()] );
          }
          else
          {
            // normal case: perform the interpolation
            try
            {
              eq.setPoints( d1.getTime(), v1[valueAxes[ia].getPosition()], d2
                  .getTime(), v2[valueAxes[ia].getPosition()] );
              tupple[valueAxes[ia].getPosition()] = new Double( eq
                  .computeY( ms ) );
            }
            catch( SameXValuesException e )
            {
              tupple[valueAxes[ia].getPosition()] = new Double(
                  v1[valueAxes[ia].getPosition()] );
            }
          }
        }

        intModel.addTupple( tupple );

        cal.add( m_calField, m_amount );
      }

      d1 = d2;
      System.arraycopy( v2, 0, v1, 0, v2.length );
    }

    // do we need to fill after the end of the base model?
    if( dr != null && m_fill )
    {
      while( cal.getTime().compareTo( dr.getTo() ) < 0 )
        fillWithDefault( dateAxis, valueAxes, intModel, cal );
    }

    return intModel;
  }

  /**
   * Fills the model with default values
   * 
   * @param dateAxis
   * @param valueAxes
   * @param intModel
   * @param cal
   */
  private void fillWithDefault( final IAxis dateAxis, final IAxis[] valueAxes,
      final SimpleTuppleModel intModel, final Calendar cal )
  {
    final Object[] tupple = new Object[valueAxes.length + 1];
    tupple[dateAxis.getPosition()] = cal.getTime();

    for( int i = 0; i < valueAxes.length; i++ )
    {
      if( KalypsoStatusUtils.isStatusAxis( valueAxes[i] ) )
      {
        tupple[valueAxes[i].getPosition()] = m_defaultStatus;
      }
      else
        tupple[valueAxes[i].getPosition()] = m_defValue;
    }

    intModel.addTupple( tupple );

    cal.add( m_calField, m_amount );
  }
}