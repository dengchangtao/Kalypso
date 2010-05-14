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
package org.kalypso.kalypsomodel1d2d.schema.binding.results;

import org.kalypso.observation.IObservation;
import org.kalypso.observation.result.IComponent;
import org.kalypso.observation.result.TupleResult;
import org.kalypso.ogc.gml.om.ObservationFeatureFactory;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree_impl.gml.binding.commons.AbstractFeatureBinder;

/**
 * @author Gernot Belger
 */
public class Hydrograph extends AbstractFeatureBinder implements IHydrograph
{
  public Hydrograph( final Feature featureToBind )
  {
    super( featureToBind, QNAME );
  }

  /**
   * @see org.kalypso.kalypsomodel1d2d.schema.binding.results.IHydrograph#getLoaction()
   */
  @Override
  public GM_Object getLocation( )
  {
    final GM_Object location = (GM_Object) getFeature().getProperty( QNAME_PROP_LOCATION );
    if( location == null )
      return null;

    return location;
  }

  /**
   * @see org.kalypso.kalypsomodel1d2d.schema.binding.results.IHydrograph#setLocation(org.kalypsodeegree.model.geometry.GM_Point)
   */
  public void setLocation( final GM_Point point )
  {
    getFeature().setProperty( QNAME_PROP_LOCATION, point );
  }

  public IObservation<TupleResult> initializeObservation( final String domainComponentUrn, final String valueComponentUrn )
  {
    final Feature obsFeature = getFeature();

    // if( domainComponentUrn.equals( Kalypso1D2DDictConstants.DICT_COMPONENT_TIME ) && valueComponentUrn.equals(
    // Kalypso1D2DDictConstants.DICT_COMPONENT_DISCHARGE ) )
    // getFeature().setProperty( QNAME_P_DIRECTION, new BigInteger( "0" ) );
    // else
    // getFeature().setProperty( QNAME_P_DIRECTION, null );

    final String[] componentUrns = new String[] { domainComponentUrn, valueComponentUrn };
    final IComponent[] components = new IComponent[componentUrns.length];

    for( int i = 0; i < components.length; i++ )
      components[i] = ObservationFeatureFactory.createDictionaryComponent( obsFeature, componentUrns[i] );

    final IObservation<TupleResult> obs = ObservationFeatureFactory.toObservation( obsFeature );

    final TupleResult result = obs.getResult();
    for( final IComponent component : components )
      result.addComponent( component );

    return obs;
  }

  public void setObservation( final IObservation<TupleResult> obs )
  {
    ObservationFeatureFactory.toFeature( obs, getFeature() );
  }

  public IObservation<TupleResult> getObservation( )
  {
    return ObservationFeatureFactory.toObservation( getFeature() );
  }

}
