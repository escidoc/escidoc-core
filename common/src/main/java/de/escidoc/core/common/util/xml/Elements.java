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

package de.escidoc.core.common.util.xml;

/**
 * Elements of eSciDoc XML representation.
 */
public final class Elements {

    /**
     * Private constructor to avoid instantiation.
     */
    private Elements() {
    }

    public static final String ATTRIBUTE_PREDICATE = "predicate";

    public static final String ELEMENT_ITEM = "item";

    public static final String ELEMENT_CONTENT_RELATION = "content-relation";

    public static final String ELEMENT_WOV_VERSION_HISTORY = "version-history";

    public static final String ELEMENT_WOV_VERSION_NUMBER = "version-number";

    public static final String ELEMENT_WOV_VERSION_TIMESTAMP = "timestamp";

    public static final String ELEMENT_WOV_VERSION_STATUS = "version-status";

    public static final String ELEMENT_WOV_VERSION_VALID_STATUS = "valid-status";

    public static final String ELEMENT_WOV_VERSION_COMMENT = "comment";

    public static final String ELEMENT_WOV_VERSION_PID = "pid";

    public static final String ELEMENT_PID = "pid";

    public static final String ELEMENT_WOV_EVENT_DATE = "eventDateTime";

    public static final String ELEMENT_WOV_EVENT_USER = "linkingAgentIdentifier";

    public static final String ELEMENT_WOV_EVENT_USER_ID = "linkingAgentIdentifierValue";

    public static final String ELEMENT_USER_PREFERENCE = "preference";

    public static final String ELEMENT_USER_PREFERENCES = "preferences";

    public static final String ELEMENT_USER_ATTRIBUTE = "attribute";

    public static final String ATTRIBUTE_XLINK_OBJID = "objid";

    public static final String ATTRIBUTE_XLINK_HREF = "href";

    public static final String ATTRIBUTE_XLINK_TITLE = "title";

    public static final String ATTRIBUTE_NAME = "name";

    public static final String PREDECESSOR_ATTRIBUTE_FORM = "form";

    public static final String ELEMENT_COMPONENT_CONTENT_CATEGORY = "content-category";

    public static final String ELEMENT_COMPONENT_CONTENT_CHECKSUM_ALGORITHM = "checksum-algorithm";

    public static final String ELEMENT_COMPONENT_CONTENT_CHECKSUM = "checksum";

    public static final String ELEMENT_FILE_NAME = "file-name";

    public static final String ELEMENT_MIME_TYPE = "mime-type";

    public static final String ELEMENT_VALID_STATUS = "valid-status";

    public static final String ELEMENT_VISIBILITY = "visibility";

    public static final String ELEMENT_LOCATOR_URL = "locator-url";

    public static final String ELEMENT_DESCRIPTION = "description";

    public static final String ELEMENT_PARAM_FORMAT = "format";

    public static final String ELEMENT_PARAM_WITHDRAW_COMMENT = "withdraw-comment";

    public static final String ELEMENT_PARAM_REVOKATION_REMARK = "revocation-remark";

    public static final String ELEMENT_PARAM_COMMENT = "comment";

    public static final String ATTRIBUTE_LAST_MODIFICATION_DATE = "last-modification-date";

    public static final String ELEMENT_RESOURCES_VERSION_HISTORY = "version-history";

    public static final String ELEMENT_CONTENT_MODEL_SPECIFIC = "content-model-specific";

    public static final String ELEMENT_CONTENT_MODEL = "content-model";

    public static final String ELEMENT_ORIGIN = "origin";

    public static final String ELEMENT_CONTEXT = "context";

    public static final String ELEMENT_NAME = "name";

    public static final String ELEMENT_DATE = "date";

    public static final String ELEMENT_COMMENT = "comment";

    public static final String ELEMENT_TYPE = "type";

    public static final String ELEMENT_CREATED_BY = "created-by";

    public static final String ELEMENT_CREATED_BY_TITLE = "created-by-title";

    public static final String ELEMENT_MODIFIED_BY = "modified-by";

    public static final String ELEMENT_MODIFIED_BY_TITLE = "modified-by-title";

    public static final String ELEMENT_ORGANIZATIONAL_UNITS = "organizational-units";

    public static final String ELEMENT_ORGANIZATIONAL_UNIT = "organizational-unit";

    public static final String ELEMENT_PROPERTIES = "properties";

    public static final String ELEMENT_PUBLIC_STATUS = "public-status";

    public static final String ELEMENT_PUBLIC_STATUS_COMMENT = "public-status-comment";

    public static final String ELEMENT_STATUS = "status";

    public static final String ELEMENT_NUMBER = "number";

    public static final String ELEMENT_DC_TITLE = "title";

    public static final String ELEMENT_DC_DESCRIPTION = "description";

    public static final String ELEMENT_CONTENT_STREAMS = "content-streams";

    public static final String ELEMENT_CONTENT_STREAM = "content-stream";

    public static final String ELEMENT_CONTENT = "content";

    public static final String ATTRIBUTE_CONTENT_STREAM_MIME_TYPE = "mime-type";

    public static final String ATTRIBUTE_STORAGE = "storage";

    public static final String ELEMENT_MD_RECORDS = "md-records";

    public static final String MD_RECORD_ATTRIBUTE_TYPE = "type";

    public static final String MD_RECORD_ATTRIBUTE_SCHEMA = "schema";

    /**
     * One md-record with this special attribute name is mandatory.
     */
    public static final String MANDATORY_MD_RECORD_NAME = "escidoc";

}
