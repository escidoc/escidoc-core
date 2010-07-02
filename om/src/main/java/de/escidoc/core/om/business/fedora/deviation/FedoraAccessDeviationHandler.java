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

import de.escidoc.core.common.business.fedora.MIMETypedStream;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.om.business.indexer.IndexerResourceCache;
import de.escidoc.core.om.business.interfaces.FedoraAccessDeviationHandlerInterface;


/*******************************************************************************
 * @author MIH
 * 
 * @spring.bean id = "business.FedoraAccessDeviationHandler"
 * @om
 */
public class FedoraAccessDeviationHandler
    implements FedoraAccessDeviationHandlerInterface {

    private static AppLogger log =
        new AppLogger(FedoraAccessDeviationHandler.class.getName());

    /**
     * @see de.escidoc.core.om.business.interfaces
     *      .FedoraAccessDeviationHandlerInterface #getDatastreamDissemination(
     *      java.lang.String,java.lang.String,java.lang.String)
     * @param pid
     *            unused.
     * @param dsID
     *            uri to component-content
     * @param asOfDateTime
     *            unused.
     * 
     * @return MIMETypedStream mimeTypedStream
     * @throws Exception
     *             ex
     * 
     * @om
     */
    public MIMETypedStream getDatastreamDissemination(
        final String pid, final String dsID, final String asOfDateTime)
        throws Exception {

        MIMETypedStream stream = null;

        if (log.isDebugEnabled()) {
            log.debug("PID:" + pid + ", DSID:" + dsID + ", ASOFDATETIME:"
                + asOfDateTime);
        }
        // Try to get MIMETypedStream from IndexerResourceCache/////////////////
        try {
            stream =
                (MIMETypedStream) IndexerResourceCache
                    .getInstance().getResource(dsID);
        }
        catch (Exception e) {
            log.error(e.toString());
            throw e;
        }
        if (stream != null) {
            return stream;
        }
        else {
            log.error(StringUtility.concatenateWithBracketsToString(
                "could not get resource for cache", dsID));
        }
        // /////////////////////////////////////////////////////////////////////

        return null;
    }
}
