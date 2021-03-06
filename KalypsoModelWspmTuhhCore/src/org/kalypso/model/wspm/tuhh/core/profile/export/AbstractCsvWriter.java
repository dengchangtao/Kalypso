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
package org.kalypso.model.wspm.tuhh.core.profile.export;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kalypso.contribs.eclipse.ui.progress.ProgressUtilities;
import org.kalypso.model.wspm.core.KalypsoModelWspmCorePlugin;
import org.kalypso.model.wspm.core.gml.IProfileFeature;
import org.kalypso.model.wspm.core.profil.IProfile;
import org.kalypso.model.wspm.tuhh.core.i18n.Messages;

/**
 * Exports profiles into a csv file.
 *
 * @author Gernot Belger
 */
public abstract class AbstractCsvWriter
{
  public static final String DEFAULT_TOKEN_SEPARATOR = "\t"; //$NON-NLS-1$

  private final IProfileExportColumn[] m_columns;

  /** Separator between two tokens */
  private final String m_tokenSeparator;

  public AbstractCsvWriter( final IProfileExportColumn[] columns, final String tokenSeparator )
  {
    m_columns = columns;
    m_tokenSeparator = tokenSeparator;
  }

  protected CharSequence getTokenSeparator( )
  {
    return m_tokenSeparator;
  }

  protected IProfileExportColumn[] getColumns( )
  {
    return m_columns;
  }

  public final void export( final IProfileFeature[] profiles, final File file, final IProgressMonitor monitor ) throws CoreException
  {
    PrintWriter writer = null;
    try
    {
      writer = new PrintWriter( file );
      write( profiles, writer, monitor );
      writer.flush();
      writer.close();

      if( writer.checkError() )
        throw new IOException();
    }
    catch( final IOException e )
    {
      final String message = String.format( Messages.getString( "CsvSink_1" ) ); //$NON-NLS-1$
      final IStatus status = new Status( IStatus.ERROR, KalypsoModelWspmCorePlugin.getID(), message, e );
      throw new CoreException( status );
    }
    finally
    {
      IOUtils.closeQuietly( writer );
      monitor.done();
    }
  }

  private boolean write( final IProfileFeature[] profiles, final PrintWriter writer, final IProgressMonitor monitor )
  {
    monitor.beginTask( Messages.getString( "CsvSink_2" ), profiles.length ); //$NON-NLS-1$

    // write table header including all components
    writeHeader( writer );

    // write table header including all components
    for( final IProfileFeature profileFeature : profiles )
    {
      monitor.subTask( String.format( "%s (km %s)", profileFeature.getName(), profileFeature.getBigStation() ) ); //$NON-NLS-1$
      final IProfile profil = profileFeature.getProfile();
      writeData( writer, profileFeature, profil );
      ProgressUtilities.worked( monitor, 1 );
    }

    return true;
  }

  private void writeHeader( final PrintWriter writer )
  {
    for( int i = 0; i < m_columns.length; i++ )
    {
      final IProfileExportColumn column = m_columns[i];
      writer.append( column.getHeader() );
      if( i != m_columns.length - 1 )
      {
        writer.append( m_tokenSeparator ); //$NON-NLS-1$
      }
    }

    writer.println();
  }

  protected abstract void writeData( final PrintWriter writer, final IProfileFeature profileFeature, final IProfile profil );
}
