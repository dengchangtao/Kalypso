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
package org.kalypso.model.wspm.pdb.gaf.internal;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.kalypso.model.wspm.pdb.connect.PdbConnectException;

/**
 * Assembles all read points into different profiles.
 * 
 * @author Gernot Belger
 */
public class GafProfiles
{
  private GafProfile m_currentProfile;

  private final Set<BigDecimal> m_committedStations = new HashSet<BigDecimal>();

  private final GafLogger m_logger;

  private final Gaf2Db m_gaf2db;

  public GafProfiles( final GafLogger logger, final Gaf2Db gaf2db )
  {
    m_logger = logger;
    m_gaf2db = gaf2db;
  }

  public void addPoint( final GafPoint point ) throws PdbConnectException
  {
    final BigDecimal station = point.getStation();

    if( m_committedStations.contains( station ) )
    {
      final String message = String.format( "duplicate station: %s", station );
      m_logger.log( IStatus.WARNING, message );
    }

    if( m_currentProfile != null && !station.equals( m_currentProfile.getStation() ) )
      commitProfile();

    if( m_currentProfile == null )
      createProfile( station );

    m_currentProfile.addPoint( point );
  }

  public void stop( ) throws PdbConnectException
  {
    commitProfile();
  }

  private void createProfile( final BigDecimal station )
  {
    m_currentProfile = new GafProfile( station, m_logger );
  }

  private void commitProfile( ) throws PdbConnectException
  {
    if( m_currentProfile == null )
      return;

    final BigDecimal station = m_currentProfile.getStation();

    m_committedStations.add( station );

    m_gaf2db.commitProfile( m_currentProfile );

    m_currentProfile = null;
  }
}