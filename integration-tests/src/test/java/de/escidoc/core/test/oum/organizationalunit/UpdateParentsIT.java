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
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Test the implementation of the organizational-unit resource.
 *
 * @author Michael Schneider
 */
public class UpdateParentsIT extends OrganizationalUnitTestBase {

    /**
     * Test successfully updating parent relation of an Organizational Unit by using the general update method.
     *
     * @throws Exception If anything fails.
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

        Element parent = ou2doc.createElementNS("http://escidoc.de/core/01/structural-relations/", "srel:parent");
        parent.setAttribute("xlink:href", "/oum/organizational-unit/" + ou1id);

        Node parents = selectSingleNode(ou2doc, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        parents.appendChild(parent);

        String tmp = toString(ou2doc, true);
        ou2xml = update(ou2id, tmp);
        ou2doc = getDocument(ou2xml);

        // assert updated values
        assertXmlExists("Missing one parent", ou2doc, XPATH_ORGANIZATIONAL_UNIT_PARENT
            + "[@href='/oum/organizational-unit/" + ou1id + "']");
        assertXmlExists("Wrong number of parents", ou2doc, XPATH_ORGANIZATIONAL_UNIT_PARENTS + "[count(./parent)='1']");
    }

    /**
     * Test successfully updating parent relation of an Organizational Unit by using the general update method.
     *
     * @throws Exception If anything fails.
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

        Element parent = ou2parents.createElementNS("http://escidoc.de/core/01/structural-relations/", "srel:parent");
        parent.setAttribute("xlink:href", "/oum/organizational-unit/" + ou1id);

        Node parents = selectSingleNode(ou2parents, "/parents");
        parents.appendChild(parent);

        String tmp = toString(ou2parents, true);
        String ou2parentsXml = updateParentOus(ou2id, tmp);
        ou2parents = getDocument(ou2parentsXml);

        // assert updated values
        assertXmlExists("Missing one parent", ou2parents, "/parents/parent[@href='/oum/organizational-unit/" + ou1id
            + "']");
        assertXmlExists("Wrong number of parents", ou2parents, "/parents[count(./parent)='1']");
    }

    /**
     * Test declining updating the parents sub resource of an organizational unit with non existing id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void test_OUM_UPOU_2_1() throws Exception {

        final Class<OrganizationalUnitNotFoundException> ec = OrganizationalUnitNotFoundException.class;
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(createdXml);
        final String toBeUpdatedXml = retrieveParents(id);
        try {
            updateParentOus(UNKNOWN_ID, toBeUpdatedXml);

            failMissingException("No exception occured on update of parents with non existing id.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating of parents of an ou in status opened.
     */
    @Test
    public void test_UpdateParentsOfOuInStatusOpened() throws Exception {
        final Class<InvalidStatusException> ec = InvalidStatusException.class;
        final String createdNewParentXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(createdNewParentXml);
        createOuHierarchie();
        open(this.ouTop1Id, getTheLastModificationParam(true, this.ouTop1Id, "Opened organizational unit '"
            + this.ouTop1Id + "'."));
        open(this.ouChild1ParentId, getTheLastModificationParam(true, this.ouChild1ParentId,
            "Opened organizational unit '" + this.ouChild1ParentId + "'."));

        final String createdXml = retrieve(this.ouChild1ParentId);
        Document createdDocument = getDocument(createdXml);
        substitute(createdDocument, XPATH_ORGANIZATIONAL_UNIT_PARENT_XLINK_HREF, "/ir/organizational-unit/" + id);
        String toBeUpdatedXml = toString(createdDocument, true);
        try {
            update(this.ouChild1ParentId, toBeUpdatedXml);

            failMissingException("No exception occured on update of parents of an OU " + "in status opened.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the parents sub resource of an organizational with id of a resource of another type.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void test_OUM_UPOU_2_2() throws Exception {

        final Class<OrganizationalUnitNotFoundException> ec = OrganizationalUnitNotFoundException.class;

        final String ouXml = createSuccessfully("escidoc_ou_create.xml");

        try {
            updateParentOus(CONTEXT_ID, retrieveParents(getObjidValue(ouXml)));
            failMissingException("No exception occured on update with id of resoure of" + " another type.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the parents sub resource of an organizational with wrong last modification date (too
     * old).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void test_OUM_UPOU_3_1() throws Exception {

        Class<OptimisticLockingException> ec = OptimisticLockingException.class;
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(createdXml);
        final String toBeUpdatedXml = retrieveParents(id);

        final Document toBeUpdatedDocument =
            (Document) substitute(getDocument(toBeUpdatedXml), XPATH_PARENTS + "/" + PART_LAST_MODIFICATION_DATE,
                "2005-01-30T11:36:42.015Z");
        try {
            updateParentOus(id, toString(toBeUpdatedDocument, false));
            failMissingException("No exception occured on update with wrong time stamp.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the parents sub resource of an organizational with wrong last modification date (more
     * recent).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void test_OUM_UPOU_3_2() throws Exception {

        Class<OptimisticLockingException> ec = OptimisticLockingException.class;
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(createdXml);
        final String toBeUpdatedXml = retrieveParents(id);

        final Document toBeUpdatedDocument =
            (Document) substitute(getDocument(toBeUpdatedXml), XPATH_PARENTS + "/" + PART_LAST_MODIFICATION_DATE,
                getNowAsTimestamp());

        try {
            updateParentOus(id, toString(toBeUpdatedDocument, false));
            failMissingException("No exception occured on update with wrong time stamp.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the parents sub resource of an organizational with missing id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void test_OUM_UPOU_4_1() throws Exception {

        final Class<MissingMethodParameterException> ec = MissingMethodParameterException.class;

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(createdXml);
        final String toBeUpdatedXml = retrieveParents(id);

        try {
            updateParentOus(null, toBeUpdatedXml);
            failMissingException("No exception occured on update with missing id.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the parents sub resource of an organizational with missing parents xml.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void test_OUM_UPOU_4_2() throws Exception {

        final Class<MissingMethodParameterException> ec = MissingMethodParameterException.class;

        final String toBeUpdatedXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(toBeUpdatedXml);

        try {
            updateParentOus(id, null);

            failMissingException("No exception occured on update with missing " + "organization-details xml.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the parents sub resource of an organizational with corrupt xml.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void test_OUM_UPOU_5_1() throws Exception {

        final Class<XmlCorruptedException> ec = XmlCorruptedException.class;

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument = getDocument(createdXml);
        final String id = getObjidValue(toBeUpdatedDocument);
        try {
            updateParentOus(id, "<org");
            failMissingException("No exception occured on update with corrupted xml.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the parents sub resource of an organizational with wrong root element
     * (organizational-unit) in xml.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void test_OUM_UPOU_5_2() throws Exception {

        final Class<XmlCorruptedException> ec = XmlCorruptedException.class;

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument = getDocument(createdXml);
        final String id = getObjidValue(toBeUpdatedDocument);
        try {
            updateParentOus(id, createdXml);
            failMissingException("No exception occured on update with corrupted xml.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining updating the parents sub resource of an organizational with wrong root element (<unknown/>) in
     * xml.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void test_OUM_UPOU_5_3() throws Exception {

        final Class<XmlCorruptedException> ec = XmlCorruptedException.class;

        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document toBeUpdatedDocument = getDocument(createdXml);
        final String id = getObjidValue(toBeUpdatedDocument);
        try {
            updateParentOus(id, "<unknown/>");
            failMissingException("No exception occured on update with corrupted xml.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test successfully updating the organization-details sub resource of an REST.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void test_OUM_UOD_1_1_REST() throws Exception {

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
    public void test_OUM_UOD_1_2_REST() throws Exception {

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

    /**
     * Test for https://www.escidoc.org/jira/browse/INFR-1184. Create A parentOf B parentOf C and change this into A
     * parentOf B and C.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testINFR1184() throws Exception {
        // create OU A
        Document ouAdoc = getDocument(createSuccessfully("escidoc_ou_create.xml"));
        String ouAid = getObjidValue(ouAdoc);

        // create OU B
        Document ouBdoc = getDocument(createSuccessfully("escidoc_ou_create.xml"));
        String ouBid = getObjidValue(ouBdoc);

        // create OU C
        Document ouCdoc = getDocument(createSuccessfully("escidoc_ou_create.xml"));
        String ouCid = getObjidValue(ouCdoc);

        // A parentOf B
        Node parents = selectSingleNode(ouBdoc, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        Element parent = ouBdoc.createElementNS("http://escidoc.de/core/01/structural-relations/", "srel:parent");
        parent.setAttribute("xlink:href", "/oum/organizational-unit/" + ouAid);
        parents.appendChild(parent);
        ouBdoc = getDocument(update(ouBid, toString(ouBdoc, true)));

        // B parentOf C
        parents = selectSingleNode(ouCdoc, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        parent = ouCdoc.createElementNS("http://escidoc.de/core/01/structural-relations/", "srel:parent");
        parent.setAttribute("xlink:href", "/oum/organizational-unit/" + ouBid);
        parents.appendChild(parent);
        ouCdoc = getDocument(update(ouCid, toString(ouCdoc, true)));

        // A parentOf C
        parents = selectSingleNode(ouCdoc, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        parent = (Element) selectSingleNode(parents, XPATH_ORGANIZATIONAL_UNIT_PARENT);
        parent.setAttribute("xlink:href", "/oum/organizational-unit/" + ouAid);
        ouCdoc = getDocument(update(ouCid, toString(ouCdoc, true)));
    }
}
