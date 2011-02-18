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
 * Content Model specific implementation of XmlTemplateProvider using the
 * velocity template engine.<br>
 * This implementation uses the velocity singleton pattern.
 * 
 * @see InfrastructureXmlProvider
 * 
 * @author FRS
 * @om
 */
public class ContentModelXmlProvider extends InfrastructureXmlProvider {

    private static final String RESOURCE_NAME = "content-model";

    private static final String PATH = "/content-model";

    private static final String PROPERTIES_RESOURCE_NAME = "properties";

    private static final String RESOURCES_RESOURCE_NAME = "resources";

    public static final String PROPERTIES_ROOT_ATTRIBUTES_RESOURCE_NAME =
        "propertiesRootAttributes";

    private static final String PROPERTIES_PATH = PATH;

    public static final String LOCK_STATUS_LOCKED_RESOURCE_NAME = "locked";

    public static final String LOCK_STATUS_UNLOCKED_RESOURCE_NAME = "unlocked";

    public static final String LOCK_STATUS_PROPERTIES_PATH = PROPERTIES_PATH;

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

    private static final String CONTENT_STREAMS_RESOURCE_NAME =
        "content-streams";

    private static final String CONTENT_STREAM_RESOURCE_NAME = "content-stream";

    public static final String MD_RECORDS_ROOT_ATTRIBUTES_RESOURCE_NAME =
        "mdRecordsRootAttributes";

    private static final String COMMON_PATH = "/common";

    public static final String MD_RECORDS_PATH = COMMON_PATH;

    public static final String MD_RECORD_PATH = COMMON_PATH;

    public static final String CONTENT_STREAMS_PATH = COMMON_PATH;

    private static final String CONTENT_STREAM_PATH = COMMON_PATH;

    public static final String RELATION_PATH = COMMON_PATH;

    public static final String RELATIONS_RESOURCE_NAME = "relations";

    public static final String RELATIONS_PATH = COMMON_PATH;

    public static final String COMPONENTS_RESOURCE_NAME = "components";

    public static final String COMPONENTS_ROOT_ATTRIBUTES_RESOURCE_NAME =
        "componentsRootAttributes";

    private static final ContentModelXmlProvider PROVIDER = new ContentModelXmlProvider();

    private ContentModelXmlProvider() {
    }

    /**
     * Get the eSciDoc XML document to deliver.
     * 
     * @param values
     *            Map of values.
     * @return The eSciDoc Content Model document.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public final String getContentModelXml(final Map<String, Object> values)
        throws WebserverSystemException {

        return getXml(RESOURCE_NAME, PATH, values);
    }

    /**
     * Get the eSciDoc XML document to deliver.
     * 
     * @param properties
     *            Map of values.
     * @return The eSciDoc Content Model Properties document.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public final String getContentModelPropertiesXml(
            final Map<String, String> properties) throws WebserverSystemException {

        return getXml(PROPERTIES_RESOURCE_NAME, PROPERTIES_PATH, properties);
    }

    /**
     * Get the eSciDoc XML document to deliver.
     * 
     * @param properties
     *            Map of values.
     * @return The eSciDoc Content Model Resources document.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public final String getContentModelResourcesXml(
            final Map<String, String> properties) throws WebserverSystemException {

        return getXml(RESOURCES_RESOURCE_NAME, PATH, properties);
    }

    /**
     * Get the eSciDoc XML document to deliver.
     * 
     * @param values
     *            Map of values.
     * @return The eSciDoc Content Model Content Streams document.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public final String getContentStreamsXml(final Map<String, Object> values)
        throws WebserverSystemException {

        return getXml(CONTENT_STREAMS_RESOURCE_NAME, CONTENT_STREAM_PATH,
            values);
    }

    /**
     * Get the eSciDoc XML document to deliver.
     * 
     * @param values
     *            Map of values.
     * @return The eSciDoc Content Model Content Stream document.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public final String getContentStreamXml(final Map<String, Object> values)
        throws WebserverSystemException {

        return getXml(CONTENT_STREAM_RESOURCE_NAME, CONTENT_STREAM_PATH, values);
    }

    /**
     * Returns an instance of this.
     * 
     * @return The singleton.
     */
    public static ContentModelXmlProvider getInstance() {
        return PROVIDER;
    }
}
