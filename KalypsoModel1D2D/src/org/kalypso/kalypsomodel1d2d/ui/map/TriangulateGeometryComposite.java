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
package org.kalypso.kalypsomodel1d2d.ui.map;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.kalypso.commons.databinding.DataBinder;
import org.kalypso.commons.databinding.forms.DatabindingForm;
import org.kalypso.commons.databinding.validation.NumberNotNegativeValidator;
import org.kalypso.commons.databinding.validation.NumberRangeValidator;
import org.kalypso.contribs.eclipse.jface.action.ActionButton;
import org.kalypso.core.status.StatusComposite;
import org.kalypso.gml.processes.constDelaunay.TriangleExe;
import org.kalypso.kalypsomodel1d2d.KalypsoModel1D2DPlugin;
import org.kalypso.kalypsomodel1d2d.ui.i18n.Messages;

/**
 * UI component for the {@link TriangulateGeometryWidget}.
 *
 * @author Gernot Belger
 *
 */
class TriangulateGeometryComposite extends Composite
{
  private final TriangulateGeometryData m_data;

  public TriangulateGeometryComposite( final FormToolkit toolkit, final Composite parent, final TriangulateGeometryData data )
  {
    super( parent, SWT.NONE );

    m_data = data;

    toolkit.adapt( this );

    setLayout( new FillLayout() );

    createContents( toolkit, this );
  }

  private void createContents( final FormToolkit toolkit, final Composite parent )
  {
    final ScrolledForm form = toolkit.createScrolledForm( parent );

    final DatabindingForm binding = new DatabindingForm( form, toolkit );

    final Composite body = form.getBody();
    GridLayoutFactory.swtDefaults().applyTo( body );

    final Section section = toolkit.createSection( body, Section.EXPANDED | Section.DESCRIPTION | Section.TITLE_BAR );
    section.setText( Messages.getString( "TriangulateGeometryWidget.5" ) ); //$NON-NLS-1$
    section.setDescription( Messages.getString( "TriangulateGeometryWidget.6" ) ); //$NON-NLS-1$
    section.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );

    final Composite sectionComposite = toolkit.createComposite( section, SWT.NONE );
    GridLayoutFactory.swtDefaults().numColumns( 2 ).applyTo( sectionComposite );
    section.setClient( sectionComposite );

    /* Check for triangle.exe */
    final IStatus statusTriangleExe = checkForTriangleExe();
    if( !statusTriangleExe.isOK() )
    {
      final StatusComposite triangleStatusComposite = new StatusComposite( sectionComposite, SWT.NONE );
      triangleStatusComposite.setStatus( statusTriangleExe );
      triangleStatusComposite.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, true, false, 2, 1 ) );
    }

    /* maximal Area */
    toolkit.createLabel( sectionComposite, Messages.getString( "TriangulateGeometryWidget.7" ) ); //$NON-NLS-1$

    final Text maxArea = toolkit.createText( sectionComposite, StringUtils.EMPTY, SWT.SINGLE | SWT.BORDER );
    maxArea.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, true, false ) );

    final ISWTObservableValue targetMaxArea = SWTObservables.observeText( maxArea, SWT.Modify );
    final IObservableValue modelMaxArea = BeansObservables.observeValue( m_data, TriangulateGeometryData.PROPERTY_MAX_AREA );
    final DataBinder maxAreaBinder = new DataBinder( targetMaxArea, modelMaxArea );
    maxAreaBinder.addTargetAfterConvertValidator( new NumberNotNegativeValidator( IStatus.ERROR, Messages.getString( "TriangulateGeometryWidget.8" ) ) ); //$NON-NLS-1$
    binding.bindValue( maxAreaBinder );

    /* Minimum Angle */
    toolkit.createLabel( sectionComposite, Messages.getString( "TriangulateGeometryWidget.10" ) ); //$NON-NLS-1$
    final Text minAngle = toolkit.createText( sectionComposite, "22", SWT.SINGLE | SWT.BORDER ); //$NON-NLS-1$
    minAngle.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, true, false ) );

    final ISWTObservableValue targetMinAngle = SWTObservables.observeText( minAngle, SWT.Modify );
    final IObservableValue modelMinAngle = BeansObservables.observeValue( m_data, TriangulateGeometryData.PROPERTY_MIN_ANGLE );
    final DataBinder minAngleBinder = new DataBinder( targetMinAngle, modelMinAngle );
    minAngleBinder.addTargetAfterConvertValidator( new NumberRangeValidator( IStatus.ERROR, 0, 32, Messages.getString( "TriangulateGeometryWidget.12" ) ) ); //$NON-NLS-1$
    binding.bindValue( minAngleBinder );

    /* Steiner checkbox */
    final Button noSteinerButton = toolkit.createButton( sectionComposite, Messages.getString( "TriangulateGeometryWidget.14" ), SWT.CHECK ); //$NON-NLS-1$
    noSteinerButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, true, false, 2, 1 ) );

    final ISWTObservableValue targetSteiner = SWTObservables.observeSelection( noSteinerButton );
    final IObservableValue modelSteiner = BeansObservables.observeValue( m_data, TriangulateGeometryData.PROPERTY_NO_STEINER );
    binding.bindValue( targetSteiner, modelSteiner );

    /* 'Apply to' button */
    /* 'Apply To' button */
    final Composite buttonPanel = toolkit.createComposite( body );
    GridLayoutFactory.swtDefaults().applyTo( buttonPanel );
    buttonPanel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

    final TriangulateGeometryApplyToAction convertToModelAction = new TriangulateGeometryApplyToAction( m_data );
    final Button buttonConvertToModel = ActionButton.createButton( toolkit, buttonPanel, convertToModelAction );
    buttonConvertToModel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
  }

  private IStatus checkForTriangleExe( )
  {
    final File triangleExe = TriangleExe.findTriangleExe();
    if( triangleExe != null && triangleExe.isFile() )
      return Status.OK_STATUS;

    return new Status( IStatus.WARNING, KalypsoModel1D2DPlugin.PLUGIN_ID, "Triangle.exe not found. Using simple triangulation." );
  }
}