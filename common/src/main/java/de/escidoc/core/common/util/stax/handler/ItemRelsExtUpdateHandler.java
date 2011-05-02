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

import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.events.StartElementWithChildElements;
import de.escidoc.core.common.util.xml.stax.events.StartElementWithText;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Deprecated
public class ItemRelsExtUpdateHandler extends DefaultHandler {

    /*
     * FIXME using element name as key for properties is ambiguous, because
     * different predicates could use same element name (the NS differs)!
     * 
     * SWA: It does not worth to fix this. It's better to use internal a Triple
     * representation which is (de-)serialized instead of handling permanently
     * with XML. That's why I've introduced the check against the full path as
     * hot fix.
     */

    private final StaxParser parser;

    private final Map<String, StartElementWithText> props;

    private String path = "/RDF/Description/";

    private final Map<String, Integer> elementPosition = new HashMap<String, Integer>();

    public ItemRelsExtUpdateHandler(final StaxParser parser) {
        this.parser = parser;
        this.props = new TreeMap<String, StartElementWithText>();
    }

    /**
     * RelsExt Update Handler.
     *
     * @param props  Elements to Update.
     * @param parser StAX Parser.
     */
    public ItemRelsExtUpdateHandler(final Map<String, ? extends StartElementWithText> props, final StaxParser parser) {
        this.parser = parser;
        this.props = new TreeMap<String, StartElementWithText>(props);
    }

    /**
     * RelsExt Update Handler.
     *
     * @param elementName
     * @param element
     * @param parser StAX Parser.
     */
    public ItemRelsExtUpdateHandler(final String elementName, final StartElementWithChildElements element,
        final StaxParser parser) {
        this.parser = parser;
        this.props = new TreeMap<String, StartElementWithText>();
        this.props.put(elementName, element);
    }

    public Map<String, StartElementWithText> getProperties() {
        return this.props;
    }

    @Override
    public StartElement startElement(final StartElement element) {
        if (!props.isEmpty()) {
            final String curPath = parser.getCurPath();
            final String theKey = element.getLocalName();
            final String curElementNamespace = element.getNamespace();
            if (curPath.endsWith(this.path + theKey)
                && (props.containsKey(theKey) || props.containsKey(curElementNamespace + theKey))) {

                // update property in an attribute and remove the used value
                StartElementWithText replacementElement = props.get(theKey);
                // quick fix
                if (replacementElement == null) {
                    replacementElement = props.get(curElementNamespace + theKey);
                }

                // process the position value
                int count = 1;
                if (replacementElement.getPosition() != 0) {

                    // not in the map
                    if (this.elementPosition.get(replacementElement.getNamespace() + replacementElement.getLocalName()) == null) {
                        this.elementPosition.put(replacementElement.getNamespace() + replacementElement.getLocalName(),
                            count);
                    }
                    else { // already in the map - update count
                        count =
                            this.elementPosition.get(replacementElement.getNamespace()
                                + replacementElement.getLocalName());

                        ++count;
                        this.elementPosition.put(replacementElement.getNamespace() + replacementElement.getLocalName(),
                            count);
                    }
                }

                if (replacementElement.getPosition() == 0 || count == replacementElement.getPosition()) {

                    if (replacementElement.getAttributeCount() > 0) {
                        final Attribute replacementAttribute = replacementElement.getAttribute(0);
                        final String replacementAttributeValue = replacementAttribute.getValue();
                        int indexOfAttributeToReplace = 0;
                        boolean attributeMatch = false;
                        Attribute attributeToReplace = null;
                        for (int i = 0; i <= element.getAttributeCount(); i++) {
                            final Attribute attribute = element.getAttribute(i);
                            if (attribute.getLocalName().equals(replacementAttribute.getLocalName())
                                && attribute.getNamespace().equals(replacementAttribute.getNamespace())
                                && attribute.getPrefix().equals(replacementAttribute.getPrefix())) {
                                attributeMatch = true;
                                attributeToReplace = attribute;
                                indexOfAttributeToReplace = i;
                                break;
                            }

                        }
                        if (attributeMatch && compareNS(curElementNamespace, replacementElement.getNamespace())) {

                            attributeToReplace.setValue(replacementAttributeValue);
                            element.setAttribute(indexOfAttributeToReplace, attributeToReplace);
                            props.remove(theKey);

                        }
                    }
                }
            }
        }
        return element;
    }

    @Override
    public String characters(final String data, final StartElement element) {

        String newData = data;
        if (!props.isEmpty()) {
            final String curPath = parser.getCurPath();
            final String theKey = element.getLocalName();

            if (curPath.endsWith(this.path + theKey)
                && (props.containsKey(theKey) || props.containsKey(element.getNamespace() + theKey))) {

                // update property and remove the used value
                StartElementWithText replacementElement = props.get(theKey);
                // quick fix
                if (replacementElement == null) {
                    replacementElement = props.get(element.getNamespace() + theKey);
                }

                if (compareNS(element.getNamespace(), replacementElement.getNamespace())) {

                    // check position count
                    int count = 0;
                    if (replacementElement.getPosition() != 0) {
                        count =
                            this.elementPosition.get(replacementElement.getNamespace()
                                + replacementElement.getLocalName());
                    }

                    if (replacementElement.getPosition() == 0 || count == replacementElement.getPosition()) {
                        // element is equal to current element
                        newData = replacementElement.getElementText();
                        props.remove(theKey);
                        if (newData == null) {
                            newData = data;
                        }
                    }
                }

            }
        }
        return newData;
    }

    /**
     * @param path XPath
     * @deprecated
     */
    @Deprecated
    public void setPath(final String path) {
        this.path = path;
    }

    /**
     * Compares Namespaces with the former build in mechanism.
     *
     * @param curElementNamespace
     * @param ns2 Namespace 2.
     * @return true if both Namespaces are equal, false otherwise.
     */
    private static boolean compareNS(final String curElementNamespace, final String ns2) {
        String replacementElementNamespace = ns2;

        // namespaces must not be null for testing
        if (curElementNamespace == null && replacementElementNamespace == null) {
            return true;
        }

        if (curElementNamespace == null || replacementElementNamespace == null) {
            return false;
        }

        if (curElementNamespace.endsWith("/") && !replacementElementNamespace.endsWith("/")) {
            replacementElementNamespace += "/";
        }
        return curElementNamespace.equals(replacementElementNamespace);

    }
}
