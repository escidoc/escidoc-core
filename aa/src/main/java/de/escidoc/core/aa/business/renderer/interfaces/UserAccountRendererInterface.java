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
package de.escidoc.core.aa.business.renderer.interfaces;

import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.aa.business.persistence.UserAccount;
import de.escidoc.core.aa.business.persistence.UserAttribute;
import de.escidoc.core.aa.business.persistence.UserPreference;
import de.escidoc.core.common.exceptions.system.SystemException;

import java.util.List;
import java.util.Set;

/**
 * Interface of an user account renderer.
 * 
 * @author TTE
 * @aa
 */
public interface UserAccountRendererInterface {

    /**
     * Gets the representation of an user account.
     * 
     * @param userAccount
     *            The user account to render.
     * @return Returns the XML representation of the user account.
     * @throws SystemException
     *             Thrown in case of an internal error.
     * @aa
     */
    String render(final UserAccount userAccount) throws SystemException;

    /**
     * Gets a list of grants (from filterfunction retrieveGrants.
     * 
     * @param grants
     *            A list of grants.
     * @param asSrw Render the returned list of user accounts as SRW response.
     *
     * @return Returns the XML representation of the grants.
     * @throws SystemException
     *             Thrown in case of an internal error.
     */
    String renderGrants(
        final List<RoleGrant> grants, final String numberOfHits, 
        final String offset, final String limit)
        throws SystemException;

    /**
     * Gets the representation of the virtual sub resource "currentGrants" of an
     * user account.
     * 
     * @param userAccount
     *            The user account to render.
     * @param currentGrants
     *            The list of currently valid grants of the user account that
     *            shall be rendered.
     * @return Returns the XML representation of the virtual sub resource
     *         "currentGrants" of an user account.
     * @throws SystemException
     *             Thrown in case of an internal error.
     * @aa
     */
    String renderCurrentGrants(
        final UserAccount userAccount, final List<RoleGrant> currentGrants)
        throws SystemException;

    /**
     * Gets the representation of the provided <code>RoleGrant</code> object.
     * 
     * @param grant
     *            The {@link RoleGrant} to render.
     * @return Returns the XML representation of the provided
     *         <code>RoleGrant</code> object.
     * @throws SystemException
     *             Thrown in case of an internal error.
     * @aa
     */
    String renderGrant(final RoleGrant grant) throws SystemException;

    /**
     * Gets the representation of the provided list of
     * <code>UserPreference</code> objects.
     * 
     * @param userAccount
     *            The user account to render preferences for.
     * @param preferences
     *            The <code>UserPreference</code> objects to render.
     * @return Returns the XML representation of the preferences.
     * @throws SystemException
     *             Thrown in case of an internal error.
     * @aa
     */
    String renderPreferences(
        final UserAccount userAccount, final Set<UserPreference> preferences)
        throws SystemException;

    /**
     * Gets the representation of the provided <code>UserPreference</code>
     * object.
     * 
     * @param preference
     *            The {@link UserPreference} to render.
     * @return Returns the XML representation of the provided
     *         <code>RoleGrant</code> object.
     * @throws SystemException
     *             Thrown in case of an internal error.
     * @aa
     */
    String renderPreference(
        final UserAccount userAccount, final UserPreference preference)
        throws SystemException;

    /**
     * Gets the representation of the provided list of
     * <code>UserAttribute</code> objects.
     * 
     * @param userAccount
     *            The user account to render preferences for.
     * @param attributes
     *            The <code>UserAttribute</code> objects to render.
     * @return Returns the XML representation of the attributes.
     * @throws SystemException
     *             Thrown in case of an internal error.
     * @aa
     */
    String renderAttributes(
        final UserAccount userAccount, final Set<UserAttribute> attributes)
        throws SystemException;

    /**
     * Gets the representation of the provided <code>UserAttribute</code>
     * object.
     * 
     * @param attribute
     *            The {@link UserAttribute} to render.
     * @return Returns the XML representation of the provided
     *         <code>UserAttribute</code> object.
     * @throws SystemException
     *             Thrown in case of an internal error.
     * @aa
     */
    String renderAttribute(final UserAttribute attribute)
        throws SystemException;

    /**
     * Gets the representation of the "resources" sub resource of an user
     * account.
     * 
     * @param userAccount
     *            The user account to render.
     * @return Returns the XML representation of the "resources" sub resource of
     *         the user account.
     * @throws SystemException
     *             Thrown in case of an internal error.
     * @aa
     */
    String renderResources(final UserAccount userAccount)
        throws SystemException;

    /**
     * Gets the representation of a list of the provided user accounts.
     * 
     * @param userAccounts
     *            The <code>List</code> of
     *            {@link de.escidoc.core.aa.business.persistence.UserAccount}
     *            objects to render.
     * @param asSrw Render the returned list of user accounts as SRW response.
     *
     * @return Returns the XML representation of the list of user accounts.
     * @throws SystemException
     *             Thrown in case of an internal error.
     * @aa
     */
    String renderUserAccounts(
        final List<UserAccount> userAccounts)
        throws SystemException;

}
