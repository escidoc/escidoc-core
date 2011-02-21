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
package de.escidoc.core.common.util.service;

import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;

import javax.naming.Context;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class to support ejb factory beans.
 * 
 * @author TTE
 * @common
 */
public final class EjbFactoryBeanHelper {

    private static final String CONFIG_PROVIDER_URL_NAME = ".provider.url";

    /**
     * Private constructor to prevent instantiation.
     */
    private EjbFactoryBeanHelper() {
    }

    /**
     * Gets the JNDI properties.
     * 
     * @param packageName
     *            The package name.
     * @return The JNDI properties.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    public static Properties getInitialContextJndiProperties(
        final String packageName) throws WebserverSystemException {

        String providerUrl;
        try {
            providerUrl =
                EscidocConfiguration.getInstance().get(
                    packageName + CONFIG_PROVIDER_URL_NAME);
            if (providerUrl == null) {
                providerUrl =
                    EscidocConfiguration.getInstance().get(
                        EscidocConfiguration.ESCIDOC_CORE_DEFAULT_JNDI_URL);
            }
        }
        catch (IOException e) {
            throw new WebserverSystemException(e);
        }
        Properties properties = new Properties();
        properties.setProperty(Context.URL_PKG_PREFIXES,
            "org.jboss.naming:org.jnp.interfaces");
        properties.setProperty(Context.PROVIDER_URL, providerUrl);

        return properties;
    }
}
