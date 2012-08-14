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
package org.kalypso.kalypsomodel1d2d.ui.map.grid;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.kalypso.commons.command.EmptyCommand;
import org.kalypso.contribs.eclipse.jface.operation.ICoreRunnableWithProgress;
import org.kalypso.kalypsomodel1d2d.ui.map.util.TempGrid;
import org.kalypso.ogc.gml.mapmodel.CommandableWorkspace;

/**
 * @author Gernot Belger
 */
class CreateGridOperation implements ICoreRunnableWithProgress
{
  private static final double DISTANCE_DEF = 0.01;

  private final TempGrid m_tempGrid;

  private final CommandableWorkspace m_workspace;

  public CreateGridOperation( final TempGrid tempGrid, final CommandableWorkspace workspace )
  {
    m_tempGrid = tempGrid;
    m_workspace = workspace;
  }

  @Override
  public IStatus execute( final IProgressMonitor monitor )
  {
    final IStatus status = m_tempGrid.getAddToModelCommand( m_workspace, DISTANCE_DEF );

    try
    {
      m_workspace.postCommand( new EmptyCommand( "set dirty command ", false ) ); //$NON-NLS-1$
    }
    catch( final Exception e )
    {
      e.printStackTrace();
    }

    return status;
  }
}