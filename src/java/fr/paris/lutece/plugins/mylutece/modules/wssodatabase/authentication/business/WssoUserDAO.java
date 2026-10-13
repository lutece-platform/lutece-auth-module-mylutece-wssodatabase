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
import fr.paris.lutece.util.sql.DAOUtil;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class provides Data Access methods for WssoUser objects
 */
@ApplicationScoped
public class WssoUserDAO implements IWssoUserDAO
{
    private static final String SQL_QUERY_NEW_PK = " SELECT max( mylutece_wsso_user_id ) FROM mylutece_wsso_user ";
    private static final String SQL_QUERY_SELECT = " SELECT mylutece_wsso_user_id, guid, last_name, first_name, email, date_last_login FROM mylutece_wsso_user WHERE mylutece_wsso_user_id = ? ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO mylutece_wsso_user ( mylutece_wsso_user_id, guid, last_name, first_name, email ) VALUES ( ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM mylutece_wsso_user WHERE mylutece_wsso_user_id = ?  ";
    private static final String SQL_QUERY_UPDATE = " UPDATE mylutece_wsso_user SET mylutece_wsso_user_id = ?, guid = ?, last_name = ?, first_name = ?, email = ? WHERE mylutece_wsso_user_id = ?  ";
    private static final String SQL_QUERY_SELECTALL = " SELECT mylutece_wsso_user_id, guid, last_name, first_name, email, date_last_login FROM mylutece_wsso_user ORDER BY last_name, first_name, email ";
    private static final String SQL_QUERY_SELECTALL_FOR_ROLE = " SELECT u.mylutece_wsso_user_id, u.guid, u.last_name, u.first_name, u.email, u.date_last_login FROM mylutece_wsso_user u, mylutece_wsso_user_role ur WHERE u.mylutece_wsso_user_id = ur.mylutece_wsso_user_id AND ur.mylutece_wsso_role_id = ? ORDER BY u.last_name, u.first_name, u.email ";
    private static final String SQL_QUERY_SELECTALL_FOR_GUID = " SELECT mylutece_wsso_user_id, guid, last_name, first_name, email, date_last_login FROM mylutece_wsso_user WHERE guid = ? ORDER BY last_name, first_name, email ";

    /**
     * {@inheritDoc}
     */
    @Override
    public int newPrimaryKey( Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin ) )
        {
            daoUtil.executeQuery( );

            if ( !daoUtil.next( ) )
            {
                return 1;
            }

            return daoUtil.getInt( 1 ) + 1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert( WssoUser wssoUser, Plugin plugin )
    {
        wssoUser.setMyluteceWssoUserId( newPrimaryKey( plugin ) );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            daoUtil.setInt( 1, wssoUser.getMyluteceWssoUserId( ) );
            daoUtil.setString( 2, wssoUser.getGuid( ) );
            daoUtil.setString( 3, wssoUser.getLastName( ) );
            daoUtil.setString( 4, wssoUser.getFirstName( ) );
            daoUtil.setString( 5, wssoUser.getEmail( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WssoUser load( int nWssoUserId, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nWssoUserId );
            daoUtil.executeQuery( );

            WssoUser wssoUser = null;

            if ( daoUtil.next( ) )
            {
                wssoUser = readUser( daoUtil );
            }

            return wssoUser;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( WssoUser wssoUser, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, wssoUser.getMyluteceWssoUserId( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( WssoUser wssoUser, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            daoUtil.setInt( 1, wssoUser.getMyluteceWssoUserId( ) );
            daoUtil.setString( 2, wssoUser.getGuid( ) );
            daoUtil.setString( 3, wssoUser.getLastName( ) );
            daoUtil.setString( 4, wssoUser.getFirstName( ) );
            daoUtil.setString( 5, wssoUser.getEmail( ) );
            daoUtil.setInt( 6, wssoUser.getMyluteceWssoUserId( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<WssoUser> selectWssoUserList( Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            return readUsers( daoUtil );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<WssoUser> selectWssoUsersListForRole( int nIdRole, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_FOR_ROLE, plugin ) )
        {
            daoUtil.setInt( 1, nIdRole );
            daoUtil.executeQuery( );

            return readUsers( daoUtil );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<WssoUser> selectWssoUserListForGuid( String strGuid, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_FOR_GUID, plugin ) )
        {
            daoUtil.setString( 1, strGuid );
            daoUtil.executeQuery( );

            return readUsers( daoUtil );
        }
    }

    /**
     * Reads every remaining row of an executed query as users.
     *
     * @param daoUtil
     *            the executed query
     * @return the users
     */
    private static Collection<WssoUser> readUsers( DAOUtil daoUtil )
    {
        Collection<WssoUser> listWssoUsers = new ArrayList<>( );

        while ( daoUtil.next( ) )
        {
            listWssoUsers.add( readUser( daoUtil ) );
        }

        return listWssoUsers;
    }

    /**
     * Reads the current row as a user.
     *
     * @param daoUtil
     *            the query positioned on a row
     * @return the user
     */
    private static WssoUser readUser( DAOUtil daoUtil )
    {
        WssoUser wssoUser = new WssoUser( );
        wssoUser.setMyluteceWssoUserId( daoUtil.getInt( 1 ) );
        wssoUser.setGuid( daoUtil.getString( 2 ) );
        wssoUser.setLastName( daoUtil.getString( 3 ) );
        wssoUser.setFirstName( daoUtil.getString( 4 ) );
        wssoUser.setEmail( daoUtil.getString( 5 ) );
        wssoUser.setDateLastLogin( daoUtil.getDate( 6 ) );

        return wssoUser;
    }
}
