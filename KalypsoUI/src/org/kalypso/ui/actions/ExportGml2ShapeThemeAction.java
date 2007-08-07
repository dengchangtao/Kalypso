package org.kalypso.ui.actions;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.kalypso.commons.java.io.FileUtilities;
import org.kalypso.contribs.eclipse.core.runtime.PluginUtilities;
import org.kalypso.ogc.gml.IKalypsoFeatureTheme;
import org.kalypso.ogc.gml.serialize.Gml2ShapeConverter;
import org.kalypso.ui.KalypsoGisPlugin;
import org.kalypsodeegree.model.feature.FeatureList;

public class ExportGml2ShapeThemeAction implements IObjectActionDelegate, IActionDelegate2
{
  private IAction m_action;

  private ISelection m_selection;

  private static final String SETTINGS_LAST_DIR = "lastDir";

  /**
   * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
   *      org.eclipse.ui.IWorkbenchPart)
   */
  public void setActivePart( final IAction action, final IWorkbenchPart targetPart )
  {
    m_action = action;

    updateAction();
  }

  /**
   * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
   */
  public void run( final IAction action )
  {
    throw new UnsupportedOperationException( "Use runWithEvent" );
  }

  /**
   * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
   *      org.eclipse.jface.viewers.ISelection)
   */
  public void selectionChanged( final IAction action, final ISelection selection )
  {
    m_action = action;
    m_selection = selection;

    updateAction();
  }

  /**
   * @see org.eclipse.ui.IActionDelegate2#dispose()
   */
  public void dispose( )
  {
    // TODO Auto-generated method stub

  }

  /**
   * @see org.eclipse.ui.IActionDelegate2#init(org.eclipse.jface.action.IAction)
   */
  public void init( final IAction action )
  {
    m_action = action;

    updateAction();
  }

  /**
   * @see org.eclipse.ui.IActionDelegate2#runWithEvent(org.eclipse.jface.action.IAction, org.eclipse.swt.widgets.Event)
   */
  public void runWithEvent( final IAction action, final Event event )
  {
    final Shell shell = event.display.getActiveShell();

    final IStructuredSelection sel = (IStructuredSelection) m_selection;
    final IKalypsoFeatureTheme theme = (IKalypsoFeatureTheme) sel.getFirstElement();
    final FeatureList featureList = theme == null ? null : theme.getFeatureList();
    if( featureList == null )
    {
      MessageDialog.openWarning( shell, action.getText(), "Kein Thema gew�hlt oder Thema enth�lt keine Daten." );
      return;
    }

    final Gml2ShapeConverter converter = Gml2ShapeConverter.createDefault( theme.getFeatureType() );

    // examine what we got and ask user
    // TODO: only use file extension which make sense (dbf OR shp)

    /* ask user for file */
    final IDialogSettings dialogSettings = PluginUtilities.getDialogSettings( KalypsoGisPlugin.getDefault(), "gml2shapeExport" );
    final String lastDirPath = dialogSettings.get( SETTINGS_LAST_DIR );
    final FileDialog fileDialog = new FileDialog( shell, SWT.SAVE );
    fileDialog.setFilterExtensions( new String[] { "*.*", "*.shp", "*.dbf" } );
    fileDialog.setFilterNames( new String[] { "Alle Dateien (*.*)", "ESRI Shape (*.shp)", "DBase (*.dbf)" } );
    fileDialog.setText( action.getText() );
    if( lastDirPath != null )
    {
      fileDialog.setFilterPath( lastDirPath );
      fileDialog.setFileName( theme.getName() );
    }
    else
      fileDialog.setFileName( theme.getName() );
    final String result = fileDialog.open();
    if( result == null )
      return;

    dialogSettings.put( SETTINGS_LAST_DIR, new File( result ).getParent() );

    final String shapeFileBase;
    if( result.toLowerCase().endsWith( ".shp" ) || result.toLowerCase().endsWith( ".dbf" ) )
      shapeFileBase = FileUtilities.nameWithoutExtension( result );
    else
      shapeFileBase = result;

    final Job job = new Job( action.getText() + " - " + result )
    {
      @Override
      protected IStatus run( final IProgressMonitor monitor )
      {
        try
        {
          converter.writeShape( featureList, shapeFileBase, monitor );
        }
        catch( final CoreException e )
        {
          return e.getStatus();
        }
        return Status.OK_STATUS;
      }
    };
    job.setUser( true );
    job.schedule();
  }

  private void updateAction( )
  {
    // here we could disable the action if no data is available, listening to the theme would be necessary then
    if( m_action != null )
      m_action.setEnabled( true );
  }

}
