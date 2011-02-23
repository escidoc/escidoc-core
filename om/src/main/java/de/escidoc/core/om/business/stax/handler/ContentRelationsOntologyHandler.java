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
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

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

    private boolean inDescription = false;

    private boolean inRdfType = false;

    private static final String RDF_PROPERTY_URI =
        Constants.RDF_NAMESPACE_URI + "Property";

    private static final String PROPERTY_PATH = "/RDF/Property";

    private static final String DESCRIPTION_PATH = "/RDF/Description";

    private static final String BASE_PATH = "/RDF";

    private static final String RDF_TYPE_PATH = "/RDF/Description/type";

    private static final AppLogger LOGGER =
        new AppLogger(ContentRelationsOntologyHandler.class.getName());

    public ContentRelationsOntologyHandler(final StaxParser parser) {

        this.parser = parser;

    }

    @Override
    public StartElement startElement(StartElement element)
        throws InvalidXmlException, InvalidContentException {

        String currentPath = parser.getCurPath();
        if (BASE_PATH.equals(currentPath)) {
            int indexOfBase =
                element.indexOfAttribute(XMLConstants.XML_NS_URI, "base");
            if (indexOfBase != -1) {
                this.base = element.getAttribute(indexOfBase).getValue();
            }
        }
        // check if a parent element 'Description' defines rdf:Property
        else if (inDescription && currentPath.startsWith(DESCRIPTION_PATH)) {
            if (RDF_TYPE_PATH.equals(currentPath)
                && element.getNamespace().equals(Constants.RDF_NAMESPACE_URI)) {
                inRdfType = true;

                int indexOfResource =
                    element.indexOfAttribute(Constants.RDF_NAMESPACE_URI,
                        "resource");
                if (indexOfResource == -1) {
                    String message =
                            "The ontology-xml is not valide rdf/xml. "
                                    + "The element 'rdf:type' must have "
                                    + "the attribute 'resource'.";
                    LOGGER.debug(message);
                    throw new XmlCorruptedException(message);
                } else {
                    String resourceValue =
                            element.getAttribute(indexOfResource).getValue();
                    if (!resourceValue.equals(RDF_PROPERTY_URI)) {
                        this.predicate = null;

                    }
                }
            }
        }
        else if (PROPERTY_PATH.equals(currentPath)
            && element.getNamespace().equals(Constants.RDF_NAMESPACE_URI)) {
            setPredicate(element);
            if (this.predicate == null) {
                String message =
                    "The ontology-xml is not valide rdf/xml."
                        + "The element 'rdf:Property' must have "
                        + "one of the attributes 'id' or 'about'";
                LOGGER.debug(message);
                throw new XmlCorruptedException(message);
            }
        }
        else if (DESCRIPTION_PATH.equals(currentPath)
            && element.getNamespace().equals(Constants.RDF_NAMESPACE_URI)) {
            inDescription = true;
            setPredicate(element);
            if (this.predicate == null) {
                String message =
                    "The ontology-xml is not valide rdf/xml."
                        + "The element 'rdf:Description' must have "
                        + "one of the attributes 'id' or 'about'";
                LOGGER.debug(message);
                throw new XmlCorruptedException(message);
            }
        }
        return element;
    }

    @Override
    public EndElement endElement(EndElement element)
        throws InvalidContentException {

        String currentPath = parser.getCurPath();
        if (DESCRIPTION_PATH.equals(currentPath)
            && element.getNamespace().equals(Constants.RDF_NAMESPACE_URI)) {
            if (!inRdfType) {
                this.predicate = null;
            }
            inDescription = false;
        }

        if ((this.predicate != null)
            && ((PROPERTY_PATH.equals(currentPath) || DESCRIPTION_PATH
                .equals(currentPath))
                && element.getNamespace().equals(Constants.RDF_NAMESPACE_URI))) {
            predicates.add(this.predicate);
            this.predicate = null;
        }
        return element;

    }

    private void setPredicate(StartElement element)
        throws InvalidContentException {
        int indexOfId =
            element.indexOfAttribute(
                de.escidoc.core.common.business.Constants.RDF_NAMESPACE_URI,
                "ID");
        if (indexOfId == -1) {
            int indexOfAbout =
                    element
                            .indexOfAttribute(
                                    Constants.RDF_NAMESPACE_URI,
                                    "about");
            if (indexOfAbout != -1) {
                String about = element.getAttribute(indexOfAbout).getValue();
                if (this.base != null) {
                    try {
                        // test if a value of about is an absolute URI
                        URI aboutUri = new URI(about);
                        this.predicate = about;
                    } catch (URISyntaxException e) {
                        this.predicate = this.base + about;
                    }
                } else {
                    this.predicate = about;
                }
            }
        } else {
            String id = element.getAttribute(indexOfId).getValue();
            if (this.base != null) {
                this.predicate = this.base + '#' + id;
            } else {
                String message =
                        "The ontology-xml does not contain a "
                                + "base-url. Therefore the element 'Description' "
                                + "may not contain the attribute 'id'.";
                LOGGER.debug(message);
                throw new InvalidContentException(message);
            }
        }
    }

    public List<String> getPredicates() {
        return this.predicates;
    }
}
