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
 * Testing retrieveChildren.
 * 
 * @author TTE
 * 
 */
@RunWith(value = Parameterized.class)
public class RetrieveChildObjectsTest extends OrganizationalUnitTestBase {

    /**
     * @param transport
     *            The transport identifier.
     */
    public RetrieveChildObjectsTest(final int transport) {
        super(transport);
    }

    /**
     * Test retrieving the list of children of an existing organizational unit
     * that has two parents.
     * 
     * @test.name Retrieve Children - Success - 2 Children
     * @test.id OUM_RCOU-1
     * @test.input
     *          <ul>
     *          <li>Id of an existing child ou that has two children</li>
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
    public void testOumRCou1() throws Exception {

        // create top level ou
        final String parentId =
            createSuccessfully("escidoc_ou_create.xml", 1)[0];

        // create two children
        String[] parentIds = new String[] { parentId };
        final String child1Xml =
            createSuccessfullyChild("escidoc_ou_create.xml", parentIds);
        final String child1Id = getObjidValue(child1Xml);
        final String child2Xml =
            createSuccessfullyChild("escidoc_ou_create.xml", parentIds);
        final String child2Id = getObjidValue(child2Xml);

        // store children in map for later assertions
        final Map<String, String> expectedChildren =
            new HashMap<String, String>(2);
        expectedChildren.put(child1Id, child1Xml);
        expectedChildren.put(child2Id, child2Xml);

        String childrenXml = null;
        try {
            childrenXml = retrieveChildObjects(parentId);
        }
        catch (Exception e) {
            failException("Retrieving children of existing parent ou failed.",
                e);
        }
        assertOrganizationalUnitList(
            "Retrieving children of existing top level ou failed.",
            expectedChildren, childrenXml);
    }

    /**
     * Test retrieving the list of children of a leave organizational unit (that
     * has no children).
     * 
     * @test.name Retrieve Children - Success - Leave OU
     * @test.id OUM_RCOU-1-2
     * @test.input
     *          <ul>
     *          <li>Id of an existing leave ou that has no children</li>
     *          </ul>
     * @test.expected: Xml representation of an empty list of organizational
     *                 unit representations according to schema
     *                 "organizational-unit-list.xsd"
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOumRCou1_2() throws Exception {

        // create top level ou
        final String parentId =
            createSuccessfully("escidoc_ou_create.xml", 1)[0];

        // create child ou
        final String childXml =
            createSuccessfullyChild("escidoc_ou_create.xml",
                new String[] { parentId });
        final String childId = getObjidValue(childXml);

        String childrenXml = null;
        try {
            childrenXml = retrieveChildObjects(childId);
        }
        catch (Exception e) {
            failException("Retrieving children of existing leave ou failed.", e);
        }
        assertOrganizationalUnitList(
            "Retrieving children of existing top level ou failed.",
            new HashMap<String, String>(),
            childrenXml);
    }

    /**
     * Test declining retrieving list of children from an organizational unit
     * with providing an unknown id.
     * 
     * @test.name Retrieve Children - Unknown Id
     * @test.id OUM_RCOU-2
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
    public void testOumRcou2() throws Exception {

        Class ec = OrganizationalUnitNotFoundException.class;
        try {
            retrieveChildObjects(UNKNOWN_ID);
            failMissingException(
                "Retrieving of children of organizational unit"
                    + " with unknown id has not been declined.", ec);
        }
        catch (Exception e) {
            assertExceptionType("Retrieving of children of organizational unit"
                + " with unknown id has not been declined, correctly.", ec, e);
        }
    }

    /**
     * Test declining retrieving list of children from an organizational unit
     * with providing the id of a resource of another type.
     * 
     * @test.name Retrieve Children - Id of Another Resource Type
     * @test.id OUM_RCOU-2-2
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
    public void testOumRcou2_2() throws Exception {

        Class ec = OrganizationalUnitNotFoundException.class;
        try {
            retrieveChildObjects(CONTEXT_ID);
            failMissingException(
                "Retrieving of children of organizational unit"
                    + " with id of resource of another resource type"
                    + " has not been declined.", ec);
        }
        catch (Exception e) {
            assertExceptionType("Retrieving of children of organizational unit"
                + " with id of resource of another resource type"
                + " has not been declined, correctly.", ec, e);
        }
    }

    /**
     * Test declining retrieving list of children without providing an id.
     * 
     * @test.name Retrieve Children - Missing Id
     * @test.id OUM_RCOU-3
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
    public void testOumRcou3() throws Exception {

        Class ec = MissingMethodParameterException.class;
        try {
            retrieveChildObjects(null);
            failMissingException(
                "Retrieving of children of organizational unit" + " without id"
                    + " has not been declined.", ec);
        }
        catch (Exception e) {
            assertExceptionType("Retrieving of children of organizational unit"
                + " without id" + " has not been declined, correctly.", ec, e);
        }
    }

}
