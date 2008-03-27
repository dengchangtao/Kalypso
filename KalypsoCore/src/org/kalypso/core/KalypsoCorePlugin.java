/*--------------- Kalypso-Header ------------------------------------------

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

 --------------------------------------------------------------------------*/

package org.kalypso.core;

import java.io.File;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.kalypso.core.catalog.CatalogManager;
import org.kalypso.core.catalog.CatalogSLD;
import org.kalypso.ogc.gml.selection.FeatureSelectionManager2;
import org.kalypso.ogc.gml.selection.IFeatureSelectionManager;
import org.kalypsodeegree.KalypsoDeegreePlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Gernot Belger
 */
public class KalypsoCorePlugin extends Plugin
{
  private static KalypsoCorePlugin m_default;

  /**
   * Storage for preferences.
   */
  private ScopedPreferenceStore m_preferenceStore;

  private IFeatureSelectionManager m_selectionManager = null;

  private CatalogManager m_catalogManager = null;

  private CatalogSLD m_sldCatalog = null;

  public static String getID( )
  {
    return getDefault().getBundle().getSymbolicName();
  }

  public static KalypsoCorePlugin getDefault( )
  {
    return m_default;
  }

  public KalypsoCorePlugin( )
  {
    m_default = this;
  }

  /**
   * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
   */
  @Override
  public void start( final BundleContext context ) throws Exception
  {
    super.start( context );
  }

  /**
   * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
   */
  @Override
  public void stop( final BundleContext context ) throws Exception
  {
    m_catalogManager = null;
    m_sldCatalog = null;
    m_selectionManager = null;

    savePluginPreferences();

    super.stop( context );
  }

  public CatalogManager getCatalogManager( )
  {
    if( m_catalogManager == null )
    {
      final File stateLocation = getStateLocation().toFile();
      final File managerDir = new File( stateLocation, "catalogManager" );
      managerDir.mkdirs();
      m_catalogManager = new CatalogManager( managerDir );
      KalypsoCoreExtensions.loadXMLCatalogs( m_catalogManager );
    }

    return m_catalogManager;
  }

  public CatalogSLD getSLDCatalog( )
  {
    if( m_sldCatalog == null )
    {
      final File stateLocation = getStateLocation().toFile();
      final File styleCatalogDir = new File( stateLocation, "style-catalog" );
      styleCatalogDir.mkdirs();
      m_sldCatalog = new CatalogSLD( getCatalogManager(), styleCatalogDir );
    }

    return m_sldCatalog;
  }

  public IFeatureSelectionManager getSelectionManager( )
  {
    if( m_selectionManager == null )
      m_selectionManager = new FeatureSelectionManager2();

    return m_selectionManager;
  }

  /**
   * This function returns the coordinate system set in the preferences.
   * 
   * @return The coordinate system.
   * @deprecated Use {@link KalypsoDeegreePlugin#getDefault()#getCoordinateSystem()} instead.
   */
  @Deprecated
  public String getCoordinatesSystem( )
  {
    return KalypsoDeegreePlugin.getDefault().getCoordinateSystem();
  }

  /**
   * Copied from {@link org.eclipse.ui.plugin.AbstractUIPlugin}.
   * <p>
   * Returns the preference store for this UI plug-in. This preference store is used to hold persistent settings for
   * this plug-in in the context of a workbench. Some of these settings will be user controlled, whereas others may be
   * internal setting that are never exposed to the user.
   * <p>
   * If an error occurs reading the preference store, an empty preference store is quietly created, initialized with
   * defaults, and returned.
   * </p>
   * <p>
   * <strong>NOTE:</strong> As of Eclipse 3.1 this method is no longer referring to the core runtime compatibility
   * layer and so plug-ins relying on Plugin#initializeDefaultPreferences will have to access the compatibility layer
   * themselves.
   * </p>
   * 
   * @return the preference store
   */
  public IPreferenceStore getPreferenceStore( )
  {
    // Create the preference store lazily.
    if( m_preferenceStore == null )
    {
      m_preferenceStore = new ScopedPreferenceStore( new InstanceScope(), getBundle().getSymbolicName() );
    }
    return m_preferenceStore;
  }
}