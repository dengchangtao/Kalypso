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
package org.kalypso.kalypsomodel1d2d.ui.map.cmds;

import java.util.List;

import javax.xml.namespace.QName;

import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IContinuityLine1D;
import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IContinuityLine2D;
import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IFE1D2DNode;
import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IFEDiscretisationModel1d2d;
import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IFELine;
import org.kalypso.kalypsomodel1d2d.ui.i18n.Messages;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree.model.feature.binding.IFeatureWrapper2;
import org.kalypsodeegree.model.feature.binding.IFeatureWrapperCollection;
import org.kalypsodeegree.model.feature.event.FeatureStructureChangeModellEvent;

public class CreateContinuityLineCommand implements IDiscrModel1d2dChangeCommand
{
  private boolean m_processed = false;

  private IFELine m_line;

  final private IFEDiscretisationModel1d2d m_model;

  final private List<IFE1D2DNode> m_nodeList;

  final private QName m_lineElementQName;

  public CreateContinuityLineCommand( final IFEDiscretisationModel1d2d model, final List<IFE1D2DNode> nodeList, final QName lineElementQName )
  {
    m_model = model;
    m_nodeList = nodeList;
    m_lineElementQName = lineElementQName;
  }

  /**
   * @see org.kalypso.commons.command.ICommand#getDescription()
   */
  @Override
  public String getDescription( )
  {
    return Messages.getString("org.kalypso.kalypsomodel1d2d.ui.map.cmds.CreateContinuityLineCommand.0"); //$NON-NLS-1$
  }

  /**
   * @see org.kalypso.commons.command.ICommand#isUndoable()
   */
  @Override
  public boolean isUndoable( )
  {
    return true;
  }

  /**
   * @see org.kalypso.commons.command.ICommand#process()
   */
  @Override
  public void process( ) throws Exception
  {
    // TODO check if such a line already exists (with same nodes etc...)
    
    final Feature parentFeature = m_model.getFeature();
    final GMLWorkspace workspace = parentFeature.getWorkspace();
    final IFeatureWrapperCollection<IFELine> continuityLines = m_model.getContinuityLines();

    if( m_lineElementQName.equals( IContinuityLine1D.QNAME ) )
      m_line = continuityLines.addNew( m_lineElementQName, IContinuityLine1D.class );
    else
      m_line = continuityLines.addNew( m_lineElementQName, IContinuityLine2D.class );
    
    final List<IFE1D2DNode> nodes = m_line.createFullNodesList( m_nodeList );
    for( int i = 0; i < nodes.size(); i++ )
      nodes.get( i ).addContainer( m_line.getGmlID() );
    m_line.getFeature().invalidEnvelope();
    workspace.fireModellEvent( new FeatureStructureChangeModellEvent( workspace, parentFeature, m_line.getFeature(), FeatureStructureChangeModellEvent.STRUCTURE_CHANGE_ADD ) );
    m_processed = true;
  }

  /**
   * @see org.kalypso.commons.command.ICommand#redo()
   */
  @Override
  public void redo( ) throws Exception
  {
    if( !m_processed )
    {
      process();
    }
  }

  /**
   * @see org.kalypso.commons.command.ICommand#undo()
   */
  @Override
  public void undo( ) throws Exception
  {
    if( m_processed )
    {
      // TODO remove element and links to it edges and delete element
    }
  }

  /**
   * @see xp.IDiscrMode1d2dlChangeCommand#getChangedFeature()
   */
  @Override
  public IFeatureWrapper2[] getChangedFeature( )
  {
    return new IFeatureWrapper2[] { m_line };
  }

  /**
   * @see xp.IDiscrMode1d2dlChangeCommand#getDiscretisationModel1d2d()
   */
  @Override
  public IFEDiscretisationModel1d2d getDiscretisationModel1d2d( )
  {
    return m_model;
  }
}