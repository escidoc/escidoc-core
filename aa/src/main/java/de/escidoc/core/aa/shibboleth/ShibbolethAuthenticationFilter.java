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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.shibboleth;

import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.ui.SpringSecurityFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Matcher;

public class ShibbolethAuthenticationFilter extends SpringSecurityFilter {

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @param request
     * @param response
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     * @see org.springframework.security.ui.SpringSecurityFilter#doFilterHttp(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
     * @aa
     */
    @Override
    protected void doFilterHttp(
        final HttpServletRequest request, final HttpServletResponse response,
        final FilterChain filterChain) throws IOException, ServletException {

        final String shibSessionId =
            request.getHeader(ShibbolethDetails.SHIB_SESSION_ID);
        if (shibSessionId != null && shibSessionId.length() != 0) {
            final ShibbolethDetails details =
                new ShibbolethDetails(
                    null,
                    request.getHeader(ShibbolethDetails.SHIB_ASSERTION_COUNT),
                    request
                        .getHeader(ShibbolethDetails.SHIB_AUTHENTICATION_METHOD),
                    request
                        .getHeader(ShibbolethDetails.SHIB_AUTHENTICATION_INSTANT),
                    request
                        .getHeader(ShibbolethDetails.SHIB_AUTHNCONTEXT_CLASS),
                    request.getHeader(ShibbolethDetails.SHIB_AUTHNCONTEXT_DECL),
                    request.getHeader(ShibbolethDetails.SHIB_IDENTITY_PROVIDER),
                    shibSessionId);

            final ShibbolethUser user = new ShibbolethUser();
            final String cnAttribute =
                EscidocConfiguration
                    .getInstance()
                    .get(
                        EscidocConfiguration.ESCIDOC_CORE_AA_COMMON_NAME_ATTRIBUTE_NAME);
            final String uidAttribute =
                EscidocConfiguration
                    .getInstance()
                    .get(
                        EscidocConfiguration.ESCIDOC_CORE_AA_PERSISTENT_ID_ATTRIBUTE_NAME);
            final String origin;

            // get origin
            if (StringUtils.isNotEmpty(details.getShibIdentityProvider())) {
                origin = details.getShibIdentityProvider();
            }
            else {
                origin = shibSessionId;
            }

            // get name
            final String name;
            if (StringUtils.isNotEmpty(cnAttribute)
                && StringUtils.isNotEmpty(request.getHeader(cnAttribute))) {
                name = request.getHeader(cnAttribute);
            }
            else {
                name = shibSessionId;
            }

            // get loginname
            final String loginname;
            if (StringUtils.isNotEmpty(uidAttribute)
                && StringUtils.isNotEmpty(request.getHeader(uidAttribute))) {
                loginname =
                    request.getHeader(uidAttribute).replaceAll("\\s", "");
            }
            else {
                loginname = name.replaceAll("\\s", "_") + '@' + origin;
            }

            user.setLoginName(loginname);
            user.setName(name);

            final Matcher disposableHeaderMatcher =
                ShibbolethUser.DISPOSABLE_HEADER_PATTERN.matcher("");
            final Enumeration<String> enu = request.getHeaderNames();
            while (enu.hasMoreElements()) {
                final String headerName = enu.nextElement();
                disposableHeaderMatcher.reset(headerName);
                if (!disposableHeaderMatcher.matches()
                    && StringUtils.isNotEmpty(request.getHeader(headerName))) {
                    final Enumeration<String> en = request.getHeaders(headerName);
                    while (en.hasMoreElements()) {
                        final String header = en.nextElement();
                        final String[] parts = header.split(";");
                        if (parts != null) {
                            for (final String part : parts) {
                                user.addStringAttribute(headerName, part);
                            }
                        }
                    }
                }
            }

            final ShibbolethToken authentication =
                new ShibbolethToken(user, null);
            authentication.setDetails(details);
            if (user.getLoginName() != null) {
                authentication.setAuthenticated(true);
            }

            SecurityContextHolder
                .getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * See Interface for functional description.
     * 
     * @return
     * @see org.springframework.security.ui.SpringSecurityFilter#getOrder()
     * @aa
     */
    @Override
    public int getOrder() {

        return 0;
    }

    // CHECKSTYLE:JAVADOC-ON

}
