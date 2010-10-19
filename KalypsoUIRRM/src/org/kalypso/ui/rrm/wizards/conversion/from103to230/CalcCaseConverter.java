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
package org.kalypso.ui.rrm.wizards.conversion.from103to230;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.kalypso.contribs.eclipse.ui.progress.ProgressUtilities;
import org.kalypso.model.hydrology.project.INaCalcCaseConstants;
import org.kalypso.module.conversion.AbstractLoggingOperation;

/**
 * Converts one calc case.
 * 
 * @author Gernot Belger
 */
public class CalcCaseConverter extends AbstractLoggingOperation
{
  private final File m_targetDir;

  private final File m_sourceDir;

  public CalcCaseConverter( final File sourceDir, final File targetDir )
  {
    super( sourceDir.getName() );

    m_sourceDir = sourceDir;
    m_targetDir = targetDir;
  }

  /**
   * @see org.kalypso.ui.rrm.wizards.conversion.AbstractLoggingOperation#doExecute(org.eclipse.core.runtime.IProgressMonitor)
   */
  @Override
  protected void doExecute( final IProgressMonitor monitor ) throws Throwable
  {
    try
    {
      copyData();
    }
    finally
    {
      ProgressUtilities.done( monitor );
    }
  }

  private void copyData( ) throws IOException
  {
    m_targetDir.mkdirs();

    /* Copy top level gml files (everything else in this path will be ignored) */
    copyFile( INaCalcCaseConstants.CALC_CASE );
    copyFile( INaCalcCaseConstants.CALC_HYDROTOP );
    copyFile( INaCalcCaseConstants.CALC_PARAMETER );
    copyFile( INaCalcCaseConstants.EXPERT_CONTROL );
    copyFile( INaCalcCaseConstants.DOT_CALCULATION );

    /* Copy special directories */
    copyDir( INaCalcCaseConstants.ANFANGSWERTE_DIR );
    copyDir( INaCalcCaseConstants.KLIMA_DIR );
    copyDir( INaCalcCaseConstants.NIEDERSCHLAG_DIR );
    copyDir( INaCalcCaseConstants.PEGEL_DIR );

    // TODO: Benutzer entscheiden lassen:
    // - ergebnisse übernehmen?
    // - nur 'aktuell' oder alle ergebnisse?
    // Momentan: es wird immer alles kopiert
    copyDir( INaCalcCaseConstants.ERGEBNISSE_DIR );

    /* Copy additional files */
    // TODO: wir könnten alles 'unbekannte' z.B. alles Vorlagentypen grundsätzlich mitkopieren...
  }

  private void copyFile( final String path ) throws IOException
  {
    copyFile( path, path );
  }

  private void copyFile( final String sourcePath, final String targetPath ) throws IOException
  {
    final File modelSourceFile = new File( m_sourceDir, sourcePath );
    final File modelTargetFile = new File( m_targetDir, targetPath );

    FileUtils.copyFile( modelSourceFile, modelTargetFile, true );
  }

  private void copyDir( final String path ) throws IOException
  {
    copyDir( path, path );
  }

  private void copyDir( final String sourcePath, final String targetPath ) throws IOException
  {
    final File modelSourceDir = new File( m_sourceDir, sourcePath );
    final File modelTargetDir = new File( m_targetDir, targetPath );

    FileUtils.copyDirectory( modelSourceDir, modelTargetDir, true );
  }

}
