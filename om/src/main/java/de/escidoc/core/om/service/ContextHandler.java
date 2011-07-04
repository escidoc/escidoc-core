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

import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.business.filter.LuceneRequestParameters;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * A context resource handler.
 *
 * @author Torsten Tetteroo
 */
@Service("service.ContextHandler")
public class ContextHandler implements ContextHandlerInterface {

    @Autowired
    @Qualifier("business.FedoraContextHandler")
    private de.escidoc.core.om.business.interfaces.ContextHandlerInterface handler;

    // FIXME: exception handling

    /**
     * See Interface for functional description.
     *
     */
    @Override
    public String create(final String xmlData) throws MissingMethodParameterException, ContextNameNotUniqueException,
        AuthenticationException, AuthorizationException, SystemException, ContentModelNotFoundException,
        ReadonlyElementViolationException, MissingAttributeValueException, MissingElementValueException,
        ReadonlyAttributeViolationException, InvalidContentException, OrganizationalUnitNotFoundException,
        InvalidStatusException, XmlCorruptedException, XmlSchemaValidationException {

        return handler.create(xmlData);
    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    public void delete(final String id) throws ContextNotFoundException, ContextNotEmptyException,
        MissingMethodParameterException, InvalidStatusException, AuthenticationException, AuthorizationException,
        SystemException {

        handler.delete(id);
    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    public String retrieve(final String id) throws ContextNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {

        return handler.retrieve(id);
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public String retrieveProperties(final String id) throws ContextNotFoundException, SystemException {
        return handler.retrieveProperties(id);
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.common.business.interfaces.ResourceHandlerInterface #update(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public String update(final String id, final String xmlData) throws ContextNotFoundException,
        MissingMethodParameterException, InvalidContentException, InvalidStatusException, AuthenticationException,
        AuthorizationException, ReadonlyElementViolationException, ReadonlyAttributeViolationException,
        OptimisticLockingException, ContextNameNotUniqueException, InvalidXmlException, MissingElementValueException,
        SystemException {

        return handler.update(id, xmlData);
    }

    @Override
    public EscidocBinaryContent retrieveResource(
        final String id, final String resourceName, final Map<String, String[]> parameters)
        throws OperationNotFoundException, ContextNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {

        return handler.retrieveResource(id, resourceName, parameters);
    }

    @Override
    public String retrieveResources(final String id) throws ContextNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {

        return handler.retrieveResources(id);
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public String open(final String id, final String taskParam) throws ContextNotFoundException,
        MissingMethodParameterException, InvalidStatusException, AuthenticationException, AuthorizationException,
        OptimisticLockingException, InvalidXmlException, SystemException, LockingException, StreamNotFoundException {

        return handler.open(id, taskParam);
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public String close(final String id, final String taskParam) throws ContextNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        OptimisticLockingException, InvalidXmlException, InvalidStatusException, LockingException,
        StreamNotFoundException {

        return handler.close(id, taskParam);
    }

    /**
     * See Interface for functional description.
     *
     * @see ContextHandlerInterface #retrieveContexts(java.util.Map)
     */
    @Override
    public String retrieveContexts(final Map<String, String[]> filter) throws MissingMethodParameterException,
        SystemException {
        return handler.retrieveContexts(new LuceneRequestParameters(filter));
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public String retrieveMembers(final String id, final Map<String, String[]> filter) throws ContextNotFoundException,
        MissingMethodParameterException, SystemException {

        return handler.retrieveMembers(id, new LuceneRequestParameters(filter));
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public String retrieveAdminDescriptor(final String id, final String name) throws ContextNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        AdminDescriptorNotFoundException {

        return handler.retrieveAdminDescriptor(id, name);
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public String retrieveAdminDescriptors(final String id) throws ContextNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {

        return handler.retrieveAdminDescriptors(id);
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public String updateAdminDescriptor(final String id, final String xmlData) throws ContextNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        OptimisticLockingException, AdminDescriptorNotFoundException, InvalidXmlException {

        return handler.updateAdminDescriptor(id, xmlData);
    }
}
