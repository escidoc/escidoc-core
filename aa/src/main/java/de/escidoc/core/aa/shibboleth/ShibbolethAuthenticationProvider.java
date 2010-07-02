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
     * @see org.springframework.security.providers.AuthenticationProvider
     *      #authenticate(org.springframework.security.Authentication)
     * @aa
     */
    public Authentication authenticate(final Authentication authentication)
        throws AuthenticationException {

        if (!supports(authentication.getClass())) {
            return null;
        }
        else {
            return authentication;
        }
    }

    /**
     * See Interface for functional description.
     * 
     * @param authentication
     * @return
     * @see org.springframework.security.providers.AuthenticationProvider
     *      #supports(java.lang.Class)
     * @aa
     */
    public boolean supports(final Class authentication) {

        return ShibbolethToken.class.isAssignableFrom(authentication);
    }

}
