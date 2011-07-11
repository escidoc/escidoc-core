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
package de.escidoc.core.test.aa;

import de.escidoc.core.common.exceptions.remote.application.security.AuthenticationException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test suite for the user management wrapper.
 *
 * @author Torsten Tetteroo
 */
public class UserManagementWrapperIT extends UserAccountTestBase {

    public static final String UNKNOWN_ESCIDOC_USER_HANDLE = "UnknownHandle";

    private static final String PASSWORD = PWCallback.PASSWORD;

    private static final String LOGIN_NAME = PWCallback.DEPOSITOR_HANDLE;

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void initialize() throws Exception {
    }

    /**
     * Clean up after servlet test.
     *
     * @throws Exception If anything fails.
     */
    @After
    public void deinitialize() throws Exception {
    }

    /**
     * Test login.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testLogin() throws Exception {

        String eSciDocUserHandle = null;
        try {
            eSciDocUserHandle = login(LOGIN_NAME, PASSWORD, true);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNotNull(eSciDocUserHandle);

        try {
            retrieve(eSciDocUserHandle);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
    }

    /**
     * Test login with providing target URL parameter that contains slashes.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testLogin2() throws Exception {

        String eSciDocUserHandle = null;
        try {
            eSciDocUserHandle = login(LOGIN_NAME, PASSWORD, false);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNotNull(eSciDocUserHandle);

        try {
            retrieve(eSciDocUserHandle);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
    }

    /**
     * Test multiple login.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testMultipleLogin() throws Exception {

        String eSciDocUserHandle1 = null;
        try {
            eSciDocUserHandle1 = login(LOGIN_NAME, PASSWORD, true);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNotNull(eSciDocUserHandle1);

        String eSciDocUserHandle2 = null;
        try {
            eSciDocUserHandle2 = login(LOGIN_NAME, PASSWORD, true);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNotNull(eSciDocUserHandle2);
        assertNotEquals("Handles of different logins do not differ.", eSciDocUserHandle1, eSciDocUserHandle2);

        String xml1 = null;
        try {
            PWCallback.setHandle(eSciDocUserHandle1);
            xml1 = retrieve(eSciDocUserHandle1);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        String xml2 = null;
        try {
            PWCallback.setHandle(eSciDocUserHandle2);
            xml2 = retrieve(eSciDocUserHandle2);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNotNull(xml1);
        assertNotNull(xml2);
        assertEquals(xml1, xml2);
    }

    /**
     * Test logout.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testLogout() throws Exception {

        String eSciDocUserHandle = null;
        try {
            eSciDocUserHandle = login(LOGIN_NAME, PASSWORD, true);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNotNull(eSciDocUserHandle);

        try {
            logout(eSciDocUserHandle);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }

        try {
            PWCallback.setHandle(eSciDocUserHandle);
            retrieve(eSciDocUserHandle);
            EscidocAbstractTest.failMissingException(AuthenticationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(AuthenticationException.class, e);
        }
    }

    /**
     * Test logout with providing valid eSciDoc user handle and redirect target URL.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testLogout2() throws Exception {

        String eSciDocUserHandle = null;
        try {
            eSciDocUserHandle = login(LOGIN_NAME, PASSWORD, true);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNotNull(eSciDocUserHandle);

        try {
            logout(getFrameworkUrl(), eSciDocUserHandle, true);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }

        try {
            PWCallback.setHandle(eSciDocUserHandle);
            retrieve(eSciDocUserHandle);
            EscidocAbstractTest.failMissingException(AuthenticationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(AuthenticationException.class, e);
        }
    }

    /**
     * Test logout with providing valid eSciDoc user handle and redirect target URL that contains slashes.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testLogout3() throws Exception {

        String eSciDocUserHandle = null;
        try {
            eSciDocUserHandle = login(LOGIN_NAME, PASSWORD, true);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNotNull(eSciDocUserHandle);

        try {
            logout(getFrameworkUrl(), eSciDocUserHandle, false);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }

        try {
            PWCallback.setHandle(eSciDocUserHandle);
            retrieve(eSciDocUserHandle);
            EscidocAbstractTest.failMissingException(AuthenticationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(AuthenticationException.class, e);
        }
    }

    /**
     * Test logout a multiple logged in user.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testLogoutMultipleLoggedInUser() throws Exception {

        String eSciDocUserHandle1 = null;
        try {
            eSciDocUserHandle1 = login(LOGIN_NAME, PASSWORD, true);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNotNull(eSciDocUserHandle1);

        String eSciDocUserHandle2 = null;
        try {
            eSciDocUserHandle2 = login(LOGIN_NAME, PASSWORD, true);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNotNull(eSciDocUserHandle2);
        assertNotEquals("Handles of different logins do not differ.", eSciDocUserHandle1, eSciDocUserHandle2);

        try {
            logout(eSciDocUserHandle1);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        String xml2 = null;
        try {
            PWCallback.setHandle(eSciDocUserHandle2);
            xml2 = retrieve(eSciDocUserHandle2);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNotNull(xml2);

        try {
            PWCallback.setHandle(eSciDocUserHandle1);
            retrieve(eSciDocUserHandle1);
            EscidocAbstractTest.failMissingException(AuthenticationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(AuthenticationException.class, e);
        }
    }

    /**
     * Test logout with providing unknown eSciDoc user handle.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testLogoutNotAuthenticatedUser() throws Exception {

        try {
            logout(UNKNOWN_ESCIDOC_USER_HANDLE);
        }
        catch (final Exception e) {
            failException(e);
        }
    }

    /**
     * Test logout with providing unknown eSciDoc user handle and redirect target URL.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testLogoutNotAuthenticatedUser2() throws Exception {

        try {
            PWCallback.setHandle(UNKNOWN_ESCIDOC_USER_HANDLE);
            logout(getFrameworkUrl());
        }
        catch (final Exception e) {
            failException(e);
        }
    }

    /**
     * Test logout without providing an eSciDoc user handle.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testLogoutNoCookie() throws Exception {

        try {
            logout(null);
        }
        catch (final Exception e) {
            failException(e);
        }
    }

    /**
     * Test logout without providing an eSciDoc user handle but providing a redirect target URL.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testLogoutNoCookie2() throws Exception {

        try {
            PWCallback.setHandle(UNKNOWN_ESCIDOC_USER_HANDLE);
            logout(getFrameworkUrl());
        }
        catch (final Exception e) {
            failException(e);
        }
    }

}
