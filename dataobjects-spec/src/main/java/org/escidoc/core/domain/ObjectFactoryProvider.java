package org.escidoc.core.domain;

import org.escidoc.core.domain.tme.jhove.ObjectFactory;

/**
 * A provider for less complicated access to the generated ObjectFactory classes. According to the JAXB spec, it is
 * not possible to rename ObjectFactory classes. This class is meant to be instantiated by Spring.
 *
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
public class ObjectFactoryProvider {

    // COMMON
    private static final org.escidoc.core.domain.common.ObjectFactory COMMON_FACTORY =
        new org.escidoc.core.domain.common.ObjectFactory();
    // MD RECORDS
    private static final org.escidoc.core.domain.metadatarecords.ObjectFactory MD_RECORDS_FACTORY =
        new org.escidoc.core.domain.metadatarecords.ObjectFactory();
    // VERSION
    private static final org.escidoc.core.domain.version.event.ObjectFactory EVENT_FACTORY =
        new org.escidoc.core.domain.version.event.ObjectFactory();
    private static final org.escidoc.core.domain.version.ObjectFactory VERSION_FACTORY =
        new org.escidoc.core.domain.version.ObjectFactory();
    private static final org.escidoc.core.domain.version.history.ObjectFactory VERSION_HISTORY_FACTORY =
        new org.escidoc.core.domain.version.history.ObjectFactory();
    // SRU
    private static final org.escidoc.core.domain.sru.ObjectFactory SRU_FACTORY =
        new org.escidoc.core.domain.sru.ObjectFactory();
    private static final org.escidoc.core.domain.sru.diagnostics.ObjectFactory SRU_DIAGNOSTICS_FACTORY =
        new org.escidoc.core.domain.sru.diagnostics.ObjectFactory();
    private static final org.escidoc.core.domain.sru.extradata.ObjectFactory SRU_EXTRA_DATA_FACTORY =
        new org.escidoc.core.domain.sru.extradata.ObjectFactory();
    // TME
    private static final ObjectFactory TME_FACTORY = new ObjectFactory();
    // TASK PARAM
    private static final org.escidoc.core.domain.taskparam.optimisticlocking.ObjectFactory
        OPTIMISTIC_LOCKING_PARAM_FACTORY = new org.escidoc.core.domain.taskparam.optimisticlocking.ObjectFactory();
    // TASK_RESULT
    private static final org.escidoc.core.domain.result.ObjectFactory RESULT_FACTORY =
        new org.escidoc.core.domain.result.ObjectFactory();
    // CONTAINER
    private static final org.escidoc.core.domain.container.ObjectFactory CONTAINER_FACTORY =
        new org.escidoc.core.domain.container.ObjectFactory();
    private static final org.escidoc.core.domain.container.list.ObjectFactory CONTAINER_LIST_FACTORY =
        new org.escidoc.core.domain.container.list.ObjectFactory();
    // STRUCT-MAP
    private static final org.escidoc.core.domain.container.structmap.ObjectFactory STRUCT_MAP_FACTORY =
        new org.escidoc.core.domain.container.structmap.ObjectFactory();
    // ITEM
    private static final org.escidoc.core.domain.item.ObjectFactory ITEM_FACTORY =
        new org.escidoc.core.domain.item.ObjectFactory();
    // COMPONENT
    private static final org.escidoc.core.domain.components.ObjectFactory COMPONENT_FACTORY =
        new org.escidoc.core.domain.components.ObjectFactory();
    // CONTEXT
    private static final org.escidoc.core.domain.context.ObjectFactory CONTEXT_FACTORY =
        new org.escidoc.core.domain.context.ObjectFactory();
    // CONTENT MODEL
    private static final org.escidoc.core.domain.content.model.ObjectFactory CONTENT_MODEL_FACTORY =
        new org.escidoc.core.domain.content.model.ObjectFactory();
    // CONTENT RELATION
    private static final org.escidoc.core.domain.content.relation.ObjectFactory CONTENT_RELATION_FACTORY =
        new org.escidoc.core.domain.content.relation.ObjectFactory();
    // CONTENT STREAM
    private static final org.escidoc.core.domain.content.stream.ObjectFactory CONTENT_STREAM_FACTORY =
        new org.escidoc.core.domain.content.stream.ObjectFactory();
    // USER ACCOUNT
    private static final org.escidoc.core.domain.aa.useraccount.ObjectFactory USER_ACCOUNT_FACTORY =
        new org.escidoc.core.domain.aa.useraccount.ObjectFactory();
    private static final org.escidoc.core.domain.aa.useraccount.list.ObjectFactory USER_ACCOUNT_LIST_FACTORY =
        new org.escidoc.core.domain.aa.useraccount.list.ObjectFactory();
    private static final org.escidoc.core.domain.aa.useraccount.preferences.ObjectFactory
        USER_ACCOUNT_PREFERENCES_FACTORY = new org.escidoc.core.domain.aa.useraccount.preferences.ObjectFactory();
    private static final org.escidoc.core.domain.aa.useraccount.attributes.ObjectFactory
        USER_ACCOUNT_ATTRIBUTES_FACTORY = new org.escidoc.core.domain.aa.useraccount.attributes.ObjectFactory();
    // USER GROUP
    private static final org.escidoc.core.domain.aa.usergroup.ObjectFactory USER_GROUP_FACTORY =
        new org.escidoc.core.domain.aa.usergroup.ObjectFactory();
    private static final org.escidoc.core.domain.aa.usergroup.list.ObjectFactory USER_GROUP_LIST_FACTORY =
            new org.escidoc.core.domain.aa.usergroup.list.ObjectFactory();
    // ROLE
    private static final org.escidoc.core.domain.aa.role.ObjectFactory ROLE_FACTORY =
        new org.escidoc.core.domain.aa.role.ObjectFactory();
    private static final org.escidoc.core.domain.aa.role.list.ObjectFactory ROLE_LIST_FACTORY =
        new org.escidoc.core.domain.aa.role.list.ObjectFactory();
    // GRANT
    private static final org.escidoc.core.domain.aa.grants.ObjectFactory GRANT_FACTORY =
        new org.escidoc.core.domain.aa.grants.ObjectFactory();
    // PDP
    private static final org.escidoc.core.domain.aa.pdp.result.ObjectFactory PDP_FACTORY =
        new org.escidoc.core.domain.aa.pdp.result.ObjectFactory();
    // PREDICATES
    private static final org.escidoc.core.domain.predicate.list.ObjectFactory PREDICATES_FACTORY =
        new org.escidoc.core.domain.predicate.list.ObjectFactory();
    // SM
    private static final org.escidoc.core.domain.sm.scope.ObjectFactory SCOPE_FACTORY =
        new org.escidoc.core.domain.sm.scope.ObjectFactory();
    private static final org.escidoc.core.domain.sm.scope.list.ObjectFactory SCOPE_LIST_FACTORY =
        new org.escidoc.core.domain.sm.scope.list.ObjectFactory();
    private static final org.escidoc.core.domain.sm.ad.ObjectFactory AGGREGATION_DEFINITION_FACTORY =
        new org.escidoc.core.domain.sm.ad.ObjectFactory();
    private static final org.escidoc.core.domain.sm.pi.ObjectFactory PRE_PROCESSING_FACTORY =
        new org.escidoc.core.domain.sm.pi.ObjectFactory();
    private static final org.escidoc.core.domain.sm.rd.ObjectFactory REPORT_DEFINITION_FACTORY =
        new org.escidoc.core.domain.sm.rd.ObjectFactory();
    private static final org.escidoc.core.domain.sm.rd.list.ObjectFactory REPORT_DEFINITION_LIST_FACTORY =
        new org.escidoc.core.domain.sm.rd.list.ObjectFactory();
    private static final org.escidoc.core.domain.sm.report.ObjectFactory REPORT_FACTORY =
        new org.escidoc.core.domain.sm.report.ObjectFactory();
    private static final org.escidoc.core.domain.sm.report.parameter.ObjectFactory REPORT_PARAM_FACTORY =
        new org.escidoc.core.domain.sm.report.parameter.ObjectFactory();
    private static final org.escidoc.core.domain.sm.sd.ObjectFactory STATISTIC_DATA_FACTORY =
        new org.escidoc.core.domain.sm.sd.ObjectFactory();
    // OU
    private static final org.escidoc.core.domain.ou.ObjectFactory OU_FACTORY =
        new org.escidoc.core.domain.ou.ObjectFactory();
    private static final org.escidoc.core.domain.ou.list.ObjectFactory OU_LIST_FACTORY =
        new org.escidoc.core.domain.ou.list.ObjectFactory();
    private static final org.escidoc.core.domain.ou.ref.ObjectFactory OU_REF_FACTORY =
        new org.escidoc.core.domain.ou.ref.ObjectFactory();
    private static final org.escidoc.core.domain.ou.path.list.ObjectFactory OU_PATH_LIST_FACTORY =
        new org.escidoc.core.domain.ou.path.list.ObjectFactory();
    private static final org.escidoc.core.domain.ou.successors.ObjectFactory OU_SUCCESSORS_FACTORY =
        new org.escidoc.core.domain.ou.successors.ObjectFactory();
    // SB
    private static final org.escidoc.core.domain.sb.ObjectFactory INDEX_CONFIG_FACTORY =
        new org.escidoc.core.domain.sb.ObjectFactory();
    // OAI
    private static final org.escidoc.core.domain.oai.ObjectFactory OAI_FACTORY =
        new org.escidoc.core.domain.oai.ObjectFactory();
    // PARENTS
    private static final org.escidoc.core.domain.parents.ObjectFactory PARENTS_FACTORY =
        new org.escidoc.core.domain.parents.ObjectFactory();
    // RELATIONS
    private static final org.escidoc.core.domain.relations.ObjectFactory RELATIONS_FACTORY =
        new org.escidoc.core.domain.relations.ObjectFactory();
    // STAGING
    private static final org.escidoc.core.domain.st.ObjectFactory STAGING_FACTORY =
        new org.escidoc.core.domain.st.ObjectFactory();
    // OTHER
    private static final org.escidoc.core.domain.properties.java.ObjectFactory JAVA_PROPERTIES_FACTORY =
        new org.escidoc.core.domain.properties.java.ObjectFactory();

    /**
     * Avoid instantiation.
     */
    protected ObjectFactoryProvider() {
    }

    public org.escidoc.core.domain.common.ObjectFactory getCommonFactory() {
        return COMMON_FACTORY;
    }

    public org.escidoc.core.domain.metadatarecords.ObjectFactory getMdRecordsFactory() {
        return MD_RECORDS_FACTORY;
    }

    public org.escidoc.core.domain.version.event.ObjectFactory getEventFactory() {
        return EVENT_FACTORY;
    }

    public org.escidoc.core.domain.version.ObjectFactory getVersionFactory() {
        return VERSION_FACTORY;
    }

    public org.escidoc.core.domain.version.history.ObjectFactory getVersionHistoryFactory() {
        return VERSION_HISTORY_FACTORY;
    }

    public org.escidoc.core.domain.sru.ObjectFactory getSruFactory() {
        return SRU_FACTORY;
    }

    public org.escidoc.core.domain.sru.diagnostics.ObjectFactory getSruDiagnosticsFactory() {
        return SRU_DIAGNOSTICS_FACTORY;
    }

    public org.escidoc.core.domain.sru.extradata.ObjectFactory getSruExtraDataFactory() {
        return SRU_EXTRA_DATA_FACTORY;
    }

    public ObjectFactory getTmeFactory() {
        return TME_FACTORY;
    }

    public org.escidoc.core.domain.taskparam.optimisticlocking.ObjectFactory getOptimisticLockingParamFactory() {
        return OPTIMISTIC_LOCKING_PARAM_FACTORY;
    }

    public org.escidoc.core.domain.result.ObjectFactory getResultFactory() {
        return RESULT_FACTORY;
    }

    public org.escidoc.core.domain.container.ObjectFactory getContainerFactory() {
        return CONTAINER_FACTORY;
    }

    public org.escidoc.core.domain.container.list.ObjectFactory getContainerListFactory() {
        return CONTAINER_LIST_FACTORY;
    }

    public org.escidoc.core.domain.aa.useraccount.list.ObjectFactory getUserAccountListFactory() {
        return USER_ACCOUNT_LIST_FACTORY;
    }

    public org.escidoc.core.domain.ou.ObjectFactory getOuFactory() {
        return OU_FACTORY;
    }

    public org.escidoc.core.domain.ou.list.ObjectFactory getOuListFactory() {
        return OU_LIST_FACTORY;
    }

    public org.escidoc.core.domain.ou.ref.ObjectFactory getOuRefFactory() {
        return OU_REF_FACTORY;
    }

    public org.escidoc.core.domain.ou.successors.ObjectFactory getOuSuccessorsFactory() {
        return OU_SUCCESSORS_FACTORY;
    }

    public org.escidoc.core.domain.ou.path.list.ObjectFactory getOuPathListFactory() {
        return OU_PATH_LIST_FACTORY;
    }

    public org.escidoc.core.domain.aa.useraccount.ObjectFactory getUserAccountFactory() {
        return USER_ACCOUNT_FACTORY;
    }

    public org.escidoc.core.domain.aa.useraccount.preferences.ObjectFactory getUserAccountPreferencesFactory() {
        return USER_ACCOUNT_PREFERENCES_FACTORY;
    }

    public org.escidoc.core.domain.aa.useraccount.attributes.ObjectFactory getUserAccountAttributesFactory() {
        return USER_ACCOUNT_ATTRIBUTES_FACTORY;
    }

    public org.escidoc.core.domain.content.relation.ObjectFactory getContentRelationFactory() {
        return CONTENT_RELATION_FACTORY;
    }

    public org.escidoc.core.domain.predicate.list.ObjectFactory getPredicatesFactory() {
        return PREDICATES_FACTORY;
    }

    public org.escidoc.core.domain.aa.pdp.result.ObjectFactory getPDPFactory() {
        return PDP_FACTORY;
    }

    public org.escidoc.core.domain.aa.role.ObjectFactory getRoleFactory() {
        return ROLE_FACTORY;
    }

    public org.escidoc.core.domain.aa.role.list.ObjectFactory getRoleListFactory() {
        return ROLE_LIST_FACTORY;
    }

    public org.escidoc.core.domain.aa.grants.ObjectFactory getGrantFactory() {
        return GRANT_FACTORY;
    }

    public org.escidoc.core.domain.aa.usergroup.ObjectFactory getUserGroupFactory() {
        return USER_GROUP_FACTORY;
    }

    public org.escidoc.core.domain.aa.usergroup.list.ObjectFactory getUserGroupListFactory() {
        return USER_GROUP_LIST_FACTORY;
    }

    public org.escidoc.core.domain.properties.java.ObjectFactory getJavaPropertiesFactory() {
        return JAVA_PROPERTIES_FACTORY;
    }

    public org.escidoc.core.domain.sb.ObjectFactory getIndexConfigFactory() {
        return INDEX_CONFIG_FACTORY;
    }

    public org.escidoc.core.domain.content.model.ObjectFactory getContentModelFactory() {
        return CONTENT_MODEL_FACTORY;
    }

    public org.escidoc.core.domain.context.ObjectFactory getContextFactory() {
        return CONTEXT_FACTORY;
    }

    public org.escidoc.core.domain.oai.ObjectFactory getOaiFactory() {
        return OAI_FACTORY;
    }

    public org.escidoc.core.domain.item.ObjectFactory getItemFactory() {
        return ITEM_FACTORY;
    }

    public org.escidoc.core.domain.components.ObjectFactory getComponentFactory() {
        return COMPONENT_FACTORY;
    }

    public org.escidoc.core.domain.content.stream.ObjectFactory getContentStreamFactory() {
        return CONTENT_STREAM_FACTORY;
    }

    public org.escidoc.core.domain.parents.ObjectFactory getParentsFactory() {
        return PARENTS_FACTORY;
    }

    public org.escidoc.core.domain.relations.ObjectFactory getRelationsFactory() {
        return RELATIONS_FACTORY;
    }

    public org.escidoc.core.domain.container.structmap.ObjectFactory getStructMapFactory() {
        return STRUCT_MAP_FACTORY;
    }

    public org.escidoc.core.domain.sm.scope.ObjectFactory getScopeFactory() {
        return SCOPE_FACTORY;
    }

    public org.escidoc.core.domain.sm.scope.list.ObjectFactory getScopeListFactory() {
        return SCOPE_LIST_FACTORY;
    }

    public org.escidoc.core.domain.sm.ad.ObjectFactory getAggregationDefinitionFactory() {
        return AGGREGATION_DEFINITION_FACTORY;
    }

    public org.escidoc.core.domain.sm.pi.ObjectFactory getPreProcessingFactory() {
        return PRE_PROCESSING_FACTORY;
    }

    public org.escidoc.core.domain.sm.rd.ObjectFactory getReportDefinitionFactory() {
        return REPORT_DEFINITION_FACTORY;
    }

    public org.escidoc.core.domain.sm.rd.list.ObjectFactory getReportDefinitionListFactory() {
        return REPORT_DEFINITION_LIST_FACTORY;
    }

    public org.escidoc.core.domain.sm.report.ObjectFactory getReportFactory() {
        return REPORT_FACTORY;
    }

    public org.escidoc.core.domain.sm.report.parameter.ObjectFactory getReportParamFactory() {
        return REPORT_PARAM_FACTORY;
    }

    public org.escidoc.core.domain.sm.sd.ObjectFactory getStatisticDataFactory() {
        return STATISTIC_DATA_FACTORY;
    }

    public org.escidoc.core.domain.st.ObjectFactory getStagingFactory() {
        return STAGING_FACTORY;
    }
}