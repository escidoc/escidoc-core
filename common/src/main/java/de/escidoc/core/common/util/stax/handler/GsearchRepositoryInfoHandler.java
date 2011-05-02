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

import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Parses the GsearchRepositoryInfo-XML into a Hash-Structure.
 *
 * @author Michael Hoppe
 */
public class GsearchRepositoryInfoHandler extends DefaultHandler {

    private final Map<String, String> gsearchRepositoryInfo = new HashMap<String, String>();

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
        gsearchRepositoryInfo.put(element.getLocalName(), s);
        return s;
    }

    /**
     * @return the gsearchRepositoryInfo
     */
    public Map<String, String> getGsearchRepositoryInfo() {
        return this.gsearchRepositoryInfo;
    }

}
