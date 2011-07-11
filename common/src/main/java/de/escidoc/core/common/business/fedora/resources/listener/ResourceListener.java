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

package de.escidoc.core.common.business.fedora.resources.listener;

import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * This interface allows you to get informed if a resource was created, modified or deleted.
 *
 * @author André Schenk
 */
public interface ResourceListener {

    /**
     * A resource was created.
     *
     * @param id      resource id
     * @param xml
     * @throws SystemException The listener object threw an exception.
     */
    void resourceCreated(String id, String xml) throws SystemException;

    /**
     * A resource was deleted.
     *
     * @param id resource id
     * @throws SystemException The listener object threw an exception.
     */
    void resourceDeleted(String id) throws SystemException;

    /**
     * A resource was modified.
     *
     * @param id      resource id
     * @param xml
     * @throws SystemException The listener object threw an exception.
     */
    void resourceModified(String id, String xml) throws SystemException;
}
