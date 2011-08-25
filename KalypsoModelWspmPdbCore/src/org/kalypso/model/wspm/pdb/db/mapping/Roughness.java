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
 * Roughness generated by hbm2java
 */
@Entity
@Table(name = "roughness", schema = "pdb")
public class Roughness implements java.io.Serializable, Coefficient, Comparable<Roughness>
{
  private RoughnessId m_id;

  private PointKind m_pointKind;

  private BigDecimal m_KValue;

  private BigDecimal m_kstValue;

  private String m_label;

  private String m_source;

  private String m_validity;

  private String m_description;

  private Set<Point> m_points = new HashSet<Point>( 0 );

  public Roughness( )
  {
  }

  public Roughness( final RoughnessId id, final PointKind pointKind )
  {
    m_id = id;
    m_pointKind = pointKind;
  }

  @Override
  public String toString( )
  {
    return String.format( "%s - %s", m_id.getName(), m_label ); //$NON-NLS-1$
  }

  public Roughness( final RoughnessId id, final PointKind pointKind, final BigDecimal KValue, final BigDecimal kstValue, final String label, final String source, final String validity, final String description, final Set<Point> points )
  {
    m_id = id;
    m_pointKind = pointKind;
    m_KValue = KValue;
    m_kstValue = kstValue;
    m_label = label;
    m_source = source;
    m_validity = validity;
    m_description = description;
    m_points = points;
  }

  @Override
  @EmbeddedId
  @AttributeOverrides({ @AttributeOverride(name = "pointKind", column = @Column(name = "point_kind", nullable = false, length = 50)),
    @AttributeOverride(name = "name", column = @Column(name = "name", nullable = false, length = 50)) })
  public RoughnessId getId( )
  {
    return m_id;
  }

  public void setId( final RoughnessId id )
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

  @Column(name = "k_value", precision = 8, scale = 1)
  public BigDecimal getKValue( )
  {
    return m_KValue;
  }

  public void setKValue( final BigDecimal KValue )
  {
    m_KValue = KValue;
  }

  @Column(name = "kst_value", precision = 8, scale = 1)
  public BigDecimal getKstValue( )
  {
    return m_kstValue;
  }

  public void setKstValue( final BigDecimal kstValue )
  {
    m_kstValue = kstValue;
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

  @Column(name = "validity", length = 255)
  public String getValidity( )
  {
    return m_validity;
  }

  public void setValidity( final String validity )
  {
    m_validity = validity;
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

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "roughness")
  public Set<Point> getPoints( )
  {
    return m_points;
  }

  public void setPoints( final Set<Point> points )
  {
    m_points = points;
  }

  @Override
  public int compareTo( final Roughness o )
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
