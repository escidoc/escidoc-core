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

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;

import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.remote.application.violated.LockingException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.security.client.PWCallback;

/**
 * Test locking and unlocking an item resource.<br>
 * By default, the tests are executed using a depositor user.
 * 
 * @author MSC
 * 
 */
@RunWith(value = Parameterized.class)
public class ItemLockTest extends ItemTestBase {

    private String theItemXml;

    private String theItemId;

    /**
     * @param transport
     *            The transport identifier.
     */
    public ItemLockTest(final int transport) {
        super(transport);
    }

    /**
     * Successfully lock of container.
     */
    @Test
    public void testOM_C_lock() throws Exception {

        String param = getTheLastModificationParam(false, theItemId);
        lock(theItemId, param);

        String itemXml = retrieve(theItemId);
        Document itemDoc = EscidocRestSoapTestsBase.getDocument(itemXml);
        assertXmlEquals("Item lock status not as expected", itemDoc,
            "/item/properties/lock-status", "locked");
        assertXmlNotNull("lock-date", itemDoc, "/item/properties/lock-date");

        String lockOwner = null;
        if (getTransport() == Constants.TRANSPORT_REST) {
            lockOwner =
                getObjidFromHref(selectSingleNode(itemDoc,
                    "/item/properties/lock-owner/@href").getTextContent());
        }
        else if (getTransport() == Constants.TRANSPORT_SOAP) {
            lockOwner =
                selectSingleNode(itemDoc, "/item/properties/lock-owner/@objid")
                    .getTextContent();
        }
        assertNotNull(lockOwner);
        assertXmlValidItem(itemXml);

        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        param = getTheLastModificationParam(false, theItemId);
        try {
            update(theItemId, itemXml);
            fail("No exception on update after lock.");
        }
        catch (Exception e) {
            Class<?> ec = LockingException.class;
            EscidocRestSoapTestsBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }
        PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
        unlock(theItemId, param);
    }

    @Test
    public void testOM_C_lockSelfUpdate() throws Exception {

        String param = getTheLastModificationParam(false, theItemId);
        lock(theItemId, param);

        String containerXml = retrieve(theItemId);
        Document containerDoc =
            EscidocRestSoapTestsBase.getDocument(containerXml);
        assertXmlEquals("Container lock status not as expected", containerDoc,
            "/item/properties/lock-status", "locked");
        assertXmlNotNull("lock-date", containerDoc,
            "/item/properties/lock-date");

        String lockOwner = null;
        if (getTransport() == Constants.TRANSPORT_REST) {
            lockOwner =
                getObjidFromHref(selectSingleNode(containerDoc,
                    "/item/properties/lock-owner/@href").getTextContent());
        }
        else if (getTransport() == Constants.TRANSPORT_SOAP) {
            lockOwner =
                selectSingleNode(containerDoc,
                    "/item/properties/lock-owner/@objid").getTextContent();
        }
        assertNotNull(lockOwner);

        assertXmlValidItem(containerXml);

        param = getTheLastModificationParam(false, theItemId);
        update(theItemId, containerXml);
        param = getTheLastModificationParam(false, theItemId);
        unlock(theItemId, param);

    }

    /**
     * Succesfully unlock item by the lock owner.
     * 
     * @test.name Unlock Item - Lock Owner
     * @test.id OUM_ULI-1-1
     * @test.input <ul>
     *             <li>Valid item id of an item that is locked.</li>
     *             <li>valid task param with last modification date of the item</li>
     *             <li>Method called by the lock-owner</li>
     *             </ul>
     * @test.expected: <ul>
     *                 <li>Unlock returns without exception</li>
     *                 <li>retrieved unlocked item has unlocked status and does
     *                 not contain lock information (lock-date, lock-owner)</li>
     *                 </ul>
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_ULI_1_1() throws Exception {

        String param = getTheLastModificationParam(false, theItemId);
        try {
            lock(theItemId, param);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(
                "Unlocking item can not be tested, locking failed"
                    + " with exception.", e);
        }

        try {
            unlock(theItemId, param);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(
                "Unlocking item failed with exception. ", e);
        }

        String containerXml = retrieve(theItemId);

        Document containerDoc =
            EscidocRestSoapTestsBase.getDocument(containerXml);
        assertXmlEquals("Item lock status not as expected", containerDoc,
            "/item/properties/lock-status", "unlocked");

        assertXmlNotExists("Unexpected element lock-date in unlocked item.",
            containerDoc, "/item/properties/lock-date");
        assertXmlNotExists("Unexpected element lock-owner in unlocked item.",
            containerDoc, "/item/properties/lock-owner");

        // try to call update by System-Administrator
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        param = getTheLastModificationParam(false, theItemId);

        try {
            update(theItemId, containerXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(
                "Updating unlocked item failed with exception. ", e);
        }

    }

    /**
     * Succesfully unlock item by a system administrator.
     * 
     * @test.name Unlock Item - System Administrator
     * @test.id OUM_ULI-1-2
     * @test.input <ul>
     *             <li>Valid item id of an item that is locked.</li>
     *             <li>valid task param with last modification date of the item</li>
     *             <li>Method called by a system-administrator, not the
     *             lock-owner</li>
     *             </ul>
     * @test.expected: <ul>
     *                 <li>Unlock returns without exception</li>
     *                 <li>retrieved unlocked item does not contain lock
     *                 information (lock-date, lock-owner)</li>
     *                 </ul>
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_ULI_1_2() throws Exception {

        String param = getTheLastModificationParam(false, theItemId);
        try {
            lock(theItemId, param);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(
                "Unlocking item can not be tested, locking failed"
                    + " with exception.", e);
        }

        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);

        try {
            unlock(theItemId, param);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(
                "Unlocking item failed with exception. ", e);
        }

        String containerXml = retrieve(theItemId);

        Document containerDoc =
            EscidocRestSoapTestsBase.getDocument(containerXml);
        assertXmlEquals("Container lock status not as expected", containerDoc,
            "/item/properties/lock-status", "unlocked");

        assertXmlNotExists("Unexpected element lock-date in unlocked item.",
            containerDoc, "/item/properties/lock-date");
        assertXmlNotExists("Unexpected element lock-owner in unlocked item.",
            containerDoc, "/item/properties/lock-owner");

        // try to call update by System-Administrator
        param = getTheLastModificationParam(false, theItemId);

        try {
            update(theItemId, containerXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(
                "Updating unlocked item failed with exception. ", e);
        }

    }

    /**
     * Declining unlock item by a user that is not the lock owner.
     * 
     * @test.name Unlock Item - Not Lock Owner
     * @test.id OUM_ULI-2
     * @test.input <ul>
     *             <li>Valid item id of an item that is locked.</li>
     *             <li>valid task param with last modification date of the item</li>
     *             <li>Method is neither called by the lock-owner nor a system
     *             administrator</li>
     *             </ul>
     * @test.expected: AuthorizationException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_ULI_2() throws Exception {

        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);

        String param = getTheLastModificationParam(false, theItemId);
        try {
            lock(theItemId, param);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(
                "Unlocking item can not be tested, locking failed"
                    + " with exception.", e);
        }

        PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);

        try {
            unlock(theItemId, param);
            EscidocRestSoapTestsBase
                .failMissingException(AuthorizationException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                AuthorizationException.class, e);
        }
    }

    /**
     * Unsuccessfully lock container with wrong container objid.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_C_lockWrongID() throws Exception {

        Class<?> ec = ItemNotFoundException.class;

        String param = getTheLastModificationParam(false, theItemId);

        try {
            lock("escidoc:noExist", param);
            EscidocRestSoapTestsBase.failMissingException(
                "No exception after lock with non existing id.", ec);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(ec, e);
        }
    }

    /**
     * unsuccessfully lock container with wrong last-modification-date
     */
    @Test
    public void testOM_C_lockOptimisicLocking() throws Exception {

        String param =
            "<param last-modification-date=\"1970-01-01T00:00:00.000Z\" ></param>";

        try {
            lock(theItemId, param);
            fail("No exception after lock with wrong last-modification-date.");
        }
        catch (Exception e) {
            Class<?> ec = OptimisticLockingException.class;
            EscidocRestSoapTestsBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }
    }

    /**
     * unsuccessfully lock container without container id
     */
    @Test
    public void testOM_C_lockWithoutID() throws Exception {

        String param = getTheLastModificationParam(false, theItemId);

        try {
            lock(null, param);
            fail("No exception after lock without id.");
        }
        catch (Exception e) {
            Class<?> ec = MissingMethodParameterException.class;
            EscidocRestSoapTestsBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }
    }

    /**
     * Test the last modification date timestamp of the lock/unlock method.
     * 
     * @throws Exception
     *             Thrown if anything failed.
     */
    @Test
    public void testLockReturnValue01() throws Exception {

        String param = getTheLastModificationParam(false, theItemId);
        String resultXml = lock(theItemId, param);
        assertXmlValidResult(resultXml);

        Document resultDoc = EscidocRestSoapTestsBase.getDocument(resultXml);
        String lmdResultLock = getLastModificationDateValue(resultDoc);

        String itemXml = retrieve(theItemId);
        Document itemDoc = EscidocRestSoapTestsBase.getDocument(itemXml);
        String lmdRetrieve = getLastModificationDateValue(itemDoc);

        assertEquals("Last modification date of result and item not equal",
            lmdResultLock, lmdRetrieve);

        // now check unlock
        resultXml = unlock(theItemId, param);
        assertXmlValidResult(resultXml);
        resultDoc = EscidocRestSoapTestsBase.getDocument(resultXml);
        String lmdResultUnlock = getLastModificationDateValue(resultDoc);

        itemXml = retrieve(theItemId);
        itemDoc = EscidocRestSoapTestsBase.getDocument(itemXml);
        lmdRetrieve = getLastModificationDateValue(itemDoc);

        assertEquals("Last modification date of result and item not equal",
            lmdResultUnlock, lmdRetrieve);

        // assert that the last-modification-date of item hasn't changed
        assertEquals("Last modification date of result and item not equal",
            lmdResultUnlock, lmdResultLock);
    }

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Before
    public void setUp() throws Exception {

        super.setUp();
        // create an item and save the id
        PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
        String xmlData =
            EscidocRestSoapTestsBase.getTemplateAsString(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
        theItemXml = create(xmlData);
        theItemId =
            getObjidValue(EscidocRestSoapTestsBase.getDocument(theItemXml));

    }

    /**
     * Clean up after test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @After
    public void tearDown() throws Exception {

        super.tearDown();
        PWCallback.resetHandle();
        try {
            delete(theItemId);
        }
        catch (LockingException e) {
        }
    }
}
