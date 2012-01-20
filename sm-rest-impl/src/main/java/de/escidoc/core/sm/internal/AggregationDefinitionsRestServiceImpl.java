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
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.sm.internal;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.sm.AggregationDefinitionListTO;
import org.escidoc.core.domain.sru.RequestType;
import org.escidoc.core.domain.sru.ResponseType;
import org.escidoc.core.domain.sru.parameters.SruRequestTypeFactory;
import org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.sm.AggregationDefinitionsRestService;
import de.escidoc.core.sm.service.interfaces.AggregationDefinitionHandlerInterface;

/**
 * @author Michael Hoppe
 *
 */
public class AggregationDefinitionsRestServiceImpl implements AggregationDefinitionsRestService {

    @Autowired
    @Qualifier("service.AggregationDefinitionHandler")
    private AggregationDefinitionHandlerInterface aggregationDefinitionHandler;

    /**
     * 
     */
    public AggregationDefinitionsRestServiceImpl() {
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.sm.AggregationDefinitionsRestService#retrieveAggregationDefinitions(java.util.String, java.util.String, java.util.String,
     * java.util.String, java.util.String, java.util.String, java.util.String, java.util.String, java.util.String,
     * java.util.String, java.util.String, java.util.String, java.util.String, java.util.String)
     */
    @Override
    public JAXBElement<? extends ResponseType> retrieveAggregationDefinitions(
        final String operation,
        final String version,
        final String query,
        final String startRecord,
        final String maximumRecords,
        final String recordPacking,
        final String recordSchema,
        final String recordXPath,
        final String resultSetTTL,
        final String sortKeys,
        final String stylesheet,
        final String scanClause,
        final String responsePosition,
        final String maximumTerms)
        throws InvalidSearchQueryException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {

        SruSearchRequestParametersBean filter =
            new SruSearchRequestParametersBean(operation, version, query, startRecord, maximumRecords, recordPacking,
                recordSchema, recordXPath, resultSetTTL, sortKeys, stylesheet, scanClause, responsePosition,
                maximumTerms);

		final JAXBElement<? extends RequestType> requestTO = SruRequestTypeFactory
				.createRequestTO(filter, null);

		return ((JAXBElement<? extends ResponseType>) ServiceUtility.fromXML(
				Constants.SRU_CONTEXT_PATH , this.aggregationDefinitionHandler
						.retrieveAggregationDefinitions(ServiceUtility.toMap(requestTO))));
    }

}
