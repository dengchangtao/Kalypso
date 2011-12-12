/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 * 
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  Denickestra�e 22
 *  21073 Hamburg, Germany
 *  http://www.tuhh.de/wb
 * 
 *  and
 *  
 *  Bjoernsen Consulting Engineers (BCE)
 *  Maria Trost 3
 *  56070 Koblenz, Germany
 *  http://www.bjoernsen.de
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *  Contact:
 * 
 *  E-Mail:
 *  belger@bjoernsen.de
 *  schlienger@bjoernsen.de
 *  v.doemming@tuhh.de
 *   
 *  ---------------------------------------------------------------------------*/
package org.kalypso.model.rcm.internal.binding;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypso.model.rcm.RcmConstants;
import org.kalypso.model.rcm.binding.AbstractRainfallGenerator;
import org.kalypso.model.rcm.binding.ICatchment;
import org.kalypso.model.rcm.binding.ICatchmentGenerator;
import org.kalypso.model.rcm.binding.IFactorizedTimeseries;
import org.kalypso.model.rcm.internal.KalypsoModelRcmActivator;
import org.kalypso.model.rcm.util.RainfallGeneratorUtilities;
import org.kalypso.ogc.sensor.DateRange;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.util.ZmlLink;
import org.kalypso.zml.obslink.TimeseriesLinkType;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureList;

/**
 * The catchment generator.
 * 
 * @author Holger Albert
 */
public class CatchmentGenerator extends AbstractRainfallGenerator implements ICatchmentGenerator
{
  /**
   * The qname of the area name property.
   */
  public static final QName QNAME_AREA_NAME_PROPERTY = new QName( RcmConstants.NS_CM, "areaNameProperty" );

  /**
   * The qname of the area property.
   */
  public static final QName QNAME_AREA_PROPERTY = new QName( RcmConstants.NS_CM, "areaProperty" );

  /**
   * The qname of the catchment member.
   */
  public static final QName QNAME_CATCHMENT_MEMBER = new QName( RcmConstants.NS_CM, "catchmentMember" );

  /**
   * The qname of the catchment.
   */
  public static final QName QNAME_CATCHMENT = new QName( RcmConstants.NS_CM, "Catchment" );

  /**
   * The constructor.
   * 
   * @param parent
   * @param parentRelation
   * @param ft
   * @param id
   * @param propValues
   */
  public CatchmentGenerator( final Object parent, final IRelationType parentRelation, final IFeatureType ft, final String id, final Object[] propValues )
  {
    super( parent, parentRelation, ft, id, propValues );
  }

  /**
   * @see org.kalypso.model.rcm.binding.IRainfallGenerator#createRainfall(org.kalypsodeegree.model.feature.Feature[],
   *      org.kalypso.ogc.sensor.DateRange, org.eclipse.core.runtime.ILog, org.eclipse.core.runtime.IProgressMonitor)
   * @param catchmentFeatures
   *          The catchment features will be taken from the generator itself, so they are not needed here. Leave them
   *          null.
   */
  @Override
  public IObservation[] createRainfall( Feature[] catchmentFeatures, DateRange range, ILog log, IProgressMonitor monitor ) throws CoreException
  {
    /* Monitor. */
    if( monitor == null )
      monitor = new NullProgressMonitor();

    /* Get the catchments. */
    ICatchment[] catchments = getCatchments();

    /* HINT: Keep in mind, that the results must match the order of the catchments array. */
    IObservation[] results = new IObservation[catchments.length];

    try
    {
      /* Monitor. */
      monitor.beginTask( String.format( "Generiere Zeitreihen f�r %d Gebiete...", catchments.length ), catchments.length * 200 );
      monitor.subTask( "Generiere Zeitreihen..." );

      /* Generate one timeseries for each catchment. */
      for( int i = 0; i < catchments.length; i++ )
      {
        /* Generate the message 1. */
        String message1 = String.format( "Sammle gewichteten Zeitreihen zur Erzeugung von Zeitreihe %d...", i + 1 );

        /* Monitor. */
        monitor.subTask( message1 );

        /* Log. */
        if( log != null )
          log.log( new Status( IStatus.INFO, KalypsoModelRcmActivator.PLUGIN_ID, message1 ) );

        /* Get the catchment. */
        ICatchment catchment = catchments[i];

        /* Memory for the factors and the observations of the catchments. */
        List<Double> factors = new ArrayList<Double>();
        List<IObservation> observations = new ArrayList<IObservation>();

        /* Get the factorized timeseries. */
        for( IFactorizedTimeseries factorizedTimeseries : catchment.getFactorizedTimeseries() )
        {
          /* Get the factor. */
          Double factor = factorizedTimeseries.getFactor();

          /* Get the timeseries link. */
          ZmlLink timeseriesLink = factorizedTimeseries.getTimeseriesLink();

          /* Load the observation. */
          IObservation[] readObservations = RainfallGeneratorUtilities.readObservations( new TimeseriesLinkType[] { timeseriesLink.getTimeseriesLink() }, range, null, factorizedTimeseries.getWorkspace().getContext() );
          IObservation observation = readObservations[0];

          /* If the factor is valid and > 0.0, add the factor and its observation. */
          if( factor != null && factor.doubleValue() > 0.0 && !Double.isNaN( factor.doubleValue() ) && !Double.isInfinite( factor.doubleValue() ) )
          {
            factors.add( factor );
            observations.add( observation );
          }
        }

        /* Generate the message 2. */
        String message2 = String.format( "Erzeuge Zeitreihe %d mit %d gewichteten Zeitreihen...", i + 1, factors.size() );

        /* Monitor. */
        monitor.worked( 100 );
        monitor.subTask( message2 );

        /* Log. */
        if( log != null )
          log.log( new Status( IStatus.INFO, KalypsoModelRcmActivator.PLUGIN_ID, message2 ) );

        /* Set the resulting observation for this catchment. */
        results[i] = RainfallGeneratorUtilities.combineObses( observations.toArray( new IObservation[] {} ), convertDoubles( factors.toArray( new Double[] {} ) ), "catchmentGenerator://thiessen" );

        /* Monitor. */
        monitor.worked( 100 );
      }

      return results;
    }
    catch( Exception ex )
    {
      /* Create the error status. */
      IStatus status = new Status( IStatus.ERROR, KalypsoModelRcmActivator.PLUGIN_ID, ex.getLocalizedMessage(), ex );

      /* If there is a log, log to it. */
      if( log != null )
        log.log( status );

      throw new CoreException( status );
    }
    finally
    {
      /* Monitor. */
      monitor.done();
    }
  }

  /**
   * @see org.kalypso.model.rcm.binding.ICatchmentGenerator#getAreaNameProperty()
   */
  @Override
  public String getAreaNameProperty( )
  {
    return getProperty( QNAME_AREA_NAME_PROPERTY, String.class );
  }

  /**
   * @see org.kalypso.model.rcm.binding.ICatchmentGenerator#getAreaProperty()
   */
  @Override
  public String getAreaProperty( )
  {
    return getProperty( QNAME_AREA_PROPERTY, String.class );
  }

  /**
   * @see org.kalypso.model.rcm.binding.ICatchmentGenerator#getCatchments()
   */
  @Override
  public ICatchment[] getCatchments( )
  {
    /* Memory for the results. */
    final List<Catchment> results = new ArrayList<Catchment>();

    /* Get all catchments. */
    final FeatureList catchments = (FeatureList) getProperty( QNAME_CATCHMENT_MEMBER );
    for( int i = 0; i < catchments.size(); i++ )
      results.add( (Catchment) catchments.get( i ) );

    return results.toArray( new Catchment[] {} );
  }

  /**
   * This function converts an array of Doubles to an array of doubles.
   * 
   * @param factors
   *          The array of Doubles.
   * @return A array of doubles.
   */
  private double[] convertDoubles( Double[] factors )
  {
    double[] weights = new double[factors.length];
    for( int j = 0; j < factors.length; j++ )
    {
      Double factor = factors[j];
      weights[j] = factor.doubleValue();
    }

    return weights;
  }
}