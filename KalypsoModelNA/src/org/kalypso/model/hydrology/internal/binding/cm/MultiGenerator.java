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
package org.kalypso.model.hydrology.internal.binding.cm;

import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypso.model.hydrology.binding.cm.IMultiGenerator;
import org.kalypso.model.rcm.binding.AbstractRainfallGenerator;
import org.kalypso.model.rcm.binding.IRainfallGenerator;
import org.kalypso.ogc.sensor.DateRange;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.IFeatureBindingCollection;
import org.kalypsodeegree_impl.model.feature.FeatureBindingCollection;

/**
 * The multi generator executes several generators based on the valid from and valid to dates.
 * 
 * @author Holger Albert
 */
public class MultiGenerator extends AbstractRainfallGenerator implements IMultiGenerator
{
  /**
   * The sub generators.
   */
  private final IFeatureBindingCollection<IRainfallGenerator> m_subGenerators;

  /**
   * The constructor.
   * 
   * @param parent
   *          The parent.
   * @param parentRelation
   *          The parent relation.
   * @param ft
   *          The feature type.
   * @param id
   *          The id.
   * @param propValues
   *          The property values.
   */
  public MultiGenerator( final Object parent, final IRelationType parentRelation, final IFeatureType ft, final String id, final Object[] propValues )
  {
    super( parent, parentRelation, ft, id, propValues );

    m_subGenerators = new FeatureBindingCollection<IRainfallGenerator>( this, IRainfallGenerator.class, MEMBER_SUB_GENERATOR, true );
  }

  /**
   * This function calculates the catchment model.
   * 
   * @param catchmentFeatures
   *          The catchment features.
   * @param range
   *          The date range.
   * @param log
   *          The log.
   * @param monitor
   *          A progress monitor.
   */
  @Override
  public IObservation[] createRainfall( final Feature[] catchmentFeatures, final DateRange range, final ILog log, IProgressMonitor monitor )
  {
    /* Monitor. */
    if( monitor == null )
      monitor = new NullProgressMonitor();

    try
    {
      /* Monitor. */
      monitor.beginTask( String.format( "Generiere Zeitreihen mit %d Gebietsmodellen...", m_subGenerators.size() ), m_subGenerators.size() * 200 );
      monitor.subTask( "Generiere Zeitreihen..." );

      // TODO Is calculated by the child generators and the results are managed outside.

      return null;
    }
    finally
    {
      /* Monitor. */
      monitor.done();
    }
  }

  /**
   * @see org.kalypso.model.rcm.binding.IMultiGenerator#getSubGenerators()
   */
  @Override
  public IFeatureBindingCollection<IRainfallGenerator> getSubGenerators( )
  {
    return m_subGenerators;
  }

  /**
   * @see org.kalypso.model.hydrology.binding.cm.IMultiGenerator#getLastModifiedInput()
   */
  @Override
  public long getLastModifiedInput( )
  {
    /* This is the last modified timestamp of the this generator itself. */
    final long lastModified = getLastModified();

    /* This is the last modified timestamp of the catchment models. */
    final long lastModifiedSubGenerators = getLastModifiedSubGenerators();

    return NumberUtils.max( new long[] { lastModified, lastModifiedSubGenerators } );
  }

  /**
   * @see org.kalypso.model.hydrology.binding.cm.IMultiGenerator#getLastModifiedSubGenerators()
   */
  @Override
  public long getLastModifiedSubGenerators( )
  {
    long result = -1;

    final IFeatureBindingCollection<IRainfallGenerator> subGenerators = getSubGenerators();
    for( final IRainfallGenerator subGenerator : subGenerators )
      result = Math.max( result, subGenerator.getLastModified() );

    return result;
  }
}