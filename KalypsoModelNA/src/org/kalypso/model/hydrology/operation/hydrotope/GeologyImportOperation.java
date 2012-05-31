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
package org.kalypso.model.hydrology.operation.hydrotope;

import org.eclipse.core.runtime.CoreException;
import org.kalypso.contribs.eclipse.core.runtime.IStatusCollector;
import org.kalypso.model.hydrology.binding.Geology;
import org.kalypso.model.hydrology.binding.GeologyCollection;
import org.kalypso.model.hydrology.binding.PolygonIntersectionHelper.ImportType;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.IFeatureBindingCollection;
import org.kalypsodeegree.model.geometry.GM_MultiSurface;

/**
 * Imports geology into a 'geology.gml' file from another gml-workspace (probably a shape-file).
 * 
 * @author Gernot Belger, Dejan Antanaskovic
 */
public class GeologyImportOperation extends AbstractImportOperation<GM_MultiSurface>
{
  public interface InputDescriptor extends AbstractImportOperation.InputDescriptor<GM_MultiSurface>
  {
    String getDescription( int index );

    double getMaxPerkulationsRate( int index ) throws CoreException;

    double getGWFactor( int index ) throws CoreException;
  }

  private final GeologyCollection m_output;

  private final ImportType m_importType;

  private final InputDescriptor m_inputDescriptor;

  /**
   * @param output
   *          An (empty) list containing rrmgeology:geology features
   */
  public GeologyImportOperation( final InputDescriptor inputDescriptor, final GeologyCollection output, final ImportType importType )
  {
    super( inputDescriptor );

    m_inputDescriptor = inputDescriptor;
    m_output = output;
    m_importType = importType;
  }

  @Override
  protected void init( )
  {
    final IFeatureBindingCollection<Geology> geologies = m_output.getGeologies();
    if( m_importType == ImportType.CLEAR_OUTPUT )
      geologies.clear();
  }

  @Override
  protected Feature importRow( final int i, final String label, final GM_MultiSurface geometry, final IStatusCollector log ) throws CoreException
  {
    final Geology geology = m_output.importGeology( label, geometry, m_importType, log );
    if( geology != null )
    {
      geology.setDescription( m_inputDescriptor.getDescription( i ) );
      geology.setMaxPerkulationsRate( m_inputDescriptor.getMaxPerkulationsRate( i ) );
      geology.setGWFactor( m_inputDescriptor.getGWFactor( i ) );
    }

    return geology;
  }
}
