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
 * Copyright 2009 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */

package de.escidoc.core.aa.ldap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;

/**
 * Object that holds LDAP-Userdata and all attributes from LDAP.
 *
 * @author Michael Hoppe
 */
public class EscidocLdapUserDetails extends LdapUserDetailsImpl {

    private static final long serialVersionUID = -3856754429168330690L;

    private final Map<String, List<String>> stringAttributes = new HashMap<String, List<String>>();

    private String dn;

    private String username;

    /**
     * Adds an attribute to the HashMap of stringAttributes.
     *
     * @param name  name of attribute
     * @param value value of attribute
     */
    public void addStringAttribute(final String name, final String value) {
        if (stringAttributes.get(name) == null) {
            stringAttributes.put(name, new ArrayList<String>());
        }
        stringAttributes.get(name).add(value);
    }

    /**
     * @return the attributes
     */
    public Map<String, List<String>> getStringAttributes() {
        return this.stringAttributes;
    }

    /**
     * Returns the value of the attribute with the given name.
     *
     * @param name name of the attribute.
     * @return one attribute with given name
     */
    public List<String> getStringAttribute(final String name) {
        return stringAttributes.get(name);
    }

    /**
     * @return the dn
     */
    @Override
    public String getDn() {
        return this.dn;
    }

    /**
     * @param dn the dn to set
     */
    public void setDn(final String dn) {
        this.dn = dn;
    }

    /**
     * @return the username
     */
    @Override
    public String getUsername() {
        return this.username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    @Override
    public int hashCode() {
        return dn.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof EscidocLdapUserDetails) {
            return dn.equals(((EscidocLdapUserDetails) obj).dn);
        }
        return false;
    }

}
