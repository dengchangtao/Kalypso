package org.kalypso.ui.calcwizard.modelpages;

import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.deegree.model.feature.event.ModellEvent;
import org.deegree.model.feature.event.ModellEventListener;
import org.deegree_impl.model.feature.visitors.GetSelectionVisitor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.jfree.chart.ChartPanel;
import org.kalypso.ogc.gml.GisTemplateHelper;
import org.kalypso.ogc.gml.IKalypsoTheme;
import org.kalypso.ogc.gml.KalypsoFeatureTheme;
import org.kalypso.ogc.gml.table.LayerTableViewer;
import org.kalypso.ogc.gml.widgets.SingleElementSelectWidget;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.diagview.IDiagramTemplate;
import org.kalypso.ogc.sensor.diagview.jfreechart.ObservationChart;
import org.kalypso.ogc.sensor.template.ObservationTemplateHelper;
import org.kalypso.ogc.sensor.timeseries.TimeserieFeatureProps;
import org.kalypso.template.gistableview.Gistableview;
import org.kalypso.ui.KalypsoGisPlugin;
import org.kalypso.ui.nature.ModelNature;

/**
 * @author Belger
 */
public class MapAndTableWizardPage extends AbstractCalcWizardPage implements ModellEventListener
{
  /** Der Titel der Seite */
  public static final String PROP_MAPTITLE = "mapTitle";

  /** Pfad auf Vorlage f�r die Gis-Tabell (.gtt Datei) */
  public final static String PROP_TABLETEMPLATE = "tableTemplate";

  /** Pfad auf die Vorlage f�r das Diagramm (.odt Datei) */
  public final static String PROP_DIAGTEMPLATE = "diagTemplate";

  /** Position des Haupt-Sash: Integer von 0 bis 100 */
  public final static String PROP_MAINSASH = "mainSash";

  /** Position des rechten Sash: Integer von 0 bis 100 */
  public final static String PROP_RIGHTSASH = "rightSash";

  /**
   * Basisname der Zeitreihen-Properties. Es kann mehrere Zeitreihen
   * geben-Property geben: eine f?r jede Kurventyp.
   */
  public final static String PROP_TIMEPROPNAME = "timeserie";

  private LayerTableViewer m_viewer;

  private Frame m_diagFrame = null;

  private IDiagramTemplate m_diagTemplate = null;

  private ObservationChart m_obsChart = null;

  public MapAndTableWizardPage()
  {
    super( "<MapAndTableWizardPage>" );
  }

  /**
   * @see org.eclipse.jface.dialogs.IDialogPage#dispose()
   */
  public void dispose()
  {
    super.dispose();
  }

  /**
   * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
   */
  public void createControl( final Composite parent )
  {
    try
    {
      final SashForm sashForm = new SashForm( parent, SWT.HORIZONTAL );
      createMapPanel( sashForm );
      createRightPanel( sashForm );

      setControl( sashForm );

      parent.getDisplay().asyncExec( new Runnable()
      {
        public void run()
        {
          maximizeMap();
        }
      } );
    }
    catch( final Exception e )
    {
      e.printStackTrace();
    }
  }

  private void createRightPanel( final SashForm sashForm ) throws NumberFormatException
  {
    final Composite rightPanel = new Composite( sashForm, SWT.NONE );

    final GridLayout gridLayout = new GridLayout();
    rightPanel.setLayout( gridLayout );
    rightPanel.setLayoutData( new GridData( GridData.FILL_BOTH ) );

    final SashForm rightSash = new SashForm( rightPanel, SWT.VERTICAL );
    rightSash.setLayoutData( new GridData( GridData.FILL_BOTH ) );
    createTablePanel( rightSash );
    createDiagramPanel( rightSash );

    final Button button = new Button( rightPanel, SWT.NONE | SWT.PUSH );
    button.setText( "Berechnung durchf�hren" );

    final int mainWeight = Integer.parseInt( getArguments().getProperty( PROP_MAINSASH, "50" ) );
    final int rightWeight = Integer.parseInt( getArguments().getProperty( PROP_RIGHTSASH, "50" ) );

    sashForm.setWeights( new int[]
    {
        mainWeight,
        100 - mainWeight } );

    rightSash.setWeights( new int[]
    {
        rightWeight,
        100 - rightWeight } );

    // die Karte soll immer maximiert sein
    rightSash.addControlListener( new ControlAdapter()
    {
      public void controlResized( ControlEvent e )
      {
        maximizeMap();
      }
    } );

    button.addSelectionListener( new SelectionAdapter()
    {
      public void widgetSelected( SelectionEvent e )
      {
        runCalculation();
      }
    } );
  }

  private void createDiagramPanel( final Composite parent )
  {
    final String diagFileName = getArguments().getProperty( PROP_DIAGTEMPLATE );
    final IFile diagFile = (IFile)getProject().findMember( diagFileName );

    try
    {
      // actually creates the template
      m_diagTemplate = ObservationTemplateHelper.loadDiagramTemplate( diagFile );

      final Composite composite = new Composite( parent, SWT.RIGHT | SWT.EMBEDDED | SWT.BORDER );
      m_diagFrame = SWT_AWT.new_Frame( composite );
      m_diagFrame.setVisible( true );

      m_obsChart = new ObservationChart( m_diagTemplate );
      m_diagTemplate.addTemplateEventListener( m_obsChart );

      final ChartPanel chartPanel = new ChartPanel( m_obsChart );
      chartPanel.setMouseZoomable( true, false );

      m_diagFrame.setVisible( true );
      chartPanel.setVisible( true );
      m_diagFrame.add( chartPanel );
    }
    catch( Exception e )
    {
      e.printStackTrace();

      final Text text = new Text( parent, SWT.CENTER );
      text.setText( "Kein Diagram vorhanden" );
    }
  }

  private void createTablePanel( final Composite parent )
  {
    try
    {
      final String templateFileName = getArguments().getProperty( PROP_TABLETEMPLATE );
      final IFile templateFile = (IFile)getProject().findMember( templateFileName );
      final Gistableview template = GisTemplateHelper.loadGisTableview( templateFile,
          getReplaceProperties() );

      m_viewer = new LayerTableViewer( parent, this, getProject(), KalypsoGisPlugin.getDefault()
          .createFeatureTypeCellEditorFactory(), SELECTION_ID, false );
      m_viewer.applyTableTemplate( template, getContext() );
    }
    catch( final Exception e )
    {
      e.printStackTrace();
      final Text text = new Text( parent, SWT.NONE );
      text.setText( "Fehler beim Laden des TableTemplate" );
    }
  }

  private void createMapPanel( final Composite parent ) throws Exception, CoreException
  {
    final Composite mapPanel = new Composite( parent, SWT.NONE );
    mapPanel.setLayout( new GridLayout() );

    final Control mapControl = initMap( mapPanel, new SingleElementSelectWidget() );
    mapControl.setLayoutData( new GridData( GridData.FILL_BOTH ) );
  }

  /**
   * @see org.kalypso.ui.calcwizard.modelpages.IModelWizardPage#performFinish()
   */
  public boolean performFinish()
  {
    // TODO: error handling?
    m_viewer.saveData();

    return true;
  }

  /**
   * @see org.deegree.model.feature.event.ModellEventListener#onModellChange(org.deegree.model.feature.event.ModellEvent)
   */
  public void onModellChange( final ModellEvent modellEvent )
  {
    if( m_diagFrame == null || !isCurrentPage() )
      return;

    final IKalypsoTheme theme = getMapModell().getActiveTheme();
    if( !( theme instanceof KalypsoFeatureTheme ) )
      return;

    final KalypsoFeatureTheme kft = (KalypsoFeatureTheme)theme;
    final List selectedFeatures = GetSelectionVisitor.getSelectedFeatures( kft.getWorkspace(), kft.getFeatureType(), SELECTION_ID );

    m_diagTemplate.removeAllCurves();

    if( selectedFeatures.size() > 0 )
    {
      final TimeserieFeatureProps[] tsProps = KalypsoWizardHelper
          .parseTimeserieFeatureProps( getArguments() );

      try
      {
        KalypsoWizardHelper.updateDiagramTemplate( tsProps, selectedFeatures, m_diagTemplate, getContext() );
      }
      catch( final SensorException e )
      {
        e.printStackTrace();
        getShell().getDisplay().asyncExec( new Runnable()
            {
              public void run( )
              {
                MessageDialog.openError( getShell(), "Aktualisierungsfehler", e.getLocalizedMessage() );
              }
            });
      }
    }
  }

  protected void runCalculation()
  {
    m_viewer.saveData();

    final WorkspaceModifyOperation op = new WorkspaceModifyOperation( null )
    {
      public void execute( final IProgressMonitor monitor ) throws CoreException
      {
        final ModelNature nature = (ModelNature)getCalcFolder().getProject().getNature( ModelNature.ID );
        nature.runCalculation( getCalcFolder(), monitor );
      }
    };

    try
    {
      getContainer().run( true, true, op );
    }
    catch( final InterruptedException e )
    {
      // canceled
      // TODO error message?
      return;
    }
    catch( final InvocationTargetException e )
    {
      e.printStackTrace();

      final Throwable te = e.getTargetException();
      if( te instanceof CoreException )
      {
        ErrorDialog.openError( getContainer().getShell(), "Fehler",
            "Fehler beim Aufruf der n�chsten Wizard-Seite",
            ( (CoreException)e.getTargetException() ).getStatus() );
      }
      else
      {
        // CoreExceptions are handled above, but unexpected runtime exceptions
        // and errors may still occur.
        MessageDialog.openError( getContainer().getShell(), "Interner Fehler",
            "Fehler beim Aufruf der n�chsten Wizard-Seite: " + te.getLocalizedMessage() );
      }
    }
    
    onModellChange( new ModellEvent( null, ModellEvent.SELECTION_CHANGED ) );
    
  }
}