package org.kalypso.project.database.client.core.model.local;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.kalypso.project.database.common.model.local.ILocalProject;
import org.kalypso.project.database.common.model.local.ILocalWorkspaceListener;

/**
 * @author Dirk Kuch
 */
public class LocalWorkspaceModel
{

  // TODO add IPreferenceChangeListener for nature preferences

  private final IResourceChangeListener RESOURCE_LISTENER = new IResourceChangeListener()
  {
    @Override
    public void resourceChanged( final IResourceChangeEvent event )
    {
      // TODO add newly created projects
      // TODO remove removed projects

      if( IResourceChangeEvent.POST_CHANGE == event.getType() )
      {
        update();
        fireLocalUpdateEvent();
      }
    }
  };

  /**
   * if m_natures == null -> handle (return) all local projects
   */
  private Set<ILocalProject> m_projects = null;

  protected final Set<ILocalWorkspaceListener> m_listener = new LinkedHashSet<ILocalWorkspaceListener>();

  public LocalWorkspaceModel( )
  {
    update();
  }

  protected void fireLocalUpdateEvent( )
  {
    for( final ILocalWorkspaceListener listener : m_listener )
    {
      listener.localWorkspaceChanged();
    }
  }

  public void dispose( )
  {
    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    workspace.removeResourceChangeListener( RESOURCE_LISTENER );

    m_projects.clear();
    m_listener.clear();
  }

  protected void update( )
  {
    m_projects = new HashSet<ILocalProject>();

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    workspace.addResourceChangeListener( RESOURCE_LISTENER );

    final IWorkspaceRoot root = workspace.getRoot();

    final IProject[] projects = root.getProjects();
    for( final IProject project : projects )
    {
      if( !project.isAccessible() || !project.isOpen() )
        continue;

      m_projects.add( new LocalProjectHandler( project, this ) );
    }
  }

  public void addListener( final ILocalWorkspaceListener listener )
  {
    m_listener.add( listener );
  }

  public void removeListener( final ILocalWorkspaceListener listener )
  {
    m_listener.remove( listener );
  }

  public ILocalProject[] getProjects( )
  {
    return m_projects.toArray( new ILocalProject[] {} );
  }
}
