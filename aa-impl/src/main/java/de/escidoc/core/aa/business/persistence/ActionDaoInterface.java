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
package de.escidoc.core.aa.business.persistence;

import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;

/**
 * Interface of a data access object used to access {@link Action} objects.
 *
 * @author Torsten Tetteroo
 */
public interface ActionDaoInterface {

    /**
     * Retrieves the unsecured action list of a Context.
     *
     * @param contextId The id of the Context object for that the list of unsecured actions shall be retrieved.
     * @return The {@link UnsecuredActionList} object related to the context
     * @throws SqlDatabaseSystemException Thrown in case of an internal database error.
     */
    UnsecuredActionList retrieveUnsecuredActionList(final String contextId) throws SqlDatabaseSystemException;

    /**
     * Saves or updates the provided {@link UnsecuredActionList} object.
     *
     * @param unsecuredActionList The {@link UnsecuredActionList} object to save or update.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database error.
     */
    void saveOrUpdate(UnsecuredActionList unsecuredActionList) throws SqlDatabaseSystemException;

    /**
     * Deletes the provided list of unsecured actions.
     *
     * @param unsecuredActionList The {@link UnsecuredActionList} to delete.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database error.
     */
    void delete(UnsecuredActionList unsecuredActionList) throws SqlDatabaseSystemException;
}
