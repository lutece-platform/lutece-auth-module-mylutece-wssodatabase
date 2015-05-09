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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.Collection;


/**
 * This class provides instances management methods (create, find, ...) for WssoUserRole objects
 */
public final class WssoUserRoleHome
{
    // Static variable pointed at the DAO instance
    private static IWssoUserRoleDAO _dao = (IWssoUserRoleDAO) SpringContextService.getPluginBean( "mylutece-wssodatabase",
            "mylutece-wssodatabase.wssoUserRoleDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private WssoUserRoleHome(  )
    {
    }

    /**
     * Returns a collection of Roles objects
     * @param nIdUser The id of the user
     * @param plugin The current plugin using this method
     * @return A collection of Roles
     */
    public static Collection<String> findRolesListForUser( int nIdUser, Plugin plugin )
    {
        return _dao.selectRoleListForUser( nIdUser, plugin );
    }

    /**
     * Returns a collection of User id
     * @param strRoleKey The key of the role
     * @param plugin The current plugin using this method
     * @return A collection of User id
     */
    public static Collection<Integer> findUserListForRole( String strRoleKey, Plugin plugin )
    {
        return _dao.selectUserListForRole( strRoleKey, plugin );
    }

    /**
     * Delete roles for a user
     * @param nIdUser The id of the user
     * @param plugin The Plugin using this data access service
     */
    public static void deleteRolesForUser( int nIdUser, Plugin plugin )
    {
        _dao.deleteRolesForUser( nIdUser, plugin );
    }

    /**
     * Assign a role to user
     * @param nIdUser The id of the user
     * @param strRoleId The id of the role
     * @param plugin The Plugin using this data access service
     */
    public static void createRoleForUser( int nIdUser, String strRoleId, Plugin plugin )
    {
        _dao.createRoleForUser( nIdUser, strRoleId, plugin );
    }
}
