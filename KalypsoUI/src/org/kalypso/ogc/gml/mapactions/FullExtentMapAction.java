package org.kalypso.ogc.gml.mapactions;

import org.deegree.model.geometry.GM_Envelope;
import org.kalypso.ogc.IMapModell;
import org.kalypso.ogc.MapPanel;
import org.kalypso.ogc.widgets.ChangeExtentCommand;
import org.kalypso.plugin.ImageProvider;
import org.kalypso.util.command.ICommand;
import org.kalypso.util.command.ICommandTarget;

/**
 * @author belger
 */
public class FullExtentMapAction extends AbstractCommandAction
{
  public FullExtentMapAction( final ICommandTarget commandTarget, final MapPanel mapPanel )
  {
    super( commandTarget, mapPanel, null, ImageProvider.IMAGE_MAPVIEW_FULLEXTENT, "Maximalen Ausschnitt darstellen" );
  }

  /**
   * @see org.eclipse.jface.action.Action#run()
   */
  public ICommand runInternal()
  {
    final MapPanel mapPanel = getMapPanel();
    final IMapModell modell = mapPanel.getMapModell();

    final GM_Envelope fullExtent = modell.getFullExtentBoundingBox();
    return new ChangeExtentCommand( mapPanel, fullExtent );
  }
}
