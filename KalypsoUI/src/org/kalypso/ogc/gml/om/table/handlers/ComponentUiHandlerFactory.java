/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 * 
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  Denickestraße 22
 *  21073 Hamburg, Germany
 *  http://www.tuhh.de/wb
 * 
 *  and
 *  
 *  Bjoernsen Consulting Engineers (BCE)
 *  Maria Trost 3
 *  56070 Koblenz, Germany
 *  http://www.bjoernsen.de
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *  Contact:
 * 
 *  E-Mail:
 *  belger@bjoernsen.de
 *  schlienger@bjoernsen.de
 *  v.doemming@tuhh.de
 *   
 *  ---------------------------------------------------------------------------*/
package org.kalypso.ogc.gml.om.table.handlers;

import javax.xml.namespace.QName;

import org.apache.commons.lang.NotImplementedException;
import org.kalypso.commons.xml.NS;
import org.kalypso.observation.result.ComponentUtilities;
import org.kalypso.observation.result.IComponent;

/**
 * Factory which creates the right handler for the right value type.
 * 
 * @author Dirk Kuch
 * @author Gernot Belger
 */
public class ComponentUiHandlerFactory
{
  public static IComponentUiHandler getHandler( final IComponent component, final boolean editable, final boolean resizeable, final String columnLabel, final int columnStyle, final int columnWidth, final int columnWidthPercent, final String displayFormat, final String nullFormat, final String parseFormat )
  {
    final QName valueTypeName = component.getValueTypeName();

    if( ComponentUtilities.restrictionContainsEnumeration( component.getRestrictions() ) )
      return new ComponentUiEnumerationHandler( component, editable, resizeable, columnLabel, columnStyle, columnWidth, columnWidthPercent, displayFormat, nullFormat, parseFormat );
    if( valueTypeName.equals( new QName( NS.XSD_SCHEMA, "dateTime" ) ) )
      return new ComponentUiDateHandler( component, editable, resizeable, columnLabel, columnStyle, columnWidth, columnWidthPercent, displayFormat, nullFormat, parseFormat );
    else if( valueTypeName.equals( new QName( NS.XSD_SCHEMA, "double" ) ) )
      return new ComponentUiDoubleHandler( component, editable, resizeable, columnLabel, columnStyle, columnWidth, columnWidthPercent, displayFormat, nullFormat, parseFormat );
    else if( valueTypeName.equals( new QName( NS.XSD_SCHEMA, "decimal" ) ) )
      return new ComponentUiDecimalHandler( component, editable, resizeable, columnLabel, columnStyle, columnWidth, columnWidthPercent, displayFormat, nullFormat, parseFormat );
    else if( valueTypeName.equals( new QName( NS.XSD_SCHEMA, "string" ) ) )
      return new ComponentUiStringHandler( component, editable, resizeable, columnLabel, columnStyle, columnWidth, columnWidthPercent, displayFormat, nullFormat, parseFormat );

    throw new NotImplementedException( "No UI-Handler for component type: " + valueTypeName );
  }
}
