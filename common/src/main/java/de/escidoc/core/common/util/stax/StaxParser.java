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

package de.escidoc.core.common.util.stax;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.naming.directory.NoSuchAttributeException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.TmeException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.PidAlreadyAssignedException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import de.escidoc.core.common.util.xml.stax.interfaces.DefaultHandlerStackInterface;

/**
 * Parser with Handler Chain.
 *
 * @author Frank Schwichtenberg
 * @see DefaultHandler
 */
public class StaxParser implements DefaultHandlerStackInterface {

    private boolean started;

    private List<DefaultHandler> handlerChain = new ArrayList<DefaultHandler>();

    private final Stack<StartElement> startElements = new Stack<StartElement>();

    private final StringBuffer curPath = new StringBuffer();

    private final XMLInputFactory factory = XMLInputFactory.newInstance();

    private String expectedName;

    private boolean checkRootElementName;

    private boolean rootChecked;

    private String xmlBase;

    /**
     * The constructor.
     */
    public StaxParser() {
        // FIXME coalescing should be set to false (switched on for backward compatibility with 1.4)
        this.factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
    }

    /**
     * The constructor.
     *
     * @param rootElementName The expected name of the root element. If the parsed document does not contain a root
     *                        element of the same name, an exception is thrown.
     */
    public StaxParser(final String rootElementName) {
        this.checkRootElementName = true;
        this.expectedName = rootElementName;
        // FIXME coalescing should be set to false (switched on for backward compatibility with 1.4)
        this.factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
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
     * Returns the XML Base valid for the element which is processed right now. If no XML Base is found till the current
     * position in the document the return value is {@code null}.
     *
     * @return The XML Base valid for the element which is processed right now.
     *         <p/>
     *         FIXME Return to previous XML Base if not (only) defined in root element.
     */
    public String getXmlBase() {
        return this.xmlBase;
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
     * @throws XMLStreamException             If parsing failed.
     * @throws ContentModelNotFoundException  eSciDoc specific; thrown by some Handlers.
     * @throws ContextNotFoundException       eSciDoc specific; thrown by some Handlers.
     * @throws LockingException               eSciDoc specific; thrown by some Handlers.
     * @throws ReadonlyElementViolationException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws OptimisticLockingException     Thrown in case of an internal error.
     * @throws PidAlreadyAssignedException    eSciDoc specific; thrown by some Handlers.
     * @throws XmlParserSystemException       eSciDoc specific; thrown by some Handlers.
     * @throws EncodingSystemException        eSciDoc specific; thrown by some Handlers.
     * @throws WebserverSystemException       eSciDoc specific; thrown by some Handlers.
     * @throws TripleStoreSystemException     eSciDoc specific; thrown by some Handlers.
     * @throws MissingContentException        eSciDoc specific; thrown by some Handlers.
     * @throws MissingAttributeValueException eSciDoc specific; thrown by some Handlers.
     * @throws MissingElementValueException   eSciDoc specific; thrown by some Handlers.
     * @throws MissingMdRecordException       eSciDoc specific; thrown by some Handlers.
     * @throws ReadonlyAttributeViolationException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws InvalidContentException        eSciDoc specific; thrown by some Handlers.
     * @throws AlreadyExistsException         eSciDoc specific; thrown by some Handlers.
     * @throws ReferencedResourceNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws InvalidStatusException         Thrown if an organizational unit is in an invalid status.
     * @throws RelationPredicateNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws OrganizationalUnitNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws ContentRelationNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws IntegritySystemException       eSciDoc specific; thrown by some Handlers.
     * @throws TmeException                   eSciDoc specific; thrown by some Handlers.
     * @throws de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException
     * @throws de.escidoc.core.common.exceptions.application.invalid.LastModificationDateMissingException
     */
    public void parse(final byte[] in) throws XMLStreamException, ContentModelNotFoundException,
        ContextNotFoundException, MissingContentException, LockingException, ReadonlyElementViolationException,
        MissingAttributeValueException, MissingElementValueException, ReadonlyAttributeViolationException,
        InvalidContentException, OptimisticLockingException, AlreadyExistsException,
        ReferencedResourceNotFoundException, InvalidStatusException, RelationPredicateNotFoundException,
        OrganizationalUnitNotFoundException, ContentRelationNotFoundException, PidAlreadyAssignedException,
        TripleStoreSystemException, WebserverSystemException, EncodingSystemException, XmlParserSystemException,
        IntegritySystemException, MissingMdRecordException, TmeException, XmlCorruptedException {

        parseStream(new ByteArrayInputStream(in));
    }

    /**
     * Parse.
     *
     * @param in XML
     * @throws XMLStreamException             If parsing failed.
     * @throws ContentModelNotFoundException  eSciDoc specific; thrown by some Handlers.
     * @throws ContextNotFoundException       eSciDoc specific; thrown by some Handlers.
     * @throws LockingException               eSciDoc specific; thrown by some Handlers.
     * @throws ReadonlyElementViolationException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws OptimisticLockingException     Thrown in case of an internal error.
     * @throws PidAlreadyAssignedException    eSciDoc specific; thrown by some Handlers.
     * @throws XmlParserSystemException       eSciDoc specific; thrown by some Handlers.
     * @throws EncodingSystemException        eSciDoc specific; thrown by some Handlers.
     * @throws WebserverSystemException       eSciDoc specific; thrown by some Handlers.
     * @throws TripleStoreSystemException     eSciDoc specific; thrown by some Handlers.
     * @throws MissingContentException        eSciDoc specific; thrown by some Handlers.
     * @throws MissingAttributeValueException eSciDoc specific; thrown by some Handlers.
     * @throws MissingElementValueException   eSciDoc specific; thrown by some Handlers.
     * @throws MissingMdRecordException       eSciDoc specific; thrown by some Handlers.
     * @throws ReadonlyAttributeViolationException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws InvalidContentException        eSciDoc specific; thrown by some Handlers.
     * @throws AlreadyExistsException         eSciDoc specific; thrown by some Handlers.
     * @throws ReferencedResourceNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws InvalidStatusException         Thrown if an organizational unit is in an invalid status.
     * @throws RelationPredicateNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws OrganizationalUnitNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws ContentRelationNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws IntegritySystemException       eSciDoc specific; thrown by some Handlers.
     * @throws TmeException                   eSciDoc specific; thrown by some Handlers.
     * @throws XmlCorruptedException          eSciDoc specific; thrown by some Handlers.
     * @throws de.escidoc.core.common.exceptions.application.invalid.LastModificationDateMissingException
     */
    public void parse(final InputStream in) throws XMLStreamException, ContentModelNotFoundException,
        ContextNotFoundException, MissingContentException, LockingException, ReadonlyElementViolationException,
        MissingAttributeValueException, MissingElementValueException, ReadonlyAttributeViolationException,
        InvalidContentException, OptimisticLockingException, AlreadyExistsException,
        ReferencedResourceNotFoundException, InvalidStatusException, RelationPredicateNotFoundException,
        OrganizationalUnitNotFoundException, ContentRelationNotFoundException, PidAlreadyAssignedException,
        TripleStoreSystemException, WebserverSystemException, EncodingSystemException, XmlParserSystemException,
        IntegritySystemException, MissingMdRecordException, TmeException, XmlCorruptedException {

        if (this.handlerChain == null || handlerChain.isEmpty()) {
            throw new XMLStreamException("Parser has no handlers. Try StaxParser sp.addHandler"
                + "(new DefaultHandler());");
        }

        parseStream(in);
    }

    /**
     * Parse.
     *
     * @param xml The to parse XML as String.
     * @throws EncodingSystemException        Thrown if character encoding is not supported.
     * @throws XMLStreamException             If parsing failed.
     * @throws ContentModelNotFoundException  eSciDoc specific; thrown by some Handlers.
     * @throws ContextNotFoundException       eSciDoc specific; thrown by some Handlers.
     * @throws LockingException               eSciDoc specific; thrown by some Handlers.
     * @throws ReadonlyElementViolationException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws OptimisticLockingException     Thrown in case of an internal error.
     * @throws PidAlreadyAssignedException    eSciDoc specific; thrown by some Handlers.
     * @throws XmlParserSystemException       eSciDoc specific; thrown by some Handlers.
     * @throws WebserverSystemException       eSciDoc specific; thrown by some Handlers.
     * @throws TripleStoreSystemException     eSciDoc specific; thrown by some Handlers.
     * @throws MissingContentException        eSciDoc specific; thrown by some Handlers.
     * @throws MissingAttributeValueException eSciDoc specific; thrown by some Handlers.
     * @throws MissingElementValueException   eSciDoc specific; thrown by some Handlers.
     * @throws MissingMdRecordException       eSciDoc specific; thrown by some Handlers.
     * @throws ReadonlyAttributeViolationException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws InvalidContentException        eSciDoc specific; thrown by some Handlers.
     * @throws AlreadyExistsException         eSciDoc specific; thrown by some Handlers.
     * @throws ReferencedResourceNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws InvalidStatusException         Thrown if an organizational unit is in an invalid status.
     * @throws RelationPredicateNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws OrganizationalUnitNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws TmeException                   eSciDoc specific; thrown by some Handlers.
     * @throws ContentRelationNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws IntegritySystemException       eSciDoc specific; thrown by some Handlers.
     * @throws XmlCorruptedException          eSciDoc specific; thrown by some Handlers.
     * @throws de.escidoc.core.common.exceptions.application.invalid.LastModificationDateMissingException
     */
    public void parse(final String xml) throws EncodingSystemException, ReadonlyElementViolationException,
        ReadonlyAttributeViolationException, ContentModelNotFoundException, ContextNotFoundException,
        MissingContentException, LockingException, MissingAttributeValueException, MissingElementValueException,
        InvalidContentException, OptimisticLockingException, AlreadyExistsException,
        ReferencedResourceNotFoundException, InvalidStatusException, RelationPredicateNotFoundException,
        OrganizationalUnitNotFoundException, ContentRelationNotFoundException, PidAlreadyAssignedException,
        MissingMdRecordException, TripleStoreSystemException, WebserverSystemException, XmlParserSystemException,
        IntegritySystemException, TmeException, XMLStreamException, XmlCorruptedException {

        final ByteArrayInputStream in;
        try {
            in = new ByteArrayInputStream(xml.getBytes(XmlUtility.CHARACTER_ENCODING));
        }
        catch (final UnsupportedEncodingException e) {
            throw new EncodingSystemException(e);
        }

        parse(in);
    }

    /**
     * Parse with {@link XMLStreamReader XMLStreamReader}.
     *
     * @param in XML
     * @throws XMLStreamException             If parsing failed.
     * @throws ContentModelNotFoundException  eSciDoc specific; thrown by some Handlers.
     * @throws ContextNotFoundException       eSciDoc specific; thrown by some Handlers.
     * @throws LockingException               eSciDoc specific; thrown by some Handlers.
     * @throws InvalidContentException        eSciDoc specific; thrown by some Handlers.
     * @throws InvalidStatusException         Thrown if an organizational unit is in an invalid status.
     * @throws OptimisticLockingException     eSciDoc specific; thrown by some Handlers.
     * @throws PidAlreadyAssignedException    eSciDoc specific; thrown by some Handlers.
     * @throws WebserverSystemException       eSciDoc specific; thrown by some Handlers.
     * @throws TripleStoreSystemException     eSciDoc specific; thrown by some Handlers.
     * @throws XmlParserSystemException       eSciDoc specific; thrown by some Handlers.
     * @throws EncodingSystemException        eSciDoc specific; thrown by some Handlers.
     * @throws MissingContentException        eSciDoc specific; thrown by some Handlers.
     * @throws ReadonlyElementViolationException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws MissingAttributeValueException eSciDoc specific; thrown by some Handlers.
     * @throws MissingElementValueException   eSciDoc specific; thrown by some Handlers.
     * @throws ReadonlyAttributeViolationException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws AlreadyExistsException         eSciDoc specific; thrown by some Handlers.
     * @throws ReferencedResourceNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws RelationPredicateNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws OrganizationalUnitNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws ContentRelationNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws IntegritySystemException       eSciDoc specific; thrown by some Handlers.
     * @throws MissingMdRecordException       eSciDoc specific; thrown by some Handlers.
     * @throws TmeException                   eSciDoc specific; thrown by some Handlers.
     * @throws XmlCorruptedException          eSciDoc specific; thrown by some Handlers.
     * @throws de.escidoc.core.common.exceptions.application.invalid.LastModificationDateMissingException
     */
    protected void parseStream(final InputStream in) throws XMLStreamException, ContentModelNotFoundException,
        ContextNotFoundException, MissingContentException, LockingException, ReadonlyElementViolationException,
        MissingAttributeValueException, MissingElementValueException, ReadonlyAttributeViolationException,
        InvalidContentException, OptimisticLockingException, AlreadyExistsException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, InvalidStatusException,
        OrganizationalUnitNotFoundException, ContentRelationNotFoundException, PidAlreadyAssignedException,
        TripleStoreSystemException, WebserverSystemException, EncodingSystemException, XmlParserSystemException,
        IntegritySystemException, MissingMdRecordException, TmeException, XmlCorruptedException {

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
                    this.xmlBase = null;
                    break;

                case XMLStreamConstants.START_ELEMENT:
                    // bug?
                    if (!this.started) {
                        init();
                    }

                    startElements.peek().setHasChild(true);
                    final StartElement startElement =
                        new StartElement(parser.getLocalName(), parser.getNamespaceURI(), parser.getPrefix(), parser
                            .getNamespaceContext());
                    final int xmlBaseIndex =
                        startElement.indexOfAttribute("http://www.w3.org/XML/1998/namespace", "base");
                    if (xmlBaseIndex > -1) {
                        this.xmlBase = startElement.getAttribute(xmlBaseIndex).getValue();
                    }
                    // add attributes
                    final int attCount = parser.getAttributeCount();
                    for (int i = 0; i < attCount; i++) {
                        final Attribute attribute =
                            new Attribute(parser.getAttributeLocalName(i), parser.getAttributeNamespace(i), parser
                                .getAttributePrefix(i), parser.getAttributeValue(i));
                        startElement.addAttribute(attribute);
                    }

                    startElements.push(startElement);
                    curPath.append('/');
                    curPath.append(startElement.getLocalName());
                    handle(startElement);
                    break;

                case XMLStreamConstants.CHARACTERS:
                    final String data = parser.getText();
                    if (data.length() != 0) {
                        startElements.peek().setHasCharacters(true);
                        handle(data);
                    }
                    break;

                case XMLStreamConstants.CDATA:
                    final String cdata = "<![CDATA[" + parser.getText() + "]]>";
                    // FIXME cdata length is always != 0
                    if (cdata.length() != 0) {
                        startElements.peek().setHasCharacters(true);
                        handle(cdata);
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (startElements.peek().isEmpty()) {
                        handle("");
                    }
                    final EndElement endElement =
                        new EndElement(parser.getLocalName(), parser.getNamespaceURI(), parser.getPrefix());
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
     * @throws XMLStreamException             If parsing failed.
     * @throws ContentModelNotFoundException  eSciDoc specific; thrown by some Handlers.
     * @throws ContextNotFoundException       eSciDoc specific; thrown by some Handlers.
     * @throws LockingException               eSciDoc specific; thrown by some Handlers.
     * @throws InvalidContentException        eSciDoc specific; thrown by some Handlers.
     * @throws InvalidStatusException         Thrown if an organizational unit is in an invalid status.
     * @throws OptimisticLockingException     eSciDoc specific; thrown by some Handlers.
     * @throws PidAlreadyAssignedException    eSciDoc specific; thrown by some Handlers.
     * @throws MissingContentException        eSciDoc specific; thrown by some Handlers.
     * @throws ReadonlyElementViolationException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws MissingAttributeValueException eSciDoc specific; thrown by some Handlers.
     * @throws MissingElementValueException   eSciDoc specific; thrown by some Handlers.
     * @throws ReadonlyAttributeViolationException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws AlreadyExistsException         eSciDoc specific; thrown by some Handlers.
     * @throws ReferencedResourceNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws RelationPredicateNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws OrganizationalUnitNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws ContentRelationNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws MissingMdRecordException       eSciDoc specific; thrown by some Handlers.
     * @throws TmeException                   eSciDoc specific; thrown by some Handlers.
     * @throws de.escidoc.core.common.exceptions.system.XmlParserSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     * @throws de.escidoc.core.common.exceptions.system.EncodingSystemException
     * @throws de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     */
    protected void parseEvents(final InputStream in) throws XMLStreamException, ContentModelNotFoundException,
        ContextNotFoundException, MissingContentException, LockingException, ReadonlyElementViolationException,
        MissingAttributeValueException, MissingElementValueException, ReadonlyAttributeViolationException,
        InvalidContentException, OptimisticLockingException, AlreadyExistsException,
        ReferencedResourceNotFoundException, InvalidStatusException, RelationPredicateNotFoundException,
        OrganizationalUnitNotFoundException, ContentRelationNotFoundException, PidAlreadyAssignedException,
        MissingMdRecordException, TmeException, XmlCorruptedException, TripleStoreSystemException,
        WebserverSystemException, EncodingSystemException, IntegritySystemException, XmlParserSystemException {

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
                    final Iterator<javax.xml.stream.events.Attribute> attIt = se.getAttributes();
                    while (attIt.hasNext()) {
                        final javax.xml.stream.events.Attribute a = attIt.next();
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

                case XMLStreamConstants.CDATA:
                    final String cdata = "<![CDATA[" + event.asCharacters().getData() + "]]>";
                    // FIXME this length is always != 0
                    if (cdata.length() != 0) {
                        startElements.peek().setHasCharacters(true);
                        handle(cdata);
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
                default:
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
     *
     * @param index Position of new Handler in handler chain.
     * @param dh    A DefaultHandler implementations.
     */
    public void addHandler(final int index, final DefaultHandler dh) {
        handlerChain.add(index, dh);
    }

    /**
     * @return handler chain.
     */
    public List<DefaultHandler> getHandlerChain() {
        return this.handlerChain;
    }

    /**
     * Calls startElement() for every Handler in the handler chain.
     *
     * @param startElement {@link de.escidoc.core.common.util.xml.stax.events.StartElement StartElement}
     * @throws XMLStreamException             If parsing failed.
     * @throws ContentModelNotFoundException  eSciDoc specific; thrown by some Handlers.
     * @throws ContextNotFoundException       eSciDoc specific; thrown by some Handlers.
     * @throws LockingException               eSciDoc specific; thrown by some Handlers.
     * @throws MissingContentException        eSciDoc specific; thrown by some Handlers.
     * @throws InvalidContentException        eSciDoc specific; thrown by some Handlers.
     * @throws OptimisticLockingException     eSciDoc specific; thrown by some Handlers.
     * @throws TripleStoreSystemException     eSciDoc specific; thrown by some Handlers.
     * @throws WebserverSystemException       eSciDoc specific; thrown by some Handlers.
     * @throws EncodingSystemException        eSciDoc specific; thrown by some Handlers.
     * @throws XmlParserSystemException       eSciDoc specific; thrown by some Handlers.
     * @throws MissingAttributeValueException eSciDoc specific; thrown by some Handlers.
     * @throws ReadonlyAttributeViolationException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws ReadonlyElementViolationException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws AlreadyExistsException         eSciDoc specific; thrown by some Handlers.
     * @throws ReferencedResourceNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws RelationPredicateNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws OrganizationalUnitNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws IntegritySystemException       eSciDoc specific; thrown by some Handlers.
     * @throws TmeException                   eSciDoc specific; thrown by some Handlers.
     * @throws XmlCorruptedException          eSciDoc specific; thrown by some Handlers.
     */
    protected void handle(final StartElement startElement) throws XMLStreamException, ContentModelNotFoundException,
        ContextNotFoundException, LockingException, MissingAttributeValueException,
        ReadonlyAttributeViolationException, ReadonlyElementViolationException, MissingContentException,
        InvalidContentException, OptimisticLockingException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, OrganizationalUnitNotFoundException, TripleStoreSystemException,
        WebserverSystemException, EncodingSystemException, XmlParserSystemException, IntegritySystemException,
        TmeException, XmlCorruptedException {

        StartElement element = startElement;
        if (this.checkRootElementName && !this.rootChecked) {
            final String localName = element.getLocalName();
            if (!expectedName.equals(localName)) {
                throw new XmlCorruptedException("Unexpected root element, expected " + this.expectedName + "but was "
                    + localName + '.');
            }
            this.rootChecked = true;
        }
        for (final DefaultHandler handler : this.handlerChain) {
            if (handler != null) {
                try {
                    element = handler.startElement(element);
                }
                catch (final NoSuchAttributeException ex) {
                    throw new ReadonlyAttributeViolationException(ex);
                }
                catch (final ContentModelNotFoundException ctnf) {
                    throw ctnf;
                }
                catch (final ContextNotFoundException cnf) {
                    throw cnf;
                }
                catch (final IntegritySystemException e) {
                    throw e;
                }
                catch (final XMLStreamException xse) {
                    throw xse;
                }
                catch (final InvalidContentException ice) {
                    throw ice;
                }
                catch (final LockingException le) {
                    throw le;
                }
                catch (final OptimisticLockingException ole) {
                    throw ole;
                }
                catch (final ReadonlyAttributeViolationException rae) {
                    throw rae;
                }
                catch (final MissingAttributeValueException mae) {
                    throw mae;
                }
                catch (final ReadonlyElementViolationException rae) {
                    throw rae;
                }
                catch (final TripleStoreSystemException ex) {
                    throw ex;
                }
                catch (final MissingContentException ex) {
                    throw ex;
                }
                catch (final XmlCorruptedException ex) {
                    throw ex;
                }
                catch (final WebserverSystemException ex) {
                    throw ex;
                }
                catch (final XmlParserSystemException ex) {
                    throw ex;
                }
                catch (final EncodingSystemException ex) {
                    throw ex;
                }
                catch (final ReferencedResourceNotFoundException ex) {
                    throw ex;
                }
                catch (final RelationPredicateNotFoundException ex) {
                    throw ex;
                }
                catch (final OrganizationalUnitNotFoundException ex) {
                    throw ex;
                }
                catch (final TmeException ex) {
                    throw ex;
                }
                catch (final Exception ex) {
                    throw new WebserverSystemException("Should not be reached. StaxParser.handle(StartElement)", ex);
                }
            }
        }
    }

    /**
     * Calls endElement() for every Handler in the handler chain.
     *
     * @param endElement The {@code }{@link de.escidoc.core.common.util.xml.stax.events.EndElement EndElement}</code>
     * @throws ContextNotFoundException       eSciDoc specific; thrown by some Handlers.
     * @throws ContentModelNotFoundException  eSciDoc specific; thrown by some Handlers.
     * @throws MissingAttributeValueException eSciDoc specific; thrown by some Handlers.
     * @throws TripleStoreSystemException     eSciDoc specific; thrown by some Handlers.
     * @throws WebserverSystemException       eSciDoc specific; thrown by some Handlers.
     * @throws MissingContentException        eSciDoc specific; thrown by some Handlers.
     * @throws ReadonlyElementViolationException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws AlreadyExistsException         eSciDoc specific; thrown by some Handlers.
     * @throws InvalidContentException        eSciDoc specific; thrown by some Handlers.
     * @throws ReferencedResourceNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws RelationPredicateNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws ContentRelationNotFoundException
     *                                        eSciDoc specific; thrown by some Handlers.
     * @throws MissingMdRecordException       eSciDoc specific; thrown by some Handlers.
     * @throws TmeException                   eSciDoc specific; thrown by some Handlers.
     * @throws XmlCorruptedException          eSciDoc specific; thrown by some Handlers.
     */
    protected void handle(final EndElement endElement) throws MissingContentException,
        ReadonlyElementViolationException, AlreadyExistsException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, ContentRelationNotFoundException, ContextNotFoundException,
        ContentModelNotFoundException, MissingAttributeValueException, TripleStoreSystemException,
        WebserverSystemException, MissingMdRecordException, TmeException, InvalidContentException,
        XmlCorruptedException {

        EndElement element = endElement;
        for (final DefaultHandler handler : this.handlerChain) {
            if (handler != null) {
                try {
                    element = handler.endElement(element);
                }
                catch (final MissingContentException mce) {
                    throw mce;

                }
                catch (final MissingAttributeValueException mave) {
                    throw mave;
                }
                catch (final ReadonlyElementViolationException ex) {
                    throw ex;
                }
                catch (final AlreadyExistsException ex) {
                    throw ex;
                }
                catch (final ReferencedResourceNotFoundException ex) {
                    throw ex;
                }
                catch (final RelationPredicateNotFoundException ex) {
                    throw ex;
                }
                catch (final ContentRelationNotFoundException ex) {
                    throw ex;
                }
                catch (final XmlCorruptedException ex) {
                    throw ex;
                }
                catch (final TripleStoreSystemException ex) {
                    throw ex;
                }
                catch (final ContextNotFoundException ex) {
                    throw ex;
                }
                catch (final ContentModelNotFoundException ex) {
                    throw ex;
                }
                catch (final MissingMdRecordException ex) {
                    throw ex;
                }
                catch (final TmeException ex) {
                    throw ex;
                }
                catch (final InvalidContentException ex) {
                    throw ex;
                }
                catch (final Exception ex) {
                    throw new WebserverSystemException("Unexpected exception in handle().", ex);
                }
            }
        }
    }

    /**
     * Calls characters() for every Handler in the handler chain.
     *
     * @param characters Characters
     * @throws XMLStreamException           If parsing failed.
     * @throws ReadonlyAttributeViolationException
     *                                      eSciDoc specific; thrown by some Handlers.
     * @throws XmlParserSystemException     eSciDoc specific; thrown by some Handlers.
     * @throws TripleStoreSystemException   eSciDoc specific; thrown by some Handlers.
     * @throws ReadonlyElementViolationException
     *                                      eSciDoc specific; thrown by some Handlers.
     * @throws MissingElementValueException eSciDoc specific; thrown by some Handlers.
     * @throws ReferencedResourceNotFoundException
     *                                      eSciDoc specific; thrown by some Handlers.
     * @throws RelationPredicateNotFoundException
     *                                      eSciDoc specific; thrown by some Handlers.
     * @throws InvalidContentException      eSciDoc specific; thrown by some Handlers.
     * @throws OrganizationalUnitNotFoundException
     *                                      eSciDoc specific; thrown by some Handlers.
     * @throws PidAlreadyAssignedException  eSciDoc specific; thrown by some Handlers.
     * @throws IntegritySystemException     eSciDoc specific; thrown by some Handlers.
     * @throws InvalidStatusException       Thrown if an organizational unit is in an invalid status.
     * @throws TmeException                 eSciDoc specific; thrown by some Handlers.
     */
    protected void handle(final String characters) throws ReadonlyElementViolationException,
        MissingElementValueException, ReferencedResourceNotFoundException, RelationPredicateNotFoundException,
        ReadonlyAttributeViolationException, InvalidContentException, OrganizationalUnitNotFoundException,
        PidAlreadyAssignedException, XmlParserSystemException, TripleStoreSystemException, IntegritySystemException,
        InvalidStatusException, TmeException {

        String chars = characters;
        for (final DefaultHandler handler : this.handlerChain) {
            if (handler != null) {
                final StartElement e = startElements.peek();
                try {
                    chars = handler.characters(chars, e);
                }
                catch (final ReadonlyAttributeViolationException ex) {
                    throw ex;
                }
                catch (final ReadonlyElementViolationException ex) {
                    throw ex;
                }
                catch (final TripleStoreSystemException ex) {
                    throw ex;
                }
                catch (final MissingElementValueException ex) {
                    throw ex;
                }
                catch (final ReferencedResourceNotFoundException ex) {
                    throw ex;
                }
                catch (final RelationPredicateNotFoundException ex) {
                    throw ex;
                }
                catch (final InvalidContentException ex) {
                    throw ex;
                }
                catch (final InvalidStatusException ex) {
                    throw ex;
                }
                catch (final OrganizationalUnitNotFoundException ex) {
                    throw ex;
                }
                catch (final IntegritySystemException ex) {
                    throw ex;
                }
                catch (final PidAlreadyAssignedException ex) {
                    throw ex;
                }
                catch (final TmeException ex) {
                    throw ex;
                }
                catch (final Exception ex) {
                    XmlUtility.handleUnexpectedStaxParserException("", ex);
                }
            }
        }
    }
}
