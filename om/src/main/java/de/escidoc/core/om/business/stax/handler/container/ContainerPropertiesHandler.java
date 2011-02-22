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
package de.escidoc.core.om.business.stax.handler.container;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import javax.naming.directory.NoSuchAttributeException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * The PropertiesHandler. Verifies the elements <code>context</code> and
 * <code>content-model</code> of a properties snippet of a parsed container xml.
 * These elements must contain <code>xlink:href</code> attribute in the REST
 * case and <code>objid</code> attribute in the SOAP case. It is checked whether
 * referenced context and content-model exist in the framework and in the REST
 * case whether REST-URLs of a context and content-model are correct. Fetches
 * values of the elements <code>description</code> and <code>pid</code> and
 * stores they in a Map.
 * 
 * @om
 */
public class ContainerPropertiesHandler extends DefaultHandler {

    public static final String PROPERTIES_PATH = "/container/properties";

    public static final String PID = "pid";

    private StaxParser staxParser = null;

    private final Map<String, String> properties =
        new HashMap<String, String>();

    private final Collection<String> expectedElements = new ArrayList<String>();

    private static final AppLogger log =
        new AppLogger(ContainerPropertiesHandler.class.getName());

    /**
     * Instantiate a PropertiesHandler.
     * 
     * @param parser
     *            The parser.
     * 
     * @om
     */
    public ContainerPropertiesHandler(final StaxParser parser) {

        this.staxParser = parser;
    }

    /**
     * Get the properties of the handler.
     * 
     * @return The properties.
     */
    public Map<String, String> getProperties() {
        return this.properties;
    }

    /**
     * Handle the start of an element.
     * 
     * @param element
     *            The element.
     * @return The element.
     * 
     * @throws ContentModelNotFoundException
     *             If the content-type is not available.
     * @throws ContextNotFoundException
     *             If the context is not available.
     * @throws MissingAttributeValueException
     *             If a required attribute is missing.
     * @throws TripleStoreSystemException
     *             Thrown if TripleStore requests fail.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * 
     * @om
     */
    @Override
    public StartElement startElement(final StartElement element)
        throws ContentModelNotFoundException, ContextNotFoundException,
        MissingAttributeValueException, TripleStoreSystemException,
        WebserverSystemException {

        String curPath = staxParser.getCurPath();
        String theName = element.getLocalName();

        if (curPath.startsWith(PROPERTIES_PATH)) {
            if (theName.equals("properties")) {
                expectedElements.add(Elements.ELEMENT_CONTEXT);
                expectedElements.add(Elements.ELEMENT_CONTENT_MODEL);
            }
            else if (theName.equals(Elements.ELEMENT_CONTEXT)) {
                expectedElements.remove(theName);
                String contextId;
                try {
                    contextId =
                        element.getAttributeValue(null,
                            Elements.ATTRIBUTE_XLINK_OBJID);
                    if (contextId == null || contextId.length() < 1) {
                        throw new MissingAttributeValueException(
                            "No context id found.");
                    }
                }
                catch (NoSuchAttributeException e) {
                    String href;
                    try {
                        href =
                            element.getAttributeValue(Constants.XLINK_NS_URI,
                                Elements.ATTRIBUTE_XLINK_HREF);
                    }
                    catch (NoSuchAttributeException e1) {
                        String att = Elements.ATTRIBUTE_XLINK_OBJID;
                        if (UserContext.isRestAccess()) {
                            att = Elements.ATTRIBUTE_XLINK_HREF;
                        }
                        String refType = Elements.ELEMENT_CONTEXT;
                        String objType = "container";
                        throw new MissingAttributeValueException(
                            "The attribute " + att + " of " + refType
                                + " is missing in " + objType + " for create.",
                            e);
                    }
                    int indexOfLastSlash = href.lastIndexOf('/');
                    contextId = href.substring(indexOfLastSlash + 1);
                    if (contextId == null || contextId.length() < 1) {
                        throw new MissingAttributeValueException(
                            "No context id found.", e);
                    }
                    if (!href
                        .substring(0, indexOfLastSlash + 1).equalsIgnoreCase(
                            Constants.CONTEXT_URL_BASE)) {
                        String message =
                            "The " + Elements.ELEMENT_CONTEXT
                                + " element has a wrong url."
                                + "the url have to look like: "
                                + Constants.CONTEXT_URL_BASE + "[id] ";
                        log.debug(message);
                        throw new ContextNotFoundException(message, e);
                    }
                }
                properties.put(theName, contextId);
            }
            else if (theName.equals(Elements.ELEMENT_CONTENT_MODEL)) {
                expectedElements.remove(theName);
                String contextId;
                try {
                    contextId =
                        element.getAttributeValue(null,
                            Elements.ATTRIBUTE_XLINK_OBJID);
                    if (contextId == null || contextId.length() < 1) {
                        throw new MissingAttributeValueException(
                            "No content-model id found.");
                    }
                }
                catch (NoSuchAttributeException e) {
                    String href;
                    try {
                        href =
                            element.getAttributeValue(Constants.XLINK_NS_URI,
                                Elements.ATTRIBUTE_XLINK_HREF);
                    }
                    catch (NoSuchAttributeException e1) {
                        String att = Elements.ATTRIBUTE_XLINK_OBJID;
                        if (UserContext.isRestAccess()) {
                            att = Elements.ATTRIBUTE_XLINK_HREF;
                        }
                        String refType = Elements.ELEMENT_CONTENT_MODEL;
                        String objType = "container";
                        throw new MissingAttributeValueException(
                            "The attribute " + att + " of " + refType
                                + " is missing in " + objType + " for create.",
                            e);
                    }
                    int indexOfLastSlash = href.lastIndexOf('/');
                    contextId = href.substring(indexOfLastSlash + 1);
                    if ((contextId == null) || contextId.length() < 1) {
                        throw new MissingAttributeValueException(
                            "No content-model id found.", e);
                    }
                    if (!href
                        .substring(0, indexOfLastSlash + 1).equalsIgnoreCase(
                            Constants.CONTENT_MODEL_URL_BASE)) {
                        String message =
                            "The " + Elements.ELEMENT_CONTENT_MODEL
                                + " element has a wrong url."
                                + "the url have to look like: "
                                + Constants.CONTENT_MODEL_URL_BASE + "[id] ";
                        log.debug(message);
                        throw new ContentModelNotFoundException(message, e);
                    }
                }
                properties.put(theName, contextId);
            }
        }

        return element;
    }

    /**
     * Handle the end of an element.
     * 
     * @param element
     *            The element.
     * @return The element.
     * @throws ContentModelNotFoundException
     *             Thrown if referenced Content Model does not exist
     * @throws InvalidXmlException
     *             Thrown if XML is invalid
     * @throws ContextNotFoundException
     *             Thrown if referenced Context does not exist
     * @throws SystemException
     *             Thrown if internal failure occur
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#endElement
     *      (de.escidoc.core.common.util.xml.stax.events.EndElement)
     */
    @Override
    public EndElement endElement(final EndElement element)
        throws ContentModelNotFoundException, InvalidXmlException,
        ContextNotFoundException, SystemException {
        if (staxParser.getCurPath().equals(PROPERTIES_PATH)) {
            if (!expectedElements.isEmpty()) {
                throw new XmlCorruptedException("One of "
                    + expectedElements.toString() + " missing.");
            }

            String id = properties.get(Elements.ELEMENT_CONTEXT);

            final Utility utility = Utility.getInstance();
            utility.checkIsContext(id);
            String title = TripleStoreUtility.getInstance().getTitle(id);
            if (title != null) {
                properties.put(Elements.ELEMENT_CONTEXT + "-title", title);
            }
            else {
                throw new IntegritySystemException("The title of the "
                    + Elements.ELEMENT_CONTEXT + " with id " + id
                    + " is not set");
            }

            id = properties.get(Elements.ELEMENT_CONTENT_MODEL);
            utility.checkIsContentModel(id);
            title = TripleStoreUtility.getInstance().getTitle(id);
            if (title != null) {
                properties
                    .put(Elements.ELEMENT_CONTENT_MODEL + "-title", title);
            }
            else {
                throw new IntegritySystemException("The title of the "
                    + Elements.ELEMENT_CONTENT_MODEL + " with id " + id
                    + " is not set");
            }

        }
        return element;
    }

    /**
     * Handle the character section of an element.
     * 
     * @param s
     *            The contents of the character section.
     * @param element
     *            The element.
     * @return The character section.
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#characters
     *      (java.lang.String,
     *      de.escidoc.core.common.util.xml.stax.events.StartElement)
     * @om
     */
    @Override
    public String characters(final String s, final StartElement element)
        throws MissingElementValueException {
        String curPath = staxParser.getCurPath();
        // String theName = element.getLocalName();
        if (curPath.startsWith(PROPERTIES_PATH)) {
            // Now properties.description is read only element.
            // It value origins from the corresponding element of the escidoc
            // meta data set
            // and is stored in dc data stream as dc.description element as
            // result of a mapping escidoc->dc.
            // if (theName.equals("description")) {
            // if ((s != null)) {
            // properties.put("description", s);
            // }
            // else {
            // getLog().debug(
            // "the value of" + " of the element " + theName
            // + " is missing");
            // throw new MissingElementValueException(
            // "the value of the element " + theName + " is missing");
            // }
            // }

            if (curPath.equals("/container/properties/public-status")) {
                if ((s != null)) {
                    properties.put(Elements.ELEMENT_PUBLIC_STATUS, s);
                }
                else {
                    String msg =
                        "Value of the element "
                            + Elements.ELEMENT_PUBLIC_STATUS + " is missing";
                    log.debug(msg);
                    throw new MissingElementValueException(msg);
                }
            }
            else if (curPath.equals("/container/properties/pid")) {
                if ((s != null)) {
                    properties.put(Elements.ELEMENT_PID, s);
                }
                else {
                    String msg =
                        "Value of the element " + Elements.ELEMENT_PID
                            + " is missing";
                    log.debug(msg);
                    throw new MissingElementValueException(msg);
                }
            }
        }
        return s;
    }

}
