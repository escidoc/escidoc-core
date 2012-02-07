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

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.aa.UserAccountClient;
import de.escidoc.core.test.security.client.PWCallback;

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
        return handleXmlResult(client.createPreference(id, xml));
    }

    /**
     * Test creating a user preference.
     *
     * @param id       The id of the UserAccount.
     * @param userHandle       The userhandle.
     * @param expectedExceptionClass       expectedExceptionClass.
     * @throws Exception If anything fails.
     */
    protected void doTestCreatePreference(
        final String id, final String userHandle, final Class<?> expectedExceptionClass) throws Exception {
        String userId = getUserId(id);
        try {
            PWCallback.setHandle(userHandle);

            client.createPreference(userId, getCreatePreferenceTaskParam(Long.toString(System.currentTimeMillis()),
                "test"));
            if (expectedExceptionClass != null) {
                EscidocAbstractTest.failMissingException(expectedExceptionClass);
            }
        }
        catch (final Exception e) {
            if (expectedExceptionClass == null) {
                EscidocAbstractTest.failException(e);
            }
            else {
                EscidocAbstractTest.assertExceptionType(expectedExceptionClass, e);
            }
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
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
        client.deletePreference(id, name);
    }

    /**
     * Test deleting a user preference.
     *
     * @param id   The id of the UserAccount.
     * @param creatorHandle       The creatorhandle.
     * @param userHandle       The userhandle.
     * @param expectedExceptionClass       expectedExceptionClass.
     * @throws Exception If anything fails.
     */
    protected void doTestDeletePreference(
        final String id, final String creatorHandle, final String userHandle, final Class<?> expectedExceptionClass)
        throws Exception {
        String userId = getUserId(id);
        String prefName = Long.toString(System.currentTimeMillis());
        try {
            PWCallback.setHandle(creatorHandle);
            client.createPreference(userId, getCreatePreferenceTaskParam(prefName, "test"));
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }

        try {
            PWCallback.setHandle(userHandle);

            client.deletePreference(userId, prefName);
            if (expectedExceptionClass != null) {
                EscidocAbstractTest.failMissingException(expectedExceptionClass);
            }
        }
        catch (final Exception e) {
            if (expectedExceptionClass == null) {
                EscidocAbstractTest.failException(e);
            }
            else {
                EscidocAbstractTest.assertExceptionType(expectedExceptionClass, e);
            }
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
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
        return handleXmlResult(client.updatePreferences(id, xml));
    }

    /**
     * Test updating user preferences.
     *
     * @param id   The id of the UserAccount.
     * @param creatorHandle       The creatorhandle.
     * @param userHandle       The userhandle.
     * @param expectedExceptionClass       expectedExceptionClass.
     * @throws Exception If anything fails.
     */
    protected void doTestUpdatePreferences(
        final String id, final String creatorHandle, final String userHandle, final Class<?> expectedExceptionClass)
        throws Exception {
        String userId = getUserId(id);
        String lmd = null;
        String prefName = Long.toString(System.currentTimeMillis());
        try {
            PWCallback.setHandle(creatorHandle);
            client.createPreference(userId, getCreatePreferenceTaskParam(prefName, "test"));
            lmd = getLastModificationDateValue(getDocument(retrieve(userId)));
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }

        try {
            PWCallback.setHandle(userHandle);

            client.updatePreferences(userId, getUpdatePreferencesTaskParam(prefName, "test1", lmd));
            if (expectedExceptionClass != null) {
                EscidocAbstractTest.failMissingException(expectedExceptionClass);
            }
        }
        catch (final Exception e) {
            if (expectedExceptionClass == null) {
                EscidocAbstractTest.failException(e);
            }
            else {
                EscidocAbstractTest.assertExceptionType(expectedExceptionClass, e);
            }
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
    }

    /**
     * Test updating single user preference.
     *
     * @param id       The id of the UserAccount.
     * @return The xml representation of the updated preference.
     * @throws Exception If anything fails.
     */
    protected String updatePreference(final String id, final String name, final String xml) throws Exception {
        return handleXmlResult(client.updatePreference(id, name, xml));
    }

    /**
     * Test updating user preference.
     *
     * @param id   The id of the UserAccount.
     * @param creatorHandle       The creatorhandle.
     * @param userHandle       The userhandle.
     * @param expectedExceptionClass       expectedExceptionClass.
     * @throws Exception If anything fails.
     */
    protected void doTestUpdatePreference(
        final String id, final String creatorHandle, final String userHandle, final Class<?> expectedExceptionClass)
        throws Exception {
        String userId = getUserId(id);
        String lmd = null;
        String prefName = Long.toString(System.currentTimeMillis());
        try {
            PWCallback.setHandle(creatorHandle);
            client.createPreference(userId, getCreatePreferenceTaskParam(prefName, "test"));
            lmd = getLastModificationDateValue(getDocument(retrieve(userId)));
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }

        try {
            PWCallback.setHandle(userHandle);

            client.updatePreference(userId, prefName, getUpdatePreferenceTaskParam(prefName, "test1", lmd));
            if (expectedExceptionClass != null) {
                EscidocAbstractTest.failMissingException(expectedExceptionClass);
            }
        }
        catch (final Exception e) {
            if (expectedExceptionClass == null) {
                EscidocAbstractTest.failException(e);
            }
            else {
                EscidocAbstractTest.assertExceptionType(expectedExceptionClass, e);
            }
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
    }

    protected String retrievePreferences(final String id) throws Exception {

        return handleXmlResult(client.retrievePreferences(id));
    }

    /**
     * Test retrieving user preferences.
     *
     * @param id   The id of the UserAccount.
     * @param creatorHandle       The creatorhandle.
     * @param userHandle       The userhandle.
     * @param expectedExceptionClass       expectedExceptionClass.
     * @throws Exception If anything fails.
     */
    protected void doTestRetrievePreferences(
        final String id, final String creatorHandle, final String userHandle, final Class<?> expectedExceptionClass)
        throws Exception {
        String userId = getUserId(id);
        String prefName = Long.toString(System.currentTimeMillis());
        try {
            PWCallback.setHandle(creatorHandle);
            client.createPreference(userId, getCreatePreferenceTaskParam(prefName, "test"));
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }

        try {
            PWCallback.setHandle(userHandle);

            client.retrievePreferences(userId);
            if (expectedExceptionClass != null) {
                EscidocAbstractTest.failMissingException(expectedExceptionClass);
            }
        }
        catch (final Exception e) {
            if (expectedExceptionClass == null) {
                EscidocAbstractTest.failException(e);
            }
            else {
                EscidocAbstractTest.assertExceptionType(expectedExceptionClass, e);
            }
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
    }

    protected String retrievePreference(final String id, final String name) throws Exception {

        return handleXmlResult(client.retrievePreference(id, name));
    }

    /**
     * Test retrieving user preference.
     *
     * @param id   The id of the UserAccount.
     * @param creatorHandle       The creatorhandle.
     * @param userHandle       The userhandle.
     * @param expectedExceptionClass       expectedExceptionClass.
     * @throws Exception If anything fails.
     */
    protected void doTestRetrievePreference(
        final String id, final String creatorHandle, final String userHandle, final Class<?> expectedExceptionClass)
        throws Exception {
        String userId = getUserId(id);
        String prefName = Long.toString(System.currentTimeMillis());
        try {
            PWCallback.setHandle(creatorHandle);
            client.createPreference(userId, getCreatePreferenceTaskParam(prefName, "test"));
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }

        try {
            PWCallback.setHandle(userHandle);

            client.retrievePreference(userId, prefName);
            if (expectedExceptionClass != null) {
                EscidocAbstractTest.failMissingException(expectedExceptionClass);
            }
        }
        catch (final Exception e) {
            if (expectedExceptionClass == null) {
                EscidocAbstractTest.failException(e);
            }
            else {
                EscidocAbstractTest.assertExceptionType(expectedExceptionClass, e);
            }
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
    }

    /**
     * create preference task-param xml.
     *
     * @param prefName The name of the Preference.
     * @param prefValue The value of the Preference.
     * @return The xml representation of the preference task param.
     * @throws Exception If anything fails.
     */
    public String getCreatePreferenceTaskParam(final String prefName, final String prefValue) throws Exception {
        return "<preference xmlns=\"" + USER_ACCOUNT_PREFERENCE_NS_URI + "\"" + " name=\"" + prefName + "\">"
            + prefValue + "</preference>";
    }

    /**
     * update preference task-param xml.
     *
     * @param prefName The name of the Preference.
     * @param prefValue The value of the Preference.
     * @return The xml representation of the preference task param.
     * @throws Exception If anything fails.
     */
    public String getUpdatePreferenceTaskParam(final String prefName, final String prefValue, final String lmd)
        throws Exception {
        return "<preference xmlns=\"" + USER_ACCOUNT_PREFERENCE_NS_URI + "\" last-modification-date=\"" + lmd
            + "\" name=\"" + prefName + "\">" + prefValue + "</preference>";
    }

    /**
     * update preferences task-param xml.
     *
     * @param prefName The name of the Preference.
     * @param prefValue The value of the Preference.
     * @return The xml representation of the preferences task param.
     * @throws Exception If anything fails.
     */
    public String getUpdatePreferencesTaskParam(final String prefName, final String prefValue, final String lmd)
        throws Exception {
        return "<preferences xmlns=\"" + USER_ACCOUNT_PREFERENCE_NS_URI + "\" " + "last-modification-date=\"" + lmd
            + "\"><preference" + " name=\"" + prefName + "\">" + prefValue + "</preference></preferences>";
    }

}
