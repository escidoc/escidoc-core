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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.test.cmm.contentmodel;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.test.EscidocRestSoapTestsBase;

/**
 * Test the mock implementation of the item resource.
 * 
 * @author MSC
 * 
 */
public class ContentModelCreateTest extends ContentModelTestBase {

    /**
     * @param transport
     *            The transport identifier.
     */
    public ContentModelCreateTest(final int transport) {
        super(transport);
    }

    /**
     * Test creating a ContentModel with minimal content.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testCmCreateMinimal() throws Exception {
        Document contentModel =
            EscidocRestSoapTestsBase.getTemplateAsDocument(
                TEMPLATE_CONTENT_MODEL_PATH + "/" + getTransport(false),
                "content-model-minimal-for-create.xml");

        String cmXml = toString(contentModel, false);
        String createdXML = create(cmXml);

        validateContentModel(createdXML, getContentModelTitle(contentModel),
            getContentModelDescription(contentModel),
            getContentModelMdRecordDefinitions(contentModel),
            getContentModelResourceDefinitions(contentModel),
            getContentModelContentStreamDefinitions(contentModel), false);

        String retrievedXML = retrieve(getObjidValue(createdXML));

        validateContentModel(retrievedXML, getContentModelTitle(contentModel),
            getContentModelDescription(contentModel),
            getContentModelMdRecordDefinitions(contentModel),
            getContentModelResourceDefinitions(contentModel),
            getContentModelContentStreamDefinitions(contentModel), false);
    }

    /**
     * Test creating a ContentModel with full content.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testCmCreateAll() throws Exception {
        Document contentModel =
            EscidocRestSoapTestsBase.getTemplateAsDocument(
                TEMPLATE_CONTENT_MODEL_PATH + "/" + getTransport(false),
                "content-model-all-for-create.xml");

        String title = getContentModelTitle(contentModel);
        String description = getContentModelDescription(contentModel);

        Map<String, String> mdRecordDefinitions =
            getContentModelMdRecordDefinitions(contentModel);

        List<String> resourceDefinitions =
            getContentModelResourceDefinitions(contentModel);

        List<List<String>> contentStreamDefinitions =
            getContentModelContentStreamDefinitions(contentModel);

        String contentModelXml = toString(contentModel, false);
        String createdXML = create(contentModelXml);

        // validate
        validateContentModel(createdXML, title, description,
            mdRecordDefinitions, resourceDefinitions, contentStreamDefinitions,
            true);

    }

    /**
     * Test creating a ContentModel from a previously retrieved representation.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testCmmCCm1() throws Exception {

        Document contentModel =
            EscidocRestSoapTestsBase.getTemplateAsDocument(
                TEMPLATE_CONTENT_MODEL_PATH + "/" + getTransport(false),
                "content-model-asRetrieved.xml");
        String cmXml = toString(contentModel, false);
        String createdXML = create(cmXml);

        retrieve(getObjidValue(createdXML));

        validateContentModel(createdXML, getContentModelTitle(contentModel),
            getContentModelDescription(contentModel),
            getContentModelMdRecordDefinitions(contentModel),
            getContentModelResourceDefinitions(contentModel),
            getContentModelContentStreamDefinitions(contentModel), false);
    }

    /**
     * Test creating a ContentModel from a previously created ContentModels
     * representation.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testCreateFromRetrieve() throws Exception {

        Document contentModel =
            EscidocRestSoapTestsBase.getTemplateAsDocument(
                TEMPLATE_CONTENT_MODEL_PATH + "/" + getTransport(false),
                "content-model-all-for-create.xml");
        String title = getContentModelTitle(contentModel);
        String description = getContentModelDescription(contentModel);
        Map<String, String> mdRecordDefinitions =
            getContentModelMdRecordDefinitions(contentModel);
        List<String> resourceDefinitions =
            getContentModelResourceDefinitions(contentModel);
        List<List<String>> contentStreamDefinitions =
            getContentModelContentStreamDefinitions(contentModel);

        String cmXml = toString(contentModel, false);
        String createdXML = create(cmXml);

        validateContentModel(createdXML, title, description,
            mdRecordDefinitions, resourceDefinitions, contentStreamDefinitions,
            true);

        createdXML = create(retrieve(getObjidValue(createdXML)));
        validateContentModel(createdXML, title, description,
            mdRecordDefinitions, resourceDefinitions, contentStreamDefinitions,
            true);

    }

    /**
     * Test creating an ContentModel without xml.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testCmmCCm3() throws Exception {

        Class< ? > ec = MissingMethodParameterException.class;
        try {
            create(null);
            EscidocRestSoapTestsBase.failMissingException(ec);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(ec, e);
        }
    }

    /**
     * Test unexpected parser exception instead of InvalidXmlException during
     * create (see issue INFR-911).
     * 
     * @throws Exception
     *             Thrown if behavior is not as expected.
     */
    public void testInvalidXml() throws Exception {

        /*
         * The infrastructure has thrown an unexpected parser exception during
         * creation if a non XML datastructur is send (e.g. String).
         */
        try {
            create("laber-rababer");
            fail("Missing Invalid XML exception");
        }
        catch (InvalidXmlException e) {
            // that's ok
        }
    }

}
