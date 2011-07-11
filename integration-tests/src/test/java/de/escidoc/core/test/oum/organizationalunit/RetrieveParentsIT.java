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
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.Constants;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RetrieveParentsIT extends OrganizationalUnitTestBase {

    /**
     * Test retrieving the parent-ous of an organizational unit containing no parent-ou elements.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumRPO1_1() throws Exception {

        Document xml = EscidocAbstractTest.getDocument(createSuccessfully("escidoc_ou_create.xml"));
        String id = getObjidValue(xml);
        String parentOus = retrieveParents(id);
        assertXmlValidOrganizationalUnit(parentOus);
        assertParentOus(id, EscidocAbstractTest.getDocument(parentOus), new HashMap<String, String>(), startTimestamp);
    }

    /**
     * Test retrieving the parent-ous of an organizational unit containing 2 parent-ou elements.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumRPO1_2() throws Exception {

        // create two parents
        final String parent1Xml = createSuccessfully("escidoc_ou_create.xml");
        final String parent1Id = getObjidValue(EscidocAbstractTest.getDocument(parent1Xml));
        final String parent2Xml = createSuccessfully("escidoc_ou_create.xml");
        final String parent2Id = getObjidValue(EscidocAbstractTest.getDocument(parent2Xml));

        // store parents in map for later assertions
        final Map<String, String> expectedParents = new HashMap<String, String>(2);
        expectedParents.put(parent1Id, parent1Xml);
        expectedParents.put(parent2Id, parent2Xml);

        // create child with the two parents
        final String childXml = createSuccessfullyChild("escidoc_ou_create.xml", new String[] { parent1Id, parent2Id });
        final String childId = getObjidValue(EscidocAbstractTest.getDocument(childXml));

        String parentOus = retrieveParents(childId);
        assertXmlValidOrganizationalUnit(parentOus);
        assertParentOus(childId, EscidocAbstractTest.getDocument(parentOus), expectedParents, startTimestamp);
    }

    public void assertParentOus(
        final String organizationalUnitId, final Node parentOus, final Map<String, String> expectedParentOus,
        final String timestampBeforeLastModification) throws Exception {
        String messagePrefix = "OU parent-ous error: ";
        Node hrefNode = selectSingleNode(parentOus, XPATH_PARENTS + PART_XLINK_HREF);
        assertNotNull(messagePrefix + " No href found! ", hrefNode);
        String href1 = hrefNode.getNodeValue();
        assertEquals(messagePrefix + "href wrong baseurl! ", href1, Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/"
            + organizationalUnitId + "/" + NAME_PARENTS);
        final String xpathLastModificationDate = XPATH_PARENTS + PART_LAST_MODIFICATION_DATE;
        assertXmlExists(messagePrefix + "Missing last modification date. ", parentOus, xpathLastModificationDate);
        final String lastModificationDate = selectSingleNode(parentOus, xpathLastModificationDate).getTextContent();
        assertNotEquals(messagePrefix + "Empty last modification date. ", "", lastModificationDate);
        if (timestampBeforeLastModification != null) {
            assertTimestampIsEqualOrAfter(messagePrefix + "last-modification-date is not as expected. ",
                lastModificationDate, timestampBeforeLastModification);
        }
        Iterator<String> expectedIter = expectedParentOus.keySet().iterator();
        if (!expectedIter.hasNext()) {
            assertXmlNotExists("No parents expected! ", parentOus, XPATH_PARENT);
        }
        else {
            while (expectedIter.hasNext()) {
                String parentId = expectedIter.next();
                String href2 = Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/" + parentId;
                assertXmlExists("", parentOus, XPATH_PARENT + "[@href='" + href2 + "']");
                assertXmlExists("", parentOus, XPATH_PARENT + "[@href='" + href2 + "']/@title");
                assertXmlExists("", parentOus, XPATH_PARENT + "[@href='" + href2 + "']/@type");
            }

        }
    }

    /**
     * Test declining retrieving properties of organizational unit with providing unknown id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumRPO2() throws Exception {

        Class ec = OrganizationalUnitNotFoundException.class;
        try {
            retrieveParents(UNKNOWN_ID);
            EscidocAbstractTest.failMissingException(ec);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining retrieving properties of organizational unit with providing id of existing resource of another
     * resource type.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumRPO2_2() throws Exception {

        Class ec = OrganizationalUnitNotFoundException.class;
        try {
            retrieveParents(CONTEXT_ID);
            EscidocAbstractTest.failMissingException(ec);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining retrieving properties of organizational unit without providing id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumRPO3_1() throws Exception {

        Class ec = MissingMethodParameterException.class;
        try {
            retrieveParents(null);
            EscidocAbstractTest.failMissingException(ec);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining retrieving properties of organizational unit without providing id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumRPO3_2() throws Exception {

        Class ec = MissingMethodParameterException.class;
        try {
            retrieveParents("");
            EscidocAbstractTest.failMissingException(ec);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ec, e);
        }
    }

}
