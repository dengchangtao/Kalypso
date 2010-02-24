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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Rectangle;
import org.kalypso.contribs.eclipse.core.runtime.PluginUtilities;
import org.kalypso.model.wspm.core.IWspmConstants;
import org.kalypso.model.wspm.core.profil.IProfil;
import org.kalypso.model.wspm.core.profil.IProfilPointMarker;
import org.kalypso.model.wspm.core.profil.IProfileObject;
import org.kalypso.model.wspm.core.profil.reparator.IProfilMarkerResolution;
import org.kalypso.model.wspm.core.profil.util.ProfilUtil;
import org.kalypso.model.wspm.core.profil.validator.AbstractValidatorRule;
import org.kalypso.model.wspm.core.profil.validator.IValidatorMarkerCollector;
import org.kalypso.model.wspm.tuhh.core.IWspmTuhhConstants;
import org.kalypso.model.wspm.tuhh.ui.KalypsoModelWspmTuhhUIPlugin;
import org.kalypso.model.wspm.tuhh.ui.i18n.Messages;
import org.kalypso.model.wspm.tuhh.ui.resolutions.DelBewuchsResolution;
import org.kalypso.model.wspm.tuhh.ui.resolutions.DelDeviderResolution;
import org.kalypso.model.wspm.tuhh.ui.resolutions.EditPointResolution;
import org.kalypso.model.wspm.tuhh.ui.resolutions.MoveDeviderResolution;
import org.kalypso.observation.result.IComponent;
import org.kalypso.observation.result.IRecord;

/**
 * Br�ckenkanten d�rfen nicht unterhalb des Gel�ndeniveaus liegen Oberkante darf nicht unter Unterkante
 * 
 * @author kimwerner
 */
public class BrueckeRule extends AbstractValidatorRule
{

  private boolean validateParams( final IProfileObject building, final IValidatorMarkerCollector collector, final String pluginId, final IProfil profil ) throws CoreException
  {
    for( final IComponent property : building.getObjectProperties() )
    {
      final Object oValue = building.getValue( property );
      if( oValue == null || (oValue instanceof Double && ((Double) oValue).isNaN()) )
      {
        collector.createProfilMarker( IMarker.SEVERITY_ERROR, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.rules.BrueckeRule.0" ) + property.getName() + "> fehlt", String.format( "km %.4f", profil.getStation() ), 0, IWspmTuhhConstants.POINT_PROPERTY_OBERKANTEBRUECKE, pluginId ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return false;
      }
    }
    return true;
  }

  private boolean validateBankfullPoints( final IValidatorMarkerCollector collector, final String pluginId, final IProfil profil ) throws CoreException
  {
    final IProfilPointMarker[] brdvp = profil.getPointMarkerFor( profil.hasPointProperty( IWspmTuhhConstants.MARKER_TYP_BORDVOLL ) );
    if( brdvp.length > 0 )
    {
      final IProfilMarkerResolution[] delRes = new IProfilMarkerResolution[] { new DelDeviderResolution( -1, IWspmTuhhConstants.MARKER_TYP_BORDVOLL ) };
      collector.createProfilMarker( IMarker.SEVERITY_ERROR, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.rules.BrueckeRule.19" ), String.format( "km %.4f", profil.getStation() ), profil.indexOfPoint( brdvp[0].getPoint() ), IWspmConstants.POINT_PROPERTY_BREITE, pluginId, delRes ); //$NON-NLS-1$ //$NON-NLS-2$
      return false;
    }
    return true;
  }

  private final Rectangle getBridgeLimits( final IRecord[] points, final double delta )
  {
    int outerLeft = -1;
    int innerLeft = -1;
    for( int i = 0; i < points.length; i++ )
    {
      final Double h = ProfilUtil.getDoubleValueFor( IWspmConstants.POINT_PROPERTY_HOEHE, points[i] );
      final Double okB = ProfilUtil.getDoubleValueFor( IWspmTuhhConstants.POINT_PROPERTY_OBERKANTEBRUECKE, points[i] );
      final Double ukB = ProfilUtil.getDoubleValueFor( IWspmTuhhConstants.POINT_PROPERTY_UNTERKANTEBRUECKE, points[i] );
      if( okB.isNaN() || ukB.isNaN() )
        continue;
      if( (outerLeft == -1) && (Math.abs( h - okB ) > delta) )
        outerLeft = i == 0 ? 0 : i - 1;
      if( (outerLeft > -1) && (Math.abs( h - ukB ) > delta) )
      {
        innerLeft = i == 0 ? 0 : i - 1;
        break;
      }
    }
    // ermitteln der rechten Grenzen
    int outerRight = points.length - 1;
    int innerRight = points.length - 1;
    for( int i = points.length - 1; i > innerLeft; i-- )
    {
      final Double h = ProfilUtil.getDoubleValueFor( IWspmConstants.POINT_PROPERTY_HOEHE, points[i] );
      final Double okB = ProfilUtil.getDoubleValueFor( IWspmTuhhConstants.POINT_PROPERTY_OBERKANTEBRUECKE, points[i] );
      final Double ukB = ProfilUtil.getDoubleValueFor( IWspmTuhhConstants.POINT_PROPERTY_UNTERKANTEBRUECKE, points[i] );
      if( okB.isNaN() || ukB.isNaN() )
        continue;
      if( (outerRight == points.length - 1) && (Math.abs( h - okB ) > delta) )
        outerRight = i == points.length - 1 ? points.length - 1 : i + 1;
      if( (outerRight < points.length - 1) && (Math.abs( h - ukB ) > delta) )
      {
        innerRight = i == points.length - 1 ? points.length - 1 : i + 1;
        break;
      }
    }

    return new Rectangle( outerLeft, innerLeft, innerRight, outerRight );

  }

  public void validate( final IProfil profil, final IValidatorMarkerCollector collector ) throws CoreException
  {

    final IProfileObject[] profileObjects = profil.getProfileObjects();
    IProfileObject building = null;
    if( profileObjects.length > 0 )
      building = profileObjects[0];

    if( profil == null || building == null || !IWspmTuhhConstants.BUILDING_TYP_BRUECKE.equals( building.getId() ) )
      return;

    try
    {
      final String pluginId = PluginUtilities.id( KalypsoModelWspmTuhhUIPlugin.getDefault() );

      // validierung ohne Br�ckengeometrie m�glich
      if( !validateParams( building, collector, pluginId, profil ) || !validateBankfullPoints( collector, pluginId, profil ) )
        return;

      // Br�ckengeometrie ermitteln

      final IRecord[] points = profil.getPoints();
      final IComponent cBreite = profil.hasPointProperty( IWspmConstants.POINT_PROPERTY_BREITE );
      final double delta = cBreite == null ? 0.0001 : cBreite.getPrecision();

      final Rectangle limits = getBridgeLimits( points, delta );

      final int outerLeft = limits.x;
      final int innerLeft = limits.y;
      final int innerRight = limits.width;
      final int outerRight = limits.height;

      if( innerLeft == -1 )
      {
        collector.createProfilMarker( IMarker.SEVERITY_ERROR, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.rules.BrueckeRule.3" ), String.format( "km %.4f", profil.getStation() ), 0, IWspmConstants.POINT_PROPERTY_BREITE, pluginId ); //$NON-NLS-1$ //$NON-NLS-2$
        return;
      }
      if( outerLeft > -1 )
      {
        final Double h = ProfilUtil.getDoubleValueFor( IWspmConstants.POINT_PROPERTY_HOEHE, points[outerLeft] );
        final Double okB = ProfilUtil.getDoubleValueFor( IWspmTuhhConstants.POINT_PROPERTY_OBERKANTEBRUECKE, points[outerLeft] );
        if( okB.isNaN() || (!h.isNaN() && Math.abs( h - okB ) > delta) )
          collector.createProfilMarker( IMarker.SEVERITY_ERROR, "Br�cke schlie�t nicht mit Gel�ndeh�he ab", String.format( "km %.4f", profil.getStation() ), outerLeft, IWspmConstants.POINT_PROPERTY_BREITE, pluginId ); //$NON-NLS-1$ //$NON-NLS-2$
        return;
      }

      if( outerRight > -1 )
      {
        final Double h = ProfilUtil.getDoubleValueFor( IWspmConstants.POINT_PROPERTY_HOEHE, points[outerRight] );
        final Double okB = ProfilUtil.getDoubleValueFor( IWspmTuhhConstants.POINT_PROPERTY_OBERKANTEBRUECKE, points[outerRight] );
        if( okB.isNaN() || (!h.isNaN() && Math.abs( h - okB ) > delta) )
          collector.createProfilMarker( IMarker.SEVERITY_ERROR, "Br�cke schlie�t nicht mit Gel�ndeh�he ab", String.format( "km %.4f", profil.getStation() ), outerRight, IWspmConstants.POINT_PROPERTY_BREITE, pluginId ); //$NON-NLS-1$ //$NON-NLS-2$
        return;
      }

      // Trennfl�chen
      final IProfilPointMarker[] trenner = profil.getPointMarkerFor( profil.hasPointProperty( IWspmTuhhConstants.MARKER_TYP_TRENNFLAECHE ) );
      final int left = trenner.length > 1 ? profil.indexOfPoint( trenner[0].getPoint() ) : -1;
      final int right = trenner.length > 1 ? profil.indexOfPoint( trenner[trenner.length - 1].getPoint() ) : -1;

      if( innerRight == outerRight )
      {
        collector.createProfilMarker( IMarker.SEVERITY_ERROR, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.rules.BrueckeRule.5" ), String.format( "km %.4f", profil.getStation() ), outerRight, IWspmConstants.POINT_PROPERTY_BREITE, pluginId ); //$NON-NLS-1$ //$NON-NLS-2$
      }
      else if( innerLeft == outerLeft )
      {
        collector.createProfilMarker( IMarker.SEVERITY_ERROR, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.rules.BrueckeRule.7" ), String.format( "km %.4f", profil.getStation() ), outerLeft, IWspmConstants.POINT_PROPERTY_BREITE, pluginId ); //$NON-NLS-1$ //$NON-NLS-2$
      }
      else if( left != innerLeft )
      {
        final int destination = right == innerLeft ? innerRight : innerLeft;
        collector.createProfilMarker( IMarker.SEVERITY_ERROR, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.rules.BrueckeRule.9" ), String.format( "km %.4f", profil.getStation() ), left, IWspmConstants.POINT_PROPERTY_BREITE, pluginId, new MoveDeviderResolution( 0, IWspmTuhhConstants.MARKER_TYP_TRENNFLAECHE, destination ) ); //$NON-NLS-1$ //$NON-NLS-2$
      }

      else if( right != innerRight )
      {
        final int destination = left == innerRight ? innerLeft : innerRight;
        collector.createProfilMarker( IMarker.SEVERITY_ERROR, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.rules.BrueckeRule.11" ), String.format( "km %.4f", profil.getStation() ), right, IWspmConstants.POINT_PROPERTY_BREITE, pluginId, new MoveDeviderResolution( trenner.length - 1, IWspmTuhhConstants.MARKER_TYP_TRENNFLAECHE, destination ) ); //$NON-NLS-1$ //$NON-NLS-2$
      }

      else
      {
// Br�ckemkanten und Bewuchs

        // sicher den ersten okB < minOK
        // sicher den ersten ukB > maxUK
        Double minOK = Double.MAX_VALUE;
        Double maxUK = -Double.MAX_VALUE;
        final boolean checkVegetation = profil.hasPointProperty( IWspmConstants.POINT_PROPERTY_BEWUCHS_AX ) != null;

        for( int i = outerLeft; i < outerRight; i++ )
        {
          final Double h = ProfilUtil.getDoubleValueFor( IWspmConstants.POINT_PROPERTY_HOEHE, points[i] );
          final Double okB = ProfilUtil.getDoubleValueFor( IWspmTuhhConstants.POINT_PROPERTY_OBERKANTEBRUECKE, points[i] );
          final Double ukB = ProfilUtil.getDoubleValueFor( IWspmTuhhConstants.POINT_PROPERTY_UNTERKANTEBRUECKE, points[i] );
          if( (i >= innerLeft) && (i <= innerRight) )
          {
            minOK = Math.min( minOK, okB );
            maxUK = Math.max( maxUK, ukB );
            // min Oberkante > max Unterkante
            if( maxUK > minOK )
            {
              collector.createProfilMarker( IMarker.SEVERITY_ERROR, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.rules.BrueckeRule.25" ), String.format( "km %.4f", profil.getStation() ), i, IWspmConstants.POINT_PROPERTY_BREITE, pluginId ); //$NON-NLS-1$ //$NON-NLS-2$
              break;
            }
            // Mehrfeldbr�cke
            if( Math.abs( h - ukB ) < delta )
            {
              collector.createProfilMarker( IMarker.SEVERITY_INFO, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.rules.BrueckeRule.17" ), String.format( "km %.4f", profil.getStation() ), i, IWspmConstants.POINT_PROPERTY_BREITE, pluginId ); //$NON-NLS-1$ //$NON-NLS-2$
              break;
            }
          }
          // Schnittkanten
          if( ukB - okB > delta )
            collector.createProfilMarker( IMarker.SEVERITY_ERROR, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.rules.BrueckeRule.21" ), String.format( "km %.4f", profil.getStation() ), i, IWspmConstants.POINT_PROPERTY_BREITE, pluginId ); //$NON-NLS-1$ //$NON-NLS-2$
          else if( h - okB > delta )
            collector.createProfilMarker( IMarker.SEVERITY_ERROR, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.rules.BrueckeRule.23" ), String.format( "km %.4f", profil.getStation() ), i, IWspmConstants.POINT_PROPERTY_BREITE, pluginId, new EditPointResolution( i, profil.getPointPropertyFor( IWspmTuhhConstants.POINT_PROPERTY_OBERKANTEBRUECKE ), h ) ); //$NON-NLS-1$ //$NON-NLS-2$
          else if( h - ukB > delta )
            collector.createProfilMarker( IMarker.SEVERITY_ERROR, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.rules.BrueckeRule.23" ), String.format( "km %.4f", profil.getStation() ), i, IWspmConstants.POINT_PROPERTY_BREITE, pluginId, new EditPointResolution( i, profil.getPointPropertyFor( IWspmTuhhConstants.POINT_PROPERTY_UNTERKANTEBRUECKE ), h ) ); //$NON-NLS-1$ //$NON-NLS-2$

          // Bewuchs unter der Br�cke
          else if( checkVegetation )
          {
            final Double bewuchs = ProfilUtil.getDoubleValueFor( IWspmTuhhConstants.POINT_PROPERTY_BEWUCHS_AX, points[i] );
            if( bewuchs != 0.0 )
              collector.createProfilMarker( IMarker.SEVERITY_ERROR, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.rules.BrueckeRule.27" ), String.format( "km %.4f", profil.getStation() ), i, IWspmConstants.POINT_PROPERTY_BREITE, pluginId, new DelBewuchsResolution( outerLeft, outerRight ) ); //$NON-NLS-1$ //$NON-NLS-2$
          }
        }

      }
    }
    catch( final Exception e )
    {
      e.printStackTrace();
      throw new CoreException( new Status( IStatus.ERROR, KalypsoModelWspmTuhhUIPlugin.getDefault().getBundle().getSymbolicName(), 0, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.rules.BrueckeRule.29" ), e ) ); //$NON-NLS-1$
    }

  }
}
