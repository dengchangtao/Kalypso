package org.kalypso.model.wspm.pdb.db.mapping;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Point generated by hbm2java
 */
@Entity
@Table(name = "point", schema = "pdb")
public class Point implements java.io.Serializable
{

  private BigDecimal id;

  private CrossSectionPart crossSectionPart;

  private Roughness roughness;

  private Vegetation vegetation;

  private String name;

  private com.vividsolutions.jts.geom.Point location;

  private long consecutiveNum;

  private String code;

  private String hyk;

  private BigDecimal width;

  private BigDecimal height;

  private BigDecimal roughnessKValue;

  private BigDecimal roughnessKstValue;

  private BigDecimal vegetationDp;

  private BigDecimal vegetationAx;

  private BigDecimal vegetationAy;

  private String description;

  public Point( )
  {
  }

  public Point( final BigDecimal id, final CrossSectionPart crossSectionPart, final String name, final long consecutiveNum )
  {
    this.id = id;
    this.crossSectionPart = crossSectionPart;
    this.name = name;
    this.consecutiveNum = consecutiveNum;
  }

  public Point( final BigDecimal id, final CrossSectionPart crossSectionPart, final Roughness roughness, final Vegetation vegetation, final String name, final com.vividsolutions.jts.geom.Point location, final long consecutiveNum, final String code, final String hyk, final BigDecimal width, final BigDecimal height, final BigDecimal roughnessKValue, final BigDecimal roughnessKstValue, final BigDecimal vegetationDp, final BigDecimal vegetationAx, final BigDecimal vegetationAy, final String description )
  {
    this.id = id;
    this.crossSectionPart = crossSectionPart;
    this.roughness = roughness;
    this.vegetation = vegetation;
    this.name = name;
    this.location = location;
    this.consecutiveNum = consecutiveNum;
    this.code = code;
    this.hyk = hyk;
    this.width = width;
    this.height = height;
    this.roughnessKValue = roughnessKValue;
    this.roughnessKstValue = roughnessKstValue;
    this.vegetationDp = vegetationDp;
    this.vegetationAx = vegetationAx;
    this.vegetationAy = vegetationAy;
    this.description = description;
  }

  @Id
  @Column(name = "id", unique = true, nullable = false, precision = 20, scale = 0)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "point_id_seq")
  @SequenceGenerator(name = "point_id_seq", sequenceName = "seq_pdb")
  public BigDecimal getId( )
  {
    return this.id;
  }

  public void setId( final BigDecimal id )
  {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cross_section_part", nullable = false)
  public CrossSectionPart getCrossSectionPart( )
  {
    return this.crossSectionPart;
  }

  public void setCrossSectionPart( final CrossSectionPart crossSectionPart )
  {
    this.crossSectionPart = crossSectionPart;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumns({ @JoinColumn(name = "roughness_point_kind", referencedColumnName = "point_kind"), @JoinColumn(name = "roughness", referencedColumnName = "name") })
  public Roughness getRoughness( )
  {
    return this.roughness;
  }

  public void setRoughness( final Roughness roughness )
  {
    this.roughness = roughness;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumns({ @JoinColumn(name = "vegetation_point_kind", referencedColumnName = "point_kind"), @JoinColumn(name = "vegetation", referencedColumnName = "name") })
  public Vegetation getVegetation( )
  {
    return this.vegetation;
  }

  public void setVegetation( final Vegetation vegetation )
  {
    this.vegetation = vegetation;
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

  @Column(name = "location", columnDefinition = "Geometry")
  public com.vividsolutions.jts.geom.Point getLocation( )
  {
    return this.location;
  }

  public void setLocation( final com.vividsolutions.jts.geom.Point location )
  {
    this.location = location;
  }

  @Column(name = "consecutive_num", nullable = false, precision = 11, scale = 0)
  public long getConsecutiveNum( )
  {
    return this.consecutiveNum;
  }

  public void setConsecutiveNum( final long consecutiveNum )
  {
    this.consecutiveNum = consecutiveNum;
  }

  @Column(name = "code", length = 50)
  public String getCode( )
  {
    return this.code;
  }

  public void setCode( final String code )
  {
    this.code = code;
  }

  @Column(name = "hyk", length = 50)
  public String getHyk( )
  {
    return this.hyk;
  }

  public void setHyk( final String hyk )
  {
    this.hyk = hyk;
  }

  @Column(name = "width", precision = 8, scale = 4)
  public BigDecimal getWidth( )
  {
    return this.width;
  }

  public void setWidth( final BigDecimal width )
  {
    this.width = width;
  }

  @Column(name = "height", precision = 8, scale = 4)
  public BigDecimal getHeight( )
  {
    return this.height;
  }

  public void setHeight( final BigDecimal height )
  {
    this.height = height;
  }

  @Column(name = "roughness_k_value", precision = 8, scale = 1)
  public BigDecimal getRoughnessKValue( )
  {
    return this.roughnessKValue;
  }

  public void setRoughnessKValue( final BigDecimal roughnessKValue )
  {
    this.roughnessKValue = roughnessKValue;
  }

  @Column(name = "roughness_kst_value", precision = 8, scale = 1)
  public BigDecimal getRoughnessKstValue( )
  {
    return this.roughnessKstValue;
  }

  public void setRoughnessKstValue( final BigDecimal roughnessKstValue )
  {
    this.roughnessKstValue = roughnessKstValue;
  }

  @Column(name = "vegetation_dp", precision = 8, scale = 3)
  public BigDecimal getVegetationDp( )
  {
    return this.vegetationDp;
  }

  public void setVegetationDp( final BigDecimal vegetationDp )
  {
    this.vegetationDp = vegetationDp;
  }

  @Column(name = "vegetation_ax", precision = 8, scale = 3)
  public BigDecimal getVegetationAx( )
  {
    return this.vegetationAx;
  }

  public void setVegetationAx( final BigDecimal vegetationAx )
  {
    this.vegetationAx = vegetationAx;
  }

  @Column(name = "vegetation_ay", precision = 8, scale = 3)
  public BigDecimal getVegetationAy( )
  {
    return this.vegetationAy;
  }

  public void setVegetationAy( final BigDecimal vegetationAy )
  {
    this.vegetationAy = vegetationAy;
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

}
