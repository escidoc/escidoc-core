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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.HashMap;
import java.util.Map;

/**
 * Testing retrieveParents.
 * 
 * @author TTE
 * 
 */
@RunWith(value = Parameterized.class)
public class RetrieveParentObjectsTest extends OrganizationalUnitTestBase {

    /**
     * @param transport
     *            The transport identifier.
     */
    public RetrieveParentObjectsTest(final int transport) {
        super(transport);
    }

    /**
     * Test retrieving the list of parents of an existing organizational unit
     * that has two parents.
     * 
     * @test.name Retrieve Parents - Success - 2 Parents
     * @test.id OUM_RPOU-1
     * @test.input
     *          <ul>
     *          <li>Id of an existing child ou that has two parents</li>
     *          </ul>
     * @test.expected: Xml representation of a list of organizational unit
     *                 representations according to schema
     *                 "organizational-unit-list.xsd"
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumRpou1() throws Exception {

        // create two parents
        final String parent1Xml = createSuccessfully("escidoc_ou_create.xml");
        final String parent1Id = getObjidValue(getDocument(parent1Xml));
        final String parent2Xml = createSuccessfully("escidoc_ou_create.xml");
        final String parent2Id = getObjidValue(getDocument(parent2Xml));

        // store parents in map for later assertions
        final Map<String, String> expectedParents =
            new HashMap<String, String>(2);
        expectedParents.put(parent1Id, parent1Xml);
        expectedParents.put(parent2Id, parent2Xml);

        // create child with the two parents
        final String childXml =
            createSuccessfullyChild("escidoc_ou_create.xml", new String[] {
                parent1Id, parent2Id });
        final String childId = getObjidValue(getDocument(childXml));

        String parentsXml = null;
        try {
            parentsXml = retrieveParentObjects(childId);
        }
        catch (final Exception e) {
            failException("Retrieving parents of existing child ou failed.", e);
        }
        assertOrganizationalUnitList("Retrieving parents failed.",
             expectedParents, parentsXml);
    }

    /**
     * Test retrieving the list of parents of an existing top level
     * organizational unit (that has no parents).
     * 
     * @test.name Retrieve Parents - Success - Top Level
     * @test.id OUM_RPOU-1-2
     * @test.input
     *          <ul>
     *          <li>Id of an existing child ou that has no parents</li>
     *          </ul>
     * @test.expected: Xml representation of an empty list of organizational
     *                 unit representations according to schema
     *                 "organizational-unit-list.xsd".
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
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
            failException(
                "Retrieving parents of existing top level ou failed.", e);
        }
        assertOrganizationalUnitList(
            "Retrieving parents of existing top level ou failed.",
            new HashMap<String, String>(),
            parentsXml);
    }

    /**
     * Test declining retrieving list of parents from an organizational unit
     * with providing an unknown id.
     * 
     * @test.name Retrieve Parents - Unknown Id
     * @test.id OUM_RPOU-2
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
     * Test declining retrieving list of parents from an organizational unit
     * with providing the id of a resource of another type.
     * 
     * @test.name Retrieve Parents - Id of Another Resource Type
     * @test.id OUM_RPOU-2-2
     * @test.input
     *          <ul>
     *          <li>Id of a resource of another type</li>
     *          </ul>
     * @test.expected: OrganizationalUnitNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumRpou2_2() throws Exception {

        Class ec = OrganizationalUnitNotFoundException.class;
        try {
            retrieveParentObjects(CONTEXT_ID);
            failMissingException("Retrieving of parents of organizational unit"
                + " with id of resource of another resource type"
                + " has not been declined.", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Retrieving of parents of organizational unit"
                + " with id of resource of another resource type"
                + " has not been declined, correctly.", ec, e);
        }
    }

    /**
     * Test declining retrieving list of parents without providing an id.
     * 
     * @test.name Retrieve Parents - Missing Id
     * @test.id OUM_RPOU-3
     * @test.input
     *          <ul>
     *          <li>No id is provided</li>
     *          </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumRpou3() throws Exception {

        Class ec = MissingMethodParameterException.class;
        try {
            retrieveParentObjects(null);
            failMissingException("Retrieving of parents of organizational unit"
                + " without id" + " has not been declined.", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Retrieving of parents of organizational unit"
                + " without id" + " has not been declined, correctly.", ec, e);
        }
    }

}
