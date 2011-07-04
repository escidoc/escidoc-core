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
package de.escidoc.core.aa.service;

import de.escidoc.core.aa.service.interfaces.RoleHandlerInterface;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.RoleNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.RoleInUseViolationException;
import de.escidoc.core.common.exceptions.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service layer implementation of a handler that manages eSciDoc roles.
 *
 * @author Torsten Tetteroo
 */
@Service("service.RoleHandler")
public class RoleHandler implements RoleHandlerInterface {

    /**
     * The business layer implementation bean.
     */
    @Autowired
    @Qualifier("business.RoleHandler")
    private de.escidoc.core.aa.business.interfaces.RoleHandlerInterface business;

    /**
     * See Interface for functional description.
     *
     * @see RoleHandlerInterface #create(java.lang.String)
     */
    @Override
    public String create(final String xmlData) throws UniqueConstraintViolationException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException {

        return business.create(xmlData);
    }

    /**
     * See Interface for functional description.
     *
     * @see RoleHandlerInterface #delete(java.lang.String)
     */
    @Override
    public void delete(final String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, RoleNotFoundException, RoleInUseViolationException, SystemException {

        business.delete(id);
    }

    /**
     * See Interface for functional description.
     *
     * @see RoleHandlerInterface #retrieve(java.lang.String)
     */
    @Override
    public String retrieve(final String id) throws RoleNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {

        return business.retrieve(id);
    }

    @Override
    public String retrieveResources(final String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, RoleNotFoundException, SystemException {

        return business.retrieveResources(id);
    }

    @Override
    public String update(final String id, final String xmlData) throws RoleNotFoundException, XmlCorruptedException,
        XmlSchemaValidationException, MissingAttributeValueException, UniqueConstraintViolationException,
        OptimisticLockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException {

        return business.update(id, xmlData);
    }

    /**
     * See Interface for functional description.
     *
     * @see RoleHandlerInterface #retrieveRoles(java.util.Map)
     */
    @Override
    public String retrieveRoles(final Map<String, String[]> filter) throws MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException, InvalidSearchQueryException {

        return business.retrieveRoles(filter);
    }

}
