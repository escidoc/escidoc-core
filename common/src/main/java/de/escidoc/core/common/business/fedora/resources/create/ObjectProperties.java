/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.business.fedora.resources.create;

import de.escidoc.core.common.business.fedora.resources.StatusType;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * Properties of the object.
 * 
 * @author SWA
 * 
 */
public class ObjectProperties {

    private StatusType status = StatusType.PENDING;

    private String statusComment = "Object created.";

    private String pid = null;

    private String contextId = null;

    private String contentModelId = null;

    private String origin = null;

    private String originObjectId = null;

    private String originVersionId = null;

    private String contextTitle = null;

    private String contentModelTitle = null;

    private String title = null;

    private String description = null;

    /**
     * Version properties of Item.
     */
    public ObjectProperties() {
    }

    /**
     * Set status of object.
     * 
     * @param status
     *            the status to set
     */
    public final void setStatus(final StatusType status) {
        this.status = status;
    }

    /**
     * Get Status of object.
     * 
     * @return the status
     */
    public final StatusType getStatus() {
        return status;
    }

    /**
     * @param statusComment
     *            the statusComment to set
     */
    public void setStatusComment(final String statusComment) {
        this.statusComment = statusComment;
    }

    /**
     * @return the statusComment
     */
    public final String getStatusComment() {
        return statusComment;
    }

    /**
     * @param pid
     *            the pid to set
     */
    public final void setPid(final String pid) {
        this.pid = pid;
    }

    /**
     * @return the pid
     */
    public final String getPid() {
        return pid;
    }

    /**
     * @param contextId
     *            the contextId to set
     */
    public final void setContextId(final String contextId) {
        this.contextId = contextId;
    }

    /**
     * @return the contextId
     */
    public final String getContextId() {
        return contextId;
    }

    /**
     * @param contentModelId
     *            the contentModelId to set
     */
    public final void setContentModelId(final String contentModelId) {
        this.contentModelId = contentModelId;
    }

    /**
     * @param origin
     *            the objid of origin
     */
    public final void setOrigin(final String origin) {
        this.origin = origin;
        this.originObjectId = XmlUtility.getObjidWithoutVersion(this.origin);
        this.originVersionId =
            XmlUtility.getVersionNumberFromObjid(this.origin);
    }

    /**
     * @return the contentModelId
     */
    public final String getContentModelId() {
        return contentModelId;
    }

    /**
     * @return the contentModelId
     */
    public final String getOrigin() {
        return origin;
    }

    /**
     * @return the contentModelId
     */
    public final String getOriginObjectId() {
        return originObjectId;
    }

    /**
     * @return the contentModelId
     */
    public final String getOriginVersionId() {
        return originVersionId;
    }

    /**
     * @param title
     *            the title to set
     */
    public final void setTitle(final String title) {
        this.title = title;
    }

    /**
     * @return the title
     */
    public final String getTitle() {
        return title;
    }

    /**
     * @param description
     *            the description to set
     */
    public final void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the description
     */
    public final String getDescription() {
        return this.description;
    }

    /**
     * @param conextTitle
     *            the conextTitle to set
     */
    public final void setContextTitle(final String conextTitle) {
        this.contextTitle = conextTitle;
    }

    /**
     * @return the conextTitle
     */
    public final String getContextTitle() {
        return contextTitle;
    }

    /**
     * @param contentModelTitle
     *            the contentModelTitle to set
     */
    public final void setContentModelTitle(final String contentModelTitle) {
        this.contentModelTitle = contentModelTitle;
    }

    /**
     * @return the contentModelTitle
     */
    public final String getContentModelTitle() {
        return contentModelTitle;
    }

}
