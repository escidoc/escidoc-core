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

import de.escidoc.core.common.business.indexing.GsearchHandler;
import de.escidoc.core.common.business.queue.errorprocessing.ErrorMessageHandler;
import de.escidoc.core.common.util.logger.AppLogger;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.util.HashMap;

/**
 * IndexOptimizerService. sends index.optimize-message to gsearch.
 * gsearch then optimizes all lucene-indexes
 * 
 * @author MIH
 * 
 * @spring.bean id="mbean.IndexOptimizerService"
 */
@ManagedResource(objectName = "eSciDocCore:name=IndexOptimizerService", description = "sends index-optimize request to gsearch", log = true, logFile = "jmx.log", currencyTimeLimit = 15)
public class IndexOptimizerService {
    private static AppLogger log =
        new AppLogger(IndexOptimizerService.class.getName());
    
    private GsearchHandler gsearchHandler;

    private ErrorMessageHandler errorMessageHandler;
    
    /**
     * call optimize.
     * 
     */
    @ManagedOperation(description = "call optimize.")
    public void execute() {
        long lastExecutionTime = 
            IndexOptimizerServiceTimer.getInstance().getLastExecutionTime();
        if (lastExecutionTime > 0 
            && (System.currentTimeMillis() - lastExecutionTime) < 1000) {
            return;
        }
        try {
            log.info("optimizing search-indices");
            gsearchHandler.requestOptimize(null);
        } catch (Exception e) {
            final String message = 
                "optimizing search-indices failed";
            errorMessageHandler.putErrorMessage(
                    new HashMap<String, String>() { {
                        put("message", message); } }, e,
                        de.escidoc.core.common.business.Constants.
                        INDEXING_ERROR_LOGFILE);
            log.error(e);
        }
    }

    /**
     * Injects the {@link GsearchHandler} to use.
     * 
     * @param gsearchHandler
     *            The {@link GsearchHandler}.
     * @spring.property ref="common.business.indexing.GsearchHandler"
     */
    public void setGsearchHandler(final GsearchHandler gsearchHandler) {
        this.gsearchHandler = gsearchHandler;
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
