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
package de.escidoc.core.test.common.client.servlet.aa;

import de.escidoc.core.test.common.client.servlet.ClientBase;
import de.escidoc.core.test.common.client.servlet.HttpHelper;
import org.apache.http.HttpResponse;

/**
 * Offers access methods to the escidoc interface of the user management wrapper.
 *
 * @author Torsten Tetteroo
 */
public class UserManagementWrapperClient extends ClientBase {

    /**
     * Login an user.
     *
     * @param loginName                     The login name of the user.
     * @param password                      The password of the user.
     * @param expectedAuthenticationFailure Flag indicating that the provided values should cause a failed
     *                                      authentication, i.e. login page will be presented to the user as the
     *                                      result.
     * @param accountIsDeactivated          Flag indicating that the authenticated user account should be deactivated.
     * @param targetUrl                     The target url to that the user shall be redirected.
     * @param encodeTargetUrlSlashes        Flag indicating that the slashes contained in the targetUrl shall be encoded
     *                                      (<code>true</code>) or shall not be encoded (<code>false</code>).
     * @return The HttpMethod after the service call (REST).
     * @throws Exception If the service call fails.
     */
    public HttpResponse login(
        final String loginName, final String password, final boolean expectedAuthenticationFailure,
        final boolean accountIsDeactivated, final String targetUrl, final boolean encodeTargetUrlSlashes)
        throws Exception {

        return HttpHelper.performLogin(getHttpClient(), loginName, password, expectedAuthenticationFailure,
            accountIsDeactivated, targetUrl, encodeTargetUrlSlashes);
    }

    /**
     * Logout an user.
     *
     * @param userHandle The eSciDOc user handle that shall be sent in the cookie of the logout request.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object logout(final String userHandle) throws Exception {

        return HttpHelper.performLogout(getHttpClient(), null, userHandle, true);
    }

    /**
     * Logout an user.
     *
     * @param redirectUrl              The target to that the user shall be redirected after being logged out. This may
     *                                 be <code>null</code> (no redirect).
     * @param userHandle               The eSciDOc user handle that shall be sent in the cookie of the logout request.
     * @param encodeRedirectUrlSlashes Flag indicating that the slashes contained in the redirectUrl shall be encoded
     *                                 (<code>true</code>) or shall not be encoded (<code>false</code>).
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object logout(final String redirectUrl, final String userHandle, final boolean encodeRedirectUrlSlashes)
        throws Exception {

        return HttpHelper.performLogout(getHttpClient(), redirectUrl, userHandle, encodeRedirectUrlSlashes);
    }

}
