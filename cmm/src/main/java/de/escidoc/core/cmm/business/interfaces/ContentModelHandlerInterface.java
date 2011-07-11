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
package de.escidoc.core.cmm.business.interfaces;

import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.business.filter.SRURequestParameters;
import de.escidoc.core.common.business.interfaces.IngestableResource;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentStreamNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.application.violated.ResourceInUseException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Interface of a business handler of the content type resource.
 *
 * @author Torsten Tetteroo
 */
public interface ContentModelHandlerInterface extends IngestableResource {

    /**
     * Create a content model.
     *
     * @param xmlData The xml representation of the content model.
     * @return The xml representation of the created content model.
     * @throws SystemException If an internal error occured.
     * @throws de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     */
    String create(String xmlData) throws SystemException, InvalidContentException, MissingAttributeValueException,
        XmlCorruptedException;

    /**
     * Deletes the specified content model.
     *
     * @param id The id of the content model.
     * @throws ContentModelNotFoundException If the specified content model was not found.
     * @throws SystemException               If an internal error occured.
     * @throws de.escidoc.core.common.exceptions.application.violated.LockingException
     * @throws de.escidoc.core.common.exceptions.application.violated.ResourceInUseException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException
     */
    void delete(String id) throws ContentModelNotFoundException, SystemException, LockingException,
        InvalidStatusException, ResourceInUseException;

    /**
     * Retrieves the specified content model.
     *
     * @param id The id of the content model.
     * @return Returns the XML representation of the content model.
     * @throws ContentModelNotFoundException If the specified content model was not found.
     * @throws SystemException               If an internal error occured.
     */
    String retrieve(String id) throws ContentModelNotFoundException, SystemException;

    /**
     * Retrieves the specified content-stream of the specified content model.
     *
     * @param id   The id of the content model.
     * @param name The name of the content-stream.
     * @return Returns the XML representation of the specified content-stream of the content model.
     * @throws ContentModelNotFoundException If the specified content model was not found.
     * @throws SystemException               If an internal error occured.
     */
    String retrieveContentStream(final String id, final String name) throws ContentModelNotFoundException,
        SystemException;

    EscidocBinaryContent retrieveContentStreamContent(final String id, final String name)
        throws ContentModelNotFoundException, SystemException, ContentStreamNotFoundException, InvalidStatusException;

    /**
     * Retrieves the resources of the specified content model.
     *
     * @param id The id of the content model.
     * @return Returns the XML representation of the resources of the content model.
     * @throws ContentModelNotFoundException If the specified content model was not found.
     * @throws SystemException               If an internal error occured.
     */
    String retrieveResources(final String id) throws ContentModelNotFoundException, SystemException;

    String retrieveVersionHistory(final String id) throws ContentModelNotFoundException, SystemException;

    /**
     * Retrieves a filtered list of Content Models.
     *
     * @param parameters parameters from the SRU request
     * @return Returns XML representation of the list of Content Model objects.
     * @throws SystemException Thrown in case of an internal error.
     */
    String retrieveContentModels(final SRURequestParameters parameters) throws SystemException;

    /**
     * Updates the specified content model with the provided data.
     *
     * @param id      The id of the content model.
     * @param xmlData The updated xml representation of the content model.
     * @return Returns the xml representation of the updated resource.
     * @throws ContentModelNotFoundException If the specified content model was not found.
     * @throws InvalidXmlException           If the schema validation fails.
     * @throws OptimisticLockingException    If the content model was chaged in the meantime.
     * @throws SystemException               If an internal error occured.
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     */
    String update(String id, String xmlData) throws ContentModelNotFoundException, InvalidXmlException,
        OptimisticLockingException, SystemException, ReadonlyVersionException, MissingAttributeValueException,
        InvalidContentException;

    EscidocBinaryContent retrieveMdRecordDefinitionSchemaContent(final String id, final String name)
        throws ContentModelNotFoundException, SystemException;

    EscidocBinaryContent retrieveResourceDefinitionXsltContent(final String id, final String name)
        throws SystemException, ResourceNotFoundException;
}
