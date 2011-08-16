package org.kalypso.model.wspm.pdb.db.mapping;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * PointKind generated by hbm2java
 */
@Entity
@Table(name = "point_kind", schema = "pdb")
public class PointKind implements java.io.Serializable
{

  private String name;

  private String label;

  private String description;

  private Set<Roughness> roughnesses = new HashSet<Roughness>( 0 );

  private Set<Vegetation> vegetations = new HashSet<Vegetation>( 0 );

  public PointKind( )
  {
  }

  public PointKind( final String name, final String label )
  {
    this.name = name;
    this.label = label;
  }

  public PointKind( final String name, final String label, final String description, final Set<Roughness> roughnesses, final Set<Vegetation> vegetations )
  {
    this.name = name;
    this.label = label;
    this.description = description;
    this.roughnesses = roughnesses;
    this.vegetations = vegetations;
  }

  @Id
  @Column(name = "name", unique = true, nullable = false, length = 50)
  public String getName( )
  {
    return this.name;
  }

  public void setName( final String name )
  {
    this.name = name;
  }

  @Column(name = "label", nullable = false, length = 100)
  public String getLabel( )
  {
    return this.label;
  }

  public void setLabel( final String label )
  {
    this.label = label;
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

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "pointKind")
  public Set<Roughness> getRoughnesses( )
  {
    return this.roughnesses;
  }

  public void setRoughnesses( final Set<Roughness> roughnesses )
  {
    this.roughnesses = roughnesses;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "pointKind")
  public Set<Vegetation> getVegetations( )
  {
    return this.vegetations;
  }

  public void setVegetations( final Set<Vegetation> vegetations )
  {
    this.vegetations = vegetations;
  }

}
