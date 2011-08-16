package org.kalypso.model.wspm.pdb.db.mapping;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Vegetation generated by hbm2java
 */
@Entity
@Table(name = "vegetation", schema = "pdb")
public class Vegetation implements java.io.Serializable, Coefficient
{
  private VegetationId id;

  private PointKind pointKind;

  private BigDecimal dp;

  private BigDecimal ax;

  private BigDecimal ay;

  private String label;

  private String source;

  private String description;

  private Set<Point> points = new HashSet<Point>( 0 );

  public Vegetation( )
  {
  }

  public Vegetation( final VegetationId id, final PointKind pointKind, final BigDecimal dp, final BigDecimal ax, final BigDecimal ay )
  {
    this.id = id;
    this.pointKind = pointKind;
    this.dp = dp;
    this.ax = ax;
    this.ay = ay;
  }

  public Vegetation( final VegetationId id, final PointKind pointKind, final BigDecimal dp, final BigDecimal ax, final BigDecimal ay, final String label, final String source, final String description, final Set<Point> points )
  {
    this.id = id;
    this.pointKind = pointKind;
    this.dp = dp;
    this.ax = ax;
    this.ay = ay;
    this.label = label;
    this.source = source;
    this.description = description;
    this.points = points;
  }

  @Override
  @EmbeddedId
  @AttributeOverrides({ @AttributeOverride(name = "pointKind", column = @Column(name = "point_kind", nullable = false, length = 50)),
      @AttributeOverride(name = "name", column = @Column(name = "name", nullable = false, length = 50)) })
  public VegetationId getId( )
  {
    return this.id;
  }

  public void setId( final VegetationId id )
  {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "point_kind", nullable = false, insertable = false, updatable = false)
  public PointKind getPointKind( )
  {
    return this.pointKind;
  }

  public void setPointKind( final PointKind pointKind )
  {
    this.pointKind = pointKind;
  }

  @Column(name = "dp", nullable = false, precision = 8, scale = 3)
  public BigDecimal getDp( )
  {
    return this.dp;
  }

  public void setDp( final BigDecimal dp )
  {
    this.dp = dp;
  }

  @Column(name = "ax", nullable = false, precision = 8, scale = 3)
  public BigDecimal getAx( )
  {
    return this.ax;
  }

  public void setAx( final BigDecimal ax )
  {
    this.ax = ax;
  }

  @Column(name = "ay", nullable = false, precision = 8, scale = 3)
  public BigDecimal getAy( )
  {
    return this.ay;
  }

  public void setAy( final BigDecimal ay )
  {
    this.ay = ay;
  }

  @Column(name = "label", length = 100)
  public String getLabel( )
  {
    return this.label;
  }

  public void setLabel( final String label )
  {
    this.label = label;
  }

  @Column(name = "source", length = 255)
  public String getSource( )
  {
    return this.source;
  }

  public void setSource( final String source )
  {
    this.source = source;
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

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "vegetation")
  public Set<Point> getPoints( )
  {
    return this.points;
  }

  public void setPoints( final Set<Point> points )
  {
    this.points = points;
  }

}
