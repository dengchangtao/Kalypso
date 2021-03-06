/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 *
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  Denickestraße 22
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

package org.kalypso.model.hydrology.internal.preprocessing.writer;

import java.io.PrintWriter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.ArrayUtils;
import org.kalypso.model.hydrology.binding.parameter.DRWBMSoilLayerParameter;
import org.kalypso.model.hydrology.binding.parameter.DRWBMSoiltype;
import org.kalypso.model.hydrology.binding.parameter.Parameter;
import org.kalypso.model.hydrology.binding.parameter.SoilLayerParameter;
import org.kalypso.model.hydrology.binding.parameter.Soiltype;
import org.kalypso.model.hydrology.internal.i18n.Messages;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree.model.feature.IFeatureBindingCollection;

/**
 * @author huebsch
 */
public class BodentypWriter extends AbstractCoreFileWriter
{
  private final GMLWorkspace m_parameterWorkspace;

  public BodentypWriter( final GMLWorkspace parameterWorkspace, final Logger logger )
  {
    super( logger );

    m_parameterWorkspace = parameterWorkspace;
  }

  @Override
  protected void writeContent( final PrintWriter buffer )
  {
    buffer.append( "/Bodentypen:\n/\n/Typ       Tiefe[dm]\n" ); //$NON-NLS-1$

    final Parameter parameter = (Parameter) m_parameterWorkspace.getRootFeature();

    // write normal soil types
    doWrite( parameter.getSoiltypes(), buffer );

    // write DRWBM soil types
    doWriteDRWBM( parameter.getDRWBMSoiltypes(), buffer );
  }

  private void doWrite( final IFeatureBindingCollection<Soiltype> soiltypes, final PrintWriter buffer )
  {
    for( final Soiltype soiltype : soiltypes )
    {
      final IFeatureBindingCollection<SoilLayerParameter> layerList = soiltype.getParameters();

      final ValidSoilParametersVisitor<SoilLayerParameter> visitor = new ValidSoilParametersVisitor<>();
      layerList.accept( visitor );

      final String soiltypeName = soiltype.getName();

      reportInvalidParameters( soiltypeName, visitor );

      final SoilLayerParameter[] validParameters = visitor.getValidParameters();
      writeSoilType( buffer, soiltypeName, validParameters );
    }
  }

  private void reportInvalidParameters( final String soilType, final ValidSoilParametersVisitor< ? extends SoilLayerParameter> visitor )
  {
    final SoilLayerParameter[] invalidParameters = visitor.getInvalidParameters();
    for( final SoilLayerParameter parameter : invalidParameters )
    {
      Logger.getAnonymousLogger().log( Level.WARNING, Messages.getString( "org.kalypso.convert.namodel.manager.BodentypManager.29", soilType, parameter.getName() ) ); //$NON-NLS-1$
    }
  }

  private void doWriteDRWBM( final IFeatureBindingCollection<DRWBMSoiltype> soiltypes, final PrintWriter buffer )
  {
    for( final DRWBMSoiltype soiltype : soiltypes )
    {
      final IFeatureBindingCollection<DRWBMSoilLayerParameter> layerList = soiltype.getParameters();
      final ValidSoilParametersVisitor<DRWBMSoilLayerParameter> visitor = new ValidSoilParametersVisitor<>();
      layerList.accept( visitor );

      final String soiltypeName = soiltype.getName();

      reportInvalidParameters( soiltypeName, visitor );

      final SoilLayerParameter[] validParameters = visitor.getValidParameters();

      writeSoilType( buffer, soiltypeName, validParameters );
    }
  }

  private void writeSoilType( final PrintWriter buffer, final String soiltype, final SoilLayerParameter[] parameters )
  {
    buffer.format( Locale.US, "%-10s%4d%n", soiltype, ArrayUtils.getLength( parameters ) ); //$NON-NLS-1$

    for( final SoilLayerParameter parameter : parameters )
    {
      // basic soil type parameters
      final String layerName = parameter.getLinkedSoilLayer().getName();
      buffer.format( Locale.US, "%-8s%.1f %.1f", layerName, parameter.getThickness(), parameter.isInterflowFloat() ); //$NON-NLS-1$

      // additional drwbm soil type parameters
      if( parameter instanceof DRWBMSoilLayerParameter )
      {
        final DRWBMSoilLayerParameter drwbmParam = (DRWBMSoilLayerParameter) parameter;
        buffer.append( String.format( Locale.US, " %.1f %.1f %.1f %.1f %.1f %.1f %.1f %.1f", drwbmParam.getPipeDiameter(), drwbmParam.getPipeRoughness(), drwbmParam.getDrainagePipeKfValue(), drwbmParam.getDrainagePipeSlope(), drwbmParam.getOverflowHeight(), drwbmParam.getAreaPerOutlet(), drwbmParam.getWidthOfArea() ) ); //$NON-NLS-1$
      }

      // line end
      buffer.format( "%n" ); //$NON-NLS-1$
    }
  }
}