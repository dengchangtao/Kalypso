package org.kalypso.ui.application;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.ide.IDEWorkbenchAdvisor;
import org.eclipse.ui.internal.registry.ActionSetRegistry;
import org.eclipse.ui.internal.registry.IActionSetDescriptor;
import org.kalypso.ui.IKalypsoUIConstants;

/**
 * @author belger
 */
public class KalypsoWorkbenchAdvisor extends IDEWorkbenchAdvisor
{
  public KalypsoWorkbenchAdvisor()
  {
    super();
  }
  
  /**
   * @see org.eclipse.ui.application.WorkbenchAdvisor#initialize(org.eclipse.ui.application.IWorkbenchConfigurer)
   */
  public void initialize( final IWorkbenchConfigurer configurer )
  {
    super.initialize( configurer );
    
    final ActionSetRegistry reg = WorkbenchPlugin.getDefault().getActionSetRegistry();
    final IActionSetDescriptor[] array = reg.getActionSets();
    final int count = array.length;
    for( int nX = 0; nX < count; nX++ )
    {
      final IActionSetDescriptor desc = array[nX];
      
      desc.setInitiallyVisible( desc.getId().startsWith( "org.kalypso" ) );
    }
  }
  
  /**
   * @see org.eclipse.ui.application.WorkbenchAdvisor#getInitialWindowPerspectiveId()
   */
  public String getInitialWindowPerspectiveId()
  {
    return IKalypsoUIConstants.MODELER_PERSPECTIVE;
  }
  
  /**
   * @see org.eclipse.ui.internal.ide.IDEWorkbenchAdvisor#fillActionBars(org.eclipse.ui.IWorkbenchWindow, org.eclipse.ui.application.IActionBarConfigurer, int)
   */
  public void fillActionBars( final IWorkbenchWindow window, final IActionBarConfigurer actionConfigurer,
      final int flags )
  {
    super.fillActionBars( window, actionConfigurer, flags );

    final IMenuManager menuManager = actionConfigurer.getMenuManager();
    
    // Men�s umbauen
    final IMenuManager windowMenu = (IMenuManager)menuManager.find( IWorkbenchActionConstants.M_WINDOW );
    final IMenuManager showViewMenu = (IMenuManager)windowMenu.find( "showView" );
    menuManager.insertAfter( IWorkbenchActionConstants.M_EDIT, showViewMenu );

    // Men�s entfernen
    menuManager.remove( IWorkbenchActionConstants.M_PROJECT );
    menuManager.remove( IWorkbenchActionConstants.M_NAVIGATE );
    menuManager.remove( IWorkbenchActionConstants.M_WINDOW );
    
    final IMenuManager fileMenu = (IMenuManager)menuManager.find( IWorkbenchActionConstants.M_FILE );
    fileMenu.remove( "move" );
    fileMenu.remove( "openWorkspace" );
    fileMenu.remove( "import" );
    fileMenu.remove( "export" );

    final IMenuManager editMenu = (IMenuManager)menuManager.find( IWorkbenchActionConstants.M_EDIT );
    editMenu.remove( "bookmark" );
    editMenu.remove( "addTask" );

    final IMenuManager helpMenu = menuManager.findMenuUsingPath( IWorkbenchActionConstants.M_HELP );
    helpMenu.remove( "tipsAndTricks" );
  }
  
  /**
   * @see org.eclipse.ui.internal.ide.IDEWorkbenchAdvisor#preWindowOpen(org.eclipse.ui.application.IWorkbenchWindowConfigurer)
   */
  public void preWindowOpen( final IWorkbenchWindowConfigurer windowConfigurer )
  {
    super.preWindowOpen( windowConfigurer );
    
    windowConfigurer.setShowPerspectiveBar( false );
  }
  
  /**
   * @see org.eclipse.ui.internal.ide.IDEWorkbenchAdvisor#postStartup()
   */
  public void postStartup()
  {
    super.postStartup();

    // Das 'AutoBuild' aktivieren, damit das 'BuildAll' Icon in der Toolbar verschwindet
    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IWorkspaceDescription description = workspace.getDescription();
    description.setAutoBuilding( true );
    
    try
    {
      workspace.setDescription( description );
    }
    catch( CoreException e )
    {
      e.printStackTrace();
    }
  }
}
