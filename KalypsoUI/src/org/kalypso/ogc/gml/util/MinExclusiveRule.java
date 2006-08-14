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
package org.kalypso.ogc.gml.util;

/**
 * This class is a rule for the MinExclusiveRestriction.
 * 
 * @author albert
 */
public class MinExclusiveRule implements IRule
{
  /**
   * This variable stores a value, that should be checked against. This is a minimum exclusive value.
   */
  public Number m_checkagainst;

  public MinExclusiveRule( Number checkagainst )
  {
    super();
    m_checkagainst = checkagainst;
  }

  /**
   * RULE : MinExclusiveRestriction
   * 
   * @see org.kalypso.ogc.gml.util.Rule#isValid(java.lang.Object)
   */
  public boolean isValid( Object object )
  {
    boolean ret = false;

    /* If the object does not exist or is no number, return true. */
    if( (object == null) || (!(object instanceof Number)) )
      return true;

    /* Cast in a number. It must be one, because a restriction for a minimum could only work with numbers. */
    Number number = (Number) object;

    if( number.floatValue() > m_checkagainst.floatValue() )
    {
      ret = true;
    }

    return ret;
  }

  /**
   * This function sets the parameter to check against.
   * 
   * @param checkagainst
   *          The min value.
   */
  public void setCheckParameter( Number checkagainst )
  {
    m_checkagainst = checkagainst;
  }

  /**
   * This function returns the parameter, against which is checked.
   * 
   * @return The min value.
   */
  public Number getCheckParameter( )
  {
    return m_checkagainst;
  }
}
