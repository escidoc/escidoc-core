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

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.HttpHelper;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Base class for search tests.
 * 
 * @author Michael Hoppe
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
     * Wait until the given id exists in the given index.
     * 
     * @param id
     *            resource id
     * @param indexName
     *            name of the index
     * @throws Exception
     *             Thrown if the connection to the indexer failed.
     */
    protected void waitForIndexerToAppear(final String id, final String indexName) throws Exception {
        waitForIndexer(id, indexName, true, MAX_TIME_TO_WAIT_FOR_INDEXER);
    }

    /**
     * Wait until the given id doesn't exist in the given index.
     * 
     * @param id
     *            resource id
     * @param indexName
     *            name of the index
     * @throws Exception
     *             Thrown if the connection to the indexer failed.
     */
    protected void waitForIndexerToDisappear(final String id, final String indexName) throws Exception {
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
     * @throws Exception
     *             Thrown if the connection to the indexer failed.
     */
    private void waitForIndexer(
        final String id, final String indexName, final boolean checkExists, final long maxTimeToWait) throws Exception {
        long time = System.currentTimeMillis();
        String query = "PID=" + id + " or distinction.rootPid=" + id;
        String httpUrl =
            getFrameworkUrl() + de.escidoc.core.test.common.client.servlet.Constants.SEARCH_BASE_URI + "/" + indexName
                + "?query=" + URLEncoder.encode(query, DEFAULT_CHARSET);

        for (;;) {
            HttpResponse httpRes =
                HttpHelper.executeHttpRequest(de.escidoc.core.test.common.client.servlet.Constants.HTTP_METHOD_GET,
                    httpUrl, null, null, null);

            if (httpRes.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                Pattern numberOfRecordsPattern = Pattern.compile("numberOfRecords>(.*?)<");
                Matcher m = numberOfRecordsPattern.matcher(EntityUtils.toString(httpRes.getEntity(), HTTP.UTF_8));

                if (m.find()) {
                    if (checkExists && (Integer.parseInt(m.group(1)) > 0)) {
                        break;
                    }
                    else if (!checkExists && (Integer.parseInt(m.group(1)) == 0)) {
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
    protected String search(final HashMap<String, String> parameters, final String database) throws Exception {
        Object result = getSearchClient().search(parameters, database);
        HttpResponse httpRes = (HttpResponse) result;
        assertHttpStatusOfMethod("", httpRes);
        return EntityUtils.toString(httpRes.getEntity(), HTTP.UTF_8);
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
    protected String explain(final HashMap<String, String[]> parameters, final String database) throws Exception {
        Object result = getSearchClient().explain(parameters, database);
        HttpResponse httpRes = (HttpResponse) result;
        assertHttpStatusOfMethod("", httpRes);
        return EntityUtils.toString(httpRes.getEntity(), HTTP.UTF_8);
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
    protected String scan(final HashMap parameters, final String database) throws Exception {

        Object result = getSearchClient().scan(parameters, database);
        HttpResponse httpRes = (HttpResponse) result;
        assertHttpStatusOfMethod("", httpRes);
        return EntityUtils.toString(httpRes.getEntity(), HTTP.UTF_8);
    }

    /**
     * extract id out of item-xml.
     * 
     * @param xml
     *            String xml
     * @return String id
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

        return getLastModificationDateValue(EscidocAbstractTest.getDocument(xml));
    }

    /**
     * get creation-date from xml String.
     * 
     * @param xml
     *            String xml
     * @return String creation-date
     * @throws Exception
     *             If anything fails.
     */
    protected String getCreationDate(final String xml) throws Exception {

        return getCreationDateValue(EscidocAbstractTest.getDocument(xml));
    }

    /**
     * get number of hits from xml String.
     * 
     * @param searchResult
     *            String searchResult
     * @return String number of hits
     */
    protected String getNumberOfHits(final String searchResult) {
        String numberOfHits = null;
        Pattern dateAttributePattern = Pattern.compile("numberOfRecords>(.*?)<");
        Matcher m = dateAttributePattern.matcher(searchResult);
        if (m.find()) {
            numberOfHits = m.group(1);
        }
        return numberOfHits;
    }

    /**
     * get number of scan hits from xml String.
     * 
     * @param scanResult
     *            String scanResult
     * @return String number of hits
     */
    protected String getNumberOfScanHits(final String scanResult) throws Exception {
        Document scanResultDoc = getDocument(scanResult);
        NodeList nodes = selectNodeList(scanResultDoc, "/scanResponse/terms/term");
        return Integer.toString(nodes.getLength());
    }

    /**
     * get first record from xml String.
     * 
     * @param searchResult
     *            String searchResult
     * @return String first record
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
     */
    protected String getNextRecordPosition(final String searchResult) {
        String nextRecordPosition = null;
        Pattern dateAttributePattern = Pattern.compile("nextRecordPosition>(.*?)<");
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
     */
    protected int getSortFieldCount(final String explainPlan) {
        if (explainPlan == null) {
            return 0;
        }
        int sortFieldCount = explainPlan.split("<[^\\/>]*?sortKeyword>").length - 1;
        return sortFieldCount;
    }

    /**
     * get diagnostic details from xml String.
     * 
     * @param searchResult
     *            String searchResult
     * @return String diagnostic details
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
     */
    protected boolean checkHighlighting(final String searchResult) {
        if (searchResult.matches("(?s).*highlight.*")) {
            return true;
        }
        return false;
    }

    /**
     * check if public-status, version-number and latest-version-numer are as expected.
     * 
     * @param xml
     *            String searchResult
     * @param versionCheckMap
     *            HashMap with objectIds + expected version info.
     * @throws Exception
     *             e
     */
    protected void checkVersions(final String xml, final Map<String, HashMap<String, String>> versionCheckMap)
        throws Exception {
        Document doc = getDocument(xml);
        Pattern objIdPattern = Pattern.compile("\\$\\{objId\\}");
        Matcher objIdMatcher = objIdPattern.matcher("");
        Pattern objTypePattern = Pattern.compile("\\$\\{objType\\}");
        Matcher objTypeMatcher = objTypePattern.matcher("");
        String publicStatusXpath = "//${objType}[@href=\"${objId}" + "\"]/properties/public-status";
        String versionNumberXpath = "//${objType}[@href=\"${objId}" + "\"]/properties/version/number";
        String latestVersionNumberXpath = "//${objType}[@href=\"${objId}" + "\"]/properties/latest-version/number";

        for (String key : versionCheckMap.keySet()) {
            objIdMatcher.reset(publicStatusXpath);
            String replacedPublicStatusXpath = objIdMatcher.replaceFirst(key);
            objTypeMatcher.reset(replacedPublicStatusXpath);
            replacedPublicStatusXpath = objTypeMatcher.replaceFirst(versionCheckMap.get(key).get("objectType"));

            objIdMatcher.reset(versionNumberXpath);
            String replacedVersionNumberXpath = objIdMatcher.replaceFirst(key);
            objTypeMatcher.reset(replacedVersionNumberXpath);
            replacedVersionNumberXpath = objTypeMatcher.replaceFirst(versionCheckMap.get(key).get("objectType"));

            objIdMatcher.reset(latestVersionNumberXpath);
            String replacedLatestVersionNumberXpath = objIdMatcher.replaceFirst(key);
            objTypeMatcher.reset(replacedLatestVersionNumberXpath);
            replacedLatestVersionNumberXpath = objTypeMatcher.replaceFirst(versionCheckMap.get(key).get("objectType"));

            Node publicStatus = selectSingleNode(doc, replacedPublicStatusXpath);
            Node versionNumber = selectSingleNode(doc, replacedVersionNumberXpath);
            Node latestVersionNumber = selectSingleNode(doc, replacedLatestVersionNumberXpath);
            assertEquals("Public-Status not as expected", versionCheckMap.get(key).get("expectedPublicStatus"),
                publicStatus.getTextContent());
            assertEquals("Version-Number not as expected", versionCheckMap.get(key).get("expectedVersionNumber"),
                versionNumber.getTextContent());
            assertEquals("Latest-Version-Number not as expected", versionCheckMap.get(key).get(
                "expectedLatestVersionNumber"), latestVersionNumber.getTextContent());
        }
    }

    /**
     * Create a Param structure for PID assignments. The last-modification-date is retrieved from the by id selected
     * object.
     * 
     * @param itemId
     *            itemId
     * @return param XML snippet.
     * @throws Exception
     *             Thrown if anything fails.
     */
    protected final String getItemPidParam(final String itemId) throws Exception {
        String xml = item.retrieve(itemId);
        String lastModDate = getLastModificationDate(xml);
        String url = getFrameworkUrl() + de.escidoc.core.test.common.client.servlet.Constants.ITEM_BASE_URI + itemId;
        String param = "<param last-modification-date=\"" + lastModDate + "\"><url>" + url + "</url></param>";
        return (param);
    }

    /**
     * Create a Param structure for PID assignments. The last-modification-date is retrieved from the by id selected
     * object.
     * 
     * @param containerId
     *            containerId
     * @return param XML snippet.
     * @throws Exception
     *             Thrown if anything fails.
     */
    protected final String getContainerPidParam(final String containerId) throws Exception {
        String xml = container.retrieve(containerId);
        String lastModDate = getLastModificationDate(xml);
        String url =
            getFrameworkUrl() + de.escidoc.core.test.common.client.servlet.Constants.CONTAINER_BASE_URI + containerId;
        String param = "<param last-modification-date=\"" + lastModDate + "\"><url>" + url + "</url></param>";
        return (param);
    }

    /**
     * Replaces special Characters..
     * 
     * @param text
     *            String text to replace
     * @return String Replaced String
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
     * @param text
     *            String text to replace
     * @return String Replaced String
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
