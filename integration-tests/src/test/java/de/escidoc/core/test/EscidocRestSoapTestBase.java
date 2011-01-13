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

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.apache.axis.encoding.Base64;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.xpath.XPathAPI;
import org.joda.time.DateTime;
import org.junit.runners.Parameterized.Parameters;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.aa.UserManagementWrapperClient;
import de.escidoc.core.test.common.logger.AppLogger;
import de.escidoc.core.test.common.resources.PropertiesProvider;
import de.escidoc.core.test.common.resources.ResourceProvider;

/**
 * Base class for tests that are used to test the REST and SOAP interfaces of
 * the eSciDoc core services.
 * 
 * @author TTE
 * 
 */
public class EscidocRestSoapTestBase extends EscidocTestBase {

    @Parameters
    public static Collection<Object[]> getParameters() {
        Collection<Object[]> parameters = new ArrayList<Object[]>();
        parameters.add(new Object[] { Constants.TRANSPORT_REST });
        parameters.add(new Object[] { Constants.TRANSPORT_SOAP });
        return parameters;
    }

    protected static AppLogger log = new AppLogger(
        EscidocRestSoapTestBase.class.getName());

    private static final String XPATH_MODIFIED_BY = "//" + NAME_PROPERTIES
        + "/" + NAME_MODIFIED_BY;

    private static final String XPATH_CREATED_BY = "//" + NAME_PROPERTIES + "/"
        + NAME_CREATED_BY;

    private static final String CONTEXT_XSD = "context.xsd";

    private static final String GRANTS_XSD = "grants.xsd";

    private static final String PREFERENCES_XSD = "preferences.xsd";

    private static final String ATTRIBUTES_XSD = "attributes.xsd";

    private static final String ORGANIZATIONAL_UNIT_REF_LIST_XSD =
        "organizational-unit-ref-list.xsd";

    private static final String ORGANIZATIONAL_UNIT_LIST_XSD =
        "organizational-unit-list.xsd";

    private static final String ORGANIZATIONAL_UNIT_PATH_LIST_XSD =
        "organizational-unit-path-list.xsd";

    private static final String ORGANIZATIONAL_UNIT_XSD =
        "organizational-unit.xsd";

    private static final String CONTENT_MODEL_XSD = "content-model.xsd";

    private static final String REQUESTS_XSD = "requests.xsd";

    private static final String RESULTS_XSD = "results.xsd";

    private static final String ROLE_XSD = "role.xsd";

    private static final String ROLE_LIST_XSD = "role-list.xsd";

    private static final String USER_ACCOUNT_XSD = "user-account.xsd";

    private static final String USER_ACCOUNT_LIST_XSD = "user-account-list.xsd";

    private static final String USER_GROUP_XSD = "user-group.xsd";

    private static final String USER_GROUP_LIST_XSD = "user-group-list.xsd";

    private static final String INDEX_CONFIGURATION_XSD =
        "index-configuration.xsd";

    private static final String CONTAINER_XSD = "container.xsd";

    private static final String CONTAINER_LIST_XSD = "container-list.xsd";

    private static final String VERSION_HISTORY_XSD = "version-history.xsd";

    private static final String TOC_VIEW_XSD = "toc-view.xsd";

    private static final String RELATIONS_XSD = "relations.xsd";

    private static final String MEMBER_LIST_XSD = "member-list.xsd";

    private static final String PARENTS_XSD = "parents.xsd";

    private static final String CONTAINER_REF_LIST_XSD =
        "container-ref-list.xsd";

    public static final String NAME_PARAM = "param";

    public static final String XPATH_PARAM = "/" + NAME_PARAM;

    public static final String XPATH_RESULT = "/" + "result";

    public static final String NAME_PID = "pid";

    public static final String NAME_CONTEXT = "context";

    public static final String XPATH_CONTEXT = "/" + NAME_CONTEXT;

    public static final String XPATH_CONTEXT_RESOURCES = XPATH_CONTEXT + "/"
        + NAME_RESOURCES;

    public static final String XPATH_CONTEXT_ADMIN_DESCRIPTORS = XPATH_CONTEXT
        + "/" + NAME_ADMIN_DESCRIPTORS;

    public static final String XPATH_CONTEXT_ADMIN_DESCRIPTOR =
        XPATH_CONTEXT_ADMIN_DESCRIPTORS + "/" + NAME_ADMIN_DESCRIPTOR;

    /**
     * @deprecated
     */
    @Deprecated
    public static final String XPATH_CONTEXT_ADMIN_DESCRIPTOR_DESCRIPTION =
        XPATH_CONTEXT_ADMIN_DESCRIPTOR + "/" + NAME_DESCRIPTION;

    public static final String XPATH_CONTEXT_ADMIN_DESCRIPTOR_NAME =
        XPATH_CONTEXT_ADMIN_DESCRIPTOR + "[@name]";

    public static final String XPATH_CONTEXT_PROPERTIES = XPATH_CONTEXT + "/"
        + NAME_PROPERTIES;

    public static final String XPATH_CONTEXT_PROPERTIES_ORGANIZATIONAL_UNITS =
        XPATH_CONTEXT_PROPERTIES + "/" + NAME_ORGANIZATIONAL_UNITS;

    public static final String XPATH_CONTEXT_PROPERTIES_ORGANIZATIONAL_UNIT =
        XPATH_CONTEXT_PROPERTIES_ORGANIZATIONAL_UNITS + "/"
            + NAME_ORGANIZATIONAL_UNIT;

    public static final String XPATH_CONTEXT_PROPERTIES_ORGANIZATIONAL_UNIT_OBJID =
        XPATH_CONTEXT_PROPERTIES_ORGANIZATIONAL_UNIT + PART_OBJID;

    public static final String XPATH_CONTEXT_PROPERTIES_ORGANIZATIONAL_UNIT_XLINK_HREF =
        XPATH_CONTEXT_PROPERTIES_ORGANIZATIONAL_UNIT + PART_XLINK_HREF;

    public static final String XPATH_CONTEXT_PROPERTIES_DESCRIPTION =
        XPATH_CONTEXT_PROPERTIES + "/" + NAME_DESCRIPTION;

    public static final String XPATH_CONTEXT_PROPERTIES_NAME =
        XPATH_CONTEXT_PROPERTIES + "/" + NAME_NAME;

    public static final String XPATH_CONTEXT_NAME = "";

    public static final String NAME_REPORT_DEFINITION = "report-definition";

    public static final String NAME_REPORT_PARAMETERS = "report-parameters";

    public static final String XPATH_REPORT_PARAMETERS = "/"
        + NAME_REPORT_PARAMETERS;

    public static final String XPATH_REPORT_PARAMETERS_REPORT_DEFINITION =
        XPATH_REPORT_PARAMETERS + "/" + NAME_REPORT_DEFINITION;

    public static final String XPATH_REPORT_PARAMETERS_REPORT_DEFINITION_OBJID =
        XPATH_REPORT_PARAMETERS_REPORT_DEFINITION + PART_OBJID;

    private static String BASE_URL_SCHEMA_LOCATION = null;

    public static final String NAME_ASSIGNED_ON = "assigned-on";

    public static final String NAME_RDF = "RDF";

    public static final String XPATH_RDF = "/" + NAME_RDF;

    public static final String XPATH_RDF_DESCRIPTION = XPATH_RDF
        + "/Description";

    public static final String FILTER_PARAMETER_EXPLAIN = "explain";

    public static final String FILTER_PARAMETER_QUERY = "query";

    public static final String FILTER_PARAMETER_MAXIMUMRECORDS =
        "maximumRecords";

    public static final String FILTER_PARAMETER_STARTRECORD = "startRecord";

    public static final String FILTER_IDENTIFIER = "/id";

    public static final String FILTER_URI_IDENTIFIER =
        "http://purl.org/dc/elements/1.1/identifier";

    public static final String NAME_EMAIL = "email";

    public static final String FILTER_URI_EMAIL = PROPERTIES_NS_URI_04 + NAME_EMAIL;

    public static final String FILTER_EMAIL = PROPERTIES_FILTER_PREFIX + NAME_EMAIL;

    public static final String NAME_LOGIN_NAME = "login-name";

    public static final String FILTER_URI_LOGIN_NAME = PROPERTIES_NS_URI_04
        + NAME_LOGIN_NAME;

    public static final String FILTER_LOGIN_NAME = PROPERTIES_FILTER_PREFIX
    + NAME_LOGIN_NAME;

    public static final String NAME_LABEL = "label";

    public static final String FILTER_URI_LABEL = PROPERTIES_NS_URI_04 + NAME_LABEL;

    public static final String FILTER_LABEL = PROPERTIES_FILTER_PREFIX + NAME_LABEL;

    public static final String FILTER_URI_SET_SPECIFICATION =
        "http://www.escidoc.de/schemas/setdefinition/0.1/specification";

    public static final String FILTER_URI_NAME = PROPERTIES_NS_URI_04 + NAME_NAME;

    public static final String FILTER_NAME = PROPERTIES_FILTER_PREFIX + NAME_NAME;

    public static final String FILTER_URI_ORGANIZATIONAL_UNIT =
        STRUCTURAL_RELATIONS_NS_URI + NAME_ORGANIZATIONAL_UNIT;

    public static final String FILTER_ORGANIZATIONAL_UNIT =
        STRUCTURAL_RELATIONS_FILTER_PREFIX + NAME_ORGANIZATIONAL_UNIT;

    public static final String NAME_ACTIVE = "active";

    public static final String FILTER_URI_ACTIVE = PROPERTIES_NS_URI_04
        + NAME_ACTIVE;

    public static final String FILTER_ACTIVE = PROPERTIES_FILTER_PREFIX
    + NAME_ACTIVE;

    public static final String FILTER_URI_CONTEXT = PROPERTIES_NS_URI_04
        + NAME_CONTEXT;

    public static final String FILTER_CONTEXT = PROPERTIES_FILTER_PREFIX
    + NAME_CONTEXT;

    public static final String FILTER_PRIMARY_AFFILIATION =
        "primary-affiliation";

    public static final String FILTER_URI_CONTENT_MODEL =
        STRUCTURAL_RELATIONS_NS_URI + NAME_CONTENT_MODEL;

    public static final String FILTER_URI_VERSION_STATUS = PROPERTIES_NS_URI_04
        + "version/status";

    public static final String FILTER_URI_PUBLIC_STATUS = PROPERTIES_NS_URI_04
        + NAME_PUBLIC_STATUS;

    public static final String FILTER_URI_USER = PROPERTIES_NS_URI_04 + "user";

    public static final String FILTER_USER = PROPERTIES_FILTER_PREFIX + "user/id";

    public static final String FILTER_USER_GROUP_USER = 
        STRUCTURAL_RELATIONS_FILTER_PREFIX + "user/id";

    public static final String FILTER_URI_GROUP = PROPERTIES_NS_URI_04 + "group";

    public static final String FILTER_GROUP = PROPERTIES_FILTER_PREFIX + "group/id";

    public static final String FILTER_USER_ACCOUNT_GROUP = 
        STRUCTURAL_RELATIONS_FILTER_PREFIX + "group/id";

    public static final String FILTER_URI_ROLE = PROPERTIES_NS_URI_04 + "role";

    public static final String FILTER_ROLE = PROPERTIES_FILTER_PREFIX + "role/id";

    public static final String FILTER_URI_ASSIGNED_ON = PROPERTIES_NS_URI_04
        + "assigned-on";

    public static final String FILTER_ASSIGNED_ON = PROPERTIES_FILTER_PREFIX
    + "assigned-on/id";

    public static final String FILTER_URI_REVOCATION_DATE = PROPERTIES_NS_URI_04
        + "revocation-date";

    public static final String FILTER_REVOCATION_DATE = PROPERTIES_FILTER_PREFIX
    + "revocation-date";

    public static final String FILTER_URI_CREATION_DATE = PROPERTIES_NS_URI_04
        + "creation-date";

    public static final String FILTER_CREATION_DATE = PROPERTIES_FILTER_PREFIX
    + "creation-date";

    public static final String FILTER_URI_GRANTED_FROM = PROPERTIES_NS_URI_04
        + "granted-from";

    public static final String FILTER_GRANTED_FROM = PROPERTIES_FILTER_PREFIX
    + "granted-from";

    public static final String FILTER_URI_GRANTED_TO = PROPERTIES_NS_URI_04
        + "granted-to";

    public static final String FILTER_GRANTED_TO = PROPERTIES_FILTER_PREFIX
    + "granted-to";

    public static final String FILTER_URI_CREATED_BY = PROPERTIES_NS_URI_04
        + "created-by";

    public static final String FILTER_CREATED_BY = PROPERTIES_FILTER_PREFIX
    + "created-by/id";

    public static final String FILTER_URI_REVOKED_BY = PROPERTIES_NS_URI_04
        + "revoked-by";

    public static final String FILTER_REVOKED_BY = PROPERTIES_FILTER_PREFIX
    + "revoked-by/id";

    public static final String FILTER_TOP_LEVEL_OUS_ONLY =
        "top-level-organizational-units";

    public static final String FILTER_URI_TYPE = PROPERTIES_NS_URI_04 + NAME_TYPE;

    public static final String XPATH_ORGANIZATIONAL_UNIT = "/"
        + NAME_ORGANIZATIONAL_UNIT;

    public static final String XPATH_ORGANIZATION_MD_RECORDS = "/"
        + NAME_MD_RECORDS;

    public static final String XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS =
        XPATH_ORGANIZATIONAL_UNIT + XPATH_ORGANIZATION_MD_RECORDS;

    public static final String XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS_XLINK_HREF =
        XPATH_ORGANIZATIONAL_UNIT + XPATH_ORGANIZATION_MD_RECORDS
            + PART_XLINK_HREF;

    public static final String XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS_XLINK_TITLE =
        XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS + PART_XLINK_TITLE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS_XLINK_TYPE =
        XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS + PART_XLINK_TYPE;

    public static final String NAME_OU_MD_RECORD = "ou";

    public static final String XPATH_ORGANIZATIONAL_UNIT_ESCIDOC_MD_RECORD =
        XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS + "/" + NAME_MD_RECORD
            + "[@name=\"escidoc\"]" + "/" + NAME_OU_MD_RECORD;

    public static final String XPATH_ORGANIZATIONAL_UNIT_TITLE =
        XPATH_ORGANIZATIONAL_UNIT_ESCIDOC_MD_RECORD + "/" + NAME_TITLE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_DESCRIPTION =
        XPATH_ORGANIZATIONAL_UNIT_ESCIDOC_MD_RECORD + "/" + NAME_DESCRIPTION;

    public static final String XPATH_ORGANIZATIONAL_UNIT_PARENTS =
        XPATH_ORGANIZATIONAL_UNIT + "/" + NAME_PARENTS;

    public static final String XPATH_PARENTS = "/" + NAME_PARENTS;

    public static final String XPATH_ORGANIZATIONAL_UNIT_PARENT =
        XPATH_ORGANIZATIONAL_UNIT_PARENTS + "/" + NAME_PARENT;

    public static final String XPATH_ORGANIZATIONAL_UNIT_PARENT_XLINK_HREF =
        XPATH_ORGANIZATIONAL_UNIT_PARENT + PART_XLINK_HREF;

    public static final String XPATH_ORGANIZATIONAL_UNIT_PARENT_XLINK_TITLE =
        XPATH_ORGANIZATIONAL_UNIT_PARENT + PART_XLINK_TITLE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_PARENT_XLINK_TYPE =
        XPATH_ORGANIZATIONAL_UNIT_PARENT + PART_XLINK_TYPE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_PARENT_OBJID =
        XPATH_ORGANIZATIONAL_UNIT_PARENT + PART_OBJID;

    public static final String XPATH_ORGANIZATIONAL_UNIT_PARENTS_XLINK_HREF =
        XPATH_ORGANIZATIONAL_UNIT_PARENTS + PART_XLINK_HREF;

    public static final String XPATH_ORGANIZATIONAL_UNIT_PARENTS_XLINK_TITLE =
        XPATH_ORGANIZATIONAL_UNIT_PARENTS + PART_XLINK_TITLE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_PARENTS_XLINK_TYPE =
        XPATH_ORGANIZATIONAL_UNIT_PARENTS + PART_XLINK_TYPE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_PROPERTIES =
        XPATH_ORGANIZATIONAL_UNIT + "/" + NAME_PROPERTIES;

    public static final String XPATH_ORGANIZATIONAL_UNIT_PROPERTIES_XLINK_TYPE =
        XPATH_ORGANIZATIONAL_UNIT_PROPERTIES + PART_XLINK_TYPE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_PROPERTIES_XLINK_TITLE =
        XPATH_ORGANIZATIONAL_UNIT_PROPERTIES + PART_XLINK_TITLE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_PROPERTIES_XLINK_HREF =
        XPATH_ORGANIZATIONAL_UNIT_PROPERTIES + PART_XLINK_HREF;

    public static final String XPATH_ORGANIZATIONAL_UNIT_REGION =
        XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS + "/" + NAME_REGION;

    public static final String XPATH_ORGANIZATIONAL_UNIT_TELEPHONE =
        XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS + "/" + NAME_TELEPHONE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_ORGANIZATION_TYPE =
        XPATH_ORGANIZATIONAL_UNIT_ESCIDOC_MD_RECORD + "/"
            + NAME_ORGANIZATION_TYPE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_URI =
        XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS + "/" + NAME_URI;

    public static final String XPATH_ORGANIZATIONAL_UNIT_CREATION_DATE =
        XPATH_ORGANIZATIONAL_UNIT_PROPERTIES + "/" + NAME_CREATION_DATE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_CREATED_BY =
        XPATH_ORGANIZATIONAL_UNIT_PROPERTIES + "/" + NAME_CREATED_BY;

    public static final String XPATH_ORGANIZATIONAL_UNIT_CREATED_BY_XLINK_TYPE =
        XPATH_ORGANIZATIONAL_UNIT_CREATED_BY + PART_XLINK_TYPE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_CREATED_BY_XLINK_TITLE =
        XPATH_ORGANIZATIONAL_UNIT_CREATED_BY + PART_XLINK_TITLE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_CREATED_BY_XLINK_HREF =
        XPATH_ORGANIZATIONAL_UNIT_CREATED_BY + PART_XLINK_HREF;

    public static final String XPATH_ORGANIZATIONAL_UNIT_CREATED_BY_OBJID =
        XPATH_ORGANIZATIONAL_UNIT_CREATED_BY + PART_OBJID;

    public static final String XPATH_ORGANIZATIONAL_UNIT_MODIFIED_BY =
        XPATH_ORGANIZATIONAL_UNIT_PROPERTIES + "/" + NAME_MODIFIED_BY;

    public static final String XPATH_ORGANIZATIONAL_UNIT_MODIFIED_BY_OBJID =
        XPATH_ORGANIZATIONAL_UNIT_MODIFIED_BY + PART_OBJID;

    public static final String XPATH_ORGANIZATIONAL_UNIT_MODIFIED_BY_XLINK_HREF =
        XPATH_ORGANIZATIONAL_UNIT_MODIFIED_BY + PART_XLINK_HREF;

    public static final String XPATH_ORGANIZATIONAL_UNIT_MODIFIED_BY_XLINK_TITLE =
        XPATH_ORGANIZATIONAL_UNIT_MODIFIED_BY + PART_XLINK_TITLE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_MODIFIED_BY_XLINK_TYPE =
        XPATH_ORGANIZATIONAL_UNIT_MODIFIED_BY + PART_XLINK_TYPE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_OBJID =
        XPATH_ORGANIZATIONAL_UNIT + PART_OBJID;

    public static final String XPATH_ORGANIZATIONAL_UNIT_PUBLIC_STATUS =
        XPATH_ORGANIZATIONAL_UNIT_PROPERTIES + "/" + NAME_PUBLIC_STATUS;

    public static final String XPATH_ORGANIZATIONAL_UNIT_PUBLIC_STATUS_COMMENT =
        XPATH_ORGANIZATIONAL_UNIT_PROPERTIES + "/" + NAME_PUBLIC_STATUS_COMMENT;

    public static final String XPATH_ORGANIZATIONAL_UNIT_RESOURCES =
        XPATH_ORGANIZATIONAL_UNIT + "/" + NAME_RESOURCES;

    public static final String XPATH_ORGANIZATIONAL_UNIT_RESOURCES_XLINK_HREF =
        XPATH_ORGANIZATIONAL_UNIT_RESOURCES + PART_XLINK_HREF;

    public static final String XPATH_ORGANIZATIONAL_UNIT_RESOURCES_XLINK_TITLE =
        XPATH_ORGANIZATIONAL_UNIT_RESOURCES + PART_XLINK_TITLE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_RESOURCES_XLINK_TYPE =
        XPATH_ORGANIZATIONAL_UNIT_RESOURCES + PART_XLINK_TYPE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_XLINK_HREF =
        XPATH_ORGANIZATIONAL_UNIT + PART_XLINK_HREF;

    public static final String XPATH_ORGANIZATIONAL_UNIT_XLINK_TITLE =
        XPATH_ORGANIZATIONAL_UNIT + PART_XLINK_TITLE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_XLINK_TYPE =
        XPATH_ORGANIZATIONAL_UNIT + PART_XLINK_TYPE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_XML_BASE =
        XPATH_ORGANIZATIONAL_UNIT + PART_XML_BASE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_HAS_CHILDREN =
        XPATH_ORGANIZATIONAL_UNIT_PROPERTIES + "/" + NAME_HAS_CHILDREN;

    public static final String XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PATH_LIST =
        XPATH_ORGANIZATIONAL_UNIT_RESOURCES + "/" + "path-list";

    public static final String XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PATH_LIST_XLINK_HREF =
        XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PATH_LIST + PART_XLINK_HREF;

    public static final String XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PATH_LIST_XLINK_TITLE =
        XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PATH_LIST + PART_XLINK_TITLE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PATH_LIST_XLINK_TYPE =
        XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PATH_LIST + PART_XLINK_TYPE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PARENT_OBJECTS =
        XPATH_ORGANIZATIONAL_UNIT_RESOURCES + "/" + "parent-objects";

    public static final String XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PARENT_OBJECTS_XLINK_HREF =
        XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PARENT_OBJECTS + PART_XLINK_HREF;

    public static final String XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PARENT_OBJECTS_XLINK_TITLE =
        XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PARENT_OBJECTS + PART_XLINK_TITLE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PARENT_OBJECTS_XLINK_TYPE =
        XPATH_ORGANIZATIONAL_UNIT_RESOURCE_PARENT_OBJECTS + PART_XLINK_TYPE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_RESOURCE_CHILD_OBJECTS =
        XPATH_ORGANIZATIONAL_UNIT_RESOURCES + "/" + "child-objects";

    public static final String XPATH_ORGANIZATIONAL_UNIT_RESOURCE_CHILD_OBJECTS_XLINK_HREF =
        XPATH_ORGANIZATIONAL_UNIT_RESOURCE_CHILD_OBJECTS + PART_XLINK_HREF;

    public static final String XPATH_ORGANIZATIONAL_UNIT_RESOURCE_CHILD_OBJECTS_XLINK_TITLE =
        XPATH_ORGANIZATIONAL_UNIT_RESOURCE_CHILD_OBJECTS + PART_XLINK_TITLE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_RESOURCE_CHILD_OBJECTS_XLINK_TYPE =
        XPATH_ORGANIZATIONAL_UNIT_RESOURCE_CHILD_OBJECTS + PART_XLINK_TYPE;

    public static final String NAME_ORGANIZATIONAL_UNIT_REF =
        "organizational-unit-ref";

    public static final String NAME_ORGANIZATIONAL_UNIT_REF_LIST =
        "organizational-unit-ref-list";

    public static final String XPATH_ORGANIZATIONAL_UNIT_REF_LIST = "/"
        + NAME_ORGANIZATIONAL_UNIT_REF_LIST;

    public static final String XPATH_ORGANIZATIONAL_UNIT_REF_LIST_ORGANIZATIONAL_UNIT_REF =
        XPATH_ORGANIZATIONAL_UNIT_REF_LIST + "/" + NAME_ORGANIZATIONAL_UNIT_REF;

    public static final String NAME_ORGANIZATIONAL_UNIT_LIST =
        "organizational-unit-list";

    public static final String XPATH_ORGANIZATIONAL_UNIT_LIST = "/"
        + NAME_ORGANIZATIONAL_UNIT_LIST;

    public static final String XPATH_ORGANIZATIONAL_UNIT_LIST_ORGANIZATIONAL_UNIT =
        XPATH_ORGANIZATIONAL_UNIT_LIST + "/" + NAME_ORGANIZATIONAL_UNIT;

    public static final String XPATH_ORGANIZATIONAL_UNIT_ALTERNATIVE =
        XPATH_ORGANIZATIONAL_UNIT_ESCIDOC_MD_RECORD + "/" + NAME_ALTERNATIVE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_START_DATE =
        XPATH_ORGANIZATIONAL_UNIT_ESCIDOC_MD_RECORD + "/" + NAME_START_DATE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_END_DATE =
        XPATH_ORGANIZATIONAL_UNIT_ESCIDOC_MD_RECORD + "/" + NAME_END_DATE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_CITY =
        XPATH_ORGANIZATIONAL_UNIT_ESCIDOC_MD_RECORD + "/" + NAME_CITY;

    public static final String XPATH_ORGANIZATIONAL_UNIT_COUNTRY =
        XPATH_ORGANIZATIONAL_UNIT_ESCIDOC_MD_RECORD + "/" + NAME_COUNTRY;

    public static final String XPATH_ORGANIZATIONAL_UNIT_IDENTIFIER =
        XPATH_ORGANIZATIONAL_UNIT_ESCIDOC_MD_RECORD + "/" + NAME_IDENTIFIER;

    public static final String XPATH_ORGANIZATIONAL_UNIT_FAX =
        XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS + "/" + NAME_FAX;

    public static final String XPATH_ORGANIZATIONAL_UNIT_GEO_COORDINATE =
        XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS + "/" + NAME_GEO_COORDINATE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_GEO_COORDINATE_LATITUDE =
        XPATH_ORGANIZATIONAL_UNIT_GEO_COORDINATE + "/" + NAME_LOCATION_LATITUDE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_GEO_COORDINATE_LONGITUDE =
        XPATH_ORGANIZATIONAL_UNIT_GEO_COORDINATE + "/"
            + NAME_LOCATION_LONGITUDE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_POSTCODE =
        XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS + "/" + NAME_POSTCODE;

    public static final String XPATH_ORGANIZATIONAL_UNIT_LAST_MODIFICATION_DATE =
        XPATH_ORGANIZATIONAL_UNIT + PART_LAST_MODIFICATION_DATE;

    private static final Pattern PATTERN_VERSION_NUMBER = Pattern
        .compile("[a-zA-Z]+:[a-zA-Z0-9]+:([0-9]+)");

    /**
     * The constructor.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * 
     */
    public EscidocRestSoapTestBase(final int transport) {

        super(transport);
    }

    /**
     * Creates a new element node for the provided document. The created element
     * is an element that refers to another resource, i.e. it has xlink
     * attributes in case of REST or objid attribute in case of SOAP.<br/>
     * This method takes the set transport definition for this class to decide
     * which attributes have to be sent.<br>
     * This method delegates to
     * <code>createReferencingElementNode(doc, namespaceUri, prefix, tagName,
            xlinkPrefix, title, href, false)</code>. I.e., in case of rest access,
     * the read only values of the referencing node are not created.
     * 
     * @param doc
     *            The document for that the node shall be created.
     * @param namespaceUri
     *            The name space uri of the node to create. This may be null.
     * @param prefix
     *            The prefix to use.
     * @param tagName
     *            The tag name of the node.
     * @param xlinkPrefix
     *            The prefix to use for the xlink attributes.
     * @param title
     *            The title of the referencing element (=xlink:title)
     * @param href
     *            The href of the referencing element (=xlink:href). The objid
     *            attribute value is extracted from this href.
     * @return Returns the created node.
     * @throws Exception
     *             Thrown if anything fails.
     */
    @Override
    public Element createReferencingElementNode(
        final Document doc, final String namespaceUri, final String prefix,
        final String tagName, final String xlinkPrefix, final String title,
        final String href) throws Exception {

        return createReferencingElementNode(doc, namespaceUri, prefix, tagName,
            xlinkPrefix, title, href, false);
    }

    /**
     * Creates a new element node for the provided document. The created element
     * is an element that refers to another resource, i.e. it has xlink
     * attributes in case of REST or objid attribute in case of SOAP.<br/>
     * This method takes the set transport definition for this class to decide
     * which attributes have to be sent.
     * 
     * @param doc
     *            The document for that the node shall be created.
     * @param namespaceUri
     *            The name space uri of the node to create. This may be null.
     * @param prefix
     *            The prefix to use.
     * @param tagName
     *            The tag name of the node.
     * @param xlinkPrefix
     *            The prefix to use for the xlink attributes.
     * @param title
     *            The title of the referencing element (=xlink:title)
     * @param href
     *            The href of the referencing element (=xlink:href). The objid
     *            attribute value is extracted from this href.
     * @param withRestReadOnly
     *            Flag indicating if the parent-ous element shall contain the
     *            REST specific read only attributes.
     * @return Returns the created node.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public Element createReferencingElementNode(
        final Document doc, final String namespaceUri, final String prefix,
        final String tagName, final String xlinkPrefix, final String title,
        final String href, final boolean withRestReadOnly) throws Exception {

        return createReferencingElementNode(getTransport(), doc, namespaceUri,
            prefix, tagName, xlinkPrefix, title, href, withRestReadOnly);
    }

    /**
     * Creates a new element node for the provided document. The created element
     * is an element that refers to another resource, i.e. it has xlink
     * attributes in case of REST or objid attribute in case of SOAP.<br/>
     * This method takes the provided transport definition to decide which
     * attributes have to be sent.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param doc
     *            The document for that the node shall be created.
     * @param namespaceUri
     *            The name space uri of the node to create. This may be null.
     * @param prefix
     *            The prefix to use.
     * @param tagName
     *            The tag name of the node.
     * @param xlinkPrefix
     *            The prefix to use for the xlink attributes.
     * @param title
     *            The title of the referencing element (=xlink:title)
     * @param href
     *            The href of the referencing element (=xlink:href). The objid
     *            attribute value is extracted from this href.
     * @param withRestReadOnly
     *            Flag indicating if the parent-ous element shall contain the
     *            REST specific read only attributes.
     * @return Returns the created node.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public static Element createReferencingElementNode(
        final int transport, final Document doc, final String namespaceUri,
        final String prefix, final String tagName, final String xlinkPrefix,
        final String title, final String href, final boolean withRestReadOnly)
        throws Exception {

        assertTransport(transport);

        Element newElement;
        if (transport == Constants.TRANSPORT_REST) {
            newElement =
                createElementNodeWithXlink(doc, namespaceUri, prefix, tagName,
                    xlinkPrefix, title, href, withRestReadOnly);
        }
        else {
            newElement =
                createElementNode(doc, namespaceUri, prefix, tagName, null);
            Attr objidAttr =
                createAttributeNode(doc, null, null, NAME_OBJID,
                    getObjidFromHref(href));
            newElement.getAttributes().setNamedItemNS(objidAttr);
        }

        return newElement;
    }

    /**
     * Creates a new element node for the provided document. The created element
     * is an element that that has xlink attributes, but does not have an objid
     * attribute.
     * 
     * @param doc
     *            The document for that the node shall be created.
     * @param namespaceUri
     *            The name space uri of the node to create. This may be null.
     * @param prefix
     *            The prefix to use.
     * @param tagName
     *            The tag name of the node.
     * @param xlinkPrefix
     *            The prefix to use for the xlink attributes.
     * @param title
     *            The title of the referencing element (=xlink:title)
     * @param href
     *            The href of the referencing element (=xlink:href).
     * @param withRestReadOnly
     *            Flag indicating if the parent-ous element shall contain the
     *            REST specific read only attributes (type and title).
     * @return Returns the created node.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public static Element createElementNodeWithXlink(
        final Document doc, final String namespaceUri, final String prefix,
        final String tagName, final String xlinkPrefix, final String title,
        final String href, final boolean withRestReadOnly) throws Exception {

        Element newElement =
            createElementNode(doc, namespaceUri, prefix, tagName, null);
        if (withRestReadOnly) {
            Attr xlinkTypeAttr =
                createAttributeNode(doc, XLINK_NS_URI, xlinkPrefix, NAME_TYPE,
                    "simple");
            Attr xlinkTitleAttr =
                createAttributeNode(doc, XLINK_NS_URI, xlinkPrefix, NAME_TITLE,
                    title);
            newElement.getAttributes().setNamedItemNS(xlinkTypeAttr);
            newElement.getAttributes().setNamedItemNS(xlinkTitleAttr);
        }
        Attr xlinkHrefAttr =
            createAttributeNode(doc, XLINK_NS_URI, xlinkPrefix, NAME_HREF, href);
        newElement.getAttributes().setNamedItemNS(xlinkHrefAttr);

        return newElement;
    }

    /**
     * Asserts that the provided xml data is a valid for grants schema.<br/>
     * This method takes the set transport definition for this instance to
     * decide if the provided xml data has to be validated using the REST schema
     * or the SOAP schema.
     * 
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidGrants(final String xmlData) throws Exception {

        assertXmlValidGrants(getTransport(), xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid for grants schema.<br/>
     * This method takes the provided transport definition to decide if the
     * provided xml data has to be validated using the REST schema or the SOAP
     * schema.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidGrants(final int transport, final String xmlData)
        throws Exception {

        assertXmlValid(xmlData, new URL(getGrantsSchemaLocation(transport)));
        assertAllPlaceholderResolved(xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid for preferences schema.<br/>
     * This method takes the set transport definition for this instance to
     * decide if the provided xml data has to be validated using the REST schema
     * or the SOAP schema.
     * 
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidPreferences(final String xmlData)
        throws Exception {

        assertXmlValidPreferences(getTransport(), xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid for preferences schema.<br/>
     * This method takes the provided transport definition to decide if the
     * provided xml data has to be validated using the REST schema or the SOAP
     * schema.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidPreferences(
        final int transport, final String xmlData) throws Exception {

        assertXmlValid(xmlData,
            new URL(getPreferencesSchemaLocation(transport)));
        assertAllPlaceholderResolved(xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid for attributes schema.<br/>
     * This method takes the set transport definition for this instance to
     * decide if the provided xml data has to be validated using the REST schema
     * or the SOAP schema.
     * 
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidAttributes(final String xmlData) throws Exception {

        assertXmlValidAttributes(getTransport(), xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid for attributes schema.<br/>
     * This method takes the provided transport definition to decide if the
     * provided xml data has to be validated using the REST schema or the SOAP
     * schema.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidAttributes(
        final int transport, final String xmlData) throws Exception {

        assertXmlValid(xmlData, new URL(getAttributesSchemaLocation(transport)));
        assertAllPlaceholderResolved(xmlData);
    }

    /**
     * Checks if the provided transport is valid.
     * 
     * @param transport
     *            Specifies the transport to be asserted, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}. Otherwise, the assertion
     *            fails.
     */
    public static void assertTransport(final int transport) {

        if (transport != Constants.TRANSPORT_REST
            && transport != Constants.TRANSPORT_SOAP) {
            fail("Invalid value for transport, was " + transport);
        }
    }

    public void assertXmlValidContext(final String xmlData) throws Exception {

        assertXmlValidContext(getTransport(), xmlData);
    }

    public void assertXmlValidContext(final int transport, final String xmlData)
        throws Exception {

        assertXmlValid(xmlData, new URL(getContextSchemaBase(transport)
            + CONTEXT_XSD));
        assertAllPlaceholderResolved(xmlData);
    }

    public void assertXmlValidContextsList(final String xmlData)
        throws Exception {
        assertXmlValidContextsList(getTransport(), xmlData);
    }

    public void assertXmlValidContextsList(
        final int transport, final String xmlData) throws Exception {
        assertXmlValid(xmlData, new URL(getContextSchemaBase(transport)
            + "context-list.xsd"));
        assertAllPlaceholderResolved(xmlData);
    }

    public void assertXmlValidContextRefsList(final String xmlData)
        throws Exception {
        assertXmlValidContextRefsList(getTransport(), xmlData);
    }

    public void assertXmlValidContextRefsList(
        final int transport, final String xmlData) throws Exception {
        assertXmlValid(xmlData, new URL(getContextSchemaBase(transport)
            + "context-ref-list.xsd"));
        assertAllPlaceholderResolved(xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid content model.<br/>
     * This method takes the set transport definition for this instance to
     * decide if the provided xml data has to be validated using the REST schema
     * or the SOAP schema.
     * 
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidContentModel(final String xmlData)
        throws Exception {

        assertXmlValidContentModel(getTransport(), xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid organizational unit.<br/>
     * This method takes the provided transport definition to decide if the
     * provided xml data has to be validated using the REST schema or the SOAP
     * schema.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidContentModel(
        final int transport, final String xmlData) throws Exception {

        assertXmlValid(xmlData,
            new URL(getContentModelSchemaBase(transport, "0.1")
                + CONTENT_MODEL_XSD));
    }

    /**
     * Asserts that the provided xml data is a valid organizational unit.<br/>
     * This method takes the set transport definition for this instance to
     * decide if the provided xml data has to be validated using the REST schema
     * or the SOAP schema.
     * 
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidOrganizationalUnit(final String xmlData)
        throws Exception {

        assertXmlValidOrganizationalUnit(getTransport(), xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid organizational unit.<br/>
     * This method takes the provided transport definition to decide if the
     * provided xml data has to be validated using the REST schema or the SOAP
     * schema.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidOrganizationalUnit(
        final int transport, final String xmlData) throws Exception {

        assertXmlValid(xmlData,
            new URL(getOrganizationalUnitSchemaBase(transport, "0.8")
                + ORGANIZATIONAL_UNIT_XSD));
        assertAllPlaceholderResolved(xmlData);
    }

    /**
     * Asserts that the provided xml data is valid for authorization requests.
     * 
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidRequests(final String xmlData) throws Exception {

        assertXmlValidRequests(getTransport(), xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid role.<br/>
     * This method takes the provided transport definition to decide if the
     * provided xml data has to be validated using the REST schema or the SOAP
     * schema.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidRequests(final int transport, final String xmlData)
        throws Exception {

        assertXmlValid(xmlData, new URL(getRequestsSchemaBase(transport)
            + REQUESTS_XSD));
        assertAllPlaceholderResolved(xmlData);
    }

    /**
     * Asserts that the provided xml data is valid for authorization responses.
     * 
     * @param toBeAsserted
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidResults(final String toBeAsserted)
        throws Exception {

        assertXmlValidResults(getTransport(), toBeAsserted);
    }

    /**
     * Asserts that the provided xml data is valid for authorization responses.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param toBeAsserted
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidResults(
        final int transport, final String toBeAsserted) throws Exception {

        assertXmlValid(toBeAsserted, new URL(getResultsSchemaBase(transport)
            + RESULTS_XSD));
        assertAllPlaceholderResolved(toBeAsserted);
    }

    /**
     * Asserts that the provided xml data is a valid role.<br/>
     * This method takes the set transport definition for this instance to
     * decide if the provided xml data has to be validated using the REST schema
     * or the SOAP schema.
     * 
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidRole(final String xmlData) throws Exception {

        assertXmlValidRole(getTransport(), xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid role.<br/>
     * This method takes the provided transport definition to decide if the
     * provided xml data has to be validated using the REST schema or the SOAP
     * schema.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidRole(final int transport, final String xmlData)
        throws Exception {

        assertXmlValid(xmlData,
            new URL(getRoleSchemaBase(transport) + ROLE_XSD));
        assertAllPlaceholderResolved(xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid list of roles.<br/>
     * This method takes the set transport definition for this instance to
     * decide if the provided xml data has to be validated using the REST schema
     * or the SOAP schema.
     * 
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidRoleList(final String xmlData) throws Exception {

        assertXmlValidRoleList(getTransport(), xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid list of roles.<br/>
     * This method takes the provided transport definition to decide if the
     * provided xml data has to be validated using the REST schema or the SOAP
     * schema.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidRoleList(final int transport, final String xmlData)
        throws Exception {

        assertXmlValid(xmlData, new URL(getRoleListSchemaBase(transport)
            + ROLE_LIST_XSD));
        assertAllPlaceholderResolved(xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid user account.<br/>
     * This method takes the set transport definition for this instance to
     * decide if the provided xml data has to be validated using the REST schema
     * or the SOAP schema.
     * 
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidUserAccount(final String xmlData)
        throws Exception {

        assertXmlValidUserAccount(getTransport(), xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid user attribute.<br/>
     * This method takes the set transport definition for this instance to
     * decide if the provided xml data has to be validated using the REST schema
     * or the SOAP schema.
     * 
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidUserAttribute(final String xmlData)
        throws Exception {

        assertXmlValidUserAttribute(getTransport(), xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid list of user accounts.<br/>
     * This method takes the set transport definition for this instance to
     * decide if the provided xml data has to be validated using the REST schema
     * or the SOAP schema.
     * 
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidUserAccountList(final String xmlData)
        throws Exception {

        assertXmlValidUserAccountList(getTransport(), xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid user account.<br/>
     * This method takes the set transport definition for this instance to
     * decide if the provided xml data has to be validated using the REST schema
     * or the SOAP schema.
     * 
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidUserGroup(final String xmlData) throws Exception {

        assertXmlValidUserGroup(getTransport(), xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid list of user groups.<br/>
     * This method takes the set transport definition for this instance to
     * decide if the provided xml data has to be validated using the REST schema
     * or the SOAP schema.
     * 
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidUserGroupList(final String xmlData)
        throws Exception {

        assertXmlValidUserGroupList(getTransport(), xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid list of grants.<br/>
     * This method takes the set transport definition for this instance to
     * decide if the provided xml data has to be validated using the REST schema
     * or the SOAP schema.
     * 
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidGrantList(final String xmlData) throws Exception {

        assertXmlValidGrantList(getTransport(), xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid index configuration.<br/>
     * This method takes the set transport definition for this instance to
     * decide if the provided xml data has to be validated using the REST schema
     * or the SOAP schema.
     * 
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidIndexConfiguration(final String xmlData)
        throws Exception {

        assertXmlValidIndexConfiguration(getTransport(), xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid index configuration.<br/>
     * This method takes the provided transport definition to decide if the
     * provided xml data has to be validated using the REST schema or the SOAP
     * schema.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidIndexConfiguration(
        final int transport, final String xmlData) throws Exception {

        assertXmlValid(xmlData, new URL(getAdminSchemaBase(transport)
            + INDEX_CONFIGURATION_XSD));
        assertAllPlaceholderResolved(xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid container.<br/>
     * This method takes the set transport definition for this instance to
     * decide if the provided xml data has to be validated using the REST schema
     * or the SOAP schema.
     * 
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidContainer(final String xmlData) throws Exception {

        assertXmlValidContainer(getTransport(), xmlData);
    }

    public void assertXmlValidContainerList(final String xmlData)
        throws Exception {

        assertXmlValidContainerList(getTransport(), xmlData);
    }

    public void assertXmlValidTocView(final String xmlData) throws Exception {

        assertXmlValidTocView(getTransport(), xmlData);
    }

    public void assertXmlValidContainerMembersList(final String xmlData)
        throws Exception {
        assertXmlValidContainerMembersList(getTransport(), xmlData);

    }

    public void assertXmlValidParents(final String xmlData) throws Exception {
        assertXmlValidParents(getTransport(), xmlData);

    }

    public void assertXmlValidContainerRefList(final String xmlData)
        throws Exception {
        assertXmlValidContainerRefList(getTransport(), xmlData);

    }

    public void assertXmlValidRelations(final String xmlData) throws Exception {

        assertXmlValidRelations(getTransport(), xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid user account.<br/>
     * This method takes the provided transport definition to decide if the
     * provided xml data has to be validated using the REST schema or the SOAP
     * schema.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidUserAccount(
        final int transport, final String xmlData) throws Exception {

        assertXmlValid(xmlData, new URL(getUserAccountSchemaBase(transport)
            + USER_ACCOUNT_XSD));
        assertAllPlaceholderResolved(xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid user account.<br/>
     * This method takes the provided transport definition to decide if the
     * provided xml data has to be validated using the REST schema or the SOAP
     * schema.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidUserAttribute(
        final int transport, final String xmlData) throws Exception {

        assertXmlValid(xmlData, new URL(getAttributesSchemaLocation(transport)));
        assertAllPlaceholderResolved(xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid list of user accounts.<br/>
     * This method takes the provided transport definition to decide if the
     * provided xml data has to be validated using the REST schema or the SOAP
     * schema.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidUserAccountList(
        final int transport, final String xmlData) throws Exception {

        assertXmlValid(xmlData, new URL(getUserAccountListSchemaBase(transport)
            + USER_ACCOUNT_LIST_XSD));
        assertAllPlaceholderResolved(xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid user group.<br/>
     * This method takes the provided transport definition to decide if the
     * provided xml data has to be validated using the REST schema or the SOAP
     * schema.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidUserGroup(
        final int transport, final String xmlData) throws Exception {

        assertXmlValid(xmlData, new URL(getUserGroupSchemaBase(transport)
            + USER_GROUP_XSD));
        assertAllPlaceholderResolved(xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid list of user groups.<br/>
     * This method takes the provided transport definition to decide if the
     * provided xml data has to be validated using the REST schema or the SOAP
     * schema.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidUserGroupList(
        final int transport, final String xmlData) throws Exception {

        assertXmlValid(xmlData, new URL(getUserGroupListSchemaBase(transport)
            + USER_GROUP_LIST_XSD));
        assertAllPlaceholderResolved(xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid list of grants.<br/>
     * This method takes the provided transport definition to decide if the
     * provided xml data has to be validated using the REST schema or the SOAP
     * schema.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidGrantList(
        final int transport, final String xmlData) throws Exception {

        assertXmlValid(xmlData, new URL(getGrantsSchemaLocation(transport)));
        assertAllPlaceholderResolved(xmlData);
    }

    public void assertXmlValidContainer(
        final int transport, final String xmlData) throws Exception {

        assertXmlValid(xmlData, new URL(getContainerSchemaBase(transport)
            + CONTAINER_XSD));
        assertAllPlaceholderResolved(xmlData);
    }

    public void assertXmlValidContainerList(
        final int transport, final String xmlData) throws Exception {

        assertXmlValid(xmlData, new URL(getContainerSchemaBase(transport)
            + CONTAINER_LIST_XSD));
        assertAllPlaceholderResolved(xmlData);
    }

    public void assertXmlValidVersionHistory(
        final int transport, final String xmlData) throws Exception {

        assertXmlValid(xmlData, new URL(getCommonSchemaBase_03(transport)
            + VERSION_HISTORY_XSD));
        assertAllPlaceholderResolved(xmlData);
    }

    public void assertXmlValidTocView(final int transport, final String xmlData)
        throws Exception {

        assertXmlValid(xmlData, new URL(getContainerSchemaBase(transport)
            + TOC_VIEW_XSD));
        assertAllPlaceholderResolved(xmlData);
    }

    public void assertXmlValidRelations(
        final int transport, final String xmlData) throws Exception {

        assertXmlValid(xmlData, new URL(getCommonSchemaBase_03(transport)
            + RELATIONS_XSD));
        assertAllPlaceholderResolved(xmlData);
    }

    public void assertXmlValidContainerMembersList(
        final int transport, final String xmlData) throws Exception {

        assertXmlValid(xmlData, new URL(getMemberListSchemaBase(transport)
            + MEMBER_LIST_XSD));
        assertAllPlaceholderResolved(xmlData);
    }

    public void assertXmlValidParents(final int transport, final String xmlData)
        throws Exception {

        assertXmlValid(xmlData, new URL(getParentsSchemaBase(transport)
            + PARENTS_XSD));
        assertAllPlaceholderResolved(xmlData);
    }

    public void assertXmlValidContainerRefList(
        final int transport, final String xmlData) throws Exception {

        assertXmlValid(xmlData, new URL(getContainerSchemaBase_03(transport)
            + CONTAINER_REF_LIST_XSD));
        assertAllPlaceholderResolved(xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid organizational unit
     * pathlist.<br/>
     * This method takes the set transport definition for this class to decide
     * if the provided xml data has to be validated using the REST schema or the
     * SOAP schema.
     * 
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidOrganizationalUnitPathList(final String xmlData)
        throws Exception {

        assertXmlValidOrganizationalUnitPathList(getTransport(), xmlData);
        assertAllPlaceholderResolved(xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid organizational unit
     * pathlist.<br/>
     * This method takes the provided transport definition to decide if the
     * provided xml data has to be validated using the REST schema or the SOAP
     * schema.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidOrganizationalUnitPathList(
        final int transport, final String xmlData) throws Exception {

        assertXmlValid(xmlData,
            new URL(getOrganizationalUnitSchemaBase(transport, "0.4")
                + ORGANIZATIONAL_UNIT_PATH_LIST_XSD));
        assertAllPlaceholderResolved(xmlData);

    }

    /**
     * Asserts that the provided xml data is a valid organizational unit.<br/>
     * This method takes the set transport definition for this instance to
     * decide if the provided xml data has to be validated using the REST schema
     * or the SOAP schema.
     * 
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidOrganizationalUnits(final String xmlData)
        throws Exception {

        assertXmlValidOrganizationalUnits(getTransport(), xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid organizational unit.<br/>
     * This method takes the provided transport definition to decide if the
     * provided xml data has to be validated using the REST schema or the SOAP
     * schema.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidOrganizationalUnits(
        final int transport, final String xmlData) throws Exception {

        assertXmlValid(xmlData,
            new URL(getOrganizationalUnitSchemaBase(transport, "0.8")
                + ORGANIZATIONAL_UNIT_LIST_XSD));
        assertAllPlaceholderResolved(xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid organizational unit.<br/>
     * This method takes the set transport definition for this instance to
     * decide if the provided xml data has to be validated using the REST schema
     * or the SOAP schema.
     * 
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidOrganizationalUnitsRefs(final String xmlData)
        throws Exception {

        assertXmlValidOrganizationalUnitsRefs(getTransport(), xmlData);
        assertAllPlaceholderResolved(xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid organizational unit.<br/>
     * This method takes the provided transport definition to decide if the
     * provided xml data has to be validated using the REST schema or the SOAP
     * schema.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param xmlData
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public void assertXmlValidOrganizationalUnitsRefs(
        final int transport, final String xmlData) throws Exception {

        assertXmlValid(xmlData,
            new URL(getOrganizationalUnitSchemaBase(transport, "0.4")
                + ORGANIZATIONAL_UNIT_REF_LIST_XSD));
        assertAllPlaceholderResolved(xmlData);
    }

    /**
     * Gets the base url for the schemas depending on the provided transport
     * definition.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @return Returns the transport specific schema location base.
     */
    protected String getSchemaLocationBase(final int transport) {

        assertTransport(transport);
        if (transport == Constants.TRANSPORT_REST) {
            return getSchemaLocationBase() + "rest";
        }
        else {
            return getSchemaLocationBase() + "soap";
        }
    }

    /**
     * Get location of Schema. Schema are delived with framework and therefore
     * the schema location based on the framework location. Schemas are
     * currently not pulled from www.escidoc.org wven if they could.
     * 
     * @return schema location
     */
    protected String getSchemaLocationBase() {

        if (BASE_URL_SCHEMA_LOCATION == null) {
            // Maybe it would be better to load the schema from www.escidoc.org
            BASE_URL_SCHEMA_LOCATION = getFrameworkUrl() + "/xsd/";
        }
        return BASE_URL_SCHEMA_LOCATION;
    }

    protected String getContextSchemaBase(final int transport) {

        return getSchemaLocationBase(transport) + "/context/0.7/";
    }

    protected String getContainerSchemaBase(final int transport) {

        return getSchemaLocationBase(transport) + "/container/0.9/";
    }

    protected String getMemberListSchemaBase(final int transport) {

        return getSchemaLocationBase(transport) + "/common/0.10/";
    }

    protected String getParentsSchemaBase(final int transport) {

        return getSchemaLocationBase(transport) + "/common/0.9/";
    }

    protected String getContainerSchemaBase_03(final int transport) {

        return getSchemaLocationBase(transport) + "/container/0.3/";
    }

    protected String getCommonSchemaBase_03(final int transport) {

        return getSchemaLocationBase(transport) + "/common/0.3/";
    }

    /**
     * Gets the base url for all schema locations related to organizational
     * units.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @return Returns the base url for all schema locations related to
     *         organizational units.
     */
    protected String getOrganizationalUnitSchemaBase(
        final int transport, final String version) {

        return getSchemaLocationBase(transport) + "/organizational-unit/"
            + version + "/";
    }

    /**
     * Gets the base url for all schema locations related to organizational
     * units.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @return Returns the base url for all schema locations related to
     *         organizational units.
     */
    protected String getContentModelSchemaBase(
        final int transport, final String version) {

        return getSchemaLocationBase(transport) + "/content-model/" + version
            + "/";
    }

    /**
     * Create a Param structure for PID assignments. The last-modification-date
     * is retrieved from the by id selected object.
     * 
     * @param id
     *            The object Id
     * @param url
     *            URL of the resource (not checked)
     * @throws Exception
     *             Thrown if getTheLastModificationDate() fails.
     * @return param XML snippet.
     */
    public final String getPidParam(final String id, final String url)
        throws Exception {

        String extUrl = url;
        if (!extUrl.endsWith("/")) {
            extUrl += "/";
        }
        extUrl += System.nanoTime();

        String param =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<param last-modification-date=\""
                + getTheLastModificationDate(id) + "\"><url>" + extUrl
                + "</url></param>";
        return (param);
    }

    /**
     * Create a Param structure for PID assignments.
     * 
     * @param lstModDate
     *            The last-modification-date.
     * @param url
     *            URL of the resource (not checked)
     * @return param XML snippet.
     */
    protected final String getPidParam2(final DateTime lstModDate, final URL url) {

        String extUrl = url.toString();
        if (!extUrl.endsWith("/")) {
            extUrl += "/";
        }
        extUrl += System.nanoTime();

        String param =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<param last-modification-date=\"" + lstModDate.toString()
                + "\"><url>" + extUrl + "</url></param>";
        return (param);
    }

    /**
     * Gets the base url for all schema locations related to PDP requests.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @return Returns the base url for all schema locations related to PDP
     *         requests.
     */
    protected String getRequestsSchemaBase(final int transport) {

        return getSchemaLocationBase(transport) + "/pdp/0.3/";
    }

    /**
     * Gets the base url for all schema locations related to PDP results.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @return Returns the base url for all schema locations related to PDP
     *         results.
     */
    protected String getResultsSchemaBase(final int transport) {

        return getSchemaLocationBase(transport) + "/pdp/0.3/";
    }

    /**
     * Gets the base url for all schema locations related to roles.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @return Returns the base url for all schema locations related to roles.
     */
    protected String getRoleSchemaBase(final int transport) {

        return getSchemaLocationBase(transport) + "/role/0.5/";
    }

    /**
     * Gets the base url for all schema locations related to list of roles.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @return Returns the base url for all schema locations related to list of
     *         roles.
     */
    protected String getRoleListSchemaBase(final int transport) {

        return getSchemaLocationBase(transport) + "/role/0.5/";
    }

    /**
     * Gets the schema locations for grants grants.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @return Returns the grants schema location.
     */
    protected String getGrantsSchemaLocation(final int transport) {

        return getSchemaLocationBase(transport) + "/user-account/0.5/"
            + GRANTS_XSD;
    }

    /**
     * Gets the schema locations for preferences.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @return Returns the preferences schema location.
     */
    protected String getPreferencesSchemaLocation(final int transport) {

        return getSchemaLocationBase(transport) + "/user-account/0.1/"
            + PREFERENCES_XSD;
    }

    /**
     * Gets the schema locations for attributes.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @return Returns the attributes schema location.
     */
    protected String getAttributesSchemaLocation(final int transport) {

        return getSchemaLocationBase(transport) + "/user-account/0.1/"
            + ATTRIBUTES_XSD;
    }

    /**
     * Gets the base url for all schema locations related to user accounts.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @return Returns the base url for all schema locations related to user
     *         accounts.
     */
    protected String getUserAccountSchemaBase(final int transport) {

        return getSchemaLocationBase(transport) + "/user-account/0.7/";
    }

    /**
     * Gets the base url for all schema locations related to list of user
     * accounts.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @return Returns the base url for all schema locations related to list of
     *         user accounts.
     */
    protected String getUserAccountListSchemaBase(final int transport) {

        return getSchemaLocationBase(transport) + "/user-account/0.7/";
    }

    /**
     * Gets the base url for all schema locations related to user groups.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @return Returns the base url for all schema locations related to user
     *         groups.
     */
    protected String getUserGroupSchemaBase(final int transport) {

        return getSchemaLocationBase(transport) + "/user-group/0.6/";
    }

    /**
     * Gets the name of a resource. The name is fetched from the name element
     * with in the properties element of the resource.
     * 
     * @param document
     *            The document to retrieve the value from.
     * @return Returns the value of the element selected by xpath
     *         '//properties/name'. If this value does not exist, an assertion
     *         fails.
     * @throws Exception
     *             If anything fails.
     */
    public String getNameValue(final Document document) throws Exception {

        final String nameXpath = "//" + NAME_PROPERTIES + "/" + NAME_NAME;
        final Node nameNode = selectSingleNodeAsserted(document, nameXpath);
        assertNotNull("Expected name element does not exist [" + nameXpath
            + "]", nameNode);
        return nameNode.getTextContent();
    }

    /**
     * Gets the base url for all schema locations related to list of user
     * groups.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @return Returns the base url for all schema locations related to list of
     *         user groups.
     */
    protected String getUserGroupListSchemaBase(final int transport) {

        return getSchemaLocationBase(transport) + "/user-group/0.6/";
    }

    /**
     * Gets the base url for all schema locations related to admin.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @return Returns the base url for all schema locations related to user
     *         accounts.
     */
    protected String getAdminSchemaBase(final int transport) {

        return getSchemaLocationBase(transport) + "/admin/0.1/";
    }

    /**
     * Gets the title value of the root element from the document.<br/>
     * This method takes the provided transport definition to decide if the
     * title value has to be extracted from the title attribute of the root
     * element (REST) or if <code>null</code> has to be returned as no title
     * exists (SOAP).
     * 
     * @param document
     *            The document to retrieve the value from.
     * @return Returns the attribute value or <code>null</code>.
     * @throws Exception
     *             If anything fails.
     */
    public String getTitleValue(final Document document) throws Exception {

        return getTitleValue(getTransport(), document);
    }

    /**
     * Gets the title value of the root element from the document.<br/>
     * This method takes the provided transport definition to decide to decide
     * if the title value has to be extracted from the title attribute of the
     * root element (REST) or if <code>null</code> has to be returned as no
     * title exists (SOAP).
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param document
     *            The document to retrieve the value from.
     * @return Returns the attribute value or <code>null</code>.
     * @throws Exception
     *             If anything fails.
     */
    public static String getTitleValue(
        final int transport, final Document document) throws Exception {

        assertTransport(transport);

        if (transport == Constants.TRANSPORT_REST) {
            return getRootElementAttributeValue(document, NAME_TITLE);
        }
        else {
            return null;
        }
    }

    /**
     * Gets the objid value of the root element from the document.<br/>
     * This method takes set transport definition of this instance to decide if
     * the objid value has to be extracted from the href attribute of the root
     * element (REST) or can be taken from the objid attribute of the root
     * element (SOAP)
     * 
     * @param xmlData
     *            The xml data to retrieve the value from.
     * @return Returns the attribute value.
     * @throws Exception
     *             If anything fails.
     */
    @Override
    public String getObjidValue(final String xmlData) throws Exception {

        return getObjidValue(getTransport(), getDocument(xmlData));
    }

    /**
     * Gets the objid value of the root element from the document.<br/>
     * This method takes set transport definition of this instance to decide if
     * the objid value has to be extracted from the href attribute of the root
     * element (REST) or can be taken from the objid attribute of the root
     * element (SOAP)
     * 
     * @param document
     *            The document to retrieve the value from.
     * @return Returns the attribute value.
     * @throws Exception
     *             If anything fails.
     */
    @Override
    public String getObjidValue(final Document document) throws Exception {

        return getObjidValue(getTransport(), document);
    }

    /**
     * Get id of latest version of object (item, container).
     * 
     * @param document
     *            The item or container document.
     * @return The latest version objid.
     * @throws Exception
     *             Thrown if parsing fails.
     */
    public String getLatestVersionObjidValue(final Document document)
        throws Exception {
        String id = null;
        int transport = getTransport();

        if (transport == Constants.TRANSPORT_REST) {
            id =
                getIdFromHrefValue(selectSingleNode(document,
                    "//properties/latest-version/@href").getTextContent());
        }
        else {
            id =
                selectSingleNode(document, "//properties/latest-version/@objid")
                    .getTextContent();
        }

        return (id);
    }

    /**
     * Asserts that the selected element is an element that contains xlink
     * attributes in case of REST access. In case of SOAP, this element may or
     * may not contain an objid attribute, and in case of REST the xlink:href
     * attribute may or may not end with an object id.
     * 
     * @param message
     *            The assertion failed message.
     * @param node
     *            The node in that the element shall be selected and asserted.
     * @param xpath
     *            The Xpath to the element that shall be asserted.
     * @param hrefBase
     *            The base value the href value. It is checked if the href
     *            attribute starts with this value in case of REST.<br/>
     *            If this value is <code>null</code>, this assertion is skipped.
     * @return Returns an array containing the found href and title values. In
     *         case of SOAP, the returned href and title value are
     *         <code>null</code>.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public String[] assertXlinkElement(
        final String message, final Node node, final String xpath,
        final String hrefBase) throws Exception {

        final String msg = prepareAssertionFailedMessage(message);

        assertXmlExists(msg + "Node does not exist [" + xpath + "]", node,
            xpath);

        final String xpathXlinkHref = xpath + PART_XLINK_HREF;
        final String xpathXlinkTitle = xpath + PART_XLINK_TITLE;
        final String xpathXlinkType = xpath + PART_XLINK_TYPE;

        final String href;
        final String title;
        if (getTransport() == Constants.TRANSPORT_REST) {
            assertXmlExists(msg + "Missing xlink:href in REST data. ", node,
                xpathXlinkHref);
            assertXmlExists(msg + "Missing xlink:title in REST data. ", node,
                xpathXlinkTitle);
            assertXmlExists(msg + "Missing xlink:type in REST data. ", node,
                xpathXlinkType);

            href = selectSingleNode(node, xpathXlinkHref).getTextContent();
            assertNotEquals(msg + "Empty href. ", "", href);
            if (hrefBase != null) {
                if (!href.startsWith(hrefBase)) {
                    fail(msg + "Href does not start as expected. " + hrefBase
                        + " but was " + href);
                }
            }

            title = selectSingleNode(node, xpathXlinkTitle).getTextContent();

            assertXmlEquals(msg
                + "Unexpected value for xlink:type in REST data. ", node,
                xpathXlinkType, XLINK_TYPE_VALUE);
        }
        else {
            assertXmlNotExists(msg + "Unexpected xlink:href in SOAP data. ",
                node, xpathXlinkHref);
            assertXmlNotExists(msg + "Unexpected xlink:title in SOAP data. ",
                node, xpathXlinkTitle);
            assertXmlNotExists(msg + "Unexpected xlink:type in SOAP data. ",
                node, xpathXlinkType);

            href = null;
            title = null;
        }

        return new String[] { href, title };
    }

    /**
     * Asserts that the selected element is an element that contains xlink
     * attributes in case of REST access. In case of SOAP, this element may or
     * may not contain an objid attribute, and in case of REST the xlink:href
     * attribute may or may not end with an object id.<br/>
     * It is also asserted, that the attributes matches the expected values.
     * 
     * @param message
     *            The assertion failed message.
     * @param expectedHref
     *            The expected href that has to be asserted.
     * @param expectedTitle
     *            The expected title that has to be asserted.
     * @param node
     *            The node in that the element shall be selected and asserted.
     * @param xpath
     *            The Xpath to the element that shall be asserted.
     * @return Returns an array containing the found href and title values. In
     *         case of SOAP, the returned href and title value are
     *         <code>null</code>.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public String[] assertXlinkElement(
        final String message, final String expectedHref,
        final String expectedTitle, final Node node, final String xpath)
        throws Exception {

        final String msg = prepareAssertionFailedMessage(message);
        final String[] values = assertXlinkElement(message, node, xpath, null);
        final String href = values[0];
        final String title = values[1];

        if (getTransport() == Constants.TRANSPORT_REST) {
            assertEquals(msg + "Mismatch in href. ", expectedHref, href);
            assertEquals(msg + "Mismatch in title. ", expectedTitle, title);
        }

        return values;
    }

    /**
     * Asserts that the selected element is an element that contains xlink
     * attributes in case of REST access, but does not contain an objid
     * attribute in case of SOAP.
     * 
     * @param message
     *            The assertion failed message.
     * @param node
     *            The node in that the element shall be selected and asserted.
     * @param xpath
     *            The Xpath to the element that shall be asserted.
     * @param hrefBase
     *            The base value the href value. It is checked if the href
     *            attribute starts with this value in case of REST.<br/>
     *            If this value is <code>null</code>, this assertion is skipped.
     * @return Returns an array containing the found href and title values. In
     *         case of SOAP, the returned href and title value are
     *         <code>null</code>.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public String[] assertXlinkElementWithoutObjid(
        final String message, final Node node, final String xpath,
        final String hrefBase) throws Exception {

        final String msg = prepareAssertionFailedMessage(message);

        final String[] values =
            assertXlinkElement(message, node, xpath, hrefBase);

        assertXmlNotExists(msg + "Unexpected objid in REST data. ", node, xpath
            + PART_OBJID);

        return values;

    }

    /**
     * Asserts that the selected element is an element that contains xlink
     * attributes in case of REST access, but does not contain an objid
     * attribute in case of SOAP.<br/>
     * It is also asserted, that the attributes matches the expected values.
     * 
     * @param message
     *            The assertion failed message.
     * @param expectedHref
     *            The expected href that has to be asserted.
     * @param expectedTitle
     *            The expected title that has to be asserted.
     * @param node
     *            The node in that the element shall be selected and asserted.
     * @param hrefBase
     *            TODO
     * @param xpath
     *            The Xpath to the element that shall be asserted.
     * @return Returns an array containing the found href and title values. In
     *         case of SOAP, the returned href and title value are
     *         <code>null</code>.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public String[] assertXlinkElementWithoutObjid(
        final String message, final String expectedHref,
        final String expectedTitle, final Node node, final String xpath,
        final String hrefBase) throws Exception {

        final String msg = prepareAssertionFailedMessage(message);

        final String[] values =
            assertXlinkElement(message, node, xpath, hrefBase);

        assertXmlNotExists(msg + "Unexpected objid in REST data. ", node, xpath
            + PART_OBJID);

        return values;
    }

    /**
     * Asserts that the selected element is a valid referencing element. This is
     * an element that contains xlink attributes (REST) or an objid attribute
     * (SOAP). In case of Rest, the xlink:href ends with the object id of the
     * referenced attribute.
     * 
     * @param message
     *            The assertion failed message.
     * @param node
     *            The node in that the element shall be selected and asserted.
     * @param xpath
     *            The Xpath to the element that shall be asserted.
     * @param hrefBase
     *            The base value the href value. This value together with the
     *            object id must match the value of the href attribute in case
     *            of REST.<br/>
     *            If this value is <code>null</code>, this assertion is skipped.
     * @return Returns an array containing the object id, the href (if any), and
     *         the title (if any) of the referenced element. In case of SOAP,
     *         the returned href and title values are <code>null</code>.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public String[] assertReferencingElement(
        final String message, final Node node, final String xpath,
        final String hrefBase) throws Exception {

        final String msg = prepareAssertionFailedMessage(message);

        assertXmlExists(msg + "Missing the root element [" + xpath + "]", node,
            xpath);

        final String[] xlinkValues =
            assertXlinkElement(msg, node, xpath, hrefBase);
        final String href = xlinkValues[0];
        final String title = xlinkValues[1];

        final String xpathObjid = xpath + PART_OBJID;

        final String id;
        if (getTransport() == Constants.TRANSPORT_REST) {
            assertXmlNotExists(msg + "Unexpected objid in REST data. ", node,
                xpathObjid);

            id = getObjidFromHref(href);
            if (hrefBase != null) {
                String hrefBaseWithId = hrefBase + "/" + id;
                assertEquals(msg + "Invalid xlink:href", hrefBaseWithId, href);
            }
        }
        else {
            assertXmlExists(msg + "Missing objid in SOAP data. ", node,
                xpathObjid);
            id = selectSingleNode(node, xpathObjid).getTextContent();
        }

        assertNotEquals(msg + "Empty objid value. ", "", id);

        return new String[] { id, href, title };
    }

    /**
     * Asserts that the selected element is a valid referencing element. This is
     * an element that contains xlink attributes (REST) or an objid attribute
     * (SOAP). In case of Rest, the xlink:href ends with the object id of the
     * referenced attribute.
     * 
     * @param message
     *            The assertion failed message.
     * @param expectedObjid
     *            The expected object id of the referenced object.
     * @param expectedTitle
     *            The expected title of the referenced object.
     * @param toBeAssertedNode
     *            The node in that the element shall be selected and asserted.
     * @param xpath
     *            The Xpath to the element that shall be asserted.
     * @param hrefBase
     *            The base value the href value. This value together with the
     *            object id must match the value of the href attribute in case
     *            of REST.<br/>
     *            If this value is <code>null</code>, this assertion is skipped.
     * @return Returns an array containing the object id, the href (if any), and
     *         the title (if any) of the referenced element. In case of SOAP,
     *         the returned href and title values are <code>null</code>.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public String[] assertReferencingElement(
        final String message, final String expectedObjid,
        final String expectedTitle, final Node toBeAssertedNode,
        final String xpath, final String hrefBase) throws Exception {

        final String msg = prepareAssertionFailedMessage(message);

        final String expectedHref = hrefBase + "/" + expectedObjid;
        String[] values =
            assertXlinkElement(msg, expectedHref, expectedTitle,
                toBeAssertedNode, xpath);

        // objid value
        final String objid = getObjidValue(toBeAssertedNode, xpath);
        assertEquals(msg + "objid values mismatch. ", expectedObjid, objid);

        return new String[] { objid, values[0], values[1] };
    }

    /**
     * Asserts that the selected element is a valid referencing element. This is
     * an element that contains xlink attributes (REST) or an objid attribute
     * (SOAP). In case of Rest, the xlink:href ends with the object id of the
     * referenced attribute.<br/>
     * Additionally it is checked whether the values from the selected element
     * matches the respective values from the node containing the expected
     * values. Hereby, the title only is asserted, if the expected node contains
     * one, as this value is optional.
     * 
     * @param message
     *            The assertion failed message.
     * @param expectedNode
     *            The node in that the element shall be selected and used to
     *            check it against the toBeAsserted node.
     * @param toBeAssertedNode
     *            The node in that the element shall be selected and asserted.
     * @param xpath
     *            The Xpath to the element that shall be asserted.
     * @param hrefBase
     *            The base value the href value. This value together with the
     *            object id must match the value of the href attribute in case
     *            of REST.<br/>
     *            If this value is <code>null</code>, this assertion is skipped.
     * @return Returns an array containing the object id, the href (if any), and
     *         the title (if any) of the referenced element. In case of SOAP,
     *         the returned href and title values are <code>null</code>.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public String[] assertReferencingElement(
        final String message, final Node expectedNode,
        final Node toBeAssertedNode, final String xpath, final String hrefBase)
        throws Exception {

        final String msg = prepareAssertionFailedMessage(message);

        String[] refValues =
            assertReferencingElement(msg, toBeAssertedNode, xpath, hrefBase);

        // objid value
        final String objid = refValues[0];
        final String expectedObjid = getObjidValue(expectedNode, xpath);
        assertEquals(msg + "objid values mismatch. ", expectedObjid, objid);

        // href
        final String href = refValues[1];
        if (href != null) {
            assertEquals(msg + "href values mismatch. ",
                selectSingleNodeAsserted(expectedNode, xpath + PART_XLINK_HREF)
                    .getTextContent(), href);
        }

        // title
        final String title = refValues[2];
        if (title != null) {
            final Node expectedTitleNode =
                selectSingleNode(expectedNode, xpath + PART_XLINK_TITLE);
            if (expectedTitleNode != null) {
                assertEquals(msg + "title values mismatch. ",
                    expectedTitleNode.getTextContent(), title);
            }
        }

        return refValues;
    }

    /**
     * Asserts that the selected element is a valid referencing element. This is
     * an element that contains xlink attributes (REST) or an objid attribute
     * (SOAP). In case of Rest, the xlink:href ends with the object id of the
     * referenced attribute.<br/>
     * Additionally it is checked whether the values from the selected element
     * matches the respective values from the node containing the expected
     * values. In comparing to assertReferencingElement(String, Node, Node,
     * String, String); is the order of the compared element nodes free.
     * 
     * @param message
     *            The assertion failed message.
     * @param expectedNode
     *            The node in that the element shall be selected and used to
     *            check it against the toBeAsserted node.
     * @param toBeAssertedNode
     *            The node in that the element shall be selected and asserted.
     * @param xpathElement
     *            The Xpath to the element that shall be asserted.
     * @param xpathGroup
     *            The Xpath to the group of the elements that shall be asserted.
     * @param hrefBase
     *            The base value the href value. This value together with the
     *            object id must match the value of the href attribute in case
     *            of REST.<br/>
     *            If this value is <code>null</code>, this assertion is skipped.
     * @return Returns an array containing the object id, the href (if any), and
     *         the title (if any) of the referenced element. In case of SOAP,
     *         the returned href and title values are <code>null</code>.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public String[] assertReferencingGroupElements(
        final String message, final Node expectedNode,
        final Node toBeAssertedNode, final String xpathElement,
        final String xpathGroup, final String hrefBase) throws Exception {

        final String msg = prepareAssertionFailedMessage(message);

        String[] refValues =
            assertReferencingElement(msg, toBeAssertedNode, xpathElement,
                hrefBase);
        final String objid = refValues[0];

        // objid value
        NodeList expectedOus = selectNodeList(expectedNode, xpathGroup);
        final int ouLength = expectedOus.getLength();
        String expectedObjid = null;
        String elementXpath = null;

        for (int i = 0; i < ouLength; i++) {
            elementXpath = xpathGroup + "[" + (i + 1) + "]";
            expectedObjid = getObjidValue(expectedNode, elementXpath);
            if ((expectedObjid != null) && (expectedObjid.equals(objid))) {
                break;
            }
        }

        assertEquals(msg + "objid values mismatch. ", expectedObjid, objid);

        // href
        final String href = refValues[1];
        if (href != null) {
            String tmpHref =
                selectSingleNodeAsserted(expectedNode, elementXpath + "/@href")
                    .getTextContent();
            assertEquals(msg + "href values mismatch. ", tmpHref, href);
        }

        // title
        // final String title = refValues[2];
        // if (title != null) {
        // assertEquals(msg + "title values mismatch. ",
        // selectSingleNodeAsserted(expectedNode, elementXpath + "/@title")
        // .getTextContent(), title);
        // }

        return refValues;
    }

    /**
     * Asserts that the selected element is a valid root element.
     * 
     * @param message
     *            The assertion failed message.
     * @param node
     *            The node in that the element shall be selected and asserted.
     * @param xpath
     *            The Xpath to the element that shall be asserted.
     * @param hrefBase
     *            The base value the href value. This value together with the
     *            object id must match the value of the href attribute in case
     *            of REST. It must not be <code>null</code>.
     * @param timestampBeforeLastMod
     *            A Timestamp that must be before the reported last modification
     *            date. If this is <code>null</code>, this assertion is skipped.
     * @return Returns an array containing the object id and the last
     *         modification date of the referenced element.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public String[] assertRootElement(
        final String message, final Node node, final String xpath,
        final String hrefBase, final String timestampBeforeLastMod)
        throws Exception {

        final String msg = prepareAssertionFailedMessage(message);
        assertNotNull(msg + "Parameter hrefBase must not be null. ", hrefBase);

        final String id =
            assertReferencingElement(msg, node, xpath, hrefBase)[0];

        final String lastModificationDate =
            assertLastModificationDate(message, node, xpath,
                timestampBeforeLastMod);

        return new String[] { id, lastModificationDate };
    }

    /**
     * Checks last modification date.
     * 
     * @param msg
     *            The message.
     * @param node
     *            Document to be checked.
     * @param xpath
     *            XPath to element to be checked.
     * @param timestampBeforeLastMod
     *            The timestamp before last modification.
     * @return The current timestamp.
     * @throws Exception
     *             If an error occures.
     */
    public String assertLastModificationDate(
        final String msg, final Node node, final String xpath,
        final String timestampBeforeLastMod) throws Exception {
        final String xpathLastModificationDate =
            xpath + PART_LAST_MODIFICATION_DATE;
        assertXmlExists(msg + "Missing last modification date. ", node,
            xpathLastModificationDate);
        final String lastModificationDate =
            selectSingleNode(node, xpathLastModificationDate).getTextContent();
        assertNotEquals(msg + "Empty last modification date. ", "",
            lastModificationDate);
        if (timestampBeforeLastMod != null) {
            assertTimestampIsEqualOrAfter(msg
                + "last-modification-date is not as expected. ",
                lastModificationDate, timestampBeforeLastMod);
        }
        return lastModificationDate;
    }

    /**
     * Asserts that the selected element is a valid properties element of a
     * resource. <br/>
     * The resource may or may not be under version control, as this method only
     * checks creation-date and created-by elements. This method asserts the
     * commonly used property elements created-by and modified-by. Further
     * checks have to be implemented by the subclasses.
     * 
     * @param message
     *            The assertion failed message.
     * @param node
     *            The node in that the element shall be selected and asserted.
     * @param xpath
     *            The Xpath to the element that shall be asserted.
     * @param timestampBeforeCreation
     *            A Timestamp that must be before the reported creation date. If
     *            this is <code>null</code>, this assertion is skipped.
     * @return Returns an array containing the creation date and the objid of
     *         the created-by element.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public String[] assertPropertiesElement(
        final String message, final Node node, final String xpath,
        final String timestampBeforeCreation) throws Exception {

        final String msg = prepareAssertionFailedMessage(message);

        assertXmlExists(msg + "Missing properties [" + xpath + "] ", node,
            xpath);

        final String xpathCreationDate = xpath + "/" + NAME_CREATION_DATE;
        final String xpathCreatedBy = xpath + "/" + NAME_CREATED_BY;

        final String creationDate =
            selectSingleNodeAsserted(node, xpathCreationDate).getTextContent();
        if (timestampBeforeCreation != null) {
            assertTimestampIsEqualOrAfter(msg
                + "creation-date is not as expected. ", creationDate,
                timestampBeforeCreation);
        }

        final String createdById =
            assertReferencingElement(msg + "Invalid created-by. ", node,
                xpathCreatedBy, Constants.USER_ACCOUNT_BASE_URI)[0];

        return new String[] { creationDate, createdById };
    }

    /**
     * Asserts that the selected element is a valid properties element of a
     * resource that is not under version control.<br/>
     * In addition to the method <code>assertPropertiesElement</code>, this
     * method checks the modified-by element and additionally returns the
     * modified-by objid.
     * 
     * @param message
     *            The assertion failed message.
     * @param node
     *            The node in that the element shall be selected and asserted.
     * @param xpath
     *            The Xpath to the element that shall be asserted.
     * @param timestampBeforeCreation
     *            A Timestamp that must be before the reported creation date. If
     *            this is <code>null</code>, this assertion is skipped.
     * @return Returns an array containing the creation date, the objid of the
     *         created-by element, and the objid of the modified-by element.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public String[] assertPropertiesElementUnversioned(
        final String message, final Node node, final String xpath,
        final String timestampBeforeCreation) throws Exception {

        final String msg = prepareAssertionFailedMessage(message);

        final String[] propertiesValues =
            assertPropertiesElement(message, node, xpath,
                timestampBeforeCreation);

        final String xpathModifiedBy = xpath + "/" + NAME_MODIFIED_BY;
        final String modifiedById =
            assertReferencingElement(msg + "Invalid modified-by. ", node,
                xpathModifiedBy, Constants.USER_ACCOUNT_BASE_URI)[0];

        return new String[] { propertiesValues[0], propertiesValues[1],
            modifiedById };
    }

    /**
     * Asserts that the selected element is a valid properties element of a
     * resource. <br/>
     * The resource may or may not be under version control, as this method only
     * checks creation-date and created-by elements. This method asserts the
     * commonly used property elements created-by and modified-by. Further
     * checks have to be implemented by the subclasses.
     * 
     * @param message
     *            The assertion failed message.
     * @param node
     *            The node in that the element shall be selected and asserted.
     * @param xpath
     *            The Xpath to the element that shall be asserted.
     * @param expectedElementValue
     *            expectedElementValue.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public void assertElementEquals(
        final String message, final Node node, final String xpath,
        final String expectedElementValue) throws Exception {

        final String msg = prepareAssertionFailedMessage(message);

        assertXmlExists(msg + "Missing properties [" + xpath + "] ", node,
            xpath);

        final String elementValue =
            selectSingleNodeAsserted(node, xpath).getTextContent();

        assertEquals(message, expectedElementValue, elementValue);
    }

    /**
     * Gets the objid value of the root element from the document.<br/>
     * This method takes the provided transport definition to decide if the
     * objid value has to be extracted from the href attribute of the root
     * element (REST) or can be taken from the objid attribute of the root
     * element (SOAP).
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param document
     *            The document to retrieve the value from.
     * @return Returns the attribute value.
     * @throws Exception
     *             If anything fails.
     */
    public static String getObjidValue(
        final int transport, final Document document) throws Exception {

        assertTransport(transport);

        if (transport == Constants.TRANSPORT_REST) {
            return getObjidFromHref(getRootElementHrefValue(document));
        }
        else {
            return getRootElementAttributeValue(document, NAME_OBJID);
        }
    }

    /**
     * Gets the objid value of the element selected in the provided node.<br/>
     * This method takes the set transport definition for this instance to
     * decide if the objid value has to be extracted from the href attribute of
     * the selected element (REST) or can be taken from the objid attribute of
     * the selected element (SOAP).
     * 
     * @param node
     *            The node to select an element from.
     * @param xpath
     *            The xpath to select the element in the provided node.
     * @return Returns the attribute value.
     * @throws Exception
     *             If anything fails.
     */
    @Override
    public String getObjidValue(final Node node, final String xpath)
        throws Exception {

        return getObjidValue(getTransport(), node, xpath);
    }

    /**
     * Gets the objid values of the elements selected in the provided node.<br/>
     * This method takes the set transport definition for this instance to
     * decide if the objid value has to be extracted from the href attribute of
     * the selected element (REST) or can be taken from the objid attribute of
     * the selected element (SOAP).
     * 
     * @param node
     *            The node to select an element from.
     * @param xpath
     *            The xpath to select the element in the provided node.
     * @return Returns the attribute values as String[].
     * @throws Exception
     *             If anything fails.
     */
    public String[] getObjidValues(final Node node, final String xpath)
        throws Exception {

        return getObjidValues(getTransport(), node, xpath);
    }

    /**
     * Gets the objid value of the element selected in the provided node.<br/>
     * This method takes the provided transport definition to decide if the
     * objid value has to be extracted from the href attribute of the selected
     * element (REST) or can be taken from the objid attribute of the selected
     * element (SOAP).
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param node
     *            The node to select an element from.
     * @param xpath
     *            The xpath to select the element in the provided node.
     * @return Returns the attribute value.
     * @throws Exception
     *             If anything fails.
     */
    public static String getObjidValue(
        final int transport, final Node node, final String xpath)
        throws Exception {

        assertTransport(transport);

        final String attributeXpathPrefix;
        if (xpath == null || "".equals(xpath)) {
            attributeXpathPrefix = "@";
        }
        else {
            attributeXpathPrefix = xpath + "/@";
        }
        if (transport == Constants.TRANSPORT_REST) {
            final String xpathHref = attributeXpathPrefix + NAME_HREF;
            final String href =
                selectSingleNodeAsserted(node, xpathHref).getTextContent();
            return getObjidFromHref(href);
        }
        else {
            final String xpathObjid = attributeXpathPrefix + NAME_OBJID;
            return selectSingleNodeAsserted(node, xpathObjid).getTextContent();
        }
    }

    /**
     * Gets the objid values of the elements selected in the provided node.<br/>
     * This method takes the provided transport definition to decide if the
     * objid value has to be extracted from the href attribute of the selected
     * element (REST) or can be taken from the objid attribute of the selected
     * element (SOAP).
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param node
     *            The node to select an element from.
     * @param xpath
     *            The xpath to select the element in the provided node.
     * @return Returns the attribute values as String[].
     * @throws Exception
     *             If anything fails.
     */
    public static String[] getObjidValues(
        final int transport, final Node node, final String xpath)
        throws Exception {

        String[] objids = null;
        assertTransport(transport);

        final String attributeXpathPrefix;
        if (xpath == null || "".equals(xpath)) {
            attributeXpathPrefix = "@";
        }
        else {
            attributeXpathPrefix = xpath + "/@";
        }
        if (transport == Constants.TRANSPORT_REST) {
            final String xpathHref = attributeXpathPrefix + NAME_HREF;
            final NodeList hrefs = selectNodeList(node, xpathHref);
            objids = new String[hrefs.getLength()];
            for (int i = 0; i < hrefs.getLength(); i++) {
                String hrefStr = hrefs.item(i).getTextContent();
                objids[i] = getObjidFromHref(hrefStr);
            }
        }
        else {
            final String xpathObjid = attributeXpathPrefix + NAME_OBJID;
            final NodeList hrefs = selectNodeList(node, xpathObjid);
            objids = new String[hrefs.getLength()];
            for (int i = 0; i < hrefs.getLength(); i++) {
                String hrefStr = hrefs.item(i).getTextContent();
                objids[i] = hrefStr;
            }
        }
        return objids;
    }

    /**
     * Asserts the created-by element.
     * 
     * @param message
     *            The assertion failed message.
     * @param expectedNode
     *            The node that shall be used to check the toBeAssertedNode.
     * @param toBeAssertedNode
     *            The node that shall be asserted.
     * @return Returns an array containing the object id, the href (if any), and
     *         the title (if any) of the referenced element. In case of SOAP,
     *         the returned href and title values are <code>null</code>.
     * @throws Exception
     *             If anything fails.
     */
    public String[] assertCreatedBy(
        final String message, final Node expectedNode,
        final Node toBeAssertedNode) throws Exception {

        final String msg = prepareAssertionFailedMessage(message);

        return assertReferencingElement(msg, expectedNode, toBeAssertedNode,
            XPATH_CREATED_BY, Constants.USER_ACCOUNT_BASE_URI);
    }

    /**
     * Asserts the modified-by element.
     * 
     * @param message
     *            The assertion failed message.
     * @param expectedNode
     *            The node that shall be used to check the toBeAssertedNode.
     * @param toBeAssertedNode
     *            The node that shall be asserted.
     * @return Returns an array containing the object id, the href (if any), and
     *         the title (if any) of the referenced element. In case of SOAP,
     *         the returned href and title values are <code>null</code>.
     * @throws Exception
     *             If anything fails.
     */
    public String[] assertModifiedBy(
        final String message, final Node expectedNode,
        final Node toBeAssertedNode) throws Exception {

        final String msg = prepareAssertionFailedMessage(message);

        return assertReferencingElement(msg, expectedNode, toBeAssertedNode,
            XPATH_MODIFIED_BY, Constants.USER_ACCOUNT_BASE_URI);
    }

    /**
     * Gets the objid value of the created-by element.<br/>
     * This method takes the set transport definition for this instance to
     * decide if the objid value has to be extracted from the href attribute of
     * the created-by element (REST) or can be taken from the objid attribute of
     * the created-by element (SOAP).
     * 
     * @param node
     *            The node to select an element from.
     * @return Returns the objid value.
     * @throws Exception
     *             If anything fails.
     */
    public String getCreatedByObjidValue(final Node node) throws Exception {

        return getCreatedByObjidValue(getTransport(), node);
    }

    /**
     * Gets the objid value of the created-by element.<br/>
     * This method takes the provided transport definition to decide if the
     * objid value has to be extracted from the href attribute of the created-by
     * element (REST) or can be taken from the objid attribute of the created-by
     * element (SOAP).
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param node
     *            The node to select an element from.
     * @return Returns the objid value.
     * @throws Exception
     *             If anything fails.
     */
    public static String getCreatedByObjidValue(
        final int transport, final Node node) throws Exception {

        return getObjidValue(transport, node, "//" + NAME_CREATED_BY);
    }

    /**
     * Gets the objid value of the modified-by element.<br/>
     * This method takes the set transport definition for this instance to
     * decide if the objid value has to be extracted from the href attribute of
     * the modified-by element (REST) or can be taken from the objid attribute
     * of the modified-by element (SOAP).
     * 
     * @param node
     *            The node to select an element from.
     * @return Returns the objid value.
     * @throws Exception
     *             If anything fails.
     */
    public String getModifiedByObjidValue(final Node node) throws Exception {

        return getModifiedByObjidValue(getTransport(), node);
    }

    /**
     * Gets the objid value of the modified-by element.<br/>
     * This method takes the provided transport definition to decide if the
     * objid value has to be extracted from the href attribute of the modified
     * element (REST) or can be taken from the objid attribute of the modified
     * element (SOAP).
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param node
     *            The node to select an element from.
     * @return Returns the objid value.
     * @throws Exception
     *             If anything fails.
     */
    public static String getModifiedByObjidValue(
        final int transport, final Node node) throws Exception {

        return getObjidValue(transport, node, "//" + NAME_MODIFIED_BY);
    }

    /**
     * Fixes the attributes of the selected "link" nodes for usage with the SOAP
     * interface. <br/>
     * This method takes the set transport definition for this instance to
     * decide if nothing has to be done (REST) or if the xlink attributes have
     * to be removed and an appropriate objid attribute has to be added (SOAP).
     * 
     * @param document
     *            The document from which the nodes shall be selected and fixed.
     * @param xpath
     *            The xpath expression to select the nodes that shall be fixed.
     * @return Returns the provided document.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public Document fixLinkAttributes(
        final Document document, final String xpath) throws Exception {

        return fixSoapLinkAttributes(getTransport(), document, xpath);
    }

    /**
     * Fixes the attributes of the selected "namespace" nodes for usage with the
     * SOAP interface. <br/>
     * This method takes the set transport definition for this instance to
     * decide if nothing has to be done (REST) or if the xlink namespace and
     * xml:base have to be removed.
     * 
     * @param document
     *            The document from which the nodes shall be selected and fixed.
     * @param xpath
     *            The xpath expression to select the nodes that shall be fixed.
     * @return Returns the provided document.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public Document fixNamespaceAttributes(
        final Document document, final String xpath) throws Exception {

        return fixSoapNamespaceAttributes(getTransport(), document, xpath);
    }

    /**
     * Fixes the attributes of the selected "link" nodes for usage with the SOAP
     * interface. <br/>
     * This method takes the provided transport definition to decide if nothing
     * has to be done (REST) or if the xlink attributes have to be removed and
     * an appropriate objid attribute has to be added (SOAP).
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param document
     *            The document from which the nodes shall be selected and fixed.
     * @param xpath
     *            The xpath expression to select the nodes that shall be fixed.
     * @return Returns the provided document.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public static Document fixSoapLinkAttributes(
        final int transport, final Document document, final String xpath)
        throws Exception {

        assertTransport(transport);

        if (Constants.TRANSPORT_REST == transport) {
            return document;
        }

        final String hrefXpath = xpath + PART_XLINK_HREF;
        NodeList hrefNodes = selectNodeList(document, hrefXpath);
        int length = hrefNodes.getLength();
        for (int i = 0; i < length; i++) {
            final Node hrefNode = hrefNodes.item(i);
            final String objid = getObjidFromHref(hrefNode.getTextContent());
            addAttribute(document, xpath + "[" + (i + 1) + "]",
                createAttributeNode(document, null, null, NAME_OBJID, objid));
        }
        deleteNodes(document, hrefXpath);
        deleteNodes(document, xpath + PART_XLINK_TITLE);
        deleteNodes(document, xpath + PART_XLINK_TYPE);
        return document;
    }

    /**
     * Fixes the attributes of the selected "namespace" nodes for usage with the
     * SOAP interface. <br/>
     * This method takes the provided transport definition to decide if nothing
     * has to be done (REST) or if the xlink namespace and xml:base have to be
     * removed.
     * 
     * @param transport
     *            Specifies the transport, must be one of
     *            {@link Constants.TRANSPORT_REST} or
     *            {@link Constants.TRANSPORT_SOAP}.
     * @param document
     *            The document from which the nodes shall be selected and fixed.
     * @param xpath
     *            The xpath expression to select the nodes that shall be fixed.
     * @return Returns the provided document.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public static Document fixSoapNamespaceAttributes(
        final int transport, final Document document, final String xpath)
        throws Exception {

        assertTransport(transport);

        if (Constants.TRANSPORT_REST == transport) {
            return document;
        }

        deleteNodes(document, xpath + PART_XML_BASE);
        deleteNodes(document, xpath + PART_XLINK_NS);
        return document;
    }

    // /**
    // * Serialize the given Dom Object to a String. If the parameter
    // * <code>namespacePrefixPrefix</code> is specified, its value is added to
    // * the existing prefixes to change the namespace prefixes.
    // *
    // * @param xml
    // * The Xml Node to serialize.
    // * @param omitXMLDeclaration
    // * Indicates if XML declaration will be omitted.
    // * @param namespacePrefixPrefix
    // * @return The String representation of the Xml Node.
    // * @throws Exception
    // * If anything fails.
    // */
    // public static String toString(final Node xml,
    // final boolean omitXMLDeclaration,
    // final String namespacePrefixPrefix)
    // throws Exception {
    //
    // String ret = toString(xml, omitXMLDeclaration);
    //
    // if (namespacePrefixPrefix != null) {
    //
    // ret = ret.replaceAll(currentNamespacePrefix + ":",
    // namespacePrefix + ":");
    // ret = ret.replaceAll("xmlns:" + currentNamespacePrefix,
    // "xmlns:" + namespacePrefix);
    // }
    //
    // return ret;
    // }

    /**
     * Asserts that a container holding a list of references is as expected.
     * 
     * @param message
     *            The assertion failed message.
     * @param expected
     *            The node in that the element shall be selected and used to
     *            check it against the toBeAsserted node. This parameetr may be
     *            <code>null</code>. In this case, it is only asserted that the
     *            container holds valid references.
     * @param toBeAsserted
     *            The node in that the element shall be selected and asserted.
     * @param xpath
     *            The xpath to the container holding the references in the
     *            provided nodes, e.g. /organizational-unit/parent-ous in case
     *            of a document holding an OU representation.
     * @param childrenName
     *            The name of the referencing elements in the container, e.g.
     *            parent-ou.
     * @param hrefBaseOfContainer
     *            The href base of the containier holding the references. If
     *            this is <code>null</code>, the container's xlink attributes
     *            (in case of REST) are not checked.
     * @param isRoot
     *            The flag indicating if the container element is a root element
     *            (<code>true</code>) or not. If it is expected to be a root
     *            element, it is asserted using the
     *            <code>assertRootElement</code> method. If it is not a root
     *            element, only xlink attributes are asserted, if the parameter
     *            <code>hrefBaseOfContainer</code> is provided.
     * @param timestampBeforeLastMod
     *            A timestap before the last modification, used to assert the
     *            root element in case of <code>isRoot</code> is
     *            <code>true</code>.
     * @param hrefBaseOfReferenced
     *            The href base of the references objects, e.g.
     *            /oum/organizational-unit if the container holds references to
     *            organizational units.
     * @param sorted
     *            The flag indicating if the list is expected to be sorted (
     *            <code>true</code>) or not.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public void assertListOfReferences(
        final String message, final Node expected, final Node toBeAsserted,
        final String xpath, final String childrenName,
        final String hrefBaseOfContainer, final boolean isRoot,
        final String timestampBeforeLastMod, final String hrefBaseOfReferenced,
        final boolean sorted) throws Exception {

        final String msg = prepareAssertionFailedMessage(message);

        final Node toBeAssertedContainerNode =
            selectSingleNode(toBeAsserted, xpath);

        if (toBeAssertedContainerNode == null) {
            // if (expected != null && selectSingleNode(expected, xpath) !=
            // null) {
            fail(msg + "Missing container node.");
            // }
            // return;
        }

        // assert root element, if it is one
        if (isRoot) {
            assertRootElement(msg, toBeAssertedContainerNode, ".",
                hrefBaseOfContainer, timestampBeforeLastMod);
        }
        // assert xlink values if hrefBaseOfContainer provided
        else if (hrefBaseOfContainer != null) {
            assertXlinkElement(msg, toBeAssertedContainerNode, ".",
                hrefBaseOfContainer);
        }

        final String xpathChildren = childrenName;
        final NodeList toBeAssertedNodes =
            selectNodeList(toBeAssertedContainerNode, xpathChildren);
        final int length = toBeAssertedNodes.getLength();
        // assertTrue(msg + "Empty container element. ", length > 0);

        if (expected == null) {
            for (int i = 0; i < length; i++) {
                final Node toBeAssertedNode = toBeAssertedNodes.item(i);
                assertReferencingElement(msg, toBeAssertedNode, "/",
                    hrefBaseOfReferenced);
            }
        }
        else {
            final Node expectedContainerNode =
                selectSingleNode(expected, xpath);
            if (expectedContainerNode == null) {
                if (length > 0) {
                    assertNull(msg + "Unexpected node. "
                        + toBeAssertedContainerNode, toBeAssertedContainerNode);
                }
                return;
            }

            NodeList expectedNodes =
                selectNodeList(expectedContainerNode, xpathChildren);
            assertEquals(msg + "Number of children mismatch. ",
                expectedNodes.getLength(), length);

            Map<String, Node> expectedMap = new HashMap<String, Node>(length);
            if (!sorted) {
                // list is not sorted, map is build to find corresponding
                // nodes in expected list.
                for (int i = 0; i < length; i++) {
                    final Node expectedNode = expectedNodes.item(i);
                    final String objid = getObjidValue(expectedNode, "");
                    expectedMap.put(objid, expectedNode);
                }
            }
            for (int i = 0; i < length; i++) {
                final Node toBeAssertedNode = toBeAssertedNodes.item(i);
                final String objid = getObjidValue(toBeAssertedNode, "");
                final Node expectedNode;
                if (sorted) {
                    expectedNode = expectedNodes.item(i);
                }
                else {
                    expectedNode = expectedMap.get(objid);
                    assertNotNull(msg + "Unexpected reference [" + i + ", "
                        + objid + "]", expectedNode);
                }
                assertReferencingElement(msg, expectedNode, toBeAssertedNode,
                    ".", hrefBaseOfReferenced);

                // remove asserted from the map
                expectedMap.remove(objid);
            }
        }
    }

    /**
     * Asserts that the node selected from the provided node by the provided
     * xpath is an RDF Description element as expected.
     * 
     * @param msg
     *            The error message.
     * @param toBeAsserted
     *            The node to be asserted.
     * @param xPath
     *            The xpath to select the RDF Description Element to be
     *            asserted.
     * @param expectedRdfResource
     *            The expected rdf resource. If this is <code>null</code>,
     *            asserting the resource value is skipped.
     * @param expectedRdfAboutBaseUri
     *            The expected base of the rdf about. If this is
     *            <code>null</code>, asserting the base value is skipped.
     * @param expectedRdfAbout
     *            The expected rdf about. If this is <code>null</code>,
     *            asserting the about value is skipped.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public void assertRDFDescription(
        final String msg, final Node toBeAsserted, final String xPath,
        final String expectedRdfResource, final String expectedRdfAboutBaseUri,
        final String expectedRdfAbout) throws Exception {

        final String xpathRdfAbout = xPath + "/@about";
        assertXmlExists("Missing rdf:about", toBeAsserted, xpathRdfAbout);
        final String xpathRdfType = xPath + "/type";
        assertXmlExists("Missing rdf:type", toBeAsserted, xpathRdfType);
        final String xpathRdfResource = xPath + "/type/@resource";
        assertXmlExists("Missing rdf:resource", toBeAsserted, xpathRdfResource);

        if (expectedRdfResource != null) {
            assertXmlEquals(msg + " Unexpected rdf:resource.", toBeAsserted,
                xpathRdfResource, expectedRdfResource);
        }
        if (expectedRdfAboutBaseUri != null) {
            final String about =
                selectSingleNode(toBeAsserted, xpathRdfAbout).getTextContent();
            assertTrue("rdf:about does not start with expected base uri ["
                + expectedRdfAboutBaseUri + "," + about + "]",
                about.startsWith(expectedRdfAboutBaseUri));
        }
        if (expectedRdfAbout != null) {
            assertXmlEquals(msg + " Unexpected rdf:about.", toBeAsserted,
                xpathRdfAbout, expectedRdfAbout);
        }
    }

    /**
     * Asserts the description nodes of the provided document. The description
     * node of the provided node are selected using the XPATH_RDF_DESCRIPTION.
     * 
     * @param toBeAssertedNode
     *            The Node to be asserted.
     * @param expectedRdfResource
     *            The expected rdf resource. If this is null, asserting the
     *            resource value is skipped.
     * 
     * @throws Exception
     * @throws TransformerException
     */
    public void assertRdfDescriptions(
        final Node toBeAssertedNode, final String expectedRdfResource)
        throws Exception, TransformerException {

        final NodeList descriptionNodes =
            selectNodeList(toBeAssertedNode, XPATH_RDF_DESCRIPTION);
        for (int i = 0; i < descriptionNodes.getLength(); i++) {
            final Node descriptionNode = descriptionNodes.item(i);
            assertRDFDescription("Asserting " + (i + 1)
                + ".st description element failed. ", descriptionNode, ".",
                expectedRdfResource, null, null);

            // assert no duplicates
            final String about =
                selectSingleNodeAsserted(descriptionNode, "@about")
                    .getTextContent();
            final NodeList nodeList =
                selectNodeList(toBeAssertedNode, XPATH_RDF_DESCRIPTION
                    + "[@about = '" + about + "']");
            assertEquals("Duplicate description detected [" + about + "]", 1,
                nodeList.getLength());

        }
    }

    /**
     * Asserts if only the values provided in the set occur in the elements
     * defined by the xpath-expression.
     * 
     * @param xml
     *            xml.
     * @param xpath
     *            xpath-expression
     * @param values
     *            the values that may occur
     * @param getIdAttribute
     *            if true: get the id-attribute of the element dependent of the
     *            transport
     * 
     * @throws Exception
     *             e
     */
    public void assertAllowedXpathValues(
        final String xml, final String xpath, final List<String> values,
        final boolean getIdAttribute) throws Exception {

        String replacedXpath = xpath;
        if (getIdAttribute) {
            if (getTransport() == Constants.TRANSPORT_REST) {
                replacedXpath += PART_XLINK_HREF;
            }
            else {
                replacedXpath += PART_OBJID;
            }
        }
        NodeList nodes =
            selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                replacedXpath);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String nodeValue = node.getNodeValue();
            if (nodeValue == null) {
                fail("nodeValue for xpath " + replacedXpath + " is null");
            }
            if (getIdAttribute && getTransport() == Constants.TRANSPORT_REST) {
                nodeValue = getObjidFromHref(nodeValue);
            }
            if (!values.contains(nodeValue)) {
                fail(replacedXpath + " contains " + node.getNodeValue());
            }
        }
    }

    /**
     * Asserts if the elements requested by the xPath are sorted correctly.
     * 
     * @param xml
     *            xml.
     * @param xpath
     *            xpath-expression
     * @param prerequisiteXpath
     *            prerequisite xpath-expression
     * @param isAscending
     *            if sort-order is ascending
     * @param getIdAttribute
     *            if true: get the id-attribute of the element dependent of the
     *            transport
     * 
     * @throws Exception
     *             e
     */
    public void assertSorted(
        final String xml, final String xpath, final String prerequisiteXpath,
        final boolean isAscending, final boolean getIdAttribute)
        throws Exception {

        String replacedXpath = xpath;
        if (getIdAttribute) {
            if (getTransport() == Constants.TRANSPORT_REST) {
                replacedXpath += PART_XLINK_HREF;
            }
            else {
                replacedXpath += PART_OBJID;
            }
        }
        NodeList nodes =
            selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                replacedXpath);
        String lastValue = "";
        if (!isAscending) {
            lastValue = "ZZZZZZZZZZZZZZZZZZZZZ";
        }
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (prerequisiteXpath != null) {
                NodeList prerequisite =
                    XPathAPI.selectNodeList(node, prerequisiteXpath);
                if (prerequisite == null || prerequisite.getLength() == 0) {
                    continue;
                }
            }
            String nodeValue = node.getNodeValue();
            if (nodeValue == null) {
                fail("nodeValue for xpath " + replacedXpath + " is null");
            }
            if (getIdAttribute && getTransport() == Constants.TRANSPORT_REST) {
                nodeValue = getObjidFromHref(nodeValue);
            }
            if (isAscending) {
                if (nodeValue.compareToIgnoreCase(lastValue) < 0) {
                    fail(nodeValue + " is not sorted correctly");
                }
                else if (nodeValue.compareTo(lastValue) > 0) {
                    lastValue = nodeValue;
                }
            }
            else {
                if (nodeValue.compareToIgnoreCase(lastValue) > 0) {
                    fail(nodeValue + " is not sorted correctly");
                }
                else if (nodeValue.compareTo(lastValue) < 0) {
                    lastValue = nodeValue;
                }
            }
        }
    }

    /**
     * Asserts the number of nodesreturned by the given xpath-expression .
     * 
     * @param xml
     *            xml.
     * @param xpath
     *            xpath-expression
     * @param count
     *            expected nodeCount
     * 
     * @throws Exception
     *             e
     */
    public void assertNodeCount(
        final String xml, final String xpath, final int count) throws Exception {

        NodeList nodes =
            selectNodeList(EscidocRestSoapTestBase.getDocument(xml), xpath);
        assertEquals(count, nodes.getLength());
    }

    /**
     * Retrieve the stack trace from the provided exception and returns it in a
     * <code>String</code>.
     * 
     * @param e
     *            The exception to retrieve the stack trace from.
     * @return Returns the stack trace in a <code>String</code>.
     */
    public static String getStackTrace(final Exception e) {

        StringWriter writer = new StringWriter();
        PrintWriter printwriter = new PrintWriter(writer);
        e.printStackTrace(printwriter);
        return writer.toString();
    }

    /**
     * Asserts that the provided xml data is valid for a staging file.
     * 
     * @param toBeAsserted
     *            The xml data to be asserted.
     * @throws Exception
     *             If anything fails.
     */
    public static void assertXmlValidStagingFile(final String toBeAsserted)
        throws Exception {

        if (EscidocTestBase.stagingFileSchema == null) {
            EscidocTestBase.stagingFileSchema =
                EscidocTestBase.getSchema("http://" + Constants.HOST_PORT
                    + "/xsd/rest/staging-file/0.3/staging-file.xsd");
        }
        EscidocTestBase.assertXmlValid(toBeAsserted,
            EscidocTestBase.stagingFileSchema);
    }

    /**
     * Retrieve a Template as a String.
     * 
     * @param path
     *            The Path of the Template.
     * @return The String representation of the Template.
     * @throws Exception
     *             If anything fails.
     */
    public static String getTemplateAsString(final URL path) throws Exception {
        InputStream is = null;
        StringBuffer response = new StringBuffer();

        // Open Connection to given resource
        HttpURLConnection con = (HttpURLConnection) path.openConnection();

        // Set request-method and timeout
        con.setRequestMethod("GET");

        // Request
        is = con.getInputStream();

        // Read response
        String currentLine = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ((currentLine = br.readLine()) != null) {
            response.append(currentLine + "\n");
        }
        is.close();
        return response.toString();

    }

    /**
     * Loading template from example directory.
     * 
     * @param filename
     *            The name of the file.
     * @return The file content as String.
     * 
     * @throws IOException
     *             Thrown if the file with the provided name was not found under
     *             the location of example files or if reading the file content
     *             to String failed.
     */
    public String getExampleTemplate(final String filename) throws Exception {

        File f =
            getTemplatePath(TEMPLATE_EXAMPLE_PATH + "/" + getTransport(false),
                filename);
        InputStream fis = new FileInputStream(f);
        return ResourceProvider.getContentsFromInputStream(fis);
    }

    /**
     * Retrieve a Template as a String.
     * 
     * @param path
     *            The Path of the Template.
     * @param templateName
     *            The name of the template.
     * @return The String representation of the Template.
     * @throws Exception
     *             If anything fails.
     */
    public static String getTemplateAsString(
        final String path, final String templateName) throws Exception {

        // obtain path to templates
        File f = getTemplatePath(path, templateName);

        // get content from template file
        String template =
            ResourceProvider.getContentsFromInputStream(new FileInputStream(f));

        // replace URLs if they don't fit to the current framework config
        PropertiesProvider prop = new PropertiesProvider();

        String fedoraTemplateUrl = "http://localhost:8082/fedora";
        String fedoraUrl = prop.getProperty(PropertiesProvider.FEDORA_URL);

        if (fedoraUrl != null && !fedoraTemplateUrl.equals(fedoraUrl)) {
            template = template.replace(fedoraTemplateUrl, fedoraUrl);
        }

        return template;
    }

    /**
     * Obtain the real path to template/file.
     * 
     * @param path
     * @param templateName
     * @return File handler
     * @throws Exception
     *             Thrown if file is not readable.
     */
    private static File getTemplatePath(
        final String path, final String templateName) throws Exception {

        final String className = "EscidocRestSoapTestBase.class";
        URL url = EscidocRestSoapTestBase.class.getResource(className);

        int pos = url.getPath().indexOf("de/escidoc/core/test/" + className);
        String tempPath =
            url.getPath().substring(0, pos) + "../../src/test/resources/"
                + path + "/" + templateName;

        File f = new File(tempPath);
        if (!f.canRead()) {
            throw new Exception("Cannot read '" + tempPath + "'");
        }

        return f;
    }

    /**
     * Retrieve a Template as a Document.<br>
     * The used parser is NOT namespace aware!
     * 
     * @param path
     *            The Path of the Template.
     * @return The String representation of the Template.
     * @throws Exception
     *             If anything fails.
     */
    public static Document getTemplateAsDocument(final URL path)
        throws Exception {

        return getDocument(getTemplateAsString(path));
    }

    /**
     * Retrieve a Template as a Document.<br>
     * The used parser is NOT namespace aware!
     * 
     * @param path
     *            The Path of the Template.
     * @param templateName
     *            The name of the template.
     * @return The String representation of the Template.
     * @throws Exception
     *             If anything fails.
     */
    public static Document getTemplateAsDocument(
        final String path, final String templateName) throws Exception {

        return getDocument(getTemplateAsString(path, templateName));
    }

    /**
     * Parse the given xml InputStream into a Document.<br>
     * This is NOT done namespace aware!
     * 
     * @param xml
     *            The xml String.
     * @return The Document.
     * @throws Exception
     *             If anything fails.
     */
    public static Document getDocument(final InputStream xml) throws Exception {

        Document result = null;
        DocumentBuilderFactory docBuilderFactory =
            DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        result = docBuilder.parse(xml);
        result.getDocumentElement().normalize();
        xml.close();
        return result;
    }

    /**
     * Parse the given xml String into a Document.<br>
     * This is NOT done namespace aware!
     * 
     * @param xml
     *            The xml String.
     * @return The Document.
     * @throws Exception
     *             If anything fails.
     */
    public static Document getDocument(final String xml) throws Exception {

        return getDocument(xml, true);
    }

    /**
     * Parse the given xml String into a Document.<br>
     * This is NOT done namespace aware!
     * 
     * @param xml
     *            The xml String.
     * @param failOnParseError
     *            A flag indicating if method shall fail in case of a parse
     *            error (<code>true</code>) or if the parse exception shall be
     *            thrown (<code>false</code>).
     * @return The Document.
     * @throws Exception
     *             If anything fails.
     */
    public static Document getDocument(
        final String xml, final boolean failOnParseError) throws Exception {

        assertNotNull("Can't create a document without provided XML data. ",
            xml);

        // TODO re work of encoding settings
        String charset = EscidocTestBase.DEFAULT_CHARSET;
        Document result = null;
        DocumentBuilderFactory docBuilderFactory =
            DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        try {
            result =
                docBuilder
                    .parse(new ByteArrayInputStream(xml.getBytes(charset)));
        }
        catch (SAXException e) {
            if (failOnParseError) {
                final StringBuffer errorMsg = new StringBuffer("XML invalid. ");
                errorMsg.append(e.getMessage());
                if (EscidocTestBase.log.isDebugEnabled()) {
                    EscidocTestBase.log.debug(errorMsg.toString());
                    EscidocTestBase.log.debug(xml);
                    EscidocTestBase.log
                        .debug("============ End of invalid xml ============");
                    appendStackTrace(errorMsg, e);
                }
                fail(errorMsg.toString());
            }
            else {
                throw e;
            }
        }
        result.getDocumentElement().normalize();
        return result;
    }

    /**
     * Fails due to an unexpected exception.
     * 
     * @param e
     *            The unexpected exception.
     */
    public static void failException(final Exception e) {
        failException("Unexpected exception. ", e);
    }

    /**
     * Fails due to an unexpected exception.
     * 
     * @param message
     *            The message.
     * @param e
     *            The unexpected exception.
     */
    public static void failException(final String message, final Exception e) {
        log.error("test failed due to an unexpected exception: " + e);

        StringBuffer msg =
            new StringBuffer(
                EscidocTestBase.prepareAssertionFailedMessage(message));
        msg.append("Exception: ");
        msg.append(e.getClass().getName());
        appendStackTrace(msg, e);
        fail(msg.toString());
    }

    /**
     * Fails due to a missing exception.
     * 
     * @param exceptionClass
     *            The expected exception type.
     */
    public static void failMissingException(final Class exceptionClass) {
        failMissingException("Missing exception. ", exceptionClass);
    }

    /**
     * Fails due to a missing exception.
     * 
     * @param message
     *            The message.
     * @param exceptionClass
     *            The expected exception type.
     */
    public static void failMissingException(
        final String message, final Class exceptionClass) {

        StringBuffer msg = new StringBuffer(message);
        msg.append(" Missing expected exception: ");
        msg.append(exceptionClass.getName());
        fail(msg.toString());
    }

    /**
     * Asserts that the exception is of expected type<br>
     * This method compares the provided ecpected class with the class of the
     * provided exception.
     * 
     * @param message
     *            The message printed in case of failed assertion.
     * @param expectedClass
     *            The expected type.
     * @param e
     *            The exception to be asserted.
     */
    public static void assertExceptionType(
        final String message, final Class expectedClass, final Exception e) {

        if (!e.getClass().equals(expectedClass)) {
            StringBuffer msg = new StringBuffer(message);
            msg.append(" expected:<");
            msg.append(expectedClass.getName());
            msg.append("> but was:<");
            msg.append(e.getClass().getName());
            msg.append(">");
            appendStackTrace(msg, e);
            fail(msg.toString());
        }
    }

    /**
     * Asserts that the exception is of expected type<br>
     * This method compares the provided ecpected class with the class of the
     * provided exception.
     * 
     * @param expectedClass
     *            The expected type.
     * @param e
     *            The exception to be asserted.
     */
    public static void assertExceptionType(
        final Class expectedClass, final Exception e) {

        assertExceptionType("Unexpected exception, ", expectedClass, e);
    }

    /**
     * Adds the stack trace to the provided string buffer, if debug logging
     * level is enabled.
     * 
     * @param msg
     *            The StringBuffer to append the stack trace to.
     * @param e
     *            The exception for that the stack trace shall be appended.
     */
    private static void appendStackTrace(
        final StringBuffer msg, final Exception e) {

        if (log.isDebugEnabled()) {
            msg.append("\n");
            msg.append(getStackTrace(e));
        }
    }

    /**
     * Gets the xml representation of a filter criteria with the specified name
     * and value.
     * 
     * @param filterName
     *            The name of the filter.
     * @param filterValue
     *            The value of the filter. if this is <code>null</code>, an
     *            empty filter element is returned.
     * @return Returns the filter element.
     */
    public static String getFilter(
        final String filterName, final String filterValue) {

        if (filterValue == null) {
            return "<filter name=\"" + filterName + "\"/>";
        }
        else {
            return "<filter name=\"" + filterName + "\">" + filterValue
                + "</filter>";
        }
    }

    /**
     * Gets the xml representation of a order-by specification with the
     * specified name and sorting.
     * 
     * @param orderByPredicate
     *            The name of the predicate used for ordering.
     * @param ascending
     *            Flag indicating if ascending (<code>true</code>) or descendinf
     *            (<code>false</code>) sorting shall be performed.
     * @return Returns the order-by element.
     */
    public static String getOrderBy(
        final String orderByPredicate, final boolean ascending) {

        if (ascending) {
            return "<order-by sorting=\"ascending\">" + orderByPredicate
                + "</order-by>";
        }
        else {
            return "<order-by sorting=\"descending\">" + orderByPredicate
                + "</order-by>";
        }
    }

    /**
     * Gets the xml representation of an offset specification with the specified
     * value.
     * 
     * @param offset
     *            The offset value.
     * @return Returns the offset element.
     */
    public static String getOffset(final int offset) {

        return "<offset>" + offset + "</offset>";
    }

    /**
     * Gets the xml representation of a limit specification with the specified
     * value.
     * 
     * @param limit
     *            The limit value.
     * @return Returns the limit element.
     */
    public static String getLimit(final int limit) {

        return "<limit>" + limit + "</limit>";
    }

    /**
     * Get the status of the Object (Item/Container).
     * 
     * @param objid
     *            The object Id.
     * @return Status of the Object.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public String getObjectStatus(final String objid) throws Exception {

        String xpathPublicStatus = "/[item|container]/properties/status";

        String objXml = retrieve(objid);
        Document objDoc = EscidocRestSoapTestBase.getDocument(objXml);
        Node statusNode = selectSingleNode(objDoc, xpathPublicStatus);

        return statusNode.getTextContent();
    }

    /**
     * Select the Version Number from the object Identifier.
     * 
     * @param objid
     *            The object Id.
     * @return The version number as String or null if no number could be
     *         recognized.
     */
    public String getVersionNumber(final String objid) {
        String version = null;
        Matcher m = PATTERN_VERSION_NUMBER.matcher(objid);
        if (m.find()) {
            version = m.group(1);
        }
        return version;
    }

    /**
     * replaces the id of the root-element from given xml with given primKey.
     * 
     * @param xml
     *            String xml
     * @param primKey
     *            String primKey
     * @return Returns the replacedXml.
     */
    public String replacePrimKey(final String xml, final String primKey) {
        String replacedXml = null;
        if (getTransport() == Constants.TRANSPORT_SOAP) {
            replacedXml =
                xml.replaceFirst("(?s)(.*?objid=\").*?(\".*)", "$1" + primKey
                    + "$2");
        }
        else {
            replacedXml =
                xml.replaceFirst("(?s)(.*?href=\"[^\"]*/).*?(\".*)", "$1"
                    + primKey + "$2");
        }
        return replacedXml;
    }

    /**
     * Test logging in an user.
     * 
     * @param loginName
     *            The login name of the user.
     * @param password
     *            The password of the user.
     * @param encodeTargetUrlSlashes
     *            Flag indicating that the slashes contained in the targetUrl
     *            shall be encoded (<code>true</code>) or shall not be encoded (
     *            <code>false</code>).
     * 
     * @return The eSciDoc user handle.
     * @throws Exception
     *             If anything fails.
     */
    protected String login(
        final String loginName, final String password,
        final boolean encodeTargetUrlSlashes) throws Exception {

        UserManagementWrapperClient userManagementWrapperClient =
            new UserManagementWrapperClient(getTransport());
        HttpResponse result =
            userManagementWrapperClient.login(loginName, password, false,
                false, "http://www.fiz-karlsruhe.de", encodeTargetUrlSlashes);

        assertHttpStatus("", HttpServletResponse.SC_SEE_OTHER, result);
        assertNotNull(result.getFirstHeader("Location"));
        assertNotNull(result.getFirstHeader("Set-Cookie"));

        String userHandleFromRedirectUrl = null;
        String userHandleFromCookie = null;
        Header[] headers = result.getAllHeaders();
        for (int i = 0; i < headers.length; ++i) {
            if ("Location".equals(headers[i].getName())) {
                String locationHeaderValue =
                    result.getFirstHeader("Location").getValue();
                int index = locationHeaderValue.indexOf('=');
                userHandleFromRedirectUrl =
                    new String(Base64.decode(locationHeaderValue.substring(
                        index + 1, locationHeaderValue.length())));
            }
            else if ("Set-Cookie".equals(headers[i].getName())) {
                String setCookieHeaderValue =
                    result.getFirstHeader("Set-Cookie").getValue();
                int index = setCookieHeaderValue.indexOf("escidocCookie=");
                String value =
                    setCookieHeaderValue.substring(index
                        + "escidocCookie=".length());
                index = value.indexOf(';');
                userHandleFromCookie = value.substring(0, index);

            }
        }
        assertNotNull("No handle from redirect URL", userHandleFromRedirectUrl);
        assertNotNull("No handle from cookie", userHandleFromCookie);
        assertEquals("Handle mismatch in cookie and URL", userHandleFromCookie,
            userHandleFromRedirectUrl);

        return userHandleFromCookie;
    }
}
