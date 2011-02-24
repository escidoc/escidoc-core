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
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
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

import java.util.Map;

/**
 * Service layer implementation of a handler that manages eSciDoc roles.
 * 
 * @spring.bean id="service.RoleHandler"
 * @interface class="de.escidoc.core.aa.service.interfaces.RoleHandlerInterface"
 * @service
 * @author TTE
 * @aa
 */
public class RoleHandler implements RoleHandlerInterface {

    /** The business layer implementation bean. */
    private de.escidoc.core.aa.business.interfaces.RoleHandlerInterface business;



    /**
     * See Interface for functional description.
     * 
     * @param xmlData
     * @return
     * @throws UniqueConstraintViolationException
     * @throws InvalidXmlException
     * @throws MissingMethodParameterException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.RoleHandlerInterface
     *      #create(java.lang.String)
     * @aa
     */
    @Override
    public String create(final String xmlData)
        throws UniqueConstraintViolationException, 
        XmlCorruptedException, XmlSchemaValidationException,
        MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {

        return business.create(xmlData);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws MissingMethodParameterException
     * @throws RoleNotFoundException
     * @throws RoleInUseViolationException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.RoleHandlerInterface
     *      #delete(java.lang.String)
     * @aa
     */
    @Override
    public void delete(final String id) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        RoleNotFoundException, RoleInUseViolationException, SystemException {

        business.delete(id);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @throws RoleNotFoundException
     * @throws MissingMethodParameterException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.RoleHandlerInterface
     *      #retrieve(java.lang.String)
     * @aa
     */
    @Override
    public String retrieve(final String id) throws RoleNotFoundException,
        MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {

        return business.retrieve(id);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws MissingMethodParameterException
     * @throws RoleNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.RoleHandlerInterface
     *      #retrieveResources(java.lang.String)
     * @aa
     * 
     * @axis.exclude
     */
    @Override
    public String retrieveResources(final String id)
        throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, RoleNotFoundException, SystemException {

        return business.retrieveResources(id);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param xmlData
     * @return
     * @throws RoleNotFoundException
     * @throws XmlCorruptedException 
     * @throws XmlSchemaValidationException,
     * @throws MissingAttributeValueException
     * @throws UniqueConstraintViolationException
     * @throws OptimisticLockingException
     * @throws MissingMethodParameterException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.RoleHandlerInterface
     *      #update(java.lang.String, java.lang.String)
     * @aa
     */
    @Override
    public String update(final String id, final String xmlData)
        throws RoleNotFoundException, 
        XmlCorruptedException, XmlSchemaValidationException,
        MissingAttributeValueException, UniqueConstraintViolationException,
        OptimisticLockingException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {

        return business.update(id, xmlData);
    }

    /**
     * See Interface for functional description.
     * 
     * @param filter
     * @return
     * @throws MissingMethodParameterException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws SystemException
     * @throws InvalidSearchQueryException
     * @see de.escidoc.core.aa.service.interfaces.RoleHandlerInterface
     *      #retrieveRoles(java.util.Map)
     */
    @Override
    public String retrieveRoles(final Map<String, String[]> filter)
        throws MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, InvalidSearchQueryException {

        return business.retrieveRoles(filter);
    }



    /**
     * Injects the business object.
     * 
     * @param business
     *            The business layer bean
     * @spring.property ref="business.RoleHandler"
     * @service.exclude
     * @aa
     */
    public void setBusiness(
        final de.escidoc.core.aa.business.interfaces.RoleHandlerInterface business) {

        this.business = business;
    }

}
