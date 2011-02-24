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

import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.StagingFileNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.st.service.interfaces.StagingFileHandlerInterface;

/**
 * A StagingFile handler.
 * 
 * @spring.bean id="service.StagingFileHandler"
 * @interface class="de.escidoc.core.st.service.interfaces.StagingFileHandlerInterface"
 * @author TTE
 * @service
 * @axis.exclude
 * @st
 * 
 */
public class StagingFileHandler implements StagingFileHandlerInterface {

    private de.escidoc.core.st.business.interfaces.StagingFileHandlerInterface handler;

    /**
     * Injects the staging file handler.
     * 
     * @param stagingFileHandler
     *            The item handler bean to inject.
     * 
     * @spring.property ref="business.StagingFileHandler"
     * @service.exclude
     * @om
     */
    public void setStagingFileHandler(
        final de.escidoc.core.st.business.interfaces.StagingFileHandlerInterface stagingFileHandler) {

        this.handler = stagingFileHandler;
    }



    /**
     * See Interface for functional description.
     * 
     * @param binaryContent
     * @return
     * @see de.escidoc.core.st.service.interfaces.StagingFileHandlerInterface
     *      #create(de.escidoc.core.om.service.result.EscidocBinaryContent)
     * 
     * @ejb.transaction type="RequiresNew"
     * @axis.exclude
     * @st
     */
    @Override
    public String create(final EscidocBinaryContent binaryContent)
        throws MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {

        return handler.create(binaryContent);
    }

    /**
     * See Interface for functional description.
     * 
     * @param stagingFileId
     * @return
     * @see de.escidoc.core.st.service.interfaces.StagingFileHandlerInterface
     *      #retrieve(java.lang.String)
     * 
     * @axis.exclude
     * @st
     */
    @Override
    public EscidocBinaryContent retrieve(final String stagingFileId)
        throws StagingFileNotFoundException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        SystemException {

        return handler.retrieve(stagingFileId);
    }

}
