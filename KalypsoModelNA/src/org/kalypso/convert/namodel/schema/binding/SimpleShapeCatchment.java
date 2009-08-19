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
package org.kalypso.convert.namodel.schema.binding;

import javax.xml.namespace.QName;

import org.kalypso.convert.namodel.NaModelConstants;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypsodeegree.model.geometry.GM_MultiSurface;
import org.kalypsodeegree_impl.model.feature.Feature_Impl;

/**
 * @author antanas
 */
public class SimpleShapeCatchment extends Feature_Impl
{
  public static final QName QNAME = new QName( NaModelConstants.NS_NASIMPLESHAPECATCHMENT, "SimpleShapeCatchment" ); //$NON-NLS-1$

  public static final QName QNAME_PROP_GEOMETRY = new QName( NaModelConstants.NS_NASIMPLESHAPECATCHMENT, "location" ); //$NON-NLS-1$

  public static final QName QNAME_PROP_DRAINAGE_NODE_ID = new QName( NaModelConstants.NS_NASIMPLESHAPECATCHMENT, "drainageNodeID" ); //$NON-NLS-1$

  protected SimpleShapeCatchment( final Object parent, final IRelationType parentRelation, final IFeatureType ft, final String id, final Object[] propValues )
  {
    super( parent, parentRelation, ft, id, propValues );
  }

  public GM_MultiSurface getGeometry( )
  {
    return getProperty( QNAME_PROP_GEOMETRY, GM_MultiSurface.class );
  }

  public void setGeometry( final GM_MultiSurface geometry )
  {
    setProperty( QNAME_PROP_GEOMETRY, geometry );
  }

  public void setDrainageNodeID( final String value )
  {
    setProperty( QNAME_PROP_DRAINAGE_NODE_ID, value );
  }

  public String getDrainageNodeID( )
  {
    return getProperty( QNAME_PROP_DRAINAGE_NODE_ID, String.class );
  }

}
