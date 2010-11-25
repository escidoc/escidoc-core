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
package de.escidoc.core.common.business;

/**
 * Constants.
 * 
 * @author MIH
 */
public final class Constants {

    public static final int MAX_THREADS = 4;

    public static final String XML_HEADER =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    /**
     * Format of all timestamps which are delivered by framework.
     */
    public static final String TIMESTAMP_FORMAT =
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static final String DEFAULT_MIME_TYPE = "text/xml";

    public static final String UNKNOWN = "unknown";

    public static final String DEFAULT_ALTID_TYPE = UNKNOWN;

    public static final String DEFAULT_ALTID_SCHEMA = UNKNOWN;

    /**
     * Namespace-Prefixes.
     * 
     * @common
     */
    public static final String GRANTS_NS_PREFIX = "grants";

    public static final String PROPERTIES_NS_PREFIX = "prop";

    public static final String VERSION_NS_PREFIX = "version";

    public static final String RELEASE_NS_PREFIX = "release";

    public static final String RESOURCES_NS_PREFIX = "resources";

    public static final String REQUESTS_NS_PREFIX = "requests";

    public static final String ROLE_NS_PREFIX = "role";

    public static final String ROLE_LIST_NS_PREFIX = "role-list";

    public static final String INDEX_CONFIGURATION_NS_PREFIX =
        "index-configuration";

    public static final String STRUCTURAL_RELATIONS_NS_PREFIX = "srel";

    public static final String PARAMETER_NS_PREFIX = "param";

    public static final String ORIGIN_NS_PREFIX = "origin";

    public static final String PARENTS_NAMESPACE_PREFIX = "parents";

    public static final String USER_ACCOUNT_NS_PREFIX = "user-account";

    public static final String USER_PREFERENCES_NS_PREFIX = "preferences";

    public static final String USER_ATTRIBUTES_NS_PREFIX = "attributes";

    public static final String USER_GROUP_LIST_NS_PREFIX = "user-group-list";

    public static final String SET_DEFINITION_LIST_NS_PREFIX =
        "set-definition-list";

    public static final String USER_GROUP_NS_PREFIX = "user-group";

    public static final String SET_DEFINITION_NS_PREFIX = "set-definition";

    public static final String USER_ACCOUNT_LIST_NS_PREFIX =
        "user-account-list";

    public static final String XACML_CONTEXT_NS_PREFIX = "xacml-context";

    public static final String XACML_POLICY_NS_PREFIX = "xacml-policy";

    public static final String XLINK_NS_PREFIX = "xlink";

    public static final String RDF_NAMESPACE_PREFIX = "rdf";

    public static final String DC_NS_PREFIX = "dc";

    public static final String OAI_DC_NS_PREFIX = "oai_dc";

    public static final String CONTENT_RELATIONS_NS_PREFIX_IN_RELSEXT = "nsCR";

    public static final String CONTENT_RELATIONS_NEW_NS_PREFIX_IN_RELSEXT =
        "crel";

    public static final String OLD_RELATIONS_NAMESPACE_PREFIX = "myNamespace";

    public static final String RELATIONS_NAMESPACE_PREFIX = "escidocRelations";

    public static final String RELATION_NAMESPACE_PREFIX =
        "escidocContentRelation";

    public static final String ITEM_NAMESPACE_PREFIX = "escidocItem";

    public static final String ITEM_PROPERTIES_NAMESPACE_PREFIX = "escidocItem";

    public static final String ITEM_LIST_NAMESPACE_PREFIX = "il";

    public static final String METADATARECORDS_NAMESPACE_PREFIX =
        "escidocMetadataRecords";

    public static final String CONTENT_STREAMS_NAMESPACE_PREFIX =
        "escidocContentStreams";

    public static final String COMPONENTS_NAMESPACE_PREFIX =
        "escidocComponents";

    public static final String CONTENT_RELATIONS_NAMESPACE_PREFIX = "relations";

    public static final String CONTENT_MODEL_NAMESPACE_PREFIX =
        "escidocContentModel";

    public static final String CONTAINER_NAMESPACE_PREFIX = "container";

    public static final String WOV_NAMESPACE_PREFIX = "escidocVersions";

    public static final String AGGREGATION_DEFINITION_NS_PREFIX =
        "aggregation-definition";

    public static final String AGGREGATION_DEFINITION_LIST_NS_PREFIX =
        "aggregation-definition-list";

    public static final String REPORT_DEFINITION_NS_PREFIX =
        "report-definition";

    public static final String REPORT_DEFINITION_LIST_NS_PREFIX =
        "report-definition-list";

    public static final String SCOPE_NS_PREFIX = "scope";

    public static final String SCOPE_LIST_NS_PREFIX = "scope-list";

    public static final String REPORT_NS_PREFIX = "report";

    /**
     * Namespace-URIs.
     * 
     * @common
     */
    public static final String NS_URI_PREFIX = "http://www.escidoc.de/schemas/";

    public static final String NS_URI_SCHEMA_VERSION_0_1 = "/0.1";

    public static final String NS_URI_SCHEMA_VERSION_0_3 = "/0.3";

    public static final String NS_URI_SCHEMA_VERSION_0_4 = "/0.4";

    public static final String NS_URI_SCHEMA_VERSION_0_5 = "/0.5";

    public static final String NS_URI_SCHEMA_VERSION_0_6 = "/0.6";

    public static final String NS_URI_SCHEMA_VERSION_0_7 = "/0.7";

    public static final String NS_URI_SCHEMA_VERSION_0_8 = "/0.8";

    public static final String NS_URI_SCHEMA_VERSION_0_9 = "/0.9";

    public static final String NS_URI_SCHEMA_VERSION_0_10 = "/0.10";

    /*
     * Current schema versions per resource
     */

    public static final String ITEM_NS_URI_SCHEMA_VERSION =
        NS_URI_SCHEMA_VERSION_0_10;

    public static final String CONTENT_MODEL_NS_URI_SCHEMA_VERSION =
        NS_URI_SCHEMA_VERSION_0_1;

    public static final String ITEM_LIST_NS_URI_SCHEMA_VERSION =
        ITEM_NS_URI_SCHEMA_VERSION;

    public static final String CONTAINER_NS_URI_SCHEMA_VERSION =
        NS_URI_SCHEMA_VERSION_0_9;

    public static final String CONTAINER_LIST_NS_URI_SCHEMA_VERSION =
        CONTAINER_NS_URI_SCHEMA_VERSION;

    public static final String CONTEXT_NS_URI_SCHEMA_VERSION =
        NS_URI_SCHEMA_VERSION_0_7;

    public static final String CONTEXT_LIST_NS_URI_SCHEMA_VERSION =
        CONTEXT_NS_URI_SCHEMA_VERSION;

    public static final String CONTENT_RELATION_NS_URI_SCHEMA_VERSION =
        NS_URI_SCHEMA_VERSION_0_1;

    public static final String MEMBER_LIST_NS_URI_SCHEMA_VERSION =
        NS_URI_SCHEMA_VERSION_0_10;

    public static final String USER_ACCOUNT_NS_URI_SCHEMA_VERSION =
        NS_URI_SCHEMA_VERSION_0_7;

    public static final String USER_ACCOUNT_LIST_NS_URI_SCHEMA_VERSION =
        USER_ACCOUNT_NS_URI_SCHEMA_VERSION;

    public static final String USER_PREFERENCES_NS_URI_SCHEMA_VERSION =
        NS_URI_SCHEMA_VERSION_0_1;

    public static final String USER_ATTRIBUTES_NS_URI_SCHEMA_VERSION =
        NS_URI_SCHEMA_VERSION_0_1;

    public static final String USER_GROUP_NS_URI_SCHEMA_VERSION =
        NS_URI_SCHEMA_VERSION_0_6;

    public static final String USER_GROUP_LIST_NS_URI_SCHEMA_VERSION =
        USER_GROUP_NS_URI_SCHEMA_VERSION;

    public static final String ROLE_NS_URI_SCHEMA_VERSION =
        NS_URI_SCHEMA_VERSION_0_5;

    public static final String ROLE_LIST_NS_URI_SCHEMA_VERSION =
        ROLE_NS_URI_SCHEMA_VERSION;

    public static final String INDEX_CONFIGURATION_NS_URI_SCHEMA_VERSION =
        NS_URI_SCHEMA_VERSION_0_1;

    public static final String AGGREGATION_DEFINITION_NS_URI_SCHEMA_VERSION =
        NS_URI_SCHEMA_VERSION_0_4;

    public static final String AGGREGATION_DEFINITION_LIST_NS_URI_SCHEMA_VERSION =
        AGGREGATION_DEFINITION_NS_URI_SCHEMA_VERSION;

    public static final String REPORT_DEFINITION_NS_URI_SCHEMA_VERSION =
        NS_URI_SCHEMA_VERSION_0_4;

    public static final String REPORT_DEFINITION_LIST_NS_URI_SCHEMA_VERSION =
        REPORT_DEFINITION_NS_URI_SCHEMA_VERSION;

    public static final String SCOPE_NS_URI_SCHEMA_VERSION =
        NS_URI_SCHEMA_VERSION_0_4;

    public static final String SCOPE_LIST_NS_URI_SCHEMA_VERSION =
        SCOPE_NS_URI_SCHEMA_VERSION;

    public static final String REPORT_NS_URI_SCHEMA_VERSION =
        NS_URI_SCHEMA_VERSION_0_4;

    public static final String REPORT_PARAMETERS_NS_URI_SCHEMA_VERSION =
        NS_URI_SCHEMA_VERSION_0_4;

    public static final String PREPROCESSING_INFORMATION_NS_URI_SCHEMA_VERSION =
        NS_URI_SCHEMA_VERSION_0_3;

    public static final String STATISTIC_DATA_NS_URI_SCHEMA_VERSION =
        NS_URI_SCHEMA_VERSION_0_3;

    /*
     * END Current schema versions per resource
     */

    public static final String GRANTS_NS_URI = NS_URI_PREFIX + "grants"
        + NS_URI_SCHEMA_VERSION_0_5;

    public static final String PROPERTIES_NS_URI =
        "http://escidoc.de/core/01/properties/";

    public static final String RESOURCES_NS_URI =
        "http://escidoc.de/core/01/resources/";

    public static final String VERSION_NS_URI =
        "http://escidoc.de/core/01/properties/version/";

    public static final String RELEASE_NS_URI =
        "http://escidoc.de/core/01/properties/release/";

    public static final String REQUESTS_NS_URI = NS_URI_PREFIX + "pdp"
        + NS_URI_SCHEMA_VERSION_0_3 + "/requests";

    public static final String RESULTS_NS_URI = NS_URI_PREFIX + "pdp"
        + NS_URI_SCHEMA_VERSION_0_3 + "/results";

    public static final String ROLE_NS_URI = NS_URI_PREFIX + "role"
        + ROLE_NS_URI_SCHEMA_VERSION;

    public static final String ROLE_LIST_NS_URI = NS_URI_PREFIX + "rolelist"
        + ROLE_LIST_NS_URI_SCHEMA_VERSION;

    public static final String INDEX_CONFIGURATION_NS_URI = NS_URI_PREFIX
        + "index-configuration" + INDEX_CONFIGURATION_NS_URI_SCHEMA_VERSION;

    public static final String STAGING_FILE_NS_URI = NS_URI_PREFIX
        + "stagingfile" + NS_URI_SCHEMA_VERSION_0_3;

    public static final String STRUCTURAL_RELATIONS_NS_URI =
        "http://escidoc.de/core/01/structural-relations/";

    public static final String PARAMETER_NS_URI =
        "http://escidoc.de/core/01/parameter/";

    public static final String ORIGIN_NS_URI =
        "http://escidoc.de/core/01/structural-relations/origin/";

    public static final String USER_ACCOUNT_NS_URI = NS_URI_PREFIX
        + "useraccount" + USER_ACCOUNT_NS_URI_SCHEMA_VERSION;

    public static final String USER_ACCOUNT_LIST_NS_URI = NS_URI_PREFIX
        + "useraccountlist" + USER_ACCOUNT_LIST_NS_URI_SCHEMA_VERSION;

    public static final String USER_PREFERENCES_NS_URI = NS_URI_PREFIX
        + "preferences" + USER_PREFERENCES_NS_URI_SCHEMA_VERSION;

    public static final String USER_ATTRIBUTES_NS_URI = NS_URI_PREFIX
        + "attributes" + USER_ATTRIBUTES_NS_URI_SCHEMA_VERSION;

    public static final String USER_GROUP_NS_URI = NS_URI_PREFIX + "usergroup"
        + USER_GROUP_NS_URI_SCHEMA_VERSION;

    public static final String USER_GROUP_LIST_NS_URI = NS_URI_PREFIX
        + "usergrouplist" + USER_GROUP_LIST_NS_URI_SCHEMA_VERSION;

    public static final String SET_DEFINITION_NS_URI = NS_URI_PREFIX
        + "setdefinition" + NS_URI_SCHEMA_VERSION_0_1;

    public static final String SET_DEFINITION_LIST_NS_URI = NS_URI_PREFIX
        + "setdefinitionlist" + NS_URI_SCHEMA_VERSION_0_1;

    public static final String XACML_CONTEXT_NS_URI =
        "urn:oasis:names:tc:xacml:1.0:context";

    public static final String XACML_POLICY_NS_URI =
        "urn:oasis:names:tc:xacml:1.0:policy";

    public static final String DC_NS_URI = "http://purl.org/dc/elements/1.1/";

    public static final String DC_IDENTIFIER_URI = DC_NS_URI + "identifier";

    public static final String OAI_DC_NS_URI =
        "http://www.openarchives.org/OAI/2.0/oai_dc/";

    public static final String XLINK_NS_URI = "http://www.w3.org/1999/xlink";

    public static final String XML_NS_URI =
        "http://www.w3.org/XML/1998/namespace";

    public static final String RDF_NAMESPACE_URI =
        "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    public static final String RDF_SCHEMA_NAMESPACE_URI =
        "http://www.w3.org/2000/01/rdf-schema#";

    public static final String FEDORA_MODEL_NS_PREFIX = "fedora";

    public static final String FEDORA_MODEL_NS_URI =
        "info:fedora/fedora-system:def/model#";

    public static final String RELATIONS_NAMESPACE_URI =
        "http://www.nsdl.org/ontologies/relationships";

    public static final String RELATIONS_TITLE = RELATIONS_NAMESPACE_URI
        + "/title";

    public static final String RESULT_NAMESPACE_URI = NS_URI_PREFIX + "result"
        + NS_URI_SCHEMA_VERSION_0_1;

    public static final String ITEM_NAMESPACE_URI = NS_URI_PREFIX + "item"
        + ITEM_NS_URI_SCHEMA_VERSION;

    public static final String CONTENT_MODEL_NAMESPACE_URI = NS_URI_PREFIX
        + "contentmodel" + CONTENT_MODEL_NS_URI_SCHEMA_VERSION;

    public static final String ITEM_PROPERTIES_NAMESPACE_URI =
        ITEM_NAMESPACE_URI;

    public static final String ITEM_LIST_NAMESPACE_URI = NS_URI_PREFIX
        + "itemlist" + ITEM_LIST_NS_URI_SCHEMA_VERSION;

    public static final String METADATARECORDS_NAMESPACE_URI = NS_URI_PREFIX
        + "metadatarecords" + NS_URI_SCHEMA_VERSION_0_5;

    public static final String CONTENT_STREAMS_NAMESPACE_URI = NS_URI_PREFIX
        + "contentstreams" + NS_URI_SCHEMA_VERSION_0_7;

    public static final String COMPONENTS_NAMESPACE_URI = NS_URI_PREFIX
        + "components" + NS_URI_SCHEMA_VERSION_0_9;

    public static final String CONTENT_RELATIONS_NAMESPACE_URI = NS_URI_PREFIX
        + "relations" + NS_URI_SCHEMA_VERSION_0_3;

    public static final String CONTENT_RELATION_NAMESPACE_URI = NS_URI_PREFIX
        + "content-relation" + NS_URI_SCHEMA_VERSION_0_1;

    public static final String PARENTS_NAMESPACE_URI = NS_URI_PREFIX
        + "parents" + NS_URI_SCHEMA_VERSION_0_9;

    public static final String CONTEXT_NAMESPACE_PREFIX = "context";

    public static final String CONTEXT_NAMESPACE_URI = NS_URI_PREFIX
        + "context" + CONTEXT_NS_URI_SCHEMA_VERSION;

    public static final String CONTEXT_PROPERTIES_NAMESPACE_URI =
        CONTEXT_NAMESPACE_URI;

    public static final String CONTEXT_LIST_NAMESPACE_URI = NS_URI_PREFIX
        + "contextlist" + CONTEXT_NS_URI_SCHEMA_VERSION;

    public static final String CONTAINER_NAMESPACE_URI = NS_URI_PREFIX
        + "container" + CONTAINER_NS_URI_SCHEMA_VERSION;

    public static final String CONTAINER_PROPERTIES_NAMESPACE_URI =
        CONTAINER_NAMESPACE_URI;

    public static final String CONTAINER_LIST_NAMESPACE_URI = NS_URI_PREFIX
        + "containerlist" + CONTAINER_LIST_NS_URI_SCHEMA_VERSION;

    public static final String CONTAINER_REF_LIST_NAMESPACE = NS_URI_PREFIX
        + "containerreflist" + NS_URI_SCHEMA_VERSION_0_3;

    public static final String WOV_NAMESPACE_URI = NS_URI_PREFIX
        + "versionhistory" + NS_URI_SCHEMA_VERSION_0_3;

    public static final String XLINK_URI = XLINK_NS_URI;

    public static final String STRUCT_MAP_NAMESPACE_URI = NS_URI_PREFIX
        + "structmap" + NS_URI_SCHEMA_VERSION_0_4;

    public static final String MEMBER_LIST_NAMESPACE_URI = NS_URI_PREFIX
        + "memberlist" + MEMBER_LIST_NS_URI_SCHEMA_VERSION;

    public static final String MEMBER_REF_LIST_NAMESPACE_URI = NS_URI_PREFIX
        + "memberreflist" + NS_URI_SCHEMA_VERSION_0_3;

    public static final String AGGREGATION_DEFINITION_NS_URI = NS_URI_PREFIX
        + "aggregationdefinition"
        + AGGREGATION_DEFINITION_NS_URI_SCHEMA_VERSION;

    public static final String REPORT_DEFINITION_NS_URI = NS_URI_PREFIX
        + "reportdefinition" + REPORT_DEFINITION_NS_URI_SCHEMA_VERSION;

    public static final String SCOPE_NS_URI = NS_URI_PREFIX + "scope"
        + SCOPE_NS_URI_SCHEMA_VERSION;

    public static final String AGGREGATION_DEFINITION_LIST_NS_URI =
        NS_URI_PREFIX + "aggregationdefinitionlist"
            + AGGREGATION_DEFINITION_NS_URI_SCHEMA_VERSION;

    public static final String REPORT_DEFINITION_LIST_NS_URI = NS_URI_PREFIX
        + "reportdefinitionlist" + REPORT_DEFINITION_NS_URI_SCHEMA_VERSION;

    public static final String SCOPE_LIST_NS_URI = NS_URI_PREFIX + "scopelist"
        + SCOPE_NS_URI_SCHEMA_VERSION;

    public static final String REPORT_NS_URI = NS_URI_PREFIX + "report"
        + REPORT_NS_URI_SCHEMA_VERSION;

    /**
     * Prefixes.
     * 
     * @common
     */
    public static final String CONTEXT_LIST_PREFIX = "context-list";

    public static final String CONTEXT_PROPERTIES_PREFIX = "context";

    public static final String CONTAINER_PROPERTIES_PREFIX = "container";

    public static final String CONTAINER_LIST_PREFIX = "cl";

    public static final String XLINK_PREFIX = "xlink";

    public static final String XML_PREFIX = "xml";

    public static final String STRUCT_MAP_PREFIX = "struct-map";

    public static final String MEMBER_LIST_PREFIX = "member-list";

    public static final String CONTAINER_REF_LIST_PREFIX = "container-ref-list";

    public static final String MEMBER_REF_LIST_PREFIX = "member-ref-list";

    /**
     * Types.
     * 
     * @common
     */
    public static final String XLINK_TYPE_SIMPLE = "simple";

    public static final String ORGANIZATIONAL_UNIT_PREFIX =
        "organizational-unit";

    public static final String ORGANIZATIONAL_UNIT_LIST_PREFIX =
        "organizational-unit-list";

    public static final String ORGANIZATIONAL_UNIT_PATH_LIST_PREFIX =
        "organizational-unit-path-list";

    public static final String ORGANIZATIONAL_UNIT_SUCCESSORS_PREFIX =
        "organizational-unit-successors-list";

    public static final String ORGANIZATIONAL_UNIT_REF_LIST_PREFIX =
        "organizational-unit-ref-list";

    public static final String ORGANIZATIONAL_UNIT_REF_PREFIX =
        "organizational-unit-ref";

    public static final String ORGANIZATIONAL_UNIT_NS_URI_SCHEMA_VERSION =
        NS_URI_SCHEMA_VERSION_0_8;

    public static final String ORGANIZATIONAL_UNIT_NAMESPACE_URI =
        NS_URI_PREFIX + "organizationalunit"
            + ORGANIZATIONAL_UNIT_NS_URI_SCHEMA_VERSION;

    public static final String ORGANIZATIONAL_UNIT_LIST_NAMESPACE_URI =
        NS_URI_PREFIX + "organizationalunitlist"
            + ORGANIZATIONAL_UNIT_NS_URI_SCHEMA_VERSION;

    public static final String ORGANIZATIONAL_UNIT_PATH_LIST_NAMESPACE_URI =
        NS_URI_PREFIX + "organizationalunitpathlist"
            + NS_URI_SCHEMA_VERSION_0_4;

    public static final String ORGANIZATIONAL_UNIT_SUCCESSORS_LIST_NAMESPACE_URI =
        NS_URI_PREFIX + "organizationalunitsuccessors"
            + NS_URI_SCHEMA_VERSION_0_7;

    public static final String ORGANIZATIONAL_UNIT_REFS_NAMESPACE_URI =
        NS_URI_PREFIX + "organizationalunitrefslist"
            + NS_URI_SCHEMA_VERSION_0_4;

    public static final String ORGANIZATIONAL_UNIT_REF_NAMESPACE_URI =
        NS_URI_PREFIX + "organizationalunitref" + NS_URI_SCHEMA_VERSION_0_4;

    /**
     * Base-URLs.
     * 
     * @common
     */
    public static final String ITEM_URL_BASE = "/ir/item/";

    public static final String COMPONENT_URL_PART = "/components/component/";

    public static final String CONTEXT_URL_BASE = "/ir/context/";

    public static final String CONTENT_MODEL_URL_BASE = "/cmm/content-model/";

    public static final String CONTENT_RELATION_URL_BASE =
        "/ir/content-relation/";

    public static final String USER_ACCOUNT_URL_BASE = "/aa/user-account/";

    public static final String CONTAINER_URL_BASE = "/ir/container/";

    public static final String ORGANIZATIONAL_UNIT_URL_BASE =
        "/oum/organizational-unit/";

    public static final String MD_RECORDS_URL_PART = "/md-records";

    public static final String MD_RECORD_URL_PART = MD_RECORDS_URL_PART
        + "/md-record";

    public static final String SCHEMA_LOCATION_BASE =
        "http://www.escidoc.org/schemas";

    /**
     * Object-types.
     * 
     * @common
     */
    public static final String CONTAINER_OBJECT_TYPE = RESOURCES_NS_URI
        + "Container";

    public static final String CONTEXT_OBJECT_TYPE = RESOURCES_NS_URI
        + "Context";

    public static final String CONTENT_MODEL_OBJECT_TYPE = RESOURCES_NS_URI
        + "ContentModel";

    @Deprecated
    public static final String CONTENT_RELATION_OBJECT_TYPE = RESOURCES_NS_URI
        + "Relation";

    public static final String CONTENT_RELATION2_OBJECT_TYPE = RESOURCES_NS_URI
        + "ContentRelation";

    public static final String COMPONENT_OBJECT_TYPE = RESOURCES_NS_URI
        + "Component";

    public static final String ITEM_OBJECT_TYPE = RESOURCES_NS_URI + "Item";

    public static final String ORGANIZATIONAL_UNIT_OBJECT_TYPE =
        RESOURCES_NS_URI + "OrganizationalUnit";

    public static final String RELATION_OBJECT_TYPE = RESOURCES_NS_URI
        + "Relation";

    public static final String ROLE_OBJECT_TYPE = RESOURCES_NS_URI + "Role";

    public static final String SCOPE_OBJECT_TYPE = RESOURCES_NS_URI + "Scope";

    public static final String USER_ACCOUNT_OBJECT_TYPE = RESOURCES_NS_URI
        + "UserAccount";

    public static final String USER_GROUP_OBJECT_TYPE = RESOURCES_NS_URI
        + "UserGroup";

    /**
     * Stati.
     * 
     * @common
     */
    public static final String STATUS_PENDING = "pending";

    public static final String STATUS_SUBMITTED = "submitted";

    public static final String STATUS_IN_REVISION = "in-revision";

    public static final String STATUS_RELEASED = "released";

    public static final String STATUS_WITHDRAWN = "withdrawn";

    public static final String STATUS_LOCKED = "locked";

    public static final String STATUS_UNLOCKED = "unlocked";

    public static final String STATUS_VALID = "valid";

    public static final String STATUS_INVALID = "invalid";

    public static final String STATUS_CONTEXT_CREATED = "created";

    public static final String STATUS_CONTEXT_OPENED = "opened";

    public static final String STATUS_CONTEXT_CLOSED = "closed";

    public static final String STATUS_OU_CREATED = "created";

    public static final String STATUS_OU_OPENED = "opened";

    public static final String STATUS_OU_CLOSED = "closed";

    public static final String STATUS_RELATION_ACTIVE = "active";

    public static final String STATUS_RELATION_INACTIVE = "inactive";

    public static final String PREMIS_ID_TYPE_ESCIDOC = "escidoc-internal";

    public static final String PREMIS_ID_TYPE_URL_RELATIVE = "URL";

    /**
     * user-group member types.
     * 
     * @common
     */
    public static final String TYPE_USER_GROUP_MEMBER_INTERNAL = "internal";

    public static final String TYPE_USER_GROUP_MEMBER_USER_ATTRIBUTE =
        "user-attribute";

    /**
     * user-group member names.
     * 
     * @common
     */
    public static final String NAME_USER_GROUP_MEMBER_USER_ACCOUNT =
        "user-account";

    public static final String NAME_USER_GROUP_MEMBER_USER_GROUP = "user-group";

    public static final String NAME_USER_GROUP_MEMBER_ORG_UNIT =
        "organizational-unit";

    /**
     * SRU request parameter names.
     */
    public static final String SRU_PARAMETER_OPERATION = "operation";

    public static final String SRU_PARAMETER_QUERY = "query";

    public static final String SRU_PARAMETER_START_RECORD = "startRecord";

    public static final String SRU_PARAMETER_MAXIMUM_RECORDS = "maximumRecords";

    public static final String SRU_PARAMETER_EXPLAIN = "explain";

    public static final String SRU_PARAMETER_RECORD_SCHEMA = "recordSchema";

    public static final String SRU_PARAMETER_ROLE = "x-info5-roleId";

    public static final String SRU_PARAMETER_USER = "x-info5-userId";

    public static final String SRU_PARAMETER_VERSION = "version";

    /**
     * Filter.
     */
    public static final String FILTER_USER = Constants.PROPERTIES_NS_URI
        + "user";

    public static final String FILTER_GROUP = Constants.PROPERTIES_NS_URI
        + "group";

    public static final String FILTER_LABEL = Constants.PROPERTIES_NS_URI
        + "label";

    public static final String FILTER_EMAIL = Constants.PROPERTIES_NS_URI
        + "email";

    public static final String FILTER_ROLE = Constants.PROPERTIES_NS_URI
        + "role";

    public static final String FILTER_ASSIGNED_ON = Constants.PROPERTIES_NS_URI
        + "assigned-on";

    public static final String FILTER_REVOCATION_DATE =
        Constants.PROPERTIES_NS_URI + "revocation-date";

    public static final String FILTER_REVOCATION_DATE_FROM =
        Constants.PROPERTIES_NS_URI + "revocation-date-from";

    public static final String FILTER_REVOCATION_DATE_TO =
        Constants.PROPERTIES_NS_URI + "revocation-date-to";

    public static final String FILTER_CREATION_DATE =
        Constants.PROPERTIES_NS_URI + "creation-date";

    public static final String FILTER_CREATION_DATE_FROM =
        Constants.PROPERTIES_NS_URI + "creation-date-from";

    public static final String FILTER_CREATION_DATE_TO =
        Constants.PROPERTIES_NS_URI + "creation-date-to";

    public static final String FILTER_GRANTED_FROM =
        Constants.PROPERTIES_NS_URI + "granted-from";

    public static final String FILTER_GRANTED_TO = Constants.PROPERTIES_NS_URI
        + "granted-to";

    public static final String FILTER_CREATED_BY = Constants.PROPERTIES_NS_URI
        + "created-by";

    public static final String FILTER_REVOKED_BY = Constants.PROPERTIES_NS_URI
        + "revoked-by";

    /**
     * Message Queue Parameters.
     * 
     * @common
     */
    public static final String INDEXER_QUEUE_ACTION_PARAMETER = "action";

    public static final String INDEXER_QUEUE_RESOURCE_PARAMETER = "resource";

    public static final String INDEXER_QUEUE_REINDEXER_CALLER =
        "reindexerCaller";

    public static final String INDEXER_QUEUE_OBJECT_TYPE_PARAMETER =
        "objectType";

    public static final String INDEXER_QUEUE_ACTION_PARAMETER_DELETE_VALUE =
        "delete";

    public static final String INDEXER_QUEUE_ACTION_PARAMETER_UPDATE_VALUE =
        "update";

    public static final String INDEXER_QUEUE_ACTION_PARAMETER_CREATE_EMPTY_VALUE =
        "create-empty";

    public static final String INDEXER_QUEUE_PARAMETER_INDEX_NAME = "indexName";

    public static final String STATISTIC_QUEUE_DATA_PARAMETER = "data";

    // Ingest related
    public static final String INGEST_OBJ_ID = "objid";

    public static final String INGEST_RESOURCE_TYPE = "resourceType";

    /**
     * Global AA Parameters.
     * 
     * @common
     */
    public static final String UNRESOLVED_ATTRIBUTE_VALUE = "unresolved";

    /**
     * Logfiles.
     * 
     * @common
     */
    public static final String INDEXING_ERROR_LOGFILE = "indexer-errors";

    public static final String STATISTIC_ERROR_LOGFILE = "statistics-errors";

    public static final String STATISTIC_PREPROCESSING_ERROR_LOGFILE =
        "statistics-preprocessing-errors";

    public static final String COLON_REPLACEMENT_PID = "_";

    /*
     * Forms of Predecessors
     */
    public static final String PREDECESSOR_REPLACEMENT =
        Constants.STRUCTURAL_RELATIONS_NS_URI + "replacement";

    public static final String PREDECESSOR_SPLITTING =
        Constants.STRUCTURAL_RELATIONS_NS_URI + "splitting";

    public static final String PREDECESSOR_AFFILIATION =
        Constants.STRUCTURAL_RELATIONS_NS_URI + "affiliation";

    public static final String PREDECESSOR_SPIN_OFF =
        Constants.STRUCTURAL_RELATIONS_NS_URI + "spin-off";

    public static final String PREDECESSOR_FUSION =
        Constants.STRUCTURAL_RELATIONS_NS_URI + "fusion";

    /**
     * Do not instantiate this class.
     */
    private Constants() {
    }
}
