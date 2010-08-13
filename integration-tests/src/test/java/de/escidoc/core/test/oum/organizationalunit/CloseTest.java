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
 * Copyright 2007-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.test.oum.organizationalunit;

import org.w3c.dom.Document;

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.OrganizationalUnitNotFoundException;

/**
 * Test close method of OrganizationalUnitHandler.
 * 
 * @author MSC
 * 
 */
public class CloseTest extends OrganizationalUnitTestBase {

    /**
     * @param transport
     *            The transport identifier.
     */
    public CloseTest(final int transport) {
        super(transport);
    }

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Override
    protected void setUp() throws Exception {

        super.setUp();
    }

    /**
     * Clean up after servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Tests successfully close an organizational unit without children.
     * 
     * @test.name Close Organizational Unit - Success
     * @test.id OUM_COU-1-a
     * @test.input <ul>
     *             <li>Id of existing organizational unit without children.</li>
     *             </ul>
     * @test.expected: Close returns with success. Organizational unit is in
     *                 state closed.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumCou1a() throws Exception {

        final Document toBeClosedDocument =
            getDocument(createSuccessfully("escidoc_ou_create.xml"));
        final String objid = getObjidValue(toBeClosedDocument);
        open(objid, getTheLastModificationParam(true, objid,
            "Opened organizational unit '" + objid + "'."));
        try {
            close(objid, getTheLastModificationParam(true, objid,
                "Closed organizational unit '" + objid + "'."));
        }
        catch (Exception e) {
            failException("Close of OU without children failed.", e);
        }

        // check if OU is closed
        substitute(toBeClosedDocument, XPATH_ORGANIZATIONAL_UNIT_PUBLIC_STATUS,
            ORGANIZATIONAL_UNIT_STATUS_CLOSED);
        String toBeClosedString = toString(toBeClosedDocument, false);
        String closed = retrieve(objid);
        assertOrganizationalUnit(closed, toBeClosedString, null, null, false,
            false);
    }

    /**
     * Tests successfully close an organizational unit with children in state
     * closed.
     * 
     * @test.name Close Organizational Unit - Success
     * @test.id OUM_COU-1-b
     * @test.input <ul>
     *             <li>Id of existing organizational unit without children.</li>
     *             </ul>
     * @test.expected: Close returns with success. Organizational unit is in
     *                 state closed.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumCou1b() throws Exception {

        createOuHierarchie();
        open(ouTop1Id, getTheLastModificationParam(true, ouTop1Id,
            "Opened organizational unit '" + ouTop1Id + "'."));
        open(ouChild1ParentId, getTheLastModificationParam(true,
            ouChild1ParentId, "Opened organizational unit '" + ouChild1ParentId
                + "'."));
        open(ouChild2ParentsId, getTheLastModificationParam(true,
            ouChild2ParentsId, "Opened organizational unit '"
                + ouChild2ParentsId + "'."));
        close(ouChild2ParentsId, getTheLastModificationParam(true,
            ouChild2ParentsId, "Closed organizational unit '"
                + ouChild2ParentsId + "'."));
        close(ouChild1ParentId, getTheLastModificationParam(true,
            ouChild1ParentId, "Closed organizational unit '" + ouChild1ParentId
                + "'."));

        final String objid = ouTop1Id;
        final Document toBeClosedDocument = getDocument(retrieve(ouTop1Id));
        try {
            close(objid, getTheLastModificationParam(true, objid,
                "Closed organizational unit '" + objid + "'."));
        }
        catch (Exception e) {
            failException("Close of OU with children failed.", e);
        }

        // check if OU is closed
        substitute(toBeClosedDocument, XPATH_ORGANIZATIONAL_UNIT_PUBLIC_STATUS,
            ORGANIZATIONAL_UNIT_STATUS_CLOSED);
        String toBeClosedString = toString(toBeClosedDocument, false);
        String closed = retrieve(objid);
        assertOrganizationalUnit(closed, toBeClosedString, null, null, false,
            false);
    }

    /**
     * Tests declining closing an organizational unit with children in state
     * created.
     * 
     * @test.name Close Organizational Unit - With children
     * @test.id OUM_COU-2-a
     * @test.input <ul>
     *             <li>Id of existing organizational unit with children.</li>
     *             </ul>
     * @test.expected: InvalidStatusException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumCou2a() throws Exception {

        final Class<InvalidStatusException> ec = InvalidStatusException.class;
        // create parent ou
        final Document toBeClosedDocument =
            getDocument(createSuccessfully("escidoc_ou_create.xml"));
        final String parentId = getObjidValue(toBeClosedDocument);
        // create child ou
        createSuccessfullyChild("escidoc_ou_create.xml",
            new String[] { parentId });
        open(parentId, getTheLastModificationParam(true, parentId,
            "Opened organizational unit '" + parentId + "'."));
        // close parent ou
        try {
            close(parentId, getTheLastModificationParam(true, parentId,
                "Closed organizational unit '" + parentId + "'."));
            failMissingException(
                "Closing OU with children has not been declined correctly.", ec);
        }
        catch (Exception e) {
            assertExceptionType(
                "Closing OU with children has not been declined correctly.",
                ec, e);
        }
    }

    /**
     * Tests declining closing an organizational unit with children in state
     * opened.
     * 
     * @test.name Close Organizational Unit - With children
     * @test.id OUM_COU-2-b
     * @test.input <ul>
     *             <li>Id of existing organizational unit with children.</li>
     *             </ul>
     * @test.expected: InvalidStatusException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumCou2b() throws Exception {

        final Class<InvalidStatusException> ec = InvalidStatusException.class;
        // create parent ou
        final Document toBeClosedDocument =
            getDocument(createSuccessfully("escidoc_ou_create.xml"));
        final String parentId = getObjidValue(toBeClosedDocument);
        open(parentId, getTheLastModificationParam(true, parentId,
            "Opened organizational unit '" + parentId + "'."));
        // create child ou
        final String child =
            createSuccessfullyChild("escidoc_ou_create.xml",
                new String[] { parentId });
        final String childId = getObjidValue(child);

        open(childId, getTheLastModificationParam(true, childId,
            "Opened organizational unit '" + childId + "'."));

        // close parent ou
        try {
            close(parentId, getTheLastModificationParam(true, parentId,
                "Closed organizational unit '" + parentId + "'."));
            failMissingException(
                "Closing OU with children has not been declined correctly.", ec);
        }
        catch (Exception e) {
            assertExceptionType(
                "Closing OU with children in state opened has not been declined correctly.",
                ec, e);
        }
    }

    /**
     * Tests declining closing an organizational unit with providing an unknown
     * id.
     * 
     * @test.name Close Organizational Unit - Unknown Id
     * @test.id OUM_COU-2-c
     * @test.input <ul>
     *             <li>Unknown id</li>
     *             </ul>
     * @test.expected: OrganizationalUnitNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumCou2c() throws Exception {

        final Class<OrganizationalUnitNotFoundException> ec =
            OrganizationalUnitNotFoundException.class;
        try {
            close(UNKNOWN_ID, "<param />");
            failMissingException(
                "Closing OU with unknown id has not been declined", ec);
        }
        catch (Exception e) {
            assertExceptionType(
                "Closing OU with unknown id has not been declined correctly.",
                ec, e);
        }
    }

    /**
     * Tests declining closing an organizational unit with providing an id of an
     * existing resource of another type.
     * 
     * @test.name Close Organizational Unit - Id of Another Resource Type
     * @test.id OUM_COU-2-d
     * @test.input <ul>
     *             <li>Id of an existing resource of another type.</li>
     *             </ul>
     * @test.expected: OrganizationalUnitNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumCou2d() throws Exception {

        final Class<OrganizationalUnitNotFoundException> ec =
            OrganizationalUnitNotFoundException.class;
        try {
            close(CONTEXT_ID, getTheLastModificationParam(true, CONTEXT_ID,
                "Closed organizational unit '" + CONTEXT_ID + "'."));
            failMissingException(
                "Closing OU with id of an existing resource of another type"
                    + " has not been declined", ec);
        }
        catch (Exception e) {
            assertExceptionType(
                "Closing OU with id of an existing resource of another type"
                    + " has not been declined correctly.", ec, e);
        }
    }

    /**
     * Tests declining closing an organizational unit without providing an id.
     * 
     * @test.name Close Organizational Unit - Missing Id
     * @test.id OUM_COU-3-a
     * @test.input <ul>
     *             <li>No id is provided.</li>
     *             </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumCou3a() throws Exception {

        final Class<MissingMethodParameterException> ec =
            MissingMethodParameterException.class;
        try {
            close(null, "<param />");
            failMissingException(
                "Closing OU without an id has not been declined", ec);
        }
        catch (Exception e) {
            assertExceptionType(
                "Closing OU without an id has not been declined correctly.",
                ec, e);
        }
    }

    /**
     * Tests declining closing an organizational unit without providing a task
     * param.
     * 
     * @test.name Close Organizational Unit - Missing Task Param
     * @test.id OUM_COU-3-b
     * @test.input <ul>
     *             <li>No id is provided.</li>
     *             </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumCou3b() throws Exception {

        final Class<MissingMethodParameterException> ec =
            MissingMethodParameterException.class;
        try {
            close(ORGANIZATIONAL_UNIT_ID, null);
            failMissingException(
                "Closing OU without a task param has not been declined", ec);
        }
        catch (Exception e) {
            assertExceptionType(
                "Closing OU without a task param has not been declined correctly.",
                ec, e);
        }
    }

    /**
     * Tests declining closing an organizational unit with an invalid task param
     * (missing timestamp).
     * 
     * @test.name Close Organizational Unit - Invalid Task Param (missing
     *            timestamp)
     * @test.id OUM_COU-3-c
     * @test.input <ul>
     *             <li>Invalid Task Param (missing timestamp) is provided.</li>
     *             </ul>
     * @test.expected: InvalidXmlException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumCou3c() throws Exception {

        final Class<XmlCorruptedException> ec = XmlCorruptedException.class;
        try {
            close(ORGANIZATIONAL_UNIT_ID, "<param />");
            failMissingException(
                "Closing OU with an invalid task param has not been declined",
                ec);
        }
        catch (Exception e) {
            assertExceptionType(
                "Closing OU with an invalid task param has not been declined correctly.",
                ec, e);
        }
    }

    /**
     * Tests declining closing an organizational unit with an invalid task param
     * (wrong root).
     * 
     * @test.name Close Organizational Unit - Invalid Task Param (wrong root)
     * @test.id OUM_COU-3-d
     * @test.input <ul>
     *             <li>Invalid Task Param (wrong root) is provided.</li>
     *             </ul>
     * @test.expected: InvalidXmlException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumCou3d() throws Exception {

        final Class<XmlCorruptedException> ec = XmlCorruptedException.class;
        try {
            close(ORGANIZATIONAL_UNIT_ID, "<task-parm />");
            failMissingException(
                "Closing OU with an invalid task param has not been declined",
                ec);
        }
        catch (Exception e) {
            assertExceptionType(
                "Closing OU with an invalid task param has not been declined correctly.",
                ec, e);
        }
    }

    /**
     * Tests declining closing an organizational unit twice.
     * 
     * @test.name Close Organizational Unit - Id of already closed
     * @test.id OUM_COU-4-a
     * @test.input <ul>
     *             <li>Id of existing organizational unit which is already
     *             closed.</li>
     *             </ul>
     * @test.expected: InvalidStatusException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumCou4a() throws Exception {

        final Class<InvalidStatusException> ec = InvalidStatusException.class;

        // create ou
        final Document toBeDeletedDocument =
            getDocument(createSuccessfully("escidoc_ou_create.xml"));
        final String objid = getObjidValue(toBeDeletedDocument);

        // open ou
        open(objid, getTheLastModificationParam(true, objid,
            "1. Opened organizational unit '" + objid + "'."));
        // close ou
        close(objid, getTheLastModificationParam(true, objid,
            "1. Closed organizational unit '" + objid + "'."));

        // close ou for the 2nd time
        try {
            close(objid, getTheLastModificationParam(true, objid,
                "2. Closed organizational unit '" + objid + "'."));
            failMissingException("Closing OU twice has not been declined", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Tests declining closing an organizational unit in state 'created'.
     * 
     * @test.name Close Organizational Unit - Id of created
     * @test.id OUM_COU-4-b
     * @test.input <ul>
     *             <li>Id of existing organizational unit in state 'created'.</li>
     *             </ul>
     * @test.expected: InvalidStatusException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumCou4b() throws Exception {

        final Class<InvalidStatusException> ec = InvalidStatusException.class;

        // create ou
        final Document toBeDeletedDocument =
            getDocument(createSuccessfully("escidoc_ou_create.xml"));
        final String objid = getObjidValue(toBeDeletedDocument);

        // close ou
        try {
            close(objid, getTheLastModificationParam(true, objid,
                "1. Close organizational unit '" + objid + "'."));
            failMissingException(
                "Closing OU in state created has not been declined", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }
}
