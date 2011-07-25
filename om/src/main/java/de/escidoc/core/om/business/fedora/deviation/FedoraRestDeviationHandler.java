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
package de.escidoc.core.om.business.fedora.deviation;

import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.om.business.indexer.IndexerResourceRequester;
import de.escidoc.core.om.business.interfaces.FedoraRestDeviationHandlerInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Michael Hoppe
 */
@Service("business.FedoraRestDeviationHandler")
public class FedoraRestDeviationHandler implements FedoraRestDeviationHandlerInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(FedoraRestDeviationHandler.class);

    @Autowired
    private IndexerResourceRequester indexerResourceRequester;

    /**
     * @param pid        unused.
     * @param dsID       uri to component-content
     * @param parameters REST-GET-Parameters.
     * @return EscidocBinaryContent escidocBinaryContent
     * @throws Exception ex
     * @see de.escidoc.core.om.business.interfaces .FedoraRestDeviationHandlerInterface #getDatastreamDissemination(
     *      java.lang.String,java.lang.String,java.lang.String)
     */
    @Override
    public EscidocBinaryContent getDatastreamDissemination(
        final String pid, final String dsID, final Map<String, String[]> parameters) throws SystemException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("PID:" + pid + ", DSID:" + dsID);
        }
        EscidocBinaryContent escidocBinaryContent = null;
        try {
            escidocBinaryContent = (EscidocBinaryContent) this.indexerResourceRequester.getResource(dsID);
        }
        catch (final SystemException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on getting datastream dissemination.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on getting datastream dissemination.", e);
            }
            throw e;
        }
        if (escidocBinaryContent != null) {
            return escidocBinaryContent;
        }
        LOGGER.error(StringUtility.format("could not get resource for cache", dsID));

        return null;
    }

    /**
     * @param pid        uri to the resource.
     * @param parameters REST-GET-Parameters.
     * @return String String with the fedora-object as escidoc-xml
     * @throws Exception ex
     * @see de.escidoc.core.om.business.interfaces .FedoraRestDeviationHandlerInterface
     *      #export(java.lang.String,java.lang.String,java.lang.String)
     */
    @Override
    public String export(final String pid, final Map<String, String[]> parameters) throws SystemException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("PID:" + pid);
        }
        final String xml;

        try {
            xml = (String) this.indexerResourceRequester.getResource(pid);
        }
        catch (final SystemException e) {
            LOGGER.error(e.toString());
            throw e;
        }
        if (xml != null) {
            return xml;
        }
        LOGGER.info("Could not get resource " + pid + " for cache.");
        return null;
    }

    /**
     * writes the given xml into the cache.
     *
     * @param pid uri to the resource.
     * @param xml xml-representation of the object
     */
    @Override
    public void cache(final String pid, final String xml) throws SystemException {
        this.indexerResourceRequester.setResource(pid, xml);
    }

    /**
     * removes the given pid from the cache.
     *
     * @param pid uri to the resource.
     */
    @Override
    public void removeFromCache(final String pid) throws SystemException {
        this.indexerResourceRequester.deleteResource(pid);
    }

    /**
     * retreives the given pid not from cache.
     *
     * @param pid uri to the resource.
     */
    @Override
    public String retrieveUncached(final String pid) throws SystemException {
        return (String) this.indexerResourceRequester.getResourceUncached(pid);
    }

}
