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

import java.util.Map;

import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.business.interfaces.IngestableResource;
import de.escidoc.core.common.exceptions.application.invalid.ContextNotEmptyException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.notfound.AdminDescriptorNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OperationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.ContextNameNotUniqueException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Interface of a context handler of the business layer.
 * 
 * @author TTE
 * 
 * @om
 */
public interface ContextHandlerInterface extends IngestableResource {

    /**
     * Retrieves a filtered list of contexts.
     * 
     * @param filter
     *            XML snippet of filter.
     * @return Returns XML representation of the list of context objects.
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     * @throws InvalidXmlException
     *             Thrown in case of provided invalid XML data (corrupted data,
     *             schema validation failed, missing mandatory element or
     *             attribute values).
     * @throws SystemException
     *             If case of internal error.
     * @om
     */
    String retrieveContexts(final String filter)
        throws InvalidSearchQueryException, InvalidXmlException,
        SystemException;

    /**
     * Retrieves a filtered list of contexts.
     * 
     * @param filter
     *            map of key - value pairs describing the filter
     * 
     * @return Returns XML representation of the list of context objects.
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     * @throws InvalidXmlException
     *             Thrown in case of provided invalid XML data (corrupted data,
     *             schema validation failed, missing mandatory element or
     *             attribute values).
     * @throws SystemException
     *             If case of internal error.
     * @om
     */
    String retrieveContexts(final Map<String, String[]> filter)
        throws InvalidSearchQueryException, InvalidXmlException,
        SystemException;

    /**
     * Creates a resource with the provided data.
     * 
     * @param xmlData
     *            The data of the resource.
     * @return Returns the XML representation of the created resource, now
     *         containing the id by which the resource can be identified in the
     *         system.
     * @throws ContentModelNotFoundException
     *             Thrown if content type could not be found.
     * @throws ContextNameNotUniqueException
     *             If the name of the Context is not unique.
     * @throws InvalidContentException
     *             Thrown if Content is invalid.
     * @throws MissingAttributeValueException
     *             Thrown if attributes are missing.
     * @throws MissingElementValueException
     *             Thrown if elements are missing.
     * @throws ReadonlyAttributeViolationException
     *             Thrown if read-only attributes are altered.
     * @throws ReadonlyElementViolationException
     *             Thrown if read-only elements are altered.
     * @throws SystemException
     *             If case of internal error.
     * @throws OrganizationalUnitNotFoundException
     *             Thrown if organizational unit(s) of context could not be
     *             found.
     * @throws InvalidStatusException
     *             Thrown if an organizational unit is in an invalid status.
     * @om
     */
    String create(String xmlData) throws ContentModelNotFoundException,
        ContextNameNotUniqueException, InvalidContentException,
        MissingAttributeValueException, MissingElementValueException,
        ReadonlyAttributeViolationException, ReadonlyElementViolationException,
        SystemException, OrganizationalUnitNotFoundException,
        InvalidStatusException;

    /**
     * Deletes the specified resource.
     * 
     * @param id
     *            The id of the resource.
     * @throws ContextNotEmptyException
     *             If the Context containes depending resources.
     * @throws ContextNotFoundException
     *             Thrown if a context with the provided id cannot be found.
     * @throws InvalidStatusException
     *             Thrown if Context is in invalid status.
     * @throws SystemException
     *             If case of internal error.
     * @om
     */
    void delete(String id) throws ContextNotEmptyException,
        ContextNotFoundException, InvalidStatusException, SystemException;

    /**
     * Retrieves the specified resource.
     * 
     * @param id
     *            The id of the resource.
     * @return Returns the XML representation of the resource.
     * @throws ContextNotFoundException
     *             Thrown if a context with the provided id cannot be found.
     * @throws SystemException
     *             If case of internal error.
     * @om
     */
    String retrieve(String id) throws ContextNotFoundException, SystemException;

    /**
     * Retrieves the properites of context.
     * 
     * @param id
     *            The id of the context.
     * @return Returns the properties of context.
     * @throws ContextNotFoundException
     *             Thrown if a context with the provided id cannot be found.
     * @throws SystemException
     *             If case of internal error.
     * @om
     */
    String retrieveProperties(final String id) throws ContextNotFoundException,
        SystemException;

    /**
     * Updates the specified resource with the provided data.
     * 
     * @param id
     *            The id of the resource.
     * @param xmlData
     *            The new data of the resource.
     * @return Returns the XML representation of the updated resource.
     * @throws ContextNotFoundException
     *             Thrown if a context with the provided id cannot be found.
     * @throws InvalidStatusException
     *             Thrown if Context has invalid status to update.
     * @throws InvalidContentException
     *             TODO
     * @throws OptimisticLockingException
     *             Thrown if Context was altered by third on update.
     * @throws ReadonlyAttributeViolationException
     *             Thrown if read-only attributes are altered.
     * @throws ReadonlyElementViolationException
     *             Thrown if read-only elements are altered.
     * @throws ContextNameNotUniqueException
     *             Thrown if new name of context is not unique.
     * @throws MissingElementValueException
     *             Thrown if value of element is missing.
     * @throws SystemException
     *             If case of internal error.
     * @om
     */
    String update(String id, String xmlData) throws ContextNotFoundException,
        InvalidStatusException, OptimisticLockingException,
        ReadonlyAttributeViolationException, ReadonlyElementViolationException,
        ContextNameNotUniqueException, MissingElementValueException,
        SystemException, InvalidContentException;

    /**
     * Retrieves the subresource members.<br>
     * This subresource represents the items/containers related to the context
     * resource.
     * 
     * @param id
     *            The id of the resource.
     * @param filter
     *            XML snippet of filter.
     * @return Returns the value of the subresource.
     * @throws ContextNotFoundException
     *             Thrown if a context with the provided id cannot be found.
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     * @throws InvalidXmlException
     *             Thrown in case of provided invalid XML data (corrupted data,
     *             schema validation failed, missing mandatory element or
     *             attribute values).
     * @throws SystemException
     *             If case of internal error.
     * @om
     */
    String retrieveMembers(final String id, final String filter)
        throws ContextNotFoundException, InvalidSearchQueryException,
        InvalidXmlException, SystemException;

    /**
     * Retrieves the subresource members.<br>
     * This subresource represents the items/containers related to the context
     * resource.
     * 
     * @param id
     *            The id of the resource.
     * @param filter
     *            map of key - value pairs describing the filter
     * 
     * @return Returns the value of the subresource.
     * @throws ContextNotFoundException
     *             Thrown if a context with the provided id cannot be found.
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     * @throws InvalidXmlException
     *             Thrown in case of provided invalid XML data (corrupted data,
     *             schema validation failed, missing mandatory element or
     *             attribute values).
     * @throws SystemException
     *             If case of internal error.
     * @om
     */
    String retrieveMembers(final String id, final Map<String, String[]> filter)
        throws ContextNotFoundException, InvalidSearchQueryException,
        InvalidXmlException, SystemException;

    //
    // Subresource - admin descriptor
    //

    /**
     * Retrieves one, by unique name selected, admin-descriptor from Context.
     * 
     * @param id
     *            The id of the Context.
     * @param name
     *            The name of the admin-descriptor (unique).
     * @return The XML representation of the selected admin-descriptor.
     * @throws ContextNotFoundException
     *             Thrown if a context with the provided id cannot be found.
     * @throws AdminDescriptorNotFoundException
     *             Thrown if the selected admin-descriptor could not be found.
     * @throws SystemException
     *             If case of internal error.
     * @om
     */
    String retrieveAdminDescriptor(final String id, final String name)
        throws AdminDescriptorNotFoundException, ContextNotFoundException,
        SystemException;

    /**
     * Retrieves all admin-descriptors from Context.
     * 
     * @param id
     *            The id of the Context.
     * @return the XML representation of all admin-descriptors of the Context.
     * @throws ContextNotFoundException
     *             Thrown if a context with the provided id cannot be found.
     * @throws SystemException
     *             If case of internal error.
     * @om
     */
    String retrieveAdminDescriptors(final String id)
        throws ContextNotFoundException, SystemException;

    /**
     * Updates the subresource admin descriptor.
     * 
     * @param id
     *            The id of the resource.
     * @param xmlData
     *            The new value of the subresource.
     * @return Returns the value of the updated subresource.
     * @throws AdminDescriptorNotFoundException
     *             Thrown if admin descriptor could not be found.
     * @throws ContextNotFoundException
     *             Thrown if a context with the provided id cannot be found.
     * @throws OptimisticLockingException
     *             Thrown if admin descriptor was altered on update.
     * @om
     */
    String updateAdminDescriptor(final String id, final String xmlData)
        throws AdminDescriptorNotFoundException, ContextNotFoundException,
        OptimisticLockingException;

    //
    // Subresource - resources
    //

    /**
     * Retrieve a virtual resource by name.
     * 
     * @param id
     *            context id
     * @param resourceName
     *            name of the virtual resource
     * @param parameters
     *            query parameters
     * 
     * @return virtual resource as XML representation
     * @throws OperationNotFoundException
     *             thrown if there is no method configured for the given
     *             resource name
     * @throws ContextNotFoundException
     *             Thrown if a context with the provided id cannot be found.
     * @throws SystemException
     *             If an internal error occurred.
     */
    EscidocBinaryContent retrieveResource(
        final String id, final String resourceName, final Map< ? , ? > parameters)
        throws ContextNotFoundException, OperationNotFoundException,
        SystemException;

    /**
     * Retrieves the subresource resources.
     * 
     * @param id
     *            The id of the resource.
     * @return Returns the value of the subresource.
     * @throws ContextNotFoundException
     *             Thrown if a context with the provided id cannot be found.
     * @throws SystemException
     *             If case of internal error.
     * @om
     */
    String retrieveResources(final String id) throws ContextNotFoundException,
        SystemException;

    /**
     * Set Context status to open.
     * 
     * @param id
     *            The id of the resource.
     * @param taskParam
     *            The status parameter.
     * @return XML corresponding to (result.xsd) with last-modification-date.
     * 
     * @throws ContextNotFoundException
     *             Thrown if a context with the provided id cannot be found.
     * @throws InvalidStatusException
     *             Thrown if Context is in invalid status.
     * @throws InvalidXmlException
     *             Thrown if if XML is invalid.
     * @throws LockingException
     *             TODO
     * @throws StreamNotFoundException
     *             TODO
     * @throws OptimisticLockingException
     *             Thrown if Context was altered by third on update.
     * @throws SystemException
     *             If case of internal error.
     */
    String open(final String id, final String taskParam)
        throws ContextNotFoundException, InvalidStatusException,
        InvalidXmlException, OptimisticLockingException, SystemException,
        LockingException, StreamNotFoundException;

    /**
     * Set Context status to close.
     * 
     * @param id
     *            The id of the resource.
     * @param taskParam
     *            The status parameter.
     * @return XML corresponding to (result.xsd) with last-modification-date.
     * 
     * @throws ContextNotFoundException
     *             Thrown if a context with the provided id cannot be found.
     * @throws InvalidStatusException
     *             Thrown if Context is in invalid status.
     * @throws InvalidXmlException
     *             Thrown if if XML is invalid.
     * @throws LockingException
     *             TODO
     * @throws StreamNotFoundException
     *             TODO
     * @throws OptimisticLockingException
     *             Thrown if Context was altered by third on update.
     * @throws SystemException
     *             If case of internal error.
     */
    String close(final String id, final String taskParam)
        throws ContextNotFoundException, InvalidStatusException,
        InvalidXmlException, OptimisticLockingException, SystemException,
        LockingException, StreamNotFoundException;
}
