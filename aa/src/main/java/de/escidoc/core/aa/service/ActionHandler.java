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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.service;

import de.escidoc.core.aa.service.interfaces.ActionHandlerInterface;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service layer implementation of a handler that manages eSciDoc actions.
 *
 * @author Torsten Tetteroo
 */
@Service("service.ActionHandler")
public class ActionHandler implements ActionHandlerInterface {

    @Autowired
    @Qualifier("business.ActionHandler")
    private ActionHandlerInterface business;

    /**
     * See Interface for functional description.
     */
    @Override
    public String createUnsecuredActions(final String contextId, final String actions) throws ContextNotFoundException,
        XmlCorruptedException, XmlSchemaValidationException, AuthenticationException, AuthorizationException,
        SystemException {

        return business.createUnsecuredActions(contextId, actions);
    }

    /**
     * See Interface for functional description.
     *
     * @see ActionHandlerInterface #deleteUnsecuredActions(java.lang.String)
     */
    @Override
    public void deleteUnsecuredActions(final String contextId) throws ContextNotFoundException,
        AuthenticationException, AuthorizationException, SystemException {

        business.deleteUnsecuredActions(contextId);
    }

    /**
     * See Interface for functional description.
     *
     * @see ActionHandlerInterface #retrieveUnsecuredActions(java.lang.String)
     */
    @Override
    public String retrieveUnsecuredActions(final String contextId) throws ContextNotFoundException,
        AuthenticationException, AuthorizationException, SystemException {

        return business.retrieveUnsecuredActions(contextId);
    }

}
