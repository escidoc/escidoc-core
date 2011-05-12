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
package de.escidoc.core.test.aa.rest;

import de.escidoc.core.common.exceptions.remote.application.security.AuthorizationException;
import de.escidoc.core.test.aa.PrivilegedViewerAbstractTest;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

/**
 * Test suite for the role PrivilegedViewer using the REST interface.
 *
 * @author Michael Hoppe
 */
@RunWith(Parameterized.class)
public class PrivilegedViewerRestTest extends PrivilegedViewerAbstractTest {

    /**
     * Initializes test-class with data.
     *
     * @return Collection with data.
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { USER_ACCOUNT_HANDLER_CODE, PWCallback.ID_PREFIX + PWCallback.TEST_HANDLE },
            { USER_GROUP_HANDLER_CODE, USER_GROUP_WITH_GROUP_LIST_ID },
            { USER_GROUP_HANDLER_CODE, USER_GROUP_WITH_USER_LIST_ID },
            { USER_GROUP_HANDLER_CODE, USER_GROUP_WITH_OU_LIST_ID },
            { USER_GROUP_HANDLER_CODE, USER_GROUP_WITH_EXTERNAL_SELECTOR } });
    }

    /**
     * Constructor.
     *
     * @param handlerCode   handlerCode of UserAccountHandler or UserGroupHandler
     * @param userOrGroupId userOrGroupId for grantCreation.
     * @throws Exception If anything fails.
     */
    public PrivilegedViewerRestTest(final int handlerCode, final String userOrGroupId) throws Exception {

        super(handlerCode, userOrGroupId);
    }

    // REST only tests

    /**
     * Tests successfully retrieving a private component of an item in status pending by an privileged viewer.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrievePrivateComponentOfPendingItemAllowed() throws Exception {
        HashMap<String, String> createdIds = prepare(STATUS_PENDING, ALLOWED_CONTEXT);
        doTestRetrieveContent(PWCallback.TEST_HANDLE, createdIds.get("itemId"), createdIds.get("privateComponentId"),
            null, null);
    }

    /**
     * Tests unsuccessfully retrieving a private component of an item in status pending by an privileged viewer.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrievePrivateComponentOfPendingItemDisallowed() throws Exception {
        HashMap<String, String> createdIds = prepare(STATUS_PENDING, DISALLOWED_CONTEXT);
        doTestRetrieveContent(PWCallback.TEST_HANDLE, createdIds.get("itemId"), createdIds.get("privateComponentId"),
            null, AuthorizationException.class);
    }

    /**
     * Tests successfully retrieving a private component of an item in status released by an privileged viewer.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrievePrivateComponentOfReleasedItemAllowed() throws Exception {
        HashMap<String, String> createdIds = prepare(STATUS_PENDING, ALLOWED_CONTEXT);
        doTestRetrieveContent(PWCallback.TEST_HANDLE, createdIds.get("itemId"), createdIds.get("privateComponentId"),
            null, null);
    }

    /**
     * Tests unsuccessfully retrieving a private component of an item in status released by an privileged viewer.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrievePrivateComponentOfReleasedItemDisallowed() throws Exception {
        HashMap<String, String> createdIds = prepare(STATUS_PENDING, DISALLOWED_CONTEXT);
        doTestRetrieveContent(PWCallback.TEST_HANDLE, createdIds.get("itemId"), createdIds.get("privateComponentId"),
            null, AuthorizationException.class);
    }

    /**
     * Tests successfully retrieving a private component of an item in status released by an privileged viewer.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrievePrivateComponentOfVersionAllowed() throws Exception {
        HashMap<String, String> createdIds = prepare(STATUS_PENDING, ALLOWED_CONTEXT);
        doTestRetrieveContent(PWCallback.TEST_HANDLE, createdIds.get("itemId"), createdIds.get("privateComponentId"),
            "1", null);
    }

    /**
     * Tests unsuccessfully retrieving a private component of an item in status released by an privileged viewer.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrievePrivateComponentOfVersionDisallowed() throws Exception {
        HashMap<String, String> createdIds = prepare(STATUS_PENDING, DISALLOWED_CONTEXT);
        doTestRetrieveContent(PWCallback.TEST_HANDLE, createdIds.get("itemId"), createdIds.get("privateComponentId"),
            "1", AuthorizationException.class);
    }

}
