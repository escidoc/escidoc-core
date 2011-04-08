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
import java.util.ArrayList;
import java.util.List;

/**
 * StartElementWithChildElements.
 */
public class StartElementWithChildElements extends StartElementWithText {

    private List<StartElementWithText> children;

    /**
     * StartElementWithChildElements.
     */
    public StartElementWithChildElements() {
        this.children = null;
    }

    /**
     * StartElementWithChildElements.
     *
     * @param localName   The localName (node).
     * @param namespace   The namespace.
     * @param prefix      The prefix.
     * @param children    The childrens
     * @param elementText The element text
     * @param nscontext   TODO
     */
    public StartElementWithChildElements(final String localName, final String namespace, final String prefix,
        final List<StartElementWithText> children, final String elementText, final NamespaceContext nscontext) {

        super(localName, namespace, prefix, elementText, nscontext);
        this.children = children;
    }

    /**
     * StartElementWithChildElements.
     *
     * @param localName   The localName (node).
     * @param namespace   The namespace.
     * @param prefix      The prefix.
     * @param children    The childrens
     * @param elementText The element text
     * @param nscontext   TODO
     * @param position
     */
    public StartElementWithChildElements(final String localName, final String namespace, final String prefix,
        final List<StartElementWithText> children, final String elementText, final NamespaceContext nscontext,
        final int position) {

        super(localName, namespace, prefix, elementText, nscontext);
        this.children = children;
        setPosition(position);

    }

    /**
     * Set children elements.
     *
     * @param childs Vector of child elements.
     */
    public void setChildrenElements(final List<StartElementWithText> childs) {
        this.children = childs;
    }

    /**
     * Add a child element.
     *
     * @param child A new child element.
     */
    public void addChildElement(final StartElementWithText child) {
        if (this.children == null) {
            this.children = new ArrayList<StartElementWithText>();
        }
        children.add(child);
    }

    /**
     * Get all child elements.
     *
     * @return all child elements
     */
    public List<StartElementWithText> getChildrenElements() {
        return this.children;
    }

}
