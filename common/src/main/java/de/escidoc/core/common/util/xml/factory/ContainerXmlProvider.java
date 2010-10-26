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
package de.escidoc.core.common.util.xml.factory;

import de.escidoc.core.common.exceptions.system.WebserverSystemException;

import java.util.HashMap;
import java.util.Map;

public class ContainerXmlProvider extends InfrastructureXmlProvider {

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

    private static final String CONTAINER_RESOURCE_NAME = "container";

    private static final String PROPERTIES_RESOURCE_NAME = "properties";

    private static final String RESOURCES_RESOURCE_NAME = "resources";

    private static final String STRUCTMAP_RESOURCE_NAME = "struct-map";

    private static final String MEMBER_REF_LIST_RESOURCE_NAME =
        "member-ref-list";

    private static final String TOC_VIEW_RESOURCE_NAME = "toc-view";

    private static final String CONTAINER_REF_LIST_RESOURCE_NAME =
        "container-ref-list";

    private static final String MEMBER_LIST_RESOURCE_NAME = "member-list";

    private static final String CONTAINER_LIST_RESOURCE_NAME = "container-list";

    private static final String PARENTS_RESOURCE_NAME = "parents";

    public static final String VAR_ID = "\\$\\{ID\\}";

    public static final String VAR_TITLE = "\\$\\{TITLE\\}";

    public static final String VAR_CONTENT = "\\$\\{CONTENT\\}";

    public static final String VAR_ADMIN_DESCRIPTOR =
        "\\$\\{ADMIN_DESCRIPTOR\\}";

    public static final String VAR_ADMIN_DESCRIPTOR_TITLE =
        "\\$\\{ADMIN_DESCRIPTOR_TITLE\\}";

    public static final String VAR_CREATION_DATE = "\\$\\{CREATION_DATE\\}";

    public static final String VAR_LATEST_MODIFICATION_DATE =
        "\\$\\{LATEST_MODIFICATION_DATE\\}";

    public static final String VAR_CONTEXT = "\\$\\{CONTEXT\\}";

    public static final String VAR_CONTEXT_TITLE = "\\$\\{CONTEXT_TITLE\\}";

    public static final String VAR_CONTENT_TYPE = "\\$\\{CONTENT_TYPE\\}";

    public static final String VAR_CONTENT_TYPE_TITLE =
        "\\$\\{CONTENT_TYPE_TITLE\\}";

    public static final String VAR_CREATOR = "\\$\\{CREATOR\\}";

    public static final String VAR_CREATOR_TITLE = "\\$\\{CREATOR_TITLE\\}";

    public static final String VAR_LOCK_OWNER = "\\$\\{LOCK_OWNER\\}";

    public static final String VAR_LOCK_STATUS = "\\$\\{LOCK_STATUS\\}";

    private static final String VAR_LOCK_ELEMENTS = "\\$\\{LOCK_ELEMENTS\\}";

    public static final String VAR_STATUS = "\\$\\{STATUS\\}";

    public static final String VAR_CONTENT_TYPE_SPECIFIC = "\\$\\{CTS\\}";

    public static final String VAR_CURRENT_VERSION_DATE =
        "\\$\\{CURRENT_VERSION_DATE\\}";

    public static final String VAR_CURRENT_VERSION_NUMBER =
        "\\$\\{CURRENT_VERSION_NUMBER\\}";

    public static final String VAR_CURRENT_VERSION_STATUS =
        "\\$\\{CURRENT_VERSION_STATUS\\}";

    public static final String VAR_CURRENT_VERSION_VALID_STATUS =
        "\\$\\{CURRENT_VERSION_VALID_STATUS\\}";

    public static final String VAR_LATEST_VERSION_DATE =
        "\\$\\{LATEST_VERSION_DATE\\}";

    public static final String VAR_LATEST_VERSION_NUMBER =
        "\\$\\{LATEST_VERSION_NUMBER\\}";

    public static final String VAR_LATEST_REVISION_DATE =
        "\\$\\{LATEST_REVISION_DATE\\}";

    public static final String VAR_LATEST_REVISION_NUMBER =
        "\\$\\{LATEST_REVISION_NUMBER\\}";

    public static final String VAR_LATEST_REVISION_PID =
        "\\$\\{LATEST_REVISION_PID\\}";

    public static final String VAR_LATEST_REVISION_PID_TITLE =
        "\\$\\{LATEST_REVISION_PID_TITLE\\}";

    public static final String VAR_OBJECT_TYPE = "\\$\\{OBJECT_TYPE\\}";

    public static final String VAR_MD_RECORDS_CONTENT =
        "\\$\\{MD_RECORDS_CONTENT\\}";

    public static final String VAR_MD_RECORDS_TITLE =
        "\\$\\{MD_RECORDS_TITLE\\}";

    private HashMap propertiesElements;

    private static ContainerXmlProvider provider;

    /**
     * Private constructor to prevent initialization.
     * 
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @aa
     */
    private ContainerXmlProvider() throws WebserverSystemException {
        super();
    }

    /**
     * Gets the role xml provider.
     * 
     * @return Returns the <code>UserAccountXmlProvider</code> object.
     * @throws WebserverSystemException
     *             Thrown if the instance cannot be created due to an internal
     *             error.
     * @aa
     */
    public static ContainerXmlProvider getInstance()
        throws WebserverSystemException {

        if (provider == null) {
            provider = new ContainerXmlProvider();
        }
        return provider;
    }

    public String getContainerXml(final Map values)
        throws WebserverSystemException {

        return getXml(CONTAINER_RESOURCE_NAME, CONTAINER_PATH, values);
    }

    public String getPropertiesXml(final Map values)
        throws WebserverSystemException {

        return getXml(PROPERTIES_RESOURCE_NAME, PROPERTIES_PATH, values);
    }

    public String getStructMapXml(final Map values)
        throws WebserverSystemException {

        return getXml(STRUCTMAP_RESOURCE_NAME, STRUCTMAP_PATH, values);
    }

    public String getMemberRefsXml(final Map values)
        throws WebserverSystemException {

        return getXml(MEMBER_REF_LIST_RESOURCE_NAME, MEMBER_REF_LIST_PATH,
            values);
    }

    public String getTocViewXml(final Map values)
        throws WebserverSystemException {

        return getXml(TOC_VIEW_RESOURCE_NAME, TOC_VIEW_PATH, values);
    }

    public String getContainerRefsXml(final Map values)
        throws WebserverSystemException {

        return getXml(CONTAINER_REF_LIST_RESOURCE_NAME,
            CONTAINER_REF_LIST_PATH, values);
    }

    public String getMembersXml(final Map values)
        throws WebserverSystemException {

        return getXml(MEMBER_LIST_RESOURCE_NAME, MEMBER_LIST_PATH, values);
    }

    public String getParentsXml(final Map values)
        throws WebserverSystemException {

        return getXml(PARENTS_RESOURCE_NAME, COMMON_PATH, values);
    }

    public String getContainersXml(final Map values)
        throws WebserverSystemException {

        return getXml(CONTAINER_LIST_RESOURCE_NAME, CONTAINER_LIST_PATH, values);
    }

    public String getResourcesXml(final Map values)
        throws WebserverSystemException {

        return getXml(RESOURCES_RESOURCE_NAME, RESOURCES_PATH, values);
    }

    // public String getDataXml(final Map values) throws
    // WebserverSystemException {
    //
    // return getXml(ORGANIZATION_DETAILS_RESOURCE_NAME, DATA_PATH, values);
    // }

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

    public String structMapXml(final Map values)
        throws WebserverSystemException {

        return getXml(STRUCTMAP_RESOURCE_NAME, STRUCTMAP_PATH, values);
    }

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
