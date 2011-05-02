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

package de.escidoc.core.common.util.xml;

/**
 * This class provides escaping of special XML characters in text content and attributes.
 *
 * @author Torsten Tetteroo
 */
public final class XmlEscaper {

    // TODO: Make this constructor private!
    public XmlEscaper() {
    }

    /**
     * Escapes the provided value for output in an attribute.
     *
     * @param attributeValue The value to escape.
     * @return Returns the escpaed value.
     */
    public static String escapeAttribute(final String attributeValue) {
        return XmlUtility.escapeForbiddenXmlCharacters(attributeValue);
    }

    /**
     * Escapes the provided value for output in text content.
     *
     * @param textContent The value to escape.
     * @return Returns the escpaed value.
     */
    public static String escapeTextContent(final String textContent) {
        return XmlUtility.escapeForbiddenXmlCharacters(textContent);
    }

    /**
     * Unescapes the provided value.
     *
     * @param value The value to unescape.
     * @return Returns the unescpaed value.
     */
    public static String unescape(final String value) {
        return XmlUtility.unescapeForbiddenXmlCharacters(value);
    }

}
