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
 * Copyright 2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.test.examples;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.ProxyHost;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;

import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.oum.OrganizationalUnitClient;
import de.escidoc.core.test.common.resources.PropertiesProvider;

/**
 * @author Joerg Hermann
 */
public class RetrieveCompressedExamplesIT extends EscidocAbstractTest {

    private final String[] EXAMPLE_OU_IDS = { "escidoc:ex3" };

    private final OrganizationalUnitClient ouClient = new OrganizationalUnitClient();

    @Test
    public void testRetrieveCompressedExampleOrganizationalUnits() throws Exception {

        for (int i = 0; i < EXAMPLE_OU_IDS.length; ++i) {

            String ou = handleXmlResult(ouClient.retrieve(EXAMPLE_OU_IDS[i]));

            String response = null;

            String url = "http://localhost:8080" + Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/" + EXAMPLE_OU_IDS[i];

            HttpClient client = new HttpClient();

            try {
                if (PropertiesProvider.getInstance().getProperty("http.proxyHost") != null
                    && PropertiesProvider.getInstance().getProperty("http.proxyPort") != null) {
                    ProxyHost proxyHost =
                        new ProxyHost(PropertiesProvider.getInstance().getProperty("http.proxyHost"), Integer
                            .parseInt(PropertiesProvider.getInstance().getProperty("http.proxyPort")));

                    client.getHostConfiguration().setProxyHost(proxyHost);
                }
            }
            catch (final Exception e) {
                throw new RuntimeException("[ClientBase] Error occured loading properties! " + e.getMessage(), e);
            }

            GetMethod getMethod = new GetMethod(url);
            getMethod.setRequestHeader("Accept-Encoding", "gzip");

            InputStream responseBody = null;
            try {

                int statusCode = client.executeMethod(getMethod);

                if (statusCode != HttpStatus.SC_OK) {
                    throw new RuntimeException("getMethod failed:" + getMethod.getStatusLine());
                }
                responseBody = new GZIPInputStream(getMethod.getResponseBodyAsStream());

                BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(responseBody, getMethod.getResponseCharSet()));
                StringBuffer result = new StringBuffer();

                char[] buffer = new char[4 * 1024];
                int charsRead;
                while ((charsRead = bufferedReader.read(buffer)) != -1) {
                    result.append(buffer, 0, charsRead);
                }

                response = result.toString();
            }
            catch (Exception e) {
                throw new RuntimeException("Error occured in retrieving compressed example! " + e.getMessage(), e);
            }

            assertXmlEquals("Compressed document not the same like the uncompressed one", ou, response);
        }
    }
}
