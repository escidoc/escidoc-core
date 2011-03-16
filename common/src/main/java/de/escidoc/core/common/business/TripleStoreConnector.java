/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.common.business;

import de.escidoc.core.common.exceptions.application.invalid.InvalidTripleStoreOutputFormatException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidTripleStoreQueryException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import de.escidoc.core.common.util.service.ConnectionUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.apache.http.HttpEntity;
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
 * @author ROF
 * 
 *         TODO move to TriplestoreUtility implementation
 */
public class TripleStoreConnector {

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

    private static final Logger LOGGER = LoggerFactory.getLogger(
        TripleStoreConnector.class);

    private ConnectionUtility connectionUtility;

    private String fedoraUrl;

    private String fedoraUser;

    private String fedoraPassword;

    /**
     * 
     * @param spoQuery
     * @return
     * @throws TripleStoreSystemException
     * 
     *             TODO move to TriplestoreUtility implementation
     */
    public String requestMPT(final String spoQuery, final String outputFormat)
        throws TripleStoreSystemException,
        InvalidTripleStoreOutputFormatException,
        InvalidTripleStoreQueryException {
        try {
            final List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("format", outputFormat));
            params.add(new BasicNameValuePair("query", spoQuery));
            params.add(new BasicNameValuePair("type", TYPE_MPT));
            params.add(new BasicNameValuePair("lang", LANG_MPT));

            // The flush parameter tells the resource index to ensure
            // that any recently-added/modified/deleted triples are
            // flushed to the triplestore before executing the query.
            params.add(new BasicNameValuePair("flush", FLUSH));

            final String url = fedoraUrl + "/risearch";
            final HttpPost httpPost = new HttpPost(url);
            final HttpEntity entity =
                new UrlEncodedFormEntity(params, XmlUtility.CHARACTER_ENCODING);

            httpPost.setEntity(entity);
            connectionUtility.setAuthentication(new URL(url), fedoraUser,
                fedoraPassword);

            final HttpResponse httpResponse =
                connectionUtility.getHttpClient(url).execute(httpPost);
            String responseContent =
                connectionUtility.readResponse(httpResponse).trim();

            if (responseContent == null || responseContent.length() == 0) {
                return null;
            }
            if (responseContent.startsWith("<html")) {
                final Pattern p = Pattern.compile(QUERY_ERROR);
                final Matcher m = p.matcher(responseContent);

                final Pattern p1 = Pattern.compile(PARSE_ERROR);
                final Matcher m1 = p1.matcher(responseContent);

                final Pattern p2 = Pattern.compile(FORMAT_ERROR);
                final Matcher m2 = p2.matcher(responseContent);
                if (m.find()) {
                    LOGGER.error(responseContent);
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
                    responseContent =
                        XmlUtility.CDATA_START + responseContent
                            + XmlUtility.CDATA_END;
                    throw new TripleStoreSystemException(
                        "Request to MPT failed." + responseContent);
                }
            }
            return responseContent;
        }
        catch (final IOException e) {
            throw new TripleStoreSystemException(e.toString(), e);
        }
        catch (final WebserverSystemException e) {
            throw new TripleStoreSystemException(e.toString(), e);
        }
    }

    /**
     * Set the connection utility.
     * 
     * @param connectionUtility
     *            ConnectionUtility.
     */
    public void setConnectionUtility(final ConnectionUtility connectionUtility) {
        this.connectionUtility = connectionUtility;
    }

    /**
     * @param fedoraUrl
     *            the fedoraUrl to inject
     */
    public void setFedoraUrl(final String fedoraUrl) {
        this.fedoraUrl = fedoraUrl;
    }

    /**
     * @param fedoraUser
     *            the fedoraUser to inject
     */
    public void setFedoraUser(final String fedoraUser) {
        this.fedoraUser = fedoraUser;
    }

    /**
     * @param fedoraPassword
     *            the fedoraPassword to inject
     */
    public void setFedoraPassword(final String fedoraPassword) {
        this.fedoraPassword = fedoraPassword;
    }
}
