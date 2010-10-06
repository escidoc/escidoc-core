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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import de.escidoc.core.test.common.client.servlet.Constants;

/**
 * Test the implementation of the organizational-unit resource.
 * 
 * @author MSC
 * 
 * 
 */
@RunWith(value = Parameterized.class)
public class UpdateParentsTest extends OrganizationalUnitTestBase {

    /**
     * @param transport
     *            The transport identifier.
     */
    public UpdateParentsTest(final int transport) {
        super(transport);
    }

    /**
     * Test successfully updating parent relation of an Organizational Unit by
     * using the general update method.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testUpdateParentRelation01() throws Exception {

        // prepare
        // create OU 1
        String ou1xml = createSuccessfully("escidoc_ou_create.xml");
        String ou1id = getObjidValue(getDocument(ou1xml));

        // create OU 2
        String ou2xml = createSuccessfully("escidoc_ou_create.xml");
        String ou2id = getObjidValue(getDocument(ou2xml));

        Document ou2doc = getDocument(ou2xml);

        Element parent =
            ou2doc.createElementNS(
                "http://escidoc.de/core/01/structural-relations/",
                "srel:parent");

        if (getTransport() == Constants.TRANSPORT_SOAP) {
            parent.setAttribute("objid", ou1id);
        }
        else {
            parent.setAttribute("xlink:href", "/oum/organizational-unit/"
                + ou1id);
        }

        Node parents =
            selectSingleNode(ou2doc, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        parents.appendChild(parent);

        String tmp = toString(ou2doc, true);
        ou2xml = update(ou2id, tmp);
        ou2doc = getDocument(ou2xml);

        // assert updated values
        if (getTransport() == Constants.TRANSPORT_SOAP) {
            assertXmlExists("Missing one parent", ou2doc,
                XPATH_ORGANIZATIONAL_UNIT_PARENT + "[@objid='" + ou1id + "']");
        }
        else {
            assertXmlExists("Missing one parent", ou2doc,
                XPATH_ORGANIZATIONAL_UNIT_PARENT
                    + "[@href='/oum/organizational-unit/" + ou1id + "']");
        }
        assertXmlExists("Wrong number of parents", ou2doc,
            XPATH_ORGANIZATIONAL_UNIT_PARENTS + "[count(./parent)='1']");
    }

    /**
     * Test successfully updating parent relation of an Organizational Unit by
     * using the general update method.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testUpdateParentRelation02() throws Exception {

        // prepare
        // create OU 1
        String ou1xml = createSuccessfully("escidoc_ou_create.xml");
        String ou1id = getObjidValue(getDocument(ou1xml));

        // create OU 2
        String ou2xml = createSuccessfully("escidoc_ou_create.xml");
        String ou2id = getObjidValue(getDocument(ou2xml));

        String parentsXml = retrieveParents(ou2id);
        Document ou2parents = getDocument(parentsXml);

        Element parent =
            ou2parents.createElementNS(
                "http://escidoc.de/core/01/structural-relations/",
                "srel:parent");

        if (getTransport() == Constants.TRANSPORT_SOAP) {
            parent.setAttribute("objid", ou1id);
        }
        else {
            parent.setAttribute("xlink:href", "/oum/organizational-unit/"
                + ou1id);
        }

        Node parents = selectSingleNode(ou2parents, "/parents");
        parents.appendChild(parent);

        String tmp = toString(ou2parents, true);
        String ou2parentsXml = updateParentOus(ou2id, tmp);
        ou2parents = getDocument(ou2parentsXml);

        // assert updated values
        if (getTransport() == Constants.TRANSPORT_SOAP) {
            assertXmlExists("Missing one parent", ou2parents,
                "/parents/parent[@objid='" + ou1id + "']");
        }
        else {
            assertXmlExists("Missing one parent", ou2parents,
                "/parents/parent[@href='/oum/organizational-unit/" + ou1id
                    + "']");
        }
        assertXmlExists("Wrong number of parents", ou2parents,
            "/parents[count(./parent)='1']");
    }

    /**
     * Test declining updating the parents sub resource of an organizational
     * unit with non existing id.
     * 
     * @test.name Update Parent Ous - Unknown id
     * @test.id OUM-UPOU-2-1
     * @test.input <ul>
     *             <li>Non existing id</li>
     *             <li>Parent Ous XML representation</li>
     *             </ul>
     * @test.expected: OrganizationalUnitNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void test_OUM_UPOU_2_1() throws Exception {

        final Class<OrganizationalUnitNotFoundException> ec =
            OrganizationalUnitNotFoundException.class;
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(createdXml);
        final String toBeUpdatedXml = retrieveParents(id);
        try {
            updateParentOus(UNKNOWN_ID, toBeUpdatedXml);

            failMissingException(
                "No exception occured on update of parents with non existing id.",
                ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating of parents of an ou in status opened.
     */
    @Test
    public void test_UpdateParentsOfOuInStatusOpened() throws Exception {
        final Class<InvalidStatusException> ec = InvalidStatusException.class;
        final String createdNewParentXml =
            createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(createdNewParentXml);
        createOuHierarchie();
        open(this.ouTop1Id, getTheLastModificationParam(true, this.ouTop1Id,
            "Opened organizational unit '" + this.ouTop1Id + "'."));
        open(this.ouChild1ParentId, getTheLastModificationParam(true,
            this.ouChild1ParentId, "Opened organizational unit '"
                + this.ouChild1ParentId + "'."));

        final String createdXml = retrieve(this.ouChild1ParentId);
        Document createdDocument = getDocument(createdXml);
        if (getTransport() == Constants.TRANSPORT_REST) {
            substitute(createdDocument,
                XPATH_ORGANIZATIONAL_UNIT_PARENT_XLINK_HREF,
                "/ir/organizational-unit/" + id);
        }
        else if (getTransport() == Constants.TRANSPORT_SOAP) {
            substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_PARENT_OBJID,
                id);
        }
        String toBeUpdatedXml = toString(createdDocument, true);
        try {
            update(this.ouChild1ParentId, toBeUpdatedXml);

            failMissingException(
                "No exception occured on update of parents of an OU "
                    + "in status opened.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the parents sub resource of an organizational
     * with id of a resource of another type.
     * 
     * @test.name Update Parent Ous - Id of another resource type
     * @test.id OUM-UPOU-2-2
     * @test.input <ul>
     *             <li>Existing id of a resource of another resource type</li>
     *             <li>Parent Ous XML representation</li>
     *             </ul>
     * @test.expected: OrganizationalUnitNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void test_OUM_UPOU_2_2() throws Exception {

        final Class<OrganizationalUnitNotFoundException> ec =
            OrganizationalUnitNotFoundException.class;

        final String ouXml = createSuccessfully("escidoc_ou_create.xml");

        try {
            updateParentOus(CONTEXT_ID, retrieveParents(getObjidValue(ouXml)));
            failMissingException(
                "No exception occured on update with id of resoure of"
                    + " another type.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the parents sub resource of an organizational
     * with wrong last modification date (too old).
     * 
     * @test.name Update Parent Ous - Optimistic locking error - Too Old
     * @test.id OUM-UPOU-3-1
     * @test.input <ul>
     *             <li>Valid id of organizational unit</li>
     *             <li>Parent Ous XML representation with set last modification
     *             date that is older than the last modification date of the
     *             organizational unit</li>
     *             </ul>
     * @test.expected: OptimisticLockingException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void test_OUM_UPOU_3_1() throws Exception {

        Class<OptimisticLockingException> ec = OptimisticLockingException.class;
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(createdXml);
        final String toBeUpdatedXml = retrieveParents(id);

        final Document toBeUpdatedDocument =
            (Document) substitute(getDocument(toBeUpdatedXml), XPATH_PARENTS
                + "/" + PART_LAST_MODIFICATION_DATE, "2005-01-30T11:36:42.015Z");
        try {
            updateParentOus(id, toString(toBeUpdatedDocument, false));
            failMissingException(
                "No exception occured on update with wrong time stamp.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the parents sub resource of an organizational
     * with wrong last modification date (more recent).
     * 
     * @test.name Update Parent Ous - Optimistic locking error - More recent
     * @test.id OUM-UPOU-3-2
     * @test.input <ul>
     *             <li>Valid id of organizational unit</li>
     *             <li>Parent Ous XML representation with set last modification
     *             date that is more recent than the last modification date of
     *             the organizational unit</li>
     *             </ul>
     * @test.expected: OptimisticLockingException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void test_OUM_UPOU_3_2() throws Exception {

        Class<OptimisticLockingException> ec = OptimisticLockingException.class;
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(createdXml);
        final String toBeUpdatedXml = retrieveParents(id);

        final Document toBeUpdatedDocument =
            (Document) substitute(getDocument(toBeUpdatedXml), XPATH_PARENTS
                + "/" + PART_LAST_MODIFICATION_DATE, getNowAsTimestamp());

        try {
            updateParentOus(id, toString(toBeUpdatedDocument, false));
            failMissingException(
                "No exception occured on update with wrong time stamp.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the parents sub resource of an organizational
     * with missing id.
     * 
     * @test.name Update Parent Ous - Missing method parameter error - Missing
     *            id
     * @test.id OUM-UPOU-4-1
     * @test.input <ul>
     *             <li>Missing id of organizational unit</li>
     *             </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void test_OUM_UPOU_4_1() throws Exception {

        final Class<MissingMethodParameterException> ec =
            MissingMethodParameterException.class;

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(createdXml);
        final String toBeUpdatedXml = retrieveParents(id);

        try {
            updateParentOus(null, toBeUpdatedXml);
            failMissingException(
                "No exception occured on update with missing id.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the parents sub resource of an organizational
     * with missing parents xml.
     * 
     * @test.name Update Parent Ous - Missing method parameter error - Missing
     *            parents
     * @test.id OUM-UPOU-4-2
     * @test.input <ul>
     *             <li>Valid id of organizational unit</li>
     *             <li>Missing Parent Ous XML representation</li>
     *             </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void test_OUM_UPOU_4_2() throws Exception {

        final Class<MissingMethodParameterException> ec =
            MissingMethodParameterException.class;

        final String toBeUpdatedXml =
            createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(toBeUpdatedXml);

        try {
            updateParentOus(id, null);

            failMissingException("No exception occured on update with missing "
                + "organization-details xml.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the parents sub resource of an organizational
     * with corrupt xml.
     * 
     * @test.name Update Parent Ous - Corrupt Xml
     * @test.id OUM-UPOU-5-1
     * @test.input Corrupted Xml.
     * @test.expected: XmlCorruptedException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void test_OUM_UPOU_5_1() throws Exception {

        final Class<XmlCorruptedException> ec = XmlCorruptedException.class;

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument = getDocument(createdXml);
        final String id = getObjidValue(toBeUpdatedDocument);
        try {
            updateParentOus(id, "<org");
            failMissingException(
                "No exception occured on update with corrupted xml.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the parents sub resource of an organizational
     * with wrong root element (organizational-unit) in xml.
     * 
     * @test.name Update Parent Ous - Invalid Xml
     * @test.id OUM-UPOU-5-2
     * @test.input Invalid Xml.
     * @test.expected: InvalidXmlException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void test_OUM_UPOU_5_2() throws Exception {

        final Class<XmlCorruptedException> ec = XmlCorruptedException.class;

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument = getDocument(createdXml);
        final String id = getObjidValue(toBeUpdatedDocument);
        try {
            updateParentOus(id, createdXml);
            failMissingException(
                "No exception occured on update with corrupted xml.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the parents sub resource of an organizational
     * with wrong root element (<unknown/>) in xml.
     * 
     * @test.name Update Parent Ous - Invalid Xml
     * @test.id OUM-UPOU-5-3
     * @test.input Invalid Xml.
     * @test.expected: InvalidXmlException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void test_OUM_UPOU_5_3() throws Exception {

        final Class<XmlCorruptedException> ec = XmlCorruptedException.class;

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument = getDocument(createdXml);
        final String id = getObjidValue(toBeUpdatedDocument);
        try {
            updateParentOus(id, "<unknown/>");
            failMissingException(
                "No exception occured on update with corrupted xml.", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    // ========================================================================
    // /**
    // * Test declining updating an organizational unit setting confusing data
    // in
    // * reference to a parent.
    // *
    // * @test.name Organizational Unit - With Parents
    // * @test.id UpdateParentOusWithConfusingData
    // * @test.input Parent Ous XML representation with confusing
    // * data.
    // * @test.expected: InvalidContentExcetion
    // * @test.status Rejected
    // *
    // * @throws Exception
    // * If anything fails.
    // */
    //
    // /**
    // * Tests successfully updating an organizational unit with set but
    // unchanged
    // * external id.
    // *
    // * @test.name Update Parent Ous - Unchanged External Id
    // * @test.id OUM-UOU-2-2
    // * @test.input Parent Ous XML representation with changed pid
    // * @test.expected: Parent Ous XML representation with set
    // * external-id
    // * @test.status Implemented
    // *
    // * @throws Exception
    // */
    // public void notest_OUM_UPOU_2_2() throws Exception {
    //
    // // create ou with set external id
    // final String createdXml =
    // createSuccessfully("escidoc_ou_create_with_external_id.xml");
    // final Document toBeUpdatedDocument = getDocument(createdXml);
    // final String id = getObjidValue(toBeUpdatedDocument);
    //
    // // change something to really update it, but do not change external id
    // substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_ABBREVIATION,
    // "http://new.uri");
    //
    // final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
    //
    // String beforeUpdateTimestamp = getNowAsTimestamp();
    // String updatedXml = null;
    // try {
    // updatedXml = updateParentOus(id, toBeUpdatedXml);
    // }
    // catch (Exception e) {
    // failException("Updating OU with set but unchanged external id"
    // + " failed. ", e);
    // }
    // assertOrganizationalUnit(updatedXml, toBeUpdatedXml, startTimestamp,
    // beforeUpdateTimestamp);
    // }
    //
    // // /**
    // // * Tests declining updating the external id.
    // // *
    // // * @test.name Update Parent Ous - Changed External Id
    // // * @test.id OUM-UOU-2-3
    // // * @test.input Parent Ous XML representation with changed
    // pid
    // // * @test.expected: ReadonlyElementViolationException
    // // * @test.status Implemented
    // // *
    // // * @throws Exception
    // // */
    // // public void notest_OUM_UPOU_2_3() throws Exception {
    // //
    // // final Class ec = PidAlreadyAssignedException.class;
    // //
    // // final String createdXml =
    // // createSuccessfully("escidoc_ou_create_with_external_id.xml");
    // // final Document toBeUpdatedDocument = getDocument(createdXml);
    // // final String id = getObjidValue(toBeUpdatedDocument);
    // //
    // // // change external id (by making it unique)
    // // setUniqueValue(toBeUpdatedDocument,
    // // XPATH_ORGANIZATIONAL_UNIT_EXTERNAL_ID);
    // //
    // // try {
    // // final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
    // // updateParentOus(id, toBeUpdatedXml);
    // // failMissingException(ec);
    // // }
    // // catch (Exception e) {
    // // assertExceptionType(ec, e);
    // // }
    // // }
    //
    // // /**
    // // * Tests declining updating the external id twice.
    // // *
    // // * @test.name Update Parent Ous - Changed External Id
    // // * @test.id OUM-UOU-2-4
    // // * @test.input Parent Ous XML representation with changed
    // pid
    // // * @test.expected: ReadonlyElementViolationException
    // // * @test.status Implemented
    // // *
    // // * @throws Exception
    // // */
    // // public void notest_OUM_UPOU_2_4() throws Exception {
    // //
    // // final Class ec = PidAlreadyAssignedException.class;
    // //
    // // // create ou without set external id
    // // final String createdXml = createSuccessfully("escidoc_ou_create.xml");
    // // final Document toBeUpdatedDocument = getDocument(createdXml);
    // // final String id = getObjidValue(toBeUpdatedDocument);
    // //
    // // // insert external-id
    // // addAfter(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_DESCRIPTION,
    // // createElementNode(toBeUpdatedDocument, ORGANIZATIONAL_UNIT_NS_URI,
    // // ORGANIZATIONAL_UNIT_PREFIX_ESCIDOC, NAME_EXTERNAL_ID, "12345"));
    // //
    // // final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
    // //
    // // String updatedXml = null;
    // // try {
    // // updatedXml = updateParentOus(id, toBeUpdatedXml);
    // // }
    // // catch (Exception e) {
    // // failException("Updating ou with setting external-id failed.", e);
    // // }
    // //
    // // final Document toBeUpdatedDocument2 = getDocument(updatedXml);
    // //
    // // // change external id (by making it unique)
    // // setUniqueValue(toBeUpdatedDocument2,
    // // XPATH_ORGANIZATIONAL_UNIT_EXTERNAL_ID);
    // //
    // // final String toBeUpdatedXml2 = toString(toBeUpdatedDocument2, false);
    // //
    // // try {
    // // updateParentOus(id, toBeUpdatedXml2);
    // // failMissingException(ec);
    // // }
    // // catch (Exception e) {
    // // assertExceptionType(ec, e);
    // // }
    // // }
    //
    // /**
    // * Test declining updating a top level organizational unit with setting a
    // * non unique name.
    // *
    // * @test.name Update Parent Ous - Duplicate Name of Top Level
    // OU
    // * @test.id OUM_UOD-5-1
    // * @test.input Parent Ous XML representation of a top level
    // * Organizational unit containing a name of an organizational
    // * unit that just exists for another top level ou.
    // * @test.expected: OrganizationalUnitNameNotUniqueException
    // * @test.status Implemented
    // *
    // * @throws Exception
    // * If anything fails.
    // */
    // public void notest_OUM_UPOU_5_1() throws Exception {
    //
    // final Class ec = OrganizationalUnitNameNotUniqueException.class;
    //
    // // create first top level ou
    // final String ou1Xml = createSuccessfully("escidoc_ou_create.xml");
    // final String ou1Name =
    // selectSingleNodeAsserted(getDocument(ou1Xml),
    // XPATH_ORGANIZATIONAL_UNIT_NAME).getTextContent();
    //
    // // create second top level ou
    // final String ou2Xml = createSuccessfully("escidoc_ou_create.xml");
    //
    // // update name of second top level ou to name of first top level ou
    // final Document toBeUpdatedDocument = getDocument(ou2Xml);
    // final String ou2Id = getObjidValue(toBeUpdatedDocument);
    // substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_NAME, ou1Name);
    //
    // final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
    //
    // try {
    // updateParentOus(ou2Id, toBeUpdatedXml);
    // failMissingException(ec);
    // }
    // catch (Exception e) {
    // assertExceptionType(ec, e);
    // }
    //
    // }
    //
    // /**
    // * Test declining updating a top level organizational unit with setting an
    // * empty name.
    // *
    // * @throws Exception
    // * If anything fails.
    // */
    // public void notest_OUM_UPOU_5_1a() throws Exception {
    //
    // final Class ec = MissingElementValueException.class;
    //
    // // create first top level ou
    // final String ou1Xml = createSuccessfully("escidoc_ou_create.xml");
    // final Document toBeUpdatedDocument = getDocument(ou1Xml);
    // final String ou1Id = getObjidValue(toBeUpdatedDocument);
    // substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_NAME, "");
    //
    // final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
    //
    // try {
    // updateParentOus(ou1Id, toBeUpdatedXml);
    // failMissingException(ec);
    // }
    // catch (Exception e) {
    // assertExceptionType(ec, e);
    // }
    // }
    //
    // /**
    // * Successfully update of OU name.
    // *
    // * @throws Exception
    // */
    // public void notest_OUM_UPOU_5_1b() throws Exception {
    //
    // // create first top level ou
    // final String ou1Xml = createSuccessfully("escidoc_ou_create.xml");
    // final Document toBeUpdatedDocument = getDocument(ou1Xml);
    // final String ou1Id = getObjidValue(toBeUpdatedDocument);
    // final String ou1Name =
    // selectSingleNodeAsserted(getDocument(ou1Xml),
    // XPATH_ORGANIZATIONAL_UNIT_NAME).getTextContent();
    //
    // final String newOuName = ou1Name + "-1";
    // substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_NAME,
    // newOuName);
    //
    // final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
    //
    // String ou = updateParentOus(ou1Id, toBeUpdatedXml);
    //
    // // check if ou contains new name
    // final String ouName =
    // selectSingleNodeAsserted(getDocument(ou),
    // XPATH_ORGANIZATIONAL_UNIT_NAME).getTextContent();
    // assertEquals(newOuName, ouName);
    //
    // // check if name and title was also changed in RELS-EXT
    // TripleStoreTestBase tripleStore = new TripleStoreTestBase();
    // String result =
    // tripleStore.requestMPT("<info:fedora/" + ou1Id + "> "
    // + "<http://www.escidoc.de/schemas/organizationalunit/0.3/name>"
    // + " *", "RDF/XML");
    //
    // String resultName =
    // selectSingleNodeAsserted(getDocument(result),
    // XPATH_TRIPLE_STORE_OU_NAME).getTextContent();
    //
    // assertEquals(newOuName, resultName);
    //
    // // check if changing name leads to changing title to (in DS
    // // ou-description and RELS-EXT)
    // result =
    // tripleStore.requestMPT(
    // "<info:fedora/" + ou1Id + "> "
    // + "<http://www.nsdl.org/ontologies/relationships/title>"
    // + " *", "RDF/XML");
    //
    // resultName =
    // selectSingleNodeAsserted(getDocument(result),
    // XPATH_TRIPLE_STORE_OU_TITLE).getTextContent();
    //
    // assertEquals(newOuName, resultName);
    // }
    //
    // /**
    // * Test declining updating an organizational unit with setting a non
    // unique
    // * name in the scope of the parents.
    // *
    // * @test.name Update Parent Ous - Update Name with Duplicate
    // * Name in Scope of Parents
    // * @test.id OUM_UOD-5-2
    // * @test.input Parent Ous XML representation with updating the
    // * name to the value of the name of an organizational unit that
    // * just exists in the scope of the parents.
    // * @test.expected: OrganizationalUnitNameNotUniqueException
    // * @test.status Implemented
    // *
    // * @throws Exception
    // * If anything fails.
    // */
    // public void notest_OUM_UPOU_5_2() throws Exception {
    //
    // Class ec = OrganizationalUnitNameNotUniqueException.class;
    //
    // // create parent
    // final String topLevelId =
    // createSuccessfully("escidoc_ou_create.xml", 1)[0];
    //
    // // create first child
    // final String child1Xml =
    // createSuccessfullyChild("escidoc_ou_create.xml",
    // new String[] { topLevelId });
    // final String child1Name =
    // selectSingleNodeAsserted(getDocument(child1Xml),
    // XPATH_ORGANIZATIONAL_UNIT_NAME).getTextContent();
    //
    // // create second child
    // final String child2Xml =
    // createSuccessfullyChild("escidoc_ou_create.xml",
    // new String[] { topLevelId });
    //
    // // update name of second child ou to name of first child ou
    // final Document toBeUpdatedDocument = getDocument(child2Xml);
    // final String child2Id = getObjidValue(toBeUpdatedDocument);
    // substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_NAME,
    // child1Name);
    //
    // final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
    //
    // try {
    // updateParentOus(child2Id, toBeUpdatedXml);
    // failMissingException(ec);
    // }
    // catch (Exception e) {
    // assertExceptionType(ec, e);
    // }
    // }
    //
    // /**
    // * Test sucessfully updating an organizational unit with a name of an
    // * existing organizational unit in another scope of the parents.
    // *
    // * @test.name Update Parent Ous - Update Name with Duplicate
    // * Name in different Scopes of Parents
    // * @test.id OUM_UOD-5-3
    // * @test.input Parent Ous XML representation containing a name
    // * of an organizational unit that just exists, but that is not
    // * in the scope of the parents of the organizational unit to be
    // * created.
    // * @test.expected: The expected result is the XML representation of the
    // * created OrganizationalUnit, corresponding to XML-schema
    // * "organizational unit.xsd" including generated id, creator
    // * and creation date
    // * @test.status Implemented
    // *
    // * @throws Exception
    // * If anything fails.
    // */
    // public void notest_OUM_UPOU_5_3() throws Exception {
    //
    // // create two parent ous
    // String[] parentIds = createSuccessfully("escidoc_ou_create.xml", 2);
    // final String parent1Id = parentIds[0];
    // final String parent2Id = parentIds[1];
    //
    // // create child of first parent ou
    // final String child1Xml =
    // createSuccessfullyChild("escidoc_ou_create.xml",
    // new String[] { parent1Id });
    // final String child1Name =
    // selectSingleNodeAsserted(getDocument(child1Xml),
    // XPATH_ORGANIZATIONAL_UNIT_NAME).getTextContent();
    //
    // // create child of second parent ou
    // final String child2Xml =
    // createSuccessfullyChild("escidoc_ou_create.xml",
    // new String[] { parent2Id });
    // final String child2Name =
    // selectSingleNodeAsserted(getDocument(child1Xml),
    // XPATH_ORGANIZATIONAL_UNIT_NAME).getTextContent();
    //
    // // update name of second child ou to name of first child ou
    // final Document toBeUpdatedDocument = getDocument(child2Xml);
    // final String child2Id = getObjidValue(toBeUpdatedDocument);
    // substitute(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_NAME,
    // child1Name);
    //
    // final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
    // final String beforeUpdateTimestamp = getNowAsTimestamp();
    // String updatedXml = null;
    // try {
    // updatedXml = updateParentOus(child2Id, toBeUpdatedXml);
    // }
    // catch (Exception e) {
    // failException(
    // "Updating OU with name of ou in another scope failed.", e);
    // }
    // assertOrganizationalUnit(updatedXml, toBeUpdatedXml, startTimestamp,
    // beforeUpdateTimestamp);
    // }

}
