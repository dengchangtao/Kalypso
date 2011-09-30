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
package org.kalypso.model.wspm.pdb.internal.wspm;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kalypso.contribs.eclipse.ui.progress.ProgressUtilities;
import org.kalypso.model.wspm.core.gml.WspmWaterBody;
import org.kalypso.model.wspm.pdb.db.constants.WaterBodyConstants.STATIONING_DIRECTION;
import org.kalypso.model.wspm.pdb.db.mapping.WaterBody;
import org.kalypso.model.wspm.pdb.internal.WspmPdbCorePlugin;
import org.kalypso.model.wspm.pdb.wspm.CheckoutDataMapping;
import org.kalypso.model.wspm.tuhh.core.gml.TuhhWspmProject;
import org.kalypsodeegree.KalypsoDeegreePlugin;
import org.kalypsodeegree.model.geometry.GM_Curve;
import org.kalypsodeegree_impl.model.geometry.JTSAdapter;

/**
 * @author Gernot Belger
 */
public class CheckoutWaterBodyWorker
{
  private final CheckoutDataMapping m_mapping;

  public CheckoutWaterBodyWorker( final CheckoutDataMapping mapping )
  {
    m_mapping = mapping;
  }

  public void execute( final IProgressMonitor monitor ) throws CoreException
  {
    try
    {
      final WaterBody[] waterBodies = m_mapping.getWaterBodies();

      monitor.beginTask( "Reading water bodies from database", waterBodies.length );

      for( final WaterBody waterBody : waterBodies )
      {
        final WspmWaterBody wspmWater = m_mapping.getWspmWaterBody( waterBody );
        final WspmWaterBody newWspmWater = updateOrCreateWspmWaterBody( waterBody, wspmWater );
        m_mapping.set( waterBody, newWspmWater );
        m_mapping.addChangedFeatures( newWspmWater );

        monitor.worked( 1 );
      }
    }
    catch( final Exception e )
    {
      e.printStackTrace();
      final IStatus status = new Status( IStatus.ERROR, WspmPdbCorePlugin.PLUGIN_ID, "Should never happen", e ); //$NON-NLS-1$
      throw new CoreException( status );
    }
    finally
    {
      ProgressUtilities.done( monitor );
    }
  }

  private WspmWaterBody updateOrCreateWspmWaterBody( final WaterBody waterBody, final WspmWaterBody wspmWater ) throws Exception
  {
    if( wspmWater == null )
      return createWspmWaterBody( waterBody );

    updateWaterBody( waterBody, wspmWater );
    return wspmWater;
  }

  private WspmWaterBody createWspmWaterBody( final WaterBody waterBody ) throws Exception
  {
    final String name = waterBody.getLabel();

    final TuhhWspmProject project = m_mapping.getProject();

    final WspmWaterBody newWspmWaterBody = project.createWaterBody( name, true );

    updateWaterBody( waterBody, newWspmWaterBody );

    return newWspmWaterBody;
  }

  private void updateWaterBody( final WaterBody waterBody, final WspmWaterBody wspmWater ) throws Exception
  {
    wspmWater.setName( waterBody.getLabel() );
    wspmWater.setRefNr( waterBody.getName() );
    wspmWater.setDescription( waterBody.getDescription() );

    final STATIONING_DIRECTION directionOfStationing = waterBody.getDirectionOfStationing();
    final boolean isDirectionUpstreams = directionOfStationing == WaterBody.STATIONING_DIRECTION.upstream;
    wspmWater.setDirectionUpstreams( isDirectionUpstreams );

    final String kalypsoSRS = KalypsoDeegreePlugin.getDefault().getCoordinateSystem();
    final GM_Curve centerLine = (GM_Curve) JTSAdapter.wrapWithSrid( waterBody.getRiverlineAsLine() );
    if( centerLine != null )
    {
      final GM_Curve transformedCenterline = (GM_Curve) centerLine.transform( kalypsoSRS );
      wspmWater.setCenterLine( transformedCenterline );
    }
  }
}