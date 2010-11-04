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
package org.kalypso.model.hydrology.binding.model;

import javax.xml.namespace.QName;

import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypso.model.hydrology.NaModelConstants;

/**
 * Binding class for {http://www.tuhh.de/kalypsoNA}StorageChannel.
 * 
 * @author Gernot Belger
 */
public class StorageChannel extends Channel
{
  public static final QName FEATURE_STORAGE_CHANNEL = new QName( NaModelConstants.NS_NAMODELL, "StorageChannel" ); //$NON-NLS-1$

  private static final QName GENERATE_RESULT_PROP = new QName( NS_NAMODELL, "generateResult" ); //$NON-NLS-1$

  private static final QName IKNOT_MEMBER = new QName( NS_NAMODELL, "iknotNodeMember" ); //$NON-NLS-1$

  public StorageChannel( final Object parent, final IRelationType parentRelation, final IFeatureType ft, final String id, final Object[] propValues )
  {
    super( parent, parentRelation, ft, id, propValues );
  }

  public boolean isGenerateResults( )
  {
    return getBoolean( GENERATE_RESULT_PROP, false );
  }

  public void setGenerateResults( final boolean value )
  {
    setProperty( GENERATE_RESULT_PROP, value );
  }

  public Node getOverflowNode( )
  {
    return getProperty( IKNOT_MEMBER, Node.class );
  }

}
