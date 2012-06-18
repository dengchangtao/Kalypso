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
package org.kalypso.ui.rrm.internal.cm.idw;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.kalypso.contribs.eclipse.jface.action.ActionButton;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.model.rcm.binding.IThiessenStation;
import org.kalypso.ogc.gml.command.ChangeFeaturesCommand;
import org.kalypso.ogc.gml.command.FeatureChange;
import org.kalypso.ogc.gml.featureview.control.AbstractFeatureControl;
import org.kalypsodeegree.model.feature.Feature;

/**
 * @author Gernot Belger
 * @author Holger Albert
 */
public class IdwWizardFeatureControl extends AbstractFeatureControl
{
  public IdwWizardFeatureControl( final Feature feature, final IPropertyType pt )
  {
    super( feature, pt );
  }

  @Override
  public Control createControl( final FormToolkit toolkit, final Composite parent, final int style )
  {
    final Composite composite = toolkit.createComposite( parent );
    GridLayoutFactory.swtDefaults().numColumns( 2 ).applyTo( composite );

    final IAction selectAllAction = new SelectAllAction( this, Boolean.TRUE );
    final Button selectAllButton = ActionButton.createButton( toolkit, composite, selectAllAction );
    setButtonData( selectAllButton );

    final IAction deselectAllAction = new SelectAllAction( this, Boolean.FALSE );
    final Button deselectAllButton = ActionButton.createButton( toolkit, composite, deselectAllAction );
    setButtonData( deselectAllButton );

    return composite;
  }

  @Override
  public void updateControl( )
  {
  }

  @Override
  public boolean isValid( )
  {
    return true;
  }

  private void setButtonData( final Button button )
  {
    final GC gc = new GC( button );
    gc.setFont( JFaceResources.getDialogFont() );
    final FontMetrics fontMetrics = gc.getFontMetrics();
    gc.dispose();

    final GridData data = new GridData( SWT.FILL, SWT.CENTER, true, false );
    final int widthHint = Dialog.convertHorizontalDLUsToPixels( fontMetrics, 30 );
    final Point minSize = button.computeSize( SWT.DEFAULT, SWT.DEFAULT, true );
    data.widthHint = Math.max( widthHint, minSize.x );
    button.setLayoutData( data );
  }

  void changeIsUsed( final Boolean active )
  {
    final List<FeatureChange> changes = new ArrayList<FeatureChange>();

    final List< ? > stations = (List< ? >) getFeature().getProperty( getFeatureTypeProperty() );
    for( final Object object : stations )
    {
      final FeatureChange change = new FeatureChange( (Feature) object, IThiessenStation.PROPERTY_ACTIVE, active );
      changes.add( change );
    }

    final ChangeFeaturesCommand command = new ChangeFeaturesCommand( getFeature().getWorkspace(), changes.toArray( new FeatureChange[changes.size()] ) );
    fireFeatureChange( command );
  }
}