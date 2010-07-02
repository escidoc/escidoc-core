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
/**
 * 
 */
package de.escidoc.core.common.business.fedora.resources;

import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.fedora.resources.interfaces.VersionableResource;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;

/**
 * Implementation of a Fedora Item Object which consist of datastreams managed
 * in Fedora Digital Repository System.
 * 
 * @author FRS
 * 
 */
public class Relation extends GenericVersionableResource
    implements VersionableResource {

    private static AppLogger log = new AppLogger(Relation.class.getName());

    /**
     * Constructs the relation with the specified id. The datastreams are
     * instantiated and retrieved if the related getter is called.
     * 
     * @param id
     *            The id of an item managed in Fedora.
     * @throws TripleStoreSystemException
     *             Thrown if TripleStore access failed.
     * @throws IntegritySystemException
     *             Thrown if there is an integrity error with the addressed
     *             object.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @throws ResourceNotFoundException
     */
    public Relation(final String id) throws TripleStoreSystemException,
        WebserverSystemException, IntegritySystemException,
        ResourceNotFoundException {

        super(id);
        Utility.getInstance().checkIsRelation(id);
    }

    @Override
    public String getHref() {
        throw new UnsupportedOperationException("Relation.getHref");
    }

    // /*
    // * (non-Javadoc)
    // *
    // * @see
    // de.escidoc.core.om.business.fedora.resources.interfaces.FedoraResource#setGenericProperties(de.escidoc.core.common.business.fedora.datastream.Datastream)
    // */
    // public void setWov(Datastream ds) throws StreamNotFoundException,
    // LockingException, SystemException {
    // // TODO wov is set after locking
    // // if (this.isLocked) {
    // // throw new LockingException("Item " + this.id + " is locked.");
    // // }
    //
    // try {
    // Datastream curDs = getWov();
    // if (!ds.equals(curDs)) {
    // this.wov = ds;
    // ds.merge();
    // }
    // }
    // catch (StreamNotFoundException e) {
    // // there must be a versions stream
    // throw new StreamNotFoundException("No version-history in item "
    // + this.id + ".", e);
    // }
    //
    // }

}
