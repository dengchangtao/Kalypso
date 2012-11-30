package org.kalypso.model.wspm.pdb.db.mapping;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.kalypso.commons.java.util.AbstractModelObject;
import org.kalypso.model.wspm.pdb.db.constants.StateConstants;

/**
 * State generated by hbm2java
 */
@Entity
@Table( name = "state", schema = "pdb", uniqueConstraints = @UniqueConstraint( columnNames = "name" ) )
public class State extends AbstractModelObject implements Serializable, StateConstants, IElementWithDates, IDocumentContainer
{
  private long m_id;

  private String m_name;

  private ZeroState m_zeroState;

  private Date m_creationDate = new Date();

  private Date m_editingDate;

  private String m_editingUser;

  private Date m_measurementDate;

  private String m_source;

  private String m_description;

  private Set<CrossSection> m_crossSections = new HashSet<>( 0 );

  private Set<Document> m_documents = new HashSet<>( 0 );

  private Set<Event> m_events = new HashSet<>( 0 );

  public State( )
  {
  }

  public State( final long id, final String name, final ZeroState zeroState, final Date creationDate, final Date editingDate, final String editingUser )
  {
    m_id = id;
    m_name = name;
    m_zeroState = zeroState;
    m_creationDate = creationDate;
    m_editingDate = editingDate;
    m_editingUser = editingUser;
  }

  public State( final long id, final String name, final ZeroState zeroState, final Date creationDate, final Date editingDate, final String editingUser, final Date measurementDate, final String source, final String description, final Set<CrossSection> crossSections, final Set<Document> documents, final Set<Event> events )
  {
    m_id = id;
    m_name = name;
    m_zeroState = zeroState;
    m_creationDate = creationDate;
    m_editingDate = editingDate;
    m_editingUser = editingUser;
    m_measurementDate = measurementDate;
    m_source = source;
    m_description = description;
    m_crossSections = crossSections;
    m_documents = documents;
    m_events = events;
  }

  /**
   * Copy constructor, completely copy everything from the other state.<br/>
   * This is a shallow copy, associations are copied as they are, not cloned as well.
   */
  public State( final State state )
  {
    m_id = state.getId();

    m_name = state.getName();

    m_zeroState = state.getIsstatezero();

    m_creationDate = state.getCreationDate();

    m_editingDate = state.getEditingDate();

    m_editingUser = state.getEditingUser();

    m_measurementDate = state.getMeasurementDate();

    m_source = state.getSource();

    m_description = state.getDescription();

    m_crossSections = new HashSet<>( state.getCrossSections() );

    m_documents = new HashSet<>( state.getDocuments() );

    m_events = new HashSet<>( state.getEvents() );
  }

  @Id
  @Column( name = "id", unique = true, nullable = false, precision = 20, scale = 0 )
  @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "state_id_seq" )
  @SequenceGenerator( name = "state_id_seq", sequenceName = "pdb.seq_pdb" )
  public long getId( )
  {
    return m_id;
  }

  public void setId( final long id )
  {
    final Object oldValue = m_id;

    m_id = id;

    firePropertyChange( PROPERTY_ID, oldValue, id );
  }

  @Override
  @Column( name = "name", unique = true, nullable = false, length = 100 )
  public String getName( )
  {
    return m_name;
  }

  public void setName( final String name )
  {
    final Object oldValue = m_name;

    m_name = name;

    firePropertyChange( PROPERTY_NAME, oldValue, name );
  }

  @Column( name = "isstatezero", nullable = false, length = 1 )
  @Enumerated( EnumType.STRING )
  public ZeroState getIsstatezero( )
  {
    return m_zeroState;
  }

  public void setIsstatezero( final ZeroState state )
  {
    final Object oldValue = m_zeroState;

    m_zeroState = state;

    firePropertyChange( PROPERTY_ISSTATEZERO, oldValue, state );
  }

  @Override
  @Temporal( TemporalType.TIMESTAMP )
  @Column( name = "creation_date", nullable = false, length = 22 )
  public Date getCreationDate( )
  {
    return m_creationDate;
  }

  @Override
  public void setCreationDate( final Date creationDate )
  {
    final Object oldValue = m_creationDate;

    m_creationDate = creationDate;

    firePropertyChange( PROPERTY_CREATIONDATE, oldValue, creationDate );
  }

  @Override
  @Temporal( TemporalType.TIMESTAMP )
  @Column( name = "editing_date", nullable = false, length = 22 )
  public Date getEditingDate( )
  {
    return m_editingDate;
  }

  @Override
  public void setEditingDate( final Date editingDate )
  {
    final Object oldValue = m_editingDate;

    m_editingDate = editingDate;

    firePropertyChange( PROPERTY_EDITINGDATE, oldValue, editingDate );
  }

  @Override
  @Column( name = "editing_user", nullable = false, length = 50 )
  public String getEditingUser( )
  {
    return m_editingUser;
  }

  @Override
  public void setEditingUser( final String editingUser )
  {
    final Object oldValue = m_editingUser;

    m_editingUser = editingUser;

    firePropertyChange( PROPERTY_EDITINGUSER, oldValue, editingUser );
  }

  @Override
  @Temporal( TemporalType.TIMESTAMP )
  @Column( name = "measurement_date", length = 22 )
  public Date getMeasurementDate( )
  {
    return m_measurementDate;
  }

  @Override
  public void setMeasurementDate( final Date measurementDate )
  {
    final Object oldValue = m_measurementDate;

    m_measurementDate = measurementDate;

    firePropertyChange( PROPERTY_MEASUREMENTDATE, oldValue, measurementDate );
  }

  @Column( name = "source" )
  public String getSource( )
  {
    return m_source;
  }

  public void setSource( final String source )
  {
    final Object oldValue = m_source;

    m_source = source;

    firePropertyChange( PROPERTY_SOURCE, oldValue, source );
  }

  @Column( name = "description" )
  public String getDescription( )
  {
    return m_description;
  }

  public void setDescription( final String description )
  {
    final Object oldValue = m_description;

    m_description = description;

    firePropertyChange( PROPERTY_DESCRIPTION, oldValue, description );
  }

  @OneToMany( fetch = FetchType.LAZY, mappedBy = "state" )
  public Set<CrossSection> getCrossSections( )
  {
    return m_crossSections;
  }

  public void setCrossSections( final Set<CrossSection> crossSections )
  {
    m_crossSections = crossSections;
  }

  @Override
  @OneToMany( fetch = FetchType.LAZY, mappedBy = "state" )
  public Set<Document> getDocuments( )
  {
    return m_documents;
  }

  @Override
  public void setDocuments( final Set<Document> documents )
  {
    m_documents = documents;
  }

  @OneToMany( fetch = FetchType.LAZY, mappedBy = "state" )
  public Set<Event> getEvents( )
  {
    return m_events;
  }

  public void setEvents( final Set<Event> events )
  {
    m_events = events;
  }

  @Override
  public String toString( )
  {
    return getName();
  }
}