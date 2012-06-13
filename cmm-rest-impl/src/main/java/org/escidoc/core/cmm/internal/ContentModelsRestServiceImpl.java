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
package org.escidoc.core.cmm.internal;

import java.util.Map;

import javax.xml.bind.JAXBElement;

import net.sf.oval.guard.Guarded;
import org.escidoc.core.cmm.ContentModelsRestService;
import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.sru.ResponseTypeTO;
import org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.escidoc.core.cmm.service.interfaces.ContentModelHandlerInterface;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * @author SWA
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Guarded(applyFieldConstraintsToConstructors = false, applyFieldConstraintsToSetters = false,
    assertParametersNotNull = false, checkInvariants = false, inspectInterfaces = true)
public class ContentModelsRestServiceImpl implements ContentModelsRestService {

    @Autowired
    @Qualifier("service.ContentModelHandler")
    private ContentModelHandlerInterface contentModelHandler;

    @Autowired
    private ServiceUtility serviceUtility;

    /**
     *
     */
    protected ContentModelsRestServiceImpl() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.cmm.ContentModelsRestService#retrieveContentModels(org.escidoc.core.domain.sru.parameters
     * .SruSearchRequestParametersBean, java.util.String, java.util.String, java.util.String)
     */
    @Override
    public JAXBElement<? extends ResponseTypeTO> retrieveContentModels(final SruSearchRequestParametersBean parameters,
        final String roleId, final String userId, final String omitHighlighting)
        throws InvalidSearchQueryException, SystemException {

        Map<String, String[]> map = serviceUtility.handleSruRequest(parameters, roleId, userId, omitHighlighting);

        return (JAXBElement<? extends ResponseTypeTO>) serviceUtility.fromXML(
            this.contentModelHandler.retrieveContentModels(map));
    }
}