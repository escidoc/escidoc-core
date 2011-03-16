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
import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test suite for the user management wrapper.
 * 
 * @author TTE
 * 
 */
public class UserManagementWrapperAbstractTest extends UserManagementWrapperTestBase {

    public static final String UNKNOWN_ESCIDOC_USER_HANDLE = "UnknownHandle";

    private static final String PASSWORD = PWCallback.PASSWORD;

    private static final String LOGIN_NAME = PWCallback.DEPOSITOR_HANDLE;

    /**
     * The constructor.
     * 
     * @param transport
     *            The transport identifier.
     * @throws Exception
     *             If anything fails.
     */
    public UserManagementWrapperAbstractTest(final int transport) throws Exception {

        super(transport);
    }

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Before
    public void initialize() throws Exception {
    }

    /**
     * Clean up after servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @After
    public void deinitialize() throws Exception {
    }

    /**
     * Test login.
     * 
     * @test.name Login
     * @test.id AA_Login
     * @test.input
     *          <ul>
     *          <li>Valid name and password</li>
     *          <li>Valid target URL</li>
     *          </ul>
     * @test.expected: Login successful, user account retrievable using the user
     *                 handle.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testLogin() throws Exception {

        String eSciDocUserHandle = null;
        try {
            eSciDocUserHandle = login(LOGIN_NAME, PASSWORD, true);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertNotNull(eSciDocUserHandle);

        try {
            retrieve(eSciDocUserHandle);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
    }

    /**
     * Test login with providing target URL parameter that contains slashes.
     * 
     * @test.name Login - Target Url Parameter With Slashes.
     * @test.id AA_Login-2
     * @test.input
     *          <ul>
     *          <li>Valid name and password</li>
     *          <li>Valid target URL parameter that contains slashes</li>
     *          </ul>
     * @test.expected: Login successful, user account retrievable using the user
     *                 handle.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testLogin2() throws Exception {

        String eSciDocUserHandle = null;
        try {
            eSciDocUserHandle = login(LOGIN_NAME, PASSWORD, false);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertNotNull(eSciDocUserHandle);

        try {
            retrieve(eSciDocUserHandle);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
    }

    /**
     * Test multiple login.
     * 
     * @test.name Login - Multiple
     * @test.id AA_MultipleLogin
     * @test.input
     *          <ul>
     *          <li>Valid name and password</li>
     *          </ul>
     * @test.expected: Login successful, user account retrievable using the user
     *                 handles, both handle returns the same user account xml
     *                 representation.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testMultipleLogin() throws Exception {

        String eSciDocUserHandle1 = null;
        try {
            eSciDocUserHandle1 = login(LOGIN_NAME, PASSWORD, true);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertNotNull(eSciDocUserHandle1);

        String eSciDocUserHandle2 = null;
        try {
            eSciDocUserHandle2 = login(LOGIN_NAME, PASSWORD, true);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertNotNull(eSciDocUserHandle2);
        assertNotEquals("Handles of different logins do not differ.",
            eSciDocUserHandle1, eSciDocUserHandle2);

        String xml1 = null;
        try {
            PWCallback.setHandle(eSciDocUserHandle1);
            xml1 = retrieve(eSciDocUserHandle1);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        String xml2 = null;
        try {
            PWCallback.setHandle(eSciDocUserHandle2);
            xml2 = retrieve(eSciDocUserHandle2);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertNotNull(xml1);
        assertNotNull(xml2);
        assertEquals(xml1, xml2);
    }

    /**
     * Test logout.
     * 
     * @test.name Logout Authenticated User
     * @test.id AA_Logout
     * @test.input
     *          <ul>
     *          <li>Valid eSciDoc user handle</li>
     *          </ul>
     * @test.expected: Logout page is presented, no redirect occurs.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testLogout() throws Exception {

        String eSciDocUserHandle = null;
        try {
            eSciDocUserHandle = login(LOGIN_NAME, PASSWORD, true);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertNotNull(eSciDocUserHandle);

        try {
            logout(eSciDocUserHandle);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }

        try {
            PWCallback.setHandle(eSciDocUserHandle);
            retrieve(eSciDocUserHandle);
            EscidocRestSoapTestBase
                .failMissingException(AuthenticationException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                AuthenticationException.class, e);
        }
    }

    /**
     * Test logout with providing valid eSciDoc user handle and redirect target
     * URL.
     * 
     * @test.name Logout Authenticated User - Target Url
     * @test.id AA_Logout-2
     * @test.input
     *          <ul>
     *          <li>Valid eSciDoc user handle</li>
     *          <li>TargetUrl for redirect of the user after logout.</li>
     *          </ul>
     * @test.expected: Redirect to targetUrl occurs, logout page as message.
     * @test.status Implemented
     * @test.issue http://www.escidoc-project.de/issueManagement/show_bug.cgi?id=561
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testLogout2() throws Exception {

        String eSciDocUserHandle = null;
        try {
            eSciDocUserHandle = login(LOGIN_NAME, PASSWORD, true);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertNotNull(eSciDocUserHandle);

        try {
            logout("http://" + Constants.HOST_PORT, eSciDocUserHandle, true);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }

        try {
            PWCallback.setHandle(eSciDocUserHandle);
            retrieve(eSciDocUserHandle);
            EscidocRestSoapTestBase
                .failMissingException(AuthenticationException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                AuthenticationException.class, e);
        }
    }

    /**
     * Test logout with providing valid eSciDoc user handle and redirect target
     * URL that contains slashes.
     * 
     * @test.name Logout Authenticated User - Target Url Parameter With Slashes
     * @test.id AA_Logout-3
     * @test.input
     *          <ul>
     *          <li>Valid eSciDoc user handle</li>
     *          <li>TargetUrl for redirect of the user after logout. The
     *          provided targetUrl parameter contains slashes.</li>
     *          </ul>
     * @test.expected: Redirect to targetUrl occurs, logout page as message.
     * @test.status Implemented
     * @test.issue http://www.escidoc-project.de/issueManagement/show_bug.cgi?id=561
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testLogout3() throws Exception {

        String eSciDocUserHandle = null;
        try {
            eSciDocUserHandle = login(LOGIN_NAME, PASSWORD, true);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertNotNull(eSciDocUserHandle);

        try {
            logout("http://" + Constants.HOST_PORT, eSciDocUserHandle, false);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }

        try {
            PWCallback.setHandle(eSciDocUserHandle);
            retrieve(eSciDocUserHandle);
            EscidocRestSoapTestBase
                .failMissingException(AuthenticationException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                AuthenticationException.class, e);
        }
    }

    /**
     * Test logout a multiple logged in user.
     * 
     * @test.name Logout - Multiple Logged In User
     * @test.id AA_LogoutMultipleLoggedInUser
     * @test.input
     *          <ul>
     *          <li>Valid eScidoc user handle of user with more than one valid
     *          handle.</li>
     *          </ul>
     * @test.expected: Logout successful, user account retrievable using the
     *                 still valid user handle, not retrievable using the
     *                 "logged-out" handle.
     * @test.status Implemented
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testLogoutMultipleLoggedInUser() throws Exception {

        String eSciDocUserHandle1 = null;
        try {
            eSciDocUserHandle1 = login(LOGIN_NAME, PASSWORD, true);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertNotNull(eSciDocUserHandle1);

        String eSciDocUserHandle2 = null;
        try {
            eSciDocUserHandle2 = login(LOGIN_NAME, PASSWORD, true);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertNotNull(eSciDocUserHandle2);
        assertNotEquals("Handles of different logins do not differ.",
            eSciDocUserHandle1, eSciDocUserHandle2);

        try {
            logout(eSciDocUserHandle1);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        String xml2 = null;
        try {
            PWCallback.setHandle(eSciDocUserHandle2);
            xml2 = retrieve(eSciDocUserHandle2);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertNotNull(xml2);

        try {
            PWCallback.setHandle(eSciDocUserHandle1);
            retrieve(eSciDocUserHandle1);
            EscidocRestSoapTestBase
                .failMissingException(AuthenticationException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                AuthenticationException.class, e);
        }
    }

    /**
     * Test logout with providing unknown eSciDoc user handle.
     * 
     * @test.name Logout Not Authenticated User
     * @test.id AA_LogoutNotAuthenticatedUser
     * @test.input
     *          <ul>
     *          <li>Unknown eSciDoc user handle</li>
     *          </ul>
     * @test.expected: Logout page is presented to the user, no redirect occurs.
     * @test.status Implemented
     * @test.issue http://www.escidoc-project.de/issueManagement/show_bug.cgi?id=561
     * 
     * @throws Exception
     *             If anything fails.
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
     * Test logout with providing unknown eSciDoc user handle and redirect
     * target URL.
     * 
     * @test.name Logout Not Authenticated User - Target Url
     * @test.id AA_LogoutNotAuthenticatedUser-2
     * @test.input
     *          <ul>
     *          <li>Unknown eSciDoc user handle</li>
     *          <li>TargetUrl for redirect of the user after logout.</li>
     *          </ul>
     * @test.expected: Redirect to targetUrl occurs, logout page as message.
     * @test.status Implemented
     * @test.issue http://www.escidoc-project.de/issueManagement/show_bug.cgi?id=561
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testLogoutNotAuthenticatedUser2() throws Exception {

        try {
            PWCallback.setHandle(UNKNOWN_ESCIDOC_USER_HANDLE);
            logout("http://" + Constants.HOST_PORT);
        }
        catch (final Exception e) {
            failException(e);
        }
    }

    /**
     * Test logout without providing an eSciDoc user handle.
     * 
     * @test.name Logout - No cookie
     * @test.id AA_LogoutNoCookie
     * @test.input
     *          <ul>
     *          <li>No eSciDoc user handle is provided in a cookie</li>
     *          </ul>
     * @test.expected: Logout page is presented to the user, no redirect occurs.
     * @test.status Implemented
     * @test.issue http://www.escidoc-project.de/issueManagement/show_bug.cgi?id=561
     * 
     * @throws Exception
     *             If anything fails.
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
     * Test logout without providing an eSciDoc user handle but providing a
     * redirect target URL.
     * 
     * @test.name Logout - Target Url, No Cookie
     * @test.id AA_LogoutNoCookie-2
     * @test.input
     *          <ul>
     *          <li>No eSciDoc user handle is provided in a cookie</li>
     *          <li>TargetUrl for redirect of the user after logout.</li>
     *          </ul>
     * @test.expected: Redirect to targetUrl occurs, logout page as message.
     * @test.status Implemented
     * @test.issue http://www.escidoc-project.de/issueManagement/show_bug.cgi?id=561
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testLogoutNoCookie2() throws Exception {

        try {
            PWCallback.setHandle(UNKNOWN_ESCIDOC_USER_HANDLE);
            logout("http://" + Constants.HOST_PORT);
        }
        catch (final Exception e) {
            failException(e);
        }
    }

}
