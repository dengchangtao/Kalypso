/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 *
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  Denickestra�e 22
 *  21073 Hamburg, Germany
 *  http://www.tuhh.de/wb
 *
 *  and
 *
 *  Bjoernsen Consulting Engineers (BCE)
 *  Maria Trost 3
 *  56070 Koblenz, Germany
 *  http://www.bjoernsen.de
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Contact:
 *
 *  E-Mail:
 *  belger@bjoernsen.de
 *  schlienger@bjoernsen.de
 *  v.doemming@tuhh.de
 *
 *  ---------------------------------------------------------------------------*/
package org.kalypso.ui.rrm.internal.results.view.base;

import org.eclipse.jface.resource.ImageDescriptor;
import org.kalypso.ui.rrm.internal.KalypsoUIRRMPlugin;
import org.kalypso.ui.rrm.internal.UIRrmImages;
import org.kalypso.ui.rrm.internal.UIRrmImages.DESCRIPTORS;
import org.kalypso.ui.rrm.internal.i18n.Messages;

/**
 * @author Dirk Kuch
 */
public class KalypsoHydrologyResults
{
  public enum RRM_RESULT_TYPE
  {
    eNode,
    eCatchment,
    eStorage,
    eInputTimeseries;
  }

  public enum RRM_RESULT
  {
    nodeGesamtknotenAbfluss(
        Messages.getString( "KalypsoHydrologyResults_0" ), UIRrmImages.DESCRIPTORS.PARAMETER_TYPE_DISCHARGE, UIRrmImages.DESCRIPTORS.MISSING_PARAMETER_TYPE_DISCHARGE, "Gesamtabfluss.zml", RRM_RESULT_TYPE.eNode), //$NON-NLS-1$//$NON-NLS-2$

    catchmentTemperature(
        Messages.getString( "KalypsoHydrologyResults_1" ), UIRrmImages.DESCRIPTORS.PARAMETER_TYPE_TEMPERATURE, UIRrmImages.DESCRIPTORS.MISSING_PARAMETER_TYPE_TEMPERATURE, "Temperatur.zml", RRM_RESULT_TYPE.eCatchment), //$NON-NLS-1$//$NON-NLS-2$
    catchmentNiederschlag(
        Messages.getString( "KalypsoHydrologyResults_2" ), UIRrmImages.DESCRIPTORS.PARAMETER_TYPE_RAINFALL, UIRrmImages.DESCRIPTORS.MISSING_PARAMETER_TYPE_RAINFALL, "Niederschlag.zml", RRM_RESULT_TYPE.eCatchment), //$NON-NLS-1$//$NON-NLS-2$
    catchmentSchneehoehe(
        Messages.getString( "KalypsoHydrologyResults_3" ), UIRrmImages.DESCRIPTORS.PARAMETER_TYPE_SNOW_HEIGHT, UIRrmImages.DESCRIPTORS.MISSING_PARAMETER_TYPE_SNOW_HEIGHT, null, RRM_RESULT_TYPE.eCatchment), //$NON-NLS-2$ // FIXME //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-1$
    catchmentGesamtTeilgebietsQ(
        Messages.getString( "KalypsoHydrologyResults_4" ), UIRrmImages.DESCRIPTORS.PARAMETER_TYPE_DISCHARGE, UIRrmImages.DESCRIPTORS.MISSING_PARAMETER_TYPE_DISCHARGE, "Gesamtabfluss.zml", RRM_RESULT_TYPE.eCatchment), //$NON-NLS-1$//$NON-NLS-2$
    catchmentOberflaechenQNatuerlich(
        Messages.getString( "KalypsoHydrologyResults_5" ), UIRrmImages.DESCRIPTORS.PARAMETER_TYPE_DISCHARGE, UIRrmImages.DESCRIPTORS.MISSING_PARAMETER_TYPE_DISCHARGE, "Oberflaechenabfluss(natuerlich).zml", RRM_RESULT_TYPE.eCatchment), //$NON-NLS-1$//$NON-NLS-2$
    catchmentOberflaechenQVersiegelt(
        Messages.getString( "KalypsoHydrologyResults_6" ), UIRrmImages.DESCRIPTORS.PARAMETER_TYPE_DISCHARGE, UIRrmImages.DESCRIPTORS.MISSING_PARAMETER_TYPE_DISCHARGE, "Oberflaechenabfluss(versiegelt).zml", RRM_RESULT_TYPE.eCatchment), //$NON-NLS-1$//$NON-NLS-2$
    catchmentInterflow(
        Messages.getString( "KalypsoHydrologyResults_7" ), UIRrmImages.DESCRIPTORS.PARAMETER_TYPE_DISCHARGE, UIRrmImages.DESCRIPTORS.MISSING_PARAMETER_TYPE_DISCHARGE, "Interflow.zml", RRM_RESULT_TYPE.eCatchment), //$NON-NLS-1$//$NON-NLS-2$
    catchmentBasisQ(
        Messages.getString( "KalypsoHydrologyResults_8" ), UIRrmImages.DESCRIPTORS.PARAMETER_TYPE_DISCHARGE, UIRrmImages.DESCRIPTORS.MISSING_PARAMETER_TYPE_DISCHARGE, "Basisabfluss.zml", RRM_RESULT_TYPE.eCatchment), //$NON-NLS-1$//$NON-NLS-2$
    catchmentGrundwasserQ(
        Messages.getString( "KalypsoHydrologyResults_9" ), UIRrmImages.DESCRIPTORS.PARAMETER_TYPE_DISCHARGE, UIRrmImages.DESCRIPTORS.MISSING_PARAMETER_TYPE_DISCHARGE, "Grundwasserabfluss.zml", RRM_RESULT_TYPE.eCatchment), //$NON-NLS-1$//$NON-NLS-2$
    catchmentGrundwasserstand(
        Messages.getString( "KalypsoHydrologyResults_10" ), UIRrmImages.DESCRIPTORS.PARAMETER_TYPE_WATERLEVEL, UIRrmImages.DESCRIPTORS.MISSING_PARAMETER_TYPE_WATERLEVEL, "Grundwasserstand.zml", RRM_RESULT_TYPE.eCatchment), //$NON-NLS-1$//$NON-NLS-2$
    catchmentEvapotranspiration(
        Messages.getString( "KalypsoHydrologyResults_11" ), UIRrmImages.DESCRIPTORS.PARAMETER_TYPE_EVAPORATION, UIRrmImages.DESCRIPTORS.MISSING_PARAMETER_TYPE_EVAPORATION, null, RRM_RESULT_TYPE.eCatchment), //$NON-NLS-2$ // FIXME //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-1$

    storageFuellvolumen(
        Messages.getString( "KalypsoHydrologyResults_12" ), UIRrmImages.DESCRIPTORS.PARAMETER_TYPE_VOLUME, UIRrmImages.DESCRIPTORS.MISSING_PARAMETER_TYPE_VOLUME, "Fuellvolumen.zml", RRM_RESULT_TYPE.eStorage), //$NON-NLS-1$//$NON-NLS-2$
    storageSpeicherUeberlauf(
        Messages.getString( "KalypsoHydrologyResults_13" ), UIRrmImages.DESCRIPTORS.PARAMETER_TYPE_DISCHARGE, UIRrmImages.DESCRIPTORS.MISSING_PARAMETER_TYPE_DISCHARGE, "Speicherueberlauf.zml", RRM_RESULT_TYPE.eStorage), //$NON-NLS-1$//$NON-NLS-2$

    inputEvaporation(
        Messages.getString( "KalypsoHydrologyResults_14" ), UIRrmImages.DESCRIPTORS.PARAMETER_INPUT_TYPE_EVAPORATION, UIRrmImages.DESCRIPTORS.MISSING_PARAMETER_INPUT_TYPE_EVAPORATION, null, RRM_RESULT_TYPE.eInputTimeseries), //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-1$
    inputInflow(
        Messages.getString( "KalypsoHydrologyResults_15" ), UIRrmImages.DESCRIPTORS.PARAMETER_INPUT_TYPE_INFLOW, UIRrmImages.DESCRIPTORS.MISSING_PARAMETER_INPUT_TYPE_INFLOW, null, RRM_RESULT_TYPE.eInputTimeseries), //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-1$
    inputRainfall(
        Messages.getString( "KalypsoHydrologyResults_16" ), UIRrmImages.DESCRIPTORS.PARAMETER_INPUT_TYPE_RAINFALL, UIRrmImages.DESCRIPTORS.MISSING_PARAMETER_INPUT_TYPE_RAINFALL, null, RRM_RESULT_TYPE.eInputTimeseries), //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-1$
    inputSeaEvaporation(
        Messages.getString( "KalypsoHydrologyResults_17" ), UIRrmImages.DESCRIPTORS.PARAMETER_INPUT_TYPE_SEA_EVAPORATION, UIRrmImages.DESCRIPTORS.MISSING_PARAMETER_INPUT_TYPE_SEA_EVAPORATION, null, RRM_RESULT_TYPE.eInputTimeseries), //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-1$
    inputTemperature(
        Messages.getString( "KalypsoHydrologyResults_18" ), UIRrmImages.DESCRIPTORS.PARAMETER_INPUT_TYPE_TEMPERATURE, UIRrmImages.DESCRIPTORS.MISSING_PARAMETER_INPUT_TYPE_TEMPERATURE, null, RRM_RESULT_TYPE.eInputTimeseries); //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-1$

    private final String m_label;

    private final String m_fileName;

    private final DESCRIPTORS m_image;

    private final DESCRIPTORS m_missing;

    private final RRM_RESULT_TYPE m_type;

    RRM_RESULT( final String label, final UIRrmImages.DESCRIPTORS image, final UIRrmImages.DESCRIPTORS missing, final String fileName, final RRM_RESULT_TYPE type )
    {
      m_label = label;
      m_image = image;
      m_missing = missing;
      m_fileName = fileName;
      m_type = type;
    }

    public String getLabel( )
    {
      return m_label;
    }

    public ImageDescriptor getImage( )
    {
      return KalypsoUIRRMPlugin.getDefault().getImageProvider().getImageDescriptor( m_image );
    }

    public ImageDescriptor getMissingImage( )
    {
      return KalypsoUIRRMPlugin.getDefault().getImageProvider().getImageDescriptor( m_missing );
    }

    @Override
    public String toString( )
    {
      return getLabel();
    }

    public String getFileName( )
    {
      return m_fileName;
    }

    public RRM_RESULT_TYPE getType( )
    {
      return m_type;
    }
  }

}