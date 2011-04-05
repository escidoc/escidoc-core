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

import com.ctc.wstx.exc.WstxParsingException;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.XMLHashHandler;
import de.escidoc.core.common.util.stax.handler.TaskParamHandler;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.stax.StaxAttributeEscapingWriterFactory;
import de.escidoc.core.common.util.xml.stax.StaxTextEscapingWriterFactory;
import de.escidoc.core.common.util.xml.stax.events.AbstractElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.CheckRootElementStaxHandler;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import de.escidoc.core.common.util.xml.transformer.PoolableTransformerFactory;
import org.apache.commons.pool.impl.StackKeyedObjectPool;
import org.codehaus.stax2.XMLOutputFactory2;
import org.joda.time.ReadableDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.naming.directory.NoSuchAttributeException;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to support Xml stuff in eSciDoc.<br> This class provides the validation of XML data using specified
 * schemas.<br> The schemas are specified by providing their schema URIs from that <code>Schema</code> objects are
 * created. These <code>Schema</code> objects are thread-safe and are cached to prevent unnecessary recreation.
 *
 * @author Torsten Tetteroo
 */
public final class XmlUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlUtility.class);

    /**
     * Pattern used to detect Object type is in resource type format, e.g. http://escidoc.de/core/01/resources/OrganizationalUnit
     */
    private static final Pattern PATTERN_RESOURCE_OBJECT_TYPE =
        Pattern.compile('^' + Constants.RESOURCES_NS_URI + ".*$");

    /**
     * The cache storing the compiled schemas.
     */
    private static final Map<String, Schema> SCHEMA_CACHE = new HashMap<String, Schema>();

    /**
     * The UTF-8 character encoding used in eSciDoc.
     */
    public static final String CHARACTER_ENCODING = "UTF-8";

    /**
     * The XML version.
     */
    public static final String XML_VERSION = "1.0";

    /**
     * Head of document.
     */
    public static final String DOCUMENT_START =
        "<?xml version=\"" + XML_VERSION + "\" encoding=\"" + CHARACTER_ENCODING + "\"?>\n";

    /**
     * CDATA start.
     */
    public static final String CDATA_START = "<![CDATA[";

    /**
     * CDATA end.
     */
    public static final String CDATA_END = "]]>";

    public static final String AMPERSAND = "&";

    public static final String ESC_AMPERSAND = "&amp;";

    public static final String LESS_THAN = "<";

    public static final String ESC_LESS_THAN = "&lt;";

    public static final String GREATER_THAN = ">";

    public static final String ESC_GREATER_THAN = "&gt;";

    public static final String APOS = "'";

    public static final String ESC_APOS = "&apos;";

    public static final String QUOT = "\"";

    public static final String ESC_QUOT = "&quot;";

    private static final Pattern PATTERN_ESCAPE_NEEDED =
        Pattern.compile(AMPERSAND + '|' + LESS_THAN + '|' + GREATER_THAN + '|' + QUOT + '|' + APOS);

    private static final Pattern PATTERN_UNESCAPE_NEEDED =
        Pattern.compile(ESC_AMPERSAND + '|' + ESC_LESS_THAN + '|' + ESC_GREATER_THAN + '|' + ESC_QUOT + '|' + ESC_APOS);

    private static final Pattern PATTERN_AMPERSAND = Pattern.compile('(' + AMPERSAND + ')');

    private static final Pattern PATTERN_LESS_THAN = Pattern.compile('(' + LESS_THAN + ')');

    private static final Pattern PATTERN_GREATER_THAN = Pattern.compile('(' + GREATER_THAN + ')');

    private static final Pattern PATTERN_QUOT = Pattern.compile('(' + QUOT + ')');

    private static final Pattern PATTERN_APOS = Pattern.compile('(' + APOS + ')');

    private static final Pattern PATTERN_ESC_AMPERSAND = Pattern.compile('(' + ESC_AMPERSAND + ')');

    private static final Pattern PATTERN_ESC_LESS_THAN = Pattern.compile('(' + ESC_LESS_THAN + ')');

    private static final Pattern PATTERN_ESC_GREATER_THAN = Pattern.compile('(' + ESC_GREATER_THAN + ')');

    private static final Pattern PATTERN_ESC_QUOT = Pattern.compile('(' + ESC_QUOT + ')');

    private static final Pattern PATTERN_ESC_APOS = Pattern.compile('(' + ESC_APOS + ')');

    private static String containerRestSchemaLocation;

    private static String containerSoapSchemaLocation;

    private static String updateRelationsSchemaLocation;

    private static String relationsSchemaLocation;

    private static String itemRestSchemaLocation;

    private static String itemSoapSchemaLocation;

    private static String organizationalUnitRestSchemaLocation;

    private static String organizationalUnitSoapSchemaLocation;

    private static String filterSchemaLocationRest;

    private static String filterSchemaLocationSoap;

    private static String organizationalUnitListRestSchemaLocation;

    private static String organizationalUnitListSoapSchemaLocation;

    private static String organizationalUnitPathListRestSchemaLocation;

    private static String organizationalUnitPathListSoapSchemaLocation;

    private static String organizationalUnitRefListRestSchemaLocation;

    private static String organizationalUnitRefListSoapSchemaLocation;

    private static String pdpRequestsSchemaLocation;

    private static String tmeRequestsSchemaLocation;

    private static String containersFilterRestSchemaLocation;

    private static String containersFilterSoapSchemaLocation;

    private static String containerMembersFilterSoapSchemaLocation;

    private static String spoTaskParamSchemaLocation;

    private static String containerMembersFilterRestSchemaLocation;

    private static String contextRestSchemaLocation;

    private static String contextSoapSchemaLocation;

    private static String contentRelationRestSchemaLocation;

    private static String contentRelationSoapSchemaLocation;

    private static String contentModelRestSchemaLocation;

    private static String contentModelSoapSchemaLocation;

    private static String setDefinitionRestSchemaLocation;

    private static String setDefinitionSoapSchemaLocation;

    private static String contextsFilterSchemaLocationRest;

    private static String contextsFilterSchemaLocationSoap;

    private static String contextMembersFilterSchemaLocationRest;

    private static String contextMembersFilterSchemaLocationSoap;

    private static String xmlSchemaSchemaLocation;

    private static String stagingFileSchemaLocation;

    private static String statisticDataSchemaLocation;

    private static String aggregationDefinitionRestSchemaLocation;

    private static String aggregationDefinitionSoapSchemaLocation;

    private static String reportDefinitionRestSchemaLocation;

    private static String reportDefinitionSoapSchemaLocation;

    private static String reportRestSchemaLocation;

    private static String reportSoapSchemaLocation;

    private static String scopeRestSchemaLocation;

    private static String scopeSoapSchemaLocation;

    private static String reportParametersRestSchemaLocation;

    private static String reportParametersSoapSchemaLocation;

    private static String preprocessingInformationSchemaLocation;

    private static String stylesheetDefinition;

    public static final String CDATA_END_QUOTED = "]]&gt;";

    public static final String NAME_ABBREVIATION = "abbreviation";

    public static final String NAME_ACTIVE = "active";

    public static final String NAME_VALUE = "value";

    public static final String NAME_TYPE = "type";

    public static final String NAME_ASSIGNED_ON = "assigned-on";

    public static final String NAME_COMPONENT = "component";

    public static final String NAME_COMPONENTS = "components";

    public static final String NAME_CREATED_BY = "created-by";

    public static final String NAME_CREATION_DATE = "creation-date";

    public static final String NAME_SPECIFICATION = "specification";

    public static final String NAME_LOCK_OWNER = "lock-owner";

    public static final String NAME_MEMBER = "member";

    public static final String NAME_MODIFIED_BY = "modified-by";

    public static final String NAME_CONTAINER_REF = "container-ref";

    public static final String NAME_CONTENT_CATEGORY = "content-category";

    public static final String NAME_ITEM_REF = "item-ref";

    /**
     * The name of the last modification date attribute.
     */
    public static final String NAME_LAST_MODIFICATION_DATE = "last-modification-date";

    public static final String NAME_LATEST_RELEASE_NUMBER = "latest-release-number";

    public static final String NAME_LATEST_VERSION_MODIFIED_BY = "latest-version-modified-by";

    public static final String NAME_LATEST_VERSION_NUMBER = "latest-version-number";

    public static final String NAME_LATEST_VERSION_STATUS = "latest-version-status";

    public static final String NAME_LATEST_VERSION_VALID_STATUS = "latest-version-valid-status";

    public static final String NAME_PUBLIC_STATUS = "public-status";

    public static final String NAME_VALID_STATUS = "valid-status";

    public static final String NAME_STATUS = "status";

    public static final String NAME_VISIBILITY = "visibility";

    public static final String NAME_ITEM = "item";

    public static final String NAME_CONTEXT = "context";

    public static final String NAME_CONTAINER = "container";

    public static final String NAME_CONTENT_MODEL = "content-model";

    public static final String NAME_CONTENT_RELATION = "content-relation";

    public static final String NAME_USER_ACCOUNT = "user-account";

    public static final String NAME_USER_GROUP = "user-group";

    public static final String NAME_SET_DEFINITION = "set-definition";

    public static final String NAME_ROLE = "role";

    public static final String NAME_USER_ID = "userId";

    public static final String NAME_GROUP_ID = "groupId";

    public static final String NAME_ROLE_ID = "roleId";

    public static final String NAME_OBJECT_ID = "objectId";

    public static final String NAME_REVOCATION_DATE_FROM = "revocationDateFrom";

    public static final String NAME_REVOCATION_DATE_TO = "revocationDateTo";

    public static final String NAME_GRANTED_DATE_FROM = "grantedDateFrom";

    public static final String NAME_GRANTED_DATE_TO = "grantedDateTo";

    public static final String NAME_CREATOR_ID = "creatorId";

    public static final String NAME_REVOKER_ID = "revokerId";

    // Names of Statistic-Manager Resources
    public static final String NAME_STATISTIC_DATA = "statistic-data";

    public static final String NAME_SCOPE = "scope";

    public static final String NAME_AGGREGATION_DEFINITION = "aggregation-definition";

    public static final String NAME_REPORT = "report";

    public static final String NAME_REPORT_DEFINITION = "report-definition";

    public static final String NAME_PREPROCESSING_INFORMATION = "preprocessing-information";

    // /////////////////////////////////////////

    public static final String NAME_OBJECT = "object";

    public static final String NAME_ATTRIBUTES = "attributes";

    public static final String NAME_ATTRIBUTE = "attribute";

    public static final String NAME_OBJID = "objid";

    public static final String NAME_NAME = "name";

    public static final String NAME_PARAM = "param";

    public static final String NAME_PID = "pid";

    public static final String NAME_EXTERNAL_ID = "external-id";

    public static final String NAME_PROPERTIES = "properties";

    public static final String NAME_MDRECORDS = "md-records";

    public static final String NAME_MDRECORD = "md-record";

    public static final String NAME_RESOURCES = "resources";

    public static final String NAME_ORGANIZATION_DETAILS = "organization-details";

    public static final String NAME_ID = "id";

    public static final String NAME_EMAIL = "email";

    public static final String NAME_FILTER = "filter";

    public static final String NAME_FORMAT = "format";

    public static final String NAME_LOGIN_NAME = "login-name";

    public static final String NAME_HANDLE = "handle";

    public static final String NAME_HREF = "href";

    public static final String NAME_TASK_INSTANCE_ID = "task-instance-id";

    public static final String NAME_DESCRIPTION = "description";

    public static final String NAME_GENRE = "genre";

    public static final String NAME_GRANT = "grant";

    public static final String NAME_ADMIN_DESCRIPTOR = "admin-descriptor";

    public static final String NAME_ORGANIZATIONAL_UNIT = "organizational-unit";

    public static final String NAME_METADATA_SCHEMA = "md-schema";

    public static final String NAME_PARENT_OBJECTS = "parent-objects";

    public static final String NAME_PARENTS = "parents";

    public static final String NAME_PARENT = "parent";

    public static final String NAME_CHILD_OBJECTS = "child-objects";

    public static final String NAME_PATH_LIST = "path-list";

    public static final String NAME_PREDECESSORS = "predecessors";

    public static final String NAME_PREDECESSOR = "predecessor";

    public static final String NAME_SUCCESSORS = "successors";

    public static final String NAME_ORDER_BY = "order-by";

    public static final String NAME_SORTING = "sorting";

    public static final String NAME_LIMIT = "limit";

    public static final String NAME_OFFSET = "offset";

    public static final String NAME_UNSECURED_ACTION = "unsecured-action";

    public static final String NAME_UNSECURED_ACTIONS = "unsecured-actions";

    public static final String XPATH_ITEM_COMPONENT_STATUS = "/item/component/properties/status";

    public static final String XPATH_USER_ACCOUNT_ORGANIZATIONAL_UNIT =
        "/user-account/properties/organizational-units/organizational-unit";

    public static final String BASE_AA = "/aa/";

    public static final String BASE_SM = "/statistic/";

    public static final String BASE_OUM = "/oum/";

    public static final String BASE_OM = "/ir/";

    public static final String BASE_ORGANIZATIONAL_UNIT = BASE_OUM + NAME_ORGANIZATIONAL_UNIT + '/';

    public static final String BASE_USER_ACCOUNT = BASE_AA + NAME_USER_ACCOUNT + '/';

    public static final String BASE_USER_GROUP = BASE_AA + NAME_USER_GROUP + '/';

    public static final String BASE_SET_DEFINITION = "/oai/" + NAME_SET_DEFINITION + '/';

    public static final String BASE_ROLE = BASE_AA + NAME_ROLE + '/';

    public static final String BASE_LOGIN = BASE_AA + "login" + '/';

    public static final String BASE_SCOPE = BASE_SM + NAME_SCOPE + '/';

    public static final String BASE_AGGREGATION_DEFINITION = BASE_SM + NAME_AGGREGATION_DEFINITION + '/';

    public static final String BASE_REPORT_DEFINITION = BASE_SM + NAME_REPORT_DEFINITION + '/';

    private static final Map<String, String> REST_SCHEMA_LOCATIONS = new HashMap<String, String>();

    private static final Map<String, String> SOAP_SCHEMA_LOCATIONS = new HashMap<String, String>();

    public static final String XPATH_USER_ACCOUNT_PROPERTIES = '/' + NAME_USER_ACCOUNT + '/' + NAME_PROPERTIES;

    /**
     * The thread-safe compiled Pattern used to extract the object id from an provided URI/Fedora id, e.g. from
     * &lt;info:fedora/escidoc:1&gt; or from http://www.escidoc.de/some/path/escidoc:1
     */
    private static final Pattern PATTERN_GET_ID_FROM_URI_OR_FEDORA_ID = Pattern.compile(".*/([^/>]+)>{0,1}");

    /**
     * The thread-safe compiled pattern to extract an object id from an XML representation of a resource, either by
     * getting it from the attribute objid or extracting it from the attribute ...:href.
     */
    private static final Pattern PATTERN_OBJID_FROM_XML = Pattern.compile("(.*?objid=\"|.*?:href=\".*/)(.*?)\".*");

    /**
     * The thread-safe compiled pattern to extract a title from an XML representation of a resource.
     */
    private static final Pattern PATTERN_NAME_FROM_XML =
        Pattern
            .compile(".*?<.*?:" + NAME_NAME + ">(.*?)</.*?:" + NAME_NAME + ">.*", Pattern.DOTALL | Pattern.MULTILINE);

    private static final Pattern PATTERN_ID_WITHOUT_VERSION = Pattern.compile("([a-zA-Z]+:[a-zA-Z0-9]+):[0-9]+");

    private static final Pattern PATTERN_VERSION_NUMBER = Pattern.compile("[a-zA-Z]+:[a-zA-Z0-9]+:([0-9]+)");

    public static final String ERR_MSG_MISSING_ATTRIBUTE = "Missing attribute";

    private static final StackKeyedObjectPool TRANSFORMER_POOL =
        new StackKeyedObjectPool(new PoolableTransformerFactory());

    /**
     * Private constructor to prevent initialization.
     */
    private XmlUtility() {
    }

    /**
     * Simple proxy method that can decide about the resource type and return the matching schema location.
     *
     * @param type The type of the resource.
     * @return Returns the location of the appropriate schema
     * @throws WebserverSystemException Thrown if retrieve of SchemaLocation failed.
     */
    public static String getSchemaLocationForResource(final ResourceType type) throws WebserverSystemException {

        final String schemaLocation;
        switch (type) {
            case ITEM:
                schemaLocation = getItemSchemaLocation();
                break;
            case CONTAINER:
                schemaLocation = getContainerSchemaLocation();
                break;
            case CONTEXT:
                schemaLocation = getContextSchemaLocation();
                break;
            case OU:
                schemaLocation = getOrganizationalUnitSchemaLocation();
                break;
            case CONTENT_RELATION:
                schemaLocation = getContentRelationSchemaLocation();
                break;
            case CONTENT_MODEL:
                schemaLocation = getContentModelSchemaLocation();
                break;
            default:
                throw new WebserverSystemException("Unknown schema location for resoure type " + type);
        }

        return schemaLocation;
    }

    /**
     * Gets the organizational unit href for the provided organizational unit id.
     *
     * @param organizationalUnitId The id of the organizational unit.
     * @return Returns the href for the provided organizational unit id.
     */
    public static String getOrganizationalUnitHref(final String organizationalUnitId) {

        return BASE_ORGANIZATIONAL_UNIT + organizationalUnitId;
    }

    /**
     * Gets the container md records href for the provided organizational unit id.
     *
     * @param organizationalUnitId The id of organizational unit.
     * @return Returns the href for the md records of the provided organizational unit id.
     */
    public static String getOrganizationalUnitMdRecordsHref(final String organizationalUnitId) {

        return getOrganizationalUnitHref(organizationalUnitId) + '/' + NAME_MDRECORDS;
    }

    /**
     * Gets the container properties href for the provided organizational unit id and name.
     *
     * @param organizationalUnitId The id of organizational unit.
     * @param name                 The name of the md record.
     * @return Returns the href for the md record of the provided organizational unit id and name.
     */
    public static String getOrganizationalUnitMdRecordHref(final String organizationalUnitId, final String name) {

        return getOrganizationalUnitMdRecordsHref(organizationalUnitId) + '/' + NAME_MDRECORD + '/' + name;
    }

    /**
     * Gets the container href for the provided container id.
     * <p/>
     * Use the getHref() methods of the resource objects itself (Container).
     *
     * @param containerId The id of the container.
     * @return Returns the href for the provided container id.
     */
    @Deprecated
    public static String getContainerHref(final String containerId) {

        return Constants.CONTAINER_URL_BASE + containerId;
    }

    /**
     * Gets the container properties href for the provided container id.
     *
     * @param containerHref The href of container.
     * @return Returns the href for the properties of the provided container id.
     */
    public static String getContainerMdRecordsHref(final String containerHref) {

        return containerHref + '/' + NAME_MDRECORDS;
    }

    /**
     * Gets the container properties href for the provided container id.
     *
     * @param containerHref The href of the container.
     * @return Returns the href for the properties of the provided container id.
     */
    public static String getContainerPropertiesHref(final String containerHref) {

        return containerHref + '/' + NAME_PROPERTIES;
    }

    /**
     * Gets the item href for the provided item id.
     *
     * @param itemId The id of the container.
     * @return Returns the href for the provided container id.
     */
    public static String getItemHref(final String itemId) {

        return Constants.ITEM_URL_BASE + itemId;
    }

    /**
     * Gets the item parents href for the provided item href.
     *
     * @param itemHref The href of the item.
     * @return Returns the href for the data of the provided item id.
     */
    public static String getItemParentsHref(final String itemHref) {

        return itemHref + '/' + NAME_RESOURCES + "/parents";
    }

    /**
     * Gets the component href for the provided component id.
     *
     * @param componentId The id of the component.
     * @return Returns the href for the provided component id.
     */
    public static String getComponentHref(final String componentId) {
        final String itemId;
        try {
            itemId = TripleStoreUtility.getInstance().getItemForComponent(componentId);
        }
        catch (final TripleStoreSystemException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on accessing triple store.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on accessing triple store.", e);
            }
            return null;
        }
        catch (final WebserverSystemException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on accessing triple store.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on accessing triple store.", e);
            }
            return null;
        }
        return Constants.ITEM_URL_BASE + itemId + Constants.COMPONENT_URL_PART + componentId;
    }

    /**
     * Gets the content-relation href for the provided content-relation id.
     *
     * @param contentRelationId The id of the content-relation.
     * @return Returns the href for the provided content-relation id.
     */
    public static String getContentRelationHref(final String contentRelationId) {
        return Constants.CONTENT_RELATION_URL_BASE + contentRelationId;
    }

    /**
     * Gets the context href for the provided container id.
     *
     * @param contextId The id of the context.
     * @return Returns the href for the provided context id.
     */
    public static String getContextHref(final String contextId) {

        return Constants.CONTEXT_URL_BASE + contextId;
    }

    /**
     * Get the properties href for the provided context.
     *
     * @param contextId The id of the context.
     * @return Returns the href of the properties for the provided context id.
     */
    public static String getContextPropertiesHref(final String contextId) {

        return getContextHref(contextId) + '/' + NAME_PROPERTIES;
    }

    /**
     * Gets the context resources href for the provided context id.
     *
     * @param contextId The id of the context.
     * @return Returns the href for the data of the provided context id.
     */
    public static String getContextResourcesHref(final String contextId) {

        return getContextHref(contextId) + '/' + NAME_RESOURCES;
    }

    /**
     * Gets the content model href for the provided content model id.
     *
     * @param contentModelId The id of the content model.
     * @return Returns the href for the provided content model id.
     */
    public static String getContentModelHref(final String contentModelId) {

        return Constants.CONTENT_MODEL_URL_BASE + contentModelId;
    }

    /**
     * Gets the organizational unit properties href for the provided organizational unit id.
     *
     * @param organizationalUnitId The id of the organizational unit.
     * @return Returns the href for the properties of the provided organizational unit id.
     */
    public static String getOrganizationalUnitPropertiesHref(final String organizationalUnitId) {

        return getOrganizationalUnitHref(organizationalUnitId) + '/' + NAME_PROPERTIES;
    }

    /**
     * Gets the organizational unit parent-ous href for the provided organizational unit id.
     *
     * @param organizationalUnitId The id of the organizational unit.
     * @return Returns the href for the parent-ous of the provided organizational unit id.
     */
    public static String getOrganizationalUnitParentsHref(final String organizationalUnitId) {

        return getOrganizationalUnitHref(organizationalUnitId) + '/' + NAME_PARENTS;
    }

    /**
     * Get href of organizational unit predecessor OUs for the provided organizational unit id.
     *
     * @param organizationalUnitId The id of the organizational unit.
     * @return Returns the href for the predecessor OUs of the provided organizational unit id.
     */
    public static String getOrganizationalUnitPredecessorsHref(final String organizationalUnitId) {

        return getOrganizationalUnitHref(organizationalUnitId) + '/' + NAME_PREDECESSORS;
    }

    /**
     * Get href of organizational unit successors for the provided organizational unit id.
     *
     * @param organizationalUnitId The id of the organizational unit.
     * @return Returns the href for the successor OUs of the provided organizational unit id.
     */
    public static String getOrganizationalUnitSuccessorsHref(final String organizationalUnitId) {

        return getOrganizationalUnitHref(organizationalUnitId) + '/' + NAME_SUCCESSORS;
    }

    /**
     * Gets the container resources href for the provided container id.
     *
     * @param containerHref The href of the container.
     * @return Returns the href for the data of the provided container id.
     */
    public static String getContainerResourcesHref(final String containerHref) {

        return containerHref + '/' + NAME_RESOURCES;
    }

    /**
     * Gets the container parents href for the provided container id.
     *
     * @param containerHref The href of the container.
     * @return Returns the href for the data of the provided container id.
     */
    public static String getContainerParentsHref(final String containerHref) {

        return containerHref + '/' + NAME_RESOURCES + "/parents";
    }

    /**
     * Gets the organizational unit resources href for the provided organizational unit id.
     *
     * @param organizationalUnitId The id of the organizational unit.
     * @return Returns the href for the data of the provided organizational unit id.
     */
    public static String getOrganizationalUnitResourcesHref(final String organizationalUnitId) {

        return getOrganizationalUnitHref(organizationalUnitId) + '/' + NAME_RESOURCES;
    }

    /**
     * Gets the organizational unit virtual resource parents href for the provided organizational unit id.
     *
     * @param organizationalUnitId The id of the organizational unit.
     * @return Returns the href for the data of the provided organizational unit id.
     */
    public static String getOrganizationalUnitResourcesParentObjectsHref(final String organizationalUnitId) {

        return getOrganizationalUnitResourcesHref(organizationalUnitId) + '/' + NAME_PARENT_OBJECTS;
    }

    /**
     * Gets the organizational unit virtual resource children href for the provided organizational unit id.
     *
     * @param organizationalUnitId The id of the organizational unit.
     * @return Returns the href for the data of the provided organizational unit id.
     */
    public static String getOrganizationalUnitResourcesChildObjectsHref(final String organizationalUnitId) {

        return getOrganizationalUnitResourcesHref(organizationalUnitId) + '/' + NAME_CHILD_OBJECTS;
    }

    /**
     * Gets the organizational unit virtual resource path-list href for the provided organizational unit id.
     *
     * @param organizationalUnitId The id of the organizational unit.
     * @return Returns the href for the data of the provided organizational unit id.
     */
    public static String getOrganizationalUnitResourcesPathListHref(final String organizationalUnitId) {

        return getOrganizationalUnitResourcesHref(organizationalUnitId) + '/' + NAME_PATH_LIST;
    }

    /**
     * Gets the organizational unit virtual resource successors href for the provided organizational unit id.
     *
     * @param organizationalUnitId The id of the organizational unit.
     * @return Returns the href for the data of the provided organizational unit id.
     */
    public static String getOrganizationalUnitResourcesSuccessorsHref(final String organizationalUnitId) {

        return getOrganizationalUnitResourcesHref(organizationalUnitId) + '/' + NAME_SUCCESSORS;
    }

    /**
     * Gets the user account href for the provided user account id.
     *
     * @param userAccountId The id of the user account.
     * @return Returns the href for the provided user account id.
     */
    public static String getUserAccountHref(final String userAccountId) {

        return BASE_USER_ACCOUNT + userAccountId;
    }

    /**
     * Gets the user group href for the provided user group id.
     *
     * @param userGroupId The id of the user group.
     * @return Returns the href for the provided user group id.
     */
    public static String getUserGroupHref(final String userGroupId) {

        return BASE_USER_GROUP + userGroupId;
    }

    /**
     * Gets the set definition href for the provided set definition id.
     *
     * @param setDefinitionId The id of the set definition.
     * @return Returns the href for the provided set definition id.
     */
    public static String getSetDefinitionHref(final String setDefinitionId) {

        return BASE_SET_DEFINITION + setDefinitionId;
    }

    /**
     * Gets the user group member href for the provided user group member id.
     *
     * @param userGroupHref     The href of the user group member.
     * @param userGroupMemberId The id of the user group member.
     * @return Returns the href for the provided user group member id.
     */
    public static String getUserGroupMemberHref(final String userGroupHref, final String userGroupMemberId) {

        return userGroupHref + "/selectors/selector/" + userGroupMemberId;
    }

    /**
     * Get the href for current grants.
     *
     * @param userAccountId objid of user account
     * @return href for the provided grant with user account id
     */
    public static String getCurrentGrantsHref(final String userAccountId) {

        return getUserAccountHref(userAccountId) + "/resources/current-grants";
    }

    public static String getPreferencesHref(final String userAccountId) {

        return getUserAccountHref(userAccountId) + "/resources/preferences";
    }

    public static String getAttributesHref(final String userAccountId) {

        return getUserAccountHref(userAccountId) + "/resources/attributes";
    }

    public static String getUserGroupCurrentGrantsHref(final String userGroupId) {

        return getUserGroupHref(userGroupId) + "/resources/current-grants";
    }

    public static String getUserAccountResourcesHref(final String userAccountId) {

        return getUserAccountHref(userAccountId) + "/resources";
    }

    public static String getUserGroupResourcesHref(final String userGroupId) {

        return getUserGroupHref(userGroupId) + "/resources";
    }

    public static String getUserAccountGrantsHref(final String userAccountId) {

        return getUserAccountHref(userAccountId) + "/resources/grants";
    }

    public static String getUserGroupGrantsHref(final String userGroupId) {

        return getUserGroupHref(userGroupId) + "/resources/grants";
    }

    /**
     * Get the href to the specified role grant of the specified user.
     *
     * @param userAccountId The account of the user account owning the grant.
     * @param grantId       The id of the grant.
     * @return The href of the provided role grant.
     */
    public static String getUserAccountGrantHref(final String userAccountId, final String grantId) {

        return getUserAccountGrantsHref(userAccountId) + "/grant/" + grantId;
    }

    /**
     * Get the href to the specified role grant of the specified user group.
     *
     * @param userGroupId id of the user group owning the grant
     * @param grantId     id of the grant
     * @return The href of the provided role grant.
     */
    public static String getUserGroupGrantHref(final String userGroupId, final String grantId) {

        return getUserGroupGrantsHref(userGroupId) + "/grant/" + grantId;
    }

    /**
     * Get the href to the specified role.
     *
     * @param roleId The id of the role.
     * @return Returns the href to the role with the specified id.
     */
    public static String getRoleHref(final String roleId) {

        return BASE_ROLE + roleId;
    }

    /**
     * Get the href to the specified scope.
     *
     * @param scopeId The id of the role.
     * @return Returns the href to the role with the specified id.
     */
    public static String getScopeHref(final String scopeId) {

        return BASE_SCOPE + scopeId;
    }

    /**
     * Get the href to the specified aggregation-definition.
     *
     * @param aggregationDefinitionId The id of the aggregationDefinition.
     * @return Returns the href to the aggregationDefinitionId with the specified id.
     */
    public static String getAggregationDefinitionHref(final String aggregationDefinitionId) {

        return BASE_AGGREGATION_DEFINITION + aggregationDefinitionId;
    }

    /**
     * Get the href to the specified report-definition.
     *
     * @param reportDefinitionId The id of the reportDefinition.
     * @return Returns the href to the reportDefinitionId with the specified id.
     */
    public static String getReportDefinitionHref(final String reportDefinitionId) {

        return BASE_REPORT_DEFINITION + reportDefinitionId;
    }

    /**
     * Adds the commonly used namespaces (xlink) to the provided <code>XMLStreamWriter</code> object.
     *
     * @param writer The <code>XMLStreamWriter</code> object to add the prefixes to.
     * @throws XMLStreamException Thrown in case of an xml stream error.
     */
    public static void addCommonNamespaces(final XMLStreamWriter writer) throws XMLStreamException {

        writer.writeNamespace("xlink", Constants.XLINK_NS_URI);
    }

    /**
     * Adds a new element to the provided <code>XMLStreamWriter</code> object containing a <code>String</code> value.
     *
     * @param writer         The <code>XMLStreamWriter</code> object to add the element to.
     * @param elementName    The name of the new element.
     * @param elementContent The <code>String</code> that shall be set as the value of the new element.
     * @param namespaceUri   The namespace URI of the new element.
     * @param createEmpty    Flag indicating if a an empty element shall be created if the provided data is
     *                       <code>null</code> ( <code>true</code> ), or if the element shall not be created (
     *                       <code>false</code> ).
     * @throws XMLStreamException Thrown in case of an xml stream error.
     */
    public static void addElement(
        final XMLStreamWriter writer, final String elementName, final String elementContent, final String namespaceUri,
        final boolean createEmpty) throws XMLStreamException {
        if (elementContent == null) {
            if (createEmpty) {
                writer.writeEmptyElement(namespaceUri, elementName);
            }
        }
        else {
            writer.writeStartElement(namespaceUri, elementName);
            writer.writeCharacters(elementContent);
            writer.writeEndElement();
        }
    }

    /**
     * Adds a new element to the provided <code>XMLStreamWriter</code> object containing a date value.
     *
     * @param writer         The <code>XMLStreamWriter</code> object to add the element to.
     * @param elementName    The name of the new element.
     * @param elementContent The <code>Date</code> that shall be set as the value of the new element.
     * @param namespaceUri   The namespace URI of the new element.
     * @param createEmpty    Flag indicating if a an empty element shall be created if the provided data is
     *                       <code>null</code> ( <code>true</code> ), or if the element shall not be created (
     *                       <code>false</code> ).
     * @throws XMLStreamException Thrown in case of an xml stream error.
     */
    public static void addElement(
        final XMLStreamWriter writer, final String elementName, final ReadableDateTime elementContent,
        final String namespaceUri, final boolean createEmpty) throws XMLStreamException {

        if (elementContent == null) {
            if (createEmpty) {
                writer.writeEmptyElement(namespaceUri, elementName);
            }
        }
        else {
            writer.writeStartElement(namespaceUri, elementName);
            writer.writeCharacters(elementContent.toString(Constants.TIMESTAMP_FORMAT));
            writer.writeEndElement();
        }
    }

    /**
     * Adds the "last-modification-date" attribute to the provided <code>XMLStreamWriter</code>.<br> The value of the
     * attribute is set to the value of the provided date.<br> If no date is provided, nothing is added.
     *
     * @param writer       The <code>XMLStreamWriter</code> object to add the attribute to.
     * @param modifiedDate The date to set as the last modified date.
     * @throws XMLStreamException Thrown in case of an xml stream error.
     */
    public static void addLastModificationDateAttribute(
        final XMLStreamWriter writer, final ReadableDateTime modifiedDate) throws XMLStreamException {

        if (modifiedDate == null) {
            return;
        }

        writer.writeAttribute("last-modification-date", modifiedDate.toString(Constants.TIMESTAMP_FORMAT));
    }

    /**
     * Adds the provided object id to the <code>XMLStreamWriter</code> that has been provided.<br> The object id is
     * added as the attribute "objid".
     *
     * @param writer The <code>XMLStreamWriter</code> object to add the attribute to.
     * @param objId  The object id to add.
     * @throws XMLStreamException Thrown in case of an xml stream error.
     */
    public static void addObjectId(final XMLStreamWriter writer, final String objId) throws XMLStreamException {

        writer.writeAttribute("objid", objId);
    }

    /**
     * Adds a new element to the provided <code>XMLStreamWriter</code> containing a simple xlink with the provided
     * values. The new element is empty.
     *
     * @param writer       The <code>XMLStreamWriter</code> object to add the element to.
     * @param elementName  The name of the new element.
     * @param xlinkTitle   The title of the xlink contained in the new element.
     * @param xlinkHref    The href of the xlink contained in the new element.
     * @param namespaceUri The namespace URI of the new element.
     * @throws XMLStreamException Thrown in case of an xml stream error.
     */
    public static void addReferencingElement(
        final XMLStreamWriter writer, final String elementName, final String xlinkTitle, final String xlinkHref,
        final String namespaceUri) throws XMLStreamException {

        writer.writeStartElement(namespaceUri, elementName);
        addXlinkAttributes(writer, xlinkTitle, xlinkHref);
        writer.writeEndElement();
    }

    /**
     * Adds the xlink attributes to the provided <code>Element</code>.<br> The attribute "xlink:type" is set to
     * "simple", the attributes "xlink:title" and "xlink:href" to the respective provided values.<br> If the provided
     * title is <code>null</code>. the title attribute is skipped.
     *
     * @param writer     The <code>XMLStreamWriter</code> object to add the attributes to.
     * @param xlinkTitle The title of the xlink.
     * @param xlinkHref  The href of the xlink.
     * @throws XMLStreamException Thrown in case of an xml stream error.
     */
    public static void addXlinkAttributes(final XMLStreamWriter writer, final String xlinkTitle, final String xlinkHref)
        throws XMLStreamException {

        writer.writeAttribute(Constants.XLINK_NS_URI, "type", "simple");
        if (xlinkTitle != null) {
            writer.writeAttribute(Constants.XLINK_NS_URI, "title", xlinkTitle);
        }
        writer.writeAttribute(Constants.XLINK_NS_URI, "href", xlinkHref);
    }

    /**
     * Adds the "xml:base" attribute to the provided <code>XMLStreamWriter</code>.<br> The value of the attribute is set
     * to the value of the configuration property <code>escidoc.baseurl</code>.
     *
     * @param writer The <code>XMLStreamWriter</code> object to add the attribute to.
     * @throws IOException        Thrown if the base url cannot be determined.
     * @throws XMLStreamException Thrown in case of an xml stream error.
     */
    public static void addXmlBaseAttribute(final XMLStreamWriter writer) throws XMLStreamException, IOException {

        writer.writeAttribute(Constants.XML_NS_URI, "base", EscidocConfiguration.getInstance().get(
            EscidocConfiguration.ESCIDOC_CORE_BASEURL));
    }

    /**
     * Sets the commonly used prefixes (xlink, xsi, and xlink).
     *
     * @param writer The <code>XMLStreamWriter</code> object to set the prefixes for.
     * @throws XMLStreamException Thrown in case of an xml stream error.
     */
    public static void setCommonPrefixes(final XMLStreamWriter writer) throws XMLStreamException {

        writer.setPrefix("xlink", Constants.XLINK_NS_URI);
        writer.setPrefix("xml", Constants.XML_NS_URI);
    }

    /**
     * Gets the <code>Schema</code> from the cache.<br> If none exists for the provided schema URL, it is created and
     * put into the cache.
     *
     * @param schemaUri The schema URI
     * @return Returns the validator for the schema specified by the provided URL.
     * @throws IOException              Thrown in case of an I/O error.
     * @throws WebserverSystemException Thrown if schema can not be parsed.
     */
    public static Schema getSchema(final String schemaUri) throws IOException, WebserverSystemException {

        Schema schema = SCHEMA_CACHE.get(schemaUri);
        if (schema == null) {
            final URLConnection conn = new URL(schemaUri).openConnection();
            final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            // set resource resolver to change schema-location-host
            sf.setResourceResolver(new SchemaBaseResourceResolver());

            schema = null;
            try {
                schema = sf.newSchema(new SAXSource(new InputSource(conn.getInputStream())));
            }
            catch (final SAXException e) {
                throw new WebserverSystemException("Problem with schema " + schemaUri + ". ", e);
            }

            SCHEMA_CACHE.put(schemaUri, schema);

        }
        return schema;
    }

    /**
     * Gets a <code>Validator</code> object for the specified schema.<br> The validator is thread-unsafe and
     * non-reentrant. Therefore, it is not cached but a new <code>Validator</code> object is created using cached
     * <code>Schema</code>.
     *
     * @param schemaUri The schema URI
     * @return Returns the validator for the schema specified by the provided URL.
     * @throws IOException              Thrown in case of an I/O error.
     * @throws WebserverSystemException Thrown if schema can not be parsed.
     */
    public static Validator getValidator(final String schemaUri) throws IOException, WebserverSystemException {

        return getSchema(schemaUri).newValidator();
    }

    /**
     * Validates the provided XML data using the specified schema and creates a <code>ByteArrayInputStream</code> for
     * the data.
     *
     * @param xmlData   The xml data.
     * @param schemaUri The URL identifying the schema that shall be used for validation.
     * @return Returns the xml data in a <code>ByteArrayInputStream</code>.
     * @throws XmlSchemaValidationException Thrown if data in not valid.
     * @throws XmlCorruptedException        Thrown if the XML data cannot be parsed.
     * @throws WebserverSystemException     Thrown in case of any other failure.
     */
    public static ByteArrayInputStream createValidatedByteArrayInputStream(final String xmlData, final String schemaUri)
        throws XmlCorruptedException, WebserverSystemException, XmlSchemaValidationException {

        final ByteArrayInputStream byteArrayInputStream = convertToByteArrayInputStream(xmlData);
        validate(byteArrayInputStream, schemaUri);
        return byteArrayInputStream;
    }

    /**
     * Validates the provided XML data using the specified schema.<br> The provided <code>ByteArrayInputStream</code> is
     * reset after validation.
     *
     * @param byteArrayInputStream The XML data to validate in an <code>ByteArrayInputStream</code>.<br> This input
     *                             stream is reset after the validation.
     * @param schemaUri            The URL identifying the schema that shall be used for validation.
     * @throws XmlCorruptedException        Thrown if the XML data cannot be parsed.
     * @throws XmlSchemaValidationException Thrown if both validation fail or only one validation is executed and fails
     * @throws WebserverSystemException     Thrown in any other case.
     */
    public static void validate(final ByteArrayInputStream byteArrayInputStream, final String schemaUri)
        throws XmlCorruptedException, XmlSchemaValidationException, WebserverSystemException {

        try {
            final Validator validator = getValidator(schemaUri);
            validator.validate(new SAXSource(new InputSource(byteArrayInputStream)));
        }
        catch (final SAXParseException e) {
            final String errorMsg =
                "Error in line " + e.getLineNumber() + ", column " + e.getColumnNumber() + ". " + e.getMessage();
            if (e.getMessage().startsWith("cvc")) {
                throw new XmlSchemaValidationException(errorMsg, e);
            }
            else {
                throw new XmlCorruptedException(errorMsg, e);
            }
        }
        catch (final Exception e) {
            throw new WebserverSystemException(e.getMessage(), e);
        }
        finally {
            if (byteArrayInputStream != null) {
                byteArrayInputStream.reset();
            }
        }
    }

    /**
     * Checks, if the provided XML data has the provided root element, afterwards validates the provided XML data using
     * the specified schema.
     *
     * @param xmlData   The XML data to validate.
     * @param schemaUri The URL identifying the schema that shall be used for validation.
     * @param root      Check for this root element.
     * @throws XmlCorruptedException        Thrown if the XML data cannot be parsed.
     * @throws XmlSchemaValidationException Thrown if both validation fail or only one validation is executed and fails
     * @throws WebserverSystemException     Thrown in any other case.
     * @throws XmlParserSystemException     Thrown if the expected root element raise an unexpected error.
     */
    public static void validate(final String xmlData, final String schemaUri, final String root)
        throws XmlCorruptedException, XmlSchemaValidationException, WebserverSystemException, XmlParserSystemException {

        if (root.length() > 0) {
            checkRootElement(xmlData, root);
        }
        validate(xmlData, schemaUri);
    }

    /**
     * Check if the root element has the expected element.
     *
     * @param xmlData      The XML document which is to check.
     * @param expectedRoot The expected root element.
     * @throws XmlCorruptedException    Thrown if the document has not the expected element.
     * @throws XmlParserSystemException Thrown if the expected root element raise an unexpected error.
     */
    private static void checkRootElement(final String xmlData, final String expectedRoot) throws XmlCorruptedException,
        XmlParserSystemException {

        final StaxParser sp = new StaxParser();
        final CheckRootElementStaxHandler checkRoot = new CheckRootElementStaxHandler(expectedRoot);
        sp.addHandler(checkRoot);
        try {
            sp.parse(xmlData);
        }
        catch (final InvalidXmlException e) {
            throw new XmlCorruptedException("Xml Document has wrong root element, expected '" + expectedRoot + "'.", e);
        }
        catch (final WstxParsingException e) {
            throw new XmlCorruptedException(e.getMessage(), e);
        }
        catch (final WebserverSystemException e) {
            // ignore, check was successful and parsing aborted
        }
        catch (final Exception e) {
            handleUnexpectedStaxParserException("Check for root '" + expectedRoot
                + "' element raised unexpected exception! ", e);
        }
    }

    /**
     * Validates the provided XML data using the specified schema.
     *
     * @param xmlData   The XML data to validate.
     * @param schemaUri The URL identifying the schema that shall be used for validation.
     * @throws XmlCorruptedException        Thrown if the XML data cannot be parsed.
     * @throws XmlSchemaValidationException Thrown if both validation fail or only one validation is executed and fails
     * @throws WebserverSystemException     Thrown in any other case.
     */
    public static void validate(final String xmlData, final String schemaUri) throws XmlCorruptedException,
        XmlSchemaValidationException, WebserverSystemException {

        validate(convertToByteArrayInputStream(xmlData), schemaUri);
    }

    /**
     * Validates the provided XML data using the specified resource type.
     *
     * @param xmlData      The XML data to validate.
     * @param resourceType The resourceType whose schema will be used for validation validation.
     * @throws XmlCorruptedException        Thrown if the XML data cannot be parsed.
     * @throws XmlSchemaValidationException # Thrown if both validation fail or only one validation is executed and
     *                                      fails
     * @throws WebserverSystemException     Thrown in any other case.
     */
    public static void validate(final String xmlData, final ResourceType resourceType) throws XmlCorruptedException,
        XmlSchemaValidationException, WebserverSystemException {
        validate(xmlData, getSchemaLocationForResource(resourceType));

    }

    /**
     * Converts the provided String to a <code>ByteArrayInputStream</code>.
     *
     * @param str The string to get as <code>ByteArrayInputStream</code>.
     * @return Returns the <code>ByteArrayInputStream</code> for the provided string.
     */
    public static ByteArrayInputStream convertToByteArrayInputStream(final String str) {

        try {
            return new ByteArrayInputStream(str.getBytes(CHARACTER_ENCODING));
        }
        catch (final UnsupportedEncodingException e) {
            // this should not happen
            return new ByteArrayInputStream("".getBytes());
        }
    }

    /**
     * Converts the provided String to a <code>ByteArrayOutputStream</code>.
     *
     * @param str The string to get as <code>ByteArrayOutputStream</code>.
     * @return Returns the <code>ByteArrayOutputStream</code> for the provided string.
     */
    public static ByteArrayOutputStream convertToByteArrayOutputStream(final String str) {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(str.getBytes(CHARACTER_ENCODING));
        }
        catch (final UnsupportedEncodingException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on writing to stream.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on writing to stream.", e);
            }
        }
        catch (final IOException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on writing to stream.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on writing to stream.", e);
            }
        }
        return stream;
    }

    /**
     * Get the objid from an URI/Fedora identifier, e.g. from &lt;info:fedora/escidoc:1&gt;<br/> If the provided value
     * does not match the expected pattern, it is returned as provided. Otherwise, the objid is extracted from it and
     * returned.
     *
     * @param uri The value to get the objid from
     * @return Returns the extracted objid or the provided value.
     */
    public static String getIdFromURI(final String uri) {

        if (uri == null) {
            return null;
        }
        final Matcher matcher = PATTERN_GET_ID_FROM_URI_OR_FEDORA_ID.matcher(uri);
        return matcher.find() ? matcher.group(1) : uri;
    }

    /**
     * Extracts the objid from the provided resource XML representation.<br/> Either the first occurence of objid="..."
     * is searched and the value is returned, or the first occurence of :href="..." is searched and from this value the
     * objid is extracted and returned.
     *
     * @param resourceXml The XML representation of the resource to get the objid from.
     * @return Returns the extracted objid or <code>null</code>.
     */
    public static String getIdFromXml(final CharSequence resourceXml) {

        final Matcher matcher = PATTERN_OBJID_FROM_XML.matcher(resourceXml);
        return matcher.find() ? matcher.group(2) : null;
    }

    /**
     * Extracts the objid from the provided element.<br/> Either the id is fetched from the attribute objid of the
     * provided element. If this fails, it is extracted from the attribute href. If this fials, too, an exception is
     * thrown.
     *
     * @param element The element to get the objid from.
     * @return Returns the objid value.
     * @throws MissingAttributeValueException Thrown if neither an objid nor an href attribute exists.
     */

    public static String getIdFromStartElement(final StartElement element) throws MissingAttributeValueException {

        try {
            final String objid =
                element.indexOfAttribute(null, NAME_OBJID) == -1 ? getIdFromURI(element.getAttributeValue(
                    Constants.XLINK_NS_URI, NAME_HREF)) : element.getAttributeValue(null, NAME_OBJID);
            return objid;
        }
        catch (final NoSuchAttributeException e) {
            throwMissingAttributeValueException(element, NAME_OBJID + '|' + NAME_HREF);
            return null;
        }
    }

    /**
     * Remove version information from given objid.
     *
     * @param objid The objid.
     * @return The objid without version information.
     */
    public static String getObjidWithoutVersion(final String objid) {

        String result = objid;
        final Matcher m = PATTERN_ID_WITHOUT_VERSION.matcher(objid);
        if (m.find()) {
            result = m.group(1);
        }
        return result;
    }

    /**
     * Extract version number from objid.
     *
     * @param objid The objid.
     * @return The number of version or null.
     */
    public static String getVersionNumberFromObjid(final CharSequence objid) {
        String version = null;
        final Matcher m = PATTERN_VERSION_NUMBER.matcher(objid);
        if (m.find()) {
            version = m.group(1);
        }
        return version;
    }

    /**
     * Extracts the name from the provided resource XML representation.<br> The first occurence of
     * &lt;...:name&gt;...&lt;/...:name&gt; is searched and the value is returned.
     *
     * @param resourceXml The XML representation of the resource to get the name from.
     * @return Returns the extracted name (trimmed) or <code>null</code>.
     */
    public static String extractNameFromXml(final CharSequence resourceXml) {

        final Matcher matcher = PATTERN_NAME_FROM_XML.matcher(resourceXml);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    /**
     * Parse the task parameter structure.
     *
     * @param param The parameter structure.
     * @return The handler holding the extracted values.
     * @throws EncodingSystemException If a wrong Encoding is detected.
     * @throws XmlCorruptedException   If the given XML is not valid.
     */
    public static TaskParamHandler parseTaskParam(final String param) throws XmlCorruptedException,
        EncodingSystemException {
        return parseTaskParam(param, true);
    }

    public static TaskParamHandler parseTaskParam(final String param, final boolean checkLastModificationDate)
        throws XmlCorruptedException, EncodingSystemException {

        final StaxParser staxParser = new StaxParser();
        final TaskParamHandler result = new TaskParamHandler(staxParser);
        if (param != null) {
            result.setCheckLastModificationDate(checkLastModificationDate);
            final ByteArrayInputStream xmlDataIs;
            try {
                xmlDataIs = new ByteArrayInputStream(param.getBytes(CHARACTER_ENCODING));
            }
            catch (final UnsupportedEncodingException e) {
                throw new EncodingSystemException(e.getMessage(), e);
            }
            final List<DefaultHandler> handlerChain = new ArrayList<DefaultHandler>();
            handlerChain.add(result);
            staxParser.setHandlerChain(handlerChain);
            try {
                staxParser.parse(xmlDataIs);
            }
            catch (final Exception e) {
                throw new XmlCorruptedException(e.getMessage(), e);
            }

            staxParser.clearHandlerChain();
        }
        return result;
    }

    /**
     * Retrieves the base url for XML schemas for internal validation (i.e. escidoc-core.selfurl +
     * escidoc-core.xsd-path.<br>
     *
     * @return Returns the base url.
     * @throws WebserverSystemException In case of an error.
     */
    private static String getSchemaBaseUrl() throws WebserverSystemException {

        try {
            return EscidocConfiguration.getInstance().get(EscidocConfiguration.ESCIDOC_CORE_SELFURL)
                + EscidocConfiguration.getInstance().get(EscidocConfiguration.ESCIDOC_CORE_XSD_PATH) + '/';
        }
        catch (final IOException e) {
            throw new WebserverSystemException("Error accessing Escidoc configuration!", e);
        }
    }

    /**
     * Gets the stylesheet definition.
     *
     * @return Returns the stylesheet definition. This may be an empty string, if the xslt has not been defined with the
     *         eSciDoc configuration property escidoc.xslt.std.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getStylesheetDefinition() throws WebserverSystemException {

        if (stylesheetDefinition == null) {
            try {
                final String xslt = EscidocConfiguration.getInstance().get(EscidocConfiguration.ESCIDOC_CORE_XSLT_STD);
                stylesheetDefinition =
                    xslt != null && xslt.length() > 0 ? "<?xml-stylesheet type=\"text/xsl\" " + "href=\""
                        + getEscidocBaseUrl() + xslt + "\"?>\n" : "";
            }
            catch (final IOException e) {
                throw new WebserverSystemException(e.getMessage(), e);
            }
        }
        return stylesheetDefinition;
    }

    /**
     * @return Returns the adminDescriptorSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getAdminDescriptorSchemaLocation() throws WebserverSystemException {

        final String result;
        final String subPath = "context/0.4/context.xsd";
        if (UserContext.isRestAccess()) {
            if (contextRestSchemaLocation == null) {
                contextRestSchemaLocation = getSchemaBaseUrl() + "rest/" + subPath;
            }
            result = contextRestSchemaLocation;
        }
        else {
            if (contextSoapSchemaLocation == null) {
                contextSoapSchemaLocation = getSchemaBaseUrl() + "soap/" + subPath;
            }
            result = contextSoapSchemaLocation;
        }
        return result;
    }

    /**
     * @return Returns the containerSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getContainerSchemaLocation() throws WebserverSystemException {

        final String result;
        if (UserContext.isRestAccess()) {
            if (containerRestSchemaLocation == null) {
                containerRestSchemaLocation =
                    getSchemaBaseUrl() + "rest/" + "container" + Constants.CONTAINER_NS_URI_SCHEMA_VERSION
                        + "/container.xsd";
            }
            result = containerRestSchemaLocation;
        }
        else {
            if (containerSoapSchemaLocation == null) {
                containerSoapSchemaLocation =
                    getSchemaBaseUrl() + "soap/" + "container" + Constants.CONTAINER_NS_URI_SCHEMA_VERSION
                        + "/container.xsd";
            }
            result = containerSoapSchemaLocation;
        }
        return result;
    }

    /**
     * @return Returns the spoTaskParamSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getSpoTaskParamSchemaLocation() throws WebserverSystemException {
        if (spoTaskParamSchemaLocation == null) {
            spoTaskParamSchemaLocation = getSchemaBaseUrl() + "common/0.3/query.xsd";
        }
        return spoTaskParamSchemaLocation;
    }

    /**
     * @return Returns the containerMembersFilterSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getContainerMembersFilterSchemaLocation() throws WebserverSystemException {
        final String result;
        if (UserContext.isRestAccess()) {
            if (containerMembersFilterRestSchemaLocation == null) {
                containerMembersFilterRestSchemaLocation =
                    getSchemaBaseUrl() + "rest/" + "container/0.3/filter-members.xsd";
            }
            result = containerMembersFilterRestSchemaLocation;
        }
        else {
            if (containerMembersFilterSoapSchemaLocation == null) {
                containerMembersFilterSoapSchemaLocation =
                    getSchemaBaseUrl() + "soap/" + "container/0.3/filter-members.xsd";
            }
            result = containerMembersFilterSoapSchemaLocation;
        }
        return result;
    }

    /**
     * @return Returns the containersFilterSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getContainersFilterSchemaLocation() throws WebserverSystemException {
        final String result;
        if (UserContext.isRestAccess()) {
            containersFilterRestSchemaLocation = getSchemaBaseUrl() + "rest/" + "container/0.3/filter-containers.xsd";
            result = containersFilterRestSchemaLocation;
        }
        else {
            containersFilterSoapSchemaLocation = getSchemaBaseUrl() + "soap/" + "container/0.3/filter-containers.xsd";
            result = containersFilterSoapSchemaLocation;
        }

        return result;
    }

    /**
     * @return Returns the content relation schema location.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getContentModelSchemaLocation() throws WebserverSystemException {

        final String result;
        final String contentModelXsd =
            "content-model" + Constants.CONTENT_MODEL_NS_URI_SCHEMA_VERSION + "/content-model.xsd";
        if (UserContext.isRestAccess()) {
            if (contentModelRestSchemaLocation == null) {
                contentModelRestSchemaLocation = getSchemaBaseUrl() + "rest/" + contentModelXsd;
            }
            result = contentModelRestSchemaLocation;
        }
        else {
            if (contentModelSoapSchemaLocation == null) {
                contentModelSoapSchemaLocation = getSchemaBaseUrl() + "soap/" + contentModelXsd;
            }
            result = contentModelSoapSchemaLocation;
        }
        return result;
    }

    /**
     * @return Returns the contextSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getContextSchemaLocation() throws WebserverSystemException {

        final String result;
        final String contextXsd = "context" + Constants.CONTEXT_NS_URI_SCHEMA_VERSION + "/context.xsd";
        if (UserContext.isRestAccess()) {
            if (contextRestSchemaLocation == null) {
                contextRestSchemaLocation = getSchemaBaseUrl() + "rest/" + contextXsd;
            }
            result = contextRestSchemaLocation;
        }
        else {
            if (contextSoapSchemaLocation == null) {
                contextSoapSchemaLocation = getSchemaBaseUrl() + "soap/" + contextXsd;
            }
            result = contextSoapSchemaLocation;
        }
        return result;
    }

    /**
     * @return Returns the content relation schema location.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getContentRelationSchemaLocation() throws WebserverSystemException {

        final String result;
        final String contentRelationXsd =
            "content-relation" + Constants.CONTENT_RELATION_NS_URI_SCHEMA_VERSION + "/content-relation.xsd";
        if (UserContext.isRestAccess()) {
            if (contentRelationRestSchemaLocation == null) {
                contentRelationRestSchemaLocation = getSchemaBaseUrl() + "rest/" + contentRelationXsd;
            }
            result = contentRelationRestSchemaLocation;
        }
        else {
            if (contentRelationSoapSchemaLocation == null) {
                contentRelationSoapSchemaLocation = getSchemaBaseUrl() + "soap/" + contentRelationXsd;
            }
            result = contentRelationSoapSchemaLocation;
        }
        return result;
    }

    /**
     * @return Returns the contextSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getSetDefinitionSchemaLocation() throws WebserverSystemException {

        final String result;
        final String setDefinitionXsd = "set-definition/0.1/set-definition.xsd";
        if (UserContext.isRestAccess()) {
            if (setDefinitionRestSchemaLocation == null) {
                setDefinitionRestSchemaLocation = getSchemaBaseUrl() + "rest/" + setDefinitionXsd;
            }
            result = setDefinitionRestSchemaLocation;
        }
        else {
            if (setDefinitionSoapSchemaLocation == null) {
                setDefinitionSoapSchemaLocation = getSchemaBaseUrl() + "soap/" + setDefinitionXsd;
            }
            result = setDefinitionSoapSchemaLocation;
        }
        return result;
    }

    /**
     * @return Returns the contextsFilterSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getContextsFilterSchemaLocation() throws WebserverSystemException {

        final String result;
        if (UserContext.isRestAccess()) {
            if (contextsFilterSchemaLocationRest == null) {
                contextsFilterSchemaLocationRest = getSchemaBaseUrl() + "rest/" + "context/0.3/filter-contexts.xsd";
            }
            result = contextsFilterSchemaLocationRest;
        }
        else {
            if (contextsFilterSchemaLocationSoap == null) {
                contextsFilterSchemaLocationSoap = getSchemaBaseUrl() + "soap/" + "context/0.3/filter-contexts.xsd";
            }
            result = contextsFilterSchemaLocationSoap;
        }

        return result;
    }

    /**
     * @return Returns the contextMembersFilterSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getContextMembersFilterSchemaLocation() throws WebserverSystemException {

        final String result;
        if (UserContext.isRestAccess()) {
            if (contextMembersFilterSchemaLocationRest == null) {
                contextMembersFilterSchemaLocationRest =
                    getSchemaBaseUrl() + "rest/" + "context/0.3/filter-contexts.xsd";
            }
            result = contextMembersFilterSchemaLocationRest;
        }
        else {
            if (contextMembersFilterSchemaLocationSoap == null) {
                contextMembersFilterSchemaLocationSoap =
                    getSchemaBaseUrl() + "soap/" + "context/0.3/filter-contexts.xsd";
            }
            result = contextMembersFilterSchemaLocationSoap;
        }

        return result;
    }

    /**
     * @return Returns the grantsSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getGrantsSchemaLocation() throws WebserverSystemException {

        return getSchemaLocation("user-account/0.5/grants.xsd");
    }

    /**
     * @return Returns the preferencesSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getPreferencesSchemaLocation() throws WebserverSystemException {

        return getSchemaLocation("user-account/0.1/preferences.xsd");
    }

    /**
     * @return Returns the preferencesSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getAttributesSchemaLocation() throws WebserverSystemException {

        return getSchemaLocation("user-account/0.1/attributes.xsd");
    }

    /**
     * @return Returns the itemSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getItemSchemaLocation() throws WebserverSystemException {

        final String result;
        if (UserContext.isRestAccess()) {
            if (itemRestSchemaLocation == null) {
                itemRestSchemaLocation =
                    getSchemaBaseUrl() + "rest/" + "item" + Constants.ITEM_NS_URI_SCHEMA_VERSION + "/item.xsd";
            }
            result = itemRestSchemaLocation;
        }
        else {
            if (itemSoapSchemaLocation == null) {
                itemSoapSchemaLocation =
                    getSchemaBaseUrl() + "soap/" + "item" + Constants.ITEM_NS_URI_SCHEMA_VERSION + "/item.xsd";
            }
            result = itemSoapSchemaLocation;
        }
        return result;
    }

    /**
     * @return Returns the itemsFilterSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getUpdateRelationsSchemaLocation() throws WebserverSystemException {
        final String result;
        if (UserContext.isRestAccess()) {
            if (updateRelationsSchemaLocation == null) {
                updateRelationsSchemaLocation = getSchemaBaseUrl() + "rest/common/0.3/update-relations.xsd";
            }
            result = updateRelationsSchemaLocation;
        }
        else {
            if (updateRelationsSchemaLocation == null) {
                updateRelationsSchemaLocation = getSchemaBaseUrl() + "soap/common/0.3/update-relations.xsd";
            }
            result = updateRelationsSchemaLocation;
        }
        return result;

    }

    /**
     * @return Returns the relationsSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getRelationsSchemaLocation() throws WebserverSystemException {
        final String result;
        if (UserContext.isRestAccess()) {
            if (relationsSchemaLocation == null) {
                relationsSchemaLocation = getSchemaBaseUrl() + "rest/common/0.3/relations.xsd";
            }
            result = relationsSchemaLocation;
        }
        else {
            if (relationsSchemaLocation == null) {
                relationsSchemaLocation = getSchemaBaseUrl() + "soap/common/0.3/relations.xsd";
            }
            result = relationsSchemaLocation;
        }
        return result;
    }

    /**
     * @return Returns the organizationalUnitSchemaLocation dependent on UserContext flag isRestAccess.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getOrganizationalUnitSchemaLocation() throws WebserverSystemException {

        final String result;
        if (UserContext.isRestAccess()) {
            if (organizationalUnitRestSchemaLocation == null) {
                organizationalUnitRestSchemaLocation =
                    getSchemaBaseUrl() + "rest/" + "organizational-unit"
                        + Constants.ORGANIZATIONAL_UNIT_NS_URI_SCHEMA_VERSION + "/organizational-unit.xsd";
            }
            result = organizationalUnitRestSchemaLocation;
        }
        else {
            if (organizationalUnitSoapSchemaLocation == null) {
                organizationalUnitSoapSchemaLocation =
                    getSchemaBaseUrl() + "soap/" + "organizational-unit"
                        + Constants.ORGANIZATIONAL_UNIT_NS_URI_SCHEMA_VERSION + "/organizational-unit.xsd";
            }
            result = organizationalUnitSoapSchemaLocation;
        }
        return result;
    }

    /**
     * @return Returns the organizationalUnitListSchemaLocation dependent on UserContext flag isRestAccess.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getOrganizationalUnitListSchemaLocation() throws WebserverSystemException {

        final String result;
        if (UserContext.isRestAccess()) {
            if (organizationalUnitListRestSchemaLocation == null) {
                organizationalUnitListRestSchemaLocation =
                    getSchemaBaseUrl() + "rest/" + "organizational-unit"
                        + Constants.CONTAINER_LIST_NS_URI_SCHEMA_VERSION + "/organizational-unit-list.xsd";
            }
            result = organizationalUnitListRestSchemaLocation;
        }
        else {
            if (organizationalUnitListSoapSchemaLocation == null) {
                organizationalUnitListSoapSchemaLocation =
                    getSchemaBaseUrl() + "soap/" + "organizational-unit"
                        + Constants.CONTAINER_LIST_NS_URI_SCHEMA_VERSION + "/organizational-unit-list.xsd";
            }
            result = organizationalUnitListSoapSchemaLocation;
        }
        return result;
    }

    /**
     * @return Returns the organizationalUnitPathListSchemaLocation dependent on UserContext flag isRestAccess.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getOrganizationalUnitPathListSchemaLocation() throws WebserverSystemException {

        final String result;
        if (UserContext.isRestAccess()) {
            if (organizationalUnitPathListRestSchemaLocation == null) {
                organizationalUnitPathListRestSchemaLocation =
                    getSchemaBaseUrl() + "rest/" + "organizational-unit/0.4/organizational-unit-path-list.xsd";
            }
            result = organizationalUnitPathListRestSchemaLocation;
        }
        else {
            if (organizationalUnitPathListSoapSchemaLocation == null) {
                organizationalUnitPathListSoapSchemaLocation =
                    getSchemaBaseUrl() + "soap/" + "organizational-unit/0.4/organizational-unit-path-list.xsd";
            }
            result = organizationalUnitPathListSoapSchemaLocation;
        }
        return result;
    }

    /**
     * @return Returns the organizationalUnitRefListSchemaLocation dependent on UserContext flag isRestAccess.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getOrganizationalUnitRefListSchemaLocation() throws WebserverSystemException {

        final String result;
        if (UserContext.isRestAccess()) {
            if (organizationalUnitRefListRestSchemaLocation == null) {
                organizationalUnitRefListRestSchemaLocation =
                    getSchemaBaseUrl() + "rest/" + "organizational-unit/0.4/organizational-unit-ref-list.xsd";
            }
            result = organizationalUnitRefListRestSchemaLocation;
        }
        else {
            if (organizationalUnitRefListSoapSchemaLocation == null) {
                organizationalUnitRefListSoapSchemaLocation =
                    getSchemaBaseUrl() + "soap/" + "organizational-unit/0.4/organizational-unit-ref-list.xsd";
            }
            result = organizationalUnitRefListSoapSchemaLocation;
        }
        return result;
    }

    /**
     * @return Returns the filterSchemaLocation dependent on UserContext flag isRestAccess.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getFilterSchemaLocation() throws WebserverSystemException {

        final String result;
        if (UserContext.isRestAccess()) {
            if (filterSchemaLocationRest == null) {
                filterSchemaLocationRest = getSchemaBaseUrl() + "rest/" + "common/0.4/filter.xsd";
            }
            result = filterSchemaLocationRest;
        }
        else {
            if (filterSchemaLocationSoap == null) {
                filterSchemaLocationSoap = getSchemaBaseUrl() + "soap/" + "common/0.4/filter.xsd";
            }
            result = filterSchemaLocationSoap;
        }

        return result;
    }

    /**
     * @return Returns the pdpRequestsSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getPdpRequestsSchemaLocation() throws WebserverSystemException {

        // There are is no difference between calling the pdp via rest or soap,
        // the schemas are the same. Therefore, the schema from the rest folder
        // is allways used.
        if (pdpRequestsSchemaLocation == null) {
            pdpRequestsSchemaLocation = getSchemaBaseUrl() + "rest/pdp/0.3/requests.xsd";
        }
        return pdpRequestsSchemaLocation;
    }

    /**
     * @return Returns the roleSchemaLocation dependent on UserContext flag isRestAccess.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getRoleSchemaLocation() throws WebserverSystemException {

        return getSchemaLocation("role/0.5/role.xsd");
    }

    /**
     * @return Returns the stagingFileSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getStagingFileSchemaLocation() throws WebserverSystemException {

        if (stagingFileSchemaLocation == null) {
            stagingFileSchemaLocation = getSchemaBaseUrl() + "rest/staging-file/0.3/staging-file.xsd";
        }
        return stagingFileSchemaLocation;
    }

    /**
     * @return Returns the tmeRequestsSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getTmeRequestsSchemaLocation() throws WebserverSystemException {

        // There is no difference between calling the Tme via rest or soap,
        // the schemas are the same. Therefore, the schema from the rest folder
        // is allways used.
        if (tmeRequestsSchemaLocation == null) {
            tmeRequestsSchemaLocation = getSchemaBaseUrl() + "tme/0.1/request.xsd";
        }
        return tmeRequestsSchemaLocation;
    }

    /**
     * @return Returns the unsecuredActionsSchemaLocation dependent on UserContext flag isRestAccess.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getUnsecuredActionsSchemaLocation() throws WebserverSystemException {

        return getSchemaLocation("role/0.4/unsecured-actions.xsd");
    }

    /**
     * @return Returns the userAccountSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getUserAccountSchemaLocation() throws WebserverSystemException {

        return getSchemaLocation("user-account" + Constants.USER_ACCOUNT_NS_URI_SCHEMA_VERSION + "/user-account.xsd");
    }

    /**
     * @return Returns the addSelectorsSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getAddSelectorsSchemaLocation() throws WebserverSystemException {

        return getSchemaLocation("user-group/0.6/add-selectors.xsd");
    }

    /**
     * @return Returns the removeSelectorsSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getRemoveSelectorsSchemaLocation() throws WebserverSystemException {

        return getSchemaLocation("user-group/0.6/remove-selectors.xsd");
    }

    /**
     * @return Returns the userGroupSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getUserGroupSchemaLocation() throws WebserverSystemException {

        return getSchemaLocation("user-group/0.6/user-group.xsd");
    }

    /**
     * @return Returns the statisticDataSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getStatisticDataSchemaLocation() throws WebserverSystemException {

        if (statisticDataSchemaLocation == null) {
            statisticDataSchemaLocation = getSchemaBaseUrl() + "statistic-data/0.3/statistic-data.xsd";
        }
        return statisticDataSchemaLocation;
    }

    /**
     * @return Returns the aggregationDefinitionSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getAggregationDefinitionSchemaLocation() throws WebserverSystemException {
        final String result;
        if (UserContext.isRestAccess()) {
            if (aggregationDefinitionRestSchemaLocation == null) {
                aggregationDefinitionRestSchemaLocation =
                    getSchemaBaseUrl() + "rest/aggregation-definition"
                        + Constants.AGGREGATION_DEFINITION_NS_URI_SCHEMA_VERSION + "/aggregation-definition.xsd";
            }
            result = aggregationDefinitionRestSchemaLocation;
        }
        else {
            if (aggregationDefinitionSoapSchemaLocation == null) {
                aggregationDefinitionSoapSchemaLocation =
                    getSchemaBaseUrl() + "soap/aggregation-definition"
                        + Constants.AGGREGATION_DEFINITION_NS_URI_SCHEMA_VERSION + "/aggregation-definition.xsd";
            }
            result = aggregationDefinitionSoapSchemaLocation;
        }

        return result;
    }

    /**
     * @return Returns the reportDefinitionSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getReportDefinitionSchemaLocation() throws WebserverSystemException {
        final String result;
        if (UserContext.isRestAccess()) {
            if (reportDefinitionRestSchemaLocation == null) {
                reportDefinitionRestSchemaLocation =
                    getSchemaBaseUrl() + "rest/report-definition" + Constants.REPORT_DEFINITION_NS_URI_SCHEMA_VERSION
                        + "/report-definition.xsd";
            }
            result = reportDefinitionRestSchemaLocation;
        }
        else {
            if (reportDefinitionSoapSchemaLocation == null) {
                reportDefinitionSoapSchemaLocation =
                    getSchemaBaseUrl() + "soap/report-definition" + Constants.REPORT_DEFINITION_NS_URI_SCHEMA_VERSION
                        + "/report-definition.xsd";
            }
            result = reportDefinitionSoapSchemaLocation;
        }

        return result;
    }

    /**
     * @return Returns the scopeSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getScopeSchemaLocation() throws WebserverSystemException {
        final String result;
        if (UserContext.isRestAccess()) {
            if (scopeRestSchemaLocation == null) {
                scopeRestSchemaLocation =
                    getSchemaBaseUrl() + "rest/scope" + Constants.SCOPE_NS_URI_SCHEMA_VERSION + "/scope.xsd";
            }
            result = scopeRestSchemaLocation;
        }
        else {
            if (scopeSoapSchemaLocation == null) {
                scopeSoapSchemaLocation =
                    getSchemaBaseUrl() + "soap/scope" + Constants.SCOPE_NS_URI_SCHEMA_VERSION + "/scope.xsd";
            }
            result = scopeSoapSchemaLocation;
        }

        return result;
    }

    /**
     * @return Returns the reportSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getReportSchemaLocation() throws WebserverSystemException {
        final String result;
        if (UserContext.isRestAccess()) {
            if (reportRestSchemaLocation == null) {
                reportRestSchemaLocation =
                    getSchemaBaseUrl() + "rest/report" + Constants.REPORT_NS_URI_SCHEMA_VERSION + "/report.xsd";
            }
            result = reportRestSchemaLocation;
        }
        else {
            if (reportSoapSchemaLocation == null) {
                reportSoapSchemaLocation =
                    getSchemaBaseUrl() + "soap/report" + Constants.REPORT_NS_URI_SCHEMA_VERSION + "/report.xsd";
            }
            result = reportSoapSchemaLocation;
        }

        return result;
    }

    /**
     * @return Returns the ReportParametersSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getReportParametersSchemaLocation() throws WebserverSystemException {
        final String result;
        if (UserContext.isRestAccess()) {
            if (reportParametersRestSchemaLocation == null) {
                reportParametersRestSchemaLocation =
                    getSchemaBaseUrl() + "rest/report" + Constants.REPORT_PARAMETERS_NS_URI_SCHEMA_VERSION
                        + "/report-parameters.xsd";
            }
            result = reportParametersRestSchemaLocation;
        }
        else {
            if (reportParametersSoapSchemaLocation == null) {
                reportParametersSoapSchemaLocation =
                    getSchemaBaseUrl() + "soap/report" + Constants.REPORT_PARAMETERS_NS_URI_SCHEMA_VERSION
                        + "/report-parameters.xsd";
            }
            result = reportParametersSoapSchemaLocation;
        }

        return result;
    }

    /**
     * @return Returns the PreprocessingInformationSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getPreprocessingInformationSchemaLocation() throws WebserverSystemException {

        if (preprocessingInformationSchemaLocation == null) {
            preprocessingInformationSchemaLocation =
                getSchemaBaseUrl() + "soap/preprocessing-information"
                    + Constants.PREPROCESSING_INFORMATION_NS_URI_SCHEMA_VERSION + "/preprocessing-information.xsd";
        }
        return preprocessingInformationSchemaLocation;
    }

    /**
     * @return Returns the xmlSchemaSchemaLocation.
     * @throws WebserverSystemException In case of an error.
     */
    public static String getXmlSchemaSchemaLocation() throws WebserverSystemException {

        if (xmlSchemaSchemaLocation == null) {
            xmlSchemaSchemaLocation = getSchemaBaseUrl() + "common/0.2/xml-schema.xsd";
        }
        return xmlSchemaSchemaLocation;
    }

    /**
     * @param commonPart The tailing part of a schema location that is common for the rest schema location and the soap
     *                   schema location, e.g. role/0.4/role.xsd.
     * @return Returns the complete schema location for the provided value dependent on UserContext flag isRestAccess.
     * @throws WebserverSystemException In case of an error.
     */
    private static String getSchemaLocation(final String commonPart) throws WebserverSystemException {

        String result;

        if (UserContext.isRestAccess()) {
            result = REST_SCHEMA_LOCATIONS.get(commonPart);
            if (result == null) {
                result = getSchemaBaseUrl() + "rest/" + commonPart;
                REST_SCHEMA_LOCATIONS.put(commonPart, result);
            }
        }
        else {
            result = SOAP_SCHEMA_LOCATIONS.get(commonPart);
            if (result == null) {
                result = getSchemaBaseUrl() + "soap/" + commonPart;
                SOAP_SCHEMA_LOCATIONS.put(commonPart, result);
            }
        }
        return result;
    }

    /**
     * Replace forbidden characters in xml content with their escape sequence.<br/> This method escapes &, <, and > in
     * attributes and text content. In attributes, it additionally escapes " and '.
     *
     * @param xmlText The xml text.
     * @return The resulting text with escaped characters.
     */
    public static String escapeForbiddenXmlCharacters(final String xmlText) {

        String result = xmlText;
        if (result != null && PATTERN_ESCAPE_NEEDED.matcher(result).find()) {
            result = PATTERN_AMPERSAND.matcher(result).replaceAll(ESC_AMPERSAND);
            result = PATTERN_LESS_THAN.matcher(result).replaceAll(ESC_LESS_THAN);
            result = PATTERN_GREATER_THAN.matcher(result).replaceAll(ESC_GREATER_THAN);
            result = PATTERN_QUOT.matcher(result).replaceAll(ESC_QUOT);
            result = PATTERN_APOS.matcher(result).replaceAll(ESC_APOS);
        }

        return result;
    }

    /**
     * Replace all escape sequences for forbidden charcters with their readable.
     *
     * @param xmlText     The xml text with escape sequences.
     * @param isAttribute Indicates if this is an attribute.
     * @return The resulting text with unescaped characters.
     */
    public static String unescapeForbiddenXmlCharacters(final String xmlText, final boolean isAttribute) {

        String result = xmlText;
        if (result != null && PATTERN_UNESCAPE_NEEDED.matcher(result).find()) {
            result = PATTERN_ESC_LESS_THAN.matcher(result).replaceAll(LESS_THAN);
            result = PATTERN_ESC_GREATER_THAN.matcher(result).replaceAll(GREATER_THAN);
            result = PATTERN_ESC_QUOT.matcher(result).replaceAll(QUOT);
            result = PATTERN_ESC_APOS.matcher(result).replaceAll(APOS);
            result = PATTERN_ESC_AMPERSAND.matcher(result).replaceAll(AMPERSAND);
        }
        return result;
    }

    /**
     * Throw a uniform escidoc system exception in case of an unexpected exception from the stax parser.
     *
     * @param message A handler specific message added to thrown exception message.
     * @param e       The unexcepcted exception.
     * @throws XmlParserSystemException Thrown in case of an internal system error.
     */
    public static void handleUnexpectedStaxParserException(final String message, final Exception e)
        throws XmlParserSystemException {
        final String text = message != null ? message + e.getMessage() : e.getMessage();
        throw new XmlParserSystemException(text, e);
    }

    /**
     * FIXME Often this method is not used but get(ESCIDOC_CORE_BASEURL) directly. And/or there is no such method for
     * every other property!?
     *
     * @return Return the configured escidoc baseurl.
     * @throws WebserverSystemException If an error occurs accessing the escidoc configuration
     */
    public static String getEscidocBaseUrl() throws WebserverSystemException {
        try {
            return EscidocConfiguration.getInstance().get(EscidocConfiguration.ESCIDOC_CORE_BASEURL);
        }
        catch (final IOException e) {
            throw new WebserverSystemException("Error accessing Escidoc configuration!", e);
        }
    }

    /**
     * Replace the oldPrefix with newPrefix in xml String.
     *
     * @param xml           The xml String.
     * @param currentPrefix The currentPrefix.
     * @param newPrefix     The newPrefix.
     * @return The resulting xml String.
     */
    public static String replaceNamespacePrefix(final String xml, final String currentPrefix, final String newPrefix) {
        String result = xml;
        if (result.contains(currentPrefix)) {
            result = result.replaceAll("xmlns:" + currentPrefix, "xmlns:" + newPrefix);
            result = result.replaceAll('<' + currentPrefix + ':', '<' + newPrefix + ':');
            result = result.replaceAll("</" + currentPrefix + ':', "</" + newPrefix + ':');
        }
        return result;
    }

    /**
     * Gets an initilized <code>XMLOutputFactory2</code> instance.<br/> The returned instance is initialized as follows:
     * <ul> <li>If the provided parameter is set to <code>true</code>, IS_REPAIRING_NAMESPACES is set to true, i.e. the
     * created writers will automatically repair the namespaces, see <code>XMLOutputFactory</code> for details.</li>
     * <li>For writing escaped attribute values, the {@link StaxAttributeEscapingWriterFactory} is used<./li> <li>For
     * writing escaped text content, the {@link StaxTextEscapingWriterFactory} is used.</li> </ul>
     *
     * @param repairing Flag indicating if the factory shall create namespace repairing writers (<code>true</code>) or
     *                  non repairing writers (<code>false</code>).
     * @return Returns the initalized <code>XMLOutputFactory</code> instance.
     */
    private static XMLOutputFactory getInitilizedXmlOutputFactory(final boolean repairing) {

        final XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
        xmlof.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, repairing);
        if (repairing) {
            xmlof.setProperty(XMLOutputFactory2.P_AUTOMATIC_NS_PREFIX, "ext");
        }
        xmlof.setProperty(XMLOutputFactory2.P_ATTR_VALUE_ESCAPER, new StaxAttributeEscapingWriterFactory());
        xmlof.setProperty(XMLOutputFactory2.P_TEXT_ESCAPER, new StaxTextEscapingWriterFactory());
        return xmlof;
    }

    /**
     * Creates an <code>XMLStreamWriter</code> for the provided <code>OutputStream</code>.
     *
     * @param out The <code>OutputStream</code> to get the writer for.
     * @return Returns the <code>XMLStreamWriter</code>.
     * @throws XMLStreamException Thrown in case of an error during creating the writer.
     */
    public static XMLStreamWriter createXmlStreamWriter(final OutputStream out) throws XMLStreamException {

        return getInitilizedXmlOutputFactory(false).createXMLStreamWriter(out);
    }

    /**
     * Creates a namespace repairing <code>XMLStreamWriter</code> for the provided <code>OutputStream</code>.
     *
     * @param out The <code>OutputStream</code> to get the writer for.
     * @return Returns the <code>XMLStreamWriter</code>.
     * @throws XMLStreamException Thrown in case of an error during creating the writer.
     */
    public static XMLStreamWriter createXmlStreamWriterNamespaceRepairing(final OutputStream out)
        throws XMLStreamException {

        return getInitilizedXmlOutputFactory(true).createXMLStreamWriter(out);
    }

    /**
     * Creates an <code>XMLStreamWriter</code> for the provided <code>OutputStream</code>.
     *
     * @param writer The <code>Writer</code> to get the writer for.
     * @return Returns the <code>XMLStreamWriter</code>.
     * @throws XMLStreamException Thrown in case of an error during creating the writer.
     */
    public static XMLStreamWriter createXmlStreamWriter(final Writer writer) throws XMLStreamException {

        return getInitilizedXmlOutputFactory(false).createXMLStreamWriter(writer);
    }

    /**
     * Creates an <code>XMLEventWriter</code> for the provided <code>Writer</code>.
     *
     * @param writer The <code>Writer</code> to get the writer for.
     * @return Returns the <code>XMLEventWriter</code>.
     * @throws XMLStreamException Thrown in case of an error during creating the writer.
     */
    public static XMLEventWriter createXmlEventWriter(final Writer writer) throws XMLStreamException {

        return getInitilizedXmlOutputFactory(false).createXMLEventWriter(writer);
    }

    /**
     * Throws an <code>MissingAttributeValueException</code>.
     *
     * @param element       The element in that the attribute is missing.
     * @param attributeName The name of the missing attribute.
     * @throws MissingAttributeValueException Throws created exception.
     */
    public static void throwMissingAttributeValueException(final AbstractElement element, final String attributeName)
        throws MissingAttributeValueException {

        throw new MissingAttributeValueException(StringUtility.format(ERR_MSG_MISSING_ATTRIBUTE, element.getPath(),
            attributeName, element.getLocationString()));
    }

    /**
     * Get the href to the object with the specified type and id.
     *
     * @param objectType The type of the object. This must be one of <ul> <li>container</li> <li>content-model</li>
     *                   <li>context</li> <li>item</li> <li>component</li> <li>content-relation</li>
     *                   <li>organizational-unit</li> <li>role</li> <li>scope</li> <li>user-account</li> </ul>
     *                   Otherwise, <code>null</code> is returned.
     * @param objectId   The id of the object.
     * @return Returns the href to the specified object or <code>null</code>.
     */
    public static String getHref(final String objectType, final String objectId) {

        String type = null;
        if (objectType != null) {
            type =
                PATTERN_RESOURCE_OBJECT_TYPE.matcher(objectType).find() ? objectType : Constants.RESOURCES_NS_URI
                    + StringUtility.convertToUpperCaseLetterFormat(objectType);
        }

        String objectHref = null;
        if (Constants.CONTAINER_OBJECT_TYPE.equals(type)) {
            objectHref = getContainerHref(objectId);
        }
        else if (Constants.CONTENT_MODEL_OBJECT_TYPE.equals(type)) {
            objectHref = getContentModelHref(objectId);
        }
        else if (Constants.CONTEXT_OBJECT_TYPE.equals(type)) {
            objectHref = getContextHref(objectId);
        }
        else if (Constants.ITEM_OBJECT_TYPE.equals(type)) {
            objectHref = getItemHref(objectId);
        }
        else if (Constants.COMPONENT_OBJECT_TYPE.equals(type)) {
            objectHref = getComponentHref(objectId);
        }
        else if (Constants.CONTENT_RELATION2_OBJECT_TYPE.equals(type)) {
            objectHref = getContentRelationHref(objectId);
        }
        else if (Constants.ORGANIZATIONAL_UNIT_OBJECT_TYPE.equals(type)) {
            objectHref = getOrganizationalUnitHref(objectId);
        }
        else if (Constants.ROLE_OBJECT_TYPE.equals(type)) {
            objectHref = getRoleHref(objectId);
        }
        else if (Constants.SCOPE_OBJECT_TYPE.equals(type)) {
            objectHref = getScopeHref(objectId);
        }
        else if (Constants.USER_ACCOUNT_OBJECT_TYPE.equals(type)) {
            objectHref = getUserAccountHref(objectId);
        }
        else if (Constants.USER_GROUP_OBJECT_TYPE.equals(type)) {
            objectHref = getUserGroupHref(objectId);
        }

        return objectHref;
    }

    /**
     * Create the content of the DC datastream to store in Fedora.
     *
     * @param nsUri       nsUri of the md record. Through this URI is the mapping schema selected.
     * @param mdRecordXml Xml representation of the md record to parse.
     * @param objID       The objid of the Fedora object. A triple is created with this objid.
     * @return The content of the DC datastream or null if content is empty.
     * @throws WebserverSystemException If an error occurs.
     */
    public static String createDC(final String nsUri, final String mdRecordXml, final String objID)
        throws WebserverSystemException {
        return createDC(nsUri, mdRecordXml, objID, null);
    }

    /**
     * Create the content of the DC datastream to store in Fedora.
     *
     * @param nsUri          nsUri of the md record. Through this URI is the mapping schema selected.
     * @param mdRecordXml    Xml representation of the md record to parse.
     * @param objID          The objid of the Fedora object. A triple is created with this objid.
     * @param contentModelID The objid of the content-model.
     * @return The content of the DC datastream or null if content is empty.
     * @throws WebserverSystemException If an error occurs.
     */
    public static String createDC(
        final String nsUri, final String mdRecordXml, final CharSequence objID, final String contentModelID)
        throws WebserverSystemException {

        String result = null;

        Transformer t = null;
        final String transformerKey = nsUri + ';' + contentModelID;
        try {
            t = (Transformer) TRANSFORMER_POOL.borrowObject(transformerKey);
            if (objID != null && objID.length() > 0) {
                t.setParameter("ID", objID);
            }
            else {
                t.clearParameters();
            }
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            t.transform(new StreamSource(new ByteArrayInputStream(mdRecordXml.getBytes(CHARACTER_ENCODING))),
                new StreamResult(out));

            result = out.toString(CHARACTER_ENCODING).trim();
        }
        catch (final Exception e) {
            throw new WebserverSystemException("Mapping of Metadata to DC failed.", e);
        }
        finally {
            try {
                TRANSFORMER_POOL.returnObject(transformerKey, t);
            }
            catch (final Exception e) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Returning transformer to pool failed.");
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Returning transformer to pool failed.", e);
                }
            }
        }

        // check if result is empty
        if (result != null && result.length() == 0) {
            result = null;
        }

        return result;
    }

    /**
     * Calculate the MD 5 Checksum.
     *
     * @param xmlBytes Content over which the checksum is to calculate.
     * @return MD 5 checksum of xmlBytes.
     * @throws ParserConfigurationException Thrown if instance new SAXParser failed.
     * @throws SAXException                 Thrown if pasring failed.
     */
    // TODO create XMLCompareUtility
    public static String getMd5Hash(final byte[] xmlBytes) throws ParserConfigurationException, SAXException {
        if (xmlBytes.length == 0) {
            return "";
        }
        final SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        final SAXParser sp = spf.newSAXParser();
        final XMLHashHandler xhh = new XMLHashHandler();
        try {
            sp.parse(new ByteArrayInputStream(xmlBytes), xhh);
        }
        catch (final IOException e) {
            throw new SAXException("IO Exception.", e);
        }
        return xhh.getHash();
    }

}
