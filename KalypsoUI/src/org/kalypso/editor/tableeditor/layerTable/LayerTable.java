package org.kalypso.editor.tableeditor.layerTable;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.deegree.model.feature.FeatureType;
import org.deegree.model.feature.FeatureTypeProperty;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.kalypso.eclipse.jface.viewers.ICellEditorFactory;
import org.kalypso.eclipse.swt.custom.ExcelLikeTableCursor;
import org.kalypso.editor.tableeditor.actions.ColumnAction;
import org.kalypso.ogc.event.ModellEvent;
import org.kalypso.ogc.event.ModellEventListener;
import org.kalypso.ogc.gml.KalypsoFeature;
import org.kalypso.util.command.ICommandTarget;
import org.kalypso.util.factory.FactoryException;

/**
 * @author bce
 */
public class LayerTable implements ILayerTableModelListener, ISelectionProvider,
    ModellEventListener
{
  private static Logger LOGGER = Logger.getLogger( LayerTable.class.getName() );

  protected final TableViewer m_viewer;

  private LayerTableModel m_model = null;

  private final ICommandTarget m_columnCommandTarget;

  private final ICommandTarget m_layerCommandTarget;

  private IMenuManager m_menu;

  private Runnable m_refreshRunner = new Runnable()
  {
    public void run()
    {
      m_viewer.refresh( /* row */);
    }
  };

  private final ICellEditorFactory m_cellEditorFactory;

  private Map m_ftp2ColumnMap = new HashMap();

  private IMenuManager m_spaltenMenu;



  public LayerTable( final Composite parent, final ICommandTarget columnCommandTarget, final ICommandTarget layerCommandTarget,
      final ICellEditorFactory cellEditorFactory )
  {
    m_columnCommandTarget = columnCommandTarget;
    m_layerCommandTarget = layerCommandTarget;
    m_cellEditorFactory = cellEditorFactory;

    m_viewer = new TableViewer( parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION );

    m_viewer.setContentProvider( new LayerTableContentProvider() );
    m_viewer.setLabelProvider( new LayerTableLabelProvider( this ) );

    // init table
    
    final Table table = m_viewer.getTable();
    table.setHeaderVisible( true );
    table.setLinesVisible( true );
    
    new ExcelLikeTableCursor( m_viewer, SWT.NONE );
  }

  public void dispose()
  {
    if( m_model != null )
    {
      m_model.removeModelListener( this );
      m_model.getLayer().removeModellListener( this );
    }

    if( m_menu != null )
      m_menu.dispose();
    if( m_spaltenMenu != null )
      m_menu.dispose();
  }

  public LayerTableModel getModel()
  {
    return m_model;
  }

  public void setModel( final LayerTableModel model )
  {
    clearColumns();

    if( m_model != null )
    {
      m_model.removeModelListener( this );
      m_model.getLayer().addModellListener( this );
    }

    m_model = model;

    if( model != null )
    {
      m_model.addModelListener( this );
      m_model.getLayer().addModellListener( this );
    }

    final IAction[] actions = createActions();

    m_menu = refillMenu( m_menu, actions );
    m_spaltenMenu = refillMenu( m_spaltenMenu, actions );

    m_viewer.getTable().setMenu( ( (MenuManager)m_menu ).createContextMenu( m_viewer.getTable() ) );

    createColumns();
  }

  private IAction[] createActions()
  {
    if( m_model != null )
    {
      final FeatureTypeProperty[] ftps = m_model.getFeatureType().getProperties();

      final IAction[] actions = new IAction[ftps.length];

      for( int i = 0; i < ftps.length; i++ )
        actions[i] = new ColumnAction( m_columnCommandTarget, this, ftps[i], m_model
            .getInitialWidth( ftps[i] ) != 0 );

      return actions;
    }

    return new IAction[] {};
  }

  private IMenuManager refillMenu( final IMenuManager oldMenu, final IAction[] actions )
  {
    if( oldMenu != null )
    {
      oldMenu.removeAll();
      oldMenu.dispose();
    }

    final IMenuManager menu = new MenuManager( "Spalten" );

    // create context menu
    for( int i = 0; i < actions.length; i++ )
      menu.add( actions[i] );

    return menu;
  }

  protected void clearColumns()
  {
    m_ftp2ColumnMap.clear();

    final Table table = m_viewer.getTable();
    final TableColumn[] columns = table.getColumns();
    for( int i = 0; i < columns.length; i++ )
      columns[i].dispose();
  }

  protected void createColumns()
  {
    if( m_model == null )
      return;

    final Table table = m_viewer.getTable();

    final FeatureType featureType = m_model.getLayer().getFeatureType();
    final LayerTableModel.Column[] columns = m_model.getColumns();

    final String[] colProperties = new String[columns.length];
    final CellEditor[] cellEditors = new CellEditor[columns.length];
    for( int i = 0; i < columns.length; i++ )
    {
      final FeatureTypeProperty ftp = columns[i].ftp;
      if( ftp == null )
      {
        LOGGER.warning( "Column doesnt exist: " + i );
        continue;
      }
      
      final TableColumn tc = new TableColumn( table, SWT.CENTER );
      tc.setWidth( 100 );

      final String columnName = ftp.getName();
      if( columnName != null )
        tc.setText( columnName );
      tc.setData( ftp );
      tc.setWidth( m_model.getInitialWidth( ftp ) );

      colProperties[i] = columnName;

      m_ftp2ColumnMap.put( ftp, tc );

      try
      {
        if( m_model.isEditable( ftp ) )
          cellEditors[i] = m_cellEditorFactory.createEditor( ftp.getType(), table, SWT.NONE );
      }
      catch( final FactoryException e )
      {
        // ignore: Type not supported
        LOGGER.warning( "CellEditor not found for type: " + ftp.getType() );
      }
    }

    m_viewer.setColumnProperties( colProperties );
    m_viewer.setCellEditors( cellEditors );
    m_viewer.setInput( m_model );
    m_viewer.setCellModifier( new LayerTableCellModifier( m_layerCommandTarget, m_model, featureType ) );
  }

  /**
   * @see org.kalypso.editor.tableeditor.layerTable.ILayerTableModelListener#onColumnsChanged()
   */
  public void onColumnsChanged()
  {
    m_viewer.getControl().getDisplay().asyncExec( new Runnable()
    {
      public void run()
      {
        clearColumns();
        createColumns();
      }
    } );
  }

  /**
   * 
   * @see org.kalypso.editor.tableeditor.layerTable.ILayerTableModelListener#onRowsChanged(org.kalypso.ogc.gml.KalypsoFeature)
   */
  public void onRowsChanged( final KalypsoFeature fe )
  {
    m_viewer.getControl().getDisplay().asyncExec( m_refreshRunner );
  }

  public void selectRow( final KalypsoFeature feature )
  {
    m_viewer.getControl().getDisplay().asyncExec( new Runnable()
    {
      public void run()
      {
        m_viewer.setSelection( new StructuredSelection( feature ) );
      }
    } );
  }

  public ISelection getSelection()
  {
    return m_viewer == null ? null : (IStructuredSelection)m_viewer.getSelection();
  }

  /**
   * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
   */
  public void addSelectionChangedListener( ISelectionChangedListener listener )
  {
    m_viewer.addSelectionChangedListener( listener );
  }

  /**
   * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
   */
  public void removeSelectionChangedListener( ISelectionChangedListener listener )
  {
    m_viewer.removeSelectionChangedListener( listener );
  }

  /**
   * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
   */
  public void setSelection( final ISelection selection )
  {
    m_viewer.setSelection( selection );
  }

  /**
   * @see org.kalypso.ogc.event.ModellEventListener#onModellChange(org.kalypso.ogc.event.ModellEvent)
   */
  public void onModellChange( final ModellEvent modellEvent )
  {
    m_viewer.getControl().getDisplay().asyncExec( m_refreshRunner );
  }

  public int getWidth( final FeatureTypeProperty ftp )
  {
    final TableColumn tc = (TableColumn)m_ftp2ColumnMap.get( ftp );
    return tc.getWidth();
  }

  public IMenuManager getSpaltenMenu()
  {
    return m_spaltenMenu;
  }

  public FeatureTypeProperty getFeatureTypePropertyFromIndex( final int columnIndex )
  {
    if( m_viewer != null )
    {
      final Table table = m_viewer.getTable();
      if( columnIndex >= table.getColumnCount() )
        return null;
      
      return (FeatureTypeProperty)table.getColumn( columnIndex ).getData();
    }

    return null;
  }

  public boolean isDisposed()
  {
    return m_viewer.getTable().isDisposed();
  }
}