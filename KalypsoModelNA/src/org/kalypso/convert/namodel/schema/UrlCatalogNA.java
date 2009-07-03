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

package org.kalypso.convert.namodel.schema;

import java.net.URL;
import java.util.Map;

import org.kalypso.contribs.java.net.AbstractUrlCatalog;
import org.kalypso.convert.namodel.NaModelConstants;

/**
 * class UrlCatalogNA
 *
 * provides the schemas for kalypso rainfall runoff simulation
 *
 * created by
 *
 * @author doemming (08.05.2005)
 */
public class UrlCatalogNA extends AbstractUrlCatalog
{
  /**
   * @see org.kalypso.contribs.java.net.AbstractUrlCatalog#fillCatalog(java.lang.Class, java.util.Map)
   */
  @Override
  protected void fillCatalog( final Class<?> myClass, final Map<String, URL> catalog, final Map<String, String> prefixes )
  {
    // TODO: this type of versioning does not make any sense!
    // If a real versioning should be done, namespace MUST change (see W3C recommendations)
    // Anything else is done by CVS/SVN
    catalog.put( NaModelConstants.NS_NAMETA, myClass.getResource( "v1.0/control.xsd" ) );
    catalog.put( NaModelConstants.NS_NAMODELL, myClass.getResource( "v1.0/namodell.xsd" ) );
    catalog.put( NaModelConstants.NS_NACONTROL, myClass.getResource( "v0.8/nacontrol.xsd" ) );
    catalog.put( NaModelConstants.NS_NAHYDROTOP, myClass.getResource( "v0.9/hydrotop.xsd" ) );
    catalog.put( NaModelConstants.NS_NAPARAMETER, myClass.getResource( "v0.8/parameter.xsd" ) );
    catalog.put( NaModelConstants.NS_OMBROMETER, myClass.getResource( "v0.2/ombrometer.xsd" ) );
    catalog.put( NaModelConstants.NS_SYNTHN, myClass.getResource( "v0.6/synthN.xsd" ) );
    catalog.put( NaModelConstants.NS_INIVALUES, myClass.getResource( "v0.8/initialValues.xsd" ) );
    catalog.put( NaModelConstants.NS_NAFORTRANLOG, myClass.getResource( "v1.0/NAFortranLog.xsd" ) );

    catalog.put( NaModelConstants.NS_NALANDUSE, myClass.getResource( "v1.0/landuse.xsd" ) );
    catalog.put( NaModelConstants.NS_NAPEDOLOGIE, myClass.getResource( "v1.0/pedologie.xsd" ) );
    catalog.put( NaModelConstants.NS_NAGEOLOGIE, myClass.getResource( "v1.0/geologie.xsd" ) );
    catalog.put( NaModelConstants.NS_NASUDS, myClass.getResource( "v1.0/suds.xsd" ) );

    // REMARK: these prefix definition are crucial for the optimisation, as the
    // sce xpathes rely on this special prefix.
    prefixes.put( NaModelConstants.NS_NAMETA, "rrmMeta" );
    prefixes.put( NaModelConstants.NS_NAMODELL, "rrm" );
    prefixes.put( NaModelConstants.NS_NACONTROL, "rrmControl" );
    prefixes.put( NaModelConstants.NS_NAHYDROTOP, "rrmHydrotop" );
    prefixes.put( NaModelConstants.NS_NAPARAMETER, "rrmParam" );
    prefixes.put( NaModelConstants.NS_OMBROMETER, "rrmOmbro" );

    prefixes.put( NaModelConstants.NS_NALANDUSE, "rrmLanduse" );
    prefixes.put( NaModelConstants.NS_NAPEDOLOGIE, "rrmPedo" );
    prefixes.put( NaModelConstants.NS_NAGEOLOGIE, "rrmGeo" );
    prefixes.put( NaModelConstants.NS_NASUDS, "rrmSuds" );
  }
}
