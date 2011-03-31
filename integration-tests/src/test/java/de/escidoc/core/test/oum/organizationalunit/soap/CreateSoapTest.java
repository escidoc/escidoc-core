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
package de.escidoc.core.test.oum.organizationalunit.soap;

import de.escidoc.core.common.exceptions.remote.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.oum.organizationalunit.OrganizationalUnitTestBase;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Organizational Unit tests with Soap transport.
 *
 * @author Michael Schneider
 */
public class CreateSoapTest extends OrganizationalUnitTestBase {

    /**
     * Constructor.
     */
    public CreateSoapTest() {
        super(Constants.TRANSPORT_SOAP);
    }

    /**
     * Test successfully creating an organizational unit with set read only attributes and elements via SOAP.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumCou3_soap() throws Exception {

        final String[] parentValues = createSuccessfully("escidoc_ou_create.xml", 2);

        final Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_ORGANIZATIONAL_UNIT_PATH, "escidoc_ou_create_soap_read_only.xml");
        setUniqueValue(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE);
        insertParentsElement(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS, parentValues, false);

        String toBeCreatedXml = toString(toBeCreatedDocument, false);

        String createdXml = null;
        try {
            createdXml = create(toBeCreatedXml);
        }
        catch (final Exception e) {
            failException("Creating OU with set read only values failed. ", e);
        }
        assertOrganizationalUnit(createdXml, toBeCreatedXml, startTimestamp, startTimestamp, false, false);

    }

    /**
     * Test declining creating an organizational unit setting a forbidden attribute (organizational-unit/@href).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumCou6a_soap() throws Exception {

        Class ec = XmlSchemaValidationException.class;

        final Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_ORGANIZATIONAL_UNIT_PATH, "escidoc_ou_create.xml");
        setUniqueValue(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE);
        addAttribute(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT, createAttributeNode(toBeCreatedDocument,
            XLINK_NS_URI, XLINK_PREFIX_TEMPLATES, NAME_HREF, "/oum/organizational-unit/escidoc:41414"));

        try {
            create(toString(toBeCreatedDocument, false));
            failMissingException(ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining creating an organizational unit setting a forbidden element (organizational-unit/resources).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumCou6b_soap() throws Exception {

        Class ec = XmlSchemaValidationException.class;

        Map elements = new HashMap();
        elements.put(XPATH_ORGANIZATIONAL_UNIT_RESOURCES, "");

        final String toBeCreatedXml = getOrganizationalUnitTemplateWithReadOnlyElements(elements);

        try {
            create(toBeCreatedXml);
            failMissingException(ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining creating an organizational unit setting a forbidden attribute (/organizational-unit/xml:base).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumCou6c_soap() throws Exception {

        Class ec = XmlSchemaValidationException.class;

        Map elements = new HashMap();
        elements.put(XPATH_ORGANIZATIONAL_UNIT_XML_BASE, "http://www.escidoc.de");

        final String toBeCreatedXml = getOrganizationalUnitTemplateWithReadOnlyElements(elements);

        try {
            create(toBeCreatedXml);
            failMissingException(ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining creating an organizational unit setting a forbidden attribute (/organizational-unit/xlink:title).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumCou6d_soap() throws Exception {

        Class ec = XmlSchemaValidationException.class;
        Map elements = new HashMap();
        elements.put(XPATH_ORGANIZATIONAL_UNIT_XLINK_TITLE, "Organizational Unit Title");

        final String toBeCreatedXml = getOrganizationalUnitTemplateWithReadOnlyElements(elements);
        try {
            create(toBeCreatedXml);
            failMissingException(ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Get a organizational unit template for creation containing some read only elements. The wanted read only elements
     * (depicted by their xpath, e.g. /organizational-unit/creation-date) are the keys in the elements map. If the
     * stored value equals the empty string the element is left as it is in the template, otherwise its value is
     * substituted by the stored value.
     *
     * @param expected The elements Map.
     * @return The resulting template.
     * @throws Exception If anything fails.
     */
    protected String getOrganizationalUnitTemplateWithReadOnlyElements(final Map expected) throws Exception {
        Node template = getTemplateAsDocument(TEMPLATE_ORGANIZATIONAL_UNIT_PATH, "escidoc_ou_read_only_elements.xml");
        setUniqueValue(template, XPATH_ORGANIZATIONAL_UNIT_TITLE);

        template = changeTemplateWithReadOnly(template, expected, XPATH_ORGANIZATIONAL_UNIT_OBJID);
        template = changeTemplateWithReadOnly(template, expected, XPATH_ORGANIZATIONAL_UNIT_XLINK_HREF);
        template = changeTemplateWithReadOnly(template, expected, XPATH_ORGANIZATIONAL_UNIT_XLINK_TITLE);
        template = changeTemplateWithReadOnly(template, expected, XPATH_ORGANIZATIONAL_UNIT_XLINK_TYPE);
        template = changeTemplateWithReadOnly(template, expected, XPATH_ORGANIZATIONAL_UNIT_XML_BASE);
        template = changeTemplateWithReadOnly(template, expected, XPATH_ORGANIZATIONAL_UNIT_LAST_MODIFICATION_DATE);
        template = changeTemplateWithReadOnly(template, expected, XPATH_ORGANIZATIONAL_UNIT_IDENTIFIER);
        template = changeTemplateWithReadOnly(template, expected, XPATH_ORGANIZATIONAL_UNIT_RESOURCES);
        template = changeTemplateWithReadOnly(template, expected, XPATH_ORGANIZATIONAL_UNIT_PUBLIC_STATUS);
        template = changeTemplateWithReadOnly(template, expected, XPATH_ORGANIZATIONAL_UNIT_CREATION_DATE);
        template = changeTemplateWithReadOnly(template, expected, XPATH_ORGANIZATIONAL_UNIT_CREATED_BY);
        template = changeTemplateWithReadOnly(template, expected, XPATH_ORGANIZATIONAL_UNIT_MODIFIED_BY);
        return toString(template, false);
    }

}
