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
package org.kalypso.model.hydrology.internal.preprocessing.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.kalypso.contribs.java.net.UrlUtilities;
import org.kalypso.contribs.java.util.FortranFormatHelper;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.model.hydrology.NaModelConstants;
import org.kalypso.model.hydrology.binding.NAControl;
import org.kalypso.model.hydrology.binding.NAOptimize;
import org.kalypso.model.hydrology.binding.model.Catchment;
import org.kalypso.model.hydrology.binding.model.Channel;
import org.kalypso.model.hydrology.internal.i18n.Messages;
import org.kalypso.model.hydrology.internal.preprocessing.NAPreprocessorException;
import org.kalypso.model.hydrology.internal.preprocessing.net.NetElement;
import org.kalypso.model.hydrology.internal.preprocessing.timeseries.Ext2Writer;
import org.kalypso.model.hydrology.internal.preprocessing.timeseries.GrapWriter;
import org.kalypso.model.hydrology.internal.preprocessing.timeseries.RangeFactor;
import org.kalypso.ogc.sensor.DateRange;
import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.ITupleModel;
import org.kalypso.ogc.sensor.ObservationUtilities;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.metadata.ITimeseriesConstants;
import org.kalypso.ogc.sensor.zml.ZmlFactory;
import org.kalypso.ogc.sensor.zml.ZmlURL;
import org.kalypso.zml.obslink.TimeseriesLinkType;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree.model.feature.IFeatureBindingCollection;

/**
 * @author Gernot Belger
 */
public class TsFileWriter
{
  private final static String FILTER_T = "<filter><intervallFilter amount=\"24\" calendarField=\"HOUR_OF_DAY\" mode=\"intensity\" xmlns=\"filters.zml.kalypso.org\"/></filter>"; //$NON-NLS-1$

  private final static String FILTER_V = FILTER_T;

  private final Logger m_logger;

  private final URL m_zmlContext;

  private final NetElement[] m_channels;

  private final TimeseriesFileManager m_tsFileManager;

  private final GMLWorkspace m_synthNWorkspace;

  private final NAControl m_metaControl;

  private final NAOptimize m_naOptimize;

  public TsFileWriter( final GMLWorkspace synthNWorkspace, final NAControl naControl, final NAOptimize naOptimize, final NetElement[] channels, final URL zmlContext, final TimeseriesFileManager tsFileManager, final Logger logger )
  {
    m_synthNWorkspace = synthNWorkspace;
    m_metaControl = naControl;
    m_naOptimize = naOptimize;
    m_channels = channels;
    m_zmlContext = zmlContext;
    m_tsFileManager = tsFileManager;
    m_logger = logger;
  }

  public void write( final File targetDir ) throws NAPreprocessorException
  {
    for( final NetElement netElement : m_channels )
    {
      final Channel channel = netElement.getChannel();
      generateTimeSeries( channel, targetDir );
    }
  }

  private void generateTimeSeries( final Channel channel, final File klimaDir ) throws NAPreprocessorException
  {
    final Date simulationStart = m_metaControl.getSimulationStart();
    final Date simulationEnd = m_metaControl.getSimulationEnd();

    final Catchment[] catchments = channel.findCatchments();

    for( final Catchment catchment : catchments )
    {
      try
      {
        writeCatchmentTimeseries( klimaDir, simulationStart, simulationEnd, catchment );
      }
      catch( final Exception e )
      {
        e.printStackTrace();
        final String message = String.format( "Fehler beim Schreiben der Zeitreihen für Teilgebiet '%s'", catchment.getName() );
        throw new NAPreprocessorException( message, e );
      }
    }
  }

  private void writeCatchmentTimeseries( final File klimaDir, final Date simulationStart, final Date simulationEnd, final Catchment catchment ) throws SensorException, IOException
  {
    final File targetFileN = m_tsFileManager.getNiederschlagEingabeDatei( catchment, klimaDir ); //$NON-NLS-1$

    if( m_metaControl.isUsePrecipitationForm() )
    {
      if( !targetFileN.exists() )
        writeSynthNFile( targetFileN, catchment );
    }
    else
    {
      if( !targetFileN.exists() )
        writeNFile( targetFileN, catchment );

      final TimeseriesLinkType linkT = catchment.getTemperatureLink();
      final File targetFileT = m_tsFileManager.getTemperaturEingabeDatei( catchment, klimaDir ); //$NON-NLS-1$
      writeTimeseries( targetFileT, linkT, m_zmlContext, ITimeseriesConstants.TYPE_TEMPERATURE, FILTER_T, "1.0", simulationStart, simulationEnd );

      final TimeseriesLinkType linkV = catchment.getEvaporationLink();
      final File targetFileV = m_tsFileManager.getVerdunstungEingabeDatei( catchment, klimaDir ); //$NON-NLS-1$
      writeTimeseries( targetFileV, linkV, m_zmlContext, ITimeseriesConstants.TYPE_EVAPORATION, FILTER_V, "0.5", simulationStart, simulationEnd );
    }
  }

  private void writeNFile( final File targetFileN, final Catchment catchment ) throws SensorException, IOException
  {
    final TimeseriesLinkType linkN = catchment.getPrecipitationLink();

    final IObservation observation = loadObservationWithFilter( linkN, m_zmlContext, null );
    if( observation == null )
      return;

    final StringBuffer writer = new StringBuffer();

    final GrapWriter grapWriter = new GrapWriter( ITimeseriesConstants.TYPE_RAINFALL, observation );

    final RangeFactor forecastFactor = createForecastFactor( catchment );
    grapWriter.setRangeFactor( forecastFactor );

    grapWriter.write( writer );

    FileUtils.writeStringToFile( targetFileN, writer.toString() );
  }

  /**
   * Very specialized: we have a special factor for precipitation in the forecast date range.<br/>
   * This makes actually only sense for highwater prediction.
   */
  private RangeFactor createForecastFactor( final Catchment catchment )
  {
    final Date startForecast = m_metaControl.getStartForecast();
    if( startForecast == null )
      return null;

    final Date simulationEnd = m_metaControl.getSimulationEnd();
    if( startForecast.equals( simulationEnd ) )
      return null;

    if( m_naOptimize == null )
      return null;

    final IFeatureBindingCollection<Catchment> catchmentCollection = m_naOptimize.getCatchmentCollection();
    if( !catchmentCollection.contains( catchment ) )
      return null;

    final double faktn = catchment.getFaktn();

    final double faktnPrognose = m_naOptimize.getFaktnPrognose();
    // THE BIG HACK: We always devide by the catchment factor, else we would get two factors: the catchment factor
    // (applied by kalypso-na.exe) AND the forecast factor. But we only want one factor.
    final double forcastFactorN = faktnPrognose / faktn;

    final DateRange forecastRange = new DateRange( startForecast, simulationEnd );
    return new RangeFactor( forecastRange, forcastFactorN );
  }

  public static final void writeTimeseries( final File targetFile, final TimeseriesLinkType link, final URL zmlContext, final String valueAxisType, final String filter, final String defaultValue, final Date simulationStart, final Date simulationEnd ) throws SensorException, IOException
  {
    if( targetFile.exists() )
      return;

    final IObservation observation = loadObservationWithFilter( link, zmlContext, filter );
    if( observation == null )
      return;

    final StringBuffer writer = new StringBuffer();

    final Ext2Writer extWriter = new Ext2Writer( simulationStart, simulationEnd );
    extWriter.write( observation, valueAxisType, writer, defaultValue );

    FileUtils.writeStringToFile( targetFile, writer.toString() );
  }

  private static IObservation loadObservationWithFilter( final TimeseriesLinkType link, final URL zmlContext, final String filter ) throws MalformedURLException, SensorException
  {
    if( link == null )
      return null;

    final String href = link.getHref();
    final String hrefWithFilter = filter == null ? href : ZmlURL.insertFilter( href, filter );

    final URL location = new UrlUtilities().resolveURL( zmlContext, hrefWithFilter );

    final IObservation observation = ZmlFactory.parseXML( location ); //$NON-NLS-1$
    return observation;
  }

  // FIXME: does not belong here -> move into own writer
  private void writeSynthNFile( final File targetFileN, final Catchment catchment ) throws SensorException, IOException
  {
    final List<Feature> statNList = new ArrayList<Feature>();

    final StringBuffer buffer = new StringBuffer();
    final Double annualityKey = m_metaControl.getAnnuality();
    // Kostra-Kachel/ synth. N gebietsabängig
    final String synthNKey = catchment.getSynthZR();

    final IFeatureType syntNft = m_synthNWorkspace.getGMLSchema().getFeatureType( NaModelConstants.SYNTHN_STATN_FT );
    statNList.addAll( Arrays.asList( m_synthNWorkspace.getFeatures( syntNft ) ) );

    // Performance & readability: linear search loop; first hash the snyt-definitions; then write....

    for( final Feature statNFE : statNList )
    {
      if( statNFE.getName() != null )
      {
        if( statNFE.getName().equals( synthNKey ) )
        {
          final List< ? > statNParameterList = (List< ? >) statNFE.getProperty( NaModelConstants.STATNPARA_MEMBER );
          for( final Object object : statNParameterList )
          {
            final Catchment fe = (Catchment) object;
            final String annuality = Double.toString( 1d / (Double) fe.getProperty( NaModelConstants.STATN_PROP_XJAH ) );
            if( annuality.equals( annualityKey.toString() ) )
            {
              final IObservation tnProp = (IObservation) fe.getProperty( NaModelConstants.STATN_PROP_STATN_DIAG );
              if( tnProp != null )
              {
                final IObservation observation = tnProp;
                final IAxis[] axisList = observation.getAxisList();
                final IAxis minutesAxis = ObservationUtilities.findAxisByType( axisList, ITimeseriesConstants.TYPE_MIN );
                final IAxis precipitationAxis = ObservationUtilities.findAxisByType( axisList, ITimeseriesConstants.TYPE_RAINFALL );
                buffer.append( FortranFormatHelper.printf( annualityKey, "f6.3" ) + " " + "1" + "\n" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                final ITupleModel values = observation.getValues( null );
                final int count = values.size();
                // if( count > 20 )
                // throw new Exception( "Fehler!!! NA-Modell: Anzahl Wertepaare synth Niederschlag > maximale Anzahl
                // (20) \n Niederschlag:" + synthNKey + "\n Wiederkehrwahrscheinlichkeit: "
                // + annualityKey );
                for( int row = 0; row < count; row++ )
                {
                  final Double minutesValue = (Double) values.get( row, minutesAxis );
                  final Double hoursValue = minutesValue / 60d;
                  if( hoursValue.equals( m_metaControl.getDurationHours() ) )
                  {
                    final Double precipitationValue = (Double) values.get( row, precipitationAxis );
                    buffer.append( FortranFormatHelper.printf( hoursValue, "f9.3" ) + " " + FortranFormatHelper.printf( precipitationValue, "*" ) + "\n" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                  }
                }

                // FIXME: highly dubious: the same buffer is written again and again, but never cleared during the
                // loop... is this correct?

                final FileWriter writer = new FileWriter( targetFileN );
                writer.write( buffer.toString() );
                IOUtils.closeQuietly( writer );
              }
              else
              {
                final String msg = Messages.getString( "org.kalypso.convert.namodel.manager.CatchmentManager.143", synthNKey, annualityKey ); //$NON-NLS-1$
                m_logger.log( Level.WARNING, msg );
              }
            }
          }
        }
      }
    }
  }

}
