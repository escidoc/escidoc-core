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
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.aa.GrantClient;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Test suite for the role ou-administrator. ou-administrator may -create, retrieve, delete, update, open, close
 * organizational-units, if parent is in scope.
 * <p/>
 * -grant Organizational Unit Administrator privileges for organizational-units in scope.
 * <p/>
 * This role is restricted to an organizational-unit and implicitly to all its children.
 *
 * @author Michael Hoppe
 */
@RunWith(Parameterized.class)
public class OrgUnitAdminIT extends GrantTestBase {

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

    protected static final String HANDLE = PWCallback.TEST_HANDLE;

    protected static final String LOGINNAME = HANDLE;

    protected static final String PASSWORD = PWCallback.PASSWORD;

    protected static String grantCreationUserOrGroupId = null;

    private static String topLevelOrgUnit = null;

    private static String topLevelOrgUnit2 = null;

    private static String[] middleLevelOrgUnits = null;

    private static String lowLevelOrgUnit = null;

    private static String superLowLevelOrgUnit = null;

    private static String createdOrgUnit = null;

    private static int methodCounter = 0;

    /**
     * The constructor.
     *
     * @param handlerCode   handlerCode of either UserAccountHandler or UserGroupHandler.
     * @param userOrGroupId userOrGroupId for grantCreation.
     * @throws Exception If anything fails.
     */
    public OrgUnitAdminIT(final int handlerCode, final String userOrGroupId) throws Exception {
        super(handlerCode);
        grantCreationUserOrGroupId = userOrGroupId;
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
            setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            revokeAllGrants(TEST_USER_ACCOUNT_ID1);
            setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            revokeAllGrants(TEST_USER_GROUP_ID);
            setClient((GrantClient) getClient(handlerCode));
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
        setClient((GrantClient) getClient(handlerCode));
        if (methodCounter == getTestAnnotationsCount()) {
            methodCounter = 0;
            testOpenCloseDeleteOrgUnit();
            revokeAllGrants(grantCreationUserOrGroupId);
        }
    }

    /**
     * prepare tests (create group).
     *
     * @throws Exception If anything fails.
     */
    public void prepare() throws Exception {
        //create top-level org unit
        String ouXml = prepareOrgUnit(PWCallback.DEFAULT_HANDLE, STATUS_OPEN, null);
        topLevelOrgUnit = getObjidValue(ouXml);

        //create top-level org unit 2
        ouXml = prepareOrgUnit(PWCallback.DEFAULT_HANDLE, STATUS_NEW, null);
        topLevelOrgUnit2 = getObjidValue(ouXml);

        //create middle level org units
        middleLevelOrgUnits = new String[2];
        ouXml = prepareOrgUnit(PWCallback.DEFAULT_HANDLE, STATUS_NEW, new String[] { topLevelOrgUnit });
        middleLevelOrgUnits[0] = getObjidValue(ouXml);
        ouXml = prepareOrgUnit(PWCallback.DEFAULT_HANDLE, STATUS_OPEN, new String[] { topLevelOrgUnit });
        middleLevelOrgUnits[1] = getObjidValue(ouXml);

        //create low level org unit
        ouXml = prepareOrgUnit(PWCallback.DEFAULT_HANDLE, STATUS_NEW, middleLevelOrgUnits);
        lowLevelOrgUnit = getObjidValue(ouXml);

        //create super low level org unit
        ouXml = prepareOrgUnit(PWCallback.DEFAULT_HANDLE, STATUS_NEW, new String[] { lowLevelOrgUnit });
        superLowLevelOrgUnit = getObjidValue(ouXml);

        //create grant for middleLevelOrgUnits[0]
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/"
            + middleLevelOrgUnits[0], ROLE_HREF_OU_ADMINISTRATOR, null);
    }

    /**
     * Tests successfully creating an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateOrgUnit() throws Exception {
        String ouXml = orgUnitHelper.doTestCreate(HANDLE, new String[] { lowLevelOrgUnit }, null);
        createdOrgUnit = getObjidValue(ouXml);
    }

    /**
     * Tests successfully creating an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateOrgUnit1() throws Exception {
        String ouXml = orgUnitHelper.doTestCreate(HANDLE, new String[] { middleLevelOrgUnits[0] }, null);
        orgUnitHelper.doTestDelete(HANDLE, getObjidValue(ouXml), null);
    }

    /**
     * Tests successfully creating an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateOrgUnit2() throws Exception {
        String ouXml = orgUnitHelper.doTestCreate(HANDLE, middleLevelOrgUnits, null);
        orgUnitHelper.doTestDelete(HANDLE, getObjidValue(ouXml), null);
    }

    /**
     * Tests successfully creating an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateOrgUnit3() throws Exception {
        String ouXml = orgUnitHelper.doTestCreate(HANDLE, new String[] { superLowLevelOrgUnit }, null);
        orgUnitHelper.doTestDelete(HANDLE, getObjidValue(ouXml), null);
    }

    /**
     * Tests declining creating an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDecliningCreateOrgUnit() throws Exception {
        orgUnitHelper.doTestCreate(HANDLE, new String[] { middleLevelOrgUnits[1] }, AuthorizationException.class);
    }

    /**
     * Tests declining creating an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDecliningCreateOrgUnit1() throws Exception {
        orgUnitHelper.doTestCreate(HANDLE, new String[] { topLevelOrgUnit }, AuthorizationException.class);
    }

    /**
     * Tests declining creating an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDecliningCreateOrgUnit2() throws Exception {
        orgUnitHelper.doTestCreate(HANDLE, new String[] {}, AuthorizationException.class);
    }

    /**
     * Tests retrieving an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveOrgUnit() throws Exception {
        orgUnitHelper.doTestRetrieve(HANDLE, lowLevelOrgUnit, null);
    }

    /**
     * Tests retrieving an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveOrgUnit1() throws Exception {
        orgUnitHelper.doTestRetrieve(HANDLE, superLowLevelOrgUnit, null);
    }

    /**
     * Tests retrieving an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveOrgUnit2() throws Exception {
        orgUnitHelper.doTestRetrieve(HANDLE, middleLevelOrgUnits[0], null);
    }

    /**
     * Tests retrieving an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveOrgUnit3() throws Exception {
        orgUnitHelper.doTestRetrieve(HANDLE, createdOrgUnit, null);
    }

    /**
     * Tests declining retrieving an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineRetrieveOrgUnit() throws Exception {
        String ouXml = prepareOrgUnit(PWCallback.DEFAULT_HANDLE, STATUS_NEW, new String[] { topLevelOrgUnit });
        orgUnitHelper.doTestRetrieve(HANDLE, getObjidValue(ouXml), AuthorizationException.class);
        orgUnitHelper.doTestDelete(PWCallback.DEFAULT_HANDLE, getObjidValue(ouXml), null);
    }

    /**
     * Tests declining retrieving an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineRetrieveOrgUnit1() throws Exception {
        String ouXml = prepareOrgUnit(PWCallback.DEFAULT_HANDLE, STATUS_NEW, new String[] { middleLevelOrgUnits[1] });
        orgUnitHelper.doTestRetrieve(HANDLE, getObjidValue(ouXml), AuthorizationException.class);
        orgUnitHelper.doTestDelete(PWCallback.DEFAULT_HANDLE, getObjidValue(ouXml), null);
    }

    /**
     * Tests updating an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateOrgUnit() throws Exception {
        orgUnitHelper.doTestUpdate(HANDLE, createdOrgUnit, null);
    }

    /**
     * Tests updating an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateOrgUnit1() throws Exception {
        orgUnitHelper.doTestUpdate(HANDLE, superLowLevelOrgUnit, null);
    }

    /**
     * Tests updating an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateOrgUnit2() throws Exception {
        orgUnitHelper.doTestUpdate(HANDLE, lowLevelOrgUnit, null);
    }

    /**
     * Tests updating an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateOrgUnit3() throws Exception {
        orgUnitHelper.doTestUpdate(HANDLE, middleLevelOrgUnits[0], null);
    }

    /**
     * Tests declining updating an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineUpdateOrgUnit() throws Exception {
        orgUnitHelper.doTestUpdate(HANDLE, middleLevelOrgUnits[1], AuthorizationException.class);
    }

    /**
     * Tests declining updating an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineUpdateOrgUnit1() throws Exception {
        orgUnitHelper.doTestUpdate(HANDLE, topLevelOrgUnit, AuthorizationException.class);
    }

    /**
     * Tests updating parents of an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateOrgUnitParents() throws Exception {
        String ouXml = prepareOrgUnit(PWCallback.DEFAULT_HANDLE, STATUS_NEW, new String[] { middleLevelOrgUnits[0] });
        orgUnitHelper.doTestUpdateWithChangedParents(HANDLE, getObjidValue(ouXml),
            new String[] { superLowLevelOrgUnit }, null);
        orgUnitHelper.doTestDelete(HANDLE, getObjidValue(ouXml), null);
    }

    /**
     * Tests updating parents of an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateOrgUnitParents1() throws Exception {
        String ouXml = prepareOrgUnit(PWCallback.DEFAULT_HANDLE, STATUS_NEW, new String[] { lowLevelOrgUnit });
        orgUnitHelper.doTestUpdateWithChangedParents(HANDLE, getObjidValue(ouXml), new String[] { lowLevelOrgUnit },
            null);
        orgUnitHelper.doTestDelete(HANDLE, getObjidValue(ouXml), null);
    }

    /**
     * Tests updating parents of an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateOrgUnitParents2() throws Exception {
        String ouXml = prepareOrgUnit(PWCallback.DEFAULT_HANDLE, STATUS_NEW, new String[] { superLowLevelOrgUnit });
        orgUnitHelper.doTestUpdateWithChangedParents(HANDLE, getObjidValue(ouXml),
            new String[] { middleLevelOrgUnits[0] }, null);
        orgUnitHelper.doTestDelete(HANDLE, getObjidValue(ouXml), null);
    }

    /**
     * Tests updating parents of an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateOrgUnitParents3() throws Exception {
        String ouXml = prepareOrgUnit(PWCallback.DEFAULT_HANDLE, STATUS_NEW, new String[] { middleLevelOrgUnits[0] });
        orgUnitHelper.doTestUpdateWithChangedParents(HANDLE, getObjidValue(ouXml),
            new String[] { superLowLevelOrgUnit }, null);
        orgUnitHelper.doTestDelete(HANDLE, getObjidValue(ouXml), null);
    }

    /**
     * Tests updating parents of an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateOrgUnitParents4() throws Exception {
        String ouXml = prepareOrgUnit(PWCallback.DEFAULT_HANDLE, STATUS_NEW, new String[] { lowLevelOrgUnit });
        orgUnitHelper.doTestUpdateWithChangedParents(HANDLE, getObjidValue(ouXml), new String[] {
            middleLevelOrgUnits[0], topLevelOrgUnit2 }, null);
        orgUnitHelper.doTestDelete(HANDLE, getObjidValue(ouXml), null);
    }

    /**
     * Tests updating parents of an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateOrgUnitParents5() throws Exception {
        String ouXml = prepareOrgUnit(PWCallback.DEFAULT_HANDLE, STATUS_NEW, new String[] { superLowLevelOrgUnit });
        orgUnitHelper.doTestUpdateWithChangedParents(HANDLE, getObjidValue(ouXml), new String[] { superLowLevelOrgUnit,
            middleLevelOrgUnits[1] }, null);
        orgUnitHelper.doTestDelete(HANDLE, getObjidValue(ouXml), null);
    }

    /**
     * Tests declining updating parents of an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineUpdateOrgUnitParents() throws Exception {
        String ouXml = prepareOrgUnit(PWCallback.DEFAULT_HANDLE, STATUS_NEW, new String[] { superLowLevelOrgUnit });
        orgUnitHelper.doTestUpdateWithChangedParents(HANDLE, getObjidValue(ouXml), new String[] { topLevelOrgUnit2 },
            AuthorizationException.class);
        orgUnitHelper.doTestDelete(PWCallback.DEFAULT_HANDLE, getObjidValue(ouXml), null);
    }

    /**
     * Tests declining updating parents of an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineUpdateOrgUnitParents1() throws Exception {
        String ouXml = prepareOrgUnit(PWCallback.DEFAULT_HANDLE, STATUS_NEW, new String[] { superLowLevelOrgUnit });
        orgUnitHelper.doTestUpdateWithChangedParents(HANDLE, getObjidValue(ouXml), new String[] { topLevelOrgUnit },
            AuthorizationException.class);
        orgUnitHelper.doTestDelete(PWCallback.DEFAULT_HANDLE, getObjidValue(ouXml), null);
    }

    /**
     * Tests declining updating parents of an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineUpdateOrgUnitParents2() throws Exception {
        String ouXml = prepareOrgUnit(PWCallback.DEFAULT_HANDLE, STATUS_NEW, new String[] { superLowLevelOrgUnit });
        orgUnitHelper.doTestUpdateWithChangedParents(HANDLE, getObjidValue(ouXml),
            new String[] { middleLevelOrgUnits[1] }, AuthorizationException.class);
        orgUnitHelper.doTestDelete(PWCallback.DEFAULT_HANDLE, getObjidValue(ouXml), null);
    }

    /**
     * Tests declining updating parents of an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineUpdateOrgUnitParents3() throws Exception {
        String ouXml = prepareOrgUnit(PWCallback.DEFAULT_HANDLE, STATUS_NEW, new String[] { superLowLevelOrgUnit });
        orgUnitHelper.doTestUpdateWithChangedParents(HANDLE, getObjidValue(ouXml), new String[] { topLevelOrgUnit,
            topLevelOrgUnit2 }, AuthorizationException.class);
        orgUnitHelper.doTestDelete(PWCallback.DEFAULT_HANDLE, getObjidValue(ouXml), null);
    }

    /**
     * Tests declining updating parents of an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineUpdateOrgUnitParents4() throws Exception {
        String ouXml = prepareOrgUnit(PWCallback.DEFAULT_HANDLE, STATUS_NEW, new String[] { topLevelOrgUnit });
        orgUnitHelper.doTestUpdateWithChangedParents(HANDLE, getObjidValue(ouXml), new String[] { topLevelOrgUnit,
            topLevelOrgUnit2 }, AuthorizationException.class);
        orgUnitHelper.doTestDelete(PWCallback.DEFAULT_HANDLE, getObjidValue(ouXml), null);
    }

    /**
     * Tests declining opening an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineOpenOrgUnit() throws Exception {
        orgUnitHelper.doTestOpen(HANDLE, middleLevelOrgUnits[1], AuthorizationException.class);
    }

    /**
     * Tests declining opening an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineOpenOrgUnit1() throws Exception {
        orgUnitHelper.doTestOpen(HANDLE, topLevelOrgUnit, AuthorizationException.class);
    }

    /**
     * Tests declining opening an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineOpenOrgUnit2() throws Exception {
        orgUnitHelper.doTestOpen(HANDLE, topLevelOrgUnit2, AuthorizationException.class);
    }

    /**
     * Tests declining closing an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineCloseOrgUnit() throws Exception {
        orgUnitHelper.doTestClose(HANDLE, middleLevelOrgUnits[1], AuthorizationException.class);
    }

    /**
     * Tests declining closing an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineCloseOrgUnit1() throws Exception {
        orgUnitHelper.doTestClose(HANDLE, topLevelOrgUnit, AuthorizationException.class);
    }

    /**
     * Tests declining closing an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineCloseOrgUnit2() throws Exception {
        orgUnitHelper.doTestClose(HANDLE, topLevelOrgUnit2, AuthorizationException.class);
    }

    /**
     * Tests declining deleting an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineDeleteOrgUnit() throws Exception {
        orgUnitHelper.doTestDelete(HANDLE, middleLevelOrgUnits[1], AuthorizationException.class);
    }

    /**
     * Tests declining deleting an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineDeleteOrgUnit1() throws Exception {
        orgUnitHelper.doTestDelete(HANDLE, topLevelOrgUnit, AuthorizationException.class);
    }

    /**
     * Tests declining deleting an org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineDeleteOrgUnit2() throws Exception {
        orgUnitHelper.doTestDelete(HANDLE, topLevelOrgUnit2, AuthorizationException.class);
    }

    /**
     * Tests creating user-account grant OU-Admin for ou.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateUserAccountGrant() throws Exception {
        setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        String grantXml =
            doTestCreateGrant(HANDLE, TEST_USER_ACCOUNT_ID1, Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/"
                + middleLevelOrgUnits[0], ROLE_HREF_OU_ADMINISTRATOR, null);
        doTestRevokeGrant(HANDLE, TEST_USER_ACCOUNT_ID1, getObjidValue(grantXml), null);
    }

    /**
     * Tests creating user-account grant OU-Admin for ou.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateUserAccountGrant1() throws Exception {
        setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        String grantXml =
            doTestCreateGrant(HANDLE, TEST_USER_ACCOUNT_ID1, Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/"
                + lowLevelOrgUnit, ROLE_HREF_OU_ADMINISTRATOR, null);
        doTestRevokeGrant(HANDLE, TEST_USER_ACCOUNT_ID1, getObjidValue(grantXml), null);
    }

    /**
     * Tests creating user-account grant OU-Admin for ou.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateUserAccountGrant2() throws Exception {
        setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        String grantXml =
            doTestCreateGrant(HANDLE, TEST_USER_ACCOUNT_ID1, Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/"
                + superLowLevelOrgUnit, ROLE_HREF_OU_ADMINISTRATOR, null);
        doTestRevokeGrant(HANDLE, TEST_USER_ACCOUNT_ID1, getObjidValue(grantXml), null);
    }

    /**
     * Tests revoking user-account grant OU-Admin for ou.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRevokeUserAccountGrant() throws Exception {
        setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        String grantXml =
            doTestCreateGrant(PWCallback.DEFAULT_HANDLE, TEST_USER_ACCOUNT_ID1, Constants.ORGANIZATIONAL_UNIT_BASE_URI
                + "/" + middleLevelOrgUnits[0], ROLE_HREF_OU_ADMINISTRATOR, null);
        doTestRevokeGrant(HANDLE, TEST_USER_ACCOUNT_ID1, getObjidValue(grantXml), null);
    }

    /**
     * Tests revoking user-account grant OU-Admin for ou.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRevokeUserAccountGrant1() throws Exception {
        setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        String grantXml =
            doTestCreateGrant(PWCallback.DEFAULT_HANDLE, TEST_USER_ACCOUNT_ID1, Constants.ORGANIZATIONAL_UNIT_BASE_URI
                + "/" + lowLevelOrgUnit, ROLE_HREF_OU_ADMINISTRATOR, null);
        doTestRevokeGrant(HANDLE, TEST_USER_ACCOUNT_ID1, getObjidValue(grantXml), null);
    }

    /**
     * Tests revoking user-account grant OU-Admin for ou.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRevokeUserAccountGrant2() throws Exception {
        setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        String grantXml =
            doTestCreateGrant(PWCallback.DEFAULT_HANDLE, TEST_USER_ACCOUNT_ID1, Constants.ORGANIZATIONAL_UNIT_BASE_URI
                + "/" + superLowLevelOrgUnit, ROLE_HREF_OU_ADMINISTRATOR, null);
        doTestRevokeGrant(HANDLE, TEST_USER_ACCOUNT_ID1, getObjidValue(grantXml), null);
    }

    /**
     * Tests declining creating user-account grant OU-Admin for ou.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineCreateUserAccountGrant() throws Exception {
        setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        doTestCreateGrant(HANDLE, TEST_USER_ACCOUNT_ID1,
            Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/" + topLevelOrgUnit, ROLE_HREF_OU_ADMINISTRATOR,
            AuthorizationException.class);
    }

    /**
     * Tests declining creating user-account grant OU-Admin for ou.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineCreateUserAccountGrant1() throws Exception {
        setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        doTestCreateGrant(HANDLE, TEST_USER_ACCOUNT_ID1, Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/"
            + middleLevelOrgUnits[1], ROLE_HREF_OU_ADMINISTRATOR, AuthorizationException.class);
    }

    /**
     * Tests declining creating user-account grant OU-Admin for ou.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineCreateUserAccountGrant2() throws Exception {
        setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        doTestCreateGrant(HANDLE, TEST_USER_ACCOUNT_ID1, Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/"
            + topLevelOrgUnit2, ROLE_HREF_OU_ADMINISTRATOR, AuthorizationException.class);
    }

    /**
     * Tests declining revoking user-account grant OU-Admin for ou.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineRevokeUserAccountGrant() throws Exception {
        setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        String grantXml =
            doTestCreateGrant(PWCallback.DEFAULT_HANDLE, TEST_USER_ACCOUNT_ID1, Constants.ORGANIZATIONAL_UNIT_BASE_URI
                + "/" + topLevelOrgUnit, ROLE_HREF_OU_ADMINISTRATOR, null);
        doTestRevokeGrant(HANDLE, TEST_USER_ACCOUNT_ID1, getObjidValue(grantXml), AuthorizationException.class);
    }

    /**
     * Tests creating user-group grant OU-Admin for ou.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateUserGroupGrant() throws Exception {
        setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        String grantXml =
            doTestCreateGrant(HANDLE, TEST_USER_GROUP_ID, Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/"
                + middleLevelOrgUnits[0], ROLE_HREF_OU_ADMINISTRATOR, null);
        doTestRevokeGrant(HANDLE, TEST_USER_GROUP_ID, getObjidValue(grantXml), null);
    }

    /**
     * Tests creating user-group grant OU-Admin for ou.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateUserGroupGrant1() throws Exception {
        setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        String grantXml =
            doTestCreateGrant(HANDLE, TEST_USER_GROUP_ID, Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/"
                + lowLevelOrgUnit, ROLE_HREF_OU_ADMINISTRATOR, null);
        doTestRevokeGrant(HANDLE, TEST_USER_GROUP_ID, getObjidValue(grantXml), null);
    }

    /**
     * Tests creating user-group grant OU-Admin for ou.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateUserGroupGrant2() throws Exception {
        setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        String grantXml =
            doTestCreateGrant(HANDLE, TEST_USER_GROUP_ID, Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/"
                + superLowLevelOrgUnit, ROLE_HREF_OU_ADMINISTRATOR, null);
        doTestRevokeGrant(HANDLE, TEST_USER_GROUP_ID, getObjidValue(grantXml), null);
    }

    /**
     * Tests revoking user-group grant OU-Admin for ou.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRevokeUserGroupGrant() throws Exception {
        setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        String grantXml =
            doTestCreateGrant(PWCallback.DEFAULT_HANDLE, TEST_USER_GROUP_ID, Constants.ORGANIZATIONAL_UNIT_BASE_URI
                + "/" + middleLevelOrgUnits[0], ROLE_HREF_OU_ADMINISTRATOR, null);
        doTestRevokeGrant(HANDLE, TEST_USER_GROUP_ID, getObjidValue(grantXml), null);
    }

    /**
     * Tests revoking user-group grant OU-Admin for ou.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRevokeUserGroupGrant1() throws Exception {
        setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        String grantXml =
            doTestCreateGrant(PWCallback.DEFAULT_HANDLE, TEST_USER_GROUP_ID, Constants.ORGANIZATIONAL_UNIT_BASE_URI
                + "/" + lowLevelOrgUnit, ROLE_HREF_OU_ADMINISTRATOR, null);
        doTestRevokeGrant(HANDLE, TEST_USER_GROUP_ID, getObjidValue(grantXml), null);
    }

    /**
     * Tests revoking user-group grant OU-Admin for ou.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRevokeUserGroupGrant2() throws Exception {
        setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        String grantXml =
            doTestCreateGrant(PWCallback.DEFAULT_HANDLE, TEST_USER_GROUP_ID, Constants.ORGANIZATIONAL_UNIT_BASE_URI
                + "/" + superLowLevelOrgUnit, ROLE_HREF_OU_ADMINISTRATOR, null);
        doTestRevokeGrant(HANDLE, TEST_USER_GROUP_ID, getObjidValue(grantXml), null);
    }

    /**
     * Tests declining creating user-group grant OU-Admin for ou.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineCreateUserGroupGrant() throws Exception {
        setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        doTestCreateGrant(HANDLE, TEST_USER_GROUP_ID, Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/" + topLevelOrgUnit,
            ROLE_HREF_OU_ADMINISTRATOR, AuthorizationException.class);
    }

    /**
     * Tests declining creating user-group grant OU-Admin for ou.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineCreateUserGroupGrant1() throws Exception {
        setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        doTestCreateGrant(HANDLE, TEST_USER_GROUP_ID, Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/"
            + middleLevelOrgUnits[1], ROLE_HREF_OU_ADMINISTRATOR, AuthorizationException.class);
    }

    /**
     * Tests declining creating user-group grant OU-Admin for ou.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineCreateUserGroupGrant2() throws Exception {
        setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        doTestCreateGrant(HANDLE, TEST_USER_GROUP_ID, Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/" + topLevelOrgUnit2,
            ROLE_HREF_OU_ADMINISTRATOR, AuthorizationException.class);
    }

    /**
     * Tests declining revoking user-group grant OU-Admin for ou.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineRevokeUserGroupGrant() throws Exception {
        setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        String grantXml =
            doTestCreateGrant(PWCallback.DEFAULT_HANDLE, TEST_USER_GROUP_ID, Constants.ORGANIZATIONAL_UNIT_BASE_URI
                + "/" + topLevelOrgUnit, ROLE_HREF_OU_ADMINISTRATOR, null);
        doTestRevokeGrant(HANDLE, TEST_USER_GROUP_ID, getObjidValue(grantXml), AuthorizationException.class);
    }

    /**
     * Tests opening, closing, deleting an org-unit.
     *
     * @throws Exception If anything fails.
     */
    public void testOpenCloseDeleteOrgUnit() throws Exception {
        orgUnitHelper.doTestDelete(HANDLE, createdOrgUnit, null);
        orgUnitHelper.doTestOpen(HANDLE, middleLevelOrgUnits[0], null);
        orgUnitHelper.doTestOpen(HANDLE, lowLevelOrgUnit, null);
        orgUnitHelper.doTestOpen(HANDLE, superLowLevelOrgUnit, null);
        orgUnitHelper.doTestClose(HANDLE, superLowLevelOrgUnit, null);
        orgUnitHelper.doTestClose(HANDLE, lowLevelOrgUnit, null);
        orgUnitHelper.doTestClose(HANDLE, middleLevelOrgUnits[0], null);
    }

}
