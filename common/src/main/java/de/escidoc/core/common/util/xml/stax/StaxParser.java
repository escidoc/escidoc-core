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

package de.escidoc.core.common.util.xml.stax;

import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import de.escidoc.core.common.util.xml.stax.interfaces.DefaultHandlerStackInterface;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * Parser with Handler Chain.
 *
 * @author Frank Schwichtenberg
 * @see de.escidoc.core.common.util.xml.stax.handler
 */
public class StaxParser implements DefaultHandlerStackInterface {

    private boolean started;

    private boolean rootChecked;

    private final String expectedName;

    private List<DefaultHandler> handlerChain = new ArrayList<DefaultHandler>();

    private final Stack<StartElement> startElements = new Stack<StartElement>();

    private final StringBuffer curPath = new StringBuffer();

    private final XMLInputFactory factory = XMLInputFactory.newInstance();

    /**
     * The constructor.
     *
     * @param rootElementName The expected name of the root element. If the parsed document does not contain a root
     *                        element of the same name, an exception is thrown.
     */
    public StaxParser(final String rootElementName) {

        this.expectedName = rootElementName;
    }

    /**
     * Returns the path to the element which is processed right now. The path consists of the local name of the elements
     * seperated by '/'. There is no '/' at the end of the path. So a handler does not need to implement some code to
     * get the position in the document.
     *
     * @return The path to the current element consisting of local names.
     */
    @Override
    public String getCurPath() {
        return curPath.toString();
    }

    /**
     * Sets the list of handlers.
     *
     * @param hc A {@link List List} of DefaultHandler implementations. Same as call {@code clearHandlerChain()}
     *           and add every single Handler in list order.
     */
    public void setHandlerChain(final List<DefaultHandler> hc) {
        this.handlerChain = hc;
    }

    /**
     * Removes all Handlers from this Parser.
     */
    public void clearHandlerChain() {
        handlerChain.clear();
    }

    /**
     * Parse.
     *
     * @param in XML
     * @throws Exception If anything fails. This depends on the implementation of the handlers in the used handler
     *                   chain
     */
    public void parse(final InputStream in) throws Exception {
        if (this.handlerChain == null || handlerChain.isEmpty()) {
            throw new XMLStreamException("Parser has no handlers. Try StaxParser sp.addHandler"
                + "(new DefaultHandler());");
        }
        parseStream(in);
    }

    /**
     * Parse with {@link XMLStreamReader XMLStreamReader}.
     *
     * @param in XML
     * @throws Exception If anything fails. This depends on the implementation of the handlers in the used handler
     *                   chain
     */
    protected void parseStream(final InputStream in) throws Exception {

        final XMLStreamReader parser = factory.createXMLStreamReader(in, XmlUtility.CHARACTER_ENCODING);

        while (parser.hasNext()) {
            final int event = parser.next();
            switch (event) {

                case XMLStreamConstants.START_DOCUMENT:
                    init();
                    break;

                case XMLStreamConstants.END_DOCUMENT:
                    // close the XMLStreamReader or TODO reset the input stream
                    parser.close();
                    // reset for next run
                    this.started = false;
                    startElements.pop();
                    break;

                case XMLStreamConstants.START_ELEMENT:
                    // bug?
                    if (!this.started) {
                        init();
                    }

                    startElements.peek().setHasChild(true);
                    curPath.append('/');
                    curPath.append(parser.getLocalName());
                    final StartElement startElement = new StartElement(parser, curPath.toString());
                    startElements.push(startElement);
                    handle(startElement);
                    break;

                case XMLStreamConstants.CHARACTERS:
                    final String data = parser.getText();
                    if (data.length() != 0) {
                        startElements.peek().setHasCharacters(true);
                        handle(data);
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (startElements.peek().isEmpty()) {
                        // Throw empty CHARACTERS event!");
                        handle("");
                    }
                    final EndElement endElement = new EndElement(parser, curPath.toString());
                    handle(endElement);
                    startElements.pop();
                    curPath.setLength(curPath.lastIndexOf("/"));
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * Parse with {@link XMLEventReader XMLEventReader}.
     *
     * @param in XML
     * @throws Exception If anything fails. This depends on the implementation of the handlers in the used handler
     *                   chain
     */
    protected void parseEvents(final InputStream in) throws Exception {

        final XMLEventReader parser = factory.createXMLEventReader(in);

        while (parser.hasNext()) {
            final XMLEvent event = parser.nextEvent();

            switch (event.getEventType()) {
                case XMLStreamConstants.START_DOCUMENT:
                    init();
                    break;

                case XMLStreamConstants.END_DOCUMENT:
                    // close the XMLStreamReader or TODO reset the input stream
                    parser.close();

                    // set ready
                    this.started = false;

                    // there have to be the root element
                    startElements.pop();
                    break;

                case XMLStreamConstants.START_ELEMENT:
                    // bug?
                    if (!this.started) {
                        init();
                    }
                    startElements.peek().setHasChild(true);
                    final javax.xml.stream.events.StartElement se = event.asStartElement();
                    final StartElement startElement =
                        new StartElement(se.getName().getLocalPart(), se.getName().getNamespaceURI(), se
                            .getName().getPrefix(), se.getNamespaceContext());
                    // add attributes
                    final Iterator attIt = se.getAttributes();
                    while (attIt.hasNext()) {
                        final javax.xml.stream.events.Attribute a = (javax.xml.stream.events.Attribute) attIt.next();
                        final QName name = a.getName();
                        final Attribute attribute =
                            new Attribute(name.getLocalPart(), name.getNamespaceURI(), name.getPrefix(), a.getValue());
                        startElement.addAttribute(attribute);
                    }
                    startElements.push(startElement);
                    curPath.append('/');
                    curPath.append(startElement.getLocalName());
                    handle(startElement);
                    break;

                case XMLStreamConstants.CHARACTERS:
                    final String data = event.asCharacters().getData();
                    if (data.length() != 0) {
                        startElements.peek().setHasCharacters(true);
                        handle(data);
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    final javax.xml.stream.events.EndElement ee = event.asEndElement();
                    final EndElement endElement =
                        new EndElement(ee.getName().getLocalPart(), ee.getName().getNamespaceURI(), ee
                            .getName().getPrefix());
                    startElements.pop();
                    curPath.setLength(curPath.lastIndexOf("/"));
                    handle(endElement);
                    break;
            }
        }
    }

    /**
     * Set start state.
     */
    protected void init() {
        startElements.clear();
        startElements.push(new StartElement("root", null, null, null));
        curPath.setLength(0);
        this.started = true;
        this.rootChecked = false;
    }

    /**
     * Adds a Handler at the end of the handler chain.
     *
     * @param dh A DefaultHandler implementation.
     */
    public void addHandler(final DefaultHandler dh) {
        handlerChain.add(dh);
    }

    /**
     * Adds all Handlers at the end of the handler chain.
     *
     * @param c A Collection of DefaultHandler implementations.
     */
    public void addHandler(final Collection<DefaultHandler> c) {
        handlerChain.addAll(c);
    }

    /**
     * Inserts the specified Handler at the specified position in the handler chain. Shifts the element currently at
     * that position (if any) and any subsequent elements to the right (adds one to their indices).
     * @param index
     * @param dh
     */
    public void addHandler(final int index, final DefaultHandler dh) {
        handlerChain.add(index, dh);
    }

    /**
     * Calls startElement() for every Handler in the handler chain. If the current start element is the root element,
     * the name is asserted using the value set during creation of the parser..
     *
     * @param e {@link StartElement StartElement}
     * @throws Exception If anything fails. This depends on the implementation of the handlers in the used handler
     *                   chain.
     */
    protected void handle(final StartElement e) throws Exception {

        StartElement element = e;
        if (!this.rootChecked) {
            final String localName = e.getLocalName();
            if (!expectedName.equals(localName)) {
                throw new XmlCorruptedException("Unexpected root element, expected " + this.expectedName + "but was "
                    + localName + '.');
            }
            this.rootChecked = true;
        }

        //        int chainSize = handlerChain.size();
        for (final DefaultHandler aHandlerChain : this.handlerChain) {
            if (aHandlerChain != null) {
                element = aHandlerChain.startElement(element);
            }
        }
    }

    /**
     * Calls endElement() for every Handler in the handler chain.
     *
     * @param e {@link EndElement EndElement}
     * @throws Exception If anything fails. This depends on the implementation of the handlers in the used handler
     *                   chain
     */
    protected void handle(final EndElement e) throws Exception {
        EndElement element = e;
        //        int chainSize = handlerChain.size();
        for (final DefaultHandler aHandlerChain : this.handlerChain) {
            if (aHandlerChain != null) {
                element = aHandlerChain.endElement(element);
            }
        }
    }

    /**
     * Calls characters() for every Handler in the handler chain.
     *
     * @param s Characters
     * @throws Exception If anything fails. This depends on the implementation of the handlers in the used handler
     *                   chain
     */
    protected void handle(final String s) throws Exception {
        String chars = s;
        //        int chainSize = handlerChain.size();
        for (final DefaultHandler aHandlerChain : this.handlerChain) {
            if (aHandlerChain != null) {
                final StartElement e = startElements.peek();
                chars = aHandlerChain.characters(chars, e);
            }
        }
    }
}
