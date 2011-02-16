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

import de.escidoc.core.common.business.Constants;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * This handler must be the last in the chain! Adds a new sub-tree to the data
 * stream.
 * 
 */
public class AddNewSubTreesToDatastream extends DefaultHandler {

    private boolean inside = false;

    private String path = null;

    private int insideLevel = 0;

    private Map nsuris = null;

    private XMLStreamWriter writer = null;

    private ByteArrayOutputStream out = null;

    private boolean isParsed = false;

    private boolean isRelsExt = false;

    private boolean isContentRelation = false;

    private boolean isNew = false;

    private final StaxParser parser;

    private int deepLevel = 0;

    private List<StartElementWithChildElements> subtreesToInsert = null;

    private StartElement pointerElement = null;

    /**
     * Must be the <b>last</b> handler in the handler chain!
     * 
     * @param path
     * @param parser
     */
    public AddNewSubTreesToDatastream(final String path, final StaxParser parser) {
        this.parser = parser;
        this.path = path;

    }

    /**
     * Must be the last handler in the handler chain.
     * 
     * @param path
     * @param parser
     * @param subtreesToInsert
     * @param pointerElement
     */
    public AddNewSubTreesToDatastream(final String path,
        final StaxParser parser,
        final List<StartElementWithChildElements> subtreesToInsert,
        final StartElement pointerElement) {

        this.parser = parser;
        this.path = path;
        this.subtreesToInsert = subtreesToInsert;
        this.pointerElement = pointerElement;

    }

    public void addSubteeToSubtreesVector(
        StartElementWithChildElements subtreeToInsert) {
        if (this.subtreesToInsert == null) {
            this.subtreesToInsert = new ArrayList<StartElementWithChildElements>();
        }
        this.subtreesToInsert.add(subtreeToInsert);

    }

    public void setSubtreeToInsert(
        List<StartElementWithChildElements> subtreesToInsert) {
        this.subtreesToInsert = subtreesToInsert;
    }

    public ByteArrayOutputStream getOutputStreams() {
        return this.out;
    }

    public void setPointerElement(StartElement pointerElement) {
        this.pointerElement = pointerElement;
    }

    /**
     * 
     * @param element -
     *            StartElement
     * @throws XMLStreamException
     *             In case of parse error.
     * @return StarteElement
     * 
     * @overwrite
     */

    // TODO: Now there is no need to differentiate between RELS-EXT and other
    // data streams while writing namespace uri (with "/" on the end, or without
    // "/"
    // on the end, because all namespaces in RELS-EXT have now "/" on the end.
    // Therefore delete variables isRelsExt, isNew, isContentRelation
    @Override
    public StartElement startElement(final StartElement element)
        throws XMLStreamException {
        if (path.startsWith("/RDF")) {
            isRelsExt = true;
        }

        deepLevel++;
        String theName = element.getLocalName();
        String nsuri = element.getNamespace();
        String prefix = element.getPrefix();

        if (inside) {
            writeElement(nsuri, theName, prefix, deepLevel, isRelsExt, isNew,
                false);
            int attCount = element.getAttributeCount();
            for (int i = 0; i < attCount; i++) {
                Attribute curAtt = element.getAttribute(i);
                handleAttribute(curAtt, theName, deepLevel, isRelsExt, isNew,
                    false);

            }

            insideLevel++;
        }
        else {
            String currentPath = parser.getCurPath();

            if (path.equals(currentPath)) {
                this.out = new ByteArrayOutputStream();
                this.writer = XmlUtility.createXmlStreamWriter(out);
                inside = true;
                nsuris = new HashMap();
                writeElement(nsuri, theName, prefix, deepLevel, isRelsExt,
                    isNew, false);

                int attCount = element.getAttributeCount();
                for (int i = 0; i < attCount; i++) {
                    Attribute curAtt = element.getAttribute(i);
                    handleAttribute(curAtt, theName, deepLevel, isRelsExt,
                        isNew, false);
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
            && prefix.equals(pointerElement.getPrefix()) && !isParsed
            && equalAttr(pointerElement, element)) {

            isNew = true;

            Iterator<StartElementWithChildElements> subteeIterator =
                subtreesToInsert.iterator();
            while (subteeIterator.hasNext()) {
                StartElementWithChildElements subtreeToInsert =
                    (StartElementWithChildElements) subteeIterator.next();

                String subtreeName = subtreeToInsert.getLocalName();
                String subtreeNsUri = subtreeToInsert.getNamespace();
                String subtreePrefix = subtreeToInsert.getPrefix();
                if (subtreePrefix
                    .equals(Constants.CONTENT_RELATIONS_NS_PREFIX_IN_RELSEXT)) {
                    isContentRelation = true;
                }

                writeElement(subtreeNsUri, subtreeName, subtreePrefix,
                    deepLevel + 1, isRelsExt, isNew, isContentRelation);
                int attCount = subtreeToInsert.getAttributeCount();
                for (int j = 0; j < attCount; j++) {
                    Attribute curAtt = subtreeToInsert.getAttribute(j);
                    handleAttribute(curAtt, theName, deepLevel + 1, isRelsExt,
                        isNew, isContentRelation);
                }
                String subtreeText = subtreeToInsert.getElementText();
                if ((subtreeText != null) && subtreeText.length() > 0) {
                    writer.writeCharacters(subtreeText);
                    writer.flush();
                }
                isContentRelation = false;
                List<StartElementWithText> children =
                    subtreeToInsert.getChildrenElements();
                if ((children != null) && (children.size() > 0)) {
                    Iterator<StartElementWithText> it = children.iterator();
                    while (it.hasNext()) {
                        StartElementWithText inserted =
                            (StartElementWithText) it.next();
                        String insertedName = inserted.getLocalName();
                        String insertedNsUri = inserted.getNamespace();
                        String insertedPrefix = inserted.getPrefix();
                        if (insertedPrefix
                            .equals(Constants.CONTENT_RELATIONS_NS_PREFIX_IN_RELSEXT)) {
                            isContentRelation = true;
                        }
                        writeElement(insertedNsUri, insertedName,
                            insertedPrefix, deepLevel + 2, isRelsExt, isNew,
                            isContentRelation);

                        int attCount2 = inserted.getAttributeCount();
                        for (int j = 0; j < attCount2; j++) {
                            Attribute curAtt = inserted.getAttribute(j);
                            handleAttribute(curAtt, theName, deepLevel + 2,
                                isRelsExt, isNew, isContentRelation);
                        }
                        String insertedText = inserted.getElementText();
                        if ((insertedText != null) && insertedText.length() > 0) {
                            writer.writeCharacters(insertedText);
                            writer.flush();
                        }
                        writer.writeEndElement();
                        isContentRelation = false;
                    }
                }
                writer.writeEndElement();
                writer.flush();
            }
            isNew = false;
            isParsed = true;
        }
        // this have to be the last handler
        return null;
    }

    /*
     * public void startElement(XMLStreamReader xmlr, Vector pids) throws
     * XMLStreamException { }
     */

    @Override
    public EndElement endElement(EndElement element) throws XMLStreamException {

        deepLevel--;
        String theName = element.getLocalName();

        if (inside) {
            insideLevel--;

            writer.writeEndElement();
            // remove namespace if is defined in this element
            String ns = element.getNamespace();
            List nsTrace = (List) nsuris.get(ns);

            if (nsTrace != null
                && (nsTrace.get(2) == null || nsTrace.get(2).equals(
                    element.getPrefix()))
                && nsTrace.get(1).equals(element.getLocalName())
                && (Integer) nsTrace.get(0) == (deepLevel + 1)) {

                nsuris.remove(ns);

            }

            // attribute namespaces
            // TODO iteration is a hack, use
            // javax.xml.namespace.NamespaceContext
            Iterator it = nsuris.keySet().iterator();
            List toRemove = new ArrayList();
            while (it.hasNext()) {
                try {
                    String key = (String) it.next();
                    nsTrace = (List) nsuris.get(key);
                    if ((Integer) nsTrace.get(0) == (deepLevel + 1)) {
                        toRemove.add(key);
                    }
                }
                catch (Exception e) {
                    throw new XMLStreamException(e.getMessage(), e);
                }
            }
            it = toRemove.iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                nsuris.remove(key);
            }

            if (insideLevel == 0) {
                inside = false;

                writer.flush();
                writer.close();
            }
        }

        return null;
    }

    @Override
    public String characters(final String data, final StartElement element)
        throws XMLStreamException {
        if (inside) {
            writer.writeCharacters(data);
        }
        return null;
    }

    private void writeElement(
        String uri, String name, String prefix, int deep, boolean isRelsExt,
        boolean isNew, boolean isContentRelation) throws XMLStreamException {
        if ((uri) != null) {
            if (!nsuris.containsKey(uri)) {
                List namespaceTrace = new ArrayList();
                namespaceTrace.add(deep);
                namespaceTrace.add(name);
                namespaceTrace.add(prefix);
                nsuris.put(uri, namespaceTrace);
                writer.writeStartElement(prefix, name, uri);
                // if (isRelsExt && isNew && !isContentRelation) {
                // writer.writeNamespace(prefix, uri + "/");
                // }
                // else {
                writer.writeNamespace(prefix, uri);
                // }
            }
            else {
                List namespaceTrace = (List) nsuris.get(uri);
                Integer deepLevelInMAp = (Integer) namespaceTrace.get(0);
                String prefixTrace = (String) namespaceTrace.get(2);
                if (!prefixTrace.equals(prefix)) {
                    prefix = prefixTrace;
                }
                if (deepLevelInMAp >= deep) {
                    writer.writeStartElement(prefix, name, uri);
                    // if (isRelsExt && isNew && !isContentRelation) {
                    // writer.writeNamespace(prefix, uri + "/");
                    // }
                    // else {
                    writer.writeNamespace(prefix, uri);
                    // }
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
        final String uri, final String elementName, final String attributeName,
        final String attributeValue, String prefix, final int deep,
        final boolean isRelsExt, final boolean isNew,
        final boolean isContentRelation) throws XMLStreamException {
        if (uri != null) {
            if (!nsuris.containsKey(uri)) {
                List namespaceTrace = new ArrayList();
                namespaceTrace.add(deep);
                namespaceTrace.add(elementName);
                namespaceTrace.add(prefix);
                nsuris.put(uri, namespaceTrace);
                // if (isRelsExt && isNew && !isContentRelation) {
                // writer.writeNamespace(prefix, uri + "/");
                // }
                // else {
                writer.writeNamespace(prefix, uri);
                // }

            }
            else {
                List namespaceTrace = (List) nsuris.get(uri);
                String prefixTrace = (String) namespaceTrace.get(2);
                if (!prefixTrace.equals(prefix)) {
                    prefix = prefixTrace;
                }
                Integer deepLevelInMAp = (Integer) namespaceTrace.get(0);
                String nameTrace = (String) namespaceTrace.get(1);
                if (((deepLevelInMAp == deep) && (!elementName
                    .equals(nameTrace)))
                    || (deepLevelInMAp > deep)) {
                    // if (isRelsExt && isNew) {
                    //
                    // writer.writeNamespace(prefix, uri + "/");
                    // }
                    // else {
                    writer.writeNamespace(prefix, uri);
                    // }

                }
            }
        }
        writer.writeAttribute(prefix, uri, attributeName, attributeValue);
    }

    private void handleAttribute(
        Attribute attribute, String theName, int deep, boolean isRelsExt,
        boolean isNew, boolean isContentRelation) throws XMLStreamException {
        String attLocalName = attribute.getLocalName();
        String attrNameSpace = attribute.getNamespace();
        String attrPrefix = attribute.getPrefix();
        String attValue = attribute.getValue();
        writeAttribute(attrNameSpace, theName, attLocalName, attValue,
            attrPrefix, deep, isRelsExt, isNew, isContentRelation);

    }

    /**
     * Compares all attributes of pointerElement with attributes of element.
     * StartElement element must have all attributes of pointerElement with
     * equal value.
     * 
     * @param pointerElement
     * @param element
     * @return true - If all attributes of pointerElement exist in element and
     *         there values are equal. Other attibutes (of element) are not
     *         compared. false - otherwise
     */
    private boolean equalAttr(
        final StartElement pointerElement, final StartElement element) {
        try {
            int pointerAttsNo = pointerElement.getAttributeCount();
            for (int i = 0; i < pointerAttsNo; i++) {
                String attName = pointerElement.getAttribute(i).getLocalName();
                String attNS = pointerElement.getAttribute(i).getNamespace();

                if (!pointerElement.getAttribute(i).getValue().equals(
                    element.getAttribute(attNS, attName).getValue())) {
                    return (false);
                }
            }
        }
        catch (IndexOutOfBoundsException e) {
            return (false);
        }
        catch (NoSuchAttributeException e) {
            return (false);
        }

        return (true);
    }
}
