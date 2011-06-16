package org.kalypso.model.wspm.pdb.db.mapping;

import java.math.BigDecimal;
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
import javax.persistence.UniqueConstraint;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

/**
 * CrossSectionPart generated by hbm2java
 */
@Entity
@Table(name = "cross_section_part", schema = "pdb_admin", uniqueConstraints = @UniqueConstraint(columnNames = { "name", "cross_section" }))
public class CrossSectionPart implements java.io.Serializable
{

  private BigDecimal id;

  private CrossSection crossSection;

  private String name;

  private Geometry line;

  private String category;

  private String description;

  private Set<Point> points = new HashSet<Point>( 0 );

  public CrossSectionPart( )
  {
  }

  public CrossSectionPart( final BigDecimal id, final CrossSection crossSection, final String name, final String category )
  {
    this.id = id;
    this.crossSection = crossSection;
    this.name = name;
    this.category = category;
  }

  public CrossSectionPart( final BigDecimal id, final CrossSection crossSection, final String name, final LineString line, final String category, final String description, final Set<Point> points )
  {
    this.id = id;
    this.crossSection = crossSection;
    this.name = name;
    this.line = line;
    this.category = category;
    this.description = description;
    this.points = points;
  }

  @Id
  @Column(name = "id", unique = true, nullable = false, precision = 20, scale = 0)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cspart_id_seq")
  @SequenceGenerator(name = "cspart_id_seq", sequenceName = "seq_pdb")
  public BigDecimal getId( )
  {
    return this.id;
  }

  public void setId( final BigDecimal id )
  {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cross_section", nullable = false)
  public CrossSection getCrossSection( )
  {
    return this.crossSection;
  }

  public void setCrossSection( final CrossSection crossSection )
  {
    this.crossSection = crossSection;
  }

  @Column(name = "name", nullable = false, length = 50)
  public String getName( )
  {
    return this.name;
  }

  public void setName( final String name )
  {
    this.name = name;
  }

  @Column(name = "line", columnDefinition = "Geometry")
  public Geometry getLine( )
  {
    return this.line;
  }

  public void setLine( final Geometry line )
  {
    this.line = line;

// if( line instanceof LineString )
// this.line = (LineString) line;
// else if( line == null )
// this.line = null;
// else if( line.isEmpty() )
// {
// // HACK: if geometry is empty convert to empty LineString so we always have a LineString
// // REMARK: this is needed, because we get an empty GeometryCollection from the db
// this.line = line.getFactory().createLineString( (Coordinate[]) null );
// }
// else
// throw new IllegalArgumentException( "'line' must be a LineString or Empty GeometryCollection" );
  }

  @Column(name = "category", nullable = false, length = 50)
  public String getCategory( )
  {
    return this.category;
  }

  public void setCategory( final String category )
  {
    this.category = category;
  }

  @Column(name = "description")
  public String getDescription( )
  {
    return this.description;
  }

  public void setDescription( final String description )
  {
    this.description = description;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "crossSectionPart")
  public Set<Point> getPoints( )
  {
    return this.points;
  }

  public void setPoints( final Set<Point> points )
  {
    this.points = points;
  }
}