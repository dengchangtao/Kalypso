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

import java.util.ArrayList;
import java.util.Collection;

import org.kalypso.model.wspm.core.gml.WspmFixation;
import org.kalypso.model.wspm.core.gml.WspmReach;
import org.kalypso.model.wspm.core.gml.WspmWaterBody;
import org.kalypso.model.wspm.tuhh.core.gml.TuhhReach;
import org.kalypsodeegree.model.feature.IFeatureBindingCollection;

/**
 * @author Gernot Belger
 */
public class WspmResultWaterNode extends AbstractWspmResultNode
{
  private final WspmWaterBody m_water;

  public WspmResultWaterNode( final IWspmResultNode parent, final WspmWaterBody water )
  {
    super( parent );
    m_water = water;
  }

  @Override
  protected IWspmResultNode[] createChildren( )
  {
    final Collection<IWspmResultNode> results = new ArrayList<>();

    final IFeatureBindingCollection<WspmReach> reaches = m_water.getReaches();
    for( final WspmReach reach : reaches )
      results.add( new WspmResultReachNode( this, (TuhhReach) reach ) );

    final IFeatureBindingCollection<WspmFixation> fixations = m_water.getWspFixations();
    for( final WspmFixation fixation : fixations )
    {
      results.add( new WspmResultFixationNode( this, fixation ) );
    }

    return results.toArray( new IWspmResultNode[results.size()] );
  }

  @Override
  public String getLabel( )
  {
    return m_water.getName();
  }

  @Override
  protected String getInternalName( )
  {
    return m_water.getId();
  }

  @Override
  public Object getObject( )
  {
    return m_water;
  }

  @Override
  public String toString( )
  {
    return String.format( "%s\n%s", super.toString(), m_water.getName() ); //$NON-NLS-1$
  }
}
