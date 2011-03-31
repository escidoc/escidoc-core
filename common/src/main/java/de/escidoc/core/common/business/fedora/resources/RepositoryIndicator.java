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

package de.escidoc.core.common.business.fedora.resources;

import java.io.Serializable;

/**
 * Indicates the relation to the repository.
 *
 * @author Steffen Wagner
 */
public class RepositoryIndicator implements Serializable {

    private static final long serialVersionUID = -3585710715439866879L;

    /**
     * Indicate that the resource has changed in comparing to the repository.
     */
    private boolean repositorySynchron = true;

    private boolean resourceIsNew;

    private boolean resourceToDeleted;

    private boolean statusDeleted;

    /**
     * Indicate if resource has changed in comparing to repository.
     *
     * @param resourceChanged set true if resource has changed, false if not.
     */
    public void setResourceChanged(final boolean resourceChanged) {
        this.repositorySynchron = !resourceChanged;
    }

    /**
     * Indicate if resource has changed in comparing to repository.
     *
     * @return true if resource has changed, false if not.
     */
    public boolean isResourceChanged() {
        return !this.repositorySynchron;
    }

    /**
     * Indicate that the resource is marked as deleted and has to remove from the repository.
     *
     * @param resourceToDelete Set true if the resource is to delete from the repository
     */
    public void setResourceToDelete(final boolean resourceToDelete) {
        this.resourceToDeleted = resourceToDelete;
    }

    /**
     * Indicates that the resource is to delete from the repository.
     *
     * @return true if the resource is to delete from the repository
     */
    public boolean isResourceToDelete() {
        return this.resourceToDeleted;
    }

    /**
     * Indicates that the resource is new and not stored in repository.
     *
     * @param resourceIsNew Set true if the resource is new and not synchron with the repository
     */
    public void setResourceIsNew(final boolean resourceIsNew) {
        this.resourceIsNew = resourceIsNew;
    }

    /**
     * Indicates that the resource in new and not stored in the repository.
     *
     * @return true if resource is new and not stored in the repository, false otherwise
     */
    public boolean isResourceIsNew() {
        return this.resourceIsNew;
    }

    /**
     * Indicates that the status of resource is marked as deleted.
     *
     * @param statusDeleted set true if resource is marked as deleted.
     */
    public void setStatusDeleted(final boolean statusDeleted) {
        this.statusDeleted = statusDeleted;
    }

    /**
     * Indicates that the status of the resource is marked as deleted within repository.
     *
     * @return true if resource is marked as deleted in repository, false otherwise.
     */
    public boolean isStatusDeleted() {
        return this.statusDeleted;
    }

}
