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

package de.escidoc.core.common.business.fedora.resources.create;

import de.escidoc.core.common.business.fedora.Constants;

/**
 * Enumeration to describe all types of storage.
 *
 * @author Steffen Wagner
 */
public enum StorageType {

    INTERNAL_MANAGED("M"), EXTERNAL_URL("R"), EXTERNAL_MANAGED("E");

    // TODO internal XML Metadata is missing

    private String storageType = "M";

    /**
     * Storage Type (equals Fedora Control Group).
     *
     * @param storageType Storage Type
     */
    StorageType(final String storageType) {

        this.storageType = storageType;
    }

    /**
     * Get the abbreviation of storage type (equals to Fedora control group).
     *
     * @return Fedora Control Group type
     */
    public String getAbbreviation() {

        return this.storageType;
    }

    public String getESciDocName() {
        if ("E".equals(this.storageType)) {
            return Constants.STORAGE_EXTERNAL_MANAGED;
        }
        else if ("R".equals(this.storageType)) {
            return Constants.STORAGE_EXTERNAL_URL;
        }
        // else if (this.storageType.equals(StorageType.INTERNAL_MANAGED)) {
        return Constants.STORAGE_INTERNAL_MANAGED;
        // }
    }
}
