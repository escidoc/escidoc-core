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
 * Copyright 2009 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */

package de.escidoc.core.common.util.xml;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xerces.dom.DOMInputImpl;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;

/**
 * Helper class to change the base-url of imported schemas.
 * 
 * @author MIH
 * @common
 */
public class SchemaBaseResourceResolver implements LSResourceResolver {

    /**
     * Pattern used to detect base-url of schema-location
     * in imported schemas.
     */
    private static final Pattern PATTERN_SCHEMA_LOCATION_BASE =
        Pattern.compile(Constants.SCHEMA_LOCATION_BASE);

    /**
     * Replaces base-part of system-id.
     * 
     * @param s String
     * @param s1 String1
     * @param s2 String2
     * @param s3 String3
     * @param s4 String4
     * @return LSInput LSInput.
     * 
     * @common
     */
    public LSInput resolveResource(final String s,
            final String s1, final String s2,
            final String s3, final String s4) {
        if (s3 != null) {
            Matcher schemaLocationMatcher =
                    PATTERN_SCHEMA_LOCATION_BASE.matcher(s3);
            try {
                // FIXME Use XmlUtility.getSchemaBaseUrl() ?
                if (schemaLocationMatcher.find()
                        && EscidocConfiguration.getInstance().get(
                        EscidocConfiguration
                        .ESCIDOC_CORE_BASEURL) != null
                        && EscidocConfiguration.getInstance().get(
                                EscidocConfiguration
                                .ESCIDOC_CORE_XSD_PATH) != null) {
                    String systemId = schemaLocationMatcher
                            .replaceAll(
                            EscidocConfiguration.getInstance()
                            .get(EscidocConfiguration
                            .ESCIDOC_CORE_BASEURL) 
                            +  EscidocConfiguration.getInstance()
                            .get(EscidocConfiguration
                                    .ESCIDOC_CORE_XSD_PATH));
                    return new DOMInputImpl(
                            s2,
                            systemId,
                            s4);
                } else {
                    return null;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }
}
