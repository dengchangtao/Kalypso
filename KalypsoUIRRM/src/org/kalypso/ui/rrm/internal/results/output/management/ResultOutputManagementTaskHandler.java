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
package org.kalypso.ui.rrm.internal.results.output.management;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.kalypso.afgui.scenarios.ScenarioHelper;
import org.kalypso.afgui.scenarios.SzenarioDataProvider;
import org.kalypso.featureview.views.FeatureView;
import org.kalypso.ogc.gml.featureview.maker.CachedFeatureviewFactory;
import org.kalypso.ui.rrm.internal.i18n.Messages;
import org.kalypso.ui.rrm.internal.utils.WorkflowHandlerUtils;

/**
 * @author Gernot Belger
 */
public class ResultOutputManagementTaskHandler extends AbstractHandler
{
  @Override
  public Object execute( final ExecutionEvent event ) throws ExecutionException
  {
    final IEvaluationContext context = (IEvaluationContext) event.getApplicationContext();

    /* Get the map */
    final IWorkbenchWindow window = (IWorkbenchWindow) context.getVariable( ISources.ACTIVE_WORKBENCH_WINDOW_NAME );
    final IWorkbenchPage activePage = window.getActivePage();

    configureFeatureView( activePage );

    /* set input to gtt tables */
    try
    {
      final SzenarioDataProvider dataProvider = ScenarioHelper.getScenarioDataProvider();
      final IFolder scenarioFolder = (IFolder) dataProvider.getScenarioFolder();

      WorkflowHandlerUtils.setGttInput( activePage, "KMChannels", "urn:org.kalypso.model.rrm.editCalibration:workflow:KMChannels:gtt", Messages.getString( "EditCalibrationTaskHandler_0" ), scenarioFolder ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      WorkflowHandlerUtils.setGttInput( activePage, "StorageChannels", "urn:org.kalypso.model.rrm.editCalibration:workflow:StorageChannels:gtt", Messages.getString( "EditCalibrationTaskHandler_1" ), scenarioFolder ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      WorkflowHandlerUtils.setGttInput( activePage, "Catchments", "urn:org.kalypso.model.rrm.editCalibration:workflow:Catchments:gtt", Messages.getString( "EditCalibrationTaskHandler_2" ), scenarioFolder ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    catch( final CoreException e )
    {
      e.printStackTrace();
      throw new ExecutionException( "Failed ot initialize tables", e ); //$NON-NLS-1$
    }

    return null;
  }

  /** Configure feature view with specialized templates */
  private void configureFeatureView( final IWorkbenchPage page )
  {
    final IViewPart part = page.findView( FeatureView.ID );
    if( !(part instanceof FeatureView) )
      return;

    final FeatureView featureView = (FeatureView) part;
    final CachedFeatureviewFactory factory = featureView.getCachedFeatureViewFactory();

    factory.addView( getClass().getResource( "/org/kalypso/ui/rrm/catalog/resources/EditCalibration_Catchment.gft" ) ); //$NON-NLS-1$
    factory.addView( getClass().getResource( "/org/kalypso/ui/rrm/catalog/resources/EditCalibration_KMChannel.gft" ) ); //$NON-NLS-1$
    factory.addView( getClass().getResource( "/org/kalypso/ui/rrm/catalog/resources/EditCalibration_RHBChannel.gft" ) ); //$NON-NLS-1$
  }
}