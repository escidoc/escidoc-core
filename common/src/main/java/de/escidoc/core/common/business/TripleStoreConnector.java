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
package de.escidoc.core.common.business;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;

import de.escidoc.core.common.exceptions.application.invalid.InvalidTripleStoreOutputFormatException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidTripleStoreQueryException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * An utility class for Kowary request.
 * 
 * @spring.bean id="business.TripleStoreConnector"
 * @author ROF
 * @om
 * 
 * TODO move to TriplestoreUtility implementation
 */
public class TripleStoreConnector {

    // TODO ? Maybe the http client returned by FedoraUtility.getHttpClient can
    // be used.
    private static HttpClient client = null;

    static final String TYPE = "tuples";

    static final String LANG = "iTQL";

    static final String LANG_MPT = "spo";

    static final String FORMAT_CSV = "CSV";

    static final String FORMAT_MPT = "N-Triples";

    static final String TYPE_MPT = "triples";

    static final String FORMAT_SIMPLE = "Simple";

    static final String FORMAT_SPARQL = "Sparql";

    static final String FORMAT_TSV = "TSV";

    static final String FLUSH = "true";

    public static final String QUERY_ERROR = "<title>.*Error</title>";

    public static final String PARSE_ERROR = "Parse error:";

    public static final String FORMAT_ERROR = "Unrecognized format:";

    private static AppLogger log =
        new AppLogger(TripleStoreConnector.class.getName());

    private static String fedoraUrl = null;

    private static String fedoraUser = null;

    private static String fedoraPass = null;

    /**
     * Default constructor.
     * 
     */
    public TripleStoreConnector() throws WebserverSystemException {
        try {
            String preemptAuth = System.getProperties().getProperty("httpclient.authentication.preemptive");
            System.out.println("Preemptive authentication is switched " + (Boolean.getBoolean(preemptAuth) ? " ON." : " OFF. (" + preemptAuth + ")"));
            if (fedoraUrl == null || fedoraUser == null || fedoraPass == null) {
                fedoraUrl =
                    EscidocConfiguration.getInstance().get(
                        EscidocConfiguration.FEDORA_URL);
                fedoraUser =
                    EscidocConfiguration.getInstance().get(
                        EscidocConfiguration.FEDORA_USER);
                fedoraPass =
                    EscidocConfiguration.getInstance().get(
                        EscidocConfiguration.FEDORA_PASSWORD);
            }
            // TODO ? Maybe the http client returned by
            // FedoraUtility.getHttpClient can be used.
            if (client == null) {
                client = new HttpClient();
                URL url = new URL(fedoraUrl);
                AuthScope m_authScope =
                    new AuthScope(url.getHost(), AuthScope.ANY_PORT,
                        AuthScope.ANY_REALM);
                UsernamePasswordCredentials m_creds =
                    new UsernamePasswordCredentials(fedoraUser, fedoraPass);
                client.getState().setCredentials(m_authScope, m_creds);
                // don't wait for auth request
                client.getParams().setAuthenticationPreemptive(true);
            }
        }
        catch (Exception e) {
            String errorMsg =
                "Failed to retrieve configuration parameter "
                    + EscidocConfiguration.FEDORA_URL;
            log.error(errorMsg, e);
            throw new WebserverSystemException(errorMsg, e);
        }
    }

    public static void init() throws WebserverSystemException {
        try {

            String preemptAuth = System.getProperties().getProperty("httpclient.authentication.preemptive");
            System.out.println("Preemptive authentication is switched " + (Boolean.getBoolean(preemptAuth) ? " ON." : " OFF. (" + preemptAuth + ")"));
            if (fedoraUrl == null || fedoraUser == null || fedoraPass == null) {
                fedoraUrl =
                    EscidocConfiguration.getInstance().get(
                        EscidocConfiguration.FEDORA_URL);
                fedoraUser =
                    EscidocConfiguration.getInstance().get(
                        EscidocConfiguration.FEDORA_USER);
                fedoraPass =
                    EscidocConfiguration.getInstance().get(
                        EscidocConfiguration.FEDORA_PASSWORD);
            }
            // TODO ? Maybe the http client returned by
            // FedoraUtility.getHttpClient can be used.
            if (client == null) {
                client = new HttpClient();
                URL url = new URL(fedoraUrl);
                AuthScope m_authScope =
                    new AuthScope(url.getHost(), AuthScope.ANY_PORT,
                        AuthScope.ANY_REALM);
                UsernamePasswordCredentials m_creds =
                    new UsernamePasswordCredentials(fedoraUser, fedoraPass);
                client.getState().setCredentials(m_authScope, m_creds);
                // don't wait for auth request
                client.getParams().setAuthenticationPreemptive(true);
            }
        }
        catch (Exception e) {
            String errorMsg =
                "Failed to retrieve configuration parameter "
                    + EscidocConfiguration.FEDORA_URL;
            log.error(errorMsg, e);
            throw new WebserverSystemException(errorMsg, e);
        }
    }

    /**
     * 
     * @param spoQuery
     * @return
     * @throws TripleStoreSystemException
     * 
     * TODO move to TriplestoreUtility implementation
     */
    public static String requestMPT(
        final String spoQuery, final String outputFormat)
        throws TripleStoreSystemException,
        InvalidTripleStoreOutputFormatException,
        InvalidTripleStoreQueryException {

        synchronized (client) {
            PostMethod post = new PostMethod(fedoraUrl + "/risearch");
            post.addRequestHeader("Content-type",
                "application/x-www-form-urlencoded; charset=utf-8");
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
                resultCode = client.executeMethod(post);

                // result code from risearch seems to be unreliable
                // if (resultCode != HttpServletResponse.SC_OK) {
                // log.error("Bad request. Http response : " + resultCode);
                // throw new TripleStoreSystemException(
                // "Bad request. Http response : " + resultCode);
                // }

                String result = post.getResponseBodyAsString().trim();
                if (result == null || result.length() == 0) {
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
                        log.error(result);
                        result =
                            XmlUtility.CDATA_START + result
                                + XmlUtility.CDATA_END;
                        if (m1.find()) {
                            throw new InvalidTripleStoreQueryException(result);
                        }
                        else if (m2.find()) {
                            throw new InvalidTripleStoreOutputFormatException(
                                result);
                        }
                    }
                    else {
                        log.error("Request failed:\n" + result);
                        result =
                            XmlUtility.CDATA_START + result
                                + XmlUtility.CDATA_END;
                        throw new TripleStoreSystemException(
                            "Request to MPT failed." + result);
                    }
                }

                return result;
            }
            catch (HttpException e) {
                log.error("Error requesting MPT", e);
                throw new TripleStoreSystemException(e.toString(), e);
            }
            catch (IOException e) {
                log.error("Error requesting MPT", e);
                throw new TripleStoreSystemException(e.toString(), e);
            }
            finally {
                if (post != null) {
                    post.releaseConnection();
                }
            }
        }

    }

}
