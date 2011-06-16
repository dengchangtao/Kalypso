/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 * 
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  Denickestra�e 22
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
package org.kalypso.model.wspm.pdb.internal.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * Generator for name when uploading gaf data into db.<br/>
 * This class is responsible for taking the various name constraints into account.
 * 
 * @author Gernot Belger
 */
public class PDBNameGenerator
{
  private final Set<String> m_names = new HashSet<String>();

  public String createUniqueName( final String protoName )
  {
    if( !m_names.contains( protoName ) )
    {
      m_names.add( protoName );
      return protoName;
    }

    /* Maxmimum of 99 section with the same station (else something is wrong) */
    for( int i = 0; i < 999; i++ )
    {
      final String name = String.format( "%s_%d", protoName, i );
      if( !m_names.contains( name ) )
      {
        m_names.add( name );
        return name;
      }
    }

    final String message = String.format( "More than 999 cross section have the same station(%s). Unable to create unique cross section name.", protoName );
    throw new IllegalArgumentException( message );
  }

  /**
   * Adds a (supposedly) unique name to these names.
   * 
   * @return <code>false</code>, if the added name was already known and is hence not unique.
   */
  public boolean addUniqueName( final String name )
  {
    if( m_names.contains( name ) )
      return false;

    m_names.add( name );
    return true;
  }
}