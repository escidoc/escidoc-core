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

import de.escidoc.core.common.business.fedora.resources.StatusType;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * Properties of the object.
 *
 * @author Steffen Wagner
 */
public class ObjectProperties {

    private StatusType status = StatusType.PENDING;

    private String statusComment = "Object created.";

    private String pid;

    private String contextId;

    private String contentModelId;

    private String origin;

    private String originObjectId;

    private String originVersionId;

    private String contextTitle;

    private String contentModelTitle;

    private String title;

    private String description;

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
     * @param contextId the contextId to set
     */
    public void setContextId(final String contextId) {
        this.contextId = contextId;
    }

    /**
     * @return the contextId
     */
    public String getContextId() {
        return this.contextId;
    }

    /**
     * @param contentModelId the contentModelId to set
     */
    public void setContentModelId(final String contentModelId) {
        this.contentModelId = contentModelId;
    }

    /**
     * @param origin the objid of origin
     */
    public void setOrigin(final String origin) {
        this.origin = origin;
        this.originObjectId = XmlUtility.getObjidWithoutVersion(this.origin);
        this.originVersionId = XmlUtility.getVersionNumberFromObjid(this.origin);
    }

    /**
     * @return the contentModelId
     */
    public String getContentModelId() {
        return this.contentModelId;
    }

    /**
     * @return the contentModelId
     */
    public String getOrigin() {
        return this.origin;
    }

    /**
     * @return the contentModelId
     */
    public String getOriginObjectId() {
        return this.originObjectId;
    }

    /**
     * @return the contentModelId
     */
    public String getOriginVersionId() {
        return this.originVersionId;
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
     * @param description the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @param conextTitle the conextTitle to set
     */
    public void setContextTitle(final String conextTitle) {
        this.contextTitle = conextTitle;
    }

    /**
     * @return the conextTitle
     */
    public String getContextTitle() {
        return this.contextTitle;
    }

    /**
     * @param contentModelTitle the contentModelTitle to set
     */
    public void setContentModelTitle(final String contentModelTitle) {
        this.contentModelTitle = contentModelTitle;
    }

    /**
     * @return the contentModelTitle
     */
    public String getContentModelTitle() {
        return this.contentModelTitle;
    }

}
