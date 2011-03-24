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

package de.escidoc.core.common.business.fedora;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.persistence.interfaces.ResourceIdentifierDao;

/**
 * {@link ResourceIdentifierDao} implementation using the Fedora repository.
 *
 * @author Torsten Tetteroo
 */
public class FedoraResourceIdentifierDao implements ResourceIdentifierDao {

    private FedoraUtility fedoraUtility;

    /**
     * See Interface for functional description.
     * 
     * @param noOfPids
     * @return
     * @throws SystemException
     * @see ResourceIdentifierDao#getNextPids(int)
     */
    @Override
    public String[] getNextPids(final int noOfPids) throws SystemException {

        return getFedoraUtility().getNextPID(noOfPids);
    }

    /**
     * Gets the {@link FedoraUtility}.
     * 
     * @return FedoraUtility Returns the {@link FedoraUtility} object.
     */
    protected FedoraUtility getFedoraUtility() {

        return this.fedoraUtility;
    }

    /**
     * Injects the {@link FedoraUtility}.
     * 
     * @param fedoraUtility
     *            The {@link FedoraUtility} to set
     */
    public void setFedoraUtility(final FedoraUtility fedoraUtility) {

        this.fedoraUtility = fedoraUtility;
    }

}
