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
package org.kalypso.model.hydrology.binding.parameter;

import javax.xml.namespace.QName;

import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypso.model.hydrology.NaModelConstants;

/**
 * Binding class for {http://www.tuhh.de/parameter}DRWBMSoilLayerParameter
 * 
 * @author Dirk Kuch
 */
public class DRWBMSoilLayerParameter extends SoilLayerParameter
{
  private static final String NS_NAPARAMETER = NaModelConstants.NS_NAPARAMETER;

  public static final QName FEATURE_DRWBMSOILLAYERPARAMETER = new QName( NS_NAPARAMETER, "DRWBMSoilLayerParameter" ); //$NON-NLS-1$

  public static final QName PROPERTY_PIPE_DIAMETER = new QName( NS_NAPARAMETER, "diameterPipe" ); //$NON-NLS-1$

  public static final QName PROPERTY_PIPE_ROUGHNESS = new QName( NS_NAPARAMETER, "roughnessPipe" ); //$NON-NLS-1$

  public static final QName PROPERTY_DRAINAGE_PIPE_KF_VALUE = new QName( NS_NAPARAMETER, "drainagePipeKFvalue" ); //$NON-NLS-1$

  public static final QName PROPERTY_DRAINAGE_PIPE_SLOPE = new QName( NS_NAPARAMETER, "drainagePipeSlope" ); //$NON-NLS-1$

  public static final QName PROPERTY_OVERFLOW_HEIGHT = new QName( NS_NAPARAMETER, "overflowHeight" ); //$NON-NLS-1$

  public static final QName PROPERTY_AREA_PER_OUTLET = new QName( NS_NAPARAMETER, "areaPerOutlet" ); //$NON-NLS-1$

  public static final QName PROPERTY_WIDTH_OF_AREA = new QName( NS_NAPARAMETER, "widthOfArea" ); //$NON-NLS-1$

  public DRWBMSoilLayerParameter( final Object parent, final IRelationType parentRelation, final IFeatureType ft, final String id, final Object[] propValues )
  {
    super( parent, parentRelation, ft, id, propValues );
  }

  public Double getPipeDiameter( )
  {
    return getProperty( PROPERTY_PIPE_DIAMETER, Double.class );
  }

  public Double getPipeRoughness( )
  {
    return getProperty( PROPERTY_PIPE_ROUGHNESS, Double.class );
  }

  public Double getDrainagePipeKfValue( )
  {
    return getProperty( PROPERTY_DRAINAGE_PIPE_KF_VALUE, Double.class );
  }

  public Double getDrainagePipeSlope( )
  {
    return getProperty( PROPERTY_DRAINAGE_PIPE_SLOPE, Double.class );
  }

  public Double getOverflowHeight( )
  {
    return getProperty( PROPERTY_OVERFLOW_HEIGHT, Double.class );
  }

  public Double getAreaPerOutlet( )
  {
    return getProperty( PROPERTY_AREA_PER_OUTLET, Double.class );
  }

  public Double getWidthOfArea( )
  {
    return getProperty( PROPERTY_WIDTH_OF_AREA, Double.class );
  }
}