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
package org.kalypso.ui.rrm.internal.cm.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.eclipse.core.runtime.CoreException;
import org.kalypso.afgui.scenarios.ScenarioHelper;
import org.kalypso.afgui.scenarios.SzenarioDataProvider;
import org.kalypso.commons.command.ICommand;
import org.kalypso.contribs.java.util.DateUtilities;
import org.kalypso.gmlschema.GMLSchemaUtilities;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypso.model.hydrology.binding.model.NaModell;
import org.kalypso.model.hydrology.binding.timeseriesMappings.IMappingElement;
import org.kalypso.model.hydrology.binding.timeseriesMappings.ITimeseriesMapping;
import org.kalypso.model.hydrology.binding.timeseriesMappings.ITimeseriesMappingCollection;
import org.kalypso.model.hydrology.binding.timeseriesMappings.TimeseriesMappingType;
import org.kalypso.ogc.gml.command.AddLinkCommand;
import org.kalypso.ogc.gml.command.ChangeFeaturesCommand;
import org.kalypso.ogc.gml.command.CompositeCommand;
import org.kalypso.ogc.gml.command.DeleteFeatureCommand;
import org.kalypso.ogc.gml.command.FeatureChange;
import org.kalypso.ogc.gml.mapmodel.CommandableWorkspace;
import org.kalypso.ogc.sensor.util.ZmlLink;
import org.kalypso.ui.editor.gmleditor.command.AddFeatureCommand;
import org.kalypso.ui.rrm.internal.IUiRrmWorkflowConstants;
import org.kalypso.ui.rrm.internal.utils.featureBinding.FeatureBean;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree.model.feature.IFeatureBindingCollection;
import org.kalypsodeegree.model.feature.IFeatureProvider;
import org.kalypsodeegree.model.feature.IXLinkedFeature;

/**
 * @author Gernot Belger
 */
public class TimeseriesMappingBean extends FeatureBean<ITimeseriesMapping>
{
  private final Collection<MappingElementBean> m_lostMappings = new ArrayList<>();

  private final Collection<MappingElementBean> m_mappingElements = new ArrayList<>();

  private final TimeseriesMappingType m_mappingType;

  public TimeseriesMappingBean( final TimeseriesMappingType mappingType )
  {
    super( ITimeseriesMapping.FEATURE_TIMESERIES_MAPPING );

    m_mappingType = mappingType;

    setProperty( ITimeseriesMapping.PROPERTY_TYPE, mappingType.name() );
  }

  public TimeseriesMappingBean( final ITimeseriesMapping mapping )
  {
    super( mapping );

    m_mappingType = mapping.getType();
  }

  public void initFromNaModel( )
  {
    try
    {
      /* get all elements with potential mapping */
      final SzenarioDataProvider dataProvider = ScenarioHelper.getScenarioDataProvider();

      final CommandableWorkspace modelWorkspace = dataProvider.getCommandableWorkSpace( IUiRrmWorkflowConstants.SCENARIO_DATA_MODEL );
      final NaModell naModel = (NaModell) modelWorkspace.getRootFeature();

      final Feature[] modelElements = m_mappingType.getModelElements( naModel );

      /* Initialize empty mapping */
      final Map<Feature, MappingElementBean> mappingElements = new HashMap<>();

      for( final Feature modelElement : modelElements )
        mappingElements.put( modelElement, new MappingElementBean( null, modelElement, null ) );

      /* Fill in existing associations */

      final ITimeseriesMapping mapping = getFeature();
      if( mapping != null )
      {
        final IFeatureBindingCollection<IMappingElement> mappings = mapping.getMappings();
        for( final IMappingElement existingMappingElement : mappings )
        {
          final IXLinkedFeature linkedFeature = existingMappingElement.getLinkedFeature();
          final ZmlLink linkedTimeseries = existingMappingElement.getLinkedTimeseries();
          final String linkedHref = linkedTimeseries.getHref();

          final Feature linkedModelFeature = linkedFeature.getFeature();

          final MappingElementBean mappingBean = mappingElements.get( linkedModelFeature );
          if( mappingBean == null )
          {
            /* Remember if target does not exist any more -> show to user, mapping will be lost on safe */
            m_lostMappings.add( new MappingElementBean( existingMappingElement, null, linkedHref ) );
          }
          else
            mappingBean.setMappingElement( existingMappingElement );
        }
      }

      m_mappingElements.addAll( mappingElements.values() );
    }
    catch( final IllegalArgumentException e )
    {
      e.printStackTrace();
    }
    catch( final CoreException e )
    {
      e.printStackTrace();
    }
  }

  public Feature apply( final CommandableWorkspace workspace, final ITimeseriesMappingCollection timeseriesMappings ) throws Exception
  {
    final XMLGregorianCalendar lastModified = DateUtilities.toXMLGregorianCalendar( new Date() );
    setProperty( ITimeseriesMapping.PROPERTY_LAST_MODIFIED, lastModified );

    if( getFeature() == null )
    {
      /* Needs to create new feature */
      final IRelationType relation = (IRelationType) timeseriesMappings.getFeatureType().getProperty( ITimeseriesMappingCollection.MEMBER_TIMESERIES_MAPPING );
      final Map<QName, Object> properties = getProperties();

      /* Post the command. */
      final AddFeatureCommand command = new AddFeatureCommand( workspace, ITimeseriesMapping.FEATURE_TIMESERIES_MAPPING, timeseriesMappings, relation, -1, properties, null, -1 );
      workspace.postCommand( command );

      /* add new mappings */
      final ITimeseriesMapping newFeature = (ITimeseriesMapping) command.getNewFeature();
      final ICommand mappingCommand = applyMappings( newFeature );
      workspace.postCommand( mappingCommand );

      return newFeature;
    }

    /* apply all changes to existing feature */
    final ICommand changeCommands = applyChanges();
    workspace.postCommand( changeCommands );

    final ICommand mappingCommand = applyMappings( getFeature() );
    workspace.postCommand( mappingCommand );

    return getFeature();
  }

  private ICommand applyMappings( final ITimeseriesMapping parentMapping )
  {
    final CompositeCommand commands = new CompositeCommand( "apply mappings" ); //$NON-NLS-1$
    final GMLWorkspace workspace = parentMapping.getWorkspace();
    final IRelationType mappingRelation = (IRelationType) parentMapping.getFeatureType().getProperty( ITimeseriesMapping.MEMBER_MAPPING );

    final IFeatureType mappingElementType = GMLSchemaUtilities.getFeatureTypeQuiet( IMappingElement.FEATURE_MAPPING_ELEMENT );
    final IRelationType mappingElementLinkRelation = (IRelationType) mappingElementType.getProperty( IMappingElement.MEMBER_FEATURE_LINK );

    /* delete all lost mappings */
    for( final MappingElementBean lostMapping : m_lostMappings )
      commands.addCommand( new DeleteFeatureCommand( lostMapping.getMappingElement() ) );

    for( final MappingElementBean mapping : m_mappingElements )
    {
      final String href = mapping.getHref();

      final IMappingElement mappingElement = mapping.getMappingElement();
      if( mappingElement == null )
      {
        /* add new mapping element */
        final Feature modelFeature = mapping.getModelElement();

        final Map<QName, Object> properties = new HashMap<>();
        properties.put( IMappingElement.PROPERTY_TIMESERIES_LINK, href );

        final AddFeatureCommand addCommand = new AddFeatureCommand( workspace, IMappingElement.FEATURE_MAPPING_ELEMENT, parentMapping, mappingRelation, -1, properties, null, -1 );
        commands.addCommand( addCommand );

        final IFeatureProvider newElementProvider = addCommand;
        final AddLinkCommand linkCommand = new AddLinkCommand( newElementProvider, mappingElementLinkRelation, -1, modelFeature );
        commands.addCommand( linkCommand );
      }
      else
      {
        final FeatureChange change = new FeatureChange( mappingElement, IMappingElement.PROPERTY_TIMESERIES_LINK, href );
        commands.addCommand( new ChangeFeaturesCommand( workspace, change ) );
      }
    }

    return commands;
  }

  public MappingElementBean[] getLostMappings( )
  {
    return m_lostMappings.toArray( new MappingElementBean[m_lostMappings.size()] );
  }
}