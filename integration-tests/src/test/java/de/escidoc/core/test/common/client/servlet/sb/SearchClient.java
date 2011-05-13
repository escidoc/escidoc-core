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
package de.escidoc.core.test.common.client.servlet.sb;

import de.escidoc.core.test.common.client.servlet.ClientBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.HttpHelper;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Offers access methods to the escidoc interface of the Search resource. (SRW-Server)
 * 
 * @author Michael Hoppe
 */
public class SearchClient extends ClientBase {

    /**
     * Retrieve srw search response.
     * 
     * @param parameters
     *            The http-parameters as HashMap.
     * @param database
     *            database where search is executed.
     * @return The HttpMethod after the service call .
     * @throws Exception
     *             If the service call fails.
     */
    public Object search(final HashMap<String, String> parameters, final String database) throws Exception {

        StringBuffer paramString = new StringBuffer("?");
        for (String key : parameters.keySet()) {
            if (paramString.length() > 1) {
                paramString.append("&");
            }
            String value = parameters.get(key);
            value = URLEncoder.encode(value, HttpHelper.HTTP_DEFAULT_CHARSET);
            paramString.append(key).append("=").append(value);
        }
        parameters.put("database", database);
        return callEsciDoc("Sb.search", METHOD_SEARCH, Constants.HTTP_METHOD_GET, Constants.SEARCH_BASE_URI + "/"
            + database + paramString, new String[] {}, parameters);
    }

    /**
     * Retrieve srw explain response.
     * 
     * @param parameters
     *            The http-parameters as HashMap.
     * @param database
     *            database where search is executed.
     * @return The HttpMethod after the service call .
     * @throws Exception
     *             If the service call fails.
     */
    public Object explain(final HashMap<String, String[]> parameters, final String database) throws Exception {

        StringBuffer paramString = new StringBuffer("?");
        for (Iterator iter = parameters.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            paramString.append(key).append("=").append(parameters.get(key));
        }
        parameters.put("database", new String[] { database });
        return callEsciDoc("Sb.explain", METHOD_EXPLAIN, Constants.HTTP_METHOD_GET, Constants.SEARCH_BASE_URI + "/"
            + database + paramString, new String[] {}, parameters);
    }

    /**
     * Retrieve srw scan response.
     * 
     * @param parameters
     *            The http-parameters as HashMap.
     * @param database
     *            database where scan is executed.
     * @return The HttpMethod after the service call .
     * @throws Exception
     *             If the service call fails.
     */
    public Object scan(final HashMap<String, String> parameters, final String database) throws Exception {

        StringBuffer paramString = new StringBuffer("?");
        for (Iterator iter = parameters.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            if (paramString.length() > 1) {
                paramString.append("&");
            }
            String value = parameters.get(key);
            value = URLEncoder.encode(value, HttpHelper.HTTP_DEFAULT_CHARSET);
            paramString.append(key).append("=").append(value);
        }
        parameters.put("database", database);
        return callEsciDoc("Sb.scan", METHOD_SCAN, Constants.HTTP_METHOD_GET, Constants.SEARCH_BASE_URI + "/"
            + database + paramString, new String[] {});
    }

}
