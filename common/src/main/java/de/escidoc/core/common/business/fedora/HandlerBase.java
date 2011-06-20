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

package de.escidoc.core.common.business.fedora;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.persistence.EscidocIdProvider;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.escidoc.core.services.fedora.FedoraServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Abstract base class for handlers.
 *
 * @author Torsten Tetteroo
 */
public abstract class HandlerBase {

    @Autowired
    private FedoraServiceClient fedoraServiceClient;

    @Autowired
    @Qualifier("business.TripleStoreUtility")
    private TripleStoreUtility tripleStoreUtility;

    @Autowired
    @Qualifier("escidoc.core.business.EscidocIdProvider")
    private EscidocIdProvider idProvider;

    @Autowired
    @Qualifier("business.Utility")
    private Utility utility;

    protected String transformSearchResponse2relations(final String searchResponse) throws SystemException {

        try {
            final TransformerFactory tf = TransformerFactory.newInstance();

            final URL xsltUrl =
                new URL(EscidocConfiguration.getInstance().appendToSelfURL("/xsl/searchResponse2relations.xsl"));
            final HttpURLConnection conn = (HttpURLConnection) xsltUrl.openConnection();
            final Transformer t = tf.newTransformer(new StreamSource(conn.getInputStream()));
            t.setParameter("XSLT", EscidocConfiguration.getInstance().get(EscidocConfiguration.ESCIDOC_CORE_XSLT_STD));

            // searchResponse is already a String; so, no effort to stream
            final StringWriter sw = new StringWriter();
            t.transform(new StreamSource(new ByteArrayInputStream(searchResponse
                .getBytes(XmlUtility.CHARACTER_ENCODING))), new StreamResult(sw));
            return sw.toString();

        }
        catch (final IOException e) {
            throw new SystemException("Convertion of search response to relations failed.", e);
        }
        catch (final TransformerException e) {
            throw new SystemException("Convertion of search response to relations failed.", e);
        }
    }

    protected FedoraServiceClient getFedoraServiceClient() {
        return fedoraServiceClient;
    }

    /**
     * Gets the {@link TripleStoreUtility}.
     *
     * @return TripleStoreUtility Returns the {@link TripleStoreUtility} object.
     */
    protected TripleStoreUtility getTripleStoreUtility() {
        return this.tripleStoreUtility;
    }

    /**
     * Gets the {@link EscidocIdProvider}.
     *
     * @return Returns the {@link EscidocIdProvider} object.
     */
    protected EscidocIdProvider getIdProvider() {
        return this.idProvider;
    }

    /**
     * @return Returns the utility.
     */
    protected Utility getUtility() {
        return this.utility;
    }
}
