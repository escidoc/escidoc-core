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

import de.escidoc.core.common.exceptions.remote.application.notfound.ContainerNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.StagingFileNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.security.AuthorizationException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.aa.GrantClient;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import java.util.ArrayList;

/**
 * Test suite for the default policies. Test-Set for Grant-test: create new user (userId) create 2 new groups (groupId
 * and groupId1, groupId1 belongs to groupId)) add testuser1 to group1 let testuser and testuser1 create one container
 * and one item in status pending both users have no grants assigned
 *
 * @author Torsten Tetteroo
 */
public class DefaultPoliciesIT extends GrantTestBase {

    private static UserGroupTestBase userGroupTestBase = null;

    protected static final String HANDLE = PWCallback.TEST_HANDLE;

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

    private static String userId = null;

    private static String groupId = null;

    private static String groupId1 = null;

    /**
     * The constructor.
     *
     * @throws Exception If anything fails.
     */
    public DefaultPoliciesIT() throws Exception {
        super(USER_ACCOUNT_HANDLER_CODE);
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
            prepare();
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
    }

    /**
     * insert data into system for the tests.
     *
     * @throws Exception If anything fails.
     */
    protected void prepare() throws Exception {
        //create sysadmin grant for testusers
        revokeAllGrants(TEST_USER_ACCOUNT_ID);
        doTestCreateGrant(null, TEST_USER_ACCOUNT_ID, null, ROLE_HREF_SYSTEM_ADMINISTRATOR, null);
        revokeAllGrants(TEST_USER_ACCOUNT_ID1);
        doTestCreateGrant(null, TEST_USER_ACCOUNT_ID1, null, ROLE_HREF_SYSTEM_ADMINISTRATOR, null);
        try {
            //create user to attach test-grants
            String userXml = prepareUserAccount(PWCallback.DEFAULT_HANDLE, STATUS_ACTIVE);
            Document userDocument = EscidocAbstractTest.getDocument(userXml);
            userId = getObjidValue(userDocument);

            //create groups to attach test-grants
            String groupXml = prepareUserGroup(PWCallback.DEFAULT_HANDLE);
            Document groupDocument = EscidocAbstractTest.getDocument(groupXml);
            groupId = getObjidValue(groupDocument);
            String lastModificationDate = getLastModificationDateValue(groupDocument);

            groupXml = prepareUserGroup(PWCallback.DEFAULT_HANDLE);
            groupDocument = EscidocAbstractTest.getDocument(groupXml);
            groupId1 = getObjidValue(groupDocument);
            String lastModificationDate1 = getLastModificationDateValue(groupDocument);

            //add group1 to group
            ArrayList<String[]> selectors = new ArrayList<String[]>();
            String[] selector = new String[3];
            selector[0] = "user-group";
            selector[1] = "internal";
            selector[2] = groupId1;

            selectors.add(selector);
            String taskParam = userGroupTestBase.getAddSelectorsTaskParam(selectors, lastModificationDate);
            userGroupTestBase.doTestAddSelectors(null, groupId, taskParam, null);

            //add testuser1 to group1
            selectors = new ArrayList<String[]>();
            selector = new String[3];
            selector[0] = "user-account";
            selector[1] = "internal";
            selector[2] = TEST_USER_ACCOUNT_ID1;

            selectors.add(selector);
            taskParam = userGroupTestBase.getAddSelectorsTaskParam(selectors, lastModificationDate1);
            userGroupTestBase.doTestAddSelectors(null, groupId1, taskParam, null);

            //testuser create container in status pending
            String containerXml = prepareContainer(PWCallback.TEST_HANDLE, STATUS_PENDING, null, false, false);
            Document containerDocument = EscidocAbstractTest.getDocument(containerXml);
            containerId = getObjidValue(containerDocument);
            containerHref = Constants.CONTAINER_BASE_URI + "/" + containerId;

            //testuser1 create container in status pending
            containerXml = prepareContainer(PWCallback.TEST_HANDLE1, STATUS_PENDING, null, false, false);
            containerDocument = EscidocAbstractTest.getDocument(containerXml);
            containerId1 = getObjidValue(containerDocument);
            containerHref1 = Constants.CONTAINER_BASE_URI + "/" + containerId1;

            //testuser create item in status pending
            // in context /ir/context/escidoc:persistent3
            String itemXml = prepareItem(PWCallback.TEST_HANDLE, STATUS_PENDING, null, false, false);
            Document document = EscidocAbstractTest.getDocument(itemXml);

            //save ids
            itemId = getObjidValue(document);
            itemHref = Constants.ITEM_BASE_URI + "/" + itemId;
            publicComponentId = extractComponentId(document, VISIBILITY_PUBLIC);
            publicComponentHref = itemHref + "/" + Constants.SUB_COMPONENT + "/" + publicComponentId;

            //testuser1 create item in status pending
            // in context /ir/context/escidoc:persistent3
            itemXml = prepareItem(PWCallback.TEST_HANDLE1, STATUS_PENDING, null, false, false);
            document = EscidocAbstractTest.getDocument(itemXml);

            //save ids
            itemId1 = getObjidValue(document);
            itemHref1 = Constants.ITEM_BASE_URI + "/" + itemId1;
            publicComponentId1 = extractComponentId(document, VISIBILITY_PUBLIC);
            publicComponentHref1 = itemHref1 + "/" + Constants.SUB_COMPONENT + "/" + publicComponentId1;

        }
        finally {
            //revoke sysadmin grant for testuser
            revokeAllGrants(TEST_USER_ACCOUNT_ID);
            revokeAllGrants(TEST_USER_ACCOUNT_ID1);
        }

    }

    /**
     * Test declining retrieving an unknown item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveUnknownItemDecline() throws Exception {

        doTestRetrieveItem(HANDLE, PWCallback.DEFAULT_HANDLE, null, false, null, ItemNotFoundException.class);
    }

    /**
     * Test declining retrieving an item in state pending without specifying the version id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveItemPendingDecline() throws Exception {

        doTestRetrieveItem(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_PENDING, false, null, AuthorizationException.class);
    }

    /**
     * Test declining retrieving an item in state pending with specifying the version id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveItemPendingDeclineWithVersionNumber() throws Exception {

        doTestRetrieveItem(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_PENDING, false, "1", AuthorizationException.class);
    }

    /**
     * Test declining retrieving an item in state submitted.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveItemSubmittedDecline() throws Exception {

        doTestRetrieveItem(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, false, "1",
            AuthorizationException.class);
    }

    /**
     * Test successfully retrieving an item in state released.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveItemReleased() throws Exception {

        doTestRetrieveItem(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_RELEASED, false, null, null);
    }

    /**
     * Test successfully retrieving an item version in version status released and in public-status withdrawn.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveItemWithdrawnVersionReleased() throws Exception {

        doTestRetrieveItem(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_WITHDRAWN, true, "3", null);
    }

    /**
     * Test successfully retrieving an item in public-status withdrawn, version status not released.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveItemWithdrawn() throws Exception {

        doTestRetrieveItem(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_WITHDRAWN, true, null, null);
    }

    /**
     * Test successfully retrieving the latest released version of an item in status pending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void _testRetrieveItemReleasedUpdated() throws Exception {

        String xml = doTestRetrieveItem(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_RESUBMITTED, true, null, null);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(xml);
        String thisVersion =
            selectSingleNode(retrievedDocument, "/item/properties/version/number/text()").getNodeValue();
        String latestVersion =
            selectSingleNode(retrievedDocument, "/item/properties/latest-version/number/text()").getNodeValue();
        assertNotEquals("latest-release was not returned", latestVersion, thisVersion);
    }

    /**
     * Test declining submitting an unknown item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSubmitUnknownItemDecline() throws Exception {

        doTestSubmitItem(HANDLE, PWCallback.DEFAULT_HANDLE, null, AuthorizationException.class);
    }

    /**
     * Test declining submitting an item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSubmitItemDecline() throws Exception {

        doTestSubmitItem(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_PENDING, AuthorizationException.class);
    }

    /**
     * Test declining releasing an unknown item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testReleaseUnknownItemDecline() throws Exception {

        doTestReleaseItem(HANDLE, PWCallback.DEFAULT_HANDLE, null, AuthorizationException.class);
    }

    /**
     * Test declining releasing an item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testReleaseItemDecline() throws Exception {

        doTestReleaseItem(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, AuthorizationException.class);
    }

    /**
     * Test declining updating an unknown item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateUnknownItemDecline() throws Exception {

        doTestUpdateItem(HANDLE, PWCallback.DEFAULT_HANDLE, null, null, AuthorizationException.class);
    }

    /**
     * Test declining updating an item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateItemDecline() throws Exception {

        doTestUpdateItem(HANDLE, PWCallback.DEFAULT_HANDLE, null, null, AuthorizationException.class);
    }

    /**
     * Test declining withdrawing an unknown item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testWithdrawUnknownItemDecline() throws Exception {

        doTestWithdrawItem(HANDLE, PWCallback.DEFAULT_HANDLE, null, AuthorizationException.class);
    }

    /**
     * Test declining withdrawing an item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testWithdrawItemDecline() throws Exception {

        doTestWithdrawItem(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_RELEASED, AuthorizationException.class);
    }

    /**
     * Test successfully retrieving a container in public-status withdrawn, version status not released.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveContainerWithdrawn() throws Exception {

        doTestRetrieveContainer(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_WITHDRAWN, true, null, null);
    }

    /**
     * Test declining creating a StagingFile by an anonymous user.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateStagingFileDecline() throws Exception {

        doTestCreateStagingFile(HANDLE, AuthorizationException.class);
    }

    /**
     * Test declining retrieving an user account by an anonymous user.<br> The retrieval uses the user account id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveUserAccountDecline() throws Exception {

        doTestRetrieveUserAccount(HANDLE, null, "byId", PWCallback.DEFAULT_HANDLE, STATUS_ACTIVE,
            AuthorizationException.class);
    }

    /**
     * Test declining retrieving an user account by an anonymous user.<br> The retrieval uses the user account login
     * name.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveUserAccountByLoginNameDecline() throws Exception {

        doTestRetrieveUserAccount(HANDLE, null, "byLoginName", PWCallback.DEFAULT_HANDLE, STATUS_ACTIVE,
            AuthorizationException.class);
    }

    /**
     * Test declining retrieving an user account by an anonymous user.<br> The retrieval uses an handle
     * (PWCallback.DEPOSITOR_HANDLE).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveUserAccountByHandle() throws Exception {

        doTestRetrieveUserAccount(HANDLE, PWCallback.DEPOSITOR_HANDLE, null, null, null, AuthorizationException.class);
    }

    /**
     * Test successfully retrieving a content model by an anonymous user.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveContentModel() throws Exception {

        doTestRetrieveContentModel(HANDLE, CONTENT_TYPE_ID, null);
    }

    /**
     * Test successfully retrieving children of a organizational unit by an anonymous user.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveOrgUnitChildren() throws Exception {
        doTestRetrieveOrganizationalUnitChildren(HANDLE, STATUS_OPEN, new String[] { STATUS_NEW, STATUS_OPEN }, null);
    }

    /**
     * Test declining retrieving a container by providing an id of a existing resource of another resource type.
     *
     * @throws Exception Thrown if anything fails.
     */
    @Test
    public void testAaDef1a() throws Exception {

        try {
            retrieve(CONTAINER_HANDLER_CODE, CONTEXT_ID);
            EscidocAbstractTest.failMissingException(ContainerNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ContainerNotFoundException.class, e);
        }
    }

    /**
     * Test declining retrieving a content model by providing an id of a existing resource of another resource type.
     *
     * @throws Exception Thrown if anything fails.
     */
    @Test
    public void testAaDef1b() throws Exception {

        try {
            retrieve(CONTENT_MODEL_HANDLER_CODE, CONTEXT_ID);
            EscidocAbstractTest.failMissingException(ContentModelNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ContentModelNotFoundException.class, e);
        }
    }

    /**
     * Test declining retrieving a context by providing an id of a existing resource of another resource type.
     *
     * @throws Exception Thrown if anything fails.
     */
    @Test
    public void testAaDef1c() throws Exception {

        try {
            retrieve(CONTEXT_HANDLER_CODE, CONTENT_TYPE_ID);
            EscidocAbstractTest.failMissingException(ContextNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ContextNotFoundException.class, e);
        }
    }

    /**
     * Test declining retrieving an item by providing an id of a existing resource of another resource type.
     *
     * @throws Exception Thrown if anything fails.
     */
    @Test
    public void testAaDef1d() throws Exception {

        try {
            retrieve(ITEM_HANDLER_CODE, CONTEXT_ID);
            EscidocAbstractTest.failMissingException(ItemNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ItemNotFoundException.class, e);
        }
    }

    /**
     * Test declining retrieving an organizational unit by providing an id of a existing resource of another resource
     * type.
     *
     * @throws Exception Thrown if anything fails.
     */
    @Test
    public void testAaDef1e() throws Exception {

        try {
            retrieve(ORGANIZATIONAL_UNIT_HANDLER_CODE, CONTEXT_ID);
            EscidocAbstractTest.failMissingException(OrganizationalUnitNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(OrganizationalUnitNotFoundException.class, e);
        }
    }

    /**
     * Test declining retrieving a role by providing an id of a existing
     * resource of another resource type. This test is not possibleas intented,
     * because the default user is never allowed to retrieve a role.
     *
     * @test.name Default Policies - Retrieve Role - Wrong Id
     * @test.id AA_Def-1-f
     * @test.input Id of an existing resource of another type.
     * @test.expected: RoleNotFoundException
     * @test.status Implemented
     *
     * @throws Exception
     *             Thrown if anything fails.
     */
    // public void testAaDef1f() throws Exception {
    //        
    // try {
    // retrieve(ROLE_HANDLER_CODE, CONTEXT_ID);
    // failMissingException(RoleNotFoundException.class);
    // }
    // catch (final Exception e) {
    // assertExceptionType(RoleNotFoundException.class, e);
    // }
    // }

    /**
     * Test declining retrieving a staging file by providing an id of a existing resource of another resource type.
     *
     * @throws Exception Thrown if anything fails.
     */
    @Test
    public void testAaDef1g() throws Exception {

        try {
            retrieve(STAGING_FILE_HANDLER_CODE, CONTEXT_ID);
            EscidocAbstractTest.failMissingException(StagingFileNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(StagingFileNotFoundException.class, e);
        }
    }

    /**
     * Test declining retrieving an user account by providing an id of a
     * existing resource of another resource type. <br>
     * This test is not possibleas intented, because the subject's handle is
     * fetched before an user-account attribute is fetched. This leads to a
     * redirect to login as in any other attempt to retrieve an user-account.
     *
     * @test.name Default Policies - Retrieve User Account - Wrong Id
     * @test.id AA_Def-1-h
     * @test.input Id of an existing resource of another type.
     * @test.expected: UserAccountNotFoundException
     * @test.status Rejected
     *
     * @throws Exception
     *             Thrown if anything fails.
     */

    /**
     * Test declining querying the semantic store.<br/>
     *
     * @throws Exception Thrown if anything fails.
     */
    @Test
    public void testAaDef2() throws Exception {

        final String param = getTaskParametrSpo("&lt;info:fedora/escidov:user1" + "&gt;  * * ooo", "N-Triples");
        PWCallback.setHandle(PWCallback.ANONYMOUS_HANDLE);
        try {
            spo(param);
            EscidocAbstractTest.failMissingException(AuthorizationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(AuthorizationException.class, e);
        }
    }

    /**
     * Tests declining retrieving a context in status created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDef3() throws Exception {

        // create context using System-Administrator
        PWCallback.resetHandle();

        String path = TEMPLATE_CONTEXT_PATH;
        path += "/rest";
        final Document toBeCreatedDocument = EscidocAbstractTest.getTemplateAsDocument(path, "context_create.xml");
        substitute(toBeCreatedDocument, XPATH_CONTEXT_PROPERTIES_NAME, getUniqueName("Some Context "));
        final String toBeCreatedXml = toString(toBeCreatedDocument, false);

        String createdXml = null;
        try {
            createdXml = create(CONTEXT_HANDLER_CODE, toBeCreatedXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Test init: Context creation failed.", e);
        }
        final String id = getObjidValue(createdXml);

        // retrieve context

        Class<AuthorizationException> ec = AuthorizationException.class;

        PWCallback.setHandle(HANDLE);
        try {
            retrieve(CONTEXT_HANDLER_CODE, id);
            EscidocAbstractTest.failMissingException("Retrieving created context not declined.", ec);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Retrieving created context not declined, properly.", ec, e);
        }

    }

    /**
     * Tests declining retrieving an aggregation definition.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDef4() throws Exception {

        final Class<AuthorizationException> ec = AuthorizationException.class;
        try {
            retrieve(AGGREGATION_DEFINITION_HANDLER_CODE, "1");
            EscidocAbstractTest.failMissingException("Retrieving aggregation definition not declined.", ec);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Retrieving aggregation definition not declined, properly.", ec, e);
        }
    }

    /**
     * Tests declining retrieving a report definition.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDef5() throws Exception {

        final Class<AuthorizationException> ec = AuthorizationException.class;
        try {
            retrieve(REPORT_DEFINITION_HANDLER_CODE, "1");
            EscidocAbstractTest.failMissingException("Retrieving report definition not declined.", ec);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Retrieving report definition not declined, properly.", ec, e);
        }
    }

    /**
     * Tests successfully retrieving a report.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDef6() throws Exception {

        final Document parameterDocument =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_REP_PARAMETERS_PATH, "escidoc_report_parameters1.xml");
        String parameterXml = toString(parameterDocument, false);
        parameterXml = parameterXml.replaceAll("repdef3", "repdef1");

        try {
            retrieve(REPORT_HANDLER_CODE, parameterXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
    }

    /**
     * Tests declining retrieving a scope.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDef7() throws Exception {

        final Class<AuthorizationException> ec = AuthorizationException.class;
        try {
            retrieve(SCOPE_HANDLER_CODE, "1");
            EscidocAbstractTest.failMissingException("Retrieving scope not declined.", ec);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Retrieving scope not declined, properly.", ec, e);
        }
    }

    /**
     * Tests declining deleting an aggregation definition.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDef8() throws Exception {

        final Class<AuthorizationException> ec = AuthorizationException.class;

        try {
            delete(AGGREGATION_DEFINITION_HANDLER_CODE, "1");
            EscidocAbstractTest.failMissingException("Deleting aggregation definition not declined.", ec);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Deleting aggregation definition not declined, properly.", ec, e);
        }
    }

    /**
     * Tests declining deleting a report definition.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDef9() throws Exception {

        final Class<AuthorizationException> ec = AuthorizationException.class;

        try {
            delete(REPORT_DEFINITION_HANDLER_CODE, "1");
            EscidocAbstractTest.failMissingException("Deleting report definition not declined.", ec);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Deleting report definition not declined, properly.", ec, e);
        }
    }

    /**
     * Tests declining deleting a scope.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDef10() throws Exception {

        final Class<AuthorizationException> ec = AuthorizationException.class;
        try {
            delete(SCOPE_HANDLER_CODE, "1");
            EscidocAbstractTest.failMissingException("Deleting scope not declined.", ec);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Deleting scope not declined, properly.", ec, e);
        }
    }

    /**
     * Tests declining creating an aggregation definition.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDef11() throws Exception {

        final Class<AuthorizationException> ec = AuthorizationException.class;

        final Document toBeCreatedDoc =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_AGG_DEF_PATH, "escidoc_aggregation_definition2.xml");
        String toBeCreatedXml = toString(toBeCreatedDoc, false);

        try {
            create(AGGREGATION_DEFINITION_HANDLER_CODE, toBeCreatedXml);
            EscidocAbstractTest.failMissingException("Creating aggregation definition not declined.", ec);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Creating aggregation definition not declined, properly.", ec, e);
        }
    }

    /**
     * Tests declining creating a report definition.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDef12() throws Exception {

        final Class<AuthorizationException> ec = AuthorizationException.class;

        final Document toBeCreatedDoc =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_REP_DEF_PATH, "escidoc_report_definition1.xml");
        String toBeCreatedXml = toString(toBeCreatedDoc, false);

        try {
            create(REPORT_DEFINITION_HANDLER_CODE, toBeCreatedXml);
            EscidocAbstractTest.failMissingException("Creating report definition not declined.", ec);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Creating report definition not declined, properly.", ec, e);
        }
    }

    /**
     * Tests declining creating a scope.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDef13() throws Exception {

        final Class<AuthorizationException> ec = AuthorizationException.class;

        final String toBeCreatedXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_SCOPE_PATH, "escidoc_scope1.xml");

        try {
            create(SCOPE_HANDLER_CODE, toBeCreatedXml);
            EscidocAbstractTest.failMissingException("Creating scope not declined.", ec);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Creating scope not declined, properly.", ec, e);
        }
    }

    /**
     * Tests creating a user-grant with no scope.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefCreateGrantWithoutScopeDecline() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            doTestCreateGrant(PWCallback.TEST_HANDLE, userId, null, ROLE_HREF_MD_EDITOR, AuthorizationException.class);
        }
        finally {
            revokeAllGrants(userId);
        }
    }

    /**
     * Tests creating a user-grant with scope on an item the user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefCreateGrantOnItem() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            doTestCreateGrant(PWCallback.TEST_HANDLE, userId, itemHref, ROLE_HREF_COLLABORATOR, null);
        }
        finally {
            revokeAllGrants(userId);
        }
    }

    /**
     * Tests creating a user-grant with scope on an container the user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefCreateGrantOnContainer() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            doTestCreateGrant(PWCallback.TEST_HANDLE, userId, containerHref, ROLE_HREF_COLLABORATOR, null);
        }
        finally {
            revokeAllGrants(userId);
        }
    }

    /**
     * Tests creating a user-grant with scope on an component the user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefCreateGrantOnComponent() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            doTestCreateGrant(PWCallback.TEST_HANDLE, userId, publicComponentHref, ROLE_HREF_COLLABORATOR, null);
        }
        finally {
            revokeAllGrants(userId);
        }
    }

    /**
     * Tests creating a user-grant for Role User-Inspector with scope on own account.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefCreateUserInspectorGrantForOwnUserAccount() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            doTestCreateGrant(PWCallback.TEST_HANDLE, userId, Constants.USER_ACCOUNT_BASE_URI + "/"
                + TEST_USER_ACCOUNT_ID, ROLE_HREF_USER_ACCOUNT_INSPECTOR, null);
        }
        finally {
            revokeAllGrants(userId);
        }
    }

    /**
     * Tests declining creating a user-grant with scope on an item the user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefCreateGrantOnItemDecline() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            doTestCreateGrant(PWCallback.TEST_HANDLE, userId, itemHref1, ROLE_HREF_COLLABORATOR,
                AuthorizationException.class);
        }
        finally {
            revokeAllGrants(userId);
        }
    }

    /**
     * Tests declining creating a user-grant with scope on an container the user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefCreateGrantOnContainerDecline() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            doTestCreateGrant(PWCallback.TEST_HANDLE, userId, containerHref1, ROLE_HREF_COLLABORATOR,
                AuthorizationException.class);
        }
        finally {
            revokeAllGrants(userId);
        }
    }

    /**
     * Tests declining creating a user-grant with scope on an component the user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefCreateGrantOnComponentDecline() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            doTestCreateGrant(PWCallback.TEST_HANDLE, userId, publicComponentHref1, ROLE_HREF_COLLABORATOR,
                AuthorizationException.class);
        }
        finally {
            revokeAllGrants(userId);
        }

    }

    /**
     * Tests declining creating a user-grant for Role User-Inspector with scope not on own account.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefCreateUserInspectorGrantForDifferentUserAccountDecline() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            doTestCreateGrant(PWCallback.TEST_HANDLE, userId, Constants.USER_ACCOUNT_BASE_URI + "/"
                + TEST_USER_ACCOUNT_ID1, ROLE_HREF_USER_ACCOUNT_INSPECTOR, AuthorizationException.class);
        }
        finally {
            revokeAllGrants(userId);
        }
    }

    /**
     * Tests declining creating a user-grant for Role Not User-Inspector with scope on own account.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefCreateNonUserInspectorGrantForOwnUserAccountDecline() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            doTestCreateGrant(PWCallback.TEST_HANDLE, userId, Constants.USER_ACCOUNT_BASE_URI + "/"
                + TEST_USER_ACCOUNT_ID, ROLE_HREF_ADMINISTRATOR, AuthorizationException.class);
        }
        finally {
            revokeAllGrants(userId);
        }
    }

    /**
     * Tests creating a group-grant with scope on an item the user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefCreateGroupGrantOnItem() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            doTestCreateGrant(PWCallback.TEST_HANDLE, groupId, itemHref, ROLE_HREF_COLLABORATOR, null);
        }
        finally {
            revokeAllGrants(groupId);
        }
    }

    /**
     * Tests creating a group-grant with scope on an container the user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefCreateGroupGrantOnContainer() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            doTestCreateGrant(PWCallback.TEST_HANDLE, groupId, containerHref, ROLE_HREF_COLLABORATOR, null);
        }
        finally {
            revokeAllGrants(groupId);
        }
    }

    /**
     * Tests creating a group-grant with scope on an component the user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefCreateGroupGrantOnComponent() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            doTestCreateGrant(PWCallback.TEST_HANDLE, groupId, publicComponentHref, ROLE_HREF_COLLABORATOR, null);
        }
        finally {
            revokeAllGrants(groupId);
        }
    }

    /**
     * Tests creating a group-grant for Role User-Inspector with scope on own account.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefCreateUserInspectorGroupGrantForOwnUserAccount() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            doTestCreateGrant(PWCallback.TEST_HANDLE, groupId, Constants.USER_ACCOUNT_BASE_URI + "/"
                + TEST_USER_ACCOUNT_ID, ROLE_HREF_USER_ACCOUNT_INSPECTOR, null);
        }
        finally {
            revokeAllGrants(groupId);
        }
    }

    /**
     * Tests declining creating a group-grant with scope on an item the user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefCreateGroupGrantOnItemDecline() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            doTestCreateGrant(PWCallback.TEST_HANDLE, groupId, itemHref1, ROLE_HREF_COLLABORATOR,
                AuthorizationException.class);
        }
        finally {
            revokeAllGrants(groupId);
        }
    }

    /**
     * Tests declining creating a group-grant with scope on an container the user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefCreateGroupGrantOnContainerDecline() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            doTestCreateGrant(PWCallback.TEST_HANDLE, groupId, containerHref1, ROLE_HREF_COLLABORATOR,
                AuthorizationException.class);
        }
        finally {
            revokeAllGrants(groupId);
        }
    }

    /**
     * Tests declining creating a group-grant with scope on an component the user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefCreateGroupGrantOnComponentDecline() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            doTestCreateGrant(PWCallback.TEST_HANDLE, groupId, publicComponentHref1, ROLE_HREF_COLLABORATOR,
                AuthorizationException.class);
        }
        finally {
            revokeAllGrants(groupId);
        }
    }

    /**
     * Tests declining creating a group-grant for Role User-Inspector with scope not on own account.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefCreateUserInspectorGroupGrantForDifferentUserAccountDecline() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            doTestCreateGrant(PWCallback.TEST_HANDLE, groupId, Constants.USER_ACCOUNT_BASE_URI + "/"
                + TEST_USER_ACCOUNT_ID1, ROLE_HREF_USER_ACCOUNT_INSPECTOR, AuthorizationException.class);
        }
        finally {
            revokeAllGrants(groupId);
        }
    }

    /**
     * Tests declining creating a user-grant for Role Not User-Inspector with scope on own account.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefCreateNonUserInspectorGroupGrantForOwnUserAccountDecline() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            doTestCreateGrant(PWCallback.TEST_HANDLE, groupId, Constants.USER_ACCOUNT_BASE_URI + "/"
                + TEST_USER_ACCOUNT_ID, ROLE_HREF_MD_EDITOR, AuthorizationException.class);
        }
        finally {
            revokeAllGrants(groupId);
        }
    }

    /**
     * Tests revoking a user-grant the user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefRevokeGrantOnItem() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            String grantXml = doTestCreateGrant(PWCallback.TEST_HANDLE, userId, itemHref, ROLE_HREF_COLLABORATOR, null);
            String grantId = getObjidValue(grantXml);
            doTestRevokeGrant(PWCallback.TEST_HANDLE, userId, grantId, null);
        }
        finally {
            revokeAllGrants(userId);
        }
    }

    /**
     * Tests revoking a user-grant the user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefRevokeGrantOnContainer() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            String grantXml =
                doTestCreateGrant(PWCallback.TEST_HANDLE, userId, containerHref, ROLE_HREF_COLLABORATOR, null);
            String grantId = getObjidValue(grantXml);
            doTestRevokeGrant(PWCallback.TEST_HANDLE, userId, grantId, null);
        }
        finally {
            revokeAllGrants(userId);
        }
    }

    /**
     * Tests revoking a user-grant the user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefRevokeGrantOnComponent() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            String grantXml =
                doTestCreateGrant(PWCallback.TEST_HANDLE, userId, publicComponentHref, ROLE_HREF_COLLABORATOR, null);
            String grantId = getObjidValue(grantXml);
            doTestRevokeGrant(PWCallback.TEST_HANDLE, userId, grantId, null);
        }
        finally {
            revokeAllGrants(userId);
        }
    }

    /**
     * Tests revoking a user-grant another user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefRevokeGrantOnItemDecline() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            String grantXml = doTestCreateGrant(PWCallback.TEST_HANDLE, userId, itemHref, ROLE_HREF_COLLABORATOR, null);
            String grantId = getObjidValue(grantXml);
            doTestRevokeGrant(PWCallback.TEST_HANDLE1, userId, grantId, AuthorizationException.class);
        }
        finally {
            revokeAllGrants(userId);
        }
    }

    /**
     * Tests revoking a user-grant another user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefRevokeGrantOnContainerDecline() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            String grantXml =
                doTestCreateGrant(PWCallback.TEST_HANDLE, userId, containerHref, ROLE_HREF_COLLABORATOR, null);
            String grantId = getObjidValue(grantXml);
            doTestRevokeGrant(PWCallback.TEST_HANDLE1, userId, grantId, AuthorizationException.class);
        }
        finally {
            revokeAllGrants(userId);
        }
    }

    /**
     * Tests revoking a user-grant another user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefRevokeGrantOnComponentDecline() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            String grantXml =
                doTestCreateGrant(PWCallback.TEST_HANDLE, userId, publicComponentHref, ROLE_HREF_COLLABORATOR, null);
            String grantId = getObjidValue(grantXml);
            doTestRevokeGrant(PWCallback.TEST_HANDLE1, userId, grantId, AuthorizationException.class);
        }
        finally {
            revokeAllGrants(userId);
        }
    }

    /**
     * Tests revoking a group-grant the user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefRevokeGroupGrantOnItem() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            String grantXml =
                doTestCreateGrant(PWCallback.TEST_HANDLE, groupId, itemHref, ROLE_HREF_COLLABORATOR, null);
            String grantId = getObjidValue(grantXml);
            doTestRevokeGrant(PWCallback.TEST_HANDLE, groupId, grantId, null);
        }
        finally {
            revokeAllGrants(groupId);
        }
    }

    /**
     * Tests revoking a group-grant the user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefRevokeGroupGrantOnContainer() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            String grantXml =
                doTestCreateGrant(PWCallback.TEST_HANDLE, groupId, containerHref, ROLE_HREF_COLLABORATOR, null);
            String grantId = getObjidValue(grantXml);
            doTestRevokeGrant(PWCallback.TEST_HANDLE, groupId, grantId, null);
        }
        finally {
            revokeAllGrants(groupId);
        }
    }

    /**
     * Tests revoking a group-grant the user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefRevokeGroupGrantOnComponent() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            String grantXml =
                doTestCreateGrant(PWCallback.TEST_HANDLE, groupId, publicComponentHref, ROLE_HREF_COLLABORATOR, null);
            String grantId = getObjidValue(grantXml);
            doTestRevokeGrant(PWCallback.TEST_HANDLE, groupId, grantId, null);
        }
        finally {
            revokeAllGrants(groupId);
        }
    }

    /**
     * Tests revoking a group-grant another user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefRevokeGroupGrantOnItemDecline() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            String grantXml =
                doTestCreateGrant(PWCallback.TEST_HANDLE, groupId, itemHref, ROLE_HREF_COLLABORATOR, null);
            String grantId = getObjidValue(grantXml);
            doTestRevokeGrant(PWCallback.TEST_HANDLE1, groupId, grantId, AuthorizationException.class);
        }
        finally {
            revokeAllGrants(groupId);
        }
    }

    /**
     * Tests revoking a group-grant another user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefRevokeGroupGrantOnContainerDecline() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            String grantXml =
                doTestCreateGrant(PWCallback.TEST_HANDLE, groupId, containerHref, ROLE_HREF_COLLABORATOR, null);
            String grantId = getObjidValue(grantXml);
            doTestRevokeGrant(PWCallback.TEST_HANDLE1, groupId, grantId, AuthorizationException.class);
        }
        finally {
            revokeAllGrants(groupId);
        }
    }

    /**
     * Tests revoking a group-grant another user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefRevokeGroupGrantOnComponentDecline() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            String grantXml =
                doTestCreateGrant(PWCallback.TEST_HANDLE, groupId, publicComponentHref, ROLE_HREF_COLLABORATOR, null);
            String grantId = getObjidValue(grantXml);
            doTestRevokeGrant(PWCallback.TEST_HANDLE1, groupId, grantId, AuthorizationException.class);
        }
        finally {
            revokeAllGrants(groupId);
        }
    }

    /**
     * Tests retrieving a grant of himself.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefRetrieveGrantHimself() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            String grantXml =
                doTestCreateGrant(PWCallback.DEFAULT_HANDLE, TEST_USER_ACCOUNT_ID, Constants.CONTEXT_BASE_URI + "/"
                    + CONTEXT_ID, ROLE_HREF_MD_EDITOR, null);
            String grantId = getObjidValue(grantXml);
            doTestRetrieveGrant(PWCallback.TEST_HANDLE, TEST_USER_ACCOUNT_ID, grantId, null);
        }
        finally {
            revokeAllGrants(TEST_USER_ACCOUNT_ID);
        }
    }

    /**
     * Tests retrieving a grant user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefRetrieveGrant() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            String grantXml = doTestCreateGrant(PWCallback.TEST_HANDLE, userId, itemHref, ROLE_HREF_COLLABORATOR, null);
            String grantId = getObjidValue(grantXml);
            doTestRetrieveGrant(PWCallback.TEST_HANDLE, userId, grantId, null);
        }
        finally {
            revokeAllGrants(userId);
        }
    }

    /**
     * Tests declining retrieving a grant user did not create.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefRetrieveGrantDecline() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
            String grantXml =
                doTestCreateGrant(PWCallback.DEFAULT_HANDLE, userId, publicComponentHref, ROLE_HREF_COLLABORATOR, null);
            String grantId = getObjidValue(grantXml);
            doTestRetrieveGrant(PWCallback.TEST_HANDLE, userId, grantId, AuthorizationException.class);
        }
        finally {
            revokeAllGrants(userId);
        }
    }

    /**
     * Tests retrieving a group-grant of a group user is member.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefRetrieveGroupGrant() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            revokeAllGrants(groupId1);
            String grantXml =
                doTestCreateGrant(PWCallback.DEFAULT_HANDLE, groupId1, itemHref1, ROLE_HREF_COLLABORATOR, null);
            String grantId = getObjidValue(grantXml);
            doTestRetrieveGrant(PWCallback.TEST_HANDLE1, groupId1, grantId, null);
        }
        finally {
            revokeAllGrants(groupId1);
        }
    }

    /**
     * Tests retrieving a group-grant user created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefRetrieveGroupGrant1() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            revokeAllGrants(TEST_USER_GROUP_ID);
            String grantXml =
                doTestCreateGrant(PWCallback.TEST_HANDLE1, TEST_USER_GROUP_ID, itemHref1, ROLE_HREF_COLLABORATOR, null);
            String grantId = getObjidValue(grantXml);
            doTestRetrieveGrant(PWCallback.TEST_HANDLE1, TEST_USER_GROUP_ID, grantId, null);
        }
        finally {
            revokeAllGrants(TEST_USER_GROUP_ID);
        }
    }

    /**
     * Tests retrieving a group-grant of a group where another group is member. user is member of the other group
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefRetrieveHierarchyGroupGrant() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            String grantXml =
                doTestCreateGrant(PWCallback.DEFAULT_HANDLE, groupId, publicComponentHref, ROLE_HREF_COLLABORATOR, null);
            String grantId = getObjidValue(grantXml);
            doTestRetrieveGrant(PWCallback.TEST_HANDLE1, groupId, grantId, null);
        }
        finally {
            revokeAllGrants(groupId);
        }
    }

    /**
     * Tests declining retrieving a group-grant of a group user is not member.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefRetrieveGroupGrantDecline() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            String grantXml =
                doTestCreateGrant(PWCallback.DEFAULT_HANDLE, groupId1, publicComponentHref, ROLE_HREF_COLLABORATOR,
                    null);
            String grantId = getObjidValue(grantXml);
            doTestRetrieveGrant(PWCallback.TEST_HANDLE, groupId1, grantId, AuthorizationException.class);
        }
        finally {
            revokeAllGrants(groupId1);
        }
    }

    /**
     * Tests declining retrieving a group-grant user did not create.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefRetrieveGroupGrantDecline1() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            String grantXml =
                doTestCreateGrant(PWCallback.DEFAULT_HANDLE, TEST_USER_GROUP_ID, itemHref1, ROLE_HREF_COLLABORATOR,
                    null);
            String grantId = getObjidValue(grantXml);
            doTestRetrieveGrant(PWCallback.TEST_HANDLE, TEST_USER_GROUP_ID, grantId, AuthorizationException.class);
        }
        finally {
            revokeAllGrants(TEST_USER_GROUP_ID);
        }
    }

    /**
     * Tests declining retrieving a group-grant of a group where another group is member. testuser1 is member of the
     * other group, but not testuser
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefRetrieveHierarchyGroupGrantDecline() throws Exception {
        try {
            super.setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            String grantXml =
                doTestCreateGrant(PWCallback.DEFAULT_HANDLE, groupId, publicComponentHref, ROLE_HREF_COLLABORATOR, null);
            String grantId = getObjidValue(grantXml);
            doTestRetrieveGrant(PWCallback.TEST_HANDLE, groupId, grantId, AuthorizationException.class);
        }
        finally {
            revokeAllGrants(groupId);
        }
    }

    /**
     * Tests evaluating requests for anonymous user
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefAnonymousEvaluateAnonymous() throws Exception {
        try {
            PWCallback.setAnonymousHandle();
            String requestsXml = EscidocAbstractTest.getTemplateAsString(TEMPLATE_REQUESTS_PATH, "requests2.xml");
            assertXmlValidRequests(requestsXml);
            String responseXml = handleXmlResult(getPolicyDecisionPointClient().evaluate(requestsXml));
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
    }

    /**
     * Tests declining evaluating requests for anonymous user
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefAnonymousEvaluateUserDecline() throws Exception {
        try {
            PWCallback.setAnonymousHandle();
            String requestsXml = EscidocAbstractTest.getTemplateAsString(TEMPLATE_REQUESTS_PATH, "requests1.xml");
            assertXmlValidRequests(requestsXml);
            String responseXml = handleXmlResult(getPolicyDecisionPointClient().evaluate(requestsXml));
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
     * Tests evaluating requests for anonymous user
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefUserEvaluateAnonymous() throws Exception {
        try {
            revokeAllGrants(TEST_USER_ACCOUNT_ID);
            PWCallback.setHandle(PWCallback.TEST_HANDLE);
            String requestsXml = EscidocAbstractTest.getTemplateAsString(TEMPLATE_REQUESTS_PATH, "requests2.xml");
            assertXmlValidRequests(requestsXml);
            String responseXml = handleXmlResult(getPolicyDecisionPointClient().evaluate(requestsXml));
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
    }

    /**
     * Tests declining evaluating requests for anonymous user
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefUserEvaluateUser() throws Exception {
        try {
            revokeAllGrants(TEST_USER_ACCOUNT_ID);
            PWCallback.setHandle(PWCallback.TEST_HANDLE);
            String requestsXml = EscidocAbstractTest.getTemplateAsString(TEMPLATE_REQUESTS_PATH, "requests1.xml");
            assertXmlValidRequests(requestsXml);
            String responseXml = handleXmlResult(getPolicyDecisionPointClient().evaluate(requestsXml));
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
    }

    /**
     * Tests declining evaluating requests for anonymous user
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDefUserEvaluateUserDecline() throws Exception {
        try {
            revokeAllGrants(TEST_USER_ACCOUNT_ID);
            PWCallback.setHandle(PWCallback.TEST_HANDLE);
            String requestsXml = EscidocAbstractTest.getTemplateAsString(TEMPLATE_REQUESTS_PATH, "requests.xml");
            assertXmlValidRequests(requestsXml);
            String responseXml = handleXmlResult(getPolicyDecisionPointClient().evaluate(requestsXml));
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
     * Test declining retrieving a content of an item in state pending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveContentOfItemPendingDecline() throws Exception {

        doTestRetrieveContent(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_PENDING, false, false, null, VISIBILITY_PUBLIC,
            AuthorizationException.class);

        doTestRetrieveContent(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_PENDING, false, false, "1", VISIBILITY_PUBLIC,
            AuthorizationException.class);
    }

    /**
     * Test declining retrieving a content of an item in state submitted.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveContentOfItemSubmittedDecline() throws Exception {

        doTestRetrieveContent(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, true, true, null, VISIBILITY_PUBLIC,
            AuthorizationException.class);

        doTestRetrieveContent(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, true, true, "2", VISIBILITY_PUBLIC,
            AuthorizationException.class);

        doTestRetrieveContent(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, false, false, null,
            VISIBILITY_PUBLIC, AuthorizationException.class);

        doTestRetrieveContent(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, false, false, "1",
            VISIBILITY_PUBLIC, AuthorizationException.class);
    }

    /**
     * Test successfully retrieving a content of an item in state released.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveContentOfItemReleased() throws Exception {

        doTestRetrieveContent(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_RELEASED, true, true, null, VISIBILITY_PUBLIC,
            null);

        doTestRetrieveContent(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_RELEASED, true, true, "4", VISIBILITY_PUBLIC,
            null);

        doTestRetrieveContent(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_RELEASED, false, false, null,
            VISIBILITY_PUBLIC, null);

        doTestRetrieveContent(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_RELEASED, false, false, "1", VISIBILITY_PUBLIC,
            null);
    }

    /**
     * Test declining retrieving a content of an item in state withdrawn.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveContentOfItemWithdrawnDecline() throws Exception {

        doTestRetrieveContent(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_WITHDRAWN, false, false, null,
            VISIBILITY_PUBLIC, AuthorizationException.class);

        doTestRetrieveContent(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_WITHDRAWN, false, false, "1",
            VISIBILITY_PUBLIC, AuthorizationException.class);
    }

    /**
     * Test retrieving content of an item in state pending after release (retrieve successfully last released version).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveContentOfReleasedItemUpdated() throws Exception {
        doTestRetrieveContent(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_RELEASED_UPDATED, true, false, null,
            VISIBILITY_PUBLIC, null);
    }

    /**
     * Test declining retrieving a private content of an item in state released.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrievePrivateContentOfItemDecline() throws Exception {

        doTestRetrieveContent(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_RELEASED, false, false, null,
            VISIBILITY_PRIVATE, AuthorizationException.class);

        doTestRetrieveContent(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_RELEASED, false, false, "1",
            VISIBILITY_PRIVATE, AuthorizationException.class);
    }

    /**
     * Test declining retrieving a audience content of an item in state released.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveAudienceContentOfItemDecline() throws Exception {

        doTestRetrieveContent(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_RELEASED, false, false, null,
            VISIBILITY_AUDIENCE, AuthorizationException.class);

        doTestRetrieveContent(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_RELEASED, false, false, "1",
            VISIBILITY_AUDIENCE, AuthorizationException.class);
    }

}
