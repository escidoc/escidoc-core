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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

public class RemoveObjectRelationHandler extends DefaultHandler {

    private Vector objects;

    private XMLStreamWriter writer;

    private OutputStream outputStream;

    private StaxParser parser;

    private Stack startElements;

    private Map nsuris;

    private int deepLevel = 0;

    private boolean justRemoved;

    public RemoveObjectRelationHandler(Vector objects, StaxParser parser)
        throws XMLStreamException {
        // TODO Auto-generated constructor stub
        this.objects = objects;
        this.parser = parser;
        this.deepLevel = 0;

        this.nsuris = new HashMap();
        this.startElements = new Stack();

        outputStream = new ByteArrayOutputStream();
        writer = XmlUtility.createXmlStreamWriter(outputStream);
    }

    public RemoveObjectRelationHandler(String objid, StaxParser parser)
        throws XMLStreamException {
        // TODO Auto-generated constructor stub
        this.objects = new Vector();
        this.objects.add(objid);
        this.parser = parser;
        this.deepLevel = 0;

        this.nsuris = new HashMap();
        this.startElements = new Stack();

        outputStream = new ByteArrayOutputStream();
        writer = XmlUtility.createXmlStreamWriter(outputStream);
    }

    public String characters(String data, StartElement element)
        throws Exception {
        Iterator it = objects.iterator();
        while (it.hasNext()) {
            String obj = (String) it.next();
            if (data.equals(obj)) {
                it.remove();
                justRemoved = true;
                return null;
            }
        }
        writeElement(element);
        int attCount = element.getAttributeCount();
        for (int i = 0; i < attCount; i++) {
            Attribute att = element.getAttribute(i);
            writeAttribute(att.getNamespace(), element.getLocalName(), att
                .getLocalName(), att.getValue(), att.getPrefix());
        }
        writer.writeCharacters(data);
        // startElements.pop();
        return data;
    }

    public EndElement endElement(EndElement element) throws Exception {
        if (justRemoved) {
            justRemoved = false;
            return element;
        }
        writer.writeEndElement();
        deepLevel--;
        return element;
    }

    public StartElement startElement(StartElement element) throws Exception {
        // if (!startElements.empty()) {
        // StartElement latest = (StartElement) startElements.pop();
        // writeElement(latest);
        // int attCount = latest.getAttributeCount();
        // for (int i = 0; i < attCount; i++) {
        // Attribute att = latest.getAttribute(i);
        // writeAttribute(att.getNamespace(), latest.getLocalName(), att
        // .getLocalName(), att.getValue(), att.getPrefix());
        // }
        // }
        // startElements.push(element);
        // deepLevel++;
        if (deepLevel <= 1) {
            writeElement(element);
            int attCount = element.getAttributeCount();
            for (int i = 0; i < attCount; i++) {
                Attribute att = element.getAttribute(i);
                writeAttribute(att.getNamespace(), element.getLocalName(), att
                    .getLocalName(), att.getValue(), att.getPrefix());
            }

        }
        return element;
    }

    public OutputStream getOutputStream() throws XMLStreamException {
        writer.flush();
        writer.close();
        return outputStream;
    }

    public void setObjects(Vector objects) {
        this.objects = objects;
    }

    public void addObject(String objid) {
        this.objects.add(objid);
    }

    private void writeElement(StartElement element) throws XMLStreamException {
        deepLevel++;
        String name = element.getLocalName();
        String uri = element.getNamespace();
        String prefix = element.getPrefix();
        if ((uri) != null) {
            if (!nsuris.containsKey(uri)) {
                Vector namespaceTrace = new Vector();
                namespaceTrace.add(new Integer(deepLevel));
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
                if (deepLevelInMAp.intValue() >= deepLevel) {
                    writer.writeStartElement(prefix, name, uri);
                    writer.writeNamespace(prefix, uri);
                }
                else {
                    writer.writeStartElement(prefix, name, uri);
                }
            }
        }

    }

    private void writeAttribute(
        String uri, String elementName, String attributeName,
        String attributeValue, String prefix) throws XMLStreamException {
        if (uri != null) {
            if (!nsuris.containsKey(uri)) {
                Vector namespaceTrace = new Vector();
                namespaceTrace.add(new Integer(deepLevel));
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
                if (((deepLevelInMAp.intValue() == deepLevel) && (!elementName
                    .equals(nameTrace)))
                    || (deepLevelInMAp.intValue() > deepLevel)) {
                    writer.writeNamespace(prefix, uri);
                }
            }
        }
        writer.writeAttribute(prefix, uri, attributeName, attributeValue);
    }
}
