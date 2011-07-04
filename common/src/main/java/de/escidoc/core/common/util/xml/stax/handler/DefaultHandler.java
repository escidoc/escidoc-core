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

package de.escidoc.core.common.util.xml.stax.handler;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;

/**
 * Default stax handler. This should be the base class of all stax handlers.
 *
 * @author Torsten Tetteroo
 */
public abstract class DefaultHandler {

    /**
     * Mode indicating an attribute/element is discarded, i.e. it may be set but will be ignored.
     */
    public static final char DISCARDED = (char) 4;

    /**
     * Mode indicating an attribute/element is optional.
     */
    public static final char OPTIONAL = (char) 2;

    /**
     * Mode indicating an attribute/element is mandatory.
     */
    public static final char MANDATORY = (char) 1;

    /**
     * Mode indicating an attribute/element is not allowed to be in the Xml data.
     */
    public static final char FORBIDDEN = (char) 0;

    /**
     * Error message in case of not found attribute that is mandatory in schema. As this should not occur in a Stax
     * handler (parsing is executed after schema validation), this should be reported as an internal system error.
     */
    private static final String MSG_MANDATORY_ATTRIBUTE_NOT_FOUND = "Mandatory attribute not found.";

    private boolean ready;

    /**
     * This method handles a start element.<br> The default implementation just returns the provided object.
     *
     * @param element The {@link StartElement} to handle.
     * @return Returns a start element that shall be handled by further handlers in the chain, or {@code null} to
     *         stop the chain.
     * @throws Exception Thrown if anything fails. This depends on the implementation of the concrete class.
     */
    public StartElement startElement(final StartElement element) throws Exception {
        return element;
    }

    /**
     * This method handles an end element.<br> The default implementation just returns the provided object.
     *
     * @param element The {@link StartElement} to handle.
     * @return Returns an end element that shall be handled by further handlers in the chain, or {@code null} to
     *         stop the chain.
     * @throws Exception Thrown if anything fails. This depends on the implementation of the concrete class.
     */
    public EndElement endElement(final EndElement element) throws Exception {
        return element;
    }

    /**
     * This method handles the character content of an element.
     *
     * @param data    The character content of the element.
     * @param element The current {@link StartElement} to that the content belongs.
     * @return Returns a {@link String} that shall be handled by further handlers in the chain, or {@code null} to
     *         stop the chain.
     * @throws Exception Thrown if anything fails. This depends on the implementation of the concrete class.
     */
    public String characters(final String data, final StartElement element) throws Exception {
        return data;
    }

    /**
     * Creates an exception for the situation that an attribute that is mandatory in the schema cannot be found during
     * xml parsing, and this situation should not occur has the schema validation is done before the parsing.
     *
     * @param startElement  The {@link StartElement} that does not contain the expected attribute.
     * @param namespaceUri  The namespace of the expected attribute.
     * @param attributeName The name of the expected attribute
     * @param e
     * @return Returns a {@link SystemException} as this situation can occur due to internal errors, only.
     */
    protected static SystemException createMandatoryAttributeNotFoundException(
        final StartElement startElement, final String namespaceUri, final String attributeName, final Exception e) {

        return new WebserverSystemException(StringUtility.format(MSG_MANDATORY_ATTRIBUTE_NOT_FOUND, startElement
            .getPath(), namespaceUri, attributeName), e);
    }

    /**
     * @return Returns {@code true} if the ready flag is not set.
     */
    protected boolean isNotReady() {
        return !this.ready;
    }

    /**
     * Marks this handler has finished by setting the ready flag to {@code true}.
     */
    protected void setReady() {
        this.ready = true;
    }

    /**
     * @param element       XML StartElement
     * @param namespace     Namespace (set null if undef).
     * @param attributeName The name of the Attribute.
     * @return Value of Attribute or null.
     * @deprecated Use StartElement.getAttributeValue(namespace, attributeName) instead. Which does not return null but
     *             throws an exception. Get the value of an Attribute.
     */
    @Deprecated
    protected static String getAttributeValue(
        final StartElement element, final String namespace, final String attributeName) {

        String typeValue = null;
        final int indexOfType = element.indexOfAttribute(namespace, attributeName);
        if (indexOfType != -1) {
            final Attribute type = element.getAttribute(indexOfType);
            typeValue = type.getValue();
        }

        return typeValue;
    }

}
