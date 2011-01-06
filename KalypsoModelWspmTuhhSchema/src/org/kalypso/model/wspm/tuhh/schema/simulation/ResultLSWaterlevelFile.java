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
package org.kalypso.model.wspm.tuhh.schema.simulation;

import java.io.File;

import org.kalypso.model.wspm.tuhh.core.gml.TuhhReachProfileSegment;
import org.kalypso.model.wspm.tuhh.schema.i18n.Messages;
import org.kalypso.observation.result.TupleResult;

/**
 * @author Gernot Belger
 */
public class ResultLSWaterlevelFile extends AbstractResultLSModelBoundaryFile
{
  public ResultLSWaterlevelFile( final File outDir, final String runoffName, final TupleResult result, final TuhhReachProfileSegment[] segments )
  {
    super( outDir, result, segments, runoffName );
  }

  /**
   * @see org.kalypso.model.wspm.tuhh.schema.simulation.IResultLSFile#getTitle()
   */
  @Override
  public String getTitle( )
  {
    return Messages.getString( "org.kalypso.model.wspm.tuhh.schema.simulation.WspmTuhhCalcJob.31" ); //$NON-NLS-1$
  }

  /**
   * @see org.kalypso.model.wspm.tuhh.schema.simulation.IResultLSFile#getFilename()
   */
  @Override
  public String getFilename( )
  {
    return "Überschwemmungslinie" + getRunoffName() + ".gml"; //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * @see org.kalypso.model.wspm.tuhh.schema.simulation.IResultLSFile#getResultID()
   */
  @Override
  public String getResultID( )
  {
    return "Ueberschwemmungslinie"; //$NON-NLS-1$
  }

  /**
   * @see org.kalypso.model.wspm.tuhh.schema.simulation.AbstractResultLSModelBoundaryFile#getUseWsp()
   */
  @Override
  protected boolean getUseWsp( )
  {
    return true;
  }
}
