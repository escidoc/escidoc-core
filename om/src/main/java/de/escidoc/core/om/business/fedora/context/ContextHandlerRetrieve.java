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
package de.escidoc.core.om.business.fedora.context;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

import java.util.HashMap;
import java.util.Map;

/**
 * Context Retrieve Handler.
 *
 * @author Steffen Wagner
 */
public class ContextHandlerRetrieve extends ContextHandlerBase {

    private static final String IS_ROOT_RESOURCES = "isRootResources";

    /**
     * Get XML representation of Context.
     *
     * @param contextHandler FedoraContextHandler
     * @return XML representation of Context.
     * @throws SystemException If anything fails.
     */
    protected String getContextXml(final FedoraContextHandler contextHandler) throws SystemException {

        return getContextRenderer().render(contextHandler);
    }

    /**
     * Get XML representation of Context Properties.
     *
     * @param contextHandler FedoraContextHandler
     * @return XML representation of Context Properties.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    protected String getPropertiesXml(final FedoraContextHandler contextHandler) throws WebserverSystemException {

        return getContextRenderer().renderProperties(contextHandler);
    }

    /**
     * Get XML representation of Context Resources.
     *
     * @param contextHandler FedoraContextHandler
     * @return XML representation of Context Resources.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    protected String getResourcesXml(final FedoraContextHandler contextHandler) throws WebserverSystemException {

        return getContextRenderer().renderResources(contextHandler);
    }

    /**
     * Get the XML representation of all admin-descriptors.
     *
     * @param contextHandler FedoraContextHandler
     * @return XML representation of all admin-descriptors.
     * @throws SystemException          If anything fails.
     */
    protected String getAdminDescriptorsXml(final FedoraContextHandler contextHandler) throws SystemException {

        final Map<String, Object> values = new HashMap<String, Object>();
        values.put(IS_ROOT_RESOURCES, true);

        return getContextRenderer().renderAdminDescriptors(contextHandler, values);
    }
}
