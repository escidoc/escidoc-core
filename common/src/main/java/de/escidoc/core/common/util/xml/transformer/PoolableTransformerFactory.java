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

package de.escidoc.core.common.util.xml.transformer;

import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.IOUtils;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.service.ConnectionUtility;
import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.escidoc.core.services.fedora.FedoraServiceClient;
import org.esidoc.core.utils.io.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * {@link BaseKeyedPoolableObjectFactory} implementation creating {@link Transformer} objects using a {@link
 * TransformerFactory}.<br/> The key must be a {@link String}, otherwise makeObject throws an exception. It should be a
 * namespaceUri that can be mapped to a style sheet. If this mapping fails, the specified default style sheet is used.
 *
 * @author Torsten Tetteroo
 */
@Configurable
public class PoolableTransformerFactory extends BaseKeyedPoolableObjectFactory {

    private static final Pattern SPLIT_PATTERN = Pattern.compile(";");

    private static final Logger LOGGER = LoggerFactory.getLogger(PoolableTransformerFactory.class);

    private static final String ERR_MSG_UNSUPPORTED_ARG_TYPE =
        "Only keys of type " + String.class.getName() + " are supported in makeObject.";

    private final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    private static final String NS_BASE_METADATAPROFILE_SCHEMA_ESCIDOC_MPG_DE =
        "http://escidoc.mpg.de/metadataprofile/schema/0.";

    private static final String XSL_MAPPING_MPDL_TO_DC = "/xsl/mapping-mpdl2dc-onlyMD.xsl";

    private static final String XSL_MAPPING_UNKNOWN_TO_DC = "/xsl/mapping-unknown2dc-onlyMD.xsl";

    private static final String CONTENT_MODEL_XSLT_DC_DATASTREAM = "DC-MAPPING";

    private String defaultXsltUrl = null;

    @Autowired
    private FedoraServiceClient fedoraServiceClient;

    @Autowired
    @Qualifier("escidoc.core.common.util.service.ConnectionUtility")
    private ConnectionUtility connectionUtility;

    /**
     * The default constructor.<br/> The default style sheet uri is set to the value of the constant
     * {@code XSL_MAPPING_UNKNOWN_TO_DC}.
     */
    public PoolableTransformerFactory() {
        setDefaultXsltUrl(EscidocConfiguration.getInstance().appendToSelfURL(XSL_MAPPING_UNKNOWN_TO_DC));
    }

    /**
     * Sets the default style sheet url.
     *
     * @param defaultXsltUrl The default style sheet url.
     */
    public final void setDefaultXsltUrl(final String defaultXsltUrl) {

        this.defaultXsltUrl = defaultXsltUrl;
    }

    /**
     * See Interface for functional description.
     *
     * @param key Expecting String with XSLT location or String with XSLT location and contentModelId (separated through
     *            semicolon).
     * @return Transformer
     */
    @Override
    public Object makeObject(final Object key) throws WebserverSystemException, FedoraSystemException {
        if (!(key instanceof String)) {
            throw new UnsupportedOperationException(ERR_MSG_UNSUPPORTED_ARG_TYPE);
        }
        Transformer result = null;
        InputStream xslt = null;
        try {
            xslt = mapKeyToXslt((String) key);
            final StreamSource streamSrc = new StreamSource(xslt);
            result = transformerFactory.newTransformer(streamSrc);
        }
        catch (final IOException e) {
            throw new WebserverSystemException("XSLT for DC-mapping not retrievable.", e);
        }
        catch (final TransformerConfigurationException e) {
            throw new WebserverSystemException("Transformer for DC-mapping can not be created.", e);
        }
        finally {
            IOUtils.closeStream(xslt);
        }
        return result;
    }

    /**
     * Maps the provided key to the related style sheet.
     *
     * @param key The key for that the related style sheet shall be identified.
     * @return Returns the {@link URL} to the addressed style sheet. If no style sheet can be identified for the
     *         provided key, the default one identified by constant {@code XSL_MAPPING_UNKNOWN_TO_DC} is returned.
     * @throws IOException Thrown if retrieving values from eSciDoc properties (configuration) failed.
     */
    private InputStream mapKeyToXslt(final String key) throws IOException {
        final String[] keyParts = SPLIT_PATTERN.split(key);
        final String nsUri = keyParts[0];
        final String contentModelId = keyParts[1];
        InputStream xslt = null;
        // xslt is the mpdl-xslt- or default-xslt-stream
        final String internalXsltUrl =
            nsUri != null && nsUri.startsWith(NS_BASE_METADATAPROFILE_SCHEMA_ESCIDOC_MPG_DE) ? EscidocConfiguration
                .getInstance().appendToSelfURL(XSL_MAPPING_MPDL_TO_DC) : this.defaultXsltUrl;
        String xsltUrl = null;

        try {
            if (contentModelId.length() > 0 && !"null".equalsIgnoreCase(contentModelId)) {
                try {
                    // create link to content of DC-MAPPING in content model object
                    final Stream xmltStream =
                        this.fedoraServiceClient.getBinaryContent(contentModelId, CONTENT_MODEL_XSLT_DC_DATASTREAM,
                            null);
                    xslt = xmltStream.getInputStream();
                }
                catch (final Exception e) {
                    // fall back to internal XSLT
                    xsltUrl = internalXsltUrl;
                    xslt = this.connectionUtility.getRequestURL(new URL(xsltUrl)).getEntity().getContent();
                }
            }
            else {
                xsltUrl = internalXsltUrl;
                xslt = this.connectionUtility.getRequestURL(new URL(xsltUrl)).getEntity().getContent();
            }
        }
        catch (final WebserverSystemException e) {
            // xslt is still the stream set above
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on requesting URL '" + xsltUrl + '\'');
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on requesting URL '" + xsltUrl + '\'', e);
            }
        }
        return xslt;
    }
}
