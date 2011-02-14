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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.util.xml.transformer;

import de.escidoc.core.common.business.fedora.FedoraUtility;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;
import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * {@link BaseKeyedPoolableObjectFactory} implementation creating
 * {@link Transformer} objects using a {@link TransformerFactory}.<br/>
 * The key must be a {@link String}, otherwise makeObject throws an exception.
 * It should be a namespaceUri that can be mapped to a style sheet. If this
 * mapping fails, the specified default style sheet is used.
 * 
 * @author tte
 * @common
 */
public class PoolableTransformerFactory extends BaseKeyedPoolableObjectFactory {

    private static final AppLogger LOG = new AppLogger(
        PoolableTransformerFactory.class.getName());

    private static final String ERR_MSG_UNSUPPORTED_ARG_TYPE =
        "Only keys of type " + String.class.getName()
            + " are supported in makeObject.";

    private final TransformerFactory transformerFactory = TransformerFactory
        .newInstance();

    private static final String NS_BASE_METADATAPROFILE_SCHEMA_ESCIDOC_MPG_DE =
        "http://escidoc.mpg.de/metadataprofile/schema/0.";

    private static final String XSL_MAPPING_MPDL_TO_DC =
        "/xsl/mapping-mpdl2dc-onlyMD.xsl";

    private static final String XSL_MAPPING_UNKNOWN_TO_DC =
        "/xsl/mapping-unknown2dc-onlyMD.xsl";

    private static final String CONTENT_MODEL_XSLT_DC_DATASTREAM = "DC-MAPPING";

    private String defaultXsltUrl = "http://localhost:8080"
        + XSL_MAPPING_UNKNOWN_TO_DC;

    /**
     * The default constructor.<br/>
     * The default style sheet uri is set to the value of the constant
     * <code>XSL_MAPPING_UNKNOWN_TO_DC</code>.
     * 
     * @common
     */
    public PoolableTransformerFactory() {

        super();
        try {
            setDefaultXsltUrl(EscidocConfiguration
                .getInstance().appendToSelfURL(XSL_MAPPING_UNKNOWN_TO_DC));
        }
        catch (IOException e) {
            LOG.warn("Unable to set URL of DC mapping XSLTs "
                + "from configuration. " + e);
        }
    }

    /**
     * Sets the default style sheet url.
     * 
     * @param defaultXsltUrl
     *            The default style sheet url.
     * @common
     */
    public void setDefaultXsltUrl(final String defaultXsltUrl) {

        this.defaultXsltUrl = defaultXsltUrl;
    }

    /**
     * See Interface for functional description.
     * 
     * @param key
     *            Expecting String with XSLT location or String with XSLT
     *            location and contentModelId (separated through semicolon).
     * @return Transformer
     * @throws WebserverSystemException
     * @throws FedoraSystemException
     * @throws Exception
     * @see org.apache.commons.pool.BaseKeyedPoolableObjectFactory#makeObject(java.lang.Object)
     */
    @Override
    public Object makeObject(final Object key)
        throws WebserverSystemException, FedoraSystemException {
        if (!(key instanceof String)) {
            throw new UnsupportedOperationException(
                ERR_MSG_UNSUPPORTED_ARG_TYPE);
        }
        Transformer result = null;
        StreamSource streamSrc = null;
        InputStream xslt =null;
        try {
            xslt = mapKeyToXslt((String) key);
            streamSrc = new StreamSource(xslt);
            result = transformerFactory.newTransformer(streamSrc);
        } catch (IOException e) {
                throw new WebserverSystemException("XSLT for DC-mapping not retrievable.", e);
        } catch (TransformerConfigurationException e) {
            throw new WebserverSystemException("Transformer for DC-mapping can not be created.", e);
        } finally {
            try {
                if (xslt != null) {
                    xslt.close();
                }
            } catch (IOException e) {
                LOG.error("error on closing stream", e);
            }
        }
        return result;
    }

    /**
     * Maps the provided key to the related style sheet.
     * 
     * @param key
     *            The key for that the related style sheet shall be identified.
     * @return Returns the {@link URL} to the addressed style sheet. If no style
     *         sheet can be identified for the provided key, the default one
     *         identified by constant <code>XSL_MAPPING_UNKNOWN_TO_DC</code> is
     *         returned.
     * @throws FedoraSystemException
     * @throws WebserverSystemException
     * @throws IOException
     *             Thrown if retrieving values from eSciDoc properties
     *             (configuration) failed.
     */
    private InputStream mapKeyToXslt(final String key)
        throws WebserverSystemException, FedoraSystemException, IOException {

        String[] keyParts = key.split(";");
        String nsUri = keyParts[0];
        String contentModelId = keyParts[1];

        InputStream xslt = null;
        if (nsUri != null
            && nsUri.startsWith(NS_BASE_METADATAPROFILE_SCHEMA_ESCIDOC_MPG_DE)) {
            xslt =
                new URL(EscidocConfiguration.getInstance().appendToSelfURL(
                    XSL_MAPPING_MPDL_TO_DC)).openStream();
        }
        else {
            xslt = new URL(defaultXsltUrl).openStream();
        }
        // xslt is the mpdl-xslt- or default-xslt-stream
        if (contentModelId.length() > 0
            && !contentModelId.equalsIgnoreCase("null")) {
            // create link to content of DC-MAPPING in content model object
            String dcMappingXsltFedoraUrl =
                "/get/" + contentModelId + "/"
                    + CONTENT_MODEL_XSLT_DC_DATASTREAM;
            try {
                xslt =
                    FedoraUtility.getInstance().requestFedoraURL(
                        dcMappingXsltFedoraUrl);

            }
            catch (WebserverSystemException e) {
                // xslt is still the stream set above
            }
        }

        return xslt;
    }
}
