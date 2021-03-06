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
package org.kalypso.kalypsomodel1d2d.ui.map.temsys.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.kalypso.contribs.eclipse.jface.operation.ICoreRunnableWithProgress;
import org.kalypso.contribs.eclipse.ui.progress.ProgressUtilities;
import org.kalypso.kalypsomodel1d2d.conv.results.TinResultWriter;
import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IFE1D2DElement;
import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IFEDiscretisationModel1d2d;
import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IPolyElement;
import org.kalypsodeegree.KalypsoDeegreePlugin;
import org.kalypsodeegree.model.geometry.GM_Polygon;
import org.kalypsodeegree.model.geometry.GM_Position;

/**
 * @author Gernot Belger
 */
public class ModelTinExporter implements ICoreRunnableWithProgress
{
  private final IFile m_targetFile;

  private final IFEDiscretisationModel1d2d m_model;

  private BigDecimal m_min = new BigDecimal( Double.MAX_VALUE );

  private BigDecimal m_max = new BigDecimal( -Double.MAX_VALUE );

  public ModelTinExporter( final IFile targetFile, final IFEDiscretisationModel1d2d model )
  {
    m_targetFile = targetFile;
    m_model = model;
  }

  @Override
  public IStatus execute( final IProgressMonitor monitor ) throws CoreException, InvocationTargetException
  {
    final IFE1D2DElement[] elements = m_model.getElements();
    final int size = elements.length;
    monitor.beginTask( "Exporting triangles", size ); //$NON-NLS-1$

    OutputStream os = null;
    try
    {
      final File dtmJavaFile = m_targetFile.getLocation().toFile();

      os = new GZIPOutputStream( new FileOutputStream( dtmJavaFile ) );

      final String crs = KalypsoDeegreePlugin.getDefault().getCoordinateSystem();
      final TinResultWriter triangleWriter = new TinResultWriter( os, crs, null );

      int cnt = 0;
      for( final IFE1D2DElement element : elements )
      {
        if( ++cnt % 100 == 0 )
          monitor.subTask( String.format( " %d/%d", cnt, size ) ); //$NON-NLS-1$

        if( element instanceof IPolyElement )
        {
          final IPolyElement poly = (IPolyElement)element;
          final GM_Polygon surface = poly.getGeometry();
          if( surface != null )
          {
            final GM_Position[] exteriorRing = surface.getSurfacePatch().getExteriorRing();
            setMinMax( exteriorRing );
            triangleWriter.add( exteriorRing[0], exteriorRing[1], exteriorRing[2] );
            if( exteriorRing.length > 4 )
              triangleWriter.add( exteriorRing[2], exteriorRing[3], exteriorRing[4] );
          }
        }

        ProgressUtilities.worked( monitor, 1 );
      }

      triangleWriter.finished();
      os.close();

      return Status.OK_STATUS;
    }
    catch( final CoreException e )
    {
      e.printStackTrace();
      throw e;
    }
    catch( final Exception e )
    {
      e.printStackTrace();
      throw new InvocationTargetException( e );
    }
    finally
    {
      IOUtils.closeQuietly( os );
      monitor.done();
      m_targetFile.refreshLocal( IResource.DEPTH_ONE, new NullProgressMonitor() );
    }
  }

  private void setMinMax( final GM_Position[] exteriorRing )
  {
    for( final GM_Position position : exteriorRing )
    {
      final double z = position.getZ();
      if( !Double.isNaN( z ) )
      {
        final BigDecimal val = new BigDecimal( z );
        m_min = m_min.min( val );
        m_max = m_min.max( val );
      }
    }
  }

  public BigDecimal getMax( )
  {
    return m_max;
  }

  public BigDecimal getMin( )
  {
    return m_min;
  }
}