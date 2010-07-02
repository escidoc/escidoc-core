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

import java.security.Principal;
import java.security.acl.Group;

import javax.security.auth.login.LoginException;

import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.auth.spi.AbstractServerLoginModule;
import org.jboss.security.auth.spi.AnonLoginModule;

import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.string.StringUtility;

/**
 * A server side login module that always returns a simple principal with empty
 * name and with role "eSciDocUser".
 * 
 * @author Bernhard Kraus (Accenture)
 * @deprecated As this module just provides anonymous login, it should be
 *             removed and in the jboss login-config.xml the
 *             {@link AnonLoginModule} should be used instead:
 *             &lt;application-policy name="escidoc"&gt; &lt;authentication&gt;
 *             &lt;login-module
 *             code="org.jboss.security.auth.spi.AnonLoginModule"
 *             flag="required"&gt; &lt;module-option
 *             name="unauthenticatedIdentity"&gt;anonymous&lt;/module-option&gt;
 *             &lt;/login-module&gt; &lt;/authentication&gt;
 *             &lt;/application-policy&gt;
 * 
 */
@Deprecated
public class CustomServerLoginModule extends AbstractServerLoginModule {

    private static final AppLogger LOG =
        new AppLogger(CustomServerLoginModule.class.getName());

    private Principal identity;

    /**
     * Get the identity.<br>
     * See Interface for functional description.
     * 
     * @return
     * @see org.jboss.security.auth.spi.AbstractServerLoginModule#getIdentity()
     */
    @Override
    protected Principal getIdentity() {
        LOG.debug("CustomServerLoginModule:getIdentity()");
        return identity;
    }

    /**
     * Sets the user principal.
     * 
     * @param principal
     *            the user information
     */
    protected void setIdentity(final Principal principal) {
        LOG.debug("CustomServerLoginModule:setIdentity()");
        identity = principal;
    }

    /**
     * Used by the container to verify the user.<br>
     * See Interface for functional description.
     * 
     * @return boolean state of the login
     * @throws LoginException
     * @see org.jboss.security.auth.spi.AbstractServerLoginModule#login()
     */
    @Override
    public boolean login() throws LoginException {
        identity = org.jboss.security.SecurityAssociation.getPrincipal();

        if (identity == null) {
            identity = new SimplePrincipal("");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(StringUtility.concatenate("LoginModule - Principal: '",
                identity.getName(), "'.").toString());
        }
        loginOk = true;
        return true;
    }

    /**
     * Get the role sets.<br>
     * See Interface for functional description.
     * 
     * @return Empty array of {@link Group}s for the identity
     * @see org.jboss.security.auth.spi.AbstractServerLoginModule #getRoleSets()
     */
    @Override
    public Group[] getRoleSets() {
        Group rolesGroup = new SimpleGroup("Roles");
        rolesGroup.addMember(new SimplePrincipal("eSciDocUser"));

        if (LOG.isDebugEnabled()) {
            LOG.debug(StringUtility.concatenateWithBracketsToString(
                "CustomServerLoginModule:getRoleSets()", rolesGroup));
        }
        return new Group[] { rolesGroup };
    }
}
