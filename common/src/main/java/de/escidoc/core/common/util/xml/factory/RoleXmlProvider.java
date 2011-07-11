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

public final class RoleXmlProvider extends InfrastructureXmlProvider {

    private static final String ROLE_RESOURCE_NAME = "role";

    private static final String ROLES_RESOURCE_NAME = "rolelist";

    private static final String ROLES_SRW_RESOURCE_NAME = "role-srw-list";

    private static final String ROLE_PATH = "/role";

    private static final String RESOURCES_PATH = ROLE_PATH;

    private static final String RESOURCES_RESOURCE_NAME = "resources";

    private static final RoleXmlProvider PROVIDER = new RoleXmlProvider();

    /**
     * Private constructor to prevent initialization.
     */
    private RoleXmlProvider() {
    }

    /**
     * Gets the role xml PROVIDER.
     *
     * @return Returns the {@code RoleXmlProvider} object.
     */
    public static RoleXmlProvider getInstance() {
        return PROVIDER;
    }

    public String getRoleXml(final Map values) throws WebserverSystemException {

        return getXml(ROLE_RESOURCE_NAME, RESOURCES_PATH, values);
    }

    public String getResourcesXml(final Map values) throws WebserverSystemException {

        return getXml(RESOURCES_RESOURCE_NAME, RESOURCES_PATH, values);
    }

    public String getRolesXml(final Map values) throws WebserverSystemException {

        return getXml(ROLES_RESOURCE_NAME, RESOURCES_PATH, values);
    }

    public String getRolesSrwXml(final Map values) throws WebserverSystemException {

        return getXml(ROLES_SRW_RESOURCE_NAME, RESOURCES_PATH, values);
    }
}
