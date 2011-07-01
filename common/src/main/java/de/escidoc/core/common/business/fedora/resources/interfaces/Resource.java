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

import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

import java.util.Map;

/**
 * Resource Interface.
 *
 * @author Steffen Wagner
 */
public interface Resource extends FedoraResource {

    /**
     * Get a map of the Resource properties. The keys are defined in PropertyMapKeys.
     *
     * @return properties map
     * @throws TripleStoreSystemException Thrown if requesting TripleStore failed.
     * @throws WebserverSystemException   Thrown if requesting Fedora failed or in case of internal (parser) error.
     */
    Map<String, String> getResourceProperties() throws TripleStoreSystemException, WebserverSystemException;

    /**
     * Indicate that the public-status of the resource has changed. E.g from pending to submitted or to released.
     *
     * @return True if public-status has changed during the current update. False if public-status is same as before
     *         update.
     */
    boolean hasPublicStatusChanged();

    /**
     * Indicate that the public-status has changed.
     */
    void setPublicStatusChange();

}
