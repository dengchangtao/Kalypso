package org.kalypso.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.kalypso.util.command.ICommand;
import org.kalypso.util.command.ICommandTarget;
import org.kalypso.util.command.JobExclusiveCommandTarget;

/**
 * @author bce
 */
public abstract class AbstractEditorPart extends EditorPart implements IResourceChangeListener,
    ICommandTarget
{
  private final Runnable m_dirtyRunnable = new Runnable()
  {
    public void run()
    {
      getEditorSite().getShell().getDisplay().asyncExec( new Runnable()
      {
        public void run()
        {
          fireDirty();
        }
      } );
    }
  };

  private boolean m_isSaving = false;

  private JobExclusiveCommandTarget m_commandTarget = new JobExclusiveCommandTarget(
      m_dirtyRunnable );

  public AbstractEditorPart()
  {
    ResourcesPlugin.getWorkspace().addResourceChangeListener( this );
  }

  public void dispose()
  {
    ResourcesPlugin.getWorkspace().removeResourceChangeListener( this );

    m_commandTarget.dispose();

    super.dispose();
  }

  /**
   * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
   */
  public Object getAdapter( Class adapter )
  {
    //System.out.println( adapter.getName() );
    return null;
  }

  /**
   * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
   */
  public final void doSave( final IProgressMonitor monitor )
  {
    final IFileEditorInput input = (IFileEditorInput)getEditorInput();

    if( input != null )
    {
      try
      {
        m_isSaving = true;
        doSaveInternal( monitor, input );
        m_commandTarget.setDirty( false );
      }
      finally
      {
        m_isSaving = false;
      }
    }
  }

  protected abstract void doSaveInternal( final IProgressMonitor monitor,
      final IFileEditorInput input );

  /**
   * Returns the status line manager of this editor.
   * 
   * @return the status line manager of this editor
   */
  private IStatusLineManager getStatusLineManager()
  {
    final IEditorActionBarContributor contributor = getEditorSite().getActionBarContributor();
    if( !( contributor instanceof EditorActionBarContributor ) )
      return null;

    final IActionBars actionBars = ( (EditorActionBarContributor)contributor ).getActionBars();
    if( actionBars == null )
      return null;

    return actionBars.getStatusLineManager();
  }

  /**
   * @see org.eclipse.ui.part.EditorPart#isDirty()
   */
  public boolean isDirty()
  {
    return m_commandTarget.isDirty();
  }

  /**
   * Returns the progress monitor related to this editor.
   * 
   * @return the progress monitor related to this editor
   */
  protected IProgressMonitor getProgressMonitor()
  {
    IProgressMonitor pm = null;

    IStatusLineManager manager = getStatusLineManager();
    if( manager != null )
      pm = manager.getProgressMonitor();

    return pm != null ? pm : new NullProgressMonitor();
  }

  /**
   * @see org.eclipse.ui.part.EditorPart#doSaveAs()
   */
  public void doSaveAs()
  {
    final Shell shell = getSite().getShell();
    final IEditorInput input = getEditorInput();

    final SaveAsDialog dialog = new SaveAsDialog( shell );

    final IFile original = ( input instanceof IFileEditorInput ) ? ( (IFileEditorInput)input )
        .getFile() : null;
    if( original != null )
      dialog.setOriginalFile( original );

    dialog.create();

    final IProgressMonitor progressMonitor = getProgressMonitor();

    if( dialog.open() == Window.CANCEL )
    {
      if( progressMonitor != null )
        progressMonitor.setCanceled( true );
      return;
    }

    final IPath filePath = dialog.getResult();
    if( filePath == null )
    {
      if( progressMonitor != null )
        progressMonitor.setCanceled( true );
      return;
    }

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IFile file = workspace.getRoot().getFile( filePath );
    final IFileEditorInput newInput = new FileEditorInput( file );

    doSaveInternal( progressMonitor, newInput );
    m_commandTarget.setDirty( false );

    if( progressMonitor != null )
      progressMonitor.setCanceled( false );

    setInput( newInput );
  }

  /**
   * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite,
   *      org.eclipse.ui.IEditorInput)
   */
  public void init( IEditorSite site, IEditorInput input ) throws PartInitException
  {
    if( !( input instanceof IFileEditorInput ) )
      throw new PartInitException( "Can only use IFileEditorInput" );

    setSite( site );

    setInput( input );
  }

  /**
   * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
   */
  public boolean isSaveAsAllowed()
  {
    return true;
  }

  /**
   * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
   */
  protected final void setInput( IEditorInput input )
  {
    super.setInput( input );

    load();
  }

  protected final void load()
  {
    // TODO: mit ProgressMonitor!!! und errorhandling -> Job??
    
    loadInternal();
    
    m_commandTarget.setDirty( false );
  }

  protected abstract void loadInternal();

  /**
   * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
   */
  public void resourceChanged( final IResourceChangeEvent event )
  {
    final IFileEditorInput input = (IFileEditorInput)getEditorInput();

    if( event.getType() == IResourceChangeEvent.POST_CHANGE && input != null )
    {
      final IResourceDelta delta = event.getDelta().findMember( input.getFile().getFullPath() );
      if( delta != null && delta.getKind() == IResourceDelta.CHANGED )
      {
        // TODO: ask user?
        if( !m_isSaving && MessageDialog.openQuestion( getSite().getShell(), "GisTableEditor",
            "Die Vorlagendatei hat sich ge�ndert. Neu laden?" ) )
          load();
      }
    }
  }

  /**
   * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
   */
  public void createPartControl( final Composite parent )
  {
    final IActionBars actionBars = getEditorSite().getActionBars();
    actionBars.setGlobalActionHandler( ActionFactory.UNDO.getId(), m_commandTarget.undoAction );
    actionBars.setGlobalActionHandler( ActionFactory.REDO.getId(), m_commandTarget.redoAction );

    actionBars.updateActionBars();
  }

  /**
   * @see org.eclipse.ui.IWorkbenchPart#setFocus()
   */
  public void setFocus()
  {
  // nix
  }

  public void fireDirty()
  {
    firePropertyChange( PROP_DIRTY );
  }

  /**
   * @see org.kalypso.util.command.ICommandTarget#postCommand(org.kalypso.util.command.ICommand,
   *      java.lang.Runnable)
   */
  public void postCommand( final ICommand command, final Runnable runnable )
  {
    m_commandTarget.postCommand( command, runnable );
  }

  public JobExclusiveCommandTarget getCommandTarget()
  {
    return m_commandTarget;
  }
}