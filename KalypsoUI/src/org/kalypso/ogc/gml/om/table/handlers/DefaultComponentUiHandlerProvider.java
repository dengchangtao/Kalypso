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

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.swt.SWT;
import org.kalypso.commons.xml.NS;
import org.kalypso.observation.result.ComponentUtilities;
import org.kalypso.observation.result.IComponent;

/**
 * Default implementation of {@link IComponentUiHandlerProvider}.<br/> Creates columns for all components of the given
 * observation according to its types.
 * 
 * @author Gernot Belger
 */
public class DefaultComponentUiHandlerProvider implements IComponentUiHandlerProvider
{
  public IComponentUiHandler[] createComponentHandler( final IComponent[] components )
  {
    final List<IComponentUiHandler> result = new ArrayList<IComponentUiHandler>();

    final int widthPercent = components.length == 0 ? 100 : (int) (100.0 / components.length);

    for( final IComponent component : components )
    {
      final IComponentUiHandler handlerForComponent = handlerForComponent( component, widthPercent );
      if( handlerForComponent != null )
        result.add( handlerForComponent );
    }

    return result.toArray( new IComponentUiHandler[result.size()] );
  }

  private IComponentUiHandler handlerForComponent( final IComponent component, final int columnWidthPercent )
  {
    // Some default value
    final boolean editable = true;
    final boolean resizeable = true;
    final boolean moveable = true;
    final String columnLabel = component.getName();
    final int columnWidth = 100;

    if( ComponentUtilities.restrictionContainsEnumeration( component.getRestrictions() ) )
      return new ComponentUiEnumerationHandler( component, editable, resizeable, moveable, columnLabel, SWT.NONE, columnWidth, columnWidthPercent, "", "", null );

    final QName valueTypeName = component.getValueTypeName();

    if( valueTypeName.equals( new QName( NS.XSD_SCHEMA, "dateTime" ) ) )
      return new ComponentUiDateHandler( component, editable, resizeable, moveable, columnLabel, SWT.NONE, columnWidth, columnWidthPercent, "%1$tm %1$te,%1$tY", "", null );

    if( valueTypeName.equals( new QName( NS.XSD_SCHEMA, "double" ) ) )
      return new ComponentUiDoubleHandler( component, editable, resizeable, moveable, columnLabel, SWT.RIGHT, columnWidth, columnWidthPercent, "%f", "", null );

    if( valueTypeName.equals( new QName( NS.XSD_SCHEMA, "decimal" ) ) )
      return new ComponentUiDecimalHandler( component, editable, resizeable, moveable, columnLabel, SWT.RIGHT, columnWidth, columnWidthPercent, "%f", "", null );

    if( valueTypeName.equals( new QName( NS.XSD_SCHEMA, "integer" ) ) )
      return new ComponentUiIntegerHandler( component, editable, resizeable, moveable, columnLabel, SWT.RIGHT, columnWidth, columnWidthPercent, "%d", "", null );

    if( valueTypeName.equals( new QName( NS.XSD_SCHEMA, "string" ) ) )
      return new ComponentUiStringHandler( component, editable, resizeable, moveable, columnLabel, SWT.LEFT, columnWidth, columnWidthPercent, "%s", "", null );

    return null;
  }

}
