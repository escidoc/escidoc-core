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
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.oum.business.fedora.resources.interfaces;

import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.interfaces.FedoraResource;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;

import java.util.Map;

/**
 * Interface of an organizational unit fedora resource.
 *
 * @author Michael Schneider
 */
public interface OrganizationalUnitInterface extends FedoraResource {

    /**
     * @return A Map containing the metadata datastreams of this resource.
     * @throws FedoraSystemException    Thrown if access to fedora fails.
     * @throws IntegritySystemException Thrown if an expected metadata datastream was not found.
     */
    Map<String, Datastream> getMdRecords() throws FedoraSystemException, IntegritySystemException;

    /**
     * @param mdRecords A Map containing the metadata datastreams of this resource.
     * @throws SystemException Thrown if access to fedora fails.
     */
    void setMdRecords(final Map<String, Datastream> mdRecords) throws SystemException;

    /**
     * Retrieve a specific metadata datastream.
     *
     * @param name The name of the exprected metadata datastream.
     * @return The metadata datastream.
     * @throws FedoraSystemException   Thrown if access to fedora fails.
     * @throws StreamNotFoundException Thrown if the metadata datastream was not found.
     */
    Datastream getMdRecord(String name) throws FedoraSystemException, StreamNotFoundException;

    /**
     * Add or update a metadata datastream.
     *
     * @param name The name of the metadata datastream.
     * @param ds   The new/updated metadata datastream.
     * @throws SystemException Thrown if access to fedora fails.
     */
    void setMdRecord(final String name, final Datastream ds) throws SystemException;
}
