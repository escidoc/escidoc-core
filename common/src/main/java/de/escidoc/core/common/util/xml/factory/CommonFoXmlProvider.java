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

public final class CommonFoXmlProvider extends InfrastructureFoXmlProvider {

    private static final String COMMON_PATH = "/common";

    private static final String WOV_PATH = COMMON_PATH;

    private static final String WOV_RESOURCE_NAME = "wov";

    private static final String FOXML_VERSION_ENTRY_RESOURCE_NAME = "wov-version";

    public static final String FOXML_EVENT_RESOURCE_NAME = "premis-event";

    private static final CommonFoXmlProvider PROVIDER = new CommonFoXmlProvider();

    /**
     * Private constructor to prevent initialization.
     */
    private CommonFoXmlProvider() {
    }

    /**
     * Gets the role xml PROVIDER.
     *
     * @return Returns the {@code UserAccountXmlProvider} object.
     */
    public static CommonFoXmlProvider getInstance() {
        return PROVIDER;
    }

    public String getWov(final Map values) throws WebserverSystemException {

        return getXml(WOV_RESOURCE_NAME, WOV_PATH, values);
    }

    public String getWovVersionEntryXml(final Map values) throws WebserverSystemException {

        return getXml(FOXML_VERSION_ENTRY_RESOURCE_NAME, WOV_PATH, values);
    }

    public String getPremisEventXml(final Map values) throws WebserverSystemException {
        return getXml(FOXML_EVENT_RESOURCE_NAME, WOV_PATH, values);
    }
}
