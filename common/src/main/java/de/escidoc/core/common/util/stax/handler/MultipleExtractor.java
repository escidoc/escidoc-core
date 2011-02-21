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
package de.escidoc.core.common.util.stax.handler;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.events.StartElementWithChildElements;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts the subtrees according to the provided pathes from the parsed
 * ByteArrayInputStream containing a xml document. ByteArrayOutputStreams with
 * extracted subtrees will be stored in a Map. A path to a subtree can be
 * provided along with a name of a subtrees root element attribute in order to
 * distinguish between extracted subtrees with the same root elements names.
 * 
 * Extracted subtrees will be stored in a Map with values of the provided
 * attributes as keys. If no attribute is provided, then a key wil be set to the
 * subtrees root element name.
 * 
 * Subtrees with a root elements 'md-record' will be grouped by a submap. The
 * submap has values of the provided attributes as keys. A key 'md-records' of
 * the outside map has this submap as a value.
 * 
 * Subtrees containing element 'component' in a path, will be grouped by another
 * submap. This submap has component ids as keys. Its values are subsubmaps,
 * which are structured analog to the outside map. A key 'components' of the
 * outside map has this submap as a value. The component ids can be set with a
 * method setPids(). Otherwise values of attributes /item/component/@objid will
 * be used.
 * 
 * An additional functionality is a modification of extracted subtrees: via an
 * input parameter of the method 'removeElements' provided subtrees will be
 * removed from extracted subtrees during extraction process. A provided subtree
 * is addressed via the container element of this subtree and a path to the
 * container element in a whole document. The provided container element can
 * contain attributes and/or text to tighten a matching criteria. The following
 * rule is used to remove a subtree matching a provided path from an extracted
 * subtree:
 * 
 * 1. if a provided container element has attributes and/or element text, the
 * business logic checks if a matching subtree element to remove has the same
 * attributes and/oder the same element text 2. if a provided outer subtree
 * element has no attributes/text, the business logic ignores attributes/text in
 * the matching element.
 * 
 * @author ROF, FRA
 * 
 */
public class MultipleExtractor extends WriteHandler {

    private boolean inside = false;

    private boolean insideRemoveElement = false;

    private Map<String, String> pathes = null;

    private int insideLevel = 0;

    private Map<String, ByteArrayOutputStream> metadata = null;

    private Map components = null;

    private final Map<String, Object> outputStreams =
        new HashMap<String, Object>();

    private String componentId = null;

    private boolean inComponent = false;

    private final StaxParser parser;

    // private String mdNameValue = null;

    private int number = 0;

    private List<String> pids = null;

    private static final Pattern PATTERN_OBJID_IN_HREF =
        Pattern.compile(".*\\/([^\"\\/]*)");

    private Map<String, List<StartElementWithChildElements>> removeElements =
        null;

    private boolean isMatchedAttribute = false;

    private boolean isMatchedText = false;

    private StartElementWithChildElements elementToDelete = null;

    private static final AppLogger LOGGER =
        new AppLogger(MultipleExtractor.class.getName());

    /**
     * Creates a instance of MultipleExtractor.
     * 
     * @param extractPathes
     *            Paths subtree to extract as key and subtree root element
     *            attribute name or null as values
     * @param parser
     *            The parser this handler is added to.
     */
    public MultipleExtractor(final String extractPathes, final StaxParser parser) {

        this.parser = parser;
        this.pathes = new HashMap<String, String>();
        this.pathes.put(extractPathes, null);
    }

    /**
     * Creates a instance of MultipleExtractor.
     * 
     * @param extractPathes
     *            Map with pathes to subtrees to extract as keys and subtrees
     *            root elements attribute name or null as values
     * @param parser
     *            The parser this handler is added to.
     */
    public MultipleExtractor(final Map<String, String> extractPathes,
        final StaxParser parser) {
        this.parser = parser;
        this.pathes = extractPathes;
    }

    /**
     * Creates a instance of MultipleExtractor.
     * 
     * @param extractPath
     *            path to subtree to extract
     * @param extractAtt
     *            attribute name of the subtrees root element
     * @param parser
     *            The parser this handler is added to.
     */
    public MultipleExtractor(final String extractPath, final String extractAtt,
        final StaxParser parser) {
        this.parser = parser;
        this.pathes = new HashMap<String, String>();
        this.pathes.put(extractPath, extractAtt);
    }

    /**
     * Retrieves a Map with extracted subtrees.
     * 
     * @return outputStreams
     */
    public final Map<String, Object> getOutputStreams() {
        return this.outputStreams;
    }

    /**
     * Sets component ids.
     * 
     * @param pids
     *            List with ids of components.
     */
    public final void setPids(final List<String> pids) {
        this.pids = pids;
    }

    /**
     * Map of elements which are to remove from XML tree.
     * 
     * @param elements
     *            Elements which are to remove.
     */
    public final void removeElements(
            final Map<String, List<StartElementWithChildElements>> elements) {
        // TODO extend this to List<StartElement>
        this.removeElements = elements;
    }

    /**
     * See Interface for functional description.
     * 
     * @throws WebserverSystemException
     *             If an error occured writing XML data.
     * 
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#startElement
     *      (de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public final StartElement startElement(final StartElement element)
        throws InvalidContentException, WebserverSystemException {
        NamespaceContext nscontext = element.getNamespaceContext();
        this.increaseDeepLevel();
        String currentPath = parser.getCurPath();
        String theName = element.getLocalName();
        if (this.insideRemoveElement) {
            return element;
        }
        else {
            if (((this.removeElements != null)
                && (!this.removeElements.isEmpty()))
                && (this.removeElements.containsKey(currentPath))) {
                List<StartElementWithChildElements> elementsToDelete =
                    removeElements.get(currentPath);
                Iterator<StartElementWithChildElements> iterator =
                    elementsToDelete.iterator();
                this.elementToDelete = null;
                loop1: while (iterator.hasNext()) {
                    this.elementToDelete = iterator.next();
                    // September 2009, changed logic: if prefix or namespace
                    // of elementToDelete is null it is not compared with
                    // the current element and handled as match. (FRS)
                    if ((elementToDelete.getPrefix() == null || elementToDelete
                        .getPrefix().equals(element.getPrefix()))
                        && (elementToDelete.getNamespace() == null || elementToDelete
                            .getNamespace().equals(element.getNamespace()))) {

                        int attCount2 = elementToDelete.getAttributeCount();
                        if (attCount2 == 0) {
                            // if a provided element to remove does not
                            // contain
                            // attributes
                            // all matched elements will be removed
                            // independent from attribute number and their
                            // values
                            // but we check at least the prefix (namespace)!

                            this.isMatchedAttribute = true;
                            iterator.remove();
                            break loop1;

                        }
                        else {
                            int attCount1 = element.getAttributeCount();

                            if (attCount1 == attCount2) {
                                int matchedAttributesNumber = 0;
                                for (int i = 0; i < attCount1; i++) {
                                    Attribute curAtt =
                                        element.getAttribute(i);
                                    String curName = curAtt.getLocalName();
                                    String curNameSpace =
                                        curAtt.getNamespace();
                                    String curValue = curAtt.getValue();
                                    for (int j = 0; j < attCount2; j++) {
                                        Attribute attToDelete =
                                            elementToDelete.getAttribute(j);
                                        String nameToDelete =
                                            attToDelete.getLocalName();
                                        String nameSpaceToDelete =
                                            attToDelete.getNamespace();
                                        String valueToDelete =
                                            attToDelete.getValue();
                                        if (curName.equals(nameToDelete)
                                            && curNameSpace
                                                .equals(nameSpaceToDelete)
                                            && curValue
                                                .equals(valueToDelete)) {

                                            // i = attCount1;
                                            // break;
                                            matchedAttributesNumber++;
                                        }
                                    }
                                    if (matchedAttributesNumber == attCount1) {
                                        this.isMatchedAttribute = true;
                                        iterator.remove();
                                        break loop1;

                                    }
                                }

                            }
                        }
                    }
                }
                if (isMatchedAttribute) {
                    removeElements.put(currentPath, elementsToDelete);
                    this.insideRemoveElement = true;
                    isMatchedAttribute = false;
                    return element;
                }

            }
        }
        if (theName.equals("component")
            && element
                .indexOfAttribute(Constants.RDF_NAMESPACE_URI, "resource") < 0
            && element.indexOfAttribute(null, "inherited") < 0) {
            inComponent = true;
            if (pids != null) {
                componentId = pids.get(number);
                number++;
            }
            else {
                int indexOfObjid = element.indexOfAttribute(null, "objid");
                if (indexOfObjid != -1) {
                    String value =
                        element.getAttribute(indexOfObjid).getValue();
                    if ((value != null) && (value.length() > 0)) {
                        componentId = value;
                    }
                }
                int indexOfHref =
                    element.indexOfAttribute(Constants.XLINK_URI, "href");
                if (indexOfHref != -1) {
                    String value = element.getAttribute(indexOfHref).getValue();
                    if ((value != null) && (value.length() > 0)) {

                        Matcher m1 = PATTERN_OBJID_IN_HREF.matcher(value);
                        if (m1.find()) {
                            componentId = m1.group(1);
                        }
                    }
                }

                // FIXME workaround issue 630
                if (components != null && components.containsKey(componentId)) {
                    throw new InvalidContentException(
                        "Found component ID twice.");
                }

            }
        }

        try {
            if (inside) {

                writeElement(element);

                int attCount = element.getAttributeCount();
                for (int i = 0; i < attCount; i++) {
                    Attribute curAtt = element.getAttribute(i);
                    handleAttributeInInsideElement(curAtt, nscontext, theName);
                }
                insideLevel++;
            }
            else {
                if ((pathes.containsKey(currentPath))
                    && (element.indexOfAttribute(null, "inherited") < 0)) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();

                    this.setWriter(XmlUtility.createXmlStreamWriter(out));
                    String attributeName = pathes.get(currentPath);

                    inside = true;
                    // create and initialize namespace map
                    this.setNsuris(new HashMap<String, List>());
                    List namespaceTrace = new ArrayList();
                    namespaceTrace.add(-1);
                    namespaceTrace.add("");
                    namespaceTrace.add("xml");
                    this.getNsuris().put("http://www.w3.org/XML/1998/namespace",
                        namespaceTrace);
                    namespaceTrace = new ArrayList();
                    namespaceTrace.add(-1);
                    namespaceTrace.add("");
                    namespaceTrace.add("xmlns");
                    this.getNsuris().put("http://www.w3.org/2000/xmlns/",
                        namespaceTrace);
                    // initialized namespace map

                    if (!theName.equals("md-record")
                        && !theName.equals("admin-descriptor")) {
                        writeElement(element);
                    }
                    String attributeValue = null;
                    int attCount = element.getAttributeCount();
                    for (int i = 0; i < attCount; i++) {
                        Attribute curAtt = element.getAttribute(i);
                        String currentAttributeValue =
                            handleAttributeInOutsideElement(curAtt,
                                nscontext, theName, attributeName);
                        if (currentAttributeValue != null) {
                            attributeValue = currentAttributeValue;
                        }
                    }

                    HashMap component;
                    HashMap<String, ByteArrayOutputStream> mdRecords;
                    if (inComponent) {
                        if (components == null) {
                            components = new HashMap();
                            outputStreams.put("components", components);
                        }
                        if (!components.containsKey(componentId)) {
                            component = new HashMap();

                            components.put(componentId, component);
                        }
                        else {
                            component =
                                (HashMap) components.get(componentId);
                        }

                        if (attributeName == null) {

                            component.put(theName, out);
                        }
                        else {
                            if (theName.equals("md-record")) {

                                if (!component.containsKey("md-records")) {
                                    mdRecords =
                                        new HashMap<String, ByteArrayOutputStream>();
                                    component.put("md-records", mdRecords);
                                }
                                else {
                                    mdRecords =
                                        (HashMap<String, ByteArrayOutputStream>) component
                                            .get("md-records");
                                }
                                if (mdRecords.containsKey(attributeValue)) {
                                    String message =
                                        "A component md-record with the name '"
                                            + attributeValue
                                            + "' occurs multiple times in the representation"
                                            + " of a component.";
                                    LOGGER.error(message);
                                    throw new InvalidContentException(
                                        message);

                                }
                                else {
                                    mdRecords.put(attributeValue, out);
                                }
                            }
                            else {
                                component.put(attributeValue, out);
                            }
                        }
                    }
                    else {
                        if (attributeName == null) {
                            outputStreams.put(theName, out);
                        }
                        else {
                            if (theName.equals("md-record")) {
                                if (metadata == null) {
                                    metadata =
                                        new HashMap<String, ByteArrayOutputStream>();
                                    outputStreams.put("md-records",
                                        metadata);
                                }
                                if (metadata.containsKey(attributeValue)) {
                                    String message =
                                        "A md-record with the name '"
                                            + attributeValue
                                            + "' occurs multiple times in the representation"
                                            + " of the resource";
                                    LOGGER.error(message);
                                    throw new InvalidContentException(
                                        message);

                                }
                                else {
                                    metadata.put(attributeValue, out);
                                }

                            }
                            else {
                                if (outputStreams
                                    .containsKey(attributeValue)) {
                                    String message;
                                    if (currentPath
                                        .equals("/context/admin-descriptors/admin-descriptor")) {
                                        message =
                                            "An admin-descriptor with the name '"
                                                + attributeValue
                                                + "' occurs multiple times in the "
                                                + "representation of the context";
                                    }
                                    else {
                                        message =
                                            "A subresource with the name '"
                                                + attributeValue
                                                + "' occurs multiple times in the"
                                                + " representation of the resource";
                                    }
                                    LOGGER.error(message);
                                    throw new InvalidContentException(
                                        message);

                                }
                                else {
                                    outputStreams.put(attributeValue, out);
                                }

                            }
                        }
                    }
                    // writeElementStart(theName, xmlr);
                    insideLevel++;
                    if (insideLevel != 1) {
                        throw new XMLStreamException("insideLevel != 1: "
                            + insideLevel);
                    }
                }
            }
        }
        catch (XMLStreamException e) {
            throw new WebserverSystemException(
                "Error occured writing XML data.", e);
        }
        // this have to be the last handler
        return element;
    }

    /**
     * See Interface for functional description.
     * 
     * @throws WebserverSystemException
     *             If an error occured writing XML data.
     * 
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#endElement(de.escidoc.core.common.util.xml.stax.events.EndElement)
     */
    public final EndElement endElement(final EndElement element)
        throws WebserverSystemException {
        String theName = element.getLocalName();
        String currentPath = parser.getCurPath();
        if (this.insideRemoveElement && isMatchedText) {
            if (this.removeElements.containsKey(currentPath)) {
                this.insideRemoveElement = false;
                this.isMatchedText = false;
                return element;

            }
            else {
                return element;
            }
        }
        this.decreaseDeepLevel();
        if (inComponent && theName.equals("component")) {
            if (componentId == null) {
                Map components = (HashMap) outputStreams.get("components");
                components.remove(componentId);
            }
            inComponent = false;
            componentId = null;

        }

        try {
            if (inside) {
                insideLevel--;
                if ((insideLevel > 0)
                    || ((insideLevel == 0) && !theName.equals("md-record") && !theName
                        .equals("admin-descriptor"))) {
                    this.getWriter().writeEndElement();
                }

                // remove namespace if is defined in this element
                String ns = element.getNamespace();
                List nsTrace = this.getNsuris().get(ns);

                if (nsTrace != null
                    && (nsTrace.get(2) == null || nsTrace.get(2).equals(
                        element.getPrefix()))
                    && nsTrace.get(1).equals(element.getLocalName())
                    && (Integer) nsTrace.get(0) == (this.getDeepLevel() + 1)) {

                    this.getNsuris().remove(ns);

                }

                // attribute namespaces
                // TODO iteration is a hack, use
                // javax.xml.namespace.NamespaceContext
                Iterator it = this.getNsuris().keySet().iterator();
                Collection<String> toRemove = new ArrayList<String>();
                while (it.hasNext()) {
                    try {
                        String key = (String) it.next();
                        nsTrace = this.getNsuris().get(key);
                        if ((Integer) nsTrace.get(0) == (this.getDeepLevel() + 1)) {
                            toRemove.add(key);
                        }
                    }
                    catch (Exception e) {
                        throw new XMLStreamException(e.getMessage(), e);
                    }
                }
                it = toRemove.iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    this.getNsuris().remove(key);
                }

                if (insideLevel == 0) {
                    inside = false;
                    this.getWriter().flush();
                    this.getWriter().close();
                }
            }
        }
        catch (XMLStreamException e) {
            throw new WebserverSystemException(
                "Error occured writing XML data.", e);
        }
        return element;
    }

    /**
     * See Interface for functional description.
     * 
     * @throws WebserverSystemException
     *             If an error occured writing XML data.
     * 
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#characters(java.lang.String,
     *      de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public final String characters(final String data, final StartElement element)
        throws WebserverSystemException {

        try {
            if ((inside)) {
                if (!this.insideRemoveElement) {
                    this.getWriter().writeCharacters(data);
                }
                else {
                    String text = this.elementToDelete.getElementText();
                    if ((text != null) && (text.length() > 0)) {
                        if (data.equals(text)) {
                            this.isMatchedText = true;
                        }
                        else {
                            this.getWriter().writeCharacters(data);
                        }
                    }
                    else {
                        // if a provided element to remove does not contain a
                        // text
                        // all matched elements will be removed
                        // independent from their text value
                        this.isMatchedText = true;

                    }
                }
            }
        }
        catch (XMLStreamException e) {
            throw new WebserverSystemException(
                "Error occured writing XML data.", e);
        }
        return data;
    }

}
