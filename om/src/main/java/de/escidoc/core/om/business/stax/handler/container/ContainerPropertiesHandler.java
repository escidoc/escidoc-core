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
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.naming.directory.NoSuchAttributeException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The PropertiesHandler. Verifies the elements {@code context} and {@code content-model} of a properties
 * snippet of a parsed container xml. It is checked whether referenced context and content-model exist in the framework
 * and whether URLs of a context and content-model are correct. Fetches values of the elements {@code description}
 * and {@code pid} and stores they in a Map.
 */
@Configurable
public class ContainerPropertiesHandler extends DefaultHandler {

    @Autowired
    @Qualifier("business.TripleStoreUtility")
    private TripleStoreUtility tripleStoreUtility;

    @Autowired
    @Qualifier("business.Utility")
    private Utility utility;

    private static final String PROPERTIES_PATH = "/container/properties";

    public static final String PID = "pid";

    private final StaxParser staxParser;

    private final Map<String, String> properties = new HashMap<String, String>();

    private final Collection<String> expectedElements = new ArrayList<String>();

    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerPropertiesHandler.class);

    /**
     * Instantiate a PropertiesHandler.
     *
     * @param parser The parser.
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
     * @param element The element.
     * @return The element.
     * @throws ContentModelNotFoundException  If the content-type is not available.
     * @throws ContextNotFoundException       If the context is not available.
     * @throws MissingAttributeValueException If a required attribute is missing.
     * @throws TripleStoreSystemException     Thrown if TripleStore requests fail.
     * @throws WebserverSystemException       Thrown in case of an internal error.
     */
    @Override
    public StartElement startElement(final StartElement element) throws ContentModelNotFoundException,
        ContextNotFoundException, MissingAttributeValueException, TripleStoreSystemException, WebserverSystemException {

        final String curPath = staxParser.getCurPath();
        final String theName = element.getLocalName();

        if (curPath.startsWith(PROPERTIES_PATH)) {
            if ("properties".equals(theName)) {
                expectedElements.add(Elements.ELEMENT_CONTEXT);
                expectedElements.add(Elements.ELEMENT_CONTENT_MODEL);
            }
            else if (theName.equals(Elements.ELEMENT_CONTEXT)) {
                expectedElements.remove(theName);
                String contextId;
                try {
                    contextId = element.getAttributeValue(null, Elements.ATTRIBUTE_XLINK_OBJID);
                    if (contextId == null || contextId.length() < 1) {
                        throw new MissingAttributeValueException("No context id found.");
                    }
                }
                catch (final NoSuchAttributeException e) {
                    final String href;
                    try {
                        href = element.getAttributeValue(Constants.XLINK_NS_URI, Elements.ATTRIBUTE_XLINK_HREF);
                    }
                    catch (final NoSuchAttributeException e1) {
                        if (LOGGER.isWarnEnabled()) {
                            LOGGER.warn("Error on getting attribute value.");
                        }
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Error on getting attribute value.", e1);
                        }
                        final String att = Elements.ATTRIBUTE_XLINK_HREF;
                        final String refType = Elements.ELEMENT_CONTEXT;
                        final String objType = "container";
                        throw new MissingAttributeValueException("The attribute " + att + " of " + refType
                            + " is missing in " + objType + " for create.", e);
                    }
                    final int indexOfLastSlash = href.lastIndexOf('/');
                    contextId = href.substring(indexOfLastSlash + 1);
                    if (contextId == null || contextId.length() < 1) {
                        throw new MissingAttributeValueException("No context id found.", e);
                    }
                    if (!href.substring(0, indexOfLastSlash + 1).equalsIgnoreCase(Constants.CONTEXT_URL_BASE)) {
                        throw new ContextNotFoundException("The " + Elements.ELEMENT_CONTEXT
                            + " element has a wrong url." + "the url have to look like: " + Constants.CONTEXT_URL_BASE
                            + "[id] ", e);
                    }
                }
                properties.put(theName, contextId);
            }
            else if (theName.equals(Elements.ELEMENT_CONTENT_MODEL)) {
                expectedElements.remove(theName);
                String contextId;
                try {
                    contextId = element.getAttributeValue(null, Elements.ATTRIBUTE_XLINK_OBJID);
                    if (contextId == null || contextId.length() < 1) {
                        throw new MissingAttributeValueException("No content-model id found.");
                    }
                }
                catch (final NoSuchAttributeException e) {
                    final String href;
                    try {
                        href = element.getAttributeValue(Constants.XLINK_NS_URI, Elements.ATTRIBUTE_XLINK_HREF);
                    }
                    catch (final NoSuchAttributeException e1) {
                        if (LOGGER.isWarnEnabled()) {
                            LOGGER.warn("Error on getting attribute value.");
                        }
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Error on getting attribute value.", e1);
                        }
                        final String att = Elements.ATTRIBUTE_XLINK_HREF;
                        final String refType = Elements.ELEMENT_CONTENT_MODEL;
                        final String objType = "container";
                        throw new MissingAttributeValueException("The attribute " + att + " of " + refType
                            + " is missing in " + objType + " for create.", e);
                    }
                    final int indexOfLastSlash = href.lastIndexOf('/');
                    contextId = href.substring(indexOfLastSlash + 1);
                    if (contextId == null || contextId.length() < 1) {
                        throw new MissingAttributeValueException("No content-model id found.", e);
                    }
                    if (!href.substring(0, indexOfLastSlash + 1).equalsIgnoreCase(Constants.CONTENT_MODEL_URL_BASE)) {
                        throw new ContentModelNotFoundException("The " + Elements.ELEMENT_CONTENT_MODEL
                            + " element has a wrong url." + "the url have to look like: "
                            + Constants.CONTENT_MODEL_URL_BASE + "[id] ", e);
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
     * @param element The element.
     * @return The element.
     * @throws ContentModelNotFoundException Thrown if referenced Content Model does not exist
     * @throws ContextNotFoundException      Thrown if referenced Context does not exist
     */
    @Override
    public EndElement endElement(final EndElement element) throws ContentModelNotFoundException,
        ContextNotFoundException, TripleStoreSystemException, IntegritySystemException, WebserverSystemException,
        XmlCorruptedException {
        if (staxParser.getCurPath().equals(PROPERTIES_PATH)) {
            if (!expectedElements.isEmpty()) {
                throw new XmlCorruptedException("One of " + expectedElements.toString() + " missing.");
            }

            String id = properties.get(Elements.ELEMENT_CONTEXT);
            utility.checkIsContext(id);
            String title = this.tripleStoreUtility.getTitle(id);
            if (title != null) {
                properties.put(Elements.ELEMENT_CONTEXT + "-title", title);
            }
            else {
                throw new IntegritySystemException("The title of the " + Elements.ELEMENT_CONTEXT + " with id " + id
                    + " is not set");
            }

            id = properties.get(Elements.ELEMENT_CONTENT_MODEL);
            utility.checkIsContentModel(id);
            title = this.tripleStoreUtility.getTitle(id);
            if (title != null) {
                properties.put(Elements.ELEMENT_CONTENT_MODEL + "-title", title);
            }
            else {
                throw new IntegritySystemException("The title of the " + Elements.ELEMENT_CONTENT_MODEL + " with id "
                    + id + " is not set");
            }

        }
        return element;
    }

    /**
     * Handle the character section of an element.
     *
     * @param s       The contents of the character section.
     * @param element The element.
     * @return The character section.
     */
    @Override
    public String characters(final String s, final StartElement element) throws MissingElementValueException {
        final String curPath = staxParser.getCurPath();
        // String theName = element.getLocalName();
        if (curPath.startsWith(PROPERTIES_PATH)) {
            if ("/container/properties/public-status".equals(curPath)) {
                if (s != null) {
                    properties.put(Elements.ELEMENT_PUBLIC_STATUS, s);
                }
                else {
                    throw new MissingElementValueException("Value of the element " + Elements.ELEMENT_PUBLIC_STATUS
                        + " is missing");
                }
            }
            else if ("/container/properties/pid".equals(curPath)) {
                if (s != null) {
                    properties.put(Elements.ELEMENT_PID, s);
                }
                else {
                    throw new MissingElementValueException("Value of the element " + Elements.ELEMENT_PID
                        + " is missing");
                }
            }
        }
        return s;
    }

}
