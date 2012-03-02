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

package de.escidoc.core.content.relation.internal;

import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.sru.RequestTypeTO;
import org.escidoc.core.domain.sru.ResponseTypeTO;
import org.escidoc.core.domain.sru.parameters.SruRequestTypeFactory;
import org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.service.KeyValuePair;
import de.escidoc.core.content.relation.ContentRelationsRestService;
import de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface;

/**
 * 
 * @author ?, SWA
 * 
 */
public class ContentRelationsRestServiceImpl implements ContentRelationsRestService {

    @Autowired
    @Qualifier("service.ContentRelationHandler")
    private ContentRelationHandlerInterface contentRelationHandler;

    @Autowired
    private ServiceUtility serviceUtility;

    /**
     * 
     */
    protected ContentRelationsRestServiceImpl() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.content.relation.ContentRelationsRestService#retrieveContentRelations(org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean, java.util.String, java.util.String, java.util.String)
     */
    @Override
    public JAXBElement<? extends ResponseTypeTO> retrieveContentRelations(
        final SruSearchRequestParametersBean parameters, 
        final String roleId, 
        final String userId,
        final String omitHighlighting) throws InvalidSearchQueryException,
        SystemException {

        final List<Map.Entry<String, String>> additionalParams = SruRequestTypeFactory.getDefaultAdditionalParams(
                roleId, userId, omitHighlighting);
        final JAXBElement<? extends RequestTypeTO> requestTO =
            SruRequestTypeFactory.createRequestTO(parameters, additionalParams);

        return (JAXBElement<? extends ResponseTypeTO>)serviceUtility.fromXML(
                this.contentRelationHandler.retrieveContentRelations(serviceUtility.toMap(requestTO)));
    }

}
