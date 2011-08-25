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

import org.kalypso.contribs.java.lang.NumberUtils;

/**
 * Vegetation generated by hbm2java
 */
@Entity
@Table(name = "vegetation", schema = "pdb")
public class Vegetation implements java.io.Serializable, Coefficient, Comparable<Vegetation>
{
  private VegetationId m_id;

  private PointKind m_pointKind;

  private BigDecimal m_dp;

  private BigDecimal m_ax;

  private BigDecimal m_ay;

  private String m_label;

  private String m_source;

  private String m_description;

  private Set<Point> m_points = new HashSet<Point>( 0 );

  public Vegetation( )
  {
  }

  public Vegetation( final VegetationId id, final PointKind pointKind, final BigDecimal dp, final BigDecimal ax, final BigDecimal ay )
  {
    m_id = id;
    m_pointKind = pointKind;
    m_dp = dp;
    m_ax = ax;
    m_ay = ay;
  }

  public Vegetation( final VegetationId id, final PointKind pointKind, final BigDecimal dp, final BigDecimal ax, final BigDecimal ay, final String label, final String source, final String description, final Set<Point> points )
  {
    m_id = id;
    m_pointKind = pointKind;
    m_dp = dp;
    m_ax = ax;
    m_ay = ay;
    m_label = label;
    m_source = source;
    m_description = description;
    m_points = points;
  }

  @Override
  public String toString( )
  {
    return String.format( "%s - %s", m_id.getName(), m_label ); //$NON-NLS-1$
  }

  @Override
  @EmbeddedId
  @AttributeOverrides({ @AttributeOverride(name = "pointKind", column = @Column(name = "point_kind", nullable = false, length = 50)),
    @AttributeOverride(name = "name", column = @Column(name = "name", nullable = false, length = 50)) })
  public VegetationId getId( )
  {
    return m_id;
  }

  public void setId( final VegetationId id )
  {
    m_id = id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "point_kind", nullable = false, insertable = false, updatable = false)
  public PointKind getPointKind( )
  {
    return m_pointKind;
  }

  public void setPointKind( final PointKind pointKind )
  {
    m_pointKind = pointKind;
  }

  @Column(name = "dp", nullable = false, precision = 8, scale = 3)
  public BigDecimal getDp( )
  {
    return m_dp;
  }

  public void setDp( final BigDecimal dp )
  {
    m_dp = dp;
  }

  @Column(name = "ax", nullable = false, precision = 8, scale = 3)
  public BigDecimal getAx( )
  {
    return m_ax;
  }

  public void setAx( final BigDecimal ax )
  {
    m_ax = ax;
  }

  @Column(name = "ay", nullable = false, precision = 8, scale = 3)
  public BigDecimal getAy( )
  {
    return m_ay;
  }

  public void setAy( final BigDecimal ay )
  {
    m_ay = ay;
  }

  @Column(name = "label", length = 100)
  public String getLabel( )
  {
    return m_label;
  }

  public void setLabel( final String label )
  {
    m_label = label;
  }

  @Column(name = "source", length = 255)
  public String getSource( )
  {
    return m_source;
  }

  public void setSource( final String source )
  {
    m_source = source;
  }

  @Column(name = "description")
  public String getDescription( )
  {
    return m_description;
  }

  public void setDescription( final String description )
  {
    m_description = description;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "vegetation")
  public Set<Point> getPoints( )
  {
    return m_points;
  }

  public void setPoints( final Set<Point> points )
  {
    m_points = points;
  }

  @Override
  public int compareTo( final Vegetation o )
  {
    final String name = m_id.getName();
    final String otherName = o.m_id.getName();

    final Integer nameValue = NumberUtils.parseQuietInteger( name );
    final Integer otherNameValue = NumberUtils.parseQuietInteger( otherName );
    if( nameValue != null && otherNameValue != null )
      return nameValue.compareTo( otherNameValue );

    return name.compareToIgnoreCase( otherName );
  }
}
