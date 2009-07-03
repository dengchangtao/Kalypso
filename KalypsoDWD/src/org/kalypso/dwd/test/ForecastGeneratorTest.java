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
package org.kalypso.dwd.test;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import junit.framework.TestCase;

import org.apache.commons.io.CopyUtils;
import org.apache.commons.io.FileUtils;
import org.kalypso.dwd.ForecastGenerator;
import org.kalypso.dwd.dwdzml.DwdzmlConf;
import org.kalypso.dwd.dwdzml.ObjectFactory;

/**
 * @author doemming
 */
public class ForecastGeneratorTest extends TestCase
{
  public void test()
  {
    try
    {
      //      final String foreCastName = "lm_2004_10_22_00";
      final String foreCastName = "lm_2004_11_10_00";
      //      final String foreCastName = "lm_2004_11_09_00";
      //      final String foreCastName = "lm_2004_11_08_00";
      final InputStream rasterConfAsStream = getClass().getResourceAsStream( "raster.conf" );
      final ObjectFactory fac = new ObjectFactory();
      final Unmarshaller unmarshaller = fac.createUnmarshaller();
      final DwdzmlConf conf = (DwdzmlConf)unmarshaller.unmarshal( rasterConfAsStream );
      final String inputFolder = conf.getInputFolder();

      final InputStream dataAsStream = getClass().getResourceAsStream( foreCastName );
      final File tmpDir = new File( inputFolder );
      if( !tmpDir.exists() )
      {
        tmpDir.mkdirs();
      }
      else
      {
        FileUtils.cleanDirectory( tmpDir );
      }
      final File dataFile = new File( tmpDir, foreCastName );
      final FileWriter dataWriter = new FileWriter( dataFile );
      CopyUtils.copy( dataAsStream, dataWriter );
      dataWriter.close();

      final Marshaller marshaller = fac.createMarshaller();
      final File rasterConfFile = new File( tmpDir, "raster.conf" );
      final FileWriter confWriter = new FileWriter( rasterConfFile );
      marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
      marshaller.marshal( conf, confWriter );
      confWriter.close();

      final String[] args = new String[]
      { rasterConfFile.getCanonicalPath()
      //      "C:\\TMP\\raster\\raster.conf"
      };
      ForecastGenerator.main( args );
    }
    catch( Exception e )
    {
      e.printStackTrace();
      fail( e.getMessage() );
    }
  }
}