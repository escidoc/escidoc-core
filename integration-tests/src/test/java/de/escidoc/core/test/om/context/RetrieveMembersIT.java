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
package de.escidoc.core.test.om.context;

import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContextNotFoundException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.om.container.ContainerTestBase;
import de.escidoc.core.test.om.item.ItemTestBase;
import de.escidoc.core.test.security.client.PWCallback;
import org.apache.http.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Test the task oriented method retrieveContexts.
 *
 * @author Michael Schneider
 */
public class RetrieveMembersIT extends ContextTestBase {

    public static final String XPATH_SRW_CONTEXT_LIST_MEMBER =
        XPATH_SRW_RESPONSE_RECORD + "/recordData/search-result-record";

    private String path = "";

    private static String contextId = null;

    private static int noOfItems = 0;

    private static int noOfPendingItems = 0;

    private static int noOfSubmittedItems = 0;

    private static int noOfReleasedItems = 0;

    private static int noOfWithdrawnItems = 0;

    private static int noOfContainers = 0;

    private static int noOfPendingContainers = 0;

    private static int noOfSubmittedContainers = 0;

    private static int noOfReleasedContainers = 0;

    private static int noOfWithdrawnContainers = 0;

    private static int noOfItemsOfCTPersistent4 = 0;

    private static int noOfContainersOfCTPersistent4 = 0;

    private static Collection<String> items = null;

    private static Collection<String> containers = null;

    private ItemTestBase itemBase = null;

    private ContainerTestBase containerBase = null;

    public RetrieveMembersIT() {
        this.itemBase = new ItemTestBase();
        this.containerBase = new ContainerTestBase();
    }

    /**
     * Prepare test environment.
     *
     * @throws Exception Thrown if anything fails.
     */
    private void prepare() throws Exception {

        contextId = createContext(CONTEXT_STATUS_OPENED);
        items = new LinkedList<String>();
        items.add(createItem(STATUS_PENDING, contextId, "escidoc:persistent4"));
        incNoOfPendingItems();
        noOfItemsOfCTPersistent4 = 1;
        items.add(createItem(STATUS_SUBMITTED, contextId, "escidoc:persistent4"));
        incNoOfSubmittedItems();
        noOfItemsOfCTPersistent4 = 2;
        items.add(createItem(STATUS_RELEASED, contextId, "escidoc:persistent6"));
        incNoOfReleasedItems();
        items.add(createItem(STATUS_WITHDRAWN, contextId, "escidoc:persistent6"));
        incNoOfWithdrawnItems();
        log("Created items: " + items);
        containers = new LinkedList<String>();
        containers.add(createContainer(STATUS_PENDING, contextId, "escidoc:persistent4"));
        incNoOfPendingContainers();
        containers.add(createContainer(STATUS_SUBMITTED, contextId, "escidoc:persistent4"));
        incNoOfSubmittedContainers();
        noOfContainersOfCTPersistent4 = 2;
        containers.add(createContainer(STATUS_RELEASED, contextId, "escidoc:persistent6"));
        incNoOfReleasedContainers();
        containers.add(createContainer(STATUS_WITHDRAWN, contextId, "escidoc:persistent6"));
        incNoOfWithdrawnContainers();
        log("Created containers: " + containers);
    }

    /**
     * Create Context in the expected status.
     *
     * @param expectedState The expected status of the Context.
     * @return The object Id of the created Context.
     * @throws Exception Thrown if anything fails.
     */
    private String createContext(final String expectedState) throws Exception {

        log("Create context in state '" + expectedState + "'");
        Document created =
            EscidocAbstractTest.getDocument(create(toString(substitute(EscidocAbstractTest.getTemplateAsDocument(
                TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml"), "/context/properties/name",
                getUniqueName("PubMan Context ")), false)));
        String objId = getObjidValue(created);

        if (CONTEXT_STATUS_CREATED.equals(expectedState)) {

        }
        else if (CONTEXT_STATUS_OPENED.equals(expectedState)) {
            open(objId, getTaskParam(getLastModificationDateValue(created)));
        }
        else if (CONTEXT_STATUS_CLOSED.equals(expectedState)) {
            // open(objId, getTaskParam(getLastModificationDateValue(created)));
            // taskParam = getTaskParam(getLastModificationDateValue(created))
            // close(objId, taskParam);
        }
        else {
            throw new Exception(expectedState + " is no valid context status!");
        }

        log("Created context '" + objId + "' in state '" + expectedState + "'");
        return objId;
    }

    /**
     * Create an Item and bring it to the expected state.
     *
     * @param expectedState One of the possible item states.
     * @param context       The expected context
     * @param contentType   The type of content.
     * @return The id of the item.
     * @throws Exception If anything fails.
     */
    private String createItem(final String expectedState, final String context, final String contentType)
        throws Exception {

        String result = null;
        log("Create item in state '" + expectedState + "'");
        Node create =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        if (context != null) {
            create = substitute(create, "/item/properties/context/@href", "/ir/context/" + context);
        }
        if (contentType != null) {
            create = substitute(create, "/item/properties/content-model/@href", "/cmm/content-model/" + contentType);
        }

        String template = toString(create, false);
        Document item = EscidocAbstractTest.getDocument(itemBase.create(template));
        result = getObjidValue(item);
        if (STATUS_PENDING.equals(expectedState)) {

        }
        else if (STATUS_SUBMITTED.equals(expectedState)) {
            log("Submit item '" + result + "'");
            itemBase.submit(result, getTaskParam(getLastModificationDateValue(item)));
        }
        else if (STATUS_RELEASED.equals(expectedState)) {
            log("Submit item '" + result + "'");
            itemBase.submit(result, getTaskParam(getLastModificationDateValue(item)));
            log("Release item '" + result + "'");
            itemBase.releaseWithPid(result);
        }
        else if (STATUS_WITHDRAWN.equals(expectedState)) {
            log("Submit item '" + result + "'");
            itemBase.submit(result, getTaskParam(getLastModificationDateValue(item)));
            log("Release item '" + result + "'");
            itemBase.releaseWithPid(result);
            log("Withdraw item '" + result + "'");
            itemBase.withdraw(result, getWithdrawTaskParam(getLastModificationDateValue(EscidocAbstractTest
                .getDocument(itemBase.retrieve(result))), "Withdrawn for Context retrieve members tests!"));
        }
        else {
            throw new Exception(expectedState + " is no valid item status!");
        }
        log("Created item '" + result + "' in state '" + expectedState + "'");
        return result;
    }

    /**
     * Create an Item and bring it to the expected state.
     *
     * @param expectedState One of the possible item states.
     * @param contextID     The id of the Context.
     * @param contentType   The type of the content.
     * @return The id of the Container.
     * @throws Exception If anything fails.
     */
    private String createContainer(final String expectedState, final String contextID, final String contentType)
        throws Exception {

        String objID = null;
        log("Create container in state '" + expectedState + "'");
        Node create = null;
        create =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTAINER_PATH + "/rest",
                "create_container_WithoutMembers_v1.1.xml");

        // -------------------------------------------------
        if (contextID != null) {
            create = substitute(create, "/container/properties/context/@href", "/ir/context/" + contextID);
        }
        // -------------------------------------------------
        if (contentType != null) {
            create =
                substitute(create, "/container/properties/content-model/@href", "/cmm/content-model/" + contentType);
        }
        Document container = EscidocAbstractTest.getDocument(containerBase.create(toString(create, false)));

        objID = getObjidValue(container);
        if (STATUS_PENDING.equals(expectedState)) {
            log("Expect pending container '" + objID + "'");
        }
        else if (STATUS_SUBMITTED.equals(expectedState)) {
            log("Submit container '" + objID + "'");
            containerBase.submit(objID, getTaskParam(getLastModificationDateValue(container)));
        }
        else if (STATUS_RELEASED.equals(expectedState)) {
            log("Submit container '" + objID + "'");
            containerBase.submit(objID, getTaskParam(getLastModificationDateValue(container)));
            log("Release container '" + objID + "'");
            containerBase.releaseWithPid(objID);
        }
        else if (STATUS_WITHDRAWN.equals(expectedState)) {
            log("Submit container '" + objID + "'");
            containerBase.submit(objID, getTaskParam(getLastModificationDateValue(container)));
            log("Release container '" + objID + "'");

            containerBase.releaseWithPid(objID);
            log("Withdraw container '" + objID + "'");
            containerBase.withdraw(objID, getWithdrawTaskParam(getLastModificationDateValue(EscidocAbstractTest
                .getDocument(containerBase.retrieve(objID))), "Withdrawn for Context retrieve members tests!"));
        }
        else {
            throw new Exception(expectedState + " is no valid container status!");
        }
        log("Created container '" + objID + "' in state '" + expectedState + "'");
        return objID;
    }

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void setUp() throws Exception {
        this.path = "/rest";

        if (contextId == null) {
            prepare();
            logCounts();
        }
    }

    /**
     * Log.
     */
    private void logCounts() {

        log("Member counts:");
        log("noOfItems                   = '" + noOfItems + "' for context = '" + contextId + "'");
        log("noOfPendingItems            = '" + noOfPendingItems + "' for context = '" + contextId + "'");
        log("noOfSubmittedItems          = '" + noOfSubmittedItems + "' for context = '" + contextId + "'");
        log("noOfReleasedItems           = '" + noOfReleasedItems + "' for context = '" + contextId + "'");
        log("noOfWithdrawnItems          = '" + noOfWithdrawnItems + "' for context = '" + contextId + "'");

        log("noOfContainers              = '" + noOfContainers + "' for context = '" + contextId + "'");
        log("noOfPendingContainers       = '" + noOfPendingContainers + "' for context = '" + contextId + "'");
        log("noOfSubmittedContainers     = '" + noOfSubmittedContainers + "' for context = '" + contextId + "'");
        log("noOfReleasedContainers      = '" + noOfReleasedContainers + "' for context = '" + contextId + "'");
        log("noOfWithdrawnContainers     = '" + noOfWithdrawnContainers + "' for context = '" + contextId + "'");

        log("noOfItemsOfContentType      = '" + noOfItemsOfCTPersistent4 + "' for context = '" + contextId + "'");
        log("noOfContainersOfContentType = '" + noOfContainersOfCTPersistent4 + "' for context = '" + contextId + "'");
        log("=======================");
    }

    /**
     * Clean up after servlet test.
     *
     * @throws Exception If anything fails.
     */
    @After
    @Override
    public void tearDown() throws Exception {

        super.tearDown();
    }

    /**
     * Test retrieve all members of a context.
     *
     * @throws Exception If something unexpected goes wrong.
     */
    @Test
    public void testOmRmf1a() throws Exception {

        String members = retrieveMembers(contextId, getFilterRetrieveContexts(null, null, null));

        assertXmlValidSrwResponse(members);

        Document membersDoc = EscidocAbstractTest.getDocument(members);

        assertEquals("Some items have another context!", noOfItems, getNoOfSelections(membersDoc,
            XPATH_SRW_CONTEXT_LIST_MEMBER + "/item/properties/context[@href=\"/ir/context/" + contextId + "\"]"));
        assertEquals("Some containers have another context!", noOfContainers, getNoOfSelections(membersDoc,
            XPATH_SRW_CONTEXT_LIST_MEMBER + "/container/properties/context[@href=\"/ir/context/" + contextId + "\"]"));
        assertEquals("Wrong number of pending items retrieved!", noOfPendingItems, getNoOfSelections(membersDoc,
            XPATH_SRW_CONTEXT_LIST_MEMBER + "/item/properties[public-status=\"" + STATUS_PENDING + "\"]"));
        assertEquals("Wrong number of submitted items retrieved!", noOfSubmittedItems, getNoOfSelections(membersDoc,
            XPATH_SRW_CONTEXT_LIST_MEMBER + "/item/properties[public-status=\"" + STATUS_SUBMITTED + "\"]"));
        assertEquals("Wrong number of released items retrieved!", noOfReleasedItems, getNoOfSelections(membersDoc,
            XPATH_SRW_CONTEXT_LIST_MEMBER + "/item/properties[public-status=\"" + STATUS_RELEASED + "\"]"));
        assertEquals("Wrong number of withdrawn items retrieved!", noOfWithdrawnItems, getNoOfSelections(membersDoc,
            XPATH_SRW_CONTEXT_LIST_MEMBER + "/item/properties[public-status=\"" + STATUS_WITHDRAWN + "\"]"));

        assertEquals("Wrong number of pending containers retrieved!", noOfPendingContainers, getNoOfSelections(
            membersDoc, XPATH_SRW_CONTEXT_LIST_MEMBER + "/item/properties[public-status=\"" + STATUS_PENDING + "\"]"));
        assertEquals("Wrong number of submitted containers retrieved!", noOfSubmittedContainers, getNoOfSelections(
            membersDoc, XPATH_SRW_CONTEXT_LIST_MEMBER + "/item/properties[public-status=\"" + STATUS_SUBMITTED + "\"]"));
        assertEquals("Wrong number of released containers retrieved!", noOfReleasedContainers, getNoOfSelections(
            membersDoc, XPATH_SRW_CONTEXT_LIST_MEMBER + "/item/properties[public-status=\"" + STATUS_RELEASED + "\"]"));
        assertEquals("Wrong number of withdrawn containers retrieved!", noOfWithdrawnContainers, getNoOfSelections(
            membersDoc, XPATH_SRW_CONTEXT_LIST_MEMBER + "/container/properties[public-status=\"" + STATUS_WITHDRAWN
                + "\"]"));
    }

    /**
     * Test retrieve all members of a context which are items.
     *
     * @throws Exception If something unexpected goes wrong.
     */
    @Test
    public void testOmRmf1b() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"type\"=item" });

        String members = retrieveMembers(contextId, filterParams);

        assertXmlValidSrwResponse(members);
        assertEquals("Type filter doesn't work!", noOfItems, getNoOfSelections(
            EscidocAbstractTest.getDocument(members), XPATH_SRW_CONTEXT_LIST_MEMBER + "/item"));
    }

    /**
     * Test retrieve all members of a context which are containers.
     *
     * @throws Exception If something unexpected goes wrong.
     */
    @Test
    public void testOmRmf1c() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"type\"=container" });

        String members = retrieveMembers(contextId, filterParams);

        assertXmlValidSrwResponse(members);
        assertEquals("Type filter doesn't work!", noOfContainers, getNoOfSelections(EscidocAbstractTest
            .getDocument(members), XPATH_SRW_CONTEXT_LIST_MEMBER + "/container"));
    }

    /**
     * Test retrieve all members of a context with status pending.
     *
     * @throws Exception If something unexpected goes wrong.
     */
    @Test
    public void testOmRmf1d() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"/properties/public-status\"=" + STATUS_PENDING });

        String members = retrieveMembers(contextId, filterParams);

        assertXmlValidSrwResponse(members);
        assertEquals("Status filter doesn't work!", noOfPendingItems, getNoOfSelections(EscidocAbstractTest
            .getDocument(members), XPATH_SRW_CONTEXT_LIST_MEMBER + "/item"));
        assertEquals("Status filter doesn't work!", noOfPendingContainers, getNoOfSelections(EscidocAbstractTest
            .getDocument(members), XPATH_SRW_CONTEXT_LIST_MEMBER + "/container"));
    }

    /**
     * Test retrieve all members of a context with status submitted.
     *
     * @throws Exception If something unexpected goes wrong.
     */
    @Test
    public void testOmRmf1e() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"/properties/public-status\"=" + STATUS_SUBMITTED });

        String members = retrieveMembers(contextId, filterParams);

        assertXmlValidSrwResponse(members);
        assertEquals("Status filter doesn't work!", noOfSubmittedItems, getNoOfSelections(EscidocAbstractTest
            .getDocument(members), XPATH_SRW_CONTEXT_LIST_MEMBER + "/item"));
        assertEquals("Status filter doesn't work!", noOfPendingContainers, getNoOfSelections(EscidocAbstractTest
            .getDocument(members), XPATH_SRW_CONTEXT_LIST_MEMBER + "/container"));
    }

    /**
     * Test retrieve all members of a context with a specific content-type.
     *
     * @throws Exception If something unexpected goes wrong.
     */
    @Test
    public void testOmRmf1f() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY,
            new String[] { "\"/properties/content-model/id\"=escidoc:persistent4" });

        String members = retrieveMembers(contextId, filterParams);

        assertXmlValidSrwResponse(members);
        assertEquals("Content type filter doesn't work!", noOfItemsOfCTPersistent4, getNoOfSelections(
            EscidocAbstractTest.getDocument(members), XPATH_SRW_CONTEXT_LIST_MEMBER + "/item"));
        assertEquals("Content type doesn't work!", noOfContainersOfCTPersistent4, getNoOfSelections(EscidocAbstractTest
            .getDocument(members), XPATH_SRW_CONTEXT_LIST_MEMBER + "/container"));
    }

    /**
     * Test retrieve all items of a context with a specific content-type.
     *
     * @throws Exception If something unexpected goes wrong.
     */
    @Test
    public void testOmRmf1g() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"type\"=item and "
            + "\"/properties/content-model/id\"=escidoc:persistent4" });

        String members = retrieveMembers(contextId, filterParams);

        assertXmlValidSrwResponse(members);
        assertEquals("Content type filter doesn't work!", noOfItemsOfCTPersistent4, getNoOfSelections(
            EscidocAbstractTest.getDocument(members), XPATH_SRW_CONTEXT_LIST_MEMBER + "/item"));
    }

    /**
     * Test retrieve all containers of a context with a specific content-type.
     *
     * @throws Exception If something unexpected goes wrong.
     */
    @Test
    public void testOmRmf1h() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"type\"=container and "
            + "\"/properties/content-model/id\"=escidoc:persistent4" });

        String members = retrieveMembers(contextId, filterParams);

        assertXmlValidSrwResponse(members);
        assertEquals("Content type doesn't work!", noOfContainersOfCTPersistent4, getNoOfSelections(EscidocAbstractTest
            .getDocument(members), XPATH_SRW_CONTEXT_LIST_MEMBER + "/container"));
    }

    /**
     * Test retrieve all items of a context with a specific status and content-type.
     *
     * @throws Exception If something unexpected goes wrong.
     */
    @Test
    public void testOmRmf1i() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"type\"=item and "
            + "\"/properties/content-model/id\"=escidoc:persistent4 and " + "\"/properties/public-status\"="
            + STATUS_PENDING });

        String members = retrieveMembers(contextId, filterParams);

        assertXmlValidSrwResponse(members);
        assertEquals("Content type filter doesn't work!", noOfPendingItems, getNoOfSelections(EscidocAbstractTest
            .getDocument(members), XPATH_SRW_CONTEXT_LIST_MEMBER + "/item"));
    }

    /**
     * Test retrieve all containers of a context with a specific status and content-type.
     *
     * @throws Exception If something unexpected goes wrong.
     */
    @Test
    public void testOmRmf1j() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"type\"=container and "
            + "\"/properties/content-model/id\"=escidoc:persistent4 and " + "\"/properties/public-status\"="
            + STATUS_SUBMITTED });

        String members = retrieveMembers(contextId, filterParams);

        assertXmlValidSrwResponse(members);
        assertEquals("Content type doesn't work!", noOfSubmittedContainers, getNoOfSelections(EscidocAbstractTest
            .getDocument(members), XPATH_SRW_CONTEXT_LIST_MEMBER + "/container"));
    }

    /**
     * Test retrieve all members of a not existing context.
     *
     * @throws Exception If something unexpected goes wrong.
     */
    @Test(expected = ContextNotFoundException.class)
    public void testOmRmf2() throws Exception {

        retrieveMembers("escidoc:unknown1", new HashMap<String, String[]>());
    }

    /**
     * Test retrieve the members of a context. The context id is null
     *
     * @throws Exception If something unexpected goes wrong.
     */
    @Test(expected = MissingMethodParameterException.class)
    public void testOmRmf4a() throws Exception {

        retrieveMembers(null, new HashMap<String, String[]>());
    }

    /**
     * Test retrieve the members of a context. The context id and the filter are null.
     * <p/>
     * This test does not work in SOAP because in ClientBase.callSoapMethod() all null parameters are mapped to the type
     * String which leads to a call to retrieveMembers(String, String) which is not defined.
     *
     * @throws Exception If something unexpected goes wrong.
     */
    @Test
    public void testOmRmf4c() throws Exception {
        try {
            retrieveMembers(null, null);
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (Exception e) {
            EscidocAbstractTest.assertExceptionType(MissingMethodParameterException.class, e);
        }
    }

    /**
     * Increments the static variables noOfPendingItems and noOfItems.
     */
    public static void incNoOfPendingItems() {
        noOfPendingItems += 1;
        noOfItems += 1;
    }

    /**
     * Increments the static variables noOfSubmittedItems and noOfItems.
     */
    public static void incNoOfSubmittedItems() {
        noOfSubmittedItems += 1;
        noOfItems += 1;
    }

    /**
     * Increments the static variables noOfReleasedItems and noOfItems.
     */
    public static void incNoOfReleasedItems() {
        noOfReleasedItems += 1;
        noOfItems += 1;
    }

    /**
     * Increments the static variables noOfWithdrawnItems and noOfItems.
     */
    public static void incNoOfWithdrawnItems() {
        noOfWithdrawnItems += 1;
        noOfItems += 1;
    }

    /**
     * Increments the static variables noOfPendingContainers and noOfContainers.
     */
    public static void incNoOfPendingContainers() {
        noOfPendingContainers += 1;
        noOfContainers += 1;
    }

    /**
     * Increments the static variables noOfSubmittedContainers and noOfContainers.
     */
    public static void incNoOfSubmittedContainers() {
        noOfSubmittedContainers += 1;
        noOfContainers += 1;
    }

    /**
     * Increments the static variables noOfReleasedContainers and noOfContainers.
     */
    public static void incNoOfReleasedContainers() {
        noOfReleasedContainers += 1;
        noOfContainers += 1;
    }

    /**
     * Increments the static variables noOfWithdrawnItems and noOfItems.
     */
    public static void incNoOfWithdrawnContainers() {
        noOfWithdrawnContainers += 1;
        noOfContainers += 1;
    }

    @Test
    public void testFilterMemberUserRoleWoRole() throws Exception {

        doTestFilterMembersUserRole(contextId, PWCallback.ID_PREFIX + PWCallback.DEFAULT_HANDLE, null);
    }

    @Test
    public void testFilterMemberUserRoleNonexistingUser() throws Exception {
        doTestFilterMembersUserRole(contextId, "escidoc:userX", "System-Administrator");
    }

    @Test
    public void testFilterMemberUserRoleAdmin() throws Exception {
        doTestFilterMembersUserRole(contextId, PWCallback.ID_PREFIX + PWCallback.DEFAULT_HANDLE, "System-Administrator");
    }

    public void doTestFilterMembersUserRole(final String id, final String reqUser, final String reqRole)
        throws Exception {

        String list = null;
        final StringBuffer filter = new StringBuffer();
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        if (reqUser != null) {
            filter.append("user=" + reqUser);
        }
        if (reqRole != null) {
            if (filter.length() > 0) {
                filter.append(" and ");
            }
            filter.append("role=" + reqRole);
        }
        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { filter.toString() });
        list = retrieveMembers(id, filterParams);

        NodeList items =
            selectNodeList(EscidocAbstractTest.getDocument(list), XPATH_SRW_CONTEXT_LIST_MEMBER + "/item/@objid");
        NodeList containers =
            selectNodeList(EscidocAbstractTest.getDocument(list), XPATH_SRW_CONTEXT_LIST_MEMBER + "/container/@objid");

        for (int count = containers.getLength() - 1; count >= 0; count--) {
            Node node = containers.item(count);
            String nodeValue = node.getNodeValue();
            Object result = getContainerClient().retrieve(nodeValue);

            if (result instanceof HttpResponse) {
                HttpResponse httpRes = (HttpResponse) result;
                assertHttpStatusOfMethod("", httpRes);
            }
        }

        for (int count = items.getLength() - 1; count >= 0; count--) {
            Node node = items.item(count);
            String nodeValue = node.getNodeValue();
            Object result = getItemClient().retrieve(nodeValue);

            if (result instanceof HttpResponse) {
                HttpResponse httpRes = (HttpResponse) result;
                assertHttpStatusOfMethod("", httpRes);
            }
        }
    }

    /**
     * Test successfully retrieving an explain response.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testExplainRetrieveMembers() throws Exception {
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_EXPLAIN, new String[] { "" });

        String result = null;

        try {
            result = retrieveMembers(contextId, filterParams);
        }
        catch (Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
    }
}
