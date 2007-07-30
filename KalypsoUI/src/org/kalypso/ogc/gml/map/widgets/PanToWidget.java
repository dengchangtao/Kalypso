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
package org.kalypso.ogc.gml.map.widgets;

import java.awt.Point;

import org.kalypso.ogc.gml.command.ChangeExtentCommand;
import org.kalypso.ogc.gml.map.MapPanel;
import org.kalypsodeegree.model.geometry.GM_Envelope;

/**
 * @author vDoemming
 */
public class PanToWidget extends AbstractWidget
{
  // Patrice Congo: So people stop removing things
  public PanToWidget( final String name, final String toolTip )
  {
    super( name, toolTip );
  }

  public PanToWidget( )
  {
    super( "pan to", "" );
  }

  private Point endPoint = null;

  private Point startPoint = null;

  @Override
  public void dragged( final Point p )
  {
    if( startPoint != null )
    {
      endPoint = p;

      final int dx = (int) (endPoint.getX() - startPoint.getX());
      final int dy = (int) (endPoint.getY() - startPoint.getY());
      getMapPanel().setOffset( dx, dy );
    }
    // TODO: check if this repaint is really necessary
    final MapPanel panel = getMapPanel();
    if( panel != null )
      panel.repaint();

  }

  @Override
  public void finish( )
  {
    getMapPanel().setOffset( 0, 0 );
  }

  @Override
  public void leftPressed( final Point p )
  {
    startPoint = p;
    endPoint = null;
    getMapPanel().setOffset( 0, 0 );
  }

  @Override
  public void leftReleased( final Point p )
  {
    endPoint = p;
    perform();
  }

  public void perform( )
  {
    if( startPoint != null && endPoint != null && !startPoint.equals( endPoint ) )
    {
      final MapPanel mapPanel = getMapPanel();
      final double mx = mapPanel.getWidth() / 2d - (endPoint.getX() - startPoint.getX());
      final double my = mapPanel.getHeight() / 2d - (endPoint.getY() - startPoint.getY());

      final GM_Envelope panBox = mapPanel.getPanToPixelBoundingBox( mx, my );

      startPoint = null;
      endPoint = null;

      if( panBox != null )
      {
        final ChangeExtentCommand command = new ChangeExtentCommand( mapPanel, panBox );
        postViewCommand( command, null );
      }

    }
  }
}