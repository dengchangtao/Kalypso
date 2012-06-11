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
package org.kalypso.ui.rrm.internal.scenarios;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.Wizard;
import org.kalypso.contribs.eclipse.jface.operation.RunnableContextHelper;
import org.kalypso.core.status.StatusDialog;
import org.kalypso.ui.rrm.internal.KalypsoUIRRMPlugin;

import de.renew.workflow.connector.cases.IScenario;

/**
 * The merge scenario wizard.
 * 
 * @author Holger Albert
 */
public class MergeScenariosWizard extends Wizard
{
  /**
   * The scenario, where the others scenarios should be merged into.
   */
  private final IScenario m_scenario;

  /**
   * The scenarios data object.
   */
  private final MergeScenariosData m_scenariosData;

  /**
   * The merge scenarios wizard page.
   */
  private MergeScenariosWizardPage m_mergeScenariosWizardPage;

  /**
   * The constructor.
   * 
   * @param scenario
   *          The scenario, where the others scenarios should be merged into.
   */
  public MergeScenariosWizard( final IScenario scenario )
  {
    m_scenario = scenario;
    m_scenariosData = new MergeScenariosData();
    m_mergeScenariosWizardPage = null;

    setWindowTitle( "Szenarien zusammenf�hren" );
  }

  /**
   * @see org.eclipse.jface.wizard.Wizard#addPages()
   */
  @Override
  public void addPages( )
  {
    m_mergeScenariosWizardPage = new MergeScenariosWizardPage( "MergeScenariosWizardPage", m_scenario, m_scenariosData );
    addPage( m_mergeScenariosWizardPage );
  }

  /**
   * @see org.eclipse.jface.wizard.Wizard#performFinish()
   */
  @Override
  public boolean performFinish( )
  {
    /* Create the operation. */
    final MergeScenariosOperation operation = new MergeScenariosOperation( m_scenario, m_scenariosData );

    /* Execute the operation. */
    final IStatus status = RunnableContextHelper.execute( getContainer(), false, true, operation );
    if( !status.isOK() )
    {
      /* Log the error message. */
      KalypsoUIRRMPlugin.getDefault().getLog().log( status );

      /* Show a status dialog. */
      final StatusDialog statusDialog = new StatusDialog( getShell(), status, getWindowTitle() );
      statusDialog.open();

      return false;
    }

    return true;
  }
}