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

import java.io.IOException;
import java.security.Principal;
import java.security.acl.Group;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.auth.spi.AbstractServerLoginModule;

import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.string.StringUtility;

/**
 * 
 * A server side login module.
 * 
 * @author Michael Hoppe
 */
public class QueueConnectionLoginModule extends AbstractServerLoginModule {

    private static final AppLogger LOG =
        new AppLogger(QueueConnectionLoginModule.class.getName());

    private Principal identity;

    /**
     * Get the identity.<br>
     * See Interface for functional description.
     * 
     * @return Principal
     * @see org.jboss.security.auth.spi.AbstractServerLoginModule#getIdentity()
     */
    @Override
    protected Principal getIdentity() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("QueueConnectionLoginModule:getIdentity()");
        }
        return identity;
    }

    /**
     * Sets the user principal.
     * 
     * @param principal
     *            the user information
     */
    protected void setIdentity(final Principal principal) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("QueueConnectionLoginModule:setIdentity()");
        }
        identity = principal;
    }

    /**
     * Used by the container to verify the user.<br>
     * See Interface for functional description.
     * 
     * @return boolean state of the login
     * @throws LoginException
     *             e
     * @see org.jboss.security.auth.spi.AbstractServerLoginModule#login()
     */
    @Override
    public boolean login() throws LoginException {
        identity = org.jboss.security.SecurityAssociation.getPrincipal();

        if (identity == null) {
            identity = new SimplePrincipal("");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("LoginModule - Principal: '" +
                identity.getName()+ "'.");
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
        // Your implementation should lookup what roles
        // the user, identified by the identity principal, is in
        // from a database, or some group management
        // system you have in place.
        Group rolesGroup = new SimpleGroup("Roles");
        try {
            String[] usernamePassword = getUsernameAndPassword();
            String username = "";
            String password = "";
            String requiredUsername = "";
            String requiredPassword = "";
            username = usernamePassword[0];
            password = usernamePassword[1];
            try {
                requiredUsername =
                    EscidocConfiguration.getInstance().get(
                        EscidocConfiguration.ESCIDOC_CORE_QUEUE_USER);
                requiredPassword =
                    EscidocConfiguration.getInstance().get(
                        EscidocConfiguration.ESCIDOC_CORE_QUEUE_PASSWORD);
            }
            catch (IOException e) {
                log.error(e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("username: " + username + " password: " + password);
            }
            rolesGroup.addMember(new SimplePrincipal("guest"));
            if (requiredUsername.equals(username)
                && requiredPassword.equals(password)) {
                rolesGroup.addMember(new SimplePrincipal("Administrator"));
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(StringUtility.concatenateWithBracketsToString(
                    "QueueConnectionLoginModule:getRoleSets()", rolesGroup));
            }
        }
        catch (Exception e) {
            LOG.error(e);
        }
        return new Group[] { rolesGroup };
    }

    /**
     * Get username and password.<br>
     * 
     * @return String[] with username as element 0 and password as element 1.
     * @throws LoginException
     *             e
     */
    protected String[] getUsernameAndPassword() throws LoginException {
        String[] info = { null, null };
        if (callbackHandler == null) {
            throw new LoginException("Error: no CallbackHandler available "
                + "to collect authentication information");
        }
        NameCallback nc = new NameCallback("User name: ", "guest");
        PasswordCallback pc = new PasswordCallback("Password: ", false);
        Callback[] callbacks = { nc, pc };
        String username = null;
        String password = null;
        try {
            callbackHandler.handle(callbacks);
            username = nc.getName();
            char[] tmpPassword = pc.getPassword();
            if (tmpPassword != null) {
                char[] credential = new char[tmpPassword.length];
                System.arraycopy(tmpPassword, 0, credential, 0,
                    tmpPassword.length);
                pc.clearPassword();
                password = new String(credential);
            }
        }
        catch (IOException ioe) {
            throw new LoginException(ioe.toString());
        }
        catch (UnsupportedCallbackException uce) {
            throw new LoginException("CallbackHandler does not support: "
                + uce.getCallback());
        }
        info[0] = username;
        info[1] = password;
        return info;
    }

}
