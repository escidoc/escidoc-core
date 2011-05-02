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
import de.escidoc.core.common.business.queue.errorprocessing.ErrorQueueProcessor;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * StatisticErrorProcessor. Reads the messages that were written into the StatisticError Logfile and sends them via
 * email to the sm.administrator.email
 *
 * @author Michael Hoppe, Torsten Tetteroo
 */
@ManagedResource(objectName = "eSciDocCore:name=StatisticErrorProcessorService", description = "Reads the messages that were written into the StatisticError Logfile and sends them via email to the sm.administrator.email.", log = true, logFile = "jmx.log", currencyTimeLimit = 15)
public class SpringStatisticErrorProcessorService {

    private ErrorQueueProcessor processor;

    /**
     * Process statistic error queue by calling processor.
     */
    @ManagedOperation(description = "Process statistic error logfile.")
    public void execute() {
        processor.execute(Constants.STATISTIC_ERROR_LOGFILE);
    }

    /**
     * Injects the {@link ErrorQueueProcessor} to use.
     *
     * @param processor The {@link ErrorQueueProcessor}.
     */
    public void setProcessor(final ErrorQueueProcessor processor) {
        this.processor = processor;
    }

}
