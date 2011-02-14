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
package de.escidoc.core.common.util.stax;

import de.escidoc.core.common.util.xml.XmlUtility;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * 
 * @author ROF
 * 
 */

public class ExtractHandler {

    private String extractPath = null;

    private String[] pathElements = null;

    private int curElement = 0;

    private boolean inside = false;

    private int insideLevel = 0;

    private Map<String, String> nsuris = null;

    XMLStreamWriter writer = null;


    private Map<String, ByteArrayOutputStream> outputStreams =
        new HashMap<String, ByteArrayOutputStream>();

    private int deepLevel = 0;

    private int counter = 0;

    private int number = 0;

    public ExtractHandler(final String extractPath) {
        setExtractPath(extractPath);
    }

    public String getExtractPath() {
        return extractPath;
    }

    public Map<String, ByteArrayOutputStream> getOutputStreams() {
        return this.outputStreams;
    }

    public void setExtractPath(final String extractPath) {
        this.extractPath = extractPath;
        this.pathElements = extractPath.split("/");
        this.insideLevel = 0;
        this.inside = false;

    }

    public void startElement(XMLStreamReader xmlr, String attributeName)
        throws XMLStreamException {
        deepLevel++;
        // eat empty pathElements
        while (pathElements[curElement] == null
            || pathElements[curElement].length() == 0) {
            curElement++;
            if (curElement >= pathElements.length) {
                throw new XMLStreamException("Invalid extract path!");
            }
        }

        String theName = xmlr.getLocalName();

        if (inside) {

            String nsuri = xmlr.getNamespaceURI();
            String prefix = xmlr.getPrefix();
            this.writer.writeStartElement(prefix, xmlr.getLocalName(), nsuri);
            if ((nsuri != null) && (!nsuris.containsKey(nsuri))) {
                nsuris.put(nsuri, prefix);
                writer.writeNamespace(prefix, nsuri);
            }

            for (int i = 0; i < xmlr.getAttributeCount(); i++) {

                String attLocalName = xmlr.getAttributeLocalName(i);
                String attrNameSpace = xmlr.getAttributeNamespace(i);
                String attrPrefix = xmlr.getAttributePrefix(i);
                String attValue = xmlr.getAttributeValue(i);
                writer.writeAttribute(attrPrefix, attrNameSpace, attLocalName,
                    attValue);
                if ((attrNameSpace != null)
                    && (!nsuris.containsKey(attrNameSpace))) {
                    nsuris.put(attrNameSpace, attrPrefix);
                    writer.writeNamespace(attrPrefix, attrNameSpace);

                }

            }
            insideLevel++;
        }
        else {

            if (theName.equals(pathElements[curElement])) {
                if ((curElement == (pathElements.length - 1))) {
                    if ((deepLevel == (pathElements.length - 1))) {
                        inside = true;
                        nsuris = new HashMap<String, String>();
                        String nsuri = xmlr.getNamespaceURI();
                        String prefix = xmlr.getPrefix();
                        nsuris.put(nsuri, prefix);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();

                        this.writer = XmlUtility.createXmlStreamWriter(out);

                        this.writer.writeStartElement(prefix, xmlr
                            .getLocalName(), nsuri);

                        this.writer.writeNamespace(prefix, nsuri);

                        String attributeValue = null;

                        for (int i = 0; i < xmlr.getAttributeCount(); i++) {

                            String attLocalName = xmlr.getAttributeLocalName(i);
                            String attrNameSpace =
                                xmlr.getAttributeNamespace(i);
                            String attrPrefix = xmlr.getAttributePrefix(i);
                            String attValue = xmlr.getAttributeValue(i);

                            if (attributeName != null
                                && attLocalName.equals(attributeName)) {
                                attributeValue = attValue;

                            }
                            writer.writeAttribute(attrPrefix, attrNameSpace,
                                attLocalName, attValue);
                            if ((attrNameSpace != null)
                                && (!nsuris.containsKey(attrNameSpace))) {
                                nsuris.put(attrNameSpace, attrPrefix);
                                writer
                                    .writeNamespace(attrPrefix, attrNameSpace);

                            }

                        }

                        if (attributeName == null) {
                            outputStreams.put(theName, out);
                        }
                        else {
                            outputStreams.put(attributeValue, out);
                        }
                        // writeElementStart(theName, xmlr);
                        insideLevel++;
                        if (insideLevel != 1) {
                            throw new XMLStreamException("insideLevel != 1: "
                                + insideLevel);
                        }
                    }
                }
                else {
                    curElement++;
                }
            }
        }
    }

    public void startElement(
        XMLStreamReader xmlr, String attributeName, List<String> pids)
        throws XMLStreamException {
        deepLevel++;
        // eat empty pathElements
        while (pathElements[curElement] == null
            || pathElements[curElement].length() == 0) {
            curElement++;
            if (curElement >= pathElements.length) {
                throw new XMLStreamException("Invalid extract path!");
            }
        }

        String theName = xmlr.getLocalName();

        if (inside) {

            String nsuri = xmlr.getNamespaceURI();
            String prefix = xmlr.getPrefix();
            writer.writeStartElement(prefix, xmlr.getLocalName(), nsuri);
            if ((nsuri != null) && (!nsuris.containsKey(nsuri))) {
                nsuris.put(nsuri, prefix);
                writer.writeNamespace(prefix, nsuri);
            }

            for (int i = 0; i < xmlr.getAttributeCount(); i++) {

                String attLocalName = xmlr.getAttributeLocalName(i);
                String attrNameSpace = xmlr.getAttributeNamespace(i);
                String attrPrefix = xmlr.getAttributePrefix(i);
                String attValue = xmlr.getAttributeValue(i);
                writer.writeAttribute(attrPrefix, attrNameSpace, attLocalName,
                    attValue);
                if ((attrNameSpace != null)
                    && (!nsuris.containsKey(attrNameSpace))) {
                    nsuris.put(attrNameSpace, attrPrefix);
                    writer.writeNamespace(attrPrefix, attrNameSpace);

                }

            }
            insideLevel++;
        }
        else {

            if (theName.equals(pathElements[curElement])) {
                if ((curElement == (pathElements.length - 1))) {
                    if ((deepLevel == (pathElements.length - 1))) {
                        inside = true;
                        nsuris = new HashMap<String, String>();
                        String nsuri = xmlr.getNamespaceURI();
                        String prefix = xmlr.getPrefix();
                        nsuris.put(nsuri, prefix);

                        ByteArrayOutputStream out = new ByteArrayOutputStream();

                        this.writer = XmlUtility.createXmlStreamWriter(out);

                        writer.writeStartElement(prefix, xmlr.getLocalName(),
                            nsuri);

                        writer.writeNamespace(prefix, nsuri);

                        String attributeValue = null;

                        for (int i = 0; i < xmlr.getAttributeCount(); i++) {

                            String attLocalName = xmlr.getAttributeLocalName(i);
                            String attrNameSpace =
                                xmlr.getAttributeNamespace(i);
                            String attrPrefix = xmlr.getAttributePrefix(i);
                            String attValue = xmlr.getAttributeValue(i);
                            if (attributeName != null
                                && attLocalName.equals(attributeName)) {
                                attributeValue = attValue;
                            }
                            writer.writeAttribute(attrPrefix, attrNameSpace,
                                attLocalName, attValue);
                            if ((attrNameSpace != null)
                                && (!nsuris.containsKey(attrNameSpace))) {
                                nsuris.put(attrNameSpace, attrPrefix);
                                writer
                                    .writeNamespace(attrPrefix, attrNameSpace);

                            }

                        }

                        String subId = (String) pids.get(number);
                        number++;
                        if (attributeName == null) {
                            outputStreams.put(theName + "*" + subId, out);
                        }
                        else {
                            outputStreams
                                .put(attributeValue + "*" + subId, out);
                        }
                        // writeElementStart(theName, xmlr);
                        insideLevel++;
                        if (insideLevel != 1) {
                            throw new XMLStreamException("insideLevel != 1: "
                                + insideLevel);
                        }
                    }
                }
                else {
                    curElement++;
                }
            }
        }
    }

    /**
     * 
     * @param xmlr
     * @throws XMLStreamException
     */
    public void endElement(XMLStreamReader xmlr) throws XMLStreamException {
        deepLevel--;
        if (inside) {
            insideLevel--;
            this.writer.writeEndElement();
            if (insideLevel == 0) {
                inside = false;
                counter++;
                writer.flush();
                writer.close();
                writer = null;
            }
        }
    }

    public void characters(XMLStreamReader xmlr) throws XMLStreamException {

        if (inside) {
            this.writer.writeCharacters(xmlr.getText());
        }
    }

}
