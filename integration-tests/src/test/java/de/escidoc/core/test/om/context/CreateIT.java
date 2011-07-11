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

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.violated.ContextNameNotUniqueException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.oum.organizationalunit.OrganizationalUnitTestBase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test the mock implementation of the item resource.
 *
 * @author Michael Schneider
 */
public class CreateIT extends ContextTestBase {

    private String path = TEMPLATE_CONTEXT_PATH;

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
     * Test the timestamp validation.
     *
     * @throws Exception If the compareTimestamps() methods throws invalid exception.
     */
    @Ignore("Test the timestamp validation")
    @Test
    public void testDateValidation() throws Exception {
        String t1 = "2008-02-24T20:13:010Z";

        /**
         * Fedora 3.0b1 delivered following date result. It seams that this is
         * invalid. TODO alter test to check Fedora.
         *
         */
        String t2 = "2008-02-24T20:13:01+0000";

        compareTimestamps(t1, t2);
    }

    /**
     * Successful creation of a Context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmCr1a() throws Exception {

        Document context = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
        String template = toString(context, false);
        String created = create(template);
        assertCreatedContext(created, template, startTimestamp);
    }

    /**
     * Successful creation of a Context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmCrc1b() throws Exception {

        Document context = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_mpdl.xml");
        substitute(context, "/context/properties/name", getUniqueName("my context reloaded "));
        String template = toString(context, false);
        String created = create(template);
        assertXmlValidContext(created);
        assertCreatedContext(created, template, startTimestamp);
    }

    /**
     * Successful creation of a Context with special chars in name, description, and type.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmCr1aSC() throws Exception {

        Document context = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create_sc.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan < äöüß > & &amp; Context "));
        String template = toString(context, false);
        String created = create(template);
        assertCreatedContext(created, template, startTimestamp);
    }

    /**
     * Successful creation of a Context with entity references in elements and attributes.
     * <p/>
     * Test if double organizational units entries are removed.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmCrc1c() throws Exception {

        Document context =
            EscidocAbstractTest.getTemplateAsDocument(this.path, "context_createWithDoubleOrganizationalUnits.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
        String template = toString(context, false);
        String created = create(template);
        Document createdDoc = EscidocAbstractTest.getDocument(created);
        assertXmlValidContext(created);
        NodeList expectedOus =
            selectNodeList(EscidocAbstractTest.getDocument(template), XPATH_CONTEXT_PROPERTIES_ORGANIZATIONAL_UNIT);
        NodeList toBeAssertedOus = selectNodeList(createdDoc, XPATH_CONTEXT_PROPERTIES_ORGANIZATIONAL_UNIT);

        // double entries of same ou are to remove
        // it is assumed that only one unit is double
        assertEquals("double Organizational Units not removed", expectedOus.getLength(),
            toBeAssertedOus.getLength() + 1);

        // compare admin-descriptor titles
        NodeList expectedAdminDesc = selectNodeList(getDocument(template), XPATH_CONTEXT_ADMIN_DESCRIPTOR);
        NodeList toBeAssertedAdminDesc = selectNodeList(createdDoc, XPATH_CONTEXT_ADMIN_DESCRIPTOR);
        assertEquals(expectedAdminDesc.getLength(), toBeAssertedAdminDesc.getLength());
    }

    /**
     * Successful creation of a Context. Issue 303 test.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmCrc1d() throws Exception {
        /*
         * This test should provoke the exception of issue 303.
         * 
         * 500 Internal eSciDoc System Error Should not be reached.
         * StaxParser.handle(StartElement)
         */
        Document context = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_mpdl_issue303.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
        String template = toString(context, false);
        String created = create(template);
        assertXmlValidContext(created);
    }

    /**
     * Successful creation of a Context. Issue 357 test.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmCrc1e() throws Exception {

        Document context = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_mpdl_issue357.xml");
        substitute(context, "/context/properties/name", getUniqueName("Publications of the MPI for Plasmaphysics "));
        String template = toString(context, false);
        String created = create(template);
        assertCreatedContext(created, template, startTimestamp);
    }

    /**
     * Test large Context handling for REST.
     *
     * @throws Exception If anything fails.
     */
    // FIXME this test should run (and it does in former time)
    @Ignore("Test large Context handling for REST")
    @Test
    public void testOmCrc1f() throws Exception {
        Document context = EscidocAbstractTest.getTemplateAsDocument(this.path, "context-large-rest.xml");
        substitute(context, "/context/properties/name", getUniqueName("Large Context-1 "));
        create(toString(context, false));
    }

    /**
     * Create a Context with a not unique name.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = ContextNameNotUniqueException.class)
    public void testOmCrc2() throws Exception {

        Document context = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
        String template = toString(context, false);
        create(template);
        create(template);
    }

    /**
     * Create a Context with empty context name.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = MissingElementValueException.class)
    public void testOmCrc2a() throws Exception {

        Document context = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create.xml");
        substitute(context, "/context/properties/name", "");
        String template = toString(context, false);
        create(template);
    }

    /**
     * Create a Context with invalid xml (missing properties element).
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = XmlSchemaValidationException.class)
    public void testOmCrc3a() throws Exception {

        create(toString(deleteElement(EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create.xml"),
            "/context/properties"), false));
    }

    /**
     * Create a Context with missing admin-descriptors element.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmCrc3b() throws Exception {

        Document context = getTemplateAsDocument(this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));

        create(toString(deleteElement(context, "/context/admin-descriptors"), false));
    }

    /**
     * Create a Context with no admin-descriptor element in admin-descriptors.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmCrc3c() throws Exception {

        Document context = getTemplateAsDocument(this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));

        create(toString(deleteElement(context, "/context/admin-descriptors/admin-descriptor"), false));
    }

    /**
     * Create a Context with invalid xml (without name).
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = XmlSchemaValidationException.class)
    public void testOmCrc3d() throws Exception {

        create(toString(deleteElement(EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create.xml"),
            "/context/properties/name"), false));
    }

    /**
     * Create a Context with invalid xml (context objid is set).
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = XmlSchemaValidationException.class)
    public void testOmCrc4a() throws Exception {

        String contextXml = null;

        Map<String, String> elements = new HashMap<String, String>();
        elements.put("/context/@" + XLINK_HREF_TEMPLATES, "escidoc:id1");
        contextXml = getContextTemplateWithReadOnlyElements(elements);
        create(contextXml);
    }

    /**
     * Create a Context with invalid xml (context href is set).
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = XmlSchemaValidationException.class)
    public void testOmCrc4b() throws Exception {

        Map<String, String> elements = new HashMap<String, String>();
        elements.put("/context/@" + XLINK_HREF_TEMPLATES, "/ir/context/escidoc:id1");
        create(getContextTemplateWithReadOnlyElements(elements));
    }

    /**
     * Create a Context with invalid xml (admin-descriptor objid is set).
     *
     * @throws Exception If anything fails.
     */
    @Ignore("I don't know this test was commented out")
    @Test(expected = XmlSchemaValidationException.class)
    public void testOmCrc4c() throws Exception {

        Map<String, String> elements = new HashMap<String, String>();
        elements.put("/context/admin-descriptors/@objid", "escidoc:id2");
        create(getContextTemplateWithReadOnlyElements(elements));
    }

    /**
     * Create a Context with invalid xml (admin-descriptor href is set).
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = XmlSchemaValidationException.class)
    public void testOmCrc4d() throws Exception {

        Map<String, String> elements = new HashMap<String, String>();
        elements.put("/context/admin-descriptors/admin-descriptor/@" + XLINK_HREF_TEMPLATES,
            "ir/context/escidoc:id2/admin-descriptors/admin-descriptor");
        create(getContextTemplateWithReadOnlyElements(elements));
    }

    /**
     * Create a Context with invalid xml (creation-date element is present and set).
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = XmlSchemaValidationException.class)
    public void testOmCrc4e() throws Exception {

        Map<String, String> elements = new HashMap<String, String>();
        elements.put("/context/properties/creation-date", "");
        create(getContextTemplateWithReadOnlyElements(elements));
    }

    /**
     * Create a Context with invalid xml (status element is present and set).
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = XmlSchemaValidationException.class)
    public void testOmCrc4f() throws Exception {

        Map<String, String> elements = new HashMap<String, String>();
        elements.put("/context/properties/status", "");
        create(getContextTemplateWithReadOnlyElements(elements));
    }

    /**
     * Create a Context with invalid xml (last-modification-date element is present and set).
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = XmlSchemaValidationException.class)
    public void testOmCrc4g() throws Exception {

        Map<String, String> elements = new HashMap<String, String>();
        elements.put("/context/properties/last-modification-date", "");
        create(getContextTemplateWithReadOnlyElements(elements));
    }

    /**
     * Create a Context with invalid xml (resources element is present and set).
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = XmlSchemaValidationException.class)
    public void testOmCrc4h() throws Exception {

        Map<String, String> elements = new HashMap<String, String>();
        elements.put("/context/resources", "");
        create(getContextTemplateWithReadOnlyElements(elements));
    }

    /**
     * Create a Context with invalid xml (creator element is present and set).
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = XmlSchemaValidationException.class)
    public void testOmCrc4i() throws Exception {

        Map<String, String> elements = new HashMap<String, String>();
        elements.put("/context/properties/creator", "");
        create(getContextTemplateWithReadOnlyElements(elements));
    }

    /**
     * Call create method without a Context (Context xml is null).
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = MissingMethodParameterException.class)
    public void testOmCrc5() throws Exception {

        create(null);
    }

    /**
     * Create a Context without OU. (forbidden)
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmCreateContextWithoutOU() throws Exception {

        Document contextDoc = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create.xml");
        substitute(contextDoc, "/context/properties/name", getUniqueName("PubMan Context "));
        deleteElements(contextDoc, "/context/properties/" + "organizational-units/organizational-unit");

        Class<?> ec = InvalidContentException.class;
        try {
            create(toString(contextDoc, false));
            fail(ec + " expected but no error occurred!");
        }
        catch (Exception e) {
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }

        // ---------------------------------------
        ec = XmlSchemaValidationException.class;
        try {
            create(toString(deleteElement(contextDoc, "/context/properties/" + "organizational-units"), false));
            fail(ec + " expected but no error occurred!");
        }
        catch (Exception e) {
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * Check if creating a context fails when a OU is in a wrong state (not open).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateContextWithWrongOuState() throws Exception {

        OrganizationalUnitTestBase organizationalUnitTestBase = new OrganizationalUnitTestBase();
        String ou = null;
        String context = null;
        Class<?> ec = InvalidStatusException.class;

        try {
            // create OU
            String ouXML = organizationalUnitTestBase.createSuccessfully("escidoc_ou_create.xml");
            ou = getObjidValue(ouXML);

            // create context
            String contextXML = EscidocAbstractTest.getTemplateAsString(path, "context_create.xml");
            contextXML = contextXML.replace("escidoc:persistent13", ou);
            try {
                contextXML = create(contextXML);
                fail(InvalidStatusException.class + " expected but no error occurred!");
            }
            catch (Exception e) {
                EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
            }
        }
        finally {
            if (context != null) {
                // delete context
                delete(context);
            }
            if (ou != null) {
                // delete OU
                organizationalUnitTestBase.delete(ou);
            }
        }
    }

    /**
     * Get a context template for creation containing some read only elements. The wanted read only elements (depicted
     * by their xpath, e.g. /context/properties/creation-date) are the keys in the elements map. If the stored value
     * equals the empty string the element is left as it is in the template, otherwise its value is substituted by the
     * stored value. If a the xpath to an read only element is not contained the element is deleted.
     *
     * @param expected The elements Map.
     * @return The resulting template.
     * @throws Exception If anything fails.
     */
    protected String getContextTemplateWithReadOnlyElements(final Map<String, String> expected) throws Exception {
        Node template =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH, "context_create_read_only_elements.xml");

        template = changeTemplateWithReadOnly(template, expected, "/context/@objid");
        template = changeTemplateWithReadOnly(template, expected, "/context/@" + XLINK_HREF_TEMPLATES);
        template = changeTemplateWithReadOnly(template, expected, "/context/admin-descriptor/@objid");
        template = changeTemplateWithReadOnly(template, expected, "/context/admin-descriptor/@" + XLINK_HREF_TEMPLATES);
        template = changeTemplateWithReadOnly(template, expected, "/context/properties/created-by");
        template = changeTemplateWithReadOnly(template, expected, "/context/properties/modified-by");
        template = changeTemplateWithReadOnly(template, expected, "/context/properties/creation-date");
        template = changeTemplateWithReadOnly(template, expected, "/context/properties/status");
        template = changeTemplateWithReadOnly(template, expected, "/context/properties/last-modification-date");
        template = changeTemplateWithReadOnly(template, expected, "/context/resources");
        return toString(template, false);
    }

    /**
     * Successful creation of a Context with empty content of an admin-descriptor.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = XmlSchemaValidationException.class)
    public void testOmCr7() throws Exception {

        Document context = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
        substitute(context, "/context/admin-descriptors/admin-descriptor[1]", "");
        String template = toString(context, false);

        create(template);
    }

    /**
     * Create a Context with whitespaces in admin-descriptor attribute name. This has to be fail with a schema
     * exception.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = XmlSchemaValidationException.class)
    public void testOmCrcAdminDesc() throws Exception {

        String nameWS = "Admin Descriptor Name with whitespaces";

        Document context = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create.xml");
        substitute(context, "/context/admin-descriptors/admin-descriptor[1]/@name", nameWS);
        String template = toString(context, false);

        create(template);
    }

    /**
     * Create a Context with more than the allowed number of characters in admin-descriptor attribute name. The length
     * is limited to 64 character. This has to be fail with a schema exception.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = XmlSchemaValidationException.class)
    public void testOmCrcAdminDesc2() throws Exception {

        String nameLong =
            "Admin_Descriptor_Name_without_whitespaces_but_" + "extra_long_to_reach_the_64_character_limit_of_fedora";

        Document context = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create.xml");
        substitute(context, "/context/admin-descriptors/admin-descriptor[1]/@name", nameLong);
        String template = toString(context, false);

        create(template);
    }

}
