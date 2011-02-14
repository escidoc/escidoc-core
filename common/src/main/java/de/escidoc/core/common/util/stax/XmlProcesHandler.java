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
public class XmlProcesHandler {

    public static final String XLINK_PREFIX = "xlink";

    public static final String XLINK_URI = "http://www.w3.org/1999/xlink";

    private String extractPath = null;

    private String[] pathElements = null;

    private int curElement = 0;

    private boolean inside = false;

    private int insideLevel = 0;

    private Map nsuris = null;

    private List writers = new Vector();

    private Map outputStreams = new HashMap();

    private int deepLevel = 0;

    private int counter = 0;

    private int number = 0;

    public XmlProcesHandler(final String extractPath) {
        setExtractPath(extractPath);
    }

    public String getExtractPath() {
        return extractPath;
    }

    public Map getOutputStreams() {
        return this.outputStreams;
    }

    public void setExtractPath(String extractPath) {
        this.extractPath = extractPath;
        this.pathElements = extractPath.split("/");
        this.insideLevel = 0;
        this.inside = false;

    }

    public void startElement(
        XMLStreamReader xmlr, String attributeName, String resourceName,
        String pid, List pids) throws XMLStreamException {
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
            XMLStreamWriter writer = (XMLStreamWriter) writers.get(counter);
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

                        nsuris = new HashMap();
                        int count = xmlr.getNamespaceCount();
                        String nsuri = xmlr.getNamespaceURI();
                        String prefix = xmlr.getPrefix();
                        nsuris.put(nsuri, prefix);

                        ByteArrayOutputStream out = new ByteArrayOutputStream();

                        XMLStreamWriter writer =
                            XmlUtility.createXmlStreamWriter(out);

                        writer.writeStartElement(prefix, xmlr.getLocalName(),
                            nsuri);

                        writer.writeNamespace(prefix, nsuri);

                        String attributeValue = null;

                        for (int i = 0; i < xmlr.getAttributeCount(); i++) {
                            String attLocalName = xmlr.getAttributeLocalName(i);
                            String attPrefix = xmlr.getAttributePrefix(i);
                            String attrNameSpace =
                                xmlr.getAttributeNamespace(i);
                            String attValue = xmlr.getAttributeValue(i);
                            if (attributeName != null
                                && attLocalName.equals(attributeName)) {
                                attributeValue = attValue;

                            }

                            if ((attrNameSpace != null)
                                && (!nsuris.containsKey(attrNameSpace))) {
                                nsuris.put(attrNameSpace, attPrefix);

                                writer.writeNamespace(attPrefix, attrNameSpace);
                            }
                            if (!((attLocalName.equals("href")) && (attrNameSpace
                                .equals(XLINK_URI)))) {

                                writer.writeAttribute(attPrefix, attrNameSpace,
                                    attLocalName, attValue);

                            }
                        }

                        String hRefValue = null;
                        String subId = null;
                        if (resourceName.equals("item")) {
                            hRefValue = "/ir/item/" + pid + "/";
                        }
                        else if (resourceName.equals("component")) {
                            subId = (String) pids.get(number);
                            number++;

                            hRefValue =
                                "/ir/item/" + pid + "/components/" + subId
                                    + "/";

                        }

                        writers.add(writer);
                        if (attributeName == null) {
                            writer.writeAttribute(XLINK_PREFIX, XLINK_URI,
                                "href", hRefValue + theName);
                            outputStreams.put(theName + "*" + subId, out);

                        }
                        else {
                            writer.writeAttribute(XLINK_PREFIX, XLINK_URI,
                                "href", hRefValue
                                    + pathElements[pathElements.length - 2]
                                    + "/" + attributeValue);

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

    public void startElement(
        XMLStreamReader xmlr, String attributeName, String resourceName,
        String pid) throws XMLStreamException {
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
            XMLStreamWriter writer = (XMLStreamWriter) writers.get(counter);
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
                    && !(nsuris.containsKey(attrNameSpace))) {
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

                        nsuris = new HashMap();
                        int count = xmlr.getNamespaceCount();
                        String nsuri = xmlr.getNamespaceURI();
                        String prefix = xmlr.getPrefix();
                        nsuris.put(nsuri, prefix);

                        ByteArrayOutputStream out = new ByteArrayOutputStream();

                        XMLStreamWriter writer =
                            XmlUtility.createXmlStreamWriter(out);

                        writer.writeStartElement(prefix, xmlr.getLocalName(),
                            nsuri);

                        writer.writeNamespace(prefix, nsuri);

                        String attributeValue = null;

                        for (int i = 0; i < xmlr.getAttributeCount(); i++) {
                            String attLocalName = xmlr.getAttributeLocalName(i);
                            String attPrefix = xmlr.getAttributePrefix(i);
                            String attrNameSpace =
                                xmlr.getAttributeNamespace(i);
                            String attValue = xmlr.getAttributeValue(i);
                            if (attributeName != null
                                && attLocalName.equals(attributeName)) {
                                attributeValue = attValue;

                            }
                            if ((attrNameSpace != null)
                                && (!nsuris.containsKey(attrNameSpace))) {
                                nsuris.put(attrNameSpace, attPrefix);
                                writer.writeNamespace(attPrefix, attrNameSpace);
                            }
                            if (!((attLocalName.equals("href")) && (attrNameSpace
                                .equals(XLINK_URI)))) {

                                writer.writeAttribute(attPrefix, attrNameSpace,
                                    attLocalName, attValue);

                            }

                        }

                        String hRefValue = null;
                        if (resourceName.equals("item")) {
                            hRefValue = "/ir/item/" + pid + "/";
                        }
                        // TODO else ...

                        writers.add(writer);
                        if (attributeName == null) {
                            writer.writeAttribute(XLINK_PREFIX, XLINK_URI,
                                "href", hRefValue + theName);
                            outputStreams.put(theName, out);
                        }
                        else {
                            writer.writeAttribute(XLINK_PREFIX, XLINK_URI,
                                "href", hRefValue
                                    + pathElements[pathElements.length - 2]
                                    + "/" + attributeValue);

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

    /**
     * 
     * @param xmlr
     * @throws XMLStreamException
     */
    public void endElement(final XMLStreamReader xmlr)
        throws XMLStreamException {

        deepLevel--;
        if (inside) {
            insideLevel--;
            XMLStreamWriter writer = (XMLStreamWriter) writers.get(counter);
            writer.writeEndElement();
            if (insideLevel == 0) {
                inside = false;
                counter++;
                writer.flush();
                writer.close();

            }
        }
    }

    public void characters(final XMLStreamReader xmlr)
        throws XMLStreamException {

        if (inside) {
            XMLStreamWriter writer = (XMLStreamWriter) writers.get(counter);

            writer.writeCharacters(xmlr.getText());
        }
    }

}
