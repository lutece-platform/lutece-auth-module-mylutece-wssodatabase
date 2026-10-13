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
package fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.service;

import fr.paris.lutece.plugins.mylutece.authentication.MultiLuteceAuthentication;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.IdxWSSODatabaseAuthentication;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.IdxWSSODatabaseUser;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.IdxWSSODatabaseHome;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoUserRoleRemovalListener;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.security.LuteceAuthentication;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.util.BeanUtils;
import fr.paris.lutece.portal.service.util.RemovalListenerService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Registers the WSSO authentication and loads the WSSO users.
 */
@ApplicationScoped
public class WssoDatabaseService
{
    private static final String PROPERTY_LOAD_ALL_USER_INFORMATIONS_FROM_WSSO = "mylutece-wssodatabase.loadAllUserInformationsFromWsso";
    private static final String PROPERTY_USER_MAPPING_ATTRIBUTES = "mylutece-wssodatabase.userMappingAttributes";
    private static final String CONSTANT_LUTECE_USER_PROPERTIES_PATH = "mylutece-wssodatabase.attribute";
    private static final String SEPARATOR = ",";

    @Inject
    private IdxWSSODatabaseAuthentication _authentication;

    @Inject
    @Named( BeanUtils.BEAN_ROLE_REMOVAL_SERVICE )
    private RemovalListenerService _roleRemovalService;

    @Inject
    @ConfigProperty( name = PROPERTY_LOAD_ALL_USER_INFORMATIONS_FROM_WSSO, defaultValue = "false" )
    private boolean _bLoadAllUserInformationsFromWsso;

    @Inject
    @ConfigProperty( name = PROPERTY_USER_MAPPING_ATTRIBUTES )
    private Optional<String> _strUserMappingAttributes;

    private final Map<String, List<String>> _mapAttributeUserMapping = new HashMap<>( );

    /**
     * Registers the authentication and the role removal listener when the application starts.
     *
     * @param context
     *            the servlet context
     */
    public void initializedService( @Observes @Initialized( ApplicationScoped.class ) ServletContext context )
    {
        _roleRemovalService.registerListener( new WssoUserRoleRemovalListener( ) );
        if ( _bLoadAllUserInformationsFromWsso )
        {
            loadAttributeUserMapping( );
        }

        MultiLuteceAuthentication.registerAuthentication( _authentication );
    }

    /**
     * Reads the mapping between the WSSO cookies and the Lutece user attributes.
     */
    private void loadAttributeUserMapping( )
    {
        String strUserMappingAttributes = _strUserMappingAttributes.orElse( null );

        if ( StringUtils.isBlank( strUserMappingAttributes ) )
        {
            return;
        }

        for ( String strUserProperty : strUserMappingAttributes.split( SEPARATOR ) )
        {
            String strCookieNames = AppPropertiesService.getProperty( CONSTANT_LUTECE_USER_PROPERTIES_PATH + "." + strUserProperty );

            if ( StringUtils.isNotBlank( strCookieNames ) )
            {
                for ( String strCookieName : strCookieNames.split( SEPARATOR ) )
                {
                    _mapAttributeUserMapping.computeIfAbsent( strCookieName, k -> new ArrayList<>( ) ).add( strUserProperty );
                }
            }
        }
    }

    /**
     * Loads a WSSO user, from the database or from the WSSO cookies.
     *
     * @param strUserID
     *            the WSSO guid
     * @param request
     *            the request
     * @param authenticationService
     *            the authentication service
     * @param plugin
     *            the plugin
     * @return the user, or null if the database does not know the guid
     */
    public IdxWSSODatabaseUser loadIdxWSSOUser( String strUserID, HttpServletRequest request, LuteceAuthentication authenticationService, Plugin plugin )
    {
        if ( !_bLoadAllUserInformationsFromWsso )
        {
            return IdxWSSODatabaseHome.findUserByGuid( strUserID, plugin, authenticationService );
        }

        IdxWSSODatabaseUser user = new IdxWSSODatabaseUser( strUserID, authenticationService );
        Cookie [ ] cookies = request.getCookies( );

        if ( cookies != null )
        {
            for ( Cookie cookie : cookies )
            {
                for ( String strUserInfo : _mapAttributeUserMapping.getOrDefault( cookie.getName( ), List.of( ) ) )
                {
                    user.setUserInfo( strUserInfo, cookie.getValue( ) );
                }
            }
        }

        return user;
    }
}
