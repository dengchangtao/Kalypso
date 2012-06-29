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
package org.kalypso.kalypsomodel1d2d.conv.results;

import org.kalypso.kalypsomodel1d2d.schema.binding.results.INodeResult;

/**
 * @author Thomas Jung
 * 
 */
public class ArcResult
{
  public int arcID;

  public int node1ID;

  public int node2ID;

  public int elementLeftID;

  public int elementRightID;

  public int middleNodeID;

  public INodeResult m_nodeUp;

  public INodeResult m_nodeDown;

  public INodeResult getNodeUp( )
  {
    return m_nodeUp;
  }

  public void setNodeUp( INodeResult nodeUp )
  {
    m_nodeUp = nodeUp;
  }

  public INodeResult getNodeDown( )
  {
    return m_nodeDown;
  }

  public void setNodeDown( INodeResult nodeDown )
  {
    m_nodeDown = nodeDown;
  }

  public ArcResult( int id, int node1, int node2, int elementLeft, int elementRight, int middleNode )
  {
    arcID = id;
    node1ID = node1;
    node2ID = node2;
    elementLeftID = elementLeft;
    elementRightID = elementRight;
    middleNodeID = middleNode;
  }
}