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

import java.util.LinkedList;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.kalypso.contribs.eclipse.swt.events.DoubleModifyListener;
import org.kalypso.contribs.java.lang.NumberUtils;
import org.kalypso.model.wspm.core.IWspmConstants;
import org.kalypso.model.wspm.core.profil.IProfil;
import org.kalypso.model.wspm.core.profil.IProfilChange;
import org.kalypso.model.wspm.core.profil.IProfilPointMarker;
import org.kalypso.model.wspm.core.profil.changes.ActiveObjectEdit;
import org.kalypso.model.wspm.core.profil.changes.PointMarkerEdit;
import org.kalypso.model.wspm.core.profil.changes.PointMarkerSetPoint;
import org.kalypso.model.wspm.core.profil.changes.ProfilChangeHint;
import org.kalypso.model.wspm.core.profil.changes.ProfileObjectEdit;
import org.kalypso.model.wspm.core.profil.util.ProfilUtil;
import org.kalypso.model.wspm.tuhh.core.IWspmTuhhConstants;
import org.kalypso.model.wspm.tuhh.core.profile.buildings.BuildingUtil;
import org.kalypso.model.wspm.tuhh.core.profile.buildings.building.BuildingWehr;
import org.kalypso.model.wspm.tuhh.core.util.WspmProfileHelper;
import org.kalypso.model.wspm.tuhh.ui.i18n.Messages;
import org.kalypso.model.wspm.ui.KalypsoModelWspmUIImages;
import org.kalypso.model.wspm.ui.profil.operation.ProfilOperation;
import org.kalypso.model.wspm.ui.profil.operation.ProfilOperationJob;
import org.kalypso.model.wspm.ui.view.AbstractProfilView;
import org.kalypso.observation.result.IComponent;
import org.kalypso.observation.result.IRecord;

/**
 * @author kimwerner
 */
public class WeirPanel extends AbstractProfilView
{
  private class DeviderLine
  {
    protected final IProfilPointMarker m_devider;

    protected final Text m_position;

    protected Composite m_composite;

    public DeviderLine( final Composite parent, final IProfilPointMarker devider )
    {

      m_devider = devider;
      m_composite = m_toolkit.createComposite( parent );
      final GridLayout layout2 = new GridLayout( 4, true );
      layout2.marginWidth = 0;
      layout2.marginHeight = 0;
      m_composite.setLayout( layout2 );
      final GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
      gridData.horizontalSpan = 2;
      m_composite.setLayoutData( gridData );

      final Display display = parent.getDisplay();
      final Color goodColor = display.getSystemColor( SWT.COLOR_BLACK );
      final Color badColor = display.getSystemColor( SWT.COLOR_RED );
      final DoubleModifyListener doubleModifyListener = new DoubleModifyListener( goodColor, badColor );

      m_toolkit.createLabel( m_composite, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.panel.WeirPanel.0" ) ); //$NON-NLS-1$
      m_position = m_toolkit.createText( m_composite, "", SWT.TRAIL | SWT.SINGLE | SWT.BORDER ); //$NON-NLS-1$
      final GridData pointData = new GridData( SWT.FILL, SWT.CENTER, true, false );
      final Label spacer = m_toolkit.createSeparator( m_composite, SWT.SEPARATOR | SWT.HORIZONTAL );
      spacer.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false, 2, 1 ) );
      m_position.setLayoutData( pointData );

      m_position.addModifyListener( doubleModifyListener );
      m_position.addFocusListener( new FocusAdapter()
      {
        @Override
        public void focusGained( final FocusEvent e )
        {
          if( (m_position != null) && !m_position.isDisposed() )
            m_position.selectAll();
        }

        /**
         * @see org.eclipse.swt.events.FocusAdapter#focusLost(org.eclipse.swt.events.FocusEvent)
         */
        @Override
        public void focusLost( final FocusEvent e )
        {
          if( m_position == null || m_position.isDisposed() || m_devider == null )
            return;

          final double value = NumberUtils.parseQuietDouble( m_position.getText() );
          if( Double.isNaN( value ) )
            return;

          final Double pos = ProfilUtil.getDoubleValueFor( IWspmConstants.POINT_PROPERTY_BREITE, m_devider.getPoint() );
          if( ProfilUtil.compareValues( value, pos, 0.0001 ) )
            return;
          final IRecord point = ProfilUtil.findNearestPoint( getProfil(), value );
          final ProfilOperation operation = new ProfilOperation( Messages.getString( "org.kalypso.model.wspm.tuhh.ui.panel.WeirPanel.2" ), getProfil(), true ); //$NON-NLS-1$
          operation.addChange( new PointMarkerSetPoint( m_devider, point ) );
          operation.addChange( new ActiveObjectEdit( getProfil(), point, null ) );
          new ProfilOperationJob( operation ).schedule();
        }
      } );
    }

    public void dispose( )
    {
      m_composite.dispose();
    }

    public void refresh( )
    {
      m_position.setText( String.format( "%.4f", ProfilUtil.getDoubleValueFor( IWspmConstants.POINT_PROPERTY_BREITE, m_devider.getPoint() ) ) );

    }
// if( m_position < Integer.MAX_VALUE )
// {
// final Composite btnGroup = m_toolkit.createComposite( cmp );
// final GridLayout layout3 = new GridLayout( 2, true );
// layout3.marginWidth = 0;
// layout3.marginHeight = 0;
//
// btnGroup.setLayout( layout3 );
// btnGroup.setLayoutData( new GridData( SWT.CENTER, SWT.CENTER, false, false, 2, 1 ) );
//
//        final Button btnDel = m_toolkit.createButton( btnGroup, "", SWT.NONE ); //$NON-NLS-1$
//        btnDel.setToolTipText( Messages.getString( "org.kalypso.model.wspm.tuhh.ui.panel.WeirPanel.4" ) ); //$NON-NLS-1$
// btnDel.setImage( m_deleteImg );
// btnDel.setEnabled( m_position > -1 );
// btnDel.addSelectionListener( new SelectionAdapter()
// {
// @Override
// public void widgetSelected( final SelectionEvent e )
// {
//
// final IProfilChange change = new PointMarkerEdit( getMarker(), null );
//            final ProfilOperation operation = new ProfilOperation( Messages.getString( "org.kalypso.model.wspm.tuhh.ui.panel.WeirPanel.5" ), getProfil(), change, true ); //$NON-NLS-1$
// new ProfilOperationJob( operation ).schedule();
// }
// } );
//        final Button btnAdd = m_toolkit.createButton( btnGroup, "", SWT.NONE ); //$NON-NLS-1$
//        btnAdd.setToolTipText( Messages.getString( "org.kalypso.model.wspm.tuhh.ui.panel.WeirPanel.7" ) ); //$NON-NLS-1$
// btnAdd.setImage( m_addImg );
// btnAdd.addSelectionListener( new SelectionAdapter()
// {
// @Override
// public void widgetSelected( final SelectionEvent e )
// {
//
// final IProfilPointMarker marker = getMarker();
// final IProfil profil = getProfil();
// final IRecord point = profil.getPoint( getProfil().indexOfPoint( marker.getPoint() ) + 1 );
//
//            final ProfilOperation operation = new ProfilOperation( Messages.getString( "org.kalypso.model.wspm.tuhh.ui.panel.WeirPanel.8" ), profil, true ); //$NON-NLS-1$
// final IProfilPointMarker trenner = profil.createPointMarker( IWspmTuhhConstants.MARKER_TYP_WEHR, point );
//
// if( trenner != null )
// {
// final Object objVal = marker.getValue();
//
// final BuildingWehr building = WspmProfileHelper.getBuilding( profil, BuildingWehr.class );
// if( building == null )
// return;
//
// final Object dblVal = (objVal instanceof Double) ? objVal : BuildingUtil.getDoubleValueFor(
// IWspmTuhhConstants.BUILDING_PROPERTY_FORMBEIWERT, building );
// operation.addChange( new PointMarkerEdit( trenner, dblVal ) );
// operation.addChange( new ActiveObjectEdit( getProfil(), point, null ) );
// new ProfilOperationJob( operation ).schedule();
// }
// }
// } );
//
//        m_toolkit.createLabel( cmp, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.panel.WeirPanel.9" ) ); //$NON-NLS-1$
//        m_beiwert = m_toolkit.createText( cmp, "", SWT.TRAIL | SWT.SINGLE | SWT.BORDER ); //$NON-NLS-1$
// m_beiwert.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
// m_beiwert.addModifyListener( doubleModifyListener );
// m_beiwert.addFocusListener( new FocusAdapter()
// {
// @Override
// public void focusGained( final FocusEvent e )
// {
// if( (m_beiwert == null) || m_beiwert.isDisposed() )
// return;
// m_beiwert.selectAll();
// }
//
// /**
// * @see org.eclipse.swt.events.FocusAdapter#focusLost(org.eclipse.swt.events.FocusEvent)
// */
// @Override
// public void focusLost( final FocusEvent e )
// {
// if( m_beiwert == null || m_beiwert.isDisposed() )
// return;
//
// final String currentText = m_beiwert.getText();
// final String beiwertText = getBeiwertText();
//
// /* If nothing changed, just return */
// if( ObjectUtils.equals( currentText, beiwertText ) )
// return;
//
// final double value = NumberUtils.parseQuietDouble( currentText );
// // FIXME Performance, check if really anything changed
// if( Double.isNaN( value ) )
// return;
//
// if( m_position < 0 )
// {
// final BuildingWehr building = WspmProfileHelper.getBuilding( getProfil(), BuildingWehr.class );
// if( building == null )
// return;
//
// final IProfilChange change = new ProfileObjectEdit( building, building.getObjectProperty(
// IWspmTuhhConstants.BUILDING_PROPERTY_FORMBEIWERT ), value );
//              final ProfilOperation operation = new ProfilOperation( Messages.getString( "org.kalypso.model.wspm.tuhh.ui.panel.WeirPanel.11" ), getProfil(), change, true ); //$NON-NLS-1$
// new ProfilOperationJob( operation ).schedule();
// }
// else
// {
//              final ProfilOperation operation = new ProfilOperation( Messages.getString( "org.kalypso.model.wspm.tuhh.ui.panel.WeirPanel.12" ), getProfil(), true ); //$NON-NLS-1$
// operation.addChange( new PointMarkerEdit( getMarker(), value ) );
// new ProfilOperationJob( operation ).schedule();
// }
// }
// } );
// }
// }
//
// protected final IProfilPointMarker getMarker( )
// {
// final String m_markerID = (m_position < 0 | m_position == Integer.MAX_VALUE) ?
// IWspmTuhhConstants.MARKER_TYP_TRENNFLAECHE : IWspmTuhhConstants.MARKER_TYP_WEHR;
//
// final IComponent cmpMarker = getProfil().hasPointProperty( m_markerID );
// final IProfilPointMarker[] markers = getProfil().getPointMarkerFor( cmpMarker );
// if( m_position < 0 )
// return markers[0];
// else if( m_position == Integer.MAX_VALUE )
// return markers[markers.length - 1];
// else
// return markers[m_position];
// }
//
// public void dispose( )
// {
// m_beiwert.getParent().dispose();
// }
//
// public void refresh( )
// {
// final String markerText = getMarkerText();
//      m_point.setText( markerText ); //$NON-NLS-1$
//
// final String beiwertText = getBeiwertText();
// if( beiwertText != null && m_beiwert != null && !m_beiwert.isDisposed() )
// m_beiwert.setText( beiwertText );
// }
//
// protected String getBeiwertText( )
// {
// if( m_position < 0 )
// {
// final BuildingWehr building = WspmProfileHelper.getBuilding( getProfil(), BuildingWehr.class );
// if( building != null )
// {
// final IComponent beiwert = building.getObjectProperty( IWspmTuhhConstants.BUILDING_PROPERTY_FORMBEIWERT );
// // TODO: very strange code... check this! What is the type of these components??
// final String objValue = building.getValue( beiwert ).toString();
//          return NumberUtils.isDouble( objValue ) ? objValue : String.format( "%.4f", objValue ); //$NON-NLS-1$
// }
// }
// else if( m_position < Integer.MAX_VALUE )
// {
// final IProfilPointMarker marker = getMarker();
// final Object objValue = marker.getValue();
// if( objValue instanceof Number )
//          return String.format( "%.4f", objValue ); //$NON-NLS-1$
// }
//
//      return "";//$NON-NLS-1$
// }
//
// protected String getMarkerText( )
// {
// final IProfilPointMarker marker = getMarker();
//      return String.format( "%.4f", ProfilUtil.getDoubleValueFor( IWspmConstants.POINT_PROPERTY_BREITE, marker.getPoint() ) ); //$NON-NLS-1$
// }
  }

  private class ParameterLine
  {
    protected final IProfilPointMarker m_devider;

    protected final Composite m_composite;

    protected final Text m_value;

    public ParameterLine( final Composite parent, final IProfilPointMarker devider, final boolean canDelete )
    {
      m_composite = m_toolkit.createComposite( parent );

      final GridLayout layout3 = new GridLayout( 4, true );
      layout3.marginWidth = 0;
      layout3.marginHeight = 0;
      m_devider = devider;
      m_composite.setLayout( layout3 );
      m_composite.setLayoutData( new GridData( SWT.CENTER, SWT.CENTER, false, false, 2, 1 ) );

      final Button btnDel = m_toolkit.createButton( m_composite, "", SWT.NONE ); //$NON-NLS-1$
      btnDel.setToolTipText( Messages.getString( "org.kalypso.model.wspm.tuhh.ui.panel.WeirPanel.4" ) ); //$NON-NLS-1$
      btnDel.setImage( m_deleteImg );
      btnDel.setEnabled( canDelete );
      btnDel.addSelectionListener( new SelectionAdapter()
      {
        @Override
        public void widgetSelected( final SelectionEvent e )
        {

          final IProfilChange change = new PointMarkerEdit( m_devider, null );
          final ProfilOperation operation = new ProfilOperation( Messages.getString( "org.kalypso.model.wspm.tuhh.ui.panel.WeirPanel.5" ), getProfil(), change, true ); //$NON-NLS-1$
          new ProfilOperationJob( operation ).schedule();
        }
      } );
      final Button btnAdd = m_toolkit.createButton( m_composite, "", SWT.NONE ); //$NON-NLS-1$
      btnAdd.setToolTipText( Messages.getString( "org.kalypso.model.wspm.tuhh.ui.panel.WeirPanel.7" ) ); //$NON-NLS-1$
      btnAdd.setImage( m_addImg );
      btnAdd.addSelectionListener( new SelectionAdapter()
      {
        @Override
        public void widgetSelected( final SelectionEvent e )
        {

          final IProfilPointMarker marker = m_devider;
          final IProfil profil = getProfil();
          final IRecord point = profil.getPoint( getProfil().indexOfPoint( marker.getPoint() ) + 1 );

          final ProfilOperation operation = new ProfilOperation( Messages.getString( "org.kalypso.model.wspm.tuhh.ui.panel.WeirPanel.8" ), profil, true ); //$NON-NLS-1$
          final IProfilPointMarker trenner = profil.createPointMarker( IWspmTuhhConstants.MARKER_TYP_WEHR, point );

          if( trenner != null )
          {
            final Object objVal = marker.getValue();

            final BuildingWehr building = WspmProfileHelper.getBuilding( profil, BuildingWehr.class );
            if( building == null )
              return;

            final Object dblVal = (objVal instanceof Double) ? objVal : BuildingUtil.getDoubleValueFor( IWspmTuhhConstants.BUILDING_PROPERTY_FORMBEIWERT, building );
            operation.addChange( new PointMarkerEdit( trenner, dblVal ) );
            operation.addChange( new ActiveObjectEdit( getProfil(), point, null ) );
            new ProfilOperationJob( operation ).schedule();
          }
        }
      } );

      m_toolkit.createLabel( m_composite, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.panel.WeirPanel.9" ) ); //$NON-NLS-1$
      m_value = m_toolkit.createText( m_composite, "", SWT.TRAIL | SWT.SINGLE | SWT.BORDER ); //$NON-NLS-1$
      m_composite.layout();
// m_beiwert.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
// m_beiwert.addModifyListener( doubleModifyListener );
// m_beiwert.addFocusListener( new FocusAdapter()
// {
// @Override
// public void focusGained( final FocusEvent e )
// {
// if( (m_beiwert == null) || m_beiwert.isDisposed() )
// return;
// m_beiwert.selectAll();
// }
//
// /**
// * @see org.eclipse.swt.events.FocusAdapter#focusLost(org.eclipse.swt.events.FocusEvent)
// */
// @Override
// public void focusLost( final FocusEvent e )
// {
// if( m_beiwert == null || m_beiwert.isDisposed() )
// return;
//
// final String currentText = m_beiwert.getText();
// final String beiwertText = getBeiwertText();
//
// /* If nothing changed, just return */
// if( ObjectUtils.equals( currentText, beiwertText ) )
// return;
//
// final double value = NumberUtils.parseQuietDouble( currentText );
// // FIXME Performance, check if really anything changed
// if( Double.isNaN( value ) )
// return;
//
// if( m_position < 0 )
// {
// final BuildingWehr building = WspmProfileHelper.getBuilding( getProfil(), BuildingWehr.class );
// if( building == null )
// return;
//
// final IProfilChange change = new ProfileObjectEdit( building, building.getObjectProperty(
// IWspmTuhhConstants.BUILDING_PROPERTY_FORMBEIWERT ), value );
//               final ProfilOperation operation = new ProfilOperation( Messages.getString( "org.kalypso.model.wspm.tuhh.ui.panel.WeirPanel.11" ), getProfil(), change, true ); //$NON-NLS-1$
// new ProfilOperationJob( operation ).schedule();
// }
// else
// {
//               final ProfilOperation operation = new ProfilOperation( Messages.getString( "org.kalypso.model.wspm.tuhh.ui.panel.WeirPanel.12" ), getProfil(), true ); //$NON-NLS-1$
// operation.addChange( new PointMarkerEdit( getMarker(), value ) );
// new ProfilOperationJob( operation ).schedule();
// }
// }
// } );
    }

    public void dispose( )
    {
      m_composite.dispose();
    }
  }

  protected ComboViewer m_wehrart;

  protected Composite m_deviderGroup;

  private final LinkedList<DeviderLine> m_deviderLines;

  protected final Image m_deleteImg;

  protected final Image m_addImg;

  protected Label m_parameterLabel;

  private DeviderLine m_wehrStart;

  private DeviderLine m_wehrEnd;

  protected FormToolkit m_toolkit;

  protected final WeirLabelProvider m_labelProvider = new WeirLabelProvider();

  public WeirPanel( final IProfil profile )
  {
    super( profile );
    m_deviderLines = new LinkedList<DeviderLine>();
    m_deleteImg = KalypsoModelWspmUIImages.ID_BUTTON_WEHR_DELETE.createImage();
    m_addImg = KalypsoModelWspmUIImages.ID_BUTTON_WEHR_ADD.createImage();
  }

  @Override
  public void dispose( )
  {
    super.dispose();

    m_deleteImg.dispose();
    m_addImg.dispose();
  }

  /**
   * @see org.kalypso.model.wspm.ui.view.AbstractProfilView#doCreateControl(org.eclipse.swt.widgets.Composite,
   *      org.eclipse.ui.forms.widgets.FormToolkit)
   */
  @Override
  protected Control doCreateControl( final Composite parent, final FormToolkit toolkit )
  {
    m_toolkit = toolkit;
    final IProfil profile = getProfil();
    final Composite panel = toolkit.createComposite( parent );
    final GridLayout gridLayout = new GridLayout( 2, false );
    panel.setLayout( gridLayout );

    // Wehrart ComboBox

    final Label label = toolkit.createLabel( panel, Messages.getString( "org.kalypso.model.wspm.tuhh.ui.panel.WeirPanel.31" ), SWT.NONE ); //$NON-NLS-1$
    label.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING ) );

    m_wehrart = new ComboViewer( panel, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER );
    m_wehrart.getCombo().setLayoutData( new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL ) );
    m_wehrart.setContentProvider( new ArrayContentProvider() );
    m_wehrart.setLabelProvider( m_labelProvider );

    m_wehrart.setInput( m_labelProvider.getTypes() );
    m_wehrart.addSelectionChangedListener( new ISelectionChangedListener()
    {

      @Override
      public void selectionChanged( final SelectionChangedEvent event )
      {
        final BuildingWehr building = WspmProfileHelper.getBuilding( getProfil(), BuildingWehr.class );
        if( building == null )
          return;

        final IStructuredSelection selection = (IStructuredSelection) m_wehrart.getSelection();
        if( selection.isEmpty() )
          return;

        final String id = selection.getFirstElement().toString();
        final IComponent cWehr = building.getObjectProperty( IWspmTuhhConstants.BUILDING_PROPERTY_WEHRART );

        if( id.equals( building.getValue( cWehr ) ) )
          return;
        final IProfilChange change = new ProfileObjectEdit( building, cWehr, id );
        final ProfilOperation operation = new ProfilOperation( Messages.getString( "org.kalypso.model.wspm.tuhh.ui.panel.WeirPanel.32" ), getProfil(), change, true ); //$NON-NLS-1$
        new ProfilOperationJob( operation ).schedule();
      }
    } );
    toolkit.adapt( m_wehrart.getCombo() );

    final GridData plGridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
    plGridData.horizontalSpan = 2;
// m_parameterLabel.setLayoutData( plGridData );
// m_parameterLabel.setAlignment( SWT.RIGHT );

    final GridData psGridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
    psGridData.horizontalSpan = 2;

    final IProfilPointMarker[] devider = profile.getPointMarkerFor( IWspmTuhhConstants.MARKER_TYP_TRENNFLAECHE );

    m_wehrStart = new DeviderLine( panel, devider.length < 1 ? null : devider[0] );
// // Wehrparameter Group
// m_deviderGroup = toolkit.createComposite( panel );
// final GridLayout layout4 = new GridLayout( 1, false );
// // layout4.marginHeight=0;
// m_deviderGroup.setLayout( layout4 );
//
// final GridData groupData = new GridData( SWT.FILL, SWT.TOP, true, false );
//
// groupData.horizontalSpan = 2;
// // groupData.exclude = true;
// m_deviderGroup.setLayoutData( groupData );
    new ParameterLine( panel, devider[0], false );
    m_wehrEnd = new DeviderLine( panel, devider.length < 2 ? null : devider[1] );

    updateControls();

    return panel;
  }

  protected void updateControls( )
  {
    if( m_wehrart.getCombo().isDisposed() )
      return;

    final BuildingWehr building = WspmProfileHelper.getBuilding( getProfil(), BuildingWehr.class );
    if( building == null )
      return;

    final IComponent objProp = building.getObjectProperty( IWspmTuhhConstants.BUILDING_PROPERTY_WEHRART );
    final String id = (String) building.getValue( objProp );
    if( id != null )
      m_wehrart.setSelection( new StructuredSelection( id ) );

    // m_parameterLabel.setText( m_labelProvider.getText( id ) );
    m_wehrStart.refresh();
    m_wehrEnd.refresh();

// final IComponent cmpWehrTrenner = getProfil().hasPointProperty( IWspmTuhhConstants.MARKER_TYP_WEHR );
// final IProfilPointMarker[] deviders = getProfil().getPointMarkerFor( cmpWehrTrenner );
//
// while( m_deviderLines.size() < deviders.length )
// {
// m_deviderLines.add( new DeviderLine( m_deviderGroup, m_deviderLines.size() ) );
// }
//
// while( m_deviderLines.size() > deviders.length )
// {
// m_deviderLines.getLast().dispose();
// m_deviderLines.removeLast();
// }
//
// for( final DeviderLine devl : m_deviderLines )
// {
// devl.refresh();
// }

// m_deviderGroup.getParent().layout();
  }

  @Override
  public void onProfilChanged( final ProfilChangeHint hint, final IProfilChange[] changes )
  {
    if( hint.isObjectDataChanged() || hint.isProfilPropertyChanged() || hint.isMarkerMoved() || hint.isMarkerDataChanged() )
    {
      final Control control = getControl();
      if( control != null && !control.isDisposed() )
        control.getDisplay().asyncExec( new Runnable()
        {
          @Override
          public void run( )
          {
            updateControls();
          }
        } );
    }
  }
}