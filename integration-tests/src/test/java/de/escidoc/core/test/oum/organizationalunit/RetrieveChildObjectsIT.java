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
 * Testing retrieveChildren.
 *
 * @author Torsten Tetteroo
 */
public class RetrieveChildObjectsIT extends OrganizationalUnitTestBase {

    /**
     * Test retrieving the list of children of an existing organizational unit that has two parents.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumRCou1() throws Exception {

        // create top level ou
        final String parentId = createSuccessfully("escidoc_ou_create.xml", 1)[0];

        // create two children
        String[] parentIds = new String[] { parentId };
        final String child1Xml = createSuccessfullyChild("escidoc_ou_create.xml", parentIds);
        final String child1Id = getObjidValue(child1Xml);
        final String child2Xml = createSuccessfullyChild("escidoc_ou_create.xml", parentIds);
        final String child2Id = getObjidValue(child2Xml);

        // store children in map for later assertions
        final Map<String, String> expectedChildren = new HashMap<String, String>(2);
        expectedChildren.put(child1Id, child1Xml);
        expectedChildren.put(child2Id, child2Xml);

        String childrenXml = null;
        try {
            childrenXml = retrieveChildObjects(parentId);
        }
        catch (final Exception e) {
            failException("Retrieving children of existing parent ou failed.", e);
        }
        assertOrganizationalUnitList("Retrieving children of existing top level ou failed.", expectedChildren,
            childrenXml);
    }

    /**
     * Test retrieving the list of children of a leave organizational unit (that has no children).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumRCou1_2() throws Exception {

        // create top level ou
        final String parentId = createSuccessfully("escidoc_ou_create.xml", 1)[0];

        // create child ou
        final String childXml = createSuccessfullyChild("escidoc_ou_create.xml", new String[] { parentId });
        final String childId = getObjidValue(childXml);

        String childrenXml = null;
        try {
            childrenXml = retrieveChildObjects(childId);
        }
        catch (final Exception e) {
            failException("Retrieving children of existing leave ou failed.", e);
        }
        assertOrganizationalUnitList("Retrieving children of existing top level ou failed.",
            new HashMap<String, String>(), childrenXml);
    }

    /**
     * Test declining retrieving list of children from an organizational unit with providing an unknown id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumRcou2() throws Exception {

        Class ec = OrganizationalUnitNotFoundException.class;
        try {
            retrieveChildObjects(UNKNOWN_ID);
            failMissingException("Retrieving of children of organizational unit"
                + " with unknown id has not been declined.", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Retrieving of children of organizational unit"
                + " with unknown id has not been declined, correctly.", ec, e);
        }
    }

    /**
     * Test declining retrieving list of children from an organizational unit with providing the id of a resource of
     * another type.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumRcou2_2() throws Exception {

        Class ec = OrganizationalUnitNotFoundException.class;
        try {
            retrieveChildObjects(CONTEXT_ID);
            failMissingException("Retrieving of children of organizational unit"
                + " with id of resource of another resource type" + " has not been declined.", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Retrieving of children of organizational unit"
                + " with id of resource of another resource type" + " has not been declined, correctly.", ec, e);
        }
    }

    /**
     * Test declining retrieving list of children without providing an id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumRcou3() throws Exception {

        Class ec = MissingMethodParameterException.class;
        try {
            retrieveChildObjects(null);
            failMissingException("Retrieving of children of organizational unit" + " without id"
                + " has not been declined.", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Retrieving of children of organizational unit" + " without id"
                + " has not been declined, correctly.", ec, e);
        }
    }

}
