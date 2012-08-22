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
package de.escidoc.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.TransformerException;
import javax.xml.validation.Schema;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.xerces.dom.AttrImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import de.escidoc.core.test.common.client.servlet.adm.AdminClient;
import de.escidoc.core.test.common.client.servlet.interfaces.ResourceHandlerClientInterface;
import de.escidoc.core.test.common.client.servlet.om.ContainerClient;
import de.escidoc.core.test.common.client.servlet.om.ContentRelationClient;
import de.escidoc.core.test.common.client.servlet.om.ContextClient;
import de.escidoc.core.test.common.client.servlet.om.DeviationClient;
import de.escidoc.core.test.common.client.servlet.om.IngestClient;
import de.escidoc.core.test.common.client.servlet.om.ItemClient;
import de.escidoc.core.test.common.client.servlet.oum.OrganizationalUnitClient;
import de.escidoc.core.test.common.client.servlet.st.StagingFileClient;
import de.escidoc.core.test.common.resources.PropertiesProvider;
import de.escidoc.core.test.security.client.PWCallback;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

/**
 * Base class for Escidoc tests.
 *
 * @author Michael Schneider
 */
public abstract class EscidocTestBase extends EscidocAssertions {

    public static final String VERSION_SUFFIX_SEPARATOR = ":";

    private static final String NAME_USER_ACCOUNT_LIST = "user-account-list";

    public static final String NAME_USER_ACCOUNT = "user-account";

    public static final String NAME_ATTRIBUTE = "attribute";

    private static final String NAME_USER_GROUP_LIST = "user-group-list";

    public static final String NAME_USER_GROUP = "user-group";

    private static final String NAME_SET_DEFINITION = "set-definition";

    public static final String NAME_LATEST_VERSION = "latest-version";

    public static final String NAME_AGG_DEF = "aggregation-definition";

    public static final String NAME_REP_DEF = "report-definition";

    public static final String NAME_SCOPE = "scope";

    private static final Pattern PATTERN_ID_WITHOUT_VERSION = Pattern.compile("([a-zA-Z]+:[0-9]+):[0-9]+");

    private static final Logger LOGGER = LoggerFactory.getLogger(EscidocTestBase.class);

    protected static final EtmMonitor ETM_MONITOR = EtmManager.getEtmMonitor();

    private StagingFileClient stagingFileClient = new StagingFileClient();

    /**
     * Id of a persistent content type object.
     */
    protected static final String CONTENT_TYPE_ID = "escidoc:persistent4";

    /**
     * Id of a persistent context object.
     */
    protected static final String CONTEXT_ID = "escidoc:persistent3";

    /**
     * another Id of a persistent context object.
     */
    protected static final String CONTEXT_ID1 = "escidoc:persistent23";

    /**
     * another Id of a persistent context object.
     */
    protected static final String CONTEXT_ID2 = "escidoc:persistent5";

    /**
     * another Id of a persistent context object.
     */
    protected static final String CONTEXT_ID3 = "escidoc:persistent10";

    /**
     * Id of a persistent statistic-scope.
     */
    protected static final String STATISTIC_SCOPE_ID = "escidoc:scope3";

    /**
     * Id of a persistent statistic-scope.
     */
    protected static final String STATISTIC_SCOPE_ID1 = "escidoc:scope4";

    /**
     * Id of a persistent org unit object.
     */
    protected static final String ORGANIZATIONAL_UNIT_ID = "escidoc:persistent11";

    /**
     * Id of a persistent org unit object.
     */
    protected static final String ORGANIZATIONAL_UNIT_ID1 = "escidoc:persistent1";

    /**
     * Id of a persistent user-group object containing a list of users.
     */
    protected static final String USER_GROUP_WITH_USER_LIST_ID = "escidoc:testgroupwithuser";

    /**
     * Id of a persistent user-group object containing a list of ous.
     */
    protected static final String USER_GROUP_WITH_OU_LIST_ID = "escidoc:testgroupwithorgunit";

    /**
     * Id of a persistent user-group object containing a list of groups.
     */
    protected static final String USER_GROUP_WITH_GROUP_LIST_ID = "escidoc:testgroupwithgroup";

    /**
     * Id of a persistent user-group object containing an attribute selector.
     */
    protected static final String USER_GROUP_WITH_EXTERNAL_SELECTOR = "escidoc:testgroupwithexternalselector";

    protected static final String TEST_SYSTEMADMINISTRATOR_ID1 = "escidoc:testsystemadministrator1";

    protected static final String TEST_USER_ACCOUNT_ID = "escidoc:test";

    protected static final String TEST_USER_ACCOUNT_ID1 = "escidoc:test1";

    protected static final String TEST_DEPOSITOR_ACCOUNT_ID = "escidoc:testdepositor";

    protected static final String TEST_USER_GROUP_ID = "escidoc:testgroup";

    /**
     * Id of a persistent aggregation-definition.
     */
    protected static final String TEST_AGGREGATION_DEFINITION_ID = "escidoc:aggdef1";

    public static final Pattern PATTERN_OBJID_ATTRIBUTE = Pattern.compile("objid=\"([^\"]*)\"");

    /**
     * Pattern used in modfyNamespacePrefixes to find and replace prefixes.
     */
    private static final Pattern PATTERN_MODIFY_NAMESPACE_PREFIXES_REPLACE_PREFIXES =
        Pattern.compile("(</?|[\\s])([0-9a-zA-Z-]+?:[^ =/>]+)", Pattern.DOTALL | Pattern.MULTILINE);

    /**
     * Pattern used in modfyNamespacePrefixes to fix namespace declarations after changing prefixes.
     */
    private static final Pattern PATTERN_MODIFY_NAMESPACE_PREFIXES_FIX_NAMESPACE_DECLARATIONS =
        Pattern.compile("prefix-xmlns:([a-zA-Z-].+?)", Pattern.DOTALL | Pattern.MULTILINE);

    /**
     * Pattern used in modfyNamespacePrefixes to fix xml namespace declaration.
     */
    private static final Pattern PATTERN_MODIFY_NAMESPACE_PREFIXES_FIX_PREFIX_XML = Pattern.compile("prefix-xml");

    public static final String STATE_PENDING = "pending";

    public static final String STATE_IN_REVISION = "in-revision";

    public static final String STATE_SUBMITTED = "submitted";

    public static final String STATE_RELEASED = "released";

    public static final String STATE_WITHDRAWN = "withdrawn";

    public static final String STATE_LOCKED = "locked";

    public static final String STATE_UNLOCKED = "unlocked";

    public static final String NAME_ALTERNATIVE = "alternative";

    public static final String NAME_START_DATE = "start-date";

    public static final String NAME_END_DATE = "end-date";

    public static final String NAME_BASE = "base";

    public static final String NAME_CITY = "city";

    public static final String NAME_COMPONENT = "component";

    public static final String NAME_COMPONENTS = "components";

    public static final String NAME_COUNTRY = "country";

    public static final String NAME_CREATION_DATE = "creation-date";

    public static final String NAME_CURRENT_GRANTS = "current-grants";

    public static final String NAME_VERSION = "version";

    public static final String NAME_LATEST_RELEASE = "latest-release";

    public static final String NAME_CREATED_BY = "created-by";

    public static final String NAME_MD_RECORDS = "md-records";

    public static final String NAME_MD_RECORD = "md-record";

    public static final String NAME_DESCRIPTION = "description";

    public static final String NAME_IDENTIFIER = "identifier";

    public static final String NAME_FAX = "fax";

    public static final String NAME_GEO_COORDINATE = "geo-coordinate";

    public static final String NAME_GRANT_REMARK = "grant-remark";

    public static final String NAME_HAS_CHILDREN = "has-children";

    public static final String NAME_HREF = "href";

    public static final String NAME_ITEM = "item";

    public static final String XPATH_ITEM = "/" + NAME_ITEM;

    public static final String NAME_CONTAINER = "container";

    public static final String NAME_CONTENT_MODEL = "content-model";

    public static final String NAME_CONTENT_RELATION = "content-relation";

    public static final String NAME_GRANT = "grant";

    public static final String NAME_LAST_MODIFICATION_DATE = "last-modification-date";

    public static final String NAME_LOCATION_LATITUDE = "location-latitude";

    public static final String NAME_LOCATION_LONGITUDE = "location-longitude";

    public static final String NAME_MODIFIED_BY = "modified-by";

    public static final String NAME_NAME = "name";

    public static final String NAME_OBJID = "objid";

    public static final String NAME_ORGANIZATIONAL_UNIT = "organizational-unit";

    public static final String NAME_ORGANIZATIONAL_UNITS = "organizational-units";

    public static final String NAME_PARENT = "parent";

    public static final String NAME_PARENTS = "parents";

    public static final String NAME_POSTCODE = "postcode";

    public static final String NAME_PROPERTIES = "properties";

    public static final String NAME_PUBLIC_STATUS = "public-status";

    public static final String NAME_PUBLIC_STATUS_COMMENT = "public-status-comment";

    public static final String NAME_CONTEXT = "context";

    public static final String NAME_REGION = "region";

    public static final String NAME_RESOURCES = "resources";

    public static final String NAME_ROLE = "role";

    public static final String NAME_TELEPHONE = "telephone";

    public static final String NAME_TITLE = "title";

    public static final String NAME_URI = "uri";

    public static final String NAME_VERSION_STATUS = "status";

    public static final String NAME_ORGANIZATION_TYPE = "organization-type";

    public static final String NAME_TYPE = "type";

    public static final String PART_LAST_MODIFICATION_DATE = "/@" + NAME_LAST_MODIFICATION_DATE;

    public static final String PART_OBJID = "/@objid";

    public static final String PART_XLINK_TITLE = "/@title";

    public static final String PART_XLINK_TYPE = "/@type";

    public static final String PART_XLINK_HREF = "/@href";

    public static final String PART_XML_BASE = "/@" + NAME_BASE;

    public static final String PART_XLINK_NS = "/@xlink";

    public static final String GRANTS_PREFIX_TEMPLATES = "prefix-grants";

    public static final String GRANTS_PREFIX_ESCIDOC = "grants";

    public static final String ORGANIZATIONAL_UNIT_PREFIX_TEMPLATES = "prefix-organizational-unit";

    public static final String ORGANIZATIONAL_UNIT_PREFIX_ESCIDOC = "organizational-unit";

    public static final String ROLE_PREFIX_TEMPLATES = "prefix-role";

    public static final String ROLE_PREFIX_ESCIDOC = "role";

    public static final String SREL_PREFIX_TEMPLATES = "prefix-srel";

    public static final String SREL_PREFIX_ESCIDOC = "srel";

    public static final String USER_ACCOUNT_PREFIX_TEMPLATES = "prefix-user-account";

    public static final String USER_GROUP_PREFIX_TEMPLATES = "prefix-user-group";

    public static final String USER_ACCOUNT_PREFIX_ESCIDOC = NAME_USER_ACCOUNT;

    public static final String USER_GROUP_PREFIX_ESCIDOC = NAME_USER_GROUP;

    public static final String PROPERTIES_FILTER_PREFIX = "/properties/";

    public static final String STRUCTURAL_RELATIONS_FILTER_PREFIX = "/structural-relations/";

    public static final String TYPE_FEDORA_OBJECT_URI = "info:fedora/fedora-system:def/model#FedoraObject";

    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static final String UNKNOWN_ID = "escidoc:-1";

    public static final String TEMPLATE_BASE_PATH = "templates";

    public static final String TEMPLATE_EXAMPLE_PATH = "examples/escidoc";

    public static final String TEMPLATE_AA_PATH = TEMPLATE_BASE_PATH + "/aa/template";

    public static final String TEMPLATE_ST_PATH = TEMPLATE_BASE_PATH + "/st/template";

    public static final String TEMPLATE_REQUESTS_PATH = TEMPLATE_AA_PATH + "/requests";

    public static final String TEMPLATE_AA_ITEM_PATH = TEMPLATE_AA_PATH + "/item";

    public static final String TEMPLATE_ROLE_PATH = TEMPLATE_AA_PATH + "/role";

    public static final String TEMPLATE_CMM_PATH = TEMPLATE_BASE_PATH + "/cmm/template";

    public static final String TEMPLATE_OM_PATH = TEMPLATE_BASE_PATH + "/om/template";

    public static final String TEMPLATE_OAI_PATH = TEMPLATE_BASE_PATH + "/oai/template";

    public static final String TEMPLATE_OUM_PATH = TEMPLATE_BASE_PATH + "/oum/template";

    public static final String TEMPLATE_ADMIN_DESCRIPTOR_PATH = TEMPLATE_OM_PATH + "/admindescriptor";

    public static final String TEMPLATE_OM_COMMON_PATH = TEMPLATE_OM_PATH + "/common";

    public static final String TEMPLATE_CONTAINER_PATH = TEMPLATE_OM_PATH + "/container";

    public static final String TEMPLATE_CONTAINER_SEARCH_PATH = TEMPLATE_CONTAINER_PATH + "/search";

    public static final String TEMPLATE_XML_SCHEMA_PATH = TEMPLATE_OM_PATH + "/xmlschema";

    public static final String TEMPLATE_CONTEXT_PATH = TEMPLATE_OM_PATH + "/context";

    public static final String TEMPLATE_CONTENT_MODEL_PATH = TEMPLATE_CMM_PATH + "/content-model";

    public static final String TEMPLATE_CONTEXT_VERSION = "0.4";

    public static final String TEMPLATE_ITEM_PATH = TEMPLATE_OM_PATH + "/item";

    public static final String TEMPLATE_ITEM_SEARCH_PATH = TEMPLATE_ITEM_PATH + "/search";

    public static final String TEMPLATE_ITEM_SEARCH_ADMIN_PATH = TEMPLATE_ITEM_SEARCH_PATH + "/admin";

    public static final String TEMPLATE_INGEST_PATH = TEMPLATE_OM_PATH + "/ingest";

    public static final String TEMPLATE_LANGUAGE_ITEMS_PATH = TEMPLATE_ITEM_PATH + "/language";

    public static final String TEMPLATE_TME_PATH = TEMPLATE_BASE_PATH + "/tme/template/tme/0.1";

    public static final String TEMPLATE_LICENSE_TYPE_PATH = TEMPLATE_OM_PATH + "/licensetype";

    public static final String TEMPLATE_ORGANIZATIONAL_UNIT_PATH = TEMPLATE_OUM_PATH + "/organizationalunit/0.9";

    public static final String TEMPLATE_SB_PATH = TEMPLATE_BASE_PATH + "/sb/template";

    public static final String TEMPLATE_SB_ORGANIZATIONAL_UNIT_PATH = TEMPLATE_SB_PATH + "/organizationalunit";

    public static final String TEMPLATE_SB_CONTEXT_PATH = TEMPLATE_SB_PATH + "/context";

    public static final String TEMPLATE_SB_CONTENT_MODEL_PATH = TEMPLATE_SB_PATH + "/contentmodel";

    public static final String TEMPLATE_SB_CONTENT_RELATION_PATH = TEMPLATE_SB_PATH + "/contentrelation";

    public static final String TEMPLATE_USER_ACCOUNT_PATH = TEMPLATE_AA_PATH + "/useraccount";

    public static final String TEMPLATE_USER_GROUP_PATH = TEMPLATE_AA_PATH + "/usergroup";

    public static final String TEMPLATE_SET_DEFINITION_PATH = TEMPLATE_OAI_PATH + "/setdefinition";

    public static final String TEMPLATE_OAIPROVIDERTEST_ITEM_PATH = TEMPLATE_OAI_PATH + "/item";

    public static final String TEMPLATE_OAIPROVIDERTEST_CONTAINER_PATH = TEMPLATE_OAI_PATH + "/container";

    public static final String TEMPLATE_SM_PATH = TEMPLATE_BASE_PATH + "/sm/template";

    public static final String TEMPLATE_STAT_DATA_PATH = TEMPLATE_SM_PATH + "/statisticdata";

    public static final String TEMPLATE_AGG_DEF_PATH = TEMPLATE_SM_PATH + "/aggregationdefinition";

    public static final String TEMPLATE_REP_DEF_PATH = TEMPLATE_SM_PATH + "/reportdefinition";

    public static final String TEMPLATE_REPORT_PATH = TEMPLATE_SM_PATH + "/report";

    public static final String TEMPLATE_SCOPE_PATH = TEMPLATE_SM_PATH + "/scope";

    public static final String TEMPLATE_REP_PARAMETERS_PATH = TEMPLATE_SM_PATH + "/reportparameters";

    public static final String TEMPLATE_PREPROCESSING_INFO_PATH = TEMPLATE_SM_PATH + "/preprocessinginformation";

    public static final String TEMPLATE_ST_ITEM_PATH = TEMPLATE_ST_PATH + "/item";

    public static final String ESCIDOC_OBJECTS_SAVE_PATH = "build/escidoc";

    public static final String VAR_COMPONENT_ID = "\\$\\{COMPONENT_ID\\}";

    public static final String VAR_MD_RECORD_NAME = "\\$\\{MD_RECORD_NAME\\}";

    public static final String STATUS_PENDING = "pending";

    public static final String STATUS_SUBMITTED = "submitted";

    public static final String STATUS_IN_REVISION = "in-revision";

    public static final String STATUS_RELEASED = "released";

    public static final String STATUS_WITHDRAWN = "withdrawn";

    public static final String CONTEXT_STATUS_CREATED = "created";

    public static final String CONTEXT_STATUS_OPENED = "opened";

    public static final String CONTEXT_STATUS_CLOSED = "closed";

    public static final String CONTEXT_STATUS_DELETED = "deleted";

    public static final String CONTENT_MODEL_STATUS_CREATED = "created";

    public static final String CONTENT_MODEL_STATUS_UPDATED = "updated";

    public static final String CONTENT_MODEL_STATUS_DELETED = "deleted";

    public static final String ORGANIZATIONAL_UNIT_STATUS_CREATED = "created";

    public static final String ORGANIZATIONAL_UNIT_STATUS_OPENED = "opened";

    public static final String ORGANIZATIONAL_UNIT_STATUS_CLOSED = "closed";

    public static final String ORGANIZATIONAL_UNIT_STATUS_DELETED = "deleted";

    protected static final String WITHDRAW_COMMENT = "This is a &lt; withdraw comment.";

    protected static Schema stagingFileSchema;

    protected static String startTimestamp = getNowAsTimestamp();

    /**
     * Xlink namespace prefix in templates.
     */
    public static final String XLINK_PREFIX_TEMPLATES = "prefix-xlink";

    /**
     * Xlink namespace prefix in documents retrieved from the eSciDoc.
     */
    public static final String XLINK_PREFIX_ESCIDOC = "xlink";

    /**
     * Xlink namespace declaration in templates.
     */
    public static final String XLINK_NS_DECL_TEPLATES =
        "xmlns:" + XLINK_PREFIX_TEMPLATES + "=\"" + Constants.NS_EXTERNAL_XLINK + "\" ";

    /**
     * Xlink namespace declaration in documents retrieved from the eSciDoc.
     */
    public static final String XLINK_NS_DECL_ESCIDOC =
        "xmlns:" + XLINK_PREFIX_ESCIDOC + "=\"" + Constants.NS_EXTERNAL_XLINK + "\" ";

    /**
     * Prefix and name of xlink href attribute in templates.
     */
    public static final String XLINK_HREF_TEMPLATES = XLINK_PREFIX_TEMPLATES + ":href";

    /**
     * Prefix and name of xlink href attribute in documents retrieved from the eSciDoc.
     */
    public static final String XLINK_HREF_ESCIDOC = XLINK_PREFIX_ESCIDOC + ":href";

    /**
     * Prefix and name of xlink type attribute in documents retrieved from the eSciDoc.
     */
    public static final String XLINK_TYPE_ESCIDOC = XLINK_PREFIX_ESCIDOC + ":type";

    public static final String XPATH_ATTRIBUTE = "/" + NAME_ATTRIBUTE;

    public static final String XPATH_RESOURCES = "/resources";

    public static final String XPATH_RESOURCES_BASE = XPATH_RESOURCES + "/@" + NAME_BASE;

    public static final String XPATH_RESOURCES_XLINK_HREF = XPATH_RESOURCES + "/@" + NAME_HREF;

    public static final String XPATH_RESOURCES_XLINK_TITLE = XPATH_RESOURCES + "/@" + NAME_TITLE;

    public static final String XPATH_RESOURCES_XLINK_TYPE = XPATH_RESOURCES + "/@" + NAME_TYPE;

    public static final String XPATH_RESOURCES_CURRENT_GRANTS = XPATH_RESOURCES + "/" + NAME_CURRENT_GRANTS;

    public static final String XPATH_RESOURCES_CURRENT_GRANTS_XLINK_HREF =
        XPATH_RESOURCES_CURRENT_GRANTS + "/@" + NAME_HREF;

    public static final String XPATH_RESOURCES_CURRENT_GRANTS_XLINK_TYPE =
        XPATH_RESOURCES_CURRENT_GRANTS + "/@" + NAME_TYPE;

    public static final String XPATH_RESOURCES_CURRENT_GRANTS_XLINK_TITLE =
        XPATH_RESOURCES_CURRENT_GRANTS + "/@" + NAME_TITLE;

    public static final String XPATH_USER_ACCOUNT = "/" + NAME_USER_ACCOUNT;

    public static final String XPATH_USER_ACCOUNT_LIST = "/" + NAME_USER_ACCOUNT_LIST;

    public static final String XPATH_USER_ACCOUNT_LIST_USER_ACCOUNT = XPATH_USER_ACCOUNT_LIST + "/" + NAME_USER_ACCOUNT;

    public static final String XPATH_USER_ACCOUNT_OBJID = XPATH_USER_ACCOUNT + "/@objid";

    public static final String XPATH_USER_ACCOUNT_XLINK_TITLE = XPATH_USER_ACCOUNT + "/@title";

    public static final String XPATH_USER_ACCOUNT_LAST_MOD_DATE = XPATH_USER_ACCOUNT + "/@last-modification-date";

    public static final String XPATH_USER_ACCOUNT_XLINK_TYPE = XPATH_USER_ACCOUNT + "/@type";

    public static final String XPATH_USER_ACCOUNT_XLINK_HREF = XPATH_USER_ACCOUNT + "/@href";

    public static final String XPATH_USER_ACCOUNT_PROPERTIES = XPATH_USER_ACCOUNT + "/properties";

    public static final String XPATH_USER_ACCOUNT_CREATED_BY = XPATH_USER_ACCOUNT_PROPERTIES + "/" + NAME_CREATED_BY;

    public static final String XPATH_USER_ACCOUNT_MODIFIED_BY = XPATH_USER_ACCOUNT_PROPERTIES + "/" + NAME_MODIFIED_BY;

    public static final String XPATH_USER_ACCOUNT_MODIFIED_BY_OBJID =
        XPATH_USER_ACCOUNT_MODIFIED_BY + "/@" + NAME_OBJID;

    public static final String XPATH_USER_ACCOUNT_MODIFIED_BY_XLINK_HREF =
        XPATH_USER_ACCOUNT_MODIFIED_BY + "/@" + NAME_HREF;

    public static final String XPATH_USER_ACCOUNT_MODIFIED_BY_XLINK_TITLE =
        XPATH_USER_ACCOUNT_MODIFIED_BY + "/@" + NAME_TITLE;

    public static final String XPATH_USER_ACCOUNT_MODIFIED_BY_XLINK_TYPE =
        XPATH_USER_ACCOUNT_MODIFIED_BY + "/@" + NAME_TYPE;

    public static final String XPATH_USER_ACCOUNT_CREATED_BY_OBJID = XPATH_USER_ACCOUNT_CREATED_BY + "/@" + NAME_OBJID;

    public static final String XPATH_USER_ACCOUNT_CREATED_BY_XLINK_HREF =
        XPATH_USER_ACCOUNT_CREATED_BY + "/@" + NAME_HREF;

    public static final String XPATH_USER_ACCOUNT_CREATED_BY_XLINK_TITLE =
        XPATH_USER_ACCOUNT_CREATED_BY + "/@" + NAME_TITLE;

    public static final String XPATH_USER_ACCOUNT_CREATED_BY_XLINK_TYPE =
        XPATH_USER_ACCOUNT_CREATED_BY + "/@" + NAME_TYPE;

    public static final String XPATH_USER_ACCOUNT_CREATION_DATE = XPATH_USER_ACCOUNT_PROPERTIES + "/creation-date";

    public static final String XPATH_USER_ACCOUNT_RESOURCES = XPATH_USER_ACCOUNT + "/" + NAME_RESOURCES;

    public static final String XPATH_USER_ACCOUNT_RESOURCES_XLINK_HREF = XPATH_USER_ACCOUNT_RESOURCES + PART_XLINK_HREF;

    public static final String XPATH_USER_ACCOUNT_RESOURCES_XLINK_TITLE =
        XPATH_USER_ACCOUNT_RESOURCES + PART_XLINK_TITLE;

    public static final String XPATH_USER_ACCOUNT_RESOURCES_XLINK_TYPE = XPATH_USER_ACCOUNT_RESOURCES + PART_XLINK_TYPE;

    public static final String XPATH_USER_ACCOUNT_CURRENT_GRANTS =
        XPATH_USER_ACCOUNT_RESOURCES + "/" + NAME_CURRENT_GRANTS;

    public static final String XPATH_USER_ACCOUNT_CURRENT_GRANTS_XLINK_TYPE =
        XPATH_USER_ACCOUNT_CURRENT_GRANTS + PART_XLINK_TYPE;

    public static final String XPATH_USER_ACCOUNT_ACTIVE = XPATH_USER_ACCOUNT_PROPERTIES + "/active";

    public static final String XPATH_USER_ACCOUNT_LOGINNAME = XPATH_USER_ACCOUNT_PROPERTIES + "/login-name";

    public static final String XPATH_USER_ACCOUNT_NAME = XPATH_USER_ACCOUNT_PROPERTIES + "/name";

    public static final String XPATH_USER_GROUP = "/" + NAME_USER_GROUP;

    public static final String XPATH_SET_DEFINITION = "/" + NAME_SET_DEFINITION;

    public static final String XPATH_SRW_RESPONSE_ROOT = "/searchRetrieveResponse";

    public static final String XPATH_SRW_RESPONSE_RECORD = XPATH_SRW_RESPONSE_ROOT + "/records/record";

    public static final String XPATH_SRW_RESPONSE_OBJECT_SUBPATH = "/recordData/search-result-record/";

    public static final String XPATH_SRW_RESPONSE_OBJECT =
        XPATH_SRW_RESPONSE_RECORD + XPATH_SRW_RESPONSE_OBJECT_SUBPATH;

    public static final String XPATH_USER_GROUP_LIST = "/" + NAME_USER_GROUP_LIST;

    public static final String XPATH_USER_GROUP_LIST_USER_GROUP = XPATH_USER_GROUP_LIST + "/" + NAME_USER_GROUP;

    public static final String XPATH_SRW_USER_GROUP_LIST_USER_GROUP = XPATH_SRW_RESPONSE_OBJECT + NAME_USER_GROUP;

    public static final String XPATH_SRW_CONTAINER_LIST_CONTAINER = XPATH_SRW_RESPONSE_OBJECT + NAME_CONTAINER;

    public static final String XPATH_SRW_ITEM_LIST_ITEM = XPATH_SRW_RESPONSE_OBJECT + NAME_ITEM;

    public static final String XPATH_SRW_SET_DEFINITION_LIST_SET_DEFINITION =
        XPATH_SRW_RESPONSE_OBJECT + NAME_SET_DEFINITION;

    public static final String XPATH_SRW_ORGANIZATIONAL_UNIT_LIST_ORGANIZATIONAL_UNIT =
        XPATH_SRW_RESPONSE_OBJECT + NAME_ORGANIZATIONAL_UNIT;

    public static final String XPATH_SRW_CONTENT_MODEL_LIST_CONTENT_MODEL =
        XPATH_SRW_RESPONSE_OBJECT + NAME_CONTENT_MODEL;

    public static final String XPATH_USER_GROUP_OBJID = XPATH_USER_GROUP + "/@objid";

    public static final String XPATH_USER_GROUP_XLINK_TITLE = XPATH_USER_GROUP + "/@title";

    public static final String XPATH_USER_GROUP_LAST_MOD_DATE = XPATH_USER_GROUP + "/@last-modification-date";

    public static final String XPATH_USER_GROUP_XLINK_TYPE = XPATH_USER_GROUP + "/@type";

    public static final String XPATH_USER_GROUP_XLINK_HREF = XPATH_USER_GROUP + "/@href";

    public static final String XPATH_USER_GROUP_PROPERTIES = XPATH_USER_GROUP + "/properties";

    public static final String XPATH_USER_GROUP_SELECTORS = XPATH_USER_GROUP + "/selectors";

    public static final String XPATH_USER_GROUP_SELECTOR = XPATH_USER_GROUP + "/selectors/selector";

    public static final String XPATH_USER_GROUP_CREATED_BY = XPATH_USER_GROUP_PROPERTIES + "/" + NAME_CREATED_BY;

    public static final String XPATH_USER_GROUP_MODIFIED_BY = XPATH_USER_GROUP_PROPERTIES + "/" + NAME_MODIFIED_BY;

    public static final String XPATH_USER_GROUP_MODIFIED_BY_OBJID = XPATH_USER_GROUP_MODIFIED_BY + "/@" + NAME_OBJID;

    public static final String XPATH_USER_GROUP_MODIFIED_BY_XLINK_HREF =
        XPATH_USER_GROUP_MODIFIED_BY + "/@" + NAME_HREF;

    public static final String XPATH_USER_GROUP_MODIFIED_BY_XLINK_TITLE =
        XPATH_USER_GROUP_MODIFIED_BY + "/@" + NAME_TITLE;

    public static final String XPATH_USER_GROUP_MODIFIED_BY_XLINK_TYPE =
        XPATH_USER_GROUP_MODIFIED_BY + "/@" + NAME_TYPE;

    public static final String XPATH_USER_GROUP_CREATED_BY_OBJID = XPATH_USER_GROUP_CREATED_BY + "/@" + NAME_OBJID;

    public static final String XPATH_USER_GROUP_CREATED_BY_XLINK_HREF = XPATH_USER_GROUP_CREATED_BY + "/@" + NAME_HREF;

    public static final String XPATH_USER_GROUP_CREATED_BY_XLINK_TITLE =
        XPATH_USER_GROUP_CREATED_BY + "/@" + NAME_TITLE;

    public static final String XPATH_USER_GROUP_CREATED_BY_XLINK_TYPE = XPATH_USER_GROUP_CREATED_BY + "/@" + NAME_TYPE;

    public static final String XPATH_USER_GROUP_CREATION_DATE = XPATH_USER_GROUP_PROPERTIES + "/creation-date";

    public static final String XPATH_USER_GROUP_RESOURCES = XPATH_USER_GROUP + "/" + NAME_RESOURCES;

    public static final String XPATH_USER_GROUP_RESOURCES_XLINK_HREF = XPATH_USER_GROUP_RESOURCES + PART_XLINK_HREF;

    public static final String XPATH_USER_GROUP_RESOURCES_XLINK_TITLE = XPATH_USER_GROUP_RESOURCES + PART_XLINK_TITLE;

    public static final String XPATH_USER_GROUP_RESOURCES_XLINK_TYPE = XPATH_USER_GROUP_RESOURCES + PART_XLINK_TYPE;

    public static final String XPATH_USER_GROUP_CURRENT_GRANTS = XPATH_USER_GROUP_RESOURCES + "/" + NAME_CURRENT_GRANTS;

    public static final String XPATH_USER_GROUP_CURRENT_GRANTS_XLINK_TYPE =
        XPATH_USER_GROUP_CURRENT_GRANTS + PART_XLINK_TYPE;

    public static final String XPATH_USER_GROUP_ACTIVE = XPATH_USER_GROUP_PROPERTIES + "/active";

    public static final String XPATH_USER_GROUP_NAME = XPATH_USER_GROUP_PROPERTIES + "/name";

    public static final String XPATH_USER_GROUP_LABEL = XPATH_USER_GROUP_PROPERTIES + "/label";

    public static final String XPATH_GRANT = "/grant";

    public static final String XPATH_GRANT_PROPERTIES = XPATH_GRANT + "/" + NAME_PROPERTIES;

    public static final String XPATH_GRANT_GRANT_REMARK = XPATH_GRANT_PROPERTIES + "/" + NAME_GRANT_REMARK;

    public static final String XPATH_GRANT_ROLE = XPATH_GRANT_PROPERTIES + "/" + NAME_ROLE;

    public static final String XPATH_GRANT_ROLE_XLINK_TITLE = XPATH_GRANT_ROLE + PART_XLINK_TITLE;

    public static final String XPATH_GRANT_ROLE_XLINK_HREF = XPATH_GRANT_ROLE + PART_XLINK_HREF;

    public static final String XPATH_GRANT_ROLE_OBJID = XPATH_GRANT_ROLE + "/@" + NAME_OBJID;

    public static final String XPATH_GRANT_OBJECT = XPATH_GRANT_PROPERTIES + "/" + EscidocAbstractTest.NAME_ASSIGNED_ON;

    public static final String XPATH_GRANT_OBJECT_XLINK_TITLE = XPATH_GRANT_OBJECT + PART_XLINK_TITLE;

    public static final String XPATH_GRANT_OBJECT_XLINK_HREF = XPATH_GRANT_OBJECT + PART_XLINK_HREF;

    public static final String XPATH_GRANT_OBJECT_OBJID = XPATH_GRANT_OBJECT + PART_OBJID;

    public static final String XPATH_SCOPE = "/scope";

    public static final String XPATH_STATISTIC_DATA_SCOPE = "/statistic-record/scope";

    public static final String XPATH_AGGREGATION_DEFINITION_SCOPE = "/aggregation-definition/scope";

    public static final String XPATH_REPORT_DEFINITION_SCOPE = "/report-definition/scope";

    public static final String XPATH_SCOPE_OBJID = XPATH_SCOPE + PART_OBJID;

    public static final String XPATH_STATISTIC_DATA_SCOPE_OBJID = XPATH_STATISTIC_DATA_SCOPE + PART_OBJID;

    public static final String XPATH_AGGREGATION_DEFINITION = "/aggregation-definition";

    public static final String XPATH_REPORT_DEFINITION = "//report-definition";

    public static final String XPATH_REPORT_DEFINITION_OBJID = XPATH_REPORT_DEFINITION + PART_OBJID;

    public static final String XPATH_REPORT = "/report";

    public static final String XPATH_REPORT_REPORT_DEFINITION = "/report/report-definition";

    public static final String ENTITY_REFERENCES =
        "A &lt; &gt; &amp; &amp;lt; &amp;gt; &amp;amp; &amp;quot; &amp;apos; Z";

    public static final String XLINK_TYPE_VALUE = "simple";

    public static final String NAME_ADMIN_DESCRIPTOR = "admin-descriptor";

    public static final String NAME_ADMIN_DESCRIPTORS = "admin-descriptors";

    private static final Pattern PATTERN_GET_ID_FROM_URI_OR_FEDORA_ID = Pattern.compile(".*/([^/>]+)>?");

    private static String REPOSITORY_VERSION = null;

    private static String baseHost = null;

    private static String basePort = null;

    private static String frameworkContext = null;

    private static String fedoragsearchContext = null;

    private static String oaiproviderContext = null;

    private static String fedoraContext = null;

    private static String testdataContext = null;

    private static String srwContext = null;

    private static String baseUrl = null;

    private AdminClient adminClient = null;

    private ItemClient itemClient = null;

    private IngestClient ingestClient = null;

    private ContainerClient containerClient = null;

    private ContextClient contextClient = null;

    private ContentRelationClient contentRelationClient = null;

    private DeviationClient deviationClient = null;

    private OrganizationalUnitClient ouClient = null;

    /**
     * Tear down. Resets the user handle in <code>PWCallback</code>.
     *
     * @throws Exception If an error occurs.
     */
    @After
    public void tearDown() throws Exception {
        PWCallback.resetHandle();
    }

    /**
     * @return Returns the client to use in the test.
     * @throws Exception If anything fails.
     */
    public ResourceHandlerClientInterface getClient() throws Exception {

        // get the current stack position
        try {
            throw new Exception();
        }
        catch (final Exception e) {
            throw new UnsupportedOperationException("getClient() not implemented by this test class.", e);
        }
    }

    /**
     * @return Returns the adminClient.
     */
    public AdminClient getAdminClient() {
        if (this.adminClient == null) {
            this.adminClient = new AdminClient();
        }
        return adminClient;
    }

    /**
     * @return Returns the itemClient.
     */
    public ItemClient getItemClient() {
        if (this.itemClient == null) {
            this.itemClient = new ItemClient();
        }
        return itemClient;
    }

    /**
     * @return Returns the IngestClient.
     */
    public IngestClient getIngestClient() {
        if (this.ingestClient == null) {
            this.ingestClient = new IngestClient();
        }
        return this.ingestClient;
    }

    /**
     * @return Returns the containerClient.
     */
    public ContainerClient getContainerClient() {
        if (this.containerClient == null) {
            this.containerClient = new ContainerClient();
        }
        return containerClient;
    }

    /**
     * @return Returns the contextClient.
     */
    public ContextClient getContextClient() {
        if (this.contextClient == null) {
            this.contextClient = new ContextClient();
        }
        return contextClient;
    }

    /**
     * @return Returns the contentRelationClient.
     */
    public ContentRelationClient getContentRelationClient() {
        if (this.contentRelationClient == null) {
            this.contentRelationClient = new ContentRelationClient();
        }
        return contentRelationClient;
    }

    /**
     * @return Returns the DeviationClient.
     */
    public DeviationClient getDeviationClient() {
        if (this.deviationClient == null) {
            this.deviationClient = new DeviationClient();
        }
        return this.deviationClient;
    }

    /**
     * @return Returns the OrganizationalUnitClient.
     */
    public OrganizationalUnitClient getOrganizationalUnitClient() {
        if (this.ouClient == null) {
            this.ouClient = new OrganizationalUnitClient();
        }
        return this.ouClient;
    }

    /**
     * Returns the xml data of the provided result.
     *
     * @param result The object holding the result.
     * @return Returns the xml string.
     * @throws Exception If anything fails.
     */
    protected String handleXmlResult(final Object result) throws Exception {

        String xmlResult = null;
        if (result instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result;
            assertHttpStatusOfMethod("", httpRes);
            assertContentTypeTextXmlUTF8OfMethod("", httpRes);
            xmlResult = EntityUtil.toString(httpRes.getEntity(), HTTP.UTF_8);
        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }

    /**
     * Grab the object id from URI.
     *
     * @param uri Fedora URI with objid .
     * @return object id
     */
    public static String getIdFromURI(final String uri) {

        if (uri == null) {
            return null;
        }
        Matcher matcher = PATTERN_GET_ID_FROM_URI_OR_FEDORA_ID.matcher(uri);
        if (matcher.find()) {
            return matcher.group(1);
        }
        else {
            return uri;
        }
    }

    /**
     * Test creating a resource using the specified resource handler.<br> The client to use is determined by getClient()
     * that must be implemented by the concrete test class.
     *
     * @param resourceXml The xml representation of the resource.
     * @return The xml representation of the created resource.
     * @throws Exception If anything fails.
     */
    public String create(final String resourceXml) throws Exception {

        EtmPoint point = ETM_MONITOR.createPoint("EscidocTestBase:create");
        try {
            return handleXmlResult(getClient().create(resourceXml));
        }
        finally {
            point.collect();
        }
    }

    /**
     * Test deleting a resource from the framework.<br> The client to use is determined by getClient() that must be
     * implemented by the concrete test class.
     *
     * @param id The id of the resource.
     * @throws Exception If anything fails.
     */
    public void delete(final String id) throws Exception {

        Object result = getClient().delete(id);
        if (result instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result;
            assertHttpStatusOfMethod("", httpRes);
        }
    }

    /**
     * Test retrieving a resource from the framework.<br> The client to use is determined by getClient() that must be
     * implemented by the concrete test class.
     *
     * @param id The id of the resource.
     * @return The retrieved resource.
     * @throws Exception If anything fails.
     */
    public String retrieve(final String id) throws Exception {

        EtmPoint point = ETM_MONITOR.createPoint("EscidocTestBase:retrieve");
        try {
            return handleXmlResult(getClient().retrieve(id));
        }
        finally {
            point.collect();
        }

    }

    /**
     * Test retrieving the virtual resources of a resource from the framework.<br> The client to use is determined by
     * getClient() that must be implemented by the concrete test class.
     *
     * @param id The id of the resource.
     * @return The retrieved virtual resources.
     * @throws Exception If anything fails.
     */
    public String retrieveResources(final String id) throws Exception {

        return handleXmlResult(getClient().retrieveResources(id));
    }

    /**
     * Test updating a resource of the framework.<br> The client to use is determined by getClient() that must be
     * implemented by the concrete test class.
     *
     * @param id          The id of the resource.
     * @param resourceXml The xml representation of the resource.
     * @return The updated resource.
     * @throws Exception If anything fails.
     */
    public String update(final String id, final String resourceXml) throws Exception {

        EtmPoint point = ETM_MONITOR.createPoint("EscidocTestBase:update");
        try {
            return handleXmlResult(getClient().update(id, resourceXml));
        }
        finally {
            point.collect();
        }

    }

    /**
     * Get the host name of the framework (read from properties).
     *
     * @return the host name of the framework
     */
    public static String getBaseHost() {
        if (baseHost == null) {
            baseHost = PropertiesProvider.getInstance().getProperty("server.name", "localhost");
        }
        return baseHost;
    }

    /**
     * Get the port number of the framework (read from properties).
     *
     * @return the port number of the framework
     */
    public static String getBasePort() {
        if (basePort == null) {
            basePort = PropertiesProvider.getInstance().getProperty("server.port", "8080");
        }
        return basePort;
    }

    /**
     * Get the context-path of the framework (read from properties).
     *
     * @return the context of the framework
     */
    public static String getFrameworkContext() {
        if (frameworkContext == null) {
            frameworkContext = PropertiesProvider.getInstance().getProperty("server.context", "/escidoc");
            if (!frameworkContext.startsWith("/")) {
                frameworkContext = "/" + frameworkContext;
            }
        }
        return frameworkContext;
    }

    /**
     * Get the context-path of fedoragsearch (read from properties).
     *
     * @return the context of fedoragsearch
     */
    public static String getFedoragsearchContext() {
        if (fedoragsearchContext == null) {
            fedoragsearchContext =
                PropertiesProvider.getInstance().getProperty("fedoragsearch.context", "/fedoragsearch");
        }
        return fedoragsearchContext;
    }

    /**
     * Get the context-path of oaiprovider (read from properties).
     *
     * @return the context of oaiprovider
     */
    public static String getOaiproviderContext() {
        if (oaiproviderContext == null) {
            oaiproviderContext =
                PropertiesProvider.getInstance().getProperty("oaiprovider.context", "/escidoc-oaiprovider/");
        }
        return oaiproviderContext;
    }

    /**
     * Get the context-path of fedora (read from properties).
     *
     * @return the context of fedora
     */
    public static String getFedoraContext() {
        if (fedoraContext == null) {
            fedoraContext = PropertiesProvider.getInstance().getProperty("fedora.context", "/fedora");
        }
        return fedoraContext;
    }

    /**
     * Get the context-path of testdata (read from properties).
     *
     * @return the context of testdata
     */
    public static String getTestdataContext() {
        if (testdataContext == null) {
            testdataContext = PropertiesProvider.getInstance().getProperty("testdata.context", "/testdata");
        }
        return testdataContext;
    }

    /**
     * Get the context-path of srw (read from properties).
     *
     * @return the context of srw
     */
    public static String getSrwContext() {
        if (srwContext == null) {
            srwContext = PropertiesProvider.getInstance().getProperty("srw.context", "/srw");
        }
        return srwContext;
    }

    /**
     * Get the href of the framework (read from properties).
     *
     * @return the href of the framework.
     */
    public static String getBaseUrl() {
        if (baseUrl == null) {
            baseUrl = Constants.HTTP_PROTOCOL + "://" + getBaseHost() + ":" + getBasePort();
        }
        return baseUrl;
    }

    /**
     * Serialize the given Dom Object to a String.
     *
     * @param xml                The Xml Node to serialize.
     * @param omitXMLDeclaration Indicates if XML declaration will be omitted.
     * @return The String representation of the Xml Node.
     * @throws Exception If anything fails.
     */
    public static String toString(final Node xml, final boolean omitXMLDeclaration) throws Exception {
        String result;
        if (xml instanceof AttrImpl) {
            result = xml.getTextContent();
        }
        else if (xml instanceof Document) {
            StringWriter stringOut = new StringWriter();
            // format
            OutputFormat format = new OutputFormat((Document) xml);
            format.setIndenting(true);
            format.setPreserveSpace(true);
            format.setOmitXMLDeclaration(omitXMLDeclaration);
            format.setEncoding(Constants.DEFAULT_CHARSET);
            // serialize
            XMLSerializer serial = new XMLSerializer(stringOut, format);
            serial.asDOMSerializer();

            serial.serialize((Document) xml);
            result = stringOut.toString();
        }
        else {
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
            LSOutput lsOutput = impl.createLSOutput();
            lsOutput.setEncoding(Constants.DEFAULT_CHARSET);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            lsOutput.setByteStream(os);
            LSSerializer writer = impl.createLSSerializer();
            // result = writer.writeToString(xml);
            writer.write(xml, lsOutput);
            result = ((ByteArrayOutputStream) lsOutput.getByteStream()).toString(Constants.DEFAULT_CHARSET);
            if (omitXMLDeclaration && result.contains("?>")) {
                result = result.substring(result.indexOf("?>") + 2);
            }
            // result = toString(getDocument(writer.writeToString(xml)),
            // true);
        }
        return result;
    }

    /**
     * Delete an Element from a Node.
     *
     * @param node  the node.
     * @param xPath The xPath selecting the element.
     * @return The resulting node.
     * @throws Exception If anything fails.
     */
    public static Node deleteElement(final Node node, final String xPath) throws Exception {

        Node delete = selectSingleNode(node, xPath);
        assertNotNull("No node found for provided xpath [" + xPath + "]", delete);
        if (delete instanceof AttrImpl) {
            throw new Exception("Removal of Element not successful! " + "xPath selects an Attribute!");
        }
        else {
            delete.getParentNode().removeChild(delete);
        }
        return node;
    }

    /**
     * Delete all Elements from a Node.
     *
     * @param node  the node.
     * @param xPath The xPath selecting the element.
     * @return The resulting node.
     * @throws Exception If anything fails.
     */
    public static Node deleteElements(final Node node, final String xPath) throws Exception {

        NodeList nodes = selectNodeList(node, xPath);
        for (int i = 0; i < nodes.getLength(); ++i) {
            deleteElement(node, xPath);
        }
        return node;
    }

    /**
     * Return the text value of the selected attribute.
     *
     * @param node          The node.
     * @param xPath         The xpath to select the node contain the attribute,
     * @param attributeName The name of the attribute.
     * @return The text value of the selected attribute.
     * @throws Exception If anything fails.
     */
    public static String getAttributeValue(final Node node, final String xPath, final String attributeName)
        throws Exception {
        String result = null;
        Node element = selectSingleNode(node, xPath);
        if (element != null && element.hasAttributes()) {
            for (int i = 0; i < element.getAttributes().getLength(); ++i) {
                String nodeName = element.getAttributes().item(i).getNodeName();
                if (nodeName.endsWith(":" + attributeName) || nodeName.equals(attributeName)) {
                    result = element.getAttributes().getNamedItem(nodeName).getTextContent();
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Delete an Attribute from an Element of a Node.
     *
     * @param node          the node.
     * @param xPath         The xPath selecting the element.
     * @param attributeName The name of the attribute.
     * @return The resulting node.
     * @throws Exception If anything fails.
     */
    public static Node deleteAttribute(final Node node, final String xPath, final String attributeName)
        throws Exception {

        if (node == null) {
            return node;
        }
        Node delete = selectSingleNode(node, xPath);
        if (delete == null) {
            return node;
        }
        if (delete.hasAttributes()) {
            for (int i = 0; i < delete.getAttributes().getLength(); ++i) {
                String nodeName = delete.getAttributes().item(i).getNodeName();
                if (nodeName.endsWith(":" + attributeName) || nodeName.equals(attributeName)) {
                    delete.getAttributes().removeNamedItem(nodeName);
                    break;
                }
            }
        }
        return node;
    }

    /**
     * Delete an Attribute from an Element of a Node.
     *
     * @param node  the node.
     * @param xPath The xPath selecting the attribute.
     * @return The resulting node.
     * @throws Exception If anything fails.
     */
    public static Node deleteAttribute(final Node node, final String xPath) throws Exception {
        final int index = xPath.lastIndexOf('/');
        final String elementXpath = xPath.substring(0, index);
        final String attrName = xPath.substring(index + 2);
        return deleteAttribute(node, elementXpath, attrName);
    }

    /**
     * Substitute the element selected by the xPath in the given node with the new value.
     *
     * @param node     The node.
     * @param xPath    The xPath.
     * @param newValue The newValue.
     * @return The resulting node after the substitution.
     * @throws Exception If anything fails.
     */
    public static Node substitute(final Node node, final String xPath, final String newValue) throws Exception {
        Node replace = selectSingleNode(node, xPath);
        assertNotNull("No node found for specified xpath [" + xPath + "]", replace);
        // if (replace.getNodeType() == Node.ELEMENT_NODE) {
        replace.setTextContent(newValue);
        // }
        // else if (replace.getNodeType() == Node.ATTRIBUTE_NODE) {
        // replace.setNodeValue(newValue);
        // }
        // else {
        // throw new Exception("Unsupported node type '"
        // + replace.getNodeType() + "' in EscidocTestBase.substitute.");
        // }
        return node;
    }

    /**
     * Substitute the element selected by the xPath in the given node with the new value.
     *
     * @param node     The node.
     * @param xPath    The xPath.
     * @param newValue The newValue.
     * @return The resulting node after the substitution.
     * @throws Exception If anything fails.
     */
    public Node substituteId(final Node node, final String xPath, final String newValue) throws Exception {
        Node replace = selectSingleNode(node, xPath + "/@href");
        String path = replace.getTextContent().substring(0, replace.getTextContent().lastIndexOf("/") + 1);

        assertNotNull("No node found for specified xpath [" + xPath + "]", replace);
        // if (replace.getNodeType() == Node.ELEMENT_NODE) {
        replace.setTextContent(path + newValue);
        // }
        // else if (replace.getNodeType() == Node.ATTRIBUTE_NODE) {
        // replace.setNodeValue(newValue);
        // }
        // else {
        // throw new Exception("Unsupported node type '"
        // + replace.getNodeType() + "' in EscidocTestBase.substitute.");
        // }
        return node;
    }

    /**
     * Gets the prefix of the provided node.<br> This returns Node.getPrefix() if this is not null. Otherwise, ittries
     * to extract the prefix from Node.getNodeName(). If this fails, null is returned.
     *
     * @param node The node to get the prefix from.
     * @return Returns the determined prefix or null.
     * @throws Exception Thrown if anything fails.
     */
    public static String getPrefix(final Node node) throws Exception {

        String prefix = node.getPrefix();
        if (prefix == null) {
            String nodeName = node.getNodeName();
            int index = nodeName.indexOf(":");
            if (index != -1) {
                prefix = nodeName.substring(0, index);
            }
        }
        return prefix;
    }

    /**
     * Creates a new element node for the provided document. The created element is an element that refers to another
     * resource, i.e. it has xlink attributes and an objid attribute.
     *
     * @param doc          The document for that the node shall be created.
     * @param namespaceUri The name space uri of the node to create. This may be null.
     * @param prefix       The prefix to use.
     * @param tagName      The tag name of the node.
     * @param xlinkPrefix  The prefix to use for the xlink attributes.
     * @param title        The title of the referencing element (=xlink:title)
     * @param href         The href of the referencing element (=xlink:href). The objid attribute value is extracted
     *                     from this href.
     * @return Returns the created node.
     * @throws Exception Thrown if anything fails.
     */
    public Element createReferencingElementNode(
        final Document doc, final String namespaceUri, final String prefix, final String tagName,
        final String xlinkPrefix, final String title, final String href) throws Exception {

        Element newElement = createElementNodeWithXlink(doc, namespaceUri, prefix, tagName, xlinkPrefix, title, href);

        Attr objidAttr = createAttributeNode(doc, null, null, NAME_OBJID, getObjidFromHref(href));
        newElement.getAttributes().setNamedItemNS(objidAttr);

        return newElement;
    }

    /**
     * Creates a new element node for the provided document. The created element is an element that that has xlink
     * attributes, but does not have an objid attribute.
     *
     * @param doc          The document for that the node shall be created.
     * @param namespaceUri The name space uri of the node to create. This may be null.
     * @param prefix       The prefix to use.
     * @param tagName      The tag name of the node.
     * @param xlinkPrefix  The prefix to use for the xlink attributes.
     * @param title        The title of the referencing element (=xlink:title)
     * @param href         The href of the referencing element (=xlink:href). The objid attribute value is extracted
     *                     from this href.
     * @return Returns the created node.
     * @throws Exception Thrown if anything fails.
     */
    public static Element createElementNodeWithXlink(
        final Document doc, final String namespaceUri, final String prefix, final String tagName,
        final String xlinkPrefix, final String title, final String href) throws Exception {

        Element newElement = createElementNode(doc, namespaceUri, prefix, tagName, null);
        Attr xlinkTypeAttr = createAttributeNode(doc, Constants.NS_EXTERNAL_XLINK, xlinkPrefix, NAME_TYPE, "simple");
        Attr xlinkTitleAttr = createAttributeNode(doc, Constants.NS_EXTERNAL_XLINK, xlinkPrefix, NAME_TITLE, title);
        Attr xlinkHrefAttr = createAttributeNode(doc, Constants.NS_EXTERNAL_XLINK, xlinkPrefix, NAME_HREF, href);
        newElement.getAttributes().setNamedItemNS(xlinkTypeAttr);
        newElement.getAttributes().setNamedItemNS(xlinkTitleAttr);
        newElement.getAttributes().setNamedItemNS(xlinkHrefAttr);

        return newElement;
    }

    /**
     * Creates a new element node for the provided document.
     *
     * @param doc          The document for that the node shall be created.
     * @param namespaceUri The name space uri of the node to create. This may be null.
     * @param prefix       The prefix to use.
     * @param tagName      The tag name of the node.
     * @param textContent  The text content of the node. This may be null.
     * @return Returns the created node.
     * @throws Exception Thrown if anything fails.
     */
    public static Element createElementNode(
        final Document doc, final String namespaceUri, final String prefix, final String tagName,
        final String textContent) throws Exception {

        Element newNode = doc.createElementNS(namespaceUri, tagName);
        newNode.setPrefix(prefix);
        if (textContent != null) {
            newNode.setTextContent(textContent);
        }
        return newNode;
    }

    /**
     * Creates a new attribute node for the provided document.
     *
     * @param doc          The document for that the node shall be created.
     * @param namespaceUri The name space uri of the node to create. This may be null.
     * @param prefix       The prefix to use.
     * @param tagName      The tag name of the node.
     * @param value        The attribute value.
     * @return Returns the created node.
     * @throws Exception Thrown if anything fails.
     */
    public static Attr createAttributeNode(
        final Document doc, final String namespaceUri, final String prefix, final String tagName, final String value)
        throws Exception {

        Attr newAttribute = doc.createAttributeNS(namespaceUri, tagName);
        newAttribute.setPrefix(prefix);
        newAttribute.setValue(value);
        if (value != null) {
            newAttribute.setTextContent(value);
        }
        return newAttribute;
    }

    /**
     * Gets the prefix of the node selected by the xpath in the provided node.<br> This returns Node.getPrefix() if this
     * is not null. Otherwise, ittries to extract the prefix from Node.getNodeName(). If this fails, null is returned.
     *
     * @param node  The node to get the prefix from.
     * @param xPath XPath to the Node to select the prefix from.
     * @return Returns the determined prefix or null.
     * @throws Exception Thrown if anything fails.
     */
    public static String getPrefix(final Node node, final String xPath) throws Exception {
        return getPrefix(selectSingleNode(node, xPath));
    }

    /**
     * Adds the provided new node as the child of the element selected by the xPath in the given node.
     *
     * @param node    The node.
     * @param xPath   The xPath.
     * @param newNode The new node.
     * @return The resulting node after the substitution.
     * @throws Exception If anything fails.
     */
    public static Node addAsChild(final Node node, final String xPath, final Element newNode) throws Exception {
        Node parent = selectSingleNode(node, xPath);
        assertNotNull("No node for xpath found [" + xPath + "]", parent);
        // inserts at end of list
        parent.insertBefore(newNode, null);
        return node;
    }

    /**
     * Adds the provided new node after the element selected by the xPath in the given node.
     *
     * @param node    The node.
     * @param xPath   The xPath.
     * @param newNode The new node.
     * @return The resulting node after the substitution.
     * @throws Exception If anything fails.
     */
    public static Node addAfter(final Node node, final String xPath, final Node newNode) throws Exception {
        Node before = selectSingleNode(node, xPath);
        assertNotNull("No node for xpath [" + xPath + "] found", before);
        Node parent = before.getParentNode();
        parent.insertBefore(newNode, before.getNextSibling());
        return node;
    }

    /**
     * Adds the provided new node before the element selected by the xPath in the given node.
     *
     * @param node    The node.
     * @param xPath   The xPath.
     * @param newNode The new node.
     * @return The resulting node after the substitution.
     * @throws Exception If anything fails.
     */
    public static Node addBefore(final Node node, final String xPath, final Node newNode) throws Exception {
        Node after = selectSingleNode(node, xPath);
        assertNotNull("No node for xpath found [" + xPath + "]", after);
        Node parent = after.getParentNode();
        parent.insertBefore(newNode, after);
        return node;
    }

    /**
     * Adds the provided new attribute node to the element selected by the xPath in the given node.
     *
     * @param node          The node.
     * @param xPath         The xPath.
     * @param attributeNode The new attribute node.
     * @return The resulting node after the substitution.
     * @throws Exception If anything fails.
     */
    public static Node addAttribute(final Node node, final String xPath, final Attr attributeNode) throws Exception {
        Node element = selectSingleNodeAsserted(node, xPath);
        NamedNodeMap attributes = element.getAttributes();
        attributes.setNamedItemNS(attributeNode);
        return node;
    }

    /**
     * Substitute the element selected by the xPath in the given node with the new node.
     *
     * @param node    The node.
     * @param xPath   The xPath.
     * @param newNode The new node.
     * @return The resulting node after the substitution.
     * @throws Exception If anything fails.
     */
    public static Node substitute(final Node node, final String xPath, final Node newNode) throws Exception {
        Node result = node;
        Node replace = selectSingleNode(result, xPath);
        assertNotNull("No node selected for substitute. ", replace);
        Node parent = replace.getParentNode();
        parent.replaceChild(newNode, replace);
        return result;
    }

    public static int getNoOfSelections(final Node node, final String xPath) throws TransformerException {
        int result = 0;
        NodeList matches = selectNodeList(node, xPath);
        if (matches != null) {
            result = matches.getLength();
        }
        return result;
    }

    /**
     * Return a filter parameter including only an empty filter.
     *
     * @return The filter parameter including only an empty filter.
     * @throws Exception If anything fails.
     */
    public static String getEmptyFilter() throws Exception {
        return EscidocAbstractTest.getTemplateAsString(TEMPLATE_OM_COMMON_PATH, "emptyFilter.xml");
    }

    /**
     * Get the filter parameter for retrieving contexts matching the given filter criteria. If a criteria is null the
     * filter is deleted from the parameter.
     *
     * @param user        The expected user.
     * @param role        The expected role.
     * @param contextType The expected type of the context.
     * @return The filter parameter.
     * @throws Exception If anything fails.
     */
    public static Map<String, String[]> getFilterRetrieveContexts(
        final String user, final String role, final String contextType) throws Exception {
        Map<String, String[]> result = new HashMap<String, String[]>();
        StringBuilder filter = new StringBuilder();

        if ((user != null) && (user.length() > 0)) {
            filter.append("user=\"").append(user).append("\"");
        }
        if ((role != null) && (role.length() > 0)) {
            if (filter.length() > 0) {
                filter.append(" and ");
            }
            filter.append("role=\"").append(role).append("\"");
        }
        if ((contextType != null) && (contextType.length() > 0)) {
            if (filter.length() > 0) {
                filter.append(" and ");
            }
            filter.append("\"/properties/type\"=\"").append(contextType).append("\"");
        }
        if (filter.length() > 0) {
            result.put("query", new String[] { filter.toString() });
        }
        result.put("maximumRecords", new String[] { "1000" });
        return result;
    }

    /**
     * Get the filter parameter for retrieving members of a context matching the given filter criteria. If a criteria is
     * null the filter is deleted from the parameter.
     *
     * @param members     A list of members to restrict the resulting members.
     * @param objectType  The type of the object (item or container).
     * @param user        The expected user.
     * @param role        The expected role.
     * @param status      The expected status of the resulting objects.
     * @param contentType The expected contentType of the resulting objects.
     * @return The filter parameter.
     * @throws Exception If anything fails.
     */
    public static String getFilterRetrieveMembersOfContext(
        final List<String> members, final String objectType, final String user, final String role, final String status,
        final String contentType) throws Exception {

        Document filter =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_OM_COMMON_PATH, "filterRetrieveMembersOfContext.xml");
        if ((members != null) && (members.size() > 0)) {
            for (int i = 0; i < 5; ++i) {
                String value;
                try {
                    value = members.get(i);
                    filter =
                        (Document) replaceInFilter(filter, value, "/param/filter[@name=\"members\"]/id[" + (i + 1)
                            + "]");
                }
                catch (final RuntimeException e) {
                    filter =
                        (Document) replaceInFilter(filter, null, "/param/filter[@name=\"members\"]/id["
                            + (members.size() + 1) + "]");
                }
            }
        }
        else {
            filter = (Document) replaceInFilter(filter, null, "param/filter[@name=\"members\"]");
        }

        filter =
            (Document) replaceInFilter(filter, objectType, "param/filter[@name=\"http://www.w3.org/1999/02/"
                + "22-rdf-syntax-ns#type\"]");
        filter = (Document) replaceInFilter(filter, user, "param/filter[@name=\"user\"]");
        filter = (Document) replaceInFilter(filter, role, "param/filter[@name=\"role\"]");

        filter =
            (Document) replaceInFilter(filter, status, "param/filter[@name=\"http://escidoc.de/core/01/"
                + "properties/public-status\"]");
        filter =
            (Document) replaceInFilter(filter, contentType, "param/filter[@name=\"http://escidoc.de/core/01/"
                + "structural-relations/content-model\"]");
        return toString(filter, true);
    }

    /**
     * If value is null the element selected by xPath is removed from the filter otherwise the elements value is set to
     * vlaue.
     *
     * @param filter The filter parameter.
     * @param value  The value.
     * @param xPath  The xPath.
     * @return The resulting filter parameter.
     * @throws Exception If anything fails.
     */
    public static Node replaceInFilter(final Node filter, final String value, final String xPath) throws Exception {

        if (value != null) {
            return substitute(filter, xPath, value);
        }
        else {
            return deleteElement(filter, xPath);
        }

    }

    /**
     * Gets the last modification date from the resource through retrieve.
     *
     * @param id The id of the Resource.
     * @return last-modification-date
     * @throws Exception Thrown if anything fails.
     */
    public String getTheLastModificationDate(final String id) throws Exception {

        Document resource = EscidocAbstractTest.getDocument(retrieve(id));
        return getTheLastModificationDate(resource);
    }

    /**
     * Gets the last modification date from the Resource.
     *
     * @param resource The Resource.
     * @return last-modification-date
     * @throws Exception Thrown if anything fails.
     */
    public String getTheLastModificationDate(final Document resource) throws Exception {

        // get last-modification-date
        NamedNodeMap atts = resource.getDocumentElement().getAttributes();
        Node lastModificationDateNode = atts.getNamedItem("last-modification-date");
        return (lastModificationDateNode.getNodeValue());

    }

    /**
     * Gets the task param containing the last modification date of the specified object.
     *
     * @param includeComment Flag indicating if the comment shall be additionally included.
     * @param id             The id of the object.
     * @return Returns the created task param xml.
     * @throws Exception Thrown if anything fails.
     */
    public String getTheLastModificationParam(final boolean includeComment, final String id) throws Exception {
        return getTheLastModificationParam(includeComment, id, null);
    }

    /**
     * @param includeComment Flag indicating if the comment shall be additionally included.
     * @param id             The id of the object.
     * @param comment        The comment for the param structure (withdraw comment).
     * @return Returns the created task param xml.
     * @throws Exception If an error occurs.
     */
    @Deprecated
    public String getTheLastModificationParam(final boolean includeComment, final String id, final String comment)
        throws Exception {

        String xml;
        if (includeComment) {
            xml = TaskParamFactory.getStatusTaskParam(new DateTime(getTheLastModificationDate(id)), comment);
        }
        else {
            xml = TaskParamFactory.getStatusTaskParam(new DateTime(getTheLastModificationDate(id)), null);
        }

        return xml;
    }

    /**
     * Asserts that the objid and href attributes of the root element exist and are consistent.
     *
     * @param document The document.
     * @throws Exception Thrown if anything fails.
     */
    public void assertHrefObjidConsistency(final Document document) throws Exception {

        assertHrefObjidConsistency(document, null);
    }

    /**
     * Asserts that a given string has the structure of an eSciDoc objid.
     *
     * @param objid The string.
     * @throws Exception Thrown if anything fails.
     */
    public void assertObjid(final String objid) throws Exception {

        Pattern PATTERN_OBJID_ATTRIBUTE = Pattern.compile("[a-zA-Z]+:[a-zA-Z0-9_-]+");
        if (!PATTERN_OBJID_ATTRIBUTE.matcher(objid).find()) {
            fail("Does not look like an objid: " + objid);
        }
    }

    /**
     * Asserts that the objid and href attributes exist and are consistent.
     *
     * @param document The document.
     * @param xPath    The xpath to the element containing the objid and href attributes. If this parameter is
     *                 <code>null</code>, the root element is used.
     * @throws Exception Thrown if anything fails.
     */
    public void assertHrefObjidConsistency(final Document document, final String xPath) throws Exception {
        final String objid;
        final String objidFromHref;

        if (xPath == null) {
            objid = getRootElementAttributeValue(document, NAME_OBJID);
            objidFromHref = getObjidFromHref(getRootElementHrefValue(document));
            assertEquals("Href and objid are inconsistent in root element", objidFromHref, objid);
        }
        else {
            Node objidNode = selectSingleNode(document, xPath + "/@objid");
            Node hrefNode = selectSingleNode(document, xPath + "/@href");
            assertNotNull("Objid not found for element [" + xPath + "]", objidNode);
            assertNotNull("Href not found for element [" + xPath + "]", hrefNode);
            objid = objidNode.getTextContent();
            objidFromHref = getObjidFromHref(hrefNode.getTextContent());
            assertEquals("Href and objid are inconsistent [" + xPath + "]", objidFromHref, objid);
        }

    }

    /**
     * Assert if the framework created the necessary elements and attributes.
     *
     * @param xmlData The xml representation of the context.
     * @throws Exception If anything fails.
     */
    public static void assertXmlCreatedContext(final String xmlData) throws Exception {

        Document document = EscidocAbstractTest.getDocument(xmlData);
        assertXmlNotNull("/context/@objid", document, "/context/@objid");
        assertXmlNotNull("/context/@last-modification-date", document, "/context/@last-modification-date");

        assertXmlNotNull("/context/properties/creator/@href", document, "/context/properties/creator/@href");
        assertXmlNotNull("/context/properties/creator/@title", document, "/context/properties/creator/@title");

        assertXmlNotNull("/context/admin-descriptor/@objid", document, "/context/admin-descriptor/@objid");
        assertXmlNotNull("/context/admin-descriptor/@href", document, "/context/admin-descriptor/@href");

        assertXmlNotNull("/context/admin-descriptor/properties/creator/@href", document,
            "/context/admin-descriptor/properties/creator/@href");
        assertXmlNotNull("/context/admin-descriptor/properties/creator/@title", document,
            "/context/admin-descriptor/properties/creator/@title");

        // TODO check this list for completeness?

    }

    /**
     * Assert that the Element/Attribute selected by the xPath does not exist.
     *
     * @param message The message printed if assertion fails.
     * @param xml     XML
     * @param xPath   The xPath.
     * @throws Exception If anything fails.
     */
    public static void assertXmlNotExists(final String message, final String xml, final String xPath) throws Exception {
        // TODO: do more than nothing
    }

    /**
     * Assert that the Element/Attribute selected by the xPath does not exist.
     *
     * @param message The message printed if assertion fails.
     * @param node    The Node.
     * @param xPath   The xPath.
     * @throws Exception If anything fails.
     */
    public static void assertXmlNotExists(final String message, final Node node, final String xPath) throws Exception {
        NodeList nodes = selectNodeList(node, xPath);
        assertTrue(message, nodes.getLength() == 0);
    }

    /**
     * Assert that the node selected by the xpath exists int the given document and is not empty.
     *
     * @param elementLabel The label for assertion messages.
     * @param document     The document.
     * @param xPath        The xPath.
     * @throws Exception If anything fails.
     */
    public static void assertXmlNotNull(final String elementLabel, final Node document, final String xPath)
        throws Exception {
        Node element = selectSingleNode(document, xPath);
        assertNotNull(elementLabel + " not found!", element);
        assertFalse(elementLabel + " must not be empty!", "".equals(element.getTextContent()));
    }

    /**
     * Extracts the id from the href attribute of the root element of the provided document.
     *
     * @param document The document to retrieve the id from.
     * @return Returns the extracted id value.
     * @throws Exception If anything fails.
     */
    public static String getIdFromRootElementHref(final Document document) throws Exception {

        return getObjidFromHref(getRootElementHrefValue(document));
    }

    /**
     * Obtain the objid from the href.
     *
     * @param val The href attribute.
     * @return objid
     */
    public String getIdFromHrefValue(final String val) {
        String result = null;
        // FIXME it's no objid pattern
        Pattern PATTERN_OBJID_ATTRIBUTE = Pattern.compile(".*\\/([^\"\\/]*)");

        Matcher m1 = PATTERN_OBJID_ATTRIBUTE.matcher(val);
        if (m1.find()) {
            result = m1.group(1);
        }
        return result;
    }

    /**
     * Obtain the objid from the XML root element.
     *
     * @param xml The XML data.
     * @return objid
     */
    public String getIdFromRootElement(final String xml) {
        String result = null;
        // FIXME PATTERN_OBJID_ATTRIBUTE is static field !
        // FIXME this pattern does not work for componentId
        Pattern patternObjidAttributeJustForThisMethod = Pattern.compile("href=\"/ir/[^/]+/([^\"]*)\"");
        Matcher m1 = patternObjidAttributeJustForThisMethod.matcher(xml);
        if (m1.find()) {
            result = m1.group(1);
        }

        return result;
    }

    /**
     * Gets the id from the provided uri (href).
     *
     * @param href The uri to extract the id from.
     * @return Returns the extracted id.
     */
    public static String getObjidFromHref(final String href) {
        return href.substring(href.lastIndexOf('/') + 1);
    }

    /**
     * Gets the href attribute of the root element from the document.
     *
     * @param document The document to retrieve the value from.
     * @return Returns the attribute value.
     * @throws Exception If anything fails.
     */
    public static String getRootElementHrefValue(final Document document) throws Exception {
        return getRootElementAttributeValueNS(document, NAME_HREF, Constants.NS_EXTERNAL_XLINK);
    }

    /**
     * Gets the objid attribute of the element selected in the provided node.<br> It tries to get the objid attribute of
     * the selected node. If this fails, it tries to get the xlink:href attribute. If both fails, an assertion exception
     * is "thrown".
     *
     * @param node  The node to select an element from.
     * @param xPath The xpath to select the element in the provided node.
     * @return Returns the attribute value.
     * @throws Exception If anything fails.
     */
    public String getObjidValue(final Node node, final String xPath) throws Exception {
        Node selected = selectSingleNode(node, xPath);
        assertNotNull("No Element selected to retrieve the object id from", selected);
        NamedNodeMap attributes = selected.getAttributes();
        assertNotNull("Selected node has no attributes (not an element?) ", attributes);
        Node objidAttr = attributes.getNamedItem(NAME_OBJID);
        if (objidAttr != null) {
            return objidAttr.getTextContent();
        }
        else {
            objidAttr = selectSingleNode(selected, "." + PART_XLINK_HREF);
            assertNotNull("Selected node neither has an objid " + "attribute nor an xlink href attribute", objidAttr);
            return getObjidFromHref(objidAttr.getTextContent());
        }
    }

    /**
     * Gets the objid attribute of the root element from the Xml.
     *
     * @param xml The xml representation of an object to retrieve the value from.
     * @return Returns the objid.
     * @throws Exception If anything fails.
     */
    public String getObjidValue(final String xml) throws Exception {

        Matcher m = PATTERN_OBJID_ATTRIBUTE.matcher(xml);
        if (m.find()) {
            return m.group(1);
        }
        else {
            fail("Missing objid in provided xml data");
            return null;
        }
    }

    /**
     * Get objId with version part of latest version of document.
     *
     * @param xml The Item XML.
     * @return The object id of the latest version.
     * @throws Exception If anything fails.
     */
    public final String getLatestVersionObjidValue(final String xml) throws Exception {

        Node latestVersionNode =
            selectSingleNode(EscidocAbstractTest.getDocument(xml), "//properties/latest-version/number");
        String id = getIdFromRootElement(xml) + VERSION_SUFFIX_SEPARATOR + latestVersionNode.getTextContent();
        return (id);
    }

    /**
     * Remove version informaion from given objid.
     *
     * @param objid The objid.
     * @return The objid without version information.
     */
    public static String getObjidWithoutVersion(final String objid) {

        String result = objid;
        Matcher m = PATTERN_ID_WITHOUT_VERSION.matcher(objid);
        if (m.find()) {
            result = m.group(1);
        }
        return result;
    }

    /**
     * Asserts that the creation-date element of the document exists.
     *
     * @param message  The fail message.
     * @param document The document to retrieve the value from.
     * @return Returns the creation date value.
     * @throws Exception If anything fails.
     */
    public static String assertCreationDateExists(final String message, final Document document) throws Exception {

        return assertCreationDateExists(message, document, null);
    }

    /**
     * Asserts that the creation-date element of the specified properties element from the document exists.
     *
     * @param message  The fail message.
     * @param document The document to retrieve the value from.
     * @param xPath    The xpath to the parent element that contains the creation date element. If this is
     *                 <code>null</code>, the first element named "properties" will be selected.
     * @return Returns the creation date value.
     * @throws Exception If anything fails.
     */
    public static String assertCreationDateExists(final String message, final Document document, final String xPath)
        throws Exception {

        final String creationDate = getCreationDateValue(document, xPath);
        assertNotNull(prepareAssertionFailedMessage(message) + "No creation-date", creationDate);
        return creationDate;
    }

    /**
     * Asserts that ts1 depicts a time after ts2 (ts1 > ts2).
     *
     * @param message The message is the assertion fails.
     * @param ts1     The first timestamp.
     * @param ts2     The second timestamp.
     * @throws Exception If anything fails (e.g. one timstamp has incorrect format)
     */
    public static void assertTimestampIsEqualOrAfter(final String message, final String ts1, final String ts2)
        throws Exception {

        assertTrue(message, compareTimestamps(ts1, ts2) >= 0);
    }

    /**
     * Asserts that ts1 and ts2 depict the same time (ts1 == ts2).
     *
     * @param message The message is the assertion fails.
     * @param ts1     The first timestamp.
     * @param ts2     The second timestamp.
     * @throws Exception If anything fails (e.g. one timstamp has incorrect format)
     */
    public static void assertTimestampEquals(final String message, final String ts1, final String ts2) throws Exception {

        assertTrue(message, compareTimestamps(ts1, ts2) == 0);
    }

    /**
     * Returns a positive integer if ts1 depicts a time after ts2 (ts1 > ts2), 0 if ts1 and ts2 depict the same time
     * (ts1 == ts2), and a negative integer if if ts1 depicts a time before ts2 (ts1 < ts2).
     *
     * @param ts1 The first timestamp.
     * @param ts2 The second timestamp.
     * @return The comparison result.
     * @throws Exception If anything fails (e.g. one timstamp has incorrect format).
     */
    public static int compareTimestamps(final String ts1, final String ts2) throws Exception {

        int result = 0;

        XMLGregorianCalendar date1 = DatatypeFactory.newInstance().newXMLGregorianCalendar(ts1);
        XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(ts2);

        int diff = date1.compare(date2);
        if (diff == DatatypeConstants.LESSER) {
            result = -1;
        }
        else if (diff == DatatypeConstants.GREATER) {
            result = 1;
        }
        else if (diff == DatatypeConstants.EQUAL) {
            result = 0;
        }
        else if (diff == DatatypeConstants.INDETERMINATE) {
            throw new Exception("Date comparing: INDETERMINATE");
        }

        return result;
    }

    /**
     * Get the current time as timestamp. The date format is yyyy-MM-dd'T'HH:mm:ss.SSSZ.
     *
     * @return The current time as timestamp.
     */
    public static String getNowAsTimestamp() {

        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(new Date()).replaceAll("\\+0000", "Z").replaceAll("([+-][0-9]{2}):([0-9]{2})", "$1$2");
    }

    /**
     * @param timestamp The timestamp.
     * @return The normalized timestamp.
     */
    public static String normalizeTimestamp(final String timestamp) {

        return timestamp.replaceAll("Z", "+0000").replaceAll("([+-][0-9]{2}):([0-9]{2})", "$1$2");
    }

    /**
     * Return a unique name. It is the concatenation of the prefix and the current time in milli seconds.
     *
     * @param prefix The prefix.
     * @return The unique name.
     */
    public static String getUniqueName(final String prefix) {

        return prefix + System.currentTimeMillis();
    }

    /**
     * Makes the value of the provided node unique by adding a timestamp to it.
     *
     * @param node The node to find the "name" element in and make it unique.
     * @return Returns the unique value.
     */
    public String setUniqueName(final Node node) {

        final String uniqueName = getUniqueName(node.getTextContent().trim());
        node.setTextContent(uniqueName);
        return uniqueName;
    }

    /**
     * Makes the value of the selected node of the provided node unique by adding a timestamp to it.<br> If no node can
     * be selected, an assertion fails.
     *
     * @param node  The node to find the "name" element in and make it unique.
     * @param xpath The xpath selecting the "name" element to change.
     * @return Returns the unique value.
     * @throws Exception If anything fails.
     */
    public String setUniqueValue(final Node node, final String xpath) throws Exception {

        assertNotNull("No node provided to select a node in.", node);
        Node selected = selectSingleNodeAsserted(node, xpath);
        return setUniqueName(selected);
    }

    /**
     * Changes the given template. If currentElement (e.g. /context/properties/creation-date) is not a key in the
     * elements Map, currentElement is interpreted as a xpath and the selected node is removed from the template. If the
     * value for key currentElement is different from the empty String, the selected node's value is substituted with
     * this value.
     *
     * @param template       The context template.
     * @param elements       The elements Map.
     * @param currentElement The currentElemnt.
     * @return The resulting template.
     * @throws Exception If anything fails.
     */
    public static Node changeTemplateWithReadOnly(
        final Node template, final Map<String, String> elements, final String currentElement) throws Exception {
        Node result = template;
        if (elements.get(currentElement) == null) {
            deleteNodes(template, currentElement);
            //
            // if (currentElement.indexOf("@") != -1) {
            // String xpath = currentElement.substring(0, currentElement
            // .indexOf("@") - 1);
            // String attribute = currentElement.substring(currentElement
            // .indexOf("@") + 1);
            // deleteAttribute(result, xpath, attribute);
            // }
            // else {
            // result = deleteElement(result, currentElement);
            // }
        }
        else if (!"".equals(elements.get(currentElement))) {
            String element = currentElement;
            if (element.contains("@" + XLINK_HREF_TEMPLATES)) {
                element = element.replaceAll("@" + XLINK_HREF_TEMPLATES, "@href");
            }
            result = substitute(result, element, elements.get(currentElement));
        }
        return result;
    }

    /**
     * Inserts the namespaces into the provided element node.
     *
     * @param element The element into that the namespace definitions shall be inserted.
     * @return Returns the changed element node.
     * @throws Exception If anything fails.
     */
    public Element insertNamespaces(final Element element) throws Exception {

        element.setAttribute("xmlns:prefix-container", Constants.NS_IR_CONTAINER);
        //element.setAttribute("xmlns:prefix-content-type", CONTENT_TYPE_NS_URI); TODO: does no longer exist?
        element.setAttribute("xmlns:prefix-context", Constants.NS_IR_CONTEXT);
        element.setAttribute("xmlns:prefix-dc", Constants.NS_EXTERNAL_DC);
        element.setAttribute("xmlns:prefix-dcterms", Constants.NS_EXTERNAL_DC_TERMS);
        element.setAttribute("xmlns:prefix-grants", Constants.NS_AA_GRANTS);
        //element.setAttribute("xmlns:prefix-internal-metadata", INTERNAL_METADATA_NS_URI); TODO: does no longer exist?
        element.setAttribute("xmlns:prefix-item", Constants.NS_IR_ITEM);
        //element.setAttribute("xmlns:prefix-member-list", MEMBER_LIST_NS_URI); TODO: does no longer exist?
        //element.setAttribute("xmlns:prefix-member-ref-list", MEMBER_REF_LIST_NS_URI); TODO: does no longer exist?
        //element.setAttribute("xmlns:prefix-metadata", METADATA_NS_URI); TODO: does no longer exist?
        //element.setAttribute("xmlns:prefix-metadatarecords", METADATARECORDS_NS_URI); TODO: does no longer exist?
        //element.setAttribute("xmlns:escidocMetadataRecords", METADATARECORDS_NS_URI); TODO: does no longer exist?
        element.setAttribute("xmlns:escidocComponents", Constants.NS_IR_COMPONENTS);
        element.setAttribute("xmlns:prefix-organizational-unit", Constants.NS_OUM_OU);
        //element.setAttribute("xmlns:prefix-properties", PROPERTIES_NS_URI); TODO: does no longer exist?
        //element.setAttribute("xmlns:prefix-schema", SCHEMA_NS_URI); TODO: huh???
        element.setAttribute("xmlns:prefix-staging-file", Constants.NS_ST_FILE);
        element.setAttribute("xmlns:prefix-user-account", Constants.NS_AA_USER_ACCOUNT);
        element.setAttribute("xmlns:prefix-xacml-context", Constants.NS_EXTERNAL_XACML_CONTEXT);
        element.setAttribute("xmlns:prefix-xacml-policy", Constants.NS_EXTERNAL_XACML_POLICY);
        element.setAttribute("xmlns:prefix-xlink", Constants.NS_EXTERNAL_XLINK);
        element.setAttribute("xmlns:prefix-xsi", Constants.NS_EXTERNAL_XSI);
        return element;
    }

    /**
     * Inserts the namespaces into the root element of the provided document.
     *
     * @param doc The document that shall be changed.
     * @return Returns the changed document.
     * @throws Exception If anything fails.
     */
    public Document insertNamespacesInRootElement(final Document doc) throws Exception {

        final Element rootElement = getRootElement(doc);
        assertNotNull("No root element found in the provided document", rootElement);
        insertNamespaces(rootElement);
        return doc;
    }

    /**
     * Inserts the namespaces into the root element of the provided xml data.
     *
     * @param xmlData The xmlData that shall be changed.
     * @return Returns the changed XML data.
     * @throws Exception If anything fails.
     */
    public String insertNamespacesInRootElement(final String xmlData) throws Exception {

        return toString(insertNamespacesInRootElement(EscidocAbstractTest.getDocument(xmlData)), false);
    }

    /**
     * Deletes the node selected by the given XPath from the provided node.
     *
     * @param node  The Node to delete the selected nodes from.
     * @param xPath The XPath selecting the sub nodes in the provided node.
     * @return returns the provided <code>Node</code> object. This <code>Node</code> object may be changed.
     * @throws Exception Thrown if anything fails.
     */
    public static Node deleteNodes(final Node node, final String xPath) throws Exception {

        NodeList nodes = selectNodeList(node, xPath);
        if (nodes == null || nodes.getLength() == 0) {
            return node;
        }

        for (int i = 0; i < nodes.getLength(); ++i) {
            Node delete = nodes.item(i);
            if (delete.getNodeType() == Node.ATTRIBUTE_NODE) {
                final int index = xPath.lastIndexOf('/');
                String attribute = delete.getNodeName();
                attribute = attribute.substring(attribute.lastIndexOf(':') + 1);
                String elementXpath = xPath.substring(0, index);
                elementXpath += "[@" + attribute + "=\"" + delete.getTextContent().trim() + "\"]";
                Node parent = selectSingleNode(node, elementXpath);
                if (parent.hasAttributes()) {
                    parent.getAttributes().removeNamedItem(delete.getNodeName());
                }
            }
            else {
                delete.getParentNode().removeChild(delete);
            }
        }

        return node;
    }

    /**
     * Asserts that the value of the selected node of the provided node starts with the expected base.
     *
     * @param node         The node in that the node shall be selected abd asserted.
     * @param xPath        The Xpath to select the node that shall be asserted.
     * @param expectedBase The expected value with that the selected node's value shall start.
     * @return Returns the value of the node that has been successfully checked.
     * @throws Exception Thrown if anything fails.
     */
    public static String assertHrefBase(final Node node, final String xPath, final String expectedBase)
        throws Exception {

        final String value = selectSingleNode(node, xPath).getTextContent();
        assertTrue("href does not start with " + expectedBase, value.startsWith(expectedBase));

        return value;
    }

    /**
     * Asserts that the selected xlink:type attribute of the provided node has the value "simple".
     *
     * @param document The node from that the xlink:type attribute shall be selected and asserted.
     * @throws Exception If an error ocurres.
     */
    public static void assertXlinkType(final Document document, final String xPath) throws Exception {

        assertXmlExists("No xlink:type attribute found [" + xPath + "]", document,
            XPATH_USER_ACCOUNT_CURRENT_GRANTS_XLINK_TYPE);
        assertXmlEquals("Unexpected xlink:type [" + xPath + "]", document, xPath, "simple");
    }

    /**
     * Modifies the namespace prefixes of the provided xml data by adding a prefix to the namespac.
     *
     * @param xml The xml data to change the namespace prefixes in.
     * @return Returns the modified xml data.
     */
    public static String modifyNamespacePrefixes(final String xml) {

        Matcher matcher = PATTERN_MODIFY_NAMESPACE_PREFIXES_REPLACE_PREFIXES.matcher(xml);
        String ret = matcher.replaceAll("$1prefix-$2");
        matcher = PATTERN_MODIFY_NAMESPACE_PREFIXES_FIX_NAMESPACE_DECLARATIONS.matcher(ret);
        ret = matcher.replaceAll("xmlns:prefix-$1");
        matcher = PATTERN_MODIFY_NAMESPACE_PREFIXES_FIX_PREFIX_XML.matcher(ret);
        ret = matcher.replaceAll("xml");
        return ret;
    }

    public void assertRdfList(
        final Document xmlDoc, final String objectTypeUri, final String orderByPropertyUri, final boolean descending,
        final int limit, final int offset) throws Exception {
        selectSingleNodeAsserted(xmlDoc, "/RDF");
        selectSingleNodeAsserted(xmlDoc, "/RDF/Description");

        NodeList descriptions = selectNodeList(xmlDoc, "/RDF/Description");

        if (limit != 0) {
            assertOrderNotAfter(descriptions.getLength(), limit);
        }

        for (int i = 0; i < descriptions.getLength(); i++) {
            NodeList nl = selectNodeList(descriptions.item(i), "type");
            boolean foundTypeObjectTypeUri = false;
            for (int j = 0; j < nl.getLength(); j++) {
                Node n = nl.item(j);
                NamedNodeMap nnm = n.getAttributes();
                Node att = nnm.getNamedItem("rdf:resource");
                String uri = att.getNodeValue();
                if (uri.equals(objectTypeUri)) {
                    foundTypeObjectTypeUri = true;
                }
            }
            if (!foundTypeObjectTypeUri) {
                String about = selectSingleNode(descriptions.item(i), "@about").getNodeValue();
                fail("Could not find type element refering " + objectTypeUri + " in RDF description of " + about + ".");
            }
        }

        if (orderByPropertyUri != null) {
            String localName = orderByPropertyUri.substring(orderByPropertyUri.lastIndexOf('/') + 1);
            Node orderNodeA = null;
            Node orderNodeB = null;
            // init order node A
            NodeList nl = selectNodeList(descriptions.item(0), localName);
            for (int j = 0; j < nl.getLength(); j++) {
                // FIXME compare with namespace
                orderNodeA = nl.item(j);
            }
            for (int i = 1; i < descriptions.getLength(); i++) {
                nl = selectNodeList(descriptions.item(i), localName);
                for (int j = 0; j < nl.getLength(); j++) {
                    // FIXME compare with namespace
                    // String curNsUri = nl.item(j).getNamespaceURI();
                    // if (nsUri.equals(curNsUri)) {
                    orderNodeB = nl.item(j);
                    // }
                }
                if (descending) {
                    assertNotNull(orderNodeA);
                    assertNotNull(orderNodeB);
                    assertOrderNotAfter(orderNodeB.getTextContent(), orderNodeA.getTextContent());
                }
                else {
                    assertNotNull(orderNodeA);
                    assertNotNull(orderNodeB);
                    assertOrderNotAfter(orderNodeA.getTextContent(), orderNodeB.getTextContent());
                }
                orderNodeA = orderNodeB;
            }
        }
        else {
            String orderValueXPath = "@about";
            // "/Description/"
            // + orderByPropertyUri.substring(orderByPropertyUri
            // .lastIndexOf('/') + 1);
            for (int i = 1; i < descriptions.getLength(); i++) {
                int a, b;
                if (descending) {
                    a = i;
                    b = i - 1;
                }
                else {
                    a = i - 1;
                    b = i;
                }
                String lower = selectSingleNodeAsserted(descriptions.item(a), orderValueXPath).getNodeValue();
                String higher = selectSingleNodeAsserted(descriptions.item(b), orderValueXPath).getNodeValue();
                assertOrderNotAfter(lower, higher);
            }
        }

    }

    public void assertContentStreamsOf_escidoc_item_198_for_create_3content_streams(
        final String itemId, final Document itemDoc, final boolean isRootContentStreams) throws Exception {

        // there should be content streams
        selectSingleNodeAsserted(itemDoc, "//content-streams[1]");
        // but only once
        assertNull(selectSingleNode(itemDoc, "//content-streams[2]"));
        if (isRootContentStreams) {
            // there should be attributes xml:base, xlink:href and
            // last-modification-date in content streams container
            selectSingleNodeAsserted(itemDoc, "/content-streams[@base]");
            // = '" + SearchTestConstants.HTTP_PROTOCOL + "://" + SearchTestConstants.HOST_PORT + "']");
            selectSingleNodeAsserted(itemDoc, "/content-streams[@href = '/ir/item/" + itemId + "/content-streams']");
            selectSingleNodeAsserted(itemDoc, "/content-streams[@last-modification-date]");
            // TODO check if latter is date
        }
        // there should be three content streams
        selectSingleNodeAsserted(itemDoc, "//content-streams/content-stream[3]");
        // not more
        assertNull(selectSingleNode(itemDoc, "//content-streams/content-stream[4]"));
        // one of each storage type
        selectSingleNodeAsserted(itemDoc, "//content-streams/content-stream[@storage='external-managed']");
        selectSingleNodeAsserted(itemDoc, "//content-streams/content-stream[@storage='internal-managed']");
        selectSingleNodeAsserted(itemDoc, "//content-streams/content-stream[@storage='external-url']");
        // check content URLs
        selectSingleNodeAsserted(itemDoc, "//content-streams/content-stream[@storage='external-managed'"
            + " and starts-with(@href,'/ir/item/')]");
        selectSingleNodeAsserted(itemDoc, "//content-streams/content-stream[@storage='internal-managed'"
            + " and starts-with(@href,'/ir/item/')]");
        selectSingleNodeAsserted(itemDoc, "//content-streams/content-stream[@storage='external-url'"
            + " and starts-with(@href,'http://')]");

        selectSingleNodeAsserted(itemDoc, "//content-streams/content-stream[@storage='external-managed'"
            + " and @name='external_image' and @mime-type='image/jpeg'" + " and @href = '/ir/item/" + itemId
            + "/content-streams/content-stream/external_image/content']");
        selectSingleNodeAsserted(itemDoc, "//content-streams/content-stream[@storage='internal-managed'"
            + " and @name='internal_xml' and @mime-type='text/xml'" + " and @href = '/ir/item/" + itemId
            + "/content-streams/content-stream/internal_xml/content']");
        selectSingleNodeAsserted(itemDoc, "//content-streams/content-stream[@storage='external-url'"
            + " and @name='redirect_image' and @mime-type='image/jpeg']");

    }

    /**
     * Uploading file to Staging Service and get URL back.
     *
     * @param file The file which is to upload to the staging service.
     * @return The URL at the staging service.
     * @throws Exception Thrown if uploading failed.
     */
    public URL uploadFileToStagingServlet(final File file) throws Exception {

        String mimeType = file.toURI().toURL().openConnection().getContentType();
        return uploadFileToStagingServlet(file, mimeType);
    }

    /**
     * Uploading file to Staging Service and get URL back.
     *
     * @param file     The file (which is to upload).
     * @param mimeType The mime type of the content.
     * @return The URL at the staging service.
     * @throws Exception Thrown if uploading failed.
     */
    public URL uploadFileToStagingServlet(final File file, final String mimeType) throws Exception {

        InputStream fileInputStream = new FileInputStream(file);

        return uploadFileToStagingServlet(fileInputStream, file.getName(), mimeType);
    }

    /**
     * Uploading file to Staging Service and get URL back.
     *
     * @param fileInputStream The filenInputStream (whic upload).
     * @param filename        The name of the file.
     * @param mimeType        The mime type of the content.
     * @return The URL fro the staging service.
     * @throws Exception Thrown if uploading failed.
     */
    public URL uploadFileToStagingServlet(
        final InputStream fileInputStream, final String filename, final String mimeType) throws Exception {

        Object result = getStagingFileClient().create(fileInputStream, mimeType, filename);
        if (result instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result;
            final String stagingFileXml = EntityUtil.toString(httpRes.getEntity(), HTTP.UTF_8);
            EntityUtil.consumeContent(httpRes.getEntity());
            Document document = EscidocAbstractTest.getDocument(stagingFileXml);
            Node fileHref = selectSingleNode(document, "/staging-file/@href");

            return new URL(getBaseUrl() + Constants.WEB_CONTEXT_URI_ESCIDOC + fileHref.getTextContent());
        }
        else {
            fail("Unsupported result type [" + result.getClass().getName() + "]");
            throw new Exception("Upload to staging service failed.");
        }
    }

    /**
     * @return Returns the itemClient.
     */
    public StagingFileClient getStagingFileClient() {
        return this.stagingFileClient;
    }

    /**
     * count the number of testMethods in the testClass.
     *
     * @return number of testMethods.
     */
    public int getTestMethodCount() {
        Method[] methods = this.getClass().getMethods();
        int count = 0;
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().startsWith("test") && methods[i].getParameterTypes().length == 0
                && methods[i].getReturnType().getName().equals("void")) {
                count++;
            }
        }
        return count;
    }

    /**
     * count the number of testMethods in the testClass.
     *
     * @return number of testMethods.
     */
    public int getTestAnnotationsCount() {
        Method[] methods = this.getClass().getMethods();
        int count = 0;
        for (Method method : methods) {
            if (method.getAnnotations() != null) {
                for (Annotation annotation : method.getAnnotations()) {
                    if (annotation.annotationType().equals(Test.class)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Obtain version number of framework by requesting it from Admin Service.
     *
     * @return version number of framework.
     * @throws Exception Thrown if request failed.
     */
    public String obtainFrameworkVersion() throws Exception {

        if (REPOSITORY_VERSION == null) {

            AdminClient admClient = new AdminClient();
            String info = handleXmlResult(admClient.getRepositoryInfo(null));

            Pattern p = Pattern.compile(".*<entry key=\"escidoc-core.build\">([^<]*)</entry>.*");
            Matcher m = p.matcher(info);
            if (m.find()) {
                REPOSITORY_VERSION = m.group(1);
            }
            else {
                throw new Exception("Cannot obtain framework version " + "from eSciDoc Core installation.");
            }
        }
        return REPOSITORY_VERSION;
    }

}
