package org.kalypso.ui.internal.registry;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.dialogs.WorkbenchWizardElement;
import org.eclipse.ui.internal.registry.WizardsRegistryReader;
import org.kalypso.ui.IKalypsoUIConstants;
import org.kalypso.ui.KalypsoGisPlugin;

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

public class KalypsoWizardRegistryReader extends WizardsRegistryReader
{

  private String m_pluginPoint;

  /*
   * 
   * @author kuepfer
   */
  public KalypsoWizardRegistryReader( String pluginPointId )
  {
    super( pluginPointId );
    m_pluginPoint = pluginPointId;

  }

  /**
   * Reads the wizards in a registry. This method has been overwritten to only read Extensions from 
   * the org.kalypso.ui plugin. This is acomblished by sending the org.kalypso.ui as pluginid.
   */
  protected void readWizards() {
    if (!areWizardsRead()) {
      createEmptyWizardCollection();
      IExtensionRegistry registry = Platform.getExtensionRegistry();
      readRegistry(registry, KalypsoGisPlugin.getId(), m_pluginPoint);
    }
  }
}