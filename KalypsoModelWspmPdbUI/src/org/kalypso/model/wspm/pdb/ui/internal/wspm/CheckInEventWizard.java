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
package org.kalypso.model.wspm.pdb.ui.internal.wspm;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.Wizard;
import org.kalypso.contribs.eclipse.jface.operation.ICoreRunnableWithProgress;
import org.kalypso.contribs.eclipse.jface.operation.RunnableContextHelper;
import org.kalypso.core.status.StatusDialog2;
import org.kalypso.model.wspm.pdb.ui.internal.admin.event.EditEventPage;
import org.kalypso.model.wspm.pdb.ui.internal.i18n.Messages;
import org.kalypso.model.wspm.pdb.wspm.IEditEventPageData;

/**
 * Uploads local WSPM data into the cross section database.
 * 
 * @author Gernot Belger
 */
public class CheckInEventWizard extends Wizard
{
  private final IEditEventPageData m_data;

  private final ICoreRunnableWithProgress m_operation;

  public CheckInEventWizard( final IEditEventPageData data, final ICoreRunnableWithProgress operation )
  {
    m_data = data;
    m_operation = operation;

    setNeedsProgressMonitor( true );
  }

  @Override
  public void addPages( )
  {
    final EditEventPage editStatePage = new EditEventPage( "editEvent", m_data, false ); //$NON-NLS-1$
    editStatePage.setDescription( Messages.getString( "CheckInEventWizard.1" ) ); //$NON-NLS-1$

    addPage( editStatePage );
  }

  @Override
  public boolean performFinish( )
  {
    final IStatus status = RunnableContextHelper.execute( getContainer(), true, true, m_operation );
    if( !status.isOK() )
      new StatusDialog2( getShell(), status, getWindowTitle() ).open();

    // FIXME: if wizard is not closed due to error, we need to reinitialize the state, as it is still attached to the
    // old session

    return !status.matches( IStatus.ERROR );
  }
}