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
package de.escidoc.core.test.aa;

import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.aa.PolicyDecisionPointClient;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * PDP test suite.
 *
 * @author Torsten Tetteroo
 */
public class PdpIT extends AaTestBase {

    private PolicyDecisionPointClient policyDecisionPointClient;

    /**
     * The constructor.
     *
     * @throws Exception If anything fails.
     */
    public PdpIT() throws Exception {
        this.policyDecisionPointClient = new PolicyDecisionPointClient();
    }

    /**
     * @return the roleClient
     */
    public PolicyDecisionPointClient getPolicyDecisionPointClient() {

        return policyDecisionPointClient;
    }

    /**
     * Test evaluating the provided authorization requests.
     *
     * @param requestsXml The xml representation of the list of authorization requests.
     * @return Returns the xml representation of the list of authorization responses.
     * @throws Exception If anything fails.
     */
    protected String evaluate(final String requestsXml) throws Exception {

        return handleXmlResult(getPolicyDecisionPointClient().evaluate(requestsXml));
    }

    /**
     * Test successful evaluation of authorization requests.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAEar1() throws Exception {

        String requestsXML = EscidocAbstractTest.getTemplateAsString(TEMPLATE_REQUESTS_PATH, "requests.xml");
        assertXmlValidRequests(requestsXML);

        String evaluationResponsesXml = null;
        try {
            evaluationResponsesXml = evaluate(requestsXML);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNotNull(evaluationResponsesXml);
        assertXmlValidResults(evaluationResponsesXml);
        Document evaluationDocument = EscidocAbstractTest.getDocument(evaluationResponsesXml);
        assertXmlExists("", evaluationDocument, "/results/result[1]");
        assertXmlExists("", evaluationDocument, "/results/result[2]");
    }

    /**
     * Test declining evaluation of authorization requests with corrupted XML data.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAEar2() throws Exception {

        try {
            evaluate("<Corrupt XML data");
            EscidocAbstractTest.failMissingException(XmlCorruptedException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(XmlCorruptedException.class, e);
        }
    }

    /**
     * Test declining evaluation of authorization requests without providing XML data.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAEar3() throws Exception {

        try {
            evaluate(null);
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining evaluation of authorization requests with providing unknown action.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAEar4() throws Exception {

        String requestsXML = EscidocAbstractTest.getTemplateAsString(TEMPLATE_REQUESTS_PATH, "requests3.xml");
        assertXmlValidRequests(requestsXML);
        String evaluationResponsesXml = null;
        try {
            evaluationResponsesXml = evaluate(requestsXML);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNotNull(evaluationResponsesXml);
        assertXmlValidResults(evaluationResponsesXml);
        Document evaluationDocument = EscidocAbstractTest.getDocument(evaluationResponsesXml);
        NodeList nodes = selectNodeList(evaluationDocument, "/results/result/@decision");
        assertEquals("result-node not found", 1, nodes.getLength());
        assertEquals("wrong decision", "deny", nodes.item(0).getTextContent());

    }

    /**
     * Test evaluation of authorization requests with providing load-examples action.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAEar5() throws Exception {

        String requestsXML = EscidocAbstractTest.getTemplateAsString(TEMPLATE_REQUESTS_PATH, "requests4.xml");
        assertXmlValidRequests(requestsXML);
        String evaluationResponsesXml = null;
        try {
            evaluationResponsesXml = evaluate(requestsXML);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNotNull(evaluationResponsesXml);
        assertXmlValidResults(evaluationResponsesXml);
        Document evaluationDocument = EscidocAbstractTest.getDocument(evaluationResponsesXml);
        NodeList nodes = selectNodeList(evaluationDocument, "/results/result/@decision");
        assertEquals("result-node not found", 1, nodes.getLength());
        assertEquals("wrong decision", "permit", nodes.item(0).getTextContent());

    }

}
