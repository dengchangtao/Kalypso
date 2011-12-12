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
package org.kalypso.model.rcm.internal.binding;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypso.model.rcm.RcmConstants;
import org.kalypso.model.rcm.binding.ICatchmentGenerator;
import org.kalypso.model.rcm.binding.ICatchmentModel;
import org.kalypsodeegree.model.feature.FeatureList;
import org.kalypsodeegree_impl.model.feature.Feature_Impl;

/**
 * The catchment model contains generators for timeseries generation for catchments.
 * 
 * @author Holger Albert
 */
public class CatchmentModel extends Feature_Impl implements ICatchmentModel
{
  /**
   * The qname of the generator member.
   */
  public static final QName QNAME_CATCHMENT_GENERATOR_MEMBER = new QName( RcmConstants.NS_CM, "catchmentGeneratorMember" );

  /**
   * The qname of the catchment generator.
   */
  public static final QName QNAME_CATCHMENT_GENERATOR = new QName( RcmConstants.NS_CM, "CatchmentGenerator" );

  /**
   * The constructor.
   * 
   * @param parent
   * @param parentRelation
   * @param ft
   * @param id
   * @param propValues
   */
  public CatchmentModel( final Object parent, final IRelationType parentRelation, final IFeatureType ft, final String id, final Object[] propValues )
  {
    super( parent, parentRelation, ft, id, propValues );
  }

  /**
   * @see org.kalypso.model.rcm.binding.ICatchmentModel#getCatchmentGenerators()
   */
  @Override
  public ICatchmentGenerator[] getCatchmentGenerators( )
  {
    /* Memory for the results. */
    final List<CatchmentGenerator> results = new ArrayList<CatchmentGenerator>();

    /* Get all catchment generators. */
    final FeatureList catchmentGenerators = (FeatureList) getProperty( QNAME_CATCHMENT_GENERATOR_MEMBER );
    for( int i = 0; i < catchmentGenerators.size(); i++ )
      results.add( (CatchmentGenerator) catchmentGenerators.get( i ) );

    return results.toArray( new CatchmentGenerator[] {} );
  }
}