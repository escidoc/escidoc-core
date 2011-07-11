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

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Test the task oriented method retrieveContexts.
 *
 * @author Michael Schneider
 */
public class RetrieveContextsIT extends ContextTestBase {

    public static final String XPATH_SRW_CONTEXT_LIST_CONTEXT =
        XPATH_SRW_RESPONSE_RECORD + "/recordData/search-result-record/" + NAME_CONTEXT;

    private String path = TEMPLATE_CONTEXT_PATH;

    private static int noOfContexts = -1;

    private static int noOfPubManContexts = -1;

    private static int noOfPubManContextsForDepositorUser = -1;

    private static int noOfPubManContextsForDepositorUserAndRole = -1;

    private static int noOfSwbContexts = -1;

    private static int methodCounter = 0;

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void setUp() throws Exception {
        this.path += "/rest";

        if (noOfContexts == -1) {
            String contexts = retrieveContexts(getFilterRetrieveContexts(null, null, null));
            assertXmlValidSrwResponse(contexts);
            noOfContexts = getNoOfSelections(EscidocAbstractTest.getDocument(contexts), XPATH_SRW_CONTEXT_LIST_CONTEXT);
            noOfPubManContexts =
                getNoOfSelections(EscidocAbstractTest.getDocument(contexts), XPATH_SRW_CONTEXT_LIST_CONTEXT
                    + "/properties[type=\"" + CONTEXT_TYPE_PUB_MAN + "\"]");
            noOfSwbContexts =
                getNoOfSelections(EscidocAbstractTest.getDocument(contexts), XPATH_SRW_CONTEXT_LIST_CONTEXT
                    + "/properties[type=\"" + CONTEXT_TYPE_SWB + "\"]");

            Document context = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create.xml");
            substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
            substitute(context, "/context/properties/type", CONTEXT_TYPE_PUB_MAN);
            create(toString(context, false));
            noOfContexts = noOfContexts + 1;
            noOfPubManContexts = noOfPubManContexts + 1;

            substitute(context, "/context/properties/type", CONTEXT_TYPE_SWB);
            substitute(context, "/context/properties/name", getUniqueName("Swb Context "));
            create(toString(context, false));
            noOfContexts = noOfContexts + 1;
            noOfSwbContexts = noOfSwbContexts + 1;

            // get the number of PubMan contexts which are visible for depositor
            // user/role
            PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
            contexts = retrieveContexts(getFilterRetrieveContexts(null, null, null));
            assertXmlValidSrwResponse(contexts);
            noOfPubManContextsForDepositorUser =
                getNoOfSelections(EscidocAbstractTest.getDocument(contexts), XPATH_SRW_CONTEXT_LIST_CONTEXT
                    + "/properties[type=\"" + CONTEXT_TYPE_PUB_MAN + "\"]");
            contexts =
                retrieveContexts(getFilterRetrieveContexts(PWCallback.ID_PREFIX + PWCallback.DEPOSITOR_HANDLE,
                    "escidoc:role-depositor", null));
            assertXmlValidSrwResponse(contexts);
            noOfPubManContextsForDepositorUserAndRole =
                getNoOfSelections(EscidocAbstractTest.getDocument(contexts), XPATH_SRW_CONTEXT_LIST_CONTEXT
                    + "/properties[type=\"" + CONTEXT_TYPE_PUB_MAN + "\"]");
            PWCallback.resetHandle();
        }
    }

    /**
     * Clean up after servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Override
    @After
    public void tearDown() throws Exception {
        methodCounter++;
        if (methodCounter == getTestAnnotationsCount()) {
            methodCounter = 0;
            noOfContexts = -1;
            noOfPubManContexts = -1;
            noOfPubManContextsForDepositorUser = -1;
            noOfPubManContextsForDepositorUserAndRole = -1;
            noOfSwbContexts = -1;
        }
        super.tearDown();
    }

    /**
     * Test retrieve all Contexts.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveAllContexts() throws Exception {
        retrieveContexts(null, null, null, noOfContexts);
    }

    /**
     * Test retrieving PubMan Contexts.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrievePubManContexts() throws Exception {
        retrieveContexts(null, null, CONTEXT_TYPE_PUB_MAN, noOfPubManContexts);
    }

    /**
     * Tests successfully retrieving of contexts of PubMan contexts for a Depositor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrievePubManContextsWithDepositor() throws Exception {
        retrieveContexts(PWCallback.ID_PREFIX + PWCallback.DEPOSITOR_HANDLE, "escidoc:role-depositor",
            CONTEXT_TYPE_PUB_MAN, noOfPubManContextsForDepositorUserAndRole);
    }

    /**
     * Tests successfully retrieving of contexts of PubMan contexts for all Roles.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrievePubManContextsWithAllRoles() throws Exception {
        retrieveContexts(PWCallback.ID_PREFIX + PWCallback.DEPOSITOR_HANDLE, null, CONTEXT_TYPE_PUB_MAN,
            noOfPubManContextsForDepositorUser);
    }

    /**
     * Tests successfully retrieving empty list of PubMan contexts for a unknown role.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrievePubManContextsWithUnknownRole() throws Exception {
        retrieveContexts(PWCallback.ID_PREFIX + PWCallback.DEPOSITOR_HANDLE, UNKNOWN_ID, CONTEXT_TYPE_PUB_MAN, 0);
    }

    /**
     * Tests successfully retrieving empty list of PubMan contexts for a unknown user.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrievePubManContextsWithUnknownUser() throws Exception {
        retrieveContexts(UNKNOWN_ID, "Depositor", CONTEXT_TYPE_PUB_MAN, 0);
    }

    /**
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveSWBContexts() throws Exception {
        retrieveContexts(null, null, CONTEXT_TYPE_SWB, noOfSwbContexts);
    }

    /**
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveUnknownTypeContexts() throws Exception {
        retrieveContexts(null, null, "unknown", 0);
    }

    /**
     * Test successful retrieving of all admin-descriptors of a Context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveAdminDescriptors() throws Exception {

        Document context = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));

        String template = toString(context, false);
        Document newContext = getDocument(create(template));

        String id = getObjidValue(newContext);

        NodeList admDescs = selectNodeList(context, "/context/admin-descriptors/admin-descriptor");

        String temp = retrieveAdminDescriptors(id);
        Document docAdmDesc = getDocument(temp);

        NodeList admDescsNLst = selectNodeList(docAdmDesc, "/admin-descriptors/admin-descriptor");

        assertEquals(admDescs.getLength(), admDescsNLst.getLength());

        for (int i = 0; i < admDescs.getLength(); i++) {
            admDescs.item(i);
            String admDescName =
                selectSingleNode(context,
                    "context/admin-descriptors/admin-descriptor[position()=" + (i + 1) + "]/@name").getTextContent();

            Node contextAdmDesc =
                selectSingleNode(context, "context/admin-descriptors/admin-descriptor[@name='" + admDescName + "']");

            Node newContextAdmDesc =
                selectSingleNode(newContext, "context/admin-descriptors/admin-descriptor[@name='" + admDescName + "']");

            Node admDesc =
                selectSingleNode(docAdmDesc, "admin-descriptors/admin-descriptor[@name='" + admDescName + "']");

            assertXmlEquals("Admin Descriptors not equal. ", contextAdmDesc, newContextAdmDesc);

            assertXmlEquals("Admin Descriptors not equal. ", contextAdmDesc, admDesc);
        }
    }

    /**
     * Test successful retrieving of an empty admin-descriptors element. An empty admin-descriptors section has to be in
     * the XML of the Context (consitent behavior like filters).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveEmptyAdminDescriptors() throws Exception {

        Document context =
            EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create_without_admindescriptor.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));

        // create Context
        String template = toString(context, false);
        assertXmlValidContext(template);
        Document newContext = getDocument(create(template));

        String id = getObjidValue(newContext);

        NodeList admDescs = selectNodeList(context, "/context/admin-descriptors/admin-descriptor");

        String temp = retrieveAdminDescriptors(id);
        Document docAdmDesc = getDocument(temp);

        NodeList admDescsNLst = selectNodeList(docAdmDesc, "/admin-descriptors/admin-descriptor");

        assertEquals(admDescs.getLength(), admDescsNLst.getLength());
        assertEquals(0, admDescs.getLength());
    }

    /**
     * Test retrieve Context.
     *
     * @param user                 The user id (e.g. PWCallback.ID_PREFIX + PWCallback.DEPOSITOR_HANDLE) or null.
     * @param role                 Role (e.g. "Depositor")
     * @param contextType          Type of Context (e.g. "PubMan")
     * @param expectedNoOfContexts Expected number of Contexts (or -1).
     * @throws Exception If anything fails.
     */
    private void retrieveContexts(
        final String user, final String role, final String contextType, final int expectedNoOfContexts)
        throws Exception {
        String contexts = retrieveContexts(getFilterRetrieveContexts(user, role, contextType));

        assertXmlValidSrwResponse(contexts);

        int no = getNoOfSelections(EscidocAbstractTest.getDocument(contexts), XPATH_SRW_CONTEXT_LIST_CONTEXT);
        String label = contextType;

        if (contextType == null) {
            label = "";
        }
        assertEquals("Wrong no of " + label + " Contexts found!", expectedNoOfContexts, no);
    }

    /**
     * Test successfully retrieving an explain response.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testExplainRetrieveContexts() throws Exception {
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_EXPLAIN, new String[] { "" });

        String result = null;

        try {
            result = retrieveContexts(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
    }
}
