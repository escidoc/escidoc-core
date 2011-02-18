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
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MultipleExtractor2 extends DefaultHandler {

    private boolean inside = false;

    private Map<String, String> paths = null;

    private XMLStreamWriter writer;

    private int insideLevel = 0;

    private Map<String, OutputStream> metadata = null;

    private Map<String, Map> components = null;

    private final Map<String, Object> outputStreams =
        new HashMap<String, Object>();

    private String componentId = null;

    private boolean inComponent = false;

    private final StaxParser parser;

    private Map<String, String> namespaceMap;

    private int number = 0;

    private List<String> pids = null;

    /**
     * @param namespaceMap
     *            Map from namespace to prefix.
     * @param extractPathes
     * @param parser
     */
    public MultipleExtractor2(final Map<String, String> namespaceMap,
        final Map<String, String> extractPathes, final StaxParser parser) {

        // FIXME: as parameter
        this.namespaceMap = namespaceMap;

        this.parser = parser;
        this.paths = extractPathes;
    }

    public MultipleExtractor2(final Map<String, String> extractPathes,
        final StaxParser parser) {

        this(null, extractPathes, parser);
    }

    public MultipleExtractor2(final String extractPath,
        final String extractAtt, StaxParser parser) {

        this.parser = parser;
        this.paths = new HashMap<String, String>();
        this.paths.put(extractPath, extractAtt);
    }

    public final Map<String, Object> getOutputStreams() {

        return this.outputStreams;
    }

    public void setPids(final List<String> pids) {

        this.pids = pids;
    }

    public StartElement startElement(final StartElement element)
        throws XMLStreamException {
        String elementName = element.getLocalName();
        if (elementName.equals("component")) {
            inComponent = true;
            if (pids != null) {
                componentId = pids.get(number);
                number++;
            }
            else {
                int index = element.indexOfAttribute(null, "objid");
                if (index != -1) {
                    String value = element.getAttribute(index).getValue();
                    if ((value != null) && (value.length() > 0)) {
                        componentId = value;
                    }
                }

            }
        }

        if (!inside) {
            String currentPath = parser.getCurPath();
            if (paths.containsKey(currentPath)) {
                if (insideLevel != 0) {
                    throw new XMLStreamException("insideLevel != 0: "
                        + insideLevel);
                }

                inside = true;
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                writer = newInitializedWriter(out);

                final String attributeName = paths.get(currentPath);
                final Map<String, Object> component;
                Map<String, OutputStream> mdRecords;
                if (inComponent) {
                    if (components == null) {
                        components = new HashMap<String, Map>();
                        outputStreams.put("components", components);
                    }
                    if (!components.containsKey(componentId)) {
                        component = new HashMap<String, Object>();
                        components.put(componentId, component);
                    }
                    else {
                        component = (HashMap) components.get(componentId);
                    }

                    if (attributeName == null) {
                        component.put(elementName, out);
                    }
                    else {
                        String attributeValue =
                            getAttributeValue(element, null, attributeName);
                        if (elementName.equals("md-record")) {
                            mdRecords = components.get(components);
                            if (mdRecords == null) {
                                mdRecords = new HashMap<String, OutputStream>();
                                component.put("md-records", mdRecords);
                            }
                            mdRecords.put(attributeValue, out);
                        }
                        else {
                            component.put(attributeValue, out);
                        }
                    }
                }
                else {
                    if (attributeName == null) {
                        outputStreams.put(elementName, out);
                    }
                    else {
                        String attributeValue =
                            getAttributeValue(element, null, attributeName);
                        if (elementName.equals("md-record")) {
                            if (metadata == null) {
                                metadata = new HashMap<String, OutputStream>();
                                outputStreams.put("md-records", metadata);
                            }
                            metadata.put(attributeValue, out);
                        }
                        else {
                            outputStreams.put(attributeValue, out);
                        }
                    }
                }
            }
        }

        // write start element with attributes (and implicit neccessary
        // namespace declarations due to the repairing xml writer
        if (inside) {
            String namespace = element.getNamespace();
            if (namespace != null && !namespaceMap.containsKey(namespace)) {
                String prefix = element.getPrefix();
                if (prefix != null) {
                    writer.setPrefix(prefix, element.getNamespace());
                }
                else {
                    writer.setDefaultNamespace(element.getNamespace());
                }
            }

            if (!(elementName.equals("md-record") && paths.containsKey(parser
                .getCurPath()))) {
                writer.writeStartElement(element.getNamespace(), elementName);
            }
            int attCount = element.getAttributeCount();
            for (int i = 0; i < attCount; i++) {
                Attribute curAtt = element.getAttribute(i);
                namespace = curAtt.getNamespace();
                if (namespace != null && !namespaceMap.containsKey(namespace)) {
                    // Prefix is not null. (FRS)
                    writer.setPrefix(curAtt.getPrefix(), namespace);
                }
                if (!(elementName.equals("md-record") && paths
                    .containsKey(parser.getCurPath()))) {
                    writer.writeAttribute(namespace, curAtt.getLocalName(),
                        curAtt.getValue());
                }
            }
            insideLevel++;
        }

        // this has to be the last handler
        return element;
    }

    public EndElement endElement(EndElement element) throws XMLStreamException {
        String theName = element.getLocalName();

        if (theName.equals("component")) {
            if (componentId == null) {
                Map components = (HashMap) outputStreams.get("components");
                components.remove(componentId);
            }
            inComponent = false;
            componentId = null;

        }
        if (inside) {
            insideLevel--;
            if ((insideLevel > 0)
                || ((insideLevel == 0) && !theName.equals("md-record"))) {
                writer.writeEndElement();
            }

            if (insideLevel == 0) {
                inside = false;
                writer.flush();
                writer.close();
            }
        }

        return element;
    }

    public String characters(final String data, final StartElement element)
        throws XMLStreamException {

        if (inside) {
            writer.writeCharacters(data);
        }
        return data;
    }

    /**
     * Creates a new initialized writer.<br/>
     * The writer's prefixes are initialized to the values of the prefixMap.
     * 
     * @param out
     * @return Returns the initialized <code>XmlStreamWriter</code>X instance.
     * @throws XMLStreamException
     */
    private XMLStreamWriter newInitializedWriter(final ByteArrayOutputStream out)
        throws XMLStreamException {

        XMLStreamWriter writer =
            XmlUtility.createXmlStreamWriterNamespaceRepairing(out);
        if (namespaceMap != null && !namespaceMap.isEmpty()) {
            for (String s : namespaceMap.keySet()) {
                String namespace = s;
                String prefix = namespaceMap.get(namespace);
                if (prefix != null) {
                    writer.setPrefix(prefix, namespace);
                } else {
                    writer.setDefaultNamespace(namespace);
                }
            }
        }
        return writer;
    }

}
