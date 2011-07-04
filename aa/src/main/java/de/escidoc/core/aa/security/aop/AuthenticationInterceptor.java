/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.security.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import de.escidoc.core.aa.service.interfaces.UserManagementWrapperInterface;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.aop.AopUtil;
import de.escidoc.core.common.util.service.EscidocUserDetails;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.string.StringUtility;

/**
 * Interceptor used to authenticate the current user.<br>
 * <p/>
 * It must be the first Interceptor after the StatisticInterceptor, i.e. it has to be the second interceptor in the
 * chain.
 *
 * @author Torsten Tetteroo
 */
@Aspect
public class AuthenticationInterceptor implements Ordered {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    private static final String HANDLE_MUST_NOT_BE_NULL = "eSciDoc user handle must not be null";

    private static final String USER_CONTEXT_HAS_NOT_BEEN_CORRECTLY_SET_UP =
        "UserContext has not been correctly set up: " + HANDLE_MUST_NOT_BE_NULL;

    private static final String INTERNAL_INTERCEPTION_IS_DISABLED =
        "Internal interception is disabled, calling method without further" + " authorization";

    private static final String FAILED_TO_AUTHENTICATE_USER_BY_HANDLE =
        "Failed to authenticate user with provided handle";

    /**
     * The user details service.
     */
    private UserDetailsService userDetailsService;

    private UserManagementWrapperInterface userManagementWrapper;

    /**
     * See Interface for functional description.
     */
    @Override
    public int getOrder() {

        return AopUtil.PRECEDENCE_AUTHENTICATION_INTERCEPTOR;
    }

    /**
     * Before advice to perform the authentication of the user of the current request.
     *
     * @param joinPoint The current {@link JoinPoint}.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.application.security.AuthenticationException
     */
    @Before("execution(public * de.escidoc.core.*.service.*.*(..))"
        + " && !within(de.escidoc.core.aa.service.EscidocUserDetailsService)"
        + " && !within(de.escidoc.core.common.util.aop..*)")
    public void authenticate(final JoinPoint joinPoint) throws AuthenticationException, WebserverSystemException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(StringUtility.format("authenticate", this, UserContext.getSecurityContext()));
        }

        doAuthentication();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(StringUtility.format("continuation", this, UserContext.getSecurityContext()));
        }
    }

    /**
     * Does the authentication part of the interception.
     * <p/>
     * <p/>
     * Checks if an anonymous user tries to access the services.<br> In this case, the user id is set to the empty
     * string and the real name to "Anonymous".<br>
     * <p/>
     * Otherwise it retrieves the eSciDoc user handle from the UserContext and calls method {@code retrieve} from
     * UserAccountHandler using this key and extracts the internal user id and the real name of the user. <br>
     * This method stores the user id and the user's real name in the {@code UserContext}. <br>
     *
     * @throws AuthenticationException  Thrown if no user is found for the handle
     * @throws WebserverSystemException Thrown in case of an internal error
     */
    private void doAuthentication() throws AuthenticationException, WebserverSystemException {

        final String handle = UserContext.getHandle();
        if (handle == null) {
            throw new WebserverSystemException(USER_CONTEXT_HAS_NOT_BEEN_CORRECTLY_SET_UP, new NullPointerException(
                HANDLE_MUST_NOT_BE_NULL));
        }

        // check if internal interception has been set to false
        // if the user is the internal user, we don't
        // need to authorize since this has already been done.
        if (authenticateInternalUser()) {
            LOGGER.debug(INTERNAL_INTERCEPTION_IS_DISABLED);
            return;
        }

        if (UserContext.getId() != null) {
            return;
        }

        if (UserContext.isAnonymousUser()) {
            UserContext.setId(UserContext.ANONYMOUS_IDENTIFIER);
            UserContext.setRealName("Anonymous");
            return;
        }

        // retrieve the user id for the handle
        // throws an AuthenticationException if no user id for this handle
        // exists

        try {
            UserContext.setPrincipal((EscidocUserDetails) userDetailsService.loadUserByUsername(handle));
        }
        catch (final UsernameNotFoundException e) {
            throw new AuthenticationException(StringUtility.format(FAILED_TO_AUTHENTICATE_USER_BY_HANDLE, handle), e);
        }
        catch (final DataAccessException e) {
            throw new WebserverSystemException(e.getMessage(), e);
        }
        boolean wasExternalBefore = false;
        try {
            // Calls from the authorization component to other components
            // run
            // with privileges of the internal user (superuser).
            // They will not be further intercepted.
            wasExternalBefore = UserContext.runAsInternalUser();

            userManagementWrapper.initHandleExpiryTimestamp(handle);
        }
        catch (final SystemException e) {
            throw new WebserverSystemException(e);
        }
        finally {
            if (wasExternalBefore) {
                try {
                    UserContext.runAsExternalUser();
                }
                catch (final WebserverSystemException e) {
                    if (LOGGER.isWarnEnabled()) {
                        LOGGER.warn("Error on changing user context.");
                    }
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Error on changing user context.", e);
                    }
                }
            }
        }
    }

    /**
     * Authenticates the internal user.<br> The values for authentication are fetched from the
     * {@code UserContext}.
     *
     * @return Returns {@code true} in case of successfully authentication.
     * @throws WebserverSystemException Thrown in case of an internal error
     */
    private static boolean authenticateInternalUser() throws WebserverSystemException {
        return UserContext.isInternalUser();
    }

    /**
     * Injects the {@link UserDetailsService}.
     *
     * @param userDetailsService the {@link UserDetailsService} to inject.
     */
    public void setUserDetailsService(final UserDetailsService userDetailsService) {

        this.userDetailsService = userDetailsService;
    }

    /**
     * Injects the {@link UserManagementWrapperInterface}.
     *
     * @param userManagementWrapper the {@link UserManagementWrapperInterface} to inject.
     */
    public void setUserManagementWrapper(final UserManagementWrapperInterface userManagementWrapper) {
        this.userManagementWrapper = userManagementWrapper;
    }

}
