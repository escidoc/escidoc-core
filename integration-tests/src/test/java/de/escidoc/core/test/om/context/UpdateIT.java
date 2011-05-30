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

import de.escidoc.core.common.exceptions.remote.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.ContextNameNotUniqueException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.remote.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.remote.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.test.EscidocAbstractTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Test the mock implementation of the item resource.
 *
 * @author Michael Schneider
 */
public class UpdateIT extends ContextTestBase {

    private static final String XPATH_ADMIN_DESCRIPTOR = "/context/admin-descriptors/admin-descriptor";

    private String path = "";

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void setUp() throws Exception {
        this.path += "/rest";
    }

    /**
     * Successfully test updating a context. Enhanced related to Issue 611. Enhanced related to Issue 629.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmUc1() throws Exception {

        final String newType = "newType";
        String contextXml = null;

        Document context =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
        String template = toString(context, false);

        String created = create(template);
        assertXmlValidContext(created);

        Document createdDoc = EscidocAbstractTest.getDocument(created);
        String id = getObjidValue(createdDoc);

        // change contexts type
        assertNotEquals("Type should be update by new value.", newType, selectSingleNode(createdDoc,
            "/context/properties/type"));
        createdDoc = (Document) substitute(createdDoc, "/context/properties/type", newType);
        created = toString(createdDoc, false);

        contextXml = update(id, created);

        assertXmlValidContext(contextXml);

        // assert changes
        Document contextDoc = getDocument(contextXml);
        String resultPropType = selectSingleNode(contextDoc, "/context/properties/type").getTextContent();
        assertEquals("Type hasn't changed", newType, resultPropType);
    }

    /**
     * Successfully test handling attribute values.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmUc6() throws Exception {
        Document context =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");

        // check updating /context/properties/name
        String uniqueName = getUniqueName("PubMan Context ");
        substitute(context, "/context/properties/name", uniqueName);
        String created = create(toString(context, false));
        assertXmlValidContext(created);

        Document resultDocument = EscidocAbstractTest.getDocument(created);

        HashMap<String, String> admTitleList = new HashMap<String, String>();

        // TODO add check of OU
        // @title -----
        String id = getObjidValue(resultDocument);

        NodeList nodes = selectNodeList(resultDocument, XPATH_ADMIN_DESCRIPTOR);

        String admDescrTitle = null;
        String admDescrName = null;
        String updateTile = null;
        for (int i = 0; i < nodes.getLength(); i++) {
            admDescrTitle =
                toString(selectSingleNode(resultDocument, XPATH_ADMIN_DESCRIPTOR + "[" + (i + 1) + "]/@title"), true);

            admDescrName =
                toString(selectSingleNode(resultDocument, XPATH_ADMIN_DESCRIPTOR + "[" + (i + 1) + "]/@name"), true);

            updateTile = "' > " + (i + 1) + " < &";
            substitute(resultDocument, XPATH_ADMIN_DESCRIPTOR + "[" + (i + 1) + "]/@title", updateTile);
            admTitleList.put(admDescrName, admDescrTitle);
        }

        // created-by ----
        String createdBy = selectSingleNode(resultDocument, "context/properties/created-by/@href").getTextContent();
        substitute(resultDocument, "context/properties/created-by/@href", createdBy + "1");

        // modified-by ----
        String modifiedBy = selectSingleNode(resultDocument, "context/properties/modified-by/@href").getTextContent();
        substitute(resultDocument, "context/properties/modified-by/@href", modifiedBy + "1");

        // ----------
        String updated = update(id, toString(resultDocument, false));
        assertXmlValidContext(updated);
        resultDocument = EscidocAbstractTest.getDocument(updated);

        nodes = selectNodeList(resultDocument, XPATH_ADMIN_DESCRIPTOR);

        for (int i = 0; i < nodes.getLength(); i++) {
            admDescrName =
                toString(selectSingleNode(resultDocument, XPATH_ADMIN_DESCRIPTOR + "[" + (i + 1) + "]/@name"), true);

            assertEquals("Title by update not discarded", selectSingleNode(resultDocument,
                XPATH_ADMIN_DESCRIPTOR + "[" + (i + 1) + "]/@title").getTextContent(), admTitleList.get(admDescrName));
        }

        assertEquals("created-by updated", selectSingleNode(resultDocument, "context/properties/created-by/@href")
            .getTextContent(), createdBy);

        assertEquals("modified-by updated", selectSingleNode(resultDocument, "context/properties/modified-by/@href")
            .getTextContent(), createdBy);
    }

    /**
     * Successfully test for updating attribute name of admin-descriptors.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmUc7() throws Exception {

        Document context =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");

        String admDescNamePrefix = "admDesc_name_";

        // check updating /context/properties/name
        String uniqueName = getUniqueName("PubMan Context ");
        substitute(context, "/context/properties/name", uniqueName);
        String created = create(toString(context, false));
        assertXmlValidContext(created);

        Document resultDocument = EscidocAbstractTest.getDocument(created);

        NodeList nodes = selectNodeList(resultDocument, XPATH_ADMIN_DESCRIPTOR);

        Vector<String> updatedNames = new Vector<String>();
        String updateName = null;
        for (int i = 0; i < nodes.getLength(); i++) {
            updateName = admDescNamePrefix + (i + 1);
            substitute(resultDocument, XPATH_ADMIN_DESCRIPTOR + "[" + (i + 1) + "]/@name", updateName);
            updatedNames.add(updateName);
        }

        String id = getObjidValue(resultDocument);
        String updated = update(id, toString(resultDocument, false));
        assertXmlValidContext(updated);
        resultDocument = EscidocAbstractTest.getDocument(updated);

        nodes = selectNodeList(resultDocument, XPATH_ADMIN_DESCRIPTOR);

        for (int i = 0; i < nodes.getLength(); i++) {
            String nodeName =
                selectSingleNode(resultDocument, XPATH_ADMIN_DESCRIPTOR + "[" + (i + 1) + "]/@name").getTextContent();

            int pos = updatedNames.indexOf(nodeName);
            if (pos != -1) {
                updatedNames.remove(pos);
            }
            else {
                throw new Exception("Name of admin-descriptor not updated.");
            }
        }
    }

    /**
     * Test updating a context with a non existing context id and correct context xml.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = ContextNotFoundException.class)
    public void testOmUc2() throws Exception {

        Document context =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
        update("escidoc:UnknownContext", create(toString(context, false)));
    }

    /**
     * Test updating a context with an existing context id and incorrect context xml. Change the value of read-only
     * attribute objid.
     *
     * @throws Exception If anything fails.
     */
    @Ignore("Change the value of read-only attribute objid")
    @Test(expected = ReadonlyAttributeViolationException.class)
    public void testOmUc3a() throws Exception {

        Document context =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
        String template = toString(context, false);
        String created = create(template);
        assertXmlValidContext(created);
        Document createdDoc = EscidocAbstractTest.getDocument(created);
        String id = getObjidValue(createdDoc);
        Node update = substitute(createdDoc, "context/@href", "/ir/context/" + id + "1/admin-descriptor");
        update(id, toString(update, false));
    }

    /**
     * Test updating a context with an existing context id and incorrect context xml. Change the value of read-only
     * attribute xlink href.
     *
     * @throws Exception If anything fails.
     */
    @Ignore("Change the value of read-only attribute xlink:href")
    @Test(expected = ReadonlyAttributeViolationException.class)
    public void testOmUc3b() throws Exception {

        Document context =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
        String contextName = getUniqueName("PubMan Context ");
        substitute(context, "/context/properties/name", contextName);

        String template = toString(context, false);
        String created = create(template);
        assertXmlValidContext(created);

        Document createdDoc = EscidocAbstractTest.getDocument(created);
        String id = getObjidValue(createdDoc);
        String toBeChanged = null;
        Node update = null;
        toBeChanged = getAttributeValue(createdDoc, "/context", XLINK_HREF_ESCIDOC);
        update = substitute(createdDoc, "/context/@href", toBeChanged + "12");
        update(id, toString(update, false));
    }

    /**
     * Test updating a context with an existing context id and incorrect context xml. Change the value of read-only
     * attribute xlink title.
     *
     * @throws Exception If anything fails.
     */
    @Ignore("Change the value of read-only attribute xlink:title")
    @Test(expected = ReadonlyAttributeViolationException.class)
    public void testOmUc3c() throws Exception {
        Document context =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
        String template = toString(context, false);
        String created = create(template);
        assertXmlValidContext(created);
        Document createdDoc = EscidocAbstractTest.getDocument(created);
        String id = getObjidValue(createdDoc);
        String toBeChanged = getAttributeValue(createdDoc, "/context", XLINK_TITLE_ESCIDOC);
        Node update = substitute(createdDoc, "/context/@title", toBeChanged + "12");
        String s = toString(update, false);
        update(id, s);
    }

    /**
     * Test updating a Context. The first update deletes a property description, the second update add the property
     * description again.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmUc3l() throws Exception {
        Document context =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
        String uniqueName = getUniqueName("PubMan Context ");
        substitute(context, "/context/properties/name", uniqueName);
        String xpathDescription = "/context/properties/description";
        // Node contextWithoutDescription =
        // deleteElement(context, xpathDescription);
        String template = toString(context, false);
        String created = create(template);
        assertXmlValidContext(created);

        Document createdDoc = EscidocAbstractTest.getDocument(created);
        Node description = selectSingleNode(createdDoc, "/context/properties/description");
        assertNotNull("node description is null", description);
        String id = getObjidValue(createdDoc);

        Node update = deleteElement(createdDoc, xpathDescription);

        String contextXml = toString(update, false);
        String updated = update(id, contextXml);
        Document resultDocument = EscidocAbstractTest.getDocument(updated);

        description = selectSingleNode(resultDocument, "/context/properties/description");
        assertNull(description);
        Node type = selectSingleNode(resultDocument, "/context/properties/type");
        Element newDescription =
            resultDocument.createElementNS("http://www.escidoc.de/schemas/context/0.6", "prop:description");
        newDescription.setTextContent("new Description");
        selectSingleNode(resultDocument, "/context/properties").insertBefore(newDescription, type);
        String doubleModified = toString(resultDocument, false);
        String doubleUpdated = update(id, doubleModified);
        Document doubleUpdatedDocument = EscidocAbstractTest.getDocument(doubleUpdated);
        description = selectSingleNode(doubleUpdatedDocument, "/context/properties/description");
        assertNotNull("node description is null", description);
    }

    /**
     * Test updating a Context. Change all non-fixed values of context (name, description).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmUc3d() throws Exception {
        Document context =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
        String uniqueName = getUniqueName("PubMan Context ");
        substitute(context, "/context/properties/name", uniqueName);
        String template = toString(context, false);
        String created = create(template);
        assertXmlValidContext(created);

        Document createdDoc = EscidocAbstractTest.getDocument(created);
        String id = getObjidValue(createdDoc);

        String xpathName = "/context/properties/name";
        uniqueName = selectSingleNode(createdDoc, xpathName).getTextContent() + "-test-12";

        Node update = substitute(createdDoc, xpathName, uniqueName);
        String xpathDescription = "/context/properties/description";
        String newDescription = "------ New Test Context Description - " + "< &lt; & &amp; &gt; > ------";
        update = substitute(createdDoc, xpathDescription, newDescription);
        String contextXml = toString(update, false);
        String updated = update(id, contextXml);
        Document resultDocument = EscidocAbstractTest.getDocument(updated);

        String nameValue = selectSingleNode(resultDocument, xpathName).getTextContent();
        assertEquals("Property element 'name' not updated ", uniqueName, nameValue);

        String descriptionValue = selectSingleNode(resultDocument, xpathDescription).getTextContent();
        assertEquals("Property element 'description' not updated ", newDescription, descriptionValue);
    }

    /**
     * Test updating a Context. All non-fixed values of context (name, description) contain entity references and stay
     * unchanged .
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmUc3m() throws Exception {
        Document context =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
        String uniqueName = getUniqueName("PubMan Context ");
        substitute(context, "/context/properties/name", uniqueName + "&lt");
        String description = selectSingleNode(context, "/context/properties/description").getTextContent();
        substitute(context, "/context/properties/description", description + "&quot");
        String template = toString(context, false);
        String created = create(template);
        assertXmlValidContext(created);

        Document createdDoc = EscidocAbstractTest.getDocument(created);
        String id = getObjidValue(createdDoc);

        String xpathName = "/context/properties/name";

        String updated = update(id, created);
        Document resultDocument = EscidocAbstractTest.getDocument(updated);

        String nameValue = selectSingleNode(resultDocument, xpathName).getTextContent();

        String descriptionValue = selectSingleNode(resultDocument, "/context/properties/description").getTextContent();
        assertEquals("Property element 'description' updated ", description + "&quot", descriptionValue);
        assertEquals("Property element 'name' updated ", uniqueName + "&lt", nameValue);
    }

    /**
     * Test updating a Context. Check handling of non-unique name value.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = ContextNameNotUniqueException.class)
    public void testOmUc3d1() throws Exception {

        Document context =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
        String uniqueName = getUniqueName("PubMan Context ");
        substitute(context, "/context/properties/name", uniqueName);
        String created = create(toString(context, false));
        assertXmlValidContext(created);

        Document context2 =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
        String uniqueName2 = getUniqueName("PubMan Context ");
        substitute(context2, "/context/properties/name", uniqueName2);
        String created2 = create(toString(context2, false));
        assertXmlValidContext(created2);

        // update first context with non-unique name of second
        Document createdDoc = EscidocAbstractTest.getDocument(created);
        String id = getObjidValue(createdDoc);

        String xpathName = "/context/properties/name";
        Node update = substitute(createdDoc, xpathName, uniqueName2);

        String contextXml = toString(update, false);
        update(id, contextXml);
    }

    /**
     * Test updating name of Context with empty string.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = MissingElementValueException.class)
    public void testOmUc3d2() throws Exception {

        Document context =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
        String uniqueName = getUniqueName("PubMan Context ");
        substitute(context, "/context/properties/name", uniqueName);
        String template = toString(context, false);
        String created = create(template);
        assertXmlValidContext(created);

        Document createdDoc = EscidocAbstractTest.getDocument(created);
        String id = getObjidValue(createdDoc);

        String xpathName = "/context/properties/name";
        Node update = substitute(createdDoc, xpathName, "");

        String contextXml = toString(update, false);
        update(id, contextXml);
    }

    /**
     * Test updating a context with an existing context id and incorrect context xml. Change the value of read-only
     * element /context/properties/description.
     *
     * @throws Exception If anything fails.
     */
    // Commented out because framework does not test read-only elements
    @Ignore("Change the value of read-only element prop:description")
    @Test
    public void testOmUc3e() throws Exception {
        Class<?> ec = ReadonlyElementViolationException.class;
        try {
            Document context =
                EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
            substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
            String template = toString(context, false);
            String created = create(template);
            assertXmlValidContext(created);
            Document createdDoc = EscidocAbstractTest.getDocument(created);
            String xpath = "/context/properties/description";
            String id = getObjidValue(createdDoc);
            String toBeChanged = selectSingleNode(createdDoc, xpath).getTextContent();
            Node update = substitute(createdDoc, xpath, toBeChanged + "12");
            update(id, toString(update, false));
            fail(ec + " expected but no error occured!");
        }
        catch (Exception e) {
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * Test updating a context with an existing context id and incorrect context xml. Change the value of read-only
     * element /context/properties/status.
     *
     * @throws Exception If anything fails.
     */
    // Commented out because framework does not test read-only elements
    @Ignore("Change the value of read-only element prop:status")
    @Test
    public void testOmUc3f() throws Exception {
        Class<?> ec = ReadonlyElementViolationException.class;
        try {
            Document context =
                EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
            substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
            String template = toString(context, false);
            String created = create(template);
            assertXmlValidContext(created);
            Document createdDoc = EscidocAbstractTest.getDocument(created);
            String xpath = "/context/properties/status";
            String id = getObjidValue(createdDoc);
            Node update = substitute(createdDoc, xpath, CONTEXT_STATUS_OPENED);
            update(id, toString(update, false));
            fail(ec + " expected but no error occured!");
        }
        catch (Exception e) {
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * Test updating a context with an existing context id and incorrect context xml. Change the value of read-only
     * element /context/properties/type.
     *
     * @throws Exception If anything fails.
     */
    // Commented out because framework does not test read-only elements
    @Ignore("Change the value of read-only element prop:type")
    @Test
    public void testOmUc3g() throws Exception {
        Class<?> ec = ReadonlyElementViolationException.class;
        try {
            Document context =
                EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
            substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
            String template = toString(context, false);
            String created = create(template);
            assertXmlValidContext(created);
            Document createdDoc = EscidocAbstractTest.getDocument(created);
            String xpath = "/context/properties/type";
            String id = getObjidValue(createdDoc);
            Node update = substitute(createdDoc, xpath, CONTEXT_TYPE_SWB);
            update(id, toString(update, false));
            fail(ec + " expected but no error occured!");
        }
        catch (Exception e) {
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * Test updating a context with an existing context id and incorrect context xml. Change the value of read-only
     * attribute /context/properties/creator/@xlink href.
     *
     * @throws Exception If anything fails.
     */
    // Commented out because framework does not test read-only elements
    @Ignore("Change the value of read-only attribute prop:creator@xlink:href")
    @Test
    public void UtestOmUc3h() throws Exception {
        Class<?> ec = ReadonlyAttributeViolationException.class;
        try {
            Document context =
                EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
            substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
            String template = toString(context, false);
            String created = create(template);
            assertXmlValidContext(created);
            Document createdDoc = EscidocAbstractTest.getDocument(created);
            String xpath = "/context/properties/creator";
            String id = getObjidValue(createdDoc);
            String toBeChanged = getAttributeValue(createdDoc, xpath, XLINK_HREF_ESCIDOC);
            Node update = substitute(createdDoc, xpath + "/@href", toBeChanged + "12");
            update(id, toString(update, false));
            fail(ec + " expected but no error occured!");
        }
        catch (Exception e) {
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * Test updating a context with an existing context id and incorrect context xml. Change the value of read-only
     * attribute /context/properties/organizational-unit/@xlink href.
     *
     * @throws Exception If anything fails.
     */
    // Commented out because framework does not test read-only elements
    @Ignore("Change the value of read-only attribute prop:organizational-unit@xlink:href")
    @Test
    public void testOmUc3i() throws Exception {
        Class<?> ec = ReadonlyAttributeViolationException.class;
        try {
            Document context =
                EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
            substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
            String template = toString(context, false);
            String created = create(template);
            assertXmlValidContext(created);
            Document createdDoc = EscidocAbstractTest.getDocument(created);
            String xpath = "/context/properties/organizational-units/organizational-unit";
            String id = getObjidValue(createdDoc);
            String toBeChanged = getAttributeValue(createdDoc, xpath, XLINK_HREF_ESCIDOC);
            Node update = substitute(createdDoc, xpath + "/@href", toBeChanged + "12");
            String s = toString(update, false);
            update(id, s);
            fail(ec + " expected but no error occured!");
        }
        catch (Exception e) {
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * Test updating a context with an existing context id and incorrect context xml. Change the value of read-only
     * element /context/properties/creation-date.
     *
     * @throws Exception If anything fails.
     */
    // Commented out because framework does not test read-only elements
    @Ignore("Change the value of read-only element prop:creation-date")
    @Test
    public void testOmUc3j() throws Exception {
        Class<?> ec = ReadonlyElementViolationException.class;
        try {
            Document context =
                EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
            substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
            String template = toString(context, false);
            String created = create(template);
            assertXmlValidContext(created);
            Document createdDoc = EscidocAbstractTest.getDocument(created);
            String xpath = "/context/properties/creation-date";
            String id = getObjidValue(createdDoc);
            Node update = substitute(createdDoc, xpath, getNowAsTimestamp());
            update(id, toString(update, false));
            fail(ec + " expected but no error occured!");
        }
        catch (Exception e) {
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * Test updating a context with an existing context id and incorrect context xml. Change the value of read-only
     * element /context/properties/last-modification-date.
     *
     * @throws Exception If anything fails.
     */
    // Commented out because framework does not test read-only elements
    @Ignore("Change the value of read-only element prop:last-modification-date")
    @Test
    public void testOmUc3k() throws Exception {
        Class<?> ec = ReadonlyElementViolationException.class;
        try {
            Document context =
                EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
            substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
            String template = toString(context, false);
            String created = create(template);
            assertXmlValidContext(created);
            Document createdDoc = EscidocAbstractTest.getDocument(created);
            String xpath = "/context/properties/last-modification-date";
            String id = getObjidValue(createdDoc);
            Node update = substitute(createdDoc, xpath, getNowAsTimestamp());
            String s = toString(update, false);
            update(id, s);
            fail(ec + " expected but no error occured!");
        }
        catch (Exception e) {
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * Test updating a context with an existing context id and incorrect context xml. Change the value of read-only
     * attribute last-modification-date.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = OptimisticLockingException.class)
    public void testOmUc5() throws Exception {
        Document context =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
        String template = toString(context, false);
        String created = create(template);
        assertXmlValidContext(created);
        Document createdDoc = EscidocAbstractTest.getDocument(created);
        String id = getObjidValue(createdDoc);
        String date = getNowAsTimestamp();
        Node update = substitute(createdDoc, "/context/@last-modification-date", date);
        String s = toString(update, false);
        update(id, s);
    }

    /**
     * Test successfully adding of a new admin-descriptor to a context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAddNewAdminDescriptorWhileUpdate() throws Exception {
        Document context =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
        NodeList adminDescriptors = selectNodeList(context, "/context/admin-descriptors/admin-descriptor");

        String contextXml = toString(context, true);
        String createdXml = create(contextXml);
        assertXmlValidContext(createdXml);
        Document createdDocument = EscidocAbstractTest.getDocument(createdXml);
        String createdContextId = getObjidValue(createdDocument);
        NodeList adminDescriptorsAfterCreate =
            selectNodeList(createdDocument, "/context/admin-descriptors/admin-descriptor");
        assertEquals(adminDescriptors.getLength(), adminDescriptorsAfterCreate.getLength());

        Element adminDescriptor =
            createdDocument.createElementNS("http://www.escidoc.de/schemas/context/0.6", "context:admin-descriptor");
        adminDescriptor.setAttribute("name", "name1");

        Element adminDescriptorContent = createdDocument.createElement("bla");
        adminDescriptor.appendChild(adminDescriptorContent);
        selectSingleNode(createdDocument, "/context/admin-descriptors").appendChild(adminDescriptor);
        String contextWithAdditionalAdminDescriptorsXml = toString(createdDocument, true);
        String updated = update(createdContextId, contextWithAdditionalAdminDescriptorsXml);
        Document updatedDocument = EscidocAbstractTest.getDocument(updated);
        NodeList adminDescriptorsAfterUpdate =
            selectNodeList(updatedDocument, "/context/admin-descriptors/admin-descriptor");
        assertEquals(adminDescriptorsAfterUpdate.getLength() - 1, adminDescriptorsAfterCreate.getLength());
    }
}
