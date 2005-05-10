/*--------------- Kalypso-Header --------------------------------------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 Technical University Hamburg-Harburg (TUHH)
 Institute of River and coastal engineering
 Denickestr. 22
 21073 Hamburg, Germany
 http://www.tuhh.de/wb

 and

 Bjoernsen Consulting Engineers (BCE)
 Maria Trost 3
 56070 Koblenz, Germany
 http://www.bjoernsen.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 E-Mail:
 belger@bjoernsen.de
 schlienger@bjoernsen.de
 v.doemming@tuhh.de

 ---------------------------------------------------------------------------------------------------*/
package org.kalypso.ui.application.login;

import java.rmi.RemoteException;

import org.kalypso.services.proxy.IUserService;
import org.kalypso.users.User;

/**
 * AuthLoginValidator: allows full-authentication (user name is changeable and
 * password must be entered by user).
 * 
 * @author schlienger
 */
public class AuthLoginValidator implements ILoginValidator
{
  private final IUserService m_srv;

  public AuthLoginValidator( final IUserService srv )
  {
    m_srv = srv;
  }

  public User validate( final String username, final String password )
      throws Exception
  {
    try
    {
      final String[] rights = m_srv.getRights2( username, password );

      if( rights != null )
        return new User( username, rights );
    }
    catch( final RemoteException e )
    {
      throw e;
    }

    return null;
  }

  public boolean userNameChangeable( )
  {
    return true;
  }

  public boolean passwordEnabled( )
  {
    return true;
  }

  public String getDefaultUserName( )
  {
    return System.getProperty( "user.name" );
  }

  public String getMessage( )
  {
    return "Melden Sie sich bitte an.";
  }
}