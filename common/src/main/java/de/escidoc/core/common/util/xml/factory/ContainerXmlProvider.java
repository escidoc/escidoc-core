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

public final class ContainerXmlProvider extends InfrastructureXmlProvider {

    private static final String COMMON_PATH = "/common";

    private static final String CONTAINER_PATH = "/container";

    private static final String PROPERTIES_PATH = CONTAINER_PATH;

    private static final String RESOURCES_PATH = CONTAINER_PATH;

    private static final String STRUCTMAP_PATH = CONTAINER_PATH;

    private static final String MEMBER_REF_LIST_PATH = CONTAINER_PATH;

    private static final String TOC_VIEW_PATH = CONTAINER_PATH;

    private static final String CONTAINER_REF_LIST_PATH = CONTAINER_PATH;

    private static final String MEMBER_LIST_PATH = CONTAINER_PATH;

    private static final String CONTAINER_LIST_PATH = CONTAINER_PATH;

    private static final String CONTAINER_RESOURCE_NAME = "container";

    private static final String PROPERTIES_RESOURCE_NAME = "properties";

    private static final String RESOURCES_RESOURCE_NAME = "resources";

    private static final String STRUCTMAP_RESOURCE_NAME = "struct-map";

    private static final String MEMBER_REF_LIST_RESOURCE_NAME = "member-ref-list";

    private static final String TOC_VIEW_RESOURCE_NAME = "toc-view";

    private static final String CONTAINER_REF_LIST_RESOURCE_NAME = "container-ref-list";

    private static final String MEMBER_LIST_RESOURCE_NAME = "member-list";

    private static final String CONTAINER_LIST_RESOURCE_NAME = "container-list";

    private static final String PARENTS_RESOURCE_NAME = "parents";

    private static final ContainerXmlProvider PROVIDER = new ContainerXmlProvider();

    /**
     * Private constructor to prevent initialization.
     */
    private ContainerXmlProvider() {
    }

    /**
     * Gets the role xml PROVIDER.
     *
     * @return Returns the {@code UserAccountXmlProvider} object.
     */
    public static ContainerXmlProvider getInstance() {
        return PROVIDER;
    }

    public String getContainerXml(final Map values) throws WebserverSystemException {

        return getXml(CONTAINER_RESOURCE_NAME, CONTAINER_PATH, values);
    }

    public String getPropertiesXml(final Map values) throws WebserverSystemException {

        return getXml(PROPERTIES_RESOURCE_NAME, PROPERTIES_PATH, values);
    }

    public String getStructMapXml(final Map values) throws WebserverSystemException {

        return getXml(STRUCTMAP_RESOURCE_NAME, STRUCTMAP_PATH, values);
    }

    public String getMemberRefsXml(final Map values) throws WebserverSystemException {

        return getXml(MEMBER_REF_LIST_RESOURCE_NAME, MEMBER_REF_LIST_PATH, values);
    }

    public String getTocViewXml(final Map values) throws WebserverSystemException {

        return getXml(TOC_VIEW_RESOURCE_NAME, TOC_VIEW_PATH, values);
    }

    public String getContainerRefsXml(final Map values) throws WebserverSystemException {

        return getXml(CONTAINER_REF_LIST_RESOURCE_NAME, CONTAINER_REF_LIST_PATH, values);
    }

    public String getMembersXml(final Map values) throws WebserverSystemException {

        return getXml(MEMBER_LIST_RESOURCE_NAME, MEMBER_LIST_PATH, values);
    }

    public String getParentsXml(final Map values) throws WebserverSystemException {

        return getXml(PARENTS_RESOURCE_NAME, COMMON_PATH, values);
    }

    public String getContainersXml(final Map values) throws WebserverSystemException {

        return getXml(CONTAINER_LIST_RESOURCE_NAME, CONTAINER_LIST_PATH, values);
    }

    public String getResourcesXml(final Map values) throws WebserverSystemException {

        return getXml(RESOURCES_RESOURCE_NAME, RESOURCES_PATH, values);
    }

    public String structMapXml(final Map values) throws WebserverSystemException {

        return getXml(STRUCTMAP_RESOURCE_NAME, STRUCTMAP_PATH, values);
    }

}
