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
package fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.web;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoUser;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoUserHome;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoUserRoleHome;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.service.WssoDatabasePlugin;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.util.LdapBrowser;
import fr.paris.lutece.plugins.mylutece.service.RoleResourceIdService;
import fr.paris.lutece.portal.business.role.Role;
import fr.paris.lutece.portal.business.role.RoleHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.util.url.UrlItem;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Collection;

/**
 * Back office management of the WSSO users: import from the directory, roles, removal.
 */
@RequestScoped
@Named( "myluteceWssodatabaseJspBean" )
@Controller( controllerJsp = "ManageUsers.jsp", controllerPath = "jsp/admin/plugins/mylutece/modules/wssodatabase/", right = WssodatabaseJspBean.RIGHT_MANAGE_WSSO_USERS, securityTokenEnabled = true )
public class WssodatabaseJspBean extends MVCAdminJspBean
{
    public static final String RIGHT_MANAGE_WSSO_USERS = "WSSODATABASE_MANAGEMENT_USERS";

    private static final long serialVersionUID = 1L;

    private static final String VIEW_MANAGE_USERS = "manageUsers";
    private static final String VIEW_CREATE_USER = "createUser";
    private static final String VIEW_CONFIRM_REMOVE_USER = "confirmRemoveUser";
    private static final String VIEW_MANAGE_ROLES_USER = "manageRolesUser";
    private static final String ACTION_CREATE_USER = "createUser";
    private static final String ACTION_REMOVE_USER = "removeUser";
    private static final String ACTION_ASSIGN_ROLE_USER = "assignRoleUser";

    private static final String PROPERTY_PAGE_TITLE_MANAGE_USERS = "module.mylutece.wssodatabase.manage_users.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MANAGE_ROLES_USER = "module.mylutece.wssodatabase.manage_roles_user.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_USER = "module.mylutece.wssodatabase.create_user.pageTitle";

    private static final String MESSAGE_CONFIRM_REMOVE_USER = "module.mylutece.wssodatabase.message.confirmRemoveUser";
    private static final String MESSAGE_USER_EXIST = "module.mylutece.wssodatabase.message.user_exist";
    private static final String MESSAGE_ERROR_CREATE_USER = "module.mylutece.wssodatabase.message.create.user";
    private static final String MESSAGE_ERROR_REMOVE_USER = "module.mylutece.wssodatabase.message.remove.user";
    private static final String MESSAGE_ERROR_USER_NOT_FOUND = "module.mylutece.wssodatabase.message.user_not_found";

    private static final String PARAMETER_MYLUTECE_WSSO_ROLE_ID = "mylutece_wsso_role_id";
    private static final String PARAMETER_MYLUTECE_WSSO_USER_ID = "mylutece_wsso_user_id";
    private static final String PARAMETER_GUID = "guid";
    private static final String PARAMETER_LAST_NAME = "last_name";
    private static final String PARAMETER_FIRST_NAME = "first_name";
    private static final String PARAMETER_EMAIL = "email";

    private static final String MARK_ROLES_LIST = "role_list";
    private static final String MARK_ROLES_LIST_FOR_USER = "user_role_list";
    private static final String MARK_USERS_LIST = "user_list";
    private static final String MARK_USER = "user";
    private static final String MARK_LAST_NAME = "last_name";
    private static final String MARK_FIRST_NAME = "first_name";
    private static final String MARK_EMAIL = "email";
    private static final String MARK_IS_SEARCH = "is_search";

    private static final String TEMPLATE_CREATE_USER = "admin/plugins/mylutece/modules/wssodatabase/create_user.html";
    private static final String TEMPLATE_MANAGE_USERS = "admin/plugins/mylutece/modules/wssodatabase/manage_users.html";
    private static final String TEMPLATE_MANAGE_ROLES_USER = "admin/plugins/mylutece/modules/wssodatabase/manage_roles_user.html";

    private static final String CONSTANT_WILDCARD = "*";

    /**
     * {@inheritDoc}
     */
    @Override
    public Plugin getPlugin( )
    {
        return PluginService.getPlugin( WssoDatabasePlugin.PLUGIN_NAME );
    }

    /**
     * Returns the list of the WSSO users.
     *
     * @param model
     *            the model
     * @return the page
     */
    @View( value = VIEW_MANAGE_USERS, defaultView = true )
    public String getManageUsers( Models model )
    {
        model.put( MARK_USERS_LIST, WssoUserHome.findWssoUsersList( getPlugin( ) ) );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_USERS, TEMPLATE_MANAGE_USERS );
    }

    /**
     * Returns the directory search form and the users it found.
     *
     * @param request
     *            the request
     * @param model
     *            the model
     * @return the page
     */
    @View( value = VIEW_CREATE_USER, securityTokenAction = ACTION_CREATE_USER )
    public String getCreateUser( HttpServletRequest request, Models model )
    {
        String strUserLastName = StringUtils.defaultString( request.getParameter( PARAMETER_LAST_NAME ) );
        String strUserFirstName = StringUtils.defaultString( request.getParameter( PARAMETER_FIRST_NAME ) );
        String strUserEMail = StringUtils.defaultString( request.getParameter( PARAMETER_EMAIL ) );
        boolean bSearch = !( strUserLastName.isEmpty( ) && strUserFirstName.isEmpty( ) && strUserEMail.isEmpty( ) );
        Collection<WssoUser> userList = null;

        if ( bSearch )
        {
            userList = new LdapBrowser( ).getUserList( strUserLastName + CONSTANT_WILDCARD, strUserFirstName + CONSTANT_WILDCARD,
                    strUserEMail + CONSTANT_WILDCARD );
        }

        model.put( MARK_USERS_LIST, userList );
        model.put( MARK_IS_SEARCH, bSearch );
        model.put( MARK_LAST_NAME, strUserLastName );
        model.put( MARK_FIRST_NAME, strUserFirstName );
        model.put( MARK_EMAIL, strUserEMail );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_USER, TEMPLATE_CREATE_USER );
    }

    /**
     * Imports a user of the directory.
     *
     * @param request
     *            the request
     * @return the redirection
     */
    @Action( ACTION_CREATE_USER )
    public String doCreateUser( HttpServletRequest request )
    {
        String strUserGuid = request.getParameter( PARAMETER_GUID );

        if ( StringUtils.isBlank( strUserGuid ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_CREATE_USER, AdminMessage.TYPE_ERROR ) );
        }

        if ( !WssoUserHome.findWssoUsersListForGuid( strUserGuid, getPlugin( ) ).isEmpty( ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_USER_EXIST, AdminMessage.TYPE_STOP ) );
        }

        WssoUser user = new LdapBrowser( ).getUserPublicData( strUserGuid );

        if ( user == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_CREATE_USER, AdminMessage.TYPE_ERROR ) );
        }

        WssoUserHome.create( user, getPlugin( ) );

        return redirectView( request, VIEW_MANAGE_USERS );
    }

    /**
     * Asks the confirmation of a user removal.
     *
     * @param request
     *            the request
     * @return the redirection to the confirmation message
     */
    @View( value = VIEW_CONFIRM_REMOVE_USER, securityTokenAction = ACTION_REMOVE_USER )
    public String getConfirmRemoveUser( HttpServletRequest request )
    {
        WssoUser user = findUser( request );

        if ( user == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_USER_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_USER ) );
        url.addParameter( PARAMETER_MYLUTECE_WSSO_USER_ID, user.getMyluteceWssoUserId( ) );

        return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_USER, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Removes a user and its roles.
     *
     * @param request
     *            the request
     * @return the redirection
     */
    @Action( ACTION_REMOVE_USER )
    public String doRemoveUser( HttpServletRequest request )
    {
        WssoUser user = findUser( request );

        if ( user == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_REMOVE_USER, AdminMessage.TYPE_ERROR ) );
        }

        WssoUserHome.remove( user, getPlugin( ) );
        WssoUserRoleHome.deleteRolesForUser( user.getMyluteceWssoUserId( ), getPlugin( ) );

        return redirectView( request, VIEW_MANAGE_USERS );
    }

    /**
     * Returns the roles of a user.
     *
     * @param request
     *            the request
     * @param model
     *            the model
     * @return the page
     */
    @View( value = VIEW_MANAGE_ROLES_USER, securityTokenAction = ACTION_ASSIGN_ROLE_USER )
    public String getManageRolesUser( HttpServletRequest request, Models model )
    {
        WssoUser user = findUser( request );

        if ( user == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_USER_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        Collection<Role> allRoleList = RBACService.getAuthorizedCollection( RoleHome.findAll( ), RoleResourceIdService.PERMISSION_ASSIGN_ROLE,
                (User) getUser( ) );

        model.put( MARK_ROLES_LIST, allRoleList );
        model.put( MARK_ROLES_LIST_FOR_USER, WssoUserRoleHome.findRolesListForUser( user.getMyluteceWssoUserId( ), getPlugin( ) ) );
        model.put( MARK_USER, user );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_ROLES_USER, TEMPLATE_MANAGE_ROLES_USER );
    }

    /**
     * Replaces the roles of a user by the checked ones.
     *
     * @param request
     *            the request
     * @return the redirection
     */
    @Action( ACTION_ASSIGN_ROLE_USER )
    public String doAssignRoleUser( HttpServletRequest request )
    {
        WssoUser user = findUser( request );

        if ( user == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_USER_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        WssoUserRoleHome.deleteRolesForUser( user.getMyluteceWssoUserId( ), getPlugin( ) );

        String [ ] roleArray = request.getParameterValues( PARAMETER_MYLUTECE_WSSO_ROLE_ID );

        if ( roleArray != null )
        {
            for ( String strRole : roleArray )
            {
                WssoUserRoleHome.createRoleForUser( user.getMyluteceWssoUserId( ), strRole, getPlugin( ) );
            }
        }

        return redirectView( request, VIEW_MANAGE_USERS );
    }

    /**
     * Loads the user named by the request.
     *
     * @param request
     *            the request
     * @return the user, or null if the id is missing or unknown
     */
    private WssoUser findUser( HttpServletRequest request )
    {
        int nUserId = NumberUtils.toInt( request.getParameter( PARAMETER_MYLUTECE_WSSO_USER_ID ), -1 );

        return ( nUserId < 0 ) ? null : WssoUserHome.findByPrimaryKey( nUserId, getPlugin( ) );
    }
}
