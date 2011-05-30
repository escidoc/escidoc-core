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
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Test suite for the role user-group-inspector.
 *
 * @author Michael Hoppe
 */
@RunWith(Parameterized.class)
public class UserGroupInspectorIT extends GrantTestBase {

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

    private static UserGroupTestBase userGroupTestBase = null;

    private static int methodCounter = 0;

    private static String groupId = null;

    private static String groupId1 = null;

    /**
     * The constructor.
     *
     * @param handlerCode   handlerCode of either UserAccountHandler or UserGroupHandler.
     * @param userOrGroupId userOrGroupId for grantCreation.
     * @throws Exception If anything fails.
     */
    public UserGroupInspectorIT(final int handlerCode, final String userOrGroupId) throws Exception {
        super(handlerCode);
        grantCreationUserOrGroupId = userOrGroupId;
        userGroupTestBase = new UserGroupTestBase() {
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
        String groupXml = prepareUserGroup(PWCallback.DEFAULT_HANDLE);
        Document groupDocument = EscidocAbstractTest.getDocument(groupXml);
        groupId = getObjidValue(groupDocument);
        String lastModificationDate = getLastModificationDateValue(groupDocument);

        groupXml = prepareUserGroup(PWCallback.DEFAULT_HANDLE);
        groupDocument = EscidocAbstractTest.getDocument(groupXml);
        groupId1 = getObjidValue(groupDocument);

        //add group1 to group
        ArrayList<String[]> selectors = new ArrayList<String[]>();
        String[] selector = new String[3];
        selector[0] = "user-group";
        selector[1] = "internal";
        selector[2] = groupId1;

        selectors.add(selector);
        String taskParam = userGroupTestBase.getAddSelectorsTaskParam(selectors, lastModificationDate);
        userGroupTestBase.doTestAddSelectors(null, groupId, taskParam, null);

    }

    /**
     * Tests successfully retrieving a user-group.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveGroup() throws Exception {
        try {
            //grant role user-group-inspector to user with scope on group
            doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.USER_GROUP_BASE_URI + "/" + groupId,
                ROLE_HREF_USER_GROUP_INSPECTOR, null);

            userGroupTestBase.doTestRetrieve(HANDLE, groupId, null);
        }
        finally {
            revokeAllGrants(grantCreationUserOrGroupId);
        }
    }

    /**
     * Tests declining retrieving a user-group.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveGroupHierarchicalDecline() throws Exception {
        try {
            //grant role user-group-inspector to user with scope on group
            doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.USER_GROUP_BASE_URI + "/" + groupId,
                ROLE_HREF_USER_GROUP_INSPECTOR, null);

            userGroupTestBase.doTestRetrieve(HANDLE, groupId1, AuthorizationException.class);
        }
        finally {
            revokeAllGrants(grantCreationUserOrGroupId);
        }
    }

    /**
     * Tests successfully retrieving a user-group.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveGroup1() throws Exception {
        try {
            //grant role user-group-inspector to user with scope on group
            doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.USER_GROUP_BASE_URI + "/" + groupId1,
                ROLE_HREF_USER_GROUP_INSPECTOR, null);

            userGroupTestBase.doTestRetrieve(HANDLE, groupId1, null);
        }
        finally {
            revokeAllGrants(grantCreationUserOrGroupId);
        }
    }

    /**
     * Tests declining retrieving a user-group.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveGroupHierarchicalDecline1() throws Exception {
        try {
            //grant role user-group-inspector to user with scope on group
            doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.USER_GROUP_BASE_URI + "/" + groupId1,
                ROLE_HREF_USER_GROUP_INSPECTOR, null);

            userGroupTestBase.doTestRetrieve(HANDLE, groupId, AuthorizationException.class);
        }
        finally {
            revokeAllGrants(grantCreationUserOrGroupId);
        }
    }

    /**
     * Tests declining retrieving a user-group.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveGroupDecline() throws Exception {
        try {
            //grant role user-group-inspector to user with scope on group
            doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.USER_GROUP_BASE_URI + "/" + groupId,
                ROLE_HREF_USER_GROUP_INSPECTOR, null);

            userGroupTestBase.doTestRetrieve(HANDLE, groupId1, AuthorizationException.class);
        }
        finally {
            revokeAllGrants(grantCreationUserOrGroupId);
        }
    }

    /**
     * Tests declining retrieving a user-group.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveGroupDecline1() throws Exception {
        try {
            //grant role user-group-inspector to user with scope on group
            doTestCreateGrant(null, grantCreationUserOrGroupId, null, ROLE_HREF_USER_GROUP_INSPECTOR, null);

            userGroupTestBase.doTestRetrieve(HANDLE, groupId1, AuthorizationException.class);
        }
        finally {
            revokeAllGrants(grantCreationUserOrGroupId);
        }
    }

}
