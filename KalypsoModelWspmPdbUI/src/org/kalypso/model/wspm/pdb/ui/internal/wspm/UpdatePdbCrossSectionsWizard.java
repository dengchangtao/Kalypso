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
package org.kalypso.model.wspm.pdb.ui.internal.wspm;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.forms.IMessage;
import org.kalypso.contribs.eclipse.ui.forms.MessageUtilitites;
import org.kalypso.model.wspm.core.gml.IProfileFeature;
import org.kalypso.model.wspm.pdb.connect.IPdbConnection;
import org.kalypso.model.wspm.pdb.connect.PDBRole;
import org.kalypso.model.wspm.pdb.ui.internal.PdbUiUtils;
import org.kalypso.model.wspm.pdb.ui.internal.checkout.ConnectionChooserPage;
import org.kalypso.model.wspm.pdb.ui.internal.content.IConnectionViewer;
import org.kalypso.model.wspm.pdb.ui.internal.i18n.Messages;
import org.kalypso.model.wspm.tuhh.core.gml.TuhhReach;
import org.kalypso.model.wspm.tuhh.ui.export.ExportProfilesWizard;
import org.kalypso.model.wspm.tuhh.ui.light.WspmLightPerspective;

/**
 * @author Gernot Belger
 */
public class UpdatePdbCrossSectionsWizard extends ExportProfilesWizard
{
  private final IPageChangingListener m_pageListener = new IPageChangingListener()
  {
    @Override
    public void handlePageChanging( final PageChangingEvent event )
    {
      pageChanging( event );
    }
  };

  private final UpdatePdbCrossSectionsData m_data = new UpdatePdbCrossSectionsData();

  @Override
  public void init( final IWorkbench workbench, final IStructuredSelection selection )
  {
    final TuhhReach reach = (TuhhReach) selection.getFirstElement();
    m_data.setReach( reach );

    /* Only add connection page, if we are not in the PDB perspective */
    final IConnectionViewer pdbView = getPdbView( workbench );
    if( pdbView == null )
    {
      addPage( new ConnectionChooserPage( "connectionChooser", m_data ) ); //$NON-NLS-1$
    }
    else
    {
      final IPdbConnection viewConnection = pdbView.getConnection();
      m_data.setConnection( viewConnection );
    }

    super.init( workbench, selection );
  }

  private IConnectionViewer getPdbView( final IWorkbench workbench )
  {
    final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    final IWorkbenchPage page = window.getActivePage();
    final IPerspectiveDescriptor perspective = page.getPerspective();

    /* Ignore if view is outside the wspm light perspective */
    if( !WspmLightPerspective.ID.equals( perspective.getId() ) )
      return null;

    return PdbUiUtils.getConnectionViewer( window );
  }

  @Override
  public void addPages( )
  {
    super.addPages();

    final IWizardPage optionsPage = new UpdatePdbCrossSectionsOptionPage( "optionsPage", m_data ); //$NON-NLS-1$
    addPage( optionsPage );
  }

  @Override
  public void setContainer( final IWizardContainer container )
  {
    final IWizardContainer oldContainer = getContainer();
    if( oldContainer instanceof WizardDialog )
      ((WizardDialog) oldContainer).removePageChangingListener( m_pageListener );

    super.setContainer( container );

    if( container instanceof WizardDialog )
      ((WizardDialog) container).addPageChangingListener( m_pageListener );
  }

  protected void pageChanging( final PageChangingEvent event )
  {
    final Object currentPage = event.getCurrentPage();
    if( currentPage instanceof ConnectionChooserPage )
      doConnect( event );
  }

  private void doConnect( final PageChangingEvent event )
  {
    final IStatus result = m_data.doConnect( getContainer() );
    if( result.isOK() )
    {
      final IPdbConnection connection = m_data.getConnection();

      final PDBRole role = connection.getRole();
      if( role == PDBRole.user )
      {
        event.doit = false;
        ((WizardPage) event.getCurrentPage()).setMessage( Messages.getString( "CheckinWspmWizard.0" ), IMessageProvider.WARNING ); //$NON-NLS-1$
        return;
      }

      // event.doit = initializeWorker( connection );
      event.doit = true;
    }
    else
    {
      event.doit = false;
      final IMessage message = MessageUtilitites.convertStatus( result );
      ((WizardPage) event.getCurrentPage()).setMessage( message.getMessage(), message.getMessageType() );
    }
  }

  @Override
  protected IStatus exportProfiles( final IProfileFeature[] profiles, final IProgressMonitor monitor )
  {
    m_data.setSelectedProfiles( profiles );

    final UpdatePdbCrossSectionsOperation operation = new UpdatePdbCrossSectionsOperation( m_data );
    return operation.execute( monitor );
  }
}