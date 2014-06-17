/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.IdxWSSODatabaseUser;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.security.LuteceAuthentication;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.Collection;
import java.util.List;


/**
 * Home for IdxWSSODatabase
 * 
 */
public final class IdxWSSODatabaseHome
{
    // Static variable pointed at the DAO instance
    private static IIdxWSSODatabaseDAO _dao = SpringContextService.getBean( "mylutece-wssodatabase.idxWSSODatabaseDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private IdxWSSODatabaseHome( )
    {
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Find users by guid
     * 
     * @param strGuid the WSSO guid
     * @param plugin The Plugin using this data access service
     * @param authenticationService the LuteceAuthentication object
     * @return IdxWSSODatabaseUser the user corresponding to the guid
     */
    public static IdxWSSODatabaseUser findUserByGuid( String strGuid, Plugin plugin,
            LuteceAuthentication authenticationService )
    {
        return _dao.findUserByGuid( strGuid, plugin, authenticationService );
    }

    /**
     * Find user's roles by guid
     * 
     * @param strGuid the WSSO guid
     * @param plugin The Plugin using this data access service
     * @param authenticationService the LuteceAuthentication object
     * @return ArrayList the roles list corresponding to the guid
     */
    public static List<String> findUserRolesFromGuid( String strGuid, Plugin plugin,
            LuteceAuthentication authenticationService )
    {
        return _dao.findUserRolesFromGuid( strGuid, plugin, authenticationService );
    }

    /**
     * Update the last login date of a user
     * @param strGuid The GUID of the user to update
     * @param dateLastLogin New date of last connection
     * @param plugin The plugin
     */
    public static void updateDateLastLogin( String strGuid, java.util.Date dateLastLogin, Plugin plugin )
    {
        _dao.updateDateLastLogin( strGuid, dateLastLogin, plugin );
    }

    /**
     * Find users by guid
     * 
     * @param plugin The Plugin using this data access service
     * @param authenticationService the LuteceAuthentication object
     * @return IdxWSSODatabaseUser the user corresponding to the guid
     */
    public static Collection<IdxWSSODatabaseUser> findUsersList( Plugin plugin,
            LuteceAuthentication authenticationService )
    {
        return _dao.findUsersList( plugin, authenticationService );
    }
}
