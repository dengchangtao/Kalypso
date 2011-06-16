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
package org.kalypso.model.hydrology.internal.simulation.planerclient.global_preprocessing;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.kalypso.simulation.core.AbstractInternalStatusJob;
import org.kalypso.simulation.core.ISimulation;
import org.kalypso.simulation.core.ISimulationDataProvider;
import org.kalypso.simulation.core.ISimulationMonitor;
import org.kalypso.simulation.core.ISimulationResultEater;
import org.kalypso.simulation.core.SimulationException;

/**
 * @author Dejan Antanaskovic
 */
public class NA_GlobalPreprocessingJob extends AbstractInternalStatusJob implements ISimulation
{
  private static final String INPUT_PARAMETER_MODEL = "parametersModel"; //$NON-NLS-1$

  private static final String OUTPUT_SUDS_MODEL = "sudsModel"; //$NON-NLS-1$

  private static final String OUTPUT_SUDS_FOLDER = "SudsFolder"; //$NON-NLS-1$

  @Override
  public URL getSpezifikation( )
  {
    return getClass().getResource( "resources/modelSpecification.xml" ); //$NON-NLS-1$
  }

  @Override
  public void run( final File tmpdir, final ISimulationDataProvider inputProvider, final ISimulationResultEater resultEater, final ISimulationMonitor monitor ) throws SimulationException
  {
    try
    {
      final File parametersFile = FileUtils.toFile( (URL) inputProvider.getInputForID( INPUT_PARAMETER_MODEL ) );
      final URL sudsURL = getClass().getResource( "resources/suds.gml" ); //$NON-NLS-1$
      final File sudsFile = File.createTempFile( "suds", ".gml", tmpdir ); //$NON-NLS-1$ //$NON-NLS-2$
      FileUtils.copyURLToFile( sudsURL, sudsFile );

      final File sudsFolder = new File( tmpdir, "suds" ); //$NON-NLS-1$
      sudsFolder.mkdirs();
      final String[] folders = new String[] { "greenroof", "swale", "swale-infiltration-ditch" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      for( final String folderName : folders )
      {
        final File folder = new File( sudsFolder, folderName );
        folder.mkdirs();
        FileUtils.copyFileToDirectory( parametersFile, folder );
      }

      resultEater.addResult( OUTPUT_SUDS_MODEL, sudsFile );
      resultEater.addResult( OUTPUT_SUDS_FOLDER, sudsFolder );
    }
    catch( final IOException e )
    {
      setStatus( STATUS.ERROR, "Error creating data structure." ); //$NON-NLS-1$
      return;
    }
    setStatus( STATUS.OK, "Success" ); //$NON-NLS-1$
  }
}