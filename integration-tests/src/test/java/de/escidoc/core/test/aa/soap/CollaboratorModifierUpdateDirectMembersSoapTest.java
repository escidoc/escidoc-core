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
package de.escidoc.core.test.aa.soap;

import de.escidoc.core.test.aa.CollaboratorModifierUpdateDirectMembersAbstractTest;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

/**
 * Test suite for the role CollaboratorModifierUpdateDirectMembers using the SOAP interface.
 * 
 * @author Michael Hoppe
 * 
 */
@RunWith(Parameterized.class)
public class CollaboratorModifierUpdateDirectMembersSoapTest 
            extends CollaboratorModifierUpdateDirectMembersAbstractTest {

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
    public CollaboratorModifierUpdateDirectMembersSoapTest(final int handlerCode,
            final String userOrGroupId) throws Exception {

        super(Constants.TRANSPORT_SOAP, handlerCode, userOrGroupId);
    }
}
