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
package de.escidoc.core.common.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import de.escidoc.core.common.util.service.BeanLocator;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.om.service.interfaces.FedoraDescribeDeviationHandlerInterface;

/**
 * The Servlet tunnels the call of Fedoras DescribeRepositoryServlet through the
 * Framework, adding Framework-Security.
 * 
 * @author MIH
 * @common
 */
public class FedoraDescribeDeviationServlet extends HttpServlet {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = 8089059317780016328L;

    private FedoraDescribeDeviationHandlerInterface fedoraDescribe = null;

    /**
     * Initialize FedoraDescribeDeviationHandler.
     * 
     * @param config
     *            ServletConfig
     * @throws ServletException
     *             e
     */
    @Override
    public void init(final ServletConfig config) throws ServletException {
    }

    /**
     * The central service method. Gets Authorization-Haeder and puts it in user
     * context. Calls OMs FedoraDescribeDeviationHandler.
     * 
     * @param request
     *            The servlet request.
     * @param response
     *            The servlet response
     * @throws ServletException
     *             If anything fails.
     * @throws IOException
     *             If anything fails.
     * @common
     */
    @Override
    public void service(
        final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException, IOException {
        // Get Username and Password////////////////////////////////////////
        String pwd = UserContext.ANONYMOUS_IDENTIFIER;
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && !authHeader.equals("")
            && authHeader.indexOf(":") > -1) {
            authHeader = authHeader.substring(authHeader.indexOf(" "));
            String decoded =
                new String(Base64.decodeBase64(authHeader.getBytes()));
            int i = decoded.indexOf(":");
            pwd = decoded.substring(i + 1, decoded.length());
        }
        // /////////////////////////////////////////////////////////////////

        Map<String, String[]> parameters = request.getParameterMap();

        ServletOutputStream out = null;
        InputStream in = null;
        try {
            UserContext.setUserContext(pwd);
            fedoraDescribe = BeanLocator.locateFedoraDescribeDeviationHandler();
            in = fedoraDescribe.getFedoraDescription(parameters);
            out = response.getOutputStream();
            int byteval;
            while ((byteval = in.read()) > -1) {
                out.write(byteval);
            }
        }
        catch (Exception e) {
            throw new ServletException(e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (Exception e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                }
                catch (Exception e) {
                }
            }
        }
    }

}
