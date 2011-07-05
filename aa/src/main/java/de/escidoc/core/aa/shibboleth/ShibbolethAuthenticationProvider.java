package de.escidoc.core.aa.shibboleth;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;

public class ShibbolethAuthenticationProvider implements AuthenticationProvider {

    /**
     * See Interface for functional description.
     *
     * @see AuthenticationProvider #authenticate(org.springframework.security.Authentication)
     */
    @Override
    public Authentication authenticate(final Authentication authentication) {

        return supports(authentication.getClass()) ? authentication : null;
    }

    /**
     * See Interface for functional description.
     *
     * @see AuthenticationProvider #supports(java.lang.Class)
     */
    @Override
    public boolean supports(final Class authentication) {

        return ShibbolethToken.class.isAssignableFrom(authentication);
    }

}
