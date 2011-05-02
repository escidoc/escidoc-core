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

package de.escidoc.core.common.business.interfaces;

import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Common interface implemented by all resource handlers.
 *
 * @author André Schenk
 */
public interface ResourceHandlerInterface {

    /**
     * Retrieve the XML representation of a resource object representing an eSciDoc resource.
     *
     * @param resourceId unique identifier of the resource
     * @return The XML representation of the resource.
     * @throws ResourceNotFoundException Thrown if no resource with the provided id exists.
     * @throws SystemException           Thrown in case of an internal system error.
     */
    String retrieve(final String resourceId) throws ResourceNotFoundException, SystemException;

    /**
     * Create a resource object representing an eSciDoc resource.
     *
     * @param xmlData XML representation of the resource to be created
     * @return XML representation of the created resource.
     * @throws UniqueConstraintViolationException
     *                             Thrown if the provided login name of the user is not unique.
     * @throws InvalidXmlException Thrown if the provided XML data is invalid.
     * @throws SystemException     Thrown in case of an internal system error.
     */
    String create(final String xmlData) throws UniqueConstraintViolationException, InvalidXmlException, SystemException;

    /**
     * Delete the specified resource.
     *
     * @param resourceId unique identifier of the resource
     * @throws ResourceNotFoundException Thrown if no resource with the provided id exists.
     * @throws SystemException           Thrown in case of an internal system error.
     */
    void delete(final String resourceId) throws ResourceNotFoundException, SystemException;

    /**
     * Update the data of a resource object representing an eSciDoc resource.
     *
     * @param resourceId unique identifier of the resource
     * @param xmlData    XML representation of the resource to be created
     * @return XML representation of the updated resource.
     * @throws ResourceNotFoundException      Thrown if no resource with the provided id exists.
     * @throws UniqueConstraintViolationException
     *                                        Thrown if the provided login name of the user is not unique.
     * @throws InvalidXmlException            Thrown if the provided XML data is invalid.
     * @throws MissingAttributeValueException Thrown if a mandatory attribute is not provided within the XML data.
     * @throws OptimisticLockingException     Thrown in case of an optimistic locking error.
     * @throws SystemException                Thrown in case of an internal system error.
     */
    String update(final String resourceId, final String xmlData) throws ResourceNotFoundException,
        UniqueConstraintViolationException, InvalidXmlException, MissingAttributeValueException,
        OptimisticLockingException, SystemException;
}
