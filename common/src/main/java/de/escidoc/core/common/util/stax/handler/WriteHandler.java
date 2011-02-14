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

import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Map;
import java.util.Vector;

/**
 * 
 * @author ??
 * 
 */
public abstract class WriteHandler extends DefaultHandler {

    private XMLStreamWriter writer = null;

    // must be initialized if an instance is created, see creation in
    // MultipleExtractor
    private Map<String, Vector> nsuris;

    private int deepLevel = 0;

    protected XMLStreamWriter getWriter() {
        return writer;
    }

    protected void setWriter(XMLStreamWriter writer) {
        this.writer = writer;
    }

    protected Map<String, Vector> getNsuris() {
        return nsuris;
    }

    protected void setNsuris(Map<String, Vector> nsuris) {
        this.nsuris = nsuris;
    }

    protected int getDeepLevel() {
        return deepLevel;
    }

    protected void increaseDeepLevel() {
        deepLevel++;
    }

    protected void decreaseDeepLevel() {
        deepLevel--;
    }

    protected void writeElement(StartElement element) throws XMLStreamException {

        String name = element.getLocalName();
        String uri = element.getNamespace();
        String prefix = element.getPrefix();

        if ((uri) != null) {
            if (!nsuris.containsKey(uri)) {
                Vector namespaceTrace = new Vector();
                namespaceTrace.add(Integer.valueOf(deepLevel));
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
                if (prefixTrace == null || !prefixTrace.equals(prefix)) {
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
        else {
            writer.writeStartElement(name);
        }

    }

    protected void writeAttribute(
        String uri, String elementName, String attributeName,
        String attributeValue, String prefix, NamespaceContext nscontext)
        throws XMLStreamException {
        if (uri != null) {
            if (!nsuris.containsKey(uri)) {
                Vector namespaceTrace = new Vector();
                namespaceTrace.add(Integer.valueOf(deepLevel));
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
                // Integer deepLevelInMAp = (Integer) namespaceTrace.get(0);
                // String nameTrace = (String) namespaceTrace.get(1);
                // if ( (deepLevelInMAp.intValue() >= deepLevel)) {
                // writer.writeNamespace(prefix, uri);
                // }
            }
        }
        if (prefix != null) {
            String nameUri = nscontext.getNamespaceURI(prefix);
            if (nameUri != null
                && nameUri.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI)) {

                String[] attributeValueArray = attributeValue.split(":");
                if (attributeValueArray.length == 2) {
                    int index = attributeValueArray[1].indexOf("/");
                    if (index == -1) {
                        // int index = attributeValue.indexOf(":");
                        // if(index != -1) {
                        // String prefixValue =
                        // attributeValue.substring(0, index);
                        String prefixValue = attributeValueArray[0];
                        if (prefixValue != null) {
                            String valueUri =
                                nscontext.getNamespaceURI(prefixValue);
                            if (valueUri != null) {
                                if (!nsuris.containsKey(valueUri)) {
                                    Vector namespaceTrace = new Vector();
                                    namespaceTrace.add(Integer.valueOf(deepLevel));
                                    namespaceTrace.add(elementName);
                                    namespaceTrace.add(prefixValue);
                                    nsuris.put(valueUri, namespaceTrace);

                                    writer
                                        .writeNamespace(prefixValue, valueUri);
                                }
                                else {
                                    Vector namespaceTrace =
                                        (Vector) nsuris.get(valueUri);
                                    String prefixTrace =
                                        (String) namespaceTrace.get(2);
                                    if (!prefixTrace.equals(prefixValue)) {
                                        prefixValue = prefixTrace;
                                    }

                                }
                            }
                        }
                    }
                    // writer.writeNamespace(valuePrefix, valueUri);
                }
            }
        }
        writer.writeAttribute(prefix, uri, attributeName, attributeValue);

    }

    protected void handleAttributeInInsideElement(
        Attribute attribute, NamespaceContext nscontext, String theName)
        throws XMLStreamException {
        String attLocalName = attribute.getLocalName();
        String attrNameSpace = attribute.getNamespace();
        String attrPrefix = attribute.getPrefix();
        String attValue = attribute.getValue();
        writeAttribute(attrNameSpace, theName, attLocalName, attValue,
            attrPrefix, nscontext);

    }

    protected String handleAttributeInOutsideElement(
        Attribute attribute, NamespaceContext nscontext, String theName,
        String attributeName) throws XMLStreamException {
        String attributeValue = null;
        String attLocalName = attribute.getLocalName();
        String attrNameSpace = attribute.getNamespace();
        String attrPrefix = attribute.getPrefix();
        String attValue = attribute.getValue();
        if ((attributeName != null) && attLocalName.equals(attributeName)) {
            attributeValue = attValue;
        }
        if (!theName.equals("md-record") && !theName.equals("admin-descriptor")) {
            writeAttribute(attrNameSpace, theName, attLocalName, attValue,
                attrPrefix, nscontext);
        }
        return attributeValue;
    }
}
