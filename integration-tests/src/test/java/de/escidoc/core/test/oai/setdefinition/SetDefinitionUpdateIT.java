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

import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.test.EscidocAbstractTest;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Rozita Friedman
 */
public class SetDefinitionUpdateIT extends SetDefinitionTestBase {

    private String objid;

    private Document createdSetDefinitionDocument;

    /**
     * Set up test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void setUp() throws Exception {

        createdSetDefinitionDocument = createSuccessfully("escidoc_setdefinition_for_create.xml");
        objid = getObjidValue(createdSetDefinitionDocument);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testUpdateSuccessfullyNameAndDescription() throws Exception {
        final String retrievedLastModificationDate = getLastModificationDateValue(createdSetDefinitionDocument);
        String nameXPath = "/set-definition/properties/name";
        String descriptionXPath = "/set-definition/properties/description";
        String newName = "newName";
        String newDescription = "newDescription";
        Node toBeUpdated = substitute(createdSetDefinitionDocument, nameXPath, newName);
        toBeUpdated = substitute(toBeUpdated, descriptionXPath, newDescription);
        String createdSetDefinition = toString(toBeUpdated, false);
        String updatedSetDefinition = update(objid, createdSetDefinition);
        Document updatedSetDefinitionDocument = getDocument(updatedSetDefinition);
        String name = selectSingleNode(updatedSetDefinitionDocument, nameXPath).getTextContent();
        String description = selectSingleNode(updatedSetDefinitionDocument, descriptionXPath).getTextContent();
        final String updatedLastModificationDate = getLastModificationDateValue(updatedSetDefinitionDocument);
        assertEquals("Set definition name after update is wrong", name, newName);
        assertEquals("Set definition description after update is wrong", description, newDescription);
        assertDateBeforeAfter(retrievedLastModificationDate, updatedLastModificationDate);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testIgnoreUpdateOfSpecificationAndQuery() throws Exception {

        String specificationXPath = "/set-definition/specification";
        String queryXPath = "/set-definition/query";
        String newSpecification = "newSpecification";
        String newQuery = "newQuery";
        String oldSpecification = selectSingleNode(createdSetDefinitionDocument, specificationXPath).getTextContent();
        String oldQuery = selectSingleNode(createdSetDefinitionDocument, queryXPath).getTextContent();
        Node toBeUpdated = substitute(createdSetDefinitionDocument, specificationXPath, newSpecification);
        toBeUpdated = substitute(toBeUpdated, queryXPath, newQuery);
        String createdSetDefinition = toString(toBeUpdated, false);
        String updatedSetDefinition = update(objid, createdSetDefinition);
        Document updatedSetDefinitionDocument = getDocument(updatedSetDefinition);
        String specification = selectSingleNode(updatedSetDefinitionDocument, specificationXPath).getTextContent();
        String query = selectSingleNode(updatedSetDefinitionDocument, queryXPath).getTextContent();
        assertEquals("Set definition specification is changed after update", oldSpecification, specification);
        assertEquals("Set definition query is changed after update", oldQuery, query);
        assertEquals("Creation date and last modification date are different. ",
            getLastModificationDateValue(createdSetDefinitionDocument),
            getLastModificationDateValue(updatedSetDefinitionDocument));
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testUpdateWithIdNull() throws Exception {
        try {
            update(null, "");
            fail("No exception on update without id..");
        }
        catch (final Exception e) {
            Class<?> ec = MissingMethodParameterException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }
}
