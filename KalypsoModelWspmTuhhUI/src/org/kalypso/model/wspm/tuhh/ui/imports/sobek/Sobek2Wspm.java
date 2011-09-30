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
package org.kalypso.model.wspm.tuhh.ui.imports.sobek;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kalypso.contribs.eclipse.core.runtime.IStatusCollector;
import org.kalypso.contribs.eclipse.core.runtime.StatusCollector;
import org.kalypso.model.wspm.core.IWspmConstants;
import org.kalypso.model.wspm.core.KalypsoModelWspmCoreExtensions;
import org.kalypso.model.wspm.core.gml.IProfileFeature;
import org.kalypso.model.wspm.core.gml.ProfileFeatureFactory;
import org.kalypso.model.wspm.core.gml.WspmWaterBody;
import org.kalypso.model.wspm.core.profil.IProfil;
import org.kalypso.model.wspm.core.profil.IProfilPointMarker;
import org.kalypso.model.wspm.core.profil.IProfilPointPropertyProvider;
import org.kalypso.model.wspm.core.profil.sobek.SobekModel;
import org.kalypso.model.wspm.core.profil.sobek.profiles.ISobekProfileDefData;
import org.kalypso.model.wspm.core.profil.sobek.profiles.SobekFrictionDat;
import org.kalypso.model.wspm.core.profil.sobek.profiles.SobekFrictionDat.FrictionType;
import org.kalypso.model.wspm.core.profil.sobek.profiles.SobekFrictionDatCRFRSection;
import org.kalypso.model.wspm.core.profil.sobek.profiles.SobekNetworkD12Point;
import org.kalypso.model.wspm.core.profil.sobek.profiles.SobekProfile;
import org.kalypso.model.wspm.core.profil.sobek.profiles.SobekProfileDat;
import org.kalypso.model.wspm.core.profil.sobek.profiles.SobekProfileDef;
import org.kalypso.model.wspm.core.profil.sobek.profiles.SobekProfileDefYZTable;
import org.kalypso.model.wspm.core.profil.sobek.profiles.SobekYZPoint;
import org.kalypso.model.wspm.core.profil.util.ProfilUtil;
import org.kalypso.model.wspm.tuhh.core.IWspmTuhhConstants;
import org.kalypso.model.wspm.tuhh.ui.KalypsoModelWspmTuhhUIPlugin;
import org.kalypso.model.wspm.tuhh.ui.imports.sobek.SobekImportData.GUESS_STATION_STRATEGY;
import org.kalypso.model.wspm.tuhh.ui.utils.GuessStationContext;
import org.kalypso.model.wspm.tuhh.ui.utils.GuessStationPatternReplacer;
import org.kalypso.observation.result.IRecord;
import org.kalypso.observation.result.TupleResult;
import org.kalypsodeegree.model.feature.Feature;

/**
 * @author Gernot Belger
 */
public class Sobek2Wspm
{
  private final Collection<Feature> m_newFeatures = new ArrayList<Feature>();

  private final IStatusCollector m_stati = new StatusCollector( KalypsoModelWspmTuhhUIPlugin.getID() );

  private final SobekImportData m_data;

  private Pattern[] m_stationPatterns;

  public Sobek2Wspm( final SobekImportData data )
  {
    m_data = data;
  }

  public void convert( final SobekModel model )
  {
    prepareSearchPatterns();

    final SobekProfile[] profiles = model.getProfiles();
    for( final SobekProfile sobekProfile : profiles )
      convert( sobekProfile );
  }

  private void prepareSearchPatterns( )
  {
    try
    {
      if( m_data.getStationPatternEnabled() )
      {
        final String searchRegex = GuessStationPatternReplacer.getSearchRegex( m_data.getStationPattern() );
        m_stationPatterns = GuessStationPatternReplacer.preparePatterns( GuessStationContext.DEFAULT_SEARCH_CONTEXTS, searchRegex );
      }
    }
    catch( final CoreException e )
    {
      e.printStackTrace();
      m_stati.add( e.getStatus() );
    }
  }

  public void convert( final SobekProfile sobekProfile )
  {
    final SobekProfileDef profileDef = sobekProfile.getProfileDef();
    final SobekProfileDat profileDat = sobekProfile.getProfileDat();
    final SobekFrictionDat frictionDat = sobekProfile.getFrictionDat();
    final SobekNetworkD12Point networkPoint = sobekProfile.getNetworkPoint();

    if( profileDef == null && profileDat == null )
      return;

    if( profileDef == null )
    {
      final String id = profileDat.getId();
      m_stati.add( IStatus.ERROR, "Missing definition for cross section with id '%s'", null, id );
      return;
    }

    final WspmWaterBody water = m_data.getWater();
    final String srs = m_data.getSrs();

    final IProfileFeature newProfile = water.createNewProfile();
    newProfile.setName( profileDef.getId() );
    newProfile.setDescription( profileDef.getNm() );
    newProfile.setProfileType( IWspmTuhhConstants.PROFIL_TYPE_PASCHE );
    newProfile.setSrsName( srs );

    final ISobekProfileDefData profileData = profileDef.getData();

    try
    {
      final BigDecimal station = guessStation( profileDef );
      newProfile.setBigStation( station );

      final IProfil profil = newProfile.getProfil();
      convertData( profil, profileData );
      convertFriction( profil, frictionDat );
      convertNetworkPoint( profil, networkPoint );

      ProfileFeatureFactory.toFeature( profil, newProfile );
      m_newFeatures.add( newProfile );
    }
    catch( final CoreException e )
    {
      water.getProfiles().remove( newProfile );
      m_stati.add( e.getStatus() );
      return;
    }
  }

  private BigDecimal guessStation( final SobekProfileDef profileDef )
  {
    final String id = profileDef.getId();
    final String nm = profileDef.getNm();

    final GUESS_STATION_STRATEGY strategy = m_data.getStationStrategy();
    switch( strategy )
    {
      case doNotGuess:
        return null;

      case sectionId:
        return guessStation( id );

      case sectionNm:
        return guessStation( nm );
    }

    throw new IllegalArgumentException();
  }

  private BigDecimal guessStation( final String searchString )
  {
    if( m_stationPatterns == null )
      return null;

    final BigDecimal station = GuessStationPatternReplacer.findStation( searchString, GuessStationContext.DEFAULT_SEARCH_CONTEXTS, m_stationPatterns );
    if( station == null )
      m_stati.add( IStatus.WARNING, "Failed to find station in '%s'", null, searchString );
    return station;
  }

  private void convertData( final IProfil profil, final ISobekProfileDefData data ) throws CoreException
  {
    final int type = data.getType();
    switch( type )
    {
      case 10:
        convertYZTable( profil, (SobekProfileDefYZTable) data );
        break;

      default:
        final String message = String.format( "Cross section '%s' has unknown type: %d", profil.getName(), type );
        final IStatus status = new Status( IStatus.WARNING, KalypsoModelWspmTuhhUIPlugin.getID(), message );
        throw new CoreException( status );
    }
  }

  private void convertYZTable( final IProfil profil, final SobekProfileDefYZTable data )
  {
    final TupleResult result = profil.getResult();

    final int yIndex = ProfilUtil.getOrCreateComponent( profil, IWspmConstants.POINT_PROPERTY_BREITE );
    final int zIndex = ProfilUtil.getOrCreateComponent( profil, IWspmConstants.POINT_PROPERTY_HOEHE );

    final SobekYZPoint[] points = data.getPoints();
    for( final SobekYZPoint point : points )
    {
      final IRecord record = result.createRecord();

      record.setValue( yIndex, point.getY().doubleValue() );
      record.setValue( zIndex, point.getZ().doubleValue() );

      result.add( record );
    }
  }

  public IStatus getStatus( )
  {
    return m_stati.asMultiStatusOrOK( "Problems during SOBEK conversion", "Conversion sucessfully terminated" );
  }

  public Feature[] getNewFeatures( )
  {
    return m_newFeatures.toArray( new Feature[m_newFeatures.size()] );
  }

  private void convertFriction( final IProfil profil, final SobekFrictionDat frictionDat )
  {
    if( frictionDat == null )
    {
      m_stati.add( IStatus.WARNING, "Missing friction for cross section '%s'", null, profil.getName() );
      return;
    }

    final SobekFrictionDatCRFRSection[] sections = frictionDat.getSections();

    final Queue<String> markerStack = createMarkerStack( sections );

    for( int s = 0; s < sections.length; s++ )
    {
      final SobekFrictionDatCRFRSection section = sections[s];

      // REMARK: only using positive stuff here; check?
      final FrictionType type = section.getPositiveType();
      final BigDecimal value = section.getPositiveValue();
      final String componentID = convertType( type );

      // insert start and end point
      final BigDecimal start = section.getStart();
      final BigDecimal end = section.getEnd();

      final IRecord startRecord = ProfilUtil.findOrInsertPointAt( profil, start );
      final IRecord endRecord = ProfilUtil.findOrInsertPointAt( profil, end );

      final int startIndex = profil.indexOfPoint( startRecord );
      final int endIndex = profil.indexOfPoint( endRecord );

      // We also set value of endIndex (against definition of friction, which is exclusive), in order to avoid holes.
      // Probably, the next section will overwrite the end with the next start.
      for( int i = startIndex; i <= endIndex; i++ )
      {
        final IRecord record = profil.getPoint( i );
        final int index = ProfilUtil.getOrCreateComponent( profil, componentID );
        record.setValue( index, value.doubleValue() );
      }

      final String markerType = markerStack.poll();
      setMarker( profil, startRecord, markerType );

      if( s == sections.length - 1 )
        setMarker( profil, endRecord, IWspmTuhhConstants.MARKER_TYP_DURCHSTROEMTE );
    }
  }

  private void setMarker( final IProfil profil, final IRecord record, final String markerType )
  {
    if( markerType == null )
      return;

    final IProfilPointPropertyProvider provider = KalypsoModelWspmCoreExtensions.getPointPropertyProviders( profil.getType() );
    final IProfilPointMarker marker = profil.createPointMarker( markerType, record );
    marker.setValue( provider.getDefaultValue( markerType ) );
  }

  private Queue<String> createMarkerStack( final SobekFrictionDatCRFRSection[] sections )
  {
    final Queue<String> stack = new LinkedList<String>();

    stack.add( IWspmTuhhConstants.MARKER_TYP_DURCHSTROEMTE );
    if( sections.length > 2 )
    {
      if( sections.length > 4 )
        stack.add( IWspmTuhhConstants.MARKER_TYP_BORDVOLL );

      stack.add( IWspmTuhhConstants.MARKER_TYP_TRENNFLAECHE );

      stack.add( IWspmTuhhConstants.MARKER_TYP_TRENNFLAECHE );

      if( sections.length > 4 )
        stack.add( IWspmTuhhConstants.MARKER_TYP_BORDVOLL );
    }

    return stack;
  }

  // FIXME: check!
  // FIXME: probably we need to convert the unit of the value according to its type
  private String convertType( final FrictionType type )
  {
    switch( type )
    {
      case Manning:
        return IWspmTuhhConstants.POINT_PROPERTY_RAUHEIT_KST;

      default:
        return IWspmTuhhConstants.POINT_PROPERTY_RAUHEIT_KS;
    }
  }

  private void convertNetworkPoint( final IProfil profil, final SobekNetworkD12Point networkPoint )
  {
    if( networkPoint == null )
      return;

    final int pointCount = profil.getPoints().length;
    if( pointCount == 0 )
      return;

    final BigDecimal px = networkPoint.getPX();
    final BigDecimal py = networkPoint.getPY();

    // FIXME: would be nice to at least calculate two points with a distant to the centerline (which is defined how...?)

    // for now, we just use the same point for start and end

    final int indexRW = ProfilUtil.getOrCreateComponent( profil, IWspmTuhhConstants.POINT_PROPERTY_RECHTSWERT );
    final int indexHW = ProfilUtil.getOrCreateComponent( profil, IWspmTuhhConstants.POINT_PROPERTY_HOCHWERT );

    final IRecord startPoint = profil.getPoint( 0 );
    final IRecord endPoint = profil.getPoint( pointCount - 1 );

    startPoint.setValue( indexRW, px.doubleValue() );
    startPoint.setValue( indexHW, py.doubleValue() );

    endPoint.setValue( indexRW, px.doubleValue() );
    endPoint.setValue( indexHW, py.doubleValue() );
  }
}