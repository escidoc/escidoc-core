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

import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.service.UserContext;

/**
 * Properties for Component.
 *
 * @author Steffen Wagner
 */
public class ComponentProperties {

    private String contentCatagory;

    private String createdById;

    private String createdByName;

    private String visibility;

    private String validStatus;

    private String mimeType;

    /**
     * Component Properties POJO for create.
     *
     * @throws WebserverSystemException Thrown if obtaining user context failed.
     */
    public ComponentProperties() throws WebserverSystemException {

        // setting up some default values
        setCreatedById(UserContext.getId());
        setCreatedByName(UserContext.getRealName());
    }

    /**
     * @param contentCatagory the contentCatagory to set
     */
    public void setContentCatagory(final String contentCatagory) {
        this.contentCatagory = contentCatagory;
    }

    /**
     * @return the contentCatagory
     */
    public String getContentCatagory() {
        return this.contentCatagory;
    }

    /**
     * @param createdById the createdById to set
     */
    public final void setCreatedById(final String createdById) {
        this.createdById = createdById;
    }

    /**
     * @return the createdById
     */
    public String getCreatedById() {
        return this.createdById;
    }

    /**
     * @param createdByName the createdByName to set
     */
    public final void setCreatedByName(final String createdByName) {
        this.createdByName = createdByName;
    }

    /**
     * @return the createdByName
     */
    public String getCreatedByName() {
        return this.createdByName;
    }

    /**
     * @param visibility the visibility to set
     */
    public void setVisibility(final String visibility) {
        this.visibility = visibility;
    }

    /**
     * @return the visibility
     */
    public String getVisibility() {
        return this.visibility;
    }

    /**
     * @param validStatus the validStatus to set
     */
    public void setValidStatus(final String validStatus) {
        this.validStatus = validStatus;
    }

    /**
     * @return the validStatus
     */
    public String getValidStatus() {
        return this.validStatus;
    }

    /**
     * @param mimeType the mimeType to set
     */
    public void setMimeType(final String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * @return the mimeType
     */
    public String getMimeType() {
        return this.mimeType;
    }

}
