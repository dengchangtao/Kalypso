/*--------------- Kalypso-Header --------------------------------------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 Technical University Hamburg-Harburg (TUHH)
 Institute of River and coastal engineering
 Denickestr. 22
 21073 Hamburg, Germany
 http://www.tuhh.de/wb

 and

 Bjoernsen Consulting Engineers (BCE)
 Maria Trost 3
 56070 Koblenz, Germany
 http://www.bjoernsen.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 E-Mail:
 belger@bjoernsen.de
 schlienger@bjoernsen.de
 v.doemming@tuhh.de

 ---------------------------------------------------------------------------------------------------*/
package org.kalypso.ogc.gml.map.widgets.editrelation;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.kalypso.commons.command.ICommand;
import org.kalypso.commons.command.ICommandTarget;
import org.kalypso.commons.xml.NS;
import org.kalypso.contribs.eclipse.jface.viewers.tree.TreeViewerUtilities;
import org.kalypso.contribs.eclipse.jface.viewers.tree.TreeVisiterAbortException;
import org.kalypso.gmlschema.annotation.IAnnotation;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.i18n.Messages;
import org.kalypso.ogc.gml.IKalypsoFeatureTheme;
import org.kalypso.ogc.gml.IKalypsoTheme;
import org.kalypso.ogc.gml.command.JMSelector;
import org.kalypso.ogc.gml.map.IMapPanel;
import org.kalypso.ogc.gml.map.widgets.AbstractWidget;
import org.kalypso.ogc.gml.mapmodel.CommandableWorkspace;
import org.kalypso.ogc.gml.mapmodel.IMapModell;
import org.kalypso.ui.editor.gmleditor.util.command.AddHeavyRelationshipCommand;
import org.kalypso.ui.editor.gmleditor.util.command.AddRelationCommand;
import org.kalypso.ui.editor.gmleditor.util.command.RemoveHeavyRelationCommand;
import org.kalypso.ui.editor.gmleditor.util.command.RemoveRelationCommand;
import org.kalypso.ui.editor.mapeditor.views.IWidgetWithOptions;
import org.kalypsodeegree.graphics.transformation.GeoTransform;
import org.kalypsodeegree.model.feature.CascadingFeatureList;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureList;
import org.kalypsodeegree.model.feature.FindExistingHeavyRelationsFeatureVisitor;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree_impl.model.geometry.GeometryFactory;

/**
 * Widget where the user can create relations between selected features. only features from the workspace of the active
 * featuretheme can be selected <br>
 * Constraints from gml-application schemas are supported. TODO use check icons that indicate mixed child status <br>
 * TODO support removing relations <br>
 * 
 * @author doemming
 */
public class EditRelationWidget extends AbstractWidget implements IWidgetWithOptions
{
  final String[] m_modeItems = new String[] {
      Messages.getString( "org.kalypso.ogc.gml.map.widgets.editrelation.EditRelationWidget.0" ), Messages.getString( "org.kalypso.ogc.gml.map.widgets.editrelation.EditRelationWidget.1" ) }; //$NON-NLS-1$ //$NON-NLS-2$

  Feature m_srcFE = null;

  Feature m_targetFE = null;

  private FeatureList m_allowedFeatureList = null;

  private static final double RADIUS = 30;

  private TreeViewer m_viewer;

  final EditRelationOptionsContentProvider m_contentProvider = new EditRelationOptionsContentProvider();

  Composite m_topLevel;

  Text m_textInfo;

  Text m_textProblem;

  final StringBuffer m_fitProblems = new StringBuffer();

  private Combo m_modeCombo;

  public static final int MODE_ADD = 0;

  public static final int MODE_REMOVE = 1;

  int m_modificationMode = MODE_ADD;

  EditRelationOptionsLabelProvider m_labelProvider = null;

  public EditRelationWidget( final String name, final String toolTip )
  {
    super( name, toolTip );
  }

  /**
   * empty constructor so widget can be used with SelectWidgetHandler
   */
  public EditRelationWidget( )
  {
    super( Messages.getString( "org.kalypso.ogc.gml.map.widgets.editrelation.EditRelationWidget.2" ), "" ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Override
  public void leftPressed( final Point p )
  {
    if( m_srcFE != null && m_targetFE != null )
    {
      perform();
      finish();
      return;
    }
    m_targetFE = null;
    final JMSelector selector = new JMSelector();
    final IMapPanel mapPanel = getMapPanel();
    final GeoTransform transform = mapPanel.getProjection();
    final GM_Point point = GeometryFactory.createGM_Point( p, transform, mapPanel.getMapModell().getCoordinatesSystem() );

    final double r = transform.getSourceX( RADIUS ) - transform.getSourceX( 0 );

    final Feature feature = (Feature) selector.selectNearest( point, r, m_allowedFeatureList, false );
    m_srcFE = feature;
    m_fitProblems.setLength( 0 );
    updateProblemsText();
    updateInfoText();
  }

  /**
   * @see org.kalypso.ogc.gml.map.widgets.AbstractWidget#finish()
   */
  @Override
  public void finish( )
  {
    super.finish();
    m_srcFE = null;
    m_targetFE = null;
    m_fitProblems.setLength( 0 );
    updateProblemsText();
    updateInfoText();
  }

  /**
   * @return list of
   * @see RelationType that fit to the selected features
   */
  private List<IRelationType> getFitList( final Feature fromFE, final Feature toFE )
  {
    final List<IRelationType> fitList = new ArrayList<IRelationType>();
    final IKalypsoTheme activeTheme = getActiveTheme();

    if( fromFE == null || toFE == null || activeTheme == null || !(activeTheme instanceof IKalypsoFeatureTheme) )
      return fitList;
    final GMLWorkspace workspace = ((IKalypsoFeatureTheme) activeTheme).getWorkspace();
    final IRelationType[] relations = m_contentProvider.getCheckedRelations();
    for( final IRelationType relation : relations )
    {
      if( relation.fitsTypes( fromFE.getFeatureType(), toFE.getFeatureType() ) )
      {
        final String fitProblems = relation.getFitProblems( workspace, fromFE, toFE, getModificationMode() == MODE_ADD );
        if( fitProblems == null )
          fitList.add( relation );
        else
          m_fitProblems.append( fitProblems );
      }
    }
    if( fitList.isEmpty() && m_fitProblems.length() == 0 )
      m_fitProblems.append( Messages.getString( "org.kalypso.ogc.gml.map.widgets.editrelation.EditRelationWidget.4" ) ); //$NON-NLS-1$
    return fitList;
  }

  int getModificationMode( )
  {
    return m_modificationMode;
  }

  @Override
  public void dragged( final Point p )
  {
    moved( p );
    // TODO: check if this repaint is really necessary
    final IMapPanel panel = getMapPanel();
    if( panel != null )
      panel.repaintMap();

  }

  /**
   * @see org.kalypso.ogc.gml.map.widgets.AbstractWidget#moved(java.awt.Point)
   */
  @Override
  public void moved( final Point p )
  {
    super.moved( p );
    if( m_srcFE == null )
      return;
    final JMSelector selector = new JMSelector();
    final IMapPanel mapPanel = getMapPanel();
    final GeoTransform transform = mapPanel.getProjection();
    final GM_Point point = GeometryFactory.createGM_Point( p, transform, mapPanel.getMapModell().getCoordinatesSystem() );
    final double r = transform.getSourceX( RADIUS ) - transform.getSourceX( 0 );
    final Feature feature = (Feature) selector.selectNearest( point, r, m_allowedFeatureList, false );
    m_fitProblems.setLength( 0 );
    m_targetFE = null;
    if( m_srcFE == feature )
      m_fitProblems.append( Messages.getString( "org.kalypso.ogc.gml.map.widgets.editrelation.EditRelationWidget.5" ) ); //$NON-NLS-1$
    else
    {
      if( !getFitList( m_srcFE, feature ).isEmpty() )
        m_targetFE = feature;
    }
    updateInfoText();
    updateProblemsText();

// TODO: check if this repaint is necessary for the widget
    final IMapPanel panel = getMapPanel();
    if( panel != null )
      panel.repaintMap();
  }

  @Override
  public void leftReleased( final Point p )
  {
    perform();
    finish();
  }

  /**
   * @see org.kalypso.ogc.gml.map.widgets.AbstractWidget#rightClicked(java.awt.Point)
   */
  @Override
  public void rightClicked( final Point p )
  {
    finish();
  }

  @Override
  public void paint( final Graphics g )
  {
    if( m_srcFE == null || m_targetFE == null )
      return;
    final GM_Object fromGeom = m_srcFE.getDefaultGeometryProperty();
    final GM_Object toGeom = m_targetFE.getDefaultGeometryProperty();
    if( fromGeom == null || toGeom == null )
      return;
    final GM_Point fromCenter = fromGeom.getCentroid();
    final GM_Point toCenter = toGeom.getCentroid();
    final IMapPanel mapPanel = getMapPanel();
    final GeoTransform transform = mapPanel.getProjection();
    final int x1 = (int) transform.getDestX( fromCenter.getX() );
    final int y1 = (int) transform.getDestY( fromCenter.getY() );
    final int x2 = (int) transform.getDestX( toCenter.getX() );
    final int y2 = (int) transform.getDestY( toCenter.getY() );
    g.drawLine( x1, y1, x2, y2 );
  }

  private FeatureList getallowedFeatureList( )
  {
    final List<FeatureList> result = new ArrayList<FeatureList>();
    final IKalypsoTheme activeTheme = getActiveTheme();
    final IMapPanel mapPanel = getMapPanel();
    final IMapModell mapModell = mapPanel == null ? null : mapPanel.getMapModell();
    if( mapModell == null || activeTheme == null || !(activeTheme instanceof IKalypsoFeatureTheme) )
      return new CascadingFeatureList( result.toArray( new FeatureList[result.size()] ) );

    final IKalypsoFeatureTheme activeFeatureTheme = (IKalypsoFeatureTheme) activeTheme;
    final GMLWorkspace workspace = activeFeatureTheme.getWorkspace();
    final IKalypsoTheme[] allThemes = mapModell.getAllThemes();
    for( final IKalypsoTheme element : allThemes )
    {
      if( element != null && element instanceof IKalypsoFeatureTheme )
      {
        final IKalypsoFeatureTheme kalypsoFeatureTheme = (IKalypsoFeatureTheme) element;
        if( kalypsoFeatureTheme.getWorkspace() == workspace )
          result.add( (kalypsoFeatureTheme).getFeatureList() );
      }
    }
    return new CascadingFeatureList( result.toArray( new FeatureList[result.size()] ) );
  }

  private void refreshSettings( )
  {
    m_allowedFeatureList = getallowedFeatureList();
    final TreeViewer viewer = m_viewer;
    if( viewer != null && !viewer.getControl().isDisposed() )
    {
      final IKalypsoTheme activeTheme = getActiveTheme();
      if( viewer.getInput() != activeTheme )
        m_viewer.getControl().getDisplay().asyncExec( new Runnable()
        {
          public void run( )
          {
            if( viewer != null && !viewer.getControl().isDisposed() )
              viewer.setInput( activeTheme );
          }
        } );
    }
  }

  /**
   * @see org.kalypso.ogc.gml.widgets.IWidget#perform()
   */
  public synchronized void perform( )
  {
    final Feature srcFeature = m_srcFE;
    final Feature targetFeature = m_targetFE;
    if( srcFeature == null || targetFeature == null )
      return;
    final List<IRelationType> fitList = getFitList( srcFeature, targetFeature );
    m_topLevel.getDisplay().asyncExec( new Runnable()
    {
      public void run( )
      {
        for( final IRelationType element : fitList )
        {
          System.out.println( element.toString() );
        }
        // TODO handle fitList.size()>1 with dialog
        if( fitList.size() < 1 )
          return;
        final IRelationType relation;
        if( fitList.size() == 1 )
        {
          relation = fitList.get( 0 );
        }
        else
        {
          final IStructuredContentProvider cProvider = new IStructuredContentProvider()
          {
            public Object[] getElements( final Object inputElement )
            {
              if( inputElement instanceof List )
              {
                return ((List< ? >) inputElement).toArray();
              }
              return null;
            }

            public void dispose( )
            {
              // m_input = null;
            }

            public void inputChanged( final Viewer viewer, final Object oldInput, final Object newInput )
            {
              // m_input = newInput;
            }
          };

          final ListSelectionDialog dialog = new ListSelectionDialog( m_topLevel.getShell(), fitList, cProvider, m_labelProvider, fitList.size()
              + Messages.getString( "org.kalypso.ogc.gml.map.widgets.editrelation.EditRelationWidget.6" ) ); //$NON-NLS-1$
          dialog.setInitialSelections( new Object[] { fitList.get( 0 ) } );
          dialog.setBlockOnOpen( true );
          boolean correct = false;
          Object[] result = null;
          while( !correct )
          {
            final int answer = dialog.open();
            if( answer == Window.CANCEL )
              return;
            result = dialog.getResult();
            correct = result.length == 1;
          }
          relation = (IRelationType) result[0];
        }

        final CommandableWorkspace workspace = ((IKalypsoFeatureTheme) getActiveTheme()).getWorkspace();
        final ICommand command;
        switch( getModificationMode() )
        {
          case MODE_ADD:
            if( relation instanceof HeavyRelationType )
            {
              final HeavyRelationType heavyRealtion = (HeavyRelationType) relation;

              command = new AddHeavyRelationshipCommand( workspace, srcFeature, heavyRealtion.getLink1(), heavyRealtion.getBodyFT(), heavyRealtion.getLink2(), targetFeature );
            }
            else
            {
              final RelationType normalRelation = (RelationType) relation;
              command = new AddRelationCommand( srcFeature, normalRelation.getLink(), 0, targetFeature );
            }
            break;
          case MODE_REMOVE:
            if( relation instanceof HeavyRelationType )
            {
              final HeavyRelationType heavyRealtion = (HeavyRelationType) relation;

              final FindExistingHeavyRelationsFeatureVisitor visitor = new FindExistingHeavyRelationsFeatureVisitor( workspace, heavyRealtion );
              visitor.visit( srcFeature );
              final Feature[] bodyFeatureFor = visitor.getBodyFeatureFor( targetFeature );
              if( bodyFeatureFor.length > 0 )
                command = new RemoveHeavyRelationCommand( workspace, srcFeature, heavyRealtion.getLink1(), bodyFeatureFor[0], heavyRealtion.getLink2(), targetFeature );
              else
                command = null;
            }
            else
            {
              final RelationType normalRelation = (RelationType) relation;
              command = new RemoveRelationCommand( srcFeature, normalRelation.getLink(), targetFeature );
            }
            break;
          default:
            command = null;
        }
        try
        {
          workspace.postCommand( command );
        }
        catch( final Exception e )
        {
          e.printStackTrace();
        }
      }
    } );
  }

  private void updateProblemsText( )
  {
    if( m_textProblem != null && !m_textProblem.isDisposed() )
    {
      m_textProblem.getDisplay().asyncExec( new Runnable()
      {
        public void run( )
        {
          final String problems = m_fitProblems.toString();
          if( m_textProblem != null && !m_textProblem.isDisposed() )
          {
            if( problems.length() == 0 )
              m_textProblem.setText( "" ); //$NON-NLS-1$
            else
              m_textProblem.setText( problems );
            m_topLevel.layout();
          }
        }
      } );
    }

  }

  void updateInfoText( )
  {
    final StringBuffer labelBuffer = new StringBuffer();
    final StringBuffer tipBuffer = new StringBuffer();
    labelBuffer.append( Messages.getString( "org.kalypso.ogc.gml.map.widgets.editrelation.EditRelationWidget.8" ) ); //$NON-NLS-1$
    labelBuffer.append( Messages.getString( "org.kalypso.ogc.gml.map.widgets.editrelation.EditRelationWidget.9" ) ); //$NON-NLS-1$
    tipBuffer.append( Messages.getString( "org.kalypso.ogc.gml.map.widgets.editrelation.EditRelationWidget.10" ) ); //$NON-NLS-1$
    tipBuffer.append( Messages.getString( "org.kalypso.ogc.gml.map.widgets.editrelation.EditRelationWidget.11" ) ); //$NON-NLS-1$

    if( m_srcFE == null )
    {
      labelBuffer.append( "<select>" ); //$NON-NLS-1$
      tipBuffer.append( "<select>" ); //$NON-NLS-1$
    }
    else
    {
      final IFeatureType ft = m_srcFE.getFeatureType();
      final IAnnotation annotation = ft.getAnnotation();
      labelBuffer.append( annotation.getTooltip() + "#" + m_srcFE.getProperty( new QName( NS.GML2, "name" ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
      tipBuffer.append( ft.getQName() + "#" + m_srcFE.getId() ); //$NON-NLS-1$
    }
    labelBuffer.append( Messages.getString( "org.kalypso.ogc.gml.map.widgets.editrelation.EditRelationWidget.17" ) ); //$NON-NLS-1$
    tipBuffer.append( Messages.getString( "org.kalypso.ogc.gml.map.widgets.editrelation.EditRelationWidget.18" ) ); //$NON-NLS-1$
    if( m_targetFE == null )
    {
      labelBuffer.append( "<select>" ); //$NON-NLS-1$
      tipBuffer.append( "<select>" ); //$NON-NLS-1$
    }
    else
    {
      final IFeatureType ft = m_targetFE.getFeatureType();
      final IAnnotation annotation = ft.getAnnotation();
      labelBuffer.append( annotation.getTooltip() + "#" + m_targetFE.getProperty( new QName( NS.GML2, "name" ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
      tipBuffer.append( ft.getQName() + "#" + m_targetFE.getId() ); //$NON-NLS-1$
    }
    if( m_textInfo != null && !m_textInfo.isDisposed() )
    {
      m_textInfo.getDisplay().asyncExec( new Runnable()
      {
        public void run( )
        {
          if( m_textInfo != null && !m_textInfo.isDisposed() )
          {
            m_textInfo.setText( labelBuffer.toString() );
            m_textInfo.setToolTipText( tipBuffer.toString() );
            m_topLevel.layout();
          }
        }
      } );
    }
    // Andreas: das hatte keine Auswirkungen (mehr). Weg?
    // if( m_srcFE == null )
    // {
    // setLeftMFunction( "Quelle w�hlen" );
    // setRightMFunction( null );
    // }
    // // src != null && m_targetFE==null
    // else if( m_targetFE == null )
    // {
    // setLeftMFunction( "Ziel w�hlen" );
    // setRightMFunction( "Auswahl aufheben" );
    // }
    // // src != null && m_targetFE!=null
    // else
    // {
    // setLeftMFunction( "Relation anlegen" );
    // setRightMFunction( "Auswahl aufheben" );
    // }
  }

  /**
   * @see org.kalypso.ui.editor.mapeditor.views.IWidgetWithOptions#disposeControl()
   */
  public void disposeControl( )
  {
    if( m_modeCombo != null && !m_modeCombo.isDisposed() )
      m_modeCombo.dispose();
    if( m_topLevel != null && !m_topLevel.isDisposed() )
      m_topLevel.dispose();
    if( m_viewer != null && !m_viewer.getControl().isDisposed() )
    {
      m_viewer.getControl().dispose();
      m_viewer = null;
    }
  }

  /**
   * @see org.kalypso.ui.editor.mapeditor.views.IWidgetWithOptions#createControl(org.eclipse.swt.widgets.Composite)
   */
  public Control createControl( final Composite parent, final FormToolkit toolkit )
  {
    m_topLevel = toolkit.createComposite( parent, SWT.NONE );
    final Layout gridLayout = new GridLayout( 1, false );

    m_topLevel.setLayout( gridLayout );

    // tree
    final GridData data2 = new GridData();
    data2.horizontalAlignment = GridData.FILL;
    data2.verticalAlignment = GridData.FILL;
    data2.grabExcessHorizontalSpace = true;
    data2.grabExcessVerticalSpace = true;

    // m_viewer = new CheckboxTreeViewer( parent, SWT.FILL );
    final TreeViewer viewer = new TreeViewer( m_topLevel, SWT.FILL );
    m_viewer = viewer;
    final Control treeControl = viewer.getControl();
    toolkit.adapt( treeControl, true, true );
    treeControl.setLayoutData( data2 );
    viewer.setContentProvider( m_contentProvider );
    m_labelProvider = new EditRelationOptionsLabelProvider( m_contentProvider );
    viewer.setLabelProvider( m_labelProvider );

    // combo, "add relation" or "remove relation"
    final GridData data3 = new GridData();
    data3.horizontalAlignment = GridData.FILL;
    data3.verticalAlignment = GridData.FILL;
    data3.grabExcessHorizontalSpace = true;
    data3.grabExcessVerticalSpace = false;

    m_modeCombo = new Combo( m_topLevel, SWT.READ_ONLY );
    toolkit.adapt( m_modeCombo );
    m_modeCombo.setItems( m_modeItems );
    m_modeCombo.setText( m_modeItems[m_modificationMode] );
    m_modeCombo.setLayoutData( data3 );
    m_modeCombo.addModifyListener( new ModifyListener()
    {
      public void modifyText( final ModifyEvent e )
      {
        m_modificationMode = ((Combo) e.getSource()).getSelectionIndex();
        m_srcFE = null;
        m_targetFE = null;
        updateInfoText();
      }
    } );
    m_textInfo = toolkit.createText( m_topLevel, Messages.getString( "org.kalypso.ogc.gml.map.widgets.editrelation.EditRelationWidget.24" ), SWT.READ_ONLY | SWT.MULTI | SWT.BORDER | SWT.WRAP ); //$NON-NLS-1$
    m_textProblem = toolkit.createText( m_topLevel, Messages.getString( "org.kalypso.ogc.gml.map.widgets.editrelation.EditRelationWidget.25" ), SWT.READ_ONLY | SWT.MULTI | SWT.WRAP ); //$NON-NLS-1$

    viewer.setAutoExpandLevel( 2 );
    viewer.getTree().addMouseListener( new MouseAdapter()
    {
      @Override
      public void mouseUp( final MouseEvent e )
      {
        final TreeItem item = viewer.getTree().getItem( new org.eclipse.swt.graphics.Point( e.x, e.y ) );
        if( item != null )
        {
          final Object element = item.getData();
          if( element != null )
          {
            final boolean status = m_contentProvider.isChecked( element );

            // Strange: this visitor is used for only one element. Is this really intended?
            try
            {
              TreeViewerUtilities.accept( m_contentProvider, element, new SetCheckedTreeVisitor( !status ) );
            }
            catch( final TreeVisiterAbortException ex )
            {
              // as only one element is visited, we just ignore it
            }

            viewer.refresh( element, true );
          }
        }
      }
    } );
    refreshSettings();

    return m_topLevel;
  }

  /**
   * @see org.kalypso.ogc.gml.map.widgets.AbstractWidget#activate(org.kalypso.commons.command.ICommandTarget,
   *      org.kalypso.ogc.gml.map.MapPanel)
   */
  @Override
  public void activate( final ICommandTarget commandPoster, final IMapPanel mapPanel )
  {
    super.activate( commandPoster, mapPanel );

    refreshSettings();
  }

  /**
   * @see org.kalypso.ui.editor.mapeditor.views.IWidgetWithOptions#getPartName()
   */
  @Override
  public String getPartName( )
  {
    return null;
  }
}