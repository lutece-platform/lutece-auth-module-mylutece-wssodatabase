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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.mylutece.authentication.MultiLuteceAuthentication;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.IdxWSSODatabaseAuthentication;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.IdxWSSODatabaseUser;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.IdxWSSODatabaseHome;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoUser;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.security.LuteceAuthentication;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 *
 * LdapDatabaseService
 *
 */
public class WssoDatabaseService
{
    private static final String AUTHENTICATION_BEAN_NAME = "mylutece-wssodatabase.authentication";
    private static final String PROPERTY_LOAD_ALL_USER_INFORMATIONS_FROM_WSSO = "mylutece-wssodatabase.loadAllUserInformationsFromWsso";
    private static final String PROPERTY_USER_MAPPING_ATTRIBUTES = "mylutece-wssodatabase.userMappingAttributes";
    // true if the user informations informations must be load from wsso
    private boolean _bLoadAllUserInformationsFomWsso;
    private static final String CONSTANT_LUTECE_USER_PROPERTIES_PATH = "mylutece-wssodatabase.attribute";
    private Map<String, List<String>> ATTRIBUTE_USER_MAPPING;
    private static final String SEPARATOR = ",";

    private static WssoDatabaseService _singleton = new WssoDatabaseService( );

    /**
     * Initialize the WssoDatabase service
     *
     */
    public void init( )
    {
        WssoUser.init( );

        IdxWSSODatabaseAuthentication baseAuthentication = (IdxWSSODatabaseAuthentication) SpringContextService.getPluginBean( WssoDatabasePlugin.PLUGIN_NAME,
                AUTHENTICATION_BEAN_NAME );
        _bLoadAllUserInformationsFomWsso = AppPropertiesService.getPropertyBoolean( PROPERTY_LOAD_ALL_USER_INFORMATIONS_FROM_WSSO, false );

        if ( _bLoadAllUserInformationsFomWsso )
        {
            String strUserMappingAttributes = AppPropertiesService.getProperty( PROPERTY_USER_MAPPING_ATTRIBUTES );

            ATTRIBUTE_USER_MAPPING = new HashMap<String, List<String>>( );

            if ( StringUtils.isNotBlank( strUserMappingAttributes ) )
            {
                String [ ] tabUserProperties = strUserMappingAttributes.split( SEPARATOR );
                String [ ] tabPropertiesValues;
                String userProperties;

                for ( int i = 0; i < tabUserProperties.length; i++ )
                {
                    userProperties = AppPropertiesService.getProperty( CONSTANT_LUTECE_USER_PROPERTIES_PATH + "." + tabUserProperties [i] );

                    if ( StringUtils.isNotBlank( userProperties ) )
                    {

                        if ( userProperties.contains( SEPARATOR ) )
                        {
                            tabPropertiesValues = userProperties.split( SEPARATOR );

                            for ( int n = 0; i < tabPropertiesValues.length; n++ )
                            {
                                if ( !ATTRIBUTE_USER_MAPPING.containsKey( tabPropertiesValues [n] ) )
                                {
                                    ATTRIBUTE_USER_MAPPING.put( tabPropertiesValues [n], new ArrayList<String>( ) );
                                }
                                ATTRIBUTE_USER_MAPPING.get( tabPropertiesValues [n] ).add( tabUserProperties [i] );
                            }

                        }
                        else
                        {

                            if ( !ATTRIBUTE_USER_MAPPING.containsKey( userProperties ) )
                            {
                                ATTRIBUTE_USER_MAPPING.put( userProperties, new ArrayList<String>( ) );
                            }
                            ATTRIBUTE_USER_MAPPING.get( userProperties ).add( tabUserProperties [i] );
                        }

                    }
                }
            }
        }

        if ( baseAuthentication != null )
        {
            MultiLuteceAuthentication.registerAuthentication( baseAuthentication );
        }
        else
        {
            AppLogService.error( "IdxWSSODatabaseAuthentication not found, please check your wssodatabase_context.xml configuration" );
        }
    }

    

    /**
     * Load IdxWssoUSer
     * @param strUserID the userId
     * @param request the request
     * @param authenticationService the authentication service
     * @param plugin the plugin
     * @return IdxWSSODatabaseUser
     */
    public IdxWSSODatabaseUser loadIdxWSSOUser( String strUserID, HttpServletRequest request, LuteceAuthentication authenticationService ,Plugin plugin)
    {
        IdxWSSODatabaseUser user =null;
        if ( !isLoadAllUserInformationsFromWssoEnabled( ) )
        {
            user = IdxWSSODatabaseHome.findUserByGuid( strUserID, plugin, authenticationService );
        }
        else
        {
            Cookie [ ] cookies = request.getCookies( );

             user = new IdxWSSODatabaseUser( strUserID, authenticationService );
            if ( cookies != null )
            {
                for ( int i = 0; i < cookies.length; i++ )
                {

                    if ( ATTRIBUTE_USER_MAPPING.containsKey( cookies [i].getName( ) ) )
                    {
                        for ( String strUserInfo : ATTRIBUTE_USER_MAPPING.get( cookies [i].getName( ) ) )
                        {
                            Object val = cookies [i].getValue( );
                            if ( val instanceof ArrayList<?> )
                            {

                                StringBuffer strBufVal = new StringBuffer( );
                                for ( String tabVal : (ArrayList<String>) val )
                                {
                                    strBufVal.append( tabVal );
                                    strBufVal.append( SEPARATOR );
                                }
                                if ( strBufVal.length( ) > 0 )
                                {
                                    user.setUserInfo( strUserInfo, strBufVal.substring( 0, strBufVal.length( ) - 1 ) );
                                }

                                user.setUserInfo( strUserInfo, strBufVal.toString( ) );

                            }
                            else
                            {
                                user.setUserInfo( strUserInfo, (String) val );

                            }
                        }
                    }
                }

            }
          
        }
        return user;
    }
    /**
     * 
     * @return true if the user informations informations must be load from wsso
     */
    private boolean isLoadAllUserInformationsFromWssoEnabled( )
    {

        return _bLoadAllUserInformationsFomWsso;
    }

    /**
     * Returns the instance of the singleton
     *
     * @return The instance of the singleton
     */
    public static WssoDatabaseService getInstance( )
    {
        return _singleton;
    }
}
