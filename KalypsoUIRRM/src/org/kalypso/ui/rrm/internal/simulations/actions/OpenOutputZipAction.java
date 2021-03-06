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
package org.kalypso.ui.rrm.internal.simulations.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.kalypso.commons.java.io.FileUtilities;
import org.kalypso.commons.java.util.zip.ZipUtilities;
import org.kalypso.contribs.eclipse.jface.wizard.IUpdateable;
import org.kalypso.ui.rrm.internal.KalypsoUIRRMPlugin;
import org.kalypso.ui.rrm.internal.UIRrmImages;
import org.kalypso.ui.rrm.internal.i18n.Messages;

/**
 * This actions opens one file of the output.zip.
 * 
 * @author Holger Albert
 */
public class OpenOutputZipAction extends Action implements IUpdateable
{

  /**
   * If true, the error.txt will be opened. If false the output.txt will be opened.
   */
  private final boolean m_errorTxt;

  /**
   * na kernel output zip file
   */
  private final IFile m_zipFile;

  /**
   * The constructor.
   * 
   * @param text
   *          The text.
   * @param tooltipText
   *          The tooltip text.
   * @param simulation
   *          The simulation.
   * @param errorTxt
   *          If true, the error.txt will be opened. If false the output.txt will be opened.
   */
  public OpenOutputZipAction( final String text, final String tooltipText, final IFile zipFile, final boolean errorTxt )
  {
    super( text );

    setToolTipText( tooltipText );

    m_zipFile = zipFile;
    m_errorTxt = errorTxt;
  }

  /**
   * @see org.eclipse.jface.action.Action#run()
   */
  @Override
  public void run( )
  {
    /* The temporary directory. */
    File tmpDir = null;

    try
    {

      /* Check if the file exists. */
      if( !m_zipFile.exists() )
        throw new IOException( String.format( Messages.getString( "OpenOutputZipAction_0" ), m_zipFile.getName() ) ); //$NON-NLS-1$

      /* Create the temporary directory. */
      tmpDir = new File( FileUtilities.TMP_DIR, "rrm_OutputZip" ); //$NON-NLS-1$
      tmpDir.mkdirs();

      /* Unzip the output.zip. */
      unzipResources( m_zipFile.getLocation().toFile(), tmpDir );

      /* Get the file. */
      File textFile = null;
      if( m_errorTxt )
        textFile = new File( tmpDir, "error.txt" ); //$NON-NLS-1$
      else
        textFile = new File( tmpDir, "output.txt" ); //$NON-NLS-1$

      /* Check if the text file exists. */
      if( !textFile.exists() )
        throw new IOException( String.format( Messages.getString( "OpenOutputZipAction_4" ), textFile.getName() ) ); //$NON-NLS-1$

      /* Find the text editor registered for txt files. */
      final Program program = Program.findProgram( "txt" ); //$NON-NLS-1$
      if( program == null )
      {
        Program.launch( textFile.getAbsolutePath() );
        return;
      }

      /* Open the text editor with the text file. */
      program.execute( textFile.getAbsolutePath() );
    }
    catch( final Exception ex )
    {
      /* Display the error. */
      final Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
      final String dialogTitle = getText();
      final String message = Messages.getString( "OpenOutputZipAction_6" ); //$NON-NLS-1$
      final IStatus status = new Status( IStatus.ERROR, KalypsoUIRRMPlugin.getID(), ex.getLocalizedMessage(), ex );
      ErrorDialog.openError( shell, dialogTitle, message, status );
    }
    finally
    {
      /* Delete the temporary directory. */
      // if( tmpDir != null )
      // FileUtils.deleteQuietly( tmpDir );
    }
  }

  /**
   * @see org.eclipse.jface.action.Action#getImageDescriptor()
   */
  @Override
  public ImageDescriptor getImageDescriptor( )
  {
    return UIRrmImages.id( UIRrmImages.DESCRIPTORS.OPEN_OUTPUT_ZML_ACTION );
  }

  /**
   * @see org.kalypso.contribs.eclipse.jface.wizard.IUpdateable#update()
   */
  @Override
  public void update( )
  {
    if( m_zipFile.exists() )
      setEnabled( true );
    else
      setEnabled( false );
  }

  /**
   * This function unzips the output zip.
   * 
   * @param outputZip
   *          The output.zip.
   * @param targetPath
   *          The target path.
   */
  private void unzipResources( final File outputZip, final File targetPath ) throws IOException
  {
    /* The input stream. */
    InputStream inputStream = null;

    try
    {
      /* Get the input stream. */
      inputStream = new FileInputStream( outputZip );

      /* Unzip into the target path. */
      ZipUtilities.unzip( inputStream, targetPath );
    }
    finally
    {
      /* Close the input stream. */
      IOUtils.closeQuietly( inputStream );
    }
  }
}