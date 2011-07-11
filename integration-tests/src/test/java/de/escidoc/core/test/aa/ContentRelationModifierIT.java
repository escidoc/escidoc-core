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
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Test suite for the role ContentRelationManager.
 *
 * @author Michael Hoppe
 */
@RunWith(Parameterized.class)
public class ContentRelationModifierIT extends GrantTestBase {

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
    public ContentRelationModifierIT(final int handlerCode, final String userOrGroupId) throws Exception {
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
        }
    }

    /**
     * Clean up after servlet test.
     *
     * @throws Exception If anything fails.
     */
    @After
    public void deinitialize() throws Exception {
        revokeAllGrants(grantCreationUserOrGroupId);
    }

    /**
     * Tests declining creating a content-relation by a anonymous user.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateContentRelationDecline() throws Exception {
        doTestCreateContentRelation(HANDLE, AuthorizationException.class);
    }

    /**
     * Tests updating a content-relation by a content-relation-modifier who has scope on content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateContentRelation() throws Exception {
        String createdXml = prepareContentRelation(PWCallback.DEFAULT_HANDLE, STATUS_PENDING, null, null, false, false);
        doTestCreateGrant(PWCallback.DEFAULT_HANDLE, grantCreationUserOrGroupId, Constants.CONTENT_RELATION_BASE_URI
            + "/" + getObjidValue(createdXml), ROLE_HREF_CONTENT_RELATION_MODIFIER, null);
        doTestUpdateContentRelation(HANDLE, null, STATUS_PENDING, getObjidValue(createdXml), null, null);
    }

    /**
     * Tests declining updating a content-relation by a content-relation-modifier who has no scope on content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateContentRelationDecline() throws Exception {
        String createdXml = prepareContentRelation(PWCallback.DEFAULT_HANDLE, STATUS_PENDING, null, null, false, false);
        doTestCreateGrant(PWCallback.DEFAULT_HANDLE, grantCreationUserOrGroupId, null,
            ROLE_HREF_CONTENT_RELATION_MODIFIER, null);
        doTestUpdateContentRelation(HANDLE, null, STATUS_PENDING, getObjidValue(createdXml), null,
            AuthorizationException.class);
    }

    /**
     * Tests deleting a content-relation by a content-relation-modifier who has scope on content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeleteContentRelation() throws Exception {
        String createdXml = prepareContentRelation(PWCallback.DEFAULT_HANDLE, STATUS_PENDING, null, null, false, false);
        doTestCreateGrant(PWCallback.DEFAULT_HANDLE, grantCreationUserOrGroupId, Constants.CONTENT_RELATION_BASE_URI
            + "/" + getObjidValue(createdXml), ROLE_HREF_CONTENT_RELATION_MODIFIER, null);
        doTestDeleteContentRelation(HANDLE, null, STATUS_PENDING, getObjidValue(createdXml), null);
    }

    /**
     * Tests declining deleting a content-relation by a content-relation-modifier who has no scope on content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeleteContentRelationDecline() throws Exception {
        String createdXml = prepareContentRelation(PWCallback.DEFAULT_HANDLE, STATUS_PENDING, null, null, false, false);
        doTestCreateGrant(PWCallback.DEFAULT_HANDLE, grantCreationUserOrGroupId, null,
            ROLE_HREF_CONTENT_RELATION_MODIFIER, null);
        doTestDeleteContentRelation(HANDLE, null, STATUS_PENDING, getObjidValue(createdXml),
            AuthorizationException.class);
    }

    /**
     * Tests locking a content-relation by a content-relation-modifier who has scope on content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testLockContentRelation() throws Exception {
        String createdXml =
            prepareContentRelation(PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, null, null, false, false);
        doTestCreateGrant(PWCallback.DEFAULT_HANDLE, grantCreationUserOrGroupId, Constants.CONTENT_RELATION_BASE_URI
            + "/" + getObjidValue(createdXml), ROLE_HREF_CONTENT_RELATION_MODIFIER, null);
        doTestLockContentRelation(HANDLE, null, STATUS_SUBMITTED, getObjidValue(createdXml), null);
    }

    /**
     * Tests declining locking a content-relation by a content-relation-modifier who has no scope on content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testLockContentRelationDecline() throws Exception {
        String createdXml =
            prepareContentRelation(PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, null, null, false, false);
        doTestCreateGrant(PWCallback.DEFAULT_HANDLE, grantCreationUserOrGroupId, null,
            ROLE_HREF_CONTENT_RELATION_MODIFIER, null);
        doTestLockContentRelation(HANDLE, null, STATUS_SUBMITTED, getObjidValue(createdXml),
            AuthorizationException.class);
    }

    /**
     * Tests unlocking a content-relation by a content-relation-modifier who has scope on content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUnlockContentRelation() throws Exception {
        String createdXml =
            prepareContentRelation(PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, null, null, false, false);
        doTestCreateGrant(PWCallback.DEFAULT_HANDLE, grantCreationUserOrGroupId, Constants.CONTENT_RELATION_BASE_URI
            + "/" + getObjidValue(createdXml), ROLE_HREF_CONTENT_RELATION_MODIFIER, null);
        doTestUnlockContentRelation(HANDLE, null, STATUS_SUBMITTED, getObjidValue(createdXml), null);
    }

    /**
     * Tests declining unlocking a content-relation by a content-relation-modifier who has no scope on
     * content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUnlockContentRelationDecline() throws Exception {
        String createdXml =
            prepareContentRelation(PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, null, null, false, false);
        doTestCreateGrant(PWCallback.DEFAULT_HANDLE, grantCreationUserOrGroupId, null,
            ROLE_HREF_CONTENT_RELATION_MODIFIER, null);
        doTestUnlockContentRelation(HANDLE, null, STATUS_SUBMITTED, getObjidValue(createdXml),
            AuthorizationException.class);
    }

    /**
     * Tests assigning an object-pid to a content-relation by a content-relation-modifier who has scope on
     * content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAssignObjectPidContentRelation() throws Exception {
        String createdXml =
            prepareContentRelation(PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, null, null, false, false);
        doTestCreateGrant(PWCallback.DEFAULT_HANDLE, grantCreationUserOrGroupId, Constants.CONTENT_RELATION_BASE_URI
            + "/" + getObjidValue(createdXml), ROLE_HREF_CONTENT_RELATION_MODIFIER, null);
        doTestContentRelationAssignObjectPid(HANDLE, null, STATUS_SUBMITTED, getObjidValue(createdXml), null);
    }

    /**
     * Tests declining assigning an object-pid to a content-relation by a content-relation-modifier who has no scope on
     * content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAssignObjectPidContentRelationDecline() throws Exception {
        String createdXml =
            prepareContentRelation(PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, null, null, false, false);
        doTestCreateGrant(PWCallback.DEFAULT_HANDLE, grantCreationUserOrGroupId, null,
            ROLE_HREF_CONTENT_RELATION_MODIFIER, null);
        doTestContentRelationAssignObjectPid(HANDLE, null, STATUS_SUBMITTED, getObjidValue(createdXml),
            AuthorizationException.class);
    }

    /**
     * Tests submitting a content-relation by a content-relation-modifier who has scope on content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSubmitContentRelation() throws Exception {
        String createdXml = prepareContentRelation(PWCallback.DEFAULT_HANDLE, STATUS_PENDING, null, null, false, false);
        doTestCreateGrant(PWCallback.DEFAULT_HANDLE, grantCreationUserOrGroupId, Constants.CONTENT_RELATION_BASE_URI
            + "/" + getObjidValue(createdXml), ROLE_HREF_CONTENT_RELATION_MODIFIER, null);
        doTestSubmitContentRelation(HANDLE, null, getObjidValue(createdXml), null);
    }

    /**
     * Tests declining submitting a content-relation by a content-relation-modifier who has no scope on
     * content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSubmitContentRelationDecline() throws Exception {
        String createdXml = prepareContentRelation(PWCallback.DEFAULT_HANDLE, STATUS_PENDING, null, null, false, false);
        doTestCreateGrant(PWCallback.DEFAULT_HANDLE, grantCreationUserOrGroupId, null,
            ROLE_HREF_CONTENT_RELATION_MODIFIER, null);
        doTestSubmitContentRelation(HANDLE, null, getObjidValue(createdXml), AuthorizationException.class);
    }

    /**
     * Tests releasing a content-relation by a content-relation-modifier who has scope on content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testReleaseContentRelation() throws Exception {
        String createdXml =
            prepareContentRelation(PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, null, null, false, false);
        doTestCreateGrant(PWCallback.DEFAULT_HANDLE, grantCreationUserOrGroupId, Constants.CONTENT_RELATION_BASE_URI
            + "/" + getObjidValue(createdXml), ROLE_HREF_CONTENT_RELATION_MODIFIER, null);
        doTestReleaseContentRelation(HANDLE, null, getObjidValue(createdXml), null);
    }

    /**
     * Tests declining releasing a content-relation by a content-relation-modifier who has no scope on
     * content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testReleaseContentRelationDecline() throws Exception {
        String createdXml =
            prepareContentRelation(PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, null, null, false, false);
        doTestCreateGrant(PWCallback.DEFAULT_HANDLE, grantCreationUserOrGroupId, null,
            ROLE_HREF_CONTENT_RELATION_MODIFIER, null);
        doTestReleaseContentRelation(HANDLE, null, getObjidValue(createdXml), AuthorizationException.class);
    }

    /**
     * Tests revising a content-relation by a content-relation-modifier who has scope on content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testReviseContentRelation() throws Exception {
        String createdXml =
            prepareContentRelation(PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, null, null, false, false);
        doTestCreateGrant(PWCallback.DEFAULT_HANDLE, grantCreationUserOrGroupId, Constants.CONTENT_RELATION_BASE_URI
            + "/" + getObjidValue(createdXml), ROLE_HREF_CONTENT_RELATION_MODIFIER, null);
        doTestReviseContentRelation(HANDLE, null, getObjidValue(createdXml), null);
    }

    /**
     * Tests declining revising a content-relation by a content-relation-modifier who has no scope on content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testReviseContentRelationDecline() throws Exception {
        String createdXml =
            prepareContentRelation(PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, null, null, false, false);
        doTestCreateGrant(PWCallback.DEFAULT_HANDLE, grantCreationUserOrGroupId, null,
            ROLE_HREF_CONTENT_RELATION_MODIFIER, null);
        doTestReviseContentRelation(HANDLE, null, getObjidValue(createdXml), AuthorizationException.class);
    }

    /**
     * Tests retrieving a content-relation by a content-relation-modifier who has scope on content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveContentRelation() throws Exception {
        String createdXml =
            prepareContentRelation(PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, null, null, false, false);
        doTestCreateGrant(PWCallback.DEFAULT_HANDLE, grantCreationUserOrGroupId, Constants.CONTENT_RELATION_BASE_URI
            + "/" + getObjidValue(createdXml), ROLE_HREF_CONTENT_RELATION_MODIFIER, null);
        doTestRetrieveContentRelation(HANDLE, null, STATUS_SUBMITTED, getObjidValue(createdXml), false, null, null);
    }

    /**
     * Tests declining retrieving a content-relation by a content-relation-modifier who has no scope on
     * content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveContentRelationDecline() throws Exception {
        String createdXml =
            prepareContentRelation(PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, null, null, false, false);
        doTestCreateGrant(PWCallback.DEFAULT_HANDLE, grantCreationUserOrGroupId, null,
            ROLE_HREF_CONTENT_RELATION_MODIFIER, null);
        doTestRetrieveContentRelation(HANDLE, null, STATUS_SUBMITTED, getObjidValue(createdXml), false, null,
            AuthorizationException.class);
    }

}
