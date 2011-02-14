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
package de.escidoc.core.sm.business.preprocessing;

import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.BeanLocator;
import de.escidoc.core.sm.business.persistence.hibernate.AggregationDefinition;

import java.util.Date;
import java.util.List;

/**
 * Triggers Preprocessing of Raw Statistic Data according one Aggregation-Definition.
 * 
 * @author MIH
 * @sm
 */
public class AggregationPreprocessorRunnable implements Runnable {

    private static AppLogger log =
        new AppLogger(AggregationPreprocessorRunnable.class.getName());

    private AggregationPreprocessor aggregationPreprocessor = null;
    
    private AggregationDefinition aggregationDefinition = null;
    
    private Date date = null;

    private AggregationDataSelector aggregationDataSelector;

    /**
     * Default Constructor. Initialize spring Beans.
     * 
     * @throws WebserverSystemException e
     * 
     * @sm
     */
    public AggregationPreprocessorRunnable(
            AggregationDefinition aggregationDefinition, Date date) 
                    throws WebserverSystemException {
        aggregationPreprocessor =
            (AggregationPreprocessor) BeanLocator
                .getBean("Sm.spring.ejb.context",
                    "business.AggregationPreprocessor");
        aggregationDataSelector = (AggregationDataSelector) BeanLocator
                .getBean("Sm.spring.ejb.context", 
                        "business.AggregationDataSelector");
        this.aggregationDefinition = aggregationDefinition;
        this.date = date;
    }

    /**
     * run Thread.
     * 
     * @sm
     */
    public void run() {
        try {
            List resultList =
                aggregationDataSelector.getDataForAggregation(
                        aggregationDefinition,
                        date);
            aggregationPreprocessor.processAggregation(
                        aggregationDefinition, resultList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
