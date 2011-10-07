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
package org.kalypso.kalypsomodel1d2d.ui.map.import2d.imports;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.Wizard;
import org.kalypso.contribs.eclipse.jface.dialog.DialogSettingsUtils;
import org.kalypso.contribs.eclipse.jface.operation.RunnableContextHelper;
import org.kalypso.core.status.StatusDialog;
import org.kalypso.kalypsomodel1d2d.KalypsoModel1D2DPlugin;
import org.kalypso.kalypsomodel1d2d.ui.map.import2d.Import2dElementsData;

/**
 * @author Gernot Belger
 */
public class Import2dImportWizard extends Wizard
{
  private final Import2dImportData m_importData = new Import2dImportData();

  private final Import2dElementsData m_data;

  private final IImport2dImportOperation[] m_operations;

  public Import2dImportWizard( final Import2dElementsData data )
  {
    m_data = data;

    m_operations = new IImport2dImportOperation[] { new Import2dImport2dmOperation( m_data, m_importData ), new Import2dImportShapeOperation( m_data, m_importData ) };

    setDialogSettings( DialogSettingsUtils.getDialogSettings( KalypsoModel1D2DPlugin.getDefault(), getClass().getName() ) );
    setWindowTitle( "Import 2D Elements" );
    setNeedsProgressMonitor( true );

    addPage( new Import2dImportPage( "filePage", m_importData, m_operations ) ); //$NON-NLS-1$

    m_importData.init( getDialogSettings() );
  }

  @Override
  public boolean performCancel( )
  {
    m_importData.storeSettings( getDialogSettings() );

    return super.performCancel();
  }

  @Override
  public boolean performFinish( )
  {
    m_importData.storeSettings( getDialogSettings() );

    final File smsFile = m_importData.getFile();

    final IImport2dImportOperation operation = getOperation( smsFile );

    final IStatus status = RunnableContextHelper.execute( getContainer(), true, true, operation );
    new StatusDialog( getShell(), status, getWindowTitle() ).open();

    return !status.matches( IStatus.ERROR );
  }

  private IImport2dImportOperation getOperation( final File smsFile )
  {
    final String filename = smsFile.getName();

    for( final IImport2dImportOperation operation : m_operations )
    {
      final String filterExtension = operation.getFilterExtension();
      if( filename.endsWith( filterExtension ) )
        return operation;
    }

    throw new IllegalStateException();
  }
}