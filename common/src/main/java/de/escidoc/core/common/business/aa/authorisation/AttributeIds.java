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

package de.escidoc.core.common.business.aa.authorisation;

import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * Helper class providing attribute id URNs.
 *
 * @author Torsten Tetteroo
 */
public final class AttributeIds {

    /**
     * Private constructor to avoid instantiation.
     */
    private AttributeIds() {
    }

    /**
     * The part of the id that identifies the "new" attribute for that the value has to be provided within the
     * authorization request to the PDP.
     */
    public static final String MARKER = "-new";

    /**
     * Prefix of eSciDoc attributes.
     */
    public static final String ATTR_PREFIX = "info:escidoc/names:aa:1.0:";

    /**
     * Prefix identifying eSciDoc actions.
     */
    public static final String ACTION_PREFIX = ATTR_PREFIX + "action:";

    /**
     * Prefix identifying internal eSciDoc attributes.<br> These attributes are for internal use, only, and must not be
     * used in policies nor in evaluation requests.
     */
    public static final String ATTR_INTERNAL_PREFIX = ATTR_PREFIX + "internal:";

    /**
     * Prefix identifying internal environment attributes.
     */
    public static final String INTERNAL_ENVIRONMENT_PREFIX = ATTR_INTERNAL_PREFIX + "environment:";

    /**
     * Prefix identifying eSciDoc (internal?) functions.
     */
    // FIXME: internal, only?
    public static final String FUNCTION_PREFIX = ATTR_PREFIX + "function:";

    /**
     * Prefix identifying internal status.
     */
    // FIXME: internal, only?
    public static final String STATUS_PREFIX = ATTR_PREFIX + "status:";

    /**
     * Prefix identifying eSciDoc resource attributes.
     */
    public static final String RESOURCE_ATTR_PREFIX = ATTR_PREFIX + "resource:";

    /**
     * Length of prefix identifying eSciDoc resource attributes.
     */
    public static final int RESOURCE_ATTR_PREFIX_LENGTH = RESOURCE_ATTR_PREFIX.length();

    /**
     * Prefix identifying eSciDoc resource aggregation definition attributes.
     */
    public static final String RESOURCE_AGGREGATION_DEFINITION_ATTR_PREFIX =
        RESOURCE_ATTR_PREFIX + XmlUtility.NAME_AGGREGATION_DEFINITION + ':';

    /**
     * Prefix identifying eSciDoc subject attributes.
     */
    public static final String SUBJECT_ATTR_PREFIX = ATTR_PREFIX + "subject:";

    /**
     * Prefix identifying eSciDoc resource container attributes.
     */
    public static final String CONTAINER_ATTR_PREFIX = RESOURCE_ATTR_PREFIX + XmlUtility.NAME_CONTAINER + ':';

    /**
     * Prefix identifying eSciDoc resource content-model attributes.
     */
    public static final String CONTENT_MODEL_ATTR_PREFIX = RESOURCE_ATTR_PREFIX + XmlUtility.NAME_CONTENT_MODEL + ':';

    /**
     * Prefix identifying eSciDoc resource context attributes.
     */
    public static final String CONTEXT_ATTR_PREFIX = RESOURCE_ATTR_PREFIX + XmlUtility.NAME_CONTEXT + ':';

    /**
     * Prefix identifying eSciDoc resource item attributes.
     */
    public static final String ITEM_ATTR_PREFIX = RESOURCE_ATTR_PREFIX + XmlUtility.NAME_ITEM + ':';

    /**
     * Prefix identifying eSciDoc resource component attributes.
     */
    public static final String ITEM_COMPONENT_ATTR_PREFIX = ITEM_ATTR_PREFIX + XmlUtility.NAME_COMPONENT + ':';

    /**
     * Prefix identifying eSciDoc resource organizational unit attributes.
     */
    public static final String ORGANIZATIONAL_UNIT_ATTR_PREFIX =
        RESOURCE_ATTR_PREFIX + XmlUtility.NAME_ORGANIZATIONAL_UNIT + ':';

    /**
     * Prefix identifying eSciDoc resource report attributes.
     */
    public static final String REPORT_ATTR_PREFIX = RESOURCE_ATTR_PREFIX + XmlUtility.NAME_REPORT + ':';

    /**
     * Prefix identifying eSciDoc resource report definition attributes.
     */
    public static final String REPORT_DEFINITION_ATTR_PREFIX =
        RESOURCE_ATTR_PREFIX + XmlUtility.NAME_REPORT_DEFINITION + ':';

    /**
     * Prefix identifying eSciDoc resource role attributes.
     */
    public static final String ROLE_ATTR_PREFIX = RESOURCE_ATTR_PREFIX + XmlUtility.NAME_ROLE + ':';

    /**
     * Prefix identifying eSciDoc resource user-account attributes.
     */
    public static final String USER_ACCOUNT_ATTR_PREFIX = RESOURCE_ATTR_PREFIX + XmlUtility.NAME_USER_ACCOUNT + ':';

    /**
     * Prefix identifying eSciDoc resource user-account attributes.
     */
    public static final String USER_GROUP_ATTR_PREFIX = RESOURCE_ATTR_PREFIX + XmlUtility.NAME_USER_GROUP + ':';

    /**
     * Prefix identifying eSciDoc resource grant attributes.
     */
    public static final String USER_ACCOUNT_GRANT_ATTR_PREFIX = USER_ACCOUNT_ATTR_PREFIX + XmlUtility.NAME_GRANT + ':';

    /**
     * Prefix identifying eSciDoc resource grant attributes.
     */
    public static final String USER_GROUP_GRANT_ATTR_PREFIX = USER_GROUP_ATTR_PREFIX + XmlUtility.NAME_GRANT + ':';

    /**
     * Escidoc URNs.
     */
    public static final String URN_ITEM_COMPONENT_ATTR = ITEM_ATTR_PREFIX + XmlUtility.NAME_COMPONENT;

    public static final String URN_ITEM_PUBLIC_STATUS_ATTR = ITEM_ATTR_PREFIX + XmlUtility.NAME_PUBLIC_STATUS;

    public static final String URN_ITEM_VERSION_MODIFIED_BY_ATTR =
        ITEM_ATTR_PREFIX + "version-" + XmlUtility.NAME_MODIFIED_BY;

    public static final String URN_ITEM_VERSION_STATUS_ATTR = ITEM_ATTR_PREFIX + "version-" + XmlUtility.NAME_STATUS;

    public static final String URN_ITEM_LOCK_OWNER_ATTR = ITEM_ATTR_PREFIX + XmlUtility.NAME_LOCK_OWNER;

    public static final String URN_ITEM_MODIFIED_BY_ATTR = ITEM_ATTR_PREFIX + XmlUtility.NAME_MODIFIED_BY;

    public static final String URN_ITEM_COMPONENT_CONTENT_CATEGORY_ATTR =
        ITEM_COMPONENT_ATTR_PREFIX + XmlUtility.NAME_CONTENT_CATEGORY;

    public static final String URN_ITEM_COMPONENT_CREATED_BY_ATTR =
        ITEM_COMPONENT_ATTR_PREFIX + XmlUtility.NAME_CREATED_BY;

    public static final String URN_ITEM_COMPONENT_VALID_STATUS_ATTR =
        ITEM_COMPONENT_ATTR_PREFIX + XmlUtility.NAME_VALID_STATUS;

    public static final String URN_ITEM_COMPONENT_VISIBILITY_ATTR =
        ITEM_COMPONENT_ATTR_PREFIX + XmlUtility.NAME_VISIBILITY;

    public static final String URN_ITEM_CONTENT_MODEL_ATTR = ITEM_ATTR_PREFIX + XmlUtility.NAME_CONTENT_MODEL;

    public static final String URN_ITEM_CONTEXT_ATTR = ITEM_ATTR_PREFIX + XmlUtility.NAME_CONTEXT;

    public static final String URN_ITEM_CONTEXT_NEW_ATTR = URN_ITEM_CONTEXT_ATTR + MARKER;

    public static final String URN_ITEM_CREATED_BY_ATTR = ITEM_ATTR_PREFIX + XmlUtility.NAME_CREATED_BY;

    public static final String URN_ITEM_LATEST_RELEASE_NUMBER_ATTR =
        ITEM_ATTR_PREFIX + XmlUtility.NAME_LATEST_RELEASE_NUMBER;

    public static final String URN_ITEM_LATEST_VERSION_MODIFIED_BY_ATTR =
        ITEM_ATTR_PREFIX + XmlUtility.NAME_LATEST_VERSION_MODIFIED_BY;

    public static final String URN_ITEM_LATEST_VERSION_NUMBER_ATTR =
        ITEM_ATTR_PREFIX + XmlUtility.NAME_LATEST_VERSION_NUMBER;

    public static final String URN_ITEM_LATEST_VERSION_STATUS_ATTR =
        ITEM_ATTR_PREFIX + XmlUtility.NAME_LATEST_VERSION_STATUS;

    public static final String URN_ORGANIZATIONAL_UNIT_CREATED_BY_ATTR =
        ORGANIZATIONAL_UNIT_ATTR_PREFIX + XmlUtility.NAME_CREATED_BY;

    public static final String URN_ORGANIZATIONAL_UNIT_ID = RESOURCE_ATTR_PREFIX + "organizational-unit-id";

    public static final String URN_ORGANIZATIONAL_UNIT_PUBLIC_STATUS_ATTR =
        ORGANIZATIONAL_UNIT_ATTR_PREFIX + XmlUtility.NAME_PUBLIC_STATUS;

    public static final String URN_STATISTIC_SCOPE_ID = RESOURCE_ATTR_PREFIX + "scope-id";

    public static final String URN_SUBJECT_EMAIL_ATTR = SUBJECT_ATTR_PREFIX + XmlUtility.NAME_EMAIL;

    public static final String URN_SUBJECT_GRANT_OBJECT_ATTR =
        SUBJECT_ATTR_PREFIX + XmlUtility.NAME_GRANT + ':' + "object-ref";

    public static final String URN_SUBJECT_HANDLE_ATTR = SUBJECT_ATTR_PREFIX + "handle";

    public static final String URN_SUBJECT_LOGIN_NAME_ATTR = SUBJECT_ATTR_PREFIX + XmlUtility.NAME_LOGIN_NAME;

    public static final String URN_SUBJECT_NAME_ATTR = SUBJECT_ATTR_PREFIX + XmlUtility.NAME_NAME;

    public static final String URN_SUBJECT_ORGANIZATIONAL_UNIT_ATTR =
        SUBJECT_ATTR_PREFIX + XmlUtility.NAME_ORGANIZATIONAL_UNIT;

    public static final String URN_REPORT_DEFINITION_SCOPE_ATTR = REPORT_DEFINITION_ATTR_PREFIX + XmlUtility.NAME_SCOPE;

    public static final String URN_REPORT_REPORT_DEFINITION_ATTR =
        REPORT_ATTR_PREFIX + XmlUtility.NAME_REPORT_DEFINITION;

    public static final String URN_ROLE_CREATED_BY_ATTR = ROLE_ATTR_PREFIX + XmlUtility.NAME_CREATED_BY;

    public static final String URN_ROLE_NAME_ATTR = ROLE_ATTR_PREFIX + XmlUtility.NAME_NAME;

    public static final String URN_CONTENT_MODEL_ID = RESOURCE_ATTR_PREFIX + "content-model-id";

    public static final String URN_CONTENT_MODEL_CREATED_BY_ATTR =
        CONTENT_MODEL_ATTR_PREFIX + XmlUtility.NAME_CREATED_BY;

    public static final String URN_CONTENT_MODEL_PUBLIC_STATUS_ATTR =
        CONTENT_MODEL_ATTR_PREFIX + XmlUtility.NAME_PUBLIC_STATUS;

    public static final String URN_CONTEXT_ID = RESOURCE_ATTR_PREFIX + "context-id";

    public static final String URN_CONTEXT_CREATED_BY_ATTR = CONTEXT_ATTR_PREFIX + XmlUtility.NAME_CREATED_BY;

    public static final String URN_CONTEXT_ORGANIZATIONAL_UNIT_ATTR =
        CONTEXT_ATTR_PREFIX + XmlUtility.NAME_ORGANIZATIONAL_UNIT;

    public static final String URN_CONTEXT_PUBLIC_STATUS_ATTR = CONTEXT_ATTR_PREFIX + XmlUtility.NAME_PUBLIC_STATUS;

    public static final String URN_ITEM_ID = RESOURCE_ATTR_PREFIX + "item-id";

    public static final String URN_COMPONENT_ID = RESOURCE_ATTR_PREFIX + "component-id";

    public static final String URN_METADATA_SCHEMA_GENRE = RESOURCE_ATTR_PREFIX + "md-schema:genre";

    public static final String URN_OBJECT_TYPE = RESOURCE_ATTR_PREFIX + "object-type";

    public static final String URN_OBJECT_TYPE_NEW = URN_OBJECT_TYPE + MARKER;

    public static final String URN_CONTAINER_CONTENT_MODEL_ATTR = CONTAINER_ATTR_PREFIX + XmlUtility.NAME_CONTENT_MODEL;

    public static final String URN_CONTAINER_CONTEXT_ATTR = CONTAINER_ATTR_PREFIX + XmlUtility.NAME_CONTEXT;

    public static final String URN_CONTAINER_CREATED_BY_ATTR = CONTAINER_ATTR_PREFIX + XmlUtility.NAME_CREATED_BY;

    public static final String URN_CONTAINER_ID = RESOURCE_ATTR_PREFIX + "container-id";

    public static final String URN_CONTAINER_LATEST_RELEASE_NUMBER_ATTR =
        CONTAINER_ATTR_PREFIX + XmlUtility.NAME_LATEST_RELEASE_NUMBER;

    public static final String URN_CONTAINER_LATEST_VERSION_MODIFIED_BY_ATTR =
        CONTAINER_ATTR_PREFIX + XmlUtility.NAME_LATEST_VERSION_MODIFIED_BY;

    public static final String URN_CONTAINER_LATEST_VERSION_NUMBER_ATTR =
        CONTAINER_ATTR_PREFIX + XmlUtility.NAME_LATEST_VERSION_NUMBER;

    public static final String URN_CONTAINER_LATEST_VERSION_STATUS_ATTR =
        CONTAINER_ATTR_PREFIX + XmlUtility.NAME_LATEST_VERSION_STATUS;

    public static final String URN_CONTAINER_LOCK_OWNER_ATTR = CONTAINER_ATTR_PREFIX + XmlUtility.NAME_LOCK_OWNER;

    public static final String URN_CONTAINER_MEMBER_ATTR = CONTAINER_ATTR_PREFIX + XmlUtility.NAME_MEMBER;

    public static final String URN_CONTAINER_PUBLIC_STATUS_ATTR = CONTAINER_ATTR_PREFIX + XmlUtility.NAME_PUBLIC_STATUS;

    public static final String URN_CONTAINER_VERSION_MODIFIED_BY_ATTR =
        CONTAINER_ATTR_PREFIX + "version-" + XmlUtility.NAME_MODIFIED_BY;

    public static final String URN_CONTAINER_VERSION_STATUS_ATTR =
        CONTAINER_ATTR_PREFIX + "version-" + XmlUtility.NAME_STATUS;

    public static final String URN_USER_ACCOUNT_EMAIL_ATTR = USER_ACCOUNT_ATTR_PREFIX + XmlUtility.NAME_EMAIL;

    public static final String URN_USER_ACCOUNT_HANDLE_ATTR = USER_ACCOUNT_ATTR_PREFIX + "handle";

    public static final String URN_USER_ACCOUNT_LOGIN_NAME_ATTR = USER_ACCOUNT_ATTR_PREFIX + XmlUtility.NAME_LOGIN_NAME;

    public static final String URN_USER_ACCOUNT_NAME_ATTR = USER_ACCOUNT_ATTR_PREFIX + XmlUtility.NAME_NAME;

    public static final String URN_USER_ACCOUNT_ORGANIZATIONAL_UNIT_ATTR =
        USER_ACCOUNT_ATTR_PREFIX + XmlUtility.NAME_ORGANIZATIONAL_UNIT;

    public static final String URN_SUBRESOURCE_ATTR = RESOURCE_ATTR_PREFIX + "subresource-id";

    /**
     * XACML URNs.
     */
    public static final String XACML_ATTR_PREFIX = "urn:oasis:names:tc:xacml:1.0:";

    public static final String URN_ACTION_ID = XACML_ATTR_PREFIX + "action:action-id";

    public static final String URN_RESOURCE_ID = XACML_ATTR_PREFIX + "resource:resource-id";

    public static final String URN_SUBJECT_ID = XACML_ATTR_PREFIX + "subject:subject-id";

}
