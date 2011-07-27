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
package de.escidoc.core.aa.business;

import de.escidoc.core.aa.business.interfaces.UserManagementWrapperInterface;
import de.escidoc.core.aa.business.persistence.UserAccountDaoInterface;
import de.escidoc.core.aa.business.persistence.UserLoginData;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.string.StringUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Implementation of a wrapper of an external user management.
 *
 * @author Torsten Tetteroo
 */
@Service("business.UserManagementWrapper")
public class UserManagementWrapper implements UserManagementWrapperInterface {

    private static final String ERROR_MSG_LOGOUT_HANDLE_NULL =
        "Handle of current user not initialized, logout cannot be performed.";

    @Autowired
    @Qualifier("persistence.UserAccountDao")
    private UserAccountDaoInterface dao;

    /**
     * The time span during that the eSciDoc user handle is valid (in milli seconds).
     */
    private long eSciDocUserHandleLifetime = Long.MIN_VALUE;

    /**
     * Private constructor to prevent initialization.
     */
    protected UserManagementWrapper() {
    }

    /**
     * Setter for the dao.
     *
     * @param dao The data access object.
     */
    public void setDao(final UserAccountDaoInterface dao) {

        this.dao = dao;
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.aa.service.interfaces.UserManagementWrapperInterface #logout()
     */
    @Override
    public void logout() throws AuthenticationException, SqlDatabaseSystemException, WebserverSystemException {

        if (UserContext.isAnonymousUser()) {
            return;
        }

        final String handle = UserContext.getHandle();
        if (handle == null) {
            throw new WebserverSystemException(ERROR_MSG_LOGOUT_HANDLE_NULL);
        }
        dao.deleteUserLoginData(handle);
    }

    /**
     * See Interface for functional description.
     *
     * @param handle the handle
     */
    @Override
    public void initHandleExpiryTimestamp(final String handle) throws SqlDatabaseSystemException,
        WebserverSystemException {
        final UserLoginData userLoginData = dao.retrieveUserLoginDataByHandle(handle);
        final long expiryts = System.currentTimeMillis() + getESciDocUserHandleLifetime();
        if (userLoginData.getExpiryts() < expiryts) {
            if (userLoginData.getExpiryts() < System.currentTimeMillis()) {
                throw new WebserverSystemException("Handle-expiry-timestamp cannot get "
                    + "reinitialized on expired handles");
            }
            userLoginData.setExpiryts(expiryts);
            dao.saveOrUpdate(userLoginData);
        }
    }

    /**
     * @return the eSciDocUserHandleLifetime.
     * @throws WebserverSystemException Thrown if access to configuration properties fails.
     */
    private long getESciDocUserHandleLifetime() throws WebserverSystemException {

        if (this.eSciDocUserHandleLifetime == Long.MIN_VALUE) {
            try {
                this.eSciDocUserHandleLifetime =
                    Long.parseLong(EscidocConfiguration.getInstance().get(
                        EscidocConfiguration.ESCIDOC_CORE_USERHANDLE_LIFETIME));
            }
            catch (final Exception e) {
                throw new WebserverSystemException(StringUtility.format("Can't get configuration parameter",
                    EscidocConfiguration.ESCIDOC_CORE_USERHANDLE_LIFETIME, e.getMessage()), e);
            }
        }
        return this.eSciDocUserHandleLifetime;
    }

}
