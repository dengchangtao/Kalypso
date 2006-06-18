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

import java.util.Calendar;
import java.util.Date;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.kalypso.ogc.gml.IKalypsoTheme;
import org.kalypso.ogc.gml.map.MapPanel;
import org.kalypso.ogc.gml.map.PointOfinterest;
import org.kalypso.ogc.gml.mapmodel.IMapModell;
import org.kalypso.ui.editor.mapeditor.GisMapEditor;
import org.kalypso.workflow.ui.browser.AbstractURLAction;
import org.kalypso.workflow.ui.browser.ICommandURL;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree_impl.model.cs.ConvenienceCSFactoryFull;
import org.kalypsodeegree_impl.model.geometry.GeometryFactory;
import org.opengis.cs.CS_CoordinateSystem;

/**
 * @author doemming
 */
public class URLActionShowInMap extends AbstractURLAction
{
  // final String showInMap = "kalypso://showInMap?title=" + nodeName//
  // + "&duration=4000" //
  // + "&x=" + x//
  // + "&y=" + y//
  // + "&crs=" + crsName;
  private final static String COMMAND_NAME = "showInMap";

  /**
   * title to show in map<br>
   * optional
   */
  private final static String PARAM_TITLE = "title";

  /**
   * time in millis, how long to show in map<br>
   * optional
   */
  private final static String PARAM_DURATION = "duration";

  /**
   * part of position
   */
  private final static String PARAM_X = "x";

  /**
   * part of position
   */
  private final static String PARAM_Y = "y";

  /**
   * part of position
   */
  private final static String PARAM_CRS = "crs";

  /**
   * It is assumed the active part is the GisMapEditor, use the URLSelectEditor to activate the apporpriate Editor.
   * 
   * @see URLActionSelectEditor
   */

  public URLActionShowInMap( )
  {
  }

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
    final IEditorReference[] editorReferences = getActivePage().getEditorReferences();
    String title = commandURL.getParameter( PARAM_TITLE );
    if( title == null )
      title = "";
    title=title.replaceAll("%20", " ");
    long duration = 3000l; // 3 seconds
    try
    {
      duration = Long.parseLong( commandURL.getParameter( PARAM_DURATION ) );
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
    final double x;
    final double y;
    final String crsName;
    try
    {
      x = Double.parseDouble( commandURL.getParameter( PARAM_X ) );
      y = Double.parseDouble( commandURL.getParameter( PARAM_Y ) );
      crsName = commandURL.getParameter( PARAM_CRS );
      final ConvenienceCSFactoryFull csFac = new ConvenienceCSFactoryFull();
      final CS_CoordinateSystem coordinateSystem = org.kalypsodeegree_impl.model.cs.Adapters.getDefault().export( csFac.getCSByName( crsName ) );
      final GM_Point point = GeometryFactory.createGM_Point( x, y, coordinateSystem );
      final long timeInMillis = Calendar.getInstance().getTimeInMillis();
      long validEnd = timeInMillis + 100;//duration;
      final PointOfinterest pointOfInterest = new PointOfinterest( title, validEnd, point );
      for( IEditorReference reference : editorReferences )
      {
        final IEditorPart editor = reference.getEditor( false );
        if( !(editor instanceof GisMapEditor) )
          continue;
        final GisMapEditor mapEditor = (GisMapEditor) editor;
        final MapPanel mapPanel = mapEditor.getMapPanel();
        mapPanel.addPointOfInterest( pointOfInterest );
      }
      return true;
    }
    catch( Exception e )
    {
      e.printStackTrace();
      return false;
    }
  }
}
