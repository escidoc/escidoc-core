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

import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.om.business.indexer.IndexerResourceCache;
import de.escidoc.core.om.business.interfaces.FedoraManagementDeviationHandlerInterface;

/**
 * @author MIH
 * 
 * @spring.bean id = "business.FedoraManagementDeviationHandler"
 * @om
 */
public class FedoraManagementDeviationHandler
    implements FedoraManagementDeviationHandlerInterface {
    
    private static AppLogger log =
        new AppLogger(FedoraManagementDeviationHandler.class.getName());

    /**
     * @see de.escidoc.core.om.business.interfaces
     *      .FedoraManagementDeviationHandlerInterface
     *      #export(java.lang.String,java.lang.String,java.lang.String)
     * @param pid
     *            uri to the resource.
     * @param format
     *            unused
     * @param context
     *            unused.
     * 
     * @return byte[] byte[] with the fedora-object as escidoc-xml
     * @throws Exception
     *             ex
     * 
     * @om
     */
    public byte[] export(
        final String pid, final String format, final String context)
        throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("PID:" + pid + ", FORMAT:" + format + ", CONTEXT:"
                + context);
        }
        String xml = null;

        // Try to get xml from IndexerResourceCache/////////////////
        try {
            xml = (String) IndexerResourceCache.getInstance().getResource(pid);
        }
        catch (Exception e) {
            log.error(e.toString());
            throw e;
        }
        if (xml != null) {
            return xml.getBytes(XmlUtility.CHARACTER_ENCODING);
        }
        else {
            log.error("couldnt get resource " + pid + " for cache");
        }
        return null;
    }

    /**
     * writes the given xml into the cache.
     * 
     * @param pid
     *            uri to the resource.
     * @param xml
     *            xml-representation of the object
     * 
     * @throws Exception
     *             ex
     * 
     * @om
     */
    public void cache(final String pid, final String xml) throws Exception {
        IndexerResourceCache.getInstance().setResource(pid, xml);
    }

    /**
     * removes the given pid from the cache.
     * 
     * @param pid
     *            uri to the resource.
     * @throws Exception
     *             ex
     * 
     * @om
     */
    public void removeFromCache(final String pid) throws Exception {
        IndexerResourceCache.getInstance().deleteResource(pid);
    }

    /**
     * replaces the given pid in the cache
     * with the given xml.
     * 
     * @param pid
     *            uri to the resource.
     * @param xml
     *            xml-representation of the object.
     * @throws Exception
     *             ex
     * 
     * @om
     */
    public void replaceInCache(final String pid, final String xml) throws Exception {
        IndexerResourceCache.getInstance().replaceResource(pid, xml);
    }

}
