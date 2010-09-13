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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import org.w3c.dom.Document;

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.remote.application.violated.OrganizationalUnitHierarchyViolationException;
import de.escidoc.core.common.exceptions.remote.application.violated.OrganizationalUnitNameNotUniqueException;
import de.escidoc.core.test.common.fedora.TripleStoreTestsBase;

/**
 * Test the mock implementation of the item resource.
 * 
 * @author MSC
 * 
 * Changes:
 * <ul>
 * <li>schema version 0.3:
 * <ul>
 * <li>removed testUpdateParentOusWithConfusingData as this is not possible,
 * anymore.</li>
 * <li>removed OUM_UOU-1-2 as now special soap test exists for checking with
 * setting all read-only values.</li>
 * <li>removed OUM_UOU-1-3 as now special rest test exists for checking with
 * setting all read-only values.</li>
 * <li>removed testUpdatePropertiesReadOnlyElement as this is now a positive
 * test with set read-only value</li>
 * <li>added OUM-UOU-2-1</li>
 * <li>renamed testUpdatePropertiesPid to OUM-UOU-2-2</li>
 * <li>renamed testUpdateWithNonExistingId to OUM-UOU-6-1</li>
 * <li>renamed testUpdateWithWrongLastModificationDate to OUM-UOU-7-1</li>
 * <li>added OUM-UOU-7-2</li>
 * <li>added OUM-UOU-6-2</li>
 * </ul>
 * </ul>
 * 
 */
@RunWith(value = Parameterized.class)
public class UpdateTest extends OrganizationalUnitTestBase {

    /**
     * @param transport
     *            The transport identifier.
     */
    public UpdateTest(final int transport) {
        super(transport);
    }

    /**
     * Test successfully update of an organizational unit. Element of the
     * section "md-record" with name 'escidoc' will be updated.
     * 
     * @test.name Update Organizational Unit - md-record
     * @test.id OUM_UOU-1-a
     * @test.input Organizational Unit XML representation with updated md-record
     *             section.
     * @test.expected: Xml representation of updated organizational unit.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUou1a() throws Exception {

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument = getDocument(createdXml);
        final String id = getObjidValue(toBeUpdatedDocument);

        // dc:title
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE,
            getUniqueName("New Title for update"));
        // dcterms:alternative
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_ALTERNATIVE,
            "NTfU");
        // dc:description
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_DESCRIPTION,
            "NewDescription");
        // dc:identifier
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_IDENTIFIER,
            "NewIdentifier");
        // type
        substitute(toBeUpdatedDocument,
            XPATH_ORGANIZATIONAL_UNIT_ORGANIZATION_TYPE, "NewType");
        // country
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_COUNTRY, "NC");
        // city
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_CITY,
            "New City");
        // start-date
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_START_DATE,
            startTimestamp);
        // end-date
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_END_DATE,
            getNowAsTimestamp());
        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        String beforeUpdateTimestamp = getNowAsTimestamp();
        String updatedXml = null;
        try {
            updatedXml = update(id, toBeUpdatedXml);
        }
        catch (Exception e) {
            failException("Updating OU failed with exception. ", e);
        }
        assertOrganizationalUnit(updatedXml, toBeUpdatedXml, startTimestamp,
            beforeUpdateTimestamp);
    }

    /**
     * Test successfully update of an organizational unit. References to parents
     * will be updated: exist references to parents will be deleted, a new
     * reference to a parent in status created will be added.
     * 
     * @test.name Update Organizational Unit - Success
     * @test.id OUM_UOU-1-b
     * @test.input
     *          <ul>
     *          <li>Id of existing organizational unit</li>
     *          <li>Valid Organizational Unit XML representation to update the
     *          addressed organizational unit</li>
     *          </ul>
     * @test.expected: The expected result is the XML representation of the
     *                 created OrganizationalUnit, corresponding to XML-schema
     *                 "organizational-unit.xsd" including generated id, creator
     *                 and creation date
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUou1b() throws Exception {

        createOuHierarchie();

        // create new parent for child 2
        final String ouNewParentXml =
            createSuccessfully("escidoc_ou_create.xml");
        final Document ouNewParentDocument = getDocument(ouNewParentXml);
        final String ouNewParentId = getObjidValue(ouNewParentDocument);
        // final String ouNewParentTitle = getTitleValue(ouNewParentDocument);

        String child2Xml = retrieve(ouChild2ParentsId);
        Document toBeUpdatedDocument = getDocument(child2Xml);

        // delete old parents and add new parent to child2
        // this is done by replacing the parent ous element
        deleteElement(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        insertParentsElement(toBeUpdatedDocument,
            XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS, new String[] { ouNewParentId,
                null }, false);

        String beforeUpdateTimestamp = getNowAsTimestamp();
        String toBeUpdatedXml =
            toString(toBeUpdatedDocument, false).replaceAll(
                SREL_PREFIX_TEMPLATES, SREL_PREFIX_ESCIDOC);

        String updatedXml = null;
        try {
            updatedXml = update(ouChild2ParentsId, toBeUpdatedXml);
        }
        catch (Exception e) {
            failException("Updating parent ous failed.", e);
        }
        assertOrganizationalUnit(updatedXml, toBeUpdatedXml, startTimestamp,
            beforeUpdateTimestamp);
    }

    /**
     * Test successfully update of an organizational unit. References to parents
     * will be updated: exist references to parents will be deleted, a new
     * reference to a parent in status opened will be added.
     * 
     * @test.name Update Organizational Unit - Success
     * @test.id OUM_UOU-1-c
     * @test.input
     *          <ul>
     *          <li>Id of existing organizational unit</li>
     *          <li>Valid Organizational Unit XML representation to update the
     *          addressed organizational unit</li>
     *          </ul>
     * @test.expected: The expected result is the XML representation of the
     *                 created OrganizationalUnit, corresponding to XML-schema
     *                 "organizational-unit.xsd" including generated id, creator
     *                 and creation date
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUou1c() throws Exception {

        createOuHierarchie();

        // create new parent for child 2
        final String ouNewParentXml =
            createSuccessfully("escidoc_ou_create.xml");
        final Document ouNewParentDocument = getDocument(ouNewParentXml);
        final String ouNewParentId = getObjidValue(ouNewParentDocument);
        open(ouNewParentId, getTheLastModificationParam(true, ouNewParentId,
            "Opened organizational unit '" + ouNewParentId + "'."));

        String child2Xml = retrieve(ouChild2ParentsId);
        Document toBeUpdatedDocument = getDocument(child2Xml);

        // delete old parents and add new parent to child2
        // this is done by replacing the parent ous element
        deleteElement(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        insertParentsElement(toBeUpdatedDocument,
            XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS, new String[] { ouNewParentId,
                null }, false);

        String beforeUpdateTimestamp = getNowAsTimestamp();
        String toBeUpdatedXml =
            toString(toBeUpdatedDocument, false).replaceAll(
                SREL_PREFIX_TEMPLATES, SREL_PREFIX_ESCIDOC);

        String updatedXml = null;
        try {
            updatedXml = update(ouChild2ParentsId, toBeUpdatedXml);
        }
        catch (Exception e) {
            failException("Updating parent ous failed.", e);
        }
        assertOrganizationalUnit(updatedXml, toBeUpdatedXml, startTimestamp,
            beforeUpdateTimestamp);
    }

    /**
     * Test successfully update of an organizational unit. References to
     * parent-ous will be updated twice with existing references to parent-ous.
     * Issue 382
     * 
     * @test.name Update Organizational Unit - Success
     * @test.id OUM_UOU-1-d
     * @test.input
     *          <ul>
     *          <li>Id of existing organizational unit</li>
     *          <li>Valid Organizational Unit XML representation to update the
     *          addressed organizational unit</li>
     *          </ul>
     * @test.expected: The expected result is the XML representation of the
     *                 created OrganizationalUnit, corresponding to XML-schema
     *                 "organizational-unit.xsd" including generated id, creator
     *                 and creation date
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUou1d() throws Exception {

        createOuHierarchie();

        // create new parent for child 2
        final String ouNewParentXml =
            createSuccessfully("escidoc_ou_create.xml");
        final Document ouNewParentDocument = getDocument(ouNewParentXml);
        final String ouNewParentId = getObjidValue(ouNewParentDocument);
        String child2Xml = retrieve(ouChild2ParentsId);
        Document toBeUpdatedDocument = getDocument(child2Xml);

        deleteElement(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        insertParentsElement(toBeUpdatedDocument,
            XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS, new String[] { ouNewParentId,
                ouTop2Id, null, null }, false);

        String beforeUpdateTimestamp = getNowAsTimestamp();
        String toBeUpdatedXml =
            toString(toBeUpdatedDocument, false).replaceAll(
                SREL_PREFIX_TEMPLATES, SREL_PREFIX_ESCIDOC);
        String updatedXml = null;
        try {
            updatedXml = update(ouChild2ParentsId, toBeUpdatedXml);
        }
        catch (Exception e) {
            failException("Updating parent ous failed.", e);
        }
        assertOrganizationalUnit(updatedXml, toBeUpdatedXml, startTimestamp,
            beforeUpdateTimestamp);

        // 2nd update
        final String ou3rdParentId =
            getObjidValue(createSuccessfully("escidoc_ou_create.xml"));
        toBeUpdatedDocument = getDocument(updatedXml);

        deleteElement(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        insertParentsElement(toBeUpdatedDocument,
            XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS, new String[] { ouNewParentId,
                ouTop2Id, ou3rdParentId, null, null, null }, false);

        beforeUpdateTimestamp = getNowAsTimestamp();
        toBeUpdatedXml =
            toString(toBeUpdatedDocument, false).replaceAll(
                SREL_PREFIX_TEMPLATES, SREL_PREFIX_ESCIDOC);
        updatedXml = null;
        try {
            updatedXml = update(ouChild2ParentsId, toBeUpdatedXml);
        }
        catch (Exception e) {
            failException("Updating parent ous failed.", e);
        }
        assertOrganizationalUnit(updatedXml, toBeUpdatedXml, startTimestamp,
            beforeUpdateTimestamp);
    }

    /**
     * Test successfully update of an organizational unit. The name of the
     * organizational unit will be updated.
     * 
     * @test.name Update Organizational Unit - Success
     * @test.id OUM_UOU-1-e
     * @test.input
     *          <ul>
     *          <li>Id of existing organizational unit</li>
     *          <li>Valid Organizational Unit XML representation to update the
     *          addressed organizational unit</li>
     *          </ul>
     * @test.expected: The expected result is the XML representation of the
     *                 created OrganizationalUnit, corresponding to XML-schema
     *                 "organizational-unit.xsd" including generated id, creator
     *                 and creation date
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUou1e() throws Exception {

        // create first top level ou
        final String ouXml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument = getDocument(ouXml);
        final String ouId = getObjidValue(toBeUpdatedDocument);
        final String ouTitle =
            selectSingleNodeAsserted(getDocument(ouXml),
                XPATH_ORGANIZATIONAL_UNIT_TITLE).getTextContent();

        final String ouTitleToUpdate = getUniqueName(ouTitle);
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE,
            ouTitleToUpdate);

        final String ouDescription =
            selectSingleNodeAsserted(getDocument(ouXml),
                XPATH_ORGANIZATIONAL_UNIT_DESCRIPTION).getTextContent();
        final String ouDescriptionToUpdate = getUniqueName(ouDescription);
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_DESCRIPTION,
            ouDescriptionToUpdate);

        String ou = update(ouId, toString(toBeUpdatedDocument, false));

        // check if updated organizational-unit contains new title
        final String updatedTitle =
            selectSingleNodeAsserted(getDocument(ou),
                XPATH_ORGANIZATIONAL_UNIT_TITLE).getTextContent();
        assertEquals(ouTitleToUpdate, updatedTitle);
        // check if updated organizational-unit contains new title
        final String updatedDescription =
            selectSingleNodeAsserted(getDocument(ou),
                XPATH_ORGANIZATIONAL_UNIT_DESCRIPTION).getTextContent();
        assertEquals(ouDescriptionToUpdate, updatedDescription);

        // check if title was also changed in resource index
        TripleStoreTestsBase tripleStore = new TripleStoreTestsBase();
        String result =
            tripleStore.requestMPT("<info:fedora/" + ouId + "> "
                + "<http://purl.org/dc/elements/1.1/title>" + " *", "RDF/XML");
        String riTitle =
            selectSingleNodeAsserted(getDocument(result),
                XPATH_TRIPLE_STORE_OU_TITLE).getTextContent();
        assertEquals(ouTitleToUpdate, riTitle);

        // check if description was also changed in resource index
        result =
            tripleStore.requestMPT("<info:fedora/" + ouId + "> "
                + "<http://purl.org/dc/elements/1.1/description>" + " *",
                "RDF/XML");
        String riDescription =
            selectSingleNodeAsserted(getDocument(result),
                XPATH_TRIPLE_STORE_OU_DESCRIPTION).getTextContent();
        assertEquals(ouDescriptionToUpdate, riDescription);
    }

    /**
     * Test declining update of an organizational unit with references to parent
     * organizational units, which causes cycles in organizational units
     * hierarchy.
     * 
     * @test.name Update Organizational Unit - Cycle In Hierarchy
     * @test.id OUM_UOU-2-a
     * @test.input
     *          <ul>
     *          <li>Id of existing organizational unit</li>
     *          <li>Valid Organizational Unit XML representation to update the
     *          addressed organizational unit, but updated parent-ous causes
     *          cycle in organizational units hierarchy</li>
     *          </ul>
     * @test.expected: OrganizationalUnitHierarchyViolationException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUou2a() throws Exception {

        Class<OrganizationalUnitHierarchyViolationException> ec =
            OrganizationalUnitHierarchyViolationException.class;

        // create parent
        final String parentXml = createSuccessfully("escidoc_ou_create.xml");
        final Document parentDocument = getDocument(parentXml);
        final String parentId = getObjidValue(parentDocument);

        // create child
        final String childXml =
            createSuccessfullyChild("escidoc_ou_create.xml",
                new String[] { parentId });
        final String childId = getObjidValue(getDocument(childXml));

        // create cycle by updating the parent
        insertParentsElement((Document) deleteElement(parentDocument,
            XPATH_ORGANIZATIONAL_UNIT_PARENTS),
            XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS,
            new String[] { childId, null }, false);

        String toBeUpdatedXml =
            toString(parentDocument, false).replaceAll(SREL_PREFIX_TEMPLATES,
                SREL_PREFIX_ESCIDOC);
        try {
            update(parentId, toBeUpdatedXml);
            failMissingException(
                "No exception occured on update with references to parents, which"
                    + "causes cycles in organizational units hierarchy.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec.getName() + " expected.", ec, e);
            return;
        }
    }

    /**
     * Test declining updating an organizational unit with providing a parent in
     * state closed.
     * 
     * @test.name Update Organizational Unit - Parent in state closed
     * @test.id OUM_UOU-2-b
     * @test.inputOrganizational Unit XML representation with a parent in state
     *                           closed.
     * @test.expected: InvalidStatusException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUou2b() throws Exception {

        createOuHierarchie();

        // create new parent for child 2
        final String ouNewParentXml =
            createSuccessfully("escidoc_ou_create.xml");
        final Document ouNewParentDocument = getDocument(ouNewParentXml);
        final String ouNewParentId = getObjidValue(ouNewParentDocument);
        open(ouNewParentId, getTheLastModificationParam(true, ouNewParentId,
            "Opened organizational unit '" + ouNewParentId + "'."));
        close(ouNewParentId, getTheLastModificationParam(true, ouNewParentId,
            "Closed organizational unit '" + ouNewParentId + "'."));

        String child2Xml = retrieve(ouChild2ParentsId);
        Document toBeUpdatedDocument = getDocument(child2Xml);

        // delete old parents and add new parent to child2
        // this is done by replacing the parent ous element
        deleteElement(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        insertParentsElement(toBeUpdatedDocument,
            XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS, new String[] { ouNewParentId,
                null }, false);

        String toBeUpdatedXml =
            toString(toBeUpdatedDocument, false).replaceAll(
                SREL_PREFIX_TEMPLATES, SREL_PREFIX_ESCIDOC);

        final Class<InvalidStatusException> ec = InvalidStatusException.class;
        try {
            update(ouChild2ParentsId, toBeUpdatedXml);
            failMissingException(ec);
        }
        catch (Exception e) {
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
    // catch (Exception e) {
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
    // catch (Exception e) {
    // assertExceptionType(ec, e);
    // }
    // }
    /**
     * Test declining update of an organizational unit with non existing id.
     * 
     * @test.name Update Organizational Unit - Unknown id
     * @test.id OUM-UOU-2-e
     * @test.input
     *          <ul>
     *          <li>Non existing id</li>
     *          <li> Organizational Unit XML representation</li>
     *          </ul>
     * @test.expected: OrganizationalUnitNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUou2e() throws Exception {

        final Class<OrganizationalUnitNotFoundException> ec =
            OrganizationalUnitNotFoundException.class;

        final String toBeUpdatedXml =
            createSuccessfully("escidoc_ou_create.xml");

        try {
            update(UNKNOWN_ID, toBeUpdatedXml);
            failMissingException(
                "No exception occured on update with non existing id.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining update of an organizational unit with id of a resource of
     * another type.
     * 
     * @test.name Update Organizational Unit - Id of another resource type
     * @test.id OUM-UOU-2-f
     * @test.input
     *          <ul>
     *          <li>Existing id of a resource of another resource type</li>
     *          <li>Organizational Unit XML representation</li>
     *          </ul>
     * @test.expected: OrganizationalUnitNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUou2f() throws Exception {

        final Class<OrganizationalUnitNotFoundException> ec =
            OrganizationalUnitNotFoundException.class;

        final String toBeUpdatedXml =
            createSuccessfully("escidoc_ou_create.xml");

        try {
            update(CONTEXT_ID, toBeUpdatedXml);
            failMissingException(
                "No exception occured on update with id of resoure of"
                    + " another type.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining update of an organizational unit with with missing id.
     * 
     * @test.name Update Organizational Unit - Missing id
     * @test.id OUM-UOU-2-g
     * @test.input
     *          <ul>
     *          <li>Empty id</li>
     *          <li>Organizational Unit XML representation</li>
     *          </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUou2g() throws Exception {

        final Class<MissingMethodParameterException> ec =
            MissingMethodParameterException.class;

        final String toBeUpdatedXml =
            createSuccessfully("escidoc_ou_create.xml");

        try {
            update(null, toBeUpdatedXml);
            failMissingException(
                "No exception occured on update with missing id.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining update of an organizational unit with with missing
     * organizational unit xml.
     * 
     * @test.name Update Organizational Unit - Missing organizational unit xml
     * @test.id OUM-UOU-2-h
     * @test.input
     *          <ul>
     *          <li>Empty id</li>
     *          <li>Organizational Unit XML representation</li>
     *          </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUou2h() throws Exception {

        final Class<MissingMethodParameterException> ec =
            MissingMethodParameterException.class;

        final String toBeUpdatedXml =
            createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(toBeUpdatedXml);

        try {
            update(id, null);
            failMissingException(
                "No exception occured on update with missing ou xml.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining update of an organizational unit with wrong last
     * modification date (too old).
     * 
     * @test.name Update Organizational Unit - Optimistic locking error - Too
     *            Old
     * @test.id OUM-UOU-7-a
     * @test.input
     *          <ul>
     *          <li>Valid id of organizational unit</li>
     *          <li>Organizational Unit XML representation with set last
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
    public void testOumUou7a() throws Exception {

        Class<OptimisticLockingException> ec = OptimisticLockingException.class;

        final String toBeUpdatedXml =
            createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument =
            (Document) substitute(getDocument(toBeUpdatedXml),
                XPATH_ORGANIZATIONAL_UNIT_LAST_MODIFICATION_DATE,
                "2005-01-30T11:36:42.015Z");
        final String id = getObjidValue(toBeUpdatedDocument);

        try {
            update(id, toString(toBeUpdatedDocument, false));
            failMissingException(
                "No exception occured on update with wrong time stamp.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining update of an organizational unit with wrong last
     * modification date (more recent).
     * 
     * @test.name Update Organizational Unit - Optimistic locking error - More
     *            recent
     * @test.id OUM-UOU-7-b
     * @test.input
     *          <ul>
     *          <li>Valid id of organizational unit</li>
     *          <li>Organizational Unit XML representation with set last
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
    public void testOumUou7b() throws Exception {

        Class<OptimisticLockingException> ec = OptimisticLockingException.class;

        final String toBeUpdatedXml =
            createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument =
            (Document) substitute(getDocument(toBeUpdatedXml),
                XPATH_ORGANIZATIONAL_UNIT_LAST_MODIFICATION_DATE,
                getNowAsTimestamp());
        final String id = getObjidValue(toBeUpdatedDocument);

        try {
            update(id, toString(toBeUpdatedDocument, false));
            failMissingException(
                "No exception occured on update with wrong time stamp.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining update of an organizational unit with corrupt xml.
     * 
     * @test.name Update Organizational Unit - Corrupt Xml
     * @test.id OUM_UOU-8-a
     * @test.input Corrupted Xml.
     * @test.expected: XmlCorruptedException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUou8a() throws Exception {

        final Class<XmlCorruptedException> ec = XmlCorruptedException.class;

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument = getDocument(createdXml);
        final String id = getObjidValue(toBeUpdatedDocument);
        deleteElement(toBeUpdatedDocument,
            XPATH_ORGANIZATIONAL_UNIT_CREATION_DATE);
        try {
            update(id, "<org");
            failMissingException(
                "No exception occured on update with corrupted xml.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Tests successfully updating an organizational-unit with changing the
     * namespace prefixes.
     * 
     * @test.name Update Organizational Unit - Changed Nanespace Prefixes
     * @test.id OUM-UOU-9-a
     * @test.input Organizational Unit XML representation with changed
     *             namespaces prefixes.
     * @test.expected: Exception
     * @test.status Implemented
     * @test.issues http://www.escidoc-project.de/issueManagement/show_bug.cgi?id=308
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUou9a() throws Exception {

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument = getDocument(createdXml);
        final String id = getObjidValue(toBeUpdatedDocument);

        final String toBeUpdatedXml =
            modifyNamespacePrefixes(toString(toBeUpdatedDocument, false));
        assertXmlValidOrganizationalUnit(toBeUpdatedXml);

        final String beforeUpdateTimestamp = getNowAsTimestamp();
        String updatedXml = null;
        try {
            updatedXml = update(id, toBeUpdatedXml);
        }
        catch (Exception e) {
            failException(
                "Updating OU with set new external id failed with exception. ",
                e);
        }
        assertOrganizationalUnit(updatedXml, toBeUpdatedXml, startTimestamp,
            beforeUpdateTimestamp);
    }

    /**
     * Test declining updating a top level organizational unit with setting a
     * non unique name.
     * 
     * @test.name Update Organizational Unit - Duplicate Name of Top Level OU
     * @test.id OUM_UOU-5-a
     * @test.input Organizational Unit XML representation of a top level
     *             Organizational unit containing a name of an organizational
     *             unit that just exists for another top level ou.
     * @test.expected: OrganizationalUnitNameNotUniqueException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void tesOumUou5a() throws Exception {

        final Class<OrganizationalUnitNameNotUniqueException> ec =
            OrganizationalUnitNameNotUniqueException.class;

        // create first top level ou
        final String ou1Xml = createSuccessfully("escidoc_ou_create.xml");
        final String ou1Name =
            selectSingleNodeAsserted(getDocument(ou1Xml),
                XPATH_ORGANIZATIONAL_UNIT_TITLE).getTextContent();

        // create second top level ou
        final String ou2Xml = createSuccessfully("escidoc_ou_create.xml");

        // update name of second top level ou to name of first top level ou
        final Document toBeUpdatedDocument = getDocument(ou2Xml);
        final String ou2Id = getObjidValue(toBeUpdatedDocument);
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE,
            ou1Name);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        try {
            update(ou2Id, toBeUpdatedXml);
            failMissingException(ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }

    }

    /**
     * Test declining updating a top level organizational unit with setting an
     * empty name.
     * 
     * @test.name Update Organizational Unit - Empty Name
     * @test.id OUM_UOU-5-b
     * @test.input Organizational Unit XML representation of a top level
     *             Organizational unit containing an empty name element.
     * @test.expected: OrganizationalUnitNameNotUniqueException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUou5b() throws Exception {

        final Class<MissingElementValueException> ec =
            MissingElementValueException.class;

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
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

//    /**
//     * Test declining updating an organizational unit with setting a non unique
//     * name in the scope of the parents.
//     * 
//     * @test.name Update Organizational Unit - Update Name with Duplicate Name
//     *            in Scope of Parents
//     * @test.id OUM_UOU-5-c
//     * @test.input Organizational Unit XML representation with updating the name
//     *             to the value of the name of an organizational unit that just
//     *             exists in the scope of the parents.
//     * @test.expected: OrganizationalUnitNameNotUniqueException
//     * @test.status Implemented
//     * 
//     * @throws Exception
//     *             If anything fails.
//     */
//    public void testOumUou5c() throws Exception {
//
//        Class<OrganizationalUnitNameNotUniqueException> ec =
//            OrganizationalUnitNameNotUniqueException.class;
//
//        // create parent
//        final String topLevelId =
//            createSuccessfully("escidoc_ou_create.xml", 1)[0];
//
//        // create first child
//        final String child1Xml =
//            createSuccessfullyChild("escidoc_ou_create.xml",
//                new String[] { topLevelId });
//        final String child1Name =
//            selectSingleNodeAsserted(getDocument(child1Xml),
//                XPATH_ORGANIZATIONAL_UNIT_TITLE).getTextContent();
//
//        // create second child
//        final String child2Xml =
//            createSuccessfullyChild("escidoc_ou_create.xml",
//                new String[] { topLevelId });
//
//        // update name of second child ou to name of first child ou
//        final Document toBeUpdatedDocument = getDocument(child2Xml);
//        final String child2Id = getObjidValue(toBeUpdatedDocument);
//        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE,
//            child1Name);
//
//        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
//
//        try {
//            update(child2Id, toBeUpdatedXml);
//            failMissingException(ec);
//        }
//        catch (Exception e) {
//            assertExceptionType(ec, e);
//        }
//    }

    /**
     * Test sucessfully updating an organizational unit with a name of an
     * existing organizational unit in another scope of the parents.
     * 
     * @test.name Update Organizational Unit - Update Name with Duplicate Name
     *            in different Scopes of Parents
     * @test.id OUM_UOU-5-d
     * @test.input Organizational Unit XML representation containing a name of
     *             an organizational unit that just exists, but that is not in
     *             the scope of the parents of the organizational unit to be
     *             created.
     * @test.expected: The expected result is the XML representation of the
     *                 created OrganizationalUnit, corresponding to XML-schema
     *                 "organizational-unit.xsd" including generated id, creator
     *                 and creation date
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUou5d() throws Exception {

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

        // update name of second child ou to name of first child ou
        final Document toBeUpdatedDocument = getDocument(child2Xml);
        final String child2Id = getObjidValue(toBeUpdatedDocument);
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE,
            child1Name);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
        final String beforeUpdateTimestamp = getNowAsTimestamp();
        String updatedXml = null;
        try {
            updatedXml = update(child2Id, toBeUpdatedXml);
        }
        catch (Exception e) {
            failException(
                "Updating OU with name of ou in another scope failed.", e);
        }
        assertOrganizationalUnit(updatedXml, toBeUpdatedXml, startTimestamp,
            beforeUpdateTimestamp);
    }

    // /**
    // * Test declining updating an organizational unit with setting a non
    // unique
    // * name in the scope of the parents.
    // *
    // * @test.name Update Organizational Unit - Duplicate Name in Scope of
    // * Parents
    // * @test.id OUM_UOU-5-e
    // * @test.input Organizational Unit XML representation containing a name of
    // * an organizational unit that just exists in the scope of the
    // * parents.
    // * @test.expected: OrganizationalUnitNameNotUniqueException
    // * @test.status Implemented
    // *
    // * @throws Exception
    // * If anything fails.
    // */
    // public void testOumUou5e() throws Exception {
    //
    // final Class<OrganizationalUnitNameNotUniqueException> ec =
    // OrganizationalUnitNameNotUniqueException.class;
    //
    // createOuHierarchie();
    //
    // // create ou with same name as ouId1
    // Document ouChild2Document =
    // getTemplateAsDocument(TEMPLATE_ORGANIZATIONAL_UNIT_PATH,
    // "escidoc_ou_create.xml");
    // substitute(ouChild2Document, XPATH_ORGANIZATIONAL_UNIT_TITLE,
    // ouChild1ParentName);
    // String toBeCreatedChild2Xml = toString(ouChild2Document, false);
    // final String createdChild2Xml = create(toBeCreatedChild2Xml);
    //
    // // add created ou to ouIdTop using update. ouIdTop has ouId1 as child.
    // final Document toBeUpdatedDocument = getDocument(createdChild2Xml);
    // final String objid = getObjidValue(toBeUpdatedDocument);
    // insertParentsElement((Document) deleteElement(toBeUpdatedDocument,
    // XPATH_ORGANIZATIONAL_UNIT_PARENTS),
    // XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS,
    // new String[] { ouTop1Id, null }, false);
    //
    // final String toBeUpdatedXml =
    // toString(toBeUpdatedDocument, false).replaceAll(
    // SREL_PREFIX_TEMPLATES, SREL_PREFIX_ESCIDOC);
    //
    // try {
    // update(objid, toBeUpdatedXml);
    // failMissingException(ec);
    // }
    // catch (Exception e) {
    // assertExceptionType(ec, e);
    // }
    // }

    /**
     * Test sucessfully updating an organizational unit with moving to another
     * scope (top-level) and setting the name of an existing organizational unit
     * in the previous scope of the parents.
     * 
     * @test.name Update Organizational Unit - Update Name with Duplicate Name
     *            in Previous Scopes of Parents
     * @test.id OUM_UOU-5-f
     * @test.input Valid XML representation of an organizational unit for
     *             updating with:
     *             <ul>
     *             <li>removing parent ou</li>
     *             <li>changing name to the name of an Organizational unit that
     *             exists in the previous scope of parents.</li>
     *             </ul>
     * @test.expected: The expected result is the XML representation of the
     *                 created OrganizationalUnit, corresponding to XML-schema
     *                 "organizational-unit.xsd" including generated id, creator
     *                 and creation date
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumUou5f() throws Exception {

        // create parent
        final String topLevelId =
            createSuccessfully("escidoc_ou_create.xml", 1)[0];

        // create first child
        final String child1Xml =
            createSuccessfullyChild("escidoc_ou_create.xml",
                new String[] { topLevelId });
        final String child1Name =
            selectSingleNodeAsserted(getDocument(child1Xml),
                XPATH_ORGANIZATIONAL_UNIT_TITLE).getTextContent();

        // create second child
        final String child2Xml =
            createSuccessfullyChild("escidoc_ou_create.xml",
                new String[] { topLevelId });

        // remove parent of second child (making it a top-level ou) and set the
        // name of the first child.
        final Document toBeUpdatedDocument = getDocument(child2Xml);
        final String child2Id = getObjidValue(toBeUpdatedDocument);
        substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE,
            child1Name);
        deleteNodes(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_PARENTS);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
        final String beforeUpdateTimestamp = getNowAsTimestamp();
        String updatedXml = null;
        try {
            updatedXml = update(child2Id, toBeUpdatedXml);
        }
        catch (Exception e) {
            failException(
                "Updating OU with removed parents and set name of ou in"
                    + " previous scope failed.", e);
        }
        assertOrganizationalUnit(updatedXml, toBeUpdatedXml, startTimestamp,
            beforeUpdateTimestamp);
    }

}
