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
package de.escidoc.core.common.util.service;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;

/**
 * Class encapsulating the information about the user stored in the user
 * context.<br>
 * This class implements <code>UserDetails</code> and is serializable.
 * 
 * @author TTE
 * @aa
 */
public class EscidocUserDetails implements UserDetails {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /** The id of the user. */
    private String id = null;

    /** The real name of the user, as stored in the UserAccount. */
    private String realName;

    /**
     * Gets the id.
     * 
     * @return Returns the id of the user.
     * @aa
     */
    public String getId() {

        return id;
    }

    /**
     * Sets the id of the user.<br>
     * This resets the signature and the principal has to be resigned.
     * 
     * @param id
     *            The id of the user.
     * @aa
     */
    public void setId(final String id) {

        this.id = id;
    }

    /**
     * Gets the real name of the user.
     * 
     * @return Returns the real name of the user as stored in the UserAccount.
     * @aa
     */
    public String getRealName() {

        return realName;
    }

    /**
     * Sets the real name of the user.<br>
     * This resets the signature and the principal has to be resigned.
     * 
     * @param realName
     *            The real name of the user as stored in the UserAccount.
     * @aa
     */
    public void setRealName(final String realName) {

        this.realName = realName;
    }

    /**
     * Signs the principal using the provided key.
     * 
     * @param key
     *            The key. This must not be <code>null</code>. If it is
     *            <code>null</code>, nothing is done.
     * @aa
     */
    public void sign(final String key) {
    }

    /**
     * See Interface for functional description.<br>
     * This implementation returns <code>null</code>!
     * 
     * @return
     * @see org.acegisecurity.userdetails.UserDetails#getAuthorities()
     * @aa
     */
    @Override
    public GrantedAuthority[] getAuthorities() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * See Interface for functional description.<br>
     * This implementation returns <code>null</code>!
     * 
     * @return
     * @see org.acegisecurity.userdetails.UserDetails#getPassword()
     * @aa
     */
    @Override
    public String getPassword() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * See Interface for functional description.<br/> This delegates to
     * <code>getRealName()</code>
     * 
     * @return Returns the realName of the user account.
     * @see org.acegisecurity.userdetails.UserDetails#getUsername()
     * @aa
     */
    @Override
    public String getUsername() {

        return getRealName();
    }

    /**
     * See Interface for functional description.<br>
     * This implementation returns <code>false</code>!
     * 
     * @return
     * @see org.acegisecurity.userdetails.UserDetails#isAccountNonExpired()
     * @aa
     */
    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * See Interface for functional description.<br>
     * This implementation returns <code>false</code>!
     * 
     * @return
     * @see org.acegisecurity.userdetails.UserDetails#isAccountNonLocked()
     * @aa
     */
    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * See Interface for functional description.<br>
     * This implementation returns <code>false</code>!
     * 
     * @return
     * @see org.acegisecurity.userdetails.UserDetails#isCredentialsNonExpired()
     * @aa
     */
    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * See Interface for functional description.<br>
     * This implementation returns <code>false</code>!
     * 
     * @return
     * @see org.acegisecurity.userdetails.UserDetails#isEnabled()
     * @aa
     */
    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

}
