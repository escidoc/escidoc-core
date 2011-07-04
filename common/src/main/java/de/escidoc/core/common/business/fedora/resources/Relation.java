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
package de.escidoc.core.common.business.fedora.resources;

import de.escidoc.core.common.business.fedora.resources.interfaces.VersionableResource;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Implementation of a Fedora Item Object which consist of datastreams managed in Fedora Digital Repository System.
 *
 * @author Frank Schwichtenberg
 */
@Configurable
public class Relation extends GenericVersionableResource implements VersionableResource {

    /**
     * Constructs the relation with the specified id. The datastreams are instantiated and retrieved if the related
     * getter is called.
     *
     * @param id The id of an item managed in Fedora.
     * @throws TripleStoreSystemException Thrown if TripleStore access failed.
     * @throws IntegritySystemException   Thrown if there is an integrity error with the addressed object.
     * @throws WebserverSystemException   Thrown in case of an internal error.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException
     */
    public Relation(final String id) throws TripleStoreSystemException, WebserverSystemException,
        IntegritySystemException, ResourceNotFoundException {
        super(id);
        this.getUtility().checkIsRelation(id);
    }

    @Override
    public String getHref() {
        throw new UnsupportedOperationException("Relation.getHref");
    }

}
