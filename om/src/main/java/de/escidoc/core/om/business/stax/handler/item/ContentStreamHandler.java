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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.om.business.stax.handler.item;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.resources.Item;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.util.stax.handler.WriteHandler;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;

import javax.naming.directory.NoSuchAttributeException;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @author FRS
 * 
 */
public class ContentStreamHandler extends WriteHandler {

    private boolean inContentStreams = false;

    private final Map<String, Map<String, Object>> contentStreams =
        new HashMap<String, Map<String, Object>>();

    private String contentStreamName = null;

    private Item item = null;

    private boolean wrote;

    public ContentStreamHandler() {
        super();
    }

    public ContentStreamHandler(Item item) {
        super();
        this.item = item;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#characters
     * (java.lang.String,
     * de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public String characters(String data, StartElement element)
        throws XMLStreamException {
        if ((inContentStreams)
            && (contentStreamName != null
                && getWriter() != null
                && contentStreams.get(contentStreamName).get(
                    Elements.ATTRIBUTE_CONTENT_STREAM_MIME_TYPE).equals(
                    "text/xml"))) {
            getWriter().writeCharacters(data);
        }
        return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#endElement
     * (de.escidoc.core.common.util.xml.stax.events.EndElement)
     */
    @Override
    public EndElement endElement(EndElement element)
        throws InvalidXmlException, XMLStreamException {

        // in respect to WriteHandler
        this.decreaseDeepLevel();
        // but WriteHandler does not handle namespaces correctly
        if (this.getNsuris() != null) {
            Iterator<String> it = this.getNsuris().keySet().iterator();
            while (it.hasNext()) {
                String uri = it.next();
                List v = this.getNsuris().get(uri);
                Integer i = (Integer) v.get(0);
                if (i > this.getDeepLevel()) {
                    it.remove();
                }
            }
        }

        if (element.getLocalName().equals(Elements.ELEMENT_CONTENT_STREAMS)) {
            inContentStreams = false;
        }
        else if (inContentStreams) {
            if (Elements.ELEMENT_CONTENT_STREAM.equals(element.getLocalName())) {
                // end of content-stream
                if (getWriter() != null) {
                    if (wrote) {
                        getWriter().flush();
                        getWriter().close();
                        wrote = false;
                    }
                    else {
                        contentStreams.get(contentStreamName).remove(
                            Elements.ELEMENT_CONTENT);
                    }
                }
                // check if we got an href or content
                if ((!contentStreams.get(contentStreamName).containsKey(
                    Elements.ATTRIBUTE_XLINK_HREF))
                    && (contentStreams
                        .get(contentStreamName)
                        .get(Elements.ATTRIBUTE_STORAGE)
                        .equals(
                            de.escidoc.core.common.business.fedora.Constants.STORAGE_INTERNAL_MANAGED))) {
                    // internal-managed content-stream must have either href
                    // or content
                    ByteArrayOutputStream baos =
                        (ByteArrayOutputStream) contentStreams
                            .get(contentStreamName).get(
                                Elements.ELEMENT_CONTENT);
                    if (baos.size() < 1) {
                        throw new XmlCorruptedException(
                            "Internal managed content stream has "
                                + "neither href nor XML content.");
                    }
                }
                contentStreamName = null;
            }
            else {
                // in a particular stream
                getWriter().writeEndElement();
            }
        }
        return element;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#startElement
     * (de.escidoc.core.common.util.xml.stax.events.StartElement)
     * 
     * @throws FedoraSystemException If FedoraUtility can not connect to Fedora.
     */
    @Override
    public StartElement startElement(StartElement element)
        throws InvalidXmlException, InvalidContentException,
        XMLStreamException, FedoraSystemException {

        // in respect to WriteHandler
        this.increaseDeepLevel();

        if (Elements.ELEMENT_CONTENT_STREAMS.equals(element.getLocalName())) {
            inContentStreams = true;
        }
        else if (inContentStreams) {
            if (Elements.ELEMENT_CONTENT_STREAM.equals(element.getLocalName())) {
                // begin of content-stream, create a new map for its values
                try {
                    // name
                    contentStreamName =
                        element
                            .getAttributeValue(null, Elements.ATTRIBUTE_NAME);

                    // content stream name must be unique within one XML
                    // representation
                    if (contentStreams.containsKey(contentStreamName)) {
                        throw new XmlCorruptedException(
                            "Found more than one content stream with name '"
                                + contentStreamName + "'.");
                    }
                    // create map for this content stream
                    contentStreams.put(contentStreamName,
                        new HashMap<String, Object>());

                    // mime-type
                    if (element.hasAttribute(null,
                        Elements.ATTRIBUTE_CONTENT_STREAM_MIME_TYPE)) {
                        contentStreams.get(contentStreamName).put(
                            Elements.ATTRIBUTE_CONTENT_STREAM_MIME_TYPE,
                            element.getAttributeValue(null,
                                Elements.ATTRIBUTE_CONTENT_STREAM_MIME_TYPE));
                    }

                    // xlink:title
                    if (element.hasAttribute(Constants.XLINK_NS_URI,
                        Elements.ATTRIBUTE_XLINK_TITLE)) {
                        contentStreams.get(contentStreamName).put(
                            Elements.ATTRIBUTE_XLINK_TITLE,
                            element.getAttributeValue(Constants.XLINK_NS_URI,
                                Elements.ATTRIBUTE_XLINK_TITLE));
                    }

                    // xlink:href
                    if (element.hasAttribute(Constants.XLINK_NS_URI,
                        Elements.ATTRIBUTE_XLINK_HREF)) {
                        contentStreams.get(contentStreamName).put(
                            Elements.ATTRIBUTE_XLINK_HREF,
                            element.getAttributeValue(Constants.XLINK_NS_URI,
                                Elements.ATTRIBUTE_XLINK_HREF));
                    }

                    // storage
                    String storage;
                    if (element.hasAttribute(null, Elements.ATTRIBUTE_STORAGE)) {
                        storage =
                            element.getAttributeValue(null,
                                Elements.ATTRIBUTE_STORAGE);
                    }
                    else {
                        storage =
                            de.escidoc.core.common.business.fedora.Constants.STORAGE_INTERNAL_MANAGED;
                    }
                    contentStreams.get(contentStreamName).put(
                        Elements.ATTRIBUTE_STORAGE, storage);

                    String curControlGroup = null;
                    if (item != null) {
                        curControlGroup =
                            item
                                .getContentStream(contentStreamName)
                                .getControlGroup();
                    }

                    if (storage
                        .equals(de.escidoc.core.common.business.fedora.Constants.STORAGE_INTERNAL_MANAGED)) {
                        if (contentStreams
                            .get(contentStreamName).get(
                                Elements.ATTRIBUTE_CONTENT_STREAM_MIME_TYPE)
                            .equals("text/xml")) {

                            // check if control group is changed
                            if (curControlGroup != null
                                && !(curControlGroup.equals("M") || curControlGroup
                                    .equals("X"))) {
                                throw new InvalidContentException(
                                    "The value of storage can not be changed in existing content stream '"
                                        + contentStreamName + "'.");
                            }

                            // prepare XML writing
                            ByteArrayOutputStream out =
                                new ByteArrayOutputStream();
                            contentStreams.get(contentStreamName).put(
                                Elements.ELEMENT_CONTENT, out);
                            this.setWriter(XmlUtility.createXmlStreamWriter(out));
                            this.setNsuris(new HashMap());
                        }
                        else if (!element.hasChild() && element.hasCharacters()) {
                            // FIXME support in-line base64 content
                            throw new UnsupportedOperationException(
                                "Inline base64 content in ContentStreams needs to be implemented.");
                        }
                        else if (!contentStreams
                            .get(contentStreamName).containsKey(
                                Elements.ATTRIBUTE_XLINK_HREF)) {
                            throw new XmlCorruptedException(
                                "Internal managed content stream has "
                                    + "neither href nor XML content.");
                        }
                    }
                    else if (storage
                        .equals(de.escidoc.core.common.business.fedora.Constants.STORAGE_EXTERNAL_MANAGED)) {
                        if (!contentStreams.get(contentStreamName).containsKey(
                            Elements.ATTRIBUTE_XLINK_HREF)) {
                            throw new XmlCorruptedException(
                                "Content stream with storage set to '"
                                    + de.escidoc.core.common.business.fedora.Constants.STORAGE_EXTERNAL_MANAGED
                                    + "' " + "needs href.");
                        }
                        // check if control group is changed
                        if (curControlGroup != null
                            && !curControlGroup.equals("E")) {
                            throw new InvalidContentException(
                                "The value of storage can not be changed in existing content stream '"
                                    + contentStreamName + "'.");
                        }

                    }
                    else if (storage
                        .equals(de.escidoc.core.common.business.fedora.Constants.STORAGE_EXTERNAL_URL)) {
                        if (!contentStreams.get(contentStreamName).containsKey(
                            Elements.ATTRIBUTE_XLINK_HREF)) {
                            throw new XmlCorruptedException(
                                "Content stream with storage set to '"
                                    + de.escidoc.core.common.business.fedora.Constants.STORAGE_EXTERNAL_URL
                                    + "' " + "needs href.");
                        }
                        // check if control group is changed
                        if (curControlGroup != null
                            && !curControlGroup.equals("R")) {
                            throw new InvalidContentException(
                                "The value of storage can not be changed in existing content stream '"
                                    + contentStreamName + "'.");
                        }

                    }
                }
                catch (NoSuchAttributeException e) {
                    throw new XmlCorruptedException(e);
                }
            }
            else {
                // in a particular stream
                wrote = true;
                writeElement(element);
                int c = element.getAttributeCount();
                for (int i = 0; i < c; i++) {
                    Attribute att = element.getAttribute(i);
                    writeAttribute(att.getNamespace(), element.getLocalName(),
                        att.getLocalName(), att.getValue(), att.getPrefix(),
                        element.getNamespaceContext());
                }
            }
        }
        return element;
    }

    /**
     * @return the contentStreams
     */
    public Map<String, Map<String, Object>> getContentStreams() {
        return contentStreams;
    }

}
