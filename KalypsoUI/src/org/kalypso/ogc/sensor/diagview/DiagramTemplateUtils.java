/*--------------- Kalypso-Header --------------------------------------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 Technical University Hamburg-Harburg (TUHH)
 Institute of River and coastal engineering
 Denickestr. 22
 21073 Hamburg, Germany
 http://www.tuhh.de/wb

 and
 
 Bjoernsen Consulting Engineers (BCE)
 Maria Trost 3
 56070 Koblenz, Germany
 http://www.bjoernsen.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 E-Mail:
 belger@bjoernsen.de
 schlienger@bjoernsen.de
 v.doemming@tuhh.de
  
---------------------------------------------------------------------------------------------------*/
package org.kalypso.ogc.sensor.diagview;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.IOUtils;
import org.kalypso.java.util.StringUtilities;
import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.diagview.impl.AxisMapping;
import org.kalypso.ogc.sensor.diagview.impl.DiagViewCurve;
import org.kalypso.ogc.sensor.diagview.impl.DiagViewTemplate;
import org.kalypso.ogc.sensor.diagview.impl.DiagViewTheme;
import org.kalypso.ogc.sensor.diagview.impl.DiagramAxis;
import org.kalypso.ogc.sensor.timeseries.TimeserieConstants;
import org.kalypso.template.obsdiagview.ObjectFactory;
import org.kalypso.template.obsdiagview.ObsdiagviewType;
import org.kalypso.template.obsdiagview.TypeAxis;
import org.kalypso.template.obsdiagview.TypeAxisMapping;
import org.kalypso.template.obsdiagview.TypeCurve;
import org.kalypso.template.obsdiagview.TypeObservation;
import org.kalypso.template.obsdiagview.ObsdiagviewType.LegendType;

/**
 * Observation template handling made easy.
 * 
 * @author schlienger
 */
public class DiagramTemplateUtils
{
  public final static String ODT_FILE_EXTENSION = "odt";

  private final static ObjectFactory ODT_OF = new ObjectFactory();

  private DiagramTemplateUtils( )
  {
    // not to be instanciated
  }

  /**
   * Saves the given template (binding). Closes the stream.
   * 
   * @param tpl
   * @param out
   * @throws JAXBException
   */
  public static void saveDiagramTemplateXML( final ObsdiagviewType tpl,
      final OutputStream out ) throws JAXBException
  {
    try
    {
      final Marshaller m = ODT_OF.createMarshaller();
      m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
      m.marshal( tpl, out );
    }
    finally
    {
      IOUtils.closeQuietly( out );
    }
  }

  /**
   * Saves the given template (binding). Closes the writer.
   * 
   * @param tpl
   * @param writer
   * @throws JAXBException
   */
  public static void saveDiagramTemplateXML( final ObsdiagviewType tpl,
      final Writer writer ) throws JAXBException
  {
    try
    {
      final Marshaller m = ODT_OF.createMarshaller();
      m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
      m.marshal( tpl, writer );
    }
    finally
    {
      IOUtils.closeQuietly( writer );
    }
  }

  /**
   * Loads a binding template. Closes the stream.
   * 
   * @param ins
   * @return diagram template object parsed from the file
   * @throws JAXBException
   */
  public static ObsdiagviewType loadDiagramTemplateXML( final InputStream ins )
      throws JAXBException
  {
    try
    {
      final ObsdiagviewType baseTemplate = (ObsdiagviewType) ODT_OF
          .createUnmarshaller().unmarshal( ins );

      return baseTemplate;
    }
    finally
    {
      IOUtils.closeQuietly( ins );
    }
  }

  /**
   * Builds the template binding type using the given template.
   * 
   * @param template
   * @return binding type, ready for marshalling
   * @throws JAXBException
   */
  public static ObsdiagviewType buildDiagramTemplateXML(
      final DiagViewTemplate template ) throws JAXBException
  {
    final ObsdiagviewType bdgTemplate = ODT_OF.createObsdiagview();

    final LegendType bdgLegend = ODT_OF.createObsdiagviewTypeLegendType();
    bdgLegend.setTitle( template.getLegendName() );
    bdgLegend.setVisible( template.isShowLegend() );

    bdgTemplate.setLegend( bdgLegend );
    bdgTemplate.setTitle( template.getTitle() );

    final List bdgAxes = bdgTemplate.getAxis();
    final Iterator itAxes = template.getDiagramAxes().iterator();
    while( itAxes.hasNext() )
    {
      final DiagramAxis axis = (DiagramAxis) itAxes.next();

      final TypeAxis bdgAxis = ODT_OF.createTypeAxis();
      bdgAxis.setDatatype( axis.getDataType() );
      bdgAxis.setDirection( axis.getDirection() );
      bdgAxis.setId( axis.getIdentifier() );
      bdgAxis.setInverted( axis.isInverted() );
      bdgAxis.setLabel( axis.getLabel() );
      bdgAxis.setPosition( axis.getPosition() );
      bdgAxis.setUnit( axis.getUnit() );

      bdgAxes.add( bdgAxis );
    }

    int ixCurve = 1;

    final List bdgThemes = bdgTemplate.getObservation();
    final Iterator itThemes = template.getThemes().iterator();
    while( itThemes.hasNext() )
    {
      final DiagViewTheme theme = (DiagViewTheme) itThemes
          .next();

      final IObservation obs = theme.getObservation();

      final TypeObservation bdgTheme = ODT_OF.createTypeObservation();
      bdgTheme.setLinktype( "zml" );
      bdgTheme.setHref( obs.getHref() );

      final List bdgCurves = bdgTheme.getCurve();

      final Iterator itCurves = theme.getCurves().iterator();
      while( itCurves.hasNext() )
      {
        final DiagViewCurve curve = (DiagViewCurve) itCurves.next();

        final TypeCurve bdgCurve = ODT_OF.createTypeCurve();
        bdgCurve.setId( "C" + ixCurve++ );
        bdgCurve.setName( curve.getName() );
        bdgCurve.setColor( StringUtilities.colorToString( curve.getColor() ) );

        final List bdgMappings = bdgCurve.getMapping();

        final AxisMapping[] mappings = curve.getMappings();
        for( int i = 0; i < mappings.length; i++ )
        {
          final TypeAxisMapping bdgMapping = ODT_OF.createTypeAxisMapping();
          bdgMapping.setDiagramAxis( mappings[i].getDiagramAxis()
              .getIdentifier() );
          bdgMapping.setObservationAxis( mappings[i].getObservationAxis()
              .getName() );

          bdgMappings.add( bdgMapping );
        }

        bdgCurves.add( bdgCurve );
      }

      bdgThemes.add( bdgTheme );
    }

    return bdgTemplate;
  }

  /**
   * Creates a diagram axis according to the given IObservation axis
   * 
   * @param axis
   * @return diagram axis
   */
  public static DiagramAxis createAxisFor( final IAxis axis )
  {
    return createAxisFor( axis.getType(), axis.getName(), axis.getUnit() );
  }
  
  /**
   * Creates a diagram axis according to the given IObservation axis
   * 
   * @param axisType
   * @param label
   * @param unit
   * @return diagram axis
   */
  public static DiagramAxis createAxisFor( final String axisType,
      final String label, final String unit )
  {
    if( axisType.equals( TimeserieConstants.TYPE_DATE ) )
      return new DiagramAxis( axisType, "date", label, unit,
          DiagramAxis.DIRECTION_HORIZONTAL, DiagramAxis.POSITION_BOTTOM,
          false );

    if( axisType.equals( TimeserieConstants.TYPE_WATERLEVEL ) )
      return new DiagramAxis( axisType, "double", label, unit,
          DiagramAxis.DIRECTION_VERTICAL, DiagramAxis.POSITION_LEFT, false );

    if( axisType.equals( TimeserieConstants.TYPE_RUNOFF ) )
      return new DiagramAxis( axisType, "double", label, unit,
          DiagramAxis.DIRECTION_VERTICAL, DiagramAxis.POSITION_LEFT, false );

    if( axisType.equals( TimeserieConstants.TYPE_RAINFALL ) )
      return new DiagramAxis( axisType, "double", label, unit,
          DiagramAxis.DIRECTION_VERTICAL, DiagramAxis.POSITION_RIGHT, true, null, new Double(0.8) );

    if( axisType.equals( TimeserieConstants.TYPE_TEMPERATURE ) )
      return new DiagramAxis( axisType, "double", label, unit,
          DiagramAxis.DIRECTION_VERTICAL, DiagramAxis.POSITION_RIGHT, false );

    // default axis
    return new DiagramAxis( axisType, "double", label, unit,
        DiagramAxis.DIRECTION_VERTICAL, DiagramAxis.POSITION_LEFT, false );
  }
}