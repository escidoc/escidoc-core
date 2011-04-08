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

package de.escidoc.core.common.util.xml.stax.events;

import javax.xml.namespace.NamespaceContext;

public class StartElementWithText extends StartElement {

    private String elementText;

    public StartElementWithText() {
        this.elementText = null;
    }

    /**
     * Start Element.
     *
     * @param localName   The local name.
     * @param namespace   The namespace.
     * @param prefix      The prefix
     * @param elementText The text of the element.
     * @param nsContext   TODO
     */
    public StartElementWithText(final String localName, final String namespace, final String prefix,
        final String elementText, final NamespaceContext nsContext) {

        super(localName, namespace, prefix, nsContext);
        this.elementText = elementText;
    }

    public String getElementText() {
        return this.elementText;
    }

    public void setElementText(final String elementText) {
        this.elementText = elementText;
    }
}
