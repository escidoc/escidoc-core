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
import org.w3c.dom.Document;

import java.util.ArrayList;

/**
 * Test suite for UserGroupAuthorizationTest.
 *
 * @author Michael Hoppe
 */
public class UserGroupAuthorizationIT extends GrantTestBase {

    private static int methodCounter = 0;

    private static UserGroupTestBase userGroupTestBase = null;

    private static GrantTestBase groupGrantTestBase = null;

    private static final String HANDLE = PWCallback.TEST_HANDLE;

    private static final String USER_ID = PWCallback.ID_PREFIX + HANDLE;

    private static final String LOGINNAME = HANDLE;

    private static final String PASSWORD = PWCallback.PASSWORD;

    private static final int COLLABORATOR_GROUP_COUNT = 10;

    private static final int ITEMS_PER_GROUP = 2;

    private static String noCollaboratorItem = null;

    private static String[] userCollaboratorItems = new String[ITEMS_PER_GROUP];

    private static String userAdminItem = null;

    private static String groupAdminItem = null;

    private static String[] groupIds = new String[COLLABORATOR_GROUP_COUNT + 1];

    private static String[][] groupCollaboratorItems = new String[COLLABORATOR_GROUP_COUNT][ITEMS_PER_GROUP];

    /**
     * @throws Exception e
     */
    public UserGroupAuthorizationIT() throws Exception {
        super(USER_ACCOUNT_HANDLER_CODE);
        userGroupTestBase = new UserGroupTestBase() {
        };
        groupGrantTestBase = new GrantTestBase(
            USER_GROUP_HANDLER_CODE) {
        };
    }

    /**
     * Creates many Items in status pending , for each item creates group and attaches role collaorator for item to the
     * group. Attaches group to user (either via org-unit or via userId)
     * <p/>
     * Creates one item in status pending in another context, attaches role Administrator to user for the context of the
     * item.
     * <p/>
     * Creates one item in status pending in a third context, Creates new Group, attaches role Administrator for third
     * context to group, Attaches group to user via org-unit.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void initialize() throws Exception {
        if (methodCounter == 0) {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
            revokeAllGrants(USER_ID);

            //create one item with no roles on
            String itemXml = prepareItem(PWCallback.DEFAULT_HANDLE, STATUS_PENDING, CONTEXT_ID, false, false);
            Document item = EscidocAbstractTest.getDocument(itemXml);
            String itemId = getObjidValue(item);
            noCollaboratorItem = itemId;

            //create collaborator roles
            //create collaborator role for user
            //create item
            for (int i = 0; i < ITEMS_PER_GROUP; i++) {
                itemXml = prepareItem(PWCallback.DEFAULT_HANDLE, STATUS_PENDING, CONTEXT_ID, false, false);
                item = EscidocAbstractTest.getDocument(itemXml);
                itemId = getObjidValue(item);

                //attach role collaborator for item to user
                doTestCreateGrant(null, USER_ID, Constants.ITEM_BASE_URI + "/" + itemId, ROLE_HREF_COLLABORATOR, null);
                userCollaboratorItems[i] = itemId;
            }

            //create groups, items for each group
            //attach role collaborator for each item to group
            //attach group to user
            for (int i = 0; i < COLLABORATOR_GROUP_COUNT; i++) {
                //create items
                for (int j = 0; j < ITEMS_PER_GROUP; j++) {
                    //create item
                    itemXml = prepareItem(PWCallback.DEFAULT_HANDLE, STATUS_PENDING, CONTEXT_ID, false, false);
                    item = EscidocAbstractTest.getDocument(itemXml);
                    itemId = getObjidValue(item);
                    groupCollaboratorItems[i][j] = itemId;
                }

                //create group
                Document group = userGroupTestBase.createSuccessfully("escidoc_usergroup_for_create.xml");
                String groupId = getObjidValue(group);
                groupIds[i] = groupId;
                String lastModificationDate = getLastModificationDateValue(group);

                //attach group to user
                ArrayList<String[]> selectors = new ArrayList<String[]>();
                String[] selector = new String[3];
                if (i % 2 == 0) {
                    selector[0] = "user-account";
                    selector[1] = "internal";
                    selector[2] = USER_ID;
                }
                else {
                    selector[0] = "o";
                    selector[1] = "user-attribute";
                    selector[2] = ORGANIZATIONAL_UNIT_ID;
                }
                selectors.add(selector);
                String taskParam = userGroupTestBase.getAddSelectorsTaskParam(selectors, lastModificationDate);
                userGroupTestBase.addSelectors(groupId, taskParam);

                //attach role collaborator for items to group
                for (int j = 0; j < ITEMS_PER_GROUP; j++) {
                    groupGrantTestBase.doTestCreateGrant(null, groupId, Constants.ITEM_BASE_URI + "/"
                        + groupCollaboratorItems[i][j], ROLE_HREF_COLLABORATOR, null);
                }
            }

            //create item in context CONTEXT_ID2
            itemXml = prepareItem(PWCallback.DEFAULT_HANDLE, STATUS_PENDING, CONTEXT_ID2, false, false);
            item = EscidocAbstractTest.getDocument(itemXml);
            itemId = getObjidValue(item);

            //attach role administrator for item to user
            doTestCreateGrant(null, USER_ID, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID2, ROLE_HREF_ADMINISTRATOR,
                null);
            userAdminItem = itemId;

            //create item in context CONTEXT_ID3
            itemXml = prepareItem(PWCallback.DEFAULT_HANDLE, STATUS_PENDING, CONTEXT_ID3, false, false);
            item = EscidocAbstractTest.getDocument(itemXml);
            itemId = getObjidValue(item);

            //create group
            Document group = userGroupTestBase.createSuccessfully("escidoc_usergroup_for_create.xml");
            String groupId = getObjidValue(group);
            groupIds[COLLABORATOR_GROUP_COUNT] = groupId;
            String lastModificationDate = getLastModificationDateValue(group);

            //attach group to user
            ArrayList<String[]> selectors = new ArrayList<String[]>();
            String[] selector = new String[3];
            selector[0] = "o";
            selector[1] = "user-attribute";
            selector[2] = ORGANIZATIONAL_UNIT_ID;
            selectors.add(selector);
            String taskParam = userGroupTestBase.getAddSelectorsTaskParam(selectors, lastModificationDate);
            userGroupTestBase.addSelectors(groupId, taskParam);

            //attach role administrator for item to group
            groupGrantTestBase.doTestCreateGrant(null, groupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID3,
                ROLE_HREF_ADMINISTRATOR, null);
            groupAdminItem = itemId;
        }
        PWCallback.setHandle(HANDLE);
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
        if (getTestAnnotationsCount() == methodCounter) {
            for (int i = 0; i < groupIds.length; i++) {
                userGroupTestBase.delete(groupIds[i]);
            }
            revokeAllGrants(USER_ID);
            methodCounter = 0;
        }
    }

    /**
     * Test successfully retrieving all created items, which are accessible via different groups the user is attached
     * to.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUga1() throws Exception {
        //test retrieving all items the user may see via the groups
        //he belongs to, groups have collaborator-roles
        for (int i = 0; i < groupCollaboratorItems.length; i++) {
            for (int j = 0; j < groupCollaboratorItems[i].length; j++) {
                retrieve(ITEM_HANDLER_CODE, groupCollaboratorItems[i][j]);
            }
        }

        //test retrieving item the user may see 
        //via his user-collaborator role
        for (int i = 0; i < userCollaboratorItems.length; i++) {
            retrieve(ITEM_HANDLER_CODE, userCollaboratorItems[i]);
        }

        //test retrieving items the user may see via his administrator-roles
        //he has attached via user-role and group-role
        retrieve(ITEM_HANDLER_CODE, userAdminItem);
        retrieve(ITEM_HANDLER_CODE, groupAdminItem);
    }

    /**
     * Test unsuccessfully retrieving created items that is not accessible to the user.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUga2() throws Exception {
        PWCallback.setHandle(HANDLE);
        try {
            retrieve(ITEM_HANDLER_CODE, noCollaboratorItem);
            EscidocAbstractTest.failMissingException(AuthorizationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(AuthorizationException.class, e);
        }
    }

}
