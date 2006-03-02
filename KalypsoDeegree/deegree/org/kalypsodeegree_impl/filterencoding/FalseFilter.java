/*--------------- Kalypso-Deegree-Header ------------------------------------------------------------

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
 
 
 history:
 
 Files in this package are originally taken from deegree and modified here
 to fit in kalypso. As goals of kalypso differ from that one in deegree
 interface-compatibility to deegree is wanted but not retained always. 
 
 If you intend to use this software in other ways than in kalypso 
 (e.g. OGC-web services), you should consider the latest version of deegree,
 see http://www.deegree.org .

 all modifications are licensed as deegree, 
 original copyright:
 
 Copyright (C) 2001 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
 lat/lon GmbH
 http://www.lat-lon.de
 
 ---------------------------------------------------------------------------------------------------*/
package org.kalypsodeegree_impl.filterencoding;

import java.io.ByteArrayInputStream;

import org.kalypsodeegree.filterencoding.Filter;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree_impl.gml.schema.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Abstract superclass representing <Filter>elements (as defined in the Filter DTD). A <Filter>element either consists
 * of (one or more) FeatureId-elements or one operation-element. This is reflected in the two implementations
 * FeatureFilter and ComplexFilter.
 * <p>
 * 
 * @author Markus Schneider
 * @version $Revision$
 */
public class FalseFilter implements Filter
{

  /**
   * Calculates the <tt>Filter</tt>'s logical value (false).
   * <p>
   * 
   * @param feature
   *          (in this special case irrelevant)
   * @return false (always)
   */
  public boolean evaluate( Feature feature )
  {
    return false;
  }

  /** Produces an indented XML representation of this object. */
  public StringBuffer toXML( )
  {
    StringBuffer sb = new StringBuffer();
    sb.append( "<ogc:Filter xmlns:ogc='http://www.opengis.net/ogc'>" );
    sb.append( "<False/>" );
    sb.append( "</ogc:Filter>\n" );
    return sb;
  }

  /**
   * @see org.kalypsodeegree.filterencoding.Filter#clone(org.kalypsodeegree.filterencoding.Filter)
   */

  @Override
  public Filter clone( ) throws CloneNotSupportedException
  {
    StringBuffer buffer = toXML();
    ByteArrayInputStream input = new ByteArrayInputStream( buffer.toString().getBytes() );
    Document asDOM = null;
    try
    {
      asDOM = XMLHelper.getAsDOM( input, true );
      Element element = asDOM.getDocumentElement();
      return AbstractFilter.buildFromDOM( element );
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
    throw new CloneNotSupportedException();
  }
}