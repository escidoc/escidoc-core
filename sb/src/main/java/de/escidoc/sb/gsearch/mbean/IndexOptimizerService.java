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
package de.escidoc.sb.gsearch.mbean;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.indexing.GsearchHandler;
import de.escidoc.core.common.business.queue.errorprocessing.ErrorMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * IndexOptimizerService. sends index.optimize-message to gsearch. gsearch then optimizes all lucene-indexes
 *
 * @author Michael Hoppe
 */
@Service("mbean.IndexOptimizerService")
@ManagedResource(objectName = "eSciDocCore:name=IndexOptimizerService", description = "sends index-optimize request to gsearch", log = true, logFile = "jmx.log", currencyTimeLimit = 15)
public class IndexOptimizerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexOptimizerService.class);

    @Autowired
    @Qualifier("common.business.indexing.GsearchHandler")
    private GsearchHandler gsearchHandler;

    @Autowired
    @Qualifier("common.ErrorMessageHandler")
    private ErrorMessageHandler errorMessageHandler;

    /**
     * call optimize.
     */
    @ManagedOperation(description = "call optimize.")
    @Scheduled(cron = "0 0 2 * * ?")
    public void execute() {
        final long lastExecutionTime = IndexOptimizerServiceTimer.getInstance().getLastExecutionTime();
        if (lastExecutionTime > 0L && System.currentTimeMillis() - lastExecutionTime < 1000L) {
            return;
        }
        try {
            LOGGER.info("optimizing search-indices");
            gsearchHandler.requestOptimize(null);
        }
        catch (final Exception e) {
            final Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("message", "optimizing search-indices failed");
            errorMessageHandler.putErrorMessage(parameters, e, Constants.INDEXING_ERROR_LOGFILE);
            LOGGER.error("Optimizing search-indices failed.", e);
        }
    }
}
