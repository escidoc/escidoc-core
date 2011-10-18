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
package de.escidoc.core.om.business.stax.handler;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.XMLConstants;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ContentRelationsOntologyHandler extends DefaultHandler {

    private final StaxParser parser;

    private String base = "";

    private String predicate;

    private final List<String> predicates = new ArrayList<String>();

    private boolean inDescription;

    private boolean inRdfType;

    private static final String RDF_PROPERTY_URI = Constants.RDF_NAMESPACE_URI + "Property";

    private static final String PROPERTY_PATH = "/RDF/Property";

    private static final String DESCRIPTION_PATH = "/RDF/Description";

    private static final String BASE_PATH = "/RDF";

    private static final String RDF_TYPE_PATH = "/RDF/Description/type";

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentRelationsOntologyHandler.class);

    public ContentRelationsOntologyHandler(final StaxParser parser) {

        this.parser = parser;

    }

    @Override
    public StartElement startElement(final StartElement element) throws InvalidContentException, XmlCorruptedException {

        final String currentPath = parser.getCurPath();
        if (BASE_PATH.equals(currentPath)) {
            final int indexOfBase = element.indexOfAttribute(XMLConstants.XML_NS_URI, "base");
            if (indexOfBase != -1) {
                this.base = element.getAttribute(indexOfBase).getValue();
            }
        }
        // check if a parent element 'Description' defines rdf:Property
        else if (this.inDescription && currentPath.startsWith(DESCRIPTION_PATH)) {
            if (RDF_TYPE_PATH.equals(currentPath) && element.getNamespace().equals(Constants.RDF_NAMESPACE_URI)) {
                this.inRdfType = true;

                final int indexOfResource = element.indexOfAttribute(Constants.RDF_NAMESPACE_URI, "resource");
                if (indexOfResource == -1) {
                    throw new XmlCorruptedException("The ontology-xml is not valide rdf/xml. "
                        + "The element 'rdf:type' must have the attribute 'resource'.");
                }
                else {
                    final String resourceValue = element.getAttribute(indexOfResource).getValue();
                    if (!resourceValue.equals(RDF_PROPERTY_URI)) {
                        this.predicate = null;

                    }
                }
            }
        }
        else if (PROPERTY_PATH.equals(currentPath) && element.getNamespace().equals(Constants.RDF_NAMESPACE_URI)) {
            setPredicate(element);
            if (this.predicate == null) {
                throw new XmlCorruptedException("The ontology-xml is not valide rdf/xml."
                    + "The element 'rdf:Property' must have one of the attributes 'id' or 'about'");
            }
        }
        else if (DESCRIPTION_PATH.equals(currentPath) && element.getNamespace().equals(Constants.RDF_NAMESPACE_URI)) {
            this.inDescription = true;
            setPredicate(element);
            if (this.predicate == null) {
                throw new XmlCorruptedException("The ontology-xml is not valide rdf/xml."
                    + "The element 'rdf:Description' must have one of the attributes 'id' or 'about'");
            }
        }
        return element;
    }

    @Override
    public EndElement endElement(final EndElement element) throws InvalidContentException {

        final String currentPath = parser.getCurPath();
        if (DESCRIPTION_PATH.equals(currentPath) && element.getNamespace().equals(Constants.RDF_NAMESPACE_URI)) {
            if (!this.inRdfType) {
                this.predicate = null;
            }
            this.inDescription = false;
        }

        if (this.predicate != null && (PROPERTY_PATH.equals(currentPath) || DESCRIPTION_PATH.equals(currentPath))
            && element.getNamespace().equals(Constants.RDF_NAMESPACE_URI)) {
            predicates.add(this.predicate);
            this.predicate = null;
        }
        return element;

    }

    private void setPredicate(final StartElement element) throws InvalidContentException {
        final int indexOfId = element.indexOfAttribute(Constants.RDF_NAMESPACE_URI, "ID");
        if (indexOfId == -1) {
            final int indexOfAbout = element.indexOfAttribute(Constants.RDF_NAMESPACE_URI, "about");
            if (indexOfAbout != -1) {
                final String about = element.getAttribute(indexOfAbout).getValue();
                if (this.base != null) {
                    try {
                        // test if a value of about is an absolute URI
                        new URI(about);
                        this.predicate = about;
                    }
                    catch (final URISyntaxException e) {
                        if (LOGGER.isWarnEnabled()) {
                            LOGGER.warn("Invalid URL '" + about + '\'');
                        }
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Invalid URL '" + about + '\'', e);
                        }
                        this.predicate = this.base + about;
                    }
                }
                else {
                    this.predicate = about;
                }
            }
        }
        else {
            final String id = element.getAttribute(indexOfId).getValue();
            if (this.base != null) {
                this.predicate = this.base + '#' + id;
            }
            else {
                throw new InvalidContentException("The ontology-xml does not contain a "
                    + "base-url. Therefore the element 'Description' " + "may not contain the attribute 'id'.");
            }
        }
    }

    public List<String> getPredicates() {
        return this.predicates;
    }
}
