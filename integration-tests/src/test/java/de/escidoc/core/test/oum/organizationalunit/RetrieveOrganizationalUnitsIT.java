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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import de.escidoc.core.test.EscidocAbstractTest;

public class RetrieveOrganizationalUnitsIT extends OrganizationalUnitTestBase {

    static final String TYPE = "tuples";

    static final String LANG = "iTQL";

    static final String FORMAT_CSV = "CSV";

    static final String FORMAT_SIMPLE = "Simple";

    static final String FORMAT_SPARQL = "Sparql";

    static final String FORMAT_TSV = "TSV";

    static final String FLUSH = "true";

    private static final Logger LOGGER = LoggerFactory.getLogger(RetrieveOrganizationalUnitsIT.class);

    /**
     * Test retrieving a list of organizational units that are top level organizational units.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumFrou1_1() throws Exception {

        // Create a top level OU to ensure there is at least one.
        createSuccessfully("escidoc_ou_create.xml");

        final String msg = "Retrieving filtered list of top level-ous failed.";
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { FILTER_TOP_LEVEL_OUS_ONLY + "=true" });

        String ouListXml = null;

        try {
            ouListXml = retrieveOrganizationalUnits(filterParams);
        }
        catch (final Exception e) {
            failException(msg, e);
        }
        assertXmlValidSrwResponse(ouListXml);
        Document createdDoc = getDocument(ouListXml);

        Set<String> listedOus = new HashSet<String>();
        NodeList ouNodes = selectNodeList(createdDoc, XPATH_SRW_ORGANIZATIONAL_UNIT_LIST_ORGANIZATIONAL_UNIT);
        assertTrue(msg + " No refs in result. ", ouNodes.getLength() > 0);
        for (int i = 0; i < ouNodes.getLength(); i++) {
            final String ouId =
                getObjidValue(createdDoc, XPATH_SRW_RESPONSE_RECORD + "[" + (i + 1) + "]"
                    + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_ORGANIZATIONAL_UNIT);

            // assert referenced once
            assertTrue(msg + " OU listed more than once [" + ouId + "]", listedOus.add(ouId));
        }

        // assert all ous in the list are top-level ous, i.e. they do not have
        // parent-ous elements.
        NodeList parentOuNodes = selectNodeList(createdDoc, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        assertEquals(msg + " Not all filtered organizational units are top-level-ous.", 0, parentOuNodes.getLength());
    }

    @Test
    public void testOumRetrieveAllOus() throws Exception {

        final String msg = "Retrieving filtered list of top level-ous failed.";

        String ouListXml = null;
        try {
            ouListXml = retrieveOrganizationalUnits(new HashMap<String, String[]>());
        }
        catch (final Exception e) {
            failException(msg, e);
        }
        assertXmlValidSrwResponse(ouListXml);
        Document createdDoc = getDocument(ouListXml);

        Set<String> listedOus = new HashSet<String>();
        NodeList ouNodes = selectNodeList(createdDoc, XPATH_SRW_ORGANIZATIONAL_UNIT_LIST_ORGANIZATIONAL_UNIT);
        assertTrue(msg + " No refs in result. ", ouNodes.getLength() > 0);
        for (int i = 0; i < ouNodes.getLength(); i++) {
            final String ouId =
                getObjidValue(createdDoc, XPATH_SRW_RESPONSE_RECORD + "[" + (i + 1) + "]"
                    + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_ORGANIZATIONAL_UNIT);

            // assert referenced once
            assertTrue(msg + " OU listed more than once [" + ouId + "]", listedOus.add(ouId));
        }

        // assert all ous in the list are top-level ous, i.e. they do not have
        // parent-ous elements.
        NodeList parentOuNodes = selectNodeList(createdDoc, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        assertEquals(msg + " Not all filtered organizational units are top-level-ous.", 0, parentOuNodes.getLength());
    }

    /**
     * Test retrieving a specified OrganizationalUnit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumFrou4() throws Exception {

        final String msg = "Retrieving filtered list of one created ou failed.";

        // create ou
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(createdXml);

        // store ou in map for later assertion
        final Map<String, String> expectedOus = new HashMap<String, String>(1);
        expectedOus.put(id, createdXml);

        // filter for created ou
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_IDENTIFIER + "\"=" + id });

        String ouListXml = null;

        try {
            ouListXml = retrieveOrganizationalUnits(filterParams);
        }
        catch (final Exception e) {
            failException(msg, e);
        }
        assertOrganizationalUnitSrwList(msg, null, expectedOus, ouListXml);
    }

    /**
     * Test retrieving OrganizationalUnits with combined filter criteria "top-level-organizational-unit" and a specified
     * id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumFrou5() throws Exception {

        final String msg = "Retrieving filtered list of specified top level ous failed.";

        // create 2 top level ous
        final String parent1Xml = createSuccessfully("escidoc_ou_create.xml");
        final String parent1Id = getObjidValue(parent1Xml);
        final String parent2Xml = createSuccessfully("escidoc_ou_create.xml");
        final String parent2Id = getObjidValue(parent2Xml);

        // store top level ous for later assertion
        final Map<String, String> expectedOus = new HashMap<String, String>(1);
        expectedOus.put(parent1Id, parent1Xml);
        expectedOus.put(parent2Id, parent2Xml);

        // create 2 children
        final String child1Xml = createSuccessfullyChild("escidoc_ou_create.xml", new String[] { parent1Id });
        final String child1Id = getObjidValue(child1Xml);
        final String child2Xml = createSuccessfullyChild("escidoc_ou_create.xml", new String[] { parent2Id, child1Id });
        final String child2Id = getObjidValue(child2Xml);

        // filter for created ous and for top level constraint
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_IDENTIFIER + "\"=" + parent1Id + " or "
            + "\"" + FILTER_IDENTIFIER + "\"=" + parent2Id + " or " + "\"" + FILTER_IDENTIFIER + "\"=" + child1Id
            + " or " + "\"" + FILTER_IDENTIFIER + "\"=" + child2Id + ") and " + "\"" + FILTER_TOP_LEVEL_OUS_ONLY
            + "\"=true" });

        String ouListXml = null;

        try {
            ouListXml = retrieveOrganizationalUnits(filterParams);
        }
        catch (final Exception e) {
            failException(msg, e);
        }
        assertOrganizationalUnitSrwList(msg, null, expectedOus, ouListXml);
    }

    /**
     * Test retrieving organizational units with filter that must result in empty list, because a resource of another
     * resource type is addressed.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumFrou9() throws Exception {

        final String msg = "Retrieving filtered list that is empty failed.";

        // filter for another resource type
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_IDENTIFIER + "\"=" + CONTEXT_ID });

        String ouListXml = null;

        try {
            ouListXml = retrieveOrganizationalUnits(filterParams);
        }
        catch (final Exception e) {
            failException(msg, e);
        }
        assertOrganizationalUnitSrwList(msg, null, new HashMap<String, String>(), ouListXml);
    }

    /**
     * Test retrieving organizational units with filter that must result in empty list.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumFrou10() throws Exception {

        final String msg = "Retrieving filtered list that is empty failed.";

        // create parent
        final String parent1Id = createSuccessfully("escidoc_ou_create.xml", 1)[0];

        // create child
        final String child1Xml = createSuccessfullyChild("escidoc_ou_create.xml", new String[] { parent1Id });
        final String child1Id = getObjidValue(child1Xml);

        // filter for created child ou and for top level constraint
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_IDENTIFIER + "\"=" + child1Id + " and "
            + "\"" + FILTER_TOP_LEVEL_OUS_ONLY + "\"=true" });

        String ouListXml = null;

        try {
            ouListXml = retrieveOrganizationalUnits(filterParams);
        }
        catch (final Exception e) {
            failException(msg, e);
        }
        assertOrganizationalUnitSrwList(msg, null, new HashMap<String, String>(), ouListXml);
    }

    /**
     * Test retrieving organizational units after creating a child (before create the new parent has no other
     * children).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumFrou11() throws Exception {

        final String msg = "Retrieving filtered list of one created ou failed.";

        // create parent ou
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String parentId = getObjidValue(createdXml);

        // check if property "has-children" is false
        assertXmlEquals("OU error: " + " has-children", createdXml, XPATH_ORGANIZATIONAL_UNIT_HAS_CHILDREN, "false");

        // create child ou
        createSuccessfullyChild("escidoc_ou_create.xml", new String[] { parentId });

        // filter for created parent ou
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_IDENTIFIER + "\"=" + parentId });

        String ouListXml = null;

        try {
            ouListXml = retrieveOrganizationalUnits(filterParams);
        }
        catch (final Exception e) {
            failException(msg, e);
        }

        // check if property "has-children" is now true
        assertXmlEquals("OU error: " + " has-children", ouListXml, XPATH_SRW_ORGANIZATIONAL_UNIT_LIST_MEMBER
            + XPATH_ORGANIZATIONAL_UNIT_HAS_CHILDREN, "true");
    }

    /**
     * Test retrieving organizational units after updating the parents of a child (before update the new parent has no
     * other children).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumFrou12() throws Exception {

        final String msg = "Retrieving filtered list of one created ou failed.";

        // create parent ou
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String parentId = getObjidValue(createdXml);

        // check if property "has-children" is false
        assertXmlEquals("OU error: " + " has-children", createdXml, XPATH_ORGANIZATIONAL_UNIT_HAS_CHILDREN, "false");

        // create child ou
        final String childXml = createSuccessfully("escidoc_ou_create.xml");
        LOGGER.info("starting testOumFrou12 at "
            + new DateTime(System.currentTimeMillis() + (60 * 60 * 1000), DateTimeZone.UTC).toString());

        Document toBeUpdatedDocument = getDocument(childXml);

        // and add new parent to child
        // this is done by replacing the parent ous element
        deleteElement(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        insertParentsElement(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS,
            new String[] { parentId, null }, false);
        update(getObjidValue(childXml), toString(toBeUpdatedDocument, false).replaceAll(SREL_PREFIX_TEMPLATES,
            SREL_PREFIX_ESCIDOC));

        // filter for created parent ou
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_IDENTIFIER + "\"=" + parentId });

        String ouListXml = null;

        try {
            ouListXml = retrieveOrganizationalUnits(filterParams);
        }
        catch (final Exception e) {
            failException(msg, e);
        }

        // check if property "has-children" is now true
        assertXmlEquals("OU error: " + " has-children", ouListXml, XPATH_SRW_ORGANIZATIONAL_UNIT_LIST_MEMBER
            + XPATH_ORGANIZATIONAL_UNIT_HAS_CHILDREN, "true");
    }

    /**
     * Test retrieving Organizational-Units sorted from the repository.
     *
     * @throws Exception
     */
    @Test
    public void testRetrieveOusSorted() throws Exception {
        String createdXml = createSuccessfully("escidoc_ou_create.xml");
        String lmd = getLastModificationDateValue(getDocument(createdXml));
        String ouId = getObjidValue(createdXml);
        open(getObjidValue(createdXml), getTheLastModificationParam(true, ouId, "Opened organizational unit '" + ouId
            + "'."));

        createSuccessfully("escidoc_ou_create.xml");

        createdXml = createSuccessfully("escidoc_ou_create.xml");
        ouId = getObjidValue(createdXml);
        open(getObjidValue(createdXml), getTheLastModificationParam(true, ouId, "Opened organizational unit '" + ouId
            + "'."));

        createSuccessfully("escidoc_ou_create.xml");

        HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"PID\"=escidoc* and \"/last-modification-date\" >= "
            + lmd + " sortBy " + "\"/sort/properties/public-status\"/sort.ascending "
            + "\"/sort/properties/creation-date\"/sort.descending" });
        String xml = retrieveOrganizationalUnits(filterParams);

        assertXmlValidSrwResponse(xml);

        NodeList primNodes =
            selectNodeList(EscidocAbstractTest.getDocument(xml), XPATH_SRW_ORGANIZATIONAL_UNIT_LIST_ORGANIZATIONAL_UNIT
                + "/properties/public-status");
        NodeList secNodes =
            selectNodeList(EscidocAbstractTest.getDocument(xml), XPATH_SRW_ORGANIZATIONAL_UNIT_LIST_ORGANIZATIONAL_UNIT
                + "/properties/creation-date");
        assertEquals("search result doesnt contain expected number of hits", 4, primNodes.getLength());
        String lastPrim = LOWEST_COMPARABLE;
        String lastSec = HIGHEST_COMPARABLE;

        for (int count = 0; count < primNodes.getLength(); count++) {
            if (primNodes.item(count).getTextContent().compareTo(lastPrim) < 0) {
                assertTrue("wrong sortorder", false);
            }
            if (primNodes.item(count).getTextContent().compareTo(lastPrim) > 0) {
                lastSec = HIGHEST_COMPARABLE;
            }
            if (secNodes.item(count).getTextContent().compareTo(lastSec) > 0) {
                assertTrue("wrong sortorder", false);
            }
            lastPrim = primNodes.item(count).getTextContent();
            lastSec = secNodes.item(count).getTextContent();
        }
    }

    /**
     * Test successfully retrieving an explain response.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testExplainRetrieveOrganizationalUnits() throws Exception {
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_EXPLAIN, new String[] { "" });

        String result = null;

        try {
            result = retrieveOrganizationalUnits(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
    }

    /**
     * The method replaces all line breaks in the provided String with a provided character.
     *
     * @param in          given string
     * @param replaceWith replacement
     * @return string containing the given replacement instead of line break
     */
    public static String replaceNewlines(final String in, final String replaceWith) {
        return in.replaceAll("\r", replaceWith).replaceAll("\n", replaceWith);
    }

    public static String getIdFromURI(final String uri) {
        String result = uri;
        int index = uri.lastIndexOf("/");
        int indexOfBrace = uri.indexOf(">");
        if ((index != -1) && (indexOfBrace != -1)) {
            result = uri.substring(index + 1, indexOfBrace);
        }
        return result;
    }

}
