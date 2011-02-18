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

import de.escidoc.core.common.util.logger.AppLogger;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xpath.XPathAPI;
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
 * @author MSC
 * @common
 */
public class XMLBase {
    static final String ROOT_ELEMENT = "mapping";

    protected static final String DOCUMENTATION_ELEMENT = "documentation";

    protected static final String TITLE_ELEMENT = "title";

    protected static final String DOCUMENTATION_VISIBLE_ATTR = "visible";

    protected static final String DOCUMENTATION_AVAILABLE_ATTR = "available";

    protected static final String PARAM_ELEMENT = "param";

    protected static final String PARAM_NAME_ATTR = "name";

    protected static final String RESULT_ELEMENT = "result";

    protected static final String RESULT_ATTR_TYPE = "type";

    public static final String ERROR_ELEMENT = "error";

    public static final String REST_ELEMENT = "rest";

    public static final String SOAP_ELEMENT = "soap";

    public static final String PREVIOUS_ELEMENT = "previous";

    protected static final String DESCRIPTION_ELEMENT = "description";

    static final String DEFINITION_ELEMENT = "define";

    static final String DEFINITION_VAR_ELEMENT = "var";

    static final String DEFINITION_VAR_NAME_ATTR = "name";

    static final String DEFINITION_VAR_REGEXP_ATTR = "regexp";

    protected static final String DESCRIPTOR_ELEMENT = "descriptor";

    protected static final String DESCRIPTOR_URI_ATTR = "uri";

    static final String RESOURCE_ELEMENT = "resource";

    static final String RESOURCE_URI_ATTR = "base-uri";

    static final String RESOURCE_NAME_ATTR = "name";

    static final String RESOURCE_SERVICE_NAME_ATTR = "service-name";

    protected static final String INVOKE_ELEMENT = "invoke";

    protected static final String INVOKE_HTTP_ATTR = "http";

    protected static final String INVOKE_METHOD_ATTR = "method";

    protected static final String INVOKE_PARAM_ATTR = "param";

    protected static final String XPATH_DELIMITER = "/";

    static final String VAR_PREFIX = "${";

    static final String VAR_POSTFIX = "}";

    protected static final String VAR_BODY = "BODY";

    static final String VAR_QUERY_STRING = "QUERY";

    static final String VAR_PARAMETERS = "PARAMETERS";

    public static final String VAR_BODY_METHOD = "BODY.METHOD";

    protected static final String VAR_BODY_LAST_MODIFICATION_DATE =
        "BODY.LAST-MODIFICATION-DATE";

    private static final AppLogger logger = new AppLogger(XMLBase.class.getName());

    private static final int BUFFER_SIZE = 0xFFFF;

    /**
     * Apply the xPath to the given dom node and return the resulting list of
     * nodes.
     * 
     * @param xPath
     *            The xPath.
     * @param node
     *            The node.
     * @return The resulting list of nodes.
     * @throws TransformerException
     *             If anything fails.
     * @common
     */
    protected static final NodeList parse(final String xPath, final Node node)
        throws TransformerException {
        return XPathAPI.selectNodeList(node, xPath);
    }

    /**
     * Get the value of the attribute from the given node.
     * 
     * @param node
     *            The node.
     * @param attribute
     *            The attribute name.
     * @return The value of teh attribute.
     * @common
     */
    protected static final String getAttributeValue(final Node node, final String attribute) {

        String result = null;
        if (node != null) {
            final NamedNodeMap attributes = node.getAttributes();
            if (attributes != null) {
                Node attributeNode = attributes.getNamedItem(attribute);
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
     * @param node
     *            The node.
     * @param childName
     *            The name of the child node.
     * @return The child node.
     */
    protected static final Node getChild(final Node node, final String childName) {

        Node result = null;
        try {
            result = XPathAPI.selectSingleNode(node, childName);
        }
        catch (TransformerException e) {
            getLogger().error("Child node not found!" + e);
        }

        return result;

    }

    /**
     * Append the path to the given xPath.
     * 
     * @param xPath
     *            The xPath.
     * @param path
     *            The path.
     * @return The resulting xPath.
     * @common
     */
    static final String appendToXpath(final String xPath, final String path) {

        String result = xPath;
        if ((xPath != null) && (path != null)) {
            if ((!xPath.endsWith(XPATH_DELIMITER))
                && (!path.startsWith(XPATH_DELIMITER))) {
                result += XPATH_DELIMITER + path;
            }
            else if ((xPath.endsWith(XPATH_DELIMITER))
                && (path.startsWith(XPATH_DELIMITER))) {
                result += path.substring(1);
            }
            else {
                result += path;
            }
        }
        return result;
    }

    /**
     * Get a dom document from the given file.
     * 
     * @param filename
     *            The filename.
     * @return The dom document.
     * @throws ParserConfigurationException
     *             If anything fails.
     * @throws SAXException
     *             If anything fails.
     * @throws IOException
     *             If anything fails.
     * @common
     */
    protected final Document getDocument(final String filename)
        throws ParserConfigurationException, SAXException, IOException {

        Document result = null;
        DocumentBuilderFactory docBuilderFactory =
            DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

        final InputStream inputStream = getFileInputStream(filename);
        if (inputStream == null) {
            throw new FileNotFoundException("XML file not found [" + filename
                + ']');
        }
        try {
            result = docBuilder.parse(inputStream);
        } finally {
            inputStream.close();
        }
        result.getDocumentElement().normalize();
        return result;
    }

    /**
     * Serialize the given <code>org.w3c.Document</code> to a
     * <code>String</code>.
     * 
     * @param document
     *            The document.
     * @return The <code>String</code> representation of teh document.
     * @throws IOException
     *             If the serialization fails.
     */
    public static String getDocumentAsString(final Document document)
        throws IOException {
        String result;

        StringWriter stringOut = new StringWriter();

        // format
        OutputFormat format = new OutputFormat(document);
        format.setIndenting(true);
        format.setPreserveSpace(false);

        // serialize
        XMLSerializer serial = new XMLSerializer(stringOut, format);
        serial.asDOMSerializer();
        serial.serialize(document);
        result = stringOut.toString();

        return result;
    }

    /**
     * Get a file input stream for the given filename.
     * 
     * @param filename
     *            The file name.
     * @return The file input stream.
     * @common
     */
    final InputStream getFileInputStream(final String filename) {
        InputStream result;
        getLogger().debug("getFileInputStream: Looking for file: " + filename);
        result = this.getClass().getResourceAsStream(filename);
        return result;
    }

    /**
     * Get the contents of the file as String.
     * 
     * @param filename
     *            The filename.
     * @return The contents of the file.
     * @throws IOException
     *             If anything fails.
     * @common
     */
    public final String getFileContents(final String filename) throws IOException {
        String result = "";

        getLogger().info("looking for file " + filename);

        InputStream inputStream = getFileInputStream(filename);

        byte[] buffer = new byte[BUFFER_SIZE];
        int length = inputStream.read(buffer);
        while (length != -1) {
            result += new String(buffer, 0, length);
            length = inputStream.read(buffer);
        }
        return result;
    }

    /**
     * Save the contents to a file.
     * 
     * @param filename
     *            The name of the file.
     * @param contents
     *            The contents to save.
     * @throws IOException
     *             If the save operation fails.
     */
    public static void saveToFile(final String filename, final String contents)
        throws IOException {

        File outFile = new File(filename);
        outFile.createNewFile();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outFile);
            fos.write(contents.getBytes());
            fos.flush();
        } finally {
            if(fos != null) {
                try {
                    fos.close();
                } catch(IOException e) {
                    // Ignore exception
                }
            }
        }
    }

    /**
     * @return Returns the logger.
     * @common
     */
    protected static AppLogger getLogger() {
        return logger;
    }
}
