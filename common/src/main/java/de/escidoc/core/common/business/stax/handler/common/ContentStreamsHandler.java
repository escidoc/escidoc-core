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

package de.escidoc.core.common.business.stax.handler.common;

import de.escidoc.core.common.business.fedora.resources.create.ContentStreamCreate;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.item.ContentStreamHandler2;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Handle Item XML to obtain ContentStreams.
 * <p/>
 * This handler obtains all (required) values of each ContentStream of an Item and handle is within ContentStream
 * objects.
 *
 * @author Steffen Wagner
 */
@Deprecated
public class ContentStreamsHandler extends DefaultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentStreamsHandler.class);

    private String xpathContentStreams = "/item/content-streams";

    private final String xpathContentStream;

    private final StaxParser parser;

    private final List<ContentStreamCreate> contentStreams = new ArrayList<ContentStreamCreate>();

    private boolean parsingContentStream;

    private ContentStreamHandler2 contentStreamHandler;

    /**
     * ContentStreamsHandler.
     *
     * @param parser StAX Parser.
     */
    @Deprecated
    public ContentStreamsHandler(final StaxParser parser) {
        this.parser = parser;
        this.xpathContentStream = this.xpathContentStreams + '/' + Elements.ELEMENT_CONTENT_STREAM;
    }

    /**
     * ContentStreamsHandler.
     *
     * @param parser StAX Parser.
     * @param xpathContentStreams
     */
    @Deprecated
    public ContentStreamsHandler(final StaxParser parser, final String xpathContentStreams) {
        this.xpathContentStreams = xpathContentStreams;
        this.xpathContentStream = this.xpathContentStreams + '/' + Elements.ELEMENT_CONTENT_STREAM;
        this.parser = parser;
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

        if (this.parsingContentStream) {
            this.contentStreamHandler.startElement(element);
        }
        else {
            final String currentPath = parser.getCurPath();

            if (xpathContentStream.equals(currentPath)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Parsing " + this.xpathContentStream);
                }
                this.parsingContentStream = true;
                this.contentStreamHandler = new ContentStreamHandler2(this.parser, this.xpathContentStream);
                this.contentStreamHandler.startElement(element);
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
    public EndElement endElement(final EndElement element) throws WebserverSystemException, InvalidContentException {

        final String currentPath = parser.getCurPath();

        if (xpathContentStream.equals(currentPath)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Reached end of " + this.xpathContentStream);
            }

            this.parsingContentStream = false;
            this.contentStreamHandler.endElement(element);
            checkUniqueContentStreamNames(this.contentStreamHandler.getContentStream().getName());
            this.contentStreams.add(this.contentStreamHandler.getContentStream());
        }
        else if (this.parsingContentStream) {
            this.contentStreamHandler.endElement(element);
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

        if (this.parsingContentStream) {
            this.contentStreamHandler.characters(s, element);
        }

        return s;
    }

    /**
     * Get all Components of the ComponentHandler.
     *
     * @return Vector with all ContentStreams.
     */
    public List<ContentStreamCreate> getContentStreams() {

        return this.contentStreams;
    }

    /**
     * Check if the name of content stream is unique.
     *
     * @param name Name which is to check.
     * @throws InvalidContentException Thrown if the provided name is used multiple times.
     */
    private void checkUniqueContentStreamNames(final String name) throws InvalidContentException {

        for (final ContentStreamCreate contentStream : this.contentStreams) {
            if (name.equals(contentStream.getName())) {
                throw new InvalidContentException("The item representation contains multiple "
                    + "content streams with a name '" + name + "'.");
            }
        }
    }
}
