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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.business.stax.handler;

import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

public class UserPreferenceReadHandler extends DefaultHandler {

    // preference name must be unique for user : Map
    private final Map<String, String> preferences = new HashMap<String, String>();

    @Override
    public String characters(final String data, final StartElement element) throws Exception {
        if (element.getLocalName().equals(Elements.ELEMENT_USER_PREFERENCE)) {
            final String name = element.getAttributeValue(null, "name");
            this.preferences.put(name, data);
        }
        return super.characters(data, element);
    }

    public Map<String, String> getPreferences() {
        return this.preferences;
    }

}
