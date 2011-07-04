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

import de.escidoc.core.common.business.fedora.HandlerBase;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.om.business.renderer.interfaces.ContextRendererInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Contains base functionality to handle Fedora Context.
 *
 * @author Steffen Wagner
 */
public class ContextHandlerBase extends HandlerBase {

    @Autowired
    private ContextRendererInterface contextRenderer;

    private Context context;

    @Autowired
    @Qualifier("business.Utility")
    private Utility utility;

    public ContextRendererInterface getContextRenderer() {
        return contextRenderer;
    }

    /**
     * @return Return the Context.
     */
    public Context getContext() {
        return this.context;
    }

    /**
     * Bounds a Context object to this handler. Subsequent calls to this method have no effect.
     *
     * @param id The ID of the context which should be bound to this Handler.
     * @throws ContextNotFoundException If there is no context with {@code id} in the repository.
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     */
    public void setContext(final String id) throws ContextNotFoundException, TripleStoreSystemException,
        IntegritySystemException {

        this.context = new Context(id);
    }

    /**
     * @return Returns the utility.
     */
    @Override
    public Utility getUtility() {
        return this.utility;
    }

    /**
     * Check Status of Context against given value.
     *
     * @param status Value of Context status which is to check.
     * @throws InvalidStatusException Thrown if status compares of Context not to the status parameter.
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     */
    protected void checkStatus(final String status) throws InvalidStatusException, TripleStoreSystemException {
        final String objectStatus =
            getTripleStoreUtility().getPropertiesElements(this.context.getId(), TripleStoreUtility.PROP_PUBLIC_STATUS);

        if (!objectStatus.equals(status)) {
            throw new InvalidStatusException("Context " + context.getId() + " is in "
                + TripleStoreUtility.PROP_PUBLIC_STATUS + " '" + objectStatus + "'.");
        }
    }

}
