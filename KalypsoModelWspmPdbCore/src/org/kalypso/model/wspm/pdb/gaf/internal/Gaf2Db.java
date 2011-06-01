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
package org.kalypso.model.wspm.pdb.gaf.internal;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.hibernate.Session;
import org.kalypso.model.wspm.pdb.connect.IPdbOperation;
import org.kalypso.model.wspm.pdb.connect.command.GetPdbList;
import org.kalypso.model.wspm.pdb.db.mapping.CrossSection;
import org.kalypso.model.wspm.pdb.db.mapping.CrossSectionPart;
import org.kalypso.model.wspm.pdb.db.mapping.Point;
import org.kalypso.model.wspm.pdb.db.mapping.Roughness;
import org.kalypso.model.wspm.pdb.db.mapping.State;
import org.kalypso.model.wspm.pdb.db.mapping.Vegetation;
import org.kalypso.model.wspm.pdb.db.mapping.WaterBody;
import org.kalypso.model.wspm.pdb.gaf.GafProfile;
import org.kalypso.model.wspm.pdb.gaf.GafProfiles;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * Writes a gaf profile into the database.
 * 
 * @author Gernot Belger
 */
public class Gaf2Db implements IPdbOperation
{
  private final WaterBody m_waterBody;

  private final GeometryFactory m_geometryFactory;

  private int m_profileCount = 0;

  private final State m_state;

  private final GafProfiles m_profiles;

  private final IProgressMonitor m_monitor;

  private final String m_dbType;

  public Gaf2Db( final String dbType, final WaterBody waterBody, final State state, final GafProfiles profiles, final int srid, final IProgressMonitor monitor )
  {
    m_dbType = dbType;
    m_waterBody = waterBody;
    m_state = state;
    m_profiles = profiles;
    m_monitor = monitor;
    m_geometryFactory = new GeometryFactory( new PrecisionModel(), srid );
  }

  @Override
  public String getLabel( )
  {
    return "Import gaf data";
  }

  @Override
  public void execute( final Session session )
  {
    final GafProfile[] profiles = m_profiles.getProfiles();
    m_monitor.beginTask( "Importing cross sections into database", profiles.length );

    final Roughness[] allRoughness = loadRoughness( session );
    final Vegetation[] allVegetation = loadVegetation( session );

    addState( session, m_state );

    for( final GafProfile profile : profiles )
    {
      m_monitor.subTask( String.format( "converting cross section %s", profile.getStation() ) );
      commitProfile( session, m_dbType, profile, allRoughness, allVegetation );
      m_monitor.worked( 1 );
    }

    m_monitor.subTask( "writing data into database" );
  }

  private Roughness[] loadRoughness( final Session session )
  {
    // FIXME: only get roughness of kind 'GAF'
    final List<Roughness> list = GetPdbList.getList( session, Roughness.class );
    return list.toArray( new Roughness[list.size()] );
  }

  private Vegetation[] loadVegetation( final Session session )
  {
    // FIXME: only get vegetation of kind 'GAF'
    final List<Vegetation> list = GetPdbList.getList( session, Vegetation.class );
    return list.toArray( new Vegetation[list.size()] );
  }

  private void addState( final Session session, final State state )
  {
    final Date now = new Date();
    state.setCreationDate( now );
    state.setEditingDate( now );

    session.save( m_state );
  }

  private void commitProfile( final Session session, final String dbType, final GafProfile profile, final Roughness[] allRoughness, final Vegetation[] allVegetation )
  {
    final CrossSection crossSection = commitCrossSection( session, dbType, profile );

    /* add parts */
    final GafPart[] parts = profile.getParts();
    for( int i = 0; i < parts.length; i++ )
    {
      final GafPart gafPart = parts[i];
      final CrossSectionPart csPart = commitPart( session, dbType, crossSection, gafPart, i );
      if( csPart == null )
        continue;

      final GafPoint[] points = gafPart.getPoints();
      for( int j = 0; j < points.length; j++ )
      {
        final GafPoint gafPoint = points[j];
        commitPoint( session, csPart, gafPoint, j, allRoughness, allVegetation );
      }
    }
  }

  private CrossSection commitCrossSection( final Session session, final String dbType, final GafProfile profile )
  {
    final CrossSection crossSection = new CrossSection();

    final String id = m_state.getName() + "_" + m_profileCount++;
    crossSection.setName( id );

    // TODO: what to set into comment?
    // Log entries of this station?
    // or comment of state

    crossSection.setState( m_state );
    crossSection.setWaterBody( m_waterBody );

    crossSection.setDescription( StringUtils.EMPTY );

    crossSection.setStation( profile.getStation() );

    /* Copy initial dates from state */
    crossSection.setCreationDate( m_state.getCreationDate() );
    crossSection.setEditingDate( m_state.getEditingDate() );
    crossSection.setEditingUser( m_state.getEditingUser() );
    crossSection.setMeasurementDate( m_state.getMeasurementDate() );

    final Geometry line = profile.createLine( dbType );
    crossSection.setLine( line );

    session.save( crossSection );

    return crossSection;
  }

  private CrossSectionPart commitPart( final Session session, final String dbType, final CrossSection crossSection, final GafPart part, final int index )
  {
    final CrossSectionPart csPart = new CrossSectionPart();

    final String name = crossSection.getName() + "_" + index;
    csPart.setName( name );
    csPart.setDescription( StringUtils.EMPTY );
    csPart.setCategory( part.getKind() );
    final Geometry line = part.getLine( dbType );
    csPart.setLine( line );

    if( line == null )
      return null;

    csPart.setCrossSection( crossSection );
    session.save( csPart );
    return csPart;
  }

  private void commitPoint( final Session session, final CrossSectionPart csPart, final GafPoint gafPoint, final int index, final Roughness[] allRoughness, final Vegetation[] allVegetation )
  {
    final Point point = new Point();

    point.setCrossSectionPart( csPart );

    final String readCode = gafPoint.getCode();
    final String realCode = m_profiles.translateCode( readCode );

    final String readHyk = gafPoint.getCode();
    final String realHyk = m_profiles.translateHyk( readHyk );

    final String name = csPart.getName() + "_" + index;
    point.setName( name );
    point.setDescription( StringUtils.EMPTY );

    point.setConsecutiveNum( index );
    point.setHeight( gafPoint.getHeight() );
    point.setWidth( gafPoint.getWidth() );
    point.setHyk( realHyk );
    point.setCode( realCode );

    final Coordinate coordinate = gafPoint.getCoordinate();
    if( coordinate != null )
    {
      final com.vividsolutions.jts.geom.Point location = m_geometryFactory.createPoint( coordinate );
      point.setLocation( location );
    }

    point.setName( gafPoint.getPointId() );


    // FIXME: implement roughness
    point.setRoughness( allRoughness[0] );
    point.setRoughnessKstValue( null );
    point.setRoughnessKValue( null );

    // FIXME: implement vegetation
    point.setVegetation( allVegetation[0] );
    point.setVegetationAx( null );
    point.setVegetationAy( null );
    point.setVegetationDp( null );

    session.save( point );
  }
}