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
package org.kalypso.model.wspm.tuhh.ui.export.wspwin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.kalypso.chart.ui.editor.commandhandler.ChartHandlerUtilities;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.model.wspm.tuhh.core.wspwin.LengthSectionExporter;
import org.kalypso.model.wspm.tuhh.ui.i18n.Messages;
import org.kalypso.observation.IObservation;
import org.kalypso.observation.result.TupleResult;
import org.kalypso.wspwin.core.Plotter;

import de.openali.odysseus.chart.framework.model.IChartModel;
import de.openali.odysseus.chart.framework.model.layer.ILayerManager;
import de.openali.odysseus.chart.framework.view.IChartComposite;

/**
 * @author kimwerner
 */
public class LengthSectionExportHandler extends AbstractHandler
{
  @Override
  public Object execute( final ExecutionEvent event ) throws ExecutionException
  {
    final Shell shell = HandlerUtil.getActiveShellChecked( event );

    final IEvaluationContext context = (IEvaluationContext) event.getApplicationContext();
    final IChartComposite chart = ChartHandlerUtilities.getChartChecked( context );

    final IObservation<TupleResult> obs = getLSObservation( chart );

    if( !Plotter.checkPlotterExe( shell ) )
      return null;

    try
    {
      doExport( obs );
    }
    catch( final Exception e )
    {
      e.printStackTrace();

      final IStatus error = StatusUtilities.statusFromThrowable( e );
      ErrorDialog.openError( shell, Messages.getString( "LengthSectionExportHandler_0" ), Messages.getString( "LengthSectionExportHandler_1" ), error ); //$NON-NLS-1$ //$NON-NLS-2$
    }

    return null;
  }

  private void doExport( final IObservation<TupleResult> obs ) throws IOException
  {
    final File file = new File( System.getProperty( "java.io.tmpdir" ), "exportTmp.lng" );//$NON-NLS-1$ //$NON-NLS-2$
    file.deleteOnExit();
    final LengthSectionExporter lngExp = new LengthSectionExporter();
    final PrintWriter writer = new PrintWriter( new FileOutputStream( file ) );
    try
    {
      if( !lngExp.write( obs, writer ) )
        throw new IOException( Messages.getString( "LengthSectionExportHandler_2" ) ); //$NON-NLS-1$
    }
    finally
    {
      writer.close();
    }
    // FIXME: get from user
    final boolean doPrint = false;
    Plotter.openPrf( file, doPrint );
  }

  static IObservation<TupleResult> getLSObservation( final IChartComposite chart )
  {
    final IChartModel chartModel = chart.getChartModel();
    final ILayerManager layerManager = chartModel.getLayerManager();
    final LengthSectionExportVisitor visitor = new LengthSectionExportVisitor();
    layerManager.accept( visitor );
    return visitor.getObservation();
  }
}
