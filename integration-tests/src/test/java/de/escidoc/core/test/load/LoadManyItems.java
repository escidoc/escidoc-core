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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import de.escidoc.core.test.load.statistics.Collector;

/**
 * Test the mock implementation of the item resource.
 * 
 * @author MSC
 * 
 */
public class LoadManyItems extends LoadTestBase {

    private static String label = "";

    private boolean clearLogFile = true;

    /**
     * @param transport
     *            The transport identifier.
     */
    public LoadManyItems(final int transport) {
        super(transport);
    }

    /**
     * Successful creation of a Context.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testLoad() throws Exception {

        int noOfThreads = 3;
        int noOfItemsPerThread = 3400;
        int startDok = 870;
        execute(noOfThreads, noOfItemsPerThread, startDok);
        boolean keep = true;
        Collector.merge(LoadManyItemsThread.ITEM_LIFE_CYCLE_TEXT,
            LoadManyItemsThread.ITEM_LIFE_CYCLE_TEXT, keep);
        Collector.merge(LoadManyItemsThread.ITEM_CREATE_TEXT,
            LoadManyItemsThread.ITEM_CREATE_TEXT, keep);
        Collector.merge(LoadManyItemsThread.ITEM_SUBMIT_TEXT,
            LoadManyItemsThread.ITEM_SUBMIT_TEXT, keep);
        Collector.merge(LoadManyItemsThread.ITEM_RELEASE_TEXT,
            LoadManyItemsThread.ITEM_RELEASE_TEXT, keep);
        Collector.merge(LoadManyItemsThread.ITEM_RETRIEVE_TEXT,
            LoadManyItemsThread.ITEM_RETRIEVE_TEXT, keep);
        try {
            File path = new File(getStatsSavePath());
            path.mkdirs();
            BufferedWriter out =
                new BufferedWriter(new FileWriter(getStatsSavePath()
                    + "/load-statistics.xml"));
            out.write(Collector.getMessage());
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void execute(
        final int noOfThreads, final int noOfItemsPerThread, final int startDok)
        throws Exception {
        String label = getTestLabel(noOfThreads, noOfItemsPerThread);
        Thread[] threads =
            createThreads(noOfThreads, noOfItemsPerThread, startDok);
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
                finished =
                    finished && ((LoadManyItemsThread) threads[i]).isStopped();
                if (!((LoadManyItemsThread) threads[i]).isSuccess()) {
                    throw new Exception(((LoadManyItemsThread) threads[i])
                        .getLastExeception());
                }
            }
        }
    }

    private Thread[] createThreads(
        final int noOfThreads, final int noOfItemsPerThread, final int startDok) {
        int startDokNum = startDok;
        Thread[] result = new Thread[noOfThreads];
        for (int i = 0; i < noOfThreads; ++i) {
            result[i] =
                new LoadManyItemsThread(getThreadLabel(noOfThreads,
                    noOfItemsPerThread), getTransport(), noOfItemsPerThread,
                    startDokNum);
            startDokNum += noOfItemsPerThread;
        }
        return result;
    }

    private String getThreadLabel(
        final int noOfThreads, final int noOfItemsPerThread) {
        // return label + " Thread(" + threadNo + ")";
        return label + "T" + noOfThreads + "I" + noOfItemsPerThread;
    }

    private String getTestLabel(final int noOfThreads, final int noOfItems) {
        return "Execution " + label + " (Threads=" + noOfThreads + ", Items="
            + noOfItems + ")";
    }
}
