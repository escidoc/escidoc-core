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

import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The MetadataHandler.
 *
 * @author Michael Schneider
 */
public class ComponentMetadataHandler extends DefaultHandler {

    private final StaxParser parser;

    private String title;

    private String nameValue;

    public static final String CONTAINER = "/container";

    private final String elementPath;

    private final String mdRecordsPath;

    private final String componentPath;

    private String componentId;

    private List<String> pids;

    private int number;

    private boolean isInside;

    private boolean isRootMetadataElement;

    private String escidocMdRecordNameSpace;

    private final Map<String, Map<String, Map<String, String>>> metadataAttributes =
        new HashMap<String, Map<String, Map<String, String>>>();

    private Map<String, Map<String, String>> componentMdRecords;

    private final Map<String, String> escidocMdNamespacesMap = new HashMap<String, String>();

    // FIXME: work around, mandatory md-record name will be defined in a content
    // model

    /**
     * Instantiate a MetaDataHandler.
     *
     * @param parser        The parser.
     * @param componentPath XPath to component element.
     */
    public ComponentMetadataHandler(final StaxParser parser, final String componentPath) {
        this.parser = parser;
        this.componentPath = componentPath;
        this.mdRecordsPath = componentPath + "/md-records";
        this.elementPath = this.mdRecordsPath + "/md-record";
    }

    /**
     * Sets component ids.
     *
     * @param objids list of objid of Components.
     */
    public void setObjids(final List<String> objids) {
        this.pids = objids;
    }

    /**
     * Handle the start of an element.
     *
     * @param element The element.
     * @return The element.
     * @throws MissingAttributeValueException If a required attribute is missing.
     */
    @Override
    public StartElement startElement(final StartElement element) throws MissingAttributeValueException {

        final String currentPath = parser.getCurPath();

        final String theName = element.getLocalName();

        if (elementPath.equals(currentPath)) {
            final int indexOfName = element.indexOfAttribute(null, "name");
            final Attribute name = element.getAttribute(indexOfName);
            this.nameValue = name.getValue();

            if (nameValue.length() == 0) {
                throw new MissingAttributeValueException("The value of the" + " \"name\" atribute of the element "
                    + theName + " is missing");

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

            if (schemaValue != null) {
                md.put("schema", schemaValue);
            }

            componentMdRecords.put(this.nameValue, md);

            metadataAttributes.put(this.componentId, this.componentMdRecords);

        }
        else if (this.isInside && !this.isRootMetadataElement) {
            this.isRootMetadataElement = true;
            if (this.nameValue.equals(Elements.MANDATORY_MD_RECORD_NAME)) {
                this.escidocMdRecordNameSpace = element.getNamespace();
                this.escidocMdNamespacesMap.put(this.componentId, this.escidocMdRecordNameSpace);
            }

        }
        else if (componentPath.equals(currentPath)) {
            if (this.pids != null) {
                this.componentId = pids.get(this.number);
                this.number++;
            }
        }
        else if (mdRecordsPath.equals(currentPath)) {
            this.componentMdRecords = new HashMap<String, Map<String, String>>();

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
    public EndElement endElement(final EndElement element) {
        if (elementPath.equals(parser.getCurPath())) {
            this.nameValue = null;
            this.isInside = false;
            this.isRootMetadataElement = false;
        }
        else if (componentPath.equals(parser.getCurPath())) {
            this.componentId = null;
            this.componentMdRecords = null;
            this.escidocMdRecordNameSpace = null;
        }

        return element;
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
