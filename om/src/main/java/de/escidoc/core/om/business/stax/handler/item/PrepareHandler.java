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
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

public class PrepareHandler extends DefaultHandler {

    private static final String ELEMENT_PATH = "/item/components/component/content";

    private final StaxParser parser;

    private final Map<Integer, Map<String, String>> binaryData = new HashMap<Integer, Map<String, String>>();

    private String uploadUrl;

    private String storageValue;

    private String content;

    private boolean inContent;

    private int componentNumber;

    public PrepareHandler(final StaxParser parser) {
        this.parser = parser;
    }

    public Map<Integer, Map<String, String>> getBinaryData() {
        return this.binaryData;
    }

    @Override
    public StartElement startElement(final StartElement element) throws InvalidContentException {

        final String currentPath = parser.getCurPath();

        if (ELEMENT_PATH.equals(currentPath)) {

            this.storageValue = getStorageAttribute(element);
            this.inContent = true;
            final Map<String, String> componentBinary = new HashMap<String, String>();
            componentBinary.put("storage", this.storageValue);
            binaryData.put(this.componentNumber, componentBinary);

            final int indexOfHref = element.indexOfAttribute(Constants.XLINK_URI, "href");
            if (indexOfHref != -1) {
                final Attribute href = element.getAttribute(indexOfHref);
                this.uploadUrl = href.getValue();
            }

        }
        return null;
    }

    @Override
    public EndElement endElement(final EndElement element) throws MissingContentException {

        if (this.inContent) {
            final Map<String, String> componentBinary = binaryData.get(this.componentNumber);
            if (this.content == null) {
                if (this.uploadUrl != null && this.uploadUrl.length() > 0) {
                    // FIXME use constant as in
                    // ItemHandlerCreate.handleComponent()
                    componentBinary.put("uploadUrl", this.uploadUrl);
                }
                else {
                    throw new MissingContentException("The content of component with id " + this.componentNumber
                        + " is missing");
                }
            }
            this.inContent = false;
            this.componentNumber++;
            this.uploadUrl = null;
            this.content = null;
            this.storageValue = null;
        }
        return null;
    }

    @Override
    public String characters(final String s, final StartElement element) throws InvalidContentException {

        if (this.inContent) {

            final Map<String, String> componentBinary = binaryData.get(this.componentNumber);
            if (s != null && s.length() > 0) {
                if (this.storageValue.equals(de.escidoc.core.common.business.fedora.Constants.STORAGE_EXTERNAL_URL)
                    || this.storageValue
                        .equals(de.escidoc.core.common.business.fedora.Constants.STORAGE_EXTERNAL_MANAGED)) {
                    throw new InvalidContentException(
                        "The component section 'content' with the attribute 'storage' set to 'external-url' "
                            + "or 'external-managed' may not have an inline content.");
                }

                this.content = s;
                // FIXME use constant as in ItemHandlerCreate.handleComponent()
                componentBinary.put("content", s);

            }
        }
        return null;
    }

    /**
     * Get value of attribute storage.
     *
     * @param element StartElement.
     * @return Value of attribute storage.
     * @throws InvalidContentException Thrown if attribute Storage not exists.
     */
    private static String getStorageAttribute(final StartElement element) throws InvalidContentException {

        final int indexOfStorage = element.indexOfAttribute(null, "storage");
        if (indexOfStorage == -1) {
            throw new InvalidContentException("The attribute 'storage' of the element '" + element.getLocalName()
                + "' is missing.");
        }
        final Attribute storage = element.getAttribute(indexOfStorage);
        return storage.getValue();
    }
}
