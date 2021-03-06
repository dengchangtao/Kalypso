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
package org.kalypso.model.wspm.tuhh.ui.rules;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.kalypso.model.wspm.core.profil.IProfile;
import org.kalypso.model.wspm.core.profil.reparator.IProfileMarkerResolution;
import org.kalypso.model.wspm.core.profil.validator.AbstractValidatorRule;
import org.kalypso.model.wspm.core.profil.validator.IValidatorMarkerCollector;
import org.kalypso.model.wspm.tuhh.ui.i18n.Messages;
import org.kalypso.model.wspm.tuhh.ui.resolutions.RemovePropertyResolution;
import org.kalypso.observation.result.ComponentUtilities;
import org.kalypso.observation.result.IComponent;

/**
 * Checks a profile for duplicate component. This is a desperate measure to get rid of duplicate marker components.<br/>
 * Instead we should rather find the place where they come from...
 *
 * @author Gernot Belger
 */
public class DuplicateComponentRule extends AbstractValidatorRule
{
  @Override
  public void validate( final IProfile profil, final IValidatorMarkerCollector collector ) throws CoreException
  {
    final IComponent[] pointProperties = profil.getPointProperties();

    final Set<String> existingComponents = new HashSet<>();

    for( int i = 0; i < pointProperties.length; i++ )
    {
      final IComponent component = pointProperties[i];
      final String id = component.getId();
      if( existingComponents.contains( id ) )
      {
        createDuplicateRule( profil, component, i, collector );
      }

      existingComponents.add( id );
    }
  }

  private void createDuplicateRule( final IProfile profil, final IComponent component, final int componentIndex, final IValidatorMarkerCollector collector ) throws CoreException
  {
    // TODO: we need a list of components that allow duplicates.
    final String componentLabel = ComponentUtilities.getComponentLabel( component );
    final String msg = String.format( Messages.getString( "DuplicateComponentRule_0" ), componentLabel ); //$NON-NLS-1$

    final IProfileMarkerResolution resolution = new RemovePropertyResolution( componentIndex );
    collector.createProfilMarker( IMarker.SEVERITY_ERROR, msg, profil, resolution );
  }
}
