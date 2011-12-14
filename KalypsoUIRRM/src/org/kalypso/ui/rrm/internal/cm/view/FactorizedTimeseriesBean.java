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
package org.kalypso.ui.rrm.internal.cm.view;

import org.kalypso.model.rcm.binding.IFactorizedTimeseries;
import org.kalypso.ui.rrm.internal.timeseries.view.TimeseriesBean;
import org.kalypso.ui.rrm.internal.utils.featureBinding.FeatureBean;
import org.kalypsodeegree.model.feature.Feature;

/**
 * @author Gernot Belger
 * @author Holger Albert
 */
public class FactorizedTimeseriesBean extends FeatureBean<IFactorizedTimeseries>
{
  private TimeseriesBean m_timeseriesBean;

  public FactorizedTimeseriesBean( )
  {
    super( IFactorizedTimeseries.FEATURE_FACTORIZED_TIMESERIES );

    m_timeseriesBean = null;
  }

  public FactorizedTimeseriesBean( final IFactorizedTimeseries factorizedTimeseries )
  {
    super( factorizedTimeseries );

    m_timeseriesBean = null;
  }

  public String getLabel( )
  {
    return (String) getProperty( Feature.QN_DESCRIPTION );
  }

  public String getStationText( )
  {
    return getLabel();
  }

  public String getTimestepText( )
  {
    // TODO

    return "";
  }

  public String getQualityText( )
  {
    // TODO

    return "";
  }

  public String getFactorText( )
  {
    Double factor = getFeature().getFactor();
    if( factor != null && !factor.isNaN() && !factor.isInfinite() )
      return String.format( "%f", factor.doubleValue() );

    return "0.0";
  }
}