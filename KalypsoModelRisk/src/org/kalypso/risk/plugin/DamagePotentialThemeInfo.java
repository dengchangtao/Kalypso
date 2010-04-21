package org.kalypso.risk.plugin;

import java.util.Formatter;
import java.util.Properties;

import org.kalypso.gml.ui.map.CoverageThemeInfo;
import org.kalypso.ogc.gml.IKalypsoThemeInfo;
import org.kalypso.risk.i18n.Messages;
import org.kalypsodeegree.model.geometry.GM_Position;

public class DamagePotentialThemeInfo extends CoverageThemeInfo implements IKalypsoThemeInfo
{
  /**
   * @see org.kalypso.gml.ui.map.CoverageThemeInfo#initFormatString(java.util.Properties)
   */
  @Override
  protected String initFormatString( final Properties props )
  {
    final int digits = KalypsoRiskPlugin.getPreferences_themeInfoPrecision();
    return props.getProperty( PROP_FORMAT, new StringBuffer( "%,." ).append( digits ).append( "f \u20ac/m\u00b2" ).toString() ); //$NON-NLS-1$ //$NON-NLS-2$
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
      formatter.format( getFormatString(), Math.abs( value ) );
    }
    catch( Exception e )
    {
      e.printStackTrace();
      formatter.format( Messages.getString( "org.kalypso.risk.plugin.RiskZonesThemeInfo.1" ), e.toString() ); //$NON-NLS-1$
    }
  }

}
