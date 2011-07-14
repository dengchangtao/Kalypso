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
package org.kalypso.model.wspm.pdb.ui.internal.content;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.services.IServiceLocator;
import org.kalypso.contribs.eclipse.jface.viewers.ViewerUtilities;
import org.kalypso.contribs.eclipse.jface.viewers.table.ColumnsResizeControlListener;
import org.kalypso.contribs.eclipse.swt.widgets.ColumnViewerSorter;
import org.kalypso.contribs.eclipse.swt.widgets.ControlUtils;
import org.kalypso.model.wspm.pdb.connect.IPdbConnection;
import org.kalypso.model.wspm.pdb.ui.internal.IWaterBodyStructure;
import org.kalypso.model.wspm.pdb.ui.internal.admin.state.StatesViewer;
import org.kalypso.model.wspm.pdb.ui.internal.admin.waterbody.WaterBodyViewer;
import org.kalypso.model.wspm.pdb.ui.internal.wspm.PdbWspmProject;

/**
 * @author Gernot Belger
 */
public class ConnectionContentControl extends Composite
{
  private static final String TOOLBAR_URI = "toolbar:org.kalypso.model.wspm.pdb.ui.content"; //$NON-NLS-1$

  private final IJobChangeListener m_refreshJobListener = new JobChangeAdapter()
  {
    @Override
    public void done( final IJobChangeEvent event )
    {
      handleRefreshDone();
    }
  };

  private final RefreshContentJob m_refreshJob;

  private TreeViewer m_viewer;

  private final ToolBarManager m_manager;

  private final PdbWspmProject m_project;

  private ColumnsResizeControlListener m_treeListener;

  private final IServiceLocator m_serviceLocator;

  public ConnectionContentControl( final IServiceLocator serviceLocator, final FormToolkit toolkit, final Section parent, final IPdbConnection connection, final PdbWspmProject project )
  {
    super( parent, SWT.NONE );

    m_serviceLocator = serviceLocator;

    m_project = project;

    m_refreshJob = new RefreshContentJob( connection );
    m_refreshJob.setSystem( true );
    m_refreshJob.addJobChangeListener( m_refreshJobListener );

    GridLayoutFactory.fillDefaults().spacing( 0, 0 ).applyTo( this );
    toolkit.adapt( this );

    ControlUtils.addDisposeListener( this );

    m_manager = new ToolBarManager( SWT.FLAT | SWT.SHADOW_OUT );

    createToolbar( toolkit, this ).setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    createTree( toolkit, this ).setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    createActions();

    refresh( null );
  }

  @Override
  public void dispose( )
  {
    setInput( null );

    final IMenuService service = (IMenuService) m_serviceLocator.getService( IMenuService.class );
    service.releaseContributions( m_manager );
    m_manager.dispose();

    super.dispose();
  }

  private Control createToolbar( final FormToolkit toolkit, final Composite parent )
  {
    final ToolBar toolBar = m_manager.createControl( parent );
    toolkit.adapt( toolBar );
    return toolBar;
  }

  private Control createTree( final FormToolkit toolkit, final Composite parent )
  {
    final Tree tree = toolkit.createTree( parent, SWT.FULL_SELECTION | SWT.MULTI );
    tree.setHeaderVisible( true );
    m_viewer = new TreeViewer( tree );
    m_viewer.setUseHashlookup( true );
    m_viewer.setContentProvider( new ByWaterBodyContentProvider() );
    m_viewer.setInput( PdbLabelProvider.PENDING );

    final ViewerColumn nameColumn = StatesViewer.createNameColumn( m_viewer );
    WaterBodyViewer.createNameColumn( m_viewer );
    StatesViewer.createMeasurementDateColumn( m_viewer );

    ColumnViewerSorter.setSortState( nameColumn, false );

    m_treeListener = new ColumnsResizeControlListener();
    tree.addControlListener( m_treeListener );

    m_viewer.addSelectionChangedListener( new ISelectionChangedListener()
    {
      @Override
      public void selectionChanged( final SelectionChangedEvent event )
      {
        handleSelectionChanged( (IStructuredSelection) event.getSelection() );
      }
    } );

    tree.addTreeListener( new TreeListener()
    {
      @Override
      public void treeExpanded( final TreeEvent e )
      {
        refreshColumnSizes();
      }

      @Override
      public void treeCollapsed( final TreeEvent e )
      {
        refreshColumnSizes();
      }
    } );

    return tree;
  }

  protected void handleSelectionChanged( final IStructuredSelection selection )
  {
    /* Preserve selection from user, i.e. if the selection is changed during refresh, use that new selection */
    final ElementSelector selector = new ElementSelector();
    selector.setElemensToSelect( selection.toArray() );
    m_refreshJob.setElementToSelect( selector );
  }

  private void createActions( )
  {
    m_manager.add( new RefreshAction( this ) );
    m_manager.add( new ExpandAllAction( this ) );
    m_manager.add( new CollapseAllAction( this ) );
    m_manager.add( new Separator() );

    final IMenuService service = (IMenuService) m_serviceLocator.getService( IMenuService.class );
    service.populateContributionManager( m_manager, TOOLBAR_URI );

    m_manager.update( true );
  }

  public void refresh( final ElementSelector elementToSelect )
  {
    // TODO: it would also be nice to directly update any changed elements
    // Hm, maybe we should directly always work on the current state?

    applySelection( elementToSelect );

    m_refreshJob.cancel();

    final Font italicFont = JFaceResources.getFontRegistry().getItalic( JFaceResources.DIALOG_FONT );
    m_viewer.getControl().setFont( italicFont );

    // m_refreshJob.setElementToSelect( elementToSelect );
    m_refreshJob.schedule( 500 );
  }

  protected synchronized void setInput( final Object input )
  {
    final Object oldInput = m_viewer.getInput();
    if( oldInput instanceof ConnectionInput )
      ((ConnectionInput) oldInput).dispose();

    m_viewer.setInput( input );
    refreshColumnSizes();
  }

  protected void handleRefreshDone( )
  {
    final ConnectionInput input = m_refreshJob.getInput();
    final ElementSelector selector = m_refreshJob.getElementToSelect();

    final TreeViewer viewer = m_viewer;
    final Runnable operation = new Runnable()
    {
      @Override
      public void run( )
      {
        setInput( input );

        final Font normalFont = JFaceResources.getFontRegistry().get( JFaceResources.DIALOG_FONT );
        viewer.getControl().setFont( normalFont );

        applySelection( selector );
      }
    };
    ViewerUtilities.execute( m_viewer, operation, true );
  }

  protected void applySelection( final ElementSelector selector )
  {
    final Object input = m_viewer.getInput();
    if( !(input instanceof ConnectionInput) )
      return;

    final Object[] element = selector == null ? ArrayUtils.EMPTY_OBJECT_ARRAY : selector.getElements( (ConnectionInput) input );
    if( element != null )
      ViewerUtilities.setSelection( m_viewer, new StructuredSelection( element ), true, true );

    refreshColumnSizes();
  }

  public IStructuredSelection getSelection( )
  {
    return (IStructuredSelection) m_viewer.getSelection();
  }

  public PdbWspmProject getProject( )
  {
    return m_project;
  }

  protected void refreshColumnSizes( )
  {
    m_treeListener.updateColumnSizes();
  }

  public TreeViewer getTreeViewer( )
  {
    return m_viewer;
  }

  public IWaterBodyStructure getStructure( )
  {
    if( m_viewer == null )
      return null;

    final ConnectionInput input = (ConnectionInput) m_viewer.getInput();
    if( input == null )
      return null;

    return input.getStructure();
  }
}