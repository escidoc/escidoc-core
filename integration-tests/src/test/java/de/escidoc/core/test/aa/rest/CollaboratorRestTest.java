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
import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.aa.CollaboratorAbstractTest;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.om.ItemClient;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

/**
 * Test suite for the role Collaborator using the REST interface.
 *
 * @author Michael Hoppe
 */
@RunWith(Parameterized.class)
public class CollaboratorRestTest extends CollaboratorAbstractTest {

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
    public CollaboratorRestTest(final int handlerCode, final String userOrGroupId) throws Exception {

        super(Constants.TRANSPORT_REST, handlerCode, userOrGroupId);
    }

    /**
     * Test collaborator with scope on item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveContentWithItemScope() throws Exception {

        //create grant collaborator for user USER_ID and scope of item
        doTestCreateGrant(null, grantCreationUserOrGroupId, itemHref, ROLE_HREF_COLLABORATOR, null);

        //test successfully retrieving content
        try {
            PWCallback.setHandle(HANDLE);
            ((ItemClient) getClient(ITEM_HANDLER_CODE)).retrieveContent(itemId, privateComponentId);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException("retrieving content failed. ", e);
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }

    }

    /**
     * Test collaborator with scope on component.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveContentWithContentScope() throws Exception {

        //create grant collaborator for user USER_ID and scope of component
        doTestCreateGrant(null, grantCreationUserOrGroupId, privateComponentHref, ROLE_HREF_COLLABORATOR, null);

        //test successfully retrieving granted content
        try {
            PWCallback.setHandle(HANDLE);
            ((ItemClient) getClient(ITEM_HANDLER_CODE)).retrieveContent(itemId, privateComponentId);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException("retrieving content failed. ", e);
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }

        //test successfully retrieving granted content of version of item
        try {
            PWCallback.setHandle(HANDLE);
            ((ItemClient) getClient(ITEM_HANDLER_CODE)).retrieveContent(itemId + ":1", privateComponentId);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException("retrieving content failed. ", e);
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }

        //test declining retrieving non-granted content
        try {
            PWCallback.setHandle(HANDLE);
            ((ItemClient) getClient(ITEM_HANDLER_CODE)).retrieveContent(itemId, publicComponentId);
            EscidocRestSoapTestBase.failMissingException(AuthorizationException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(AuthorizationException.class, e);
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }

    }

    /**
     * Test declining collaborator with scope on container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveContentWithContainerScopeDecline() throws Exception {

        //create grant collaborator for user USER_ID and scope of container
        doTestCreateGrant(null, grantCreationUserOrGroupId, containerHref2, ROLE_HREF_COLLABORATOR, null);

        //test declining retrieving content
        try {
            PWCallback.setHandle(HANDLE);
            ((ItemClient) getClient(ITEM_HANDLER_CODE)).retrieveContent(itemId, privateComponentId);
            EscidocRestSoapTestBase.failMissingException(AuthorizationException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(AuthorizationException.class, e);
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
        doTestCreateGrant(null, grantCreationUserOrGroupId, containerHref, ROLE_HREF_COLLABORATOR, null);

        //test declining retrieving content
        try {
            PWCallback.setHandle(HANDLE);
            ((ItemClient) getClient(ITEM_HANDLER_CODE)).retrieveContent(itemId, privateComponentId);
            EscidocRestSoapTestBase.failMissingException(AuthorizationException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(AuthorizationException.class, e);
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }

    }

    /**
     * Test collaborator with scope on context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveContentWithContextScope() throws Exception {

        //create grant collaborator for user USER_ID and scope of context
        doTestCreateGrant(null, grantCreationUserOrGroupId, contextHref, ROLE_HREF_COLLABORATOR, null);

        //test successfully retrieving content
        try {
            PWCallback.setHandle(HANDLE);
            ((ItemClient) getClient(ITEM_HANDLER_CODE)).retrieveContent(itemId, privateComponentId);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException("retrieving content failed. ", e);
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
    public void testRetrieveContentWithNoScopeDecline() throws Exception {

        //create grant collaborator for user USER_ID and scope null
        doTestCreateGrant(null, grantCreationUserOrGroupId, null, ROLE_HREF_COLLABORATOR, null);

        //test declining retrieving content
        try {
            PWCallback.setHandle(HANDLE);
            ((ItemClient) getClient(ITEM_HANDLER_CODE)).retrieveContent(itemId, privateComponentId);
            EscidocRestSoapTestBase.failMissingException(AuthorizationException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(AuthorizationException.class, e);
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }

    }

}
