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
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.methods.GetMethod;

import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.ConnectionUtility;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * The Servlet redirects to different servlet.
 * 
 * @author MIH
 * @common
 */
public class RedirectServlet extends HttpServlet {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = -631763669808439871L;
    
    /** The logger. */
    private static AppLogger log =
        new AppLogger(RedirectServlet.class.getName());
    
    /** The ConnectionUtility. */
    private ConnectionUtility connectionUtility = new ConnectionUtility();

    /**
     * The central service method.
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
    public void service(
        final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException, IOException {
        ServletOutputStream out = null;
        StringBuffer url = new StringBuffer("");
        String destination = getInitParameter("destination");
        Map parameters = request.getParameterMap();
        String host =
            EscidocConfiguration.getInstance().get(
                EscidocConfiguration.ESCIDOC_CORE_SELFURL);
        url.append(host).append(destination);
        if (parameters != null && !parameters.isEmpty()) {
            int i = 0;
            for (Iterator iter = parameters.keySet().iterator(); iter.hasNext();) {
                String key = (String) iter.next();
                String[] values = (String[]) parameters.get(key);
                for (int j = 0; j < values.length; j++) {
                    if (i == 0) {
                        url.append("?");
                    }
                    else {
                        url.append("&");
                    }
                    url.append(key).append("=").append(values[j]);
                    i++;
                }
            }
        }

        //Get userId and password from Authorization Header
        String userId = null;
        String password = null;
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            StringTokenizer st = new StringTokenizer(authHeader);
            if (st.hasMoreTokens()) {
               String basic = st.nextToken();

               // We only handle HTTP Basic authentication
               if (basic.equalsIgnoreCase("Basic")) {
                  String credentials = st.nextToken();
                  String userPass =
                      new String(Base64.decodeBase64(credentials.getBytes()));
                  int p = userPass.indexOf(":");
                  if (p != -1) {
                     userId = userPass.substring(0, p);
                     password = userPass.substring(p + 1);
                  }
               }
            }
         }

        GetMethod getMethod = null;
        try {
            String result =
                connectionUtility.getRequestURLAsString(
                        new URL(url.toString()), userId, password);
            out = response.getOutputStream();
            out.write(result.getBytes(XmlUtility.CHARACTER_ENCODING));
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
            if (getMethod != null) {
                try {
                    getMethod.releaseConnection();
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        
    }

}
