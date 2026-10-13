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
package fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication;

import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoUser;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoUserHome;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoUserRoleHome;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.service.WssoDatabasePlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.Utils;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

import jakarta.inject.Inject;
import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the authentication of a user by the WSSO cookies.
 */
public class IdxWSSODatabaseAuthenticationTest extends LuteceTestCase
{
    @Inject
    private IdxWSSODatabaseAuthentication _authentication;

    private Plugin _plugin;
    private WssoUser _user;

    /**
     * Creates a user with a role.
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
        WssoUserRoleHome.createRoleForUser( _user.getMyluteceWssoUserId( ), "role_a", _plugin );
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
     * The guid cookie of a known user authenticates it with its roles.
     */
    @Test
    public void testAuthenticatedByCookie( )
    {
        MockHttpServletRequest request = requestWithCookies( new Cookie( "MDP-WSSO-MDPWSSOGUID", _user.getGuid( ) ),
                new Cookie( "MDP-WSSO-AUTHENTIFICATION", "certificate" ) );

        LuteceUser user = _authentication.getHttpAuthenticatedUser( request );

        assertNotNull( user );
        assertEquals( _user.getGuid( ), user.getName( ) );
        assertTrue( _authentication.isUserInRole( user, request, "role_a" ) );
        assertFalse( _authentication.isUserInRole( user, request, "role_b" ) );
        assertEquals( "certificate", _authentication.getAuthType( request ) );
        assertNotNull( WssoUserHome.findByPrimaryKey( _user.getMyluteceWssoUserId( ), _plugin ).getDateLastLogin( ) );
    }

    /**
     * No cookie, or the cookie of an unknown guid, authenticates nobody.
     */
    @Test
    public void testNotAuthenticated( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        assertNull( _authentication.getHttpAuthenticatedUser( request ) );
        assertNull( _authentication.getAuthType( request ) );

        assertNull( _authentication.getHttpAuthenticatedUser( requestWithCookies( new Cookie( "MDP-WSSO-MDPWSSOGUID", "unknown-" + _user.getGuid( ) ) ) ) );
    }

    /**
     * Builds a request carrying cookies.
     *
     * @param cookies
     *            the cookies
     * @return the request
     */
    private static MockHttpServletRequest requestWithCookies( Cookie... cookies )
    {
        return new MockHttpServletRequest( )
        {
            @Override
            public Cookie [ ] getCookies( )
            {
                return cookies;
            }
        };
    }
}
