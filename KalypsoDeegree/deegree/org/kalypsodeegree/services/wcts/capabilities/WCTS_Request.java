/*----------------    FILE HEADER  ------------------------------------------

This file is part of Deegree.
Copyright (C) 2001 by:
EXSE, Department of Geography, University of Bonn
http://www.giub.uni-bonn.de/exse/
lat/lon Fitzke/Fretter/Poth GbR
http://www.lat-lon.de

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

Andreas Poth
lat/lon Fitzke/Fretter/Poth GbR
Meckenheimer Allee 176
53115 Bonn
Germany
E-Mail: poth@lat-lon.de

Markus Mueller
Department of Geography
University of Bonn
Meckenheimer Allee 166
53115 Bonn
Germany
E-Mail: mm@giub.uni-bonn.de

 ---------------------------------------------------------------------------*/
package org.deegree.services.wcts.capabilities;

/**
 * <p>This WCTS_Request distributes 4 elements, which are part of the
 * &lt;Request&gt; element: &lt;GetCapabilities&gt;, &lt;Transform&gt;,
 * &lt;IsTransformable&gt; and the optional &lt;DescribeTransformation&gt;
 * <p>Everyone of these element consits the same subelements, which are
 * combined in the ActionType class.
 *
 * <p>----------------------------------------------------------------------</p>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version 2002-07-10
 */

public interface WCTS_Request
{
	
	/**
     * returns the mandatory &lt;GetCapabilities&gt; element
     */
     public ActionType getGetCapabilities();

	/**
     * returns the mandatory &lt;Transform&gt; element
     */     
     public ActionType getTransform();

	/**
     * returns the mandatory &lt;IsTransformable&gt; element
     */     
     public ActionType getIsTransformable();

	/**
     * returns the optional &lt;DescribeTransformation&gt; element
     */     
     public ActionType getDescribeTransformation();
}