package org.kalypso.risk.model.utils;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deegree.crs.transformations.coordinate.CRSTransformation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.grid.AbstractDelegatingGeoGrid;
import org.kalypso.grid.GeoGridException;
import org.kalypso.grid.GeoGridUtilities;
import org.kalypso.grid.IGeoGrid;
import org.kalypso.kalypsosimulationmodel.utils.SLDHelper;
import org.kalypso.ogc.gml.AbstractCascadingLayerTheme;
import org.kalypso.ogc.gml.CascadingKalypsoTheme;
import org.kalypso.ogc.gml.CascadingThemeHelper;
import org.kalypso.ogc.gml.GisTemplateMapModell;
import org.kalypso.ogc.gml.IKalypsoTheme;
import org.kalypso.risk.Messages;
import org.kalypso.risk.model.actions.dataImport.waterdepth.AsciiRasterInfo;
import org.kalypso.risk.model.schema.binding.IAnnualCoverageCollection;
import org.kalypso.risk.model.schema.binding.ILanduseClass;
import org.kalypso.risk.model.schema.binding.ILandusePolygon;
import org.kalypso.risk.model.schema.binding.IRasterDataModel;
import org.kalypso.risk.model.schema.binding.IRasterizationControlModel;
import org.kalypso.risk.model.schema.binding.IRiskLanduseStatistic;
import org.kalypso.template.types.StyledLayerType;
import org.kalypso.template.types.StyledLayerType.Property;
import org.kalypso.template.types.StyledLayerType.Style;
import org.kalypso.transformation.CachedTransformationFactory;
import org.kalypso.transformation.TransformUtilities;
import org.kalypsodeegree.model.feature.binding.IFeatureWrapperCollection;
import org.kalypsodeegree.model.geometry.GM_Position;
import org.kalypsodeegree_impl.gml.binding.commons.ICoverage;
import org.kalypsodeegree_impl.gml.binding.commons.ICoverageCollection;
import org.kalypsodeegree_impl.model.geometry.JTSAdapter;
import org.xml.sax.SAXException;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * @author Thomas Jung
 */
public class RiskModelHelper
{

  /**
   * updates the style for the specific annual damage value layers according to the overall min and max values.
   *
   * @param scenarioFolder
   * @param model
   * @param sldFile
   * @throws IOException
   * @throws SAXException
   * @throws CoreException
   */
  public static void updateDamageStyle( final IFile sldFile, final IRasterDataModel model ) throws IOException, SAXException, CoreException
  {
    /* update style */
    final IFeatureWrapperCollection<IAnnualCoverageCollection> specificDamageCoverageCollections = model.getSpecificDamageCoverageCollection();

    BigDecimal maxDamageValue = new BigDecimal( Double.MIN_VALUE ).setScale( 4, BigDecimal.ROUND_HALF_UP );
    BigDecimal minDamageValue = new BigDecimal( Double.MAX_VALUE ).setScale( 4, BigDecimal.ROUND_HALF_UP );

    for( final ICoverageCollection collection : specificDamageCoverageCollections )
    {
      try
      {
        final BigDecimal[] extrema = GeoGridUtilities.getMinMax( collection );

        minDamageValue = minDamageValue.min( extrema[0] );
        maxDamageValue = maxDamageValue.max( extrema[1] );
      }
      catch( final Exception e )
      {
        e.printStackTrace();
      }
    }

    final Color color = new Color( 237, 80, 25 );
    SLDHelper.exportRasterSymbolyzerSLD( sldFile, minDamageValue.doubleValue(), maxDamageValue.doubleValue() * 1.05, 20, color, "Kalypso style", "Kalypso style", null ); //$NON-NLS-1$ //$NON-NLS-2$

    // TODO: maybe refreshing...
  }

  /**
   * Creates the specific damage coverage collection. <br>
   * The damage value for each grid cell is taken from the underlying polygon.
   *
   * @param scenarioFolder
   *            scenario folder
   * @param polygonCollection
   *            landuse polygon collection
   * @param sourceCoverageCollection
   *            {@link CoverageCollection} with flow depth values
   * @param specificDamageCoverageCollection
   *            {@link CoverageCollection} with damage values
   * @return {@link CoverageCollection} with the annual damage values
   * @throws Exception
   */
  public static IAnnualCoverageCollection createSpecificDamageCoverages( final IFolder scenarioFolder, final IFeatureWrapperCollection<ILandusePolygon> polygonCollection, final IAnnualCoverageCollection sourceCoverageCollection, final IFeatureWrapperCollection<IAnnualCoverageCollection> specificDamageCoverageCollection, final List<ILanduseClass> landuseClassesList ) throws Exception
  {
    final IAnnualCoverageCollection destCoverageCollection = specificDamageCoverageCollection.addNew( IAnnualCoverageCollection.QNAME );

    final int returnPeriod = sourceCoverageCollection.getReturnPeriod();

    for( int i = 0; i < sourceCoverageCollection.size(); i++ )
    {
      final ICoverage inputCoverage = sourceCoverageCollection.get( i );

      final IGeoGrid inputGrid = GeoGridUtilities.toGrid( inputCoverage );
      final double cellSize = Math.abs( inputGrid.getOffsetX().x - inputGrid.getOffsetY().x ) * Math.abs( inputGrid.getOffsetX().y - inputGrid.getOffsetY().y );

      final IGeoGrid outputGrid = new AbstractDelegatingGeoGrid( inputGrid )
      {
        /**
         * @see org.kalypso.grid.AbstractDelegatingGeoGrid#getValue(int, int)
         *
         * gets the damage value for each grid cell from the underlying polygon.
         */
        @Override
        public double getValue( int x, int y ) throws GeoGridException
        {
          try
          {
            final Double value = super.getValue( x, y );
            if( value.equals( Double.NaN ) )
              return Double.NaN;
            else
            {

              /* This coordinate has the cs of the input grid! */
              final Coordinate coordinate = GeoGridUtilities.toCoordinate( inputGrid, x, y, null );

              if( polygonCollection.size() == 0 )
                return Double.NaN;

              final ILandusePolygon landusePolygon = polygonCollection.get( 0 );
              final String coordinateSystem = landusePolygon.getGeometry().getCoordinateSystem();
              final GM_Position positionAt = JTSAdapter.wrap( coordinate );

              /* Transform query position into the cs of the polygons. */
              final CRSTransformation transformation = CachedTransformationFactory.getInstance().createFromCoordinateSystems( inputGrid.getSourceCRS(), coordinateSystem );
              final GM_Position position = TransformUtilities.transform( positionAt, transformation );

              /* This list has some unknown cs. */

              final List<ILandusePolygon> list = polygonCollection.query( position );
              if( list == null || list.size() == 0 )
                return Double.NaN;
              else
              {
                for( final ILandusePolygon polygon : list )
                {
                  if( polygon.contains( position ) )
                  {
                    final int landuseClassOrdinalNumber = polygon.getLanduseClassOrdinalNumber();
                    final double damageValue = polygon.getDamageValue( value );

                    if( Double.isNaN( damageValue ) )
                      return Double.NaN;

                    if( damageValue < 0.0 )
                      return Double.NaN;

                    /* set statistic for landuse class */
                    fillStatistics( returnPeriod, landuseClassesList, polygon, damageValue, landuseClassOrdinalNumber, cellSize );
                    return damageValue;
                  }
                }
              }
              return Double.NaN;
            }
          }
          catch( Exception ex )
          {
            throw new GeoGridException( org.kalypso.risk.Messages.getString( "RiskModelHelper.0" ), ex ); //$NON-NLS-1$
          }
        }
      };

      /* add the new coverage to the collection */
      final String outputFilePath = "raster/output/specificDamage_HQ" + sourceCoverageCollection.getReturnPeriod() + "_part" + i + ".bin"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

      final IFile ifile = scenarioFolder.getFile( new Path( "models/" + outputFilePath ) ); //$NON-NLS-1$
      final File file = new File( ifile.getRawLocation().toPortableString() );

      final ICoverage newCoverage = GeoGridUtilities.addCoverage( destCoverageCollection, outputGrid, file, outputFilePath, "image/bin", new NullProgressMonitor() ); //$NON-NLS-1$

      for( final ILanduseClass landuseClass : landuseClassesList )
      {
        landuseClass.updateStatistic( returnPeriod );
      }
      newCoverage.setName( Messages.getString( org.kalypso.risk.Messages.getString( "RiskModelHelper.6" ) ) + sourceCoverageCollection.getReturnPeriod() + " [" + i + "]" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      newCoverage.setDescription( Messages.getString( org.kalypso.risk.Messages.getString( "RiskModelHelper.9" ) ) + new Date().toString() ); //$NON-NLS-1$

      inputGrid.dispose();
    }
    /* set the return period of the specific damage grid */
    destCoverageCollection.setReturnPeriod( sourceCoverageCollection.getReturnPeriod() );
    return destCoverageCollection;
  }

  protected static void fillStatistics( final int returnPeriod, final List<ILanduseClass> landuseClassesList, final ILandusePolygon polygon, final double damageValue, final int landuseClassOrdinalNumber, final double cellSize )
  {
    /* add the current damage value to all landuse polygons that covers the current raster cell */
    polygon.updateStatistics( damageValue, returnPeriod );

    /* find the right landuse class that holds the polygon */
    for( final ILanduseClass landuseClass : landuseClassesList )
    {
      if( landuseClass.getOrdinalNumber() == landuseClassOrdinalNumber )
      {
        final IRiskLanduseStatistic statistic = RiskLanduseHelper.getLanduseStatisticEntry( landuseClass, returnPeriod, cellSize );

        final BigDecimal value = new BigDecimal( damageValue ).setScale( 2, BigDecimal.ROUND_HALF_UP );
        statistic.updateStatistic( value );
      }
    }
  }

  /**
   * creates a map layer for the grid collection
   *
   * @param parentKalypsoTheme
   *            {@link AbstractCascadingLayerTheme} in which we add the new theme layer
   * @param coverageCollection
   *            {@link IAnnualCoverageCollection} that will be added
   * @param scenarioFolder
   * @throws Exception
   */
  public static void createSpecificDamageMapLayer( final AbstractCascadingLayerTheme parentKalypsoTheme, final IAnnualCoverageCollection coverageCollection, final IResource scenarioFolder ) throws Exception
  {
    final String layerName = Messages.getString( "DamagePotentialCalculationHandler.13" ) + coverageCollection.getReturnPeriod() + ")"; //$NON-NLS-1$ //$NON-NLS-2$

    final StyledLayerType layer = new StyledLayerType();
    layer.setName( layerName );
    layer.setFeaturePath( "#fid#" + coverageCollection.getFeature().getId() + "/coverageMember" ); //$NON-NLS-1$ //$NON-NLS-2$
    layer.setLinktype( "gml" ); //$NON-NLS-1$
    layer.setType( "simple" ); //$NON-NLS-1$
    layer.setVisible( true );
    layer.setActuate( "onRequest" ); //$NON-NLS-1$
    layer.setHref( "project:/" + scenarioFolder.getProjectRelativePath() + "/models/RasterDataModel.gml" ); //$NON-NLS-1$ //$NON-NLS-2$
    layer.setVisible( true );
    final Property layerPropertyDeletable = new Property();
    layerPropertyDeletable.setName( IKalypsoTheme.PROPERTY_DELETEABLE );
    layerPropertyDeletable.setValue( "false" ); //$NON-NLS-1$
    final Property layerPropertyThemeInfoId = new Property();
    layerPropertyThemeInfoId.setName( IKalypsoTheme.PROPERTY_THEME_INFO_ID );
    layerPropertyThemeInfoId.setValue( "org.kalypso.gml.ui.map.CoverageThemeInfo?format=Schadenspotential %.2f €/m²" ); //$NON-NLS-1$
    final List<Property> layerPropertyList = layer.getProperty();
    layerPropertyList.add( layerPropertyDeletable );
    layerPropertyList.add( layerPropertyThemeInfoId );
    final List<Style> styleList = layer.getStyle();
    final Style style = new Style();
    style.setLinktype( "sld" ); //$NON-NLS-1$
    style.setStyle( "Kalypso style" ); //$NON-NLS-1$
    style.setActuate( "onRequest" ); //$NON-NLS-1$
    style.setHref( "../styles/SpecificDamagePotentialCoverage.sld" ); //$NON-NLS-1$
    style.setType( "simple" ); //$NON-NLS-1$
    styleList.add( style );

    parentKalypsoTheme.addLayer( layer );
  }

  public static void deleteExistingMapLayers( final CascadingKalypsoTheme parentKalypsoTheme )
  {
    final IKalypsoTheme[] childThemes = parentKalypsoTheme.getAllThemes();
    for( int i = 0; i < childThemes.length; i++ )
      parentKalypsoTheme.removeTheme( childThemes[i] );
  }

  /**
   * calculates the average annual damage value for one raster cell <br>
   * further informations: DVWK-Mitteilung 10
   *
   * @param damages
   *            damage values for all annualities
   * @param probabilities
   *            the probability values for all annualtities
   * @return damage potential value [€/a]
   */
  public static double calcAverageAnnualDamageValue( final double[] damages, final double[] probabilities )
  {
    /* average annual damage value (jährlicher Schadenserwartungswert) [€/m²/a] */
    double result = 0.0;

    for( int j = 1; j < probabilities.length; j++ )
    {
      final double dP = Math.abs( probabilities[j - 1] - probabilities[j] );
      result += (damages[j - 1] + damages[j]) * dP / 2;
    }
    return result;
  }

  /**
   * creates the land use raster files. The grid cells get the ordinal number of the the land use class.
   *
   * @param scenarioFolder
   *            relative path needed for the output file path to append on
   * @param inputCoverages
   *            {@link ICoverageCollection} that gives the extend of the raster
   * @param outputCoverages
   *            {@link ICoverageCollection} for the landuse
   * @param polygonCollection
   *            landuse polygons that give the landuse class ordinal number
   * @throws Exception
   */
  public static IStatus doRasterLanduse( final IFolder scenarioFolder, final ICoverageCollection inputCoverages, final ICoverageCollection outputCoverages, final IFeatureWrapperCollection<ILandusePolygon> polygonCollection, final IProgressMonitor monitor )
  {
    try
    {
      for( int i = 0; i < inputCoverages.size(); i++ )
      {
        final ICoverage inputCoverage = inputCoverages.get( i );
        final SubMonitor progress = SubMonitor.convert( monitor, Messages.getString( "RiskModelHelper.14" ) + (i+1) + "/" + inputCoverages.size() + "]...", 100 ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        final IGeoGrid inputGrid = GeoGridUtilities.toGrid( inputCoverage );
        final int sizeY = inputGrid.getSizeY();

        /* This grid should have the cs of the input grid. */
        final IGeoGrid outputGrid = new AbstractDelegatingGeoGrid( inputGrid )
        {
          /**
           * @see org.kalypso.grid.AbstractDelegatingGeoGrid#getValue(int, int)
           *
           * gets the ordinal number of the landuse class
           */
          @Override
          public double getValue( int x, int y ) throws GeoGridException
          {
            progress.setWorkRemaining( sizeY + 2 );

            try
            {
              final Double value = super.getValue( x, y );
              if( value.equals( Double.NaN ) )
                return Double.NaN;
              else
              {
                /* This coordinate has the cs of the input grid! */
                final Coordinate coordinate = GeoGridUtilities.toCoordinate( inputGrid, x, y, null );

                if( polygonCollection.size() == 0 )
                  return Double.NaN;

                final ILandusePolygon landusePolygon = polygonCollection.get( 0 );
                final String coordinateSystem = landusePolygon.getGeometry().getCoordinateSystem();
                final GM_Position positionAt = JTSAdapter.wrap( coordinate );

                /* Transform query position into the cs of the polygons. */
                final CRSTransformation transformation = CachedTransformationFactory.getInstance().createFromCoordinateSystems( inputGrid.getSourceCRS(), coordinateSystem );
                final GM_Position position = TransformUtilities.transform( positionAt, transformation );

                /* This list has some unknown cs. */
                final List<ILandusePolygon> list = polygonCollection.query( position );
                if( list == null || list.size() == 0 )
                  return Double.NaN;
                else
                  for( final ILandusePolygon polygon : list )
                  {
                    if( polygon.contains( position ) )
                      return polygon.getLanduseClassOrdinalNumber();
                  }
                return Double.NaN;
              }
            }
            catch( Exception ex )
            {
              throw new GeoGridException( org.kalypso.risk.Messages.getString( "RiskModelHelper.10" ), ex ); //$NON-NLS-1$
            }
          }
        };

        // TODO: change name: better: use input name
        final String outputFilePath = "raster/output/LanduseCoverage" + i + ".dat"; //$NON-NLS-1$ //$NON-NLS-2$

        final IFile ifile = scenarioFolder.getFile( new Path( "models/" + outputFilePath ) ); //$NON-NLS-1$
        final File file = new File( ifile.getRawLocation().toPortableString() );

        GeoGridUtilities.addCoverage( outputCoverages, outputGrid, file, outputFilePath, "image/bin", new NullProgressMonitor() ); //$NON-NLS-1$
        inputGrid.dispose();
      }

      return Status.OK_STATUS;
    }
    catch( final Exception e )
    {
      return StatusUtilities.statusFromThrowable( e, org.kalypso.risk.Messages.getString( "RiskModelHelper.11" ) ); //$NON-NLS-1$
    }
  }

  /**
   * get the water depth raster with the greatest annuality
   *
   * @param waterDepthCoverageCollection
   *            raster collection
   * @return {@link IAnnualCoverageCollection} with greatest return period value
   */
  public static IAnnualCoverageCollection getMaxReturnPeriodCollection( final IFeatureWrapperCollection<IAnnualCoverageCollection> waterDepthCoverageCollection )
  {
    int maxReturnPeriod = Integer.MIN_VALUE;
    IAnnualCoverageCollection maxCoveragesCollection = null;
    for( final IAnnualCoverageCollection annualCoverageCollection : waterDepthCoverageCollection )
    {
      if( annualCoverageCollection.getReturnPeriod() > maxReturnPeriod && annualCoverageCollection.size() > 0 )
      {
        maxReturnPeriod = annualCoverageCollection.getReturnPeriod();
        maxCoveragesCollection = annualCoverageCollection;
      }
    }
    return maxCoveragesCollection;
  }

  /**
   * deletes the old layer, add the new one and modifies the style according to the max values
   *
   * @param scenarioFolder
   * @param model
   * @param mapModell
   * @throws Exception
   * @throws IOException
   * @throws SAXException
   * @throws CoreException
   */
  public static void updateDamageLayers( final IFolder scenarioFolder, final IRasterDataModel model, final GisTemplateMapModell mapModell ) throws Exception
  {
    /* get cascading them that holds the damage layers */
    final String damageThemeProperty = "damagePotentialThemes"; //$NON-NLS-1$

    final CascadingKalypsoTheme parentKalypsoTheme = CascadingThemeHelper.getNamedCascadingTheme( mapModell, org.kalypso.risk.Messages.getString( "RiskModelHelper.12" ), damageThemeProperty ); //$NON-NLS-1$

    /* delete existing damage layers */
    deleteExistingMapLayers( parentKalypsoTheme );

    parentKalypsoTheme.setVisible( true );

    /* add the coverage collections to the map */
    final IFeatureWrapperCollection<IAnnualCoverageCollection> specificDamageCoverageCollection = model.getSpecificDamageCoverageCollection();
    for( final IAnnualCoverageCollection annualCoverageCollection : specificDamageCoverageCollection )
      createSpecificDamageMapLayer( parentKalypsoTheme, annualCoverageCollection, scenarioFolder );

    final IFile sldFile = scenarioFolder.getFile( "/styles/SpecificDamagePotentialCoverage.sld" ); //$NON-NLS-1$
    updateDamageStyle( sldFile, model );
  }

  /**
   * deletes the old layers and adds the new ones
   *
   * @param scenarioFolder
   * @param model
   * @param mapModell
   * @throws Exception
   * @throws IOException
   * @throws SAXException
   * @throws CoreException
   */
  public static void updateWaterdepthLayers( final IFolder scenarioFolder, final IRasterDataModel model, final List<AsciiRasterInfo> rasterInfos, final GisTemplateMapModell mapModell ) throws Exception
  {
    /* get cascading them that holds the damage layers */
    final String depthThemeProperty = "depthGridThemes"; //$NON-NLS-1$
    final CascadingKalypsoTheme parentKalypsoTheme = CascadingThemeHelper.getNamedCascadingTheme( mapModell, org.kalypso.risk.Messages.getString( "RiskModelHelper.13" ), depthThemeProperty ); //$NON-NLS-1$

    /* delete existing damage layers */
    // TODO: manage that only the newly imported gets deleted.
    deleteExistingMapLayers( parentKalypsoTheme, rasterInfos );

    parentKalypsoTheme.setVisible( true );

    final IFeatureWrapperCollection<IAnnualCoverageCollection> waterdepthCoverageCollection = model.getWaterlevelCoverageCollection();

    for( int i = 0; i < rasterInfos.size(); i++ )
    {
      final AsciiRasterInfo asciiRasterInfo = rasterInfos.get( i );
      final int returnPeriod = asciiRasterInfo.getReturnPeriod();
      final String layerName = "HQ " + returnPeriod; //$NON-NLS-1$

      final int collectionIndex = getCollectionIndex( waterdepthCoverageCollection, returnPeriod );
      createWaterdepthLayer( scenarioFolder, parentKalypsoTheme, waterdepthCoverageCollection.get( collectionIndex ), layerName );
    }
  }

  private static int getCollectionIndex( final IFeatureWrapperCollection<IAnnualCoverageCollection> waterdepthCoverageCollection, final int returnPeriod )
  {
    int index = 0;

    for( int i = 0; i < waterdepthCoverageCollection.size(); i++ )
    {
      final IAnnualCoverageCollection collection = waterdepthCoverageCollection.get( i );
      if( collection.getReturnPeriod().equals( returnPeriod ) )
        index = i;
    }
    return index;

  }

  private static void deleteExistingMapLayers( final CascadingKalypsoTheme parentKalypsoTheme, final List<AsciiRasterInfo> rasterInfos )
  {
    final List<IKalypsoTheme> themesToRemove = new ArrayList<IKalypsoTheme>();

    for( int i = 0; i < rasterInfos.size(); i++ )
    {
      final AsciiRasterInfo asciiRasterInfo = rasterInfos.get( i );
      final String layerName = "HQ " + asciiRasterInfo.getReturnPeriod(); //$NON-NLS-1$
      final IKalypsoTheme[] childThemes = parentKalypsoTheme.getAllThemes();
      for( int j = 0; j < childThemes.length; j++ )
        if( childThemes[j].getName().getKey().equals( layerName ) )
          themesToRemove.add( childThemes[j] );
    }
    for( final IKalypsoTheme themeToRemove : themesToRemove )
      parentKalypsoTheme.removeTheme( themeToRemove );
  }

  private static void createWaterdepthLayer( final IFolder scenarioFolder, final CascadingKalypsoTheme parentKalypsoTheme, final IAnnualCoverageCollection annualCoverageCollection, final String layerName ) throws Exception
  {
    final StyledLayerType layer = new StyledLayerType();
    layer.setName( layerName );
    layer.setFeaturePath( "#fid#" + annualCoverageCollection.getGmlID() + "/coverageMember" ); //$NON-NLS-1$ //$NON-NLS-2$
    layer.setLinktype( "gml" ); //$NON-NLS-1$
    layer.setType( "simple" ); //$NON-NLS-1$
    layer.setVisible( true );
    layer.setActuate( "onRequest" ); //$NON-NLS-1$
    layer.setHref( "project:/" + scenarioFolder.getProjectRelativePath() + "/models/RasterDataModel.gml" ); //$NON-NLS-1$ //$NON-NLS-2$
    layer.setVisible( true );
    final Property layerPropertyDeletable = new Property();
    layerPropertyDeletable.setName( IKalypsoTheme.PROPERTY_DELETEABLE );
    layerPropertyDeletable.setValue( "false" ); //$NON-NLS-1$
    final Property layerPropertyThemeInfoId = new Property();
    layerPropertyThemeInfoId.setName( IKalypsoTheme.PROPERTY_THEME_INFO_ID );
    layerPropertyThemeInfoId.setValue( "org.kalypso.gml.ui.map.CoverageThemeInfo?format=Wassertiefe %.2f m" ); //$NON-NLS-1$
    final List<Property> layerPropertyList = layer.getProperty();
    layerPropertyList.add( layerPropertyDeletable );
    layerPropertyList.add( layerPropertyThemeInfoId );
    final List<Style> styleList = layer.getStyle();
    final Style style = new Style();
    style.setLinktype( "sld" ); //$NON-NLS-1$
    style.setStyle( "Kalypso style" ); //$NON-NLS-1$
    style.setActuate( "onRequest" ); //$NON-NLS-1$
    style.setHref( "../styles/WaterlevelCoverage.sld" ); //$NON-NLS-1$
    style.setType( "simple" ); //$NON-NLS-1$
    styleList.add( style );

    parentKalypsoTheme.addLayer( layer );
  }

  public static int guessReturnPeriodFromName( final String name )
  {
    final Pattern pattern = Pattern.compile( "([^0-9]*)([0-9]+)([^0-9]*)" ); //$NON-NLS-1$
    final Matcher matcher = pattern.matcher( name );
    if( matcher.matches() )
      return Integer.parseInt( matcher.group( 2 ) );
    return 0;
  }

  /**
   * calculates the average annual damage value for each landuse class<br>
   * The value is calculated by integrating the specific damage values.<br>
   *
   */
  @SuppressWarnings("unchecked")//$NON-NLS-1$
  public static void calcLanduseAnnualAverageDamage( final IRasterizationControlModel rasterizationControlModel )
  {
    /* get the landuse classes */
    final List<ILanduseClass> landuseClassesList = rasterizationControlModel.getLanduseClassesList();
    if( landuseClassesList.size() == 0 )
      return;

    for( final ILanduseClass landuseClass : landuseClassesList )
    {
      /* get the statistical data for each landuse class */
      final List<IRiskLanduseStatistic> landuseStatisticList = landuseClass.getLanduseStatisticList();
      if( landuseStatisticList.size() == 0 )
        landuseClass.setAverageAnnualDamage( 0.0 );

      // generate a return period - sorted list
      final Map<Double, IRiskLanduseStatistic> periodSortedMap = new TreeMap<Double, IRiskLanduseStatistic>();
      for( int i = 0; i < landuseStatisticList.size(); i++ )
      {
        final IRiskLanduseStatistic riskLanduseStatistic = landuseStatisticList.get( i );
        final double period = riskLanduseStatistic.getReturnPeriod();
        periodSortedMap.put( period, riskLanduseStatistic );
      }

      final Set<Double> keySet = periodSortedMap.keySet();

      final Double[] periods = keySet.toArray( new Double[keySet.size()] );

      /* calculate the average annual damage by integrating the specific damage values */

      double averageSum = 0.0;

      for( int i = 0; i < periods.length - 1; i++ )
      {
        if( periods[i] == null || periods[i] == 0 )
          continue;

        /* get the probability for each return period */
        final double p1 = 1 / periods[i];
        final double p2 = 1 / periods[i + 1];

        /* calculate the difference */
        final double d_pi = p1 - p2;

        /*
         * get the specific damage summation value for this and the next return period an calculate the difference
         * (divided by 2). This means nothing else than to calculate the area for trapezoid with ha=specific value 1 and
         * hb= specific value 2. The width of the trapezoid is the difference of the probabilities that belong to both
         * specific damages values.
         */
        final IRiskLanduseStatistic statEntry1 = periodSortedMap.get( periods[i] );
        final IRiskLanduseStatistic statEntry2 = periodSortedMap.get( periods[i + 1] );

        // final BigDecimal sumStat = statEntry2.getDamageSum().add( statEntry1.getDamageSum() );
        final BigDecimal sumStat = statEntry2.getAverageDamage().add( statEntry1.getAverageDamage() );
        final double value = sumStat.doubleValue() / 2;
        final BigDecimal si = new BigDecimal( value ).setScale( 2, BigDecimal.ROUND_HALF_UP );

        /* calculate the average damage and add it */
        averageSum = averageSum + si.doubleValue() * d_pi;
      }

      /* set the average annual damage value for the current landuse class */
      landuseClass.setAverageAnnualDamage( averageSum );

    }
  }

}
