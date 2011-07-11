/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.common.util.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.string.StringUtility;

/**
 * Using the SecurityAssociation class to get information about the user in the local environment per request.
 *
 * @author Bernhard Kraus, Roland Werner (Accenture), TTE
 */
public final class UserContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserContext.class);

    private static final String MISSING_REQUEST_DETAIL = "Missing request detail.";

    /**
     * The string for the handle and id of the anonymous user.
     */
    public static final String ANONYMOUS_IDENTIFIER = "";

    private static final String USER_CONTEXT_IS_NOT_INITIALIZED = "UserContext is not initialized";

    public static final int UNRESTRICTED_PERMISSION = 0;

    public static final int RESTRICTED_PERMISSION_RELEASES_ONLY = 1;

    /**
     * Private constructor to prevent initialization.
     */
    private UserContext() {
    }

    /**
     * Sets the user context. The user context is put into a spring security {@link Authentication} object, which is put
     * into an spring security {@link SecurityContext} object and finally stored via the spring security {@link
     * SecurityContextHolder}.<br> A previous UserContext is deleted by this method.<br> This method must not be used to
     * set an eSciDoc special user, e.g. authorization user. This is only possible by initializing the
     * UserContextPrincipal via the setTechnicalName method.<br> This method initializes an unauthenticated {@link
     * Authentication} object in the security context.
     *
     * @param handle The handle identifying the user.<br> This parameter is optional. If it is not provided, an empty
     *               {@link String} is stored as the eSciDoc user handle identifying anonymous access.
     * @throws MissingMethodParameterException
     *                                  Thrown if a mandatory parameter is not provided.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public static void setUserContext(final String handle) {
        String hd = handle;
        if (hd == null) {
            hd = ANONYMOUS_IDENTIFIER;
        }
        SecurityContextHolder.clearContext();
        final UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(new EscidocUserDetails(), hd);
        authentication.setAuthenticated(false);
        authentication.setDetails(new EscidocRequestDetail());
        final SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    /**
     * Sets the user context. The user context is stored in the provided {@link SecurityContext} object. This object is
     * stored via the spring security {@link SecurityContextHolder}</code>.<br> This constructor is used to forward the
     * security context to another service. The provided security context is checked for being created by the base
     * services to prevent setting it by external services.<br> A previous UserContext is deleted by this method.
     *
     * @param context The principal storing information about the user. This principal must be signed by a known key.
     *                Otherwise, an exception is thrown.
     * @throws MissingMethodParameterException
     *                                  Thrown if a mandatorz parameter is not provided.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public static void setUserContext(final SecurityContext context) throws MissingMethodParameterException,
        WebserverSystemException {
        if (context == null) {
            throw new MissingMethodParameterException("No security context provided");
        }
        SecurityContextHolder.clearContext();
        SecurityContextHolder.setContext(context);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(StringUtility.format("Stored provided security context", getSecurityContext()));
        }
    }

    /**
     * Executes the following accesses of the user identified by the current {@link Authentication} with the privileges
     * of the internal user. If the current access is running with the privileges of the internal user, the {@link
     * Authentication} is kept unchanged.
     *
     * @return Returns {@code true} if the {@link Authentication} has been changed, {@code false} else.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public static boolean runAsInternalUser() throws WebserverSystemException {
        final Authentication authentication = getSecurityContext().getAuthentication();
        if (!(authentication instanceof EscidocRunAsInternalUserToken)) {
            getSecurityContext().setAuthentication(new EscidocRunAsInternalUserToken(authentication));
            return true;
        }
        return false;
    }

    /**
     * Reverts executing the following accesses with the privileges of the internal user. If the current access is not
     * executed with the internal user's privileges, the {@link Authentication} is kept unchanged.
     *
     * @return Returns {@code true} if the {@link Authentication} has been changed, {@code false} else.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public static boolean runAsExternalUser() throws WebserverSystemException {
        final Authentication authentication = getSecurityContext().getAuthentication();
        if (authentication instanceof EscidocRunAsInternalUserToken) {
            getSecurityContext().setAuthentication(
                ((EscidocRunAsInternalUserToken) authentication).getOrginalAuthentication());
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Sets the id of the user. The id is put into the principal of an spring security {@link Authentication} object,
     * which is put into an spring security {@link SecurityContext} object and finally stored via the spring security
     * {@link SecurityContextHolder}.<br> Before calling this method, the user context must be set by using the
     * setContext method. Otherwise, an exception is thrown here.<br> This method resets the signature of the principal
     * and it has to be resigned.<br>
     *
     * @param id The id of the user. This may be {@code null} or an empty string. e.g. in case of an anonymous
     *           user.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public static void setId(final String id) throws WebserverSystemException {
        getPrincipal().setId(id);
    }

    /**
     * Sets the real name of the user as stored in the UserAccount. The user's real name is put into the principal of an
     * spring security {@link Authentication} object, which is put into an spring security {@link SecurityContext}
     * object and finally stored via the spring security {@link SecurityContextHolder}.<br> Before calling this method,
     * the user context must be set by using the setContext method. Otherwise, an exception is thrown here.<br> This
     * method resets the signature of the principal and it has to be resigned.
     *
     * @param realName The real name of the user. This must not be {@code null}.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public static void setRealName(final String realName) throws WebserverSystemException {
        if (realName == null) {
            return;
        }
        getPrincipal().setRealName(realName);
    }

    /**
     * Gets the security context.<br> Before calling this method, the user context must be set by using the setContext
     * method. Otherwise, an exception is thrown here.
     *
     * @return Returns the security context.
     * @throws WebserverSystemException Thrown in case of an uninitialized user context.
     */
    public static SecurityContext getSecurityContext() throws WebserverSystemException {
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext == null) {
            throw new WebserverSystemException(USER_CONTEXT_IS_NOT_INITIALIZED);
        }
        return securityContext;
    }

    /**
     * Gets the authentication token stored within the user context.<br> Before calling this method, the user context
     * must be set by using the setContext method. Otherwise, an exception is thrown here.
     *
     * @return Returns the authentication token.
     * @throws WebserverSystemException Thrown in case of an uninitialized user context.
     */
    private static Authentication getAuthentication() throws WebserverSystemException {
        final Authentication authenticationToken = getSecurityContext().getAuthentication();
        if (authenticationToken == null) {
            throw new WebserverSystemException(USER_CONTEXT_IS_NOT_INITIALIZED);
        }
        return authenticationToken;
    }

    /**
     * Gets the principal stored within the user context.<br> Before calling this method, the user context must be set
     * by using the setContext method. Otherwise, an exception is thrown here.
     *
     * @return Returns the {@link EscidocUserDetails} object holding the information stored about the user.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    private static EscidocUserDetails getPrincipal() throws WebserverSystemException {
        return (EscidocUserDetails) getAuthentication().getPrincipal();
    }

    /**
     * Sets the principal stored within the user context.<br> Before calling this method, the user context must be set
     * by using the setContext method. Otherwise, an exception is thrown here.<br> Note: This method establishes a new
     * {@link Authentication} in the {@link SecurityContext}.
     *
     * @param principal The principal to set.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public static void setPrincipal(final EscidocUserDetails principal) throws WebserverSystemException {
        final Authentication oldAuthentication = getAuthentication();
        final boolean isInternaluser = isInternalUser();
        final UsernamePasswordAuthenticationToken newAuthentication =
            new UsernamePasswordAuthenticationToken(principal, oldAuthentication.getCredentials());
        newAuthentication.setDetails(oldAuthentication.getDetails());
        getSecurityContext().setAuthentication(newAuthentication);
        // restore run as internal user context if the original authentication was one.
        if (isInternaluser) {
            runAsInternalUser();
        }
    }

    /**
     * Gets the user id from the user context. The user id is fetched from the principals of the authentication token
     * fetched from the spring security {@link SecurityContextHolder}.<br> Before calling this method, the user context
     * must be set by using the setContext method. Otherwise, an exception is thrown here.
     *
     * @return Returns the user id.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public static String getId() throws WebserverSystemException {
        return getPrincipal().getId();
    }

    /**
     * Gets the real name of the user (as stored in the UserAccount) from the user context. The real name is fetched
     * from the principals of the authentication token fetched from the spring security {@link
     * SecurityContextHolder}.<br> Before calling this method, the user context must be set by using the setContext
     * method. Otherwise, an exception is thrown here.
     *
     * @return Returns the real name of the user or {@code null} if this has not been set in the UserContext.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public static String getRealName() throws WebserverSystemException {
        return getPrincipal().getRealName();
    }

    /**
     * Gets the user handle (the credential) from the user context. The handle is fetched from the credentials of the
     * authentication token context is fetched from the spring security {@link SecurityContextHolder}.<br> Before
     * calling this method, the user context must be set by using the setContext method. Otherwise, an exception is
     * thrown here.
     *
     * @return Returns the handle of the user.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public static String getHandle() throws WebserverSystemException {
        return (String) getAuthentication().getCredentials();
    }

    /**
     * Checks if the stored user context is the context of an external user.<br> Before calling this method, the user
     * context must be set by using the setContext method. Otherwise, an exception is thrown here.
     *
     * @return Returns {@code true} if the context has been initialized and the technical user name is the name of
     *         the external super user.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public static boolean isExternalUser() throws WebserverSystemException {
        return !(getAuthentication() instanceof EscidocRunAsInternalUserToken);
    }

    /**
     * Checks if the stored user context is the context of the internal user.<br> Before calling this method, the user
     * context must be set by using the setContext method. Otherwise, an exception is thrown here.
     *
     * @return Returns {@code true} if the context has been initialized and the technical user name is the name of
     *         the internal user.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public static boolean isInternalUser() throws WebserverSystemException {
        return getAuthentication() instanceof EscidocRunAsInternalUserToken;
    }

    /**
     * Checks if the stored user context is the context of an anonymous user.
     *
     * @return Returns {@code true} if the context has been initialized and the handle equals to an empty {@link
     *         String}, as this identifies the anonymous user.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public static boolean isAnonymousUser() throws WebserverSystemException {
        return ANONYMOUS_IDENTIFIER.equals(getHandle());
    }

    /**
     * Checks if the provided user id matches an anonymous user.
     *
     * @param userId The user id to check.
     * @return Returns {@code true} if the provided user id identifies an anonymous User. This is the case, if it
     *         is an empty string.
     */
    public static boolean isIdOfAnonymousUser(final String userId) {
        return ANONYMOUS_IDENTIFIER.equals(userId);
    }

    /**
     * Sets restricted permissions, e.g. retrieval restricted to releases.
     *
     * @param restrictedPermissionCode The code identifying the restricted permissions.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public static void setRestrictedPermissions(final int restrictedPermissionCode) throws WebserverSystemException {
        getRequestDetails().setRestrictedPermissions(restrictedPermissionCode);
    }

    /**
     * Gets the restricted permissions.
     *
     * @return Returns the code identifying the restricted permissions.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public static int getRestrictedPermissions() throws WebserverSystemException {
        return getRequestDetails().getRestrictedPermissions();
    }

    /**
     * Checks if the retrieve of a resource object is restricted to released versions.<br> This method may be used by
     * business logic to decide if released versions or all versions are provided for the current user.
     *
     * @return Returns {@code true} if the current retrieve access is restricted to released versions.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public static boolean isRetrieveRestrictedToReleased() throws WebserverSystemException {
        return (getRequestDetails().getRestrictedPermissions() & RESTRICTED_PERMISSION_RELEASES_ONLY) != 0;
    }

    /**
     * Gets the {@link EscidocRequestDetail} that is stored in the {@link Authentication} object of the {@link
     * SecurityContext}.
     *
     * @return Returns the {@link EscidocRequestDetail}.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    private static EscidocRequestDetail getRequestDetails() throws WebserverSystemException {

        final Object details = getAuthentication().getDetails();
        if (details == null) {
            throw new WebserverSystemException(StringUtility.format(USER_CONTEXT_IS_NOT_INITIALIZED,
                MISSING_REQUEST_DETAIL));
        }
        return (EscidocRequestDetail) details;
    }

}
