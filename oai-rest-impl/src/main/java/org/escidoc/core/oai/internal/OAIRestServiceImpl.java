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

import net.sf.oval.guard.Guarded;
import org.escidoc.core.domain.ObjectFactoryProvider;
import org.escidoc.core.domain.oai.SetDefinitionTypeTO;
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

import javax.xml.bind.JAXBElement;

/**
 * REST Service Implementation for OAI Set Definition Service.
 *
 * @author SWA
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Service
@Guarded(applyFieldConstraintsToConstructors = false, applyFieldConstraintsToSetters = false,
    assertParametersNotNull = false, checkInvariants = false, inspectInterfaces = true)
public class OAIRestServiceImpl implements OAIRestService {

    @Autowired
    @Qualifier("service.SetDefinitionHandler")
    private SetDefinitionHandlerInterface oaiHandler;

    @Autowired
    private ServiceUtility serviceUtility;

    @Autowired
    private ObjectFactoryProvider factoryProvider;

    protected OAIRestServiceImpl() {
    }

    /**
     * {@inheritDoc}
     */
    public JAXBElement<SetDefinitionTypeTO> create(final SetDefinitionTypeTO setDefinitionTO)
        throws UniqueConstraintViolationException, InvalidXmlException, MissingMethodParameterException,
        SystemException, AuthenticationException, AuthorizationException {

        return factoryProvider.getOaiFactory().createSetDefinition(serviceUtility
            .fromXML(SetDefinitionTypeTO.class, this.oaiHandler.create(serviceUtility.toXML(setDefinitionTO))));
    }

    /**
     * {@inheritDoc}
     */
    public JAXBElement<SetDefinitionTypeTO> retrieve(final String id)
        throws ResourceNotFoundException, MissingMethodParameterException, SystemException, AuthenticationException,
        AuthorizationException {

        return factoryProvider.getOaiFactory().createSetDefinition(
            serviceUtility.fromXML(SetDefinitionTypeTO.class, this.oaiHandler.retrieve(id)));
    }

    /**
     * {@inheritDoc}
     */
    public JAXBElement<SetDefinitionTypeTO> update(final String id, final SetDefinitionTypeTO setDefinitionTO)
        throws ResourceNotFoundException, OptimisticLockingException, MissingMethodParameterException, SystemException,
        AuthenticationException, AuthorizationException {

        return factoryProvider.getOaiFactory().createSetDefinition(serviceUtility
            .fromXML(SetDefinitionTypeTO.class, this.oaiHandler.update(id, serviceUtility.toXML(setDefinitionTO))));
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final String id)
        throws ResourceNotFoundException, MissingMethodParameterException, SystemException, AuthenticationException,
        AuthorizationException {
        this.oaiHandler.delete(id);
    }
}