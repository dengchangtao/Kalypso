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
package org.kalypso.kalypsomodel1d2d.ui.map.cmds;

import java.util.ArrayList;
import java.util.List;

import org.kalypso.commons.command.ICommand;
import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IFE1D2DEdge;
import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IFEDiscretisationModel1d2d;
import org.kalypso.kalypsomodel1d2d.schema.functions.GeometryCalcControl;
import org.kalypso.kalypsomodel1d2d.ui.i18n.Messages;
import org.kalypso.kalypsosimulationmodel.core.Assert;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree.model.feature.binding.IFeatureWrapper2;
import org.kalypsodeegree.model.feature.event.FeatureStructureChangeModellEvent;

/**
 * Composite command used to change the discretisation command. This composite takes the responsibility to notify the
 * commandable workspace about the change introduced by its sub command
 * 
 * 
 * @author Patrice Congo
 * 
 */
public class ChangeDiscretiationModelCommand implements ICommand
{
  public static final String DEFAULT_DESCRIPTION = Messages.getString( "org.kalypso.kalypsomodel1d2d.ui.map.cmds.ChangeDiscretiationModelCommand.0" ); //$NON-NLS-1$

  private final String m_description;

  private final IFEDiscretisationModel1d2d m_model1d2d;

  private final GMLWorkspace m_commandableWorkspace;

  private final List<IDiscrModel1d2dChangeCommand> m_commands = new ArrayList<IDiscrModel1d2dChangeCommand>();

  private final List<IDiscrModel1d2dChangeCommand> m_nodeCommands = new ArrayList<IDiscrModel1d2dChangeCommand>();

  private boolean m_isUndoable = true;

  public ChangeDiscretiationModelCommand( final GMLWorkspace commandableWorkspace, final IFEDiscretisationModel1d2d model1d2d )
  {
    this( commandableWorkspace, model1d2d, DEFAULT_DESCRIPTION );
  }

  public ChangeDiscretiationModelCommand( final GMLWorkspace commandableWorkspace, final IFEDiscretisationModel1d2d model1d2d, final String description )
  {
    Assert.throwIAEOnNullParam( model1d2d, Messages.getString( "org.kalypso.kalypsomodel1d2d.ui.map.cmds.ChangeDiscretiationModelCommand.1" ) ); //$NON-NLS-1$
    Assert.throwIAEOnNullParam( description, Messages.getString( "org.kalypso.kalypsomodel1d2d.ui.map.cmds.ChangeDiscretiationModelCommand.2" ) ); //$NON-NLS-1$
    m_description = description;
    m_model1d2d = model1d2d;
    m_commandableWorkspace = commandableWorkspace;
  }

  /**
   * @see org.kalypso.commons.command.ICommand#getDescription()
   */
  @Override
  public String getDescription( )
  {
    return m_description;
  }

  /**
   * @see org.kalypso.commons.command.ICommand#isUndoable()
   */
  @Override
  public boolean isUndoable( )
  {
    return m_isUndoable;
  }

  /**
   * @see org.kalypso.commons.command.ICommand#process()
   */
  @Override
  public void process( ) throws Exception
  {
    final List<Feature> changedFeatures = new ArrayList<Feature>();

    // build nodes with geo indexing
    for( final IDiscrModel1d2dChangeCommand command : m_nodeCommands )
    {
      try
      {
        command.process();
        for( final IFeatureWrapper2 changedFeature : command.getChangedFeature() )
        {
          if( changedFeature != null )
          {
            final Feature wrappedFeature = changedFeature.getFeature();
            if( wrappedFeature != null )
            {
              changedFeatures.add( wrappedFeature );
              // wrappedFeature.invalidEnvelope();
            }
          }
        }
      }
      catch( final Exception e )
      {
        e.printStackTrace();
      }
    }

    // build edges and element without indexing
    GeometryCalcControl.setDoCalcEdge( false );
    GeometryCalcControl.setDoCalcElement( false );
    try
    {
      for( final IDiscrModel1d2dChangeCommand command : m_commands )
      {
        try
        {
          command.process();
          for( final IFeatureWrapper2 changedFeature : command.getChangedFeature() )
          {
            if( changedFeature != null )
            {
              final Feature wrappedFeature = changedFeature.getFeature();
              if( wrappedFeature != null )
              {
                changedFeatures.add( wrappedFeature );
                wrappedFeature.invalidEnvelope();
              }
            }
          }
        }
        catch( final Exception e )
        {
          e.printStackTrace();
        }
      }
    }
    finally
    {
      GeometryCalcControl.setDoCalcEdge( true );
      GeometryCalcControl.setDoCalcElement( true );
    }

    for( final IFE1D2DEdge edge : m_model1d2d.getEdges() )
    {
      edge.getFeature().invalidEnvelope();
    }
    m_model1d2d.getEdges().getWrappedList().invalidate();
    m_model1d2d.getElements().getWrappedList().invalidate();
    // model1d2d.getNodes().getWrappedList().invalidate();
    fireStructureChange( changedFeatures );
  }

  private final void fireStructureChange( final List<Feature> changedFeatures )
  {
    final Feature[] changedFeaturesArray = new Feature[changedFeatures.size()];
    changedFeatures.toArray( changedFeaturesArray );
    m_commandableWorkspace.fireModellEvent( new FeatureStructureChangeModellEvent( m_commandableWorkspace, m_model1d2d.getFeature(), changedFeaturesArray, FeatureStructureChangeModellEvent.STRUCTURE_CHANGE_ADD ) );
  }

  /**
   * @see org.kalypso.commons.command.ICommand#redo()
   */
  @Override
  public void redo( ) throws Exception
  {
    for( final IDiscrModel1d2dChangeCommand command : m_commands )
    {
      try
      {
        command.redo();
      }
      catch( final Throwable th )
      {
        th.printStackTrace();
      }
    }
  }

  /**
   * @see org.kalypso.commons.command.ICommand#undo()
   */
  @Override
  public void undo( ) throws Exception
  {

    // reverse order is taken because of eventual dependencies
    IDiscrModel1d2dChangeCommand command;
    for( int index = m_commands.size() - 1; index >= 0; index-- )
    {
      command = m_commands.get( index );
      try
      {
        command.undo();
      }
      catch( final Exception e )
      {
        e.printStackTrace();
      }
    }

  }

  public void addCommand( final IDiscrModel1d2dChangeCommand command )
  {
    Assert.throwIAEOnNullParam( command, Messages.getString( "org.kalypso.kalypsomodel1d2d.ui.map.cmds.ChangeDiscretiationModelCommand.3" ) ); //$NON-NLS-1$
    if( m_commands instanceof AddNodeCommand )
    {
      m_nodeCommands.add( command );
    }
    else
    {
      m_commands.add( command );

    }

    m_isUndoable = m_isUndoable && command.isUndoable();

  }
}
