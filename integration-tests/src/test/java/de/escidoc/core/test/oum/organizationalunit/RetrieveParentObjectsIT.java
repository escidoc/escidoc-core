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

import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.OrganizationalUnitNotFoundException;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Testing retrieveParents.
 *
 * @author Torsten Tetteroo
 */
public class RetrieveParentObjectsIT extends OrganizationalUnitTestBase {

    /**
     * Test retrieving the list of parents of an existing organizational unit that has two parents.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumRpou1() throws Exception {

        // create two parents
        final String parent1Xml = createSuccessfully("escidoc_ou_create.xml");
        final String parent1Id = getObjidValue(getDocument(parent1Xml));
        final String parent2Xml = createSuccessfully("escidoc_ou_create.xml");
        final String parent2Id = getObjidValue(getDocument(parent2Xml));

        // store parents in map for later assertions
        final Map<String, String> expectedParents = new HashMap<String, String>(2);
        expectedParents.put(parent1Id, parent1Xml);
        expectedParents.put(parent2Id, parent2Xml);

        // create child with the two parents
        final String childXml = createSuccessfullyChild("escidoc_ou_create.xml", new String[] { parent1Id, parent2Id });
        final String childId = getObjidValue(getDocument(childXml));

        String parentsXml = null;
        try {
            parentsXml = retrieveParentObjects(childId);
        }
        catch (final Exception e) {
            failException("Retrieving parents of existing child ou failed.", e);
        }
        assertOrganizationalUnitList("Retrieving parents failed.", expectedParents, parentsXml);
    }

    /**
     * Test retrieving the list of parents of an existing top level organizational unit (that has no parents).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumRpou1_2() throws Exception {

        // create top-level ou
        final String objid = createSuccessfully("escidoc_ou_create.xml", 1)[0];

        // retrieveParents
        String parentsXml = null;
        try {
            parentsXml = retrieveParentObjects(objid);
        }
        catch (final Exception e) {
            failException("Retrieving parents of existing top level ou failed.", e);
        }
        assertOrganizationalUnitList("Retrieving parents of existing top level ou failed.",
            new HashMap<String, String>(), parentsXml);
    }

    /**
     * Test declining retrieving list of parents from an organizational unit with providing an unknown id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumRpou2() throws Exception {

        Class ec = OrganizationalUnitNotFoundException.class;
        try {
            retrieveParentObjects(UNKNOWN_ID);
            failMissingException("Retrieving of parents of organizational unit"
                + " with unknown id has not been declined.", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Retrieving of parents of organizational unit"
                + " with unknown id has not been declined, correctly.", ec, e);
        }
    }

    /**
     * Test declining retrieving list of parents from an organizational unit with providing the id of a resource of
     * another type.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumRpou2_2() throws Exception {

        Class ec = OrganizationalUnitNotFoundException.class;
        try {
            retrieveParentObjects(CONTEXT_ID);
            failMissingException("Retrieving of parents of organizational unit"
                + " with id of resource of another resource type" + " has not been declined.", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Retrieving of parents of organizational unit"
                + " with id of resource of another resource type" + " has not been declined, correctly.", ec, e);
        }
    }

    /**
     * Test declining retrieving list of parents without providing an id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumRpou3() throws Exception {

        Class ec = MissingMethodParameterException.class;
        try {
            retrieveParentObjects(null);
            failMissingException("Retrieving of parents of organizational unit" + " without id"
                + " has not been declined.", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Retrieving of parents of organizational unit" + " without id"
                + " has not been declined, correctly.", ec, e);
        }
    }

}
