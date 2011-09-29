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
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.business.fedora.resources;

/**
 * Data object for the content of one version entry of the version history.
 *
 * @author André Schenk
 */
public class Version {

    private String versionNumber;

    // attribute timestamp
    private String timestamp;

    // element timestamp
    private String versionDate;

    private String versionPid;

    private String versionStatus;

    private String validStatus;

    private String comment;

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(final String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final String timestamp) {
        this.timestamp = timestamp;
    }

    public String getVersionDate() {
        return versionDate;
    }

    public void setVersionDate(final String versionDate) {
        this.versionDate = versionDate;
    }

    public String getVersionPid() {
        return versionPid;
    }

    public void setVersionPid(final String versionPid) {
        this.versionPid = versionPid;
    }

    public String getVersionStatus() {
        return versionStatus;
    }

    public void setVersionStatus(final String versionStatus) {
        this.versionStatus = versionStatus;
    }

    public String getValidStatus() {
        return validStatus;
    }

    public void setValidStatus(final String validStatus) {
        this.validStatus = validStatus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }
}
