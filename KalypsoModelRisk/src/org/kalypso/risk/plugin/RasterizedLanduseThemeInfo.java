package org.kalypso.risk.plugin;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.kalypso.gml.ui.map.CoverageThemeInfo;
import org.kalypso.ogc.gml.IKalypsoThemeInfo;
import org.kalypso.risk.i18n.Messages;
import org.kalypsodeegree.model.geometry.GM_Position;

public class RasterizedLanduseThemeInfo extends CoverageThemeInfo implements IKalypsoThemeInfo
{
  public static final String DEFAULT_FORMAT_STRING = Messages.getString("org.kalypso.risk.plugin.RasterizedLanduseThemeInfo.0"); //$NON-NLS-1$

  private static Map<Double, String> LANDUSE_CLASSES_MAP = new HashMap<Double, String>();

  /**
   * @see org.kalypso.gml.ui.map.CoverageThemeInfo#initFormatString(java.util.Properties)
   */
  @Override
  protected String initFormatString( Properties props )
  {
    return props.getProperty( PROP_FORMAT, DEFAULT_FORMAT_STRING );
  }

  public static void updateClassesDefinition( final HashMap<Double, String> values )
  {
    LANDUSE_CLASSES_MAP.clear();
    for( final Double key : values.keySet() )
      LANDUSE_CLASSES_MAP.put( key, values.get( key ) );
  }

  /**
   * @see org.kalypso.gml.ui.map.CoverageThemeInfo#appendQuickInfo(java.util.Formatter,
   *      org.kalypsodeegree.model.geometry.GM_Position)
   */
  @Override
  public void appendQuickInfo( final Formatter formatter, final GM_Position pos )
  {
    try
    {
      final Double value = getValue( pos );
      if( value == null )
        return;
      formatter.format( getFormatString(), LANDUSE_CLASSES_MAP.get( value ) );
    }
    catch( Exception e )
    {
      e.printStackTrace();
      formatter.format( Messages.getString("org.kalypso.risk.plugin.RasterizedLanduseThemeInfo.1"), e.toString() ); //$NON-NLS-1$
    }
  }

}