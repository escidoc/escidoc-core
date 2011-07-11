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
 * XmlTemplateProviderConstants implementation using the Velocity template engine.<br/> This implementation uses the velocity
 * singleton pattern.
 *
 * @author André Schenk
 */
public final class UserGroupXmlProvider extends InfrastructureXmlProvider {

    private static final String USER_GROUP_RESOURCE_NAME = "user-group";

    private static final String USER_GROUPS_RESOURCE_NAME = "user-group-list";

    private static final String USER_GROUPS_SRW_RESOURCE_NAME = "user-group-srw-list";

    private static final String USER_GROUP_PATH = "/user-group";

    private static final String RESOURCES_PATH = USER_GROUP_PATH;

    private static final String RESOURCES_RESOURCE_NAME = "resources";

    private static final String CURRENT_GRANTS_RESOURCE_NAME = "current-grants";

    private static final String CURRENT_GRANTS_PATH = USER_GROUP_PATH;

    private static final String GRANT_RESOURCE_NAME = "grant";

    private static final String GRANT_PATH = USER_GROUP_PATH;

    private static final UserGroupXmlProvider PROVIDER = new UserGroupXmlProvider();

    /**
     * Private constructor to prevent initialization.
     */
    private UserGroupXmlProvider() {
    }

    /**
     * Gets the role xml PROVIDER.
     *
     * @return Returns the {@code UserGroupXmlProvider} object.
     */
    public static UserGroupXmlProvider getInstance() {
        return PROVIDER;
    }

    public String getCurrentGrantsXml(final Map values) throws WebserverSystemException {

        return getXml(CURRENT_GRANTS_RESOURCE_NAME, CURRENT_GRANTS_PATH, values);
    }

    public String getGrantXml(final Map values) throws WebserverSystemException {

        return getXml(GRANT_RESOURCE_NAME, GRANT_PATH, values);
    }

    /**
     * Get the XML representation for a user group.
     *
     * @param values variables to be put into the Velocity template
     * @return XML representation for the user group
     * @throws WebserverSystemException Thrown if the instance cannot be created due to an internal error.
     */
    public String getUserGroupXml(final Map<String, Object> values) throws WebserverSystemException {

        return getXml(USER_GROUP_RESOURCE_NAME, USER_GROUP_PATH, values);
    }

    /**
     * Get the XML representation for the resources of a user group.
     *
     * @param values variables to be put into the Velocity template
     * @return XML representation for the resources of the user group
     * @throws WebserverSystemException Thrown if the instance cannot be created due to an internal error.
     */
    public String getResourcesXml(final Map<String, Object> values) throws WebserverSystemException {

        return getXml(RESOURCES_RESOURCE_NAME, RESOURCES_PATH, values);
    }

    /**
     * Get the XML representation for a list of user group.
     *
     * @param values variables to be put into the Velocity template
     * @return XML representation for the list of user group
     * @throws WebserverSystemException Thrown if the instance cannot be created due to an internal error.
     */
    public String getUserGroupsXml(final Map<String, Object> values) throws WebserverSystemException {

        return getXml(USER_GROUPS_RESOURCE_NAME, RESOURCES_PATH, values);
    }

    /**
     * Get the SRW response for a list of user group.
     *
     * @param values variables to be put into the Velocity template
     * @return XML representation for the list of user group
     * @throws WebserverSystemException Thrown if the instance cannot be created due to an internal error.
     */
    public String getUserGroupsSrwXml(final Map<String, Object> values) throws WebserverSystemException {

        return getXml(USER_GROUPS_SRW_RESOURCE_NAME, RESOURCES_PATH, values);
    }
}
