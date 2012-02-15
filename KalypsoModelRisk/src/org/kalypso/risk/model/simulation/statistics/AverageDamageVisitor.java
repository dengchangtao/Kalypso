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
package org.kalypso.risk.model.simulation.statistics;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.index.ItemVisitor;

/**
 * @author Gernot Belger
 */
public class AverageDamageVisitor implements ItemVisitor
{
  private final Coordinate m_position;

  private final double m_cellArea;

  private int m_foundElement = 0;

  public AverageDamageVisitor( final Coordinate position, final double cellArea )
  {
    m_position = position;
    m_cellArea = cellArea;
  }

  @Override
  public void visitItem( final Object item )
  {
    final StatisticArea area = (StatisticArea) item;
    if( area.contains( m_position ) )
    {
      area.getItem().addAverageAnnualDamage( m_position.z, m_cellArea );
      m_foundElement++;
    }
  }

  public int getFoundElements( )
  {
    return m_foundElement;
  }
}