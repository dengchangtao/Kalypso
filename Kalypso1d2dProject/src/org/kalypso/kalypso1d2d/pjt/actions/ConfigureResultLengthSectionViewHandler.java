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
package org.kalypso.kalypso1d2d.pjt.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchWindow;
import org.kalypso.afgui.model.ICommandPoster;
import org.kalypso.commons.command.EmptyCommand;
import org.kalypso.contribs.eclipse.jface.wizard.WizardDialog2;
import org.kalypso.kalypso1d2d.pjt.i18n.Messages;
import org.kalypso.kalypsomodel1d2d.schema.binding.result.IScenarioResultMeta;
import org.kalypso.ogc.gml.GisTemplateMapModell;
import org.kalypso.ogc.gml.map.IMapPanel;
import org.kalypso.ogc.gml.mapmodel.IMapModell;
import org.kalypso.ogc.gml.mapmodel.MapModellHelper;
import org.kalypso.ui.views.map.MapView;
import org.kalypso.ui.wizards.lengthsection.ConfigureLengthSectionWizard;
import org.kalypsodeegree.model.feature.binding.IFeatureWrapper2;

import de.renew.workflow.connector.cases.CaseHandlingSourceProvider;
import de.renew.workflow.connector.cases.ICaseDataProvider;
import de.renew.workflow.contexts.ICaseHandlingSourceProvider;

/**
 * @author Thomas Jung
 */
public class ConfigureResultLengthSectionViewHandler extends AbstractHandler
{
  /**
   * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
   */
  @Override
  @SuppressWarnings("unchecked")
  public Object execute( final ExecutionEvent event ) throws ExecutionException
  {
    final IEvaluationContext context = (IEvaluationContext) event.getApplicationContext();
    final Shell shell = (Shell) context.getVariable( ISources.ACTIVE_SHELL_NAME );
    final ICaseDataProvider<IFeatureWrapper2> modelProvider = (ICaseDataProvider<IFeatureWrapper2>) context.getVariable( CaseHandlingSourceProvider.ACTIVE_CASE_DATA_PROVIDER_NAME );
    final IFolder scenarioFolder = (IFolder) context.getVariable( ICaseHandlingSourceProvider.ACTIVE_CASE_FOLDER_NAME );

    /* Get the map */
    final IWorkbenchWindow window = (IWorkbenchWindow) context.getVariable( ISources.ACTIVE_WORKBENCH_WINDOW_NAME );
    final MapView mapView = (MapView) window.getActivePage().findView( MapView.ID );
    final IMapPanel mapPanel = mapView.getMapPanel();

    /* wait for map to load */
    if( !MapModellHelper.waitForAndErrorDialog( shell, mapPanel, Messages.getString("org.kalypso.kalypso1d2d.pjt.actions.ConfigureResultLengthSectionViewHandler.0"), Messages.getString("org.kalypso.kalypso1d2d.pjt.actions.ConfigureResultLengthSectionViewHandler.1") ) ) //$NON-NLS-1$ //$NON-NLS-2$
      return null;

    final IMapModell orgMapModell = mapPanel.getMapModell();

    if( !(orgMapModell instanceof GisTemplateMapModell) )
      throw new ExecutionException( Messages.getString("org.kalypso.kalypso1d2d.pjt.actions.ConfigureResultLengthSectionViewHandler.2") ); //$NON-NLS-1$

    try
    {
      final IScenarioResultMeta resultModel = modelProvider.getModel( IScenarioResultMeta.class );

      final IMapPanel panel = mapView.getMapPanel();

      // open wizard
      final ConfigureLengthSectionWizard wizard = new ConfigureLengthSectionWizard( scenarioFolder, resultModel, panel );
      final WizardDialog2 wizardDialog2 = new WizardDialog2( shell, wizard );
      if( wizardDialog2.open() == Window.OK )
      {
        ((ICommandPoster) modelProvider).postCommand( IScenarioResultMeta.class.getName(), new EmptyCommand( "", false ) ); //$NON-NLS-1$
        modelProvider.saveModel( IScenarioResultMeta.class.getName(), new NullProgressMonitor() );
//        modelProvider.saveModel( IScenarioResultMeta.class, new NullProgressMonitor() );

        return Status.OK_STATUS;
      }
    }
    catch( final CoreException e )
    {
      e.printStackTrace();
    }
    catch( InvocationTargetException e )
    {
      e.printStackTrace();
    }

    return Status.CANCEL_STATUS;
  }

}