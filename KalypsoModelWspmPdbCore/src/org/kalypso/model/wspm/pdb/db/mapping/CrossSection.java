package org.kalypso.model.wspm.pdb.db.mapping;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

/**
 * CrossSection generated by hbm2java
 */
@Entity
@Table( name = "cross_section", schema = "pdb", uniqueConstraints = @UniqueConstraint( columnNames = { "name", "state" } ) )
public class CrossSection implements java.io.Serializable, IDocumentContainer
{
  private static final String COLUMN_WATER_ID = "water_body"; //$NON-NLS-1$

  public static final String PROPERTY_WATER_BODY = "waterBody"; //$NON-NLS-1$

  //public static final String PROPERTY_STATION = "station"; //$NON-NLS-1$

  private BigDecimal m_id;

  private WaterBody m_waterBody;

  private State m_state;

  private String m_name;

  private Geometry m_line;

  private BigDecimal m_station;

  private Date m_creationDate;

  private Date m_editingDate;

  private String m_editingUser;

  private Date m_measurementDate;

  private String m_description;

  private Set<Document> m_documents = new HashSet<>( 0 );

  private Set<CrossSectionPart> m_crossSectionParts = new HashSet<>( 0 );

  public CrossSection( )
  {
  }

  public CrossSection( final BigDecimal id, final WaterBody waterBody, final State state, final String name, final BigDecimal station, final Date creationDate, final Date editingDate, final String editingUser )
  {
    m_id = id;
    m_waterBody = waterBody;
    m_state = state;
    m_name = name;
    m_station = station;
    m_creationDate = creationDate;
    m_editingDate = editingDate;
    m_editingUser = editingUser;
  }

  public CrossSection( final BigDecimal id, final WaterBody waterBody, final State state, final String name, final LineString line, final BigDecimal station, final Date creationDate, final Date editingDate, final String editingUser, final Date measurementDate, final String description, final Set<Document> documents, final Set<CrossSectionPart> crossSectionParts )
  {
    m_id = id;
    m_waterBody = waterBody;
    m_state = state;
    m_name = name;
    m_line = line;
    m_station = station;
    m_creationDate = creationDate;
    m_editingDate = editingDate;
    m_editingUser = editingUser;
    m_measurementDate = measurementDate;
    m_description = description;
    m_documents = documents;
    m_crossSectionParts = crossSectionParts;
  }

  @Id
  @Column( name = "id", unique = true, nullable = false, precision = 20, scale = 0 )
  @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "crosssection_id_seq" )
  @SequenceGenerator( name = "crosssection_id_seq", sequenceName = "pdb.seq_pdb" )
  public BigDecimal getId( )
  {
    return m_id;
  }

  public void setId( final BigDecimal id )
  {
    m_id = id;
  }

  @ManyToOne( fetch = FetchType.LAZY )
  @JoinColumn( name = COLUMN_WATER_ID, nullable = false )
  public WaterBody getWaterBody( )
  {
    return m_waterBody;
  }

  public void setWaterBody( final WaterBody waterBody )
  {
    m_waterBody = waterBody;
  }

  @ManyToOne( fetch = FetchType.LAZY )
  @JoinColumn( name = "state", nullable = false )
  public State getState( )
  {
    return m_state;
  }

  public void setState( final State state )
  {
    m_state = state;
  }

  @Override
  @Column( name = "name", nullable = false, length = 50 )
  public String getName( )
  {
    return m_name;
  }

  public void setName( final String name )
  {
    m_name = name;
  }

  @Column( name = "line", columnDefinition = "Geometry" )
  public Geometry getLine( )
  {
    return m_line;
  }

  public void setLine( final Geometry line )
  {
    m_line = line;
  }

  @Column( name = "station", nullable = false, precision = 8, scale = 1 )
  public BigDecimal getStation( )
  {
    if( m_station == null )
      return null;

    // REMARK: necessary, as hibernate does it not (at least with oracle), we rely on it.
    return m_station.setScale( 1, BigDecimal.ROUND_HALF_UP );
  }

  public void setStation( final BigDecimal station )
  {
    m_station = station;
  }

  @Temporal( TemporalType.TIMESTAMP )
  @Column( name = "creation_date", nullable = false, length = 22 )
  public Date getCreationDate( )
  {
    return m_creationDate;
  }

  public void setCreationDate( final Date creationDate )
  {
    m_creationDate = creationDate;
  }

  @Temporal( TemporalType.TIMESTAMP )
  @Column( name = "editing_date", nullable = false, length = 22 )
  public Date getEditingDate( )
  {
    return m_editingDate;
  }

  public void setEditingDate( final Date editingDate )
  {
    m_editingDate = editingDate;
  }

  @Column( name = "editing_user", nullable = false, length = 50 )
  public String getEditingUser( )
  {
    return m_editingUser;
  }

  public void setEditingUser( final String editingUser )
  {
    m_editingUser = editingUser;
  }

  @Temporal( TemporalType.TIMESTAMP )
  @Column( name = "measurement_date", length = 22 )
  public Date getMeasurementDate( )
  {
    return m_measurementDate;
  }

  public void setMeasurementDate( final Date measurementDate )
  {
    m_measurementDate = measurementDate;
  }

  @Column( name = "description" )
  public String getDescription( )
  {
    return m_description;
  }

  public void setDescription( final String description )
  {
    m_description = description;
  }

  @Override
  @OneToMany( fetch = FetchType.LAZY, mappedBy = "crossSection" )
  public Set<Document> getDocuments( )
  {
    return m_documents;
  }

  @Override
  public void setDocuments( final Set<Document> documents )
  {
    m_documents = documents;
  }

  @OneToMany( fetch = FetchType.LAZY, mappedBy = "crossSection" )
  public Set<CrossSectionPart> getCrossSectionParts( )
  {
    return m_crossSectionParts;
  }

  public void setCrossSectionParts( final Set<CrossSectionPart> crossSectionParts )
  {
    m_crossSectionParts = crossSectionParts;
  }

  // Helper accessors

  public CrossSectionPart findPartByCategory( final String category )
  {
    final Set<CrossSectionPart> parts = getCrossSectionParts();
    for( final CrossSectionPart part : parts )
    {
      final String partCategory = part.getCrossSectionPartType().getCategory();
      if( category.equals( partCategory ) )
        return part;
    }

    return null;
  }
}
