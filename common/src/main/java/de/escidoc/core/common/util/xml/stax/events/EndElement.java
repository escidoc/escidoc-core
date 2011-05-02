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

public class EndElement extends AbstractElement {

    private String localName;

    private String namespace;

    private String prefix;

    public EndElement() {

        super(null, null);
        this.localName = null;
        this.namespace = null;
        this.prefix = null;
    }

    public EndElement(final String localName, final String namespace, final String prefix) {

        super(null, null);
        this.localName = localName;
        this.namespace = namespace;
        this.prefix = prefix;
    }

    public EndElement(final XMLStreamReader parser, final String path) {

        super(parser, path);
        this.localName = parser.getLocalName();
        this.namespace = parser.getNamespaceURI();
        this.prefix = parser.getPrefix();
    }

    public String getLocalName() {
        return this.localName;
    }

    public void setLocalName(final String localName) {
        this.localName = localName;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }

}
