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

import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.sru.ResponseTypeTO;
import org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.om.ItemsRestService;
import de.escidoc.core.om.service.interfaces.ItemHandlerInterface;

/**
 * REST Service Implementation for Items.
 * 
 * @author SWA
 * 
 */
@Service
@Transactional
public class ItemsRestServiceImpl implements ItemsRestService {

    private final static Logger LOG = LoggerFactory.getLogger(ItemsRestServiceImpl.class);

    @Autowired
    @Qualifier("service.ItemHandler")
    private ItemHandlerInterface itemHandler;

    @Autowired
    private ServiceUtility serviceUtility;

    protected ItemsRestServiceImpl() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.context.ItemsRestService#retrieveItems(org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean, java.util.String, java.util.String, java.util.String)
     */
    @Override
    public JAXBElement<? extends ResponseTypeTO> retrieveItems(
        final SruSearchRequestParametersBean parameters, 
        final String roleId, 
        final String userId,
        final String omitHighlighting) throws SystemException {

        Map<String, String[]> map = serviceUtility.handleSruRequest(parameters, roleId, userId, omitHighlighting);

        return (JAXBElement<? extends ResponseTypeTO>) serviceUtility.fromXML(
                this.itemHandler.retrieveItems(map));
    }

}
