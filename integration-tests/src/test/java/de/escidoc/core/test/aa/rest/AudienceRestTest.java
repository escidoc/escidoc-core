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

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.escidoc.core.common.exceptions.remote.application.security.AuthorizationException;
import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.aa.AudienceAbstractTest;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.om.ItemClient;
import de.escidoc.core.test.security.client.PWCallback;

/**
 * Test suite for the role Audience using the REST interface.
 * 
 * @author MIH
 * 
 */
@RunWith(Parameterized.class)
public class AudienceRestTest extends AudienceAbstractTest {

    /**
     * Initializes test-class with data.
     * 
     * @return Collection with data.
     * 
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {USER_ACCOUNT_HANDLER_CODE, 
                    PWCallback.ID_PREFIX + PWCallback.TEST_HANDLE},
                {USER_GROUP_HANDLER_CODE, USER_GROUP_WITH_GROUP_LIST_ID},
                {USER_GROUP_HANDLER_CODE, USER_GROUP_WITH_USER_LIST_ID},
                {USER_GROUP_HANDLER_CODE, USER_GROUP_WITH_OU_LIST_ID},
                {USER_GROUP_HANDLER_CODE, USER_GROUP_WITH_EXTERNAL_SELECTOR}
        });
    }

    /**
     * Constructor.
     * 
     * @param handlerCode handlerCode 
     *      of UserAccountHandler or UserGroupHandler
     * @param userOrGroupId
     *            userOrGroupId for grantCreation.
     * @throws Exception
     *             If anything fails.
     */
    public AudienceRestTest(final int handlerCode,
            final String userOrGroupId) throws Exception {

        super(Constants.TRANSPORT_REST, handlerCode, userOrGroupId);
    }

    /**
     * Test retrieving successfully a component 
     * with visibility='audience' as audience-user.
     * 
     * @test.name Audience - Retrieve Audience Component
     * @test.id AA-Audience-RetrieveAudienceComponent
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveContentSuccessfull() throws Exception {

        //create grant audience for user USER_ID and scope of component
        doTestCreateGrant(null, grantCreationUserOrGroupId, 
            audienceComponentHref, ROLE_HREF_AUDIENCE, null);
        
        //test successfully retrieving content
        try {
            PWCallback.setHandle(HANDLE);
            ((ItemClient) getClient(ITEM_HANDLER_CODE)).retrieveContent(
                    itemId, audienceComponentId);
        } 
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(
                    "retrieving content failed. ", e);
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
        
    }

    /**
     * Test retrieving declining a component 
     * with visibility='private' as audience-user.
     * 
     * @test.name Audience - Retrieve Private Component
     * @test.id AA-Audience-RetrievePrivateComponent
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveContentDecline() throws Exception {

        //create grant audience for user USER_ID and scope of component
        doTestCreateGrant(null, grantCreationUserOrGroupId, 
            audienceComponentHref, ROLE_HREF_AUDIENCE, null);
        
        //test declining retrieving content
        try {
            PWCallback.setHandle(HANDLE);
            ((ItemClient) getClient(ITEM_HANDLER_CODE)).retrieveContent(
                    itemId, privateComponentId);
            EscidocRestSoapTestsBase
                .failMissingException(AuthorizationException.class);
        } catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                AuthorizationException.class, e);
        } finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
        
    }

}
