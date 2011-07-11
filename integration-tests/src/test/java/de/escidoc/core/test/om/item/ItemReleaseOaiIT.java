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
package de.escidoc.core.test.om.item;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test creates and releases items in order to fill a Escidoc repository and to test a Escidoc OAI-Provider.
 *
 * @author Rozita Friedman
 */
public class ItemReleaseOaiIT extends ItemTestBase {

    private String theItemXml;

    private String theItemId;

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void setUp() throws Exception {
        // create an item and save the id
        String xmlData =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create_withoutComponents_2Md-Records.xml");
        theItemXml = create(xmlData);
        theItemId = getObjidValue(theItemXml);

    }

    /**
     * Clean up after servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Override
    @After
    public void tearDown() throws Exception {

        super.tearDown();
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);

        // TODO purge object from Fedora
    }

    private String getTheLastModificationParam(boolean includeWithdrawComment) throws Exception {
        return getTheLastModificationParam(includeWithdrawComment, theItemId);
    }

    /**
     *
     * @throws Exception
     */
    // This test runs endless and without possibility to control it from outside.
    @Ignore("What should be test with this while-true test?")
    @Test
    public void testReleaseItem() throws Exception {
        while (true) {
            for (int i = 0; i < 20; i++) {
                try {
                    setUp();
                    String param = getTheLastModificationParam(false);
                    submit(theItemId, param);
                    String pidParam;
                    if (getItemClient().getPidConfig("cmm.Item.objectPid.setPidBeforeRelease", "true")
                        && !getItemClient().getPidConfig("cmm.Item.objectPid.releaseWithoutPid", "false")) {
                        pidParam = getPidParam(this.theItemId, "http://somewhere" + this.theItemId);
                        assignObjectPid(this.theItemId, pidParam);
                    }
                    if (getItemClient().getPidConfig("cmm.Item.versionPid.setPidBeforeRelease", "true")
                        && !getItemClient().getPidConfig("cmm.Item.versionPid.releaseWithoutPid", "false")) {
                        String latestVersion = getLatestVersionObjidValue(theItemXml);
                        pidParam = getPidParam(latestVersion, "http://somewhere" + latestVersion);
                        assignVersionPid(latestVersion, pidParam);
                    }

                    param = getTheLastModificationParam(false);
                    release(theItemId, param);
                }
                catch (Exception e) {

                }

                // TODO include floating PID in properties of released items
                // assertXMLExist("Released item floating pid", xml,
                // "/item/properties/pid/text()");

            }
            Thread.sleep(360000);
        }

    }
}
