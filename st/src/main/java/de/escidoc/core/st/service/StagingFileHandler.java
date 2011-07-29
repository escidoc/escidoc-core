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
package de.escidoc.core.st.service;

import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.StagingFileNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.st.service.interfaces.StagingFileHandlerInterface;

import org.esidoc.core.utils.io.EscidocBinaryContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * A StagingFile handler.
 *
 * @author Torsten Tetteroo
 */
@Service("service.StagingFileHandler")
public class StagingFileHandler implements StagingFileHandlerInterface {

    @Autowired
    @Qualifier("business.StagingFileHandler")
    private de.escidoc.core.st.business.interfaces.StagingFileHandlerInterface handler;

    /**
     * Protected constructor to prevent instantiation outside of the Spring-context.
     */
    protected StagingFileHandler() {
    }

    @Override
    public String create(final EscidocBinaryContent binaryContent) throws MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        return handler.create(binaryContent);
    }

    @Override
    public EscidocBinaryContent retrieve(final String stagingFileId) throws StagingFileNotFoundException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException {
        return handler.retrieve(stagingFileId);
    }

}
