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

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class ShibbolethToken extends AbstractAuthenticationToken {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = -1450643513934137420L;

    private final ShibbolethUser user;

    public ShibbolethToken(final ShibbolethUser user, final Collection<GrantedAuthority> arg0) {
        super(arg0);
        this.user = user;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @return Returns {@code null} as the user credentials are unknown to the shibboleth service provider.
     */
    @Override
    public Object getCredentials() {

        return null;
    }

    /**
     * See Interface for functional description.
     *
     * @see AbstractAuthenticationToken#getPrincipal()
     */
    @Override
    public Object getPrincipal() {

        return this.user;
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public String getName() {

        return user.getName();
    }

}
