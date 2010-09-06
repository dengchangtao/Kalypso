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
package org.kalypso.model.wspm.tuhh.ui.rules;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.kalypso.contribs.eclipse.core.runtime.PluginUtilities;
import org.kalypso.model.wspm.core.IWspmConstants;
import org.kalypso.model.wspm.core.profil.IProfil;
import org.kalypso.model.wspm.core.profil.IProfilPointMarker;
import org.kalypso.model.wspm.core.profil.util.ProfilUtil;
import org.kalypso.model.wspm.core.profil.validator.AbstractValidatorRule;
import org.kalypso.model.wspm.core.profil.validator.IValidatorMarkerCollector;
import org.kalypso.model.wspm.tuhh.core.IWspmTuhhConstants;
import org.kalypso.model.wspm.tuhh.core.profile.buildings.AbstractObservationBuilding;
import org.kalypso.model.wspm.tuhh.core.profile.buildings.IProfileBuilding;
import org.kalypso.model.wspm.tuhh.core.util.WspmProfileHelper;
import org.kalypso.model.wspm.tuhh.ui.KalypsoModelWspmTuhhUIPlugin;
import org.kalypso.model.wspm.tuhh.ui.i18n.Messages;
import org.kalypso.model.wspm.tuhh.ui.resolutions.DelBewuchsResolution;
import org.kalypso.observation.result.IComponent;
import org.kalypso.observation.result.IRecord;

/**
 * @author kimwerner
 */
public class WehrRule extends AbstractValidatorRule
{
  @Override
  public void validate( final IProfil profil, final IValidatorMarkerCollector collector ) throws CoreException
  {
    if( profil == null )
      return;

    final IProfileBuilding building = WspmProfileHelper.getBuilding( profil, AbstractObservationBuilding.class );

    if( building == null || !IWspmTuhhConstants.BUILDING_TYP_WEHR.equals( building.getId() ) )
      return;

    final String pluginId = PluginUtilities.id( KalypsoModelWspmTuhhUIPlugin.getDefault() );
    validateLimits( profil, collector, pluginId );
    validateProfilLines( profil, collector, pluginId );
    validateDevider( profil, collector, pluginId );
    validateParams( profil, collector, pluginId );
    validateBewuchs( profil, collector, pluginId );

  }

  private void validateDevider( final IProfil profil, final IValidatorMarkerCollector collector, final String pluginId ) throws CoreException
  {
    final IProfilPointMarker[] wehrDevider = profil.getPointMarkerFor( profil.hasPointProperty( IWspmTuhhConstants.MARKER_TYP_WEHR ) );
    if( wehrDevider == null || wehrDevider.length < 1 )
      return;
    final IProfilPointMarker[] deviders = profil.getPointMarkerFor( profil.hasPointProperty( IWspmTuhhConstants.MARKER_TYP_TRENNFLAECHE ) );
    final int index1 = profil.indexOfPoint( wehrDevider[0].getPoint() );
    final int index2 = profil.indexOfPoint( wehrDevider[wehrDevider.length - 1].getPoint() );
    final int index3 = profil.indexOfPoint( deviders[0].getPoint() );
    final int index4 = profil.indexOfPoint( deviders[deviders.length - 1].getPoint() );
    if( index1 < index3 )
      collector.createProfilMarker( IMarker.SEVERITY_ERROR, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.rules.WehrRule.1" ), String.format( "km %.4f", profil.getStation() ), index1, IWspmTuhhConstants.POINT_PROPERTY_OBERKANTEWEHR, pluginId ); //$NON-NLS-1$ //$NON-NLS-2$
    if( index2 > index4 )
      collector.createProfilMarker( IMarker.SEVERITY_ERROR, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.rules.WehrRule.3" ), String.format( "km %.4f", profil.getStation() ), index2, IWspmTuhhConstants.POINT_PROPERTY_OBERKANTEWEHR, pluginId ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  private void validateBewuchs( final IProfil profil, final IValidatorMarkerCollector collector, final String pluginId ) throws CoreException
  {

    if( profil.hasPointProperty( IWspmConstants.POINT_PROPERTY_BEWUCHS_AX ) == null )
      return;
    final IProfilPointMarker[] deviders = profil.getPointMarkerFor( profil.hasPointProperty( IWspmTuhhConstants.MARKER_TYP_TRENNFLAECHE ) );
    if( deviders.length < 2 )
      return;
    final int index1 = profil.indexOfPoint( deviders[0].getPoint() );
    final int index2 = profil.indexOfPoint( deviders[deviders.length - 1].getPoint() );
    final IRecord[] points = profil.getPoints();
    for( int i = index1; i < index2; i++ )
    {
      final Double vAX = ProfilUtil.getDoubleValueFor( IWspmConstants.POINT_PROPERTY_BEWUCHS_AX, points[i] );
      final Double vAY = ProfilUtil.getDoubleValueFor( IWspmConstants.POINT_PROPERTY_BEWUCHS_AY, points[i] );
      final Double vDP = ProfilUtil.getDoubleValueFor( IWspmConstants.POINT_PROPERTY_BEWUCHS_DP, points[i] );

      if( !(vAX.isNaN() || vAY.isNaN() || vDP.isNaN()) && vAX + vAY + vDP > 0 )
      {
        collector.createProfilMarker( IMarker.SEVERITY_ERROR, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.rules.WehrRule.5" ), String.format( "km %.4f", profil.getStation() ), i, IWspmTuhhConstants.POINT_PROPERTY_OBERKANTEWEHR, pluginId, new DelBewuchsResolution() ); //$NON-NLS-1$ //$NON-NLS-2$
        break;
      }
    }
  }

  private void validateParams( final IProfil profil, final IValidatorMarkerCollector collector, final String pluginId ) throws CoreException
  {

    final IProfilPointMarker[] deviders = profil.getPointMarkerFor( profil.hasPointProperty( IWspmTuhhConstants.MARKER_TYP_WEHR ) );

    final IProfileBuilding building = WspmProfileHelper.getBuilding( profil, AbstractObservationBuilding.class );
    if( building == null )
      return;

    final IComponent cmp = building.getObjectProperty( IWspmTuhhConstants.BUILDING_PROPERTY_WEHRART );
    if( IWspmTuhhConstants.WEHR_TYP_SCHARFKANTIG.equals( building.getValue( cmp ) ) )
      return;
    if( deviders != null )
      for( final IProfilPointMarker devider : deviders )
      {
        final Object objValue = devider.getValue();
        if( (objValue == null) || !(objValue instanceof Double) || (((Double) objValue).isNaN()) || ((Double) objValue == 0.0) )
        {
          collector.createProfilMarker( IMarker.SEVERITY_ERROR, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.rules.WehrRule.7" ), String.format( "km %.4f", profil.getStation() ), profil.indexOfPoint( devider.getPoint() ), IWspmTuhhConstants.POINT_PROPERTY_OBERKANTEWEHR, pluginId ); //$NON-NLS-1$ //$NON-NLS-2$
          break;
        }
      }

    for( final IComponent property : building.getObjectProperties() )
    {
      final Object prop = building.getValue( property );
      if( !(prop instanceof Double) )
        continue;

      if( ((Double) prop).isNaN() || (Double) prop == 0.0 )
      {
        collector.createProfilMarker( IMarker.SEVERITY_ERROR, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.rules.WehrRule.9", property.getName() ), String.format( "km %.4f", profil.getStation() ), 0, IWspmTuhhConstants.POINT_PROPERTY_OBERKANTEWEHR, pluginId ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        break;
      }
    }
  }

  private void validateProfilLines( final IProfil profil, final IValidatorMarkerCollector collector, final String pluginId ) throws CoreException
  {

    final IProfilPointMarker[] deviders = profil.getPointMarkerFor( profil.hasPointProperty( IWspmTuhhConstants.MARKER_TYP_TRENNFLAECHE ) );
    if( deviders.length < 1 )
      return;
    final int iHoehe = profil.indexOfProperty( IWspmConstants.POINT_PROPERTY_HOEHE );
    final int iOKWehr = profil.indexOfProperty( IWspmTuhhConstants.BUILDING_TYP_WEHR );
    if( iOKWehr < 0 || iHoehe < 0 )
      return;
    final int left = profil.indexOfPoint( deviders[0].getPoint() );
    final int right = profil.indexOfPoint( deviders[deviders.length - 1].getPoint() );
    if( left + 1 > right )
      return;
    final IRecord[] midPoints = profil.getPoints( left + 1, right - 1 );
    for( final IRecord point : midPoints )
    {
      final Double h = ProfilUtil.getDoubleValueFor( IWspmConstants.POINT_PROPERTY_HOEHE, point );
      final Double wk = ProfilUtil.getDoubleValueFor( IWspmTuhhConstants.BUILDING_TYP_WEHR, point );
      if( !h.isNaN() && !wk.isNaN() && wk < h )
        collector.createProfilMarker( IMarker.SEVERITY_ERROR, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.rules.WehrRule.12" ), String.format( "km %.4f", profil.getStation() ), profil.indexOfPoint( point ), IWspmTuhhConstants.POINT_PROPERTY_OBERKANTEWEHR, pluginId ); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  private void validateLimits( final IProfil profil, final IValidatorMarkerCollector collector, final String pluginId ) throws CoreException
  {
    final IProfilPointMarker[] devider = profil.getPointMarkerFor( profil.hasPointProperty( IWspmTuhhConstants.MARKER_TYP_TRENNFLAECHE ) );
    if( devider.length < 2 )
      return;
    final IRecord firstPoint = devider[0].getPoint();
    final IRecord lastPoint = devider[devider.length - 1].getPoint();
    IRecord point = null;
    final int iHoehe = profil.indexOfProperty( IWspmConstants.POINT_PROPERTY_HOEHE );
    final int iOKWehr = profil.indexOfProperty( IWspmTuhhConstants.POINT_PROPERTY_OBERKANTEWEHR );
    if( iOKWehr < 0 || iHoehe < 0 )
      return;
    final Object p1H = firstPoint.getValue( iHoehe );
    final Object p1OkW = firstPoint.getValue( iOKWehr );
    final Object p2H = lastPoint.getValue( iHoehe );
    final Object p2OkW = lastPoint.getValue( iOKWehr );
    if( p1H == null || p1OkW == null || p2H == null || p2OkW == null )
      return;
    final double dOkW = profil.hasPointProperty( IWspmTuhhConstants.POINT_PROPERTY_OBERKANTEWEHR ).getPrecision();
    if( Math.abs( (Double) p1H - (Double) p1OkW ) > dOkW )
      point = firstPoint;
    if( Math.abs( (Double) p2H - (Double) p2OkW ) > dOkW )
      point = lastPoint;
    if( point != null )
    {
      collector.createProfilMarker( IMarker.SEVERITY_ERROR, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.rules.WehrRule.14" ), String.format( "km %.4f", profil.getStation() ), profil.indexOfPoint( point ), IWspmTuhhConstants.POINT_PROPERTY_OBERKANTEWEHR, pluginId ); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }
}
