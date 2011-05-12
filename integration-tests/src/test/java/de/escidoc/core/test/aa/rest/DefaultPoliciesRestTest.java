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
import de.escidoc.core.test.aa.DefaultPoliciesAbstractTest;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Test suite for the Default Policies using the REST interface.
 *
 * @author Torsten Tetteroo
 */
@RunWith(JUnit4.class)
public class DefaultPoliciesRestTest extends DefaultPoliciesAbstractTest {

    /**
     * The constructor.
     *
     * @throws Exception If anything fails.
     */
    public DefaultPoliciesRestTest() throws Exception {
        super();
    }

    // REST only tests

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
