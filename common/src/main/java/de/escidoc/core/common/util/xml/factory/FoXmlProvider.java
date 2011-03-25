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

package de.escidoc.core.common.util.xml.factory;

import de.escidoc.core.common.util.xml.Elements;

/**
 * A factory to create Foxml documents.
 * 
 * @author Michael Schneider
 * 
 */
public class FoXmlProvider extends XmlTemplateProvider {

    public static final String CONTROL_GROUP_M = "M";

    public static final String CONTROL_GROUP_X = "X";

    public static final String CONTROL_GROUP_E = "E";

    public static final String CONTROL_GROUP_R = "R";

    public static final String MIME_TYPE_TEXT_XML = "text/xml";

    public static final String MIME_TYPE_APPLICATION_OCTET_STREAM =
        "application/octet-stream";

    public static final String DATASTREAM_CONTENT = "content";

    public static final String DATASTREAM_STORAGE_ATTRIBUTE = "storage";

    public static final String DATASTREAM_CONTENT_TYPE_SPECIFIC =
        Elements.ELEMENT_CONTENT_MODEL_SPECIFIC;

    public static final String DATASTREAM_VERSION_HISTORY = "version-history";

    public static final String DATASTREAM_MD_RECORDS = "md-records";

    public static final String DATASTREAM_UPLOAD_URL = "uploadUrl";

    public static final String CONTENT_LOCATION_TYPE_URL = "URL";

    public static final String FEDORA_COMPONENT_LABEL = "component";

    public static final String FEDORA_ITEM_LABEL = "Item";

    public static final String FOXML_DIGITAL_OBJECT_RESOURCE_NAME =
        "digitalObject";

    public static final String FOXML_DIGITAL_OBJECT_PATH = "/foxml";

    public static final String FOXML_DIGITAL_OBJECT_PROPERTIES_RESOURCE_NAME =
        "digitalObjectProperties";

    public static final String FOXML_DIGITAL_OBJECT_PROPERTIES_PATH =
        FOXML_DIGITAL_OBJECT_PATH;

    public static final String FOXML_DATASTREAM_RESOURCE_NAME = "datastream";

    public static final String FOXML_DATASTREAM_INLINE_XML_CONTENT_RESOURCE_NAME =
        "datastreamInlineXmlContent";

    public static final String FOXML_DATASTREAM_PATH =
        FOXML_DIGITAL_OBJECT_PATH;

    public static final String FOXML_CONTENT_LOCATION_RESOURCE_NAME =
        "contentLocation";

    public static final String FOXML_CONTENT_LOCATION_PATH =
        FOXML_DIGITAL_OBJECT_PATH;

    public static final String FOXML_ITEM_RELS_EXT_RESOURCE_NAME =
        "itemRelsExt";

    static final String FOXML_ITEM_RELS_EXT_CONTENT_RELATION_RESOURCE_NAME =
        "contentRelation";

    public static final String FOXML_ITEM_RELS_EXT_PATH =
        FOXML_DIGITAL_OBJECT_PATH;

    public static final String FOXML_ITEM_RELS_EXT_CONTENT_RELATION_PATH =
        FOXML_ITEM_RELS_EXT_PATH;

    public static final String FOXML_ITEM_RELS_EXT_RELATED_COMPONENT_RESOURCE_NAME =
        "relatedComponent";

    public static final String FOXML_ITEM_RELS_EXT_RELATED_COMPONENT_PATH =
        FOXML_ITEM_RELS_EXT_PATH;

    public static final String FOXML_WOV_RESOURCE_NAME = "wov";

    public static final String FOXML_EVENT_RESOURCE_NAME = "premis-event";

    public static final String FOXML_VERSION_ENTRY_RESOURCE_NAME =
        "wov-version";

    public static final String FOXML_WOV_PATH = FOXML_DIGITAL_OBJECT_PATH;

    public static final String FOXML_EVENT_PATH = FOXML_DIGITAL_OBJECT_PATH;

    public static final String FOXML_VERSION_ENTRY_PATH =
        FOXML_DIGITAL_OBJECT_PATH;

    public static final String FOXML_COMPONENT_RELS_EXT_RESOURCE_NAME =
        "componentRelsExt";

    public static final String FOXML_COMPONENT_RELS_EXT_DESCRIPTION_RESOURCE_NAME =
        "componentRelsExtDescription";

    public static final String FOXML_COMPONENT_RELS_EXT_LOCATOR_URL_RESOURCE_NAME =
        "componentRelsExtLocatorUrl";

    public static final String FOXML_COMPONENT_RELS_EXT_MIME_TYPE_RESOURCE_NAME =
        "componentRelsExtMimeType";

    public static final String FOXML_COMPONENT_RELS_EXT_PATH =
        FOXML_DIGITAL_OBJECT_PATH;
}
