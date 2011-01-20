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

import java.net.URI;
import java.util.List;
import java.util.Map;

import de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.logger.AppLogger;

/**
 * 
 * Implementation for the service layer for Aa component.
 * 
 * @spring.bean id="service.PolicyDecisionPoint"
 * @interface 
 *            class="de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface"
 * @service
 * @author ROF
 * @aa
 */
public class PolicyDecisionPoint implements PolicyDecisionPointInterface {

    /**
     * The logger.
     */
    private static final AppLogger LOG =
        new AppLogger(PolicyDecisionPoint.class.getName());

    private de.escidoc.core.aa.business.interfaces.PolicyDecisionPointInterface business;

    /**
     * Default constructor.
     * 
     * @aa
     */

    public PolicyDecisionPoint() {

    }

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @param requestsXml
     * @return
     * @throws ResourceNotFoundException
     * @throws XmlCorruptedException
     * @throws XmlSchemaValidationException
     * @throws MissingMethodParameterException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface
     *      #evaluate(java.lang.String)
     * @aa
     */
    public String evaluate(final String requestsXml)
        throws ResourceNotFoundException, XmlCorruptedException, 
        XmlSchemaValidationException,
        MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {

        return business.evaluate(requestsXml);
    }

    /**
     * See Interface for functional description.
     * 
     * @param requests
     * @return
     * @throws ResourceNotFoundException
     * @throws MissingMethodParameterException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface
     *      #evaluate(java.lang.String)
     * @aa
     * 
     * @axis.exclude
     */
    public boolean[] evaluateRequestList(
        final List<Map<String, String>> requests)
        throws ResourceNotFoundException,
        MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {

        return business.evaluateRequestList(requests);
    }

    /**
     * See Interface for functional description.
     * 
     * @param resourceName
     * @param ids
     * @return
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws MissingMethodParameterException
     * @throws ResourceNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface
     *      #evaluateRetrieve(java.lang.String, java.util.List)
     * @aa
     * 
     * @axis.exclude
     */
    public List<String> evaluateRetrieve(
        final String resourceName, final List<String> ids)
        throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, ResourceNotFoundException,
        SystemException {

        return business.evaluateRetrieve(resourceName, ids);
    }

    /**
     * See Interface for functional description.
     * 
     * @param resourceName
     * @param methodName
     * @param argumentList
     * @return
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws MissingMethodParameterException
     * @throws ResourceNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface
     *      #evaluateMethodForList(java.lang.String, java.lang.String,
     *      java.util.List)
     * @aa
     * 
     * @axis.exclude
     */
    public List<Object[]> evaluateMethodForList(
        final String resourceName, final String methodName,
        final List<Object[]> argumentList) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        ResourceNotFoundException, SystemException {

        return business.evaluateMethodForList(resourceName, methodName,
            argumentList);
    }

    // CHECKSTYLE:JAVADOC-ON

    /**
     * Setter for the business object.
     * 
     * @spring.property ref="business.PolicyDecisionPoint"
     * @service.exclude
     * @param business
     *            object
     */
    public void setBusiness(
        final de.escidoc.core.aa.business.interfaces.PolicyDecisionPointInterface business) {

        LOG.debug("setBusiness");

        this.business = business;
    }

    public void touch() throws SystemException {
        this.business.touch();
    }
}
