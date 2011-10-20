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
package de.escidoc.core.test.om.semanticstore.spo;

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidTripleStoreOutputFormatException;
import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidTripleStoreQueryException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.om.OmTestBase;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests the Semantic Store.<br> The tests are executed by the depositor.
 *
 * @author Michael Schneider
 */
public class SpoIT extends SpoTestBase {

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void setUp() throws Exception {
        PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
    }

    /**
     * Clean up after servlet test.
     *
     * @throws Exception If anything fails.
     */
    @After
    public void tearDown() throws Exception {

        PWCallback.resetHandle();
        super.tearDown();
    }

    /**
     * Tests successfully request of the triple store. Query for all entries of a subject. Result is filtered to
     * relation entries only.
     */
    @Test
    public void testSpoRequestNTriples() throws Exception {
        String sourceId = createItemHelper();
        String targetId = createItemHelper();
        String p = "http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isRevisionOf";
        String format = "N-Triples";
        // String format = "RDF/XML";
        addRelation(sourceId, p, targetId);
        String param = getTaskParametrSpo("&lt;info:fedora/" + sourceId + "&gt;  * *", format);
        String result = spo(param);

        assertTrue(result.contains(sourceId));
        // TODO check result for registered predicates
        assertTrue(result.contains(p));
        // check for one single triple (result is filtered)
        String[] parts = result.split(sourceId);
        assertEquals(2, parts.length);

    }

    /**
     * Tests successfully request of the triple store. Query for all entrie of a subject. Result is filtered to relation
     * entries only.
     */
    @Test
    public void testSpoRequestRdfXml() throws Exception {
        String sourceId = createItemHelper();
        String targetId = createItemHelper();
        String p = "http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isRevisionOf";
        String format = "RDF/XML";
        addRelation(sourceId, p, targetId);
        String param = getTaskParametrSpo("&lt;info:fedora/" + sourceId + "&gt;  &lt;" + p + "&gt; *", format);
        String result = spo(param);

        Node resultDoc = EscidocAbstractTest.getDocument(result);
        selectSingleNodeAsserted(resultDoc, "/RDF/Description[@about = 'info:fedora/" + sourceId + "']");
        selectSingleNodeAsserted(resultDoc, "/RDF/Description/isRevisionOf");
        NodeList nl = selectNodeList(resultDoc, "/RDF/Description/*");
        assertEquals("Number of triples", 1, nl.getLength());
    }

    /**
     * Tests unsuccessfully request of the triple store with invalid predicate.
     */
    @Test
    public void testSpoRequestForbiddenPredicate1() throws Exception {
        String sourceId = createItemHelper();
        String targetId = createItemHelper();
        String p = "http://www.escidoc.de/ontologies/mpdl-ontologies/" + "content-relations#isRevisionOf";
        addRelation(sourceId, p, targetId);
        p = "http://www.escidoc.de/ontologies/mpdl-ontologies/" + "content-relations/isRevisionOf";
        String o = "*";
        String format = "N-Triples";
        String param = getTaskParametrSpo("&lt;info:fedora/" + sourceId + "&gt;  &lt;" + p + "&gt;  " + o, format);
        try {
            String result = spo(param);
            fail("No exception on query with forbidden predicate.");
        }
        catch (final Exception e) {
            Class ec = InvalidTripleStoreQueryException.class;
            EscidocAbstractTest.assertExceptionType(ec, e);
        }
    }

    /**
     * Tests unsuccessfully request of the triple store with invalid predicate.
     */
    @Test
    public void testSpoRequestForbiddenPredicate2() throws Exception {
        String sourceId = createItemHelper();
        String targetId = createItemHelper();
        String p = "http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isRevisionOf";
        addRelation(sourceId, p, targetId);
        p = "http://www.escidoc.de/ontologies/mpdl-ontologies/content-relation#isRevisionOf";
        String o = "*";
        String format = "N-Triples";
        String param = getTaskParametrSpo("&lt;info:fedora/" + sourceId + "&gt;  &lt;" + p + "&gt;  " + o, format);
        try {
            String result = spo(param);
            fail("No exception on query with forbidden predicate.");
        }
        catch (final Exception e) {
            Class ec = InvalidTripleStoreQueryException.class;
            EscidocAbstractTest.assertExceptionType(ec, e);
        }
    }

    /**
     * Tests successfully request of the triple store. Query for a specific (single) relation entry.
     */
    @Test
    public void testSpoRequestAllowedPredicate() throws Exception {
        String sourceId = createItemHelper();
        String targetId = createItemHelper();
        // String p =
        // "http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isRevisionOf";
        String p = "http://www.escidoc.org/ontologies/test/content-relations#isTest\u00dc\u00c4\u00d6";
        addRelation(sourceId, p, targetId);
        String o = "*";
        String format = "N-Triples";
        String param = getTaskParametrSpo("&lt;info:fedora/" + sourceId + "&gt; &lt;" + p + "&gt; " + o, format);
        String result = spo(param);
        assertTrue(result.contains(sourceId));
        // check for one single triple (result is filtered)
        String[] parts = result.split(sourceId);
        assertEquals(2, parts.length);
    }

    /**
     * Tests declining wrong request of the triple store containing a wrong query.
     */
    @Test
    public void testSpoRequestWithWrongQuery() throws Exception {
        String id = createItemHelper();
        String param = getTaskParametrSpo("&lt;info:fedora/" + id + "&gt;  * * ooo", "N-Triples");
        try {
            spo(param);
            fail("No exception occured on a triple store request with a wrong query");
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("InvalidTripleStoreQueryException excpected.",
                InvalidTripleStoreQueryException.class, e);
        }
    }

    /**
     * Tests declining wrong request of the triple store containing a wrong output format.
     */
    @Test
    public void testSpoRequestWithWrongOutputFormat() throws Exception {
        String id = createItemHelper();
        String param = getTaskParametrSpo("&lt;info:fedora/" + id + "&gt;  * *", "bla");
        try {
            spo(param);
            fail("No exception occured on a triple store " + "request with a wrong output format");
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("InvalidTripleStoreOutputFormatException excpected.",
                InvalidTripleStoreOutputFormatException.class, e);
        }

    }

    /**
     * Tests issue 375.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOM_Spo_1() throws Exception {

        String sourceId = createItemHelper();
        String targetId = createItemHelper();

        // submit source
        submitItemHelper(sourceId);

        // release source, super user needed
        PWCallback.resetHandle();
        releaseItemHelper(sourceId);

        // add relation
        String p = "http://www.escidoc.de/ontologies/mpdl-ontologies/" + "content-relations#isRevisionOf";
        addRelation(sourceId, p, targetId);

        // update source item
        updateItemHelper(sourceId);

        // query relation
        String s = "*";
        String format = "RDF/XML";
        String param = getTaskParametrSpo(s + " &lt;" + p + "&gt; &lt;info:fedora/" + targetId + "&gt;", format);
        String result = null;
        try {
            result = spo(param);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("SPO query failed.", e);
        }

        assertTrue(result.contains(sourceId));
        // check for one single triple (result is filtered)
        String[] parts = result.split(sourceId);
        assertEquals(2, parts.length);
    }

    private String createItemHelper() throws Exception {
        String theItemId = null;
        // create an item and save the id
        Document xmlData =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        Node xmlItemWithoutComponents = deleteElement(xmlData, "/item/components");
        String itemWithoutComponents = toString(xmlItemWithoutComponents, true);
        OmTestBase testBase = new OmTestBase();
        String theItemXml = handleXmlResult(testBase.getItemClient().create(itemWithoutComponents));
        theItemId = getIdFromRootElementHref(EscidocAbstractTest.getDocument(theItemXml));
        return theItemId;
    }

    private void updateItemHelper(final String id) throws Exception {

        OmTestBase testBase = new OmTestBase();
        String toBeUpdatedXml = handleXmlResult(testBase.getItemClient().retrieve(id));
        toBeUpdatedXml = toBeUpdatedXml.replaceFirst("Semiconductors", "Semiconductors - Updated");
        testBase.getItemClient().update(id, toBeUpdatedXml);
    }

    private void submitItemHelper(final String id) throws Exception {

        OmTestBase testBase = new OmTestBase();
        String toBeSubmittedXml = handleXmlResult(testBase.getItemClient().retrieve(id));
        final String taskParam =
            getTaskParam(getLastModificationDateValue(EscidocAbstractTest.getDocument(toBeSubmittedXml)));
        testBase.getItemClient().submit(id, taskParam);
    }

    private void releaseItemHelper(final String id) throws Exception {

        OmTestBase testBase = new OmTestBase();
        testBase.getItemClient().releaseWithPid(id, null);
    }

    private void addRelation(final String sourceId, final String predicate, final String targetId) throws Exception {
        OmTestBase testBase = new OmTestBase();
        String taskParam =
            "<param last-modification-date=\""
                + getLastModificationDateValue(EscidocAbstractTest.getDocument(handleXmlResult(testBase
                    .getItemClient().retrieve(sourceId)))) + "\">\n";
        taskParam += "<relation><targetId>" + targetId + "</targetId>\n";
        taskParam += "<predicate>";
        if (predicate != null) {
            taskParam += predicate;
        }
        else {
            taskParam += "http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isPartOf";
        }
        taskParam += "</predicate></relation>\n";
        taskParam += "</param>";

        testBase.getItemClient().addContentRelations(sourceId, taskParam);
    }

}
