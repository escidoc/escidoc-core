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
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.sm.business.interfaces.ReportHandlerInterface;
import de.escidoc.core.sm.business.persistence.DirectDatabaseAccessorInterface;
import de.escidoc.core.sm.business.persistence.SmReportDefinitionsDaoInterface;
import de.escidoc.core.sm.business.persistence.hibernate.ReportDefinition;
import de.escidoc.core.sm.business.renderer.interfaces.ReportRendererInterface;
import de.escidoc.core.sm.business.stax.handler.ParameterVo;
import de.escidoc.core.sm.business.stax.handler.ReportParametersStaxHandler;
import de.escidoc.core.sm.business.stax.handler.ReportParametersVo;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;

/**
 * An statistic Report resource handler.
 *
 * @author Michael Hoppe
 */
@Service("business.ReportHandler")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ReportHandler implements ReportHandlerInterface {

    @Autowired
    @Qualifier("persistence.SmReportDefinitionsDao")
    private SmReportDefinitionsDaoInterface dao;

    @Autowired
    @Qualifier("sm.persistence.DirectDatabaseAccessor")
    private DirectDatabaseAccessorInterface dbAccessor;

    @Autowired
    @Qualifier("eSciDoc.core.aa.business.renderer.VelocityXmlReportRenderer")
    private ReportRendererInterface renderer;

    /**
     * Private constructor to prevent initialization.
     */
    protected ReportHandler() {
    }

    /**
     * See Interface for functional description.
     *
     * @param xml the xml with parameters (report-parameters.xsd).
     * @return Returns the XML representation of the resource.
     * @throws ReportDefinitionNotFoundException
     *                             e.
     * @throws MissingMethodParameterException
     *                             e.
     * @throws InvalidSqlException e.
     * @throws SystemException     e.
     * @see ReportHandlerInterface #retrieve(java.lang.String)
     */
    @Override
    public String retrieve(final String xml) throws ReportDefinitionNotFoundException, MissingMethodParameterException,
        InvalidSqlException, SystemException {
        if (xml == null || xml.length() == 0) {
            throw new MissingMethodParameterException("xml may not be null");
        }

        //parse
        final StaxParser sp = new StaxParser();
        final ReportParametersStaxHandler handler = new ReportParametersStaxHandler();
        sp.addHandler(handler);
        try {
            sp.parse(xml);
        }
        catch (final Exception e) {
            throw new SystemException(e);
        }

        // Check if report-definition exists
        final ReportDefinition reportDefinition = dao.retrieve(handler.getReportParametersVo().getReportDefinitionId());

        final String sql = generateSql(handler.getReportParametersVo(), reportDefinition);

        // get Data as defined in sql
        final List results;
        try {
            results = dbAccessor.executeReadOnlySql(sql);
        }
        catch (final SqlDatabaseSystemException e) {
            throw new InvalidSqlException(e);
        }

        return renderer.render(results, reportDefinition);
    }

    /**
     * takes sql from reportDefinition, extends tablenames with db-schema-name and adds given Parameters.
     *
     * @param reportParametersVo reportParametersVo.
     * @param reportDefinition   reportDefinition-hibernate-object.
     * @return String sql
     * @throws MissingMethodParameterException
     *          e
     */
    private static String generateSql(
        final ReportParametersVo reportParametersVo, final ReportDefinition reportDefinition)
        throws MissingMethodParameterException {
        String sql = reportDefinition.getSql();
        if (sql == null || sql.length() == 0) {
            throw new MissingMethodParameterException("sql in reportDefinition may not be null");
        }

        // remove CDATA and entities
        sql = sql.replaceAll("\\s+", " ");

        // replace Parameters in sql
        final Collection<ParameterVo> parameterVos = reportParametersVo.getParameterVos();
        if (parameterVos != null) {
            for (final ParameterVo parameterVo : parameterVos) {
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
                        String replacementString = null;
                        if (parameterVo.getDateValue() != null) {
                            replacementString =
                                parameterVo.getDateValue().withZone(DateTimeZone.UTC).toString(
                                    de.escidoc.core.common.business.Constants.TIMESTAMP_FORMAT);
                        }
                        else if (parameterVo.getDecimalValue() != null) {
                            replacementString = parameterVo.getDecimalValue().toString();
                        }
                        else if (parameterVo.getStringValue() != null) {
                            replacementString = parameterVo.getStringValue();
                        }
                        if (!type.equals(Constants.DATABASE_FIELD_TYPE_NUMERIC)) {
                            replacementString = '\'' + replacementString + '\'';
                        }
                        sql =
                            sql.replaceAll("(?s)'?\"?\\{" + parameterVo.getName() + "\\}'?\"?", Matcher
                                .quoteReplacement(replacementString));
                    }
                }
            }
        }

        if (sql.matches("(?s).*\\{.*")) {
            throw new MissingMethodParameterException("could not replace all parameters in sql");
        }

        return sql;
    }

}
