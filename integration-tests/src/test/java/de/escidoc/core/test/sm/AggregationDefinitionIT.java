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
package de.escidoc.core.test.sm;

import de.escidoc.core.test.EscidocAbstractTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test the implementation of the AggregationDefinition resource.
 *
 * @author Michael Hoppe
 */
public class AggregationDefinitionIT extends AggregationDefinitionTestBase {

    private ScopeAbstractIT scope = null;

    private static String scopeId = null;

    private static Collection<String> primKeys = new ArrayList<String>();

    private static int methodCounter = 0;

    public static final String XPATH_SRW_AGG_DEF_LIST_AGG_DEF = XPATH_SRW_RESPONSE_OBJECT + NAME_AGG_DEF;

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void initialize() throws Exception {
        scope = new ScopeAbstractIT() {
        };
        if (methodCounter == 0) {
            primKeys = new ArrayList<String>();
            createScope();
            createAggregationDefinition();
        }
    }

    /**
     * Clean up after servlet test.
     *
     * @throws Exception If anything fails.
     */
    @After
    public void deinitialize() throws Exception {
        methodCounter++;
        if (methodCounter == getTestAnnotationsCount()) {
            methodCounter = 0;
            deleteAggregationDefinition();
            deleteScope();
        }
    }

    /**
     * Creates scope for tests.
     *
     * @throws Exception If anything fails.
     */
    private void createScope() throws Exception {
        String xml = getTemplateAsFixedScopeString(TEMPLATE_SCOPE_PATH, "escidoc_scope1.xml");
        String result = scope.create(xml);
        scopeId = getPrimKey(result);
    }

    /**
     * delete scope to clean database.
     *
     * @throws Exception If anything fails.
     */
    public void deleteScope() throws Exception {
        scope.delete(scopeId.toString());
    }

    /**
     * create new aggregation-definition.
     *
     * @throws Exception If anything fails.
     */
    public void createAggregationDefinition() throws Exception {
        String xml =
            getTemplateAsFixedAggregationDefinitionString(TEMPLATE_AGG_DEF_PATH, "escidoc_aggregation_definition2.xml");
        xml = replaceElementPrimKey(xml, "scope", scopeId.toString());
        try {
            String result = create(xml);
            assertXmlValidAggregationDefinition(result);
            primKeys.add(getPrimKey(result));
        }
        catch (final Exception e) {
            fail("Exception occured " + e.toString());
        }
    }

    /**
     * delete aggrgation-definition.
     *
     * @throws Exception If anything fails.
     */
    public void deleteAggregationDefinition() throws Exception {
        for (String primKey : primKeys) {
            delete(primKey);
        }
    }

    /**
     * retrieve aggregation-definition.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMAD1() throws Exception {
        String result = retrieve(primKeys.iterator().next());
        assertXmlValidAggregationDefinition(result);
    }

    /**
     * retrieve aggregation-definition with invalid id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMAD2() throws Exception {
        try {
            retrieve("99999");
            fail("No exception occured on retrieve with invalid id.");

        }
        catch (final Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            assertEquals("AggregationDefinitionNotFoundException", exceptionType);
        }
    }

    /**
     * create aggregation-definition with invalid xml.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMAD3() throws Exception {
        String xml =
            getTemplateAsFixedAggregationDefinitionString(TEMPLATE_AGG_DEF_PATH,
                "escidoc_aggregation_definition_invalid.xml");
        xml = replaceElementPrimKey(xml, "scope", scopeId.toString());
        try {
            create(xml);
            fail("No exception occured on create with invalid xml.");

        }
        catch (final Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            assertEquals(exceptionType, "XmlSchemaValidationException");
        }
    }

    /**
     * create with wrong namespace-prefix.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMAD4() throws Exception {
        String xml =
            getTemplateAsFixedAggregationDefinitionString(TEMPLATE_AGG_DEF_PATH, "escidoc_aggregation_definition4.xml");
        xml = replaceElementPrimKey(xml, "scope", scopeId.toString());
        try {
            create(xml);
            fail("No exception occured on create with wrong namespace-prefix.");

        }
        catch (final Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            assertEquals(exceptionType, "XmlCorruptedException");
        }
    }

    /**
     * create correct namespace-prefix.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMAD5() throws Exception {
        String xml =
            getTemplateAsFixedAggregationDefinitionString(TEMPLATE_AGG_DEF_PATH, "escidoc_aggregation_definition1.xml");
        xml = replaceElementPrimKey(xml, "scope", scopeId.toString());
        String result = create(xml);
        primKeys.add(getPrimKey(result));
        assertXmlValidAggregationDefinition(result);
    }

    /**
     * create with id-attribute.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMAD6() throws Exception {
        String xml =
            getTemplateAsFixedAggregationDefinitionString(TEMPLATE_AGG_DEF_PATH, "escidoc_aggregation_definition2.xml");
        xml = replaceElementPrimKey(xml, "scope", scopeId.toString());
        String result = create(xml);
        String primKey = getPrimKey(result);
        primKeys.add(primKey);
        assertXmlValidAggregationDefinition(result);
        assertNotEquals("primkey is 99999", "99999", primKey.toString());
    }

    /**
     * create with wrong scope.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMAD7() throws Exception {
        String xml =
            getTemplateAsFixedAggregationDefinitionString(TEMPLATE_AGG_DEF_PATH, "escidoc_aggregation_definition2.xml");
        try {
            create(xml);
            fail("No exception occured on create with wrong scope.");

        }
        catch (final Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            assertEquals("ScopeNotFoundException", exceptionType);
        }
    }

    /**
     * create with reserved expression in fieldname.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMAD8() throws Exception {
        String xml =
            getTemplateAsFixedAggregationDefinitionString(TEMPLATE_AGG_DEF_PATH, "escidoc_aggregation_definition6.xml");
        xml = replaceElementPrimKey(xml, "scope", scopeId.toString());
        try {
            create(xml);
            fail("No exception occured on create with " + "reserved expression in fieldname.");

        }
        catch (final Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            assertEquals("SqlDatabaseSystemException", exceptionType);
        }
    }

    /**
     * create with whitespace in tablename.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMAD8_1() throws Exception {
        String xml =
            getTemplateAsFixedAggregationDefinitionString(TEMPLATE_AGG_DEF_PATH, "escidoc_aggregation_definition7.xml");
        xml = replaceElementPrimKey(xml, "scope", scopeId.toString());
        try {
            create(xml);
            fail("No exception occured on create with " + "whitespace in tablename.");

        }
        catch (final Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            assertEquals("SqlDatabaseSystemException", exceptionType);
        }
    }

    /**
     * create with whitespace in fieldname.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMAD8_2() throws Exception {
        String xml =
            getTemplateAsFixedAggregationDefinitionString(TEMPLATE_AGG_DEF_PATH, "escidoc_aggregation_definition8.xml");
        xml = replaceElementPrimKey(xml, "scope", scopeId.toString());
        try {
            create(xml);
            fail("No exception occured on create with " + "whitespace in fieldname.");

        }
        catch (final Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            assertEquals("SqlDatabaseSystemException", exceptionType);
        }
    }

    /**
     * create with whitespace in indexname.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMAD8_3() throws Exception {
        String xml =
            getTemplateAsFixedAggregationDefinitionString(TEMPLATE_AGG_DEF_PATH, "escidoc_aggregation_definition9.xml");
        xml = replaceElementPrimKey(xml, "scope", scopeId.toString());
        try {
            create(xml);
            fail("No exception occured on create with " + "whitespace in indexname.");

        }
        catch (final Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            assertEquals("SqlDatabaseSystemException", exceptionType);
        }
    }

    /**
     * create with whitespace in index fieldname.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMAD8_4() throws Exception {
        String xml =
            getTemplateAsFixedAggregationDefinitionString(TEMPLATE_AGG_DEF_PATH, "escidoc_aggregation_definition10.xml");
        xml = replaceElementPrimKey(xml, "scope", scopeId.toString());
        try {
            create(xml);
            fail("No exception occured on create with " + "whitespace in index fieldname.");

        }
        catch (final Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            assertEquals("SqlDatabaseSystemException", exceptionType);
        }
    }

    /**
     * create with quote in tablename.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMAD8_5() throws Exception {
        String xml =
            getTemplateAsFixedAggregationDefinitionString(TEMPLATE_AGG_DEF_PATH, "escidoc_aggregation_definition11.xml");
        xml = replaceElementPrimKey(xml, "scope", scopeId.toString());
        try {
            create(xml);
            fail("No exception occured on create with " + "quote in tablename.");

        }
        catch (final Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            assertEquals("SqlDatabaseSystemException", exceptionType);
        }
    }

    /**
     * create with quote in fieldname.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMAD8_6() throws Exception {
        String xml =
            getTemplateAsFixedAggregationDefinitionString(TEMPLATE_AGG_DEF_PATH, "escidoc_aggregation_definition12.xml");
        xml = replaceElementPrimKey(xml, "scope", scopeId.toString());
        try {
            create(xml);
            fail("No exception occured on create with " + "quote in fieldname.");

        }
        catch (final Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            assertEquals("SqlDatabaseSystemException", exceptionType);
        }
    }

    /**
     * create with quote in indexname.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMAD8_7() throws Exception {
        String xml =
            getTemplateAsFixedAggregationDefinitionString(TEMPLATE_AGG_DEF_PATH, "escidoc_aggregation_definition13.xml");
        xml = replaceElementPrimKey(xml, "scope", scopeId.toString());
        try {
            create(xml);
            fail("No exception occured on create with " + "quote in indexname.");

        }
        catch (final Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            assertEquals("SqlDatabaseSystemException", exceptionType);
        }
    }

    /**
     * create with quote in index fieldname.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMAD8_8() throws Exception {
        String xml =
            getTemplateAsFixedAggregationDefinitionString(TEMPLATE_AGG_DEF_PATH, "escidoc_aggregation_definition14.xml");
        xml = replaceElementPrimKey(xml, "scope", scopeId.toString());
        try {
            create(xml);
            fail("No exception occured on create with " + "quote in index fieldname.");

        }
        catch (final Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            assertEquals("SqlDatabaseSystemException", exceptionType);
        }
    }

    /**
     * retrieve list of all aggregation-definitions.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMAD9CQL() throws Exception {
        String result = retrieveAggregationDefinitions(new HashMap<String, String[]>());
        assertXmlValidSrwResponse(result);
    }

    /**
     * Test successful retrieving a list of existing AggregationDefinitions resources. Test if maximumRecords=0 delivers
     * 0 Roles
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void emptyFilterZeroMaximumRecords() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "0" });

        String result = null;

        try {
            result = retrieveAggregationDefinitions(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving of list of AggregationDefinitions failed. ", e);
        }

        assertXmlValidSrwResponse(result);
        Document retrievedDocument = EscidocAbstractTest.getDocument(result);
        NodeList resultNodes = selectNodeList(retrievedDocument, XPATH_SRW_AGG_DEF_LIST_AGG_DEF);
        final int totalRecordsWithZeroMaximum = resultNodes.getLength();

        assertEquals("Unexpected number of records.", totalRecordsWithZeroMaximum, 0);

    }

    /**
     * Test successfully retrieving an explain response.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void explainTest() throws Exception {
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(EscidocAbstractTest.FILTER_PARAMETER_EXPLAIN, new String[] { "" });

        String result = null;

        try {
            result = retrieveAggregationDefinitions(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
    }
}
