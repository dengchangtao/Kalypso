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
package org.kalypso.model.wspm.tuhh.core.results;

import java.math.BigDecimal;

import org.kalypso.observation.IObservation;
import org.kalypso.observation.result.IComponent;
import org.kalypso.observation.result.IRecord;
import org.kalypso.observation.result.TupleResult;
import org.kalypso.observation.util.TupleResultIndex;

/**
 * @author Gernot Belger
 */
public class WspmResultLengthSectionColumn
{
  private final IObservation<TupleResult> m_observation;

  private final TupleResultIndex m_stationIndex;

  private final int m_component;

  private final String m_label;

  public WspmResultLengthSectionColumn( final IObservation<TupleResult> observation, final TupleResultIndex stationIndex, final IComponent component )
  {
    m_observation = observation;
    m_stationIndex = stationIndex;
    final TupleResult result = m_observation.getResult();
    m_component = result.indexOfComponent( component );

    // FIXME: replace with component.getLabel
    final String componentLabel = component.getName();
    m_label = String.format( "%s - %s", m_observation.getName(), componentLabel );
  }

  private BigDecimal getValue( final BigDecimal station, final int componentIndex )
  {
    final IRecord record = m_stationIndex.getRecord( station );
    if( record == null )
      return null;

    return (BigDecimal) record.getValue( componentIndex );
  }

  /** The result value for the given station */
  public Object getValue( final BigDecimal station )
  {
    return getValue( station, m_component );
  }

  public String getLabel( )
  {
    return m_label;
  }

}
