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

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.test.EscidocAbstractTest;

import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.junit.Assert.fail;

/**
 * Test open method of OrganizationalUnitHandler.
 * 
 * @author Michael Schneider
 */
public class OpenIT extends OrganizationalUnitTestBase {

    /**
     * Tests successfully open an organizational unit without children.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumOou1a() throws Exception {

        final Document toBeOpenedDocument = getDocument(createSuccessfully("escidoc_ou_create.xml"));
        final String objid = getObjidValue(toBeOpenedDocument);

        try {
            open(objid, getTheLastModificationParam(true, objid, "Opened organizational unit '" + objid + "'."));
        }
        catch (final Exception e) {
            failException("Open of OU without children failed.", e);
        }

        // check if OU is opened
        substitute(toBeOpenedDocument, XPATH_ORGANIZATIONAL_UNIT_PUBLIC_STATUS, ORGANIZATIONAL_UNIT_STATUS_OPENED);
        String toBeOpenedString = toString(toBeOpenedDocument, false);
        String opened = retrieve(objid);
        assertOrganizationalUnit(opened, toBeOpenedString, null, null, false, false);
    }

    /**
     * Tests successfully open an organizational unit with children.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumOou1b() throws Exception {

        // create parent ou
        final Document toBeOpenedDocument = getDocument(createSuccessfully("escidoc_ou_create.xml"));
        final String parentId = getObjidValue(toBeOpenedDocument);
        // create child ou
        createSuccessfullyChild("escidoc_ou_create.xml", new String[] { parentId });

        // delete parent ou
        try {
            open(parentId,
                getTheLastModificationParam(true, parentId, "Opened organizational unit '" + parentId + "'."));
        }
        catch (final Exception e) {
            failException("Open of OU with children failed.", e);
        }

        // check if OU is opened
        substitute(toBeOpenedDocument, XPATH_ORGANIZATIONAL_UNIT_PUBLIC_STATUS, ORGANIZATIONAL_UNIT_STATUS_OPENED);
        String toBeOpenedString = toString(toBeOpenedDocument, false);
        String opened = retrieve(parentId);
        assertOrganizationalUnit(opened, toBeOpenedString, null, null, false, false);
    }

    /**
     * Tests successfully open an organizational unit with parents in status opened.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumOou1c() throws Exception {

        final String[] parentValues = createSuccessfully("escidoc_ou_create.xml", 2);

        open(parentValues[0], getTheLastModificationParam(true, parentValues[0], "Opened organizational unit '"
            + parentValues[0] + "'."));
        open(parentValues[1], getTheLastModificationParam(true, parentValues[1], "Opened organizational unit '"
            + parentValues[1] + "'."));

        Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_ORGANIZATIONAL_UNIT_PATH, "escidoc_ou_create.xml");
        setUniqueValue(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE);
        insertParentsElement(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS, parentValues, false);

        String toBeCreatedXml = toString(toBeCreatedDocument, false);

        String createdXml = null;
        try {
            createdXml = create(toBeCreatedXml);
        }
        catch (final Exception e) {
            failException("Creating OU with parents failed with exception. ", e);
        }

        final Document toBeOpenedDocument = getDocument(createdXml);
        final String objid = getObjidValue(toBeOpenedDocument);

        try {
            open(objid, getTheLastModificationParam(true, objid, "Opened organizational unit '" + objid + "'."));
        }
        catch (final Exception e) {
            failException("Open of OU without children failed.", e);
        }
        // check if OU is opened
        substitute(toBeOpenedDocument, XPATH_ORGANIZATIONAL_UNIT_PUBLIC_STATUS, ORGANIZATIONAL_UNIT_STATUS_OPENED);
        String toBeOpenedString = toString(toBeOpenedDocument, false);
        String opened = retrieve(objid);
        assertOrganizationalUnit(opened, toBeOpenedString, null, null, false, false);
    }

    /**
     * Tests declining remove a parent from an open organizational unit.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRemoveParentFromOpen() throws Exception {
        // create parent ou
        final Document toBeOpenedDocument = getDocument(createSuccessfully("escidoc_ou_create.xml"));
        final String parentId = getObjidValue(toBeOpenedDocument);

        // create child ou
        String ou = createSuccessfullyChild("escidoc_ou_create.xml", new String[] { parentId });
        final String ouId = getObjidValue(ou);

        // open both OUs
        try {
            open(parentId,
                getTheLastModificationParam(true, parentId, "Opened organizational unit '" + parentId + "'."));
            open(ouId, getTheLastModificationParam(true, ouId, "Opened organizational unit '" + ouId + "'."));
        }
        catch (final Exception e) {
            failException("Open of OU with children failed.", e);
        }

        Document ouDoc = getDocument(retrieve(ouId));
        ouDoc = (Document) deleteElement(ouDoc, "/organizational-unit/parents/*");
        ou = toString(ouDoc, false);
        try {
            update(ouId, ou);
            fail("No exception when removing parent from opened organizational-unit.");
        }
        catch (final Exception e) {
            Class ec = InvalidStatusException.class;
            assertExceptionType(ec, e);
        }
    }

    /**
     * Tests declining opening an organizational unit with providing an unknown id.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumOou2a() throws Exception {

        final Class<OrganizationalUnitNotFoundException> ec = OrganizationalUnitNotFoundException.class;
        try {
            open(UNKNOWN_ID, "<param />");
            failMissingException("Opening OU with unknown id has not been declined", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Opening OU with unknown id has not been declined correctly.", ec, e);
        }
    }

    /**
     * Tests declining opening an organizational unit with providing an id of an existing resource of another type.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumOou2b() throws Exception {

        final Class<OrganizationalUnitNotFoundException> ec = OrganizationalUnitNotFoundException.class;
        try {
            open(CONTEXT_ID, getTheLastModificationParam(true, CONTEXT_ID, "Opened organizational unit '" + CONTEXT_ID
                + "'."));
            failMissingException("Opening OU with id of an existing resource of another type"
                + " has not been declined", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Opening OU with id of an existing resource of another type"
                + " has not been declined correctly.", ec, e);
        }
    }

    /**
     * Tests declining opening an organizational unit without providing an id.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumOou3a() throws Exception {

        final Class<MissingMethodParameterException> ec = MissingMethodParameterException.class;
        try {
            open(null, "<param />");
            failMissingException("Opening OU without an id has not been declined", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Opening OU without an id has not been declined correctly.", ec, e);
        }
    }

    /**
     * Tests declining opening an organizational unit without providing a task param.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumOou3b() throws Exception {

        final Class<MissingMethodParameterException> ec = MissingMethodParameterException.class;
        try {
            open(ORGANIZATIONAL_UNIT_ID, null);
            failMissingException("Opening OU without a task param has not been declined", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Opening OU without a task param has not been declined correctly.", ec, e);
        }
    }

    /**
     * Tests declining opening an organizational unit with an invalid task param (missing timestamp).
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumOou3c() throws Exception {

        final Class<XmlCorruptedException> ec = XmlCorruptedException.class;
        try {
            open(ORGANIZATIONAL_UNIT_ID, "<param />");
            failMissingException("Opening OU with an invalid task param has not been declined", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Opening OU with an invalid task param has not been declined correctly.", ec, e);
        }
    }

    /**
     * Tests declining opening an organizational unit with an invalid task param (wrong root).
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumOou3d() throws Exception {

        final Class<XmlCorruptedException> ec = XmlCorruptedException.class;
        try {
            open(ORGANIZATIONAL_UNIT_ID, "<task-parm />");
            failMissingException("Opening OU with an invalid task param has not been declined", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Opening OU with an invalid task param has not been declined correctly.", ec, e);
        }
    }

    /**
     * Tests declining opening an organizational unit which is already opened.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumOou4a() throws Exception {

        final Class<InvalidStatusException> ec = InvalidStatusException.class;

        // create ou
        final Document toBeDeletedDocument = getDocument(createSuccessfully("escidoc_ou_create.xml"));
        final String objid = getObjidValue(toBeDeletedDocument);

        // open ou
        open(objid, getTheLastModificationParam(true, objid, "2. Opened organizational unit '" + objid + "'."));

        // open ou for the 2nd time
        try {
            open(objid, getTheLastModificationParam(true, objid, "2. Opened organizational unit '" + objid + "'."));
            failMissingException("Opening OU with children has not been declined", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Tests declining opening an organizational unit which is in state closed.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumOou4b() throws Exception {

        final Class<InvalidStatusException> ec = InvalidStatusException.class;

        // create ou
        final Document toBeDeletedDocument = getDocument(createSuccessfully("escidoc_ou_create.xml"));
        final String objid = getObjidValue(toBeDeletedDocument);

        // open ou
        open(objid, getTheLastModificationParam(true, objid, "1. Opened organizational unit '" + objid + "'."));
        // close ou
        close(objid, getTheLastModificationParam(true, objid, "1. Closed organizational unit '" + objid + "'."));

        // open ou for the 2nd time
        try {
            open(objid, getTheLastModificationParam(true, objid, "2. Opened organizational unit '" + objid + "'."));
            failMissingException("Opening OU with children has not been declined", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Tests declining opening an organizational unit with parents in status created.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumOou4c() throws Exception {

        final String[] parentValues = createSuccessfully("escidoc_ou_create.xml", 2);

        Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_ORGANIZATIONAL_UNIT_PATH, "escidoc_ou_create.xml");
        setUniqueValue(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE);
        insertParentsElement(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS, parentValues, false);

        String toBeCreatedXml = toString(toBeCreatedDocument, false);

        String createdXml = null;
        try {
            createdXml = create(toBeCreatedXml);
        }
        catch (final Exception e) {
            failException("Creating OU with parents failed with exception. ", e);
        }

        final Document toBeOpenedDocument = getDocument(createdXml);
        final String objid = getObjidValue(toBeOpenedDocument);
        final Class<InvalidStatusException> ec = InvalidStatusException.class;
        try {
            open(objid, getTheLastModificationParam(true, objid, "Opened organizational unit '" + objid + "'."));
            failMissingException("Opening OU has not been declined", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);

        }
    }

    /**
     * Test the last modification date timestamp of the open/close method.
     * 
     * @throws Exception
     *             Thrown if anything failed.
     */
    @Test
    public void testReturnValue01() throws Exception {

        // final String[] parentValues =
        // createSuccessfully("escidoc_ou_create.xml", 2);

        Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_ORGANIZATIONAL_UNIT_PATH, "escidoc_ou_create.xml");
        setUniqueValue(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE);
        // insertParentsElement(toBeCreatedDocument,
        // XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS, parentValues, false);

        String toBeCreatedXml = toString(toBeCreatedDocument, false);

        String createdXml = create(toBeCreatedXml);

        final Document toBeOpenedDocument = getDocument(createdXml);
        final String objid = getObjidValue(toBeOpenedDocument);

        String lmd = getLastModificationDateValue(toBeOpenedDocument);
        String resultXml = open(objid, getTaskParam(lmd));
        assertXmlValidResult(resultXml);
        String lmdOpen = getLastModificationDateValue(getDocument(resultXml));

        resultXml = close(objid, getTaskParam(lmdOpen));
        assertXmlValidResult(resultXml);
        String lmdClose = getLastModificationDateValue(getDocument(resultXml));

        assertTimestampIsEqualOrAfter("Wrong timestamp", lmdClose, lmdOpen);
    }

    /**
     * Test if comment is processed (issue INFR-1403)
     * 
     * @throws Exception
     *             Thrown if anything failed.
     */
    @Test
    @Ignore("additional values required in persistence data - fix with 1.5")
    public void comment() throws Exception {

        Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_ORGANIZATIONAL_UNIT_PATH, "escidoc_ou_create.xml");
        setUniqueValue(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE);

        String createdXml = create(toString(toBeCreatedDocument, false));

        final Document toBeOpenedDocument = getDocument(createdXml);
        final String objid = getObjidValue(toBeOpenedDocument);

        final String openComment = String.valueOf(System.nanoTime());
        String taskParam = getStatusTaskParam(getLastModificationDateValue(toBeOpenedDocument), openComment);
        String resultXml = open(objid, taskParam);
        assertXmlValidResult(resultXml);
        assertXmlEquals("Comment string not as expected", EscidocAbstractTest.getDocument(retrieve(objid)),
            "/organizational-unit/properties/public-status-comment", openComment);

        final String closeComment = String.valueOf(System.nanoTime());
        taskParam = getStatusTaskParam(getLastModificationDateValue(getDocument(resultXml)), closeComment);

        resultXml = close(objid, taskParam);
        assertXmlValidResult(resultXml);
        assertXmlEquals("Comment string not as expected", EscidocAbstractTest.getDocument(retrieve(objid)),
            "/organizational-unit/properties/public-status-comment", closeComment);
    }

    /**
     * Tests declining opening an organizational unit with parents in status closed.
     * 
     * @test.name Open Organizational Unit - Success
     * @test.id OUM_OOU-4-d
     * @test.input <ul>
     *             <li>Id of existing organizational unit with parents in status closed.</li>
     *             </ul>
     * @test.expected: InvalidStatusException
     * @test.status Revoked - because it is impossible to create an ou with parents in status closed.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    @Ignore("unkown")
    public void testOumOou4d() throws Exception {

        final String[] parentValues = createSuccessfully("escidoc_ou_create.xml", 2);

        open(parentValues[0], getTheLastModificationParam(true, parentValues[0], "Opened organizational unit '"
            + parentValues[0] + "'."));
        open(parentValues[1], getTheLastModificationParam(true, parentValues[1], "Opened organizational unit '"
            + parentValues[1] + "'."));
        Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_ORGANIZATIONAL_UNIT_PATH, "escidoc_ou_create.xml");
        setUniqueValue(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE);
        insertParentsElement(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS, parentValues, false);

        String toBeCreatedXml = toString(toBeCreatedDocument, false);

        String createdXml = null;
        try {
            createdXml = create(toBeCreatedXml);
        }
        catch (final Exception e) {
            failException("Creating OU with parents failed with exception. ", e);
        }

        close(parentValues[0], getTheLastModificationParam(true, parentValues[0], "Closed organizational unit '"
            + parentValues[0] + "'."));
        close(parentValues[1], getTheLastModificationParam(true, parentValues[1], "Closed organizational unit '"
            + parentValues[1] + "'."));
        final Document toBeOpenedDocument = getDocument(createdXml);
        final String objid = getObjidValue(toBeOpenedDocument);
        final Class<InvalidStatusException> ec = InvalidStatusException.class;
        try {
            open(objid, getTheLastModificationParam(true, objid, "Opened organizational unit '" + objid + "'."));
            failMissingException("Opening OU has not been declined", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);

        }
    }
}
