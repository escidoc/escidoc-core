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
import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.common.client.servlet.aa.PolicyDecisionPointClient;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * PDP test suite.
 * 
 * @author TTE
 * 
 */
public class PdpAbstractTest extends AaTestBase {

    private PolicyDecisionPointClient policyDecisionPointClient;

    /**
     * The constructor.
     * 
     * @param transport
     *            The transport identifier.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public PdpAbstractTest(final int transport) throws Exception {

        super(transport);
        this.policyDecisionPointClient =
            new PolicyDecisionPointClient(transport);
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
     * @param requestsXml
     *            The xml representation of the list of authorization requests.
     * @return Returns the xml representation of the list of authorization
     *         responses.
     * @throws Exception
     *             If anything fails.
     */
    protected String evaluate(final String requestsXml) throws Exception {

        return handleXmlResult(getPolicyDecisionPointClient().evaluate(
            requestsXml));
    }

    /**
     * Test successful evaluation of authorization requests.
     * 
     * @test.name Evaluate Authorization Requests - Success
     * @test.id AA_EAR-1
     * @test.input: Valid XML representation of a list of authorization
     *              requests.
     * @test.expected: Response contining the evaluation results.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAEar1() throws Exception {

        String requestsXML =
            EscidocRestSoapTestBase.getTemplateAsString(TEMPLATE_REQUESTS_PATH, "requests.xml");
        assertXmlValidRequests(requestsXML);

        String evaluationResponsesXml = null;
        try {
            evaluationResponsesXml = evaluate(requestsXML);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertNotNull(evaluationResponsesXml);
        assertXmlValidResults(evaluationResponsesXml);
        Document evaluationDocument = EscidocRestSoapTestBase.getDocument(evaluationResponsesXml);
        assertXmlExists("", evaluationDocument, "/results/result[1]");
        assertXmlExists("", evaluationDocument, "/results/result[2]");
    }

    /**
     * Test declining evaluation of authorization requests with corrupted XML
     * data.
     * 
     * @test.name Evaluate Authorization Requests - corrupted xml
     * @test.id AA_EAR-2
     * @test.input: Corrupted XML representation of a list of authorization
     *              requests.
     * @test.expected: XmlCorruptedException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAEar2() throws Exception {

        try {
            evaluate("<Corrupt XML data");
            EscidocRestSoapTestBase.failMissingException(XmlCorruptedException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(XmlCorruptedException.class, e);
        }
    }

    /**
     * Test declining evaluation of authorization requests without providing XML
     * data.
     * 
     * @test.name Evaluate Authorization Requests - no xml
     * @test.id AA_EAR-3
     * @test.input: No XML representation of a list of authorization requests is
     *              provided.
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAEar3() throws Exception {

        try {
            evaluate(null);
            EscidocRestSoapTestBase.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining evaluation of authorization requests 
     * with providing unknown action.
     * 
     * @test.name Evaluate Authorization Requests - unknown action
     * @test.id AA_EAR-4
     * @test.input: XML representation of a list of authorization requests 
     *              with unknown action is provided.
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAEar4() throws Exception {

        String requestsXML =
            EscidocRestSoapTestBase.getTemplateAsString(TEMPLATE_REQUESTS_PATH, "requests3.xml");
        assertXmlValidRequests(requestsXML);
        String evaluationResponsesXml = null;
        try {
            evaluationResponsesXml = evaluate(requestsXML);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertNotNull(evaluationResponsesXml);
        assertXmlValidResults(evaluationResponsesXml);
        Document evaluationDocument = EscidocRestSoapTestBase.getDocument(evaluationResponsesXml);
        NodeList nodes = selectNodeList(evaluationDocument, "/results/result/@decision");
        assertEquals("result-node not found", 1, nodes.getLength());
        assertEquals("wrong decision", "deny", nodes.item(0).getTextContent());

    }

    /**
     * Test evaluation of authorization requests 
     * with providing load-examples action.
     * 
     * @test.name Evaluate Authorization Requests - unknown action
     * @test.id AA_EAR-4
     * @test.input: XML representation of a list of authorization requests 
     *              with unknown action is provided.
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAEar5() throws Exception {

        String requestsXML =
            EscidocRestSoapTestBase.getTemplateAsString(TEMPLATE_REQUESTS_PATH, "requests4.xml");
        assertXmlValidRequests(requestsXML);
        String evaluationResponsesXml = null;
        try {
            evaluationResponsesXml = evaluate(requestsXML);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertNotNull(evaluationResponsesXml);
        assertXmlValidResults(evaluationResponsesXml);
        Document evaluationDocument = EscidocRestSoapTestBase.getDocument(evaluationResponsesXml);
        NodeList nodes = selectNodeList(evaluationDocument, "/results/result/@decision");
        assertEquals("result-node not found", 1, nodes.getLength());
        assertEquals("wrong decision", "permit", nodes.item(0).getTextContent());

    }

}
