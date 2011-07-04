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
package de.escidoc.core.aa.business.persistence;

import java.util.List;

/**
 * Data access object to retrieve xacml policies from an underlying persistent layer.
 *
 * @author Torsten Tetteroo
 */
public interface XacmlPolicyDao {

    /**
     * Loads Xacml Policies objects.
     *
     * @param userId The user ID to fetch policies for.
     * @param action The action to fetch policies for.
     * @return The Policies as Set of Xacml policy objects.
     */
    List loadUserPolicies(final String userId, final String action);

    /**
     * Loads Xacml Policies.<br> This method fetches all policies created to restrict privileges of roles related to the
     * provided action for the specified user.
     *
     * @param userId The user ID to fetch policies for.
     * @param action The action to fetch policies for.
     * @return The Policies as Set of Xacml policy objects.
     */
    List loadUserRestrictRolesPolicies(final String userId, final String action);

    /**
     * Loads Xacml Polices related to the specified role.
     *
     * @param roleId The id of the role to load policies for.
     * @return The role's polices Set of Xacml policy objects.
     */
    List loadRolePolices(final String roleId);

    /**
     * Loads an action from the database table {@code actions}.
     *
     * @param actionName The name of the action
     * @return The action object.
     */
    Action loadAction(final String actionName);

}
