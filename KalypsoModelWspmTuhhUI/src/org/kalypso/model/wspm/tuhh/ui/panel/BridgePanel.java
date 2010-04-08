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
package org.kalypso.model.wspm.tuhh.ui.panel;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.kalypso.contribs.eclipse.swt.events.DoubleModifyListener;
import org.kalypso.contribs.java.lang.NumberUtils;
import org.kalypso.model.wspm.core.profil.IProfil;
import org.kalypso.model.wspm.core.profil.IProfilChange;
import org.kalypso.model.wspm.core.profil.IProfileObject;
import org.kalypso.model.wspm.core.profil.changes.ProfilChangeHint;
import org.kalypso.model.wspm.core.profil.changes.ProfileObjectEdit;
import org.kalypso.model.wspm.core.profil.util.ProfilUtil;
import org.kalypso.model.wspm.tuhh.core.IWspmTuhhConstants;
import org.kalypso.model.wspm.tuhh.ui.i18n.Messages;
import org.kalypso.model.wspm.ui.profil.operation.ProfilOperation;
import org.kalypso.model.wspm.ui.profil.operation.ProfilOperationJob;
import org.kalypso.model.wspm.ui.view.AbstractProfilView;
import org.kalypso.observation.result.IComponent;

/**
 * @author kimwerner
 */
public class BridgePanel extends AbstractProfilView
{
  protected ArrayList<PropertyLine> m_lines = new ArrayList<PropertyLine>( 8 );

  private FormToolkit m_toolkit = null;

  protected Composite m_propPanel;

  public BridgePanel( final IProfil profile )
  {
    super( profile );
  }

  private class PropertyLine
  {
    protected final IComponent m_property;

    protected final Text m_text;

    protected final Label m_label;

    public PropertyLine( final FormToolkit toolkit, final Composite parent, final IComponent property )
    {
      m_property = property;

      final Display display = parent.getDisplay();
      final Color goodColor = display.getSystemColor( SWT.COLOR_BLACK );
      final Color badColor = display.getSystemColor( SWT.COLOR_RED );
      final DoubleModifyListener doubleModifyListener = new DoubleModifyListener( goodColor, badColor );

      m_label = toolkit.createLabel( parent, getLabel( m_property ) );

      m_text = toolkit.createText( parent, null, SWT.FILL | SWT.TRAIL | SWT.SINGLE | SWT.BORDER );
      m_text.setLayoutData( new GridData( GridData.FILL, GridData.CENTER, true, true ) );
      m_text.addModifyListener( doubleModifyListener );
      m_text.addFocusListener( new FocusAdapter()
      {
        @Override
        public void focusGained( final FocusEvent e )
        {
          if( (m_text != null) && !m_text.isDisposed() )
            m_text.selectAll();
        }

        /**
         * @see org.eclipse.swt.events.FocusAdapter#focusLost(org.eclipse.swt.events.FocusEvent)
         */
        @Override
        public void focusLost( final FocusEvent e )
        {
          final double value = NumberUtils.parseQuietDouble( m_text.getText() );
          if( !Double.isNaN( value ) )
          {
            final IProfil profil = getProfil();
            final IProfileObject[] objects = profil == null ? null : profil.getProfileObjects();
            final IProfileObject building = objects == null || objects.length < 1 ? null : objects[0];
            if( building == null )
              return;
            final Double val = ProfilUtil.getDoubleValueFor( m_property.getId(), building );
            if( val == value )
              return;

            final ProfilOperation operation = new ProfilOperation( Messages.getString("org.kalypso.model.wspm.tuhh.ui.panel.BridgePanel.0",m_property.getName()), getProfil(), true ); //$NON-NLS-1$
            operation.addChange( new ProfileObjectEdit( building, m_property, value ) );
            new ProfilOperationJob( operation ).schedule();
          }
        }
      } );

    }

    public void updateValue( )
    {
      m_label.setText( getLabel( m_property ) );
      if( m_text == null || m_text.isDisposed() )
        return;

      final IProfil profil = getProfil();
      final IProfileObject[] objects = profil == null ? null : profil.getProfileObjects();
      final IProfileObject building = objects == null || objects.length < 1 ? null : objects[0];
      if( building == null )
        return;
      final Double val = ProfilUtil.getDoubleValueFor( m_property.getId(), building );
      final String text = String.format( "%.2f", val );
      m_text.setText( text );
      if(m_text.isFocusControl())
        m_text.selectAll();
    }
    
    public void dispose( )
    {
      m_text.dispose();
      m_label.dispose();
    }
  }

  @SuppressWarnings("finally")
  protected String getLabel( final IComponent property )
  {
    String label = property.getName();
    try
    {
// TUHH Hack
      if( IWspmTuhhConstants.BUILDING_PROPERTY_BREITE.equals( property.getId() ) )
        label = Messages.getString("org.kalypso.model.wspm.tuhh.ui.panel.BridgePanel.1"); //$NON-NLS-1$
      if( IWspmTuhhConstants.BUILDING_PROPERTY_FORMBEIWERT.equals( property.getId() ) )
        label = Messages.getString("org.kalypso.model.wspm.tuhh.ui.panel.BridgePanel.2"); //$NON-NLS-1$
    }
    finally
    {
      return Messages.getString("org.kalypso.model.wspm.tuhh.ui.panel.BridgePanel.3",label, property.getUnit()); //$NON-NLS-1$
    }
  }

  /**
   * @see org.kalypso.model.wspm.ui.view.AbstractProfilView#doCreateControl(org.eclipse.swt.widgets.Composite,
   *      org.eclipse.ui.forms.widgets.FormToolkit)
   */
  @Override
  protected Control doCreateControl( final Composite parent, final FormToolkit toolkit )
  {
    m_toolkit = toolkit;
    m_propPanel = m_toolkit.createComposite( parent );
    m_propPanel.setLayout( new GridLayout( 2, false ) );

    createPropertyPanel();
    updateControls();
    return m_propPanel;
  }

  protected void createPropertyPanel( )
  {
    for( final PropertyLine line : m_lines )
    {
      line.dispose();
    }
    m_lines = new ArrayList<PropertyLine>( 8 );
    final IProfil profil = getProfil();
    final IProfileObject[] objects = profil == null ? null : profil.getProfileObjects();
    final IProfileObject building = objects == null || objects.length < 1 ? null : objects[0];
    if( building == null )
      return;
    for( final IComponent property : building.getObjectProperties() )
    {
      m_lines.add( new PropertyLine( m_toolkit, m_propPanel, property ) );
    }
    m_propPanel.layout();
  }

  protected void updateControls( )
  {
    final IProfileObject[] obj = getProfil().getProfileObjects();
    if( obj == null || obj.length < 1 )
    {
      return;
    }
    for( final PropertyLine line : m_lines )
      line.updateValue();
 //   m_propPanel.layout();
  }

  @Override
  public void onProfilChanged( final ProfilChangeHint hint, final IProfilChange[] changes )
  {
    if( hint.isObjectChanged() || hint.isObjectDataChanged() )
    {
      final Control control = getControl();
      if( control != null && !control.isDisposed() )
        control.getDisplay().asyncExec( new Runnable()
        {
          public void run( )
          {
           // createPropertyPanel();
            updateControls();
          }
        } );
    }
  }
}