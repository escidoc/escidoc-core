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
import de.escidoc.core.common.business.fedora.resources.create.BinaryContent;
import de.escidoc.core.common.business.fedora.resources.create.ContentStreamCreate;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.MultipleExtractor;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

/**
 * Handle ContentStream XML to obtain all required ContentStream values.
 *
 * @author Steffen Wagner
 */
public class ContentStreamHandler2 extends DefaultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentStreamHandler2.class);

    private String xpathContentStream = "/item/content-streams/content-stream";

    private final StaxParser parser;

    private boolean parsingContent;

    private ContentStreamCreate contentStream = new ContentStreamCreate();

    private BinaryContent content;

    private MultipleExtractor contentHandler;

    private boolean hasContent;

    /**
     * ContentStreamHandler.
     *
     * @param parser StAX Parser.
     */
    public ContentStreamHandler2(final StaxParser parser) {
        this.parser = parser;
    }

    /**
     * ContentStreamHandler
     *
     * @param parser            StAX Parser.
     * @param contentStreamPath The content-stream element path.
     */
    public ContentStreamHandler2(final StaxParser parser, final String contentStreamPath) {
        this.parser = parser;
        this.xpathContentStream = contentStreamPath;
    }

    /**
     * Parser hits an XML start element.
     *
     * @param element StartElement from StAX parser
     * @return StAX StartElement
     */
    @Override
    public StartElement startElement(final StartElement element) throws InvalidContentException,
        MissingAttributeValueException, WebserverSystemException {

        if (this.parsingContent) {
            if (this.contentHandler == null) {
                // reached first element after content-stream root element
                this.contentHandler =
                    new MultipleExtractor(this.xpathContentStream + '/' + element.getLocalName(), this.parser);
            }
            this.hasContent = true;
            this.contentHandler.startElement(element);
        }
        else {
            final String currentPath = parser.getCurPath();
            if (currentPath.equals(this.xpathContentStream)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Parser reached " + currentPath);
                }
                this.parsingContent = true;

                this.contentStream = new ContentStreamCreate();

                this.contentStream.setName(getAttributeValue(element, null, "name"));
                this.contentStream.setMimeType(getAttributeValue(element, null, "mime-type"));
                // this seams strange (title is a href attribute which should
                // be ignored)
                this.contentStream.setTitle(getAttributeValue(element, Constants.XLINK_NS_URI, "title"));

                this.content = new BinaryContent();
                this.content.setStorageType(getAttributeValue(element, null, "storage"));
                this.content.setDataLocation(getAttributeValue(element, Constants.XLINK_NS_URI, "href"));
            }
        }

        return element;
    }

    /**
     * Parser hits an XML end element.
     *
     * @param element StAX EndElement
     * @return StAX EndElement
     */
    @Override
    public EndElement endElement(final EndElement element) throws WebserverSystemException {

        final String currentPath = parser.getCurPath();

        if (xpathContentStream.equals(currentPath)) {

            this.parsingContent = false;

            if (this.hasContent) {
                final Map<String, Object> outputStreams = this.contentHandler.getOutputStreams();

                // MultipleExtractor could deliver a list of stream. But it
                // should be only possible to extract one with this parser
                // chain.
                if (outputStreams.size() > 1) {
                    LOGGER.warn("Multiple content-streams.");
                }
                final Iterator<String> it = outputStreams.keySet().iterator();
                final ByteArrayOutputStream outStream = (ByteArrayOutputStream) outputStreams.get(it.next());

                try {
                    this.content.setContent(outStream.toString(XmlUtility.CHARACTER_ENCODING));
                }
                catch (final UnsupportedEncodingException e) {
                    throw new WebserverSystemException("Application default encoding not supported.", e);
                }
                this.hasContent = false;
                this.contentHandler = null;
            }
            this.contentStream.setContent(this.content);
        }
        else {
            if (this.parsingContent && this.hasContent) {
                this.contentHandler.endElement(element);
            }
        }

        return element;
    }

    /**
     * Parser hits an XML character element.
     *
     * @param s       XML character element.
     * @param element StAX StartElement
     * @return XML character element.
     */
    @Override
    public String characters(final String s, final StartElement element) throws InvalidContentException,
        MissingElementValueException, WebserverSystemException {

        if (this.parsingContent && this.contentHandler != null) {
            this.contentHandler.characters(s, element);
        }
        return s;
    }

    /**
     * Get the ContentStream.
     * <p/>
     * Attention! ContentStreamCreate is only a transition object. Later implementation has to return the ContentStream
     * class.
     *
     * @return ContentStream
     */
    public ContentStreamCreate getContentStream() {

        return this.contentStream;
    }

}
