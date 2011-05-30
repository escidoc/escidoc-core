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
package de.escidoc.core.test.om.contentRelation;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.Constants;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test the implementation of the content relation resource.
 *
 * @author André Schenk
 */
public class ContentRelationFilterIT extends ContentRelationTestBase {

    private static final String XPATH_SRW_RELATION_LIST_RELATION =
        XPATH_SRW_RESPONSE_RECORD + "/recordData/search-result-record/" + NAME_CONTENT_RELATION;

    /**
     * Test successfully retrieving a filtered content relation list filtering by created-by.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testFilterCreatedBy() throws Exception {
        String xml = createContentRelation();
        String relationId = getObjidValue(xml);
        String createdBy =
            getObjidValue(EscidocAbstractTest.getDocument(xml), "/content-relation/properties/created-by");
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_IDENTIFIER + "\"=" + relationId + " and "
            + "\"" + FILTER_CREATED_BY + "\"=" + createdBy });

        String result = retrieveContentRelations(filterParams);

        assertXmlValidSrwResponse(result);

        NodeList relations = selectNodeList(EscidocAbstractTest.getDocument(result), XPATH_SRW_RELATION_LIST_RELATION);

        assertTrue("Wrong number of content relations matched filter criteria, " + "expected 1, but was "
            + relations.getLength(), relations.getLength() == 1);
        assertEquals("Wrong content relation matched filter criteria.", relationId, getObjidValue(relations.item(0),
            "/"));
    }

    /**
     * Test successfully retrieving a filtered content relation list filtering by created-by with an unknown user.
     * Expected is an empty content relation list.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testFilterCreatedByUnknownCreator() throws Exception {
        String xml = createContentRelation();
        String relationId = getObjidValue(xml);
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_IDENTIFIER + "\"=" + relationId + " and "
            + "\"" + FILTER_CREATED_BY + "\"=escidoc:unknwonUser" });

        String result = retrieveContentRelations(filterParams);

        assertXmlValidSrwResponse(result);

        NodeList relations = selectNodeList(EscidocAbstractTest.getDocument(result), XPATH_SRW_RELATION_LIST_RELATION);

        assertTrue("Wrong number of content relations matched filter criteria, " + "expected 0, but was "
            + relations.getLength(), relations.getLength() == 0);
    }

    /**
     * Test successfully retrieving a filtered content relation list filtering by content relation id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testFilterId() throws Exception {
        String xml = createContentRelation();
        String relationId = getObjidValue(xml);
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_IDENTIFIER + "\"=" + relationId });

        String result = retrieveContentRelations(filterParams);

        assertXmlValidSrwResponse(result);

        Document resultDoc = EscidocAbstractTest.getDocument(result);
        NodeList nl;
        selectSingleNodeAsserted(resultDoc, XPATH_SRW_RELATION_LIST_RELATION + "[@href = '"
            + Constants.CONTENT_RELATION_BASE_URI + "/" + relationId + "']");
        nl = selectNodeList(resultDoc, XPATH_SRW_RELATION_LIST_RELATION);
        assertEquals("Only one content relation should be retrieved.", nl.getLength(), 1);

        // delete the content relation
        delete(relationId);
    }

    /**
     * Test successfully retrieving an explain response.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testExplainRetrieveContentRelations() throws Exception {
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_EXPLAIN, new String[] { "" });

        String result = null;

        try {
            result = retrieveContentRelations(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
    }

    /**
     * Create a content relation.
     *
     * @return content relation XML
     * @throws Exception If anything fails.
     */
    private String createContentRelation() throws Exception {
        return create(getExampleTemplate("content-relation-01.xml"));
    }
}
