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
package de.escidoc.core.test.sb;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.escidoc.core.test.EscidocRestSoapTestBase;
import gov.loc.www.zing.cql.xcql.SearchClauseType;
import gov.loc.www.zing.srw.DiagnosticsType;
import gov.loc.www.zing.srw.EchoedScanRequestType;
import gov.loc.www.zing.srw.EchoedSearchRetrieveRequestType;
import gov.loc.www.zing.srw.ExplainResponseType;
import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.RecordsType;
import gov.loc.www.zing.srw.ScanResponseType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.TermType;
import gov.loc.www.zing.srw.TermsType;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;

import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.escidoc.core.test.common.client.servlet.HttpHelper;

/**
 * Base class for search tests.
 * 
 * @author MIH
 * 
 */
public class SearchTestBase extends SbTestBase {

    private static final int TIME_TO_WAIT_FOR_INDEXER = 180000;

    private static final int MAX_TIME_TO_WAIT_FOR_INDEXER = 1200000;

    protected ItemHelper item = null;

    protected ContainerHelper container = null;

    protected ContextHelper context = null;

    protected ContentModelHelper contentModel = null;

    protected ContentRelationHelper contentRelation = null;

    protected GrantHelper grant = null;

    /**
     * @param transport
     *            The transport identifier.
     */
    public SearchTestBase(final int transport) {
        super(transport);
    }

    /**
     * Wait until the indexer has (hopefully :) run the next time.
     *
     * @throws InterruptedException
     *             If sleep fails.
     */
    protected void waitForIndexer() throws InterruptedException {

        Thread.sleep(TIME_TO_WAIT_FOR_INDEXER);
    }

    /**
     * Wait until the given id exists in the given index.
     * 
     * @param id
     *            resource id
     * @param indexName
     *            name of the index
     * 
     * @throws Exception
     *             Thrown if the connection to the indexer failed.
     */
    protected void waitForIndexerToAppear(
        final String id, final String indexName) throws Exception {
        waitForIndexer(id, indexName, true, MAX_TIME_TO_WAIT_FOR_INDEXER);
    }

    /**
     * Wait until the given id doesn't exist in the given index.
     * 
     * @param id
     *            resource id
     * @param indexName
     *            name of the index
     * 
     * @throws Exception
     *             Thrown if the connection to the indexer failed.
     */
    protected void waitForIndexerToDisappear(
        final String id, final String indexName) throws Exception {
        waitForIndexer(id, indexName, false, MAX_TIME_TO_WAIT_FOR_INDEXER);
    }

    /**
     * Wait until the given id exists in the given index.
     * 
     * @param id
     *            resource id
     * @param indexName
     *            name of the index
     * @param checkExists
     *            true for existence check, false for nonexistence
     * @param maxTimeToWait
     *            maximum time to wait in milliseconds
     * 
     * @throws Exception
     *             Thrown if the connection to the indexer failed.
     */
    private void waitForIndexer(
        final String id, 
        final String indexName, 
        final boolean checkExists, 
        final long maxTimeToWait)
        throws Exception {
        long time = System.currentTimeMillis();
        String query = "PID=" + id + " or distinction.rootPid=" + id;
        String httpUrl =
            HttpHelper
                .createUrl(
                    de.escidoc.core.test.common.client.servlet.Constants.PROTOCOL,
                    de.escidoc.core.test.common.client.servlet.Constants.HOST_PORT,
                    de.escidoc.core.test.common.client.servlet.Constants.SEARCH_BASE_URI
                        + "/" + indexName + "?query=" 
                        + URLEncoder.encode(query, "UTF-8"));

        for (;;) {
            HttpResponse httpRes =
                HttpHelper
                    .executeHttpRequest(
                        de.escidoc.core.test.common.client.servlet.Constants.HTTP_METHOD_GET,
                        httpUrl, null, null, null);

            if (httpRes.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                Pattern numberOfRecordsPattern =
                    Pattern.compile("numberOfRecords>(.*?)<");
                Matcher m =
                    numberOfRecordsPattern.matcher(EntityUtils.toString(httpRes.getEntity(), HTTP.UTF_8));

                if (m.find()) {
                    if (checkExists && (Integer.parseInt(m.group(1)) > 0)) {
                        break;
                    }
                    else if (!checkExists
                        && (Integer.parseInt(m.group(1)) == 0)) {
                        break;
                    }
                }
            }
            Thread.sleep(5000);
            if ((System.currentTimeMillis() - time) > maxTimeToWait) {
                break;
            }
        }
    }

    /**
     * Test retrieving an search result from the framework.
     * 
     * @param parameters
     *            The http-parameters as hashMap.
     * @param database
     *            database where search is executed.
     * @return The retrieved search-result.
     * @throws Exception
     *             If anything fails.
     */
    protected String search(
        final HashMap<String, String> parameters, final String database)
        throws Exception {

        Object result = getSearchClient().search(parameters, database);
        String xmlResult = null;
        if (result instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result;
            assertHttpStatusOfMethod("", httpRes);
            xmlResult = EntityUtils.toString(httpRes.getEntity(), HTTP.UTF_8);
         }
        else if (result instanceof SearchRetrieveResponseType) {
            xmlResult =
                makeSearchResponseXml((SearchRetrieveResponseType) result);
        }
        return xmlResult;
    }

    /**
     * Test retrieving an explain plan from the framework.
     * 
     * @param parameters
     *            The http-parameters as hashMap.
     * @param database
     *            database where explain is executed.
     * @return The retrieved explain plan.
     * @throws Exception
     *             If anything fails.
     */
    protected String explain(
        final HashMap<String, String> parameters, final String database)
        throws Exception {

        Object result = getSearchClient().explain(parameters, database);
        String xmlResult = null;
        if (result instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result;
            assertHttpStatusOfMethod("", httpRes);
            xmlResult = EntityUtils.toString(httpRes.getEntity(), HTTP.UTF_8);
           
        }
        else if (result instanceof ExplainResponseType) {
            xmlResult =
                ((ExplainResponseType) result)
                    .getRecord().getRecordData().get_any()[0].getAsString();
            xmlResult = xmlResult.replaceAll("&gt;", ">");
            xmlResult = xmlResult.replaceAll("&lt;", "<");
        }
        return xmlResult;
    }

    /**
     * Test retrieving an scan response from the framework.
     * 
     * @param parameters
     *            The http-parameters as hashMap.
     * @param database
     *            database where explain is executed.
     * @return The retrieved explain plan.
     * @throws Exception
     *             If anything fails.
     */
    protected String scan(final HashMap parameters, final String database)
        throws Exception {

        Object result = getSearchClient().scan(parameters, database);
        String xmlResult = null;
        if (result instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result;
            assertHttpStatusOfMethod("", httpRes);
            xmlResult = EntityUtils.toString(httpRes.getEntity(), HTTP.UTF_8);
                   }
        else if (result instanceof ScanResponseType) {
            xmlResult = makeScanResponseXml((ScanResponseType) result);
        }
        return xmlResult;
    }

    /**
     * Converts SOAP-return object to xml.
     * 
     * @param result
     *            The SOAP response object.
     * @return The xml-representation of the soap object.
     * @throws Exception
     *             If anything fails.
     */
    protected String makeSearchResponseXml(
        final SearchRetrieveResponseType result) throws Exception {
        StringBuffer soapXmlResult = new StringBuffer("");
        soapXmlResult.append("<searchRetrieveResponse  ").append(
            "xmlns=\"http://www.loc.gov/zing/srw/\">");
        soapXmlResult.append("<version>").append(result.getVersion()).append(
            "</version>");
        // get number of records////////////////////////////////////////////
        if (result.getNumberOfRecords() != null) {
            soapXmlResult.append("<numberOfRecords>").append(
                (result).getNumberOfRecords().toString()).append(
                "</numberOfRecords>");
        }
        // /////////////////////////////////////////////////////////////////

        // Get record-data//////////////////////////////////////////////////
        RecordsType recordsType = result.getRecords();
        RecordType[] records = null;
        if (recordsType != null) {
        	records = recordsType.getRecord();
        }
        if (records != null) {
            soapXmlResult.append("<records>");
            for (int i = 0; i < records.length; i++) {
                soapXmlResult.append("<record>");
                RecordType record = records[i];
                soapXmlResult.append("<recordSchema>").append(
                    record.getRecordSchema()).append("</recordSchema>");
                soapXmlResult.append("<recordPacking>").append(
                    record.getRecordPacking()).append("</recordPacking>");
                String recordData =
                    decodeCharacters(record.getRecordData().get_any()[0]
                        .getAsString());
                soapXmlResult.append("<recordData>").append(recordData).append(
                    "</recordData>");
                soapXmlResult.append("<recordPosition>").append(
                    record.getRecordPosition()).append("</recordPosition>");
                soapXmlResult.append("</record>");
            }
            soapXmlResult.append("</records>");
        }
        // /////////////////////////////////////////////////////////////////
        // get nextRecordPosition////////////////////////////////////////////
        if (result.getNextRecordPosition() != null) {
            soapXmlResult.append("<nextRecordPosition>").append(
                result.getNextRecordPosition().toString()).append(
                "</nextRecordPosition>");
        }
        // /////////////////////////////////////////////////////////////////
        // get echoed searchRetrieveRequest/////////////////////////////////
        if (result.getEchoedSearchRetrieveRequest() != null) {
            EchoedSearchRetrieveRequestType echoedSearchRequest =
                result.getEchoedSearchRetrieveRequest();
            soapXmlResult.append("<echoedSearchRetrieveRequest>");
            if (echoedSearchRequest.getVersion() != null) {
                soapXmlResult.append("<version>").append(
                    echoedSearchRequest.getVersion()).append("</version>");
            }
            if (echoedSearchRequest.getQuery() != null) {
                soapXmlResult.append("<query>").append(
                    encodeCharacters(echoedSearchRequest.getQuery())).append(
                    "</query>");
            }
            if (echoedSearchRequest.getXQuery() != null) {
                SearchClauseType searchClause =
                    echoedSearchRequest.getXQuery().getSearchClause();
                if (searchClause == null) {
                    searchClause =
                        echoedSearchRequest
                            .getXQuery().getTriple().getLeftOperand()
                            .getSearchClause();
                }
                if (searchClause == null) {
                    searchClause =
                        echoedSearchRequest
                            .getXQuery().getTriple().getLeftOperand()
                            .getTriple().getLeftOperand().getSearchClause();
                }
                soapXmlResult.append("<xQuery><ns3:searchClause  ").append(
                    "xmlns:ns3=\"http://www.loc.gov/zing/cql/xcql/\">");
                if (searchClause.getIndex() != null) {
                    soapXmlResult.append("<ns3:index>").append(
                        searchClause.getIndex()).append("</ns3:index>");
                }
                if (searchClause.getRelation() != null
                    && searchClause.getRelation().getValue() != null) {
                    soapXmlResult.append("<ns3:relation><ns3:value>")
                    .append(encodeCharacters(
                                    searchClause.getRelation().getValue()))
                            .append(
                        "</ns3:value></ns3:relation>");
                }
                if (searchClause.getTerm() != null) {
                    soapXmlResult.append("<ns3:term>").append(
                        encodeCharacters(searchClause.getTerm())).append(
                        "</ns3:term>");
                }
                soapXmlResult.append("</ns3:searchClause></xQuery>");
            }
            if (echoedSearchRequest.getMaximumRecords() != null) {
                soapXmlResult.append("<maximumRecords>").append(
                    echoedSearchRequest.getMaximumRecords()).append(
                    "</maximumRecords>");
            }
            if (echoedSearchRequest.getRecordPacking() != null) {
                soapXmlResult.append("<recordPacking>").append(
                    echoedSearchRequest.getRecordPacking()).append(
                    "</recordPacking>");
            }
            if (echoedSearchRequest.getRecordSchema() != null) {
                soapXmlResult.append("<recordSchema>").append(
                    echoedSearchRequest.getRecordSchema()).append(
                    "</recordSchema>");
            }
            soapXmlResult.append("</echoedSearchRetrieveRequest>");
        }
        // /////////////////////////////////////////////////////////////////
        // get Diagnostics//////////////////////////////////////////////////
        DiagnosticsType diagnosticsType = result.getDiagnostics();
        DiagnosticType[] diagnostics = null;
        if (diagnosticsType != null) {
        	diagnostics = diagnosticsType.getDiagnostic();
        }
        if (diagnostics != null) {
            soapXmlResult.append("<diagnostics>");
            for (int i = 0; i < diagnostics.length; i++) {
                soapXmlResult
                    .append("<ns4:diagnostic xmlns:ns4=\"http://www.loc.gov/zing/srw/diagnostic/\">");
                DiagnosticType diagnostic = diagnostics[i];
                soapXmlResult.append("<ns4:uri>").append(
                    diagnostic.getUri().getPath()).append("</ns4:uri>");
                soapXmlResult.append("<ns4:details>").append(
                    diagnostic.getDetails()).append("</ns4:details>");
                soapXmlResult.append("</ns4:diagnostic>");
            }
            soapXmlResult.append("</diagnostics>");
        }
        // /////////////////////////////////////////////////////////////////
        soapXmlResult.append("</searchRetrieveResponse>");
        return soapXmlResult.toString();
    }

    /**
     * Converts SOAP-return object to xml.
     * 
     * @param result
     *            The SOAP response object.
     * @return The xml-representation of the soap object.
     * @throws Exception
     *             If anything fails.
     */
    protected String makeScanResponseXml(final ScanResponseType result)
        throws Exception {
        StringBuffer soapXmlResult =
            new StringBuffer(
                "<scanResponse xmlns=\"http://www.loc.gov/zing/srw/\">")
                .append("<version>1.1</version>");

        // Get terms//////////////////////////////////////////////////
        TermsType termsType = result.getTerms();
        TermType[] terms = null;
        if (termsType != null) {
            terms = termsType.getTerm();
        }
        if (terms != null) {
            soapXmlResult
                .append("<terms xmlns:ns1=\"http://www.loc.gov/zing/srw/\">");
            for (int i = 0; i < terms.length; i++) {
                soapXmlResult.append("<term>");
                TermType term = terms[i];
                soapXmlResult.append("<value>").append(term.getValue()).append(
                    "</value>");
                soapXmlResult.append("<numberOfRecords>").append(
                    term.getNumberOfRecords()).append("</numberOfRecords>");
                soapXmlResult.append("</term>");
            }
            soapXmlResult.append("</terms>");
        }
        // /////////////////////////////////////////////////////////////////
        // get Echoed Scan Request/////////////////////////////////////////////
        EchoedScanRequestType echoedScanRequest =
            (result).getEchoedScanRequest();
        if (echoedScanRequest != null) {
            soapXmlResult
                .append(
                    "<echoedScanRequest xmlns:ns2=\"http://www.loc.gov/zing/srw/\">")
                .append("<version>1.1</version>");
            soapXmlResult.append("<scanClause>").append(
                echoedScanRequest.getScanClause()).append("</scanClause>");
            SearchClauseType xScanClause = echoedScanRequest.getXScanClause();
            if (xScanClause != null) {
                soapXmlResult.append("<xScanClause>");
                if (xScanClause.getIndex() != null) {
                    soapXmlResult
                        .append("<ns3:index xmlns:ns3=\"http://www.loc.gov/zing/cql/xcql/\">");
                    soapXmlResult.append(xScanClause.getIndex()).append(
                        "</ns3:index>");
                }
                if (xScanClause.getRelation() != null
                    && xScanClause.getRelation().getValue() != null) {
                    soapXmlResult
                        .append("<ns4:relation xmlns:ns4=\"http://www.loc.gov/zing/cql/xcql/\"><ns4:value>");
                    soapXmlResult
                        .append(xScanClause.getRelation().getValue()).append(
                            "</ns4:value></ns4:relation>");
                }
                if (xScanClause.getTerm() != null) {
                    soapXmlResult
                        .append("<ns5:term xmlns:ns5=\"http://www.loc.gov/zing/cql/xcql/\">");
                    soapXmlResult.append(xScanClause.getTerm()).append(
                        "</ns5:term>");
                }
                soapXmlResult.append("</xScanClause>");
            }
            soapXmlResult.append("</echoedScanRequest>");
        }
        // get Diagnostics//////////////////////////////////////////////////
        DiagnosticsType diagnosticsType = result.getDiagnostics();
        DiagnosticType[] diagnostics = null;
        if (diagnosticsType != null) {
            diagnostics = diagnosticsType.getDiagnostic();
        }
        if (diagnostics != null) {
            soapXmlResult.append("<diagnostics>");
            for (int i = 0; i < diagnostics.length; i++) {
                soapXmlResult
                    .append("<ns4:diagnostic xmlns:ns4=\"http://www.loc.gov/zing/srw/diagnostic/\">");
                DiagnosticType diagnostic = diagnostics[i];
                soapXmlResult.append("<ns4:uri>").append(
                    diagnostic.getUri().getPath()).append("</ns4:uri>");
                soapXmlResult.append("<ns4:details>").append(
                    diagnostic.getDetails()).append("</ns4:details>");
                soapXmlResult.append("</ns4:diagnostic>");
            }
            soapXmlResult.append("</diagnostics>");
        }
        soapXmlResult.append("</scanResponse>");
        // /////////////////////////////////////////////////////////////////
        // /////////////////////////////////////////////////////////////////

        return soapXmlResult.toString();
    }

    /**
     * extract id out of item-xml.
     * 
     * @param xml
     *            String xml
     * @return String id
     * 
     */
    protected String getId(final String xml) {
        String id = null;
        Pattern objidAttributePattern = Pattern.compile("objid=\"([^\"]*)\"");
        Matcher m = objidAttributePattern.matcher(xml);
        if (m.find()) {
            id = m.group(1);
        }
        else {
            Pattern hrefPattern = Pattern.compile("xlink:href=\"([^\"]*)\"");
            m = hrefPattern.matcher(xml);
            if (m.find()) {
                id = m.group(1);
                id = id.replaceFirst(".*\\/", "");
            }
        }
        return id;
    }

    /**
     * get last-modification-date from xml String.
     * 
     * @param xml
     *            String xml
     * @return String last-modification-date
     * @throws Exception
     *             If anything fails.
     */
    protected String getLastModificationDate(final String xml) throws Exception {

        return getLastModificationDateValue(EscidocRestSoapTestBase
            .getDocument(xml));
    }

    /**
     * get number of hits from xml String.
     * 
     * @param searchResult
     *            String searchResult
     * @return String number of hits
     * 
     */
    protected String getNumberOfHits(final String searchResult) {
        String numberOfHits = null;
        Pattern dateAttributePattern =
            Pattern.compile("numberOfRecords>(.*?)<");
        Matcher m = dateAttributePattern.matcher(searchResult);
        if (m.find()) {
            numberOfHits = m.group(1);
        }
        return numberOfHits;
    }

    /**
     * get first record from xml String.
     * 
     * @param searchResult
     *            String searchResult
     * @return String first record
     * 
     */
    protected String getFirstRecord(final String searchResult) {
        String firstRecord = null;
        Pattern dateAttributePattern = Pattern.compile("recordPosition>(.*?)<");
        Matcher m = dateAttributePattern.matcher(searchResult);
        if (m.find()) {
            firstRecord = m.group(1);
        }
        return firstRecord;
    }

    /**
     * get first record from xml String.
     * 
     * @param searchResult
     *            String searchResult
     * @return String first record
     * 
     */
    protected String getNextRecordPosition(final String searchResult) {
        String nextRecordPosition = null;
        Pattern dateAttributePattern =
            Pattern.compile("nextRecordPosition>(.*?)<");
        Matcher m = dateAttributePattern.matcher(searchResult);
        if (m.find()) {
            nextRecordPosition = m.group(1);
        }
        return nextRecordPosition;
    }

    /**
     * get name of the database from explain plan.
     * 
     * @param explainPlan
     *            String explainPlan
     * @return String number of hits
     * 
     */
    protected String getDatabase(final String explainPlan) {
        String database = null;
        Pattern dateAttributePattern = Pattern.compile("database>(.*?)<");
        Matcher m = dateAttributePattern.matcher(explainPlan);
        if (m.find()) {
            database = m.group(1);
        }
        return database;
    }

    /**
     * get number of index-fields from explain plan.
     * 
     * @param explainPlan
     *            String explainPlan
     * @return String number of index-fields
     * 
     */
    protected int getIndexFieldCount(final String explainPlan) {
        if (explainPlan == null) {
            return 0;
        }
        int indexFieldCount = explainPlan.split("<[^\\/>]*?index>").length - 1;
        return indexFieldCount;
    }

    /**
     * get number of sort-fields from explain plan.
     * 
     * @param explainPlan
     *            String explainPlan
     * @return String number of sort-fields
     * 
     */
    protected int getSortFieldCount(final String explainPlan) {
        if (explainPlan == null) {
            return 0;
        }
        int sortFieldCount =
            explainPlan.split("<[^\\/>]*?sortKeyword>").length - 1;
        return sortFieldCount;
    }

    /**
     * get diagnostic details from xml String.
     * 
     * @param searchResult
     *            String searchResult
     * @return String diagnostic details
     * 
     */
    protected String getDiagnostics(final String searchResult) {
        String details = null;
        Pattern dateAttributePattern = Pattern.compile("details>(.*?)<");
        Matcher m = dateAttributePattern.matcher(searchResult);
        if (m.find()) {
            details = m.group(1);
        }
        if (details != null && details.equals("null")) {
            details = null;
        }
        return details;
    }

    /**
     * check if highlighting-element is there.
     * 
     * @param searchResult
     *            String searchResult
     * @return boolean
     * 
     */
    protected boolean checkHighlighting(final String searchResult) {
        if (searchResult.matches("(?s).*highlight.*")) {
            return true;
        }
        return false;
    }

    /**
     * check if public-status, version-number and latest-version-numer are as
     * expected.
     * 
     * @param xml
     *            String searchResult
     * @param versionCheckMap
     *            HashMap with objectIds + expected version info.
     * @throws Exception
     *             e
     * 
     */
    protected void checkVersions(
        final String xml,
        final Map<String, HashMap<String, String>> versionCheckMap)
        throws Exception {
        Document doc = getDocument(xml);
        Pattern objIdPattern = Pattern.compile("\\$\\{objId\\}");
        Matcher objIdMatcher = objIdPattern.matcher("");
        Pattern objTypePattern = Pattern.compile("\\$\\{objType\\}");
        Matcher objTypeMatcher = objTypePattern.matcher("");
        String publicStatusXpath =
            "//${objType}[@href=\"${objId}" + "\"]/properties/public-status";
        String versionNumberXpath =
            "//${objType}[@href=\"${objId}" + "\"]/properties/version/number";
        String latestVersionNumberXpath =
            "//${objType}[@href=\"${objId}"
                + "\"]/properties/latest-version/number";

        for (String key : versionCheckMap.keySet()) {
            objIdMatcher.reset(publicStatusXpath);
            String replacedPublicStatusXpath = objIdMatcher.replaceFirst(key);
            objTypeMatcher.reset(replacedPublicStatusXpath);
            replacedPublicStatusXpath =
                objTypeMatcher.replaceFirst(versionCheckMap.get(key).get(
                    "objectType"));

            objIdMatcher.reset(versionNumberXpath);
            String replacedVersionNumberXpath = objIdMatcher.replaceFirst(key);
            objTypeMatcher.reset(replacedVersionNumberXpath);
            replacedVersionNumberXpath =
                objTypeMatcher.replaceFirst(versionCheckMap.get(key).get(
                    "objectType"));

            objIdMatcher.reset(latestVersionNumberXpath);
            String replacedLatestVersionNumberXpath =
                objIdMatcher.replaceFirst(key);
            objTypeMatcher.reset(replacedLatestVersionNumberXpath);
            replacedLatestVersionNumberXpath =
                objTypeMatcher.replaceFirst(versionCheckMap.get(key).get(
                    "objectType"));

            Node publicStatus =
                selectSingleNode(doc, replacedPublicStatusXpath);
            Node versionNumber =
                selectSingleNode(doc, replacedVersionNumberXpath);
            Node latestVersionNumber =
                selectSingleNode(doc, replacedLatestVersionNumberXpath);
            assertEquals("Public-Status not as expected", versionCheckMap.get(
                key).get("expectedPublicStatus"), publicStatus.getTextContent());
            assertEquals("Version-Number not as expected", versionCheckMap.get(
                key).get("expectedVersionNumber"), versionNumber
                .getTextContent());
            assertEquals("Latest-Version-Number not as expected",
                versionCheckMap.get(key).get("expectedLatestVersionNumber"),
                latestVersionNumber.getTextContent());
        }
    }

    /**
     * Create a Param structure for PID assignments. The last-modification-date
     * is retrieved from the by id selected object.
     * 
     * @param itemId
     *            itemId
     * @throws Exception
     *             Thrown if anything fails.
     * @return param XML snippet.
     */
    protected final String getItemPidParam(final String itemId)
        throws Exception {
        String xml = item.retrieve(itemId);
        String lastModDate = getLastModificationDate(xml);
        String url =
            HttpHelper
                .createUrl(
                    de.escidoc.core.test.common.client.servlet.Constants.PROTOCOL,
                    de.escidoc.core.test.common.client.servlet.Constants.HOST_PORT,
                    de.escidoc.core.test.common.client.servlet.Constants.ITEM_BASE_URI)
                + itemId;
        String param =
            "<param last-modification-date=\"" + lastModDate + "\"><url>" + url
                + "</url></param>";
        return (param);
    }

    /**
     * Create a Param structure for PID assignments. The last-modification-date
     * is retrieved from the by id selected object.
     * 
     * @param containerId
     *            containerId
     * @throws Exception
     *             Thrown if anything fails.
     * @return param XML snippet.
     */
    protected final String getContainerPidParam(final String containerId)
        throws Exception {
        String xml = container.retrieve(containerId);
        String lastModDate = getLastModificationDate(xml);
        String url =
            HttpHelper
                .createUrl(
                    de.escidoc.core.test.common.client.servlet.Constants.PROTOCOL,
                    de.escidoc.core.test.common.client.servlet.Constants.HOST_PORT,
                    de.escidoc.core.test.common.client.servlet.Constants.CONTAINER_BASE_URI)
                + containerId;
        String param =
            "<param last-modification-date=\"" + lastModDate + "\"><url>" + url
                + "</url></param>";
        return (param);
    }

    /**
     * Replaces special Characters..
     * 
     * @return String Replaced String
     * @param text
     *            String text to replace
     * 
     * @sb
     */
    private String decodeCharacters(String text) {
        text = text.replaceAll("&lt;", "<");
        text = text.replaceAll("&gt;", ">");
        text = text.replaceAll("&quot;", "\"");
        text = text.replaceAll("&apos;", "'");
        // text = text.replaceAll("&amp;", "&");
        return text;
    }

    /**
     * Replaces special Characters..
     * 
     * @return String Replaced String
     * @param text
     *            String text to replace
     * 
     * @sb
     */
    private String encodeCharacters(String text) {
        text = text.replaceAll("<", "&lt;");
        text = text.replaceAll(">", "&gt;");
        text = text.replaceAll("\"", "&quot;");
        text = text.replaceAll("'", "&apos;");
        text = text.replaceAll("&", "&amp;");
        return text;
    }

}
