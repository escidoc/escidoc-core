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
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.sm.business.persistence.SmPreprocessingLogsDaoInterface;
import org.hibernate.SessionFactory;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Database-Backend for the PreprocessingLogs database-table.
 * 
 * @author MIH
 * @spring.bean id="persistence.SmPreprocessingLogsDao"
 * @sm
 */
public class SmPreprocessingLogsHibernateDao 
    extends AbstractHibernateDao
    implements SmPreprocessingLogsDaoInterface {

    public static final String QUERY_LOGS_BY_AGG_DEF_ID = 
        "from PreprocessingLog pl where pl.aggregationDefinition.id = ?";

    public static final String QUERY_LOGS_BY_DATE = 
        "from PreprocessingLog pl where pl.processingDate = ?";

    public static final String QUERY_LOGS_BY_AGG_DEF_ID_AND_DATE = 
        "from PreprocessingLog pl where pl.aggregationDefinition.id = ? "
        + "and pl.processingDate = ?";

    public static final String QUERY_LOGS_BY_AGG_DEF_ID_AND_ERROR = 
        "from PreprocessingLog pl where pl.aggregationDefinition.id = ? "
        + "and pl.hasError = ?";

    public static final String QUERY_LOGS_BY_DATE_AND_ERROR = 
        "from PreprocessingLog pl where pl.processingDate = ? "
        + "and pl.hasError = ?";

    public static final String QUERY_LOGS_BY_AGG_DEF_ID_AND_DATE_AND_ERROR = 
        "from PreprocessingLog pl where pl.aggregationDefinition.id = ? "
        + "and pl.processingDate = ? and pl.hasError = ?";

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.persistence.SmPreprocessingLogsDaoInterface
     *      #savePreprocessingLog(PreprocessingLog)
     * 
     * @param preprocessingLog
     *            preprocessingLog-Hibernate Object.
     * @return Integer primary key of created Object
     * 
     * @sm
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     */
    @Override
    public String savePreprocessingLog(
            final PreprocessingLog preprocessingLog) 
                        throws SqlDatabaseSystemException {
        String savedPreprocessingLog = (String) super.save(preprocessingLog);
        super.flush();
        return savedPreprocessingLog;
        
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.persistence.SmPreprocessingLogsDaoInterface
     *      #retrievePreprocessingLogs(java.lang.String)
     * 
     * @param aggregationDefinitionId
     *            aggregationDefinitionId
     * @return Collection of PreprocessingLogs as Hibernate-Objects
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     * 
     * @sm
     */
    @Override
    public Collection<PreprocessingLog>
        retrievePreprocessingLogs(final String aggregationDefinitionId)
        throws SqlDatabaseSystemException {
        List<PreprocessingLog> results = getHibernateTemplate().find(
                QUERY_LOGS_BY_AGG_DEF_ID,
                new Object[] { aggregationDefinitionId });
        return results;
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.persistence.SmPreprocessingLogsDaoInterface
     *      #retrievePreprocessingLogs(java.lang.Date)
     * 
     * @param processingDate
     *            processingDate
     * @return Collection of PreprocessingLogs as Hibernate-Objects
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     * 
     * @sm
     */
    @Override
    public Collection<PreprocessingLog>
        retrievePreprocessingLogs(final Date processingDate)
        throws SqlDatabaseSystemException {
        List<PreprocessingLog> results = getHibernateTemplate().find(
                QUERY_LOGS_BY_DATE,
                new Object[] { processingDate });
        return results;
        
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.persistence.SmPreprocessingLogsDaoInterface
     *      #retrievePreprocessingLogs(java.lang.String, java.lang.Date)
     * 
     * @param aggregationDefinitionId
     *            aggregationDefinitionId
     * @param processingDate
     *            processingDate
     * @return Collection of PreprocessingLogs as Hibernate-Objects
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     * 
     * @sm
     */
    @Override
    public Collection<PreprocessingLog>
        retrievePreprocessingLogs(final String aggregationDefinitionId, 
                final Date processingDate)
        throws SqlDatabaseSystemException {
        List<PreprocessingLog> results = getHibernateTemplate().find(
                QUERY_LOGS_BY_AGG_DEF_ID_AND_DATE,
                new Object[] { aggregationDefinitionId, processingDate });
        return results;
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.persistence.SmPreprocessingLogsDaoInterface
     *      #retrievePreprocessingLogs(java.lang.String, boolean)
     * 
     * @param aggregationDefinitionId
     *            aggregationDefinitionId
     * @param hasError
     *            hasError
     * @return Collection of PreprocessingLogs as Hibernate-Objects
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     * 
     * @sm
     */
    @Override
    public Collection<PreprocessingLog>
        retrievePreprocessingLogs(final String aggregationDefinitionId, 
                final boolean hasError)
        throws SqlDatabaseSystemException {
        List<PreprocessingLog> results = getHibernateTemplate().find(
                QUERY_LOGS_BY_AGG_DEF_ID_AND_ERROR,
                new Object[] { aggregationDefinitionId, hasError });
        return results;
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.persistence.SmPreprocessingLogsDaoInterface
     *      #retrievePreprocessingLogs(java.lang.Date, boolean)
     * 
     * 
     * @param processingDate
     *            processingDate
     * @param hasError
     *            hasError
     * @return Collection of PreprocessingLogs as Hibernate-Objects
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     * 
     * @sm
     */
    @Override
    public Collection<PreprocessingLog>
        retrievePreprocessingLogs(
                final Date processingDate, final boolean hasError)
        throws SqlDatabaseSystemException {
        List<PreprocessingLog> results = getHibernateTemplate().find(
                QUERY_LOGS_BY_DATE_AND_ERROR,
                new Object[] { processingDate, hasError });
        return results;
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.persistence.SmPreprocessingLogsDaoInterface
     *      #retrievePreprocessingLogs(java.lang.String,java.lang.Date, boolean)
     * 
     * 
     * @param aggregationDefinitionId
     *            aggregationDefinitionId
     * @param processingDate
     *            processingDate
     * @param hasError
     *            hasError
     * @return Collection of PreprocessingLogs as Hibernate-Objects
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     * 
     * @sm
     */
    @Override
    public Collection<PreprocessingLog>
        retrievePreprocessingLogs(final String aggregationDefinitionId,
                final Date processingDate, final boolean hasError)
        throws SqlDatabaseSystemException {
        List<PreprocessingLog> results = getHibernateTemplate().find(
                QUERY_LOGS_BY_AGG_DEF_ID_AND_DATE_AND_ERROR,
                new Object[] { 
                       aggregationDefinitionId, processingDate, hasError });
        return results;
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
