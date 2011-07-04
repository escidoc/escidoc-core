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
package de.escidoc.core.om.service;

import de.escidoc.core.common.exceptions.application.invalid.InvalidTripleStoreOutputFormatException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidTripleStoreQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.om.service.interfaces.SemanticStoreHandlerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * A semantic store handler.
 *
 * @author Rozita Friedman
 */
@Service("service.SemanticStoreHandler")
public class SemanticStoreHandler implements SemanticStoreHandlerInterface {

    @Autowired
    @Qualifier("business.FedoraSemanticStoreHandler")
    private de.escidoc.core.om.business.interfaces.SemanticStoreHandlerInterface handler;

    /**
     * Retrieves a result of provided triple store query in a provided output format.
     *
     * @return Returns XML representation of the query result.
     * @throws SystemException         TODO
     * @throws InvalidTripleStoreQueryException
     *                                 TODO
     * @throws InvalidTripleStoreOutputFormatException
     *                                 TODO
     * @throws AuthenticationException Thrown in case of a failed authentication.
     * @throws AuthorizationException  Thrown in case of a failed authorization.
     */
    @Override
    public String spo(final String taskParam) throws SystemException, InvalidTripleStoreQueryException,
        InvalidTripleStoreOutputFormatException, InvalidXmlException, MissingElementValueException,
        AuthenticationException, AuthorizationException {

        return handler.spo(taskParam);
    }

}
