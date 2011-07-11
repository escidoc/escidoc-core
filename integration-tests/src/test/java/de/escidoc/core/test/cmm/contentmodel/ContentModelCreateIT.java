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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.test.EscidocAbstractTest;
import org.junit.Test;
import org.w3c.dom.Document;

import java.util.List;
import java.util.Map;

/**
 * Test the mock implementation of the item resource.
 *
 * @author Michael Schneider
 */
public class ContentModelCreateIT extends ContentModelTestBase {

    /**
     * Test creating a ContentModel with minimal content.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCmCreateMinimal() throws Exception {
        String cmXml = getExampleTemplate("content-model-minimal-for-create.xml");
        String createdXML = create(cmXml);
        Document contentModel = getDocument(cmXml);

        validateContentModel(createdXML, getContentModelTitle(contentModel), getContentModelDescription(contentModel),
            getContentModelMdRecordDefinitions(contentModel), getContentModelResourceDefinitions(contentModel),
            getContentModelContentStreamDefinitions(contentModel), false);

        String retrievedXML = retrieve(getObjidValue(createdXML));

        validateContentModel(retrievedXML, getContentModelTitle(contentModel),
            getContentModelDescription(contentModel), getContentModelMdRecordDefinitions(contentModel),
            getContentModelResourceDefinitions(contentModel), getContentModelContentStreamDefinitions(contentModel),
            false);
    }

    /**
     * Test creating a ContentModel with full content.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCmCreateAll() throws Exception {
        Document contentModel =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTENT_MODEL_PATH + "/rest",
                "content-model-all-for-create.xml");

        String title = getContentModelTitle(contentModel);
        String description = getContentModelDescription(contentModel);

        Map<String, String> mdRecordDefinitions = getContentModelMdRecordDefinitions(contentModel);

        List<String> resourceDefinitions = getContentModelResourceDefinitions(contentModel);

        List<List<String>> contentStreamDefinitions = getContentModelContentStreamDefinitions(contentModel);

        String contentModelXml = toString(contentModel, false);
        String createdXML = create(contentModelXml);

        // validate
        validateContentModel(createdXML, title, description, mdRecordDefinitions, resourceDefinitions,
            contentStreamDefinitions, true);

    }

    /**
     * Test creating a ContentModel from a previously retrieved representation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCmmCCm1() throws Exception {

        Document contentModel =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTENT_MODEL_PATH + "/rest",
                "content-model-asRetrieved.xml");
        String cmXml = toString(contentModel, false);
        String createdXML = create(cmXml);

        retrieve(getObjidValue(createdXML));

        validateContentModel(createdXML, getContentModelTitle(contentModel), getContentModelDescription(contentModel),
            getContentModelMdRecordDefinitions(contentModel), getContentModelResourceDefinitions(contentModel),
            getContentModelContentStreamDefinitions(contentModel), false);
    }

    /**
     * Test creating a ContentModel from a previously created ContentModels representation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateFromRetrieve() throws Exception {

        Document contentModel =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTENT_MODEL_PATH + "/rest",
                "content-model-all-for-create.xml");
        String title = getContentModelTitle(contentModel);
        String description = getContentModelDescription(contentModel);
        Map<String, String> mdRecordDefinitions = getContentModelMdRecordDefinitions(contentModel);
        List<String> resourceDefinitions = getContentModelResourceDefinitions(contentModel);
        List<List<String>> contentStreamDefinitions = getContentModelContentStreamDefinitions(contentModel);

        String cmXml = toString(contentModel, false);
        String createdXML = create(cmXml);

        validateContentModel(createdXML, title, description, mdRecordDefinitions, resourceDefinitions,
            contentStreamDefinitions, true);

        createdXML = create(retrieve(getObjidValue(createdXML)));
        validateContentModel(createdXML, title, description, mdRecordDefinitions, resourceDefinitions,
            contentStreamDefinitions, true);

    }

    /**
     * Test creating an ContentModel without xml.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCmmCCm3() throws Exception {

        Class<?> ec = MissingMethodParameterException.class;
        try {
            create(null);
            EscidocAbstractTest.failMissingException(ec);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ec, e);
        }
    }

    /**
     * Test unexpected parser exception instead of InvalidXmlException during create (see issue INFR-911).
     *
     * @throws Exception Thrown if behavior is not as expected.
     */
    @Test(expected = InvalidXmlException.class)
    public void testInvalidXml() throws Exception {

        /*
         * The infrastructure has thrown an unexpected parser exception during
         * creation if a non XML data structure is send (e.g. String).
         */
        create("laber-rababer");
    }

    /**
     * Test links in Version History.
     * <p/>
     * See Issue INFR-942.
     *
     * @throws Exception If anything fails.
     */
    public void contentModelVersionHistory() throws Exception {
        String cmXml = getExampleTemplate("content-model-minimal-for-create.xml");
        String createdXML = create(cmXml);
        String cmId = getObjidValue(createdXML);

        String versionHistory = retrieveVersionHistory(cmId);
        assertFalse("Wrong references", versionHistory.contains("item"));
    }

    /**
     * Test if md-record-name of resource-definitions of ContentModel is handled
     * well.
     * 
     * See issue INFR-1122
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testCmMetadataRecordName() throws Exception {

        String xPath = "/content-model/resource-definitions/resource-definition[@name='trans']/md-record-name";
        Document contentModel =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTENT_MODEL_PATH + "/rest",
                "content-model-all-for-create.xml");

        String mdRecordName = "blafasel" + System.nanoTime();
        substitute(contentModel, xPath, mdRecordName);

        String contentModelXml = toString(contentModel, false);
        String createdXML = create(contentModelXml);

        Document cmCreated = EscidocAbstractTest.getDocument(createdXML);

        assertEquals("Wrong md-record-name", mdRecordName, selectSingleNode(cmCreated, xPath + "/text()")
            .getNodeValue());
    }

}
