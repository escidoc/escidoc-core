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

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.security.AuthorizationException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.compare.TripleStoreValue;
import de.escidoc.core.test.common.util.xml.Select;
import de.escidoc.core.test.security.client.PWCallback;
import org.apache.xpath.XPathAPI;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test the mock implementation of the item resource.
 *
 * @author Michael Schneider
 */
public class ItemVersioningIT extends ItemTestBase {

    private static final int ITEM_TRIPLE_COUNT = 36;

    private static final int ITEM_TRIPLE_COUNT_MINUS6 = ITEM_TRIPLE_COUNT - 6;

    private static final int ITEM_TRIPLE_COUNT_MINUS1 = ITEM_TRIPLE_COUNT - 1;

    private static final int ITEM_TRIPLE_COUNT_PLUS2 = ITEM_TRIPLE_COUNT + 2;

    private static final int ITEM_TRIPLE_COUNT_MINUS5 = ITEM_TRIPLE_COUNT - 5;

    private String theItemId;

    private String theItemXml;

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void setUp() throws Exception {
        // create an item and save the id
        String xmlData =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        theItemXml = create(xmlData);
        theItemId = getObjidValue(EscidocAbstractTest.getDocument(theItemXml));
    }

    /**
     * Test german umlaute in comment inside the version-history. Issue INFR-755.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testLifecycleCommentSpecialChars() throws Exception {

        String comment = "test äöüß test";
        String xml;
        String versionHistory;

        submit(theItemId, getTheLastModificationParam(true, theItemId, comment));
        xml = retrieve(theItemId);
        versionHistory = retrieveVersionHistory(theItemId);

        assertEquals(comment, selectSingleNodeAsserted(getDocument(xml), "/*/properties/version/comment/text()")
            .getNodeValue());
        assertEquals(comment, selectSingleNodeAsserted(getDocument(versionHistory),
            "/version-history/version[1]/comment/text()").getNodeValue());

        assignObjectPid(theItemId, getPidParam(theItemId, getFrameworkUrl() + "/ir/item/" + theItemId));

        String versionId = theItemId + ":1";
        assignVersionPid(versionId, getPidParam(versionId, getFrameworkUrl() + "/ir/item/" + versionId));

        release(theItemId, getTheLastModificationParam(true, theItemId, comment));
        xml = retrieve(theItemId);
        versionHistory = retrieveVersionHistory(theItemId);

        assertEquals(comment, selectSingleNodeAsserted(getDocument(xml), "/*/properties/version/comment/text()")
            .getNodeValue());
        assertEquals(comment, selectSingleNodeAsserted(getDocument(versionHistory),
            "/version-history/version[1]/comment/text()").getNodeValue());
    }

    /**
     * Test retrieve version-history.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveVersionHistory() throws Exception {

        String versionHistory = retrieveVersionHistory(theItemId);
        assertXmlValidVersionHistory(versionHistory);

        // assert some node existence -------------------------------
        Node versionHistoryDoc = EscidocAbstractTest.getDocument(versionHistory);
        selectSingleNodeAsserted(versionHistoryDoc, "/version-history");
        selectSingleNodeAsserted(versionHistoryDoc, "/version-history/version");
        selectSingleNodeAsserted(versionHistoryDoc, "/version-history/version/events/event");

        // assert values of selected nodes --------------------------

        // check href of version element
        assertHrefBase(versionHistoryDoc, "/version-history/version/@href", Constants.ITEM_BASE_URI + "/" + theItemId
            + ":1");
        // check objid of version element
        assertHrefObjidConsistency((Document) versionHistoryDoc, "/version-history/version");

        /*
         * Issue INFR-877 (no version number in objid of eventIdentifierValue 'link')
         */
        // /version-history/version[version-number='1']/events/event[1]/eventIdentifierValue
        // == '/ir/item/<objid>/resources/version-history#' +
        // /version-history/version[version-number='1']/events/event[1]/@xmlID
        assertEquals("eventIdentifierVaule " + " of Item [" + theItemId + "] differs from defined format", XPathAPI
            .selectSingleNode(
                (Document) versionHistoryDoc,
                "/version-history/version[version-number='1']"
                    + "/events/event[1]/eventIdentifier/eventIdentifierValue").getTextContent(), "/ir/item/"
            + theItemId
            + "/resources/version-history#"
            + XPathAPI.selectSingleNode((Document) versionHistoryDoc,
                "/version-history/version[version-number='1']" + "/events/event[1]/@xmlID").getTextContent());

    }

    /**
     * Test retrieving version history with wrong id.
     *
     * @throws Exception Thrown if framework throws no ItemNotFoundException
     */
    @Test
    public void testRetrieveVersionHistoryWithWrongId() throws Exception {
        try {
            retrieveVersionHistory("escidoc:foo");
        }
        catch (final Exception e) {
            Class<?> ec = ItemNotFoundException.class;
            EscidocAbstractTest.assertExceptionType(ec, e);
        }
    }

    /**
     * Test retrieving version history without id.
     *
     * @throws Exception Thrown if framework throws no MissingMethodParameterException.
     */
    @Test
    public void testRetrieveVersionHistoryWithoutId() throws Exception {
        try {
            retrieveVersionHistory(null);
        }
        catch (final Exception e) {
            Class<?> ec = MissingMethodParameterException.class;
            EscidocAbstractTest.assertExceptionType(ec, e);
        }
    }

    /**
     * Testing the whole Item lifecycle.
     *
     * @throws Exception Thrown if at least one value is wrong.
     */
    @Test
    public void testLifecycleVersions() throws Exception {

        String xml = theItemXml;
        assertXmlValidItem(xml);

        Document itemDoc = EscidocAbstractTest.getDocument(xml);

        assertXmlExists("New version number", itemDoc, "/item/properties/version/number[text() = '1']");
        assertXmlExists("Properties status pending", itemDoc, "/item/properties/public-status[text() = 'pending']");
        assertXmlExists("version status pending", itemDoc, "/item/properties/version/status[text() = 'pending']");

        TripleStoreValue tsv = new TripleStoreValue();
        tsv.itemTripleStoreValues(itemDoc);

        // assert version-history ----------------------------------------------
        String lmdv1e1 = getLastModificationDateValue(itemDoc);
        String versionHistory = retrieveVersionHistory(theItemId);
        Document versionHistoryDoc = getDocument(versionHistory);
        // check href of version-history/version element
        assertHrefBase(versionHistoryDoc, "/version-history/version/@href", Constants.ITEM_BASE_URI + "/" + theItemId
            + ":1");
        // check objid of version-history/version element
        assertHrefObjidConsistency((Document) versionHistoryDoc, "/version-history/version");

        // check timestamps
        assertXmlExists("Wrong timestamp in version-history root " + "element of Item '" + theItemId + "'",
            versionHistory, "/version-history[@last-modification-date = '" + lmdv1e1 + "']");

        assertEquals("last-modification-date in root attribute of Item [" + theItemId
            + "] differs from /version-history/version/events/event[1]/" + "eventDateTime", XPathAPI.selectSingleNode(
            itemDoc, "/item/@last-modification-date").getTextContent(), XPathAPI.selectSingleNode(versionHistoryDoc,
            "/version-history/version/events/event[1]/eventDateTime").getTextContent());

        assertXmlExists("Wrong timestamp in version element of version-history of Item '" + theItemId + "'",
            versionHistoryDoc, "/version-history/version[@timestamp='" + lmdv1e1 + "']/version-number[text() = '1']");

        // update Item --------------------------------------------------------

        final String xPath = "/item/properties/content-model-specific";

        xml = addElement(xml, xPath + "/nix");
        xml = update(theItemId, xml);
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '2']");
        assertXmlValidItem(xml);

        // assert version-history ----------------------------------------------
        Document itemDocV2 = getDocument(xml);
        String lmdv2e1 = getLastModificationDateValue(itemDocV2);

        versionHistory = retrieveVersionHistory(theItemId);
        versionHistoryDoc = getDocument(versionHistory);
        // check href of version-history/version element
        assertHrefBase(versionHistoryDoc, "/version-history/version/@href", Constants.ITEM_BASE_URI + "/" + theItemId
            + ":2");
        // check objid of version-history/version element
        assertHrefObjidConsistency((Document) versionHistoryDoc, "/version-history/version");
        // check timestamps
        assertXmlExists("Wrong timestamp in version-history root element", versionHistory,
            "/version-history[@last-modification-date = '" + lmdv2e1 + "']");
        assertEquals("last-modification-date in root attribute of Item [" + theItemId
            + "] differs from /version-history/version[version-number='2']/events/event[1]/" + "eventDateTime",
            XPathAPI.selectSingleNode(itemDocV2, "/item/@last-modification-date").getTextContent(), XPathAPI
                .selectSingleNode(versionHistoryDoc,
                    "/version-history/version[version-number='2']/events/event[1]/eventDateTime").getTextContent());

        assertXmlExists("Wrong timestamp in version element of version-history", versionHistoryDoc,
            "/version-history/version[version-number='2']/events/event[last()]/" + "eventDateTime[text() = '" + lmdv2e1
                + "']");
        assertXmlExists("Wrong timestamp in version 2 element of version-history", versionHistoryDoc,
            "/version-history/version[@timestamp='" + lmdv2e1 + "']/version-number[text() = '2']");

        // check version 1 again
        xml = retrieve(theItemId + ":1");
        itemDoc = EscidocAbstractTest.getDocument(xml);
        assertXmlExists("New version number", itemDoc, "/item/properties/version/number[text() = '1']");
        assertXmlExists("Properties status pending", itemDoc, "/item/properties/public-status[text() = 'pending']");
        assertXmlExists("version status pending", itemDoc, "/item/properties/version/status[text() = 'pending']");
        assertXmlValidItem(xml);

        tsv.itemTripleStoreValues(itemDoc);

        submit(theItemId, getTheLastModificationParam(false, theItemId));
        xml = retrieve(theItemId);

        tsv.itemTripleStoreValues(getDocument(xml));
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '2']");
        assertXmlExists("Properties status submitted", xml, "/item/properties/public-status[text() = 'submitted']");
        assertXmlExists("version status submitted", xml, "/item/properties/version/status[text() = 'submitted']");
        assertXmlValidItem(xml);

        xml = addElement(xml, xPath + "/nix");
        xml = update(theItemId, xml);
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '3']");
        assertXmlValidItem(xml);

        versionHistoryDoc = getDocument(retrieveVersionHistory(theItemId));
        // check href of version-history/version element
        assertHrefBase(versionHistoryDoc, "/version-history/version/@href", Constants.ITEM_BASE_URI + "/" + theItemId
            + ":3");
        // check objid of version-history/version element
        assertHrefObjidConsistency((Document) versionHistoryDoc, "/version-history/version");

        // check version 1 and 2 again
        xml = retrieve(theItemId + ":1");
        tsv.itemTripleStoreValues(getDocument(xml));
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '1']");
        assertXmlExists("Properties status submitted", xml, "/item/properties/public-status[text() = 'submitted']");
        assertXmlExists("version status pending", xml, "/item/properties/version/status[text() = 'pending']");
        assertXmlValidItem(xml);
        xml = retrieve(theItemId + ":2");
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '2']");
        assertXmlExists("Properties status submitted", xml, "/item/properties/public-status[text() = 'submitted']");
        assertXmlExists("version status submitted", xml, "/item/properties/version/status[text() = 'submitted']");
        assertXmlValidItem(xml);

        assignObjectPid(theItemId, getPidParam(theItemId, getFrameworkUrl() + "/ir/item/" + theItemId));

        String versionId = theItemId + ":3";
        assignVersionPid(versionId, getPidParam(versionId, getFrameworkUrl() + "/ir/item/" + versionId));

        release(theItemId, getTheLastModificationParam(false, theItemId));
        xml = retrieve(theItemId);
        tsv.itemTripleStoreValues(getDocument(xml));
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '3']");
        assertXmlExists("Properties status released", xml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status released", xml, "/item/properties/version/status[text() = 'released']");
        assertXmlExists("Released item latest-release", xml, "/item/properties/latest-release");
        assertXmlValidItem(xml);

        // updates after version status released are now allowed
        xml = addElement(xml, xPath + "/nix");
        xml = update(theItemId, xml);
        tsv.itemTripleStoreValues(getDocument(xml));
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '4']");
        assertXmlExists("Properties status released", xml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status pending", xml, "/item/properties/version/status[text() = 'pending']");
        assertXmlValidItem(xml);

        versionHistoryDoc = getDocument(retrieveVersionHistory(theItemId));
        // check href of version-history/version element
        assertHrefBase(versionHistoryDoc, "/version-history/version/@href", Constants.ITEM_BASE_URI + "/" + theItemId
            + ":4");
        // check objid of version-history/version element
        assertHrefObjidConsistency((Document) versionHistoryDoc, "/version-history/version");

        // check version 1 and 2 and 3 again
        xml = retrieve(theItemId + ":1");
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '1']");
        assertXmlExists("Properties status released", xml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status pending", xml, "/item/properties/version/status[text() = 'pending']");
        assertXmlValidItem(xml);
        xml = retrieve(theItemId + ":2");
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '2']");
        assertXmlExists("Properties status released", xml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status submitted", xml, "/item/properties/version/status[text() = 'submitted']");
        assertXmlValidItem(xml);
        xml = retrieve(theItemId + ":3");
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '3']");
        assertXmlExists("Properties status released", xml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status released", xml, "/item/properties/version/status[text() = 'released']");
        assertXmlExists("Released item latest-release", xml, "/item/properties/latest-release");
        assertXmlValidItem(xml);

        // submit again
        submit(theItemId, getTheLastModificationParam(false, theItemId));
        xml = retrieve(theItemId);
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '4']");
        assertXmlExists("Properties status released", xml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status submitted", xml, "/item/properties/version/status[text() = 'submitted']");
        assertXmlValidItem(xml);

        xml = addElement(xml, xPath + "/nix");
        xml = update(theItemId, xml);
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '5']");
        assertXmlExists("Properties status released", xml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status submitted", xml, "/item/properties/version/status[text() = 'submitted']");
        assertXmlValidItem(xml);

        versionHistoryDoc = getDocument(retrieveVersionHistory(theItemId));
        // check href of version-history/version element
        assertHrefBase(versionHistoryDoc, "/version-history/version/@href", Constants.ITEM_BASE_URI + "/" + theItemId
            + ":5");
        // check objid of version-history/version element
        assertHrefObjidConsistency((Document) versionHistoryDoc, "/version-history/version");

        // check version 1 and 2 and 3 and 4 again
        xml = retrieve(theItemId + ":1");
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '1']");
        assertXmlExists("Properties status released", xml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status pending", xml, "/item/properties/version/status[text() = 'pending']");
        assertXmlValidItem(xml);
        xml = retrieve(theItemId + ":2");
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '2']");
        assertXmlExists("Properties status released", xml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status submitted", xml, "/item/properties/version/status[text() = 'submitted']");
        assertXmlValidItem(xml);
        xml = retrieve(theItemId + ":3");
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '3']");
        assertXmlExists("Properties status released", xml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status released", xml, "/item/properties/version/status[text() = 'released']");
        assertXmlExists("Released item latest-release", xml, "/item/properties/latest-release");
        assertXmlValidItem(xml);
        xml = retrieve(theItemId + ":4");
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '4']");
        assertXmlExists("Properties status released", xml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status submitted", xml, "/item/properties/version/status[text() = 'submitted']");
        assertXmlValidItem(xml);

        // release again
        versionId = theItemId + ":5";
        assignVersionPid(versionId, getPidParam(versionId, getFrameworkUrl() + "/ir/item/" + versionId));

        release(theItemId, getTheLastModificationParam(false, theItemId));
        xml = retrieve(theItemId);
        tsv.itemTripleStoreValues(getDocument(xml));
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '5']");
        assertXmlExists("Properties status released", xml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status released", xml, "/item/properties/version/status[text() = 'released']");
        assertXmlExists("Released item latest-release", xml, "/item/properties/latest-release");
        assertXmlValidItem(xml);

        withdraw(theItemId, getTheLastModificationParam(true, theItemId));
        xml = retrieve(theItemId);
        tsv.itemTripleStoreValues(getDocument(xml));
        assertXmlExists("Properties status withdrawn", xml, "/item/properties/public-status[text() = 'withdrawn']");
        assertXmlExists("version status must be released if withdrawn", xml,
            "/item/properties/version/status[text() = 'released']");
        assertXmlExists("Further released withdrawn item latest-release", xml, "/item/properties/latest-release");
        assertXmlValidItem(xml);

        try {
            xml = addElement(xml, xPath + "/nix");
            xml = update(theItemId, xml);
            assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '4']");
            assertXmlValidItem(xml);
            fail("Succesful update after withdraw.");
        }
        catch (final Exception e) {
            Class<?> ec = InvalidStatusException.class;
            EscidocAbstractTest.assertExceptionType(ec, e);
        }

        versionHistory = retrieveVersionHistory(theItemId);
        assertFalse("Timestamps not right replaced in version-history", versionHistory.contains("###"));
    }

    /**
     * TODO move this method to a better place in the class. Compare an Item with a list of values which has to be there
     * and which are forbidden.
     *
     * @param xml             The XML of an Item
     * @param expectedValues  Expected values with &lt;XPath, Value&gt; structure. If value is null than is the
     *                        existence of the element itself checked. An Exception is thrown if the element not exist.
     *                        If value is not null the content of the element is compared. An Exception is thrown if the
     *                        values not compare.
     * @param forbiddenValues Expected values with &lt;XPath, Value&gt; structure. If value is null than is the
     *                        existence of the element itself checked. An Exception is thrown if the element exists. If
     *                        value is not null the content of the element is compared. An Exception is thrown if the
     *                        values compare.
     * @throws Exception Is thrown if a expected value not exists (with the right value) or a forbidden element (with
     *                   the wrong value) exist.
     */
    private void validateResource(
        final String xml, final HashMap<String, String> expectedValues, final Vector<String> forbiddenValues)
        throws Exception {

        Document resource = getDocument(xml);

        // checking expected values
        Iterator<String> it = expectedValues.keySet().iterator();

        while (it.hasNext()) {
            String xPath = it.next();
            String value = expectedValues.get(xPath);
            Node n = XPathAPI.selectSingleNode(resource, xPath);
            if (n == null) {
                throw new Exception("Node '" + xPath + "' not exists but it should. (Item "
                    + Select.getObjidValue(resource) + ")");
            }
            if (value != null) {
                String nodeValue = n.getTextContent();
                if (!value.equals(nodeValue)) {
                    throw new Exception("Value of node '" + xPath + "' is '" + nodeValue
                        + "' and differs from expected value '" + value + "' (Item " + Select.getObjidValue(resource)
                        + ")");
                }
            }
        }

        // checking forbidden values
        it = forbiddenValues.iterator();

        while (it.hasNext()) {
            String xPath = it.next();
            Node n = XPathAPI.selectSingleNode(resource, xPath);
            if (n != null) {
                throw new Exception("Node '" + xPath + "' exists but it shouldn't. (Item "
                    + Select.getObjidValue(resource) + ")");
            }
        }

    }

    /**
     * This test extends more or less the lifecycle test by acting with different roles.
     *
     * @throws Exception Thrown if at least one value is wrong.
     */
    @Test
    public void testLifecycleVersions02() throws Exception {

        String itemTempl =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "item_without_component.xml");

        String handle = PWCallback.getHandle();
        String xml = create(itemTempl);
        String objid = getObjidValue(xml);

        // Map<number of item version, vector with components>
        HashMap<String, Vector<String>> componentIds = new HashMap<String, Vector<String>>();

        // version 1 has no Component
        componentIds.put("1", new Vector<String>());

        assertXmlValidItem(xml);
        validateItemVersion1(objid, componentIds, xml);

        // check same on retrieve
        xml = retrieve(objid);
        validateItemVersion1(objid, componentIds, xml);

        // test retrieve Item with different roles and check the Item
        PWCallback.setHandle(PWCallback.ANONYMOUS_HANDLE);
        try {
            retrieve(objid);
            fail("Can retrieve unreleased Item as anonymous!" + " Missing AuthorizationException!");
        }
        catch (final AuthorizationException e) {
            // that's quite well
        }
        PWCallback.setHandle(PWCallback.AUTHOR_HANDLE);
        try {
            retrieve(objid);
            fail("Can retrieve unreleased Item with Author role!" + " Missing AuthorizationException!");
        }
        catch (final AuthorizationException e) {
            // that's quite well
        }
        PWCallback.setHandle(PWCallback.ADMINISTRATOR_HANDLE);
        try {
            retrieve(objid);
            fail("Can retrieve unreleased Item with SystemAdministrator role!" + " Missing AuthorizationException!");
        }
        catch (final AuthorizationException e) {
            // that's quite well
        }

        /*
         * *********************************************************************
         * 
         * submit Item
         * 
         * *********************************************************************
         */
        PWCallback.setHandle(handle);
        submit(objid, getTheLastModificationParam(false, objid));

        xml = retrieve(objid);
        validateItemVersion1Submitted(objid, xml);

        // test retrieve Item with different roles and check the Item
        PWCallback.setHandle(PWCallback.ANONYMOUS_HANDLE);
        try {
            retrieve(objid);
            fail("Can retrieve unreleased Item as anonymous!" + " Missing AuthorizationException!");
        }
        catch (final AuthorizationException e) {
            // that's quite well
        }
        PWCallback.setHandle(PWCallback.AUTHOR_HANDLE);
        try {
            retrieve(objid);
            fail("Can retrieve unreleased Item with Author role!" + " Missing AuthorizationException!");
        }
        catch (final AuthorizationException e) {
            // that's quite well
        }
        PWCallback.setHandle(PWCallback.ADMINISTRATOR_HANDLE);
        try {
            retrieve(objid);
            fail("Can retrieve unreleased Item with SystemAdministrator role!" + " Missing AuthorizationException!");
        }
        catch (final AuthorizationException e) {
            // that's quite well
        }

        /*
         * *********************************************************************
         * 
         * release Item
         * 
         * *********************************************************************
         */
        PWCallback.setHandle(handle);
        releaseWithPid(objid);

        xml = retrieve(objid);
        validateItemVersion1Released(objid, xml);

        // test retrieve Item with different roles and check the Item
        PWCallback.setHandle(PWCallback.ANONYMOUS_HANDLE);
        xml = retrieve(objid);
        validateItemVersion1Released(objid, xml);

        PWCallback.setHandle(PWCallback.AUTHOR_HANDLE);
        xml = retrieve(objid);
        validateItemVersion1Released(objid, xml);

        PWCallback.setHandle(PWCallback.ADMINISTRATOR_HANDLE);
        xml = retrieve(objid);
        validateItemVersion1Released(objid, xml);

        /*
         * *********************************************************************
         * 
         * update Item (by adding first component)
         * 
         * *********************************************************************
         */
        PWCallback.setHandle(handle);
        xml = addComponent(xml);
        xml = update(objid, xml);

        // obtain objid from Component
        Vector<String> components = new Vector<String>();
        String href1 =
            XPathAPI.selectSingleNode(getDocument(xml), "/item/components/component[1]/@href").getTextContent();
        components.add(getObjidFromHref(href1));
        componentIds.put("2", components);

        validateItemVersion2(objid, componentIds, xml);

        // retrieve with same role
        xml = retrieve(objid);
        validateItemVersion2(objid, componentIds, xml);

        // test retrieve Item with different roles and check the Item
        PWCallback.setHandle(PWCallback.ANONYMOUS_HANDLE);
        xml = retrieve(objid);
        validateItemVersion2AsAnonymous(objid, componentIds, xml);

        // retrieving updated Component with different roles
        PWCallback.setHandle(PWCallback.AUTHOR_HANDLE);
        xml = retrieve(objid);
        validateItemVersion2AsAnonymous(objid, componentIds, xml);

        PWCallback.setHandle(PWCallback.ADMINISTRATOR_HANDLE);
        xml = retrieve(objid);
        validateItemVersion2AsAnonymous(objid, componentIds, xml);

        /*
         * *********************************************************************
         * 
         * release Item (version 2)
         * 
         * *********************************************************************
         */
        PWCallback.setHandle(handle);
        submit(objid, getTheLastModificationParam(false, objid));
        releaseWithPid(objid);

        xml = retrieve(objid);
        validateItemVersion2Released(objid, componentIds, xml);
        xml = retrieve(objid + ":2");
        validateItemVersion2Released(objid, componentIds, xml);

        // test retrieve Item with different roles and check the Item
        PWCallback.setHandle(PWCallback.ANONYMOUS_HANDLE);
        xml = retrieve(objid);
        validateItemVersion2Released(objid, componentIds, xml);
        xml = retrieve(objid + ":2");
        validateItemVersion2Released(objid, componentIds, xml);

        PWCallback.setHandle(PWCallback.AUTHOR_HANDLE);
        xml = retrieve(objid);
        validateItemVersion2Released(objid, componentIds, xml);
        xml = retrieve(objid + ":2");
        validateItemVersion2Released(objid, componentIds, xml);

        PWCallback.setHandle(PWCallback.ADMINISTRATOR_HANDLE);
        xml = retrieve(objid);
        validateItemVersion2Released(objid, componentIds, xml);
        xml = retrieve(objid + ":2");
        validateItemVersion2Released(objid, componentIds, xml);

        /*
         * retrieving version 1 (version 2 exists and is released, version 3 is
         * pending)
         */
        PWCallback.setHandle(handle);
        xml = retrieve(objid + ":1");
        validateItemVersion1ReleasedWhileVersion2Released(objid, xml);

        PWCallback.setHandle(PWCallback.ANONYMOUS_HANDLE);
        xml = retrieve(objid + ":1");
        validateItemVersion1ReleasedWhileVersion2Released(objid, xml);

        PWCallback.setHandle(PWCallback.AUTHOR_HANDLE);
        xml = retrieve(objid + ":1");
        validateItemVersion1ReleasedWhileVersion2Released(objid, xml);

        PWCallback.setHandle(PWCallback.ADMINISTRATOR_HANDLE);
        xml = retrieve(objid + ":1");
        validateItemVersion1ReleasedWhileVersion2Released(objid, xml);

        /*
         * *********************************************************************
         * 
         * update Item (2) (by adding second component. Item goes to version
         * number 3, status pending)
         * 
         * *********************************************************************
         */
        PWCallback.setHandle(handle);
        xml = retrieve(objid);
        Document component =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "component_for_create.xml");
        // make second component unique
        final String COMPONENT_2_CONTENT_CATEGORY = String.valueOf(System.nanoTime());
        substitute(component, "/component/properties/content-category", COMPONENT_2_CONTENT_CATEGORY);
        xml = addComponent(xml, component);
        xml = update(objid, xml);

        // obtain objid from Component
        components = new Vector<String>();

        String href2 =
            XPathAPI.selectSingleNode(getDocument(xml), "/item/components/component[1]/@href").getTextContent();
        components.add(getObjidFromHref(href2));
        href2 = XPathAPI.selectSingleNode(getDocument(xml), "/item/components/component[2]/@href").getTextContent();
        components.add(getObjidFromHref(href2));

        componentIds.put("3", components);

        HashMap<String, String> requiredValues = new HashMap<String, String>();
        requiredValues.put("/item/components/component/properties/content-category[text()='"
            + COMPONENT_2_CONTENT_CATEGORY + "']", null);
        validateItemVersion3(objid, componentIds, xml, requiredValues);

        // retrieve with same role
        xml = retrieve(objid);
        validateItemVersion3(objid, componentIds, xml, null);
        xml = retrieve(objid + ":3");
        validateItemVersion3(objid, componentIds, xml, null);

        // test retrieve Item with different roles and check the Item
        PWCallback.setHandle(PWCallback.ANONYMOUS_HANDLE);
        xml = retrieve(objid);
        validateItemVersion3AsAnonymous(objid, componentIds, xml);
        try {
            retrieve(objid + ":3");
            fail("Can retrieve unreleased Item with Anonymous role!" + " Missing AuthorizationException!");
        }
        catch (final AuthorizationException e) {
            // thats quite well
        }

        // retrieving updated Component with different roles
        PWCallback.setHandle(PWCallback.AUTHOR_HANDLE);
        xml = retrieve(objid);
        validateItemVersion3AsAnonymous(objid, componentIds, xml);
        try {
            retrieve(objid + ":3");
            fail("Can retrieve unreleased Item with wrong Author role!" + " Missing AuthorizationException!");
        }
        catch (final AuthorizationException e) {
            // thats quite well
        }

        PWCallback.setHandle(PWCallback.ADMINISTRATOR_HANDLE);
        xml = retrieve(objid);
        validateItemVersion3AsAnonymous(objid, componentIds, xml);
        try {
            retrieve(objid + ":3");
            fail("Can retrieve unreleased Item with Administrator role!" + " Missing AuthorizationException!");
        }
        catch (final AuthorizationException e) {
            // thats quite well
        }

        /*
         * retrieving version 1 (version 2 exists and is released, version 3 is
         * pending)
         */
        PWCallback.setHandle(handle);
        xml = retrieve(objid + ":1");
        validateItemVersion1ReleasedWhileVersion2ReleasedVersion3pending(objid, xml);

        PWCallback.setHandle(PWCallback.ANONYMOUS_HANDLE);
        xml = retrieve(objid + ":1");
        validateItemVersion1ReleasedWhileVersion2ReleasedVersion3pending(objid, xml);

        PWCallback.setHandle(PWCallback.AUTHOR_HANDLE);
        xml = retrieve(objid + ":1");
        validateItemVersion1ReleasedWhileVersion2ReleasedVersion3pending(objid, xml);

        PWCallback.setHandle(PWCallback.ADMINISTRATOR_HANDLE);
        xml = retrieve(objid + ":1");
        validateItemVersion1ReleasedWhileVersion2ReleasedVersion3pending(objid, xml);

        /*
         * retrieving version 2 (version 2 is released with one Component,
         * version 3 exists in status pendig)
         */
        // test retrieve different versions
        // ..
    }

    /**
     * Test properties if version 1 is released and version 2 of Item is pending. See
     * http://www.escidoc.org/jira/browse/INFR-625.
     *
     * @throws Exception Thrown if at least one value is wrong.
     */
    @Test
    public void testLifecycleVersions03() throws Exception {

        /*
         * version 1: 0 Components, submitted, released
         */
        String itemTempl =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "item_without_component.xml");

        String handle = PWCallback.getHandle();
        String xml = create(itemTempl);
        String objid = getObjidValue(xml);
        submit(objid, getTheLastModificationParam(false, objid));
        releaseWithPid(objid);
        xml = retrieve(objid);

        /*
         * version 1: 0 Components, submitted, released version 2: 1 Component,
         * pending
         */
        xml = addComponent(xml);
        xml = update(objid, xml);

        // obtain objid from Component
        Vector<String> components = new Vector<String>();
        String href3 =
            XPathAPI.selectSingleNode(getDocument(xml), "/item/components/component[1]/@href").getTextContent();
        components.add(getObjidFromHref(href3));

        HashMap<String, Vector<String>> componentIds = new HashMap<String, Vector<String>>();
        componentIds.put("2", components);

        validateItemVersion2T03(objid, componentIds, xml);
        xml = retrieve(objid);
        validateItemVersion2T03(objid, componentIds, xml);
        xml = retrieve(objid + ":1");
        validateItemVersion1T03(objid, componentIds, xml);
        xml = retrieve(objid + ":2");
        validateItemVersion2T03(objid, componentIds, xml);

        PWCallback.setAnonymousHandle();
        xml = retrieve(objid);
        validateItemVersion1T03(objid, componentIds, xml);
        xml = retrieve(objid + ":1");
        validateItemVersion1T03(objid, componentIds, xml);

        /*
         * update to version 3
         * 
         * version 1: 0 Components, submitted, released version 2: 1 Component,
         * pending version 3: 2 Components, pending
         */
        PWCallback.setHandle(handle);
        xml = retrieve(objid);
        xml = addComponent(xml);
        xml = update(objid, xml);

        // validateItemVersion3T03(objid, componentIds, xml);
        // xml = retrieve(objid);
        // validateItemVersion3T03(objid, componentIds, xml);

        xml = retrieve(objid + ":1");
        validateItemVersion1T04(objid, componentIds, xml);
        xml = retrieve(objid + ":2");
        validateItemVersion2T04(objid, componentIds, xml);

        PWCallback.setAnonymousHandle();
        xml = retrieve(objid);
        validateItemVersion1T04(objid, componentIds, xml);
        xml = retrieve(objid + ":1");
        validateItemVersion1T04(objid, componentIds, xml);

        /*
         * release version 3
         * 
         * version 1: 0 Components, submitted, released version 2: 1 Component,
         * pending version 3: 2 Components, pending, submitted, released
         */
        PWCallback.setHandle(handle);
        submit(objid, getTheLastModificationParam(false, objid));
        releaseWithPid(objid);

        /*
         * update 2 times: to version 4 and 5 version 1: 0 Components,
         * submitted, released version 2: 1 Component, pending version 3: 2
         * Components, pending, submitted, released version 4: 3 components,
         * pending, components version 5: 4 Components, pending, components
         */
        xml = retrieve(objid);
        xml = addComponent(xml);
        xml = update(objid, xml);
        xml = addComponent(xml);
        xml = update(objid, xml);

        validateItemVersion5T01(objid, componentIds, xml);
        xml = retrieve(objid);
        validateItemVersion5T01(objid, componentIds, xml);

        xml = retrieve(objid + ":4");
        validateItemVersion4T01(objid, componentIds, xml);

        //
        PWCallback.setAnonymousHandle();
        xml = retrieve(objid);
        validateItemVersion3V5T04(objid, componentIds, xml);
        xml = retrieve(objid + ":1");
        validateItemVersion1V5T04(objid, componentIds, xml);
    }

    /**
     * Test update of Component.
     *
     * @throws Exception Thrown if at least one value is wrong.
     */
    @Test
    public void testLifecycleVersions04() throws Exception {

        String xml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_1_component.xml");

        xml = create(xml);
        String objid = getObjidValue(xml);

        Document item = getDocument(xml);
        substitute(item, "/item/components/component/properties/content-category", "some value of version 2");
        xml = toString(item, true);
        xml = update(objid, xml);

        // obtain objid from Component
        Vector<String> components = new Vector<String>();

        String href =
            XPathAPI.selectSingleNode(getDocument(xml), "/item/components/component[1]/@href").getTextContent();
        components.add(getObjidFromHref(href));

        HashMap<String, Vector<String>> componentIds = new HashMap<String, Vector<String>>();
        componentIds.put("2", components);

        validateItemVersion02Test04(objid, componentIds, xml);

        xml = retrieve(objid);
        validateItemVersion02Test04(objid, componentIds, xml);
        xml = retrieve(objid + ":2");
        validateItemVersion02Test04(objid, componentIds, xml);

        /*
         * version 3 (by updating Component)
         */
        item = getDocument(xml);
        substitute(item, "/item/components/component/properties/content-category", "some value of version 3");
        xml = toString(item, true);
        xml = update(objid, xml);

        validateItemVersion03Test04(objid, componentIds, xml);

        xml = retrieve(objid);
        validateItemVersion03Test04(objid, componentIds, xml);
        xml = retrieve(objid + ":3");
        validateItemVersion03Test04(objid, componentIds, xml);

        // retrieve older Item version
        xml = retrieve(objid + ":2");
        validateItemVersion02Test041(objid, componentIds, xml);

    }

    /**
     * Count numbers of latest-release/pids after second submit.
     *
     * @throws Exception Thrown if at least one value is wrong.
     */
    @Test
    public void notestLatestReleasePid() throws Exception {
        // FIXME activate this test

        String itemTempl =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "item_without_component.xml");

        String xml = create(itemTempl);
        String objid = getObjidValue(xml);

        /*
         * Validate TripleStore Value
         */
        TripleStoreValue tsv = new TripleStoreValue();
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/pid>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/version/pid>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/release/pid>");

        submit(objid, getTheLastModificationParam(false, objid));
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/pid>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/version/pid>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/release/pid>");

        releaseWithPid(objid);
        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/pid>");
        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid + ":1", "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/version/pid>");
        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid + ":1", "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/release/pid>");

        xml = retrieve(objid);
        xml = addComponent(xml);
        xml = update(objid, xml);
        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/pid>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/version/pid>");
        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid + ":1", "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/release/pid>");

        submit(objid, getTheLastModificationParam(false, objid));
        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/pid>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/version/pid>");
        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid + ":1", "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/release/pid>");

        releaseWithPid(objid);
        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/pid>");
        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid + ":2", "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/version/pid>");
        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid + ":2", "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/release/pid>");

    }

    /**
     * update with version number in the item id (Bugzilla #614).
     *
     * @throws Exception Thrown if framework not react as expected.
     */
    @Test
    public void testUpdateWithVersionNumber() throws Exception {
        assertXmlValidItem(theItemXml);
        final String xPath = "/item/properties/content-model-specific";

        Document xmlDoc = getDocument(theItemXml);
        int currentVersion =
            Integer.parseInt(selectSingleNode(xmlDoc, "/item/properties/version/number/text()").getNodeValue());
        String newItemXml = addElement(theItemXml, xPath + "/nix");

        theItemXml = update(theItemId + ":" + currentVersion, newItemXml);
        assertXmlExists("New version number", theItemXml, "/item/properties/version/number[text() = '"
            + (currentVersion + 1) + "']");
        assertXmlValidItem(theItemXml);
    }

    @Test
    public void testIncrementVersionNumberByUpdateCMS() throws Exception {
        testIncrementVersionNumber(10, "/item/properties/content-model-specific");
    }

    @Test
    public void testIncrementVersionNumberByUpdateCMSSubmitted() throws Exception {
        submit(theItemId, getTheLastModificationParam(false, theItemId));
        theItemXml = retrieve(theItemId);
        testIncrementVersionNumber(10, "/item/properties/content-model-specific");
    }

    @Test
    public void testIncrementVersionNumberByUpdateMdRecord() throws Exception {
        testIncrementVersionNumber(10, "/item/md-records/md-record[@name='escidoc']/*[1]");
    }

    @Test
    public void testIncrementVersionNumberByUpdateMdRecordWhenSubmitted() throws Exception {
        submit(theItemId, getTheLastModificationParam(false, theItemId));
        theItemXml = retrieve(theItemId);
        testIncrementVersionNumber(10, "/item/md-records/md-record[@name='escidoc']/*[1]");
    }

    public void testIncrementVersionNumber(int rounds, String xPath) throws Exception {
        String addedElementXPath = xPath + "/nox";

        addedElementXPath += "[";
        for (int i = 1; i < rounds; i++) {
            assertXmlExists("Old version number", theItemXml, "/item/properties/version/number[text() = '" + i + "']");
            assertXmlValidItem(theItemXml);
            String newItemXml = addElement(theItemXml, xPath + "/*[1]");
            theItemXml = update(theItemId, newItemXml);
            assertXmlExists("New version number", theItemXml, "/item/properties/version/number[text() = '" + (i + 1)
                + "']");

            // check return value of update
            Document itemDoc = EscidocAbstractTest.getDocument(theItemXml);
            assertXmlExists("Version " + i + " of Item " + theItemId + " content not as expected (negativ).", itemDoc,
                addedElementXPath + i + "]");
            assertXmlNotExists("Version " + i + " of Item " + theItemId + " content not as expected (negativ).",
                itemDoc, addedElementXPath + (i + 1) + "]");

            // check return value of retrieve latest version
            theItemXml = retrieve(theItemId);
            assertXmlExists("Version " + i + " of Item " + theItemId + " content not as expected (negativ).", itemDoc,
                addedElementXPath + i + "]");
            assertXmlNotExists("Version " + i + " of Item " + theItemId + " content not as expected (negativ).",
                itemDoc, addedElementXPath + (i + 1) + "]");
        }
        assertXmlExists("New version number", theItemXml, "/item/properties/version/number[text() = '" + rounds + "']");
        assertXmlValidItem(theItemXml);

        // retrieve versions
        String xml;
        for (int i = rounds; i > 0; i--) {
            xml = retrieve(theItemId + ":" + i);
            if (i > 1) {
                assertXmlExists("Version " + i + " content not as expected (positiv).", xml, addedElementXPath
                    + (i - 1) + "]");
            }

            assertXmlNotExists("Version " + i + " of Item " + theItemId + " content not as expected (negativ).",
                EscidocAbstractTest.getDocument(xml), addedElementXPath + i + "]");
            if (i < rounds) {
                // hrefs
                String baseHref = "/ir/item/" + theItemId + ":" + i;
                assertXmlExists("Item href not as expected.", xml, "/item[@href = '" + baseHref + "']");
                // baseHref += ":" + i;
                assertXmlExists("Item properties href not as expected.", xml, "/item/properties[@href = '" + baseHref
                    + "/properties']");
                NodeList nl = selectNodeList(EscidocAbstractTest.getDocument(xml), "/item/components/component");
                for (int j = nl.getLength() - 1; j >= 0; j--) {
                    selectSingleNodeAsserted(nl.item(j), "self::node()/@href");
                    String compId = getIdFromHrefValue(selectSingleNode(nl.item(j), "./@href").getTextContent());
                    selectSingleNodeAsserted(nl.item(j), "self::node()[@href = '" + baseHref + "/components/component/"
                        + compId + "']");
                    selectSingleNodeAsserted(nl.item(j), "./properties[@href = '" + baseHref + "/components/component/"
                        + compId + "/properties']");
                    selectSingleNodeAsserted(nl.item(j), "./properties[substring(@href, 1, " + baseHref.length()
                        + ") = '" + baseHref + "']");
                }
            }
            assertXmlExists("Version number ", xml, "/item/properties/version/number[text() = '" + i + "']");
            assertXmlValidItem(xml);
        }

        // retrieve versions
        String versionHistory = retrieveVersionHistory(theItemId);
        assertXmlValidVersionHistory(versionHistory);
        Node versionHistoryDoc = EscidocAbstractTest.getDocument(versionHistory);
        selectSingleNodeAsserted(versionHistoryDoc, "/version-history");
        NodeList versions = selectNodeList(versionHistoryDoc, "/version-history/version");
        assertEquals(rounds, versions.getLength());
    }

    /**
     * Test values of Item after full life cycle.
     *
     * @throws Exception Thrown if something is not as expected.
     */
    @Test
    public void testLifecycle() throws Exception {

        // check status in properties, version,
        // TODO version-history
        fullLifecycle(theItemId);
    }

    /**
     * Check full life cycled Item.
     *
     * @param id Id of Item.
     * @throws Exception Thrown if something is not as expected.
     */
    private void fullLifecycle(final String id) throws Exception {

        String xml;
        String versionHistory;

        submit(id, getTheLastModificationParam(false, theItemId));
        xml = retrieve(id);
        assertXmlExists("Properties status submitted", xml, "/item/properties/public-status[text() = 'submitted']");
        assertXmlExists("version status submitted", xml, "/item/properties/version/status[text() = 'submitted']");
        assertXmlValidItem(xml);

        versionHistory = retrieveVersionHistory(id);
        assertXmlValidVersionHistory(versionHistory);
        assertXmlExists("Missing status submitted within event", versionHistory,
            "/version-history/version[1]/events/event[1]/" + "eventType[text() = 'submitted']");

        assignObjectPid(id, getPidParam(id, getFrameworkUrl() + "/ir/item/" + id));

        String versionId = id + ":1";
        assignVersionPid(versionId, getPidParam(versionId, getFrameworkUrl() + "/ir/item/" + versionId));

        release(id, getTheLastModificationParam(false, theItemId));
        xml = retrieve(id);
        assertXmlExists("Properties status released", xml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status released", xml, "/item/properties/version/status[text() = 'released']");
        assertXmlExists("Released item latest-release", xml, "/item/properties/latest-release");
        assertXmlValidItem(xml);

        versionHistory = retrieveVersionHistory(id);
        assertXmlValidVersionHistory(versionHistory);
        assertXmlExists("Event submitted ", versionHistory, "/version-history/version[1]/events/event[1]/"
            + "eventType[text() = 'released']");

        withdraw(id, getTheLastModificationParam(true, theItemId));
        xml = retrieve(id);

        assertXmlExists("Properties status withdrawn", xml, "/item/properties/public-status[text() = 'withdrawn']");
        assertXmlExists("version status must still be released", xml,
            "/item/properties/version/status[text() = 'released']");
        assertXmlExists("Further released withdrawn item latest-release", xml, "/item/properties/latest-release");
        assertXmlValidItem(xml);

        versionHistory = retrieveVersionHistory(id);
        assertXmlValidVersionHistory(versionHistory);
        assertXmlExists("Missing withdraw comment within event", versionHistory,
            "/version-history/version[1]/events/event[1]/" + "eventType[text() = 'withdrawn']");
        /*
         * Issue #INFR-696 (http://www.escidoc.org/jira/browse/INFR-696).
         */
        assertXmlExists("Wrong version-status in version-history", versionHistory,
            "/version-history/version[1]/version-status" + "[text() = 'released']");
    }

    @Test
    public void testStatusLifecycleVersions() throws Exception {
        final String xPath = "/item/properties/content-model-specific";
        final String PUBLIC_STATUS_COMMENT = "public-status-comment";
        final String VERSION_COMMMENT = "version/comment";
        List<String> publicStatusComments = new Vector<String>();
        publicStatusComments.add(0, null);
        List<String> versionComments = new Vector<String>();
        versionComments.add(0, null);

        String xml = theItemXml;
        Document xmlDoc = getDocument(xml);
        int currentVersion = 1;

        assertXmlExists(PUBLIC_STATUS_COMMENT, xml, "/item/properties/" + PUBLIC_STATUS_COMMENT);
        assertXmlExists(VERSION_COMMMENT, xml, "/item/properties/" + VERSION_COMMMENT);
        assertXmlExists("Properties status pending", xml, "/item/properties/public-status[text() = 'pending']");
        assertXmlExists("version status pending", xml, "/item/properties/version/status[text() = 'pending']");

        publicStatusComments.add(currentVersion, selectSingleNode(xmlDoc,
            "item/properties/" + PUBLIC_STATUS_COMMENT + "/text()").getNodeValue());
        versionComments.add(currentVersion, selectSingleNode(xmlDoc, "item/properties/" + VERSION_COMMMENT + "/text()")
            .getNodeValue());
        assertXmlValidItem(xml);

        Document versionHistoryDoc;
        versionHistoryDoc = getDocument(retrieveVersionHistory(theItemId));
        // check comment in version-history
        assertEquals(versionComments.get(currentVersion), selectSingleNode(
            versionHistoryDoc,
            "/version-history/version[@href = '" + Constants.ITEM_BASE_URI + "/" + theItemId + ":" + currentVersion
                + "']/comment/text()").getNodeValue());
        assertEquals(publicStatusComments.get(currentVersion), selectSingleNode(
            versionHistoryDoc,
            "/version-history/version[@href = '" + Constants.ITEM_BASE_URI + "/" + theItemId + ":" + currentVersion
                + "']/comment/text()").getNodeValue());

        // ---------------------------------------------------------------

        xml = addElement(xml, xPath + "/nix");
        xml = update(theItemId, xml);
        xmlDoc = getDocument(xml);
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '2']");
        currentVersion = 2;
        assertXmlValidItem(xml);

        // ---------------------------------------------------------------

        assertXmlExists(PUBLIC_STATUS_COMMENT, xml, "/item/properties/" + PUBLIC_STATUS_COMMENT);
        assertXmlExists(VERSION_COMMMENT, xml, "/item/properties/" + VERSION_COMMMENT);
        assertXmlExists("Properties status pending", xml, "/item/properties/public-status[text() = 'pending']");
        assertXmlExists("version status pending", xml, "/item/properties/version/status[text() = 'pending']");

        publicStatusComments.add(currentVersion, selectSingleNode(xmlDoc,
            "item/properties/" + PUBLIC_STATUS_COMMENT + "/text()").getNodeValue());
        versionComments.add(currentVersion, selectSingleNode(xmlDoc, "item/properties/" + VERSION_COMMMENT + "/text()")
            .getNodeValue());

        assertEquals(publicStatusComments.get(currentVersion), publicStatusComments.get(currentVersion - 1));
        versionHistoryDoc = getDocument(retrieveVersionHistory(theItemId));
        // check comment in version-history
        assertEquals(versionComments.get(currentVersion), selectSingleNode(
            versionHistoryDoc,
            "/version-history/version[@href = '" + Constants.ITEM_BASE_URI + "/" + theItemId + ":" + currentVersion
                + "']/comment/text()").getNodeValue());
        assertNotEquals("Update, " + PUBLIC_STATUS_COMMENT + " must not be equal to " + VERSION_COMMMENT,
            publicStatusComments.get(currentVersion), selectSingleNode(
                versionHistoryDoc,
                "/version-history/version[@href = '" + Constants.ITEM_BASE_URI + "/" + theItemId + ":" + currentVersion
                    + "']/comment/text()").getNodeValue());

        // ---------------------------------------------------------------

        submit(theItemId, getTheLastModificationParam(false, theItemId));
        xml = retrieve(theItemId);
        xmlDoc = getDocument(xml);
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '2']");
        assertXmlValidItem(xml);

        // ---------------------------------------------------------------

        assertXmlExists(PUBLIC_STATUS_COMMENT, xml, "/item/properties/" + PUBLIC_STATUS_COMMENT);
        assertXmlExists(VERSION_COMMMENT, xml, "/item/properties/" + VERSION_COMMMENT);
        assertXmlExists("Properties status submitted", xml, "/item/properties/public-status[text() = 'submitted']");
        assertXmlExists("version status submitted", xml, "/item/properties/version/status[text() = 'submitted']");

        // no new version but
        // version comment changed and
        // public status comment changed
        assertNotEquals(VERSION_COMMMENT + " must be changed", versionComments.get(currentVersion), selectSingleNode(
            xmlDoc, "item/properties/" + VERSION_COMMMENT + "/text()").getNodeValue());
        versionComments.set(currentVersion, selectSingleNode(xmlDoc, "item/properties/" + VERSION_COMMMENT + "/text()")
            .getNodeValue());
        assertNotEquals(PUBLIC_STATUS_COMMENT + " must be changed", publicStatusComments.get(currentVersion),
            selectSingleNode(xmlDoc, "item/properties/" + PUBLIC_STATUS_COMMENT + "/text()").getNodeValue());
        publicStatusComments.set(currentVersion, selectSingleNode(xmlDoc,
            "item/properties/" + PUBLIC_STATUS_COMMENT + "/text()").getNodeValue());

        assertNotEquals(PUBLIC_STATUS_COMMENT + " must be changed", publicStatusComments.get(currentVersion),
            publicStatusComments.get(currentVersion - 1));
        versionHistoryDoc = getDocument(retrieveVersionHistory(theItemId));
        // check comment in version-history
        assertEquals(versionComments.get(currentVersion), selectSingleNode(
            versionHistoryDoc,
            "/version-history/version[@href = '" + Constants.ITEM_BASE_URI + "/" + theItemId + ":" + currentVersion
                + "']/comment/text()").getNodeValue());
        assertEquals(publicStatusComments.get(currentVersion), selectSingleNode(
            versionHistoryDoc,
            "/version-history/version[@href = '" + Constants.ITEM_BASE_URI + "/" + theItemId + ":" + currentVersion
                + "']/comment/text()").getNodeValue());

        // ---------------------------------------------------------------

        xml = addElement(xml, xPath + "/nix");
        xml = update(theItemId, xml);
        xmlDoc = getDocument(xml);
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '3']");
        currentVersion = 3;
        assertXmlValidItem(xml);

        // ---------------------------------------------------------------

        assertXmlExists(PUBLIC_STATUS_COMMENT, xml, "/item/properties/" + PUBLIC_STATUS_COMMENT);
        assertXmlExists(VERSION_COMMMENT, xml, "/item/properties/" + VERSION_COMMMENT);
        assertXmlExists("Properties status submitted", xml, "/item/properties/public-status[text() = 'submitted']");
        assertXmlExists("version status submitted", xml, "/item/properties/version/status[text() = 'submitted']");

        publicStatusComments.add(currentVersion, selectSingleNode(xmlDoc,
            "item/properties/" + PUBLIC_STATUS_COMMENT + "/text()").getNodeValue());
        versionComments.add(currentVersion, selectSingleNode(xmlDoc, "item/properties/" + VERSION_COMMMENT + "/text()")
            .getNodeValue());

        assertEquals(publicStatusComments.get(currentVersion), publicStatusComments.get(currentVersion - 1));
        versionHistoryDoc = getDocument(retrieveVersionHistory(theItemId));
        // check comment in version-history
        assertEquals(versionComments.get(currentVersion), selectSingleNode(
            versionHistoryDoc,
            "/version-history/version[@href = '" + Constants.ITEM_BASE_URI + "/" + theItemId + ":" + currentVersion
                + "']/comment/text()").getNodeValue());
        assertNotEquals("Update, " + PUBLIC_STATUS_COMMENT + " must not be equal to " + VERSION_COMMMENT,
            publicStatusComments.get(currentVersion), selectSingleNode(
                versionHistoryDoc,
                "/version-history/version[@href = '" + Constants.ITEM_BASE_URI + "/" + theItemId + ":" + currentVersion
                    + "']/comment/text()").getNodeValue());

        // ---------------------------------------------------------------

        assignObjectPid(theItemId, getPidParam(theItemId, getFrameworkUrl() + "/ir/item/" + theItemId));

        String versionId = theItemId + ":3";
        assignVersionPid(versionId, getPidParam(versionId, getFrameworkUrl() + "/ir/item/" + versionId));

        release(theItemId, getTheLastModificationParam(false, theItemId));
        xml = retrieve(theItemId);
        xmlDoc = getDocument(xml);
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '3']");
        assertXmlValidItem(xml);

        // ---------------------------------------------------------------

        assertXmlExists(PUBLIC_STATUS_COMMENT, xml, "/item/properties/" + PUBLIC_STATUS_COMMENT);
        assertXmlExists(VERSION_COMMMENT, xml, "/item/properties/" + VERSION_COMMMENT);
        assertXmlExists("Properties status released", xml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status released", xml, "/item/properties/version/status[text() = 'released']");

        // no new version but
        // version comment changed and
        // public status comment changed
        assertNotEquals(VERSION_COMMMENT + " must be changed", versionComments.get(currentVersion), selectSingleNode(
            xmlDoc, "item/properties/" + VERSION_COMMMENT + "/text()").getNodeValue());
        versionComments.add(currentVersion, selectSingleNode(xmlDoc, "item/properties/" + VERSION_COMMMENT + "/text()")
            .getNodeValue());
        assertNotEquals(PUBLIC_STATUS_COMMENT + " must be changed", publicStatusComments.get(currentVersion),
            selectSingleNode(xmlDoc, "item/properties/" + PUBLIC_STATUS_COMMENT + "/text()").getNodeValue());
        publicStatusComments.set(currentVersion, selectSingleNode(xmlDoc,
            "item/properties/" + PUBLIC_STATUS_COMMENT + "/text()").getNodeValue());

        assertNotEquals(PUBLIC_STATUS_COMMENT + " must be changed", publicStatusComments.get(currentVersion),
            publicStatusComments.get(currentVersion - 1));
        versionHistoryDoc = getDocument(retrieveVersionHistory(theItemId));
        // check comment in version-history
        assertEquals(versionComments.get(currentVersion), selectSingleNode(
            versionHistoryDoc,
            "/version-history/version[@href = '" + Constants.ITEM_BASE_URI + "/" + theItemId + ":" + currentVersion
                + "']/comment/text()").getNodeValue());
        assertEquals(publicStatusComments.get(currentVersion), selectSingleNode(
            versionHistoryDoc,
            "/version-history/version[@href = '" + Constants.ITEM_BASE_URI + "/" + theItemId + ":" + currentVersion
                + "']/comment/text()").getNodeValue());

        // ---------------------------------------------------------------

        xml = addElement(xml, xPath + "/nix");
        xml = update(theItemId, xml);
        xmlDoc = getDocument(xml);
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '4']");
        currentVersion = 4;
        assertXmlValidItem(xml);

        // ---------------------------------------------------------------

        assertXmlExists(PUBLIC_STATUS_COMMENT, xml, "/item/properties/" + PUBLIC_STATUS_COMMENT);
        assertXmlExists(VERSION_COMMMENT, xml, "/item/properties/" + VERSION_COMMMENT);
        assertXmlExists("Properties status released", xml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status pending", xml, "/item/properties/version/status[text() = 'pending']");

        publicStatusComments.add(currentVersion, selectSingleNode(xmlDoc,
            "item/properties/" + PUBLIC_STATUS_COMMENT + "/text()").getNodeValue());
        versionComments.add(currentVersion, selectSingleNode(xmlDoc, "item/properties/" + VERSION_COMMMENT + "/text()")
            .getNodeValue());

        assertEquals(publicStatusComments.get(currentVersion), publicStatusComments.get(currentVersion - 1));
        versionHistoryDoc = getDocument(retrieveVersionHistory(theItemId));
        // check comment in version-history
        assertEquals(versionComments.get(currentVersion), selectSingleNode(
            versionHistoryDoc,
            "/version-history/version[@href = '" + Constants.ITEM_BASE_URI + "/" + theItemId + ":" + currentVersion
                + "']/comment/text()").getNodeValue());
        assertNotEquals("Update, " + PUBLIC_STATUS_COMMENT + " must not be equal to " + VERSION_COMMMENT,
            publicStatusComments.get(currentVersion), selectSingleNode(
                versionHistoryDoc,
                "/version-history/version[@href = '" + Constants.ITEM_BASE_URI + "/" + theItemId + ":" + currentVersion
                    + "']/comment/text()").getNodeValue());

        // ---------------------------------------------------------------

        // submit again
        submit(theItemId, getTheLastModificationParam(false, theItemId));
        xml = retrieve(theItemId);
        xmlDoc = getDocument(xml);
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '4']");
        assertXmlValidItem(xml);

        // ---------------------------------------------------------------

        assertXmlExists(PUBLIC_STATUS_COMMENT, xml, "/item/properties/" + PUBLIC_STATUS_COMMENT);
        assertXmlExists(VERSION_COMMMENT, xml, "/item/properties/" + VERSION_COMMMENT);
        assertXmlExists("Properties status released", xml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status submitted", xml, "/item/properties/version/status[text() = 'submitted']");

        // no new version but
        // version comment changed and
        // public status comment NOT changed
        assertNotEquals(VERSION_COMMMENT + " must be changed", versionComments.get(currentVersion), selectSingleNode(
            xmlDoc, "item/properties/" + VERSION_COMMMENT + "/text()").getNodeValue());
        versionComments.set(currentVersion, selectSingleNode(xmlDoc, "item/properties/" + VERSION_COMMMENT + "/text()")
            .getNodeValue());
        assertEquals(PUBLIC_STATUS_COMMENT + " must NOT be changed", publicStatusComments.get(currentVersion),
            selectSingleNode(xmlDoc, "item/properties/" + PUBLIC_STATUS_COMMENT + "/text()").getNodeValue());

        versionHistoryDoc = getDocument(retrieveVersionHistory(theItemId));
        // check comment in version-history
        assertEquals(versionComments.get(currentVersion), selectSingleNode(
            versionHistoryDoc,
            "/version-history/version[@href = '" + Constants.ITEM_BASE_URI + "/" + theItemId + ":" + currentVersion
                + "']/comment/text()").getNodeValue());
        assertNotEquals(PUBLIC_STATUS_COMMENT + " must not be changed", publicStatusComments.get(currentVersion),
            selectSingleNode(
                versionHistoryDoc,
                "/version-history/version[@href = '" + Constants.ITEM_BASE_URI + "/" + theItemId + ":" + currentVersion
                    + "']/comment/text()").getNodeValue());

        // ---------------------------------------------------------------

        xml = addElement(xml, xPath + "/nix");
        xml = update(theItemId, xml);
        xmlDoc = getDocument(xml);
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '5']");
        currentVersion = 5;
        assertXmlValidItem(xml);

        // ---------------------------------------------------------------

        assertXmlExists(PUBLIC_STATUS_COMMENT, xml, "/item/properties/" + PUBLIC_STATUS_COMMENT);
        assertXmlExists(VERSION_COMMMENT, xml, "/item/properties/" + VERSION_COMMMENT);
        assertXmlExists("Properties status released", xml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status submitted", xml, "/item/properties/version/status[text() = 'submitted']");

        publicStatusComments.add(currentVersion, selectSingleNode(xmlDoc,
            "item/properties/" + PUBLIC_STATUS_COMMENT + "/text()").getNodeValue());
        versionComments.add(currentVersion, selectSingleNode(xmlDoc, "item/properties/" + VERSION_COMMMENT + "/text()")
            .getNodeValue());

        assertEquals(publicStatusComments.get(currentVersion), publicStatusComments.get(currentVersion - 1));
        versionHistoryDoc = getDocument(retrieveVersionHistory(theItemId));
        // check comment in version-history
        assertEquals(versionComments.get(currentVersion), selectSingleNode(
            versionHistoryDoc,
            "/version-history/version[@href = '" + Constants.ITEM_BASE_URI + "/" + theItemId + ":" + currentVersion
                + "']/comment/text()").getNodeValue());
        assertNotEquals("Update, " + PUBLIC_STATUS_COMMENT + " must not be equal to " + VERSION_COMMMENT,
            publicStatusComments.get(currentVersion), selectSingleNode(
                versionHistoryDoc,
                "/version-history/version[@href = '" + Constants.ITEM_BASE_URI + "/" + theItemId + ":" + currentVersion
                    + "']/comment/text()").getNodeValue());

        // ---------------------------------------------------------------

        // release again
        versionId = theItemId + ":5";
        assignVersionPid(versionId, getPidParam(versionId, getFrameworkUrl() + "/ir/item/" + versionId));

        release(theItemId, getTheLastModificationParam(false, theItemId));
        xml = retrieve(theItemId);
        xmlDoc = getDocument(xml);
        assertXmlExists("New version number", xml, "/item/properties/version/number[text() = '5']");
        assertXmlValidItem(xml);

        // ---------------------------------------------------------------

        assertXmlExists(PUBLIC_STATUS_COMMENT, xml, "/item/properties/" + PUBLIC_STATUS_COMMENT);
        assertXmlExists(VERSION_COMMMENT, xml, "/item/properties/" + VERSION_COMMMENT);
        assertXmlExists("Properties status released", xml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status released", xml, "/item/properties/version/status[text() = 'released']");

        // no new version but
        // version comment changed and
        // public status comment changed
        assertNotEquals(VERSION_COMMMENT + " must be changed", versionComments.get(currentVersion), selectSingleNode(
            xmlDoc, "item/properties/" + VERSION_COMMMENT + "/text()").getNodeValue());
        versionComments.set(currentVersion, selectSingleNode(xmlDoc, "item/properties/" + VERSION_COMMMENT + "/text()")
            .getNodeValue());
        // TODO same generated comment as with last release
        // assertNotEquals(PUBLIC_STATUS_COMMENT + " must be changed",
        // publicStatusComments.get(currentVersion), selectSingleNode(xmlDoc,
        // "item/properties/" + PUBLIC_STATUS_COMMENT + "/text()")
        // .getNodeValue());
        publicStatusComments.set(currentVersion, selectSingleNode(xmlDoc,
            "item/properties/" + PUBLIC_STATUS_COMMENT + "/text()").getNodeValue());

        versionHistoryDoc = getDocument(retrieveVersionHistory(theItemId));
        // check comment in version-history
        assertEquals(versionComments.get(currentVersion), selectSingleNode(
            versionHistoryDoc,
            "/version-history/version[@href = '" + Constants.ITEM_BASE_URI + "/" + theItemId + ":" + currentVersion
                + "']/comment/text()").getNodeValue());
        assertEquals(publicStatusComments.get(currentVersion), selectSingleNode(
            versionHistoryDoc,
            "/version-history/version[@href = '" + Constants.ITEM_BASE_URI + "/" + theItemId + ":" + currentVersion
                + "']/comment/text()").getNodeValue());

        // ---------------------------------------------------------------

        withdraw(theItemId, getTheLastModificationParam(true, theItemId));
        xml = retrieve(theItemId);
        xmlDoc = getDocument(xml);
        assertXmlValidItem(xml);

        // ---------------------------------------------------------------

        assertXmlExists(PUBLIC_STATUS_COMMENT, xml, "/item/properties/" + PUBLIC_STATUS_COMMENT);
        assertXmlExists(VERSION_COMMMENT, xml, "/item/properties/" + VERSION_COMMMENT);
        assertXmlExists("Properties status withdrawn", xml, "/item/properties/public-status[text() = 'withdrawn']");
        assertXmlExists("version status must still be released", xml,
            "/item/properties/version/status[text() = 'released']");

        // no new version but
        // version comment NOT changed and
        // public status comment changed
        assertEquals(VERSION_COMMMENT + " must NOT be changed", versionComments.get(currentVersion), selectSingleNode(
            xmlDoc, "item/properties/" + VERSION_COMMMENT + "/text()").getNodeValue());
        assertNotEquals(PUBLIC_STATUS_COMMENT + " must be changed", publicStatusComments.get(currentVersion),
            selectSingleNode(xmlDoc, "item/properties/" + PUBLIC_STATUS_COMMENT + "/text()").getNodeValue());
        publicStatusComments.set(currentVersion, selectSingleNode(xmlDoc,
            "item/properties/" + PUBLIC_STATUS_COMMENT + "/text()").getNodeValue());

        versionHistoryDoc = getDocument(retrieveVersionHistory(theItemId));
        // check comment in version-history
        assertEquals(versionComments.get(currentVersion), selectSingleNode(
            versionHistoryDoc,
            "/version-history/version[@href = '" + Constants.ITEM_BASE_URI + "/" + theItemId + ":" + currentVersion
                + "']/comment/text()").getNodeValue());
        assertNotEquals(PUBLIC_STATUS_COMMENT + " should not be in version history. :-)", publicStatusComments
            .get(currentVersion), selectSingleNode(
            versionHistoryDoc,
            "/version-history/version[@href = '" + Constants.ITEM_BASE_URI + "/" + theItemId + ":" + currentVersion
                + "']/comment/text()").getNodeValue());

        // ---------------------------------------------------------------
        // test update withdrawn Item
        // see also (issue INFR-710)
        try {
            xml = addElement(xml, xPath + "/nix");
            update(theItemId, xml);
            fail("Update after withdrawn is possible.");
        }
        catch (final InvalidStatusException e) {
            // that's ok
        }

        try {
            submit(theItemId, getTheLastModificationParam(false, theItemId));
            fail("Submit after withdrawn is possible.");
        }
        catch (final InvalidStatusException e) {
            // that's ok
        }

        try {
            revise(theItemId, getTheLastModificationParam(false, theItemId));
            fail("Revise after withdrawn is possible.");
        }
        catch (final InvalidStatusException e) {
            // that's ok
        }

        try {
            release(theItemId, getTheLastModificationParam(false, theItemId));
            fail("Release after withdrawn is possible.");
        }
        catch (final InvalidStatusException e) {
            // that's ok
        }

        try {
            delete(theItemId);
            fail("Delete after withdrawn is possible.");
        }
        catch (final InvalidStatusException e) {
            // that's ok
        }

        try {
            lock(theItemId, getTheLastModificationParam(false, theItemId));
            fail("Lock after withdrawn is possible.");
        }
        catch (final InvalidStatusException e) {
            // that's ok
        }
    }

    /**
     * Test the last-modification-date of the return values in the whole item lifecycle.
     * <p/>
     * The timestamp of the task oriented methods is compared with the timestamp of the resource before and after the
     * method call (retrieve timestamp &lt; task oriented method timestamp == retrieve timestamp).
     *
     * @throws Exception Thrown if anything went wrong.
     */
    @Test
    public void testReturnLastModificationDateLifecycle() throws Exception {

        String xml = theItemXml;
        Document itemDoc = getDocument(xml);
        String lmdRetrieve1 = getLastModificationDateValue(itemDoc);

        // ---------------------------------------------------------------
        // check first version

        // submit
        String resultXml = submit(theItemId, getTheLastModificationParam(false, theItemId));
        assertXmlValidResult(resultXml);
        Document resultDoc = getDocument(resultXml);
        String lmdMethod = getLastModificationDateValue(resultDoc);
        assertDateBeforeAfter(lmdRetrieve1, lmdMethod);

        xml = retrieve(theItemId);
        itemDoc = getDocument(xml);
        String lmdRetrieve2 = getLastModificationDateValue(itemDoc);

        assertEquals("Last modification date of retrieve does not match the"
            + " last modification date of the latest task oriented method", lmdMethod, lmdRetrieve2);

        // revise
        resultXml = revise(theItemId, getTheLastModificationParam(false, theItemId, "comment", lmdMethod));
        assertXmlValidResult(resultXml);
        resultDoc = getDocument(resultXml);
        lmdMethod = getLastModificationDateValue(resultDoc);
        assertDateBeforeAfter(lmdRetrieve1, lmdMethod);

        xml = retrieve(theItemId);
        itemDoc = getDocument(xml);
        lmdRetrieve2 = getLastModificationDateValue(itemDoc);

        assertEquals("Last modification date of retrieve does not match the"
            + " last modification date of the latest task oriented method", lmdMethod, lmdRetrieve2);

        // submit again
        resultXml = submit(theItemId, getTheLastModificationParam(false, theItemId, "comment", lmdMethod));
        assertXmlValidResult(resultXml);
        resultDoc = getDocument(resultXml);
        lmdMethod = getLastModificationDateValue(resultDoc);
        assertDateBeforeAfter(lmdRetrieve1, lmdMethod);

        xml = retrieve(theItemId);
        itemDoc = getDocument(xml);
        lmdRetrieve2 = getLastModificationDateValue(itemDoc);

        assertEquals("Last modification date of retrieve does not match the"
            + " last modification date of the latest task oriented method", lmdMethod, lmdRetrieve2);

        // assignPids
        String pidParam = getPidParam(theItemId, "http://some/url");
        resultXml = assignObjectPid(theItemId, pidParam);
        assertXmlValidResult(resultXml);

        pidParam = getPidParam(theItemId, "http://some/url");
        resultXml = assignVersionPid(theItemId, pidParam);
        assertXmlValidResult(resultXml);

        resultDoc = getDocument(resultXml);
        lmdMethod = getLastModificationDateValue(resultDoc);

        // release
        resultXml = release(theItemId, getTheLastModificationParam(false, theItemId, "comment", lmdMethod));
        assertXmlValidResult(resultXml);
        resultDoc = getDocument(resultXml);
        lmdMethod = getLastModificationDateValue(resultDoc);
        assertDateBeforeAfter(lmdRetrieve1, lmdMethod);

        xml = retrieve(theItemId);
        itemDoc = getDocument(xml);
        lmdRetrieve2 = getLastModificationDateValue(itemDoc);

        assertEquals("Last modification date of retrieve does not match the"
            + " last modification date of the latest task oriented method", lmdMethod, lmdRetrieve2);

        // withdraw
        resultXml = withdraw(theItemId, getTheLastModificationParam(true, theItemId, "comment", lmdMethod));
        assertXmlValidResult(resultXml);
        resultDoc = getDocument(resultXml);
        lmdMethod = getLastModificationDateValue(resultDoc);
        assertDateBeforeAfter(lmdRetrieve1, lmdMethod);

        xml = retrieve(theItemId);
        itemDoc = getDocument(xml);
        lmdRetrieve2 = getLastModificationDateValue(itemDoc);

        assertEquals("Last modification date of retrieve does not match the"
            + " last modification date of the latest task oriented method", lmdMethod, lmdRetrieve2);
    }

    /**
     * Test update Item title. The new version has to have the new, updated title and description, but the old versions
     * have to have the old title and description of the Item.
     * <p/>
     * See Issue #647 (http://www.escidoc.org/jira/browse/INFR-647)
     *
     * @throws Exception Thrown if behavior is not like expected.
     */
    @Test
    public void testUpdateItemTitle01() throws Exception {

        int maxVersions = 4;

        Document itemDoc = getDocument(getExampleTemplate("item-minimal-for-create-05.xml"));
        substitute(itemDoc, "/item/md-records/md-record[@name='escidoc']/metadata/title", "Title Version 1");
        substitute(itemDoc, "/item/md-records/md-record[@name='escidoc']/metadata/description", "Description Version 1");
        String itemXml = toString(itemDoc, true);
        itemXml = create(itemXml);

        itemDoc = getDocument(itemXml);
        String id = getObjidValue(itemDoc);

        for (int i = 2; i <= maxVersions; i++) {
            substitute(itemDoc, "/item/md-records/md-record[@name='escidoc']/metadata/title", "Title Version " + i);
            substitute(itemDoc, "/item/md-records/md-record[@name='escidoc']/metadata/description",
                "Description Version " + i);
            itemXml = toString(itemDoc, true);
            itemXml = update(id, itemXml);
            itemDoc = getDocument(itemXml);

        }
        // retrieve all versions and assert titles
        for (int i = 1; i <= maxVersions; i++) {
            itemXml = retrieve(id + ":" + i);
            assertXmlExists("Wrong dc:title in version " + i, itemXml,
                "/item/md-records/md-record[@name='escidoc']/metadata/title" + "[text() = 'Title Version " + i + "']");
            assertXmlExists("Wrong xlink:title in version " + i, itemXml, "/item[@title='Title Version " + i + "']");
            assertXmlExists("Wrong dc:title in version " + i, itemXml,
                "/item/md-records/md-record[@name='escidoc']/metadata/description" + "[text() = 'Description Version "
                    + i + "']");
        }

    }

    /**
     * Test timestamps of Item.
     * <p/>
     * Issue INFR-707
     */
    @Test
    public void testItemTimestamps01() throws Exception {

        String itemXml = getExampleTemplate("item-minimal-for-create-01.xml");
        String itemV1E1 = create(itemXml);

        Document itemDocV1E1 = getDocument(itemV1E1);
        String objid = getObjidValue(itemV1E1);

        Document wovDocV1E1 = EscidocAbstractTest.getDocument(retrieveVersionHistory(objid));

        // check timestamps consistency ----------------------------------
        // /item/@last-modification-date == /item/properties/creation-date
        assertEquals("Timestamp in root attribute of Item [" + objid + "] differs from creation-date"
            + " (create was the one and only event)", XPathAPI.selectSingleNode(itemDocV1E1,
            "/item/@last-modification-date").getTextContent(), XPathAPI.selectSingleNode(itemDocV1E1,
            "/item/properties/creation-date").getTextContent());

        // /item/@last-modification-date == /item/properties/version/date
        assertEquals("Timestamp in root attribute differs from version-date" + " (this Item has only one version)",
            XPathAPI.selectSingleNode(itemDocV1E1, "/item/@last-modification-date").getTextContent(), XPathAPI
                .selectSingleNode(itemDocV1E1, "/item/properties/version/date").getTextContent());

        // /item/@last-modification-date == /item/latest-version/date
        assertEquals("Timestamp in root attribute differs from creation-date", XPathAPI.selectSingleNode(itemDocV1E1,
            "/item/@last-modification-date").getTextContent(), XPathAPI.selectSingleNode(itemDocV1E1,
            "/item/properties/latest-version/date").getTextContent());

        // /item/@last-modification-date ==
        // /version-history/@last-modification-date
        assertEquals("Timestamp in root attribute differs from creation-date", XPathAPI.selectSingleNode(itemDocV1E1,
            "/item/@last-modification-date").getTextContent(), XPathAPI.selectSingleNode(wovDocV1E1,
            "/version-history/@last-modification-date").getTextContent());

        // /item/@last-modification-date ==
        // /version-history/version[version-number='1']/timestamp
        assertEquals("Timestamp in root attribute differs from creation-date", XPathAPI.selectSingleNode(itemDocV1E1,
            "/item/@last-modification-date").getTextContent(), XPathAPI.selectSingleNode(wovDocV1E1,
            "/version-history/version[version-number='1']/timestamp").getTextContent());

        // /item/@last-modification-date ==
        // /version-history/version[<objid>:1]/events/event/eventDateTime
        assertEquals("Timestamp in root attribute differs from creation-date", XPathAPI.selectSingleNode(itemDocV1E1,
            "/item/@last-modification-date").getTextContent(), XPathAPI.selectSingleNode(wovDocV1E1,
            "/version-history/version[version-number='1']" + "/events/event[last()]/eventDateTime").getTextContent());

        // change status of version 1 ------------------------------------
        submit(objid, getTheLastModificationParam(false, objid, "submit", getLastModificationDateValue(itemDocV1E1)));
        assignObjectPid(objid, getPidParam(objid, getFrameworkUrl() + "/ir/item/" + objid));
        String versionId = objid + ":1";
        assignVersionPid(versionId, getPidParam(versionId, getFrameworkUrl() + "/ir/item/" + versionId));
        release(objid, getTheLastModificationParam(false, objid));

        itemXml = retrieve(objid);
        // update to version 2
        String temp = addComponent(itemXml);
        update(objid, temp);

        // check timestamps of now retrieved version 1 with former retrieved
        // version 1
        String itemV1E5 = retrieve(objid + ":1");
        Document itemDocV1E5 = getDocument(itemV1E5);

        String lmdRootV1E1R = getLastModificationDateValue(itemDocV1E5);
        String creationDateV1E1R =
            selectSingleNode(itemDocV1E5, "/item/properties/creation-date/text()").getNodeValue();
        String versionDateV1E1R = selectSingleNode(itemDocV1E5, "/item/properties/version/date/text()").getNodeValue();
        String latestVersionDateV1E1R =
            selectSingleNode(itemDocV1E5, "/item/properties/latest-version/date/text()").getNodeValue();
        String latestReleaseDateV1E1R =
            selectSingleNode(itemDocV1E5, "/item/properties/latest-release/date/text()").getNodeValue();

        assertTrue("last-modification-date is behind version date",
            compareTimestamps(lmdRootV1E1R, creationDateV1E1R) >= 0);
        assertTrue("last-modification-date fits not to //latest-release/date", compareTimestamps(lmdRootV1E1R,
            latestReleaseDateV1E1R) >= 0);
        assertTrue("creation-date fits not to //version/date",
            compareTimestamps(versionDateV1E1R, creationDateV1E1R) > 0);
        assertTrue("cration-date fits not to //latest-version/date", compareTimestamps(latestVersionDateV1E1R,
            creationDateV1E1R) > 0);
    }

    /**
     * Test timestamps of Item.
     * <p/>
     * Issue INFR-820
     * <p/>
     * Item changed in following way: <ul> <li>create</li> <li>submit</li> <li>assign object pid</li> <li>assign version
     * pid</li> <li>release</li> </ul>
     */
    @Test
    public void testItemTimestamps02() throws Exception {

        String itemXml = getExampleTemplate("item-minimal-for-create-01.xml");
        String itemV1E1 = create(itemXml);

        String objid = getObjidValue(itemV1E1);

        submit(objid, getTheLastModificationParam(false, objid, "submit",
            getLastModificationDateValue(getDocument(itemV1E1))));
        assignObjectPid(objid, getPidParam(objid, getFrameworkUrl() + "/ir/item/" + objid));
        String versionId = objid + ":1";
        assignVersionPid(versionId, getPidParam(versionId, getFrameworkUrl() + "/ir/item/" + versionId));
        release(objid, getTheLastModificationParam(false, objid));

        itemXml = retrieve(objid);
        Document itemDocV1E5 = getDocument(itemXml);

        Document wovDocV1E5 = EscidocAbstractTest.getDocument(retrieveVersionHistory(objid));

        // check timestamps consistency ==================================

        // check timestamps within Item XML-------------------------------
        // /item/@last-modification-date == /item/properties/version/date
        assertEquals("last-modification-date in root attribute of Item [" + objid + "] differs from //version/date",
            XPathAPI.selectSingleNode(itemDocV1E5, "/item/@last-modification-date").getTextContent(), XPathAPI
                .selectSingleNode(itemDocV1E5, "/item/properties/version/date").getTextContent());

        // /item/@last-modification-date == /item/properties/latest-version/date
        assertEquals("last-modification-date in root attribute of Item [" + objid
            + "] differs from //latest-version/date", XPathAPI.selectSingleNode(itemDocV1E5,
            "/item/@last-modification-date").getTextContent(), XPathAPI.selectSingleNode(itemDocV1E5,
            "/item/properties/latest-version/date").getTextContent());

        // /item/@last-modification-date == /item/properties/latest-release/date
        assertEquals("last-modification-date in root attribute of Item [" + objid
            + "] differs from //latest-version/date", XPathAPI.selectSingleNode(itemDocV1E5,
            "/item/@last-modification-date").getTextContent(), XPathAPI.selectSingleNode(itemDocV1E5,
            "/item/properties/latest-release/date").getTextContent());

        // check timestamps within Version History XML -------------------
        // /version-history/version[version-number='1']/events/event[1]/eventDateTime
        // ==
        // /version-history/version[version-number='1']/@timestamp
        assertEquals("eventDateTime of the latest event of version 1 differs "
            + "from timestamp attribute of version 1 [" + objid + "]", XPathAPI.selectSingleNode(wovDocV1E5,
            "/version-history/version[version-number='1']" + "/events/event[5]/eventDateTime").getTextContent(),
            XPathAPI
                .selectSingleNode(wovDocV1E5, "/version-history/version[version-number='1']/@timestamp")
                .getTextContent());

        // check timestamps between Item XML and Version History XML -----
        // /item/@last-modification-date ==
        // /version-history/@last-modification-date
        assertEquals("last-modification-date in root attribute of Item [" + objid
            + "] differs from last-modification-date " + "attribute of version-history", XPathAPI.selectSingleNode(
            itemDocV1E5, "/item/@last-modification-date").getTextContent(), XPathAPI.selectSingleNode(wovDocV1E5,
            "/version-history/@last-modification-date").getTextContent());

        // /version-history/version[version-number='1']/@timestamp ==
        // /item/properties/creation-date
        assertEquals("last-modification-date in root attribute of Item [" + objid
            + "] differs from creation date of version", XPathAPI.selectSingleNode(wovDocV1E5,
            "/version-history/version[version-number='1']/@timestamp").getTextContent(), XPathAPI.selectSingleNode(
            itemDocV1E5, "/item/properties/creation-date").getTextContent());

        // /version-history/version[version-number='1']/timestamp ==
        // /item/@last-modification-date
        assertEquals("last-modification-date in root attribute of Item [" + objid
            + "] differs from timestamp of version 1 " + "in version-history", XPathAPI.selectSingleNode(wovDocV1E5,
            "/version-history/version[version-number='1']/timestamp").getTextContent(), XPathAPI.selectSingleNode(
            itemDocV1E5, "/item/@last-modification-date").getTextContent());

        // release ----------
        // /version-history/version[version-number='1']/events/event[1]/eventType
        // ==
        // release
        assertEquals("eventType for release is not as expected", "released", XPathAPI.selectSingleNode(wovDocV1E5,
            "/version-history/version[version-number='1']" + "/events/event[1]/eventType").getTextContent());

        // /version-history/version[version-number='1']/events/event[1]/eventDateTime
        // ==
        // /item/@last-modification-date
        assertEquals("eventDateTime of the latest event of version 1 differs "
            + "from last-modification-date root attribute of Item [" + objid + "]", XPathAPI
            .selectSingleNode(wovDocV1E5,
                "/version-history/version[version-number='1']" + "/events/event[1]/eventDateTime").getTextContent(),
            XPathAPI.selectSingleNode(itemDocV1E5, "/item/@last-modification-date").getTextContent());

        // assign version pid ----
        // assign object pid -----
        // submit ----------------

        // create ----------------
        // /version-history/version[version-number='1']/events/event[5]/eventType
        // == 'create'
        assertEquals("eventType for create is not as expected for Item [" + objid + "]", "create",
            XPathAPI.selectSingleNode(wovDocV1E5,
                "/version-history/version[version-number='1']" + "/events/event[5]/eventType").getTextContent());

        assertEquals("eventType for create is not as expected for Item [" + objid + "]", "create",
            XPathAPI.selectSingleNode(wovDocV1E5,
                "/version-history/version[version-number='1']" + "/events/event[5]/eventType").getTextContent());

        /*
         * check ISSUE INFR-876
         */
        // /version-history/version[version-number='1']/events/event[1]/eventIdentifierValue
        // == '/ir/item/<objid>/resources/version-history#' +
        // /version-history/version[version-number='1']/events/event[1]/@xmlID
        assertEquals("eventIdentifierVaule " + " of Item [" + objid + "] differs from defined format", XPathAPI
            .selectSingleNode(
                wovDocV1E5,
                "/version-history/version[version-number='1']"
                    + "/events/event[1]/eventIdentifier/eventIdentifierValue").getTextContent(), "/ir/item/"
            + objid
            + "/resources/version-history#"
            + XPathAPI.selectSingleNode(wovDocV1E5,
                "/version-history/version[version-number='1']" + "/events/event[1]/@xmlID").getTextContent());

        /*
         * check ISSUE INFR-877 (eventIdentifierValue)
         */
        // /version-history/version[version-number='1']/events/event[1]/eventIdentifierValue
        // == '/ir/item/<objid>/resources/version-history#' +
        // /version-history/version[version-number='1']/events/event[1]/@xmlID
        assertEquals("eventIdentifierVaule " + " of Item [" + objid + "] differs from defined format", XPathAPI
            .selectSingleNode(
                wovDocV1E5,
                "/version-history/version[version-number='1']"
                    + "/events/event[1]/eventIdentifier/eventIdentifierValue").getTextContent(), "/ir/item/"
            + objid
            + "/resources/version-history#"
            + XPathAPI.selectSingleNode(wovDocV1E5,
                "/version-history/version[version-number='1']" + "/events/event[1]/@xmlID").getTextContent());

        // event 2
        assertEquals("eventIdentifierVaule " + " of Item [" + objid + "] differs from defined format", XPathAPI
            .selectSingleNode(
                wovDocV1E5,
                "/version-history/version[version-number='1']"
                    + "/events/event[2]/eventIdentifier/eventIdentifierValue").getTextContent(), "/ir/item/"
            + objid
            + "/resources/version-history#"
            + XPathAPI.selectSingleNode(wovDocV1E5,
                "/version-history/version[version-number='1']" + "/events/event[2]/@xmlID").getTextContent());

        // event 3
        assertEquals("eventIdentifierVaule " + " of Item [" + objid + "] differs from defined format", XPathAPI
            .selectSingleNode(
                wovDocV1E5,
                "/version-history/version[version-number='1']"
                    + "/events/event[3]/eventIdentifier/eventIdentifierValue").getTextContent(), "/ir/item/"
            + objid
            + "/resources/version-history#"
            + XPathAPI.selectSingleNode(wovDocV1E5,
                "/version-history/version[version-number='1']" + "/events/event[3]/@xmlID").getTextContent());

        // event 4
        assertEquals("eventIdentifierVaule " + " of Item [" + objid + "] differs from defined format", XPathAPI
            .selectSingleNode(
                wovDocV1E5,
                "/version-history/version[version-number='1']"
                    + "/events/event[4]/eventIdentifier/eventIdentifierValue").getTextContent(), "/ir/item/"
            + objid
            + "/resources/version-history#"
            + XPathAPI.selectSingleNode(wovDocV1E5,
                "/version-history/version[version-number='1']" + "/events/event[4]/@xmlID").getTextContent());

        // event 5
        assertEquals("eventIdentifierVaule " + " of Item [" + objid + "] differs from defined format", XPathAPI
            .selectSingleNode(
                wovDocV1E5,
                "/version-history/version[version-number='1']"
                    + "/events/event[5]/eventIdentifier/eventIdentifierValue").getTextContent(), "/ir/item/"
            + objid
            + "/resources/version-history#"
            + XPathAPI.selectSingleNode(wovDocV1E5,
                "/version-history/version[version-number='1']" + "/events/event[5]/@xmlID").getTextContent());
    }

    /**
     * Test timestamps of Item.
     * <p/>
     * Issue INFR-820
     * <p/>
     * Item changed in following way: <ul> <li>create</li> <li>submit</li> <li>assign object pid</li> <li>assign version
     * pid</li> <li>release</li> <li>update</li> <li>submit</li> <li>assign version pid</li> <li>release</li> </ul> The
     * datastructure is check only after update to version 2 to keep this test clearly arranged. There is an other test
     * which checks the data structure for a lifecycle where release of version 1 is the latest event.
     */
    @Test
    public void testItemTimestamps03() throws Exception {

        // version 1
        String itemXml = getExampleTemplate("item-minimal-for-create-01.xml");
        String itemV1E1 = create(itemXml);

        String objid = getObjidValue(itemV1E1);

        submit(objid, getTheLastModificationParam(false, objid, "submit",
            getLastModificationDateValue(getDocument(itemV1E1))));
        assignObjectPid(objid, getPidParam(objid, getFrameworkUrl() + "/ir/item/" + objid));
        String versionId = objid + ":1";
        assignVersionPid(versionId, getPidParam(versionId, getFrameworkUrl() + "/ir/item/" + versionId));
        release(objid, getTheLastModificationParam(false, objid));

        itemXml = retrieve(objid);

        // version 2
        String itemXmlV2E1 = update(objid, addComponent(itemXml));
        Document itemDocV2E1 = EscidocAbstractTest.getDocument(itemXmlV2E1);

        /*
         * check data structure
         */
        Document wovDocV2E1 = EscidocAbstractTest.getDocument(retrieveVersionHistory(objid));

        // check timestamp of version 1
        assertEquals("timestamp of version 1 of Item [" + objid
            + "] differs from timestamp of latest event of version 1 " + "in version-history", XPathAPI
            .selectSingleNode(wovDocV2E1, "/version-history/version[version-number='1']/timestamp").getTextContent(),
            XPathAPI.selectSingleNode(wovDocV2E1,
                "/version-history/version[version-number='1']" + "/events/event[1]/eventDateTime").getTextContent());

        submit(objid, getTheLastModificationParam(false, objid, "submit", getLastModificationDateValue(itemDocV2E1)));

        /*
         * check data structure
         */
        Document wovDocV2E2 = EscidocAbstractTest.getDocument(retrieveVersionHistory(objid));

        // check timestamp of version 1
        assertEquals(
            "timestamp of version 1 of Item [" + objid + "] differs from timestamp of latest event of version 1 "
                + "in version-history after submit of version 2", XPathAPI.selectSingleNode(wovDocV2E2,
                "/version-history/version[version-number='1']/timestamp").getTextContent(), XPathAPI
                .selectSingleNode(wovDocV2E2,
                    "/version-history/version[version-number='1']" + "/events/event[1]/eventDateTime").getTextContent());

        String versionId2 = objid + ":2";
        assignVersionPid(versionId2, getPidParam(versionId2, getFrameworkUrl() + "/ir/item/" + versionId2));

        /*
         * check data structure
         */
        Document wovDocV2E3 = EscidocAbstractTest.getDocument(retrieveVersionHistory(objid));

        // check timestamp of version 1
        assertEquals("timestamp of version 1 of Item [" + objid
            + "] differs from timestamp of latest event of version 1 "
            + "in version-history  after assign of version PID of version 2", XPathAPI.selectSingleNode(wovDocV2E3,
            "/version-history/version[version-number='1']/timestamp").getTextContent(), XPathAPI
            .selectSingleNode(wovDocV2E3,
                "/version-history/version[version-number='1']" + "/events/event[1]/eventDateTime").getTextContent());

        release(objid, getTheLastModificationParam(false, objid));

        /*
         * check data structure
         */
        Document wovDocV2E4 = EscidocAbstractTest.getDocument(retrieveVersionHistory(objid));

        // check timestamp of version 1
        assertEquals("timestamp of version 1 of Item [" + objid
            + "] differs from timestamp of latest event of version 1 "
            + "in version-history after release of version 2", XPathAPI.selectSingleNode(wovDocV2E4,
            "/version-history/version[version-number='1']/timestamp").getTextContent(), XPathAPI
            .selectSingleNode(wovDocV2E4,
                "/version-history/version[version-number='1']" + "/events/event[1]/eventDateTime").getTextContent());
    }

    /**
     * Depends on testLifecycleVersion02. Each Element, Attribute or Value is (should be) checked against the predefined
     * value tables. At least is checked if the values are in the TripleStore.
     * <p/>
     * Item is in version 1 (is also latest version). Item has no Components.
     *
     * @param objid        The objid of the Item.
     * @param componentIds &lt;version number, Vector&lt;objids of components&gt;&gt;
     * @param xml          The XML of the Item in version 1.
     * @throws Exception Thrown if a single value differs from the required.
     */
    private void validateItemVersion1(
        final String objid, final HashMap<String, Vector<String>> componentIds, final String xml) throws Exception {

        HashMap<String, String> expectedValues = new HashMap<String, String>();
        expectedValues.put("/item/properties/creation-date", null);
        expectedValues.put("/item/properties/created-by", null);
        expectedValues.put("/item/properties/public-status", "pending");
        expectedValues.put("/item/properties/public-status-comment", "Object created.");
        expectedValues.put("/item/properties/lock-status", "unlocked");

        expectedValues.put("/item/properties/version/number", "1");
        expectedValues.put("/item/properties/version/status", "pending");
        expectedValues.put("/item/properties/version/comment", "Object created.");
        expectedValues.put("/item/properties/latest-version/number", "1");

        expectedValues.put("/item/properties/content-model-specific/cms-prop/@test", "1");

        expectedValues.put("/item/md-records/md-record[@name = 'escidoc']", null);

        expectedValues.put("/item/@href", null);
        expectedValues.put("/item/properties/@title", "Properties");
        expectedValues.put("/item/properties/context/@href", "/ir/context/escidoc:ex1");
        expectedValues.put("/item/properties/context/@title", "Context Example 1");
        expectedValues.put("/item/properties/content-model/@href", "/cmm/content-model/escidoc:ex4");
        expectedValues.put("/item/properties/content-model/@title", "Generic Content Model");

        expectedValues.put("/item/properties/version/modified-by/@href",
            "/aa/user-account/escidoc:testsystemadministrator");
        expectedValues.put("/item/properties/version/modified-by/@title", "Test System Administrator User");

        expectedValues.put("/item/properties/latest-version/@href", "/ir/item/" + objid + ":1");
        expectedValues.put("/item/properties/latest-version/@title", "Latest Version");

        // -------------------
        Vector<String> forbiddenValues = new Vector<String>();
        forbiddenValues.add("/item/properties/context[2]");
        forbiddenValues.add("/item/properties/latest-release");
        forbiddenValues.add("/item/components/component");
        forbiddenValues.add("/item/@objid");
        validateResource(xml, expectedValues, forbiddenValues);

        /*
         * Validate TripleStore Value
         */
        TripleStoreValue tsv = new TripleStoreValue();
        tsv.compareValueWithTripleStore(objid, "Context Example 1", "/RDF/Description/context-title",
            "<http://escidoc.de/core/01/properties/context-title>");
        tsv.compareValueWithTripleStore(objid, "pending", "/RDF/Description/public-status",
            "<http://escidoc.de/core/01/properties/public-status>");
        tsv.compareValueWithTripleStore(objid, "Object created.", "/RDF/Description/public-status-comment",
            "<http://escidoc.de/core/01/properties/public-status-comment>");
        tsv.compareValueWithTripleStore(objid, "Generic Content Model", "/RDF/Description/content-model-title",
            "<http://escidoc.de/core/01/properties/content-model-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/modified-by-title",
            "<http://escidoc.de/core/01/properties/modified-by-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/created-by-title",
            "<http://escidoc.de/core/01/properties/created-by-title>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex4", "/RDF/Description/content-model/@resource",
            "<http://escidoc.de/core/01/structural-relations/content-model>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/created-by/@resource", "<http://escidoc.de/core/01/structural-relations/created-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/modified-by/@resource", "<http://escidoc.de/core/01/structural-relations/modified-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex1", "/RDF/Description/context/@resource",
            "<http://escidoc.de/core/01/structural-relations/context>");
        tsv.compareValueWithTripleStore(objid, "Object created.", "/RDF/Description/comment",
            "<http://escidoc.de/core/01/properties/version/comment>");
        tsv.compareValueWithTripleStore(objid, "pending", "/RDF/Description/status",
            "<http://escidoc.de/core/01/properties/version/status>");
        tsv.compareValueWithTripleStore(objid, "1", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/version/number>");

        // forbidden values (for version 1)
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/number",
            "<http://escidoc.de/core/01/latest-release/number>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/pid>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/version/pid>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/release/pid>");

        // number of triples for this object (to prevent missing tests)
        assertEquals("Wrong number of triples for objid='" + objid + "'", ITEM_TRIPLE_COUNT_MINUS6, tsv
            .countTriples(objid));
    }

    /**
     * Depends on testLifecycleVersion02. Each Element, Attribute or Value is (should be) checked against the predefined
     * value tables.
     * <p/>
     * Item is in version 1, status submitted, no Component
     *
     * @param objid The objid of the Item.
     * @param xml   The XML of the Item in version 1.
     * @throws Exception Thrown if a single value differes from the requied.
     */
    private void validateItemVersion1Submitted(final String objid, final String xml) throws Exception {

        HashMap<String, String> expectedValues = new HashMap<String, String>();
        expectedValues.put("/item/properties/creation-date", null);
        expectedValues.put("/item/properties/created-by", null);
        expectedValues.put("/item/properties/public-status", "submitted");
        expectedValues.put("/item/properties/public-status-comment", "Status changed to submitted for Item " + objid
            + ".");
        expectedValues.put("/item/properties/lock-status", "unlocked");

        expectedValues.put("/item/properties/version/number", "1");
        expectedValues.put("/item/properties/version/status", "submitted");
        expectedValues.put("/item/properties/version/comment", "Status changed to submitted for Item " + objid + ".");
        expectedValues.put("/item/properties/latest-version/number", "1");

        expectedValues.put("/item/properties/content-model-specific/cms-prop/@test", "1");

        expectedValues.put("/item/md-records/md-record[@name = 'escidoc']", null);

        expectedValues.put("/item/@href", null);
        expectedValues.put("/item/properties/@title", "Properties");
        expectedValues.put("/item/properties/context/@href", "/ir/context/escidoc:ex1");
        expectedValues.put("/item/properties/context/@title", "Context Example 1");
        expectedValues.put("/item/properties/content-model/@href", "/cmm/content-model/escidoc:ex4");
        expectedValues.put("/item/properties/content-model/@title", "Generic Content Model");

        expectedValues.put("/item/properties/version/modified-by/@href",
            "/aa/user-account/escidoc:testsystemadministrator");
        expectedValues.put("/item/properties/version/modified-by/@title", "Test System Administrator User");

        expectedValues.put("/item/properties/latest-version/@href", "/ir/item/" + objid + ":1");
        expectedValues.put("/item/properties/latest-version/@title", "Latest Version");

        // -------------------
        Vector<String> forbiddenValues = new Vector<String>();
        forbiddenValues.add("/item/properties/context[2]");
        forbiddenValues.add("/item/properties/latest-release");
        forbiddenValues.add("/item/components/component");
        forbiddenValues.add("/item/@objid");

        validateResource(xml, expectedValues, forbiddenValues);
    }

    /**
     * Depends on testLifecycleVersion02. Each Element, Attribute or Value is (should be) checked against the predefined
     * value tables.
     * <p/>
     * Item is in version 1, status submitted, no Component
     *
     * @param objid The objid of the Item.
     * @param xml   The XML of the Item in version 1.
     * @throws Exception Thrown if a single value differs from the required.
     */
    private void validateItemVersion1Released(final String objid, final String xml) throws Exception {

        // investigate the version special values
        HashMap<String, String> expectedValues = new HashMap<String, String>();
        expectedValues.put("/item/properties/creation-date", null);
        expectedValues.put("/item/properties/created-by", null);
        expectedValues.put("/item/properties/public-status", "released");
        expectedValues.put("/item/properties/public-status-comment", "Status changed to released for Item " + objid
            + ".");
        expectedValues.put("/item/properties/lock-status", "unlocked");

        expectedValues.put("/item/properties/version/number", "1");
        expectedValues.put("/item/properties/version/status", "released");
        expectedValues.put("/item/properties/version/comment", "Status changed to released for Item " + objid + ".");
        expectedValues.put("/item/properties/latest-version/number", "1");

        expectedValues.put("/item/properties/latest-release/number", "1");

        expectedValues.put("/item/properties/content-model-specific/cms-prop/@test", "1");

        expectedValues.put("/item/md-records/md-record[@name = 'escidoc']", null);

        expectedValues.put("/item/@href", null);
        expectedValues.put("/item/properties/@title", "Properties");
        expectedValues.put("/item/properties/context/@href", "/ir/context/escidoc:ex1");
        expectedValues.put("/item/properties/context/@title", "Context Example 1");
        expectedValues.put("/item/properties/content-model/@href", "/cmm/content-model/escidoc:ex4");
        expectedValues.put("/item/properties/content-model/@title", "Generic Content Model");

        expectedValues.put("/item/properties/version/modified-by/@href",
            "/aa/user-account/escidoc:testsystemadministrator");
        expectedValues.put("/item/properties/version/modified-by/@title", "Test System Administrator User");

        expectedValues.put("/item/properties/latest-version/@href", "/ir/item/" + objid + ":1");
        expectedValues.put("/item/properties/latest-version/@title", "Latest Version");

        expectedValues.put("/item/properties/latest-release/@href", "/ir/item/" + objid + ":1");
        expectedValues.put("/item/properties/latest-release/@title", "Latest public version");

        // -------------------
        Vector<String> forbiddenValues = new Vector<String>();
        forbiddenValues.add("/item/properties/context[2]");
        forbiddenValues.add("/item/components/component");
        forbiddenValues.add("/item/@objid");
        validateResource(xml, expectedValues, forbiddenValues);

        /*
         * Validate TripleStore Value
         */
        TripleStoreValue tsv = new TripleStoreValue();
        tsv.compareValueWithTripleStore(objid, "Context Example 1", "/RDF/Description/context-title",
            "<http://escidoc.de/core/01/properties/context-title>");
        tsv.compareValueWithTripleStore(objid, "released", "/RDF/Description/public-status",
            "<http://escidoc.de/core/01/properties/public-status>");
        tsv.compareValueWithTripleStore(objid, "Status changed to released for Item " + objid + ".",
            "/RDF/Description/public-status-comment", "<http://escidoc.de/core/01/properties/public-status-comment>");
        tsv.compareValueWithTripleStore(objid, "Generic Content Model", "/RDF/Description/content-model-title",
            "<http://escidoc.de/core/01/properties/content-model-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/modified-by-title",
            "<http://escidoc.de/core/01/properties/modified-by-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/created-by-title",
            "<http://escidoc.de/core/01/properties/created-by-title>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex4", "/RDF/Description/content-model/@resource",
            "<http://escidoc.de/core/01/structural-relations/content-model>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/created-by/@resource", "<http://escidoc.de/core/01/structural-relations/created-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/modified-by/@resource", "<http://escidoc.de/core/01/structural-relations/modified-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex1", "/RDF/Description/context/@resource",
            "<http://escidoc.de/core/01/structural-relations/context>");
        tsv.compareValueWithTripleStore(objid, "Status changed to released for Item " + objid + ".",
            "/RDF/Description/comment", "<http://escidoc.de/core/01/properties/version/comment>");
        tsv.compareValueWithTripleStore(objid, "released", "/RDF/Description/status",
            "<http://escidoc.de/core/01/properties/version/status>");
        tsv.compareValueWithTripleStore(objid, "1", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/version/number>");
        tsv.compareValueWithTripleStore(objid, "1", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/release/number>");

        // forbidden values (for this version)

        // number of Triples for this object (to prevent a missing test)
        assertEquals("Wrong number of triples for objid='" + objid + "'", ITEM_TRIPLE_COUNT_MINUS1, tsv
            .countTriples(objid));
    }

    /**
     * Depends on testLifecycleVersion02. Each Element, Attribute or Value is (should be) checked against the predefined
     * value tables.
     *
     * @param objid        The objid of the Item.
     * @param componentIds &lt;version number, Vector&lt;objids of components&gt;&gt;
     * @param xml          The XML of the Item in version 2.
     * @throws Exception Thrown if a single value differs from the required.
     */
    private void validateItemVersion2(
        final String objid, final HashMap<String, Vector<String>> componentIds, final String xml) throws Exception {

        // investigate the version special values
        HashMap<String, String> expectedValues = new HashMap<String, String>();
        expectedValues.put("/item/properties/creation-date", null);
        expectedValues.put("/item/properties/created-by", null);
        expectedValues.put("/item/properties/public-status", "released");
        expectedValues.put("/item/properties/public-status-comment", "Status changed to released for Item " + objid
            + ".");
        expectedValues.put("/item/properties/lock-status", "unlocked");

        expectedValues.put("/item/properties/version/number", "2");
        expectedValues.put("/item/properties/version/status", "pending");
        expectedValues.put("/item/properties/version/comment", "ItemHandler.update()");
        expectedValues.put("/item/properties/latest-version/number", "2");
        expectedValues.put("/item/properties/latest-release/number", "1");
        expectedValues.put("/item/properties/content-model-specific/cms-prop/@test", "1");

        expectedValues.put("/item/md-records/md-record[@name = 'escidoc']", null);

        // Component
        expectedValues.put("/item/components/component", null);
        expectedValues.put("/item/components/component[1]/md-records/md-record", null);

        expectedValues.put("/item/@href", null);
        expectedValues.put("/item/properties/@title", "Properties");
        expectedValues.put("/item/properties/context/@href", "/ir/context/escidoc:ex1");
        expectedValues.put("/item/properties/context/@title", "Context Example 1");
        expectedValues.put("/item/properties/content-model/@href", "/cmm/content-model/escidoc:ex4");
        expectedValues.put("/item/properties/content-model/@title", "Generic Content Model");

        expectedValues.put("/item/properties/version/modified-by/@href",
            "/aa/user-account/escidoc:testsystemadministrator");
        expectedValues.put("/item/properties/version/modified-by/@title", "Test System Administrator User");

        expectedValues.put("/item/properties/latest-version/@href", "/ir/item/" + objid + ":2");
        expectedValues.put("/item/properties/latest-version/@title", "Latest Version");

        expectedValues.put("/item/properties/latest-release/@href", "/ir/item/" + objid + ":1");
        expectedValues.put("/item/properties/latest-release/@title", "Latest public version");

        // Component
        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + "/components/component/"
            + componentIds.get("2").get(0) + "']/properties/created-by/@href",
            "/aa/user-account/escidoc:testsystemadministrator");

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + "/components/component/"
            + componentIds.get("2").get(0) + "']/properties/created-by/@title", "Test System Administrator User");

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + "/components/component/"
            + componentIds.get("2").get(0) + "']/properties/valid-status", "valid");

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + "/components/component/"
            + componentIds.get("2").get(0) + "']/properties/visibility", "public");

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + "/components/component/"
            + componentIds.get("2").get(0) + "']/properties/content-category", "pre-print");

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + "/components/component/"
            + componentIds.get("2").get(0) + "']/content/@title", "Component " + componentIds.get("2").get(0));

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + "/components/component/"
            + componentIds.get("2").get(0) + "']/content/@href", "/ir/item/" + objid + "/components/component/"
            + componentIds.get("2").get(0) + "/content");

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + "/components/component/"
            + componentIds.get("2").get(0) + "']/content/@storage", "internal-managed");

        // -------------------
        Vector<String> forbiddenValues = new Vector<String>();
        forbiddenValues.add("/item/properties/context[2]");
        forbiddenValues.add("/item/components/component[2]");
        forbiddenValues.add("/item/@objid");
        validateResource(xml, expectedValues, forbiddenValues);
    }

    /**
     * Depends on testLifecycleVersion02. Each Element, Attribute or Value is (should be) checked against the predefined
     * value tables.
     *
     * @param objid        The objid of the Item.
     * @param componentIds &lt;version number, Vector&lt;objids of components&gt;&gt;
     * @param xml          The XML of the Item in version 2.
     * @throws Exception Thrown if a single value differs from the required.
     */
    private void validateItemVersion2AsAnonymous(
        final String objid, final HashMap<String, Vector<String>> componentIds, final String xml) throws Exception {

        HashMap<String, String> expectedValues = new HashMap<String, String>();
        expectedValues.put("/item/properties/creation-date", null);
        expectedValues.put("/item/properties/created-by", null);
        expectedValues.put("/item/properties/public-status", "released");
        expectedValues.put("/item/properties/public-status-comment", "Status changed to released for Item " + objid
            + ".");
        expectedValues.put("/item/properties/lock-status", "unlocked");

        expectedValues.put("/item/properties/version/number", "1");
        expectedValues.put("/item/properties/version/status", "released");
        expectedValues.put("/item/properties/version/comment", "Status changed to released for Item " + objid + ".");
        expectedValues.put("/item/properties/latest-version/number", "2");

        expectedValues.put("/item/properties/latest-release/number", "1");

        expectedValues.put("/item/properties/content-model-specific/cms-prop/@test", "1");

        expectedValues.put("/item/md-records/md-record[@name = 'escidoc']", null);

        expectedValues.put("/item/@href", null);
        expectedValues.put("/item/properties/@title", "Properties");
        expectedValues.put("/item/properties/context/@href", "/ir/context/escidoc:ex1");
        expectedValues.put("/item/properties/context/@title", "Context Example 1");
        expectedValues.put("/item/properties/content-model/@href", "/cmm/content-model/escidoc:ex4");
        expectedValues.put("/item/properties/content-model/@title", "Generic Content Model");

        expectedValues.put("/item/properties/version/modified-by/@href",
            "/aa/user-account/escidoc:testsystemadministrator");
        expectedValues.put("/item/properties/version/modified-by/@title", "Test System Administrator User");

        expectedValues.put("/item/properties/latest-version/@href", "/ir/item/" + objid + ":2");
        expectedValues.put("/item/properties/latest-version/@title", "Latest Version");

        expectedValues.put("/item/properties/latest-release/@href", "/ir/item/" + objid + ":1");
        expectedValues.put("/item/properties/latest-release/@title", "Latest public version");

        // -------------------
        Vector<String> forbiddenValues = new Vector<String>();
        forbiddenValues.add("/item/properties/context[2]");
        forbiddenValues.add("/item/components/component");
        forbiddenValues.add("/item/@objid");
        validateResource(xml, expectedValues, forbiddenValues);
    }

    /**
     * Depends on testLifecycleVersion02. Each Element, Attribute or Value is (should be) checked against the predefined
     * value tables.
     *
     * @param objid        The objid of the Item.
     * @param componentIds &lt;version number, Vector&lt;objids of components&gt;&gt;
     * @param xml          The XML of the Item in version 2.
     * @throws Exception Thrown if a single value differs from the required.
     */
    private void validateItemVersion2Released(
        final String objid, final HashMap<String, Vector<String>> componentIds, final String xml) throws Exception {

        HashMap<String, String> expectedValues = new HashMap<String, String>();
        expectedValues.put("/item/properties/creation-date", null);
        expectedValues.put("/item/properties/created-by", null);
        expectedValues.put("/item/properties/public-status", "released");
        expectedValues.put("/item/properties/public-status-comment", "Status changed to released for Item " + objid
            + ".");
        expectedValues.put("/item/properties/lock-status", "unlocked");

        expectedValues.put("/item/properties/version/number", "2");
        expectedValues.put("/item/properties/version/status", "released");
        expectedValues.put("/item/properties/version/comment", "Status changed to released for Item " + objid + ".");
        expectedValues.put("/item/properties/latest-version/number", "2");

        expectedValues.put("/item/properties/latest-release/number", "2");

        expectedValues.put("/item/properties/content-model-specific/cms-prop/@test", "1");

        expectedValues.put("/item/md-records/md-record[@name = 'escidoc']", null);
        expectedValues.put("/item/@href", null);
        expectedValues.put("/item/properties/@title", "Properties");
        expectedValues.put("/item/properties/context/@href", "/ir/context/escidoc:ex1");
        expectedValues.put("/item/properties/context/@title", "Context Example 1");
        expectedValues.put("/item/properties/content-model/@href", "/cmm/content-model/escidoc:ex4");
        expectedValues.put("/item/properties/content-model/@title", "Generic Content Model");

        expectedValues.put("/item/properties/version/modified-by/@href",
            "/aa/user-account/escidoc:testsystemadministrator");
        expectedValues.put("/item/properties/version/modified-by/@title", "Test System Administrator User");

        expectedValues.put("/item/properties/latest-version/@href", "/ir/item/" + objid + ":2");
        expectedValues.put("/item/properties/latest-version/@title", "Latest Version");

        expectedValues.put("/item/properties/latest-release/@href", "/ir/item/" + objid + ":2");
        expectedValues.put("/item/properties/latest-release/@title", "Latest public version");

        // Component
        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + "/components/component/"
            + componentIds.get("2").get(0) + "']/properties/created-by/@href",
            "/aa/user-account/escidoc:testsystemadministrator");

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + "/components/component/"
            + componentIds.get("2").get(0) + "']/properties/created-by/@title", "Test System Administrator User");

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + "/components/component/"
            + componentIds.get("2").get(0) + "']/properties/valid-status", "valid");

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + "/components/component/"
            + componentIds.get("2").get(0) + "']/properties/visibility", "public");

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + "/components/component/"
            + componentIds.get("2").get(0) + "']/properties/content-category", "pre-print");

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + "/components/component/"
            + componentIds.get("2").get(0) + "']/content/@title", "Component " + componentIds.get("2").get(0));

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + "/components/component/"
            + componentIds.get("2").get(0) + "']/content/@href", "/ir/item/" + objid + "/components/component/"
            + componentIds.get("2").get(0) + "/content");

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + "/components/component/"
            + componentIds.get("2").get(0) + "']/content/@storage", "internal-managed");

        // -------------------
        Vector<String> forbiddenValues = new Vector<String>();
        forbiddenValues.add("/item/properties/context[2]");
        forbiddenValues.add("/item/@objid");
        validateResource(xml, expectedValues, forbiddenValues);
    }

    /**
     * Depends on testLifecycleVersion02. Each Element, Attribute or Value is (should be) checked against the predefined
     * value tables.
     *
     * @param objid          The objid of the Item.
     * @param componentIds   &lt;version number, Vector&lt;objids of components&gt;&gt;
     * @param xml            The XML of the Item in version 2.
     * @param requiredValues Map of required values &lt;Xpath, value&gt;
     * @throws Exception Thrown if a single value differs from the required.
     */
    private void validateItemVersion3(
        final String objid, final HashMap<String, Vector<String>> componentIds, final String xml,
        final HashMap<String, String> requiredValues) throws Exception {

        HashMap<String, String> expectedValues = new HashMap<String, String>();
        if (requiredValues != null) {
            expectedValues.putAll(requiredValues);
        }
        expectedValues.put("/item/properties/creation-date", null);
        expectedValues.put("/item/properties/created-by", null);
        expectedValues.put("/item/properties/public-status", "released");
        expectedValues.put("/item/properties/public-status-comment", "Status changed to released for Item " + objid
            + ".");
        expectedValues.put("/item/properties/lock-status", "unlocked");

        expectedValues.put("/item/properties/version/number", "3");
        expectedValues.put("/item/properties/version/status", "pending");
        expectedValues.put("/item/properties/version/comment", "ItemHandler.update()");
        expectedValues.put("/item/properties/latest-version/number", "3");

        expectedValues.put("/item/properties/latest-release/number", "2");

        expectedValues.put("/item/properties/content-model-specific/cms-prop/@test", "1");

        expectedValues.put("/item/md-records/md-record[@name = 'escidoc']", null);

        // Component
        expectedValues.put("/item/components/component", null);
        expectedValues.put("/item/components/component/properties/" + "content-category[text() ='pre-print']", null);

        expectedValues.put("/item/components/component[1]/md-records/md-record", null);
        expectedValues.put("/item/@href", null);
        expectedValues.put("/item/properties/@title", "Properties");
        expectedValues.put("/item/properties/context/@href", "/ir/context/escidoc:ex1");
        expectedValues.put("/item/properties/context/@title", "Context Example 1");
        expectedValues.put("/item/properties/content-model/@href", "/cmm/content-model/escidoc:ex4");
        expectedValues.put("/item/properties/content-model/@title", "Generic Content Model");

        expectedValues.put("/item/properties/version/modified-by/@href",
            "/aa/user-account/escidoc:testsystemadministrator");
        expectedValues.put("/item/properties/version/modified-by/@title", "Test System Administrator User");

        expectedValues.put("/item/properties/latest-version/@href", "/ir/item/" + objid + ":3");
        expectedValues.put("/item/properties/latest-version/@title", "Latest Version");

        expectedValues.put("/item/properties/latest-release/@href", "/ir/item/" + objid + ":2");
        expectedValues.put("/item/properties/latest-release/@title", "Latest public version");

        // Component
        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + "/components/component/"
            + componentIds.get("3").get(0) + "']/properties/created-by/@href",
            "/aa/user-account/escidoc:testsystemadministrator");

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + "/components/component/"
            + componentIds.get("3").get(0) + "']/properties/created-by/@title", "Test System Administrator User");

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + "/components/component/"
            + componentIds.get("3").get(0) + "']/properties/valid-status", "valid");

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + "/components/component/"
            + componentIds.get("3").get(0) + "']/properties/visibility", "public");

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + "/components/component/"
            + componentIds.get("3").get(0) + "']/content/@title", "Component " + componentIds.get("3").get(0));

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + "/components/component/"
            + componentIds.get("3").get(0) + "']/content/@href", "/ir/item/" + objid + "/components/component/"
            + componentIds.get("3").get(0) + "/content");

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + "/components/component/"
            + componentIds.get("3").get(0) + "']/content/@storage", "internal-managed");

        // -------------------
        Vector<String> forbiddenValues = new Vector<String>();
        forbiddenValues.add("/item/properties/context[2]");
        forbiddenValues.add("/item/components/component[3]");
        forbiddenValues.add("/item/@objid");
        validateResource(xml, expectedValues, forbiddenValues);
    }

    /**
     * Depends on testLifecycleVersion02. Each Element, Attribute or Value is (should be) checked against the predefined
     * value tables.
     * <p/>
     * Item in version 3 means it has public-status=released, version-status=pending, 2 Components, Version 1 and 2 are
     * released, etc.
     *
     * @param objid        The objid of the Item.
     * @param componentIds &lt;version number, Vector&lt;objids of components&gt;&gt;
     * @param xml          The XML of the Item in version 2.
     * @throws Exception Thrown if a single value differs from the required.
     */
    private void validateItemVersion3AsAnonymous(
        final String objid, final HashMap<String, Vector<String>> componentIds, final String xml) throws Exception {

        HashMap<String, String> expectedValues = new HashMap<String, String>();
        expectedValues.put("/item/properties/creation-date", null);
        expectedValues.put("/item/properties/created-by", null);
        expectedValues.put("/item/properties/public-status", "released");
        expectedValues.put("/item/properties/public-status-comment", "Status changed to released for Item " + objid
            + ".");
        expectedValues.put("/item/properties/lock-status", "unlocked");

        expectedValues.put("/item/properties/version/number", "2");
        expectedValues.put("/item/properties/version/status", "released");
        expectedValues.put("/item/properties/version/comment", "Status changed to released for Item " + objid + ".");
        expectedValues.put("/item/properties/latest-version/number", "3");
        expectedValues.put("/item/properties/latest-release/number", "2");

        expectedValues.put("/item/properties/content-model-specific/cms-prop/@test", "1");

        expectedValues.put("/item/md-records/md-record[@name = 'escidoc']", null);
        expectedValues.put("/item/@href", null);
        expectedValues.put("/item/properties/@title", "Properties");
        expectedValues.put("/item/properties/context/@href", "/ir/context/escidoc:ex1");
        expectedValues.put("/item/properties/context/@title", "Context Example 1");
        expectedValues.put("/item/properties/content-model/@href", "/cmm/content-model/escidoc:ex4");
        expectedValues.put("/item/properties/content-model/@title", "Generic Content Model");

        expectedValues.put("/item/properties/version/modified-by/@href",
            "/aa/user-account/escidoc:testsystemadministrator");
        expectedValues.put("/item/properties/version/modified-by/@title", "Test System Administrator User");

        expectedValues.put("/item/properties/latest-version/@href", "/ir/item/" + objid + ":3");
        expectedValues.put("/item/properties/latest-version/@title", "Latest Version");

        expectedValues.put("/item/properties/latest-release/@href", "/ir/item/" + objid + ":2");
        expectedValues.put("/item/properties/latest-release/@title", "Latest public version");

        // Component
        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + ":2" + "/components/component/"
            + componentIds.get("2").get(0) + "']/properties/created-by/@href",
            "/aa/user-account/escidoc:testsystemadministrator");

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + ":2" + "/components/component/"
            + componentIds.get("2").get(0) + "']/properties/created-by/@title", "Test System Administrator User");

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + ":2" + "/components/component/"
            + componentIds.get("2").get(0) + "']/properties/valid-status", "valid");

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + ":2" + "/components/component/"
            + componentIds.get("2").get(0) + "']/properties/visibility", "public");

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + ":2" + "/components/component/"
            + componentIds.get("2").get(0) + "']/properties/content-category", "pre-print");

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + ":2" + "/components/component/"
            + componentIds.get("2").get(0) + "']/content/@title", "Component " + componentIds.get("2").get(0));

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + ":2" + "/components/component/"
            + componentIds.get("2").get(0) + "']/content/@href", "/ir/item/" + objid + ":2" + "/components/component/"
            + componentIds.get("2").get(0) + "/content");

        expectedValues.put("/item/components/component[@href='/ir/item/" + objid + ":2" + "/components/component/"
            + componentIds.get("2").get(0) + "']/content/@storage", "internal-managed");

        // -------------------
        Vector<String> forbiddenValues = new Vector<String>();
        forbiddenValues.add("/item/properties/context[2]");
        forbiddenValues.add("/item/components/component[3]");
        forbiddenValues.add("/item/@objid");
        validateResource(xml, expectedValues, forbiddenValues);
    }

    /**
     * Depends on testLifecycleVersion02. Each Element, Attribute or Value is (should be) checked against the predefined
     * value tables.
     * <p/>
     * Item <br/> version 1: released, no Components version 2: released, one Component version 3: pending
     *
     * @param objid The objid of the Item.
     * @param xml   The XML of the Item in version 1.
     * @throws Exception Thrown if a single value differs from the required.
     */
    private void validateItemVersion1ReleasedWhileVersion2Released(final String objid, final String xml)
        throws Exception {

        // investigate the version special values
        HashMap<String, String> expectedValues = new HashMap<String, String>();
        expectedValues.put("/item/properties/creation-date", null);
        expectedValues.put("/item/properties/created-by", null);
        expectedValues.put("/item/properties/public-status", "released");
        expectedValues.put("/item/properties/public-status-comment", "Status changed to released for Item " + objid
            + ".");
        expectedValues.put("/item/properties/lock-status", "unlocked");

        expectedValues.put("/item/properties/version/number", "1");
        expectedValues.put("/item/properties/version/status", "released");
        expectedValues.put("/item/properties/version/comment", "Status changed to released for Item " + objid + ".");
        expectedValues.put("/item/properties/latest-version/number", "2");

        expectedValues.put("/item/properties/latest-release/number", "2");

        expectedValues.put("/item/properties/content-model-specific/cms-prop/@test", "1");

        expectedValues.put("/item/md-records/md-record[@name = 'escidoc']", null);
        expectedValues.put("/item/@href", null);
        expectedValues.put("/item/properties/@title", "Properties");
        expectedValues.put("/item/properties/context/@href", "/ir/context/escidoc:ex1");
        expectedValues.put("/item/properties/context/@title", "Context Example 1");
        expectedValues.put("/item/properties/content-model/@href", "/cmm/content-model/escidoc:ex4");
        expectedValues.put("/item/properties/content-model/@title", "Generic Content Model");

        expectedValues.put("/item/properties/version/modified-by/@href",
            "/aa/user-account/escidoc:testsystemadministrator");
        expectedValues.put("/item/properties/version/modified-by/@title", "Test System Administrator User");

        expectedValues.put("/item/properties/latest-version/@href", "/ir/item/" + objid + ":2");
        expectedValues.put("/item/properties/latest-version/@title", "Latest Version");

        expectedValues.put("/item/properties/latest-release/@href", "/ir/item/" + objid + ":2");
        expectedValues.put("/item/properties/latest-release/@title", "Latest public version");

        // -------------------
        Vector<String> forbiddenValues = new Vector<String>();
        forbiddenValues.add("/item/properties/context[2]");
        forbiddenValues.add("/item/components/component");
        forbiddenValues.add("/item/@objid");
        validateResource(xml, expectedValues, forbiddenValues);

        /*
         * Validate TripleStore Value
         */
        // Keep in mind that these are the Triples for version 3!
        TripleStoreValue tsv = new TripleStoreValue();
        tsv.compareValueWithTripleStore(objid, "Context Example 1", "/RDF/Description/context-title",
            "<http://escidoc.de/core/01/properties/context-title>");
        tsv.compareValueWithTripleStore(objid, "released", "/RDF/Description/public-status",
            "<http://escidoc.de/core/01/properties/public-status>");
        tsv.compareValueWithTripleStore(objid, "Status changed to released for Item " + objid + ".",
            "/RDF/Description/public-status-comment", "<http://escidoc.de/core/01/properties/public-status-comment>");
        tsv.compareValueWithTripleStore(objid, "Generic Content Model", "/RDF/Description/content-model-title",
            "<http://escidoc.de/core/01/properties/content-model-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/modified-by-title",
            "<http://escidoc.de/core/01/properties/modified-by-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/created-by-title",
            "<http://escidoc.de/core/01/properties/created-by-title>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex4", "/RDF/Description/content-model/@resource",
            "<http://escidoc.de/core/01/structural-relations/content-model>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/created-by/@resource", "<http://escidoc.de/core/01/structural-relations/created-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/modified-by/@resource", "<http://escidoc.de/core/01/structural-relations/modified-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex1", "/RDF/Description/context/@resource",
            "<http://escidoc.de/core/01/structural-relations/context>");
        tsv.compareValueWithTripleStore(objid, "Status changed to released for Item " + objid + ".",
            "/RDF/Description/comment", "<http://escidoc.de/core/01/properties/version/comment>");
        tsv.compareValueWithTripleStore(objid, "released", "/RDF/Description/status",
            "<http://escidoc.de/core/01/properties/version/status>");
        tsv.compareValueWithTripleStore(objid, "2", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/version/number>");
        tsv.compareValueWithTripleStore(objid, "2", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/release/number>");

        // number of Triples for this object (to prevent a missing test)
        assertEquals("Wrong number of triples for objid='" + objid + "'", ITEM_TRIPLE_COUNT, tsv.countTriples(objid));
    }

    /**
     * Depends on testLifecycleVersion02. Each Element, Attribute or Value is (should be) checked against the predefined
     * value tables.
     * <p/>
     * Item <br/> version 1: released, no Components version 2: released, one Component version 3: pending
     *
     * @param objid The objid of the Item.
     * @param xml   The XML of the Item in version 1.
     * @throws Exception Thrown if a single value differs from the required.
     */
    private void validateItemVersion1ReleasedWhileVersion2ReleasedVersion3pending(final String objid, final String xml)
        throws Exception {

        // investigate the version special values
        HashMap<String, String> expectedValues = new HashMap<String, String>();
        expectedValues.put("/item/properties/creation-date", null);
        expectedValues.put("/item/properties/created-by", null);
        expectedValues.put("/item/properties/public-status", "released");
        expectedValues.put("/item/properties/public-status-comment", "Status changed to released for Item " + objid
            + ".");
        expectedValues.put("/item/properties/lock-status", "unlocked");

        expectedValues.put("/item/properties/version/number", "1");
        expectedValues.put("/item/properties/version/status", "released");
        expectedValues.put("/item/properties/version/comment", "Status changed to released for Item " + objid + ".");
        expectedValues.put("/item/properties/latest-version/number", "3");

        expectedValues.put("/item/properties/latest-release/number", "2");

        expectedValues.put("/item/properties/content-model-specific/cms-prop/@test", "1");

        expectedValues.put("/item/md-records/md-record[@name = 'escidoc']", null);
        expectedValues.put("/item/@href", null);
        expectedValues.put("/item/properties/@title", "Properties");
        expectedValues.put("/item/properties/context/@href", "/ir/context/escidoc:ex1");
        expectedValues.put("/item/properties/context/@title", "Context Example 1");
        expectedValues.put("/item/properties/content-model/@href", "/cmm/content-model/escidoc:ex4");
        expectedValues.put("/item/properties/content-model/@title", "Generic Content Model");

        expectedValues.put("/item/properties/version/modified-by/@href",
            "/aa/user-account/escidoc:testsystemadministrator");
        expectedValues.put("/item/properties/version/modified-by/@title", "Test System Administrator User");

        expectedValues.put("/item/properties/latest-version/@href", "/ir/item/" + objid + ":3");
        expectedValues.put("/item/properties/latest-version/@title", "Latest Version");

        expectedValues.put("/item/properties/latest-release/@href", "/ir/item/" + objid + ":2");
        expectedValues.put("/item/properties/latest-release/@title", "Latest public version");

        // -------------------
        Vector<String> forbiddenValues = new Vector<String>();
        forbiddenValues.add("/item/properties/context[2]");
        forbiddenValues.add("/item/components/component");
        forbiddenValues.add("/item/@objid");
        validateResource(xml, expectedValues, forbiddenValues);

        /*
         * Validate TripleStore Value
         */
        // Keep in mind that these are the Triples for version 3!
        TripleStoreValue tsv = new TripleStoreValue();
        tsv.compareValueWithTripleStore(objid, "Context Example 1", "/RDF/Description/context-title",
            "<http://escidoc.de/core/01/properties/context-title>");
        tsv.compareValueWithTripleStore(objid, "released", "/RDF/Description/public-status",
            "<http://escidoc.de/core/01/properties/public-status>");
        tsv.compareValueWithTripleStore(objid, "Status changed to released for Item " + objid + ".",
            "/RDF/Description/public-status-comment", "<http://escidoc.de/core/01/properties/public-status-comment>");
        tsv.compareValueWithTripleStore(objid, "Generic Content Model", "/RDF/Description/content-model-title",
            "<http://escidoc.de/core/01/properties/content-model-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/modified-by-title",
            "<http://escidoc.de/core/01/properties/modified-by-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/created-by-title",
            "<http://escidoc.de/core/01/properties/created-by-title>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex4", "/RDF/Description/content-model/@resource",
            "<http://escidoc.de/core/01/structural-relations/content-model>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/created-by/@resource", "<http://escidoc.de/core/01/structural-relations/created-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/modified-by/@resource", "<http://escidoc.de/core/01/structural-relations/modified-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex1", "/RDF/Description/context/@resource",
            "<http://escidoc.de/core/01/structural-relations/context>");
        tsv.compareValueWithTripleStore(objid, "ItemHandler.update()", "/RDF/Description/comment",
            "<http://escidoc.de/core/01/properties/version/comment>");
        tsv.compareValueWithTripleStore(objid, "pending", "/RDF/Description/status",
            "<http://escidoc.de/core/01/properties/version/status>");
        tsv.compareValueWithTripleStore(objid, "3", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/version/number>");
        tsv.compareValueWithTripleStore(objid, "2", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/release/number>");

        // number of Triples for this object (to prevent a missing test)
        assertEquals("Wrong number of triples for objid='" + objid + "'", ITEM_TRIPLE_COUNT, tsv.countTriples(objid));
    }

    /**
     * Validates an Item where version 1 is in status released and version 2 is updated by adding a Component and in
     * status pending. No further versions exists.
     *
     * @param objid        The objid of the Item.
     * @param componentIds The map with component IDs &lt;version number, Vector&lt;componentIds&gt;&gt;.
     * @param xml          XML of Item.
     * @throws Exception Thrown if XML or TripleStore not compares to the required values.
     */
    private void validateItemVersion1T03(
        final String objid, final HashMap<String, Vector<String>> componentIds, final String xml) throws Exception {

        /*
         * Item in version 1.
         * 
         * Properties
         */
        HashMap<String, String> expectedValues = new HashMap<String, String>();
        expectedValues.put("/item/properties/creation-date", null);
        expectedValues.put("/item/properties/created-by", null);
        expectedValues.put("/item/properties/public-status", "released");
        expectedValues.put("/item/properties/public-status-comment", "Status changed to released for Item " + objid
            + ".");
        expectedValues.put("/item/properties/lock-status", "unlocked");

        expectedValues.put("/item/properties/version/number", "1");
        expectedValues.put("/item/properties/version/status", "released");
        expectedValues.put("/item/properties/version/comment", "Status changed to released for Item " + objid + ".");
        expectedValues.put("/item/properties/latest-version/number", "2");

        expectedValues.put("/item/properties/content-model-specific/cms-prop/@test", "1");

        expectedValues.put("/item/md-records/md-record[@name = 'escidoc']", null);
        expectedValues.put("/item/@href", null);
        expectedValues.put("/item/properties/@title", "Properties");
        expectedValues.put("/item/properties/context/@href", "/ir/context/escidoc:ex1");
        expectedValues.put("/item/properties/context/@title", "Context Example 1");
        expectedValues.put("/item/properties/content-model/@href", "/cmm/content-model/escidoc:ex4");
        expectedValues.put("/item/properties/content-model/@title", "Generic Content Model");

        expectedValues.put("/item/properties/version/modified-by/@href",
            "/aa/user-account/escidoc:testsystemadministrator");
        expectedValues.put("/item/properties/version/modified-by/@title", "Test System Administrator User");

        expectedValues.put("/item/properties/latest-version/@href", "/ir/item/" + objid + ":2");
        expectedValues.put("/item/properties/latest-version/@title", "Latest Version");

        expectedValues.put("/item/properties/latest-release/@href", "/ir/item/" + objid + ":1");
        expectedValues.put("/item/properties/latest-release/@title", "Latest public version");

        // -------------------
        Vector<String> forbiddenValues = new Vector<String>();
        forbiddenValues.add("/item/properties/context[2]");
        forbiddenValues.add("/item/components/component");
        forbiddenValues.add("/item/@objid");
        validateResource(xml, expectedValues, forbiddenValues);

        /*
         * Validate TripleStore Value
         */
        TripleStoreValue tsv = new TripleStoreValue();
        tsv.compareValueWithTripleStore(objid, "Context Example 1", "/RDF/Description/context-title",
            "<http://escidoc.de/core/01/properties/context-title>");
        tsv.compareValueWithTripleStore(objid, "released", "/RDF/Description/public-status",
            "<http://escidoc.de/core/01/properties/public-status>");
        tsv.compareValueWithTripleStore(objid, "Status changed to released for Item " + objid + ".",
            "/RDF/Description/public-status-comment", "<http://escidoc.de/core/01/properties/public-status-comment>");
        tsv.compareValueWithTripleStore(objid, "Generic Content Model", "/RDF/Description/content-model-title",
            "<http://escidoc.de/core/01/properties/content-model-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/modified-by-title",
            "<http://escidoc.de/core/01/properties/modified-by-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/created-by-title",
            "<http://escidoc.de/core/01/properties/created-by-title>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex4", "/RDF/Description/content-model/@resource",
            "<http://escidoc.de/core/01/structural-relations/content-model>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/created-by/@resource", "<http://escidoc.de/core/01/structural-relations/created-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/modified-by/@resource", "<http://escidoc.de/core/01/structural-relations/modified-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex1", "/RDF/Description/context/@resource",
            "<http://escidoc.de/core/01/structural-relations/context>");
        tsv.compareValueWithTripleStore(objid, "ItemHandler.update()", "/RDF/Description/comment",
            "<http://escidoc.de/core/01/properties/version/comment>");
        tsv.compareValueWithTripleStore(objid, "pending", "/RDF/Description/status",
            "<http://escidoc.de/core/01/properties/version/status>");
        tsv.compareValueWithTripleStore(objid, "2", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/version/number>");

        tsv.compareValueWithTripleStore(objid, "1", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/release/number>");

        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/pid>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/version/pid>");
        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid + ":1", "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/release/pid>");

        // number of triples for this object (to prevent missing tests)
        assertEquals("Wrong number of triples for objid='" + objid + "'", ITEM_TRIPLE_COUNT_MINUS1, tsv
            .countTriples(objid));
    }

    /**
     * Validates an Item where version 1 is in status released and version 2 is updated by adding a Component and in
     * status pending. No further versions exists.
     *
     * @param objid        The objid of hte Item.
     * @param componentIds The map with component Ids &lt;version number, Vector&lt;componentIds&gt;&gt;.
     * @param xml          XML of Item.
     * @throws Exception Thrown if XML or TripleStore not compares to the required values.
     */
    private void validateItemVersion2T03(
        final String objid, final HashMap<String, Vector<String>> componentIds, final String xml) throws Exception {

        /*
         * Item in version 2.
         */
        HashMap<String, String> expectedValues = new HashMap<String, String>();
        expectedValues.put("/item/properties/creation-date", null);
        expectedValues.put("/item/properties/created-by", null);
        expectedValues.put("/item/properties/public-status", "released");
        expectedValues.put("/item/properties/public-status-comment", "Status changed to released for Item " + objid
            + ".");
        expectedValues.put("/item/properties/lock-status", "unlocked");

        expectedValues.put("/item/properties/version/number", "2");
        expectedValues.put("/item/properties/version/status", "pending");
        expectedValues.put("/item/properties/version/comment", "ItemHandler.update()");
        expectedValues.put("/item/properties/latest-version/number", "2");

        expectedValues.put("/item/properties/content-model-specific/cms-prop/@test", "1");

        expectedValues.put("/item/md-records/md-record[@name = 'escidoc']", null);
        expectedValues.put("/item/@href", null);
        expectedValues.put("/item/properties/@title", "Properties");
        expectedValues.put("/item/properties/context/@href", "/ir/context/escidoc:ex1");
        expectedValues.put("/item/properties/context/@title", "Context Example 1");
        expectedValues.put("/item/properties/content-model/@href", "/cmm/content-model/escidoc:ex4");
        expectedValues.put("/item/properties/content-model/@title", "Generic Content Model");

        expectedValues.put("/item/properties/version/modified-by/@href",
            "/aa/user-account/escidoc:testsystemadministrator");
        expectedValues.put("/item/properties/version/modified-by/@title", "Test System Administrator User");

        expectedValues.put("/item/properties/latest-version/@href", "/ir/item/" + objid + ":2");
        expectedValues.put("/item/properties/latest-version/@title", "Latest Version");

        expectedValues.put("/item/properties/latest-release/@href", "/ir/item/" + objid + ":1");
        expectedValues.put("/item/properties/latest-release/@title", "Latest public version");

        // -------------------
        Vector<String> forbiddenValues = new Vector<String>();
        forbiddenValues.add("/item/properties/context[2]");
        forbiddenValues.add("/item/components/component[2]");
        forbiddenValues.add("/item/@objid");

        validateResource(xml, expectedValues, forbiddenValues);

        /*
         * Validate TripleStore Value
         */
        TripleStoreValue tsv = new TripleStoreValue();
        tsv.compareValueWithTripleStore(objid, "Context Example 1", "/RDF/Description/context-title",
            "<http://escidoc.de/core/01/properties/context-title>");
        tsv.compareValueWithTripleStore(objid, "released", "/RDF/Description/public-status",
            "<http://escidoc.de/core/01/properties/public-status>");
        tsv.compareValueWithTripleStore(objid, "Status changed to released for Item " + objid + ".",
            "/RDF/Description/public-status-comment", "<http://escidoc.de/core/01/properties/public-status-comment>");
        tsv.compareValueWithTripleStore(objid, "Generic Content Model", "/RDF/Description/content-model-title",
            "<http://escidoc.de/core/01/properties/content-model-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/modified-by-title",
            "<http://escidoc.de/core/01/properties/modified-by-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/created-by-title",
            "<http://escidoc.de/core/01/properties/created-by-title>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex4", "/RDF/Description/content-model/@resource",
            "<http://escidoc.de/core/01/structural-relations/content-model>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/created-by/@resource", "<http://escidoc.de/core/01/structural-relations/created-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/modified-by/@resource", "<http://escidoc.de/core/01/structural-relations/modified-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex1", "/RDF/Description/context/@resource",
            "<http://escidoc.de/core/01/structural-relations/context>");
        tsv.compareValueWithTripleStore(objid, "ItemHandler.update()", "/RDF/Description/comment",
            "<http://escidoc.de/core/01/properties/version/comment>");
        tsv.compareValueWithTripleStore(objid, "pending", "/RDF/Description/status",
            "<http://escidoc.de/core/01/properties/version/status>");
        tsv.compareValueWithTripleStore(objid, "2", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/version/number>");

        tsv.compareValueWithTripleStore(objid, "1", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/release/number>");

        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/pid>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/version/pid>");
        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid + ":1", "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/release/pid>");

        // number of triples for this object (to prevent missing tests)
        assertEquals("Wrong number of triples for objid='" + objid + "'", ITEM_TRIPLE_COUNT_MINUS1, tsv
            .countTriples(objid));
    }

    /**
     * Validates an Item where version 1 is in status released and version 2 is updated by adding a Component and in
     * status pending. No further versions exists.
     *
     * @param objid        The objid of hte Item.
     * @param componentIds The map with component Ids &lt;version number, Vector&lt;componentIds&gt;&gt;.
     * @param xml          XML of Item.
     * @throws Exception Thrown if XML or TripleStore not compares to the required values.
     */
    private void validateItemVersion1T04(
        final String objid, final HashMap<String, Vector<String>> componentIds, final String xml) throws Exception {

        /*
         * Item in version 1.
         * 
         * Properties
         */
        HashMap<String, String> expectedValues = new HashMap<String, String>();
        expectedValues.put("/item/properties/creation-date", null);
        expectedValues.put("/item/properties/created-by", null);
        expectedValues.put("/item/properties/public-status", "released");
        expectedValues.put("/item/properties/public-status-comment", "Status changed to released for Item " + objid
            + ".");
        expectedValues.put("/item/properties/lock-status", "unlocked");

        expectedValues.put("/item/properties/version/number", "1");
        expectedValues.put("/item/properties/version/status", "released");
        expectedValues.put("/item/properties/version/comment", "Status changed to released for Item " + objid + ".");
        expectedValues.put("/item/properties/latest-version/number", "3");

        expectedValues.put("/item/properties/content-model-specific/cms-prop/@test", "1");

        expectedValues.put("/item/md-records/md-record[@name = 'escidoc']", null);

        expectedValues.put("/item/@href", null);
        expectedValues.put("/item/properties/@title", "Properties");
        expectedValues.put("/item/properties/context/@href", "/ir/context/escidoc:ex1");
        expectedValues.put("/item/properties/context/@title", "Context Example 1");
        expectedValues.put("/item/properties/content-model/@href", "/cmm/content-model/escidoc:ex4");
        expectedValues.put("/item/properties/content-model/@title", "Generic Content Model");

        expectedValues.put("/item/properties/version/modified-by/@href",
            "/aa/user-account/escidoc:testsystemadministrator");
        expectedValues.put("/item/properties/version/modified-by/@title", "Test System Administrator User");

        expectedValues.put("/item/properties/latest-version/@href", "/ir/item/" + objid + ":3");
        expectedValues.put("/item/properties/latest-version/@title", "Latest Version");

        expectedValues.put("/item/properties/latest-release/@href", "/ir/item/" + objid + ":1");
        expectedValues.put("/item/properties/latest-release/@title", "Latest public version");

        // -------------------
        Vector<String> forbiddenValues = new Vector<String>();
        forbiddenValues.add("/item/properties/context[2]");
        forbiddenValues.add("/item/components/component");
        forbiddenValues.add("/item/@objid");

        validateResource(xml, expectedValues, forbiddenValues);

        /*
         * Validate TripleStore Value
         */
        TripleStoreValue tsv = new TripleStoreValue();
        tsv.compareValueWithTripleStore(objid, "Context Example 1", "/RDF/Description/context-title",
            "<http://escidoc.de/core/01/properties/context-title>");
        tsv.compareValueWithTripleStore(objid, "released", "/RDF/Description/public-status",
            "<http://escidoc.de/core/01/properties/public-status>");
        tsv.compareValueWithTripleStore(objid, "Status changed to released for Item " + objid + ".",
            "/RDF/Description/public-status-comment", "<http://escidoc.de/core/01/properties/public-status-comment>");
        tsv.compareValueWithTripleStore(objid, "Generic Content Model", "/RDF/Description/content-model-title",
            "<http://escidoc.de/core/01/properties/content-model-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/modified-by-title",
            "<http://escidoc.de/core/01/properties/modified-by-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/created-by-title",
            "<http://escidoc.de/core/01/properties/created-by-title>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex4", "/RDF/Description/content-model/@resource",
            "<http://escidoc.de/core/01/structural-relations/content-model>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/created-by/@resource", "<http://escidoc.de/core/01/structural-relations/created-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/modified-by/@resource", "<http://escidoc.de/core/01/structural-relations/modified-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex1", "/RDF/Description/context/@resource",
            "<http://escidoc.de/core/01/structural-relations/context>");
        tsv.compareValueWithTripleStore(objid, "ItemHandler.update()", "/RDF/Description/comment",
            "<http://escidoc.de/core/01/properties/version/comment>");
        tsv.compareValueWithTripleStore(objid, "pending", "/RDF/Description/status",
            "<http://escidoc.de/core/01/properties/version/status>");
        tsv.compareValueWithTripleStore(objid, "3", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/version/number>");

        tsv.compareValueWithTripleStore(objid, "1", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/release/number>");

        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/pid>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/version/pid>");
        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid + ":1", "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/release/pid>");

        // number of triples for this object (to prevent missing tests)
        assertEquals("Wrong number of triples for objid='" + objid + "'", ITEM_TRIPLE_COUNT, tsv.countTriples(objid));
    }

    /**
     * Validates an Item in version 1. Where version 1 is in status released and version 2 is updated by adding a
     * Component and in status pending. Version 3 is in status released.
     *
     * @param objid        The objid of hte Item.
     * @param componentIds The map with component Ids &lt;version number, Vector&lt;componentIds&gt;&gt;.
     * @param xml          XML of Item.
     * @throws Exception Thrown if XML or TripleStore not compares to the required values.
     */
    private void validateItemVersion1V5T04(
        final String objid, final HashMap<String, Vector<String>> componentIds, final String xml) throws Exception {

        /*
         * Item in version 1.
         * 
         * Properties
         */
        HashMap<String, String> expectedValues = new HashMap<String, String>();
        expectedValues.put("/item/properties/creation-date", null);
        expectedValues.put("/item/properties/created-by", null);
        expectedValues.put("/item/properties/public-status", "released");
        expectedValues.put("/item/properties/public-status-comment", "Status changed to released for Item " + objid
            + ".");
        expectedValues.put("/item/properties/lock-status", "unlocked");

        expectedValues.put("/item/properties/version/number", "1");
        expectedValues.put("/item/properties/version/status", "released");
        expectedValues.put("/item/properties/version/comment", "Status changed to released for Item " + objid + ".");
        expectedValues.put("/item/properties/latest-version/number", "5");

        expectedValues.put("/item/properties/content-model-specific/cms-prop/@test", "1");

        expectedValues.put("/item/md-records/md-record[@name = 'escidoc']", null);
        expectedValues.put("/item/@href", null);
        expectedValues.put("/item/properties/@title", "Properties");
        expectedValues.put("/item/properties/context/@href", "/ir/context/escidoc:ex1");
        expectedValues.put("/item/properties/context/@title", "Context Example 1");
        expectedValues.put("/item/properties/content-model/@href", "/cmm/content-model/escidoc:ex4");
        expectedValues.put("/item/properties/content-model/@title", "Generic Content Model");

        expectedValues.put("/item/properties/version/modified-by/@href",
            "/aa/user-account/escidoc:testsystemadministrator");
        expectedValues.put("/item/properties/version/modified-by/@title", "Test System Administrator User");

        expectedValues.put("/item/properties/latest-version/@href", "/ir/item/" + objid + ":5");
        expectedValues.put("/item/properties/latest-version/@title", "Latest Version");

        expectedValues.put("/item/properties/latest-release/@href", "/ir/item/" + objid + ":3");
        expectedValues.put("/item/properties/latest-release/@title", "Latest public version");

        // -------------------
        Vector<String> forbiddenValues = new Vector<String>();
        forbiddenValues.add("/item/properties/context[2]");
        forbiddenValues.add("/item/components/component[5]");
        forbiddenValues.add("/item/@objid");
        validateResource(xml, expectedValues, forbiddenValues);

        /*
         * Validate TripleStore Value
         */
        TripleStoreValue tsv = new TripleStoreValue();
        tsv.compareValueWithTripleStore(objid, "Context Example 1", "/RDF/Description/context-title",
            "<http://escidoc.de/core/01/properties/context-title>");
        tsv.compareValueWithTripleStore(objid, "released", "/RDF/Description/public-status",
            "<http://escidoc.de/core/01/properties/public-status>");
        tsv.compareValueWithTripleStore(objid, "Status changed to released for Item " + objid + ".",
            "/RDF/Description/public-status-comment", "<http://escidoc.de/core/01/properties/public-status-comment>");
        tsv.compareValueWithTripleStore(objid, "Generic Content Model", "/RDF/Description/content-model-title",
            "<http://escidoc.de/core/01/properties/content-model-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/modified-by-title",
            "<http://escidoc.de/core/01/properties/modified-by-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/created-by-title",
            "<http://escidoc.de/core/01/properties/created-by-title>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex4", "/RDF/Description/content-model/@resource",
            "<http://escidoc.de/core/01/structural-relations/content-model>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/created-by/@resource", "<http://escidoc.de/core/01/structural-relations/created-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/modified-by/@resource", "<http://escidoc.de/core/01/structural-relations/modified-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex1", "/RDF/Description/context/@resource",
            "<http://escidoc.de/core/01/structural-relations/context>");
        tsv.compareValueWithTripleStore(objid, "ItemHandler.update()", "/RDF/Description/comment",
            "<http://escidoc.de/core/01/properties/version/comment>");
        tsv.compareValueWithTripleStore(objid, "pending", "/RDF/Description/status",
            "<http://escidoc.de/core/01/properties/version/status>");
        tsv.compareValueWithTripleStore(objid, "5", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/version/number>");

        tsv.compareValueWithTripleStore(objid, "3", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/release/number>");

        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/pid>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/version/pid>");
        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid + ":3", "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/release/pid>");

        // number of triples for this object (to prevent missing tests)
        assertEquals("Wrong number of triples for objid='" + objid + "'", ITEM_TRIPLE_COUNT_PLUS2, tsv
            .countTriples(objid));
    }

    /**
     * Validates an Item in version 3. Where version 1 is in status released and version 2 is updated by adding a
     * Component and in status pending. Version 3 is in status released.
     *
     * @param objid        The objid of hte Item.
     * @param componentIds The map with component Ids &lt;version number, Vector&lt;componentIds&gt;&gt;.
     * @param xml          XML of Item.
     * @throws Exception Thrown if XML or TripleStore not compares to the required values.
     */
    private void validateItemVersion3V5T04(
        final String objid, final HashMap<String, Vector<String>> componentIds, final String xml) throws Exception {

        /*
         * Item in version 1.
         * 
         * Properties
         */
        HashMap<String, String> expectedValues = new HashMap<String, String>();
        expectedValues.put("/item/properties/creation-date", null);
        expectedValues.put("/item/properties/created-by", null);
        expectedValues.put("/item/properties/public-status", "released");
        expectedValues.put("/item/properties/public-status-comment", "Status changed to released for Item " + objid
            + ".");
        expectedValues.put("/item/properties/lock-status", "unlocked");

        expectedValues.put("/item/properties/version/number", "3");
        expectedValues.put("/item/properties/version/status", "released");
        expectedValues.put("/item/properties/version/comment", "Status changed to released for Item " + objid + ".");
        expectedValues.put("/item/properties/latest-version/number", "5");

        expectedValues.put("/item/properties/content-model-specific/cms-prop/@test", "1");

        expectedValues.put("/item/md-records/md-record[@name = 'escidoc']", null);
        expectedValues.put("/item/@href", null);
        expectedValues.put("/item/properties/@title", "Properties");
        expectedValues.put("/item/properties/context/@href", "/ir/context/escidoc:ex1");
        expectedValues.put("/item/properties/context/@title", "Context Example 1");
        expectedValues.put("/item/properties/content-model/@href", "/cmm/content-model/escidoc:ex4");
        expectedValues.put("/item/properties/content-model/@title", "Generic Content Model");

        expectedValues.put("/item/properties/version/modified-by/@href",
            "/aa/user-account/escidoc:testsystemadministrator");
        expectedValues.put("/item/properties/version/modified-by/@title", "Test System Administrator User");

        expectedValues.put("/item/properties/latest-version/@href", "/ir/item/" + objid + ":5");
        expectedValues.put("/item/properties/latest-version/@title", "Latest Version");

        expectedValues.put("/item/properties/latest-release/@href", "/ir/item/" + objid + ":3");
        expectedValues.put("/item/properties/latest-release/@title", "Latest public version");

        // -------------------
        Vector<String> forbiddenValues = new Vector<String>();
        forbiddenValues.add("/item/properties/context[2]");
        forbiddenValues.add("/item/components/component[5]");
        forbiddenValues.add("/item/@objid");

        validateResource(xml, expectedValues, forbiddenValues);

        /*
         * Validate TripleStore Value
         */
        TripleStoreValue tsv = new TripleStoreValue();
        tsv.compareValueWithTripleStore(objid, "Context Example 1", "/RDF/Description/context-title",
            "<http://escidoc.de/core/01/properties/context-title>");
        tsv.compareValueWithTripleStore(objid, "released", "/RDF/Description/public-status",
            "<http://escidoc.de/core/01/properties/public-status>");
        tsv.compareValueWithTripleStore(objid, "Status changed to released for Item " + objid + ".",
            "/RDF/Description/public-status-comment", "<http://escidoc.de/core/01/properties/public-status-comment>");
        tsv.compareValueWithTripleStore(objid, "Generic Content Model", "/RDF/Description/content-model-title",
            "<http://escidoc.de/core/01/properties/content-model-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/modified-by-title",
            "<http://escidoc.de/core/01/properties/modified-by-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/created-by-title",
            "<http://escidoc.de/core/01/properties/created-by-title>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex4", "/RDF/Description/content-model/@resource",
            "<http://escidoc.de/core/01/structural-relations/content-model>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/created-by/@resource", "<http://escidoc.de/core/01/structural-relations/created-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/modified-by/@resource", "<http://escidoc.de/core/01/structural-relations/modified-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex1", "/RDF/Description/context/@resource",
            "<http://escidoc.de/core/01/structural-relations/context>");
        tsv.compareValueWithTripleStore(objid, "ItemHandler.update()", "/RDF/Description/comment",
            "<http://escidoc.de/core/01/properties/version/comment>");
        tsv.compareValueWithTripleStore(objid, "pending", "/RDF/Description/status",
            "<http://escidoc.de/core/01/properties/version/status>");
        tsv.compareValueWithTripleStore(objid, "5", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/version/number>");

        tsv.compareValueWithTripleStore(objid, "3", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/release/number>");

        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/pid>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/version/pid>");
        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid + ":3", "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/release/pid>");

        // number of triples for this object (to prevent missing tests)
        assertEquals("Wrong number of triples for objid='" + objid + "'", ITEM_TRIPLE_COUNT_PLUS2, tsv
            .countTriples(objid));
    }

    /**
     * Validates an Item where version 1 is in status released and version 2 is updated by adding a Component and in
     * status pending. No further versions exists.
     *
     * @param objid        The objid of hte Item.
     * @param componentIds The map with component Ids &lt;version number, Vector&lt;componentIds&gt;&gt;.
     * @param xml          XML of Item.
     * @throws Exception Thrown if XML or TripleStore not compares to the required values.
     */
    private void validateItemVersion2T04(
        final String objid, final HashMap<String, Vector<String>> componentIds, final String xml) throws Exception {

        /*
         * Item in version 2.
         */
        HashMap<String, String> expectedValues = new HashMap<String, String>();
        expectedValues.put("/item/properties/creation-date", null);
        expectedValues.put("/item/properties/created-by", null);
        expectedValues.put("/item/properties/public-status", "released");
        expectedValues.put("/item/properties/public-status-comment", "Status changed to released for Item " + objid
            + ".");
        expectedValues.put("/item/properties/lock-status", "unlocked");

        expectedValues.put("/item/properties/version/number", "2");
        expectedValues.put("/item/properties/version/status", "pending");
        expectedValues.put("/item/properties/version/comment", "ItemHandler.update()");
        expectedValues.put("/item/properties/latest-version/number", "3");

        expectedValues.put("/item/properties/content-model-specific/cms-prop/@test", "1");

        expectedValues.put("/item/md-records/md-record[@name = 'escidoc']", null);

        expectedValues.put("/item/@href", null);
        expectedValues.put("/item/properties/@title", "Properties");
        expectedValues.put("/item/properties/context/@href", "/ir/context/escidoc:ex1");
        expectedValues.put("/item/properties/context/@title", "Context Example 1");
        expectedValues.put("/item/properties/content-model/@href", "/cmm/content-model/escidoc:ex4");
        expectedValues.put("/item/properties/content-model/@title", "Generic Content Model");

        expectedValues.put("/item/properties/version/modified-by/@href",
            "/aa/user-account/escidoc:testsystemadministrator");
        expectedValues.put("/item/properties/version/modified-by/@title", "Test System Administrator User");

        expectedValues.put("/item/properties/latest-version/@href", "/ir/item/" + objid + ":3");
        expectedValues.put("/item/properties/latest-version/@title", "Latest Version");

        expectedValues.put("/item/properties/latest-release/@href", "/ir/item/" + objid + ":1");
        expectedValues.put("/item/properties/latest-release/@title", "Latest public version");

        // -------------------
        Vector<String> forbiddenValues = new Vector<String>();
        forbiddenValues.add("/item/properties/context[2]");
        forbiddenValues.add("/item/components/component[2]");

        forbiddenValues.add("/item/@objid");

        validateResource(xml, expectedValues, forbiddenValues);

        /*
         * Validate TripleStore Value
         */
        TripleStoreValue tsv = new TripleStoreValue();
        tsv.compareValueWithTripleStore(objid, "Context Example 1", "/RDF/Description/context-title",
            "<http://escidoc.de/core/01/properties/context-title>");
        tsv.compareValueWithTripleStore(objid, "released", "/RDF/Description/public-status",
            "<http://escidoc.de/core/01/properties/public-status>");
        tsv.compareValueWithTripleStore(objid, "Status changed to released for Item " + objid + ".",
            "/RDF/Description/public-status-comment", "<http://escidoc.de/core/01/properties/public-status-comment>");
        tsv.compareValueWithTripleStore(objid, "Generic Content Model", "/RDF/Description/content-model-title",
            "<http://escidoc.de/core/01/properties/content-model-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/modified-by-title",
            "<http://escidoc.de/core/01/properties/modified-by-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/created-by-title",
            "<http://escidoc.de/core/01/properties/created-by-title>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex4", "/RDF/Description/content-model/@resource",
            "<http://escidoc.de/core/01/structural-relations/content-model>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/created-by/@resource", "<http://escidoc.de/core/01/structural-relations/created-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/modified-by/@resource", "<http://escidoc.de/core/01/structural-relations/modified-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex1", "/RDF/Description/context/@resource",
            "<http://escidoc.de/core/01/structural-relations/context>");
        tsv.compareValueWithTripleStore(objid, "ItemHandler.update()", "/RDF/Description/comment",
            "<http://escidoc.de/core/01/properties/version/comment>");
        tsv.compareValueWithTripleStore(objid, "pending", "/RDF/Description/status",
            "<http://escidoc.de/core/01/properties/version/status>");
        tsv.compareValueWithTripleStore(objid, "3", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/version/number>");

        tsv.compareValueWithTripleStore(objid, "1", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/release/number>");

        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/pid>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/version/pid>");
        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid + ":1", "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/release/pid>");

        // number of triples for this object (to prevent missing tests)
        assertEquals("Wrong number of triples for objid='" + objid + "'", ITEM_TRIPLE_COUNT, tsv.countTriples(objid));
    }

    /**
     * Validates an Item where version 1 is in status released and version 2 is updated by adding a Component and in
     * status released. Version 4 and 5 are pending. No further versions exists.
     *
     * @param objid        The objid of hte Item.
     * @param componentIds The map with component Ids &lt;version number, Vector&lt;componentIds&gt;&gt;.
     * @param xml          XML of Item.
     * @throws Exception Thrown if XML or TripleStore not compares to the required values.
     */
    private void validateItemVersion4T01(
        final String objid, final HashMap<String, Vector<String>> componentIds, final String xml) throws Exception {

        /*
         * Item in version 4.version 1: 0 Components, submitted, released;
         * version 2: 1 Component, pending; version 3: 2 Components, pending,
         * submitted, released; version 4: 3 components, pending, components
         */
        HashMap<String, String> expectedValues = new HashMap<String, String>();
        expectedValues.put("/item/properties/creation-date", null);
        expectedValues.put("/item/properties/created-by", null);
        expectedValues.put("/item/properties/public-status", "released");
        expectedValues.put("/item/properties/public-status-comment", "Status changed to released for Item " + objid
            + ".");
        expectedValues.put("/item/properties/lock-status", "unlocked");

        expectedValues.put("/item/properties/version/number", "4");
        expectedValues.put("/item/properties/version/status", "pending");
        expectedValues.put("/item/properties/version/comment", "ItemHandler.update()");
        expectedValues.put("/item/properties/latest-version/number", "5");

        expectedValues.put("/item/properties/content-model-specific/cms-prop/@test", "1");

        expectedValues.put("/item/md-records/md-record[@name = 'escidoc']", null);

        expectedValues.put("/item/components/component[1]", null);
        expectedValues.put("/item/components/component[2]", null);
        expectedValues.put("/item/components/component[3]", null);

        expectedValues.put("/item/@href", null);
        expectedValues.put("/item/properties/@title", "Properties");
        expectedValues.put("/item/properties/context/@href", "/ir/context/escidoc:ex1");
        expectedValues.put("/item/properties/context/@title", "Context Example 1");
        expectedValues.put("/item/properties/content-model/@href", "/cmm/content-model/escidoc:ex4");
        expectedValues.put("/item/properties/content-model/@title", "Generic Content Model");

        expectedValues.put("/item/properties/version/modified-by/@href",
            "/aa/user-account/escidoc:testsystemadministrator");
        expectedValues.put("/item/properties/version/modified-by/@title", "Test System Administrator User");

        expectedValues.put("/item/properties/latest-version/@href", "/ir/item/" + objid + ":5");
        expectedValues.put("/item/properties/latest-version/@title", "Latest Version");

        expectedValues.put("/item/properties/latest-release/@href", "/ir/item/" + objid + ":3");
        expectedValues.put("/item/properties/latest-release/@title", "Latest public version");

        // -------------------
        Vector<String> forbiddenValues = new Vector<String>();
        forbiddenValues.add("/item/properties/context[2]");
        forbiddenValues.add("/item/components/component[4]");
        forbiddenValues.add("/item/@objid");
        validateResource(xml, expectedValues, forbiddenValues);

        /*
         * Validate TripleStore Value
         */
        TripleStoreValue tsv = new TripleStoreValue();
        tsv.compareValueWithTripleStore(objid, "Context Example 1", "/RDF/Description/context-title",
            "<http://escidoc.de/core/01/properties/context-title>");
        tsv.compareValueWithTripleStore(objid, "released", "/RDF/Description/public-status",
            "<http://escidoc.de/core/01/properties/public-status>");
        tsv.compareValueWithTripleStore(objid, "Status changed to released for Item " + objid + ".",
            "/RDF/Description/public-status-comment", "<http://escidoc.de/core/01/properties/public-status-comment>");
        tsv.compareValueWithTripleStore(objid, "Generic Content Model", "/RDF/Description/content-model-title",
            "<http://escidoc.de/core/01/properties/content-model-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/modified-by-title",
            "<http://escidoc.de/core/01/properties/modified-by-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/created-by-title",
            "<http://escidoc.de/core/01/properties/created-by-title>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex4", "/RDF/Description/content-model/@resource",
            "<http://escidoc.de/core/01/structural-relations/content-model>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/created-by/@resource", "<http://escidoc.de/core/01/structural-relations/created-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/modified-by/@resource", "<http://escidoc.de/core/01/structural-relations/modified-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex1", "/RDF/Description/context/@resource",
            "<http://escidoc.de/core/01/structural-relations/context>");
        tsv.compareValueWithTripleStore(objid, "ItemHandler.update()", "/RDF/Description/comment",
            "<http://escidoc.de/core/01/properties/version/comment>");
        tsv.compareValueWithTripleStore(objid, "pending", "/RDF/Description/status",
            "<http://escidoc.de/core/01/properties/version/status>");
        tsv.compareValueWithTripleStore(objid, "5", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/version/number>");

        tsv.compareValueWithTripleStore(objid, "3", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/release/number>");

        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/pid>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/version/pid>");
        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid + ":3", "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/release/pid>");

        // number of triples for this object (to prevent missing tests)
        assertEquals("Wrong number of triples for objid='" + objid + "'", ITEM_TRIPLE_COUNT_PLUS2, tsv
            .countTriples(objid));
    }

    /**
     * Validates an Item where version 1 is in status released and version 2 is updated by adding a Component and in
     * status released. Version 4 and 5 are pending. No further versions exists.
     *
     * @param objid        The objid of hte Item.
     * @param componentIds The map with component Ids &lt;version number, Vector&lt;componentIds&gt;&gt;.
     * @param xml          XML of Item.
     * @throws Exception Thrown if XML or TripleStore not compares to the required values.
     */
    private void validateItemVersion5T01(
        final String objid, final HashMap<String, Vector<String>> componentIds, final String xml) throws Exception {

        /*
         * Item in version 5.
         */
        HashMap<String, String> expectedValues = new HashMap<String, String>();
        expectedValues.put("/item/properties/creation-date", null);
        expectedValues.put("/item/properties/created-by", null);
        expectedValues.put("/item/properties/public-status", "released");
        expectedValues.put("/item/properties/public-status-comment", "Status changed to released for Item " + objid
            + ".");
        expectedValues.put("/item/properties/lock-status", "unlocked");

        expectedValues.put("/item/properties/version/number", "5");
        expectedValues.put("/item/properties/version/status", "pending");
        expectedValues.put("/item/properties/version/comment", "ItemHandler.update()");
        expectedValues.put("/item/properties/latest-version/number", "5");

        expectedValues.put("/item/properties/content-model-specific/cms-prop/@test", "1");

        expectedValues.put("/item/md-records/md-record[@name = 'escidoc']", null);

        expectedValues.put("/item/components/component[1]", null);
        expectedValues.put("/item/components/component[2]", null);
        expectedValues.put("/item/components/component[3]", null);
        expectedValues.put("/item/components/component[4]", null);

        expectedValues.put("/item/@href", null);
        expectedValues.put("/item/properties/@title", "Properties");
        expectedValues.put("/item/properties/context/@href", "/ir/context/escidoc:ex1");
        expectedValues.put("/item/properties/context/@title", "Context Example 1");
        expectedValues.put("/item/properties/content-model/@href", "/cmm/content-model/escidoc:ex4");
        expectedValues.put("/item/properties/content-model/@title", "Generic Content Model");

        expectedValues.put("/item/properties/version/modified-by/@href",
            "/aa/user-account/escidoc:testsystemadministrator");
        expectedValues.put("/item/properties/version/modified-by/@title", "Test System Administrator User");

        expectedValues.put("/item/properties/latest-version/@href", "/ir/item/" + objid + ":5");
        expectedValues.put("/item/properties/latest-version/@title", "Latest Version");

        expectedValues.put("/item/properties/latest-release/@href", "/ir/item/" + objid + ":3");
        expectedValues.put("/item/properties/latest-release/@title", "Latest public version");

        // -------------------
        Vector<String> forbiddenValues = new Vector<String>();
        forbiddenValues.add("/item/properties/context[2]");
        forbiddenValues.add("/item/components/component[5]");
        forbiddenValues.add("/item/@objid");
        validateResource(xml, expectedValues, forbiddenValues);

        /*
         * Validate TripleStore Value
         */
        TripleStoreValue tsv = new TripleStoreValue();
        tsv.compareValueWithTripleStore(objid, "Context Example 1", "/RDF/Description/context-title",
            "<http://escidoc.de/core/01/properties/context-title>");
        tsv.compareValueWithTripleStore(objid, "released", "/RDF/Description/public-status",
            "<http://escidoc.de/core/01/properties/public-status>");
        tsv.compareValueWithTripleStore(objid, "Status changed to released for Item " + objid + ".",
            "/RDF/Description/public-status-comment", "<http://escidoc.de/core/01/properties/public-status-comment>");
        tsv.compareValueWithTripleStore(objid, "Generic Content Model", "/RDF/Description/content-model-title",
            "<http://escidoc.de/core/01/properties/content-model-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/modified-by-title",
            "<http://escidoc.de/core/01/properties/modified-by-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/created-by-title",
            "<http://escidoc.de/core/01/properties/created-by-title>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex4", "/RDF/Description/content-model/@resource",
            "<http://escidoc.de/core/01/structural-relations/content-model>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/created-by/@resource", "<http://escidoc.de/core/01/structural-relations/created-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/modified-by/@resource", "<http://escidoc.de/core/01/structural-relations/modified-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:ex1", "/RDF/Description/context/@resource",
            "<http://escidoc.de/core/01/structural-relations/context>");
        tsv.compareValueWithTripleStore(objid, "ItemHandler.update()", "/RDF/Description/comment",
            "<http://escidoc.de/core/01/properties/version/comment>");
        tsv.compareValueWithTripleStore(objid, "pending", "/RDF/Description/status",
            "<http://escidoc.de/core/01/properties/version/status>");
        tsv.compareValueWithTripleStore(objid, "5", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/version/number>");

        tsv.compareValueWithTripleStore(objid, "3", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/release/number>");

        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/pid>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/version/pid>");
        tsv.compareValueWithTripleStore(objid, "hdl:someHandle/test/" + objid + ":3", "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/release/pid>");

        // number of triples for this object (to prevent missing tests)
        assertEquals("Wrong number of triples for objid='" + objid + "'", ITEM_TRIPLE_COUNT_PLUS2, tsv
            .countTriples(objid));
    }

    /**
     * Test Item where component was updated.
     *
     * @param objid        The objid of the Item.
     * @param componentIds The map with component Ids &lt;version number, Vector&lt;componentIds&gt;&gt;.
     * @param xml          XML of Item.
     * @throws Exception Thrown if XML or TripleStore not compares to the required values.
     */
    private void validateItemVersion02Test04(
        final String objid, final HashMap<String, Vector<String>> componentIds, final String xml) throws Exception {

        /*
         * Item in version 2.
         */
        HashMap<String, String> expectedValues = new HashMap<String, String>();
        expectedValues.put("/item/properties/creation-date", null);
        expectedValues.put("/item/properties/created-by", null);
        expectedValues.put("/item/properties/public-status", "pending");
        expectedValues.put("/item/properties/public-status-comment", "Object created.");
        expectedValues.put("/item/properties/lock-status", "unlocked");

        expectedValues.put("/item/properties/version/number", "2");
        expectedValues.put("/item/properties/version/status", "pending");
        expectedValues.put("/item/properties/version/comment", "ItemHandler.update()");
        expectedValues.put("/item/properties/latest-version/number", "2");

        expectedValues.put("/item/properties/content-model-specific/nix", null);

        expectedValues.put("/item/md-records/md-record[@name = 'escidoc']", null);

        expectedValues.put("/item/components/component/properties/content-category", "some value of version 2");
        expectedValues.put("/item/@href", null);

        // -------------------
        Vector<String> forbiddenValues = new Vector<String>();
        forbiddenValues.add("/item/properties/context[2]");
        forbiddenValues.add("/item/components/component[2]");
        forbiddenValues.add("/item/@objid");
        validateResource(xml, expectedValues, forbiddenValues);

        /*
         * Validate TripleStore Value
         */
        TripleStoreValue tsv = new TripleStoreValue();
        tsv.compareValueWithTripleStore(objid, "Test Collection", "/RDF/Description/context-title",
            "<http://escidoc.de/core/01/properties/context-title>");
        tsv.compareValueWithTripleStore(objid, "pending", "/RDF/Description/public-status",
            "<http://escidoc.de/core/01/properties/public-status>");
        tsv.compareValueWithTripleStore(objid, "Object created.", "/RDF/Description/public-status-comment",
            "<http://escidoc.de/core/01/properties/public-status-comment>");
        tsv.compareValueWithTripleStore(objid, "Persistent Content Model", "/RDF/Description/content-model-title",
            "<http://escidoc.de/core/01/properties/content-model-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/modified-by-title",
            "<http://escidoc.de/core/01/properties/modified-by-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/created-by-title",
            "<http://escidoc.de/core/01/properties/created-by-title>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:persistent4",
            "/RDF/Description/content-model/@resource",
            "<http://escidoc.de/core/01/structural-relations/content-model>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/created-by/@resource", "<http://escidoc.de/core/01/structural-relations/created-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/modified-by/@resource", "<http://escidoc.de/core/01/structural-relations/modified-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:persistent3", "/RDF/Description/context/@resource",
            "<http://escidoc.de/core/01/structural-relations/context>");
        tsv.compareValueWithTripleStore(objid, "ItemHandler.update()", "/RDF/Description/comment",
            "<http://escidoc.de/core/01/properties/version/comment>");
        tsv.compareValueWithTripleStore(objid, "pending", "/RDF/Description/status",
            "<http://escidoc.de/core/01/properties/version/status>");
        tsv.compareValueWithTripleStore(objid, "2", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/version/number>");

        // forbidden values
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/release/number>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/pid>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/version/pid>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/release/pid>");

        // number of triples for this object (to prevent missing tests)
        assertEquals("Wrong number of triples for objid='" + objid + "'", ITEM_TRIPLE_COUNT_MINUS5, tsv
            .countTriples(objid));

    }

    /**
     * Test retrieve older version of Item where component was updated.
     *
     * @param objid        The objid of hte Item.
     * @param componentIds The map with component Ids &lt;version number, Vector&lt;componentIds&gt;&gt;.
     * @param xml          XML of Item.
     * @throws Exception Thrown if XML or TripleStore not compares to the required values.
     */
    private void validateItemVersion02Test041(
        final String objid, final HashMap<String, Vector<String>> componentIds, final String xml) throws Exception {

        /*
         * Item in version 2.
         */
        HashMap<String, String> expectedValues = new HashMap<String, String>();
        expectedValues.put("/item/properties/creation-date", null);
        expectedValues.put("/item/properties/created-by", null);
        expectedValues.put("/item/properties/public-status", "pending");
        expectedValues.put("/item/properties/public-status-comment", "Object created.");
        expectedValues.put("/item/properties/lock-status", "unlocked");

        expectedValues.put("/item/properties/version/number", "2");
        expectedValues.put("/item/properties/version/status", "pending");
        expectedValues.put("/item/properties/version/comment", "ItemHandler.update()");
        expectedValues.put("/item/properties/latest-version/number", "3");

        expectedValues.put("/item/properties/content-model-specific/nix", null);

        expectedValues.put("/item/md-records/md-record[@name = 'escidoc']", null);

        expectedValues.put("/item/components/component/properties/content-category", "some value of version 2");
        expectedValues.put("/item/@href", null);

        // -------------------
        Vector<String> forbiddenValues = new Vector<String>();
        forbiddenValues.add("/item/properties/context[2]");
        forbiddenValues.add("/item/components/component[2]");
        forbiddenValues.add("/item/@objid");
        validateResource(xml, expectedValues, forbiddenValues);

        /*
         * Validate TripleStore Value
         */
        TripleStoreValue tsv = new TripleStoreValue();
        tsv.compareValueWithTripleStore(objid, "Test Collection", "/RDF/Description/context-title",
            "<http://escidoc.de/core/01/properties/context-title>");
        tsv.compareValueWithTripleStore(objid, "pending", "/RDF/Description/public-status",
            "<http://escidoc.de/core/01/properties/public-status>");
        tsv.compareValueWithTripleStore(objid, "Object created.", "/RDF/Description/public-status-comment",
            "<http://escidoc.de/core/01/properties/public-status-comment>");
        tsv.compareValueWithTripleStore(objid, "Persistent Content Model", "/RDF/Description/content-model-title",
            "<http://escidoc.de/core/01/properties/content-model-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/modified-by-title",
            "<http://escidoc.de/core/01/properties/modified-by-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/created-by-title",
            "<http://escidoc.de/core/01/properties/created-by-title>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:persistent4",
            "/RDF/Description/content-model/@resource",
            "<http://escidoc.de/core/01/structural-relations/content-model>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/created-by/@resource", "<http://escidoc.de/core/01/structural-relations/created-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/modified-by/@resource", "<http://escidoc.de/core/01/structural-relations/modified-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:persistent3", "/RDF/Description/context/@resource",
            "<http://escidoc.de/core/01/structural-relations/context>");
        tsv.compareValueWithTripleStore(objid, "ItemHandler.update()", "/RDF/Description/comment",
            "<http://escidoc.de/core/01/properties/version/comment>");
        tsv.compareValueWithTripleStore(objid, "pending", "/RDF/Description/status",
            "<http://escidoc.de/core/01/properties/version/status>");
        tsv.compareValueWithTripleStore(objid, "3", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/version/number>");

        // forbidden values
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/release/number>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/pid>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/version/pid>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/release/pid>");

        // number of triples for this object (to prevent missing tests)
        assertEquals("Wrong number of triples for objid='" + objid + "'", ITEM_TRIPLE_COUNT_MINUS5, tsv
            .countTriples(objid));

    }

    /**
     * Test Item where component was updated.
     *
     * @param objid        The objid of hte Item.
     * @param componentIds The map with component Ids &lt;version number, Vector&lt;componentIds&gt;&gt;.
     * @param xml          XML of Item.
     * @throws Exception Thrown if XML or TripleStore not compares to the required values.
     */
    private void validateItemVersion03Test04(
        final String objid, final HashMap<String, Vector<String>> componentIds, final String xml) throws Exception {

        HashMap<String, String> expectedValues = new HashMap<String, String>();
        expectedValues.put("/item/properties/creation-date", null);
        expectedValues.put("/item/properties/created-by", null);
        expectedValues.put("/item/properties/public-status", "pending");
        expectedValues.put("/item/properties/public-status-comment", "Object created.");
        expectedValues.put("/item/properties/lock-status", "unlocked");

        expectedValues.put("/item/properties/version/number", "3");
        expectedValues.put("/item/properties/version/status", "pending");
        expectedValues.put("/item/properties/version/comment", "ItemHandler.update()");
        expectedValues.put("/item/properties/latest-version/number", "3");

        expectedValues.put("/item/properties/content-model-specific/nix", null);

        expectedValues.put("/item/md-records/md-record[@name = 'escidoc']", null);

        expectedValues.put("/item/components/component/properties/content-category", "some value of version 3");

        expectedValues.put("/item/@href", null);

        // -------------------
        Vector<String> forbiddenValues = new Vector<String>();
        forbiddenValues.add("/item/properties/context[2]");
        forbiddenValues.add("/item/components/component[2]");
        forbiddenValues.add("/item/@objid");
        validateResource(xml, expectedValues, forbiddenValues);

        /*
         * Validate TripleStore Value
         */
        TripleStoreValue tsv = new TripleStoreValue();
        tsv.compareValueWithTripleStore(objid, "Test Collection", "/RDF/Description/context-title",
            "<http://escidoc.de/core/01/properties/context-title>");
        tsv.compareValueWithTripleStore(objid, "pending", "/RDF/Description/public-status",
            "<http://escidoc.de/core/01/properties/public-status>");
        tsv.compareValueWithTripleStore(objid, "Object created.", "/RDF/Description/public-status-comment",
            "<http://escidoc.de/core/01/properties/public-status-comment>");
        tsv.compareValueWithTripleStore(objid, "Persistent Content Model", "/RDF/Description/content-model-title",
            "<http://escidoc.de/core/01/properties/content-model-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/modified-by-title",
            "<http://escidoc.de/core/01/properties/modified-by-title>");
        tsv.compareValueWithTripleStore(objid, "Test System Administrator User", "/RDF/Description/created-by-title",
            "<http://escidoc.de/core/01/properties/created-by-title>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:persistent4",
            "/RDF/Description/content-model/@resource",
            "<http://escidoc.de/core/01/structural-relations/content-model>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/created-by/@resource", "<http://escidoc.de/core/01/structural-relations/created-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:testsystemadministrator",
            "/RDF/Description/modified-by/@resource", "<http://escidoc.de/core/01/structural-relations/modified-by>");
        tsv.compareValueWithTripleStore(objid, "info:fedora/escidoc:persistent3", "/RDF/Description/context/@resource",
            "<http://escidoc.de/core/01/structural-relations/context>");
        tsv.compareValueWithTripleStore(objid, "ItemHandler.update()", "/RDF/Description/comment",
            "<http://escidoc.de/core/01/properties/version/comment>");
        tsv.compareValueWithTripleStore(objid, "pending", "/RDF/Description/status",
            "<http://escidoc.de/core/01/properties/version/status>");
        tsv.compareValueWithTripleStore(objid, "3", "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/version/number>");

        // forbidden values
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/number",
            "<http://escidoc.de/core/01/properties/release/number>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/pid>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/version/pid>");
        tsv.compareValueWithTripleStore(objid, null, "/RDF/Description/pid",
            "<http://escidoc.de/core/01/properties/release/pid>");

        // number of triples for this object (to prevent missing tests)
        assertEquals("Wrong number of triples for objid='" + objid + "'", ITEM_TRIPLE_COUNT_MINUS5, tsv
            .countTriples(objid));

    }

}
