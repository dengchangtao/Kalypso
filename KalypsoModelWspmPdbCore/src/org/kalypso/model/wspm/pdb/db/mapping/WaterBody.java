package org.kalypso.model.wspm.pdb.db.mapping;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.kalypso.commons.java.util.AbstractModelObject;
import org.kalypso.model.wspm.pdb.db.constants.WaterBodyConstants;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;

/**
 * WaterBody generated by hbm2java
 */
@Entity
@Table(name = "water_body", schema = "pdb", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class WaterBody extends AbstractModelObject implements java.io.Serializable, WaterBodyConstants
{
  private BigDecimal m_id;

  private String m_name = StringUtils.EMPTY;

  private Geometry m_riverline;

  private String m_label = StringUtils.EMPTY;

  private STATIONING_DIRECTION m_directionOfStationing = STATIONING_DIRECTION.upstream;

  private String m_description = StringUtils.EMPTY;

  private Integer m_rank;

  private Set<Event> m_events = new HashSet<Event>( 0 );

  private Set<CrossSection> m_crossSections = new HashSet<CrossSection>( 0 );

  private Set<Document> m_documents = new HashSet<Document>( 0 );

  public WaterBody( )
  {
  }

  public WaterBody( final BigDecimal id, final String name, final String label, final STATIONING_DIRECTION directionOfStationing )
  {
    m_id = id;
    m_name = name;
    m_label = label;
    m_directionOfStationing = directionOfStationing;
  }

  public WaterBody( final BigDecimal id, final String name, final Geometry riverline, final String label, final STATIONING_DIRECTION directionOfStationing, final Integer rank, final String description, final Set<Event> events, final Set<CrossSection> crossSections, final Set<Document> documents )
  {
    m_id = id;
    m_name = name;
    m_riverline = riverline;
    m_label = label;
    m_directionOfStationing = directionOfStationing;
    m_rank = rank;
    m_description = description;
    m_events = events;
    m_crossSections = crossSections;
    m_documents = documents;
  }

  @Id
  @Column(name = "id", unique = true, nullable = false, precision = 20, scale = 0)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "waterbody_id_seq")
  @SequenceGenerator(name = "waterbody_id_seq", sequenceName = "pdb.seq_pdb")
  public BigDecimal getId( )
  {
    return m_id;
  }

  public void setId( final BigDecimal id )
  {
    final Object oldValue = m_id;

    m_id = id;

    firePropertyChange( PROPERTY_ID, oldValue, id );
  }

  @Column(name = "name", unique = true, nullable = false, length = 100)
  public String getName( )
  {
    return m_name;
  }

  public void setName( final String name )
  {
    final Object oldValue = m_name;

    m_name = name;

    firePropertyChange( PROPERTY_NAME, oldValue, name );
  }

  @Column(name = "riverline", columnDefinition = "Geometry")
  public Geometry getRiverline( )
  {
    return m_riverline;
  }

  public void setRiverline( final Geometry riverline )
  {
    final Object oldValue = m_riverline;

    m_riverline = riverline;

    firePropertyChange( PROPERTY_RIVERLINE, oldValue, riverline );
  }

  @Column(name = "label", nullable = false, length = 100)
  public String getLabel( )
  {
    return m_label;
  }

  public void setLabel( final String label )
  {
    final Object oldValue = m_label;

    m_label = label;

    firePropertyChange( PROPERTY_LABEL, oldValue, label );
  }

  @Column(name = "direction_of_stationing", nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  public STATIONING_DIRECTION getDirectionOfStationing( )
  {
    return m_directionOfStationing;
  }

  public void setDirectionOfStationing( final STATIONING_DIRECTION directionOfStationing )
  {
    final Object oldValue = m_directionOfStationing;

    m_directionOfStationing = directionOfStationing;

    firePropertyChange( PROPERTY_DIRECTION_OF_STATIONING, oldValue, directionOfStationing );
  }

  @Column(name = "rank")
  public Integer getRank( )
  {
    return m_rank;
  }

  public void setRank( final Integer rank )
  {
    final Object oldValue = m_rank;

    m_rank = rank;

    firePropertyChange( PROPERTY_RANK, oldValue, rank );
  }

  @Column(name = "description")
  public String getDescription( )
  {
    return m_description;
  }

  public void setDescription( final String description )
  {
    final Object oldValue = m_description;

    m_description = description;

    firePropertyChange( PROPERTY_DESCRIPTION, oldValue, description );
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "waterBody")
  public Set<Event> getEvents( )
  {
    return m_events;
  }

  public void setEvents( final Set<Event> events )
  {
    m_events = events;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "waterBody")
  public Set<CrossSection> getCrossSections( )
  {
    return m_crossSections;
  }

  public void setCrossSections( final Set<CrossSection> crossSections )
  {
    m_crossSections = crossSections;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "waterBody")
  public Set<Document> getDocuments( )
  {
    return m_documents;
  }

  public void setDocuments( final Set<Document> documents )
  {
    m_documents = documents;
  }

  @Transient
  public LineString getRiverlineAsLine( )
  {
    if( m_riverline instanceof LineString )
      return (LineString) m_riverline;

    if( m_riverline == null )
      return null;

    if( m_riverline instanceof GeometryCollection )
    {
      Assert.isTrue( m_riverline.isEmpty() );
      return null;
    }

    return null;
  }

  /**
   * Copy all (simple) properties of the given water body into this instance.
   */
  public void setAll( final WaterBody waterBody )
  {
    setDescription( waterBody.getDescription() );
    setDirectionOfStationing( waterBody.getDirectionOfStationing() );
    setLabel( waterBody.getLabel() );
    setName( waterBody.getName() );
    setRank( waterBody.getRank() );
    setRiverline( waterBody.getRiverline() );
  }
}