package org.kalypso.ogc.sensor.jface;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Control;
import org.kalypso.eclipse.core.resources.IProjectProvider;
import org.kalypso.eclipse.jface.viewers.AbstractFeatureCellEditor;
import org.kalypso.eclipse.jface.viewers.DefaultCellValidators;
import org.kalypso.eclipse.jface.viewers.DialogCellEditor;
import org.kalypso.ogc.gml.KalypsoFeature;
import org.kalypso.zml.obslink.TimeseriesLink;


/**
 * @author Belger
 */
public class ObservationFeatureCellEditor extends AbstractFeatureCellEditor implements IProjectProvider
{
  public ObservationFeatureCellEditor( )
  {
    setCellEditor( new ObservationDialogEditor( this ) );
    setValidator( DefaultCellValidators.DOUBLE_VALIDATOR );
  }

  /**
   * @see org.kalypso.eclipse.jface.viewers.AbstractFeatureCellEditor#doGetValues()
   */
  protected Map doGetValues()
  {
    final Object value = getEditor().getValue();

    final Map map = new HashMap();
    map.put( getPropertyName(), value );

    return map;
  }

  /**
   * @see org.kalypso.eclipse.jface.viewers.AbstractFeatureCellEditor#doSetFeature(org.kalypso.ogc.gml.KalypsoFeature)
   */
  protected void doSetFeature( final KalypsoFeature feature )
  {
    final Object value = feature.getProperty( getPropertyName() );
    getEditor().setValue( value );
  }

  /**
   * @see org.kalypso.eclipse.jface.viewers.AbstractFeatureCellEditor#renderLabel(org.kalypso.ogc.gml.KalypsoFeature)
   */
  public String renderLabel( KalypsoFeature feature )
  {
    final TimeseriesLink link = (TimeseriesLink)feature.getProperty( getPropertyName() );
    return link == null ? "<kein Wert>" : "Zeitreihe: " + link.getHref();
  }
  
  private final static class ObservationDialogEditor extends DialogCellEditor
  {
    private final IProjectProvider m_pp;

    public ObservationDialogEditor( final IProjectProvider pp )
    {
     m_pp = pp; 
    }
    
    /**
     * @see org.kalypso.eclipse.jface.viewers.DialogCellEditor#openDialog(org.eclipse.swt.widgets.Control)
     */
    protected boolean openDialog( final Control control )
    {
      final TimeseriesLink obslink = (TimeseriesLink)doGetValue();
      
      final ObservationLinkDialog dialog = new ObservationLinkDialog( control.getShell(), obslink, m_pp );
      if( dialog.open() == Window.OK )
      {
        doSetValue( dialog.getResult() );
        return true;
      }

      return false;
    }
  }

}
