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
package org.kalypso.ui.rrm.internal.timeseries.view;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.kalypso.commons.databinding.IDataBinding;
import org.kalypso.ogc.sensor.timeseries.TimeseriesUtils;
import org.kalypso.ui.rrm.internal.UIRrmImages;
import org.kalypso.ui.rrm.internal.timeseries.binding.Timeseries;

/**
 * @author Gernot Belger
 */
public class ParameterUiHandler implements ITimeseriesNodeUiHandler
{
  private final String m_parameterType;

  private final Timeseries[] m_timeseries;

  public ParameterUiHandler( final String parameterType, final Timeseries[] timeseries )
  {
    m_parameterType = parameterType;
    m_timeseries = timeseries;
  }

  @Override
  public String getTypeLabel( )
  {
    return "Parameter Type";
  }

  @Override
  public String getIdentifier( )
  {
    return StringUtils.EMPTY;
  }

  @Override
  public String getTreeLabel( )
  {
    final String parameterName = TimeseriesUtils.getName( m_parameterType );
    final String parameterUnit = TimeseriesUtils.getUnit( m_parameterType );

    return String.format( "%s [%s]", parameterName, parameterUnit );
  }

  @Override
  public ImageDescriptor getTreeImage( )
  {
    final String imageLocation = UIRrmImages.DESCRIPTORS.PARAMETER_TYPE_BASE.getImagePath() + "_" + m_parameterType + ".png"; //$NON-NLS-1$ //$NON-NLS-2$
    return UIRrmImages.id( imageLocation );
  }

  @Override
  public Control createControl( final FormToolkit toolkit, final Composite parent, final IDataBinding binding )
  {
    // TODO Auto-generated method stub
    return null;
  }
}