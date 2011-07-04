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

package de.escidoc.core.common.util.xml.factory;

import de.escidoc.core.common.exceptions.system.WebserverSystemException;

import java.util.Map;

/**
 * Scope renderer implementation using the velocity template engine.
 *
 * @author Michael Hoppe
 */
public final class ScopeXmlProvider extends InfrastructureXmlProvider {

    private static final String SCOPE_RESOURCE_NAME = "scope";

    private static final String SCOPES_RESOURCE_NAME = "scope-list";

    private static final String SCOPES_SRW_RESOURCE_NAME = "scope-srw-list";

    private static final String SCOPE_PATH = "/scope";

    private static final ScopeXmlProvider PROVIDER = new ScopeXmlProvider();

    /**
     * Private constructor to prevent initialization.
     */
    private ScopeXmlProvider() {
    }

    /**
     * Gets the Scope xml PROVIDER.
     *
     * @return Returns the {@code ScopeXmlProvider} object.
     * @throws WebserverSystemException Thrown if the instance cannot be created due to an internal error.
     */
    public static ScopeXmlProvider getInstance() {
        return PROVIDER;
    }

    /**
     * Gets the Scope xml.
     *
     * @param values rendering values
     * @return String rendered xml
     * @throws WebserverSystemException Thrown if the instance cannot be created due to an internal error.
     */
    public String getScopeXml(final Map values) throws WebserverSystemException {

        return getXml(SCOPE_RESOURCE_NAME, SCOPE_PATH, values);
    }

    /**
     * Gets the Scopes xml.
     *
     * @param values rendering values
     * @return String rendered xml
     * @throws WebserverSystemException Thrown if the instance cannot be created due to an internal error.
     */
    public String getScopesXml(final Map values) throws WebserverSystemException {

        return getXml(SCOPES_RESOURCE_NAME, SCOPE_PATH, values);
    }

    /**
     * Gets the Scopes xml in srw-schema.
     *
     * @param values rendering values
     * @return String rendered xml
     * @throws WebserverSystemException Thrown if the instance cannot be created due to an internal error.
     */
    public String getScopesSrwXml(final Map values) throws WebserverSystemException {

        return getXml(SCOPES_SRW_RESOURCE_NAME, SCOPE_PATH, values);
    }

}
