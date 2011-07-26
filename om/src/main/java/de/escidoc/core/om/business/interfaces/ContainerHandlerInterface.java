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
import de.escidoc.core.common.business.interfaces.IngestableResource;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContextException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContextStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidItemStatusException;
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

import java.util.Map;

import org.esidoc.core.utils.io.EscidocBinaryContent;

/**
 * Interface of a container handler of the business layer.
 *
 * @author Torsten Tetteroo
 */
public interface ContainerHandlerInterface extends IngestableResource {

    /**
     * Creates a resource with the provided data.
     *
     * @param xmlData The data of the resource.
     * @return Returns the XML representation of the created resource, now containing the id by which the resource can
     *         be identified in the system.
     * @throws ContextNotFoundException      Thrown if the context specified in the provided data cannot be found.
     * @throws ContentModelNotFoundException Thrown if the content type specified in the provided data cannot be found.
     * @throws InvalidContentException       Thrown if the content is invalid.
     * @throws MissingMethodParameterException
     *                                       Thrown if a method parameter is missing
     * @throws SystemException               Thrown if a framework internal error occurs.
     * @throws InvalidStatusException        Thrown if an organizational unit is in an invalid status.
     * @throws XmlCorruptedException         Thrown if provided data is corrupted.
     * @throws XmlSchemaValidationException  Thrown if the schema validation of the provided data fails.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingElementValueException
     * @throws de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     */
    String create(final String xmlData) throws ContextNotFoundException, ContentModelNotFoundException,
        InvalidContentException, MissingMethodParameterException, XmlCorruptedException,
        MissingAttributeValueException, MissingElementValueException, SystemException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, InvalidStatusException,
        MissingMdRecordException, XmlSchemaValidationException;

    /**
     * Deletes the specified resource.
     *
     * @param id The id of the resource.
     * @throws ContainerNotFoundException Thrown if a container with the provided id cannot be found.
     * @throws LockingException           Thrown if container is locked.
     * @throws SystemException            Thrown if a framework internal error occurs.
     * @throws AuthorizationException     If further calls fail because of insufficient access rights.
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException
     */
    void delete(final String id) throws ContainerNotFoundException, LockingException, InvalidStatusException,
        SystemException, AuthorizationException;

    /**
     * Retrieves the specified resource.
     *
     * @param id The id of the resource.
     * @return Returns the XML representation of the resource.
     * @throws ContainerNotFoundException Thrown if a container with the provided id cannot be found.
     * @throws MissingMethodParameterException
     *                                    Thrown if a method parameter is missing
     * @throws SystemException            Thrown if a framework internal error occurs.
     */
    String retrieve(final String id) throws ContainerNotFoundException, MissingMethodParameterException,
        SystemException;

    /**
     * Updates the specified resource with the provided data.
     *
     * @param id      The id of the resource.
     * @param xmlData The new data of the resource.
     * @return Returns the XML representation of the updated resource.
     * @throws ContainerNotFoundException Thrown if a container with the provided id cannot be found.
     * @throws LockingException           Thrown in case of an optimistic locking error.
     * @throws InvalidContentException    Thrown if the content is invalid.
     * @throws MissingMethodParameterException
     *                                    Thrown if a method parameter is missing
     * @throws SystemException            Thrown if a framework internal error occurs.
     * @throws ReadonlyVersionException   TODO
     * @throws de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException
     * @throws de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     * @throws de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException
     */
    String update(final String id, final String xmlData) throws ContainerNotFoundException, LockingException,
        InvalidContentException, MissingMethodParameterException, InvalidXmlException, OptimisticLockingException,
        InvalidStatusException, MissingAttributeValueException, ReadonlyVersionException, SystemException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, MissingMdRecordException;

    /**
     * Retrieve Containers via filter.
     *
     * @param parameters parameters from the SRU request
     * @return List of Containers
     * @throws SystemException Thrown if a framework internal error occurs.
     */
    String retrieveContainers(final SRURequestParameters parameters) throws SystemException;

    /**
     * Retrieves the subresource members.<br> This subresource provides access to a list of the containers and items of
     * the specified container. This list contains the object's data instead of references to them.
     *
     * @param id         The id of the resource.
     * @param parameters parameters from the SRU request
     * @return Returns xml representation of a list of containers and items.
     * @throws ContainerNotFoundException Thrown if a container with the provided id cannot be found.
     * @throws SystemException            Thrown if a framework internal error occurs.
     */
    String retrieveMembers(final String id, final SRURequestParameters parameters) throws ContainerNotFoundException,
        SystemException;

    /**
     * Retrieves the subresource tocs.<br> This subresource provides access to a list of the items of the specified
     * container which are of content model Toc. The toc content model ID is configurable in escidoc-core.properties.
     * This list contains the object's data instead of references to them.
     *
     * @param id         The id of the resource.
     * @param parameters parameters from the SRU request
     * @return Returns xml representation of a list of toc items.
     * @throws ContainerNotFoundException Thrown if a container with the provided id cannot be found.
     * @throws SystemException            Thrown if a framework internal error occurs.
     */
    String retrieveTocs(final String id, final SRURequestParameters parameters) throws ContainerNotFoundException,
        SystemException;

    /**
     * @param id
     * @param taskParam
     * @return last-modification-date within XML (result.xsd)
     * @throws SystemException Thrown if a framework internal error occurs.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.violated.LockingException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContextException
     * @throws de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     */
    String addTocs(final String id, final String taskParam) throws ContainerNotFoundException, LockingException,
        InvalidContentException, OptimisticLockingException, SystemException, InvalidContextException,
        MissingAttributeValueException;

    /**
     * @param id
     * @param taskParam
     * @return last-modification-date within XML (result.xsd)
     * @throws SystemException Thrown if a framework internal error occurs.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.violated.LockingException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContextException
     * @throws de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     */
    String addMembers(final String id, final String taskParam) throws ContainerNotFoundException, LockingException,
        InvalidContentException, OptimisticLockingException, SystemException, InvalidContextException,
        MissingAttributeValueException;

    /**
     * @param id
     * @param taskParam
     * @throws SystemException Thrown if a framework internal error occurs.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.violated.LockingException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidItemStatusException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContextStatusException
     * @throws de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     * @return
     */
    String removeMembers(final String id, final String taskParam) throws ContextNotFoundException, LockingException,
        XmlSchemaValidationException, ItemNotFoundException, InvalidContextStatusException, InvalidItemStatusException,
        SystemException, ContainerNotFoundException, InvalidContentException;

    //
    // Subresource - metadata record
    //

    /**
     * Creates the subresource metadata record.
     *
     * @param id      The id of the resource.
     * @param xmlData The new value of the subresource.
     * @return Returns the value of the subresource.
     * @throws ContainerNotFoundException Thrown if a container with the provided id cannot be found.
     * @throws LockingException           Thrown in case of an optimistic locking error.
     * @throws MissingMethodParameterException
     *                                    Thrown if a method parameter is missing
     * @throws InvalidXmlException        TODO
     */
    String createMdRecord(final String id, final String xmlData) throws ContainerNotFoundException,
        InvalidXmlException, LockingException, MissingMethodParameterException;

    /**
     * Retrieves the subresource metadata record.
     *
     * @param id         The id of the resource.
     * @param mdRecordId The id of the metadata record subresource.
     * @return Returns the value of the subresource.
     * @throws ContainerNotFoundException Thrown if a container with the provided id cannot be found.
     * @throws MdRecordNotFoundException  Thrown if the container does not have the specified metadata record.
     * @throws SystemException            Thrown if a framework internal error occurs.
     */
    String retrieveMdRecord(final String id, final String mdRecordId) throws ContainerNotFoundException,
        MdRecordNotFoundException, SystemException;

    /**
     * @param id
     * @param mdRecordId
     * @throws MissingMethodParameterException
     *                         Thrown if a method parameter is missing
     * @throws SystemException Thrown if a framework internal error occurs.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException
     * @return
     */
    String retrieveMdRecordContent(final String id, final String mdRecordId) throws ContainerNotFoundException,
        MdRecordNotFoundException, MissingMethodParameterException, SystemException;

    /**
     * @param id
     * @throws MissingMethodParameterException
     *                         Thrown if a method parameter is missing
     * @throws SystemException Thrown if a framework internal error occurs.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException
     * @return
     */
    String retrieveDcRecordContent(final String id) throws ContainerNotFoundException, MissingMethodParameterException,
        SystemException;

    /**
     * Updates the subresource metadata record.
     *
     * @param id         The id of the resource.
     * @param mdRecordId The id of the metadata record subresource.
     * @param xmlData    The new value of the subresource.
     * @return Returns the value of the subresource.
     * @throws ContainerNotFoundException   Thrown if a container with the provided id cannot be found.
     * @throws LockingException             Thrown in case of an optimistic locking error.
     * @throws XmlSchemaValidationException Thrown if the schema validation of the provided data fails.
     * @throws XmlSchemaNotFoundException   Thrown if the specified schema cannot be found.
     * @throws MdRecordNotFoundException    TODO
     * @throws MissingMethodParameterException
     *                                      Thrown if a method parameter is missing
     * @throws SystemException              Thrown if a framework internal error occurs.
     * @throws InvalidXmlException          TODO
     * @throws ReadonlyVersionException     TODO
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException
     */
    String updateMetadataRecord(final String id, final String mdRecordId, final String xmlData)
        throws ContainerNotFoundException, LockingException, XmlSchemaNotFoundException, MdRecordNotFoundException,
        MissingMethodParameterException, SystemException, InvalidXmlException, InvalidStatusException,
        ReadonlyVersionException;

    //
    // Subresource - metadata records
    //

    /**
     * Retrieves the subresource metadata records.
     *
     * @param id The id of the resource.
     * @return Returns the value of the subresource.
     * @throws ContainerNotFoundException Thrown if a container with the provided id cannot be found.
     * @throws SystemException            Thrown if a framework internal error occurs.
     */
    String retrieveMdRecords(final String id) throws ContainerNotFoundException, SystemException;

    //
    // Subresource - properties
    //

    /**
     * Retrieves the subresource properties.
     *
     * @param id The id of the resource.
     * @return Returns the value of the subresource.
     * @throws ContainerNotFoundException Thrown if a container with the provided id cannot be found.
     * @throws MissingMethodParameterException
     *                                    Thrown if a method parameter is missing
     * @throws SystemException            Thrown if a framework internal error occurs.
     */
    String retrieveProperties(final String id) throws ContainerNotFoundException, MissingMethodParameterException,
        SystemException;

    //
    // Subresource - resources
    //

    /**
     * Retrieves the subresource resources.
     *
     * @param id The id of the resource.
     * @return Returns the value of the subresource.
     * @throws ContainerNotFoundException Thrown if a container with the provided id cannot be found.
     * @throws MissingMethodParameterException
     *                                    Thrown if a method parameter is missing
     * @throws SystemException            Thrown if a framework internal error occurs.
     */
    String retrieveResources(final String id) throws ContainerNotFoundException, MissingMethodParameterException,
        SystemException;

    /**
     * Retrieve the content of the specified virtual Resources of a container.
     * <p/>
     * <b>Prerequisites:</b><br/> The container must exist<br/> <b>Tasks:</b><br/> <ul> <li>The container is accessed
     * using the provided Id.</li> <li>Determine if the resource is available.</li> <li>Create the content of the
     * resource.</li> <li>The data is returned.</li> </ul>
     *
     * @param id           The id of the container.
     * @param resourceName The name of the resource.
     * @param parameters
     * @return The content of the resource.
     * @throws ContainerNotFoundException Thrown if an item with the specified id cannot be found.
     * @throws SystemException            If an error occurs.
     * @throws de.escidoc.core.common.exceptions.application.notfound.OperationNotFoundException
     */
    EscidocBinaryContent retrieveResource(
        final String id, final String resourceName, final Map<String, String[]> parameters) throws SystemException,
        ContainerNotFoundException, OperationNotFoundException;

    /**
     * @param id
     * @throws MissingMethodParameterException
     *                         Thrown if a method parameter is missing
     * @throws SystemException Thrown if a framework internal error occurs.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException
     * @return
     */
    String retrieveRelations(final String id) throws ContainerNotFoundException, MissingMethodParameterException,
        SystemException;

    /**
     * @param id
     * @throws SystemException Thrown if a framework internal error occurs.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException
     * @return
     */
    String retrieveVersionHistory(final String id) throws ContainerNotFoundException, SystemException;

    /**
     * @param id
     * @throws SystemException Thrown if a framework internal error occurs.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException
     * @return
     */
    String retrieveParents(final String id) throws ContainerNotFoundException, SystemException;

    //
    // Subresource - structural map
    //

    String retrieveStructMap(final String id) throws ContainerNotFoundException, MissingMethodParameterException,
        SystemException;

    // /**
    // * Retrieves the subresource structural map.
    // *
    // * @param id
    // * The id of the resource.
    // * @return Returns the value of the subresource.
    // * @throws ContainerNotFoundException
    // * Thrown if a container with the provided id cannot be found.
    // *
    // */
    // String retrieveStructuralMap(final String id)
    // throws ContainerNotFoundException;
    //
    // /**
    // * Updates the subresource structural map.
    // *
    // * @param id
    // * The id of the resource.
    // * @param xmlData
    // * The new value of the subresource.
    // * @return Returns the value of the updated subresource.
    // * @throws ContainerNotFoundException
    // * Thrown if a container with the provided id cannot be found.
    // * @throws LockingException
    // * Thrown in case of an optimistic locking error.
    // * @throws XmlSchemaValidationException
    // * Thrown if the schema validation of the provided data fails.
    // *
    // */
    // String updateStructuralMap(final String id, final String xmlData)
    // throws ContainerNotFoundException, LockingException,
    // XmlSchemaValidationException;
    //
    // //
    // // Subresource - structural map entry
    // //
    //
    // /**
    // * Creates an new entry in the structural map of the specified container,
    // * i.e. adds the object referenced by the entry to the container.<br/> The
    // * new entry is appended to the entries of the structural map.
    // *
    // * @param id
    // * The id of the resource.
    // * @param xmlData
    // * The XML representation of a structural map entry referencing
    // * an item or a container that shall be added to the structural
    // * map.
    // * @return TODO
    // * @throws ContainerNotFoundException
    // * Thrown if a container with the provided id cannot be found.
    // * @throws LockingException
    // * Thrown in case of an optimistic locking error.
    // * @throws ItemNotFoundException
    // * Thrown if the entry's data references an item that does not
    // * exist.
    // */
    // String createStructuralMapEntry(final String id, final String xmlData)
    // throws ContainerNotFoundException, LockingException,
    // ItemNotFoundException;
    //
    // /**
    // * Removes an entry from the structural map of the specified container,
    // i.e.
    // * removes the referenced object (container or item) from the container.
    // *
    // * @param id
    // * The id of the resource.
    // * @param entryId
    // * The id of the entry that shall be removed.
    // * @throws ContainerNotFoundException
    // * Thrown if a container with the provided id cannot be found.
    // * @throws LockingException
    // * Thrown in case of an optimistic locking error.
    // * @throws StructuralMapEntryNotFoundException
    // * Thrown if the structural map entry with the provided id
    // * cannot be found.
    // *
    // *
    // */
    // void deleteStructuralMapEntry(final String id, final String entryId)
    // throws ContainerNotFoundException, LockingException,
    // StructuralMapEntryNotFoundException;
    //
    // /**
    // * Deletes entries from the structural map of the specified container,
    // i.e.
    // * removes the referenced objects (containers or items) from the
    // container.
    // *
    // * @param id
    // * The id of the resource.
    // * @param xmlData
    // * The XML representation of a list of entries referencing items
    // * and containers that shall be removed from the structural map.
    // * @throws ContainerNotFoundException
    // * Thrown if a container with the provided id cannot be found.
    // * @throws LockingException
    // * Thrown in case of an optimistic locking error.
    // * @throws StructuralMapEntryNotFoundException
    // * Thrown if an structural map entry cannot be found.
    // *
    // *
    // */
    // void deleteStructuralMapEntries(final String id, final String xmlData)
    // throws ContainerNotFoundException, LockingException,
    // StructuralMapEntryNotFoundException;
    //
    // /**
    // * Inserts entries of the provided list into the structural map, i.e. adds
    // * the referenced objects to the container.
    // *
    // * @param id
    // * The id of the resource.
    // * @param xmlData
    // * The XML representation of a list of entries referencing items
    // * and containers that shall be inserted into the structural map.
    // * @param preceedingId
    // * The id of the object that shall preceed the new objects after
    // * the insert. If this value is not provided (or empty), the
    // * objects are appended to the members list.
    // * @return Returns the value of the updated subresource.
    // * @throws ContainerNotFoundException
    // * Thrown if a container with the provided id cannot be found.
    // * @throws LockingException
    // * Thrown in case of an optimistic locking error.
    // * @throws ItemNotFoundException
    // * Thrown if an item meber shall be removed that is not part of
    // * container.
    // *
    // *
    // */
    // String insertStructuralMapEntries(final String id,
    // final String xmlData,
    // final String preceedingId)
    // throws ContainerNotFoundException, LockingException,
    // ItemNotFoundException;

    /**
     * Release a Container.
     *
     * @param id        The id of the Container.
     * @param taskParam The timestamp of the last modification of the Container. Necessary for optimistic locking
     *                  purpose.
     * @return last-modification-date within XML (result.xsd)
     * @throws ContainerNotFoundException Thrown if a Container with the specified id cannot be found.
     * @throws LockingException           TODO
     * @throws MissingMethodParameterException
     *                                    Thrown if a method parameter is missing
     * @throws SystemException            Thrown if a framework internal error occurs.
     * @throws ReadonlyVersionException   TODO
     * @throws InvalidXmlException        TODO
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException
     * @throws de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException
     */
    String release(final String id, final String taskParam) throws ContainerNotFoundException, LockingException,
        MissingMethodParameterException, InvalidStatusException, SystemException, OptimisticLockingException,
        ReadonlyVersionException, InvalidXmlException;

    /**
     * Submit a Container.
     *
     * @param id        The id of the Container.
     * @param taskParam The timestamp of the last modification of the Container. Necessary for optimistic locking
     *                  purpose.
     * @return last-modification-date within XML (result.xsd)
     * @throws ContainerNotFoundException Thrown if a Container with the specified id cannot be found.
     * @throws LockingException           TODO
     * @throws MissingMethodParameterException
     *                                    Thrown if a method parameter is missing
     * @throws SystemException            Thrown if a framework internal error occurs.
     * @throws ReadonlyVersionException   TODO
     * @throws InvalidXmlException        TODO
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException
     * @throws de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException
     */
    String submit(final String id, final String taskParam) throws ContainerNotFoundException, LockingException,
        MissingMethodParameterException, InvalidStatusException, SystemException, OptimisticLockingException,
        ReadonlyVersionException, InvalidXmlException;

    /**
     * Set an Container in revision.
     *
     * @param id The id of the Container.
     * @param param
     * @return last-modification-date within XML (result.xsd)
     * @throws MissingMethodParameterException
     *                               Thrown if a method parameter is missing
     * @throws SystemException       Thrown if a framework internal error occurs.
     * @throws XmlCorruptedException TODO
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.violated.LockingException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException
     * @throws de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException
     */
    String revise(final String id, final String param) throws ContainerNotFoundException, LockingException,
        MissingMethodParameterException, InvalidStatusException, SystemException, OptimisticLockingException,
        ReadonlyVersionException, XmlCorruptedException;

    /**
     * Withdraw a Container.
     *
     * @param id        The id of the Container.
     * @param taskParam The timestamp of the last modification of the Container. Necessary for optimistic locking
     *                  purpose.
     * @return last-modification-date within XML (result.xsd)
     * @throws ContainerNotFoundException Thrown if a Container with the specified id cannot be found.
     * @throws LockingException           Thrown if Container is locked
     * @throws MissingMethodParameterException
     *                                    Thrown if a method parameter is missing
     * @throws InvalidStatusException     Thrown if Container has invalid status for withdraw
     * @throws SystemException            Thrown if a framework internal error occurs.
     * @throws AlreadyWithdrawnException  TODO
     * @throws ReadonlyVersionException   TODO
     * @throws InvalidXmlException        TODO
     * @throws de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException
     */
    String withdraw(final String id, final String taskParam) throws ContainerNotFoundException, LockingException,
        MissingMethodParameterException, InvalidStatusException, SystemException, OptimisticLockingException,
        AlreadyWithdrawnException, ReadonlyVersionException, InvalidXmlException;

    /**
     * The Container will be locked for changes until the lockOwner (the current user) or an Administrator unlocks the
     * Container.
     *
     * @param id        The id of the Container.
     * @param taskParam The task parameter structure.
     * @return last-modification-date within XML (result.xsd)
     * @throws ContainerNotFoundException Thrown if the Container was not found.
     * @throws LockingException           Thrown if the Container could not been locked.
     * @throws MissingMethodParameterException
     *                                    Thrown if a method parameter is missing
     * @throws SystemException            Thrown if a framework internal error occurs.
     * @throws InvalidXmlException        TODO
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException
     * @throws de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException
     */
    String lock(final String id, final String taskParam) throws ContainerNotFoundException, LockingException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidStatusException,
        InvalidXmlException;

    /**
     * The Container will be unlocked. The lockOwner or an Administrator may unlock the Container.
     *
     * @param id        The id of the Container.
     * @param taskParam The task parameter structure.
     * @return last-modification-date within XML (result.xsd)
     * @throws ContainerNotFoundException Thrown if the Container was not found.
     * @throws LockingException           Thrown if the Container could not been unlocked.
     * @throws MissingMethodParameterException
     *                                    Thrown if a method parameter is missing
     * @throws SystemException            Thrown if a framework internal error occurs.
     * @throws InvalidXmlException        TODO
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException
     * @throws de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException
     */
    String unlock(final String id, final String taskParam) throws ContainerNotFoundException, LockingException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidStatusException,
        InvalidXmlException;

    /**
     * @param containerId
     * @param taskParam
     * @return The XML representation of the Container.
     * @throws MissingMethodParameterException
     *          Thrown if a method parameter is missing
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.violated.LockingException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     */
    String moveToContext(final String containerId, final String taskParam) throws ContainerNotFoundException,
        ContextNotFoundException, InvalidContentException, LockingException, MissingMethodParameterException;

    /**
     * @param containerId
     * @param xmlData
     * @throws MissingMethodParameterException
     *                         Thrown if a method parameter is missing
     * @throws SystemException Thrown if a framework internal error occurs.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingContentException
     * @throws de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     * @throws de.escidoc.core.common.exceptions.application.violated.LockingException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingElementValueException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContextException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException
     * @throws de.escidoc.core.common.exceptions.application.security.AuthorizationException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     * @throws de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException
     * @return
     */
    String createItem(final String containerId, final String xmlData) throws ContainerNotFoundException,
        MissingContentException, ContextNotFoundException, ContentModelNotFoundException,
        ReadonlyElementViolationException, MissingAttributeValueException, MissingElementValueException,
        ReadonlyAttributeViolationException, MissingMethodParameterException, InvalidXmlException,
        FileNotFoundException, LockingException, InvalidContentException, InvalidContextException,
        RelationPredicateNotFoundException, ReferencedResourceNotFoundException, MissingMdRecordException,
        SystemException, InvalidStatusException, AuthorizationException;

    /**
     * @param containerId
     * @param xmlData
     * @throws MissingMethodParameterException
     *                         Thrown if a method parameter is missing
     * @throws SystemException Thrown if a framework internal error occurs.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException
     * @throws de.escidoc.core.common.exceptions.application.security.AuthenticationException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     * @throws de.escidoc.core.common.exceptions.application.violated.LockingException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingElementValueException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContextException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException
     * @throws de.escidoc.core.common.exceptions.application.security.AuthorizationException
     * @throws de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     * @return
     */
    String createContainer(final String containerId, final String xmlData) throws MissingMethodParameterException,
        ContainerNotFoundException, LockingException, ContextNotFoundException, ContentModelNotFoundException,
        InvalidContentException, InvalidXmlException, MissingAttributeValueException, MissingElementValueException,
        AuthenticationException, AuthorizationException, InvalidContextException, RelationPredicateNotFoundException,
        InvalidStatusException, ReferencedResourceNotFoundException, MissingMdRecordException, SystemException;

    // String retrieveVersions(final String id) throws
    // ContainerNotFoundException;

    /**
     * @param id
     * @param param
     * @return last-modification-date within XML (result.xsd)
     * @throws SystemException Thrown if a framework internal error occurs.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.violated.LockingException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingElementValueException
     * @throws de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException
     * @throws de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException
     * @throws de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     */
    String addContentRelations(final String id, final String param) throws SystemException, ContainerNotFoundException,
        OptimisticLockingException, ReferencedResourceNotFoundException, RelationPredicateNotFoundException,
        AlreadyExistsException, InvalidStatusException, InvalidXmlException, MissingElementValueException,
        LockingException, ReadonlyVersionException, InvalidContentException;

    /**
     * @param id
     * @param param
     * @return last-modification-date within XML (result.xsd)
     * @throws SystemException Thrown if a framework internal error occurs.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.violated.LockingException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingElementValueException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException
     * @throws de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException
     */
    String removeContentRelations(final String id, final String param) throws SystemException,
        ContainerNotFoundException, OptimisticLockingException, InvalidStatusException, MissingElementValueException,
        InvalidXmlException, ContentRelationNotFoundException, LockingException, ReadonlyVersionException;

    /**
     * Assigns persistent identifier to container object. The identfier will be created through the method and assigned
     * at the resolver.
     *
     * @param id    id of the container (without version part)
     * @param param XML snippet with dataset for persistent identifier resolver. {@code  <param}
     *              last-modification-date=""> <url>http://ResourceLocation</url> </param> </code>
     * @return last-modification-date within XML (result.xsd)
     * @throws InvalidStatusException     Thrown if a container in the current status is not allowed to to assign a new
     *                                    persistent identifier.
     * @throws ContainerNotFoundException Thrown if the Container with the provided id was not found.
     * @throws LockingException           Thrown if the container is locked and the current user is not the one who
     *                                    locked it.
     * @throws MissingMethodParameterException
     *                                    Thrown if parameter data contains not the requiered data.
     * @throws OptimisticLockingException OptimisticLockingException If the provided latest-modification-date does not
     *                                    match.
     * @throws SystemException            Thrown if a framework internal error occurs.
     * @throws InvalidXmlException        TODO
     */
    String assignObjectPid(final String id, final String param) throws InvalidStatusException,
        ContainerNotFoundException, LockingException, MissingMethodParameterException, OptimisticLockingException,
        SystemException, InvalidXmlException;

    /**
     * Assign a Persistent Identifier (PID) to a defined version of a Container.
     * <p/>
     * The method creates an identifier and write it to the persitent identifier resolver.
     *
     * @param id    id with version information of the container
     * @param param XML snippet with dataset for persistent identifier resolver. {@code  <param}
     *              last-modification-date=""> <url>http://ResourceLocation</url> </param> </code>
     * @return last-modification-date within XML (result.xsd)
     * @throws ContainerNotFoundException Thrown if the Container with the provided id was not found.
     * @throws LockingException           Thrown if the container is locked and the current user is not the one who
     *                                    locked it.
     * @throws MissingMethodParameterException
     *                                    Thrown if parameter data contains not the requiered data.
     * @throws SystemException            Thrown if a framework internal error occurs.
     * @throws OptimisticLockingException OptimisticLockingException If the provided latest-modification-date does not
     *                                    match.
     * @throws InvalidStatusException     Thrown if a container in the current status is not allowed to to assign a new
     *                                    persistent identifier.
     * @throws ReadonlyVersionException   Thrown if a provided container version id is not a latest version.
     * @throws XmlCorruptedException      TODO
     */
    String assignVersionPid(final String id, final String param) throws ContainerNotFoundException, LockingException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidStatusException,
        XmlCorruptedException, ReadonlyVersionException;

}
