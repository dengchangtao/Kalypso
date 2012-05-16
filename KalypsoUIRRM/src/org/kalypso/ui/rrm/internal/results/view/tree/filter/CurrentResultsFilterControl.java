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
package org.kalypso.ui.rrm.internal.results.view.tree.filter;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.kalypso.contribs.eclipse.swt.layout.Layouts;

/**
 * Search control - for displaying only last calculation result
 * 
 * @author Dirk kuch
 */
public class CurrentResultsFilterControl extends Composite
{
  private TreeViewer m_viewer;

  private final CurrentResultFilter m_filter = new CurrentResultFilter();

  public CurrentResultsFilterControl( final Composite parent, final FormToolkit toolkit )
  {
    super( parent, SWT.NULL );

    setLayout( Layouts.createGridLayout() );
    doRenderControl( toolkit );

    layout();

  }

  private void doRenderControl( final FormToolkit toolkit )
  {
    final Button button = toolkit.createButton( this, "Nur aktuelle Berechnungsergebnis", SWT.CHECK );
    button.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, false ) );

    button.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( final org.eclipse.swt.events.SelectionEvent e )
      {
        m_filter.setSelection( button.getSelection() );
        m_viewer.refresh();
      }
    } );
  }

  public void setViewer( final TreeViewer viewer )
  {
    m_viewer = viewer;
    m_viewer.addFilter( m_filter );
  }

}
