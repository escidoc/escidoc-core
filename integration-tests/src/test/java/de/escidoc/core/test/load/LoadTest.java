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
package de.escidoc.core.test.load;

import de.escidoc.core.test.load.statistics.Collector;

/**
 * Test the mock implementation of the item resource.
 * 
 * @author MSC
 * 
 */
public class LoadTest extends LoadTestBase {

    private static String label = "";

    private boolean clearLogFile = true;

    /**
     * @param transport
     *            The transport identifier.
     */
    public LoadTest(final int transport) {
        super(transport);
    }

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void setUp() throws Exception {

        initLogFile(clearLogFile);
        super.setUp();
    }

    /**
     * Clean up after servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void tearDown() throws Exception {

        super.tearDown();
    }

    /**
     * Successful creation of a Context.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testLoad() throws Exception {

        int noOfThreads = 1;
        int noOfItemsPerThread = 3;
        int repeat = 1;
        for (int j = 0; j < repeat; ++j) {
            log("Starting " + (j + 1) + " of " + repeat + " repetitions!");
            for (int threadNo = 1; threadNo <= noOfThreads; ++threadNo) {
                log("   Starting execution with " + threadNo
                    + " threads and working on " + noOfItemsPerThread
                    + " Items per Thread!");
                execute(threadNo, noOfItemsPerThread);
            }
            log("Finished (" + (j + 1) + " of " + repeat + ") repetitions!");
        }
        boolean keep = true;
        Collector.merge(ItemThread.ITEM_LIFE_CYCLE_TEXT,
            ItemThread.ITEM_LIFE_CYCLE_TEXT, keep);
        Collector.merge(ItemThread.ITEM_CREATE_TEXT,
            ItemThread.ITEM_CREATE_TEXT, keep);
        Collector.merge(ItemThread.ITEM_SUBMIT_TEXT,
            ItemThread.ITEM_SUBMIT_TEXT, keep);
        Collector.merge(ItemThread.ITEM_RELEASE_TEXT,
            ItemThread.ITEM_RELEASE_TEXT, keep);
        Collector.merge(ItemThread.ITEM_RETRIEVE_TEXT,
            ItemThread.ITEM_RETRIEVE_TEXT, keep);
        log(Collector.getMessage());
        writeStatisticsFile();
    }

    private void execute(final int noOfThreads, final int noOfItemsPerThread)
        throws Exception {
        String label = getTestLabel(noOfThreads, noOfItemsPerThread);
        Thread[] threads = createThreads(noOfThreads, noOfItemsPerThread);
        Collector.setStart(label);
        for (int i = 0; i < noOfThreads; ++i) {
            threads[i].start();
        }
        waitUntilStopped(threads);
        Collector.setEnd(label);
    }

    private void waitUntilStopped(final Thread[] threads) throws Exception {
        boolean finished = false;
        int noOfThreads = threads.length;
        while (!finished) {
            finished = true;
            for (int i = 0; i < noOfThreads; ++i) {
                finished = finished && ((ItemThread) threads[i]).isStopped();
                if (!((ItemThread) threads[i]).isSuccess()) {
                    ((ItemThread) threads[i])
                        .getLastExeception().printStackTrace();
                    throw new Exception(((ItemThread) threads[i])
                        .getLastExeception());
                }
            }
        }
        for (int i = 0; i < noOfThreads; ++i) {
            log("T(" + ((ItemThread) threads[i]).getIdAsString()
                + ") created Items: " + ((ItemThread) threads[i]).getItems());
        }
    }

    private Thread[] createThreads(
        final int noOfThreads, final int noOfItemsPerThread) {
        Thread[] result = new Thread[noOfThreads];
        for (int i = 0; i < noOfThreads; ++i) {
            result[i] =
                new ItemThread(getThreadLabel(noOfThreads, noOfItemsPerThread),
                    getTransport(), noOfItemsPerThread, STATUS_RELEASED, this);

        }
        return result;
    }

    private String getThreadLabel(
        final int noOfThread, final int noOfItemsPerThread) {
        // return label + " Thread(" + threadNo + ")";
        return label + "T" + noOfThread + "I" + noOfItemsPerThread;
    }

    private String getTestLabel(final int noOfThreads, final int noOfItems) {
        return "Execution " + label + " (Threads=" + noOfThreads + ", Items="
            + noOfItems + ")";
    }
}
