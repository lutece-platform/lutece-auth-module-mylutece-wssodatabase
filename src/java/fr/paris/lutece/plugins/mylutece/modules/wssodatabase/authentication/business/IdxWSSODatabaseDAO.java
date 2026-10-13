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
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.util.sql.DAOUtil;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class provides Data Access methods for authentication (role retrieval).
 */
@ApplicationScoped
public class IdxWSSODatabaseDAO implements IIdxWSSODatabaseDAO
{
    public static final String SQL_QUERY_FIND_USER_BY_GUID = "SELECT mylutece_wsso_user_id, last_name, first_name, email FROM mylutece_wsso_user WHERE guid like ? ";
    public static final String SQL_QUERY_FIND_ALL_USERS = "SELECT guid, last_name, first_name, email FROM mylutece_wsso_user";
    public static final String SQL_QUERY_FIND_ROLES_FROM_GUID = "SELECT a.role FROM mylutece_wsso_user_role a, mylutece_wsso_user b"
            + " WHERE b.mylutece_wsso_user_id = a.mylutece_wsso_user_id AND b.guid like ? ";
    private static final String SQL_QUERY_UPDATE_DATE_LAST_LOGIN = " UPDATE mylutece_wsso_user SET date_last_login = ? WHERE guid like ? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public IdxWSSODatabaseUser findUserByGuid( String strGuid, Plugin plugin, LuteceAuthentication authenticationService )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_USER_BY_GUID, plugin ) )
        {
            daoUtil.setString( 1, strGuid );
            daoUtil.executeQuery( );

            if ( !daoUtil.next( ) )
            {
                return null;
            }

            IdxWSSODatabaseUser user = new IdxWSSODatabaseUser( strGuid, authenticationService );
            user.setUserInfo( LuteceUser.NAME_FAMILY, daoUtil.getString( 2 ) );
            user.setUserInfo( LuteceUser.NAME_GIVEN, daoUtil.getString( 3 ) );
            user.setUserInfo( LuteceUser.BUSINESS_INFO_ONLINE_EMAIL, daoUtil.getString( 4 ) );

            return user;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> findUserRolesFromGuid( String strGuid, Plugin plugin, LuteceAuthentication authenticationService )
    {
        List<String> arrayRoles = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_ROLES_FROM_GUID, plugin ) )
        {
            daoUtil.setString( 1, strGuid );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                arrayRoles.add( daoUtil.getString( 1 ) );
            }
        }

        return arrayRoles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateDateLastLogin( String strGuid, java.util.Date dateLastLogin, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_DATE_LAST_LOGIN, plugin ) )
        {
            daoUtil.setDate( 1, new java.sql.Date( dateLastLogin.getTime( ) ) );
            daoUtil.setString( 2, strGuid );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<IdxWSSODatabaseUser> findUsersList( Plugin plugin, LuteceAuthentication authenticationService )
    {
        Collection<IdxWSSODatabaseUser> usersList = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_ALL_USERS, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                String strGuid = daoUtil.getString( 1 );
                IdxWSSODatabaseUser user = new IdxWSSODatabaseUser( strGuid, authenticationService );
                user.setUserInfo( LuteceUser.NAME_FAMILY, daoUtil.getString( 2 ) );
                user.setUserInfo( LuteceUser.NAME_GIVEN, daoUtil.getString( 3 ) );
                user.setUserInfo( LuteceUser.BUSINESS_INFO_ONLINE_EMAIL, daoUtil.getString( 4 ) );
                usersList.add( user );
            }
        }

        for ( IdxWSSODatabaseUser user : usersList )
        {
            user.setRoles( findUserRolesFromGuid( user.getName( ), plugin, authenticationService ) );
        }

        return usersList;
    }
}
