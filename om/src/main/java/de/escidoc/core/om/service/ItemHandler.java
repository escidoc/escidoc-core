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
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingLicenceException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentStreamNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OperationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.XmlSchemaNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyDeletedException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyPublishedException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyWithdrawnException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.NotPublishedException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.om.service.interfaces.EscidocServiceRedirectInterface;
import de.escidoc.core.om.service.interfaces.ItemHandlerInterface;

import java.util.Map;

/**
 * An item resource handler.
 * 
 * @spring.bean id="service.ItemHandler" scope="prototype"
 * @interface class="de.escidoc.core.om.service.interfaces.ItemHandlerInterface"
 * @author TTE
 * @service
 */
public class ItemHandler implements ItemHandlerInterface {

    private de.escidoc.core.om.business.interfaces.ItemHandlerInterface handler;

    /**
     * Injects the item handler.
     * 
     * @param itemHandler
     *            The item handler bean to inject.
     * 
     * @spring.property ref="business.FedoraItemHandler"
     * @service.exclude
     */
    public void setItemHandler(
        final de.escidoc.core.om.business.interfaces.ItemHandlerInterface itemHandler) {

        this.handler = itemHandler;
    }

    // CHECKSTYLE:JAVADOC-OFF

    // FIXME: exception handling
    /**
     * See Interface for functional description.
     * 
     * @param xmlData
     * @return
     * @throws ReadonlyElementViolationException
     * @throws MissingAttributeValueException
     * @throws XmlCorruptedException
     * @throws XmlSchemaValidationException
     * @throws FileNotFoundException
     * @throws InvalidContentException
     * @throws InvalidStatusException
     * @throws ComponentNotFoundException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#create(java.lang.String)
     */
    public String create(final String xmlData) throws MissingContentException,
        ContextNotFoundException, ContentModelNotFoundException,
        ReadonlyElementViolationException, MissingAttributeValueException,
        MissingElementValueException, ReadonlyAttributeViolationException,
        AuthenticationException, AuthorizationException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException,
        FileNotFoundException, SystemException, InvalidContentException,
        ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, MissingMdRecordException,
        InvalidStatusException {

        return handler.create(xmlData);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @throws LockingException
     * @throws InvalidStatusException
     * @throws ComponentNotFoundException
     * @see de.escidoc.core.common.service.interfaces.ResourceHandlerInterface
     *      #delete(java.lang.String)
     */
    public void delete(final String id) throws ItemNotFoundException,
        AlreadyPublishedException, LockingException, AuthenticationException,
        AuthorizationException, InvalidStatusException,
        MissingMethodParameterException, SystemException {

        handler.delete(id);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @throws ComponentNotFoundException
     * @see de.escidoc.core.common.service.interfaces.ResourceHandlerInterface
     *      #retrieve(java.lang.String)
     */
    public String retrieve(final String id) throws ItemNotFoundException,
        ComponentNotFoundException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        SystemException {

        return handler.retrieve(id);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param xmlData
     * @return
     * @throws ReadonlyElementViolationException
     * @throws MissingAttributeValueException
     * @throws AlreadyPublishedException
     * @throws InvalidXmlException
     * @throws InvalidContentException
     * @throws ReadonlyVersionException
     * @throws MissingAttributeValueException
     * @see de.escidoc.core.common.service.interfaces.ResourceHandlerInterface
     *      #update(java.lang.String, java.lang.String)
     */
    public String update(final String id, final String xmlData)
        throws ItemNotFoundException, FileNotFoundException,
        InvalidContextException, InvalidStatusException, LockingException,
        NotPublishedException, MissingLicenceException,
        ComponentNotFoundException, MissingContentException,
        AuthenticationException, AuthorizationException, InvalidXmlException,
        MissingMethodParameterException, InvalidContentException,
        SystemException, OptimisticLockingException, AlreadyExistsException,
        ReadonlyViolationException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, ReadonlyVersionException,
        MissingAttributeValueException, MissingMdRecordException {

        return handler.update(id, xmlData);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param xmlData
     * @return
     * @throws MissingContentException
     * @throws ItemNotFoundException
     * @throws LockingException
     * @throws MissingElementValueException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws InvalidStatusException
     * @throws MissingMethodParameterException
     * @throws FileNotFoundException
     * @throws InvalidXmlException
     * @throws InvalidContentException
     * @throws SystemException
     * @throws ReadonlyViolationException
     * @throws OptimisticLockingException
     * @throws MissingAttributeValueException
     *             cf. Interface
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#createComponent(java.lang.String,
     *      java.lang.String)
     * 
     * 
     */
    public String createComponent(final String id, final String xmlData)
        throws MissingContentException, ItemNotFoundException,
        ComponentNotFoundException, LockingException,
        MissingElementValueException, AuthenticationException,
        AuthorizationException, InvalidStatusException,
        MissingMethodParameterException, FileNotFoundException,
        InvalidXmlException, InvalidContentException, SystemException,
        ReadonlyViolationException, OptimisticLockingException,
        MissingAttributeValueException {

        return handler.createComponent(id, xmlData);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param componentId
     * @return
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface
     *      #retrieveComponent(java.lang.String, java.lang.String)
     * 
     * 
     */
    public String retrieveComponent(final String id, final String componentId)
        throws ItemNotFoundException, ComponentNotFoundException,
        AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException {

        return handler.retrieveComponent(id, componentId);
    }

    /**
     * See Interface for functional description.
     * 
     * @param itemId
     * @param componentId
     * @return
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface
     *      #retrieveComponentMdRecords(java.lang.String, java.lang.String)
     * 
     * 
     */
    public String retrieveComponentMdRecords(
        final String id, final String componentId)
        throws ItemNotFoundException, ComponentNotFoundException,
        AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException {
        return handler.retrieveComponentMdRecords(id, componentId);
    }

    /**
     * See Interface for functional description.
     * 
     * @param itemId
     * @param componentId
     * @param mdRecordId
     * @return
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface
     *      #retrieveComponentMdRecord(java.lang.String, java.lang.String,
     *      java.lang.String)
     * 
     * 
     */
    public String retrieveComponentMdRecord(
        final String id, final String componentId, final String mdRecordId)
        throws ItemNotFoundException, AuthenticationException,
        AuthorizationException, ComponentNotFoundException,
        MdRecordNotFoundException, MissingMethodParameterException,
        SystemException {
        return handler.retrieveComponentMdRecord(id, componentId, mdRecordId);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param componentId
     * @param xmlData
     * @return
     * @throws ReadonlyElementViolationException
     * @throws MissingAttributeValueException
     * @throws InvalidStatusException
     * @throws InvalidContentException
     * @throws MissingContentException
     * @throws ReadonlyVersionException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface
     *      #updateComponent(java.lang.String, java.lang.String,
     *      java.lang.String)
     * 
     * 
     */
    public String updateComponent(
        final String id, final String componentId, final String xmlData)
        throws ItemNotFoundException, ComponentNotFoundException,
        LockingException, FileNotFoundException,
        MissingAttributeValueException, AuthenticationException,
        AuthorizationException, InvalidStatusException,
        MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidXmlException,
        ReadonlyViolationException, MissingContentException,
        InvalidContentException, ReadonlyVersionException {

        return handler.updateComponent(id, componentId, xmlData);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @throws ComponentNotFoundException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface
     *      #retrieveComponents(java.lang.String)
     * 
     * 
     */
    public String retrieveComponents(final String id)
        throws ItemNotFoundException, AuthenticationException,
        AuthorizationException, ComponentNotFoundException,
        MissingMethodParameterException, SystemException {

        return handler.retrieveComponents(id);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param componentId
     * @return
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface
     *      #retrieveComponentProperties(java.lang.String, java.lang.String)
     * 
     * 
     */
    public String retrieveComponentProperties(
        final String id, final String componentId)
        throws ItemNotFoundException, ComponentNotFoundException,
        AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException {

        return handler.retrieveComponentProperties(id, componentId);
    }

    /**
     * See Interface for functional description.
     * 
     * Deprecated because of inconsistent naming. Use createMdRecord instead of.
     * 
     * @param id
     * @param xmlData
     * @return
     * @throws MissingAttributeValueException
     * @throws InvalidStatusException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface
     *      #createMetadataRecord(java.lang.String, java.lang.String)
     */
    @Deprecated
    public String createMetadataRecord(final String id, final String xmlData)
        throws ItemNotFoundException, ComponentNotFoundException,
        XmlSchemaNotFoundException, LockingException,
        MissingAttributeValueException, AuthenticationException,
        AuthorizationException, InvalidStatusException,
        MissingMethodParameterException, SystemException, InvalidXmlException {

        return handler.createMetadataRecord(id, xmlData);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param xmlData
     * @return
     * @throws MissingAttributeValueException
     * @throws InvalidStatusException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface
     *      #createMetadataRecord(java.lang.String, java.lang.String)
     * 
     * 
     */
    public String createMdRecord(final String id, final String xmlData)
        throws ItemNotFoundException, SystemException, InvalidXmlException,
        LockingException, MissingAttributeValueException,
        InvalidStatusException, ComponentNotFoundException,
        MissingMethodParameterException, AuthorizationException,
        AuthenticationException {

        return handler.createMdRecord(id, xmlData);
    }

    //
    // Subresource - content
    //
    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param contentId
     * @return
     * @throws ItemNotFoundException
     * @throws InvalidStatusException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface
     *      #retrieveContent(java.lang.String, java.lang.String)
     * @axis.exclude
     * 
     * 
     */
    public EscidocBinaryContent retrieveContent(
        final String id, final String contentId) throws ItemNotFoundException,
        AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException,
        InvalidStatusException, ResourceNotFoundException {

        return handler.retrieveContent(id, contentId);
    }

    /**
     * See Interface for functional description.
     * 
     * @param itemId
     * @param name
     * @return
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws MissingMethodParameterException
     * @throws ItemNotFoundException
     * @throws SystemException
     * @throws ContentStreamNotFoundException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface
     *      #retrieveContentStreamContent(java.lang.String, java.lang.String)
     * 
     * @axis.exclude
     * 
     * 
     */
    public EscidocBinaryContent retrieveContentStreamContent(
        final String itemId, final String name) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        ItemNotFoundException, SystemException, ContentStreamNotFoundException {
        return handler.retrieveContentStreamContent(itemId, name);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     *            The item id.
     * @param contentId
     * @param transformer
     * @param param
     * @return transformed binary content
     * 
     * @throws ItemNotFoundException
     * @throws InvalidStatusException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface
     *      #retrieveContent(java.lang.String, java.lang.String)
     * @axis.exclude
     * 
     * 
     */
    public EscidocBinaryContent retrieveContent(
        final String id, final String contentId, final String transformer,
        final String param) throws ItemNotFoundException,
        ComponentNotFoundException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        SystemException, InvalidStatusException {

        return handler.retrieveContent(id, contentId, transformer, param);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     *            The item id.
     * @param contentId
     * @param transformer
     * @return
     * 
     * @throws ItemNotFoundException
     * @throws InvalidStatusException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface
     *      #retrieveContent(java.lang.String, java.lang.String)
     * @axis.exclude
     * 
     * 
     */
    public EscidocServiceRedirectInterface redirectContentService(
        final String id, final String contentId, final String transformer,
        final String clientService) throws ItemNotFoundException,
        ComponentNotFoundException, AuthorizationException,
        MissingMethodParameterException, SystemException,
        InvalidStatusException {

        return handler.redirectContentService(id, contentId, transformer,
            clientService);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param mdRecordId
     * @return
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface
     *      #retrieveMdRecord(java.lang.String, java.lang.String)
     * 
     * 
     */
    public String retrieveMdRecord(final String id, final String mdRecordId)
        throws ItemNotFoundException, MdRecordNotFoundException,
        AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException {

        return handler.retrieveMdRecord(id, mdRecordId);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param mdRecordId
     * @return
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface
     *      #retrieveMdRecordContent(java.lang.String, java.lang.String)
     * @axis.exclude
     * 
     * 
     */
    public String retrieveMdRecordContent(
        final String id, final String mdRecordId) throws ItemNotFoundException,
        MdRecordNotFoundException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        SystemException {

        return handler.retrieveMdRecordContent(id, mdRecordId);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface
     *      #retrieveDcRecordContent(java.lang.String)
     * @axis.exclude
     * @om
     */
    public String retrieveDcRecordContent(final String id)
        throws ItemNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException,
        MdRecordNotFoundException, SystemException {
        return handler.retrieveDcRecordContent(id);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param mdRecordId
     * @param xmlData
     * @return
     * @throws LockingException
     * @throws InvalidContentException
     * @throws MdRecordNotFoundException
     * @throws InvalidStatusException
     * @throws OptimisticLockingException
     * @throws ReadonlyVersionException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface
     *      #updateMdRecord(java.lang.String, java.lang.String,
     *      java.lang.String)
     * 
     * 
     */
    public String updateMdRecord(
        final String id, final String mdRecordId, final String xmlData)
        throws ItemNotFoundException, XmlSchemaNotFoundException,
        LockingException, InvalidContentException, MdRecordNotFoundException,
        AuthenticationException, AuthorizationException,
        InvalidStatusException, MissingMethodParameterException,
        SystemException, OptimisticLockingException, InvalidXmlException,
        ReadonlyViolationException, ReadonlyVersionException {

        return handler.updateMetadataRecord(id, mdRecordId, xmlData);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface
     *      #retrieveMdRecords(java.lang.String)
     * 
     * 
     */
    public String retrieveMdRecords(final String id)
        throws ItemNotFoundException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        SystemException {

        return handler.retrieveMdRecords(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @seede.escidoc.core.om.service.interfaces.ItemHandlerInterface#
     * retrieveContentStreams(java.lang.String)
     */
    public String retrieveContentStreams(final String id)
        throws ItemNotFoundException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        SystemException {
        return handler.retrieveContentStreams(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @seede.escidoc.core.om.service.interfaces.ItemHandlerInterface#
     * retrieveContentStream(java.lang.String, java.lang.String)
     */
    public String retrieveContentStream(final String id, final String name)
        throws ItemNotFoundException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        SystemException, ContentStreamNotFoundException {
        return handler.retrieveContentStream(id, name);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface
     *      #retrieveProperties(java.lang.String)
     * 
     * 
     */
    public String retrieveProperties(final String id)
        throws ItemNotFoundException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        SystemException {

        return handler.retrieveProperties(id);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface
     *      #retrieveResources(java.lang.String)
     * @axis.exclude
     * 
     * 
     */
    public String retrieveResources(final String id)
        throws ItemNotFoundException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        SystemException {

        return handler.retrieveResources(id);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @throws OperationNotFoundException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface
     *      #retrieveResources(java.lang.String)
     * @axis.exclude
     * 
     * @escidoc_core.warning Experimental
     * 
     * 
     * 
     */
    public EscidocBinaryContent retrieveResource(
        final String id, final String resourceName,
        final Map<String, String[]> parameters) throws ItemNotFoundException,
        AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException,
        OperationNotFoundException {
        return handler.retrieveResource(id, resourceName, parameters);
    }

    public String retrieveVersionHistory(final String id)
        throws ItemNotFoundException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        SystemException {
        return handler.retrieveVersionHistory(id);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface
     *      #retrieveParents(java.lang.String)
     */
    public String retrieveParents(final String id)
        throws ItemNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        return handler.retrieveParents(id);
    }

    public String retrieveRelations(final String id)
        throws ItemNotFoundException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        SystemException {

        return handler.retrieveRelations(id);
    }

    public String release(final String id, final String lastModified)
        throws ItemNotFoundException, ComponentNotFoundException,
        LockingException, InvalidStatusException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        SystemException, OptimisticLockingException,
        ReadonlyViolationException, ReadonlyVersionException,
        InvalidXmlException {

        return handler.release(id, lastModified);
    }

    public String submit(final String id, final String lastModified)
        throws ItemNotFoundException, ComponentNotFoundException,
        LockingException, InvalidStatusException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        SystemException, OptimisticLockingException,
        ReadonlyViolationException, ReadonlyVersionException,
        InvalidXmlException {

        return handler.submit(id, lastModified);
    }

    public String revise(final String id, final String lastModified)
        throws AuthenticationException, AuthorizationException,
        ItemNotFoundException, ComponentNotFoundException, LockingException,
        InvalidStatusException, MissingMethodParameterException,
        SystemException, OptimisticLockingException,
        ReadonlyViolationException, ReadonlyVersionException,
        InvalidContentException, XmlCorruptedException {

        return handler.revise(id, lastModified);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param lastModified
     * @return
     * @throws ItemNotFoundException
     * @throws NotPublishedException
     * @throws LockingException
     * @throws AlreadyWithdrawnException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws InvalidStatusException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws OptimisticLockingException
     * @throws ReadonlyViolationException
     * @throws ReadonlyVersionException
     * @throws InvalidXmlException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#withdraw(java.lang.String,
     *      java.lang.String)
     */
    public String withdraw(final String id, final String lastModified)
        throws ItemNotFoundException, ComponentNotFoundException,
        NotPublishedException, LockingException, AlreadyWithdrawnException,
        AuthenticationException, AuthorizationException,
        InvalidStatusException, MissingMethodParameterException,
        SystemException, OptimisticLockingException,
        ReadonlyViolationException, ReadonlyVersionException,
        InvalidXmlException {

        return handler.withdraw(id, lastModified);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param lastModified
     * @return
     * @throws ItemNotFoundException
     * @throws LockingException
     * @throws InvalidContentException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws OptimisticLockingException
     * @throws InvalidXmlException
     * @throws InvalidStatusException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#lock(java.lang.String,
     *      java.lang.String)
     */
    public String lock(final String id, final String lastModified)
        throws ItemNotFoundException, ComponentNotFoundException,
        LockingException, InvalidContentException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        SystemException, OptimisticLockingException, InvalidXmlException,
        InvalidStatusException {

        return handler.lock(id, lastModified);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param lastModified
     * @return
     * @throws ItemNotFoundException
     * @throws LockingException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws OptimisticLockingException
     * @throws InvalidXmlException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#unlock(java.lang.String,
     *      java.lang.String)
     */
    public String unlock(final String id, final String lastModified)
        throws ItemNotFoundException, ComponentNotFoundException,
        LockingException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidXmlException {

        return handler.unlock(id, lastModified);
    }

    /**
     * See Interface for functional description.
     * 
     * @param itemId
     * @param componentId
     * @throws ItemNotFoundException
     * @throws ComponentNotFoundException
     * @throws LockingException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws InvalidStatusException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#deleteComponent(java.lang.String,
     *      java.lang.String)
     * 
     * 
     */
    public void deleteComponent(final String itemId, final String componentId)
        throws ItemNotFoundException, ComponentNotFoundException,
        LockingException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException,
        InvalidStatusException {

        handler.deleteComponent(itemId, componentId);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param taskParam
     * @return
     * @throws ContextNotFoundException
     * @throws InvalidContentException
     * @throws ItemNotFoundException
     * @throws LockingException
     * @throws InvalidStatusException
     * @throws MissingMethodParameterException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws SystemException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#moveToContext(java.lang.String,
     *      java.lang.String)
     * 
     * 
     */
    public String moveToContext(final String id, final String taskParam)
        throws ContextNotFoundException, InvalidContentException,
        ItemNotFoundException, LockingException, InvalidStatusException,
        MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {

        return handler.moveToContext(id, taskParam);
    }

    /**
     * See Interface for functional description.
     * 
     * @param filter
     * @return
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface
     *      #retrieveItems(java.util.Map)
     */
    public String retrieveItems(final Map<String, String[]> filter)
        throws SystemException {

        return handler.retrieveItems(new LuceneRequestParameters(filter));
    }

    /**
     * See Interface for functional description.
     * 
     * @throws ItemNotFoundException
     * @throws LockingException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws OptimisticLockingException
     * @throws ReadonlyVersionException
     * @throws XmlCorruptedException
     *             Thrown if a provided item version id is not a latest version.
     */
    public String assignVersionPid(final String id, final String taskParam)
        throws ItemNotFoundException, ComponentNotFoundException,
        LockingException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidStatusException,
        XmlCorruptedException, ReadonlyVersionException {

        return handler.assignVersionPid(id, taskParam);
    }

    /**
     * See Interface for functional description.
     * 
     * @throws ItemNotFoundException
     * @throws LockingException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws OptimisticLockingException
     * @throws XmlCorruptedException
     */
    public String assignObjectPid(final String id, final String taskParam)
        throws ItemNotFoundException, ComponentNotFoundException,
        LockingException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidStatusException,
        XmlCorruptedException {

        return handler.assignObjectPid(id, taskParam);
    }

    /**
     * See Interface for functional description.
     * 
     * @throws ItemNotFoundException
     * @throws LockingException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws OptimisticLockingException
     * @throws InvalidStatusException
     * @throws ComponentNotFoundException
     * @throws XmlCorruptedException
     * @throws ReadonlyVersionException
     *             Thrown if a provided item version id is not a latest version.
     */
    public String assignContentPid(
        final String id, final String componentId, final String taskParam)
        throws ItemNotFoundException, LockingException,
        AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidStatusException,
        ComponentNotFoundException, XmlCorruptedException,
        ReadonlyVersionException {
        return handler.assignContentPid(id, componentId, taskParam);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param param
     * @return
     * @throws SystemException
     * @throws ItemNotFoundException
     * @throws OptimisticLockingException
     * @throws ReferencedResourceNotFoundException
     * @throws RelationPredicateNotFoundException
     * @throws AlreadyExistsException
     * @throws InvalidContentException
     * @throws InvalidStatusException
     * @throws InvalidXmlException
     * @throws MissingElementValueException
     * @throws LockingException
     * @throws ReadonlyViolationException
     *             cf. Interface
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#addContentRelations(java.lang.String,
     *      java.lang.String)
     */
    public String addContentRelations(final String id, final String param)
        throws SystemException, ItemNotFoundException,
        ComponentNotFoundException, OptimisticLockingException,
        ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, AlreadyExistsException,
        InvalidStatusException, InvalidXmlException,
        MissingElementValueException, LockingException,
        ReadonlyViolationException, InvalidContentException,
        AuthenticationException, AuthorizationException,
        MissingMethodParameterException, ReadonlyVersionException {

        return handler.addContentRelations(id, param);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param param
     * @throws SystemException
     * @throws ItemNotFoundException
     * @throws OptimisticLockingException
     * @throws InvalidStatusException
     * @throws MissingElementValueException
     * @throws InvalidContentException
     * @throws InvalidXmlException
     * @throws ContentRelationNotFoundException
     * @throws AlreadyDeletedException
     * @throws LockingException
     * @throws ReadonlyViolationException
     *             cf. Interface
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#removeContentRelations(java.lang.String,
     *      java.lang.String)
     */
    public String removeContentRelations(final String id, final String param)
        throws SystemException, ItemNotFoundException,
        ComponentNotFoundException, OptimisticLockingException,
        InvalidStatusException, MissingElementValueException,
        InvalidContentException, InvalidXmlException,
        ContentRelationNotFoundException, AlreadyDeletedException,
        LockingException, ReadonlyViolationException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        ReadonlyVersionException {

        return handler.removeContentRelations(id, param);

    }
}
