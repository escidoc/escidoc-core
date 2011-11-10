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

import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.cmm.ContentModelClient;

/**
 * Stress test for item create and release.
 * 
 * See issue (INFR-1076, INFR-1077)
 * 
 * @author Steffen Wagner
 */
public class ItemCreateReleaseStressIT extends ItemTestBase {

    private final static int NUM_OF_ITEMS = 2500;

    /**
     * Short reference to Item with important information for processing.
     * 
     * @author SWA
     * 
     */
    class ItemReference {

        private String objid;

        private String lmd;

        public ItemReference(final String objid, final String lmd) {

            if (objid == null) {
                throw new NullPointerException("objid is not allow to be null");
            }
            if (lmd == null) {
                throw new NullPointerException("lmd is not allow to be null");
            }

            this.objid = objid;
            this.lmd = lmd;
        }

        /**
         * Objid of Item
         * 
         * @return objid
         */
        public String getObjid() {
            return objid;
        }

        /**
         * Get last-modification-date of Item.
         * 
         * @return last-modification-date
         */
        public String getLmdAsString() {
            return lmd;
        }

        /**
         * Set last modification date.
         * 
         * @param lmd
         *            last-modification-date
         */
        public void setLmd(final String lmd) {

            if (lmd == null) {
                throw new NullPointerException("lmd is not allow to be null");
            }
            this.lmd = lmd;
        }
    }

    /**
     * Create a bunch of Items an release it afterward.
     * 
     * @throws Exception
     *             Thrown if behavior is not as expected.
     */
    @Ignore("Ignore because of very long process time")
    @Test
    public void createSubmitReleaseSingleThreadStress() throws Exception {

        // Content-model
        ContentModelClient cmmClient = new ContentModelClient();
        String cmmId =
            getObjidValue(handleXmlResult(cmmClient.create(getExampleTemplate("content-model-minimal-for-create.xml"))));

        // Context
        Document contextDoc =
            getDocument(handleXmlResult(getContextClient().create(
                getExampleTemplate("context-minimal-for-create-01.xml"))));
        String contextId = getObjidValue(contextDoc);
        getContextClient().open(contextId, getStatusTaskParam(getLastModificationDateValue2(contextDoc), null));

        // create an item an replace the value of the public-status element
        String itemXml = getExampleTemplate("item-minimal-for-create-01.xml");
        // replace references (by string replace, sorry)
        itemXml = itemXml.replace("escidoc:ex1", contextId);
        itemXml = itemXml.replace("escidoc:ex4", cmmId);

        ItemReference[] ids = new ItemReference[NUM_OF_ITEMS];

        // create all Items
        for (int i = 0; i < NUM_OF_ITEMS; i++) {
            try {
                Document doc = EscidocAbstractTest.getDocument(create(itemXml));
                String itemId = getObjidValue(doc);
                ItemReference itemRef = new ItemReference(itemId, getLastModificationDateValue(doc));
                ids[i] = itemRef;
                System.out.println("Item created " + itemRef.getObjid());
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error create: " + ids[i].objid + " lmd=" + ids[i].lmd + " - " + e.getMessage()
                    + "\n" + e.getStackTrace());
                throw e;
            }
        }

        // submit and release all Items
        for (int i = 0; i < NUM_OF_ITEMS; i++) {
            try {
                //                String param = getTheLastModificationParam(false, ids[i].getObjid(), "", ids[i].getLmdAsString());
                //                String xml = submit(ids[i].getObjid(), param);

                String xml =
                    submit(ids[i].getObjid(), getStatusTaskParam(
                        getLastModificationDateValue2(getDocument(retrieve(ids[i].getObjid()))), null));

                ids[i].setLmd(getLastModificationDateValue(getDocument(xml)));
                System.out.println("Item submitted " + ids[i].getObjid());
            }
            catch (Exception e) {
                System.out.println("Error submit: " + ids[i].objid + " lmd=" + ids[i].lmd + " - " + e.getMessage()
                    + "\n" + e.getStackTrace());
                throw e;
            }
        }

        // check all Items

        // delete
        //        for (int i = 0; i < NUM_OF_ITEMS; i++) {
        //            try {
        //                delete(ids[i].getObjid());
        //                ids[i].setLmd(null);
        //                System.out.println("Item deleted " + ids[i].getObjid());
        //            }
        //            catch (Exception e) {
        //                System.out.println("Error deleting: " + ids[i].objid + e.getMessage() + "\n" + e.getStackTrace());
        //                throw e;
        //            }
        //        }

    }
}
