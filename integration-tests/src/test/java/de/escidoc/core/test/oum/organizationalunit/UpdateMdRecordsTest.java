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
package de.escidoc.core.test.oum.organizationalunit;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import org.w3c.dom.Document;

import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.remote.application.violated.OrganizationalUnitNameNotUniqueException;
import de.escidoc.core.test.common.fedora.TripleStoreTestsBase;

/**
 * Test the mock implementation of the item resource.
 * 
 * @author MSC
 * 
 * 
 */
@RunWith(value = Parameterized.class)
public class UpdateMdRecordsTest extends OrganizationalUnitTestBase {

    /**
     * @param transport
     *            The transport identifier.
     */
    public UpdateMdRecordsTest(final int transport) {
        super(transport);
    }

    /**
     * Test successfully updating the organization-details sub resource of an
     * organizational.
     * 
     * @test.name Update Organizational Details - organization-details
     * @test.id OUM_UMS-1-a
     * @test.input Organizational Details XML representation with updated data
     *             section.
     * @test.expected: Xml representation of updated organizational unit.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUms1a() throws Exception {

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document createdDocument = getDocument(createdXml);
        final String id = getObjidValue(createdDocument);

        String mdRecords = retrieveMdRecords(id);
        Document toBeUpdatedDocument = getDocument(mdRecords);
        String beforeUpdateTimestamp = getNowAsTimestamp();
        changeMdRecordsValues(XPATH_ORGANIZATIONAL_UNIT
            + XPATH_MD_RECORDS_ESCIDOC_MD_RECORD, createdDocument,
            beforeUpdateTimestamp);

        changeMdRecordsValues(XPATH_MD_RECORDS_ESCIDOC_MD_RECORD,
            toBeUpdatedDocument, beforeUpdateTimestamp);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        String updatedXml = null;
        try {
            updatedXml = updateMdRecords(id, toBeUpdatedXml);
        }
        catch (Exception e) {
            failException("Updating OU failed with exception. ", e);
        }
        assertEscidocMdRecord(id, getDocument(updatedXml), createdDocument,
            beforeUpdateTimestamp);
        // assertOrganizationalUnit(updatedXml, toBeUpdatedXml, startTimestamp,
        // beforeUpdateTimestamp);
    }

    private Document changeMdRecordsValues(
        final String xpathToEscidocMdRecord, final Document document,
        final String uniqueNamePart) throws Exception {

        // substitute(document, xpathToDetails + "/" + NAME_ABBREVIATION,
        // "NewAbbr");

        substitute(document, xpathToEscidocMdRecord + "/" + NAME_TITLE,
            selectSingleNode(document,
                xpathToEscidocMdRecord + "/" + NAME_TITLE).getTextContent()
                + "_" + uniqueNamePart);
        // substitute(document, xpathToEscidocMdRecord + "/" + NAME_URI,
        // "http://new.uri");
        substitute(document, xpathToEscidocMdRecord + "/"
            + NAME_ORGANIZATION_TYPE, "NewType");
        substitute(document, xpathToEscidocMdRecord + "/" + NAME_DESCRIPTION,
            "NewDescription");
        // // external-id must not be changed
        // // postcode
        // substitute(document, xpathToEscidocMdRecord + "/" + NAME_POSTCODE,
        // "NewPostCode");
        substitute(document, xpathToEscidocMdRecord + "/" + NAME_COUNTRY, "NC");
        // // region
        // substitute(document, xpathToEscidocMdRecord + "/" + NAME_REGION,
        // "NewRegion");
        // // address
        // substitute(document, xpathToEscidocMdRecord + "/" + NAME_START_DATE,
        // "NewAddress");
        // city
        substitute(document, xpathToEscidocMdRecord + "/" + NAME_CITY,
            "New City");
        // telephone
        // substitute(document, xpathToEscidocMdRecord + "/" + NAME_TELEPHONE,
        // "424242");
        // // fax
        // substitute(document, xpathToEscidocMdRecord + "/" + NAME_FAX,
        // "424242");

        // start-date
        substitute(document, xpathToEscidocMdRecord + "/" + NAME_START_DATE,
            startTimestamp);
        // end-date
        substitute(document, xpathToEscidocMdRecord + "/" + NAME_END_DATE,
            getNowAsTimestamp());
        // // longitude
        // substitute(document, xpathToEscidocMdRecord + "/" +
        // NAME_GEO_COORDINATE
        // + "/" + NAME_LOCATION_LONGITUDE, "1111");
        // // latitude
        // substitute(document, xpathToEscidocMdRecord + "/" +
        // NAME_GEO_COORDINATE
        // + "/" + NAME_LOCATION_LATITUDE, "1111");
        return document;
    }

//    /**
//     * Tests successfully updating the organization-details sub resource of an
//     * organizational unit with setting an external id that has not been set
//     * before.
//     * 
//     * @test.name Update Organizational Details - New External Id
//     * @test.id OUM_UMS-1-2
//     * @test.input Organizational Details XML representation with set
//     *             external-id (new)
//     * @test.expected: Exception
//     * @test.status Revoked - no more requirements for external id handling at
//     *              the moment
//     * 
//     * @throws Exception
//     */
    // public void testOumUms1_2() throws Exception {
    //
    // final String createdXml = createSuccessfully("escidoc_ou_create.xml");
    // final String id = getObjidValue(createdXml);
    // final String organizationDetails = retrieveOrganizationDetails(id);
    // final Document toBeUpdatedDocument = getDocument(organizationDetails);
    //
    // final String externalId = "12345";
    // // insert external-id
    // addAfter(toBeUpdatedDocument, XPATH_MD_RECORDS_ESCIDOC_MD_RECORD + "/"
    // + NAME_DESCRIPTION, createElementNode(toBeUpdatedDocument,
    // ORGANIZATIONAL_UNIT_NS_URI, ORGANIZATIONAL_UNIT_PREFIX_ESCIDOC,
    // NAME_IDENTIFIER, externalId));
    //
    // final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
    //
    // String beforeUpdateTimestamp = getNowAsTimestamp();
    // String updatedXml = null;
    // try {
    // updatedXml = updateOrganizationDetails(id, toBeUpdatedXml);
    // }
    // catch (Exception e) {
    // failException(
    // "Updating Organization Details with set new external id failed with
    // exception. ",
    // e);
    // }
    // Document createdDocument = getDocument(createdXml);
    // addAfter(
    // createdDocument,
    // XPATH_ORGANIZATIONAL_UNIT_DESCRIPTION,
    // createElementNode(createdDocument, ORGANIZATIONAL_UNIT_NS_URI,
    // ORGANIZATIONAL_UNIT_PREFIX_ESCIDOC, NAME_IDENTIFIER, externalId));
    // assertEscidocMdRecord(id, getDocument(updatedXml),
    // getDocument(toString(createdDocument, false)),
    // beforeUpdateTimestamp);
    // }
    /**
     * Tests successfully updating the organization-details sub resource of an
     * organizational unit with setting a new name.
     * 
     * @test.name Update Organizational Details - Change name
     * @test.id OUM_UMS-1-c
     * @test.input Organizational Details XML representation with set changed
     *             name
     * @test.expected: Exception
     * @test.status Implemented
     * 
     * @throws Exception
     */
    @Test
    public void testOumUms1c() throws Exception {
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(createdXml);

        final String organizationDetails = retrieveMdRecords(id);
        final Document toBeUpdatedDocument = getDocument(organizationDetails);

        // change name
        final String name = getUniqueName("Neuer Name");
        substitute(toBeUpdatedDocument, XPATH_MD_RECORDS_ESCIDOC_MD_RECORD
            + "/" + NAME_TITLE, name);
        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        String beforeUpdateTimestamp = getNowAsTimestamp();
        String updatedXml = null;
        try {
            updatedXml = updateMdRecords(id, toBeUpdatedXml);
        }
        catch (Exception e) {

            failException(
                "Updating Organization Details with set new external id failed with exception. ",
                e);
        }
        Document createdDocument = getDocument(createdXml);
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE, name);
        assertEscidocMdRecord(id, getDocument(updatedXml),
            getDocument(toString(createdDocument, false)),
            beforeUpdateTimestamp);
    }

    /**
     * Test declining updating the organization-details sub resource of an
     * organizational unit with non existing id.
     * 
     * @test.name Update Organizational Details - Unknown id
     * @test.id OUM_UMS-2-a
     * @test.input
     *          <ul>
     *          <li>Non existing id</li>
     *          <li> Organizational Details XML representation</li>
     *          </ul>
     * @test.expected: OrganizationalUnitNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUms2a() throws Exception {

        final Class<OrganizationalUnitNotFoundException> ec =
            OrganizationalUnitNotFoundException.class;
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(createdXml);
        final String toBeUpdatedXml = retrieveMdRecords(id);
        try {
            updateMdRecords(UNKNOWN_ID, toBeUpdatedXml);

            failMissingException(
                "No exception occured on update of organization-details with non existing id.",
                ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the organization-details sub resource of an
     * organizational with id of a resource of another type.
     * 
     * @test.name Update Organizational Details - Id of another resource type
     * @test.id OUM_UMS-2-b
     * @test.input
     *          <ul>
     *          <li>Existing id of a resource of another resource type</li>
     *          <li>Organizational Details XML representation</li>
     *          </ul>
     * @test.expected: OrganizationalUnitNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUms2b() throws Exception {

        final Class<OrganizationalUnitNotFoundException> ec =
            OrganizationalUnitNotFoundException.class;

        final String ouXml = createSuccessfully("escidoc_ou_create.xml");
        final String toBeUpdatedXml = retrieveMdRecords(getObjidValue(ouXml));

        try {
            updateMdRecords(CONTEXT_ID, toBeUpdatedXml);
            failMissingException(
                "No exception occured on update with id of resoure of"
                    + " another type.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the organization-details sub resource of an
     * organizational with wrong last modification date (too old).
     * 
     * @test.name Update Organizational Details - Optimistic locking error - Too
     *            Old
     * @test.id OUM_UMS-3-a
     * @test.input
     *          <ul>
     *          <li>Valid id of organizational unit</li>
     *          <li>Organizational Details XML representation with set last
     *          modification date that is older than the last modification date
     *          of the organizational unit</li>
     *          </ul>
     * @test.expected: OptimisticLockingException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUms3a() throws Exception {

        Class<OptimisticLockingException> ec = OptimisticLockingException.class;
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(createdXml);
        final String toBeUpdatedXml = retrieveMdRecords(id);

        final Document toBeUpdatedDocument =
            (Document) substitute(getDocument(toBeUpdatedXml),
                XPATH_ORGANIZATION_MD_RECORDS + "/"
                    + PART_LAST_MODIFICATION_DATE, "2005-01-30T11:36:42.015Z");
        try {
            updateMdRecords(id, toString(toBeUpdatedDocument, false));
            failMissingException(
                "No exception occured on update with wrong time stamp.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the organization-details sub resource of an
     * organizational with wrong last modification date (more recent).
     * 
     * @test.name Update Organizational Details - Optimistic locking error -
     *            More recent
     * @test.id OUM_UMS-3-b
     * @test.input
     *          <ul>
     *          <li>Valid id of organizational unit</li>
     *          <li>Organizational Details XML representation with set last
     *          modification date that is more recent than the last modification
     *          date of the organizational unit</li>
     *          </ul>
     * @test.expected: OptimisticLockingException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUms3b() throws Exception {

        Class<OptimisticLockingException> ec = OptimisticLockingException.class;
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(createdXml);
        final String toBeUpdatedXml = retrieveMdRecords(id);

        final Document toBeUpdatedDocument =
            (Document) substitute(getDocument(toBeUpdatedXml),
                XPATH_ORGANIZATION_MD_RECORDS + "/"
                    + PART_LAST_MODIFICATION_DATE, getNowAsTimestamp());

        try {
            updateMdRecords(id, toString(toBeUpdatedDocument, false));
            failMissingException(
                "No exception occured on update with wrong time stamp.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the organization-details sub resource of an
     * organizational with missing id.
     * 
     * @test.name Update Organizational Details - Missing method parameter error -
     *            Missing id
     * @test.id OUM_UMS-4-a
     * @test.input
     *          <ul>
     *          <li>Missing id of organizational unit</li>
     *          </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUms4a() throws Exception {

        final Class<MissingMethodParameterException> ec =
            MissingMethodParameterException.class;

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(createdXml);
        final String toBeUpdatedXml = retrieveMdRecords(id);

        try {
            updateMdRecords(null, toBeUpdatedXml);
            failMissingException(
                "No exception occured on update with missing id.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the organization-details sub resource of an
     * organizational with missing organizational-details xml.
     * 
     * @test.name Update Organizational Details - Missing method parameter error -
     *            Missing organizational-details
     * @test.id OUM_UMS-4-b
     * @test.input
     *          <ul>
     *          <li>Valid id of organizational unit</li>
     *          <li>Missing Organizational Details XML representation</li>
     *          </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUms4b() throws Exception {

        final Class<MissingMethodParameterException> ec =
            MissingMethodParameterException.class;

        final String toBeUpdatedXml =
            createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(toBeUpdatedXml);

        try {
            updateMdRecords(id, null);

            failMissingException(
                "No exception occured on update with missing organization-details xml.",
                ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the organization-details sub resource of an
     * organizational with corrupt xml.
     * 
     * @test.name Update Organizational Details - Corrupt Xml
     * @test.id OUM_UMS-5-a
     * @test.input Corrupted Xml.
     * @test.expected: XmlCorruptedException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUms5a() throws Exception {

        final Class<XmlCorruptedException> ec = XmlCorruptedException.class;

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument = getDocument(createdXml);
        final String id = getObjidValue(toBeUpdatedDocument);
        try {
            updateMdRecords(id, "<org");
            failMissingException(
                "No exception occured on update with corrupted xml.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the md-records sub resource of an organizational
     * with wrong root element (organizational-unit) in xml.
     * 
     * @test.name Update Organizational Details - Invalid Xml
     * @test.id OUM_UMS-5-b
     * @test.input Invalid Xml.
     * @test.expected: InvalidXmlException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUms5b() throws Exception {

        final Class<XmlCorruptedException> ec = XmlCorruptedException.class;

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument = getDocument(createdXml);
        final String id = getObjidValue(toBeUpdatedDocument);
        try {
            updateMdRecords(id, createdXml);
            failMissingException(
                "No exception occured on update with corrupted xml.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the organization-details sub resource of an
     * organizational with wrong root element (<unknown/>) in xml.
     * 
     * @test.name Update Organizational Details - Invalid Xml
     * @test.id OUM_UMS-5-c
     * @test.input Invalid Xml.
     * @test.expected: InvalidXmlException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUms5c() throws Exception {

        final Class<XmlCorruptedException> ec = XmlCorruptedException.class;

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument = getDocument(createdXml);
        final String id = getObjidValue(toBeUpdatedDocument);
        try {
            updateMdRecords(id, "<unknown/>");
            failMissingException(
                "No exception occured on update with corrupted xml.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    // /**
    // * Test declining updating a top level organizational unit with setting a
    // * non unique name.
    // *
    // * @test.name Update Organizational Details - Duplicate Name of Top Level
    // OU
    // * @test.id OUM_UMS-6-a
    // * @test.input Organizational Details XML representation of a top level
    // * Organizational unit containing a name of an organizational
    // * unit that just exists for another top level ou.
    // * @test.expected: OrganizationalUnitNameNotUniqueException
    // * @test.status Implemented
    // *
    // * @throws Exception
    // * If anything fails.
    // */
    // public void testOumUms6a() throws Exception {
    //
    // final Class<OrganizationalUnitNameNotUniqueException> ec =
    // OrganizationalUnitNameNotUniqueException.class;
    //
    // // create first top level ou
    // final String ou1Xml = createSuccessfully("escidoc_ou_create.xml");
    // final String ou1Name =
    // selectSingleNodeAsserted(getDocument(ou1Xml),
    // XPATH_ORGANIZATIONAL_UNIT_TITLE).getTextContent();
    //
    // // create second top level ou
    // final String ou2Xml = createSuccessfully("escidoc_ou_create.xml");
    //
    // // update name of second top level ou to name of first top level ou
    //
    // final String ou2Id = getObjidValue(getDocument(ou2Xml));
    //
    // final String toBeUpdatedMdRecord = retrieveMdRecords(ou2Id);
    // final Document toBeUpdatedDocument = getDocument(toBeUpdatedMdRecord);
    // substitute(toBeUpdatedDocument, XPATH_MD_RECORDS_ESCIDOC_MD_RECORD
    // + "/" + NAME_TITLE, ou1Name);
    //
    // final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
    //
    // try {
    // updateMdRecords(ou2Id, toBeUpdatedXml);
    // failMissingException(ec);
    // }
    // catch (Exception e) {
    // assertExceptionType(ec, e);
    // }
    //
    // }

    // /**
    // * Test declining updating an organizational unit with setting a non
    // unique
    // * name in the scope of the parents.
    // *
    // * @test.name Update Md Records - Duplicate Name in Scope of Parents
    // * @test.id OUM_UMS-6-b
    // * @test.input Organizational Details XML representation containing a name
    // * of an organizational unit that just exists in the scope of
    // * the parents.
    // * @test.expected: OrganizationalUnitNameNotUniqueException
    // * @test.status Implemented
    // *
    // * @throws Exception
    // * If anything fails.
    // */
    // public void testOumUms6b() throws Exception {
    //
    // final Class<OrganizationalUnitNameNotUniqueException> ec =
    // OrganizationalUnitNameNotUniqueException.class;
    //
    // createOuHierarchie();
    //
    // final String createdChild2Xml =
    // createSuccessfully("escidoc_ou_create.xml");
    //
    // // add created ou to ouIdTop using update. ouIdTop has ouId1 as child.
    // Document toBeUpdatedDocument = getDocument(createdChild2Xml);
    // final String objid = getObjidValue(toBeUpdatedDocument);
    // insertParentsElement((Document) deleteElement(toBeUpdatedDocument,
    // XPATH_ORGANIZATIONAL_UNIT_PARENTS),
    // XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS,
    // new String[] { ouTop1Id, null }, false);
    // update(objid, toString(toBeUpdatedDocument, false).replaceAll(
    // "prefix-srel", "srel"));
    //
    // toBeUpdatedDocument = getDocument(retrieveMdRecords(objid));
    // substitute(toBeUpdatedDocument, XPATH_MD_RECORDS_ESCIDOC_MD_RECORD
    // + "/" + NAME_TITLE, ouChild1ParentName);
    // try {
    // updateMdRecords(objid, toString(toBeUpdatedDocument, false));
    // failMissingException(ec);
    // }
    // catch (Exception e) {
    // assertExceptionType(ec, e);
    // }
    // }

    /**
     * Test sucessfully updating an organizational unit with a name of an
     * existing organizational unit in another scope of the parents.
     * 
     * @test.name Update Organizational Details - Update Name with Duplicate
     *            Name in different Scopes of Parents
     * @test.id OUM_UMS-6-c
     * @test.input Organizational Details XML representation containing a name
     *             of an organizational unit that just exists, but that is not
     *             in the scope of the parents of the organizational unit to be
     *             created.
     * @test.expected: The expected result is the XML representation of the
     *                 created OrganizationalUnit, corresponding to XML-schema
     *                 "organizational unit.xsd" including generated id, creator
     *                 and creation date
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUms6c() throws Exception {

        // create two parent ous
        String[] parentIds = createSuccessfully("escidoc_ou_create.xml", 2);
        final String parent1Id = parentIds[0];
        final String parent2Id = parentIds[1];

        // create child of first parent ou
        final String child1Xml =
            createSuccessfullyChild("escidoc_ou_create.xml",
                new String[] { parent1Id });
        final String child1Name =
            selectSingleNodeAsserted(getDocument(child1Xml),
                XPATH_ORGANIZATIONAL_UNIT_TITLE).getTextContent();

        // create child of second parent ou
        final String child2Xml =
            createSuccessfullyChild("escidoc_ou_create.xml",
                new String[] { parent2Id });
        final String child2Name =
            selectSingleNodeAsserted(getDocument(child1Xml),
                XPATH_ORGANIZATIONAL_UNIT_TITLE).getTextContent();

        // update name of second child ou to name of first child ou
        final String child2Id = getObjidValue(child2Xml);
        final Document toBeUpdatedDocument =
            getDocument(retrieveMdRecords(child2Id));
        substitute(toBeUpdatedDocument, XPATH_MD_RECORDS_ESCIDOC_MD_RECORD
            + "/" + NAME_TITLE, child1Name);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
        final String beforeUpdateTimestamp = getNowAsTimestamp();
        String updatedXml = null;
        try {
            updatedXml = updateMdRecords(child2Id, toBeUpdatedXml);
        }
        catch (Exception e) {
            failException(
                "Updating OU with name of ou in another scope failed.", e);
        }

        assertEscidocMdRecord(child2Id, getDocument(updatedXml),
            substitute(getDocument(child2Xml), XPATH_ORGANIZATIONAL_UNIT_TITLE,
                child1Name), beforeUpdateTimestamp);
        // assertOrganizationalUnit(updatedXml, toBeUpdatedXml, startTimestamp,
        // beforeUpdateTimestamp);
    }

    // ==============================================================================================
    /**
     * Test declining updating an organizational unit setting confusing data in
     * reference to a parent.
     * 
     * @test.name Organizational Unit - With Parents
     * @test.id UpdateParentOusWithConfusingData
     * @test.input Organizational Details XML representation with confusing
     *             data.
     * @test.expected: InvalidContentExcetion
     * @test.status Rejected
     * 
     * @throws Exception
     *             If anything fails.
     */

    /**
     * Test declining updating a top level organizational unit with setting an
     * empty name.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Ignore
    @Test
    public void notestOumUms5_1a() throws Exception {

        final Class ec = MissingElementValueException.class;

        // create first top level ou
        final String ou1Xml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument = getDocument(ou1Xml);
        final String ou1Id = getObjidValue(toBeUpdatedDocument);
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE, "");

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        try {
            updateMdRecords(ou1Id, toBeUpdatedXml);
            failMissingException(ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Successfully update of OU name.
     * 
     * @throws Exception
     */
    @Ignore
    @Test
    public void notestOumUms5_1b() throws Exception {

        // create first top level ou
        final String ou1Xml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument = getDocument(ou1Xml);
        final String ou1Id = getObjidValue(toBeUpdatedDocument);
        final String ou1Name =
            selectSingleNodeAsserted(getDocument(ou1Xml),
                XPATH_ORGANIZATIONAL_UNIT_TITLE).getTextContent();

        final String newOuName = ou1Name + "-1";
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE,
            newOuName);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        String ou = updateMdRecords(ou1Id, toBeUpdatedXml);

        // check if ou contains new name
        final String ouName =
            selectSingleNodeAsserted(getDocument(ou),
                XPATH_ORGANIZATIONAL_UNIT_TITLE).getTextContent();
        assertEquals(newOuName, ouName);

        // check if name and title was also changed in RELS-EXT
        TripleStoreTestsBase tripleStore = new TripleStoreTestsBase();
        String result =
            tripleStore.requestMPT("<info:fedora/" + ou1Id + "> "
                + "<http://www.escidoc.de/schemas/organizationalunit/0.3/name>"
                + " *", "RDF/XML");

        String resultName =
            selectSingleNodeAsserted(getDocument(result),
                XPATH_TRIPLE_STORE_OU_NAME).getTextContent();

        assertEquals(newOuName, resultName);

        // check if changing name leads to changing title to (in DS
        // ou-description and RELS-EXT)
        result =
            tripleStore.requestMPT(
                "<info:fedora/" + ou1Id + "> "
                    + "<http://www.nsdl.org/ontologies/relationships/title>"
                    + " *", "RDF/XML");

        resultName =
            selectSingleNodeAsserted(getDocument(result),
                XPATH_TRIPLE_STORE_OU_TITLE).getTextContent();

        assertEquals(newOuName, resultName);
    }

    /**
     * Test succesfully update metadata by just changing the namespace but NOT
     * the prefix bound to that namespace.
     * 
     * @throws Exception
     */
    @Test
    public void testUpdateMdRecordNamespace() throws Exception {


        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document resourceDoc = getDocument(createdXml);
        final String id = getObjidValue(resourceDoc);
        
        String modDate =
            selectSingleNodeAsserted(resourceDoc, "/organizational-unit/@last-modification-date")
                .getNodeValue();
        
        String resourceDocString = toString(resourceDoc, false);
        resourceDocString = resourceDocString.replace("xmlns:ou=\"http://www.escidoc.de/metadata/organizational-unit\"", "xmlns:ou=\"http://just.for.test/namespace\"");

        String updatedResource = update(id, resourceDocString);
        
        // last modification timestamp must be changed
        assertNotEquals("last modification date should be changed updating md-record namespace", modDate, selectSingleNode(
            getDocument(updatedResource), "/organizational-unit/@last-modification-date")
            .getNodeValue());

        delete(id);
    }

}
