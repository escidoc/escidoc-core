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
import de.escidoc.core.test.TaskParamFactory;
import de.escidoc.core.test.Constants;
import de.escidoc.core.test.common.client.servlet.om.ItemClient;
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
 * Test suite for the role CollaboratorModifierUpdateDirectMembers.
 *
 * @author Michael Hoppe
 */
@RunWith(Parameterized.class)
public class CollaboratorModifierUpdateDirectMembersIT extends GrantTestBase {

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

    protected static int methodCounter = 0;

    protected static String contextId = null;

    protected static String contextHref = null;

    protected static String itemId = null;

    protected static String itemHref = null;

    protected static String containerId = null;

    protected static String containerHref = null;

    protected static String containerId2 = null;

    protected static String containerHref2 = null;

    protected static String publicComponentId = null;

    protected static String publicComponentHref = null;

    protected static String privateComponentId = null;

    protected static String privateComponentHref = null;

    /**
     * The constructor.
     *
     * @param handlerCode   handlerCode of either UserAccountHandler or UserGroupHandler.
     * @param userOrGroupId userOrGroupId for grantCreation.
     * @throws Exception If anything fails.
     */
    public CollaboratorModifierUpdateDirectMembersIT(final int handlerCode, final String userOrGroupId)
        throws Exception {
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
        revokeAllGrants(grantCreationUserOrGroupId);
    }

    /**
     * insert data into system for the tests.
     *
     * @throws Exception If anything fails.
     */
    protected void prepare() throws Exception {

        //create 1. container in status pending
        String containerXml = prepareContainer(PWCallback.DEFAULT_HANDLE, STATUS_PENDING, null, false, false);
        Document containerDocument = EscidocAbstractTest.getDocument(containerXml);
        containerId = getObjidValue(containerDocument);
        containerHref = Constants.CONTAINER_BASE_URI + "/" + containerId;

        //create 2. container in status pending
        containerXml = prepareContainer(PWCallback.DEFAULT_HANDLE, STATUS_PENDING, null, false, false);
        Document containerDocument2 = EscidocAbstractTest.getDocument(containerXml);
        containerId2 = getObjidValue(containerDocument2);
        containerHref2 = Constants.CONTAINER_BASE_URI + "/" + containerId2;

        //create item in status pending
        // in context /ir/context/escidoc:persistent3
        String itemXml = prepareItem(PWCallback.DEFAULT_HANDLE, STATUS_PENDING, null, false, false);
        Document document = EscidocAbstractTest.getDocument(itemXml);

        //save ids
        contextId = extractContextId(document);
        contextHref = Constants.CONTEXT_BASE_URI + "/" + contextId;
        itemId = getObjidValue(document);
        itemHref = Constants.ITEM_BASE_URI + "/" + itemId;
        publicComponentId = extractComponentId(document, VISIBILITY_PUBLIC);
        publicComponentHref = itemHref + "/" + Constants.SUB_COMPONENT + "/" + publicComponentId;
        privateComponentId = extractComponentId(document, VISIBILITY_PRIVATE);
        privateComponentHref = itemHref + "/" + Constants.SUB_COMPONENT + "/" + privateComponentId;

        //add container2 to container
        ArrayList<String> ids = new ArrayList<String>();
        ids.add(containerId2);
        getContainerClient().addMembers(containerId,
            TaskParamFactory.getMembersTaskParam(ids, getLastModificationDateValue2(containerDocument)));

        //add item to container2
        ids = new ArrayList<String>();
        ids.add(itemId);
        getContainerClient().addMembers(containerId2,
            TaskParamFactory.getMembersTaskParam(ids, getLastModificationDateValue2(containerDocument2)));

        //update item to create new version
        itemXml = itemXml.replaceAll("semiconductor surfaces", "semiconductor surfaces u");
        itemXml = update(ITEM_HANDLER_CODE, itemId, itemXml);

    }

    /**
     * Test CollaboratorModifierUpdateDirectMembers retrieving an item with scope on container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveItemWithContainerScope() throws Exception {
        doTestRetrieveWithRole(grantCreationUserOrGroupId, ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS,
            containerHref2, HANDLE, ITEM_HANDLER_CODE, itemId, true, null);
    }

    /**
     * Test CollaboratorModifierUpdateDirectMembers retrieving an item with scope on parent container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveItemWithParentContainerScopeDecline() throws Exception {
        doTestRetrieveWithRole(grantCreationUserOrGroupId, ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS,
            containerHref, HANDLE, ITEM_HANDLER_CODE, itemId, true, AuthorizationException.class);
    }

    /**
     * Test CollaboratorModifierUpdateDirectMembers retrieving an item with no scope.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveItemWithNoScope() throws Exception {
        doTestRetrieveWithRole(grantCreationUserOrGroupId, ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS, null,
            HANDLE, ITEM_HANDLER_CODE, itemId, true, AuthorizationException.class);
    }

    /**
     * Test CollaboratorModifierUpdateDirectMembers retrieving an container with scope on container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveContainerWithContainerScope() throws Exception {
        doTestRetrieveWithRole(grantCreationUserOrGroupId, ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS,
            containerHref2, HANDLE, CONTAINER_HANDLER_CODE, containerId2, true, null);
    }

    /**
     * Test CollaboratorModifierUpdateDirectMembers retrieving an container with scope on parent container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveContainerWithParentContainerScope() throws Exception {
        doTestRetrieveWithRole(grantCreationUserOrGroupId, ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS,
            containerHref, HANDLE, CONTAINER_HANDLER_CODE, containerId2, true, null);
    }

    /**
     * Test CollaboratorModifierUpdateDirectMembers updating an item with scope on container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateItemWithContainerScope() throws Exception {
        doTestUpdateWithRole(grantCreationUserOrGroupId, ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS,
            containerHref2, HANDLE, ITEM_HANDLER_CODE, itemId, "semiconductor surfaces", " u", null);
    }

    /**
     * Test CollaboratorModifierUpdateDirectMembers updating an item with scope on parent container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateItemWithParentContainerScopeDecline() throws Exception {
        doTestUpdateWithRole(grantCreationUserOrGroupId, ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS,
            containerHref, HANDLE, ITEM_HANDLER_CODE, itemId, "semiconductor surfaces", " u",
            AuthorizationException.class);
    }

    /**
     * Test CollaboratorModifierUpdateDirectMembers updating an item with no scope.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateItemWithNoScope() throws Exception {
        doTestUpdateWithRole(grantCreationUserOrGroupId, ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS, null,
            HANDLE, ITEM_HANDLER_CODE, itemId, "semiconductor surfaces", " u", AuthorizationException.class);
    }

    /**
     * Test CollaboratorModifierUpdateDirectMembers updating an container with scope on container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateContainerWithContainerScope() throws Exception {
        doTestUpdateWithRole(grantCreationUserOrGroupId, ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS,
            containerHref2, HANDLE, CONTAINER_HANDLER_CODE, containerId2, "the title", " u", null);
    }

    /**
     * Test CollaboratorModifierUpdateDirectMembers updating an container with scope on parent container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateContainerWithParentContainerScope() throws Exception {
        doTestUpdateWithRole(grantCreationUserOrGroupId, ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS,
            containerHref, HANDLE, CONTAINER_HANDLER_CODE, containerId2, "the title", " u", null);
    }

    /**
     * Test CollaboratorModifierUpdateDirectMembers updating an container with scope on child-container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateContainerWithChildContainerScopeDecline() throws Exception {
        doTestUpdateWithRole(grantCreationUserOrGroupId, ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS,
            containerHref2, HANDLE, CONTAINER_HANDLER_CODE, containerId, "the title", " u",
            AuthorizationException.class);
    }

    /**
     * Test CollaboratorModifierUpdateDirectMembers updating an container with no scope.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateContainerWithNoScope() throws Exception {
        doTestUpdateWithRole(grantCreationUserOrGroupId, ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS, null,
            HANDLE, CONTAINER_HANDLER_CODE, containerId2, "the title", " u", AuthorizationException.class);
    }

    /**
     * Test CollaboratorModifierUpdateDirectMembers locking/unlocking an item with scope on container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testLockUnlockItemWithContainerScope() throws Exception {
        doTestLockUnlockWithRole(grantCreationUserOrGroupId, ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS,
            containerHref2, HANDLE, ITEM_HANDLER_CODE, itemId, null);
    }

    /**
     * Test CollaboratorModifierUpdateDirectMembers locking/unlocking an item with scope on parent container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testLockUnlockItemWithParentContainerScopeDecline() throws Exception {
        doTestLockUnlockWithRole(grantCreationUserOrGroupId, ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS,
            containerHref, HANDLE, ITEM_HANDLER_CODE, itemId, AuthorizationException.class);
    }

    /**
     * Test CollaboratorModifierUpdateDirectMembers locking/unlocking an item with no scope.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testLockUnlockItemWithNoScope() throws Exception {
        doTestLockUnlockWithRole(grantCreationUserOrGroupId, ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS,
            null, HANDLE, ITEM_HANDLER_CODE, itemId, AuthorizationException.class);
    }

    /**
     * Test CollaboratorModifierUpdateDirectMembers locking/unlocking an container with scope on container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testLockUnlockContainerWithContainerScope() throws Exception {
        doTestLockUnlockWithRole(grantCreationUserOrGroupId, ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS,
            containerHref2, HANDLE, CONTAINER_HANDLER_CODE, containerId2, null);
    }

    /**
     * Test CollaboratorModifierUpdateDirectMembers locking/unlocking an container with scope on parent container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testLockUnlockContainerWithParentContainerScope() throws Exception {
        doTestLockUnlockWithRole(grantCreationUserOrGroupId, ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS,
            containerHref, HANDLE, CONTAINER_HANDLER_CODE, containerId2, null);
    }

    /**
     * Test CollaboratorModifierUpdateDirectMembers locking/unlocking an container with scope on child-container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testLockUnlockContainerWithChildContainerScopeDecline() throws Exception {
        doTestLockUnlockWithRole(grantCreationUserOrGroupId, ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS,
            containerHref2, HANDLE, CONTAINER_HANDLER_CODE, containerId, AuthorizationException.class);
    }

    /**
     * Test CollaboratorModifierUpdateDirectMembers locking/unlocking an container with no scope.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testLockUnlockContainerWithNoScope() throws Exception {
        doTestLockUnlockWithRole(grantCreationUserOrGroupId, ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS,
            null, HANDLE, CONTAINER_HANDLER_CODE, containerId2, AuthorizationException.class);
    }

    /**
     * Test CollaboratorModifierUpdateDirectMembers adding an item to a container with scope on container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAddItemToContainerWithContainerScope() throws Exception {
        doTestAddMemberWithRole(grantCreationUserOrGroupId, ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS,
            containerHref, HANDLE, ITEM_HANDLER_CODE, containerId, null);
    }

    /**
     * Test CollaboratorModifierUpdateDirectMembers adding an item to a container with scope on parent container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAddItemToContainerWithParentContainerScope() throws Exception {
        doTestAddMemberWithRole(grantCreationUserOrGroupId, ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS,
            containerHref, HANDLE, ITEM_HANDLER_CODE, containerId2, null);
    }

    /**
     * Test CollaboratorModifierUpdateDirectMembers adding an item to a container with scope on container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAddContainerToContainerWithContainerScope() throws Exception {
        doTestAddMemberWithRole(grantCreationUserOrGroupId, ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS,
            containerHref, HANDLE, CONTAINER_HANDLER_CODE, containerId, null);
    }

    /**
     * Test CollaboratorModifierUpdateDirectMembers adding an item to a container with scope on parent container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAddContainerToContainerWithParentContainerScope() throws Exception {
        doTestAddMemberWithRole(grantCreationUserOrGroupId, ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS,
            containerHref, HANDLE, CONTAINER_HANDLER_CODE, containerId2, null);
    }

    /**
     * Test logging out a CollaboratorModifierUpdateDirectMembers.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaCollaboratorLogout() throws Exception {

        doTestLogout(LOGINNAME, PASSWORD);
    }

    /**
     * Test collaborator with scope on container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveContentWithContainerScope() throws Exception {

        //create grant collaborator for user USER_ID and scope of container
        doTestCreateGrant(null, grantCreationUserOrGroupId, containerHref2,
            ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS, null);

        //test declining retrieving content
        try {
            PWCallback.setHandle(HANDLE);
            ((ItemClient) getClient(ITEM_HANDLER_CODE)).retrieveContent(itemId, privateComponentId);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("retrieving content of object failed. ", e);
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }

    }

    /**
     * Test collaborator with scope on parent container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveContentWithParentContainerScopeDecline() throws Exception {

        //create grant collaborator for user USER_ID and scope of container
        doTestCreateGrant(null, grantCreationUserOrGroupId, containerHref,
            ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS, null);

        //test declining retrieving content
        try {
            PWCallback.setHandle(HANDLE);
            ((ItemClient) getClient(ITEM_HANDLER_CODE)).retrieveContent(itemId, privateComponentId);
            EscidocAbstractTest.failMissingException(AuthorizationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(AuthorizationException.class, e);
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }

    }

    /**
     * Test collaborator with no scope.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveContentWithNoScope() throws Exception {

        //create grant collaborator for user USER_ID and scope null
        doTestCreateGrant(null, grantCreationUserOrGroupId, null,
            ROLE_HREF_COLLABORATOR_MODIFIER_UPDATE_DIRECT_MEMBERS, null);

        //test declining retrieving content
        try {
            PWCallback.setHandle(HANDLE);
            ((ItemClient) getClient(ITEM_HANDLER_CODE)).retrieveContent(itemId, privateComponentId);
            EscidocAbstractTest.failMissingException(AuthorizationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(AuthorizationException.class, e);
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }

    }
}
