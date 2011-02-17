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

import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.sm.business.interfaces.PreprocessingHandlerInterface;
import de.escidoc.core.sm.business.preprocessing.StatisticPreprocessor;
import de.escidoc.core.sm.business.stax.handler.PreprocessingInformationStaxHandler;

/**
 * An Preprocessing resource handler.
 * 
 * @spring.bean id="business.PreprocessingHandler" scope="prototype"
 * @author MIH
 * @sm
 */
public class PreprocessingHandler implements PreprocessingHandlerInterface {

    private static final AppLogger log =
        new AppLogger(PreprocessingHandler.class.getName());

    private StatisticPreprocessor preprocessor;

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.interfaces.PreprocessingHandlerInterface
     *      #create(java.lang.String)
     * 
     * @param aggregationDefinitionId
     *         id of the aggregation-definition to preprocess.
     * @param xmlData
     *            preprocessing-information as xml in statistic-data schema.
     * 
     * @throws MissingMethodParameterException
     *             ex
     * @throws SystemException
     *             ex
     * 
     * @sm
     */
    public void preprocess(
            final String aggregationDefinitionId, 
            final String xmlData)
        throws MissingMethodParameterException, SystemException {

        //parse
        StaxParser sp = new StaxParser();
        PreprocessingInformationStaxHandler handler = 
                new PreprocessingInformationStaxHandler(sp);
        sp.addHandler(handler);
        try {
            sp.parse(xmlData);
        } catch (Exception e) {
            log.error(e);
            throw new SystemException(e);
        }
        
        //call statistic-preprocessor
        preprocessor.execute(
                handler.getStartDate(), 
                handler.getEndDate(), 
                aggregationDefinitionId);
    }

    /**
     * Injects the {@link StatisticPreprocessor} to use.
     * 
     * @param preprocessor
     *            The {@link StatisticPreprocessor}.
     * @spring.property ref="business.StatisticPreprocessor"
     * @sm
     */
    public void setPreprocessor(final StatisticPreprocessor preprocessor) {
        this.preprocessor = preprocessor;
    }

}
