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
package org.kalypso.model.wspm.tuhh.schema.schemata;

import org.kalypso.model.wspm.tuhh.core.IWspmTuhhConstants;

/**
 * Constants for the use of the QIntervall-Schema
 * 
 * @author Gernot Belger
 */
public interface IWspmTuhhQIntervallConstants extends IWspmTuhhConstants
{
  public static final String DICT_BASE = "urn:ogc:gml:dict:kalypso:model:wspmtuhh:qIntervallPointsComponents#";

  public static final String DICT_PHENOMENON_WATERLEVEL = DICT_BASE + "phenomenonWaterlevel";

  public static final String DICT_PHENOMENON_RUNOFF = DICT_BASE + "phenomenonRunoff";

  public static final String DICT_PHENOMENON_AREA = DICT_BASE + "phenomenonArea";

  public static final String DICT_PHENOMENON_ALPHA = DICT_BASE + "phenomenonAlpha";

  public static final String DICT_COMPONENT_WATERLEVEL = DICT_BASE + "Waterlevel";

  public static final String DICT_COMPONENT_WATERLEVEL_UPSTREAM = DICT_BASE + "WaterlevelUpstream";

  public static final String DICT_COMPONENT_WATERLEVEL_DOWNSTREAM = DICT_BASE + "WaterlevelDownstream";

  public static final String DICT_COMPONENT_DEPTH = DICT_BASE + "Depth";

  public static final String DICT_COMPONENT_AREA = DICT_BASE + "Area";

  public static final String DICT_COMPONENT_RUNOFF = DICT_BASE + "Runoff";

  public static final String DICT_COMPONENT_ALPHA = DICT_BASE + "Alpha";

  public static final String DICT_COMPONENT_DELTA_AREA = DICT_BASE + "DeltaArea";

  public static final String DICT_COMPONENT_DELTA_RUNOFF = DICT_BASE + "DeltaRunoff";

  public static final String DICT_COMPONENT_DELTA_ALPHA = DICT_BASE + "DeltaAlpha";

}
