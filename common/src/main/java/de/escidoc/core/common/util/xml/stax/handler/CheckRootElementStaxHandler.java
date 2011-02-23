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
package de.escidoc.core.common.util.xml.stax.handler;

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.stax.events.StartElement;

/**
 * Stax handler that asserts the name of the root element.
 * 
 * @author TTE
 * @common
 */
public class CheckRootElementStaxHandler extends DefaultHandler {

    private final String expectedRootElement;

    /**
     * The constructor.
     * 
     * @param expectedRootElement
     *            The expected root element.
     */
    public CheckRootElementStaxHandler(final String expectedRootElement) {

        this.expectedRootElement = expectedRootElement;
    }

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @param element
     * @return
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler
     *      #startElement
     *      (de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public StartElement startElement(final StartElement element)
        throws EscidocException {

        if (element.getLocalName().equals(expectedRootElement)) {
            throw new WebserverSystemException(
                    "Check successful, please ignore.");
        } else {
            throw new XmlCorruptedException(StringUtility
                    .format(
                            "Root element is not as expected", expectedRootElement,
                            element.getLocalName()));
        }
    }
    // CHECKSTYLE:JAVADOC-ON
}
