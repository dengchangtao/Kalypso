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
package org.kalypso.kalypsomodel1d2d.ui.map;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.kalypso.commons.command.ICommandTarget;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.kalypsomodel1d2d.KalypsoModel1D2DPlugin;
import org.kalypso.kalypsomodel1d2d.schema.Kalypso1D2DSchemaConstants;
import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IFE1D2DNode;
import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IFEDiscretisationModel1d2d;
import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IPolyElement;
import org.kalypso.kalypsomodel1d2d.ui.i18n.Messages;
import org.kalypso.kalypsomodel1d2d.ui.map.util.PointSnapper;
import org.kalypso.kalypsomodel1d2d.ui.map.util.UtilMap;
import org.kalypso.ogc.gml.IKalypsoFeatureTheme;
import org.kalypso.ogc.gml.map.IMapPanel;
import org.kalypso.ogc.gml.map.utilities.MapUtilities;
import org.kalypso.ogc.gml.map.utilities.tooltip.ToolTipRenderer;
import org.kalypso.ogc.gml.map.widgets.builders.LineGeometryBuilder;
import org.kalypso.ogc.gml.map.widgets.builders.PolygonGeometryBuilder;
import org.kalypso.ogc.gml.mapmodel.IMapModell;
import org.kalypso.ogc.gml.widgets.AbstractWidget;
import org.kalypso.ui.editor.mapeditor.views.IWidgetWithOptions;
import org.kalypsodeegree.graphics.displayelements.DisplayElement;
import org.kalypsodeegree.graphics.sld.Fill;
import org.kalypsodeegree.graphics.sld.LineSymbolizer;
import org.kalypsodeegree.graphics.sld.PolygonSymbolizer;
import org.kalypsodeegree.graphics.sld.Stroke;
import org.kalypsodeegree.graphics.transformation.GeoTransform;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureList;
import org.kalypsodeegree.model.geometry.GM_Curve;
import org.kalypsodeegree.model.geometry.GM_MultiCurve;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree.model.geometry.GM_Position;
import org.kalypsodeegree.model.geometry.GM_Surface;
import org.kalypsodeegree.model.geometry.GM_TriangulatedSurface;
import org.kalypsodeegree_impl.graphics.displayelements.DisplayElementFactory;
import org.kalypsodeegree_impl.graphics.sld.LineSymbolizer_Impl;
import org.kalypsodeegree_impl.graphics.sld.PolygonSymbolizer_Impl;
import org.kalypsodeegree_impl.graphics.sld.StyleFactory;
import org.kalypsodeegree_impl.tools.GeometryUtilities;

/**
 * This widget is used to triangulate a boundary with breaklines. The user gets a preview before he starts the
 * refinement of the model.
 *
 * @author Stefan Kurzbach
 */
public class TriangulateGeometryWidget extends AbstractWidget implements IWidgetWithOptions
{
  private final TriangulateGeometryData m_data = new TriangulateGeometryData( this );

  private boolean m_modePolygon = true;

  private Point m_currentMapPoint;

  private PointSnapper m_pointSnapper;

  private PolygonGeometryBuilder m_boundaryGeometryBuilder = null;

  private LineGeometryBuilder m_breaklineGeometryBuilder = null;

  private final ToolTipRenderer m_toolTipRenderer = new ToolTipRenderer();

  private final ToolTipRenderer m_warningRenderer = new ToolTipRenderer();

  private Composite m_composite;

  private FeatureList m_featureList;

  private final Map<GM_Position, IFE1D2DNode> m_nodesNameConversionMap = new HashMap<GM_Position, IFE1D2DNode>();

  private IFEDiscretisationModel1d2d m_discModel;

  private PolygonSymbolizer m_polySymb;

  private LineSymbolizer m_lineSymb;

  private IKalypsoFeatureTheme m_theme;

  public TriangulateGeometryWidget( )
  {
    super( Messages.getString( "org.kalypso.kalypsomodel1d2d.ui.map.RefineFEGeometryWidget.0" ), Messages.getString( "org.kalypso.kalypsomodel1d2d.ui.map.RefineFEGeometryWidget.1" ) ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Override
  public void activate( final ICommandTarget commandPoster, final IMapPanel mapPanel )
  {
    super.activate( commandPoster, mapPanel );

    reinit();
  }

  final void reinit( )
  {
    final IMapPanel mapPanel = getMapPanel();
    final IMapModell mapModell = mapPanel.getMapModell();
    mapPanel.repaintMap();

    m_modePolygon = true;

    m_theme = UtilMap.findEditableTheme( mapPanel, Kalypso1D2DSchemaConstants.WB1D2D_F_NODE );

    m_discModel = UtilMap.findFEModelTheme( mapPanel );

    m_pointSnapper = new PointSnapper( m_discModel, mapPanel );

    final IKalypsoFeatureTheme theme = UtilMap.findEditableTheme( mapPanel, IPolyElement.QNAME );
    m_featureList = theme == null ? null : theme.getFeatureList();

    final String mode = m_modePolygon ? Messages.getString( "TriangulateGeometryWidget.3" ) : Messages.getString( "TriangulateGeometryWidget.4" ); //$NON-NLS-1$ //$NON-NLS-2$
    final String modeTooltip = String.format( Messages.getString( "TriangulateGeometryWidget.2" ), mode ); //$NON-NLS-1$

    m_toolTipRenderer.setBackgroundColor( new Color( 1f, 1f, 0.6f, 0.70f ) );
    m_toolTipRenderer.setTooltip( Messages.getString( "org.kalypso.kalypsomodel1d2d.ui.map.TriangulateGeometryWidget.2" ) + modeTooltip ); //$NON-NLS-1$
    m_warningRenderer.setBackgroundColor( new Color( 1f, 0.4f, 0.4f, 0.80f ) );

    m_boundaryGeometryBuilder = new PolygonGeometryBuilder( 0, mapModell.getCoordinatesSystem() );
    m_breaklineGeometryBuilder = new LineGeometryBuilder( 0, mapModell.getCoordinatesSystem() );

    m_data.resetGeometries();

    m_nodesNameConversionMap.clear();

    m_polySymb = new PolygonSymbolizer_Impl();
    final Fill fill = StyleFactory.createFill( new Color( 255, 255, 255 ) );
    fill.setOpacity( 0.0 );
    final Stroke polyStroke = StyleFactory.createStroke( new Color( 255, 0, 0 ) );
    m_polySymb.setFill( fill );
    m_polySymb.setStroke( polyStroke );

    m_lineSymb = new LineSymbolizer_Impl();
    final Stroke lineStroke = StyleFactory.createStroke( new Color( 0, 255, 0 ) );
    m_lineSymb.setStroke( lineStroke );
  }

  @Override
  public void mousePressed( final MouseEvent event )
  {
    if( event.getButton() != MouseEvent.BUTTON1 )
      return;

    event.consume();

    try
    {
      final IMapPanel mapPanel = getMapPanel();

      final boolean snappingActive = true;
      final GM_Point thePoint = snapToNode( event.getPoint(), snappingActive );
      if( thePoint == null )
      {
        mapPanel.setCursor( Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR ) );
        return;
      }

      m_currentMapPoint = MapUtilities.retransform( getMapPanel(), thePoint );

      if( m_modePolygon )
      {
        m_boundaryGeometryBuilder.addPoint( thePoint );

        final String warning = validatePolygon( null );
        m_warningRenderer.setTooltip( warning );
      }
      else
        m_breaklineGeometryBuilder.addPoint( thePoint );

      mapPanel.setCursor( Cursor.getDefaultCursor() );
    }
    catch( final Exception e )
    {
      e.printStackTrace();
      final IStatus status = StatusUtilities.statusFromThrowable( e );
      KalypsoModel1D2DPlugin.getDefault().getLog().log( status );
      reinit();
    }
  }

  @Override
  public void mouseClicked( final MouseEvent event )
  {
    if( event.getButton() != MouseEvent.BUTTON1 || event.getClickCount() < 2 )
      return;

    try
    {
      if( m_modePolygon )
      {
        final GM_Surface< ? > boundaryGeom = (GM_Surface< ? >) m_boundaryGeometryBuilder.finish();
        m_data.setBoundary( boundaryGeom );
      }
      else
      {
        final GM_Curve finish = (GM_Curve) m_breaklineGeometryBuilder.finish();
        m_data.addBreakLine( finish );
      }

      final GM_MultiCurve breaklines = m_data.getBreaklines();
      if( breaklines != null )
      {
        final GM_Curve[] curves = breaklines.getAllCurves();
        if( curves != null )
        {
          if( curves.length > 0 )
            m_breaklineGeometryBuilder.reset();
        }
      }

      m_data.finishGeometry();
    }
    catch( final Exception e )
    {
      e.printStackTrace();
      final IStatus status = StatusUtilities.statusFromThrowable( e );
      KalypsoModel1D2DPlugin.getDefault().getLog().log( status );
      final IMapPanel mapPanel = getMapPanel();
      mapPanel.setMessage( Messages.getString( "org.kalypso.kalypsomodel1d2d.ui.map.RefineFEGeometryWidget.4" ) + status.getMessage() ); //$NON-NLS-1$
      reinit();
    }
  }

  private String validatePolygon( final GM_Point point )
  {
    m_warningRenderer.setTooltip( null );

    try
    {
      if( point != null )
        m_boundaryGeometryBuilder.addPoint( point );

      final GM_Surface< ? > finish = (GM_Surface< ? >) m_boundaryGeometryBuilder.finish();
      if( finish == null )
        return null;

      if( GeometryUtilities.isSelfIntersecting( finish.get( 0 ).getExteriorRing() ) )
        return Messages.getString( "TriangulateGeometryWidget.0" ); //$NON-NLS-1$

      final List<Feature> possiblyIntersecting = m_featureList.query( finish.getEnvelope(), null );
      for( final Feature feature : possiblyIntersecting )
      {
        final GM_Object geom = feature.getDefaultGeometryPropertyValue();
        if( geom.intersects( finish ) )
        {
          final GM_Object intersection = geom.intersection( finish );
          if( intersection instanceof GM_Surface )
            return Messages.getString( "TriangulateGeometryWidget.1" ); //$NON-NLS-1$
        }
      }

      return null;
    }
    catch( final Exception e )
    {
      return e.toString();
    }
    finally
    {
      if( point != null && m_boundaryGeometryBuilder != null )
        m_boundaryGeometryBuilder.removeLastPoint();
    }
  }

  @Override
  public void mouseMoved( final MouseEvent event )
  {
    final IMapPanel mapPanel = getMapPanel();
    if( mapPanel == null )
      return;

    final boolean snappingActive = true;
    final GM_Point point = snapToNode( event.getPoint(), snappingActive );
    if( point == null )
    {
      getMapPanel().setCursor( Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR ) );
      return;
    }

    m_currentMapPoint = MapUtilities.retransform( getMapPanel(), point );

    getMapPanel().setCursor( Cursor.getDefaultCursor() );

    if( m_modePolygon )
    {
      final String warning = validatePolygon( point );
      m_warningRenderer.setTooltip( warning );
    }

    repaintMap();
  }

  @Override
  public void paint( final Graphics g )
  {
    /* always paint a small rectangle of current position */
    if( m_currentMapPoint == null )
      return;

    final int[][] posPoints = UtilMap.getPointArrays( m_currentMapPoint );

    final int[] arrayX = posPoints[0];
    final int[] arrayY = posPoints[1];

    /* Paint as linestring. */
    g.drawPolygon( arrayX, arrayY, arrayX.length );
    UtilMap.drawHandles( g, arrayX, arrayY );

    /* paint the snap */
    if( m_pointSnapper != null )
      m_pointSnapper.paint( g );

    super.paint( g );

    final IMapPanel mapPanel = getMapPanel();
    final Rectangle bounds = mapPanel.getScreenBounds();
    final GeoTransform projection = mapPanel.getProjection();

    m_toolTipRenderer.paintToolTip( new Point( 5, bounds.height - 5 ), g, bounds );

    m_warningRenderer.paintToolTip( new Point( 5, bounds.height - 80 ), g, bounds );

    if( m_modePolygon )
    {
      if( m_boundaryGeometryBuilder != null )
        m_boundaryGeometryBuilder.paint( g, projection, m_currentMapPoint );

      // TODO: would be nice to hilight the snapped nodes as well (so the user can see, if there are unsnapped nodes)
    }
    else
    {
      if( m_breaklineGeometryBuilder != null )
        m_breaklineGeometryBuilder.paint( g, projection, m_currentMapPoint );
    }

    drawTinAndBreaklines( g, projection );
  }

  private void drawTinAndBreaklines( final Graphics g, final GeoTransform projection )
  {
    try
    {
      final GM_TriangulatedSurface tin = m_data.getTin();

      if( tin != null && !tin.isEmpty() )
      {
        final DisplayElement de = DisplayElementFactory.buildPolygonDisplayElement( null, tin, m_polySymb );
        de.paint( g, projection, new NullProgressMonitor() );
      }

      final GM_MultiCurve breaklines = m_data.getBreaklines();
      if( breaklines != null && !breaklines.isEmpty() )
      {
        final DisplayElement de = DisplayElementFactory.buildLineStringDisplayElement( null, breaklines, m_lineSymb );
        de.paint( g, projection, new NullProgressMonitor() );
      }
    }
    catch( final Exception e )
    {
      e.printStackTrace();
    }
  }

  @Override
  public void keyPressed( final KeyEvent e )
  {
    if( e.getKeyCode() == KeyEvent.VK_SPACE )
    {
      final String mode = m_modePolygon ? Messages.getString( "TriangulateGeometryWidget.4" ) : Messages.getString( "TriangulateGeometryWidget.3" ); //$NON-NLS-1$ //$NON-NLS-2$
      final String modeTooltip = String.format( Messages.getString( "TriangulateGeometryWidget.2" ), mode ); //$NON-NLS-1$

      if( m_modePolygon == true )
      {
        m_modePolygon = !m_modePolygon;
        m_toolTipRenderer.setTooltip( Messages.getString( "org.kalypso.kalypsomodel1d2d.ui.map.TriangulateGeometryWidget.2" ) + modeTooltip ); //$NON-NLS-1$
      }
      else
      {
        m_modePolygon = !m_modePolygon;
        m_toolTipRenderer.setTooltip( Messages.getString( "org.kalypso.kalypsomodel1d2d.ui.map.TriangulateGeometryWidget.2" ) + modeTooltip ); //$NON-NLS-1$
      }
      repaintMap();
    }

    else if( e.getKeyCode() == KeyEvent.VK_ESCAPE )
      reinit();
    else if( e.getKeyCode() == KeyEvent.VK_BACK_SPACE )
    {
      if( m_modePolygon )
        m_boundaryGeometryBuilder.removeLastPoint();
      else
        m_breaklineGeometryBuilder.removeLastPoint();
    }
    else if( e.getKeyCode() == KeyEvent.VK_ENTER )
      m_data.convertTriangulationToModel();
    else
      super.keyPressed( e );
  }


  private GM_Point snapToNode( final Point p, final boolean snappingActive )
  {
    final IMapPanel mapPanel = getMapPanel();
    if( mapPanel == null || m_pointSnapper == null )
      return null;

    final GM_Point currentPoint = MapUtilities.transform( mapPanel, p );

    m_pointSnapper.activate( snappingActive );
    final IFE1D2DNode snapNode = m_pointSnapper.moved( currentPoint );

    if( snapNode == null )
      return currentPoint;

    return snapNode.getPoint();
  }

  @Override
  public Control createControl( final Composite parent, final FormToolkit toolkit )
  {
    m_composite = new TriangulateGeometryComposite( toolkit, parent, m_data );
    return m_composite;
  }

  @Override
  public void disposeControl( )
  {
    if( m_composite != null && !m_composite.isDisposed() )
      m_composite.dispose();
  }

  @Override
  public String getPartName( )
  {
    return Messages.getString( "TriangulateGeometryWidget.15" ); //$NON-NLS-1$
  }

  public void onTinUpdated( )
  {
    m_boundaryGeometryBuilder.reset();
    m_breaklineGeometryBuilder.reset();

    repaintMap();
  }

  IKalypsoFeatureTheme getDiscTheme( )
  {
    return m_theme;
  }
}