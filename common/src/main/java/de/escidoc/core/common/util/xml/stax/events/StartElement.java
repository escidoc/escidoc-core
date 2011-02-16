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
package de.escidoc.core.common.util.xml.stax.events;

import de.escidoc.core.common.exceptions.system.XmlParserSystemException;

import javax.naming.directory.NoSuchAttributeException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class StartElement extends AbstractElement {

    private String localName;

    private String namespace;

    private String prefix;

    private NamespaceContext nsContext;

    private List<Attribute> attributes;

    private boolean hasCharacters;

    private boolean hasChild;

    /*
     * This is a try to extend the implementation with less effort to enable the
     * position of an element.
     */
    // 0 = all, 1 first occurrence, ..
    private int position = 0;

    public StartElement() {

        super(null, null);
        this.localName = null;
        this.namespace = null;
        this.prefix = null;
        this.nsContext = null;
        this.hasCharacters = false;
        this.hasChild = false;
        this.attributes = new ArrayList<Attribute>();
    }

    public StartElement(final String localName, final String namespace,
        final String prefix, final NamespaceContext nsContext) {

        super(null, null);
        this.localName = localName;
        this.namespace = namespace;
        this.prefix = prefix;
        this.nsContext = nsContext;
        this.hasCharacters = false;
        this.hasChild = false;
        this.attributes = new ArrayList<Attribute>();
    }

    public StartElement(final XMLStreamReader parser, final String path)
        throws XmlParserSystemException {

        super(parser, path);
        this.localName = parser.getLocalName();
        this.namespace = parser.getNamespaceURI();
        this.prefix = parser.getPrefix();
        this.nsContext = parser.getNamespaceContext();
        this.hasCharacters = false;
        this.hasChild = false;
        this.attributes = new ArrayList<Attribute>();
        // init attributes
        int attCount = parser.getAttributeCount();
        for (int i = 0; i < attCount; i++) {
            Attribute attribute = new Attribute(parser, i);
            this.attributes.add(attribute);
        }
    }

    /*
     * public void setAttributes(List attributes) { this.attributes =
     * attributes; }
     */

    public Attribute getAttribute(final int index)
        throws IndexOutOfBoundsException {
        return (Attribute) attributes.get(index);
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public NamespaceContext getNamespaceContext() {
        return this.nsContext;

    }

    public Attribute getAttribute(
        final String namespaceUri, final String localName)
        throws NoSuchAttributeException {
        Attribute result = null;

        int index = indexOfAttribute(namespaceUri, localName);
        if (index >= 0) {
            result = getAttribute(index);
        }
        else {
            throw new NoSuchAttributeException("No attribute {" + namespaceUri
                + "}" + localName + ".");
        }

        return result;

    }

    public boolean hasAttribute(
        final String namespaceUri, final String localName) {
        try {
            getAttribute(namespaceUri, localName);
        }
        catch (NoSuchAttributeException e) {
            return false;
        }
        return true;
    }

    public String getAttributeValue(
        final String namespaceUri, final String localName)
        throws NoSuchAttributeException {

        return getAttribute(namespaceUri, localName).getValue();
    }

    public int indexOfAttribute(final String namespace, final String localName) {
        int size = attributes.size();
        for (int i = 0; i < size; i++) {
            Attribute att = (Attribute) attributes.get(i);
            if (att.getLocalName().equals(localName)) {
                if (namespace == null || namespace.length() == 0) {
                    String ns = att.getNamespace();
                    if (ns == null || ns.length() == 0) {
                        return i;
                    }
                }
                else if (att.getNamespace() != null
                    && att.getNamespace().equals(namespace)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void setAttribute(final int index, final Attribute att) {
        attributes.set(index, att);
    }

    public void removeAttribute(final int index) {
        attributes.remove(index);
    }

    public void addAttribute(final Attribute attribute) {
        this.attributes.add(attribute);
    }

    public int getAttributeCount() {
        return attributes.size();
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(final String localName) {
        this.localName = localName;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }

    public boolean isEmpty() {
        return !(hasCharacters || hasChild);
    }

    public boolean hasCharacters() {
        return hasCharacters;
    }

    public void setHasCharacters(final boolean hasCharacters) {
        this.hasCharacters = hasCharacters;
    }

    public boolean hasChild() {
        return hasChild;
    }

    public void setHasChild(final boolean hasChild) {
        this.hasChild = hasChild;
    }

    /**
     * Get the defined position of the start element. <br/>
     * 0 = any position, 1 = first occurrence, 2 = ..
     * 
     * @return position of element
     */
    public int getPosition() {

        return this.position;
    }

    /**
     * Set the defined position of the start element. <br/>
     * 0 = any position, 1 = first occurrence, 2 = ..
     * 
     * @param position
     *            of element
     */
    public void setPosition(final int position) {

        this.position = position;
    }

}
