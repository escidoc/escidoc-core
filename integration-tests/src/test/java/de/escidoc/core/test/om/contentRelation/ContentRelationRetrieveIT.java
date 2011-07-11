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

import de.escidoc.core.common.exceptions.remote.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.Constants;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Test content relation retrieve implementation.
 *
 * @author Steffen Wagner
 */
public class ContentRelationRetrieveIT extends ContentRelationTestBase {

    /**
     * Test retrieving content relation with non-existing objid.
     *
     * @throws Exception Thrown if deleting failed.
     */
    @Test(expected = ContentRelationNotFoundException.class)
    public void testWrongObjid() throws Exception {

        retrieve("test");
    }

    /**
     * Test retrieving content relation properties.
     *
     * @throws Exception Thrown if deleting failed.
     */
    @Test
    public void testRetrieveProperties01() throws Exception {

        String contentRelationXml = getExampleTemplate("content-relation-01.xml");
        String xml = create(contentRelationXml);
        String relationId = getObjidValue(xml);
        String lmd = getLastModificationDateValue(getDocument(xml));

        String propertiesXML = retrieveProperties(relationId);
        assertContentRelationProperties(propertiesXML, lmd);
    }

    /**
     * Test retrieving content relation (virtual-)resources.
     *
     * @throws Exception Thrown if deleting failed.
     */
    @Test
    public void retrieveResources() throws Exception {
        String contentRelationXml = getExampleTemplate("content-relation-01.xml");
        String xml = create(contentRelationXml);
        String relationId = getObjidValue(xml);

        String resourcesXML = retrieveResources(relationId);
        assertXmlValidContentRelation(resourcesXML);
    }

    /**
     * Assert the xmlItemProperties match the expected xmlTemplateItemProperties.
     *
     * @param propertiesXml           The retrieved properties.
     * @param timestampBeforeCreation A timestamp before the creation of the item.
     * @throws Exception If anything fails.
     */
    private void assertContentRelationProperties(final String propertiesXml, final String timestampBeforeCreation)
        throws Exception {

        Document createdProperties = EscidocAbstractTest.getDocument(propertiesXml);
        String href = getRootElementHrefValue(createdProperties);
        if ("".equals(href)) {
            href = null;
        }
        assertNotNull("Properties error: href attribute was not set!", href);
        String rootLastModificationDate = getLastModificationDateValue(createdProperties);
        if ("".equals(rootLastModificationDate)) {
            rootLastModificationDate = null;
        }
        assertNotNull("Properties error: last-modification-date attribute " + "was not set!", rootLastModificationDate);
        assertXmlExists("Properties error: creation-date was not set!", createdProperties, "/properties/creation-date");
        assertXmlExists("Properties error: public-status was not set!", createdProperties, "/properties/public-status");
        assertXmlExists("Properties error: lock-status was not set in properties!", createdProperties,
            "/properties/lock-status");

        assertXmlExists("Properties error: created-by was not set in properties!", createdProperties,
            "/properties/created-by");
        String creationDate = selectSingleNode(createdProperties, "/properties/creation-date").getTextContent();

        assertTimestampEquals("Properties error: creation-date is not as expected!", creationDate,
            timestampBeforeCreation);

        assertReferencingElement("Invalid created-by. ", createdProperties, "/properties/created-by",
            Constants.USER_ACCOUNT_BASE_URI);

        Node nodeLockOwner = selectSingleNode(createdProperties, "/properties/lock-owner");
        assertNull("Properties error: lock-owner must be null!", nodeLockOwner);

        Node nodeWithdrawalComment = selectSingleNode(createdProperties, "/properties/withdrawal-comment");
        assertNull("Properties error: withdrawal-comment must be null!", nodeWithdrawalComment);

        String status = selectSingleNode(createdProperties, "/properties/public-status").getTextContent();
        assertEquals("Properties error: invalid public-status!", STATE_PENDING, status);

        String lockStatus = selectSingleNode(createdProperties, "/properties/lock-status").getTextContent();
        assertEquals("Iroperties error: invalid lock-status!", STATE_UNLOCKED, lockStatus);
    }
}