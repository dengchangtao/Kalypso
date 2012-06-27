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
package org.kalypso.kalypsomodel1d2d.internal.import2dm;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.eclipse.core.runtime.IStatus;
import org.kalypso.contribs.eclipse.core.runtime.IStatusCollector;
import org.kalypso.contribs.eclipse.core.runtime.StatusCollector;
import org.kalypso.kalypsomodel1d2d.KalypsoModel1D2DPlugin;
import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IFEDiscretisationModel1d2d;
import org.kalypsodeegree_impl.model.sort.SpatialIndexExt;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.ItemVisitor;

/**
 * Validates a collection of {@link IPolygonWithName}'s against an existing discretisation model.
 *
 * @author Gernot Belger
 */
public class SurfaceModelChecker
{
  private final IStatusCollector m_stati = new StatusCollector( KalypsoModel1D2DPlugin.PLUGIN_ID );

  private final Collection< ? extends IPolygonWithName> m_badElement = new LinkedList<IPolygonWithName>();

  private final SpatialIndexExt m_incoming;

  private final PolygonDiscretisationValidator m_validator;

  public SurfaceModelChecker( final SpatialIndexExt incoming, final IFEDiscretisationModel1d2d model )
  {
    m_incoming = incoming;
    m_validator = new PolygonDiscretisationValidator( model );
  }

  public Collection<IPolygonWithName> getBadElements( )
  {
    return Collections.unmodifiableCollection( m_badElement );
  }

  public IStatus execute( )
  {
    final Envelope fullExtent = m_incoming.getBoundingBox();

    final ItemVisitor visitor = new ItemVisitor()
    {
      @Override
      public void visitItem( final Object item )
      {
        validateItem( (IPolygonWithName) item );
      }
    };

    m_incoming.query( fullExtent, visitor );

    return m_stati.asMultiStatusOrOK( "Found inconsistent element(s)", "No inconsistent elements found" );
  }

  protected void validateItem( final IPolygonWithName item )
  {
    final String message = m_validator.validate( item );
    if( message != null )
      m_stati.add( IStatus.WARNING, "Element %s: %s", null, item.getName(), message );
  }
}