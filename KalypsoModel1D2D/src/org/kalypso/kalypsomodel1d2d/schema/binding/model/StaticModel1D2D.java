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
package org.kalypso.kalypsomodel1d2d.schema.binding.model;

import javax.xml.namespace.QName;

import org.kalypso.afgui.model.IModel;
import org.kalypso.kalypsomodel1d2d.schema.UrlCatalog1D2D;
import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IFEDiscretisationModel1d2d;
import org.kalypso.kalypsosimulationmodel.core.VersionedModel;
import org.kalypso.kalypsosimulationmodel.schema.UrlCatalogModelSimulationBase;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree_impl.model.feature.FeatureHelper;

/**
 * {@link AbstractFeatureBinder} based, default implementation for {@link IStaticModel1D2D}
 * 
 * @author Patrice Congo
 * 
 */
public class StaticModel1D2D extends VersionedModel implements IStaticModel1D2D
{

  public static final QName SIM_BASE_P_DICRRETISATION_MODEL = new QName( UrlCatalogModelSimulationBase.SIM_MODEL_NS, "feDiscretisationModel" ); //$NON-NLS-1$

  public static final QName WB1D2D_F_STATIC_MODEL = new QName( UrlCatalog1D2D.MODEL_1D2D_NS, "StaticModel1D2D" ); //$NON-NLS-1$

  public StaticModel1D2D( final Feature featureToBind )
  {
    this( featureToBind, StaticModel1D2D.WB1D2D_F_STATIC_MODEL );
  }

  public StaticModel1D2D( final Feature featureToBind, final QName qnameToBind )
  {
    super( featureToBind, qnameToBind );
  }

  /**
   * @see org.kalypso.kalypsomodel1d2d.schema.binding.model.IStaticModel1D2D#getDiscretisationModel()
   */
  public IFEDiscretisationModel1d2d getDiscretisationModel( )
  {
    return getSubModel( SIM_BASE_P_DICRRETISATION_MODEL, IFEDiscretisationModel1d2d.class );
  }

  private final <T extends IModel> T getSubModel( final QName propertyQName, final Class<T> adapterTargetClass )
  {
    final Feature feature = getFeature();
    final T subModel = FeatureHelper.resolveLink( feature, propertyQName, adapterTargetClass );
    return subModel;
  }
}
