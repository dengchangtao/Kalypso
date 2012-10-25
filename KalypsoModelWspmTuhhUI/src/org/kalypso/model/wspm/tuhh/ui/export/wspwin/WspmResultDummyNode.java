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
package org.kalypso.model.wspm.tuhh.ui.export.wspwin;

import org.kalypso.model.wspm.tuhh.core.results.AbstractWspmResultNode;
import org.kalypso.model.wspm.tuhh.core.results.IWspmResultNode;

/**
 * A node implementation that does not wrap a model element but gets all information plainly from outside.
 *
 * @author Gernot Belger
 */
class WspmResultDummyNode extends AbstractWspmResultNode
{
  private final IWspmResultNode[] m_children;

  private final String m_internalName;

  public WspmResultDummyNode( final String internalName, final IWspmResultNode parentNode, final IWspmResultNode[] children )
  {
    super( parentNode );

    m_internalName = internalName;

    m_children = children;
  }

  @Override
  public String getLabel( )
  {
    return null;
  }

  @Override
  protected IWspmResultNode[] createChildren( )
  {
    return m_children;
  }

  @Override
  public Object getObject( )
  {
    return null;
  }

  @Override
  protected String getInternalName( )
  {
    return m_internalName;
  }
}