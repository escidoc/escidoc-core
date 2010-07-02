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
package de.escidoc.core.aa.security.server;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import de.escidoc.core.common.util.logger.AppLogger;

/**
 * Currently not used.
 * <p/>
 * 
 * This class is invoked on server side if the webservice description defines
 * its invocation. Can be used to make a pre-check on the provided username (but
 * the real check is done in the SecurityInterceptor).
 * 
 * @author Bernhard Kraus (Accenture)
 */
public class PWCallback implements CallbackHandler {

    /** The logger. */
    private static AppLogger log = new AppLogger(PWCallback.class.getName());

    /**
     * The handle class of the callback handler.
     * 
     * @param callbacks
     *            the WSPasswordCallback implementation
     * @throws IOException
     *             Exception
     * @throws UnsupportedCallbackException
     *             Exception
     */
    public void handle(final Callback[] callbacks) throws IOException,
        UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {

            try {
                Method method =
                    callbacks[i].getClass().getMethod("getIdentifer",
                        (Class[]) null);

                String name =
                    (String) method.invoke(callbacks[i], new Object[] {});

                log.debug("The CallbackHandler server-side: " + name);

            }
            catch (Exception ex) {
                log.error("Error:", ex);
            }
            log.debug("The authentication for: " + callbacks[i].toString());
        }
    }
}
