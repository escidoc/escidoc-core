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
 * @author MSC
 * 
 * @om
 */
public class ComponentMetadataHandler extends DefaultHandler {

    private final StaxParser parser;

    private String title;

    private String nameValue = null;

    public static final String CONTAINER = "/container";

    private String elementPath = null;

    private String mdRecordsPath = null;

    private String componentPath = null;

    private String componentId = null;

    private List<String> pids = null;

    private int number = 0;

    private boolean isInside = false;

    private boolean isRootMetadataElement = false;

    private String escidocMdRecordNameSpace = null;

    private final Map<String, Map<String, Map<String, String>>> metadataAttributes =
        new HashMap<String, Map<String, Map<String, String>>>();

    private Map<String, Map<String, String>> componentMdRecords = null;

    private final Map<String, String> escidocMdNamespacesMap =
        new HashMap<String, String>();

    // FIXME: work around, mandatory md-record name will be defined in a content
    // model

    /**
     * Instantiate a MetaDataHandler.
     * 
     * @param parser
     *            The parser.
     * @param componentPath
     *            XPath to component element.
     * @om
     */
    public ComponentMetadataHandler(final StaxParser parser,
        final String componentPath) {
        this.parser = parser;
        this.componentPath = componentPath;
        this.mdRecordsPath = componentPath + "/md-records";
        this.elementPath = mdRecordsPath + "/md-record";
    }

    /**
     * Sets component ids.
     * 
     * @param objids
     *            list of objid of Components.
     */
    public void setObjids(final List<String> objids) {
        this.pids = objids;
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
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#startElement
     *      (de.escidoc.core.common.util.xml.stax.events.StartElement)
     * @om
     */
    @Override
    public StartElement startElement(final StartElement element)
        throws MissingAttributeValueException {

        String currentPath = parser.getCurPath();

        String theName = element.getLocalName();

        if (elementPath.equals(currentPath)) {
            int indexOfName = element.indexOfAttribute(null, "name");
            Attribute name = element.getAttribute(indexOfName);
            this.nameValue = name.getValue();

            if (nameValue.equals("")) {
                final String errorMsg =
                    "the value of the" + " \"name\" atribute of the element "
                        + theName + " is missing";
                throw new MissingAttributeValueException(errorMsg);

            }

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

            if (schemaValue != null) {
                md.put("schema", schemaValue);
            }

            componentMdRecords.put(this.nameValue, md);

            metadataAttributes.put(this.componentId, this.componentMdRecords);


        }
        else if (isInside && !isRootMetadataElement) {
            isRootMetadataElement = true;
            if (this.nameValue.equals(Elements.MANDATORY_MD_RECORD_NAME)) {
                this.escidocMdRecordNameSpace = element.getNamespace();
                this.escidocMdNamespacesMap.put(componentId,
                    this.escidocMdRecordNameSpace);
            }

        }
        else if (componentPath.equals(currentPath)) {
            if (pids != null) {
                componentId = pids.get(number);
                number++;
            }
        }
        else if (mdRecordsPath.equals(currentPath)) {
            this.componentMdRecords =
                new HashMap<String, Map<String, String>>();

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
    @Override
    public EndElement endElement(final EndElement element) {
        if (elementPath.equals(parser.getCurPath())) {
            this.nameValue = null;
            isInside = false;
            isRootMetadataElement = false;
        }
        else if (componentPath.equals(parser.getCurPath())) {
            componentId = null;
            this.componentMdRecords = null;
            this.escidocMdRecordNameSpace = null;
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
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#characters
     *      (java.lang.String,
     *      de.escidoc.core.common.util.xml.stax.events.StartElement)
     * @om
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
