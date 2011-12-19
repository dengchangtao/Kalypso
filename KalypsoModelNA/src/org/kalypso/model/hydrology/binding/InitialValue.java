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
package org.kalypso.model.hydrology.binding;

import java.util.Date;

import javax.xml.namespace.QName;

import org.kalypso.contribs.java.util.DateUtilities;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypsodeegree_impl.model.feature.Feature_Impl;

/**
 * Binding class for {org.kalypso.namodell.control_v2}InitialValue.
 *
 * @author Gernot Belger
 */
public class InitialValue extends Feature_Impl
{
  public static final QName FEATURE_INITIAL_VALUE = new QName( NAModellControl.NS_NACONTROL, "InitialValue" ); //$NON-NLS-1$

  private static final QName PROP_IS_ACTIVE = new QName( NAModellControl.NS_NACONTROL, "isActive" ); //$NON-NLS-1$

  private static final QName PROP_INITIALDATE = new QName( NAModellControl.NS_NACONTROL, "initialDate" ); //$NON-NLS-1$


  public InitialValue( final Object parent, final IRelationType parentRelation, final IFeatureType ft, final String id, final Object[] propValues )
  {
    super( parent, parentRelation, ft, id, propValues );
  }

  public boolean isActive( )
  {
    final Boolean doWrite = getProperty( PROP_IS_ACTIVE, Boolean.class );
    if( doWrite == null )
      return false;

    return doWrite;
  }

  public void setActive( final boolean isActive )
  {
    setProperty( PROP_IS_ACTIVE, isActive );
  }

  public Date getInitialDate( )
  {
    return DateUtilities.toDate( getProperty( PROP_INITIALDATE ) );
  }

  public void setInitialDate( final Date date )
  {
    setProperty( PROP_INITIALDATE, DateUtilities.toXMLGregorianCalendar( date ) );
  }

}