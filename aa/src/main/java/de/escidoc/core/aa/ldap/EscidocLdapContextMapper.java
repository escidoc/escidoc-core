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

import de.escidoc.core.aa.business.authorisation.Constants;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.AuthenticationCredentialsNotFoundException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.ldap.UserDetailsContextMapper;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Customized Springsecurity-ContextMapper.
 * Writes all attributes from LDAP into EscidocLdapUserDetails-Object.
 * 
 * @author MIH
 * @aa
 */
public class EscidocLdapContextMapper implements UserDetailsContextMapper {
    
    private static final Collection<String> IGNORED_VALUES = new HashSet<String>() {
        private static final long serialVersionUID = -5594739057849019019L;
        { add("objectClass"); } };
    
    /**
     * Writes data from LDAP into EscidocLdapUserDetails-Object.
     * 
     * @param ctx DirContextOperations
     * @param username name of user
     * @param authority array of granted authorities
     * 
     * @return UserDetails object with userDetails
     * 
     * @aa
     */
    public final UserDetails mapUserFromContext(final DirContextOperations ctx,
                                                final String username, final GrantedAuthority[] authority) {
        EscidocLdapUserDetails user = new EscidocLdapUserDetails();

        String dn = ctx.getNameInNamespace();
        user.setDn(dn);
        
        user.setUsername(username);

        Collection<GrantedAuthority> compare = new ArrayList<GrantedAuthority>();
        for (GrantedAuthority anAuthority : authority) {
            if (!compare.contains(anAuthority)) {
                user.addStringAttribute(
                        Constants.GROUP_ATTRIBUTE_NAME,
                        anAuthority.getAuthority());
                compare.add(anAuthority);
            }
        }

        Attributes atts;
        try {
            atts = ctx.getAttributes("");
            if (atts != null) {
                NamingEnumeration< ? extends Attribute> enumer = atts.getAll();
                if (enumer != null) {
                    while (enumer.hasMoreElements()) {
                        Attribute attribute =  enumer.nextElement();
                        String key = attribute.getID();
                        if (!IGNORED_VALUES.contains(key)) {
                            NamingEnumeration< ? > values = attribute.getAll();
                            while (values.hasMoreElements()) {
                                try {
                                    String val = (String) values.nextElement();
                                    if (val != null && val.length() != 0) {
                                        user.addStringAttribute(key, val);
                                    }
                                } catch (Exception e) {}
                            }
                        }
                    }
                }
            }
        } catch (NamingException e) {
            throw new AuthenticationCredentialsNotFoundException(
                    "User-Attributes not found");
        }



        return user;
    }

    /**
     * See interface for detailed description.
     * 
     * @param arg0 UserDetails
     * @param arg1 DirContextAdapter
     * 
     * @aa
     */
    public final void mapUserToContext(
            final UserDetails arg0,
            final DirContextAdapter arg1) {
        // TODO Auto-generated method stub

    }

}
