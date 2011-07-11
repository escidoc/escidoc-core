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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.test.examples;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.aa.RoleClient;
import de.escidoc.core.test.common.client.servlet.aa.UserAccountClient;
import de.escidoc.core.test.common.client.servlet.cmm.ContentModelClient;
import de.escidoc.core.test.common.client.servlet.om.ContextClient;
import de.escidoc.core.test.common.client.servlet.om.ItemClient;
import de.escidoc.core.test.common.client.servlet.oum.OrganizationalUnitClient;
import de.escidoc.core.test.common.resources.ResourceProvider;
import org.junit.Test;

/**
 * @author Michael Schneider
 */
public class RetrieveExamplesIT extends EscidocAbstractTest {

    private final String[] EXAMPLE_CONTEXT_IDS = { "escidoc:ex1" };

    private final String[] EXAMPLE_CONTENT_MODEL_IDS = { "escidoc:ex4" };

    private final String[] EXAMPLE_ITEM_IDS = { "escidoc:ex5" };

    private final String[] EXAMPLE_OU_IDS = { "escidoc:ex3" };

    private final String[] EXAMPLE_ROLE_IDS = { "escidoc:role-administrator", "escidoc:role-system-inspector" };

    private final String[] EXAMPLE_USER_ACCOUNT_IDS = { "escidoc:exuser1" };

    private final ContentModelClient cmClient = new ContentModelClient();

    private final ContextClient contextClient = new ContextClient();

    private final ItemClient itemClient = new ItemClient();

    private final OrganizationalUnitClient ouClient = new OrganizationalUnitClient();

    private final RoleClient roleClient = new RoleClient();

    private final UserAccountClient uaClient = new UserAccountClient();

    @Test
    public void testRetrieveExampleContexts() throws Exception {

        int noContexts = EXAMPLE_CONTEXT_IDS.length;
        for (int i = 0; i < noContexts; ++i) {
            String context = handleXmlResult(contextClient.retrieve(EXAMPLE_CONTEXT_IDS[i]));
            assertXmlValidContext(context);
            ResourceProvider.saveToFile(ESCIDOC_OBJECTS_SAVE_PATH, getFilename("context", EXAMPLE_CONTEXT_IDS[i],
                ".xml"), toString(getDocument(context), false));
        }
    }

    @Test
    public void testRetrieveExampleItems() throws Exception {

        int noItems = EXAMPLE_ITEM_IDS.length;
        for (int i = 0; i < noItems; ++i) {
            String item = handleXmlResult(itemClient.retrieve(EXAMPLE_ITEM_IDS[i]));
            assertXmlValidItem(item);
            ResourceProvider.saveToFile(ESCIDOC_OBJECTS_SAVE_PATH, getFilename("item", EXAMPLE_ITEM_IDS[i], ".xml"),
                toString(getDocument(item), false));
        }
    }

    @Test
    public void testRetrieveExampleOrganizationalUnits() throws Exception {

        int noOus = EXAMPLE_OU_IDS.length;
        for (int i = 0; i < noOus; ++i) {
            String ou = handleXmlResult(ouClient.retrieve(EXAMPLE_OU_IDS[i]));
            assertXmlValidOrganizationalUnit(ou);
            ResourceProvider.saveToFile(ESCIDOC_OBJECTS_SAVE_PATH, getFilename("ou", EXAMPLE_OU_IDS[i], ".xml"),
                toString(getDocument(ou), false));
        }
    }

    @Test
    public void testRetrieveExampleContentModels() throws Exception {

        int noContentModels = EXAMPLE_CONTENT_MODEL_IDS.length;
        for (int i = 0; i < noContentModels; ++i) {
            String contentModel = handleXmlResult(cmClient.retrieve(EXAMPLE_CONTENT_MODEL_IDS[i]));
            ResourceProvider.saveToFile(ESCIDOC_OBJECTS_SAVE_PATH, getFilename("content-model",
                EXAMPLE_CONTENT_MODEL_IDS[i], ".xml"), toString(getDocument(contentModel), false));
        }
    }

    @Test
    public void testRetrieveExampleUserAccounts() throws Exception {

        int noUserAccounts = EXAMPLE_USER_ACCOUNT_IDS.length;
        for (int i = 0; i < noUserAccounts; ++i) {
            String userAccount = handleXmlResult(uaClient.retrieve(EXAMPLE_USER_ACCOUNT_IDS[i]));
            assertXmlValidUserAccount(userAccount);
            ResourceProvider.saveToFile(ESCIDOC_OBJECTS_SAVE_PATH, getFilename("user-account",
                EXAMPLE_USER_ACCOUNT_IDS[i], ".xml"), toString(getDocument(userAccount), false));
        }
    }

    @Test
    public void testRetrieveExampleRoles() throws Exception {

        int noRoles = EXAMPLE_ROLE_IDS.length;
        for (int i = 0; i < noRoles; ++i) {
            String role = handleXmlResult(roleClient.retrieve(EXAMPLE_ROLE_IDS[i]));
            assertXmlValidRole(role);
            ResourceProvider.saveToFile(ESCIDOC_OBJECTS_SAVE_PATH, getFilename("role", EXAMPLE_ROLE_IDS[i], ".xml"),
                toString(getDocument(role), false));
        }
    }

    public static String getFilename(final String resource, final String id, final String extension) {
        String result = resource + "_" + id;
        if (!extension.startsWith(".")) {
            result += ".";
        }
        result += extension;
        return result.replaceAll(":", "_");

    }
}
