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

import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.EscidocTestsBase;
import de.escidoc.core.test.common.logger.AppLogger;
import de.escidoc.core.test.load.statistics.Collector;
import de.escidoc.core.test.om.item.ItemTestBase;

public class LoadManyItemsThread extends Thread {

    protected static AppLogger log =
        new AppLogger(LoadManyItemsThread.class.getName());

    public static final String ITEM_WITHDRAW_TEXT = "Item.withdraw";

    public static final String ITEM_RELEASE_TEXT = "Item.release";

    public static final String ITEM_SUBMIT_TEXT = "Item.submit";

    public static final String ITEM_CREATE_TEXT = "Item.create";

    public static final String ITEM_RETRIEVE_TEXT = "Item.retrieve";

    public static final String ITEM_LIFE_CYCLE_TEXT = "Item LifeCycle";

    private int transport = -1;

    private String label = null;

    private String itemLabel = null;

    private int noOfItems = -1;

    private int startDokNum = -1;

    private LoadTestBase clients = null;

    private ItemTestBase itemBase = null;

    private boolean stopped = false;

    private boolean success = true;

    private Exception lastExeception = null;

    // private ContainerTestBase containerBase = null;

    public LoadManyItemsThread(final String label, final int transport,
        final int noOfItems, final int startDokNum) {
        this.label = label + " T(" + getIdAsString() + ")";

        this.transport = transport;
        this.clients = new LoadTestBase(transport);
        this.itemBase = new ItemTestBase(transport);
        this.noOfItems = noOfItems;
        this.startDokNum = startDokNum;
    }

    @Override
    public void run() {
        if (log.isDebugEnabled()) {
            log.debug("Thread " + label + " startet!");
        }

        Collector.setStart(label);
        for (int i = 0; i < noOfItems && !stopped; ++i) {
            this.itemLabel = getItemLifecycleLabel(i + 1);
            Collector.setStart(this.itemLabel);
            try {
                testItem(i + 1, LoadTestBase.STATUS_RELEASED, null, null);
            }
            catch (Exception e) {
                StackTraceElement[] stack = e.getStackTrace();
                for (int j = 0; j < stack.length; j++) {
                    getLoadTestBase().log(stack[i].toString());
                }
                // setSuccess(false);
                // setStopped(true);
                setLastExeception(e);
                e.printStackTrace();
            }
            Collector.setEnd(this.itemLabel);
        }
        Collector.setEnd(label);
        setStopped(true);
    }

    private String testItem(
        final int itemNo, final String expectedState, final String context,
        final String contentModel) throws Exception {
        String result = null;
        int dokNum = startDokNum + itemNo - 1;
        getLoadTestBase().log("Create item in state '" + expectedState + "'");
        Node create =
            EscidocRestSoapTestsBase.getTemplateAsDocument(new URL(
                LoadTestBase.DOCUMENT_BASE_URI + "/escidoc_search_item"
                    + dokNum + ".xml"));
        if (context != null) {
            create =
                LoadTestBase.substitute(create,
                    "/item/properties/context/@href", "/ir/context/" + context);
        }
        if (contentModel != null) {
            create =
                LoadTestBase.substitute(create,
                    "/item/properties/content-model/@href",
                    "/cmm/content-model/" + contentModel);
        }
        Document item = createItem(itemNo, create);
        result = getObjidValue(item);
        if (LoadTestBase.STATUS_PENDING.equals(expectedState)) {

        }
        else if (LoadTestBase.STATUS_SUBMITTED.equals(expectedState)) {
            getLoadTestBase().log("Submit item '" + result + "'");
            submitItem(itemNo, result, getLoadTestBase().getTaskParam(
                LoadTestBase.getLastModificationDateValue(item)));
        }
        else if (LoadTestBase.STATUS_RELEASED.equals(expectedState)) {
            getLoadTestBase().log("Submit item '" + result + "'");
            submitItem(itemNo, result, getLoadTestBase().getTaskParam(
                LoadTestBase.getLastModificationDateValue(item)));

            getLoadTestBase().log("Release item '" + result + "'");
            releaseItem(itemNo, result, getLoadTestBase().getTaskParam(
                LoadTestBase
                    .getLastModificationDateValue(EscidocRestSoapTestsBase
                        .getDocument(itemBase.retrieve(result)))));
        }
        else if (LoadTestBase.STATUS_WITHDRAWN.equals(expectedState)) {
            getLoadTestBase().log("Submit item '" + result + "'");
            submitItem(itemNo, result, getLoadTestBase().getTaskParam(
                LoadTestBase.getLastModificationDateValue(item)));

            getLoadTestBase().log("Release item '" + result + "'");
            releaseItem(itemNo, result, getLoadTestBase().getTaskParam(
                LoadTestBase
                    .getLastModificationDateValue(EscidocRestSoapTestsBase
                        .getDocument(itemBase.retrieve(result)))));

            getLoadTestBase().log("Withdraw item '" + result + "'");
            withdrawItem(itemNo, result, getLoadTestBase()
                .getWithdrawTaskParam(
                    LoadTestBase
                        .getLastModificationDateValue(EscidocRestSoapTestsBase
                            .getDocument(itemBase.retrieve(result))),
                    "Withdrawed for Context retrieve members tests!"));
        }
        else {
            throw new Exception(expectedState + " is no valid item status!");
        }
        retrieveItem(itemNo, result);
        getLoadTestBase().log(
            "Created item '" + result + "' in state '" + expectedState + "'");
        return result;
    }

    private String retrieveItem(final int itemNo, final String itemId)
        throws Exception {
        Collector.setStart(getItemRetrieveLabel(itemNo));
        String result = itemBase.retrieve(itemId);
        Collector.setEnd(getItemRetrieveLabel(itemNo));
        return result;
    }

    private Document createItem(final int itemNo, final Node item)
        throws Exception {
        Collector.setStart(getItemCreateLabel(itemNo));
        Document created =
            EscidocRestSoapTestsBase.getDocument(itemBase.create(LoadTestBase
                .toString(item, false)));
        Collector.setEnd(getItemCreateLabel(itemNo));
        return created;
    }

    private void submitItem(
        final int itemNo, final String itemId, final String taskParam)
        throws Exception {
        Collector.setStart(getItemSubmitLabel(itemNo));
        itemBase.submit(itemId, taskParam);
        Collector.setEnd(getItemSubmitLabel(itemNo));
    }

    private void releaseItem(
        final int itemNo, final String itemId, final String taskParam)
        throws Exception {
        Collector.setStart(getItemReleaseLabel(itemNo));
        itemBase.release(itemId, taskParam);
        Collector.setEnd(getItemReleaseLabel(itemNo));
    }

    private void withdrawItem(
        final int itemNo, final String itemId, final String taskParam)
        throws Exception {
        Collector.setStart(getItemWithdrawLabel(itemNo));
        itemBase.withdraw(itemId, taskParam);
        Collector.setEnd(getItemWithdrawLabel(itemNo));
    }

    public LoadTestBase getLoadTestBase() {

        if (this.clients == null) {
            this.clients = new LoadTestBase(transport);
        }
        return this.clients;
    }

    /**
     * @param stopped
     *            the stopped to set
     */
    public void setStopped(final boolean stopped) {
        this.stopped = stopped;
    }

    public boolean isStopped() {
        return this.stopped;
    }

    private String getItemLifecycleLabel(final int itemNo) {
        return createLabel(itemNo, ITEM_LIFE_CYCLE_TEXT);
    }

    private String getItemRetrieveLabel(final int itemNo) {
        return createLabel(itemNo, ITEM_RETRIEVE_TEXT);
    }

    private String getItemCreateLabel(final int itemNo) {
        return createLabel(itemNo, ITEM_CREATE_TEXT);
    }

    private String getItemSubmitLabel(final int itemNo) {
        return createLabel(itemNo, ITEM_SUBMIT_TEXT);
    }

    private String getItemReleaseLabel(final int itemNo) {
        return createLabel(itemNo, ITEM_RELEASE_TEXT);
    }

    private String getItemWithdrawLabel(final int itemNo) {
        return createLabel(itemNo, ITEM_WITHDRAW_TEXT);
    }

    /**
     * Create a label.
     * 
     * @param itemNo
     * @param text
     * @return
     */
    private String createLabel(final int itemNo, final String text) {
        // return label + " " + text + "(" + itemNo + ")";
        return label + " " + text;
    }

    /**
     * Convert the thread id to a String with length of 4 and leading '0's.
     * 
     * @return The thread id as a String
     */
    private String getIdAsString() {
        String result = "";
        long id = getId();
        if (id < 10) {
            result = "000" + id;
        }
        else if (id < 100) {
            result = "00" + id;
        }
        else if (id < 1000) {
            result = "0" + id;
        }
        else {
            result = "" + id;
        }
        return result;
    }

    /**
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return the lastExeception
     */
    public Exception getLastExeception() {
        return lastExeception;
    }

    /**
     * @param lastExeception
     *            the lastExeception to set
     */
    public void setLastExeception(Exception lastExeception) {
        this.lastExeception = lastExeception;
    }

    /**
     * @param success
     *            the success to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Gets the objid attribute of the root element from the document.
     * 
     * @param document
     *            The document to retrieve the value from.
     * @return Returns the attribute value.
     * @throws Exception
     *             If anything fails.
     */
    public String getObjidValue(final Document document) throws Exception {

        return EscidocTestsBase.getRootElementAttributeValue(document,
            EscidocTestsBase.NAME_OBJID);
    }

}
