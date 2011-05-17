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
package de.escidoc.core.test.om.interfaces;

import de.escidoc.core.test.om.OmTestBase;

/**
 * Xpath constants related to the item resource.
 *
 * @author Torsten Tetteroo
 */
public class ItemXpathsProvider {

    /**
     * Private contructor to avoid instantiation.
     */
    private ItemXpathsProvider() {
    }

    public static String XPATH_ITEM = "/item";

    public static String XPATH_ITEM_XLINK_HREF = XPATH_ITEM + OmTestBase.PART_XLINK_HREF;

    public static String XPATH_ITEM_XLINK_TYPE = XPATH_ITEM + OmTestBase.PART_XLINK_TYPE;

    public static String XPATH_ITEM_XLINK_TITLE = XPATH_ITEM + OmTestBase.PART_XLINK_TITLE;

    public static String XPATH_ITEM_OBJID = XPATH_ITEM + OmTestBase.PART_OBJID;

    public static String XPATH_ITEM_XML_BASE = XPATH_ITEM + "/@base";

    public static String XPATH_ITEM_COMPONENTS = XPATH_ITEM + "/components";

    public static String XPATH_ITEM_COMPONENTS_XLINK_HREF = XPATH_ITEM_COMPONENTS + OmTestBase.PART_XLINK_HREF;

    public static String XPATH_ITEM_COMPONENTS_XLINK_TYPE = XPATH_ITEM_COMPONENTS + OmTestBase.PART_XLINK_TYPE;

    public static String XPATH_ITEM_COMPONENTS_XLINK_TITLE = XPATH_ITEM_COMPONENTS + OmTestBase.PART_XLINK_TITLE;

    public static String XPATH_ITEM_COMPONENT = XPATH_ITEM_COMPONENTS + "/component";

    public static String XPATH_ITEM_COMPONENT_XLINK_HREF = XPATH_ITEM_COMPONENT + OmTestBase.PART_XLINK_HREF;

    public static String XPATH_ITEM_COMPONENT_XLINK_TYPE = XPATH_ITEM_COMPONENT + OmTestBase.PART_XLINK_TYPE;

    public static String XPATH_ITEM_COMPONENT_XLINK_TITLE = XPATH_ITEM_COMPONENT + OmTestBase.PART_XLINK_TITLE;

    public static String XPATH_ITEM_COMPONENT_OBJID = XPATH_ITEM_COMPONENT + OmTestBase.PART_OBJID;

    public static String XPATH_ITEM_COMPONENT_PROPERTIES = XPATH_ITEM_COMPONENT + "/properties";

    public static String XPATH_ITEM_COMPONENT_PROPERTIES_XLINK_HREF =
        XPATH_ITEM_COMPONENT_PROPERTIES + OmTestBase.PART_XLINK_HREF;

    public static String XPATH_ITEM_COMPONENT_PROPERTIES_XLINK_TYPE =
        XPATH_ITEM_COMPONENT_PROPERTIES + OmTestBase.PART_XLINK_TYPE;

    public static String XPATH_ITEM_COMPONENT_PROPERTIES_XLINK_TITLE =
        XPATH_ITEM_COMPONENT_PROPERTIES + OmTestBase.PART_XLINK_TITLE;

    public static String XPATH_ITEM_COMPONENT_CREATOR = XPATH_ITEM_COMPONENT_PROPERTIES + "/creator";

    public static String XPATH_ITEM_COMPONENT_CREATOR_XLINK_HREF =
        XPATH_ITEM_COMPONENT_CREATOR + OmTestBase.PART_XLINK_HREF;

    public static String XPATH_ITEM_COMPONENT_CREATOR_XLINK_TYPE =
        XPATH_ITEM_COMPONENT_CREATOR + OmTestBase.PART_XLINK_TYPE;

    public static String XPATH_ITEM_COMPONENT_CREATOR_XLINK_TITLE =
        XPATH_ITEM_COMPONENT_CREATOR + OmTestBase.PART_XLINK_TITLE;

    public static String XPATH_ITEM_COMPONENT_CREATOR_OBJID = XPATH_ITEM_COMPONENT_CREATOR + OmTestBase.PART_OBJID;

    public static String XPATH_ITEM_COMPONENT_MIME_TYPE = XPATH_ITEM_COMPONENT_PROPERTIES + "/mime-type";

    public static String XPATH_ITEM_CONTENT = XPATH_ITEM_COMPONENT + "/content";

    public static String XPATH_ITEM_CONTENT_XLINK_HREF = XPATH_ITEM_CONTENT + OmTestBase.PART_XLINK_HREF;

    public static String XPATH_ITEM_CONTENT_XLINK_TYPE = XPATH_ITEM_CONTENT + OmTestBase.PART_XLINK_TYPE;

    public static String XPATH_ITEM_CONTENT_XLINK_TITLE = XPATH_ITEM_CONTENT + OmTestBase.PART_XLINK_TITLE;

    public static String XPATH_ITEM_MD_RECORDS = XPATH_ITEM + "/md-records";

    public static String XPATH_ITEM_MD_RECORDS_XLINK_HREF = XPATH_ITEM_MD_RECORDS + OmTestBase.PART_XLINK_HREF;

    public static String XPATH_ITEM_MD_RECORDS_XLINK_TYPE = XPATH_ITEM_MD_RECORDS + OmTestBase.PART_XLINK_TYPE;

    public static String XPATH_ITEM_MD_RECORDS_XLINK_TITLE = XPATH_ITEM_MD_RECORDS + OmTestBase.PART_XLINK_TITLE;

    public static String XPATH_ITEM_MD_RECORD = XPATH_ITEM_MD_RECORDS + "/md-record";

    public static String XPATH_ITEM_MD_RECORD_SCHEMA = XPATH_ITEM_MD_RECORD + "/@schema";

    public static String XPATH_ITEM_MD_RECORD_XLINK_HREF = XPATH_ITEM_MD_RECORD + OmTestBase.PART_XLINK_HREF;

    public static String XPATH_ITEM_MD_RECORD_XLINK_TYPE = XPATH_ITEM_MD_RECORD + OmTestBase.PART_XLINK_TYPE;

    public static String XPATH_ITEM_MD_RECORD_XLINK_TITLE = XPATH_ITEM_MD_RECORD + OmTestBase.PART_XLINK_TITLE;

    public static String XPATH_ITEM_PROPERTIES = XPATH_ITEM + "/properties";

    public static String XPATH_ITEM_PROPERTIES_XLINK_HREF = XPATH_ITEM_PROPERTIES + OmTestBase.PART_XLINK_HREF;

    public static String XPATH_ITEM_PROPERTIES_XLINK_TYPE = XPATH_ITEM_PROPERTIES + OmTestBase.PART_XLINK_TYPE;

    public static String XPATH_ITEM_PROPERTIES_XLINK_TITLE = XPATH_ITEM_PROPERTIES + OmTestBase.PART_XLINK_TITLE;

    public static String XPATH_ITEM_CONTENT_TYPE = XPATH_ITEM_PROPERTIES + "/content-model";

    public static String XPATH_ITEM_CONTENT_TYPE_XLINK_HREF = XPATH_ITEM_CONTENT_TYPE + OmTestBase.PART_XLINK_HREF;

    public static String XPATH_ITEM_CONTENT_TYPE_XLINK_TYPE = XPATH_ITEM_CONTENT_TYPE + OmTestBase.PART_XLINK_TYPE;

    public static String XPATH_ITEM_CONTENT_TYPE_XLINK_TITLE = XPATH_ITEM_CONTENT_TYPE + OmTestBase.PART_XLINK_TITLE;

    public static String XPATH_ITEM_CONTENT_TYPE_OBJID = XPATH_ITEM_CONTENT_TYPE + OmTestBase.PART_OBJID;

    public static String XPATH_ITEM_CONTEXT = XPATH_ITEM_PROPERTIES + "/context";

    public static String XPATH_ITEM_CONTEXT_XLINK_HREF = XPATH_ITEM_CONTEXT + OmTestBase.PART_XLINK_HREF;

    public static String XPATH_ITEM_CONTEXT_XLINK_TYPE = XPATH_ITEM_CONTEXT + OmTestBase.PART_XLINK_TYPE;

    public static String XPATH_ITEM_CONTEXT_XLINK_TITLE = XPATH_ITEM_CONTEXT + OmTestBase.PART_XLINK_TITLE;

    public static String XPATH_ITEM_CONTEXT_OBJID = XPATH_ITEM_CONTEXT + OmTestBase.PART_OBJID;

    public static String XPATH_ITEM_CREATOR = XPATH_ITEM_PROPERTIES + "/created-by";

    public static String XPATH_ITEM_CREATOR_XLINK_HREF = XPATH_ITEM_CREATOR + OmTestBase.PART_XLINK_HREF;

    public static String XPATH_ITEM_CREATOR_XLINK_TYPE = XPATH_ITEM_CREATOR + OmTestBase.PART_XLINK_TYPE;

    public static String XPATH_ITEM_CREATOR_XLINK_TITLE = XPATH_ITEM_CREATOR + OmTestBase.PART_XLINK_TITLE;

    public static String XPATH_ITEM_CREATOR_OBJID = XPATH_ITEM_CREATOR + OmTestBase.PART_OBJID;

    public static String XPATH_ITEM_CURRENT_VERSION = XPATH_ITEM_PROPERTIES + "/current-version";

    public static String XPATH_ITEM_CURRENT_VERSION_XLINK_HREF =
        XPATH_ITEM_CURRENT_VERSION + OmTestBase.PART_XLINK_HREF;

    public static String XPATH_ITEM_CURRENT_VERSION_XLINK_TYPE =
        XPATH_ITEM_CURRENT_VERSION + OmTestBase.PART_XLINK_TYPE;

    public static String XPATH_ITEM_CURRENT_VERSION_XLINK_TITLE =
        XPATH_ITEM_CURRENT_VERSION + OmTestBase.PART_XLINK_TITLE;

    public static String XPATH_ITEM_CURRENT_VERSION_OBJID = XPATH_ITEM_CURRENT_VERSION + OmTestBase.PART_OBJID;

    public static String XPATH_ITEM_MODIFIED_BY = XPATH_ITEM_CURRENT_VERSION + "/modified-by";

    public static String XPATH_ITEM_MODIFIED_BY_XLINK_HREF = XPATH_ITEM_MODIFIED_BY + OmTestBase.PART_XLINK_HREF;

    public static String XPATH_ITEM_MODIFIED_BY_XLINK_TYPE = XPATH_ITEM_MODIFIED_BY + OmTestBase.PART_XLINK_TYPE;

    public static String XPATH_ITEM_MODIFIED_BY_XLINK_TITLE = XPATH_ITEM_MODIFIED_BY + OmTestBase.PART_XLINK_TITLE;

    public static String XPATH_ITEM_MODIFIED_BY_OBJID = XPATH_ITEM_MODIFIED_BY + OmTestBase.PART_OBJID;

    public static String XPATH_ITEM_LATEST_RELEASE = XPATH_ITEM_PROPERTIES + "/latest-release";

    public static String XPATH_ITEM_LATEST_RELEASE_XLINK_HREF = XPATH_ITEM_LATEST_RELEASE + OmTestBase.PART_XLINK_HREF;

    public static String XPATH_ITEM_LATEST_RELEASE_XLINK_TYPE = XPATH_ITEM_LATEST_RELEASE + OmTestBase.PART_XLINK_TYPE;

    public static String XPATH_ITEM_LATEST_RELEASE_XLINK_TITLE =
        XPATH_ITEM_LATEST_RELEASE + OmTestBase.PART_XLINK_TITLE;

    public static String XPATH_ITEM_LATEST_RELEASE_OBJID = XPATH_ITEM_LATEST_RELEASE + OmTestBase.PART_OBJID;

    public static String XPATH_ITEM_LATEST_VERSION = XPATH_ITEM_PROPERTIES + "/latest-version";

    public static String XPATH_ITEM_LATEST_VERSION_XLINK_HREF = XPATH_ITEM_LATEST_VERSION + OmTestBase.PART_XLINK_HREF;

    public static String XPATH_ITEM_LATEST_VERSION_XLINK_TYPE = XPATH_ITEM_LATEST_VERSION + OmTestBase.PART_XLINK_TYPE;

    public static String XPATH_ITEM_LATEST_VERSION_XLINK_TITLE =
        XPATH_ITEM_LATEST_VERSION + OmTestBase.PART_XLINK_TITLE;

    public static String XPATH_ITEM_LATEST_VERSION_OBJID = XPATH_ITEM_LATEST_VERSION + OmTestBase.PART_OBJID;

    public static String XPATH_ITEM_LOCK_OWNER = XPATH_ITEM_PROPERTIES + "/lock-owner";

    public static String XPATH_ITEM_LOCK_OWNER_XLINK_HREF = XPATH_ITEM_LOCK_OWNER + OmTestBase.PART_XLINK_HREF;

    public static String XPATH_ITEM_LOCK_OWNER_XLINK_TYPE = XPATH_ITEM_LOCK_OWNER + OmTestBase.PART_XLINK_TYPE;

    public static String XPATH_ITEM_LOCK_OWNER_XLINK_TITLE = XPATH_ITEM_LOCK_OWNER + OmTestBase.PART_XLINK_TITLE;

    public static String XPATH_ITEM_LOCK_OWNER_OBJID = XPATH_ITEM_LOCK_OWNER + OmTestBase.PART_OBJID;

}
