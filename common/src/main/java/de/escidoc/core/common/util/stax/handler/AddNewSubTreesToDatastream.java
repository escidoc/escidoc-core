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

import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.events.StartElementWithChildElements;
import de.escidoc.core.common.util.xml.stax.events.StartElementWithText;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import javax.naming.directory.NoSuchAttributeException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This handler must be the last in the chain! Adds a new sub-tree to the data stream.
 */
public class AddNewSubTreesToDatastream extends DefaultHandler {

    private boolean inside;

    private final String path;

    private int insideLevel;

    private Map nsuris;

    private XMLStreamWriter writer;

    private ByteArrayOutputStream out;

    private boolean isParsed;

    private final StaxParser parser;

    private int deepLevel;

    private List<StartElementWithChildElements> subtreesToInsert;

    private StartElement pointerElement;

    /**
     * Must be the <b>last</b> handler in the handler chain!
     * @param path
     * @param parser
     */
    public AddNewSubTreesToDatastream(final String path, final StaxParser parser) {
        this.parser = parser;
        this.path = path;

    }

    /**
     * Must be the last handler in the handler chain.
     * @param path
     * @param parser
     * @param subtreesToInsert
     * @param pointerElement
     */
    public AddNewSubTreesToDatastream(final String path, final StaxParser parser,
        final List<StartElementWithChildElements> subtreesToInsert, final StartElement pointerElement) {

        this.parser = parser;
        this.path = path;
        this.subtreesToInsert = subtreesToInsert;
        this.pointerElement = pointerElement;

    }

    public void addSubteeToSubtreesVector(final StartElementWithChildElements subtreeToInsert) {
        if (this.subtreesToInsert == null) {
            this.subtreesToInsert = new ArrayList<StartElementWithChildElements>();
        }
        this.subtreesToInsert.add(subtreeToInsert);

    }

    public void setSubtreeToInsert(final List<StartElementWithChildElements> subtreesToInsert) {
        this.subtreesToInsert = subtreesToInsert;
    }

    public ByteArrayOutputStream getOutputStreams() {
        return this.out;
    }

    public void setPointerElement(final StartElement pointerElement) {
        this.pointerElement = pointerElement;
    }

    /**
     * @param element - StartElement
     * @return StarteElement
     * @throws XMLStreamException In case of parse error.
     */

    // TODO: Now there is no need to differentiate between RELS-EXT and other
    // data streams while writing namespace uri (with "/" on the end, or without
    // "/"
    // on the end, because all namespaces in RELS-EXT have now "/" on the end.
    // Therefore delete variables isRelsExt, isNew, isContentRelation
    @Override
    public StartElement startElement(final StartElement element) throws XMLStreamException {
        this.deepLevel++;
        final String theName = element.getLocalName();
        final String nsuri = element.getNamespace();
        final String prefix = element.getPrefix();

        if (this.inside) {
            writeElement(nsuri, theName, prefix, this.deepLevel);
            final List<Attribute> attributes = element.getAttributes();
            for (final Attribute curAtt : attributes) {
                handleAttribute(curAtt, theName, this.deepLevel);
            }
            this.insideLevel++;
        }
        else {
            final String currentPath = parser.getCurPath();
            if (path.equals(currentPath)) {
                this.out = new ByteArrayOutputStream();
                this.writer = XmlUtility.createXmlStreamWriter(this.out);
                this.inside = true;
                this.nsuris = new HashMap();
                writeElement(nsuri, theName, prefix, this.deepLevel);
                final List<Attribute> attributes = element.getAttributes();
                for (final Attribute curAtt : attributes) {
                    handleAttribute(curAtt, theName, this.deepLevel);
                }
                this.insideLevel++;
                if (this.insideLevel != 1) {
                    throw new XMLStreamException("insideLevel != 1: " + this.insideLevel);
                }
            }

        }
        if (theName.equals(pointerElement.getLocalName()) && nsuri.equals(pointerElement.getNamespace())
            && prefix.equals(pointerElement.getPrefix()) && !this.isParsed && equalAttr(this.pointerElement, element)) {
            for (final StartElementWithChildElements subtreeToInsert : this.subtreesToInsert) {
                final String subtreeName = subtreeToInsert.getLocalName();
                final String subtreeNsUri = subtreeToInsert.getNamespace();
                final String subtreePrefix = subtreeToInsert.getPrefix();
                writeElement(subtreeNsUri, subtreeName, subtreePrefix, this.deepLevel + 1);
                final int attCount = subtreeToInsert.getAttributeCount();
                for (int j = 0; j < attCount; j++) {
                    final Attribute curAtt = subtreeToInsert.getAttribute(j);
                    handleAttribute(curAtt, theName, this.deepLevel + 1);
                }
                final String subtreeText = subtreeToInsert.getElementText();
                if (subtreeText != null && subtreeText.length() > 0) {
                    writer.writeCharacters(subtreeText);
                    writer.flush();
                }
                final List<StartElementWithText> children = subtreeToInsert.getChildrenElements();
                if (children != null && !children.isEmpty()) {
                    for (final StartElementWithText inserted : children) {
                        final String insertedName = inserted.getLocalName();
                        final String insertedNsUri = inserted.getNamespace();
                        final String insertedPrefix = inserted.getPrefix();
                        writeElement(insertedNsUri, insertedName, insertedPrefix, this.deepLevel + 2);

                        final int attCount2 = inserted.getAttributeCount();
                        for (int j = 0; j < attCount2; j++) {
                            final Attribute curAtt = inserted.getAttribute(j);
                            handleAttribute(curAtt, theName, this.deepLevel + 2);
                        }
                        final String insertedText = inserted.getElementText();
                        if (insertedText != null && insertedText.length() > 0) {
                            writer.writeCharacters(insertedText);
                            writer.flush();
                        }
                        writer.writeEndElement();
                    }
                }
                writer.writeEndElement();
                writer.flush();
            }
            this.isParsed = true;
        }
        // this have to be the last handler
        return null;
    }

    /*
     * public void startElement(XMLStreamReader xmlr, Vector pids) throws
     * XMLStreamException { }
     */

    @Override
    public EndElement endElement(final EndElement element) throws XMLStreamException {
        this.deepLevel--;
        if (this.inside) {
            this.insideLevel--;

            writer.writeEndElement();
            // remove namespace if is defined in this element
            final String ns = element.getNamespace();
            List nsTrace = (List) nsuris.get(ns);

            if (nsTrace != null && (nsTrace.get(2) == null || nsTrace.get(2).equals(element.getPrefix()))
                && nsTrace.get(1).equals(element.getLocalName()) && (Integer) nsTrace.get(0) == this.deepLevel + 1) {

                nsuris.remove(ns);

            }

            // attribute namespaces
            // TODO iteration is a hack, use javax.xml.namespace.NamespaceContext
            final Iterator it = nsuris.keySet().iterator();
            final Collection<String> toRemove = new ArrayList<String>();
            while (it.hasNext()) {
                try {
                    final String key = (String) it.next();
                    nsTrace = (List) nsuris.get(key);
                    if ((Integer) nsTrace.get(0) == this.deepLevel + 1) {
                        toRemove.add(key);
                    }
                }
                catch (final Exception e) {
                    throw new XMLStreamException(e.getMessage(), e);
                }
            }
            for (final String key : toRemove) {
                nsuris.remove(key);
            }
            if (this.insideLevel == 0) {
                this.inside = false;

                writer.flush();
                writer.close();
            }
        }

        return null;
    }

    @Override
    public String characters(final String data, final StartElement element) throws XMLStreamException {
        if (this.inside) {
            writer.writeCharacters(data);
        }
        return null;
    }

    private void writeElement(final String uri, final String name, final String prefix, final int deep)
        throws XMLStreamException {
        String myPrefix = prefix;
        if (uri != null) {
            if (nsuris.containsKey(uri)) {
                final List namespaceTrace = (List) nsuris.get(uri);
                final Integer deepLevelInMAp = (Integer) namespaceTrace.get(0);
                final String prefixTrace = (String) namespaceTrace.get(2);
                if (!prefixTrace.equals(myPrefix)) {
                    myPrefix = prefixTrace;
                }
                if (deepLevelInMAp >= deep) {
                    writer.writeStartElement(myPrefix, name, uri);
                    writer.writeNamespace(myPrefix, uri);
                }
                else {
                    writer.writeStartElement(myPrefix, name, uri);
                }
            }
            else {
                final List namespaceTrace = new ArrayList();
                namespaceTrace.add(deep);
                namespaceTrace.add(name);
                namespaceTrace.add(myPrefix);
                nsuris.put(uri, namespaceTrace);
                writer.writeStartElement(myPrefix, name, uri);
                writer.writeNamespace(myPrefix, uri);
            }
        }
        else {
            writer.writeStartElement(name);
        }

    }

    private void writeAttribute(
        final String uri, final String elementName, final String attributeName, final String attributeValue,
        String prefix, final int deep) throws XMLStreamException {
        if (uri != null) {
            if (nsuris.containsKey(uri)) {
                final List namespaceTrace = (List) nsuris.get(uri);
                final String prefixTrace = (String) namespaceTrace.get(2);
                if (!prefixTrace.equals(prefix)) {
                    prefix = prefixTrace;
                }
                final Integer deepLevelInMAp = (Integer) namespaceTrace.get(0);
                final String nameTrace = (String) namespaceTrace.get(1);
                if (deepLevelInMAp == deep && !elementName.equals(nameTrace) || deepLevelInMAp > deep) {
                    writer.writeNamespace(prefix, uri);
                }
            }
            else {
                final List namespaceTrace = new ArrayList();
                namespaceTrace.add(deep);
                namespaceTrace.add(elementName);
                namespaceTrace.add(prefix);
                nsuris.put(uri, namespaceTrace);
                writer.writeNamespace(prefix, uri);
            }
        }
        writer.writeAttribute(prefix, uri, attributeName, attributeValue);
    }

    private void handleAttribute(final Attribute attribute, final String theName, final int deep)
        throws XMLStreamException {
        final String attLocalName = attribute.getLocalName();
        final String attrNameSpace = attribute.getNamespace();
        final String attrPrefix = attribute.getPrefix();
        final String attValue = attribute.getValue();
        writeAttribute(attrNameSpace, theName, attLocalName, attValue, attrPrefix, deep);

    }

    /**
     * Compares all attributes of pointerElement with attributes of element. StartElement element must have all
     * attributes of pointerElement with equal value.
     *
     * @param pointerElement
     * @param element
     * @return true - If all attributes of pointerElement exist in element and there values are equal. Other attibutes
     *         (of element) are not compared. false - otherwise
     */
    private static boolean equalAttr(final StartElement pointerElement, final StartElement element) {
        try {
            final int pointerAttsNo = pointerElement.getAttributeCount();
            for (int i = 0; i < pointerAttsNo; i++) {
                final String attName = pointerElement.getAttribute(i).getLocalName();
                final String attNS = pointerElement.getAttribute(i).getNamespace();

                if (!pointerElement.getAttribute(i).getValue().equals(element.getAttribute(attNS, attName).getValue())) {
                    return false;
                }
            }
        }
        catch (final IndexOutOfBoundsException e) { // TODO: Refactor this. Don't use exception for controll flow!
            return false;
        }
        catch (final NoSuchAttributeException e) { // TODO: Refactor this. Don't use exception for controll flow!
            return false;
        }

        return true;
    }
}
