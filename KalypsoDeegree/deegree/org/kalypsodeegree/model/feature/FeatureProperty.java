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

package org.deegree.model.feature;


/**
*
* the interface describes a property entry of a feature
* 
* <p>-----------------------------------------------------------------------</p>
*
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
* @version $Revision$ $Date$
*/ 

public interface FeatureProperty {

   /**
    * returns the name of the property
    */
    public String getName();

   /**
    * sets the name of the property
    */
    public void setName(String name);

   /**
    * returns the value of the property
    */
    public Object getValue();

   /**
    * sets the value of the property
    */
    public void setValue(Object value);

}
/*
 * Changes to this class. What the people haven been up to:
 *
 * $Log$
 * Revision 1.1  2004/05/11 16:43:22  doemming
 * Initial revision
 *
 * Revision 1.2  2004/02/09 07:57:01  poth
 * no message
 *
 * Revision 1.1.1.1  2002/09/25 16:01:47  poth
 * no message
 *
 * Revision 1.2  2002/08/15 10:02:25  ap
 * no message
 *
 * Revision 1.1  2002/04/04 16:17:15  ap
 * no message
 *
 * Revision 1.2  2001/10/15 14:48:19  ap
 * no message
 *
 * Revision 1.1  2001/10/05 15:19:43  ap
 * no message
 *
 */
