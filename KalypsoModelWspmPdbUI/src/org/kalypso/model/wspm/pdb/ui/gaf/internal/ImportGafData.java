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
package org.kalypso.model.wspm.pdb.ui.gaf.internal;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.kalypso.commons.java.util.AbstractModelObject;
import org.kalypsodeegree.KalypsoDeegreePlugin;

/**
 * @author Gernot Belger
 */
public class ImportGafData extends AbstractModelObject
{
  public static final String PROPERTY_SRS = "srs"; //$NON-NLS-1$

  public static final String PROPERTY_GAF_FILE = "gafFile"; //$NON-NLS-1$

  private String m_srs = KalypsoDeegreePlugin.getDefault().getCoordinateSystem();

  private File m_gafFile;

  public String getSrs( )
  {
    return m_srs;
  }

  public void setSrs( final String srs )
  {
    final String oldValue = m_srs;

    m_srs = srs;

    firePropertyChange( PROPERTY_SRS, oldValue, m_srs );
  }

  public File getGafFile( )
  {
    return m_gafFile;
  }

  public void setGafFile( final File gafFile )
  {
    final File oldValue = m_gafFile;

    m_gafFile = gafFile;

    firePropertyChange( PROPERTY_GAF_FILE, oldValue, m_gafFile );
  }

  public void init( final IDialogSettings settings )
  {
    if( settings == null )
      return;

    final String gafPath = settings.get( PROPERTY_GAF_FILE );
    if( !StringUtils.isBlank( gafPath ) )
      m_gafFile = new File( gafPath );

    final String srs = settings.get( PROPERTY_SRS );
    if( srs != null )
      m_srs = srs;
  }

  public void store( final IDialogSettings settings )
  {
    if( settings == null )
      return;

    final String gafPath = m_gafFile == null ? null : m_gafFile.getAbsolutePath();

    settings.put( PROPERTY_SRS, m_srs );
    settings.put( PROPERTY_GAF_FILE, gafPath );
  }
}