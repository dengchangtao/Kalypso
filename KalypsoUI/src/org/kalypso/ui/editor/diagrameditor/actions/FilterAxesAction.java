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
package org.kalypso.ui.editor.diagrameditor.actions;

import java.util.Vector;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.dialogs.ListDialog;
import org.kalypso.eclipse.jface.action.FullAction;
import org.kalypso.ogc.sensor.diagview.impl.DiagViewTemplate;
import org.kalypso.ogc.sensor.diagview.impl.DiagramAxis;
import org.kalypso.ogc.sensor.template.TemplateEvent;
import org.kalypso.ui.ImageProvider;
import org.kalypso.ui.editor.diagrameditor.ObsDiagOutlinePage;

/**
 * SwitchAxisTypePulldownActionDelegate
 * 
 * @author schlienger
 */
public class FilterAxesAction extends FullAction
{
  private static final String TITLE = "Achsentyp ignorieren";

  private final static String MSG = "W�hlen Sie der Achsentyp welche nicht dargestellt werden soll";

  private ObsDiagOutlinePage m_page;

  public FilterAxesAction( ObsDiagOutlinePage page )
  {
    super( "Achsen filtern", ImageProvider.IMAGE_UTIL_FILTER,
        "Erlaubt die Deaktivierung einer Achsentyp" );

    m_page = page;
  }

  /**
   * @see org.eclipse.jface.action.Action#run()
   */
  public void run( )
  {
    final DiagViewTemplate tpl = m_page.getTemplate();

    final Vector elts = new Vector( tpl.getDiagramAxes() );
    elts.add( 0, "(Keiner)" );

    final ListDialog dlg = new ListDialog( m_page.getSite().getShell() );

    dlg.setInput( elts.toArray() );
    dlg.setContentProvider( new ArrayContentProvider() );
    dlg.setLabelProvider( new LabelProvider() );
    dlg.setMessage( MSG );
    dlg.setTitle( TITLE );

    if( dlg.open() == Window.OK )
    {
      final Object[] res = dlg.getResult();

      if( res.length == 1 && res[0] instanceof DiagramAxis )
        tpl.setIgnoreType( ((DiagramAxis) res[0]).getLabel() );
      else
        tpl.setIgnoreType( null );
      
      tpl.fireTemplateChanged( new TemplateEvent( tpl.getThemes(), TemplateEvent.TYPE_REFRESH ) );
    }
  }
}