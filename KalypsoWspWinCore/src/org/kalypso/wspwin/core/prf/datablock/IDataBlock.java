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
package org.kalypso.wspwin.core.prf.datablock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author belger
 */
public interface IDataBlock
{
  /**
   * Liest den Inhalt des Datenblock aus einem Reader, evtl. vorhandene Daten werden gel�scht
   */
  public void readFromReader( final int count, final BufferedReader reader ) throws IOException;

  public String getFirstLine( );

  public String getSecondLine( );

  public String getThirdLine( );

  public void setFirstLine( final String text );

  public void setSecondLine( final String text );

  public void setThirdLine( final String text );

  public Double[] getX( );

  public Double[] getY( );

  public String[] getText( );

  public DataBlockHeader getDataBlockHeader( );

  /**
   * Anzahl der Koordinaten f�r Zeile 14
   */
  public int getCoordCount( );

  /**
   * Schreibt Datenblock in einen Writer
   */
  public void printToPrinter( final PrintWriter pw ) throws IOException;

}