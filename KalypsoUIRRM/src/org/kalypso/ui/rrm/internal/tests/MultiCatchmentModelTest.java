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
package org.kalypso.ui.rrm.internal.tests;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kalypso.commons.java.io.FileUtilities;
import org.kalypso.commons.java.util.zip.ZipUtilities;

/**
 * Test for verifying a multi catchment model.
 * 
 * @author Holger Albert
 */
public class MultiCatchmentModelTest
{
  /**
   * The temporary directory.
   */
  private File m_tmpDir;

  @Before
  public void setUp( ) throws Exception
  {
    /* Get the temporary directory of the system. */
    final File tmpDir = FileUtilities.TMP_DIR;

    /* Create the temporary directory of the test. */
    m_tmpDir = FileUtilities.createNewTempDir( "multiTest", tmpDir );

    /* Get the test resources. */
    final InputStream inputStream = getClass().getResourceAsStream( "resources/multisample.zip" );

    /* Unzip them into the temporary directory of the test. */
    ZipUtilities.unzip( inputStream, m_tmpDir );

    /* Close the input stream. */
    IOUtils.closeQuietly( inputStream );
  }

  @After
  public void tearDown( ) throws Exception
  {
    /* The temporary directory of the test will not be deleted here. */
    /* Because this function is always executed. */
    /* But we want to keep the directory, if the test has failed. */
    m_tmpDir = null;
  }

  @Test
  public void test( )
  {
    // TODO

    fail( "Not yet implemented" );

    /* Delete the temporary directory of the test. */
    FileUtilities.deleteQuietly( m_tmpDir );
  }
}