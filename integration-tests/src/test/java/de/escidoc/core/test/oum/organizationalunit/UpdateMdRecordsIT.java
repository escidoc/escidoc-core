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

import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import de.escidoc.core.test.common.fedora.TripleStoreTestBase;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.junit.Assert.assertEquals;

/**
 * Test the mock implementation of the item resource.
 *
 * @author Michael Schneider
 */
public class UpdateMdRecordsIT extends OrganizationalUnitTestBase {

    /**
     * Test successfully updating the organization-details sub resource of an organizational.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUms1a() throws Exception {

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document createdDocument = getDocument(createdXml);
        final String id = getObjidValue(createdDocument);

        String mdRecords = retrieveMdRecords(id);
        Document toBeUpdatedDocument = getDocument(mdRecords);
        String beforeUpdateTimestamp = getNowAsTimestamp();
        changeMdRecordsValues(XPATH_ORGANIZATIONAL_UNIT + XPATH_MD_RECORDS_ESCIDOC_MD_RECORD, createdDocument,
            beforeUpdateTimestamp);

        changeMdRecordsValues(XPATH_MD_RECORDS_ESCIDOC_MD_RECORD, toBeUpdatedDocument, beforeUpdateTimestamp);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        String updatedXml = null;
        try {
            updatedXml = updateMdRecords(id, toBeUpdatedXml);
        }
        catch (final Exception e) {
            failException("Updating OU failed with exception. ", e);
        }
        assertEscidocMdRecord(id, getDocument(updatedXml), createdDocument, beforeUpdateTimestamp);
    }

    private Document changeMdRecordsValues(
        final String xpathToEscidocMdRecord, final Document document, final String uniqueNamePart) throws Exception {
        substitute(document, xpathToEscidocMdRecord + "/" + NAME_TITLE, selectSingleNode(document,
            xpathToEscidocMdRecord + "/" + NAME_TITLE).getTextContent()
            + "_" + uniqueNamePart);
        substitute(document, xpathToEscidocMdRecord + "/" + NAME_ORGANIZATION_TYPE, "NewType");
        substitute(document, xpathToEscidocMdRecord + "/" + NAME_DESCRIPTION, "NewDescription");
        substitute(document, xpathToEscidocMdRecord + "/" + NAME_COUNTRY, "NC");
        substitute(document, xpathToEscidocMdRecord + "/" + NAME_CITY, "New City");
        // start-date
        substitute(document, xpathToEscidocMdRecord + "/" + NAME_START_DATE, startTimestamp);
        // end-date
        substitute(document, xpathToEscidocMdRecord + "/" + NAME_END_DATE, getNowAsTimestamp());
        return document;
    }

    /**
     * Tests successfully updating the organization-details sub resource of an organizational unit with setting a new
     * name.
     */
    @Test
    public void testOumUms1c() throws Exception {
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(createdXml);

        final String organizationDetails = retrieveMdRecords(id);
        final Document toBeUpdatedDocument = getDocument(organizationDetails);

        // change name
        final String name = getUniqueName("Neuer Name");
        substitute(toBeUpdatedDocument, XPATH_MD_RECORDS_ESCIDOC_MD_RECORD + "/" + NAME_TITLE, name);
        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        String beforeUpdateTimestamp = getNowAsTimestamp();
        String updatedXml = null;
        try {
            updatedXml = updateMdRecords(id, toBeUpdatedXml);
        }
        catch (final Exception e) {

            failException("Updating Organization Details with set new external id failed with exception. ", e);
        }
        Document createdDocument = getDocument(createdXml);
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE, name);
        assertEscidocMdRecord(id, getDocument(updatedXml), getDocument(toString(createdDocument, false)),
            beforeUpdateTimestamp);
    }

    /**
     * Test declining updating the organization-details sub resource of an organizational unit with non existing id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUms2a() throws Exception {

        final Class<OrganizationalUnitNotFoundException> ec = OrganizationalUnitNotFoundException.class;
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(createdXml);
        final String toBeUpdatedXml = retrieveMdRecords(id);
        try {
            updateMdRecords(UNKNOWN_ID, toBeUpdatedXml);

            failMissingException("No exception occured on update of organization-details with non existing id.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the organization-details sub resource of an organizational with id of a resource of
     * another type.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUms2b() throws Exception {

        final Class<OrganizationalUnitNotFoundException> ec = OrganizationalUnitNotFoundException.class;

        final String ouXml = createSuccessfully("escidoc_ou_create.xml");
        final String toBeUpdatedXml = retrieveMdRecords(getObjidValue(ouXml));

        try {
            updateMdRecords(CONTEXT_ID, toBeUpdatedXml);
            failMissingException("No exception occured on update with id of resoure of" + " another type.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the organization-details sub resource of an organizational with wrong last modification
     * date (too old).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUms3a() throws Exception {

        Class<OptimisticLockingException> ec = OptimisticLockingException.class;
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(createdXml);
        final String toBeUpdatedXml = retrieveMdRecords(id);

        final Document toBeUpdatedDocument =
            (Document) substitute(getDocument(toBeUpdatedXml), XPATH_ORGANIZATION_MD_RECORDS + "/"
                + PART_LAST_MODIFICATION_DATE, "2005-01-30T11:36:42.015Z");
        try {
            updateMdRecords(id, toString(toBeUpdatedDocument, false));
            failMissingException("No exception occured on update with wrong time stamp.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the organization-details sub resource of an organizational with wrong last modification
     * date (more recent).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUms3b() throws Exception {

        Class<OptimisticLockingException> ec = OptimisticLockingException.class;
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(createdXml);
        final String toBeUpdatedXml = retrieveMdRecords(id);

        final Document toBeUpdatedDocument =
            (Document) substitute(getDocument(toBeUpdatedXml), XPATH_ORGANIZATION_MD_RECORDS + "/"
                + PART_LAST_MODIFICATION_DATE, getNowAsTimestamp());

        try {
            updateMdRecords(id, toString(toBeUpdatedDocument, false));
            failMissingException("No exception occured on update with wrong time stamp.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the organization-details sub resource of an organizational with missing id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUms4a() throws Exception {

        final Class<MissingMethodParameterException> ec = MissingMethodParameterException.class;

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(createdXml);
        final String toBeUpdatedXml = retrieveMdRecords(id);

        try {
            updateMdRecords(null, toBeUpdatedXml);
            failMissingException("No exception occured on update with missing id.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the organization-details sub resource of an organizational with missing
     * organizational-details xml.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUms4b() throws Exception {

        final Class<MissingMethodParameterException> ec = MissingMethodParameterException.class;

        final String toBeUpdatedXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(toBeUpdatedXml);

        try {
            updateMdRecords(id, null);

            failMissingException("No exception occured on update with missing organization-details xml.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the organization-details sub resource of an organizational with corrupt xml.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUms5a() throws Exception {

        final Class<XmlCorruptedException> ec = XmlCorruptedException.class;

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument = getDocument(createdXml);
        final String id = getObjidValue(toBeUpdatedDocument);
        try {
            updateMdRecords(id, "<org");
            failMissingException("No exception occured on update with corrupted xml.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the md-records sub resource of an organizational with wrong root element
     * (organizational-unit) in xml.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUms5b() throws Exception {

        final Class<XmlCorruptedException> ec = XmlCorruptedException.class;

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument = getDocument(createdXml);
        final String id = getObjidValue(toBeUpdatedDocument);
        try {
            updateMdRecords(id, createdXml);
            failMissingException("No exception occured on update with corrupted xml.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the organization-details sub resource of an organizational with wrong root element
     * (<unknown/>) in xml.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUms5c() throws Exception {

        final Class<XmlCorruptedException> ec = XmlCorruptedException.class;

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument = getDocument(createdXml);
        final String id = getObjidValue(toBeUpdatedDocument);
        try {
            updateMdRecords(id, "<unknown/>");
            failMissingException("No exception occured on update with corrupted xml.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test sucessfully updating an organizational unit with a name of an existing organizational unit in another scope
     * of the parents.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUms6c() throws Exception {

        // create two parent ous
        String[] parentIds = createSuccessfully("escidoc_ou_create.xml", 2);
        final String parent1Id = parentIds[0];
        final String parent2Id = parentIds[1];

        // create child of first parent ou
        final String child1Xml = createSuccessfullyChild("escidoc_ou_create.xml", new String[] { parent1Id });
        final String child1Name =
            selectSingleNodeAsserted(getDocument(child1Xml), XPATH_ORGANIZATIONAL_UNIT_TITLE).getTextContent();

        // create child of second parent ou
        final String child2Xml = createSuccessfullyChild("escidoc_ou_create.xml", new String[] { parent2Id });
        final String child2Name =
            selectSingleNodeAsserted(getDocument(child1Xml), XPATH_ORGANIZATIONAL_UNIT_TITLE).getTextContent();

        // update name of second child ou to name of first child ou
        final String child2Id = getObjidValue(child2Xml);
        final Document toBeUpdatedDocument = getDocument(retrieveMdRecords(child2Id));
        substitute(toBeUpdatedDocument, XPATH_MD_RECORDS_ESCIDOC_MD_RECORD + "/" + NAME_TITLE, child1Name);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
        final String beforeUpdateTimestamp = getNowAsTimestamp();
        String updatedXml = null;
        try {
            updatedXml = updateMdRecords(child2Id, toBeUpdatedXml);
        }
        catch (final Exception e) {
            failException("Updating OU with name of ou in another scope failed.", e);
        }

        assertEscidocMdRecord(child2Id, getDocument(updatedXml), substitute(getDocument(child2Xml),
            XPATH_ORGANIZATIONAL_UNIT_TITLE, child1Name), beforeUpdateTimestamp);
    }

    /**
     * Test declining updating a top level organizational unit with setting an empty name.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUms5_1a() throws Exception {

        final Class<?> ec = XmlCorruptedException.class;

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
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Successfully update of OU name.
     */
    @Ignore("Successfully update of OU name")
    @Test
    public void testOumUms5_1b() throws Exception {

        // create first top level ou
        final String ou1Xml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument = getDocument(ou1Xml);
        final String ou1Id = getObjidValue(toBeUpdatedDocument);
        final String ou1Name =
            selectSingleNodeAsserted(getDocument(ou1Xml), XPATH_ORGANIZATIONAL_UNIT_TITLE).getTextContent();

        final String newOuName = ou1Name + "-1";
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE, newOuName);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        String ou = updateMdRecords(ou1Id, toBeUpdatedXml);

        // check if ou contains new name
        final String ouName =
            selectSingleNodeAsserted(getDocument(ou), XPATH_ORGANIZATIONAL_UNIT_TITLE).getTextContent();
        assertEquals(newOuName, ouName);

        // check if name and title was also changed in RELS-EXT
        TripleStoreTestBase tripleStore = new TripleStoreTestBase();
        String result =
            tripleStore.requestMPT("<info:fedora/" + ou1Id + "> "
                + "<http://www.escidoc.de/schemas/organizationalunit/0.3/name>" + " *", "RDF/XML");

        String resultName = selectSingleNodeAsserted(getDocument(result), XPATH_TRIPLE_STORE_OU_NAME).getTextContent();

        assertEquals(newOuName, resultName);

        // check if changing name leads to changing title to (in DS
        // ou-description and RELS-EXT)
        result =
            tripleStore.requestMPT("<info:fedora/" + ou1Id + "> "
                + "<http://www.nsdl.org/ontologies/relationships/title>" + " *", "RDF/XML");

        resultName = selectSingleNodeAsserted(getDocument(result), XPATH_TRIPLE_STORE_OU_TITLE).getTextContent();

        assertEquals(newOuName, resultName);
    }

    /**
     * Test succesfully update metadata by just changing the namespace but NOT the prefix bound to that namespace.
     */
    @Test
    public void testUpdateMdRecordNamespace() throws Exception {

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document resourceDoc = getDocument(createdXml);
        final String id = getObjidValue(resourceDoc);

        String modDate =
            selectSingleNodeAsserted(resourceDoc, "/organizational-unit/@last-modification-date").getNodeValue();

        String resourceDocString = toString(resourceDoc, false);
        resourceDocString =
            resourceDocString.replace("xmlns:ou=\"http://www.escidoc.de/metadata/organizational-unit\"",
                "xmlns:ou=\"http://just.for.test/namespace\"");

        String updatedResource = update(id, resourceDocString);

        // last modification timestamp must be changed
        assertNotEquals("last modification date should be changed updating md-record namespace", modDate,
            selectSingleNode(getDocument(updatedResource), "/organizational-unit/@last-modification-date")
                .getNodeValue());

        delete(id);
    }

    /**
     * Test successfully updating the organization-details sub resource of an REST.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUms1aRest() throws Exception {

        final String[] parentValues = createSuccessfully("escidoc_ou_create.xml", 2);

        final String createdXml =
            createSuccessfullyChild("escidoc_ou_create.xml", new String[] { parentValues[0], parentValues[1] });
        final Document createdDocument = getDocument(createdXml);
        final String objid = getObjidValue(createdDocument);
        final String organizationDetails = retrieveMdRecords(objid);
        final Document toBeUpdatedDocument = getDocument(organizationDetails);

        // organization details xlink
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATION_MD_RECORDS + PART_XLINK_HREF, "Some Href");
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATION_MD_RECORDS + PART_XLINK_TITLE, "Some Title");
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATION_MD_RECORDS + PART_XLINK_TYPE, "none");

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, true);

        String updatedXml = null;
        try {
            updatedXml = updateMdRecords(objid, toBeUpdatedXml);
        }
        catch (final Exception e) {
            failException("Updating OU with changed read only values failed. ", e);
        }
        assertEscidocMdRecord(objid, getDocument(updatedXml), createdDocument, startTimestamp);

    }

    /**
     * Test successfully updating an organizational unit without read only attributes and elements via REST.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUms1bRest() throws Exception {

        final String[] parentValues = createSuccessfully("escidoc_ou_create.xml", 2);

        final String createdXml =
            createSuccessfullyChild("escidoc_ou_create.xml", new String[] { parentValues[0], parentValues[1] });
        final Document createdDocument = getDocument(createdXml);
        final String objid = getObjidValue(createdDocument);
        final String organizationDetails = retrieveMdRecords(objid);
        final Document toBeUpdatedDocument = getDocument(organizationDetails);

        // root attributes

        // organization details xlink
        deleteAttribute(toBeUpdatedDocument, XPATH_ORGANIZATION_MD_RECORDS + PART_XLINK_HREF);
        deleteAttribute(toBeUpdatedDocument, XPATH_ORGANIZATION_MD_RECORDS + PART_XLINK_TITLE);
        deleteAttribute(toBeUpdatedDocument, XPATH_ORGANIZATION_MD_RECORDS + PART_XLINK_TYPE);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, true);

        String updatedXml = null;
        try {
            updatedXml = updateMdRecords(objid, toBeUpdatedXml);
        }
        catch (final Exception e) {
            failException("Updating OU without read only values failed. ", e);
        }
        assertEscidocMdRecord(objid, getDocument(updatedXml), createdDocument, startTimestamp);
    }
}
