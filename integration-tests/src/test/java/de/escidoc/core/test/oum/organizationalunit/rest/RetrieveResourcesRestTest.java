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

import org.junit.Test;

import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.oum.organizationalunit.OrganizationalUnitTestBase;

/**
 * 
 * Test retrieve of Organizational Unit via REST.
 * 
 */
public class RetrieveResourcesRestTest extends OrganizationalUnitTestBase {

    /**
     * Constructor.
     * 
     */
    public RetrieveResourcesRestTest() {
        super(Constants.TRANSPORT_REST);
    }

    /**
     * Test retrieving the list of virtual resources of an organizational unit.
     * 
     * @test.name Retrieve Resources of Organizational Unit - Success
     * @test.id OUM_RVR-1
     * @test.input <ul>
     *             <li>Id of existing organizational unit.</li>
     *             </ul>
     * @test.expected: XML representation of the list of virtual resources
     *                 organizational unit.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumRvr1() throws Exception {

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String objid = getObjidValue(getDocument(createdXml));

        String retrievedXml = null;
        try {
            retrievedXml = retrieveResources(objid);
        }
        catch (Exception e) {
            failException(
                "Retrieving list of resources of existing OU failed.", e);
        }
        assertXmlValidOrganizationalUnit(retrievedXml);
        Document retrievedDocument = getDocument(retrievedXml);

        assertXlinkElementWithoutObjid("Invalid resources element.",
            retrievedDocument, "/resources",
            Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/" + objid + "/resources");
        assertXlinkElementWithoutObjid(
            "Invalid resources/parent-objects element.", retrievedDocument,
            "/resources/parent-objects", Constants.ORGANIZATIONAL_UNIT_BASE_URI
                + "/" + objid + "/resources/parent-objects");
        assertXlinkElementWithoutObjid(
            "Invalid resources/child-objects element.", retrievedDocument,
            "/resources/child-objects", Constants.ORGANIZATIONAL_UNIT_BASE_URI
                + "/" + objid + "/resources/child-objects");
        assertXlinkElementWithoutObjid("Invalid resources/path-list element.",
            retrievedDocument, "/resources/path-list",
            Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/" + objid
                + "/resources/path-list");
        assertXlinkElementWithoutObjid(
            "Invalid resources/successor-objects element.", retrievedDocument,
            "/resources/successor-objects",
            Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/" + objid
                + "/resources/successors");
        assertXlinkElementWithoutObjid(
            "Invalid resources/relations element.", retrievedDocument,
            "/resources/relations",
            Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/" + objid
                + "/resources/relations");

        assertXmlNotExists("Unexpected 6th virtual resource element.",
            retrievedDocument, "/resources/*[6]");
    }

    /**
     * Test declining retrieving resources of organizational unit with providing
     * unknown id.
     * 
     * @test.name Retrieve Resources of Organizational Unit - Unknown Id
     * @test.id OUM_RVR-2
     * @test.input <ul>
     *             <li>Unknown id</li>
     *             </ul>
     * @test.expected: OrganizationalUnitNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumRvr2() throws Exception {

        Class<?> ec = OrganizationalUnitNotFoundException.class;
        try {
            retrieveResources(UNKNOWN_ID);
            failMissingException(
                "Retrieving of resources of OU with providing unknown id"
                    + " has not been declined.", ec);
        }
        catch (Exception e) {
            assertExceptionType(
                "Retrieving of resources of OU with providing unknown id"
                    + " has not been declined, correctly.", ec, e);
        }
    }

    /**
     * Test declining retrieving resources of organizational unit with providing
     * id of existing resource of another resource type.
     * 
     * @test.name Retrieve Resources of Organizational Unit - Id of Another
     *            Resource Type
     * @test.id OUM_RVR-2-2
     * @test.input <ul>
     *             <li>Id of a resource of another type</li>
     *             </ul>
     * @test.expected: OrganizationalUnitNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumRvr2_2() throws Exception {

        Class<?> ec = OrganizationalUnitNotFoundException.class;
        try {
            retrieveResources(CONTEXT_ID);
            failMissingException(
                "Retrieving of resources of OU with providing id"
                    + " of resource of another resource type"
                    + " has not been declined.", ec);
        }
        catch (Exception e) {
            assertExceptionType(
                "Retrieving of resources of OU with providing id"
                    + " of resource of another resource type"
                    + " has not been declined, correctly.", ec, e);
        }
    }

    /**
     * Test declining retrieving resources of organizational unit without
     * providing id.
     * 
     * @test.name Retrieve Resources of Organizational Unit - Missing Id
     * @test.id OUM_RVR-3
     * @test.input <ul>
     *             <li>No id is provided</li>
     *             </ul>
     * @test.expected: OrganizationalUnitNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumRvr3() throws Exception {

        Class<?> ec = MissingMethodParameterException.class;
        try {
            retrieveResources(null);
            failMissingException(
                "Retrieving of resources of OU without providing id"
                    + " has not been declined.", ec);
        }
        catch (Exception e) {
            assertExceptionType(
                "Retrieving of resources of OU without providing id"
                    + " has not been declined, correctly.", ec, e);
        }

    }

}
