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
package org.kalypso.model.wspm.tuhh.ui.export.sobek;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.kalypso.model.wspm.core.gml.IProfileFeature;
import org.kalypso.model.wspm.core.profil.IProfil;
import org.kalypso.model.wspm.core.profil.IProfileObject;
import org.kalypso.model.wspm.core.profil.visitors.ProfileVisitors;
import org.kalypso.model.wspm.core.profil.wrappers.IProfileRecord;
import org.kalypso.model.wspm.core.util.WspmGeometryUtilities;
import org.kalypso.model.wspm.tuhh.core.profile.buildings.building.BuildingBruecke;
import org.kalypso.shape.ShapeDataException;
import org.kalypso.shape.dbf.DBaseException;
import org.kalypso.shape.shp.SHPException;
import org.kalypsodeegree.model.geometry.GM_Point;

/**
 * Exports WSPM profiles as struct.def SOBEK file.
 * 
 * @author Gernot Belger
 */
public class SobekStructShapeExportOperation extends AbstractSobekExportOperation
{
  public static final String LOCATION = "struct"; //$NON-NLS-1$

  private final SobekShapePoint m_shapePoint;

  public SobekStructShapeExportOperation( final SobekExportInfo info )
  {
    super( info );

    final IProfileFeature[] profilesToExport = info.getProfiles();
    m_shapePoint = new SobekShapePoint( profilesToExport, LOCATION );
  }

  @Override
  public String getLabel( )
  {
    return LOCATION;
  }

  @Override
  protected void initTargetFile( ) throws DBaseException, IOException
  {
    final File dir = getInfo().getTargetDir();
    m_shapePoint.create( dir );
  }

  @Override
  protected void close( ) throws IOException
  {
    m_shapePoint.close();
  }

  /**
   * @see org.kalypso.model.wspm.tuhh.ui.export.sobek.AbstractSobekExportOperation#closeQuiet()
   */
  @Override
  protected void closeQuiet( )
  {
    m_shapePoint.closeQuiet();
  }

  @Override
  protected void writeProfile( final IProfileFeature profileFeature ) throws IOException, DBaseException, SHPException, ShapeDataException
  {
    final IProfil profil = profileFeature.getProfil();

    final SobekExportInfo info = getInfo();

    final IProfileRecord minPoint = ProfileVisitors.findLowestPoint( profil );
    if( minPoint == null )
      return;


    if( minPoint == null )
    {
      // empty profile, ignore
      return;
    }

    final String srs = profileFeature.getSrsName();
    final GM_Point lowPoint = WspmGeometryUtilities.createLocation( profil, minPoint, srs );

    final String structID = info.getStructID( profileFeature );
    final String name = info.getName( profileFeature );

    final IProfileObject[] profileObjects = profil.getProfileObjects();
    int reallyExportedBuildings = 0;
    for( final IProfileObject profileObject : profileObjects )
    {
      final String countSuffix = reallyExportedBuildings == 0 ? StringUtils.EMPTY : "_" + reallyExportedBuildings; //$NON-NLS-1$
      final String structId = structID + countSuffix;

      if( writeBuilding( lowPoint, structId, name, profileObject ) )
      {
        reallyExportedBuildings++;
      }
    }
  }

  private boolean writeBuilding( final GM_Point lowPoint, final String structId, final String name, final IProfileObject profileObject ) throws ShapeDataException, IOException, DBaseException, SHPException
  {
    if( profileObject instanceof BuildingBruecke )
    {
      m_shapePoint.addEntry( lowPoint, structId, name );
      return true;
    }

    return false;
  }
}