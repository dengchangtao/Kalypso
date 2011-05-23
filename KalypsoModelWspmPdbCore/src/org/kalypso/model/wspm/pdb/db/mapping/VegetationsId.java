package org.kalypso.model.wspm.pdb.db.mapping;

// default package
// Generated May 19, 2011 4:16:48 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * VegetationsId generated by hbm2java
 */
@Embeddable
public class VegetationsId implements java.io.Serializable
{

  private String pointKind;

  private String vegetation;

  public VegetationsId( )
  {
  }

  public VegetationsId( String pointKind, String vegetation )
  {
    this.pointKind = pointKind;
    this.vegetation = vegetation;
  }

  @Column(name = "POINT_KIND", nullable = false, length = 50)
  public String getPointKind( )
  {
    return this.pointKind;
  }

  public void setPointKind( String pointKind )
  {
    this.pointKind = pointKind;
  }

  @Column(name = "VEGETATION", nullable = false, length = 50)
  public String getVegetation( )
  {
    return this.vegetation;
  }

  public void setVegetation( String vegetation )
  {
    this.vegetation = vegetation;
  }

  public boolean equals( Object other )
  {
    if( (this == other) )
      return true;
    if( (other == null) )
      return false;
    if( !(other instanceof VegetationsId) )
      return false;
    VegetationsId castOther = (VegetationsId) other;

    return ((this.getPointKind() == castOther.getPointKind()) || (this.getPointKind() != null && castOther.getPointKind() != null && this.getPointKind().equals( castOther.getPointKind() )))
        && ((this.getVegetation() == castOther.getVegetation()) || (this.getVegetation() != null && castOther.getVegetation() != null && this.getVegetation().equals( castOther.getVegetation() )));
  }

  public int hashCode( )
  {
    int result = 17;

    result = 37 * result + (getPointKind() == null ? 0 : this.getPointKind().hashCode());
    result = 37 * result + (getVegetation() == null ? 0 : this.getVegetation().hashCode());
    return result;
  }

}