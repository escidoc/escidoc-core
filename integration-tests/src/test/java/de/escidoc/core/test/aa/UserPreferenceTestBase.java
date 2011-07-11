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
package de.escidoc.core.test.aa;

import de.escidoc.core.test.common.client.servlet.aa.UserAccountClient;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * Base Class for GrantTests (userGrants and groupGrants).
 *
 * @author Torsten Tetteroo
 */
public abstract class UserPreferenceTestBase extends UserAccountTestBase {

    private UserAccountClient client = null;

    public UserPreferenceTestBase() {
        client = (UserAccountClient) getClient();
    }

    /**
     * Test creating a user preference.
     *
     * @param id       The id of the UserAccount.
     * @return The xml representation of the created preference.
     * @throws Exception If anything fails.
     */
    protected String createPreference(final String id, final String xml) throws Exception {
        Object result = client.createPreference(id, xml);
        String xmlResult = null;
        if (result instanceof HttpResponse) {
            HttpResponse method = (HttpResponse) result;
            xmlResult = EntityUtils.toString(method.getEntity(), HTTP.UTF_8);
            assertHttpStatusOfMethod("", method);

        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }

    /**
     * Test deleting a user preference.
     *
     * @param id   The id of the UserAccount.
     * @param name The name of the preference.
     * @return The xml representation of the created preference.
     * @throws Exception If anything fails.
     */
    protected void deletePreference(final String id, final String name) throws Exception {
        Object result = client.deletePreference(id, name);
        if (result instanceof HttpResponse) {
            HttpResponse method = (HttpResponse) result;
            assertHttpStatusOfMethod("", method);
        }
    }

    /**
     * Test updating user preferences.
     *
     * @param id       The id of the UserAccount.
     * @return The xml representation of the updated preferences.
     * @throws Exception If anything fails.
     */
    protected String updatePreferences(final String id, final String xml) throws Exception {
        Object result = client.updatePreferences(id, xml);
        String xmlResult = null;
        if (result instanceof HttpResponse) {
            HttpResponse method = (HttpResponse) result;
            xmlResult = EntityUtils.toString(method.getEntity(), HTTP.UTF_8);
            assertHttpStatusOfMethod("", method);
        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }

    /**
     * Test updating single user preference.
     *
     * @param id       The id of the UserAccount.
     * @return The xml representation of the updated preference.
     * @throws Exception If anything fails.
     */
    protected String updatePreference(final String id, final String name, final String xml) throws Exception {
        Object result = client.updatePreference(id, name, xml);
        String xmlResult = null;
        if (result instanceof HttpResponse) {
            HttpResponse method = (HttpResponse) result;
            xmlResult = EntityUtils.toString(method.getEntity(), HTTP.UTF_8);
            assertHttpStatusOfMethod("", method);
        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }

    protected String retrievePreferences(final String id) throws Exception {

        Object result = client.retrievePreferences(id);
        String xmlResult = null;
        if (result instanceof HttpResponse) {
            HttpResponse method = (HttpResponse) result;
            xmlResult = EntityUtils.toString(method.getEntity(), HTTP.UTF_8);
            assertHttpStatusOfMethod("", method);
        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }

    protected String retrievePreference(final String id, final String name) throws Exception {

        Object result = client.retrievePreference(id, name);
        String xmlResult = null;
        if (result instanceof HttpResponse) {
            HttpResponse method = (HttpResponse) result;
            xmlResult = EntityUtils.toString(method.getEntity(), HTTP.UTF_8);
            assertHttpStatusOfMethod("", method);

        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }
}
