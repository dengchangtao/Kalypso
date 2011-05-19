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
package org.kalypso.model.wspm.pdb.ui.admin.gaf.internal;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.program.Program;
import org.kalypso.contribs.eclipse.jface.dialog.DialogSettingsUtils;
import org.kalypso.contribs.eclipse.jface.operation.RunnableContextHelper;
import org.kalypso.model.wspm.pdb.connect.IPdbConnection;
import org.kalypso.model.wspm.pdb.gaf.ImportGafData;
import org.kalypso.model.wspm.pdb.gaf.ImportGafOperation;
import org.kalypso.model.wspm.pdb.ui.internal.WspmPdbUiPlugin;

public class ImportGafWizard extends Wizard
{
  private final IPdbConnection m_connection;

  private final ImportGafData m_data = new ImportGafData();

  public ImportGafWizard( final IPdbConnection connection )
  {
    m_connection = connection;

    final IDialogSettings settings = DialogSettingsUtils.getDialogSettings( WspmPdbUiPlugin.getDefault(), getClass().getName() );
    setDialogSettings( settings );

    m_data.init( settings );

    addPage( new ImportGafPage( "gaf", connection, m_data ) ); //$NON-NLS-1$
    addPage( new ChooseWaterPage( "waterBody", connection, m_data ) ); //$NON-NLS-1$
    addPage( new EditStatePage( "state", connection, m_data.getState() ) ); //$NON-NLS-1$

    setNeedsProgressMonitor( true );
  }

  @Override
  public boolean canFinish( )
  {
    /* Do not allow to finish early */
    final IWizardPage currentPage = getContainer().getCurrentPage();
    if( currentPage.getNextPage() != null )
      return false;

    return super.canFinish();
  }

  @Override
  public boolean performFinish( )
  {
    final IDialogSettings settings = getDialogSettings();
    if( settings != null )
      m_data.store( settings );

    final ImportGafOperation operation = new ImportGafOperation( m_connection, m_data );
    final IStatus result = RunnableContextHelper.execute( getContainer(), true, true, operation );

    final ImportGafResultDialog resultDialog = new ImportGafResultDialog( getShell(), result, m_data );
    resultDialog.open();

    /* Open log */
    if( m_data.getOpenLog() )
      openLogFile( m_data.getLogFile() );

    return true;
  }

  private void openLogFile( final File logFile )
  {
    if( !Program.launch( logFile.getAbsolutePath(), logFile.getParent() ) )
    {
      final String message = String.format( "Failed to open external viewer for: '%s", logFile.getAbsolutePath() );
      MessageDialog.openWarning( getShell(), "GAF Import", message );
    }
  }
}