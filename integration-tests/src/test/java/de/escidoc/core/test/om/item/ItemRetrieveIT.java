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
import de.escidoc.core.common.exceptions.remote.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.security.AuthorizationException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.Test;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;

/**
 * Test the mock implementation of the item resource.
 *
 * @author Michael Schneider
 */
public class ItemRetrieveIT extends ItemTestBase {

    /**
     * Test retrieve resources of Item.
     *
     * @throws Exception Thrown if anything fails.
     */
    @Test
    public void testRetrieveResources() throws Exception {
        String xml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        String itemXml = create(xml);
        String itemId = getObjidValue(itemXml);

        String resources = retrieveResources(itemId);
        assertXmlValidItem(resources);

    }

    /**
     * Test successfully retrieving item with a component without valid-status. Issue 655.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveItemWithoutComponentValidStatus() throws Exception {
        String xml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create_ComponentWithoutValidStatus.xml");
        String itemXml = create(xml);
        String itemId = getObjidValue(itemXml);
        String componentId =
            getObjidValue(toString(selectSingleNode(getDocument(itemXml), "/item/components/component"), true));

        String retrievedItem = retrieve(itemId);
        assertXmlValidItem(retrievedItem);
        retrieveContent(itemId, componentId);
        // TODO assert that the retrieved item contains the expected values
        assertCreatedItem(retrievedItem, itemXml, startTimestamp);
    }

    /**
     * Test successfully retrieving item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRi1a() throws Exception {
        String xml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        String itemXml = create(xml);
        String itemId = getObjidValue(itemXml);

        String retrievedItem = retrieve(itemId);
        assertXmlValidItem(retrievedItem);
        // TODO assert that the retrieved item contains the expected values
        assertCreatedItem(retrievedItem, itemXml, startTimestamp);
    }

    /**
     * Test declining retrieving item with not existing id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRi2a() throws Exception {

        try {
            retrieve("test");
            fail("No ItemNotFoundException retrieving item with not existing id.");
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ItemNotFoundException.class, e);
        }

    }

    /**
     * Test declining retrieving item with wrong id (id refers to another object type).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRi5() throws Exception {
        String xml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");

        String itemXml = create(xml);
        String componentId = null;
        Node componentObjiId =
            selectSingleNode(EscidocAbstractTest.getDocument(itemXml), "/item/components/component/@href");
        componentId = getObjidFromHref(componentObjiId.getTextContent());
        try {
            retrieve(componentId);
        }
        catch (final ItemNotFoundException e) {
            return;
        }
        fail("Not expected exception");
    }

    /**
     * Test declining retrieving item (input parameter item id is missing).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMDi3() throws Exception {

        try {

            retrieve(null);
        }
        catch (final MissingMethodParameterException e) {
            return;
        }
        fail("Not expected exception");
    }

    /**
     * Test declining retrieving depositor item as anonymous.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveAnonymous() throws Exception {
        String xml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        // PWCallback.setHandle(PWCallback.DEPOSITOR_LIB_HANDLE);
        PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
        String itemXml = create(xml);
        String itemId = getObjidValue(itemXml);

        PWCallback.setHandle(PWCallback.ANONYMOUS_HANDLE);
        Class ec = AuthorizationException.class;
        try {
            retrieve(itemId);
            fail(ec.getName() + " expected");
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
        finally {
            PWCallback.resetHandle();
        }
    }

    /**
     * Test declining retrieving depositor item as author.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveAuthor() throws Exception {
        String xml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        // PWCallback.setHandle(PWCallback.DEPOSITOR_LIB_HANDLE);
        PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
        String itemXml = create(xml);
        String itemId = getObjidValue(itemXml);

        PWCallback.setHandle(PWCallback.AUTHOR_HANDLE);
        Class ec = AuthorizationException.class;
        try {
            retrieve(itemId);
            fail(ec.getName() + " expected");
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
        finally {
            PWCallback.resetHandle();
        }
    }

    /**
     * Test decline retrieving depositor item as other depositor. Issue 608
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveOtherDepositor() throws Exception {
        String xml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
        String itemXml = create(xml);
        String itemId = getObjidValue(itemXml);

        PWCallback.setHandle(PWCallback.DEPOSITOR_LIB_HANDLE);
        try {
            retrieve(itemId);
            EscidocAbstractTest.failMissingException(AuthorizationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(AuthorizationException.class, e);
        }
        PWCallback.resetHandle();
    }

    /**
     * Test retrieving items.
     * <p/>
     * See Bugzilla #586
     *
     * @throws Exception Thrown if the retrieved list is invalid.
     */
    @Test
    public void testRetrieveItems() throws Exception {

        String reqCT = "escidoc:persistent4";
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"/properties/content-model/id\"=" + reqCT });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "10" });

        String list = retrieveItems(filterParams);

        assertXmlValidSrwResponse(list);

        // assert that the components elements has not empty (or $link etc.)
        // values
    }

    /**
     * Test successfully retrieving md-record.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveMdRecord() throws Exception {
        retrieveMdRecord(true, "escidoc");
    }

    /**
     * Test decline retrieving md-record without item ID.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveMdRecordWithoutItemID() throws Exception {
        Class ec = MissingMethodParameterException.class;
        String msg = "Expected " + ec.getName();
        try {
            retrieveMdRecord(false, "escidoc");
        }
        catch (final Exception e) {
            assertExceptionType(msg, ec, e);
        }
    }

    /**
     * Test decline retrieving md-record with no name.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveMdRecordWithoutName() throws Exception {
        Class ec = MissingMethodParameterException.class;
        String msg = "Expected " + ec.getName();
        try {
            retrieveMdRecord(true, null);
        }
        catch (final Exception e) {
            assertExceptionType(msg, ec, e);
        }
    }

    /**
     * Test decline retrieving md-record with empty name.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveMdRecordWithEmptyName() throws Exception {
        Class ec = MissingMethodParameterException.class;
        String msg = "Expected " + ec.getName();
        try {
            retrieveMdRecord(true, "");
        }
        catch (final Exception e) {
            assertExceptionType(msg, ec, e);
        }
    }

    /**
     * Test successfully retrieving md-record with non existing name.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveMdRecordNonExistingName() throws Exception {
        Class ec = MdRecordNotFoundException.class;
        String msg = "Expected " + ec.getName();
        try {
            retrieveMdRecord(true, "blablub");
        }
        catch (final Exception e) {
            assertExceptionType(msg, ec, e);
        }
    }

    /**
     * Test if the objid is handles right.
     * <p/>
     * see issue INFR-773
     * <p/>
     * The tests creates an Item with one Component and uses then on the Item handler the ComponentID with and without
     * version suffix. The framework has to answer with ItemNotFoundException in all cases.
     *
     * @throws Exception If framework behavior is not as expected.
     */
    @Test
    public void testWrongObjid01() throws Exception {

        String xml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_1_component.xml");
        String itemXml = create(xml);
        // String itemId = getObjidValue(itemXml);
        String componentId =
            getObjidValue(toString(selectSingleNode(getDocument(itemXml), "/item/components/component"), true));

        try {
            retrieve(componentId);
        }
        catch (final Exception e) {
            assertExceptionType("Wrong exception", ItemNotFoundException.class, e);
        }

        try {
            retrieve(componentId + ":1");
        }
        catch (final Exception e) {
            assertExceptionType("Wrong exception", ItemNotFoundException.class, e);
        }

        try {
            retrieve(componentId + ":a");
        }
        catch (final Exception e) {
            assertExceptionType("Wrong exception", ItemNotFoundException.class, e);
        }
    }

    /**
     * Creates an Item and retrieves the md-record by given name.
     *
     * @param resourceId If the retrieve should be done with resource ID.
     * @param name       The name of the md-record to be retrieved.
     * @throws Exception If an error occures.
     */
    private void retrieveMdRecord(final boolean resourceId, final String name) throws Exception {
        String xml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        String resourceXml = create(xml);
        String itemId = getObjidValue(resourceXml);
        if (!resourceId) {
            itemId = null;
        }

        String retrievedMdRecord = retrieveMetadataRecord(itemId, name);
        assertCreatedMdRecord(name, itemId, "item", retrievedMdRecord, resourceXml, startTimestamp);

    }

}
