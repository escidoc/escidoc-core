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
package de.escidoc.core.aa.springsecurity;

import de.escidoc.core.common.util.logger.AppLogger;
import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilterEntryPoint;
import org.springframework.security.util.RedirectUrlBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Overwrites AuthenticationProcessingFilterEntryPoint
 * to enable to use absolute urls for the login-form.
 * 
 * @author MIH
 * @aa
 */
public class EscidocAuthenticationProcessingFilterEntryPoint extends
        AuthenticationProcessingFilterEntryPoint {
    private static final AppLogger logger = new AppLogger(
            AuthenticationProcessingFilterEntryPoint.class.getName());

    /**
     * Get url to login-page from configuration.
     * 
     * @param request servlet-request
     * @param response servlet-response
     * @param authException exception
     * @return String url to login-form
     * @aa
     */
    @Override
    protected final String buildRedirectUrlToLoginPage(final HttpServletRequest request,
                                                       final HttpServletResponse response,
                                                       final AuthenticationException authException) {
        final String loginForm =
            determineUrlToUseForThisRequest(request, response, authException);
        if (loginForm.startsWith("http://") || loginForm.startsWith("https://")) {
            return loginForm;
        }
        final int serverPort = super.getPortResolver().getServerPort(request);
        final String scheme = request.getScheme();
        final RedirectUrlBuilder urlBuilder = new RedirectUrlBuilder();
        urlBuilder.setScheme(scheme);
        urlBuilder.setServerName(request.getServerName());
        urlBuilder.setPort(serverPort);
        urlBuilder.setContextPath(request.getContextPath());
        urlBuilder.setPathInfo(loginForm);
        if (super.isForceHttps() && "http".equals(scheme)) {
            final Integer httpsPort = super.getPortMapper()
                    .lookupHttpsPort(serverPort);
            if (httpsPort != null) {
                urlBuilder.setScheme("https");
                urlBuilder.setPort(httpsPort);
            } else {
                logger.warn(
                        "Unable to redirect to HTTPS as "
                        + "no port mapping found for HTTP port " 
                        + serverPort);
            }
        }
        return urlBuilder.getUrl();
    }
}
