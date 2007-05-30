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
package org.kalypsodeegree_impl.graphics.sld;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.kalypsodeegree.filterencoding.FilterEvaluationException;
import org.kalypsodeegree.graphics.sld.ExternalGraphic;
import org.kalypsodeegree.graphics.sld.Graphic;
import org.kalypsodeegree.graphics.sld.Mark;
import org.kalypsodeegree.graphics.sld.ParameterValueType;
import org.kalypsodeegree.graphics.transformation.GeoTransform;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree.xml.Marshallable;
import org.kalypsodeegree_impl.graphics.sld.Symbolizer_Impl.UOM;
import org.kalypsodeegree_impl.tools.Debug;

/**
 * A Graphic is a "graphic symbol" with an inherent shape, color, and size. Graphics can either be referenced from an
 * external URL in a common format (such as GIF or SVG) or may be derived from a Mark. Multiple external URLs may be
 * referenced with the semantic that they all provide the same graphic in different formats. The "hot spot" to use for
 * rendering at a point or the start and finish handle points to use for rendering a graphic along a line must either be
 * inherent in the external format or are system- dependent. The default size of an image format (such as GIF) is the
 * inherent size of the image. The default size of a format without an inherent size is 16 pixels in height and the
 * corresponding aspect in width. If a size is specified, the height of the graphic will be scaled to that size and the
 * corresponding aspect will be used for the width. The default if neither an ExternalURL nor a Mark is specified is to
 * use the default Mark with a size of 6 pixels. The size is in pixels and the rotation is in degrees clockwise, with 0
 * (default) meaning no rotation. In the case that a Graphic is derived from a font-glyph Mark, the Size specified here
 * will be used for the final rendering. Allowed CssParameters are "opacity", "size", and "rotation".
 * <p>
 * 
 * @author <a href="mailto:k.lupp@web.de">Katharina Lupp </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @version $Revision$ $Date$
 */
public class Graphic_Impl implements Graphic, Marshallable
{
  private final ArrayList marksAndExtGraphics = new ArrayList();

  private BufferedImage image = null;

  private ParameterValueType opacity = null;

  private ParameterValueType rotation = null;

  private ParameterValueType size = null;

  /**
   * Creates a new <tt>Graphic_Impl</tt> instance.
   * <p>
   * 
   * @param marksAndExtGraphics
   *            the image will be based upon these
   * @param opacity
   *            opacity that the resulting image will have
   * @param size
   *            image height will be scaled to this value, respecting the proportions
   * @param rotation
   *            image will be rotated clockwise for positive values, negative values result in anti-clockwise rotation
   */
  protected Graphic_Impl( final Object[] marksAndExtGraphics, final ParameterValueType opacity, final ParameterValueType size, final ParameterValueType rotation )
  {
    setMarksAndExtGraphics( marksAndExtGraphics );
    this.opacity = opacity;
    this.size = size;
    this.rotation = rotation;
  }

  /**
   * Creates a new <tt>Graphic_Impl</tt> instance based on the default <tt>Mark</tt>: a square.
   * <p>
   * 
   * @param opacity
   *            opacity that the resulting image will have
   * @param size
   *            image height will be scaled to this value, respecting the proportions
   * @param rotation
   *            image will be rotated clockwise for positive values, negative values result in anti-clockwise rotation
   */
  protected Graphic_Impl( final ParameterValueType opacity, final ParameterValueType size, final ParameterValueType rotation )
  {
    final Mark[] marks = new Mark[1];
    marks[0] = new Mark_Impl( "square", null, null );
    setMarksAndExtGraphics( marks );
    this.opacity = opacity;
    this.size = size;
    this.rotation = rotation;
  }

  /**
   * Creates a new <tt>Graphic_Impl</tt> instance based on the default <tt>Mark</tt>: a square.
   */
  protected Graphic_Impl( )
  {
    this( null, null, null );
  }

  /**
   * Returns an object-array that enables the access to the stored <tt>ExternalGraphic</tt> and <tt>Mark</tt>
   * -instances.
   * <p>
   * 
   * @return contains <tt>ExternalGraphic</tt> and <tt>Mark</tt> -objects
   */
  public Object[] getMarksAndExtGraphics( )
  {
    final Object[] objects = new Object[marksAndExtGraphics.size()];
    return marksAndExtGraphics.toArray( objects );
  }

  /**
   * Sets the <tt>ExternalGraphic</tt>/<tt>Mark<tt>-instances that the image
   * will be based on.
   * <p>
   * @param object to be used as basis for the resulting image
   */
  public void setMarksAndExtGraphics( final Object[] object )
  {
    image = null;
    this.marksAndExtGraphics.clear();

    if( object != null )
    {
      for( final Object element : object )
      {
        marksAndExtGraphics.add( element );
      }
    }
  }

  /**
   * Adds an Object to an object-array that enables the access to the stored <tt>ExternalGraphic</tt> and
   * <tt>Mark</tt> -instances.
   * <p>
   * 
   * @param object
   *            to be used as basis for the resulting image
   */
  public void addMarksAndExtGraphic( final Object object )
  {
    marksAndExtGraphics.add( object );
  }

  /**
   * Removes an Object from an object-array that enables the access to the stored <tt>ExternalGraphic</tt> and
   * <tt>Mark</tt> -instances.
   * <p>
   * 
   * @param object
   *            to be used as basis for the resulting image
   */
  public void removeMarksAndExtGraphic( final Object object )
  {
    marksAndExtGraphics.remove( marksAndExtGraphics.indexOf( object ) );
  }

  /**
   * The Opacity element gives the opacity to use for rendering the graphic.
   * <p>
   * 
   * @param feature
   *            specifies the <tt>Feature</tt> to be used for evaluation of the underlying 'sld:ParameterValueType'
   * @return the (evaluated) value of the parameter
   * @throws FilterEvaluationException
   *             if the evaluation fails or the value is invalid
   */
  public double getOpacity( final Feature feature ) throws FilterEvaluationException
  {
    double opacityVal = OPACITY_DEFAULT;

    if( opacity != null )
    {
      final String value = opacity.evaluate( feature );

      try
      {
        opacityVal = Double.parseDouble( value );
      }
      catch( final NumberFormatException e )
      {
        throw new FilterEvaluationException( "Given value for parameter 'opacity' ('" + value + "') has invalid format!" );
      }

      if( (opacityVal < 0.0) || (opacityVal > 1.0) )
      {
        throw new FilterEvaluationException( "Value for parameter 'opacity' (given: '" + value + "') must be between 0.0 and 1.0!" );
      }
    }

    return opacityVal;
  }

  /**
   * The Opacity element gives the opacity of to use for rendering the graphic.
   * <p>
   * 
   * @param opacity
   *            Opacity to be set for the graphic
   */
  public void setOpacity( final double opacity )
  {
    ParameterValueType pvt = null;
    pvt = StyleFactory.createParameterValueType( "" + opacity );
    this.opacity = pvt;
  }

  /**
   * The Size element gives the absolute size of the graphic in pixels encoded as a floating-point number. This element
   * is also used in other contexts than graphic size and pixel units are still used even for font size. The default
   * size for an object is context-dependent. Negative values are not allowed.
   * <p>
   * 
   * @param feature
   *            specifies the <tt>Feature</tt> to be used for evaluation of the underlying 'sld:ParameterValueType'
   * @return the (evaluated) value of the parameter
   * @throws FilterEvaluationException
   *             if the evaluation fails or the value is invalid
   */
  public double getSize( final Feature feature ) throws FilterEvaluationException
  {
    double sizeVal = SIZE_DEFAULT;

    if( size != null )
    {
      final String value = size.evaluate( feature );

      try
      {
        sizeVal = Double.parseDouble( value );
      }
      catch( final NumberFormatException e )
      {
        throw new FilterEvaluationException( "Given value for parameter 'size' ('" + value + "') has invalid format!" );
      }

      if( sizeVal <= 0.0 )
      {
        throw new FilterEvaluationException( "Value for parameter 'size' (given: '" + value + "') must be greater than 0!" );
      }
    }

    return sizeVal;
  }

  /**
   * Returns the size in pixel, interprted in the defined units, any default behaviours are applied.
   */
  public int getNormalizedSize( final Feature feature, final UOM uom, final GeoTransform transform ) throws FilterEvaluationException
  {
    double size = getSize( feature );

    // if size is unspecified, use the height of the first ExternalGraphic
    if( size < 0 )
    {
      for( int i = 0; i < marksAndExtGraphics.size(); i++ )
      {
        final Object o = marksAndExtGraphics.get( i );
        BufferedImage extImage = null;

        if( o instanceof ExternalGraphic )
        {
          extImage = ((ExternalGraphic) o).getAsImage();
          size = extImage.getHeight();
          break;
        }
      }
    }

    // if there is none, use default value of 6
    if( size < 0 )
      size = 6;

    switch( uom )
    {
      case meter:
        // REMARK: we transform here a length near the origina of the coordinate sytem, which is not correct
        // for all coordinate systems.
        final GM_Envelope sourceRect = transform.getSourceRect();
        final double sizeFromNull = transform.getDestX( sourceRect.getMin().getX() );
        final double sizeFromMeters = transform.getDestX( sourceRect.getMin().getX() + size );
        final double lengthInMeters = Math.abs( sizeFromMeters - sizeFromNull );
        return (int) lengthInMeters;

      case foot:
        throw new UnsupportedOperationException( "Foot unit not implemented yet..." );

      case pixel:
      default:
        return (int) size;

    }
  }

  /**
   * @see org.kalypsodeegree_impl.graphics.sld.Graphic_Impl#getSize(Feature)
   *      <p>
   * @param size
   *            size to be set for the graphic
   */
  public void setSize( final double size )
  {
    ParameterValueType pvt = null;
    pvt = StyleFactory.createParameterValueType( "" + size );
    this.size = pvt;
  }

  /**
   * The Rotation element gives the rotation of a graphic in the clockwise direction about its center point in radian,
   * encoded as a floating- point number. Negative values mean counter-clockwise rotation. The default value is 0.0 (no
   * rotation).
   * <p>
   * 
   * @param feature
   *            specifies the <tt>Feature</tt> to be used for evaluation of the underlying 'sld:ParameterValueType'
   * @return the (evaluated) value of the parameter
   * @throws FilterEvaluationException
   *             if the evaluation fails or the value is invalid
   */
  public double getRotation( final Feature feature ) throws FilterEvaluationException
  {
    double rotVal = ROTATION_DEFAULT;

    if( rotation != null )
    {
      final String value = rotation.evaluate( feature );

      try
      {
        rotVal = Double.parseDouble( value );
      }
      catch( final NumberFormatException e )
      {
        throw new FilterEvaluationException( " Given value for parameter 'rotation' ('" + value + "') has invalid format!" );
      }
    }

    return rotVal;
  }

  /**
   * @see org.kalypsodeegree_impl.graphics.sld.Graphic_Impl#getRotation(Feature)
   *      <p>
   * @param rotation
   *            rotation to be set for the graphic
   */
  public void setRotation( final double rotation )
  {
    ParameterValueType pvt = null;
    pvt = StyleFactory.createParameterValueType( "" + rotation );
    this.rotation = pvt;
  }

  /**
   * Returns a <tt>BufferedImage</tt> representing this object. The image respects the 'Opacity', 'Size' and
   * 'Rotation' parameters. If the 'Size'-parameter is omitted, the height of the first <tt>ExternalGraphic</tt> is
   * used. If there is none, the default value of 6 pixels is used.
   * <p>
   * 
   * @return the <tt>BufferedImage</tt> ready to be painted
   * @throws FilterEvaluationException
   *             if the evaluation fails
   */
  public BufferedImage getAsImage( final Feature feature, final UOM uom, final GeoTransform transform ) throws FilterEvaluationException
  {
    final int intSize = getNormalizedSize( feature, uom, transform );
    if( intSize <= 0 )
      return new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB );
    ;

    image = new BufferedImage( intSize, intSize, BufferedImage.TYPE_INT_ARGB );

    final Graphics2D g = (Graphics2D) image.getGraphics();

    for( int i = 0; i < marksAndExtGraphics.size(); i++ )
    {
      final Object o = marksAndExtGraphics.get( i );
      BufferedImage extImage = null;

      if( o instanceof ExternalGraphic )
      {
        extImage = ((ExternalGraphic) o).getAsImage();
      }
      else
      {
        extImage = ((Mark) o).getAsImage( feature, intSize );
      }

      g.drawImage( extImage, 0, 0, intSize, intSize, null );
    }

    // use the default Mark if there are no Marks / ExternalGraphics
    // specified at all
    if( marksAndExtGraphics.size() == 0 )
    {
      final Mark mark = new Mark_Impl();
      final BufferedImage extImage = mark.getAsImage( feature, intSize );
      g.drawImage( extImage, 0, 0, intSize, intSize, null );
    }

    return image;
  }

  /**
   * Sets a <tt>BufferedImage</tt> representing this object. The image respects the 'Opacity', 'Size' and 'Rotation'
   * parameters.
   * <p>
   * 
   * @param bufferedImage
   *            BufferedImage to be set
   */
  public void setAsImage( final BufferedImage bufferedImage )
  {
    image = bufferedImage;
  }

  /**
   * exports the content of the Graphic as XML formated String
   * 
   * @return xml representation of the Graphic
   */
  public String exportAsXML( )
  {
    Debug.debugMethodBegin();

    final StringBuffer sb = new StringBuffer( 1000 );
    sb.append( "<Graphic>" );
    for( int i = 0; i < marksAndExtGraphics.size(); i++ )
    {
      sb.append( ((Marshallable) marksAndExtGraphics.get( i )).exportAsXML() );
    }
    if( opacity != null )
    {
      sb.append( "<Opacity>" );
      sb.append( ((Marshallable) opacity).exportAsXML() );
      sb.append( "</Opacity>" );
    }
    if( size != null )
    {
      sb.append( "<Size>" );
      sb.append( ((Marshallable) size).exportAsXML() );
      sb.append( "</Size>" );
    }
    if( rotation != null )
    {
      sb.append( "<Rotation>" );
      sb.append( ((Marshallable) rotation).exportAsXML() );
      sb.append( "</Rotation>" );
    }
    sb.append( "</Graphic>" );

    Debug.debugMethodEnd();
    return sb.toString();
  }

}