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
package de.escidoc.core.common.util.stax.handler.item;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

public class RemoveObjectRelationHandlerNew extends DefaultHandler {

    private Vector<String> objects;

    private XMLStreamWriter writer;

    private OutputStream outputStream;

    private StaxParser parser;

    private Stack startElements;

    private Map nsuris;

    private int deepLevel = 0;

    private boolean justRemoved;

    /**
     * Counter for removed objects.
     */
    private int noOfRemoved = 0;

    /**
     * 
     * @param objects
     * @param parser
     * @throws XMLStreamException
     */
    public RemoveObjectRelationHandlerNew(Vector<String> objects,
        StaxParser parser) throws XMLStreamException {
        // TODO Auto-generated constructor stub
        this.objects = objects;
        this.parser = parser;
        this.deepLevel = 0;
        this.noOfRemoved = 0;

        this.nsuris = new HashMap();
        this.startElements = new Stack();

        outputStream = new ByteArrayOutputStream();
        writer = XmlUtility.createXmlStreamWriter(outputStream);
    }

    public RemoveObjectRelationHandlerNew(final String objid, StaxParser parser)
        throws WebserverSystemException {
        // TODO Auto-generated constructor stub
        this.objects = new Vector<String>();
        this.objects.add(objid);
        this.parser = parser;
        this.deepLevel = 0;
        this.noOfRemoved = 0;

        this.nsuris = new HashMap();
        this.startElements = new Stack();

        outputStream = new ByteArrayOutputStream();
        try {
            writer = XmlUtility.createXmlStreamWriter(outputStream);
        }
        catch (XMLStreamException e) {
            throw new WebserverSystemException(e);
        }
    }

    public String characters(String data, StartElement element)
        throws XMLStreamException {

        writer.writeCharacters(data);

        return data;
    }

    public EndElement endElement(EndElement element) throws XMLStreamException {
        if (justRemoved) {
            justRemoved = false;
            return element;
        }
        writer.writeEndElement();
        deepLevel--;
        return element;
    }

    public StartElement startElement(StartElement element)
        throws XMLStreamException {

        int k =
            element.indexOfAttribute(
                de.escidoc.core.common.business.Constants.RDF_NAMESPACE_URI,
                "resource");
        if (k != -1) {
            Iterator<String> it = objects.iterator();
            while (it.hasNext()) {
                String obj = it.next();
                String value = element.getAttribute(k).getValue();
                value = XmlUtility.getIdFromURI(value);
                if (value.equals(obj)) {
                    it.remove();
                    this.justRemoved = true;
                    this.noOfRemoved++;
                    return null;
                }
            }
        }
        writeElement(element);
        int attCount = element.getAttributeCount();

        for (int i = 0; i < attCount; i++) {
            Attribute att = element.getAttribute(i);
            // String namespace = att.getNamespace();
            writeAttribute(att.getNamespace(), element.getLocalName(), att
                .getLocalName(), att.getValue(), att.getPrefix());

        }

        return element;
    }

    public OutputStream getOutputStream() throws XMLStreamException {
        writer.flush();
        writer.close();
        return outputStream;
    }

    /**
     * Set objid to remove.
     * 
     * @param objects
     *            The objid to remove.
     */
    public void setObjects(final Vector<String> objects) {
        this.objects = objects;
    }

    /**
     * Add objid to the remove list.
     * 
     * @param objid
     *            objid to add to the remove list
     */
    public void addObject(final String objid) {
        this.objects.add(objid);
    }

    /**
     * Get the number of removed objects.
     * 
     * @return number of removed objects.
     */
    public int getNoOfRemovedObjects() {
        return this.noOfRemoved;
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
