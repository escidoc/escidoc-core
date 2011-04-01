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

/**
 *
 */
package de.escidoc.core.common.business.fedora.resources.interfaces;

import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;

import java.util.Map;

/**
 * @author Frank Schwichtenberg
 */
public interface ComponentInterface extends FedoraResource {

    /**
     * @return A Map containing the metadata datastreams of this resource.
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     */
    Map<String, Datastream> getMdRecords() throws FedoraSystemException, IntegritySystemException;

    /**
     * @param ds A Map containing the metadata datastreams of this resource.
     * @throws LockingException If item is locked.
     * @throws SystemException  Thrown in case of an internal error.
     */
    void setMdRecords(Map<String, Datastream> ds) throws LockingException, SystemException;

    /**
     * @param name The name of a matadata datastream.
     * @return A metadata datastreams of this resource.
     * @throws de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     */
    Datastream getMdRecord(String name) throws StreamNotFoundException, FedoraSystemException;

    /**
     * @param name The name of a matadata datastream.
     * @param ds   A metadata datastreams of this resource.
     * @throws LockingException If item is locked.
     * @throws SystemException  Thrown in case of an internal error.
     */
    void setMdRecord(String name, Datastream ds) throws LockingException, SystemException;

}
