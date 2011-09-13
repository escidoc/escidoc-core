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

import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.sm.AggregationDefinitionTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.AggregationDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.sm.AggregationDefinitionRestService;
import de.escidoc.core.sm.service.interfaces.AggregationDefinitionHandlerInterface;

/**
 * @author Michael Hoppe
 *
 */
public class AggregationDefinitionRestServiceImpl implements AggregationDefinitionRestService {

    @Autowired
    @Qualifier("service.AggregationDefinitionHandler")
    private AggregationDefinitionHandlerInterface aggregationDefinitionHandler;

    /**
     * 
     */
    public AggregationDefinitionRestServiceImpl() {
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.sm.AggregationDefinitionRestService#create(org.escidoc.core.domain.sm.AggregationDefinitionTO)
     */
    @Override
    public AggregationDefinitionTO create(final AggregationDefinitionTO aggregationDefinitionTO)
        throws AuthenticationException, AuthorizationException, XmlSchemaValidationException, XmlCorruptedException,
        MissingMethodParameterException, ScopeNotFoundException, SystemException {
        return ServiceUtility.fromXML(AggregationDefinitionTO.class, this.aggregationDefinitionHandler.create(ServiceUtility.toXML(aggregationDefinitionTO)));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.sm.AggregationDefinitionRestService#delete(java.lang.String)
     */
    @Override
    public void delete(final String id) throws AuthenticationException, AuthorizationException,
        AggregationDefinitionNotFoundException, MissingMethodParameterException, SystemException {
        this.aggregationDefinitionHandler.delete(id);
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.sm.AggregationDefinitionRestService#retrieve(java.lang.String)
     */
    @Override
    public AggregationDefinitionTO retrieve(final String id) throws AuthenticationException, AuthorizationException,
        AggregationDefinitionNotFoundException, MissingMethodParameterException, SystemException {
        return ServiceUtility.fromXML(AggregationDefinitionTO.class, this.aggregationDefinitionHandler.retrieve(id));
    }

}
