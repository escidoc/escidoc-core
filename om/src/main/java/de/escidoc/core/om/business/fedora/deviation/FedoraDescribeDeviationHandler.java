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
package de.escidoc.core.om.business.fedora.deviation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.apache.commons.httpclient.methods.GetMethod;

import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.service.ConnectionUtility;
import de.escidoc.core.om.business.interfaces.FedoraDescribeDeviationHandlerInterface;

/*******************************************************************************
 * @author MIH
 * 
 * @spring.bean id = "business.FedoraDescribeDeviationHandler"
 * @om
 */
public class FedoraDescribeDeviationHandler
    implements FedoraDescribeDeviationHandlerInterface {

    private ConnectionUtility connectionUtility;

    // private static AppLogger log =
    // new AppLogger(FedoraDescribeDeviationHandler.class.getName());

    private String baseURL;

    private String user;

    private String pass;

    /**
     * @see de.escidoc.core.om.business.interfaces
     *      .FedoraDescribeDeviationHandlerInterface#getDatastreamDissemination(Map)
     * @param parameters
     *            http request parameters.
     * 
     * @return InputStream inputStream
     * @throws Exception
     *             ex
     * 
     */
    public InputStream getFedoraDescription(
        final Map<String, String[]> parameters) throws Exception {

        String urlParams = buildUrlParameters(parameters);
        baseURL =
            EscidocConfiguration.getInstance().get(
                EscidocConfiguration.FEDORA_URL);
        user =
            EscidocConfiguration.getInstance().get(
                EscidocConfiguration.FEDORA_USER);
        pass =
            EscidocConfiguration.getInstance().get(
                EscidocConfiguration.FEDORA_PASSWORD);
        if (!baseURL.endsWith("/")) {
            baseURL += "/";
        }

        String describeUrl = null;
        GetMethod getMethod = null;
        InputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            describeUrl = baseURL + "describe" + urlParams.toString();
            getMethod =
                connectionUtility.getRequestURL(new URL(describeUrl), user,
                    pass);
            out = new ByteArrayOutputStream();
            in = getMethod.getResponseBodyAsStream();
            int byteval;
            while ((byteval = in.read()) > -1) {
                out.write(byteval);
            }
            in = new ByteArrayInputStream(out.toByteArray());

            return in;
        }
        finally {
            if (describeUrl != null) {
                try {
                    connectionUtility.resetAuthentication(new URL(describeUrl));
                }
                catch (Exception e) {
                }
            }
            if (getMethod != null) {
                try {
                    getMethod.releaseConnection();
                }
                catch (Exception e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {}
            }
        }
    }

    /**
     * make http-requestparameter string out of given Map.
     * 
     * @param parameters
     *            http request parameters.
     * 
     * @return String http requestparameters as String
     * 
     * @om
     */
    private String buildUrlParameters(final Map<String, String[]> parameters) {
        StringBuffer urlParams = new StringBuffer("");
        if (parameters != null && !parameters.isEmpty()) {
            urlParams.append("?");
            for (String key : parameters.keySet()) {
                if (urlParams.length() > 1) {
                    urlParams.append("&");
                }
                String[] values = parameters.get(key);
                if (values != null && values.length > 0) {
                    urlParams.append(key).append("=").append(values[0]);
                }
            }
        }
        return urlParams.toString();

    }

    /**
     * See Interface for functional description.
     * 
     * @param connectionUtility
     *            The HTTP connection utility.
     * 
     * @spring.property ref="escidoc.core.common.util.service.ConnectionUtility"
     */
    public void setConnectionUtility(final ConnectionUtility connectionUtility) {

        this.connectionUtility = connectionUtility;
    }

}
