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
package org.kalypso.model.wspm.pdb.gaf.internal;

import org.kalypso.model.wspm.pdb.connect.IPdbConnection;
import org.kalypso.model.wspm.pdb.connect.PdbConnectException;
import org.kalypso.model.wspm.pdb.db.mapping.States;
import org.kalypso.model.wspm.pdb.db.mapping.WaterBodies;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * Writes a gaf profile into the database.
 * 
 * @author Gernot Belger
 */
public class Gaf2Db
{
  private final IPdbConnection m_connection;

  private final WaterBodies m_waterBody;

  private final States m_state;

  private final GeometryFactory m_geometryFactory;

  private int m_profileCount = 0;

  public Gaf2Db( final IPdbConnection connection, final WaterBodies waterBody, final States state, final int srid )
  {
    m_connection = connection;
    m_waterBody = waterBody;
    m_state = state;
    m_geometryFactory = new GeometryFactory( new PrecisionModel(), srid );
  }

  public void addState( ) throws PdbConnectException
  {
    m_connection.addState( m_state );
  }

  public void commitProfile( final GafProfile profile ) throws PdbConnectException
  {
    final AddProfileCommand operation = new AddProfileCommand( m_profileCount++, profile, m_waterBody, m_state, m_geometryFactory );
    m_connection.executeCommand( operation );
  }

  public GeometryFactory getGeometryFactory( )
  {
    return m_geometryFactory;
  }
}