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

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.HttpHelper;
import de.escidoc.core.test.security.client.PWCallback;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test the implementation of the admin search for content-relations.
 * 
 * @author Michael Hoppe
 */
public class ContentRelationAdminSearchIT extends SearchTestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentRelationAdminSearchIT.class);

    private static final String INDEX_NAME = "content_relation_admin";

    private static final ArrayList<String> RESULT_XPATHS = new ArrayList<String>() {
        private static final long serialVersionUID = 1L;

        {
            add(XPATH_SRW_RESPONSE_OBJECT + "content-relation");
        }
    };

    private static String[] contentRelationIds = null;

    private static String[] itemIds = null;

    private static int methodCounter = 0;

    private static String startTime = "";

    /**
     * @throws Exception
     *             e
     */
    public ContentRelationAdminSearchIT() throws Exception {
        contentRelation = new ContentRelationHelper();
        item = new ItemHelper();
        grant = new GrantHelper(GrantHelper.getUserAccountHandlerCode());
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
            // int c = 8324;
            // itemIds = new String[10];
            // for (int i = 0; i < 10; i++) {
            // itemIds[i] = "escidoc:" + c;
            // c += 3;
            // }
            // c -= 2;
            //
            // contentRelationIds = new String[5];
            // for (int i = 0; i < 5; i++) {
            // contentRelationIds[i] = "escidoc:" + c;
            // c++;
            // }
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
        if (methodCounter == getTestAnnotationsCount()) {
            methodCounter = 0;
        }
    }

    /**
     * insert content-relation(s) into system for the tests.
     * 
     * @throws Exception
     *             If anything fails.
     */
    private void prepare() throws Exception {
        LOGGER.info("starting SearchTest at "
            + new DateTime(System.currentTimeMillis() + (60 * 60 * 1000), DateTimeZone.UTC).toString());
        // create empty indices/////////////////////////////////////////////////
        String urlParameters =
            "?operation=updateIndex" + "&action=createEmpty" + "&repositoryName=escidocrepository" + "&indexName=";
        String httpUrl =
            getFrameworkUrl() + de.escidoc.core.test.common.client.servlet.Constants.FEDORAGSEARCH_BASE_URI
                + urlParameters;
        HttpHelper.executeHttpRequest(de.escidoc.core.test.common.client.servlet.Constants.HTTP_METHOD_GET, httpUrl,
            null, null, null);
        // /////////////////////////////////////////////////////////////////////

        startTime = new DateTime(System.currentTimeMillis(), DateTimeZone.UTC).toString();
        // Create Items to relate
        itemIds = new String[10];
        for (int k = 0; k < 10; k++) {
            String status = STATUS_PENDING;
            HashMap<String, String> itemHash =
                prepareItem(PWCallback.SYSTEMADMINISTRATOR_HANDLE, CONTEXT_ID, null, "escidoc_search_item" + k
                    + "_rest.xml", status);
            itemIds[k] = itemHash.get("itemId");
        }

        // Create Content-Relations with different status/////////////////////////////////////////////////
        String handle = PWCallback.CONTENT_RELATION_MANAGER_HANDLE;
        contentRelationIds = new String[5];
        contentRelationIds[0] =
            prepareContentRelation(PWCallback.SYSTEMADMINISTRATOR_HANDLE, itemIds[0], itemIds[1],
                "escidoc_search_content_relation0_rest.xml", STATUS_PENDING);
        contentRelationIds[1] =
            prepareContentRelation(PWCallback.SYSTEMADMINISTRATOR_HANDLE, itemIds[2], itemIds[3],
                "escidoc_search_content_relation0_rest.xml", STATUS_SUBMITTED);
        contentRelationIds[2] =
            prepareContentRelation(PWCallback.SYSTEMADMINISTRATOR_HANDLE, itemIds[4], itemIds[5],
                "escidoc_search_content_relation0_rest.xml", STATUS_RELEASED);
        contentRelationIds[3] =
            prepareContentRelation(PWCallback.SYSTEMADMINISTRATOR_HANDLE, itemIds[6], itemIds[7],
                "escidoc_search_content_relation0_rest.xml", STATUS_IN_REVISION);
        contentRelationIds[4] =
            prepareContentRelation(handle, itemIds[8], itemIds[9], "escidoc_search_content_relation0_rest.xml",
                STATUS_PENDING);

        // /////////////////////////////////////////////////////////////////////

        waitForIndexerToAppear(contentRelationIds[4], INDEX_NAME);
        Thread.sleep(60000);
    }

    /**
     * explain operation without parameters for existing database xyz.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBEX1() throws Exception {
        HashMap<String, String[]> parameters = new HashMap<String, String[]>();
        String response = explain(parameters, INDEX_NAME);
        assertXmlValidExplainPlan(response);
        assertEquals("srw/search/" + INDEX_NAME, getDatabase(response));
        assertEquals(Constants.CONTENT_RELATION_ADMIN_INDEX_FIELD_COUNT, getIndexFieldCount(response));
        assertEquals(Constants.CONTENT_RELATION_ADMIN_SORT_FIELD_COUNT, getSortFieldCount(response));
    }

    /**
     * Test searching as anonymous user.
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
                put("expectedHits", "1");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;

                    {
                        put(contentRelationIds[2], getContentRelationXpathList(2, null));
                    }
                });
            }
        };
        search(role);
    }

    /**
     * Test searching as Systemadministrator user.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSearchAsSystemadministratorUser() throws Exception {
        HashMap<String, Object> role = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;

            {
                put("role0", GrantHelper.ROLE_HREF_SYSTEM_ADMINISTRATOR);
                put("handle", PWCallback.TEST_HANDLE1);
                put("user", TEST_USER_ACCOUNT_ID1);
                put("expectedHits", "5");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;

                    {
                        put(contentRelationIds[0], getContentRelationXpathList(0, null));
                        put(contentRelationIds[1], getContentRelationXpathList(1, null));
                        put(contentRelationIds[2], getContentRelationXpathList(2, null));
                        put(contentRelationIds[3], getContentRelationXpathList(3, null));
                        put(contentRelationIds[4], getContentRelationXpathList(4, null));
                    }
                });
            }
        };
        search(role);
    }

    /**
     * Test searching as Systeminspector user.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSearchAsSysteminspectorUser() throws Exception {
        HashMap<String, Object> role = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;

            {
                put("role0", GrantHelper.ROLE_HREF_SYSTEM_INSPECTOR);
                put("handle", PWCallback.TEST_HANDLE1);
                put("user", TEST_USER_ACCOUNT_ID1);
                put("expectedHits", "5");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;

                    {
                        put(contentRelationIds[0], getContentRelationXpathList(0, null));
                        put(contentRelationIds[1], getContentRelationXpathList(1, null));
                        put(contentRelationIds[2], getContentRelationXpathList(2, null));
                        put(contentRelationIds[3], getContentRelationXpathList(3, null));
                        put(contentRelationIds[4], getContentRelationXpathList(4, null));
                    }
                });
            }
        };
        search(role);
    }

    /**
     * Test searching as ContentRelationManager user.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSearchAsContentRelationManagerUser() throws Exception {
        HashMap<String, Object> role = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;

            {
                put("handle", PWCallback.CONTENT_RELATION_MANAGER_HANDLE);
                put("expectedHits", "2");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;

                    {
                        put(contentRelationIds[2], getContentRelationXpathList(2, null));
                        put(contentRelationIds[4], getContentRelationXpathList(4, null));
                    }
                });
            }
        };
        search(role);
    }

    /**
     * Test searching as ContentRelationModifier user.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSearchAsContentRelationModifierUser() throws Exception {
        HashMap<String, Object> role = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;

            {
                put("role0", GrantHelper.ROLE_HREF_CONTENT_RELATION_MODIFIER);
                put("scope0", de.escidoc.core.test.common.client.servlet.Constants.CONTENT_RELATION_BASE_URI + "/"
                    + contentRelationIds[0]);
                put("handle", PWCallback.TEST_HANDLE1);
                put("user", TEST_USER_ACCOUNT_ID1);
                put("expectedHits", "2");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;

                    {
                        put(contentRelationIds[0], getContentRelationXpathList(0, null));
                        put(contentRelationIds[2], getContentRelationXpathList(2, null));
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
                errorTrace.append("role: ").append(role.get("role" + i)).append("\n");
                errorTrace.append("scope: ").append(role.get("scope" + i)).append("\n");
                grant.doTestCreateGrant(null, (String) role.get("user"), (String) role.get("scope" + i), (String) role
                    .get("role" + i), null);
            }
            PWCallback.setHandle((String) role.get("handle"));
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put(FILTER_PARAMETER_QUERY, "PID=escidoc*");
            parameters.put(FILTER_PARAMETER_MAXIMUMRECORDS, "150");
            String response = search(parameters, INDEX_NAME);
            assertXmlValidSearchResult(response);
            Document searchResultDoc = getDocument(response, true);
            Node n = selectSingleNode(searchResultDoc, "/searchRetrieveResponse/diagnostics/diagnostic/details");
            String textContent = null;
            if (n != null) {
                textContent = n.getTextContent();
            }
            assertEquals(errorTrace.toString() + "diagnostics: " + textContent, null, n);
            // assertEquals(true, checkHighlighting(response));
            assertEquals(errorTrace.toString() + "hits not as expected: expected: " + role.get("expectedHits")
                + ", but was " + getNumberOfHits(response) + " for ", role.get("expectedHits"),
                getNumberOfHits(response));
            HashSet<String> foundIds = new HashSet<String>();
            // check if all items in result may be there
            for (String xPath : RESULT_XPATHS) {
                NodeList nodes = selectNodeList(searchResultDoc, xPath);
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    String objId = getObjidValue(node, null);
                    foundIds.add(objId);
                    assertTrue(errorTrace.toString() + "object " + objId + " may not be in searchResult",
                        ((HashMap<String, String>) role.get("searchresultIds")).containsKey(objId));
                    ArrayList<String> searchIds =
                        ((HashMap<String, ArrayList<String>>) role.get("searchresultIds")).get(objId);
                    if (searchIds != null) {
                        for (String searchId : searchIds) {
                            String[] parts = searchId.split("=");
                            assertXmlEquals(errorTrace.toString() + "not expected value in " + parts[0]
                                + " for objectId " + objId, node, parts[0], parts[1]);
                        }
                    }
                }
            }
            // check if all objects that should be in result are there
            for (String id : ((HashMap<String, String>) role.get("searchresultIds")).keySet()) {
                assertTrue(errorTrace.toString() + id + " was not in searchResult", foundIds.contains(id));

            }
        }
        finally {
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
     * @param containerIds
     *            ids of container to create item in
     * @param templateName
     *            template for item to create
     * @param status
     *            status of item to create
     * @return HashMap id of created item + componentIds
     * @throws Exception
     *             If anything fails.
     */
    private HashMap<String, String> prepareItem(
        final String creatorHandle, final String contextId, final String[] containerIds, final String templateName,
        final String status) throws Exception {
        HashMap<String, String> returnHash = new HashMap<String, String>();
        try {
            if (creatorHandle != null) {
                PWCallback.setHandle(creatorHandle);
            }
            Document xmlData = EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_SEARCH_ADMIN_PATH, templateName);
            String contextHref =
                de.escidoc.core.test.common.client.servlet.Constants.CONTEXT_BASE_URI + "/" + contextId;
            substitute(xmlData, "/item/properties/context/@href", contextHref);
            String xml = item.create(toString(xmlData, false));
            String objectId = getId(xml);
            xml = xml.replaceAll("Meier", "Meier1");
            xml = item.update(objectId, xml);
            String lastModDate = getLastModificationDate(xml);
            Document itemDoc = EscidocAbstractTest.getDocument(xml);
            returnHash.put("itemId", objectId);
            for (int i = 1;; i++) {
                try {
                    String componentId = getComponentObjidValue(itemDoc, i);
                    if (componentId == null) {
                        break;
                    }
                    returnHash.put("componentId" + i, componentId);
                }
                catch (final NullPointerException e) {
                    break;
                }
            }

            if (!status.equals(STATUS_PENDING)) {
                // submit item
                item.submit(objectId, "<param last-modification-date=\"" + lastModDate + "\" />");
                xml = item.retrieve(objectId);
                xml = xml.replaceAll("Meier", "Meier1");
                xml = item.update(objectId, xml);

                if (!status.equals(STATUS_SUBMITTED)) {
                    if (status.equals(STATUS_IN_REVISION)) {
                        xml = item.retrieve(objectId);
                        lastModDate = getLastModificationDate(xml);
                        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
                        item.revise(objectId, "<param last-modification-date=\"" + lastModDate + "\" />");
                    }
                    else {
                        // assignPids
                        xml = item.retrieve(objectId);
                        String componentId = getComponentObjidValue(itemDoc, 1);
                        String pidParam = getItemPidParam(objectId);
                        item.assignContentPid(objectId, componentId, pidParam);
                        pidParam = getItemPidParam(objectId);
                        item.assignObjectPid(objectId, pidParam);
                        Node n = selectSingleNode(getDocument(xml), "/item/properties/version/number");
                        String versionNumber = n.getTextContent();
                        String versionId = objectId + ":" + versionNumber;
                        pidParam = getItemPidParam(versionId);
                        item.assignVersionPid(versionId, pidParam);
                        // }

                        // release item
                        xml = item.retrieve(objectId);
                        lastModDate = getLastModificationDate(xml);
                        item.release(objectId, "<param last-modification-date=\"" + lastModDate + "\" />");
                    }
                    if (!status.equals(STATUS_RELEASED) && !status.equals(STATUS_WITHDRAWN)
                        && !status.equals(STATUS_IN_REVISION)) {
                        xml = item.retrieve(objectId);
                        xml = xml.replaceAll("Meier", "Meier1");
                        item.update(objectId, xml);
                    }
                    else if (!status.equals(STATUS_RELEASED) && !status.equals(STATUS_IN_REVISION)) {
                        xml = item.retrieve(objectId);
                        lastModDate = getLastModificationDate(xml);
                        item.withdraw(objectId, "<param last-modification-date=\"" + lastModDate
                            + "\"><withdraw-comment>" + "This is a withdraw comment." + "</withdraw-comment></param>");
                    }
                }
            }
            if (containerIds != null) {
                for (int i = 0; i < containerIds.length; i++) {
                    xml = container.retrieve(containerIds[i]);
                    lastModDate = getLastModificationDate(xml);
                    String taskParam =
                        "<param last-modification-date=\"" + lastModDate + "\">" + "<id>" + objectId + "</id></param>";

                    container.addMembers(containerIds[i], taskParam);
                }
            }
            return returnHash;
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
    }

    /**
     * /** prepare content-relation for tests.
     * 
     * @param creatorHandle
     *            handle of creator
     * @param subjectId
     *            id of subject
     * @param objectId
     *            id of object
     * @param templateName
     *            template for content-relation to create
     * @param status
     *            status of content-relation to create
     * @return String id of created content-relation
     * @throws Exception
     *             If anything fails.
     */
    private String prepareContentRelation(
        final String creatorHandle, final String subjectId, final String objectId, final String templateName,
        final String status) throws Exception {
        String resourceId = null;
        try {
            if (creatorHandle != null) {
                PWCallback.setHandle(creatorHandle);
            }
            Document xmlData =
                EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_SB_CONTENT_RELATION_PATH, templateName);

            String subjectHref = de.escidoc.core.test.common.client.servlet.Constants.ITEM_BASE_URI + "/" + subjectId;
            String objectHref = de.escidoc.core.test.common.client.servlet.Constants.ITEM_BASE_URI + "/" + objectId;
            substitute(xmlData, "/content-relation/subject/@href", subjectHref);
            substitute(xmlData, "/content-relation/object/@href", objectHref);
            String xml = contentRelation.create(toString(xmlData, false));
            resourceId = getId(xml);
            xml = xml.replaceAll("Meier", "Meier1");
            xml = contentRelation.update(resourceId, xml);
            String lastModDate = getLastModificationDate(xml);

            if (!status.equals(STATUS_PENDING)) {
                // submit content-relation
                contentRelation.submit(resourceId, "<param last-modification-date=\"" + lastModDate + "\" />");
                xml = contentRelation.retrieve(resourceId);
                xml = xml.replaceAll("Meier", "Meier1");
                xml = contentRelation.update(resourceId, xml);

                if (!status.equals(STATUS_SUBMITTED)) {
                    if (status.equals(STATUS_IN_REVISION)) {
                        xml = contentRelation.retrieve(resourceId);
                        lastModDate = getLastModificationDate(xml);
                        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
                        contentRelation.revise(resourceId, "<param last-modification-date=\"" + lastModDate + "\" />");
                    }
                    else {
                        // release item
                        xml = contentRelation.retrieve(resourceId);
                        lastModDate = getLastModificationDate(xml);
                        contentRelation.release(resourceId, "<param last-modification-date=\"" + lastModDate + "\" />");
                    }
                    if (!status.equals(STATUS_RELEASED) && !status.equals(STATUS_IN_REVISION)) {
                        xml = contentRelation.retrieve(resourceId);
                        xml = xml.replaceAll("Meier", "Meier1");
                        contentRelation.update(resourceId, xml);
                    }
                }
            }
            return resourceId;
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
    }

    private ArrayList<String> getContentRelationXpathList(final int i, final String postreleasedStatus) {
        ArrayList<String> xpaths = new ArrayList<String>();
        if (postreleasedStatus != null) {
            xpaths.add("properties/version/status=" + postreleasedStatus);
        }
        if (i % 6 == 0) {
            // pending
            xpaths.add("properties/public-status=pending");
        }
        else if (i % 6 == 1) {
            // submitted
            xpaths.add("properties/public-status=submitted");
        }
        else if (i % 6 == 2) {
            // released
            xpaths.add("properties/public-status=released");
        }
        else if (i % 6 == 3) {
            // in-revision
            xpaths.add("properties/public-status=in-revision");
        }
        else if (i % 6 == 4) {
            // pending
            xpaths.add("properties/public-status=pending");
        }
        return xpaths;
    }

}
