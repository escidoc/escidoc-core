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
import de.escidoc.core.common.business.fedora.MIMETypedStream;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.om.business.indexer.IndexerResourceCache;
import de.escidoc.core.om.business.interfaces.FedoraRestDeviationHandlerInterface;

import java.io.ByteArrayInputStream;
import java.util.Map;


/*******************************************************************************
 * @author MIH
 * 
 * @spring.bean id = "business.FedoraRestDeviationHandler"
 */
public class FedoraRestDeviationHandler
    implements FedoraRestDeviationHandlerInterface {

    private static final AppLogger log =
        new AppLogger(FedoraRestDeviationHandler.class.getName());

    /**
     * @see de.escidoc.core.om.business.interfaces
     *      .FedoraRestDeviationHandlerInterface #getDatastreamDissemination(
     *      java.lang.String,java.lang.String,java.lang.String)
     * @param pid
     *            unused.
     * @param dsID
     *            uri to component-content
     * @param parameters REST-GET-Parameters.
     * 
     * @return EscidocBinaryContent escidocBinaryContent
     * @throws Exception
     *             ex
     * 
     */
    @Override
    public EscidocBinaryContent getDatastreamDissemination(
        final String pid, final String dsID, 
        final Map<String, String[]> parameters)
        throws Exception {

        EscidocBinaryContent escidocBinaryContent = null;
        
        if (log.isDebugEnabled()) {
            log.debug("PID:" + pid + ", DSID:" + dsID);
        }
        // Try to get EscidocBinaryContent from IndexerResourceCache/////////////////
        try {
            MIMETypedStream mimeTypedStream =
                (MIMETypedStream) IndexerResourceCache
                    .getInstance().getResource(dsID);
            if (mimeTypedStream != null && mimeTypedStream.getStream() != null) {
                escidocBinaryContent = new EscidocBinaryContent();
                escidocBinaryContent.setMimeType(mimeTypedStream.getMIMEType());
                escidocBinaryContent.setContent(
                		new ByteArrayInputStream(mimeTypedStream.getStream()));
            }
        }
        catch (Exception e) {
            log.error(e.toString());
            throw e;
        }
        if (escidocBinaryContent != null) {
            return escidocBinaryContent;
        }
        else {
            log.error(StringUtility.format(
                "could not get resource for cache", dsID));
        }
        // /////////////////////////////////////////////////////////////////////

        return null;
    }

    /**
     * @see de.escidoc.core.om.business.interfaces
     *      .FedoraRestDeviationHandlerInterface
     *      #export(java.lang.String,java.lang.String,java.lang.String)
     * @param pid
     *            uri to the resource.
     * @param parameters REST-GET-Parameters.
     * 
     * @return String String with the fedora-object as escidoc-xml
     * @throws Exception
     *             ex
     * 
     */
    @Override
    public String export(
        final String pid, final Map<String, String[]> parameters)
        throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("PID:" + pid);
        }
        String xml;

        // Try to get xml from IndexerResourceCache/////////////////
        try {
            xml = (String) IndexerResourceCache.getInstance().getResource(pid);
        }
        catch (Exception e) {
            log.error(e.toString());
            throw e;
        }
        if (xml != null) {
            return xml;
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
    @Override
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
    @Override
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
    @Override
    public void replaceInCache(final String pid, final String xml) throws Exception {
        IndexerResourceCache.getInstance().replaceResource(pid, xml);
    }

}
