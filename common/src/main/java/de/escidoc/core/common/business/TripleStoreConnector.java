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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

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
 *     TODO move to TriplestoreUtility implementation
 */
public class TripleStoreConnector {

    // TODO ? Maybe the http client returned by FedoraUtility.getHttpClient can
    // be used.
    private static DefaultHttpClient client = null;

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

    private static AppLogger log = new AppLogger(
        TripleStoreConnector.class.getName());

    private static String fedoraUrl = null;

    private static String fedoraUser = null;

    private static String fedoraPass = null;

    /**
     * Default constructor.
     * 
     */
    public TripleStoreConnector() throws WebserverSystemException {
        try {
            String preemptAuth =
                System.getProperties().getProperty(
                    "httpclient.authentication.preemptive");
            System.out.println("Preemptive authentication is switched "
                + (Boolean.getBoolean(preemptAuth) ? " ON." : " OFF. ("
                    + preemptAuth + ")"));
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
                client = new DefaultHttpClient();
                URL url = new URL(fedoraUrl);
                AuthScope m_authScope =
                    new AuthScope(url.getHost(), AuthScope.ANY_PORT,
                        AuthScope.ANY_REALM);
                UsernamePasswordCredentials m_creds =
                    new UsernamePasswordCredentials(fedoraUser, fedoraPass);
                client.getCredentialsProvider().setCredentials(m_authScope, m_creds);
                // don't wait for auth request
                // TODO FIXME nach TEst auch hier preemptive authentification einbauen 
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

            String preemptAuth =
                System.getProperties().getProperty(
                    "httpclient.authentication.preemptive");
            System.out.println("Preemptive authentication is switched "
                + (Boolean.getBoolean(preemptAuth) ? " ON." : " OFF. ("
                    + preemptAuth + ")"));
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
                client = new DefaultHttpClient();
                URL url = new URL(fedoraUrl);
                AuthScope m_authScope =
                    new AuthScope(url.getHost(), AuthScope.ANY_PORT,
                        AuthScope.ANY_REALM);
                UsernamePasswordCredentials m_creds =
                    new UsernamePasswordCredentials(fedoraUser, fedoraPass);
                client.getCredentialsProvider().setCredentials(m_authScope, m_creds);
                // don't wait for auth request
                // TODO FIXME nach TEst auch hier preemptive authentification einbauen 
                //client.getParams().setAuthenticationPreemptive(true);
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
     *             TODO move to TriplestoreUtility implementation
     */
    public static String requestMPT(
        final String spoQuery, final String outputFormat)
        throws TripleStoreSystemException,
        InvalidTripleStoreOutputFormatException,
        InvalidTripleStoreQueryException {

        synchronized (client) {
            HttpPost post = new HttpPost(fedoraUrl + "/risearch");
            post.addHeader("Content-type",
                "application/x-www-form-urlencoded; charset=utf-8");

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("format", outputFormat));
            params.add(new BasicNameValuePair("query", spoQuery));
            params.add(new BasicNameValuePair("type", TYPE_MPT));
            params.add(new BasicNameValuePair("lang", LANG_MPT));

            // The flush parameter tells the resource index to ensure
            // that any recently-added/modified/deleted triples are
            // flushed to the triplestore before executing the query.
            params.add(new BasicNameValuePair("flush", FLUSH));

            try {

                UrlEncodedFormEntity entity;
                entity = new UrlEncodedFormEntity(params, "UTF-8");

                post.setEntity(entity);

                HttpResponse response = client.execute(post);

                // result code from risearch seems to be unreliable
                // if (resultCode != HttpServletResponse.SC_OK) {
                // log.error("Bad request. Http response : " + resultCode);
                // throw new TripleStoreSystemException(
                // "Bad request. Http response : " + resultCode);
                // }

                String responseContent =
                    convertStreamToString(response.getEntity().getContent());
                if (responseContent == null || responseContent.length() == 0) {
                    return null;
                }
                if (responseContent.startsWith("<html")) {
                    Pattern p = Pattern.compile(QUERY_ERROR);
                    Matcher m = p.matcher(responseContent);

                    Pattern p1 = Pattern.compile(PARSE_ERROR);
                    Matcher m1 = p1.matcher(responseContent);

                    Pattern p2 = Pattern.compile(FORMAT_ERROR);
                    Matcher m2 = p2.matcher(responseContent);
                    if (m.find()) {
                        log.error(responseContent);
                        responseContent =
                            XmlUtility.CDATA_START + responseContent
                                + XmlUtility.CDATA_END;
                        if (m1.find()) {
                            throw new InvalidTripleStoreQueryException(
                                responseContent);
                        }
                        else if (m2.find()) {
                            throw new InvalidTripleStoreOutputFormatException(
                                responseContent);
                        }
                    }
                    else {
                        log.error("Request failed:\n" + responseContent);
                        responseContent =
                            XmlUtility.CDATA_START + responseContent
                                + XmlUtility.CDATA_END;
                        throw new TripleStoreSystemException(
                            "Request to MPT failed." + responseContent);
                    }
                }

                return responseContent;
            }

            catch (UnsupportedEncodingException e) {
                log.error("Error requesting MPT", e);
                throw new TripleStoreSystemException(e.toString(), e);
            }
            catch (IOException e) {
                log.error("Error requesting MPT", e);
                throw new TripleStoreSystemException(e.toString(), e);
            }

        }

    }

    /**
     * Convert InputStream content to String. Stream is closed at EOF.
     * 
     * @param is
     *            InputStream
     * @return String
     */
    public static String convertStreamToString(final InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                is.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
