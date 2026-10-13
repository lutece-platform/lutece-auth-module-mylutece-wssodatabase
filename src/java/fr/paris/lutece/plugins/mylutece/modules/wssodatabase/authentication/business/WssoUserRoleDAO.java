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
 * This class provides Data Access methods for the roles of the WSSO users
 */
@ApplicationScoped
public class WssoUserRoleDAO implements IWssoUserRoleDAO
{
    private static final String SQL_QUERY_SELECTALL_FOR_USER = " SELECT ur.role FROM mylutece_wsso_user_role ur WHERE ur.mylutece_wsso_user_id = ? ORDER BY ur.role ";
    private static final String SQL_QUERY_SELECTALL_FOR_ROLE = " SELECT ur.mylutece_wsso_user_id FROM mylutece_wsso_user_role ur WHERE ur.role = ? ORDER BY ur.mylutece_wsso_user_id ";
    private static final String SQL_QUERY_DELETE_ROLES_FOR_USER = "DELETE FROM mylutece_wsso_user_role WHERE mylutece_wsso_user_id = ?";
    private static final String SQL_QUERY_INSERT_ROLE_FOR_USER = "INSERT INTO mylutece_wsso_user_role ( mylutece_wsso_user_id, role ) VALUES ( ?, ? ) ";

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> selectRoleListForUser( int nIdUser, Plugin plugin )
    {
        Collection<String> listRoles = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_FOR_USER, plugin ) )
        {
            daoUtil.setInt( 1, nIdUser );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listRoles.add( daoUtil.getString( 1 ) );
            }
        }

        return listRoles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Integer> selectUserListForRole( String strRoleKey, Plugin plugin )
    {
        Collection<Integer> listUserId = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_FOR_ROLE, plugin ) )
        {
            daoUtil.setString( 1, strRoleKey );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listUserId.add( daoUtil.getInt( 1 ) );
            }
        }

        return listUserId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteRolesForUser( int nIdUser, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ROLES_FOR_USER, plugin ) )
        {
            daoUtil.setInt( 1, nIdUser );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createRoleForUser( int nIdUser, String strRoleId, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_ROLE_FOR_USER, plugin ) )
        {
            daoUtil.setInt( 1, nIdUser );
            daoUtil.setString( 2, strRoleId );
            daoUtil.executeUpdate( );
        }
    }
}
