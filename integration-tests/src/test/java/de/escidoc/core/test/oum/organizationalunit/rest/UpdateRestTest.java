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

import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.oum.organizationalunit.OrganizationalUnitTestBase;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Organizational Unit tests with REST transport.
 * 
 * @author Michael Schwantner
 * 
 */
public class UpdateRestTest extends OrganizationalUnitTestBase {

    /**
     * Constructor.
     * 
     */
    public UpdateRestTest() {
        super(Constants.TRANSPORT_REST);
    }

    /**
     * Test successfully updating an organizational unit with changed read only
     * attributes and elements via REST.
     * 
     * @test.name Update Organizational Unit - Read Only Values - REST
     * @test.id OUM_UOU-3-rest
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
    public void testOumUou3_rest() throws Exception {

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
                XPATH_ORGANIZATIONAL_UNIT_XLINK_HREF, "Some Href");
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_XLINK_TITLE,
            "Some Title");
        // substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_XLINK_TYPE,
        // "none");
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_XML_BASE,
            "http://some.base.uri");

        // resources xlink
        substitute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_RESOURCES_XLINK_HREF, "Some Href");
        substitute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_RESOURCES_XLINK_TITLE, "Some Title");
        // substitute(createdDocument,
        // XPATH_ORGANIZATIONAL_UNIT_RESOURCES_XLINK_TYPE, "none");

        substitute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PATH_LIST_XLINK_HREF,
            "Some Href");
        substitute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PATH_LIST_XLINK_TITLE,
            "Some Title");
        // substitute(createdDocument,
        // XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PATH_LIST_XLINK_TYPE, "none");

        substitute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PARENT_OBJECTS_XLINK_HREF,
            "Some Href");
        substitute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PARENT_OBJECTS_XLINK_TITLE,
            "Some Title");
        // substitute(createdDocument,
        // XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PARENT_OBJECTS_XLINK_TYPE,
        // "none");

        substitute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_RESOURCE_CHILD_OBJECTS_XLINK_HREF,
            "Some Href");
        substitute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_RESOURCE_CHILD_OBJECTS_XLINK_TITLE,
            "Some Title");
        // substitute(createdDocument,
        // XPATH_ORGANIZATIONAL_UNIT_RESOURCE_CHILD_OBJECTS_XLINK_TYPE, "none");

        // properties xlink
        substitute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_PROPERTIES_XLINK_HREF, "Some Href");
        substitute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_PROPERTIES_XLINK_TITLE, "Some Title");
        // substitute(createdDocument,
        // XPATH_ORGANIZATIONAL_UNIT_PROPERTIES_XLINK_TYPE, "none");

        // creation-date
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_CREATION_DATE,
            getNowAsTimestamp());

        // created-by
        substitute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_CREATED_BY_XLINK_HREF, "Some Href");
        substitute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_CREATED_BY_XLINK_TITLE, "Some Title");
        // substitute(createdDocument,
        // XPATH_ORGANIZATIONAL_UNIT_CREATED_BY_XLINK_TYPE, "none");

        // modified-by
        substitute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_MODIFIED_BY_XLINK_HREF, "Some Href");
        substitute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_MODIFIED_BY_XLINK_TITLE, "Some Title");
        // substitute(createdDocument,
        // XPATH_ORGANIZATIONAL_UNIT_MODIFIED_BY_XLINK_TYPE, "none");

        // public status cannot be changed as "opened" is currently the one and
        // only allowed value.

        // has-children
        substitute(createdDocument,
            EscidocRestSoapTestBase.XPATH_ORGANIZATIONAL_UNIT_HAS_CHILDREN,
            "true");

        // data xlink
        substitute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS_XLINK_HREF, "Some Href");
        substitute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS_XLINK_TITLE, "Some Title");
        // substitute(createdDocument,
        // XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS_XLINK_TYPE, "none");

        // parent-ous xlink
        substitute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_PARENTS_XLINK_HREF, "Some Href");
        substitute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_PARENTS_XLINK_TITLE, "Some Title");
        // substitute(createdDocument,
        // XPATH_ORGANIZATIONAL_UNIT_PARENTS_XLINK_TYPE, "none");

        // parent-ou xlink
        // substitute(createdDocument,
        // XPATH_ORGANIZATIONAL_UNIT_PARENT_XLINK_TYPE, "none");

        // final String toBeUpdatedXml = toString(toBeUpdatedDocument, true);
        //
        // String updatedXml = null;
        // try {
        // updatedXml = update(objid, toBeUpdatedXml);
        // }
        // catch (final Exception e) {
        // failException("Creating OU with set read only values failed. ", e);
        // }
        // assertOrganizationalUnit(updatedXml, createdXml, startTimestamp,
        // startTimestamp, true, false);

    }

    /**
     * Test successfully updating an organizational unit without read only
     * attributes and elements via REST.
     * 
     * @test.name Update Organizational Unit - Without Read Only Values - REST
     * @test.id OUM_UOU-4-rest
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
    public void testOumUou4_rest() throws Exception {

        final String[] parentValues =
            createSuccessfully("escidoc_ou_create.xml", 2);

        final String createdXml =
            createSuccessfullyChild("escidoc_ou_create.xml", new String[] {
                parentValues[0], parentValues[1] });
        Document createdDocument = getDocument(createdXml);
        final String objid = getObjidValue(createdDocument);

        // root attributes
        final Document toBeUpdatedDocument =
            (Document) deleteNodes(createdDocument,
                XPATH_ORGANIZATIONAL_UNIT_XLINK_HREF);
        deleteAttribute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_XLINK_TITLE);
        deleteAttribute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_XLINK_TYPE);
        deleteAttribute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_XML_BASE);

        // resources, deleted
        deleteNodes(createdDocument, XPATH_ORGANIZATIONAL_UNIT_RESOURCES);

        // properties xlink
        deleteNodes(createdDocument, XPATH_ORGANIZATIONAL_UNIT_PROPERTIES);

        // data xlink
        deleteAttribute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS_XLINK_HREF);
        deleteAttribute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS_XLINK_TITLE);
        deleteAttribute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS_XLINK_TYPE);

        // parent-ous xlink
        deleteAttribute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_PARENTS_XLINK_HREF);
        deleteAttribute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_PARENTS_XLINK_TITLE);
        deleteAttribute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_PARENTS_XLINK_TYPE);

        // parent-ou xlink
        deleteAttribute(createdDocument,
            XPATH_ORGANIZATIONAL_UNIT_PARENT_XLINK_TYPE);

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
