package org.kalypso.model.wspm.pdb.db.mapping;

// default package
// Generated May 19, 2011 4:16:48 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.LineString;

/**
 * CrossSections generated by hbm2java
 */
@Entity
@Table(name = "CROSS_SECTIONS", schema = "PDB_ADMIN")
public class CrossSections implements java.io.Serializable
{
  private String crossSection;

  private States states;

  private WaterBodies waterBodies;

  private LineString line;

  private BigDecimal station;

  private Date creationDate;

  private Date editingDate;

  private String editingUser;

  private Date measurementDate;

  private String description;

  private Set<CrossSectionParts> crossSectionPartses = new HashSet<CrossSectionParts>( 0 );

  public CrossSections( )
  {
  }

  public CrossSections( final String crossSection, final States states, final WaterBodies waterBodies, final BigDecimal station, final Date creationDate, final Date editingDate, final String editingUser )
  {
    this.crossSection = crossSection;
    this.states = states;
    this.waterBodies = waterBodies;
    this.station = station;
    this.creationDate = creationDate;
    this.editingDate = editingDate;
    this.editingUser = editingUser;
  }

  public CrossSections( final String crossSection, final States states, final WaterBodies waterBodies, final LineString line, final BigDecimal station, final Date creationDate, final Date editingDate, final String editingUser, final Date measurementDate, final String comment, final Set<CrossSectionParts> crossSectionPartses )
  {
    this.crossSection = crossSection;
    this.states = states;
    this.waterBodies = waterBodies;
    this.line = line;
    this.station = station;
    this.creationDate = creationDate;
    this.editingDate = editingDate;
    this.editingUser = editingUser;
    this.measurementDate = measurementDate;
    this.description = comment;
    this.crossSectionPartses = crossSectionPartses;
  }

  @Id
  @Column(name = "CROSS_SECTION", unique = true, nullable = false, length = 50)
  public String getCrossSection( )
  {
    return this.crossSection;
  }

  public void setCrossSection( final String crossSection )
  {
    this.crossSection = crossSection;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "STATE", nullable = false)
  public States getStates( )
  {
    return this.states;
  }

  public void setStates( final States states )
  {
    this.states = states;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "WATER_BODY", nullable = false)
  public WaterBodies getWaterBodies( )
  {
    return this.waterBodies;
  }

  public void setWaterBodies( final WaterBodies waterBodies )
  {
    this.waterBodies = waterBodies;
  }

  @Column(name = "LINE")
  @Type(type = "org.hibernatespatial.GeometryUserType")
  public LineString getLine( )
  {
    return this.line;
  }

  public void setLine( final LineString line )
  {
    this.line = line;
  }

  @Column(name = "STATION", nullable = false, precision = 22, scale = 0)
  public BigDecimal getStation( )
  {
    return this.station;
  }

  public void setStation( final BigDecimal station )
  {
    this.station = station;
  }

  @Temporal(TemporalType.DATE)
  @Column(name = "CREATION_DATE", nullable = false, length = 7)
  public Date getCreationDate( )
  {
    return this.creationDate;
  }

  public void setCreationDate( final Date creationDate )
  {
    this.creationDate = creationDate;
  }

  @Temporal(TemporalType.DATE)
  @Column(name = "EDITING_DATE", nullable = false, length = 7)
  public Date getEditingDate( )
  {
    return this.editingDate;
  }

  public void setEditingDate( final Date editingDate )
  {
    this.editingDate = editingDate;
  }

  @Column(name = "EDITING_USER", nullable = false, length = 50)
  public String getEditingUser( )
  {
    return this.editingUser;
  }

  public void setEditingUser( final String editingUser )
  {
    this.editingUser = editingUser;
  }

  @Temporal(TemporalType.DATE)
  @Column(name = "MEASUREMENT_DATE", length = 7)
  public Date getMeasurementDate( )
  {
    return this.measurementDate;
  }

  public void setMeasurementDate( final Date measurementDate )
  {
    this.measurementDate = measurementDate;
  }

  @Column(name = "description")
  public String getComment( )
  {
    return this.description;
  }

  public void setComment( final String comment )
  {
    this.description = comment;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "crossSections")
  public Set<CrossSectionParts> getCrossSectionPartses( )
  {
    return this.crossSectionPartses;
  }

  public void setCrossSectionPartses( final Set<CrossSectionParts> crossSectionPartses )
  {
    this.crossSectionPartses = crossSectionPartses;
  }
}