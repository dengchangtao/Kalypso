package org.kalypso.model.wspm.pdb.db.mapping;

// default package
// Generated May 19, 2011 4:16:48 PM by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * PointKinds generated by hbm2java
 */
@Entity
@Table(name = "POINT_KINDS", schema = "PDB_ADMIN")
public class PointKinds implements java.io.Serializable
{

  private String pointKind;

  private String name;

  private String comment;

  private Set<Roughnesses> roughnesseses = new HashSet<Roughnesses>( 0 );

  private Set<Vegetations> vegetationses = new HashSet<Vegetations>( 0 );

  public PointKinds( )
  {
  }

  public PointKinds( String pointKind, String name )
  {
    this.pointKind = pointKind;
    this.name = name;
  }

  public PointKinds( String pointKind, String name, String comment, Set<Roughnesses> roughnesseses, Set<Vegetations> vegetationses )
  {
    this.pointKind = pointKind;
    this.name = name;
    this.comment = comment;
    this.roughnesseses = roughnesseses;
    this.vegetationses = vegetationses;
  }

  @Id
  @Column(name = "POINT_KIND", unique = true, nullable = false, length = 50)
  public String getPointKind( )
  {
    return this.pointKind;
  }

  public void setPointKind( String pointKind )
  {
    this.pointKind = pointKind;
  }

  @Column(name = "NAME", nullable = false, length = 100)
  public String getName( )
  {
    return this.name;
  }

  public void setName( String name )
  {
    this.name = name;
  }

  @Column(name = "Comment")
  public String getComment( )
  {
    return this.comment;
  }

  public void setComment( String comment )
  {
    this.comment = comment;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "pointKinds")
  public Set<Roughnesses> getRoughnesseses( )
  {
    return this.roughnesseses;
  }

  public void setRoughnesseses( Set<Roughnesses> roughnesseses )
  {
    this.roughnesseses = roughnesseses;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "pointKinds")
  public Set<Vegetations> getVegetationses( )
  {
    return this.vegetationses;
  }

  public void setVegetationses( Set<Vegetations> vegetationses )
  {
    this.vegetationses = vegetationses;
  }

}