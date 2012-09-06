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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.servlet;

import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.transport.servlet.CXFServlet;

/**
 * @author Michael Hoppe
 * 
 */
public class EscidocCxfServlet extends CXFServlet {

    private static final String STATIC_RESOURCES_PARAMETER = "static-resources-list";

    private static final String STATIC_WELCOME_FILE_PARAMETER = "static-welcome-file";

    private static final String REDIRECT_QUERY_CHECK_PARAMETER = "redirect-query-check";

    private List<Pattern> staticResourcesList;

    private String staticWelcomeFile;

    private boolean redirectQueryCheck;

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        staticResourcesList = parseListSequence(servletConfig.getInitParameter(STATIC_RESOURCES_PARAMETER));
        staticWelcomeFile = servletConfig.getInitParameter(STATIC_WELCOME_FILE_PARAMETER);
        redirectQueryCheck = Boolean.valueOf(servletConfig.getInitParameter(REDIRECT_QUERY_CHECK_PARAMETER));

    }

    protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if (staticResourcesList != null && matchPath(staticResourcesList, request)) {
            serveStaticContent(request, response, request.getPathInfo());
            return;
        }
        else if (staticWelcomeFile != null
            && (StringUtils.isEmpty(request.getPathInfo()) || request.getPathInfo().equals("/"))) {
            serveStaticContent(request, response, staticWelcomeFile);
        }
        super.handleRequest(request, response);
    }

    private boolean matchPath(List<Pattern> values, HttpServletRequest request) {
        String path = request.getPathInfo();
        if (path == null) {
            path = "/";
        }
        if (redirectQueryCheck) {
            String queryString = request.getQueryString();
            if (queryString != null && queryString.length() > 0) {
                path += "?" + queryString;
            }
        }
        for (Pattern pattern : values) {
            if (pattern.matcher(path).matches()) {
                return true;
            }
        }
        return false;
    }

}
