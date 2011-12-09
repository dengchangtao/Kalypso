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
package org.kalypso.ui.rrm.internal.conversion.to12_02;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kalypso.contribs.eclipse.ui.progress.ProgressUtilities;
import org.kalypso.model.hydrology.binding.NAControl;
import org.kalypso.model.hydrology.binding.model.NaModell;
import org.kalypso.model.hydrology.project.INaCalcCaseConstants;
import org.kalypso.model.hydrology.project.INaProjectConstants;
import org.kalypso.module.conversion.AbstractLoggingOperation;
import org.kalypso.ogc.gml.serialize.GmlSerializeException;
import org.kalypso.ui.rrm.internal.KalypsoUIRRMPlugin;
import org.kalypso.ui.rrm.internal.conversion.ConverterData;
import org.kalypso.ui.rrm.internal.conversion.ITimeseriesVisitor;
import org.kalypso.ui.rrm.internal.conversion.TimeseriesWalker;
import org.kalypso.ui.rrm.internal.i18n.Messages;
import org.kalypsodeegree.model.feature.FeatureVisitor;

/**
 * Converts one calc case.
 *
 * @author Gernot Belger
 */
public class CalcCaseConverter extends AbstractLoggingOperation
{
  private final String DOT_CALCULATION = ".calculation"; //$NON-NLS-1$

  private final String CALC_CASE = "calcCase.gml"; //$NON-NLS-1$

  private final String CALC_HYDROTOP = "calcHydrotop.gml"; //$NON-NLS-1$

  private final String CALC_PARAMETER = "calcParameter.gml"; //$NON-NLS-1$

  private final File m_targetCalcCaseDir;

  private final File m_sourceDir;

  private final ConverterData m_data;

  private final String m_chosenExe;

  public CalcCaseConverter( final File sourceDir, final File targetcalcCaseDir, final String chosenExe )
  {
    super( sourceDir.getName() );

    m_sourceDir = sourceDir;
    m_targetCalcCaseDir = targetcalcCaseDir;
    m_chosenExe = chosenExe;
    m_data = new ConverterData( m_targetCalcCaseDir );
  }

  @Override
  protected void doExecute( final IProgressMonitor monitor ) throws Exception
  {
    try
    {
      copyData();
    }
    finally
    {
      ProgressUtilities.done( monitor );

      m_data.dispose();
    }
  }

  private void copyData( ) throws Exception
  {
    m_targetCalcCaseDir.mkdirs();

    copyBasicData();

    m_data.load();

    renameOldResults();

    /* Tweak model data */
    tweakCalculation();

    fixTimeseriesLinks();
  }

  private void copyBasicData( ) throws IOException
  {
    /* Copy top level gml files (everything else in this path will be ignored) */
    copyFile( CALC_CASE, INaProjectConstants.GML_MODELL_PATH );
    copyFile( CALC_HYDROTOP, INaProjectConstants.GML_HYDROTOP_PATH );
    copyFile( CALC_PARAMETER, INaProjectConstants.GML_PARAMETER_PATH );
    copyFile( INaCalcCaseConstants.EXPERT_CONTROL_FILE, INaCalcCaseConstants.EXPERT_CONTROL_PATH );
    copyFile( DOT_CALCULATION, INaCalcCaseConstants.CALCULATION_GML_PATH );

    /* Copy special directories */
    copyDir( INaCalcCaseConstants.ANFANGSWERTE_DIR, INaCalcCaseConstants.ANFANGSWERTE_DIR );
    copyDir( INaCalcCaseConstants.KLIMA_DIR );
    copyDir( INaCalcCaseConstants.NIEDERSCHLAG_DIR );
    copyPegel();
    copyZufluss();

    copyDir( INaCalcCaseConstants.ERGEBNISSE_DIR );

    getLog().add( new Status( IStatus.OK, KalypsoUIRRMPlugin.getID(), Messages.getString( "CalcCaseConverter_0" ) ) ); //$NON-NLS-1$
  }

  private void renameOldResults( )
  {
    final File aktuellDir = new File( m_targetCalcCaseDir, INaCalcCaseConstants.AKTUELL_DIR );
    final File resultDir = new File( m_targetCalcCaseDir, INaCalcCaseConstants.ERGEBNISSE_DIR );
    final File origCurrentResultDir = new File( resultDir, Messages.getString( "CalcCaseConverter.0" ) ); //$NON-NLS-1$
    if( aktuellDir.isDirectory() )
    {
      aktuellDir.renameTo( origCurrentResultDir );
      final String msg = String.format( Messages.getString( "CalcCaseConverter.1" ), origCurrentResultDir.getName() ); //$NON-NLS-1$
      getLog().add( IStatus.INFO, msg );
      return;
    }
  }

  private void copyPegel( ) throws IOException
  {
    final File modelSourceDir = new File( m_sourceDir, INaCalcCaseConstants.PEGEL_DIR ); //$NON-NLS-1$
    final File modelTargetDir = new File( m_targetCalcCaseDir, INaCalcCaseConstants.PEGEL_DIR ); //$NON-NLS-1$
    if( modelSourceDir.isDirectory() )
      FileUtils.copyDirectory( modelSourceDir, modelTargetDir, true );
    else
    {
      getLog().add( IStatus.INFO, "Missing 'Pegel' folder in calculation case. Creating an empty folder." );
      modelTargetDir.mkdirs();
    }
  }

  private void copyZufluss( ) throws IOException
  {
    final File modelSourceDir = new File( m_sourceDir, "Zufluss" ); //$NON-NLS-1$
    final File modelTargetDir = new File( m_targetCalcCaseDir, "Zufluss" ); //$NON-NLS-1$
    if( modelSourceDir.isDirectory() )
      FileUtils.copyDirectory( modelSourceDir, modelTargetDir, true );
    else
      getLog().add( IStatus.INFO, Messages.getString( "CalcCaseConverter.6" ) ); //$NON-NLS-1$
  }

  private void copyFile( final String sourcePath, final String targetPath ) throws IOException
  {
    final File modelSourceFile = new File( m_sourceDir, sourcePath );
    final File modelTargetFile = new File( m_targetCalcCaseDir, targetPath );

    FileUtils.copyFile( modelSourceFile, modelTargetFile, true );
  }

  private void copyDir( final String path ) throws IOException
  {
    copyDir( path, path );
  }

  private void copyDir( final String sourcePath, final String targetPath ) throws IOException
  {
    final File modelSourceDir = new File( m_sourceDir, sourcePath );
    final File modelTargetDir = new File( m_targetCalcCaseDir, targetPath );

    FileUtils.copyDirectory( modelSourceDir, modelTargetDir, true );
  }

  private void tweakCalculation( ) throws Exception
  {
    tweakExeVersion();
  }

  private void tweakExeVersion( ) throws IOException, GmlSerializeException
  {
    final NAControl metaControl = m_data.getMetaControl();
    final String exeVersion = metaControl.getExeVersion();
    if( m_chosenExe != null )
    {
      metaControl.setExeVersion( m_chosenExe );

      m_data.saveModel( metaControl, INaCalcCaseConstants.CALCULATION_GML_PATH );

      final String statusMsg = Messages.getString( "CalcCaseConverter_2", m_chosenExe, exeVersion ); //$NON-NLS-1$
      getLog().add( IStatus.OK, statusMsg );
    }
    else
    {
      final String statusMsg = String.format( Messages.getString( "CalcCaseConverter_3" ), exeVersion ); //$NON-NLS-1$
      getLog().add( IStatus.OK, statusMsg );
    }

    metaControl.getWorkspace().dispose();
  }

  /**
   * Add an additional '../' to every timeseries path.
   */
  private void fixTimeseriesLinks( ) throws IOException, GmlSerializeException
  {
    final NaModell naModel = m_data.getNaModel();

    final IStatus log = fixTimeseriesLinks( naModel );
    getLog().add( log );

    m_data.saveModel( naModel, INaProjectConstants.GML_MODELL_PATH );
    getLog().add( IStatus.INFO, "Timeseries links have been updated." );

  }

  static IStatus fixTimeseriesLinks( final NaModell naModel )
  {
    final ITimeseriesVisitor visitor = new FixDotDotTimeseriesVisitor();

    final TimeseriesWalker walker = new TimeseriesWalker( visitor );
    naModel.getWorkspace().accept( walker, naModel, FeatureVisitor.DEPTH_INFINITE );
    return walker.getStatus();
  }
}