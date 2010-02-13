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
package org.kalypso.model.wspm.tuhh.ui.actions;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.kalypso.contribs.eclipse.jface.operation.RunnableContextHelper;
import org.kalypso.model.wspm.tuhh.core.gml.TuhhWspmProject;
import org.kalypso.model.wspm.tuhh.core.profile.importer.wprof.WProfImportOperation;
import org.kalypso.ogc.gml.mapmodel.CommandableWorkspace;

/**
 * @author Gernot Belger
 */
public class WProfImportWizard extends Wizard
{
  private final TuhhWspmProject m_targetProject;

  private final WProfImportFilePage m_wprofFilePage;

  private final CommandableWorkspace m_workspace;

  public WProfImportWizard( final CommandableWorkspace workspace, final TuhhWspmProject targetProject )
  {
    m_workspace = workspace;
    m_targetProject = targetProject;

    m_wprofFilePage = new WProfImportFilePage( "wprofFilePage", "WProf File Selection", null ); //$NON-NLS-1$
    m_wprofFilePage.setDescription( "Select a file to import WProf data from." );
    addPage( m_wprofFilePage );

    setWindowTitle( "WProf Import" );
    setNeedsProgressMonitor( true );
  }

  /**
   * @see org.eclipse.jface.wizard.Wizard#performFinish()
   */
  @Override
  public boolean performFinish( )
  {
    final File shapeFile = m_wprofFilePage.getWProfFile();
    final WProfImportOperation op = new WProfImportOperation( shapeFile, m_workspace, m_targetProject );
    op.setPhotoContext( m_wprofFilePage.getPhotoContext() );
    op.setShapeDefaultSrs( m_wprofFilePage.getShapeDefaultSrs() );

    final IWizardContainer container = getContainer();
    final IStatus result = RunnableContextHelper.execute( container, true, false, op );
    ErrorDialog.openError( getShell(), getWindowTitle(), "Failed to import WProf data", result );

    return !result.matches( IStatus.ERROR );
  }
}
