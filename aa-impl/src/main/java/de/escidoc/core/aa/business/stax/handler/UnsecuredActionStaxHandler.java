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

import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

/**
 * Stax handler that manages the unsecured-action elements.
 *
 * @author Torsten Tetteroo
 */
public class UnsecuredActionStaxHandler extends DefaultHandler {

    private static final String UNSECURED_ACTION_PATH =
        '/' + XmlUtility.NAME_UNSECURED_ACTIONS + '/' + XmlUtility.NAME_UNSECURED_ACTION;

    private final StringBuffer unsecuredActions = new StringBuffer();

    /**
     * See Interface for functional description.
     *
     * @see DefaultHandler #characters(java.lang.String, de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public String characters(final String s, final StartElement element) {

        final String currentPath = element.getPath();
        if (UNSECURED_ACTION_PATH.equals(currentPath)) {
            unsecuredActions.append(s);
            unsecuredActions.append(' ');
        }

        return s;
    }

    /**
     * Gets the parsed unsecured actions.
     *
     * @return Returns the unsecured actions in a {@link String} with spaces as delimiters.
     */
    public String getUnsecuredActions() {

        return unsecuredActions.toString();
    }
}
