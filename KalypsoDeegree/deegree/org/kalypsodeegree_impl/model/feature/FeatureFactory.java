/*--------------- Kalypso-Deegree-Header ------------------------------------------------------------

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
 
 
 history:
 
 Files in this package are originally taken from deegree and modified here
 to fit in kalypso. As goals of kalypso differ from that one in deegree
 interface-compatibility to deegree is wanted but not retained always. 
 
 If you intend to use this software in other ways than in kalypso 
 (e.g. OGC-web services), you should consider the latest version of deegree,
 see http://www.deegree.org .

 all modifications are licensed as deegree, 
 original copyright:
 
 Copyright (C) 2001 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
 lat/lon GmbH
 http://www.lat-lon.de
 
 ---------------------------------------------------------------------------------------------------*/
package org.kalypsodeegree_impl.model.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kalypsodeegree.gml.GMLFeature;
import org.kalypsodeegree.gml.GMLGeometry;
import org.kalypsodeegree.gml.GMLProperty;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureAssociationTypeProperty;
import org.kalypsodeegree.model.feature.FeatureList;
import org.kalypsodeegree.model.feature.FeatureProperty;
import org.kalypsodeegree.model.feature.FeatureType;
import org.kalypsodeegree.model.feature.FeatureTypeProperty;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree_impl.extension.ITypeHandler;
import org.kalypsodeegree_impl.extension.TypeRegistrySingleton;
import org.kalypsodeegree_impl.gml.schema.Mapper;
import org.kalypsodeegree_impl.gml.schema.virtual.VirtualFeatureTypeRegistry;
import org.kalypsodeegree_impl.model.geometry.GMLAdapter;
import org.kalypsodeegree_impl.model.sort.SplitSort;
import org.kalypsodeegree_impl.tools.Debug;

/**
 * This factory offers methods for creating Features, FeatureCollection and all
 * direct related classes/interfaces that are part of the
 * org.kalypsodeegree.model.feature package.
 * 
 * <p>
 * -----------------------------------------------------------------------
 * </p>
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @version $Revision$ $Date$
 */
public class FeatureFactory
{

  private static final String DEFAULTNAMESPACE = "www.generic";

  /**
   * creates an instance of a FeatureTypeProperty from its name and the data
   * type it describes
   * 
   * @param name
   *          name of the feature type property
   * @param type
   *          type represented by the feature type property
   * @param nullable
   *          true if the feature type property is allowed to be <CODE>null
   *          </CODE>
   * @return instance of a <CODE>FeatureTypeProperty</CODE>
   */
  public static FeatureTypeProperty createFeatureTypeProperty( String name, String type,
      boolean nullable )
  {
    return createFeatureTypeProperty( name, DEFAULTNAMESPACE, type, nullable, null );
    // return new FeatureTypeProperty_Impl( name, type, nullable );
  }

  public static FeatureTypeProperty createFeatureTypeProperty( String name, String namespace,
      String type, boolean nullable, Map annotationMap )
  {
    return new FeatureTypeProperty_Impl( name, namespace, type, nullable, annotationMap );
  }

  /**
   * creates an instance of a FeatureType from an array of
   * FeatureTypeProperties, its parents and childs and its name.
   * 
   * @param name
   *          name of the <CODE>FeatureType</CODE>
   * @param properties
   *          properties containing the <CODE>FeatureType</CODE> s content
   * @return instance of a <CODE>FeatureType</CODE>
   * @deprecated
   *  
   */
  public static FeatureType createFeatureType( String name, FeatureTypeProperty[] properties )
  {
    final int[] defaultOccurs = new int[properties.length];
    for( int i = 0; i < defaultOccurs.length; i++ )
      defaultOccurs[i] = 1;
    return createFeatureType( name, DEFAULTNAMESPACE, properties, defaultOccurs, defaultOccurs,
        null, new HashMap() );
  }

  public static FeatureType createFeatureType( String name, String namespace,
      FeatureTypeProperty[] properties, int[] minOccurs, int[] maxOccurs, String substitutionGroup,
      Map annotationMap )
  {
    return new FeatureType_Impl( name, namespace, properties, minOccurs, maxOccurs,
        substitutionGroup, annotationMap );
  }

  /**
   * creates an instance of a FeatureProperty from its name and the data (value)
   * it contains
   * 
   * @param name
   *          name of the <CODE>FeatureProperty</CODE>
   * @return an instance of a <CODE>FeatureProperty</CODE>
   * @param value
   *          value of the <CODE>FeatureProperty</CODE>
   */
  public static FeatureProperty createFeatureProperty( String name, Object value )
  {
    return new FeatureProperty_Impl( name, value );
  }

  /**
   * creates an instance of a Feature from its FeatureType and an array of
   * Objects that represents it properties. It is assumed that the order of the
   * properties is identical to the order of the FeatureTypeProperties of the
   * the FeatureType.
   * 
   * @param id
   *          unique id of the <CODE>Feature</CODE>
   * @param featureType
   *          <CODE>FeatureType</CODE> of the <CODE>Feature</CODE>
   * @param properties
   *          properties (content) of the <CODE>Feature</CODE>
   * @return instance of a <CODE>Feature</CODE>
   */
  public static Feature createFeature( String id, FeatureType featureType, Object[] properties )
  {
    return new Feature_Impl( featureType, id, properties );
  }

  public static Feature createFeature( String id, FeatureType featureType )
  {
    return new Feature_Impl( featureType, id );
  }

  /**
   * creates an instance of a Feature from its FeatureType and an array of
   * Objects that represents it properties. It is assumed that the order of the
   * properties is identical to the order of the FeatureTypeProperties of the
   * the FeatureType.
   * 
   * @param id
   *          unique id of the <CODE>Feature</CODE>
   * @param featureType
   *          <CODE>FeatureType</CODE> of the <CODE>Feature</CODE>
   * @param properties
   *          properties (content) of the <CODE>Feature</CODE>
   * @return instance of a <CODE>Feature</CODE>
   */
  public static Feature createFeature( String id, FeatureType featureType,
      FeatureProperty[] properties )
  {
    //    return new Feature_Impl( id, featureType, properties );

    Object[] o = new Object[properties.length];
    FeatureTypeProperty[] ftp = featureType.getProperties();
    for( int i = 0; i < ftp.length; i++ )
    {
      String name = ftp[i].getName();
      for( int j = 0; j < properties.length; j++ )
      {
        if( properties[j].getName().equals( name ) )
        {
          o[i] = properties[j].getValue();
          break;
        }
      }
    }

    Debug.debugMethodEnd();
    //	return new Feature_Impl( id, featureType, properties );
    return createFeature( id, featureType, o );
  }

  /**
   * creates an instance of a Feature from its FeatureType and a GMLFeature that
   * contains the features data.
   * 
   * @param gmlFeature
   *          instance of a <CODE>GMLFeature</CODE>
   * @return instance of a <CODE>Feature</CODE>
   * @deprecated do not create feature without GML-applicationschema
   */
  public static Feature createFeature( GMLFeature gmlFeature )
  {
    Debug.debugMethodBegin();

    GMLProperty[] props = gmlFeature.getProperties();
    FeatureTypeProperty[] ftp = new FeatureTypeProperty[props.length];
    FeatureProperty[] fp = new FeatureProperty[props.length];
    for( int j = 0; j < props.length; j++ )
    {

      ftp[j] = createFeatureTypeProperty( props[j].getName(),
          getType( props[j].getPropertyType() ), true );
      Object o = props[j].getPropertyValue();
      if( o instanceof GMLGeometry )
      {
        try
        {
          o = GMLAdapter.wrap( (GMLGeometry)o );
        }
        catch( Exception e )
        {
          System.out.println( " eee " + e );
          continue;
        }
      }

      fp[j] = createFeatureProperty( props[j].getName(), o );
    }
    FeatureType featureType = createFeatureType( gmlFeature.getName(), ftp );

    String id = gmlFeature.getId();

    Feature feature = createFeature( id, featureType, fp );

    Debug.debugMethodEnd();
    return feature;
  }

  public static Feature createFeature( final GMLFeature gmlFeature,
      final FeatureType featureTypes[] ) throws Exception
  {
    Debug.debugMethodBegin();

    final String featureName = gmlFeature.getLocalName();
    final String featureNamespace = gmlFeature.getNamespaceURI();

    FeatureType featureType = null;
    for( int ft_i = 0; ft_i < featureTypes.length; ft_i++ )
    {
      final String name = featureTypes[ft_i].getName();
      final String namespace = featureTypes[ft_i].getNamespace();

      if( featureName.equals( name ) && featureNamespace.equals( namespace ) )
      {
        featureType = featureTypes[ft_i];
        break;
      }
    }

    if( featureType == null )
      throw new Exception( "Could not find named feature " + featureNamespace + ":" + featureName
          + " in schema" );

    final GMLProperty[] gmlProps = gmlFeature.getProperties();

    String id = gmlFeature.getId();
    Feature feature = new Feature_Impl( featureType, id );

    // every gmlProp must fit to a featurePropertyType
    for( int p = 0; p < gmlProps.length; p++ )
    {
      // TODO: compare properties by namespace
      GMLProperty gmlProp = gmlProps[p];
      final String propName = gmlProp.getName();
      int propertyPosition = featureType.getPropertyPosition( propName );

      FeatureTypeProperty ftp = featureType.getProperty( propName );
      if( ftp == null )
        throw new Exception( "property '" + propName + "' not defined in schema" );

      Object o = wrap( ftp, gmlProp );

      int maxOccurs = featureType.getMaxOccurs( propertyPosition );
      if( maxOccurs > 1 || maxOccurs == FeatureType.UNBOUND_OCCURENCY )
        ( (List)feature.getProperty( propertyPosition ) ).add( o );
      else
        feature.setProperty( createFeatureProperty( propName, o ) );
    }

    Debug.debugMethodEnd();
    return feature;
  }

  /** Creates default feature, used by LegendView */
  public static Feature createDefaultFeature( final FeatureType ft, final boolean createGeometry )
  {
    final FeatureTypeProperty[] propTypes = ft.getProperties();
    final FeatureProperty[] props = createDefaultFeatureProperty( propTypes, createGeometry );
    final Feature feature = FeatureFactory.createFeature( "default", ft, props );
    return feature;
  }

  /** Creates default FeatureProperties, used by LegendView */
  public static FeatureProperty[] createDefaultFeatureProperty(
      final FeatureTypeProperty[] propTypes, final boolean createGeometry )
  {
    // TODO handle occurency here and generate empty List or FeatureList as default
      final List results = new ArrayList();
    for( int i = 0; i < propTypes.length; i++ )
    {
      final FeatureTypeProperty ftp = propTypes[i];

      final String type = ftp.getType();
      final Object value = Mapper.defaultValueforJavaType( type, createGeometry );
      results.add( FeatureFactory.createFeatureProperty( ftp.getName(), value ) );
    }
    return (FeatureProperty[])results.toArray( new FeatureProperty[results.size()] );
  }

  private static Object wrap( FeatureTypeProperty ftp, GMLProperty gmlProperty ) throws Exception
  {
    if( ftp instanceof FeatureAssociationTypeProperty )
      return wrapXLink( ftp, gmlProperty );
    return wrapNOXLink( ftp, gmlProperty );
  }

  private static Object wrapXLink( FeatureTypeProperty ftp, GMLProperty gmlProperty )
  {
    final Object value = gmlProperty.getPropertyValue();
    Object result = null;
    if( ftp instanceof FeatureAssociationTypeProperty )
    {
      if( value != null && value instanceof GMLFeature )
      {
        FeatureAssociationTypeProperty featureAssociationTypeProperty = (FeatureAssociationTypeProperty)ftp;
        FeatureType[] linkFTs = featureAssociationTypeProperty.getAssociationFeatureTypes();

        try
        {
          result = createFeature( (GMLFeature)value, linkFTs );
        }
        catch( Exception e )
        {
          e.printStackTrace();
        }
      }
      else
      {
        String string = (String)gmlProperty.getAttributeValue( "http://www.w3.org/1999/xlink",
            "href" );
        // remove leading "#"
        if( string.startsWith( "#" ) )
          result = string.substring( 1 );
        else
          result = string;
      }
    }
    return result;
  }

  private static Object wrapNOXLink( FeatureTypeProperty ftp, GMLProperty gmlProperty )
      throws Exception
  {

    final String type = ftp.getType();
    if( type == null )
      System.out.println( "no Type" );
    final ITypeHandler typeHandler = TypeRegistrySingleton.getTypeRegistry()
        .getTypeHandlerForClassName( type );
//  TODO give context not null
    if( typeHandler != null )
      return typeHandler.unmarshall( gmlProperty.getElement() ,null);
    //FeatureAssociationType
    final Object o = gmlProperty.getPropertyValue();
    if( o == null )
    {
      if( ftp.isNullable() )
        return null;
      throw new Exception( "Property " + ftp.getName() + " is not nullable, but value is" );
    }

    //		System.out.println("Object"+o.getClass().toString());
    if( o instanceof String )
      return Mapper.mapXMLValueToJava( (String)o, type );

    if( o instanceof GMLGeometry && type.startsWith( "org.kalypsodeegree.model.geometry." ) )
      return GMLAdapter.wrap( (GMLGeometry)o );
    if( o instanceof GMLGeometry )
      System.out.println( o.getClass().toString() );
    throw new Exception( "could not convert property (" + o.toString() + ") to " + type );
  }

  /**
   * returns the name of the (toplevel)class that is assigned to the submitted
   * GML property type.
   * 
   * @param t
   *          GML property type
   */
  private static String getType( int t )
  {

    String type = "java.lang.Object";
    switch( t )
    {
    case GMLProperty.STRING:
      type = "java.lang.String";
      break;
    case GMLProperty.GEOMETRY:
      type = "org.kalypsodeegree.model.geometry.GM_Object";
      break;
    case GMLProperty.POINT:
      type = "org.kalypsodeegree.model.geometry.GM_Point";
      break;
    case GMLProperty.LINESTRING:
      type = "org.kalypsodeegree.model.geometry.GM_LineString";
      break;
    case GMLProperty.POLYGON:
      type = "org.kalypsodeegree.model.geometry.GM_Polygon";
      break;
    case GMLProperty.MULTIGEOMETRY:
      type = "org.kalypsodeegree.model.geometry.GM_Object";
      break;
    case GMLProperty.MULTILINESTRING:
      type = "org.kalypsodeegree.model.geometry.GM_Object";
      break;
    case GMLProperty.MULTIPOINT:
      type = "org.kalypsodeegree.model.geometry.GM_MultiPoint";
      break;
    case GMLProperty.MULTIPOLYGON:
      // TODO
      type = "org.kalypsodeegree.model.geometry.GM_Object";
      break;
    case GMLProperty.FEATURE:
      type = "org.kalypsodeegree.model.feature.Feature";
      break;
    case GMLProperty.FEATURECOLLECTION:
      type = "org.kalypsodeegree.model.feature.FeatureCollection";
      break;
    case GMLProperty.BOX:
      type = "org.kalypsodeegree.model.geometry.GM_Envelope";
      break;
    }
    return type;
  }

  public static FeatureList createFeatureList( final List list )
  {
    final SplitSort result = new SplitSort();
    result.addAll( list );
    return result;
  }

  public static FeatureList createFeatureList()
  {
    return new SplitSort();
  }

  public static FeatureList createFeatureList( final GM_Envelope env )
  {
    return new SplitSort( env );
  }

  public static FeatureTypeProperty[] createVirtualFeatureTypeProperties( FeatureType realFeatureType )
  {
    final FeatureTypeProperty[] properties = realFeatureType.getProperties();
    final List newFTP = new ArrayList();
    for( int i = 0; i < properties.length; i++ )
    {
      FeatureTypeProperty ftp = properties[i];
      FeatureTypeProperty[] newFtp = VirtualFeatureTypeRegistry.getInstance()
          .getVirtualFeatureTypePropertiesFor( ftp );
      for( int j = 0; j < newFtp.length; j++ )
        newFTP.add( newFtp[j] );
    }
    return (FeatureTypeProperty[])newFTP.toArray( new FeatureTypeProperty[newFTP.size()] );       
  }

}