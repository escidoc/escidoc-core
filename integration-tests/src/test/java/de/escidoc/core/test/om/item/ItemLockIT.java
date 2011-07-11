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
import de.escidoc.core.common.exceptions.remote.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.remote.application.violated.LockingException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Test locking and unlocking an item resource.<br> By default, the tests are executed using a depositor user.
 *
 * @author Michael Schneider
 */
public class ItemLockIT extends ItemTestBase {

    private String theItemXml;

    private String theItemId;

    /**
     * Successfully lock of container.
     */
    @Test
    public void testOM_C_lock() throws Exception {

        String param = getTheLastModificationParam(false, theItemId);
        lock(theItemId, param);

        String itemXml = retrieve(theItemId);
        Document itemDoc = EscidocAbstractTest.getDocument(itemXml);
        assertXmlEquals("Item lock status not as expected", itemDoc, "/item/properties/lock-status", "locked");
        assertXmlNotNull("lock-date", itemDoc, "/item/properties/lock-date");

        String lockOwner =
            getObjidFromHref(selectSingleNode(itemDoc, "/item/properties/lock-owner/@href").getTextContent());
        assertNotNull(lockOwner);
        assertXmlValidItem(itemXml);

        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        param = getTheLastModificationParam(false, theItemId);
        try {
            update(theItemId, itemXml);
            fail("No exception on update after lock.");
        }
        catch (final Exception e) {
            Class<?> ec = LockingException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
        PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
        unlock(theItemId, param);
    }

    @Test
    public void testOM_C_lockSelfUpdate() throws Exception {

        String param = getTheLastModificationParam(false, theItemId);
        lock(theItemId, param);

        String containerXml = retrieve(theItemId);
        Document containerDoc = EscidocAbstractTest.getDocument(containerXml);
        assertXmlEquals("Container lock status not as expected", containerDoc, "/item/properties/lock-status", "locked");
        assertXmlNotNull("lock-date", containerDoc, "/item/properties/lock-date");

        String lockOwner =
            getObjidFromHref(selectSingleNode(containerDoc, "/item/properties/lock-owner/@href").getTextContent());
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
     * @throws Exception If anything fails.
     */
    @Test
    public void testOM_ULI_1_1() throws Exception {

        String param = getTheLastModificationParam(false, theItemId);
        try {
            lock(theItemId, param);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Unlocking item can not be tested, locking failed" + " with exception.",
                e);
        }

        try {
            unlock(theItemId, param);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Unlocking item failed with exception. ", e);
        }

        String containerXml = retrieve(theItemId);

        Document containerDoc = EscidocAbstractTest.getDocument(containerXml);
        assertXmlEquals("Item lock status not as expected", containerDoc, "/item/properties/lock-status", "unlocked");

        assertXmlNotExists("Unexpected element lock-date in unlocked item.", containerDoc, "/item/properties/lock-date");
        assertXmlNotExists("Unexpected element lock-owner in unlocked item.", containerDoc,
            "/item/properties/lock-owner");

        // try to call update by System-Administrator
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        param = getTheLastModificationParam(false, theItemId);

        try {
            update(theItemId, containerXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Updating unlocked item failed with exception. ", e);
        }

    }

    /**
     * Succesfully unlock item by a system administrator.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOM_ULI_1_2() throws Exception {

        String param = getTheLastModificationParam(false, theItemId);
        try {
            lock(theItemId, param);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Unlocking item can not be tested, locking failed" + " with exception.",
                e);
        }

        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);

        try {
            unlock(theItemId, param);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Unlocking item failed with exception. ", e);
        }

        String containerXml = retrieve(theItemId);

        Document containerDoc = EscidocAbstractTest.getDocument(containerXml);
        assertXmlEquals("Container lock status not as expected", containerDoc, "/item/properties/lock-status",
            "unlocked");

        assertXmlNotExists("Unexpected element lock-date in unlocked item.", containerDoc, "/item/properties/lock-date");
        assertXmlNotExists("Unexpected element lock-owner in unlocked item.", containerDoc,
            "/item/properties/lock-owner");

        // try to call update by System-Administrator
        param = getTheLastModificationParam(false, theItemId);

        try {
            update(theItemId, containerXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Updating unlocked item failed with exception. ", e);
        }

    }

    /**
     * Declining unlock item by a user that is not the lock owner.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOM_ULI_2() throws Exception {

        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);

        String param = getTheLastModificationParam(false, theItemId);
        try {
            lock(theItemId, param);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Unlocking item can not be tested, locking failed" + " with exception.",
                e);
        }

        PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);

        try {
            unlock(theItemId, param);
            EscidocAbstractTest.failMissingException(AuthorizationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(AuthorizationException.class, e);
        }
    }

    /**
     * Unsuccessfully lock container with wrong container objid.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOM_C_lockWrongID() throws Exception {

        Class<?> ec = ItemNotFoundException.class;

        String param = getTheLastModificationParam(false, theItemId);

        try {
            lock("escidoc:noExist", param);
            EscidocAbstractTest.failMissingException("No exception after lock with non existing id.", ec);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ec, e);
        }
    }

    /**
     * unsuccessfully lock container with wrong last-modification-date
     */
    @Test
    public void testOM_C_lockOptimisicLocking() throws Exception {

        String param = "<param last-modification-date=\"1970-01-01T00:00:00.000Z\" ></param>";

        try {
            lock(theItemId, param);
            fail("No exception after lock with wrong last-modification-date.");
        }
        catch (final Exception e) {
            Class<?> ec = OptimisticLockingException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
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
        catch (final Exception e) {
            Class<?> ec = MissingMethodParameterException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * Test the last modification date timestamp of the lock/unlock method.
     *
     * @throws Exception Thrown if anything failed.
     */
    @Test
    public void testLockReturnValue01() throws Exception {

        String param = getTheLastModificationParam(false, theItemId);
        String resultXml = lock(theItemId, param);
        assertXmlValidResult(resultXml);

        Document resultDoc = EscidocAbstractTest.getDocument(resultXml);
        String lmdResultLock = getLastModificationDateValue(resultDoc);

        String itemXml = retrieve(theItemId);
        Document itemDoc = EscidocAbstractTest.getDocument(itemXml);
        String lmdRetrieve = getLastModificationDateValue(itemDoc);

        assertEquals("Last modification date of result and item not equal", lmdResultLock, lmdRetrieve);

        // now check unlock
        resultXml = unlock(theItemId, param);
        assertXmlValidResult(resultXml);
        resultDoc = EscidocAbstractTest.getDocument(resultXml);
        String lmdResultUnlock = getLastModificationDateValue(resultDoc);

        itemXml = retrieve(theItemId);
        itemDoc = EscidocAbstractTest.getDocument(itemXml);
        lmdRetrieve = getLastModificationDateValue(itemDoc);

        assertEquals("Last modification date of result and item not equal", lmdResultUnlock, lmdRetrieve);

        // assert that the last-modification-date of item hasn't changed
        assertEquals("Last modification date of result and item not equal", lmdResultUnlock, lmdResultLock);
    }

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void setUp() throws Exception {
        // create an item and save the id
        PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
        String xmlData =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        theItemXml = create(xmlData);
        theItemId = getObjidValue(EscidocAbstractTest.getDocument(theItemXml));

    }

    /**
     * Clean up after test.
     *
     * @throws Exception If anything fails.
     */
    @After
    public void tearDown() throws Exception {

        super.tearDown();
        PWCallback.resetHandle();
        try {
            delete(theItemId);
        }
        catch (final LockingException e) {
        }
    }
}
