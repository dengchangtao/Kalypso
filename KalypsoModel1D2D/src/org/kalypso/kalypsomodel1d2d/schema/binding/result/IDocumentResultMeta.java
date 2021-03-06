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
package org.kalypso.kalypsomodel1d2d.schema.binding.result;

import java.math.BigDecimal;

import javax.xml.namespace.QName;

import org.kalypso.kalypsomodel1d2d.i18n.Messages;
import org.kalypso.kalypsomodel1d2d.schema.UrlCatalog1D2D;
import org.kalypso.kalypsomodel1d2d.sim.NodeResultMinMaxCatcher;
import org.kalypso.kalypsosimulationmodel.core.resultmeta.IResultMeta;

/**
 * @author Thomas Jung
 * 
 */
public interface IDocumentResultMeta extends IResultMeta
{
  enum DOCUMENTTYPE
  {
    nodes(Messages.getString( "org.kalypso.kalypsomodel1d2d.schema.binding.result.IDocumentResultMeta.0" )), //$NON-NLS-1$
    tinTerrain(Messages.getString( "org.kalypso.kalypsomodel1d2d.schema.binding.result.IDocumentResultMeta.1" )), //$NON-NLS-1$
    tinWsp(Messages.getString( "org.kalypso.kalypsomodel1d2d.schema.binding.result.IDocumentResultMeta.2" )), //$NON-NLS-1$
    tinVelo(Messages.getString( "org.kalypso.kalypsomodel1d2d.schema.binding.result.IDocumentResultMeta.3" )), //$NON-NLS-1$
    tinDepth(Messages.getString( "org.kalypso.kalypsomodel1d2d.schema.binding.result.IDocumentResultMeta.4" )), //$NON-NLS-1$
    tinShearStress(Messages.getString( "org.kalypso.kalypsomodel1d2d.schema.binding.result.IDocumentResultMeta.5" )), //$NON-NLS-1$
    hydrograph(Messages.getString( "org.kalypso.kalypsomodel1d2d.schema.binding.result.IDocumentResultMeta.6" )), //$NON-NLS-1$
    lengthSection(Messages.getString( "org.kalypso.kalypsomodel1d2d.schema.binding.result.IDocumentResultMeta.7" )), //$NON-NLS-1$
    tinDifference(Messages.getString( "org.kalypso.kalypsomodel1d2d.schema.binding.result.IDocumentResultMeta.8" )), //$NON-NLS-1$
    log(Messages.getString( "org.kalypso.kalypsomodel1d2d.schema.binding.result.IDocumentResultMeta.9" )), //$NON-NLS-1$
    coreDataZip(Messages.getString( "org.kalypso.kalypsomodel1d2d.schema.binding.result.IDocumentResultMeta.10" )); //$NON-NLS-1$

    private final String m_label;

    private DOCUMENTTYPE( final String label )
    {
      m_label = label;
    }

    @Override
    public final String toString( )
    {
      return m_label;
    }
  }

  public static final QName QNAME = new QName( UrlCatalog1D2D.MODEL_1D2DResult_NS, "DocumentResultMeta" ); //$NON-NLS-1$

  public static final QName QNAME_PROP_DOCUMENT_TYPE = new QName( UrlCatalog1D2D.MODEL_1D2DResult_NS, "type" ); //$NON-NLS-1$

  public static final QName QNAME_PROP_DOCUMENT_MIN_VALUE = new QName( UrlCatalog1D2D.MODEL_1D2DResult_NS, "minValue" ); //$NON-NLS-1$

  public static final QName QNAME_PROP_DOCUMENT_MAX_VALUE = new QName( UrlCatalog1D2D.MODEL_1D2DResult_NS, "maxValue" ); //$NON-NLS-1$

  public static final QName QNAME_PROP_DOCUMENT_MIN_VALUE_VELO = new QName( UrlCatalog1D2D.MODEL_1D2DResult_NS, "minValueVelo" ); //$NON-NLS-1$

  public static final QName QNAME_PROP_DOCUMENT_MAX_VALUE_VELO = new QName( UrlCatalog1D2D.MODEL_1D2DResult_NS, "maxValueVelo" ); //$NON-NLS-1$

  public static final QName QNAME_PROP_DOCUMENT_MIN_VALUE_DEPTH = new QName( UrlCatalog1D2D.MODEL_1D2DResult_NS, "minValueDepth" ); //$NON-NLS-1$

  public static final QName QNAME_PROP_DOCUMENT_MAX_VALUE_DEPTH = new QName( UrlCatalog1D2D.MODEL_1D2DResult_NS, "maxValueDepth" ); //$NON-NLS-1$

  public static final QName QNAME_PROP_DOCUMENT_MIN_VALUE_WATERLEVEL = new QName( UrlCatalog1D2D.MODEL_1D2DResult_NS, "minValueWaterlevel" ); //$NON-NLS-1$

  public static final QName QNAME_PROP_DOCUMENT_MAX_VALUE_WATERLEVEL = new QName( UrlCatalog1D2D.MODEL_1D2DResult_NS, "maxValueWaterlevel" ); //$NON-NLS-1$

  public static final QName QNAME_PROP_DOCUMENT_MIN_VALUE_WAVE_HSIG = new QName( UrlCatalog1D2D.MODEL_1D2DResult_NS, "minValueWaveHsig" ); //$NON-NLS-1$

  public static final QName QNAME_PROP_DOCUMENT_MAX_VALUE_WAVE_HSIG = new QName( UrlCatalog1D2D.MODEL_1D2DResult_NS, "maxValueWaveHsig" ); //$NON-NLS-1$

  public static final QName QNAME_PROP_DOCUMENT_MIN_VALUE_WAVE_PER = new QName( UrlCatalog1D2D.MODEL_1D2DResult_NS, "minValueWavePeriod" ); //$NON-NLS-1$

  public static final QName QNAME_PROP_DOCUMENT_MAX_VALUE_WAVE_PER = new QName( UrlCatalog1D2D.MODEL_1D2DResult_NS, "maxValueWavePeriod" ); //$NON-NLS-1$

  public DOCUMENTTYPE getDocumentType( );

  public void setDocumentType( DOCUMENTTYPE documentType );

  public BigDecimal getMinValue( );

  public BigDecimal getMaxValue( );

  public void setMinValue( BigDecimal minValue );

  public void setMaxValue( BigDecimal maxValue );

  public void setMinMaxValues( final NodeResultMinMaxCatcher minMaxCatcher );

  public BigDecimal getMinValueVelo( );

  public BigDecimal getMinValueDepth( );

  public BigDecimal getMinValueWaterlevel( );

  public BigDecimal getMinValueWaveHsig( );

  public BigDecimal getMinValueWavePer( );

  public BigDecimal getMaxValueVelo( );

  public BigDecimal getMaxValueDepth( );

  public BigDecimal getMaxValueWaterlevel( );

  public BigDecimal getMaxValueWaveHsig( );

  public BigDecimal getMaxValueWavePer( );

  /**
   * returns the {@link BigDecimal} max value from this result document for given type of result, the type string should
   * be one of the node result types provided by {@link org.kalypso.kalypsomodel1d2d.conv.results.NodeResultHelper} on
   * error returns null
   */
  public BigDecimal getMaxValueForType( String type );

  /**
   * returns the {@link BigDecimal} min value from this result document for given type of result, the type string should
   * be one of the node result types provided by {@link org.kalypso.kalypsomodel1d2d.conv.results.NodeResultHelper} on
   * error returns null
   */
  public BigDecimal getMinValueForType( String type );
}