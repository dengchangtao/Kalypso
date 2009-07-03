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
package org.kalypso.convert.namodel;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.Marshaller;

import org.kalypso.java.net.UrlUtilities;
import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.ITuppleModel;
import org.kalypso.ogc.sensor.ObservationUtilities;
import org.kalypso.ogc.sensor.timeseries.TimeserieConstants;
import org.kalypso.ogc.sensor.timeseries.TimeserieUtils;
import org.kalypso.ogc.sensor.zml.ZmlFactory;
import org.kalypso.zml.ObservationType;
import org.kalypso.zml.obslink.ObjectFactory;
import org.kalypso.zml.obslink.TimeseriesLink;

/**
 * @author doemming
 *  
 */
public class NAZMLGenerator
{
  private static boolean DEBUG = false;

  // debug = true skips converting ascii timeseries to zml timeseries while
  // importing ascii

  final static SimpleDateFormat m_grapDateFormat = new SimpleDateFormat( "dd MM yyyy HH mm ss" );

  final static NAZMLGenerator m_singelton = new NAZMLGenerator();

  public NAZMLGenerator()
  {
  // do not instanciate
  }

  /**
   * generate copy of custom timeseriesfile to zml-format, and returns
   * timeserieslink
   * 
   * @param copySource
   *          url to the data to copy
   * @param targetBaseDir
   *          basedir for targetfile
   * @param targetRelativePath
   *          relative path from basedir to store target zml file
   */
  public static TimeseriesLink copyToTimeseriesLink( URL copySource, String axis1Type,
      String axis2Type, File targetBaseDir, String targetRelativePath, boolean relative,
      boolean simulateCopy ) throws Exception
  {

    File targetZmlFile = new File( targetBaseDir, targetRelativePath );
    File dir = targetZmlFile.getParentFile();
    if( !dir.exists() )
      dir.mkdirs();
    if( !simulateCopy && !DEBUG )
      try
      {
        convert( copySource, axis1Type, axis2Type, targetZmlFile );
      }
      catch( Exception e )
      {
        e.printStackTrace();
        System.out.println( "could not create ZML, but operation will continue..." );
      }
    if( relative )
      return generateobsLink( targetRelativePath );
    UrlUtilities urlUtilities = new UrlUtilities();
    final URL targetURL = urlUtilities.resolveURL( targetBaseDir.toURL(), targetRelativePath );
    return generateobsLink( targetURL.toExternalForm() );
  }

  /**
   * @param location
   *          location of zml data
   */
  public static TimeseriesLink generateobsLink( String location ) throws Exception
  {

    final ObjectFactory factory = new ObjectFactory();

    final TimeseriesLink link = factory.createTimeseriesLink();
    link.setLinktype( "zml" );
    link.setType( "simple" );
    link.setHref( location );
    return link;
  }

  private static void convert( URL sourceURL, String axis1Type, String axis2Type, File targetZmlFile )
      throws Exception
  {
    StringBuffer buffer = new StringBuffer();
    generateTmpZml( buffer, axis1Type, axis2Type, sourceURL );

    File zmlTmpFile = File.createTempFile( "tmp", ".zml" );
    zmlTmpFile.deleteOnExit();
    Writer tmpWriter = new FileWriter( zmlTmpFile );
    tmpWriter.write( buffer.toString() );
    tmpWriter.close();

    IObservation observation = ZmlFactory.parseXML( zmlTmpFile.toURL(), "ID" );
    ObservationType type = ZmlFactory.createXML( observation, null );
    Marshaller marshaller = ZmlFactory.getMarshaller();
    marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
    Writer writer = new FileWriter( targetZmlFile );
    marshaller.marshal( type, writer );
    writer.close();

  }

  private static void generateTmpZml( StringBuffer buffer, String axis1Type, String axis2Type,
      URL sourceURL ) throws Exception
  {
    final String location = sourceURL.toExternalForm();

    buffer.append( "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" );
    buffer.append( "    <observation xmlns=\"zml.kalypso.org\" " );
    buffer
        .append( " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"zml.kalypso.org./observation.xsd\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">" );
    buffer.append( "  <name>Eine Test-Observation</name>" );
    buffer.append( "      <!-- die Metadaten list ist erweiterbar -->" );
    buffer.append( "      <metadataList>" );
    //    buffer.append( " <metadata name=\"Pegelnullpunkt\" value=\"10\"/>" );
    //    buffer.append( " <metadata name=\"Rechtswert\" value=\"445566\"/>" );
    //    buffer.append( " <metadata name=\"Hochwert\" value=\"887766\"/>" );
    //    buffer.append( " <metadata name=\"Alarmstufe 1\" value=\"4.3\"/>" );
    //    buffer.append( " <metadata name=\"Alarmstufe 2\" value=\"5.6\"/>" );
    buffer.append( "      </metadataList>" );

    //axis1
    buffer.append( "<axis name=\"" + TimeserieUtils.getName( axis1Type ) + "\" "
    //        +"key=\"true\""
        + " type=\"" + axis1Type + "\" unit=\"" + TimeserieUtils.getUnit( axis1Type ) + "\"" );

    //    buffer.append( " datatype=\"TYPE=xs:date#FORMAT=dd MM yyyy HH mm ss\"");

    buffer.append( " >" );
    buffer.append( "<valueLink separator=\",\" column=\"1\" line=\"4\" " );
    buffer.append( " xlink:href=\"" + location + "\"/>" );
    buffer.append( "</axis>" );
    // axis2
    buffer.append( "<axis name=\"" + TimeserieUtils.getName( axis2Type ) + "\" "
    //        +"key=\"true\""
        + " type=\"" + axis2Type + "\" unit=\"" + TimeserieUtils.getUnit( axis2Type ) + "\"" );

    buffer.append( "<valueLink separator=\",\" column=\"2\" line=\"4\" " );
    buffer.append( " xlink:href=\"" + location + "\"/>" );
    buffer.append( "</axis>" );
    buffer.append( "</observation>" );
  }

  public static void createFile( FileWriter writer, String axisValueType, IObservation observation )
      throws Exception
  {
    createGRAPFile( writer, axisValueType, observation );
  }

  private static void createGRAPFile( Writer writer, String valueAxisType, IObservation observation )
      throws Exception
  {
    SimpleDateFormat sd = new SimpleDateFormat( "yyMMddHH" );
    // write standard header

    // write data

    final IAxis[] axis = observation.getAxisList();
    final IAxis dateAxis = ObservationUtilities.findAxisByType( axis, TimeserieConstants.TYPE_DATE );
    final IAxis valueAxis = ObservationUtilities.findAxisByType( axis, valueAxisType );

    final ITuppleModel values = observation.getValues( null );
    for( int i = 0; i < values.getCount(); i++ )
    {
      final Date date = (Date)values.getElement( i, dateAxis );
      final Object value = values.getElement( i, valueAxis );
      if( i == 0 )
      {
        writer.write( "\n" );
        writer.write( "    " + sd.format( date ) + "000  0\n" );
        //    writer.write( " 95090100000 0\n" );
        writer.write( "grap\n" );

      }
      writer.write( m_grapDateFormat.format( date ) + " " + value.toString() + "\n" );
    }
  }
}