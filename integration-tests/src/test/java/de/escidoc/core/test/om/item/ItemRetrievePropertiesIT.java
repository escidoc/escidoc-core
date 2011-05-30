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
package de.escidoc.core.test.om.item;

import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ItemNotFoundException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.Constants;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author Michael Schneider
 */
public class ItemRetrievePropertiesIT extends ItemTestBase {

    private static String ITEM_ID = null;

    private static String ITEM_XML = null;

    private static Document CREATED_ITEM = null;

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void setUp() throws Exception {
        ITEM_XML =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        CREATED_ITEM = EscidocAbstractTest.getDocument(create(ITEM_XML));
        ITEM_ID = getObjidValue(CREATED_ITEM);
    }

    /**
     * Test successfully retrieving the properties of an item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRIP1() throws Exception {

        String properties = retrieveProperties(ITEM_ID);
        assertItemProperties(properties, toString(selectSingleNode(EscidocAbstractTest.getDocument(ITEM_XML),
            "/item/properties"), true), "/ir/item/" + ITEM_ID + "/properties",
            getLastModificationDateValue(CREATED_ITEM), startTimestamp);

    }

    /**
     * Test retrieving the properties of an unknown item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRIP2a() throws Exception {
        Class ec = ItemNotFoundException.class;
        try {
            retrieveProperties("unknown");
            fail(ec + " expected but no error occurred!");
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }

    }

    /**
     * Test retrieving the properties of an item with missing item id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRIP3a() throws Exception {
        Class ec = MissingMethodParameterException.class;
        try {
            retrieveProperties(null);
            fail(ec + " expected but no error occurred!");
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }

    }

    /**
     * Assert the xmlItemProperties match the expected xmlTemplateItemProperties.
     *
     * @param xmlItemProperties         The retrieved properties.
     * @param xmlTemplateItemProperties The expected properties.
     * @param expectedHRef              The expected href.
     * @param expectedLastModificationTimestamp
     *                                  The last-modification timestamp of the item.
     * @param timestampBeforeCreation   A timestamp before the creation of the item.
     * @throws Exception If anything fails.
     */
    private void assertItemProperties(
        final String xmlItemProperties, final String xmlTemplateItemProperties, final String expectedHRef,
        final String expectedLastModificationTimestamp, final String timestampBeforeCreation) throws Exception {

        Document createdProperties = EscidocAbstractTest.getDocument(xmlItemProperties);
        String href = getRootElementHrefValue(createdProperties);
        if ("".equals(href)) {
            href = null;
        }
        assertNotNull("Item Properties error: href attribute was not set!", href);
        assertEquals("Item Properties error: href has wrong value!", expectedHRef, href);
        String rootLastModificationDate = getLastModificationDateValue(createdProperties);
        if ("".equals(rootLastModificationDate)) {
            rootLastModificationDate = null;
        }
        assertNotNull("Item Properties error: last-modification-date attribute " + "was not set!",
            rootLastModificationDate);
        assertXmlExists("Item Properties error: creation-date was not set!", createdProperties,
            "/properties/creation-date");
        assertXmlExists("Item Properties error: content-model was not set " + "in properties!", createdProperties,
            "/properties/content-model");
        assertXmlExists("Item Properties error: context was not set in " + "properties!", createdProperties,
            "/properties/context");
        assertXmlExists("Item Properties error: public-status was not set!", createdProperties,
            "/properties/public-status");
        assertXmlExists("Item Properties error: lock-status was not set in properties!", createdProperties,
            "/properties/lock-status");

        assertXmlExists("Item Properties error: created-by was not set in properties!", createdProperties,
            "/properties/created-by");

        String creationDate = selectSingleNode(createdProperties, "/properties/creation-date").getTextContent();

        assertTimestampEquals("Item Properties error: last-modification-date in properties "
            + "and in root element are not equal!", expectedLastModificationTimestamp, rootLastModificationDate);

        assertTimestampIsEqualOrAfter("Item Properties error: creation-date is not as expected!", creationDate,
            timestampBeforeCreation);

        assertReferencingElement("Invalid created-by. ", createdProperties, "/properties/created-by",
            Constants.USER_ACCOUNT_BASE_URI);

        Node nodeLockOwner = selectSingleNode(createdProperties, "/properties/lock-owner");
        assertNull("Item Properties error: lock-owner must be null!", nodeLockOwner);

        Node nodeWithdrawalComment = selectSingleNode(createdProperties, "/properties/withdrawal-comment");
        assertNull("Item Properties error: withdrawal-comment must be null!", nodeWithdrawalComment);

        Node nodeLatestRevision = selectSingleNode(createdProperties, "/properties/latest-revison");
        Document template = EscidocAbstractTest.getDocument(xmlTemplateItemProperties);
        assertNull("Item Properties error: latest-revison must be null!", nodeLatestRevision);

        assertXmlEquals("Item Properties error: content-type-specific is wrong!", template, createdProperties,
            "/properties/content-model-specific");

        String status = selectSingleNode(createdProperties, "/properties/public-status").getTextContent();
        assertEquals("Item Properties error: invalid public-status!", STATE_PENDING, status);

        String lockStatus = selectSingleNode(createdProperties, "/properties/lock-status").getTextContent();
        assertEquals("Item Properties error: invalid lock-status!", STATE_UNLOCKED, lockStatus);

        assertReferencingElement("Invalid context. ", selectSingleNode(template, "/properties/context"),
            selectSingleNode(createdProperties, "/properties/context"), "/properties/context",
            Constants.CONTEXT_BASE_URI);

        assertReferencingElement("Invalid content-model. ", selectSingleNode(template, "/properties/content-model"),
            selectSingleNode(createdProperties, "/properties/content-model"), "/properties/content-model",
            Constants.CONTENT_MODEL_BASE_URI);

        // TODO: add check for latest-version, current-version
    }
}
