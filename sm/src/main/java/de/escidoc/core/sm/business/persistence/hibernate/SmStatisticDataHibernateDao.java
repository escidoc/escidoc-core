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
package de.escidoc.core.sm.business.persistence.hibernate;

import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.persistence.hibernate.AbstractHibernateDao;
import de.escidoc.core.sm.business.persistence.SmStatisticDataDaoInterface;
import org.hibernate.SessionFactory;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Database-Backend for the Statistic-Data database-table.
 * 
 * @author MIH
 * @spring.bean id="persistence.SmStatisticDataDao"
 * @sm
 */
public class SmStatisticDataHibernateDao extends AbstractHibernateDao
    implements SmStatisticDataDaoInterface {

    private static final String QUERY_MIN_TIMESTAMP_FOR_SCOPE =
        "select min(timemarker) from StatisticData sd "
            + "where sd.scope.id = ?";

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.persistence.SmStatisticDataDaoInterface
     *      #saveStatisticData(java.lang.String, java.lang.String)
     * 
     * @param xmlData
     *            The statistic data xml.
     * @param scopeId
     *            The id of the scope.
     * @throws SqlDatabaseSystemException
     *             e
     * 
     * @sm
     */
    public void saveStatisticData(final String xmlData, final String scopeId)
        throws SqlDatabaseSystemException {
        Scope scope = new Scope();
        scope.setId(scopeId);
        StatisticData data =
            new StatisticData(xmlData,
                new Timestamp(System.currentTimeMillis()), scope);
        super.save(data);
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business
     *      .persistence.SmAggregationDefinitionsDaoInterface
     *      #retrieveAggregationDefinition(java.lang.Integer)
     * 
     * @param scopeId
     *            The scopeId.
     * @return Date min date of given scope
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     * 
     * @sm
     */
    public Date retrieveMinTimestamp(final String scopeId)
        throws SqlDatabaseSystemException {

        List results =
            getHibernateTemplate().find(QUERY_MIN_TIMESTAMP_FOR_SCOPE,
                new Object[] { scopeId });
        if (results != null) {
            return (Date) results.get(0);
        }
        else {
            return null;
        }
    }

    /**
     * Wrapper of setSessionFactory to enable bean stuff generation for this
     * bean.
     * 
     * @param mySessionFactory
     *            The sessionFactory to set.
     * @spring.property ref="sm.SessionFactory"
     * @sm
     */
    public final void setMySessionFactory(final SessionFactory mySessionFactory) {

        super.setSessionFactory(mySessionFactory);
    }

}
