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
package org.kalypso.kalypsomodel1d2d.conv;

import java.io.File;
import java.io.IOException;

import org.kalypso.gmlschema.GMLSchemaException;
import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IFEDiscretisationModel1d2d;
import org.kalypso.kalypsomodel1d2d.ui.map.ElementGeometryHelper;
import org.kalypso.ogc.gml.mapmodel.CommandableWorkspace;
import org.kalypso.ogc.gml.serialize.GmlSerializeException;
import org.kalypso.ogc.gml.serialize.GmlSerializer;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.geometry.GM_Surface;
import org.kalypsodeegree_impl.model.feature.FeatureFactory;

/**
 * @author Thomas Jung
 */
public class DiscModelImporter implements IDiscModelImporter
{
  private CommandableWorkspace m_workspace;

  private final File m_file;

  public DiscModelImporter( final File outputFile )
  {
    m_file = outputFile;
    try
    {
      // TODO: get workspace from outside
      m_workspace = new CommandableWorkspace( FeatureFactory.createGMLWorkspace( IFEDiscretisationModel1d2d.QNAME, null, null ) );
      // TODO: dispose the commandableworkspace
    }
    catch( final GMLSchemaException e )
    {
      e.printStackTrace();
    }
  }

  /**
   * @see org.kalypso.kalypsomodel1d2d.conv.IDiscModelImporter#addElement(org.kalypsodeegree.model.geometry.GM_SurfacePatch)
   */
  @Override
  public void addElement( final GM_Surface surface )
  {
    // add elements to workspace
    try
    {
      final Feature parentFeature = m_workspace.getRootFeature();
      final IFEDiscretisationModel1d2d discModel = (IFEDiscretisationModel1d2d) parentFeature.getAdapter( IFEDiscretisationModel1d2d.class );
      ElementGeometryHelper.createFE1D2DfromSurface( m_workspace, discModel, surface );
    }
    catch( final Exception e )
    {
      e.printStackTrace();
    }
  }

  /**
   * @see org.kalypso.kalypsomodel1d2d.conv.IDiscModelImporter#finish()
   */
  @Override
  public void finish( )
  {
    try
    {
      GmlSerializer.serializeWorkspace( m_file, m_workspace, "UTF-8" ); //$NON-NLS-1$
    }
    catch( final IOException e )
    {
      e.printStackTrace();
    }
    catch( final GmlSerializeException e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // write workspace into file
  }

}