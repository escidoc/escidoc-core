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
import de.escidoc.core.test.common.client.servlet.Constants;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RetrievePathListIT extends OrganizationalUnitTestBase {

    /**
     * Test retrieving a pathlist of an existing organizational unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumRpl1() throws Exception {

        // create hierarchy
        final Document parentOu1Document = getDocument(createSuccessfully("escidoc_ou_create.xml"));
        final String parentOuId1 = getObjidValue(parentOu1Document);
        final String parentOuId2 = getObjidValue(getDocument(createSuccessfully("escidoc_ou_create.xml")));
        String[] parents = new String[] { parentOuId1, parentOuId2 };
        final String parentOuId3 =
            getObjidValue(getDocument(createSuccessfullyChild("escidoc_ou_create.xml", parents)));

        parents = new String[] { parentOuId3 };

        final String parentOuId4 =
            getObjidValue(getDocument(createSuccessfullyChild("escidoc_ou_create.xml", parents)));
        final String parentOuId5 =
            getObjidValue(getDocument(createSuccessfullyChild("escidoc_ou_create.xml", parents)));

        parents = new String[] { parentOuId4, parentOuId5 };
        final String childOuId1 = getObjidValue(getDocument(createSuccessfullyChild("escidoc_ou_create.xml", parents)));

        Vector<String> path1 = new Vector<String>();
        path1.add(childOuId1);
        path1.add(parentOuId5);
        path1.add(parentOuId3);
        path1.add(parentOuId1);

        Vector<String> path2 = new Vector<String>();
        path2.add(childOuId1);
        path2.add(parentOuId5);
        path2.add(parentOuId3);
        path2.add(parentOuId2);

        Vector<String> path3 = new Vector<String>();
        path3.add(childOuId1);
        path3.add(parentOuId4);
        path3.add(parentOuId3);
        path3.add(parentOuId1);

        Vector<String> path4 = new Vector<String>();
        path4.add(childOuId1);
        path4.add(parentOuId4);
        path4.add(parentOuId3);
        path4.add(parentOuId2);

        Vector<Vector> expectedPathes = new Vector<Vector>(4);
        expectedPathes.add(path1);
        expectedPathes.add(path2);
        expectedPathes.add(path3);
        expectedPathes.add(path4);

        String pathListXml = null;
        try {
            pathListXml = retrievePathList(childOuId1);
        }
        catch (final Exception e) {
            failException("Retrieving path list of a child OU failed.", e);
        }
        assertXmlValidOrganizationalUnitPathList(pathListXml);
        Document pathListDocument = getDocument(pathListXml);

        // organizational-unit-path-list (root element)
        assertXlinkElement("Asserting organizational-unit-path-list failed.", pathListDocument,
            "/organizational-unit-path-list", Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/" + childOuId1
                + "/resources/path-list");

        final NodeList pathesNodeList =
            selectNodeList(pathListDocument, "/organizational-unit-path-list/organizational-unit-path");
        assertEquals("wrong number of paths retrieved!", 4, pathesNodeList.getLength());

        for (int i = 0; i < pathesNodeList.getLength(); i++) {
            final Node pathNode = pathesNodeList.item(i);
            final String xpathOuRef =
                "/organizational-unit-path-list/organizational-unit-path[" + (i + 1) + "]/organizational-unit-ref";
            final NodeList ouRefNodes = selectNodeList(pathListDocument, xpathOuRef);
            assertEquals("wrong number of ou refs retrieved, [" + xpathOuRef + "]", 4, ouRefNodes.getLength());

            boolean foundMatch = true;
            for (int j = 0; j < expectedPathes.size(); j++) {
                final Vector expectedPath = expectedPathes.get(j);
                foundMatch = true;
                for (int l = 0; l < expectedPath.size(); l++) {
                    final String toBeAssertedId = getObjidValue(pathNode, xpathOuRef + "[" + (l + 1) + "]");
                    if (!expectedPath.get(l).equals(toBeAssertedId)) {
                        foundMatch = false;
                        break;
                    }
                }
                if (foundMatch) {
                    expectedPathes.remove(expectedPath);
                    break;
                }
            }
            if (!foundMatch) {
                fail("Pathes are not as expected");
            }
        }
    }

    /**
     * Test retrieving a pathlist of an existing top-level organizational unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumRpl1_2() throws Exception {

        // create a top level ou
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final Document createdDocument = getDocument(createdXml);
        final String objid = getObjidValue(createdDocument);

        String pathList = null;
        try {
            pathList = retrievePathList(objid);
        }
        catch (final Exception e) {
            failException("Retrieving path list of existing to level OU failed.", e);
        }
        assertXmlValidOrganizationalUnitPathList(pathList);
        Document pathListDocument = getDocument(pathList);

        assertXlinkElement("Asserting organizational-unit-path-list failed.", pathListDocument,
            "/organizational-unit-path-list", Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/" + objid
                + "/resources/path-list");
        assertXmlExists("Missing organizational-unit-path element", pathListDocument,
            "/organizational-unit-path-list/organizational-unit-path");
        assertXmlNotExists("Unexpected 2nd organizational-unit-path element", pathListDocument,
            "/organizational-unit-path-list/*[2]");

        String expectedTitle = getTitleValue(createdDocument);
        assertReferencingElement("Asserting path-list element failed.", objid, expectedTitle, pathListDocument,
            "/organizational-unit-path-list/organizational-unit-path/organizational-unit-ref",
            Constants.ORGANIZATIONAL_UNIT_BASE_URI);

        assertXmlNotExists("Path list contains more than one element.", pathListDocument,
            "/organizational-unit-path-list/organizational-unit-path/*[2]");

    }

    /**
     * Tests declining retrieving a pathlist with providing an unknown id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumRpl2() throws Exception {

        Class ec = OrganizationalUnitNotFoundException.class;

        try {
            retrievePathList(UNKNOWN_ID);

            failMissingException("Retrieving path list of an OU" + " with unknown id has not been declined.", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Retrieving path list of an OU" + " with unknown id has not been declined, correctly.",
                ec, e);
        }

    }

    /**
     * Tests declining retrieving a pathlist with providing the id of a resource of another type.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumRpl2_2() throws Exception {

        Class ec = OrganizationalUnitNotFoundException.class;

        try {
            retrievePathList(CONTEXT_ID);

            failMissingException("Retrieving path list of an OU" + " with id of resource of another resource type"
                + " has not been declined.", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Retrieving path list of an OU" + " with id of resource of another resource type"
                + " has not been declined, correctly.", ec, e);
        }

    }

    /**
     * Tests declining retrieving a pathlist without providing an id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumRpl3() throws Exception {

        Class ec = MissingMethodParameterException.class;

        try {
            retrievePathList(null);

            failMissingException("Retrieving path list of an OU" + " without id" + " has not been declined.", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Retrieving path list of an OU" + " without id" + " has not been declined, correctly.",
                ec, e);
        }

    }

}
