/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.util;

import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoUser;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import javax.naming.CommunicationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;


/**
 * Data authentication module for admin authentication
 */
public class LdapBrowser
{
    //ldap
    private static final String PROPERTY_INITIAL_CONTEXT_PROVIDER = "mylutece-wssodatabase.ldap.initialContextProvider";
    private static final String PROPERTY_PROVIDER_URL = "mylutece-wssodatabase.ldap.connectionUrl";
    private static final String PROPERTY_BIND_DN = "mylutece-wssodatabase.ldap.connectionName";
    private static final String PROPERTY_BIND_PASSWORD = "mylutece-wssodatabase.ldap.connectionPassword";
    private static final String PROPERTY_USER_DN_SEARCH_BASE = "mylutece-wssodatabase.ldap.userBase";
    private static final String PROPERTY_USER_DN_SEARCH_FILTER_BY_GUID = "mylutece-wssodatabase.ldap.userSearch.guid";
    private static final String PROPERTY_USER_DN_SEARCH_FILTER_BY_CRITERIA_SN = "mylutece-wssodatabase.ldap.userSearch.criteria.sn";
    private static final String PROPERTY_USER_DN_SEARCH_FILTER_BY_CRITERIA_GIVENNAME = "mylutece-wssodatabase.ldap.userSearch.criteria.givenname";
    private static final String PROPERTY_USER_DN_SEARCH_FILTER_BY_CRITERIA_MAIL = "mylutece-wssodatabase.ldap.userSearch.criteria.mail";
    private static final String PROPERTY_USER_SUBTREE = "mylutece-wssodatabase.ldap.userSubtree";
    private static final String PROPERTY_DN_ATTRIBUTE_GUID = "mylutece-wssodatabase.ldap.dn.attributeName.wssoGuid";
    private static final String PROPERTY_DN_ATTRIBUTE_FAMILY_NAME = "mylutece-wssodatabase.ldap.dn.attributeName.familyName";
    private static final String PROPERTY_DN_ATTRIBUTE_GIVEN_NAME = "mylutece-wssodatabase.ldap.dn.attributeName.givenName";
    private static final String PROPERTY_DN_ATTRIBUTE_EMAIL = "mylutece-wssodatabase.ldap.dn.attributeName.email";
    private static final String ATTRIBUTE_GUID = AppPropertiesService.getProperty( PROPERTY_DN_ATTRIBUTE_GUID );
    private static final String ATTRIBUTE_FAMILY_NAME = AppPropertiesService.getProperty( PROPERTY_DN_ATTRIBUTE_FAMILY_NAME );
    private static final String ATTRIBUTE_GIVEN_NAME = AppPropertiesService.getProperty( PROPERTY_DN_ATTRIBUTE_GIVEN_NAME );
    private static final String ATTRIBUTE_EMAIL = AppPropertiesService.getProperty( PROPERTY_DN_ATTRIBUTE_EMAIL );

    /* comparator for sorting - date ascendant order */
    private static final Comparator COMPARATOR_USER = new Comparator(  )
        {
            public int compare( Object obj1, Object obj2 )
            {
                WssoUser user1 = (WssoUser) obj1;
                WssoUser user2 = (WssoUser) obj2;

                int nOrder = user1.getLastName(  ).toUpperCase(  ).compareTo( user2.getLastName(  ).toUpperCase(  ) );

                if ( nOrder == 0 )
                {
                    nOrder = user1.getFirstName(  ).toUpperCase(  ).compareTo( user2.getFirstName(  ).toUpperCase(  ) );

                    if ( nOrder == 0 )
                    {
                        nOrder = user1.getEmail(  ).toUpperCase(  ).compareTo( user2.getEmail(  ).toUpperCase(  ) );
                    }
                }

                return nOrder;
            }
        };

    /**
     * Search controls for the user entry search
     */
    private SearchControls _scUserSearchControls;

    /**
     *
     */
    public LdapBrowser(  )
    {
    }

    /**
     * Returns a list of users corresponding to the given parameters. An empty parameter is remplaced by the wildcard (*)
     * @param strParameterLastName
     * @param strParameterFirstName
     * @param strParameterEmail
     * @return
     */
    public Collection getUserList( String strParameterLastName, String strParameterFirstName, String strParameterEmail )
    {
        ArrayList userList = new ArrayList(  );
        SearchResult sr = null;
        Object[] messageFormatParam = new Object[3];
        String[] messageFormatFilter = new String[3];

        DirContext context = null;

        messageFormatParam[0] = checkSyntax( strParameterLastName );
        messageFormatParam[1] = checkSyntax( strParameterFirstName );
        messageFormatParam[2] = checkSyntax( strParameterEmail );

        messageFormatFilter[0] = getUserDnSearchFilterByCriteriaSn(  );
        messageFormatFilter[1] = getUserDnSearchFilterByCriteriaGivenname(  );
        messageFormatFilter[2] = getUserDnSearchFilterByCriteriaMail(  );

        String strUserSearchFilter = buildRequest( messageFormatFilter, messageFormatParam );

        try
        {
            _scUserSearchControls = new SearchControls(  );
            _scUserSearchControls.setSearchScope( getUserDnSearchScope(  ) );
            _scUserSearchControls.setReturningObjFlag( true );
            _scUserSearchControls.setCountLimit( 0 );

            context = LdapUtil.getContext( getInitialContextProvider(  ), getProviderUrl(  ), getBindDn(  ),
                    getBindPassword(  ) );

            NamingEnumeration userResults = LdapUtil.searchUsers( context, strUserSearchFilter,
                    getUserDnSearchBase(  ), "", _scUserSearchControls );

            while ( ( userResults != null ) && userResults.hasMore(  ) )
            {
                sr = (SearchResult) userResults.next(  );

                Attributes attributes = sr.getAttributes(  );
                String strWssoId = "";

                if ( attributes.get( ATTRIBUTE_GUID ) != null )
                {
                    strWssoId = attributes.get( ATTRIBUTE_GUID ).get(  ).toString(  );
                }

                String strLastName = "";

                if ( attributes.get( ATTRIBUTE_FAMILY_NAME ) != null )
                {
                    strLastName = attributes.get( ATTRIBUTE_FAMILY_NAME ).get(  ).toString(  );
                }

                String strFirstName = "";

                if ( attributes.get( ATTRIBUTE_GIVEN_NAME ) != null )
                {
                    strFirstName = attributes.get( ATTRIBUTE_GIVEN_NAME ).get(  ).toString(  );
                }

                String strEmail = "";

                if ( attributes.get( ATTRIBUTE_EMAIL ) != null )
                {
                    strEmail = attributes.get( ATTRIBUTE_EMAIL ).get(  ).toString(  );
                }

                WssoUser user = new WssoUser(  );
                user.setGuid( strWssoId );
                user.setLastName( strLastName );
                user.setFirstName( strFirstName );
                user.setEmail( strEmail );
                userList.add( user );
            }

            Collections.sort( userList, COMPARATOR_USER );

            return userList;
        }
        catch ( CommunicationException e )
        {
            AppLogService.error( "Error while searching for users '" + "' with search filter : " +
                getDebugInfo( strUserSearchFilter ), e );

            return null;
        }
        catch ( NamingException e )
        {
            AppLogService.error( "Error while searching for users ", e );

            return null;
        }
        finally
        {
            try
            {
                LdapUtil.freeContext( context );
            }
            catch ( NamingException naming )
            {
                AppLogService.error( naming.getMessage(  ), naming );
            }
        }
    }

    /**
     * Return a user given its guid
     * @param strId the guid
     * @return the corresponding user
     */
    public WssoUser getUserPublicData( String strId )
    {
        WssoUser user = null;
        SearchResult sr = null;
        Object[] messageFormatParam = new Object[1];

        DirContext context = null;

        messageFormatParam[0] = strId;

        String strUserSearchFilter = MessageFormat.format( getUserDnSearchFilterByGUID(  ), messageFormatParam );

        try
        {
            _scUserSearchControls = new SearchControls(  );
            _scUserSearchControls.setSearchScope( getUserDnSearchScope(  ) );
            _scUserSearchControls.setReturningObjFlag( true );
            _scUserSearchControls.setCountLimit( 0 );

            context = LdapUtil.getContext( getInitialContextProvider(  ), getProviderUrl(  ), getBindDn(  ),
                    getBindPassword(  ) );

            NamingEnumeration userResults = LdapUtil.searchUsers( context, strUserSearchFilter,
                    getUserDnSearchBase(  ), "", _scUserSearchControls );

            int count = 0;

            while ( ( userResults != null ) && userResults.hasMore(  ) )
            {
                sr = (SearchResult) userResults.next(  );

                Attributes attributes = sr.getAttributes(  );
                String strWssoId = "";

                if ( attributes.get( ATTRIBUTE_GUID ) != null )
                {
                    strWssoId = attributes.get( ATTRIBUTE_GUID ).get(  ).toString(  );
                }

                String strLastName = "";

                if ( attributes.get( ATTRIBUTE_FAMILY_NAME ) != null )
                {
                    strLastName = attributes.get( ATTRIBUTE_FAMILY_NAME ).get(  ).toString(  );
                }

                String strFirstName = "";

                if ( attributes.get( ATTRIBUTE_GIVEN_NAME ) != null )
                {
                    strFirstName = attributes.get( ATTRIBUTE_GIVEN_NAME ).get(  ).toString(  );
                }

                String strEmail = "";

                if ( attributes.get( ATTRIBUTE_EMAIL ) != null )
                {
                    strEmail = attributes.get( ATTRIBUTE_EMAIL ).get(  ).toString(  );
                }

                user = new WssoUser(  );
                user.setGuid( strWssoId );
                user.setLastName( strLastName );
                user.setFirstName( strFirstName );
                user.setEmail( strEmail );
                count++;
            }

            // More than one user found (failure)
            if ( count > 1 )
            {
                AppLogService.error( "More than one entry in the directory for id " + strId );

                return null;
            }

            return user;
        }
        catch ( CommunicationException e )
        {
            AppLogService.error( "Error while searching for users '" + "' with search filter : " +
                getDebugInfo( strUserSearchFilter ), e );

            return null;
        }
        catch ( NamingException e )
        {
            AppLogService.error( "Error while searching for users ", e );

            return null;
        }
        finally
        {
            try
            {
                LdapUtil.freeContext( context );
            }
            catch ( NamingException naming )
            {
                AppLogService.error( naming.getMessage(  ), naming );
            }
        }
    }

    /**
     * Replace the null string or empty string by the wilcard
     * @param in
     * @return
     */
    private String checkSyntax( String in )
    {
        return ( ( ( in == null ) || ( in.equals( "" ) ) ) ? "*" : in );
    }

    /**
     * Return info for debugging
     * @param strUserSearchFilter
     * @return
     */
    private String getDebugInfo( String strUserSearchFilter )
    {
        StringBuffer sb = new StringBuffer(  );
        sb.append( "userBase : " );
        sb.append( getUserDnSearchBase(  ) );
        sb.append( "\nuserSearch : " );
        sb.append( strUserSearchFilter );

        return sb.toString(  );
    }

    /**
     * Get the initial context provider from the properties
     * @return
     */
    private String getInitialContextProvider(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_INITIAL_CONTEXT_PROVIDER );
    }

    /**
     * Get the provider url from the properties
     * @return
     */
    private String getProviderUrl(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_PROVIDER_URL );
    }

    /**
     * Get the base user dn from the properties
     * @return
     */
    private String getUserDnSearchBase(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_USER_DN_SEARCH_BASE );
    }

    /**
     * Get the filter for search by guid
     * @return
     */
    private String getUserDnSearchFilterByGUID(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_USER_DN_SEARCH_FILTER_BY_GUID );
    }

    /**
     * Get the filter for search by sn
     * @return
     */
    private String getUserDnSearchFilterByCriteriaSn(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_USER_DN_SEARCH_FILTER_BY_CRITERIA_SN );
    }

    /**
     * Get the filter for search by givenname
     * @return
     */
    private String getUserDnSearchFilterByCriteriaGivenname(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_USER_DN_SEARCH_FILTER_BY_CRITERIA_GIVENNAME );
    }

    /**
     * Get the filter for search by mail
     * @return
     */
    private String getUserDnSearchFilterByCriteriaMail(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_USER_DN_SEARCH_FILTER_BY_CRITERIA_MAIL );
    }

    /**
     * Get the user dn search scope
     * @return
     */
    private int getUserDnSearchScope(  )
    {
        String strSearchScope = AppPropertiesService.getProperty( PROPERTY_USER_SUBTREE );

        if ( strSearchScope.equalsIgnoreCase( "true" ) )
        {
            return SearchControls.SUBTREE_SCOPE;
        }

        return SearchControls.ONELEVEL_SCOPE;
    }

    /**
     * get the bind dn
     * @return
     */
    private String getBindDn(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_BIND_DN );
    }

    /**
     * Get the bing password
     * @return
     */
    private String getBindPassword(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_BIND_PASSWORD );
    }

    /**
     * build request for search by sn, givenname and mail
     * @return
     */
    private String buildRequest( String[] messageFormatFilter, Object[] messageFormatParam )
    {
        String strUserSearchFilter = "(&";

        for ( int i = 0; i < messageFormatParam.length; i++ )
        {
            if ( messageFormatParam[i].equals( "*" ) == false )
            {
                strUserSearchFilter += MessageFormat.format( messageFormatFilter[i], messageFormatParam );
            }
        }

        strUserSearchFilter += ")";

        return strUserSearchFilter;
    }
}
