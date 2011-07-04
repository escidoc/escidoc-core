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

import java.util.Map;

import org.escidoc.core.services.fedora.management.DatastreamProfileTO;

import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.exceptions.application.notfound.AdminDescriptorNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

/**
 * Interface for Context.
 */
public interface ContextInterface extends FedoraResource {

    /**
     * Sets the RELS-EXT datastream and saves it in fedora. If the datastream is
     * already set and unchanged, nothing will be done.
     *
     * @param ds
     *            A Stream representing the Fedora RELS-EXT datastream.
     * @throws StreamNotFoundException
     *             If there is no RELS-EXT datastream of a fedora object with
     *             {@code id}. This is probably an error cause a fedora
     *             object have to have this datastream.
     * @throws LockingException
     *             Thrown if Context is locked.
     * @throws WebserverSystemException
     *             Thrown if an unexpected error occurs.
     * @throws TripleStoreSystemException
     *             Thrown if TripleStore reports an error.
     * @throws FedoraSystemException
     *             Thrown if Fedora reports an error.
     */
    @Override
    void setRelsExt(Datastream ds) throws StreamNotFoundException, LockingException, FedoraSystemException,
        WebserverSystemException, TripleStoreSystemException;

    /**
     * Gets the properties datastream of the fedora object.
     *
     * @return The Fedora properties datastream.
     * @throws StreamNotFoundException
     *             If there is no properties datastream of a fedora object with
     *             {@code id}.
     * @throws FedoraSystemException
     *             Thrown if Fedora reports an error.
     */
    Datastream getProperties() throws StreamNotFoundException, FedoraSystemException;

    /**
     * Sets the properties datastream and saves it in fedora. If the datastream
     * is already set and unchanged, nothing will be done.
     *
     * @param ds
     *            The Fedora properties datastream.
     * @throws StreamNotFoundException
     *             If there is no properties datastream of a fedora object with
     *             {@code id}. This is probably an error cause a fedora
     *             object have to have this datastream.
     * @throws LockingException
     *             Thrown if Context is locked
     * @throws SystemException
     *             Thrown in case of an internal error.
     */
    void setProperties(Datastream ds) throws StreamNotFoundException, LockingException, SystemException;

    /**
     * Gets all admin-descriptor datastreams of the Context. The keys are the
     * names of the datastreams which has to be unique in Context.
     * Admin-Descriptor datastreams {@code alternateId[0]} is
     * "admin-descriptor".
     *
     * @return A Map containing the admin-descriptor datastreams of this
     *         resource.
     * @throws FedoraSystemException
     *             Thrown if Fedora reports an error.
     * @throws IntegritySystemException
     *             Thrown if data integrity is violated
     * @see Datastream
     */
    Map<String, DatastreamProfileTO> getAdminDescriptors() throws FedoraSystemException, IntegritySystemException;

    /**
     * Get the Stream of an selected AdminDescriptor.
     * 
     * @param adminDescriptorName
     *            Name of the AdminDescriptor.
     * @return Datastream of AdminDescriptor.
     * @throws FedoraSystemException
     *             Thrown if AdminDescriptor not exist or could not retrieved
     *             from Fedora.
     * @throws de.escidoc.core.common.exceptions.application.notfound.AdminDescriptorNotFoundException
     */
    Datastream getAdminDescriptor(final String adminDescriptorName) throws FedoraSystemException,
        AdminDescriptorNotFoundException;

    /**
     * Add an AdminDescriptor to the Context. The datastream must be framed by a
     * XML root element and mark by a unique name.
     * 
     * @param ds
     *            New admin-descriptor datastream.
     * @throws FedoraSystemException
     *             Thrown if AdminDescriptor not exist or could not retrieved
     *             from Fedora.
     * @throws WebserverSystemException
     *             Thrown if write of new AdminDescriptor fails.
     */
    void setAdminDescriptor(final Datastream ds) throws FedoraSystemException, WebserverSystemException;
}
