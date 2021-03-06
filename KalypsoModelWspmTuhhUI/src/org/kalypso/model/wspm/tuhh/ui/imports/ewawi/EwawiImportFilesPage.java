/** This file is part of Kalypso
 *
 *  Copyright (c) 2012 by
 *
 *  Bj�rnsen Beratende Ingenieure GmbH, Koblenz, Germany (Bjoernsen Consulting Engineers), http://www.bjoernsen.de
 *  Technische Universit�t Hamburg-Harburg, Institut f�r Wasserbau, Hamburg, Germany
 *  (Technical University Hamburg-Harburg, Institute of River and Coastal Engineering), http://www.tu-harburg.de/wb/
 *
 *  Kalypso is free software: you can redistribute it and/or modify it under the terms  
 *  of the GNU Lesser General Public License (LGPL) as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  Kalypso is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with Kalypso.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kalypso.model.wspm.tuhh.ui.imports.ewawi;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.kalypso.commons.databinding.DataBinder;
import org.kalypso.commons.databinding.IDataBinding;
import org.kalypso.commons.databinding.jface.wizard.DatabindingWizardPage;
import org.kalypso.commons.databinding.swt.DirectoryBinding;
import org.kalypso.commons.databinding.swt.FileAndHistoryData;
import org.kalypso.commons.databinding.swt.FileBinding;
import org.kalypso.commons.databinding.validation.StringBlankValidator;
import org.kalypso.contribs.eclipse.jface.wizard.FileChooserDelegateOpen;
import org.kalypso.contribs.java.io.FileExtensions;
import org.kalypso.contribs.java.io.FilePattern;
import org.kalypso.model.wspm.tuhh.ui.i18n.Messages;
import org.kalypso.transformation.ui.CRSSelectionPanel;

/**
 * @author Holger Albert
 */
public class EwawiImportFilesPage extends WizardPage
{
  private final EwawiImportData m_data;

  public EwawiImportFilesPage( final EwawiImportData data )
  {
    super( "ewawiImportFilesPage" ); //$NON-NLS-1$

    m_data = data;

    setTitle( Messages.getString("EwawiImportFilesPage.0") ); //$NON-NLS-1$
    setDescription( Messages.getString("EwawiImportFilesPage.1") ); //$NON-NLS-1$
  }

  @Override
  public void createControl( final Composite parent )
  {
    /* Create the databinding. */
    final DatabindingWizardPage dataBinding = new DatabindingWizardPage( this, null );

    /* Create the main composite. */
    final Composite main = new Composite( parent, SWT.NONE );
    final GridLayout mainLayout = new GridLayout( 1, false );
    mainLayout.marginHeight = 0;
    mainLayout.marginWidth = 0;
    main.setLayout( mainLayout );

    /* Create a group. */
    final Group filesGroup = new Group( main, SWT.NONE );
    filesGroup.setLayout( new GridLayout( 2, false ) );
    filesGroup.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
    filesGroup.setText( Messages.getString("EwawiImportFilesPage.2") ); //$NON-NLS-1$

    /* Create a label. */
    final Label proFileLabel = new Label( filesGroup, SWT.NONE );
    proFileLabel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
    proFileLabel.setText( Messages.getString("EwawiImportFilesPage.3") ); //$NON-NLS-1$

    /* Create the .pro file controls. */
    createProFileControls( filesGroup, dataBinding );

    /* Create a label. */
    final Label staFileLabel = new Label( filesGroup, SWT.NONE );
    staFileLabel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
    staFileLabel.setText( Messages.getString("EwawiImportFilesPage.4") ); //$NON-NLS-1$

    /* Create the .sta file controls. */
    createStaFileControls( filesGroup, dataBinding );

    /* Create a label. */
    final Label fotoDirectoryLabel = new Label( filesGroup, SWT.NONE );
    fotoDirectoryLabel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
    fotoDirectoryLabel.setText( Messages.getString("EwawiImportFilesPage.5") ); //$NON-NLS-1$

    /* Create the foto directory controls. */
    createFotoDirectoryControls( filesGroup, dataBinding );

    /* Create a label. */
    final Label documentDirectoryLabel = new Label( filesGroup, SWT.NONE );
    documentDirectoryLabel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
    documentDirectoryLabel.setText( Messages.getString("EwawiImportFilesPage.6") ); //$NON-NLS-1$

    /* Create the document directory controls. */
    createDocumentDirectoryControls( filesGroup, dataBinding );

    /* Create a label. */
    final Label shpFileLabel = new Label( filesGroup, SWT.NONE );
    shpFileLabel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
    shpFileLabel.setText( Messages.getString("EwawiImportFilesPage.7") ); //$NON-NLS-1$

    /* Create the .shp file controls. */
    createShpFileControls( filesGroup, dataBinding );

    /* Create the coordinate system control. */
    createCoordinateSystemControl( main, dataBinding );

    /* Create a group. */
    final Group optionsGroup = new Group( main, SWT.NONE );
    optionsGroup.setLayout( new GridLayout( 1, false ) );
    optionsGroup.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    optionsGroup.setText( Messages.getString("EwawiImportFilesPage.8") ); //$NON-NLS-1$

    /* Create the direction upstreams controls. */
    createDirectionUpstreamsControls( optionsGroup, dataBinding );

    /* Set the control to the page. */
    setControl( main );
  }

  private void createProFileControls( final Composite main, final DatabindingWizardPage dataBinding )
  {
    final FileAndHistoryData sourceFile = m_data.getProFile();
    final FilePattern filePattern = new FilePattern( "*.pro", Messages.getString("EwawiImportFilesPage.9") ); //$NON-NLS-1$ //$NON-NLS-2$

    final IObservableValue modelFile = BeansObservables.observeValue( sourceFile, FileAndHistoryData.PROPERTY_FILE );
    final FileChooserDelegateOpen delegate = createFileChooserDelegate( filePattern, false );
    final FileBinding fileBinding = new FileBinding( dataBinding, modelFile, delegate );

    final IObservableValue modelHistory = BeansObservables.observeValue( sourceFile, FileAndHistoryData.PROPERTY_HISTORY );
    final Control historyControl = fileBinding.createFileFieldWithHistory( main, modelHistory );
    historyControl.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

    final Button fileButton = fileBinding.createFileSearchButton( main, historyControl );
    fileButton.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false ) );
  }

  private void createStaFileControls( final Composite main, final DatabindingWizardPage dataBinding )
  {
    final FileAndHistoryData sourceFile = m_data.getStaFile();
    final FilePattern filePattern = new FilePattern( "*.sta", Messages.getString("EwawiImportFilesPage.10") ); //$NON-NLS-1$ //$NON-NLS-2$

    final IObservableValue modelFile = BeansObservables.observeValue( sourceFile, FileAndHistoryData.PROPERTY_FILE );
    final FileChooserDelegateOpen delegate = createFileChooserDelegate( filePattern, false );
    final FileBinding fileBinding = new FileBinding( dataBinding, modelFile, delegate );

    final IObservableValue modelHistory = BeansObservables.observeValue( sourceFile, FileAndHistoryData.PROPERTY_HISTORY );
    final Control historyControl = fileBinding.createFileFieldWithHistory( main, modelHistory );
    historyControl.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

    final Button fileButton = fileBinding.createFileSearchButton( main, historyControl );
    fileButton.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false ) );
  }

  private void createFotoDirectoryControls( final Composite main, final DatabindingWizardPage dataBinding )
  {
    final IObservableValue modelDir = BeansObservables.observeValue( m_data, EwawiImportData.PROPERTY_FOTO_DIRECTORY );
    final DirectoryBinding directoryBinding = new DirectoryBinding( modelDir, SWT.OPEN );

    final IObservableValue modelHistory = BeansObservables.observeValue( m_data, EwawiImportData.PROPERTY_FOTO_DIRECTORY_HISTORY );
    final Control historyControl = directoryBinding.createDirectoryFieldWithHistory( main, modelHistory );
    historyControl.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

    final Button directoryButton = directoryBinding.createDirectorySearchButton( main, historyControl, Messages.getString("EwawiImportFilesPage.11"), Messages.getString("EwawiImportFilesPage.12") ); //$NON-NLS-1$ //$NON-NLS-2$
    directoryButton.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false ) );

    directoryBinding.applyBinding( dataBinding );
  }

  private void createDocumentDirectoryControls( final Composite main, final DatabindingWizardPage dataBinding )
  {
    final IObservableValue modelDir = BeansObservables.observeValue( m_data, EwawiImportData.PROPERTY_DOCUMENT_DIRECTORY );
    final DirectoryBinding directoryBinding = new DirectoryBinding( modelDir, SWT.OPEN );

    final IObservableValue modelHistory = BeansObservables.observeValue( m_data, EwawiImportData.PROPERTY_DOCUMENT_DIRECTORY_HISTORY );
    final Control historyControl = directoryBinding.createDirectoryFieldWithHistory( main, modelHistory );
    historyControl.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

    final Button directoryButton = directoryBinding.createDirectorySearchButton( main, historyControl, Messages.getString("EwawiImportFilesPage.13"), Messages.getString("EwawiImportFilesPage.14") ); //$NON-NLS-1$ //$NON-NLS-2$
    directoryButton.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false ) );

    directoryBinding.applyBinding( dataBinding );
  }

  private void createShpFileControls( final Composite main, final DatabindingWizardPage dataBinding )
  {
    final FileAndHistoryData sourceFile = m_data.getRiverShapeData().getShpFile();
    final FilePattern filePattern = new FilePattern( "*.shp", Messages.getString("EwawiImportFilesPage.15") ); //$NON-NLS-1$ //$NON-NLS-2$

    final IObservableValue modelFile = BeansObservables.observeValue( sourceFile, FileAndHistoryData.PROPERTY_FILE );
    final FileChooserDelegateOpen delegate = createFileChooserDelegate( filePattern, true );
    final FileBinding fileBinding = new FileBinding( dataBinding, modelFile, delegate );

    final IObservableValue modelHistory = BeansObservables.observeValue( sourceFile, FileAndHistoryData.PROPERTY_HISTORY );
    final Control historyControl = fileBinding.createFileFieldWithHistory( main, modelHistory );
    historyControl.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

    final Button fileButton = fileBinding.createFileSearchButton( main, historyControl );
    fileButton.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false ) );
  }

  private void createCoordinateSystemControl( final Composite panel, final IDataBinding dataBinding )
  {
    final CRSSelectionPanel crsPanel = new CRSSelectionPanel( panel, SWT.NONE );
    crsPanel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    final IObservableValue target = crsPanel.observe();
    final IObservableValue model = BeansObservables.observeValue( m_data, EwawiImportData.PROPERTY_COORDINATE_SYSTEM );

    final DataBinder binder = new DataBinder( target, model );
    binder.addTargetAfterGetValidator( new StringBlankValidator( IStatus.ERROR, Messages.getString("EwawiImportFilesPage.16") ) ); //$NON-NLS-1$
    dataBinding.bindValue( binder );
  }

  private void createDirectionUpstreamsControls( final Group optionsGroup, final DatabindingWizardPage dataBinding )
  {
    final Button directionUpstreamsButton = new Button( optionsGroup, SWT.CHECK );
    directionUpstreamsButton.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    directionUpstreamsButton.setText( Messages.getString("EwawiImportFilesPage.17") ); //$NON-NLS-1$

    final IObservableValue target = SWTObservables.observeSelection( directionUpstreamsButton );
    final IObservableValue model = BeansObservables.observeValue( m_data, EwawiImportData.PROPERTY_DIRECTION_UPSTREAMS );
    dataBinding.bindValue( target, model );
  }

  private FileChooserDelegateOpen createFileChooserDelegate( final FilePattern pattern, final boolean optional )
  {
    final FileChooserDelegateOpen delegate = new FileChooserDelegateOpen( new String[] {}, new String[] {}, optional );
    delegate.addFilter( pattern );
    delegate.addFilter( FileExtensions.ALL_FILES );

    return delegate;
  }
}