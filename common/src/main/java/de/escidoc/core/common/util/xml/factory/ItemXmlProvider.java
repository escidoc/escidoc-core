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

import de.escidoc.core.common.exceptions.system.WebserverSystemException;

import java.util.Map;

/**
 * Item specific implementation of XmlTemplateProvider using the velocity
 * template engine.<br>
 * This implementation uses the velocity singleton pattern.
 * 
 * @see InfrastructureXmlProvider
 * 
 */
public class ItemXmlProvider extends InfrastructureXmlProvider {

    public static final String ITEM_RESOURCE_NAME = "item";

    public static final String ITEM_PATH = "/item";

    private static final String PARENTS_RESOURCE_NAME = "parents";

    public static final String PROPERTIES_RESOURCE_NAME = "properties";

    public static final String RESOURCES_RESOURCE_NAME = "resources";

    public static final String PROPERTIES_ROOT_ATTRIBUTES_RESOURCE_NAME =
        "propertiesRootAttributes";

    public static final String PROPERTIES_PATH = ITEM_PATH;

    public static final String LOCK_STATUS_LOCKED_RESOURCE_NAME = "locked";

    public static final String LOCK_STATUS_UNLOCKED_RESOURCE_NAME = "unlocked";

    public static final String LOCK_STATUS_PROPERTIES_PATH = PROPERTIES_PATH;

    public static final String WITHDRAW_RESOURCE_NAME = "withdrawn";

    public static final String WITHDRAW_RESOURCE_PATH = PROPERTIES_PATH;

    public static final String CURRENT_VERSION_RESOURCE_NAME = "currentVersion";

    public static final String CURRENT_VERSION_RESOURCE_NAME_PID =
        "currentVersionPid";

    public static final String OBJECT_PID_RESOURCE_NAME = "objectPid";

    public static final String CURRENT_VERSION_PROPERTIES_PATH =
        PROPERTIES_PATH;

    public static final String LATEST_VERSION_RESOURCE_NAME = "latestVersion";

    public static final String LATEST_VERSION_PROPERTIES_PATH = PROPERTIES_PATH;

    public static final String LATEST_REVISION_RESOURCE_NAME = "latestRevision";

    public static final String LATEST_REVISION_PROPERTIES_PATH =
        PROPERTIES_PATH;

    public static final String MD_RECORDS_RESOURCE_NAME = "md-records";

    public static final String MD_RECORD_RESOURCE_NAME = "md-record";

    public static final String CONTENT_STREAMS_RESOURCE_NAME =
        "content-streams";

    public static final String CONTENT_STREAM_RESOURCE_NAME = "content-stream";

    public static final String MD_RECORDS_ROOT_ATTRIBUTES_RESOURCE_NAME =
        "mdRecordsRootAttributes";

    public static final String COMMON_PATH = "/common";

    public static final String MD_RECORDS_PATH = COMMON_PATH;

    public static final String MD_RECORD_PATH = COMMON_PATH;

    public static final String CONTENT_STREAMS_PATH = COMMON_PATH;

    public static final String CONTENT_STREAM_PATH = COMMON_PATH;

    public static final String RELATION_PATH = COMMON_PATH;

    public static final String RELATIONS_RESOURCE_NAME = "relations";

    public static final String RELATIONS_PATH = COMMON_PATH;

    public static final String COMPONENTS_RESOURCE_NAME = "components";

    public static final String COMPONENTS_ROOT_ATTRIBUTES_RESOURCE_NAME =
        "componentsRootAttributes";

    public static final String COMPONENTS_PATH = ITEM_PATH;

    public static final String COMPONENT_RESOURCE_NAME = "component";

    public static final String COMPONENT_ROOT_ATTRIBUTES_RESOURCE_NAME =
        "componentRootAttributes";

    public static final String COMPONENT_PATH = COMPONENTS_PATH;

    public static final String COMPONENT_PROPERTIES_RESOURCE_NAME =
        "componentProperties";

    public static final String COMPONENT_PROPERTIES_PATH = COMPONENTS_PATH;

    public static final String COMPONENT_CONTENT_RESOURCE_NAME =
        "componentContent";

    public static final String COMPONENT_CONTENT_PATH = COMPONENTS_PATH;

    public static final String COMPONENT_DESCRIPTION_RESOURCE_NAME =
        "componentDescription";

    public static final String COMPONENT_DESCRIPTION_PATH = COMPONENTS_PATH;

    public static final String COMPONENT_LOCATOR_URL_RESOURCE_NAME =
        "componentLocatorUrl";

    public static final String COMPONENT_PID_RESOURCE_NAME = "componentPid";

    public static final String COMPONENT_PID_PATH = COMPONENTS_PATH;

    public static final String COMPONENT_MIME_TYPE_RESOURCE_NAME =
        "componentMimeType";

    public static final String COMPONENT_MIME_TYPE_PATH = COMPONENTS_PATH;

    public static final String ITEM_LIST_RESOURCE_NAME = "itemlist";

    public static final String ITEM_LIST_PATH = ITEM_PATH;

    public static final String ITEM_LIST_ENTRY_WITHDRAWN_RESOURCE_NAME =
        "itemlistEntryWithdrawn";

    public static final String ITEM_LIST_ENTRY_WITHDRAWN_TYPE_PATH = ITEM_PATH;

    private static final ItemXmlProvider PROVIDER = new ItemXmlProvider();

    /**
     * Get the eSciDoc XML document to deliver.
     * 
     * @param values
     *            Map of values.
     * @return The eSciDoc Item document.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public final String getItemXml(final Map<String, Object> values)
        throws WebserverSystemException {

        return getXml(ITEM_RESOURCE_NAME, ITEM_PATH, values);
    }

    /**
     * Get the eSciDoc XML document to deliver.
     * 
     * @param properties
     *            Map of values.
     * @return The eSciDoc Item Properties document.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public final String getItemPropertiesXml(final Map<String, String> properties)
        throws WebserverSystemException {

        return getXml(PROPERTIES_RESOURCE_NAME, PROPERTIES_PATH, properties);
    }

    /**
     * Get the eSciDoc XML document to deliver.
     * 
     * @param properties
     *            Map of values.
     * @return The eSciDoc Item Relations document.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public final String getItemRelationsXml(final Map<String, Object> properties)
        throws WebserverSystemException {

        return getXml(RELATIONS_RESOURCE_NAME, RELATIONS_PATH, properties);
    }

    /**
     * Get the eSciDoc XML document to deliver.
     * 
     * @param properties
     *            Map of values.
     * @return The eSciDoc Item Resources document.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public final String getItemResourcesXml(final Map<String, Object> properties)
        throws WebserverSystemException {

        return getXml(RESOURCES_RESOURCE_NAME, ITEM_PATH, properties);
    }

    /**
     * Get the eSciDoc XML document to deliver.
     * 
     * @param values
     *            Map of values.
     * @return The eSciDoc Item Metadata Records document.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public final String getMdRecordsXml(final Map<String, String> values)
        throws WebserverSystemException {

        return getXml(MD_RECORDS_RESOURCE_NAME, MD_RECORDS_PATH, values);
    }

    /**
     * Get the eSciDoc XML document to deliver.
     * 
     * @param values
     *            Map of values.
     * @return The eSciDoc Item Metadata Record document.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public final String getMdRecordXml(final Map<String, String> values)
        throws WebserverSystemException {

        return getXml(MD_RECORD_RESOURCE_NAME, MD_RECORD_PATH, values);
    }

    /**
     * Get the eSciDoc XML document to deliver.
     * 
     * @param values
     *            Map of values.
     * @return The eSciDoc Item Content Streams document.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public final String getContentStreamsXml(final Map<String, String> values)
        throws WebserverSystemException {

        return getXml(CONTENT_STREAMS_RESOURCE_NAME, CONTENT_STREAM_PATH,
            values);
    }

    /**
     * Get the eSciDoc XML document to deliver.
     * 
     * @param values
     *            Map of values.
     * @return The eSciDoc Item Content Stream document.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public final String getContentStreamXml(final Map<String, String> values)
        throws WebserverSystemException {

        return getXml(CONTENT_STREAM_RESOURCE_NAME, CONTENT_STREAM_PATH, values);
    }

    public final String getParentsXml(final Map values)
        throws WebserverSystemException {

        return getXml(PARENTS_RESOURCE_NAME, COMMON_PATH, values);
    }

    /**
     * Get the eSciDoc XML document to deliver.
     * 
     * @param values
     *            Map of values.
     * @return The md-records root attributes
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public String getMdRecordsRootAttributes(final Object values)
        throws WebserverSystemException {

        String result = "";
        if (values instanceof Map) {
            result =
                getXml(MD_RECORDS_ROOT_ATTRIBUTES_RESOURCE_NAME,
                    MD_RECORDS_PATH, (Map) values);
        }
        return result;
    }

    /**
     * Get the eSciDoc XML document to deliver.
     * 
     * @param values
     *            Map of values.
     * @return The md-record root attributes
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public String getMdRecordRootAttributes(final Object values)
        throws WebserverSystemException {

        String result = "";
        if (values instanceof Map) {
            result =
                getXml(MD_RECORDS_ROOT_ATTRIBUTES_RESOURCE_NAME,
                    MD_RECORDS_PATH, (Map) values);
        }
        return result;
    }

    /**
     * Get the eSciDoc XML document to deliver.
     * 
     * @param values
     *            Map of values.
     * @return The eSciDoc Item Components document.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public final String getComponentsXml(final Map<String, String> values)
        throws WebserverSystemException {

        return getXml(COMPONENTS_RESOURCE_NAME, COMPONENTS_PATH, values);
    }

    /**
     * Get the eSciDoc XML document to deliver.
     * 
     * @param values
     *            Map of values.
     * @return The Components root attributes
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public String getComponentsRootAttributes(final Object values)
        throws WebserverSystemException {

        String result = "";
        if (values instanceof Map) {
            result =
                getXml(COMPONENTS_ROOT_ATTRIBUTES_RESOURCE_NAME,
                    COMPONENTS_PATH, (Map) values);
        }
        return result;
    }

    /**
     * Get the eSciDoc XML document to deliver.
     * 
     * @param values
     *            Map of values.
     * @return The eSciDoc Item Component document.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public final String getComponentXml(final Map<String, String> values)
        throws WebserverSystemException {

        return getXml(COMPONENT_RESOURCE_NAME, COMPONENT_PATH, values);
    }

    /**
     * Get the eSciDoc XML document to deliver.
     * 
     * @param values
     *            Map of values.
     * @return The Component root attributes
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public String getComponentRootAttributes(final Object values)
        throws WebserverSystemException {

        String result = "";
        if (values instanceof Map) {
            result =
                getXml(COMPONENT_ROOT_ATTRIBUTES_RESOURCE_NAME, COMPONENT_PATH,
                    (Map) values);
        }
        return result;
    }

    /**
     * Get the eSciDoc XML document to deliver.
     * 
     * @param values
     *            Map of values.
     * @return The eSciDoc Item Component Properties document.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public final String getComponentPropertiesXml(final Map<String, String> values)
        throws WebserverSystemException {

        return getXml(COMPONENT_PROPERTIES_RESOURCE_NAME,
            COMPONENT_PROPERTIES_PATH, values);
    }

    /**
     * Get the eSciDoc XML document to deliver.
     * 
     * @param values
     *            Map of values.
     * @return The eSciDoc Item Component Content document.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public String getComponentContentXml(final Map<String, Object> values)
        throws WebserverSystemException {

        return getXml(COMPONENT_CONTENT_RESOURCE_NAME, COMPONENT_CONTENT_PATH,
            values);
    }

    /**
     * Get the eSciDoc XML document to deliver.
     * 
     * @param properties
     *            Map of values.
     * @return The eSciDoc Item List document.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public final String getItemListXml(final Map<String, Object> properties)
        throws WebserverSystemException {

        return getXml(ITEM_LIST_RESOURCE_NAME, ITEM_LIST_PATH, properties);
    }

    /**
     * Get the eSciDoc XML document to deliver.
     * 
     * @param properties
     *            Map of values.
     * @return The eSciDoc Item List Withdrawn Entry document.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public String getItemListEntryWithdrawn(final Map<String, Object> properties)
        throws WebserverSystemException {

        return getXml(ITEM_LIST_ENTRY_WITHDRAWN_RESOURCE_NAME,
            ITEM_LIST_ENTRY_WITHDRAWN_TYPE_PATH, properties);
    }

    /**
     * Returns an instance of this.
     * 
     * @return The singleton.
     */
    public static ItemXmlProvider getInstance() {
        return PROVIDER;
    }
}
