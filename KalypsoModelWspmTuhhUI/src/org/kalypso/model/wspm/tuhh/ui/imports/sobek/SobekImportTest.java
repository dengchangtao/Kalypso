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
package org.kalypso.model.wspm.tuhh.ui.imports.sobek;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;
import org.kalypso.commons.databinding.swt.FileAndHistoryData;
import org.kalypso.commons.java.io.FileUtilities;
import org.kalypso.commons.java.util.zip.ZipUtilities;
import org.kalypso.gmlschema.GMLSchemaException;
import org.kalypso.model.wspm.core.gml.WspmWaterBody;
import org.kalypso.ogc.gml.mapmodel.CommandableWorkspace;
import org.kalypso.ogc.gml.selection.EasyFeatureWrapper;
import org.kalypso.ogc.gml.selection.FeatureSelectionManager2;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree_impl.model.feature.FeatureFactory;

/**
 * @author Gernot Belger
 */
public class SobekImportTest extends Assert
{
  @Test
  public void testSobek2Wspm( ) throws IOException, CoreException, InvocationTargetException, GMLSchemaException
  {
    final File inputDir = FileUtilities.createNewTempDir( "testSobekImport" ); //$NON-NLS-1$
    final URL inputResource = getClass().getResource( "resources/testInputData.zip" ); //$NON-NLS-1$
    ZipUtilities.unzip( inputResource, inputDir );

    final SobekImportData data = new SobekImportData();

    final GMLWorkspace waterWorkspace = FeatureFactory.createGMLWorkspace( WspmWaterBody.FEATURE_WSPM_WATER_BODY, null, null );
    final WspmWaterBody water = (WspmWaterBody) waterWorkspace.getRootFeature();

    final CommandableWorkspace workspace = new CommandableWorkspace( waterWorkspace );
    final EasyFeatureWrapper selectedFeature = new EasyFeatureWrapper( workspace, water );

    final FeatureSelectionManager2 selection = new FeatureSelectionManager2();
    selection.setSelection( new EasyFeatureWrapper[] { selectedFeature } );

    data.init( null, selection );

    final FileAndHistoryData inputData = data.getInputDir();
    inputData.setFile( inputDir );

    final SobekImportOperation operation = new SobekImportOperation( data );
    final IStatus result = operation.execute( new NullProgressMonitor() );

    // TODO: REAMRK: this test fails, as we do not support enough of sobek for the moment...

    if( !result.isOK() )
      fail( result.getMessage() );

    FileUtils.forceDelete( inputDir );
  }
}