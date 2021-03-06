package org.kalypso.kalypso1d2d.pjt.map;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.kalypso.afgui.KalypsoAFGUIFrameworkPlugin;
import org.kalypso.kalypsomodel1d2d.conv.results.NodeResultHelper;
import org.kalypso.kalypsomodel1d2d.conv.results.ResultMeta1d2dHelper;
import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IFE1D2DNode;
import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IFEDiscretisationModel1d2d;
import org.kalypso.kalypsomodel1d2d.schema.binding.discr.IPolyElement;
import org.kalypso.kalypsomodel1d2d.schema.binding.results.GMLNodeResult;
import org.kalypso.kalypsomodel1d2d.schema.binding.results.INodeResult;
import org.kalypso.ogc.gml.IKalypsoFeatureTheme;
import org.kalypso.ogc.gml.IKalypsoTheme;
import org.kalypso.ogc.gml.IKalypsoThemeInfo;
import org.kalypso.ogc.gml.mapmodel.CommandableWorkspace;
import org.kalypsodeegree.KalypsoDeegreePlugin;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureList;
import org.kalypsodeegree.model.geometry.GM_Position;
import org.kalypsodeegree.model.geometry.GM_Triangle;
import org.kalypsodeegree_impl.model.feature.FeatureHelper;
import org.kalypsodeegree_impl.model.geometry.GeometryFactory;
import org.kalypsodeegree_impl.tools.GeometryUtilities;

import de.renew.workflow.connector.cases.IScenarioDataProvider;

public class NodeThemeInfo implements IKalypsoThemeInfo
{
  public NodeThemeInfo( )
  {
  }

  public final static String PROP_NS_PREFIX = "http://www.tu-harburg.de/wb/kalypso/schemata/1d2dResults"; //$NON-NLS-1$

  private IKalypsoFeatureTheme m_theme;

  private QName m_actPropQname;

  private final double m_grabDistance = 50;

  private String m_propertyNameFromTheme;

  private boolean m_boolResolveNodesFromDiscrModel;

  private IFEDiscretisationModel1d2d m_discretisationModel = null;

  private CommandableWorkspace m_workspace = null;

  private FeatureList m_featureList = null;

  // private static List<String> m_listSWANProps = new ArrayList< String >( Arrays.asList( new String[]{
  // ResultSldHelper.WAVE_HSIG_TYPE, ResultSldHelper.WAVE_DIRECTION_TYPE, ResultSldHelper.WAVE_PERIOD_TYPE } ) );

  /**
   * @see org.kalypso.ogc.gml.IKalypsoThemeInfo#init(org.kalypso.ogc.gml.IKalypsoTheme, java.util.Properties)
   */
  @Override
  public void init( final IKalypsoTheme theme, final Properties props )
  {
    Assert.isLegal( theme instanceof IKalypsoFeatureTheme );

    m_theme = (IKalypsoFeatureTheme) theme;

    m_workspace = m_theme.getWorkspace();

    m_featureList = m_theme.getFeatureList();

    m_propertyNameFromTheme = getPropertyNameFromTheme( theme );
    if( NodeResultHelper.VELO_TYPE.equals( m_propertyNameFromTheme ) )
    {
      m_actPropQname = new QName( PROP_NS_PREFIX, NodeResultHelper.VELOCITY );
    }
    else
    {
      m_actPropQname = "".equals( m_propertyNameFromTheme ) ? null : new QName( PROP_NS_PREFIX, m_propertyNameFromTheme.toLowerCase() ); //$NON-NLS-1$
    }
    // if( m_listSWANProps.contains( m_propertyNameFromTheme ) )
    {
      m_boolResolveNodesFromDiscrModel = true;
      final IScenarioDataProvider caseDataProvider = KalypsoAFGUIFrameworkPlugin.getDataProvider();

      try
      {
        m_discretisationModel = caseDataProvider.getModel( IFEDiscretisationModel1d2d.class.getName() );
      }
      catch( final CoreException e )
      {
        return;
      }
    }
  }

  private String getPropertyNameFromTheme( final IKalypsoTheme theme )
  {
    final StringTokenizer lTokenizer = new StringTokenizer( theme.getLabel(), ResultMeta1d2dHelper.STR_THEME_NAME_SEPARATOR.trim() );
    if( lTokenizer.countTokens() > 2 )
    {
      lTokenizer.nextToken();
      return lTokenizer.nextToken();
    }
    return ""; //$NON-NLS-1$
  }

  /**
   * @see org.kalypso.ogc.gml.IKalypsoThemeInfo#appendInfo(java.util.Formatter,
   *      org.kalypsodeegree.model.geometry.GM_Position)
   */
  @Override
  public void appendInfo( final Formatter formatter, final GM_Position pos )
  {
    // not yet implemented, use quick-info
    appendQuickInfo( formatter, pos );
  }

  /**
   * @see org.kalypso.ogc.gml.IKalypsoThemeInfo#appendQuickInfo(java.util.Formatter,
   *      org.kalypsodeegree.model.geometry.GM_Position)
   */
  @SuppressWarnings("unchecked")
  @Override
  public void appendQuickInfo( final Formatter formatter, final GM_Position pos )
  {
    Assert.isNotNull( m_theme );

    if( m_featureList == null )
      return;

    Object nodeObject = null;
    if( m_boolResolveNodesFromDiscrModel )
    {
      if( m_discretisationModel == null )
      {
        return;
      }
      nodeObject = m_discretisationModel.find2DElement( GeometryFactory.createGM_Point( pos, KalypsoDeegreePlugin.getDefault().getCoordinateSystem() ), 0.01 );
    }
    else
    {
      nodeObject = GeometryUtilities.findNearestFeature( GeometryFactory.createGM_Point( pos, KalypsoDeegreePlugin.getDefault().getCoordinateSystem() ), m_grabDistance, m_featureList, GMLNodeResult.QNAME_PROP_LOCATION );
    }
    if( nodeObject == null )
      return;

    {
      /* Search for the first feature which provides a value */
      // final Feature feature = FeatureHelper.getFeature( workspace, nodeObject );

      final Object value = getInterpolatedValue( nodeObject, pos );
      // Object value = feature.getProperty( m_actPropQname );

      if( value instanceof Double && !Double.isNaN( (Double) value ) )
      {
        formatter.format( "%s: %.3f", m_propertyNameFromTheme, value ); //$NON-NLS-1$
        return;
      }
      else
      {
        if( value != null && value instanceof List< ? > )
        {
          final List<Double> vector = (List<Double>) value;
          if( vector.size() != 2 )
            return;

          final double vx = vector.get( 0 );
          final double vy = vector.get( 1 );

          formatter.format( "%s: %.2fm/s, %.2f�", m_propertyNameFromTheme, Math.sqrt( vx * vx + vy * vy ), GeometryUtilities.directionFromVector( vx, vy ) ); //$NON-NLS-1$
        }
      }
    }
  }

  private Object getInterpolatedValue( final Object nodeObject, final GM_Position pos )
  {
    if( nodeObject instanceof IPolyElement )
    {
      final IPolyElement lPolyEl = (IPolyElement) nodeObject;

      final IFE1D2DNode[] nodes = lPolyEl.getNodes();
      if( nodes.length > 5 )
      {
        return getNodePropertyAtPos( pos );
      }
      final String coordinateSystem = KalypsoDeegreePlugin.getDefault().getCoordinateSystem();
      final List<GM_Position> lListPositionWithValues = new ArrayList<>();
      final List<GM_Position> lListPositionWithValues2 = new ArrayList<>();
      for( final IFE1D2DNode actNode : nodes )
      {
        final Feature nodeRes = GeometryUtilities.findNearestFeature( GeometryFactory.createGM_Point( actNode.getPoint().getPosition(), coordinateSystem ), m_grabDistance, m_featureList, GMLNodeResult.QNAME_PROP_LOCATION );
        if( nodeRes == null )
          continue;

        Object value = nodeRes.getProperty( m_actPropQname );
        if( value == null )
          continue;
        if( NodeResultHelper.WAVE_DIRECTION_TYPE.equals( m_propertyNameFromTheme ) )
        {
          final List<Double> vector = new ArrayList<>();
          vector.add( Math.cos( (Double) value * (2 * Math.PI) / 360 ) );
          vector.add( Math.sin( (Double) value * (2 * Math.PI) / 360 ) );
          value = vector;
        }

        final INodeResult nodeResAdapter = (INodeResult) nodeRes.getAdapter( INodeResult.class );
        if( value instanceof Double )
        {
          lListPositionWithValues.add( GeometryFactory.createGM_Position( nodeResAdapter.getPoint().getX(), nodeResAdapter.getPoint().getY(), (Double) value ) );
        }
        else if( value instanceof List< ? > )
        {
          final List< ? > vector = (List< ? >) value;
          if( vector.size() != 2 )
            continue;

          lListPositionWithValues.add( GeometryFactory.createGM_Position( nodeResAdapter.getPoint().getX(), nodeResAdapter.getPoint().getY(), (Double) vector.get( 0 ) ) );
          lListPositionWithValues2.add( GeometryFactory.createGM_Position( nodeResAdapter.getPoint().getX(), nodeResAdapter.getPoint().getY(), (Double) vector.get( 1 ) ) );
        }
      }
      if( lListPositionWithValues.size() < 3 )
      {
        return getNodePropertyAtPos( pos );
      }
      GM_Triangle lTri = GeometryFactory.createGM_Triangle( lListPositionWithValues.get( 0 ), lListPositionWithValues.get( 1 ), lListPositionWithValues.get( 2 ), coordinateSystem );
      GM_Triangle lTri2 = null;
      if( lListPositionWithValues2.size() > 2 )
        lTri2 = GeometryFactory.createGM_Triangle( lListPositionWithValues2.get( 0 ), lListPositionWithValues2.get( 1 ), lListPositionWithValues2.get( 2 ), coordinateSystem );
      final Object lRes = getValueFromTrianglesAtPosition( pos, lTri, lTri2 );
      if( lRes != null )
      {
        return lRes;
      }
      else if( lListPositionWithValues.size() > 4 )
      {
        lTri = GeometryFactory.createGM_Triangle( lListPositionWithValues.get( 0 ), lListPositionWithValues.get( 2 ), lListPositionWithValues.get( 3 ), coordinateSystem );
        if( lListPositionWithValues2.size() > 2 )
          lTri2 = GeometryFactory.createGM_Triangle( lListPositionWithValues2.get( 0 ), lListPositionWithValues2.get( 2 ), lListPositionWithValues2.get( 3 ), coordinateSystem );
        return getValueFromTrianglesAtPosition( pos, lTri, lTri2 );
      }
      else
      {
        return getNodePropertyAtPos( pos );
      }

    }
    else
    {
      return getNodePropertyAtPos( pos );
    }
  }

  // TODO: switch to this implementation
  // private Pair<Double, Double> getInterpolatedPair( final GM_Point pPointToInterpolateAt, final Map<GM_Point,
  // Pair<Double, Double>> pMapPointsValues )
  // {
  // GM_Triangle lTriFirst = null;
  // GM_Triangle lTriSecond = null;
  // try
  // {
  // final List<GM_Point> lListAllPoints = new ArrayList<GM_Point>();
  // final List<Double> lListAllFirsts = new ArrayList<Double>();
  // final List<Double> lListAllSeconds = new ArrayList<Double>();
  //
  // final Set<GM_Point> lSetPoints = pMapPointsValues.keySet();
  // for( final GM_Point gmPoint : lSetPoints )
  // {
  // lListAllPoints.add( gmPoint );
  // lListAllFirsts.add( pMapPointsValues.get( gmPoint ).first );
  // lListAllSeconds.add( pMapPointsValues.get( gmPoint ).second );
  // }
  // Map<GM_Point, Double> lMapFirst = CollectionsHelper.joinListsToMap( lListAllPoints.subList( 0, 3 ),
  // lListAllFirsts.subList( 0, 3 ) );
  // lTriFirst = GeometryUtilities.createTriangleForBilinearInterpolation( lMapFirst );
  // if( lTriFirst.contains( pPointToInterpolateAt ) )
  // {
  // final Map<GM_Point, Double> lMapSecond = CollectionsHelper.joinListsToMap( lListAllPoints.subList( 0, 3 ),
  // lListAllFirsts.subList( 0, 3 ) );
  // lTriSecond = GeometryUtilities.createTriangleForBilinearInterpolation( lMapSecond );
  // return new Pair<Double, Double>( lTriFirst.getValue( pPointToInterpolateAt.getPosition() ), lTriSecond.getValue(
  // pPointToInterpolateAt.getPosition() ) );
  // }
  // else
  // {
  // lListAllPoints.remove( 1 );
  // lListAllFirsts.remove( 1 );
  // lListAllSeconds.remove( 1 );
  // lMapFirst = CollectionsHelper.joinListsToMap( lListAllPoints, lListAllFirsts );
  // lTriFirst = GeometryUtilities.createTriangleForBilinearInterpolation( lMapFirst );
  // final Map<GM_Point, Double> lMapSecond = CollectionsHelper.joinListsToMap( lListAllPoints, lListAllFirsts );
  // lTriSecond = GeometryUtilities.createTriangleForBilinearInterpolation( lMapSecond );
  // return new Pair<Double, Double>( lTriFirst.getValue( pPointToInterpolateAt.getPosition() ), lTriSecond.getValue(
  // pPointToInterpolateAt.getPosition() ) );
  // }
  // }
  // catch( final Exception e )
  // {
  // return null;
  // }
  // }

  private Object getValueFromTrianglesAtPosition( final GM_Position pos, final GM_Triangle lTri, final GM_Triangle lTri2 )
  {
    if( lTri != null && lTri.contains( pos ) )
    {
      if( lTri2 != null )
      {
        final List<Double> lListRes = new ArrayList<>();
        lListRes.add( lTri.getValue( pos ) );
        lListRes.add( lTri2.getValue( pos ) );
        if( NodeResultHelper.WAVE_DIRECTION_TYPE.equals( m_propertyNameFromTheme ) )
        {
          return GeometryUtilities.directionFromVector( lListRes.get( 0 ), lListRes.get( 1 ) );
        }
        return lListRes;
      }
      return lTri.getValue( pos );
    }
    return null;
  }

  private Object getNodePropertyAtPos( final GM_Position pos )
  {
    final Feature nodeRes = GeometryUtilities.findNearestFeature( GeometryFactory.createGM_Point( pos, KalypsoDeegreePlugin.getDefault().getCoordinateSystem() ), m_grabDistance, m_featureList, GMLNodeResult.QNAME_PROP_LOCATION );
    final Feature feature = FeatureHelper.getFeature( m_workspace, nodeRes );
    return feature == null ? null : feature.getProperty( m_actPropQname );
  }

}
