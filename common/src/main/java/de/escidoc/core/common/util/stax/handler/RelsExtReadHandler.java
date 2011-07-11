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

package de.escidoc.core.common.util.stax.handler;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.Triple;
import de.escidoc.core.common.business.fedora.Triples;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.directory.NoSuchAttributeException;

/**
 * Read RelsExt and stores predicate and values within a Map.
 *
 * @author Steffen Wagner
 */
public class RelsExtReadHandler extends DefaultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelsExtReadHandler.class);

    private final StaxParser parser;

    private static final String RDF_DESCRIPTION_PATH = "/RDF/Description";

    private static final String RDF_ABOUT = "about";

    private static final String RDF_RESOURCE = "resource";

    private boolean inTripleSection;

    private boolean readCharacter = true;

    private boolean cleanIdentifier;

    private final Triples triples = new Triples();

    private String subject;

    private String predicate;

    private String object;

    /**
     * RelsExtReadHandler.
     *
     * @param parser The Parser.
     */
    public RelsExtReadHandler(final StaxParser parser) {

        this.parser = parser;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#startElement
     * (de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public StartElement startElement(final StartElement element) throws WebserverSystemException {

        if (this.inTripleSection) {
            this.predicate = element.getNamespace() + element.getLocalName();
            getObjectValue(element);
        }
        else if (parser.getCurPath().startsWith(RDF_DESCRIPTION_PATH)) {
            if (parser.getCurPath().equals(RDF_DESCRIPTION_PATH)) {
                try {
                    // select subject
                    this.subject = element.getAttributeValue(Constants.RDF_NAMESPACE_URI, RDF_ABOUT);
                    if (this.cleanIdentifier) {
                        this.subject = cleanIdentifier(this.subject);
                    }
                    getObjectValue(element);
                }
                catch (final NoSuchAttributeException e) {
                    throw new WebserverSystemException(e);
                }
            }
            else {
                this.inTripleSection = true;
                // handle the first element
                this.predicate = element.getNamespace() + element.getLocalName();
            }
        }

        return element;
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
    public String characters(final String data, final StartElement element) throws IntegritySystemException {

        if (this.inTripleSection && this.readCharacter) {
            this.object += data;
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
    public EndElement endElement(final EndElement element) {

        if (this.inTripleSection) {
            if (RDF_DESCRIPTION_PATH.equals(parser.getCurPath())) {
                this.inTripleSection = false;
            }
            else {
                this.triples.add(new Triple(this.subject, this.predicate, this.object));
            }
        }

        return element;
    }

    /**
     * Switch if info:fedora/ should be removed from resource object or not.
     *
     * @param clean Set true to remove info:fedora/ from object. False (default) keeps object value untouched.
     */
    public void cleanIdentifier(final boolean clean) {
        this.cleanIdentifier = clean;
    }

    /**
     * Get all from RELS-EXT obtained Triples.
     *
     * @return Triples
     */
    public Triples getElementValues() {
        return this.triples;
    }

    /**
     * Get the value of attribute resource.
     *
     * @param element The start element.
     */
    private void getObjectValue(final StartElement element) {

        final int index = element.indexOfAttribute(Constants.RDF_NAMESPACE_URI, RDF_RESOURCE);
        if (index == -1) {
            this.readCharacter = true;
            this.object = "";
        }
        else {
            try {
                this.object = element.getAttribute(index).getValue();
                if (this.cleanIdentifier) {
                    this.object = cleanIdentifier(this.object);
                }
            }
            catch (final IndexOutOfBoundsException e) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Error on getting attribute.");
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Error on getting attribute.", e);
                }
            }
        }
    }

    /**
     * Removes the first 12 character from String, which should remove info:fedora/.
     *
     * @param identifier The String where info:fedora/ is to remove
     * @return the cleaned resource identifier.
     */
    private static String cleanIdentifier(final String identifier) {

        if (identifier.startsWith(Constants.IDENTIFIER_PREFIX)) {
            return identifier.substring(Constants.IDENTIFIER_PREFIX.length());
        }

        return identifier;

    }
}
