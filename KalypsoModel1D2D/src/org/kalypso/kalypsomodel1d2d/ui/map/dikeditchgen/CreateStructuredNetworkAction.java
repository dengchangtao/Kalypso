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
package org.kalypso.kalypsomodel1d2d.ui.map.dikeditchgen;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;
import org.kalypso.commons.eclipse.core.runtime.PluginImageProvider;
import org.kalypso.kalypsomodel1d2d.KalypsoModel1D2DPlugin;
import org.kalypso.kalypsomodel1d2d.KalypsoModel1D2DUIImages;

/**
 * @author Gernot Belger
 */
public class CreateStructuredNetworkAction extends Action
{
  private TriangulationBuilder m_builder;

  private CreateStructuredNetworkStrategy m_strategy;

  public CreateStructuredNetworkAction( final TriangulationBuilder builder, final CreateStructuredNetworkStrategy strategy )
  {
    m_builder = builder;
    m_strategy = strategy;

    setText( "Triangulieren" );
    setToolTipText( "Trianguliert ein Netz anhand der gew�hlten Parameter" );

    final PluginImageProvider imageProvider = KalypsoModel1D2DPlugin.getImageProvider();
    setImageDescriptor( imageProvider.getImageDescriptor( KalypsoModel1D2DUIImages.IMGKEY.OK ) );
  }

  @Override
  public void runWithEvent( final Event event )
  {
    m_strategy.createMesh( m_builder );
    // TODO: encapsulate into operation?
//    final Shell shell = event.widget.getDisplay().getActiveShell();
//    final IStatus result = ProgressUtilities.busyCursorWhile( operation );
//    if( !result.isOK() )
//      StatusDialog.open( shell, result, getText() );
  }

}