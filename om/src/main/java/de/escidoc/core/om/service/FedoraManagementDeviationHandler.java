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

import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.om.service.interfaces.FedoraManagementDeviationHandlerInterface;

/**
 * Fedora management deviation handler that provides interface for fedoragsearch
 * and delegates to the resource handlers with providing the user information.
 * 
 * Security note: This handler should not be intercepted for authorization, as
 * it delegates to secured resource handlers with providing the original user
 * information.
 * 
 * @spring.bean id="service.FedoraManagementDeviationHandler" scope="prototype"
 * @interface class="de.escidoc.core.om.service.interfaces.FedoraManagementDeviationHandlerInterface"
 * @author MIH
 * @axis.service scope="Request" urn="management" provider="java:EscidocEJB"
 * @service
 * @om
 */
public class FedoraManagementDeviationHandler
    implements FedoraManagementDeviationHandlerInterface {

    private de.escidoc.core.om.business.interfaces.FedoraManagementDeviationHandlerInterface handler;

    /**
     * Injects the FedoraManagementDeviation handler.
     * 
     * @param fedoraManagementDeviationHandler
     *            The FedoraManagementDeviation handler bean to inject.
     * 
     * @spring.property ref="business.FedoraManagementDeviationHandler"
     * @service.exclude
     * @om
     */
    public void setFedoraManagementDeviationHandler(
        final de.escidoc.core.om.business.interfaces.FedoraManagementDeviationHandlerInterface fedoraManagementDeviationHandler) {

        this.handler = fedoraManagementDeviationHandler;
    }

    /**
     * @see de.escidoc.core.om.service.interfaces
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
     */
    public byte[] export(
        final String pid, final String format, final String context)
        throws Exception {
        return handler.export(pid, format, context);
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
     */
    public void cache(final String pid, final String xml) throws Exception {
        handler.cache(pid, xml);
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
        handler.removeFromCache(pid);
    }


}
