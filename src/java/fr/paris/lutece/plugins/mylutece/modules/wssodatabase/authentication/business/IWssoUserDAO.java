/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.Collection;


/**
 * 
 * @author Etienne
 */
public interface IWssoUserDAO
{
	/**
	 * Generates a new primary key
	 * @param plugin The Plugin using this data access service
	 * @return The new primary key
	 */
	int newPrimaryKey( Plugin plugin );

	/**
	 * Insert a new record in the table.
	 * 
	 * @param wssoUser The wssoUser object
	 * @param plugin The Plugin using this data access service
	 */
	void insert( WssoUser wssoUser, Plugin plugin );

	/**
	 * Load the data of WssoUser from the table
	 * 
	 * @param nWssoUserId The identifier of WssoUser
	 * @param plugin The Plugin using this data access service
	 * @return the instance of the WssoUser
	 */
	WssoUser load( int nWssoUserId, Plugin plugin );

	/**
	 * Delete a record from the table
	 * @param wssoUser The WssoUser object
	 * @param plugin The Plugin using this data access service
	 */
	void delete( WssoUser wssoUser, Plugin plugin );

	/**
	 * Update the record in the table
	 * @param wssoUser The reference of wssoUser
	 * @param plugin The Plugin using this data access service
	 */
	void store( WssoUser wssoUser, Plugin plugin );

	/**
	 * Load the list of wssoUsers
	 * @param plugin The Plugin using this data access service
	 * @return The Collection of the WssoUsers
	 */
	Collection selectWssoUserList( Plugin plugin );

	/**
	 * Load the list of wssoUsers for a role
	 * @param nIdRole The role of WssoUser
	 * @param plugin The Plugin using this data access service
	 * @return The Collection of the WssoUsers
	 */
	Collection selectWssoUsersListForRole( int nIdRole, Plugin plugin );

	/**
	 * Load the list of wssoUsers for a guid
	 * @param strGuid The guid of WssoUser
	 * @param plugin The Plugin using this data access service
	 * @return The Collection of the WssoUsers
	 */
	Collection selectWssoUserListForGuid( String strGuid, Plugin plugin );
}
