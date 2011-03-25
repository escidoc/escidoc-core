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

import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.oum.organizationalunit.OrganizationalUnitTestBase;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Organizational Unit tests with Soap transport.
 * 
 * @author Michael Schneider
 * 
 */
public class UpdateSoapTest extends OrganizationalUnitTestBase {

    /**
     * Constructor.
     * 
     */
    public UpdateSoapTest() {
        super(Constants.TRANSPORT_SOAP);
    }

    /**
     * Test successfully updating an organizational unit with changed read only
     * attributes and elements via SOAP.
     * 
     * @test.name Update Organizational Unit - Read Only Values - SOAP
     * @test.id OUM_UOU-3-soap
     * @test.input Organizational Unit XML representation with changed read only
     *             attributes and elements.
     * @test.expected: Xml representation of successfully updated organizational
     *                 unit.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUou3_soap() throws Exception {

        final String[] parentValues =
            createSuccessfully("escidoc_ou_create.xml", 2);

        final String createdXml =
            createSuccessfullyChild("escidoc_ou_create.xml", new String[] {
                parentValues[0], parentValues[1] });
        final Document createdDocument = getDocument(createdXml);
        final String objid = getObjidValue(createdDocument);

        // root attributes
        final Document toBeUpdatedDocument =
            (Document) substitute(createdDocument,
                XPATH_ORGANIZATIONAL_UNIT_OBJID, "Some:Objid");

        // creation-date
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_CREATION_DATE,
            getNowAsTimestamp());

        // created-by
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_CREATED_BY_OBJID,
            "Some:Objid");

        // modified-by
        substitute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_MODIFIED_BY_OBJID, "Some:Objid");

        // public status
        // deleted, value cannot be changes as "opened" is currently the one and
        // only allowed value.
        deleteElement(createdDocument, XPATH_ORGANIZATIONAL_UNIT_PUBLIC_STATUS);

        // has-children
        substitute(createdDocument,
            EscidocRestSoapTestBase.XPATH_ORGANIZATIONAL_UNIT_HAS_CHILDREN, "true");

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, true);

        String updatedXml = null;
        try {
            updatedXml = update(objid, toBeUpdatedXml);
        }
        catch (final Exception e) {
            failException("Creating OU with set read only values failed. ", e);
        }
        assertOrganizationalUnit(updatedXml, createdXml, startTimestamp,
            startTimestamp, true, false);

    }

    /**
     * Test successfully updating an organizational unit without read only
     * attributes and elements via SOAP.
     * 
     * @test.name Update Organizational Unit - Without Read Only Values - SOAP
     * @test.id OUM_UOU-4-soap
     * @test.input Organizational Unit XML representation without read only
     *             attributes and elements.
     * @test.expected: Xml representation of successfully updated organizational
     *                 unit.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUou4_soap() throws Exception {

        final String[] parentValues =
            createSuccessfully("escidoc_ou_create.xml", 2);

        final String createdXml =
            createSuccessfullyChild("escidoc_ou_create.xml", new String[] {
                parentValues[0], parentValues[1] });
        final Document createdDocument = getDocument(createdXml);
        final String objid = getObjidValue(createdDocument);

        // root attributes
        final Document toBeUpdatedDocument =
            (Document) deleteAttribute(createdDocument,
                XPATH_ORGANIZATIONAL_UNIT_OBJID);

        // properties
        deleteElement(createdDocument, XPATH_ORGANIZATIONAL_UNIT_PROPERTIES);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, true);

        String updatedXml = null;
        try {
            updatedXml = update(objid, toBeUpdatedXml);
        }
        catch (final Exception e) {
            failException("Creating OU with set read only values failed. ", e);
        }
        assertOrganizationalUnit(updatedXml, createdXml, startTimestamp,
            startTimestamp, true, false);

    }

}
