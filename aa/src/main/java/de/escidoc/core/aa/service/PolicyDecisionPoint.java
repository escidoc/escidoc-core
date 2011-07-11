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
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.service;

import de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Implementation for the service layer for Aa component.
 *
 * @author Rozita Friedman
 */
@Service("service.PolicyDecisionPoint")
public class PolicyDecisionPoint implements PolicyDecisionPointInterface {

    @Autowired
    @Qualifier("business.PolicyDecisionPoint")
    private de.escidoc.core.aa.business.interfaces.PolicyDecisionPointInterface business;

    /**
     * Default constructor.
     */
    public PolicyDecisionPoint() {
    }

    /**
     * See Interface for functional description.
     *
     * @see PolicyDecisionPointInterface #evaluate(java.lang.String)
     */
    @Override
    public String evaluate(final String requestsXml) throws ResourceNotFoundException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException {

        return business.evaluate(requestsXml);
    }

    @Override
    public boolean[] evaluateRequestList(final List<Map<String, String>> requests) throws ResourceNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {

        return business.evaluateRequestList(requests);
    }

    @Override
    public List<String> evaluateRetrieve(final String resourceName, final List<String> ids)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        ResourceNotFoundException, SystemException {

        return business.evaluateRetrieve(resourceName, ids);
    }

    @Override
    public List<Object[]> evaluateMethodForList(
        final String resourceName, final String methodName, final List<Object[]> argumentList)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        ResourceNotFoundException, SystemException {

        return business.evaluateMethodForList(resourceName, methodName, argumentList);
    }

    /**
     * Setter for the business object.
     *
     * @param business object
     */
    public void setBusiness(final de.escidoc.core.aa.business.interfaces.PolicyDecisionPointInterface business) {
        this.business = business;
    }

    @Override
    public void touch() throws SystemException {
        this.business.touch();
    }
}
