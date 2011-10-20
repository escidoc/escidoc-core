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

package de.escidoc.core.common.util.stax.handler;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts the subtrees according to the provided pathes from the parsed ByteArrayInputStream containing a xml
 * document. ByteArrayOutputStreams with extracted subtrees will be stored in a Map. A path to a subtree can be provided
 * along with a name of a subtrees root element attribute in order to distinguish between extracted subtrees with the
 * same root elements names.
 * <p/>
 * Extracted subtrees will be stored in a Map with values of the provided attributes as keys. If no attribute is
 * provided, then a key wil be set to the subtrees root element name.
 * <p/>
 * Subtrees with a root elements 'md-record' will be grouped by a submap. The submap has values of the provided
 * attributes as keys. A key 'md-records' of the outside map has this submap as a value.
 * <p/>
 * Subtrees containing element 'component' in a path, will be grouped by another submap. This submap has component ids
 * as keys. Its values are subsubmaps, which are structured analog to the outside map. A key 'components' of the outside
 * map has this submap as a value. The component ids can be set with a method setPids(). Otherwise values of attributes
 * /item/component/@objid will be used.
 * <p/>
 * An additional functionality is a modification of extracted subtrees: via an input parameter of the method
 * 'removeElements' provided subtrees will be removed from extracted subtrees during extraction process. A provided
 * subtree is addressed via the container element of this subtree and a path to the container element in a whole
 * document. The provided container element can contain attributes and/or text to tighten a matching criteria. The
 * following rule is used to remove a subtree matching a provided path from an extracted subtree:
 * <p/>
 * 1. if a provided container element has attributes and/or element text, the business logic checks if a matching
 * subtree element to remove has the same attributes and/oder the same element text 2. if a provided outer subtree
 * element has no attributes/text, the business logic ignores attributes/text in the matching element.
 *
 * @author Rozita Friedman, FRA
 */
public class MultipleExtractor extends WriteHandler {

    private boolean inside;

    private boolean insideRemoveElement;

    private final Map<String, String> pathes;

    private int insideLevel;

    private Map<String, ByteArrayOutputStream> metadata;

    private Map<String, HashMap<String, Object>> components;

    private final Map<String, Object> outputStreams = new HashMap<String, Object>();

    private String componentId;

    private boolean inComponent;

    private final StaxParser parser;

    // private String mdNameValue = null;

    private int number;

    private List<String> pids;

    private static final Pattern PATTERN_OBJID_IN_HREF = Pattern.compile(".*\\/([^\"\\/]*)");

    private Map<String, List<StartElementWithChildElements>> removeElements;

    private boolean isMatchedAttribute;

    private boolean isMatchedText;

    private StartElementWithChildElements elementToDelete;

    /**
     * Creates a instance of MultipleExtractor.
     *
     * @param extractPathes Paths subtree to extract as key and subtree root element attribute name or null as values
     * @param parser        The parser this handler is added to.
     */
    public MultipleExtractor(final String extractPathes, final StaxParser parser) {

        this.parser = parser;
        this.pathes = new HashMap<String, String>();
        this.pathes.put(extractPathes, null);
    }

    /**
     * Creates a instance of MultipleExtractor.
     *
     * @param extractPathes Map with pathes to subtrees to extract as keys and subtrees root elements attribute name or
     *                      null as values
     * @param parser        The parser this handler is added to.
     */
    public MultipleExtractor(final Map<String, String> extractPathes, final StaxParser parser) {
        this.parser = parser;
        this.pathes = extractPathes;
    }

    /**
     * Creates a instance of MultipleExtractor.
     *
     * @param extractPath path to subtree to extract
     * @param extractAtt  attribute name of the subtrees root element
     * @param parser      The parser this handler is added to.
     */
    public MultipleExtractor(final String extractPath, final String extractAtt, final StaxParser parser) {
        this.parser = parser;
        this.pathes = new HashMap<String, String>();
        this.pathes.put(extractPath, extractAtt);
    }

    /**
     * Retrieves a Map with extracted subtrees.
     *
     * @return outputStreams
     */
    public Map<String, Object> getOutputStreams() {
        return this.outputStreams;
    }

    /**
     * Sets component ids.
     *
     * @param pids List with ids of components.
     */
    public void setPids(final List<String> pids) {
        this.pids = pids;
    }

    /**
     * Map of elements which are to remove from XML tree.
     *
     * @param elements Elements which are to remove.
     */
    public void removeElements(final Map<String, List<StartElementWithChildElements>> elements) {
        // TODO extend this to List<StartElement>
        this.removeElements = elements;
    }

    /**
     * See Interface for functional description.
     *
     * @throws WebserverSystemException If an error occured writing XML data.
     */
    @Override
    public StartElement startElement(final StartElement element) throws InvalidContentException,
        WebserverSystemException {
        final NamespaceContext nscontext = element.getNamespaceContext();
        this.increaseDeepLevel();
        final String currentPath = parser.getCurPath();
        final String theName = element.getLocalName();
        if (this.insideRemoveElement) {
            return element;
        }
        if (this.removeElements != null && !this.removeElements.isEmpty()
            && this.removeElements.containsKey(currentPath)) {
            final List<StartElementWithChildElements> elementsToDelete = removeElements.get(currentPath);
            final Iterator<StartElementWithChildElements> iterator = elementsToDelete.iterator();
            this.elementToDelete = null;
            loop1: while (iterator.hasNext()) {
                this.elementToDelete = iterator.next();
                // September 2009, changed logic: if prefix or namespace
                // of elementToDelete is null it is not compared with
                // the current element and handled as match. (FRS)

                // Workaround to handle defect content relations which came from version <= 1.3.3 and where fixed
                // with 1.4 (see issue INFR-1329)
                // TODO remove if FoXML is fixed (e.g. by migration)
                String ns = element.getNamespace();
                if (!(ns.endsWith("#") || ns.endsWith("/"))) {
                    // we can at this point only guess how it should be ('#' or '/'). '#' is probable.
                    ns += "#";
                }

                if ((elementToDelete.getPrefix() == null || elementToDelete.getPrefix().equals(element.getPrefix()))
                    && (elementToDelete.getNamespace() == null || elementToDelete.getNamespace().equals(ns))) {

                    // end workaround
                    // original code
                    //                if ((elementToDelete.getPrefix() == null || elementToDelete.getPrefix().equals(element.getPrefix()))
                    //                    && (elementToDelete.getNamespace() == null || elementToDelete.getNamespace().equals(
                    //                        element.getNamespace()))) {

                    final int attCount2 = elementToDelete.getAttributeCount();
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
                        final int attCount1 = element.getAttributeCount();

                        if (attCount1 == attCount2) {
                            int matchedAttributesNumber = 0;
                            for (int i = 0; i < attCount1; i++) {
                                final Attribute curAtt = element.getAttribute(i);
                                final String curName = curAtt.getLocalName();
                                final String curNameSpace = curAtt.getNamespace();
                                final String curValue = curAtt.getValue();
                                for (int j = 0; j < attCount2; j++) {
                                    final Attribute attToDelete = elementToDelete.getAttribute(j);
                                    final String nameToDelete = attToDelete.getLocalName();
                                    final String nameSpaceToDelete = attToDelete.getNamespace();
                                    final String valueToDelete = attToDelete.getValue();
                                    if (curName.equals(nameToDelete) && curNameSpace.equals(nameSpaceToDelete)
                                        && curValue.equals(valueToDelete)) {

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
            if (this.isMatchedAttribute) {
                removeElements.put(currentPath, elementsToDelete);
                this.insideRemoveElement = true;
                this.isMatchedAttribute = false;
                return element;
            }

        }
        if ("component".equals(theName) && element.indexOfAttribute(Constants.RDF_NAMESPACE_URI, "resource") < 0
            && element.indexOfAttribute(null, "inherited") < 0) {
            this.inComponent = true;
            // Object id = pids.get(number);
            if (this.pids != null) {
                this.componentId = pids.get(this.number);
                this.number++;
            }
            else {
                final int indexOfObjid = element.indexOfAttribute(null, "objid");
                if (indexOfObjid != -1) {
                    final String value = element.getAttribute(indexOfObjid).getValue();
                    if (value != null && value.length() > 0) {
                        this.componentId = value;
                    }
                }
                final int indexOfHref = element.indexOfAttribute(Constants.XLINK_URI, "href");
                if (indexOfHref != -1) {
                    final String value = element.getAttribute(indexOfHref).getValue();
                    if (value != null && value.length() > 0) {

                        final Matcher m1 = PATTERN_OBJID_IN_HREF.matcher(value);
                        if (m1.find()) {
                            this.componentId = m1.group(1);
                        }
                    }
                }

                // FIXME workaround issue 630
                if (this.components != null && components.containsKey(this.componentId)) {
                    throw new InvalidContentException("Found component ID twice.");
                }

            }
        }

        try {
            if (this.inside) {

                writeElement(element);

                final int attCount = element.getAttributeCount();
                for (int i = 0; i < attCount; i++) {
                    final Attribute curAtt = element.getAttribute(i);
                    handleAttributeInInsideElement(curAtt, nscontext, theName);
                }
                this.insideLevel++;
            }
            else {
                if (pathes.containsKey(currentPath) && element.indexOfAttribute(null, "inherited") < 0) {
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();

                    this.setWriter(XmlUtility.createXmlStreamWriter(out));
                    final String attributeName = pathes.get(currentPath);

                    this.inside = true;
                    // create and initialize namespace map
                    this.setNsuris(new HashMap<String, List>());
                    List namespaceTrace = new ArrayList();
                    namespaceTrace.add(-1);
                    namespaceTrace.add("");
                    namespaceTrace.add("xml");
                    this.getNsuris().put("http://www.w3.org/XML/1998/namespace", namespaceTrace);
                    namespaceTrace = new ArrayList();
                    namespaceTrace.add(-1);
                    namespaceTrace.add("");
                    namespaceTrace.add("xmlns");
                    this.getNsuris().put("http://www.w3.org/2000/xmlns/", namespaceTrace);
                    // initialized namespace map

                    if (!"md-record".equals(theName) && !"admin-descriptor".equals(theName)) {
                        writeElement(element);
                    }
                    String attributeValue = null;
                    final int attCount = element.getAttributeCount();
                    for (int i = 0; i < attCount; i++) {
                        final Attribute curAtt = element.getAttribute(i);
                        final String currentAttributeValue =
                            handleAttributeInOutsideElement(curAtt, nscontext, theName, attributeName);
                        if (currentAttributeValue != null) {
                            attributeValue = currentAttributeValue;
                        }
                    }

                    if (this.inComponent) {
                        if (this.components == null) {
                            this.components = new HashMap<String, HashMap<String, Object>>();
                            outputStreams.put("components", this.components);
                        }
                        final HashMap<String, Object> component;
                        if (components.containsKey(this.componentId)) {
                            component = components.get(this.componentId);
                        }
                        else {
                            component = new HashMap<String, Object>();

                            components.put(this.componentId, component);
                        }

                        // String subId = (String) pids.get(number);
                        // number++;
                        if (attributeName == null) {
                            // outputStreams.put(theName + "*" + subId,
                            // out);
                            component.put(theName, out);
                        }
                        else {
                            if ("md-record".equals(theName)) {

                                // this.mdNameValue = attributeValue;
                                // outputStreams.put(attributeValue + "*" +
                                // subId,
                                // out);
                                final Map<String, ByteArrayOutputStream> mdRecords;
                                if (component.containsKey(XmlUtility.NAME_MDRECORDS)) {
                                    mdRecords =
                                        (HashMap<String, ByteArrayOutputStream>) component
                                            .get(XmlUtility.NAME_MDRECORDS);
                                }
                                else {
                                    mdRecords = new HashMap<String, ByteArrayOutputStream>();
                                    component.put(XmlUtility.NAME_MDRECORDS, mdRecords);
                                }
                                if (mdRecords.containsKey(attributeValue)) {
                                    throw new InvalidContentException("A component md-record with the name '"
                                        + attributeValue + "' occurs multiple times in the representation"
                                        + " of a component.");

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
                            if ("md-record".equals(theName)) {
                                if (this.metadata == null) {
                                    this.metadata = new HashMap<String, ByteArrayOutputStream>();
                                    outputStreams.put(XmlUtility.NAME_MDRECORDS, this.metadata);
                                }
                                if (metadata.containsKey(attributeValue)) {
                                    throw new InvalidContentException("A md-record with the name '" + attributeValue
                                        + "' occurs multiple times in the representation" + " of the resource");

                                }
                                else {
                                    metadata.put(attributeValue, out);
                                }

                            }
                            else {
                                if (outputStreams.containsKey(attributeValue)) {
                                    final String message =
                                        "/context/admin-descriptors/admin-descriptor".equals(currentPath) ? "An admin-descriptor with the name '"
                                            + attributeValue
                                            + "' occurs multiple times in the "
                                            + "representation of the context" : "A subresource with the name '"
                                            + attributeValue + "' occurs multiple times in the"
                                            + " representation of the resource";
                                    throw new InvalidContentException(message);

                                }
                                else {
                                    outputStreams.put(attributeValue, out);
                                }

                            }
                        }
                    }
                    // writeElementStart(theName, xmlr);
                    this.insideLevel++;
                    if (this.insideLevel != 1) {
                        throw new XMLStreamException("insideLevel != 1: " + this.insideLevel);
                    }
                }
            }
        }
        catch (final XMLStreamException e) {
            throw new WebserverSystemException("Error occured writing XML data.", e);
        }
        // this have to be the last handler
        return element;
    }

    /**
     * See Interface for functional description.
     *
     * @throws WebserverSystemException If an error occurred writing XML data.
     */
    @Override
    public EndElement endElement(final EndElement element) throws WebserverSystemException {
        final String theName = element.getLocalName();
        final String currentPath = parser.getCurPath();
        if (this.insideRemoveElement && this.isMatchedText) {
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
        if (this.inComponent && "component".equals(theName)) {
            if (this.componentId == null) {
                final Map components = (Map) outputStreams.get("components");
                components.remove(this.componentId);
            }
            this.inComponent = false;
            this.componentId = null;

        }

        try {
            if (this.inside) {
                this.insideLevel--;
                if (this.insideLevel > 0 || this.insideLevel == 0 && !"md-record".equals(theName)
                    && !"admin-descriptor".equals(theName)) {
                    this.getWriter().writeEndElement();
                }

                // remove namespace if is defined in this element
                final String ns = element.getNamespace();
                List nsTrace = this.getNsuris().get(ns);

                if (nsTrace != null && (nsTrace.get(2) == null || nsTrace.get(2).equals(element.getPrefix()))
                    && nsTrace.get(1).equals(element.getLocalName())
                    && (Integer) nsTrace.get(0) == this.getDeepLevel() + 1) {

                    this.getNsuris().remove(ns);

                }

                // attribute namespaces
                // TODO iteration is a hack, use
                // javax.xml.namespace.NamespaceContext
                Iterator<String> it = this.getNsuris().keySet().iterator();
                final Collection<String> toRemove = new ArrayList<String>();
                while (it.hasNext()) {
                    try {
                        final String key = it.next();
                        nsTrace = this.getNsuris().get(key);
                        if ((Integer) nsTrace.get(0) == this.getDeepLevel() + 1) {
                            toRemove.add(key);
                        }
                    }
                    catch (final Exception e) {
                        throw new XMLStreamException(e.getMessage(), e);
                    }
                }
                it = toRemove.iterator();
                while (it.hasNext()) {
                    final String key = it.next();
                    this.getNsuris().remove(key);
                }

                if (this.insideLevel == 0) {
                    this.inside = false;
                    this.getWriter().flush();
                    this.getWriter().close();
                }
            }
        }
        catch (final XMLStreamException e) {
            throw new WebserverSystemException("Error occured writing XML data.", e);
        }
        return element;
    }

    /**
     * See Interface for functional description.
     *
     * @throws WebserverSystemException If an error occured writing XML data.
     */
    @Override
    public String characters(final String data, final StartElement element) throws WebserverSystemException {

        try {
            if (this.inside) {
                if (this.insideRemoveElement) {
                    final String text = this.elementToDelete.getElementText();
                    if (text != null && text.length() > 0) {
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
                else {
                    this.getWriter().writeCharacters(data);
                }
            }
        }
        catch (final XMLStreamException e) {
            throw new WebserverSystemException("Error occured writing XML data.", e);
        }
        return data;
    }

}
