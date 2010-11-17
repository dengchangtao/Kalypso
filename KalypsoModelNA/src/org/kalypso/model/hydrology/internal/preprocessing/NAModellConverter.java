/*--------------- Kalypso-Header --------------------------------------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 Technical University Hamburg-Harburg (TUHH)
 Institute of River and coastal engineering
 Denickestr. 22
 21073 Hamburg, Germany
 http://www.tuhh.de/wb

 and

 Bjoernsen Consulting Engineers (BCE)
 Maria Trost 3
 56070 Koblenz, Germany
 http://www.bjoernsen.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 E-Mail:
 belger@bjoernsen.de
 schlienger@bjoernsen.de
 v.doemming@tuhh.de

 ---------------------------------------------------------------------------------------------------*/
package org.kalypso.model.hydrology.internal.preprocessing;

import java.util.Map.Entry;
import java.util.logging.Logger;

import org.kalypso.convert.namodel.NAConfiguration;
import org.kalypso.convert.namodel.NaSimulationData;
import org.kalypso.convert.namodel.manager.IDManager;
import org.kalypso.convert.namodel.net.NetElement;
import org.kalypso.model.hydrology.binding.NAControl;
import org.kalypso.model.hydrology.binding.NAHydrotop;
import org.kalypso.model.hydrology.binding.initialValues.InitialValues;
import org.kalypso.model.hydrology.binding.model.Catchment;
import org.kalypso.model.hydrology.binding.model.NaModell;
import org.kalypso.model.hydrology.binding.parameter.Parameter;
import org.kalypso.model.hydrology.internal.NaAsciiDirs;
import org.kalypso.model.hydrology.internal.preprocessing.hydrotope.HydroHash;
import org.kalypso.model.hydrology.internal.preprocessing.writer.BodenartWriter;
import org.kalypso.model.hydrology.internal.preprocessing.writer.BodentypWriter;
import org.kalypso.model.hydrology.internal.preprocessing.writer.GebWriter;
import org.kalypso.model.hydrology.internal.preprocessing.writer.GerWriter;
import org.kalypso.model.hydrology.internal.preprocessing.writer.HRBFileWriter;
import org.kalypso.model.hydrology.internal.preprocessing.writer.LzsimWriter;
import org.kalypso.model.hydrology.internal.preprocessing.writer.NetFileWriter;
import org.kalypso.model.hydrology.internal.preprocessing.writer.NutzungWriter;
import org.kalypso.model.hydrology.internal.preprocessing.writer.RhbWriter;
import org.kalypso.model.hydrology.internal.preprocessing.writer.SchneeManager;
import org.kalypso.model.hydrology.internal.preprocessing.writer.SudsFileWriter;
import org.kalypso.model.hydrology.internal.preprocessing.writer.TimeseriesFileManager;
import org.kalypso.model.hydrology.internal.preprocessing.writer.ZftWriter;
import org.kalypsodeegree.model.feature.GMLWorkspace;

/**
 * Import kalypso rainfall runoff models converts between custom ascii format and gml format. Export to ascii can be
 * generated from a gml file or from a gml workspace.
 * 
 * @author doemming
 */
public class NAModellConverter
{
  private final NAConfiguration m_conf;

  private final Logger m_logger;

  private final NaSimulationData m_data;

  private final NaAsciiDirs m_asciiDirs;

  public NAModellConverter( final NAConfiguration conf, final NaSimulationData data, final NaAsciiDirs asciiDirs, final Logger logger ) throws Exception
  {
    m_conf = conf;
    m_data = data;
    m_asciiDirs = asciiDirs;
    m_logger = logger;
  }

  public void writeUncalibratedFiles( final RelevantNetElements relevantElements, final TimeseriesFileManager tsFileManager, final HydroHash hydroHash ) throws Exception
  {
    final GMLWorkspace modelWorkspace = m_data.getModelWorkspace();
    final NaModell naModel = (NaModell) modelWorkspace.getRootFeature();
    final NAHydrotop hydrotopeCollection = m_data.getHydrotopCollection();
    final GMLWorkspace sudsWorkspace = m_data.getSudsWorkspace();
    final IDManager idManager = m_conf.getIdManager();
    final GMLWorkspace parameterWorkspace = m_data.getParameterWorkspace();
    final Parameter parameter = (Parameter) parameterWorkspace.getRootFeature();

    final NetElement[] channels = relevantElements.getChannelsSorted( idManager );
    final Catchment[] catchments = relevantElements.getCatchmentsSorted( idManager );

    final NetFileWriter netWriter = new NetFileWriter( m_conf, relevantElements, tsFileManager, idManager, modelWorkspace, m_logger );
    netWriter.write( m_conf.getNetFile() );

    final ZftWriter zftWriter = new ZftWriter( idManager, m_logger, catchments );
    zftWriter.write( m_conf.getZFTFile() );

    final RhbWriter rhbWriter = new RhbWriter( idManager, channels, m_logger );
    rhbWriter.write( m_conf.getRHBFile() );

    final BodenartWriter bodenartManager = new BodenartWriter( parameterWorkspace, m_logger );
    bodenartManager.write( m_conf.getBodenartFile() );

    final BodentypWriter bodentypManager = new BodentypWriter( parameterWorkspace, m_logger );
    bodentypManager.write( m_conf.getBodentypFile() );

    final SchneeManager schneeManager = new SchneeManager( parameterWorkspace, m_logger );
    schneeManager.write( m_conf.getSchneeFile() );

    final NutzungWriter nutzungManager = new NutzungWriter( m_conf.getNutzungDir() );
    nutzungManager.writeFile( parameter, hydroHash );

    final SudsFileWriter sudsFileWriter = new SudsFileWriter( naModel, hydrotopeCollection, sudsWorkspace, m_logger );
    sudsFileWriter.write( m_conf.getSwaleAndTrenchFile() );

    final HRBFileWriter hrbFileWriter = new HRBFileWriter( naModel.getStorageChannels(), m_conf, m_logger );
    hrbFileWriter.write( m_conf.getHRBFile() );

    final NAControl metaControl = m_data.getMetaControl();
    final InitialValues initialValues = m_data.getInitialValues();
    final LzsimWriter lzsimWriter = new LzsimWriter( idManager, hydroHash, initialValues, metaControl, m_logger );
    lzsimWriter.writeLzsimFiles( m_asciiDirs.lzsimDir );
  }

  public void writeCalibratedFiles( final RelevantNetElements relevantElements, final TimeseriesFileManager tsFileManager ) throws Exception
  {
    final GMLWorkspace modelWorkspace = m_conf.getModelWorkspace();
    final NaModell naModel = (NaModell) modelWorkspace.getRootFeature();
    final IDManager idManager = m_conf.getIdManager();

    final Entry<NetElement, Integer>[] rootChannels = relevantElements.getRootChannels();
    final NetElement[] channels = relevantElements.getChannelsSorted( idManager );
    final Catchment[] catchments = relevantElements.getCatchmentsSorted( idManager );

    /* Write files that are changed by calibration factors */
    final GerWriter gerWriter = new GerWriter( idManager, rootChannels, channels, m_logger );
    gerWriter.write( m_conf.getChannelFile() );

    final GebWriter catchmentManager = new GebWriter( m_conf, m_logger, catchments, naModel, tsFileManager );
    catchmentManager.write( m_conf.getCatchmentFile() );
  }
}
