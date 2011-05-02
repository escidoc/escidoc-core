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

import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Test suite for the role Audience.
 *
 * @author Michael Hoppe
 */
public class AudienceAbstractTest extends GrantTestBase {

    protected static final String HANDLE = PWCallback.TEST_HANDLE;

    protected static final String LOGINNAME = HANDLE;

    protected static final String PASSWORD = PWCallback.PASSWORD;

    protected static String grantCreationUserOrGroupId = null;

    protected static int methodCounter = 0;

    protected static String contextId = null;

    protected static String contextHref = null;

    protected static String itemId = null;

    protected static String itemHref = null;

    protected static String containerId = null;

    protected static String containerHref = null;

    protected static String publicComponentId = null;

    protected static String publicComponentHref = null;

    protected static String privateComponentId = null;

    protected static String privateComponentHref = null;

    protected static String audienceComponentId = null;

    protected static String audienceComponentHref = null;

    /**
     * The constructor.
     *
     * @param transport     The transport identifier.
     * @param handlerCode   handlerCode of either UserAccountHandler or UserGroupHandler.
     * @param userOrGroupId userOrGroupId for grantCreation.
     * @throws Exception If anything fails.
     */
    public AudienceAbstractTest(final int transport, final int handlerCode, final String userOrGroupId)
        throws Exception {
        super(transport, handlerCode);
        grantCreationUserOrGroupId = userOrGroupId;
    }

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void initialize() throws Exception {
        revokeAllGrants(grantCreationUserOrGroupId);
        if (methodCounter == 0) {
            prepare();
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
        revokeAllGrants(grantCreationUserOrGroupId);
    }

    /**
     * insert data into system for the tests.
     *
     * @throws Exception If anything fails.
     */
    protected void prepare() throws Exception {

        //create container
        String containerXml = prepareContainer(PWCallback.DEFAULT_HANDLE, STATUS_RELEASED, null, false, false);
        Document containerDocument = EscidocRestSoapTestBase.getDocument(containerXml);
        containerId = getObjidValue(containerDocument);
        containerHref = Constants.CONTAINER_BASE_URI + "/" + containerId;

        //create item in status released
        String itemXml = prepareItem(PWCallback.DEFAULT_HANDLE, STATUS_RELEASED, null, false, false);
        Document document = EscidocRestSoapTestBase.getDocument(itemXml);

        //save ids
        contextId = extractContextId(document);
        contextHref = Constants.CONTEXT_BASE_URI + "/" + contextId;
        itemId = getObjidValue(document);
        itemHref = Constants.ITEM_BASE_URI + "/" + itemId;
        publicComponentId = extractComponentId(document, VISIBILITY_PUBLIC);
        publicComponentHref = itemHref + "/" + Constants.SUB_COMPONENT + "/" + publicComponentId;
        privateComponentId = extractComponentId(document, VISIBILITY_PRIVATE);
        privateComponentHref = itemHref + "/" + Constants.SUB_COMPONENT + "/" + privateComponentId;
        audienceComponentId = extractComponentId(document, VISIBILITY_AUDIENCE);
        audienceComponentHref = itemHref + "/" + Constants.SUB_COMPONENT + "/" + audienceComponentId;

        //add item to container
        String lastModificationDate = getLastModificationDateValue(containerDocument);
        String taskParam =
            "<param last-modification-date=\"" + lastModificationDate + "\"><id>" + itemId + "</id></param>";
        getContainerClient().addMembers(containerId, taskParam);

    }

    /**
     * Test logging out a collaborator.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaCollaboratorLogout() throws Exception {

        doTestLogout(LOGINNAME, PASSWORD);
    }
}
