package org.kalypso.kalypsosimulationmodel.core;

/**
 * Provides assertion methods
 * 
 * @author Patrice Congo
 */
public class Assert
{
  private Assert( )
  {
    // empty
  }

  /**
   * Assert the given object for null value. This method throws concequently an illegal argument exception if the passed
   * object is null
   * 
   * @param obj
   *          the object to be asserted
   * @param message
   *          the exception message
   * @throws IllegalArgumentException
   *           if the passed object is null
   */
  public static final void throwIAEOnNull( final Object obj, final String message ) throws IllegalArgumentException
  {
    if( obj == null )
    {
      throw new IllegalArgumentException( message );
    }
  }

  /**
   * Assert the given object for null value. This method throws concequently an illegal argument exception if the passed
   * object is null
   * 
   * @param obj
   *          the object to be asserted
   * @param message
   *          the exception message
   * @throws IllegalArgumentException
   *           if the passed object is null
   */
  public static final void throwIAEOnNullParam( final Object param, final String paramName ) throws IllegalArgumentException
  {
    if( param == null )
    {
      final StringBuffer buf = new StringBuffer( 128 );
      buf.append( "Parameter must not be null; param name: " ); //$NON-NLS-1$
      buf.append( paramName );
      throw new IllegalArgumentException( buf.toString() );
    }
  }

  public static final String throwIAEOnNullOrEmpty( String str ) throws IllegalArgumentException
  {
    if( str == null )
    {
      throw new IllegalArgumentException( "String must not be null" ); //$NON-NLS-1$
    }
    str = str.trim();
    if( str.length() == 0 )
    {
      throw new IllegalArgumentException( "String must not be empty" ); //$NON-NLS-1$
    }
    return str;
  }

  public static final boolean isNullOrEmpty( String str )
  {
    if( str == null )
    {
      return true;
    }
    str = str.trim();
    if( str.length() == 0 )
    {
      return true;
    }
    return false;
  }

  public static final void throwIAEOnLessThan0( final double d, String message ) throws IllegalArgumentException
  {
    if( d < 0 )
    {
      if( message == null )
      {
        message = "Number must be greater or equals to 0"; //$NON-NLS-1$
      }

      throw new IllegalArgumentException( message );
    }
  }
}
