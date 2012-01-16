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
 * Test the implementation of the admin search for contexts.
 *
 * @author Michael Hoppe
 */
public class ContextAdminSearchIT extends SearchTestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextAdminSearchIT.class);

    private static final String INDEX_NAME = "context_admin";

    private static final ArrayList<String> RESULT_XPATHS = new ArrayList<String>() {
        private static final long serialVersionUID = 1L;

        {
            add(XPATH_SRW_RESPONSE_OBJECT + "context");
        }
    };

    private static String[] contextIds = null;

    private static int methodCounter = 0;

    private static String startTime = "";

    /**
     * @throws Exception e
     */
    public ContextAdminSearchIT() throws Exception {
        context = new ContextHelper();
        grant = new GrantHelper(GrantHelper.getUserAccountHandlerCode());
    }

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void initialize() throws Exception {
        if (methodCounter == 0) {
            prepare();
            //          int c = 8357;
            //          contextIds = new String[4];
            //          for (int i = 0; i < 4; i++) {
            //              contextIds[i] = "escidoc:" + c;
            //              c++;
            //          }
        }
    }

    /**
     * Clean up after servlet test.
     *
     * @throws Exception If anything fails.
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
     * @throws Exception If anything fails.
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
        // Create Contexts with different status/////////////////////////////////////////////////
        String handle = PWCallback.CONTEXT_ADMINISTRATOR_HANDLE;
        contextIds = new String[5];
        contextIds[0] =
            prepareContext(PWCallback.SYSTEMADMINISTRATOR_HANDLE, "escidoc_search_context0_rest.xml",
                CONTEXT_STATUS_CREATED);
        contextIds[1] = prepareContext(handle, "escidoc_search_context0_rest.xml", CONTEXT_STATUS_CREATED);
        contextIds[2] = prepareContext(handle, "escidoc_search_context0_rest.xml", CONTEXT_STATUS_OPENED);
        contextIds[3] = prepareContext(handle, "escidoc_search_context0_rest.xml", CONTEXT_STATUS_CLOSED);
        contextIds[4] = prepareContext(handle, "escidoc_search_context0_rest.xml", CONTEXT_STATUS_DELETED);

        // /////////////////////////////////////////////////////////////////////

        waitForIndexerToAppear(contextIds[3], INDEX_NAME);
    }

    /**
     * explain operation without parameters for existing database xyz.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSBEX1() throws Exception {
        HashMap<String, String[]> parameters = new HashMap<String, String[]>();
        String response = explain(parameters, INDEX_NAME);
        assertXmlValidExplainPlan(response);
        assertEquals("srw/search/" + INDEX_NAME, getDatabase(response));
        assertEquals(Constants.CONTEXT_ADMIN_INDEX_FIELD_COUNT, getIndexFieldCount(response));
        assertEquals(Constants.CONTEXT_ADMIN_SORT_FIELD_COUNT, getSortFieldCount(response));
    }

    /**
     * Test searching as anonymous user.
     *
     * @throws Exception If anything fails.
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
                        put(contextIds[2], getContextXpathList(2));
                        put(contextIds[3], getContextXpathList(3));
                    }
                });
            }
        };
        search(role);
    }

    /**
     * Test searching as Systemadministrator user.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSearchAsSystemadministratorUser() throws Exception {
        HashMap<String, Object> role = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;

            {
                put("role0", GrantHelper.ROLE_HREF_SYSTEM_ADMINISTRATOR);
                put("handle", PWCallback.TEST_HANDLE1);
                put("user", TEST_USER_ACCOUNT_ID1);
                put("expectedHits", "4");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;

                    {
                        put(contextIds[0], getContextXpathList(0));
                        put(contextIds[1], getContextXpathList(1));
                        put(contextIds[2], getContextXpathList(2));
                        put(contextIds[3], getContextXpathList(3));
                    }
                });
            }
        };
        search(role);
    }

    /**
     * Test searching as Systeminspector user.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSearchAsSysteminspectorUser() throws Exception {
        HashMap<String, Object> role = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;

            {
                put("role0", GrantHelper.ROLE_HREF_SYSTEM_INSPECTOR);
                put("handle", PWCallback.TEST_HANDLE1);
                put("user", TEST_USER_ACCOUNT_ID1);
                put("expectedHits", "4");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;

                    {
                        put(contextIds[0], getContextXpathList(0));
                        put(contextIds[1], getContextXpathList(1));
                        put(contextIds[2], getContextXpathList(2));
                        put(contextIds[3], getContextXpathList(3));
                    }
                });
            }
        };
        search(role);
    }

    /**
     * Test searching as Administrator user.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSearchAsAdminUser() throws Exception {
        HashMap<String, Object> role = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;

            {
                put("role0", GrantHelper.ROLE_HREF_ADMINISTRATOR);
                put("scope0", de.escidoc.core.test.common.client.servlet.Constants.CONTEXT_BASE_URI + "/"
                    + contextIds[0]);
                put("handle", PWCallback.TEST_HANDLE1);
                put("user", TEST_USER_ACCOUNT_ID1);
                put("expectedHits", "3");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;

                    {
                        put(contextIds[0], getContextXpathList(0));
                        put(contextIds[2], getContextXpathList(2));
                        put(contextIds[3], getContextXpathList(3));
                    }
                });
            }
        };
        search(role);
    }

    /**
     * Test searching as Context-Administrator user.
     *
     * @throws Exception If anything fails.
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
                        put(contextIds[1], getContextXpathList(1));
                        put(contextIds[2], getContextXpathList(2));
                        put(contextIds[3], getContextXpathList(3));
                    }
                });
            }
        };
        search(role);
    }

    /**
     * Test searching as Context-Modifier user.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSearchAsContextModifierUser() throws Exception {
        HashMap<String, Object> role = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;

            {
                put("role0", GrantHelper.ROLE_HREF_CONTEXT_MODIFIER);
                put("scope0", de.escidoc.core.test.common.client.servlet.Constants.CONTEXT_BASE_URI + "/"
                    + contextIds[1]);
                put("handle", PWCallback.TEST_HANDLE1);
                put("user", TEST_USER_ACCOUNT_ID1);
                put("expectedHits", "3");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;

                    {
                        put(contextIds[1], getContextXpathList(1));
                        put(contextIds[2], getContextXpathList(2));
                        put(contextIds[3], getContextXpathList(3));
                    }
                });
            }
        };
        search(role);
    }

    /**
     * search with roles provided in HashMap.
     *
     * @param role parameters
     * @throws Exception If anything fails.
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
            //            assertEquals(true, checkHighlighting(response));
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
     * @param creatorHandle handle of creator
     * @param templateName  template for item to create
     * @param status        status of item to create
     * @return String id of created context
     * @throws Exception If anything fails.
     */
    private String prepareContext(final String creatorHandle, final String templateName, final String status)
        throws Exception {
        try {
            if (creatorHandle != null) {
                PWCallback.setHandle(creatorHandle);
            }
            Document xmlData = EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_SB_CONTEXT_PATH, templateName);
            substitute(xmlData, "/context/properties/name", getUniqueName("PubMan Context "));
            String xml = context.create(toString(xmlData, false));
            String lastModDate = getLastModificationDate(xml);
            String objectId = getId(xml);

            if (!status.equals(CONTEXT_STATUS_CREATED) && !status.equals(CONTEXT_STATUS_DELETED)) {
                // open context
                context.open(objectId, "<param last-modification-date=\"" + lastModDate + "\" />");

                if (!status.equals(CONTEXT_STATUS_OPENED)) {
                    // close item
                    xml = context.retrieve(objectId);
                    lastModDate = getLastModificationDate(xml);
                    context.close(objectId, "<param last-modification-date=\"" + lastModDate + "\" />");
                }
            }
            else if (status.equals(CONTEXT_STATUS_DELETED)) {
                context.delete(objectId);
            }
            return objectId;
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
    }

    private ArrayList<String> getContextXpathList(final int i) {
        ArrayList<String> xpaths = new ArrayList<String>();
        if (i >= 0 && i <= 1) {
            //created
            xpaths.add("properties/public-status=created");
        }
        else if (i == 2) {
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
