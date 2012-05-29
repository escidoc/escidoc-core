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
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.PidAlreadyAssignedException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Interface of a content relation handler of the business layer.
 *
 * @author Steffen Wagner
 */
public interface ContentRelationHandlerInterface extends IngestableResource {

    /**
     * Creates a resource with the provided data.
     *
     * @param xmlData The data of the resource.
     * @return Returns the XML representation of the created resource, now containing the id by which the resource can
     *         be identified in the system.
     * @throws MissingAttributeValueException Thrown if attribute value is missing
     * @throws MissingMethodParameterException
     *                                        Thrown if method parameter is missing
     * @throws InvalidContentException        Thrown if content is invalid
     * @throws InvalidXmlException            Thrown if XML is invalid
     * @throws ReferencedResourceNotFoundException
     *                                        Thrown if referenced resource does not exist.
     * @throws RelationPredicateNotFoundException
     *                                        Thrown if the predicate is not registered.
     * @throws SystemException                Thrown if internal error occurs.
     */

    String create(final String xmlData) throws MissingAttributeValueException, MissingMethodParameterException,
        InvalidXmlException, InvalidContentException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, SystemException;

    /**
     * Update Content Relation.
     *
     * @param id      objid of Content Relation
     * @param xmlData XML representation of Content Relation
     * @return XML representation of updated Content Relation
     * @throws ContentRelationNotFoundException
     *                                        Thrown if no Content Relation could be found under provided objid
     * @throws OptimisticLockingException     Thrown if resource is updated in the meantime and last modification date
     *                                        differs
     * @throws InvalidStatusException         Thrown if resource has invalid status to update
     * @throws MissingAttributeValueException Thrown if attribute value is missing
     * @throws LockingException               Thrown if resource is locked through other user
     * @throws MissingMethodParameterException
     *                                        Thrown if method parameter is missing
     * @throws ReferencedResourceNotFoundException
     *                                        Thrown if referenced resource does not exist.
     * @throws RelationPredicateNotFoundException
     *                                        Thrown if the predicate is not registered.
     * @throws InvalidContentException        Thrown if content is invalid
     * @throws InvalidXmlException            Thrown if XML is invalid
     * @throws SystemException                Thrown if internal error occur
     */
    String update(final String id, final String xmlData) throws ContentRelationNotFoundException,
        OptimisticLockingException, InvalidContentException, InvalidStatusException, LockingException,
        MissingAttributeValueException, MissingMethodParameterException, InvalidXmlException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, SystemException;

    /**
     * Delete Content Relation.
     *
     * @param id The objid of the Content Relation
     * @throws ContentRelationNotFoundException
     *                          Thrown if a content relation with the provided id cannot be found.
     * @throws LockingException Thrown if Content Relation is locked by other user
     * @throws SystemException  Thrown if internal error occurs.
     */
    void delete(final String id) throws ContentRelationNotFoundException, SystemException, LockingException;

    /**
     * Submit a resource with a provided id.
     * @param id
     * @param param
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException
     * @throws de.escidoc.core.common.exceptions.application.violated.LockingException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     * @throws de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     * @return
     */
    String submit(final String id, final String param) throws ContentRelationNotFoundException, LockingException,
        InvalidStatusException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidXmlException, InvalidContentException;

    /**
     * Release a resource with a provided id.
     * @param id
     * @param param
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException
     * @throws de.escidoc.core.common.exceptions.application.violated.LockingException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     * @throws de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     * @return
     */
    String release(final String id, final String param) throws ContentRelationNotFoundException, LockingException,
        InvalidStatusException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidXmlException, InvalidContentException;

    /**
     * Revise a resource with a provided id.
     * @param id
     * @param param
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException
     * @throws de.escidoc.core.common.exceptions.application.violated.LockingException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException
     * @throws de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     * @throws de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     * @return
     */
    String revise(final String id, final String param) throws ContentRelationNotFoundException, LockingException,
        InvalidStatusException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        XmlCorruptedException, InvalidContentException;

    /**
     * Lock a Content Relation for other user access.
     * @param id
     * @param param
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException
     * @throws de.escidoc.core.common.exceptions.application.violated.LockingException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     * @throws de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     * @return
     */
    String lock(final String id, final String param) throws ContentRelationNotFoundException, LockingException,
        InvalidContentException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidXmlException, InvalidStatusException;

    /**
     * Unlock a Content Relation.
     *
     * @param id
     * @param param
     * @throws InvalidStatusException Thrown if resource is not locked.
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException
     * @throws de.escidoc.core.common.exceptions.application.violated.LockingException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     * @throws de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     * @return
     */
    String unlock(final String id, final String param) throws ContentRelationNotFoundException, LockingException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidXmlException,
        InvalidContentException, InvalidStatusException;

    /**
     * Get escidoc XML representation of ContentRelation.
     *
     * @param id objid of ContentRelation resource
     * @return escidoc XML representation of ContentRelation
     * @throws ContentRelationNotFoundException
     *                         Thrown if under provided id no ContentRelation could be found
     * @throws SystemException Thrown if internal error occurs.
     */
    String retrieve(final String id) throws ContentRelationNotFoundException, SystemException;

    /**
     * Retrieves a filtered list of content relations.
     *
     * @param parameters parameters from the SRU request
     * @return Returns XML representation of the list of content relation objects.
     * @throws SystemException If case of internal error.
     */
    String retrieveContentRelations(final SRURequestParameters parameters) throws SystemException;

    /**
     * Get escidoc XML representation of ContentRelations properties.
     *
     * @param id objid of ContentRelation resource
     * @return escidoc XML representation of ContentRelation
     * @throws ContentRelationNotFoundException
     *                         Thrown if under provided id no ContentRelation could be found
     * @throws SystemException Thrown if internal error occurs.
     */
    String retrieveProperties(final String id) throws ContentRelationNotFoundException, SystemException;

    /**
     * Assign persistent identifier to a Content-relation object.
     *
     * @param id        The Id of the Content-relation witch is to assign with an ObjectPid.
     * @param taskParam XML snippet with parameter for the persistent identifier system.
     * @return The assigned persistent identifier for the Content-relation.
     * @throws ContentRelationNotFoundException
     *                                     Thrown if the object with id is does not exist or is no Item.
     * @throws LockingException            Thrown if the Item is locked
     * @throws MissingMethodParameterException
     *                                     Thrown if a parameter is missing within {@code taskParam}.
     * @throws OptimisticLockingException  Thrown if Item was altered in the mean time.
     * @throws PidAlreadyAssignedException Thrown if a Content-relation is already assigned a PID.
     * @throws InvalidXmlException         Thrown if taskParam has invalid XML.
     * @throws SystemException             Thrown in case of internal error.
     */
    String assignObjectPid(final String id, final String taskParam) throws ContentRelationNotFoundException,
        LockingException, MissingMethodParameterException, OptimisticLockingException, InvalidXmlException,
        SystemException, PidAlreadyAssignedException;

    /**
     * Get escidoc XML representation of ContentRelations md-records.
     *
     * @param id objid of ContentRelation resource
     * @return escidoc XML representation of ContentRelation
     * @throws ContentRelationNotFoundException
     *                         Thrown if under provided id no ContentRelation could be found
     * @throws SystemException Thrown if internal error occurs.
     */
    String retrieveMdRecords(final String id) throws ContentRelationNotFoundException, SystemException;

    /**
     * Get escidoc XML representation of ContentRelations md-records.
     *
     * @param id   objid of ContentRelation resource
     * @param name name of a md-record
     * @return escidoc XML representation of ContentRelation
     * @throws ContentRelationNotFoundException
     *                                   Thrown if under provided id no ContentRelation could be found
     * @throws MdRecordNotFoundException Thrown if md-record with provided name was not found
     * @throws SystemException           Thrown if internal error occurs.
     */
    String retrieveMdRecord(final String id, final String name) throws ContentRelationNotFoundException,
        MdRecordNotFoundException, SystemException;

    /**
     * Retrieves a list of registered predicates which can be used to create content relations.
     *
     * @return String containing a list with registered predicates.
     * @throws InvalidContentException Thrown if a xml file with an ontology has invalid content
     * @throws InvalidXmlException     Thrown if a xml file with an ontology is invalid rdf/xml
     * @throws SystemException         Thrown if internal error occurs.
     */
    String retrieveRegisteredPredicates() throws InvalidContentException, InvalidXmlException, SystemException;

    /**
     * Retrieves the subresource resources.
     *
     * @param id The id of the resource.
     * @return Returns the value of the subresource.
     * @throws ContentRelationNotFoundException
     *                         Thrown if a Content Relation with the provided id cannot be found.
     * @throws SystemException If case of internal error.
     */
    String retrieveResources(String id) throws ContentRelationNotFoundException, SystemException;
}
