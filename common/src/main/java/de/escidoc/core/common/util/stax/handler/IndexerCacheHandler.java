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

import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * Parses a escidoc-resource and extracts latest version and eventually component-ids.
 *
 * @author Michael Hoppe
 */
public class IndexerCacheHandler extends DefaultHandler {

    private final StaxParser parser;

    private final Set<String> components = new HashSet<String>();

    private int lastVersion = -1;

    private static final String XLINK_URI = "http://www.w3.org/1999/xlink";

    private static final String LATEST_VERSION_PATH = "/properties/latest-version/number";

    private static final String CONTENT_PATH = "/item/components/component/content";

    /**
     * Constructor
     * @param parser
     */
    public IndexerCacheHandler(final StaxParser parser) {
        this.parser = parser;

    }

    @Override
    public StartElement startElement(final StartElement element) {
        if (CONTENT_PATH.equals(parser.getCurPath())) {
            final int indexOfHref = element.indexOfAttribute(XLINK_URI, "href");
            if (indexOfHref != (-1)) {
                final Attribute href = element.getAttribute(indexOfHref);
                components.add(href.getValue());
            }
        }
        return element;
    }

    /**
     * Handle the character section of an element.
     *
     * @param s       The contents of the character section.
     * @param element The element.
     * @return The character section.
     * @throws Exception e
     */
    @Override
    public String characters(final String s, final StartElement element) throws Exception {
        if (parser.getCurPath().endsWith(LATEST_VERSION_PATH)) {
            lastVersion = Integer.parseInt(s);
        }
        return s;
    }

    /**
     * Get components-Set.
     *
     * @return components set.
     */
    public Set<String> getComponents() {
        return this.components;
    }

    /**
     * Get lastVersion.
     *
     * @return last version.
     */
    public int getLastVersion() {
        return this.lastVersion;
    }

}
