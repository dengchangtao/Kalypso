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
package org.kalypso.wspwin.core.prf.datablock;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kalypso.wspwin.core.i18n.Messages;

/**
 * @author belger
 */
public class DoubleDataBlock extends AbstractDataBlock
{
  private static NumberFormat DF = NumberFormat.getNumberInstance( Locale.US );

  static
  {
    DF.setMaximumIntegerDigits( 8 );
    DF.setMinimumIntegerDigits( 8 );
    DF.setMaximumFractionDigits( 4 );
    DF.setMinimumFractionDigits( 4 );
  }

  private final static Logger m_logger = Logger.getLogger( DoubleDataBlock.class.getName() );

  private Double[] m_values = new Double[0];

  public DoubleDataBlock( final DataBlockHeader dbh )
  {
    super( dbh );
  }

  /**
   * Muss von allen Implementatoren zuerst aufgerufen werden
   * 
   * @see org.kalypso.model.wspm.profileeditor.serializer.datablock.IDataBlock#readFromReader(java.lang.String,
   *      java.io.BufferedReader)
   */
  @Override
  public final void readFromReader( final int count, final BufferedReader reader )
  {
    readDoubleBlock( count, reader );
  }

  /**
   * @see org.kalypso.model.wspm.profileeditor.serializer.datablock.ICoordDataBlock#getX()
   */
  @Override
  public Double[] getX( )
  {
    return m_values;
  }

  private final Double[] readDoubleBlock( final int count, final BufferedReader reader )
  {
    m_values = new Double[count];

    int counter = 0;

    while( true )
    {
      final StringTokenizer sT;
      try
      {
        final String readLine = reader.readLine();
        if( readLine == null )
          throw new EOFException( Messages.getString( "org.kalypso.wspwin.core.prf.datablock.CoordDataBlock.0" ) ); //$NON-NLS-1$

        sT = new StringTokenizer( readLine );
      }
      catch( final IOException e )
      {
        // TODO: ist das gut, vielleicht doch lieber ne exception raus werfen und das ganze profil
        // verwerfen??
        // aber es hilft beim Einlesen von Geokoordinaten mit z.B. Schrottrauheiten(nur der Datanblock wird verworfen)
        m_logger.log( Level.SEVERE, e.getLocalizedMessage() );
        return m_values;
      }

      if( count < 1 )
        return m_values;

      String dblStr = ""; //$NON-NLS-1$
      final int ci = sT.countTokens();

      if( counter + ci < count )
      {
        m_logger.log( Level.SEVERE, Messages.getString( "org.kalypso.wspwin.core.prf.datablock.CoordDataBlock.1" ) ); //$NON-NLS-1$
        return m_values;
      }

      for( int i = 0; i < ci; i++ )
      {
        try
        {
          dblStr = sT.nextToken();
          m_values[counter] = Double.parseDouble( dblStr );
        }
        catch( final NoSuchElementException e )
        {
          m_values[counter] = 0.0;
          m_logger.log( Level.SEVERE, Messages.getString( "org.kalypso.wspwin.core.prf.datablock.CoordDataBlock.2", dblStr, counter + 1, e.getLocalizedMessage() ) ); //$NON-NLS-1$
        }
        counter++;
        if( counter == count )
          return m_values;
      }
    }
  }

  /**
   * @see org.kalypso.model.wspm.profileeditor.serializer.datablock.AbstractDataBlock#printToPrinterInternal(java.io.PrintWriter)
   */
  @Override
  public void printToPrinter( final PrintWriter pw )
  {
    getDataBlockHeader().printToPrinter( pw );
    writeDoubleBlock( m_values, pw );
  }

  /**
   * Schreibt einen Doubleblock raus
   * 
   * @param dbls
   *          -
   * @param pw
   *          -
   */
  private void writeDoubleBlock( final Double[] dbls, final PrintWriter pw )
  {
    for( int i = 0; i < dbls.length; i++ )
    {
      pw.write( String.format(Locale.US, "%13.4f", dbls[i] == null ? Double.NaN : dbls[i] ) );

      if( (i + 1) % 8 == 0 & i != dbls.length - 1 )
        pw.println();
    }

    pw.println();
  }

  /**
   * @see org.kalypso.model.wspm.profileeditor.serializer.datablock.IDataBlock#getCoordCount()
   */
  @Override
  public int getCoordCount( )
  {
    return m_values.length;
  }

  public final void setDoubles( final Double[] values )
  {
    if( values == null )
      throw new IllegalArgumentException();

    m_values = values;
  }

  /**
   * @see org.bce.wspm.core.prf.datablock.IDataBlock#getText()
   */
  @Override
  public String[] getText( )
  {
    return new String[] { m_values.toString() };
  }

  /**
   * @see org.kalypso.wspwin.core.prf.datablock.IDataBlock#getY()
   */
  @Override
  public Double[] getY( )
  {
    return new Double[] {};
  }
}