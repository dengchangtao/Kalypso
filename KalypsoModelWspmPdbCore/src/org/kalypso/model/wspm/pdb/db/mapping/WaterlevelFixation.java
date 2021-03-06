package org.kalypso.model.wspm.pdb.db.mapping;

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

import org.apache.commons.lang3.StringUtils;

/**
 * WaterlevelFixation generated by hbm2java
 */
@Entity
@Table(name = "waterlevel_fixation", schema = "pdb")
public class WaterlevelFixation implements java.io.Serializable
{
  public static final String PROPERTY_STATION = "station"; //$NON-NLS-1$

  public static final String PROPERTY_WATERLEVEL = "waterlevel"; //$NON-NLS-1$

  public static final String PROPERTY_DISCHARGE = "discharge"; //$NON-NLS-1$

  public static final String PROPERTY_MEASURMENT_DATE = "measurementDate"; //$NON-NLS-1$

  public static final String PROPERTY_DESCRIPTION = "description"; //$NON-NLS-1$

  private BigDecimal m_id;

  private Event m_event;

  private BigDecimal m_station;

  private com.vividsolutions.jts.geom.Point m_location;

  private Date m_creationDate;

  private Date m_editingDate;

  private String m_editingUser;

  private Date m_measurementDate;

  private BigDecimal m_waterlevel;

  private BigDecimal m_discharge;

  private String m_description = StringUtils.EMPTY;

  public WaterlevelFixation( )
  {
  }

  public WaterlevelFixation( final BigDecimal id, final Event event, final BigDecimal station, final Date creationDate, final Date editingDate, final String editingUser )
  {
    m_id = id;
    m_event = event;
    m_station = station;
    m_creationDate = creationDate;
    m_editingDate = editingDate;
    m_editingUser = editingUser;
  }

  public WaterlevelFixation( final BigDecimal id, final Event event, final BigDecimal station, final com.vividsolutions.jts.geom.Point location, final Date creationDate, final Date editingDate, final String editingUser, final Date measurementDate, final BigDecimal waterlevel, final BigDecimal discharge, final String description )
  {
    m_id = id;
    m_event = event;
    m_station = station;
    m_location = location;
    m_creationDate = creationDate;
    m_editingDate = editingDate;
    m_editingUser = editingUser;
    m_measurementDate = measurementDate;
    m_waterlevel = waterlevel;
    m_discharge = discharge;
    m_description = description;
  }

  @Id
  @Column(name = "id", unique = true, nullable = false, precision = 20, scale = 0)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "waterlevel_fixation_id_seq")
  @SequenceGenerator(name = "waterlevel_fixation_id_seq", sequenceName = "pdb.seq_pdb")
  public BigDecimal getId( )
  {
    return m_id;
  }

  public void setId( final BigDecimal id )
  {
    m_id = id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event", nullable = false)
  public Event getEvent( )
  {
    return m_event;
  }

  public void setEvent( final Event event )
  {
    m_event = event;
  }

  @Column(name = "station", nullable = false, precision = 8, scale = 1)
  public BigDecimal getStation( )
  {
    return m_station;
  }

  public void setStation( final BigDecimal station )
  {
    m_station = station;
  }

  @Column(name = "location", columnDefinition = "Geometry")
  public com.vividsolutions.jts.geom.Point getLocation( )
  {
    return m_location;
  }

  public void setLocation( final com.vividsolutions.jts.geom.Point location )
  {
    m_location = location;
  }

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "creation_date", nullable = false, length = 22)
  public Date getCreationDate( )
  {
    return m_creationDate;
  }

  public void setCreationDate( final Date creationDate )
  {
    m_creationDate = creationDate;
  }

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "editing_date", nullable = false, length = 22)
  public Date getEditingDate( )
  {
    return m_editingDate;
  }

  public void setEditingDate( final Date editingDate )
  {
    m_editingDate = editingDate;
  }

  @Column(name = "editing_user", nullable = false, length = 50)
  public String getEditingUser( )
  {
    return m_editingUser;
  }

  public void setEditingUser( final String editingUser )
  {
    m_editingUser = editingUser;
  }

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "measurement_date", length = 22)
  public Date getMeasurementDate( )
  {
    return m_measurementDate;
  }

  public void setMeasurementDate( final Date measurementDate )
  {
    m_measurementDate = measurementDate;
  }

  @Column(name = "waterlevel", precision = 8, scale = 3)
  public BigDecimal getWaterlevel( )
  {
    return m_waterlevel;
  }

  public void setWaterlevel( final BigDecimal waterlevel )
  {
    m_waterlevel = waterlevel;
  }

  @Column(name = "discharge", precision = 8, scale = 3)
  public BigDecimal getDischarge( )
  {
    return m_discharge;
  }

  public void setDischarge( final BigDecimal discharge )
  {
    m_discharge = discharge;
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
}