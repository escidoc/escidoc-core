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

/**
 * Attribute.
 */
public class Attribute {

    private String localName;

    private String namespace;

    private String prefix;

    private String value;

    /**
     * Attribute.
     */
    public Attribute() {
        this.localName = null;
        this.namespace = null;
        this.prefix = null;
        this.value = null;
    }

    /**
     * Attribute.
     *
     * @param localName The local name.
     * @param namespace The namespace.
     * @param prefix    The prefix.
     * @param value     The attribute value.
     */
    public Attribute(final String localName, final String namespace, final String prefix, final String value) {

        this.localName = localName;
        this.namespace = namespace;
        this.prefix = prefix;
        this.value = value;
    }

    /**
     * Attribute.
     *
     * @param parser The parser.
     * @param index  The index.
     * @throws XmlParserSystemException Thrown if parsing failed.
     */
    public Attribute(final XMLStreamReader parser, final int index) {

        this.localName = parser.getAttributeLocalName(index);
        this.namespace = parser.getAttributeNamespace(index);
        this.prefix = parser.getAttributePrefix(index);
        this.value = parser.getAttributeValue(index);
    }

    /**
     * Get the local name (node).
     *
     * @return local name
     */
    public String getLocalName() {
        return this.localName;
    }

    /**
     * Set the local (node) name.
     *
     * @param localName The name of the node.
     */
    public void setLocalName(final String localName) {
        this.localName = localName;
    }

    /**
     * Get the namespace.
     *
     * @return namespace
     */
    public String getNamespace() {
        return this.namespace;
    }

    /**
     * Set the namespace.
     *
     * @param namespace The new namespace.
     */
    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    /**
     * Get the prefix.
     *
     * @return prefix
     */
    public String getPrefix() {
        return this.prefix;
    }

    /**
     * Set the prefix.
     *
     * @param prefix The prefix
     */
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }

    /**
     * Get the attribute value.
     *
     * @return value of attribute
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Set the value of the attribute.
     *
     * @param value attribute value
     */
    public void setValue(final String value) {
        this.value = value;
    }

}
