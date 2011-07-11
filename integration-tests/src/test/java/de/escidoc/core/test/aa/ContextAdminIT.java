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

import java.util.Arrays;
import java.util.Collection;

/**
 * Test suite for the role escidoc:role-context-administrator. Context-Administrator may -create contexts -retrieve,
 * modify, delete, open, close contexts (s)he created.
 * <p/>
 * This role is a unlimited role. (Has no scope-definitions).
 *
 * @author Michael Hoppe
 */
@RunWith(Parameterized.class)
public class ContextAdminIT extends GrantTestBase {

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

    private static int methodCounter = 0;

    /**
     * The constructor.
     *
     * @param handlerCode   handlerCode of either UserAccountHandler or UserGroupHandler.
     * @param userOrGroupId userOrGroupId for grantCreation.
     * @throws Exception If anything fails.
     */
    public ContextAdminIT(final int handlerCode, final String userOrGroupId) throws Exception {
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
            //create grant context-admin for user grantCreationUserOrGroupId 
            doTestCreateGrant(null, grantCreationUserOrGroupId, null, ROLE_HREF_CONTEXT_ADMIN, null);
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
     * Tests successfully creating a context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateContext() throws Exception {
        doTestCreateContext(HANDLE, "context_create.xml", null);
    }

    /**
     * Tests successfully retrieving a context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveContext() throws Exception {
        doTestRetrieveContext(HANDLE, HANDLE, "context_create.xml", null);
    }

    /**
     * Tests declining retrieving a context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineRetrieveContext() throws Exception {
        doTestRetrieveContext(PWCallback.DEFAULT_HANDLE, HANDLE, "context_create.xml", AuthorizationException.class);
    }

    /**
     * Tests successfully updating a context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateContext() throws Exception {
        doTestUpdateContext(HANDLE, HANDLE, "context_create.xml", null);
    }

    /**
     * Tests declining updating a context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineUpdateContext() throws Exception {
        doTestUpdateContext(PWCallback.DEFAULT_HANDLE, HANDLE, "context_create.xml", AuthorizationException.class);
    }

    /**
     * Tests successfully opening a context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOpenContext() throws Exception {
        doTestOpenContext(HANDLE, HANDLE, "context_create.xml", null);
    }

    /**
     * Tests declining opening a context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineOpenContext() throws Exception {
        doTestOpenContext(PWCallback.DEFAULT_HANDLE, HANDLE, "context_create.xml", AuthorizationException.class);
    }

    /**
     * Tests successfully closing a context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCloseContext() throws Exception {
        doTestCloseContext(HANDLE, HANDLE, "context_create.xml", null);
    }

    /**
     * Tests declining closing a context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineCloseContext() throws Exception {
        doTestCloseContext(PWCallback.DEFAULT_HANDLE, HANDLE, "context_create.xml", AuthorizationException.class);
    }

    /**
     * Tests successfully deleting a context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeleteContext() throws Exception {
        doTestDeleteContext(HANDLE, HANDLE, "context_create.xml", null);
    }

    /**
     * Tests declining deleting a context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineDeleteContext() throws Exception {
        doTestDeleteContext(PWCallback.DEFAULT_HANDLE, HANDLE, "context_create.xml", AuthorizationException.class);
    }

    /**
     * Tests granting a role to a user-account with scope of created context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testGrantRoleForContext() throws Exception {
        if (!isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        }
        try {
            revokeAllGrants(EscidocAbstractTest.TEST_USER_ACCOUNT_ID1);
            String contextXml = doTestCreateContext(HANDLE, "context_create.xml", null);
            String contextId = getObjidValue(contextXml);
            doTestCreateGrant(HANDLE, EscidocAbstractTest.TEST_USER_ACCOUNT_ID1, Constants.CONTEXT_BASE_URI + "/"
                + contextId, ROLE_HREF_MODERATOR, null);
        }
        finally {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests declining granting a role to a user-account with scope of other context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineGrantRoleForContext() throws Exception {
        if (!isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        }
        try {
            revokeAllGrants(EscidocAbstractTest.TEST_USER_ACCOUNT_ID1);
            doTestCreateGrant(HANDLE, EscidocAbstractTest.TEST_USER_ACCOUNT_ID1, Constants.CONTEXT_BASE_URI + "/"
                + EscidocAbstractTest.CONTEXT_ID, ROLE_HREF_MODERATOR, AuthorizationException.class);
        }
        finally {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests granting a role to a user-group with scope of created context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testGrantGroupRoleForContext() throws Exception {
        if (isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        }
        try {
            revokeAllGrants(EscidocAbstractTest.TEST_USER_GROUP_ID);
            String contextXml = doTestCreateContext(HANDLE, "context_create.xml", null);
            String contextId = getObjidValue(contextXml);
            doTestCreateGrant(HANDLE, EscidocAbstractTest.TEST_USER_GROUP_ID, Constants.CONTEXT_BASE_URI + "/"
                + contextId, ROLE_HREF_MODERATOR, null);
        }
        finally {
            if (isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests declining granting a role to a user-group with scope of other context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineGrantGroupRoleForContext() throws Exception {
        if (isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        }
        try {
            revokeAllGrants(EscidocAbstractTest.TEST_USER_GROUP_ID);
            doTestCreateGrant(HANDLE, EscidocAbstractTest.TEST_USER_GROUP_ID, Constants.CONTEXT_BASE_URI + "/"
                + EscidocAbstractTest.CONTEXT_ID, ROLE_HREF_MODERATOR, AuthorizationException.class);
        }
        finally {
            if (isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
        }
    }

}
