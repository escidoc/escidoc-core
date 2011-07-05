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
package de.escidoc.core.om.business.fedora.context;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.util.string.StringUtility;

/**
 * @author Steffen Wagner
 */
public class ContextHandlerDelete extends ContextHandlerCreate {

    /**
     * Removes an context from repository.
     *
     * @param contextHandler FedoraContextHandler
     * @throws ContextNotFoundException If context could not be found.
     * @throws InvalidStatusException   If context is in invalid status.
     * @throws SystemException          If anything else fails.
     */
    public void remove(final FedoraContextHandler contextHandler) throws ContextNotFoundException,
        InvalidStatusException, SystemException {

        final Context context = contextHandler.getContext();
        final String objectType = getTripleStoreUtility().getObjectType(context.getId());

        if (!getTripleStoreUtility().exists(context.getId()) || !Constants.CONTEXT_OBJECT_TYPE.equals(objectType)) {
            throw new ContextNotFoundException(StringUtility.format("Context not found", context.getId()));
        }

        checkStatus(Constants.STATUS_CONTEXT_CREATED);
        this.getFedoraServiceClient().deleteObject(context.getId());
        this.getFedoraServiceClient().sync();
        try {
            this.getTripleStoreUtility().reinitialize();
        }
        catch (TripleStoreSystemException e) {
            throw new FedoraSystemException("Error on reinitializing triple store.", e);
        }
    }
}
