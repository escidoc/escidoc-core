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
 * Test the implementation of the admin search for items and containers.
 *
 * @author Michael Hoppe
 */
public class OrgUnitAdminSearchIT extends SearchTestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrgUnitAdminSearchIT.class);

    private static final String INDEX_NAME = "ou_admin";

    private static final ArrayList<String> RESULT_XPATHS = new ArrayList<String>() {
        private static final long serialVersionUID = 1L;

        {
            add(XPATH_SRW_RESPONSE_OBJECT + "organizational-unit");
        }
    };

    private static String[] ouIds = null;

    private static int methodCounter = 0;

    private static String startTime = "";

    /**
     * @throws Exception e
     */
    public OrgUnitAdminSearchIT() throws Exception {
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
        // Create Org Units/////////////////////////////////////////////////////
        // Build Org-Unit-Hierarchy with one parent,
        // two members with each again having two members.
        String handle = PWCallback.SYSTEMADMINISTRATOR_HANDLE;
        ouIds = new String[14];
        // FOR DEFAULT-USER TESTS
        ouIds[0] =
            getId(prepareOrgUnit(handle, getTemplateAsDocument(TEMPLATE_SB_ORGANIZATIONAL_UNIT_PATH,
                "escidoc_search_ou0.xml"), ORGANIZATIONAL_UNIT_STATUS_OPENED, null));
        ouIds[1] =
            getId(prepareOrgUnit(handle, getTemplateAsDocument(TEMPLATE_SB_ORGANIZATIONAL_UNIT_PATH,
                "escidoc_search_ou1.xml"), ORGANIZATIONAL_UNIT_STATUS_OPENED, new String[] { ouIds[0] }));
        ouIds[2] =
            getId(prepareOrgUnit(handle, getTemplateAsDocument(TEMPLATE_SB_ORGANIZATIONAL_UNIT_PATH,
                "escidoc_search_ou2.xml"), ORGANIZATIONAL_UNIT_STATUS_OPENED, new String[] { ouIds[0] }));
        ouIds[3] =
            getId(prepareOrgUnit(handle, getTemplateAsDocument(TEMPLATE_SB_ORGANIZATIONAL_UNIT_PATH,
                "escidoc_search_ou3.xml"), ORGANIZATIONAL_UNIT_STATUS_CLOSED, new String[] { ouIds[1] }));
        ouIds[4] =
            getId(prepareOrgUnit(handle, getTemplateAsDocument(TEMPLATE_SB_ORGANIZATIONAL_UNIT_PATH,
                "escidoc_search_ou4.xml"), ORGANIZATIONAL_UNIT_STATUS_DELETED, new String[] { ouIds[1] }));
        ouIds[5] =
            getId(prepareOrgUnit(handle, getTemplateAsDocument(TEMPLATE_SB_ORGANIZATIONAL_UNIT_PATH,
                "escidoc_search_ou5.xml"), ORGANIZATIONAL_UNIT_STATUS_CLOSED, new String[] { ouIds[2] }));
        ouIds[6] =
            getId(prepareOrgUnit(handle, getTemplateAsDocument(TEMPLATE_SB_ORGANIZATIONAL_UNIT_PATH,
                "escidoc_search_ou6.xml"), ORGANIZATIONAL_UNIT_STATUS_DELETED, new String[] { ouIds[2] }));

        //FOR OU-ADMIN TESTS
        ouIds[7] =
            getId(prepareOrgUnit(handle, getTemplateAsDocument(TEMPLATE_SB_ORGANIZATIONAL_UNIT_PATH,
                "escidoc_search_ou0.xml"), ORGANIZATIONAL_UNIT_STATUS_CREATED, null));
        ouIds[8] =
            getId(prepareOrgUnit(handle, getTemplateAsDocument(TEMPLATE_SB_ORGANIZATIONAL_UNIT_PATH,
                "escidoc_search_ou1.xml"), ORGANIZATIONAL_UNIT_STATUS_CREATED, new String[] { ouIds[7] }));
        ouIds[9] =
            getId(prepareOrgUnit(handle, getTemplateAsDocument(TEMPLATE_SB_ORGANIZATIONAL_UNIT_PATH,
                "escidoc_search_ou2.xml"), ORGANIZATIONAL_UNIT_STATUS_CREATED, new String[] { ouIds[7] }));
        ouIds[10] =
            getId(prepareOrgUnit(handle, getTemplateAsDocument(TEMPLATE_SB_ORGANIZATIONAL_UNIT_PATH,
                "escidoc_search_ou3.xml"), ORGANIZATIONAL_UNIT_STATUS_CREATED, new String[] { ouIds[8] }));
        ouIds[11] =
            getId(prepareOrgUnit(handle, getTemplateAsDocument(TEMPLATE_SB_ORGANIZATIONAL_UNIT_PATH,
                "escidoc_search_ou4.xml"), ORGANIZATIONAL_UNIT_STATUS_CREATED, new String[] { ouIds[8] }));
        ouIds[12] =
            getId(prepareOrgUnit(handle, getTemplateAsDocument(TEMPLATE_SB_ORGANIZATIONAL_UNIT_PATH,
                "escidoc_search_ou5.xml"), ORGANIZATIONAL_UNIT_STATUS_CREATED, new String[] { ouIds[9], ouIds[8] }));
        ouIds[13] =
            getId(prepareOrgUnit(handle, getTemplateAsDocument(TEMPLATE_SB_ORGANIZATIONAL_UNIT_PATH,
                "escidoc_search_ou6.xml"), ORGANIZATIONAL_UNIT_STATUS_CREATED, new String[] { ouIds[9] }));

        // /////////////////////////////////////////////////////////////////////

        waitForIndexerToAppear(ouIds[13], INDEX_NAME);
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
        assertEquals(Constants.OU_ADMIN_INDEX_FIELD_COUNT, getIndexFieldCount(response));
        assertEquals(Constants.OU_ADMIN_SORT_FIELD_COUNT, getSortFieldCount(response));
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
                put("expectedHits", "5");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;

                    {
                        for (int i = 0; i < 3; i++) {
                            put(ouIds[i], getOuXpathList(i));
                        }
                        put(ouIds[3], getOuXpathList(3));
                        put(ouIds[5], getOuXpathList(5));
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
                put("expectedHits", "12");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;

                    {
                        for (int i = 0; i < 14; i++) {
                            if (i != 4 && i != 6) {
                                put(ouIds[i], getOuXpathList(i));
                            }
                        }
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
                put("expectedHits", "12");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;

                    {
                        for (int i = 0; i < 14; i++) {
                            if (i != 4 && i != 6) {
                                put(ouIds[i], getOuXpathList(i));
                            }
                        }
                    }
                });
            }
        };
        search(role);
    }

    /**
     * Test searching as OU-Administrator user.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSearchAsOuAdminUser() throws Exception {
        HashMap<String, Object> role = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;

            {
                put("role0", GrantHelper.ROLE_HREF_OU_ADMINISTRATOR);
                put("scope0", de.escidoc.core.test.common.client.servlet.Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/"
                    + ouIds[7]);
                put("handle", PWCallback.TEST_HANDLE1);
                put("user", TEST_USER_ACCOUNT_ID1);
                put("expectedHits", "12");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;

                    {
                        for (int i = 0; i < 3; i++) {
                            put(ouIds[i], getOuXpathList(i));
                        }
                        put(ouIds[3], getOuXpathList(3));
                        put(ouIds[5], getOuXpathList(5));
                        for (int i = 7; i < 14; i++) {
                            put(ouIds[i], getOuXpathList(i));
                        }
                    }
                });
            }
        };
        search(role);
    }

    /**
     * Test searching as OU-Administrator user.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSearchAsOuAdminUser1() throws Exception {
        HashMap<String, Object> role = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;

            {
                put("role0", GrantHelper.ROLE_HREF_OU_ADMINISTRATOR);
                put("scope0", de.escidoc.core.test.common.client.servlet.Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/"
                    + ouIds[8]);
                put("handle", PWCallback.TEST_HANDLE1);
                put("user", TEST_USER_ACCOUNT_ID1);
                put("expectedHits", "9");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;

                    {
                        for (int i = 0; i < 3; i++) {
                            put(ouIds[i], getOuXpathList(i));
                        }
                        put(ouIds[3], getOuXpathList(3));
                        put(ouIds[5], getOuXpathList(5));
                        put(ouIds[8], getOuXpathList(8));
                        put(ouIds[10], getOuXpathList(10));
                        put(ouIds[11], getOuXpathList(11));
                        put(ouIds[12], getOuXpathList(12));
                    }
                });
            }
        };
        search(role);
    }

    /**
     * Test searching as OU-Administrator user.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSearchAsOuAdminUser2() throws Exception {
        HashMap<String, Object> role = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;

            {
                put("role0", GrantHelper.ROLE_HREF_OU_ADMINISTRATOR);
                put("scope0", de.escidoc.core.test.common.client.servlet.Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/"
                    + ouIds[9]);
                put("handle", PWCallback.TEST_HANDLE1);
                put("user", TEST_USER_ACCOUNT_ID1);
                put("expectedHits", "8");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;

                    {
                        for (int i = 0; i < 3; i++) {
                            put(ouIds[i], getOuXpathList(i));
                        }
                        put(ouIds[3], getOuXpathList(3));
                        put(ouIds[5], getOuXpathList(5));
                        put(ouIds[9], getOuXpathList(9));
                        put(ouIds[12], getOuXpathList(12));
                        put(ouIds[13], getOuXpathList(13));
                    }
                });
            }
        };
        search(role);
    }

    /**
     * Test searching as OU-Administrator user.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSearchAsOuAdminUser3() throws Exception {
        HashMap<String, Object> role = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;

            {
                put("role0", GrantHelper.ROLE_HREF_OU_ADMINISTRATOR);
                put("scope0", de.escidoc.core.test.common.client.servlet.Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/"
                    + ouIds[12]);
                put("handle", PWCallback.TEST_HANDLE1);
                put("user", TEST_USER_ACCOUNT_ID1);
                put("expectedHits", "6");
                put("searchresultIds", new HashMap<String, ArrayList<String>>() {
                    private static final long serialVersionUID = 1L;

                    {
                        for (int i = 0; i < 3; i++) {
                            put(ouIds[i], getOuXpathList(i));
                        }
                        put(ouIds[3], getOuXpathList(3));
                        put(ouIds[5], getOuXpathList(5));
                        put(ouIds[12], getOuXpathList(12));
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
            assertEquals(true, checkHighlighting(response));
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

    private ArrayList<String> getOuXpathList(final int i) {
        ArrayList<String> xpaths = new ArrayList<String>();
        if (i >= 7 && i <= 13) {
            //created
            xpaths.add("properties/public-status=created");
        }
        else if (i >= 0 && i <= 2) {
            //opened
            xpaths.add("properties/public-status=opened");
        }
        else if (i == 3 || i == 5) {
            //closed
            xpaths.add("properties/public-status=closed");
        }
        return xpaths;
    }

}
