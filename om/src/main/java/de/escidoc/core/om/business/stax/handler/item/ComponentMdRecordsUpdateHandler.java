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
package de.escidoc.core.om.business.stax.handler.item;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.directory.NoSuchAttributeException;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

/**
 * This Handler is invoked to obtain attributes of md-records of Components.
 * 
 * Attention: It seems that this handler obtains also values for metadata of new
 * Components (without objid), but these values are dropped later. Even if the
 * parser runs through all component/md-records are new Components handled in a
 * separate process.
 * 
 * FIXME: convoluted Maps are not a valid data structure.
 * 
 * @author ??
 * 
 */
public class ComponentMdRecordsUpdateHandler extends DefaultHandler {

    private StaxParser parser = null;

    private String componentPath = null;

    private String name = null;

    private String componentId = null;

    private String escidocMdRecordNameSpace = null;

    private HashMap<String, String> escidocMdNamespacesMap =
        new HashMap<String, String>();

    private boolean isInside = false;

    private boolean isRootMetadataElement = false;

    private static final Pattern PATTERN_OBJID_IN_HREF =
        Pattern.compile(".*\\/([^\"\\/]*)");

    private HashMap<String, HashMap<String, String>> componentMdRecords = null;

    private HashMap<String, HashMap<String, HashMap<String, String>>> metadataAttributes =
        new HashMap<String, HashMap<String, HashMap<String, String>>>();

    private static AppLogger log =
        new AppLogger(ComponentMetadataHandler.class.getName());

    /**
     * ComponentMdRecordsUpdateHandler.
     * 
     * @param componentPath
     *            XPath to Components.
     * @param parser
     *            StAX parser.
     */
    public ComponentMdRecordsUpdateHandler(final String componentPath,
        final StaxParser parser) {

        this.componentPath = componentPath;
        this.parser = parser;
    }

    @Override
    public StartElement startElement(final StartElement element)
        throws MissingAttributeValueException {
        String curPath = parser.getCurPath();
        String theName = element.getLocalName();
        if (curPath.startsWith(componentPath) || componentPath.equals("")) {

            if (curPath.equals(componentPath)) {

                int indexOfObjid =
                    element.indexOfAttribute(null,
                        Elements.ATTRIBUTE_XLINK_OBJID);
                if (indexOfObjid != -1) {
                    String value =
                        element.getAttribute(indexOfObjid).getValue();
                    if ((value != null) && (value.length() > 0)) {
                        componentId = value;
                    }
                }
                int indexOfHref =
                    element.indexOfAttribute(Constants.XLINK_URI,
                        Elements.ATTRIBUTE_XLINK_HREF);
                if (indexOfHref != -1) {
                    String value = element.getAttribute(indexOfHref).getValue();
                    if ((value != null) && (value.length() > 0)) {

                        Matcher m1 = PATTERN_OBJID_IN_HREF.matcher(value);
                        if (m1.find()) {
                            componentId = m1.group(1);
                        }
                    }
                }
            }
            else if (isInside && !isRootMetadataElement) {
                isRootMetadataElement = true;
                if (this.name.equals(Elements.MANDATORY_MD_RECORD_NAME)) {
                    this.escidocMdRecordNameSpace = element.getNamespace();
                    this.escidocMdNamespacesMap.put(this.componentId,
                        this.escidocMdRecordNameSpace);
                }
            }
            else if (curPath.equals(componentPath + "/md-records")) {
                if (this.metadataAttributes.containsKey(componentId)) {
                    this.componentMdRecords =
                        this.metadataAttributes.get(componentId);
                }
                else {
                    this.componentMdRecords =
                        new HashMap<String, HashMap<String, String>>();
                }
            }
            else if (curPath.equals(componentPath + "/md-records/md-record")) {

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
                }
                catch (NoSuchAttributeException e) {
                    log.debug(e);
                }
                // if (name.equals(Elements.MANDATORY_MD_RECORD_NAME)) {
                // isMandatoryName = true;
                // }
                isInside = true;

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

                componentMdRecords.put(this.name, md);
                // keep in mind, that new components are ignored at this point.
                if (componentId != null) {
                    metadataAttributes.put(this.componentId,
                        this.componentMdRecords);
                }
            }
        }
        return element;
    }

    /**
     * Handle XMl end element.
     * 
     * @param element
     *            The element.
     * @return The element.
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#endElement
     *      (de.escidoc.core.common.util.xml.stax.events.EndElement)
     */
    @Override
    public EndElement endElement(final EndElement element) {
        if (parser.getCurPath().equals(componentPath + "/md-records/md-record")) {
            this.name = null;
            isInside = false;
            isRootMetadataElement = false;
        }
        else if (componentPath.equals(parser.getCurPath())) {
            // if (!isMandatoryName) {
            // String message =
            // "Mandatory md-record with a name "
            // + Elements.MANDATORY_MD_RECORD_NAME + " is missing.";
            // log.error(message);
            // throw new MissingMdRecordException(message);
            // }

            this.escidocMdRecordNameSpace = null;
            this.componentId = null;
            this.componentMdRecords = null;
        }
        return element;
    }

    /**
     * Get Attributes of md-record element.
     * 
     * @return Attributes of md-record element.
     */
    public HashMap<String, HashMap<String, HashMap<String, String>>> getMetadataAttributes() {
        return metadataAttributes;
    }

    /**
     * @return Returns a map with a namespaces.
     */
    public Map<String, String> getNamespacesMap() {
        return this.escidocMdNamespacesMap;
    }
}
