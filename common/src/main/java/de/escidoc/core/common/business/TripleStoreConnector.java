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

import de.escidoc.core.common.exceptions.application.invalid.InvalidTripleStoreOutputFormatException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidTripleStoreQueryException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.ConnectionUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An utility class for Kowary request.
 * 
 * @spring.bean id="business.TripleStoreConnector"
 * @author ROF
 * 
 *         TODO move to TriplestoreUtility implementation
 */
public class TripleStoreConnector {

    static final String TYPE = "tuples";

    static final String LANG = "iTQL";

    private static final String LANG_MPT = "spo";

    static final String FORMAT_CSV = "CSV";

    static final String FORMAT_MPT = "N-Triples";

    private static final String TYPE_MPT = "triples";

    static final String FORMAT_SIMPLE = "Simple";

    static final String FORMAT_SPARQL = "Sparql";

    static final String FORMAT_TSV = "TSV";

    private static final String FLUSH = "true";

    public static final String QUERY_ERROR = "<title>.*Error</title>";

    public static final String PARSE_ERROR = "Parse error:";

    public static final String FORMAT_ERROR = "Unrecognized format:";

    private static final AppLogger log = new AppLogger(
        TripleStoreConnector.class.getName());

    private ConnectionUtility connectionUtility = null;

    private String fedoraUrl = null;

    private String fedoraUser = null;

    private String fedoraPassword = null;

    /**
     * 
     * @param spoQuery
     * @return
     * @throws TripleStoreSystemException
     * 
     *             TODO move to TriplestoreUtility implementation
     */
    public final String requestMPT(final String spoQuery, final String outputFormat)
        throws TripleStoreSystemException,
        InvalidTripleStoreOutputFormatException,
        InvalidTripleStoreQueryException {
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("format", outputFormat));
            params.add(new BasicNameValuePair("query", spoQuery));
            params.add(new BasicNameValuePair("type", TYPE_MPT));
            params.add(new BasicNameValuePair("lang", LANG_MPT));

            // The flush parameter tells the resource index to ensure
            // that any recently-added/modified/deleted triples are
            // flushed to the triplestore before executing the query.
            params.add(new BasicNameValuePair("flush", FLUSH));

            String url = fedoraUrl + "/risearch";
            HttpPost httpPost = new HttpPost(url);
            UrlEncodedFormEntity entity =
                new UrlEncodedFormEntity(params, XmlUtility.CHARACTER_ENCODING);

            httpPost.setEntity(entity);
            connectionUtility.setAuthentication(new URL(url), fedoraUser,
                fedoraPassword);

            HttpResponse httpResponse =
                connectionUtility.getHttpClient(url).execute(httpPost);
            String responseContent =
                connectionUtility.readResponse(httpResponse).trim();

            // result code from risearch seems to be unreliable
            // if (resultCode != HttpServletResponse.SC_OK) {
            // log.error("Bad request. Http response : " + resultCode);
            // throw new TripleStoreSystemException(
            // "Bad request. Http response : " + resultCode);
            // }

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
        catch (IOException e) {
            log.error("Error requesting MPT", e);
            throw new TripleStoreSystemException(e.toString(), e);
        }
        catch (WebserverSystemException e) {
            log.error("Error requesting MPT", e);
            throw new TripleStoreSystemException(e.toString(), e);
        }
    }

    /**
     * Set the connection utility.
     * 
     * @param connectionUtility
     *            ConnectionUtility.
     * 
     * @spring.property ref="escidoc.core.common.util.service.ConnectionUtility"
     */
    public void setConnectionUtility(final ConnectionUtility connectionUtility) {
        this.connectionUtility = connectionUtility;
    }

    /**
     * @param fedoraUrl
     *            the fedoraUrl to inject
     * @spring.property value="${fedora.url}"
     */
    public void setFedoraUrl(final String fedoraUrl) {
        this.fedoraUrl = fedoraUrl;
    }

    /**
     * @param fedoraUser
     *            the fedoraUser to inject
     * @spring.property value="${fedora.user}"
     */
    public void setFedoraUser(final String fedoraUser) {
        this.fedoraUser = fedoraUser;
    }

    /**
     * @param fedoraPassword
     *            the fedoraPassword to inject
     * @spring.property value="${fedora.password}"
     */
    public void setFedoraPassword(final String fedoraPassword) {
        this.fedoraPassword = fedoraPassword;
    }
}
