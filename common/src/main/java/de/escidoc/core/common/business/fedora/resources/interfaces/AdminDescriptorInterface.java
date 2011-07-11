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
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;

import java.util.Map;

public interface AdminDescriptorInterface extends FedoraResource {

    /**
     * Gets the RELS-EXT datastream of the fedora object.
     *
     * @return The Fedora RELS-EXT datastream.
     * @throws StreamNotFoundException If there is no RELS-EXT datastream of a fedora object with {@code id}.
     */
    @Override
    Datastream getRelsExt() throws StreamNotFoundException;

    /**
     * Sets the RELS-EXT datastream and saves it in fedora. If the datastream is already set and unchanged, nothing will
     * be done.
     *
     * @param ds A Stream representing the Fedora RELS-EXT datastream.
     * @throws StreamNotFoundException If there is no RELS-EXT datastream of a fedora object with {@code id}. This
     *                                 is probably an error cause a fedora object have to have this datastream.
     */
    @Override
    void setRelsExt(Datastream ds) throws StreamNotFoundException, LockingException;

    /**
     * @return A Map containing the metadata datastreams of this resource.
     */
    Map getMdRecords();

    /**
     * @param ds A Map containing the metadata datastreams of this resource.
     * @throws LockingException If the container is locked.
     */
    void setMdRecords(Map ds) throws LockingException;

    /**
     * @param name The name of a matadata datastream.
     * @return A metadata datastreams of this resource.
     * @throws de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException
     */
    Datastream getMdRecord(String name) throws StreamNotFoundException;

    /**
     * @param name The name of a matadata datastream.
     * @param ds   A metadata datastreams of this resource.
     * @throws LockingException If the container is locked.
     */
    void setMdRecord(String name, Datastream ds) throws LockingException;

}
