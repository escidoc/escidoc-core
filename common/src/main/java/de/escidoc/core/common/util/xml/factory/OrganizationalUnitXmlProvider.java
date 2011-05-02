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
 * OrganizationalUnitXmlProvider.
 */
public final class OrganizationalUnitXmlProvider extends InfrastructureXmlProvider {

    private static final String ORGANIZATIONAL_UNIT_PATH = "/organizational-unit";

    private static final String PROPERTIES_PATH = ORGANIZATIONAL_UNIT_PATH;

    private static final String RESOURCES_PATH = ORGANIZATIONAL_UNIT_PATH;

    private static final String COMMON_PATH = "/common";

    private static final String PARENT_OUS_PATH = ORGANIZATIONAL_UNIT_PATH;

    private static final String SUCCESSORS_PATH = ORGANIZATIONAL_UNIT_PATH;

    private static final String ORGANIZATIONAL_UNIT_LIST_PATH = ORGANIZATIONAL_UNIT_PATH;

    private static final String ORGANIZATIONAL_UNIT_PATH_LIST_PATH = ORGANIZATIONAL_UNIT_PATH;

    private static final String ORGANIZATIONAL_UNIT_RESOURCE_NAME = "organizational-unit";

    private static final String PROPERTIES_RESOURCE_NAME = "properties";

    private static final String RESOURCES_RESOURCE_NAME = "resources";

    private static final String MD_RECORDS_RESOURCE_NAME = "md-records";

    private static final String PARENTS_RESOURCE_NAME = "parents";

    private static final String SUCCESSORS_RESOURCE_NAME = "successors";

    private static final String ORGANIZATIONAL_UNIT_LIST_RESOURCE_NAME = "organizational-unit-list";

    private static final String ORGANIZATIONAL_UNIT_PATH_LIST_RESOURCE_NAME = "organizational-unit-path-list";

    private static final OrganizationalUnitXmlProvider PROVIDER = new OrganizationalUnitXmlProvider();

    /**
     * Private constructor to prevent initialization.
     */
    private OrganizationalUnitXmlProvider() {
    }

    /**
     * Get instance of OrganizationalUnitXmlProvider.
     *
     * @return OrganizationalUnitXmlProvider
     */
    public static OrganizationalUnitXmlProvider getInstance() {
        return PROVIDER;
    }

    public String getOrganizationalUnitXml(final Map values) throws WebserverSystemException {

        return getXml(ORGANIZATIONAL_UNIT_RESOURCE_NAME, ORGANIZATIONAL_UNIT_PATH, values);
    }

    public String getPropertiesXml(final Map values) throws WebserverSystemException {

        return getXml(PROPERTIES_RESOURCE_NAME, PROPERTIES_PATH, values);
    }

    public String getResourcesXml(final Map values) throws WebserverSystemException {

        return getXml(RESOURCES_RESOURCE_NAME, RESOURCES_PATH, values);
    }

    public String getMdRecordsXml(final Map values) throws WebserverSystemException {

        return getXml(MD_RECORDS_RESOURCE_NAME, COMMON_PATH, values);
    }

    public String getParentsXml(final Map values) throws WebserverSystemException {

        return getXml(PARENTS_RESOURCE_NAME, PARENT_OUS_PATH, values);
    }

    public String getChildObjectsXml(final Map values) throws WebserverSystemException {

        return getXml(ORGANIZATIONAL_UNIT_LIST_RESOURCE_NAME, ORGANIZATIONAL_UNIT_LIST_PATH, values);
    }

    public String getParentObjectsXml(final Map values) throws WebserverSystemException {

        return getXml(ORGANIZATIONAL_UNIT_LIST_RESOURCE_NAME, ORGANIZATIONAL_UNIT_LIST_PATH, values);
    }

    public String getPathListXml(final Map values) throws WebserverSystemException {

        return getXml(ORGANIZATIONAL_UNIT_PATH_LIST_RESOURCE_NAME, ORGANIZATIONAL_UNIT_PATH_LIST_PATH, values);
    }

    /**
     * Get list of successors of Orgnaizational Unit as XML.
     *
     * @param values value map.
     * @return XML representation of Organizational Unit successors.
     * @throws WebserverSystemException Thrown if rendering failed.
     */
    public String getSuccessorsXml(final Map values) throws WebserverSystemException {

        return getXml(SUCCESSORS_RESOURCE_NAME, SUCCESSORS_PATH, values);
    }

}
