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
package org.kalypso.kalypsomodel1d2d.ui.map.temsys;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Event;
import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IFE1D2DNode;
import org.kalypso.kalypsomodel1d2d.ui.i18n.Messages;
import org.kalypsodeegree.model.geometry.GM_Point;

/**
 * Selects all nodes that have no elevation.
 * 
 * @author Gernot Belger
 */
class SelectAllWithoutElevationAction extends Action
{
  private final TableViewer m_nodeViewer;

  public SelectAllWithoutElevationAction( final TableViewer nodeViewer )
  {
    m_nodeViewer = nodeViewer;

    setText( Messages.getString( "AssignNodeElevationFaceComponent.2" ) ); //$NON-NLS-1$
  }

  @Override
  public void runWithEvent( final Event event )
  {
    final List<IFE1D2DNode> nodesWithoutElevation = new ArrayList<>();

    final IFE1D2DNode[] allNodes = (IFE1D2DNode[])m_nodeViewer.getInput();
    for( final IFE1D2DNode node : allNodes )
    {
      final GM_Point point = node.getPoint();
      if( point != null && Double.isNaN( point.getZ() ) )
        nodesWithoutElevation.add( node );
    }

    final ISelection selection = new StructuredSelection( nodesWithoutElevation );
    m_nodeViewer.setSelection( selection );
  }
}