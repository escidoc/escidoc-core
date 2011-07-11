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
package de.escidoc.core.aa.business.interfaces;

import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.aa.business.persistence.UserLoginData;
import de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException;
import de.escidoc.core.common.exceptions.system.SystemException;

import java.util.List;
import java.util.Map;

/**
 * The interface for access to a user resource.
 *
 * @author Michael Schneider
 */
public interface UserAccountHandlerInterface extends de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface {

    /**
     * Retrieves the current grants of the user with the provided id in a {@code Map}.
     *
     * @param userId The User Account ID.
     * @return Returns the current Grants of the User Account in a {@code Map}.
     * @throws UserAccountNotFoundException Thrown if no user account with the provided id exists.
     * @throws SystemException              Thrown in case of an internal system error.
     */
    Map<String, Map<String, List<RoleGrant>>> retrieveCurrentGrantsAsMap(String userId)
        throws UserAccountNotFoundException, SystemException;

    /**
     * Retrieves the current eScidoc user handles of an user.
     *
     * @param userId The User Account ID.
     * @return Returns the current eSciDoc user handles of the specified User Account in a {@code Set}.
     * @throws UserAccountNotFoundException Thrown if no user with the provided id exists.
     * @throws SystemException              Thrown in case of an internal system error.
     */
    List<UserLoginData> retrieveUserHandles(String userId) throws UserAccountNotFoundException, SystemException;

}
