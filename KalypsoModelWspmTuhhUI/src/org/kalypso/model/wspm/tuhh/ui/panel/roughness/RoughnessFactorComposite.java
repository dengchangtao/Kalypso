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
package org.kalypso.model.wspm.tuhh.ui.panel.roughness;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.kalypso.commons.databinding.AbstractDatabinding;
import org.kalypso.model.wspm.core.profil.IProfil;
import org.kalypso.observation.result.IComponent;

/**
 * @author Dirk Kuch
 */
public class RoughnessFactorComposite extends AbstractRoughnessComposite
{
  public static final String LABEL = "Roughness Factor";

  public RoughnessFactorComposite( final IProfil profile, final IComponent component )
  {
    super( profile, component );
  }

  /**
   * @see org.kalypso.model.wspm.tuhh.ui.panel.roughness.AbstractRoughnessComposite#getLabel()
   */
  @Override
  public String getLabel( )
  {
    return LABEL;
  }

  /**
   * @see org.kalypso.contribs.eclipse.ui.pager.IElementPage#render(org.eclipse.swt.widgets.Composite,
   *      org.eclipse.ui.forms.widgets.FormToolkit)
   */
  @Override
  public void render( final Composite parent, final FormToolkit toolkit )
  {
    setBinding( new AbstractDatabinding( toolkit )
    {
    } );

    final Composite body = toolkit.createComposite( parent );
    body.setLayout( new GridLayout( 2, false ) );
    body.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, false ) );

    // TODO validators
    build( body, toolkit, "Roughness Factor", ProfileRoguhnessesDataModel.PROPERTY_ROUGHNESS_FACTOR, null );

    final ImageHyperlink lnkRemove = toolkit.createImageHyperlink( body, SWT.NULL );
    lnkRemove.setLayoutData( new GridData( SWT.RIGHT, GridData.FILL, true, false, 2, 0 ) );
    lnkRemove.setText( String.format( "Remove: %s", getLabel() ) );

    lnkRemove.addHyperlinkListener( new HyperlinkAdapter()
    {
      @Override
      public void linkActivated( final org.eclipse.ui.forms.events.HyperlinkEvent e )
      {
        RoughnessPanelHelper.removeRoughness( getProfile(), getComponent().getId() );
      }
    } );

  }

}