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

package de.escidoc.core.common.util.security;

import de.escidoc.core.aa.service.interfaces.EscidocUserDetailsServiceInterface;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.providers.AuthenticationProvider;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.anonymous.AnonymousAuthenticationToken;
import org.springframework.security.userdetails.UserDetails;

/**
 * @author TTE
 */
public class EscidocAuthenticationProvider implements AuthenticationProvider {

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(EscidocAuthenticationProvider.class);

    private EscidocUserDetailsServiceInterface escidocUserDetailsService;



    /**
     * See Interface for functional description.
     * 
     * @param authentication
     * @return
     * @throws AuthenticationException
     * @see org.acegisecurity.providers.AuthenticationProvider
     *      #authenticate(org.acegisecurity.Authentication)
     *
     */
    @Override
    public Authentication authenticate(final Authentication authentication)
        throws AuthenticationException {
        final String credentials = (String) authentication.getCredentials();
        if (credentials == null || "".equals(credentials)) {
            final GrantedAuthority grantedAuthority =
                new GrantedAuthorityImpl("");
            final GrantedAuthority[] grantedAuthorities = { grantedAuthority };
            return new AnonymousAuthenticationToken("key", "Anonymous", grantedAuthorities);
        }
        final UserDetails userDetails = escidocUserDetailsService.loadUserByUsername(credentials);
        return new UsernamePasswordAuthenticationToken(userDetails, credentials);
    }

    /**
     * See Interface for functional description.
     * 
     * @param cls
     * @return
     * @see org.acegisecurity.providers.AuthenticationProvider
     *      #supports(java.lang.Class)
     *
     */
    @Override
    public boolean supports(final Class cls) {

        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(cls);
    }



    /**
     * Injects the {@link EscidocUserDetailsServiceInterface}.
     * 
     * @param escidocUserDetailsService
     *            the escidocUserDetailsService to inject.
     */
    public void setEscidocUserDetailsService(
        final EscidocUserDetailsServiceInterface escidocUserDetailsService) {

        this.escidocUserDetailsService = escidocUserDetailsService;
    }

}
