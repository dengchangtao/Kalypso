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
package org.kalypso.model.wspm.tuhh.core.profile.importer.wprof;

import java.math.BigDecimal;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kalypso.contribs.java.util.Arrays;
import org.kalypso.gmlschema.GMLSchemaException;
import org.kalypso.model.wspm.core.KalypsoModelWspmCoreExtensions;
import org.kalypso.model.wspm.core.gml.IProfileFeature;
import org.kalypso.model.wspm.core.gml.ProfileFeatureFactory;
import org.kalypso.model.wspm.core.profil.IProfil;
import org.kalypso.model.wspm.core.profil.IProfilPointPropertyProvider;
import org.kalypso.model.wspm.tuhh.core.IWspmTuhhConstants;
import org.kalypso.model.wspm.tuhh.core.KalypsoModelWspmTuhhCorePlugin;
import org.kalypso.model.wspm.tuhh.core.gml.TuhhWspmProject;
import org.kalypso.model.wspm.tuhh.core.wprof.IWProfPoint;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree_impl.gml.binding.commons.Image;

/**
 * @author Gernot
 */
public abstract class AbstractProfileCreator implements IProfileCreator, IWspmTuhhConstants
{
  private BigDecimal m_overwriteStation;

  private final String m_description;

  private final ProfileData m_data;

  private boolean m_createProfileComment = true;

  public AbstractProfileCreator( final String description, final ProfileData data )
  {
    m_description = description;
    m_data = data;
  }

  protected final IWProfPoint[] getPoints( final String id )
  {
    final ProfilePolygon polygon = m_data.getProfilePolygones().get( id );
    if( polygon == null )
      return null;

    return polygon.getPoints();
  }

  protected final ProfilePolygones getPolygones( )
  {
    return m_data.getProfilePolygones();
  }

  public GM_Point transform( final GM_Point location ) throws Exception
  {
    return (GM_Point) m_data.getTransformer().transform( location );
  }

  public ProfileMarkers getMarkers( )
  {
    return m_data.getMarkers();
  }

  /**
   * @see org.kalypso.model.wspm.tuhh.core.profile.importer.wprof.IProfileCreator#addProfiles(org.kalypso.model.wspm.tuhh.core.gml.TuhhWspmProject)
   */
  @Override
  public final IProfil addProfile( final TuhhWspmProject project ) throws CoreException
  {
    final IProfileFeature profileFeature = createNewProfile( project );
    if( profileFeature == null )
      return null;

    final IProfil profile = profileFeature.getProfil();

    try
    {
      addData( profile );
    }
    catch( final CoreException e )
    {
      e.printStackTrace();

      final IStatus status = e.getStatus();
      profile.setComment( status.toString() );
    }
    finally
    {
      ProfileFeatureFactory.toFeature( profile, profileFeature );
    }

    return profile;
  }

  private IProfileFeature createNewProfile( final TuhhWspmProject project ) throws CoreException
  {
    final ProfilePolygones profilePolygones = m_data.getProfilePolygones();
    final IWProfPoint anyPoint = profilePolygones.getAnyPoint();
    if( anyPoint == null )
      return null;

    final BigDecimal station = getStation( anyPoint );

    try
    {
      final String riverId = anyPoint.getRiverId();
      final String riverName = anyPoint.getRiverName();
      final URL[] photoUrls = profilePolygones.getPhotoUrls();

      final IProfileFeature profileFeature = project.createNewProfile( riverName, true );
      profileFeature.getWater().setRefNr( riverId );

      profileFeature.setSrsName( m_data.getTransformer().getTarget() );
      for( final URL url : photoUrls )
      {
        final Image image = profileFeature.addImage( url );
        final String path = url.getPath();
        final String imageName = FilenameUtils.getBaseName( path );
        image.setName( imageName );
      }

      return profileFeature;
    }
    catch( final GMLSchemaException e )
    {
      final String message = String.format( "Unable to create profile at %s", station ); //$NON-NLS-1$
      final Status status = new Status( IStatus.ERROR, KalypsoModelWspmTuhhCorePlugin.getID(), message, e );
      throw new CoreException( status );
    }
  }

  private BigDecimal getStation( final IWProfPoint anyPoint )
  {
    if( m_overwriteStation != null )
      return m_overwriteStation;

    return anyPoint.getStation();
  }

  private void addData( final IProfil profile ) throws CoreException
  {
    addBasicData( profile );

    final String newName = String.format( "%s (%s)", profile.getName(), m_description ); //$NON-NLS-1$
    // FIXME: Andrea
// final String newName = String.format( "%s", profile.getName() );
    profile.setName( newName );

    configure( profile );
  }

  private void addBasicData( final IProfil profile )
  {
    final IWProfPoint wprofPoint = m_data.getProfilePolygones().getAnyPoint();

    final BigDecimal station = getStation( wprofPoint );
    final String comment = wprofPoint.getProfileComment();
    final String profileName = wprofPoint.getPNam();

    System.out.println( String.format( "Create profile '%s'", profileName ) ); //$NON-NLS-1$

    profile.setName( profileName );

    final ProfilePolygones profilePolygones = m_data.getProfilePolygones();
    final int numPoints = profilePolygones.getNumPoints();
    final String[] objTypes = profilePolygones.getAllIDs();
    final String objTypesString = Arrays.toString( objTypes, ", " ); //$NON-NLS-1$
    final String profileComment = comment + "\nVorhandene Datenarten: " + objTypesString + "\nPunkte insgesammt: " + numPoints;
    if( m_createProfileComment )
      profile.setComment( profileComment );

    // FIXME:
    profile.setStation( station == null ? -999.999 : station.doubleValue() );
    final IProfilPointPropertyProvider provider = KalypsoModelWspmCoreExtensions.getPointPropertyProviders( profile.getType() );
    profile.addPointProperty( provider.getPointProperty( POINT_PROPERTY_RECHTSWERT ) );
    profile.addPointProperty( provider.getPointProperty( POINT_PROPERTY_HOCHWERT ) );
    profile.addPointProperty( provider.getPointProperty( POINT_PROPERTY_COMMENT ) );
  }

  public void setDoCreateProfileComment( final boolean createProfileComment )
  {
    m_createProfileComment = createProfileComment;
  }

  public void setOverwriteStation( final BigDecimal station )
  {
    m_overwriteStation = station;
  }

  protected abstract void configure( IProfil profile ) throws CoreException;

}
