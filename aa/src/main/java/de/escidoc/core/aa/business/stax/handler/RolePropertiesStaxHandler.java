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
package de.escidoc.core.aa.business.stax.handler;

import de.escidoc.core.aa.business.RoleHandler;
import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.aa.business.persistence.EscidocRoleDaoInterface;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

/**
 * Stax handler that manages the properties section of a role.
 *
 * @author Torsten Tetteroo
 */
public class RolePropertiesStaxHandler extends DefaultHandler {

    private static final String BASE_PATH = '/' + XmlUtility.NAME_ROLE + '/' + XmlUtility.NAME_PROPERTIES;

    private static final String DESCRIPTION_PATH = BASE_PATH + '/' + XmlUtility.NAME_DESCRIPTION;

    private static final String NAME_PATH = BASE_PATH + '/' + XmlUtility.NAME_NAME;

    private final EscidocRole role;

    private final EscidocRoleDaoInterface roleDao;

    /**
     * The constructor.
     *
     * @param role    The role to handle.
     * @param roleDao The data access object to access roles.
     */
    public RolePropertiesStaxHandler(final EscidocRole role, final EscidocRoleDaoInterface roleDao) {

        this.role = role;
        this.roleDao = roleDao;
    }

    /**
     * See Interface for functional description.
     *
     * @throws XmlCorruptedException Thrown if an invalid role name is used.
     * @see DefaultHandler #characters(java.lang.String, de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public String characters(final String s, final StartElement element) throws UniqueConstraintViolationException,
        XmlCorruptedException, SqlDatabaseSystemException {

        if (isNotReady()) {

            final String currentPath = element.getPath();
            if (NAME_PATH.equals(currentPath)) {
                assertValidAndUniqueLoginName(s, role.getId());
                role.setRoleName(s);
            }
            if (DESCRIPTION_PATH.equals(currentPath)) {
                role.setDescription(s);
                // description is the last element that has to be parsed
                setReady();
            }
        }

        return s;
    }

    /**
     * Asserts that the role name is valid and unique.
     *
     * @param name The role name that shall be asserted.
     * @param id   The id of the role that shall be created or updated and for that the unique constraint has to be
     *             checked.
     * @throws SqlDatabaseSystemException Thrown in case of an database error.
     * @throws UniqueConstraintViolationException
     *                                    Thrown if the role name is not unique, i.e. it exists another role with the
     *                                    same name but a different id.
     * @throws XmlCorruptedException      Thrown if an invalid role name is used.
     */
    private void assertValidAndUniqueLoginName(final String name, final String id) throws SqlDatabaseSystemException,
        XmlCorruptedException, UniqueConstraintViolationException {

        // first, assert valid name.
        if (RoleHandler.FORBIDDEN_ROLE_NAME.equals(name)) {
            throw new XmlCorruptedException(StringUtility.format("Role name not allowed", name));
        }

        final EscidocRole existingRoleWithSameName = roleDao.retrieveRole(name);
        if (existingRoleWithSameName != null) {
            if (existingRoleWithSameName.getId().equals(id)) {
                return;
            }
            throw new UniqueConstraintViolationException(StringUtility.format(
                "Role name must be unique within eSciDoc", name));
        }
    }

}
