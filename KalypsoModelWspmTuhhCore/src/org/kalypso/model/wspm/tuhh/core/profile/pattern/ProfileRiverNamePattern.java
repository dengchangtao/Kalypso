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
package org.kalypso.model.wspm.tuhh.core.profile.pattern;

import org.kalypso.model.wspm.core.gml.IProfileFeature;
import org.kalypso.model.wspm.core.gml.WspmWaterBody;
import org.kalypso.model.wspm.tuhh.core.i18n.Messages;

/**
 * @author Gernot Belger
 */
public class ProfileRiverNamePattern extends AbstractProfileStringPattern
{
  public ProfileRiverNamePattern( )
  {
    super( "River", Messages.getString( "ProfileRiverNamePattern_0" ) ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * @see org.kalypso.model.wspm.tuhh.core.profile.pattern.IValueWithFormat#getValue(org.kalypso.model.wspm.tuhh.core.profile.pattern.IProfilePatternData,
   *      java.lang.String)
   */
  @Override
  public String getValue( final IProfilePatternData data, final String params )
  {
    final IProfileFeature profileFeature = data.getProfileFeature();
    if( profileFeature == null )
      return null;

    final WspmWaterBody water = profileFeature.getWater();
    if( water == null )
      return null;

    return water.getName();
  }

  /**
   * @see org.kalypso.model.wspm.tuhh.core.profile.pattern.IValueWithFormat#getDefaultWidth(java.lang.String)
   */
  @Override
  public int getDefaultWidth( final String params )
  {
    return 15;
  }
}
