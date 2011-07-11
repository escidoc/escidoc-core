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

public final class UserAccountXmlProvider extends InfrastructureXmlProvider {

    private static final String USER_ACCOUNT_RESOURCE_NAME = "user-account";

    private static final String USER_ACCOUNTS_SRW_RESOURCE_NAME = "user-account-srw-list";

    private static final String USER_ACCOUNT_PATH = "/user-account";

    private static final String RESOURCES_PATH = USER_ACCOUNT_PATH;

    private static final String RESOURCES_RESOURCE_NAME = "resources";

    private static final String GRANT_RESOURCE_NAME = "grant";

    private static final String GRANT_PATH = USER_ACCOUNT_PATH;

    private static final String CURRENT_GRANTS_RESOURCE_NAME = "current-grants";

    private static final String CURRENT_GRANTS_PATH = USER_ACCOUNT_PATH;

    private static final String GRANTS_SRW_RESOURCE_NAME = "grant-srw-list";

    private static final String GRANTS_PATH = USER_ACCOUNT_PATH;

    private static final String PREFERENCE_RESOURCE_NAME = "preference";

    private static final String PREFERENCE_PATH = USER_ACCOUNT_PATH;

    private static final String PREFERENCES_RESOURCE_NAME = "preferences";

    private static final String PREFERENCES_PATH = USER_ACCOUNT_PATH;

    private static final String ATTRIBUTE_RESOURCE_NAME = "attribute";

    private static final String ATTRIBUTE_PATH = USER_ACCOUNT_PATH;

    private static final String ATTRIBUTES_RESOURCE_NAME = "attributes";

    private static final String ATTRIBUTES_PATH = USER_ACCOUNT_PATH;

    private static final UserAccountXmlProvider PROVIDER = new UserAccountXmlProvider();

    /**
     * Private constructor to prevent initialization.
     */
    private UserAccountXmlProvider() {
    }

    /**
     * Gets the role xml PROVIDER.
     *
     * @return Returns the {@code UserAccountXmlProvider} object.
     */
    public static UserAccountXmlProvider getInstance() {
        return PROVIDER;
    }

    public String getUserAccountXml(final Map values) throws WebserverSystemException {

        return getXml(USER_ACCOUNT_RESOURCE_NAME, USER_ACCOUNT_PATH, values);
    }

    public String getResourcesXml(final Map values) throws WebserverSystemException {

        return getXml(RESOURCES_RESOURCE_NAME, RESOURCES_PATH, values);
    }

    public String getGrantXml(final Map values) throws WebserverSystemException {

        return getXml(GRANT_RESOURCE_NAME, GRANT_PATH, values);
    }

    public String getCurrentGrantsXml(final Map values) throws WebserverSystemException {

        return getXml(CURRENT_GRANTS_RESOURCE_NAME, CURRENT_GRANTS_PATH, values);
    }

    public String getGrantsXml(final Map values) throws WebserverSystemException {

        return getXml(GRANTS_SRW_RESOURCE_NAME, GRANTS_PATH, values);
    }

    public String getPreferenceXml(final Map values) throws WebserverSystemException {

        return getXml(PREFERENCE_RESOURCE_NAME, PREFERENCE_PATH, values);
    }

    public String getPreferencesXml(final Map values) throws WebserverSystemException {

        return getXml(PREFERENCES_RESOURCE_NAME, PREFERENCES_PATH, values);
    }

    /**
     * Gets xml for given user-attribute.
     *
     * @param values
     * @return String xml.
     * @throws WebserverSystemException Thrown if the instance cannot be created due to an internal error.
     */
    public String getAttributeXml(final Map values) throws WebserverSystemException {

        return getXml(ATTRIBUTE_RESOURCE_NAME, ATTRIBUTE_PATH, values);
    }

    /**
     * Gets xml for given user-attributes.
     *
     * @param values
     * @return String xml.
     * @throws WebserverSystemException Thrown if the instance cannot be created due to an internal error.
     */
    public String getAttributesXml(final Map values) throws WebserverSystemException {

        return getXml(ATTRIBUTES_RESOURCE_NAME, ATTRIBUTES_PATH, values);
    }

    public String getUserAccountsXml(final Map values) throws WebserverSystemException {

        return getXml(USER_ACCOUNTS_SRW_RESOURCE_NAME, RESOURCES_PATH, values);
    }
}
