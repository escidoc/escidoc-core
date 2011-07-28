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

import de.escidoc.core.common.business.filter.LuceneRequestParameters;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.PidAlreadyAssignedException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * A content relation resource handler.
 *
 * @author Steffen Wagner
 */
@Service("service.ContentRelationHandler")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ContentRelationHandler implements ContentRelationHandlerInterface {

    @Autowired
    @Qualifier("business.FedoraContentRelationHandler")
    private de.escidoc.core.om.business.interfaces.ContentRelationHandlerInterface handler;

    /**
     * Private constructor to prevent initialization.
     */
    protected ContentRelationHandler() {
    }

    /**
     * See Interface for functional description.
     *
     * @param xmlData to be created corresponding to XML-schema "content-relation.xsd".
     * @return The XML representation of the created Content Relation corresponding to XML-schema
     *         "content-relation.xsd".
     * @throws AuthenticationException        Thrown if authentication fails.
     * @throws AuthorizationException         Thrown if authorization fails.
     * @throws MissingAttributeValueException Thrown if a value of an attribute is missing.
     * @throws MissingMethodParameterException
     *                                        Thrown if method parameter is empty
     * @throws InvalidXmlException            Thrown if a provided xml is not valid.
     * @throws InvalidContentException        Thrown if a provided xml contains an invalid data.
     * @throws ReferencedResourceNotFoundException
     *                                        Thrown if one of the referenced resources is not exist.
     * @throws RelationPredicateNotFoundException
     *                                        Thrown if a provided relation type is not on the predicate list.
     * @throws SystemException                e
     * @see de.escidoc.core.common.business.interfaces.ResourceHandlerInterface #create(java.lang.String)
     */
    @Override
    public String create(final String xmlData) throws AuthenticationException, AuthorizationException,
        MissingAttributeValueException, MissingMethodParameterException, InvalidXmlException, InvalidContentException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, SystemException {

        return handler.create(xmlData);
    }

    /**
     * See Interface for functional description.
     *
     * @param id The id of the resource.
     * @throws AuthenticationException Thrown if authentication fails.
     * @throws AuthorizationException  Thrown if authorization fails.
     * @throws ContentRelationNotFoundException
     *                                 Thrown if a content relation with the provided id cannot be found.
     * @throws LockingException        Thrown if Content Relation is locked by other user
     * @throws SystemException         Thrown if internal error occurs.
     */
    @Override
    public void delete(final String id) throws AuthenticationException, AuthorizationException,
        ContentRelationNotFoundException, SystemException, LockingException {

        handler.delete(id);
    }

    /**
     * Lock a Content Relation for other user access.
     *
     * @param id    The id of the content-relation.
     * @param param The time stamp of the last modification of the content-relation. Necessary for optimistic locking
     *              purpose.
     * @return last-modification-date within XML (result.xsd)
     * @throws AuthenticationException    Thrown if authentication fails.
     * @throws AuthorizationException     Thrown if authorization fails.
     * @throws ContentRelationNotFoundException
     *                                    e
     * @throws LockingException           e
     * @throws InvalidContentException    e
     * @throws MissingMethodParameterException
     *                                    e
     * @throws SystemException            e
     * @throws OptimisticLockingException e
     * @throws InvalidXmlException        e
     * @throws InvalidStatusException     e
     */
    @Override
    public String lock(final String id, final String param) throws AuthenticationException, AuthorizationException,
        ContentRelationNotFoundException, LockingException, InvalidContentException, MissingMethodParameterException,
        SystemException, OptimisticLockingException, InvalidXmlException, InvalidStatusException {
        return handler.lock(id, param);
    }

    /**
     * Unlock a Content Relation.
     *
     * @param id    The id of the content-relation.
     * @param param The time stamp of the last modification of the content-relation. Necessary for optimistic locking
     *              purpose.
     * @return last-modification-date within XML (result.xsd)
     * @throws AuthenticationException    Thrown if authentication fails.
     * @throws AuthorizationException     Thrown if authorization fails.
     * @throws ContentRelationNotFoundException
     *                                    e
     * @throws LockingException           e
     * @throws MissingMethodParameterException
     *                                    e
     * @throws SystemException            e
     * @throws OptimisticLockingException e
     * @throws InvalidXmlException        e
     * @throws InvalidContentException    e
     * @throws InvalidStatusException     Thrown if resource is not locked.
     */
    @Override
    public String unlock(final String id, final String param) throws AuthenticationException, AuthorizationException,
        ContentRelationNotFoundException, LockingException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidXmlException, InvalidContentException, InvalidStatusException {

        return handler.unlock(id, param);
    }

    /**
     * Submit a resource with a provided id.
     *
     * @param id    The id of the content-relation.
     * @param param The time stamp of the last modification of the content-relation. Necessary for optimistic locking
     *              purpose.
     * @return last-modification-date within XML (result.xsd)
     * @throws AuthenticationException    Thrown if authentication fails.
     * @throws AuthorizationException     Thrown if authorization fails.
     * @throws ContentRelationNotFoundException
     *                                    e
     * @throws LockingException           e
     * @throws InvalidStatusException     e
     * @throws MissingMethodParameterException
     *                                    e
     * @throws SystemException            e
     * @throws OptimisticLockingException e
     * @throws InvalidXmlException        e
     * @throws InvalidContentException    e
     */
    @Override
    public String submit(final String id, final String param) throws AuthenticationException, AuthorizationException,
        ContentRelationNotFoundException, LockingException, InvalidStatusException, MissingMethodParameterException,
        SystemException, OptimisticLockingException, InvalidXmlException, InvalidContentException {
        return handler.submit(id, param);
    }

    /**
     * Release a resource with a provided id.
     *
     * @param id    The id of the content-relation.
     * @param param The time stamp of the last modification of the content-relation. Necessary for optimistic locking
     *              purpose.
     * @return last-modification-date within XML (result.xsd)
     * @throws AuthenticationException    Thrown if authentication fails.
     * @throws AuthorizationException     Thrown if authorization fails.
     * @throws ContentRelationNotFoundException
     *                                    e
     * @throws LockingException           e
     * @throws InvalidStatusException     e
     * @throws MissingMethodParameterException
     *                                    e
     * @throws SystemException            e
     * @throws OptimisticLockingException e
     * @throws InvalidXmlException        e
     * @throws InvalidContentException    e
     */
    @Override
    public String release(final String id, final String param) throws AuthenticationException, AuthorizationException,
        ContentRelationNotFoundException, LockingException, InvalidStatusException, MissingMethodParameterException,
        SystemException, OptimisticLockingException, InvalidXmlException, InvalidContentException {
        return handler.release(id, param);
    }

    /**
     * Revise a resource with a provided id.
     *
     * @param id    The id of the content-relation.
     * @param param The time stamp of the last modification of the content-relation. Necessary for optimistic locking
     *              purpose.
     * @return last-modification-date within XML (result.xsd)
     * @throws AuthenticationException    Thrown if authentication fails.
     * @throws AuthorizationException     Thrown if authorization fails.
     * @throws ContentRelationNotFoundException
     *                                    e
     * @throws LockingException           e
     * @throws InvalidStatusException     e
     * @throws MissingMethodParameterException
     *                                    e
     * @throws SystemException            e
     * @throws OptimisticLockingException e
     * @throws XmlCorruptedException      e
     * @throws InvalidContentException    e
     */
    @Override
    public String revise(final String id, final String param) throws AuthenticationException, AuthorizationException,
        ContentRelationNotFoundException, LockingException, InvalidStatusException, MissingMethodParameterException,
        SystemException, OptimisticLockingException, XmlCorruptedException, InvalidContentException {
        return handler.revise(id, param);
    }

    /**
     * See Interface for functional description.
     *
     * @param id The id of the content-relation.
     * @return last-modification-date within XML (result.xsd)
     * @throws AuthenticationException Thrown if authentication fails.
     * @throws AuthorizationException  Thrown if authorization fails.
     * @throws ContentRelationNotFoundException
     *                                 e
     * @throws SystemException         e
     * @see de.escidoc.core.common.business.interfaces.ResourceHandlerInterface #retrieve(java.lang.String)
     */
    @Override
    public String retrieve(final String id) throws AuthenticationException, AuthorizationException,
        ContentRelationNotFoundException, SystemException {

        return handler.retrieve(id);
    }

    /**
     * Retrieves a filtered list of content relations.
     *
     * @param parameterMap map of key - value pairs describing the filter
     * @return Returns XML representation of the list of content relation objects.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws SystemException             If case of internal error.
     */
    @Override
    public String retrieveContentRelations(final Map<String, String[]> parameterMap)
        throws InvalidSearchQueryException, SystemException {

        return handler.retrieveContentRelations(new LuceneRequestParameters(parameterMap));
    }

    /**
     * See Interface for functional description.
     *
     * @param id objid of Content Relation
     * @return XML Representation of properties
     * @throws AuthenticationException Thrown if authentication fails.
     * @throws AuthorizationException  Thrown if authorization fails.
     * @throws ContentRelationNotFoundException
     *                                 e
     * @throws SystemException         e
     * @see ContentRelationHandlerInterface #retrieveProperties(java.lang.String)
     */
    @Override
    public String retrieveProperties(final String id) throws AuthenticationException, AuthorizationException,
        ContentRelationNotFoundException, SystemException {

        return handler.retrieveProperties(id);
    }

    /**
     * Update Content Relation.
     *
     * @param id      objid of Content Relation
     * @param xmlData XML representation of Content Relation
     * @return XML representation of updated Content Relation
     * @throws AuthenticationException        Thrown if authentication fails.
     * @throws AuthorizationException         Thrown if authorization fails.
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
    @Override
    public String update(final String id, final String xmlData) throws AuthenticationException, AuthorizationException,
        ContentRelationNotFoundException, OptimisticLockingException, InvalidContentException, InvalidStatusException,
        LockingException, MissingAttributeValueException, SystemException, InvalidXmlException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, MissingMethodParameterException {

        return handler.update(id, xmlData);
    }

    /**
     * Assign persistent identifier to a Content-relation object.
     *
     * @param id        The Id of the Content-relation witch is to assign with an ObjectPid.
     * @param taskParam XML snippet with parameter for the persistent identifier system.
     * @return The assigned persistent identifier for the Content-relation.
     * @throws AuthenticationException     Thrown if authentication fails.
     * @throws AuthorizationException      Thrown if authorization fails.
     * @throws ContentRelationNotFoundException
     *                                     Thrown if the object with id is does not exist or is no Item.
     * @throws LockingException            Thrown if the Item is locked
     * @throws MissingMethodParameterException
     *                                     Thrown if a parameter is missing within {@code taskParam}.
     * @throws OptimisticLockingException  Thrown if Item was altered in the mean time.
     * @throws PidAlreadyAssignedException Thrown if a Content-relation is already assigned a PID.
     * @throws InvalidXmlException         Thrown if taskParam has invalid XML.
     * @throws SystemException             Thrown in case of internal error.
     * @see ContentRelationHandlerInterface #assignObjectPid(java.lang.String,java.lang.String)
     */
    @Override
    public String assignObjectPid(final String id, final String taskParam) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, LockingException, MissingMethodParameterException,
        OptimisticLockingException, InvalidXmlException, SystemException, PidAlreadyAssignedException {

        return handler.assignObjectPid(id, taskParam);
    }

    /**
     * Get escidoc XML representation of ContentRelations md-records.
     *
     * @param id objid of ContentRelation resource
     * @return escidoc XML representation of ContentRelation
     * @throws AuthenticationException Thrown if authentication fails.
     * @throws AuthorizationException  Thrown if authorization fails.
     * @throws ContentRelationNotFoundException
     *                                 Thrown if under provided id no ContentRelation could be found
     * @throws SystemException         Thrown if internal error occurs.
     * @see ContentRelationHandlerInterface #retrieveMdRecords(java.lang.String)
     */
    @Override
    public String retrieveMdRecords(final String id) throws AuthenticationException, AuthorizationException,
        ContentRelationNotFoundException, SystemException {

        return handler.retrieveMdRecords(id);
    }

    /**
     * Retrieves a list of registered predicates which can be used to create content relations.
     *
     * @return String containing a list with registered predicates.
     * @throws InvalidContentException Thrown if a XML file with an ntology has invalid content
     * @throws InvalidXmlException     Thrown if a XML file with an ontology is invalid rdf/xml
     * @throws SystemException         Thrown if internal error occurs.
     */
    @Override
    public String retrieveRegisteredPredicates() throws InvalidContentException, InvalidXmlException, SystemException {
        return handler.retrieveRegisteredPredicates();
    }

    /**
     * Get escidoc XML representation of ContentRelations md-records.
     *
     * @param id   objid of ContentRelation resource
     * @param name name of a md-record
     * @return escidoc XML representation of ContentRelation
     * @throws AuthenticationException   Thrown if authentication fails.
     * @throws AuthorizationException    Thrown if authorization fails.
     * @throws ContentRelationNotFoundException
     *                                   Thrown if under provided id no ContentRelation could be found
     * @throws MdRecordNotFoundException Thrown if md-record with provided name was not found
     * @throws SystemException           Thrown if internal error occurs.
     * @see de.escidoc.core.om.service.interfaces .ContentRelationHandlerInterface #retrieveMdRecord(java.lang.String,java.lang.String)
     */
    @Override
    public String retrieveMdRecord(final String id, final String name) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, MdRecordNotFoundException, SystemException {

        return handler.retrieveMdRecord(id, name);
    }

    @Override
    public String retrieveResources(final String id) throws ContentRelationNotFoundException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException {

        return handler.retrieveResources(id);
    }
}
