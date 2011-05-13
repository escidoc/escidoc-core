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
package de.escidoc.core.test.oai.setdefinition;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.interfaces.ResourceHandlerClientInterface;
import de.escidoc.core.test.oai.OaiTestBase;
import org.apache.http.HttpResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Map;

/**
 * Base class for tests of the mock implementation of the OM resources.
 *
 * @author Rozita Friedman
 */
public class SetDefinitionTestBase extends OaiTestBase {

    /**
     * Successfully creates an UserGroup.
     *
     * @param templateName The name of the template.
     * @return Returns the UserGroup document.
     * @throws Exception If anything fails
     */
    public Document createSuccessfully(final String templateName) throws Exception {

        final Document toBeCreatedDocument =
            getTemplateAsFixedSetDefinitionDocument(TEMPLATE_SET_DEFINITION_PATH, templateName);
        insertUniqueSetSpecification(toBeCreatedDocument);

        final String toBeCreatedXml = toString(toBeCreatedDocument, false);
        assertXmlValidSetDefinition(toBeCreatedXml);
        String createdSetDefinitionXml = null;
        try {
            createdSetDefinitionXml = create(toBeCreatedXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSetDefinition(createdSetDefinitionXml);
        Document createdSetDefinition = getDocument(createdSetDefinitionXml);
        return createdSetDefinition;
    }

    /**
     * Retrieve a Template as a Document .<br> The used parser is NOT
     * namespace aware!
     *
     * @param path         The Path of the Template.
     * @param templateName The name of the template.
     * @return The String representation of the Template.
     * @throws Exception If anything fails.
     */
    public Document getTemplateAsFixedSetDefinitionDocument(final String path, final String templateName)
        throws Exception {

        // return fixLinkAttributes(EscidocAbstractTest
        // .getTemplateAsDocument(path, templateName),
        // XPATH_USER_GROUP_SELECTORS);
        return EscidocAbstractTest.getTemplateAsDocument(path, templateName);
    }

    /**
     * Inserts a unique label into the provided document by adding the current timestamp to the contained label.
     *
     * @param document The document.
     * @return The inserted login name.
     * @throws Exception If anything fails.
     */
    protected String insertUniqueSetSpecification(final Document document) throws Exception {

        assertXmlExists("No specification found in template data. ", document, "/set-definition/specification");
        final Node specNode = selectSingleNode(document, "/set-definition/specification");
        String specification = specNode.getTextContent().trim();
        specification += System.currentTimeMillis();

        specNode.setTextContent(specification);

        return specification;
    }

    /**
     * @return Returns the itemClient
     */
    @Override
    public ResourceHandlerClientInterface getClient() {

        return getSetDefinitionClient();
    }

    /**
     * Retrieve set definitions.
     *
     * @param filter The filter query.
     * @return a list of set definitions as SRW response.
     * @throws Exception Thrown if an internal error occurred.
     */
    public String retrieveSetDefinitions(final Map<String, String[]> filter) throws Exception {
        Object result = getSetDefinitionClient().retrieveSetDefinitions(filter);

        if (result instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result;
            assertHttpStatusOfMethod("", httpRes);
        }
        return handleXmlResult(result);
    }
}
