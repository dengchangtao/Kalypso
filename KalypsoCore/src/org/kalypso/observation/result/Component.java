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
package org.kalypso.observation.result;

import javax.xml.namespace.QName;

/**
 * @author schlienger
 */
public class Component implements IComponent
{
  private final String m_name;

  private final String m_description;

  private final QName m_valueTypeName;

  private final Object m_defaultValue;

  public Component( final String name, final String description, final QName valueTypeName )
  {
    this( name, description, valueTypeName, null );
  }

  public Component( final String name, final String description, final QName valueTypeName, final Object defaultValue )
  {
    if( name == null )
      throw new IllegalArgumentException( "name argument must not be null for " + getClass().getName() );
    if( valueTypeName == null )
      throw new IllegalArgumentException( "valueTypeName argument must not be null for " + getClass().getName() );

    m_name = name;
    m_description = description;
    m_valueTypeName = valueTypeName;
    m_defaultValue = defaultValue;
  }

  /**
   * @see org.kalypso.om.tuple.IComponent#getName()
   */
  public String getName( )
  {
    return m_name;
  }

  /**
   * @see org.kalypso.observation.result.IComponent#getValueTypeName()
   */
  public QName getValueTypeName( )
  {
    return m_valueTypeName;
  }

  /**
   * @see org.kalypso.om.tuple.IComponent#getDefaultValue()
   */
  public Object getDefaultValue( )
  {
    return m_defaultValue;
  }

  /**
   * @see org.kalypso.observation.result.IComponent#getDescription()
   */
  public String getDescription( )
  {
    return m_description;
  }
}
