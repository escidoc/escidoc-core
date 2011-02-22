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

import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.servlet.invocation.exceptions.MethodNotFoundException;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * The method mapper.<br>
 * This mapper provides the resource handler's method that has to be invoked for
 * a specific (sub-) resource URI.<br>
 * All (sub-) resource URIs have the form:<br>
 * /&lt;repositoryname&gt;/&lt;resourcename&gt;/...<br>
 * and &lt;repositoryname&gt; and &lt;resourcename&gt; are simple names, not
 * paths.
 * 
 * @author MSC
 * @common
 */
public class MethodMapper extends XMLBase implements MapperInterface {

    private List<Document> methodMappings = null;

    private Map descriptors = null;

    private Map<String, Collection> definitions =
        new HashMap<String, Collection>();

    private Map<String, Resource> resources = null;

    private Collection<String> descriptorFilenames = null;

    /**
     * Default constructor.
     * 
     * @common
     */
    public MethodMapper() {
    }

    /**
     * Constructor for method mapper.
     * 
     * @param descriptor
     *            Descriptor filename.
     * @throws ParserConfigurationException
     *             If anything fails.
     * @throws SAXException
     *             If anything fails.
     * @throws IOException
     *             If anything fails.
     * @throws TransformerException
     *             Thrown if an xml transformation fails.
     * @common
     */
    public MethodMapper(final String descriptor)
        throws ParserConfigurationException, SAXException, IOException,
        TransformerException {

        Collection<String> paths = new ArrayList<String>();
        paths.add(descriptor);
        setDescriptorFilenames(paths);
    }

    /**
     * Constructor for method mapper.
     * 
     * @param descriptors
     *            Descriptor filenames.
     * @throws ParserConfigurationException
     *             If anything fails.
     * @throws SAXException
     *             If anything fails.
     * @throws IOException
     *             If anything fails.
     * @throws TransformerException
     *             Thrown if an xml transformation fails.
     * @common
     */
    public MethodMapper(final Collection<String> descriptors)
        throws ParserConfigurationException, SAXException, IOException,
        TransformerException {

        setDescriptorFilenames(descriptors);
    }

    /**
     * Sets the names of the descriptor files and initializes this method
     * mapper.
     * 
     * @param descriptorFilenames
     *            The <code>Collection</code> containing the paths to the
     *            descriptors.
     * @throws ParserConfigurationException
     *             If anything fails.
     * @throws SAXException
     *             If anything fails.
     * @throws IOException
     *             If anything fails.
     * @throws TransformerException
     *             Thrown if an xml transformation fails.
     * @common
     */
    public void setDescriptorFilenames(
        final Iterable<String> descriptorFilenames)
        throws ParserConfigurationException, SAXException, IOException,
        TransformerException {

        this.descriptorFilenames = new ArrayList<String>();
        for (String descriptor : descriptorFilenames) {
            descriptor = descriptor.trim();
            if (!descriptor.startsWith("/")) {
                this.descriptorFilenames.add('/' + descriptor);
            }
            else {
                this.descriptorFilenames.add(descriptor);
            }
        }
        init();
    }

    /**
     * Initialize the data structures.
     * 
     * @throws ParserConfigurationException
     *             If anything fails.
     * @throws SAXException
     *             If anything fails.
     * @throws IOException
     *             If anything fails.
     * @throws TransformerException
     *             Thrown if an xml transformation fails.
     * @common
     */
    private void init() throws ParserConfigurationException, SAXException,
        IOException, TransformerException {

        Iterator<String> iter = this.descriptorFilenames.iterator();
        this.methodMappings = new ArrayList<Document>();
        while (iter.hasNext()) {
            String filename = iter.next();
            Document document = getDocument(filename);
            if (document != null) {
                this.methodMappings.add(document);
            }
        }
        putDefinitions(DEFINITION_VAR_ELEMENT,
            initDefinitions(DEFINITION_VAR_ELEMENT));
        setResources(initResources());

        // Debug output
        Iterator<Node> defIter =
            getDefinitions(DEFINITION_VAR_ELEMENT).iterator();
        getLogger().debug("Definitions (Variables):");
        while (defIter.hasNext()) {
            Node next = defIter.next();
            getLogger()
                .debug(
                    "Node: " + next.getNodeName() + " name='"
                        + getAttributeValue(next, DEFINITION_VAR_NAME_ATTR)
                        + "', regexp='"
                        + getAttributeValue(next, DEFINITION_VAR_REGEXP_ATTR)
                        + '\'');
        }
        Iterator<String> resIter = getResources().keySet().iterator();
        getLogger().debug("Resources:");
        while (resIter.hasNext()) {
            getLogger().debug(
                getResources().get(resIter.next()).toString());
        }
        getLogger().debug("Finished.");
    }

    /**
     * Initialize the definitions from mappings descriptor.
     * 
     * @param type
     *            The type of definitions.
     * @return The collection of definitions.
     * @throws TransformerException
     *             Thrown if an xml transformation fails.
     * @common
     */
    private Collection<Node> initDefinitions(final String type)
        throws TransformerException {
        Collection<Node> result = new ArrayList<Node>();
        String xPath =
            appendToXpath(appendToXpath(XPATH_DELIMITER + ROOT_ELEMENT,
                DEFINITION_ELEMENT), type);
        for (Document methodMapping1 : methodMappings) {
            Document methodMapping = methodMapping1;
            NodeList methodDefinitions = parse(xPath, methodMapping);
            int noOfDefinitions = methodDefinitions.getLength();
            for (int i = 0; i < noOfDefinitions; ++i) {
                result.add(methodDefinitions.item(i));
            }
        }

        return result;
    }

    /**
     * Initialize the resources.
     * 
     * @return The map of resources.
     * @throws TransformerException
     *             Thrown if an xml transformation fails.
     * @common
     */
    private Map<String, Resource> initResources() throws TransformerException {
        Map<String, Resource> result = new HashMap<String, Resource>();

        String xPath =
            appendToXpath(XPATH_DELIMITER + ROOT_ELEMENT, RESOURCE_ELEMENT);
        for (Document methodMapping : methodMappings) {
            NodeList resourcesNodes = parse(xPath, methodMapping);
            int noOfResources = resourcesNodes.getLength();
            for (int i = 0; i < noOfResources; ++i) {
                Node resource = resourcesNodes.item(i);
                result.put(getAttributeValue(resource, RESOURCE_URI_ATTR),
                    new Resource(resource, getDefinitions()));
            }
        }

        return result;
    }

    /**
     * Retrieve the matching resource method from the HTTP method and the
     * request uri.
     * 
     * @param request
     *            The HTTP request.
     * @return The resource method.
     * @throws MethodNotFoundException
     *             If no matching method is found.
     * @throws EncodingSystemException
     *             e
     * @common
     */
    @Override
    public BeanMethod getMethod(final HttpServletRequest request)
        throws MethodNotFoundException, EncodingSystemException {

        return getMethod(request.getRequestURI(), request.getQueryString(),
            request.getMethod().equals("GET") ? request.getParameterMap()
                : null, request.getMethod(), Resource.getRequestBody(request));
    }

    /**
     * Retrieve the matching resource method from the provided URI, HTTP method
     * and the request body.
     * 
     * @param uri
     *            The request URI.
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
     * @throws EncodingSystemException
     *             e
     * @common
     */
    @Override
    public BeanMethod getMethod(
        final String uri, final String query,
        final Map<String, String[]> parameters, final String httpMethod,
        final Object body) throws MethodNotFoundException,
        EncodingSystemException {
        String decodedUri = null;
        try {
            decodedUri = URLDecoder.decode(uri, XmlUtility.CHARACTER_ENCODING);
        }
        catch (UnsupportedEncodingException e) {
            throw new EncodingSystemException(e);
        }
        Resource resource = getResource(decodedUri);
        if (resource != null) {
            return resource.getMethod(decodedUri, query, parameters,
                httpMethod, body);
        }
        else {
            throw new MethodNotFoundException(
                "Could not identify resource matching uri '" + uri + "'.");
        }

    }

    /**
     * Get the resource corresponding to the request uri.
     * 
     * @param requestUri
     *            The request uri.
     * @return The resource name.
     * @common
     */
    public Resource getResource(final String requestUri) {

        final String uri;
        if (requestUri.endsWith("/")) {
            uri = requestUri;
        }
        else {
            uri = requestUri + '/';
        }
        Resource result = null;
        // FIXME: Remove this iteration. All base-URIs are like
        // /<ir, um, oum, ...>/<resourcename>
        Map<String, Resource> resourcesMap = getResources();
        for (String s : resourcesMap.keySet()) {
            String baseUri = s;
            if (uri.startsWith(baseUri)) {
                result = resourcesMap.get(baseUri);
                break;
            }
        }
        return result;
    }

    /**
     * @return Returns the descriptors.
     * @common
     */
    public Map getDescriptors() {

        return descriptors;
    }

    /**
     * @param descriptors
     *            The descriptors to set.
     * @common
     */
    public void setDescriptors(final Map descriptors) {

        this.descriptors = descriptors;
    }

    /**
     * Get the definitons for the specified type.
     * 
     * @param type
     *            The type.
     * @return The definitions.
     * @common
     */
    public Collection getDefinitions(final String type) {

        return getDefinitions().get(type);
    }

    /**
     * Add definitons for the specified type.
     * 
     * @param type
     *            The type.
     * @param newDefinitions
     *            The collection of definitions.
     * @common
     */
    public void putDefinitions(
        final String type, final Collection<Node> newDefinitions) {

        if (this.definitions == null) {
            this.definitions = new HashMap<String, Collection>();
        }
        this.definitions.put(type, newDefinitions);
    }

    /**
     * @return Returns the definitions.
     * @common
     */
    public Map<String, Collection> getDefinitions() {
        return definitions;
    }

    /**
     * @param definitions
     *            The definitions to set.
     * @common
     */
    public void setDefinitions(final Map<String, Collection> definitions) {

        this.definitions = definitions;
    }

    /**
     * @return Returns the resources.
     * @common
     */
    public Map<String, Resource> getResources() {
        return resources;
    }

    /**
     * @param resources
     *            The resources to set.
     * @common
     */
    public void setResources(final Map<String, Resource> resources) {
        this.resources = resources;
    }
}
