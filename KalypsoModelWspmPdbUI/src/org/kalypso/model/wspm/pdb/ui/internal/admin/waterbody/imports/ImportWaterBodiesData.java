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
package org.kalypso.model.wspm.pdb.ui.internal.admin.waterbody.imports;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.jface.viewers.ComboViewer;
import org.kalypso.commons.java.util.AbstractModelObject;
import org.kalypso.model.wspm.pdb.connect.IPdbConnection;
import org.kalypso.model.wspm.pdb.db.mapping.WaterBody;
import org.kalypso.shape.FileMode;
import org.kalypso.shape.ShapeFile;
import org.kalypso.shape.dbf.DBaseException;

/**
 * @author Gernot Belger
 */
public class ImportWaterBodiesData extends AbstractModelObject
{
  public static enum INSERTION_MODE
  {
    skip("Skip"),
    overwrite("Overwrite");

    private final String m_label;

    private INSERTION_MODE( final String label )
    {
      m_label = label;
    }

    @Override
    public String toString( )
    {
      return m_label;
    }
  }

  public static final String PROPERTY_WATER_BODIES = "waterBodies"; //$NON-NLS-1$

  public static final String PROPERTY_INSERTION_MODE = "insertionMode"; //$NON-NLS-1$

  private final Map<String, ImportAttributeInfo> m_infos = new HashMap<String, ImportAttributeInfo>();

  private String m_srs;

  private String m_shapeFile;

  private WaterBody[] m_waterBodies;

  private INSERTION_MODE m_insertionMode = INSERTION_MODE.skip;

  private final WritableSet m_selectedWaterBodies = new WritableSet( new HashSet<WaterBody>(), WaterBody.class );

  private final WaterBody[] m_existingWaterbodies;

  private final IPdbConnection m_connection;

  public ImportWaterBodiesData( final IPdbConnection connection, final WaterBody[] existingWaterbodies )
  {
    m_connection = connection;
    m_existingWaterbodies = existingWaterbodies;

    // TODO: init from dialog settings

    // TODO: save dialog settings
  }

  public IPdbConnection getConnection( )
  {
    return m_connection;
  }

  public void setShapeInput( final String shapeFile, final String srs )
  {
    if( shapeFile == null )
      m_shapeFile = null;
    else
    {
      if( shapeFile.toLowerCase().endsWith( ".shp" ) )
        m_shapeFile = FilenameUtils.removeExtension( shapeFile );
      else
        m_shapeFile = shapeFile;
    }

    m_srs = srs;
  }

  public ImportAttributeInfo addAttributeInfo( final String property, final ComboViewer viewer, final boolean optional )
  {
    final ImportAttributeInfo info = new ImportAttributeInfo( property, viewer, optional );
    m_infos.put( property, info );
    return info;
  }

  public String getShapeFile( )
  {
    return m_shapeFile;
  }

  public ImportAttributeInfo[] getAttributeInfos( )
  {
    final Collection<ImportAttributeInfo> values = m_infos.values();
    return values.toArray( new ImportAttributeInfo[values.size()] );
  }

  public void setWaterBodies( final WaterBody[] waterBodies )
  {
    final Object oldValue = m_waterBodies;

    m_waterBodies = waterBodies;

    firePropertyChange( PROPERTY_WATER_BODIES, waterBodies, oldValue );
  }

  public WaterBody[] getWaterBodies( )
  {
    return m_waterBodies;
  }

  public ShapeFile openShape( ) throws IOException, DBaseException
  {
    final String basePath = getShapeFile();
    return new ShapeFile( basePath, Charset.defaultCharset(), FileMode.READ );
  }

  public String getSrs( )
  {
    return m_srs;
  }

  public WritableSet getSelectedWaterBodies( )
  {
    return m_selectedWaterBodies;
  }

  public INSERTION_MODE getInsertionMode( )
  {
    return m_insertionMode;
  }

  public void setInsertionMode( final INSERTION_MODE insertionMode )
  {
    final INSERTION_MODE oldValue = m_insertionMode;

    m_insertionMode = insertionMode;

    firePropertyChange( PROPERTY_INSERTION_MODE, oldValue, m_insertionMode );
  }

  public WaterBody[] getExistingWaterBodies( )
  {
    return m_existingWaterbodies;
  }
}