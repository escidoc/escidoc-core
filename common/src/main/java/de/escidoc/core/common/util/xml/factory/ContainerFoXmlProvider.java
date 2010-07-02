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
 * Copyright 2007-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.util.xml.factory;

import java.util.Map;

import de.escidoc.core.common.exceptions.system.WebserverSystemException;

/**
 * Container FoXML provider.
 * 
 * 
 */
public class ContainerFoXmlProvider extends InfrastructureFoXmlProvider {

    private static final String CONTAINER_PATH = "/container";

    private static final String CONTAINER_RESOURCE_NAME = "container";

    private static final String RELS_EXT_PATH = CONTAINER_PATH;

    private static final String RELS_EXT_RESOURCE_NAME = "rels-ext";

    // private static final String PROPERTIES_PATH = ORGANIZATIONAL_UNIT_PATH;
    //
    // private static final String RESOURCES_PATH = ORGANIZATIONAL_UNIT_PATH;
    //
    // private static final String DATA_PATH = ORGANIZATIONAL_UNIT_PATH;
    //
    // private static final String PARENT_OUS_PATH = ORGANIZATIONAL_UNIT_PATH;
    //
    // private static final String ORGANIZATIONAL_UNIT_LIST_PATH =
    // ORGANIZATIONAL_UNIT_PATH;
    //
    // private static final String ORGANIZATIONAL_UNIT_PATH_LIST_PATH =
    // ORGANIZATIONAL_UNIT_PATH;
    //
    // private static final String ORGANIZATIONAL_UNIT_REF_LIST_PATH =
    // ORGANIZATIONAL_UNIT_PATH;
    //
    // private static final String PROPERTIES_RESOURCE_NAME = "properties";
    //
    // private static final String RESOURCES_RESOURCE_NAME = "resources";
    //
    // private static final String ORGANIZATION_DETAILS_RESOURCE_NAME =
    // "organization-details";
    //
    // private static final String PARENT_OUS_RESOURCE_NAME = "parent-ous";
    //
    // private static final String ORGANIZATIONAL_UNIT_LIST_RESOURCE_NAME =
    // "organizational-unit-list";
    //
    // private static final String ORGANIZATIONAL_UNIT_PATH_LIST_RESOURCE_NAME =
    // "organizational-unit-path-list";
    //
    // private static final String ORGANIZATIONAL_UNIT_REF_LIST_RESOURCE_NAME =
    // "organizational-unit-ref-list";

    private static ContainerFoXmlProvider provider;

    /**
     * Private constructor to prevent initialization.
     * 
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @om
     */
    private ContainerFoXmlProvider() throws WebserverSystemException {
        super();
    }

    /**
     * Gets the role xml provider.
     * 
     * @return Returns the <code>UserAccountXmlProvider</code> object.
     * @throws WebserverSystemException
     *             Thrown if the instance cannot be created due to an internal
     *             error.
     * @om
     */
    public static ContainerFoXmlProvider getInstance()
        throws WebserverSystemException {

        if (provider == null) {
            provider = new ContainerFoXmlProvider();
        }
        return provider;
    }

    /**
     * Render Container to XML.
     * 
     * @param values
     *            Value Map.
     * @return XML representation of Container
     * 
     * @throws WebserverSystemException
     *             Thrown in case of internal failure.
     */
    public String getContainerFoXml(final Map values)
        throws WebserverSystemException {

        return getXml(CONTAINER_RESOURCE_NAME, CONTAINER_PATH, values);
    }

    /**
     * Render RELS-EXT.
     * 
     * @param values
     *            Value Map.
     * @return XML representation of RELS-EXT
     * @throws WebserverSystemException
     *             Thrown in case of internal failure.
     */
    public String getContainerRelsExt(final Map values)
        throws WebserverSystemException {

        return getXml(RELS_EXT_RESOURCE_NAME, RELS_EXT_PATH, values);
    }

    // public String getPropertiesXml(final Map values)
    // throws WebserverSystemException {
    //
    // return getXml(PROPERTIES_RESOURCE_NAME, PROPERTIES_PATH, values);
    // }
    //
    // public String getResourcesXml(final Map values)
    // throws WebserverSystemException {
    //
    // return getXml(RESOURCES_RESOURCE_NAME, RESOURCES_PATH, values);
    // }
    //
    // public String getDataXml(final Map values) throws
    // WebserverSystemException {
    //
    // return getXml(ORGANIZATION_DETAILS_RESOURCE_NAME, DATA_PATH, values);
    // }
    //
    // public String getParentOusXml(final Map values)
    // throws WebserverSystemException {
    //
    // return getXml(PARENT_OUS_RESOURCE_NAME, PARENT_OUS_PATH, values);
    // }
    //
    // public String getChildrenXml(final Map values)
    // throws WebserverSystemException {
    //
    // return getXml(ORGANIZATIONAL_UNIT_LIST_RESOURCE_NAME,
    // ORGANIZATIONAL_UNIT_LIST_PATH, values);
    // }
    //
    // public String getParentsXml(final Map values)
    // throws WebserverSystemException {
    //
    // return getXml(ORGANIZATIONAL_UNIT_LIST_RESOURCE_NAME,
    // ORGANIZATIONAL_UNIT_LIST_PATH, values);
    // }
    //
    // public String getPathListXml(final Map values)
    // throws WebserverSystemException {
    //
    // return getXml(ORGANIZATIONAL_UNIT_PATH_LIST_RESOURCE_NAME,
    // ORGANIZATIONAL_UNIT_PATH_LIST_PATH, values);
    // }
    //
    // public String getOrganizationalUnitsXml(final Map values)
    // throws WebserverSystemException {
    //
    // return getXml(ORGANIZATIONAL_UNIT_LIST_RESOURCE_NAME,
    // ORGANIZATIONAL_UNIT_LIST_PATH, values);
    // }
    //
    // public String getOrganizationalUnitRefsXml(final Map values)
    // throws WebserverSystemException {
    //
    // return getXml(ORGANIZATIONAL_UNIT_REF_LIST_RESOURCE_NAME,
    // ORGANIZATIONAL_UNIT_REF_LIST_PATH, values);
    // }
}
