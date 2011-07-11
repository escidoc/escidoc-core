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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xerces.dom.DOMInputImpl;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import de.escidoc.core.common.business.Constants;

/**
 * Helper class to change the base-url of imported schemas.
 *
 * @author Michael Hoppe
 */
public class SchemaBaseResourceResolver implements LSResourceResolver {

    /**
     * Pattern used to detect base-url of schema-location in imported schemas.
     */
    private static final Pattern PATTERN_SCHEMA_LOCATION_BASE = Pattern.compile(Constants.SCHEMA_LOCATION_BASE);

    /**
     * Replaces base-part of system-id.
     *
     * @param type         String
     * @param namespaceURI String1
     * @param publicId     String2
     * @param systemId     String3
     * @param baseURI      String4
     * @return LSInput LSInput.
     */
    @Override
    public LSInput resolveResource(
        final String type, final String namespaceURI, final String publicId, final String systemId, final String baseURI) {
        if (systemId != null) {
            final Matcher schemaLocationMatcher = PATTERN_SCHEMA_LOCATION_BASE.matcher(systemId);
            if (schemaLocationMatcher.find()) {
                final String systemIdLocal = schemaLocationMatcher.replaceAll(XmlUtility.getSchemaBaseUrl());
                return new DOMInputImpl(publicId, systemIdLocal, baseURI);
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }
}
