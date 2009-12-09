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
package org.kalypso.model.wspm.tuhh.core.profile;

import java.util.HashMap;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.kalypso.model.wspm.core.profil.IProfilPointMarkerProvider;
import org.kalypso.model.wspm.tuhh.core.IWspmTuhhConstants;

/**
 * @author kimwerner
 */
public class DeviderProvider implements IProfilPointMarkerProvider
{

  private static final HashMap<String, RGB> m_markerTypes = new HashMap<String, RGB>();

  public DeviderProvider( )
  {
    m_markerTypes.put( IWspmTuhhConstants.MARKER_TYP_BORDVOLL, new RGB( 200, 50, 0 ) );
    m_markerTypes.put( IWspmTuhhConstants.MARKER_TYP_DURCHSTROEMTE, new RGB( 0, 0, 255 ) );
    m_markerTypes.put( IWspmTuhhConstants.MARKER_TYP_TRENNFLAECHE, new RGB( 0, 180, 0 ) );
    m_markerTypes.put( IWspmTuhhConstants.MARKER_TYP_WEHR, new RGB( 0, 128, 0 ) );
  }

  /**
   * @see org.kalypso.model.wspm.core.profil.IProfilPointMarkerProvider#getImageFor(java.lang.String)
   */
  public void drawMarker( final String[] markers, GC gc )
  {

    final int cnt = markers.length;
    final int offset = (16 - (3 * cnt)) / 2;
    int i = 0;
    final Color oldColor = gc.getBackground();
    for( final String marker : markers )
    {
      final Color color = new Color( gc.getDevice(), m_markerTypes.get( marker ) );
      try
      {
        gc.setBackground( color );
        gc.fillRectangle( offset + 4 * i++, 0, 3, 16 );
      }
      finally
      {
        gc.setBackground( oldColor );
        color.dispose();
      }

    }
  }

  /**
   * @see org.kalypso.model.wspm.core.profil.IProfilPointMarkerProvider#getColorFor(java.lang.String)
   */
  public RGB getColorFor( String marker )
  {
    return m_markerTypes.get( marker );
  }
}
