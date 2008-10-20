/*--------------- Kalypso-Header ------------------------------------------

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

 --------------------------------------------------------------------------*/

package org.kalypso.commons.java.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;

/**
 * @author belger
 */
public class UrlUtilities
{
  private UrlUtilities( )
  { 
    // will not get instantiated
  }

  /**
   * Opens a stream on the given url and copies its content into a string.
   * 
   * @throws IOException
   */
  public static String toString( final URL url, final String encoding ) throws IOException
  {
    InputStream is = null;
    try
    {
      is = url.openStream();
      final String content = IOUtils.toString( is, encoding );
      is.close();
      return content;
    }
    finally
    {
      IOUtils.closeQuietly( is );
    }
  }

  /**
   * Opens a stream on the given url and copies its content into a byte array.
   * 
   * @throws IOException
   */
  public static byte[] toByteArray( final URL url ) throws IOException
  {
    InputStream is = null;
    try
    {
      is = url.openStream();
      final byte[] content = IOUtils.toByteArray( is );
      is.close();
      return content;
    }
    finally
    {
      IOUtils.closeQuietly( is );
    }
  }

}
