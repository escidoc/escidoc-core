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
import de.escidoc.core.common.business.filter.RecordPacking;
import de.escidoc.core.common.exceptions.system.SystemException;

import java.util.List;
import java.util.Set;

/**
 * Interface of an user account renderer.
 *
 * @author Torsten Tetteroo
 */
public interface UserAccountRendererInterface {

    /**
     * Gets the representation of an user account.
     *
     * @param userAccount The user account to render.
     * @return Returns the XML representation of the user account.
     * @throws SystemException Thrown in case of an internal error.
     */
    String render(final UserAccount userAccount) throws SystemException;

    /**
     * Gets a list of grants (from filterfunction retrieveGrants.
     *
     * @param grants        A list of grants.
     * @param numberOfHits
     * @param offset
     * @param limit
     * @param recordPacking A string to determine how the record should be escaped in the response. Defined values are
     *                      'string' and 'xml'. The default is 'xml'.
     * @return Returns the XML representation of the grants.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderGrants(
        final List<RoleGrant> grants, final String numberOfHits, final String offset, final String limit,
        final RecordPacking recordPacking) throws SystemException;

    /**
     * Gets the representation of the virtual sub resource "currentGrants" of an user account.
     *
     * @param userAccount   The user account to render.
     * @param currentGrants The list of currently valid grants of the user account that shall be rendered.
     * @return Returns the XML representation of the virtual sub resource "currentGrants" of an user account.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderCurrentGrants(final UserAccount userAccount, final List<RoleGrant> currentGrants)
        throws SystemException;

    /**
     * Gets the representation of the provided {@code RoleGrant} object.
     *
     * @param grant The {@link RoleGrant} to render.
     * @return Returns the XML representation of the provided {@code RoleGrant} object.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderGrant(final RoleGrant grant) throws SystemException;

    /**
     * Gets the representation of the provided list of {@code UserPreference} objects.
     *
     * @param userAccount The user account to render preferences for.
     * @param preferences The {@code UserPreference} objects to render.
     * @return Returns the XML representation of the preferences.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderPreferences(final UserAccount userAccount, final Set<UserPreference> preferences)
        throws SystemException;

    /**
     * Gets the representation of the provided {@code UserPreference} object.
     *
     * @param userAccount
     * @param preference The {@link UserPreference} to render.
     * @return Returns the XML representation of the provided {@code RoleGrant} object.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderPreference(final UserAccount userAccount, final UserPreference preference) throws SystemException;

    /**
     * Gets the representation of the provided list of {@code UserAttribute} objects.
     *
     * @param userAccount The user account to render preferences for.
     * @param attributes  The {@code UserAttribute} objects to render.
     * @return Returns the XML representation of the attributes.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderAttributes(final UserAccount userAccount, final Set<UserAttribute> attributes) throws SystemException;

    /**
     * Gets the representation of the provided {@code UserAttribute} object.
     *
     * @param attribute The {@link UserAttribute} to render.
     * @return Returns the XML representation of the provided {@code UserAttribute} object.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderAttribute(final UserAttribute attribute) throws SystemException;

    /**
     * Gets the representation of the "resources" sub resource of an user account.
     *
     * @param userAccount The user account to render.
     * @return Returns the XML representation of the "resources" sub resource of the user account.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderResources(final UserAccount userAccount) throws SystemException;

    /**
     * Gets the representation of a list of the provided user accounts.
     *
     * @param userAccounts  The {@code List} of {@link UserAccount} objects to render.
     * @param recordPacking A string to determine how the record should be escaped in the response. Defined values are
     *                      'string' and 'xml'. The default is 'xml'.
     * @return Returns the XML representation of the list of user accounts.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderUserAccounts(final List<UserAccount> userAccounts, final RecordPacking recordPacking)
        throws SystemException;

}
