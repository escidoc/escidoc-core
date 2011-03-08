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

import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.sm.business.interfaces.StatisticDataHandlerInterface;
import de.escidoc.core.sm.business.persistence.SmStatisticDataDaoInterface;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;

/**
 * An statistic data resource handler.
 * 
 * @spring.bean id="business.StatisticDataHandler" scope="prototype"
 * @author MIH
 * @sm
 */
public class StatisticDataHandler implements StatisticDataHandlerInterface {

    private static final AppLogger LOGGER =
        new AppLogger(StatisticDataHandler.class.getName());

    private SmStatisticDataDaoInterface dao;

    private SmXmlUtility xmlUtility;

    private CamelContext camelContext;

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.interfaces .StatisticDataHandlerInterface
     *      #create(java.lang.String)
     * 
     * @param xmlData
     *            statistic data as xml in statistic-data schema.
     * 
     * @throws MissingMethodParameterException
     *             ex
     * @throws SystemException
     *             ex
     * 
     * @sm
     */
    @Override
    public void create(final String xmlData)
        throws MissingMethodParameterException, SystemException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("StatisticDataHandler does create");
        }
        if (xmlData == null || xmlData.length() == 0) {
            throw new MissingMethodParameterException("xml may not be null");
        }
        final ProducerTemplate producerTemplate = this.camelContext.createProducerTemplate();
        producerTemplate.asyncSendBody("jms:queue:de.escidoc.core.statistic.StatisticService.input?disableReplyTo=true", xmlData);
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.interfaces .StatisticDataHandlerInterface
     *      #insertStatisticData(java.lang.String)
     * 
     * @param xmlData
     *            xmlData
     * 
     * @throws ScopeNotFoundException
     *             ex
     * @throws MissingMethodParameterException
     *             ex
     * @throws XmlSchemaValidationException
     *             ex
     * @throws XmlCorruptedException
     *             ex
     * @throws SystemException
     *             e
     */
    @Override
    public void insertStatisticData(final String xmlData)
        throws ScopeNotFoundException, MissingMethodParameterException,
        XmlSchemaValidationException, XmlCorruptedException, SystemException {
        if (xmlData == null || xmlData.length() == 0) {
            throw new MissingMethodParameterException("xml may not be null");
        }
        XmlUtility.validate(xmlData, XmlUtility
            .getStatisticDataSchemaLocation());

        final String scopeId = xmlUtility.getScopeId(xmlData);

        if (scopeId == null || scopeId.length() == 0) {
            throw new ScopeNotFoundException("scopeId is null");
        }
        try {
            dao.saveStatisticData(xmlData, scopeId);
        }
        catch (SqlDatabaseSystemException e) {
            if (e.getCause() != null
                && e.getCause().getClass() != null
                && "ConstraintViolationException".equals(e.getCause().getClass().getSimpleName())) {
                throw new ScopeNotFoundException("scope with id " + scopeId
                    + " not found in database");
            }
            else {
                LOGGER.error(e);
                throw e;
            }
        }
    }


    /**
     * Setter for the dao.
     * 
     * @spring.property ref="persistence.SmStatisticDataDao"
     * @param dao
     *            The data access object.
     * 
     * @sm
     */
    public void setDao(final SmStatisticDataDaoInterface dao) {
        this.dao = dao;
    }

    /**
     * Setting the xmlUtility.
     * 
     * @param xmlUtility
     *            The xmlUtility to set.
     * @spring.property ref="business.sm.XmlUtility"
     */
    public final void setXmlUtility(final SmXmlUtility xmlUtility) {
        this.xmlUtility = xmlUtility;
    }

    public void setCamelContext(final CamelContext camelContext) {
        this.camelContext = camelContext;
    }
}
