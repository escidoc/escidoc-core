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

package de.escidoc.core.cmm.service.interfaces;

import de.escidoc.core.common.annotation.Validate;
import de.escidoc.core.common.business.interfaces.IngestableResource;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.application.violated.ResourceInUseException;
import de.escidoc.core.common.exceptions.system.SystemException;

import java.util.Map;

import org.esidoc.core.utils.io.EscidocBinaryContent;

/**
 * Interface of a business layer content type handler.
 *
 * @author Frank Schwichtenberg
 */
public interface ContentModelHandlerInterface extends IngestableResource {

    /**
     * Create a Content Model.<br/> Since Version 1.2 of the eSciDoc Infrastructure the XML representation of Content
     * Model allows for defining cardinality and metadata profiles for metadata records. This feature is in an
     * experimental state and does not allow to validate an actual content object against these definitions.<br/> The
     * configurable behavior (resource-definitions) for content objects in version 1.2 of the eSciDoc Infrastructure is
     * limited to transformation operations pertaining single metadata records. That means one can define an operation
     * by give a name for that operation and define a XSLT and the name of a metadata record.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Content Model is created.</li> <li>The XML data is returned.</li> </ul>
     *
     * @param xmlData The XML representation of the Content Model to be created corresponding to XML-schema
     *                "content-model.xsd".
     * @return The XML representation of the created Content Model corresponding to XML-schema "content-model.xsd".
     * @throws AuthenticationException        If authentication fails.
     * @throws AuthorizationException         If authorization fails.
     * @throws MissingMethodParameterException
     *                                        If the XML data is not provided.
     * @throws SystemException                If an internal error occurred.
     * @throws MissingAttributeValueException If a required attribute is missing.
     * @throws InvalidContentException        If the given XML data contains invalid values.
     * @throws XmlCorruptedException          Thrown if the schema validation of the provided data failed.
     * @throws XmlSchemaValidationException   Thrown if the schema validation of the provided data failed.
     */
    @Validate(param = 0, resolver = "getContentModelSchemaLocation", root = "content-model")
    String create(String xmlData) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, MissingAttributeValueException, InvalidContentException,
        XmlCorruptedException, XmlSchemaValidationException;

    /**
     * Delete the specified Content Model.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The Content Model must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Content Model is accessed using the provided reference.</li> <li>The Content
     * Model is deleted.</li> <li>No data is returned.</li> </ul>
     *
     * @param id The id of the Content Model to be deleted.
     * @throws AuthenticationException       If authentication fails.
     * @throws AuthorizationException        If authorization fails.
     * @throws ContentModelNotFoundException If the specified Content Model was not found.
     * @throws MissingMethodParameterException
     *                                       If the id is not provided.
     * @throws SystemException               If an internal error occurred.
     * @throws InvalidStatusException        If the Content Model can not be deleted because of its status.
     * @throws LockingException              If the Content Model is locked.
     * @throws ResourceInUseException        If the Content Model is referenced by any content object and can not be
     *                                       deleted.
     */
    void delete(String id) throws AuthenticationException, AuthorizationException, ContentModelNotFoundException,
        MissingMethodParameterException, SystemException, LockingException, InvalidStatusException,
        ResourceInUseException;

    /**
     * Retrieve the specified Content Model.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The Content Model must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Content Model is accessed using the provided reference.</li> <li>The XML data is
     * returned.</li> </ul>
     *
     * @param id The id of the Content Model to be retrieved.
     * @return The XML representation of the retrieved Content Model.
     * @throws AuthenticationException       If authentication fails.
     * @throws AuthorizationException        If authorization fails.
     * @throws ContentModelNotFoundException If the specified Content Model was not found.
     * @throws MissingMethodParameterException
     *                                       If the id is not provided.
     * @throws SystemException               If an internal error occurred.
     */
    String retrieve(String id) throws AuthenticationException, AuthorizationException, ContentModelNotFoundException,
        MissingMethodParameterException, SystemException;

    /**
     * Retrieve the resources of the specified Content Model.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The Content Model must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Content Model is accessed using the provided reference.</li> <li>The XML data is
     * returned.</li> </ul>
     *
     * @param id The id of the Content Model.
     * @return The XML representation of the resources of the Content Model.
     * @throws AuthenticationException       If authentication fails.
     * @throws AuthorizationException        If authorization fails.
     * @throws ContentModelNotFoundException If the specified Content Model was not found.
     * @throws MissingMethodParameterException
     *                                       If the id is not provided.
     * @throws SystemException               If an internal error occurred.
     */
    String retrieveResources(String id) throws AuthenticationException, AuthorizationException,
        ContentModelNotFoundException, MissingMethodParameterException, SystemException;

    /**
     * Retrieve the version history of the Content Model.
     *
     * @param id The id of the Content Model.
     * @return The XML representation of the version history of the content model.
     * @throws AuthenticationException       If authentication fails.
     * @throws AuthorizationException        If authorization fails.
     * @throws ContentModelNotFoundException If the specified Content Model was not found.
     * @throws MissingMethodParameterException
     *                                       If the id is not provided.
     * @throws SystemException               If an internal error occurred.
     */
    String retrieveVersionHistory(String id) throws AuthenticationException, AuthorizationException,
        ContentModelNotFoundException, MissingMethodParameterException, SystemException;

    /**
     * Retrieves a filtered list of Content Models.
     *
     * @param parameterMap map of key - value pairs describing the filter
     * @return Returns XML representation of the list of Content Model objects.
     * @throws InvalidSearchQueryException Thrown if the given search query could not be translated into a SQL query.
     * @throws SystemException             Thrown in case of an internal error.
     */
    String retrieveContentModels(Map<String, String[]> parameterMap) throws InvalidSearchQueryException,
        SystemException;

    /**
     * Update the specified Content Model with the provided data.<br/> Since Version 1.2 of the eSciDoc Infrastructure
     * the XML representation of Content Model allows for defining cardinality and metadata profiles for metadata
     * records. This feature is in an experimental state and does not allow to validate an actual content object against
     * these definitions.<br/> The configurable behavior (resource-definitions) for content objects in version 1.2 of
     * the eSciDoc Infrastructure is limited to transformation operations pertaining single metadata records. That means
     * one can define an operation by give a name for that operation and define a XSLT and the name of a metadata
     * record.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * The Content Model must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Content Model is accessed using the provided reference.</li> <li>The Content
     * Model is updated.</li> <li>The XML data is returned.</li> </ul>
     *
     * @param id      The id of the Content Model.
     * @param xmlData The XML representation of the Content Model to be updated corresponding to XML-schema
     *                "content-model.xsd".
     * @return The XML representation of the created Content Model corresponding to XML-schema "content-model.xsd"..
     * @throws AuthenticationException        If authentication fails.
     * @throws AuthorizationException         If authorization fails.
     * @throws MissingMethodParameterException
     *                                        If the id or the XML data is not provided.
     * @throws ContentModelNotFoundException  If the specified Content Model was not found.
     * @throws InvalidXmlException            If the schema validation fails.
     * @throws OptimisticLockingException     If the Content Model was changed in the meantime.
     * @throws SystemException                If an internal error occurred.
     * @throws ReadonlyVersionException       If a version is specified which is not the latest version of the Content
     *                                        Model.
     * @throws InvalidContentException        If the given XML data contains invalid values.
     * @throws MissingAttributeValueException If a required attribute is missing.
     */
    @Validate(param = 1, resolver = "getContentModelSchemaLocation", root = "content-model")
    String update(String id, String xmlData) throws AuthenticationException, AuthorizationException,
        ContentModelNotFoundException, InvalidXmlException, MissingMethodParameterException,
        OptimisticLockingException, SystemException, ReadonlyVersionException, MissingAttributeValueException,
        InvalidContentException;

    /**
     * Retrieve the schema document of a metadata record definition of a Content Model.
     *
     * @param id   The ID of a Content Model.
     * @param name The name of a metadata record definition inside the specified Content Model.
     * @return The schema document for the specified metadata record definition.
     * @throws AuthenticationException       If authentication fails.
     * @throws AuthorizationException        If authorization fails.
     * @throws MissingMethodParameterException
     *                                       If the id or the XML data is not provided.
     * @throws SystemException               If an error occurs.
     * @throws ContentModelNotFoundException If the Content Model can not be found.
     */
    EscidocBinaryContent retrieveMdRecordDefinitionSchemaContent(String id, String name)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        ContentModelNotFoundException, SystemException;

    /**
     * Retrieve the XSLT document of a resource definition of a Content Model.
     *
     * @param id   The ID of a Content Model.
     * @param name The name of a resource definition inside the specified Content Model.
     * @return The XSLT document for the specified resource definition.
     * @throws SystemException               If an error occurs.
     * @throws ContentModelNotFoundException If the Content Model can not be found.
     * @throws ResourceNotFoundException     If the XSLT cannot be found.
     * @throws AuthenticationException       If authentication fails.
     * @throws AuthorizationException        If authorization fails.
     * @throws MissingMethodParameterException
     *                                       If the id or the XML data is not provided.
     */
    EscidocBinaryContent retrieveResourceDefinitionXsltContent(String id, String name) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, ResourceNotFoundException;
}
