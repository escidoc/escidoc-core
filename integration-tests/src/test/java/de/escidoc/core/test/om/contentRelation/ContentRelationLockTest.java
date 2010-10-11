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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import de.escidoc.core.test.EscidocRestSoapTestBase;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import org.w3c.dom.Document;

import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.remote.application.violated.LockingException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.security.client.PWCallback;

/**
 * Test content relation create implementation.
 * 
 * @author SWA
 * 
 */
@RunWith(value = Parameterized.class)
public class ContentRelationLockTest extends ContentRelationTestBase {

    private String theContentRelationXml;

    private String theContentRelationId;

    private String[] user = null;

    /**
     * @param transport
     *            The transport identifier.
     */
    public ContentRelationLockTest(final int transport) {
        super(transport);
    }

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Override
    @Before
    public void setUp() throws Exception {

        super.setUp();
        this.user =
            createUserWithContentRelationRole("escidoc_useraccount_for_create.xml");
        addContentRelationManagerGrant(this.user[0]);
        addContentRelationManagerGrant("escidoc:testdepositor");

        PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
        String contentRelationXml =
            getExampleTemplate("content-relation-01.xml");
        theContentRelationXml = create(contentRelationXml);
        theContentRelationId =
            getObjidValue(EscidocRestSoapTestBase
                .getDocument(theContentRelationXml));

    }

    /**
     * Clean up after test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Override
    @After
    public void tearDown() throws Exception {

        super.tearDown();
        PWCallback.resetHandle();
        try {
            delete(theContentRelationId);
        }
        catch (LockingException e) {
        }
    }

    /**
     * Successfully lock of container.
     */
    @Test
    public void testOM_C_lock() throws Exception {

        String param = getTheLastModificationParam(false, theContentRelationId);
        lock(theContentRelationId, param);

        String contentRelationXml = retrieve(theContentRelationId);
        Document contentRelationDoc =
            EscidocRestSoapTestBase.getDocument(contentRelationXml);
        assertXmlEquals("Content relation lock status not as expected",
            contentRelationDoc, "/content-relation/properties/lock-status",
            "locked");
        assertXmlNotNull("lock-date", contentRelationDoc,
            "/content-relation/properties/lock-date");

        String lockOwner = null;
        if (getTransport() == Constants.TRANSPORT_REST) {
            lockOwner =
                getObjidFromHref(selectSingleNode(contentRelationDoc,
                    "/content-relation/properties/lock-owner/@href")
                    .getTextContent());
        }
        else if (getTransport() == Constants.TRANSPORT_SOAP) {
            lockOwner =
                selectSingleNode(contentRelationDoc,
                    "/content-relation/properties/lock-owner/@objid")
                    .getTextContent();
        }
        assertNotNull(lockOwner);
        assertXmlValidContentRelation(contentRelationXml);

        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        param = getTheLastModificationParam(false, theContentRelationId);
        try {
            update(theContentRelationId, contentRelationXml);
            fail("No exception on update after lock.");
        }
        catch (Exception e) {
            Class<?> ec = LockingException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }
        PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
        unlock(theContentRelationId, param);
    }

    @Test
    public void testOM_C_lockSelfUpdate() throws Exception {

        String param = getTheLastModificationParam(false, theContentRelationId);
        lock(theContentRelationId, param);

        String contentRelationXml = retrieve(theContentRelationId);
        Document contentRelationDoc =
            EscidocRestSoapTestBase.getDocument(contentRelationXml);
        assertXmlEquals("content Relation lock status not as expected",
            contentRelationDoc, "/content-relation/properties/lock-status",
            "locked");
        assertXmlNotNull("lock-date", contentRelationDoc,
            "/content-relation/properties/lock-date");

        String lockOwner = null;
        if (getTransport() == Constants.TRANSPORT_REST) {
            lockOwner =
                getObjidFromHref(selectSingleNode(contentRelationDoc,
                    "/content-relation/properties/lock-owner/@href")
                    .getTextContent());
        }
        else if (getTransport() == Constants.TRANSPORT_SOAP) {
            lockOwner =
                selectSingleNode(contentRelationDoc,
                    "/content-relation/properties/lock-owner/@objid")
                    .getTextContent();
        }
        assertNotNull(lockOwner);

        assertXmlValidContentRelation(contentRelationXml);

        param = getTheLastModificationParam(false, theContentRelationId);
        update(theContentRelationId, contentRelationXml);
        param = getTheLastModificationParam(false, theContentRelationId);
        unlock(theContentRelationId, param);

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

        String param = getTheLastModificationParam(false, theContentRelationId);
        try {
            lock(theContentRelationId, param);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.failException(
                "Unlocking content relation can not be tested, locking failed"
                    + " with exception.", e);
        }

        try {
            unlock(theContentRelationId, param);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.failException(
                "Unlocking item failed with exception. ", e);
        }

        String contentRelationXml = retrieve(theContentRelationId);

        Document contentRelationDoc =
            EscidocRestSoapTestBase.getDocument(contentRelationXml);
        assertXmlEquals("content-relation lock status not as expected",
            contentRelationDoc, "/content-relation/properties/lock-status",
            "unlocked");

        assertXmlNotExists("Unexpected element lock-date in "
            + "unlocked content-relation.", contentRelationDoc,
            "/content-relation/properties/lock-date");
        assertXmlNotExists("Unexpected element lock-owner in "
            + "unlocked content-relation.", contentRelationDoc,
            "/content-relation/properties/lock-owner");

        // try to call update by System-Administrator
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        param = getTheLastModificationParam(false, theContentRelationId);

        try {
            update(theContentRelationId, contentRelationXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase
                .failException(
                    "Updating unlocked content relation failed with exception. ",
                    e);
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

        String param = getTheLastModificationParam(false, theContentRelationId);
        try {
            lock(theContentRelationId, param);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.failException(
                "Unlocking item can not be tested, locking failed"
                    + " with exception.", e);
        }

        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);

        try {
            unlock(theContentRelationId, param);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.failException(
                "Unlocking item failed with exception. ", e);
        }

        String contentRelationXml = retrieve(theContentRelationId);

        Document contentRelationDoc =
            EscidocRestSoapTestBase.getDocument(contentRelationXml);
        assertXmlEquals("content-relation lock status not as expected",
            contentRelationDoc, "/content-relation/properties/lock-status",
            "unlocked");

        assertXmlNotExists(
            "Unexpected element lock-date in unlocked content-relation.",
            contentRelationDoc, "/content-relation/properties/lock-date");
        assertXmlNotExists(
            "Unexpected element lock-owner in unlocked content-relation.",
            contentRelationDoc, "/content-relation/properties/lock-owner");

        // try to call update by System-Administrator
        param = getTheLastModificationParam(false, theContentRelationId);

        try {
            update(theContentRelationId, contentRelationXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase
                .failException(
                    "Updating unlocked content relation failed with exception. ",
                    e);
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

        String param = getTheLastModificationParam(false, theContentRelationId);
        try {
            lock(theContentRelationId, param);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.failException(
                "Unlocking content relation can not be tested, locking failed"
                    + " with exception.", e);
        }

        String handle =
            login(this.user[1], Constants.DEFAULT_USER_PASSWORD, true);
        PWCallback.setHandle(handle);

        try {
            unlock(theContentRelationId, param);
            EscidocRestSoapTestBase
                .failMissingException(AuthorizationException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
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

        Class<?> ec = ContentRelationNotFoundException.class;

        String param = getTheLastModificationParam(false, theContentRelationId);

        try {
            lock("escidoc:noExist", param);
            EscidocRestSoapTestBase.failMissingException(
                "No exception after lock with non existing id.", ec);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(ec, e);
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
            lock(theContentRelationId, param);
            fail("No exception after lock with wrong last-modification-date.");
        }
        catch (Exception e) {
            Class<?> ec = OptimisticLockingException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }
    }

    /**
     * unsuccessfully lock container without container id
     */
    @Test
    public void testOM_C_lockWithoutID() throws Exception {

        String param = getTheLastModificationParam(false, theContentRelationId);

        try {
            lock(null, param);
            fail("No exception after lock without id.");
        }
        catch (Exception e) {
            Class<?> ec = MissingMethodParameterException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
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

        String param = getTheLastModificationParam(false, theContentRelationId);
        String resultXml = lock(theContentRelationId, param);
        assertXmlValidResult(resultXml);

        Document resultDoc = EscidocRestSoapTestBase.getDocument(resultXml);
        String lmdResultLock = getLastModificationDateValue(resultDoc);

        String contentRelationXml = retrieve(theContentRelationId);
        Document contentRelationDoc =
            EscidocRestSoapTestBase.getDocument(contentRelationXml);
        String lmdRetrieve = getLastModificationDateValue(contentRelationDoc);

        assertEquals("Last modification date of result and content relation "
            + "not equal", lmdResultLock, lmdRetrieve);

        // now check unlock
        resultXml = unlock(theContentRelationId, param);
        assertXmlValidResult(resultXml);
        resultDoc = EscidocRestSoapTestBase.getDocument(resultXml);
        String lmdResultUnlock = getLastModificationDateValue(resultDoc);

        contentRelationXml = retrieve(theContentRelationId);
        contentRelationDoc =
            EscidocRestSoapTestBase.getDocument(contentRelationXml);
        lmdRetrieve = getLastModificationDateValue(contentRelationDoc);

        assertEquals(
            "Last modification date of result and content relation not "
                + "equal", lmdResultUnlock, lmdRetrieve);

        // assert that the last-modification-date of item hasn't changed
        assertEquals(
            "Last modification date of result and content relation not"
                + " equal", lmdResultUnlock, lmdResultLock);
    }

}