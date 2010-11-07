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
package org.kalypso.model.hydrology.internal.test;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.junit.Test;
import org.kalypso.commons.java.io.FileUtilities;
import org.kalypso.commons.java.util.zip.ZipUtilities;
import org.kalypso.convert.namodel.optimize.NAOptimizingJob;
import org.kalypso.model.hydrology.internal.simulation.NaModelCalcJob;
import org.kalypso.optimize.IOptimizingJob;
import org.kalypso.optimize.OptimizeMonitor;
import org.kalypso.optimize.OptimizerCalJob;
import org.kalypso.simulation.core.ISimulationDataProvider;
import org.kalypso.simulation.core.ISimulationMonitor;
import org.kalypso.simulation.core.SimulationDataPath;
import org.kalypso.simulation.core.SimulationException;
import org.kalypso.simulation.core.TestSimulationMonitor;
import org.kalypso.simulation.core.internal.queued.ModelspecData;
import org.kalypso.simulation.core.refactoring.local.LocalSimulationDataProvider;
import org.kalypso.simulation.core.util.DefaultSimulationResultEater;

/**
 * Test the optimization run of the na model.
 * 
 * @author Gernot Belger
 */
public class OptimizeTest
{
  @Test
  public void testOptimize( ) throws Exception
  {
    final File tmpDir = FileUtilities.createNewTempDir( "optimizeTest" );
    final File dataDir = new File( tmpDir, "inputData" );
    dataDir.mkdirs();
    final File simulationDir = new File( tmpDir, "simulation" );
    simulationDir.mkdirs();
    final File expectedResultsDir = new File( dataDir, "Ergebnisse/Aktuell_Soll" );

    final ISimulationDataProvider dataProvider = prepareData( dataDir );
    final DefaultSimulationResultEater results = doOptimizeRun( simulationDir, dataProvider );
    checkResults( results, expectedResultsDir );
  }

  private ISimulationDataProvider prepareData( final File dataDir ) throws SimulationException, IOException
  {
    final URL dataLocation = getClass().getResource( "resources/weisseElster_optimize/gmlInput.zip" );
    ZipUtilities.unzip( dataLocation, dataDir );

    final NaModelCalcJob naModelCalcJob = new NaModelCalcJob();
    final ModelspecData modelspec = new ModelspecData( naModelCalcJob.getSpezifikation() );
    final SimulationDataPath[] inputPaths = createInputPaths();
    final URL inputContext = dataDir.toURI().toURL();
    return new LocalSimulationDataProvider( modelspec, inputPaths, inputContext );
  }

  private SimulationDataPath[] createInputPaths( )
  {
    final Collection<SimulationDataPath> pathes = new ArrayList<SimulationDataPath>();

    final String controlFile = ".nacontrol_17.gml"; // Goessnitz

    // Hm, copied from the build.xml.... must be updated if something changes there....
    pathes.add( new SimulationDataPath( "MetaSteuerdaten", ".calculation" ) );
    pathes.add( new SimulationDataPath( "Modell", "modell.gml" ) );
    pathes.add( new SimulationDataPath( "Control", controlFile ) );
    pathes.add( new SimulationDataPath( "Hydrotop", "hydrotop.gml" ) );
    pathes.add( new SimulationDataPath( "Parameter", "parameter.gml" ) );
    pathes.add( new SimulationDataPath( "SceConf", ".sce.xml" ) );

    pathes.add( new SimulationDataPath( "NiederschlagDir", "Niederschlag" ) );
    pathes.add( new SimulationDataPath( "KlimaDir", "Klima" ) );
    pathes.add( new SimulationDataPath( "PegelDir", "Pegel" ) );
    pathes.add( new SimulationDataPath( "ErgebnisDir", "Ergebnisse" ) );
    pathes.add( new SimulationDataPath( "LZSIM_IN", "Anfangswerte" ) );

    return pathes.toArray( new SimulationDataPath[pathes.size()] );
  }

  private DefaultSimulationResultEater doOptimizeRun( final File tmpDir, final ISimulationDataProvider dataProvider ) throws Exception
  {
    final File monitorFile = new File( tmpDir, "monitor.out" );
    final PrintStream monitorOut = new PrintStream( monitorFile );

    try
    {
      final ISimulationMonitor monitor = new TestSimulationMonitor( monitorOut, "Optimize-Test: " );
      final File logFile = new File( tmpDir, "optimizeTest.log" );
      final Logger logger = createLogger( logFile );

      final DefaultSimulationResultEater resultEater = new DefaultSimulationResultEater();

      final IOptimizingJob optimizeJob = new NAOptimizingJob( tmpDir, dataProvider, new OptimizeMonitor( monitor ) );
      final OptimizerCalJob optimizeSimulation = new OptimizerCalJob( logger, optimizeJob );

      optimizeSimulation.run( tmpDir, dataProvider, resultEater, monitor );

      disconnectLogger( logger );

      return resultEater;
    }
    finally
    {
      monitorOut.close();
    }
  }

  private void checkResults( final DefaultSimulationResultEater results, final File expectedResultsDir )
  {
    final File resultsDir = (File) results.getResult( "OUT_ZML" );
    final File optimizeFile = (File) results.getResult( "OUT_OPTIMIZEFILE" );

    final File actualResultsDir = new File( resultsDir, "Aktuell" );

    /* Remove files that connot be compared (change each time) */
    new File( actualResultsDir, "Log/calculation.log" ).delete();
    new File( actualResultsDir, "Log/output.zip" ).delete();

    NaPreprocessingTest.checkkDifferences( expectedResultsDir, actualResultsDir );
  }

  private Logger createLogger( final File logFile ) throws SecurityException, IOException
  {
    final Logger logger = Logger.getAnonymousLogger();
    final Handler[] handlers = logger.getHandlers();
    for( final Handler handler : handlers )
      logger.removeHandler( handler );

    final FileHandler fileHandler = new FileHandler( logFile.getAbsolutePath() );
    logger.addHandler( fileHandler );

    return logger;
  }

  private void disconnectLogger( final Logger logger )
  {
    final Handler[] handlers = logger.getHandlers();
    for( final Handler handler : handlers )
    {
      handler.close();
      logger.removeHandler( handler );
    }
  }
}
