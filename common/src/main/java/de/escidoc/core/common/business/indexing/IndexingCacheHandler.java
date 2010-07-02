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
package de.escidoc.core.common.business.indexing;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.om.service.interfaces.FedoraManagementDeviationHandlerInterface;

/**
 * Handler for handling cache for indexing.
 * 
 * @spring.bean id="common.business.indexing.IndexingCacheHandler" lazy-init="true"
 *              scope="singleton"
 * @author MIH
 * 
 * @common
 * 
 */
public class IndexingCacheHandler {

    private static AppLogger log =
        new AppLogger(IndexingCacheHandler.class.getName());

    private FedoraManagementDeviationHandlerInterface 
                        fedoraManagementDeviationHandler;

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
            fedoraManagementDeviationHandler.removeFromCache(id);
        } catch (Exception e) {
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
            fedoraManagementDeviationHandler.cache(id, xml);
        } catch (Exception e) {
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
            byte[] xmlBytes =
                fedoraManagementDeviationHandler.export(id, "", "public");
            if (xmlBytes != null) {
                return new String(xmlBytes, XmlUtility.CHARACTER_ENCODING);
            } else {
                throw new SystemException("Couldnt retrieve object with id " + id);
            }
        } catch (Exception e) {
            throw new SystemException(e);
        }
   }

    /**
     * Setting the fedoraManagementDeviationHandler.
     * 
     * @param fedoraManagementDeviationHandler
     *            The fedoraManagementDeviationHandler to set.
     * @spring.property ref="service.FedoraManagementDeviationHandlerBean"
     */
    public final void setFedoraManagementDeviationHandler(
        final FedoraManagementDeviationHandlerInterface 
        fedoraManagementDeviationHandler) {
        this.fedoraManagementDeviationHandler 
                = fedoraManagementDeviationHandler;
    }

}
