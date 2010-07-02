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

import java.util.HashMap;
import java.util.Map;

import javax.naming.directory.NoSuchAttributeException;

import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

public class MdRecordsUpdateHandler extends DefaultHandler {

    private StaxParser parser = null;

    private String mdRecordsPath = null;

    private String name = null;

    private String escidocMdRecordNameSpace = null;

    private boolean isInside = false;

    private boolean isRootMetadataElement = false;

    private Map<String, Map<String, String>> metadataAttributes =
        new HashMap<String, Map<String, String>>();

    private static AppLogger log =
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
        if (curPath.startsWith(mdRecordsPath) || mdRecordsPath.equals("")) {

            if (curPath.equals(mdRecordsPath)) {
                // check href
                // String hrefStr = "/ir/" + objectType + "/" + itemId
                // + "/md-records";
                // try {
                // String href =
                // element.getAttribute(de.escidoc.core.common.business.Constants.XLINK_URI,
                // "href").getValue();
                // // LAX adjust href
                // if (!href.equals(hrefStr)) {
                // throw new ReadonlyAttributeViolationException(
                // objectType + " has invalid href.");
                // }
                // }
                // catch (NoSuchAttributeException e) {
                // // LAX
                // }
                // check type
                // check title
            }
            else if (curPath.equals(mdRecordsPath + "/md-record")
                && (indexInherited < 0)) {
                // the entire md-record element is stored in fedora, so adjust
                // all values
                isInside = true;
                // get name of md-record

                // String onlyName = "escidoc";
                try {
                    name = element.getAttribute(null, "name").getValue();
                    if (name.equals("")) {
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
                    // only "escidoc" for now
                    // checked by XMLSchema
                    // if (!onlyName.equals(name)) {
                    // // LAX
                    // // throw new InvalidContentException(
                    // // "Name of md-record must be 'escidoc'.");
                    // element.getAttribute(null, "name").setValue(onlyName);
                    // }
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
                // check href
                // String hrefStr = "/ir/" + objectType + "/" + itemId
                // + "/md-records/md-record/" + name;
                // try {
                // Attribute href =
                // element.getAttribute(de.escidoc.core.common.business.Constants.XLINK_URI,
                // "href");
                // // LAX adjust href
                // if (!href.getValue().equals(hrefStr)) {
                // href.setValue(hrefStr);
                // }
                // }
                // catch (NoSuchAttributeException e) {
                // // LAX adjust href
                // element.addAttribute(new Attribute("href",
                // de.escidoc.core.common.business.Constants.XLINK_URI,
                // de.escidoc.core.common.business.Constants.XLINK_PREFIX,
                // hrefStr));
                // }

                // check title
                // String titleStr = "";
                // try {
                // Attribute title =
                // element.getAttribute(de.escidoc.core.common.business.Constants.XLINK_URI,
                // "title");
                // // LAX adjust title
                // if (title.getValue().length() == 0) {
                // title.setValue(titleStr);
                // }
                // }
                // catch (NoSuchAttributeException e) {
                // // LAX adjust title
                // element.addAttribute(new Attribute("title",
                // de.escidoc.core.common.business.Constants.XLINK_URI,
                // de.escidoc.core.common.business.Constants.XLINK_PREFIX,
                // titleStr));
                // }

                // check type
                // String typeStr = "simple";
                // try {
                // Attribute type =
                // element.getAttribute(de.escidoc.core.common.business.Constants.XLINK_URI,
                // "type");
                // // LAX adjust type
                // if (!type.getValue().equals(typeStr)) {
                // type.setValue(typeStr);
                // }
                // }
                // catch (NoSuchAttributeException e) {
                // // LAX adjust type
                // element.addAttribute(new Attribute("type",
                // de.escidoc.core.common.business.Constants.XLINK_URI,
                // de.escidoc.core.common.business.Constants.XLINK_PREFIX,
                // typeStr));
                // }

                // remove attribute last-modification-date
                // int modDateIndex = element.indexOfAttribute(null,
                // "last-modification-date");
                // if (modDateIndex >= 0) {
                // element.removeAttribute(modDateIndex);
                // }
                // // remove attribute xml:base
                // int xmlBaseIndex = element.indexOfAttribute(
                // de.escidoc.core.common.business.Constants.XML_NSURI, "base");
                // if (xmlBaseIndex >= 0) {
                // element.removeAttribute(xmlBaseIndex);
                // }
            }
            else if (isInside && !isRootMetadataElement) {
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
        else if (mdRecordsPath.equals(parser.getCurPath())) {
            if (!isMandatoryName && !origin) {
                String message =
                    "Mandatory md-record with a name "
                        + Elements.MANDATORY_MD_RECORD_NAME + " is missing.";
                log.error(message);
                throw new MissingMdRecordException(message);
            }
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
