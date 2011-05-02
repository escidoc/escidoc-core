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

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;

/**
 * All types of public-status, version status.
 *
 * @author Steffen Wagner
 */
public enum StatusType {

    PENDING, SUBMITTED, RELEASED, WITHDRAWN, INREVISION;

    /**
     * Get name of status.
     *
     * @return StatusType as String.
     */
    @Override
    public String toString() {

        switch (this) {
            case PENDING:
                return "pending";
            case SUBMITTED:
                return "submitted";
            case RELEASED:
                return "released";
            case WITHDRAWN:
                return "withdrawn";
            case INREVISION:
                return "in-revision";
            default:
                return Constants.UNKNOWN;
        }

    }

    /**
     * Convert status from String to Enum type.
     *
     * @param type object/version status type as String.
     * @return StatusType
     * @throws InvalidStatusException Thrown if unknown or invalid status type was set.
     */
    public static StatusType getStatusType(final String type) throws InvalidStatusException {

        if (type != null) {
            if (type.equals(PENDING.toString())) {
                return PENDING;
            }
            else if (type.equals(RELEASED.toString())) {
                return RELEASED;
            }
            else if (type.equals(SUBMITTED.toString())) {
                return SUBMITTED;
            }
            else if (type.equals(WITHDRAWN.toString())) {
                return WITHDRAWN;
            }
            else if (type.equals(INREVISION.toString())) {
                return INREVISION;
            }
        }
        throw new InvalidStatusException("Invalid status '" + type + '\'');
    }

}
