/*
 * --------------- Kalypso-Header --------------------------------------------
 * 
 * This file is part of kalypso. Copyright (C) 2004, 2005 by:
 * 
 * Technical University Hamburg-Harburg (TUHH) Institute of River and coastal
 * engineering Denickestr. 22 21073 Hamburg, Germany http://www.tuhh.de/wb
 * 
 * and
 * 
 * Bjoernsen Consulting Engineers (BCE) Maria Trost 3 56070 Koblenz, Germany
 * http://www.bjoernsen.de
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Contact:
 * 
 * E-Mail: belger@bjoernsen.de schlienger@bjoernsen.de v.doemming@tuhh.de
 * 
 * ------------------------------------------------------------------------------------
 */
package org.kalypso.ogc.sensor.zml.request;

import java.io.StringReader;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.MetadataList;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.impl.SimpleObservation;
import org.kalypso.ogc.sensor.timeseries.TimeserieUtils;
import org.kalypso.ogc.sensor.zml.ZmlURLConstants;
import org.kalypso.zml.request.ObjectFactory;
import org.kalypso.zml.request.RequestType;
import org.xml.sax.InputSource;

/**
 * Factory-class for parsing Zml-Requests
 * 
 * @author schlienger (25.05.2005)
 */
public class RequestFactory
{
  private static ObjectFactory OF = new ObjectFactory();

  private RequestFactory()
  {
    // empty
  }

  public static RequestType parseRequest( final String href )
      throws SensorException
  {
    if( href == null || href.length() == 0 )
      throw new SensorException( "No request definition" );

    final int i1 = href.indexOf( ZmlURLConstants.TAG_REQUEST1 );
    if( i1 == -1 )
      throw new SensorException( "No request definition. URL: " + href );

    final int i2 = href.indexOf( ZmlURLConstants.TAG_REQUEST2, i1 );
    if( i2 == -1 )
      throw new SensorException(
          "URL-fragment does not contain a valid request definition. URL: "
              + href );

    final String strRequestXml = href.substring( i1
        , i2 + ZmlURLConstants.TAG_REQUEST2.length() );

    StringReader sr = null;
    try
    {
      sr = new StringReader( strRequestXml );
      final RequestType xmlReq = (RequestType)OF.createUnmarshaller()
          .unmarshal( new InputSource( sr ) );
      sr.close();

      return xmlReq;
    }
    catch( final JAXBException e )
    {
      e.printStackTrace();
      throw new SensorException( e );
    }
    finally
    {
      IOUtils.closeQuietly( sr );
    }
  }

  /**
   * Create a default observation (best effort) according to the request.
   */
  public static IObservation createDefaultObservation( final RequestType xmlReq )
  {
    final String[] splits = xmlReq.getAxes().split( "," );
    final IAxis[] axes = new IAxis[splits.length];
    for( int i = 0; i < splits.length; i++ )
      axes[i] = TimeserieUtils.createDefaulAxis( splits[i] );

    final SimpleObservation obs = new SimpleObservation( "", "", xmlReq
        .getName(), false, null, new MetadataList(), axes );

    return obs;
  }
}
