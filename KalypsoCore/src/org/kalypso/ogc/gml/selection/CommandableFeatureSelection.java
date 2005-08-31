/*
 * --------------- Kalypso-Header --------------------------------------------------------------------
 * 
 * This file is part of kalypso. Copyright (C) 2004, 2005 by:
 * 
 * Technical University Hamburg-Harburg (TUHH) Institute of River and coastal engineering Denickestr. 22 21073 Hamburg,
 * Germany http://www.tuhh.de/wb
 * 
 * and
 * 
 * Bjoernsen Consulting Engineers (BCE) Maria Trost 3 56070 Koblenz, Germany http://www.bjoernsen.de
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Contact:
 * 
 * E-Mail: belger@bjoernsen.de schlienger@bjoernsen.de v.doemming@tuhh.de
 * 
 * ---------------------------------------------------------------------------------------------------
 */
package org.kalypso.ogc.gml.selection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.kalypso.ogc.gml.mapmodel.CommandableWorkspace;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureTypeProperty;

/**
 * CommandableFeatureAction
 * <p>
 * decorates a structuredselection and provides additional an
 * 
 * @see org.kalypso.ogc.gml.mapmodel.CommandableWorkspace editors that provide featureselections should overwrite
 *      getSelection()
 * @see org.eclipse.jface.viewers.ISelectionProvider and deliver this type of selection. Actions on the other hand
 *      should cast for this type of selection and use the CommanableWorkspace as Commandtarget.
 * 
 * created by
 * 
 * @author doemming (24.05.2005)
 */
public class CommandableFeatureSelection implements ICommandableFeatureSelection
{
  /*
   * things that should provided: - GMLWorkspace=Commandtarget - parentfeature - featureassociationpropertytype -...
   */

  private final IStructuredSelection m_selection;

  private final FeatureTypeProperty m_ftp;

  private final Feature m_selectedRow;

  private final CommandableWorkspace m_workspace;

  private final Object m_eventSource;

  public CommandableFeatureSelection( final CommandableWorkspace workspace, final Object eventSource,
      final IStructuredSelection selection, final FeatureTypeProperty ftp, final Feature selectedRow )
  {
    m_workspace = workspace;
    m_eventSource = eventSource;
    m_selection = selection;
    m_ftp = ftp;
    m_selectedRow = selectedRow;
  }

  public Object getFirstElement()
  {
    return m_selection.getFirstElement();
  }

  public boolean isEmpty()
  {
    return m_selection.isEmpty();
  }

  public Iterator iterator()
  {
    return m_selection.iterator();
  }

  public int size()
  {
    return m_selection.size();
  }

  public Object[] toArray()
  {
    return m_selection.toArray();
  }

  /**
   * 
   * @see org.eclipse.jface.viewers.IStructuredSelection#toList()
   */
  public List toList()
  {
    return m_selection.toList();
  }

  /**
   * @see org.kalypso.ogc.gml.selection.IFeatureThemeSelection#getFocusedFeatureTypeProperty()
   */
  public FeatureTypeProperty getFocusedFeatureTypeProperty()
  {
    return m_ftp;
  }

  public Feature getFocusedFeature()
  {
    return m_selectedRow;
  }

  /**
   * @see org.kalypsodeegree_impl.model.feature.selection.ICommandableFeatureSelection#getCommandableWorkspace()
   */
  public CommandableWorkspace getCommandableWorkspace()
  {
    return m_workspace;
  }

  /**
   * @see org.kalypsodeegree.model.feature.event.IEventSourceProvider#getEventSource()
   */
  public Object getEventSource()
  {
    return m_eventSource;
  }

  public Feature[] getSelectedFeatures()
  {
    ArrayList features = new ArrayList();
    for( Iterator iter = m_selection.iterator(); iter.hasNext(); )
    {
      Object o = (Object)iter.next();
      if( o instanceof Feature )
      {
        features.add( (Feature)o );
      }
    }
    return (Feature[])features.toArray( new Feature[features.size()] );
  }
}
