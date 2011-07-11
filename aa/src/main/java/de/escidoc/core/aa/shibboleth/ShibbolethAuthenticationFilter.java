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

import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Matcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import de.escidoc.core.common.util.configuration.EscidocConfiguration;

public class ShibbolethAuthenticationFilter extends GenericFilterBean {

    /**
     * See Interface for functional description.
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
        throws IOException, ServletException {

        final String shibSessionId = ((HttpServletRequest) request).getHeader(ShibbolethDetails.SHIB_SESSION_ID);
        if (shibSessionId != null && shibSessionId.length() != 0) {
            final ShibbolethDetails details =
                new ShibbolethDetails(null, ((HttpServletRequest) request)
                    .getHeader(ShibbolethDetails.SHIB_ASSERTION_COUNT), ((HttpServletRequest) request)
                    .getHeader(ShibbolethDetails.SHIB_AUTHENTICATION_METHOD), ((HttpServletRequest) request)
                    .getHeader(ShibbolethDetails.SHIB_AUTHENTICATION_INSTANT), ((HttpServletRequest) request)
                    .getHeader(ShibbolethDetails.SHIB_AUTHNCONTEXT_CLASS), ((HttpServletRequest) request)
                    .getHeader(ShibbolethDetails.SHIB_AUTHNCONTEXT_DECL), ((HttpServletRequest) request)
                    .getHeader(ShibbolethDetails.SHIB_IDENTITY_PROVIDER), shibSessionId);

            final ShibbolethUser user = new ShibbolethUser();
            final String cnAttribute =
                EscidocConfiguration.getInstance().get(EscidocConfiguration.ESCIDOC_CORE_AA_COMMON_NAME_ATTRIBUTE_NAME);
            final String uidAttribute =
                EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.ESCIDOC_CORE_AA_PERSISTENT_ID_ATTRIBUTE_NAME);

            // get origin
            final String origin =
                StringUtils.isNotEmpty(details.getShibIdentityProvider()) ? details.getShibIdentityProvider() : shibSessionId;

            // get name
            final String name =
                StringUtils.isNotEmpty(cnAttribute)
                    && StringUtils.isNotEmpty(((HttpServletRequest) request).getHeader(cnAttribute)) ? ((HttpServletRequest) request)
                    .getHeader(cnAttribute) : shibSessionId;

            // get loginname
            final String loginname =
                StringUtils.isNotEmpty(uidAttribute)
                    && StringUtils.isNotEmpty(((HttpServletRequest) request).getHeader(uidAttribute)) ? ((HttpServletRequest) request)
                    .getHeader(uidAttribute).replaceAll("\\s", "") : name.replaceAll("\\s", "_") + '@' + origin;

            user.setLoginName(loginname);
            user.setName(name);

            final Matcher disposableHeaderMatcher = ShibbolethUser.DISPOSABLE_HEADER_PATTERN.matcher("");
            final Enumeration<String> enu = ((HttpServletRequest) request).getHeaderNames();
            while (enu.hasMoreElements()) {
                final String headerName = enu.nextElement();
                disposableHeaderMatcher.reset(headerName);
                if (!disposableHeaderMatcher.matches()
                    && StringUtils.isNotEmpty(((HttpServletRequest) request).getHeader(headerName))) {
                    final Enumeration<String> en = ((HttpServletRequest) request).getHeaders(headerName);
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

            final ShibbolethToken authentication = new ShibbolethToken(user, null);
            authentication.setDetails(details);
            if (user.getLoginName() != null) {
                authentication.setAuthenticated(true);
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

}
