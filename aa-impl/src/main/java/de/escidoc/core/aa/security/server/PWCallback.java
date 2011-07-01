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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Currently not used.
 * <p/>
 * <p/>
 * This class is invoked on server side if the webservice description defines its invocation. Can be used to make a
 * pre-check on the provided username (but the real check is done in the SecurityInterceptor).
 *
 * @author Bernhard Kraus (Accenture)
 */
public class PWCallback implements CallbackHandler {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PWCallback.class);

    /**
     * The handle class of the callback handler.
     *
     * @param callbacks the WSPasswordCallback implementation
     * @throws IOException                  Exception
     * @throws UnsupportedCallbackException Exception
     */
    @Override
    public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (final Callback callback : callbacks) {

            try {
                final Method method = callback.getClass().getMethod("getIdentifer", (Class[]) null);

                final String name = (String) method.invoke(callback);

                LOGGER.debug("The CallbackHandler server-side: " + name);

            }
            catch (final Exception ex) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Error on invoking callback handeler.");
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Error on invoking callback handeler.", ex);
                }
            }
            LOGGER.debug("The authentication for: " + callback.toString());
        }
    }
}
