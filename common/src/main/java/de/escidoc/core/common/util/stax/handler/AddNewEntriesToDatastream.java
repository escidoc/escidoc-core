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

import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.events.StartElementWithText;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class AddNewEntriesToDatastream extends DefaultHandler {

    private boolean inside = false;

    private String path = null;

    private int insideLevel = 0;

    private Map nsuris = null;

    private XMLStreamWriter writer = null;

    private ByteArrayOutputStream out = null;

    private StaxParser parser;

    private int deepLevel = 0;

    private List elementsToInsert = null;

    private StartElement pointerElement = null;

    public AddNewEntriesToDatastream(String path, StaxParser parser) {
        this.parser = parser;
        this.path = path;

    }

    public AddNewEntriesToDatastream(String path, StaxParser parser,
        List elementsToInsert, StartElement pointerElement) {
        this.parser = parser;
        this.path = path;
        this.elementsToInsert = elementsToInsert;
        this.pointerElement = pointerElement;

    }

    public void addElementToInsert(StartElementWithText element) {
        if (this.elementsToInsert == null) {
            this.elementsToInsert = new ArrayList();
        }
        this.elementsToInsert.add(element);
    }

    public ByteArrayOutputStream getOutputStreams() {
        return this.out;
    }

    public void setElementsToInsert(List elements) {
        this.elementsToInsert = elements;
    }

    public void setPointerElement(StartElement pointerElement) {
        this.pointerElement = pointerElement;
    }

    public StartElement startElement(StartElement element)
        throws XMLStreamException {

        deepLevel++;
        String theName = element.getLocalName();
        String nsuri = element.getNamespace();
        String prefix = element.getPrefix();

        if (inside) {
            writeElement(nsuri, theName, prefix, deepLevel);
            int attCount = element.getAttributeCount();
            for (int i = 0; i < attCount; i++) {
                Attribute curAtt = element.getAttribute(i);
                handleAttribute(curAtt, theName, deepLevel);

            }

            insideLevel++;
        }
        else {
            String currenrPath = parser.getCurPath();

            if (path.equals(currenrPath)) {
                this.out = new ByteArrayOutputStream();
                this.writer = XmlUtility.createXmlStreamWriter(out);
                inside = true;
                nsuris = new HashMap();
                writeElement(nsuri, theName, prefix, deepLevel);

                int attCount = element.getAttributeCount();
                for (int i = 0; i < attCount; i++) {
                    Attribute curAtt = element.getAttribute(i);
                    handleAttribute(curAtt, theName, deepLevel);
                }
                insideLevel++;
                if (insideLevel != 1) {
                    throw new XMLStreamException("insideLevel != 1: "
                        + insideLevel);
                }
            }

        }
        if (theName.equals(pointerElement.getLocalName())
            && nsuri.equals(pointerElement.getNamespace())
            && prefix.equals(pointerElement.getPrefix())) {
            Iterator it = elementsToInsert.iterator();
            while (it.hasNext()) {
                StartElementWithText inserted =
                    (StartElementWithText) it.next();
                String insertedName = inserted.getLocalName();
                String insertedNsUri = inserted.getNamespace();
                String insertedPrefix = inserted.getPrefix();
                writeElement(insertedNsUri, insertedName, insertedPrefix,
                    deepLevel + 1);

                int attCount = inserted.getAttributeCount();
                for (int i = 0; i < attCount; i++) {
                    Attribute curAtt = inserted.getAttribute(i);
                    handleAttribute(curAtt, theName, deepLevel + 1);
                }
                String insertedText = inserted.getElementText();
                if ((insertedText != null) && insertedText.length() > 0) {
                    writer.writeCharacters(inserted.getElementText());
                }
                writer.writeEndElement();
            }
        }

        // this have to be the last handler
        return element;
    }

    /*
     * public void startElement(XMLStreamReader xmlr, Vector pids) throws
     * XMLStreamException { }
     */

    public EndElement endElement(EndElement element) throws XMLStreamException {

        deepLevel--;

        if (inside) {
            insideLevel--;

            writer.writeEndElement();
            if (insideLevel == 0) {
                inside = false;

                writer.flush();
                writer.close();
            }
        }

        return element;
    }

    public String characters(String data, StartElement element)
        throws XMLStreamException {
        if (inside) {
            writer.writeCharacters(data);
        }
        return data;
    }

    private void writeElement(String uri, String name, String prefix, int deep)
        throws XMLStreamException {
        if ((uri) != null) {
            if (!nsuris.containsKey(uri)) {
                List namespaceTrace = new ArrayList();
                namespaceTrace.add(Integer.valueOf(deep));
                namespaceTrace.add(name);
                namespaceTrace.add(prefix);
                nsuris.put(uri, namespaceTrace);
                writer.writeStartElement(prefix, name, uri);
                writer.writeNamespace(prefix, uri);
            }
            else {
                Vector namespaceTrace = (Vector) nsuris.get(uri);
                Integer deepLevelInMAp = (Integer) namespaceTrace.get(0);
                String prefixTrace = (String) namespaceTrace.get(2);
                if (!prefixTrace.equals(prefix)) {
                    prefix = prefixTrace;
                }
                if (deepLevelInMAp.intValue() >= deep) {
                    writer.writeStartElement(prefix, name, uri);
                    writer.writeNamespace(prefix, uri);
                }
                else {
                    writer.writeStartElement(prefix, name, uri);
                }
            }
        }
        else {
            writer.writeStartElement(name);
        }

    }

    private void writeAttribute(
        String uri, String elementName, String attributeName,
        String attributeValue, String prefix, int deep)
        throws XMLStreamException {
        if (uri != null) {
            if (!nsuris.containsKey(uri)) {
                List namespaceTrace = new ArrayList();
                namespaceTrace.add(Integer.valueOf(deep));
                namespaceTrace.add(elementName);
                namespaceTrace.add(prefix);
                nsuris.put(uri, namespaceTrace);

                writer.writeNamespace(prefix, uri);
            }
            else {
                Vector namespaceTrace = (Vector) nsuris.get(uri);
                String prefixTrace = (String) namespaceTrace.get(2);
                if (!prefixTrace.equals(prefix)) {
                    prefix = prefixTrace;
                }
                Integer deepLevelInMAp = (Integer) namespaceTrace.get(0);
                String nameTrace = (String) namespaceTrace.get(1);
                if (((deepLevelInMAp.intValue() == deep) && (!elementName
                    .equals(nameTrace)))
                    || (deepLevelInMAp.intValue() > deep)) {
                    writer.writeNamespace(prefix, uri);
                }
            }
        }
        writer.writeAttribute(prefix, uri, attributeName, attributeValue);
    }

    private void handleAttribute(Attribute attribute, String theName, int deep)
        throws XMLStreamException {
        String attLocalName = attribute.getLocalName();
        String attrNameSpace = attribute.getNamespace();
        String attrPrefix = attribute.getPrefix();
        String attValue = attribute.getValue();
        writeAttribute(attrNameSpace, theName, attLocalName, attValue,
            attrPrefix, deep);

    }

}
