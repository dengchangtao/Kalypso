package org.kalypso.model.wspm.tuhh.ui.chart.layers;

import org.kalypso.chart.ext.observation.TupleResultDomainValueData;
import org.kalypso.chart.ext.observation.TupleResultLineLayer;
import org.kalypso.model.wspm.core.IWspmConstants;
import org.kalypso.model.wspm.core.profil.util.ProfileUtil;
import org.kalypso.observation.result.ComponentUtilities;
import org.kalypso.observation.result.IRecord;
import org.kalypso.observation.result.TupleResult;

import de.openali.odysseus.chart.framework.model.layer.ILayerProvider;
import de.openali.odysseus.chart.framework.model.style.IStyleSet;

public class LengthSectionCommentLayer extends TupleResultLineLayer
{
  public LengthSectionCommentLayer( final ILayerProvider provider, final TupleResultDomainValueData< ? , ? > data, final IStyleSet styleSet )
  {
    super( provider, data, styleSet );
  }

  @Override
  protected String getTooltip( final IRecord record )
  {
    final TupleResult tr = record.getOwner();

    final int soilIndex = tr.indexOfComponent( IWspmConstants.LENGTH_SECTION_PROPERTY_GROUND );
    final int commentIndex = tr.indexOfComponent( IWspmConstants.LENGTH_SECTION_PROPERTY_TEXT );
    final int stationIndex = tr.indexOfComponent( IWspmConstants.LENGTH_SECTION_PROPERTY_STATION );
    final String targetOKComponentLabel = ComponentUtilities.getComponentLabel( tr.getComponent( soilIndex ) );
    final String stationComponentLabel = ComponentUtilities.getComponentLabel( tr.getComponent( stationIndex ) );
    final Double dn = ProfileUtil.getDoubleValueFor( soilIndex, record );
    final Double ds = ProfileUtil.getDoubleValueFor( stationIndex, record );

    // FIXME: better tooltip formatting
//  final TooltipFormatter tooltip = new TooltipFormatter( comment );
//  tooltip.addLine( stationLabel, station.toString() );
//  tooltip.addLine( okLabel, ok.toString() );
//  tooltip.addLine( ukLabel, uk.toString() );
//  tooltip.addLine( widthLabel, bw.toString() );

    if( commentIndex > 0 )
    {
      final Object comment = record.getValue( commentIndex );
      if( comment != null )
        return String.format( "%-12s %.4f%n%-12s %.4f%n%s", stationComponentLabel, ds, targetOKComponentLabel, dn, comment );//$NON-NLS-1$
    }

    return String.format( "%-12s %.4f%n%-12s %.4f", stationComponentLabel, ds, targetOKComponentLabel, dn );//$NON-NLS-1$
  }
}