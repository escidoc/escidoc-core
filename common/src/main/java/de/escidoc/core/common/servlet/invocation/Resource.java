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

package de.escidoc.core.common.servlet.invocation;

import de.escidoc.core.common.servlet.EscidocServlet;
import de.escidoc.core.common.servlet.invocation.exceptions.MethodNotFoundException;
import de.escidoc.core.common.util.IOUtils;
import de.escidoc.core.common.util.xml.XmlUtility;

import org.esidoc.core.utils.io.EscidocBinaryContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.xpath.XPathAPI;

/**
 * The resource class to map HTTP requests to a configured resource method.
 *
 * @author Michael Schneider
 */
public class Resource extends XMLBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(Resource.class);

    private static final String LIST_DELIMITER = ",";

    /**
     * Buffer size for copying binary content into output stream.
     */
    private static final int BUFFER_SIZE = 0xFFFF;

    private Node resource;

    private String baseUri;

    private String name;

    private Map<String, Node> descriptors;

    private Map definitions;

    private String serviceName;

    private String beanId;

    /**
     * Constructor for Resource object.
     *
     * @param resource    The xml representation of the resource.
     * @param definitions The definitions.
     * @throws TransformerException Thrown if an xml transformation fails.
     */
    public Resource(final Node resource, final Map definitions) throws TransformerException {

        this.resource = resource;
        this.definitions = definitions;
        init();
    }

    /**
     * Retrieve the matching resource method from the provided URI, HTTP method and request body. If more than one
     * resource-method is found for the provided URI, the one with less parameters is taken.
     *
     * @param uri        The URI.
     * @param query      The request Query.
     * @param parameters The request parameters.
     * @param httpMethod The http method.
     * @param body       The body of the request, if any.
     * @return The resource method.
     * @throws MethodNotFoundException If no matching method is found.
     */
    public BeanMethod getMethod(
        final String uri, final String query, final Map<String, String[]> parameters, final String httpMethod,
        final Object body) throws MethodNotFoundException {

        BeanMethod result = null;
        final Set<String> regexps = getDescriptors().keySet();
        for (final String regexp : regexps) {
            if (uri.matches(regexp)) {
                final Node descriptor = getDescriptors().get(regexp);
                final Node invokeNode;
                try {
                    invokeNode = getInvocationDescription(descriptor, httpMethod);
                }
                catch (final TransformerException e) {
                    if (LOGGER.isWarnEnabled()) {
                        LOGGER.warn("Error on getting invocation descriptor.");
                    }
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Error on getting invocation descriptor.", e);
                    }
                    result = null;
                    break;
                }
                if (invokeNode != null) {
                    final String method = getAttributeValue(invokeNode, INVOKE_METHOD_ATTR);
                    final Object[] params = getMethodParameters(uri, query, parameters, body, regexp, invokeNode);
                    if (result != null && result.getParameters() != null
                        && (params == null || result.getParameters().length > params.length) || result == null) {
                        result = new BeanMethod(getBeanId(), method, params);
                    }
                }
            }
        }
        if (result == null) {
            throw new MethodNotFoundException("Error: No method configured in resource '" + getName() + "' for "
                + httpMethod + "-request with uri '" + uri + "'!");
        }
        return result;
    }

    /**
     * Initialize.
     *
     * @throws TransformerException Thrown if an xml transformation fails.
     */
    private void init() throws TransformerException {

        this.baseUri = getAttributeValue(this.resource, RESOURCE_URI_ATTR);
        this.name = getAttributeValue(this.resource, RESOURCE_NAME_ATTR);
        this.serviceName = getAttributeValue(this.resource, RESOURCE_SERVICE_NAME_ATTR);
        if (this.serviceName == null) {
            this.serviceName = this.name + "Handler";
        }
        this.beanId = "service." + this.serviceName;

        this.descriptors = new HashMap<String, Node>();

        String xPath =
            appendToXpath(XPATH_DELIMITER + ROOT_ELEMENT, RESOURCE_ELEMENT + "[@" + RESOURCE_URI_ATTR + "=\""
                + getBaseUri() + "\"]");
        xPath = appendToXpath(xPath, DESCRIPTOR_ELEMENT);
        final NodeList descriptorNodes = parse(xPath, getResource());
        final int noOfDescriptors = descriptorNodes.getLength();
        for (int i = 0; i < noOfDescriptors; ++i) {
            final Node descriptor = descriptorNodes.item(i);
            if (!"".equals(getAttributeValue(descriptor, DESCRIPTOR_URI_ATTR))) {
                final String path =
                    replaceIdentifierToRegexp(getAttributeValue(descriptor, DESCRIPTOR_URI_ATTR),
                        (Iterable<Node>) getDefinitions().get(DEFINITION_VAR_ELEMENT));
                descriptors.put(path, descriptor);
            }
        }

    }

    /**
     * Get the invocation definition corresponding containing the given http method. This method uses the XPathAPI 
     * convenience class, because the XPath-Expressions are generic.
     *
     * @param descriptor The descriptor.
     * @param method     The http method name.
     * @return The invocation definition.
     * @throws TransformerException Thrown if an xml transformation fails.
     */
    private Node getInvocationDescription(final Node descriptor, final String method) throws TransformerException {
        return XPathAPI.selectSingleNode(descriptor, INVOKE_ELEMENT + "[@" + INVOKE_HTTP_ATTR + "=\"" + method + "\"]");
    }

    /**
     * Get the method parameter values from the request uri.
     *
     * @param uri       The uri
     * @param query     The request Query.
     * @param parameters
     * @param body      The http request body
     * @param uriRegexp The uri regexp defining the uri format.
     * @param invoke    The invocation definition.
     * @return An array containing the parameter values.
     */
    private Object[] getMethodParameters(
        final String uri, final String query, final Map<String, String[]> parameters, final Object body,
        final String uriRegexp, final Node invoke) {
        Object[] result = null;
        final Collection<String> paramNames = getMethodParameterNames(invoke);
        if (!paramNames.isEmpty()) {
            result = new Object[paramNames.size()];
            for (int i = 0; i < paramNames.size(); ++i) {
                Object value;
                final String param = (String) ((List) paramNames).get(i);
                if (param.equals(VAR_PREFIX + VAR_BODY + VAR_POSTFIX)) {
                    value = body;
                }
                else if (param.equals(VAR_PREFIX + VAR_QUERY_STRING + VAR_POSTFIX)) {
                    value = query;
                }
                else if (param.equals(VAR_PREFIX + VAR_PARAMETERS + VAR_POSTFIX)) {
                    value = parameters;
                }
                else if (param.equals(VAR_PREFIX + VAR_BODY_LAST_MODIFICATION_DATE + VAR_POSTFIX)) {
                    value =
                        getValueFromRequestBody((String) body, replaceIdentifierToRegexp(param,
                            (Iterable<Node>) getDefinitions().get(DEFINITION_VAR_ELEMENT)));
                }
                else {
                    try {
                        value = uri.replaceAll(uriRegexp, "$" + (i + 1));
                        if ("".equals(value)) {
                            value = null;
                        }
                    }
                    catch (final IndexOutOfBoundsException e) { // TODO: Refactor this! Don't use exceptions for control flow!
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
     * @param invoke The invocation definition.
     * @return A collection containing the names of the method parameters.
     */
    private Collection getMethodParameterNames(final Node invoke) {

        final Collection<String> result = new ArrayList<String>();
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
     * Replace identifier or constant names in the xPath with the configured regular expression.
     *
     * @param xPath          The xPath.
     * @param varDefinitions All definitions of variables or constants including the regular expressions.
     * @return The resulting xPath.
     */
    private String replaceIdentifierToRegexp(final String xPath, final Iterable<Node> varDefinitions) {
        String result = xPath.replaceAll("\\?", "\\\\?");

        for (final Node varDefinition : varDefinitions) {
            final String varName = getAttributeValue(varDefinition, DEFINITION_VAR_NAME_ATTR);
            final String regexp = getAttributeValue(varDefinition, DEFINITION_VAR_REGEXP_ATTR);
            if (result.contains('/' + VAR_PREFIX + varName + VAR_POSTFIX)) {
                result = result.replace('/' + VAR_PREFIX + varName + VAR_POSTFIX, regexp);
            }
        }
        return result;
    }

    /**
     * Get a value from the request body. The key=value pairs in the body must be given as a comma separated list.
     *
     * @param body The http body.
     * @param key  The key.
     * @return The value or null.
     */
    private static String getValueFromRequestBody(final String body, final String key) {

        if (body == null) {
            return null;

        }

        String result = null;
        final StringTokenizer bodyTokenizer = new StringTokenizer(body, LIST_DELIMITER);
        while (bodyTokenizer.hasMoreTokens()) {
            final String token = bodyTokenizer.nextToken().trim();
            if (token.startsWith(key)) {
                result = token.substring(token.indexOf('=') + 1);
            }
        }
        return result;
    }

    /**
     * Get the request body as String.
     *
     * @param request The request.
     * @return The request body.
     */
    public static Object getRequestBody(final HttpServletRequest request) {

        Object result = null;
        try {
            final InputStream is = request.getInputStream();

            // FIXME: Hack for staging-file. Must be solved by descriptor
            if ("PUT".equals(request.getMethod()) && request.getRequestURI().contains("staging-file")) {
                final EscidocBinaryContent binaryContent = new EscidocBinaryContent();
                binaryContent.setMimeType(request.getHeader(EscidocServlet.HTTP_HEADER_CONTENT_TYPE));
                // TODO: extract FileName
                binaryContent.setFileName("Unknown");
                binaryContent.setContent(request.getInputStream());
                result = binaryContent;
            }
            else {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                try {
                    int length;
                    final byte[] buffer = new byte[BUFFER_SIZE];
                    while ((length = is.read(buffer)) != -1) {
                        out.write(buffer, 0, length);
                    }
                    if (out.size() > 0) {
                        result = new String(out.toByteArray(), XmlUtility.CHARACTER_ENCODING);
                    }
                }
                finally {
                    IOUtils.closeStream(out);
                }
            }
        }
        catch (final IOException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("No request body found.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No request body found.", e);
            }
        }
        return result;
    }

    /**
     * @return Returns the descriptors.
     */
    public Map<String, Node> getDescriptors() {
        return this.descriptors;
    }

    /**
     * @param descriptors The descriptors to set.
     */
    public void setDescriptors(final Map<String, Node> descriptors) {
        this.descriptors = descriptors;
    }

    /**
     * @return Returns the resource.
     */
    public Node getResource() {
        return this.resource;
    }

    /**
     * @param resource The resource to set.
     */
    public void setResource(final Node resource) {
        this.resource = resource;
    }

    /**
     * @return Returns the baseUri.
     */
    public String getBaseUri() {
        return this.baseUri;
    }

    /**
     * @param baseUri The baseUri to set.
     */
    public void setBaseUri(final String baseUri) {
        this.baseUri = baseUri;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return Returns the definitions.
     */
    public Map getDefinitions() {
        return this.definitions;
    }

    /**
     * @param definitions The definitions to set.
     */
    public void setDefinitions(final Map definitions) {
        this.definitions = definitions;
    }

    /**
     * For debugging purposes.
     *
     * @return Message
     */
    @Override
    public String toString() {
        return "Resource name='" + getName() + "', base-uri='" + getBaseUri() + "' has descriptors for uris '"
            + getDescriptors().keySet() + "'.";
    }

    /**
     * @return the beanName
     */
    public String getBeanId() {
        return this.beanId;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return this.serviceName;
    }
}
