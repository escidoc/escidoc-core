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
package de.escidoc.core.common.business.queue.errorprocessing;

import de.escidoc.core.common.util.logger.AppLogger;

import java.util.HashMap;
import java.util.Map;

/*******************************************************************************
 * @author MIH
 * 
 * @spring.bean id = "common.ErrorMessageHandler" lazy-init="true"
 * @common
 */
public class ErrorMessageHandler {

    private static AppLogger errorLogger = null;

    private static final String DELIMITER = 
            "######################################################"
            + "#################################################\n";

    /**
     * Writes error-message and timestamp into logfile.
     * 
     * @param parameters
     *            Message message-object
     * @param exception
     *            exception to evaluate
     * @param logfile
     *            name of the logfile error-message has to get written to
     * @common
     */
    public void putErrorMessage(final Map<String, String> parameters,
            final Throwable exception, final String logfile) {

        StringBuffer messageBuf = new StringBuffer(DELIMITER);
        
        // put all given parameters into StringBuffer
        for (String key : parameters.keySet()) {
            messageBuf.append(key).append(": ").append(parameters.get(key))
                    .append("\n");
        }

        //put error-message into StringBuffer
        messageBuf.append("error: ").append(getStackTrace(exception)).append(
                "\n");
        
        //write StringBuffer into logfile
        errorLogger = new AppLogger(logfile);
        errorLogger.error(messageBuf);

        // ////////////////////////////////////////////////////////////////
    }

    /**
     * Get Stack Trace of Exception.
     * 
     * @param e
     *            Exception
     * @return String Stack Trace
     * @common
     */
    private String getStackTrace(final Throwable e) {
        StringBuffer stack = new StringBuffer("");
        if (e != null) {
            stack.append(e.getMessage()).append("\n");
            StackTraceElement[] stackElements = e.getStackTrace();
            if (stackElements != null && stackElements.length > 0
                    && stackElements[0] != null) {
                stack.append(stackElements[0].toString()).append("\n");
            }
            if (e.getCause() != null) {
                stack.append("Caused by:\n");
                stack.append(getStackTrace(e.getCause()));
            }
        }
        return stack.toString();
    }

}
