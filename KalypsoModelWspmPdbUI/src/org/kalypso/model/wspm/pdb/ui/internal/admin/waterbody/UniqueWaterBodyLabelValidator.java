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
package org.kalypso.model.wspm.pdb.ui.internal.admin.waterbody;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.kalypso.commons.databinding.validation.TypedValidator;
import org.kalypso.model.wspm.pdb.db.mapping.WaterBody;

/**
 * @author Gernot Belger
 */
public class UniqueWaterBodyLabelValidator extends TypedValidator<String>
{
  private final Set<String> m_labels = new HashSet<String>();

  private final String m_ignoreLabel;

  public UniqueWaterBodyLabelValidator( final WaterBody[] existingWaterbodies, final String ignoreLabel )
  {
    super( String.class, IStatus.WARNING, "A waterbody with the same name already exists" );

    m_ignoreLabel = ignoreLabel;

    for( final WaterBody waterBody : existingWaterbodies )
      m_labels.add( waterBody.getLabel() );
  }

  @Override
  protected IStatus doValidate( final String value ) throws CoreException
  {
    if( value.equals( m_ignoreLabel ) )
      return ValidationStatus.ok();

    if( m_labels.contains( value ) )
      fail();

    return ValidationStatus.ok();
  }
}