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
package org.kalypso.ui.rrm.internal.calccase;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.kalypso.contribs.eclipse.core.resources.ResourceUtilities;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.contribs.eclipse.ui.progress.ProgressUtilities;
import org.kalypso.contribs.java.util.CalendarUtilities;
import org.kalypso.contribs.java.util.logging.SystemOutLogger;
import org.kalypso.gmlschema.GMLSchemaException;
import org.kalypso.gmlschema.GMLSchemaUtilities;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypso.model.hydrology.binding.control.NAControl;
import org.kalypso.model.hydrology.binding.model.Catchment;
import org.kalypso.model.hydrology.binding.model.NaModell;
import org.kalypso.model.hydrology.project.INaCalcCaseConstants;
import org.kalypso.model.hydrology.project.INaProjectConstants;
import org.kalypso.model.rcm.IRainfallModelProvider;
import org.kalypso.model.rcm.RainfallGenerationOperation;
import org.kalypso.model.rcm.binding.ICatchment;
import org.kalypso.model.rcm.binding.IFactorizedTimeseries;
import org.kalypso.model.rcm.binding.ILinearSumGenerator;
import org.kalypso.model.rcm.binding.IRainfallCatchmentModel;
import org.kalypso.model.rcm.binding.IRainfallGenerator;
import org.kalypso.model.rcm.binding.ITarget;
import org.kalypso.model.rcm.util.PlainRainfallModelProvider;
import org.kalypso.ogc.gml.serialize.GmlSerializer;
import org.kalypso.ogc.sensor.DateRange;
import org.kalypso.ogc.sensor.metadata.ITimeseriesConstants;
import org.kalypso.ogc.sensor.metadata.MetadataList;
import org.kalypso.ogc.sensor.timeseries.TimeseriesUtils;
import org.kalypso.ogc.sensor.timeseries.merged.Source;
import org.kalypso.ogc.sensor.util.ZmlLink;
import org.kalypso.simulation.core.ant.copyobservation.CopyObservationFeatureVisitor;
import org.kalypso.simulation.core.ant.copyobservation.ICopyObservationSource;
import org.kalypso.simulation.core.ant.copyobservation.source.FeatureCopyObservationSource;
import org.kalypso.simulation.core.ant.copyobservation.target.CopyObservationTargetFactory;
import org.kalypso.simulation.core.ant.copyobservation.target.ICopyObservationTarget;
import org.kalypso.ui.rrm.internal.KalypsoUIRRMPlugin;
import org.kalypso.zml.obslink.TimeseriesLinkType;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureList;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree.model.feature.IFeatureBindingCollection;
import org.kalypsodeegree_impl.model.feature.FeatureFactory;
import org.kalypsodeegree_impl.model.feature.XLinkedFeature_Impl;
import org.kalypsodeegree_impl.model.feature.gmlxpath.GMLXPath;
import org.kalypsodeegree_impl.model.feature.visitors.MonitorFeatureVisitor;

/**
 * The worker that actually updates a simulation.
 * 
 * @author Gernot Belger
 */
public class UpdateSimulationWorker
{
  public static final QName MAPPING_MEMBER = new QName( "http://org.kalypso.updateObservationMapping", "mappingMember" );

  private final IFolder m_simulationFolder;

  public UpdateSimulationWorker( final IFolder simulationFolder )
  {
    m_simulationFolder = simulationFolder;
  }

  /**
   * <pre>
   *     <target name="updateOmbrometer" depends="setProperties">
   *         <!-- updateOmbrometer: ombrometer von Datacenter (oder lokal)-abholen -->
   *         <echo message="aktualisiere Ombrometer Zeitreihen im Rechenfall" />
   *         <delete dir="${calc.dir}/Niederschlag" />
   *         <mkdir dir="${calc.dir}/Niederschlag" />
   *         <kalypso.copyObservation gml="${project.url}/.model/observationConf/ombrometer.gml" featurePath="ombrometerMember" context="${calc.url}" targetobservationdir="${calc.dir}/Niederschlag" >
   *             <source property="NRepository" from="${startsim}" to="${stopsim}" />
   *         </kalypso.copyObservation>
   *     </target>
   * 
   *     <target name="updateObsT" depends="setProperties">
   *         <echo message="aktualisiere Temperaturen (Messung)" />
   *         <delete dir="${calc.dir}/Klima" />
   *         <mkdir dir="${calc.dir}/Klima" />
   *         <kalypso.copyObservation gml="${project.url}/.model/observationConf/ObsTMapping.gml" featurePath="mappingMember" context="${calc.url}" targetobservationdir="${calc.dir}/Klima">
   *             <source property="inObservationLink" from="${startsim}" to="${stopsim}" />
   *         </kalypso.copyObservation>
   *     </target>
   * 
   * </pre>
   */
  public IStatus execute( final IProgressMonitor monitor ) throws CoreException
  {
    monitor.beginTask( "Updating calc case", 120 );

    /* Read models */
    monitor.subTask( "Reading control file..." );
    final NAControl control = readControlFile( m_simulationFolder );
    final NaModell model = readModelFile( m_simulationFolder );
    ProgressUtilities.worked( monitor, 20 );

    final DateRange range = getRange( control );

    /* Copy observations for pegel and zufluss */
    copyPegelTimeseries( m_simulationFolder, range, new SubProgressMonitor( monitor, 20 ) );

    copyZuflussTimeseries( m_simulationFolder, range, new SubProgressMonitor( monitor, 20 ) );

    /* Execute catchment models */
    // FIXME: progress monitor
    executeCatchmentModel( m_simulationFolder, control, model, control.getGeneratorN(), Catchment.PROP_PRECIPITATION_LINK, range, ITimeseriesConstants.TYPE_RAINFALL, new SubProgressMonitor( monitor, 20 ) );
    executeCatchmentModel( m_simulationFolder, control, model, control.getGeneratorT(), Catchment.PROP_TEMPERATURE_LINK, range, ITimeseriesConstants.TYPE_TEMPERATURE, new SubProgressMonitor( monitor, 20 ) );
    executeCatchmentModel( m_simulationFolder, control, model, control.getGeneratorE(), Catchment.PROP_EVAPORATION_LINK, range, ITimeseriesConstants.TYPE_EVAPORATION, new SubProgressMonitor( monitor, 20 ) );

    return Status.OK_STATUS;
  }

  private DateRange getRange( final NAControl control )
  {
    final Date simulationStart = control.getSimulationStart();
    final Date simulationEnd = control.getSimulationEnd();

    return new DateRange( simulationStart, simulationEnd );
  }

  private NAControl readControlFile( final IFolder calcCaseFolder ) throws CoreException
  {
    try
    {
      final IFile controlFile = calcCaseFolder.getFile( INaCalcCaseConstants.CALCULATION_GML_PATH );
      final GMLWorkspace controlWorkspace = GmlSerializer.createGMLWorkspace( controlFile );
      return (NAControl) controlWorkspace.getRootFeature();
    }
    catch( final Exception e )
    {
      e.printStackTrace();
      final IStatus status = new Status( IStatus.ERROR, KalypsoUIRRMPlugin.getID(), "Failed to read control file", e );
      throw new CoreException( status );
    }
  }

  private NaModell readModelFile( final IFolder calcCaseFolder ) throws CoreException
  {
    try
    {
      final IFile modelFile = calcCaseFolder.getFile( INaProjectConstants.GML_MODELL_PATH );
      final GMLWorkspace modelWorkspace = GmlSerializer.createGMLWorkspace( modelFile );
      return (NaModell) modelWorkspace.getRootFeature();
    }
    catch( final Exception e )
    {
      e.printStackTrace();
      final IStatus status = new Status( IStatus.ERROR, KalypsoUIRRMPlugin.getID(), "Failed to read model file", e );
      throw new CoreException( status );
    }
  }

  private void copyPegelTimeseries( final IFolder calcCaseFolder, final DateRange sourceRange, final IProgressMonitor monitor ) throws CoreException
  {
    try
    {
      /* Read mapping */
      final FeatureList mappingFeatures = readMapping( calcCaseFolder, "ObsQMapping.gml", MAPPING_MEMBER );

      /* Prepare visitor */
      final CopyObservationFeatureVisitor visitor = prepareVisitor( calcCaseFolder, "Pegel", "inObservationLink", sourceRange ); //$NON-NLS-1$ //$NON-NLS-2$

      /* Execute visitor */
      final int count = mappingFeatures.size();
      final MonitorFeatureVisitor wrappedVisitor = new MonitorFeatureVisitor( monitor, count, visitor );
      mappingFeatures.accept( wrappedVisitor );
    }
    catch( final Exception e )
    {
      e.printStackTrace();
      final IStatus status = new Status( IStatus.ERROR, KalypsoUIRRMPlugin.getID(), "Failed to execute pegel mapping", e );
      throw new CoreException( status );
    }
  }

  private void copyZuflussTimeseries( final IFolder calcCaseFolder, final DateRange sourceRange, final IProgressMonitor monitor ) throws CoreException
  {
    try
    {
      /* Read mapping */
      final FeatureList mappingFeatures = readMapping( calcCaseFolder, "ObsQZuMapping.gml", MAPPING_MEMBER );

      /* Prepare visitor */
      final CopyObservationFeatureVisitor visitor = prepareVisitor( calcCaseFolder, "Zufluss", "inObservationLink", sourceRange ); //$NON-NLS-1$ //$NON-NLS-2$

      /* Execute visitor */
      final int count = mappingFeatures.size();
      final MonitorFeatureVisitor wrappedVisitor = new MonitorFeatureVisitor( monitor, count, visitor );
      mappingFeatures.accept( wrappedVisitor );
    }
    catch( final Exception e )
    {
      e.printStackTrace();
      final IStatus status = new Status( IStatus.ERROR, KalypsoUIRRMPlugin.getID(), "Failed to execute zufluss mapping", e );
      throw new CoreException( status );
    }
  }

  private FeatureList readMapping( final IFolder calcCaseFolder, final String filename, final QName memberProperty ) throws Exception
  {
    final IProject project = calcCaseFolder.getProject();
    final IFolder observationConfFolder = project.getFolder( INaProjectConstants.FOLDER_OBSERVATION_CONF );
    final IFile mappingFile = observationConfFolder.getFile( filename );
    final GMLWorkspace mappingWorkspace = GmlSerializer.createGMLWorkspace( mappingFile );
    final Feature rootFeature = mappingWorkspace.getRootFeature();
    return (FeatureList) rootFeature.getProperty( memberProperty );
  }

  private CopyObservationFeatureVisitor prepareVisitor( final IFolder calcCaseFolder, final String outputDir, final String sourceLinkName, final DateRange sourceRange )
  {
    final DateRange forecastRange = null;

    final Source source = new Source();
    source.setProperty( sourceLinkName );
    source.setFrom( Long.toString( sourceRange.getFrom().getTime() ) );
    source.setTo( Long.toString( sourceRange.getTo().getTime() ) );

    // REMARK: must be null, so the special NA-ObservationTarget is created
    final GMLXPath targetPath = null;

    final File calcDir = calcCaseFolder.getLocation().toFile();
    final File targetObservationDir = new File( calcDir, outputDir );

    final URL context = ResourceUtilities.createQuietURL( calcCaseFolder );
    final String tokens = StringUtils.EMPTY;

    final ICopyObservationTarget obsTarget = CopyObservationTargetFactory.getLink( context, targetPath, targetObservationDir, sourceRange, forecastRange );
    final ICopyObservationSource obsSource = new FeatureCopyObservationSource( context, new Source[] { source }, tokens );

    final MetadataList metadata = new MetadataList();

    final SystemOutLogger logger = new SystemOutLogger();

    return new CopyObservationFeatureVisitor( obsSource, obsTarget, metadata, logger );
  }

  private void executeCatchmentModel( final IFolder calcCaseFolder, final NAControl control, final NaModell model, final IRainfallGenerator generator, final QName targetLink, final DateRange range, final String parameterType, final IProgressMonitor monitor ) throws CoreException
  {
    try
    {
      monitor.beginTask( "Apply catchment model", 100 );
      final String name = TimeseriesUtils.getName( parameterType );
      monitor.subTask( name );

      /* Initialize the generator. */
      initGenerator( control, generator, range, parameterType );

      /* Initialize the catchment target links. */
      initTargetLinks( calcCaseFolder, generator, targetLink, parameterType );

      /* Create the rainfall generation operation. */
      final IRainfallCatchmentModel rainfallModel = createRainfallModel( calcCaseFolder, model, generator, targetLink, range );
      final IRainfallModelProvider modelProvider = new PlainRainfallModelProvider( rainfallModel );
      final RainfallGenerationOperation operation = new RainfallGenerationOperation( modelProvider, null );

      /* Execute the rainfall generation operation. */
      operation.execute( new SubProgressMonitor( monitor, 100 ) );
    }
    catch( final Exception e )
    {
      e.printStackTrace();

      final IStatus status = StatusUtilities.statusFromThrowable( e, "Failed to execute catchment model" );
      throw new CoreException( status );
    }
    finally
    {
      monitor.done();
    }
  }

  private void initGenerator( final NAControl control, final IRainfallGenerator generator, final DateRange range, final String parameterType )
  {
    /* The timestep is only defined in linear sum generators for now. */
    if( !(generator instanceof ILinearSumGenerator) )
      throw new NotImplementedException( "Only ILinearSumGenerator's are supported at the moment..." );

    /* Cast. */
    final ILinearSumGenerator gen = (ILinearSumGenerator) generator;

    /* Set the period. */
    gen.setPeriod( range );

    /* If a timestep in the generator is set, this one is used. */
    Integer timestep = gen.getTimestep();
    if( timestep == null )
      timestep = control.getMinutesOfTimestep();

    /* Get the calendar field and the amount. */
    final String calendarField = CalendarUtilities.getName( Calendar.MINUTE );
    final int amount = timestep.intValue();

    /* Add a source filter. */
    if( ITimeseriesConstants.TYPE_TEMPERATURE.equals( parameterType ) )
      gen.addInterpolationFilter( calendarField, amount, true, "0.0", 0 ); //$NON-NLS-1$
    else
      gen.addIntervalFilter( calendarField, amount, 0.0, 0 );
  }

  private void initTargetLinks( final IFolder calcCaseFolder, final IRainfallGenerator generator, final QName targetLink, final String parameterType ) throws Exception
  {
    /* The optimization only works for linear sum generators for now. */
    if( !(generator instanceof ILinearSumGenerator) )
      throw new NotImplementedException( "Only ILinearSumGenerator's are supported at the moment..." );

    initOptimizedTargetLinks( calcCaseFolder, generator, targetLink, parameterType );
  }

  private void initOptimizedTargetLinks( final IFolder calcCaseFolder, final IRainfallGenerator generator, final QName targetLink, final String parameterType ) throws Exception
  {
    /* Cast. */
    final ILinearSumGenerator linearSumGenerator = (ILinearSumGenerator) generator;

    /* Hash for the already created links. */
    final Map<String, String> linkHash = new HashMap<String, String>();

    /* The workspace to save. */
    GMLWorkspace workspaceToSave = null;

    /* Get the catchments. */
    final List<ICatchment> generatorCatchments = linearSumGenerator.getCatchments();
    for( final ICatchment generatorCatchment : generatorCatchments )
    {
      /* Get the area. */
      final XLinkedFeature_Impl areaLink = (XLinkedFeature_Impl) generatorCatchment.getAreaLink();
      final Catchment catchment = (Catchment) areaLink.getFeature();

      /* Find the workspace to save. */
      if( workspaceToSave == null )
        workspaceToSave = catchment.getWorkspace();

      /* Build the hash. */
      final String hash = buildHash( generatorCatchment );
      if( hash == null || hash.length() == 0 )
        continue;

      /* If the link hash contains this hash code, the corresponding link will be used. */
      if( linkHash.containsKey( hash ) )
      {
        /* Create the link. */
        final String link = linkHash.get( hash );

        /* Create the timeseries link type. */
        final TimeseriesLinkType tsLink = new TimeseriesLinkType();
        tsLink.setHref( link );

        /* Set the property. */
        catchment.setProperty( targetLink, tsLink );
        continue;
      }

      /* Otherwise create a new link. */
      final String link = buildLink( parameterType, catchment );

      /* Create the timeseries link type. */
      final TimeseriesLinkType tsLink = new TimeseriesLinkType();
      tsLink.setHref( link );

      /* Set the property. */
      catchment.setProperty( targetLink, tsLink );

      /* Store the hash code. */
      linkHash.put( hash, link );
    }

    /* Save the workspace, because it is reloaded in the rainfall operation. */
    /* HINT: This is the linked workspace of the modell.gml, not the loaded one here. */
    final IFile modelFile = calcCaseFolder.getFile( INaProjectConstants.GML_MODELL_PATH );
    GmlSerializer.saveWorkspace( workspaceToSave, modelFile );
  }

  private String buildHash( final ICatchment catchment )
  {
    /* Memory for the single values. */
    final List<String> values = new ArrayList<String>();

    /* Build the hash. */
    final IFeatureBindingCollection<IFactorizedTimeseries> factorizedTimeseries = catchment.getFactorizedTimeseries();
    for( final IFactorizedTimeseries timeseries : factorizedTimeseries )
    {
      final BigDecimal factor = timeseries.getFactor();
      final ZmlLink link = timeseries.getTimeseriesLink();
      values.add( String.format( Locale.PRC, "%d_%s", factor.intValue(), link.getHref() ) );
    }

    /* Join the values. */
    final String joinedValues = StringUtils.join( values.toArray( new String[] {} ), ";" );

    return joinedValues;
  }

  private String buildLink( final String parameterType, final Catchment catchment ) throws UnsupportedEncodingException
  {
    final String name = TimeseriesUtils.getName( parameterType );
    final String path = String.format( "../ZR_%s/%s.zml", name, URLEncoder.encode( catchment.getName(), "UTF-8" ) ); //$NON-NLS-1$
    return path;
  }

  private IRainfallCatchmentModel createRainfallModel( final IFolder calcCaseFolder, final NaModell model, final IRainfallGenerator generator, final QName targetLink, final DateRange targetRange ) throws GMLSchemaException
  {
    /* Rainfall model. */
    final IFolder folder = calcCaseFolder.getFolder( INaProjectConstants.FOLDER_MODELS );
    final URL context = ResourceUtilities.createQuietURL( folder );
    final GMLWorkspace modelWorkspace = FeatureFactory.createGMLWorkspace( IRainfallCatchmentModel.FEATURE_FAINFALL_CATCHMENT_MODEL, context, GmlSerializer.DEFAULT_FACTORY );
    final IRainfallCatchmentModel rainfallModel = (IRainfallCatchmentModel) modelWorkspace.getRootFeature();
    rainfallModel.getGenerators().add( generator );

    /* Create a target. */
    final IRelationType parentRelation = (IRelationType) rainfallModel.getFeatureType().getProperty( IRainfallCatchmentModel.PROPERTY_TARGET_MEMBER );
    final IFeatureType type = GMLSchemaUtilities.getFeatureTypeQuiet( ITarget.FEATURE_TARGET );
    final ITarget target = (ITarget) modelWorkspace.createFeature( rainfallModel, parentRelation, type );
    target.setCatchmentPath( "CatchmentCollectionMember/catchmentMember" ); //$NON-NLS-1$

    /* Set the target. */
    rainfallModel.setTarget( target );

    /* Create the link to the catchment. */
    final IRelationType catchmentLinkRelation = (IRelationType) target.getFeatureType().getProperty( ITarget.PROPERTY_CATCHMENT_COLLECTION );
    final IFeatureType catchmentLinkType = GMLSchemaUtilities.getFeatureTypeQuiet( Feature.QNAME_FEATURE );
    final String catchmentLinkRef = "modell.gml#" + model.getId(); //$NON-NLS-1$
    final XLinkedFeature_Impl catchmentXLink = new XLinkedFeature_Impl( target, catchmentLinkRelation, catchmentLinkType, catchmentLinkRef );
    target.setCatchmentFeature( catchmentXLink );

    /* Target range. */
    target.setPeriod( targetRange );

    /* Observation path. */
    target.setObservationPath( targetLink.getLocalPart() );

    return rainfallModel;
  }
}
