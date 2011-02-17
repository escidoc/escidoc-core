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
package de.escidoc.core.sm.business;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSqlException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ReportDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.sm.business.interfaces.ReportHandlerInterface;
import de.escidoc.core.sm.business.persistence.DirectDatabaseAccessorInterface;
import de.escidoc.core.sm.business.persistence.SmReportDefinitionsDaoInterface;
import de.escidoc.core.sm.business.persistence.hibernate.ReportDefinition;
import de.escidoc.core.sm.business.renderer.interfaces.ReportRendererInterface;
import de.escidoc.core.sm.business.stax.handler.ParameterVo;
import de.escidoc.core.sm.business.stax.handler.ReportParametersStaxHandler;
import de.escidoc.core.sm.business.stax.handler.ReportParametersVo;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;

/**
 * An statistic Report resource handler.
 * 
 * @spring.bean id="business.ReportHandler" scope="prototype"
 * @author MIH
 * @sm
 */
public class ReportHandler implements ReportHandlerInterface {

    private static AppLogger log = new AppLogger(ReportHandler.class.getName());

    private SmReportDefinitionsDaoInterface dao;

    private DirectDatabaseAccessorInterface dbAccessor;

    private ReportRendererInterface renderer;

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.interfaces.ReportHandlerInterface
     *      #retrieve(java.lang.String)
     * 
     * @param xml
     *            the xml with parameters (report-parameters.xsd).
     * @return Returns the XML representation of the resource.
     * 
     * @throws ReportDefinitionNotFoundException
     *             e.
     * @throws MissingMethodParameterException
     *             e.
     * @throws InvalidSqlException
     *             e.
     * @throws SystemException
     *             e.
     * 
     * @sm
     */
    public String retrieve(final String xml) throws 
        ReportDefinitionNotFoundException, MissingMethodParameterException,
        InvalidSqlException, SystemException {
        if (log.isDebugEnabled()) {
            log.debug("ReportHandler does create");
        }
        if (xml == null || xml.equals("")) {
            log.error("xml may not be null");
            throw new MissingMethodParameterException("xml may not be null");
        }

        //parse
        StaxParser sp = new StaxParser();
        ReportParametersStaxHandler handler = 
                new ReportParametersStaxHandler();
        sp.addHandler(handler);
        try {
            sp.parse(xml);
        } catch (Exception e) {
            log.error(e);
            throw new SystemException(e);
        }
        
        // Check if report-definition exists
        ReportDefinition reportDefinition =
            dao.retrieve(handler.getReportParametersVo().getReportDefinitionId());

        String sql = generateSql(
            handler.getReportParametersVo(), reportDefinition);

        // get Data as defined in sql
        List results;
        try {
            results = dbAccessor.executeReadOnlySql(sql);
        }
        catch (SqlDatabaseSystemException e) {
            throw new InvalidSqlException(e);
        }

        return renderer.render(results, reportDefinition);
    }

    /**
     * takes sql from reportDefinition, extends tablenames with db-schema-name
     * and adds given Parameters.
     * 
     * @param reportParametersVo
     *            reportParametersVo.
     * @param reportDefinition
     *            reportDefinition-hibernate-object.
     * @throws MissingMethodParameterException
     *             e
     * 
     * @return String sql
     * 
     * @sm
     */
    private String generateSql(
        final ReportParametersVo reportParametersVo,
        final ReportDefinition reportDefinition)
        throws MissingMethodParameterException {
        String sql = reportDefinition.getSql();
        if (sql == null || sql.equals("")) {
            log.error("sql in reportDefinition may not be null");
            throw new MissingMethodParameterException(
                "sql in reportDefinition may not be null");
        }

        // remove CDATA and entities
        sql = sql.replaceAll("\\s+", " ");

        // replace Parameters in sql
        Collection<ParameterVo> parameterVos = 
                    reportParametersVo.getParameterVos();
        if (parameterVos != null) {
            for (ParameterVo parameterVo : parameterVos) {
                String replacementString = null;
                if (parameterVo != null) {
                    String type = null;
                    if (parameterVo.getDateValue() != null) {
                        type = Constants.DATABASE_FIELD_TYPE_DATE;
                    }
                    else if (parameterVo.getDecimalValue() != null) {
                        type = Constants.DATABASE_FIELD_TYPE_NUMERIC;
                    }
                    else if (parameterVo.getStringValue() != null) {
                        type = Constants.DATABASE_FIELD_TYPE_TEXT;
                    }
                    if (type != null) {
                        if (parameterVo.getDateValue() != null) {
                            replacementString =
                                parameterVo.getDateValue()
                                .toString("yyyy-MM-dd HH:mm:ss.SSS");
                        }
                        else if (parameterVo.getDecimalValue() != null) {
                            replacementString =
                                parameterVo.getDecimalValue().toString();
                        }
                        else if (parameterVo.getStringValue() != null) {
                            replacementString = parameterVo.getStringValue();
                        }
                        if (!type.equals(Constants.DATABASE_FIELD_TYPE_NUMERIC)) {
                            replacementString = "'" + replacementString + "'";
                        }
                        sql =
                            sql.replaceAll("(?s)'?\"?\\{" + parameterVo.getName()
                                + "\\}'?\"?", 
                                Matcher.quoteReplacement(replacementString));
                    }
                }
            }
        }

        if (sql.matches("(?s).*\\{.*")) {
            throw new MissingMethodParameterException(
                "could not replace all parameters in sql");
        }

        return sql;
    }

    /**
     * Setter for the dao.
     * 
     * @spring.property ref="persistence.SmReportDefinitionsDao"
     * @param dao
     *            The data access object.
     * 
     * @sm
     */
    public void setDao(final SmReportDefinitionsDaoInterface dao) {
        this.dao = dao;
    }

    /**
     * Setting the directDatabaseAccessor.
     * 
     * @param dbAccessorIn
     *            The directDatabaseAccessor to set.
     * @spring.property ref="sm.persistence.DirectDatabaseAccessor"
     */
    public final void setDirectDatabaseAccessor(
        final DirectDatabaseAccessorInterface dbAccessorIn) {
        this.dbAccessor = dbAccessorIn;
    }

    /**
     * Injects the renderer.
     * 
     * @param renderer
     *            The renderer to inject.
     * 
     * @spring.property ref="eSciDoc.core.aa.business.renderer.VelocityXmlReportRenderer"
     * @aa
     */
    public void setRenderer(
            final ReportRendererInterface renderer) {
        this.renderer = renderer;
    }

}
