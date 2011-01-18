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
package de.escidoc.core.om.business.fedora;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.sql.DataSource;

import org.nsdl.mptstore.util.NTriplesUtil;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.mptstore.MPTTripleStoreUtility;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.util.service.BeanLocator;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * @spring.bean id="business.TripleStoreRelationsUtility"
 * @author ROF
 * 
 * @om
 * 
 * 
 */
@Deprecated
public class TripleStoreRelationsUtility {

    private String tableWithSource = null;

    private String tableWithTarget = null;

    private String tableWithPredicate = null;

    private String tableWithStatus = null;

    private String tableWithTargetVersion = null;

    private String tableWithObjectCreatedDate = null;

    private static TripleStoreRelationsUtility tsru;

    private TripleStoreUtility tripleStoreUtility = null;

    /**
     * Injects the data source.
     * 
     * @spring.property ref="fedora.triplestore.DataSource"
     * @param driverManagerDataSource
     */
    public void setMyDataSource(final DataSource myDataSource) {
        // super.setDataSource(myDataSource);
    }

    private void setTablesNames() throws SystemException {
        MPTTripleStoreUtility tripleStoreUtility =
            (MPTTripleStoreUtility) this.tripleStoreUtility;
        tableWithSource =
            tripleStoreUtility
                .getTableName(de.escidoc.core.common.business.Constants.RELATIONS_NAMESPACE_URI
                    + "/hasSource");
        tableWithTarget =
            tripleStoreUtility
                .getTableName(de.escidoc.core.common.business.Constants.RELATIONS_NAMESPACE_URI
                    + "/hasTarget");
        tableWithPredicate =
            tripleStoreUtility
                .getTableName(de.escidoc.core.common.business.Constants.CONTENT_RELATIONS_NAMESPACE_URI
                    + "/predicate");
        tableWithStatus =
            tripleStoreUtility
                .getTableName(de.escidoc.core.common.business.Constants.CONTENT_RELATIONS_NAMESPACE_URI
                    + "/status");
        tableWithStatus =
            tripleStoreUtility
                .getTableName(de.escidoc.core.common.business.Constants.CONTENT_RELATIONS_NAMESPACE_URI
                    + "/status");
        tableWithTargetVersion =
            tripleStoreUtility
                .getTableName(de.escidoc.core.common.business.Constants.CONTENT_RELATIONS_NAMESPACE_URI
                    + "/hasTargetVersion");
        tableWithObjectCreatedDate =
            tripleStoreUtility
                .getTableName("info:fedora/fedora-system:def/model#createdDate");
    }

    /**
     * Injects the TripleStore utility.
     * 
     * @spring.property ref="business.TripleStoreUtility"
     * @param tripleStoreUtility
     *            TripleStoreUtility from Spring
     */
    public void setTripleStoreUtility(
        final TripleStoreUtility tripleStoreUtility) {
        this.tripleStoreUtility = tripleStoreUtility;
    }

    public String createQuery(
        final String id, boolean fixedRelation, final String status)
        throws TripleStoreSystemException {

        StringBuffer queryResultBuf = new StringBuffer();
        String queryResult = null;
        if ((tableWithSource != null) && (tableWithPredicate != null)) {
            queryResultBuf.append("SELECT ");
            queryResultBuf.append(tableWithSource);
            queryResultBuf.append(".s ");
            if (fixedRelation) {
                queryResultBuf.append(",");
                queryResultBuf.append(tableWithTarget);
                queryResultBuf.append(".o, ");
                queryResultBuf.append(tableWithPredicate);
                queryResultBuf.append(".o ");
                if (tableWithTargetVersion != null) {
                    queryResultBuf.append(", ");
                    queryResultBuf.append(tableWithTargetVersion);
                    queryResultBuf.append(".o ");
                }
            }
            queryResultBuf.append("FROM ");
            queryResultBuf.append(tableWithSource);
            queryResultBuf.append(", ");
            queryResultBuf.append(tableWithTarget);
            queryResultBuf.append(", ");
            if (status != null) {
                queryResultBuf.append(tableWithStatus);
                queryResultBuf.append(", ");
            }
            queryResultBuf.append(tableWithPredicate);
            if (tableWithTargetVersion != null) {
                queryResultBuf.append(", ");
                queryResultBuf.append(tableWithTargetVersion);
            }
            queryResultBuf.append(" WHERE ");

            queryResultBuf.append(tableWithSource);
            queryResultBuf.append(".o='<info:fedora/");
            queryResultBuf.append(id);
            queryResultBuf.append(">' AND ");
            queryResultBuf.append(tableWithTarget);
            queryResultBuf.append(".s=");
            queryResultBuf.append(tableWithSource);
            queryResultBuf.append(".s AND ");
            queryResultBuf.append(tableWithTarget);
            queryResultBuf.append(".s=");
            queryResultBuf.append(tableWithPredicate);
            queryResultBuf.append(".s");
            if (status != null) {
                queryResultBuf.append(" AND ");
                queryResultBuf.append(tableWithPredicate);
                queryResultBuf.append(".s=");
                queryResultBuf.append(tableWithStatus);
                queryResultBuf.append(".s AND ");
                queryResultBuf.append(tableWithStatus);
                queryResultBuf.append(".o='\"" + status + "\"'");
            }
            if (tableWithTargetVersion != null) {
                queryResultBuf.append(" AND ");
                queryResultBuf.append(tableWithPredicate);
                queryResultBuf.append(".s=");
                queryResultBuf.append(tableWithTargetVersion);
                queryResultBuf.append(".s");
            }

            queryResult = queryResultBuf.toString();
        }
        return queryResult;

    }

    /**
     * 
     * @param ids
     * @return
     * @throws TripleStoreSystemException
     */
    public String createQueryForRemoveWithRelationIds(final Vector<String> ids)
        throws TripleStoreSystemException {

        StringBuffer queryResultBuf = new StringBuffer();
        String queryResult = null;
        if ((ids != null) && (ids.size() > 0)) {
            Iterator<String> it = ids.iterator();
            if ((tableWithSource != null) && (tableWithPredicate != null)) {
                queryResultBuf.append("SELECT DISTINCT ");
                queryResultBuf.append(tableWithSource);
                queryResultBuf.append(".s, ");
                queryResultBuf.append(tableWithSource);
                queryResultBuf.append(".o, ");
                queryResultBuf.append(tableWithStatus);
                queryResultBuf.append(".o FROM ");
                queryResultBuf.append(tableWithSource);
                queryResultBuf.append(", ");
                queryResultBuf.append(tableWithStatus);

                queryResultBuf.append(" WHERE ");
                queryResultBuf.append(tableWithSource);
                queryResultBuf.append(".s=");
                queryResultBuf.append(tableWithStatus);
                queryResultBuf.append(".s AND ");
                while (it.hasNext()) {
                    String id = it.next();
                    queryResultBuf.append(tableWithSource);
                    queryResultBuf.append(".s='<info:fedora/");
                    queryResultBuf.append(id);
                    queryResultBuf.append(">'");
                    if (it.hasNext()) {
                        queryResultBuf.append(" OR ");
                    }

                }
                queryResult = queryResultBuf.toString();
            }

        }

        return queryResult;

    }

    // public Vector<String> getVersionsRelationIds(final Collection ids,
    // final String createdDate, final String lastModificationDate)
    // throws TripleStoreSystemException, XmlParserSystemException {
    // setTablesNames();
    // Vector <String >relationIds = new Vector <String>();
    // Vector result =
    // executeCompexQuery(createQueryForRelationsWithCreatedDate(ids));
    // Iterator it = result.iterator();
    // try {
    // GregorianCalendar createdDateCalender = DatatypeFactory
    // .newInstance().newXMLGregorianCalendar(createdDate).toGregorianCalendar();
    // GregorianCalendar lastModDateCalender = DatatypeFactory
    // .newInstance().newXMLGregorianCalendar(lastModificationDate).toGregorianCalendar();
    // while (it.hasNext()) {
    // String[] nextResult = (String[]) it.next();
    // int indexFirst = nextResult[1].indexOf("\"");
    // if ((indexFirst != -1)) {
    // nextResult[1] = nextResult[1].substring(0, indexFirst);
    // GregorianCalendar relationTimestamp = DatatypeFactory
    // .newInstance().newXMLGregorianCalendar(nextResult[1]).toGregorianCalendar();
    //
    // if (lastModDateCalender.after(relationTimestamp)
    // && relationTimestamp.after(createdDateCalender)) {
    // relationIds.add(nextResult[0]);
    // }
    //
    // }
    //
    // }
    // } catch (DatatypeConfigurationException e){
    // throw new XmlParserSystemException(e);
    // }
    // return relationIds;
    // }

    public Vector getRelationsSourceIds(final Vector<String> ids)
        throws SystemException {
        setTablesNames();

        Vector result1 =
            executeCompexQuery(createQueryForRemoveWithRelationIds(ids));
        return result1;
    }

    public String createQueryForDeleteWithSourceIds(final String id)
        throws TripleStoreSystemException {

        StringBuffer queryResultBuf = new StringBuffer();
        String queryResult = null;
        if ((tableWithSource != null) && (tableWithPredicate != null)) {
            queryResultBuf.append("SELECT DISTINCT ");
            queryResultBuf.append(tableWithSource);
            queryResultBuf.append(".s FROM ");
            queryResultBuf.append(tableWithSource);

            queryResultBuf.append(" WHERE ");
            queryResultBuf.append(tableWithSource);
            queryResultBuf.append(".o='<info:fedora/");
            queryResultBuf.append(id);
            queryResultBuf.append(">'");
        }

        queryResult = queryResultBuf.toString();

        return queryResult;

    }

    public String createQueryForDeleteWithTargetIds(final String id)
        throws TripleStoreSystemException {

        StringBuffer queryResultBuf = new StringBuffer();
        String queryResult = null;
        if ((tableWithTarget != null) && (tableWithPredicate != null)) {
            queryResultBuf.append("SELECT DISTINCT ");
            queryResultBuf.append(tableWithTarget);
            queryResultBuf.append(".s FROM ");
            queryResultBuf.append(tableWithTarget);

            queryResultBuf.append(" WHERE ");
            queryResultBuf.append(tableWithTarget);
            queryResultBuf.append(".o='<info:fedora/");
            queryResultBuf.append(id);
            queryResultBuf.append(">'");
        }

        queryResult = queryResultBuf.toString();

        return queryResult;

    }

    public Vector<String> getRelationIds(final String id)
        throws SystemException {
        setTablesNames();
        Vector<String> relationIds = new Vector();
        Vector result1 =
            executeCompexQuery(createQueryForDeleteWithSourceIds(id));
        Iterator resultIterator1 = result1.iterator();
        while (resultIterator1.hasNext()) {
            String[] nextResult = (String[]) resultIterator1.next();
            relationIds.add(nextResult[0]);
        }
        Vector result2 =
            executeCompexQuery(createQueryForDeleteWithTargetIds(id));
        Iterator resultIterator2 = result2.iterator();
        while (resultIterator2.hasNext()) {
            String[] nextResult = (String[]) resultIterator2.next();
            relationIds.add(nextResult[0]);
        }

        return relationIds;
    }

    public HashMap getFixedRelationData(final String id, final String status)
        throws SystemException {
        setTablesNames();
        Vector result = executeCompexQuery(createQuery(id, true, status));
        Iterator resultIterator = result.iterator();
        HashMap relationsData = new HashMap();
        while (resultIterator.hasNext()) {
            String[] nextResult = (String[]) resultIterator.next();
            HashMap relationData = new HashMap();
            // relationData.put("relationId", nextResult[0]);
            relationData.put("target", nextResult[1]);
            relationData.put("predicate", nextResult[2]);
            if (tableWithTargetVersion != null) {
                relationData.put("targetVersion", nextResult[3]);
            }
            relationsData.put(nextResult[0], relationData);
        }

        return relationsData;
    }

    public HashMap getFixedRelationsHash(final String id, final String status)
        throws SystemException {
        setTablesNames();
        HashMap relationsData = new HashMap();
        String queryResult = createQuery(id, true, status);
        if (queryResult != null) {
            Vector result = executeCompexQuery(queryResult);
            Iterator resultIterator = result.iterator();
            while (resultIterator.hasNext()) {
                String[] nextResult = (String[]) resultIterator.next();
                StringBuffer relationDataHashBuffer = new StringBuffer();
                // put target id without version
                relationDataHashBuffer.append(nextResult[1]);
                relationDataHashBuffer.append("#");
                // put predicate
                relationDataHashBuffer.append(nextResult[2]);
                if (tableWithTargetVersion != null) {
                    // put target version
                    relationDataHashBuffer.append("#");
                    relationDataHashBuffer.append(nextResult[3]);
                }
                relationsData.put(relationDataHashBuffer.toString(),
                    nextResult[0]);
            }
        }
        return relationsData;
    }

    public HashMap getFloatingRelationsHash(final String id, final String status)
        throws SystemException {
        HashMap relationsData = new HashMap();
        String queryResult = createQueryForFloatingRelations(id, status);
        if (queryResult != null) {
            Vector result = executeCompexQuery(queryResult);
            Iterator resultIterator = result.iterator();

            while (resultIterator.hasNext()) {
                String[] nextResult = (String[]) resultIterator.next();
                StringBuffer relationDataHashBuffer = new StringBuffer();
                // put target id without version
                relationDataHashBuffer.append(nextResult[1]);
                relationDataHashBuffer.append("#");
                // put predicate
                relationDataHashBuffer.append(nextResult[2]);

                relationsData.put(relationDataHashBuffer.toString(),
                    nextResult[0]);
            }
        }
        return relationsData;
    }

    public HashMap getFloatingRelationData(final String id, final String status)
        throws SystemException {

        HashMap relationsData = new HashMap();
        String queryResult = createQueryForFloatingRelations(id, status);
        if (queryResult != null) {
            Vector result = executeCompexQuery(queryResult);
            Iterator resultIterator = result.iterator();

            while (resultIterator.hasNext()) {
                String[] nextResult = (String[]) resultIterator.next();
                HashMap relationData = new HashMap();
                // relationData.put("relationId", nextResult[0]);
                relationData.put("target", nextResult[1]);
                relationData.put("predicate", nextResult[2]);
                // if (tableWithTargetVersion != null) {
                // relationData.put("targetVersion", nextResult[3]);
                // }
                relationsData.put(nextResult[0], relationData);
            }
        }
        return relationsData;

    }

    public String createQueryForFloatingRelations(
        final String id, final String status) throws SystemException {
        setTablesNames();
        StringBuffer queryResultBuf = new StringBuffer();
        String queryResult = null;
        if ((tableWithSource != null) && (tableWithPredicate != null)) {
            queryResultBuf.append("SELECT ");
            queryResultBuf.append(tableWithSource);
            queryResultBuf.append(".s, ");
            queryResultBuf.append(tableWithTarget);
            queryResultBuf.append(".o, ");
            // queryResultBuf.append(tableWithStatus);
            // queryResultBuf.append(".o, ");
            queryResultBuf.append(tableWithPredicate);
            queryResultBuf.append(".o ");

            queryResultBuf.append("FROM ");
            queryResultBuf.append(tableWithSource);
            queryResultBuf.append(", ");
            queryResultBuf.append(tableWithTarget);
            queryResultBuf.append(", ");
            queryResultBuf.append(tableWithPredicate);
            if (status != null) {
                queryResultBuf.append(", ");
                queryResultBuf.append(tableWithStatus);
            }
            queryResultBuf.append(" WHERE ");
            if (status != null) {
                queryResultBuf.append(tableWithStatus);
                queryResultBuf.append(".o='\"" + status + "\"' AND ");
            }

            queryResultBuf.append(tableWithSource);
            queryResultBuf.append(".o='<info:fedora/");
            queryResultBuf.append(id);
            queryResultBuf.append(">' AND ");
            queryResultBuf.append(tableWithTarget);
            queryResultBuf.append(".s=");
            queryResultBuf.append(tableWithSource);
            queryResultBuf.append(".s AND ");
            queryResultBuf.append(tableWithTarget);
            queryResultBuf.append(".s=");
            queryResultBuf.append(tableWithPredicate);
            queryResultBuf.append(".s");
            if (status != null) {
                queryResultBuf.append(" AND ");

                queryResultBuf.append(tableWithPredicate);
                queryResultBuf.append(".s=");
                queryResultBuf.append(tableWithStatus);
                queryResultBuf.append(".s");
            }
            if (tableWithTargetVersion != null) {
                queryResultBuf.append(" AND ");
                queryResultBuf.append(tableWithSource);
                queryResultBuf.append(".s");
                queryResultBuf.append(" NOT IN (");
                queryResultBuf.append(createQuery(id, false, status));
                queryResultBuf.append(" )");
            }

            queryResult = queryResultBuf.toString();
            // if (!checkQuery(queryResult)) {
            // return relationsData;
            // }

        }
        return queryResult;

    }

    public HashMap[] getRelationsHash(final String id, final String status)
        throws SystemException {
        HashMap[] result = null;
        HashMap floating = getFloatingRelationsHash(id, status);
        HashMap fixed = null;
        if (tableWithTargetVersion != null) {
            fixed = getFixedRelationsHash(id, status);
            result = new HashMap[2];
            result[0] = floating;
            result[1] = fixed;
        }
        else {
            result = new HashMap[1];
            result[0] = floating;
        }
        return result;
    }

    public HashMap[] getRelationsData(final String id, final String status)
        throws SystemException {
        HashMap[] result = null;
        HashMap floating = getFloatingRelationData(id, status);
        HashMap fixed = null;
        if (tableWithTargetVersion != null) {
            fixed = getFixedRelationData(id, status);
            result = new HashMap[2];
            result[0] = floating;
            result[1] = fixed;
        }
        else {
            result = new HashMap[1];
            result[0] = floating;
        }
        return result;
    }

    public Vector executeCompexQuery(String sqlQuery)
        throws TripleStoreSystemException {

        Vector result = new Vector();
        if ((sqlQuery != null) && (sqlQuery.length() > 0)) {
            Connection con = null;
            ResultSet rs = null;
            try {
                con = null;// getConnection();
                rs = con.prepareStatement(sqlQuery).executeQuery();
                ResultSetMetaData rsm = rs.getMetaData();
                int size = rsm.getColumnCount();

                while (rs.next()) {
                    String[] results = new String[size];
                    for (int i = 1; i < size + 1; i++) {
                        String id = rs.getString(i);
                        if (id.startsWith("\"")) {
                            id = id.substring(1, id.length() - 1);
                            try {
                                id = NTriplesUtil.unescapeLiteralValue(id);
                            }
                            catch (ParseException e) {
                                throw new TripleStoreSystemException(e);
                            }
                        }
                        else if (id.startsWith("<")) {
                            id = XmlUtility.getIdFromURI(id);
                        }
                        results[i - 1] = id;
                    }
                    result.add(results);

                }

            }
            catch (CannotGetJdbcConnectionException e) {
                throw new TripleStoreSystemException(e.getMessage(), e);
            }
            catch (SQLException e) {
                throw new TripleStoreSystemException("Failed to execute query"
                    + sqlQuery, e);
            }
            finally {
                if (con != null) {
                    // releaseConnection(con);
                }
                if (rs != null) {
                    try {
                        rs.close();
                    }
                    catch (SQLException e) {
                        // Ignore because the result set is already closed.
                    }
                }

            }
        }
        return result;
    }

    public static TripleStoreRelationsUtility getInstance()
        throws SystemException {

        if (TripleStoreRelationsUtility.tsru == null) {
            TripleStoreRelationsUtility.tsru =
                (TripleStoreRelationsUtility) BeanLocator.getBean(
                    BeanLocator.OM_FACTORY_ID,
                    "business.TripleStoreRelationsUtility");
        }
        return TripleStoreRelationsUtility.tsru;
    }
}
