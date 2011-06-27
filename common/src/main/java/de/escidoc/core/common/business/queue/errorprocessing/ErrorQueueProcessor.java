/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.common.business.queue.errorprocessing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Gets traces out of logfile and sends them via email to the queue.error.administrator.email.
 *
 * @author Michael Hoppe
 */
@Service("business.ErrorQueueProcessor")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ErrorQueueProcessor {

    // A setter exists. Consider removing from Spring.
    @Autowired
    @Qualifier("common.ErrorMessageHandler")
    private ErrorMessageHandler errorMessageHandler;

    /**
     * core Method.
     *
     * @param logfile name of the logfile
     */
    public void execute(final String logfile) {
        //read logfile and do something
    }
}
