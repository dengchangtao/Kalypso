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
package org.kalypso.ui.rrm.internal.results.view.tree.strategies;

import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.kalypso.contribs.eclipse.core.resources.ResourceUtilities;
import org.kalypso.contribs.java.net.UrlResolverSingleton;
import org.kalypso.model.hydrology.binding.model.Catchment;
import org.kalypso.model.hydrology.binding.model.NaModell;
import org.kalypso.model.hydrology.binding.model.channels.Channel;
import org.kalypso.model.hydrology.binding.model.channels.StorageChannel;
import org.kalypso.model.hydrology.binding.model.nodes.Node;
import org.kalypso.model.hydrology.project.RrmSimulation;
import org.kalypso.ui.rrm.internal.results.view.base.HydrologyResultReference;
import org.kalypso.ui.rrm.internal.results.view.base.KalypsoHydrologyResults.RRM_RESULT;
import org.kalypso.ui.rrm.internal.results.view.tree.HydrologyCalculationFoldersCollector;
import org.kalypso.ui.rrm.internal.results.view.tree.handlers.HydrologyCalculationCaseGroupUiHandler;
import org.kalypso.ui.rrm.internal.results.view.tree.handlers.HydrologyCatchmentUiHandler;
import org.kalypso.ui.rrm.internal.results.view.tree.handlers.HydrologyCatchmentsGroupUiHandler;
import org.kalypso.ui.rrm.internal.results.view.tree.handlers.HydrologyNodeUiHandler;
import org.kalypso.ui.rrm.internal.results.view.tree.handlers.HydrologyNodesGroupUiHandler;
import org.kalypso.ui.rrm.internal.results.view.tree.handlers.HydrologyResultReferenceUiHandler;
import org.kalypso.ui.rrm.internal.results.view.tree.handlers.HydrologyStorageChannelUiHandler;
import org.kalypso.ui.rrm.internal.results.view.tree.handlers.HydrologyStorageChannelsGroupUiHandler;
import org.kalypso.ui.rrm.internal.utils.featureTree.ITreeNodeStrategy;
import org.kalypso.ui.rrm.internal.utils.featureTree.TreeNode;
import org.kalypso.ui.rrm.internal.utils.featureTree.TreeNodeModel;
import org.kalypsodeegree.model.feature.IFeatureBindingCollection;
import org.kalypsodeegree.model.feature.IFeatureBindingCollectionVisitor;

/**
 * @author Dirk Kuch
 */
public class NaModelStrategy implements ITreeNodeStrategy
{
  private final NaModell m_model;

  public NaModelStrategy( final NaModell model )
  {
    m_model = model;
  }

  @Override
  public TreeNode buildNodes( final TreeNodeModel model )
  {
    final TreeNode virtualRootNode = new TreeNode( model, null, null, null );

    try
    {
      final URL context = m_model.getWorkspace().getContext();
      final URL urlCalcCases = UrlResolverSingleton.resolveUrl( context, "../Rechenvarianten/" ); //$NON-NLS-1$
      final IFolder folderCalcCases = ResourceUtilities.findFolderFromURL( urlCalcCases );

      folderCalcCases.accept( new IResourceVisitor()
      {
        @Override
        public boolean visit( final IResource resource )
        {
          if( !(resource instanceof IFolder) )
            return true;
          else if( isBaseFolder( (IFolder) resource ) )
            return true;
          else if( isCalculationCaseFolder( (IFolder) resource ) )
          {
            if( StringUtils.startsWithIgnoreCase( resource.getName(), "tmp" ) )
              return true;

            virtualRootNode.addChild( buildCalculationCaseNodes( virtualRootNode, new RrmSimulation( (IFolder) resource ) ) );
            return true;
          }

          return true;
        }

        private boolean isBaseFolder( final IFolder folder )
        {
          return folder.equals( folderCalcCases );
        }

        private boolean isCalculationCaseFolder( final IFolder folder )
        {
          return folder.getParent().equals( folderCalcCases );
        }

      }, 1, false );
    }
    catch( final Exception ex )
    {
      ex.printStackTrace();
    }

    return virtualRootNode;
  }

  protected TreeNode buildCalculationCaseNodes( final TreeNode parent, final RrmSimulation simulation )
  {
    final TreeNode calcCase = new TreeNode( parent, new HydrologyCalculationCaseGroupUiHandler( simulation ), simulation );
    try
    {
      // FIXME english project template folder names?!?
      final HydrologyCalculationFoldersCollector visitor = new HydrologyCalculationFoldersCollector( simulation ); //$NON-NLS-1$
      simulation.getResultsFolder().accept( visitor, 1, false );

      final IFolder[] caluculationResultsFolders = visitor.getFolders();

      for( final IFolder calculationResultFolder : caluculationResultsFolders )
      {
        final TreeNode calculationResultNode = new TreeNode( calcCase, new HydrologyCalculationCaseGroupUiHandler( simulation, calculationResultFolder ), calculationResultFolder );

        calculationResultNode.addChild( buildHydrologyNodes( calculationResultNode, simulation, calculationResultFolder ) );
        calculationResultNode.addChild( buildHydrologyCatchments( calculationResultNode, simulation, calculationResultFolder ) );
        calculationResultNode.addChild( buildHydrologyStorages( calculationResultNode, simulation, calculationResultFolder ) );

        calcCase.addChild( calculationResultNode );
      }

    }
    catch( final CoreException e )
    {
      e.printStackTrace();
    }

    return calcCase;
  }

  private TreeNode buildHydrologyStorages( final TreeNode parent, final RrmSimulation simulation, final IFolder calculationFolder )
  {
    final IFeatureBindingCollection<Channel> channels = m_model.getChannels();

    final TreeNode base = new TreeNode( parent, new HydrologyStorageChannelsGroupUiHandler( simulation ), RRM_RESULT.class );
    channels.accept( new IFeatureBindingCollectionVisitor<Channel>()
    {
      @Override
      public void visit( final Channel channel )
      {
        if( channel instanceof StorageChannel )
          base.addChild( toTreeNode( base, (StorageChannel) channel, simulation, calculationFolder ) );
      }
    } );

    return base;
  }

  protected TreeNode toTreeNode( final TreeNode parent, final StorageChannel channel, final RrmSimulation simulation, final IFolder calculationFolder )
  {

    final HydrologyResultReference ref1 = new HydrologyResultReference( calculationFolder, channel, RRM_RESULT.storageFuellvolumen );
    final HydrologyResultReference ref2 = new HydrologyResultReference( calculationFolder, channel, RRM_RESULT.storageSpeicherUeberlauf );

    final HydrologyStorageChannelUiHandler handler = new HydrologyStorageChannelUiHandler( simulation, channel );
    handler.setReferences( ref1, ref2 );

    final TreeNode node = new TreeNode( parent, handler, channel );

    node.addChild( new TreeNode( node, new HydrologyResultReferenceUiHandler( simulation, ref1 ), ref1 ) );
    node.addChild( new TreeNode( node, new HydrologyResultReferenceUiHandler( simulation, ref2 ), ref2 ) );

    return node;
  }

  private TreeNode buildHydrologyCatchments( final TreeNode parent, final RrmSimulation simulation, final IFolder calculationFolder )
  {
    final IFeatureBindingCollection<Catchment> catchments = m_model.getCatchments();

    final TreeNode base = new TreeNode( parent, new HydrologyCatchmentsGroupUiHandler( simulation ), RRM_RESULT.class );
    catchments.accept( new IFeatureBindingCollectionVisitor<Catchment>()
    {
      @Override
      public void visit( final Catchment catchment )
      {
        if( catchment.isGenerateResults() )
          base.addChild( toTreeNode( base, catchment, simulation, calculationFolder ) );
      }
    } );

    return base;
  }

  protected TreeNode toTreeNode( final TreeNode parent, final Catchment catchment, final RrmSimulation simulation, final IFolder calculationFolder )
  {

    final HydrologyResultReference ref1 = new HydrologyResultReference( calculationFolder, catchment, RRM_RESULT.catchmentTemperature );
    final HydrologyResultReference ref2 = new HydrologyResultReference( calculationFolder, catchment, RRM_RESULT.catchmentNiederschlag );
    final HydrologyResultReference ref3 = new HydrologyResultReference( calculationFolder, catchment, RRM_RESULT.catchmentSchneehoehe );
    final HydrologyResultReference ref4 = new HydrologyResultReference( calculationFolder, catchment, RRM_RESULT.catchmentGesamtTeilgebietsQ );
    final HydrologyResultReference ref5 = new HydrologyResultReference( calculationFolder, catchment, RRM_RESULT.catchmentOberflaechenQNatuerlich );
    final HydrologyResultReference ref6 = new HydrologyResultReference( calculationFolder, catchment, RRM_RESULT.catchmentOberflaechenQVersiegelt );
    final HydrologyResultReference ref7 = new HydrologyResultReference( calculationFolder, catchment, RRM_RESULT.catchmentInterflow );
    final HydrologyResultReference ref8 = new HydrologyResultReference( calculationFolder, catchment, RRM_RESULT.catchmentBasisQ );
    final HydrologyResultReference ref9 = new HydrologyResultReference( calculationFolder, catchment, RRM_RESULT.catchmentGrundwasserQ );
    final HydrologyResultReference ref10 = new HydrologyResultReference( calculationFolder, catchment, RRM_RESULT.catchmentGrundwasserstand );
    final HydrologyResultReference ref11 = new HydrologyResultReference( calculationFolder, catchment, RRM_RESULT.catchmentEvapotranspiration );

    final HydrologyCatchmentUiHandler handler = new HydrologyCatchmentUiHandler( simulation, catchment );
    handler.setReferences( ref1, ref2, ref3, ref4, ref5, ref6, ref7, ref8, ref9, ref10, ref11 );

    final TreeNode nodeCatchment = new TreeNode( parent, handler, catchment );

    nodeCatchment.addChild( new TreeNode( nodeCatchment, new HydrologyResultReferenceUiHandler( simulation, ref1 ), ref1 ) );
    nodeCatchment.addChild( new TreeNode( nodeCatchment, new HydrologyResultReferenceUiHandler( simulation, ref2 ), ref2 ) );
    nodeCatchment.addChild( new TreeNode( nodeCatchment, new HydrologyResultReferenceUiHandler( simulation, ref3 ), ref3 ) );
    nodeCatchment.addChild( new TreeNode( nodeCatchment, new HydrologyResultReferenceUiHandler( simulation, ref4 ), ref4 ) );
    nodeCatchment.addChild( new TreeNode( nodeCatchment, new HydrologyResultReferenceUiHandler( simulation, ref5 ), ref5 ) );
    nodeCatchment.addChild( new TreeNode( nodeCatchment, new HydrologyResultReferenceUiHandler( simulation, ref6 ), ref6 ) );
    nodeCatchment.addChild( new TreeNode( nodeCatchment, new HydrologyResultReferenceUiHandler( simulation, ref7 ), ref7 ) );
    nodeCatchment.addChild( new TreeNode( nodeCatchment, new HydrologyResultReferenceUiHandler( simulation, ref8 ), ref8 ) );
    nodeCatchment.addChild( new TreeNode( nodeCatchment, new HydrologyResultReferenceUiHandler( simulation, ref9 ), ref9 ) );
    nodeCatchment.addChild( new TreeNode( nodeCatchment, new HydrologyResultReferenceUiHandler( simulation, ref10 ), ref10 ) );
    nodeCatchment.addChild( new TreeNode( nodeCatchment, new HydrologyResultReferenceUiHandler( simulation, ref11 ), ref11 ) );

    return nodeCatchment;
  }

  private TreeNode buildHydrologyNodes( final TreeNode parent, final RrmSimulation simulation, final IFolder calculationFolder )
  {
    final IFeatureBindingCollection<Node> nodes = m_model.getNodes();

    final TreeNode base = new TreeNode( parent, new HydrologyNodesGroupUiHandler( simulation ), RRM_RESULT.class );
    nodes.accept( new IFeatureBindingCollectionVisitor<Node>()
    {
      @Override
      public void visit( final Node node )
      {
        if( node.isGenerateResults() )
        {
          base.addChild( toTreeNode( base, node, simulation, calculationFolder ) );
        }
      }
    } );

    return base;
  }

  protected TreeNode toTreeNode( final TreeNode parent, final Node hydrologyNode, final RrmSimulation simulation, final IFolder calculationFolder )
  {
    final HydrologyResultReference reference = new HydrologyResultReference( calculationFolder, hydrologyNode, RRM_RESULT.nodeGesamtknotenAbfluss );

    final HydrologyNodeUiHandler handler = new HydrologyNodeUiHandler( simulation, hydrologyNode );
    handler.setReferences( reference );

    final TreeNode node = new TreeNode( parent, handler, hydrologyNode );
    node.addChild( new TreeNode( node, new HydrologyResultReferenceUiHandler( simulation, reference ), reference ) );

    return node;
  }
}
