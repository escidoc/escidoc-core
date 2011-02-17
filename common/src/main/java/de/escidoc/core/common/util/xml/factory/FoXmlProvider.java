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
package de.escidoc.core.common.util.xml.factory;

import de.escidoc.core.common.util.xml.Elements;

/**
 * A factory to create Foxml documents.
 * 
 * @author MSC
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

    private static final String FOXML_DIGITAL_OBJECT_PATH = "/foxml";

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

    private static final String FOXML_ITEM_RELS_EXT_PATH =
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

    // private static FoXmlProvider provider = null;
    //
    // public String getItemFoXml(final Map<String, String> values)
    // throws WebserverSystemException {
    //
    // Map<String, String> propertiesValues = new HashMap<String, String>();
    // propertiesValues.put(VAR_LABEL, FEDORA_ITEM_LABEL + " " +
    // values.get(XmlTemplateProvider.VAR_FEDORA_PID));
    // values.put(VAR_PROPERTIES,
    // getDigitalObjectPropertiesFoXml(propertiesValues));
    // return getXml(FOXML_DIGITAL_OBJECT_RESOURCE_NAME,
    // FOXML_DIGITAL_OBJECT_PATH, values);
    // }

    // public String getComponentFoXml(final Map<String, String> values)
    // throws WebserverSystemException {
    //
    // Map<String, String> propertiesValues = new HashMap<String, String>();
    // propertiesValues.put(VAR_LABEL, FEDORA_COMPONENT_LABEL);
    // values.put(VAR_PROPERTIES,
    // getDigitalObjectPropertiesFoXml(propertiesValues));
    // return getXml(FOXML_DIGITAL_OBJECT_RESOURCE_NAME,
    // FOXML_DIGITAL_OBJECT_PATH, values);
    // }

    // public String getItemRelsExtFoXml(final Map values)
    // throws WebserverSystemException {
    //
    // return getXml(FOXML_ITEM_RELS_EXT_RESOURCE_NAME,
    // FOXML_ITEM_RELS_EXT_PATH, values);
    // }

    // public String getItemRelsExtRelatedComponentFoXml(final Map values)
    // throws WebserverSystemException {
    //
    // return getXml(FOXML_ITEM_RELS_EXT_RELATED_COMPONENT_RESOURCE_NAME,
    // FOXML_ITEM_RELS_EXT_RELATED_COMPONENT_PATH, values);
    // }

    // public String getWovFoXml(final Map values) throws
    // WebserverSystemException {
    // String wov = getXml(FOXML_WOV_RESOURCE_NAME, FOXML_WOV_PATH, values);
    // // wov datastream of the content-relation.wov does not contain the
    // // element valid-status
    // // In this case the value of valid-status is null
    // if (values.get(XmlTemplateProvider.VAR_VALID_STATUS) == null) {
    // String validStatusSnippet =
    // "<" + Constants.WOV_NAMESPACE_PREFIX + ":valid-status[^<]*</"
    // + Constants.WOV_NAMESPACE_PREFIX + ":valid-status>";
    // Pattern p = Pattern.compile(validStatusSnippet);
    // Matcher m = p.matcher(wov);
    // if (m.find()) {
    // wov = wov.replaceAll(validStatusSnippet, "");
    // }
    // }
    // if (values.get(XmlTemplateProvider.VAR_HREF) == null) {
    // String hrefSnippet = Constants.XLINK_PREFIX + ":href.*" + "user=";
    //
    // Pattern p = Pattern.compile(hrefSnippet);
    // Matcher m = p.matcher(wov);
    // if (m.find()) {
    // wov = wov.replaceAll(hrefSnippet, "user=");
    // }
    // }
    // return wov;
    // }

    // public String getPremisEventXml(final Map values)
    // throws WebserverSystemException {
    // String event =
    // getXml(FOXML_EVENT_RESOURCE_NAME, FOXML_EVENT_PATH, values);
    // return event;
    // }

    // public String getWovVersionEntryXml(final Map values)
    // throws WebserverSystemException {
    // String event =
    // getXml(FOXML_VERSION_ENTRY_RESOURCE_NAME, FOXML_VERSION_ENTRY_PATH,
    // values);
    // return event;
    // }

    // public String getComponentRelsExtFoXml(final Map values)
    // throws WebserverSystemException {
    //
    // if (values.get(VAR_DESCRIPTION) != null) {
    // Map<String, String> descriptionValues =
    // new HashMap<String, String>();
    // descriptionValues.put(VAR_DESCRIPTION, (String) values
    // .get(VAR_DESCRIPTION));
    // descriptionValues.put(VAR_COMPONENTS_NAMESPACE_PREFIX,
    // (String) values.get(VAR_COMPONENTS_NAMESPACE_PREFIX));
    // values.put(VAR_DESCRIPTION, getXml(
    // FOXML_COMPONENT_RELS_EXT_DESCRIPTION_RESOURCE_NAME,
    // FOXML_COMPONENT_RELS_EXT_PATH, descriptionValues));
    // }
    // else {
    // values.put(VAR_DESCRIPTION, "");
    // }
    //
    // if (values.get(VAR_MIME_TYPE) != null) {
    // Map<String, String> mimeTypeValues = new HashMap<String, String>();
    // mimeTypeValues.put(VAR_MIME_TYPE, (String) values
    // .get(VAR_MIME_TYPE));
    // mimeTypeValues.put(VAR_COMPONENTS_NAMESPACE_PREFIX, (String) values
    // .get(VAR_COMPONENTS_NAMESPACE_PREFIX));
    // values.put(VAR_MIME_TYPE, getXml(
    // FOXML_COMPONENT_RELS_EXT_MIME_TYPE_RESOURCE_NAME,
    // FOXML_COMPONENT_RELS_EXT_PATH, mimeTypeValues));
    // }
    // else {
    // values.put(VAR_MIME_TYPE, "");
    // }
    // if (values.get(VAR_LOCATOR_URL) != null) {
    // Map<String, String> locatorUrlValues =
    // new HashMap<String, String>();
    // locatorUrlValues.put(VAR_LOCATOR_URL, (String) values
    // .get(VAR_LOCATOR_URL));
    // locatorUrlValues.put(VAR_COMPONENTS_NAMESPACE_PREFIX,
    // (String) values.get(VAR_COMPONENTS_NAMESPACE_PREFIX));
    // values.put(VAR_LOCATOR_URL, getXml(
    // FOXML_COMPONENT_RELS_EXT_LOCATOR_URL_RESOURCE_NAME,
    // FOXML_COMPONENT_RELS_EXT_PATH, locatorUrlValues));
    // }
    // else {
    // values.put(VAR_LOCATOR_URL, "");
    // }
    //
    // return getXml(FOXML_COMPONENT_RELS_EXT_RESOURCE_NAME,
    // FOXML_COMPONENT_RELS_EXT_PATH, values);
    // }

    // private String getDigitalObjectPropertiesFoXml(final Map values)
    // throws WebserverSystemException {
    //
    // return getXml(FOXML_DIGITAL_OBJECT_PROPERTIES_RESOURCE_NAME,
    // FOXML_DIGITAL_OBJECT_PROPERTIES_PATH, values);
    // }

    // public String getDataStreamInlineXmlContentFoXml(final Map values)
    // throws WebserverSystemException {
    //
    // return getXml(FOXML_DATASTREAM_INLINE_XML_CONTENT_RESOURCE_NAME,
    // FOXML_DATASTREAM_PATH, values);
    // }

    // public String getDataStreamFoXml(final Map values)
    // throws WebserverSystemException {
    //
    // return getXml(FOXML_DATASTREAM_RESOURCE_NAME, FOXML_DATASTREAM_PATH,
    // values);
    // }

    // public String getContentLocationFoXml(final Map values)
    // throws WebserverSystemException {
    //
    // return getXml(FOXML_CONTENT_LOCATION_RESOURCE_NAME,
    // FOXML_CONTENT_LOCATION_PATH, values);
    // }
    //
    // public static FoXmlProvider getInstance() {
    //
    // if (provider == null) {
    // provider = new FoXmlProvider();
    // }
    // return provider;
    // }
    //
    // public String getItemRelsExtContentRelationFoXml(final Map values)
    // throws WebserverSystemException {
    //
    // return getXml(FOXML_ITEM_RELS_EXT_CONTENT_RELATION_RESOURCE_NAME,
    // FOXML_ITEM_RELS_EXT_CONTENT_RELATION_PATH, values);
    // }
}
