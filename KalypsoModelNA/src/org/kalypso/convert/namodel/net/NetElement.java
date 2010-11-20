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
package org.kalypso.convert.namodel.net;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.kalypso.contribs.java.net.UrlUtilities;
import org.kalypso.contribs.java.util.FortranFormatHelper;
import org.kalypso.convert.namodel.manager.IDManager;
import org.kalypso.convert.namodel.net.visitors.NetElementVisitor;
import org.kalypso.convert.namodel.timeseries.NAZMLGenerator;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.model.hydrology.NaModelConstants;
import org.kalypso.model.hydrology.binding.NAControl;
import org.kalypso.model.hydrology.binding.model.Catchment;
import org.kalypso.model.hydrology.binding.model.Channel;
import org.kalypso.model.hydrology.binding.model.Node;
import org.kalypso.model.hydrology.internal.i18n.Messages;
import org.kalypso.model.hydrology.internal.preprocessing.RelevantNetElements;
import org.kalypso.model.hydrology.internal.preprocessing.writer.TimeseriesFileManager;
import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.ITupleModel;
import org.kalypso.ogc.sensor.ObservationUtilities;
import org.kalypso.ogc.sensor.metadata.ITimeseriesConstants;
import org.kalypso.ogc.sensor.zml.ZmlFactory;
import org.kalypso.ogc.sensor.zml.ZmlURL;
import org.kalypso.zml.obslink.TimeseriesLinkType;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.GMLWorkspace;

/**
 * A NetElement encapsulates a Channel-Element and its dependencies <br>
 * In the example below each channel represents one net-element. Here you can see the dependencies related to the one
 * channel written in capital letter in the middle.
 * 
 * <pre>
 *                                          Node-----&gt;-----Node
 *                                            |              |
 *                                            V              V
 *                                            |              |
 *                         Catchment---&gt;---CHANNEL        Channel (downstream)
 *                             |              |           
 *                             |              V           
 *                             V              |           
 *                             |             Node
 *                             |              |
 *                          Catchment         V
 *                             |              |
 *                             V           Channel (downstream)
 *                             |
 *                          Channel (downstream)
 * 
 * 
 * </pre>
 */
public class NetElement
{
  private final static String FILTER_T = "<filter><intervallFilter amount=\"24\" calendarField=\"HOUR_OF_DAY\" mode=\"intensity\" xmlns=\"filters.zml.kalypso.org\"/></filter>"; //$NON-NLS-1$

  private final static String FILTER_V = FILTER_T;

  private boolean m_calculated = false;

  private final List<NetElement> m_upStreamDepends = new ArrayList<NetElement>();

  private final List<NetElement> m_downStreamDepends = new ArrayList<NetElement>();

  private final Channel m_channel;

  private static final int ANFANGSKNOTEN = 9001; //$NON-NLS-1$

  private static final String ENDKNOTEN = "   10000"; //$NON-NLS-1$

  private final GMLWorkspace m_synthNWorkspace;

  private final GMLWorkspace m_workspace;

  private final Logger m_logger;

  private Node m_overflowNode = null;

  private final NAControl m_metaControl;

  private final IDManager m_idManager;

  public NetElement( final GMLWorkspace modellWorkspace, final GMLWorkspace synthNWorkspace, final Channel channel, final NAControl naControl, final IDManager idManager, final Logger logger )
  {
    m_synthNWorkspace = synthNWorkspace;
    m_channel = channel;
    m_workspace = modellWorkspace;
    m_metaControl = naControl;
    m_logger = logger;
    m_idManager = idManager;
  }

  public Channel getChannel( )
  {
    return m_channel;
  }

  public boolean isCalculated( )
  {
    return m_calculated;
  }

  public void generateTimeSeries( final File klimaDir, final TimeseriesFileManager tsFileManager, final URL zmlContext ) throws IOException, Exception
  {
    final Date simulationStart = m_metaControl.getSimulationStart();
    final Date simulationEnd = m_metaControl.getSimulationEnd();

    final Catchment[] catchments = m_channel.findCatchments();

    for( final Catchment catchment : catchments )
    {
      final File targetFileN = tsFileManager.getNiederschlagEingabeDatei( catchment, klimaDir ); //$NON-NLS-1$
      final File targetFileT = tsFileManager.getTemperaturEingabeDatei( catchment, klimaDir ); //$NON-NLS-1$
      final File targetFileV = tsFileManager.getVerdunstungEingabeDatei( catchment, klimaDir ); //$NON-NLS-1$

      if( m_metaControl.isUsePrecipitationForm() )
      {
        if( !targetFileN.exists() )
          writeSynthNFile( targetFileN, catchment );
      }
      else
      {
        final TimeseriesLinkType linkN = catchment.getPrecipitationLink();
        writeTimeseries( targetFileN, linkN, zmlContext, ITimeseriesConstants.TYPE_RAINFALL, null, null, null, null );

        final TimeseriesLinkType linkT = catchment.getTemperatureLink();
        writeTimeseries( targetFileT, linkT, zmlContext, ITimeseriesConstants.TYPE_TEMPERATURE, FILTER_T, "1.0", simulationStart, simulationEnd );

        final TimeseriesLinkType linkV = catchment.getEvaporationLink();
        writeTimeseries( targetFileV, linkV, zmlContext, ITimeseriesConstants.TYPE_EVAPORATION, FILTER_V, "0.5", simulationStart, simulationEnd );
      }
    }
  }

  public static final void writeTimeseries( final File targetFile, final TimeseriesLinkType link, final URL zmlContext, final String valueAxisType, final String filter, final String defaultValue, final Date simulationStart, final Date simulationEnd ) throws Exception
  {
    if( link == null )
      return;

    if( targetFile.exists() )
      return;

    final String href = link.getHref();
    final String hrefWithFilter = filter == null ? href : ZmlURL.insertFilter( href, filter );

    final URL location = new UrlUtilities().resolveURL( zmlContext, hrefWithFilter );

    // TODO: maybe we could cache the read observations, this would give quite some performance improvement, if the same
    // observation is used more than once

    final IObservation observation = ZmlFactory.parseXML( location ); //$NON-NLS-1$

    final StringBuffer writer = new StringBuffer();

    if( defaultValue == null )
      NAZMLGenerator.createFile( writer, valueAxisType, observation );
    else
      NAZMLGenerator.createExt2File( writer, observation, simulationStart, simulationEnd, valueAxisType, defaultValue ); //$NON-NLS-1$

    FileUtils.writeStringToFile( targetFile, writer.toString() );
  }

  private void addDownStream( final NetElement downStreamElement )
  {
    // FIXME: should also add myself as upstream to downstream: but we need to avoid endless loop

    if( !m_downStreamDepends.contains( downStreamElement ) )
      m_downStreamDepends.add( downStreamElement );
  }

  public Feature getChannelsBelowDownStreamNode( )
  {
    final Node downStreamNode = m_channel.getDownstreamNode();
    return downStreamNode.getDownstreamChannel();
  }

  public List<NetElement> getDownStreamNetElements( )
  {
    return m_downStreamDepends;
  }

  public List<NetElement> getUpStreamNetElements( )
  {
    return m_upStreamDepends;
  }

  public void addUpStream( final NetElement upStreamElement )
  {
    if( !m_upStreamDepends.contains( upStreamElement ) )
      m_upStreamDepends.add( upStreamElement );
    upStreamElement.addDownStream( this );
  }

  public void writeRootChannel( final PrintWriter netBuffer, final int virtualChannelId )
  {
    final Node downstreamNode = m_channel.getDownstreamNode();
    if( downstreamNode == null )
      System.out.println( "knotU=null" ); //$NON-NLS-1$

    final int downstreamNodeID = m_idManager.getAsciiID( downstreamNode );

    netBuffer.append( "   " + virtualChannelId ); //$NON-NLS-1$
    netBuffer.append( String.format( "%8d", downstreamNodeID ) ); //$NON-NLS-1$
    netBuffer.append( ENDKNOTEN );
    netBuffer.append( " 0\n" ); //$NON-NLS-1$
  }

  /**
   * Collects the relevant elements, that should be written to the net file.
   */
  public void collectRelevantElements( final RelevantNetElements relevantElements )
  {
    relevantElements.addChannel( this );

    m_calculated = true;

    // append channel:
    final Node downstreamNode = m_channel.getDownstreamNode();
    final Node upstreamNode = m_channel.findUpstreamNode();

    // append catchments
    final Catchment[] catchmentForThisChannel = m_channel.findCatchments();
    for( final Catchment catchment : catchmentForThisChannel )
      relevantElements.addCatchment( catchment );

    if( upstreamNode != null )
      relevantElements.addNode( upstreamNode );

    if( downstreamNode != null )
      relevantElements.addNode( downstreamNode );
  }

  /**
   * writes part 1 of netfile
   */
  public void write( final PrintWriter netBuffer )
  {
    // append channel:
    final int channelID = m_idManager.getAsciiID( m_channel );
    netBuffer.append( String.format( "%8d", channelID ) ); //$NON-NLS-1$

    final Node downstreamNode = m_channel.getDownstreamNode();
    final Node upstreamNode = m_channel.findUpstreamNode();

    final Catchment[] catchmentForThisChannel = m_channel.findCatchments();

    // append upstream node:
    final int upstreamNodeID = upstreamNode == null ? ANFANGSKNOTEN : m_idManager.getAsciiID( upstreamNode );
    netBuffer.append( String.format( "%8d", upstreamNodeID ) );

    // append downstream node:
    final int downstreamNodeID = m_idManager.getAsciiID( downstreamNode );
    netBuffer.append( String.format( "%8d", downstreamNodeID ) ); //$NON-NLS-1$

    // append catchments
    netBuffer.append( " " + catchmentForThisChannel.length + "\n" ); //$NON-NLS-1$ //$NON-NLS-2$
    for( final Catchment catchment : catchmentForThisChannel )
    {
      final int chatchmentID = m_idManager.getAsciiID( catchment );
      netBuffer.append( String.format( "%8d\n", chatchmentID ) ); //$NON-NLS-1$
    }
  }

  public void accept( final NetElementVisitor visitor ) throws Exception
  {
    visitor.visit( this );
  }

  public GMLWorkspace getWorkspace( )
  {
    return m_workspace;
  }

  @Override
  public String toString( )
  {
    final Feature channel = getChannel();
    return "FID:" + channel.getId() + " AsciiID: " + m_idManager.getAsciiID( channel ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public void writeSynthNFile( final File targetFileN, final Catchment catchment ) throws Exception
  {
    final List<Feature> statNList = new ArrayList<Feature>();
    final StringBuffer buffer = new StringBuffer();
    final Double annualityKey = m_metaControl.getAnnuality();
    // Kostra-Kachel/ synth. N gebietsabängig
    final String synthNKey = catchment.getSynthZR();

    final IFeatureType syntNft = m_synthNWorkspace.getGMLSchema().getFeatureType( NaModelConstants.SYNTHN_STATN_FT );
    statNList.addAll( Arrays.asList( m_synthNWorkspace.getFeatures( syntNft ) ) );

    final Iterator<Feature> iter = statNList.iterator();
    while( iter.hasNext() )
    {
      final Feature statNFE = iter.next();
      if( statNFE.getName() != null )
      {
        if( statNFE.getName().equals( synthNKey ) )
        {
          final List< ? > statNParameterList = (List< ? >) statNFE.getProperty( NaModelConstants.STATNPARA_MEMBER );
          final Iterator< ? > iter1 = statNParameterList.iterator();
          while( iter1.hasNext() )
          {
            final Feature fe = (Feature) iter1.next();
            final String annuality = Double.toString( 1d / (Double) fe.getProperty( NaModelConstants.STATN_PROP_XJAH ) );
            if( annuality.equals( annualityKey.toString() ) )
            {
              final Object tnProp = fe.getProperty( NaModelConstants.CATCHMENT_PROP_STATN_DIAG );
              if( tnProp instanceof IObservation )
              {
                final IObservation observation = (IObservation) tnProp;
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

  public void setOverflowNode( final Node overflowNode )
  {
    m_overflowNode = overflowNode;
  }

  public Node getOverflowNode( )
  {
    return m_overflowNode;
  }

  public int getAsciiID( )
  {
    return m_idManager.getAsciiID( getChannel() );
  }
}