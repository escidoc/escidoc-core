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

package org.escidoc.core.business.om.interfaces;

import org.escidoc.core.business.domain.base.ID;
import org.escidoc.core.business.domain.om.item.ItemDO;

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
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.NotPublishedException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Interface of an Item handler.
 * 
 * @author Torsten Tetteroo
 */
public interface ItemHandlerInterface {

    /**
     * Create an Item.<br/>
     * <b>Prerequisites:</b><br/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * See chapter 4 for detailed information about input and output data elements<br/>
     * <b>Tasks:</b><br/>
     * <ul>
     * <li>The XML data is validated against the XML-Schema of an Item.</li>
     * <li>It's checked weather the context id is provided. In a REST case it's checked weather REST-URL of a context is
     * correct.</li>
     * <li>It's checked weather the content-model id is provided. In a REST case it's checked weather REST-URL of a
     * content-model is correct</li>
     * <li>It's checked weather the context to the provided id exists.</li>
     * <li>It's checked weather the content-model to the provided id exists.</li>
     * <li>If a provided Item representation is a surrogate Item representation (contains a property'origin') it's
     * checked weather the referenced origin Item version exists and is in a state 'released'. Then it is checked if the
     * creator has privileges to access the referenced origin Item version and if the origin Item itself is not a
     * surrogate Item.</li>
     * <li>If a "relations" section is set, it's checked weather resources to all provided relations targets exist and
     * whether used relations are part of the related ontology.</li>
     * <li>Linked files are downloaded <b> or </b>extracted if inline delivered and the Components are created.</li>
     * <li>The public-status of the Item is set to "pending".</li>
     * <li>The version 1 of the Item is created.</li>
     * <li>The XML input data is updated and some new data is added (see Chapter 4)</li>
     * <li>The XML representation of the Item corresponding to the XML schema is returned as output.</li>
     * </ul>
     * <p/>
     * The Persistent Identifier (PID) of a resource can be given with create. Later, one of the assignPid methods must
     * be used.<br/>
     * 
     * @param xmlData
     *            The XML representation of the Item to be created corresponding to XML schema "item.xsd".
     * @return The XML representation of the created Item corresponding to XML schema "item.xsd".
     * @throws ContextNotFoundException
     *             Thrown if the Context specified in the provided data cannot be found.
     * @throws ContentModelNotFoundException
     *             Thrown if the content type specified in the provided data cannot be found.
     * @throws ReferencedResourceNotFoundException
     *             If a resource referred from the provided data could not be found.
     * @throws AuthenticationException
     *             Thrown if the authentication fails.
     * @throws AuthorizationException
     *             Thrown if the authorization fails.
     * @throws XmlCorruptedException
     *             Thrown if provided data is corrupted.
     * @throws XmlSchemaValidationException
     *             Thrown if the schema validation of the provided data fails.
     * @throws MissingMethodParameterException
     *             If no data is provided.
     * @throws FileNotFoundException
     *             Thrown if a file cannot be found.
     * @throws InvalidContentException
     *             Thrown if the provided XML data is not valid for the creation of the resource.
     * @throws InvalidStatusException
     *             Thrown if the status of the specified Context is not valid for executing the action.
     * @throws MissingMdRecordException
     *             Thrown if the required metadata record (with name 'escidoc') is not provided.
     * @throws SystemException
     *             If an error occurs.
     * @throws MissingContentException
     *             If some mandatory content is missing.
     * @throws ReadonlyElementViolationException
     *             If a read-only element is set.
     * @throws MissingElementValueException
     *             If a mandatory element value is missing.
     * @throws ReadonlyAttributeViolationException
     *             If a read-only attribute is set.
     * @throws RelationPredicateNotFoundException
     *             If the predicate of a given relation is unknown.
     * @throws ReferencedResourceNotFoundException
     *             If a resource referred from the provided data could not be found.
     * @throws MissingAttributeValueException
     *             It a mandatory attribute value is missing.
     */
    ItemDO create(ItemDO itemDo) throws MissingContentException, ContextNotFoundException,
        ContentModelNotFoundException, ReadonlyElementViolationException, MissingElementValueException,
        ReadonlyAttributeViolationException, AuthenticationException, AuthorizationException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, FileNotFoundException, SystemException,
        InvalidContentException, ReferencedResourceNotFoundException, RelationPredicateNotFoundException,
        MissingAttributeValueException, MissingMdRecordException, InvalidStatusException;

    /**
     * Retrieve an Item.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * The Item must exist<br/>
     * If the Item is a surrogate Item, the user has to have privileges to access the origin Item, referenced by the
     * surrogate Item.<br/>
     * <b>Tasks:</b><br/>
     * <ul>
     * <li>The Item is accessed using the provided reference.</li>
     * <li>The XML representation to be returned for that Item will not contain any binary content but references to
     * them.</li>
     * <li>The XML representation of the Item corresponding to XML schema is returned as output.</li>
     * </ul>
     * <p/>
     * The binary content of an Item is not included but referenced from the Item representation.
     * 
     * @param id
     *            The id of the Item to be retrieved. In order to retrieve a specific version of an Item the id must be
     *            suffixed with a colon (':') and the version number.
     * @return The XML representation of the retrieved Item corresponding to XML-schema "item.xsd".
     * @throws ItemNotFoundException
     *             Thrown if an Item with the specified id cannot be found.
     * @throws ComponentNotFoundException
     *             Thrown if a Component of the Item cannot be found.
     * @throws AuthenticationException
     *             Thrown if the authentication fails.
     * @throws AuthorizationException
     *             Thrown if the authorization fails.
     * @throws MissingMethodParameterException
     *             If a mandatory element value is missing.
     * @throws SystemException
     *             If an error occurs.
     */
    ItemDO retrieve(ID id) throws ItemNotFoundException, ComponentNotFoundException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException;

    /**
     * Update an Item<br/>
     * <b>Prerequisites:</b> <br/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * The Item must exist.<br/>
     * The the public-status is not "withdrawn".<br/>
     * The Item is not locked by another user.<br/>
     * Optimistic Locking criteria of the Item is checked.<br/>
     * If the Item is a surrogate Item, the user has to have privileges to access the origin Item, referenced by the
     * surrogate Item.<br/>
     * Only the latest version can be used here.<br/>
     * See chapter 4 for detailed information about input and output data elements<br/>
     * <b>Tasks:</b><br/>
     * <ul>
     * <li>The XML data is validated against the XML-Schema of an Item.</li>
     * <li>Optimistic Locking criteria is checked.</li>
     * <li>It is checked whether used relations are part of the related ontologies.</li>
     * <li>A relations section contains a list of "relation" elements with existing relations data of the provided Item,
     * which should remain after update and a new relations data. The framework will remove all existing relations of
     * the provided Item, which are not on the list. It is checked, if provided relation targets and provided relation
     * predicates exist. The attribute "xlink:href" of the "relation" element is set to a REST-url and respectively the
     * attribute "objid" is set to id of the target. A target id may not contain a version number.</li>
     * <li>A Components section contains the Components of the provided Item, which should remain after update and new
     * Components. The framework will remove all existing Components of the specified Item, which are not in this
     * section inside the provided XML data.</li>
     * <li>If new Components are specified the linked files are downloaded <b> or </b>extracted if inline delivered this
     * data is used and the new Components are created.</li>
     * <li>For existing Components if new references are specified the linked files are downloaded <b> or </b>extracted
     * from the XML representation (inline delivered) and the Components are created.</li>
     * <p/>
     * <li>Differences between modifiable elements in the delivered XML data and the XML representation of the currently
     * stored Item are taken to modify the Item in the system.</li>
     * <p/>
     * <li>If the Item is modified a new version of the Item is created.</li>
     * <li>If the status of the latest version is "released" a new version is created and gets the status "pending"
     * otherwise a new version is created with the same version status as before. This also applies to the public-status
     * till it is once set to "released".</li>
     * <li>The XML input data is updated and some new data is added (see Chapter 4)</li>
     * <li>The XML representation of the Item corresponding to XML schema is returned as output.</li>
     * </ul>
     * 
     * @param id
     *            The id of the Item to be updated.
     * @param xmlData
     *            The XML representation of the Item to be updated corresponding to XML-schema "item.xsd".
     * @return The XML representation of the updated Item corresponding to XML-schema "item.xsd".
     * @throws ItemNotFoundException
     *             Thrown if an Item with the specified id cannot be found.
     * @throws FileNotFoundException
     *             Thrown if a file cannot be found.
     * @throws InvalidContextException
     *             Thrown if the content is invalid.
     * @throws InvalidStatusException
     *             Thrown in case of an invalid status.
     * @throws LockingException
     *             Thrown if the Item is locked and the current user is not the one who locked it.
     * @throws NotPublishedException
     *             Thrown if the status shall be changed to withdrawn but the Item has not been published.
     * @throws MissingLicenceException
     *             Thrown if the status shall be changed to published but a license is missing.
     * @throws ComponentNotFoundException
     *             Thrown if a Component specified in the XML data can not be found.
     * @throws AuthenticationException
     *             Thrown if the authentication fails.
     * @throws AuthorizationException
     *             Thrown if the authorization fails.
     * @throws InvalidXmlException
     *             If the provided data is not valid XML.
     * @throws MissingMethodParameterException
     *             If one of the parameters Item ID or data is not provided.
     * @throws MissingContentException
     *             If some mandatory content is missing.
     * @throws MissingAttributeValueException
     *             If a mandatory attribute value is missing.
     * @throws MissingMdRecordException
     *             Thrown if the required metadata record (with name 'escidoc') is not provided.
     * @throws InvalidContentException
     *             Thrown if the content is invalid.
     * @throws SystemException
     *             If an error occurs.
     * @throws OptimisticLockingException
     *             If the provided latest-modification-date does not match.
     * @throws ReadonlyViolationException
     *             If a read-only rule is violated.
     * @throws ReadonlyVersionException
     *             If the specified id is not the one of the latest version of the Item.
     * @throws AlreadyExistsException
     *             If a subresource to create already exists.
     * @throws ReferencedResourceNotFoundException
     *             If a resource referred from the provided data could not be found.
     * @throws RelationPredicateNotFoundException
     *             If the predicate of a given relation is unknown.
     */
    ItemDO update(ID id, ItemDO itemDo) throws ItemNotFoundException, FileNotFoundException,
        InvalidContextException, InvalidStatusException, LockingException, NotPublishedException,
        MissingLicenceException, ComponentNotFoundException, MissingContentException, MissingAttributeValueException,
        AuthenticationException, AuthorizationException, InvalidXmlException, MissingMethodParameterException,
        InvalidContentException, SystemException, OptimisticLockingException, AlreadyExistsException,
        ReadonlyViolationException, ReferencedResourceNotFoundException, RelationPredicateNotFoundException,
        ReadonlyVersionException, MissingMdRecordException;
}

