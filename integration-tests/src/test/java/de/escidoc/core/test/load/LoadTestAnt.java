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

import de.escidoc.core.test.common.logger.AppLogger;
import de.escidoc.core.test.common.resources.PropertiesProvider;
import de.escidoc.core.test.load.statistics.Collector;

public class LoadTestAnt extends LoadTestBase {

    /** The logger. */
    private static AppLogger logger =
        new AppLogger(LoadTestAnt.class.getName());

    private int noOfThreads = 1;

    private int noOfItems = 1;

    private int noOfRepetitions = 1;

    private String expectedState = STATUS_RELEASED;

    // private String label = "";

    public LoadTestAnt(int transport) {
        super(transport);
    }

    /**
     * See Interface for functional description.
     * 
     * @throws Exception
     * 
     * @throws Exception
     * 
     * @see org.apache.tools.ant.Task#execute()
     */
    public void test() throws Exception {
        initialize();
        log("Starting tests:");
        executeTests();
        Collector.merge("0-Item LifeCycle", getFileLabel()
            + " 0-Item LifeCycle", true);
        Collector.merge("1-Item.retrieve", getFileLabel() + " 1-Item.retrieve",
            true);
        Collector.merge("2-Item.create", getFileLabel() + " 2-Item.create",
            true);
        Collector.merge("3-Item.submit", getFileLabel() + " 3-Item.submit",
            true);
        Collector.merge("4-Item.release", getFileLabel() + " 4-Item.release",
            true);
        Collector.merge("5-Item.withdraw", getFileLabel() + " 5-Item.withdraw",
            true);
        writeStatisticsFile();
    }

    private void executeTests() {
        for (int j = 0; j < getNoOfRepetitions(); ++j) {
            log("Starting " + (j + 1) + " of " + getNoOfRepetitions()
                + " repetitions!");
            try {
                startThreads(noOfThreads, getNoOfItems());
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            log("Finished (" + (j + 1) + " of " + getNoOfRepetitions()
                + ") repetitions!");
        }
    }

    private void initialize() throws Exception {

        PropertiesProvider properties = new PropertiesProvider();
        setNoOfThreads(Integer.parseInt(properties.getProperty("noOfThreads")));
        setNoOfItems(Integer.parseInt(properties.getProperty("noOfItems")));
        setNoOfRepetitions(Integer.parseInt(properties
            .getProperty("noOfRepetitions")));
        setExpectedState(properties.getProperty("expectedState"));

        super.setLabel(properties.getProperty("label") + "-T"
            + getNoOfThreads() + "I" + getNoOfItems());
        initLogFile(true);

    }

    private void startThreads(
        final int noOfThreads, final int noOfItemsPerThread) throws Exception {
        Thread[] threads = createThreads(noOfThreads, noOfItemsPerThread);
        for (int i = 0; i < noOfThreads; ++i) {
            threads[i].start();
        }
        waitUntilStopped(threads);
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
                    writeStatisticsFile();
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
                    getTransport(), noOfItemsPerThread, getExpexctedState(),
                    this);

        }
        return result;
    }

    private String getThreadLabel(
        final int noOfThread, final int noOfItemsPerThread) {
        return getFileLabel();
        // + "-T" + noOfThread + "I"
        // + noOfItemsPerThread;
    }

    private String getTestLabel(final int noOfThreads, final int noOfItems) {
        return "Execution " + getFileLabel() + " (Threads=" + noOfThreads
            + ", Items=" + noOfItems + ")";
    }

    /**
     * @return the expectedState
     */
    public String getExpexctedState() {
        return expectedState;
    }

    /**
     * @param expectedState
     *            the expectedState to set
     */
    public void setExpectedState(String expectedState) {
        if (STATE_PENDING.toLowerCase().equals(expectedState.toLowerCase())) {
            this.expectedState = STATE_PENDING;
        }
        else if (STATE_SUBMITTED.toLowerCase().equals(
            expectedState.toLowerCase())) {
            this.expectedState = STATE_SUBMITTED;
        }
        else if (STATE_RELEASED.toLowerCase().equals(
            expectedState.toLowerCase())) {
            this.expectedState = STATE_RELEASED;
        }
        else if (STATE_WITHDRAWN.toLowerCase().equals(
            expectedState.toLowerCase())) {
            this.expectedState = STATE_WITHDRAWN;
        }
        else {
            log("Unknown expected state '" + expectedState + "'.");
        }
    }

    /**
     * @return the noOfItems
     */
    public int getNoOfItems() {
        return noOfItems;
    }

    /**
     * @param noOfItems
     *            the noOfItems to set
     */
    public void setNoOfItems(int noOfItems) {
        this.noOfItems = noOfItems;
    }

    /**
     * @return the noOfThreads
     */
    public int getNoOfThreads() {
        return noOfThreads;
    }

    /**
     * @param noOfThreads
     *            the noOfThreads to set
     */
    public void setNoOfThreads(int noOfThreads) {
        this.noOfThreads = noOfThreads;
    }

    /**
     * @return the noOfRepetitions
     */
    public int getNoOfRepetitions() {
        return noOfRepetitions;
    }

    /**
     * @param noOfRepetitions
     *            the noOfRepetitions to set
     */
    public void setNoOfRepetitions(int noOfRepetitions) {
        this.noOfRepetitions = noOfRepetitions;
    }
}
