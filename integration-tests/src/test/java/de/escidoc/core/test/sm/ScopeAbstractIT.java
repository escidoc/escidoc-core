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
 * Test the implementation of the Scope resource.
 *
 * @author Michael Hoppe
 */
public abstract class ScopeAbstractIT extends ScopeTestBase {

    private static Collection<String> primKeys = new ArrayList<String>();

    private static int methodCounter = 0;

    public static final String XPATH_SRW_SCOPE_LIST_SCOPE = XPATH_SRW_RESPONSE_OBJECT + NAME_SCOPE;

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void initialize() throws Exception {
        if (methodCounter == 0) {
            primKeys = new ArrayList<String>();
            createScope();
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
            deleteScope();
        }
    }

    /**
     * create new scope.
     *
     * @throws Exception If anything fails.
     */
    public void createScope() throws Exception {
        String xml = getTemplateAsFixedScopeString(TEMPLATE_SCOPE_PATH, "escidoc_scope1.xml");
        String result = create(xml);
        assertXmlValidScope(result);
        primKeys.add(getPrimKey(result));
    }

    /**
     * delete scope to clean database.
     *
     * @throws Exception If anything fails.
     */
    public void deleteScope() throws Exception {
        for (String primKey : primKeys) {
            delete(primKey);
        }
    }

    /**
     * retrieve scope with id of created scope.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMSC1() throws Exception {
        String result = retrieve(primKeys.iterator().next());
        assertXmlValidScope(result);
    }

    /**
     * retrieve scope with invalid id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMSC2() throws Exception {
        try {
            retrieve("99999");
            fail("No exception occured on retrieve with invalid id.");

        }
        catch (final Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            assertEquals(exceptionType, "ScopeNotFoundException");
        }
    }

    /**
     * update scope.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMSC3() throws Exception {
        String xml = getTemplateAsFixedScopeString(TEMPLATE_SCOPE_PATH, "escidoc_scope2.xml");
        String primKey = primKeys.iterator().next();
        xml = replacePrimKey(xml, primKey);
        String result = update(primKey, xml);
        assertXmlValidScope(result);
    }

    /**
     * create with invalid xml.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMSC4() throws Exception {
        String xml = getTemplateAsFixedScopeString(TEMPLATE_SCOPE_PATH, "escidoc_scope_invalid.xml");
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
     * create with invalid scope-type.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMSC5() throws Exception {
        String xml = getTemplateAsFixedScopeString(TEMPLATE_SCOPE_PATH, "escidoc_scope_invalid1.xml");
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
     * update with wrong primkey.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMSC6() throws Exception {
        String xml = getTemplateAsFixedScopeString(TEMPLATE_SCOPE_PATH, "escidoc_scope1.xml");
        try {
            update("99999", xml);
            fail("No exception occured on update with wrong primkey.");

        }
        catch (final Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            assertEquals(exceptionType, "ScopeNotFoundException");
        }
    }

    /**
     * create with wrong namespace-prefix.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMSC7() throws Exception {
        String xml = getTemplateAsFixedScopeString(TEMPLATE_SCOPE_PATH, "escidoc_scope4.xml");
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
    public void testSMSC8() throws Exception {
        String xml = getTemplateAsFixedScopeString(TEMPLATE_SCOPE_PATH, "escidoc_scope5.xml");
        String result = create(xml);
        primKeys.add(getPrimKey(result));
        assertXmlValidScope(result);
    }

    /**
     * create with id-attribute.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMSC9() throws Exception {
        String xml = getTemplateAsFixedScopeString(TEMPLATE_SCOPE_PATH, "escidoc_scope6.xml");
        String result = create(xml);
        String primKey = getPrimKey(result);
        primKeys.add(primKey);
        assertXmlValidScope(result);
        assertNotEquals("primkey is 99999", "99999", primKey.toString());
    }

    /**
     * retrieve list of scopes.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMSC10CQL() throws Exception {
        String result = retrieveScopes(new HashMap<String, String[]>());
        assertXmlValidSrwResponse(result);
    }

    /**
     * Test successful retrieving a list of existing Scopes resources. Test if maximumRecords=0 delivers 0 Scopes
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void emptyFilterZeroMaximumRecords() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "0" });

        String result = null;

        try {
            result = retrieveScopes(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving of list of Scopes failed. ", e);
        }

        assertXmlValidSrwResponse(result);
        Document retrievedDocument = EscidocAbstractTest.getDocument(result);
        NodeList resultNodes = selectNodeList(retrievedDocument, XPATH_SRW_SCOPE_LIST_SCOPE);
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
            result = retrieveScopes(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
    }
}
