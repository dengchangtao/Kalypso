package org.kalypso.model.wspm.pdb.db.mapping;

// default package
// Generated May 19, 2011 4:16:48 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Info generated by hbm2java
 */
@Entity
@Table(name = "INFO", schema = "PDB_ADMIN")
public class Info implements java.io.Serializable
{

  private String key;

  private String value;

  public Info( )
  {
  }

  public Info( String key )
  {
    this.key = key;
  }

  public Info( String key, String value )
  {
    this.key = key;
    this.value = value;
  }

  @Id
  @Column(name = "KEY", unique = true, nullable = false, length = 50)
  public String getKey( )
  {
    return this.key;
  }

  public void setKey( String key )
  {
    this.key = key;
  }

  @Column(name = "VALUE")
  public String getValue( )
  {
    return this.value;
  }

  public void setValue( String value )
  {
    this.value = value;
  }

}