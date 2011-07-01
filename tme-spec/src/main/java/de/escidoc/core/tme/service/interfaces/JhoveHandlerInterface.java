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

package de.escidoc.core.tme.service.interfaces;

import de.escidoc.core.common.annotation.Validate;
import de.escidoc.core.common.exceptions.application.invalid.TmeException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Service interface of an Technical metadata extraction handler.
 *
 * @author Michael Schneider
 */
public interface JhoveHandlerInterface {

    /**
     * Identify the format of the given file and extract the metadata. Please refer to Chapter 1 for an example of the
     * request parameter.
     *
     * @param requests The list of files to examine.
     * @return A list with jhove results for the requested files.
     * @throws AuthenticationException      If authentication fails.
     * @throws AuthorizationException       If authorization fails.
     * @throws XmlCorruptedException        Thrown if provided data is corrupted.
     * @throws XmlSchemaValidationException Thrown if the schema validation of the provided data fails.
     * @throws MissingMethodParameterException
     *                                      If the xml data is not provided.
     * @throws SystemException              If an internal error occurred.
     * @throws TmeException                 If the invocation of jhove classes fails.
     */
    @Validate(param = 0, resolver = "getTmeRequestsSchemaLocation", root = "request")
    String extract(final String requests) throws AuthenticationException, AuthorizationException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException, SystemException,
        TmeException;
}
