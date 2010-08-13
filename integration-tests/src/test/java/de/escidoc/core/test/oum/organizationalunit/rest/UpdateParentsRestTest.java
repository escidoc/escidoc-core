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

import org.w3c.dom.Document;

import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.oum.organizationalunit.UpdateParentsTest;

/**
 * Organizational Unit tests with REST transport.
 * 
 * @author MSC
 * 
 */
public class UpdateParentsRestTest extends UpdateParentsTest {

    /**
     * Constructor.
     * 
     */
    public UpdateParentsRestTest() {
        super(Constants.TRANSPORT_REST);
    }

    /**
     * Test successfully updating the organization-details sub resource of an
     * REST.
     * 
     * @test.name Update Organizational Unit - Read Only Values - REST
     * @test.id OUM-UOD-1-1-REST
     * @test.input Organizational Details XML representation with changed read
     *             only attributes and elements.
     * @test.expected: Xml representation of successfully updated organization
     *                 details.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void notest_OUM_UOD_1_1_REST() throws Exception {

        final String[] parentValues =
            createSuccessfully("escidoc_ou_create.xml", 2);

        final String createdXml =
            createSuccessfullyChild("escidoc_ou_create.xml", new String[] {
                parentValues[0], parentValues[1] });
        final Document createdDocument = getDocument(createdXml);
        final String objid = getObjidValue(createdDocument);
        final String organizationDetails = retrieveMdRecords(objid);
        final Document toBeUpdatedDocument = getDocument(organizationDetails);

        // organization details xlink
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATION_MD_RECORDS
            + PART_XLINK_HREF, "Some Href");
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATION_MD_RECORDS
            + PART_XLINK_TITLE, "Some Title");
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATION_MD_RECORDS
            + PART_XLINK_TYPE, "none");

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, true);

        String updatedXml = null;
        try {
            updatedXml = updateMdRecords(objid, toBeUpdatedXml);
        }
        catch (Exception e) {
            failException("Updating OU with changed read only values failed. ",
                e);
        }
        assertEscidocMdRecord(objid, getDocument(updatedXml), createdDocument,
            startTimestamp);

    }

    /**
     * Test successfully updating an organizational unit without read only
     * attributes and elements via REST.
     * 
     * @test.name Update Organizational Unit - Without Read Only Values - REST
     * @test.id OUM_UOD-1-2-REST
     * @test.input Organizational Details XML representation without read only
     *             attributes and elements.
     * @test.expected: Xml representation of successfully updated organizational
     *                 unit.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void notest_OUM_UOD_1_2_REST() throws Exception {

        final String[] parentValues =
            createSuccessfully("escidoc_ou_create.xml", 2);

        final String createdXml =
            createSuccessfullyChild("escidoc_ou_create.xml", new String[] {
                parentValues[0], parentValues[1] });
        final Document createdDocument = getDocument(createdXml);
        final String objid = getObjidValue(createdDocument);
        final String organizationDetails = retrieveMdRecords(objid);
        final Document toBeUpdatedDocument = getDocument(organizationDetails);

        // root attributes

        // organization details xlink
        deleteAttribute(toBeUpdatedDocument, XPATH_ORGANIZATION_MD_RECORDS
            + PART_XLINK_HREF);
        deleteAttribute(toBeUpdatedDocument, XPATH_ORGANIZATION_MD_RECORDS
            + PART_XLINK_TITLE);
        deleteAttribute(toBeUpdatedDocument, XPATH_ORGANIZATION_MD_RECORDS
            + PART_XLINK_TYPE);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, true);

        String updatedXml = null;
        try {
            updatedXml = updateMdRecords(objid, toBeUpdatedXml);
        }
        catch (Exception e) {
            failException("Updating OU without read only values failed. ", e);
        }
        assertEscidocMdRecord(objid, getDocument(updatedXml), createdDocument,
            startTimestamp);
    }
}
