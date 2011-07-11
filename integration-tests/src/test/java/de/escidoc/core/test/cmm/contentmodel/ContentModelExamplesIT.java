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

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Test the mock implementation of the item resource.
 *
 * @author Michael Schneider
 */
public class ContentModelExamplesIT extends ContentModelTestBase {

    /**
     * Test creating a ContentModel with minimal content.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCmCreateMinimal() throws Exception {
        String cmXml = getExampleTemplate("content-model-minimal-for-create.xml");
        String createdXML = create(cmXml);
        String retrievedXML = retrieve(getObjidValue(createdXML));

        // validate

        // get values from template
        Document contentModel = getDocument(cmXml);
        String title = selectSingleNode(contentModel, "/content-model/properties/name/text()").getNodeValue();
        String description =
            selectSingleNode(contentModel, "/content-model/properties/description/text()").getNodeValue();

        NodeList mdRecordDefinitionNames =
            selectNodeList(contentModel, "/content-model/md-record-definitions/md-record-definition/@name");
        Map<String, String> mdRecordDefinitions = new HashMap<String, String>();
        int c = mdRecordDefinitionNames.getLength();
        for (int i = 0; i < c; i++) {
            String name = mdRecordDefinitionNames.item(i).getNodeValue();
            String xsd =
                selectSingleNode(contentModel,
                    "/content-model/md-record-definitions/md-record-definition[@name = '" + name + "']/schema/@href")
                    .getNodeValue();
            mdRecordDefinitions.put(name, xsd);
        }

        NodeList resourceDefinitionNames =
            selectNodeList(contentModel, "/content-model/resource-definitions/resource-definition/@name");
        List<String> resourceDefinitions = new Vector<String>();
        c = resourceDefinitionNames.getLength();
        for (int i = 0; i < c; i++) {
            resourceDefinitions.add(resourceDefinitionNames.item(i).getNodeValue());
        }

        NodeList contentStreamNames =
            selectNodeList(contentModel, "/content-model/content-streams/content-stream/@name");
        List<List<String>> contentStreamDefinitions = new Vector<List<String>>();
        c = contentStreamNames.getLength();
        for (int i = 0; i < c; i++) {
            String name = contentStreamNames.item(i).getNodeValue();
            List<String> contentStreamDefinition = new Vector<String>();
            contentStreamDefinition.add(0, name);
            contentStreamDefinition.add(1, selectSingleNode(contentModel,
                "/content-model/content-streams/content-stream[@name = '" + name + "']/@mime-type").getNodeValue());
            contentStreamDefinition.add(2, selectSingleNode(contentModel,
                "/content-model/content-streams/content-stream[@name = '" + name + "']/@storage").getNodeValue());
            contentStreamDefinitions.add(contentStreamDefinition);
        }

        // validate created
        validateContentModel(createdXML, title, description, mdRecordDefinitions, resourceDefinitions,
            contentStreamDefinitions, false);
        // validate retrieved
        validateContentModel(retrievedXML, title, description, mdRecordDefinitions, resourceDefinitions,
            contentStreamDefinitions, false);

    }
}
