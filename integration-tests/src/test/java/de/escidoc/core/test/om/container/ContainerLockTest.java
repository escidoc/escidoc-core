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
package de.escidoc.core.test.om.container;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.http.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;

import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContainerNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.security.client.PWCallback;

/**
 * Test locking and unlocking a container resource.
 * 
 * @author MSC
 * 
 */
@RunWith(value = Parameterized.class)
public class ContainerLockTest extends ContainerTestBase {

    private String theContainerXml;

    private String theContainerId;

    private String theItemId;

    private String theSubcontainerId;

    /**
     * @param transport
     *            The transport identifier.
     */
    public ContainerLockTest(final int transport) {
        super(transport);
    }

    /**
     * Successfully lock container, successfully update of a locked container by
     * a lock-owner.
     */
    @Test
    public void testOM_C_lock() throws Exception {

        String param = getTheLastModificationParam(false, theContainerId);
        lock(theContainerId, param);

        String containerXml = retrieve(theContainerId);
        Document containerDoc =
            EscidocRestSoapTestsBase.getDocument(containerXml);
        assertXmlEquals("Container lock status not as expected", containerDoc,
            "/container/properties/lock-status", "locked");
        assertXmlNotNull("lock-date", containerDoc,
            "/container/properties/lock-date");
        if (Constants.TRANSPORT_REST == getTransport()) {
            assertXmlNotNull("lock-owner", containerDoc,
                "/container/properties/lock-owner/@href");

        }
        else {
            assertXmlNotNull("lock-owner", containerDoc,
                "/container/properties/lock-owner/@objid");
        }
        assertXmlValidContainer(containerXml);

        param = getTheLastModificationParam(false, theContainerId);
        try {
            update(theContainerId, containerXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(
                "Update container failed with exception. ", e);
        }

    }

    /**
     * Successfully unlock container by the lock owner.
     * 
     * @test.name Unlock Container - Lock Owner
     * @test.id OUM_ULC-1-1
     * @test.input
     *          <ul>
     *          <li>Valid container id of a container that is locked.</li>
     *          <li>valid task param with last modification date of the
     *          container </li>
     *          <li>Method called by the lock-owner</li>
     *          </ul>
     * @test.expected:
     *          <ul>
     *          <li>Unlock returns without exception</li>
     *          <li>retrieved unlocked container has unlocked status and does
     *          not contain lock information (lock-date, lock-owner)</li>
     *          </ul>
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_ULC_1_1() throws Exception {

        String param = getTheLastModificationParam(false, theContainerId);
        try {
            lock(theContainerId, param);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(
                "Unlocking container can not be tested, locking"
                    + " failed with exception.", e);
        }

        try {
            unlock(theContainerId, param);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(
                "Unlocking container failed with exception. ", e);
        }

        String containerXml = retrieve(theContainerId);
        Document containerDoc =
            EscidocRestSoapTestsBase.getDocument(containerXml);
        assertXmlEquals("Container lock status not as expected", containerDoc,
            "/container/properties/lock-status", "unlocked");

        assertXmlNotExists("Unexpected element lock-date in unlocked item.",
            containerDoc, "/container/properties/lock-date");
        assertXmlNotExists("Unexpected element lock-owner in unlocked item.",
            containerDoc, "/container/properties/lock-owner");

        assertXmlValidContainer(containerXml);

        // try to call update by System-Administrator
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        param = getTheLastModificationParam(false, theContainerId);

        try {
            update(theContainerId, containerXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(
                "Updating unlocked container failed with exception. ", e);
        }

    }

    /**
     * Successfully unlock container by a system administrator.
     * 
     * @test.name Unlock Container - System Administrator
     * @test.id OUM_ULC-1-2
     * @test.input
     *          <ul>
     *          <li>Valid container id of a container that is locked.</li>
     *          <li>valid task param with last modification date of the
     *          container </li>
     *          <li>Method called by the system administrator instead of the
     *          lock owner</li>
     *          </ul>
     * @test.expected:
     *          <ul>
     *          <li>Unlock returns without exception</li>
     *          <li>retrieved unlocked container has unlocked status and does
     *          not contain lock information (lock-date, lock-owner)</li>
     *          </ul>
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_ULC_1_2() throws Exception {

        String param = getTheLastModificationParam(false, theContainerId);
        try {
            lock(theContainerId, param);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(
                "Unlocking container can not be tested, locking"
                    + " failed with exception.", e);
        }

        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);

        try {
            unlock(theContainerId, param);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(
                "Unlocking container failed with exception. ", e);
        }

        String containerXml = retrieve(theContainerId);
        Document containerDoc =
            EscidocRestSoapTestsBase.getDocument(containerXml);
        assertXmlEquals("Container lock status not as expected", containerDoc,
            "/container/properties/lock-status", "unlocked");

        assertXmlNotExists("Unexpected element lock-date in unlocked item.",
            containerDoc, "/container/properties/lock-date");
        assertXmlNotExists("Unexpected element lock-owner in unlocked item.",
            containerDoc, "/container/properties/lock-owner");

        assertXmlValidContainer(containerXml);

        // try to call update by System-Administrator
        param = getTheLastModificationParam(false, theContainerId);

        try {
            update(theContainerId, containerXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(
                "Updating unlocked container failed with exception. ", e);
        }

    }

    /**
     * Declining unlock container by a user that is not the lock owner.
     * 
     * @test.name Unlock Container - Not Lock Owner
     * @test.id OUM_ULC-2
     * @test.input
     *          <ul>
     *          <li>Valid container id of a container that is locked.</li>
     *          <li>valid task param with last modification date of the
     *          container </li>
     *          <li>Method is neither called by the lock-owner nor a system
     *          administrator</li>
     *          </ul>
     * @test.expected: AuthorizationException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_ULC_2() throws Exception {

        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);

        String param = getTheLastModificationParam(false, theContainerId);
        try {
            lock(theContainerId, param);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(
                "Unlocking container can not be tested, locking"
                    + " failed with exception.", e);
        }

        PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);

        try {
            unlock(theContainerId, param);
            EscidocRestSoapTestsBase
                .failMissingException(AuthorizationException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                AuthorizationException.class, e);
        }
    }

    /**
     * unsuccessfully lock container with wrong container id
     */
    @Test
    public void testOM_C_lockWrongID() throws Exception {

        String param = getTheLastModificationParam(false, theContainerId);

        try {
            lock("escidoc:noExist", param);
            fail("No exception after lock with non existing id.");
        }
        catch (Exception e) {
            Class<?> ec = ContainerNotFoundException.class;
            EscidocRestSoapTestsBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
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
            lock(theContainerId, param);
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

        String param = getTheLastModificationParam(false, theContainerId);

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

        String param = getTheLastModificationParam(false, theContainerId);
        String resultXml = lock(theContainerId, param);
        assertXmlValidResult(resultXml);

        Document resultDoc = EscidocRestSoapTestsBase.getDocument(resultXml);
        String lmdResultLock = getLastModificationDateValue(resultDoc);

        String containerXml = retrieve(theContainerId);
        Document containerDoc =
            EscidocRestSoapTestsBase.getDocument(containerXml);
        String lmdRetrieve = getLastModificationDateValue(containerDoc);

        assertEquals(
            "Last modification date of result and container not equal",
            lmdResultLock, lmdRetrieve);

        // now check unlock
        resultXml = unlock(theContainerId, param);
        assertXmlValidResult(resultXml);
        resultDoc = EscidocRestSoapTestsBase.getDocument(resultXml);
        String lmdResultUnlock = getLastModificationDateValue(resultDoc);

        containerXml = retrieve(theContainerId);
        containerDoc = EscidocRestSoapTestsBase.getDocument(containerXml);
        lmdRetrieve = getLastModificationDateValue(containerDoc);

        assertEquals(
            "Last modification date of result and container not equal",
            lmdResultUnlock, lmdRetrieve);

        // assert that the last-modification-date of container hasn't changed
        assertEquals(
            "Last modification date of result and container not equal",
            lmdResultUnlock, lmdResultLock);
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

        PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);

        this.theItemId =
            createItemFromTemplate("escidoc_item_198_for_create.xml");

        String xmlData =
            getContainerTemplate("create_container_WithoutMembers_v1.1.xml");
        theContainerXml = create(xmlData);

        this.theSubcontainerId = getObjidValue(theContainerXml);

        String xmlData1 =
            getContainerTemplate("create_container_v1.1-forItemAndforContainer.xml");

        String xmlWithItem = xmlData1.replaceAll("##ITEMID##", theItemId);
        String xmlWithItemAndContainer =
            xmlWithItem.replaceAll("##CONTAINERID##", theSubcontainerId);
        theContainerXml = create(xmlWithItemAndContainer);

        this.theContainerId = getObjidValue(theContainerXml);

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
        theContainerXml = null;

        theContainerId = null;

        theSubcontainerId = null;
        // TODO purge object from Fedora
    }

    private void submitItemHelp() throws Exception {
        String param = getTheLastModificationParam(false, theItemId);
        Object result1 = getItemClient().submit(theItemId, param);
        if (result1 instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result1;
            assertHttpStatusOfMethod("", httpRes);
        }
    }
}
