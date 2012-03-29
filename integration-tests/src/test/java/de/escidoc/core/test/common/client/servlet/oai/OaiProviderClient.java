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
package de.escidoc.core.test.common.client.servlet.oai;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;

import de.escidoc.core.test.EscidocTestBase;
import de.escidoc.core.test.common.client.servlet.ClientBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.HttpHelper;

/**
 * Offers access methods to the REST interface of the escidoc-oaiprovider.
 *
 * @author Michael Hoppe
 */
public class OaiProviderClient extends ClientBase {

    /**
     * Retrieve the Containers of a Container.
     *
     * @param parameters The get-parameters.
     * @return The response as String.
     * @throws Exception If the service call fails.
     */
    public String retrieve(final String parameters) throws Exception {

        Map<String, String[]> parametersMap = new HashMap<String, String[]>();
        if (parameters != null && !parameters.isEmpty()) {
            String[] parametersParts = parameters.split("&");
            for (int i = 0; i < parametersParts.length; i++) {
                if (parametersParts[i] != null && !parametersParts[i].isEmpty()) {
                    String[] parameterParts = parametersParts[i].split("=");
                    if (parameterParts != null && parameterParts.length == 2) {
                        if (!parametersMap.containsKey(parameterParts[0])) {
                            parametersMap.put(parameterParts[0], new String[0]);
                        }
                        String[] values = parametersMap.get(parameterParts[0]);
                        String[] newValues = new String[values.length + 1];
                        int j = 0;
                        for (; j < values.length; j++) {
                            newValues[j] = values[j];
                        }
                        newValues[j] = parameterParts[1];
                        parametersMap.put(parameterParts[0], newValues);
                    }
                }
            }
        }

        String url =
            HttpHelper.createUrl(Constants.PROTOCOL, EscidocTestBase.getFrameworkHost() + ":"
                + EscidocTestBase.getFrameworkPort(), Constants.OAIPMH_BASE_URI, new String[0], null, false);
        HttpResponse response =
            HttpHelper.executeHttpRequest(getHttpClient(), Constants.HTTP_METHOD_GET, url, null, null, parametersMap);
        if (((HttpResponse) response).getStatusLine().getStatusCode() >= HttpServletResponse.SC_MULTIPLE_CHOICES) {
            throwCorrespondingException((HttpResponse) response);
        }
        return handleXmlResult(response);

    }

}
