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
package org.kalypso.ui.rrm.internal.timeseries.view.edit;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.services.IServiceLocator;
import org.kalypso.commons.databinding.IDataBinding;
import org.kalypso.contribs.eclipse.ui.forms.ToolkitUtils;
import org.kalypso.model.hydrology.binding.timeseries.IStation;
import org.kalypso.model.hydrology.binding.timeseries.ITimeseries;
import org.kalypso.ui.rrm.internal.i18n.Messages;
import org.kalypso.ui.rrm.internal.timeseries.QualityUniqueValidator;
import org.kalypso.ui.rrm.internal.timeseries.view.TimeseriesPropertiesComposite;
import org.kalypso.ui.rrm.internal.utils.featureBinding.FeatureBean;
import org.kalypso.zml.core.base.IMultipleZmlSourceElement;

/**
 * @author Dirk Kuch
 */
public class EditTimeseriesDialog extends ShowTimeseriesDialog
{
  private final FeatureBean<ITimeseries> m_timeseries;

  private final IDataBinding m_binding;

  public EditTimeseriesDialog( final Shell shell, final FeatureBean<ITimeseries> timeseries, final IMultipleZmlSourceElement source, final IDataBinding binding, final IServiceLocator context )
  {
    super( shell, source, context );

    m_timeseries = timeseries;
    m_binding = binding;

    setShellStyle( SWT.CLOSE | SWT.MAX | SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL | SWT.RESIZE );
    setHelpAvailable( false );
    setDialogHelpAvailable( false );
  }

  @Override
  protected final Control createDialogArea( final Composite parent )
  {
    final FormToolkit toolkit = ToolkitUtils.createToolkit( parent );
    getShell().setText( Messages.getString( "EditTimeseriesDialog_0" ) ); //$NON-NLS-1$

    final Composite base = (Composite) super.createDialogArea( parent );

    final Section controlSection = toolkit.createSection( base, Section.TITLE_BAR | Section.EXPANDED );
    controlSection.setText( Messages.getString( "EditTimeseriesDialog_1" ) ); //$NON-NLS-1$
    controlSection.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );

    final ITimeseries timeseries = m_timeseries.getFeature();
    final IStation station = (IStation) timeseries.getOwner();

    final String currentQuality = timeseries.getQuality();
    final String ignoreQuality = currentQuality == null ? StringUtils.EMPTY : currentQuality;
    final IValidator qualityValidator = new QualityUniqueValidator( station, ignoreQuality, timeseries );

    final TimeseriesPropertiesComposite properties = new TimeseriesPropertiesComposite( station, controlSection, m_timeseries, m_binding, false, qualityValidator );
    properties.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, false ) );

    controlSection.setClient( properties );

    return base;
  }
}