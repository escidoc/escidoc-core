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

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.remote.application.violated.OrganizationalUnitHierarchyViolationException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.fedora.TripleStoreTestBase;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Test the mock implementation of the item resource.
 *
 * @author Michael Schneider
 *         <p/>
 *         Changes: <ul> <li>schema version 0.3: <ul> <li>removed testUpdateParentOusWithConfusingData as this is not
 *         possible, anymore.</li> <li>removed OUM_UOU-1-2 as now special soap test exists for checking with setting all
 *         read-only values.</li> <li>removed OUM_UOU-1-3 as now special rest test exists for checking with setting all
 *         read-only values.</li> <li>removed testUpdatePropertiesReadOnlyElement as this is now a positive test with
 *         set read-only value</li> <li>added OUM-UOU-2-1</li> <li>renamed testUpdatePropertiesPid to OUM-UOU-2-2</li>
 *         <li>renamed testUpdateWithNonExistingId to OUM-UOU-6-1</li> <li>renamed testUpdateWithWrongLastModificationDate
 *         to OUM-UOU-7-1</li> <li>added OUM-UOU-7-2</li> <li>added OUM-UOU-6-2</li> </ul> </ul>
 */
public class UpdateIT extends OrganizationalUnitTestBase {

    /**
     * Test successfully deleting one of two md-records.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDelOneOfTwoMdRecords() throws Exception {

        final String createdXml = createSuccessfully("escidoc_ou_create_2_md_records.xml");
        final Document createdDocument = getDocument(createdXml);
        final String id = getObjidValue(createdDocument);

        Document forUpdate = getDocument(retrieve(id));
        String xpathMdRecordToRemove = XPATH_ORGANIZATIONAL_UNIT + XPATH_MD_RECORD + "[@name = 'abc']";
        deleteElement(forUpdate, xpathMdRecordToRemove);
        assertNull("Metadata record must be removed for this test.", selectSingleNode(forUpdate, xpathMdRecordToRemove));

        String beforeUpdateTimestamp = getNowAsTimestamp();
        String updatedXml = update(id, toString(forUpdate, false));

        assertOrganizationalUnit(updatedXml, toString(forUpdate, false), startTimestamp, beforeUpdateTimestamp);
    }

    /**
     * Test successfully update of an organizational unit created in the eSciDoc Infrastructure without changing the
     * representation. No change should occur.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUpdateNoChanges1() throws Exception {

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document createdDocument = getDocument(createdXml);
        final String id = getObjidValue(createdDocument);
        final String creationLmd = getLastModificationDateValue(createdDocument);

        final String updatedXml = update(id, createdXml);
        final String lmdAfterUpdate = getLastModificationDateValue(getDocument(updatedXml));

        assertEquals("Update without modification should not change an Organizational Unit (from create).",
            creationLmd, lmdAfterUpdate);
    }

    /**
     * Test successfully update of an organizational unit retrieved from the eSciDoc Infrastructure without changing the
     * representation. No change should occur.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUpdateNoChanges2() throws Exception {

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document createdDocument = getDocument(createdXml);
        final String id = getObjidValue(createdDocument);

        final String retrievedXml = retrieve(id);
        final Document retrievedDocument = getDocument(retrievedXml);
        final String retrievedLmd = getLastModificationDateValue(retrievedDocument);

        final String updatedXml = update(id, retrievedXml);
        final String lmdAfterUpdate = getLastModificationDateValue(getDocument(updatedXml));

        assertEquals("Update without modification should not change an Organizational Unit (from retrieve).",
            retrievedLmd, lmdAfterUpdate);
    }

    /**
     * Test successfully update of an organizational unit. Element of the section "md-record" with name 'escidoc' will
     * be updated.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUou1a() throws Exception {

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument = getDocument(createdXml);
        final String id = getObjidValue(toBeUpdatedDocument);

        // dc:title
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE, getUniqueName("New Title for update"));
        // dcterms:alternative
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_ALTERNATIVE, "NTfU");
        // dc:description
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_DESCRIPTION, "NewDescription");
        // dc:identifier
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_IDENTIFIER, "NewIdentifier");
        // type
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_ORGANIZATION_TYPE, "NewType");
        // country
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_COUNTRY, "NC");
        // city
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_CITY, "New City");
        // start-date
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_START_DATE, startTimestamp);
        // end-date
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_END_DATE, getNowAsTimestamp());
        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        String beforeUpdateTimestamp = getNowAsTimestamp();
        String updatedXml = null;
        try {
            updatedXml = update(id, toBeUpdatedXml);
        }
        catch (final Exception e) {
            failException("Updating OU failed with exception. ", e);
        }
        assertOrganizationalUnit(updatedXml, toBeUpdatedXml, startTimestamp, beforeUpdateTimestamp);
    }

    /**
     * Test successfully update of an organizational unit. References to parents will be updated: exist references to
     * parents will be deleted, a new reference to a parent in status created will be added.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUou1b() throws Exception {

        createOuHierarchie();

        // create new parent for child 2
        final String ouNewParentXml = createSuccessfully("escidoc_ou_create.xml");
        final Document ouNewParentDocument = getDocument(ouNewParentXml);
        final String ouNewParentId = getObjidValue(ouNewParentDocument);
        // final String ouNewParentTitle = getTitleValue(ouNewParentDocument);

        String child2Xml = retrieve(ouChild2ParentsId);
        Document toBeUpdatedDocument = getDocument(child2Xml);

        // delete old parents and add new parent to child2
        // this is done by replacing the parent ous element
        deleteElement(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        insertParentsElement(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS, new String[] { ouNewParentId,
            null }, false);

        String beforeUpdateTimestamp = getNowAsTimestamp();
        String toBeUpdatedXml =
            toString(toBeUpdatedDocument, false).replaceAll(SREL_PREFIX_TEMPLATES, SREL_PREFIX_ESCIDOC);

        String updatedXml = null;
        try {
            updatedXml = update(ouChild2ParentsId, toBeUpdatedXml);
        }
        catch (final Exception e) {
            failException("Updating parent ous failed.", e);
        }
        assertOrganizationalUnit(updatedXml, toBeUpdatedXml, startTimestamp, beforeUpdateTimestamp);
    }

    /**
     * Test successfully update of an organizational unit. References to parents will be updated: exist references to
     * parents will be deleted, a new reference to a parent in status opened will be added.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUou1c() throws Exception {

        createOuHierarchie();

        // create new parent for child 2
        final String ouNewParentXml = createSuccessfully("escidoc_ou_create.xml");
        final Document ouNewParentDocument = getDocument(ouNewParentXml);
        final String ouNewParentId = getObjidValue(ouNewParentDocument);
        open(ouNewParentId, getTheLastModificationParam(true, ouNewParentId, "Opened organizational unit '"
            + ouNewParentId + "'."));

        String child2Xml = retrieve(ouChild2ParentsId);
        Document toBeUpdatedDocument = getDocument(child2Xml);

        // delete old parents and add new parent to child2
        // this is done by replacing the parent ous element
        deleteElement(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        insertParentsElement(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS, new String[] { ouNewParentId,
            null }, false);

        String beforeUpdateTimestamp = getNowAsTimestamp();
        String toBeUpdatedXml =
            toString(toBeUpdatedDocument, false).replaceAll(SREL_PREFIX_TEMPLATES, SREL_PREFIX_ESCIDOC);

        String updatedXml = null;
        try {
            updatedXml = update(ouChild2ParentsId, toBeUpdatedXml);
        }
        catch (final Exception e) {
            failException("Updating parent ous failed.", e);
        }
        assertOrganizationalUnit(updatedXml, toBeUpdatedXml, startTimestamp, beforeUpdateTimestamp);
    }

    /**
     * Test successfully update of an organizational unit. References to parent-ous will be updated twice with existing
     * references to parent-ous. Issue 382
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUou1d() throws Exception {

        createOuHierarchie();

        // create new parent for child 2
        final String ouNewParentXml = createSuccessfully("escidoc_ou_create.xml");
        final Document ouNewParentDocument = getDocument(ouNewParentXml);
        final String ouNewParentId = getObjidValue(ouNewParentDocument);
        String child2Xml = retrieve(ouChild2ParentsId);
        Document toBeUpdatedDocument = getDocument(child2Xml);

        deleteElement(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        insertParentsElement(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS, new String[] { ouNewParentId,
            ouTop2Id, null, null }, false);

        String beforeUpdateTimestamp = getNowAsTimestamp();
        String toBeUpdatedXml =
            toString(toBeUpdatedDocument, false).replaceAll(SREL_PREFIX_TEMPLATES, SREL_PREFIX_ESCIDOC);
        String updatedXml = null;
        try {
            updatedXml = update(ouChild2ParentsId, toBeUpdatedXml);
        }
        catch (final Exception e) {
            failException("Updating parent ous failed.", e);
        }
        assertOrganizationalUnit(updatedXml, toBeUpdatedXml, startTimestamp, beforeUpdateTimestamp);

        // 2nd update
        final String ou3rdParentId = getObjidValue(createSuccessfully("escidoc_ou_create.xml"));
        toBeUpdatedDocument = getDocument(updatedXml);

        deleteElement(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        insertParentsElement(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS, new String[] { ouNewParentId,
            ouTop2Id, ou3rdParentId, null, null, null }, false);

        beforeUpdateTimestamp = getNowAsTimestamp();
        toBeUpdatedXml = toString(toBeUpdatedDocument, false).replaceAll(SREL_PREFIX_TEMPLATES, SREL_PREFIX_ESCIDOC);
        updatedXml = null;
        try {
            updatedXml = update(ouChild2ParentsId, toBeUpdatedXml);
        }
        catch (final Exception e) {
            failException("Updating parent ous failed.", e);
        }
        assertOrganizationalUnit(updatedXml, toBeUpdatedXml, startTimestamp, beforeUpdateTimestamp);
    }

    /**
     * Test successfully update of an organizational unit. The name of the organizational unit will be updated.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUou1e() throws Exception {

        // create first top level ou
        final String ouXml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument = getDocument(ouXml);
        final String ouId = getObjidValue(toBeUpdatedDocument);
        final String ouTitle =
            selectSingleNodeAsserted(getDocument(ouXml), XPATH_ORGANIZATIONAL_UNIT_TITLE).getTextContent();

        final String ouTitleToUpdate = getUniqueName(ouTitle);
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE, ouTitleToUpdate);

        final String ouDescription =
            selectSingleNodeAsserted(getDocument(ouXml), XPATH_ORGANIZATIONAL_UNIT_DESCRIPTION).getTextContent();
        final String ouDescriptionToUpdate = getUniqueName(ouDescription);
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_DESCRIPTION, ouDescriptionToUpdate);

        String ou = update(ouId, toString(toBeUpdatedDocument, false));

        // check if updated organizational-unit contains new title
        final String updatedTitle =
            selectSingleNodeAsserted(getDocument(ou), XPATH_ORGANIZATIONAL_UNIT_TITLE).getTextContent();
        assertEquals(ouTitleToUpdate, updatedTitle);
        // check if updated organizational-unit contains new title
        final String updatedDescription =
            selectSingleNodeAsserted(getDocument(ou), XPATH_ORGANIZATIONAL_UNIT_DESCRIPTION).getTextContent();
        assertEquals(ouDescriptionToUpdate, updatedDescription);

        // check if title was also changed in resource index
        TripleStoreTestBase tripleStore = new TripleStoreTestBase();
        String result =
            tripleStore.requestMPT("<info:fedora/" + ouId + "> " + "<http://purl.org/dc/elements/1.1/title>" + " *",
                "RDF/XML");
        String riTitle = selectSingleNodeAsserted(getDocument(result), XPATH_TRIPLE_STORE_OU_TITLE).getTextContent();
        assertEquals(ouTitleToUpdate, riTitle);

        // check if description was also changed in resource index
        result =
            tripleStore.requestMPT("<info:fedora/" + ouId + "> " + "<http://purl.org/dc/elements/1.1/description>"
                + " *", "RDF/XML");
        String riDescription =
            selectSingleNodeAsserted(getDocument(result), XPATH_TRIPLE_STORE_OU_DESCRIPTION).getTextContent();
        assertEquals(ouDescriptionToUpdate, riDescription);
    }

    /**
     * Test declining update of an organizational unit with references to parent organizational units, which causes
     * cycles in organizational units hierarchy.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUou2a() throws Exception {

        Class<OrganizationalUnitHierarchyViolationException> ec = OrganizationalUnitHierarchyViolationException.class;

        // create parent
        final String parentXml = createSuccessfully("escidoc_ou_create.xml");
        final Document parentDocument = getDocument(parentXml);
        final String parentId = getObjidValue(parentDocument);

        // create child
        final String childXml = createSuccessfullyChild("escidoc_ou_create.xml", new String[] { parentId });
        final String childId = getObjidValue(getDocument(childXml));

        // create cycle by updating the parent
        insertParentsElement((Document) deleteElement(parentDocument, XPATH_ORGANIZATIONAL_UNIT_PARENTS),
            XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS, new String[] { childId, null }, false);

        String toBeUpdatedXml = toString(parentDocument, false).replaceAll(SREL_PREFIX_TEMPLATES, SREL_PREFIX_ESCIDOC);
        try {
            update(parentId, toBeUpdatedXml);
            failMissingException("No exception occured on update with references to parents, which"
                + "causes cycles in organizational units hierarchy.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec.getName() + " expected.", ec, e);
            return;
        }
    }

    /**
     * Test declining updating an organizational unit with providing a parent in state closed.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUou2b() throws Exception {

        createOuHierarchie();

        // create new parent for child 2
        final String ouNewParentXml = createSuccessfully("escidoc_ou_create.xml");
        final Document ouNewParentDocument = getDocument(ouNewParentXml);
        final String ouNewParentId = getObjidValue(ouNewParentDocument);
        open(ouNewParentId, getTheLastModificationParam(true, ouNewParentId, "Opened organizational unit '"
            + ouNewParentId + "'."));
        close(ouNewParentId, getTheLastModificationParam(true, ouNewParentId, "Closed organizational unit '"
            + ouNewParentId + "'."));

        String child2Xml = retrieve(ouChild2ParentsId);
        Document toBeUpdatedDocument = getDocument(child2Xml);

        // delete old parents and add new parent to child2
        // this is done by replacing the parent ous element
        deleteElement(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        insertParentsElement(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS, new String[] { ouNewParentId,
            null }, false);

        String toBeUpdatedXml =
            toString(toBeUpdatedDocument, false).replaceAll(SREL_PREFIX_TEMPLATES, SREL_PREFIX_ESCIDOC);

        final Class<InvalidStatusException> ec = InvalidStatusException.class;
        try {
            update(ouChild2ParentsId, toBeUpdatedXml);
            failMissingException(ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    //    /**
    //     * Test declining updating an organizational unit in state opened and
    //     * providing a parent in state created.
    //     *
    //     * @test.name Update Organizational Unit - Parent in state closed
    //     * @test.id OUM_UOU-2-b
    //     * @test.inputOrganizational Unit XML representation with a parent in state
    //     *                           closed.
    //     * @test.expected: InvalidStatusException
    //     * @test.status Revoked - because it is impossible to create an ou in state
    //     *              opened with parents in status closed.
    //     *
    //     * @throws Exception
    //     *             If anything fails.
    //     */
    // public void testOumUou2c() throws Exception {
    //
    // createOuHierarchie();
    //
    // // create new parent for child 2
    // final String ouNewParentXml =
    // createSuccessfully("escidoc_ou_create.xml");
    // final Document ouNewParentDocument = getDocument(ouNewParentXml);
    // final String ouNewParentId = getObjidValue(ouNewParentDocument);
    //
    // open(ouChild2ParentsId, getTheLastModificationParam(true,
    // ouChild2ParentsId, "Opened organizational unit '"
    // + ouChild2ParentsId + "'."));
    // String child2Xml = retrieve(ouChild2ParentsId);
    //
    // Document toBeUpdatedDocument = getDocument(child2Xml);
    // // delete old parents and add new parent to child2
    // // this is done by replacing the parent ous element
    // deleteElement(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
    // insertParentsElement(toBeUpdatedDocument,
    // XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS, new String[] { ouNewParentId,
    // null }, false);
    //
    // String toBeUpdatedXml =
    // toString(toBeUpdatedDocument, false).replaceAll(
    // SREL_PREFIX_TEMPLATES, SREL_PREFIX_ESCIDOC);
    //
    // final Class<InvalidStatusException> ec = InvalidStatusException.class;
    // try {
    // update(ouChild2ParentsId, toBeUpdatedXml);
    // failMissingException(ec);
    // }
    // catch (final Exception e) {
    // assertExceptionType(ec, e);
    // }
    // }
    //    /**
    //     * Test declining updating an organizational unit in state closed and
    //     * providing a parent in state created.
    //     *
    //     * @test.name Update Organizational Unit - Parent in state closed
    //     * @test.id OUM_UOU-2-d
    //     * @test.inputOrganizational Unit XML representation with a parent in state
    //     *                           closed.
    //     * @test.expected: InvalidStatusException
    //     * @test.status Revoked - because it is impossible to create an ou in state
    //     *              closed with parents in status created.
    //     *
    //     * @throws Exception
    //     *             If anything fails.
    //     */
    // public void testOumUou2d() throws Exception {
    //
    // createOuHierarchie();
    //
    // // create new parent for child 2
    // final String ouNewParentXml =
    // createSuccessfully("escidoc_ou_create.xml");
    // final Document ouNewParentDocument = getDocument(ouNewParentXml);
    // final String ouNewParentId = getObjidValue(ouNewParentDocument);
    //
    // open(ouChild2ParentsId, getTheLastModificationParam(true,
    // ouChild2ParentsId, "Opened organizational unit '"
    // + ouChild2ParentsId + "'."));
    // close(ouChild2ParentsId, getTheLastModificationParam(true,
    // ouChild2ParentsId, "Closed organizational unit '"
    // + ouChild2ParentsId + "'."));
    //
    // String child2Xml = retrieve(ouChild2ParentsId);
    // Document toBeUpdatedDocument = getDocument(child2Xml);
    //
    // // delete old parents and add new parent to child2
    // // this is done by replacing the parent ous element
    // deleteElement(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
    // insertParentsElement(toBeUpdatedDocument,
    // XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS, new String[] { ouNewParentId,
    // null }, false);
    //
    // String toBeUpdatedXml =
    // toString(toBeUpdatedDocument, false).replaceAll(
    // SREL_PREFIX_TEMPLATES, SREL_PREFIX_ESCIDOC);
    //
    // final Class<InvalidStatusException> ec = InvalidStatusException.class;
    // try {
    // update(ouChild2ParentsId, toBeUpdatedXml);
    // failMissingException(ec);
    // }
    // catch (final Exception e) {
    // assertExceptionType(ec, e);
    // }
    // }

    /**
     * Test declining update of an organizational unit with non existing id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUou2e() throws Exception {

        final Class<OrganizationalUnitNotFoundException> ec = OrganizationalUnitNotFoundException.class;

        final String toBeUpdatedXml = createSuccessfully("escidoc_ou_create.xml");

        try {
            update(UNKNOWN_ID, toBeUpdatedXml);
            failMissingException("No exception occured on update with non existing id.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining update of an organizational unit with id of a resource of another type.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUou2f() throws Exception {

        final Class<OrganizationalUnitNotFoundException> ec = OrganizationalUnitNotFoundException.class;

        final String toBeUpdatedXml = createSuccessfully("escidoc_ou_create.xml");

        try {
            update(CONTEXT_ID, toBeUpdatedXml);
            failMissingException("No exception occured on update with id of resoure of" + " another type.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining update of an organizational unit with with missing id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUou2g() throws Exception {

        final Class<MissingMethodParameterException> ec = MissingMethodParameterException.class;

        final String toBeUpdatedXml = createSuccessfully("escidoc_ou_create.xml");

        try {
            update(null, toBeUpdatedXml);
            failMissingException("No exception occured on update with missing id.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining update of an organizational unit with with missing organizational unit xml.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUou2h() throws Exception {

        final Class<MissingMethodParameterException> ec = MissingMethodParameterException.class;

        final String toBeUpdatedXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(toBeUpdatedXml);

        try {
            update(id, null);
            failMissingException("No exception occured on update with missing ou xml.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining update of an organizational unit with wrong last modification date (too old).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUou7a() throws Exception {

        Class<OptimisticLockingException> ec = OptimisticLockingException.class;

        final String toBeUpdatedXml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument =
            (Document) substitute(getDocument(toBeUpdatedXml), XPATH_ORGANIZATIONAL_UNIT_LAST_MODIFICATION_DATE,
                "2005-01-30T11:36:42.015Z");
        final String id = getObjidValue(toBeUpdatedDocument);

        try {
            update(id, toString(toBeUpdatedDocument, false));
            failMissingException("No exception occured on update with wrong time stamp.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining update of an organizational unit with wrong last modification date (more recent).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUou7b() throws Exception {

        Class<OptimisticLockingException> ec = OptimisticLockingException.class;

        final String toBeUpdatedXml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument =
            (Document) substitute(getDocument(toBeUpdatedXml), XPATH_ORGANIZATIONAL_UNIT_LAST_MODIFICATION_DATE,
                getNowAsTimestamp());
        final String id = getObjidValue(toBeUpdatedDocument);

        try {
            update(id, toString(toBeUpdatedDocument, false));
            failMissingException("No exception occured on update with wrong time stamp.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining update of an organizational unit with corrupt xml.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUou8a() throws Exception {

        final Class<XmlCorruptedException> ec = XmlCorruptedException.class;

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument = getDocument(createdXml);
        final String id = getObjidValue(toBeUpdatedDocument);
        deleteElement(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_CREATION_DATE);
        try {
            update(id, "<org");
            failMissingException("No exception occured on update with corrupted xml.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating a top level organizational unit with setting an empty name.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUou5b() throws Exception {

        final Class<MissingElementValueException> ec = MissingElementValueException.class;

        // create first top level ou
        final String ou1Xml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument = getDocument(ou1Xml);
        final String ou1Id = getObjidValue(toBeUpdatedDocument);
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE, "");

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        try {
            update(ou1Id, toBeUpdatedXml);
            failMissingException(ec);
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
    public void testOumUou5d() throws Exception {

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

        // update name of second child ou to name of first child ou
        final Document toBeUpdatedDocument = getDocument(child2Xml);
        final String child2Id = getObjidValue(toBeUpdatedDocument);
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE, child1Name);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
        final String beforeUpdateTimestamp = getNowAsTimestamp();
        String updatedXml = null;
        try {
            updatedXml = update(child2Id, toBeUpdatedXml);
        }
        catch (final Exception e) {
            failException("Updating OU with name of ou in another scope failed.", e);
        }
        assertOrganizationalUnit(updatedXml, toBeUpdatedXml, startTimestamp, beforeUpdateTimestamp);
    }

    /**
     * Test sucessfully updating an organizational unit with moving to another scope (top-level) and setting the name of
     * an existing organizational unit in the previous scope of the parents.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUou5f() throws Exception {

        // create parent
        final String topLevelId = createSuccessfully("escidoc_ou_create.xml", 1)[0];

        // create first child
        final String child1Xml = createSuccessfullyChild("escidoc_ou_create.xml", new String[] { topLevelId });
        final String child1Name =
            selectSingleNodeAsserted(getDocument(child1Xml), XPATH_ORGANIZATIONAL_UNIT_TITLE).getTextContent();

        // create second child
        final String child2Xml = createSuccessfullyChild("escidoc_ou_create.xml", new String[] { topLevelId });

        // remove parent of second child (making it a top-level ou) and set the
        // name of the first child.
        final Document toBeUpdatedDocument = getDocument(child2Xml);
        final String child2Id = getObjidValue(toBeUpdatedDocument);
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE, child1Name);
        deleteNodes(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_PARENTS);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
        final String beforeUpdateTimestamp = getNowAsTimestamp();
        String updatedXml = null;
        try {
            updatedXml = update(child2Id, toBeUpdatedXml);
        }
        catch (final Exception e) {
            failException("Updating OU with removed parents and set name of ou in" + " previous scope failed.", e);
        }
        assertOrganizationalUnit(updatedXml, toBeUpdatedXml, startTimestamp, beforeUpdateTimestamp);
    }

    /**
     * Test successfully updating an organizational unit with changed read only attributes and elements via REST.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUou3_rest() throws Exception {

        final String[] parentValues = createSuccessfully("escidoc_ou_create.xml", 2);

        final String createdXml =
            createSuccessfullyChild("escidoc_ou_create.xml", new String[] { parentValues[0], parentValues[1] });
        final Document createdDocument = getDocument(createdXml);
        final String objid = getObjidValue(createdDocument);

        // root attributes
        final Document toBeUpdatedDocument =
            (Document) substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_XLINK_HREF, "Some Href");
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_XLINK_TITLE, "Some Title");
        // substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_XLINK_TYPE,
        // "none");
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_XML_BASE, "http://some.base.uri");

        // resources xlink
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_RESOURCES_XLINK_HREF, "Some Href");
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_RESOURCES_XLINK_TITLE, "Some Title");
        // substitute(createdDocument,
        // XPATH_ORGANIZATIONAL_UNIT_RESOURCES_XLINK_TYPE, "none");

        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PATH_LIST_XLINK_HREF, "Some Href");
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PATH_LIST_XLINK_TITLE, "Some Title");
        // substitute(createdDocument,
        // XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PATH_LIST_XLINK_TYPE, "none");

        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PARENT_OBJECTS_XLINK_HREF, "Some Href");
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PARENT_OBJECTS_XLINK_TITLE, "Some Title");
        // substitute(createdDocument,
        // XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PARENT_OBJECTS_XLINK_TYPE,
        // "none");

        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_RESOURCE_CHILD_OBJECTS_XLINK_HREF, "Some Href");
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_RESOURCE_CHILD_OBJECTS_XLINK_TITLE, "Some Title");
        // substitute(createdDocument,
        // XPATH_ORGANIZATIONAL_UNIT_RESOURCE_CHILD_OBJECTS_XLINK_TYPE, "none");

        // properties xlink
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_PROPERTIES_XLINK_HREF, "Some Href");
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_PROPERTIES_XLINK_TITLE, "Some Title");
        // substitute(createdDocument,
        // XPATH_ORGANIZATIONAL_UNIT_PROPERTIES_XLINK_TYPE, "none");

        // creation-date
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_CREATION_DATE, getNowAsTimestamp());

        // created-by
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_CREATED_BY_XLINK_HREF, "Some Href");
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_CREATED_BY_XLINK_TITLE, "Some Title");
        // substitute(createdDocument,
        // XPATH_ORGANIZATIONAL_UNIT_CREATED_BY_XLINK_TYPE, "none");

        // modified-by
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_MODIFIED_BY_XLINK_HREF, "Some Href");
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_MODIFIED_BY_XLINK_TITLE, "Some Title");
        // substitute(createdDocument,
        // XPATH_ORGANIZATIONAL_UNIT_MODIFIED_BY_XLINK_TYPE, "none");

        // public status cannot be changed as "opened" is currently the one and
        // only allowed value.

        // has-children
        substitute(createdDocument, EscidocAbstractTest.XPATH_ORGANIZATIONAL_UNIT_HAS_CHILDREN, "true");

        // data xlink
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS_XLINK_HREF, "Some Href");
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS_XLINK_TITLE, "Some Title");
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_PARENTS_XLINK_HREF, "Some Href");
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_PARENTS_XLINK_TITLE, "Some Title");
    }

    /**
     * Test successfully updating an organizational unit without read only attributes and elements via REST.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumUou4_rest() throws Exception {

        final String[] parentValues = createSuccessfully("escidoc_ou_create.xml", 2);

        final String createdXml =
            createSuccessfullyChild("escidoc_ou_create.xml", new String[] { parentValues[0], parentValues[1] });
        Document createdDocument = getDocument(createdXml);
        final String objid = getObjidValue(createdDocument);

        // root attributes
        final Document toBeUpdatedDocument =
            (Document) deleteNodes(createdDocument, XPATH_ORGANIZATIONAL_UNIT_XLINK_HREF);
        deleteAttribute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_XLINK_TITLE);
        deleteAttribute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_XLINK_TYPE);
        deleteAttribute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_XML_BASE);

        // resources, deleted
        deleteNodes(createdDocument, XPATH_ORGANIZATIONAL_UNIT_RESOURCES);

        // properties xlink
        deleteNodes(createdDocument, XPATH_ORGANIZATIONAL_UNIT_PROPERTIES);

        // data xlink
        deleteAttribute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS_XLINK_HREF);
        deleteAttribute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS_XLINK_TITLE);
        deleteAttribute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS_XLINK_TYPE);

        // parent-ous xlink
        deleteAttribute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_PARENTS_XLINK_HREF);
        deleteAttribute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_PARENTS_XLINK_TITLE);
        deleteAttribute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_PARENTS_XLINK_TYPE);

        // parent-ou xlink
        deleteAttribute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_PARENT_XLINK_TYPE);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, true);

        String updatedXml = null;
        try {
            updatedXml = update(objid, toBeUpdatedXml);
        }
        catch (final Exception e) {
            failException("Creating OU with set read only values failed. ", e);
        }
        assertOrganizationalUnit(updatedXml, createdXml, startTimestamp, startTimestamp, true, false);

    }

}
