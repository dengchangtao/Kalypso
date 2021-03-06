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
package org.kalypso.kalypso1d2d.internal.importNet;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.kalypso.commons.eclipse.core.runtime.PluginImageProvider;
import org.kalypso.contribs.eclipse.jface.operation.ICoreRunnableWithProgress;
import org.kalypso.contribs.eclipse.jface.wizard.IUpdateable;
import org.kalypso.contribs.eclipse.ui.progress.ProgressUtilities;
import org.kalypso.core.status.StatusDialog;
import org.kalypso.kalypso1d2d.internal.i18n.Messages;
import org.kalypso.kalypso1d2d.pjt.Kalypso1d2dProjectImages;
import org.kalypso.kalypso1d2d.pjt.Kalypso1d2dProjectPlugin;
import org.kalypso.kalypsomodel1d2d.schema.Kalypso1D2DSchemaConstants;
import org.kalypso.kalypsomodel1d2d.ui.KalypsoModel1D2DStrings;
import org.kalypso.kalypsomodel1d2d.ui.map.util.UtilMap;
import org.kalypso.ogc.gml.IKalypsoFeatureTheme;
import org.kalypso.ogc.gml.map.IMapPanel;
import org.kalypso.ogc.gml.mapmodel.CommandableWorkspace;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree_impl.tools.GeometryUtilities;

import com.bce.gis.io.zweidm.IPolygonWithName;

/**
 * @author Gernot Belger
 */
public class ConvertToModelAction extends Action implements IUpdateable
{
  private final Import2dElementsData m_data;

  private final Import2dElementsWidget m_widget;

  public ConvertToModelAction( final Import2dElementsData data, final Import2dElementsWidget widget )
  {
    m_data = data;
    m_widget = widget;

    setText( KalypsoModel1D2DStrings.APPLY_BUTTON_LABEL ); //$NON-NLS-1$
    setToolTipText( Messages.getString( "ConvertToModelAction_0" ) ); //$NON-NLS-1$

    final PluginImageProvider imageProvider = Kalypso1d2dProjectPlugin.getImageProvider();
    setImageDescriptor( imageProvider.getImageDescriptor( Kalypso1d2dProjectImages.DESCRIPTORS.OK ) );
  }

  @Override
  public void update( )
  {
    final IPolygonWithName[] elements = m_data.getElements();
    setEnabled( !ArrayUtils.isEmpty( elements ) );
  }

  @Override
  public void runWithEvent( final Event event )
  {
    final Shell shell = event.widget.getDisplay().getActiveShell();

    final IMapPanel panel = m_widget.getMapPanel();
    if( panel == null )
      return;

    final IKalypsoFeatureTheme nodeTheme = UtilMap.findEditableTheme( panel, Kalypso1D2DSchemaConstants.WB1D2D_F_NODE );
    if( nodeTheme == null )
      return;

    final CommandableWorkspace discWorkspace = nodeTheme.getWorkspace();
    if( discWorkspace == null )
      return;

    final IPolygonWithName[] elements = m_data.getElements();
    if( ArrayUtils.isEmpty( elements ) )
    {
      MessageDialog.openInformation( shell, getText(), Messages.getString( "ConvertToModelAction_1" ) ); //$NON-NLS-1$
      return;
    }

    final ICoreRunnableWithProgress operation = new ConvertToModelOperation( discWorkspace, elements );
    final IStatus status = ProgressUtilities.busyCursorWhile( operation );
    if( !status.isOK() )
      StatusDialog.open( shell, status, getText() );
    else
    {
      final GM_Envelope boundingBox = m_data.getBoundingBox();

      /* clear old state */
      m_data.clearElements();

      /* jump to inserted elements */
      if( boundingBox != null )
      {
        final GM_Envelope wishBBox = GeometryUtilities.scaleEnvelope( boundingBox, 1.05 );
        m_widget.setExtent( wishBBox );
      }
    }
  }
}