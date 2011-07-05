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

/**
 * Handles XML Template mapping.
 *
 * @author Steffen Wagner
 */
public final class XmlTemplateProviderConstants {

    /*
     * Switches to control templates.
     */
    public static final String TRUE = "true";

    public static final String FALSE = "false";

    public static final String IN_CREATE = "IN_CREATE";

    protected static final String ESCAPER = "esc";

    public static final String DEFAULT_METADATA_FOR_DC_MAPPING = "escidoc";

    public static final String IS_ROOT = "isRoot";

    public static final String IS_ROOT_LIST = "isRootList";

    public static final String IS_ROOT_PROPERTIES = "isRootProperties";

    public static final String IS_ROOT_RESOURCES = "isRootResources";

    public static final String IS_ROOT_SUB_RESOURCE = "isRootSubResource";

    public static final String IS_ROOT_MD_RECORD = "isRootMdRecord";

    public static final String PLACEHOLDER = "---";

    /*
     * Values to control the Framework
     */
    public static final String FRAMEWORK_BUILD_NUMBER = "BUILD_NUMBER";

    /*
     * Common values
     */
    protected static final String LOCK_STATUS = "LOCK_STATUS";

    public static final String LOCK_OWNER = "LOCK_OWNER";

    /*
     * Common Object Types
     */
    public static final String OBJID = "OBJID";

    public static final String OBJID_UNDERSCORE = "OBJID_UNDERSCORE";

    public static final String TIMESTAMP = "TIMESTAMP";

    // TODO: this is strange and should be removed
    public static final String TIMESTAMP_PLACEHOLDER = "###TIMESTAMP###";

    public static final String HREF = "HREF";

    public static final String TITLE = "TITLE";

    public static final String DESCRIPTION = "DESCRIPTION";

    public static final String CREATED_BY_ID = "CREATED_BY_ID";

    protected static final String CREATED_BY_HREF = "CREATED_BY_HREF";

    public static final String CREATED_BY_TITLE = "CREATED_BY_TITLE";

    public static final String MD_RECORD_NAME = "MD_RECORD_NAME";

    public static final String MD_RECORD_SCHEMA = "MD_RECORD_SCHEMA";

    public static final String MD_RECORD_TYPE = "MD_RECORD_TYPE";

    public static final String MD_RECORD_CONTENT = "MD_RECORD_CONTENT";

    public static final String PUBLIC_STATUS = "PUBLIC_STATUS";

    public static final String PUBLIC_STATUS_COMMENT = "PUBLIC_STATUS_COMMENT";

    public static final String OBJECT_PID = "OBJECT_PID";

    public static final String BUILD_NUMBER = "build";

    /*
     * Common Version Types
     */
    public static final String MODIFIED_BY_ID = "MODIFIED_BY_ID";

    protected static final String MODIFIED_BY_HREF = "MODIFIED_BY_HREF";

    public static final String MODIFIED_BY_TITLE = "MODIFIED_BY_TITLE";

    public static final String VERSION_PID = "VERSION_PID";

    public static final String VERSION_NUMBER = "VERSION_NUMBER";

    public static final String VERSION_DATE = "VERSION_DATE";

    public static final String VERSION_STATUS = "VERSION_STATUS";

    public static final String VERSION_COMMENT = "VERSION_COMMENT";

    /*
     * Latest Version Types
     */
    public static final String LATEST_VERSION_PID = "LATEST_VERSION_PID";

    public static final String LATEST_VERSION_NUMBER = "LATEST_VERSION_NUMBER";

    public static final String LATEST_VERSION_DATE = "LATEST_VERSION_DATE";

    public static final String LATEST_VERSION_STATUS = "LATEST_VERSION_STATUS";

    public static final String LATEST_VERSION_COMMENT = "LATEST_VERSION_COMMENT";

    /*
     * Item/Container
     */
    public static final String CONTEXT_TITLE = "CONTEXT_TITLE";

    public static final String CONTEXT_ID = "CONTEXT_ID";

    public static final String CONTENT_MODEL_ID = "CONTENT_MODEL_ID";

    public static final String CONTENT_MODEL_TITLE = "CONTENT_MODEL_TITLE";

    public static final String CONTAINER_CONTENT_MODEL_SPECIFIC = "containerCms";

    /*
     * Behavior
     */
    public static final String BEHAVIOR_CONTENT_MODEL_ID = "behaviorContentModelId";

    public static final String BEHAVIOR_CONTENT_MODEL_ID_UNDERSCORE = "behaviorContentModelIdUnderscore";

    public static final String BEHAVIOR_OPERATION_NAME = "behaviorOperationName";

    public static final String BEHAVIOR_TRANSFORM_MD = "behaviorTransformMd";

    public static final String BEHAVIOR_XSLT_HREF = "behaviorXsltHref";

    /*
     * Component
     */
    public static final String VALID_STATUS = "VALID_STATUS";

    public static final String MIME_TYPE = "MIME_TYPE";

    public static final String VISIBILITY = "VISIBILITY";

    public static final String CONTENT_CATEGORY = "CONTENT_CATEGORY";

    public static final String CONTENT_CHECKSUM_ALGORITHM = "CHECKSUM_ALGORITHM";

    public static final String CONTENT_CHECKSUM = "CHECKSUM";

    public static final String REF = "REF";

    public static final String REF_TYPE = "REF_TYPE";

    /*
     * Organizational Unit
     */
    public static final String PREDECESSORS = "PREDECESSORS";

    public static final String PREDECESSORS_TITLE = "PREDECESSORS_TITLE";

    public static final String PREDECESSORS_HREF = "PREDECESSORS_HREF";

    public static final String PREDECESSOR = "PREDECESSOR";

    public static final String PREDECESSOR_FORM = "FORM";

    public static final String SUCCESSORS = "SUCCESSORS";

    public static final String SUCCESSORS_TITLE = "SUCCESSORS_TITLE";

    public static final String SUCCESSORS_HREF = "SUCCESSORS_HREF";

    public static final String SUCCESSOROR = "SUCCESSOR";

    public static final String SUCCESSOR_FORM = "FORM";

    /*
     * Stream
     */
    public static final String CONTROL_GROUP = "CONTROL_GROUP";

    public static final String DS_VERSIONABLE = "DS_VESIONABLE";

    public static final String COMPONENTS = "COMPONENTS";

    public static final String RELS_EXT = "RELS_EXT";

    public static final String DC = "DC";

    public static final String MD_RECORDS = "MD_RECORDS";

    public static final String CONTENT_MODEL_SPECIFIC = "CONTENT_MODEL_SPECIFIC";

    public static final String CONTENT_STREAMS = "CONTENT_STREAMS";

    public static final String CONTENT_RELATIONS = "CONTENT_RELATIONS";

    public static final String CONTENT_RELATION = "CONTENT_RELATION";

    public static final String PREDICATE = "PREDICATE";

    public static final String PREDICATE_NS = "PREDICATE_NS";

    /*
     * Name Spaces.
     */
    public static final String VAR_NAMESPACE_PREFIX = "NAMESPACE_PREFIX";

    public static final String VAR_NAMESPACE = "NAMESPACE";

    public static final String VAR_XLINK_NAMESPACE_PREFIX = "XLINK_NAMESPACE_PREFIX";

    public static final String VAR_XLINK_NAMESPACE = "XLINK_NAMESPACE";

    public static final String ESCIDOC_PROPERTIES_NS_PREFIX = "escidocPropertiesNamespacePrefix";

    public static final String ESCIDOC_PROPERTIES_NS = "escidocPropertiesNamespace";

    public static final String ESCIDOC_PROPERTIES_VERSION_NS_PREFIX = "escidocPropertiesVersionNamespacePrefix";

    public static final String ESCIDOC_PROPERTIES_VERSION_NS = "escidocPropertiesVersionNamespace";

    public static final String ESCIDOC_PROPERTIES_RELEASE_NS_PREFIX = "escidocPropertiesReleaseNamespacePrefix";

    public static final String ESCIDOC_PROPERTIES_RELEASE_NS = "escidocPropertiesReleaseNamespace";

    public static final String ESCIDOC_RESOURCE_NS = "escidocResourcesNamespace";

    public static final String ESCIDOC_RESOURCE_NS_PREFIX = "escidocRelationsNamespacePrefix";

    public static final String ESCIDOC_RELATION_NS = "escidocRelationsNamespace";

    public static final String ESCIDOC_RELATION_NS_PREFIX = "contentRelationsNamespacePrefix";

    public static final String ESCIDOC_ORIGIN_NS = "escidocOriginNamespace";

    public static final String ESCIDOC_ORIGIN_NS_PREFIX = "escidocOriginNamespacePrefix";

    public static final String ESCIDOC_RELEASE_NS_PREFIX = "escidocPropertiesReleaseNamespacePrefix";

    public static final String ESCIDOC_RELEASE_NS = "escidocPropertiesReleaseNamespace";

    public static final String ESCIDOC_SREL_NS_PREFIX = "structuralRelationsNamespacePrefix";

    public static final String ESCIDOC_SREL_NS = "structuralRelationsNamespace";

    public static final String ESCIDOC_PARAMETER_NS_PREFIX = "parameterNamespacePrefix";

    public static final String ESCIDOC_PARAMETER_NS = "parameterNamespace";

    protected static final String BASE_TEMPLATE_PATH = "/de/escidoc/core/common/util/xml/factory/templates";

    protected static final String CONTENT_RELATION_NAMESPACE_PREFIX = "contentRelationNamespacePrefix";

    protected static final String CONTENT_RELATION_NAMESPACE = "contentRelationNamespace";

    public static final String MD_RECRORDS_NAMESPACE_PREFIX = "mdRecordsNamespacePrefix";

    public static final String MD_RECORDS_NAMESPACE = "mdRecordsNamespace";

    /*
     * TODO make name consistent and fit into the order
     */
    public static final String VAR_ESCIDOC_BASE_URL = "ESCIDOC_BASE_URL";

    public static final String VAR_LAST_MODIFICATION_DATE = "LAST_MODIFICATION_DATE";

    public static final String VAR_CREATION_DATE = "CREATION_DATE";

    public static final String VAR_DESCRIPTION = "DESCRIPTION";

    public static final String VAR_RESOURCES_HREF = "RESOURCES_HREF";

    public static final String RESOURCES_TITLE = "resourcesTitle";

    public static final String VAR_XACML_POLICY_NAMESPACE_PREFIX = "XACML_POLICY_NAMESPACE_PREFIX";

    public static final String VAR_XACML_POLICY_NAMESPACE = "XACML_POLICY_NAMESPACE";

    public static final String VAR_AGENT_ID_VALUE = "AGENT_ID_VALUE";

    public static final String VAR_AGENT_ID_TYPE = "AGENT_ID_TYPE";

    public static final String VAR_AGENT_BASE_URI = "AGENT_BASE_URI";

    public static final String VAR_AGENT_TITLE = "AGENT_TITLE";

    public static final String VAR_EVENT_TYPE = "EVENT_TYPE";

    public static final String VAR_EVENT_XMLID = "EVENT_XMLID";

    public static final String VAR_EVENT_ID_VALUE = "EVENT_ID_VALUE";

    public static final String VAR_EVENT_ID_TYPE = "EVENT_ID_TYPE";

    public static final String VAR_OBJECT_ID_TYPE = "OBJECT_ID_TYPE";

    public static final String VAR_OBJECT_ID_VALUE = "OBJECT_ID_VALUE";

    // TODO check this
    public static final String VAR_ID = "ID";

    public static final String VAR_VERSIONABLE = "VERSIONABLE";

    public static final String VAR_ALT_IDS = "ALT_IDS";

    public static final String VAR_NAME = "NAME";

    public static final String VAR_LABEL = "LABEL";

    public static final String VAR_URL = "URL";

    public static final String VAR_CONTENT = "content";

    public static final String VAR_COMMENT = "COMMENT";

    public static final String VAR_STATUS = "STATUS";

    public static final String VAR_CURRENT_VERSION_PID = "CURRENT_VERSION_PID";

    public static final String LATEST_RELEASE_DATE = "LATEST_RELEASE_DATE";

    public static final String LATEST_RELEASE_NUMBER = "LATEST_RELEASE_NUMBER";

    public static final String VAR_LOCATOR_URL = "LOCATOR_URL";

    public static final String VAR_COMPONENT_PID = "componentPid";

    public static final String VAR_COMPONENTS_CONTENT = "componentsContent";

    public static final String VAR_MD_RECORDS_CONTENT = "mdRecordsContent";

    public static final String VAR_MD_RECORDS_TITLE = "mdRecordsTitle";

    public static final String VAR_MD_RECORDS_HREF = "mdRecordsHref";

    public static final String VAR_MD_RECORD_TITLE = "mdRecordTitle";

    public static final String VAR_MD_RECORD_HREF = "mdRecordHref";

    public static final String VAR_CONTENT_STREAMS_CONTENT = "contentStreamsContent";

    public static final String VAR_CONTENT_STREAMS_TITLE = "contentStreamsTitle";

    public static final String VAR_CONTENT_STREAMS_HREF = "contentStreamsHref";

    public static final String VAR_CONTENT_STREAM_NAME = "contentStreamName";

    public static final String VAR_CONTENT_STREAM_TITLE = "contentStreamTitle";

    public static final String VAR_CONTENT_STREAM_HREF = "contentStreamHref";

    public static final String VAR_CONTENT_STREAM_CONTENT = "contentStreamContent";

    public static final String VAR_CONTENT_STREAM_STORAGE = "contentStreamStorage";

    public static final String VAR_CONTENT_STREAM_MIME_TYPE = "contentStreamMimeType";

    /*
     * FIXME it seems that is a copy and paste failure or a hot fix
     */
    public static final String VAR_WITHDRAWAL_DATE = "WITHDRAWAL_COMMENT";

    public static final String VAR_WITHDRAWAL_COMMENT = "WITHDRAWAL_DATE";

    public static final String STORAGE = "STORAGE";

    public static final String VAR_ITEM_LIST_CONTENT = "itemListContent";

    public static final String VAR_ITEM_LIST_MEMBERS = "itemListMembers";

    public static final String VAR_ITEM_LIST_TITLE = "itemListTitle";

    public static final String VAR_ITEM_LIST_NAMESPACE = "itemListNamespace";

    public static final String VAR_ITEM_LIST_NAMESPACE_PREFIX = "itemListNamespacePrefix";

    public static final String VAR_PROPERTIES_TITLE = "propertiesTitle";

    public static final String VAR_PROPERTIES_HREF = "propertiesHref";

    public static final String VAR_ITEM_CREATION_DATE = "itemCreationDate";

    public static final String VAR_ITEM_STATUS = "itemStatus";

    public static final String VAR_ITEM_STATUS_COMMENT = "itemStatusComment";

    public static final String VAR_CONTAINER_STATUS_COMMENT = "containerStatusComment";

    public static final String VAR_ITEM_CREATED_BY_TITLE = "itemCreatedByTitle";

    public static final String VAR_ITEM_CREATED_BY_HREF = "itemCreatedByHref";

    public static final String VAR_ITEM_CREATED_BY_ID = "itemCreatedById";

    public static final String VAR_ITEM_CONTEXT_TITLE = "itemContextTitle";

    public static final String VAR_ITEM_CONTEXT_HREF = "itemContextHref";

    public static final String ORIGIN = "ORIGIN";

    public static final String VAR_ORIGIN_ID = "originId";

    public static final String VAR_ORIGIN_OBJECT_ID = "originObjectId";

    public static final String VAR_ORIGIN_VERSION_ID = "originVersionId";

    public static final String VAR_ITEM_ORIGIN_HREF = "itemOriginHref";

    public static final String VAR_ITEM_ORIGIN_TITLE = "itemOriginTitle";

    public static final String VAR_ITEM_CONTEXT_ID = "itemContextId";

    public static final String VAR_ITEM_CONTENT_MODEL_TITLE = "itemContentModelTitle";

    public static final String VAR_ITEM_CONTENT_MODEL_HREF = "itemContentModelHref";

    public static final String VAR_ITEM_CONTENT_MODEL_ID = "itemContentModelId";

    public static final String VAR_ITEM_LOCK_STATUS = "itemLockStatus";

    public static final String VAR_ITEM_LOCK_DATE = "itemLockDate";

    public static final String VAR_ITEM_LOCK_OWNER_ID = "itemLockOwnerId";

    public static final String VAR_ITEM_LOCK_OWNER_TITLE = "itemLockOwnerTitle";

    public static final String VAR_ITEM_LOCK_OWNER_HREF = "itemLockOwnerHref";

    public static final String VAR_ITEM_OBJECT_PID = "itemObjectPid";

    public static final String VAR_ITEM_CURRENT_VERSION_HREF = "itemVersionHref";

    public static final String VAR_ITEM_CURRENT_VERSION_ID = "itemVersionId";

    public static final String VAR_ITEM_CURRENT_VERSION_TITLE = "itemVersionTitle";

    public static final String VAR_ITEM_CURRENT_VERSION_NUMBER = "itemVersionNumber";

    public static final String VAR_ITEM_CURRENT_VERSION_DATE = "itemVersionDate";

    public static final String VAR_ITEM_CURRENT_VERSION_STATUS = "itemVersionStatus";

    public static final String VAR_ITEM_CURRENT_VERSION_VALID_STATUS = "itemVersionValidStatus";

    public static final String VAR_ITEM_CURRENT_VERSION_COMMENT = "itemVersionComment";

    public static final String VAR_ITEM_CURRENT_VERSION_MODIFIED_BY_ID = "itemVersionModifiedById";

    public static final String VAR_ITEM_CURRENT_VERSION_MODIFIED_BY_TITLE = "itemVersionModifiedByTitle";

    public static final String VAR_ITEM_CURRENT_VERSION_MODIFIED_BY_HREF = "itemVersionModifiedByHref";

    public static final String VAR_ITEM_LATEST_VERSION_ID = "itemLatestVersionId";

    public static final String VAR_ITEM_LATEST_VERSION_HREF = "itemLatestVersionHref";

    public static final String VAR_ITEM_LATEST_VERSION_TITLE = "itemLatestVersionTitle";

    public static final String VAR_ITEM_LATEST_VERSION_NUMBER = "itemLatestVersionNumber";

    public static final String VAR_ITEM_LATEST_VERSION_DATE = "itemLatestVersionDate";

    public static final String VAR_ITEM_LATEST_RELEASE_NUMBER = "itemLatestReleaseNumber";

    public static final String VAR_ITEM_LATEST_RELEASE_HREF = "itemLatestReleaseHref";

    public static final String VAR_ITEM_LATEST_RELEASE_TITLE = "itemLatestReleaseTitle";

    public static final String VAR_ITEM_LATEST_RELEASE_ID = "itemLatestReleaseId";

    public static final String VAR_ITEM_LATEST_RELEASE_DATE = "itemLatestReleaseDate";

    public static final String LATEST_RELEASE_PID = "LATEST_RELEASE_PID";

    public static final String VAR_ITEM_LATEST_RELEASE_PID = "itemLatestReleasePid";

    public static final String VAR_ITEM_VERSION_PID = "itemVersionPid";

    public static final String VAR_ITEM_CONTENT_MODEL_SPECIFIC = "itemContentModelSpecific";

    public static final String VAR_COMPONENT_PROPERTIES_TITLE = "componentPropertiesTitle";

    public static final String VAR_COMPONENT_PROPERTIES_HREF = "componentPropertiesHref";

    public static final String VAR_COMPONENT_DESCRIPTION = "componentDescription";

    public static final String VAR_COMPONENT_CREATION_DATE = "componentCreationDate";

    public static final String VAR_COMPONENT_CREATED_BY_TITLE = "componentCreatedByTitle";

    public static final String VAR_COMPONENT_CREATED_BY_HREF = "componentCreatedByHref";

    public static final String VAR_COMPONENT_CREATED_BY_ID = "componentCreatedById";

    public static final String VAR_COMPONENT_STATUS = "componentStatus";

    public static final String VAR_COMPONENT_VALID_STATUS = "componentValidStatus";

    public static final String VAR_COMPONENT_VISIBILITY = "componentVisibility";

    public static final String VAR_COMPONENT_CONTENT_CATEGORY = "componentContentCategory";

    public static final String VAR_COMPONENT_MIME_TYPE = "componentMimeType";

    public static final String VAR_COMPONENT_FILE_SIZE = "componentFileSize";

    public static final String VAR_COMPONENT_FILE_NAME = "componentFileName";

    public static final String VAR_COMPONENT_LOCATOR_URL = "componentLocatorUrl";

    public static final String VAR_RESOURCES_ONTOLOGIES_NAMESPACE = "resourcesOntologiesNamespace";

    public static final String VAR_STRUCT_RELATIONS_NAMESPACE = "structRelationsNamespace";

    public static final String VAR_PARENTS = "parents";

    // public static final String VAR_CREATED_BY_TITLE = ""modified-by-title"";

    // public static final String VAR_MODIFIED_BY_TITLE = "modified-by-title";

    public static final String VAR_CONTENT_MODEL_CREATION_DATE = "resourceCreationDate";

    public static final String VAR_CONTENT_MODEL_CREATED_BY_TITLE = "resourceCreatedByTitle";

    public static final String VAR_CONTENT_MODEL_CREATED_BY_HREF = "resourceCreatedByHref";

    public static final String VAR_CONTENT_MODEL_CREATED_BY_ID = "resourceCreatedById";

    private static final String VAR_CONTENT_MODEL_PUBLIC_STATUS = "resourcePublicStatus";

    public static final String VAR_CONTENT_MODEL_STATUS = VAR_CONTENT_MODEL_PUBLIC_STATUS;

    private static final String VAR_CONTENT_MODEL_PUBLIC_STATUS_COMMENT = "resourcePublicStatusComment";

    public static final String VAR_CONTENT_MODEL_STATUS_COMMENT = VAR_CONTENT_MODEL_PUBLIC_STATUS_COMMENT;

    public static final String VAR_CONTENT_MODEL_OBJECT_PID = "resourceObjectPid";

    public static final String VAR_CONTENT_MODEL_LOCK_STATUS = "resourceLockStatus";

    public static final String VAR_CONTENT_MODEL_LOCK_DATE = "resourceLockDate";

    public static final String VAR_CONTENT_MODEL_LOCK_OWNER_ID = "resourceLockOwnerId";

    public static final String VAR_CONTENT_MODEL_LOCK_OWNER_HREF = "resourceLockOwnerHref";

    public static final String VAR_CONTENT_MODEL_LOCK_OWNER_TITLE = "resourceLockOwnerTitle";

    public static final String VAR_CONTENT_MODEL_CURRENT_VERSION_HREF = "resourceCurrentVersionHref";

    public static final String VAR_CONTENT_MODEL_CURRENT_VERSION_ID = "resourceCurrentVersionId";

    public static final String VAR_CONTENT_MODEL_CURRENT_VERSION_TITLE = "resourceCurrentVersionTitle";

    public static final String VAR_CONTENT_MODEL_CURRENT_VERSION_NUMBER = "resourceCurrentVersionNumber";

    public static final String VAR_CONTENT_MODEL_CURRENT_VERSION_DATE = "resourceCurrentVersionDate";

    public static final String VAR_CONTENT_MODEL_CURRENT_VERSION_STATUS = "resourceCurrentVersionStatus";

    public static final String VAR_CONTENT_MODEL_CURRENT_VERSION_VALID_STATUS = "resourceCurrentVersionValidStatus";

    public static final String VAR_CONTENT_MODEL_CURRENT_VERSION_COMMENT = "resourceCurrentVersionComment";

    public static final String VAR_CONTENT_MODEL_CURRENT_VERSION_MODIFIED_BY_ID = "resourceCurrentVersionModifiedById";

    public static final String VAR_CONTENT_MODEL_CURRENT_VERSION_MODIFIED_BY_TITLE =
        "resourceCurrentVersionModifiedByTitle";

    public static final String VAR_CONTENT_MODEL_CURRENT_VERSION_MODIFIED_BY_HREF =
        "resourceCurrentVersionModifiedByHref";

    public static final String VAR_CONTENT_MODEL_VERSION_PID = "resourceVersionPid";

    public static final String VAR_CONTENT_MODEL_LATEST_VERSION_HREF = "resourceLatestVersionHref";

    public static final String VAR_CONTENT_MODEL_LATEST_VERSION_TITLE = "resourceLatestVersionTitle";

    public static final String VAR_CONTENT_MODEL_LATEST_VERSION_ID = "resourceLatestVersionId";

    public static final String VAR_CONTENT_MODEL_LATEST_VERSION_NUMBER = "resourceLatestVersionNumber";

    public static final String VAR_CONTENT_MODEL_LATEST_VERSION_DATE = "resourceLatestVersionDate";

    public static final String VAR_CONTENT_MODEL_LATEST_RELEASE_NUMBER = "resourceLatestReleaseNumber";

    public static final String VAR_CONTENT_MODEL_LATEST_RELEASE_HREF = "resourceLatestReleaseHref";

    public static final String VAR_CONTENT_MODEL_LATEST_RELEASE_TITLE = "resourceLatestReleaseTitle";

    public static final String VAR_CONTENT_MODEL_LATEST_RELEASE_ID = "resourceLatestReleaseId";

    public static final String VAR_CONTENT_MODEL_LATEST_RELEASE_DATE = "resourceLatestReleaseDate";

    public static final String VAR_CONTENT_MODEL_LATEST_RELEASE_PID = "resourceLatestReleasePid";

    public static final String VAR_CONTENT_MODEL_NAMESPACE_PREFIX = "resourceNamespacePrefix";

    public static final String VAR_CONTENT_MODEL_NAMESPACE = "resourceNamespace";

    public static final String VAR_CONTENT_MODEL_MDRECORD_DEFINITIONS = "contentModelMdRecordDefinitions";

    public static final String VAR_CONTENT_MODEL_RESOURCE_DEFINITIONS = "contentModelResourceDefinitions";

    public static final String VAR_CONTENT_STREAM_NS_PREFIX = "contentStreamsNamespacePrefix";

    public static final String VAR_CONTENT_STREAM_NS = "contentStreamsNamespace";

    /*
     * Content Relation
     */
    protected static final String CONTENT_RELATION_TYPE = "CONTENT_RELATION_TYPE";

    protected static final String CONTENT_RELATION_DESCRIPTION = "CONTENT_RELATION_DESCRIPTION";

    protected static final String CONTENT_RELATION_SUBJECT_TITLE = "contentRelationSubjectTitle";

    protected static final String CONTENT_RELATION_SUBJECT_HREF = "contentRelationSubjectHref";

    protected static final String CONTENT_RELATION_SUBJECT_ID = "contentRelationSubjectId";

    protected static final String CONTENT_RELATION_OBJECT_TITLE = "contentRelationObjectTitle";

    protected static final String CONTENT_RELATION_OBJECT_HREF = "contentRelationObjectHref";

    protected static final String CONTENT_RELATION_OBJECT_ID = "contentRelationObjectId";

    protected static final String CONTENT_RELATION_SUBJECT_VERSION_NUMBER = "contentRelationSubjectVersion";

    protected static final String CONTENT_RELATION_OBJECT_VERSION_NUMBER = "contentRelationObjectVersion";

    private XmlTemplateProviderConstants() {
    }

}
