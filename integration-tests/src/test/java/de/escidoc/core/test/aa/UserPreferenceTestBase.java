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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.escidoc.core.common.exceptions.remote.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.UserAccountNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.UserGroupNotFoundException;
import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.aa.GrantClient;
import de.escidoc.core.test.common.client.servlet.aa.UserAccountClient;
import de.escidoc.core.test.security.client.PWCallback;

/**
 * Base Class for GrantTests (userGrants and groupGrants).
 * 
 * @author TTE
 * 
 */
public class UserPreferenceTestBase extends UserAccountTestBase {

    private UserAccountClient client = null;

    /**
     * @param transport
     *            The transport identifier.
     * @param handlerCode
     *            handlerCode.
     * @throws Exception
     *             e
     */
    public UserPreferenceTestBase(final int transport) throws Exception {
        super(transport);
        client = (UserAccountClient) getClient();
    }

    /**
     * Test creating a user preference.
     * 
     * @param id
     *            The id of the UserAccount.
     * @param grantXml
     *            The xml representation of the preference.
     * @return The xml representation of the created preference.
     * @throws Exception
     *             If anything fails.
     */
    protected String createPreference(final String id, final String xml)
        throws Exception {
        Object result = client.createPreference(id, xml);
        String xmlResult = null;
        if (result instanceof HttpMethod) {
            HttpMethod method = (HttpMethod) result;
            xmlResult = getResponseBodyAsUTF8(method);
            assertHttpStatusOfMethod("", method);
            method.releaseConnection();
        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }

    /**
     * Test deleting a user preference.
     * 
     * @param id
     *            The id of the UserAccount.
     * @param name
     *            The name of the preference.
     * @return The xml representation of the created preference.
     * @throws Exception
     *             If anything fails.
     */
    protected void deletePreference(final String id, final String name)
        throws Exception {
        Object result = client.deletePreference(id, name);
        if (result instanceof HttpMethod) {
            HttpMethod method = (HttpMethod) result;
            assertHttpStatusOfMethod("", method);
        }
    }

    /**
     * Test updating user preferences.
     * 
     * @param id
     *            The id of the UserAccount.
     * @param grantXml
     *            The xml representation of the preferences.
     * @return The xml representation of the updated preferences.
     * @throws Exception
     *             If anything fails.
     */
    protected String updatePreferences(final String id, final String xml)
        throws Exception {
        Object result = client.updatePreferences(id, xml);
        String xmlResult = null;
        if (result instanceof HttpMethod) {
            HttpMethod method = (HttpMethod) result;
            xmlResult = getResponseBodyAsUTF8(method);
            assertHttpStatusOfMethod("", method);
            method.releaseConnection();
        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }

    /**
     * Test updating single user preference.
     * 
     * @param id
     *            The id of the UserAccount.
     * @param grantXml
     *            The xml representation of the preference.
     * @return The xml representation of the updated preference.
     * @throws Exception
     *             If anything fails.
     */
    protected String updatePreference(
        final String id, final String name, final String xml) throws Exception {
        Object result = client.updatePreference(id, name, xml);
        String xmlResult = null;
        if (result instanceof HttpMethod) {
            HttpMethod method = (HttpMethod) result;
            xmlResult = getResponseBodyAsUTF8(method);
            assertHttpStatusOfMethod("", method);
            method.releaseConnection();
        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }

    protected String retrievePreferences(final String id) throws Exception {

        Object result = client.retrievePreferences(id);
        String xmlResult = null;
        if (result instanceof HttpMethod) {
            HttpMethod method = (HttpMethod) result;
            xmlResult = getResponseBodyAsUTF8(method);
            assertHttpStatusOfMethod("", method);
            method.releaseConnection();
        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }

    protected String retrievePreference(final String id, final String name)
        throws Exception {

        Object result = client.retrievePreference(id, name);
        String xmlResult = null;
        if (result instanceof HttpMethod) {
            HttpMethod method = (HttpMethod) result;
            xmlResult = getResponseBodyAsUTF8(method);
            assertHttpStatusOfMethod("", method);
            method.releaseConnection();
        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }
}
