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
package org.kalypso.ui.metadoc;

import org.eclipse.jface.wizard.Wizard;

/**
 * @deprecated use new metadoc stuff
 * @author belger
 */
public class ExportBerichtWizard extends Wizard
{
//  private final Document m_docBean;
//
//  private FeaturePage m_featurePage;
//
//  private final Feature m_feature;

//  protected final IExportableDocument m_document2export;

//  public ExportBerichtWizard( final IExportableDocument document2export, final Document doc )
//  {
////    m_document2export = document2export;
//    m_docBean = doc;
//
//    final IDialogSettings workbenchSettings = UIPlugin.getDefault().getDialogSettings();
//    IDialogSettings section = workbenchSettings.getSection( "ExportTableWizard" );//$NON-NLS-1$
//    if( section == null )
//      section = workbenchSettings.addNewSection( "ExportTableWizard" );//$NON-NLS-1$
//    setDialogSettings( section );
//
//    setWindowTitle( "Berichtsablage" );
//
//    final IScenario scenario = KalypsoAuthPlugin.getDefault().getScenarioForCurrentUser();
//    
//    // create featuretype from bean
//    final Collection ftpColl = new ArrayList();
//    final Collection fpColl = new ArrayList();
//    final Map metadata = doc.getMetadata();
//    final int[] ints = new int[metadata.size()];
//    int count = 0;
//    for( final Iterator iter = metadata.entrySet().iterator(); iter.hasNext(); )
//    {
//      final Map.Entry entry = (Entry)iter.next();
//
//      final String name = entry.getKey().toString();
//
//      final String[] splits = entry.getValue().toString().split( ";" );
//
//      final String xmltype = splits.length == 0 ? String.class.getName() : splits[0];
//
//      String value = splits.length >= 2 ? splits[1] : null;
//
//      // TRICKY: maybe replace tokens with scenario specific values
//      value = ScenarioUtilities.replaceTokens( value, scenario );
//      // Important: always put value again in metadata, in case it changed
//      metadata.put( name, value );
//
//      String typename = null;
//      Object realValue = null;
//      try
//      {
//        typename = Mapper.mapXMLSchemaType2JavaType( xmltype );
//        realValue = value == null ? null : Mapper.mapXMLValueToJava( value, typename );
//      }
//      catch( final Exception e )
//      {
//        e.printStackTrace();
//        typename = "java.lang.String";
//      }
//      ftpColl.add( FeatureFactory.createFeatureTypeProperty( name, typename, false ) );
//      fpColl.add( FeatureFactory.createFeatureProperty( name, realValue ) );
//
//      ints[count++] = 1;
//    }
//
//    final FeatureTypeProperty[] ftps = (FeatureTypeProperty[])ftpColl.toArray( new FeatureTypeProperty[ftpColl.size()] );
//    final FeatureType ft = FeatureFactory.createFeatureType( "docbean", null, ftps, ints, ints, null, new HashMap() );
//
//    m_feature = FeatureFactory.createFeature( "0", ft, (FeatureProperty[])fpColl.toArray( new FeatureProperty[fpColl
//        .size()] ) );
//  }
//
//  /**
//   * @see org.eclipse.jface.wizard.Wizard#addPages()
//   */
//  public void addPages()
//  {
//    super.addPages();
//
//    m_featurePage = new FeaturePage( "featurePage", "Metadaten editieren", ImageProvider.IMAGE_UTIL_BERICHT_WIZ, true,
//        null, m_feature );
//
//    addPage( m_featurePage );
//  }
//
  public boolean performFinish()
  {
//    final Document docBean = commitData();
//
//    final ICoreRunnableWithProgress op = new ICoreRunnableWithProgress(  )
//    {
//      public IStatus execute( IProgressMonitor monitor ) throws InvocationTargetException
//      {
//        FileOutputStream outs = null;
//        try
//        {
//          // export
//          outs = new FileOutputStream( docBean.getFile() );
//          m_document2export.exportDocument( outs );
//          return Status.OK_STATUS;
//        }
//        catch( final Exception e )
//        {
//          e.printStackTrace();
//          throw new InvocationTargetException( e );
//        }
//        finally
//        {
//          IOUtils.closeQuietly( outs );
//        }
//      }
//    };
//
//    final IStatus status = RunnableContextHelper.execute( getContainer(), true, false, op );
//    if( !status.isOK() )
//      ErrorDialog.openError( getShell(), "Berichtsablage", "Dokument konnte nicht abgelegt werden", status );
//
    return true;
  }
//
//  protected Document commitData()
//  {
//    final Collection changes = m_featurePage.getChanges(  );
//    final Document docBean = m_docBean;
//    final Map metadata = docBean.getMetadata();
//    for( final Iterator iter = changes.iterator(); iter.hasNext(); )
//    {
//      final FeatureChange fc = (FeatureChange)iter.next();
//
//      final Object newValue = Mapper.mapJavaValueToXml( fc.newValue );
//      metadata.put( fc.property, newValue );
//    }
//    return docBean;
//  }
}