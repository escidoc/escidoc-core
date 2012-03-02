/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License
 * for the specific language governing permissions and limitations under the License.
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
package org.escidoc.core.st.internal;

import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.StagingFileNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.st.service.StagingFileHandler;
import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.utils.io.EscidocBinaryContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.escidoc.core.st.StagingRestService;
import org.escidoc.core.domain.st.StagingFileTO;

/**
 * REST Service Implementation for Technical Metadata Extractor.
 * 
 * @author SWA
 * 
 */
@Service
public class StagingRestServiceImpl implements StagingRestService {

    private final static Logger LOG = LoggerFactory.getLogger(StagingRestServiceImpl.class);

    @Autowired
    @Qualifier("service.StagingFileHandler")
    private StagingFileHandler stagingHandler;

    @Autowired
    private ServiceUtility serviceUtility;

    protected StagingRestServiceImpl() {
    }

    @Override
    public StagingFileTO create(final EscidocBinaryContent binaryContent) throws MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {

        return serviceUtility.fromXML(StagingFileTO.class, this.stagingHandler.create(binaryContent));
    }

    @Override
    public EscidocBinaryContent retrieve(final String stagingFileId) throws StagingFileNotFoundException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException {

        return this.stagingHandler.retrieve(stagingFileId);
    }

}
