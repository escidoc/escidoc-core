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
package de.escidoc.core.test.common.fedora;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;

import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.logger.AppLogger;
import de.escidoc.core.test.common.resources.PropertiesProvider;

/**
 * @author SWA
 * 
 */
public class TripleStoreTestsBase {

    protected static AppLogger log =
        new AppLogger(TripleStoreTestsBase.class.getName());

    private static HttpClient CLIENT = null;

    static final String TYPE_TUPLES = "tuples";

    static final String LANG_ITQL = "iTQL";

    static final String LANG_MPT = "spo";

    public static final String FORMAT_CSV = "CSV";

    public static final String FORMAT_MPT = "N-Triples";

    static final String TYPE_MPT = "triples";

    public static final String FORMAT_SIMPLE = "Simple";

    public static final String FORMAT_SPARQL = "Sparql";

    public static final String FORMAT_TSV = "TSV";

    static final String FLUSH = "true";

    static final String QUERY_ERROR = "<title>Query Error</title>";

    static final String PARSE_ERROR = "Parse error:";

    static final String FORMAT_ERROR = "Unrecognized format:";

    private static String fedoraUrl;

    /**
     * The constructor.
     * @throws Exception 
     * 
     */
    public TripleStoreTestsBase() throws Exception {

        PropertiesProvider propProv = new PropertiesProvider();
        
        fedoraUrl =
            propProv
                .getProperty("fedora.url", "http://localhost:8082/fedora");
        if (CLIENT == null) {
            CLIENT = new HttpClient();
            URL url = new URL(fedoraUrl);
            AuthScope m_authScope =
                new AuthScope(url.getHost(), AuthScope.ANY_PORT,
                    AuthScope.ANY_REALM);
            UsernamePasswordCredentials m_creds =
                new UsernamePasswordCredentials("fedoraAdmin", "fedoraAdmin");
            CLIENT.getState().setCredentials(m_authScope, m_creds);
            // don't wait for auth request
            CLIENT.getParams().setAuthenticationPreemptive(true);
        }
    }

    /**
     * Request SPO query to MPT triple store.
     * 
     * @param spoQuery
     *            The SPO query.
     * @param outputFormat
     *            The triple store output format (N-Triples/RDF/XML/Turtle/..)
     * @return query result
     * @throws Exception
     *             If anything fails.
     */
    public String requestMPT(final String spoQuery, final String outputFormat)
        throws Exception {

        synchronized (CLIENT) {
            PostMethod post = new PostMethod(fedoraUrl + "/risearch");
            post.addParameter("format", outputFormat);
            post.addParameter("query", spoQuery);
            post.addParameter("type", TYPE_MPT);
            post.addParameter("lang", LANG_MPT);
            // The flush parameter tells the resource index to ensure
            // that any recently-added/modified/deleted triples are
            // flushed to the triplestore before executing the query.
            post.addParameter("flush", FLUSH);
            int resultCode = 0;
            try {
                resultCode = CLIENT.executeMethod(post);
                if (resultCode != HttpServletResponse.SC_OK) {
                    throw new Exception("Bad request. Http response : "
                        + resultCode);
                }

                String result = post.getResponseBodyAsString();
                if (result == null) {
                    return null;
                }
                if (result.startsWith("<html")) {
                    Pattern p = Pattern.compile(QUERY_ERROR);
                    Matcher m = p.matcher(result);

                    Pattern p1 = Pattern.compile(PARSE_ERROR);
                    Matcher m1 = p1.matcher(result);

                    Pattern p2 = Pattern.compile(FORMAT_ERROR);
                    Matcher m2 = p2.matcher(result);
                    if (m.find()) {
                        result =
                            Constants.CDATA_START + result
                                + Constants.CDATA_END;
                        if (m1.find()) {
                            throw new Exception(result);
                        }
                        else if (m2.find()) {
                            throw new Exception(result);
                        }
                    }
                    else {
                        result =
                            Constants.CDATA_START + result
                                + Constants.CDATA_END;
                        throw new Exception("Request to MPT failed." + result);
                    }
                }

                return result;
            }
            catch (Exception e) {
                throw new Exception(e.toString(), e);
            }
            finally {
                if (post != null) {
                    post.releaseConnection();
                }
            }
        }

    }


}
