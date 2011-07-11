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
package de.escidoc.core.tme.business.interfaces;

import de.escidoc.core.common.exceptions.application.invalid.TmeException;
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
     * Identify the format of the given file and extract the metadata.
     *
     * @param requests The list of files to examine.
     * @return A list with jhove results for the files.
     * @throws AuthenticationException TODO
     * @throws AuthorizationException  TODO
     * @throws MissingMethodParameterException
     *                                 TODO
     * @throws SystemException         TODO
     * @throws TmeException            TODO
     */
    String extract(final String requests) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, TmeException;

}
