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

import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;

/**
 * All status types of lock (resource lock).
 *
 * @author Steffen Wagner
 */
public enum LockStatus {

    LOCKED("locked"), UNLOCKED("unlocked");

    private String status = "unlocked";

    /**
     *
     */
    LockStatus() {
    }

    /**
     * @param status Type of lock status as String.
     */
    LockStatus(final String status) {

        this.status = status;
    }

    /**
     * Get name of status.
     *
     * @return status as String.
     */
    @Override
    public String toString() {

        return this.status;
    }

    /**
     * Convert status from String to Enum type.
     *
     * @param status lock status as String.
     * @return LockStatus
     * @throws InvalidStatusException Thrown if unknown or invalid status was set.
     */
    public static LockStatus getStatusType(final String status) throws InvalidStatusException {

        if (status != null) {
            if (status.equals(LockStatus.LOCKED.toString())) {
                return LOCKED;
            }
            else if (status.equals(LockStatus.UNLOCKED.toString())) {
                return UNLOCKED;
            }
        }
        throw new InvalidStatusException("Invalid lock status '" + status + '\'');
    }

}
