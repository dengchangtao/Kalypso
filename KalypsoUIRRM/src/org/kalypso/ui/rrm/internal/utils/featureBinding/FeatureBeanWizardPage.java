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
package org.kalypso.ui.rrm.internal.utils.featureBinding;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.kalypso.commons.databinding.IDataBinding;
import org.kalypso.commons.databinding.jface.wizard.DatabindingWizardPage;
import org.kalypso.ui.rrm.internal.i18n.Messages;

/**
 * @author Gernot Belger
 */
public abstract class FeatureBeanWizardPage extends WizardPage
{
  protected FeatureBeanWizardPage( final String pageName )
  {
    super( pageName );

    setTitle( Messages.getString( "FeatureBeanWizardPage_0" ) ); //$NON-NLS-1$
    setDescription( Messages.getString( "FeatureBeanWizardPage_1" ) ); //$NON-NLS-1$
  }

  @Override
  public void createControl( final Composite parent )
  {
// final FormToolkit toolkit = ToolkitUtils.createToolkit( parent );
// // We do not want the white look and feel in a wizard page
// toolkit.setBackground( parent.getBackground() );
// toolkit.adapt( parent );

    final IDataBinding binding = new DatabindingWizardPage( this, null );

    final Control control = createFeatureBeanControl( parent, binding );
    setControl( control );
  }

  protected abstract Control createFeatureBeanControl( Composite parent, IDataBinding binding );
}