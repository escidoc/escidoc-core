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
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.util.stax.handler.WriteHandler;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import org.esidoc.core.utils.io.MimeTypes;

import javax.naming.directory.NoSuchAttributeException;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Frank Schwichtenberg
 */
@Deprecated
public class ContentStreamHandler extends WriteHandler {

    private boolean inContentStreams;

    private final Map<String, Map<String, Object>> contentStreams = new HashMap<String, Map<String, Object>>();

    private String contentStreamName;

    private Item item;

    private boolean wrote;

    public ContentStreamHandler() {
    }

    public ContentStreamHandler(final Item item) {
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
    public String characters(final String data, final StartElement element) throws XMLStreamException {
        if (this.inContentStreams
            && this.contentStreamName != null
            && getWriter() != null
            && MimeTypes.TEXT_XML.equals(contentStreams.get(this.contentStreamName).get(
                Elements.ATTRIBUTE_CONTENT_STREAM_MIME_TYPE))) {
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
    public EndElement endElement(final EndElement element) throws XMLStreamException, XmlCorruptedException {

        // in respect to WriteHandler
        this.decreaseDeepLevel();
        // but WriteHandler does not handle namespaces correctly
        if (this.getNsuris() != null) {
            final Iterator<String> it = this.getNsuris().keySet().iterator();
            while (it.hasNext()) {
                final String uri = it.next();
                final List v = this.getNsuris().get(uri);
                final Integer i = (Integer) v.get(0);
                if (i > this.getDeepLevel()) {
                    it.remove();
                }
            }
        }

        if (element.getLocalName().equals(Elements.ELEMENT_CONTENT_STREAMS)) {
            this.inContentStreams = false;
        }
        else if (this.inContentStreams) {
            if (Elements.ELEMENT_CONTENT_STREAM.equals(element.getLocalName())) {
                // end of content-stream
                if (getWriter() != null) {
                    if (this.wrote) {
                        getWriter().flush();
                        getWriter().close();
                        this.wrote = false;
                    }
                    else {
                        contentStreams.get(this.contentStreamName).remove(Elements.ELEMENT_CONTENT);
                    }
                }
                // check if we got an href or content
                if (!contentStreams.get(this.contentStreamName).containsKey(Elements.ATTRIBUTE_XLINK_HREF)
                    && contentStreams.get(this.contentStreamName).get(Elements.ATTRIBUTE_STORAGE).equals(
                        de.escidoc.core.common.business.fedora.Constants.STORAGE_INTERNAL_MANAGED)) {
                    // internal-managed content-stream must have either href
                    // or content
                    final ByteArrayOutputStream baos =
                        (ByteArrayOutputStream) contentStreams
                            .get(this.contentStreamName).get(Elements.ELEMENT_CONTENT);
                    if (baos.size() < 1) {
                        throw new XmlCorruptedException("Internal managed content stream has "
                            + "neither href nor XML content.");
                    }
                }
                this.contentStreamName = null;
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
     * @throws FedoraSystemException if can not connect to Fedora.
     */
    @Override
    public StartElement startElement(final StartElement element) throws InvalidContentException, XMLStreamException,
        FedoraSystemException, XmlCorruptedException {

        // in respect to WriteHandler
        this.increaseDeepLevel();

        if (Elements.ELEMENT_CONTENT_STREAMS.equals(element.getLocalName())) {
            this.inContentStreams = true;
        }
        else if (this.inContentStreams) {
            if (Elements.ELEMENT_CONTENT_STREAM.equals(element.getLocalName())) {
                // begin of content-stream, create a new map for its values
                try {
                    // name
                    this.contentStreamName = element.getAttributeValue(null, Elements.ATTRIBUTE_NAME);

                    // content stream name must be unique within one XML
                    // representation
                    if (contentStreams.containsKey(this.contentStreamName)) {
                        throw new XmlCorruptedException("Found more than one content stream with name '"
                            + this.contentStreamName + "'.");
                    }
                    // create map for this content stream
                    contentStreams.put(this.contentStreamName, new HashMap<String, Object>());

                    // mime-type
                    if (element.hasAttribute(null, Elements.ATTRIBUTE_CONTENT_STREAM_MIME_TYPE)) {
                        contentStreams.get(this.contentStreamName).put(Elements.ATTRIBUTE_CONTENT_STREAM_MIME_TYPE,
                            element.getAttributeValue(null, Elements.ATTRIBUTE_CONTENT_STREAM_MIME_TYPE));
                    }

                    // xlink:title
                    if (element.hasAttribute(Constants.XLINK_NS_URI, Elements.ATTRIBUTE_XLINK_TITLE)) {
                        contentStreams.get(this.contentStreamName).put(Elements.ATTRIBUTE_XLINK_TITLE,
                            element.getAttributeValue(Constants.XLINK_NS_URI, Elements.ATTRIBUTE_XLINK_TITLE));
                    }

                    // xlink:href
                    if (element.hasAttribute(Constants.XLINK_NS_URI, Elements.ATTRIBUTE_XLINK_HREF)) {
                        contentStreams.get(this.contentStreamName).put(Elements.ATTRIBUTE_XLINK_HREF,
                            element.getAttributeValue(Constants.XLINK_NS_URI, Elements.ATTRIBUTE_XLINK_HREF));
                    }

                    // storage
                    final String storage =
                        element.hasAttribute(null, Elements.ATTRIBUTE_STORAGE) ? element.getAttributeValue(null,
                            Elements.ATTRIBUTE_STORAGE) : de.escidoc.core.common.business.fedora.Constants.STORAGE_INTERNAL_MANAGED;
                    contentStreams.get(this.contentStreamName).put(Elements.ATTRIBUTE_STORAGE, storage);

                    String curControlGroup = null;
                    if (this.item != null) {
                        curControlGroup = item.getContentStream(this.contentStreamName).getControlGroup();
                    }

                    if (storage.equals(de.escidoc.core.common.business.fedora.Constants.STORAGE_INTERNAL_MANAGED)) {
                        if (MimeTypes.TEXT_XML.equals(contentStreams.get(this.contentStreamName).get(
                            Elements.ATTRIBUTE_CONTENT_STREAM_MIME_TYPE))) {

                            // check if control group is changed
                            if (curControlGroup != null
                                && !("M".equals(curControlGroup) || "X".equals(curControlGroup))) {
                                throw new InvalidContentException(
                                    "The value of storage can not be changed in existing content stream '"
                                        + this.contentStreamName + "'.");
                            }

                            // prepare XML writing
                            final ByteArrayOutputStream out = new ByteArrayOutputStream();
                            contentStreams.get(this.contentStreamName).put(Elements.ELEMENT_CONTENT, out);
                            this.setWriter(XmlUtility.createXmlStreamWriter(out));
                            this.setNsuris(new HashMap());
                        }
                        else if (!element.hasChild() && element.hasCharacters()) {
                            // FIXME support in-line base64 content
                            throw new UnsupportedOperationException(
                                "Inline base64 content in ContentStreams needs to be implemented.");
                        }
                        else if (!contentStreams.get(this.contentStreamName).containsKey(Elements.ATTRIBUTE_XLINK_HREF)) {
                            throw new XmlCorruptedException("Internal managed content stream has "
                                + "neither href nor XML content.");
                        }
                    }
                    else if (storage.equals(de.escidoc.core.common.business.fedora.Constants.STORAGE_EXTERNAL_MANAGED)) {
                        if (!contentStreams.get(this.contentStreamName).containsKey(Elements.ATTRIBUTE_XLINK_HREF)) {
                            throw new XmlCorruptedException("Content stream with storage set to '"
                                + de.escidoc.core.common.business.fedora.Constants.STORAGE_EXTERNAL_MANAGED + "' "
                                + "needs href.");
                        }
                        // check if control group is changed
                        if (curControlGroup != null && !"E".equals(curControlGroup)) {
                            throw new InvalidContentException(
                                "The value of storage can not be changed in existing content stream '"
                                    + this.contentStreamName + "'.");
                        }

                    }
                    else if (storage.equals(de.escidoc.core.common.business.fedora.Constants.STORAGE_EXTERNAL_URL)) {
                        if (!contentStreams.get(this.contentStreamName).containsKey(Elements.ATTRIBUTE_XLINK_HREF)) {
                            throw new XmlCorruptedException("Content stream with storage set to '"
                                + de.escidoc.core.common.business.fedora.Constants.STORAGE_EXTERNAL_URL + "' "
                                + "needs href.");
                        }
                        // check if control group is changed
                        if (curControlGroup != null && !"R".equals(curControlGroup)) {
                            throw new InvalidContentException(
                                "The value of storage can not be changed in existing content stream '"
                                    + this.contentStreamName + "'.");
                        }

                    }
                }
                catch (final NoSuchAttributeException e) {
                    throw new XmlCorruptedException(e);
                }
            }
            else {
                // in a particular stream
                this.wrote = true;
                writeElement(element);
                final int c = element.getAttributeCount();
                for (int i = 0; i < c; i++) {
                    final Attribute att = element.getAttribute(i);
                    writeAttribute(att.getNamespace(), element.getLocalName(), att.getLocalName(), att.getValue(), att
                        .getPrefix(), element.getNamespaceContext());
                }
            }
        }
        return element;
    }

    /**
     * @return the contentStreams
     */
    public Map<String, Map<String, Object>> getContentStreams() {
        return this.contentStreams;
    }

}
