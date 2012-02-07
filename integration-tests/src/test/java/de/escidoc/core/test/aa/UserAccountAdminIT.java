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

import de.escidoc.core.common.exceptions.remote.application.security.AuthorizationException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.aa.GrantClient;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;

import java.util.Arrays;
import java.util.Collection;

/**
 * Test suite for the role user-account-administrator. user-account-administrator may -create user-account -retrieve,
 * update, activate, deactivate user accounts (s)he created
 *
 * @author Michael Hoppe
 */
@RunWith(Parameterized.class)
public class UserAccountAdminIT extends GrantTestBase {

    /**
     * Initializes test-class with data.
     *
     * @return Collection with data.
     */
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { USER_ACCOUNT_HANDLER_CODE, PWCallback.ID_PREFIX + PWCallback.TEST_HANDLE },
            { USER_GROUP_HANDLER_CODE, USER_GROUP_WITH_GROUP_LIST_ID },
            { USER_GROUP_HANDLER_CODE, USER_GROUP_WITH_USER_LIST_ID },
            { USER_GROUP_HANDLER_CODE, USER_GROUP_WITH_OU_LIST_ID },
            { USER_GROUP_HANDLER_CODE, USER_GROUP_WITH_EXTERNAL_SELECTOR } });
    }

    private static UserAttributeTestBase userAttributeTestBase = null;

    private static UserPreferenceTestBase userPreferenceTestBase = null;

    protected static final String HANDLE = PWCallback.TEST_HANDLE;

    protected static final String LOGINNAME = HANDLE;

    protected static final String PASSWORD = PWCallback.PASSWORD;

    protected static String grantCreationUserOrGroupId = null;

    private static UserAccountTestBase userAccountTestBase = null;

    private static int methodCounter = 0;

    private static String userId = null;

    private static String userId1 = null;

    /**
     * The constructor.
     *
     * @param handlerCode   handlerCode of either UserAccountHandler or UserGroupHandler.
     * @param userOrGroupId userOrGroupId for grantCreation.
     * @throws Exception If anything fails.
     */
    public UserAccountAdminIT(final int handlerCode, final String userOrGroupId) throws Exception {
        super(handlerCode);
        grantCreationUserOrGroupId = userOrGroupId;
        userAccountTestBase = new UserAccountTestBase() {
        };
        userAttributeTestBase = new UserAttributeTestBase() {
        };
        userPreferenceTestBase = new UserPreferenceTestBase() {
        };
    }

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void initialize() throws Exception {
        if (methodCounter == 0) {
            //revoke all Grants
            revokeAllGrants(grantCreationUserOrGroupId);
            //create grant user-account-admin for user grantCreationUserOrGroupId 
            doTestCreateGrant(null, grantCreationUserOrGroupId, null, ROLE_HREF_USER_ACCOUNT_ADMIN, null);
            prepare();
        }
    }

    /**
     * Clean up after servlet test.
     *
     * @throws Exception If anything fails.
     */
    @After
    public void deinitialize() throws Exception {
        methodCounter++;
        if (methodCounter == getTestAnnotationsCount()) {
            revokeAllGrants(grantCreationUserOrGroupId);
            methodCounter = 0;
        }
    }

    /**
     * prepare tests (create group).
     *
     * @throws Exception If anything fails.
     */
    public void prepare() throws Exception {
        //create user-account as user-account-admin
        String userXml = prepareUserAccount(HANDLE, STATUS_ACTIVE);
        Document userDocument = EscidocAbstractTest.getDocument(userXml);
        userId = getObjidValue(userDocument);

        //create user-group as systemadministrator
        userXml = prepareUserAccount(PWCallback.DEFAULT_HANDLE, STATUS_ACTIVE);
        userDocument = EscidocAbstractTest.getDocument(userXml);
        userId1 = getObjidValue(userDocument);
    }

    /**
     * Tests successfully retrieving a user-account.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveUserAccount() throws Exception {
        userAccountTestBase.doTestRetrieveUserAccount(HANDLE, userId, null);
    }

    /**
     * Tests successfully updating a user-account.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateUserAccount() throws Exception {
        userAccountTestBase.doTestUpdateUserAccount(HANDLE, userId, null);
    }

    /**
     * Tests declining updating a user-account that was created by someone else.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateUserAccountDecline() throws Exception {
        userAccountTestBase.doTestUpdateUserAccount(HANDLE, userId1, AuthorizationException.class);
    }

    /**
     * Tests successfully activating a user-account.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testActivateUserAccount() throws Exception {
        userAccountTestBase.doTestActivateUserAccount(HANDLE, userId, null);
    }

    /**
     * Tests declining activating a user-account that was created by someone else.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testActivateUserAccountDecline() throws Exception {
        userAccountTestBase.doTestActivateUserAccount(HANDLE, userId1, AuthorizationException.class);
    }

    /**
     * Tests successfully deactivating a user-account.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeactivateUserAccount() throws Exception {
        userAccountTestBase.doTestDeactivateUserAccount(HANDLE, userId, null);
    }

    /**
     * Tests declining deactivating a user-account that was created by someone else.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeactivateUserAccountDecline() throws Exception {
        userAccountTestBase.doTestDeactivateUserAccount(HANDLE, userId1, AuthorizationException.class);
    }

    /**
     * Tests successfully creating a grant for a user-group.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateGrant() throws Exception {
        if (!isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        }
        try {
            revokeAllGrants(userId);
            doTestCreateGrant(HANDLE, userId, null, ROLE_HREF_ADMINISTRATOR, null);
        }
        finally {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests successfully creating a grant for a user-group.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateGrantDecline() throws Exception {
        if (!isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        }
        try {
            revokeAllGrants(userId1);
            doTestCreateGrant(HANDLE, userId1, null, ROLE_HREF_ADMINISTRATOR, AuthorizationException.class);
        }
        finally {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests revoking a grant from a user-group.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRevokeGrant() throws Exception {
        if (!isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        }
        try {
            revokeAllGrants(userId);
            //first create a grant as sysadmin
            String grantXml = doTestCreateGrant(null, userId, null, ROLE_HREF_AUDIENCE, null);
            String grantId = getObjidValue(grantXml);

            //then try to revoke it
            doTestRevokeGrant(HANDLE, userId, grantId, null);
        }
        finally {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests declining revoking a grant from a user-group. user did not create the group.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRevokeGrantDecline() throws Exception {
        if (!isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        }
        try {
            revokeAllGrants(userId1);
            //first create a grant as sysadmin
            String grantXml = doTestCreateGrant(null, userId1, null, ROLE_HREF_AUDIENCE, null);
            String grantId = getObjidValue(grantXml);

            //then try to revoke it
            doTestRevokeGrant(HANDLE, userId1, grantId, AuthorizationException.class);
        }
        finally {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests retrieving a grant from a user-group.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveGrant() throws Exception {
        if (!isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        }
        try {
            revokeAllGrants(userId);
            //first create a grant as sysadmin
            String grantXml = doTestCreateGrant(null, userId, null, ROLE_HREF_AUDIENCE, null);
            String grantId = getObjidValue(grantXml);

            //then try to retrieve it
            doTestRetrieveGrant(HANDLE, userId, grantId, null);
        }
        finally {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests declining retrieving a grant from a user-group. user did not create the group.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveGrantDecline() throws Exception {
        if (!isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        }
        try {
            revokeAllGrants(userId1);
            //first create a grant as sysadmin
            String grantXml = doTestCreateGrant(null, userId1, null, ROLE_HREF_AUDIENCE, null);
            String grantId = getObjidValue(grantXml);

            //then try to retrieve it
            doTestRetrieveGrant(HANDLE, userId1, grantId, AuthorizationException.class);
        }
        finally {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests successfully creating a user-inspector grant for a user-group.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateUserInspectorGrant() throws Exception {
        if (isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        }
        try {
            revokeAllGrants(EscidocAbstractTest.TEST_USER_GROUP_ID);
            doTestCreateGrant(HANDLE, EscidocAbstractTest.TEST_USER_GROUP_ID, Constants.USER_ACCOUNT_BASE_URI + "/"
                + userId, ROLE_HREF_USER_ACCOUNT_INSPECTOR, null);
        }
        finally {
            if (isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests successfully creating a user-inspector grant for a user-account.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateUserInspectorGrant1() throws Exception {
        if (!isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        }
        try {
            revokeAllGrants(EscidocAbstractTest.TEST_USER_ACCOUNT_ID1);
            doTestCreateGrant(HANDLE, EscidocAbstractTest.TEST_USER_ACCOUNT_ID1, Constants.USER_ACCOUNT_BASE_URI + "/"
                + userId, ROLE_HREF_USER_ACCOUNT_INSPECTOR, null);
        }
        finally {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests declining creating a user-inspector grant for a user-group.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateUserInspectorGrantDecline() throws Exception {
        if (isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        }
        try {
            revokeAllGrants(EscidocAbstractTest.TEST_USER_GROUP_ID);
            doTestCreateGrant(HANDLE, EscidocAbstractTest.TEST_USER_GROUP_ID, Constants.USER_ACCOUNT_BASE_URI + "/"
                + userId1, ROLE_HREF_USER_ACCOUNT_INSPECTOR, AuthorizationException.class);
        }
        finally {
            if (isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests declining creating a user-inspector grant for a user-account.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateUserInspectorGrantDecline1() throws Exception {
        if (!isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        }
        try {
            revokeAllGrants(EscidocAbstractTest.TEST_USER_ACCOUNT_ID1);
            doTestCreateGrant(HANDLE, EscidocAbstractTest.TEST_USER_ACCOUNT_ID1, Constants.USER_ACCOUNT_BASE_URI + "/"
                + userId1, ROLE_HREF_USER_ACCOUNT_INSPECTOR, AuthorizationException.class);
        }
        finally {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests revoking a user-inspector grant from a user-group.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRevokeUserInspectorGrant() throws Exception {
        if (isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        }
        try {
            revokeAllGrants(EscidocAbstractTest.TEST_USER_GROUP_ID);
            //first create a grant as sysadmin
            String grantXml =
                doTestCreateGrant(null, EscidocAbstractTest.TEST_USER_GROUP_ID, Constants.USER_ACCOUNT_BASE_URI + "/"
                    + userId, ROLE_HREF_USER_ACCOUNT_INSPECTOR, null);
            String grantId = getObjidValue(grantXml);

            //then try to revoke it
            doTestRevokeGrant(HANDLE, EscidocAbstractTest.TEST_USER_GROUP_ID, grantId, null);
        }
        finally {
            if (isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests revoking a user-inspector grant from a user-account.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRevokeUserInspectorGrant1() throws Exception {
        if (!isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        }
        try {
            revokeAllGrants(EscidocAbstractTest.TEST_USER_ACCOUNT_ID1);
            //first create a grant as sysadmin
            String grantXml =
                doTestCreateGrant(null, EscidocAbstractTest.TEST_USER_ACCOUNT_ID1, Constants.USER_ACCOUNT_BASE_URI
                    + "/" + userId, ROLE_HREF_USER_ACCOUNT_INSPECTOR, null);
            String grantId = getObjidValue(grantXml);

            //then try to revoke it
            doTestRevokeGrant(HANDLE, EscidocAbstractTest.TEST_USER_ACCOUNT_ID1, grantId, null);
        }
        finally {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests declining revoking a user-inspector grant from a user-group. user did not create the group.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRevokeUserInspectorGrantDecline() throws Exception {
        if (isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        }
        try {
            revokeAllGrants(EscidocAbstractTest.TEST_USER_GROUP_ID);
            //first create a grant as sysadmin
            String grantXml =
                doTestCreateGrant(null, EscidocAbstractTest.TEST_USER_GROUP_ID, Constants.USER_ACCOUNT_BASE_URI + "/"
                    + userId1, ROLE_HREF_USER_ACCOUNT_INSPECTOR, null);
            String grantId = getObjidValue(grantXml);

            //then try to revoke it
            doTestRevokeGrant(HANDLE, EscidocAbstractTest.TEST_USER_GROUP_ID, grantId, AuthorizationException.class);
        }
        finally {
            if (isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests declining revoking a user-inspector grant from a user-account. user did not create the user-account.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRevokeUserInspectorGrantDecline1() throws Exception {
        if (!isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        }
        try {
            revokeAllGrants(EscidocAbstractTest.TEST_USER_ACCOUNT_ID1);
            //first create a grant as sysadmin
            String grantXml =
                doTestCreateGrant(null, EscidocAbstractTest.TEST_USER_ACCOUNT_ID1, Constants.USER_ACCOUNT_BASE_URI
                    + "/" + userId1, ROLE_HREF_USER_ACCOUNT_INSPECTOR, null);
            String grantId = getObjidValue(grantXml);

            //then try to revoke it
            doTestRevokeGrant(HANDLE, EscidocAbstractTest.TEST_USER_ACCOUNT_ID1, grantId, AuthorizationException.class);
        }
        finally {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests retrieving a user-inspector grant from a user-group.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveUserInspectorGrant() throws Exception {
        if (isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        }
        try {
            revokeAllGrants(EscidocAbstractTest.TEST_USER_GROUP_ID);
            //first create a grant as sysadmin
            String grantXml =
                doTestCreateGrant(null, EscidocAbstractTest.TEST_USER_GROUP_ID, Constants.USER_ACCOUNT_BASE_URI + "/"
                    + userId, ROLE_HREF_USER_ACCOUNT_INSPECTOR, null);
            String grantId = getObjidValue(grantXml);

            //then try to retrieve it
            doTestRetrieveGrant(HANDLE, EscidocAbstractTest.TEST_USER_GROUP_ID, grantId, null);
        }
        finally {
            if (isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests retrieving a user-inspector grant from a user-account.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveUserInspectorGrant1() throws Exception {
        if (!isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        }
        try {
            revokeAllGrants(EscidocAbstractTest.TEST_USER_ACCOUNT_ID1);
            //first create a grant as sysadmin
            String grantXml =
                doTestCreateGrant(null, EscidocAbstractTest.TEST_USER_ACCOUNT_ID1, Constants.USER_ACCOUNT_BASE_URI
                    + "/" + userId, ROLE_HREF_USER_ACCOUNT_INSPECTOR, null);
            String grantId = getObjidValue(grantXml);

            //then try to retrieve it
            doTestRetrieveGrant(HANDLE, EscidocAbstractTest.TEST_USER_ACCOUNT_ID1, grantId, null);
        }
        finally {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests declining retrieving a user-inspector grant from a user-group. user did not create the group.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveUserInspectorGrantDecline() throws Exception {
        if (isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        }
        try {
            revokeAllGrants(EscidocAbstractTest.TEST_USER_GROUP_ID);
            //first create a grant as sysadmin
            String grantXml =
                doTestCreateGrant(null, EscidocAbstractTest.TEST_USER_GROUP_ID, Constants.USER_ACCOUNT_BASE_URI + "/"
                    + userId1, ROLE_HREF_USER_ACCOUNT_INSPECTOR, null);
            String grantId = getObjidValue(grantXml);

            //then try to retrieve it
            doTestRetrieveGrant(HANDLE, EscidocAbstractTest.TEST_USER_GROUP_ID, grantId, AuthorizationException.class);
        }
        finally {
            if (isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests declining retrieving a user-inspector grant from a user-account. user did not create the group.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveUserInspectorGrantDecline1() throws Exception {
        if (!isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        }
        try {
            revokeAllGrants(EscidocAbstractTest.TEST_USER_ACCOUNT_ID1);
            //first create a grant as sysadmin
            String grantXml =
                doTestCreateGrant(null, EscidocAbstractTest.TEST_USER_ACCOUNT_ID1, Constants.USER_ACCOUNT_BASE_URI
                    + "/" + userId1, ROLE_HREF_USER_ACCOUNT_INSPECTOR, null);
            String grantId = getObjidValue(grantXml);

            //then try to retrieve it
            doTestRetrieveGrant(HANDLE, EscidocAbstractTest.TEST_USER_ACCOUNT_ID1, grantId,
                AuthorizationException.class);
        }
        finally {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
        }
    }

    /**
     * Test creating a user-acccount preference for user created by user-account-admin.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateUserAccountPreference() throws Exception {

        userPreferenceTestBase.doTestCreatePreference(userId, HANDLE, null);
    }

    /**
     * Test creating a user-acccount preference.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineCreateUserAccountPreference1() throws Exception {

        userPreferenceTestBase.doTestCreatePreference(null, HANDLE, AuthorizationException.class);
    }

    /**
     * Test deleting a user-acccount preference for user created by user-account-admin.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeleteUserAccountPreference() throws Exception {
        userPreferenceTestBase.doTestDeletePreference(userId, PWCallback.DEFAULT_HANDLE, HANDLE, null);
    }

    /**
     * Test deleting a user-acccount preference.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineDeleteUserAccountPreference1() throws Exception {
        userPreferenceTestBase.doTestDeletePreference(null, PWCallback.DEFAULT_HANDLE, HANDLE,
            AuthorizationException.class);
    }

    /**
     * Test updating a user-acccount preference for user created by user-account-admin.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateUserAccountPreference() throws Exception {
        userPreferenceTestBase.doTestUpdatePreference(userId, PWCallback.DEFAULT_HANDLE, HANDLE, null);
    }

    /**
     * Test updating a user-acccount preference.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineUpdateUserAccountPreference1() throws Exception {
        userPreferenceTestBase.doTestUpdatePreference(null, PWCallback.DEFAULT_HANDLE, HANDLE,
            AuthorizationException.class);
    }

    /**
     * Test updating user-acccount preferences for user created by user-account-admin.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateUserAccountPreferences() throws Exception {
        userPreferenceTestBase.doTestUpdatePreferences(userId, PWCallback.DEFAULT_HANDLE, HANDLE, null);
    }

    /**
     * Test updating user-acccount preferences.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineUpdateUserAccountPreferences1() throws Exception {
        userPreferenceTestBase.doTestUpdatePreferences(null, PWCallback.DEFAULT_HANDLE, HANDLE,
            AuthorizationException.class);
    }

    /**
     * Test retrieving user-acccount preference for user created by user-account-admin.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveUserAccountPreference() throws Exception {
        userPreferenceTestBase.doTestRetrievePreference(userId, PWCallback.DEFAULT_HANDLE, HANDLE, null);
    }

    /**
     * Test retrieving user-acccount preference.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineRetrieveUserAccountPreference1() throws Exception {
        userPreferenceTestBase.doTestRetrievePreference(null, PWCallback.DEFAULT_HANDLE, HANDLE,
            AuthorizationException.class);
    }

    /**
     * Test retrieving user-acccount preferences for user created by user-account-admin.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveUserAccountPreferences() throws Exception {
        userPreferenceTestBase.doTestRetrievePreferences(userId, PWCallback.DEFAULT_HANDLE, HANDLE, null);
    }

    /**
     * Test retrieving user-acccount preferences.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineRetrieveUserAccountPreferences1() throws Exception {
        userPreferenceTestBase.doTestRetrievePreferences(null, PWCallback.DEFAULT_HANDLE, HANDLE,
            AuthorizationException.class);
    }

    /**
     * Test creating a user-acccount attribute for user created by user-account-admin.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateUserAccountAttribute() throws Exception {
        userAttributeTestBase.doTestCreateAttribute(userId, HANDLE, null);
    }

    /**
     * Test creating a user-acccount attribute.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineCreateUserAccountAttribute1() throws Exception {
        userAttributeTestBase.doTestCreateAttribute(null, HANDLE, AuthorizationException.class);
    }

    /**
     * Test deleting a user-acccount attribute for user created by user-account-admin.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeleteUserAccountAttribute() throws Exception {
        userAttributeTestBase.doTestDeleteAttribute(userId, PWCallback.DEFAULT_HANDLE, HANDLE, null);
    }

    /**
     * Test deleting a user-acccount attribute.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineDeleteUserAccountAttribute1() throws Exception {
        userAttributeTestBase.doTestDeleteAttribute(null, PWCallback.DEFAULT_HANDLE, HANDLE,
            AuthorizationException.class);
    }

    /**
     * Test updating a user-acccount attribute for user created by user-account-admin.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateUserAccountAttribute() throws Exception {
        userAttributeTestBase.doTestUpdateAttribute(userId, PWCallback.DEFAULT_HANDLE, HANDLE, null);
    }

    /**
     * Test updating a user-acccount attribute.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineUpdateUserAccountAttribute1() throws Exception {
        userAttributeTestBase.doTestUpdateAttribute(null, PWCallback.DEFAULT_HANDLE, HANDLE,
            AuthorizationException.class);
    }

    /**
     * Test retrieving user-acccount attribute for user created by user-account-admin.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveUserAccountAttribute() throws Exception {
        userAttributeTestBase.doTestRetrieveAttribute(userId, PWCallback.DEFAULT_HANDLE, HANDLE, null);
    }

    /**
     * Test retrieving user-acccount attribute.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineRetrieveUserAccountAttribute1() throws Exception {
        userAttributeTestBase.doTestRetrieveAttribute(null, PWCallback.DEFAULT_HANDLE, HANDLE,
            AuthorizationException.class);
    }

    /**
     * Test retrieving user-acccount attributes for user created by user-account-admin.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveUserAccountAttributes() throws Exception {
        userAttributeTestBase.doTestRetrieveAttributes(userId, PWCallback.DEFAULT_HANDLE, HANDLE, null);
    }

    /**
     * Test retrieving user-acccount attributes.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineRetrieveUserAccountAttributes1() throws Exception {
        userAttributeTestBase.doTestRetrieveAttributes(null, PWCallback.DEFAULT_HANDLE, HANDLE,
            AuthorizationException.class);
    }

    /**
     * Test retrieving named user-acccount attributes for user created by user-account-admin.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveNamedUserAccountAttributes() throws Exception {
        userAttributeTestBase.doTestRetrieveNamedAttributes(userId, PWCallback.DEFAULT_HANDLE, HANDLE, null);
    }

    /**
     * Test retrieving named user-acccount attributes.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineRetrieveNamedUserAccountAttributes1() throws Exception {
        userAttributeTestBase.doTestRetrieveNamedAttributes(null, PWCallback.DEFAULT_HANDLE, HANDLE,
            AuthorizationException.class);
    }

    /**
     * Test updating user-acccount password for user created by user-account-admin.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateUserAccountPassword() throws Exception {
        doTestUpdateUserAccountPassword(userId, HANDLE, null);
    }

    /**
     * Test updating user-acccount password.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineUpdateUserAccountPassword1() throws Exception {
        doTestUpdateUserAccountPassword(null, HANDLE, AuthorizationException.class);
    }

}
