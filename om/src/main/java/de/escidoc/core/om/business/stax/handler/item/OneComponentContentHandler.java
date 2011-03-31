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

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler for content of one Component.
 *
 * @author ??
 */
public class OneComponentContentHandler extends DefaultHandler {

    private final StaxParser parser;

    private final String elementPath;

    private final Map<String, String> componentBinary = new HashMap<String, String>();

    private String uploadUrl;

    private String content;

    private boolean inContent;

    /**
     * OneComponentContentHandler.
     *
     * @param parser StAX parser.
     */
    public OneComponentContentHandler(final StaxParser parser) {
        this.parser = parser;
        this.elementPath = "/component/content";
    }

    /**
     * OneComponentContentHandler.
     *
     * @param parser StAX parser.
     * @param path   XPath to component content.
     */
    public OneComponentContentHandler(final StaxParser parser, final String path) {
        this.parser = parser;
        this.elementPath = path;
    }

    /**
     * Data structure which contains keys (storage, uploadUrl, content) depending on XML of Component.
     *
     * @return Map with Component content.
     */
    public Map<String, String> getComponentBinary() {
        return this.componentBinary;
    }

    @Override
    public StartElement startElement(final StartElement element) {

        final String currentPath = parser.getCurPath();
        if (elementPath.equals(currentPath)) {
            final int indexOfStorage = element.indexOfAttribute(null, Elements.ATTRIBUTE_STORAGE);
            String storageValue = null;
            if (indexOfStorage != -1) {
                final Attribute storage = element.getAttribute(indexOfStorage);
                storageValue = storage.getValue();
            }
            componentBinary.put(Elements.ATTRIBUTE_STORAGE, storageValue);
            this.inContent = true;
            final int indexOfHref = element.indexOfAttribute(Constants.XLINK_URI, Elements.ATTRIBUTE_XLINK_HREF);
            if (indexOfHref >= 0) {
                final Attribute href = element.getAttribute(indexOfHref);

                this.uploadUrl = href.getValue();
            }

        }
        return element;
    }

    @Override
    public EndElement endElement(final EndElement element) throws MissingContentException {

        if (this.inContent) {

            if (this.content == null) {
                if (this.uploadUrl != null && this.uploadUrl.length() > 0) {
                    componentBinary.put("uploadUrl", this.uploadUrl);

                }
                else {
                    throw new MissingContentException("The content of one component is missing");
                }
            }
            this.inContent = false;
            this.content = null;
        }
        return element;
    }

    @Override
    public String characters(final String s, final StartElement element) {

        if (this.inContent && s != null && s.length() > 0) {
            if (this.content != null) {
                // we have to concatinate the characters
                this.content += s;
            }
            else {
                this.content = s;
            }
            componentBinary.put("content", s);
        }
        return s;
    }
}
