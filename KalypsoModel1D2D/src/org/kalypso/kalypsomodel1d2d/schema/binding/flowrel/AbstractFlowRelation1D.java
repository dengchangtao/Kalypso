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
package org.kalypso.kalypsomodel1d2d.schema.binding.flowrel;

import org.kalypso.gmlschema.GMLSchemaUtilities;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypso.kalypsosimulationmodel.core.flowrel.FlowRelationship;
import org.kalypso.model.wspm.tuhh.core.gml.TuhhCalculation;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree_impl.model.feature.FeatureHelper;

/**
 * @author Gernot Belger
 */
public abstract class AbstractFlowRelation1D extends FlowRelationship implements IFlowRelation1D
{

  public AbstractFlowRelation1D( final Object parent, final IRelationType parentRelation, final IFeatureType ft, final String id, final Object[] propValues )
  {
    super( parent, parentRelation, ft, id, propValues );
  }

  /**
   * @see org.kalypso.kalypsomodel1d2d.schema.binding.flowrel.IFlowRelation1D#getCalculation()
   */
  @Override
  public TuhhCalculation getCalculation( )
  {
    final Feature calcFeature = getProperty( QNAME_PROP_TUHH_CALCULATION, Feature.class );
    if( calcFeature instanceof TuhhCalculation )
      return (TuhhCalculation) calcFeature;

    return null;
  }

  /**
   * @see org.kalypso.kalypsomodel1d2d.schema.binding.flowrel.IFlowRelation1D#setCalculation(org.kalypso.model.wspm.tuhh.core.gml.TuhhCalculation)
   */
  @Override
  public void setCalculation( final TuhhCalculation calculation )
  {
    final Feature flowRelFeature = this;
    final IFeatureType flowRelFT = GMLSchemaUtilities.getFeatureTypeQuiet( IFlowRelation1D.QNAME );
    final IRelationType calcRT = (IRelationType) flowRelFT.getProperty( IFlowRelation1D.QNAME_PROP_TUHH_CALCULATION );
    try
    {
      FeatureHelper.cloneFeature( flowRelFeature, calcRT, calculation );
    }
    catch( final Exception e )
    {
      // should never happen
      e.printStackTrace();
    }
  }
}
