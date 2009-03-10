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
package org.kalypso.ogc.gml.featureview.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.services.IServiceLocator;
import org.kalypso.contribs.eclipse.jface.viewers.DefaultTableViewer;
import org.kalypso.contribs.eclipse.swt.custom.ExcelTableCursor;
import org.kalypso.contribs.eclipse.swt.custom.ExcelTableCursor.ADVANCE_MODE;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypso.observation.IObservation;
import org.kalypso.observation.result.IComponent;
import org.kalypso.observation.result.IRecord;
import org.kalypso.observation.result.ITupleResultChangedListener;
import org.kalypso.observation.result.TupleResult;
import org.kalypso.ogc.gml.command.ChangeFeatureCommand;
import org.kalypso.ogc.gml.om.ObservationFeatureFactory;
import org.kalypso.ogc.gml.om.table.TupleResultCellModifier;
import org.kalypso.ogc.gml.om.table.TupleResultContentProvider;
import org.kalypso.ogc.gml.om.table.TupleResultLabelProvider;
import org.kalypso.ogc.gml.om.table.command.TupleResultCommandUtils;
import org.kalypso.ogc.gml.om.table.handlers.IComponentUiHandlerProvider;
import org.kalypso.template.featureview.ColumnDescriptor;
import org.kalypso.template.featureview.TupleResult.Toolbar;
import org.kalypso.ui.KalypsoUIExtensions;
import org.kalypso.util.swt.SWTUtilities;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree_impl.model.feature.FeatureHelper;

/**
 * @author Gernot Belger
 */
public class TupleResultFeatureControl extends AbstractFeatureControl implements ITupleResultChangedListener
{
  private final List<ModifyListener> m_listener = new ArrayList<ModifyListener>( 10 );

  private final ToolBarManager m_toolbar;

  private final Set<String> m_commands = new HashSet<String>();

  private final IComponentUiHandlerProvider m_handlerProvider;

  private DefaultTableViewer m_viewer;

  private ViewerFilter m_viewerFilter;

  private TupleResultContentProvider m_tupleResultContentProvider;

  private TupleResultLabelProvider m_tupleResultLabelProvider;

  private TupleResult m_tupleResult;

  /** TRICK: in order to suppress refresh after our own changes we set this flag. */
  private int m_ignoreNextUpdateControl = 0;

  private IExecutionListener m_executionListener;

  private final boolean m_recordsFixed;

  private ExcelTableCursor m_cursor;

  private ControlEditor m_controlEditor;

  public TupleResultFeatureControl( final Feature feature, final IPropertyType ftp, final IComponentUiHandlerProvider handlerProvider, final boolean showToolbar, final boolean recordsFixed )
  {
    super( feature, ftp );

    m_handlerProvider = handlerProvider;
    m_recordsFixed = recordsFixed;

    if( showToolbar )
      m_toolbar = new ToolBarManager( SWT.HORIZONTAL | SWT.FLAT );
    else
      m_toolbar = null;
  }

  public void addToolbarItem( final String commandId, final int style )
  {
    final IServiceLocator serviceLocator = PlatformUI.getWorkbench();

    if( m_toolbar != null )
    {
      m_toolbar.add( new CommandContributionItem( serviceLocator, commandId + "_item", commandId, new HashMap<Object, Object>(), null, null, null, null, null, null, style ) ); //$NON-NLS-1$
      m_toolbar.update( true );
    }
    m_commands.add( commandId );
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.control.IFeatureControl#createControl(org.eclipse.swt.widgets.Composite, int)
   */
  public Control createControl( final Composite parent, final int style )
  {
    final Composite composite = new Composite( parent, style );
    final GridLayout compLayout = new GridLayout();
    compLayout.marginHeight = 0;
    compLayout.marginWidth = 0;
    composite.setLayout( compLayout );

    if( m_toolbar != null )
      m_toolbar.createControl( composite );

    m_viewer = new TupleResultTableViewer( composite, style ); // TODO and not SWT.BORDER delete border style here...

    // dem Editor beibringen, nur dann eine Zelle zu editieren, wenn der EditMode aktiviert ist
    ColumnViewerEditorActivationStrategy eas = new ColumnViewerEditorActivationStrategy( m_viewer )
    {

      /**
       * @see org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy#isEditorActivationEvent(org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent)
       */
      @Override
      protected boolean isEditorActivationEvent( ColumnViewerEditorActivationEvent event )
      {
        if( !isEditMode() )
          return false;
        return super.isEditorActivationEvent( event );
      }

    };

    TableViewerEditor.create( m_viewer, eas, ColumnViewerEditor.DEFAULT );

    final Table table = m_viewer.getTable();
    table.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

    m_tupleResultContentProvider = new TupleResultContentProvider( m_handlerProvider );
    m_tupleResultLabelProvider = new TupleResultLabelProvider( m_tupleResultContentProvider );

    if( m_viewerFilter != null )
      m_viewer.addFilter( m_viewerFilter );

    final ICellModifier tupleResultCellModifier = new TupleResultCellModifier( m_tupleResultContentProvider );

    m_viewer.setContentProvider( m_tupleResultContentProvider );
    m_viewer.setLabelProvider( m_tupleResultLabelProvider );

    m_viewer.setCellModifier( tupleResultCellModifier );
    m_viewer.setInput( null );

    updateControl();

    if( m_toolbar != null )
      hookExecutionListener( m_commands, m_viewer, m_toolbar );

    if( isEditMode() )
    {

      m_cursor = new ExcelTableCursor( m_viewer, SWT.BORDER_DASH, ADVANCE_MODE.DOWN, true );
      m_controlEditor = new ControlEditor( m_cursor );
      m_controlEditor.grabHorizontal = true;
      m_controlEditor.grabVertical = true;

      m_cursor.setVisible( true );
      m_cursor.setEnabled( true );
      // m_viewer.getColumnViewerEditor().addEditorActivationListener( m_cveal );
    }

    return composite;
  }

  private void hookExecutionListener( final Set<String> commands, final TableViewer tableViewer, final ToolBarManager toolBar )
  {
    final IWorkbench serviceLocator = PlatformUI.getWorkbench();
    final ICommandService cmdService = (ICommandService) serviceLocator.getService( ICommandService.class );
    final IHandlerService handlerService = (IHandlerService) serviceLocator.getService( IHandlerService.class );
    final TupleResultFeatureControl trfc = this;

    m_executionListener = new IExecutionListener()
    {
      public void notHandled( final String commandId, final NotHandledException exception )
      {
      }

      public void preExecute( final String commandId, final ExecutionEvent event )
      {
        if( !commands.contains( commandId ) )
          return;

        final Event trigger = (Event) event.getTrigger();
        final ToolItem toolItem = (ToolItem) trigger.widget;
        final ToolBar parentToolbar = toolItem.getParent();
        final ToolBar managerToolbar = toolBar.getControl();

        if( commands.contains( commandId ) && (parentToolbar == managerToolbar) )
        {
          final IEvaluationContext context = (IEvaluationContext) event.getApplicationContext();
          context.addVariable( TupleResultCommandUtils.ACTIVE_TUPLE_RESULT_TABLE_VIEWER_NAME, tableViewer );
          context.addVariable( TupleResultCommandUtils.ACTIVE_TUPLE_RESULT_FEATURE_CONTROL_NAME, trfc );
        }
      }

      public void postExecuteFailure( final String commandId, final ExecutionException exception )
      {
        if( !commands.contains( commandId ) )
          return;

        final IEvaluationContext currentState = handlerService.getCurrentState();
        currentState.removeVariable( TupleResultCommandUtils.ACTIVE_TUPLE_RESULT_TABLE_VIEWER_NAME );
        currentState.removeVariable( TupleResultCommandUtils.ACTIVE_TUPLE_RESULT_FEATURE_CONTROL_NAME );

        // REMARK: it would be nice to have an error mesage here, but:
        // If we have several tabs, we get several msg-boxes, as we have several listeners.
        // How-to avoid that??
        // final IStatus errorStatus = StatusUtilities.createStatus( IStatus.ERROR, "Kommando mit Fehler beendet",
        // exception );
        // ErrorDialog.openError( getShell(), "Kommando ausführen", "Fehler bei der Ausführung eines Kommandos",
        // errorStatus );
      }

      public void postExecuteSuccess( final String commandId, final Object returnValue )
      {
        if( !commands.contains( commandId ) )
          return;

        final IEvaluationContext currentState = handlerService.getCurrentState();
        currentState.removeVariable( TupleResultCommandUtils.ACTIVE_TUPLE_RESULT_TABLE_VIEWER_NAME );
        currentState.removeVariable( TupleResultCommandUtils.ACTIVE_TUPLE_RESULT_FEATURE_CONTROL_NAME );
      }
    };

    cmdService.addExecutionListener( m_executionListener );
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.control.AbstractFeatureControl#dispose()
   */
  @Override
  public void dispose( )
  {
    if( m_executionListener != null )
    {
      final IWorkbench serviceLocator = PlatformUI.getWorkbench();
      final ICommandService cmdService = (ICommandService) serviceLocator.getService( ICommandService.class );
      cmdService.removeExecutionListener( m_executionListener );
    }

    m_tupleResultContentProvider.dispose();
    m_tupleResultLabelProvider.dispose();

    if( m_tupleResult != null )
    {
      m_tupleResult.removeChangeListener( this );
      m_tupleResult = null;
    }

    if( m_toolbar != null )
    {
      m_toolbar.dispose();
      m_toolbar.removeAll();
    }

    super.dispose();
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.control.IFeatureControl#updateControl()
   */
  public void updateControl( )
  {
    if( m_ignoreNextUpdateControl > 0 )
    {
      m_ignoreNextUpdateControl--;
      return;
    }

    final Feature feature = getObservationFeature();
    if( m_tupleResult != null )
      m_tupleResult.removeChangeListener( this );

    final IObservation<TupleResult> obs = feature == null ? null : ObservationFeatureFactory.toObservation( feature );
    m_tupleResult = obs == null ? null : obs.getResult();

    if( m_tupleResult != null )
      m_tupleResult.addChangeListener( this );

    m_viewer.setInput( m_tupleResult );
  }

  /**
   * Returns the observation.
   * <p>
   * If the given property is a relation type, get the feature from that property, else directly use the given feature
   * of this control.
   * </p>
   */
  private Feature getObservationFeature( )
  {
    final Feature feature = getFeature();
    final IPropertyType ftp = getFeatureTypeProperty();

    return getObservationFeature( feature, ftp );
  }

  static Feature getObservationFeature( final Feature feature, final IPropertyType ftp )
  {
    if( ftp instanceof IRelationType )
    {
      final Object property = feature.getProperty( ftp );
      return FeatureHelper.getFeature( feature.getWorkspace(), property );
    }

    return feature;
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.control.IFeatureControl#isValid()
   */
  public boolean isValid( )
  {
    return true;
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.control.IFeatureControl#addModifyListener(org.eclipse.swt.events.ModifyListener)
   */
  public void addModifyListener( final ModifyListener l )
  {
    m_listener.add( l );
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.control.IFeatureControl#removeModifyListener(org.eclipse.swt.events.ModifyListener)
   */
  public void removeModifyListener( final ModifyListener l )
  {
    m_listener.remove( l );
  }

  /**
   * @see org.kalypso.observation.result.ITupleResultChangedListener#valuesChanged(org.kalypso.observation.result.ITupleResultChangedListener.ValueChange[])
   */
  public void valuesChanged( final ValueChange[] changes )
  {
    fireChanges( false );
    // fireModified();
  }

  /**
   * @see org.kalypso.observation.result.ITupleResultChangedListener#recordsChanged(org.kalypso.observation.result.IRecord[],
   *      org.kalypso.observation.result.ITupleResultChangedListener.TYPE)
   */
  public void recordsChanged( final IRecord[] records, final TYPE type )
  {
    fireChanges( false );
    // fireModified();
  }

  /**
   * @see org.kalypso.observation.result.ITupleResultChangedListener#componentsChanged(org.kalypso.observation.result.IComponent[],
   *      org.kalypso.observation.result.ITupleResultChangedListener.TYPE)
   */
  public void componentsChanged( final IComponent[] components, final TYPE type )
  {
    fireChanges( true );
  }

  private void fireChanges( final boolean definitionChanged )
  {
    final Feature obsFeature = getObservationFeature();
    final IFeatureType obsFT = obsFeature.getFeatureType();
    final IRelationType resultDefPT = (IRelationType) obsFT.getProperty( ObservationFeatureFactory.OM_RESULTDEFINITION );
    final IPropertyType resultPT = obsFT.getProperty( ObservationFeatureFactory.OM_RESULT );

    final Feature rd = ObservationFeatureFactory.buildRecordDefinition( obsFeature, resultDefPT, m_tupleResult.getComponents(), m_tupleResult.getSortComponents(), m_tupleResult.getOrdinalNumberComponent() );

    final String strResult = ObservationFeatureFactory.serializeResultAsString( m_tupleResult );

    // PROBLEM: we have 2 changes, so we get entries to the undo queue here
    // TODO: refaktor so that we may send multiple changes at one go
    if( definitionChanged )
    {
      m_ignoreNextUpdateControl++;
      fireFeatureChange( new ChangeFeatureCommand( obsFeature, resultDefPT, rd ) );
    }

    m_ignoreNextUpdateControl++;
    fireFeatureChange( new ChangeFeatureCommand( obsFeature, resultPT, strResult ) );
  }

  /**
   * must be called before createControl() is called!
   */
  public void setViewerFilter( final ViewerFilter filter )
  {
    m_viewerFilter = filter;
  }

  private static IComponentUiHandlerProvider createHandler( final org.kalypso.template.featureview.TupleResult editorType )
  {
    final String columnProviderId = editorType.getColumnProviderId();

    final ColumnDescriptor[] descriptors = editorType.getColumnDescriptor().toArray( new ColumnDescriptor[] {} );

    if( descriptors.length == 0 )
      return KalypsoUIExtensions.createComponentUiHandlerProvider( columnProviderId );

    return new TupleResultFeatureControlHandlerProvider( descriptors );
  }

  public static TupleResultFeatureControl create( final org.kalypso.template.featureview.TupleResult editorType, final Feature feature, final IPropertyType ftp )
  {
    final IComponentUiHandlerProvider provider = createHandler( editorType );
    final Toolbar toolbar = editorType.getToolbar();
    final Boolean recordsFixed = editorType.isRecordsFixed();
    final boolean areRecordsFixed = recordsFixed == null ? false : recordsFixed.booleanValue();
    final TupleResultFeatureControl tfc = new TupleResultFeatureControl( feature, ftp, provider, toolbar != null, areRecordsFixed );

    if( toolbar == null )
      return tfc;

    final List<org.kalypso.template.featureview.TupleResult.Toolbar.Command> commands = toolbar.getCommand();
    for( final org.kalypso.template.featureview.TupleResult.Toolbar.Command command : commands )
    {
      final String commandId = command.getCommandId();
      final int style = SWTUtilities.createStyleFromString( command.getStyle() );
      tfc.addToolbarItem( commandId, style );
    }

    if( toolbar.isAddStandardItems() )
    {
      if( toolbar.isBtnAddRow() )
        tfc.addToolbarItem( "org.kalypso.ui.tupleResult.addRowCommand", SWT.PUSH ); //$NON-NLS-1$
      if( toolbar.isBtnDeleteSelectedRows() )
        tfc.addToolbarItem( "org.kalypso.ui.tupleResult.deleteSelectedRowsCommand", SWT.PUSH ); //$NON-NLS-1$
      if( toolbar.isBtnCopyToClipboard() )
        tfc.addToolbarItem( "org.kalypso.ui.tupleResult.copyToClipboardCommand", SWT.PUSH ); //$NON-NLS-1$
      if( toolbar.isBtnPasteFromClipboard() )
        tfc.addToolbarItem( "org.kalypso.ui.tupleResult.pasteFromClipboardCommand", SWT.PUSH ); //$NON-NLS-1$
      if( toolbar.isBtnInterpolateRows() )
        tfc.addToolbarItem( "org.kalypso.ui.tupleResult.interpolateSelectedRowsCommand", SWT.PUSH ); //$NON-NLS-1$
      if( toolbar.isBtnMoveRowsUp() )
        tfc.addToolbarItem( "org.kalypso.ui.tupleResult.moveUpSelectedRowsCommand", SWT.PUSH ); //$NON-NLS-1$
      if( toolbar.isBtnMoveRowsDown() )
        tfc.addToolbarItem( "org.kalypso.ui.tupleResult.moveDownSelectedRowsCommand", SWT.PUSH ); //$NON-NLS-1$
    }

    return tfc;
  }

  boolean isEditMode( )
  {
    /**
     * TODO check if table is editable
     */
    return true;
  }

}
