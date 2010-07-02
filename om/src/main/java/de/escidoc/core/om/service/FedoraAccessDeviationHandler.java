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
package de.escidoc.core.om.service;

import de.escidoc.core.common.business.fedora.MIMETypedStream;
import de.escidoc.core.om.service.interfaces.FedoraAccessDeviationHandlerInterface;

/**
 * Fedora access deviation handler that provides interface for fedoragsearch and
 * delegates to the resource handlers with providing the user information.
 * 
 * 
 * Security note: This handler should not be intercepted for authorization, as
 * it delegates to secured resource handlers with providing the original user
 * information.
 * 
 * @spring.bean id="service.FedoraAccessDeviationHandler" scope="prototype"
 * @interface class="de.escidoc.core.om.service.interfaces.FedoraAccessDeviationHandlerInterface"
 * @author MIH
 * @axis.service scope="Request" urn="access" provider="java:EscidocEJB"
 * @service
 * @om
 */
public class FedoraAccessDeviationHandler
    implements FedoraAccessDeviationHandlerInterface {

    private de.escidoc.core.om.business.interfaces.FedoraAccessDeviationHandlerInterface handler;

    /**
     * Injects the FedoraAccessDeviation handler.
     * 
     * @param fedoraAccessDeviationHandler
     *            The FedoraAccessDeviation handler bean to inject.
     * 
     * @spring.property ref="business.FedoraAccessDeviationHandler"
     * @service.exclude
     * @om
     */
    public void setFedoraAccessDeviationHandler(
        final de.escidoc.core.om.business.interfaces.FedoraAccessDeviationHandlerInterface fedoraAccessDeviationHandler) {

        this.handler = fedoraAccessDeviationHandler;
    }

    /**
     * @see de.escidoc.core.om.service.interfaces
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
     */
    public MIMETypedStream getDatastreamDissemination(
        final String pid, final String dsID, final String asOfDateTime)
        throws Exception {
        return handler.getDatastreamDissemination(pid, dsID, asOfDateTime);
    }
    
}
