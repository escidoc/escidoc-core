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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.escidoc.core.common.exceptions.remote.application.notfound.ItemNotFoundException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.aa.UserAccountClient;
import de.escidoc.core.test.common.client.servlet.aa.UserGroupClient;
import de.escidoc.core.test.security.client.PWCallback;

/**
 * Test the mock implementation of the item resource.
 *
 * @author Michael Schneider
 */
public class ItemFilterIT extends ItemTestBase {

    public static final String FILTER_CREATED_BY = STRUCTURAL_RELATIONS_NS_URI + NAME_CREATED_BY;

    private String theItemXml;

    private String theItemId;

    /**
     * Test successfully retrieving a filtered item-list filtering by created-by.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testFilterCreatedBy() throws Exception {
        theItemId = createItem();

        String createdBy = getObjidValue(EscidocAbstractTest.getDocument(theItemXml), "/item/properties/created-by");
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_IDENTIFIER + "\"=" + theItemId + " and "
            + "\"/properties/public-status\"=pending and " + "\"/properties/created-by/id\"=" + createdBy });

        String result = retrieveItems(filterParams);

        assertXmlValidSrwResponse(result);

        NodeList items = selectNodeList(EscidocAbstractTest.getDocument(result), XPATH_SRW_ITEM_LIST_ITEM);

        assertTrue("Wrong number of items matched filter criteria, expected 1, but was " + items.getLength(), items
            .getLength() == 1);
        assertEquals("Wrong item matched filter criteria.", theItemId, getObjidValue(items.item(0), "/"));
    }

    /**
     * Test successfully retrieving a filtered item-list filtering by created-by with an unknown user. Expected is an
     * empty item-list.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testFilterCreatedByUnknownCreator() throws Exception {
        theItemId = createItem();

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY,
            new String[] { "\"" + FILTER_IDENTIFIER + "\"=" + theItemId + " and " + "\"" + FILTER_URI_PUBLIC_STATUS
                + "\"=pending and " + "\"" + FILTER_CREATED_BY + "\"=escidoc:unknwonUser" });

        String result = retrieveItems(filterParams);

        assertXmlValidSrwResponse(result);

        NodeList items = selectNodeList(EscidocAbstractTest.getDocument(result), XPATH_SRW_ITEM_LIST_ITEM);

        assertTrue("Wrong number of items matched filter criteria, expected 0, but was " + items.getLength(), items
            .getLength() == 0);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testFilterId() throws Exception {
        // create an item and save the id
        theItemId = createItem();

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_IDENTIFIER + "\"=" + theItemId + " and "
            + "\"/properties/public-status\"=pending" });

        String result = retrieveItems(filterParams);

        assertXmlValidSrwResponse(result);

        Document resultDoc = EscidocAbstractTest.getDocument(result);

        NodeList nl;
        selectSingleNodeAsserted(resultDoc, XPATH_SRW_ITEM_LIST_ITEM + "[@href = '" + Constants.ITEM_BASE_URI + "/"
            + theItemId + "']");
        nl = selectNodeList(resultDoc, XPATH_SRW_ITEM_LIST_ITEM);
        assertEquals("Only one item should be retrieved.", nl.getLength(), 1);

        // delete the item
        delete(theItemId);
    }

    /**
     *
     */
    @Test
    public void testFilterItemRefsReleasedVersion() throws Exception {
        createReleasedReleasedWithdrawnItem();
        doTestFilterItemsStatus(STATUS_RELEASED, true);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testFilterItemsPending() throws Exception {
        doTestFilterItemsStatus(STATUS_PENDING, false);
    }

    @Test
    public void testFilterItemsPendingVersion() throws Exception {
        createReleasedPendingItem();
        doTestFilterItemsStatus(STATUS_PENDING, true);
    }

    @Test
    public void testFilterItemsSubmitted() throws Exception {
        doTestFilterItemsStatus(STATUS_SUBMITTED, false);
    }

    @Test
    public void testFilterItemsSubmittedVersion() throws Exception {
        createReleasedSubmittedItem();
        doTestFilterItemsStatus(STATUS_SUBMITTED, true);
    }

    @Test
    public void testFilterItemsContentModel() throws Exception {
        doTestFilterItemsContentModel("escidoc:persistent4");
    }

    @Test
    public void testFilterItemsUserRole() throws Exception {
        doTestFilterItemsUserRole(PWCallback.ID_PREFIX + PWCallback.DEFAULT_HANDLE, null);
    }

    @Test
    public void testFilterItemsUserRoleAdmin() throws Exception {
        doTestFilterItemsUserRole(PWCallback.ID_PREFIX + PWCallback.DEFAULT_HANDLE, "System-Administrator");
    }

    /**
     * Test filtering for items with record packing = "string". The result should be a valid SRW response.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testFilterRecordPacking() throws Exception {
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "10" });
        filterParams.put(FILTER_PARAMETER_RECORDPACKING, new String[] { "string" });
        assertXmlValidSrwResponse(retrieveItems(filterParams));
    }

    /**
     * Test filtering with a large XML filter.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testIssue637() throws Exception {
        final int count = 50;
        StringBuffer filter = new StringBuffer();
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        for (int index = 1; index <= count; index++) {
            String itemId = createItem();

            if (filter.length() > 0) {
                filter.append(" or ");
            }
            filter.append("\"" + FILTER_IDENTIFIER + "\"=" + itemId);
        }
        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { filter.toString() });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { String.valueOf(count) });

        String result = retrieveItems(filterParams);

        assertXmlValidSrwResponse(result);

        NodeList items = selectNodeList(EscidocAbstractTest.getDocument(result), XPATH_SRW_ITEM_LIST_ITEM);
        assertTrue("Wrong number of items matched filter criteria, expected " + count + ", but was "
            + items.getLength(), items.getLength() == count);
    }

    /**
     * Create an item as depositor and check if it will be found afterwards when filtering for all items.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testIssue898() throws Exception {
        try {
            final Map<String, String[]> filterParams = new HashMap<String, String[]>();

            filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1" });

            PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);

            String result = retrieveItems(filterParams);

            assertXmlValidSrwResponse(result);

            int oldItems = getNumberOfRecords(result);

            createItem();
            result = retrieveItems(filterParams);
            assertXmlValidSrwResponse(result);

            int newItems = getNumberOfRecords(result);

            assertTrue("Wrong number of items matched filter criteria, expected " + (oldItems + 1) + ", but was "
                + newItems, newItems == oldItems + 1);
        }
        finally {
            PWCallback.resetHandle();
        }
    }

    /**
     * Create a set of hierarchical containers containing items, give the test user hierarchical access to the top level
     * container and check if he is able to see all items.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testIssue902() throws Exception {
        final String USER_ID = "escidoc:test";

        try {
            // remove all grants for test user
            revokeGrantsForUser(USER_ID);

            // search for all items
            PWCallback.setHandle(PWCallback.TEST_HANDLE);

            final Map<String, String[]> filterParams = new HashMap<String, String[]>();

            filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1" });

            String result = retrieveItems(filterParams);

            PWCallback.resetHandle();
            assertXmlValidSrwResponse(result);

            int oldItems = getNumberOfRecords(result);

            // create container and item hierarchy
            String containerXml =
                EscidocAbstractTest.getTemplateAsString(TEMPLATE_CONTAINER_PATH + "/rest",
                    "create_container_WithoutMembers_v1.1.xml");
            String itemXml =
                EscidocAbstractTest
                    .getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
            String c1 = createContainer();

            getContainerClient().createItem(c1, itemXml);
            String c11 = getObjidValue(handleXmlResult(getContainerClient().createContainer(c1, containerXml)));
            getContainerClient().createItem(c11, itemXml);
            getContainerClient().createItem(c11, itemXml);
            String c12 = getObjidValue(handleXmlResult(getContainerClient().createContainer(c1, containerXml)));
            getContainerClient().createItem(c12, itemXml);
            getContainerClient().createItem(c12, itemXml);
            String c13 = getObjidValue(handleXmlResult(getContainerClient().createContainer(c1, containerXml)));
            getContainerClient().createItem(c13, itemXml);

            // give the user access right to the top level container
            String grantXml =
                getTemplateAsString(TEMPLATE_USER_ACCOUNT_PATH, "escidoc_replaceable_grant_for_create.xml");
            grantXml =
                grantXml.replaceAll("\\$\\{rolehref\\}",
                    "/aa/role/escidoc:role-collaborator-modifier-container-add-remove-any-members");
            grantXml = grantXml.replaceAll("\\$\\{scopehref\\}", "/ir/container/" + c1);

            Document grantDocument = EscidocAbstractTest.getDocument(grantXml);

            UserAccountClient userAccountClient = new UserAccountClient();

            userAccountClient.createGrant(USER_ID, toString(grantDocument, false));

            // search for all items again
            PWCallback.setHandle(PWCallback.TEST_HANDLE);
            result = retrieveItems(filterParams);
            assertXmlValidSrwResponse(result);

            int newItems = getNumberOfRecords(result);

            assertTrue("Wrong number of items matched filter criteria, expected " + (oldItems + 6) + ", but was "
                + newItems, newItems == oldItems + 6);
        }
        finally {
            PWCallback.resetHandle();
        }
    }

    /**
     * Create a user group, add the test user to the user group, give the user group access to a context, create an item
     * within that context and check if he is able to see the item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testIssue905() throws Exception {
        final String CONTEXT_ID = "escidoc:persistent3";
        final String USER_ID = "escidoc:test";

        try {
            // remove all grants for test user
            revokeGrantsForUser(USER_ID);

            // create user group
            Document userGroup =
                EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_USER_GROUP_PATH, "escidoc_usergroup_for_create.xml");
            Node labelNode = selectSingleNode(userGroup, "/user-group/properties/label");

            labelNode.setTextContent(labelNode.getTextContent().trim() + System.currentTimeMillis());

            UserGroupClient userGroupClient = new UserGroupClient();
            String userGroupXml = handleXmlResult(userGroupClient.create(toString(userGroup, false)));

            userGroup = EscidocAbstractTest.getDocument(userGroupXml);

            String userGroupId = getObjidValue(userGroupXml);

            // add test user to user group
            String taskParam = "<param last-modification-date=\"" + getLastModificationDateValue(userGroup) + "\">";

            taskParam += "<selector name=\"user-account\" type=\"internal\">" + USER_ID + "</selector>";
            taskParam += "</param>";
            userGroupClient.addSelectors(userGroupId, taskParam);

            // give the user group access right to the item
            String grantXml =
                getTemplateAsString(TEMPLATE_USER_ACCOUNT_PATH, "escidoc_replaceable_grant_for_create.xml");

            grantXml = grantXml.replaceAll("\\$\\{rolehref\\}", "/aa/role/escidoc:role-administrator");
            grantXml = grantXml.replaceAll("\\$\\{scopehref\\}", "/ir/context/" + CONTEXT_ID);

            Document grantDocument = EscidocAbstractTest.getDocument(grantXml);

            userGroupClient.createGrant(userGroupId, toString(grantDocument, false));

            // search for all items
            PWCallback.setHandle(PWCallback.ADMINISTRATOR_HANDLE);

            final Map<String, String[]> filterParams = new HashMap<String, String[]>();

            filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1" });

            String result = retrieveItems(filterParams);

            PWCallback.resetHandle();
            assertXmlValidSrwResponse(result);

            int oldItems = getNumberOfRecords(result);

            // create item
            createItem();

            // search for all items as administrator
            PWCallback.setHandle(PWCallback.ADMINISTRATOR_HANDLE);
            result = retrieveItems(filterParams);
            assertXmlValidSrwResponse(result);

            int newItems = getNumberOfRecords(result);

            assertTrue("Wrong number of items matched filter criteria, expected " + (oldItems + 1) + ", but was "
                + newItems, newItems == oldItems + 1);
        }
        finally {
            PWCallback.resetHandle();

            // remove all grants for test user
            revokeGrantsForUser(USER_ID);
        }
    }

    /**
     * Check if only items are returned from retrieveItems() if the filter query is not empty.
     * <p/>
     * See https://www.escidoc.org/jira/browse/INFR-1106.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testIssue1106() throws Exception {
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();
        String itemId = createItem();
        String containerId = createContainer();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"/properties/public-status\"=pending" });

        String result = retrieveItems(filterParams);

        assertXmlValidSrwResponse(result);

        Document resultDoc = EscidocAbstractTest.getDocument(result);
        NodeList nl = selectNodeList(resultDoc, XPATH_SRW_ITEM_LIST_ITEM);

        assertTrue("No items were retrieved.", nl.getLength() > 0);

        nl = selectNodeList(resultDoc, XPATH_SRW_CONTAINER_LIST_CONTAINER);
        assertEquals("Only items should be retrieved.", nl.getLength(), 0);

        delete(itemId);
        getContainerClient().delete(containerId);
    }

    /**
     * Test successfully retrieving an explain response.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testExplainRetrieveItems() throws Exception {
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_EXPLAIN, new String[] { "" });

        String result = null;

        try {
            result = retrieveItems(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
    }

    /**
     *
     * @param reqStatus
     * @param versionStatus
     * @throws Exception
     */
    public void doTestFilterItemsStatus(final String reqStatus, final boolean versionStatus) throws Exception {

        String filterName = FILTER_URI_PUBLIC_STATUS;
        String filterResultXPath = "/item/properties/public-status/text()";
        if (versionStatus) {
            filterName = FILTER_URI_VERSION_STATUS;
            filterResultXPath = "/item/properties/version/status/text()";
        }

        String list = null;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();
        StringBuffer filter = new StringBuffer("\"" + filterName + "\"=" + reqStatus);

        if (versionStatus) {
            filter.append(" and \"/properties/public-status\"=released");
        }
        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { filter.toString() });
        list = retrieveItems(filterParams);
        assertXmlValidSrwResponse(list);

        NodeList nodes = selectNodeList(EscidocAbstractTest.getDocument(list), XPATH_SRW_ITEM_LIST_ITEM + "/@href");

        for (int count = nodes.getLength() - 1; count >= 0; count--) {
            Node node = nodes.item(count);
            String nodeValue = getIdFromHrefValue(node.getNodeValue());

            try {
                String item = retrieve(nodeValue);
                String itemStatus =
                    selectSingleNode(EscidocAbstractTest.getDocument(item), filterResultXPath).getNodeValue();
                assertEquals(reqStatus, itemStatus);
            }
            catch (final ItemNotFoundException e) {
                if (reqStatus.equals(STATUS_WITHDRAWN)) {
                    EscidocAbstractTest.assertExceptionType(ItemNotFoundException.class, e);
                }
                else {
                    fail("No item could be retrieved with id " + nodeValue + " returned by retrieveItemRefs.");
                }
            }

        }
    }

    public void doTestFilterItemsContentModel(final String reqCT) throws Exception {
        String list = null;

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_URI_CONTENT_MODEL + "\"=" + reqCT });
        list = retrieveItems(filterParams);
        assertXmlValidSrwResponse(list);

        NodeList nodes = selectNodeList(EscidocAbstractTest.getDocument(list), XPATH_SRW_ITEM_LIST_ITEM + "/@href");

        for (int count = nodes.getLength() - 1; count >= 0; count--) {
            Node node = nodes.item(count);
            String nodeValue = getObjidFromHref(node.getNodeValue());
            String item = retrieve(nodeValue);
            String itemCT =
                selectSingleNode(EscidocAbstractTest.getDocument(item), "/item/properties/content-model/@href")
                    .getNodeValue();
            assertEquals(Constants.CONTENT_MODEL_BASE_URI + "/" + reqCT, itemCT);
        }
    }

    public void doTestFilterItemsUserRole(final String reqUser, final String reqRole) throws Exception {
        String list = null;

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();
        StringBuffer filter = new StringBuffer("\"user=" + reqUser);

        if (reqRole != null) {
            filter.append(" and role=" + reqRole);
        }
        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { filter.toString() });
        list = retrieveItems(filterParams);
        assertXmlValidSrwResponse(list);

        NodeList nodes = selectNodeList(EscidocAbstractTest.getDocument(list), XPATH_SRW_ITEM_LIST_ITEM + "/@href");

        for (int count = nodes.getLength() - 1; count >= 0; count--) {
            Node node = nodes.item(count);
            String nodeValue = getObjidFromHref(node.getNodeValue());
            try {
                retrieve(nodeValue);

                // TODO check result
                // String itemCT = selectSingleNode(getDocument(item),
                // "/item/properties/content-type/@href").getNodeValue();
                // assertEquals("/ccm/content-model/" + reqCT, itemCT);
            }
            catch (final ItemNotFoundException e) {
            }

        }
    }

    /**
     * Test retrieving Items sorted from the repository.
     *
     * @throws Exception
     */
    @Test
    public void testRetrieveItemsSorted() throws Exception {
        String itemXml = prepareItem(STATUS_PENDING, CONTEXT_ID, false, false);
        String lmd = getLastModificationDateValue(getDocument(itemXml));
        prepareItem(STATUS_SUBMITTED, CONTEXT_ID, false, false);
        prepareItem(STATUS_PENDING, CONTEXT_ID, false, false);
        prepareItem(STATUS_SUBMITTED, CONTEXT_ID, false, false);
        HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"PID\"=escidoc* and \"/last-modification-date\" >= "
            + lmd + " sortBy " + "\"/sort/properties/public-status\"/sort.ascending "
            + "\"/sort/last-modification-date\"/sort.descending" });
        String xml = retrieveItems(filterParams);

        assertXmlValidSrwResponse(xml);

        NodeList primNodes =
            selectNodeList(EscidocAbstractTest.getDocument(xml), XPATH_SRW_ITEM_LIST_ITEM + "/properties/public-status");
        NodeList secNodes =
            selectNodeList(EscidocAbstractTest.getDocument(xml), XPATH_SRW_ITEM_LIST_ITEM + "/@last-modification-date");
        assertEquals("search result doesnt contain expected number of hits", 4, primNodes.getLength());
        String lastPrim = LOWEST_COMPARABLE;
        String lastSec = HIGHEST_COMPARABLE;

        for (int count = 0; count < primNodes.getLength(); count++) {
            if (primNodes.item(count).getTextContent().compareTo(lastPrim) < 0) {
                assertTrue("wrong sortorder", false);
            }
            if (primNodes.item(count).getTextContent().compareTo(lastPrim) > 0) {
                lastSec = HIGHEST_COMPARABLE;
            }
            if (secNodes.item(count).getTextContent().compareTo(lastSec) > 0) {
                assertTrue("wrong sortorder", false);
            }
            lastPrim = primNodes.item(count).getTextContent();
            lastSec = secNodes.item(count).getTextContent();
        }
    }

    /**
     * Create a container (from template).
     *
     * @return objid of the container
     * @throws Exception Thrown if creation of the container failed.
     */
    private String createContainer() throws Exception {
        String xmlData =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_CONTAINER_PATH + "/rest",
                "create_container_WithoutMembers_v1.1.xml");
        String theContainerXml = handleXmlResult(getContainerClient().create(xmlData));

        return getObjidValue(theContainerXml);
    }

    /**
     * Create a Item (from template).
     *
     * @return Objid of Item.
     * @throws Exception Thrown if creation or extracting of objid failed.
     */
    private String createItem() throws Exception {
        String xmlData =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        theItemXml = create(xmlData);
        theItemId = getObjidValue(EscidocAbstractTest.getDocument(theItemXml));
        return theItemId;
    }

    /**
     * Create a Item (from template) and set it to status 'submitted'.
     *
     * @return Objid of Item.
     * @throws Exception Thrown if creation or extracting of objid failed.
     */
    private String createSubmittedItem() throws Exception {
        theItemId = createItem();
        submit(theItemId, getTheLastModificationParam(false, theItemId, null));

        return theItemId;
    }

    /**
     * Create a Item (from template) and set it to status 'released'. If the infrastructure requiers object or version
     * Pid to release an Item then is a dummy Pid for each type automatically assigned.
     *
     * @return Objid of Item.
     * @throws Exception Thrown if creation or extracting of objid failed.
     */
    private String createReleasedItem() throws Exception {
        theItemId = createSubmittedItem();

        String pidParam;
        if (getItemClient().getPidConfig("cmm.Item.objectPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.objectPid.releaseWithoutPid", "false")) {
            pidParam = getPidParam(theItemId, "http://somewhere" + this.theItemId);
            assignObjectPid(theItemId, pidParam);
        }
        if (getItemClient().getPidConfig("cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.versionPid.releaseWithoutPid", "false")) {
            String latestVersion = getLatestVersionObjidValue(theItemXml);
            pidParam = getPidParam(latestVersion, "http://somewhere" + latestVersion);
            assignVersionPid(latestVersion, pidParam);
        }

        release(theItemId, getTheLastModificationParam(false, theItemId, null));

        return theItemId;
    }

    private String createReleasedPendingItem() throws Exception {
        theItemId = createReleasedItem();
        Document newItem = EscidocAbstractTest.getDocument(retrieve(theItemId));
        selectSingleNode(newItem, "/item/properties/content-model-specific").appendChild(newItem.createElement("some"));
        update(theItemId, toString(newItem, true));

        return theItemId;
    }

    private String createReleasedSubmittedItem() throws Exception {
        theItemId = createReleasedPendingItem();
        submit(theItemId, getTheLastModificationParam(false, theItemId, null));

        return theItemId;
    }

    private String createReleasedReleasedItem() throws Exception {
        theItemId = createReleasedSubmittedItem();

        String pidParam;
        if (getItemClient().getPidConfig("cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.versionPid.releaseWithoutPid", "false")) {

            String latestVersion = getLatestVersionObjidValue(retrieve(theItemId));
            pidParam = getPidParam(latestVersion, "http://somewhere" + latestVersion);
            assignVersionPid(latestVersion, pidParam);
        }

        release(theItemId, getTheLastModificationParam(false, theItemId, null));

        return theItemId;
    }

    private String createReleasedReleasedWithdrawnItem() throws Exception {
        theItemId = createReleasedReleasedItem();
        withdraw(theItemId, getTheLastModificationParam(true, theItemId));

        return theItemId;
    }

    /**
     * Get the number of records from the SRW response without counting all result records explicitly.
     *
     * @param srwResponse SRW response from filter search
     * @return number of records
     * @throws Exception Thrown if parsing the SRW document failed.
     */
    private int getNumberOfRecords(final String srwResponse) throws Exception {
        return Integer.parseInt(selectSingleNode(EscidocAbstractTest.getDocument(srwResponse),
            "/searchRetrieveResponse/numberOfRecords").getTextContent());
    }

    private void revokeGrantsForUser(final String userId) throws Exception {
        String currentHandle = PWCallback.getHandle();

        try {
            PWCallback.resetHandle();

            UserAccountClient userAccountClient = new UserAccountClient();

            userAccountClient.revokeGrants(userId, "<param><filter/>"
                + "<revocation-remark>some remark</revocation-remark></param>");

            UserGroupClient userGroupClient = new UserGroupClient();
            final Map<String, String[]> filterParams = new HashMap<String, String[]>();

            filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_URI_USER + "\"=" + userId });

            String groupXml = handleXmlResult(userGroupClient.retrieveUserGroups(filterParams));
            NodeList userGroups =
                selectNodeList(EscidocAbstractTest.getDocument(groupXml), XPATH_SRW_USER_GROUP_LIST_USER_GROUP
                    + "/@href");
            ;

            for (int index = 0; index < userGroups.getLength(); index++) {
                Node userGroup = userGroups.item(index);
                String groupId = getObjidFromHref(userGroup.getNodeValue());

                userGroupClient.revokeGrants(groupId, "<param><filter/>"
                    + "<revocation-remark>some remark</revocation-remark>" + "</param>");
            }
        }
        finally {
            PWCallback.setHandle(currentHandle);
        }
    }
}
