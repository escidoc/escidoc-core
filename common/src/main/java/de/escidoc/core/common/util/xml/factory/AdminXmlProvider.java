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
 * Admin Xml Provider.
 *
 * @author Michael Hoppe
 */
public class AdminXmlProvider extends InfrastructureXmlProvider {

    private static final String INDEX_CONFIGURATION_RESOURCE_NAME = "index-configuration";

    private static final String ADMIN_PATH = "/admin";

    private static final AdminXmlProvider PROVIDER = new AdminXmlProvider();

    /**
     * Gets the index-configuration as xml.
     *
     * @param values Map with values
     * @return Returns the index-configuration as xml.
     * @throws WebserverSystemException Thrown if the instance cannot be created due to an internal error.
     */
    public String getIndexConfigurationXml(final Map values) throws WebserverSystemException {

        return getXml(INDEX_CONFIGURATION_RESOURCE_NAME, ADMIN_PATH, values);
    }

    /**
     * Gets the admin xml PROVIDER.
     *
     * @return Returns the {@code AdminXmlProvider} object.
     */
    public static AdminXmlProvider getInstance() {
        return PROVIDER;
    }
}
