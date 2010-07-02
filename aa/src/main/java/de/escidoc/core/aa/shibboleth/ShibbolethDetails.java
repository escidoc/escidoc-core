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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.shibboleth;

/**
 * Class encapsulating the details of a shibboleth session.
 * 
 * @author TTE
 * @aa
 * 
 */
public class ShibbolethDetails {

    public static final String SHIB_APPLICATION_ID = "Shib-Application-ID";

    public static final String SHIB_ASSERTION_COUNT = "Shib-Assertion-Count";

    public static final String SHIB_AUTHENTICATION_METHOD =
        "Shib-Authentication-Method";

    public static final String SHIB_AUTHENTICATION_INSTANT =
        "Shib-Authentication-Instant";

    public static final String SHIB_AUTHNCONTEXT_CLASS =
        "Shib-AuthnContext-Class";

    public static final String SHIB_AUTHNCONTEXT_DECL =
        "Shib-AuthnContext-Decl";

    public static final String SHIB_SESSION_ID = "Shib-Session-ID";

    public static final String SHIB_IDENTITY_PROVIDER =
        "Shib-Identity-Provider";

    private final String shibApplicationId;

    private final String shibAssertionCount;

    private final String shibAuthenticationMethod;

    private final String shibAuthenticationInstant;

    private final String shibAuthnContextClass;

    private final String shibAuthContextDecl;

    private final String shibIdentityProvider;

    private final String shibSessionId;

    /**
     * Constructs a {@link ShibbolethDetails} object.
     * 
     * @param shibApplicationId
     *            TODO
     * @param shibAssertionCount
     *            The value of the request header "Shib-Assertion-Count"
     * @param shibAuthenticationMethod
     *            The value of the request header "Shib-Authentication-Method"
     * @param shibAuthenticationInstant
     *            The value of the request header "Shib-Authentication-Instant"
     * @param shibAuthnContextClass
     *            The value of the request header "Shib-AuthnContext-Class"
     * @param shibAuthContextDecl
     *            The value of the request header "Shib-AuthnContext-Decl"
     * @param shibIdentityProvider
     *            The value of the request header "Shib-Identity-Provider"
     * @param shibSessionId
     *            The value of the request header "Shib-Session-ID"
     * 
     * @aa
     */
    public ShibbolethDetails(String shibApplicationId,
        final String shibAssertionCount, final String shibAuthenticationMethod,
        final String shibAuthenticationInstant,
        final String shibAuthnContextClass, final String shibAuthContextDecl,
        final String shibIdentityProvider, final String shibSessionId) {

        super();
        this.shibApplicationId = shibApplicationId;
        this.shibAssertionCount = shibAssertionCount;
        this.shibAuthenticationMethod = shibAuthenticationMethod;
        this.shibAuthenticationInstant = shibAuthenticationInstant;
        this.shibAuthnContextClass = shibAuthnContextClass;
        this.shibAuthContextDecl = shibAuthContextDecl;
        this.shibIdentityProvider = shibIdentityProvider;
        this.shibSessionId = shibSessionId;
    }

    /**
     * @return Returns the value of the request header "Shib_Application_ID".
     * @aa
     */
    public String getShibApplicationId() {
        return shibApplicationId;
    }

    /**
     * @return Returns the value of the request header "Shib-Assertion-Count".
     * @aa
     */
    public String getShibAssertionCount() {
        return shibAssertionCount;
    }

    /**
     * @return Returns the value of the request header
     *         "Shib-Authentication-Method".
     * @aa
     */
    public String getShibAuthenticationMethod() {
        return shibAuthenticationMethod;
    }

    /**
     * @return Returns the value of the request header
     *         "Shib-Authentication-Instant".
     * @aa
     */
    public String getShibAuthenticationInstant() {
        return shibAuthenticationInstant;
    }

    /**
     * @return Returns the value of the request header
     *         "Shib-AuthnContext-Class".
     * @aa
     */
    public String getShibAuthnContextClass() {
        return shibAuthnContextClass;
    }

    /**
     * @return Returns the value of the request header "Shib-AuthnContext-Decl".
     * @aa
     */
    public String getShibAuthContextDecl() {
        return shibAuthContextDecl;
    }

    /**
     * @return Returns the value of the request header "Shib-Identity-Provider".
     * @aa
     */
    public String getShibIdentityProvider() {
        return shibIdentityProvider;
    }

    /**
     * @return Returns the value of the request header "Shib-Session-ID".
     * @aa
     */
    public String getShibSessionId() {
        return shibSessionId;
    }

}
