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

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.directory.NoSuchAttributeException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This Handler is invoked to obtain attributes of md-records of Components.
 * <p/>
 * Attention: It seems that this handler obtains also values for metadata of new Components (without objid), but these
 * values are dropped later. Even if the parser runs through all component/md-records are new Components handled in a
 * separate process.
 * <p/>
 * FIXME: convoluted Maps are not a valid data structure.
 *
 * @author ??
 */
public class ComponentMdRecordsUpdateHandler extends DefaultHandler {

    private final StaxParser parser;

    private final String componentPath;

    private String name;

    private String componentId;

    private String escidocMdRecordNameSpace;

    private final Map<String, String> escidocMdNamespacesMap = new HashMap<String, String>();

    private boolean isInside;

    private boolean isRootMetadataElement;

    private static final Pattern PATTERN_OBJID_IN_HREF = Pattern.compile(".*\\/([^\"\\/]*)");

    private Map<String, Map<String, String>> componentMdRecords;

    private final Map<String, Map<String, Map<String, String>>> metadataAttributes =
        new HashMap<String, Map<String, Map<String, String>>>();

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentMetadataHandler.class);

    /**
     * ComponentMdRecordsUpdateHandler.
     *
     * @param componentPath XPath to Components.
     * @param parser        StAX parser.
     */
    public ComponentMdRecordsUpdateHandler(final String componentPath, final StaxParser parser) {

        this.componentPath = componentPath;
        this.parser = parser;
    }

    @Override
    public StartElement startElement(final StartElement element) throws MissingAttributeValueException {
        final String curPath = parser.getCurPath();
        final String theName = element.getLocalName();
        if (curPath.startsWith(this.componentPath) || componentPath.length() == 0) {

            if (curPath.equals(this.componentPath)) {

                final int indexOfObjid = element.indexOfAttribute(null, Elements.ATTRIBUTE_XLINK_OBJID);
                if (indexOfObjid != -1) {
                    final String value = element.getAttribute(indexOfObjid).getValue();
                    if (value != null && value.length() > 0) {
                        this.componentId = value;
                    }
                }
                final int indexOfHref = element.indexOfAttribute(Constants.XLINK_URI, Elements.ATTRIBUTE_XLINK_HREF);
                if (indexOfHref != -1) {
                    final String value = element.getAttribute(indexOfHref).getValue();
                    if (value != null && value.length() > 0) {

                        final Matcher m1 = PATTERN_OBJID_IN_HREF.matcher(value);
                        if (m1.find()) {
                            this.componentId = m1.group(1);
                        }
                    }
                }
            }
            else if (this.isInside && !this.isRootMetadataElement) {
                this.isRootMetadataElement = true;
                if (this.name.equals(Elements.MANDATORY_MD_RECORD_NAME)) {
                    this.escidocMdRecordNameSpace = element.getNamespace();
                    this.escidocMdNamespacesMap.put(this.componentId, this.escidocMdRecordNameSpace);
                }
            }
            else if (curPath.equals(this.componentPath + "/md-records")) {
                this.componentMdRecords =
                    this.metadataAttributes.containsKey(this.componentId) ? this.metadataAttributes
                        .get(this.componentId) : new HashMap<String, Map<String, String>>();
            }
            else if (curPath.equals(this.componentPath + "/md-records/md-record")) {

                try {
                    this.name = element.getAttribute(null, "name").getValue();
                    if (name.length() == 0) {
                        throw new MissingAttributeValueException("The value of the"
                            + " \"name\" atribute of the element " + theName + " is missing");

                    }
                }
                catch (final NoSuchAttributeException e) {
                    if (LOGGER.isWarnEnabled()) {
                        LOGGER.warn("Error accessing attribute.");
                    }
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Error accessing attribute.", e);
                    }
                }
                this.isInside = true;

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

                componentMdRecords.put(this.name, md);
                // keep in mind, that new components are ignored at this point.
                if (this.componentId != null) {
                    metadataAttributes.put(this.componentId, this.componentMdRecords);
                }
            }
        }
        return element;
    }

    /**
     * Handle XMl end element.
     *
     * @param element The element.
     * @return The element.
     */
    @Override
    public EndElement endElement(final EndElement element) {
        if (parser.getCurPath().equals(this.componentPath + "/md-records/md-record")) {
            this.name = null;
            this.isInside = false;
            this.isRootMetadataElement = false;
        }
        else if (componentPath.equals(parser.getCurPath())) {
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
    public Map<String, Map<String, Map<String, String>>> getMetadataAttributes() {
        return this.metadataAttributes;
    }

    /**
     * @return Returns a map with a namespaces.
     */
    public Map<String, String> getNamespacesMap() {
        return this.escidocMdNamespacesMap;
    }
}
