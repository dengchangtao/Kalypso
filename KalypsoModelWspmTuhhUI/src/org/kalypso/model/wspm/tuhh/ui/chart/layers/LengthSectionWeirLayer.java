package org.kalypso.model.wspm.tuhh.ui.chart.layers;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.kalypso.chart.ext.observation.data.TupleResultDomainValueData;
import org.kalypso.chart.ext.observation.layer.TupleResultLineLayer;
import org.kalypso.contribs.eclipse.swt.graphics.RectangleUtils;
import org.kalypso.model.wspm.core.IWspmConstants;
import org.kalypso.model.wspm.core.profil.util.ProfilUtil;
import org.kalypso.observation.result.ComponentUtilities;
import org.kalypso.observation.result.IRecord;
import org.kalypso.observation.result.TupleResult;

import de.openali.odysseus.chart.framework.model.figure.impl.FullRectangleFigure;
import de.openali.odysseus.chart.framework.model.layer.EditInfo;
import de.openali.odysseus.chart.framework.model.layer.ILayerProvider;
import de.openali.odysseus.chart.framework.model.style.IPointStyle;
import de.openali.odysseus.chart.framework.model.style.IStyleSet;
import de.openali.odysseus.chart.framework.model.style.impl.AreaStyle;
import de.openali.odysseus.chart.framework.model.style.impl.ColorFill;

public class LengthSectionWeirLayer extends TupleResultLineLayer
{
  public LengthSectionWeirLayer( final ILayerProvider provider, final TupleResultDomainValueData< ? , ? > data, final IStyleSet styleSet )
  {
    super( provider, data, styleSet );

  }

  @Override
  public EditInfo getHover( final Point pos )
  {
    final TupleResultDomainValueData< ? , ? > valueData = getValueData();
    if( !isVisible() )
      return null;
    for( int i = 0; i < valueData.getDomainValues().length; i++ )
    {
      final Rectangle hover = getScreenRect( i );
      if( hover != null && hover.contains( pos ) )
        return new EditInfo( this, null, null, i, getTooltip( i ), RectangleUtils.getCenterPoint( hover ) );
    }
    return null;
  }

  @Override
  protected final String getTooltip( final int index )
  {
    final TupleResultDomainValueData< ? , ? > valueData = getValueData();
    final TupleResult tr = valueData.getObservation().getResult();
    final IRecord rec = tr.get( index );
    final int targetOKComponentIndex = tr.indexOfComponent( IWspmConstants.LENGTH_SECTION_PROPERTY_WEIR_OK );
    final int commentIndex = tr.indexOfComponent( IWspmConstants.LENGTH_SECTION_PROPERTY_TEXT );
    final String targetOKComponentLabel = ComponentUtilities.getComponentLabel( tr.getComponent( targetOKComponentIndex ) );
    final Double ok = ProfilUtil.getDoubleValueFor( targetOKComponentIndex, rec );
    if( commentIndex < 0 )
      return String.format( "%-12s %.4f", new Object[] { targetOKComponentLabel, ok } );//$NON-NLS-1$
    return String.format( "%-12s %.4f%n%s", new Object[] { targetOKComponentLabel, ok, tr.get( index ).getValue( commentIndex ) } );//$NON-NLS-1$

  }

  @Override
  public String getTitle( )
  {
    final String title = super.getTitle();

    final int index = title.indexOf( '(' ); //$NON-NLS-1$ // remove '(Oberkante)' from String
    if( index > 0 )
      return StringUtils.chomp( title.substring( 0, index - 1 ) );

    return title;
  }

  @Override
  public void paint( final GC gc )
  {
    final TupleResultDomainValueData< ? , ? > valueData = getValueData();
    if( valueData == null )
      return;
    valueData.open();

    final IPointStyle ps = getPointStyle();

    final FullRectangleFigure rf = new FullRectangleFigure();
    rf.setStyle( new AreaStyle( new ColorFill( ps.getInlineColor() ), ps.getAlpha(), ps.getStroke(), true ) );
    for( int i = 0; i < valueData.getObservation().getResult().size(); i++ )
    {
      final Rectangle rect = getScreenRect( i );
      if( rect != null )
      {
        rf.setRectangle( rect );
        rf.paint( gc );
      }
    }
  }

  private Rectangle getScreenRect( final int i )
  {
    final TupleResultDomainValueData< ? , ? > valueData = getValueData();
    final TupleResult result = valueData.getObservation().getResult();
    final IRecord record = result.get( i );
    final Double oK = ProfilUtil.getDoubleValueFor( IWspmConstants.LENGTH_SECTION_PROPERTY_WEIR_OK, record );
    final Double sT = ProfilUtil.getDoubleValueFor( IWspmConstants.LENGTH_SECTION_PROPERTY_STATION, record );
    final Double uK = ProfilUtil.getDoubleValueFor( IWspmConstants.LENGTH_SECTION_PROPERTY_GROUND, record );
    if( uK.isNaN() || oK.isNaN() || sT.isNaN() || getCoordinateMapper() == null )
      return null;
    final Point pOK = getCoordinateMapper().numericToScreen( sT, oK );
    final Point pUK = getCoordinateMapper().numericToScreen( sT, uK );
    return new Rectangle( pOK.x - 1, pOK.y, 3, pUK.y - pOK.y );
  }

}
