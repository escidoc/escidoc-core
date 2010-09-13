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

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Tests persistent ous hierarchy.
 * 
 * @author TTE
 * 
 */
@RunWith(value = Parameterized.class)
public class PersistentOUsTest extends OrganizationalUnitTestBase {

    private static final String PERSISTENT_1 = "escidoc:persistent1";

    private static final String PERSISTENT_11 = "escidoc:persistent11";

    private static final String PERSISTENT_13 = "escidoc:persistent13";

    private static final String PERSISTENT_25 = "escidoc:persistent25";

    private static final String PERSISTENT_26 = "escidoc:persistent26";

    private static final String PERSISTENT_27 = "escidoc:persistent27";

    private static final String PERSISTENT_28 = "escidoc:persistent28";

    private static final String PERSISTENT_29 = "escidoc:persistent29";

    private static final String PERSISTENT_30 = "escidoc:persistent30";

    private static final String PERSISTENT_31 = "escidoc:persistent31";

    private static final String PERSISTENT_33 = "escidoc:persistent33";

    private static final String PERSISTENT_34 = "escidoc:persistent34";

    private static final String PERSISTENT_35 = "escidoc:persistent35";

    private static final String PERSISTENT_36 = "escidoc:persistent36";

    private static final String PERSISTENT_37 = "escidoc:persistent37";

    private static final String[] PERSISTENT_1_CHILDREN = { PERSISTENT_26 };

    private static final String[] PERSISTENT_11_CHILDREN =
        { PERSISTENT_31, PERSISTENT_33 };

    private static final String[] PERSISTENT_13_CHILDREN =
        { PERSISTENT_11, PERSISTENT_25, PERSISTENT_27, PERSISTENT_28,
            PERSISTENT_29 };

    private static final String[] PERSISTENT_25_CHILDREN = { PERSISTENT_1 };

    private static final String[] PERSISTENT_26_CHILDREN = {};

    private static final String[] PERSISTENT_27_CHILDREN =
        { PERSISTENT_30, PERSISTENT_31 };

    private static final String[] PERSISTENT_28_CHILDREN =
        { PERSISTENT_34, PERSISTENT_35 };

    private static final String[] PERSISTENT_29_CHILDREN =
        { PERSISTENT_36, PERSISTENT_37 };

    private static final String[] PERSISTENT_30_CHILDREN = {};

    private static final String[] PERSISTENT_31_CHILDREN = {};

    private static final String[] PERSISTENT_33_CHILDREN = {};

    private static final String[] PERSISTENT_34_CHILDREN = {};

    private static final String[] PERSISTENT_35_CHILDREN = {};

    private static final String[] PERSISTENT_36_CHILDREN = {};

    private static final String[] PERSISTENT_37_CHILDREN = {};

    private static final String[] PERSISTENT_1_PARENTS = { PERSISTENT_25 };

    private static final String[] PERSISTENT_11_PARENTS = { PERSISTENT_13 };

    private static final String[] PERSISTENT_13_PARENTS = {};

    private static final String[] PERSISTENT_25_PARENTS = { PERSISTENT_13 };

    private static final String[] PERSISTENT_26_PARENTS = { PERSISTENT_1 };

    private static final String[] PERSISTENT_27_PARENTS = { PERSISTENT_13 };

    private static final String[] PERSISTENT_28_PARENTS = { PERSISTENT_13 };

    private static final String[] PERSISTENT_29_PARENTS = { PERSISTENT_13 };

    private static final String[] PERSISTENT_30_PARENTS = { PERSISTENT_27 };

    private static final String[] PERSISTENT_31_PARENTS =
        { PERSISTENT_11, PERSISTENT_27 };

    private static final String[] PERSISTENT_33_PARENTS = { PERSISTENT_11 };

    private static final String[] PERSISTENT_34_PARENTS = { PERSISTENT_28 };

    private static final String[] PERSISTENT_35_PARENTS = { PERSISTENT_28 };

    private static final String[] PERSISTENT_36_PARENTS = { PERSISTENT_29 };

    private static final String[] PERSISTENT_37_PARENTS = { PERSISTENT_29 };

    private static final String[][] PERSISTENT_1_PATH =
        { { PERSISTENT_1, PERSISTENT_25, PERSISTENT_13 } };

    private static final String[][] PERSISTENT_11_PATH =
        { { PERSISTENT_11, PERSISTENT_13 } };

    private static final String[][] PERSISTENT_13_PATH = { { PERSISTENT_13 } };

    private static final String[][] PERSISTENT_25_PATH =
        { { PERSISTENT_25, PERSISTENT_13 } };

    private static final String[][] PERSISTENT_26_PATH =
        { { PERSISTENT_26, PERSISTENT_1, PERSISTENT_25, PERSISTENT_13 } };

    private static final String[][] PERSISTENT_27_PATH =
        { { PERSISTENT_27, PERSISTENT_13 } };

    private static final String[][] PERSISTENT_28_PATH =
        { { PERSISTENT_28, PERSISTENT_13 } };

    private static final String[][] PERSISTENT_29_PATH =
        { { PERSISTENT_29, PERSISTENT_13 } };

    private static final String[][] PERSISTENT_30_PATH =
        { { PERSISTENT_30, PERSISTENT_27, PERSISTENT_13 } };

    private static final String[][] PERSISTENT_31_PATH =
        { { PERSISTENT_31, PERSISTENT_11, PERSISTENT_13 },
            { PERSISTENT_31, PERSISTENT_27, PERSISTENT_13 } };

    private static final String[][] PERSISTENT_33_PATH =
        { { PERSISTENT_33, PERSISTENT_11, PERSISTENT_13 } };

    private static final String[][] PERSISTENT_34_PATH =
        { { PERSISTENT_34, PERSISTENT_28, PERSISTENT_13 } };

    private static final String[][] PERSISTENT_35_PATH =
        { { PERSISTENT_35, PERSISTENT_28, PERSISTENT_13 } };

    private static final String[][] PERSISTENT_36_PATH =
        { { PERSISTENT_36, PERSISTENT_29, PERSISTENT_13 } };

    private static final String[][] PERSISTENT_37_PATH =
        { { PERSISTENT_37, PERSISTENT_29, PERSISTENT_13 } };

    /**
     * @param transport
     *            The transport identifier.
     */
    public PersistentOUsTest(final int transport) {
        super(transport);
    }

    /**
     * Test retrieving the children of ou 'escidoc:persistent1'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveChildrenOfEscidocPersistent1() throws Exception {

        checkRetrieveChildren(PERSISTENT_1, PERSISTENT_1_CHILDREN);
    }

    /**
     * Test retrieving the children of ou 'escidoc:persistent11'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveChildrenOfEscidocPersistent11() throws Exception {

        checkRetrieveChildren(PERSISTENT_11, PERSISTENT_11_CHILDREN);
    }

    /**
     * Test retrieving the children of ou 'escidoc:persistent13'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveChildrenOfEscidocPersistent13() throws Exception {

        checkRetrieveChildren(PERSISTENT_13, PERSISTENT_13_CHILDREN);
    }

    /**
     * Test retrieving the children of ou 'escidoc:persistent25'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveChildrenOfEscidocPersistent25() throws Exception {

        checkRetrieveChildren(PERSISTENT_25, PERSISTENT_25_CHILDREN);
    }

    /**
     * Test retrieving the children of ou 'escidoc:persistent26'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveChildrenOfEscidocPersistent26() throws Exception {

        checkRetrieveChildren(PERSISTENT_26, PERSISTENT_26_CHILDREN);
    }

    /**
     * Test retrieving the children of ou 'escidoc:persistent27'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveChildrenOfEscidocPersistent27() throws Exception {

        checkRetrieveChildren(PERSISTENT_27, PERSISTENT_27_CHILDREN);
    }

    /**
     * Test retrieving the children of ou 'escidoc:persistent28'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveChildrenOfEscidocPersistent28() throws Exception {

        checkRetrieveChildren(PERSISTENT_28, PERSISTENT_28_CHILDREN);
    }

    /**
     * Test retrieving the children of ou 'escidoc:persistent29'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveChildrenOfEscidocPersistent29() throws Exception {

        checkRetrieveChildren(PERSISTENT_29, PERSISTENT_29_CHILDREN);
    }

    /**
     * Test retrieving the children of ou 'escidoc:persistent30'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveChildrenOfEscidocPersistent30() throws Exception {

        checkRetrieveChildren(PERSISTENT_30, PERSISTENT_30_CHILDREN);
    }

    /**
     * Test retrieving the children of ou 'escidoc:persistent31'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveChildrenOfEscidocPersistent31() throws Exception {

        checkRetrieveChildren(PERSISTENT_31, PERSISTENT_31_CHILDREN);
    }

    /**
     * Test retrieving the children of ou 'escidoc:persistent33'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveChildrenOfEscidocPersistent33() throws Exception {

        checkRetrieveChildren(PERSISTENT_33, PERSISTENT_33_CHILDREN);
    }

    /**
     * Test retrieving the children of ou 'escidoc:persistent34'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveChildrenOfEscidocPersistent34() throws Exception {

        checkRetrieveChildren(PERSISTENT_34, PERSISTENT_34_CHILDREN);
    }

    /**
     * Test retrieving the children of ou 'escidoc:persistent35'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveChildrenOfEscidocPersistent35() throws Exception {

        checkRetrieveChildren(PERSISTENT_35, PERSISTENT_35_CHILDREN);
    }

    /**
     * Test retrieving the children of ou 'escidoc:persistent36'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveChildrenOfEscidocPersistent36() throws Exception {

        checkRetrieveChildren(PERSISTENT_36, PERSISTENT_36_CHILDREN);
    }

    /**
     * Test retrieving the children of ou 'escidoc:persistent37'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveChildrenOfEscidocPersistent37() throws Exception {

        checkRetrieveChildren(PERSISTENT_37, PERSISTENT_37_CHILDREN);
    }

    /**
     * Test retrieving the parents of ou 'escidoc:persistent1'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveParentsOfEscidocPersistent1() throws Exception {

        checkRetrieveParents(PERSISTENT_1, PERSISTENT_1_PARENTS);
    }

    /**
     * Test retrieving the parents of ou 'escidoc:persistent11'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveParentsOfEscidocPersistent11() throws Exception {

        checkRetrieveParents(PERSISTENT_11, PERSISTENT_11_PARENTS);
    }

    /**
     * Test retrieving the parents of ou 'escidoc:persistent13'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveParentsOfEscidocPersistent13() throws Exception {

        checkRetrieveParents(PERSISTENT_13, PERSISTENT_13_PARENTS);
    }

    /**
     * Test retrieving the parents of ou 'escidoc:persistent25'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveParentsOfEscidocPersistent25() throws Exception {

        checkRetrieveParents(PERSISTENT_25, PERSISTENT_25_PARENTS);
    }

    /**
     * Test retrieving the parents of ou 'escidoc:persistent26'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveParentsOfEscidocPersistent26() throws Exception {

        checkRetrieveParents(PERSISTENT_26, PERSISTENT_26_PARENTS);
    }

    /**
     * Test retrieving the parents of ou 'escidoc:persistent27'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveParentsOfEscidocPersistent27() throws Exception {

        checkRetrieveParents(PERSISTENT_27, PERSISTENT_27_PARENTS);
    }

    /**
     * Test retrieving the parents of ou 'escidoc:persistent28'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveParentsOfEscidocPersistent28() throws Exception {

        checkRetrieveParents(PERSISTENT_28, PERSISTENT_28_PARENTS);
    }

    /**
     * Test retrieving the parents of ou 'escidoc:persistent29'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveParentsOfEscidocPersistent29() throws Exception {

        checkRetrieveParents(PERSISTENT_29, PERSISTENT_29_PARENTS);
    }

    /**
     * Test retrieving the parents of ou 'escidoc:persistent30'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveParentsOfEscidocPersistent30() throws Exception {

        checkRetrieveParents(PERSISTENT_30, PERSISTENT_30_PARENTS);
    }

    /**
     * Test retrieving the parents of ou 'escidoc:persistent31'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveParentsOfEscidocPersistent31() throws Exception {

        checkRetrieveParents(PERSISTENT_31, PERSISTENT_31_PARENTS);
    }

    /**
     * Test retrieving the parents of ou 'escidoc:persistent33'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveParentsOfEscidocPersistent33() throws Exception {

        checkRetrieveParents(PERSISTENT_33, PERSISTENT_33_PARENTS);
    }

    /**
     * Test retrieving the parents of ou 'escidoc:persistent34'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveParentsOfEscidocPersistent34() throws Exception {

        checkRetrieveParents(PERSISTENT_34, PERSISTENT_34_PARENTS);
    }

    /**
     * Test retrieving the parents of ou 'escidoc:persistent35'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveParentsOfEscidocPersistent35() throws Exception {

        checkRetrieveParents(PERSISTENT_35, PERSISTENT_35_PARENTS);
    }

    /**
     * Test retrieving the parents of ou 'escidoc:persistent36'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveParentsOfEscidocPersistent36() throws Exception {

        checkRetrieveParents(PERSISTENT_36, PERSISTENT_36_PARENTS);
    }

    /**
     * Test retrieving the parents of ou 'escidoc:persistent37'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveParentsOfEscidocPersistent37() throws Exception {

        checkRetrieveParents(PERSISTENT_37, PERSISTENT_37_PARENTS);
    }

    /**
     * Test retrieving the paths of ou 'escidoc:persistent1'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrievePathsOfEscidocPersistent1() throws Exception {

        checkRetrievePath(PERSISTENT_1, PERSISTENT_1_PATH);
    }

    /**
     * Test retrieving the paths of ou 'escidoc:persistent11'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrievePathsOfEscidocPersistent11() throws Exception {

        checkRetrievePath(PERSISTENT_11, PERSISTENT_11_PATH);
    }

    /**
     * Test retrieving the paths of ou 'escidoc:persistent13'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrievePathsOfEscidocPersistent13() throws Exception {

        checkRetrievePath(PERSISTENT_13, PERSISTENT_13_PATH);
    }

    /**
     * Test retrieving the paths of ou 'escidoc:persistent25'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrievePathsOfEscidocPersistent25() throws Exception {

        checkRetrievePath(PERSISTENT_25, PERSISTENT_25_PATH);
    }

    /**
     * Test retrieving the paths of ou 'escidoc:persistent26'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrievePathsOfEscidocPersistent26() throws Exception {

        checkRetrievePath(PERSISTENT_26, PERSISTENT_26_PATH);
    }

    /**
     * Test retrieving the paths of ou 'escidoc:persistent27'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrievePathsOfEscidocPersistent27() throws Exception {

        checkRetrievePath(PERSISTENT_27, PERSISTENT_27_PATH);
    }

    /**
     * Test retrieving the paths of ou 'escidoc:persistent28'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrievePathsOfEscidocPersistent28() throws Exception {

        checkRetrievePath(PERSISTENT_28, PERSISTENT_28_PATH);
    }

    /**
     * Test retrieving the paths of ou 'escidoc:persistent29'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrievePathsOfEscidocPersistent29() throws Exception {

        checkRetrievePath(PERSISTENT_29, PERSISTENT_29_PATH);
    }

    /**
     * Test retrieving the paths of ou 'escidoc:persistent30'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrievePathsOfEscidocPersistent30() throws Exception {

        checkRetrievePath(PERSISTENT_30, PERSISTENT_30_PATH);
    }

    /**
     * Test retrieving the paths of ou 'escidoc:persistent31'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrievePathsOfEscidocPersistent31() throws Exception {

        checkRetrievePath(PERSISTENT_31, PERSISTENT_31_PATH);
    }

    /**
     * Test retrieving the paths of ou 'escidoc:persistent33'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrievePathsOfEscidocPersistent33() throws Exception {

        checkRetrievePath(PERSISTENT_33, PERSISTENT_33_PATH);
    }

    /**
     * Test retrieving the paths of ou 'escidoc:persistent34'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrievePathsOfEscidocPersistent34() throws Exception {

        checkRetrievePath(PERSISTENT_34, PERSISTENT_34_PATH);
    }

    /**
     * Test retrieving the paths of ou 'escidoc:persistent35'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrievePathsOfEscidocPersistent35() throws Exception {

        checkRetrievePath(PERSISTENT_35, PERSISTENT_35_PATH);
    }

    /**
     * Test retrieving the paths of ou 'escidoc:persistent36'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrievePathsOfEscidocPersistent36() throws Exception {

        checkRetrievePath(PERSISTENT_36, PERSISTENT_36_PATH);
    }

    /**
     * Test retrieving the paths of ou 'escidoc:persistent37'.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrievePathsOfEscidocPersistent37() throws Exception {

        checkRetrievePath(PERSISTENT_37, PERSISTENT_37_PATH);
    }

    /**
     * Retrieve the children of ou depicted by id and compare them to the array
     * of expected children.
     * 
     * @param id
     *            The parent ou.
     * @param expectedChildren
     *            The expected children.
     * @throws Exception
     *             If anything fails.
     */
    private void checkRetrieveChildren(
        final String id, final String[] expectedChildren) throws Exception {
        String children = retrieveChildObjects(id);
        assertXmlValidOrganizationalUnits(children);
        assertTrue("Children list of " + id
            + " does not contain the expected children!", compareContent(
            getIdsFromOrganizationalUnitList(children), expectedChildren));
    }

    /**
     * Retrieve the parents of ou depicted by id and compare them to the array
     * of expected parents.
     * 
     * @param id
     *            The parent ou.
     * @param expectedParents
     *            The expected parents.
     * @throws Exception
     *             If anything fails.
     */
    private void checkRetrieveParents(
        final String id, final String[] expectedParents) throws Exception {
        String parents = retrieveParentObjects(id);
        assertXmlValidOrganizationalUnits(parents);
        assertTrue("Parent list of " + id
            + " does not contain the expected parents!", compareContent(
            getIdsFromOrganizationalUnitList(parents), expectedParents));
    }

    /**
     * Retrieve the path list of ou depicted by id and compare them to the array
     * of the expected path list.
     * 
     * @param id
     *            The parent ou.
     * @param expectedPath
     *            The expected path.
     * @throws Exception
     *             If anything fails.
     */
    private void checkRetrievePath(
        final String id, final String[][] expectedPath) throws Exception {
        String path = retrievePathList(id);
        assertXmlValidOrganizationalUnitPathList(path);
        assertTrue("Retrieved path of " + id
            + " does not contain the expected organizational units!",
            comparePathLists(getIdsFromPathList(path), expectedPath));
    }
}
