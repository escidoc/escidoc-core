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
package de.escidoc.core.test.oum.organizationalunit.rest;

import de.escidoc.core.common.exceptions.remote.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.oum.organizationalunit.OrganizationalUnitTestBase;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Organizational Unit tests with REST transport.
 *
 * @author Michael Schneider
 */
public class CreateRestTest extends OrganizationalUnitTestBase {

    /**
     * Constructor.
     */
    public CreateRestTest() {
        super(Constants.TRANSPORT_REST);
    }

    /**
     * Test successfully creating an organizational unit with set read only attributes and elements via REST.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumCou3_rest() throws Exception {

        final String[] parentValues = createSuccessfully("escidoc_ou_create.xml", 2);

        final Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_ORGANIZATIONAL_UNIT_PATH, "escidoc_ou_create_rest_read_only.xml");
        setUniqueValue(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE);
        insertParentsElement(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS, parentValues, true);

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
     * Test declining creating an organizational unit setting a forbidden attribute (organizational-unit/@objid).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumCou6a_rest() throws Exception {

        Class ec = XmlSchemaValidationException.class;

        final Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_ORGANIZATIONAL_UNIT_PATH, "escidoc_ou_create.xml");
        setUniqueValue(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE);
        addAttribute(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT, createAttributeNode(toBeCreatedDocument, null,
            null, NAME_OBJID, "escidoc:41414"));

        try {
            create(toString(toBeCreatedDocument, false));
            failMissingException(ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

}
