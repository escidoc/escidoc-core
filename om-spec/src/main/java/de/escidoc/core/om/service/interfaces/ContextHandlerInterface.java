/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.om.service.interfaces;

import de.escidoc.core.common.annotation.Validate;
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

import java.util.Map;

import org.esidoc.core.utils.io.EscidocBinaryContent;

/**
 * Interface of a Context handler.
 *
 * @author Torsten Tetteroo
 */
public interface ContextHandlerInterface {

    /**
     * Retrieves a list of Contexts applying filters.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * If no filter is specified, all Contexts are retrieved. But the input must contain the xml stream
     * <b>&lt;param>&lt;/param></b><br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>Check whether all filter criteria names are valid.</li> <li>The Contexts are accessed
     * using the provided filters.</li> <li>The XML representations of the list of all Contexts corresponding to
     * XML-schema is returned as output.</li> </ul> <br/> <b>Filters:</b><br/> <ul> <li>Each filter criteria can be used
     * once</li> <li>The filter criteria are locally connected with "AND"</li> <li>All filter criteria accept one value
     * except: <li>filter criteria "items" which needs a list of id-elements</li> </ul> <br/> See chapter "Filters" for
     * detailed information about filter definitions.
     * <p/>
     * After creating the filtered list of objects the AA component filters this list again in respect to the role of
     * the requesting user. This final filtering is not done in the related "Refs"-method which returns a list of
     * references instead of complete objects. There you will get the complete list.
     *
     * @param filter map of key - value pairs containing the filter definition. See functional specification.
     * @return The XML representation of the list of the retrieved contexts corresponding to the SRW schema.
     * @throws MissingMethodParameterException
     *                         Thrown if filter parameter contains not expected data.
     * @throws SystemException Thrown if a framework internal error occurs.
     */
    String retrieveContexts(final Map<String, String[]> filter) throws MissingMethodParameterException, SystemException;

    /**
     * Creates a Context with the provided data.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * The Context may not exist<br/>
     * <p/>
     * See chapter 4 for detailed information about input and output data elements.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The XML data is validated</li> <li>The "name" of the Context must be unique: check if
     * this is true.</li> <li>public-status of the Context is set to "created".</li> <li>The XML representation of the
     * Context corresponding to XML-schema is returned as output.</li> </ul>
     *
     * @param xmlData The XML representation of the Context to be created corresponding to XML-schema "context.xsd".
     * @return The XML representation of the created Context corresponding to XML-schema "context.xsd".
     * @throws AuthenticationException        Thrown if authentication fails.
     * @throws AuthorizationException         Thrown if authorization fails.
     * @throws MissingMethodParameterException
     *                                        Thrown if filter parameter contains not expected data.
     * @throws SystemException                Thrown if a framework internal error occurs.
     * @throws ContextNameNotUniqueException  Thrown if name of to create Context object is not unique.
     * @throws ContentModelNotFoundException  Thrown if content type could not be found.
     * @throws InvalidContentException        Thrown if Content is invalid.
     * @throws MissingAttributeValueException Thrown if attributes are missing.
     * @throws MissingElementValueException   Thrown if elements are missing.
     * @throws ReadonlyAttributeViolationException
     *                                        Thrown if read-only attributes are altered.
     * @throws ReadonlyElementViolationException
     *                                        Thrown if read-only elements are altered.
     * @throws OrganizationalUnitNotFoundException
     *                                        Thrown if organizational unit(s) of Context could not be found.
     * @throws InvalidStatusException         Thrown if an organizational unit is in an invalid status.
     * @throws XmlCorruptedException          Thrown if the schema validation of the provided data failed.
     * @throws XmlSchemaValidationException   Thrown if the schema validation of the provided data failed.
     */
    @Validate(param = 0, resolver = "getContextSchemaLocation")
    String create(final String xmlData) throws AuthenticationException, AuthorizationException,
        ContentModelNotFoundException, ContextNameNotUniqueException, InvalidContentException,
        MissingAttributeValueException, MissingElementValueException, MissingMethodParameterException,
        ReadonlyAttributeViolationException, ReadonlyElementViolationException, SystemException,
        OrganizationalUnitNotFoundException, InvalidStatusException, XmlCorruptedException,
        XmlSchemaValidationException;

    /**
     * Deletes the specified context.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The Context must exist<br/>
     * <p/>
     * The public-status is "open".<br/>
     * <p/>
     * The Context has to be in public-status "created", otherwise the removing of the Context will fail.
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Context is accessed using the provided reference.</li> <li>The content will be
     * removed from IR.</li> <li>No data is returned.</li> </ul>
     *
     * @param id The id of the resource Context to be deleted.
     * @throws AuthenticationException  Thrown if authentication fails.
     * @throws AuthorizationException   Thrown if authorization fails.
     * @throws ContextNotEmptyException If the Context contains depending resources.
     * @throws ContextNotFoundException Thrown if a Context with the provided id cannot be found.
     * @throws InvalidStatusException   Thrown if Context is in invalid status.
     * @throws MissingMethodParameterException
     *                                  Thrown if method parameter is missing.
     * @throws SystemException          If case of internal error.
     */
    void delete(String id) throws AuthenticationException, AuthorizationException, ContextNotEmptyException,
        ContextNotFoundException, InvalidStatusException, MissingMethodParameterException, SystemException;

    /**
     * Retrieves the specified context.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The Context must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Context is accessed using the provided reference.</li> <li>The XML representation
     * of the Context corresponding to XML-schema is returned as output.</li> </ul>
     *
     * @param id The id of the Context to be retrieved.
     * @return The XML representation of the retrieved Context corresponding to XML-schema "context.xsd".
     * @throws AuthenticationException  Thrown if authentication fails.
     * @throws AuthorizationException   Thrown if authorization fails.
     * @throws MissingMethodParameterException
     *                                  Thrown if method parameter is missing.
     * @throws ContextNotFoundException Thrown if a Context with the provided id cannot be found.
     * @throws SystemException          If case of internal error.
     */
    String retrieve(String id) throws AuthenticationException, AuthorizationException, ContextNotFoundException,
        MissingMethodParameterException, SystemException;

    /**
     * Retrieve the properties of a context.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The Context must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Context is accessed using the provided reference.</li> <li>The XML representation
     * of the Context properties corresponding to XML-schema is returned as output.</li> </ul>
     *
     * @param id The id of the Context to be retrieved.
     * @return The XML representation of the retrieved Context properties corresponding to XML-schema "context.xsd".
     * @throws ContextNotFoundException Thrown if a Context with the provided id cannot be found.
     * @throws SystemException          If case of internal error.
     */
    String retrieveProperties(final String id) throws ContextNotFoundException, SystemException;

    /**
     * Update the specified Context with the provided data.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * The Context must exist<br/>
     * <p/>
     * The public-status is not "closed"<br/>
     * <p/>
     * See chapter 4 for detailed information about input and output data elements.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Context is accessed using the provided reference.</li> <li>Optimistic Locking
     * criteria is checked.</li> <li>The XML data is validated.</li> <li>The XML representation of the Context
     * corresponding to XML-schema is returned as output.</li> </ul>
     * <p/>
     * <b>Note:</b> The public-status of an Context can't bet changed by this method. Please use the methods dedicated
     * for this purpose.<br/>
     *
     * @param id      The id of the Context to be updated.
     * @param xmlData The XML representation of the Context to be updated corresponding to XML-schema "context.xsd".
     * @return The XML representation of the updated Context corresponding to XML-schema "context.xsd".
     * @throws AuthenticationException       Thrown if authentication fails.
     * @throws AuthorizationException        Thrown if authorization fails.
     * @throws ContextNotFoundException      Thrown if a Context with the provided id cannot be found.
     * @throws MissingMethodParameterException
     *                                       Thrown if method parameter is missing.
     * @throws InvalidContentException       Thrown if content of XML representation is invalid.
     * @throws InvalidStatusException        Thrown if Context status is not allowed to update resource.
     * @throws InvalidXmlException           Thrown if if XML is invalid.
     * @throws OptimisticLockingException    Thrown if Context was altered by third on update.
     * @throws ReadonlyAttributeViolationException
     *                                       Thrown if read-only attributes are altered.
     * @throws ReadonlyElementViolationException
     *                                       Thrown if read-only elements are altered.
     * @throws ContextNameNotUniqueException Thrown if new name of Context is not unique.
     * @throws MissingElementValueException  Thrown if element value is missing.
     * @throws SystemException               If case of internal error.
     */
    @Validate(param = 1, resolver = "getContextSchemaLocation")
    String update(String id, String xmlData) throws AuthenticationException, AuthorizationException,
        ContextNotFoundException, InvalidContentException, InvalidStatusException, InvalidXmlException,
        MissingMethodParameterException, OptimisticLockingException, ReadonlyAttributeViolationException,
        ReadonlyElementViolationException, ContextNameNotUniqueException, MissingElementValueException, SystemException;

    /**
     * Retrieves a list of members of a Context applying filters.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * The Context must exist<br/>
     * <p/>
     * The public-status is "open".<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Context is accessed using the provided reference.</li> <li>Check whether all
     * filter criteria names are valid.</li> <li>The members are accessed using the provided filters.</li> <li>The XML
     * representations of the list of all members corresponding to XML-schema is returned as output.</li> </ul> <br/>
     * See chapter "Filters" for detailed information about filter definitions.
     *
     * @param id     The id of the context.
     * @param filter map of key - value pairs containing the filter definition. See functional specification.
     * @return The XML representation of the list of member corresponding to the SRW schema.
     * @throws ContextNotFoundException Thrown if a Context with the provided id cannot be found.
     * @throws MissingMethodParameterException
     *                                  Thrown if method parameter is missing.
     * @throws SystemException          If case of internal error.
     */
    String retrieveMembers(final String id, final Map<String, String[]> filter) throws ContextNotFoundException,
        MissingMethodParameterException, SystemException;

    //
    // Subresource - admin descriptor
    //

    /**
     * Retrieve the subresource admin-descriptor of a context.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The Context must exist<br/>
     * <p/>
     * The public-status is "open".<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Context is accessed using the provided reference.</li> <li>The XML representation
     * of the admin descriptor is returned as output. </li> </ul>
     *
     * @param id   The id of the context.
     * @param name The name of the admin-descriptor.
     * @return The XML representation of the retrieved admin descriptor.
     * @throws AuthenticationException  Thrown if authentication fails.
     * @throws AuthorizationException   Thrown if authorization fails.
     * @throws ContextNotFoundException Thrown if a Context with the provided id cannot be found.
     * @throws MissingMethodParameterException
     *                                  Thrown if method parameter is missing.
     * @throws AdminDescriptorNotFoundException
     *                                  Thrown if admin descriptor could not be found.
     * @throws SystemException          If case of internal error.
     */
    String retrieveAdminDescriptor(final String id, final String name) throws AdminDescriptorNotFoundException,
        AuthenticationException, AuthorizationException, ContextNotFoundException, MissingMethodParameterException,
        SystemException;

    /**
     * Retrieve the subresource admin-descriptors of a Context.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The Context must exist.<br/>
     * <p/>
     * The public-status is "open".<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Context is accessed using the provided reference.</li> <li>The XML representation
     * of all admin-descriptors of this Context is returned as output.</li> </ul>
     *
     * @param id The id of the context.
     * @return The XML representation of all admin-descriptors of this Context.
     * @throws AuthenticationException  Thrown if authentication fails.
     * @throws AuthorizationException   Thrown if authorization fails.
     * @throws ContextNotFoundException Thrown if a Context with the provided id cannot be found.
     * @throws MissingMethodParameterException
     *                                  Thrown if method parameter is missing.
     * @throws SystemException          If case of internal error.
     */
    String retrieveAdminDescriptors(final String id) throws AuthenticationException, AuthorizationException,
        ContextNotFoundException, MissingMethodParameterException, SystemException;

    /**
     * Update an admin descriptor of a context.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * The Context must exist<br/>
     * <p/>
     * The public-status is not "closed".<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Context is accessed using the provided reference.</li> <li>Optimistic Locking
     * criteria is checked.</li> <li>The XML representation of the admin descriptor is returned.</li> </ul>
     *
     * @param id      The id of the context.
     * @param xmlData The XML representation of the admin descriptor to be updated.
     * @return The XML representation of the updated admin descriptor.
     * @throws AuthenticationException    Thrown if authentication fails.
     * @throws AuthorizationException     Thrown if authorization fails.
     * @throws ContextNotFoundException   Thrown if a Context with the provided id cannot be found.
     * @throws InvalidXmlException        Thrown if if XML is invalid.
     * @throws MissingMethodParameterException
     *                                    Thrown if method parameter is missing.
     * @throws AdminDescriptorNotFoundException
     *                                    Thrown if admin descriptor could not be found.
     * @throws OptimisticLockingException Thrown if Context was altered by third on update.
     * @throws SystemException            If case of internal error.
     */
    @Validate(param = 1, resolver = "getContextSchemaLocation")
    String updateAdminDescriptor(final String id, final String xmlData) throws AdminDescriptorNotFoundException,
        AuthenticationException, AuthorizationException, ContextNotFoundException, InvalidXmlException,
        MissingMethodParameterException, OptimisticLockingException, SystemException;

    //
    // Subresource - resources
    //

    /**
     * Retrieve a virtual resource by name.
     *
     * @param id           Context id
     * @param resourceName name of the virtual resource
     * @param parameters   query parameters
     * @return virtual resource as XML representation
     * @throws OperationNotFoundException thrown if there is no method configured for the given resource name
     * @throws AuthenticationException    Thrown if authentication fails.
     * @throws AuthorizationException     Thrown if authorization fails.
     * @throws ContextNotFoundException   Thrown if a Context with the provided id cannot be found.
     * @throws MissingMethodParameterException
     *                                    Thrown if method parameter is missing.
     * @throws SystemException            If an internal error occurred.
     */
    EscidocBinaryContent retrieveResource(
        final String id, final String resourceName, final Map<String, String[]> parameters)
        throws AuthenticationException, AuthorizationException, ContextNotFoundException,
        MissingMethodParameterException, OperationNotFoundException, SystemException;

    /**
     * Retrieve the subresource "resources". <br/>
     * <p/>
     * This method returns a list of additional resources which aren't stored in IR but created on request.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The Context must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Context is accessed using the provided reference.</li> <li>Determine which
     * resources are available.</li> <li>Create the list of resources.</li> <li>The XML representation of the
     * organizational unit corresponding to XML-schema is returned as output.</li> </ul>
     *
     * @param id The id of the context.
     * @return The XML representation of the list of virtual Resources of the Context corresponding to XML-schema
     *         "resources.xsd".
     * @throws AuthenticationException  Thrown if authentication fails.
     * @throws AuthorizationException   Thrown if authorization fails.
     * @throws ContextNotFoundException Thrown if a Context with the provided id cannot be found.
     * @throws MissingMethodParameterException
     *                                  Thrown if method parameter is missing.
     * @throws SystemException          If case of internal error.
     */
    String retrieveResources(final String id) throws AuthenticationException, AuthorizationException,
        ContextNotFoundException, MissingMethodParameterException, SystemException;

    /**
     * Open a context<br/> Set Context status to open.<br/>
     * <p/>
     * New object can be added to the open Context.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The Context must exist<br/>
     * <p/>
     * The public-status is "created".<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Context is accessed using the provided reference.</li> <li>Optimistic Locking
     * criteria is checked.</li> <li>The public-status is changed to "open".</li> <li>No data is returned.</li> </ul>
     * <p/>
     * <pre>
     * &lt;param last-modification-date=&quot;1967-08-13T12:00:00.000+01:00&quot; /&gt;
     * </pre>
     *
     * @param id        The id of the resource Context to be opened.
     * @param taskParam The timestamp of the last modification of the Container. (see above)
     * @return XML corresponding to (result.xsd) with last-modification-date.
     * @throws AuthenticationException    Thrown if authentication fails.
     * @throws AuthorizationException     Thrown if authorization fails.
     * @throws ContextNotFoundException   Thrown if a Context with the provided id cannot be found.
     * @throws InvalidStatusException     Thrown if Context is in invalid status.
     * @throws InvalidXmlException        Thrown if if XML is invalid.
     * @throws MissingMethodParameterException
     *                                    Thrown if method parameter is missing.
     * @throws OptimisticLockingException Thrown if Context was altered by third on update.
     * @throws LockingException           Thrown if Context is looked.
     * @throws StreamNotFoundException    Thrown if required stream could not be found.
     * @throws SystemException            If case of internal error.
     */
    String open(final String id, final String taskParam) throws AuthenticationException, AuthorizationException,
        ContextNotFoundException, InvalidStatusException, InvalidXmlException, MissingMethodParameterException,
        OptimisticLockingException, SystemException, LockingException, StreamNotFoundException;

    /**
     * Close a context<br/> Set Context status to close.<br/>
     * <p/>
     * Adding of new objects is not allowed on closed context.
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The Context must exist<br/>
     * <p/>
     * The public-status is "open".<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Context is accessed using the provided reference.</li> <li>Optimistic Locking
     * criteria is checked.</li> <li>The public-status is changed to "closed".</li> <li>No data is returned.</li> </ul>
     * <p/>
     * <pre>
     * &lt;param last-modification-date=&quot;1967-08-13T12:00:00.000+01:00&quot; /&gt;
     * </pre>
     *
     * @param id        The id of the resource Context to be closed.
     * @param taskParam The timestamp of the last modification of the Container. (see above)
     * @return XML corresponding to (result.xsd) with last-modification-date.
     * @throws AuthenticationException    Thrown if authentication fails.
     * @throws AuthorizationException     Thrown if authorization fails.
     * @throws ContextNotFoundException   Thrown if a Context with the provided id cannot be found.
     * @throws InvalidStatusException     Thrown if Context is in invalid status.
     * @throws InvalidXmlException        Thrown if if XML is invalid.
     * @throws MissingMethodParameterException
     *                                    Thrown if method parameter is missing.
     * @throws OptimisticLockingException Thrown if Context was altered by third on update.
     * @throws LockingException           Thrown if Context is looked.
     * @throws StreamNotFoundException    Thrown if required stream could not be found.
     * @throws SystemException            If case of internal error.
     */
    String close(final String id, final String taskParam) throws AuthenticationException, AuthorizationException,
        ContextNotFoundException, InvalidStatusException, InvalidXmlException, MissingMethodParameterException,
        OptimisticLockingException, SystemException, LockingException, StreamNotFoundException;

}
