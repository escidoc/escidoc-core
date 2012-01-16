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
 * Test the implementation of the admin search for content-models.
 * 
 * @author Michael Hoppe
 */
public class ContentModelAdminSearchIT extends SearchTestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentModelAdminSearchIT.class);

    private static final String INDEX_NAME = "content_model_admin";

    private static final ArrayList<String> RESULT_XPATHS = new ArrayList<String>() {
        private static final long serialVersionUID = 1L;

        {
            add(XPATH_SRW_RESPONSE_OBJECT + "content-model");
        }
    };

    private static String[] contentModelIds = null;

    private static int methodCounter = 0;

    private static String startTime = "";

    public ContentModelAdminSearchIT() throws Exception {
        contentModel = new ContentModelHelper();
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
            // int c = 11001;
            // contentModelIds = new String[4];
            // for (int i = 0; i < 4; i++) {
            // contentModelIds[i] = "escidoc:" + c;
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
     * insert item(s) into system for the tests.
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
        // Create Content Models/////////////////////////////////////////////////
        String handle = PWCallback.SYSTEMADMINISTRATOR_HANDLE;
        contentModelIds = new String[3];
        contentModelIds[0] =
            prepareContentModel(handle, "escidoc_search_content_model0_rest.xml", CONTENT_MODEL_STATUS_CREATED);
        contentModelIds[1] =
            prepareContentModel(handle, "escidoc_search_content_model0_rest.xml", CONTENT_MODEL_STATUS_UPDATED);
        contentModelIds[2] =
            prepareContentModel(handle, "escidoc_search_content_model0_rest.xml", CONTENT_MODEL_STATUS_DELETED);

        // /////////////////////////////////////////////////////////////////////

        waitForIndexerToAppear(contentModelIds[1], INDEX_NAME);
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
        assertEquals(Constants.CONTENT_MODEL_ADMIN_INDEX_FIELD_COUNT, getIndexFieldCount(response));
        assertEquals(Constants.CONTENT_MODEL_ADMIN_SORT_FIELD_COUNT, getSortFieldCount(response));
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
                put("expectedHits", "2");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;

                    {
                        put(contentModelIds[0], getContentModelXpathList(0));
                        put(contentModelIds[1], getContentModelXpathList(1));
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
                put("expectedHits", "2");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;

                    {
                        put(contentModelIds[0], getContentModelXpathList(0));
                        put(contentModelIds[1], getContentModelXpathList(1));
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
                put("expectedHits", "2");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;

                    {
                        put(contentModelIds[0], getContentModelXpathList(0));
                        put(contentModelIds[1], getContentModelXpathList(1));
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
     * prepare context for tests.
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
    private String prepareContentModel(final String creatorHandle, final String templateName, final String status)
        throws Exception {
        try {
            if (creatorHandle != null) {
                PWCallback.setHandle(creatorHandle);
            }
            Document xmlData = EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_SB_CONTENT_MODEL_PATH, templateName);
            String xml = contentModel.create(toString(xmlData, false));
            String objectId = getId(xml);

            if (!status.equals(CONTENT_MODEL_STATUS_CREATED) && !status.equals(CONTENT_MODEL_STATUS_DELETED)) {
                // update content model
                contentModel.update(objectId, xml.replaceAll("create CM", "create CM1"));

            }
            else if (status.equals(CONTENT_MODEL_STATUS_DELETED)) {
                contentModel.delete(objectId);
            }
            return objectId;
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
    }

    private ArrayList<String> getContentModelXpathList(final int i) {
        ArrayList<String> xpaths = new ArrayList<String>();
        if (i == 0) {
            // created
            xpaths.add("properties/version/status=pending");
            xpaths.add("properties/latest-version/number=1");
        }
        else if (i == 1) {
            // updated
            xpaths.add("properties/version/status=pending");
            xpaths.add("properties/latest-version/number=2");
        }
        return xpaths;
    }

}
