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
package org.kalypso.ogc.gml.map.themes.provider;

import java.awt.Image;

import org.eclipse.core.runtime.CoreException;
import org.kalypsodeegree.model.geometry.GM_Envelope;

/**
 * This interface provides functions for image provider.
 * 
 * @author Holger Albert
 */
public interface IKalypsoImageProvider
{
  /**
   * This function will create the image and return it.
   * 
   * @param width
   *            The requested width.
   * @param height
   *            The requested height.
   * @param bbox
   *            The requested bounding box.
   * @return The created image.
   */
  public Image getImage( int width, int height, GM_Envelope bbox ) throws CoreException;

  /**
   * This function returns a label.
   * 
   * @return The label.
   */
  public String getLabel( );

  /**
   * This function returns the full extent, if available.
   * 
   * @return The full extent.
   */
  public GM_Envelope getFullExtent( );
}