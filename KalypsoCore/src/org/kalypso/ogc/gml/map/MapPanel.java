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
package org.kalypso.ogc.gml.map;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.kalypso.commons.command.ICommandTarget;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypso.ogc.gml.IKalypsoFeatureTheme;
import org.kalypso.ogc.gml.IKalypsoTheme;
import org.kalypso.ogc.gml.KalypsoFeatureThemeSelection;
import org.kalypso.ogc.gml.command.JMSelector;
import org.kalypso.ogc.gml.mapmodel.IMapModell;
import org.kalypso.ogc.gml.mapmodel.IMapModellListener;
import org.kalypso.ogc.gml.mapmodel.MapModell;
import org.kalypso.ogc.gml.mapmodel.MapModellAdapter;
import org.kalypso.ogc.gml.mapmodel.MapModellHelper;
import org.kalypso.ogc.gml.selection.EasyFeatureWrapper;
import org.kalypso.ogc.gml.selection.IFeatureSelection;
import org.kalypso.ogc.gml.selection.IFeatureSelectionListener;
import org.kalypso.ogc.gml.selection.IFeatureSelectionManager;
import org.kalypso.ogc.gml.widgets.WidgetManager;
import org.kalypsodeegree.graphics.transformation.GeoTransform;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureList;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree.model.geometry.GM_Position;
import org.kalypsodeegree_impl.graphics.transformation.WorldToScreenTransform;
import org.kalypsodeegree_impl.model.geometry.GM_Envelope_Impl;
import org.kalypsodeegree_impl.model.geometry.GeometryFactory;
import org.opengis.cs.CS_CoordinateSystem;

/**
 * @author vdoemming
 */
public class MapPanel extends Canvas implements ComponentListener, ISelectionProvider
{
  // TODO: put this concept into a separate class, in preference an extra map layer
  public List<PointOfinterest> m_pointofInterests = new ArrayList<PointOfinterest>();

  public static final int MODE_SELECT = 0;

  public static final int MODE_TOGGLE = 1;

  public static final int MODE_UNSELECT = 2;

  private final IFeatureSelectionManager m_selectionManager;

  private final List<ISelectionChangedListener> m_selectionListeners = new ArrayList<ISelectionChangedListener>( 5 );

  private final IFeatureSelectionListener m_globalSelectionListener = new IFeatureSelectionListener()
  {
    public void selectionChanged( final IFeatureSelection selection )
    {
      globalSelectionChanged( selection );
    }
  };

  private static final long serialVersionUID = 1L;

  public final static String WIDGET_ZOOM_IN = "ZOOM_IN";

  public final static String WIDGET_ZOOM_IN_RECT = "ZOOM_IN_RECT";

  public final static String WIDGET_PAN = "PAN";

  public final static String WIDGET_EDIT_FEATURE = "EDIT_FEATURE_WITH_GEOMETRY";

  public final static String WIDGET_SELECT = "SELECT";

  public final static String WIDGET_EDIT_GEOMETRY = "EDIT_GEOMETRY";

  public final static String WIDGET_UNSELECT = "UNSELECT";

  public final static String WIDGET_TOGGLE_SELECT = "TOGGLE_SELECT";

  public final static String WIDGET_CREATE_FEATURE = "CREATE_FEATURE";

  public final static String WIDGET_CREATE_FEATURE_WITH_GEOMETRY = "CREATE_FEATURE_WITH_GEOMETRY";

  public final static String WIDGET_CREATE_FEATURE_WITH_POINT = "CREATE_FEATURE_WITH_POINT";

  public final static String WIDGET_EDIT_FEATURE_GEOMETRY = "WIDGET_EDIT_FEATURE_GEOMETRY";

  public final static String WIDGET_CREATE_FEATURE_WITH_LINESTRING = "CREATE_FEATURE_WITH_LINESTRING";

  public final static String WIDGET_CREATE_FEATURE_WITH_POLYGON = "CREATE_FEATURE_WITH_POLYGON";

  public static final String WIDGET_SINGLE_SELECT = "SINGLE_SELECT";

  private Image m_mapImage = null;

  private int xOffset = 0;

  private int yOffset = 0;

  private int m_width = 0;

  private int m_height = 0;

  private boolean validMap = false;

  private IMapModell m_model = null;

  private final WidgetManager m_widgetManager;

  private final GeoTransform m_projection = new WorldToScreenTransform();

  private GM_Envelope m_boundingBox = new GM_Envelope_Impl();

  private GM_Envelope m_wishBBox;

  private final List<IMapPanelListener> m_mapPanelListeners = new ArrayList<IMapPanelListener>();

  private String m_message = "";

  private final IMapModellListener m_modellListener = new MapModellAdapter()
  {
    /**
     * @see org.kalypso.ogc.gml.mapmodel.MapModellAdapter#themeAdded(org.kalypso.ogc.gml.mapmodel.IMapModell,
     *      org.kalypso.ogc.gml.IKalypsoTheme)
     */
    @Override
    public void themeAdded( final IMapModell source, final IKalypsoTheme theme )
    {
      if( theme.isVisible() )
        forceRepaint();
    }

    /**
     * @see org.kalypso.ogc.gml.mapmodel.MapModellAdapter#themeRemoved(org.kalypso.ogc.gml.mapmodel.IMapModell,
     *      org.kalypso.ogc.gml.IKalypsoTheme)
     */
    @Override
    public void themeRemoved( final IMapModell source, final IKalypsoTheme theme, boolean lastVisibility )
    {
      if( lastVisibility )
        forceRepaint();
    }

    /**
     * @see org.kalypso.ogc.gml.mapmodel.MapModellAdapter#themeOrderChanged(org.kalypso.ogc.gml.mapmodel.IMapModell)
     */
    @Override
    public void themeOrderChanged( final IMapModell source )
    {
      forceRepaint();
    }

    /**
     * @see org.kalypso.ogc.gml.mapmodel.MapModellAdapter#themeVisibilityChanged(org.kalypso.ogc.gml.mapmodel.IMapModell,
     *      org.kalypso.ogc.gml.IKalypsoTheme, boolean)
     */
    @Override
    public void themeVisibilityChanged( final IMapModell source, final IKalypsoTheme theme, boolean visibility )
    {
      forceRepaint();
    }

    /**
     * @see org.kalypso.ogc.gml.mapmodel.MapModellAdapter#repaintRequested(org.kalypso.ogc.gml.mapmodel.IMapModell,
     *      org.kalypsodeegree.model.geometry.GM_Envelope)
     */
    @Override
    public void repaintRequested( final IMapModell source, final GM_Envelope bbox )
    {
      forceRepaint();
    }

  };

  public MapPanel( final ICommandTarget viewCommandTarget, final CS_CoordinateSystem crs, final IFeatureSelectionManager manager )
  {
    m_selectionManager = manager;
    m_selectionManager.addSelectionListener( m_globalSelectionListener );

    // set empty Modell:
    final IMapModell mapModell = new MapModell( crs, null );
    setMapModell( mapModell );
    m_widgetManager = new WidgetManager( viewCommandTarget, this );
    addMouseListener( m_widgetManager );
    addMouseMotionListener( m_widgetManager );
    addKeyListener( m_widgetManager );
    addComponentListener( this );
    setVisible( true );
  }

  public void dispose( )
  {
    removeMouseListener( m_widgetManager );
    removeMouseMotionListener( m_widgetManager );
    removeKeyListener( m_widgetManager );
    removeComponentListener( this );

    m_selectionManager.removeSelectionListener( m_globalSelectionListener );

    m_widgetManager.dispose();
    
    setMapModell( null );

    // REMARK: this should not be necessary, but fixes the memory leak problem when opening/closing a .gmt file.
    // TODO: where is this ma panel still referenced from?
    m_selectionListeners.clear();
    m_mapPanelListeners.clear();
    m_mapImage = null;

  }

  public void setOffset( final int dx, final int dy ) // used by pan method
  {
    xOffset = dx;
    yOffset = dy;

    repaint();
  }

  public void clearOffset( ) // used by pan method
  {
    xOffset = 0;
    yOffset = 0;
    repaint();
  }

  /**
   * <p>
   * This method was synchronized in order to fix bugs caused by threading issues concerning the setBoundBox method.
   * </p>
   * <p>
   * The bug was fixed by this, an so far no dead locks are encountered. see also {@link #getBoundingBox()}and
   * {@link #setBoundingBox(GM_Envelope)}
   * </p>
   * <p>
   * Make sure, that no call to one of the 'fire...' methods is made in the synchronized code.
   * </p>
   * 
   * @see java.awt.Component#paint(java.awt.Graphics)
   */
  @Override
  public synchronized void paint( final Graphics g )
  {
    paintMap( g );

    paintWidget( g );
  }

  private void paintMap( final Graphics g )
  {
    // to avoid threading issues, get reference once
    final IMapModell model = m_model;

    if( (model == null) || (model.getThemeSize() == 0) ) // no maps ...
    {
      String welcomeText = "Kartenvorlage wird geladen ...";

      if( (model != null) && (model.getThemeSize() == 0) )
        welcomeText = "Keine Themen vorhanden";

      g.setColor( Color.white );
      g.fillRect( 0, 0, getWidth(), getHeight() );
      g.setColor( Color.black );
      g.drawString( welcomeText, getWidth() / 2 - (g.getFontMetrics().stringWidth( welcomeText ) / 2), getHeight() / 2 );
      return;
    }

    if( (getHeight() == 0) || (getWidth() == 0) )
      return;

    if( (getHeight() != m_height) || (getWidth() != m_width) )
    { // update dimension
      m_height = getHeight();
      m_width = getWidth();
    }

    if( !hasValidMap() || (m_mapImage == null) )
    {
      final Rectangle clipBounds = g.getClipBounds();
      if( clipBounds != null )
      {
        // BUGFIX: see method comment: the synchronization problem was exactly at this point, when the getBoundingBox
        // method was
        // called and immediatly afterwards the painting of the mapModell was called.
        // Problem was, that the repaint method after setting the boundingBox did not
        // cause a real repaint (maybe Swing checks if we are already repainting?)
        // remark: even calling a repaint in a SwingWorker did not help

        // we are optimistic and set valid map true, so while creating new image, other methods can invalidate the map
        // this fixes the error that sometimes a layer is not visible when a mapview opens
        setValidMap( true );

        final GeoTransform projection = getProjection();
        final GM_Envelope boundingBox = getBoundingBox();
        // TODO: have a look at the #createImage methods
        m_mapImage = MapModellHelper.createImageFromModell( projection, boundingBox, clipBounds, getWidth(), getHeight(), model );
        if( m_mapImage == null )
          setValidMap( false );
        else
          paintPointOfInterests( m_mapImage.getGraphics() );
      }
    }

    if( (xOffset != 0) && (yOffset != 0) ) // to clear backround ...
    {
      final int left = Math.max( 0, xOffset );
      final int right = Math.min( getWidth(), xOffset + getWidth() );
      final int top = Math.max( 0, yOffset );
      final int bottom = Math.min( getHeight(), yOffset + getHeight() );

      g.setColor( getBackground() );
      // g.setColor( Color.black );

      g.fillRect( 0, 0, left, getHeight() ); // left
      g.fillRect( left, 0, right - left, top ); // top
      g.fillRect( left, bottom, right - left, getHeight() - bottom ); // bottom
      g.fillRect( right, 0, getWidth() - right, getHeight() ); // right
    }

    // draw map:
    g.drawImage( m_mapImage, xOffset, yOffset, null );
    g.setPaintMode();
  }

  @Override
  public void update( final Graphics g )
  {
    paint( g );
  }

  private void paintWidget( final Graphics g )
  {
    // final Color color = Color.red;
    final Color color = new Color( Color.red.getRed(), Color.red.getGreen(), Color.red.getBlue(), 150 );
    g.setColor( color );

    g.setClip( 0, 0, getWidth(), getHeight() );
    m_widgetManager.paintWidget( g );
  }

  public void setValidMap( final boolean status )
  {
    validMap = status;
  }

  private boolean hasValidMap( )
  {
    return validMap;
  }

  private void setValidAll( final boolean status )
  {
    setValidMap( status );
  }

  /**
   * @see org.kalypso.ogc.gml.mapmodel.IMapModellView#getMapModell()
   */
  public IMapModell getMapModell( )
  {
    return m_model;
  }

  /**
   * @see org.kalypso.ogc.gml.mapmodel.IMapModellView#setMapModell(org.kalypso.ogc.gml.mapmodel.IMapModell)
   */
  public void setMapModell( final IMapModell modell )
  {
    final IMapModell oldModel = m_model;

    if( m_model != null )
      m_model.removeMapModelListener( m_modellListener );

    m_model = modell;

    if( m_model != null )
      m_model.addMapModelListener( m_modellListener );

    fireMapModelChanged( oldModel, m_model );
  }

  /**
   * @see org.kalypsodeegree.model.feature.event.ModellEventListener#onModellChange(org.kalypsodeegree.model.feature.event.ModellEvent)
   */
  protected final void forceRepaint( )
  {
    setValidAll( false );
    clearOffset();
  }

  public GM_Envelope getPanToLocationBoundingBox( final double gisMX, final double gisMY )
  {
    final double ratio = m_height / m_width;

    final GeoTransform transform = getProjection();

    final double gisDX = (transform.getSourceX( m_width / 2 ) - transform.getSourceX( 0 ));
    final double gisDY = gisDX * ratio;
    final double gisX1 = gisMX - gisDX;
    final double gisX2 = gisMX + gisDX;
    final double gisY1 = gisMY - gisDY;
    final double gisY2 = gisMY + gisDY;

    return GeometryFactory.createGM_Envelope( gisX1, gisY1, gisX2, gisY2 );
  }

  public GM_Envelope getPanToPixelBoundingBox( final double mx, final double my )
  {
    final GeoTransform transform = getProjection();

    final double gisMX = transform.getSourceX( mx );
    final double gisMY = transform.getSourceY( my );
    return getPanToLocationBoundingBox( gisMX, gisMY );
  }

  /**
   * calculates the current map scale (denominator) as defined in the OGC SLD 1.0.0 specification
   * 
   * @return scale of the map
   */
  public double getCurrentScale( )
  {
    return MapModellHelper.calcScale( m_model, getBoundingBox(), getWidth(), getHeight() );
  }

  public GeoTransform getProjection( )
  {
    return m_projection;
  }

  public synchronized GM_Envelope getBoundingBox( )
  {
    return m_boundingBox;
  }

  public synchronized void setBoundingBox( final GM_Envelope wishBBox )
  {
    m_wishBBox = wishBBox;

    final GM_Envelope oldExtent = m_boundingBox;
    m_boundingBox = MapModellHelper.adjustBoundingBox( m_model, m_wishBBox, getRatio() );

    if( m_boundingBox != null )
    {
      // TODO: introduce a trace option for this
      // final StringBuffer dump = new StringBuffer();
      // dump.append( "MinX:" + m_boundingBox.getMin().getX() );
      // dump.append( "\nMinY:" + m_boundingBox.getMin().getY() );
      // dump.append( "\nMaxX:" + m_boundingBox.getMax().getX() );
      // dump.append( "\nMaxY:" + m_boundingBox.getMax().getY() );
      // dump.append( "\n" );
      // System.out.println( dump.toString() );

      m_projection.setSourceRect( m_boundingBox );

      // don't call onModellChange and inform the listeners
      // this is dangerous (dead lock!) inside a synchronized method
      // onModellChange( null );

      // instead invalidate the map yourself
      setValidAll( false );
      clearOffset();
      repaint();
    }

    fireExtentChanged( oldExtent, m_boundingBox );
  }

  private double getRatio( )
  {
    return ((double) getHeight()) / ((double) getWidth());
  }

  public GM_Envelope getZoomOutBoundingBox( )
  {
    final GeoTransform transform = getProjection();
    final double ratio = getRatio();
    final double gisMX = transform.getSourceX( getWidth() / 2d );
    final double gisMY = transform.getSourceY( getHeight() / 2d );

    final double gisDX = 2 * (gisMX - transform.getSourceX( 0 ));
    final double gisDY = gisDX * ratio;
    final double gisX1 = gisMX - gisDX;
    final double gisX2 = gisMX + gisDX;
    final double gisY1 = gisMY - gisDY;
    final double gisY2 = gisMY + gisDY;

    return GeometryFactory.createGM_Envelope( gisX1, gisY1, gisX2, gisY2 );
  }

  public WidgetManager getWidgetManager( )
  {
    return m_widgetManager;
  }

  /**
   * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
   */
  public void componentHidden( final ComponentEvent e )
  {
    //  
  }

  /**
   * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
   */
  public void componentMoved( final ComponentEvent e )
  {
    //  
  }

  /**
   * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
   */
  public void componentResized( final ComponentEvent e )
  {
    if( m_wishBBox != null )
      setBoundingBox( m_wishBBox );
    else
      setBoundingBox( getBoundingBox() );
  }

  /**
   * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
   */
  public void componentShown( final ComponentEvent e )
  {
    if( m_wishBBox != null )
      setBoundingBox( m_wishBBox );
    else
      setBoundingBox( getBoundingBox() );
  }

  public void addSelectionChangedListener( final ISelectionChangedListener listener )
  {
    m_selectionListeners.add( listener );
  }

  public void removeSelectionChangedListener( final ISelectionChangedListener listener )
  {
    m_selectionListeners.remove( listener );
  }

  public void setSelection( final ISelection selection )
  {
    // should not be called!
    throw new UnsupportedOperationException();
  }

  /**
   * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
   */
  public ISelection getSelection( )
  {
    final IMapModell mapModell = getMapModell();
    if( mapModell == null )
      return StructuredSelection.EMPTY;

    final IKalypsoTheme activeTheme = mapModell.getActiveTheme();
    if( !(activeTheme instanceof IKalypsoFeatureTheme) )
      return StructuredSelection.EMPTY;

    return new KalypsoFeatureThemeSelection( m_selectionManager.toList(), (IKalypsoFeatureTheme) activeTheme, m_selectionManager, null, null );
  }

  protected void globalSelectionChanged( final IFeatureSelection selection )
  {
    if( selection != null )
    {
      // TODO: only repaint, if selection contains features contained in my themes changes
    }

    setValidAll( false );
    repaint();

    // TODO: should be fired in the SWT thread, because the global selection listeners
    // need this
    fireSelectionChanged();
  }

  /**
   * @deprecated
   */
  @Deprecated
  public void select( final Point startPoint, final Point endPoint, final int radius, final int selectionMode, final boolean useOnlyFirstChoosen )
  {
    final GeoTransform transform = getProjection();

    final IKalypsoTheme activeTheme = m_model.getActiveTheme();
    if( (activeTheme == null) || !(activeTheme instanceof IKalypsoFeatureTheme) )
      return;

    if( startPoint != null )
    {
      final double g1x = transform.getSourceX( startPoint.getX() );
      final double g1y = transform.getSourceY( startPoint.getY() );

      if( endPoint == null ) // not dragged
      {
        // TODO depend on featuretype
        // line and point with radius
        // polygon without radius
        final double gisRadius = Math.abs( transform.getSourceX( startPoint.getX() + radius ) - g1x );

        final JMSelector selector = new JMSelector();

        final GM_Point pointSelect = GeometryFactory.createGM_Point( g1x, g1y, getMapModell().getCoordinatesSystem() );

        final Feature fe = (Feature) selector.selectNearest( pointSelect, gisRadius, ((IKalypsoFeatureTheme) activeTheme).getFeatureListVisible( null ), false );

        final List<Feature> listFe = new ArrayList<Feature>();
        if( fe != null )
          listFe.add( fe );

        changeSelection( listFe, (IKalypsoFeatureTheme) activeTheme, m_selectionManager, selectionMode );
      }
      else
      // dragged
      {
        final double g2x = transform.getSourceX( endPoint.getX() );
        final double g2y = transform.getSourceY( endPoint.getY() );
        boolean withinStatus = false;

        if( (endPoint.getX() > startPoint.getX()) && (endPoint.getY() > startPoint.getY()) )
          withinStatus = true;

        final double minX = g1x < g2x ? g1x : g2x;
        final double maxX = g1x > g2x ? g1x : g2x;
        final double minY = g1y < g2y ? g1y : g2y;
        final double maxY = g1y > g2y ? g1y : g2y;

        if( (minX != maxX) && (minY != maxY) )
        {
          final JMSelector selector = new JMSelector();
          final GM_Envelope envSelect = GeometryFactory.createGM_Envelope( minX, minY, maxX, maxY );
          final List<Object> features = selector.select( envSelect, ((IKalypsoFeatureTheme) activeTheme).getFeatureListVisible( null ), withinStatus );

          if( useOnlyFirstChoosen && !features.isEmpty() )
          {
            // delete all but first if we shall only the first selected
            final Feature object = (Feature) features.get( 0 );
            features.clear();
            features.add( object );
          }

          changeSelection( features, (IKalypsoFeatureTheme) activeTheme, m_selectionManager, selectionMode );
        }
      }
    }
  }

  /**
   * @deprecated Does not belong into the MapPanel. Use {@link IFeatureSelectionChanger} instead.
   */
  @Deprecated
  private void changeSelection( final List features, final IKalypsoFeatureTheme theme, final IFeatureSelectionManager selectionManager2, final int selectionMode )
  {
    // nothing was choosen by the user, clear selection
    if( features.isEmpty() )
      selectionManager2.clear();
    // TODO: this should do the widget-manager?

    // remove all selected features from this theme
    // TODO: maybe only visible??
    final FeatureList featureList = theme.getFeatureList();
    if( featureList == null )
      return;

    final Feature parentFeature = featureList.getParentFeature();
    final IRelationType parentProperty = featureList.getParentFeatureTypeProperty();

    // add all selected features
    final EasyFeatureWrapper[] selectedWrapped = new EasyFeatureWrapper[features.size()];
    for( int i = 0; i < features.size(); i++ )
    {
      final Feature f = (Feature) features.get( i );
      selectedWrapped[i] = new EasyFeatureWrapper( theme.getWorkspace(), f, parentFeature, parentProperty );
    }

    final Feature[] toRemove;
    final EasyFeatureWrapper[] toAdd;

    switch( selectionMode )
    {
      case MODE_TOGGLE: // dreht die selection der auswahl um
        // BUG: past nicht mehr zur beschreibung!
        toRemove = new Feature[0];
        toAdd = selectedWrapped;
        break;

      case MODE_SELECT: // selectert genau das, was ausgew�hlt wurde
        // toRemove = featureList.toFeatures();
        final EasyFeatureWrapper[] allFeatures = selectionManager2.getAllFeatures();
        toRemove = new Feature[allFeatures.length];
        for( int i = 0; i < allFeatures.length; i++ )
          toRemove[i] = allFeatures[i].getFeature();
        toAdd = selectedWrapped;
        break;

      case MODE_UNSELECT: // l�scht alles augew�hlte aus der selection
        toRemove = featureList.toFeatures();
        toAdd = new EasyFeatureWrapper[0];

      default:
        throw new UnsupportedOperationException( "Unknown selection mode: " + selectionMode );
    }

    selectionManager2.changeSelection( toRemove, toAdd );
  }

  public final void fireSelectionChanged( )
  {
    final ISelectionChangedListener[] listenersArray = m_selectionListeners.toArray( new ISelectionChangedListener[m_selectionListeners.size()] );

    final SelectionChangedEvent e = new SelectionChangedEvent( this, getSelection() );
    for( final ISelectionChangedListener l : listenersArray )
    {
      final SafeRunnable safeRunnable = new SafeRunnable()
      {
        public void run( )
        {
          // TODO: fire in display thread!
          l.selectionChanged( e );
        }

        /**
         * Overwritten because opening the message dialog here results in a NPE
         * 
         * @see org.eclipse.jface.util.SafeRunnable#handleException(java.lang.Throwable)
         */
        @Override
        public void handleException( final Throwable t )
        {
          t.printStackTrace();
        }
      };

      SafeRunnable.run( safeRunnable );
    }
  }

  public void addPointOfInterest( final PointOfinterest pointOfInterest )
  {
    m_pointofInterests.add( pointOfInterest );
    setValidAll( false );
    repaint();
    final long duration = pointOfInterest.getDuration();
    final Thread thread = new Thread()
    {
      /**
       * @see java.lang.Thread#run()
       */
      @Override
      public void run( )
      {
        try
        {
          Thread.sleep( duration );
        }
        catch( InterruptedException e )
        {
          // nothing
        }
        removePointOfInterest( pointOfInterest );
      }
    };
    thread.start();
  }

  public void removePointOfInterest( final PointOfinterest pointOfInterest )
  {
    m_pointofInterests.remove( pointOfInterest );
    setValidAll( false );
    repaint();
  }

  private void paintPointOfInterests( final Graphics g )
  {
    final Graphics2D g2d = (Graphics2D) g;
    final Color color = Color.red;
    final Color innerColor = new Color( Color.yellow.getRed(), Color.yellow.getGreen(), Color.yellow.getBlue(), 150 );

    final List<PointOfinterest> toRemove = new ArrayList<PointOfinterest>();
    final GeoTransform projection = getProjection();
    for( final PointOfinterest poi : m_pointofInterests )
    {
      final GM_Point geometry = poi.getGeometry();
      final GM_Position screenPoint = projection.getDestPoint( geometry.getPosition() );
      final int r = 10;
      final int x = (int) screenPoint.getX();
      final int y = (int) screenPoint.getY();
      final String title = poi.getTitle();
      g2d.drawString( title, x + 2 * r, y + 2 * r );
      final Font font = g2d.getFont();
      final FontRenderContext frc = g2d.getFontRenderContext();
      final Rectangle2D bounds = font.getStringBounds( title, frc );
      final int bw = (int) bounds.getWidth();
      final int bh = (int) bounds.getHeight();
      // inner
      g2d.setColor( innerColor );
      g2d.fillOval( x - r, y - r, r * 2, r * 2 );
      g2d.setColor( Color.YELLOW );

      g2d.fillRect( x + 2 * r, y + 2 * r - bh, bw, bh + 3 );

      // outer
      g2d.setColor( color );
      g2d.drawOval( x - r, y - r, r * 2, r * 2 );
      g2d.drawString( title, x + 2 * r, y + 2 * r );
    }
    for( final PointOfinterest ofinterest : toRemove )
      m_pointofInterests.remove( ofinterest );
  }

  public IFeatureSelectionManager getSelectionManager( )
  {
    return m_selectionManager;
  }

  /**
   * Add a listener in the mapPanel that will be notified in specific changes. <br/> At the moment there is only the
   * message changed event.
   */
  public void addMapPanelListener( final IMapPanelListener l )
  {
    m_mapPanelListeners.add( l );
  }

  /**
   * Removes this listener from the mapPanel.
   */
  public void removeMapPanelListener( final IMapPanelListener l )
  {
    m_mapPanelListeners.remove( l );
  }

  /**
   * Sets the message of this mapPanel. Some widgets update it, so that the MapView could update the status-bar text.
   */
  public void setMessage( final String message )
  {
    m_message = message;

    fireMessageChanged( message );
  }

  public String getMessage( )
  {
    return m_message;
  }

  private static interface IListenerRunnable
  {
    public void visit( final IMapPanelListener l );
  }

  /**
   * Runns the given runnable on every listener in a safe way.
   */
  private void acceptListenersRunnable( final IListenerRunnable r )
  {
    final IMapPanelListener[] listeners = m_mapPanelListeners.toArray( new IMapPanelListener[m_mapPanelListeners.size()] );
    for( final IMapPanelListener l : listeners )
    {
      final ISafeRunnable code = new SafeRunnable()
      {
        public void run( ) throws Exception
        {
          r.visit( l );
        }
      };

      SafeRunner.run( code );
    }
  }

  /**
   * Must be invoked, if the message of the mapPanel has changed.
   */
  private void fireMessageChanged( final String message )
  {
    acceptListenersRunnable( new IListenerRunnable()
    {
      public void visit( final IMapPanelListener l )
      {
        l.onMessageChanged( MapPanel.this, message );
      }
    } );
  }

  protected void fireExtentChanged( final GM_Envelope oldExtent, final GM_Envelope newExtent )
  {
    acceptListenersRunnable( new IListenerRunnable()
    {
      public void visit( final IMapPanelListener l )
      {
        l.onExtentChanged( MapPanel.this, oldExtent, newExtent );
      }
    } );
  }

  protected void fireMapModelChanged( final IMapModell oldModel, final IMapModell newModel )
  {
    acceptListenersRunnable( new IListenerRunnable()
    {
      public void visit( final IMapPanelListener l )
      {
        l.onMapModelChanged( MapPanel.this, oldModel, newModel );
      }
    } );
  }

}