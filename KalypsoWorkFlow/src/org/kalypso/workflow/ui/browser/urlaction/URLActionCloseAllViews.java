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
package org.kalypso.workflow.ui.browser.urlaction;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.ui.IViewReference;
import org.kalypso.workflow.ui.browser.AbstractURLAction;
import org.kalypso.workflow.ui.browser.ICommandURL;

/**
 * example: <br>
 * kalypso://closeAllEditors?exceptThese=org.eclipse.TestEditor1#org.eclipse.TestEditor2&separator=*&doSave=true;
 * 
 * @author kuepfer
 */
public class URLActionCloseAllViews extends AbstractURLAction
{

  private final static String COMMAND_NAME = "closeAllViews";

  private final static String PARAM_DO_NOT_CLOSE = "exceptThese";

  private final static String PARAM_SEPERATOR = "sparator";

  private String DEFAULT_SEPARATOR = "#";

  /**
   * @see org.kalypso.workflow.ui.browser.IURLAction#getActionName()
   */
  public String getActionName( )
  {
    return COMMAND_NAME;
  }

  /**
   * @see org.kalypso.workflow.ui.browser.IURLAction#run(org.kalypso.workflow.ui.browser.ICommandURL)
   */
  public boolean run( ICommandURL commandURL )
  {
    final String separator = commandURL.getParameter( PARAM_SEPERATOR );
    final String[] doNotCloseList;
    final String separatedList = commandURL.getParameter( PARAM_DO_NOT_CLOSE );

    if( separatedList != null && separatedList.length() > 0 )
    {
      if( separator != null )
        doNotCloseList = separatedList.split( separator );
      else
        doNotCloseList = separatedList.split( DEFAULT_SEPARATOR );
    }
    else
      doNotCloseList = new String[0];
    final IViewReference[] viewReferences = getActivePage().getViewReferences();
    for( IViewReference reference : viewReferences )
    {
      final String viewId = reference.getId();
      if( ArrayUtils.contains( doNotCloseList, viewId ) )
        continue;
      getActivePage().hideView( reference );
    }
    return true;
  }
}
