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

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

public class MdRecordsUpdateHandler extends DefaultHandler {

    private final StaxParser parser;

    private final String mdRecordsPath;

    private String name;

    private String escidocMdRecordNameSpace;

    private boolean isInside;

    private boolean isRootMetadataElement;

    private final Map<String, Map<String, String>> metadataAttributes = new HashMap<String, Map<String, String>>();

    private boolean isMandatoryName;

    private boolean origin;

    /**
     *
     * @param mdRecordsPath
     * @param parser
     */
    public MdRecordsUpdateHandler(final String mdRecordsPath, final StaxParser parser) {

        this.mdRecordsPath = mdRecordsPath;
        this.parser = parser;
    }

    public MdRecordsUpdateHandler(final String mdRecordsPath, final StaxParser parser, final boolean origin) {

        this.mdRecordsPath = mdRecordsPath;
        this.parser = parser;
        this.origin = origin;
    }

    @Override
    public StartElement startElement(final StartElement element) throws MissingAttributeValueException {
        final String curPath = parser.getCurPath();
        final String theName = element.getLocalName();
        final int indexInherited = element.indexOfAttribute(null, "inherited");
        if (curPath.startsWith(this.mdRecordsPath) || mdRecordsPath.length() == 0) {

            if (curPath.equals(this.mdRecordsPath + "/md-record") && indexInherited < 0) {
                // the entire md-record element is stored in fedora, so adjust
                // all values
                this.isInside = true;
                // get name of md-record

                final int index = element.indexOfAttribute(null, "name");
                if (index > -1) {
                    this.name = element.getAttribute(index).getValue();
                    if (name.length() == 0) {
                        throw new MissingAttributeValueException("the value of the"
                            + " \"name\" atribute of the element " + theName + " is empty.");
                    }
                    if (Elements.MANDATORY_MD_RECORD_NAME.equals(name)) {
                        this.isMandatoryName = true;
                    }
                }

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
                    md.put("type", Constants.UNKNOWN);
                }

                if (schemaValue != null) {
                    md.put("schema", schemaValue);
                }
                else {
                    md.put("schema", Constants.UNKNOWN);
                }
                metadataAttributes.put(this.name, md);
            }
            else if (this.isInside && !this.isRootMetadataElement) {
                this.isRootMetadataElement = true;
                if ("escidoc".equals(this.name)) {
                    this.escidocMdRecordNameSpace = element.getNamespace();
                }

            }
        }
        return element;
    }

    /**
     * Handle the end of an element.
     *
     * @param element The element.
     * @return The element.
     */
    @Override
    public EndElement endElement(final EndElement element) throws MissingMdRecordException {
        if (parser.getCurPath().equals(this.mdRecordsPath + "/md-record")) {
            this.isInside = false;
            this.isRootMetadataElement = false;
            this.name = null;
        }
        else if (mdRecordsPath.equals(parser.getCurPath()) && !this.isMandatoryName && !this.origin) {
            throw new MissingMdRecordException("Mandatory md-record with a name " + Elements.MANDATORY_MD_RECORD_NAME
                + " is missing.");
        }
        return element;
    }

    /**
     * Get attributes of md-record elements.
     *
     * @return Map with map of md-record attributes.
     */
    public Map<String, Map<String, String>> getMetadataAttributes() {
        return this.metadataAttributes;
    }

    /**
     * Retrieves a namespace uri of a child element of "md-record" element, whose attribute "name" set to "escidoc".
     *
     * @return name space of md-record
     */
    public String getEscidocMdRecordNameSpace() {
        return this.escidocMdRecordNameSpace;
    }
}
