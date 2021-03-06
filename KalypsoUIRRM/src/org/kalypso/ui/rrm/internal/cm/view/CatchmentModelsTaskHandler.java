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
package org.kalypso.ui.rrm.internal.cm.view;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.kalypso.afgui.KalypsoAFGUIFrameworkPlugin;
import org.kalypso.model.hydrology.binding.cm.ICatchmentModel;
import org.kalypso.model.hydrology.binding.timeseriesMappings.ITimeseriesMappingCollection;
import org.kalypso.ogc.gml.mapmodel.CommandableWorkspace;
import org.kalypso.ui.rrm.internal.IUiRrmWorkflowConstants;
import org.kalypso.ui.rrm.internal.utils.featureTree.TreePropertiesView;

import de.renew.workflow.connector.cases.IScenarioDataProvider;

/**
 * @author Gernot Belger
 */
public class CatchmentModelsTaskHandler extends AbstractHandler
{
  @Override
  public Object execute( final ExecutionEvent event ) throws ExecutionException
  {
    final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked( event );
    final IWorkbenchPage page = window.getActivePage();

    final CatchmentModelsView managementView = (CatchmentModelsView) page.findView( CatchmentModelsView.ID );
    if( managementView == null )
      throw new ExecutionException( "Failed to access timeseries view" ); //$NON-NLS-1$

    /* Hook properties view and management view */
    final TreePropertiesView propertiesView = (TreePropertiesView) page.findView( TreePropertiesView.ID );
    if( propertiesView == null )
      throw new ExecutionException( "Failed to access properties view" ); //$NON-NLS-1$

    final ISelectionProvider selectionProvider = managementView.getViewSite().getSelectionProvider();
    propertiesView.hookSelection( selectionProvider );

    try
    {
      final IScenarioDataProvider modelProvider = KalypsoAFGUIFrameworkPlugin.getDataProvider();

      final CommandableWorkspace generatorsWorkspace = modelProvider.getCommandableWorkSpace( IUiRrmWorkflowConstants.SCENARIO_DATA_CATCHMENT_MODELS );
      final CommandableWorkspace mappingsWorkspace = modelProvider.getCommandableWorkSpace( IUiRrmWorkflowConstants.SCENARIO_DATA_TIMESERIES_MAPPINGS );

      final ICatchmentModel catchmentModel = modelProvider.getModel( IUiRrmWorkflowConstants.SCENARIO_DATA_CATCHMENT_MODELS );
      final ITimeseriesMappingCollection timeseriesMappings = modelProvider.getModel( IUiRrmWorkflowConstants.SCENARIO_DATA_TIMESERIES_MAPPINGS );

      managementView.setInput( catchmentModel, timeseriesMappings, generatorsWorkspace, mappingsWorkspace );
    }
    catch( final CoreException e )
    {
      e.printStackTrace();
      throw new ExecutionException( "Failed to initialize timeseries view", e ); //$NON-NLS-1$
    }

    return null;
  }
}