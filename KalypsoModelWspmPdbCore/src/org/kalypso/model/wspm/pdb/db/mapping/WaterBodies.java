package org.kalypso.model.wspm.pdb.db.mapping;

// default package
// Generated May 19, 2011 4:16:48 PM by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.kalypso.commons.java.util.AbstractModelObject;
import org.kalypso.model.wspm.pdb.db.constants.WaterBodiesConstants;

import com.vividsolutions.jts.geom.Geometry;

/**
 * WaterBodies generated by hbm2java
 */
@Entity
@Table(name = "WATER_BODIES", schema = "PDB_ADMIN")
public class WaterBodies extends AbstractModelObject implements java.io.Serializable, WaterBodiesConstants
{
  public static final int NAME_LIMIT = 50;

  public static final int WATERBODY_LIMIT = 50;

  public static final int COMMENT_LIMIT = 256;

  private String waterBody;

  private Geometry riverline;

  private String name;

  private String comment;

  private Set<Events> eventses = new HashSet<Events>( 0 );

  private Set<CrossSections> crossSectionses = new HashSet<CrossSections>( 0 );

  public WaterBodies( )
  {
  }

  public WaterBodies( final String waterBody, final String name )
  {
    this.waterBody = waterBody;
    this.name = name;
  }

  public WaterBodies( final String waterBody, final Geometry riverline, final String name, final String comment, final Set<Events> eventses, final Set<CrossSections> crossSectionses )
  {
    this.waterBody = waterBody;
    this.riverline = riverline;
    this.name = name;
    this.comment = comment;
    this.eventses = eventses;
    this.crossSectionses = crossSectionses;
  }

  public WaterBodies( final WaterBodies selectedItem )
  {
    // TODO Auto-generated constructor stub
  }

  @Id
  @Column(name = "WATER_BODY", unique = true, nullable = false, length = 100)
  public String getWaterBody( )
  {
    return this.waterBody;
  }

  public void setWaterBody( final String waterBody )
  {
    final Object oldValue = this.waterBody;

    this.waterBody = waterBody;

    firePropertyChange( PROPERTY_WATERBODY, oldValue, waterBody );
  }

  @Column(name = "RIVERLINE")
  public Serializable getRiverline( )
  {
    return this.riverline;
  }

  public void setRiverline( final Geometry riverline )
  {
    final Object oldValue = this.riverline;

    this.riverline = riverline;

    firePropertyChange( PROPERTY_RIVERLINE, oldValue, riverline );
  }

  @Column(name = "NAME", nullable = false, length = 100)
  public String getName( )
  {
    return this.name;
  }

  public void setName( final String name )
  {
    final Object oldValue = this.name;

    this.name = name;

    firePropertyChange( PROPERTY_NAME, oldValue, name );
  }

  @Column(name = "description")
  public String getComment( )
  {
    return this.comment;
  }

  public void setComment( final String comment )
  {
    final Object oldValue = this.comment;

    this.comment = comment;

    firePropertyChange( PROPERTY_COMMENT, oldValue, comment );
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "waterBodies")
  public Set<Events> getEventses( )
  {
    return this.eventses;
  }

  public void setEventses( final Set<Events> eventses )
  {
    final Object oldValue = this.eventses;

    this.eventses = eventses;

    firePropertyChange( PROPERTY_EVENTSES, oldValue, eventses );
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "waterBodies")
  public Set<CrossSections> getCrossSectionses( )
  {
    return this.crossSectionses;
  }

  public void setCrossSectionses( final Set<CrossSections> crossSectionses )
  {
    final Object oldValue = this.crossSectionses;

    this.crossSectionses = crossSectionses;

    firePropertyChange( PROPERTY_CROSSSECTIONSES, oldValue, crossSectionses );
  }
}