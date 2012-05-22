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
package org.escidoc.core.oum.internal;

import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.sru.ResponseTypeTO;
import org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean;
import org.escidoc.core.oum.OrganizationalUnitsRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface;

/**
 * REST Service Implementation for Organizational Units.
 * 
 * @author SWA
 * 
 */
@Service
public class OrganizationalUnitsRestServiceImpl implements OrganizationalUnitsRestService {

    private final static Logger LOG = LoggerFactory.getLogger(OrganizationalUnitsRestServiceImpl.class);

    @Autowired
    @Qualifier("service.OrganizationalUnitHandler")
    private OrganizationalUnitHandlerInterface organizationalUnitHandler;

    @Autowired
    private ServiceUtility serviceUtility;

    protected OrganizationalUnitsRestServiceImpl() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.oum.OrganizationalUnitsRestService#retrieveOrganizationalUnits(org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean, java.util.String, java.util.String, java.util.String)
     */
    @Override
    public JAXBElement<? extends ResponseTypeTO> retrieveOrganizationalUnits(
        final SruSearchRequestParametersBean parameters, 
        final String roleId,
        final String userId,
        final String omitHighlighting) throws InvalidSearchQueryException,
        InvalidXmlException, MissingMethodParameterException, SystemException {

        Map<String, String[]> map = serviceUtility.handleSruRequest(parameters, roleId, userId, omitHighlighting);

        return (JAXBElement<? extends ResponseTypeTO>) serviceUtility.fromXML(
                this.organizationalUnitHandler.retrieveOrganizationalUnits(map));
    }

}
