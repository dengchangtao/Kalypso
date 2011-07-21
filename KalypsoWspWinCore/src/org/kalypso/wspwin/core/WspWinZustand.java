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
package org.kalypso.wspwin.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.kalypso.wspwin.core.i18n.Messages;

/**
 * Represents the contents of a .str file
 * 
 * @author Belger
 */
public class WspWinZustand
{
  private final List<ProfileBean> m_profileBeans = new ArrayList<ProfileBean>();

  private final Collection<ZustandSegmentBean> m_segmentBeans = new ArrayList<ZustandSegmentBean>();

  private final ZustandBean m_bean;

  private final WspWinProfProj m_profProj;

  public WspWinZustand( final ZustandBean bean, final WspWinProfProj profProj )
  {
    m_bean = bean;
    m_profProj = profProj;
  }

  public ZustandBean getBean( )
  {
    return m_bean;
  }

  public ProfileBean[] getProfileBeans( )
  {
    return m_profileBeans.toArray( new ProfileBean[m_profileBeans.size()] );
  }

  public ZustandSegmentBean[] getSegmentBeans( )
  {
    return m_segmentBeans.toArray( new ZustandSegmentBean[m_segmentBeans.size()] );
  }

  private void addProfile( final ProfileBean profile )
  {
    m_profileBeans.add( profile );
  }

  private void addSegment( final ZustandSegmentBean segment )
  {
    m_segmentBeans.add( segment );
  }

  /**
   * Reads content from .str file
   */
  public void read( final File strFile ) throws IOException, ParseException
  {
    LineNumberReader reader = null;
    try
    {
      reader = new LineNumberReader( new FileReader( strFile ) );

      final int[] counts = WspWinProfProj.readStrHeader( reader );
      final int profilCount = counts[0];
      final int segmentCount = counts[1];

      final ProfileBean[] profileBeans = ProfileBean.readProfiles( reader, profilCount );
      for( final ProfileBean profileBean : profileBeans )
        addProfile( profileBean );

      reader.readLine(); // read empty line
      final ZustandSegmentBean[] segmentBeans = readZustandSegments( reader, segmentCount );
      for( final ZustandSegmentBean zustandSegmentBean : segmentBeans )
        addSegment( zustandSegmentBean );
    }
    finally
    {
      IOUtils.closeQuietly( reader );
    }
  }

  private static ZustandSegmentBean[] readZustandSegments( final LineNumberReader reader, final int segmentCount ) throws ParseException, IOException
  {
    final List<ZustandSegmentBean> beans = new ArrayList<ZustandSegmentBean>( 20 );
    for( int i = 0; i < segmentCount; i++ )
    {
      if( !reader.ready() )
        throw new ParseException( Messages.getString( "org.kalypso.wspwin.core.WspWinZustand.0" ) + reader.getLineNumber(), reader.getLineNumber() ); //$NON-NLS-1$

      final String line = reader.readLine();
      if( line == null || line.trim().length() == 0 )
        throw new ParseException( Messages.getString( "org.kalypso.wspwin.core.WspWinZustand.1" ) + reader.getLineNumber(), reader.getLineNumber() ); //$NON-NLS-1$

      final StringTokenizer tokenizer = new StringTokenizer( line );
      if( tokenizer.countTokens() != 7 )
        throw new ParseException( Messages.getString( "org.kalypso.wspwin.core.WspWinZustand.2" ) + reader.getLineNumber(), reader.getLineNumber() ); //$NON-NLS-1$

      try
      {
        final double stationFrom = Double.parseDouble( tokenizer.nextToken() );
        final double stationTo = Double.parseDouble( tokenizer.nextToken() );
        final double distanceVL = Double.parseDouble( tokenizer.nextToken() );
        final double distanceHF = Double.parseDouble( tokenizer.nextToken() );
        final double distanceVR = Double.parseDouble( tokenizer.nextToken() );

        final String fileNameFrom = tokenizer.nextToken();
        final String fileNameTo = tokenizer.nextToken();

        final ZustandSegmentBean bean = new ZustandSegmentBean( stationFrom, stationTo, fileNameFrom, fileNameTo, distanceVL, distanceHF, distanceVR );
        beans.add( bean );
      }
      catch( final NumberFormatException e )
      {
        e.printStackTrace();
        throw new ParseException( Messages.getString( "org.kalypso.wspwin.core.WspWinZustand.3" ) + reader.getLineNumber(), reader.getLineNumber() ); //$NON-NLS-1$
      }
    }

    return beans.toArray( new ZustandSegmentBean[beans.size()] );
  }

  public void addProfile( final String prfName, final BigDecimal station )
  {
    final String waterName = m_bean.getWaterName();
    final String name = m_bean.getName();

    final ProfileBean profileBean = new ProfileBean( waterName, name, station.doubleValue(), prfName );
    m_profileBeans.add( profileBean );

    m_profProj.add( profileBean );
  }

  void updateSegmentInfo( )
  {
    m_segmentBeans.clear();

    double minStation = Double.MAX_VALUE;
    double maxStation = -Double.MAX_VALUE;
    for( int i = 0; i < m_profileBeans.size() - 1; i++ )
    {
      final ProfileBean fromProfile = m_profileBeans.get( i );
      final ProfileBean toProfile = m_profileBeans.get( i + 1 );

      final double stationFrom = fromProfile.getStation();
      final double stationTo = toProfile.getStation();

      minStation = Math.min( minStation, stationFrom );
      minStation = Math.min( minStation, stationTo );
      maxStation = Math.max( maxStation, stationFrom );
      maxStation = Math.max( maxStation, stationTo );

      final String fileNameFrom = fromProfile.getFileName();
      final String fileNameTo = toProfile.getFileName();
      final double distance = Math.abs( stationTo - stationFrom );
      final ZustandSegmentBean segment = new ZustandSegmentBean( stationFrom, stationTo, fileNameFrom, fileNameTo, distance, distance, distance );
      m_segmentBeans.add( segment );
    }

    m_bean.setStartStation( minStation );
    m_bean.setEndStation( maxStation );
  }

  public void write( final File wspwinDir ) throws IOException
  {
    final File strFile = new File( WspWinHelper.getProfDir( wspwinDir ), m_bean.getFileName() );

    final BufferedWriter pw = new BufferedWriter( new FileWriter( strFile ) );

    /* Header */
    final ZustandBean bean = getBean();
    final String waterName = ProfileBean.shortenName( bean.getWaterName() );
    final String name = ProfileBean.shortenName( bean.getName() );
    pw.append( String.format( "%d %d %s %s%n", m_profileBeans.size(), m_segmentBeans.size(), waterName, name ) );

    /* Profiles */
    for( final ProfileBean profile : m_profileBeans )
      pw.append( profile.formatLine() ).append( SystemUtils.LINE_SEPARATOR );

    /* Segments */
    for( final ZustandSegmentBean segment : m_segmentBeans )
      pw.append( segment.formatLine() ).append( SystemUtils.LINE_SEPARATOR );

    pw.close();
  }
}