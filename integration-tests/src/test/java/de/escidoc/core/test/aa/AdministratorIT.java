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
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;

import java.util.Arrays;
import java.util.Collection;

/**
 * Test suite for the role Administrator.
 *
 * @author Michael Hoppe
 */
@RunWith(Parameterized.class)
public class AdministratorIT extends GrantTestBase {

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

    /**
     * The constructor.
     *
     * @param handlerCode   handlerCode of either UserAccountHandler or UserGroupHandler.
     * @param userOrGroupId userOrGroupId for grantCreation.
     * @throws Exception If anything fails.
     */
    public AdministratorIT(final int handlerCode, final String userOrGroupId) throws Exception {
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
            //create grant administrator for user grantCreationUserOrGroupId 
            //with scope on default-context
            doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID,
                ROLE_HREF_ADMINISTRATOR, null);
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
     * Tests declining withdrawing an unknown item by an administrator.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testWithdrawUnknownItemDecline() throws Exception {

        doTestWithdrawItem(HANDLE, PWCallback.DEFAULT_HANDLE, null, ItemNotFoundException.class);
    }

    /**
     * Tests successfully withdrawing an item by an administrator.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testWithdrawItem() throws Exception {

        doTestWithdrawItem(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_RELEASED, null);
    }

    /**
     * Test retrieving user account by an administrator using the eSciDoc user handle.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveUserAccountById() throws Exception {

        doTestRetrieveUserAccount(HANDLE, null, "byId", PWCallback.DEFAULT_HANDLE, STATUS_ACTIVE, null);
    }

    /**
     * Test retrieving user account by an administrator using the eSciDoc user handle.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveUserAccountByLoginName() throws Exception {

        doTestRetrieveUserAccount(HANDLE, null, "byLoginName", PWCallback.DEFAULT_HANDLE, STATUS_ACTIVE, null);
    }

    /**
     * Test creating grant for an user account by an administrator.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateGrant() throws Exception {

        final String createdXml = doTestCreateItem(PWCallback.DEFAULT_HANDLE, null);
        final String itemHref = Constants.ITEM_BASE_URI + "/" + getObjidValue(createdXml);
        doTestCreateGrant(HANDLE, grantCreationUserOrGroupId, itemHref, ROLE_HREF_COLLABORATOR, null);
    }

    /**
     * Test creating grant for an user account by an administrator.<br> The grant is created on a container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateGrantContainer() throws Exception {

        final String createdXml = doTestCreateContainer(PWCallback.DEFAULT_HANDLE, null);
        final String containerHref = Constants.CONTAINER_BASE_URI + "/" + getObjidValue(createdXml);
        doTestCreateGrant(HANDLE, grantCreationUserOrGroupId, containerHref, ROLE_HREF_COLLABORATOR, null);
    }

    /**
     * Test logging out a depositor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaAdministratorLogout() throws Exception {

        doTestLogout(LOGINNAME, PASSWORD);
    }

    /**
     * Tests retrieving a context in status created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaAdministratorRc() throws Exception {

        // create context using System-Administrator
        PWCallback.resetHandle();

        String path = TEMPLATE_CONTEXT_PATH;
        path += "/rest";
        final Document toBeCreatedDocument = getTemplateAsDocument(path, "context_create.xml");
        substitute(toBeCreatedDocument, XPATH_CONTEXT_PROPERTIES_NAME, getUniqueName("Some Context "));
        final String toBeCreatedXml = toString(toBeCreatedDocument, false);

        String createdXml = null;
        try {
            createdXml = create(CONTEXT_HANDLER_CODE, toBeCreatedXml);
        }
        catch (final Exception e) {
            failException("Test init: Context creation failed.", e);
        }
        final String id = getObjidValue(createdXml);

        // grant administrator role on new context to user
        final Document toBeCreatedGrantDocument =
            getTemplateAsFixedGrantDocument(TEMPLATE_USER_ACCOUNT_PATH, "escidoc_grant_for_create.xml");
        substitute(toBeCreatedGrantDocument, XPATH_GRANT_OBJECT, createReferencingElementNode(toBeCreatedGrantDocument,
            GRANTS_NS_URI, SREL_PREFIX_TEMPLATES, NAME_ASSIGNED_ON, XLINK_PREFIX_TEMPLATES, "",
            Constants.CONTEXT_BASE_URI + "/" + id));
        final String toBeCreatedGrantXml = toString(toBeCreatedGrantDocument, false);

        try {
            createGrant(grantCreationUserOrGroupId, toBeCreatedGrantXml);
        }
        catch (final Exception e) {
            failException("Test init: Grant creation failed.", e);
        }

        // retrieve context
        PWCallback.setHandle(HANDLE);
        try {
            retrieve(CONTEXT_HANDLER_CODE, id);
        }
        catch (final Exception e) {
            failException("Retrieving created context by context's administrator failed", e);
        }

    }
}
