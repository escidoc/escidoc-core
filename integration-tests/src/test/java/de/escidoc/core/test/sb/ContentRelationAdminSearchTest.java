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
package de.escidoc.core.test.sb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.common.client.servlet.HttpHelper;
import de.escidoc.core.test.security.client.PWCallback;

/**
 * Test the implementation of the admin search for content-relations.
 * 
 * @author MIH
 * 
 */
public class ContentRelationAdminSearchTest extends SearchTestBase {

    private static final String INDEX_NAME = "content_relation_admin";

    private static final ArrayList<String> RESULT_XPATHS = 
        new ArrayList<String>() {
            private static final long serialVersionUID = 1L;
            { 
                add("/searchRetrieveResponse/records/record/"
                        + "recordData/search-result-record/context");
            }
            };

    private static String[] contentRelationIds = null;

    private static int methodCounter = 0;

    private static String startTime = "";

    /**
     * @param transport
     *            The transport identifier.
     * @throws Exception
     *             e
     */
    public ContentRelationAdminSearchTest(final int transport) throws Exception {
        super(transport);
        contentRelation = new ContentRelationHelper(transport);
        grant = new GrantHelper(
                transport, GrantHelper.getUserAccountHandlerCode());
    }

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Before
    public void initialize() throws Exception {
        if (methodCounter == 0) {
            prepare();
//          int c = 213125;
//          contentRelationIds = new String[4];
//          for (int i = 0; i < 4; i++) {
//              contentRelationIds[i] = "escidoc:" + c;
//              c++;
//          }
        }
    }

    /**
     * Clean up after servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @After
    public void deinitialize() throws Exception {
        methodCounter++;
    }

    /**
     * insert item(s) into system for the tests.
     * 
     * @test.name prepare
     * @test.id PREPARE
     * @test.input
     * @test.inputDescription
     * @test.expected
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    private void prepare() throws Exception {
        log.info("starting SearchTest at "
                + new DateTime(System.currentTimeMillis()
                        + (60 * 60 * 1000), DateTimeZone.UTC).toString());
        // create empty indices/////////////////////////////////////////////////
        String urlParameters =
                "?operation=updateIndex" + "&action=createEmpty"
                        + "&repositoryName=escidocrepository" + "&indexName=";
        String httpUrl =
                HttpHelper
                        .createUrl(
                                de.escidoc.core.test.common.client.servlet.Constants.PROTOCOL,
                                de.escidoc.core.test.common.client.servlet.Constants.HOST_PORT,
                                de.escidoc.core.test.common.client.servlet.Constants.FEDORAGSEARCH_BASE_URI
                                        + urlParameters);
        HttpHelper
                .executeHttpMethod(
                        de.escidoc.core.test.common.client.servlet.Constants.HTTP_METHOD_GET,
                        httpUrl, null, null, null, null);
        // /////////////////////////////////////////////////////////////////////

        startTime =
                new DateTime(System.currentTimeMillis(), DateTimeZone.UTC)
                        .toString();
        // Create Contexts with different status/////////////////////////////////////////////////
        String handle = PWCallback.CONTEXT_ADMINISTRATOR_HANDLE;
        contentRelationIds = new String[5];
        contentRelationIds[0] = prepareContentRelation(PWCallback.SYSTEMADMINISTRATOR_HANDLE, 
                "escidoc_search_content_relation0_" + getTransport(false) + ".xml", 
                CONTEXT_STATUS_CREATED);
        contentRelationIds[1] = prepareContentRelation(handle, 
                "escidoc_search_content_relation0_" + getTransport(false) + ".xml", 
                CONTEXT_STATUS_CREATED);
        contentRelationIds[2] = prepareContentRelation(handle, 
                "escidoc_search_content_relation0_" + getTransport(false) + ".xml", 
                CONTEXT_STATUS_OPENED);
        contentRelationIds[3] = prepareContentRelation(handle, 
                "escidoc_search_content_relation0_" + getTransport(false) + ".xml", 
                CONTEXT_STATUS_CLOSED);
        contentRelationIds[4] = prepareContentRelation(handle, 
                "escidoc_search_content_relation0_" + getTransport(false) + ".xml", 
                CONTEXT_STATUS_DELETED);

        // /////////////////////////////////////////////////////////////////////

        waitForIndexerToAppear(contentRelationIds[3], INDEX_NAME);
        Thread.sleep(60000);
    }

    /**
     * explain operation without parameters for existing database xyz.
     * 
     * @test.name explain (1)
     * @test.id SB_EX-1
     * @test.input
     * @test.inputDescription
     * @test.expected explain plan for the corresponding database according
     *                ZeeRex Schema
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBEX1() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        String response = explain(parameters, INDEX_NAME);
        assertXmlValidExplainPlan(response);
        assertEquals("srw/search/" + INDEX_NAME, getDatabase(response));
        assertEquals(Constants.CONTEXT_INDEX_FIELD_COUNT,
                                            getIndexFieldCount(response));
        assertEquals(Constants.CONTEXT_SORT_FIELD_COUNT,
                                            getSortFieldCount(response));
    }

    /**
     * Test searching as anonymous user.
     * 
     * @test.name Anonymous User Search
     * @test.id SB_AnonymousUserSearch
     * @test.input anonymous user searching all objects
     * @test.expected 2 hits.
     *              Anonymous may see Contexts in public-status open + closed
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSearchAsAnonymousUser() throws Exception {
        HashMap<String, Object> role = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;
            {
                put("handle", PWCallback.ANONYMOUS_HANDLE);
                put("expectedHits", "2");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;
                    {
                        put(contentRelationIds[2], getContextXpathList(2));
                        put(contentRelationIds[3], getContextXpathList(3));
                    }
                });
            }
        };
        search(role);
    }

    /**
     * Test searching as Systemadministrator user.
     * 
     * @test.name Systemadministrator User Search
     * @test.id SB_SystemadministratorUserSearch
     * @test.input Systemadministrator user searching all objects
     * @test.expected 4 hits.
     *              Systemadministrator may see all Contexts
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSearchAsSystemadministratorUser() throws Exception {
        HashMap<String, Object> role = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;
            {
                put("role0",
                               GrantHelper.ROLE_HREF_SYSTEM_ADMINISTRATOR);
                put("handle", PWCallback.TEST_HANDLE1);
                put("user", TEST_USER_ACCOUNT_ID1);
                put("expectedHits", "4");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;
                    {
                        put(contentRelationIds[0], getContextXpathList(0));
                        put(contentRelationIds[1], getContextXpathList(1));
                        put(contentRelationIds[2], getContextXpathList(2));
                        put(contentRelationIds[3], getContextXpathList(3));
                    }
                });
            }
        };
        search(role);
    }

    /**
     * Test searching as Systeminspector user.
     * 
     * @test.name Systeminspector User Search
     * @test.id SB_SysteminspectorUserSearch
     * @test.input Systeminspector user searching all objects
     * @test.expected 4 hits.
     *              Systeminspector may see all OrgUnits
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSearchAsSysteminspectorUser() throws Exception {
        HashMap<String, Object> role = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;
            {
                put("role0",
                               GrantHelper.ROLE_HREF_SYSTEM_INSPECTOR);
                put("handle", PWCallback.TEST_HANDLE1);
                put("user", TEST_USER_ACCOUNT_ID1);
                put("expectedHits", "4");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;
                    {
                        put(contentRelationIds[0], getContextXpathList(0));
                        put(contentRelationIds[1], getContextXpathList(1));
                        put(contentRelationIds[2], getContextXpathList(2));
                        put(contentRelationIds[3], getContextXpathList(3));
                    }
                });
            }
        };
        search(role);
    }

    /**
     * Test searching as Administrator user.
     * 
     * @test.name Administrator User Search
     * @test.id SB_AdministratorUserSearch
     * @test.input Administrator user searching all objects
     *              scope on context[0].
     * @test.expected 3 hits.
     *              Administrator may see the Context 
     *              (s)he has scoped.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSearchAsAdminUser() throws Exception {
        HashMap<String, Object> role = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;
            {
                put("role0",
                               GrantHelper.ROLE_HREF_ADMINISTRATOR);
                put("scope0", de.escidoc.core.test.common.client
                        .servlet.Constants.CONTEXT_BASE_URI
                        + "/" + contentRelationIds[0]);
                put("handle", PWCallback.TEST_HANDLE1);
                put("user", TEST_USER_ACCOUNT_ID1);
                put("expectedHits", "3");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;
                    {
                        put(contentRelationIds[0], getContextXpathList(0));
                        put(contentRelationIds[2], getContextXpathList(2));
                        put(contentRelationIds[3], getContextXpathList(3));
                    }
                });
            }
        };
        search(role);
    }

    /**
     * Test searching as Context-Administrator user.
     * 
     * @test.name Context-Administrator User Search
     * @test.id SB_Context-AdministratorUserSearch
     * @test.input ContextAdministrator user searching all objects
     * @test.expected 3 hits.
     *              Context-Administrator may see the Contexts 
     *              (s)he has created.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSearchAsContextAdminUser() throws Exception {
        HashMap<String, Object> role = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;
            {
                put("handle", PWCallback.CONTEXT_ADMINISTRATOR_HANDLE);
                put("expectedHits", "3");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;
                    {
                        put(contentRelationIds[1], getContextXpathList(1));
                        put(contentRelationIds[2], getContextXpathList(2));
                        put(contentRelationIds[3], getContextXpathList(3));
                    }
                });
            }
        };
        search(role);
    }

    /**
     * Test searching as Context-Modifier user.
     * 
     * @test.name Context-Modifier User Search
     * @test.id SB_Context-ModifierUserSearch
     * @test.input Context-Modifier user searching all objects
     *              scope on context[0].
     * @test.expected 3 hits.
     *              Context-Modifier may see the Context 
     *              (s)he has scoped.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSearchAsContextModifierUser() throws Exception {
        HashMap<String, Object> role = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;
            {
                put("role0",
                               GrantHelper.ROLE_HREF_CONTEXT_MODIFIER);
                put("scope0", de.escidoc.core.test.common.client
                        .servlet.Constants.CONTEXT_BASE_URI
                        + "/" + contentRelationIds[1]);
                put("handle", PWCallback.TEST_HANDLE1);
                put("user", TEST_USER_ACCOUNT_ID1);
                put("expectedHits", "3");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;
                    {
                        put(contentRelationIds[1], getContextXpathList(1));
                        put(contentRelationIds[2], getContextXpathList(2));
                        put(contentRelationIds[3], getContextXpathList(3));
                    }
                });
            }
        };
        search(role);
    }

    /**
     * search with roles provided in HashMap.
     * 
     * @param role
     *            parameters
     * @throws Exception
     *             If anything fails.
     */
    private void search(final HashMap<String, Object> role) throws Exception {
        StringBuffer errorTrace = new StringBuffer();
        errorTrace.append("handle: ").append(role.get("handle")).append("\n");
        try {
            for (int i = 0;; i++) {
                if ((String) role.get("role" + i) == null) {
                    break;
                }
                errorTrace.append("role: ")
                        .append(role.get("role" + i)).append("\n");
                errorTrace.append("scope: ")
                        .append(role.get("scope" + i)).append("\n");
                grant.doTestCreateGrant(
                        null,
                        (String) role.get("user"),
                        (String) role.get("scope" + i),
                        (String) role.get("role" + i), null);
            }
            PWCallback.setHandle((String) role.get("handle"));
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("query", "PID=escidoc*");
            parameters.put("maximumRecords", "150");
            String response = search(parameters, INDEX_NAME);
            assertXmlValidSearchResult(response);
            Document searchResultDoc = getDocument(response, true);
            Node n = selectSingleNode(searchResultDoc,
                    "/searchRetrieveResponse/diagnostics/diagnostic/details");
            String textContent = null;
            if (n != null) {
                textContent = n.getTextContent();
            }
            assertEquals(errorTrace.toString() + "diagnostics: "
                    + textContent, null, n);
//            assertEquals(true, checkHighlighting(response));
            assertEquals(errorTrace.toString()
                    + "hits not as expected: expected: "
                    + role.get("expectedHits")
                    + ", but was "
                    + getNumberOfHits(response)
                    + " for ",
                    role.get("expectedHits"), getNumberOfHits(response));
            HashSet<String> foundIds = new HashSet<String>();
            // check if all items in result may be there
            for (String xPath : RESULT_XPATHS) {
                NodeList nodes = selectNodeList(
                        searchResultDoc, xPath);
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    String objId = getObjidValue(
                            de.escidoc.core.test.common.client
                            .servlet.Constants.TRANSPORT_SOAP, node, null);
                    foundIds.add(objId);
                    assertTrue(errorTrace.toString()
                            + "object " + objId + " may not be in searchResult",
                            ((HashMap<String, String>) role
                                    .get("searchresultIds")).containsKey(objId));
                    ArrayList<String> searchIds = 
                        ((HashMap<String, ArrayList<String>>) role
                            .get("searchresultIds")).get(objId);
                    if (searchIds != null) {
                        for (String searchId : searchIds) {
                            String[] parts = searchId.split("=");
                            assertXmlEquals(errorTrace.toString()
                                    + "not expected value in "
                                    + parts[0] + " for objectId " 
                                    + objId, node, parts[0], parts[1]);
                        }
                    }
                }
            }
            // check if all objects that should be in result are there
            for (String id : ((HashMap<String, String>) role
                    .get("searchresultIds")).keySet()) {
                assertTrue(errorTrace.toString()
                        + id + " was not in searchResult",
                        foundIds.contains(id));

            }
        } finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
            if (role.get("role0") != null) {
                grant.revokeAllGrants((String) role.get("user"));
            }
        }
    }

    /**
     * prepare item for tests.
     * 
     * @param creatorHandle
     *            handle of creator
     * @param contextId
     *            id of context to create item in
     * @param containerId
     *            id of container to create item in
     * @param templateName
     *            template for item to create
     * @param status
     *            status of item to create
     * @return HashMap id of created item + componentIds
     * @throws Exception
     *             If anything fails.
     */
    private HashMap<String, String> prepareItem(
                final String creatorHandle,
                final String contextId,
                final String[] containerIds,
                final String templateName,
                final String status) throws Exception {
        HashMap<String, String> returnHash = new HashMap<String, String>();
        try {
            if (creatorHandle != null) {
                PWCallback.setHandle(creatorHandle);
            }
            Document xmlData =
                    EscidocRestSoapTestsBase.getTemplateAsDocument(
                            TEMPLATE_ITEM_SEARCH_ADMIN_PATH, templateName);
            if (getTransport() == de.escidoc.core.test.common.client.servlet.Constants.TRANSPORT_REST) {
                String contextHref = de.escidoc.core.test.common
                        .client.servlet.Constants.CONTEXT_BASE_URI + "/"
                        + contextId;
                substitute(xmlData,
                        "/item/properties/context/@href", contextHref);
            } else {
                substitute(xmlData,
                        "/item/properties/context/@objid", contextId);
            }
            String xml = item.create(toString(xmlData, false));
            String objectId = getId(xml);
            xml = xml.replaceAll("Meier", "Meier1");
            xml = item.update(objectId, xml);
            String lastModDate = getLastModificationDate(xml);
            Document itemDoc = EscidocRestSoapTestsBase.getDocument(xml);
            returnHash.put("itemId", objectId);
            for (int i = 1;; i++) {
                try {
                    String componentId = getComponentObjidValue(itemDoc, i);
                    if (componentId == null) {
                        break;
                    }
                    returnHash.put("componentId" + i, componentId);
                } catch (NullPointerException e) {
                    break;
                }
            }

            if (!status.equals(STATUS_PENDING)) {
                // submit item
                item.submit(objectId, "<param last-modification-date=\""
                        + lastModDate + "\" />");
                xml = item.retrieve(objectId);
                xml = xml.replaceAll("Meier", "Meier1");
                xml = item.update(objectId, xml);

                if (!status.equals(STATUS_SUBMITTED)) {
                    // assignPids
                    xml = item.retrieve(objectId);
                    String componentId = getComponentObjidValue(itemDoc, 1);
                    String pidParam = getItemPidParam(objectId);
                    item.assignContentPid(objectId, componentId, pidParam);
                    pidParam = getItemPidParam(objectId);
                    item.assignObjectPid(objectId, pidParam);
                    Node n = selectSingleNode(getDocument(xml),
                            "/item/properties/version/number");
                    String versionNumber = n.getTextContent();
                    String versionId = objectId + ":" + versionNumber;
                    pidParam = getItemPidParam(versionId);
                    item.assignVersionPid(versionId, pidParam);
                    // }

                    // release item
                    xml = item.retrieve(objectId);
                    lastModDate = getLastModificationDate(xml);
                    item.release(objectId, "<param last-modification-date=\""
                            + lastModDate + "\" />");
                    if (!status.equals(STATUS_RELEASED)
                            && !status.equals(STATUS_WITHDRAWN)) {
                        xml = item.retrieve(objectId);
                        xml = xml.replaceAll("Meier", "Meier1");
                        item.update(objectId, xml);
                    } else if (!status.equals(STATUS_RELEASED)) {
                        xml = item.retrieve(objectId);
                        lastModDate = getLastModificationDate(xml);
                        item.withdraw(objectId,
                                "<param last-modification-date=\""
                                        + lastModDate
                                        + "\"><withdraw-comment>"
                                        + "This is a withdraw comment."
                                        + "</withdraw-comment></param>");
                    }
                }
            }
            if (containerIds != null) {
                for (int i = 0; i < containerIds.length; i++) {
                    xml = container.retrieve(containerIds[i]);
                    lastModDate = getLastModificationDate(xml);
                    String taskParam =
                            "<param last-modification-date=\"" + lastModDate
                                    + "\">"
                                    + "<id>" + objectId + "</id></param>";

                    container.addMembers(containerIds[i], taskParam);
                }
            }
            return returnHash;
        } finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
    }

    /**
     * prepare container for tests.
     * 
     * @param creatorHandle
     *            handle of creator
     * @param contextId
     *            id of context to create container in
     * @param containerId
     *            id of container if already exists
     * @param parentContainerId
     *            id of container to create container in
     * @param templateName
     *            template for container to create
     * @param status
     *            status of container to create
     * @return String id of created container
     * @throws Exception
     *             If anything fails.
     */
    private String prepareContainer(
                final String creatorHandle,
                final String contextId,
                final String containerId,
                final String parentContainerId,
                final String templateName,
                final String status) throws Exception {
        try {
            PWCallback.setHandle(creatorHandle);
            String xml = null;
            String lastModDate = null;
            String objectId = null;
            String containerStatus = "init";
            if (containerId != null) {
                xml = container.retrieve(containerId);
                lastModDate = getLastModificationDate(xml);
                objectId = containerId;
                Node n = selectSingleNode(getDocument(xml),
                        "/container/properties/version/status");
                containerStatus = n.getTextContent();
            } else {
                Document xmlData =
                        EscidocRestSoapTestsBase.getTemplateAsDocument(
                                TEMPLATE_CONTAINER_SEARCH_PATH, templateName);
                if (getTransport() == de.escidoc.core.test.common.client.servlet.Constants.TRANSPORT_REST) {
                    String contextHref = de.escidoc.core.test.common
                            .client.servlet.Constants.CONTEXT_BASE_URI + "/"
                            + contextId;
                    substitute(xmlData,
                            "/container/properties/context/@href", contextHref);
                } else {
                    substitute(xmlData,
                            "/container/properties/context/@objid", contextId);
                }
                xml = container.create(toString(xmlData, false));
                objectId = getId(xml);
                xml = xml.replaceAll("Hoppe", "Hoppe1");
                xml = container.update(objectId, xml);
                lastModDate = getLastModificationDate(xml);
                containerStatus = STATUS_PENDING;
            }

            if (!status.equals(STATUS_PENDING)) {
                // submit container
                if (containerStatus.equals(STATUS_PENDING)) {
                    container.submit(objectId,
                            "<param last-modification-date=\"" + lastModDate
                                    + "\" />");
                    
                    xml = container.retrieve(objectId);
                    xml = xml.replaceAll("Hoppe", "Hoppe1");
                    xml = container.update(objectId, xml);
                    containerStatus = STATUS_SUBMITTED;
                }
                if (!status.equals(STATUS_SUBMITTED)) {
                    // assign pids
                    if (containerStatus.equals(STATUS_SUBMITTED)) {
                        xml = container.retrieve(objectId);
                        Node n = selectSingleNode(getDocument(xml),
                        "/container/properties/version/number");
                        String versionNumber = n.getTextContent();
                        String pidParam = getContainerPidParam(objectId);
                        container.assignObjectPid(objectId, pidParam);
                        pidParam = getContainerPidParam(objectId);
                        container.assignVersionPid(
                                objectId + ":" + versionNumber, pidParam);

                        // release container
                        xml = container.retrieve(objectId);
                        lastModDate = getLastModificationDate(xml);
                        container.release(objectId,
                                "<param last-modification-date=\"" + lastModDate
                                        + "\" />");
                        containerStatus = STATUS_RELEASED;
                    }
                    if (!status.equals(STATUS_RELEASED)
                            && !status.equals(STATUS_WITHDRAWN)) {
                        if (containerStatus.equals(STATUS_RELEASED)) {
                            xml = container.retrieve(objectId);
                            xml = xml.replaceAll("Hoppe", "Hoppe1");
                            container.update(objectId, xml);
                        }
                    } else if (!status.equals(STATUS_RELEASED)) {
                        if (containerStatus.equals(STATUS_RELEASED)) {
                            xml = container.retrieve(objectId);
                            lastModDate = getLastModificationDate(xml);
                            container.withdraw(objectId,
                                    "<param last-modification-date=\""
                                    + lastModDate
                                    + "\"><withdraw-comment>"
                                    + "This is a withdraw comment."
                                    + "</withdraw-comment></param>");
                        }
                    }
                }

            }
            if (parentContainerId != null) {
                xml = container.retrieve(parentContainerId);
                lastModDate = getLastModificationDate(xml);
                String taskParam =
                        "<param last-modification-date=\"" + lastModDate
                                + "\">"
                                + "<id>" + objectId + "</id></param>";

                container.addMembers(parentContainerId, taskParam);
            }
            return objectId;
        } finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
    }
    
    /**
     * prepare content-relation for tests.
     * 
     * @param creatorHandle
     *            handle of creator
     * @param templateName
     *            template for item to create
     * @param status
     *            status of item to create
     * @return String id of created context
     * @throws Exception
     *             If anything fails.
     */
    private String prepareContentRelation(
                final String creatorHandle,
                final String templateName,
                final String status) throws Exception {
        try {
            if (creatorHandle != null) {
                PWCallback.setHandle(creatorHandle);
            }
            Document xmlData =
                    EscidocRestSoapTestsBase.getTemplateAsDocument(
                            TEMPLATE_SB_CONTENT_RELATION_PATH, templateName);
            String xml = contentRelation.create(toString(xmlData, false));
            String lastModDate = getLastModificationDate(xml);
            String objectId = getId(xml);

            if (!status.equals(CONTEXT_STATUS_CREATED) 
                    && !status.equals(CONTEXT_STATUS_DELETED)) {
                // open context
                context.open(objectId, "<param last-modification-date=\""
                        + lastModDate + "\" />");

                if (!status.equals(CONTEXT_STATUS_OPENED)) {
                    // close item
                    xml = context.retrieve(objectId);
                    lastModDate = getLastModificationDate(xml);
                    context.close(objectId, "<param last-modification-date=\""
                            + lastModDate + "\" />");
                }
            } else if (status.equals(CONTEXT_STATUS_DELETED)) {
                Thread.sleep(3000);
                contentRelation.delete(objectId);
            }
            return objectId;
        } finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
    }

    private ArrayList<String> getContextXpathList(
            final int i) {
        ArrayList<String> xpaths = new ArrayList<String>();
        if (i >= 0 && i <= 1) {
            //created
            xpaths.add("properties/public-status=created");
        }
        else if (i ==2) {
            //opened
            xpaths.add("properties/public-status=opened");
        }
        else if (i == 3) {
            //closed
            xpaths.add("properties/public-status=closed");
        }
        return xpaths;
    }


}
