package org.kalypso.model.wspm.pdb.db.mapping;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.kalypso.commons.java.util.AbstractModelObject;
import org.kalypso.model.wspm.pdb.db.constants.DocumentConstants;

/**
 * Document generated by hbm2java
 */
@Entity
@Table(name = "document", schema = "pdb", uniqueConstraints = @UniqueConstraint(columnNames = "filename"))
public class Document extends AbstractModelObject implements Serializable, DocumentConstants, IElementWithDates
{

  private BigDecimal id;

  private String name;

  private com.vividsolutions.jts.geom.Point location;

  private String filename;

  private String mimetype;

  private Date creationDate;

  private Date editingDate;

  private String editingUser;

  private Date measurementDate;

  private BigDecimal shotdirection;

  private BigDecimal viewangle;

  private String description;

  private CrossSection crossSection;

  private WaterBody waterBody;

  private State state;

  public Document( )
  {
  }

  public Document( final BigDecimal id, final String name, final String filename, final Date creationDate, final Date editingDate, final String editingUser )
  {
    this.id = id;
    this.name = name;
    this.filename = filename;
    this.creationDate = creationDate;
    this.editingDate = editingDate;
    this.editingUser = editingUser;
  }

  public Document( final BigDecimal id, final String name, final com.vividsolutions.jts.geom.Point location, final String filename, final String mimetype, final Date creationDate, final Date editingDate, final String editingUser, final Date measurementDate, final BigDecimal shotdirection, final BigDecimal viewangle, final String description, final CrossSection crossSection, final WaterBody waterBody, final State state )
  {
    this.id = id;
    this.waterBody = waterBody;
    this.crossSection = crossSection;
    this.state = state;
    this.name = name;
    this.location = location;
    this.filename = filename;
    this.mimetype = mimetype;
    this.creationDate = creationDate;
    this.editingDate = editingDate;
    this.editingUser = editingUser;
    this.measurementDate = measurementDate;
    this.shotdirection = shotdirection;
    this.viewangle = viewangle;
    this.description = description;
  }

  @Id
  @Column(name = "id", unique = true, nullable = false, precision = 20, scale = 0)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_id_seq")
  @SequenceGenerator(name = "document_id_seq", sequenceName = "pdb.seq_pdb")
  public BigDecimal getId( )
  {
    return this.id;
  }

  public void setId( final BigDecimal id )
  {
    this.id = id;
  }

  @Column(name = "name", nullable = false, length = 100)
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

  @Column(name = "filename", unique = true, nullable = false, length = 2048)
  public String getFilename( )
  {
    return this.filename;
  }

  public void setFilename( final String filename )
  {
    this.filename = filename;
  }

  @Column(name = "mimetype", length = 50)
  public String getMimetype( )
  {
    return this.mimetype;
  }

  public void setMimetype( final String mimetype )
  {
    this.mimetype = mimetype;
  }

  @Override
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "creation_date", nullable = false, length = 22)
  public Date getCreationDate( )
  {
    return this.creationDate;
  }

  public void setCreationDate( final Date creationDate )
  {
    final Object oldValue = this.creationDate;

    this.creationDate = creationDate;

    firePropertyChange( PROPERTY_CREATIONDATE, oldValue, creationDate );
  }

  @Override
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "editing_date", nullable = false, length = 22)
  public Date getEditingDate( )
  {
    return this.editingDate;
  }

  public void setEditingDate( final Date editingDate )
  {
    final Object oldValue = this.editingDate;

    this.editingDate = editingDate;

    firePropertyChange( PROPERTY_EDITINGDATE, oldValue, editingDate );
  }

  @Override
  @Column(name = "editing_user", nullable = false, length = 50)
  public String getEditingUser( )
  {
    return this.editingUser;
  }

  public void setEditingUser( final String editingUser )
  {
    final Object oldValue = this.editingUser;

    this.editingUser = editingUser;

    firePropertyChange( PROPERTY_EDITINGUSER, oldValue, editingUser );
  }

  @Override
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "measurement_date", length = 22)
  public Date getMeasurementDate( )
  {
    return this.measurementDate;
  }

  public void setMeasurementDate( final Date measurementDate )
  {
    final Object oldValue = this.measurementDate;

    this.measurementDate = measurementDate;

    firePropertyChange( PROPERTY_MEASUREMENTDATE, oldValue, measurementDate );
  }

  @Column(name = "shotdirection", precision = 8, scale = 3)
  public BigDecimal getShotdirection( )
  {
    return this.shotdirection;
  }

  public void setShotdirection( final BigDecimal shotdirection )
  {
    this.shotdirection = shotdirection;
  }

  @Column(name = "viewangle", precision = 8, scale = 3)
  public BigDecimal getViewangle( )
  {
    return this.viewangle;
  }

  public void setViewangle( final BigDecimal viewangle )
  {
    this.viewangle = viewangle;
  }

  @Column(name = "description", length = 255)
  public String getDescription( )
  {
    return this.description;
  }

  public void setDescription( final String description )
  {
    this.description = description;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cross_section_id")
  public CrossSection getCrossSection( )
  {
    return this.crossSection;
  }

  public void setCrossSection( final CrossSection crossSection )
  {
    this.crossSection = crossSection;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "water_body_id")
  public WaterBody getWaterBody( )
  {
    return this.waterBody;
  }

  public void setWaterBody( final WaterBody waterBody )
  {
    this.waterBody = waterBody;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "state_id")
  public State getState( )
  {
    return this.state;
  }

  public void setState( final State state )
  {
    this.state = state;
  }

}
