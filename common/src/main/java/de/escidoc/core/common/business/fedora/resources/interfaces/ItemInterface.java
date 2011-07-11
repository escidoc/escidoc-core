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

import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

import java.util.Map;

/**
 * Interface for Fedora Item Object which consist of datastreams managed in Fedora Digital Repository System.
 *
 * @author Frank Schwichtenberg
 */
public interface ItemInterface extends VersionableResource {

    /**
     * Sets the RELS-EXT datastream and saves it in fedora. If the datastream is already set and unchanged, nothing will
     * be done.
     *
     * @param ds A Stream representing the Fedora RELS-EXT datastream.
     * @throws StreamNotFoundException    If there is no RELS-EXT datastream of a fedora object with {@code id}.
     *                                    This is probably an error cause a fedora object have to have this datastream.
     * @throws LockingException           Thrown if resource is locked.
     * @throws FedoraSystemException      If Fedora reports an error.
     * @throws TripleStoreSystemException If TripleStore reports an error.
     * @throws WebserverSystemException   Thrown in case of an internal error.
     */
    @Override
    void setRelsExt(Datastream ds) throws StreamNotFoundException, LockingException, FedoraSystemException,
        WebserverSystemException, TripleStoreSystemException;

    /**
     * Gets all metadata datastreams of the item. The keys are the names of the datastreams which are unique in item
     * context. Metadata datastreams {@code alternateId} is "metadata".
     *
     * @return A Map containing the metadata datastreams of this resource.
     * @throws FedoraSystemException    If Fedora reports an error.
     * @throws IntegritySystemException If data integrity of Fedora Repository is violated
     * @see Datastream
     */
    Map<String, Datastream> getMdRecords() throws FedoraSystemException, IntegritySystemException;

    /**
     * Sets all metadata datastreams of the item. For each datastream in the map
     *
     * @param mdRecords A Map containing the metadata datastreams of this resource.
     * @throws LockingException Thrown if resource is locked.
     * @throws SystemException  Thrown in case of an internal error.
     */
    void setMdRecords(final Map<String, Datastream> mdRecords) throws LockingException, SystemException;

    /**
     * Gets the metadata datastream specified by {@code name} of the item.
     *
     * @param name The name of a matadata datastream.
     * @return A metadata datastreams of this resource.
     * @throws StreamNotFoundException   If there is no metadata datastream with given name of a fedora object with
     *                                   {@code id}.
     * @throws FedoraSystemException     If Fedora reports an error.
     * @throws MdRecordNotFoundException If there exist no metadata record with the given name.
     */
    Datastream getMdRecord(String name) throws StreamNotFoundException, FedoraSystemException,
        MdRecordNotFoundException;

    /**
     * Sets the metadata datastream specified by {@code name} and saves it in fedora. If the datastream is already
     * set and unchanged, nothing will be done.
     *
     * @param name The name of a matadata datastream.
     * @param ds   A metadata datastreams of this resource.
     * @throws LockingException Thrown if resource is locked.
     * @throws SystemException  Thrown in case of an internal error.
     */
    void setMdRecord(final String name, final Datastream ds) throws LockingException, SystemException;
}
