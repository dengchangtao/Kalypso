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
package org.kalypso.model.wspm.pdb.internal.connect;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;
import org.kalypso.model.wspm.pdb.connect.IPdbSettings;
import org.kalypso.model.wspm.pdb.connect.PdbConnectException;
import org.kalypso.model.wspm.pdb.internal.WspmPdbCorePlugin;

/**
 * @author Gernot Belger
 */
public class PdbSettingsReader
{
  public IPdbSettings[] readConnections( final ISecurePreferences preferences ) throws PdbConnectException
  {
    try
    {
      return doRead( preferences );
    }
    catch( final StorageException e )
    {
      e.printStackTrace();
      throw new PdbConnectException( "Failed to access secure storage for pdb connections", e ); //$NON-NLS-1$
    }
  }

  private IPdbSettings[] doRead( final ISecurePreferences preferences ) throws StorageException
  {
    final Collection<IPdbSettings> connections = new ArrayList<IPdbSettings>();

    final PdbSettingsRegistry registry = WspmPdbCorePlugin.getDefault().getConnectionRegistry();

    final String[] names = preferences.childrenNames();
    for( final String name : names )
    {
      final ISecurePreferences childPreferences = preferences.node( name );
      final IPdbSettings settings = registry.readSettings( childPreferences );
      connections.add( settings );
    }

    return connections.toArray( new IPdbSettings[connections.size()] );
  }
}