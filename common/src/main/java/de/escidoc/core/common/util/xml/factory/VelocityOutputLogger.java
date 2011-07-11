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

package de.escidoc.core.common.util.xml.factory;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Velocity Logger with no output.
 *
 * @author Steffen Wagner
 */
public class VelocityOutputLogger implements LogChute {

    private static final Logger LOGGER = LoggerFactory.getLogger(VelocityOutputLogger.class);

    /**
     * Init Velocity logger.
     *
     * @param arg0 RuntimeServices
     * @throws Exception Shouldn't happen.
     */
    @Override
    public void init(final RuntimeServices arg0) throws Exception {
    }

    /**
     * Check if log level is enabled.
     *
     * @param arg0 log level which is to check
     * @return true if log level is enabled, false otherwise
     */
    @Override
    public boolean isLevelEnabled(final int arg0) {
        return true;
    }

    /**
     * LOGGER.
     *
     * @param arg0 log level
     * @param arg1 log message
     * @param arg2 Exception
     */
    @Override
    public void log(final int arg0, final String arg1, final Throwable arg2) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(arg1);
        }
    }

    /**
     * LOGGER.
     *
     * @param arg0 log level
     * @param arg1 log message
     */
    @Override
    public void log(final int arg0, final String arg1) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(arg1);
        }
    }
}