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
package de.escidoc.core.test.cmm.contentmodel;

import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;

import static org.junit.Assert.fail;

/**
 * Test the mock implementation of the item resource.
 * 
 * @author MSC
 * 
 */
@RunWith(value = Parameterized.class)
public class RetrieveTest extends ContentModelTestBase {

    private String contentModelId;

    private String contentModelXml;

    /**
     * @param transport
     *            The transport identifier.
     */
    public RetrieveTest(final int transport) {
        super(transport);
    }

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Before
    public void setUp() throws Exception {
        Document contentModel =
            EscidocRestSoapTestBase.getTemplateAsDocument(
                TEMPLATE_CONTENT_MODEL_PATH + "/" + getTransport(false),
                "content-model-all-for-create.xml");
        contentModelXml = toString(contentModel, false);
        contentModelXml = create(contentModelXml);
        contentModelId = getObjidValue(contentModelXml);
    }

    /**
     * Clean up after servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();

        try {
            delete(this.contentModelId);
        }
        catch (Exception e) {
            // do nothing
        }
    }

    /**
     * Test retrieving an existing ContentModel. The excepted result is a XML
     * representation of the created ContentModel, corresponding to XML-schema
     * "ContentModel.xsd"
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testCtmRCt1() throws Exception {

        Document contentModel = getDocument(this.contentModelXml);

        String retrievedXML = retrieve(this.contentModelId);

        validateContentModel(retrievedXML, getContentModelTitle(contentModel),
            getContentModelDescription(contentModel),
            getContentModelMdRecordDefinitions(contentModel),
            getContentModelResourceDefinitions(contentModel),
            getContentModelContentStreamDefinitions(contentModel), false);
    }

    /**
     * Test retrieving a not existing ContentModel.
     * 
     * @test.name: Retrieve Content Type - Unknown Id
     * @test.id: CTM_Rct_2
     * @test.input: Id that is unknown to the system.
     * @test.expected: ContentModelNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testCtmRCt2() throws Exception {

        Class<?> ec = ContentModelNotFoundException.class;
        try {
            retrieve(UNKNOWN_ID);
            EscidocRestSoapTestBase.failMissingException(ec);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(ec, e);
        }
    }

    /**
     * Test retrieving a ContentModel with providing an id of an existing
     * resource of another type.
     * 
     * @test.name: Retrieve Content Type - Wrong Id
     * @test.id: CTM_Rct_2-2
     * @test.input: Id of an existing resource of another resource type.
     * @test.expected: ContentModelNotFoundException
     * @test.status Implemented
     * 
     * @test.issues 
     *              http://www.escidoc-project.de/issueManagement/show_bug.cgi?id
     *              =294
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testCtmRCt2_2() throws Exception {

        Class<?> ec = ContentModelNotFoundException.class;
        try {
            retrieve(CONTEXT_ID);
            EscidocRestSoapTestBase.failMissingException(ec);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(ec, e);
        }
    }

    /**
     * Test retrieving an ContentModel without id.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testCtmRCt3() throws Exception {

        Class<?> ec = MissingMethodParameterException.class;
        try {
            retrieve(null);
            EscidocRestSoapTestBase.failMissingException(ec);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(ec, e);
        }
    }

    /**
     * Test retrieve ContentModel Properties.
     */
    @Test
    public void testRetrieveContentModelProperties() throws Exception {
        Document contentModel = getDocument(this.contentModelXml);
        String subResource = retrieveProperties(this.contentModelId);
        assertXmlValidContentModel(subResource);
        validateContentModelProperties(subResource, this.contentModelId,
            "/properties", getContentModelTitle(contentModel),
            getContentModelDescription(contentModel),
            getLastModificationDateValue(contentModel));
    }

    /**
     * Test retrieve ContentModel Properties.
     */
    @Test
    public void testRetrieveContentModelResources() throws Exception {
        String subResource = null;
        try {
            subResource = retrieveResources(this.contentModelId);
        }
        catch (NoSuchMethodException e) {
            if (getTransport() != Constants.TRANSPORT_SOAP) {
                throw e;
            }
            return;
        }
        if (getTransport() == Constants.TRANSPORT_SOAP) {
            fail("No Exception trying to call 'retrieveResources' via SOAP.");
        }
        selectSingleNodeAsserted(getDocument(subResource),
            "/resources/version-history");
        assertXmlValidContentModel(subResource);
    }

    /**
     * Test retrieve ContentModel Properties.
     */
    @Test
    public void testRetrieveContentModelContentStreams() throws Exception {
        String subResource = retrieveContentStreams(this.contentModelId);
        selectSingleNodeAsserted(getDocument(subResource), "/content-streams");
        assertXmlValidContentModel(subResource);
    }

    /**
     * Test retrieve ContentModel Properties.
     */
    @Test
    public void testRetrieveContentModelContentStream() throws Exception {
        Document contentModel = getDocument(this.contentModelXml);
        String name =
            selectSingleNodeAsserted(contentModel,
                "/content-model/content-streams/content-stream[1]/@name")
                .getNodeValue();

        String subResource = retrieveContentStream(this.contentModelId, name);
        selectSingleNodeAsserted(getDocument(subResource), "/content-stream");
        assertXmlValidContentModel(subResource);
    }
}
