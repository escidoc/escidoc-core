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

import javax.xml.stream.XMLStreamReader;

public abstract class AbstractElement {

    private final XMLStreamReader parser;

    private final String path;

    protected AbstractElement(final XMLStreamReader parser, final String path) {

        this.parser = parser;
        this.path = path;
    }

    public int getLineNumber() {

        return this.parser != null ? parser.getLocation().getLineNumber() : -1;
    }

    public int getColumnNumber() {

        return this.parser != null ? parser.getLocation().getColumnNumber() : -1;
    }

    public String getLocationString() {

        return "line " + getLineNumber() + ", column " + getColumnNumber();
    }

    /**
     * Gets the path to the element.
     *
     * @return Returns the path to the element.
     */
    public String getPath() {
        return this.path;
    }

}
