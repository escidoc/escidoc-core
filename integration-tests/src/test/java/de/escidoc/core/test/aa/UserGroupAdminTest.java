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

import java.util.ArrayList;
import java.util.Vector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import de.escidoc.core.common.exceptions.remote.application.security.AuthorizationException;
import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.aa.GrantClient;
import de.escidoc.core.test.security.client.PWCallback;

/**
 * Test suite for the role user-group-administrator.
 * user-group-administrator may
 * -create user-group
 * -see user-groups (s)he created
 * -add/remove selectors to groups (s)he created
 * -create user-group grants to groups (s)he created
 * -revoke grants from groups (s)he created
 * -retrieve grants for groups (s)he created
 * 
 * @author MIH
 * 
 */
public class UserGroupAdminTest extends GrantTestBase {

    protected static final String HANDLE = PWCallback.TEST_HANDLE;

    protected static final String LOGINNAME = HANDLE;

    protected static final String PASSWORD = PWCallback.PASSWORD;

    protected static String grantCreationUserOrGroupId = null;
    
    private static UserGroupTestBase userGroupTestBase = null;
    
    private static int methodCounter = 0;
    
    private static String groupId = null;
    
    private static String lifecycleGroupId = null;
    
    private static String groupId1 = null;
    
    /**
     * The constructor.
     * 
     * @param transport
     *            The transport identifier.
     * @param handlerCode
     *            handlerCode of either UserAccountHandler or UserGroupHandler.
     * @param userOrGroupId
     *            userOrGroupId for grantCreation.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public UserGroupAdminTest(
            final int transport, 
            final int handlerCode,
            final String userOrGroupId) throws Exception {
        super(transport, handlerCode);
        grantCreationUserOrGroupId = userOrGroupId;
        userGroupTestBase = new UserGroupTestBase(transport);
    }

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Before
    public void initialize() throws Exception {
        if (methodCounter == 0) {
            //revoke all Grants
            revokeAllGrants(grantCreationUserOrGroupId);
            //create grant depositor for user grantCreationUserOrGroupId 
            //with scope on default-context
            doTestCreateGrant(null, grantCreationUserOrGroupId, 
                null, ROLE_HREF_USER_GROUP_ADMIN, null);
            prepare();
        }
    }

    /**
     * Clean up after servlet test.
     * 
     * @throws Exception
     *             If anything fails.
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
     * @throws Exception
     *             If anything fails.
     */
    public void prepare() throws Exception {
        //create user-group as group-admin
        String groupXml = prepareUserGroup(HANDLE);
        Document groupDocument =
            EscidocRestSoapTestsBase.getDocument(groupXml);
        groupId = getObjidValue(groupDocument);

        //create user-group as group-admin
        groupXml = prepareUserGroup(HANDLE);
        groupDocument =
            EscidocRestSoapTestsBase.getDocument(groupXml);
        lifecycleGroupId = getObjidValue(groupDocument);

        //create user-group as systemadministrator
        groupXml = prepareUserGroup(PWCallback.DEFAULT_HANDLE);
        groupDocument =
            EscidocRestSoapTestsBase.getDocument(groupXml);
        groupId1 = getObjidValue(groupDocument);
    }

    /**
     * Tests successfully retrieving a user-group.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testUserGroupLifecycle() throws Exception {
        userGroupTestBase.doTestRetrieve(HANDLE, lifecycleGroupId, null);
        userGroupTestBase.doTestUpdate(HANDLE, lifecycleGroupId, null);
        userGroupTestBase.doTestDeactivate(HANDLE, lifecycleGroupId, null);
        userGroupTestBase.doTestActivate(HANDLE, lifecycleGroupId, null);
        userGroupTestBase.doTestDelete(HANDLE, lifecycleGroupId, null);
    }

    /**
     * Tests declining retreiving a user-group 
     * that was created by someone else.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveUserGroupDecline() throws Exception {
        userGroupTestBase.doTestRetrieve(
                HANDLE, groupId1, AuthorizationException.class);
    }

    /**
     * Tests adding a selector to a user-group.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAddSelector() throws Exception {
        String groupXml = userGroupTestBase.doTestRetrieve(null, groupId, null);
        Document groupDocument =
            EscidocRestSoapTestsBase.getDocument(groupXml);
        String lastModificationDate = 
            getLastModificationDateValue(groupDocument);
        ArrayList<String []> selectors = new ArrayList<String[]>();
        String [] selector = new String [3];
        selector[0] = "user-account";
        selector[1] = "internal";
        selector[2] = TEST_USER_ACCOUNT_ID1;

        selectors.add(selector);
        String taskParam = 
            userGroupTestBase
                .getAddSelectorsTaskParam(
                        selectors, lastModificationDate);
        userGroupTestBase.doTestAddSelectors(
                                    HANDLE, groupId, taskParam, null);

        
    }

    /**
     * Tests declining adding a selector to a user-group.
     * user did not create the group.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAddSelectorDecline() throws Exception {
        String groupXml = userGroupTestBase.doTestRetrieve(null, groupId1, null);
        Document groupDocument =
            EscidocRestSoapTestsBase.getDocument(groupXml);
        String lastModificationDate = 
            getLastModificationDateValue(groupDocument);
        ArrayList<String []> selectors = new ArrayList<String[]>();
        String [] selector = new String [3];
        selector[0] = "user-account";
        selector[1] = "internal";
        selector[2] = TEST_USER_ACCOUNT_ID1;

        selectors.add(selector);
        String taskParam = 
            userGroupTestBase
                .getAddSelectorsTaskParam(
                        selectors, lastModificationDate);
        userGroupTestBase.doTestAddSelectors(HANDLE, groupId1, 
                            taskParam, AuthorizationException.class);
        
    }

    /**
     * Tests removing a selector from a user-group.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRemoveSelector() throws Exception {
        String groupXml = userGroupTestBase.doTestRetrieve(null, groupId, null);
        Document userGroupDoc = getDocument(groupXml);
        // NodeList selectorNodes = selectNodeList(userGroupDoc,
        // /user-group/selector);
        NodeList selectorNodes = null;
        if (Constants.TRANSPORT_REST == getTransport()) {
            selectorNodes =
                selectNodeList(
                    userGroupDoc, "/user-group/selectors/selector/@href");
        }
        else {
            selectorNodes =
                selectNodeList(
                    userGroupDoc, "/user-group/selectors/selector/@objid");
        }

        Vector<String> selectorsToRemove = new Vector<String>();
        for (int i = 0; i < selectorNodes.getLength(); i++) {
            String selectorId = selectorNodes.item(i).getNodeValue();
            if (Constants.TRANSPORT_REST == getTransport()) {
            selectorId = getIdFromHrefValue(selectorId);
            }
            selectorsToRemove.add(selectorId);
        }
        String lastModDate = getLastModificationDateValue(userGroupDoc);
        String taskParam = userGroupTestBase
                .getRemoveSelectorsTaskParam(selectorsToRemove, lastModDate);
        userGroupTestBase.doTestRemoveSelectors(HANDLE, groupId, taskParam, null);
        
    }

    /**
     * Tests declining removing a selector from a user-group.
     * user did not create the group.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRemoveSelectorDecline() throws Exception {
        //first add selector
        String groupXml = userGroupTestBase.doTestRetrieve(null, groupId1, null);
        Document groupDocument =
            EscidocRestSoapTestsBase.getDocument(groupXml);
        String lastModificationDate = 
            getLastModificationDateValue(groupDocument);
        ArrayList<String []> selectors = new ArrayList<String[]>();
        String [] selector = new String [3];
        selector[0] = "user-account";
        selector[1] = "internal";
        selector[2] = TEST_USER_ACCOUNT_ID1;

        selectors.add(selector);
        String taskParam = 
            userGroupTestBase
                .getAddSelectorsTaskParam(
                        selectors, lastModificationDate);
        groupXml = userGroupTestBase.doTestAddSelectors(null, groupId1, 
                            taskParam, null);

        
        //then try to remove selector
        Document userGroupDoc = getDocument(groupXml);
        // NodeList selectorNodes = selectNodeList(userGroupDoc,
        // /user-group/selector);
        NodeList selectorNodes = null;
        if (Constants.TRANSPORT_REST == getTransport()) {
            selectorNodes =
                selectNodeList(
                    userGroupDoc, "/user-group/selectors/selector/@href");
        }
        else {
            selectorNodes =
                selectNodeList(
                    userGroupDoc, "/user-group/selectors/selector/@objid");
        }

        Vector<String> selectorsToRemove = new Vector<String>();
        for (int i = 0; i < selectorNodes.getLength(); i++) {
            String selectorId = selectorNodes.item(i).getNodeValue();
            if (Constants.TRANSPORT_REST == getTransport()) {
            selectorId = getIdFromHrefValue(selectorId);
            }
            selectorsToRemove.add(selectorId);
        }
        String lastModDate = getLastModificationDateValue(userGroupDoc);
        taskParam = userGroupTestBase
                .getRemoveSelectorsTaskParam(selectorsToRemove, lastModDate);
        userGroupTestBase.doTestRemoveSelectors(
            HANDLE, groupId1, taskParam, AuthorizationException.class);
        
    }

    /**
     * Tests successfully creating a grant for a user-group.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testCreateGrant() throws Exception {
        if (isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        }
        try {
            doTestCreateGrant(
                    HANDLE, groupId, 
                    null, ROLE_HREF_ADMINISTRATOR, null);
        } finally {
            if (isUserAccountTest) {
                super.setClient(
                    (GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests successfully creating a grant for a user-group.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testCreateGrantDecline() throws Exception {
        if (isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        }
        try {
            doTestCreateGrant(
                    HANDLE, groupId1, 
                    null, ROLE_HREF_ADMINISTRATOR, AuthorizationException.class);
        } finally {
            if (isUserAccountTest) {
                super.setClient(
                    (GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests revoking a grant from a user-group.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRevokeGrant() throws Exception {
        if (isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        }
        try {
            //first create a grant as sysadmin
            String grantXml = 
                doTestCreateGrant(null, groupId, null, ROLE_HREF_AUDIENCE, null);
            String grantId = getObjidValue(grantXml);
            
            //then try to revoke it
            doTestRevokeGrant(HANDLE, groupId, grantId, null);
        } finally {
            if (isUserAccountTest) {
                super.setClient(
                    (GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests declining revoking a grant from a user-group.
     * user did not create the group.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRevokeGrantDecline() throws Exception {
        if (isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        }
        try {
            //first create a grant as sysadmin
            String grantXml = 
                doTestCreateGrant(null, groupId1, null, ROLE_HREF_AUDIENCE, null);
            String grantId = getObjidValue(grantXml);
            
            //then try to revoke it
            doTestRevokeGrant(
                HANDLE, groupId1, grantId, AuthorizationException.class);
        } finally {
            if (isUserAccountTest) {
                super.setClient(
                    (GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests retrieving a grant from a user-group.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveGrant() throws Exception {
        if (isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        }
        try {
            //first create a grant as sysadmin
            String grantXml = 
                doTestCreateGrant(null, groupId, null, ROLE_HREF_AUDIENCE, null);
            String grantId = getObjidValue(grantXml);
            
            //then try to retrieve it
            doTestRetrieveGrant(HANDLE, groupId, grantId, null);
        } finally {
            if (isUserAccountTest) {
                super.setClient(
                    (GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
        }
    }

    /**
     * Tests declining retrieving a grant from a user-group.
     * user did not create the group.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveGrantDecline() throws Exception {
        if (isUserAccountTest) {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
        }
        try {
            //first create a grant as sysadmin
            String grantXml = 
                doTestCreateGrant(null, groupId1, null, ROLE_HREF_AUDIENCE, null);
            String grantId = getObjidValue(grantXml);
            
            //then try to retrieve it
            doTestRetrieveGrant(
                HANDLE, groupId1, grantId, AuthorizationException.class);
        } finally {
            if (isUserAccountTest) {
                super.setClient(
                    (GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            }
        }
    }

}
