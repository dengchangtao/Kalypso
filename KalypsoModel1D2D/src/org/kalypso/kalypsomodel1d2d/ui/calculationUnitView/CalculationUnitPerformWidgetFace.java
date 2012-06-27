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
package org.kalypso.kalypsomodel1d2d.ui.calculationUnitView;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.kalypso.kalypsomodel1d2d.ui.i18n.Messages;
import org.kalypso.kalypsomodel1d2d.ui.map.calculation_unit.CalculationUnitDataModel;
import org.kalypso.kalypsomodel1d2d.ui.map.calculation_unit.SelectedCalculationComponent;

/**
 * @author Madanagopal
 */
public class CalculationUnitPerformWidgetFace
{
  private final CalculationUnitDataModel m_dataModel;

  public CalculationUnitPerformWidgetFace( final CalculationUnitDataModel dataModel )
  {
    m_dataModel = dataModel;
  }

  public Composite createControl( final Composite parent, final FormToolkit toolkit )
  {
    final Form form = toolkit.createForm( parent );

    final Composite body = form.getBody();
    body.setLayout( new TableWrapLayout() );

    // Calculation Unit Section
    final Section selectCalcUnitSection = toolkit.createSection( body, Section.EXPANDED | Section.TITLE_BAR );
    selectCalcUnitSection.setText( Messages.getString( "org.kalypso.kalypsomodel1d2d.ui.calculationUnitView.CalculationUnitPerformWidgetFace.1" ) ); //$NON-NLS-1$
    final TableWrapData selectCalcUnitData = new TableWrapData( TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB, 1, 1 );
    selectCalcUnitData.maxHeight = 250;
    selectCalcUnitSection.setLayoutData( selectCalcUnitData );

    // Creates Section for "Calculation Elements Unit"
    final ScrolledForm scrolledForm = toolkit.createScrolledForm( body );
    final TableWrapData scrolledFormData = new TableWrapData( TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB, 1, 1 );
    // scrolledFormData.maxHeight = 250;
    scrolledForm.setLayoutData( scrolledFormData );
    scrolledForm.setExpandVertical( true );
    final Composite scrolledBody = scrolledForm.getBody();
    scrolledBody.setLayout( new FillLayout() );

    final Section calculationElementUnitSection = toolkit.createSection( scrolledBody, Section.EXPANDED | Section.TITLE_BAR );
    calculationElementUnitSection.setText( Messages.getString( "org.kalypso.kalypsomodel1d2d.ui.calculationUnitView.CalculationUnitPerformWidgetFace.2" ) ); //$NON-NLS-1$

    // Creates Section for "Calculation Settings Unit"
    final Section logSection = toolkit.createSection( body, Section.EXPANDED | Section.TITLE_BAR );
    logSection.setText( Messages.getString( "org.kalypso.kalypsomodel1d2d.ui.calculationUnitView.CalculationUnitPerformWidgetFace.5" ) ); //$NON-NLS-1$
    final TableWrapData logData = new TableWrapData( TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB, 1, 1 );
    logData.heightHint = 250;
    logSection.setLayoutData( logData );

    createCalculationUnitSection( selectCalcUnitSection, toolkit );
    createCalculationElementsSection( calculationElementUnitSection, toolkit );
    createProblemsInCalculationSection( logSection, toolkit );

    return parent;
  }

  private void createCalculationUnitSection( final Section selectCalcUnitSection, final FormToolkit toolkit )
  {
    final CalculationUnitMetaTable calcSelect = new CalculationUnitMetaTable( m_dataModel, CalculationUnitMetaTable.BTN_SHOW_AND_MAXIMIZE, CalculationUnitMetaTable.BTN_CLICK_TO_CALCULATE );
    final Control client = calcSelect.createControl( selectCalcUnitSection, toolkit );
    selectCalcUnitSection.setClient( client );
  }

  private void createCalculationElementsSection( final Section calculationElementUnitSection, final FormToolkit toolkit )
  {
    final SelectedCalculationComponent calcElementGUI = new SelectedCalculationComponent( m_dataModel );
    final Control client = calcElementGUI.createControl( calculationElementUnitSection, toolkit );
    calculationElementUnitSection.setClient( client );
  }

  private void createProblemsInCalculationSection( final Section problemsSection, final FormToolkit toolkit )
  {
    final CalculationUnitLogComponent calcProblemsGUI = new CalculationUnitLogComponent( m_dataModel );
    final Control client = calcProblemsGUI.createControl( toolkit, problemsSection );
    problemsSection.setClient( client );
  }
}