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

import java.util.List;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.load.statistics.Collector;
import de.escidoc.core.test.om.item.ItemTestBase;

public class ItemThread extends Thread {

    public static final String ITEM_WITHDRAW_TEXT = "5-Item.withdraw";

    public static final String ITEM_RELEASE_TEXT = "4-Item.release";

    public static final String ITEM_SUBMIT_TEXT = "3-Item.submit";

    public static final String ITEM_CREATE_TEXT = "2-Item.create";

    public static final String ITEM_RETRIEVE_TEXT = "1-Item.retrieve";

    public static final String ITEM_LIFE_CYCLE_TEXT = "0-Item LifeCycle";

    public static final int MAX_NO_OF_ITEMS = 30;

    private int transport = -1;

    private String label = null;

    private String itemLabel = null;

    private int noOfItems = -1;

    private LoadTestBase clients = null;

    private ItemTestBase itemBase = null;

    private boolean stopped = false;

    private boolean success = true;

    private Exception lastExeception = null;

    private final List items = new Vector();

    private String expectedState = LoadTestBase.STATUS_RELEASED;

    // private ContainerTestBase containerBase = null;

    public ItemThread(final String label, final int transport,
        final int noOfItems, final String expectedState, final LoadTestBase base) {
        this.label = label + " T(" + getIdAsString() + ")";

        this.transport = transport;
        this.clients = base;
        this.expectedState = expectedState;
        this.itemBase = new ItemTestBase(transport);
        if ((noOfItems > 0) && (noOfItems <= MAX_NO_OF_ITEMS)) {
            this.noOfItems = noOfItems;
        }
        else {
            this.noOfItems = MAX_NO_OF_ITEMS;
        }
    }

    @Override
    public void run() {
        getLoadTestBase().log(
            "Thread " + label + " startet creating '" + noOfItems + "' items!");

        Collector.setStart(label);
        for (int i = 0; i < noOfItems && !stopped; ++i) {
            this.itemLabel = getItemLifecycleLabel(i + 1);
            Collector.setStart(this.itemLabel);
            try {
                items.add(testItem(i + 1, this.expectedState, null, null));
            }
            catch (Exception e) {
                getLoadTestBase().logException(e);
                setLastExeception(e);
                setSuccess(false);
                setStopped(true);
                e.printStackTrace();
            }
            Collector.setEnd(this.itemLabel);
        }
        Collector.setEnd(label);
        getLoadTestBase().log("Thread " + label + " finished!");
        setStopped(true);
    }

    private String testItem(
        final int itemNo, final String expectedState, final String context,
        final String contentModel) throws Exception {
        String result = null;
        getLoadTestBase().log("Create item in state '" + expectedState + "'");
        Node create =
            EscidocRestSoapTestsBase.getTemplateAsDocument(
                getItemTemplatePath(), "escidoc_item_198_for_create.xml");

        if (transport == Constants.TRANSPORT_REST) {
            if (context != null) {
                create =
                    LoadTestBase.substitute(create,
                        "/item/properties/context/@href", "/ir/context/"
                            + context);
            }
            if (contentModel != null) {
                create =
                    LoadTestBase.substitute(create,
                        "/item/properties/content-model/@href",
                        "/cmm/content-model/" + contentModel);
            }
        }
        else if (transport == Constants.TRANSPORT_SOAP) {
            if (context != null) {
                create =
                    LoadTestBase.substitute(create,
                        "/item/properties/context/@objid", context);
            }
            if (contentModel != null) {
                create =
                    LoadTestBase.substitute(create,
                        "/item/properties/content-model/@objid", contentModel);
            }
        }
        Document item = createItem(itemNo, create);
        result = getObjidValue(item);
        if (LoadTestBase.STATUS_PENDING.equals(expectedState)) {
            retrieveItem(itemNo, result);
        }
        else if (LoadTestBase.STATUS_SUBMITTED.equals(expectedState)) {

            submitItem(itemNo, result, LoadTestBase.getTaskParam(LoadTestBase
                .getLastModificationDateValue(EscidocRestSoapTestsBase
                    .getDocument(retrieveItem(itemNo, result)))));
        }
        else if (LoadTestBase.STATUS_RELEASED.equals(expectedState)) {
            submitItem(itemNo, result, LoadTestBase.getTaskParam(LoadTestBase
                .getLastModificationDateValue(EscidocRestSoapTestsBase
                    .getDocument(retrieveItem(itemNo, result)))));
            releaseItem(itemNo, result, LoadTestBase.getTaskParam(LoadTestBase
                .getLastModificationDateValue(EscidocRestSoapTestsBase
                    .getDocument(retrieveItem(itemNo, result)))));
        }
        else if (LoadTestBase.STATUS_WITHDRAWN.equals(expectedState)) {
            submitItem(itemNo, result, LoadTestBase.getTaskParam(LoadTestBase
                .getLastModificationDateValue(EscidocRestSoapTestsBase
                    .getDocument(retrieveItem(itemNo, result)))));
            releaseItem(itemNo, result, LoadTestBase.getTaskParam(LoadTestBase
                .getLastModificationDateValue(EscidocRestSoapTestsBase
                    .getDocument(retrieveItem(itemNo, result)))));
            withdrawItem(itemNo, result, LoadTestBase.getWithdrawTaskParam(
                LoadTestBase
                    .getLastModificationDateValue(EscidocRestSoapTestsBase
                        .getDocument(retrieveItem(itemNo, result))),
                "Withdrawn for Context retrieve members tests!"));
        }
        else {
            throw new Exception(expectedState + " is no valid item status!");
        }
        getLoadTestBase().log(
            "Created item '" + result + "' in state '" + expectedState + "'");
        return result;
    }

    private String getItemTemplatePath() {
        String result = LoadTestBase.TEMPLATE_ITEM_PATH;
        if (transport == Constants.TRANSPORT_REST) {
            result += "/rest";
        }
        else if (transport == Constants.TRANSPORT_SOAP) {
            result += "/soap";
        }
        return result;
    }

    private String retrieveItem(final int itemNo, final String itemId)
        throws Exception {
        getLoadTestBase().log(
            getItemRetrieveLabel(itemNo) + "(" + itemId + ").");
        Collector.setStart(getItemRetrieveLabel(itemNo));
        String result = itemBase.retrieve(itemId);
        Collector.setEnd(getItemRetrieveLabel(itemNo));
        return result;
    }

    private Document createItem(final int itemNo, final Node item)
        throws Exception {
        getLoadTestBase().log(getItemCreateLabel(itemNo));
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
        getLoadTestBase().log(
            getItemSubmitLabel(itemNo) + "  (" + itemId + ").");
        Collector.setStart(getItemSubmitLabel(itemNo));
        itemBase.submit(itemId, taskParam);
        Collector.setEnd(getItemSubmitLabel(itemNo));
    }

    private void releaseItem(
        final int itemNo, final String itemId, final String taskParam)
        throws Exception {
        getLoadTestBase().log(
            getItemReleaseLabel(itemNo) + " (" + itemId + ").");
        Collector.setStart(getItemReleaseLabel(itemNo));
        itemBase.releaseWithPid(itemId);
        Collector.setEnd(getItemReleaseLabel(itemNo));
    }

    private void withdrawItem(
        final int itemNo, final String itemId, final String taskParam)
        throws Exception {
        getLoadTestBase().log(
            getItemWithdrawLabel(itemNo) + "(" + itemId + ").");
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
    public String getIdAsString() {
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
     * Return the list od ids of created, submitted, abd released items.
     * 
     * @return The list of ids.
     */
    public List getItems() {

        return items;
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

        return EscidocRestSoapTestsBase.getObjidValue(transport, document);
        // return EscidocTestsBase.getRootElementAttributeValue(document,
        // EscidocTestsBase.NAME_OBJID);
    }

}
