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

import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContainerNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.StagingFileNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.remote.application.violated.ReadonlyVersionException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.om.OmTestBase;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertNotNull;

/**
 * Test suite for the role depositor.
 *
 * @author Torsten Tetteroo
 *         <p/>
 *         revoked testAaDep1f as a depositor is not allowed to retrieve a role.
 */
@RunWith(Parameterized.class)
public class DepositorIT extends GrantTestBase {

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
    public DepositorIT(final int handlerCode, final String userOrGroupId) throws Exception {
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
            //create grant depositor for user grantCreationUserOrGroupId 
            //with scope on default-context
            doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
                ROLE_HREF_DEPOSITOR, null);
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
     * Tests successfully creating an item by a depositor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateItem() throws Exception {

        doTestCreateItem(HANDLE, null);
    }

    /**
     * Tests successfully retrieving an item by a depositor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveItem() throws Exception {

        doTestRetrieveItem(HANDLE, HANDLE, STATUS_PENDING, false, null, null);
    }

    /**
     * Tests declining retrieving an item by a depositor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineRetrieveItem() throws Exception {

        doTestRetrieveItem(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_PENDING, false, null, AuthorizationException.class);
    }

    /**
     * Tests successfully retrieving an item version by a depositor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveItemVersion() throws Exception {

        doTestRetrieveItem(HANDLE, HANDLE, STATUS_PENDING, false, "1", null);
    }

    /**
     * Tests successfully retrieving an item version by a depositor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveItemVersions() throws Exception {

        final String updatedXml = doTestUpdateItem(HANDLE, HANDLE, STATUS_PENDING, null, null);
        assertNotNull(updatedXml);
        final Document updatedDocument = EscidocAbstractTest.getDocument(updatedXml);
        final String objid = getObjidValue(updatedDocument);

        String headXml = null;
        try {
            headXml = retrieve(ITEM_HANDLER_CODE, objid);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }

        String version1Xml = null;
        try {
            version1Xml = retrieve(ITEM_HANDLER_CODE, objid + ":1");
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }

        String version2Xml = null;
        try {
            version2Xml = retrieve(ITEM_HANDLER_CODE, objid + ":2");
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }

        try {
            retrieve(ITEM_HANDLER_CODE, objid + ":3");
            EscidocAbstractTest.failMissingException(ItemNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ItemNotFoundException.class, e);
        }

    }

    /**
     * Test declining updating an unknown item by a depositor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateUnknownItemDecline() throws Exception {

        doTestUpdateItem(HANDLE, HANDLE, null, null, ItemNotFoundException.class);
    }

    /**
     * Tests successfully updating an item by a depositor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateItem() throws Exception {

        doTestUpdateItem(HANDLE, HANDLE, STATUS_PENDING, null, null);
    }

    /**
     * Tests successfully updating the latest version of an item by a depositor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateItemLatestVersion() throws Exception {

        doTestUpdateItem(HANDLE, HANDLE, STATUS_PENDING, "1", null);
    }

    /**
     * Tests declining updating an old version of an item by a depositor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateItemOldVersionDecline() throws Exception {

        final String updatedXml = doTestUpdateItem(HANDLE, HANDLE, STATUS_PENDING, "1", null);

        final String toBeUpdatedXml = updatedXml.replaceAll("semiconductor surfaces", "semiconductor surfaces u");

        try {
            update(ITEM_HANDLER_CODE,
                createResourceId(getObjidValue(EscidocAbstractTest.getDocument(updatedXml)), "1"), toBeUpdatedXml);
            EscidocAbstractTest.failMissingException(ReadonlyVersionException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ReadonlyVersionException.class, e);
        }
    }

    /**
     * Tests declining updating an item by a depositor that is not the creator.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateItemDecline() throws Exception {

        doTestUpdateItem(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_PENDING, null, AuthorizationException.class);
    }

    /**
     * Tests successfully deleting an item by a depositor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeleteItem() throws Exception {

        doTestDeleteItem(HANDLE, HANDLE, STATUS_PENDING, null);
    }

    /**
     * Tests declining deleting an item by a depositor that is not the creator.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeleteItemDecline() throws Exception {

        doTestDeleteItem(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_PENDING, AuthorizationException.class);
    }

    /**
     * Test declining submitting an unknown item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSubmitUnknownItemDecline() throws Exception {

        doTestSubmitItem(HANDLE, HANDLE, null, ItemNotFoundException.class);
    }

    /**
     * Tests successfully submitting an item by a depositor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSubmitItem() throws Exception {

        doTestSubmitItem(HANDLE, HANDLE, STATUS_PENDING, null);
    }

    /**
     * Tests successfully adding a member to a container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAddMember() throws Exception {
        String itemXml = prepareItem(HANDLE, STATUS_RELEASED, null, false, false);
        String itemId = getObjidValue(itemXml);

        String containerXml = prepareContainer(HANDLE, STATUS_RELEASED, CONTEXT_ID, false, false);
        String containerId = getObjidValue(containerXml);
        final Document container = EscidocAbstractTest.getDocument(containerXml);

        String lastModificationDate = getLastModificationDateValue(container);
        String taskParam =
            "<param last-modification-date=\"" + lastModificationDate + "\"><id>" + itemId + "</id></param>";
        try {
            PWCallback.setHandle(HANDLE);
            getContainerClient().addMembers(containerId, taskParam);
        }
        finally {
            PWCallback.resetHandle();
        }
    }

    /**
     * Test successfully creating a StagingFile by a depositor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateStagingFile() throws Exception {

        doTestCreateStagingFile(HANDLE, null);
    }

    // /**
    // * Test declining retrieving an user account by a depositor that is not
    // the
    // * owner of the user account.
    // *
    // * @throws Exception
    // * If anything fails.
    // */
    // FIXME: enable this test when a real user is used
    // public void testRetrieveUserAccountByIdDecline() throws Exception {
    //
    // doTestRetrieveUserAccount(HANDLE, null, "byId",
    // PWCallback.DEFAULT_HANDLE, STATUS_ACTIVE,
    // AuthorizationException.class);
    // }

    /**
     * Test retrieving own user account by a depositor using the account id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveOwnUserAccountById() throws Exception {

        if (isUserAccountTest) {
            doTestRetrieveUserAccount(HANDLE, grantCreationUserOrGroupId, null, null, null, null);
        }
    }

    /**
     * Test retrieving own user account by a depositor using the login name.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveOwnUserAccountByLoginName() throws Exception {

        if (isUserAccountTest) {
            doTestRetrieveUserAccount(HANDLE, LOGINNAME, null, null, null, null);
        }
    }

    /**
     * Test retrieving own user account by a depositor using the eSciDoc user handle.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveOwnUserAccountByHandle() throws Exception {

        if (isUserAccountTest) {
            doTestRetrieveUserAccount(HANDLE, HANDLE, null, null, null, null);
        }
    }

    /**
     * Test declining retrieving a container by providing an id of a existing resource of another resource type.
     *
     * @throws Exception Thrown if anything fails.
     */
    @Test
    public void testAaDep1a() throws Exception {

        PWCallback.setHandle(HANDLE);

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
    public void testAaDep1b() throws Exception {

        PWCallback.setHandle(HANDLE);

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
    public void testAaDep1c() throws Exception {

        PWCallback.setHandle(HANDLE);

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
    public void testAaDep1d() throws Exception {

        PWCallback.setHandle(HANDLE);

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
    public void testAaDep1e() throws Exception {

        PWCallback.setHandle(HANDLE);

        try {
            retrieve(ORGANIZATIONAL_UNIT_HANDLER_CODE, CONTEXT_ID);
            EscidocAbstractTest.failMissingException(OrganizationalUnitNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(OrganizationalUnitNotFoundException.class, e);
        }
    }

    /**
     * Test declining retrieving a staging file by providing an id of a existing resource of another resource type.
     *
     * @throws Exception Thrown if anything fails.
     */
    @Test
    public void testAaDep1g() throws Exception {

        PWCallback.setHandle(HANDLE);

        try {
            retrieve(STAGING_FILE_HANDLER_CODE, CONTEXT_ID);
            EscidocAbstractTest.failMissingException(StagingFileNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(StagingFileNotFoundException.class, e);
        }
    }

    /**
     * Test logging out a depositor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaDepositorLogout() throws Exception {

        doTestLogout(LOGINNAME, PASSWORD);
    }

    /**
     * Tests creating an item using a scenario for issue #333.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateItemIssue333() throws Exception {

        final Document toBeCreatedDocument =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        substitute(toBeCreatedDocument, OmTestBase.XPATH_ITEM_CONTEXT, createReferencingElementNode(
            toBeCreatedDocument, SREL_NS_URI, SREL_PREFIX_ESCIDOC, NAME_CONTEXT, XLINK_PREFIX_TEMPLATES, null,
            Constants.CONTEXT_BASE_URI + "/" + UNKNOWN_ID));
        final String toBeCreatedXml = toString(toBeCreatedDocument, false);

        doTestCreateItem(HANDLE, ContextNotFoundException.class, toBeCreatedXml);
    }

    /**
     * Tests creating an item using a scenario for issue #333.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateItemIssue333_2() throws Exception {

        final Document toBeCreatedDocument =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        substitute(toBeCreatedDocument, OmTestBase.XPATH_ITEM_CONTEXT, createReferencingElementNode(
            toBeCreatedDocument, SREL_NS_URI, SREL_PREFIX_ESCIDOC, NAME_CONTEXT, XLINK_PREFIX_TEMPLATES, null,
            Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/" + "escidoc:persistent1"));
        final String toBeCreatedXml = toString(toBeCreatedDocument, false);

        doTestCreateItem(HANDLE, ContextNotFoundException.class, toBeCreatedXml);
    }

    /**
     * Tests creating an item using a scenario for issue #333.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateItemIssue333_3() throws Exception {

        final Document toBeCreatedDocument =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        deleteElement(toBeCreatedDocument, OmTestBase.XPATH_ITEM_CONTEXT);
        final String toBeCreatedXml = toString(toBeCreatedDocument, false);

        doTestCreateItem(HANDLE, XmlCorruptedException.class, toBeCreatedXml);
    }

    /**
     * Tests declining creating a context by a depositor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineCreateContext() throws Exception {

        final Class<AuthorizationException> ec = AuthorizationException.class;
        final Document toBeCreatedDocument =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + "/rest", "context_create.xml");
        substitute(toBeCreatedDocument, "/context/properties/name", getUniqueName("PubMan Context "));
        final String toBeCreatedXml = toString(toBeCreatedDocument, false);
        try {
            PWCallback.setHandle(HANDLE);
            create(CONTEXT_HANDLER_CODE, toBeCreatedXml);
            failMissingException("Creating context by depositor not declined.", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Creating context by depositor not declined, properly.", ec, e);
        }
        finally {
            PWCallback.resetHandle();
        }
    }

    /**
     * Tests successfully retrieving an content by a depositor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveContent() throws Exception {

        doTestRetrieveContent(HANDLE, HANDLE, STATUS_PENDING, false, false, null, VISIBILITY_PUBLIC, null);
    }

    /**
     * Tests declining retrieving an content by a depositor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeclineRetrieveContent() throws Exception {

        doTestRetrieveContent(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_PENDING, false, false, null, VISIBILITY_PUBLIC,
            AuthorizationException.class);
    }
}
