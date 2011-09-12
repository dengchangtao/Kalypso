/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 * 
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  Denickestra�e 22
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
package org.kalypso.model.wspm.tuhh.ui.chart;

import org.kalypso.model.wspm.core.IWspmConstants;
import org.kalypso.model.wspm.core.profil.IProfil;
import org.kalypso.model.wspm.core.profil.changes.PointPropertyRemove;
import org.kalypso.model.wspm.tuhh.core.IWspmTuhhConstants;
import org.kalypso.model.wspm.tuhh.ui.i18n.Messages;
import org.kalypso.model.wspm.ui.profil.operation.ProfilOperation;
import org.kalypso.model.wspm.ui.profil.operation.ProfilOperationJob;
import org.kalypso.model.wspm.ui.view.chart.AbstractProfilTheme;
import org.kalypso.model.wspm.ui.view.chart.IProfilChartLayer;

import de.openali.odysseus.chart.framework.model.layer.IChartLayer;
import de.openali.odysseus.chart.framework.model.mapper.ICoordinateMapper;

/**
 * @author kimwerner
 */
public class GeoCoordinateTheme extends AbstractProfilTheme
{
  public static final String TITLE = Messages.getString( "org.kalypso.model.wspm.tuhh.ui.chart.GeoCoordinateTheme.0" ); //$NON-NLS-1$

  public GeoCoordinateTheme( final IProfil profil, final IProfilChartLayer[] chartLayers, final ICoordinateMapper cm )
  {
    super( profil, IWspmTuhhConstants.LAYER_GEOKOORDINATEN, TITLE, chartLayers, cm );

  }

  /**
   * @see org.kalypso.model.wspm.ui.view.chart.AbstractProfilTheme#getLegendNodes()
   */
  @Override
  public IChartLayer[] getLegendNodes( )
  {
    return new IChartLayer[] {};
  }

  /**
   * @see org.kalypso.model.wspm.ui.view.chart.AbstractProfilLayer#removeYourself()
   */
  @Override
  public void removeYourself( )
  {
    final IProfil profil = getProfil();

    final ProfilOperation operation = new ProfilOperation( Messages.getString( "org.kalypso.model.wspm.tuhh.ui.chart.GeoCoordinateTheme.1" ), getProfil(), true ); //$NON-NLS-1$
    operation.addChange( new PointPropertyRemove( profil, profil.hasPointProperty( IWspmConstants.POINT_PROPERTY_HOCHWERT ) ) );
    operation.addChange( new PointPropertyRemove( profil, profil.hasPointProperty( IWspmConstants.POINT_PROPERTY_RECHTSWERT ) ) );
    new ProfilOperationJob( operation ).schedule();
  }
}