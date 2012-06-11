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
package org.kalypso.model.hydrology.operation.hydrotope;

import org.eclipse.core.runtime.CoreException;
import org.kalypso.contribs.eclipse.core.runtime.IStatusCollector;
import org.kalypso.model.hydrology.binding.OverlayCollection;
import org.kalypso.model.hydrology.binding.OverlayElement;
import org.kalypso.model.hydrology.binding.PolygonIntersectionHelper.ImportType;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.IFeatureBindingCollection;
import org.kalypsodeegree.model.geometry.GM_MultiSurface;

/**
 * Imports overlays into a 'overlay.gml' file from another gml-workspace (probably a shape-file).
 * 
 * @author Dirk Kuch
 */
public class OverlayImportOperation extends AbstractImportOperation<GM_MultiSurface>
{
  public interface InputDescriptor extends AbstractImportOperation.InputDescriptor<GM_MultiSurface>
  {

    String getDRWBMDefinition( int index ) throws CoreException;
  }

  private final OverlayCollection m_output;

  private final ImportType m_importType;

  private final InputDescriptor m_inputDescriptor;

  private final ILanduseClassDelegate m_landuseClasses;

  /**
   * @param output
   *          An (empty) list containing rrmHydo:OverlayElement features
   */
  public OverlayImportOperation( final InputDescriptor inputDescriptor, final OverlayCollection output, final ILanduseClassDelegate landuseClasses, final ImportType importType )
  {
    super( inputDescriptor );

    m_inputDescriptor = inputDescriptor;
    m_output = output;
    m_landuseClasses = landuseClasses;
    m_importType = importType;
  }

  @Override
  protected void init( )
  {
    if( m_importType == ImportType.CLEAR_OUTPUT )
    {
      final IFeatureBindingCollection<OverlayElement> elements = m_output.getOverlayElements();
      elements.clear();
    }
  }

  @Override
  protected Feature importRow( final int i, final String label, final GM_MultiSurface geometry, final IStatusCollector log ) throws CoreException
  {
    // find landuse and drwbm soil type
    final String definition = m_inputDescriptor.getDRWBMDefinition( i );

    if( definition == null )
      return null;

    return m_output.importOverlayElement( label, geometry, m_importType, definition, log );
  }
}