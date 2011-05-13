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
package de.escidoc.core.test.common.client.servlet.aa;

import de.escidoc.core.test.EscidocTestBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.HttpHelper;
import de.escidoc.core.test.common.client.servlet.interfaces.ResourceHandlerClientInterface;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Offers access methods to the escidoc interface of the user resource.
 *
 * @author Michael Schneider
 */
public class UserAccountClient extends GrantClient implements ResourceHandlerClientInterface {

    private static final int THREE = 3;

    /**
     * Create an user.
     *
     * @param userXml The xml representation of an user
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object create(final Object userXml) throws Exception {

        return callEsciDoc("UserAccount.create", METHOD_CREATE, Constants.HTTP_METHOD_PUT,
            Constants.USER_ACCOUNT_BASE_URI, new String[] {}, changeToString(userXml));
    }

    /**
     * Delete an user.
     *
     * @param id The user id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object delete(final String id) throws Exception {
        return callEsciDoc("UserAccount.delete", METHOD_DELETE, Constants.HTTP_METHOD_DELETE,
            Constants.USER_ACCOUNT_BASE_URI, new String[] { id });
    }

    /**
     * Retrieve the xml representation of an user.
     *
     * @param id The id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object retrieve(final String id) throws Exception {
        return callEsciDoc("UserAccount.retrieve", METHOD_RETRIEVE, Constants.HTTP_METHOD_GET,
            Constants.USER_ACCOUNT_BASE_URI, new String[] { id });
    }

    /**
     * Retrieve the xml representation of the current user.
     *
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveCurrentUser() throws Exception {
        return callEsciDoc("UserAccount.retrieveCurrentUser", METHOD_RETRIEVE_CURRENT_USER, Constants.HTTP_METHOD_GET,
            Constants.USER_ACCOUNT_BASE_URI + "/current", new String[] {});
    }

    /**
     * Retrieve the xml representation of the resources of an user account.
     *
     * @param id The id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveResources(final String id) throws Exception {
        return callEsciDoc("UserAccount.retrieveResources", METHOD_RETRIEVE_RESOURCES, Constants.HTTP_METHOD_GET,
            Constants.USER_ACCOUNT_BASE_URI, new String[] { id, Constants.SUB_RESOURCES });
    }

    /**
     * Retrieve the xml representation of the list of user accounts.
     *
     * @param filter The filter parameters.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveUserAccounts(final Map<String, String[]> filter) throws Exception {
        return callEsciDoc("UserAccount.retrieveUserAccounts", METHOD_RETRIEVE_USER_ACCOUNTS,
            Constants.HTTP_METHOD_GET, Constants.USER_ACCOUNTS_BASE_URI, new String[] {}, filter);
    }

    /**
     * Update an user.
     *
     * @param id      The user id.
     * @param userXml The xml representation of the user
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object update(final String id, final Object userXml) throws Exception {

        return callEsciDoc("UserAccount.update", METHOD_UPDATE, Constants.HTTP_METHOD_PUT,
            Constants.USER_ACCOUNT_BASE_URI, new String[] { id }, changeToString(userXml));
    }

    /**
     * Update the password of an user.
     *
     * @param id           The user id.
     * @param taskParamXml The xml representation of the user
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object updatePassword(final String id, final Object taskParamXml) throws Exception {

        return callEsciDoc("UserAccount.updatePassword", METHOD_UPDATE_PASSWORD, Constants.HTTP_METHOD_POST,
            Constants.USER_ACCOUNT_BASE_URI, new String[] { id, "update-password" }, changeToString(taskParamXml));
    }

    /**
     * Activate an user account.
     *
     * @param id           The user account id.
     * @param taskParamXml The task parameter in an XML structure.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object activate(final String id, final String taskParamXml) throws Exception {

        return callEsciDoc("UserAccount.activate", METHOD_ACTIVATE, Constants.HTTP_METHOD_POST,
            Constants.USER_ACCOUNT_BASE_URI, new String[] { id, "activate" }, changeToString(taskParamXml));
    }

    /**
     * Deactivate an user account.
     *
     * @param id           The user account id.
     * @param taskParamXml The task parameter in an XML structure.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object deactivate(final String id, final String taskParamXml) throws Exception {

        return callEsciDoc("UserAccount.deactivate", METHOD_DEACTIVATE, Constants.HTTP_METHOD_POST,
            Constants.USER_ACCOUNT_BASE_URI, new String[] { id, "deactivate" }, changeToString(taskParamXml));
    }

    /**
     * Retrieve the current grants of the specified user account.
     *
     * @param id The user account id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object retrieveCurrentGrants(final String id) throws Exception {

        return callEsciDoc("UserAccount.retrieveCurrentGrants", METHOD_RETRIEVE_CURRENT_GRANTS,
            Constants.HTTP_METHOD_GET, Constants.USER_ACCOUNT_BASE_URI, new String[] { id, "resources/current-grants" });
    }

    /**
     * Create a grant for the specified user account.
     *
     * @param id       The user account id.
     * @param grantXml The XML representation of an grant.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object createGrant(final String id, final String grantXml) throws Exception {

        return callEsciDoc("UserAccount.createGrant", METHOD_CREATE_GRANT, Constants.HTTP_METHOD_PUT,
            Constants.USER_ACCOUNT_BASE_URI, new String[] { id, "/resources/grants/grant" }, changeToString(grantXml));
    }

    /**
     * Retrieve a grant from the specified user account.
     *
     * @param id      The user account id.
     * @param grantId The id of the grant.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object retrieveGrant(final String id, final String grantId) throws Exception {

        return callEsciDoc("UserAccount.retrieveGrant", METHOD_RETRIEVE_GRANT, Constants.HTTP_METHOD_GET,
            Constants.USER_ACCOUNT_BASE_URI, new String[] { id, "resources/grants/grant", grantId });
    }

    /**
     * Revoke a grant from the specified user account.
     *
     * @param id           The user account id.
     * @param grantId      The id of the grant.
     * @param taskParamXml The task parameter in an XML structure.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object revokeGrant(final String id, final String grantId, final String taskParamXml) throws Exception {

        String[] pathElements = new String[] { id, "resources/grants/grant", grantId, "revoke-grant" };
        return callEsciDoc("UserAccount.revokeGrant", METHOD_REVOKE_GRANT, Constants.HTTP_METHOD_POST,
            Constants.USER_ACCOUNT_BASE_URI, pathElements, changeToString(taskParamXml));
    }

    /**
     * Revoke a grant from the specified user account.
     *
     * @param id        The user account id.
     * @param filterXml The filter-criteria in an XML structure.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object revokeGrants(final String id, final String filterXml) throws Exception {

        String[] pathElements = new String[] { id, "resources/grants/revoke-grants" };
        return callEsciDoc("UserAccount.revokeGrants", METHOD_REVOKE_GRANTS, Constants.HTTP_METHOD_POST,
            Constants.USER_ACCOUNT_BASE_URI, pathElements, changeToString(filterXml));
    }

    /**
     * Retrieve a list of grants matching the provided filter-criteria.
     *
     * @param filter The filter-criteria in a CQL query.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object retrieveGrants(final Map<String, String[]> filter) throws Exception {

        return callEsciDoc("UserAccount.retrieveGrants", METHOD_RETRIEVE_GRANTS, Constants.HTTP_METHOD_GET,
            Constants.GRANTS_BASE_URI, new String[] {}, filter);
    }

    /**
     * Login to the eSciDoc framework.
     *
     * @param login    The login name.
     * @param password The password.
     * @return The HttpMethod after the service call.
     * @throws Exception If the service call fails.
     */
    public HttpResponse login(final String login, final String password) throws Exception {
        HttpResponse result = null;
        String url = EscidocTestBase.getFrameworkUrl() + Constants.UM_LOGIN_BASE_URI;
        NameValuePair[] param = new NameValuePair[THREE];
        NameValuePair loginParam = new BasicNameValuePair(Constants.PARAM_UM_LOGIN_NAME, login);

        param[0] = loginParam;
        NameValuePair passwordParam = new BasicNameValuePair(Constants.PARAM_UM_LOGIN_PASSWORD, password);
        param[1] = passwordParam;
        NameValuePair redirectUrlParam = new BasicNameValuePair(Constants.PARAM_UM_REDIRECT_URL, password);
        param[2] = redirectUrlParam;
        result = HttpHelper.executeHttpRequest(getHttpClient(), Constants.HTTP_METHOD_POST, url, param, null, null);
        return result;
    }

    /**
     * Login to the eSciDoc framework.
     *
     * @param login    The login name.
     * @param password The password.
     * @param check    Indicates if the login was successful (handle != null, response-status == 200)
     * @return A handle if login was successful.
     * @throws Exception If the service call fails.
     */
    public String login(final String login, final String password, final boolean check) throws Exception {
        HttpResponse httpRes = login(login, password);
        Cookie handleCookie = HttpHelper.getCookie(getHttpClient());
        if (check) {
            assertEquals("Login to eSciDoc not successful! Wrong response status!", HttpServletResponse.SC_SEE_OTHER,
                httpRes.getStatusLine().getStatusCode());
            assertNotNull("Login to eSciDoc not successful! No handle" + " generated!", handleCookie);
            Header location = httpRes.getFirstHeader("Location");
            assertNotNull("No redirect location returned", location);
            assertTrue("No authorization handle returned as parameter of" + " redirect URL", (location
                .getValue().indexOf(Constants.PARAM_UM_AUTHORIZATION) != -1));
            assertNotNull("No authorization handle parameter returned");
        }

        String result = null;
        if (handleCookie != null) {
            result = handleCookie.getValue();
        }
        return result;
    }

    /**
     * Retrieve the current preferences of the specified user account.
     *
     * @param id The user account id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrievePreferences(final String id) throws Exception {

        return callEsciDoc("UserAccount.retrievePreferences", METHOD_RETRIEVE_PREFERENCES, Constants.HTTP_METHOD_GET,
            Constants.USER_ACCOUNT_BASE_URI, new String[] { id, "resources/preferences" });
    }

    /**
     * Retrieve the preference with given name of the specified user account.
     *
     * @param id   The user account id.
     * @param name The name of the preference.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrievePreference(final String id, final String name) throws Exception {

        return callEsciDoc("UserAccount.retrievePreference", METHOD_RETRIEVE_PREFERENCE, Constants.HTTP_METHOD_GET,
            Constants.USER_ACCOUNT_BASE_URI, new String[] { id, "resources/preferences/preference", name });
    }

    /**
     * Create a user preference.
     *
     * @param id  The id of the UserAccount.
     * @param xml The xml representation of the preference.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object createPreference(final String id, final String xml) throws Exception {

        return callEsciDoc("UserAccount.createPreference", METHOD_CREATE_PREFERENCE, Constants.HTTP_METHOD_PUT,
            Constants.USER_ACCOUNT_BASE_URI, new String[] { id, "resources/preferences/preference" },
            changeToString(xml));
    }

    /**
     * Delete a user preference.
     *
     * @param id   The id of the UserAccount.
     * @param name The xml representation of the preference.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object deletePreference(final String id, final String name) throws Exception {

        return callEsciDoc("UserAccount.createPreference", METHOD_DELETE_PREFERENCE, Constants.HTTP_METHOD_DELETE,
            Constants.USER_ACCOUNT_BASE_URI, new String[] { id, "resources/preferences/preference", name });
    }

    /**
     * Update user preferences.
     *
     * @param id  The id of the UserAccount.
     * @param xml The xml representation of the preferences.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object updatePreferences(final String id, final String xml) throws Exception {

        return callEsciDoc("UserAccount.updatePreferences", METHOD_UPDATE_PREFERENCES, Constants.HTTP_METHOD_PUT,
            Constants.USER_ACCOUNT_BASE_URI, new String[] { id, "resources/preferences" }, changeToString(xml));
    }

    /**
     * Update user preference.
     *
     * @param id   The id of the UserAccount.
     * @param name Name of preference.
     * @param xml  The xml representation of the preference.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object updatePreference(final String id, final String name, final String xml) throws Exception {

        return callEsciDoc("UserAccount.updatePreference", METHOD_UPDATE_PREFERENCE, Constants.HTTP_METHOD_PUT,
            Constants.USER_ACCOUNT_BASE_URI, new String[] { id, "resources/preferences/preference", name },
            changeToString(xml));
    }

    /**
     * Create a user attribute.
     *
     * @param userId The id of the UserAccount.
     * @param xml    The xml representation of the attribute.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object createAttribute(final String userId, final String xml) throws Exception {

        return callEsciDoc("UserAccount.createAttribute", METHOD_CREATE_ATTRIBUTE, Constants.HTTP_METHOD_PUT,
            Constants.USER_ACCOUNT_BASE_URI, new String[] { userId, "resources/attributes/attribute" },
            changeToString(xml));
    }

    /**
     * Retrieve the attributes of the specified user account.
     *
     * @param userId The user account id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveAttributes(final String userId) throws Exception {

        return callEsciDoc("UserAccount.retrieveAttributes", METHOD_RETRIEVE_ATTRIBUTES, Constants.HTTP_METHOD_GET,
            Constants.USER_ACCOUNT_BASE_URI, new String[] { userId, "resources/attributes" });
    }

    /**
     * Retrieve the attributes of the specified user account.
     *
     * @param userId The user account id.
     * @param name   The name of the requested attributes.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveNamedAttributes(final String userId, final String name) throws Exception {

        return callEsciDoc("UserAccount.retrieveNamedAttributes", METHOD_RETRIEVE_NAMED_ATTRIBUTES,
            Constants.HTTP_METHOD_GET, Constants.USER_ACCOUNT_BASE_URI, new String[] { userId, "resources/attributes",
                name });
    }

    /**
     * Retrieve the attribute of the specified user account and attributeId.
     *
     * @param userId      The user account id.
     * @param attributeId The id of the attribute.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveAttribute(final String userId, final String attributeId) throws Exception {

        return callEsciDoc("UserAccount.retrieveAttribute", METHOD_RETRIEVE_ATTRIBUTE, Constants.HTTP_METHOD_GET,
            Constants.USER_ACCOUNT_BASE_URI, new String[] { userId, "resources/attributes/attribute", attributeId });
    }

    /**
     * Update user attribute.
     *
     * @param userId      The id of the UserAccount.
     * @param attributeId The name of the attribute to update.
     * @param xml         The xml representation of the attribute.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object updateAttribute(final String userId, final String attributeId, final String xml) throws Exception {

        return callEsciDoc("UserAccount.updateAttribute", METHOD_UPDATE_ATTRIBUTE, Constants.HTTP_METHOD_PUT,
            Constants.USER_ACCOUNT_BASE_URI, new String[] { userId, "resources/attributes/attribute", attributeId },
            changeToString(xml));
    }

    /**
     * Delete user attribute.
     *
     * @param userId      The id of the UserAccount.
     * @param attributeId The id of the attribute to delete.
     * @param attributeId The xml representation of the attribute.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object deleteAttribute(final String userId, final String attributeId) throws Exception {

        return callEsciDoc("UserAccount.deleteAttribute", METHOD_DELETE_ATTRIBUTE, Constants.HTTP_METHOD_DELETE,
            Constants.USER_ACCOUNT_BASE_URI, new String[] { userId, "resources/attributes/attribute", attributeId });
    }

}
