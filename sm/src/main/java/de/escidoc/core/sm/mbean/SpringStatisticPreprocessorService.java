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

import de.escidoc.core.common.business.queue.errorprocessing.ErrorMessageHandler;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.sm.business.preprocessing.StatisticPreprocessor;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.util.Date;
import java.util.HashMap;

/**
 * StatisticPreprocessor. Preprocesses the raw statistic data into
 * aggregation-tables. Gets externally triggered, e.g. by a quartz job.
 * 
 * @author MIH, TTE
 * @spring.bean id="mbean.StatisticPreprocessorService"
 * 
 */
@ManagedResource(objectName = "eSciDocCore:name=StatisticPreprocessorService", description = "Preprocesses the raw statistic data into aggregation-tables.", log = true, logFile = "jmx.log", currencyTimeLimit = 15)
public class SpringStatisticPreprocessorService {

    private static final AppLogger log =
        new AppLogger(SpringStatisticPreprocessorService.class.getName());
    private StatisticPreprocessor preprocessor;

    private static final int HOURS_PER_DAY = 24;

    private static final int MINUTES_PER_HOUR = 60;

    private static final int SECONDS_PER_MINUTE = 60;

    private static final int MILLISECONDS_PER_SECOND = 1000;

    private static final int MILLISECONDS_PER_DAY =
        HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE
            * MILLISECONDS_PER_SECOND;

    private ErrorMessageHandler errorMessageHandler;

    /**
     * Preprocess statistic data by calling preprocessor.
     * 
     * @throws Exception e
     */
    @ManagedOperation(description = "Preprocess statistic data.")
    public void execute() throws Exception {
        long lastExecutionTime = 
            StatisticPreprocessorServiceTimer.getInstance().getLastExecutionTime();
        if (lastExecutionTime > 0 
            && (System.currentTimeMillis() - lastExecutionTime) < 1000) {
            return;
        }
        try {
            log.info("preprocessing statistic-data");
            // call with date of yesterday
            long time = System.currentTimeMillis() - MILLISECONDS_PER_DAY;
            Date date = new Date(time);
            preprocessor.execute(date);
        } catch (Exception e) {
            errorMessageHandler.putErrorMessage(
                    new HashMap<String, String>() { {
                        final String message = "preprocessing of statistic-data failed";
                        put("message", message); }

                        private static final long serialVersionUID = 5304590831581021890L;
                    }, e,
                        de.escidoc.core.common.business.Constants.
                        STATISTIC_PREPROCESSING_ERROR_LOGFILE);
            throw e;
        }
    }

    /**
     * Preprocess statistic data by calling preprocessor with specific date.
     * 
     * @param millies
     *            day to preprocess
     * @throws Exception e
     * 
     */
    @ManagedOperation(description = "Preprocess statistic data with date.")
    @ManagedOperationParameter(name = "millies", description = "Day to preprocess in millies.")
    public void execute(final long millies) throws Exception {
        try {
            Date date = new Date(millies);
            preprocessor.execute(date);
        } catch (Exception e) {
            errorMessageHandler.putErrorMessage(
                    new HashMap<String, String>() { {
                        final String message = "preprocessing of statistic-data failed";
                        put("message", message); }

                        private static final long serialVersionUID = -2340916328981929936L;
                    }, e,
                        de.escidoc.core.common.business.Constants.
                        STATISTIC_PREPROCESSING_ERROR_LOGFILE);
            throw e;
        }
    }

    /**
     * Injects the {@link StatisticPreprocessor} to use.
     * 
     * @param preprocessor
     *            The {@link StatisticPreprocessor}.
     * @spring.property ref="business.StatisticPreprocessor"
     */
    public void setPreprocessor(final StatisticPreprocessor preprocessor) {
        this.preprocessor = preprocessor;
    }

    /**
     * Setting the errorMessageHandler.
     * 
     * @param errorMessageHandler
     *            The ErrorMessageHandler to set.
     * @spring.property ref="common.ErrorMessageHandler"
     */
    public final void setErrorMessageHandler(
        final ErrorMessageHandler errorMessageHandler) {
        this.errorMessageHandler = errorMessageHandler;
    }

}
