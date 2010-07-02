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

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import de.escidoc.core.common.business.indexing.Constants;
import de.escidoc.core.common.business.queue.errorprocessing.ErrorQueueProcessor;
import de.escidoc.core.common.util.logger.AppLogger;

/**
 * IndexingErrorProcessor. Reads the messages that were written into the
 * IndexingError Logfile and sends them via email to the sb.administrator.email
 * 
 * @author MIH, TTE
 * 
 * @spring.bean id="mbean.IndexingErrorProcessorService"
 * @sb
 */
@ManagedResource(objectName = "eSciDocCore:name=IndexingErrorProcessorService", description = "Reads the messages that were written into the IndexingError Logfile and sends them via email to the sb.administrator.email.", log = true, logFile = "jmx.log", currencyTimeLimit = 15)
public class SpringIndexingErrorProcessorService {
    private static AppLogger log =
        new AppLogger(SpringIndexingErrorProcessorService.class.getName());

    private ErrorQueueProcessor processor;

    /**
     * Process indexing error queue by calling processor.
     * 
     * @sb
     */
    @ManagedOperation(description = "Process indexing error queue.")
    public void execute() {

        processor.execute(Constants.INDEXING_ERROR_LOGFILE);
    }

    /**
     * Injects the {@link ErrorQueueProcessor} to use.
     * 
     * @param processor
     *            The {@link ErrorQueueProcessor}.
     * @spring.property ref="business.ErrorQueueProcessor"
     * @sb
     */
    public void setProcessor(final ErrorQueueProcessor processor) {
        this.processor = processor;
    }

}
