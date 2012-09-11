/** This file is part of Kalypso
 *
 *  Copyright (c) 2012 by
 *
 *  Bj�rnsen Beratende Ingenieure GmbH, Koblenz, Germany (Bjoernsen Consulting Engineers), http://www.bjoernsen.de
 *  Technische Universit�t Hamburg-Harburg, Institut f�r Wasserbau, Hamburg, Germany
 *  (Technical University Hamburg-Harburg, Institute of River and Coastal Engineering), http://www.tu-harburg.de/wb/
 *
 *  Kalypso is free software: you can redistribute it and/or modify it under the terms  
 *  of the GNU Lesser General Public License (LGPL) as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  Kalypso is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with Kalypso.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kalypso.kalypsomodel1d2d.ui.wizard.profileImport;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.kalypso.kalypsosimulationmodel.core.terrainmodel.IRiverProfileNetworkCollection;
import org.kalypso.ui.views.map.MapView;
import org.kalypsodeegree.model.feature.Feature;

/**
 * @author Holger Albert
 */
public class ImportProfileData
{
  private final MapView m_mapView;

  private final IRiverProfileNetworkCollection m_profNetworkColl;

  private final List<Feature> m_terrainModelAdds;

  public ImportProfileData( final MapView mapView )
  {
    m_mapView = mapView;
    m_profNetworkColl = getRiverProfileNetworkCollection();
    m_terrainModelAdds = new ArrayList<>();
  }

  private IRiverProfileNetworkCollection getRiverProfileNetworkCollection( )
  {
    try
    {
      return ThemeHelper.getTerrainModel().getRiverProfileNetworkCollection();
    }
    catch( final ExecutionException e )
    {
      // FIXME Handle errors...
      e.printStackTrace();
      return null;
    }
  }

  public MapView getMapView( )
  {
    return m_mapView;
  }

  public IRiverProfileNetworkCollection getProfNetworkColl( )
  {
    return m_profNetworkColl;
  }

  public List<Feature> getTerrainModelAdds( )
  {
    return m_terrainModelAdds;
  }
}