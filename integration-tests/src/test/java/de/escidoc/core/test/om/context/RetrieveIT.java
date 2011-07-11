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
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.fedora.TripleStoreTestBase;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Test the task oriented method retrieveContexts.
 *
 * @author Michael Schneider
 */
public class RetrieveIT extends ContextTestBase {

    private String path = "";

    private String contextId = null;

    private String contextXml = null;

    private static String nonContextId = "escidoc:persistent1";

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void setUp() throws Exception {
        this.path += "/rest";

        if (contextId == null) {
            Document context =
                EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
            substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
            String template = toString(context, false);
            contextXml = create(template);
            assertXmlValidContext(contextXml);
            assertCreatedContext(contextXml, template, startTimestamp);
            Document created = EscidocAbstractTest.getDocument(contextXml);
            contextId = getObjidValue(created);

            String lastModified = getLastModificationDateValue(created);
            open(contextId, getTaskParam(lastModified));

            // String test = null;
            // String test2 = null;
            String item = null;
            Document itemDoc =
                EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest",
                    "escidoc_item_198_for_create.xml");
            item = toString(substitute(itemDoc, "/item/properties/context/@href", "/ir/context/" + contextId), true);
            item = createItem(item);
            // test2 = getTemplateAsString(TEMPLATE_ITEM_PATH,
            // filename);
            // test = toString(getTemplateAsDocument(TEMPLATE_ITEM_PATH,
            // filename), true);
            nonContextId = getObjidValue(EscidocAbstractTest.getDocument(item));
        }
    }

    /**
     * Test creating an item in the mock framework.
     *
     * @param itemXml The xml representation of the item.
     * @return The created item.
     * @throws Exception If anything fails.
     */
    protected String createItem(final String itemXml) throws Exception {

        return handleXmlResult(getItemClient().create(itemXml));
    }

    /**
     * Test retrieving an existing context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmReC1() throws Exception {

        String context = retrieve(contextId);
        assertXmlValidContext(context);
        // assertXmlEquals(
        // "Context retrieval error: retrieved wrong context xml!",
        // contextXml, context);
        // assertCreatedContext(context, contextXml, startTimestamp);
    }

    /**
     * Test retrieving a not existing context.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = ContextNotFoundException.class)
    public void testOmReC2() throws Exception {

        retrieve("escidoc:UnknownContext");
    }

    /**
     * Test retrieving a context without id.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = MissingMethodParameterException.class)
    public void testOmReC3() throws Exception {

        retrieve(null);
    }

    /**
     * Test retrieving a context with a existing id but the id references no context.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = ContextNotFoundException.class)
    public void testOmReC4() throws Exception {

        retrieve(nonContextId);
    }

    /**
     * Test retrieving the persistent context object with id "escidoc:persistent3".
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmReC5() throws Exception {

        String retrievedXml = null;
        try {
            retrievedXml = retrieve("escidoc:persistent10");
        }
        catch (Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNotNull("No context data retrieved. ", retrievedXml);
        assertXmlValidContext(retrievedXml);

    }

    /**
     * Test retrieving the persistent context object with id "escidoc:persistent5".
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmReC6() throws Exception {

        String retrievedXml = null;
        try {
            retrievedXml = retrieve("escidoc:persistent5");
        }
        catch (Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNotNull("No context data retrieved. ", retrievedXml);
        assertXmlValidContext(retrievedXml);

    }

    /**
     * Test retrieving the persistent context object with id "escidoc:persistent10".
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmReC7() throws Exception {

        String retrievedXml = null;
        try {
            retrievedXml = retrieve("escidoc:persistent10");
        }
        catch (Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNotNull("No context data retrieved. ", retrievedXml);
        assertXmlValidContext(retrievedXml);

    }

    /**
     * Test retrieving the persistent context object with id "escidoc:persistent16".
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmReC8() throws Exception {

        String retrievedXml = null;
        try {
            retrievedXml = retrieve("escidoc:persistent10");
        }
        catch (Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNotNull("No context data retrieved. ", retrievedXml);
        assertXmlValidContext(retrievedXml);

    }

    /**
     * Test retrieve the properties of context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveProperties() throws Exception {
        Document context = EscidocAbstractTest.getDocument(retrieve(contextId));

        String properties = retrieveProperties(contextId);
        assertXmlValidContext(properties);

        assertContextProperties(properties, toString(selectSingleNode(EscidocAbstractTest.getDocument(contextXml),
            "/context/properties"), true), "/ir/context/" + contextId + "/properties",
            getLastModificationDateValue(context), startTimestamp);
    }

    /**
     * Tests if the property name of context compares to the indirect set title in DC.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testComparePropertiesWithRelsExt() throws Exception {
        String properties = retrieveProperties(contextId);

        TripleStoreTestBase tripleStore = new TripleStoreTestBase();
        String result =
            tripleStore.requestMPT("<info:fedora/" + contextId + "> " + "<http://purl.org/dc/elements/1.1/title>"
                + " *", "RDF/XML");

        String name =
            selectSingleNodeAsserted(EscidocAbstractTest.getDocument(result), "/RDF/Description/title")
                .getTextContent();
        // result =
        // tripleStore.requestMPT(
        // "<info:fedora/" + contextId + "> "
        // + "<http://www.nsdl.org/ontologies/relationships/title>"
        // + " *", "RDF/XML");
        //
        // String title =
        // selectSingleNodeAsserted(getDocument(result),
        // "/RDF/Description/title").getTextContent();

        // assertEquals(name, title);

        String propName =
            selectSingleNodeAsserted(EscidocAbstractTest.getDocument(properties), "/properties/name").getTextContent();

        assertEquals(propName, name);
    }

    /**
     * Test retrieve resources of context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveResources() throws Exception {
        String resources = retrieveResources(contextId);
        assertXmlValidContext(resources);

        Document contextResources = EscidocAbstractTest.getDocument(contextXml);

        // context resources contains members
        // TODO check this for REST case in asserXmlValid method (see todo)

        // check members
        String membersHref = selectSingleNode(contextResources, "/context/resources/members/@href").getNodeValue();

        String SUB_CONTEXT_MEMBERS = Constants.SUB_RESOURCES + "/" + "members";

        if (!membersHref.endsWith(SUB_CONTEXT_MEMBERS)) {
            throw new Exception("resource members href wrong: " + membersHref + ", should end with "
                + SUB_CONTEXT_MEMBERS);
        }
    }

    /**
     * Assert the xmlItemProperties match the expected xmlTemplateItemProperties.
     *
     * @param xmlContextProperties         The retrieved properties.
     * @param xmlTemplateContextProperties The expected properties.
     * @param expectedHRef                 The expected href.
     * @param expectedLastModificationTimestamp
     *                                     The last-modification timestamp of the item.
     * @param timestampBeforeCreation      A timestamp before the creation of the item.
     * @throws Exception If anything fails.
     */
    private void assertContextProperties(
        final String xmlContextProperties, final String xmlTemplateContextProperties, final String expectedHRef,
        final String expectedLastModificationTimestamp, final String timestampBeforeCreation) throws Exception {

        Document createdProperties = EscidocAbstractTest.getDocument(xmlContextProperties);
        String href = getRootElementHrefValue(createdProperties);
        if ("".equals(href)) {
            href = null;
        }
        assertNotNull("Context Properties error: href attribute was not set!", href);
        assertEquals("Context Properties error: href has wrong value!", expectedHRef, href);
        String rootLastModificationDate = getLastModificationDateValue(createdProperties);
        if ("".equals(rootLastModificationDate)) {
            rootLastModificationDate = null;
        }
        assertNotNull("Context Properties error: last-modification-date attribute " + "was not set!",
            rootLastModificationDate);

        assertXmlExists("Context Properties error: creation-date was not set!", createdProperties,
            "/properties/creation-date");

        assertXmlExists("Context Properties error: created-by was not set in properties!", createdProperties,
            "/properties/created-by");

        assertXmlExists("Context Properties error: type was not set in properties!", createdProperties,
            "/properties/type");

        String creationDate = selectSingleNode(createdProperties, "/properties/creation-date").getTextContent();

        assertTimestampEquals("Context Properties error: last-modification-date in properties "
            + "and in root element are not equal!", expectedLastModificationTimestamp, rootLastModificationDate);

        assertTimestampIsEqualOrAfter("Context Properties error: creation-date is not as expected!", creationDate,
            timestampBeforeCreation);

        assertReferencingElement("Invalid created-by. ", createdProperties, "/properties/created-by",
            Constants.USER_ACCOUNT_BASE_URI);

        Node nodeLockOwner = selectSingleNode(createdProperties, "/context/properties/lock-owner");
        assertNull("Context Properties error: lock-owner must be null!", nodeLockOwner);
    }

    /**
     * Test if the admin descriptors element and the resources wrongly contain conditional root attributes when
     * retrieving a context.
     */
    @Test
    public void testIssue1001() throws Exception {
        final Document context = EscidocAbstractTest.getDocument(retrieve(contextId));

        assertXmlNotExists("admin descriptors element contains conditional root attribute", context,
            "/context/admin-descriptors/@last-modification-date");

        final Document adminDescriptors = EscidocAbstractTest.getDocument(retrieveAdminDescriptors(contextId));

        assertXmlExists("admin descriptors element does not contain conditional root attribute", adminDescriptors,
            "/admin-descriptors/@last-modification-date");
        assertXmlNotExists("resources element contains conditional root attribute", context,
            "/context/resources/@last-modification-date");

        final Document resources = EscidocAbstractTest.getDocument(retrieveResources(contextId));

        assertXmlExists("resources element does not contain conditional root attribute", resources,
            "/resources/@last-modification-date");
    }
}
