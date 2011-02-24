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
package de.escidoc.core.common.axis;

import org.apache.axis.AxisProperties;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.EngineConfigurationFactory;
import org.apache.axis.transport.http.AdminServlet;
import org.apache.axis.transport.http.AxisServlet;

import javax.servlet.http.HttpServlet;

/**
 * @author MIH
 * 
 * Overwrites default Axis Class because this class can extract placeholders out
 * of the server-config.wsdd and replace them with values specified in the
 * escidoc.properties. This is done because we have to specify the urls of the
 * jndi of the different eSciDoc-Components. These are specified in the
 * escidoc-core.properties because the server-config.wsdd 
 * is inside the .ear-file and cannot be easily changed.
 * 
 * @common
 */
public class EscidocEngineConfigurationFactory
    implements EngineConfigurationFactory {

    public static final String OPTION_CLIENT_CONFIG_FILE =
        "axis.ClientConfigFile";

    public static final String OPTION_SERVER_CONFIG_FILE =
        "axis.ServerConfigFile";

    protected static final String CLIENT_CONFIG_FILE = "client-config.wsdd";

    protected static final String SERVER_CONFIG_FILE = "server-config.wsdd";

    private final String clientConfigFile;

    private final String serverConfigFile;

    private static HttpServlet param = null;

    /**
     * Creates and returns a new EngineConfigurationFactory.
     * 
     * @param in
     *            The parameter of the engine configuration factory. This
     *            Factory needs an instance of {@link AdminServlet} or
     *            {@link AxisServlet}.
     * @return Returns the {@link EscidocEngineConfigurationFactory} or
     *         <code>null</code> if the factory cannot be created.
     * @see org.apache.axis.configuration.EngineConfigurationFactoryFinder
     */
    public static EngineConfigurationFactory newFactory(final Object in) {

        if (in != null && !in.getClass().equals(AdminServlet.class)
            && !in.getClass().equals(AxisServlet.class)) {
            return null; // not for us.
        }

        if (in != null) {
            setParam((HttpServlet) in);
        }
        return new EscidocEngineConfigurationFactory();
    }

    /**
     * Create the default engine configuration and detect whether the user has
     * overridden this with their own.
     */
    protected EscidocEngineConfigurationFactory() {

        String path = "";
        if (param != null) {
            path = param.getServletContext().getRealPath("/WEB-INF") + '/';
        }
        clientConfigFile =
            path
                + AxisProperties.getProperty(OPTION_CLIENT_CONFIG_FILE,
                    CLIENT_CONFIG_FILE);

        serverConfigFile =
            path
                + AxisProperties.getProperty(OPTION_SERVER_CONFIG_FILE,
                    SERVER_CONFIG_FILE);
    }

    /**
     * Get a default client engine configuration.
     * 
     * @return a client EngineConfiguration
     */
    @Override
    public final EngineConfiguration getClientEngineConfig() {

        return new FileProvider(clientConfigFile);
    }

    /**
     * Get a default server engine configuration.
     * 
     * @return a server EngineConfiguration
     */
    @Override
    public final EngineConfiguration getServerEngineConfig() {

        return new FileProvider(serverConfigFile);
    }

    /**
     * @return the param
     */
    public static HttpServlet getParam() {

        return param;
    }

    /**
     * @param param
     *            the param to set
     */
    public static void setParam(final HttpServlet param) {

        EscidocEngineConfigurationFactory.param = param;
    }

}
