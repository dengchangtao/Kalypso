/** This file is part of Kalypso
 *
 *  Copyright (c) 2012 by
 *
 *  Bj�rnsen Beratende Ingenieure GmbH, Koblenz, Germany (Bjoernsen Consulting Engineers), http://www.bjoernsen.de
 *  Technische Universit�t Hamburg-Harburg, Institut f�r Wasserbau, Hamburg, Germany
 *  (Technical University Hamburg-Harburg, Institute of River and Coastal Engineering), http://www.tu-harburg.de/wb/
 *
 *  Kalypso is free software: you can redistribute it and/or modify it under the terms  
 *  of the GNU Lesser General Public License (LGPL) as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  Kalypso is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with Kalypso.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kalypso.model.wspm.tuhh.ui.imports.ewawi;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.kalypso.model.wspm.ewawi.utils.profiles.EwawiProfile;
import org.kalypso.model.wspm.ewawi.utils.profiles.EwawiProfilePart;

/**
 * @author Holger Albert
 */
public class EwawiProfileDescriptionLabelProvider extends ColumnLabelProvider
{
  public EwawiProfileDescriptionLabelProvider( )
  {
  }

  @Override
  public String getText( final Object element )
  {
    if( element instanceof EwawiProfile )
    {
      final EwawiProfile profile = (EwawiProfile)element;
      final EwawiProfilePart[] parts = profile.getParts();
      if( parts != null && parts.length > 0 )
        return String.format( "%d", parts[0].getGewKennzahl() ); //$NON-NLS-1$
    }

    return super.getText( element );
  }
}