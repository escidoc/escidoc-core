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

import de.escidoc.core.common.util.IOUtils;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xpath.XPathAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * Base methods for XML handling.
 *
 * @author Michael Schneider
 */
public class XMLBase {

    protected static final String ROOT_ELEMENT = "mapping";

    public static final String DOCUMENTATION_ELEMENT = "documentation";

    public static final String TITLE_ELEMENT = "title";

    public static final String DOCUMENTATION_VISIBLE_ATTR = "visible";

    public static final String DOCUMENTATION_AVAILABLE_ATTR = "available";

    public static final String PARAM_ELEMENT = "param";

    public static final String PARAM_NAME_ATTR = "name";

    public static final String RESULT_ELEMENT = "result";

    public static final String RESULT_ATTR_TYPE = "type";

    public static final String ERROR_ELEMENT = "error";

    public static final String REST_ELEMENT = "rest";

    public static final String PREVIOUS_ELEMENT = "previous";

    public static final String DESCRIPTION_ELEMENT = "description";

    protected static final String DEFINITION_ELEMENT = "define";

    protected static final String DEFINITION_VAR_ELEMENT = "var";

    protected static final String DEFINITION_VAR_NAME_ATTR = "name";

    protected static final String DEFINITION_VAR_REGEXP_ATTR = "regexp";

    protected static final String DESCRIPTOR_ELEMENT = "descriptor";

    protected static final String DESCRIPTOR_URI_ATTR = "uri";

    protected static final String RESOURCE_ELEMENT = "resource";

    protected static final String RESOURCE_URI_ATTR = "base-uri";

    protected static final String RESOURCE_NAME_ATTR = "name";

    protected static final String RESOURCE_SERVICE_NAME_ATTR = "service-name";

    protected static final String INVOKE_ELEMENT = "invoke";

    protected static final String INVOKE_HTTP_ATTR = "http";

    protected static final String INVOKE_METHOD_ATTR = "method";

    protected static final String INVOKE_PARAM_ATTR = "param";

    protected static final String XPATH_DELIMITER = "/";

    protected static final String VAR_PREFIX = "${";

    protected static final String VAR_POSTFIX = "}";

    protected static final String VAR_BODY = "BODY";

    protected static final String VAR_QUERY_STRING = "QUERY";

    protected static final String VAR_PARAMETERS = "PARAMETERS";

    public static final String VAR_BODY_METHOD = "BODY.METHOD";

    protected static final String VAR_BODY_LAST_MODIFICATION_DATE = "BODY.LAST-MODIFICATION-DATE";

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLBase.class);

    private static final int BUFFER_SIZE = 0xFFFF;

    /**
     * Apply the xPath to the given dom node and return the resulting list of nodes.
     *
     * @param xPath The xPath.
     * @param node  The node.
     * @return The resulting list of nodes.
     * @throws TransformerException If anything fails.
     */
    public static NodeList parse(final String xPath, final Node node) throws TransformerException {
        return XPathAPI.selectNodeList(node, xPath);
    }

    /**
     * Get the value of the attribute from the given node.
     *
     * @param node      The node.
     * @param attribute The attribute name.
     * @return The value of teh attribute.
     */
    public static String getAttributeValue(final Node node, final String attribute) {

        String result = null;
        if (node != null) {
            final NamedNodeMap attributes = node.getAttributes();
            if (attributes != null) {
                final Node attributeNode = attributes.getNamedItem(attribute);
                if (attributeNode != null) {
                    result = attributeNode.getTextContent();
                }
            }
        }
        return result;
    }

    /**
     * Get a child node from the given node.
     *
     * @param node      The node.
     * @param childName The name of the child node.
     * @return The child node.
     */
    public static Node getChild(final Node node, final String childName) {

        Node result = null;
        try {
            result = XPathAPI.selectSingleNode(node, childName);
        }
        catch (final TransformerException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Child node not found.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Child node not found.", e);
            }
        }
        return result;

    }

    /**
     * Append the path to the given xPath.
     *
     * @param xPath The xPath.
     * @param path  The path.
     * @return The resulting xPath.
     */
    public static String appendToXpath(final String xPath, final String path) {

        String result = xPath;
        if (xPath != null && path != null) {
            result +=
                !xPath.endsWith(XPATH_DELIMITER) && !path.startsWith(XPATH_DELIMITER) ? XPATH_DELIMITER + path : xPath
                    .endsWith(XPATH_DELIMITER)
                    && path.startsWith(XPATH_DELIMITER) ? path.substring(1) : path;
        }
        return result;
    }

    /**
     * Get a dom document from the given file.
     *
     * @param filename The filename.
     * @return The dom document.
     * @throws ParserConfigurationException If anything fails.
     * @throws SAXException                 If anything fails.
     * @throws IOException                  If anything fails.
     */
    public Document getDocument(final String filename) throws ParserConfigurationException, SAXException, IOException {

        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document result = null;
        InputStream inputStream = null;
        try {
            inputStream = getFileInputStream(filename);
            if (inputStream == null) {
                throw new FileNotFoundException("XML file not found [" + filename + ']');
            }
            result = docBuilder.parse(inputStream);
        }
        finally {
            IOUtils.closeStream(inputStream);
        }
        result.getDocumentElement().normalize();
        return result;
    }

    /**
     * Serialize the given {@code org.w3c.Document} to a {@code String}.
     *
     * @param document The document.
     * @return The {@code String} representation of teh document.
     * @throws IOException If the serialization fails.
     */
    public static String getDocumentAsString(final Document document) throws IOException {

        final StringWriter stringOut = new StringWriter();

        // format
        final OutputFormat format = new OutputFormat(document);
        format.setIndenting(true);
        format.setPreserveSpace(false);

        // serialize
        final XMLSerializer serial = new XMLSerializer(stringOut, format);
        serial.asDOMSerializer();
        serial.serialize(document);
        return stringOut.toString();
    }

    /**
     * Get a file input stream for the given filename.
     *
     * @param filename The file name.
     * @return The file input stream.
     */
    public InputStream getFileInputStream(final String filename) {
        return XMLBase.class.getResourceAsStream(filename);
    }

    /**
     * Get the contents of the file as String.
     *
     * @param filename The filename.
     * @return The contents of the file.
     * @throws IOException If anything fails.
     */
    public String getFileContents(final String filename) throws IOException {

        LOGGER.info("looking for file " + filename);

        final InputStream inputStream = getFileInputStream(filename);

        final byte[] buffer = new byte[BUFFER_SIZE];
        int length = inputStream.read(buffer);
        String result = "";
        while (length != -1) {
            result += new String(buffer, 0, length);
            length = inputStream.read(buffer);
        }
        return result;
    }

    /**
     * Save the contents to a file.
     *
     * @param filename The name of the file.
     * @param contents The contents to save.
     * @throws IOException If the save operation fails.
     */
    public static void saveToFile(final String filename, final String contents) throws IOException {

        final File outFile = new File(filename);
        outFile.createNewFile();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outFile);
            fos.write(contents.getBytes());
            fos.flush();
        }
        finally {
            IOUtils.closeStream(fos);
        }
    }

}
