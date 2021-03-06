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
package org.kalypso.model.hydrology.binding._11_6;

import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.kalypso.contribs.java.util.DateUtilities;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypsodeegree_impl.model.feature.Feature_Impl;

/**
 * Binding class for the older version of {org.kalypso.na.control}NAControl.
 * 
 * @author Gernot Belger
 */
public class NAControl extends Feature_Impl
{
  public static final String NS_NAMETA = "org.kalypso.na.control"; //$NON-NLS-1$

  private static final QName PROP_STARTSIMULATION = new QName( NS_NAMETA, "startsimulation" ); //$NON-NLS-1$

  private static final QName PROP_FORECAST = new QName( NS_NAMETA, "startforecast" ); //$NON-NLS-1$

  private static final QName PROP_HOURS_FORECAST = new QName( NS_NAMETA, "hoursforecast" ); //$NON-NLS-1$

  private static final QName PROP_MINUTES_TIMESTEP = new QName( NS_NAMETA, "minutesTimestep" ); //$NON-NLS-1$

  private static final QName PROP_VERSION_KALYPSONA = new QName( NS_NAMETA, "versionKalypsoNA" ); //$NON-NLS-1$

  private static final QName PROP_PNS = new QName( NS_NAMETA, "pns" ); //$NON-NLS-1$

  public static final QName PROP_XJAH = new QName( NS_NAMETA, "xjah" ); //$NON-NLS-1$

  private static final QName PROP_XWAHL2 = new QName( NS_NAMETA, "xwahl2" ); //$NON-NLS-1$

  private static final QName PROP_IPVER = new QName( NS_NAMETA, "ipver" ); //$NON-NLS-1$

  private static final QName PROP_RETURN_PERIOD = new QName( NS_NAMETA, "returnPeriod" ); //$NON-NLS-1$

  private static final QName PROP_OPTIMIZATION_START = new QName( NS_NAMETA, "startOptimization" ); //$NON-NLS-1$;

  private static final QName PROP_EDITOR = new QName( NS_NAMETA, "editor" ); //$NON-NLS-1$

  private static final QName PROP_DESCRIPTION = new QName( NS_NAMETA, "description" ); //$NON-NLS-1$

  private static final QName PROP_COMMENT = new QName( NS_NAMETA, "comment" ); //$NON-NLS-1$

  private static final QName PROP_CALCTIME = new QName( NS_NAMETA, "calctime" ); //$NON-NLS-1$

  public NAControl( final Object parent, final IRelationType parentRelation, final IFeatureType ft, final String id, final Object[] propValues )
  {
    super( parent, parentRelation, ft, id, propValues );
  }

  public Date getSimulationStart( )
  {
    return DateUtilities.toDate( getProperty( PROP_STARTSIMULATION, XMLGregorianCalendar.class ) );
  }

  public void setSimulationStart( final Date simulationStart )
  {
    setProperty( PROP_STARTSIMULATION, DateUtilities.toXMLGregorianCalendar( simulationStart ) );
  }

  public Date getStartForecast( )
  {
    return DateUtilities.toDate( getProperty( PROP_FORECAST, XMLGregorianCalendar.class ) );
  }

  public Date getSimulationEnd( )
  {
    final Date startForecast = getStartForecast();
    if( startForecast == null )
      return null;

    final Integer hoursOfForecast = getProperty( PROP_HOURS_FORECAST, Integer.class );
    if( hoursOfForecast == null )
      return startForecast;

    final int hoursForecast = hoursOfForecast.intValue();

    final Calendar c = Calendar.getInstance();
    c.setTime( startForecast );
    c.add( Calendar.HOUR, hoursForecast );
    return c.getTime();
  }

  public Integer getMinutesOfTimestep( )
  {
    final Integer minutesOfTimeStep = getProperty( PROP_MINUTES_TIMESTEP, Integer.class );

    if( minutesOfTimeStep == null || minutesOfTimeStep.intValue() == 0 )
      return 60;

    return minutesOfTimeStep.intValue();
  }

  public boolean isUsePrecipitationForm( )
  {
    final Boolean value = getProperty( PROP_PNS, Boolean.class );
    if( value == null )
      return false;

    return value.booleanValue();
  }

  public double getAnnuality( )
  {
    // the GUI asks for return period [a] - the fortran kernal needs annuality [1/a]
    final Double returnPeriod = getProperty( PROP_XJAH, Double.class );
    if( returnPeriod == null )
      return Double.NaN;

    return 1d / returnPeriod;
  }

  public Double getDurationMinutes( )
  {
    return (Double) getProperty( PROP_XWAHL2 );
  }

  public double getDurationHours( )
  {
    final Double durationMinutes = getDurationMinutes();
    if( durationMinutes == null )
      return Double.NaN;

    return durationMinutes / 60d;
  }

  public String getPrecipitationForm( )
  {
    return getProperty( PROP_IPVER, String.class );
  }

  public String getExeVersion( )
  {
    return getProperty( PROP_VERSION_KALYPSONA, String.class );
  }

  public void setExeVersion( final String version )
  {
    setProperty( PROP_VERSION_KALYPSONA, version );
  }

  public Integer getReturnPeriod( )
  {
    return getProperty( PROP_RETURN_PERIOD, Integer.class );
  }

  public Date getOptimizationStart( )
  {
    final XMLGregorianCalendar optimizationStartProperty = getProperty( PROP_OPTIMIZATION_START, XMLGregorianCalendar.class );
    if( optimizationStartProperty != null )
      return DateUtilities.toDate( optimizationStartProperty );
    return getSimulationStart();
  }

  public String getEditor( )
  {
    return getProperty( PROP_EDITOR, String.class );
  }

  public String getDescription2( )
  {
    return getProperty( PROP_DESCRIPTION, String.class );
  }

  public String getComment( )
  {
    return getProperty( PROP_COMMENT, String.class );
  }

  public Date getCalcTime( )
  {
    final XMLGregorianCalendar calendar = getProperty( PROP_CALCTIME, XMLGregorianCalendar.class );
    return DateUtilities.toDate( calendar );
  }
}