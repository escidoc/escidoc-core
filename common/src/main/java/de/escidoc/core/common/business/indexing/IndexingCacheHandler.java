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

package de.escidoc.core.common.business.indexing;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

import org.esidoc.core.utils.io.EscidocBinaryContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.om.service.interfaces.FedoraRestDeviationHandlerInterface;

/**
 * Handler for handling cache for indexing.
 *
 * @author Michael Hoppe
 */
@Service("common.business.indexing.IndexingCacheHandler")
public class IndexingCacheHandler {

    @Autowired
    @Qualifier("service.FedoraRestDeviationHandler")
    private FedoraRestDeviationHandlerInterface fedoraRestDeviationHandler;

    /**
     * Protected constructor to prevent instantiation outside of the Spring-context.
     */
    protected IndexingCacheHandler() {
    }

    /**
     * removes object with given id from cache.
     *
     * @param id  resource id
     * @throws SystemException The resource could not be removed.
     */
    public void removeObjectFromCache(final String id) throws SystemException {
        try {
            if (id != null && id.matches(".*?:.*?:.*")) {
                fedoraRestDeviationHandler.removeFromCache(id.substring(0, id.lastIndexOf(':')));
            }
            else {
                fedoraRestDeviationHandler.removeFromCache(id);
            }
        }
        catch (final Exception e) {
            throw new SystemException(e);
        }
    }

    /**
     * writes object with given id into cache.
     *
     * @param id  resource id
     * @param xml xml
     * @throws SystemException The resource could not be removed.
     */
    public void writeObjectInCache(final String id, final String xml) throws SystemException {
        try {
            final EscidocBinaryContent escidocBinaryContent = new EscidocBinaryContent();
            escidocBinaryContent.setMimeType(XmlUtility.MIME_TYPE_XML);
            escidocBinaryContent.setContent(new ByteArrayInputStream(((String) xml)
                .getBytes(XmlUtility.CHARACTER_ENCODING)));
            fedoraRestDeviationHandler.cache(id, escidocBinaryContent);
        }
        catch (final Exception e) {
            throw new SystemException(e);
        }
    }

    /**
     * writes object with given id into cache.
     *
     * @param id resource id
     * @return String xml-representation of requested object
     * @throws SystemException The resource could not be removed.
     */
    public String retrieveObjectFromCache(final String id) throws SystemException {
        try {
            final String xml = fedoraRestDeviationHandler.export(id, new HashMap(0));
            if (xml != null) {
                return xml;
            }
            else {
                throw new SystemException("Couldnt retrieve object with id " + id);
            }
        }
        catch (final Exception e) {
            throw new SystemException(e);
        }
    }

    /**
     * Setting the fedoraRestDeviationHandler.
     *
     * @param fedoraRestDeviationHandler The fedorarestDeviationHandler to set.
     */
    public final void setFedoraRestDeviationHandler(final FedoraRestDeviationHandlerInterface fedoraRestDeviationHandler) {
        this.fedoraRestDeviationHandler = fedoraRestDeviationHandler;
    }

}
