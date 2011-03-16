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
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.remote.application.violated.AlreadyWithdrawnException;
import de.escidoc.core.common.exceptions.remote.application.violated.NotPublishedException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.security.client.PWCallback;
import org.apache.xpath.XPathAPI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test the mock implementation of the item resource.
 * 
 * @author MSC
 * 
 */
@RunWith(value = Parameterized.class)
public class ItemLifecycleTest extends ItemTestBase {

    private String theItemXml;

    private String theItemId;

    /**
     * @param transport
     *            The transport identifier.
     */
    public ItemLifecycleTest(final int transport) {
        super(transport);
    }

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
            EscidocRestSoapTestBase.getTemplateAsString(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
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
     * @test.name Submit Item - Pending
     * @test.id OM_SI_1
     * @test.input <ul>
     *             <li>id of an existing item in state pending</li>
     *             <li>timestamp of the last modification of the item</li>
     *             </ul>
     * @test.expected: No result, no exception, item has been submitted, Last
     *                 modification date of item has been updated.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOmSi1() throws Exception {

        String paramXml = getTheLastModificationParam(false);
        final Document paramDocument =
            EscidocRestSoapTestBase.getDocument(paramXml);
        final String pendingLastModificationDate =
            getLastModificationDateValue(paramDocument);

        try {
            submit(theItemId, paramXml);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Submitting the pending Item failed. ", e);
        }

        String submittedXml = null;
        try {
            submittedXml = retrieve(theItemId);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Retrieving the revised, submitted item failed. ", e);
        }
        final Document submittedDocument =
            EscidocRestSoapTestBase.getDocument(submittedXml);
        assertDateBeforeAfter(pendingLastModificationDate,
            getLastModificationDateValue(submittedDocument));
        assertXmlEquals("Unexpected status. ", submittedDocument,
            XPATH_ITEM_STATUS, STATE_SUBMITTED);
        assertXmlEquals("Unexpected current version status", submittedDocument,
            XPATH_ITEM_CURRENT_VERSION_STATUS, STATE_SUBMITTED);

        // check timestamps consistency ==================================

        // check timestamps within Item XML-------------------------------
        // /item/@last-modification-date == /item/properties/version/date
        assertEquals(
            "last-modification-date in root attribute of Item [" + theItemId
                + "] differs from //version/date",
            XPathAPI.selectSingleNode(submittedDocument,
                "/item/@last-modification-date").getTextContent(),
            XPathAPI.selectSingleNode(submittedDocument,
                "/item/properties/version/date").getTextContent());

        // /item/@last-modification-date == /item/properties/latest-version/date
        assertEquals(
            "last-modification-date in root attribute of Item [" + theItemId
                + "] differs from //latest-version/date",
            XPathAPI.selectSingleNode(submittedDocument,
                "/item/@last-modification-date").getTextContent(),
            XPathAPI.selectSingleNode(submittedDocument,
                "/item/properties/latest-version/date").getTextContent());

        // check timestamps within Version History XML -------------------
        Document wovDocV1E2 =
            EscidocRestSoapTestBase
                .getDocument(retrieveVersionHistory(theItemId));

        // /version-history/version[version-number='1']/events/event[1]
        // /eventDateTime ==
        // /version-history/version[version-number='1']/@timestamp
        assertEquals(
            "eventDateTime of the latest event of version 1 differs "
                + "from timestamp attribute of version 1 [" + theItemId + "]",
            XPathAPI.selectSingleNode(
                wovDocV1E2,
                "/version-history/version[version-number='1']"
                    + "/events/event[2]/eventDateTime").getTextContent(),
            XPathAPI
                .selectSingleNode(wovDocV1E2,
                    "/version-history/version[version-number='1']/@timestamp")
                .getTextContent());

        // check timestamps between Item XML and Version History XML -----
        // /item/@last-modification-date ==
        // /version-history/@last-modification-date
        assertEquals(
            "last-modification-date in root attribute of Item [" + theItemId
                + "] differs from last-modification-date "
                + "attribute of version-history",
            XPathAPI.selectSingleNode(submittedDocument,
                "/item/@last-modification-date").getTextContent(),
            XPathAPI.selectSingleNode(wovDocV1E2,
                "/version-history/@last-modification-date").getTextContent());

        // /version-history/version[version-number='1']/@timestamp ==
        // /item/properties/creation-date
        assertEquals(
            "last-modification-date in root attribute of Item [" + theItemId
                + "] differs from creation date of version",
            XPathAPI
                .selectSingleNode(wovDocV1E2,
                    "/version-history/version[version-number='1']/@timestamp")
                .getTextContent(),
            XPathAPI.selectSingleNode(submittedDocument,
                "/item/properties/creation-date").getTextContent());

        // /version-history/version[version-number='1']/timestamp ==
        // /item/@last-modification-date
        assertEquals(
            "last-modification-date in root attribute of Item [" + theItemId
                + "] differs from timestamp of version 1 "
                + "in version-history",
            XPathAPI
                .selectSingleNode(wovDocV1E2,
                    "/version-history/version[version-number='1']/timestamp")
                .getTextContent(),
            XPathAPI.selectSingleNode(submittedDocument,
                "/item/@last-modification-date").getTextContent());

    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testSubmitAfterRelease() throws Exception {

        final String xPath = "/item/properties/content-model-specific";
        final Document paramDocument =
            EscidocRestSoapTestBase
                .getDocument(getTheLastModificationParam(false));
        final String pendingLastModificationDate =
            getLastModificationDateValue(paramDocument);

        submit(theItemId, getTheLastModificationParam(false));
        String pidParam;
        if (getItemClient().getPidConfig(
            "cmm.Item.objectPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig(
                "cmm.Item.objectPid.releaseWithoutPid", "false")) {
            pidParam =
                getPidParam(this.theItemId, "http://somewhere" + this.theItemId);
            assignObjectPid(this.theItemId, pidParam);
        }
        if (getItemClient().getPidConfig(
            "cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig(
                "cmm.Item.versionPid.releaseWithoutPid", "false")) {
            String latestVersion = getLatestVersionObjidValue(theItemXml);
            pidParam =
                getPidParam(latestVersion, "http://somewhere" + latestVersion);
            assignVersionPid(latestVersion, pidParam);
        }

        release(theItemId, getTheLastModificationParam(false));
        String xml = addElement(retrieve(theItemId), xPath + "/nix");
        assertXmlValidItem(xml);
        update(theItemId, xml);
        submit(theItemId, getTheLastModificationParam(false));

        String submittedXml = null;
        try {
            submittedXml = retrieve(theItemId);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Retrieving the revised, submitted item failed. ", e);
        }
        final Document submittedDocument =
            EscidocRestSoapTestBase.getDocument(submittedXml);
        assertDateBeforeAfter(pendingLastModificationDate,
            getLastModificationDateValue(submittedDocument));
        assertXmlEquals("Unexpected status. ", submittedDocument,
            XPATH_ITEM_STATUS, STATE_RELEASED);
        assertXmlEquals("Unexpected current version status", submittedDocument,
            XPATH_ITEM_CURRENT_VERSION_STATUS, STATE_SUBMITTED);

    }

    /**
     * Test for Jira INFR-1020.
     * 
     * @throws Exception
     */
    @Test(expected = InvalidStatusException.class)
    public void testSubmitAfterSubmit() throws Exception {
        final String xPath = "/item/properties/content-model-specific";
        String xml = addElement(retrieve(theItemId), xPath + "/nix");

        assertXmlValidItem(xml);
        update(theItemId, xml);
        submit(theItemId, getTheLastModificationParam(false));

        submit(theItemId, getTheLastModificationParam(false));
    }

    /**
     * Test successful submitting an item in state "in-revision".
     * 
     * @test.name Submit Item - In Revision
     * @test.id OM_SI_1-2
     * @test.input <ul>
     *             <li>id of an existing item in state in-revision</li>
     *             <li>timestamp of the last modification of the item</li>
     *             </ul>
     * @test.expected: No result, no exception, item has been submitted, Last
     *                 modification date of item has been updated.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOmSi1_2() throws Exception {

        String paramXml = getTheLastModificationParam(false, theItemId);
        submit(theItemId, paramXml);
        paramXml = getTheLastModificationParam(false, theItemId);
        revise(theItemId, paramXml);
        paramXml = getTheLastModificationParam(false, theItemId);
        paramXml = getTheLastModificationParam(false);
        final Document paramDocument =
            EscidocRestSoapTestBase.getDocument(paramXml);
        final String revisedLastModificationDate =
            getLastModificationDateValue(paramDocument);

        try {
            submit(theItemId, paramXml);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Submitting the pending container failed. ", e);
        }

        String submittedXml = null;
        try {
            submittedXml = retrieve(theItemId);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Retrieving the revised, submitted item failed. ", e);
        }
        final Document submittedDocument =
            EscidocRestSoapTestBase.getDocument(submittedXml);
        assertDateBeforeAfter(revisedLastModificationDate,
            getLastModificationDateValue(submittedDocument));
        assertXmlEquals("Unexpected status. ", submittedDocument,
            XPATH_ITEM_STATUS, STATE_SUBMITTED);
        assertXmlEquals("Unexpected current version status", submittedDocument,
            XPATH_ITEM_CURRENT_VERSION_STATUS, STATE_SUBMITTED);
    }

    /**
     * Test handling of non-ASCII character within submit comment.
     * 
     * @throws Exception
     *             Thrown if escaping of non-ASCII character failed.
     */
    @Test
    public void testSubmitComment() throws Exception {

        String paramXml = getTheLastModificationParam(ENTITY_REFERENCES);

        submit(theItemId, paramXml);
        String submittedXml = retrieve(theItemId);
        String commentString = null;
        Matcher m =
            Pattern.compile(":comment[^>]*>([^<]*)</").matcher(submittedXml);
        if (m.find()) {
            commentString = m.group(1);
        }
        assertEquals(ENTITY_REFERENCES, commentString);
    }

    private String getTheLastModificationParam(final boolean includeWithdrawComment)
        throws Exception {
        return getTheLastModificationParam(includeWithdrawComment, theItemId);
    }

    private String getTheLastModificationParam(final String comment)
        throws Exception {
        return getTheLastModificationParam(true, theItemId, comment);
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testReleaseBeforeSubmitItem() throws Exception {

        String param = getTheLastModificationParam(false);

        try {
            release(theItemId, param);
            fail("No exception occured on release befor submit.");
        }
        catch (final Exception e) {
            Class<?> ec = InvalidStatusException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testReleaseItem() throws Exception {
        String param = getTheLastModificationParam(false);
        submit(theItemId, param);
        String pidParam;
        if (getItemClient().getPidConfig(
            "cmm.Item.objectPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig(
                "cmm.Item.objectPid.releaseWithoutPid", "false")) {
            pidParam =
                getPidParam(this.theItemId, "http://somewhere" + this.theItemId);
            assignObjectPid(this.theItemId, pidParam);
        }
        if (getItemClient().getPidConfig(
            "cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig(
                "cmm.Item.versionPid.releaseWithoutPid", "false")) {
            String latestVersion = getLatestVersionObjidValue(theItemXml);
            pidParam =
                getPidParam(latestVersion, "http://somewhere" + latestVersion);
            assignVersionPid(latestVersion, pidParam);
        }

        param = getTheLastModificationParam(false);
        release(theItemId, param);

        String xml = retrieve(theItemId);
        assertXmlExists("Properties status released", xml, XPATH_ITEM_STATUS
            + "[text() = 'released']");
        assertXmlExists("current-version status released", xml,
            XPATH_ITEM_CURRENT_VERSION_STATUS + "[text() = 'released']");
        assertXmlExists("Released item latest-release", xml,
            "/item/properties/latest-release");
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

        String param = getTheLastModificationParam(false);
        submit(theItemId, param);

        String pidParam;
        if (getItemClient().getPidConfig(
            "cmm.Item.objectPid.setPidBeforeRelease", "true")
            && getItemClient().getPidConfig(
                "cmm.Item.versionPid.setPidBeforeRelease", "true")) {
            // object pid
            pidParam =
                getPidParam(this.theItemId, "http://somewhere" + this.theItemId);
            assignObjectPid(this.theItemId, pidParam);

            // version pid
            String latestVersion = getLatestVersionObjidValue(theItemXml);
            pidParam =
                getPidParam(latestVersion, "http://somewhere" + latestVersion);
            assignVersionPid(latestVersion, pidParam);

            String retrievedItem = retrieve(this.theItemId);

            // content pid
            String componentId =
                getIdFromRootElement(toString(
                    selectSingleNode(getDocument(retrievedItem),
                        "//component[1]"), true));
            componentId = getObjidFromHref(componentId);

            pidParam =
                getPidParam(this.theItemId, "http://somewhere/content/"
                    + this.theItemId + "content");
            assignContentPid(this.theItemId, componentId, pidParam);
        }
        else {
            fail("Can not test pid before release.");
        }

        param = getTheLastModificationParam(false);
        release(theItemId, param);

        String xml = retrieve(theItemId);
        assertXmlExists("Properties status released", xml, XPATH_ITEM_STATUS
            + "[text() = 'released']");
        assertXmlExists("current-version status 'released' failed", xml,
            XPATH_ITEM_CURRENT_VERSION_STATUS + "[text() = 'released']");
        assertXmlExists("Released item latest-release", xml,
            "/item/properties/latest-release");
        // has PIDs
        assertXmlExists("Released item version pid missing", getDocument(xml),
            "/item/properties/pid/text()");
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

        String param = getTheLastModificationParam(true);

        try {
            withdraw(theItemId, param);
            fail("No exception occurred on withdraw before submit.");
        }
        catch (final Exception e) {
            Class<?> ec = NotPublishedException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testWithdrawBeforReleaseItem() throws Exception {

        String param = getTheLastModificationParam(false);
        submit(theItemId, param);

        param = getTheLastModificationParam(true);

        try {
            withdraw(theItemId, param);
            fail("No exception occured on withdraw befor release.");
        }
        catch (final Exception e) {
            Class<?> ec = NotPublishedException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testWithdrawItem() throws Exception {
        final String xPath = "/item/properties/content-model-specific";

        String param = getTheLastModificationParam(false);
        submit(theItemId, param);
        String pidParam;
        if (getItemClient().getPidConfig(
            "cmm.Item.objectPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig(
                "cmm.Item.objectPid.releaseWithoutPid", "false")) {
            pidParam =
                getPidParam(this.theItemId, "http://somewhere" + this.theItemId);
            assignObjectPid(this.theItemId, pidParam);
        }
        if (getItemClient().getPidConfig(
            "cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig(
                "cmm.Item.versionPid.releaseWithoutPid", "false")) {
            String latestVersion = getLatestVersionObjidValue(theItemXml);
            pidParam =
                getPidParam(latestVersion, "http://somewhere" + latestVersion);
            assignVersionPid(latestVersion, pidParam);
        }

        param = getTheLastModificationParam(false);
        release(theItemId, param);

        param = getTheLastModificationParam(true);
        withdraw(theItemId, param);

        String xml = retrieve(theItemId);

        assertXmlExists("Properties status withdrawn", xml, XPATH_ITEM_STATUS
            + "[text() = 'withdrawn']");
        assertXmlExists("current-version status must still be released", xml,
            XPATH_ITEM_CURRENT_VERSION_STATUS + "[text() = 'released']");
        // assertXmlExists("Withdrawn item comment", xml,
        // "/item/properties/current-version/comment");
        assertXmlExists("Further released withdrawn item latest-release", xml,
            "/item/properties/latest-release");
        assertXmlValidItem(xml);

        try {
            xml = addElement(xml, xPath + "/nix");
            xml = update(theItemId, xml);
            assertXmlExists("New version number", xml,
                "/item/properties/current-version/number[text() = '4']");
            assertXmlValidItem(xml);
            fail("Succesful update after withdraw.");
        }
        catch (final Exception e) {
            Class<?> ec = InvalidStatusException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec, e);
        }
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testWithdrawItemWithoutComment() throws Exception {

        String param = getTheLastModificationParam(false);
        submit(theItemId, param);
        String pidParam;
        if (getItemClient().getPidConfig(
            "cmm.Item.objectPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig(
                "cmm.Item.objectPid.releaseWithoutPid", "false")) {
            pidParam =
                getPidParam(this.theItemId, "http://somewhere" + this.theItemId);
            assignObjectPid(this.theItemId, pidParam);
        }
        if (getItemClient().getPidConfig(
            "cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig(
                "cmm.Item.versionPid.releaseWithoutPid", "false")) {
            String latestVersion = getLatestVersionObjidValue(theItemXml);
            pidParam =
                getPidParam(latestVersion, "http://somewhere" + latestVersion);
            assignVersionPid(latestVersion, pidParam);
        }

        param = getTheLastModificationParam(false);
        release(theItemId, param);

        param = getTheLastModificationParam(false);

        try {
            withdraw(theItemId, param);
            fail("No exception occured on withdraw without comment.");
        }
        catch (final Exception e) {
            Class<MissingMethodParameterException> ec =
                MissingMethodParameterException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }

    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testWithdrawNonExistingItem() throws Exception {

        String param = getTheLastModificationParam(true);

        try {
            withdraw("escidoc:item0", param);
            fail("No exception occured on withdraw non existing item.");
        }
        catch (final Exception e) {
            Class<ItemNotFoundException> ec = ItemNotFoundException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);

        }

    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testSecondWithdrawItem() throws Exception {

        String param = getTheLastModificationParam(false);
        submit(theItemId, param);
        String pidParam;
        if (getItemClient().getPidConfig(
            "cmm.Item.objectPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig(
                "cmm.Item.objectPid.releaseWithoutPid", "false")) {
            pidParam =
                getPidParam(this.theItemId, "http://somewhere" + this.theItemId);
            assignObjectPid(this.theItemId, pidParam);
        }
        if (getItemClient().getPidConfig(
            "cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig(
                "cmm.Item.versionPid.releaseWithoutPid", "false")) {
            String latestVersion = getLatestVersionObjidValue(theItemXml);
            pidParam =
                getPidParam(latestVersion, "http://somewhere" + latestVersion);
            assignVersionPid(latestVersion, pidParam);
        }

        param = getTheLastModificationParam(false);
        release(theItemId, param);

        param = getTheLastModificationParam(true);
        withdraw(theItemId, param);

        try {
            withdraw(theItemId, param);
            fail("No exception occured on second withdraw.");
        }
        catch (final Exception e) {
            Class<AlreadyWithdrawnException> ec =
                AlreadyWithdrawnException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }

    }

    /**
     * Test successful revising an item.
     * 
     * @test.name Revise Item - Submitted
     * @test.id OM_RVI-1
     * @test.input <ul>
     *             <li>existing item id of an item in state submitted</li>
     *             <li>timestamp of the last modification of the item</li>
     *             </ul>
     * @test.expected: No result, no exception, Item has been revised, Last
     *                 modification date of item has been updated.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvi1() throws Exception {

        String paramXml = getTheLastModificationParam(false);
        submit(theItemId, paramXml);
        paramXml = getTheLastModificationParam(false);
        final Document paramDocument =
            EscidocRestSoapTestBase.getDocument(paramXml);
        final String submittedLastModificationDate =
            getLastModificationDateValue(paramDocument);

        try {
            revise(theItemId, paramXml);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Revising the submitted item failed", e);
        }

        final String revisedXml = retrieve(theItemId);
        final Document revisedDocument =
            EscidocRestSoapTestBase.getDocument(revisedXml);
        assertDateBeforeAfter(submittedLastModificationDate,
            getLastModificationDateValue(revisedDocument));
        // assertXmlEquals("Unexpected status. ", revisedDocument,
        // XPATH_ITEM_STATUS, STATE_IN_REVISION);
        assertXmlEquals("Unexpected current version status", revisedDocument,
            XPATH_ITEM_CURRENT_VERSION_STATUS, STATE_IN_REVISION);
    }

    /**
     * Test declining revising an item in state pending.
     * 
     * @test.name Revise Item - Pending
     * @test.id OM_RVI-2
     * @test.input <ul>
     *             <li>existing item id of an item in state pending</li>
     *             <li>timestamp of the last modification of the item</li>
     *             </ul>
     * @test.expected: InvalidStatusException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvi2() throws Exception {

        String param = getTheLastModificationParam(false);

        try {
            revise(theItemId, param);
            EscidocRestSoapTestBase
                .failMissingException(InvalidStatusException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Revising failed with unexpected exception. ",
                InvalidStatusException.class, e);
        }
    }

    /**
     * Test declining revising an item in state released.
     * 
     * @test.name Revise Item - Released
     * @test.id OM_RVI-3
     * @test.input <ul>
     *             <li>existing item id of an item in state released</li>
     *             <li>timestamp of the last modification of the item</li>
     *             </ul>
     * @test.expected: InvalidStatusException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvi3() throws Exception {

        String param = getTheLastModificationParam(false);
        submit(theItemId, param);
        String pidParam;
        if (getItemClient().getPidConfig(
            "cmm.Item.objectPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig(
                "cmm.Item.objectPid.releaseWithoutPid", "false")) {
            pidParam =
                getPidParam(this.theItemId, "http://somewhere" + this.theItemId);
            assignObjectPid(this.theItemId, pidParam);
        }
        if (getItemClient().getPidConfig(
            "cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig(
                "cmm.Item.versionPid.releaseWithoutPid", "false")) {
            String latestVersion = getLatestVersionObjidValue(theItemXml);
            pidParam =
                getPidParam(latestVersion, "http://somewhere" + latestVersion);
            assignVersionPid(latestVersion, pidParam);
        }

        param = getTheLastModificationParam(false);
        release(theItemId, param);
        param = getTheLastModificationParam(false);

        try {
            revise(theItemId, param);
            EscidocRestSoapTestBase
                .failMissingException(InvalidStatusException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Revising failed with unexpected exception. ",
                InvalidStatusException.class, e);
        }
    }

    /**
     * Test declining revising an item in state withdrawn.
     * 
     * @test.name Revise Item - Withdrawn
     * @test.id OM_RVI-4
     * @test.input <ul>
     *             <li>existing item id of an item in state withdrawn</li>
     *             <li>timestamp of the last modification of the item</li>
     *             </ul>
     * @test.expected: InvalidStatusException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvi4() throws Exception {

        String param = getTheLastModificationParam(false);
        submit(theItemId, param);
        String pidParam;
        if (getItemClient().getPidConfig(
            "cmm.Item.objectPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig(
                "cmm.Item.objectPid.releaseWithoutPid", "false")) {
            pidParam =
                getPidParam(this.theItemId, "http://somewhere" + this.theItemId);
            assignObjectPid(this.theItemId, pidParam);
        }
        if (getItemClient().getPidConfig(
            "cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig(
                "cmm.Item.versionPid.releaseWithoutPid", "false")) {
            String latestVersion = getLatestVersionObjidValue(theItemXml);
            pidParam =
                getPidParam(latestVersion, "http://somewhere" + latestVersion);
            assignVersionPid(latestVersion, pidParam);
        }

        param = getTheLastModificationParam(false);
        release(theItemId, param);
        param = getTheLastModificationParam(true);
        withdraw(theItemId, param);
        param = getTheLastModificationParam(false);

        try {
            revise(theItemId, param);
            EscidocRestSoapTestBase
                .failMissingException(InvalidStatusException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Revising failed with unexpected exception. ",
                InvalidStatusException.class, e);
        }
    }

    /**
     * Test declining revising an unknown item.
     * 
     * @test.name Revise Item - Unknown item
     * @test.id OM_RVI-5
     * @test.input <ul>
     *             <li>id for that no item exists</li>
     *             <li>timestamp of the last modification of the item</li>
     *             </ul>
     * @test.expected: ItemNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvi5() throws Exception {

        String param = getTheLastModificationParam(false);

        try {
            revise(UNKNOWN_ID, param);
            EscidocRestSoapTestBase
                .failMissingException(ItemNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Revising unknown item failed with unexpected exception. ",
                ItemNotFoundException.class, e);
        }
    }

    /**
     * Test declining revising an item without providing an item id.
     * 
     * @test.name Revise Item - Missing item id
     * @test.id OM_RVI-6
     * @test.input <ul>
     *             <li>no item id is provided</li>
     *             <li>timestamp of the last modification of the item</li>
     *             </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvi6() throws Exception {

        String param = getTheLastModificationParam(false);

        try {
            revise(null, param);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Revising without id failed with unexpected exception. ",
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining revising an item without providing task param.
     * 
     * @test.name Revise Item - Missing task param
     * @test.id OM_RVI-7
     * @test.input <ul>
     *             <li>existing item id of an item in state submitted</li>
     *             <li>No task param is provided</li>
     *             </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvi7() throws Exception {

        String param = getTheLastModificationParam(false);
        submit(theItemId, param);
        param = getTheLastModificationParam(false);

        try {
            revise(theItemId, null);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Revising without id failed with unexpected exception. ",
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining revising an item without providing last modification date
     * in task param.
     * 
     * @test.name Revise Item - Missing last modification date
     * @test.id OM_RVI-8
     * @test.input <ul>
     *             <li>existing item id of an item in state submitted</li>
     *             <li>No last modification date is provided in task param</li>
     *             </ul>
     * @test.expected: MissingAttributeValueException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvi8() throws Exception {

        String param = getTheLastModificationParam(false);
        submit(theItemId, param);
        param = "<param />";

        try {
            revise(theItemId, null);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase
                .assertExceptionType(
                    "Revising without last modification date failed with unexpected exception. ",
                    MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining revising an item with corrupted task param.
     * 
     * @test.name Revise Item - Corrupted task param
     * @test.id OM_RVI-9
     * @test.input <ul>
     *             <li>existing item id of an item in state submitted</li>
     *             <li>Corrupted task param is provided</li>
     *             </ul>
     * @test.expected: XmlCorruptedException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvi9() throws Exception {

        String param = getTheLastModificationParam(false);
        submit(theItemId, param);
        param = "<param";

        try {
            revise(theItemId, param);
            EscidocRestSoapTestBase
                .failMissingException(XmlCorruptedException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase
                .assertExceptionType(
                    "Revising without last modification date failed with unexpected exception. ",
                    XmlCorruptedException.class, e);
        }
    }

    /**
     * Test declining revising an item with providing outdated last modification
     * date in task param.
     * 
     * @test.name Revise Item - Corrupted task param
     * @test.id OM_RVI-10
     * @test.input <ul>
     *             <li>existing item id of an item in state submitted</li>
     *             <li>task param is provided that contains an outdated last
     *             modificaton date</li>
     *             </ul>
     * @test.expected: XmlCorruptedException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvi10() throws Exception {

        String param = getTheLastModificationParam(false);
        submit(theItemId, param);

        try {
            revise(theItemId, param);
            EscidocRestSoapTestBase
                .failMissingException(OptimisticLockingException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase
                .assertExceptionType(
                    "Revising with outdated last modification date failed with unexpected exception. ",
                    OptimisticLockingException.class, e);
        }
    }

    /**
     * Test successful revising an item (created by a depositor) by an
     * administrator.
     * 
     * @test.name Revise Item - Administrator
     * @test.id OM_RVI-11
     * @test.input <ul>
     *             <li>existing item id of an item in state submitted</li>
     *             <li>timestamp of the last modification of the item</li>
     *             <li>revise method executed by an administrator</li>
     *             </ul>
     * @test.expected: No result, no exception, Item has been revised and is
     *                 retrievable, updateable and submittable by the depositor
     *                 that had created the item.
     * @test.status Implemented
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
                EscidocRestSoapTestBase.getTemplateAsString(TEMPLATE_ITEM_PATH
                    + "/" + getTransport(false),
                    "escidoc_item_198_for_create.xml");
            theItemXml = create(toBeCreatedXml);
            theItemId = getObjidValue(theItemXml);

            String param = getTheLastModificationParam(false);
            submit(theItemId, param);
            param = getTheLastModificationParam(false);

            // revise the item by an administrator
            PWCallback.setHandle(PWCallback.ADMINISTRATOR_HANDLE);

            try {
                revise(theItemId, param);
            }
            catch (final Exception e) {
                EscidocRestSoapTestBase.failException(
                    "Revising the submitted item failed", e);
            }

            // retrieve, update and submit the item by the depositor
            PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
            try {
                theItemXml = retrieve(theItemId);
            }
            catch (final Exception e) {
                EscidocRestSoapTestBase
                    .failException(
                        "Retrieving the revised item by the depositor failed with exception. ",
                        e);
            }
            theItemXml.replaceFirst("", "");
            try {
                theItemXml = update(theItemId, theItemXml);
            }
            catch (final Exception e) {
                EscidocRestSoapTestBase
                    .failException(
                        "Updating the revised item by the depositor failed with exception. ",
                        e);
            }
            param = getTheLastModificationParam(false);
            try {
                submit(theItemId, param);
            }
            catch (final Exception e) {
                EscidocRestSoapTestBase
                    .failException(
                        "Submitting the revised, updated item by the depositor failed with exception. ",
                        e);
            }

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
        String param = getTheLastModificationParam(false);
        submit(theItemId, param);
        String pidParam;
        if (getItemClient().getPidConfig(
            "cmm.Item.objectPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig(
                "cmm.Item.objectPid.releaseWithoutPid", "false")) {
            pidParam =
                getPidParam(this.theItemId, "http://somewhere" + this.theItemId);
            assignObjectPid(this.theItemId, pidParam);
        }
        if (getItemClient().getPidConfig(
            "cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig(
                "cmm.Item.versionPid.releaseWithoutPid", "false")) {
            String latestVersion = getLatestVersionObjidValue(theItemXml);
            pidParam =
                getPidParam(latestVersion, "http://somewhere" + latestVersion);
            assignVersionPid(latestVersion, pidParam);
        }

        param = getTheLastModificationParam(false);
        release(theItemId, param);

        String xml = retrieve(theItemId);
        assertXmlExists("Properties status released", xml, XPATH_ITEM_STATUS
            + "[text() = 'released']");
        assertXmlExists("version status released", xml,
            XPATH_ITEM_CURRENT_VERSION_STATUS + "[text() = 'released']");
        assertXmlExists("Released item latest-release", xml,
            "/item/properties/latest-release");

        Document newItem = EscidocRestSoapTestBase.getDocument(xml);
        selectSingleNode(newItem, "/item/properties/content-model-specific")
            .appendChild(newItem.createElement("nischt"));
        xml = update(theItemId, toString(newItem, false));
        Document xmlDoc = EscidocRestSoapTestBase.getDocument(xml);

        assertXmlValidItem(xml);
        assertXmlExists(
            "Properties status not 'released' after update of released item. ",
            xml, XPATH_ITEM_STATUS + "[text() = 'released']");
        assertXmlExists(
            "version status not 'pending' after update of released item. ",
            xml, XPATH_ITEM_CURRENT_VERSION_STATUS + "[text() = 'pending']");
        assertXmlExists(
            "No latest-release element after update of release item. ", xml,
            "/item/properties/latest-release");
        String versionNumber =
            selectSingleNode(xmlDoc,
                XPATH_ITEM_CURRENT_VERSION + "/number/text()").getNodeValue();
        String releaseNumber =
            selectSingleNode(xmlDoc,
                XPATH_ITEM_LATEST_RELEASE + "/number/text()").getNodeValue();
        assertEquals(
            "Latest release version is not the one before latest version after update of released item. ",
            Integer.parseInt(versionNumber),
            Integer.parseInt(releaseNumber) + 1);

    }

    /**
     * Test Item structure in lifecycle.
     * 
     * version 1: Create Item
     * 
     * @throws Exception
     */
    @Test
    public void testElementsAfterUpdate01() throws Exception {

        String xmlData =
            EscidocRestSoapTestBase.getTemplateAsString(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "item_without_component.xml");
        this.theItemXml = create(xmlData);
        this.theItemId = getObjidValue(theItemXml);

        // Assert Created ItemXML
        assertXmlExists("Wrong version number", this.theItemXml,
            "/item/properties/version/number[text() = '1']");
        assertXmlExists("Wrong public-status", this.theItemXml,
            "/item/properties/public-status[text() = 'pending']");
        assertXmlExists("Wrong version status", this.theItemXml,
            "/item/properties/version/status[text() = 'pending']");
        assertXmlNotExists("Wrong number of Components", this.theItemXml,
            "/item/components/component");

        // retrieve with object ref (latest version for author)
        String retrievedItemXml = retrieve(this.theItemId);
        assertXmlExists("Wrong version number", retrievedItemXml,
            "/item/properties/version/number[text() = '1']");
        assertXmlExists("Wrong public-status", retrievedItemXml,
            "/item/properties/public-status[text() = 'pending']");
        assertXmlExists("Wrong version status", retrievedItemXml,
            "/item/properties/version/status[text() = 'pending']");
        assertXmlNotExists("Wrong number of Components", retrievedItemXml,
            "/item/components/component");

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
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }
    }

    /**
     * Test Item structure in lifecycle.
     * 
     * created (version 1) ->update (add component) (version 2)
     * 
     * @throws Exception
     */
    @Test
    public void testElementsAfterUpdate02() throws Exception {

        // prepare object
        testElementsAfterUpdate01();
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);

        String xml = addComponent(this.theItemXml);
        this.theItemXml = update(theItemId, xml);

        // Assert Created ItemXML
        assertXmlExists("Wrong version number", this.theItemXml,
            "/item/properties/version/number[text() = '2']");
        assertXmlExists("Wrong public-status", this.theItemXml,
            "/item/properties/public-status[text() = 'pending']");
        assertXmlExists("Wrong version status", this.theItemXml,
            "/item/properties/version/status[text() = 'pending']");
        assertXmlExists("Wrong number of Components", this.theItemXml,
            "/item/components[count(./component)= '1']");

        // retrieve latest version (role: author)
        String retrievedItemXml = retrieve(this.theItemId);
        assertXmlExists("Wrong version number", retrievedItemXml,
            "/item/properties/version/number[text() = '2']");
        assertXmlExists("Wrong public-status", retrievedItemXml,
            "/item/properties/public-status[text() = 'pending']");
        assertXmlExists("Wrong version status", retrievedItemXml,
            "/item/properties/version/status[text() = 'pending']");
        assertXmlExists("Wrong number of Components", retrievedItemXml,
            "/item/components[count(./component)= '1']");

        // retrieve version 1 (role: author)
        retrievedItemXml = retrieve(this.theItemId + ":1");
        assertXmlExists("Wrong version number", retrievedItemXml,
            "/item/properties/version/number[text() = '1']");
        assertXmlExists("Wrong public-status", retrievedItemXml,
            "/item/properties/public-status[text() = 'pending']");
        assertXmlExists("Wrong version status", retrievedItemXml,
            "/item/properties/version/status[text() = 'pending']");
        assertXmlNotExists("Wrong number of Components", retrievedItemXml,
            "/item/components/component");

        // retrieve version 2 (role: author)
        retrievedItemXml = retrieve(this.theItemId + ":2");
        assertXmlExists("Wrong version number", retrievedItemXml,
            "/item/properties/version/number[text() = '2']");
        assertXmlExists("Wrong public-status", retrievedItemXml,
            "/item/properties/public-status[text() = 'pending']");
        assertXmlExists("Wrong version status", retrievedItemXml,
            "/item/properties/version/status[text() = 'pending']");
        assertXmlExists("Wrong number of Components", retrievedItemXml,
            "/item/components[count(./component)= '1']");

        // retrieve version 3 (role: author)
        try {
            retrieve(this.theItemId + ":3");
            fail("Updating Item leads to more than one new version. ");
        }
        catch (final Exception e) {
            Class<ItemNotFoundException> ec = ItemNotFoundException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
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
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }
    }

    /**
     * Test Item structure in lifecycle.
     * 
     * version 1: Created
     * 
     * version 2: updated (add Component) ->released
     * 
     * @throws Exception
     */
    @Test
    public void testElementsAfterUpdate03() throws Exception {

        // prepare object
        testElementsAfterUpdate02();
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);

        submit(theItemId, getTheLastModificationParam(false));
        releaseWithPid(this.theItemId);

        // retrieve latest version (role: author)
        String retrievedItemXml = retrieve(this.theItemId);
        assertXmlExists("Wrong version number", retrievedItemXml,
            "/item/properties/version/number[text() = '2']");
        assertXmlExists("Wrong public-status", retrievedItemXml,
            "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", retrievedItemXml,
            "/item/properties/version/status[text() = 'released']");
        assertXmlExists("Wrong latest-release number", retrievedItemXml,
            "/item/properties/latest-release/number[text() = '2']");
        assertXmlExists("Wrong number of Components", retrievedItemXml,
            "/item/components[count(./component)= '1']");

        // retrieve version 1 (role: author)
        retrievedItemXml = retrieve(this.theItemId + ":1");
        assertXmlExists("Wrong version number", retrievedItemXml,
            "/item/properties/version/number[text() = '1']");
        assertXmlExists("Wrong public-status", retrievedItemXml,
            "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", retrievedItemXml,
            "/item/properties/version/status[text() = 'pending']");
        assertXmlNotExists("Wrong number of Components", retrievedItemXml,
            "/item/components/component");
        assertXmlExists("Wrong latest-release number", retrievedItemXml,
            "/item/properties/latest-release/number[text() = '2']");

        // retrieve version 2 (role: author)
        retrievedItemXml = retrieve(this.theItemId + ":2");
        assertXmlExists("Wrong version number", retrievedItemXml,
            "/item/properties/version/number[text() = '2']");
        assertXmlExists("Wrong public-status", retrievedItemXml,
            "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", retrievedItemXml,
            "/item/properties/version/status[text() = 'released']");
        assertXmlExists("Wrong latest-release number", retrievedItemXml,
            "/item/properties/latest-release/number[text() = '2']");
        assertXmlExists("Wrong number of Components", retrievedItemXml,
            "/item/components[count(./component)= '1']");

        // retrieve version 3 (role: author)
        try {
            retrieve(this.theItemId + ":3");
            fail("Updating Item leads to more than one new version. ");
        }
        catch (final Exception e) {
            Class<ItemNotFoundException> ec = ItemNotFoundException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }

        /*
         * Retrieve as anonymous
         */
        PWCallback.setAnonymousHandle();

        // retrieve latest version (role: author)
        retrievedItemXml = retrieve(this.theItemId);
        assertXmlExists("Wrong version number", retrievedItemXml,
            "/item/properties/version/number[text() = '2']");
        assertXmlExists("Wrong public-status", retrievedItemXml,
            "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", retrievedItemXml,
            "/item/properties/version/status[text() = 'released']");
        assertXmlExists("Wrong latest-release number", retrievedItemXml,
            "/item/properties/latest-release/number[text() = '2']");
        assertXmlExists("Wrong number of Components", retrievedItemXml,
            "/item/components[count(./component)= '1']");

        // retrieve version 1 (role: anonymous)
        try {
            retrieve(this.theItemId + ":1");
            fail("Retrieve of unreleased Item is possible for anonymous. ");
        }
        catch (final Exception e) {
            Class<AuthorizationException> ec = AuthorizationException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }

        // retrieve version 2 (role: anonymous)
        retrievedItemXml = retrieve(this.theItemId + ":2");
        assertXmlExists("Wrong version number", retrievedItemXml,
            "/item/properties/version/number[text() = '2']");
        assertXmlExists("Wrong public-status", retrievedItemXml,
            "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", retrievedItemXml,
            "/item/properties/version/status[text() = 'released']");
        assertXmlExists("Wrong latest-release number", retrievedItemXml,
            "/item/properties/latest-release/number[text() = '2']");
        assertXmlExists("Wrong number of Components", retrievedItemXml,
            "/item/components[count(./component)= '1']");

    }

    /**
     * Test Item structure in lifecycle.
     * 
     * version 1: Created
     * 
     * version 2: updated->released
     * 
     * version 3: updated
     * 
     * @throws Exception
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
        assertXmlExists("New version number", updatedItemXml,
            "/item/properties/version/number[text() = '3']");
        assertXmlExists("Properties status not released", updatedItemXml,
            "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Version status not pending", updatedItemXml,
            "/item/properties/version/status[text() = 'pending']");
        // Count components
        assertXmlExists("Wrong number of Components", updatedItemXml,
            "/item/components[count(./component)= '2']");

        /*
         * Check as author
         */
        // retrieve latest version (role: author)
        String retrievedItemXml = retrieve(this.theItemId);
        assertXmlExists("Wrong version number", retrievedItemXml,
            "/item/properties/version/number[text() = '3']");
        assertXmlExists("Wrong public-status", retrievedItemXml,
            "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", retrievedItemXml,
            "/item/properties/version/status[text() = 'pending']");
        assertXmlExists("Wrong latest-release number", retrievedItemXml,
            "/item/properties/latest-release/number[text() = '2']");
        assertXmlExists("Wrong number of Components", retrievedItemXml,
            "/item/components[count(./component)= '2']");

        // retrieve version 1 (role: author)
        retrievedItemXml = retrieve(this.theItemId + ":1");
        assertXmlExists("Wrong version number", retrievedItemXml,
            "/item/properties/version/number[text() = '1']");
        assertXmlExists("Wrong public-status", retrievedItemXml,
            "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", retrievedItemXml,
            "/item/properties/version/status[text() = 'pending']");
        assertXmlNotExists("Wrong number of Components", retrievedItemXml,
            "/item/components/component");
        assertXmlExists("Wrong latest-release number", retrievedItemXml,
            "/item/properties/latest-release/number[text() = '2']");

        // retrieve version 2 (role: author)
        retrievedItemXml = retrieve(this.theItemId + ":2");
        assertXmlExists("Wrong version number", retrievedItemXml,
            "/item/properties/version/number[text() = '2']");
        assertXmlExists("Wrong public-status", retrievedItemXml,
            "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", retrievedItemXml,
            "/item/properties/version/status[text() = 'released']");
        assertXmlExists("Wrong latest-release number", retrievedItemXml,
            "/item/properties/latest-release/number[text() = '2']");
        assertXmlExists("Wrong number of Components", retrievedItemXml,
            "/item/components[count(./component)= '1']");

        // retrieve version 3 (role: author)
        retrievedItemXml = retrieve(this.theItemId + ":3");
        assertXmlExists("Wrong version number", retrievedItemXml,
            "/item/properties/version/number[text() = '3']");
        assertXmlExists("Wrong public-status", retrievedItemXml,
            "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", retrievedItemXml,
            "/item/properties/version/status[text() = 'pending']");
        assertXmlExists("Wrong latest-release number", retrievedItemXml,
            "/item/properties/latest-release/number[text() = '2']");
        assertXmlExists("Wrong number of Components", retrievedItemXml,
            "/item/components[count(./component)= '2']");

        // retrieve version 4 (role: author)
        try {
            retrieve(this.theItemId + ":4");
            fail("Updating Item leads to more than one new version. ");
        }
        catch (final Exception e) {
            Class<ItemNotFoundException> ec = ItemNotFoundException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }

        /*
         * Retrieve as anonymous
         */
        PWCallback.setAnonymousHandle();

        // retrieve latest version (role: anonymous)
        retrievedItemXml = retrieve(this.theItemId);
        assertXmlExists("Wrong version number", retrievedItemXml,
            "/item/properties/version/number[text() = '2']");
        assertXmlExists("Wrong public-status", retrievedItemXml,
            "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", retrievedItemXml,
            "/item/properties/version/status[text() = 'released']");
        assertXmlExists("Wrong latest-release number", retrievedItemXml,
            "/item/properties/latest-release/number[text() = '2']");
        assertXmlExists("Wrong number of Components", retrievedItemXml,
            "/item/components[count(./component)= '1']");

        // retrieve version 1 (role: anonymous)
        try {
            retrieve(this.theItemId + ":1");
            fail("Retrieve of unreleased Item is possible for anonymous.");
        }
        catch (final Exception e) {
            Class<AuthorizationException> ec = AuthorizationException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }

        // retrieve version 2 (role: anonymous)
        retrievedItemXml = retrieve(this.theItemId + ":2");
        assertXmlExists("Wrong version number", retrievedItemXml,
            "/item/properties/version/number[text() = '2']");
        assertXmlExists("Wrong public-status", retrievedItemXml,
            "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", retrievedItemXml,
            "/item/properties/version/status[text() = 'released']");
        assertXmlExists("Wrong latest-release number", retrievedItemXml,
            "/item/properties/latest-release/number[text() = '2']");
        assertXmlExists("Wrong number of Components", retrievedItemXml,
            "/item/components[count(./component)= '1']");

        // retrieve version 3 (role: anonymous)
        try {
            retrieve(this.theItemId + ":3");
            fail("Retrieve of unreleased Item is possible for anonymous. ");
        }
        catch (final Exception e) {
            Class<AuthorizationException> ec = AuthorizationException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }

    }

    /**
     * Retrieving of an post released Item deliveres wrong version of Item
     * (properties) back. See Bug #697.
     * 
     * @throws Exception
     */
    @Test
    public void testBug697() throws Exception {

        String xmlData =
            EscidocRestSoapTestBase.getTemplateAsString(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "item_without_component.xml");
        this.theItemXml = create(xmlData);
        this.theItemId = getObjidValue(theItemXml);

        String param = getTheLastModificationParam(false);
        submit(theItemId, param);
        String pidParam;
        if (getItemClient().getPidConfig(
            "cmm.Item.objectPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig(
                "cmm.Item.objectPid.releaseWithoutPid", "false")) {
            pidParam =
                getPidParam(this.theItemId, "http://somewhere" + this.theItemId);
            assignObjectPid(this.theItemId, pidParam);
        }
        if (getItemClient().getPidConfig(
            "cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig(
                "cmm.Item.versionPid.releaseWithoutPid", "false")) {
            String latestVersion = getLatestVersionObjidValue(theItemXml);
            pidParam =
                getPidParam(latestVersion, "http://somewhere" + latestVersion);
            assignVersionPid(latestVersion, pidParam);
        }

        param = getTheLastModificationParam(false);
        release(theItemId, param);

        // retrieve with object ref (latest version for author)
        String releasedItemXml = retrieve(theItemId);
        assertXmlExists("Wrong version number", releasedItemXml,
            "/item/properties/version/number[text() = '1']");
        assertXmlExists("Properties status not released", releasedItemXml,
            "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status pending", releasedItemXml,
            "/item/properties/version/status[text() = 'released']");
        assertXmlNotExists("Wrong number of Components", releasedItemXml,
            "/item/components/component");

        String xml = addComponent(releasedItemXml);
        String updatedItemXml = update(theItemId, xml);

        // checking some values
        assertXmlExists("New version number", updatedItemXml,
            "/item/properties/version/number[text() = '2']");
        assertXmlExists("Properties status not released", updatedItemXml,
            "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Version status not pending", updatedItemXml,
            "/item/properties/version/status[text() = 'pending']");
        // Count components
        assertXmlExists("Wrong number of Components", updatedItemXml,
            "/item/components[count(./component)= '1']");
        // TODO check components in detail

        /*
         * checking retrieve Item.
         */

        // checking author role
        releasedItemXml = retrieve(theItemId);
        assertXmlExists("Wrong version number", releasedItemXml,
            "/item/properties/version/number[text() = '2']");
        assertXmlExists("Properties status not released", releasedItemXml,
            "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status pending", releasedItemXml,
            "/item/properties/version/status[text() = 'pending']");
        assertXmlExists("Wrong number of Components", releasedItemXml,
            "/item/components[count(./component)= '1']");
        // TODO check components in detail

        // checking version 1
        releasedItemXml = retrieve(theItemId + ":1");
        assertXmlExists("Wrong version number", releasedItemXml,
            "/item/properties/version/number[text() = '1']");
        assertXmlExists("Properties status not released", releasedItemXml,
            "/item/properties/public-status[text() = 'released']");
        assertXmlExists("version status released", releasedItemXml,
            "/item/properties/version/status[text() = 'released']");
        assertXmlNotExists("Wrong number of Components", releasedItemXml,
            "/item/components/component");

        /*
         * ---- anonymous -----------------------------------
         */
        // retrieve as anonymous
        PWCallback.setAnonymousHandle();
        String anonymousXml = retrieve(theItemId);
        assertXmlExists("Wrong version number", anonymousXml,
            "/item/properties/version/number[text() = '1']");
        assertXmlExists("Properties status not released", anonymousXml,
            "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Version status not released", anonymousXml,
            "/item/properties/version/status[text() = 'released']");

        // xml = retrieve(theItemId + ":2");
        //
        // Document itemDoc = getDocument(releasedItemXml);
        // String lmd = getLastModificationDateValue(itemDoc);

    }

}
