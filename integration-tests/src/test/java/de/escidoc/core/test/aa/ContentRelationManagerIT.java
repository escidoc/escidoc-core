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
public class ContentRelationManagerIT extends GrantTestBase {

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
    public ContentRelationManagerIT(final int handlerCode, final String userOrGroupId) throws Exception {
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
            //create grant ContentRelationManager 
            //for user grantCreationUserOrGroupId 
            doTestCreateGrant(null, grantCreationUserOrGroupId, null, ROLE_HREF_CONTENT_RELATION_MANAGER, null);
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
     * Tests creating a content-relation by a content-relation-manager.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateContentRelation() throws Exception {
        doTestCreateContentRelation(HANDLE, null);
    }

    /**
     * Tests declining creating a content-relation by a anonymous user.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateContentRelationDecline() throws Exception {
        doTestCreateContentRelation(PWCallback.ANONYMOUS_HANDLE, AuthorizationException.class);
    }

    /**
     * Tests updating a content-relation by a content-relation-manager who has created the content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateContentRelation() throws Exception {
        doTestUpdateContentRelation(HANDLE, HANDLE, STATUS_PENDING, null, null, null);
    }

    /**
     * Tests declining updating a content-relation by a content-relation-manager who has not created the
     * content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateContentRelationDecline() throws Exception {
        doTestUpdateContentRelation(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_PENDING, null, null,
            AuthorizationException.class);
    }

    /**
     * Tests deleting a content-relation by a content-relation-manager who has created the content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeleteContentRelation() throws Exception {
        doTestDeleteContentRelation(HANDLE, HANDLE, STATUS_PENDING, null, null);
    }

    /**
     * Tests declining deleting a content-relation by a content-relation-manager who has not created the
     * content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeleteContentRelationDecline() throws Exception {
        doTestDeleteContentRelation(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_PENDING, null,
            AuthorizationException.class);
    }

    /**
     * Tests locking a content-relation by a content-relation-manager who has created the content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testLockContentRelation() throws Exception {
        doTestLockContentRelation(HANDLE, HANDLE, STATUS_SUBMITTED, null, null);
    }

    /**
     * Tests declining locking a content-relation by a content-relation-manager who has not created the
     * content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testLockContentRelationDecline() throws Exception {
        doTestLockContentRelation(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, null,
            AuthorizationException.class);
    }

    /**
     * Tests unlocking a content-relation by a content-relation-manager who has created the content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUnlockContentRelation() throws Exception {
        doTestUnlockContentRelation(HANDLE, HANDLE, STATUS_SUBMITTED, null, null);
    }

    /**
     * Tests declining unlocking a content-relation by a content-relation-manager who has not created the
     * content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUnlockContentRelationDecline() throws Exception {
        doTestUnlockContentRelation(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, null,
            AuthorizationException.class);
    }

    /**
     * Tests assigning an object-pid to a content-relation by a content-relation-manager who has created the
     * content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAssignObjectPidContentRelation() throws Exception {
        doTestContentRelationAssignObjectPid(HANDLE, HANDLE, STATUS_SUBMITTED, null, null);
    }

    /**
     * Tests declining assigning an object-pid to a content-relation by a content-relation-manager who has not created
     * the content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAssignObjectPidContentRelationDecline() throws Exception {
        doTestContentRelationAssignObjectPid(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, null,
            AuthorizationException.class);
    }

    /**
     * Tests submitting a content-relation by a content-relation-manager who has created the content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSubmitContentRelation() throws Exception {
        doTestSubmitContentRelation(HANDLE, HANDLE, null, null);
    }

    /**
     * Tests declining submitting a content-relation by a content-relation-manager who has not created the
     * content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSubmitContentRelationDecline() throws Exception {
        doTestSubmitContentRelation(HANDLE, PWCallback.DEFAULT_HANDLE, null, AuthorizationException.class);
    }

    /**
     * Tests releasing a content-relation by a content-relation-manager who has created the content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testReleaseContentRelation() throws Exception {
        doTestReleaseContentRelation(HANDLE, HANDLE, null, null);
    }

    /**
     * Tests declining releasing a content-relation by a content-relation-manager who has not created the
     * content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testReleaseContentRelationDecline() throws Exception {
        doTestReleaseContentRelation(HANDLE, PWCallback.DEFAULT_HANDLE, null, AuthorizationException.class);
    }

    /**
     * Tests revising a content-relation by a content-relation-manager who has created the content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testReviseContentRelation() throws Exception {
        doTestReviseContentRelation(HANDLE, HANDLE, null, null);
    }

    /**
     * Tests declining revising a content-relation by a content-relation-manager who has not created the
     * content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testReviseContentRelationDecline() throws Exception {
        doTestReviseContentRelation(HANDLE, PWCallback.DEFAULT_HANDLE, null, AuthorizationException.class);
    }

    /**
     * Tests creating a grant by a content-relation-manager for a content-relation the content-relation-manager
     * created.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateGrantForContentRelation() throws Exception {
        String createdXml = doTestCreateContentRelation(HANDLE, null);

        doTestCreateGrant(HANDLE, grantCreationUserOrGroupId, Constants.CONTENT_RELATION_BASE_URI + "/"
            + getObjidValue(createdXml), ROLE_HREF_CONTENT_RELATION_MODIFIER, null);
    }

    /**
     * Tests declining creating a grant by a content-relation-manager for a content-relation the
     * content-relation-manager did not create.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateGrantForContentRelationDecline() throws Exception {
        String createdXml = doTestCreateContentRelation(PWCallback.DEFAULT_HANDLE, null);

        doTestCreateGrant(HANDLE, grantCreationUserOrGroupId, Constants.CONTENT_RELATION_BASE_URI + "/"
            + getObjidValue(createdXml), ROLE_HREF_CONTENT_RELATION_MODIFIER, AuthorizationException.class);
    }

    /**
     * Tests retrieving a content-relation by a content-relation-manager who has created the content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveContentRelation() throws Exception {
        doTestRetrieveContentRelation(HANDLE, HANDLE, STATUS_SUBMITTED, null, false, null, null);
    }

    /**
     * Tests retrieving a released content-relation by a anonymous user.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveReleasedContentRelation() throws Exception {
        doTestRetrieveContentRelation(PWCallback.ANONYMOUS_HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_RELEASED, null,
            false, null, null);
    }

    /**
     * Tests declining retrieving a content-relation by a content-relation-manager who has not created the
     * content-relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveContentRelationDecline() throws Exception {
        doTestRetrieveContentRelation(HANDLE, PWCallback.DEFAULT_HANDLE, STATUS_SUBMITTED, null, false, null,
            AuthorizationException.class);
    }

}
