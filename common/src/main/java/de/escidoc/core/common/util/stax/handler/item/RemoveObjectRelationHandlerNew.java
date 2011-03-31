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

package de.escidoc.core.common.util.stax.handler.item;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RemoveObjectRelationHandlerNew extends DefaultHandler {

    private List<String> objects;

    private final XMLStreamWriter writer;

    private final OutputStream outputStream;

    private final Map nsuris;

    private int deepLevel;

    private boolean justRemoved;

    /**
     * Counter for removed objects.
     */
    private int noOfRemoved;

    public RemoveObjectRelationHandlerNew(final String objid) throws WebserverSystemException {
        // TODO Auto-generated constructor stub
        this.objects = new ArrayList<String>();
        this.objects.add(objid);
        this.deepLevel = 0;
        this.noOfRemoved = 0;

        this.nsuris = new HashMap();

        this.outputStream = new ByteArrayOutputStream();
        try {
            this.writer = XmlUtility.createXmlStreamWriter(this.outputStream);
        }
        catch (final XMLStreamException e) {
            throw new WebserverSystemException(e);
        }
    }

    @Override
    public String characters(final String data, final StartElement element) throws XMLStreamException {

        writer.writeCharacters(data);

        return data;
    }

    @Override
    public EndElement endElement(final EndElement element) throws XMLStreamException {
        if (this.justRemoved) {
            this.justRemoved = false;
            return element;
        }
        writer.writeEndElement();
        this.deepLevel--;
        return element;
    }

    @Override
    public StartElement startElement(final StartElement element) throws XMLStreamException {

        final int k = element.indexOfAttribute(Constants.RDF_NAMESPACE_URI, "resource");
        if (k != -1) {
            final Iterator<String> it = objects.iterator();
            while (it.hasNext()) {
                final String obj = it.next();
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
        final int attCount = element.getAttributeCount();

        for (int i = 0; i < attCount; i++) {
            final Attribute att = element.getAttribute(i);
            // String namespace = att.getNamespace();
            writeAttribute(att.getNamespace(), element.getLocalName(), att.getLocalName(), att.getValue(), att
                .getPrefix());

        }

        return element;
    }

    public OutputStream getOutputStream() throws XMLStreamException {
        writer.flush();
        writer.close();
        return this.outputStream;
    }

    /**
     * Set objid to remove.
     *
     * @param objects The objid to remove.
     */
    public void setObjects(final List<String> objects) {
        this.objects = objects;
    }

    /**
     * Add objid to the remove list.
     *
     * @param objid objid to add to the remove list
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

    private void writeElement(final StartElement element) throws XMLStreamException {
        this.deepLevel++;
        final String name = element.getLocalName();
        final String uri = element.getNamespace();
        String prefix = element.getPrefix();
        if (uri != null) {
            if (nsuris.containsKey(uri)) {
                final List namespaceTrace = (List) nsuris.get(uri);
                final Integer deepLevelInMAp = (Integer) namespaceTrace.get(0);
                final String prefixTrace = (String) namespaceTrace.get(2);
                if (!prefixTrace.equals(prefix)) {
                    prefix = prefixTrace;
                }
                if (deepLevelInMAp >= this.deepLevel) {
                    writer.writeStartElement(prefix, name, uri);
                    writer.writeNamespace(prefix, uri);
                }
                else {
                    writer.writeStartElement(prefix, name, uri);
                }
            }
            else {
                final List namespaceTrace = new ArrayList();
                namespaceTrace.add(this.deepLevel);
                namespaceTrace.add(name);
                namespaceTrace.add(prefix);
                nsuris.put(uri, namespaceTrace);
                writer.writeStartElement(prefix, name, uri);
                writer.writeNamespace(prefix, uri);
            }
        }

    }

    private void writeAttribute(
        final String uri, final String elementName, final String attributeName, final String attributeValue,
        String prefix) throws XMLStreamException {
        if (uri != null) {
            if (nsuris.containsKey(uri)) {
                final List namespaceTrace = (List) nsuris.get(uri);
                final String prefixTrace = (String) namespaceTrace.get(2);
                if (!prefixTrace.equals(prefix)) {
                    prefix = prefixTrace;
                }
                final Integer deepLevelInMAp = (Integer) namespaceTrace.get(0);
                final String nameTrace = (String) namespaceTrace.get(1);
                if (deepLevelInMAp == this.deepLevel && !elementName.equals(nameTrace)
                    || deepLevelInMAp > this.deepLevel) {
                    writer.writeNamespace(prefix, uri);
                }
            }
            else {
                final List namespaceTrace = new ArrayList();
                namespaceTrace.add(this.deepLevel);
                namespaceTrace.add(elementName);
                namespaceTrace.add(prefix);
                nsuris.put(uri, namespaceTrace);

                writer.writeNamespace(prefix, uri);
            }
        }
        writer.writeAttribute(prefix, uri, attributeName, attributeValue);
    }
}
