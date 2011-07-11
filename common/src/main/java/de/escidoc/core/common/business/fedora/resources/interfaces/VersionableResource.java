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

package de.escidoc.core.common.business.fedora.resources.interfaces;

import org.joda.time.DateTime;

import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

/**
 * Versionable Resource Interface.
 *
 * @author Steffen Wagner
 */
public interface VersionableResource extends Resource {

    /**
     * @return true is version is latest/newest version, false otherwise.
     */
    @Deprecated
    boolean isNewVersion();

    /**
     * Get Whole Object Version (WOV) Stream.
     *
     * @return WOV datastream
     * @throws StreamNotFoundException Thrown if datastream was not found.
     * @throws FedoraSystemException   Thrown in case of Fedora failure.
     */
    Datastream getWov() throws StreamNotFoundException, FedoraSystemException;

    /**
     * Indicate that the version-status (of the latest version) has changed. E.g from pending to submitted or to
     * released.
     *
     * @return True if version-status has changed during the current update. False if version-status is same as before
     *         update.
     */
    boolean hasVersionStatusChanged();

    /**
     * Indicate that the status of the latest version (version-status) has changed.
     */
    void setVersionStatusChange();

    /**
     * Get status of latest version.
     *
     * @return version-status
     * @throws IntegritySystemException If data integrity of Fedora Repository is violated
     */
    String getVersionStatus() throws IntegritySystemException;

    /**
     * Set status of latest version. (Status change is done outside of Resource in Utility.class but currently not
     * written back to resource class. The status change obtains the resource class if its created as new instance. This
     * has to change of course.)
     *
     * @param versionStatus Status of version
     * @throws IntegritySystemException If data integrity of Fedora Repository is violated
     */
    void setVersionStatus(final String versionStatus) throws IntegritySystemException;

    /**
     * Set the latest release version number.
     *
     * @param latestReleaseVersionNumber the latest release version number
     * @throws IntegritySystemException If data integrity of Fedora Repository is violated
     */
    void setLatestReleaseVersionNumber(final String latestReleaseVersionNumber) throws IntegritySystemException;

    /**
     * Set the Whole Object Version (WOV) Stream.
     *
     * @param ds WOV datastream.
     * @throws StreamNotFoundException Thrown if datastream was not found.
     * @throws LockingException        Thrown if object is locked.
     * @throws SystemException         Thrown in case of internal failure.
     */
    void setWov(Datastream ds) throws StreamNotFoundException, LockingException, SystemException;

    /**
     * Get the last-modification-date of the object.
     *
     * @return last-modification-date
     * @throws TripleStoreSystemException Thrown if TripleStore reports an error.
     * @throws FedoraSystemException      Thrown in case of Fedora failure.
     * @throws WebserverSystemException   Thrown in case of an internal error.
     */
    DateTime getLastModificationDate() throws TripleStoreSystemException, FedoraSystemException,
        WebserverSystemException;

    /**
     * Persist the resource.
     *
     * @return last-modification-date of the resource
     * @throws SystemException Thrown if persisting fails.
     */
    DateTime persist() throws SystemException;
}
