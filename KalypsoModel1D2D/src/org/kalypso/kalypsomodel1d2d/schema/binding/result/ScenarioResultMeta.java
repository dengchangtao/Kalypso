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
package org.kalypso.kalypsomodel1d2d.schema.binding.result;

import org.eclipse.core.runtime.Path;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypso.kalypsosimulationmodel.core.resultmeta.IResultMeta;
import org.kalypso.kalypsosimulationmodel.core.resultmeta.ResultMeta;
import org.kalypsodeegree.model.feature.IFeatureBindingCollection;

/**
 * @author Thomas Jung
 * 
 */
public class ScenarioResultMeta extends ResultMeta implements IScenarioResultMeta
{
  public ScenarioResultMeta( final Object parent, final IRelationType parentRelation, final IFeatureType ft, final String id, final Object[] propValues )
  {
    super( parent, parentRelation, ft, id, propValues );
    // set the path and the name of the scenarioResultMeta
    // TODO: better place for this: while creation of a new scenario!!
    // right now, it is set to the following values:

    setPath( new Path( RESULTS ) );
    setName( ERGEBNISSE );
  }

  private static final String ERGEBNISSE = "Ergebnisse"; //$NON-NLS-1$

  private static final String RESULTS = "results"; //$NON-NLS-1$

  @Override
  public ICalcUnitResultMeta findCalcUnitMetaResult( final String calcUnitGmlID )
  {
    final IFeatureBindingCollection<IResultMeta> children = getChildren();
    for( final IResultMeta resultMeta : children )
    {
      if( resultMeta instanceof ICalcUnitResultMeta )
      {
        final ICalcUnitResultMeta calcUnitMeta = (ICalcUnitResultMeta) resultMeta;
        if( calcUnitMeta.getCalcUnit().equals( calcUnitGmlID ) )
          return calcUnitMeta;
      }
    }
    return null;
  }

  @Override
  public ICalcUnitResultMeta findCalcUnitMetaResultByName( final String calcUnitName )
  {
    final IFeatureBindingCollection<IResultMeta> children = getChildren();
    for( final IResultMeta resultMeta : children )
    {
      if( resultMeta instanceof ICalcUnitResultMeta )
      {
        final ICalcUnitResultMeta calcUnitMeta = (ICalcUnitResultMeta) resultMeta;
        if( calcUnitMeta.getName().equals( calcUnitName ) )
          return calcUnitMeta;
      }
    }
    return null;
  }

}
