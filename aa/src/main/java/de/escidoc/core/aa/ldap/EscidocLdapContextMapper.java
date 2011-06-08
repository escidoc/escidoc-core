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
import java.util.Collection;
import java.util.HashSet;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import de.escidoc.core.aa.business.authorisation.Constants;

/**
 * Customized Springsecurity-ContextMapper. Writes all attributes from LDAP into EscidocLdapUserDetails-Object.
 *
 * @author Michael Hoppe
 */
public class EscidocLdapContextMapper implements UserDetailsContextMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(EscidocLdapContextMapper.class);

    private static final Collection<String> IGNORED_VALUES = new HashSet<String>();

    static {
        IGNORED_VALUES.add("objectClass");
    }

    /**
     * Writes data from LDAP into EscidocLdapUserDetails-Object.
     *
     * @param ctx       DirContextOperations
     * @param username  name of user
     * @param authority array of granted authorities
     * @return UserDetails object with userDetails
     */
    @Override
    public UserDetails mapUserFromContext(
        final DirContextOperations ctx, final String username, final Collection<GrantedAuthority> authority) {
        final EscidocLdapUserDetails user = new EscidocLdapUserDetails();

        final String dn = ctx.getNameInNamespace();
        user.setDn(dn);

        user.setUsername(username);

        final Collection<GrantedAuthority> compare = new ArrayList<GrantedAuthority>();
        for (final GrantedAuthority anAuthority : authority) {
            if (!compare.contains(anAuthority)) {
                user.addStringAttribute(Constants.GROUP_ATTRIBUTE_NAME, anAuthority.getAuthority());
                compare.add(anAuthority);
            }
        }

        try {
            final Attributes atts = ctx.getAttributes("");
            if (atts != null) {
                final NamingEnumeration<? extends Attribute> enumer = atts.getAll();
                if (enumer != null) {
                    while (enumer.hasMoreElements()) {
                        final Attribute attribute = enumer.nextElement();
                        final String key = attribute.getID();
                        if (!IGNORED_VALUES.contains(key)) {
                            final NamingEnumeration<?> values = attribute.getAll();
                            while (values.hasMoreElements()) {
                                try {
                                    final String val = (String) values.nextElement();
                                    if (val != null && val.length() != 0) {
                                        user.addStringAttribute(key, val);
                                    }
                                }
                                catch (final Exception e) {
                                    if (LOGGER.isWarnEnabled()) {
                                        LOGGER.warn("Error on setting attribute.");
                                    }
                                    if (LOGGER.isDebugEnabled()) {
                                        LOGGER.debug("Error on setting attribute.", e);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (final NamingException e) {
            throw new AuthenticationCredentialsNotFoundException("User-Attributes not found", e);
        }

        return user;
    }

    /**
     * See interface for detailed description.
     *
     * @param arg0 UserDetails
     * @param arg1 DirContextAdapter
     */
    @Override
    public void mapUserToContext(final UserDetails arg0, final DirContextAdapter arg1) {
        // TODO Auto-generated method stub

    }

}
