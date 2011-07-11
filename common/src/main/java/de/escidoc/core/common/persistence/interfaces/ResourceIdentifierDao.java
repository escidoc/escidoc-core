/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.common.persistence.interfaces;

import de.escidoc.core.common.exceptions.system.SystemException;

import java.util.List;

/**
 * Data access object to fetch resource ids from a storage back end.
 *
 * @author Torsten Tetteroo
 */
public interface ResourceIdentifierDao {

    /**
     * Get next object identifiers.
     *
     * @param noOfPids The number of ids to retrieve. (non negative integer)
     * @return An array of the requested next available object id(s).
     * @throws SystemException Thrown in case of an internal system error.
     */
    List<String> getNextPids(final int noOfPids) throws SystemException;
}
