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
import org.kalypso.model.wspm.pdb.db.mapping.CrossSection;
import org.kalypso.model.wspm.pdb.internal.WspmPdbCorePlugin;
import org.kalypso.model.wspm.tuhh.core.gml.TuhhReach;
import org.kalypso.model.wspm.tuhh.core.gml.TuhhWspmProject;

/**
 * @author Gernot Belger
 */
public class CheckoutCrossSectionsWorker
{
  private final CrossSection[] m_crossSections;

  private final TuhhWspmProject m_project;

  private final CheckoutOperation m_checkoutOperation;

  public CheckoutCrossSectionsWorker( final CheckoutOperation checkoutOperation, final TuhhWspmProject project, final CrossSection[] crossSections )
  {
    m_checkoutOperation = checkoutOperation;
    m_project = project;
    m_crossSections = crossSections;
  }

  public void execute( final IProgressMonitor monitor ) throws CoreException
  {
    monitor.beginTask( "Reading cross sections from database", m_crossSections.length );

    try
    {
      /* Convert the cross sections */
      final CrossSectionInserter inserter = new CrossSectionInserter( m_project );
      for( final CrossSection crossSection : m_crossSections )
      {
        monitor.subTask( String.format( "Converting %s", crossSection.getStation() ) );
        inserter.insert( crossSection );
        ProgressUtilities.worked( monitor, 1 );
      }

      final TuhhReach[] changedReaches = inserter.getInsertedReaches();
      m_checkoutOperation.addChangedFeatures( changedReaches );
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
}