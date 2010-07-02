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
package de.escidoc.core.common.servlet.invocation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.escidoc.core.common.servlet.EscidocServlet;
import de.escidoc.core.common.servlet.invocation.exceptions.MethodNotFoundException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.business.fedora.EscidocBinaryContent;

/**
 * The resource class to map HTTP requests to a configured resource method.
 * 
 * @author MSC
 * @common
 */
public class Resource extends XMLBase {

    private static final String LIST_DELIMITER = ",";

    /**
     * Buffer size for copying binary content into output stream.
     */
    private static final int BUFFER_SIZE = 0xFFFF;

    private Node resource = null;

    private String baseUri = null;

    private String name = null;

    private Map<String, Node> descriptors = null;

    private Map definitions = null;

    private String serviceName;

    private String beanId;

    /**
     * Constructor for Resource object.
     * 
     * @param resource
     *            The xml representation of the resource.
     * @param definitions
     *            The definitions.
     * @throws TransformerException
     *             Thrown if an xml transformation fails.
     * @common
     */
    public Resource(final Node resource, final Map definitions)
        throws TransformerException {

        this.resource = resource;
        this.definitions = definitions;
        init();
    }

    /**
     * Retrieve the matching resource method from the provided URI, HTTP method
     * and request body.
     * If more than one resource-method is found for the provided URI,
     * the one with less parameters is taken.
     * 
     * @param uri
     *            The URI.
     * @param query
     *            The request Query.
     * @param parameters
     *            The request parameters.
     * @param httpMethod
     *            The http method.
     * @param body
     *            The body of the request, if any.
     * @return The resource method.
     * @throws MethodNotFoundException
     *             If no matching method is found.
     * @common
     */
    public BeanMethod getMethod(
        final String uri, final String query,
        final Map<String, String[]> parameters, final String httpMethod,
        final Object body) throws MethodNotFoundException {

        BeanMethod result = null;
        Set<String> regexps = getDescriptors().keySet();
        Iterator<String> regexpIter = regexps.iterator();
        while (regexpIter.hasNext()) {
            String regexp = regexpIter.next();
            if (uri.matches(regexp)) {
                Node descriptor = (Node) getDescriptors().get(regexp);
                Node invokeNode;
                try {
                    invokeNode =
                        getInvocationDescription(descriptor, httpMethod);
                }
                catch (TransformerException e) {
                    result = null;
                    break;
                }
                if (invokeNode != null) {
                    String method =
                        getAttributeValue(invokeNode, INVOKE_METHOD_ATTR);
                    Object[] params =
                        getMethodParameters(uri, query, parameters, body,
                            regexp, invokeNode);
                    if ((result != null 
                        && result.getParameters() != null 
                        && (params == null 
                            || result.getParameters().length > params.length))
                        || result == null) {
                        result = new BeanMethod(getBeanId(), method, params);
                    }
                }
            }
        }
        if (result == null) {
            String message =
                "Error: No method configured in resource '" + getName()
                    + "' for " + httpMethod + "-request with uri '" + uri
                    + "'!";
            getLogger().error(message);
            throw new MethodNotFoundException(message);
        }
        return result;
    }

    /**
     * Initialize.
     * 
     * @throws TransformerException
     *             Thrown if an xml transformation fails.
     * @common
     */
    private void init() throws TransformerException {

        this.baseUri = getAttributeValue(this.resource, RESOURCE_URI_ATTR);
        this.name = getAttributeValue(this.resource, RESOURCE_NAME_ATTR);
        this.serviceName =
            getAttributeValue(this.resource, RESOURCE_SERVICE_NAME_ATTR);
        if (this.serviceName == null) {
            this.serviceName = this.name + "Handler";
        }
        this.beanId = "service." + this.serviceName + "Bean";

        this.descriptors = new HashMap<String, Node>();

        String xPath =
            appendToXpath(XPATH_DELIMITER + ROOT_ELEMENT, RESOURCE_ELEMENT
                + "[@" + RESOURCE_URI_ATTR + "=\"" + getBaseUri() + "\"]");
        xPath = appendToXpath(xPath, DESCRIPTOR_ELEMENT);
        NodeList descriptorNodes = parse(xPath, getResource());
        int noOfDescriptors = descriptorNodes.getLength();
        for (int i = 0; i < noOfDescriptors; ++i) {
            Node descriptor = descriptorNodes.item(i);
            if (!"".equals(getAttributeValue(descriptor, DESCRIPTOR_URI_ATTR))) {
                String path =
                    replaceIdentifierToRegexp(getAttributeValue(descriptor,
                        DESCRIPTOR_URI_ATTR), (Collection) getDefinitions()
                        .get(DEFINITION_VAR_ELEMENT));
                descriptors.put(path, descriptor);
            }
        }

    }

    /**
     * Get the invocation definition corresponding containing the given http
     * method.
     * 
     * @param descriptor
     *            The descriptor.
     * @param method
     *            The http method name.
     * @return The invocation definition.
     * @throws TransformerException
     *             Thrown if an xml transformation fails.
     * @common
     */
    private Node getInvocationDescription(
        final Node descriptor, final String method) throws TransformerException {
        Node result = null;
        String xPath =
            INVOKE_ELEMENT + "[@" + INVOKE_HTTP_ATTR + "=\"" + method + "\"";

        xPath += "]";
        NodeList nodes = parse(xPath, descriptor);
        if (nodes.getLength() == 1) {
            result = nodes.item(0);
        }
        return result;
    }

    /**
     * Get the method parameter values from the request uri.
     * 
     * @param uri
     *            The uri
     * @param query
     *            The request Query.
     * @param body
     *            The http request body
     * @param uriRegexp
     *            The uri regexp defining the uri format.
     * @param invoke
     *            The invocation definition.
     * 
     * @return An array containing the parameter values.
     * @common
     */
    private Object[] getMethodParameters(
        final String uri, final String query,
        final Map<String, String[]> parameters, final Object body,
        final String uriRegexp, final Node invoke) {
        Object[] result = null;
        Collection<String> paramNames = getMethodParameterNames(invoke);
        if (paramNames.size() > 0) {
            result = new Object[paramNames.size()];
            for (int i = 0; i < paramNames.size(); ++i) {
                String replace = uriRegexp;
                Object value = null;
                String param = (String) ((Vector) paramNames).get(i);
                if (param.equals(VAR_PREFIX + VAR_BODY + VAR_POSTFIX)) {
                    value = body;
                }
                else if (param.equals(VAR_PREFIX + VAR_QUERY_STRING
                    + VAR_POSTFIX)) {
                    value = query;
                }
                else if (param
                    .equals(VAR_PREFIX + VAR_PARAMETERS + VAR_POSTFIX)) {
                    value = parameters;
                }
                else if (param.equals(VAR_PREFIX
                    + VAR_BODY_LAST_MODIFICATION_DATE + VAR_POSTFIX)) {
                    value =
                        getValueFromRequestBody((String) body,
                            replaceIdentifierToRegexp(param,
                                (Collection<Node>) getDefinitions().get(
                                    DEFINITION_VAR_ELEMENT)));
                }
                else {
                    try {
                        value = uri.replaceAll(replace, "$" + (i + 1));
                        if ("".equals(value)) {
                            value = null;
                        }
                    }
                    catch (IndexOutOfBoundsException e) {
                        value = null;
                    }
                }
                result[i] = value;
            }
        }
        return result;
    }

    /**
     * Get a collection containing the names of the method parameters.
     * 
     * @param invoke
     *            The invocation definition.
     * @return A collection containing the names of the method parameters.
     * @common
     */
    private Collection getMethodParameterNames(final Node invoke) {

        Collection<String> result = new Vector<String>();
        int i = 1;
        String parameter = getAttributeValue(invoke, INVOKE_PARAM_ATTR + i);
        while (parameter != null) {
            result.add(parameter);
            i += 1;
            parameter = getAttributeValue(invoke, INVOKE_PARAM_ATTR + i);
        }
        return result;
    }

    /**
     * Replace identifier or constant names in the xPath with the configured
     * regular expression.
     * 
     * @param xPath
     *            The xPath.
     * @param varDefinitions
     *            All definitions of variables or constants including the
     *            regular expressions.
     * @return The resulting xPath.
     * @common
     */
    private String replaceIdentifierToRegexp(
        final String xPath, final Collection<Node> varDefinitions) {
        String result = xPath.replaceAll("\\?", "\\\\?");

        Iterator<Node> definitionsIter = varDefinitions.iterator();
        while (definitionsIter.hasNext()) {
            Node var = definitionsIter.next();
            String varName = getAttributeValue(var, DEFINITION_VAR_NAME_ATTR);
            String regexp = getAttributeValue(var, DEFINITION_VAR_REGEXP_ATTR);
            if (result.indexOf("/" + VAR_PREFIX + varName + VAR_POSTFIX) != -1) {
                result =
                    result.replace("/" + VAR_PREFIX + varName + VAR_POSTFIX,
                        regexp);
            }
        }
        return result;
    }

    /**
     * Get a value from the request body. The key=value pairs in the body must
     * be given as a comma separated list.
     * 
     * @param body
     *            The http body.
     * @param key
     *            The key.
     * @return The value or null.
     */
    private String getValueFromRequestBody(final String body, final String key) {

        if (body == null) {
            return null;

        }

        String result = null;
        StringTokenizer bodyTokenizer =
            new StringTokenizer(body, LIST_DELIMITER);
        while (bodyTokenizer.hasMoreTokens()) {
            String token = bodyTokenizer.nextToken().trim();
            if (token.startsWith(key)) {
                result = token.substring(token.indexOf("=") + 1);
            }
        }
        return result;
    }

    /**
     * Get the request body as String.
     * 
     * @param request
     *            The request.
     * @return The request body.
     * @common
     */
    public static Object getRequestBody(final HttpServletRequest request) {

        Object result = null;
        try {
            InputStream is = request.getInputStream();

            // FIXME: Hack for staging-file. Must be solved by descriptor
            if ("PUT".equals(request.getMethod())
                && request.getRequestURI().indexOf("staging-file") != -1) {
                EscidocBinaryContent binaryContent = new EscidocBinaryContent();
                binaryContent.setMimeType(request
                    .getHeader(EscidocServlet.HTTP_HEADER_CONTENT_TYPE));
                // TODO: extract FileName
                binaryContent.setFileName("Unknown");
                binaryContent.setContent(request.getInputStream());
                result = binaryContent;
            }
            else {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[BUFFER_SIZE];
                int length = 0;
                while ((length = is.read(buffer)) != -1) {
                    out.write(buffer, 0, length);
                }
                if (out.size() > 0) {
                    result =
                        new String(out.toByteArray(),
                            XmlUtility.CHARACTER_ENCODING);
                }
            }
        }
        catch (IOException e) {
            getLogger().debug("No request body found!");
        }
        return result;
    }

    /**
     * @return Returns the descriptors.
     * @common
     */
    public Map<String, Node> getDescriptors() {
        return descriptors;
    }

    /**
     * @param descriptors
     *            The descriptors to set.
     * @common
     */
    public void setDescriptors(final Map<String, Node> descriptors) {
        this.descriptors = descriptors;
    }

    /**
     * @return Returns the resource.
     * @common
     */
    public Node getResource() {
        return resource;
    }

    /**
     * @param resource
     *            The resource to set.
     * @common
     */
    public void setResource(final Node resource) {
        this.resource = resource;
    }

    /**
     * @return Returns the baseUri.
     * @common
     */
    public String getBaseUri() {
        return baseUri;
    }

    /**
     * @param baseUri
     *            The baseUri to set.
     * @common
     */
    public void setBaseUri(final String baseUri) {
        this.baseUri = baseUri;
    }

    /**
     * @return Returns the name.
     * @common
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            The name to set.
     * @common
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return Returns the definitions.
     * @common
     */
    public Map getDefinitions() {
        return definitions;
    }

    /**
     * @param definitions
     *            The definitions to set.
     * @common
     */
    public void setDefinitions(final Map definitions) {
        this.definitions = definitions;
    }

    /**
     * For debugging purposes.
     * 
     * @return Message
     * @common
     */
    @Override
    public String toString() {
        return "Resource name='" + getName() + "', base-uri='" + getBaseUri()
            + "' has descriptors for uris '" + getDescriptors().keySet() + "'.";
    }

    /**
     * @return the beanName
     */
    public String getBeanId() {
        return beanId;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }
}
