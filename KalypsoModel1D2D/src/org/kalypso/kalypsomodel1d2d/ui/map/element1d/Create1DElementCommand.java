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
package org.kalypso.kalypsomodel1d2d.ui.map.element1d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kalypso.commons.command.ICommand;
import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IElement1D;
import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IFE1D2DEdge;
import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IFE1D2DNode;
import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IFEDiscretisationModel1d2d;
import org.kalypso.kalypsomodel1d2d.ui.i18n.Messages;
import org.kalypso.kalypsomodel1d2d.ui.map.ElementGeometryHelper;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree.model.feature.event.FeatureStructureChangeModellEvent;
import org.kalypsodeegree.model.geometry.GM_Curve;
import org.kalypsodeegree.model.geometry.GM_CurveSegment;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree.model.geometry.GM_Position;
import org.kalypsodeegree_impl.model.geometry.GeometryFactory;

/**
 * @author Gernot Belger
 */
public class Create1DElementCommand implements ICommand
{
  private final IFEDiscretisationModel1d2d m_discModel;

  private final GM_Curve m_curve;

  private final Set<IFE1D2DNode> m_changedNodes = new HashSet<>();

  private final Set<IFE1D2DEdge> m_changedEdges = new HashSet<>();

  private final Set<IElement1D> m_changedElements = new HashSet<>();

  public Create1DElementCommand( final IFEDiscretisationModel1d2d discModel, final GM_Curve curve )
  {
    m_discModel = discModel;
    m_curve = curve;
  }

  @Override
  public boolean isUndoable( )
  {
    return false;
  }

  @Override
  public void redo( ) throws Exception
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void undo( ) throws Exception
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getDescription( )
  {
    return Messages.getString( "org.kalypso.kalypsomodel1d2d.ui.map.element1d.CreateFEElement1DWidget.6" ); //$NON-NLS-1$
  }

  @Override
  public void process( ) throws Exception
  {
    final String crs = m_curve.getCoordinateSystem();

    final int numberOfCurveSegments = m_curve.getNumberOfCurveSegments();

    for( int i = 0; i < numberOfCurveSegments; i++ )
    {
      final GM_CurveSegment segment = m_curve.getCurveSegmentAt( i );
      final int numberOfPoints = segment.getNumberOfPoints();

      for( int j = 0; j < numberOfPoints - 1; j++ )
      {
        final GM_Position startPosition = segment.getPositionAt( j );
        final GM_Position endPosition = segment.getPositionAt( j + 1 );

        final GM_Point startPoint = GeometryFactory.createGM_Point( startPosition, crs );
        final GM_Point endPoint = GeometryFactory.createGM_Point( endPosition, crs );

        final List<GM_Point> nodes = new ArrayList<>( 2 );
        nodes.add( startPoint );
        nodes.add( endPoint );

        add1dElement( startPoint, endPoint );
      }
    }

    /* fire workspace events */

    final GMLWorkspace discWorkspace = m_discModel.getWorkspace();

    final Feature[] changedNodes = m_changedNodes.toArray( new Feature[m_changedNodes.size()] );
    discWorkspace.fireModellEvent( new FeatureStructureChangeModellEvent( discWorkspace, m_discModel, changedNodes, FeatureStructureChangeModellEvent.STRUCTURE_CHANGE_ADD ) );

    final Feature[] changedEdges = m_changedEdges.toArray( new Feature[m_changedEdges.size()] );
    discWorkspace.fireModellEvent( new FeatureStructureChangeModellEvent( discWorkspace, m_discModel, changedEdges, FeatureStructureChangeModellEvent.STRUCTURE_CHANGE_ADD ) );

    final Feature[] changedElements = m_changedElements.toArray( new Feature[m_changedElements.size()] );
    discWorkspace.fireModellEvent( new FeatureStructureChangeModellEvent( discWorkspace, m_discModel, changedElements, FeatureStructureChangeModellEvent.STRUCTURE_CHANGE_ADD ) );
  }

  /**
   * Add new {@link IFE1D2DElement} is specified by its geometry.
   */
  private void add1dElement( final GM_Point startPoint, final GM_Point endPoint )
  {
    /* Build new nodes */
    final IFE1D2DNode[] nodes = ElementGeometryHelper.buildNewNodes( m_discModel, startPoint, endPoint );

    // REMARK: never build a new 1d edge on top of an existing edge
    final IFE1D2DEdge existing = m_discModel.findEdge( nodes[0], nodes[1] );
    if( existing != null )
      return;

    /* Build one new edge for the 1d element */
    final IFE1D2DEdge edge = ElementGeometryHelper.buildNewEdges( m_discModel, nodes, 1 )[0];

    /* Build new element */
    final IElement1D newElement = m_discModel.createElement1D( edge );

    /* Remember new elements for events */
    m_changedNodes.addAll( Arrays.asList( nodes ) );
    m_changedEdges.add( edge );
    m_changedElements.add( newElement );
  }
}