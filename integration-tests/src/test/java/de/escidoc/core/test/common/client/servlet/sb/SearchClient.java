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
package de.escidoc.core.test.common.client.servlet.sb;

import de.escidoc.core.test.common.client.servlet.ClientBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.HttpHelper;
import gov.loc.www.zing.srw.ExplainRequestType;
import gov.loc.www.zing.srw.ExplainResponseType;
import gov.loc.www.zing.srw.ScanRequestType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.service.ExplainPort;
import gov.loc.www.zing.srw.service.SRWPort;
import gov.loc.www.zing.srw.service.SRWSampleServiceLocator;
import org.apache.axis.types.PositiveInteger;
import org.apache.axis.types.URI;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Offers access methods to the escidoc REST and SOAP interface of the Search
 * resource. (SRW-Server)
 * 
 * @author MIH
 * 
 */
public class SearchClient extends ClientBase {

    private SRWPort searchClient = null;

    private ExplainPort explainClient = null;

    /**
     * 
     * @param transport
     *            The transport identifier.
     */
    public SearchClient(final int transport) {
        super(transport);

    }

    /**
     * Retrieve srw search response.
     * 
     * @param parameters
     *            The http-parameters as HashMap.
     * @param database
     *            database where search is executed.
     * @return The HttpMethod after the service call (REST) or the result object
     *         (SOAP).
     * @throws Exception
     *             If the service call fails.
     */
    public Object search(
        final HashMap<String, String> parameters, final String database)
        throws Exception {

        StringBuffer paramString = new StringBuffer("?");
        for (String key : parameters.keySet()) {
            if (paramString.length() > 1) {
                paramString.append("&");
            }
            String value = parameters.get(key);
            if (getTransport() == Constants.TRANSPORT_REST) {
                value =
                    URLEncoder.encode(value, HttpHelper.HTTP_DEFAULT_CHARSET);
            }
            paramString.append(key).append("=").append(value);
        }
        parameters.put("database", database);
        switch (getTransport()) {
            case Constants.TRANSPORT_REST:
                return callEsciDoc("Sb.search", METHOD_SEARCH,
                    Constants.HTTP_METHOD_GET, Constants.SEARCH_BASE_URI + "/"
                        + database + paramString, new String[] {});
            case Constants.TRANSPORT_SOAP:
                return callEsciDoc("Sb.search", METHOD_SEARCH,
                    Constants.HTTP_METHOD_GET, Constants.SEARCH_BASE_URI + "/"
                        + database + paramString, new String[] {}, parameters);
            default:
                return null;
        }
    }

    /**
     * Retrieve srw explain response.
     * 
     * @param parameters
     *            The http-parameters as HashMap.
     * @param database
     *            database where search is executed.
     * @return The HttpMethod after the service call (REST) or the result object
     *         (SOAP).
     * @throws Exception
     *             If the service call fails.
     */
    public Object explain(final HashMap parameters, final String database)
        throws Exception {

        StringBuffer paramString = new StringBuffer("?");
        for (Iterator iter = parameters.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            paramString.append(key).append("=").append(parameters.get(key));
        }
        parameters.put("database", database);
        switch (getTransport()) {
            case Constants.TRANSPORT_REST:
                return callEsciDoc("Sb.explain", METHOD_EXPLAIN,
                    Constants.HTTP_METHOD_GET, Constants.SEARCH_BASE_URI + "/"
                        + database + paramString, new String[] {});
            case Constants.TRANSPORT_SOAP:
                return callEsciDoc("Sb.explain", METHOD_EXPLAIN,
                    Constants.HTTP_METHOD_GET, Constants.SEARCH_BASE_URI + "/"
                        + database + paramString, new String[] {}, parameters);
            default:
                return null;
        }
    }

    /**
     * Retrieve srw scan response.
     * 
     * @param parameters
     *            The http-parameters as HashMap.
     * @param database
     *            database where scan is executed.
     * @return The HttpMethod after the service call (REST) or the result object
     *         (SOAP).
     * @throws Exception
     *             If the service call fails.
     */
    public Object scan(final HashMap parameters, final String database)
        throws Exception {

        StringBuffer paramString = new StringBuffer("?");
        for (Iterator iter = parameters.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            if (paramString.length() > 1) {
                paramString.append("&");
            }
            paramString.append(key).append("=").append(parameters.get(key));
        }
        parameters.put("database", database);
        switch (getTransport()) {
            case Constants.TRANSPORT_REST:
                return callEsciDoc("Sb.scan", METHOD_SCAN,
                    Constants.HTTP_METHOD_GET, Constants.SEARCH_BASE_URI + "/"
                        + database + paramString, new String[] {});
            case Constants.TRANSPORT_SOAP:
                return callEsciDoc("Sb.scan", METHOD_SCAN,
                    Constants.HTTP_METHOD_GET, Constants.SEARCH_BASE_URI + "/"
                        + database + paramString, new String[] {}, parameters);
            default:
                return null;
        }
    }

    /**
     * @param parameters
     *            (database, url parameters..)
     * @return Returns the soapClient.
     * @throws ServiceException
     *             If the client creation fails.
     */
    public SRWPort getSearchClient(final HashMap parameters)
        throws ServiceException {
        searchClient = createSearchClient(parameters);
        return searchClient;
    }

    /**
     * Create the soap client.
     * 
     * @param parameters
     *            (database, url parameters..)
     * @return The soap client.
     * @throws ServiceException
     *             If the client creation fails.
     */
    private SRWPort createSearchClient(final HashMap parameters)
        throws ServiceException {
        SRWPort result = null;
        Vector mappings = new Vector();
        addBeanMapping(SearchRetrieveResponseType.class, mappings);
        addBeanMapping(SearchRetrieveRequestType.class, mappings);

        SRWSampleServiceLocator service = new SRWSampleServiceLocator(getEngineConfig());
        URL url;
        try {
            String httpUrl =
                HttpHelper
                    .createUrl(
                        de.escidoc.core.test.common.client.servlet.Constants.PROTOCOL,
                        de.escidoc.core.test.common.client.servlet.Constants.HOST_PORT,
                        "srw/search/" + parameters.get("database"));

            url = new URL(httpUrl);
        }
        catch (MalformedURLException e) {
            throw new ServiceException(e);
        }
        result = service.getSRW(url);
        return result;
    }

    /**
     * @param parameters
     *            (database, url parameters..)
     * @return Returns the soapClient.
     * @throws ServiceException
     *             If the client creation fails.
     */
    public ExplainPort getExplainClient(final HashMap parameters)
        throws ServiceException {
        explainClient = createExplainClient(parameters);
        return explainClient;
    }

    /**
     * Create the soap client.
     * 
     * @param parameters
     *            (database, url parameters..)
     * @return The soap client.
     * @throws ServiceException
     *             If the client creation fails.
     */
    private ExplainPort createExplainClient(final HashMap parameters)
        throws ServiceException {
        ExplainPort result = null;
        Vector mappings = new Vector();
        addBeanMapping(ExplainResponseType.class, mappings);
        addBeanMapping(ExplainRequestType.class, mappings);

        SRWSampleServiceLocator service = new SRWSampleServiceLocator();
        URL url;
        try {
            String httpUrl =
                HttpHelper
                    .createUrl(
                        de.escidoc.core.test.common.client.servlet.Constants.PROTOCOL,
                        de.escidoc.core.test.common.client.servlet.Constants.HOST_PORT,
                        "srw/search/" + parameters.get("database"));
            url = new URL(httpUrl);
        }
        catch (MalformedURLException e) {
            throw new ServiceException(e);
        }
        result = service.getExplainSOAP(url);
        return result;
    }

    /**
     * Call the soap method on the appropriate soap client.
     * 
     * @param label
     *            A label for logging purposes.
     * @param soapMethod
     *            The soap method.
     * @param params
     *            Array of additional parameters.
     * @return The result of the soap call.
     * @throws Exception
     *             If anything fails.
     */
    @Override
    protected Object callSoapMethod(
        final String label, final String soapMethod, final Object[] params)
        throws Exception {
        Object result = null;
        HashMap parameters = (HashMap) params[params.length - 1];
        logSoapServiceCall(label, params);
        if (METHOD_SEARCH.equals(soapMethod)) {
            SearchRetrieveRequestType request = new SearchRetrieveRequestType();
            request.setVersion("1.1");
            if (parameters.get("query") != null) {
                request.setQuery((String) parameters.get("query"));
            }
            if (parameters.get("startRecord") != null) {
                request.setStartRecord(new PositiveInteger((String) parameters
                    .get("startRecord")));
            }
            if (parameters.get("maximumRecords") != null) {
                request.setMaximumRecords(new PositiveInteger(
                    (String) parameters.get("maximumRecords")));
            }
            if (parameters.get("sortKeys") != null) {
                request.setSortKeys((String) parameters.get("sortKeys"));
            }
            if (parameters.get("recordPacking") != null) {
                request.setRecordPacking((String) parameters
                    .get("recordPacking"));
            }
            if (parameters.get("recordSchema") != null) {
                request
                    .setRecordSchema((String) parameters.get("recordSchema"));
            }
            if (parameters.get("stylesheet") != null) {
                request.setStylesheet(new URI((String) parameters
                    .get("stylesheet")));
            }
            result =
                getSearchClient(parameters).searchRetrieveOperation(request);
        }
        else if (METHOD_EXPLAIN.equals(soapMethod)) {
            ExplainRequestType request = new ExplainRequestType();
            request.setVersion("1.1");
            result = getExplainClient(parameters).explainOperation(request);
        }
        else if (METHOD_SCAN.equals(soapMethod)) {
            ScanRequestType request = new ScanRequestType();
            request.setVersion("1.1");
            if (parameters.get("scanClause") != null) {
                request.setScanClause((String) parameters.get("scanClause"));
            }
            if (parameters.get("responsePosition") != null) {
                request.setResponsePosition(new PositiveInteger(
                    (String) parameters.get("responsePosition")));
            }
            if (parameters.get("maximumTerms") != null) {
                request.setMaximumTerms(new PositiveInteger((String) parameters
                    .get("maximumTerms")));
            }
            if (parameters.get("stylesheet") != null) {
                request.setStylesheet(new URI((String) parameters
                    .get("stylesheet")));
            }
            result = getSearchClient(parameters).scanOperation(request);
        }
        else {
            throw new Exception("Tried to call unknown SOAP method '"
                + soapMethod + "' with label '" + label + "'");
        }
        return result;
    }
}
