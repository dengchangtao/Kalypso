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
package org.kalypso.ogc.gml.featureview.modfier;

import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.property.IValuePropertyType;
import org.kalypso.gmlschema.property.PropertyUtils;
import org.kalypso.ogc.gml.featureview.IFeatureModifier;
import org.kalypso.ogc.gml.om.table.ComboBoxViewerCellEditor;
import org.kalypsodeegree.model.feature.Feature;

/**
 * A modifier which handles feature-value-properties which are enumerations: shows a combo-box as cell-editor.
 * 
 * @author Gernot Belger
 */
public class ComboModifier implements IFeatureModifier
{
  private static final String NO_LINK_STRING = "<kein Wert>";

  private final Map<Object, String> m_comboEntries;

  private final IValuePropertyType m_vpt;

  private ComboBoxViewerCellEditor m_comboBoxCellEditor;

  private Feature m_feature;

  public ComboModifier( final IValuePropertyType ftp )
  {
    m_vpt = ftp;

    m_comboEntries = PropertyUtils.createComboEntries( m_vpt );
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.IFeatureModifier#getValue(org.kalypsodeegree.model.feature.Feature)
   */
  public Object getValue( final Feature f )
  {
    return f.getProperty( m_vpt );
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.IFeatureModifier#parseInput(org.kalypsodeegree.model.feature.Feature,
   *      java.lang.Object)
   */
  public Object parseInput( final Feature f, final Object value )
  {
    return value;
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.IFeatureModifier#createCellEditor(org.eclipse.swt.widgets.Composite)
   */
  public CellEditor createCellEditor( final Composite parent )
  {
    final Map<Object, String> comboEntries = m_comboEntries;

    final ArrayContentProvider cp = new ArrayContentProvider();
    final LabelProvider lp = new LabelProvider()
    {
      /**
       * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
       */
      @Override
      public String getText( final Object element )
      {
        return comboEntries.get( element );
      }
    };

    final Object[] input = m_comboEntries.keySet().toArray( new Object[m_comboEntries.keySet().size()] );

    m_comboBoxCellEditor = new ComboBoxViewerCellEditor( cp, lp, input, parent, SWT.READ_ONLY | SWT.DROP_DOWN );

    return m_comboBoxCellEditor;
  }

  protected Feature getFeature( )
  {
    return m_feature;
  }

  /**
   * @see org.eclipse.jface.viewers.ICellEditorValidator#isValid(java.lang.Object)
   */
  public String isValid( final Object value )
  {
    return null; // null means vaild
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.IFeatureModifier#getFeatureTypeProperty()
   */
  public IPropertyType getFeatureTypeProperty( )
  {
    return m_vpt;
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.IFeatureModifier#getLabel(org.kalypsodeegree.model.feature.Feature)
   */
  public String getLabel( final Feature f )
  {
    // TODO: GUITypeHandler konsequent einsetzen
    // besser: abh�ngig vom IPropertyType etwas machen
    final IPropertyType ftp = getFeatureTypeProperty();
    final Object fprop = f.getProperty( ftp );

    if( fprop == null )
      return NO_LINK_STRING;

    return m_comboEntries.get( fprop );
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.IFeatureModifier#getImage(org.kalypsodeegree.model.feature.Feature)
   */
  public Image getImage( final Feature f )
  {
    // Todo: button image
    return null;
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.IFeatureModifier#dispose()
   */
  public void dispose( )
  {
    // nichts zu tun
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.IFeatureModifier#equals(java.lang.Object, java.lang.Object)
   */
  public boolean equals( final Object newData, final Object oldData )
  {
    return newData.equals( oldData );
  }
}