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
package de.escidoc.core.test.common.client.servlet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.xerces.dom.AttrImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xpath.XPathAPI;
import org.esidoc.core.utils.io.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import de.escidoc.core.common.exceptions.remote.EscidocException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.EscidocTestBase;
import de.escidoc.core.test.common.client.servlet.invocation.exceptions.MethodNotFoundException;
import de.escidoc.core.test.common.resources.PropertiesProvider;
import de.escidoc.core.test.common.resources.ResourceProvider;

/**
 * Base class for access to the escidoc REST interface.
 * 
 * @author Michael Schneider
 */
public abstract class ClientBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientBase.class);

    public static final String METHOD_ADD_CONTENT_RELATIONS = "addContentRelations";

    public static final String METHOD_REMOVE_CONTENT_RELATIONS = "removeContentRelations";

    public static final String METHOD_CREATE = "create";

    public static final String METHOD_DELETE = "delete";

    public static final String METHOD_RETRIEVE = "retrieve";

    public static final String METHOD_RETRIEVE_CURRENT_USER = "retrieveCurrentUser";

    public static final String METHOD_UPDATE = "update";

    public static final String METHOD_UPDATE_PASSWORD = "updatePassword";

    public static final String METHOD_CREATE_COMPONENT = "createComponent";

    public static final String METHOD_DELETE_COMPONENT = "deleteComponent";

    public static final String METHOD_RETRIEVE_COMPONENT = "retrieveComponent";

    public static final String METHOD_UPDATE_COMPONENT = "updateComponent";

    public static final String METHOD_RETRIEVE_COMPONENTS = "retrieveComponents";

    public static final String METHOD_RETRIEVE_COMPONENT_PROPERTIES = "retrieveComponentProperties";

    public static final String METHOD_INSERT_CONTAINERS = "insertContainers";

    public static final String METHOD_RETRIEVE_CONTAINERS = "retrieveContainers";

    public static final String METHOD_RETRIEVE_CONTAINER_REFS = "retrieveContainerRefs";

    public static final String METHOD_UPDATE_CONTAINERS = "updateContainers";

    public static final String METHOD_RETRIEVE_CONTENT = "retrieveContent";

    public static final String METHOD_RETRIEVE_CONTEXTS = "retrieveContexts";

    public static final String METHOD_RETRIEVE_SCHEMA = "retrieveSchema";

    public static final String METHOD_ADD_TOCS = "addTocs";

    public static final String METHOD_ADD_MEMBERS = "addMembers";

    public static final String METHOD_REMOVE_MEMBERS = "removeMembers";

    public static final String METHOD_CREATE_ITEM = "createItem";

    public static final String METHOD_CREATE_CONTAINER = "createContainer";

    public static final String METHOD_CLOSE = "close";

    public static final String METHOD_OPEN = "open";

    public static final String METHOD_SPO = "spo";

    public static final String METHOD_EXTRACT = "extract";

    public static final String METHOD_RETRIEVE_GENRE = "retrieveGenre";

    public static final String METHOD_INSERT_ITEMS = "insertItems";

    public static final String METHOD_RETRIEVE_ITEM_LIST = "retrieveItemList";

    public static final String METHOD_RETRIEVE_ITEMS = "retrieveItems";

    public static final String METHOD_RETRIEVE_ITEM_REFS = "retrieveItemRefs";

    public static final String METHOD_UPDATE_ITEMS = "updateItems";

    public static final String METHOD_RETRIEVE_LICENSE_TYPES = "retrieveLicenseTypes";

    public static final String METHOD_RETRIEVE_ADMIN_DESCRIPTORS = "retrieveAdminDescriptors";

    public static final String METHOD_RETRIEVE_MEMBERS = "retrieveMembers";

    public static final String METHOD_RETRIEVE_MEMBER_REFS = "retrieveMemberRefs";

    public static final String METHOD_RETRIEVE_MD_RECORD = "retrieveMdRecord";

    public static final String METHOD_UPDATE_MD_RECORD = "updateMdRecord";

    public static final String METHOD_RETRIEVE_SET_DEFINITIONS = "retrieveSetDefinitions";

    public static final String METHOD_RETRIEVE_ORGANIZATIONAL_UNITS = "retrieveOrganizationalUnits";

    public static final String METHOD_RETRIEVE_ORGANIZATIONAL_UNIT_REFS = "retrieveOrganizationalUnitRefs";

    public static final String METHOD_RETRIEVE_ORGANIZATIONAL_UNIT_PATH_LIST = "retrievePathList";

    public static final String METHOD_RETRIEVE_ORGANIZATIONAL_UNIT_SUCCESSORS = "retrieveSuccessors";

    public static final String METHOD_UPDATE_ORGANIZATIONAL_UNITS = "updateOrganizationalUnits";

    public static final String METHOD_RETRIEVE_ADMINDESCRIPTOR = "retrieveAdminDescriptor";

    public static final String METHOD_RETRIEVE_AGGREGATION_DEFINITIONS = "retrieveAggregationDefinitions";

    public static final String METHOD_RETRIEVE_REPORT_DEFINITIONS = "retrieveReportDefinitions";

    public static final String METHOD_RETRIEVE_SCOPES = "retrieveScopes";

    public static final String METHOD_PREPROCESS_STATISTICS = "preprocess";

    public static final String METHOD_RETRIEVE_PROPERTIES = "retrieveProperties";

    public static final String METHOD_RETRIEVE_MD_RECORDS = "retrieveMdRecords";

    public static final String METHOD_RETRIEVE_CONTENT_STREAMS = "retrieveContentStreams";

    public static final String METHOD_RETRIEVE_CONTENT_STREAM_CONTENT = "retrieveContentStreamContent";

    public static final String METHOD_RETRIEVE_CONTENT_STREAM = "retrieveContentStream";

    public static final String METHOD_UPDATE_MD_RECORDS = "updateMdRecords";

    public static final String METHOD_UPDATE_PARENTS = "updateParents";

    public static final String METHOD_UPDATE_PROPERTIES = "updateProperties";

    public static final String METHOD_RETRIEVE_RESOURCE = "retrieveResource";

    public static final String METHOD_RETRIEVE_RESOURCES = "retrieveResources";

    public static final String METHOD_RETRIEVE_ROLES = "retrieveRoles";

    /*
     * Sub-resource method for retrieving the relations of a particular resource (item, container ...).
     */
    public static final String METHOD_RETRIEVE_RELATIONS = "retrieveRelations";

    /*
     * Filter-method of ContentModelHandler for retrieving list of content models.
     */
    public static final String METHOD_RETRIEVE_CONTENT_MODELS = "retrieveContentModels";

    /*
     * Filter-method of ContentRelationHandler for retrieving list of content relations.
     */
    public static final String METHOD_RETRIEVE_CONTENT_RELATIONS = "retrieveContentRelations";

    public static final String METHOD_RETRIEVE_STRUCT_MAP = "retrieveStructMap";

    public static final String METHOD_CREATE_TOC = "createToc";

    public static final String METHOD_DELETE_TOC = "deleteToc";

    public static final String METHOD_RETRIEVE_TOC = "retrieveToc";

    public static final String METHOD_RETRIEVE_TOC_VIEW = "retrieveTocView";

    public static final String METHOD_RETRIEVE_TOCS = "retrieveTocs";

    public static final String METHOD_RETRIEVE_USER_ACCOUNTS = "retrieveUserAccounts";

    public static final String METHOD_RETRIEVE_USER_GROUPS = "retrieveUserGroups";

    public static final String METHOD_RETRIEVE_USER_GROUP_SELECTORS = "addSelectors";

    public static final String METHOD_ADD_USER_GROUP_SELECTORS = "retrieveSelectors";

    public static final String METHOD_REMOVE_USER_GROUP_SELECTORS = "removeSelectors";

    public static final String METHOD_RETRIEVE_METS = "retrieveMetss";

    public static final String METHOD_RETRIEVE_VERSION_HISTORY = "retrieveVersionHistory";

    public static final String METHOD_CREATE_UNSECURED_ACTIONS = "createUnsecuredActions";

    public static final String METHOD_DELETE_UNSECURED_ACTIONS = "deleteUnsecuredActions";

    public static final String METHOD_RETRIEVE_UNSECURED_ACTIONS = "retrieveUnsecuredActions";

    public static final String METHOD_UPDATE_TOC = "updateToc";

    public static final String METHOD_UPDATE_TOCS = "updateTocs";

    public static final String METHOD_VALIDATE = "validate";

    public static final String METHOD_RELEASE = "release";

    public static final String METHOD_REVISE = "revise";

    public static final String METHOD_ASSIGN_VERSION_PID = "assignVersionPid";

    public static final String METHOD_ASSIGN_OBJECT_PID = "assignObjectPid";

    public static final String METHOD_ASSIGN_CONTENT_PID = "assignContentPid";

    public static final String METHOD_SUBMIT = "submit";

    public static final String METHOD_WITHDRAW = "withdraw";

    public static final String METHOD_LOCK = "lock";

    public static final String METHOD_UNLOCK = "unlock";

    public static final String METHOD_MOVE_TO_CONTEXT = "moveToContext";

    public static final String METHOD_SEARCH = "search";

    public static final String METHOD_EXPLAIN = "explain";

    public static final String METHOD_SCAN = "scan";

    public static final String METHOD_ACTIVATE = "activate";

    public static final String METHOD_DEACTIVATE = "deactivate";

    public static final String METHOD_RETRIEVE_CURRENT_GRANTS = "retrieveCurrentGrants";

    public static final String METHOD_RETRIEVE_PREFERENCES = "retrievePreferences";

    public static final String METHOD_RETRIEVE_PREFERENCE = "retrievePreference";

    public static final String METHOD_CREATE_PREFERENCE = "createPreference";

    public static final String METHOD_DELETE_PREFERENCE = "deletePreference";

    public static final String METHOD_UPDATE_PREFERENCES = "updatePreferences";

    public static final String METHOD_UPDATE_PREFERENCE = "updatePreference";

    public static final String METHOD_RETRIEVE_ATTRIBUTES = "retrieveAttributes";

    public static final String METHOD_RETRIEVE_NAMED_ATTRIBUTES = "retrieveNamedAttributes";

    public static final String METHOD_RETRIEVE_ATTRIBUTE = "retrieveAttribute";

    public static final String METHOD_CREATE_ATTRIBUTE = "createAttribute";

    public static final String METHOD_UPDATE_ATTRIBUTE = "updateAttribute";

    public static final String METHOD_DELETE_ATTRIBUTE = "deleteAttribute";

    public static final String METHOD_CREATE_GRANT = "createGrant";

    public static final String METHOD_RETRIEVE_GRANT = "retrieveGrant";

    public static final String METHOD_ADD_SELECTORS = "addSelectors";

    public static final String METHOD_REMOVE_SELECTORS = "removeSelectors";

    public static final String METHOD_REVOKE_GRANT = "revokeGrant";

    public static final String METHOD_REVOKE_GRANTS = "revokeGrants";

    public static final String METHOD_RETRIEVE_GRANTS = "retrieveGrants";

    public static final String METHOD_RETRIEVE_PARENT_OBJECTS = "retrieveParentObjects";

    public static final String METHOD_RETRIEVE_PARENTS = "retrieveParents";

    public static final String METHOD_RETRIEVE_CHILD_OBJECTS = "retrieveChildObjects";

    public static final String METHOD_LOGIN = "login";

    public static final String METHOD_LOGOUT = "logout";

    public static final String METHOD_EVALUATE = "evaluate";

    /**
     * Ingest methods.
     */
    public static final String METHOD_INGEST_GET_CONFIGURATION = "getConfiguration";

    public static final String METHOD_INGEST_SET_CONFIGURATION = "setConfiguration";

    public static final String METHOD_INGEST_ITEM = "ingestItem";

    public static final String METHOD_INGEST_CONTAINER = "ingestContainer";

    public static final String METHOD_INGEST_GET_NEXT_OBJID = "getNextObjids";

    public static final String METHOD_INGEST_LOCKED_LIST = "lockedList";

    public static final String METHOD_INGEST_COMMIT = "commit";

    /**
     * End Ingest methods.
     */

    /**
     * Admin tool methods.
     */
    public static final String METHOD_GET_REPOSITORY_INFO = "getRepositoryInfo";

    public static final String METHOD_GET_INDEX_CONFIGURATION = "getIndexConfiguration";

    public static final String METHOD_DELETE_OBJECTS = "deleteObjects";

    public static final String METHOD_GET_PURGE_STATUS = "getPurgeStatus";

    public static final String METHOD_LOAD_EXAMPLES = "load-examples";

    public static final String METHOD_REINDEX = "reindex";

    /**
     * End admin tool methods.
     */

    /**
     * Indicating method needs no xml data.
     */
    public static final String NOXML = "method without xml data parameter";

    public static final String DEFAULT_CHARSET = "UTF-8";

    public static final String TEMPLATE_OM_COMMON_PATH = "/templates" + "/om/template" + "/common";

    private static final Pattern PATTERN_VERSION_NUMBER = Pattern.compile("[a-zA-Z]+:[a-zA-Z0-9]+:([0-9]+)");

    private static final Pattern PATTERN_ID_WITHOUT_VERSION = Pattern.compile("([a-zA-Z]+:[0-9]+):[0-9]+");

    private static final Pattern PATTERN_OBJID_ATTRIBUTE = Pattern.compile(".*\\/([^\"\\/]*)");

    private DefaultHttpClient httpClient;

    /**
     * Constructor for client base class.
     */
    public ClientBase() {
        initHttpClient();
    }

    private void initHttpClient() {
        final HttpParams httpParams = new BasicHttpParams();
        ConnManagerParams.setMaxTotalConnections(httpParams, 90);
        final ConnPerRouteBean connPerRoute = new ConnPerRouteBean(30);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, connPerRoute);
        final Scheme httpSchema = new Scheme("http", PlainSocketFactory.getSocketFactory(), 80);
        final SchemeRegistry schemaRegistry = new SchemeRegistry();
        schemaRegistry.register(httpSchema);
        final ClientConnectionManager clientConnectionManager =
            new ThreadSafeClientConnManager(httpParams, schemaRegistry);
        this.httpClient = new DefaultHttpClient(clientConnectionManager, httpParams);
        // disable cookies
        /*
         * this.httpClient.removeRequestInterceptorByClass(RequestAddCookies.class );
         * this.httpClient.removeResponseInterceptorByClass(ResponseProcessCookies .class);
         */
        // disable redirects
        this.httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        this.httpClient.getParams().setParameter("http.protocol.handle-redirects", Boolean.FALSE);
        this.httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, this.getHttpHost());
    }

    private HttpHost getHttpHost() {
        HttpHost httpHost = null;
        try {
            if (PropertiesProvider.getInstance().getProperty("http.proxyHost") != null
                && PropertiesProvider.getInstance().getProperty("http.proxyPort") != null) {
                httpHost =
                    new HttpHost(PropertiesProvider.getInstance().getProperty("http.proxyHost"), Integer
                        .parseInt(PropertiesProvider.getInstance().getProperty("http.proxyPort")));

            }
        }
        catch (final Exception e) {
            throw new RuntimeException("[ClientBase] Error occured loading properties! " + e.getMessage(), e);
        }
        return httpHost;
    }

    /**
     * Make a service call to the escidoc framework for methods without sending an xml representation of a resource.
     * 
     * @param label
     *            A label for logging purposes.
     * @param soapMethod
     *            The soap method.
     * @param HttpResponse
     *            The http method.
     * @param httpBaseUri
     *            The base uri.
     * @param pathElements
     *            The <code>String[]</code> array containing the elements describing the path of the addressed (sub)
     *            resource:
     *            <ul>
     *            <li>pathElements[0] = resourceId</li>
     *            <li>pathElements[1] = subResourceName</li>
     *            <li>pathElements[2] = subResourceId</li>
     *            <li>pathElements[3] = subSubResourceName</li>
     *            <li>pathElements[4] = subSubResourceId</li>
     *            <li>...</li>
     *            </ul>
     * @param parameters
     *            The parameters for an HTTP GET request.
     * @return The HttpResponse after the service call .
     * @throws Exception
     *             If the service call fails.
     */
    protected Object callEsciDoc(
        final String label, final String soapMethod, final String HttpResponse, final String httpBaseUri,
        final String[] pathElements, final Map<String, String[]> parameters) throws Exception {

        return doCallEscidoc(label, soapMethod, HttpResponse, httpBaseUri, pathElements, NOXML, null, null, parameters);
    }

    /**
     * Make a service call to the escidoc framework for methods without sending an xml representation of a resource.
     * 
     * @param label
     *            A label for logging purposes.
     * @param soapMethod
     *            The soap method.
     * @param HttpResponse
     *            The http method.
     * @param httpBaseUri
     *            The base uri.
     * @param pathElements
     *            The <code>String[]</code> array containing the elements describing the path of the addressed (sub)
     *            resource:
     *            <ul>
     *            <li>pathElements[0] = resourceId</li>
     *            <li>pathElements[1] = subResourceName</li>
     *            <li>pathElements[2] = subResourceId</li>
     *            <li>pathElements[3] = subSubResourceName</li>
     *            <li>pathElements[4] = subSubResourceId</li>
     *            <li>...</li>
     *            </ul>
     * @return The HttpResponse after the service call .
     * @throws Exception
     *             If the service call fails.
     */
    protected Object callEsciDoc(
        final String label, final String soapMethod, final String HttpResponse, final String httpBaseUri,
        final String[] pathElements) throws Exception {

        return doCallEscidoc(label, soapMethod, HttpResponse, httpBaseUri, pathElements, NOXML, null, null, null);
    }

    /**
     * Make a service call to the escidoc framework.
     * 
     * @param label
     *            A label for logging purposes.
     * @param soapMethod
     *            The soap method. The http method.
     * @param httpBaseUri
     *            The base uri.
     * @param pathElements
     *            The <code>String[]</code> array containing the elements describing the path of the addressed (sub)
     *            resource:
     *            <ul>
     *            <li>pathElements[0] = resourceId</li>
     *            <li>pathElements[1] = subResourceName</li>
     *            <li>pathElements[2] = subResourceId</li>
     *            <li>pathElements[3] = subSubResourceName</li>
     *            <li>pathElements[4] = subSubResourceId</li>
     *            <li>...</li>
     *            </ul>
     * @param xml
     *            The xml representaion of a new or updated framework object.
     * @return The HttpResponse after the service call .
     * @throws Exception
     *             If the service call fails.
     */
    protected Object callEsciDoc(
        final String label, final String soapMethod, final String httpMethod, final String httpBaseUri,
        final String[] pathElements, final Object xml) throws Exception {

        return doCallEscidoc(label, soapMethod, httpMethod, httpBaseUri, pathElements, xml, null, null, null);
    }

    /**
     * Call eSciDoc with parameter string, without XML.
     * 
     * @param label
     *            A label for logging purposes.
     * @param soapMethod
     *            The soap method.
     * @param HttpResponse
     *            The http method.
     * @param httpBaseUri
     *            The base uri.
     * @param pathElements
     *            The <code>String[]</code> array containing the elements describing the path of the addressed (sub)
     *            resource:
     *            <ul>
     *            <li>pathElements[0] = resourceId</li>
     *            <li>pathElements[1] = subResourceName</li>
     *            <li>pathElements[2] = subResourceId</li>
     *            <li>pathElements[3] = subSubResourceName</li>
     *            <li>pathElements[4] = subSubResourceId</li>
     *            <li>...</li>
     *            </ul>
     * @return The HttpResponse after the service call .
     * @throws Exception
     *             If the service call fails.
     */
    protected Object callEsciDoc(
        final String label, final String soapMethod, final String HttpResponse, final String httpBaseUri,
        final String parameter, final String[] pathElements) throws Exception {

        return doCallEscidoc(label, soapMethod, HttpResponse, httpBaseUri, pathElements, parameter, NOXML, null, null,
            null);
    }

    /**
     * Make a service call to the escidoc framework.
     * 
     * @param label
     *            A label for logging purposes.
     * @param soapMethod
     *            The soap method.
     * @param HttpResponse
     *            The http method.
     * @param httpBaseUri
     *            The base uri.
     * @param pathElements
     *            TThe <code>String[]</code> array containing the elements describing the path of the addressed (sub)
     *            resource:
     *            <ul>
     *            <li>pathElements[0] = resourceId</li>
     *            <li>pathElements[1] = subResourceName</li>
     *            <li>pathElements[2] = subResourceId</li>
     *            <li>pathElements[3] = subSubResourceName</li>
     *            <li>pathElements[4] = subSubResourceId</li>
     *            <li>...</li>
     *            </ul>
     * @param binaryContent
     *            The binary content of the body.
     * @param mimeType
     *            The mime type of the data.
     * @param filename
     *            The name of the file.
     * @return The HttpResponse after the service call .
     * @throws Exception
     *             If the service call fails.
     */
    protected Object callEsciDocWithBinaryContent(
        final String label, final String soapMethod, final String HttpResponse, final String httpBaseUri,
        final String[] pathElements, final InputStream binaryContent, final String mimeType, final String filename)
        throws Exception {

        return doCallEscidoc(label, soapMethod, HttpResponse, httpBaseUri, pathElements, binaryContent, mimeType,
            filename, null);
    }

    /**
     * Worker method wrapper to call eSciDoc. This is called by one of the callEsciDoc methods.
     * 
     * @param label
     *            A label for logging purposes.
     * @param soapMethod
     *            The soap method. The http method.
     * @param httpBaseUri
     *            The base URI (REST).
     * @param pathElements
     *            The elements describing the path to the (sub) resource: id, subresourceName1, subresourceId2, ...
     * @param body
     *            An <code>Object</code> holding the content of the body. Currently, String and InputStream are
     *            supported.
     * @param mimeType
     *            The mime type of the data, in case of binary content.
     * @param filename
     *            The name of the file, in case of binary content.
     * @param parameters
     *            The request parameters for HTTP GET.
     * @return The HttpResponse after the service call .
     * @throws Exception
     *             If the service call fails.
     */
    private Object doCallEscidoc(
        final String label, final String soapMethod, final String httpMethod, final String httpBaseUri,
        final String[] pathElements, final Object body, final String mimeType, final String filename,
        final Map<String, String[]> parameters) throws Exception {

        return doCallEscidoc(label, soapMethod, httpMethod, httpBaseUri, pathElements, null, body, mimeType, filename,
            parameters);
    }

    /**
     * Worker method to call eSciDoc. This is called by one of the callEsciDoc methods.
     * 
     * @param label
     *            A label for logging purposes.
     * @param soapMethod
     *            The soap method. The http method.
     * @param httpBaseUri
     *            The base URI (REST).
     * @param pathElements
     *            The elements describing the path to the (sub) resource: id, subresourceName1, subresourceId2, ...
     * @param parameter
     *            The HTTP parameter (GET)
     * @param body
     *            An <code>Object</code> holding the content of the body. Currently, String and InputStream are
     *            supported.
     * @param mimeType
     *            The mime type of the data, in case of binary content.
     * @param filename
     *            The name of the file, in case of binary content.
     * @param parameters
     *            The request parameters for HTTP GET.
     * @return The HttpResponse after the service call .
     * @throws Exception
     *             If the service call fails.
     */
    private Object doCallEscidoc(
        final String label, final String soapMethod, final String httpMethod, final String httpBaseUri,
        final String[] pathElements, final String parameter, final Object body, final String mimeType,
        final String filename, final Map<String, String[]> parameters) throws Exception {
        Object result = null;
        String httpUrl =
            HttpHelper.createUrl(Constants.PROTOCOL, EscidocTestBase.getFrameworkHost() + ":"
                + EscidocTestBase.getFrameworkPort(), httpBaseUri, pathElements, parameter, false);
        logRestServiceCall(label, httpMethod, httpUrl, body);
        if (NOXML.equals(body)) {
            result = HttpHelper.executeHttpRequest(getHttpClient(), httpMethod, httpUrl, null, mimeType, parameters);
        }
        else {
            result = HttpHelper.executeHttpRequest(getHttpClient(), httpMethod, httpUrl, body, mimeType, parameters);
        }
        if (((HttpResponse) result).getStatusLine().getStatusCode() >= HttpServletResponse.SC_MULTIPLE_CHOICES) {
            throwCorrespondingException((HttpResponse) result);
        }
        return result;
    }

    /**
     * This method handles a detected error contained in the provided result. <br>
     * This method examines the error response of the body of the result and constructs the corresponding remote
     * exception.
     * 
     * @param result
     *            The http result object describing the error.
     * @throws Exception
     *             In any case.
     */
    private void throwCorrespondingException(final HttpResponse result) throws Exception {

        String exceptionXML = ResourceProvider.getContentsFromInputStream(result.getEntity().getContent());

        // try to parse the body that may contain XML representation of an
        // eScidoc exception.
        Document exceptionDocument = null;
        try {
            exceptionDocument = EscidocAbstractTest.getDocument(exceptionXML, false);
        }
        catch (final Exception e) {
            // parsing failed, does not seem to be a known exception
            throw new EscidocException(result.getStatusLine().getStatusCode(),
                result.getStatusLine().getReasonPhrase(), "Unknown (unparseable) error response" + "\nBody:\n"
                    + exceptionXML);
        }

        Node exceptionNameNode = EscidocTestBase.selectSingleNode(exceptionDocument, "/exception/class/p");
        if (exceptionNameNode == null) {
            throw new Exception("Missing exception name node in response body:\n" + exceptionXML);
        }
        String exceptionName = exceptionNameNode.getTextContent();
        if (exceptionName == null) {
            throw new Exception("Exception could not be identified from response body:\n" + exceptionXML);
        }
        Header exceptionHeader = result.getFirstHeader("eSciDocException");
        assertNotNull("Missing eSciDocException header. ", exceptionHeader);
        assertEquals("Exception name mismatch in response header and response body.", exceptionHeader.getValue(),
            exceptionName);

        Object exceptionObject;
        if (exceptionName.equals("de.escidoc.core.common.servlet.invocation" + ".exceptions.MethodNotFoundException")) {
            exceptionObject = new MethodNotFoundException();
        }
        else {
            exceptionName = exceptionName.replaceAll("application\\.", "remote.application.");
            exceptionName = exceptionName.replaceAll("system\\.", "remote.system.");

            Class<?> exceptionClass;
            try {
                exceptionClass = Class.forName(exceptionName);
            }
            catch (final ClassNotFoundException e) {
                throw new Exception("No class found for identified exception" + " received from eSciDoc ["
                    + exceptionName + ", " + (result).getStatusLine().getReasonPhrase() + "]\n Body:\n" + exceptionXML,
                    e);
            }

            exceptionObject = exceptionClass.newInstance();
            if (exceptionObject == null || !(exceptionObject instanceof EscidocException)) {
                throw new Exception("Exception class could not be instantiated [" + exceptionName + ", "
                    + (result).getStatusLine().getReasonPhrase() + "], instantiated exception object is "
                    + exceptionObject + "\n Body:\n" + exceptionXML);
            }
            ((EscidocException) exceptionObject).setHttpStatusCode(result.getStatusLine().getStatusCode());
            ((EscidocException) exceptionObject).setHttpStatusLine(result.getStatusLine().getReasonPhrase());

            Node exceptionMessageNode = EscidocTestBase.selectSingleNode(exceptionDocument, "/exception/message/p");
            if (exceptionMessageNode != null) {
                String exceptionMessage = exceptionMessageNode.getTextContent();

                ((EscidocException) exceptionObject).setHttpStatusMsg(exceptionMessage);
            }
        }
        throw (Exception) exceptionObject;
    }

    /**
     * Log the execution of an rest service call.
     * 
     * @param method
     *            The executed resource method.
     * @param HttpResponse
     *            The executed http method.
     * @param url
     *            The url.
     * @param body
     *            The body (if the http method (POST or PUT) permits a body).
     */
    protected void logRestServiceCall(
        final String method, final String HttpResponse, final String url, final Object body) {
        String message =
            '[' + method + "] Calling eSciDoc with URL='" + url + "' and http method='"
                + HttpResponse.toUpperCase(Locale.ENGLISH) + "'";
        // if ((Constants.HTTP_METHOD_POST.equals(HttpResponse.toUpperCase()))
        // || (Constants.HTTP_METHOD_PUT.equals(HttpResponse.toUpperCase()))) {
        // message += " body='" + body + "'";
        // }
        LOGGER.debug(message);
    }

    /**
     * @return Returns the httpClient.
     */
    public DefaultHttpClient getHttpClient() {
        return this.httpClient;
    }

    /**
     * Make a String from the input.
     * 
     * @param input
     *            The input (maybe an InputStream or a String)
     * @return The input as String.
     * @throws IOException
     *             If reading from InputStream fails.
     */
    protected String changeToString(final Object input) throws IOException {

        String result = null;
        if (input instanceof InputStream) {
            result = ResourceProvider.getContentsFromInputStream((InputStream) input);
        }
        else if (input instanceof String) {
            result = (String) input;
        }
        return result;
    }

    /**
     * Gets the last-modification-date attribute of the root element from the document.
     * 
     * @param document
     *            The document to retrieve the value from.
     * @return Returns the attribute value.
     * @throws Exception
     *             If anything fails.
     */
    public static String getLastModificationDateValue(final Document document) throws Exception {

        return getRootElementAttributeValue(document, "last-modification-date");
    }

    /**
     * Retrieves the namespace from the given class.
     * 
     * @param clazz
     *            The class.
     * @return Returns the namespace
     */
    public static String getNamespace(final Class<?> clazz) {

        String[] parts = clazz.getName().split("\\.");
        return parts[parts.length - 1];
    }

    /**
     * Retrieves the namespace URI from the given class.<br>
     * The URI is generated using the package name. If the class is a remote application exception, the "remote" part is
     * removed from the package name.
     * 
     * @param clazz
     *            The class.
     * @return Returns the namespace uRI retrieved from the name of the package of the class.
     */
    public static String getNamespaceUri(final Class<?> clazz) {

        String packageName = clazz.getPackage().getName();
        packageName = packageName.replaceAll("exceptions\\.remote\\.application", "exceptions.application");
        packageName = packageName.replaceAll("\\.remote\\.", ".");
        StringBuffer ret = new StringBuffer("http://");
        String[] parts = packageName.split("\\.");

        for (int i = parts.length - 1; i > 0; i--) {
            ret.append(parts[i] + ".");
        }
        ret.append(parts[0]);

        return ret.toString();
    }

    public Object create(final Object xml) throws Exception {
        return null;
    }

    public Object retrieve(final String id) throws Exception {
        return null;
    }

    public Object update(final String id, final Object xml) throws Exception {
        return null;
    }

    public Object delete(final String id) throws Exception {
        return null;
    }

    /**
     * Deliver configured values for PID behavior.
     * 
     * @param var
     *            name of properties parameter.
     * @param defValue
     *            Default value if the parameter is not set.
     * @return The boolean expression if the property parameter is set to true or the parameter is missing and the
     *         default values is set to true. False otherwise.
     */
    public final Boolean getPidConfig(final String var, final String defValue) {
        return (Boolean.valueOf(PropertiesProvider.getInstance().getProperty(var, defValue)));
    }

    /**
     * Create a Param structure for PID assignments. The last-modification-date is retrieved from the by id selected
     * object.
     * 
     * @param id
     *            The object Id
     * @param url
     *            URL of the resource (not checked)
     * @return param XML snippet.
     * @throws Exception
     *             Thrown if getTheLastModificationDate() fails.
     */
    protected final String getPidParam(final String id, final String url) throws Exception {

        String param =
            "<param last-modification-date=\"" + getTheLastModificationDate(id) + "\"><url>" + url + "</url></param>";
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
    protected final String getPidParam2(final String lstModDate, final String url) {
        String param = "<param last-modification-date=\"" + lstModDate + "\"><url>" + url + "</url></param>";
        return (param);
    }

    /**
     * Returns the xml data of the provided result.
     * 
     * @param result
     *            The object holding the result.
     * @return Returns the xml string.
     * @throws Exception
     *             If anything fails.
     */
    protected String handleXmlResult(final Object result) throws Exception {

        String xmlResult = null;
        if (result instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result;
            assertHttpStatusOfMethod("", httpRes);
            assertContentTypeTextXmlUTF8OfMethod("", httpRes);
            xmlResult = getResponseBodyAsUTF8(httpRes);
        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }

    /**
     * Assert that the http request was successful.
     * 
     * @param message
     *            The message printed if assertion fails.
     */
    public static void assertHttpStatusOfMethod(final String message, final HttpResponse httpRes) {

        // httpDelete
        if (httpRes.getStatusLine().getStatusCode() == HttpServletResponse.SC_NO_CONTENT) {
            assertHttpStatus(message, HttpServletResponse.SC_NO_CONTENT, httpRes);
            // other httpMethods
        }
        else if (httpRes.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK) {
            assertHttpStatus(message, HttpServletResponse.SC_OK, httpRes);
        }

    }

    /**
     * Assert that the http response has the expected status.
     * 
     * @param message
     *            The message printed if assertion fails.
     * @param expectedStatus
     *            The expected status.
     */
    public static void assertHttpStatus(final String message, final int expectedStatus, final HttpResponse httpRes) {
        assertEquals(message + " Wrong response status!", expectedStatus, httpRes.getStatusLine().getStatusCode());
    }

    /**
     * Assert that the http request was successful.
     * 
     * @param message
     *            The message printed if assertion fails.
     */
    public static void assertContentTypeTextXmlUTF8OfMethod(final String message, final HttpResponse httpRes) {
        assertContentType(message, MimeTypes.TEXT_XML, "utf-8", httpRes);
    }

    public static void assertContentType(
        final String message, final String expectedContentType, final String expectedCharset, final HttpResponse httpRes) {
        Header[] headers = httpRes.getAllHeaders();
        String contentTypeHeaderValue = null;
        for (int i = 0; i < headers.length && contentTypeHeaderValue == null; ++i) {
            if (headers[i].getName().toLowerCase(Locale.ENGLISH).equals("content-type")) {
                contentTypeHeaderValue = headers[i].getValue();
            }
        }
        assertNotNull("No content-type header found, but expected 'content-type=" + expectedContentType + ";"
            + expectedCharset + "'", contentTypeHeaderValue);
        assertTrue("Wrong content-type found, expected '" + expectedContentType + "' but was '"
            + contentTypeHeaderValue + "'", contentTypeHeaderValue.indexOf(expectedContentType) > -1);
        assertTrue("Wrong charset found, expected '" + expectedCharset + "' but was '" + contentTypeHeaderValue + "'",
            contentTypeHeaderValue.indexOf(expectedContentType) > -1);
    }

    /**
     * Get the response body as an String encoded with UTF-8.
     * 
     * @return The response body.
     * @throws UnsupportedEncodingException
     *             If UTF-8 is not supported.
     * @throws IOException
     *             If the response body is not valid.
     */
    protected String getResponseBodyAsUTF8(final HttpResponse httpRes) throws UnsupportedEncodingException, IOException {

        return EntityUtils.toString(httpRes.getEntity(), HTTP.UTF_8);
    }

    /**
     * Return the child of the node selected by the xPath.
     * 
     * @param node
     *            The node.
     * @param xPath
     *            The xPath.
     * @return The child of the node selected by the xPath.
     * @throws TransformerException
     *             If anything fails.
     */
    public static Node selectSingleNode(final Node node, final String xPath) throws TransformerException {

        Node result = XPathAPI.selectSingleNode(node, xPath);
        return result;
    }

    /**
     * Remove version informaion from given objid.
     * 
     * @param objid
     *            The objid.
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
     * Select the Version Number from the object Identifier.
     * 
     * @param objid
     *            The object Id.
     * @return The version number as String or null if no number could be recognized.
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
     * Extract ID from href.
     * 
     * @param val
     *            href
     * @return id
     */
    public String getIdFromHrefValue(final String val) {
        String result = null;

        Matcher m1 = PATTERN_OBJID_ATTRIBUTE.matcher(val);
        if (m1.find()) {
            result = m1.group(1);
        }
        return result;
    }

    /**
     * Gets the last modification date from the Resource.
     * 
     * @param id
     *            The id of the Resource.
     * @return last-modification-date
     * @throws Exception
     *             Thrown if anything fails.
     */
    public String getTheLastModificationDate(final String id) throws Exception {
        Document resource = EscidocAbstractTest.getDocument(handleXmlResult(retrieve(id)));

        // get last-modification-date
        NamedNodeMap atts = resource.getDocumentElement().getAttributes();
        Node lastModificationDateNode = atts.getNamedItem("last-modification-date");
        return (lastModificationDateNode.getNodeValue());
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
    public String getLatestVersionObjidValue(final Document document) throws Exception {
        return getIdFromHrefValue(selectSingleNode(document, "//properties/version/@href").getTextContent());
    }

    /**
     * Handles the result of a base service access.
     * 
     * @param result
     *            The result to handle.
     * @return Returns the xml response.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public String handleResult(final Object result) throws Exception {

        String xmlResult = null;
        if (result instanceof HttpResponse) {
            HttpResponse method = (HttpResponse) result;
            xmlResult = getResponseBodyAsUTF8(method);
            assertHttpStatusOfMethod("", method);
        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }

    /**
     * Handles the binary result of a base service access.
     * 
     * @param result
     *            The result to handle.
     * @return Returns response InputStream.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public InputStream handleBinaryResult(final Object result) throws Exception {

        InputStream ins = null;
        if (result instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result;
            ins = httpRes.getEntity().getContent();
        }
        return ins;
    }

    /**
     * Gets the root element of the provided document.
     * 
     * @param doc
     *            The document to get the root element from.
     * @return Returns the first child of the document htat is an element node.
     * @throws Exception
     *             If anything fails.
     */
    public static Element getRootElement(final Document doc) throws Exception {

        Node node = doc.getFirstChild();
        while (node != null) {
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) node;
            }
            node = node.getNextSibling();
        }
        return null;
    }

    /**
     * Assert that the Element/Attribute selected by the xPath exists.
     * 
     * @param message
     *            The message printed if assertion fails.
     * @param node
     *            The Node.
     * @param xPath
     *            The xPath.
     * @throws Exception
     *             If anything fails.
     */
    public static void assertXmlExists(final String message, final Node node, final String xPath) throws Exception {

        NodeList nodes = XPathAPI.selectNodeList(node, xPath);
        assertTrue(message, nodes.getLength() > 0);
    }

    /**
     * Gets the value of the specified attribute of the root element from the document.
     * 
     * @param document
     *            The document to retrieve the value from.
     * @param attributeName
     *            The name of the attribute whose value shall be retrieved.
     * @return Returns the attribute value.
     * @throws Exception
     *             If anything fails.
     */
    public static String getRootElementAttributeValue(final Document document, final String attributeName)
        throws Exception {

        Node root = getRootElement(document);

        // has not been parsed namespace aware.
        String xPath;
        if (attributeName.startsWith("@")) {
            xPath = "/*/" + attributeName;
        }
        else {
            xPath = "/*/@" + attributeName;
        }
        assertXmlExists("Attribute not found [" + attributeName + "]. ", document, xPath);
        final Node attr = selectSingleNode(root, xPath);
        assertNotNull("Attribute not found [" + attributeName + "]. ", attr);
        String value = attr.getTextContent();
        return value;
    }

    /**
     * Get the task param including the last-modification-date.
     * 
     * @param timestamp
     *            If not null the last-modification-date is set to timestamp.
     * @return The task param.
     * @throws Exception
     *             If anything fails.
     */
    public static String getTaskParam(final String timestamp) throws Exception {
        String result = null;

        if (timestamp != null) {
            final Node document =
                substitute(EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_OM_COMMON_PATH,
                    "task_param_last_modification_date.xml"), "/param/@last-modification-date", timestamp);
            result = toString(document, false);
        }
        else {
            result =
                EscidocAbstractTest.getTemplateAsString(TEMPLATE_OM_COMMON_PATH,
                    "task_param_last_modification_date.xml");
        }
        return result;
    }

    /**
     * Serialize the given Dom Object to a String.
     * 
     * @param xml
     *            The Xml Node to serialize.
     * @param omitXMLDeclaration
     *            Indicates if XML declaration will be omitted.
     * @return The String representation of the Xml Node.
     * @throws Exception
     *             If anything fails.
     */
    public static String toString(final Node xml, final boolean omitXMLDeclaration) throws Exception {

        String result = new String();
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
            format.setEncoding(DEFAULT_CHARSET);
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
            lsOutput.setEncoding(DEFAULT_CHARSET);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            lsOutput.setByteStream(os);
            LSSerializer writer = impl.createLSSerializer();
            // result = writer.writeToString(xml);
            writer.write(xml, lsOutput);
            result = ((ByteArrayOutputStream) lsOutput.getByteStream()).toString(DEFAULT_CHARSET);
            if ((omitXMLDeclaration) && (result.indexOf("?>") != -1)) {
                result = result.substring(result.indexOf("?>") + 2);
            }
            // result = toString(getDocument(writer.writeToString(xml)),
            // true);
        }
        return result;
    }

    /**
     * Serialize the given InputStream to a String.
     * 
     * @param inputStream
     *            intutStream
     * @return The String representation of the InputStream.
     * @throws Exception
     *             If anything fails.
     */
    public static String toString(final InputStream inputStream) throws Exception {

        ByteArrayOutputStream out = null;
        String contentString = null;
        try {
            out = new ByteArrayOutputStream();
            if (inputStream != null) {
                byte[] bytes = new byte[0xFFFF];
                int i = -1;
                while ((i = inputStream.read(bytes)) > -1) {
                    out.write(bytes, 0, i);
                }
                out.flush();
                contentString = new String(out.toByteArray(), HttpHelper.HTTP_DEFAULT_CHARSET);
            }
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (final IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                }
                catch (final IOException e) {
                }
            }
        }
        return contentString;
    }

    /**
     * Serialize the given InputStream to a byte[].
     * 
     * @param inputStream
     *            inputStream
     * @return The byte[] representation of the InputStream.
     * @throws Exception
     *             If anything fails.
     */
    public static byte[] toByteArray(final InputStream inputStream) throws Exception {

        ByteArrayOutputStream out = null;
        byte[] returnBytes = null;
        try {
            out = new ByteArrayOutputStream();
            if (inputStream != null) {
                byte[] bytes = new byte[0xFFFF];
                int i = -1;
                while ((i = inputStream.read(bytes)) > -1) {
                    out.write(bytes, 0, i);
                }
                out.flush();
                returnBytes = out.toByteArray();
            }
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (final IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                }
                catch (final IOException e) {
                }
            }
        }
        return returnBytes;
    }

    /**
     * Substitute the element selected by the xPath in the given node with the new value.
     * 
     * @param node
     *            The node.
     * @param xPath
     *            The xPath.
     * @param newValue
     *            The newValue.
     * @return The resulting node after the substitution.
     * @throws Exception
     *             If anything fails.
     */
    public static Node substitute(final Node node, final String xPath, final String newValue) throws Exception {
        Node result = node;
        Node replace = selectSingleNode(result, xPath);
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
        return result;
    }

}
