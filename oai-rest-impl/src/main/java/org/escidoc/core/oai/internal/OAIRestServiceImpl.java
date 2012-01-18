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
package org.escidoc.core.oai.internal;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.escidoc.core.domain.oai.SetDefinitionTO;
import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.oai.OAIRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.oai.service.interfaces.SetDefinitionHandlerInterface;

/**
 * REST Service Implementation for OAI Set Definition Service.
 * 
 * @author SWA
 * 
 */
@Service
public class OAIRestServiceImpl implements OAIRestService {

    private final static Logger LOG = LoggerFactory.getLogger(OAIRestServiceImpl.class);

    @Autowired
    @Qualifier("service.SetDefinitionHandler")
    private SetDefinitionHandlerInterface oaiHandler;

    private JAXBContext jaxbContext;

    protected OAIRestServiceImpl() {
        try {
            this.jaxbContext = JAXBContext.newInstance(SetDefinitionTO.class);
        }
        catch (JAXBException e) {
            LOG.error("Error on initialising JAXB context.", e);
        }
    }

    public SetDefinitionTO create(final SetDefinitionTO setDefinitionTO) throws UniqueConstraintViolationException,
        InvalidXmlException, MissingMethodParameterException, SystemException, AuthenticationException,
        AuthorizationException {

        return ServiceUtility.fromXML(SetDefinitionTO.class,
            this.oaiHandler.create(ServiceUtility.toXML(setDefinitionTO)));
    }

    public SetDefinitionTO retrieve(final String id) throws ResourceNotFoundException, MissingMethodParameterException,
        SystemException, AuthenticationException, AuthorizationException {

        return ServiceUtility.fromXML(SetDefinitionTO.class, this.oaiHandler.retrieve(id));
    }

    public SetDefinitionTO update(final String id, final SetDefinitionTO setDefinitionTO)
        throws ResourceNotFoundException, OptimisticLockingException, MissingMethodParameterException, SystemException,
        AuthenticationException, AuthorizationException {

        return ServiceUtility.fromXML(SetDefinitionTO.class,
            this.oaiHandler.update(id, ServiceUtility.toXML(setDefinitionTO)));
    }

    public void delete(final String id) throws ResourceNotFoundException, MissingMethodParameterException,
        SystemException, AuthenticationException, AuthorizationException {

        this.oaiHandler.delete(id);
    }

}
