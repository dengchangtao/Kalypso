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
package org.kalypso.model.wspm.pdb.ui.internal.admin.event;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.kalypso.commons.databinding.jface.wizard.DatabindingWizardPage;
import org.kalypso.model.wspm.pdb.db.mapping.Event;
import org.kalypso.model.wspm.pdb.ui.internal.admin.gaf.WaterlevelComposite;

/**
 * @author Gernot Belger
 */
public class EditEventPage extends WizardPage
{
  private final Event m_event;

  private Event[] m_existingEvents;

  private DatabindingWizardPage m_binding;

  private final String m_ignoreName;

  public EditEventPage( final String pageName, final Event event, final Event[] existingEvents )
  {
    super( pageName );

    m_event = event;
    m_existingEvents = existingEvents;
    m_ignoreName = m_event.getName();

    setTitle( "Edit Event Properties" );
    setDescription( "Change the properties of the edited event." );
  }

  public void setExistingEvents( final Event[] existingEvents )
  {
    m_existingEvents = existingEvents;
  }

  @Override
  public void createControl( final Composite parent )
  {
    final Composite panel = new Composite( parent, SWT.NONE );
    setControl( panel );
    panel.setLayout( new FillLayout() );

    m_binding = new DatabindingWizardPage( this, null );

    final int style = WaterlevelComposite.SHOW_MEASUREMENT_DATE;
    final WaterlevelComposite eventEditor = new WaterlevelComposite( panel, style, m_event, m_binding, m_ignoreName );
    eventEditor.setExistingEvents( m_existingEvents );
  }
}