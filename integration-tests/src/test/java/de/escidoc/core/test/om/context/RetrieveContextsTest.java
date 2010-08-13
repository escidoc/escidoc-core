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

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.security.client.PWCallback;

/**
 * Test the task oriented method retrieveContexts.
 * 
 * @author MSC
 * 
 */
public class RetrieveContextsTest extends ContextTestBase {

    public static final String XPATH_SRW_CONTEXT_LIST_CONTEXT =
        XPATH_SRW_RESPONSE_RECORD + "/recordData/" + NAME_CONTEXT;

    private String path = TEMPLATE_CONTEXT_PATH;

    private static int noOfContexts = -1;

    private static int noOfPubManContexts = -1;

    private static int noOfPubManContextsForDepositorUser = -1;

    private static int noOfPubManContextsForDepositorUserAndRole = -1;

    private static int noOfSwbContexts = -1;

    /**
     * @param transport
     *            The transport identifier.
     */
    public RetrieveContextsTest(final int transport) {
        super(transport);
    }

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Override
    protected void setUp() throws Exception {

        super.setUp();
        this.path += "/" + getTransport(false);

        if (noOfContexts == -1) {
            String contexts =
                retrieveContexts(getFilterRetrieveContexts(null, null, null));
            assertXmlValidContextsList(contexts);
            noOfContexts =
                getNoOfSelections(EscidocRestSoapTestsBase
                    .getDocument(contexts), "/context-list/context");
            noOfPubManContexts =
                getNoOfSelections(EscidocRestSoapTestsBase
                    .getDocument(contexts),
                    "/context-list/context/properties[type=\""
                        + CONTEXT_TYPE_PUB_MAN + "\"]");
            noOfSwbContexts =
                getNoOfSelections(EscidocRestSoapTestsBase
                    .getDocument(contexts),
                    "/context-list/context/properties[type=\""
                        + CONTEXT_TYPE_SWB + "\"]");

            Document context =
                EscidocRestSoapTestsBase.getTemplateAsDocument(this.path,
                    "context_create.xml");
            substitute(context, "/context/properties/name",
                getUniqueName("PubMan Context "));
            substitute(context, "/context/properties/type",
                CONTEXT_TYPE_PUB_MAN);
            create(toString(context, false));
            noOfContexts = noOfContexts + 1;
            noOfPubManContexts = noOfPubManContexts + 1;

            substitute(context, "/context/properties/type", CONTEXT_TYPE_SWB);
            substitute(context, "/context/properties/name",
                getUniqueName("Swb Context "));
            create(toString(context, false));
            noOfContexts = noOfContexts + 1;
            noOfSwbContexts = noOfSwbContexts + 1;

            // get the number of PubMan contexts which are visible for depositor
            // user/role
            PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
            contexts =
                retrieveContexts(getFilterRetrieveContexts(null, null, null));
            assertXmlValidContextsList(contexts);
            noOfPubManContextsForDepositorUser =
                getNoOfSelections(EscidocRestSoapTestsBase
                    .getDocument(contexts),
                    "/context-list/context/properties[type=\""
                        + CONTEXT_TYPE_PUB_MAN + "\"]");
            contexts =
                retrieveContexts(getFilterRetrieveContexts(PWCallback.ID_PREFIX
                    + PWCallback.DEPOSITOR_HANDLE, "escidoc:role-depositor",
                    null));
            assertXmlValidContextsList(contexts);
            noOfPubManContextsForDepositorUserAndRole =
                getNoOfSelections(EscidocRestSoapTestsBase
                    .getDocument(contexts),
                    "/context-list/context/properties[type=\""
                        + CONTEXT_TYPE_PUB_MAN + "\"]");
            PWCallback.resetHandle();
        }
    }

    /**
     * Clean up after servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Override
    protected void tearDown() throws Exception {

        super.tearDown();
    }

    /**
     * Test retrieve all Contexts.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testRetrieveAllContexts() throws Exception {
        retrieveContexts(null, null, null, noOfContexts, false);
        retrieveContexts(null, null, null, noOfContexts, true);
    }

    /**
     * Test retrieving PubMan Contexts.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testRetrievePubManContexts() throws Exception {
        retrieveContexts(null, null, CONTEXT_TYPE_PUB_MAN, noOfPubManContexts,
            false);
        retrieveContexts(null, null, CONTEXT_TYPE_PUB_MAN, noOfPubManContexts,
            true);
    }

    /**
     * Tests successfully retrieving of contexts of PubMan contexts for a
     * Depositor.
     * 
     * @test.name Decline missing parameter in retrieveContexts.
     * @test.id testRetrievePubManContextsWithDepositor
     * @test.inputDescription: Valid XML representation of filter parameter
     *                         containing these filter criteria:
     *                         <ul>
     *                         <li>User id of existing user with the specified
     *                         role (Depositor).</li>
     *                         <li>Role id (Depositor)</li>
     *                         <li>Context type (PubMan)</li>
     *                         </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * @test.issue 203
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testRetrievePubManContextsWithDepositor() throws Exception {
        retrieveContexts(PWCallback.ID_PREFIX + PWCallback.DEPOSITOR_HANDLE,
            "escidoc:role-depositor", CONTEXT_TYPE_PUB_MAN,
            noOfPubManContextsForDepositorUserAndRole, false);
        retrieveContexts(PWCallback.ID_PREFIX + PWCallback.DEPOSITOR_HANDLE,
            "escidoc:role-depositor", CONTEXT_TYPE_PUB_MAN,
            noOfPubManContextsForDepositorUserAndRole, true);
    }

    /**
     * Tests successfully retrieving of contexts of PubMan contexts for all
     * Roles.
     * 
     * @test.name Retrieve Contexts - All Roles
     * @test.id testRetrievePubManContextsWithAllRoles
     * @test.inputDescription: Valid XML representation of filter parameter
     *                         containing these filter criteria:
     *                         <ul>
     *                         <li>User id of existing user with the specified
     *                         role (Depositor).</li>
     *                         <li>No role name</li>
     *                         <li>Context type (PubMan)</li>
     *                         </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testRetrievePubManContextsWithAllRoles() throws Exception {
        retrieveContexts(PWCallback.ID_PREFIX + PWCallback.DEPOSITOR_HANDLE,
            null, CONTEXT_TYPE_PUB_MAN, noOfPubManContextsForDepositorUser,
            false);
        retrieveContexts(PWCallback.ID_PREFIX + PWCallback.DEPOSITOR_HANDLE,
            null, CONTEXT_TYPE_PUB_MAN, noOfPubManContextsForDepositorUser,
            true);
    }

    /**
     * Tests successfully retrieving empty list of PubMan contexts for a unknown
     * role.
     * 
     * @test.name Retrieve Contexts - Unknown Role.
     * @test.id testRetrievePubManContextsWithUnknownRole
     * @test.inputDescription: Valid XML representation of filter parameter
     *                         containing these filter criteria:
     *                         <ul>
     *                         <li>User id of existing user.</li>
     *                         <li>Unknown Role name</li>
     *                         <li>Context type (PubMan)</li>
     *                         </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testRetrievePubManContextsWithUnknownRole() throws Exception {
        retrieveContexts(PWCallback.ID_PREFIX + PWCallback.DEPOSITOR_HANDLE,
            UNKNOWN_ID, CONTEXT_TYPE_PUB_MAN, 0, false);
        retrieveContexts(PWCallback.ID_PREFIX + PWCallback.DEPOSITOR_HANDLE,
            UNKNOWN_ID, CONTEXT_TYPE_PUB_MAN, 0, true);
    }

    /**
     * Tests successfully retrieving empty list of PubMan contexts for a unknown
     * user.
     * 
     * @test.name Retrieve Contexts - Unknown User.
     * @test.id testRetrievePubManContextsWithUnknownUser
     * @test.inputDescription: Valid XML representation of filter parameter
     *                         containing these filter criteria:
     *                         <ul>
     *                         <li>Unknown user id.</li>
     *                         <li>Role name (Depositor)</li>
     *                         <li>Context type (PubMan)</li>
     *                         </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testRetrievePubManContextsWithUnknownUser() throws Exception {
        retrieveContexts(UNKNOWN_ID, "Depositor", CONTEXT_TYPE_PUB_MAN, 0,
            false);
        retrieveContexts(UNKNOWN_ID, "Depositor", CONTEXT_TYPE_PUB_MAN, 0, true);
    }

    /**
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testRetrieveSWBContexts() throws Exception {
        retrieveContexts(null, null, CONTEXT_TYPE_SWB, noOfSwbContexts, false);
        retrieveContexts(null, null, CONTEXT_TYPE_SWB, noOfSwbContexts, true);
    }

    /**
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testRetrieveUnknownTypeContexts() throws Exception {
        retrieveContexts(null, null, "unknown", 0, false);
        retrieveContexts(null, null, "unknown", 0, true);
    }

    /**
     * Tests declining retrieving of contexts without providing a filter
     * parameter.
     * 
     * @test.name Decline missing parameter in retrieveContexts.
     * @test.id testMissingFilterParam
     * @test.input Filter param XML representation
     * @test.inputDescription: No filter parameter is provided.
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * @test.issue 203
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testMissingFilterParam() throws Exception {

        try {
            retrieveContexts((String) null);
            EscidocRestSoapTestsBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test successful retrieving of all admin-descriptors of a Context.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testRetrieveAdminDescriptors() throws Exception {

        Document context =
            EscidocRestSoapTestsBase.getTemplateAsDocument(this.path,
                "context_create.xml");
        substitute(context, "/context/properties/name",
            getUniqueName("PubMan Context "));

        String template = toString(context, false);
        Document newContext = getDocument(create(template));

        String id = getObjidValue(newContext);

        NodeList admDescs =
            selectNodeList(context,
                "/context/admin-descriptors/admin-descriptor");

        String temp = retrieveAdminDescriptors(id);
        Document docAdmDesc = getDocument(temp);

        NodeList admDescsNLst =
            selectNodeList(docAdmDesc, "/admin-descriptors/admin-descriptor");

        assertEquals(admDescs.getLength(), admDescsNLst.getLength());

        for (int i = 0; i < admDescs.getLength(); i++) {
            admDescs.item(i);
            String admDescName =
                selectSingleNode(
                    context,
                    "context/admin-descriptors/admin-descriptor[position()="
                        + (i + 1) + "]/@name").getTextContent();

            Node contextAdmDesc =
                selectSingleNode(context,
                    "context/admin-descriptors/admin-descriptor[@name='"
                        + admDescName + "']");

            Node newContextAdmDesc =
                selectSingleNode(newContext,
                    "context/admin-descriptors/admin-descriptor[@name='"
                        + admDescName + "']");

            Node admDesc =
                selectSingleNode(docAdmDesc,
                    "admin-descriptors/admin-descriptor[@name='" + admDescName
                        + "']");

            assertXmlEquals("Admin Descriptors not equal. ", contextAdmDesc,
                newContextAdmDesc);

            assertXmlEquals("Admin Descriptors not equal. ", contextAdmDesc,
                admDesc);
        }
    }

    /**
     * Test successful retrieving of an empty admin-descriptors element. An
     * empty admin-descriptors section has to be in the XML of the Context
     * (consitent behavior like filters).
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testRetrieveEmptyAdminDescriptors() throws Exception {

        Document context =
            EscidocRestSoapTestsBase.getTemplateAsDocument(this.path,
                "context_create_without_admindescriptor.xml");
        substitute(context, "/context/properties/name",
            getUniqueName("PubMan Context "));

        // create Context
        String template = toString(context, false);
        assertXmlValidContext(template);
        Document newContext = getDocument(create(template));

        String id = getObjidValue(newContext);

        NodeList admDescs =
            selectNodeList(context,
                "/context/admin-descriptors/admin-descriptor");

        String temp = retrieveAdminDescriptors(id);
        Document docAdmDesc = getDocument(temp);

        NodeList admDescsNLst =
            selectNodeList(docAdmDesc, "/admin-descriptors/admin-descriptor");

        assertEquals(admDescs.getLength(), admDescsNLst.getLength());
        assertEquals(0, admDescs.getLength());
    }

    /**
     * Tests declining retrieving of contexts if provided filter parameter is
     * corrupted.
     * 
     * @test.name Decline corrupted filter param in retrieveContexts.
     * @test.id CorruptedFilterParam
     * @test.input Filter param XML representation
     * @test.inputDescription: Corrupted XML data.
     * @test.expected: XmlCorruptedException
     * @test.status Implemented
     * @test.issue 203
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void notestCorruptedFilterParam() throws Exception {
        // TODO re enable when projects are merged
        try {
            retrieveContexts("corrupted");
            EscidocRestSoapTestsBase
                .failMissingException(XmlCorruptedException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                XmlCorruptedException.class, e);
        }
    }

    /**
     * Tests declining retrieving of contexts if provided filter parameter is
     * invalid.
     * 
     * @test.name Decline invalid filter parameter in retrieveContexts
     * @test.id testInvalidFilterParam
     * @test.input Filter param XML representation
     * @test.inputDescription: Xml representation of a filter param that
     *                         contains an invalid criteria ("content-type".
     * @test.expected: InvalidXmlException
     * @test.status Implemented
     * @test.issue 203
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void notestInvalidFilterParam() throws Exception {
        // TODO re enable when projects are merged
        String filterRetrieveContexts =
            getFilterRetrieveContexts(null, null, "PubMan");
        // make parameter invalid by using content-type instead of context
        // type.
        filterRetrieveContexts =
            filterRetrieveContexts.replaceAll("context-type", "content-type");

        try {
            retrieveContexts(filterRetrieveContexts);
            EscidocRestSoapTestsBase
                .failMissingException(InvalidXmlException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                InvalidXmlException.class, e);
        }

    }

    /**
     * Test retrieve Context.
     * 
     * @param user
     *            The user id (e.g. PWCallback.ID_PREFIX +
     *            PWCallback.DEPOSITOR_HANDLE) or null.
     * @param role
     *            Role (e.g. "Depositor")
     * @param contextType
     *            Type of Context (e.g. "PubMan")
     * @param expectedNoOfContexts
     *            Expected number of Contexts (or -1).
     * @param srw
     *            true if the context list should be requested as SRW response
     * 
     * @throws Exception
     *             If anything fails.
     */
    private void retrieveContexts(
        final String user, final String role, final String contextType,
        final int expectedNoOfContexts, final boolean srw) throws Exception {
        String contexts = null;

        if (srw) {
            final Map<String, String[]> filterParams =
                new HashMap<String, String[]>();
            final StringBuffer filter = new StringBuffer();

            if ((contextType != null) && (contextType.length() > 0)) {
                filter
                    .append("\"" + FILTER_TYPE + "\"=\"" + contextType + "\"");
            }
            if ((user != null) && (user.length() > 0)) {
                if (filter.length() > 0) {
                    filter.append(" and ");
                }
                filter.append("user=\"" + user + "\"");
            }
            if ((role != null) && (role.length() > 0)) {
                if (filter.length() > 0) {
                    filter.append(" and ");
                }
                filter.append("role=\"" + role + "\"");
            }
            if (filter.length() > 0) {
                filterParams.put(FILTER_PARAMETER_QUERY, new String[] { filter
                    .toString() });
            }
            contexts = retrieveContexts(filterParams);
            assertXmlValidSrwResponse(contexts);
        }
        else {
            String filterRetrieveContexts =
                getFilterRetrieveContexts(user, role, contextType);
            contexts = retrieveContexts(filterRetrieveContexts);
            assertXmlValidContextsList(contexts);
        }

        int no = -1;

        if (srw) {
            no =
                getNoOfSelections(EscidocRestSoapTestsBase
                    .getDocument(contexts), XPATH_SRW_CONTEXT_LIST_CONTEXT);
        }
        else {
            no =
                getNoOfSelections(EscidocRestSoapTestsBase
                    .getDocument(contexts), "/context-list/context");
        }
        String label = contextType;
        if (contextType == null) {
            label = "";
        }
        assertEquals("Wrong no of " + label + " Contexts found!",
            expectedNoOfContexts, no);
    }

    /**
     * Test successfully retrieving an explain response.
     * 
     * @test.name testExplainRetrieveContexts
     * @test.id testExplainRetrieveContexts
     * @test.input
     * @test.expected: valid explain response.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testExplainRetrieveContexts() throws Exception {
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_EXPLAIN, new String[] { "" });

        String result = null;

        try {
            result = retrieveContexts(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
    }
}
