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

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidStatusException;
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
 * Test suite for the role escidoc:role-context-modifier. Context-Modifier may -retrieve, modify, delete, open, close
 * context granted for. -grant other context-scoped roles to the context (s)he has privileges for (except Context
 * Modifier role).
 * <p/>
 * This role is a limited role. It is restricted to a context.
 *
 * @author Michael Hoppe
 */
@RunWith(Parameterized.class)
public class ContextModifierIT extends GrantTestBase {

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

    private static String contextId = null;

    public ContextModifierIT(final int handlerCode, final String userOrGroupId) throws Exception {
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
     * prepare tests (create context).
     *
     * @throws Exception If anything fails.
     */
    public void prepare() throws Exception {
        String contextXml = doTestCreateContext(PWCallback.DEFAULT_HANDLE, "context_create.xml", null);
        contextId = getObjidValue(contextXml);
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + contextId,
            ROLE_HREF_CONTEXT_MODIFIER, null);

    }

    /**
     * Tests successfully retrieving a context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testContextLifecycle() throws Exception {
        String contextXml = handleResult(contextClient.retrieve(contextId));
        contextXml = contextXml.replaceAll("(<[^\\/>]*?)name>", "$1name>replaced");

        contextXml = handleResult(contextClient.update(contextId, contextXml));

        String lastModificationDate = getLastModificationDateValue(EscidocAbstractTest.getDocument(contextXml));
        contextXml =
            handleResult(contextClient.open(contextId, getTheLastModificationParam(true, contextId, "comment",
                lastModificationDate)));

        lastModificationDate = getLastModificationDateValue(EscidocAbstractTest.getDocument(contextXml));
        contextXml =
            handleResult(contextClient.close(contextId, getTheLastModificationParam(true, contextId, "comment",
                lastModificationDate)));

        try {
            contextClient.delete(contextId);
        }
        catch (final InvalidStatusException e) {
        }
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
     * Tests declining updating a context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineUpdateContext() throws Exception {
        doTestUpdateContext(PWCallback.DEFAULT_HANDLE, HANDLE, "context_create.xml", AuthorizationException.class);
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
     * Tests declining closing a context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineCloseContext() throws Exception {
        doTestCloseContext(PWCallback.DEFAULT_HANDLE, HANDLE, "context_create.xml", AuthorizationException.class);
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
     * Tests creation a grant on scoped context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateGrant() throws Exception {
        if (!isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        }
        try {
            doTestCreateGrant(HANDLE, TEST_USER_ACCOUNT_ID1, Constants.CONTEXT_BASE_URI + "/" + contextId,
                ROLE_HREF_ADMINISTRATOR, null);
            revokeAllGrants(TEST_USER_ACCOUNT_ID1);
        }
        finally {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
        }
        if (isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        }
        try {
            doTestCreateGrant(HANDLE, TEST_USER_GROUP_ID, Constants.CONTEXT_BASE_URI + "/" + contextId,
                ROLE_HREF_ADMINISTRATOR, null);
            revokeAllGrants(TEST_USER_GROUP_ID);
        }
        finally {
            if (isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests declining creating a grant on scoped context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineCreateGrant() throws Exception {
        if (!isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        }
        try {
            doTestCreateGrant(HANDLE, TEST_USER_ACCOUNT_ID1, Constants.CONTEXT_BASE_URI + "/" + contextId,
                ROLE_HREF_CONTEXT_MODIFIER, AuthorizationException.class);
            revokeAllGrants(TEST_USER_ACCOUNT_ID1);
        }
        finally {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
        }
        if (isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        }
        try {
            doTestCreateGrant(HANDLE, TEST_USER_GROUP_ID, Constants.CONTEXT_BASE_URI + "/" + contextId,
                ROLE_HREF_CONTEXT_MODIFIER, AuthorizationException.class);
            revokeAllGrants(TEST_USER_GROUP_ID);
        }
        finally {
            if (isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests declining creating a grant on scoped context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineCreateGrant1() throws Exception {
        if (!isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        }
        try {
            doTestCreateGrant(HANDLE, TEST_USER_ACCOUNT_ID1, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
                ROLE_HREF_ADMINISTRATOR, AuthorizationException.class);
            revokeAllGrants(TEST_USER_ACCOUNT_ID1);
        }
        finally {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
        }
        if (isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        }
        try {
            doTestCreateGrant(HANDLE, TEST_USER_GROUP_ID, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
                ROLE_HREF_ADMINISTRATOR, AuthorizationException.class);
            revokeAllGrants(TEST_USER_GROUP_ID);
        }
        finally {
            if (isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
        }
    }

}
