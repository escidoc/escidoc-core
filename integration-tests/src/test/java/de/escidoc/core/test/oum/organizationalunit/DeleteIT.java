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
import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;

/**
 * Test delete method of OrganizationalUnitHandler.
 *
 * @author Michael Schneider
 */
public class DeleteIT extends OrganizationalUnitTestBase {

    /**
     * Tests successfully deleting an organizational unit without children.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumDou1() throws Exception {

        final Document toBeDeletedDocument = getDocument(createSuccessfully("escidoc_ou_create.xml"));
        final String objid = getObjidValue(toBeDeletedDocument);
        try {
            delete(objid);
        }
        catch (final Exception e) {
            failException("Delete of OU without children failed.", e);
        }

        // check if OU exists anymore
        Class<OrganizationalUnitNotFoundException> ec = OrganizationalUnitNotFoundException.class;
        try {
            retrieve(objid);
            failMissingException(ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Tests declining deleting an organizational unit with providing an unknown id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumDou2a() throws Exception {

        final Class<OrganizationalUnitNotFoundException> ec = OrganizationalUnitNotFoundException.class;

        try {
            delete(UNKNOWN_ID);
            failMissingException("Deleting OU with unknown id has not been declined", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Deleting OU with unknown id has not been declined, correctly.", ec, e);
        }
    }

    /**
     * Tests declining deleting an organizational unit with providing an id of an existing resource of another type.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumDou2b() throws Exception {

        final Class<OrganizationalUnitNotFoundException> ec = OrganizationalUnitNotFoundException.class;

        try {
            delete(CONTEXT_ID);
            failMissingException("Deleting OU with id of an existing resource of another type"
                + " has not been declined", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Deleting OU with id of an existing resource of another type"
                + " has not been declined, correctly.", ec, e);
        }
    }

    /**
     * Tests declining deleting an organizational unit without providing an id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumDou3() throws Exception {

        final Class<MissingMethodParameterException> ec = MissingMethodParameterException.class;

        try {
            delete(null);
            failMissingException("Deleting OU with id of an existing resource of another type"
                + " has not been declined", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Deleting OU with id of an existing resource of another type"
                + " has not been declined, correctly.", ec, e);
        }
    }

    /**
     * Tests declining deleting an organizational unit with children.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumDou4a() throws Exception {

        final Class<OrganizationalUnitHasChildrenException> ec = OrganizationalUnitHasChildrenException.class;

        // create parent ou
        final Document toBeDeletedDocument = getDocument(createSuccessfully("escidoc_ou_create.xml"));
        final String parentId = getObjidValue(toBeDeletedDocument);

        // create child ou
        createSuccessfullyChild("escidoc_ou_create.xml", new String[] { parentId });

        // delete parent ou
        try {
            delete(parentId);
            failMissingException("Deleting OU with children has not been declined", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }

        // check if OU exists anymore
        try {
            retrieve(parentId);
        }
        catch (final Exception e) {
            failException("Could not retrieve OU after declined delete request.", e);
        }
    }

    /**
     * Tests declining deleting an organizational unit which is in state 'opened'.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumDou4b() throws Exception {

        final Class<InvalidStatusException> ec = InvalidStatusException.class;

        // create parent ou
        final Document toBeDeletedDocument = getDocument(createSuccessfully("escidoc_ou_create.xml"));
        final String parentId = getObjidValue(toBeDeletedDocument);
        // create child ou
        createSuccessfullyChild("escidoc_ou_create.xml", new String[] { parentId });
        open(parentId, getTheLastModificationParam(true, parentId, "Opened to delete, should be declined!"));

        // delete parent ou
        try {
            delete(parentId);
            failMissingException("Deleting OU with children has not been declined", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }

        // check if OU exists anymore
        try {
            retrieve(parentId);
        }
        catch (final Exception e) {
            failException("Could not retrieve OU after declined delete request.", e);
        }
    }

    /**
     * Tests declining deleting an organizational unit which is in state 'opened'.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumDou4c() throws Exception {

        final Class<InvalidStatusException> ec = InvalidStatusException.class;

        // create parent ou
        final Document toBeDeletedDocument = getDocument(createSuccessfully("escidoc_ou_create.xml"));
        final String id = getObjidValue(toBeDeletedDocument);
        open(id, getTheLastModificationParam(true, id, "Opened to delete, should be declined!"));

        // delete parent ou
        try {
            delete(id);
            failMissingException("Deleting OU with children has not been declined", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }

        // check if OU exists anymore
        try {
            retrieve(id);
        }
        catch (final Exception e) {
            failException("Could not retrieve OU after declined delete request.", e);
        }
    }

    /**
     * Tests declining deleting an organizational unit which is in state 'closed'.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumDou4d() throws Exception {

        final Class<InvalidStatusException> ec = InvalidStatusException.class;

        // create parent ou
        final Document toBeDeletedDocument =

        getDocument(createSuccessfully("escidoc_ou_create.xml"));
        final String id = getObjidValue(toBeDeletedDocument);
        open(id, getTheLastModificationParam(true, id, "Opened to delete, should be declined!"));
        close(id, getTheLastModificationParam(true, id, "Closed to delete, should be declined!"));

        // delete parent ou
        try {
            delete(id);
            failMissingException("Deleting OU with children has not been declined", ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }

        // check if OU exists anymore
        try {
            retrieve(id);
        }
        catch (final Exception e) {
            failException("Could not retrieve OU after declined delete request.", e);
        }
    }

    /**
     * INFR-1075: Check if a parent OU gets updated in Lucene if a client OU has been deleted.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeleteChildOU() throws Exception {
        // create parent OU
        final Document parentOu = getDocument(createSuccessfully("escidoc_ou_create.xml"));
        final String parentOuId = getObjidValue(parentOu);

        // create child OU
        final Document childOu =
            getDocument(createSuccessfullyChild("escidoc_ou_create.xml", new String[] { parentOuId }));
        final String childOuId = getObjidValue(childOu);

        // delete child OU
        delete(childOuId);

        // check property "hasChildren" of parent OU
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_IDENTIFIER + "\"=" + parentOuId });

        String ouListXml = retrieveOrganizationalUnits(filterParams);

        assertXmlValidSrwResponse(ouListXml);

        Document createdDoc = getDocument(ouListXml);

        assertFalse("property hasChildren should be false", Boolean.valueOf(selectSingleNode(
            createdDoc,
            XPATH_SRW_RESPONSE_RECORD + "[1]" + XPATH_SRW_RESPONSE_OBJECT_SUBPATH
                + XPATH_ORGANIZATIONAL_UNIT_PROPERTIES + "/" + NAME_HAS_CHILDREN).getTextContent()));
    }
}
