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
package de.escidoc.core.common.business.queue.vo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import de.escidoc.core.common.bindings.common.LinkRequired;
import de.escidoc.core.common.bindings.statisticdata.StatisticRecord;
import de.escidoc.core.common.bindings.statisticdata.StatisticRecord.Parameter;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;

/**
 * Helper-Class to fill StatisticRecord-binding object.<br>
 * One <code>StatisticDataVo</code> object is stored as a
 * <code>ThreadLocal</code>. This is used by the
 * <code>StatisticInterceptor</code> and the business logic to define internal
 * statistic records of the escidoc infrastructure.<br/> This thread local
 * <code>StatisticDataVo</code> object is provided by the static method
 * <code>getThreadLocalInstance</code>.
 * 
 * @see de.escidoc.core.common.util.aop.StatisticInterceptor
 * @author MIH
 * @common
 */
public class StatisticDataVo {

    /**
     * The <code>ThreadLocal</code> object holding a
     * <code>StatisticDataVo</code> object.
     * 
     * @common
     */
    private static ThreadLocal<StatisticDataVo> statisticDataThreadLocal =
        new ThreadLocal<StatisticDataVo>() {

            protected synchronized StatisticDataVo initialValue() {
                try {
                    return new StatisticDataVo();
                }
                catch (SystemException e) {
                    return null;
                }
            }
        };

    private StatisticRecord statisticRecord;

    private static AppLogger log =
        new AppLogger(StatisticDataVo.class.getName());

    /**
     * initialize statisticRecord binding object with scopeId.
     * 
     * @throws SystemException
     *             e
     * 
     * @sm
     */
    public StatisticDataVo() throws SystemException {

        try {
            final String scopeId =
                EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.SM_FRAMEWORK_SCOPE_ID);
            statisticRecord = new StatisticRecord();
            LinkRequired scope = new LinkRequired();
            scope.setObjid(scopeId);
            statisticRecord.setScope(scope);
        }
        catch (IOException e) {
            log.error(e);
            throw new SystemException(e);
        }
    }

    /**
     * add a stringparameter.
     * 
     * @param name
     *            name as String
     * @param value
     *            value as String
     * 
     * @sm
     */
    public void addParameter(final String name, final String value) {
        Parameter parameter = new Parameter();
        parameter.setName(name);
        parameter.setStringvalue(value);
        statisticRecord.getParameter().add(parameter);
    }

    /**
     * add a decimalparameter.
     * 
     * @param name
     *            name as String
     * @param value
     *            value as String
     * 
     * @sm
     */
    public void addParameter(final String name, final BigDecimal value) {
        Parameter parameter = new Parameter();
        parameter.setName(name);
        parameter.setDecimalvalue(value);
        statisticRecord.getParameter().add(parameter);
    }

    /**
     * add a dateparameter.
     * 
     * @param name
     *            name as String
     * @param value
     *            value as String
     * 
     * @sm
     */
    public void addParameter(final String name, final Date value) {
        Parameter parameter = new Parameter();
        parameter.setName(name);
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(value.getTime());
        XMLGregorianCalendar xmlCal = null;
        try {
            xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        }
        catch (DatatypeConfigurationException e) {
            log.error(e);
        }
        parameter.setDatevalue(xmlCal);
        statisticRecord.getParameter().add(parameter);
    }

    /**
     * Removes all parameters.
     */
    public void clearParameters() {

        statisticRecord.getParameter().clear();
    }

    /**
     * @return the statisticRecord
     */
    public StatisticRecord getStatisticRecord() {
        return statisticRecord;
    }

    /**
     * Gets the <code>StatisticDataVo</code> object stored in a
     * <code>ThreadLocal</code>.
     * 
     * @return Returns the <code>StatisticDataVo</code>.
     * 
     * @see de.escidoc.core.common.util.aop.StatisticInterceptor
     * @common
     */
    public static StatisticDataVo getThreadLocalInstance() {

        return statisticDataThreadLocal.get();
    }

    /**
     * Sets the <code>StatisticDataVo</code> object stored in a
     * <code>ThreadLocal</code>.
     * 
     * @param data
     *            The <code>StatisticDataVo</code> to store in thread local.
     * 
     * @see de.escidoc.core.common.util.aop.StatisticInterceptor
     * @common
     */
    public static void setThreadLocalInstance(final StatisticDataVo data) {

        statisticDataThreadLocal.set(data);
    }

}
