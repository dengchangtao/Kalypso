/*----------------    FILE HEADER  ------------------------------------------

This file is part of deegree.
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

Jens Fitzke
Department of Geography
University of Bonn
Meckenheimer Allee 166
53115 Bonn
Germany
E-Mail: jens.fitzke@uni-bonn.de

                 
 ---------------------------------------------------------------------------*/
package org.deegree_impl.graphics.displayelements;

import java.awt.*;

import java.io.Serializable;

import org.deegree.graphics.displayelements.MapDiagram;
import org.deegree.model.feature.*;
import org.deegree.model.geometry.*;
import org.deegree.graphics.transformation.GeoTransform;


/**
 * The interface describes a MapDiagram-DisplayElement. A MapDiagram can be a
 * Line-Plot, a cake-Plot, another Map or anything that's renderable.
 * <p>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version $Revision$ $Date$
 */
class MapDiagram_Impl extends LocalizedDisplayElement_Impl implements MapDiagram, Serializable {
    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = 5868830845848601425L;

    /**
     * Creates a new MapDiagram_Impl object.
     *
     * @param feature 
     * @param locations 
     * @param renderableObject 
     */
    MapDiagram_Impl( Feature feature, GM_MultiPoint locations, Component renderableObject ) {
        super( feature, locations, renderableObject );
    }

    /**
     *  renders the DisplayElement to the submitted graphic context
     */
    public void paint( Graphics g, GeoTransform projection ) {
    }
}