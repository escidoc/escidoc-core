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
package de.escidoc.core.cmm.business.stax.handler.contentModel;

import de.escidoc.core.common.business.fedora.resources.create.ContentModelProperties;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

/**
 * Handler to extract property values from content model properties.
 *
 * @author Frank Schwichtenberg
 */
public class ContentModelPropertiesHandler extends DefaultHandler {

    private final StaxParser parser;

    private final ContentModelProperties properties;

    private static final String XPATH_CONTENT_MODEL = '/' + Elements.ELEMENT_CONTENT_MODEL;

    private static final String XPATH_CONTENT_MODEL_PROPERTIES =
        XPATH_CONTENT_MODEL + '/' + Elements.ELEMENT_PROPERTIES;

    /**
     * @param parser StAX Parser
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    public ContentModelPropertiesHandler(final StaxParser parser) throws WebserverSystemException {
        this.parser = parser;
        this.properties = new ContentModelProperties();
    }

    /**
     * Get ContentModelProperties.
     *
     * @return ContentModelProperties.
     */
    public ContentModelProperties getProperties() {
        return this.properties;
    }

    /**
     * @return StartElement
     */
    @Override
    public StartElement startElement(final StartElement element) {
        return element;
    }

    /**
     * @return EndElement
     */
    @Override
    public EndElement endElement(final EndElement element) throws InvalidXmlException {
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
    public String characters(final String data, final StartElement element) {
        final String curPath = parser.getCurPath();
        if (curPath.equals(XPATH_CONTENT_MODEL_PROPERTIES + '/' + Elements.ELEMENT_NAME)) {
            this.properties.getObjectProperties().setTitle(data);
        }
        else if (curPath.equals(XPATH_CONTENT_MODEL_PROPERTIES + '/' + Elements.ELEMENT_DESCRIPTION)) {
            this.properties.getObjectProperties().setDescription(data);
        }
        return data;
    }
}
