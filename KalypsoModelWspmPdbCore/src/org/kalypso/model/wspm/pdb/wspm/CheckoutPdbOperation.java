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
package org.kalypso.model.wspm.pdb.wspm;

import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.kalypso.contribs.eclipse.jface.operation.ICoreRunnableWithProgress;
import org.kalypso.contribs.eclipse.ui.progress.ProgressUtilities;
import org.kalypso.model.wspm.pdb.internal.wspm.CheckoutCrossSectionsWorker;
import org.kalypso.model.wspm.pdb.internal.wspm.CheckoutRemoveWorker;
import org.kalypso.model.wspm.pdb.internal.wspm.CheckoutStateWorker;
import org.kalypso.model.wspm.pdb.internal.wspm.CheckoutWaterBodyWorker;
import org.kalypso.model.wspm.pdb.internal.wspm.CheckoutWaterlevelWorker;

/**
 * @author Gernot Belger
 */
public class CheckoutPdbOperation implements ICoreRunnableWithProgress
{
  private final CheckoutPdbData m_data;


  public CheckoutPdbOperation( final CheckoutPdbData data )
  {
    m_data = data;
  }

  @Override
  public IStatus execute( final IProgressMonitor monitor ) throws CoreException
  {
    monitor.beginTask( "Loading data from cross section database", 100 );

    final URI documentBase = m_data.getDocumentBase();
    final CheckoutDataMapping mapping = m_data.getMapping();

    final CheckoutRemoveWorker removeWorker = new CheckoutRemoveWorker( m_data );
    removeWorker.execute();
    monitor.worked( 5 );

    final CheckoutWaterBodyWorker waterBodyWorker = new CheckoutWaterBodyWorker( mapping );
    waterBodyWorker.execute( new SubProgressMonitor( monitor, 5 ) );

    final CheckoutStateWorker stateWorker = new CheckoutStateWorker( mapping );
    stateWorker.execute( new SubProgressMonitor( monitor, 5 ) );

    final CheckoutCrossSectionsWorker crossSectionsWorker = new CheckoutCrossSectionsWorker( mapping, documentBase );
    crossSectionsWorker.execute( new SubProgressMonitor( monitor, 40 ) );

    final CheckoutWaterlevelWorker waterlevelWorker = new CheckoutWaterlevelWorker( mapping );
    waterlevelWorker.execute( new SubProgressMonitor( monitor, 40 ) );

    mapping.fireEvents( new SubProgressMonitor( monitor, 5 ) );

    ProgressUtilities.done( monitor );

    return Status.OK_STATUS;
  }
}