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

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.test.EscidocRestSoapTestsBase;

public class RetrieveOrganizationalUnitsTest extends OrganizationalUnitTestBase {

    static final String TYPE = "tuples";

    static final String LANG = "iTQL";

    static final String FORMAT_CSV = "CSV";

    static final String FORMAT_SIMPLE = "Simple";

    static final String FORMAT_SPARQL = "Sparql";

    static final String FORMAT_TSV = "TSV";

    static final String FLUSH = "true";

    /**
     * @param transport
     *            The transport identifier.
     */
    public RetrieveOrganizationalUnitsTest(final int transport) {
        super(transport);
    }

    /**
     * Test retrieving a list of organizational units that are top level
     * organizational units.
     * 
     * @test.name Retrieve Organizational Units - Success - Top Level
     * @test.id OUM_FROUR-1-1
     * @test.input
     *          <ul>
     *          <li>Valid filter param defining filter for top level ous</li>
     *          </ul>
     * @test.expected: Xml representation of a list of organizational unit
     *                 representations according to schema
     *                 "organizational-unit-list.xsd". All ous are top level
     *                 ous.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumFrou1_1() throws Exception {

        final String msg = "Retrieving filtered list of top level-ous failed.";

        final String filterXml =
            "<param>"
                + "<filter name=\"top-level-organizational-units\"></filter>"
                + "</param>";
        String ouListXml = null;
        try {
            ouListXml = retrieveOrganizationalUnits(filterXml);
        }
        catch (Exception e) {
            failException(msg, e);
        }
        assertXmlValidOrganizationalUnits(ouListXml);
        Document createdDoc = getDocument(ouListXml);

        Set<String> listedOus = new HashSet<String>();
        NodeList ouNodes =
            selectNodeList(createdDoc,
                XPATH_ORGANIZATIONAL_UNIT_LIST_ORGANIZATIONAL_UNIT);
        assertTrue(msg + " No refs in result. ", ouNodes.getLength() > 0);
        for (int i = 0; i < ouNodes.getLength(); i++) {
            final String ouId =
                getObjidValue(createdDoc,
                    XPATH_ORGANIZATIONAL_UNIT_LIST_ORGANIZATIONAL_UNIT + "["
                        + (i + 1) + "]");

            // assert referenced once
            assertTrue(msg + " OU listed more than once [" + ouId + "]",
                listedOus.add(ouId));
        }

        // assert all ous in the list are top-level ous, i.e. they do not have
        // parent-ous elements.
        NodeList parentOuNodes =
            selectNodeList(createdDoc, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        assertEquals(msg
            + " Not all filtered organizational units are top-level-ous.", 0,
            parentOuNodes.getLength());
    }

    /**
     * Test retrieving a list of organizational units that are top level
     * organizational units.
     * 
     * @test.name Retrieve Organizational Units - Success - Top Level
     * @test.id OUM_FROUR-1-1
     * @test.input
     *          <ul>
     *          <li>Valid filter param defining filter for top level ous</li>
     *          </ul>
     * @test.expected: Xml representation of a list of organizational unit
     *                 representations according to schema
     *                 "organizational-unit-list.xsd". All ous are top level
     *                 ous.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumFrou1_1CQL() throws Exception {

        final String msg = "Retrieving filtered list of top level-ous failed.";
        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            FILTER_TOP_LEVEL_OUS_ONLY + "=true"});

        String ouListXml = null;

        try {
            ouListXml = retrieveOrganizationalUnits(filterParams);
        }
        catch (Exception e) {
            failException(msg, e);
        }
        assertXmlValidSrwResponse(ouListXml);
        Document createdDoc = getDocument(ouListXml);

        Set<String> listedOus = new HashSet<String>();
        NodeList ouNodes =
            selectNodeList(createdDoc,
                XPATH_SRW_ORGANIZATIONAL_UNIT_LIST_ORGANIZATIONAL_UNIT);
        assertTrue(msg + " No refs in result. ", ouNodes.getLength() > 0);
        for (int i = 0; i < ouNodes.getLength(); i++) {
            final String ouId =
                getObjidValue(createdDoc,
                    XPATH_SRW_RESPONSE_RECORD + "[" + (i + 1)
                    + "]/recordData/" + NAME_ORGANIZATIONAL_UNIT);

            // assert referenced once
            assertTrue(msg + " OU listed more than once [" + ouId + "]",
                listedOus.add(ouId));
        }

        // assert all ous in the list are top-level ous, i.e. they do not have
        // parent-ous elements.
        NodeList parentOuNodes =
            selectNodeList(createdDoc, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        assertEquals(msg
            + " Not all filtered organizational units are top-level-ous.", 0,
            parentOuNodes.getLength());
    }

    public void testOumRetrieveAllOus() throws Exception {

        final String msg = "Retrieving filtered list of top level-ous failed.";

        final String filterXml =
            "<param>"
            + "<limit>10</limit>"
            + "<offset>0</offset>"    
            + "</param>";
        String ouListXml = null;
        try {
            ouListXml = retrieveOrganizationalUnits(filterXml);
            //System.out.println("ous all " + ouListXml);
        }
        catch (Exception e) {
            failException(msg, e);
        }
        assertXmlValidOrganizationalUnits(ouListXml);
        Document createdDoc = getDocument(ouListXml);

        Set<String> listedOus = new HashSet<String>();
        NodeList ouNodes =
            selectNodeList(createdDoc,
                XPATH_ORGANIZATIONAL_UNIT_LIST_ORGANIZATIONAL_UNIT);
        assertTrue(msg + " No refs in result. ", ouNodes.getLength() > 0);
        for (int i = 0; i < ouNodes.getLength(); i++) {
            final String ouId =
                getObjidValue(createdDoc,
                    XPATH_ORGANIZATIONAL_UNIT_LIST_ORGANIZATIONAL_UNIT + "["
                        + (i + 1) + "]");

            // assert referenced once
            assertTrue(msg + " OU listed more than once [" + ouId + "]",
                listedOus.add(ouId));
        }

        // assert all ous in the list are top-level ous, i.e. they do not have
        // parent-ous elements.
        NodeList parentOuNodes =
            selectNodeList(createdDoc, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        assertEquals(msg
            + " Not all filtered organizational units are top-level-ous.", 0,
            parentOuNodes.getLength());
    }

    public void testOumRetrieveAllOusCQL() throws Exception {

        final String msg = "Retrieving filtered list of top level-ous failed.";

        String ouListXml = null;
        try {
            ouListXml = retrieveOrganizationalUnits(new HashMap<String, String[]>());
        }
        catch (Exception e) {
            failException(msg, e);
        }
        assertXmlValidSrwResponse(ouListXml);
        Document createdDoc = getDocument(ouListXml);

        Set<String> listedOus = new HashSet<String>();
        NodeList ouNodes =
            selectNodeList(createdDoc,
                XPATH_SRW_ORGANIZATIONAL_UNIT_LIST_ORGANIZATIONAL_UNIT);
        assertTrue(msg + " No refs in result. ", ouNodes.getLength() > 0);
        for (int i = 0; i < ouNodes.getLength(); i++) {
            final String ouId =
                getObjidValue(createdDoc,
                    XPATH_SRW_RESPONSE_RECORD + "[" + (i + 1)
                    + "]/recordData/" + NAME_ORGANIZATIONAL_UNIT);

            // assert referenced once
            assertTrue(msg + " OU listed more than once [" + ouId + "]",
                listedOus.add(ouId));
        }

        // assert all ous in the list are top-level ous, i.e. they do not have
        // parent-ous elements.
        NodeList parentOuNodes =
            selectNodeList(createdDoc, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        assertEquals(msg
            + " Not all filtered organizational units are top-level-ous.", 0,
            parentOuNodes.getLength());
    }

    /**
     * Test retrieving a specified OrganizationalUnit.
     * 
     * @test.name Retrieve Organizational Units - Success - Specified Object
     * @test.id OUM_FROUR-4
     * @test.input
     *          <ul>
     *          <li>Valid filter param defining filter for one specified ou</li>
     *          </ul>
     * @test.expected: Xml representation of a list of organizational unit
     *                 representations according to schema
     *                 "organizational-unit-list.xsd". Expected OU is contained
     *                 in list, only.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumFrou4() throws Exception {

        final String msg = "Retrieving filtered list of one created ou failed.";

        // create ou
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(createdXml);

        // store ou in map for later assertion
        final Map<String, String> expectedOus = new HashMap<String, String>(1);
        expectedOus.put(id, createdXml);

        // filter for created ou
        final String filterXml =
            "<param>"
                + "<filter name=\"http://purl.org/dc/elements/1.1/identifier\">"
                + "<id>" + id + "</id>" + "</filter>" + "</param>";

        String ouListXml = null;
        try {
            ouListXml = retrieveOrganizationalUnits(filterXml);
        }
        catch (Exception e) {
            failException(msg, e);
        }
        assertOrganizationalUnitList(msg, null, expectedOus, ouListXml);
    }

    /**
     * Test retrieving a specified OrganizationalUnit.
     * 
     * @test.name Retrieve Organizational Units - Success - Specified Object
     * @test.id OUM_FROUR-4
     * @test.input
     *          <ul>
     *          <li>Valid filter param defining filter for one specified ou</li>
     *          </ul>
     * @test.expected: Xml representation of a list of organizational unit
     *                 representations according to schema
     *                 "organizational-unit-list.xsd". Expected OU is contained
     *                 in list, only.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumFrou4CQL() throws Exception {

        final String msg = "Retrieving filtered list of one created ou failed.";

        // create ou
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String id = getObjidValue(createdXml);

        // store ou in map for later assertion
        final Map<String, String> expectedOus = new HashMap<String, String>(1);
        expectedOus.put(id, createdXml);

        // filter for created ou
        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "\"" + FILTER_IDENTIFIER + "\"=" + id});

        String ouListXml = null;

        try {
            ouListXml = retrieveOrganizationalUnits(filterParams);
        }
        catch (Exception e) {
            failException(msg, e);
        }
        assertOrganizationalUnitSrwList(msg, null, expectedOus, ouListXml);
    }

    /**
     * Test retrieving OrganizationalUnits with combined filter criteria
     * "top-level-organizational-unit" and a specified id.
     * 
     * @test.name Retrieve Organizational Units - Success - Combined Filter Top
     *            Level and Ids
     * @test.id OUM_FROUR-5
     * @test.input
     *          <ul>
     *          <li>Valid filter param defining filter for top level and
     *          specified ids</li>
     *          </ul>
     * @test.expected: Xml representation of a list of organizational unit
     *                 representations according to schema
     *                 "organizational-unit-list.xsd". Expected top level OU are
     *                 contained in list, only.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumFrou5() throws Exception {

        final String msg =
            "Retrieving filtered list of specified top level ous failed.";

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
        final String child1Xml =
            createSuccessfullyChild("escidoc_ou_create.xml",
                new String[] { parent1Id });
        final String child1Id = getObjidValue(child1Xml);
        final String child2Xml =
            createSuccessfullyChild("escidoc_ou_create.xml", new String[] {
                parent2Id, child1Id });
        final String child2Id = getObjidValue(child2Xml);

        // filter for created ous and for top level constraint
        final String filterXml =
            "<param>"
                + "<filter name=\"http://purl.org/dc/elements/1.1/identifier\">"
                + "<id>" + parent1Id + "</id>" + "<id>" + parent2Id + "</id>"
                + "<id>" + child1Id + "</id>" + "<id>" + child2Id + "</id>"
                + "</filter>"
                + "<filter name=\"top-level-organizational-units\"></filter>"
                + "</param>";

        String ouListXml = null;
        try {
            ouListXml = retrieveOrganizationalUnits(filterXml);
        }
        catch (Exception e) {
            failException(msg, e);
        }
        assertOrganizationalUnitList(msg, null, expectedOus, ouListXml);
    }

    /**
     * Test retrieving OrganizationalUnits with combined filter criteria
     * "top-level-organizational-unit" and a specified id.
     * 
     * @test.name Retrieve Organizational Units - Success - Combined Filter Top
     *            Level and Ids
     * @test.id OUM_FROUR-5
     * @test.input
     *          <ul>
     *          <li>Valid filter param defining filter for top level and
     *          specified ids</li>
     *          </ul>
     * @test.expected: Xml representation of a list of organizational unit
     *                 representations according to schema
     *                 "organizational-unit-list.xsd". Expected top level OU are
     *                 contained in list, only.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumFrou5CQL() throws Exception {

        final String msg =
            "Retrieving filtered list of specified top level ous failed.";

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
        final String child1Xml =
            createSuccessfullyChild("escidoc_ou_create.xml",
                new String[] { parent1Id });
        final String child1Id = getObjidValue(child1Xml);
        final String child2Xml =
            createSuccessfullyChild("escidoc_ou_create.xml", new String[] {
                parent2Id, child1Id });
        final String child2Id = getObjidValue(child2Xml);

        // filter for created ous and for top level constraint
        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "(\"" + FILTER_IDENTIFIER + "\"=" + parent1Id + " or "
            + "\"" + FILTER_IDENTIFIER + "\"=" + parent2Id + " or "
            + "\"" + FILTER_IDENTIFIER + "\"=" + child1Id + " or "
            + "\"" + FILTER_IDENTIFIER + "\"=" + child2Id + ") and "
            + "\"" + FILTER_TOP_LEVEL_OUS_ONLY + "\"=true"});

        String ouListXml = null;

        try {
            ouListXml = retrieveOrganizationalUnits(filterParams);
        }
        catch (Exception e) {
            failException(msg, e);
        }
        assertOrganizationalUnitSrwList(msg, null, expectedOus, ouListXml);
    }

    /**
     * Test retrieving OrganizationalUnits with an invalid filter xml.
     * 
     * @test.name Retrieve Organizational Units - Invalid Filter Param
     * @test.id OUM_FROUR-6
     * @test.input
     *          <ul>
     *          <li>Invalid filer param is provided</li>
     *          </ul>
     * @test.expected: XmlSchemaValidationException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumFrou6() throws Exception {

        final Class<XmlCorruptedException> ec = XmlCorruptedException.class;

        final String filterXml =
            "<param>"
                + "<filter name=\"http://purl.org/dc/elements/1.1/identifier\">"
                + "<id>escidoc:1</id>" + "<id>escidoc:2</id>"
                + "<id>escidoc:3</id>" + "</filter>"
                + "<filter name=\"top-level-ous\"></filter>" + "</params>";
        try {
            retrieveOrganizationalUnits(filterXml);
            failMissingException(
                "Retrieving OUs with invalid filter not declined.", ec);
        }
        catch (Exception e) {
            assertExceptionType(
                "Retrieving OUs with invalid filter not declined, correctly.",
                ec, e);
        }
    }

    /**
     * Test retrieving OrganizationalUnits with corrupted filter xml.
     * 
     * @test.name Retrieve Organizational Units - Corrupted Filter Param
     * @test.id OUM_FROUR-7
     * @test.input
     *          <ul>
     *          <li>Corrupted filer param is provided</li>
     *          </ul>
     * @test.expected: XmlCorruptedException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumFrou7() throws Exception {

        final Class< ? > ec = XmlCorruptedException.class;

        final String filterXml = "<param";
        try {
            retrieveOrganizationalUnits(filterXml);
            failMissingException(
                "Retrieving OUs with corrupted filter not declined.", ec);
        }
        catch (Exception e) {

            assertExceptionType(
                "Retrieving OUs with corrupted filter not declined, correctly.",
                ec, e);
        }
    }

    /**
     * Test retrieving OrganizationalUnits without providing a filter xml.
     * 
     * @test.name Retrieve Organizational Units - Missing Filter Param
     * @test.id OUM_FROUR-8
     * @test.input
     *          <ul>
     *          <li>No filer param is provided</li>
     *          </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumFrou8() throws Exception {

        final Class< ? > ec = MissingMethodParameterException.class;

        try {
            retrieveOrganizationalUnits((String) null);
            failMissingException("Retrieving OUs without filter not declined.",
                ec);
        }
        catch (Exception e) {
            assertExceptionType(
                "Retrieving OUs without filter not declined, correctly.", ec, e);
        }
    }

    /**
     * Test retrieving organizational units with filter that must result in
     * empty list, because a resource of another resource type is addressed.
     * 
     * @test.name Retrieve Organizational Units - Success - Wrong Resource Type
     * @test.id OUM_FROUR-9
     * @test.input
     *          <ul>
     *          <li>Valid filter param defining filter for if of an existing
     *          resource of another resource type.</li>
     *          </ul>
     * @test.expected: Xml representation of a list of organizational unit
     *                 representations according to schema
     *                 "organizational-unit-list.xsd". List is empty.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumFrou9() throws Exception {

        final String msg = "Retrieving filtered list that is empty failed.";

        // filter for another resource type
        final String filterXml =
            "<param>"
                + "<filter name=\"http://purl.org/dc/elements/1.1/identifier\">"
                + "<id>" + CONTEXT_ID + "</id>" + "</filter>" + "</param>";

        String ouListXml = null;
        try {
            ouListXml = retrieveOrganizationalUnits(filterXml);
        }
        catch (Exception e) {
            failException(msg, e);
        }
        assertOrganizationalUnitList(msg, null, new HashMap<String, String>(),
            ouListXml);
    }

    /**
     * Test retrieving organizational units with filter that must result in
     * empty list, because a resource of another resource type is addressed.
     * 
     * @test.name Retrieve Organizational Units - Success - Wrong Resource Type
     * @test.id OUM_FROUR-9
     * @test.input
     *          <ul>
     *          <li>Valid filter param defining filter for if of an existing
     *          resource of another resource type.</li>
     *          </ul>
     * @test.expected: Xml representation of a list of organizational unit
     *                 representations according to schema
     *                 "organizational-unit-list.xsd". List is empty.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumFrou9CQL() throws Exception {

        final String msg = "Retrieving filtered list that is empty failed.";

        // filter for another resource type
        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "\"" + FILTER_IDENTIFIER + "\"=" + CONTEXT_ID});

        String ouListXml = null;

        try {
            ouListXml = retrieveOrganizationalUnits(filterParams);
        }
        catch (Exception e) {
            failException(msg, e);
        }
        assertOrganizationalUnitSrwList(msg, null, new HashMap<String, String>(),
            ouListXml);
    }

    /**
     * Test retrieving organizational units with filter that must result in
     * empty list.
     * 
     * @test.name Retrieve Organizational Units - Success - Empty
     * @test.id OUM_FROUR-10
     * @test.input
     *          <ul>
     *          <li>Valid filter param defining filter for no existing ou</li>
     *          </ul>
     * @test.expected: Xml representation of a list of organizational unit
     *                 representations according to schema
     *                 "organizational-unit-list.xsd". List is empty.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumFrou10() throws Exception {

        final String msg = "Retrieving filtered list that is empty failed.";

        // create parent
        final String parent1Id =
            createSuccessfully("escidoc_ou_create.xml", 1)[0];

        // create child
        final String child1Xml =
            createSuccessfullyChild("escidoc_ou_create.xml",
                new String[] { parent1Id });
        final String child1Id = getObjidValue(child1Xml);

        // filter for created child ou and for top level constraint
        final String filterXml =
            "<param>"
                + "<filter name=\"http://purl.org/dc/elements/1.1/identifier\">"
                + "<id>" + child1Id + "</id>" + "</filter>"
                + "<filter name=\"top-level-organizational-units\"></filter>"
                + "</param>";

        String ouListXml = null;
        try {
            ouListXml = retrieveOrganizationalUnits(filterXml);
        }
        catch (Exception e) {
            failException(msg, e);
        }
        assertOrganizationalUnitList(msg, null, new HashMap<String, String>(),
            ouListXml);
    }

    /**
     * Test retrieving organizational units with filter that must result in
     * empty list.
     * 
     * @test.name Retrieve Organizational Units - Success - Empty
     * @test.id OUM_FROUR-10
     * @test.input
     *          <ul>
     *          <li>Valid filter param defining filter for no existing ou</li>
     *          </ul>
     * @test.expected: Xml representation of a list of organizational unit
     *                 representations according to schema
     *                 "organizational-unit-list.xsd". List is empty.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumFrou10CQL() throws Exception {

        final String msg = "Retrieving filtered list that is empty failed.";

        // create parent
        final String parent1Id =
            createSuccessfully("escidoc_ou_create.xml", 1)[0];

        // create child
        final String child1Xml =
            createSuccessfullyChild("escidoc_ou_create.xml",
                new String[] { parent1Id });
        final String child1Id = getObjidValue(child1Xml);

        // filter for created child ou and for top level constraint
        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "\"" + FILTER_IDENTIFIER + "\"=" + child1Id + " and "
            + "\"" + FILTER_TOP_LEVEL_OUS_ONLY + "\"=true"});

        String ouListXml = null;

        try {
            ouListXml = retrieveOrganizationalUnits(filterParams);
        }
        catch (Exception e) {
            failException(msg, e);
        }
        assertOrganizationalUnitSrwList(msg, null, new HashMap<String, String>(),
            ouListXml);
    }

    /**
     * Test retrieving organizational units after creating a child (before
     * create the new parent has no other children).
     * 
     * @test.name Retrieve Organizational Units - Success
     * @test.id OUM_FROUR-11
     * @test.input
     *          <ul>
     *          <li>Valid filter param defining filter for parent</li>
     *          </ul>
     * @test.expected: Xml representation of a list of organizational unit
     *                 representations according to schema
     *                 "organizational-unit-list.xsd". The contained ou must
     *                 have the flag has-children with value true.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumFrou11() throws Exception {

        final String msg = "Retrieving filtered list of one created ou failed.";

        // create parent ou
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String parentId = getObjidValue(createdXml);

        // check if property "has-children" is false
        assertXmlEquals("OU error: " + " has-children", createdXml,
            XPATH_ORGANIZATIONAL_UNIT_HAS_CHILDREN, "false");

        // create child ou
        createSuccessfullyChild("escidoc_ou_create.xml",
            new String[] { parentId });

        // filter for created parent ou
        final String filterXml =
            "<param>"
                + "<filter name=\"http://purl.org/dc/elements/1.1/identifier\">"
                + "<id>" + parentId + "</id>" + "</filter>" + "</param>";

        String ouListXml = null;
        try {
            ouListXml = retrieveOrganizationalUnits(filterXml);
        //System.out.println("ous " + ouListXml);
        }
        catch (Exception e) {
            failException(msg, e);
        }

        // check if property "has-children" is now true
        assertXmlEquals("OU error: " + " has-children", ouListXml,
            XPATH_ORGANIZATIONAL_UNIT_LIST
                + XPATH_ORGANIZATIONAL_UNIT_HAS_CHILDREN, "true");
    }

    /**
     * Test retrieving organizational units after creating a child (before
     * create the new parent has no other children).
     * 
     * @test.name Retrieve Organizational Units - Success
     * @test.id OUM_FROUR-11
     * @test.input
     *          <ul>
     *          <li>Valid filter param defining filter for parent</li>
     *          </ul>
     * @test.expected: Xml representation of a list of organizational unit
     *                 representations according to schema
     *                 "organizational-unit-list.xsd". The contained ou must
     *                 have the flag has-children with value true.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumFrou11CQL() throws Exception {

        final String msg = "Retrieving filtered list of one created ou failed.";

        // create parent ou
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String parentId = getObjidValue(createdXml);

        // check if property "has-children" is false
        assertXmlEquals("OU error: " + " has-children", createdXml,
            XPATH_ORGANIZATIONAL_UNIT_HAS_CHILDREN, "false");

        // create child ou
        createSuccessfullyChild("escidoc_ou_create.xml",
            new String[] { parentId });

        // filter for created parent ou
        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "\"" + FILTER_IDENTIFIER + "\"=" + parentId});

        String ouListXml = null;

        try {
            ouListXml = retrieveOrganizationalUnits(filterParams);
        }
        catch (Exception e) {
            failException(msg, e);
        }

        // check if property "has-children" is now true
        assertXmlEquals("OU error: " + " has-children", ouListXml,
            XPATH_SRW_ORGANIZATIONAL_UNIT_LIST_MEMBER
                + XPATH_ORGANIZATIONAL_UNIT_HAS_CHILDREN, "true");
    }

    /**
     * Test retrieving organizational units after updating the parents of a
     * child (before update the new parent has no other children).
     * 
     * @test.name Retrieve Organizational Units - Success
     * @test.id OUM_FROUR-12
     * @test.input
     *          <ul>
     *          <li>Valid filter param defining filter for parent</li>
     *          </ul>
     * @test.expected: Xml representation of a list of organizational unit
     *                 representations according to schema
     *                 "organizational-unit-list.xsd". The contained ou must
     *                 have the flag has-children with value true.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumFrou12() throws Exception {

        final String msg = "Retrieving filtered list of one created ou failed.";

        // create parent ou
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String parentId = getObjidValue(createdXml);

        // check if property "has-children" is false
        assertXmlEquals("OU error: " + " has-children", createdXml,
            XPATH_ORGANIZATIONAL_UNIT_HAS_CHILDREN, "false");

        // create child ou
        final String childXml = createSuccessfully("escidoc_ou_create.xml");

        Document toBeUpdatedDocument = getDocument(childXml);

        // and add new parent to child
        // this is done by replacing the parent ous element
        deleteElement(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        insertParentsElement(toBeUpdatedDocument,
            XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS,
            new String[] { parentId, null }, false);
        update(getObjidValue(childXml), toString(toBeUpdatedDocument, false)
            .replaceAll(SREL_PREFIX_TEMPLATES, SREL_PREFIX_ESCIDOC));

        // filter for created parent ou
        final String filterXml =
            "<param>"
                + "<filter name=\"http://purl.org/dc/elements/1.1/identifier\">"
                + "<id>" + parentId + "</id>" + "</filter>" + "</param>";

        String ouListXml = null;
        try {
            ouListXml = retrieveOrganizationalUnits(filterXml);
        }
        catch (Exception e) {
            failException(msg, e);
        }

        // check if property "has-children" is now true
        assertXmlEquals("OU error: " + " has-children", ouListXml,
            XPATH_ORGANIZATIONAL_UNIT_LIST
                + XPATH_ORGANIZATIONAL_UNIT_HAS_CHILDREN, "true");
    }

    /**
     * Test retrieving organizational units after updating the parents of a
     * child (before update the new parent has no other children).
     * 
     * @test.name Retrieve Organizational Units - Success
     * @test.id OUM_FROUR-12
     * @test.input
     *          <ul>
     *          <li>Valid filter param defining filter for parent</li>
     *          </ul>
     * @test.expected: Xml representation of a list of organizational unit
     *                 representations according to schema
     *                 "organizational-unit-list.xsd". The contained ou must
     *                 have the flag has-children with value true.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testOumFrou12CQL() throws Exception {

        final String msg = "Retrieving filtered list of one created ou failed.";

        // create parent ou
        final String createdXml = createSuccessfully("escidoc_ou_create.xml");
        final String parentId = getObjidValue(createdXml);

        // check if property "has-children" is false
        assertXmlEquals("OU error: " + " has-children", createdXml,
            XPATH_ORGANIZATIONAL_UNIT_HAS_CHILDREN, "false");

        // create child ou
        final String childXml = createSuccessfully("escidoc_ou_create.xml");

        Document toBeUpdatedDocument = getDocument(childXml);

        // and add new parent to child
        // this is done by replacing the parent ous element
        deleteElement(toBeUpdatedDocument, XPATH_ORGANIZATIONAL_UNIT_PARENTS);
        insertParentsElement(toBeUpdatedDocument,
            XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS,
            new String[] { parentId, null }, false);
        update(getObjidValue(childXml), toString(toBeUpdatedDocument, false)
            .replaceAll(SREL_PREFIX_TEMPLATES, SREL_PREFIX_ESCIDOC));

        // filter for created parent ou
        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "\"" + FILTER_IDENTIFIER + "\"=" + parentId});

        String ouListXml = null;

        try {
            ouListXml = retrieveOrganizationalUnits(filterParams);
        }
        catch (Exception e) {
            failException(msg, e);
        }

        // check if property "has-children" is now true
        assertXmlEquals("OU error: " + " has-children", ouListXml,
            XPATH_SRW_ORGANIZATIONAL_UNIT_LIST_MEMBER
                + XPATH_ORGANIZATIONAL_UNIT_HAS_CHILDREN, "true");
    }

    /**
     * Test successfully retrieving an explain response.
     * 
     * @test.name testExplainRetrieveOrganizationalUnits
     * @test.id testExplainRetrieveOrganizationalUnits
     * @test.input
     * @test.expected: valid explain response.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testExplainRetrieveOrganizationalUnits() throws Exception {
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_EXPLAIN, new String[] {""});

        String result = null;

        try {
            result = retrieveOrganizationalUnits(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);        
    }

    /**
     * The method replaces all line breaks in the provided String with a
     * provided character.
     *
     * @param in given string
     * @param replaceWith replacement
     *
     * @return string containing the given replacement instead of line break
     */
    public static String replaceNewlines(
        final String in, final String replaceWith) {
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
