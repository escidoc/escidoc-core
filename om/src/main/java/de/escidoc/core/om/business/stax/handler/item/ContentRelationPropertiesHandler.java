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
package de.escidoc.core.om.business.stax.handler.item;

import de.escidoc.core.common.business.fedora.resources.StatusType;
import de.escidoc.core.common.business.fedora.resources.create.ContentRelationProperties;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle and obtain values from Content Relation Properties section.
 */
public class ContentRelationPropertiesHandler extends DefaultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentRelationPropertiesHandler.class);

    private final StaxParser parser;

    private final ContentRelationProperties properties;

    private static final String XPATH_CONTENT_RELATION = '/' + Elements.ELEMENT_CONTENT_RELATION;

    private static final String XPATH_CONTENT_RELATION_PROPERTIES =
        XPATH_CONTENT_RELATION + '/' + Elements.ELEMENT_PROPERTIES;

    private static final String XPATH_DESCRIPTION = XPATH_CONTENT_RELATION_PROPERTIES + "/description";

    private boolean parsingDescription;

    private String tmpDescription;

    /**
     * @param parser StAX Parser
     * @throws WebserverSystemException Thrown if setting ContentRelationProperties failed.
     */
    public ContentRelationPropertiesHandler(final StaxParser parser) throws WebserverSystemException {

        this.parser = parser;
        this.properties = new ContentRelationProperties();
    }

    /**
     * Get ContentRelationProperties.
     *
     * @return ContentRelationProperties.
     */
    public ContentRelationProperties getProperties() {

        return this.properties;
    }

    /**
     * @param element StartElement
     * @return StartElement
     */
    @Override
    public StartElement startElement(final StartElement element) {

        final String currentPath = parser.getCurPath();
        if (XPATH_DESCRIPTION.equals(currentPath)) {
            this.parsingDescription = true;
            this.tmpDescription = "";
        }

        return element;
    }

    /**
     * @param element EndElement
     * @return EndElement
     */
    @Override
    public EndElement endElement(final EndElement element) {

        final String currentPath = parser.getCurPath();
        if (XPATH_DESCRIPTION.equals(currentPath)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Parser reached end of " + XPATH_DESCRIPTION);
            }
            // parser leaves the XML description element
            this.parsingDescription = false;
            this.properties.setDescription(this.tmpDescription);
        }

        return element;
    }

    /**
     * Parser hits an XML character element.
     *
     * @param s       XML character element.
     * @param element StAX StartElement
     * @return XML character element.
     * @throws InvalidStatusException Thrown if value of status is invalid text.
     */
    @Override
    public String characters(final String s, final StartElement element) throws InvalidStatusException {

        final String curPath = parser.getCurPath();
        if (curPath.equals(XPATH_CONTENT_RELATION_PROPERTIES + '/' + Elements.ELEMENT_PID)) {
            this.properties.setPid(s);
        }
        else if (curPath.equals(XPATH_CONTENT_RELATION_PROPERTIES + '/' + Elements.ELEMENT_PUBLIC_STATUS)) {
            this.properties.setStatus(StatusType.getStatusType(s));
        }
        else if (this.parsingDescription) {
            this.tmpDescription += s;
        }

        return s;
    }
}
