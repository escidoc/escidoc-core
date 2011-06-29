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

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.cxf.jaxrs.client.ServerWebApplicationException;
import org.escidoc.core.services.fedora.FedoraServiceClient;
import org.escidoc.core.services.fedora.RisearchPathParam;
import org.escidoc.core.services.fedora.RisearchQueryParam;
import org.esidoc.core.utils.io.Encodings;
import org.esidoc.core.utils.io.IOUtils;
import org.esidoc.core.utils.io.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.application.invalid.InvalidTripleStoreOutputFormatException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidTripleStoreQueryException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * An utility class for MPT request.
 *
 * @author Rozita Friedman
 *         <p/>
 *         TODO move to TriplestoreUtility implementation
 */
@Service("business.TripleStoreConnector")
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

    private static final String QUERY_ERROR = "<title>.*Error</title>";

    private static final String PARSE_ERROR = "Parse error:";

    private static final String FORMAT_ERROR = "Unrecognized format:";

    private static final Logger LOGGER = LoggerFactory.getLogger(TripleStoreConnector.class);

    @Autowired
    private FedoraServiceClient fedoraServiceClient;

    /**
     * @param spoQuery
     * @param outputFormat
     * @throws TripleStoreSystemException TODO move to TriplestoreUtility implementation
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidTripleStoreOutputFormatException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidTripleStoreQueryException
     * @return
     */
    public String requestMPT(final String spoQuery, final String outputFormat) throws TripleStoreSystemException,
        InvalidTripleStoreOutputFormatException, InvalidTripleStoreQueryException {
        final RisearchPathParam path = new RisearchPathParam();
        final RisearchQueryParam query = new RisearchQueryParam();
        query.setFormat(outputFormat);
        query.setType(TYPE_MPT);
        query.setLang(LANG_MPT);
        query.setFlush(FLUSH);
        try {
            query.setQuery(URLEncoder.encode(spoQuery, Encodings.ISO_8859_1).replace("+", "%20"));
            final Stream stream = this.fedoraServiceClient.risearch(path, query);
            final String responseContent = new String(stream.getBytes(), Encodings.UTF8);
            if (responseContent == null || responseContent.length() == 0) {
                return null;
            }
            return responseContent;
        }
        catch (ServerWebApplicationException e) {
            final InputStream inputStream = (InputStream) e.getResponse().getEntity();
            String responseContent;
            try {
                responseContent = IOUtils.newStringFromStream(inputStream);
            }
            catch (IOException e1) {
                throw new TripleStoreSystemException("Request to MPT failed.", e);
            }
            final Pattern p = Pattern.compile(QUERY_ERROR);
            final Matcher m = p.matcher(responseContent);
            if (m.find()) {
                LOGGER.error(responseContent);
                final Pattern p1 = Pattern.compile(PARSE_ERROR);
                final Matcher m1 = p1.matcher(responseContent);
                final Pattern p2 = Pattern.compile(FORMAT_ERROR);
                final Matcher m2 = p2.matcher(responseContent);
                responseContent = XmlUtility.CDATA_START + responseContent + XmlUtility.CDATA_END;
                if (m1.find()) {
                    throw new InvalidTripleStoreQueryException(responseContent);
                }
                else if (m2.find()) {
                    throw new InvalidTripleStoreOutputFormatException(responseContent);
                }
            }
            responseContent = XmlUtility.CDATA_START + responseContent + XmlUtility.CDATA_END;
            throw new TripleStoreSystemException("Request to MPT failed." + responseContent);
        }
        catch (final Exception e) {
            throw new TripleStoreSystemException("Request to MPT failed.", e);
        }
    }
}
