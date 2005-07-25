/*
 * --------------- Kalypso-Header --------------------------------------------
 * 
 * This file is part of kalypso. Copyright (C) 2004, 2005 by:
 * 
 * Technical University Hamburg-Harburg (TUHH) Institute of River and coastal engineering Denickestr. 22 21073 Hamburg,
 * Germany http://www.tuhh.de/wb
 * 
 * and
 * 
 * Bjoernsen Consulting Engineers (BCE) Maria Trost 3 56070 Koblenz, Germany http://www.bjoernsen.de
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Contact:
 * 
 * E-Mail: belger@bjoernsen.de schlienger@bjoernsen.de v.doemming@tuhh.de
 * 
 * ------------------------------------------------------------------------------------
 */
package org.kalypso.ogc.sensor.view;

import java.net.URL;

import org.eclipse.compare.internal.ResizableDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.kalypso.ogc.sensor.IObservation;

/**
 * ObservationViewerDialog
 * <p>
 * 
 * @author schlienger (24.05.2005)
 */
public class ObservationViewerDialog extends ResizableDialog
{
  private ObservationViewer m_viewer;

  private String m_href;

  private URL m_context;

  private IObservation m_obs;

  private boolean m_withHeader;

  private boolean m_withMetaDataTable;

  private boolean m_withChart;

  public ObservationViewerDialog( final Shell parent, boolean withHeaderForm, boolean withMetaDataAndTable, boolean withChart )
  {
    super( parent, null );
    m_withHeader = withHeaderForm;
    m_withMetaDataTable = withMetaDataAndTable;
    m_withChart = withChart;
  }
  public ObservationViewerDialog( final Shell parent )
  {
    super( parent, null );
    m_withHeader = true;
    m_withMetaDataTable = true;
    m_withChart = true;
  }
  /**
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
   */
  protected Control createDialogArea( final Composite parent )
  {
    final Composite composite = (Composite)super.createDialogArea( parent );
    composite.setLayout( new FillLayout() );

    m_viewer = new ObservationViewer( composite, SWT.NONE, m_withHeader, m_withChart, m_withMetaDataTable  );
    if( m_href != null )
      m_viewer.setHref( m_context, m_href );
    // if there is no file but the Observation in the Memory
    else if( m_obs != null )
      m_viewer.setObservation( m_obs );

    getShell().setText( "Zeitreihenlink-Editor" );

    return composite;
  }

  public void setObservationHref( final URL context, final String href )
  {
    if( m_viewer != null )
      m_viewer.setHref( context, href );
    else
    {
      m_context = context;
      m_href = href;
    }
  }

  public String getObservationHref()
  {
    if( m_viewer == null )
      return m_href;

    return m_viewer.getHref();
  }

  public void setObservation( IObservation observation )
  {
    if( m_viewer != null )
      m_viewer.setObservation( observation );
    else
      m_obs = observation;
  }
}
