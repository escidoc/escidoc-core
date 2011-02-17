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
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import javax.naming.directory.NoSuchAttributeException;
import java.util.HashMap;
import java.util.Map;

public class MdRecordsUpdateHandler extends DefaultHandler {

    private StaxParser parser = null;

    private String mdRecordsPath = null;

    private String name = null;

    private String escidocMdRecordNameSpace = null;

    private boolean isInside = false;

    private boolean isRootMetadataElement = false;

    private final Map<String, Map<String, String>> metadataAttributes =
        new HashMap<String, Map<String, String>>();

    private static final AppLogger log =
        new AppLogger(MetadataHandler.class.getName());

    private boolean isMandatoryName = false;
    
    private boolean origin = false;

    /**
     * 
     * @param itemId
     * @param mdRecordsPath
     * @param parser
     */
    public MdRecordsUpdateHandler(final String mdRecordsPath,
        final StaxParser parser) {

        this.mdRecordsPath = mdRecordsPath;
        this.parser = parser;
    }
    
    public MdRecordsUpdateHandler(final String mdRecordsPath,
        final StaxParser parser, final boolean origin) {

        this.mdRecordsPath = mdRecordsPath;
        this.parser = parser;
        this.origin = origin;
    }

    public StartElement startElement(StartElement element)
        throws MissingAttributeValueException {
        String curPath = parser.getCurPath();
        String theName = element.getLocalName();
        int indexInherited = element.indexOfAttribute(null, "inherited");
        if (curPath.startsWith(mdRecordsPath) || mdRecordsPath.length() == 0) {

            if (curPath.equals(mdRecordsPath + "/md-record")
                && (indexInherited < 0)) {
                // the entire md-record element is stored in fedora, so adjust
                // all values
                isInside = true;
                // get name of md-record

                // String onlyName = "escidoc";
                try {
                    name = element.getAttribute(null, "name").getValue();
                    if (name.length() == 0) {
                        log.error("the value of"
                            + " \"name\" atribute of the element " + theName
                            + " is missing");
                        throw new MissingAttributeValueException(
                            "the value of the"
                                + " \"name\" atribute of the element "
                                + theName + " is missing");

                    }
                    if (name.equals(Elements.MANDATORY_MD_RECORD_NAME)) {
                        isMandatoryName = true;
                    }
                }
                catch (NoSuchAttributeException e) {
                    // LAX
                    // throw new InvalidContentException(
                    // "md-record element has no attribute 'name'.");
                    // element.addAttribute(new Attribute("name", null, null,
                    // onlyName));
                }

                String typeValue = null;
                int indexOfType = element.indexOfAttribute(null, "md-type");
                if (indexOfType != -1) {
                    Attribute type = element.getAttribute(indexOfType);
                    typeValue = type.getValue();
                }
                String schemaValue = null;
                int indexOfSchema = element.indexOfAttribute(null, "schema");
                if (indexOfSchema != -1) {
                    Attribute schema = element.getAttribute(indexOfSchema);
                    schemaValue = schema.getValue();
                }
                HashMap<String, String> md = new HashMap<String, String>();
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
                metadataAttributes.put(name, md);
            } else if (isInside && !isRootMetadataElement) {
                isRootMetadataElement = true;
                if (this.name.equals("escidoc")) {
                    this.escidocMdRecordNameSpace = element.getNamespace();
                }

            }
        }
        return element;
    }

    /**
     * Handle the end of an element.
     * 
     * @param element
     *            The element.
     * @return The element.
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#endElement
     *      (de.escidoc.core.common.util.xml.stax.events.EndElement)
     * @om
     */
    public EndElement endElement(final EndElement element)
        throws MissingMdRecordException {
        if (parser.getCurPath().equals(mdRecordsPath + "/md-record")) {
            isInside = false;
            isRootMetadataElement = false;
            this.name = null;
        }
        else if ((mdRecordsPath.equals(parser.getCurPath()))
            && (!isMandatoryName && !origin)) {
            String message =
                "Mandatory md-record with a name "
                    + Elements.MANDATORY_MD_RECORD_NAME + " is missing.";
            log.error(message);
            throw new MissingMdRecordException(message);
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
     * Retrieves a namespace uri of a child element of "md-record" element,
     * whose attribute "name" set to "escidoc".
     * 
     * @return name space of md-record
     */
    public String getEscidocMdRecordNameSpace() {
        return this.escidocMdRecordNameSpace;
    }
}
