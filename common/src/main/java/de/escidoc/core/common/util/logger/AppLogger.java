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
package de.escidoc.core.common.util.logger;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * Class is used to encapsulate the log4j framework.
 * 
 * @author BKR
 * 
 */
public final class AppLogger {

    private final Logger log;

    /**
     * Use the default constructor.
     * 
     * @param className
     *            The name of the class for that messages shall be logged.
     */
    public AppLogger(final String className) {

        log = Logger.getLogger(className);
    }

    /**
     * Error messages.
     * 
     * @param msg
     *            The message to print.
     */
    public void debug(final Object msg) {
        log.debug(msg);
    }

    /**
     * Error messages.
     * 
     * @param msg
     *            The message to print.
     */
    public void error(final Object msg) {
        log.error(msg);
    }

    /**
     * Error messages.
     * 
     * @param msg
     *            The message to print.
     * @param throwable
     *            The throwable.
     */
    public void error(final Object msg, final Throwable throwable) {
        log.error(msg, throwable);
    }

    /**
     * Info messages.
     * 
     * @param msg
     *            The message to print.
     */
    public void info(final Object msg) {
        log.info(msg);
    }

    /**
     * Info messages.
     * 
     * @param msg
     *            The message to print.
     * @param throwable
     *            The throwable.
     */
    public void info(final Object msg, final Throwable throwable) {
        log.info(msg, throwable);
    }

    /**
     * Log a message object with the TRACE level.
     *
     * @param message the message object to log.
     */
    public void trace(final Object message) {
        log.trace(message);
    }

    /**
     * Warn messages.
     * 
     * @param msg
     *            The message to print.
     */
    public void warn(final Object msg) {
        log.warn(msg);
    }

    /**
     * InfWarno messages.
     * 
     * @param msg
     *            The message to print.
     * @param throwable
     *            The throwable.
     */
    public void warn(final Object msg, final Throwable throwable) {
        log.warn(msg, throwable);
    }

    /**
     * Checks if the debug logging level is enabled.
     * 
     * @return Returns <code>true</code> if the debug logging level is
     *         enabled.
     */
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    /**
     * Checks if the info logging level is enabled.
     * 
     * @return Returns <code>true</code> if the info logging level is enabled.
     */
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    /**
     * Checks if the warn logging level is enabled.
     * 
     * @return Returns <code>true</code> if the warn logging level is enabled.
     */
    public boolean isWarnEnabled() {
        return log.isEnabledFor(Priority.WARN);
    }

}
