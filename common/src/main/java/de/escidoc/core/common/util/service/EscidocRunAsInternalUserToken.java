package de.escidoc.core.common.util.service;

import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.providers.AbstractAuthenticationToken;

/**
 * {@link Authentication} implementation for indicating the access of the user
 * identified by a wrapped {@link Authentication} shall be executed as the
 * internal user, that is not further authorized.
 * 
 * @author TTE
 * @common
 * 
 */
public class EscidocRunAsInternalUserToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 2667370146760424716L;
    /**
     * The wrapped {@link Authentication}.
     */
    private final Authentication orginalAuthentication;

    /**
     * Constructs an {@link EscidocRunAsInternalUserToken}.
     * 
     * @param orginalAuthentication
     *            The original {@link Authentication} whose access shall be
     *            executed as the internal user without further authorizations.
     */
    public EscidocRunAsInternalUserToken(
        final Authentication orginalAuthentication) {

        super(orginalAuthentication.getAuthorities());
        this.orginalAuthentication = orginalAuthentication;
        setDetails(orginalAuthentication.getDetails());
        setAuthenticated(true);
    }

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * Gets the original {@link Authentication}.
     * 
     * @return Returns the original {@link Authentication}.
     * @common
     */
    public Authentication getOrginalAuthentication() {
        return orginalAuthentication;
    }

    /**
     * See Interface for functional description.
     * 
     * @return
     * @see org.springframework.security.providers.AbstractAuthenticationToken
     *      #getCredentials()
     * @common
     */
    @Override
    public Object getCredentials() {

        return orginalAuthentication.getCredentials();
    }

    /**
     * See Interface for functional description.
     * 
     * @return
     * @see org.springframework.security.providers.AbstractAuthenticationToken
     *      #getPrincipal()
     * @common
     */
    @Override
    public Object getPrincipal() {

        return orginalAuthentication.getPrincipal();
    }

    /**
     * See Interface for functional description.
     * 
     * @return
     * @see org.springframework.security.providers.AbstractAuthenticationToken
     *      #getDetails()
     * @common
     */
    @Override
    public Object getDetails() {

        return orginalAuthentication.getDetails();
    }

    /**
     * See Interface for functional description.
     * 
     * @return
     * @see org.springframework.security.providers.AbstractAuthenticationToken
     *      #getAuthorities()
     * @common
     */
    @Override
    public GrantedAuthority[] getAuthorities() {

        return orginalAuthentication.getAuthorities();
    }

    /**
     * See Interface for functional description.
     * 
     * @return
     * @see org.springframework.security.providers.AbstractAuthenticationToken
     *      #getName()
     * @common
     */
    @Override
    public String getName() {

        return orginalAuthentication.getName();
    }

    // CHECKSTYLE:JAVADOC-ON

}
