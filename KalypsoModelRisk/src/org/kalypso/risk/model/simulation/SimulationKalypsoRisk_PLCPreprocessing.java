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
package org.kalypso.risk.model.simulation;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.kalypso.risk.i18n.Messages;
import org.kalypso.simulation.core.ISimulation;
import org.kalypso.simulation.core.ISimulationDataProvider;
import org.kalypso.simulation.core.ISimulationMonitor;
import org.kalypso.simulation.core.ISimulationResultEater;
import org.kalypso.simulation.core.SimulationException;

/**
 * @author Dejan Antanaskovic
 * 
 */
public class SimulationKalypsoRisk_PLCPreprocessing implements ISimulation
{
  private final static String INPUT_RASTERMODEL = "RasterModel"; //$NON-NLS-1$

  private final static String INPUT_RASTERFOLDERSOURCEINPUT = "RasterFolderSourceInput"; //$NON-NLS-1$

  private final static String INPUT_RASTERFOLDERSOURCEOUTPUT = "RasterFolderSourceOutput"; //$NON-NLS-1$

  private final static String INPUT_STATUSQUO_RASTERMODEL = "StatusQuoRasterModel"; //$NON-NLS-1$

  private final static String OUTPUT_FOLDER = "OutputFolder"; //$NON-NLS-1$

  /**
   * @see org.kalypso.simulation.core.ISimulation#getSpezifikation()
   */
  @Override
  public URL getSpezifikation( )
  {
    return getClass().getResource( "Specification_PLCPreprocessing.xml" ); //$NON-NLS-1$
  }

  /**
   * @see org.kalypso.simulation.core.ISimulation#run(java.io.File, org.kalypso.simulation.core.ISimulationDataProvider,
   *      org.kalypso.simulation.core.ISimulationResultEater, org.kalypso.simulation.core.ISimulationMonitor)
   */
  @Override
  public void run( final File tmpdir, final ISimulationDataProvider inputProvider, final ISimulationResultEater resultEater, final ISimulationMonitor monitor ) throws SimulationException
  {
    try
    {
      if( inputProvider.hasID( INPUT_STATUSQUO_RASTERMODEL ) )
      {
        final File actualRasterModel = FileUtils.toFile( (URL) inputProvider.getInputForID( INPUT_RASTERMODEL ) );
        final File actualRasterFolderInput = FileUtils.toFile( (URL) inputProvider.getInputForID( INPUT_RASTERFOLDERSOURCEINPUT ) );
        final File actualRasterFolderOutput = FileUtils.toFile( (URL) inputProvider.getInputForID( INPUT_RASTERFOLDERSOURCEOUTPUT ) );
        final List<String> folders = new ArrayList<String>();
        folders.add( "PLC" ); //$NON-NLS-1$
        folders.add( "PLC/statusQuo" ); //$NON-NLS-1$
        folders.add( "PLC/statusQuo/raster" ); //$NON-NLS-1$
        //folders.add( "PLC/statusQuo/raster/input" ); //$NON-NLS-1$
        //folders.add( "PLC/statusQuo/raster/output" ); //$NON-NLS-1$
        folders.add( "PLC/difference" ); //$NON-NLS-1$
        folders.add( "PLC/difference/raster" ); //$NON-NLS-1$
        folders.add( "PLC/difference/raster/output" ); //$NON-NLS-1$
        folders.add( "PLC/final" ); //$NON-NLS-1$
        folders.add( "PLC/final/rrm" ); //$NON-NLS-1$
        folders.add( "PLC/final/risk" ); //$NON-NLS-1$
        folders.add( "PLC/final/risk/raster" ); //$NON-NLS-1$
        folders.add( "PLC/final/risk/raster/output" ); //$NON-NLS-1$
        for( final String folder : folders )
        {
          final File f = new File( tmpdir, folder );
          f.mkdirs();
          final File d = new File( f, "control.ctl" ); //$NON-NLS-1$
          d.createNewFile();
        }
        final File differenceModelFolder = new File( tmpdir, "PLC/difference" ); //$NON-NLS-1$
        final File statusQuoModelFolder = new File( tmpdir, "PLC/statusQuo" ); //$NON-NLS-1$
        final File statusQuoRasterFolderInput = new File( tmpdir, "PLC/statusQuo/raster/input" ); //$NON-NLS-1$
        final File statusQuoRasterFolderOutput = new File( tmpdir, "PLC/statusQuo/raster/output" ); //$NON-NLS-1$
        if( actualRasterFolderInput.exists() )
        {
          FileUtils.moveDirectory( actualRasterFolderInput, statusQuoRasterFolderInput );
        }
        else
        {
          Logger.getAnonymousLogger().log( Level.WARNING, Messages.getString( "SimulationKalypsoRisk_PLCPreprocessing_0" ) ); //$NON-NLS-1$
        }
        if( actualRasterFolderOutput.exists() )
        {
          FileUtils.moveDirectory( actualRasterFolderOutput, statusQuoRasterFolderOutput );
        }
        else
        {
          Logger.getAnonymousLogger().log( Level.WARNING, Messages.getString( "SimulationKalypsoRisk_PLCPreprocessing_2" ) ); //$NON-NLS-1$
        }
        if( actualRasterModel.exists() )
        {
          FileUtils.copyFileToDirectory( actualRasterModel, statusQuoModelFolder );
          FileUtils.copyFileToDirectory( actualRasterModel, differenceModelFolder );
        }
        else
        {
          Logger.getAnonymousLogger().log( Level.WARNING, Messages.getString( "SimulationKalypsoRisk_PLCPreprocessing_1" ) ); //$NON-NLS-1$
        }
      }
    }
    catch( final IOException e )
    {
      throw new SimulationException( "Vorbereiten der Risikodifferenzen fehlgeschlagen.", e );
    }
    resultEater.addResult( OUTPUT_FOLDER, tmpdir );
  }

}