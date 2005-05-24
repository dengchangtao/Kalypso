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
package org.kalypso.ui.action;

import java.io.File;

import org.bce.eclipse.jface.viewers.FileLabelProvider;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.kalypso.util.synchronize.ModelSynchronizer;

/**
 * @author bce
 */
public class GetModelDelegate implements IWorkbenchWindowActionDelegate
{
  private IWorkbenchWindow m_window;

  /**
   * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
   */
  public void dispose()
  {
  // nix zu tun
  }

  /**
   * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
   */
  public void init( final IWorkbenchWindow window )
  {
    m_window = window;
  }

  /**
   * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
   */
  public void run( final IAction action )
  {
    try
    {
      final File serverRoot = ModelActionHelper.getServerRoot();

      final File[] files = serverRoot.listFiles();
      final ListSelectionDialog lsd = new ListSelectionDialog(
          m_window.getShell(),
          files,
          new ArrayContentProvider(),
          new FileLabelProvider(),
          "W�hlen Sie die Modelle, die Sie vom Server laden m�chten.\nIm Arbeitsbereich bereits vorhandene Modelle werden aktualisiert." );
      lsd.setInitialSelections( files );
      lsd.setTitle( "Modelle vom Server laden" );
      if( lsd.open() != Window.OK )
        return;

      final Object[] projects = lsd.getResult();

      final Job job = new Job( "Modelle aktualisieren" )
      {
        protected IStatus run( final IProgressMonitor monitor )
        {
          monitor.beginTask( "Modelle vom Server laden", files.length * 1000 );

          for( int i = 0; i < projects.length; i++ )
          {
            if( monitor.isCanceled() )
              return Status.CANCEL_STATUS;

            final File serverProject = (File)projects[i];
            final String name = serverProject.getName();

            final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject( name );

            final ModelSynchronizer synchronizer = new ModelSynchronizer( project, serverProject );

            try
            {
              synchronizer.updateLocal( new SubProgressMonitor( monitor, 1000 ) );
            }
            catch( final CoreException e )
            {
              e.printStackTrace();

              return e.getStatus();
            }
          }

          return Status.OK_STATUS;
        }
      };
      job.setUser( true );
      job.schedule();
    }
    catch( final CoreException ce )
    {
      ErrorDialog.openError( m_window.getShell(), "Modell vom Server laden",
          "Modell konnte nicht vom Server geladen werden", ce.getStatus() );
    }
  }

  /**
   * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
   *      org.eclipse.jface.viewers.ISelection)
   */
  public void selectionChanged( IAction action, ISelection selection )
  {
  // auch wurscht
  }

}