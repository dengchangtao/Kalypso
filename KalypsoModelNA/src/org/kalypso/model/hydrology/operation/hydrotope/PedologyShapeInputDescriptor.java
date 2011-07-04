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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.kalypso.commons.java.io.FileUtilities;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.model.hydrology.internal.i18n.Messages;
import org.kalypso.model.hydrology.operation.hydrotope.PedologyImportOperation.InputDescriptor;
import org.kalypsodeegree.model.geometry.GM_MultiSurface;
import org.kalypsodeegree_impl.io.shpapi.DBaseException;
import org.kalypsodeegree_impl.io.shpapi.HasNoDBaseFileException;
import org.kalypsodeegree_impl.io.shpapi.ShapeFile;

public class PedologyShapeInputDescriptor implements InputDescriptor
{
  private final Map<String, Integer> m_propHash = new HashMap<String, Integer>();

  private final File m_shapeFile;

  private final String m_soilTypeColumn;

  private ShapeFile m_shape;

  public PedologyShapeInputDescriptor( final File shapeFile, final String soilTypeColumn )
  {
    m_shapeFile = shapeFile;
    m_soilTypeColumn = soilTypeColumn;
  }

  /**
   * @see org.kalypso.model.hydrology.operation.hydrotope.LanduseImportOperation.InputDescriptor#getName(int)
   */
  @Override
  public String getName( final int index )
  {
    return "" + index; //$NON-NLS-1$
  }

  /**
   * @see org.kalypso.model.hydrology.operation.hydrotope.LanduseImportOperation.InputDescriptor#getDescription(int)
   */
  @Override
  public String getDescription( final int index )
  {
    return Messages.getString( "org.kalypso.convert.namodel.hydrotope.PedologyShapeInputDescriptor.1", m_shapeFile.getName() ); //$NON-NLS-1$
  }

  /**
   * @see org.kalypso.model.hydrology.operation.hydrotope.LanduseImportOperation.InputDescriptor#getGeometry(int)
   */
  @Override
  public GM_MultiSurface getGeometry( final int index ) throws CoreException
  {
    try
    {
      // TODO: important: let user enter crs of shape and transform read geometry to kalypso crs.
      final String crs = null;
      final Object property = getShapeFile().getGM_ObjectByRecNo( index + 1, crs );

      /* allow for null geometries */
      if( property == null )
        return null;

      if( property instanceof GM_MultiSurface )
        return (GM_MultiSurface) property;

      throw new NotImplementedException( Messages.getString( "org.kalypso.convert.namodel.hydrotope.PedologyShapeInputDescriptor.2" ) ); //$NON-NLS-1$
    }
    catch( final IOException e )
    {
      throw new CoreException( StatusUtilities.statusFromThrowable( e ) );
    }
  }

  /**
   * @see org.kalypso.convert.namodel.hydrotope.LanduseImportOperation.InputDescriptor#getSize()
   */
  @Override
  public int size( )
  {
    return getShapeFile().getRecordNum();
  }

  private ShapeFile getShapeFile( )
  {
    // lazy load shape
    if( m_shape == null )
    {
      try
      {
        final String shapeBase = FileUtilities.nameWithoutExtension( m_shapeFile.getAbsolutePath() );
        m_shape = new ShapeFile( shapeBase );
        final String[] properties = m_shape.getProperties();
        for( int i = 0; i < properties.length; i++ )
          m_propHash.put( properties[i], i );
      }
      catch( final IOException e )
      {
        e.printStackTrace();
      }
      catch( final HasNoDBaseFileException e )
      {
        e.printStackTrace();
      }
      catch( final DBaseException e )
      {
        e.printStackTrace();
      }
    }

    return m_shape;
  }

  private Object getProperty( final int index, final String property ) throws CoreException
  {
    final ShapeFile shape = getShapeFile();

    final Integer column = m_propHash.get( property );
    if( column == null )
    {
      final String message = Messages.getString( "org.kalypso.convert.namodel.hydrotope.PedologyShapeInputDescriptor.3", property ); //$NON-NLS-1$
      throw new CoreException( StatusUtilities.createStatus( IStatus.ERROR, message, null ) );
    }

    try
    {
      return shape.getRow( index + 1 )[column.intValue()];
    }
    catch( final HasNoDBaseFileException e )
    {
      throw new CoreException( StatusUtilities.statusFromThrowable( e ) );
    }
    catch( final DBaseException e )
    {
      throw new CoreException( StatusUtilities.statusFromThrowable( e ) );
    }
  }

  /**
   * @see org.kalypso.convert.namodel.hydrotope.PedologyImportOperation.InputDescriptor#getSoilType(int)
   */
  @Override
  public String getSoilType( final int index ) throws CoreException
  {
    final Object property = getProperty( index, m_soilTypeColumn );
    return property == null ? null : property.toString();
  }
}