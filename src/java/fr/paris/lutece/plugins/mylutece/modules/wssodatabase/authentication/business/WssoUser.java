/*
 * Copyright (c) 2002-2012, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business;

import fr.paris.lutece.portal.service.role.RoleRemovalListenerService;

import java.util.Date;


/**
 * This class represents the business object WssoUser
 */
public class WssoUser
{
	// Variables declarations
	private static WssoUserRoleRemovalListener _listenerRole;
	private int _nMyluteceWssoUserId;
	private String _strGuid;
	private String _strLastName;
	private String _strFirstName;
	private String _strEmail;
	private Date _dateLastLogin;

	/**
	 * Initialize the LdapDatabaseUser
	 */
	public static void init( )
	{
		// Create removal listeners and register them
		if ( _listenerRole == null )
		{
			_listenerRole = new WssoUserRoleRemovalListener( );
			RoleRemovalListenerService.getService( ).registerListener( _listenerRole );
		}
	}

	/**
	 * Returns the MyluteceWssoUserId
	 * 
	 * @return The MyluteceWssoUserId
	 */
	public int getMyluteceWssoUserId( )
	{
		return _nMyluteceWssoUserId;
	}

	/**
	 * Sets the MyluteceWssoUserId
	 * 
	 * @param nMyluteceWssoUserId The MyluteceWssoUserId
	 */
	public void setMyluteceWssoUserId( int nMyluteceWssoUserId )
	{
		_nMyluteceWssoUserId = nMyluteceWssoUserId;
	}

	/**
	 * Returns the Guid
	 * 
	 * @return The Guid
	 */
	public String getGuid( )
	{
		return _strGuid;
	}

	/**
	 * Sets the Guid
	 * 
	 * @param strGuid The Guid
	 */
	public void setGuid( String strGuid )
	{
		_strGuid = strGuid;
	}

	/**
	 * Returns the LastName
	 * 
	 * @return The LastName
	 */
	public String getLastName( )
	{
		return _strLastName;
	}

	/**
	 * Sets the LastName
	 * 
	 * @param strLastName The LastName
	 */
	public void setLastName( String strLastName )
	{
		_strLastName = strLastName;
	}

	/**
	 * Returns the FirstName
	 * 
	 * @return The FirstName
	 */
	public String getFirstName( )
	{
		return _strFirstName;
	}

	/**
	 * Sets the FirstName
	 * 
	 * @param strFirstName The FirstName
	 */
	public void setFirstName( String strFirstName )
	{
		_strFirstName = strFirstName;
	}

	/**
	 * Returns the Email
	 * 
	 * @return The Email
	 */
	public String getEmail( )
	{
		return _strEmail;
	}

	/**
	 * Sets the Email
	 * 
	 * @param strEmail The Email
	 */
	public void setEmail( String strEmail )
	{
		_strEmail = strEmail;
	}

	/**
	 * Get the date of last login of the user
	 * @return The date of last login
	 */
	public Date getDateLastLogin( )
	{
		return _dateLastLogin;
	}

	/**
	 * Set the date of last login of the user
	 * @param dateLastLogin The new date of last login
	 */
	public void setDateLastLogin( Date dateLastLogin )
	{
		_dateLastLogin = dateLastLogin;
	}
}
