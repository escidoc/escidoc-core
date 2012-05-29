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

package org.escidoc.core.util.xml.internal;

import java.util.HashMap;
import java.util.Map;

import de.escidoc.core.common.util.configuration.EscidocConfiguration;

/**
 * Helper class to get schema locations.
 * 
 * @author Michael Hoppe
 */
public final class SchemaUtility {

    public static final String SCHEMA_BASE = EscidocConfiguration.getInstance().get(
        EscidocConfiguration.ESCIDOC_CORE_XSD_PATH)
        + "/";
    public static final String ITEM_SCHEMA_LOCATION_NAME = "itemSchemaLocation";
    public static final String CONTAINER_SCHEMA_LOCATION_NAME = "containerSchemaLocation";
    public static final String CONTEXT_SCHEMA_LOCATION_NAME = "contextSchemaLocation";
    public static final String ORGANIZATIONAL_UNIT_SCHEMA_LOCATION_NAME = "organizationalUnitSchemaLocation";
    public static final String CONTENT_RELATION_SCHEMA_LOCATION_NAME = "contentRelationSchemaLocation";
    public static final String CONTENT_MODEL_SCHEMA_LOCATION_NAME = "contentModelSchemaLocation";
    
    public static final Map<String, String> SCHEMA_LOCATIONS = new HashMap<String, String>() {
        {
            put("componentsSchemaLocation", SCHEMA_BASE + "components.xsd");
            put("versionSchemaLocation", SCHEMA_BASE + "version.xsd");
            put("resultSchemaLocation", SCHEMA_BASE + "result.xsd");
            put("scopeSchemaLocation", SCHEMA_BASE + "scope.xsd");
            put("pdpResultsSchemaLocation", SCHEMA_BASE + "pdp-results.xsd");
            put("deleteObjectsTaskParamSchemaLocation", SCHEMA_BASE + "delete-objects-task-param.xsd");
            put("jhoveSchemaLocation", SCHEMA_BASE + "jhove.xsd");
            put("xcqlSchemaLocation", SCHEMA_BASE + "xcql.xsd");
            put("stagingFileSchemaLocation", SCHEMA_BASE + "staging-file.xsd");
            put("exceptionSchemaLocation", SCHEMA_BASE + "exception.xsd");
            put("structMapSchemaLocation", SCHEMA_BASE + "struct-map.xsd");
            put("EventV11SchemaLocation", SCHEMA_BASE + "Event-v1-1.xsd");
            put("reportParametersSchemaLocation", SCHEMA_BASE + "report-parameters.xsd");
            put("reportSchemaLocation", SCHEMA_BASE + "report.xsd");
            put("grantsSchemaLocation", SCHEMA_BASE + "grants.xsd");
            put("revokeGrantTaskParamSchemaLocation", SCHEMA_BASE + "revoke-grant-task-param.xsd");
            put("javautilpropertiesSchemaLocation", SCHEMA_BASE + "javautilproperties.xsd");
            put("userAccountAttributesSchemaLocation", SCHEMA_BASE + "user-account-attributes.xsd");
            put("tmeRequestSchemaLocation", SCHEMA_BASE + "tme-request.xsd");
            put("organizationalUnitRefSchemaLocation", SCHEMA_BASE + "organizational-unit-ref.xsd");
            put("contentRelationSchemaLocation", SCHEMA_BASE + "content-relation.xsd");
            put("mdRecordsSchemaLocation", SCHEMA_BASE + "md-records.xsd");
            put("commonTypesSchemaLocation", SCHEMA_BASE + "common-types.xsd");
            put("containerListSchemaLocation", SCHEMA_BASE + "container-list.xsd");
            put("contextSchemaLocation", SCHEMA_BASE + "context.xsd");
            put("predicateListSchemaLocation", SCHEMA_BASE + "predicate-list.xsd");
            put("contentModelSchemaLocation", SCHEMA_BASE + "content-model.xsd");
            put("idSetTaskParamSchemaLocation", SCHEMA_BASE + "id-set-task-param.xsd");
            put("contextListSchemaLocation", SCHEMA_BASE + "context-list.xsd");
            put("roleListSchemaLocation", SCHEMA_BASE + "role-list.xsd");
            put("pdpRequestsSchemaLocation", SCHEMA_BASE + "pdp-requests.xsd");
            put("userAccountListSchemaLocation", SCHEMA_BASE + "user-account-list.xsd");
            put("userGroupListSchemaLocation", SCHEMA_BASE + "user-group-list.xsd");
            put("roleSchemaLocation", SCHEMA_BASE + "role.xsd");
            put("reportDefinitionSchemaLocation", SCHEMA_BASE + "report-definition.xsd");
            put("statisticDataSchemaLocation", SCHEMA_BASE + "statistic-data.xsd");
            put("releaseSchemaLocation", SCHEMA_BASE + "release.xsd");
            put("userAccountSchemaLocation", SCHEMA_BASE + "user-account.xsd");
            put("removeSelectorsSchemaLocation", SCHEMA_BASE + "remove-selectors.xsd");
            put("csXacmlSchemaContext01SchemaLocation", SCHEMA_BASE + "cs-xacml-schema-context-01.xsd");
            put("indexConfigurationSchemaLocation", SCHEMA_BASE + "index-configuration.xsd");
            put("optimisticLockingTaskParamSchemaLocation", SCHEMA_BASE + "optimistic-locking-task-param.xsd");
            put("addSelectorsSchemaLocation", SCHEMA_BASE + "add-selectors.xsd");
            put("revokeGrantsTaskParamSchemaLocation", SCHEMA_BASE + "revoke-grants-task-param.xsd");
            put("contentStreamsSchemaLocation", SCHEMA_BASE + "content-streams.xsd");
            put("relationsSchemaLocation", SCHEMA_BASE + "relations.xsd");
            put("membersTaskParamSchemaLocation", SCHEMA_BASE + "members-task-param.xsd");
            put("organizationalUnitPathListSchemaLocation", SCHEMA_BASE + "organizational-unit-path-list.xsd");
            put("srwTypesSchemaLocation", SCHEMA_BASE + "srw-types.xsd");
            put("structuralRelationsSchemaLocation", SCHEMA_BASE + "structural-relations.xsd");
            put("organizationalUnitSuccessorsSchemaLocation", SCHEMA_BASE + "organizational-unit-successors.xsd");
            put("itemListSchemaLocation", SCHEMA_BASE + "item-list.xsd");
            put("aggregationDefinitionListSchemaLocation", SCHEMA_BASE + "aggregation-definition-list.xsd");
            put("userGroupSchemaLocation", SCHEMA_BASE + "user-group.xsd");
            put("scopeListSchemaLocation", SCHEMA_BASE + "scope-list.xsd");
            put("userAccountPreferencesSchemaLocation", SCHEMA_BASE + "user-account-preferences.xsd");
            put("relationTaskParamSchemaLocation", SCHEMA_BASE + "relation-task-param.xsd");
            put("aggregationDefinitionSchemaLocation", SCHEMA_BASE + "aggregation-definition.xsd");
            put("assignPidTaskParamSchemaLocation", SCHEMA_BASE + "assign-pid-task-param.xsd");
            put("statusTaskParamSchemaLocation", SCHEMA_BASE + "status-task-param.xsd");
            put("xmlSchemaLocation", SCHEMA_BASE + "xml.xsd");
            put("setDefinitionSchemaLocation", SCHEMA_BASE + "set-definition.xsd");
            put("organizationalUnitSchemaLocation", SCHEMA_BASE + "organizational-unit.xsd");
            put("permissionFilterSchemaLocation", SCHEMA_BASE + "permission-filter.xsd");
            put("diagnosticsSchemaLocation", SCHEMA_BASE + "diagnostics.xsd");
            put("preprocessingInformationSchemaLocation", SCHEMA_BASE + "preprocessing-information.xsd");
            put("propertiesSchemaLocation", SCHEMA_BASE + "properties.xsd");
            put("xlinkSchemaLocation", SCHEMA_BASE + "xlink.xsd");
            put("versionHistorySchemaLocation", SCHEMA_BASE + "version-history.xsd");
            put("csXacmlSchemaPolicy01SchemaLocation", SCHEMA_BASE + "cs-xacml-schema-policy-01.xsd");
            put("containerSchemaLocation", SCHEMA_BASE + "container.xsd");
            put("srwExtraDataSchemaLocation", SCHEMA_BASE + "srw-extra-data.xsd");
            put("reportDefinitionListSchemaLocation", SCHEMA_BASE + "report-definition-list.xsd");
            put("itemSchemaLocation", SCHEMA_BASE + "item.xsd");
            put("organizationalUnitListSchemaLocation", SCHEMA_BASE + "organizational-unit-list.xsd");
            put("reindexTaskParamSchemaLocation", SCHEMA_BASE + "reindex-task-param.xsd");
            put("updatePasswordTaskParamSchemaLocation", SCHEMA_BASE + "update-password-task-param.xsd");
        }
    };

    /**
     * Protected constructor to prevent instantiation outside of the Spring-context.
     */
    protected SchemaUtility() {
    }

}
