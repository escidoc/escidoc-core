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

import java.util.Map;

import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.business.filter.SRURequestParameters;
import de.escidoc.core.common.exceptions.application.invalid.ContextNotEmptyException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.AdminDescriptorNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OperationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.ContextNameNotUniqueException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.om.service.interfaces.ContextHandlerInterface;

/**
 * A context resource handler.
 * 
 * @spring.bean id="service.ContextHandler"
 * @interface 
 *            class="de.escidoc.core.om.service.interfaces.ContextHandlerInterface"
 * @author TTE
 * @service
 */
public class ContextHandler implements ContextHandlerInterface {

    private de.escidoc.core.om.business.interfaces.ContextHandlerInterface handler;

    /**
     * Injects the context handler.
     * 
     * @param contextHandler
     *            The context handler bean to inject.
     * 
     * @spring.property ref="business.FedoraContextHandler"
     * @service.exclude
     */
    public void setContextHandler(
        final de.escidoc.core.om.business.interfaces.ContextHandlerInterface contextHandler) {

        this.handler = contextHandler;
    }

    // CHECKSTYLE:JAVADOC-OFF

    // FIXME: exception handling
    /**
     * See Interface for functional description.
     * 
     * @param xmlData
     * @return
     * @throws ContextNotFoundException
     * @see de.escidoc.core.common.service.interfaces.ResourceHandlerInterface
     *      #create(java.lang.String)
     */
    public String create(final String xmlData)
        throws MissingMethodParameterException, ContextNameNotUniqueException,
        AuthenticationException, AuthorizationException, SystemException,
        ContentModelNotFoundException, ReadonlyElementViolationException,
        MissingAttributeValueException, MissingElementValueException,
        ReadonlyAttributeViolationException, InvalidContentException,
        OrganizationalUnitNotFoundException, InvalidStatusException,
        XmlCorruptedException, XmlSchemaValidationException {

        return handler.create(xmlData);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @see de.escidoc.core.common.service.interfaces.ResourceHandlerInterface
     *      #delete(java.lang.String)
     */
    public void delete(final String id) throws ContextNotFoundException,
        ContextNotEmptyException, MissingMethodParameterException,
        InvalidStatusException, AuthenticationException,
        AuthorizationException, SystemException {

        handler.delete(id);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @see de.escidoc.core.common.service.interfaces.ResourceHandlerInterface
     *      #retrieve(java.lang.String)
     */
    public String retrieve(final String id) throws ContextNotFoundException,
        MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {

        return handler.retrieve(id);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @throws ContextNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.om.service.interfaces.ContextHandlerInterface#retrieveProperties(java.lang.String)
     */
    public String retrieveProperties(final String id)
        throws ContextNotFoundException, SystemException {
        return handler.retrieveProperties(id);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param xmlData
     * @return
     * @throws InvalidContentException
     * @see de.escidoc.core.common.service.interfaces.ResourceHandlerInterface
     *      #update(java.lang.String, java.lang.String)
     */
    public String update(final String id, final String xmlData)
        throws ContextNotFoundException, MissingMethodParameterException,
        InvalidContentException, InvalidStatusException,
        AuthenticationException, AuthorizationException,
        ReadonlyElementViolationException, ReadonlyAttributeViolationException,
        OptimisticLockingException, ContextNameNotUniqueException,
        InvalidXmlException, MissingElementValueException, SystemException {

        return handler.update(id, xmlData);
    }

    //
    // Subresources
    //

    //
    // Subresource - resources
    //

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param resourceName
     * @param parameters
     * 
     * @return
     * @see de.escidoc.core.om.service.interfaces.ContextHandlerInterface
     *      #retrieveResource(java.lang.String, java.lang.String, java.util.Map)
     * @axis.exclude
     */
    public EscidocBinaryContent retrieveResource(
        final String id, final String resourceName,
        final Map<String, String[]> parameters)
        throws OperationNotFoundException, ContextNotFoundException,
        MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {

        return handler.retrieveResource(id, resourceName, parameters);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @see de.escidoc.core.om.service.interfaces.ContextHandlerInterface
     *      #retrieveResources(java.lang.String)
     * @axis.exclude
     */
    public String retrieveResources(final String id)
        throws ContextNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {

        return handler.retrieveResources(id);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param taskParam
     * @throws ContextNotFoundException
     * @throws MissingMethodParameterException
     * @throws InvalidStatusException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws OptimisticLockingException
     * @throws InvalidXmlException
     * @throws SystemException
     * @throws StreamNotFoundException
     * @throws LockingException
     * @see de.escidoc.core.om.service.interfaces.ContextHandlerInterface#open(java.lang.String,
     *      java.lang.String)
     */
    public String open(final String id, final String taskParam)
        throws ContextNotFoundException, MissingMethodParameterException,
        InvalidStatusException, AuthenticationException,
        AuthorizationException, OptimisticLockingException,
        InvalidXmlException, SystemException, LockingException,
        StreamNotFoundException {

        return handler.open(id, taskParam);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param taskParam
     * @throws ContextNotFoundException
     * @throws MissingMethodParameterException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws SystemException
     * @throws OptimisticLockingException
     * @throws InvalidXmlException
     * @throws InvalidStatusException
     * @throws StreamNotFoundException
     * @throws LockingException
     * @see de.escidoc.core.om.service.interfaces.ContextHandlerInterface#close(java.lang.String,
     *      java.lang.String)
     */
    public String close(final String id, final String taskParam)
        throws ContextNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException,
        OptimisticLockingException, InvalidXmlException,
        InvalidStatusException, LockingException, StreamNotFoundException {

        return handler.close(id, taskParam);
    }

    /**
     * See Interface for functional description.
     * 
     * @param filter
     * @return
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @see de.escidoc.core.om.service.interfaces.ContextHandlerInterface
     *      #retrieveContexts(java.util.Map)
     */
    public String retrieveContexts(final Map<String, String[]> filter)
        throws MissingMethodParameterException, SystemException {
        return handler.retrieveContexts(new SRURequestParameters(filter));
    }

    //
    // Subresource - members
    //

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param filter
     * @return
     * @throws ContextNotFoundException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @see de.escidoc.core.om.service.interfaces.ContextHandlerInterface#retrieveMembers(java.lang.String,
     *      java.util.Map)
     */
    public String retrieveMembers(
        final String id, final Map<String, String[]> filter)
        throws ContextNotFoundException, MissingMethodParameterException,
        SystemException {

        return handler.retrieveMembers(id, new SRURequestParameters(filter));
    }

    //
    // Subresource - admin descriptor
    //

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @throws ContextNotFoundException
     * @throws MissingMethodParameterException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws SystemException
     * @throws AdminDescriptorNotFoundException
     * @see de.escidoc.core.om.service.interfaces.ContextHandlerInterface#retrieveAdminDescriptor(java.lang.String)
     */
    public String retrieveAdminDescriptor(final String id, final String name)
        throws ContextNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException,
        AdminDescriptorNotFoundException {

        return handler.retrieveAdminDescriptor(id, name);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @throws ContextNotFoundException
     * @throws MissingMethodParameterException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws SystemException
     * @see de.escidoc.core.om.service.interfaces.ContextHandlerInterface#retrieveAdminDescriptor(java.lang.String)
     */
    public String retrieveAdminDescriptors(final String id)
        throws ContextNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {

        return handler.retrieveAdminDescriptors(id);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param xmlData
     * @return
     * @throws ContextNotFoundException
     * @throws MissingMethodParameterException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws SystemException
     * @throws OptimisticLockingException
     * @throws AdminDescriptorNotFoundException
     * @throws InvalidXmlException
     * @see de.escidoc.core.om.service.interfaces.ContextHandlerInterface#updateAdminDescriptor(java.lang.String,
     *      java.lang.String)
     * 
     * @service.exclude
     */
    public String updateAdminDescriptor(final String id, final String xmlData)
        throws ContextNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException,
        OptimisticLockingException, AdminDescriptorNotFoundException,
        InvalidXmlException {

        return handler.updateAdminDescriptor(id, xmlData);
    }
}
