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

import de.escidoc.core.common.business.fedora.resources.LockStatus;
import de.escidoc.core.common.business.fedora.resources.StatusType;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.service.UserContext;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Properties for Content Relation.<br/>
 *
 * @author Steffen Wagner
 */
public class ContentRelationProperties implements Serializable {

    private static final long serialVersionUID = -5610276928827889108L;

    private StatusType status = StatusType.PENDING;

    private String statusComment = "Object created.";

    private String pid;

    private String title = "Content Relation";

    private String createdById;

    private String createdByName;

    private String modifiedById;

    private String modifiedByName;

    private DateTime lastModificationDate;

    private DateTime creationDate;

    private DateTime versionDate;

    private LockStatus lockStatus = LockStatus.UNLOCKED;

    private DateTime lockDate;

    private String lockOwnerId;

    private String lockOwnerName;

    private String description;

    /**
     * ContentRelationProperties.
     *
     * @throws WebserverSystemException Thrown if obtaining UserContext failed.
     */
    public ContentRelationProperties() throws WebserverSystemException {

        // setting up some default values
        setCreatedById(UserContext.getId());
        setCreatedByName(UserContext.getRealName());
        setModifiedById(UserContext.getId());
        setModifiedByName(UserContext.getRealName());
    }

    /**
     * Set status of object.
     *
     * @param status the status to set
     */
    public void setStatus(final StatusType status) {
        this.status = status;
    }

    /**
     * Get Status of object.
     *
     * @return the status
     */
    public StatusType getStatus() {
        return this.status;
    }

    /**
     * @param statusComment the statusComment to set
     */
    public void setStatusComment(final String statusComment) {
        this.statusComment = statusComment;
    }

    /**
     * @return the statusComment
     */
    public String getStatusComment() {
        return this.statusComment;
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
     * @param title the title to set
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return this.title;
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
     * @param lastModificationDate Last-modification-date of ContentRelation
     */
    public void setLastModificationDate(final DateTime lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    /**
     * @return Last-modification-date of ContentRelation
     */
    public DateTime getLastModificationDate() {
        return this.lastModificationDate;
    }

    /**
     * @param creationDate Creation date of ContentRelation
     */
    public void setCreationDate(final DateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Get creation date.
     *
     * @return timestamp of create
     */
    public DateTime getCreationDate() {
        return this.creationDate;
    }

    /**
     * Set status of lock.
     *
     * @param lockStatus [locked|unlocked]
     * @throws InvalidStatusException Thrown if status in invalid
     */
    public void setLockStatus(final LockStatus lockStatus) {

        this.lockStatus = lockStatus;
    }

    /**
     * Set status of lock.
     *
     * @param lockStatus [locked|unlocked]
     * @throws InvalidStatusException Thrown if status in invalid
     */
    public void setLockStatus(final String lockStatus) throws InvalidStatusException {
        this.lockStatus = LockStatus.getStatusType(lockStatus);
    }

    /**
     * Get lock status.
     *
     * @return status of lock ([locked|unlocked]
     */
    public LockStatus getLockStatus() {
        return this.lockStatus;
    }

    /**
     * Set timestamp of lock.
     *
     * @param lockDate timestamp of lock.
     */
    public void setLockDate(final DateTime lockDate) {
        this.lockDate = lockDate;
    }

    /**
     * Set timestamp of lock.
     *
     * @param lockDate timestamp of lock.
     */
    public void setLockDate(final String lockDate) {
        setLockDate(new DateTime(lockDate));
    }

    /**
     * Get timestamp of lock.
     *
     * @return timestamp of lock.
     */
    public DateTime getLockDate() {
        return this.lockDate;
    }

    /**
     * Set lock owner.
     *
     * @param lockOwnerId Lock owner
     */
    public void setLockOwnerId(final String lockOwnerId) {
        this.lockOwnerId = lockOwnerId;
    }

    /**
     * Get lock owner.
     *
     * @return Owner of lock
     */
    public String getLockOwnerId() {
        return this.lockOwnerId;
    }

    /**
     * Set Name (title) of lock owner.
     *
     * @param lockOwnerName Name (title) of lock owner
     */
    public void setLockOwnerName(final String lockOwnerName) {
        this.lockOwnerName = lockOwnerName;
    }

    /**
     * Get Name (title) of lock owner.
     *
     * @return Name (title) of lock owner
     */
    public String getLockOwnerName() {
        return this.lockOwnerName;
    }

    /**
     * Is Content Relation locked?
     *
     * @return true if locked, false otherwise.
     */
    public boolean isLocked() {

        return this.lockStatus == LockStatus.LOCKED;
    }

    /**
     * Lock the ContentRelation.
     *
     * @param ownerId objid of the lock owner
     */
    public void lock(final String ownerId) {

        this.lockStatus = LockStatus.LOCKED;
        this.lockOwnerId = ownerId;
        this.lockDate = this.lastModificationDate;
    }

    /**
     * Set timestamp of resource/version.
     *
     * @param versionDate timestamp of resource
     */
    public void setVersionDate(final DateTime versionDate) {
        this.versionDate = versionDate;
    }

    /**
     * Set timestamp of resource/version.
     *
     * @return timestamp of resource
     */
    public DateTime getVersionDate() {
        return this.versionDate;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Set description.
     *
     * @param description Description of ContentRelation
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Get description.
     *
     * @return Description of ContentRelation
     */
    public String getDescription() {
        return this.description;
    }

}
