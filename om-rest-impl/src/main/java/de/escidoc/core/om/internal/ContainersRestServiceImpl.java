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
package de.escidoc.core.om.internal;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.sru.RequestType;
import org.escidoc.core.domain.sru.ResponseType;
import org.escidoc.core.domain.sru.parameters.SruRequestTypeFactory;
import org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.service.KeyValuePair;
import de.escidoc.core.om.ContainersRestService;
import de.escidoc.core.om.service.interfaces.ContainerHandlerInterface;

/**
 * REST Service Implementation for Containers.
 * 
 * @author SWA
 * 
 */
@Service
public class ContainersRestServiceImpl implements ContainersRestService {

    private final static Logger LOG = LoggerFactory.getLogger(ContainersRestServiceImpl.class);

    @Autowired
    @Qualifier("service.ContainerHandler")
    private ContainerHandlerInterface containerHandler;

    protected ContainersRestServiceImpl() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.context.ContainersRestService#retrieveContainers(org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean, java.util.String, java.util.String, java.util.String)
     */
    @Override
    public JAXBElement<? extends ResponseType> retrieveContainers(
        final SruSearchRequestParametersBean parameters, 
        final String roleId, 
        final String userId,
        final String omitHighlighting) throws MissingMethodParameterException,
        InvalidSearchQueryException, InvalidXmlException, SystemException {

        final List<KeyValuePair> additionalParams = new LinkedList<KeyValuePair>();
        if (roleId != null) {
            additionalParams.add(new KeyValuePair(Constants.SRU_PARAMETER_ROLE, roleId));
        }
        if (userId != null) {
            additionalParams.add(new KeyValuePair(Constants.SRU_PARAMETER_USER, userId));
        }
        if (omitHighlighting != null) {
            additionalParams.add(new KeyValuePair(Constants.SRU_PARAMETER_OMIT_HIGHLIGHTING, omitHighlighting));
        }

        final JAXBElement<? extends RequestType> requestTO =
            SruRequestTypeFactory.createRequestTO(parameters, additionalParams);

		return ((JAXBElement<? extends ResponseType>) ServiceUtility.fromXML(
				Constants.SRU_CONTEXT_PATH , this.containerHandler
						.retrieveContainers(ServiceUtility.toMap(requestTO))));
    }

}
