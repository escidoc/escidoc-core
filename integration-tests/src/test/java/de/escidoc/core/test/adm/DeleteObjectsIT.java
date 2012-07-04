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
package de.escidoc.core.test.adm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import de.escidoc.core.common.exceptions.remote.application.notfound.ItemNotFoundException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.TaskParamFactory;

/**
 * Test suite for the DeleteObjects method of the admin tool.
 * 
 * @author André Schenk
 */
public class DeleteObjectsIT extends AdminToolTestBase {

    /**
     * Delete one object.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test(timeout = 30000)
    public void testDeleteOneObject() throws Exception {

        Set<String> l = createItems(1);
        deleteObjects(TaskParamFactory.getDeleteObjectsTaskParam(l, false));

        // wait until process has finished
        final int waitTime = 5000;

        while (true) {
            String status = getPurgeStatus();
            if (status.indexOf("finished") > 0) {
                break;
            }
            Thread.sleep(waitTime);
        }

        // check if item still exists
        String id = l.iterator().next();
        try {
            retrieveItem(id);
            fail("item with id " + id + " still exists");
        }
        catch (final ItemNotFoundException e) {
            // that's alright
        }

        // check if item still exists in search index
        int numHits = retrieveItems(Arrays.asList(id));
        assertEquals("Number of hits not as expected", 1, numHits);
    }

    /**
     * Delete a list of objects.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test(timeout = 30000)
    public void testDeleteObjects() throws Exception {

        // create Items
        Set<String> l = createItems(4);

        // delete Items
        deleteObjects(TaskParamFactory.getDeleteObjectsTaskParam(l, false));

        // wait until process has finished
        final int waitTime = 5000;

        while (true) {
            String status = getPurgeStatus();
            if (status.indexOf("finished") > 0) {
                break;
            }
            Thread.sleep(waitTime);
        }

        // check if Items are deleted
        Iterator<String> it = l.iterator();
        while (it.hasNext()) {
            String id = it.next();
            try {
                retrieveItem(id);
                fail("eSciDoc Item with id " + id + " still exists although it should be deleted");
            }
            catch (final ItemNotFoundException e) {
                // that's expected for every Item
            }
        }
        // check if items still exist in search index
        int numHits = retrieveItems(Arrays.asList((String[]) l.toArray()));
        assertEquals("Number of hits not as expected", l.size(), numHits);
    }

    /**
     * Delete a list of objects with asynchronous indexer.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test(timeout = 30000)
    public void deleteObjectsWithAsyncOption() throws Exception {

        // create Items
        Set<String> l = createItems(4);

        // delete Items
        deleteObjects(TaskParamFactory.getDeleteObjectsTaskParam(l, false));

        // wait until process has finished
        final int waitTime = 5000;

        while (true) {
            String status = getPurgeStatus();
            if (status.indexOf("finished") > 0) {
                break;
            }
            Thread.sleep(waitTime);
        }

        // check if Items are deleted
        Iterator<String> it = l.iterator();
        while (it.hasNext()) {
            String id = it.next();
            try {
                retrieveItem(id);
                fail("eSciDoc Item with id " + id + " still exists although it should be deleted");
            }
            catch (final ItemNotFoundException e) {
                // that's expected for every Item
            }
        }
        // check if items still exist in search index
        int numHits = retrieveItems(Arrays.asList((String[]) l.toArray()));
        assertEquals("Number of hits not as expected", l.size(), numHits);
    }

    /**
     * Delete a list of objects with synchon indexer.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    //(timeout = 30000)
    public void deleteObjectsWithSyncOption() throws Exception {

        // create Items
        Set<String> l = createItems(4);

        // delete Items
        deleteObjects(TaskParamFactory.getDeleteObjectsTaskParam(l, true));

        // wait until process has finished
        final int waitTime = 5000;

        while (true) {
            String status = getPurgeStatus();
            if (status.indexOf("finished") > 0) {
                break;
            }
            Thread.sleep(waitTime);
        }

        // check if Items are deleted
        Iterator<String> it = l.iterator();
        while (it.hasNext()) {
            String id = it.next();
            try {
                retrieveItem(id);
                fail("eSciDoc Item with id " + id + " still exists although it should be deleted");
            }
            catch (final ItemNotFoundException e) {
                // that's expected for every Item
            }
        }
        // check if items still exist in search index
        int numHits = retrieveItems(Arrays.asList((String[]) l.toArray()));
        assertEquals("Number of hits not as expected", 0, numHits);
    }

    /**
     * Create a number of eSciDoc Items.
     * 
     * @param number
     *            Number of Items to create.
     * @return Set with escidoc IDs of the created Items.
     * 
     * @throws Exception
     *             If something failed.
     */
    private Set<String> createItems(final int number) throws Exception {

        String xml = EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "create_item_minimal.xml");

        Set<String> l = new HashSet<String>();

        for (int i = 0; i < number; i++) {
            l.add(getObjidValue(createItem(xml)));
        }

        return l;
    }
}
