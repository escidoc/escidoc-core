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
package de.escidoc.core.om.business.interfaces;

import de.escidoc.core.common.business.filter.SRURequestParameters;
import de.escidoc.core.common.business.interfaces.EscidocServiceRedirectInterface;
import de.escidoc.core.common.business.interfaces.IngestableResource;
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

import java.util.Map;

import org.esidoc.core.utils.io.EscidocBinaryContent;

/**
 * Interface of an item handler of the business layer.
 *
 * @author Torsten Tetteroo
 */
public interface ItemHandlerInterface extends IngestableResource {

    // FIXME: exception handling

    /**
     * Retrieves a filtered list of items.
     *
     * @param parameters parameters from the SRU request
     * @return Returns the XML representation of list of found items.
     * @throws SystemException If an unexpected error occurs.
     */
    String retrieveItems(final SRURequestParameters parameters) throws SystemException;

    /**
     * The method creates new Fedora objects for a content item and content components contained in a provided xml
     * string. First the provided xml string is validated against item xml schema. Then the provided xml is processed by
     * a StaxParser with a PrepareHandler to find out the number of content components in provided xml string and to
     * fetch a component binary content or respectively urls to the binary content. Then it triggers the Fedora service
     * getNextPid() to allocate pids for fedora objects which will represent the content item and provided number of
     * content components. After that it triggers the method retrieve() of the UserHandler component to get an accountId
     * of the caller of the create() method. Then provided xml is processed again by the StaxParser with Handler, which
     * splits the xml in to datastreams, modifies them and fetches some data for RELS-EXT datastream. The modified
     * datastreams contain now REST access urls, and some item/component properties are set. Following it calls the
     * method ItemCreator.handleComponent() for each content component, which handles component datastreams, requests
     * JHOVE Servlet to get a metadata about binary content, build FOXML for a component using an allocated pid and
     * store it in to fedora using fedora service ingest(). Then it calls the method ItemCreator.buildItemFoxml() and
     * store FOXML for the item in to fedora. The return value is a xml string which consists of modified datastreams of
     * item and components supplemented with created date and last modified date.
     *
     * @param xmlData provided xml string
     * @return xml string containing rest access urls and certain properties See Interface for functional description.
     * @throws AuthorizationException Thrown if the authorization fails.
     * @throws XmlCorruptedException  Thrown if provided data is corrupted.
     * @throws InvalidStatusException Thrown in case of an invalid status.
     * @throws SystemException        If an unexpected error occurs.
     * @throws RelationPredicateNotFoundException
     *                                cf. Interface
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingContentException
     * @throws de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingElementValueException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     */
    String create(String xmlData) throws MissingContentException, ContextNotFoundException,
        ContentModelNotFoundException, ReadonlyElementViolationException, MissingAttributeValueException,
        MissingElementValueException, ReadonlyAttributeViolationException, XmlCorruptedException,
        MissingMethodParameterException, FileNotFoundException, SystemException, InvalidContentException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, MissingMdRecordException,
        InvalidStatusException, AuthorizationException;

    /**
     * Deletes the specified resource.
     *
     * @param id The id of the resource.
     * @throws ItemNotFoundException     Thrown if an item with the specified id could not be found.
     * @throws AlreadyPublishedException Thrown if the item with the specified id has been published.
     * @throws LockingException          Thrown if Item is locked.
     * @throws InvalidStatusException    Thrown if Item has invalid status to delete
     * @throws MissingMethodParameterException
     *                                   Thrown if method parameter is missing
     * @throws SystemException           If an unexpected error occurs.
     * @throws AuthorizationException    Thrown if the authorization fails.
     */
    void delete(String id) throws ItemNotFoundException, AlreadyPublishedException, LockingException,
        InvalidStatusException, MissingMethodParameterException, SystemException, AuthorizationException;

    /**
     * Retrieves the specified resource.
     *
     * @param id The id of the resource.
     * @return Returns the XML representation of the resource.
     * @throws ItemNotFoundException      Thrown if an item with the specified id could not be found.
     * @throws MissingMethodParameterException
     *                                    Thrown if method parameter is missing
     * @throws ComponentNotFoundException Thrown if a component with the specified id could not be found.
     * @throws SystemException            If an unexpected error occurs.
     * @throws AuthorizationException     Thrown if the authorization fails.
     */
    String retrieve(String id) throws ItemNotFoundException, ComponentNotFoundException,
        MissingMethodParameterException, SystemException, AuthorizationException;

    /**
     * Updates the specified resource with the provided data.
     *
     * @param id      The id of the resource.
     * @param xmlData The new data of the resource.
     * @return Returns the XML representation of the updated resource.
     * @throws ItemNotFoundException        Thrown if an item with the specified id could not be found.
     * @throws FileNotFoundException        Thrown if a file could not be found.
     * @throws InvalidContextException      TODO
     * @throws InvalidStatusException       Thrown in case of an invalid status.
     * @throws LockingException             Thrown if Item is locked.
     * @throws XmlSchemaValidationException Thrown if the schema validation of the provided data fails.
     * @throws NotPublishedException        Thrown if the status shall be changed to withdrawn but the item has not been
     *                                      published.
     * @throws MissingLicenceException      Thrown if the status shall be changed to published but a license is
     *                                      missing.
     * @throws ComponentNotFoundException   Thrown if a component with the specified id could not be found.
     * @throws MissingMethodParameterException
     *                                      Thrown if method parameter is missing
     * @throws SystemException              If an unexpected error occurs.
     * @throws OptimisticLockingException   Thrown if an optimistic locking error occurs.
     * @throws ReadonlyViolationException   TODO
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingContentException
     * @throws de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     * @throws de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException
     */
    String update(String id, String xmlData) throws ItemNotFoundException, FileNotFoundException,
        InvalidContextException, InvalidStatusException, LockingException, NotPublishedException,
        MissingLicenceException, ComponentNotFoundException, MissingContentException, InvalidXmlException,
        MissingMethodParameterException, InvalidContentException, SystemException, OptimisticLockingException,
        AlreadyExistsException, ReadonlyViolationException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, ReadonlyVersionException, MissingAttributeValueException,
        MissingMdRecordException, AuthorizationException;

    //
    // Subresources
    //

    //
    // Subresource - component
    //

    /**
     * Creates the subresource component.
     *
     * @param id      The id of the resource.
     * @param xmlData The new value of the subresource.
     * @return Returns the value of the subresource.
     * @throws ItemNotFoundException        Thrown if an item with the specified id could not be found.
     * @throws LockingException             Thrown if Item is locked.
     * @throws InvalidStatusException       Thrown in case of an invalid status.
     * @throws MissingMethodParameterException
     *                                      If a mandatory element value is missing.
     * @throws FileNotFoundException        thrown if a file could not be found.
     * @throws SystemException              If an unexpected error occurs.
     * @throws XmlSchemaValidationException Thrown if the schema validation of the provided data fails.
     * @throws OptimisticLockingException   Thrown in case of an optimistic locking error.
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingContentException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyViolationException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingElementValueException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     */
    String createComponent(final String id, final String xmlData) throws ItemNotFoundException,
        ComponentNotFoundException, MissingContentException, LockingException, MissingElementValueException,
        InvalidXmlException, InvalidStatusException, MissingMethodParameterException, FileNotFoundException,
        InvalidContentException, SystemException, ReadonlyViolationException, OptimisticLockingException,
        MissingAttributeValueException;

    /**
     * Delete a Component of an Item.
     *
     * @param itemId      The item id.
     * @param componentId The component id.
     * @throws ItemNotFoundException      Thrown if an item with the specified id could not be found.
     * @throws ComponentNotFoundException Thrown if a component with the specified id could not be found.
     * @throws LockingException           Thrown if Item is locked.
     * @throws MissingMethodParameterException
     *                                    If a mandatory element value is missing.
     * @throws SystemException            If an unexpected error occurs.
     * @throws InvalidStatusException     Thrown in case of an invalid status.
     */
    void deleteComponent(final String itemId, final String componentId) throws ItemNotFoundException,
        ComponentNotFoundException, LockingException, MissingMethodParameterException, SystemException,
        InvalidStatusException;

    /**
     * Retrieves the subresource component.
     *
     * @param id          The id of the resource.
     * @param componentId The id of the component subresource.
     * @return Returns the value of the subresource.
     * @throws ItemNotFoundException      Thrown if an item with the specified id could not be found.
     * @throws ComponentNotFoundException Thrown if a component with the specified id could not be found.
     * @throws MissingMethodParameterException
     *                                    If a mandatory element value is missing.
     * @throws SystemException            If an unexpected error occurs.
     * @throws AuthorizationException     Thrown if the authorization fails.
     */
    String retrieveComponent(final String id, final String componentId) throws ItemNotFoundException,
        ComponentNotFoundException, MissingMethodParameterException, SystemException, AuthorizationException;

    /**
     * Retrieves md-records of the subresource component.
     *
     * @param id
     * @param componentId
     * @throws ItemNotFoundException      Thrown if an item with the specified id could not be found.
     * @throws ComponentNotFoundException Thrown if a component with the specified id could not be found.
     * @throws AuthorizationException     Thrown if the authorization fails.
     * @throws MissingMethodParameterException
     *                                    If a mandatory element value is missing.
     * @throws SystemException            If an unexpected error occurs.
     * @return
     */
    String retrieveComponentMdRecords(final String id, final String componentId) throws ItemNotFoundException,
        ComponentNotFoundException, MissingMethodParameterException, SystemException, AuthorizationException;

    /**
     * Retrieves a md-record of the subresource component.
     *
     * @param id
     * @param componentId
     * @param mdRecordId
     * @throws ItemNotFoundException      Thrown if an item with the specified id could not be found.
     * @throws ComponentNotFoundException Thrown if a component with the specified id could not be found.
     * @throws MissingMethodParameterException
     *                                    If a mandatory element value is missing.
     * @throws SystemException            If an unexpected error occurs.
     * @throws de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.security.AuthorizationException
     * @return
     */
    String retrieveComponentMdRecord(final String id, final String componentId, final String mdRecordId)
        throws ItemNotFoundException, ComponentNotFoundException, MdRecordNotFoundException,
        MissingMethodParameterException, SystemException, AuthorizationException;

    /**
     * Updates the subresource component.
     *
     * @param id          The id of the resource.
     * @param componentId The id of the component subresource.
     * @param xmlData     The new value of the subresource.
     * @return Returns the value of the subresource.
     * @throws ItemNotFoundException        Thrown if an item with the specified id could not be found.
     * @throws ComponentNotFoundException   Thrown if a component with the specified id could not be found.
     * @throws LockingException             Thrown if Item is locked
     * @throws XmlSchemaValidationException Thrown if the schema validation of the provided data fails.
     * @throws FileNotFoundException        thrown if a file could not be found.
     * @throws InvalidStatusException       Thrown in case of an invalid status.
     * @throws MissingMethodParameterException
     *                                      If a mandatory element value is missing.
     * @throws SystemException              If an unexpected error occurs.
     * @throws OptimisticLockingException   Thrown in case of an optimistic locking error.
     * @throws ReadonlyViolationException   TODO
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingContentException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     */
    String updateComponent(final String id, final String componentId, final String xmlData)
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, FileNotFoundException,
        MissingAttributeValueException, InvalidStatusException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidXmlException, ReadonlyViolationException, MissingContentException,
        InvalidContentException, ReadonlyVersionException, AuthorizationException;

    //
    // Subresource - components
    //

    /**
     * Retrieves the subresource components.
     *
     * @param id The id of the resource.
     * @return Returns the value of the subresource.
     * @throws ItemNotFoundException      Thrown if an item with the specified id could not be found.
     * @throws ComponentNotFoundException Thrown if a component with the specified id could not be found.
     * @throws MissingMethodParameterException
     *                                    If a mandatory element value is missing.
     * @throws SystemException            If an unexpected error occurs.
     * @throws AuthorizationException     Thrown if the authorization fails.
     */
    String retrieveComponents(final String id) throws ItemNotFoundException, ComponentNotFoundException,
        MissingMethodParameterException, SystemException, AuthorizationException;

    /**
     * @param id
     * @param componentId
     * @throws ItemNotFoundException      Thrown if an item with the specified id could not be found.
     * @throws ComponentNotFoundException Thrown if a component with the specified id could not be found.
     * @throws MissingMethodParameterException
     *                                    If a mandatory element value is missing.
     * @throws SystemException            If an unexpected error occurs.
     * @throws AuthorizationException     Thrown if the authorization fails.
     * @return
     */
    String retrieveComponentProperties(final String id, final String componentId) throws ItemNotFoundException,
        ComponentNotFoundException, MissingMethodParameterException, SystemException, AuthorizationException;

    //
    // Content
    //

    /**
     * Retrieves a content subresource.<br> This subresource provides access to the binary content of an item.
     *
     * @param id        The id of the resource.
     * @param contentId The id of the binary content that shall be retrieved.
     * @return Returns the binary content.
     * @throws ItemNotFoundException      Thrown if an item with the specified id could not be found.
     * @throws ComponentNotFoundException Thrown if the component containing the content cannot be found.
     * @throws MissingMethodParameterException
     *                                    If a mandatory element value is missing.
     * @throws SystemException            If an unexpected error occurs.
     * @throws InvalidStatusException     Thrown in case of an invalid status.
     * @throws AuthorizationException     Thrown if the authorization fails.
     */
    EscidocBinaryContent retrieveContent(final String id, final String contentId)
        throws MissingMethodParameterException, SystemException, InvalidStatusException, ResourceNotFoundException,
        AuthorizationException;

    /**
     * @param itemId
     * @param name
     * @throws ItemNotFoundException  Thrown if an item with the specified id could not be found.
     * @throws SystemException        If an unexpected error occurs.
     * @throws AuthorizationException Thrown if the authorization fails.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContentStreamNotFoundException
     * @return
     */
    @Deprecated
    EscidocBinaryContent retrieveContentStreamContent(final String itemId, final String name)
        throws ItemNotFoundException, SystemException, ContentStreamNotFoundException, AuthorizationException;

    /**
     * Retrieves a content subresource.<br> This subresource provides access to the binary content of an item.
     *
     * @param id          The id of the resource.
     * @param contentId   The id of the binary content that shall be retrieved.
     * @param transformer The transformation service.
     * @param param       parameter for the transformation service as GET parameter String (param1=val1&param2=val2).
     * @return Returns the binary content.
     * @throws ItemNotFoundException      Thrown if an item with the specified id could not be found.
     * @throws ComponentNotFoundException Thrown if the component containing the content cannot be found.
     * @throws MissingMethodParameterException
     *                                    If a mandatory element value is missing.
     * @throws SystemException            If an unexpected error occurs.
     * @throws InvalidStatusException     Thrown in case of an invalid status.
     * @throws AuthorizationException     Thrown if the authorization fails.
     */
    EscidocBinaryContent retrieveContent(
        final String id, final String contentId, final String transformer, final String param)
        throws ItemNotFoundException, ComponentNotFoundException, MissingMethodParameterException, SystemException,
        InvalidStatusException, AuthorizationException;

    /**
     * @param id
     * @param componentId
     * @param transformer
     * @param clientService
     * @throws ItemNotFoundException      Thrown if an item with the specified id could not be found.
     * @throws ComponentNotFoundException Thrown if a component with the specified id could not be found.
     * @throws MissingMethodParameterException
     *                                    If a mandatory element value is missing.
     * @throws SystemException            If an unexpected error occurs.
     * @throws InvalidStatusException     Thrown in case of an invalid status.
     * @throws AuthorizationException     Thrown if the authorization fails.
     * @return
     */
    EscidocServiceRedirectInterface redirectContentService(
        final String id, final String componentId, final String transformer, final String clientService)
        throws ItemNotFoundException, ComponentNotFoundException, MissingMethodParameterException, SystemException,
        InvalidStatusException, AuthorizationException;

    //
    // Subresource - metadata record
    //

    /**
     * Creates the subresource metadata record.
     *
     * @param id      The id of the resource.
     * @param xmlData The new value of the subresource.
     * @return Returns the value of the subresource.
     * @throws ItemNotFoundException        Thrown if an item with the specified id could not be found.
     * @throws XmlSchemaNotFoundException   Thrown if the schema specified in the data could not be found.
     * @throws XmlSchemaValidationException Thrown if the schema validation of the provided metadata fails.
     * @throws LockingException             Thrown if Item is locked.
     * @throws InvalidStatusException       Thrown in case of an invalid status.
     * @throws MissingMethodParameterException
     *                                      If a mandatory element value is missing.
     * @throws SystemException              If an unexpected error occurs.
     * @throws ComponentNotFoundException   Thrown if a component with the specified id could not be found.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     */
    @Deprecated
    String createMetadataRecord(final String id, final String xmlData) throws ItemNotFoundException,
        ComponentNotFoundException, XmlSchemaNotFoundException, LockingException, MissingAttributeValueException,
        InvalidStatusException, MissingMethodParameterException, SystemException, InvalidXmlException,
        AuthorizationException;

    /**
     * Creates the subresource metadata record.
     *
     * @param id      The id of the resource.
     * @param xmlData The new value of the subresource.
     * @return Returns the value of the subresource.
     * @throws ItemNotFoundException        Thrown if an item with the specified id could not be found.
     * @throws XmlSchemaValidationException Thrown if the schema validation of the provided metadata fails.
     * @throws LockingException             Thrown if Item is locked.
     * @throws InvalidStatusException       Thrown in case of an invalid status.
     * @throws SystemException              If an unexpected error occurs.
     * @throws ComponentNotFoundException   Thrown if a component with the specified id could not be found.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     */
    String createMdRecord(final String id, final String xmlData) throws ItemNotFoundException, SystemException,
        XmlSchemaValidationException, LockingException, MissingAttributeValueException, InvalidStatusException,
        ComponentNotFoundException, AuthorizationException;

    /**
     * Retrieves the subresource metadata record.
     *
     * @param id         The id of the resource.
     * @param mdRecordId The id of the metdata record subresource.
     * @return Returns the value of the subresource.
     * @throws ItemNotFoundException     Thrown if an item with the specified id could not be found.
     * @throws MdRecordNotFoundException Thrown if the item does not have the specified metadata record.
     * @throws MissingMethodParameterException
     *                                   If a mandatory element value is missing.
     * @throws SystemException           If an unexpected error occurs.
     * @throws AuthorizationException    Thrown if the authorization fails.
     */
    String retrieveMdRecord(final String id, final String mdRecordId) throws ItemNotFoundException,
        MdRecordNotFoundException, MissingMethodParameterException, SystemException, AuthorizationException;

    /**
     * @param id
     * @param mdRecordId
     * @throws ItemNotFoundException  Thrown if an item with the specified id could not be found.
     * @throws MissingMethodParameterException
     *                                If a mandatory element value is missing.
     * @throws SystemException        If an unexpected error occurs.
     * @throws AuthorizationException Thrown if the authorization fails.
     * @throws de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException
     * @return
     */
    String retrieveMdRecordContent(final String id, final String mdRecordId) throws ItemNotFoundException,
        MdRecordNotFoundException, MissingMethodParameterException, SystemException, AuthorizationException;

    /**
     * @param id
     * @throws ItemNotFoundException  Thrown if an item with the specified id could not be found.
     * @throws MissingMethodParameterException
     *                                If a mandatory element value is missing.
     * @throws SystemException        If an unexpected error occurs.
     * @throws AuthorizationException Thrown if the authorization fails.
     * @throws de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException
     * @return
     */
    String retrieveDcRecordContent(final String id) throws ItemNotFoundException, MissingMethodParameterException,
        SystemException, MdRecordNotFoundException, AuthorizationException;

    /**
     * Updates the subresource metadata record.
     *
     * @param id         The id of the resource.
     * @param mdRecordId The id of the metdata record subresource.
     * @param xmlData    The new value of the subresource.
     * @return Returns the value of the subresource.
     * @throws ItemNotFoundException        Thrown if an item with the specified id could not be found.
     * @throws XmlSchemaNotFoundException   Thrown if the specified schema could not be found.
     * @throws XmlSchemaValidationException Thrown if the schema validation of the provided metadata fails.
     * @throws LockingException             Thrown if Item is locked
     * @throws InvalidStatusException       Thrown in case of an invalid status.
     * @throws MissingMethodParameterException
     *                                      If a mandatory element value is missing.
     * @throws SystemException              If an unexpected error occurs.
     * @throws OptimisticLockingException   Thrown in case of an optimistic locking error.
     * @throws ReadonlyViolationException   TODO
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     * @throws de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException
     */
    String updateMetadataRecord(final String id, final String mdRecordId, final String xmlData)
        throws ItemNotFoundException, XmlSchemaNotFoundException, LockingException, InvalidContentException,
        MdRecordNotFoundException, InvalidStatusException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidXmlException, ReadonlyViolationException, ReadonlyVersionException,
        AuthorizationException;

    //
    // Subresource - metadata records
    //

    /**
     * Retrieves the subresource metadata records.
     *
     * @param id The id of the resource.
     * @return Returns the value of the subresource.
     * @throws ItemNotFoundException  Thrown if an item with the specified id could not be found.
     * @throws MissingMethodParameterException
     *                                If a mandatory element value is missing.
     * @throws SystemException        If an unexpected error occurs.
     * @throws AuthorizationException Thrown if the authorization fails.
     */
    String retrieveMdRecords(final String id) throws ItemNotFoundException, MissingMethodParameterException,
        SystemException, AuthorizationException;

    //
    // Subresource - content-streams
    //

    /**
     * Retrieves the subresource content streams containing a XML representation of each content stream.
     *
     * @param id The id of the resource.
     * @return Returns the value of the subresource.
     * @throws SystemException        If an unexpected error occurs.
     * @throws ItemNotFoundException  Thrown if an item with the specified id could not be found.
     * @throws AuthorizationException Thrown if the authorization fails.
     */
    @Deprecated
    String retrieveContentStreams(final String id) throws ItemNotFoundException, SystemException,
        AuthorizationException;

    /**
     * @param id
     * @param xmlData
     * @throws ItemNotFoundException Thrown if an item with the specified id could not be found.
     * @throws SystemException       If an unexpected error occurs.
     * @return
     */
    @Deprecated
    String updateContentStreams(final String id, final String xmlData) throws ItemNotFoundException, SystemException;

    /**
     * @param id
     * @throws ItemNotFoundException Thrown if an item with the specified id could not be found.
     * @throws SystemException       If an unexpected error occurs.
     */
    @Deprecated
    void deleteContentStreams(final String id) throws ItemNotFoundException, SystemException;

    /**
     * Retrieves a XML representation of the content stream specified by {@code name}.
     *
     * @param id   The id of the resource.
     * @param name The name of the content stream subresource.
     * @return Returns the value of the subresource.
     * @throws ItemNotFoundException  Thrown if an item with the specified id could not be found.
     * @throws AuthorizationException Thrown if the authorization fails.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContentStreamNotFoundException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     */
    @Deprecated
    String retrieveContentStream(final String id, final String name) throws ItemNotFoundException, SystemException,
        ContentStreamNotFoundException, AuthorizationException;

    /**
     * @param id
     * @param name
     * @param xml
     * @throws ItemNotFoundException Thrown if an item with the specified id could not be found.
     * @throws SystemException       If an unexpected error occurs.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContentStreamNotFoundException
     * @return
     */
    @Deprecated
    String updateContentStream(final String id, final String name, final String xml) throws ItemNotFoundException,
        SystemException, ContentStreamNotFoundException;

    /**
     * @param id
     * @param name
     * @throws ItemNotFoundException Thrown if an item with the specified id could not be found.
     * @throws SystemException       If an unexpected error occurs.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContentStreamNotFoundException
     */
    @Deprecated
    void deleteContentStream(final String id, final String name) throws ItemNotFoundException, SystemException,
        ContentStreamNotFoundException;

    /**
     * Creates a content stream in the resource specified.
     *
     * @param id      The id of the resource.
     * @param xmlData The XML representation of the new content stream subresource.
     * @return Returns the value of the subresource.
     */
    @Deprecated
    String createContentStream(final String id, final String xmlData);

    /**
     *
     * @param id
     * @param xmlData
     * @return
     */
    @Deprecated
    String createContentStreams(final String id, final String xmlData);

    //
    // Subresource - properties
    //

    /**
     * Retrieves the subresource properties.
     *
     * @param id The id of the resource.
     * @return Returns the value of the subresource.
     * @throws ItemNotFoundException Thrown if an item with the specified id could not be found.
     * @throws MissingMethodParameterException
     *                               If a mandatory element value is missing.
     * @throws SystemException       If an unexpected error occurs.
     */
    String retrieveProperties(final String id) throws ItemNotFoundException, MissingMethodParameterException,
        SystemException;

    //
    // Subresource - resources
    //

    /**
     * Retrieves the subresource resources.
     *
     * @param id The id of the resource.
     * @return Returns the value of the subresource.
     * @throws ItemNotFoundException Thrown if an item with the specified id could not be found.
     * @throws MissingMethodParameterException
     *                               If a mandatory element value is missing.
     * @throws SystemException       If an unexpected error occurs.
     */
    String retrieveResources(final String id) throws ItemNotFoundException, MissingMethodParameterException,
        SystemException;

    /**
     * Retrieve the version history subresource.
     *
     * @param id The id of the resource.
     * @return Returns the value of the subresource.
     * @throws ItemNotFoundException Thrown if an item with the specified id could not be found.
     * @throws SystemException       Thrown in case of internal error.
     */
    String retrieveVersionHistory(final String id) throws ItemNotFoundException, SystemException;

    /**
     * @param id
     * @throws SystemException Thrown if a framework internal error occurs.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException
     * @return
     */
    String retrieveParents(final String id) throws ItemNotFoundException, SystemException;

    /**
     * Publish an Item.
     *
     * @param id        The id of the Item.
     * @param taskParam The timestamp of the last modification of the item. Necessary for optimistic locking purpose.
     * @return last-modification-date within XML (result.xsd)
     * @throws ItemNotFoundException      Thrown if an item with the specified id could not be found.
     * @throws LockingException           Thrown if the Item is lock through another user.
     * @throws InvalidStatusException     Thrown in case of an invalid status.
     * @throws MissingMethodParameterException
     *                                    If a mandatory element value is missing.
     * @throws SystemException            If an unexpected error occurs.
     * @throws OptimisticLockingException Thrown in case of an optimistic locking error.
     * @throws ReadonlyViolationException TODO
     * @throws InvalidXmlException        TODO
     * @throws ComponentNotFoundException Thrown if a component with the specified id could not be found.
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException
     */
    String release(final String id, final String taskParam) throws ItemNotFoundException, ComponentNotFoundException,
        LockingException, InvalidStatusException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, ReadonlyViolationException, ReadonlyVersionException, InvalidXmlException;

    /**
     * Submit an Item.
     *
     * @param id        The id of the Item.
     * @param taskParam The timestamp of the last modification of the item. Necessary for optimistic locking purpose.
     * @return last-modification-date within XML (result.xsd)
     * @throws ItemNotFoundException      Thrown if an item with the specified id could not be found.
     * @throws LockingException           Thrown if Item is locked
     * @throws InvalidStatusException     Thrown in case of an invalid status.
     * @throws MissingMethodParameterException
     *                                    If a mandatory element value is missing.
     * @throws SystemException            If an unexpected error occurs.
     * @throws OptimisticLockingException Thrown in case of an optimistic locking error.
     * @throws ReadonlyViolationException TODO
     * @throws InvalidXmlException        TODO
     * @throws ComponentNotFoundException Thrown if a component with the specified id could not be found.
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException
     */
    String submit(final String id, final String taskParam) throws ItemNotFoundException, ComponentNotFoundException,
        LockingException, InvalidStatusException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, ReadonlyViolationException, ReadonlyVersionException, InvalidXmlException;

    /**
     * Set an Item in revision.
     *
     * @param id    The id of the Item.
     * @param param The timestamp of the last modification of the item. Necessary for optimistic locking purpose.
     * @return last-modification-date within XML (result.xsd)
     * @throws ItemNotFoundException      Thrown if an item with the specified id could not be found.
     * @throws LockingException           Thrown if Item is locked
     * @throws InvalidStatusException     Thrown in case of an invalid status.
     * @throws MissingMethodParameterException
     *                                    If a mandatory element value is missing.
     * @throws SystemException            If an unexpected error occurs.
     * @throws OptimisticLockingException Thrown in case of an optimistic locking error.
     * @throws ReadonlyViolationException TODO
     * @throws XmlCorruptedException      TODO
     * @throws ComponentNotFoundException Thrown if a component with the specified id could not be found.
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException
     */
    String revise(final String id, final String param) throws ItemNotFoundException, ComponentNotFoundException,
        LockingException, InvalidStatusException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, ReadonlyViolationException, ReadonlyVersionException, XmlCorruptedException;

    /**
     * Withdraw an Item.
     *
     * @param id        The id of the Item.
     * @param taskParam The timestamp of the last modification of the item. Necessary for optimistic locking purpose.
     * @return last-modification-date within XML (result.xsd)
     * @throws ItemNotFoundException      Thrown if an item with the specified id could not be found.
     * @throws NotPublishedException      Thrown if the status shall be changed to withdrawn but the item has not been
     *                                    published.
     * @throws LockingException           Thrown if Item is locked
     * @throws InvalidStatusException     Thrown in case of an invalid status.
     * @throws MissingMethodParameterException
     *                                    If a mandatory element value is missing.
     * @throws SystemException            If an unexpected error occurs.
     * @throws OptimisticLockingException Thrown in case of an optimistic locking error.
     * @throws ReadonlyViolationException TODO
     * @throws InvalidXmlException        TODO
     * @throws ComponentNotFoundException Thrown if a component with the specified id could not be found.
     * @throws de.escidoc.core.common.exceptions.application.violated.AlreadyWithdrawnException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException
     */
    String withdraw(final String id, final String taskParam) throws ItemNotFoundException, ComponentNotFoundException,
        NotPublishedException, LockingException, AlreadyWithdrawnException, InvalidStatusException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, ReadonlyViolationException,
        ReadonlyVersionException, InvalidXmlException;

    /**
     * Lock an Item for offline work.
     *
     * @param id        The id of the Item.
     * @param taskParam The timestamp of the last modification of the item. Necessary for optimistic locking purpose.
     * @return last-modification-date within XML (result.xsd)
     * @throws ItemNotFoundException      Thrown if an item with the specified id could not be found.
     * @throws LockingException           Thrown if Item is locked
     * @throws MissingMethodParameterException
     *                                    If a mandatory element value is missing.
     * @throws SystemException            If an unexpected error occurs.
     * @throws OptimisticLockingException Thrown in case of an optimistic locking error.
     * @throws InvalidXmlException        TODO
     * @throws InvalidStatusException     Thrown if Item is in status withdawn.
     * @throws ComponentNotFoundException Thrown if a component with the specified id could not be found.
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     */
    String lock(final String id, final String taskParam) throws ItemNotFoundException, ComponentNotFoundException,
        LockingException, InvalidContentException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidXmlException, InvalidStatusException;

    /**
     * Unlock an Item.
     *
     * @param id        The id of the Item.
     * @param taskParam The timestamp of the last modification of the item. Necessary for optimistic locking purpose.
     * @return last-modification-date within XML (result.xsd)
     * @throws ItemNotFoundException      Thrown if an item with the specified id could not be found.
     * @throws ComponentNotFoundException Thrown if a component with the specified id could not be found.
     * @throws LockingException           Thrown if Item is locked
     * @throws MissingMethodParameterException
     *                                    If a mandatory element value is missing.
     * @throws SystemException            If an unexpected error occurs.
     * @throws OptimisticLockingException Thrown in case of an optimistic locking error.
     * @throws InvalidXmlException        TODO
     */
    String unlock(final String id, final String taskParam) throws ItemNotFoundException, ComponentNotFoundException,
        LockingException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidXmlException;

    /**
     * @param id
     * @param taskParam
     * @throws ItemNotFoundException  Thrown if an item with the specified id could not be found.
     * @throws InvalidStatusException Thrown in case of an invalid status.
     * @throws MissingMethodParameterException
     *                                If a mandatory element value is missing.
     * @throws SystemException        If an unexpected error occurs.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.violated.LockingException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     * @return
     */
    String moveToContext(final String id, String taskParam) throws ItemNotFoundException, ContextNotFoundException,
        InvalidContentException, LockingException, InvalidStatusException, MissingMethodParameterException,
        SystemException;

    /**
     * Assign a Persistent Identifier to a definied object version.
     *
     * @param id        The id of the item.
     * @param taskParam Taskparameter XML containing the URL the should be assigned to:
     * @return last-modification-date within XML (result.xsd) including <pid>new pid</pid>
     * @throws ItemNotFoundException      Thrown if an item with the specified id could not be found.
     * @throws ComponentNotFoundException Thrown if a component with the specified id could not be found.
     * @throws LockingException           Thrown if Item is locked
     * @throws MissingMethodParameterException
     *                                    If a mandatory element value is missing.
     * @throws SystemException            If an unexpected error occurs.
     * @throws OptimisticLockingException Thrown in case of an optimistic locking error.
     * @throws InvalidStatusException     Thrown in case of an invalid status.
     * @throws ReadonlyVersionException   Thrown if a provided item version id is not a latest version.
     * @throws XmlCorruptedException      TODO
     */
    String assignVersionPid(final String id, final String taskParam) throws ItemNotFoundException,
        ComponentNotFoundException, LockingException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidStatusException, XmlCorruptedException, ReadonlyVersionException;

    /**
     * Assign a Persistent Identifier with objectPID to Item.
     *
     * @param id        The id of the item.
     * @param taskParam XML param structure <param> <url>http://some.url/resource</url> </param>
     * @return pid as xml snippet <param><pid>somePid</pid></param>
     * @throws ItemNotFoundException      Thrown if an item with the specified id could not be found.
     * @throws LockingException           Thrown if Item is locked
     * @throws MissingMethodParameterException
     *                                    If a mandatory element value is missing.
     * @throws SystemException            If an unexpected error occurs.
     * @throws OptimisticLockingException Thrown in case of an optimistic locking error.
     * @throws InvalidStatusException     Thrown in case of an invalid status.
     * @throws XmlCorruptedException      TODO
     * @throws ComponentNotFoundException Thrown if a component with the specified id could not be found.
     */
    String assignObjectPid(final String id, final String taskParam) throws ItemNotFoundException,
        ComponentNotFoundException, LockingException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidStatusException, XmlCorruptedException;

    /**
     * Assigns a Persistent Identifier to the Content.
     *
     * @param id          The ID of the Item.
     * @param componentId The ID of the Component.
     * @param taskParam   XML param structure <param> <url>http://some.url/resource</url> </param>
     * @return pid as xml snippet <param><pid>somePid</pid></param>
     * @throws ItemNotFoundException      Thrown if the Item could not be found under provided id.
     * @throws LockingException           Thrown if the Item is locked.
     * @throws MissingMethodParameterException
     *                                    Thrown if not all method parameter are provided.
     * @throws OptimisticLockingException Thrown if the resource has changed during method call.
     * @throws InvalidStatusException     Thrown if the Item status is invalid to assign a PID.
     * @throws XmlCorruptedException      Thorwn if taskParam is invalid XML.
     * @throws ComponentNotFoundException Thrown if the Component could not be found under provided id.
     * @throws ReadonlyVersionException   Thrown if a provided item version id is not a latest version.
     * @throws SystemException            Thrown in case of internal failure.
     */
    String assignContentPid(final String id, final String componentId, final String taskParam)
        throws ItemNotFoundException, LockingException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidStatusException, XmlCorruptedException, ComponentNotFoundException,
        ReadonlyVersionException;

    /**
     * Retrieves the subresource relations.
     *
     * @param id The id of the resource.
     * @return Returns the value of the subresource.
     * @throws ItemNotFoundException Thrown if an item with the specified id could not be found.
     * @throws MissingMethodParameterException
     *                               If a mandatory element value is missing.
     * @throws SystemException       If an unexpected error occurs.
     */
    String retrieveRelations(final String id) throws ItemNotFoundException, MissingMethodParameterException,
        SystemException;

    /**
     * @param id
     * @param taskParameter
     * @return last-modification-date within XML (result.xsd)
     * @throws ComponentNotFoundException Thrown if a component with the specified id could not be found.
     * @throws InvalidStatusException     Thrown in case of an invalid status.
     * @throws SystemException            If an unexpected error occurs.
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyViolationException
     * @throws de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     * @throws de.escidoc.core.common.exceptions.application.violated.LockingException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingElementValueException
     * @throws de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException
     */
    String addContentRelations(final String id, final String taskParameter) throws SystemException,
        ItemNotFoundException, ComponentNotFoundException, OptimisticLockingException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, AlreadyExistsException,
        InvalidStatusException, InvalidXmlException, MissingElementValueException, LockingException,
        ReadonlyViolationException, InvalidContentException, ReadonlyVersionException;

    /**
     * @param id
     * @param param
     * @return last-modification-date within XML (result.xsd)
     * @throws SystemException            If an unexpected error occurs.
     * @throws ItemNotFoundException      Thrown if an item with the specified id could not be found.
     * @throws OptimisticLockingException Thrown in case of an optimistic locking error.
     * @throws InvalidStatusException     Thrown in case of an invalid status.
     * @throws ComponentNotFoundException Thrown if a component with the specified id could not be found.
     * @throws InvalidXmlException        Thrown if taskParam is invalid XML.
     * @throws LockingException           Thrown if Item is locked
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyViolationException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     * @throws de.escidoc.core.common.exceptions.application.violated.AlreadyDeletedException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingElementValueException
     */
    String removeContentRelations(final String id, final String param) throws SystemException, ItemNotFoundException,
        ComponentNotFoundException, OptimisticLockingException, InvalidStatusException, MissingElementValueException,
        InvalidContentException, InvalidXmlException, ContentRelationNotFoundException, AlreadyDeletedException,
        LockingException, ReadonlyViolationException, ReadonlyVersionException;

    EscidocBinaryContent retrieveResource(
        final String id, final String resourceName, final Map<String, String[]> parameters) throws SystemException,
        ItemNotFoundException, OperationNotFoundException;
}
