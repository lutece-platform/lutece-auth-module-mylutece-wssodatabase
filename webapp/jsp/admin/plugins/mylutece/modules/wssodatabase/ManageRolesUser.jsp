<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:include page="../../../../AdminHeader.jsp" />

<jsp:useBean id="wssoUser" scope="session" class="fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.web.WssodatabaseJspBean" />

<% wssoUser.init( request, wssoUser.RIGHT_MANAGE_WSSO_USERS ); %>
<%= wssoUser.getManageRolesUser( request ) %>

<%@ include file="../../../../AdminFooter.jsp" %>
