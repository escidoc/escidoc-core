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
package de.escidoc.core.om.business;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;

/**
 * An utility class for Kowari request.
 * 
 * @spring.bean id="business.KowariUtility"
 * @author ROF
 * @om
 * 
 */
public class KowariUtility {

    private static final HttpClient client = new HttpClient();

    private static final Pattern PATTERN_MODIFICATION_DATE =
        Pattern.compile("\"([0-9T:\\.-])+\""); // TODO

    // "\!(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d{3}(Z|[+-]\d{2}:\d{2}))\""

    private static final Pattern PATTERN_A_TO_Z_STRING =
        Pattern.compile("\"([a-zA-Z])+\"");

    private static final Pattern PATTERN_A_TO_Z_colon_STRING =
        Pattern.compile("\"(.*)\""); // TODO

    private static final Pattern PATTERN_OBJECT_TYPE_STRING =
        Pattern.compile("\"([^\"]+)\"");

    private static final Pattern PATTERN_A_TO_Z_AND_WHITESPACE_STRING =
        Pattern.compile("\"([^\"]+)\"");

    private static final Pattern PATTERN_ID = Pattern

    .compile("([a-zA-Z]+):([^\"]+)");

    private static final Pattern PATTERN_ID_FOR_URI = Pattern

    .compile("([a-zA-Z]+):([^>]+)");

    // private static final Pattern PATTERN_INFO_URL = Pattern
    // .compile("<info:fedora/([a-zA-Z]+):([^>]+)>");
    //
    // private static final Pattern PATTERN_ERROR =
    // Pattern.compile("[E|e]rror");

    static final String TYPE = "tuples";

    static final String LANG = "iTQL";

    static final String LANG_MPT = "spo";

    static final String FORMAT_CSV = "CSV";

    static final String FORMAT_MPT = "N-Triples";

    static final String TYPE_MPT = "triples";

    static final String FORMAT_SIMPLE = "Simple";

    static final String FORMAT_SPARQL = "Sparql";

    static final String FORMAT_TSV = "TSV";

    static final String FLUSH = "true";

    private static AppLogger log = new AppLogger(KowariUtility.class.getName());

    /**
     * Default constructor.
     * 
     */
    public KowariUtility() {

    }

    /**
     * The method requests the Kowari tripple store via http post and returns
     * the result in CSV format.
     * 
     * Checks if http response body contains payload.
     * 
     * @param iTqlQuery
     *            The iTQL query to Kowari
     * @return http response body null, if http response body is empty
     * @om
     */
    public static String requestCSV(final String iTqlQuery)
        throws SystemException {

        return requestKowari(iTqlQuery, FORMAT_CSV);
    }

    /**
     * The method requests the Kowari triple store via http post and returns the
     * result in simple format.
     * 
     * Checks if http response body contains payload.
     * 
     * @param iTqlQuery
     *            The iTQL query to Kowari
     * @return http response body null, if http response body is empty
     * @throws SystemException
     * @om
     */
    public static String requestSimple(final String iTqlQuery)
        throws SystemException {
        // return requestKowari(iTqlQuery, "Simple");
        //
        // HttpClient client = new HttpClient();
        // PostMethod post = new PostMethod(URL);
        //
        // post.addParameter("type", TYPE);
        // post.addParameter("lang", LANG);
        // post.addParameter("format", "Simple");
        // post.addParameter("query", iTqlQuery);
        // // The flush parameter tells the resource index to ensure
        // // that any recently-added/modified/deleted triples are
        // // flushed to the triplestore before executing the query.
        // post.addParameter("flush", FLUSH);
        // int resultCode = 0;
        // try {
        // resultCode = client.executeMethod(post);
        // }
        // catch (HttpException e) {
        // log.error(e.toString());
        // throw new SystemException(e.toString(), e);
        // }
        // catch (IOException e) {
        // log.error(e.toString());
        // throw new SystemException(e.toString(), e);
        // }
        // String result;
        // try {
        // result = post.getResponseBodyAsString();
        // }
        // catch (IOException e) {
        // throw new EscidocRuntimeException(e);
        // }
        // if (resultCode != HttpServletResponse.SC_OK) {
        // log.error("Bad request. Http response : " + resultCode);
        // throw new SystemException("Bad request. Http response : "
        // + resultCode);
        //
        // }

        String result = requestKowari(iTqlQuery, "Simple");
        replaceNewlines(result, "");
        Pattern p = Pattern.compile("[E|e]rror");
        Matcher m = p.matcher(result);

        if (m.find()) {
            log.error("Fedora resource index fault");
            throw new SystemException("Fedora resource index fault");

        }
        if (result.indexOf("object") == -1) {
            result = null;
        }
        //
        // if (post != null) {
        // post.releaseConnection();
        // }
        return result;
    }

    /**
     * The method requests the Kowari tripple store via http post and returns
     * the result in the specified format.
     * 
     * Checks if http response body contains payload.
     * 
     * @param iTqlQuery
     *            The iTQL query to Kowari
     * @param responseFormat
     *            The format of the response. This must be one of
     *            <ul>
     *            <li><code>FORMAT_CSV</code></li>
     *            <li><code>FORMAT_SIMPLE</code></li>
     *            <li><code>FORMAT_SPARQL</code></li>
     *            <li><code>FORMAT_TSV</code></li>
     *            </ul>
     * @return http response body null, if http response body is empty
     * @throws SystemException
     * @om
     */
    public static String requestKowari(
        final String iTqlQuery, final String responseFormat)
        throws SystemException {

        synchronized (client) {
            PostMethod post =
                new PostMethod(System
                    .getProperty(EscidocConfiguration.FEDORA_URL)
                    + "/risearch");
            post.addParameter("format", responseFormat);
            post.addParameter("query", iTqlQuery);
            post.addParameter("type", TYPE);
            post.addParameter("lang", LANG);
            // The flush parameter tells the resource index to ensure
            // that any recently-added/modified/deleted triples are
            // flushed to the triplestore before executing the query.
            post.addParameter("flush", FLUSH);
            int resultCode = 0;
            try {
                resultCode = client.executeMethod(post);
                if (resultCode != HttpServletResponse.SC_OK) {
                    log.error("Bad request. Http response : " + resultCode);
                    throw new SystemException("Bad request. Http response : "
                        + resultCode);
                }

                String result = post.getResponseBodyAsString();
                if (result == null) {
                    return null;
                }
                if (result.startsWith("<html")) {
                    log.error("Request failed:\n" + result);
                    throw new SystemException("Request to Kowari failed.");
                }

                return result;
            }
            catch (HttpException e) {
                log.error("Error requesting Kowari", e);
                throw new SystemException(e.toString(), e);
            }
            catch (IOException e) {
                log.error("Error requesting Kowari", e);
                throw new SystemException(e.toString(), e);
            }
            finally {
                if (post != null) {
                    post.releaseConnection();
                }
            }
        }

    }

    /**
     * 
     * @param spoQuery
     * @return
     * @throws TripleStoreSystemException
     */
    public static String requestMPT(final String spoQuery)
        throws TripleStoreSystemException {

        synchronized (client) {
            PostMethod post =
                new PostMethod(System
                    .getProperty(EscidocConfiguration.FEDORA_URL)
                    + "/risearch");
            post.addParameter("format", FORMAT_MPT);
            post.addParameter("query", spoQuery);
            post.addParameter("type", TYPE_MPT);
            post.addParameter("lang", LANG_MPT);
            // The flush parameter tells the resource index to ensure
            // that any recently-added/modified/deleted triples are
            // flushed to the triplestore before executing the query.
            post.addParameter("flush", FLUSH);
            int resultCode = 0;
            try {
                resultCode = client.executeMethod(post);
                if (resultCode != HttpServletResponse.SC_OK) {
                    log.error("Bad request. Http response : " + resultCode);
                    throw new TripleStoreSystemException(
                        "Bad request. Http response : " + resultCode);
                }

                String result = post.getResponseBodyAsString();
                if (result == null) {
                    return null;
                }
                if (result.startsWith("<html")) {
                    log.error("Request failed:\n" + result);
                    throw new TripleStoreSystemException(
                        "Request to MPT failed.");
                }

                return result;
            }
            catch (HttpException e) {
                log.error("Error requesting MPT", e);
                throw new TripleStoreSystemException(e.toString(), e);
            }
            catch (IOException e) {
                log.error("Error requesting MPT", e);
                throw new TripleStoreSystemException(e.toString(), e);
            }
            finally {
                if (post != null) {
                    post.releaseConnection();
                }
            }
        }

    }

    /**
     * The method triggers requestKowari() to check if the tripple store
     * contains a tripple with provided contentItemId as subject,
     * &lt;http://www.nsdl.org/ontologies/relationship#hasContentComponent&gt;
     * as predicat and provided contentComponentId as object.
     * 
     * @param contentItemId
     *            contentItemId
     * @param contentComponentId
     *            contentComponentId
     * @return true, if there is a such tripple in the Kowari, false otherwise
     * @throws SystemException
     * @om
     */
    public static boolean isContainedInContentItem(
        final String contentItemId, final String contentComponentId)
        throws SystemException {
        boolean isContained = false;
        String query =
            "select $object from <#ri>" + "\n" + "where $object "
                + "<http://www.nsdl.org/ontologies/"
                + "relationships/hasComponent> " + "'" + contentComponentId
                + "'";

        String result = requestSimple(query);
        if (result != null) {

            Pattern p = Pattern.compile(contentItemId);
            Matcher m = p.matcher(result);
            if (m.find()) {
                isContained = true;
            }

        }

        return isContained;
    }

    public static boolean isParentOfOu(final String ouId, final String parentId)
        throws SystemException {
        boolean isParent = false;
        String query =
            "select $object from <#ri>" + "\n" + "where $object "
                + "<http://www.nsdl.org/ontologies/"
                + "relationships/hasParent> " + "'" + parentId + "'";

        String result = requestSimple(query);
        if (result != null) {

            Pattern p = Pattern.compile(ouId);
            Matcher m = p.matcher(result);
            if (m.find()) {
                isParent = true;
            }

        }

        return isParent;
    }

    public static Vector<String> getContentComponents(final String pid)
        throws SystemException {

        String query =
            "select $object from <#ri>" + "\n" + "where <info:fedora/" + pid
                + ">" + "<http://www.nsdl.org/ontologies/"
                + "relationships/hasComponent> " + "$object";

        String result = requestSimple(query);
        Vector<String> ids = new Vector<String>();
        if (result != null) {

            Matcher m = PATTERN_ID.matcher(result);
            while (m.find()) {

                ids.add(m.group());
            }
        }
        return ids;
    }

    public static Vector<String> getParents(final String id)
        throws SystemException {

        Vector<String> parentIds = new Vector<String>();
        String query =
            "select $object from <#ri>" + "\n" + "where <info:fedora/" + id
                + ">" + "<http://www.nsdl.org/ontologies/"
                + "relationships/hasParent> " + "$object";

        String result = requestSimple(query);
        if (result != null) {
            // replaceNewlines(result, "");
            Matcher m = PATTERN_ID_FOR_URI.matcher(result);
            while (m.find()) {
                String pid = m.group();
                int index = pid.lastIndexOf("/");
                if (index != -1) {
                    pid = pid.substring(pid.lastIndexOf("/") + 1);
                }

                parentIds.add(pid);
            }
        }
        return parentIds;
    }

    public static Vector<String> getChildren(final String id)
        throws SystemException {

        Vector<String> childrenIds = new Vector<String>();
        String query =
            "select $object from <#ri>" + "\n" + "where $object "
                + "<http://www.nsdl.org/ontologies/"
                + "relationships/hasParent> " + "<info:fedora/" + id + ">";

        String result = requestSimple(query);

        List<String> resultList = new Vector<String>();

        // TODO
        if (result != null) {
            String[] ids = result.split(">?[^>/]+/");

            for (int i = 0; i < ids.length; i++) {
                if (ids[i].length() > 0) {
                    int index = ids[i].indexOf('>');
                    if (index >= 0) {
                        resultList.add(ids[i].substring(0, index));
                    }
                    else {
                        resultList.add(ids[i]);
                    }
                }
            }
        }

        if (result != null) {
            // replaceNewlines(result, "");
            Matcher m = PATTERN_ID.matcher(result);
            while (m.find()) {
                childrenIds.add(m.group());

            }
        }
        return (Vector<String>) resultList;
    }

    public static String getAdminDescriptor(final String pid)
        throws SystemException {

        String query =
            "select $object from <#ri>" + "\n" + "where <info:fedora/" + pid
                + ">" + "<http://www.nsdl.org/ontologies/"
                + "relationships/hasAdminDescriptor> " + "$object";

        String result = requestSimple(query);
        String adminDescriptorId = null;
        Vector<String> ids = new Vector<String>();
        if (result != null) {

            Matcher m = PATTERN_ID.matcher(result);
            while (m.find()) {

                ids.add(m.group());
            }
            adminDescriptorId = ids.get(0);
        }
        return adminDescriptorId;

    }

    public static boolean isMemberOf(
        final String parentId, final String memberId) throws SystemException {
        String query =
            "select $object from <#ri>" + "\n" + "where <info:fedora/"
                + parentId + ">" + "$object '" + memberId + "'";

        String result = requestSimple(query);
        if (result != null
            && result
                .contains("<http://www.nsdl.org/ontologies/relationships/hasMember>")) {
            return true;
        }
        return false;
    }

    /**
     * The method triggers requestKowari() to retrieve the "subjects" from the
     * tripples, which contain: "object": adminGroup "predicat":
     * &lt;http://www.properties.de#type&gt;.
     * 
     * @return vector of subjects
     * @throws SystemException
     * @om
     */
    public static Vector<String> getAdminGroupIds() throws SystemException {
        Vector<String> adminGroupIds = new Vector<String>();
        String query =
            "select $object from <#ri>" + "\n" + "where $object "
                + "<http://www.properties.de#type> 'adminGroup'";

        String result = requestSimple(query);
        if (result != null) {
            // replaceNewlines(result, "");
            Matcher m = PATTERN_ID.matcher(result);
            while (m.find()) {
                adminGroupIds.add(m.group());

            }
        }

        return adminGroupIds;
    }

    /**
     * 
     * @param adminGroupId
     * @return
     * @throws SystemException
     */
    public static Vector<String> getContentItemIdsFromAdminGroup(
        final String adminGroupId) throws SystemException {
        Vector<String> contentItemIds = new Vector<String>();
        String query =
            "select $object from <#ri>" + "\n" + "where $object "
                + "<http://www.properties.de#adminGroupId> " + "'"
                + adminGroupId + "'";
        String result = requestSimple(query);
        if (result != null) {
            // replaceNewlines(result, "");
            Matcher m = PATTERN_ID.matcher(result);
            while (m.find()) {
                contentItemIds.add(m.group());

            }
        }
        return contentItemIds;
    }

    /**
     * 
     * @param pid
     * @return
     * @throws SystemException
     */
    public static String getStatusProperty(final String pid)
        throws SystemException {

        String query =
            "select $object from <#ri>" + "\n" + "where <info:fedora/" + pid
                + ">" + "<http://www.properties.de/status> " + "$object";

        String result = requestSimple(query);
        String status = null;
        if (result != null) {

            Matcher m = PATTERN_A_TO_Z_STRING.matcher(result);
            if (m.find()) {
                status = m.group();
                status = status.substring(1, status.length() - 1);
            }
        }
        return status;
    }

    /**
     * 
     * @param pid
     * @return
     * @throws SystemException
     */
    public static String getPropertiesElements(
        final String pid, final String elementName) throws SystemException {
        return getPropertiesElements(
            pid,
            elementName,
            de.escidoc.core.common.business.Constants.ITEM_PROPERTIES_NAMESPACE_URI);
    }

    public static String getPropertiesElements(
        final String pid, final String elementName, final String namespaceUri)
        throws SystemException {
        String query =
            "select $object from <#ri>" + "\n" + "where <info:fedora/" + pid
                + ">" + "<" + namespaceUri + "/" + elementName + "> "
                + "$object";

        String result = requestSimple(query);
        String status = null;
        if (result != null) {

            Matcher m = PATTERN_A_TO_Z_colon_STRING.matcher(result);
            if (m.find()) {
                status = m.group();
                status = status.substring(1, status.length() - 1);
            }
        }
        return status;
    }

    public static String getTitle(final String pid) throws SystemException {
        String query =
            "select $object from <#ri>"
                + "\n"
                + "where <info:fedora/"
                + pid
                + ">"
                + "<"
                + de.escidoc.core.common.business.Constants.RELATIONS_NAMESPACE_URI
                + "/" + "title> " + "$object";
        // String query = "select $object from <#ri>" + "\n"
        // + "where <info:fedora/" + pid + "> "
        // + "<info:fedora/fedora-system:def/model#label> " + "$object";
        String result = KowariUtility.requestSimple(query);
        if (result != null) {
            Matcher m = PATTERN_A_TO_Z_AND_WHITESPACE_STRING.matcher(result);
            if (m.find()) {
                result = m.group();
                result = result.substring(1, result.length() - 1);
            }
        }
        return result;
    }

    public static String getObjectType(final String pid) throws SystemException {
        // do not return if objectType unset
        String objectType = "unknown";

        String query =
            "select $object from <#ri>" + "\n" + "where <info:fedora/" + pid
                + "> "
                + "<http://www.nsdl.org/ontologies/relationships/objectType> "
                + "$object";
        String result = KowariUtility.requestSimple(query);
        if (result != null) {
            Matcher m = PATTERN_OBJECT_TYPE_STRING.matcher(result);
            if (m.find()) {
                result = m.group();
                objectType = result.substring(1, result.length() - 1);
            }
        }
        return objectType;
    }

    public static String getContext(final String id) throws SystemException {
        String result = null;

        if (isExist(id)) {
            String objectType = getObjectType(id);
            result =
                getPropertiesElements(id, "context",
                    "http://www.escidoc.de/schemas/" + objectType + "/0.1");
        }

        return result;
    }

    /**
     * The method triggers requestKowari() to retrieve the "object" from the
     * tripple (last modification date), which contains: "subject":
     * &lt;info:fedora/value of the provided pid"&gt; "predicat":
     * &lt;fedora-view:lastModifiedDate&gt;.
     * 
     * @param pid
     *            provided pid
     * @return last modification date
     * @throws SystemException
     *             Thrown in case of an internal error.
     * @om
     */
    public static String getLastModificationDate(final String pid)
        throws SystemException {
        String lastModificationDate = null;
        String query =
            "select $object from <#ri>" + "\n" + "where  <info:fedora/" + pid
                + "> <fedora-view:lastModifiedDate> $object ";

        String result = requestSimple(query);
        if (result != null) {

            Matcher m = PATTERN_MODIFICATION_DATE.matcher(result);
            if (m.find()) {
                String lastModDate = m.group();
                lastModificationDate =
                    lastModDate.substring(1, lastModDate.length() - 1);
            }
        }

        lastModificationDate =
            CalendarUtility.normalizeDate(lastModificationDate);

        return lastModificationDate;
    }

    public static String getCreatedDate(final String pid)
        throws SystemException {
        String createdDate = null;
        String query =
            "select $object from <#ri>" + "\n" + "where  <info:fedora/" + pid
                + "> <fedora-model:createdDate> $object ";

        String result = requestSimple(query);
        if (result != null) {

            Matcher m = PATTERN_MODIFICATION_DATE.matcher(result);
            if (m.find()) {
                String crDate = m.group();
                createdDate = crDate.substring(1, crDate.length() - 1);
            }
        }

        return CalendarUtility.normalizeDate(createdDate);
    }

    /**
     * The method triggers requestKowari() to check if there is a a tripple
     * where a subject is a provided pid and a predicat is
     * &lt;fedora-model:createdDate&gt;.
     * 
     * @param pid
     *            pid
     * @return true, if a triple exists false, otherwise
     * @throws SystemException
     * @om
     */
    public static boolean isExist(final String pid) throws SystemException {
        boolean isExist = false;
        String query =
            "select $object from <#ri>" + "\n" + "where  <info:fedora/" + pid
                + "> <fedora-model:createdDate> $object ";

        String result = requestSimple(query);
        if (result != null) {
            isExist = true;

        }

        return isExist;
    }

    /**
     * The method replaces all line breaks in the provided String with a
     * provided character.
     * 
     * @param in
     *            String to modify
     * @param replaceWith
     *            character to replace with
     * @return modified String
     * @om
     */
    public static String replaceNewlines(
        final String in, final String replaceWith) {
        return in.replaceAll("\r", replaceWith).replaceAll("\n", replaceWith);
    }

}
