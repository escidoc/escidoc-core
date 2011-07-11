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

package de.escidoc.core.common.util.service;

import java.io.Serializable;

/**
 * Class encapsulating the details of an eSciDoc request.
 *
 * @author Torsten Tetteroo
 */
public class EscidocRequestDetail implements Serializable {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = -4779944843870131966L;

    /**
     * The restricted permission, if any.
     */
    private int restrictedPermissionCode = UserContext.UNRESTRICTED_PERMISSION;

    /**
     * Sets restricted permissions, e.g. retrieval restricted to releases.
     *
     * @param restrictedPermissions The code identifying the restricted permissions.
     */
    public void setRestrictedPermissions(final int restrictedPermissions) {
        this.restrictedPermissionCode = restrictedPermissions;
    }

    /**
     * Gets restricted permissions.
     *
     * @return Returns the code identifying the restricted permissions.
     */
    public int getRestrictedPermissions() {
        return this.restrictedPermissionCode;
    }
}
