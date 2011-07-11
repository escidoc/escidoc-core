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

import de.escidoc.core.common.exceptions.remote.application.notfound.ItemNotFoundException;
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

import static org.junit.Assert.fail;

/**
 * Test suite for the role Moderator.
 *
 * @author Torsten Tetteroo
 */
@RunWith(Parameterized.class)
public class ModeratorIT extends GrantTestBase {

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

    private static final String HANDLE = PWCallback.TEST_HANDLE;

    private static final String LOGINNAME = HANDLE;

    private static final String PASSWORD = PWCallback.PASSWORD;

    protected static String grantCreationUserOrGroupId = null;

    private static int methodCounter = 0;

    private static String itemId = null;

    private static String itemHref = null;

    private static String itemId1 = null;

    private static String itemHref1 = null;

    private static String containerId = null;

    private static String containerHref = null;

    private static String containerId1 = null;

    private static String containerHref1 = null;

    private static String publicComponentId = null;

    private static String publicComponentHref = null;

    private static String publicComponentId1 = null;

    private static String publicComponentHref1 = null;

    /**
     * The constructor.
     *
     * @param handlerCode   handlerCode of either UserAccountHandler or UserGroupHandler.
     * @param userOrGroupId userOrGroupId for grantCreation.
     * @throws Exception If anything fails.
     */
    public ModeratorIT(final int handlerCode, final String userOrGroupId) throws Exception {
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
        revokeAllGrants(grantCreationUserOrGroupId);
        if (methodCounter == 0) {
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
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        if (methodCounter == getTestAnnotationsCount()) {
            revokeAllGrants(grantCreationUserOrGroupId);
            try {
                revokeAllGrants(TEST_USER_ACCOUNT_ID1);
            }
            catch (final Exception e) {
            }
            try {
                revokeAllGrants(TEST_USER_GROUP_ID);
            }
            catch (final Exception e) {
            }
            methodCounter = 0;
        }
    }

    /**
     * insert data into system for the tests.
     *
     * @throws Exception If anything fails.
     */
    protected void prepare() throws Exception {
        //create container in context1 status pending
        String containerXml = prepareContainer(PWCallback.DEFAULT_HANDLE, STATUS_PENDING, CONTEXT_ID, false, false);
        Document containerDocument = EscidocAbstractTest.getDocument(containerXml);
        containerId = getObjidValue(containerDocument);
        containerHref = Constants.CONTAINER_BASE_URI + "/" + containerId;

        //create container in context2 status pending
        containerXml = prepareContainer(PWCallback.DEFAULT_HANDLE, STATUS_PENDING, CONTEXT_ID1, false, false);
        containerDocument = EscidocAbstractTest.getDocument(containerXml);
        containerId1 = getObjidValue(containerDocument);
        containerHref1 = Constants.CONTAINER_BASE_URI + "/" + containerId1;

        //create item in status pending
        // in context1
        String itemXml = prepareItem(PWCallback.DEFAULT_HANDLE, STATUS_PENDING, CONTEXT_ID, false, false);
        Document document = EscidocAbstractTest.getDocument(itemXml);

        //save ids
        itemId = getObjidValue(document);
        itemHref = Constants.ITEM_BASE_URI + "/" + itemId;
        publicComponentId = extractComponentId(document, VISIBILITY_PUBLIC);
        publicComponentHref = itemHref + "/" + Constants.SUB_COMPONENT + "/" + publicComponentId;

        //create item in status pending
        // in context2
        itemXml = prepareItem(PWCallback.DEFAULT_HANDLE, STATUS_PENDING, CONTEXT_ID1, false, false);
        document = EscidocAbstractTest.getDocument(itemXml);

        //save ids
        itemId1 = getObjidValue(document);
        itemHref1 = Constants.ITEM_BASE_URI + "/" + itemId1;
        publicComponentId1 = extractComponentId(document, VISIBILITY_PUBLIC);
        publicComponentHref1 = itemHref1 + "/" + Constants.SUB_COMPONENT + "/" + publicComponentId1;

    }

    /**
     * Tests declining releasing an unknown item by a moderator.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testReleaseUnknownItemDecline() throws Exception {
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
            ROLE_HREF_MODERATOR, null);
        doTestReleaseItem(HANDLE, PWCallback.DEFAULT_HANDLE, null, ItemNotFoundException.class);
    }

    /**
     * Tests successfully releasing an item by a moderator that has grant with scope on context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testReleaseItem() throws Exception {
        String itemXml = prepareItem(PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, null, false, false);
        Document document = EscidocAbstractTest.getDocument(itemXml);
        String id = getObjidValue(document);
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
            ROLE_HREF_MODERATOR, null);

        releaseWithPid(ITEM_HANDLER_CODE, id, HANDLE);
    }

    /**
     * Tests successfully releasing an container by a moderator that has grant with scope on context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testReleaseContainer() throws Exception {
        String containerXml = prepareContainer(PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, null, false, false);
        Document document = EscidocAbstractTest.getDocument(containerXml);
        String id = getObjidValue(document);
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
            ROLE_HREF_MODERATOR, null);

        releaseWithPid(CONTAINER_HANDLER_CODE, id, HANDLE);
    }

    /**
     * Tests unsuccessfully retrieving a pending item by a moderator that has privileges on context-scope.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrievePendingItem() throws Exception {
        String itemXml = prepareItem(PWCallback.DEFAULT_HANDLE, STATUS_PENDING, null, false, false);
        Document document = EscidocAbstractTest.getDocument(itemXml);
        String id = getObjidValue(document);
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
            ROLE_HREF_MODERATOR, null);

        try {
            PWCallback.setHandle(HANDLE);
            retrieve(ITEM_HANDLER_CODE, id);
            fail("No exception occurred on moderator retrieving pending item.");
        }
        catch (final Exception e) {
            assertExceptionType(AuthorizationException.class, e);
        }
    }

    /**
     * Tests successfully retrieving a submitted item by a moderator that has privileges on context-scope.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveSubmittedItem() throws Exception {
        String itemXml = prepareItem(PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, null, false, false);
        Document document = EscidocAbstractTest.getDocument(itemXml);
        String id = getObjidValue(document);
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
            ROLE_HREF_MODERATOR, null);

        PWCallback.setHandle(HANDLE);
        retrieve(ITEM_HANDLER_CODE, id);
    }

    /**
     * Tests successfully retrieving a released item by a moderator that has privileges on context-scope.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveReleasedItem() throws Exception {
        String itemXml = prepareItem(PWCallback.DEFAULT_HANDLE, STATUS_RELEASED, null, false, false);
        Document document = EscidocAbstractTest.getDocument(itemXml);
        String id = getObjidValue(document);
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
            ROLE_HREF_MODERATOR, null);

        PWCallback.setHandle(HANDLE);
        retrieve(ITEM_HANDLER_CODE, id);
    }

    /**
     * Tests successfully retrieving a withdrawn item by a moderator that has privileges on context-scope.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveWithdrawnItem() throws Exception {
        String itemXml = prepareItem(PWCallback.DEFAULT_HANDLE, STATUS_WITHDRAWN, null, false, false);
        Document document = EscidocAbstractTest.getDocument(itemXml);
        String id = getObjidValue(document);
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
            ROLE_HREF_MODERATOR, null);

        PWCallback.setHandle(HANDLE);
        retrieve(ITEM_HANDLER_CODE, id);
    }

    /**
     * Tests unsuccessfully retrieving a pending container by a moderator that has privileges on context-scope.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrievePendingContainer() throws Exception {
        String containerXml = prepareContainer(PWCallback.DEFAULT_HANDLE, STATUS_PENDING, null, false, false);
        Document document = EscidocAbstractTest.getDocument(containerXml);
        String id = getObjidValue(document);
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
            ROLE_HREF_MODERATOR, null);

        try {
            PWCallback.setHandle(HANDLE);
            retrieve(CONTAINER_HANDLER_CODE, id);
            fail("No exception occurred on moderator retrieving pending container.");
        }
        catch (final Exception e) {
            assertExceptionType(AuthorizationException.class, e);
        }
    }

    /**
     * Tests successfully retrieving a submitted container by a moderator that has privileges on context-scope.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveSubmittedContainer() throws Exception {
        String containerXml = prepareContainer(PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, null, false, false);
        Document document = EscidocAbstractTest.getDocument(containerXml);
        String id = getObjidValue(document);
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
            ROLE_HREF_MODERATOR, null);

        PWCallback.setHandle(HANDLE);
        retrieve(CONTAINER_HANDLER_CODE, id);
    }

    /**
     * Tests successfully retrieving a released container by a moderator that has privileges on context-scope.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveReleasedContainer() throws Exception {
        String containerXml = prepareContainer(PWCallback.DEFAULT_HANDLE, STATUS_RELEASED, null, false, false);
        Document document = EscidocAbstractTest.getDocument(containerXml);
        String id = getObjidValue(document);
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
            ROLE_HREF_MODERATOR, null);

        PWCallback.setHandle(HANDLE);
        retrieve(CONTAINER_HANDLER_CODE, id);
    }

    /**
     * Tests successfully retrieving a withdrawn container by a moderator that has privileges on context-scope.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveWithdrawnContainer() throws Exception {
        String containerXml = prepareContainer(PWCallback.DEFAULT_HANDLE, STATUS_WITHDRAWN, null, false, false);
        Document document = EscidocAbstractTest.getDocument(containerXml);
        String id = getObjidValue(document);
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
            ROLE_HREF_MODERATOR, null);

        PWCallback.setHandle(HANDLE);
        retrieve(CONTAINER_HANDLER_CODE, id);
    }

    /**
     * Tests successfully creating a grant on an item that has same context as moderator has scope on.
     *
     * @throws Exception If anything fails.
     */
    //@Test
    public void testCreateItemGrant() throws Exception {
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
            ROLE_HREF_MODERATOR, null);

        try {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
            revokeAllGrants(TEST_USER_ACCOUNT_ID1);
            doTestCreateGrant(HANDLE, TEST_USER_ACCOUNT_ID1, itemHref, ROLE_HREF_COLLABORATOR, null);
        }
        finally {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests successfully creating a grant on an container that has same context as moderator has scope on.
     *
     * @throws Exception If anything fails.
     */
    //@Test
    public void testCreateContainerGrant() throws Exception {
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
            ROLE_HREF_MODERATOR, null);

        try {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
            revokeAllGrants(TEST_USER_ACCOUNT_ID1);
            doTestCreateGrant(HANDLE, TEST_USER_ACCOUNT_ID1, containerHref, ROLE_HREF_COLLABORATOR, null);
        }
        finally {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests successfully creating a grant on an component that has same context as moderator has scope on.
     *
     * @throws Exception If anything fails.
     */
    //@Test
    public void testCreateComponentGrant() throws Exception {
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
            ROLE_HREF_MODERATOR, null);

        try {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
            revokeAllGrants(TEST_USER_ACCOUNT_ID1);
            doTestCreateGrant(HANDLE, TEST_USER_ACCOUNT_ID1, publicComponentHref, ROLE_HREF_COLLABORATOR, null);
        }
        finally {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests declining creating a grant on an item that has not same context as moderator has scope on.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateItemGrantDecline() throws Exception {
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
            ROLE_HREF_MODERATOR, null);

        try {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
            revokeAllGrants(TEST_USER_ACCOUNT_ID1);
            doTestCreateGrant(HANDLE, TEST_USER_ACCOUNT_ID1, itemHref1, ROLE_HREF_COLLABORATOR,
                AuthorizationException.class);
        }
        finally {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests declining creating a grant on an container that has not same context as moderator has scope on.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateContainerGrantDecline() throws Exception {
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
            ROLE_HREF_MODERATOR, null);

        try {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
            revokeAllGrants(TEST_USER_ACCOUNT_ID1);
            doTestCreateGrant(HANDLE, TEST_USER_ACCOUNT_ID1, containerHref1, ROLE_HREF_COLLABORATOR,
                AuthorizationException.class);
        }
        finally {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests declining creating a grant on an component that has not same context as moderator has scope on.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateComponentGrantDecline() throws Exception {
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
            ROLE_HREF_MODERATOR, null);

        try {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
            revokeAllGrants(TEST_USER_ACCOUNT_ID1);
            doTestCreateGrant(HANDLE, TEST_USER_ACCOUNT_ID1, publicComponentHref1, ROLE_HREF_COLLABORATOR,
                AuthorizationException.class);
        }
        finally {
            if (!isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests successfully creating a group-grant on an item that has same context as moderator has scope on.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateItemGroupGrant() throws Exception {
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
            ROLE_HREF_MODERATOR, null);

        try {
            if (isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
            revokeAllGrants(TEST_USER_GROUP_ID);
            doTestCreateGrant(HANDLE, TEST_USER_GROUP_ID, itemHref, ROLE_HREF_COLLABORATOR, null);
        }
        finally {
            if (isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests successfully creating a grant on an container that has same context as moderator has scope on.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateContainerGroupGrant() throws Exception {
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
            ROLE_HREF_MODERATOR, null);

        try {
            if (isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
            revokeAllGrants(TEST_USER_GROUP_ID);
            doTestCreateGrant(HANDLE, TEST_USER_GROUP_ID, containerHref, ROLE_HREF_COLLABORATOR, null);
        }
        finally {
            if (isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests successfully creating a grant on an component that has same context as moderator has scope on.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateComponentGroupGrant() throws Exception {
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
            ROLE_HREF_MODERATOR, null);

        try {
            if (isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
            revokeAllGrants(TEST_USER_GROUP_ID);
            doTestCreateGrant(HANDLE, TEST_USER_GROUP_ID, publicComponentHref, ROLE_HREF_COLLABORATOR, null);
        }
        finally {
            if (isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests declining creating a grant on an item that has not same context as moderator has scope on.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateItemGroupGrantDecline() throws Exception {
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
            ROLE_HREF_MODERATOR, null);

        try {
            if (isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
            revokeAllGrants(TEST_USER_GROUP_ID);
            doTestCreateGrant(HANDLE, TEST_USER_GROUP_ID, itemHref1, ROLE_HREF_COLLABORATOR,
                AuthorizationException.class);
        }
        finally {
            if (isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests declining creating a grant on an container that has not same context as moderator has scope on.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateContainerGroupGrantDecline() throws Exception {
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
            ROLE_HREF_MODERATOR, null);

        try {
            if (isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
            revokeAllGrants(TEST_USER_GROUP_ID);
            doTestCreateGrant(HANDLE, TEST_USER_GROUP_ID, containerHref1, ROLE_HREF_COLLABORATOR,
                AuthorizationException.class);
        }
        finally {
            if (isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests declining creating a grant on an component that has not same context as moderator has scope on.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateComponentGroupGrantDecline() throws Exception {
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
            ROLE_HREF_MODERATOR, null);

        try {
            if (isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            }
            revokeAllGrants(TEST_USER_GROUP_ID);
            doTestCreateGrant(HANDLE, TEST_USER_GROUP_ID, publicComponentHref1, ROLE_HREF_COLLABORATOR,
                AuthorizationException.class);
        }
        finally {
            if (isUserAccountTest) {
                super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
        }
    }

    /**
     * Test logging out a moderator.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaModeratorLogout() throws Exception {

        doTestLogout(LOGINNAME, PASSWORD);
    }
}
