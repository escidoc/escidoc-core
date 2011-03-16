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
package de.escidoc.core.om.business.stax.handler;

import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * The MetadataHandler.
 * 
 * @author MSC
 * 
 *
 */
public class MetadataHandler extends DefaultHandler {

    private final StaxParser parser;

    private String title;

    private boolean isInside;

    private boolean isRootMetadataElement;

    private String nameValue;

    public static final String CONTAINER = "/container";

    private String escidocMdRecordNameSpace;

    private String elementPath;

    private String mdRecordsPath;

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MetadataHandler.class);

    private final Map<String, Map<String, String>> metadataAttributes =
        new HashMap<String, Map<String, String>>();

    // FIXME: work around, mandatory md-record name will be defined in a content
    // model
    private static final String MANDATORY_MD_RECORD_NAME = "escidoc";

    private boolean isMandatoryName;

    /**
     * Instantiate a MetaDataHandler.
     * 
     * @param parser
     *            The parser.
     *
     */
    public MetadataHandler(final StaxParser parser) {
        this.parser = parser;
    }

    /**
     * Handle the start of an element.
     * 
     * @param element
     *            The element.
     * @return The element.
     * @throws MissingAttributeValueException
     * @throws MissingAttributeValueException
     *             If a required attribute is missing.
     * @see DefaultHandler#startElement
     *      (de.escidoc.core.common.util.xml.stax.events.StartElement)
     *
     */
    @Override
    public StartElement startElement(final StartElement element)
        throws MissingAttributeValueException {

        final String currentPath = parser.getCurPath();
        mdRecordsPath = "/item/md-records";
        elementPath = "/item/md-records/md-record";
        if (currentPath.startsWith(CONTAINER)) {
            mdRecordsPath = "/container/md-records";
            elementPath = "/container/md-records/md-record";
        }
        final String theName = element.getLocalName();

        if (elementPath.equals(currentPath)) {
            final int indexOfName = element.indexOfAttribute(null, "name");
            final Attribute name = element.getAttribute(indexOfName);
            this.nameValue = name.getValue();

            if (nameValue.length() == 0) {
                throw new MissingAttributeValueException("The value of the"
                    + " \"name\" atribute of the element " + theName
                    + " is missing");

            }
            if (nameValue.equals(MANDATORY_MD_RECORD_NAME)) {
                isMandatoryName = true;
            }
            isInside = true;
            String typeValue = null;
            final int indexOfType = element.indexOfAttribute(null, "md-type");
            if (indexOfType != -1) {
                final Attribute type = element.getAttribute(indexOfType);
                typeValue = type.getValue();
            }
            String schemaValue = null;
            final int indexOfSchema = element.indexOfAttribute(null, "schema");
            if (indexOfSchema != -1) {
                final Attribute schema = element.getAttribute(indexOfSchema);
                schemaValue = schema.getValue();
            }
            final Map<String, String> md = new HashMap<String, String>();
            if (typeValue != null) {
                md.put("type", typeValue);
            }
            else {
                md.put("type", "unknown");
            }
            if (schemaValue != null) {
                md.put("schema", schemaValue);
            }
            else {
                md.put("schema", "unknown");
            }

            metadataAttributes.put(this.nameValue, md);
        }
        else if (isInside && !isRootMetadataElement) {
            isRootMetadataElement = true;
            if (this.nameValue.equals(MANDATORY_MD_RECORD_NAME)) {
                this.escidocMdRecordNameSpace = element.getNamespace();
            }

        }

        return element;
    }

    /**
     * Handle the end of an element.
     * 
     * @param element
     *            The element.
     * @throws MissingMdRecordException
     *             Thrown if a mentatory meta data datastream is missing
     *             (refernced by name).
     * @return The element.
     * @see DefaultHandler#endElement
     *      (de.escidoc.core.common.util.xml.stax.events.EndElement)
     *
     */
    @Override
    public EndElement endElement(final EndElement element)
        throws MissingMdRecordException {
        if (elementPath.equals(parser.getCurPath())) {

            isInside = false;
            isRootMetadataElement = false;
            nameValue = null;
        }
        else if (mdRecordsPath.equals(parser.getCurPath())
            && !isMandatoryName) {
            throw new MissingMdRecordException("Mandatory md-record with a name "
                    + MANDATORY_MD_RECORD_NAME + " is missing.");
        }
        return element;
    }

    /**
     * Handle the character section of an element.
     * 
     * @param s
     *            The contents of the character section.
     * @param element
     *            The element.
     * @return The character section.
     * @see DefaultHandler#characters
     *      (java.lang.String,
     *      de.escidoc.core.common.util.xml.stax.events.StartElement)
     *
     */
    @Override
    public String characters(final String s, final StartElement element) {

        return s;
    }

    /**
     * @return Returns the title.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * @return Returns metadata attributes.
     */
    public Map<String, Map<String, String>> getMetadataAttributes() {
        return this.metadataAttributes;
    }

    /**
     * Retrieves a namespace uri of a child element of "md-record" element,
     * whose attribute "name" set to "escidoc".
     * 
     * @return namespace
     */
    public String getEscidocMdRecordNameSpace() {
        return this.escidocMdRecordNameSpace;
    }
}
