package de.escidoc.core.test;

/**
 * eSciDoc test case constants.
 *
 * @author Steffen Wagner
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
public class Constants {

    public static final String ATTRIBUTE_NAME_HREF = "href";

    public static final String DEFAULT_CHARSET = "UTF-8";

    /*
     * Created user (which are not from ldap or shibboleth) have an default password.
     */
    public static String DEFAULT_USER_PASSWORD = "PubManR2";

    // *****************************************************************************************************************
    // * Web-Application Contexts
    // *****************************************************************************************************************

    public static final String WEB_CONTEXT_URI_ESCIDOC = EscidocTestBase.getFrameworkContext();

    public static final String WEB_CONTEXT_URI_FEDORA_GSEARCH = EscidocTestBase.getFedoragsearchContext() + "/rest";

    public static final String WEB_CONTEXT_URI_SEARCH = EscidocTestBase.getSrwContext() + "/search";

    public static final String WEB_CONTEXT_URI_OAI_PMH = EscidocTestBase.getOaiproviderContext();

    public static final String WEB_CONTEXT_URI_FEDORA = EscidocTestBase.getFedoraContext();

    public static final String WEB_CONTEXT_URI_TEST_DATA = EscidocTestBase.getTestdataContext();

    // *****************************************************************************************************************
    // * Web-Application fully qualified URIs
    // *****************************************************************************************************************

    public static final String WEB_APP_URI_ESCIDOC = EscidocTestBase.getBaseUrl() + WEB_CONTEXT_URI_ESCIDOC;

    // *****************************************************************************************************************
    // * Manager
    // *****************************************************************************************************************

    public static final String MANAGER_URI_IR = "/ir";

    public static final String MANAGER_URI_AA = "/aa";

    public static final String MANAGER_URI_OAI = "/oai";

    public static final String MANAGER_URI_ADMIN = "/adm";

    public static final String MANAGER_URI_OU = "/oum";

    public static final String MANAGER_URI_CM = "/cmm";

    public static final String MANAGER_URI_STATISTIC = "/statistic";

    public static final String MANAGER_URI_STAGING = "/st";

    public static final String MANAGER_URI_TME = "/tme";

    // *****************************************************************************************************************
    // * Base Resource Uris (TODO: rename to BASE_URI_* for better grouping)
    // *****************************************************************************************************************

    public static final String SPO = "/spo";

    public static final String CONTEXT_BASE_URI = MANAGER_URI_IR + "/context";

    public static final String CONTEXTS_BASE_URI = MANAGER_URI_IR + "/contexts";

    public static final String CONTAINER_BASE_URI = MANAGER_URI_IR + "/container";

    public static final String CONTAINERS_BASE_URI = MANAGER_URI_IR + "/containers";

    public static final String JHOVE_BASE_URI = MANAGER_URI_TME + "/jhove";

    public static final String ITEM_BASE_URI = MANAGER_URI_IR + "/item";

    public static final String INGEST_BASE_URI = MANAGER_URI_IR + "/ingest";

    public static final String CONTENT_RELATION_BASE_URI = MANAGER_URI_IR + "/content-relation";

    public static final String CONTENT_RELATIONS_BASE_URI = MANAGER_URI_IR + "/content-relations";

    public static final String ITEMS_BASE_URI = MANAGER_URI_IR + "/items";

    public static final String SET_DEFINITIONS_BASE_URI = MANAGER_URI_OAI + "/set-definitions";

    public static final String SEMANTIC_STORE_BASE_URI = MANAGER_URI_IR + "/semantic-store";

    public static final String SET_DEFINITION_BASE_URI = MANAGER_URI_OAI + "/set-definition";

    public static final String REPOSITORY_INFO_BASE_URI = MANAGER_URI_ADMIN + "/admin/get-repository-info";

    public static final String INDEX_CONFIGURATION_BASE_URI = MANAGER_URI_ADMIN + "/admin/get-index-configuration";

    public static final String DELETE_OBJECTS_BASE_URI = MANAGER_URI_ADMIN + "/admin/deleteobjects";

    public static final String LOAD_EXAMPLES_BASE_URI = MANAGER_URI_ADMIN + "/admin/load-examples";

    public static final String REINDEX_BASE_URI = MANAGER_URI_ADMIN + "/admin/reindex";

    public static final String ORGANIZATIONAL_UNIT_BASE_URI = MANAGER_URI_OU + "/organizational-unit";

    public static final String ORGANIZATIONAL_UNITS_BASE_URI = MANAGER_URI_OU + "/organizational-units";

    public static final String CONTENT_MODEL_BASE_URI = MANAGER_URI_CM + "/content-model";

    public static final String CONTENT_MODELS_BASE_URI = MANAGER_URI_CM + "/content-models";

    public static final String PDP_BASE_URI = MANAGER_URI_AA + "/pdp";

    public static final String ROLE_BASE_URI = MANAGER_URI_AA + "/role";

    public static final String ROLES_BASE_URI = MANAGER_URI_AA + "/roles";

    public static final String STAGING_FILE_BASE_URI = MANAGER_URI_STAGING + "/staging-file";

    public static final String USER_ACCOUNT_BASE_URI = MANAGER_URI_AA + "/user-account";

    public static final String USER_ACCOUNTS_BASE_URI = MANAGER_URI_AA + "/user-accounts";

    public static final String GRANTS_BASE_URI = MANAGER_URI_AA + "/grants";

    public static final String USER_GROUP_BASE_URI = MANAGER_URI_AA + "/user-group";

    public static final String USER_GROUPS_BASE_URI = MANAGER_URI_AA + "/user-groups";

    public static final String STATISTIC_DATA_BASE_URI = MANAGER_URI_STATISTIC + "/statistic-data";

    public static final String STATISTIC_AGGREGATION_DEFINITION_BASE_URI =
        MANAGER_URI_STATISTIC + "/aggregation-definition";

    public static final String STATISTIC_AGGREGATION_DEFINITIONS_BASE_URI =
        MANAGER_URI_STATISTIC + "/aggregation-definitions";

    public static final String STATISTIC_REPORT_DEFINITION_BASE_URI = MANAGER_URI_STATISTIC + "/report-definition";

    public static final String STATISTIC_REPORT_DEFINITIONS_BASE_URI = MANAGER_URI_STATISTIC + "/report-definitions";

    public static final String STATISTIC_REPORT_BASE_URI = MANAGER_URI_STATISTIC + "/report";

    public static final String STATISTIC_SCOPE_BASE_URI = MANAGER_URI_STATISTIC + "/scope";

    public static final String STATISTIC_SCOPES_BASE_URI = MANAGER_URI_STATISTIC + "/scopes";

    public static final String STATISTIC_PREPROCESSING_BASE_URI = MANAGER_URI_STATISTIC + "/preprocessing";

    public static final String UNSECURED_ACTIONS_BASE_URI = MANAGER_URI_AA + "/unsecured-actions";

    public static final String TEST_DATA_BASE_URI = WEB_CONTEXT_URI_TEST_DATA + "/testDocuments";

    // *****************************************************************************************************************
    // * Sub Resource names
    // *****************************************************************************************************************

    public static final String SUB_ADMINDESCRIPTORS = "admin-descriptors";

    public static final String SUB_ADMINDESCRIPTOR = SUB_ADMINDESCRIPTORS + "/" + "admin-descriptor";

    public static final String SUB_COMPONENTS = "components";

    public static final String SUB_COMPONENT = SUB_COMPONENTS + "/" + "component";

    public static final String SUB_CONTENT = "content";

    public static final String SUB_ITEMS = Constants.SUB_RESOURCES + "/" + "items";

    public static final String SUB_CONTEXT_OPEN = "open";

    public static final String SUB_MD_RECORDS = "md-records";

    public static final String SUB_CONTENT_STREAMS = "content-streams";

    public static final String SUB_CONTENT_STREAM = SUB_CONTENT_STREAMS + "/content-stream";

    public static final String SUB_MD_RECORD = SUB_MD_RECORDS + "/" + "md-record";

    public static final String SUB_PROPERTIES = "properties";

    public static final String SUB_PARENTS = "parents";

    public static final String SUB_RESOURCES = "resources";

    public static final String SUB_STRUCT_MAP = "struct-map";

    public static final String SUB_CONTAINER_MEMBERS = "members";

    public static final String SUB_METS = SUB_RESOURCES + "/" + "mets";

    public static final String SUB_CHILD_OBJECTS = SUB_RESOURCES + "/" + "child-objects";

    public static final String SUB_PARENT_OBJECTS = SUB_RESOURCES + "/" + "parent-objects";

    public static final String SUB_VERSION_HISTORY = SUB_RESOURCES + "/" + "version-history";

    public static final String SUB_SUBMIT = "submit";

    public static final String SUB_RELATIONS = "relations";

    public static final String SUB_CONTENT_RELATIONS = "content-relations";

    public static final String SUB_ADD_CONTENT_RELATIONS = SUB_CONTENT_RELATIONS + "/add";

    public static final String SUB_REMOVE_CONTENT_RELATIONS = SUB_CONTENT_RELATIONS + "/remove";

    public static final String SUB_CLOSE = "close";

    public static final String SUB_OPEN = "open";

    public static final String SUB_RELEASE = "release";

    public static final String SUB_REVISE = "revise";

    public static final String SUB_ASSIGN_VERSION_PID = "assign-version-pid";

    public static final String SUB_ASSIGN_OBJECT_PID = "assign-object-pid";

    public static final String SUB_ASSIGN_CONTENT_PID = "assign-content-pid";

    public static final String SUB_RETRIEVE_REGISTERED_PREDICATES = "retrieve-registered-predicates";

    public static final String SUB_WITHDRAW = "withdraw";

    public static final String SUB_LOCK = "lock";

    public static final String SUB_UNLOCK = "unlock";

    public static final String SUB_ADD_MEMBERS = "members/add";

    public static final String SUB_REMOVE_MEMBERS = "members/remove";

    public static final String SUB_ADD_SELECTORS = "selectors/add";

    public static final String SUB_REMOVE_SELECTORS = "selectors/remove";

    public static final String SUB_PATH_LIST = SUB_RESOURCES + "/" + "path-list";

    public static final String SUB_SUCCESSORS = SUB_RESOURCES + "/" + "successors";

    // *****************************************************************************************************************
    // * Sub Resource user-account and user-group
    // *****************************************************************************************************************

    public static final String SUB_GRANT = SUB_RESOURCES + "/grants/grant";

    // *****************************************************************************************************************
    // * HTTP
    // *****************************************************************************************************************

    public static final String HTTP_PROTOCOL = "http";

    public static final String HTTP_METHOD_DELETE = "DELETE";

    public static final String HTTP_METHOD_GET = "GET";

    public static final String HTTP_METHOD_POST = "POST";

    public static final String HTTP_METHOD_PUT = "PUT";

    // *****************************************************************************************************************
    // * XML (see javax.xml.XMLConstants before adding new constants)
    // *****************************************************************************************************************

    public static final String XML_CDATA_START = "<![CDATA[";

    public static final String XML_CDATA_END = "]]>";

    public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

    // *****************************************************************************************************************
    // * Login specific constants
    // *****************************************************************************************************************

    public static final String LOGIN_DEFAULT_PROVIDER = "/database";

    public static final String LOGIN_URI = MANAGER_URI_AA + "/login" + LOGIN_DEFAULT_PROVIDER;

    public static final String LOGIN_PARAM_UM_NAME = "j_username";

    public static final String LOGIN_PARAM_UM_PASSWORD = "j_password";

    public static final String LOGIN_SPRING_SECURITY_CHECK = "/j_spring_security_check";

    // *****************************************************************************************************************
    // * Fedora specific constants
    // *****************************************************************************************************************

    public static final String FOXML_FORMAT = "info:fedora/fedora-system:FOXML-1.1";

    // *****************************************************************************************************************
    // * NAMESPACE BASES
    // *****************************************************************************************************************

    // BASE
    public static final String SCHEMA_LOCATION_BASE = "http://www.escidoc.org/schemas";

    public static final String PROPERTIES_NS_BASE = "http://escidoc.de/core/01";

    // IR
    public static final String NS_BASE_IR_ITEM = "/item/1.0/item";

    public static final String NS_BASE_IR_CONTAINER = "/container/1.0/container";

    public static final String NS_BASE_IR_CONTEXT = "/context/1.0/context";

    public static final String NS_BASE_IR_CONTENT_RELATION = "/content-relation/1.0/content-relation";

    public static final String NS_BASE_IR_COMPONENTS = "/item/1.0/item";

    public static final String NS_BASE_IR_MD_RECORDS = "/common/1.0/md-records";

    // OUM
    public static final String NS_BASE_OUM_OU = "/organizational-unit/1.0/organizational-unit";

    // AA
    public static final String NS_BASE_AA_USER_ACCOUNT = "/user-account/1.0/user-account";

    public static final String NS_BASE_AA_USER_GROUP = "/user-group/1.0/user-group";

    public static final String NS_BASE_AA_GRANTS = "/grant/1.0/grants";

    public static final String NS_BASE_AA_ROLE = "/role/1.0/role";

    public static final String NS_BASE_AA_PDP = "/pdp/1.0";

    public static final String NS_BASE_AA_PREFERENCES = "/user-account/1.0/user-account-preferences";

    public static final String NS_BASE_AA_ATTRIBUTES = "/user-account/1.0/user-account-attributes";

    // CMM
    public static final String NS_BASE_CMM_CONTENT_MODEL = "/content-model/1.0/content-model";

    // STATISTIC
    public static final String NS_BASE_SM_SCOPE = "/scope/1.0/scope";

    public static final String NS_BASE_SM_REPORT_DEFINITION = "/report-definition/1.0/report-definition";

    public static final String NS_BASE_SM_AGGREGATION_DEFINITION = "/aggregation-definition/1.0/aggregation-definition";

    public static final String NS_BASE_SM_REPORT = "/report/1.0/report";

    // STAGING
    public static final String NS_BASE_ST_FILE = "/staging-file/1.0/staging-file";

    // OAI
    public static final String NS_BASE_OAI_SET_DEFINITION = "/set-definition/1.0/set-definition";

    // TASK PARAM
    private static final String NS_BASE_TP_OPTIMISTIC_LOCKING = "/task-param/1.0/optimistic-locking-task-param";

    private static final String NS_BASE_TP_UPDATE_PASSWORD = "/task-param/1.0/update-password-task-param";

    private static final String NS_BASE_TP_REVOKE_GRANT = "/task-param/1.0/revoke-grant-task-param";

    private static final String NS_BASE_TP_REVOKE_GRANTS = "/task-param/1.0/revoke-grants-task-param";

    private static final String NS_BASE_TP_SELECTOR_ADD = "/task-param/1.0/add-selectors";

    private static final String NS_BASE_TP_SELECTOR_REMOVE = "/task-param/1.0/remove-selectors";

    private static final String NS_BASE_TP_REINDEX = "/task-param/1.0/reindex-task-param";

    private static final String NS_BASE_TP_MEMBERS = "/task-param/1.0/members-task-param";

    private static final String NS_BASE_TP_ID_SET = "/task-param/1.0/id-set-task-param";

    private static final String NS_BASE_TP_DELETE_OBJECTS = "/task-param/1.0/delete-objects-task-param";

    private static final String NS_BASE_TP_ASSIGN_PID = "/task-param/1.0/assign-pid-task-param";

    private static final String NS_BASE_TP_STATUS = "/task-param/1.0/status-task-param";

    private static final String NS_BASE_TP_RELATION = "/task-param/1.0/relation-task-param";

    // *****************************************************************************************************************
    // * ESCIDOC NAMESPACES
    // *****************************************************************************************************************

    // COMMON
    public static final String NS_COMMON_SREL = PROPERTIES_NS_BASE + "/structural-relations/";

    public static final String NS_COMMON_RESOURCES = PROPERTIES_NS_BASE + "/resources/";

    public static final String NS_COMMON_PROPERTIES = PROPERTIES_NS_BASE + "/properties/";

    // AA
    public static final String NS_AA_USER_ACCOUNT = SCHEMA_LOCATION_BASE + NS_BASE_AA_USER_ACCOUNT;

    public static final String NS_AA_USER_GROUP = SCHEMA_LOCATION_BASE + NS_BASE_AA_USER_GROUP;

    public static final String NS_AA_GRANTS = SCHEMA_LOCATION_BASE + NS_BASE_AA_GRANTS;

    public static final String NS_AA_ROLE = SCHEMA_LOCATION_BASE + NS_BASE_AA_ROLE;

    public static final String NS_AA_PREFERENCES = SCHEMA_LOCATION_BASE + NS_BASE_AA_PREFERENCES;

    public static final String NS_AA_ATTRIBUTES = SCHEMA_LOCATION_BASE + NS_BASE_AA_ATTRIBUTES;

    public static final String NS_AA_PDP_REQUESTS = SCHEMA_LOCATION_BASE + NS_BASE_AA_PDP + "/pdp-requests";

    public static final String NS_AA_PDP_RESULTS = SCHEMA_LOCATION_BASE + NS_BASE_AA_PDP + "/pdp-results";

    // CMM
    public static final String NS_CMM_CONTENT_MODEL = SCHEMA_LOCATION_BASE + NS_BASE_CMM_CONTENT_MODEL;

    // IR
    public static final String NS_IR_ITEM = SCHEMA_LOCATION_BASE + NS_BASE_IR_ITEM;

    public static final String NS_IR_CONTAINER = SCHEMA_LOCATION_BASE + NS_BASE_IR_CONTAINER;

    public static final String NS_IR_CONTEXT = SCHEMA_LOCATION_BASE + NS_BASE_IR_CONTEXT;

    public static final String NS_IR_CONTENT_RELATION = SCHEMA_LOCATION_BASE + NS_BASE_IR_CONTENT_RELATION;

    public static final String NS_IR_COMPONENTS = SCHEMA_LOCATION_BASE + NS_BASE_IR_COMPONENTS;

    public static final String NS_IR_MD_RECORDS = SCHEMA_LOCATION_BASE + NS_BASE_IR_MD_RECORDS;

    // OUM
    public static final String NS_OUM_OU = SCHEMA_LOCATION_BASE + NS_BASE_OUM_OU;

    // STATISTIC
    public static final String NS_SM_SCOPE = SCHEMA_LOCATION_BASE + NS_BASE_SM_SCOPE;

    public static final String NS_SM_REPORT_DEFINITION = SCHEMA_LOCATION_BASE + NS_BASE_SM_REPORT_DEFINITION;

    public static final String NS_SM_AGGREGATION_DEFINITION = SCHEMA_LOCATION_BASE + NS_BASE_SM_AGGREGATION_DEFINITION;

    public static final String NS_SM_REPORT = SCHEMA_LOCATION_BASE + NS_BASE_SM_REPORT;

    // STAGING
    public static final String NS_ST_FILE = SCHEMA_LOCATION_BASE + NS_BASE_ST_FILE;

    // OAI
    public static final String NS_OAI_SET_DEFINITION = SCHEMA_LOCATION_BASE + NS_BASE_OAI_SET_DEFINITION;

    // TASK PARAM
    public static final String NS_TP_OPTIMISTIC_LOCKING = SCHEMA_LOCATION_BASE + NS_BASE_TP_OPTIMISTIC_LOCKING;

    public static final String NS_TP_UPDATE_PASSWORD = SCHEMA_LOCATION_BASE + NS_BASE_TP_UPDATE_PASSWORD;

    public static final String NS_TP_REVOKE_GRANT = SCHEMA_LOCATION_BASE + NS_BASE_TP_REVOKE_GRANT;

    public static final String NS_TP_REVOKE_GRANTS = SCHEMA_LOCATION_BASE + NS_BASE_TP_REVOKE_GRANTS;

    public static final String NS_TP_SELECTOR_ADD = SCHEMA_LOCATION_BASE + NS_BASE_TP_SELECTOR_ADD;

    public static final String NS_TP_SELECTOR_REMOVE = SCHEMA_LOCATION_BASE + NS_BASE_TP_SELECTOR_REMOVE;

    public static final String NS_TP_REINDEX = SCHEMA_LOCATION_BASE + NS_BASE_TP_REINDEX;

    public static final String NS_TP_MEMBERS = SCHEMA_LOCATION_BASE + NS_BASE_TP_MEMBERS;

    public static final String NS_TP_ID_SET = SCHEMA_LOCATION_BASE + NS_BASE_TP_ID_SET;

    public static final String NS_TP_DELETE_OBJECTS = SCHEMA_LOCATION_BASE + NS_BASE_TP_DELETE_OBJECTS;

    public static final String NS_TP_ASSIGN_PID = SCHEMA_LOCATION_BASE + NS_BASE_TP_ASSIGN_PID;

    public static final String NS_TP_STATUS = SCHEMA_LOCATION_BASE + NS_BASE_TP_STATUS;

    public static final String NS_TP_RELATION = SCHEMA_LOCATION_BASE + NS_BASE_TP_RELATION;

    // *****************************************************************************************************************
    // * EXTERNAL NAMESPACES
    // *****************************************************************************************************************

    public static final String NS_EXTERNAL_DC = "http://purl.org/dc/elements/1.1/";

    public static final String NS_EXTERNAL_ESCIDOC_DC = "http://purl.org/escidoc/metadata/terms/0.1/";

    public static final String NS_EXTERNAL_MPG_METADATA = "http://escidoc.mpg.de/metadataprofile/schema/0.1/";

    public static final String NS_EXTERNAL_JHOVE_METADATA = "http://jhove.com/metadata/schema/0.5";

    public static final String NS_EXTERNAL_DC_TERMS = "http://purl.org/dc/terms/";

    public static final String NS_EXTERNAL_XACML_CONTEXT = "urn:oasis:names:tc:xacml:1.0:context";

    public static final String NS_EXTERNAL_XACML_POLICY = "urn:oasis:names:tc:xacml:1.0:policy";

    public static final String NS_EXTERNAL_XLINK = "http://www.w3.org/1999/xlink";

    public static final String NS_EXTERNAL_XSI = "http://www.w3.org/2001/XMLSchema-instance";

    public static final String NS_EXTERNAL_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    public static final String NS_EXTERNAL_RDF_TYPE = NS_EXTERNAL_RDF + "type";

    // *****************************************************************************************************************
    // * XSD
    // *****************************************************************************************************************

    // COMMON
    public static final String XSD_COMMON_RESULTS = "result.xsd";

    public static final String XSD_COMMON_VERSION_HISTORY = "version-history.xsd";

    public static final String XSD_COMMON_RELATIONS = "relations.xsd";

    public static final String XSD_COMMON_PARENTS = "parents.xsd";

    public static final String XSD_COMMON_SRW = "srw-types.xsd";

    public static final String XSD_COMMON_ZEEREX = "zeerex-2.0.xsd";

    // IR
    public static final String XSD_IR_CONTEXT = "context.xsd";

    public static final String XSD_IR_ITEM = "item.xsd";

    public static final String XSD_IR_CONTAINER = "container.xsd";

    public static final String XSD_IR_STRUCT_MAP = "struct-map.xsd";

    public static final String XSD_IR_CONTENT_RELATION = "content-relation.xsd";

    public static final String XSD_IR_PREDICATE_LIST = "predicate-list.xsd";

    // AA
    public static final String XSD_AA_GRANTS = "grants.xsd";

    public static final String XSD_AA_PREFERENCES = "user-account-preferences.xsd";

    public static final String XSD_AA_ATTRIBUTES = "user-account-attributes.xsd";

    public static final String XSD_AA_ROLE = "role.xsd";

    public static final String XSD_AA_USER_ACCOUNT = "user-account.xsd";

    public static final String XSD_AA_USER_GROUP = "user-group.xsd";

    public static final String XSD_AA_PDP_REQUESTS = "pdp-requests.xsd";

    public static final String XSD_AA_PDP_RESULTS = "pdp-results.xsd";

    // OUM
    public static final String XSD_OUM_OU_SUCCESSORS = "organizational-unit-successors.xsd";

    public static final String XSD_OUM_OU_PATH_LIST = "organizational-unit-path-list.xsd";

    public static final String XSD_OUM_OU = "organizational-unit.xsd";

    // CMM
    public static final String XSD_CMM_CONTENT_MODEL = "content-model.xsd";

    // ADM
    public static final String XSD_ADM_INDEX_CONFIGURATION = "index-configuration.xsd";

    // SM
    public static final String XSD_SM_SCOPE = "scope.xsd";

    public static final String XSD_SM_AGGREGATION_DEFINITION = "aggregation-definition.xsd";

    public static final String XSD_SM_REPORT_DEFINITION = "report-definition.xsd";

    public static final String XSD_SM_REPORT = "report.xsd";

    // TME
    public static final String XSD_TME_JHOVE = "jhove.xsd";

    // OAI
    public static final String XSD_OAI_SET_DEFINITION = "set-definition.xsd";

    public static final String XSD_ST_FILE = "staging-file.xsd";

    // *****************************************************************************************************************
    // * XSD LOCAL ACCESS
    // *
    // * Note:
    // * Not every access can be resolved by using the NS_BASE_* constants. Because of that and the amount of
    // * inconsistencies between namespaces and schemaLocations, we better redefine these constants entirely, instead
    // * of reusing the NS_BASE_* constants here.
    // *****************************************************************************************************************

    // GENERAL
    public static final String XSD_ACCESS = "/xsd";

    // COMMON
    public static final String XSD_ACCESS_COMMON_SRW = WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/srw/1.0/" + XSD_COMMON_SRW;

    public static final String XSD_ACCESS_COMMON_ZEEREX =
        WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/srw/1.0/" + XSD_COMMON_ZEEREX;

    public static final String XSD_ACCESS_COMMON_RESULTS =
        WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/common/1.0/" + XSD_COMMON_RESULTS;

    public static final String XSD_ACCESS_COMMON_VERSION_HISTORY =
        WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/common/1.0/" + XSD_COMMON_VERSION_HISTORY;

    public static final String XSD_ACCESS_COMMON_RELATIONS =
        WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/common/1.0/" + XSD_COMMON_RELATIONS;

    // AA
    public static final String XSD_ACCESS_AA_USER_ACCOUNT =
        WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/user-account/1.0/" + XSD_AA_USER_ACCOUNT;

    public static final String XSD_ACCESS_AA_PREFERENCES =
        WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/user-account/1.0/" + XSD_AA_PREFERENCES;

    public static final String XSD_ACCESS_AA_ATTRIBUTES =
        WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/user-account/1.0/" + XSD_AA_ATTRIBUTES;

    public static final String XSD_ACCESS_AA_USER_GROUP =
        WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/user-group/1.0/" + XSD_AA_USER_GROUP;

    public static final String XSD_ACCESS_AA_PDP_REQUESTS =
        WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/pdp/1.0/" + XSD_AA_PDP_REQUESTS;

    public static final String XSD_ACCESS_AA_PDP_RESULTS =
        WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/pdp/1.0/" + XSD_AA_PDP_RESULTS;

    public static final String XSD_ACCESS_AA_ROLE = WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/role/1.0/" + XSD_AA_ROLE;

    public static final String XSD_ACCESS_AA_GRANTS = WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/grant/1.0/" + XSD_AA_GRANTS;

    // ADM
    public static final String XSD_ACCESS_ADM_INDEX_CONFIGURATION =
        WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/indexing/1.0/" + XSD_ADM_INDEX_CONFIGURATION;

    // IR
    public static final String XSD_ACCESS_IR_CONTEXT =
        WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/context/1.0/" + XSD_IR_CONTEXT;

    public static final String XSD_ACCESS_IR_ITEM = WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/item/1.0/" + XSD_IR_ITEM;

    public static final String XSD_ACCESS_IR_CONTAINER =
        WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/container/1.0/" + XSD_IR_CONTAINER;

    public static final String XSD_ACCESS_IR_STRUCT_MAP =
        WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/container/1.0/" + XSD_IR_STRUCT_MAP;

    public static final String XSD_ACCESS_IR_CONTENT_RELATION =
        WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/content-relation/1.0/" + XSD_IR_CONTENT_RELATION;

    public static final String XSD_ACCESS_IR_PREDICATE_LIST =
        WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/content-relation/1.0/" + XSD_IR_PREDICATE_LIST;

    // CMM
    public static final String XSD_ACCESS_CMM_CONTENT_MODEL =
        WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/content-model/1.0/" + XSD_CMM_CONTENT_MODEL;

    // OUM
    public static final String XSD_ACCESS_OUM_OU =
        WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/organizational-unit/1.0/" + XSD_OUM_OU;

    public static final String XSD_ACCESS_OUM_OU_SUCCESSORS =
        WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/organizational-unit/1.0/" + XSD_OUM_OU_SUCCESSORS;

    public static final String XSD_ACCESS_OUM_OU_PATH_LIST =
        WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/organizational-unit/1.0/" + XSD_OUM_OU_PATH_LIST;

    // SM
    public static final String XSD_ACCESS_SM_SCOPE = WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/scope/1.0/" + XSD_SM_SCOPE;

    public static final String XSD_ACCESS_SM_AGGREGATION_DEFINITION =
        WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/aggregation-definition/1.0/" + XSD_SM_AGGREGATION_DEFINITION;

    public static final String XSD_ACCESS_SM_REPORT_DEFINITION =
        WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/report-definition/1.0/" + XSD_SM_REPORT_DEFINITION;

    public static final String XSD_ACCESS_SM_REPORT = WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/report/1.0/" + XSD_SM_REPORT;

    // TME
    public static final String XSD_ACCESS_TME_JHOVE = WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/tme/1.0/" + XSD_TME_JHOVE;

    // OAI
    public static final String XSD_ACCESS_OAI_SET_DEFINITION =
        WEB_APP_URI_ESCIDOC + XSD_ACCESS + "/set-definition/1.0/" + XSD_OAI_SET_DEFINITION;

    // ST
    public static final String XSD_ACCESS_ST_FILE = "/staging-file/1.0/" + XSD_ST_FILE;

}
