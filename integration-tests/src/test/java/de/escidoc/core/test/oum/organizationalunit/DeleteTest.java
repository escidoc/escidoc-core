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
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.OrganizationalUnitHasChildrenException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;

/**
 * Test delete method of OrganizationalUnitHandler.
 * 
 * @author MSC
 * 
 */
@RunWith(value = Parameterized.class)
public class DeleteTest extends OrganizationalUnitTestBase {

    /**
     * @param transport
     *            The transport identifier.
     */
    public DeleteTest(final int transport) {
        super(transport);
    }

    /**
     * Tests successfully deleting an organizational unit without children.
     * 
     * @test.name Delete Organizational Unit - Success
     * @test.id OUM_DOU-1
     * @test.input
     *          <ul>
     *          <li>Id of existing organizational unit without children.</li>
     *          </ul>
     * @test.expected: Delete returns with success. Organizational unit is
     *                 deleted.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumDou1() throws Exception {

        final Document toBeDeletedDocument =
            getDocument(createSuccessfully("escidoc_ou_create.xml"));
        final String objid = getObjidValue(toBeDeletedDocument);
        try {
            delete(objid);
        }
        catch (Exception e) {
            failException("Delete of OU without children failed.", e);
        }

        // check if OU exists anymore
        Class<OrganizationalUnitNotFoundException> ec =
            OrganizationalUnitNotFoundException.class;
        try {
            retrieve(objid);
            failMissingException(ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Tests declining deleting an organizational unit with providing an unknown
     * id.
     * 
     * @test.name Delete Organizational Unit - Unknown Id
     * @test.id OUM_DOU-2-a
     * @test.input
     *          <ul>
     *          <li>Unknown id</li>
     *          </ul>
     * @test.expected: OrganizationalUnitNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumDou2a() throws Exception {

        final Class<OrganizationalUnitNotFoundException> ec =
            OrganizationalUnitNotFoundException.class;

        try {
            delete(UNKNOWN_ID);
            failMissingException(
                "Deleting OU with unknown id has not been declined", ec);
        }
        catch (Exception e) {
            assertExceptionType(
                "Deleting OU with unknown id has not been declined, correctly.",
                ec, e);
        }
    }

    /**
     * Tests declining deleting an organizational unit with providing an id of
     * an existing resource of another type.
     * 
     * @test.name Delete Organizational Unit - Id of Another Resource Type
     * @test.id OUM_DOU-2-b
     * @test.input
     *          <ul>
     *          <li>Id of an existing resource of another type.</li>
     *          </ul>
     * @test.expected: OrganizationalUnitNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumDou2b() throws Exception {

        final Class<OrganizationalUnitNotFoundException> ec =
            OrganizationalUnitNotFoundException.class;

        try {
            delete(CONTEXT_ID);
            failMissingException(
                "Deleting OU with id of an existing resource of another type"
                    + " has not been declined", ec);
        }
        catch (Exception e) {
            assertExceptionType(
                "Deleting OU with id of an existing resource of another type"
                    + " has not been declined, correctly.", ec, e);
        }
    }

    /**
     * Tests declining deleting an organizational unit without providing an id.
     * 
     * @test.name Delete Organizational Unit - Missing Id
     * @test.id OUM_DOU-3
     * @test.input
     *          <ul>
     *          <li>No id is provided.</li>
     *          </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumDou3() throws Exception {

        final Class<MissingMethodParameterException> ec =
            MissingMethodParameterException.class;

        try {
            delete(null);
            failMissingException(
                "Deleting OU with id of an existing resource of another type"
                    + " has not been declined", ec);
        }
        catch (Exception e) {
            assertExceptionType(
                "Deleting OU with id of an existing resource of another type"
                    + " has not been declined, correctly.", ec, e);
        }
    }

    /**
     * Tests declining deleting an organizational unit with children.
     * 
     * @test.name Delete Organizational Unit - With Children
     * @test.id OUM_DOU-4-a
     * @test.input
     *          <ul>
     *          <li>Id of existing organizational unit with children.</li>
     *          </ul>
     * @test.expected: OrganizationalUnitHasChildrenException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumDou4a() throws Exception {

        final Class<OrganizationalUnitHasChildrenException> ec =
            OrganizationalUnitHasChildrenException.class;

        // create parent ou
        final Document toBeDeletedDocument =
            getDocument(createSuccessfully("escidoc_ou_create.xml"));
        final String parentId = getObjidValue(toBeDeletedDocument);

        // create child ou
        createSuccessfullyChild("escidoc_ou_create.xml",
            new String[] { parentId });

        // delete parent ou
        try {
            delete(parentId);
            failMissingException(
                "Deleting OU with children has not been declined", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }

        // check if OU exists anymore
        try {
            retrieve(parentId);
        }
        catch (Exception e) {
            failException(
                "Could not retrieve OU after declined delete request.", e);
        }
    }

    /**
     * Tests declining deleting an organizational unit which is in state
     * 'opened'.
     * 
     * @test.name Delete Organizational Unit - In state 'opened'
     * @test.id OUM_DOU-4-b
     * @test.input
     *          <ul>
     *          <li>Id of existing organizational unit with children.</li>
     *          </ul>
     * @test.expected: InvalidStatusException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumDou4b() throws Exception {

        final Class<InvalidStatusException> ec = InvalidStatusException.class;

        // create parent ou
        final Document toBeDeletedDocument =
            getDocument(createSuccessfully("escidoc_ou_create.xml"));
        final String parentId = getObjidValue(toBeDeletedDocument);
        // create child ou
        createSuccessfullyChild("escidoc_ou_create.xml",
            new String[] { parentId });
        open(parentId, getTheLastModificationParam(true, parentId,
            "Opened to delete, should be declined!"));

        // delete parent ou
        try {
            delete(parentId);
            failMissingException(
                "Deleting OU with children has not been declined", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }

        // check if OU exists anymore
        try {
            retrieve(parentId);
        }
        catch (Exception e) {
            failException(
                "Could not retrieve OU after declined delete request.", e);
        }
    }

    /**
     * Tests declining deleting an organizational unit which is in state
     * 'opened'.
     * 
     * @test.name Delete Organizational Unit - In state 'opened'
     * @test.id OUM_DOU-4-c
     * @test.input
     *          <ul>
     *          <li>Id of existing organizational unit without children.</li>
     *          </ul>
     * @test.expected: InvalidStatusException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumDou4c() throws Exception {

        final Class<InvalidStatusException> ec = InvalidStatusException.class;

        // create parent ou
        final Document toBeDeletedDocument =
            getDocument(createSuccessfully("escidoc_ou_create.xml"));
        final String id = getObjidValue(toBeDeletedDocument);
        open(id, getTheLastModificationParam(true, id,
            "Opened to delete, should be declined!"));

        // delete parent ou
        try {
            delete(id);
            failMissingException(
                "Deleting OU with children has not been declined", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }

        // check if OU exists anymore
        try {
            retrieve(id);
        }
        catch (Exception e) {
            failException(
                "Could not retrieve OU after declined delete request.", e);
        }
    }

    /**
     * Tests declining deleting an organizational unit which is in state
     * 'closed'.
     * 
     * @test.name Delete Organizational Unit - In state 'closed'
     * @test.id OUM_DOU-4-d
     * @test.input
     *          <ul>
     *          <li>Id of existing organizational unit without children.</li>
     *          </ul>
     * @test.expected: InvalidStatusException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumDou4d() throws Exception {

        final Class<InvalidStatusException> ec = InvalidStatusException.class;

        // create parent ou
        final Document toBeDeletedDocument =

        getDocument(createSuccessfully("escidoc_ou_create.xml"));
        final String id = getObjidValue(toBeDeletedDocument);
        open(id, getTheLastModificationParam(true, id,
            "Opened to delete, should be declined!"));
        close(id, getTheLastModificationParam(true, id,
            "Closed to delete, should be declined!"));

        // delete parent ou
        try {
            delete(id);
            failMissingException(
                "Deleting OU with children has not been declined", ec);
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }

        // check if OU exists anymore
        try {
            retrieve(id);
        }
        catch (Exception e) {
            failException(
                "Could not retrieve OU after declined delete request.", e);
        }
    }
}
