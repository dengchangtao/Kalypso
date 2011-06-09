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
package org.kalypso.model.km.internal.ui.kmupdate;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.kalypso.contribs.eclipse.jface.operation.RunnableContextHelper;
import org.kalypso.contribs.eclipse.jface.viewers.table.ColumnsResizeControlListener;
import org.kalypso.contribs.eclipse.swt.widgets.ColumnViewerSorter;
import org.kalypso.contribs.java.lang.NumberUtils;
import org.kalypso.core.status.StatusDialog;
import org.kalypso.model.km.internal.binding.KMBindingUtils;
import org.kalypso.model.km.internal.core.ProfileData;
import org.kalypso.model.km.internal.core.ProfileDataSet;
import org.kalypso.model.km.internal.i18n.Messages;

import de.tu_harburg.wb.kalypso.rrm.kalininmiljukov.KalininMiljukovType;
import de.tu_harburg.wb.kalypso.rrm.kalininmiljukov.KalininMiljukovType.Profile;

/**
 * @author Andreas Doemming (original)
 * @author Holger Albert (modified)
 */
public class KMViewer
{
  /**
   * The text field for the label.
   */
  private Text m_labelText;

  /**
   * The widget for selecting a file.
   */
  private DirectoryFieldWidget m_dirField;

  /**
   * The text for the start km.
   */
  private Text m_startText;

  /**
   * The text for the end km.
   */
  private Text m_endText;

  /**
   * The checkbox table viewer, which displays the profiles.
   */
  private CheckboxTableViewer m_profileListViewer;

  /**
   * The abstract profile data set.
   */
  private ProfileDataSet m_profileSet;

  /**
   * The input.
   */
  private KalininMiljukovType m_input;

  private final IWizardContainer m_context;

  public KMViewer( final IWizardContainer context )
  {
    m_context = context;
  }

  /**
   * This function creates the controls.
   * 
   * @param parent
   *          The parent composite.
   */
  public void createControls( final Composite parent )
  {
    /* Set a layout to the parent. */
    parent.setLayout( new GridLayout( 3, false ) );

    /* Create a empty label. */
    final Label emptyLabel = new Label( parent, SWT.NONE );
    emptyLabel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false ) );

    /* Create a text. */
    m_labelText = new Text( parent, SWT.BORDER | SWT.READ_ONLY );
    m_labelText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

    /* Create a empty label. */
    final Label emptyLabel1 = new Label( parent, SWT.NONE );
    emptyLabel1.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false ) );

    /* Create a widget for asking for a file. */
    m_dirField = new DirectoryFieldWidget( parent, m_context ); //$NON-NLS-1$ //$NON-NLS-2$
    m_dirField.addSelectionChangedListener( new ISelectionChangedListener()
    {
      @Override
      public void selectionChanged( final SelectionChangedEvent event )
      {
        handleDirectoryFieldSelected( (IStructuredSelection) event.getSelection() );
      }
    } );

    /* Create a label. */
    final Label startLabel = new Label( parent, SWT.NONE );
    startLabel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false ) );
    startLabel.setText( Messages.getString( "org.kalypso.ui.rrm.kmupdate.KMViewer.2" ) ); //$NON-NLS-1$

    /* Create a text. */
    m_startText = new Text( parent, SWT.BORDER );
    m_startText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

    /* Create a empty label. */
    final Label emptyLabel2 = new Label( parent, SWT.NONE );
    emptyLabel2.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false ) );

    /* Create a label. */
    final Label endLabel = new Label( parent, SWT.NONE );
    endLabel.setText( Messages.getString( "org.kalypso.ui.rrm.kmupdate.KMViewer.3" ) ); //$NON-NLS-1$
    endLabel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false ) );

    /* Create a text. */
    m_endText = new Text( parent, SWT.BORDER );
    m_endText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

    /* Create a empty label. */
    final Label emptyLabel3 = new Label( parent, SWT.NONE );
    emptyLabel3.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false ) );

    /* Create a label. */
    final Label profilesLabel = new Label( parent, SWT.NONE );
    profilesLabel.setLayoutData( new GridData( SWT.BEGINNING, SWT.BEGINNING, false, false ) );
    profilesLabel.setText( Messages.getString( "org.kalypso.ui.rrm.kmupdate.KMViewer.4" ) ); //$NON-NLS-1$

    /* Create a checkbox table viewer. */
    m_profileListViewer = CheckboxTableViewer.newCheckList( parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.HIDE_SELECTION | SWT.FULL_SELECTION );
    final Table table = m_profileListViewer.getTable();
    table.setHeaderVisible( true );
    m_profileListViewer.getControl().setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 ) );
    m_profileListViewer.setContentProvider( new KMViewerContentProvider() );
    m_profileListViewer.setCheckStateProvider( new KMViewerCheckStateProvider( this ) );

    /* Create a table viewer column. */
    // TableViewerColumn labelColumn = new TableViewerColumn( m_profileListViewer, SWT.LEFT );
    // labelColumn.setLabelProvider( new ProfileNameLabelProvider() );
    //    labelColumn.getColumn().setText( Messages.getString( "KMViewer_0" ) ); //$NON-NLS-1$
    // labelColumn.getColumn().setWidth( 100 );
    // ColumnViewerSorter.registerSorter( labelColumn, new ProfileNameSorter() );

    /* Create a table viewer column. */
    final TableViewerColumn stationViewerColumn = new TableViewerColumn( m_profileListViewer, SWT.LEFT );
    stationViewerColumn.setLabelProvider( new ProfileStationLabelProvider() );
    final TableColumn stationColumn = stationViewerColumn.getColumn();
    stationColumn.setText( Messages.getString( "KMViewer_1" ) ); //$NON-NLS-1$
    stationColumn.setResizable( false );
    stationColumn.setData( ColumnsResizeControlListener.DATA_MIN_COL_WIDTH, ColumnsResizeControlListener.MIN_COL_WIDTH_PACK );
    ColumnViewerSorter.registerSorter( stationViewerColumn, new ProfileStationSorter() );

    /* Create a table viewer column. */
    final TableViewerColumn validViewerColumn = new TableViewerColumn( m_profileListViewer, SWT.LEFT );
    validViewerColumn.setLabelProvider( new ProfileValidLabelProvider( this ) );
    final TableColumn validColumn = validViewerColumn.getColumn();
    validColumn.setText( Messages.getString( "KMViewer_2" ) ); //$NON-NLS-1$
    stationColumn.setResizable( false );
    validColumn.setData( ColumnsResizeControlListener.DATA_MIN_COL_WIDTH, ColumnsResizeControlListener.MIN_COL_WIDTH_PACK );

    /* Make sure, the columns are properly resized. */
    table.addControlListener( new ColumnsResizeControlListener() );

    /* Add a listener. */
    m_startText.addFocusListener( new FocusAdapter()
    {
      /**
       * @see org.eclipse.swt.events.FocusAdapter#focusLost(org.eclipse.swt.events.FocusEvent)
       */
      @Override
      public void focusLost( final FocusEvent e )
      {
        handleKMStartModified();
      }
    } );

    /* Add a listener. */
    m_endText.addFocusListener( new FocusAdapter()
    {
      @Override
      public void focusLost( final FocusEvent e )
      {
        handleKMEndModified();
      }
    } );
  }

  protected void handleKMStartModified( )
  {
    try
    {
      final KalininMiljukovType input = getInput();
      if( input != null )
      {
        final double km = NumberUtils.parseQuietDouble( m_startText.getText() );
        if( Double.isNaN( km ) )
          input.setKmStart( null );
        else
          input.setKmStart( km );
      }

      updateProfileList();
      updateControls();
    }
    catch( final Exception ex )
    {
      ex.printStackTrace();
    }
  }

  protected void handleKMEndModified( )
  {
    try
    {
      final KalininMiljukovType input = getInput();
      if( input != null )
      {
        final double km = NumberUtils.parseQuietDouble( m_endText.getText() );
        if( Double.isNaN( km ) )
          input.setKmEnd( null );
        else
          input.setKmEnd( km );
      }

      updateProfileList();
      updateControls();
    }
    catch( final Exception ex )
    {
      ex.printStackTrace();
    }
  }

  protected void handleDirectoryFieldSelected( final IStructuredSelection selection )
  {
    final String path = (String) selection.getFirstElement();

    final KalininMiljukovType input = getInput();
    if( input != null )
      input.setPath( path );

    readProfileSet( path );

    if( m_profileSet != null && input != null )
    {
      final Double oldKmStart = input.getKmStart();
      if( oldKmStart == null )
      {
        final double startPosition = m_profileSet.getStartPosition() / 1000d;
        input.setKmStart( startPosition );
      }

      final Double oldKmEnd = input.getKmEnd();
      if( oldKmEnd == null )
      {
        final double endPosition = m_profileSet.getEndPosition() / 1000d;
        input.setKmEnd( endPosition );
      }
    }

    updateProfileList();
    updateControls();
  }

  private void readProfileSet( final String path )
  {
    /* Prevents dead lock during construction of page */
    if( StringUtils.isBlank( path ) )
    {
      m_profileSet = null;
      return;
    }

    final ProfileSetReadOperation operation = new ProfileSetReadOperation( path );
    final IStatus status = RunnableContextHelper.execute( m_context, true, false, operation );
    m_profileSet = operation.getProfileSet();
    if( !status.isOK() )
      new StatusDialog( m_context.getShell(), status, "Read Profile Data" );
  }

  private KalininMiljukovType getInput( )
  {
    return m_input;
  }

  private void updateProfileList( )
  {
    final KalininMiljukovType input = getInput();
    if( input == null )
      return;

    final List<Profile> profileList = input.getProfile();
    profileList.clear();

    if( m_profileSet == null )
      return;

    final Double kmStart = input.getKmStart();
    final Double kmEnd = input.getKmEnd();

    final ProfileData[] allProfiles = m_profileSet.getAllProfiles();
    for( final ProfileData pd : allProfiles )
    {
      final String file = pd.getFile();
      final double position = pd.getStation();
      final double station = position / 1000.0;

      if( (kmStart == null || kmStart <= station) && (kmEnd == null || station <= kmEnd) )
      {
        final Profile profileData = KMBindingUtils.OF.createKalininMiljukovTypeProfile();
        profileData.setFile( file );
        profileData.setPositionKM( position );
        profileData.setEnabled( pd.isValidForKalypso() == null );
        profileList.add( profileData );
      }
    }
  }

  private void updateControls( )
  {
    final KalininMiljukovType input = getInput();
    if( input == null )
    {
      m_labelText.setText( Messages.getString( "KMViewer_3" ) ); //$NON-NLS-1$
      m_dirField.setSelection( StructuredSelection.EMPTY );
      m_dirField.setEnabled( false );
      m_startText.setText( "" ); //$NON-NLS-1$
      m_startText.setEnabled( false );
      m_endText.setText( "" ); //$NON-NLS-1$
      m_endText.setEnabled( false );
      m_profileListViewer.getControl().setEnabled( false );
    }
    else
    {
      final Double kmStart = input.getKmStart();
      final Double kmEnd = input.getKmEnd();
      final String path = input.getPath();

      m_dirField.setSelection( new StructuredSelection( path ) );

      if( kmStart != null )
        m_startText.setText( String.format( "%.4f", kmStart ) ); //$NON-NLS-1$
      else
        m_startText.setText( "" ); //$NON-NLS-1$

      if( kmEnd != null )
        m_endText.setText( String.format( "%.4f", kmEnd ) ); //$NON-NLS-1$
      else
        m_endText.setText( "" ); //$NON-NLS-1$

      m_dirField.setEnabled( true );
      m_startText.setEnabled( !StringUtils.isBlank( path ) );
      m_endText.setEnabled( !StringUtils.isBlank( path ) );
      m_profileListViewer.getControl().setEnabled( !StringUtils.isBlank( path ) );
    }

    m_profileListViewer.refresh();
  }

  private void inputChanged( final KalininMiljukovType oldInput, final KalininMiljukovType kmType )
  {
    // TODO: Check this stuff here...
    if( oldInput != null )
    {
      /* Keep checked elements. */
      final List<Profile> profiles = oldInput.getProfile();
      for( final Profile profile2 : profiles )
        profile2.setEnabled( false );

      final Object[] checkedElements = m_profileListViewer.getCheckedElements();
      for( final Object object : checkedElements )
      {
        for( final Profile profile : profiles )
        {
          if( object == profile )
            profile.setEnabled( true );
        }
      }
    }

    m_input = kmType;

    if( m_profileListViewer != null )
      m_profileListViewer.setInput( kmType );

    final String path = m_input == null ? null : m_input.getPath();

    readProfileSet( path );

    updateControls();
  }

  private ProfileData findData( final Profile profile )
  {
    if( m_profileSet == null )
      return null;

    final String file = profile.getFile();

    final ProfileData[] data = m_profileSet.getAllProfiles();
    for( final ProfileData profileData : data )
    {
      if( file.equals( profileData.getFile() ) )
        return profileData;
    }

    return null;
  }

  public void setInput( final String label, final KalininMiljukovType km )
  {
    m_labelText.setText( label );

    final KalininMiljukovType oldInput = getInput();

    inputChanged( oldInput, km );
  }

  public String getValidMessage( final Profile profile )
  {
    final ProfileData data = findData( profile );
    if( data == null )
      return StringUtils.EMPTY;

    final String valid = data.isValidForKalypso();
    if( valid == null )
      return Messages.getString( "org.kalypso.model.km.ProfileData.10" ); //$NON-NLS-1$

    return valid;
  }

  public boolean isValid( final Profile profile )
  {
    final ProfileData data = findData( profile );
    if( data == null )
      return false;

    return data.isValidForKalypso() == null;
  }
}