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
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContextException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContextStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidItemStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OperationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.XmlSchemaNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyWithdrawnException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.om.service.interfaces.ContainerHandlerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * A container resource handler.
 *
 * @author Torsten Tetteroo
 */
@Service("service.ContainerHandler")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ContainerHandler implements ContainerHandlerInterface {

    @Autowired
    @Qualifier("business.FedoraContainerHandler")
    private de.escidoc.core.om.business.interfaces.ContainerHandlerInterface handler;

    // FIXME: exception handling

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.common.business.interfaces.ResourceHandlerInterface#create(java.lang.String)
     */
    @Override
    public String create(final String xmlData) throws ContextNotFoundException, ContentModelNotFoundException,
        InvalidContentException, MissingMethodParameterException, XmlCorruptedException,
        MissingAttributeValueException, MissingElementValueException, SystemException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, AuthenticationException,
        AuthorizationException, InvalidStatusException, MissingMdRecordException, XmlSchemaValidationException {

        return handler.create(xmlData);
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.common.business.interfaces.ResourceHandlerInterface#delete(java.lang.String)
     */
    @Override
    public void delete(final String id) throws ContainerNotFoundException, LockingException, InvalidStatusException,
        SystemException, MissingMethodParameterException, AuthenticationException, AuthorizationException {

        handler.delete(id);
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.common.business.interfaces.ResourceHandlerInterface#retrieve(java.lang.String)
     */
    @Override
    public String retrieve(final String id) throws MissingMethodParameterException, ContainerNotFoundException,
        AuthenticationException, AuthorizationException, SystemException {

        return handler.retrieve(id);
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.common.business.interfaces.ResourceHandlerInterface#update(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public String update(final String id, final String xmlData) throws ContainerNotFoundException, LockingException,
        InvalidContentException, MissingMethodParameterException, InvalidXmlException, OptimisticLockingException,
        InvalidStatusException, ReadonlyVersionException, SystemException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, AuthenticationException, AuthorizationException,
        MissingAttributeValueException, MissingMdRecordException {

        return handler.update(id, xmlData);
    }

    //
    // Subresources
    //

    //
    // Subresource - members
    //

    /**
     * See Interface for functional description.
     */
    @Override
    public String retrieveMembers(final String id, final Map<String, String[]> filter)
        throws ContainerNotFoundException, InvalidSearchQueryException, MissingMethodParameterException,
        SystemException {

        return handler.retrieveMembers(id, new LuceneRequestParameters(filter));
    }

    /**
     * See Interface for functional description.
     *
     * @return List of Tocs
     */
    @Override
    public String retrieveTocs(final String id, final Map<String, String[]> filter) throws ContainerNotFoundException,
        InvalidXmlException, InvalidSearchQueryException, MissingMethodParameterException, SystemException {

        return handler.retrieveTocs(id, new LuceneRequestParameters(filter));
    }

    @Override
    public String addMembers(final String id, final String taskParam) throws ContainerNotFoundException,
        LockingException, InvalidContentException, OptimisticLockingException, MissingMethodParameterException,
        SystemException, InvalidContextException, AuthenticationException, AuthorizationException,
        MissingAttributeValueException {

        return handler.addMembers(id, taskParam);
    }

    /**
     * See Interface for functional description.
     *
     * @return last-modification-date within XML (result.xsd)
     */
    @Override
    public String addTocs(final String id, final String taskParam) throws ContainerNotFoundException, LockingException,
        InvalidContentException, OptimisticLockingException, MissingMethodParameterException, SystemException,
        InvalidContextException, AuthenticationException, AuthorizationException, MissingAttributeValueException {

        return handler.addTocs(id, taskParam);
    }

    @Override
    public String removeMembers(final String id, final String taskParam) throws ContextNotFoundException,
        LockingException, XmlSchemaValidationException, ItemNotFoundException, InvalidContextStatusException,
        InvalidItemStatusException, AuthenticationException, AuthorizationException, SystemException,
        ContainerNotFoundException, InvalidContentException {

        return handler.removeMembers(id, taskParam);
    }

    //
    // Subresource - metadata record
    //

    /**
     * See Interface for functional description.
     * <p/>
     * Deprecated because of inconsistent naming. Use createMdRecord instead of.
     *
     * @see ContainerHandlerInterface #createMetadataRecord(java.lang.String, java.lang.String)
     */
    @Override
    @Deprecated
    public String createMetadataRecord(final String id, final String xmlData) throws ContainerNotFoundException,
        InvalidXmlException, LockingException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {

        return handler.createMdRecord(id, xmlData);
    }

    /**
     * See Interface for functional description.
     *
     * @see ContainerHandlerInterface #createMdRecord(java.lang.String, java.lang.String)
     */
    @Override
    public String createMdRecord(final String id, final String xmlData) throws ContainerNotFoundException,
        InvalidXmlException, LockingException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {

        return handler.createMdRecord(id, xmlData);
    }

    /**
     * See Interface for functional description.
     *
     * @see ContainerHandlerInterface #retrieveMdRecord(java.lang.String, java.lang.String)
     */
    @Override
    public String retrieveMdRecord(final String id, final String mdRecordId) throws ContainerNotFoundException,
        MissingMethodParameterException, MdRecordNotFoundException, AuthenticationException, AuthorizationException,
        SystemException {

        return handler.retrieveMdRecord(id, mdRecordId);
    }

    @Override
    public String retrieveMdRecordContent(final String id, final String mdRecordId) throws ContainerNotFoundException,
        MdRecordNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException {

        return handler.retrieveMdRecordContent(id, mdRecordId);
    }

    @Override
    public String retrieveDcRecordContent(final String id) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        return handler.retrieveDcRecordContent(id);
    }

    @Override
    public String updateMetadataRecord(final String id, final String mdRecordId, final String xmlData)
        throws ContainerNotFoundException, LockingException, XmlSchemaNotFoundException, MdRecordNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        InvalidXmlException, InvalidStatusException, ReadonlyVersionException {

        return handler.updateMetadataRecord(id, mdRecordId, xmlData);
    }

    @Override
    public String retrieveMdRecords(final String id) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {

        return handler.retrieveMdRecords(id);
    }

    @Override
    public String retrieveProperties(final String id) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {

        return handler.retrieveProperties(id);
    }

    @Override
    public String retrieveResources(final String id) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {

        return handler.retrieveResources(id);
    }

    @Override
    public EscidocBinaryContent retrieveResource(
        final String id, final String resourceName, final Map<String, String[]> parameters) throws SystemException,
        ContainerNotFoundException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        OperationNotFoundException {
        return handler.retrieveResource(id, resourceName, parameters);
    }

    @Override
    public String retrieveStructMap(final String id) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {

        return handler.retrieveStructMap(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @seede.escidoc.core.om.service.interfaces.ContainerHandlerInterface#
     * retrieveVersions(java.lang.String)
     */
    @Override
    public String retrieveVersionHistory(final String id) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        return handler.retrieveVersionHistory(id);
    }

    /**
     * See Interface for functional description.
     *
     * @see ContainerHandlerInterface #retrieveParents(java.lang.String)
     */
    @Override
    public String retrieveParents(final String id) throws ContainerNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        return handler.retrieveParents(id);
    }

    /**
     * See Interface for functional description.
     *
     * @see ContainerHandlerInterface #retrieveContentRelations(java.lang.String)
     */
    @Override
    public String retrieveRelations(final String id) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        return handler.retrieveRelations(id);
    }

    //
    // Subresource - status
    //

    @Override
    public String release(final String id, final String lastModified) throws ContainerNotFoundException,
        LockingException, MissingMethodParameterException, AuthenticationException, ReadonlyVersionException,
        AuthorizationException, InvalidStatusException, SystemException, OptimisticLockingException,
        InvalidXmlException {

        return handler.release(id, lastModified);
    }

    @Override
    public String submit(final String id, final String lastModified) throws ContainerNotFoundException,
        LockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        InvalidStatusException, SystemException, OptimisticLockingException, ReadonlyVersionException,
        InvalidXmlException {

        return handler.submit(id, lastModified);
    }

    @Override
    public String withdraw(final String id, final String lastModified) throws ContainerNotFoundException,
        LockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        InvalidStatusException, SystemException, OptimisticLockingException, AlreadyWithdrawnException,
        ReadonlyVersionException, InvalidXmlException {

        return handler.withdraw(id, lastModified);
    }

    @Override
    public String revise(final String id, final String lastModified) throws ContainerNotFoundException,
        LockingException, MissingMethodParameterException, InvalidStatusException, SystemException,
        OptimisticLockingException, ReadonlyVersionException, XmlCorruptedException {

        return handler.revise(id, lastModified);
    }

    @Override
    public String lock(final String id, final String lastModified) throws ContainerNotFoundException, LockingException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        OptimisticLockingException, InvalidStatusException, InvalidXmlException {

        return handler.lock(id, lastModified);
    }

    @Override
    public String unlock(final String id, final String lastModified) throws ContainerNotFoundException,
        LockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException, OptimisticLockingException, InvalidStatusException, InvalidXmlException {

        return handler.unlock(id, lastModified);
    }

    @Override
    public String moveToContext(final String containerId, final String taskParam) throws ContainerNotFoundException,
        ContextNotFoundException, InvalidContentException, LockingException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {

        return handler.moveToContext(containerId, taskParam);
    }

    @Override
    public String createItem(final String containerId, final String xmlData) throws ContainerNotFoundException,
        MissingContentException, ContextNotFoundException, ContentModelNotFoundException,
        ReadonlyElementViolationException, MissingAttributeValueException, MissingElementValueException,
        ReadonlyAttributeViolationException, MissingMethodParameterException, InvalidXmlException,
        FileNotFoundException, LockingException, InvalidContentException, InvalidContextException,
        RelationPredicateNotFoundException, ReferencedResourceNotFoundException, SystemException,
        AuthenticationException, AuthorizationException, MissingMdRecordException, InvalidStatusException {

        return handler.createItem(containerId, xmlData);
    }

    @Override
    public String createContainer(final String containerId, final String xmlData)
        throws MissingMethodParameterException, ContainerNotFoundException, LockingException, ContextNotFoundException,
        ContentModelNotFoundException, InvalidContentException, InvalidXmlException, MissingAttributeValueException,
        MissingElementValueException, AuthenticationException, AuthorizationException, InvalidContextException,
        RelationPredicateNotFoundException, InvalidStatusException, ReferencedResourceNotFoundException,
        SystemException, MissingMdRecordException {

        return handler.createContainer(containerId, xmlData);
    }

    /**
     * See Interface for functional description.
     *
     * @see ContainerHandlerInterface #retrieveContainers(java.util.Map)
     */
    @Override
    public String retrieveContainers(final Map<String, String[]> filter) throws MissingMethodParameterException,
        InvalidSearchQueryException, InvalidXmlException, SystemException {

        return handler.retrieveContainers(new LuceneRequestParameters(filter));
    }

    /**
     * See Interface for functional description.
     *
     * @throws ReadonlyVersionException cf. Interface
     */
    @Override
    public String addContentRelations(final String id, final String param) throws SystemException,
        ContainerNotFoundException, OptimisticLockingException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, AlreadyExistsException, InvalidStatusException, InvalidXmlException,
        MissingElementValueException, LockingException, ReadonlyVersionException, InvalidContentException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException {

        return handler.addContentRelations(id, param);
    }

    /**
     * See Interface for functional description.
     *
     * @throws ReadonlyVersionException cf. Interface
     */
    @Override
    public String removeContentRelations(final String id, final String param) throws SystemException,
        ContainerNotFoundException, OptimisticLockingException, InvalidStatusException, MissingElementValueException,
        InvalidXmlException, ContentRelationNotFoundException, LockingException, ReadonlyVersionException,
        AuthenticationException, AuthorizationException {

        return handler.removeContentRelations(id, param);
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public String assignObjectPid(final String id, final String param) throws InvalidStatusException,
        ContainerNotFoundException, LockingException, MissingMethodParameterException, OptimisticLockingException,
        SystemException, InvalidXmlException {

        return handler.assignObjectPid(id, param);
    }

    /**
     * See Interface for functional description.
     *
     * @throws ReadonlyVersionException Thrown if a provided container version id is not a latest version.
     */
    @Override
    public String assignVersionPid(final String id, final String param) throws ContainerNotFoundException,
        LockingException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidStatusException, XmlCorruptedException, ReadonlyVersionException {

        return handler.assignVersionPid(id, param);
    }

}
