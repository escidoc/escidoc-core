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
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.remote.application.violated.AlreadyWithdrawnException;
import de.escidoc.core.common.exceptions.remote.application.violated.NotPublishedException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.EscidocTestBase;
import de.escidoc.core.test.common.AssignParam;
import de.escidoc.core.test.security.client.PWCallback;
import org.apache.xpath.XPathAPI;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test the mock implementation of the item resource.
 * 
 * @author Michael Schneider
 */
public class ItemLifecycleIT extends ItemTestBase {

    private String theItemXml;

    private String theItemId;

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Before
    public void setUp() throws Exception {
        // create an item and save the id
        String xmlData =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        theItemXml = create(xmlData);
        theItemId = getObjidValue(theItemXml);
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
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
    }

    /**
     * Test successful submitting an Item in state "pending".
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOmSi1() throws Exception {

        DateTime t1 = getLastModificationDateValue2(getDocument(this.theItemXml));

        String xml = submit(theItemId, getStatusTaskParam(t1, null));
        DateTime t2 = getLastModificationDateValue2(getDocument(xml));

        assertTrue("Timestamp of submitted not after pending", t1.compareTo(t2) < 0);

        final Document submittedDocument = EscidocAbstractTest.getDocument(retrieve(theItemId));
        DateTime t2a = getLastModificationDateValue2(submittedDocument);

        assertTrue("Timestamp not equal", t2.compareTo(t2a) == 0);

        assertXmlEquals("Unexpected status. ", submittedDocument, XPATH_ITEM_STATUS, STATE_SUBMITTED);
        assertXmlEquals("Unexpected current version status", submittedDocument, XPATH_ITEM_CURRENT_VERSION_STATUS,
            STATE_SUBMITTED);

        // check timestamps consistency ==================================

        // check timestamps within Item XML-------------------------------
        // /item/@last-modification-date == /item/properties/version/date
        assertEquals(
            "last-modification-date in root attribute of Item [" + theItemId + "] differs from //version/date",
            XPathAPI.selectSingleNode(submittedDocument, "/item/@last-modification-date").getTextContent(), XPathAPI
                .selectSingleNode(submittedDocument, "/item/properties/version/date").getTextContent());

        // /item/@last-modification-date == /item/properties/latest-version/date
        assertEquals("last-modification-date in root attribute of Item [" + theItemId
            + "] differs from //latest-version/date", XPathAPI.selectSingleNode(submittedDocument,
            "/item/@last-modification-date").getTextContent(), XPathAPI.selectSingleNode(submittedDocument,
            "/item/properties/latest-version/date").getTextContent());

        // check timestamps within Version History XML -------------------
        Document wovDocV1E2 = EscidocAbstractTest.getDocument(retrieveVersionHistory(theItemId));

        // /version-history/version[version-number='1']/events/event[1]
        // /eventDateTime ==
        // /version-history/version[version-number='1']/@timestamp
        assertEquals("eventDateTime of the latest event of version 1 differs "
            + "from timestamp attribute of version 1 [" + theItemId + "]", XPathAPI.selectSingleNode(wovDocV1E2,
            "/version-history/version[version-number='1']" + "/events/event[2]/eventDateTime").getTextContent(),
            XPathAPI
                .selectSingleNode(wovDocV1E2, "/version-history/version[version-number='1']/@timestamp")
                .getTextContent());

        // check timestamps between Item XML and Version History XML -----
        // /item/@last-modification-date ==
        // /version-history/@last-modification-date
        assertEquals("last-modification-date in root attribute of Item [" + theItemId
            + "] differs from last-modification-date " + "attribute of version-history", XPathAPI.selectSingleNode(
            submittedDocument, "/item/@last-modification-date").getTextContent(), XPathAPI.selectSingleNode(wovDocV1E2,
            "/version-history/@last-modification-date").getTextContent());

        // /version-history/version[version-number='1']/@timestamp ==
        // /item/properties/creation-date
        assertEquals("last-modification-date in root attribute of Item [" + theItemId
            + "] differs from creation date of version", XPathAPI.selectSingleNode(wovDocV1E2,
            "/version-history/version[version-number='1']/@timestamp").getTextContent(), XPathAPI.selectSingleNode(
            submittedDocument, "/item/properties/creation-date").getTextContent());

        // /version-history/version[version-number='1']/timestamp ==
        // /item/@last-modification-date
        assertEquals("last-modification-date in root attribute of Item [" + theItemId
            + "] differs from timestamp of version 1 " + "in version-history", XPathAPI.selectSingleNode(wovDocV1E2,
            "/version-history/version[version-number='1']/timestamp").getTextContent(), XPathAPI.selectSingleNode(
            submittedDocument, "/item/@last-modification-date").getTextContent());

    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testSubmitAfterRelease() throws Exception {

        final String xPath = "/item/properties/content-model-specific";
        final String pendingLastModificationDate = getLastModificationDateValue(getDocument(this.theItemXml));

        submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theItemId))), null));
        String pidParam;
        if (getItemClient().getPidConfig("cmm.Item.objectPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.objectPid.releaseWithoutPid", "false")) {

            AssignParam assignPidParam = new AssignParam();
            assignPidParam.setUrl(new URL("http://somewhere/" + this.theItemId));
            pidParam =
                getAssignPidTaskParam(getLastModificationDateValue2(getDocument(retrieve(this.theItemId))),
                    assignPidParam);

            assignObjectPid(this.theItemId, pidParam);
        }
        if (getItemClient().getPidConfig("cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.versionPid.releaseWithoutPid", "false")) {

            String latestVersion = getLatestVersionObjidValue(theItemXml);

            AssignParam assignPidParam = new AssignParam();
            assignPidParam.setUrl(new URL("http://somewhere/" + latestVersion));
            pidParam =
                getAssignPidTaskParam(getLastModificationDateValue2(getDocument(retrieve(latestVersion))),
                    assignPidParam);

            assignVersionPid(latestVersion, pidParam);
        }

        release(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theItemId))), null));
        String xml = addElement(retrieve(theItemId), xPath + "/nix");
        assertXmlValidItem(xml);
        update(theItemId, xml);
        submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theItemId))), null));

        String submittedXml = null;
        try {
            submittedXml = retrieve(theItemId);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving the revised, submitted item failed. ", e);
        }
        final Document submittedDocument = EscidocAbstractTest.getDocument(submittedXml);
        assertDateBeforeAfter(pendingLastModificationDate, getLastModificationDateValue(submittedDocument));
        assertXmlEquals("Unexpected status. ", submittedDocument, XPATH_ITEM_STATUS, STATE_RELEASED);
        assertXmlEquals("Unexpected current version status", submittedDocument, XPATH_ITEM_CURRENT_VERSION_STATUS,
            STATE_SUBMITTED);

    }

    /**
     * Test for Jira INFR-1020.
     */
    @Test(expected = InvalidStatusException.class)
    public void testSubmitAfterSubmit() throws Exception {
        final String xPath = "/item/properties/content-model-specific";
        String xml = addElement(retrieve(theItemId), xPath + "/nix");

        assertXmlValidItem(xml);
        update(theItemId, xml);
        submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theItemId))), null));

        submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theItemId))), null));
    }

    /**
     * Test successful submitting an item in state "in-revision".
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOmSi1_2() throws Exception {

        String xml =
            submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(this.theItemXml)), null));
        xml = revise(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(xml)), null));

        final String revisedLastModificationDate = getLastModificationDateValue(getDocument(xml));

        try {
            submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(xml)), null));
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Submitting the pending container failed. ", e);
        }

        String submittedXml = null;
        try {
            submittedXml = retrieve(theItemId);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving the revised, submitted item failed. ", e);
        }
        final Document submittedDocument = EscidocAbstractTest.getDocument(submittedXml);
        assertDateBeforeAfter(revisedLastModificationDate, getLastModificationDateValue(submittedDocument));
        assertXmlEquals("Unexpected status. ", submittedDocument, XPATH_ITEM_STATUS, STATE_SUBMITTED);
        assertXmlEquals("Unexpected current version status", submittedDocument, XPATH_ITEM_CURRENT_VERSION_STATUS,
            STATE_SUBMITTED);
    }

    /**
     * Test handling of non-ASCII character within submit comment.
     * 
     * @throws Exception
     *             Thrown if escaping of non-ASCII character failed.
     */
    @Test
    public void testSubmitComment() throws Exception {

        submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(this.theItemXml)),
            ENTITY_REFERENCES));
        String submittedXml = retrieve(theItemId);
        String commentString = null;
        Matcher m = Pattern.compile(":comment[^>]*>([^<]*)</").matcher(submittedXml);
        if (m.find()) {
            commentString = m.group(1);
        }
        assertEquals(ENTITY_REFERENCES, commentString);
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testReleaseBeforeSubmitItem() throws Exception {

        try {
            release(theItemId,
                getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theItemId))), null));
            fail("No exception occured on release befor submit.");
        }
        catch (final Exception e) {
            Class<?> ec = InvalidStatusException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testReleaseItem() throws Exception {

        String xml =
            submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(this.theItemXml)), null));
        String pidParam;
        if (getItemClient().getPidConfig("cmm.Item.objectPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.objectPid.releaseWithoutPid", "false")) {

            AssignParam assignPidParam = new AssignParam();
            assignPidParam.setUrl(new URL("http://somewhere/" + this.theItemId));
            pidParam = getAssignPidTaskParam(getLastModificationDateValue2(getDocument(xml)), assignPidParam);

            xml = assignObjectPid(this.theItemId, pidParam);
        }
        if (getItemClient().getPidConfig("cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.versionPid.releaseWithoutPid", "false")) {

            String latestVersion = getLatestVersionObjidValue(theItemXml);

            AssignParam assignPidParam = new AssignParam();
            assignPidParam.setUrl(new URL("http://somewhere/" + latestVersion));
            pidParam =
                getAssignPidTaskParam(getLastModificationDateValue2(getDocument(retrieve(latestVersion))),
                    assignPidParam);

            xml = assignVersionPid(latestVersion, pidParam);
        }

        release(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(xml)), null));

        xml = retrieve(theItemId);
        assertXmlExists("Properties status released", xml, XPATH_ITEM_STATUS + "[text() = 'released']");
        assertXmlExists("current-version status released", xml, XPATH_ITEM_CURRENT_VERSION_STATUS
            + "[text() = 'released']");
        assertXmlExists("Released item latest-release", xml, "/item/properties/latest-release");
        // has PID
        // assertXMLExist("Released item version pid", xml,
        // "/item/properties/latest-release/pid/text()");
        assertXmlValidItem(xml);

        // TODO include floating PID in properties of released items
        // assertXMLExist("Released item floating pid", xml,
        // "/item/properties/pid/text()");
    }

    /**
     * Related to Issue 600.
     * 
     * @throws Exception
     *             Thrown if releasing of Item with PID failed.
     */
    @Test
    public void testReleaseItemWith3PIDs() throws Exception {

        submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(this.theItemXml)), null));

        String pidParam;
        if (getItemClient().getPidConfig("cmm.Item.objectPid.setPidBeforeRelease", "true")
            && getItemClient().getPidConfig("cmm.Item.versionPid.setPidBeforeRelease", "true")) {

            // object pid
            AssignParam assignPidParam = new AssignParam();
            assignPidParam.setUrl(new URL("http://somewhere/" + this.theItemId));
            pidParam =
                getAssignPidTaskParam(getLastModificationDateValue2(getDocument(retrieve(this.theItemId))),
                    assignPidParam);

            assignObjectPid(this.theItemId, pidParam);

            // version pid
            String latestVersion = getLatestVersionObjidValue(theItemXml);

            assignPidParam.setUrl(new URL("http://somewhere/" + latestVersion));
            pidParam =
                getAssignPidTaskParam(getLastModificationDateValue2(getDocument(retrieve(latestVersion))),
                    assignPidParam);

            assignVersionPid(latestVersion, pidParam);

            String retrievedItem = retrieve(this.theItemId);

            // content pid
            String componentId =
                getIdFromRootElement(toString(selectSingleNode(getDocument(retrievedItem), "//component[1]"), true));
            componentId = getObjidFromHref(componentId);

            assignPidParam.setUrl(new URL("http://somewhere/content/" + this.theItemId + "content"));
            pidParam =
                getAssignPidTaskParam(getLastModificationDateValue2(getDocument(retrieve(this.theItemId))),
                    assignPidParam);

            assignContentPid(this.theItemId, componentId, pidParam);
        }
        else {
            fail("Can not test pid before release.");
        }

        release(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theItemId))), null));

        String xml = retrieve(theItemId);
        assertXmlExists("Properties status released", xml, XPATH_ITEM_STATUS + "[text() = 'released']");
        assertXmlExists("current-version status 'released' failed", xml, XPATH_ITEM_CURRENT_VERSION_STATUS
            + "[text() = 'released']");
        assertXmlExists("Released item latest-release", xml, "/item/properties/latest-release");
        // has PIDs
        assertXmlExists("Released item version pid missing", getDocument(xml), "/item/properties/pid/text()");
        assertXmlExists("Released item content pid missing", getDocument(xml),
            "/item/components/component/properties/pid/text()");
        assertXmlExists("Released item version pid missing", getDocument(xml),
            "/item/properties/latest-release/pid/text()");
        assertXmlValidItem(xml);
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testWithdrawBeforSubmitItem() throws Exception {

        try {
            withdraw(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theItemId))),
                null));
            fail("No exception occurred on withdraw before submit.");
        }
        catch (final Exception e) {
            Class<?> ec = NotPublishedException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testWithdrawBeforReleaseItem() throws Exception {

        submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theItemId))), null));

        try {
            withdraw(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theItemId))),
                null));
            fail("No exception occured on withdraw befor release.");
        }
        catch (final Exception e) {
            Class<?> ec = NotPublishedException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testWithdrawItem() throws Exception {

        final String xPath = "/item/properties/content-model-specific";

        String xml =
            submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(this.theItemXml)), null));
        String pidParam;
        if (getItemClient().getPidConfig("cmm.Item.objectPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.objectPid.releaseWithoutPid", "false")) {

            AssignParam assignPidParam = new AssignParam();
            assignPidParam.setUrl(new URL("http://somewhere/" + this.theItemId));
            pidParam = getAssignPidTaskParam(getLastModificationDateValue2(getDocument(xml)), assignPidParam);

            xml = assignObjectPid(this.theItemId, pidParam);
        }
        if (getItemClient().getPidConfig("cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.versionPid.releaseWithoutPid", "false")) {

            String latestVersion = getLatestVersionObjidValue(theItemXml);

            AssignParam assignPidParam = new AssignParam();
            assignPidParam.setUrl(new URL("http://somewhere/" + latestVersion));
            pidParam =
                getAssignPidTaskParam(getLastModificationDateValue2(getDocument(retrieve(latestVersion))),
                    assignPidParam);

            xml = assignVersionPid(latestVersion, pidParam);
        }

        xml = release(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(xml)), null));
        xml =
            withdraw(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(xml)), WITHDRAW_COMMENT));

        xml = retrieve(theItemId);

        assertXmlExists("Properties status withdrawn", xml, XPATH_ITEM_STATUS + "[text() = 'withdrawn']");
        assertXmlExists("current-version status must still be released", xml, XPATH_ITEM_CURRENT_VERSION_STATUS
            + "[text() = 'released']");
        // assertXmlExists("Withdrawn item comment", xml,
        // "/item/properties/current-version/comment");
        assertXmlExists("Further released withdrawn item latest-release", xml, "/item/properties/latest-release");
        assertXmlValidItem(xml);

        try {
            xml = addElement(xml, xPath + "/nix");
            xml = update(theItemId, xml);
            assertXmlExists("New version number", xml, "/item/properties/current-version/number[text() = '4']");
            assertXmlValidItem(xml);
            fail("Succesful update after withdraw.");
        }
        catch (final Exception e) {
            Class<?> ec = InvalidStatusException.class;
            EscidocAbstractTest.assertExceptionType(ec, e);
        }
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testWithdrawItemWithoutComment() throws Exception {

        submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theItemId))), null));
        String pidParam;
        if (getItemClient().getPidConfig("cmm.Item.objectPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.objectPid.releaseWithoutPid", "false")) {

            AssignParam assignPidParam = new AssignParam();
            assignPidParam.setUrl(new URL("http://somewhere/" + this.theItemId));
            pidParam =
                getAssignPidTaskParam(getLastModificationDateValue2(getDocument(retrieve(this.theItemId))),
                    assignPidParam);

            assignObjectPid(this.theItemId, pidParam);
        }
        if (getItemClient().getPidConfig("cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.versionPid.releaseWithoutPid", "false")) {

            String latestVersion = getLatestVersionObjidValue(theItemXml);

            AssignParam assignPidParam = new AssignParam();
            assignPidParam.setUrl(new URL("http://somewhere/" + latestVersion));
            pidParam =
                getAssignPidTaskParam(getLastModificationDateValue2(getDocument(retrieve(latestVersion))),
                    assignPidParam);

            assignVersionPid(latestVersion, pidParam);
        }

        release(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theItemId))), null));

        try {
            withdraw(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theItemId))),
                null));
            fail("No exception occured on withdraw without comment.");
        }
        catch (final Exception e) {
            Class<MissingMethodParameterException> ec = MissingMethodParameterException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }

    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testWithdrawNonExistingItem() throws Exception {

        try {
            withdraw("escidoc:item0", getStatusTaskParam(new DateTime(), WITHDRAW_COMMENT));
            fail("No exception occured on withdraw non existing item.");
        }
        catch (final Exception e) {
            Class<ItemNotFoundException> ec = ItemNotFoundException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);

        }

    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testSecondWithdrawItem() throws Exception {

        String xml =
            submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(this.theItemXml)), null));
        String pidParam;
        if (getItemClient().getPidConfig("cmm.Item.objectPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.objectPid.releaseWithoutPid", "false")) {

            AssignParam assignPidParam = new AssignParam();
            assignPidParam.setUrl(new URL("http://somewhere/" + this.theItemId));
            pidParam = getAssignPidTaskParam(getLastModificationDateValue2(getDocument(xml)), assignPidParam);

            xml = assignObjectPid(this.theItemId, pidParam);
        }
        if (getItemClient().getPidConfig("cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.versionPid.releaseWithoutPid", "false")) {

            String latestVersion = getLatestVersionObjidValue(theItemXml);

            AssignParam assignPidParam = new AssignParam();
            assignPidParam.setUrl(new URL("http://somewhere/" + latestVersion));
            pidParam =
                getAssignPidTaskParam(getLastModificationDateValue2(getDocument(retrieve(latestVersion))),
                    assignPidParam);

            xml = assignVersionPid(latestVersion, pidParam);
        }

        xml = release(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(xml)), null));
        xml =
            withdraw(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(xml)), WITHDRAW_COMMENT));

        try {
            withdraw(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(xml)), WITHDRAW_COMMENT));
            fail("No exception occured on second withdraw.");
        }
        catch (final Exception e) {
            Class<AlreadyWithdrawnException> ec = AlreadyWithdrawnException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }

    }

    /**
     * Test successful revising an item.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvi1() throws Exception {

        String xml =
            submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(this.theItemXml)), null));
        DateTime t1 = getLastModificationDateValue2(getDocument(xml));

        xml = revise(theItemId, getStatusTaskParam(t1, null));
        DateTime t2 = getLastModificationDateValue2(getDocument(xml));

        assertTrue("Timestamp of submitted not after pending", t1.compareTo(t2) < 0);

        final Document revisedDocument = EscidocAbstractTest.getDocument(retrieve(theItemId));
        DateTime t2a = getLastModificationDateValue2(revisedDocument);

        assertTrue("Timestamp not equal", t2.compareTo(t2a) == 0);

        assertXmlEquals("Unexpected status. ", revisedDocument, XPATH_ITEM_STATUS, STATE_IN_REVISION);
        assertXmlEquals("Unexpected current version status", revisedDocument, XPATH_ITEM_CURRENT_VERSION_STATUS,
            STATE_IN_REVISION);
    }

    /**
     * Test declining revising an item in state pending.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvi2() throws Exception {

        try {
            revise(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(this.theItemXml)), null));
            EscidocAbstractTest.failMissingException(InvalidStatusException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Revising failed with unexpected exception. ",
                InvalidStatusException.class, e);
        }
    }

    /**
     * Test declining revising an item in state released.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvi3() throws Exception {

        submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(this.theItemXml)), null));
        String pidParam;

        if (getItemClient().getPidConfig("cmm.Item.objectPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.objectPid.releaseWithoutPid", "false")) {

            AssignParam assignPidParam = new AssignParam();
            assignPidParam.setUrl(new URL("http://somewhere/" + this.theItemId));
            pidParam =
                getAssignPidTaskParam(getLastModificationDateValue2(getDocument(retrieve(this.theItemId))),
                    assignPidParam);

            assignObjectPid(this.theItemId, pidParam);
        }
        if (getItemClient().getPidConfig("cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.versionPid.releaseWithoutPid", "false")) {

            String latestVersion = getLatestVersionObjidValue(theItemXml);

            AssignParam assignPidParam = new AssignParam();
            assignPidParam.setUrl(new URL("http://somewhere/" + latestVersion));
            pidParam =
                getAssignPidTaskParam(getLastModificationDateValue2(getDocument(retrieve(latestVersion))),
                    assignPidParam);

            assignVersionPid(latestVersion, pidParam);
        }

        String xml =
            release(theItemId,
                getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theItemId))), null));

        try {
            revise(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(xml)), null));
            EscidocAbstractTest.failMissingException(InvalidStatusException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Revising failed with unexpected exception. ",
                InvalidStatusException.class, e);
        }
    }

    /**
     * Test declining revising an item in state withdrawn.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvi4() throws Exception {

        submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(this.theItemXml)), null));
        String pidParam;
        if (getItemClient().getPidConfig("cmm.Item.objectPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.objectPid.releaseWithoutPid", "false")) {

            AssignParam assignPidParam = new AssignParam();
            assignPidParam.setUrl(new URL("http://somewhere/" + this.theItemId));
            pidParam =
                getAssignPidTaskParam(getLastModificationDateValue2(getDocument(retrieve(this.theItemId))),
                    assignPidParam);

            assignObjectPid(this.theItemId, pidParam);
        }
        if (getItemClient().getPidConfig("cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.versionPid.releaseWithoutPid", "false")) {

            String latestVersion = getLatestVersionObjidValue(theItemXml);

            AssignParam assignPidParam = new AssignParam();
            assignPidParam.setUrl(new URL("http://somewhere/" + latestVersion));
            pidParam =
                getAssignPidTaskParam(getLastModificationDateValue2(getDocument(retrieve(latestVersion))),
                    assignPidParam);

            assignVersionPid(latestVersion, pidParam);
        }

        String xml =
            release(theItemId,
                getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theItemId))), null));
        xml =
            withdraw(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(xml)), WITHDRAW_COMMENT));

        try {
            revise(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(xml)), null));
            EscidocAbstractTest.failMissingException(InvalidStatusException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Revising failed with unexpected exception. ",
                InvalidStatusException.class, e);
        }
    }

    /**
     * Test declining revising an unknown item.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvi5() throws Exception {

        try {
            revise(UNKNOWN_ID, getStatusTaskParam(new DateTime(), null));
            EscidocAbstractTest.failMissingException(ItemNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Revising unknown item failed with unexpected exception. ",
                ItemNotFoundException.class, e);
        }
    }

    /**
     * Test declining revising an item without providing an item id.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvi6() throws Exception {

        try {
            revise(null, getStatusTaskParam(getLastModificationDateValue2(getDocument(this.theItemXml)), null));
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Revising without id failed with unexpected exception. ",
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining revising an item without providing task param.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test(expected = MissingMethodParameterException.class)
    public void testOMRvi7() throws Exception {

        submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(this.theItemXml)), null));
        revise(theItemId, null);
    }

    /**
     * Test declining revising an item without providing last modification date in task param.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test(expected = XmlSchemaValidationException.class)
    public void testOMRvi8() throws Exception {

        submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(this.theItemXml)), null));
        revise(theItemId, getStatusTaskParam(null, null));
    }

    /**
     * Test declining revising an item with corrupted task param.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test(expected = XmlCorruptedException.class)
    public void testOMRvi9() throws Exception {

        submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(this.theItemXml)), null));
        revise(theItemId, "<param");
    }

    /**
     * Test declining revising an item with providing outdated last modification date in task param.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test(expected = OptimisticLockingException.class)
    public void testOMRvi10() throws Exception {

        DateTime lmd = getLastModificationDateValue2(getDocument(this.theItemXml));
        submit(theItemId, getStatusTaskParam(lmd, null));
        revise(theItemId, getStatusTaskParam(lmd, null));
    }

    /**
     * Test successful revising an item (created by a depositor) by an administrator.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvi11() throws Exception {

        try {
            // create and submit item by a depositor
            PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
            final String toBeCreatedXml =
                EscidocAbstractTest
                    .getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
            theItemXml = create(toBeCreatedXml);
            theItemId = getObjidValue(theItemXml);

            String xml =
                submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(this.theItemXml)), null));

            // revise the item by an administrator
            PWCallback.setHandle(PWCallback.ADMINISTRATOR_HANDLE);

            xml = revise(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(xml)), null));

            // retrieve, update and submit the item by the depositor
            PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
            theItemXml = retrieve(theItemId);
            theItemXml.replaceFirst("", "");
            theItemXml = update(theItemId, theItemXml);
            submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(this.theItemXml)), null));
        }
        finally {
            PWCallback.resetHandle();
        }
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testUpdateAfterReleaseItem() throws Exception {
        submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theItemId))), null));
        String pidParam;
        if (getItemClient().getPidConfig("cmm.Item.objectPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.objectPid.releaseWithoutPid", "false")) {

            AssignParam assignPidParam = new AssignParam();
            assignPidParam.setUrl(new URL("http://somewhere/" + this.theItemId));
            pidParam =
                getAssignPidTaskParam(getLastModificationDateValue2(getDocument(retrieve(this.theItemId))),
                    assignPidParam);

            assignObjectPid(this.theItemId, pidParam);
        }
        if (getItemClient().getPidConfig("cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.versionPid.releaseWithoutPid", "false")) {

            String latestVersion = getLatestVersionObjidValue(theItemXml);

            AssignParam assignPidParam = new AssignParam();
            assignPidParam.setUrl(new URL("http://somewhere/" + latestVersion));
            pidParam =
                getAssignPidTaskParam(getLastModificationDateValue2(getDocument(retrieve(latestVersion))),
                    assignPidParam);

            assignVersionPid(latestVersion, pidParam);
        }

        release(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theItemId))), null));

        String xml = retrieve(theItemId);
        assertXmlExists("Properties status released", xml, XPATH_ITEM_STATUS + "[text() = 'released']");
        assertXmlExists("version status released", xml, XPATH_ITEM_CURRENT_VERSION_STATUS + "[text() = 'released']");
        assertXmlExists("Released item latest-release", xml, "/item/properties/latest-release");

        Document newItem = EscidocAbstractTest.getDocument(xml);
        selectSingleNode(newItem, "/item/properties/content-model-specific").appendChild(
            newItem.createElement("nischt"));
        xml = update(theItemId, toString(newItem, false));
        Document xmlDoc = EscidocAbstractTest.getDocument(xml);

        assertXmlValidItem(xml);
        assertXmlExists("Properties status not 'released' after update of released item. ", xml, XPATH_ITEM_STATUS
            + "[text() = 'released']");
        assertXmlExists("version status not 'pending' after update of released item. ", xml,
            XPATH_ITEM_CURRENT_VERSION_STATUS + "[text() = 'pending']");
        assertXmlExists("No latest-release element after update of release item. ", xml,
            "/item/properties/latest-release");
        String versionNumber = selectSingleNode(xmlDoc, XPATH_ITEM_CURRENT_VERSION + "/number/text()").getNodeValue();
        String releaseNumber = selectSingleNode(xmlDoc, XPATH_ITEM_LATEST_RELEASE + "/number/text()").getNodeValue();
        assertEquals("Latest release version is not the one before latest version after update of released item. ",
            Integer.parseInt(versionNumber), Integer.parseInt(releaseNumber) + 1);

    }

    /**
     * Test Item structure in lifecycle.
     * <p/>
     * version 1: Create Item
     */
    @Test
    public void testElementsAfterUpdate01() throws Exception {

        String xmlData =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "item_without_component.xml");
        this.theItemXml = create(xmlData);
        this.theItemId = getObjidValue(theItemXml);

        // Assert Created ItemXML
        assertXmlExists("Wrong version number", this.theItemXml, "/item/properties/version/number[text() = '1']");
        assertXmlExists("Wrong public-status", this.theItemXml, "/item/properties/public-status[text() = 'pending']");
        assertXmlExists("Wrong version status", this.theItemXml, "/item/properties/version/status[text() = 'pending']");
        assertXmlNotExists("Wrong number of Components", this.theItemXml, "/item/components/component");

        // retrieve with object ref (latest version for author)
        String retrievedItemXml = retrieve(this.theItemId);
        assertXmlExists("Wrong version number", retrievedItemXml, "/item/properties/version/number[text() = '1']");
        assertXmlExists("Wrong public-status", retrievedItemXml, "/item/properties/public-status[text() = 'pending']");
        assertXmlExists("Wrong version status", retrievedItemXml, "/item/properties/version/status[text() = 'pending']");
        assertXmlNotExists("Wrong number of Components", retrievedItemXml, "/item/components/component");

        /*
         * Retrieve as anonymous
         */
        PWCallback.setAnonymousHandle();
        try {
            retrieve(this.theItemId);
            fail("Retrieve of unreleased Item is possible for anonymous. ");
            retrieve(this.theItemId + ":1");
            fail("Retrieve of unreleased Item is possible for anonymous. ");
        }
        catch (final Exception e) {
            Class<AuthorizationException> ec = AuthorizationException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * Test Item structure in lifecycle.
     * <p/>
     * created (version 1) ->update (add component) (version 2)
     */
    @Test
    public void testElementsAfterUpdate02() throws Exception {

        // prepare object
        testElementsAfterUpdate01();
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);

        String xml = addComponent(this.theItemXml);
        this.theItemXml = update(theItemId, xml);

        // Assert Created ItemXML
        assertXmlExists("Wrong version number", this.theItemXml, "/item/properties/version/number[text() = '2']");
        assertXmlExists("Wrong public-status", this.theItemXml, "/item/properties/public-status[text() = 'pending']");
        assertXmlExists("Wrong version status", this.theItemXml, "/item/properties/version/status[text() = 'pending']");
        assertXmlExists("Wrong number of Components", this.theItemXml, "/item/components[count(./component)= '1']");

        // retrieve latest version (role: author)
        String retrievedItemXml = retrieve(this.theItemId);
        assertXmlExists("Wrong version number", retrievedItemXml, "/item/properties/version/number[text() = '2']");
        assertXmlExists("Wrong public-status", retrievedItemXml, "/item/properties/public-status[text() = 'pending']");
        assertXmlExists("Wrong version status", retrievedItemXml, "/item/properties/version/status[text() = 'pending']");
        assertXmlExists("Wrong number of Components", retrievedItemXml, "/item/components[count(./component)= '1']");

        // retrieve version 1 (role: author)
        retrievedItemXml = retrieve(this.theItemId + ":1");
        assertXmlExists("Wrong version number", retrievedItemXml, "/item/properties/version/number[text() = '1']");
        assertXmlExists("Wrong public-status", retrievedItemXml, "/item/properties/public-status[text() = 'pending']");
        assertXmlExists("Wrong version status", retrievedItemXml, "/item/properties/version/status[text() = 'pending']");
        assertXmlNotExists("Wrong number of Components", retrievedItemXml, "/item/components/component");

        // retrieve version 2 (role: author)
        retrievedItemXml = retrieve(this.theItemId + ":2");
        assertXmlExists("Wrong version number", retrievedItemXml, "/item/properties/version/number[text() = '2']");
        assertXmlExists("Wrong public-status", retrievedItemXml, "/item/properties/public-status[text() = 'pending']");
        assertXmlExists("Wrong version status", retrievedItemXml, "/item/properties/version/status[text() = 'pending']");
        assertXmlExists("Wrong number of Components", retrievedItemXml, "/item/components[count(./component)= '1']");

        // retrieve version 3 (role: author)
        try {
            retrieve(this.theItemId + ":3");
            fail("Updating Item leads to more than one new version. ");
        }
        catch (final Exception e) {
            Class<ItemNotFoundException> ec = ItemNotFoundException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }

        /*
         * Retrieve as anonymous
         */
        PWCallback.setAnonymousHandle();
        try {
            retrieve(this.theItemId);
            retrieve(this.theItemId + ":1");
            retrieve(this.theItemId + ":2");
            fail("Retrieve of unreleased Item is possible for anonymous. ");
        }
        catch (final Exception e) {
            Class<AuthorizationException> ec = AuthorizationException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * Test Item structure in lifecycle.
     * <p/>
     * version 1: Created
     * <p/>
     * version 2: updated (add Component) ->released
     */
    @Test
    public void testElementsAfterUpdate03() throws Exception {

        // prepare object
        testElementsAfterUpdate02();
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);

        submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theItemId))), null));
        releaseWithPid(this.theItemId);

        // retrieve latest version (role: author)
        String retrievedItemXml = retrieve(this.theItemId);
        assertXmlExists("Wrong version number", retrievedItemXml, "/item/properties/version/number[text() = '2']");
        assertXmlExists("Wrong public-status", retrievedItemXml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", retrievedItemXml,
            "/item/properties/version/status[text() = 'released']");
        assertXmlExists("Wrong latest-release number", retrievedItemXml,
            "/item/properties/latest-release/number[text() = '2']");
        assertXmlExists("Wrong number of Components", retrievedItemXml, "/item/components[count(./component)= '1']");

        // retrieve version 1 (role: author)
        retrievedItemXml = retrieve(this.theItemId + ":1");
        assertXmlExists("Wrong version number", retrievedItemXml, "/item/properties/version/number[text() = '1']");
        assertXmlExists("Wrong public-status", retrievedItemXml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", retrievedItemXml, "/item/properties/version/status[text() = 'pending']");
        assertXmlNotExists("Wrong number of Components", retrievedItemXml, "/item/components/component");
        assertXmlExists("Wrong latest-release number", retrievedItemXml,
            "/item/properties/latest-release/number[text() = '2']");

        // retrieve version 2 (role: author)
        retrievedItemXml = retrieve(this.theItemId + ":2");
        assertXmlExists("Wrong version number", retrievedItemXml, "/item/properties/version/number[text() = '2']");
        assertXmlExists("Wrong public-status", retrievedItemXml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", retrievedItemXml,
            "/item/properties/version/status[text() = 'released']");
        assertXmlExists("Wrong latest-release number", retrievedItemXml,
            "/item/properties/latest-release/number[text() = '2']");
        assertXmlExists("Wrong number of Components", retrievedItemXml, "/item/components[count(./component)= '1']");

        // retrieve version 3 (role: author)
        try {
            retrieve(this.theItemId + ":3");
            fail("Updating Item leads to more than one new version. ");
        }
        catch (final Exception e) {
            Class<ItemNotFoundException> ec = ItemNotFoundException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }

        /*
         * Retrieve as anonymous
         */
        PWCallback.setAnonymousHandle();

        // retrieve latest version (role: author)
        retrievedItemXml = retrieve(this.theItemId);
        assertXmlExists("Wrong version number", retrievedItemXml, "/item/properties/version/number[text() = '2']");
        assertXmlExists("Wrong public-status", retrievedItemXml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", retrievedItemXml,
            "/item/properties/version/status[text() = 'released']");
        assertXmlExists("Wrong latest-release number", retrievedItemXml,
            "/item/properties/latest-release/number[text() = '2']");
        assertXmlExists("Wrong number of Components", retrievedItemXml, "/item/components[count(./component)= '1']");

        // retrieve version 1 (role: anonymous)
        try {
            retrieve(this.theItemId + ":1");
            fail("Retrieve of unreleased Item is possible for anonymous. ");
        }
        catch (final Exception e) {
            Class<AuthorizationException> ec = AuthorizationException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }

        // retrieve version 2 (role: anonymous)
        retrievedItemXml = retrieve(this.theItemId + ":2");
        assertXmlExists("Wrong version number", retrievedItemXml, "/item/properties/version/number[text() = '2']");
        assertXmlExists("Wrong public-status", retrievedItemXml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", retrievedItemXml,
            "/item/properties/version/status[text() = 'released']");
        assertXmlExists("Wrong latest-release number", retrievedItemXml,
            "/item/properties/latest-release/number[text() = '2']");
        assertXmlExists("Wrong number of Components", retrievedItemXml, "/item/components[count(./component)= '1']");

    }

    /**
     * Test Item structure in lifecycle.
     * <p/>
     * version 1: Created
     * <p/>
     * version 2: updated->released
     * <p/>
     * version 3: updated
     */
    @Test
    public void testElementsAfterUpdate04() throws Exception {

        // prepare object
        testElementsAfterUpdate03();
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);

        String releasedItemXml = retrieve(this.theItemId);
        String xml = addComponent(releasedItemXml);
        String updatedItemXml = update(theItemId, xml);

        // check updated Item XML
        assertXmlExists("New version number", updatedItemXml, "/item/properties/version/number[text() = '3']");
        assertXmlExists("Properties status not released", updatedItemXml,
            "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Version status not pending", updatedItemXml,
            "/item/properties/version/status[text() = 'pending']");
        // Count components
        assertXmlExists("Wrong number of Components", updatedItemXml, "/item/components[count(./component)= '2']");

        /*
         * Check as author
         */
        // retrieve latest version (role: author)
        String retrievedItemXml = retrieve(this.theItemId);
        assertXmlExists("Wrong version number", retrievedItemXml, "/item/properties/version/number[text() = '3']");
        assertXmlExists("Wrong public-status", retrievedItemXml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", retrievedItemXml, "/item/properties/version/status[text() = 'pending']");
        assertXmlExists("Wrong latest-release number", retrievedItemXml,
            "/item/properties/latest-release/number[text() = '2']");
        assertXmlExists("Wrong number of Components", retrievedItemXml, "/item/components[count(./component)= '2']");

        // retrieve version 1 (role: author)
        retrievedItemXml = retrieve(this.theItemId + ":1");
        assertXmlExists("Wrong version number", retrievedItemXml, "/item/properties/version/number[text() = '1']");
        assertXmlExists("Wrong public-status", retrievedItemXml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", retrievedItemXml, "/item/properties/version/status[text() = 'pending']");
        assertXmlNotExists("Wrong number of Components", retrievedItemXml, "/item/components/component");
        assertXmlExists("Wrong latest-release number", retrievedItemXml,
            "/item/properties/latest-release/number[text() = '2']");

        // retrieve version 2 (role: author)
        retrievedItemXml = retrieve(this.theItemId + ":2");
        assertXmlExists("Wrong version number", retrievedItemXml, "/item/properties/version/number[text() = '2']");
        assertXmlExists("Wrong public-status", retrievedItemXml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", retrievedItemXml,
            "/item/properties/version/status[text() = 'released']");
        assertXmlExists("Wrong latest-release number", retrievedItemXml,
            "/item/properties/latest-release/number[text() = '2']");
        assertXmlExists("Wrong number of Components", retrievedItemXml, "/item/components[count(./component)= '1']");

        // retrieve version 3 (role: author)
        retrievedItemXml = retrieve(this.theItemId + ":3");
        assertXmlExists("Wrong version number", retrievedItemXml, "/item/properties/version/number[text() = '3']");
        assertXmlExists("Wrong public-status", retrievedItemXml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", retrievedItemXml, "/item/properties/version/status[text() = 'pending']");
        assertXmlExists("Wrong latest-release number", retrievedItemXml,
            "/item/properties/latest-release/number[text() = '2']");
        assertXmlExists("Wrong number of Components", retrievedItemXml, "/item/components[count(./component)= '2']");

        // retrieve version 4 (role: author)
        try {
            retrieve(this.theItemId + ":4");
            fail("Updating Item leads to more than one new version. ");
        }
        catch (final Exception e) {
            Class<ItemNotFoundException> ec = ItemNotFoundException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }

        /*
         * Retrieve as anonymous
         */
        PWCallback.setAnonymousHandle();

        // retrieve latest version (role: anonymous)
        retrievedItemXml = retrieve(this.theItemId);
        assertXmlExists("Wrong version number", retrievedItemXml, "/item/properties/version/number[text() = '2']");
        assertXmlExists("Wrong public-status", retrievedItemXml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", retrievedItemXml,
            "/item/properties/version/status[text() = 'released']");
        assertXmlExists("Wrong latest-release number", retrievedItemXml,
            "/item/properties/latest-release/number[text() = '2']");
        assertXmlExists("Wrong number of Components", retrievedItemXml, "/item/components[count(./component)= '1']");

        // retrieve version 1 (role: anonymous)
        try {
            retrieve(this.theItemId + ":1");
            fail("Retrieve of unreleased Item is possible for anonymous.");
        }
        catch (final Exception e) {
            Class<AuthorizationException> ec = AuthorizationException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }

        // retrieve version 2 (role: anonymous)
        retrievedItemXml = retrieve(this.theItemId + ":2");
        assertXmlExists("Wrong version number", retrievedItemXml, "/item/properties/version/number[text() = '2']");
        assertXmlExists("Wrong public-status", retrievedItemXml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", retrievedItemXml,
            "/item/properties/version/status[text() = 'released']");
        assertXmlExists("Wrong latest-release number", retrievedItemXml,
            "/item/properties/latest-release/number[text() = '2']");
        assertXmlExists("Wrong number of Components", retrievedItemXml, "/item/components[count(./component)= '1']");

        // retrieve version 3 (role: anonymous)
        try {
            retrieve(this.theItemId + ":3");
            fail("Retrieve of unreleased Item is possible for anonymous. ");
        }
        catch (final Exception e) {
            Class<AuthorizationException> ec = AuthorizationException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }

    }

    /**
     * Retrieving of an post released Item deliveres wrong version of Item (properties) back. See Bug #697.
     */
    @Test
    public void testBug697() throws Exception {

        String xmlData =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "item_without_component.xml");
        this.theItemXml = create(xmlData);
        this.theItemId = getObjidValue(theItemXml);

        submit(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theItemId))), null));
        String pidParam;
        if (getItemClient().getPidConfig("cmm.Item.objectPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.objectPid.releaseWithoutPid", "false")) {

            AssignParam assignPidParam = new AssignParam();
            assignPidParam.setUrl(new URL("http://somewhere/" + this.theItemId));
            pidParam =
                getAssignPidTaskParam(getLastModificationDateValue2(getDocument(retrieve(this.theItemId))),
                    assignPidParam);

            assignObjectPid(this.theItemId, pidParam);
        }
        if (getItemClient().getPidConfig("cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.versionPid.releaseWithoutPid", "false")) {

            String latestVersion = getLatestVersionObjidValue(theItemXml);

            AssignParam assignPidParam = new AssignParam();
            assignPidParam.setUrl(new URL("http://somewhere/" + latestVersion));
            pidParam =
                getAssignPidTaskParam(getLastModificationDateValue2(getDocument(retrieve(latestVersion))),
                    assignPidParam);

            assignVersionPid(latestVersion, pidParam);
        }

        release(theItemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theItemId))), null));

        // retrieve with object ref (latest version for author)
        String releasedItemXml = retrieve(theItemId);
        assertXmlExists("Wrong version number", releasedItemXml, "/item/properties/version/number[text() = '1']");
        assertXmlExists("Properties status not released", releasedItemXml,
            "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status pending", releasedItemXml,
            "/item/properties/version/status[text() = 'released']");
        assertXmlNotExists("Wrong number of Components", releasedItemXml, "/item/components/component");

        String xml = addComponent(releasedItemXml);
        String updatedItemXml = update(theItemId, xml);

        // checking some values
        assertXmlExists("New version number", updatedItemXml, "/item/properties/version/number[text() = '2']");
        assertXmlExists("Properties status not released", updatedItemXml,
            "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Version status not pending", updatedItemXml,
            "/item/properties/version/status[text() = 'pending']");
        // Count components
        assertXmlExists("Wrong number of Components", updatedItemXml, "/item/components[count(./component)= '1']");
        // TODO check components in detail

        /*
         * checking retrieve Item.
         */

        // checking author role
        releasedItemXml = retrieve(theItemId);
        assertXmlExists("Wrong version number", releasedItemXml, "/item/properties/version/number[text() = '2']");
        assertXmlExists("Properties status not released", releasedItemXml,
            "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status pending", releasedItemXml,
            "/item/properties/version/status[text() = 'pending']");
        assertXmlExists("Wrong number of Components", releasedItemXml, "/item/components[count(./component)= '1']");
        // TODO check components in detail

        // checking version 1
        releasedItemXml = retrieve(theItemId + ":1");
        assertXmlExists("Wrong version number", releasedItemXml, "/item/properties/version/number[text() = '1']");
        assertXmlExists("Properties status not released", releasedItemXml,
            "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status released", releasedItemXml,
            "/item/properties/version/status[text() = 'released']");
        assertXmlNotExists("Wrong number of Components", releasedItemXml, "/item/components/component");

        /*
         * ---- anonymous -----------------------------------
         */
        // retrieve as anonymous
        PWCallback.setAnonymousHandle();
        String anonymousXml = retrieve(theItemId);
        assertXmlExists("Wrong version number", anonymousXml, "/item/properties/version/number[text() = '1']");
        assertXmlExists("Properties status not released", anonymousXml,
            "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Version status not released", anonymousXml,
            "/item/properties/version/status[text() = 'released']");
    }

    /**
     * Test declining retrieving of released item with component visibility "private".
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRContentVisibilityPrivate() throws Exception {
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        Document item =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        Node itemChanged = substitute(item, "/item/components/component/properties/visibility", "private");
        String itemXml = toString(itemChanged, false);
        String cretaedItem = create(itemXml);
        Document itemDocument = getDocument(cretaedItem);
        String componentId =
            selectSingleNode(itemDocument, "/item/components/component[properties/visibility='private']/@href")
                .getNodeValue();
        componentId = getIdFromHrefValue(componentId);
        String itemId = getObjidValue(cretaedItem);
        String xml = submit(itemId, getStatusTaskParam(getLastModificationDateValue2(itemDocument), null));
        String pidParam;
        if (getItemClient().getPidConfig("cmm.Item.objectPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.objectPid.releaseWithoutPid", "false")) {

            AssignParam assignPidParam = new AssignParam();
            assignPidParam.setUrl(new URL("http://somewhere/" + itemId));
            pidParam = getAssignPidTaskParam(getLastModificationDateValue2(getDocument(xml)), assignPidParam);

            xml = assignObjectPid(itemId, pidParam);
        }
        if (getItemClient().getPidConfig("cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.versionPid.releaseWithoutPid", "false")) {

            String latestVersion = getLatestVersionObjidValue(cretaedItem);

            AssignParam assignPidParam = new AssignParam();
            assignPidParam.setUrl(new URL("http://somewhere/" + latestVersion));
            pidParam =
                getAssignPidTaskParam(getLastModificationDateValue2(getDocument(retrieve(latestVersion))),
                    assignPidParam);

            xml = assignVersionPid(latestVersion, pidParam);
        }

        release(itemId, getStatusTaskParam(getLastModificationDateValue2(getDocument(xml)), null));

        // PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        PWCallback.setHandle("");
        try {
            retrieveContent(itemId, componentId);
            fail("No AuthorizationException retrieving " + "item with component visibility 'private'.");
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(AuthorizationException.class, e);
        }

    }

}
