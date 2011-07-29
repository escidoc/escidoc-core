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
package de.escidoc.core.aa.service;

import de.escidoc.core.aa.service.interfaces.UserManagementWrapperInterface;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * The User management wrapper in service layer.
 *
 * @author Torsten Tetteroo
 */
@Service("service.UserManagementWrapper")
public class UserManagementWrapper implements UserManagementWrapperInterface {

    @Autowired
    @Qualifier("business.UserManagementWrapper")
    private de.escidoc.core.aa.business.interfaces.UserManagementWrapperInterface business;

    /**
     * Protected constructor to prevent instantiation outside of the Spring-context.
     */
    protected UserManagementWrapper() {
    }

    /**
     * See Interface for functional description.
     *
     * @see UserManagementWrapperInterface #logout()
     */
    @Override
    public void logout() throws AuthenticationException, SystemException {

        business.logout();
    }

    /**
     * See Interface for functional description.
     *
     * @param handle the handle
     * @throws AuthenticationException Thrown if the authentication fails due to an invalid provided eSciDocUserHandle.
     * @throws SystemException         Thrown in case of an internal error.
     */
    @Override
    public void initHandleExpiryTimestamp(final String handle) throws AuthenticationException, SystemException {
        business.initHandleExpiryTimestamp(handle);
    }

    /**
     * Setter for the business object.
     *
     * @param business business object.
     */
    public void setBusiness(final de.escidoc.core.aa.business.interfaces.UserManagementWrapperInterface business) {

        this.business = business;
    }

}
