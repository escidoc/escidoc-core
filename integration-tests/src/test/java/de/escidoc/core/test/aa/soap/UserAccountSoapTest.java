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
package de.escidoc.core.test.aa.soap;

import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.aa.UserAccountTest;
import de.escidoc.core.test.common.client.servlet.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.w3c.dom.Document;

import static org.junit.Assert.assertEquals;

/**
 * Testsuite for the UserAccount with SOAP transport.
 * 
 * @author TTE
 * 
 */
@RunWith(JUnit4.class)
public class UserAccountSoapTest extends UserAccountTest {

    /**
     * Constructor.
     *
     * @throws Exception e
     */
    public UserAccountSoapTest() throws Exception {
        super(Constants.TRANSPORT_SOAP);
    }

    /**
     * Test successful creation of user account with set read-only values.
     * 
     * @test.name Create User Account - Read Only Values - SOAP
     * @test.id AA_CUA-10-soap
     * @test.input: Valid user account XML representation for creating a new
     *              user account is provided. Read only values are specified
     *              within the data.
     * @test.expected: User account is returned with additional data like xlink
     *                 attributes and last-modification-date attributes,
     *                 creation-date, creator.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACua10_soap() throws Exception {

        Document createdDocument = createSuccessfully(
                "escidoc_useraccount_for_create_soap_read_only.xml");

        assertEquals(
            "Creation date and last modification date are different. ",
            assertCreationDateExists("", createdDocument),
            getLastModificationDateValue(createdDocument));

        final String objid = getObjidValue(createdDocument);
        Document retrievedDocument = retrieveSuccessfully(objid);
        assertXmlEquals("Retrieved document not the same like the created one",
            createdDocument, retrievedDocument);
    }

    /**
     * Test successful update of user account with changed read-only values.
     * 
     * @test.name Update User Account - Read Only Values - SOAP
     * @test.id AA_UUA-7-soap
     * @test.input: Valid user account XML representation for updating an
     *              existing user account is provided. Changed read only values
     *              are specified within the data.
     * @test.expected: User account is returned with updated data like xlink
     *                 attributes and last-modification-date attributes.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUua7_soap() throws Exception {

        Document createdDocument =
            createSuccessfully("escidoc_useraccount_for_create.xml");
        final String id = getObjidValue(createdDocument);
        final String createdXml = toString(createdDocument, false);

        // root attributes
        final Document toBeUpdatedDocument =
            (Document) substitute(createdDocument, XPATH_USER_ACCOUNT_OBJID,
                "some:id");

        // creation-date
        substitute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_CREATION_DATE,
            getNowAsTimestamp());

        // created-by
        substitute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_CREATED_BY_OBJID,
            "some:id");

        // modified-by
        substitute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_MODIFIED_BY_OBJID,
            "some:id");

        // active
        substitute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_ACTIVE, "false");

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
        final String beforeUpdateTimestamp = getNowAsTimestamp();

        String updatedXml = null;
        try {
            updatedXml = update(id, toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Updating with changed read only values failed. ", e);
        }
        final Document updatedDocument =
            assertUserAccount(updatedXml, createdXml, startTimestamp,
                beforeUpdateTimestamp, true);

        final Document retrievedDocument = retrieveSuccessfully(id);
        assertXmlEquals("Retrieved document not the same like the created one",
            updatedDocument, retrievedDocument);
    }

    /**
     * Test successful update of user account without read-only values.
     * 
     * @test.name Update User Account - No Read Only Values - SOAP
     * @test.id AA_UUA-10-soap
     * @test.input: Valid user account XML representation for updating an
     *              existing user account is provided. Changed read only values
     *              are specified within the data.
     * @test.expected: User account is returned with updated data like xlink
     *                 attributes and last-modification-date attributes.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUua10_soap() throws Exception {

        Document createdDocument =
            createSuccessfully("escidoc_useraccount_for_create.xml");
        final String id = getObjidValue(createdDocument);
        final String createdXml = toString(createdDocument, false);

        // root attributes
        final Document toBeUpdatedDocument =
            (Document) deleteAttribute(createdDocument,
                XPATH_USER_ACCOUNT_OBJID);

        // creation-date
        deleteNodes(toBeUpdatedDocument, XPATH_USER_ACCOUNT_CREATION_DATE);

        // created-by
        deleteNodes(toBeUpdatedDocument, XPATH_USER_ACCOUNT_CREATED_BY);

        // modified-by
        deleteNodes(toBeUpdatedDocument, XPATH_USER_ACCOUNT_MODIFIED_BY);

        // active
        deleteNodes(toBeUpdatedDocument, XPATH_USER_ACCOUNT_ACTIVE);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
        final String beforeUpdateTimestamp = getNowAsTimestamp();

        String updatedXml = null;
        try {
            updatedXml = update(id, toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Updating with changed read only values failed. ", e);
        }
        final Document updatedDocument =
            assertUserAccount(updatedXml, createdXml, startTimestamp,
                beforeUpdateTimestamp, true);

        final Document retrievedDocument = retrieveSuccessfully(id);
        assertXmlEquals("Retrieved document not the same like the created one",
            updatedDocument, retrievedDocument);
    }

    /**
     * Test declining updating an UserAccount without providing modified-by
     * objid.
     * 
     * @test.name Update UserAccount - Missing Objid Attribute of Modified-by -
     *            SOAP
     * @test.id AA_UUA-11-4-soap
     * @test.input:
     *          <ul>
     *          <li>id of an existing role</li>
     *          <li>UserAccount XML representation without objid attribute in
     *          modified-by element.</li>
     *          </ul>
     * @test.expected: XmlSchemaValidationException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUua11_4_soap() throws Exception {

        Document toBeUpdatedDocument =
            createSuccessfully("escidoc_useraccount_for_create.xml");
        final String createdXml = toString(toBeUpdatedDocument, false);
        deleteAttribute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_MODIFIED_BY,
            NAME_OBJID);
        final String id = getObjidValue(toBeUpdatedDocument);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
        final String beforeUpdateTimestamp = getNowAsTimestamp();

        String updatedXml = null;
        try {
            updatedXml = update(id, toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Updating with changed read only values failed. ", e);
        }
        final Document updatedDocument =
            assertUserAccount(updatedXml, createdXml, startTimestamp,
                beforeUpdateTimestamp, true);

        Document retrievedDocument = retrieveSuccessfully(id);
        assertXmlEquals("Retrieved document not the same like the created one",
            updatedDocument, retrievedDocument);

    }

}
