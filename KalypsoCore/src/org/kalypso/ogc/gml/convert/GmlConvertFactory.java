package org.kalypso.ogc.gml.convert;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kalypso.core.IKalypsoCoreConstants;
import org.kalypso.gml.util.CsvSourceType;
import org.kalypso.gml.util.CsvTargetType;
import org.kalypso.gml.util.FeaturemappingSourceType;
import org.kalypso.gml.util.GmlSourceType;
import org.kalypso.gml.util.GmlconvertType;
import org.kalypso.gml.util.Gmltarget;
import org.kalypso.gml.util.ObjectFactory;
import org.kalypso.gml.util.SourceType;
import org.kalypso.gml.util.TargetType;
import org.kalypso.java.net.IUrlResolver;
import org.kalypso.java.net.UrlUtilities;
import org.kalypso.ogc.gml.convert.source.CsvSourceHandler;
import org.kalypso.ogc.gml.convert.source.FeaturemappingSourceHandler;
import org.kalypso.ogc.gml.convert.source.GmlSourceHandler;
import org.kalypso.ogc.gml.convert.source.ISourceHandler;
import org.kalypso.ogc.gml.convert.target.GmlTargetHandler;
import org.kalypso.ogc.gml.convert.target.ITargetHandler;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.xml.sax.InputSource;

/**
 * Dreh- und Angelpunkt des GML-Konvertierens.
 * 
 * @author belger
 */
public class GmlConvertFactory
{
  private GmlConvertFactory()
  {
  // wird nicht instantiiert
  }

  /**
   * Genau wie {@link #convertXml(InputSource, IUrlResolver, URL)}. K�mmert sich aber um die URL und Stream Details. 
   * @throws IOException
   * @throws GmlConvertException
   * @throws GmlConvertException
   * @throws JAXBException
   */
  public static IStatus convertXml( final URL url, final UrlUtilities utilities ) throws IOException, JAXBException, GmlConvertException
  {
    final URLConnection connection = url.openConnection();
    final String contentEncoding = connection.getContentEncoding();
    final InputStream inputStream = connection.getInputStream();
       
    final InputSource source = new InputSource( inputStream );
    if( contentEncoding != null )
      source.setEncoding( contentEncoding );
    
    return convertXml( source, utilities, url );
  }
  
  /**
   * F�r die in einem XML (gmc) gespeicherte Konvertierung durch.
   * 
   * @param resolver Wird f�r im XMl Referenzierte Dokumente gebraucht.
   * @param context Gegen diesen Kontext werdenim XML definierte Dokumente aufgel�st.
   * @throws JAXBException
   * @throws GmlConvertException
   */
  public static IStatus convertXml( final InputSource inputSource, final IUrlResolver resolver, URL context ) throws JAXBException, GmlConvertException
  {
    final ObjectFactory jc = new ObjectFactory();
    final Unmarshaller unmarshaller = jc.createUnmarshaller();
    final GmlconvertType convert = (GmlconvertType)unmarshaller.unmarshal( inputSource );
    final GMLWorkspace gml = GmlConvertFactory.loadSource( resolver, context, convert.getSource() );
    GmlConvertFactory.writeIntoTarget( resolver, context, gml, convert.getTarget() );
    
    final String message = "Ergebnis wurde nach " + convert.getTarget().getHref() + " geschrieben.";
    return new Status( IStatus.OK, IKalypsoCoreConstants.PLUGIN_ID, 0, message, null );
  }

  
  /**
   * L�dt das GML aus einer Source. Sorgt intern daf�r, dass die richtigen
   * Source-Handler benutzt werden.
   * 
   * @throws GmlConvertException
   */
  public static final GMLWorkspace loadSource( final IUrlResolver resolver, final URL context, final SourceType source )
      throws GmlConvertException
  {
    // switch over source-type
    final ISourceHandler handler;
    if( source instanceof FeaturemappingSourceType )
      handler = new FeaturemappingSourceHandler( resolver, context, (FeaturemappingSourceType)source );
    else if( source instanceof CsvSourceType )
      handler = new CsvSourceHandler( resolver, context, (CsvSourceType)source );
    else if( source instanceof GmlSourceType )
      handler = new GmlSourceHandler( resolver, context, (GmlSourceType)source );
    else
      throw new GmlConvertException( "Unbekannter Source-Type: " + source.getClass().getName() );

    return handler.getWorkspace();
  }

  /**
   * Schreibt das GML ins angegebene Target.
   * @throws GmlConvertException
   *  
   */
  public static void writeIntoTarget( final IUrlResolver resolver, final URL context,
      final GMLWorkspace gml, final TargetType target ) throws GmlConvertException
  {
    final ITargetHandler handler;
    if( target instanceof CsvTargetType )
      handler = new CsvTargetHandler( resolver, context, (CsvTargetType)target );
    else if( target instanceof Gmltarget )
      handler = new GmlTargetHandler( resolver, context, (Gmltarget)target );
    else
      throw new GmlConvertException( "Unbekannter Target-Type: " + target.getClass().getName() );

    handler.saveWorkspace( gml );
  }
}
