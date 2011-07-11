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

import org.joda.time.DateTime;

import de.escidoc.core.common.business.fedora.resources.StatusType;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.service.UserContext;

/**
 * Version properties of versionable resource (Item/Container)(can represent current and latest version).
 *
 * @author Steffen Wagner
 */
public class VersionProperties {

    private String versionNumber = "1";

    private String createdById;

    private String createdByName;

    private String modifiedById;

    private String modifiedByName;

    private StatusType status = StatusType.PENDING;

    private String comment = "Object created.";

    private String pid;

    private DateTime date;

    /**
     * Version properties of Item.
     *
     * @throws WebserverSystemException Thrown if obtaining user context failed.
     */
    public VersionProperties() throws WebserverSystemException {

        // setting up some default values
        setCreatedById(UserContext.getId());
        setCreatedByName(UserContext.getRealName());
        setModifiedById(UserContext.getId());
        setModifiedByName(UserContext.getRealName());

    }

    /**
     * @param versionNo the versionNumber to set
     */
    public void setNumber(final String versionNo) {
        this.versionNumber = versionNo;
    }

    /**
     * @return the versionNumber
     */
    public String getNumber() {
        return this.versionNumber;
    }

    /**
     * Set Id of Creator.
     *
     * @param createdById the creator id
     */
    public final void setCreatedById(final String createdById) {
        this.createdById = createdById;
    }

    /**
     * Get Id of creator.
     *
     * @return the creator id
     */
    public String getCreatedById() {
        return this.createdById;
    }

    /**
     * Set id of modifier of this version.
     *
     * @param modifiedById the modifiedById to set
     */
    public final void setModifiedById(final String modifiedById) {
        this.modifiedById = modifiedById;
    }

    /**
     * Get id of modifier of this version.
     *
     * @return the modifiedById
     */
    public String getModifiedById() {
        return this.modifiedById;
    }

    /**
     * Get status of version.
     *
     * @param status the status to set
     */
    public void setStatus(final StatusType status) {
        this.status = status;
    }

    /**
     * @return the status
     */
    public StatusType getStatus() {
        return this.status;
    }

    /**
     * @param comment the statusComment to set
     */
    public void setComment(final String comment) {
        this.comment = comment;
    }

    /**
     * @return the statusComment
     */
    public String getComment() {
        return this.comment;
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
     * @param modifiedByName the modifiedByName to set
     */
    public final void setModifiedByName(final String modifiedByName) {
        this.modifiedByName = modifiedByName;
    }

    /**
     * @return the modifiedByName
     */
    public String getModifiedByName() {
        return this.modifiedByName;
    }

    /**
     * @param pid the pid to set
     */
    public void setPid(final String pid) {
        this.pid = pid;
    }

    /**
     * @return the pid
     */
    public String getPid() {
        return this.pid;
    }

    /**
     * @param date the date to set
     */
    public void setDate(final DateTime date) {
        this.date = date;
    }

    /**
     * @return the date
     */
    public DateTime getDate() {
        return this.date;
    }

}
