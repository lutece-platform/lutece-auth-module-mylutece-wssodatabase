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

import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.IdxWSSODatabaseAuthentication;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.IdxWSSODatabaseUser;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.service.WssoDatabasePlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.Utils;

import jakarta.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Tests the WSSO users and roles persistence.
 */
public class WssoUserHomeTest extends LuteceTestCase
{
    @Inject
    private IdxWSSODatabaseAuthentication _authentication;

    private Plugin _plugin;
    private WssoUser _user;

    /**
     * Creates a user with a random guid.
     *
     * @throws Exception
     *             on error
     */
    @BeforeEach
    protected void setUp( ) throws Exception
    {
        super.setUp( );
        _plugin = PluginService.getPlugin( WssoDatabasePlugin.PLUGIN_NAME );
        _user = new WssoUser( );
        _user.setGuid( Utils.getRandomName( ) );
        _user.setLastName( "Durand" );
        _user.setFirstName( "Alice" );
        _user.setEmail( "alice.durand@example.org" );
        WssoUserHome.create( _user, _plugin );
    }

    /**
     * Removes the user and its roles.
     *
     * @throws Exception
     *             on error
     */
    @AfterEach
    protected void tearDown( ) throws Exception
    {
        WssoUserRoleHome.deleteRolesForUser( _user.getMyluteceWssoUserId( ), _plugin );
        WssoUserHome.remove( _user, _plugin );
        super.tearDown( );
    }

    /**
     * A created user is found by its id and its guid, updated, then removed.
     */
    @Test
    public void testCreateFindUpdateRemove( )
    {
        WssoUser loaded = WssoUserHome.findByPrimaryKey( _user.getMyluteceWssoUserId( ), _plugin );
        assertNotNull( loaded );
        assertEquals( _user.getGuid( ), loaded.getGuid( ) );
        assertEquals( "Durand", loaded.getLastName( ) );
        assertNull( loaded.getDateLastLogin( ) );

        Collection<WssoUser> byGuid = WssoUserHome.findWssoUsersListForGuid( _user.getGuid( ), _plugin );
        assertEquals( 1, byGuid.size( ) );

        _user.setEmail( "alice@example.org" );
        WssoUserHome.update( _user, _plugin );
        assertEquals( "alice@example.org", WssoUserHome.findByPrimaryKey( _user.getMyluteceWssoUserId( ), _plugin ).getEmail( ) );

        assertTrue( WssoUserHome.findWssoUsersList( _plugin ).stream( ).anyMatch( u -> u.getGuid( ).equals( _user.getGuid( ) ) ) );

        WssoUser other = new WssoUser( );
        other.setGuid( Utils.getRandomName( ) );
        other.setLastName( "Martin" );
        other.setFirstName( "Paul" );
        other.setEmail( "paul.martin@example.org" );
        WssoUserHome.create( other, _plugin );
        assertTrue( other.getMyluteceWssoUserId( ) > _user.getMyluteceWssoUserId( ) );
        WssoUserHome.remove( other, _plugin );
        assertNull( WssoUserHome.findByPrimaryKey( other.getMyluteceWssoUserId( ), _plugin ) );
    }

    /**
     * Roles are assigned, read from both sides, then deleted.
     */
    @Test
    public void testRoles( )
    {
        int nId = _user.getMyluteceWssoUserId( );
        WssoUserRoleHome.createRoleForUser( nId, "role_b", _plugin );
        WssoUserRoleHome.createRoleForUser( nId, "role_a", _plugin );

        assertEquals( List.of( "role_a", "role_b" ), List.copyOf( WssoUserRoleHome.findRolesListForUser( nId, _plugin ) ) );
        assertTrue( WssoUserRoleHome.findUserListForRole( "role_a", _plugin ).contains( nId ) );
        assertTrue( new WssoUserRoleRemovalListener( ).canBeRemoved( "role_unused_" + nId ) );
        assertFalse( new WssoUserRoleRemovalListener( ).canBeRemoved( "role_a" ) );

        WssoUserRoleHome.deleteRolesForUser( nId, _plugin );
        assertTrue( WssoUserRoleHome.findRolesListForUser( nId, _plugin ).isEmpty( ) );
    }

    /**
     * The authentication view of a user carries its names, e-mail, roles and last login date.
     */
    @Test
    public void testAuthenticationView( )
    {
        WssoUserRoleHome.createRoleForUser( _user.getMyluteceWssoUserId( ), "role_a", _plugin );

        IdxWSSODatabaseUser user = IdxWSSODatabaseHome.findUserByGuid( _user.getGuid( ), _plugin, _authentication );
        assertNotNull( user );
        assertEquals( "Durand", user.getUserInfo( LuteceUser.NAME_FAMILY ) );
        assertEquals( "Alice", user.getUserInfo( LuteceUser.NAME_GIVEN ) );
        assertEquals( "alice.durand@example.org", user.getUserInfo( LuteceUser.BUSINESS_INFO_ONLINE_EMAIL ) );
        assertEquals( List.of( "role_a" ), IdxWSSODatabaseHome.findUserRolesFromGuid( _user.getGuid( ), _plugin, _authentication ) );
        assertNull( IdxWSSODatabaseHome.findUserByGuid( "unknown-" + _user.getGuid( ), _plugin, _authentication ) );

        IdxWSSODatabaseHome.updateDateLastLogin( _user.getGuid( ), new Date( ), _plugin );
        assertNotNull( WssoUserHome.findByPrimaryKey( _user.getMyluteceWssoUserId( ), _plugin ).getDateLastLogin( ) );
    }
}
