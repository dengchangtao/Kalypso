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
package org.kalypso.ogc.gml.outline;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.kalypso.ogc.gml.IKalypsoTheme;
import org.kalypso.ogc.gml.mapmodel.IMapModellView;

/**
 * @author belger
 */
public class MoveThemeDownAction2 implements PluginMapOutlineAction
{

  /**
   * @see org.kalypso.ogc.gml.outline.PluginMapOutlineAction#run(org.kalypso.ogc.gml.outline.GisMapOutlineViewer)
   */
  public void run( IAction action )
  {
    if( action instanceof PluginMapOutlineActionDelegate )
    {
      PluginMapOutlineActionDelegate actionDelegate = (PluginMapOutlineActionDelegate) action;
      actionDelegate.getListManipulator().moveElementDown( ((IStructuredSelection) actionDelegate.getOutlineviewer().getSelection()).getFirstElement() );

    }
  }

  /**
   * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
   *      org.eclipse.jface.viewers.ISelection)
   */
  public void selectionChanged( IAction action, ISelection selection )
  {
    if( action instanceof PluginMapOutlineActionDelegate )
    {
      PluginMapOutlineActionDelegate outlineaction = (PluginMapOutlineActionDelegate) action;
      IMapModellView viewer = outlineaction.getOutlineviewer();
      boolean bEnable = false;
      if( selection instanceof IStructuredSelection )
      {
        final IStructuredSelection s = (IStructuredSelection) selection;

        if( !s.isEmpty() && (s.getFirstElement() instanceof IKalypsoTheme) )
        {
          final Object[] elements = viewer.getMapModell().getAllThemes();

          bEnable = (elements[elements.length - 1] != s.getFirstElement());
        }
        action.setEnabled( bEnable );

      }

    }

  }
}
