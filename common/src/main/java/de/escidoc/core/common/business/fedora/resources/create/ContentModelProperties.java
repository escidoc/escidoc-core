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

/**
 * Properties of an object (resource). The properties are subdivided into object, current version and latest version
 * properties. (Current version means the version which is currently retrieved and NOT the newest existing version of
 * the resource.)
 *
 * @author Frank Schwichtenberg
 */
public class ContentModelProperties {

    private ObjectProperties objectProperties;

    private VersionProperties currentVersion;

    private VersionProperties latestVersion;

    private VersionProperties latestReleasedVersion;

    /**
     * @throws WebserverSystemException Thrown by VersionProperties if obtaining user context failed.
     */
    public ContentModelProperties() throws WebserverSystemException {

        this.objectProperties = new ObjectProperties();
        this.currentVersion = new VersionProperties();
        this.latestVersion = new VersionProperties();
    }

    /**
     * @param currentVersion the currentVersion to set
     */
    public void setCurrentVersion(final VersionProperties currentVersion) {
        this.currentVersion = currentVersion;
    }

    /**
     * @return the currentVersion
     */
    public VersionProperties getCurrentVersion() {
        return this.currentVersion;
    }

    /**
     * @param latestVersion the latestVersion to set
     */
    public void setLatestVersion(final VersionProperties latestVersion) {
        this.latestVersion = latestVersion;
    }

    /**
     * @return the latestVersion
     */
    public VersionProperties getLatestVersion() {
        return this.latestVersion;
    }

    /**
     * @param latestReleasedVersion the latestReleasedVersion to set
     */
    public void setLatestReleasedVersion(final VersionProperties latestReleasedVersion) {
        this.latestReleasedVersion = latestReleasedVersion;
    }

    /**
     * @return the latestReleasedVersion
     */
    public VersionProperties getLatestReleasedVersion() {
        return this.latestReleasedVersion;
    }

    /**
     * @param objectProperties the objectProperties to set
     */
    public void setObjectProperties(final ObjectProperties objectProperties) {
        this.objectProperties = objectProperties;
    }

    /**
     * @return the objectProperties
     */
    public ObjectProperties getObjectProperties() {
        return this.objectProperties;
    }

}
