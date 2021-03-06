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
package org.kalypso.model.rcm.binding;

import javax.xml.namespace.QName;

import org.eclipse.core.runtime.CoreException;
import org.kalypso.commons.tokenreplace.IStringResolver;
import org.kalypso.model.rcm.internal.UrlCatalogRcm;
import org.kalypso.ogc.sensor.DateRange;
import org.kalypsodeegree.model.feature.Feature;

/**
 * GML-binding for {org.kalypso.model.rcm.v2}Target
 *
 * @author Gernot Belger
 */
public interface ITarget extends Feature
{
  QName FEATURE_TARGET = new QName( UrlCatalogRcm.NS_RCM, "Target" ); //$NON-NLS-1$

  QName PROPERTY_CATCHMENT_COLLECTION = new QName( UrlCatalogRcm.NS_RCM, "catchments" ); //$NON-NLS-1$

  QName PROPERTY_FEATURE_PATH = new QName( UrlCatalogRcm.NS_RCM, "featurePath" ); //$NON-NLS-1$

  QName PROPERTY_OBSERVATION_PATH = new QName( UrlCatalogRcm.NS_RCM, "observationPath" ); //$NON-NLS-1$

  QName PROPERTY_FILTER = new QName( UrlCatalogRcm.NS_RCM, "filter" ); //$NON-NLS-1$

  QName PROPERTY_PERIOD = new QName( UrlCatalogRcm.NS_RCM, "period" ); //$NON-NLS-1$

  Feature[] resolveCatchments( ) throws CoreException;

  String getObservationPath( );

  String getFilter( );

  DateRange getPeriod( IStringResolver variables );

  void setPeriod( DateRange range );

  String getCatchmentPath( );

  void setCatchmentPath( String path );

  void setObservationPath( String path );

  void setFilter( String filter );

  void setCatchmentFeature( String href );

  Feature getResolvedCatchmentFeature( );
}
