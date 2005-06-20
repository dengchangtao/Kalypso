/*--------------- Kalypso-Header --------------------------------------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 Technical University Hamburg-Harburg (TUHH)
 Institute of River and coastal engineering
 Denickestr. 22
 21073 Hamburg, Germany
 http://www.tuhh.de/wb

 and
 
 Bjoernsen Consulting Engineers (BCE)
 Maria Trost 3
 56070 Koblenz, Germany
 http://www.bjoernsen.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 E-Mail:
 belger@bjoernsen.de
 schlienger@bjoernsen.de
 v.doemming@tuhh.de
 
 ---------------------------------------------------------------------------------------------------*/
package org.kalypso.util.progress;

/**
 * Ein ProgressMonitor, der nichts tut
 * 
 * @author belger
 */
public class NullProgressMonitor implements IProgressMonitor
{
  /**
   * @see org.kalypso.util.progress.IProgressMonitor#beginTask(java.lang.String, int)
   */
  public void beginTask( String name, int totalWork )
  {
  //  
  }

  /**
   * @see org.kalypso.util.progress.IProgressMonitor#done()
   */
  public void done()
  {
  //
  }

  /**
   * @see org.kalypso.util.progress.IProgressMonitor#isCanceled()
   */
  public boolean isCanceled()
  {
    return false;
  }

  /**
   * @see org.kalypso.util.progress.IProgressMonitor#setCanceled(boolean)
   */
  public void setCanceled( boolean value )
  {
  //  
  }

  /**
   * @see org.kalypso.util.progress.IProgressMonitor#setTaskName(java.lang.String)
   */
  public void setTaskName( String name )
  {
  //
  }

  /**
   * @see org.kalypso.util.progress.IProgressMonitor#subTask(java.lang.String)
   */
  public void subTask( String name )
  {
  //
  }

  /**
   * @see org.kalypso.util.progress.IProgressMonitor#worked(int)
   */
  public void worked( int work )
  {
  //
  }
}