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

public class CommonFoXmlProvider extends InfrastructureFoXmlProvider {

    private static final String COMMON_PATH = "/common";

    private static final String WOV_PATH = COMMON_PATH;

    private static final String WOV_RESOURCE_NAME = "wov";

    private static final String FOXML_VERSION_ENTRY_RESOURCE_NAME =
        "wov-version";

    public static final String FOXML_EVENT_RESOURCE_NAME = "premis-event";

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

    private static CommonFoXmlProvider provider;

    /**
     * Private constructor to prevent initialization.
     * 
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @om
     */
    private CommonFoXmlProvider() throws WebserverSystemException {
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
    public static CommonFoXmlProvider getInstance()
        throws WebserverSystemException {

        if (provider == null) {
            provider = new CommonFoXmlProvider();
        }
        return provider;
    }

    public String getWov(final Map values) throws WebserverSystemException {

        return getXml(WOV_RESOURCE_NAME, WOV_PATH, values);
    }

    public String getWovVersionEntryXml(final Map values)
        throws WebserverSystemException {

        return getXml(FOXML_VERSION_ENTRY_RESOURCE_NAME, WOV_PATH, values);
    }

    public String getPremisEventXml(final Map values)
        throws WebserverSystemException {
        String event = getXml(FOXML_EVENT_RESOURCE_NAME, WOV_PATH, values);
        return event;
    }
}
