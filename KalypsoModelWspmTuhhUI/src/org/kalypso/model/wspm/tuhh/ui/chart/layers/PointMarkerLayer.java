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
package org.kalypso.model.wspm.tuhh.ui.chart.layers;

import java.awt.geom.Point2D;

import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.kalypso.model.wspm.core.IWspmPointProperties;
import org.kalypso.model.wspm.core.KalypsoModelWspmCoreExtensions;
import org.kalypso.model.wspm.core.profil.IProfil;
import org.kalypso.model.wspm.core.profil.IProfilPointMarker;
import org.kalypso.model.wspm.core.profil.IProfilPointMarkerProvider;
import org.kalypso.model.wspm.core.profil.changes.ActiveObjectEdit;
import org.kalypso.model.wspm.core.profil.changes.PointMarkerSetPoint;
import org.kalypso.model.wspm.core.profil.changes.ProfilChangeHint;
import org.kalypso.model.wspm.core.profil.operation.ProfilOperation;
import org.kalypso.model.wspm.core.profil.operation.ProfilOperationJob;
import org.kalypso.model.wspm.core.profil.util.ProfilUtil;
import org.kalypso.model.wspm.core.profil.visitors.ProfileVisitors;
import org.kalypso.model.wspm.core.profil.wrappers.IProfileRecord;
import org.kalypso.model.wspm.tuhh.core.IWspmTuhhConstants;
import org.kalypso.model.wspm.tuhh.ui.i18n.Messages;
import org.kalypso.model.wspm.ui.view.ILayerStyleProvider;
import org.kalypso.model.wspm.ui.view.chart.AbstractProfilLayer;
import org.kalypso.observation.result.IComponent;

import de.openali.odysseus.chart.framework.model.data.IDataRange;
import de.openali.odysseus.chart.framework.model.event.ILayerManagerEventListener.ContentChangeType;
import de.openali.odysseus.chart.framework.model.figure.impl.EmptyRectangleFigure;
import de.openali.odysseus.chart.framework.model.figure.impl.PolylineFigure;
import de.openali.odysseus.chart.framework.model.layer.EditInfo;
import de.openali.odysseus.chart.framework.model.layer.ILegendEntry;
import de.openali.odysseus.chart.framework.model.layer.impl.LegendEntry;
import de.openali.odysseus.chart.framework.model.mapper.IAxis;
import de.openali.odysseus.chart.framework.util.img.ChartImageInfo;

/**
 * @author kimwerner
 */
public class PointMarkerLayer extends AbstractProfilLayer
{
  private final int m_offset;

  private final boolean m_close;

  private int m_screenTop;

  private int m_screenBottom;

  public PointMarkerLayer( final IProfil profil, final String targetRangeProperty, final ILayerStyleProvider styleProvider, final int offset, final boolean close )
  {
    super( IWspmTuhhConstants.LAYER_DEVIDER + "_" + targetRangeProperty, profil, targetRangeProperty, styleProvider ); //$NON-NLS-1$

    m_offset = offset;

    m_close = close;
  }

  @Override
  public EditInfo drag( final Point newPos, final EditInfo dragStartData )
  {
    final IProfil profil = getProfil();
    final IProfileRecord point = ProfileVisitors.findNearestPoint( profil, toNumeric( newPos ).getX() );

    final Rectangle hoverRect = getHoverRect( point );

    final EmptyRectangleFigure hoverFigure = new EmptyRectangleFigure();
    hoverFigure.setStyle( getLineStyleHover() );
    hoverFigure.setRectangle( hoverRect );

    return new EditInfo( this, null, hoverFigure, dragStartData.getData(), getTooltipInfo( point ), dragStartData.getPosition() );
  }

  @Override
  public final void executeDrop( final Point point, final EditInfo dragStartData )
  {
    final Integer pos = dragStartData.getData() instanceof Integer ? (Integer) dragStartData.getData() : -1;
    if( pos == -1 )
      return;

    final IProfil profil = getProfil();
    final Point2D numPoint = toNumeric( point );
    if( numPoint == null )
      return;

    final IProfileRecord profilPoint = profil.getPoint( pos );
    final IProfileRecord newPoint = ProfileVisitors.findNearestPoint( profil, numPoint.getX() );
    if( newPoint == profilPoint )
      return;

    final IProfilPointMarker[] deviders = profil.getPointMarkerFor( profilPoint );
    final IProfilPointMarker[] targetDeviders = profil.getPointMarkerFor( newPoint );

    for( final IProfilPointMarker devider : deviders )
    {
      // BUGIFX: prohibit that a marker is moved on another marker of the same type, which
      // will incidentally remove it
      if( movesOnSameDeviderType( devider, targetDeviders ) )
      {
        continue;
      }

      if( devider.getComponent().getId().equals( getTargetComponent().getId() ) )
      {
        moveDevider( devider, newPoint );
      }
    }
  }

  @Override
  public EditInfo getHover( final Point pos )
  {
    final IProfilPointMarker[] deviders = getProfil().getPointMarkerFor( getTargetComponent() );
    for( final IProfilPointMarker devider : deviders )
    {
      final IProfileRecord point = devider.getPoint();
      final Rectangle hoverRect = getHoverRect( point );

      if( hoverRect != null && hoverRect.contains( pos ) )
      {
        final EmptyRectangleFigure hoverFigure = new EmptyRectangleFigure();
        hoverFigure.setStyle( getLineStyleHover() );
        hoverFigure.setRectangle( hoverRect );

        return new EditInfo( this, hoverFigure, null, point.getIndex(), getTooltipInfo( point ), pos );
      }
    }

    return null;
  }

  @Override
  public Rectangle getHoverRect( final IProfileRecord profilPoint )
  {
    final int bottom = getBottom() + 2;
    final int top = getTop() - 2;

    final Double breite = ProfilUtil.getDoubleValueFor( getDomainComponent().getId(), profilPoint );

    final int screenX = getCoordinateMapper().getDomainAxis().numericToScreen( breite );
    return new Rectangle( screenX - 5, top, 10, bottom - top );
  }

  @Override
  public synchronized ILegendEntry[] getLegendEntries( )
  {
    final IProfilPointMarkerProvider markerProvider = KalypsoModelWspmCoreExtensions.getMarkerProviders( getProfil().getType() );

    final LegendEntry le = new LegendEntry( this, getTitle() )
    {
      @Override
      public void paintSymbol( final GC gc, final Point size )
      {
        final IComponent cmp = getTargetComponent();
        if( cmp != null )
        {
          markerProvider.drawMarker( new String[] { cmp.getId() }, gc );
        }
      }
    };

    return new ILegendEntry[] { le };
  }

  @Override
  public IDataRange< ? > getTargetRange( final IDataRange< ? > domainIntervall )
  {
    return null;
  }

  @Override
  public String getTooltipInfo( final IProfileRecord point )
  {
    final Point2D p = getPoint2D( point );
    try
    {
      return Messages.getString( "org.kalypso.model.wspm.tuhh.ui.chart.PointMarkerLayer.0", new Object[] { getDomainComponent().getName(), p.getX(), getTargetComponent().getName() } ); //$NON-NLS-1$
    }
    catch( final RuntimeException e )
    {
      return e.getLocalizedMessage();
    }
  }

  protected void moveDevider( final IProfilPointMarker devider, final IProfileRecord newPoint )
  {
    final IProfil profil = getProfil();

    final ProfilOperation operation = new ProfilOperation( "", profil, true ); //$NON-NLS-1$
    operation.addChange( new PointMarkerSetPoint( devider, newPoint ) );
    operation.addChange( new ActiveObjectEdit( profil, newPoint.getBreiteAsRange(), null ) );
    new ProfilOperationJob( operation ).schedule();
  }

  private boolean movesOnSameDeviderType( final IProfilPointMarker devider, final IProfilPointMarker[] targetDeviders )
  {
    final String id = devider.getComponent().getId();
    for( final IProfilPointMarker marker : targetDeviders )
    {
      final String targetId = marker.getComponent().getId();
      if( ObjectUtils.equals( id, targetId ) )
        return true;
    }

    return false;
  }

  @Override
  public void onProfilChanged( final ProfilChangeHint hint )
  {
    if( hint.isPointPropertiesChanged() || hint.isMarkerMoved() || hint.isMarkerDataChanged() || hint.isProfilPropertyChanged() )
    {
      getEventHandler().fireLayerContentChanged( this, ContentChangeType.value );
    }
  }

  @Override
  public void paint( final GC gc, ChartImageInfo chartImageInfo, final IProgressMonitor monitor )
  {
    final IProfil profil = getProfil();
    final IComponent target = getTargetComponent();

    if( profil == null || target == null )
      return;
    final IProfilPointMarker[] deviders = profil.getPointMarkerFor( target.getId() );
    final int len = deviders.length;

    m_screenBottom = chartImageInfo.getLayerRect().y + chartImageInfo.getLayerRect().height;
    m_screenTop = chartImageInfo.getLayerRect().y + m_offset;

    final IAxis domainAxis = getDomainAxis();

    final PolylineFigure pf = new PolylineFigure();
    pf.setStyle( getLineStyle() );
    for( int i = 0; i < len; i++ )
    {
      final Double breite = ProfilUtil.getDoubleValueFor( IWspmPointProperties.POINT_PROPERTY_BREITE, deviders[i].getPoint() );
      final int screenX = domainAxis.numericToScreen( breite );
      final Point p1 = new Point( screenX, getBottom() );
      final Point p2 = new Point( screenX, getTop() );

      pf.setPoints( new Point[] { p1, p2 } );
      pf.paint( gc );
    }

    if( m_close && len > 1 )
    {
      final int screenX1 = domainAxis.numericToScreen( ProfilUtil.getDoubleValueFor( getDomainComponent().getId(), deviders[0].getPoint() ) );
      final int screenX2 = domainAxis.numericToScreen( ProfilUtil.getDoubleValueFor( getDomainComponent().getId(), deviders[len - 1].getPoint() ) );

      pf.setPoints( new Point[] { new Point( screenX1, getTop() ), new Point( screenX2, getTop() ) } );
      pf.paint( gc );
    }
  }

  private int getTop( )
  {
    return m_screenTop + m_offset;
  }

  private int getBottom( )
  {
    return m_screenBottom;
  }
}
