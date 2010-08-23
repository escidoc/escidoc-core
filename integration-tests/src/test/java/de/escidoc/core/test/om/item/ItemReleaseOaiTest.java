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

import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.security.client.PWCallback;

/**
 * Test creates and releases items in order to fill a Escidoc repository and 
 * to test a Escidoc OAI-Provider.
 * 
 * @author ROF
 * 
 */
public class ItemReleaseOaiTest extends ItemTestBase {

    private String theItemXml;

    private String theItemId;

    /**
     * @param transport
     *            The transport identifier.
     */
    public ItemReleaseOaiTest(final int transport) {
        super(transport);
    }

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Override
    public void setUp() throws Exception {

        super.setUp();
        
        // create an item and save the id
        String xmlData =
            EscidocRestSoapTestsBase.getTemplateAsString(TEMPLATE_ITEM_PATH  + "/" + getTransport(false),
                "escidoc_item_198_for_create_withoutComponents_2Md-Records.xml");
        theItemXml = create(xmlData);                                        
        theItemId = getObjidValue(theItemXml);
        
    }

    /**
     * Clean up after servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Override
    public void tearDown() throws Exception {

        super.tearDown();
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);

        // TODO purge object from Fedora
    }

    
    private String getTheLastModificationParam(boolean includeWithdrawComment)
        throws Exception {
        return getTheLastModificationParam(includeWithdrawComment, theItemId);
    }

  
   
    public void testReleaseItem() throws Exception {
        while(true) {
        for (int i = 0; i <20; i++) {
        try { 
        setUp();
        String param = getTheLastModificationParam(false);
        submit(theItemId, param);
        String pidParam;
        if (getItemClient().getPidConfig(
            "cmm.Item.objectPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig(
                "cmm.Item.objectPid.releaseWithoutPid", "false")) {
            pidParam =
                getPidParam(this.theItemId, "http://somewhere" + this.theItemId);
            assignObjectPid(this.theItemId, pidParam);
        }
        if (getItemClient().getPidConfig(
            "cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig(
                "cmm.Item.versionPid.releaseWithoutPid", "false")) {
            String latestVersion = getLatestVersionObjidValue(theItemXml);
            pidParam =
                getPidParam(latestVersion, "http://somewhere" + latestVersion);
            assignVersionPid(latestVersion, pidParam);
        }

        param = getTheLastModificationParam(false);
        release(theItemId, param);
        System.out.println("i  ++"  + i + " item id  " + theItemId);
        } catch(Exception e) {
            
        }
        

//        String xml = retrieve(theItemId);
//        assertXmlExists("Properties status released", xml, XPATH_ITEM_STATUS
//            + "[text() = 'released']");
//        assertXmlExists("current-version status released", xml,
//            XPATH_ITEM_CURRENT_VERSION_STATUS + "[text() = 'released']");
//        assertXmlExists("Released item latest-release", xml,
//            "/item/properties/latest-release");
//        // has PID
//        // assertXMLExist("Released item version pid", xml,
//        // "/item/properties/latest-release/pid/text()");
//        assertXmlValidItem(xml);

        // TODO include floating PID in properties of released items
        // assertXMLExist("Released item floating pid", xml,
        // "/item/properties/pid/text()");
        
    }
        Thread.sleep(360000);  
    }
       
    }
}
