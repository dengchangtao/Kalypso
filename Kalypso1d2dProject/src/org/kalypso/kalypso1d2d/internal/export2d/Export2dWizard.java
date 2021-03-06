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
package org.kalypso.kalypso1d2d.internal.export2d;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.kalypso.contribs.eclipse.jface.dialog.DialogSettingsUtils;
import org.kalypso.contribs.eclipse.jface.operation.ICoreRunnableWithProgress;
import org.kalypso.contribs.eclipse.jface.operation.RunnableContextHelper;
import org.kalypso.core.status.StatusDialog;
import org.kalypso.kalypso1d2d.internal.i18n.Messages;
import org.kalypso.kalypso1d2d.pjt.Kalypso1d2dProjectPlugin;

public class Export2dWizard extends Wizard implements IWorkbenchWizard
{
  private Export2dFileSelectWizardPage m_page1;

  protected IProject m_project = null;

  public Export2dWizard( )
  {
    final IDialogSettings section = DialogSettingsUtils.getDialogSettings( Kalypso1d2dProjectPlugin.getDefault(), "ExportAsFileWizard" ); //$NON-NLS-1$

    setDialogSettings( section );
  }

  @Override
  public void init( final IWorkbench workbench, final IStructuredSelection selection )
  {
  }

  @Override
  public void addPages( )
  {
    super.addPages();

    m_page1 = new Export2dFileSelectWizardPage( "fileselect", new String[] { "*.2d", "*.2dm", "*.hmo" }, new String[] { Messages.getString( "org.kalypso.wizards.export2d.Export2dWizard.3" ), Messages.getString( "org.kalypso.wizards.export2d.Export2dWizard.4" ), Messages.getString( "org.kalypso.wizards.export2d.Export2dWizard.8" ) } ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

    addPage( m_page1 );
  }

  @Override
  public void createPageControls( final Composite pageContainer )
  {
    setWindowTitle( Messages.getString( "org.kalypso.wizards.export2d.Export2dWizard.6" ) ); //$NON-NLS-1$
    setNeedsProgressMonitor( true );
  }

  @Override
  public boolean performFinish( )
  {
    final File exportFile = new File( m_page1.getFilePath() );
    final boolean exportMiddleNodes = m_page1.isSelectedExportMiddleNodes();
    final boolean exportRoughness = m_page1.isSelectedExportRoughessData();
    final String selectedExtension = m_page1.getSelectedExtension();
    if( selectedExtension == null )
      return false;

    final ICoreRunnableWithProgress operation = new Export2dMeshRunnable( exportFile, selectedExtension, exportRoughness, exportMiddleNodes );

    final IStatus result = RunnableContextHelper.execute( getContainer(), true, true, operation );
    if( !result.isOK() )
      StatusDialog.open( getShell(), result, getWindowTitle() );

    return !result.matches( IStatus.ERROR );
  }
}