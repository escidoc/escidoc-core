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
package de.escidoc.core.sm.mbean;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.queue.errorprocessing.ErrorMessageHandler;
import de.escidoc.core.sm.business.preprocessing.StatisticPreprocessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * StatisticPreprocessor. Preprocesses the raw statistic data into aggregation-tables. Gets externally triggered, e.g.
 * by a quartz job.
 *
 * @author Michael Hoppe, Torsten Tetteroo
 */
@Service("mbean.StatisticPreprocessorService")
@ManagedResource(objectName = "eSciDocCore:name=StatisticPreprocessorService", description = "Preprocesses the raw statistic data into aggregation-tables.", log = true, logFile = "jmx.log", currencyTimeLimit = 15)
public class SpringStatisticPreprocessorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringStatisticPreprocessorService.class);

    private static final int HOURS_PER_DAY = 24;

    private static final int MINUTES_PER_HOUR = 60;

    private static final int SECONDS_PER_MINUTE = 60;

    private static final int MILLISECONDS_PER_SECOND = 1000;

    private static final int MILLISECONDS_PER_DAY =
        HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;

    @Autowired
    @Qualifier("business.StatisticPreprocessor")
    private StatisticPreprocessor preprocessor;

    @Autowired
    @Qualifier("common.ErrorMessageHandler")
    private ErrorMessageHandler errorMessageHandler;

    /**
     * Preprocess statistic data by calling preprocessor.
     *
     * @throws Exception e
     */
    @ManagedOperation(description = "Preprocess statistic data.")
    @Scheduled(cron = "0 0 1 * * ?")
    public void execute() throws Exception {
        final long lastExecutionTime = StatisticPreprocessorServiceTimer.getInstance().getLastExecutionTime();
        if (lastExecutionTime > 0L && System.currentTimeMillis() - lastExecutionTime < 1000L) {
            return;
        }
        try {
            LOGGER.info("preprocessing statistic-data");
            // call with date of yesterday
            final long time = System.currentTimeMillis() - (long) MILLISECONDS_PER_DAY;
            final Date date = new Date(time);
            preprocessor.execute(date);
        }
        catch (final Exception e) {
            final Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("message", "preprocessing of statistic-data failed");
            errorMessageHandler.putErrorMessage(parameters, e, Constants.STATISTIC_PREPROCESSING_ERROR_LOGFILE);
            throw e;
        }
    }

    /**
     * Preprocess statistic data by calling preprocessor with specific date.
     *
     * @param millies day to preprocess
     * @throws Exception e
     */
    @ManagedOperation(description = "Preprocess statistic data with date.")
    @ManagedOperationParameter(name = "millies", description = "Day to preprocess in millies.")
    public void execute(final long millies) throws Exception {
        try {
            final Date date = new Date(millies);
            preprocessor.execute(date);
        }
        catch (final Exception e) {
            final Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("message", "preprocessing of statistic-data failed");
            errorMessageHandler.putErrorMessage(parameters, e, Constants.STATISTIC_PREPROCESSING_ERROR_LOGFILE);
            throw e;
        }
    }

}
