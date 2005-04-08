package org.kaylpso.ui.wizard.wfs;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.kalypso.ogc.gml.GisTemplateMapModell;
import org.kalypso.ogc.gml.mapmodel.IMapModell;
import org.kalypso.ogc.gml.outline.GisMapOutlineViewer;
import org.kalypso.ui.ImageProvider;
import org.kalypso.ui.wizard.data.IKalypsoDataImportWizard;
import org.kaylpso.ui.action.AddThemeCommand;

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

public class ImportWfsSourceWizard extends Wizard implements IKalypsoDataImportWizard
{  
  private ImportWfsWizardPage importPage;

  private GisMapOutlineViewer m_outlineviewer;

  /*
   * 
   * @author kuepfer
   */
  public ImportWfsSourceWizard()
  {
    super();
    
  }

  /**
   * @see org.eclipse.jface.wizard.IWizard#performFinish()
   */
  public boolean performFinish()
  {
    IMapModell mapModell = m_outlineviewer.getMapModell();
    if( m_outlineviewer.getMapModell() != null )
      try
      {
        String[] allLayers = importPage.getLayers();
        String layers = "";
        for( int i = 0; i < allLayers.length; i++ )
        {
          String layer = allLayers[i];
          String layername = layer;
          if( i != 0 )
            layers = layers + "," + layer;
          else
            layers = layers + layer;
//          TODO here the featurePath is set to featureMember because this is the root element of the GMLWorkspace
//           it must be implemented to only set the name of the feature (relative path of feature) 
          
          
          final String geomType=importPage.guessGeometryType(layers);
          final String defaulStyle;
          if(geomType==null)
            defaulStyle="../.styles/unknowndefault.sld";          
          else if ("org.kalypsodeegree.model.geometry.GM_Point".equals(geomType))
            defaulStyle="../.styles/pointdefault.sld";
          else if ("org.kalypsodeegree.model.geometry.GM_LineString".equals(geomType))
            defaulStyle="../.styles/linestringdefault.sld";
          else if ("org.kalypsodeegree.model.geometry.GM_Polygon".equals(geomType))
            defaulStyle="../.styles/polygondefault.sld";
          else
            throw new UnsupportedOperationException("no style available for "+geomType);
            
          AddThemeCommand command = new AddThemeCommand( (GisTemplateMapModell)mapModell,
              "[WFS] " + layername, "wfs", "featureMember" , "URL=" + importPage.getUrl() + "#" + "FEATURE=" + layers ,"sld",
              "default", defaulStyle , "simple" );
          m_outlineviewer.postCommand( command, null );
          
        }
      }
      catch( Exception e )
      {
        e.printStackTrace();
        return false;
      }
    return true;
  }

  /**
   * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
   *      org.eclipse.jface.viewers.IStructuredSelection)
   */
  public void init( IWorkbench workbench, IStructuredSelection selection )
  {
    // nothing
  }

  public void addPages()
  {
    importPage = new ImportWfsWizardPage( "WfsImportPage", "Web Feature Service einbinden",
        ImageProvider.IMAGE_UTIL_UPLOAD_WIZ ) ;
    addPage(importPage );

  }

  public boolean performCancel()
  {

    this.dispose();
    return true;
  }

  /**
   * 
   * @see org.kalypso.ui.wizard.data.IKalypsoDataImportWizard#setOutlineViewer(org.kalypso.ogc.gml.outline.GisMapOutlineViewer)
   */
  public void setOutlineViewer( GisMapOutlineViewer outlineviewer )
  {
    m_outlineviewer = outlineviewer;
  }

}