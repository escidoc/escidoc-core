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

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.om.service.interfaces.FedoraRestDeviationHandlerInterface;

import java.util.HashMap;

/**
 * Handler for handling cache for indexing.
 *
 * @author MIH
 */
public class IndexingCacheHandler {

    private FedoraRestDeviationHandlerInterface 
                        fedoraRestDeviationHandler;

    /**
     * removes object + subobjects with given id from cache.
     *
     * @param id
     *            resource id
     * @throws SystemException
     *             The resource could not be removed.
     */
    public void removeIdFromCache(final String id)
                                throws SystemException {
        try {
            fedoraRestDeviationHandler.removeFromCache(id);
        } catch (final Exception e) {
            throw new SystemException(e);
        }
   }

    /**
     * removes object + subobjects with given id from cache.
     *
     * @param id
     *            resource id
     * @param xml
     *            xml
     * @throws SystemException
     *             The resource could not be removed.
     */
    public void replaceObjectInCache(
        final String id, final String xml)
                                throws SystemException {
        try {
            fedoraRestDeviationHandler.replaceInCache(id, xml);
        } catch (final Exception e) {
            throw new SystemException(e);
        }
   }

    /**
     * writes object with given id into cache.
     *
     * @param id
     *            resource id
     * @param xml
     *            xml
     * @throws SystemException
     *             The resource could not be removed.
     */
    public void writeObjectInCache(final String id, final String xml)
                                throws SystemException {
        try {
            fedoraRestDeviationHandler.cache(id, xml);
        } catch (final Exception e) {
            throw new SystemException(e);
        }
   }

    /**
     * writes object with given id into cache.
     *
     * @param id
     *            resource id
     * @return String xml-representation of requested object
     * @throws SystemException
     *             The resource could not be removed.
     */
    public String retrieveObjectFromCache(final String id)
                                throws SystemException {
        try {
            final String xml =
                fedoraRestDeviationHandler.export(id, new HashMap(0));
            if (xml != null) {
                return xml;
            } else {
                throw new SystemException("Couldnt retrieve object with id " + id);
            }
        } catch (final Exception e) {
            throw new SystemException(e);
        }
   }

    /**
     * Setting the fedoraRestDeviationHandler.
     * 
     * @param fedoraRestDeviationHandler
     *            The fedorarestDeviationHandler to set.
     */
    public final void setFedoraRestDeviationHandler(
        final FedoraRestDeviationHandlerInterface 
        fedoraRestDeviationHandler) {
        this.fedoraRestDeviationHandler 
                = fedoraRestDeviationHandler;
    }

}
