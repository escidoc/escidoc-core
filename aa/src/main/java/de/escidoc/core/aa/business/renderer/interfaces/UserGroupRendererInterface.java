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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.business.renderer.interfaces;

import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.aa.business.persistence.UserGroup;
import de.escidoc.core.common.business.filter.RecordPacking;
import de.escidoc.core.common.exceptions.system.SystemException;

import java.util.List;

/**
 * Interface of an user group renderer.
 *
 * @author André Schenk
 */
public interface UserGroupRendererInterface {

    /**
     * Gets the representation of a user group.
     *
     * @param userGroup The user group to render.
     * @return Returns the XML representation of the user group.
     * @throws SystemException Thrown in case of an internal error.
     */
    String render(final UserGroup userGroup) throws SystemException;

    /**
     * Gets the representation of the virtual sub resource "currentGrants" of a user group.
     *
     * @param userGroup     The user group to render.
     * @param currentGrants The list of currently valid grants of the user group that shall be rendered.
     * @return Returns the XML representation of the virtual sub resource "currentGrants" of a user group.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderCurrentGrants(final UserGroup userGroup, final List<RoleGrant> currentGrants) throws SystemException;

    /**
     * Gets the representation of the provided {@code RoleGrant} object.
     *
     * @param grant The {@link RoleGrant} to render.
     * @return Returns the XML representation of the provided {@code RoleGrant} object.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderGrant(final RoleGrant grant) throws SystemException;

    /**
     * Gets the representation of the "resources" sub resource of a user group.
     *
     * @param userGroup The user group to render.
     * @return Returns the XML representation of the "resources" sub resource of the user group.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderResources(final UserGroup userGroup) throws SystemException;

    /**
     * Gets the representation of a list of the provided user groups.
     *
     * @param userGroups    The {@code List} of {@link UserGroup} objects to render.
     * @param recordPacking A string to determine how the record should be escaped in the response. Defined values are
     *                      'string' and 'xml'. The default is 'xml'.
     * @return Returns the XML representation of the list of user groups.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderUserGroups(final List<UserGroup> userGroups, final RecordPacking recordPacking) throws SystemException;
}
