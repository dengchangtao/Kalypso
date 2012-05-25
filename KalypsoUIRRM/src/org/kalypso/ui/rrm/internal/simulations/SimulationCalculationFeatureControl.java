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
package org.kalypso.ui.rrm.internal.simulations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.kalypso.afgui.scenarios.ScenarioHelper;
import org.kalypso.afgui.scenarios.SzenarioDataProvider;
import org.kalypso.contribs.eclipse.jface.action.ActionHyperlink;
import org.kalypso.core.status.StatusComposite;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.model.hydrology.binding.control.NAControl;
import org.kalypso.model.hydrology.binding.control.SimulationCollection;
import org.kalypso.model.hydrology.project.INaProjectConstants;
import org.kalypso.model.hydrology.project.RrmSimulation;
import org.kalypso.ogc.gml.featureview.control.AbstractFeatureControl;
import org.kalypso.ui.rrm.internal.KalypsoUIRRMPlugin;
import org.kalypso.ui.rrm.internal.i18n.Messages;
import org.kalypso.ui.rrm.internal.simulations.actions.OpenOutputZipAction;
import org.kalypso.ui.rrm.internal.simulations.actions.OpenTextLogAction;
import org.kalypso.ui.rrm.internal.simulations.jobs.ReadCalculationStatusJob;
import org.kalypso.ui.rrm.internal.simulations.jobs.ValidateSimulationJob;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.IFeatureBindingCollection;

/**
 * The simulation calculation feature control.
 * 
 * @author Holger Albert
 */
public class SimulationCalculationFeatureControl extends AbstractFeatureControl
{
  /**
   * The calculation status composite.
   */
  protected StatusComposite m_calculationStatusComposite;

  /**
   * The validation status composite.
   */
  protected StatusComposite m_validationStatusComposite;

  /**
   * The constructor.
   * 
   * @param ftp
   */
  public SimulationCalculationFeatureControl( final IPropertyType ftp )
  {
    super( ftp );

    m_calculationStatusComposite = null;
    m_validationStatusComposite = null;
  }

  /**
   * The constructor.
   * 
   * @param feature
   * @param ftp
   */
  public SimulationCalculationFeatureControl( final Feature feature, final IPropertyType ftp )
  {
    super( feature, ftp );
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.control.AbstractFeatureControl#createControl(org.eclipse.swt.widgets.Composite,
   *      int)
   */
  @Override
  protected Control createControl( final Composite parent, final int style )
  {
    /* Create the main composite. */
    final Composite main = new Composite( parent, style );
    main.setLayout( new GridLayout( 1, false ) );

    try
    {
      /* Create a label. */
      final Label desciptionLabel = new Label( main, SWT.WRAP );
      desciptionLabel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
      desciptionLabel.setText( "Here you can review some result files and the logs of the calculation." );

      /* Create a status composite. */
      m_calculationStatusComposite = new StatusComposite( main, SWT.NONE );
      m_calculationStatusComposite.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
      m_calculationStatusComposite.setStatus( new Status( IStatus.INFO, KalypsoUIRRMPlugin.getID(), "Please wait while updating..." ) );

      /* Create a empty label. */
      final Label emptyLabel1 = new Label( main, SWT.NONE );
      emptyLabel1.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

      /* Create a group. */
      final Group resultsGroup = new Group( main, SWT.NONE );
      resultsGroup.setLayout( new GridLayout( 1, false ) );
      resultsGroup.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
      resultsGroup.setText( "Rechenergebnisse" );

      /* Create a status composite. */
      m_validationStatusComposite = new StatusComposite( resultsGroup, SWT.NONE );
      m_validationStatusComposite.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
      m_validationStatusComposite.setStatus( new Status( IStatus.INFO, KalypsoUIRRMPlugin.getID(), "Please wait while updating..." ) );

      /* Create a empty label. */
      final Label emptyLabel2 = new Label( resultsGroup, SWT.NONE );
      emptyLabel2.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

      /* Get the current simulation. */
      final RrmSimulation simulation = getSimulation();

      /* Create the actions. */
      final List<Action> actions = new ArrayList<Action>();
      actions.add( new OpenTextLogAction( "Calculation log", "Displays the calculation log.", simulation.getCalculationLog() ) );

      // TODO This action may be added and implemented later (only the stub exists)...
      // actions.add( new OpenErrorGmlAction( "Error log", "Displays the error log.", simulation ) );

      actions.add( new OpenOutputZipAction( "Error log (calculation core)", "Displays the error log.", simulation, true ) );
      actions.add( new OpenOutputZipAction( "Output log (calculation core)", "Displays the output log.", simulation, false ) );
      actions.add( new OpenTextLogAction( "Mass Balance", "Displays the mass balance.", simulation.getBilanzTxt() ) );
      actions.add( new OpenTextLogAction( "Statistics", "Displays the statistics.", simulation.getStatisticsCsv() ) );

      /* Create the image hyperlinks. */
      for( final Action action : actions )
      {
        final ImageHyperlink imageHyperlink = ActionHyperlink.createHyperlink( null, resultsGroup, SWT.NONE, action );
        imageHyperlink.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        imageHyperlink.setText( action.getText() );
      }

      /* Create a empty label. */
      final Label emptyLabel3 = new Label( resultsGroup, SWT.NONE );
      emptyLabel3.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

      /* Create a button. */
      final Button calculationButton = new Button( resultsGroup, SWT.PUSH );
      calculationButton.setLayoutData( new GridData( SWT.END, SWT.CENTER, true, false ) );
      calculationButton.setText( "Calculate" );
      calculationButton.addSelectionListener( new SelectionAdapter()
      {
        @Override
        public void widgetSelected( final SelectionEvent e )
        {
          handleCalculatePressed();
        }
      } );

      /* Initialize. */
      initialize( simulation );
    }
    catch( final CoreException ex )
    {
      ex.printStackTrace();

      final Label errorLabel = new Label( main, SWT.WRAP );
      errorLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
      errorLabel.setText( ex.getLocalizedMessage() );
    }

    return main;
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.control.IFeatureControl#updateControl()
   */
  @Override
  public void updateControl( )
  {
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.control.IFeatureControl#isValid()
   */
  @Override
  public boolean isValid( )
  {
    return true;
  }

  private RrmSimulation getSimulation( ) throws CoreException
  {
    /* Get the description of the current simulation. */
    final Feature feature = getFeature();
    final String description = feature.getDescription();

    /* Get the folder of the current simulation. */
    final SzenarioDataProvider dataProvider = ScenarioHelper.getScenarioDataProvider();
    final IContainer scenarioFolder = dataProvider.getScenarioFolder();
    final IFolder calcCasesFolder = scenarioFolder.getFolder( new Path( INaProjectConstants.FOLDER_RECHENVARIANTEN ) );
    final IFolder simulationFolder = calcCasesFolder.getFolder( description );

    return new RrmSimulation( simulationFolder );
  }

  private void initialize( final RrmSimulation simulation )
  {
    /* Create the read calculation status job. */
    final ReadCalculationStatusJob calculationJob = new ReadCalculationStatusJob( simulation );
    calculationJob.setUser( false );
    calculationJob.addJobChangeListener( new JobChangeAdapter()
    {
      @Override
      public void done( final IJobChangeEvent event )
      {
        final Job job = event.getJob();
        if( !(job instanceof ReadCalculationStatusJob) )
          return;

        final IStatus result = job.getResult();
        if( !result.isOK() )
        {
          setCalculationStatus( result );
          return;
        }

        final ReadCalculationStatusJob task = (ReadCalculationStatusJob) job;
        final IStatus calculationStatus = task.getCalculationStatus();

        setCalculationStatus( calculationStatus );
      }
    } );

    /* Create the validate simulation job. */
    final ValidateSimulationJob validationJob = new ValidateSimulationJob( (NAControl) getFeature() );
    validationJob.setUser( false );
    validationJob.addJobChangeListener( new JobChangeAdapter()
    {
      @Override
      public void done( final IJobChangeEvent event )
      {
        final Job job = event.getJob();
        if( !(job instanceof ValidateSimulationJob) )
          return;

        final IStatus result = job.getResult();
        if( !result.isOK() )
        {
          setValidationStatus( result );
          return;
        }

        final ValidateSimulationJob task = (ValidateSimulationJob) job;
        final IStatus validationStatus = task.getValidationStatus();
        setValidationStatus( validationStatus );
      }
    } );

    /* Schedule the jobs. */
    calculationJob.schedule();
    validationJob.schedule();
  }

  private NAControl[] findAllSimulations( )
  {
    final Collection<NAControl> results = new ArrayList<NAControl>();

    final NAControl naControl = (NAControl) getFeature();
    final SimulationCollection owner = (SimulationCollection) naControl.getOwner();

    final IFeatureBindingCollection<NAControl> simulations = owner.getSimulations();
    for( final NAControl simulation : simulations )
      results.add( simulation );

    return results.toArray( new NAControl[results.size()] );
  }

  protected void handleCalculatePressed( )
  {
    if( m_calculationStatusComposite == null || m_calculationStatusComposite.isDisposed() )
      return;

    /* Get the shell and the title. */
    final Shell shell = m_calculationStatusComposite.getShell();
    final String title = "Calculate Simulations";

    /* Get the na control. */
    final NAControl naControl = (NAControl) getFeature();
    final NAControl[] simulations = new NAControl[] { naControl };

    /* Create the simulation handler. */
    final SimulationHandler handler = new SimulationHandler();

    /* Check for duplicates. */
    final NAControl[] allSimulations = findAllSimulations();
    final String duplicateName = handler.findDuplicates( allSimulations );
    if( duplicateName != null )
    {
      final String message = String.format( Messages.getString( "RefreshSimulationsTaskHandler_0" ), duplicateName ); //$NON-NLS-1$
      MessageDialog.openWarning( shell, title, message );
      return;
    }

    /* Check the sanity of the simulations. */
    final IStatus status = handler.checkSanity( simulations );
    if( !status.isOK() )
    {
      final String message = status.getMessage();
      MessageDialog.openWarning( shell, title, message );
      return;
    }

    /* Calculate the simulations. */
    handler.calculateSimulation( shell, simulations );

    /* Update the control. */
    updateControl();
  }

  protected void setCalculationStatus( final IStatus calculationStatus )
  {
    if( m_calculationStatusComposite == null || m_calculationStatusComposite.isDisposed() )
      return;

    m_calculationStatusComposite.getDisplay().asyncExec( new Runnable()
    {
      @Override
      public void run( )
      {
        m_calculationStatusComposite.setStatus( calculationStatus );
      }
    } );
  }

  protected void setValidationStatus( final IStatus validationStatus )
  {
    if( m_validationStatusComposite == null || m_validationStatusComposite.isDisposed() )
      return;

    m_validationStatusComposite.getDisplay().asyncExec( new Runnable()
    {
      @Override
      public void run( )
      {
        m_validationStatusComposite.setStatus( validationStatus );
      }
    } );
  }
}