package org.kalypso.ogc.sensor.diagview.impl;

import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.ObservationUtilities;
import org.kalypso.ogc.sensor.diagview.IAxisMapping;
import org.kalypso.ogc.sensor.diagview.IDiagramAxis;
import org.kalypso.ogc.sensor.status.KalypsoStatusUtils;
import org.kalypso.util.runtime.IVariableArguments;

/**
 * A default <code>IDiagramTemplate</code> that works directly with an <code>IObservation</code>.
 * 
 * @author schlienger
 */
public class ObservationDiagramTemplate extends DefaultDiagramTemplate
{
  public ObservationDiagramTemplate()
  {
    super( "Diagram", "", true );
  }

  /**
   * Sets the observation used by this template. Removes all curves before adding the
   * given observation.
   * 
   * @param obs
   * @param args
   */
  public void setObservation( final IObservation obs, final IVariableArguments args  )
  {
    removeAllAxes();
    removeAllThemes();
    setTitle( obs.getName() );
    
    addObservation( obs, args );
  }
 
  /**
   * Adds an observation as a theme to this template
   * 
   * @param obs
   * @param args
   */
  public void addObservation( final IObservation obs, final IVariableArguments args )
  {
    final IAxis[] valueAxis = ObservationUtilities.findAxisByClass( obs.getAxisList(), Number.class );
    final IAxis[] keyAxes = ObservationUtilities.findAxisByKey( obs.getAxisList() );

    if( keyAxes.length == 0 )
      return;
    
    final IAxis dateAxis = keyAxes[0];
    
    final DefaultDiagramTemplateTheme theme = new DefaultDiagramTemplateTheme( obs );
    theme.setArguments( args );
    
    for( int i = 0; i < valueAxis.length; i++ )
    {
      if( !KalypsoStatusUtils.isStatusAxis( valueAxis[i] ) )
      {
        final IAxisMapping[] mappings = new IAxisMapping[2];
        
        // look for a date diagram axis
        IDiagramAxis daDate = getDiagramAxis( dateAxis.getType() );
        if( daDate == null )
        {
          daDate = DiagramAxis.createAxisFor( dateAxis );
          addAxis( daDate );
        }
        mappings[0] = new AxisMapping( dateAxis, daDate );

        // look for a value diagram axis
        IDiagramAxis daValue = getDiagramAxis( valueAxis[i].getType() );
        if( daValue == null )
        {
          daValue = DiagramAxis.createAxisFor( valueAxis[i] );
          addAxis( daValue );
        }
        mappings[1] = new AxisMapping( valueAxis[i], daValue );

        final DiagramCurve curve = new DiagramCurve( valueAxis[i].getName(), theme, AxisMapping.saveAsProperties( mappings ), this );
        theme.addCurve( curve );
      }
    }
    
    addTheme( theme );
  }
}