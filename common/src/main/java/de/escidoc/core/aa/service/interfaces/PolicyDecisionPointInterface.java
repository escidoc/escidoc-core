/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
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

package de.escidoc.core.aa.service.interfaces;

import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;

import java.util.List;
import java.util.Map;

/**
 * The Authentication/Authorization (AA) component is responsible for allowing
 * or preventing access to functionality as well as data provided by the
 * framework.
 * 
 * @author ROF
 * 
 */
public interface PolicyDecisionPointInterface {

    /**
     * The method evaluates the provided authorization requests. <br/>
     * Information about user(s), action(s) and resource(s) are contained in the
     * provided XML data string which represents the content of a
     * {@link com.sun.xacml.ctx.RequestCtx} objects.<br/>
     * 
     * <b>Prerequisites:</b><br/>
     * 
     * The provided XML data in the body is only accepted if the size is less
     * than ESCIDOC_MAX_XML_SIZE.<br/>
     * 
     * At least one authorization request must be specified.<br/>
     * 
     * Each authorization request must contain the specification of the
     * resource-id attribute. In case of a "create" action this attribute should
     * be the empty string.<br/>
     * 
     * If the addressed object is an object under version control, e.g.
     * container or item, the resource id may consist of object id and the
     * version number. In this case, the privileges are evaluated for the
     * specified version. Otherwise, the privileges are evaluated for the latest
     * version, as this is the version that may be modified by accesses to the
     * base services.<br/>
     * 
     * <b>Tasks:</b><br/>
     * <ul>
     * <li>The XML data is validated against the XML-Schema of authorization
     * requests.</li>
     * <li>It is checked that within each request either the resource-id
     * (urn:oasis:names:tc:xacml:1.0:resource:resource-id) or the new
     * object-type (info:escidoc/names:aa:1.0:resource:object-type-new)
     * attribute has been provided.</li>
     * <li>It is checked that no values are provided for eScidoc PDP attributes
     * that are (partly) resolved by the policy decision point.</li>
     * <li>The values of subject attributes and resource attributes that are
     * needed for evaluating the authorization requests are determined.</li>
     * <li>The authorization requests are evaluated using the determined
     * attribute values and the provided attribute values that could not be
     * determined, automatically.</li>
     * <li>The XML representation of the evaluation results corresponding to
     * XML-schema is returned as output. This result list has the same
     * cardinality and the same order as the input data.</li>
     * </ul>
     * 
     * <b>Parameter for Request:</b> (example)<br/>
     * 
     * <xi:include href="requests-example.xml" parse="text"></xi:include>
     * 
     * <pre>
     * &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
     * </pre>
     * 
     * <pre>
     * &lt;requests:requests
     * </pre>
     * 
     * <pre>
     *   xmlns:requests=&quot;http://www.escidoc.de/schemas/pdp/0.2/requests&quot;
     * </pre>
     * 
     * <pre>
     *   xmlns:xacml-context=&quot;urn:oasis:names:tc:xacml:1.0:context&quot;&gt;
     * </pre>
     * 
     * <pre>
     *   &lt;xacml-context:Request&gt;
     * </pre>
     * 
     * <pre>
     *     &lt;xacml-context:Subject
     * </pre>
     * 
     * <pre>
     *       SubjectCategory=
     * </pre>
     * 
     * <pre>
     *         &quot;urn:oasis:names:tc:xacml:1.0:subject-category:access-subject&quot;&gt;
     * </pre>
     * 
     * <pre>
     *       &lt;xacml-context:Attribute
     * </pre>
     * 
     * <pre>
     * AttributeId = &quot;urn:oasis:names:tc:xacml:1.0:subject:subject-id&quot;
     * </pre>
     * 
     * <pre>
     *         DataType=&quot;http://www.w3.org/2001/XMLSchema#string&quot;&gt;
     * </pre>
     * 
     * <pre>
     *         &lt;xacml-context:AttributeValue&gt;
     * </pre>
     * 
     * <pre>
     *           escidoc:user1
     * </pre>
     * 
     * <pre>
     *         &lt;/xacml-context:AttributeValue&gt;
     * </pre>
     * 
     * <pre>
     *       &lt;/xacml-context:Attribute&gt;
     * </pre>
     * 
     * <pre>
     *     &lt;/xacml-context:Subject&gt;
     * </pre>
     * 
     * <pre>
     *     &lt;xacml-context:Resource&gt;
     * </pre>
     * 
     * <pre>
     *       &lt;xacml-context:Attribute
     * </pre>
     * 
     * <pre>
     * AttributeId = &quot;urn:oasis:names:tc:xacml:1.0:resource:resource-id&quot;
     * </pre>
     * 
     * <pre>
     *         DataType=&quot;http://www.w3.org/2001/XMLSchema#string&quot;&gt;
     * </pre>
     * 
     * <pre>
     *         &lt;xacml-context:AttributeValue&gt;
     * </pre>
     * 
     * <pre>
     *           escidoc:1
     * </pre>
     * 
     * <pre>
     *         &lt;/xacml-context:AttributeValue&gt;
     * </pre>
     * 
     * <pre>
     *       &lt;/xacml-context:Attribute&gt;
     * </pre>
     * 
     * <pre>
     *     &lt;/xacml-context:Resource&gt;
     * </pre>
     * 
     * <pre>
     *     &lt;xacml-context:Action&gt;
     * </pre>
     * 
     * <pre>
     *       &lt;xacml-context:Attribute
     * </pre>
     * 
     * <pre>
     * AttributeId = &quot;urn:oasis:names:tc:xacml:1.0:action:action-id&quot;
     * </pre>
     * 
     * <pre>
     *         DataType=&quot;http://www.w3.org/2001/XMLSchema#string&quot;&gt;
     * </pre>
     * 
     * <pre>
     *         &lt;xacml-context:AttributeValue&gt;
     * </pre>
     * 
     * <pre>
     *           info:escidoc/names:aa:1.0:action:retrieve-item
     * </pre>
     * 
     * <pre>
     *         &lt;/xacml-context:AttributeValue&gt;
     * </pre>
     * 
     * <pre>
     *       &lt;/xacml-context:Attribute&gt;
     * </pre>
     * 
     * <pre>
     *     &lt;/xacml-context:Action&gt;
     * </pre>
     * 
     * <pre>
     *   &lt;/xacml-context:Request&gt;
     * </pre>
     * 
     * <pre>
     *   &lt;xacml-context:Request&gt;
     * </pre>
     * 
     * <pre>
     *     &lt;xacml-context:Subject
     * </pre>
     * 
     * <pre>
     *       SubjectCategory=
     * </pre>
     * 
     * <pre>
     *         &quot;urn:oasis:names:tc:xacml:1.0:subject-category:access-subject&quot;&gt;
     * </pre>
     * 
     * <pre>
     *       &lt;xacml-context:Attribute
     * </pre>
     * 
     * <pre>
     * AttributeId = &quot;urn:oasis:names:tc:xacml:1.0:subject:subject-id&quot;
     * </pre>
     * 
     * <pre>
     *         DataType=&quot;http://www.w3.org/2001/XMLSchema#string&quot;&gt;
     * </pre>
     * 
     * <pre>
     *         &lt;xacml-context:AttributeValue&gt;
     * </pre>
     * 
     * <pre>
     *           escidoc:user42
     * </pre>
     * 
     * <pre>
     *         &lt;/xacml-context:AttributeValue&gt;
     * </pre>
     * 
     * <pre>
     *       &lt;/xacml-context:Attribute&gt;
     * </pre>
     * 
     * <pre>
     *     &lt;/xacml-context:Subject&gt;
     * </pre>
     * 
     * <pre>
     *     &lt;xacml-context:Resource&gt;
     * </pre>
     * 
     * <pre>
     *       &lt;xacml-context:Attribute
     * </pre>
     * 
     * <pre>
     * AttributeId = &quot;info:escidoc/names:aa:1.0:resource:object-type&quot;
     * </pre>
     * 
     * <pre>
     *         DataType=&quot;http://www.w3.org/2001/XMLSchema#string&quot;&gt;
     * </pre>
     * 
     * <pre>
     *         &lt;xacml-context:AttributeValue&gt;
     * </pre>
     * 
     * <pre>
     * item
     * </pre>
     * 
     * <pre>
     *         &lt;/xacml-context:AttributeValue&gt;
     * </pre>
     * 
     * <pre>
     *       &lt;/xacml-context:Attribute&gt;
     * </pre>
     * 
     * <pre>
     *       &lt;xacml-context:Attribute
     * </pre>
     * 
     * <pre>
     * AttributeId = &quot;urn:oasis:names:tc:xacml:1.0:resource:resource-id&quot;
     * </pre>
     * 
     * <pre>
     *         DataType=&quot;http://www.w3.org/2001/XMLSchema#string&quot;&gt;
     * </pre>
     * 
     * <pre>
     *         &lt;xacml-context:AttributeValue&gt;&lt;/xacml-context:AttributeValue&gt;
     * </pre>
     * 
     * <pre>
     *       &lt;/xacml-context:Attribute&gt;
     * </pre>
     * 
     * <pre>
     *       &lt;xacml-context:Attribute
     * </pre>
     * 
     * <pre>
     * AttributeId = &quot;info:escidoc/names:aa:1.0:resource:item:context&quot;
     * </pre>
     * 
     * <pre>
     *         DataType=&quot;http://www.w3.org/2001/XMLSchema#string&quot;&gt;
     * </pre>
     * 
     * <pre>
     *         &lt;xacml-context:AttributeValue&gt;
     * </pre>
     * 
     * <pre>
     *           escidoc:persistent3
     * </pre>
     * 
     * <pre>
     *         &lt;/xacml-context:AttributeValue&gt;
     * </pre>
     * 
     * <pre>
     *       &lt;/xacml-context:Attribute&gt;
     * </pre>
     * 
     * <pre>
     *     &lt;/xacml-context:Resource&gt;
     * </pre>
     * 
     * <pre>
     *     &lt;xacml-context:Action&gt;
     * </pre>
     * 
     * <pre>
     *       &lt;xacml-context:Attribute
     * </pre>
     * 
     * <pre>
     * AttributeId = &quot;urn:oasis:names:tc:xacml:1.0:action:action-id&quot;
     * </pre>
     * 
     * <pre>
     *         DataType=&quot;http://www.w3.org/2001/XMLSchema#string&quot;&gt;
     * </pre>
     * 
     * <pre>
     *         &lt;xacml-context:AttributeValue&gt;
     * </pre>
     * 
     * <pre>
     *           info:escidoc/names:aa:1.0:action:create-item
     * </pre>
     * 
     * <pre>
     *         &lt;/xacml-context:AttributeValue&gt;
     * </pre>
     * 
     * <pre>
     *       &lt;/xacml-context:Attribute&gt;
     * </pre>
     * 
     * <pre>
     *     &lt;/xacml-context:Action&gt;
     * </pre>
     * 
     * <pre>
     *   &lt;/xacml-context:Request&gt;
     * </pre>
     * 
     * <pre>
     * &lt;/requests:requests&gt;
     * </pre>
     * 
     * <br/>
     * <br/>
     * <b>Result:</b> (example)<br/>
     * 
     * <xi:include href="results-example.xml" parse="text"></xi:include>
     * 
     * <pre>
     * &lt;results:results
     * </pre>
     * 
     * <pre>
     *   xmlns:results=&quot;http://www.escidoc.de/schemas/pdp/0.2/results&quot;
     * </pre>
     * 
     * <pre>
     *   xmlns:xacml-context=&quot;urn:oasis:names:tc:xacml:1.0:context&quot;&gt;
     * </pre>
     * 
     * <pre>
     *   &lt;results:result decision=&quot;permit&quot;&gt;
     * </pre>
     * 
     * <pre>
     *     &lt;xacml-context:Response&gt;
     * </pre>
     * 
     * <pre>
     *       &lt;xacml-context:Result&gt;
     * </pre>
     * 
     * <pre>
     *         &lt;xacml-context:Decision&gt;Permit&lt;/xacml-context:Decision&gt;
     * </pre>
     * 
     * <pre>
     *         &lt;xacml-context:Status&gt;
     * </pre>
     * 
     * <pre>
     *           &lt;xacml-context:StatusCode
     * </pre>
     * 
     * <pre>
     *             Value=&quot;urn:oasis:names:tc:xacml:1.0:status:ok&quot;/&gt;
     * </pre>
     * 
     * <pre>
     *         &lt;/xacml-context:Status&gt;
     * </pre>
     * 
     * <pre>
     *       &lt;/xacml-context:Result&gt;
     * </pre>
     * 
     * <pre>
     *     &lt;/xacml-context:Response&gt;
     * </pre>
     * 
     * <pre>
     *   &lt;/results:result&gt;
     * </pre>
     * 
     * <pre>
     *   &lt;results:result decision=&quot;deny&quot;&gt;
     * </pre>
     * 
     * <pre>
     *     &lt;xacml-context:Response&gt;
     * </pre>
     * 
     * <pre>
     *       &lt;xacml-context:Result&gt;
     * </pre>
     * 
     * <pre>
     *         &lt;xacml-context:Decision&gt;NotApplicable&lt;/xacml-context:Decision&gt;
     * </pre>
     * 
     * <pre>
     *         &lt;xacml-context:Status&gt;
     * </pre>
     * 
     * <pre>
     *           &lt;xacml-context:StatusCode
     * </pre>
     * 
     * <pre>
     *             Value=&quot;urn:oasis:names:tc:xacml:1.0:status:ok&quot;/&gt;
     * </pre>
     * 
     * <pre>
     *         &lt;/xacml-context:Status&gt;
     * </pre>
     * 
     * <pre>
     *       &lt;/xacml-context:Result&gt;
     * </pre>
     * 
     * <pre>
     *     &lt;/xacml-context:Response&gt;
     * </pre>
     * 
     * <pre>
     *   &lt;/results:result&gt;
     * </pre>
     * 
     * <pre>
     * &lt;/results:results&gt;
     * </pre>
     * 
     * 
     * @param requestsXml
     *            The XML representation of the authorization requests
     *            corresponding to "requests.xsd".
     * @return Returns the XML representation of the evaluation results
     *         corresponding to XML-schema "results.xsd".
     * @throws ResourceNotFoundException
     *             Thrown if a resource that needs to be handled during
     *             authorization can not be found.
     * @throws XmlCorruptedException
     *             Thrown if the provided xml data is invalid.
     * @throws XmlSchemaValidationException
     *             Thrown if the provided xml data is not schma conform.
     * @throws MissingMethodParameterException
     *             Thrown if no role data has been provided.
     * @throws AuthenticationException
     *             Thrown if the authentication fails due to an invalid provided
     *             eSciDocUserHandle.
     * @throws AuthorizationException
     *             Thrown if the authorization fails.
     * @throws SystemException
     *             Thrown in case of an internal error
     *             
     */
    String evaluate(String requestsXml) throws ResourceNotFoundException,
    XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException;

    /**
     * This method checks the user privileges for the specified requests.<br>
     * For each request to evaluate, the provided {@link List} contains a
     * {@link Map} with the information about user, action and resource.
     * 
     * @param requests
     *            A {@link List} of {@link Map} defining the attributes of the
     *            requests to evaluate.
     * @return Returns an array with a boolean evaluation result for each input
     *         request.
     * @throws ResourceNotFoundException
     *             Thrown if a resource does not exist.
     * @throws XmlCorruptedException
     *             Thrown if invalid XML is provided.
     * @throws MissingMethodParameterException
     *             Thrown if no XML representation of requests is provided.
     * @throws AuthenticationException
     *             Thrown if the authentication fails due to an invalid provided
     *             eSciDocUserHandle.
     * @throws AuthorizationException
     *             Thrown if the authorization fails.
     * @throws SystemException
     *             Thrown in case of an internal system error.
     *             
     */
    boolean[] evaluateRequestList(List<Map<String, String>> requests)
        throws ResourceNotFoundException,
        MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException;

    /**
     * Evaluates the current user's privileges for retrieving the specified
     * objects of the specified resource type.<br>
     * The current user is retrieved from the UserContext.
     * 
     * @param resourceName
     *            The name identifying the resource. The name of the root
     *            element of the resource's XML representation ist expected
     *            here.<br>
     *            This parameter is mandatory.
     * @param ids
     *            The ids of the objects that shall be checked for retrieving
     *            them.<br>
     *            This parameter is mandatory.
     * @return Returns a <code>List</code> containing the ids of all objects for
     *         that the retrieval is allowed.
     * @throws AuthenticationException
     *             Thrown if the authentication fails due to an invalid provided
     *             eSciDocUserHandle.
     * @throws AuthorizationException
     *             Thrown if the authorization fails.
     * @throws MissingMethodParameterException
     *             Thrown if the resource name or the ids are not provided.
     * @throws ResourceNotFoundException
     *             Thrown if a resource cannot be found during evaluating the
     *             privileges.
     * @throws SystemException
     *             Thrown in case of an internal error.
     *             
     */
    List<String> evaluateRetrieve(
        String resourceName, List<String> ids)
        throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, ResourceNotFoundException,
        SystemException;

    /**
     * Evaluates the current user's privileges for calling the specified method
     * for the specified
     * objects of the specified resource type.<br>
     * The current user is retrieved from the UserContext.
     * 
     * @param resourceName
     *            The name identifying the resource. The name of the root
     *            element of the resource's XML representation ist expected
     *            here.<br>
     *            This parameter is mandatory.
     * @param methodName
     *            The name of the method.<br>
     *            This parameter is mandatory.
     * @param argumentList
     *            List of Object[], each Object[] containing 
     *            the arguments for one call to the method.<br>
     *            This parameter is mandatory.
     * @return Returns a <code>List</code> containing the arguments of all objects for
     *         that the retrieval is allowed.
     * @throws AuthenticationException
     *             Thrown if the authentication fails due to an invalid provided
     *             eSciDocUserHandle.
     * @throws AuthorizationException
     *             Thrown if the authorization fails.
     * @throws MissingMethodParameterException
     *             Thrown if the resource name or the ids are not provided.
     * @throws ResourceNotFoundException
     *             Thrown if a resource cannot be found during evaluating the
     *             privileges.
     * @throws SystemException
     *             Thrown in case of an internal error.
     *             
     */
    List<Object[]> evaluateMethodForList(
            String resourceName, 
            String methodName, 
            List<Object[]> argumentList)
        throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, ResourceNotFoundException,
        SystemException;

    /**
     * Workaround to trigger instanziation of business.PolicyDecisionPoint.
     *
     * @throws SystemException
     *             Thrown in case of an internal error.
     */
    void touch() throws SystemException;

}
