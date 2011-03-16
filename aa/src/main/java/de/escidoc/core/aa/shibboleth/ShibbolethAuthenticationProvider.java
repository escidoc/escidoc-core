package de.escidoc.core.aa.shibboleth;

import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.providers.AuthenticationProvider;

public class ShibbolethAuthenticationProvider implements AuthenticationProvider {

    /**
     * See Interface for functional description.
     * 
     * @param authentication
     * @return
     * @throws AuthenticationException
     * @see AuthenticationProvider
     *      #authenticate(org.springframework.security.Authentication)
     *
     */
    @Override
    public Authentication authenticate(final Authentication authentication)
        throws AuthenticationException {

        return supports(authentication.getClass()) ? authentication : null;
    }

    /**
     * See Interface for functional description.
     * 
     * @param authentication
     * @return
     * @see AuthenticationProvider
     *      #supports(java.lang.Class)
     *
     */
    @Override
    public boolean supports(final Class authentication) {

        return ShibbolethToken.class.isAssignableFrom(authentication);
    }

}
