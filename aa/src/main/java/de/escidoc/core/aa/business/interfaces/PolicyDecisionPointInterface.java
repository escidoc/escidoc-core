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
package de.escidoc.core.aa.business.interfaces;

import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.system.SystemException;

import java.util.List;

/**
 * Interface for the AaComponent to encapsulate the logic.
 *
 * @author Rozita Friedman, Roland Werner
 */
public interface PolicyDecisionPointInterface
    extends de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface {

    /**
     * The method evaluates the provided authorization requests. <br/> Information about user(s), action(s) and
     * resource(s) are contained in the provided XML data string which represents the content of a
     * {@code com.sun.xacml.ctx.RequestCtx} objects.<p/>
     * <p/>
     * For each authorization request, the following steps are executed: <ul> <li>An authorisation request object (a
     * {@code com.sun.xacml.ctx.RequestCtx} object) is generated from the provided XML data and evaluated.</li>
     * <li>Attributes (for subject or resource) needed to decide on a request are fetched either from the request or (if
     * not contained in the request) directly from the system.</li> <li>The evaluation results in permission or denial
     * in form of a boolean values.</li> </ul>
     * <p/>
     * The list of boolean evaluation result values are returned with the same cardinality and in the same order as the
     * input data.
     *
     * @param requestsXml The XML representation of the authorization requests corresponding to "requests.xsd".
     * @return Returns the XML representation of the evaluation results corresponding to XML-schema "results.xsd".
     * @throws MissingMethodParameterException
     *                                      Thrown if no role data has been provided.
     * @throws ResourceNotFoundException    Thrown if a resource that needs to be handledd during authorization can not
     *                                      be found.
     * @throws XmlCorruptedException        Thrown if invalid XML is provided.
     * @throws XmlSchemaValidationException Thrown if non schema conform XML is provided.
     * @throws SystemException              Thrown in case of an internal error
     */
    @Override
    String evaluate(String requestsXml) throws MissingMethodParameterException, ResourceNotFoundException,
        SystemException, XmlCorruptedException, XmlSchemaValidationException;

    /**
     * Evaluates the current user's privileges for retrieving the specified objects of the specified resource type.<br>
     * The current user is retrieved from the UserContext.
     *
     * @param resourceName The name identifying the resource. The name of the root element of the resource's XML
     *                     representation ist expected here.<br> This parameter is mandatory.
     * @param ids          The ids of the objects that shall be checked for retrieving them.<br> This parameter is
     *                     mandatory.
     * @return Returns a {@code List} containing the ids of all objects for that the retrieval is allowed.
     * @throws MissingMethodParameterException
     *                                   Thrown if the resource name or the ids are not provided.
     * @throws ResourceNotFoundException Thrown if a resource cannot be found during evaluating the privileges.
     * @throws SystemException           Thrown in case of an internal system error.
     */
    @Override
    List<String> evaluateRetrieve(String resourceName, List<String> ids) throws MissingMethodParameterException,
        ResourceNotFoundException, SystemException;

    /**
     * Evaluates the current user's privileges for calling the specified method for the specified objects of the
     * specified resource type.<br> The current user is retrieved from the UserContext.
     *
     * @param resourceName The name identifying the resource. The name of the root element of the resource's XML
     *                     representation ist expected here.<br> This parameter is mandatory.
     * @param methodName   The name of the method.<br> This parameter is mandatory.
     * @param argumentList List of Object[], each Object[] containing the arguments for one call to the method.<br> This
     *                     parameter is mandatory.
     * @return Returns a {@code List} containing the arguments of all objects for that the retrieval is allowed.
     * @throws MissingMethodParameterException
     *                                   Thrown if the resource name or the ids are not provided.
     * @throws ResourceNotFoundException Thrown if a resource cannot be found during evaluating the privileges.
     * @throws SystemException           Thrown in case of an internal error.
     */
    @Override
    List<Object[]> evaluateMethodForList(String resourceName, String methodName, List<Object[]> argumentList)
        throws MissingMethodParameterException, ResourceNotFoundException, SystemException;

}
