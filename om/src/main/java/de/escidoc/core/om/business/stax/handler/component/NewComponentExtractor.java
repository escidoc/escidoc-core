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
package de.escidoc.core.om.business.stax.handler.component;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NewComponentExtractor extends DefaultHandler {

    private XMLStreamWriter writer;

    private final List<ByteArrayOutputStream> outputStreams = new ArrayList<ByteArrayOutputStream>();

    private final StaxParser parser;

    private boolean inside;

    private final Map<String, List> nsuris = new HashMap<String, List>();

    private int deepLevel;

    public NewComponentExtractor(final StaxParser parser) {
        // TODO Auto-generated constructor stub
        this.parser = parser;
    }

    @Override
    public String characters(final String data, final StartElement element) throws XMLStreamException {
        if (this.inside) {
            writer.writeCharacters(data);
        }
        return data;
    }

    @Override
    public EndElement endElement(final EndElement element) throws XMLStreamException {
        final String curPath = parser.getCurPath();

        if (this.inside) {
            writer.writeEndElement();
            this.deepLevel--;

            final String ns = element.getNamespace();
            List nsTrace = nsuris.get(ns);

            if (nsTrace != null && (nsTrace.get(2) == null || nsTrace.get(2).equals(element.getPrefix()))
                && nsTrace.get(1).equals(element.getLocalName()) && (Integer) nsTrace.get(0) == this.deepLevel + 1) {

                nsuris.remove(ns);

            }

            // attribute namespaces
            // TODO iteration is a hack, use
            // javax.xml.namespace.NamespaceContext
            Iterator<String> it = nsuris.keySet().iterator();
            final Collection<String> toRemove = new ArrayList<String>();
            while (it.hasNext()) {
                final String key = it.next();
                nsTrace = nsuris.get(key);
                if ((Integer) nsTrace.get(0) == this.deepLevel + 1) {
                    toRemove.add(key);
                }
            }
            it = toRemove.iterator();
            while (it.hasNext()) {
                final String key = it.next();
                nsuris.remove(key);
            }
            if (curPath.endsWith("components/component")) {
                this.inside = false;
                writer.flush();
                writer.close();
            }
        }
        return element;
    }

    @Override
    public StartElement startElement(final StartElement element) throws XMLStreamException {
        final String curPath = parser.getCurPath();
        if (this.inside) {
            this.deepLevel++;
            writeElement(element);

            final int attCount = element.getAttributeCount();
            for (int i = 0; i < attCount; i++) {
                final Attribute curAtt = element.getAttribute(i);
                writeAttribute(curAtt.getNamespace(), element.getLocalName(), curAtt.getLocalName(), curAtt.getValue(),
                    curAtt.getPrefix());
            }
        }
        else {
            if (curPath.endsWith("components/component")) {
                final int indexObjid = element.indexOfAttribute(null, "objid");
                final int indexHref = element.indexOfAttribute(Constants.XLINK_NS_URI, "href");
                if (!(indexObjid > -1 && element.getAttribute(indexObjid).getValue().length() > 0 || indexHref > -1
                    && Utility.getId(element.getAttribute(indexHref).getValue()).length() > 0)) {

                    // start new component if there is no ID
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();

                    this.writer = XmlUtility.createXmlStreamWriter(out);
                    outputStreams.add(out);

                    this.inside = true;
                    this.deepLevel++;
                    writeElement(element);

                    final int attCount = element.getAttributeCount();
                    for (int i = 0; i < attCount; i++) {
                        final Attribute curAtt = element.getAttribute(i);
                        writeAttribute(curAtt.getNamespace(), element.getLocalName(), curAtt.getLocalName(), curAtt
                            .getValue(), curAtt.getPrefix());
                    }
                }
            }
        }
        return element;
    }

    private void writeElement(final StartElement element) throws XMLStreamException {
        final String name = element.getLocalName();
        final String uri = element.getNamespace();
        String prefix = element.getPrefix();
        if (uri != null) {
            if (nsuris.containsKey(uri)) {
                final List namespaceTrace = nsuris.get(uri);
                final Integer deepLevelInMAp = (Integer) namespaceTrace.get(0);
                final String prefixTrace = (String) namespaceTrace.get(2);
                if (prefixTrace == null || !prefixTrace.equals(prefix)) {
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
        else {
            writer.writeStartElement(name);
        }

    }

    private void writeAttribute(
        final String uri, final String elementName, final String attributeName, final String attributeValue,
        String prefix) throws XMLStreamException {

        if (uri != null) {
            if (nsuris.containsKey(uri)) {
                final List namespaceTrace = nsuris.get(uri);
                final String prefixTrace = (String) namespaceTrace.get(2);
                if (!prefixTrace.equals(prefix)) {
                    prefix = prefixTrace;
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

    public List<ByteArrayOutputStream> getOutputStreams() {
        return this.outputStreams;
    }
}
