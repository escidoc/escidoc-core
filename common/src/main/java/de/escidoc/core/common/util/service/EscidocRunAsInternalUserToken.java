/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.common.util.service;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * {@link Authentication} implementation for indicating the access of the user identified by a wrapped {@link
 * Authentication} shall be executed as the internal user, that is not further authorized.
 *
 * @author Torsten Tetteroo
 */
public class EscidocRunAsInternalUserToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 2667370146760424716L;

    /**
     * The wrapped {@link Authentication}.
     */
    private final Authentication orginalAuthentication;

    /**
     * Constructs an EscidocRunAsInternalUserToken.
     *
     * @param orginalAuthentication The original {@link Authentication} whose access shall be executed as the internal
     *                              user without further authorizations.
     */
    public EscidocRunAsInternalUserToken(final Authentication orginalAuthentication) {

        super(orginalAuthentication.getAuthorities());
        this.orginalAuthentication = orginalAuthentication;
        setDetails(orginalAuthentication.getDetails());
        setAuthenticated(true);
    }

    /**
     * Gets the original {@link Authentication}.
     *
     * @return Returns the original {@link Authentication}.
     */
    public Authentication getOrginalAuthentication() {
        return this.orginalAuthentication;
    }

    /**
     * See Interface for functional description.
     *
     * @see AbstractAuthenticationToken #getCredentials()
     */
    @Override
    public Object getCredentials() {

        return orginalAuthentication.getCredentials();
    }

    /**
     * See Interface for functional description.
     *
     * @see AbstractAuthenticationToken #getPrincipal()
     */
    @Override
    public Object getPrincipal() {

        return orginalAuthentication.getPrincipal();
    }

    /**
     * See Interface for functional description.
     *
     * @see AbstractAuthenticationToken #getDetails()
     */
    @Override
    public Object getDetails() {

        return orginalAuthentication.getDetails();
    }

    /**
     * See Interface for functional description.
     *
     * @see AbstractAuthenticationToken #getAuthorities()
     */
    @Override
    public Collection<GrantedAuthority> getAuthorities() {

        return orginalAuthentication.getAuthorities();
    }

    /**
     * See Interface for functional description.
     *
     * @see AbstractAuthenticationToken #getName()
     */
    @Override
    public String getName() {

        return orginalAuthentication.getName();
    }

}
